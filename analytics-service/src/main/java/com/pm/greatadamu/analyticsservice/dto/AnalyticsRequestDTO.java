package com.pm.greatadamu.analyticsservice.dto;

import com.pm.greatadamu.analyticsservice.model.Month;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnalyticsRequestDTO {
    @NotNull
    @Positive(message = "customerID must be positive")
    private Long customerId;
    @NotNull
    private Month month;
    @NotNull
    private int year;

}
