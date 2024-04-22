package com.example.orderservice.services;

import com.example.orderservice.models.Order;
import com.example.orderservice.models.OrderEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

@SpringBootTest
@Testcontainers
public class KafkaServiceTest {

    @Container
    static final KafkaContainer kafka = new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:6.2.0")
    );

    @DynamicPropertySource
    static void registryKafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }

    @Autowired
    private ConsumerFactory<String, Object> consumerFactory;

    @Autowired
    private KafkaService kafkaService;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${app.kafka.kafkaOrderTopic}")
    private String orderTopic;

    @Test
    public void sendOrderEventToKafka() {
        String product = "my product";
        Integer quantity = 100;
        Order order = new Order();
        order.setProduct(product);
        order.setQuantity(quantity);

        OrderEvent orderEvent = objectMapper.convertValue(order, OrderEvent.class);

        kafkaService.sendOrderEventToKafka(orderTopic, order);

        await()
                .pollInterval(Duration.ofSeconds(3))
                .atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    Consumer<String, Object> consumer = consumerFactory.createConsumer();
                    List<TopicPartition> partitions = consumer.partitionsFor(orderTopic).stream()
                            .map(p -> new TopicPartition(orderTopic, p.partition()))
                            .toList();
                    consumer.assign(partitions);
                    consumer.seekToBeginning(partitions);

                    ConsumerRecords<String, Object> records = consumer.poll(Duration.ofSeconds(10));

                    for (ConsumerRecord<String, Object> record : records) {
                        Assertions.assertEquals(orderEvent, record.value());
                    }
                });
    }
}