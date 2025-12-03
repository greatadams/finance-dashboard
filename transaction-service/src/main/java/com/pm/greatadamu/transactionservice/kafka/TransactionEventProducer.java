package com.pm.greatadamu.transactionservice.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransactionEventProducer {

    private final KafkaTemplate<String, TransactionEvent> kafkaTemplate;

    private static final String TOPIC = "transactions";

    public void sendTransactionEvent(TransactionEvent transactionEvent) {
        //you can use customerId or TransactionId as key
        String key = transactionEvent.getCustomerId() !=null
                ? transactionEvent.getCustomerId().toString()
                :transactionEvent.getTransactionId().toString();

        kafkaTemplate.send(TOPIC, key,transactionEvent);
    }
}
