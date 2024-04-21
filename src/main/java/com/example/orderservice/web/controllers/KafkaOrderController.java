package com.example.orderservice.web.controllers;

import com.example.orderservice.models.Order;
import com.example.orderservice.models.OrderEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/order/kafka")
@RequiredArgsConstructor
public class KafkaOrderController {

    @Value("${app.kafka.kafkaOrderTopic}")
    private String topicName;

    private final KafkaTemplate<String, OrderEvent> orderEventKafkaTemplate;

    private final ObjectMapper objectMapper;

    @PostMapping("/send")
    public ResponseEntity<String> sendMessage(@RequestBody Order order) {
        orderEventKafkaTemplate.send(topicName, objectMapper.convertValue(order, OrderEvent.class));

        return ResponseEntity.ok("Message was sent to kafka");
    }
}
