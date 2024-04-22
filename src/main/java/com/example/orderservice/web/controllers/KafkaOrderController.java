package com.example.orderservice.web.controllers;

import com.example.orderservice.models.Order;
import com.example.orderservice.services.KafkaService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/order/kafka")
@RequiredArgsConstructor
public class KafkaOrderController {

    @Value("${app.kafka.kafkaOrderTopic}")
    private String topicName;

    private final KafkaService kafkaService;

    @PostMapping("/send")
    public ResponseEntity<String> sendMessage(@RequestBody Order order) {
        kafkaService.sendOrderEventToKafka(topicName, order);

        return ResponseEntity.ok("Message was sent to kafka");
    }
}
