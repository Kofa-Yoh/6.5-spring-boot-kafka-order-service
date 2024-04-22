package com.example.orderservice.services;

import com.example.orderservice.models.Order;
import com.example.orderservice.models.OrderEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final ObjectMapper objectMapper;

    public void sendOrderEventToKafka(String topic, Order order) {
        kafkaTemplate.send(topic, objectMapper.convertValue(order, OrderEvent.class));
    }
}
