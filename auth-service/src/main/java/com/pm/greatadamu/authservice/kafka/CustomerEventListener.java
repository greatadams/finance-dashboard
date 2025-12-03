package com.pm.greatadamu.authservice.kafka;

import com.pm.greatadamu.authservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomerEventListener {
    private final AuthService authService;

    @KafkaListener(topics = "customers",groupId = "auth-service")
    public void handleCustomerEvent(CustomerEvent customerEvent) {
        log.info("Received Customer Event: {}", customerEvent);

        //hand off to business logic
        authService.updateCustomerIdFromEvent(customerEvent);
    }
}
