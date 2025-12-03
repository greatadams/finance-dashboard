package com.pm.greatadamu.authservice.service;

import com.pm.greatadamu.authservice.dto.CreateAuthResponse;
import com.pm.greatadamu.authservice.dto.CreateUserLoginDTO;
import com.pm.greatadamu.authservice.dto.CreateUserRegistrationRequestDTO;
import com.pm.greatadamu.authservice.exception.InvalidCredentialsException;
import com.pm.greatadamu.authservice.exception.UserAlreadyExistsException;
import com.pm.greatadamu.authservice.exception.UserDeactivatedException;
import com.pm.greatadamu.authservice.jwtUtil.JwtService;
import com.pm.greatadamu.authservice.kafka.CustomerEvent;
import com.pm.greatadamu.authservice.model.Role;
import com.pm.greatadamu.authservice.model.Status;
import com.pm.greatadamu.authservice.model.User;
import com.pm.greatadamu.authservice.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private static final int MAX_FAILED_ATTEMPTS = 5;

    @Transactional
    public void register(CreateUserRegistrationRequestDTO createUserRegistrationRequestDTO) {
        //email must be unique
        if (userRepository.existsByEmail(createUserRegistrationRequestDTO.getEmail())) {
            throw new UserAlreadyExistsException( "Email already exists");
        }
        // hash the raw password from DTO
        User user = User.builder()
                .email(createUserRegistrationRequestDTO.getEmail())
                .passwordHash(passwordEncoder.encode(createUserRegistrationRequestDTO.getPassword()))
                .role(Role.ROLE_CUSTOMER)
                .status(Status.ACTIVE)
                .failedLoginAttempt(0)
                .customerId(null)
                .build();

        // Save user first to get ID
        user = userRepository.save(user);

      // Use userId as customerId (temporary)
        user.setCustomerId(user.getId());
        userRepository.save(user);
    }

@Transactional
    public CreateAuthResponse login(CreateUserLoginDTO createUserLoginDTO) {
        //find email from db
        User user = userRepository.findByEmail(createUserLoginDTO.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        //check if deactivated
        if (user.getStatus()== Status.DEACTIVATED){
            throw new UserDeactivatedException("User is already deactivated");
        }

        //verify password
        boolean passwordMatches = passwordEncoder.matches(
                createUserLoginDTO.getPassword(),
                user.getPasswordHash());

        if (!passwordMatches) {
            int attempts =user.getFailedLoginAttempt()+1;
            user.setFailedLoginAttempt(attempts);

            // lock account after 5 failed attempts
            if (attempts >= MAX_FAILED_ATTEMPTS) {
                user.setStatus(Status.DEACTIVATED);
                userRepository.save(user);
                throw new UserDeactivatedException("User is deactivated");
            }

            userRepository.save(user);
            throw new InvalidCredentialsException(
                   String.format("Invalid credentials. Attempt %d of %d", attempts, MAX_FAILED_ATTEMPTS));
        }

        // success: reset attempts, update lastLogin
        user.setFailedLoginAttempt(0);
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        //call JWT
        String token = jwtService.generateToken(user);

        return CreateAuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .userId(user.getId())
                .customerId(user.getCustomerId())
                .email(user.getEmail())
                .role(user.getRole().name())
                .expiresIn(jwtService.getExpirationMs())
                .build();
    }
    @Transactional
    public void updateCustomerIdFromEvent(CustomerEvent customerEvent) {
        log.info("Processing CustomerEvent for email: {}, customerId: {}",
                customerEvent.getEmail(),customerEvent.getCustomerId());

        //find by email
        Optional<User> userOptional = userRepository.findByEmail(customerEvent.getEmail());

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            //Update customerID from customer service
            user.setCustomerId(customerEvent.getCustomerId());
            userRepository.save(user);

            log.info("Updated customerId: {} for user with email: {}", customerEvent.getCustomerId(), customerEvent.getEmail());


        }else {
            //user doesn't exist yet-they haven't registered
            // This is normal! Customer profile created before auth registration
            log.info("No user found with email: {}. User hasn't registered yet.",
                    customerEvent.getEmail());
        }






    }
}
