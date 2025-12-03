package com.pm.greatadamu.analyticsservice.repository;

import com.pm.greatadamu.analyticsservice.model.Month;
import com.pm.greatadamu.analyticsservice.model.MonthlyAnalytics;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MonthlyAnalyticsRepository extends JpaRepository<MonthlyAnalytics, Long> {
    // Find analytics for specific customer, month, and year
    Optional<MonthlyAnalytics>findByCustomerIdAndMonthAndYear(
            Long customerId,
            Month month,
            int year
    );
    //find all analytics for a customer
    List<MonthlyAnalytics> findByCustomerId(Long customerId);

    //Find analytics for a specific year
    List<MonthlyAnalytics> findByCustomerIdAndYear(Long customerId,int year);

}
