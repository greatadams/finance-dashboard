package com.pm.greatadamu.analyticsservice.kafka;

import com.pm.greatadamu.analyticsservice.service.MonthlyAnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class TransactionEventListener {
    private final MonthlyAnalyticsService monthlyAnalyticsService;

    @KafkaListener(topics = "transactions",groupId = "analytics-service")
    public void handleTransactionEvent(TransactionEvent transactionEvent) {
        log.info("Received transaction event {}", transactionEvent);

        //hand off to business logic
        monthlyAnalyticsService.updateAnalyticsFromTransaction(transactionEvent);
    }
}
