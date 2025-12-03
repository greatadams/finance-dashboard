package com.pm.greatadamu.transactionservice.controller;

import com.pm.greatadamu.transactionservice.dto.TransactionRequestDTO;
import com.pm.greatadamu.transactionservice.dto.TransactionResponseDTO;
import com.pm.greatadamu.transactionservice.mapper.TransactionMapper;
import com.pm.greatadamu.transactionservice.service.TransactionServices;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/transactions")

public class TransactionController {
    private final TransactionServices transactionServices;


    @GetMapping
    public ResponseEntity<List<TransactionResponseDTO>>getTransactions() {
        //call your service
        List<TransactionResponseDTO>transactions = transactionServices.getTransactions();
        //List<TransactionResponseDTO> into JSON return 200 ok to client
        return ResponseEntity.ok(transactions);

    }


    //get a single transaction by transaction id
    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponseDTO> getTransactionById(@PathVariable Long id) {
        //call your service
        TransactionResponseDTO response = transactionServices.getTransactionByTransactionId(id);

        return ResponseEntity.ok(response);

    }

    //get all transaction for a given customer
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<TransactionResponseDTO>> getTransactionsByCustomerId(@PathVariable Long customerId) {
        //call your service
        List<TransactionResponseDTO> response = transactionServices.getTransactionsByCustomerId(customerId);

        return ResponseEntity.ok(response);

    }

    @PostMapping
    public ResponseEntity<TransactionResponseDTO> createTransaction(
            @RequestBody @Valid TransactionRequestDTO transactionRequestDTO) {
        //call your service:this return a single transactionResponseDTO
        TransactionResponseDTO responseDTO =transactionServices.createTransaction(transactionRequestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);

    }

}

