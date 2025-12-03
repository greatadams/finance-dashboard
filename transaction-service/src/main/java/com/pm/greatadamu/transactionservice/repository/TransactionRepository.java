package com.pm.greatadamu.transactionservice.repository;

import com.pm.greatadamu.transactionservice.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByCustomerId(Long customerId);
}
