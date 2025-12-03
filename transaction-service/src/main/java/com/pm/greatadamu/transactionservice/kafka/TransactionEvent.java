package com.pm.greatadamu.transactionservice.kafka;

import com.pm.greatadamu.transactionservice.model.TransactionStatus;
import com.pm.greatadamu.transactionservice.model.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionEvent {
    private Long transactionId;
    private Long customerId;
    private BigDecimal amount;
    private TransactionType transactionType;
    private TransactionStatus transactionStatus;
    private LocalDateTime transactionDate;

}
