package com.pm.greatadamu.analyticsservice.dto;

import com.pm.greatadamu.analyticsservice.model.Month;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsResponseDTO {
    private Long id;
    private Long customerId;
    private Month month;
    private int year;
    private BigDecimal totalSpent;
    private BigDecimal totalReceived;
    private Long transactionCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
