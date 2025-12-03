package com.pm.greatadamu.customerservice.kafka;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class CustomerEvent {
    private Long customerId;
    private String email;
}
