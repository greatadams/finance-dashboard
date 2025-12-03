package com.pm.greatadamu.analyticsservice.mapper;

import com.pm.greatadamu.analyticsservice.dto.AnalyticsRequestDTO;
import com.pm.greatadamu.analyticsservice.dto.AnalyticsResponseDTO;
import com.pm.greatadamu.analyticsservice.model.Month;
import com.pm.greatadamu.analyticsservice.model.MonthlyAnalytics;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class MonthlyAnalyticsMapper {
    public MonthlyAnalytics mapToEntity(AnalyticsRequestDTO analyticsRequestDTO) {
        return MonthlyAnalytics.builder()
                .customerId(analyticsRequestDTO.getCustomerId())
                .month(analyticsRequestDTO.getMonth())
                .totalReceived(BigDecimal.ZERO)
                .totalSpent(BigDecimal.ZERO)
                .transactionCount(0L)
                .year(analyticsRequestDTO.getYear())
                .build();
    }

    public AnalyticsResponseDTO mapToResponseDTO(MonthlyAnalytics monthlyAnalytics) {
        return  new AnalyticsResponseDTO(
                monthlyAnalytics.getId(),
                monthlyAnalytics.getCustomerId(),
                monthlyAnalytics.getMonth(),
                monthlyAnalytics.getYear(),
                monthlyAnalytics.getTotalSpent(),
                monthlyAnalytics.getTotalReceived(),
                monthlyAnalytics.getTransactionCount(),
                monthlyAnalytics.getCreatedAt(),
                monthlyAnalytics.getUpdatedAt()
        );
    }

}
