package com.pm.greatadamu.customerservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomerEventProducer {
    final KafkaTemplate<String, CustomerEvent> kafkaTemplate;

    private static final String TOPIC = "customers";

    public void sendCustomerEvent(CustomerEvent event) {
        //you can use customerId
        String key = event.getCustomerId() !=null
                ? event.getCustomerId().toString()
                : event.getEmail();
        log.info("Publishing CustomerEvent to Kafka: {}", event);
        kafkaTemplate.send(TOPIC,key ,event);
        log.info("CustomerEvent published successfully");
    }

}
