package com.pm.greatadamu.authservice.filter;

import com.pm.greatadamu.authservice.jwtUtil.JwtService;
import com.pm.greatadamu.authservice.model.Status;
import com.pm.greatadamu.authservice.model.User;
import com.pm.greatadamu.authservice.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    private final UserRepository userRepository; //so can enforce DB STATUS ON EVERY request

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    )throws ServletException, IOException{
        //GET AUTHORIZATION HEADER
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        //NO AUTHORIZATION HEADER OR NOT BEARER ->CONTINUE
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        //EXTRACT TOKEN STRING
        String token = authHeader.substring(7);

        //IF ALREADY AUTHENTICATED,SKIP
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            if (!jwtService.isTokenValid(token)) {
                filterChain.doFilter(request, response);
                return;
            }

            //EXTRACT CLAIMS USED FOR AUTHORIZATION
            Long userId= jwtService.extractUserId(token);//userId becomes the “principal”
            //// TODO: Use DB role instead of JWT claim to enforce real-time role changes
            String role = jwtService.extractRole(token); //eg "ROLE_CUSTOMER//role becomes the authority used by Spring Security

            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                //user no longer exists => treat token as invalid
                filterChain.doFilter(request, response);
                return;
            }

            if (user.getStatus() == Status.DELETED ||
            user.getStatus() == Status.DISABLED ||
            user.getStatus() == Status.PENDING_VERIFICATION){
                //DON'T AUTHENTICATE: REQUEST WILL BE REJECTED BY SECURITYCONFIG ON PROTECTED ENDPOINT
                filterChain.doFilter(request, response);
                return;
            }

            //HANDLE LOCKED(TIME-BASED LOCKED)
            if (user.getStatus() ==Status.LOCKED){
                //if still locked blocked immediately
                if (user.getLockedUntil() !=null && user.getLockedUntil().isAfter(Instant.now())){
                    filterChain.doFilter(request, response);
                    return;
                }

                //if locked expired=>AUTO UNLOCK after lock time passes
                user.setStatus(Status.ACTIVE);
                user.setLockedUntil(null);
                user.setFailedLoginAttempt(0);
                userRepository.save(user);

            }

            //ACTIVE USER ONLY
            if (user.getStatus() != Status.ACTIVE) {
                filterChain.doFilter(request, response);
                return;
            }

            //HANDLE TOKENS WHEN A PASSWORD IS CHANGED
            Instant tokenIssuedAt= jwtService.extractIssuedAt(token);
            Instant passwordChangedAt = user.getPasswordChangedAt();

            if(passwordChangedAt != null && tokenIssuedAt != null && tokenIssuedAt.isBefore(passwordChangedAt)){
                //TOKEN IS OLDER THAN LAST PASSWORD CHANGE CHANGE ->REJECT
                filterChain.doFilter(request, response);
                return;
            }

            //Build authentication object and store it
            //This is the core output of the filter — it tells Spring:
            //This request is authenticated as userId with ROLE_X.”
            var authorities = List.of(new SimpleGrantedAuthority(role));

            //principal can be userId(simple & common)
            var auth = new UsernamePasswordAuthenticationToken(userId, null, authorities);
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(auth);
        }catch (Exception ignored){
            // If token is malformed/expired, do not authenticate

        }
        filterChain.doFilter(request, response);
    }

}
