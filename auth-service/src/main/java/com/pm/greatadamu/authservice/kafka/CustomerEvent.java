package com.pm.greatadamu.authservice.kafka;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CustomerEvent {
    private Long customerId;
    private String email;
}
