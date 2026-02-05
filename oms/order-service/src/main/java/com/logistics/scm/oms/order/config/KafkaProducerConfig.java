package com.logistics.scm.oms.order.config;

import com.logistics.scm.oms.order.event.order.OrderCancelledEvent;
import com.logistics.scm.oms.order.event.order.OrderCreatedEvent;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka Producer 설정
 * 
 * Order Service에서 이벤트를 Kafka로 발행하기 위한 설정
 */
@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    /**
     * Producer 공통 설정
     */
    private Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(ProducerConfig.ACKS_CONFIG, "all"); // 모든 replica 확인
        props.put(ProducerConfig.RETRIES_CONFIG, 3); // 재시도 3회
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true); // 멱등성 보장
        return props;
    }

    /**
     * OrderCreatedEvent Producer Factory
     */
    @Bean
    public ProducerFactory<String, OrderCreatedEvent> orderCreatedEventProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    /**
     * OrderCreatedEvent KafkaTemplate
     */
    @Bean
    public KafkaTemplate<String, OrderCreatedEvent> orderCreatedEventKafkaTemplate() {
        return new KafkaTemplate<>(orderCreatedEventProducerFactory());
    }

    /**
     * OrderCancelledEvent Producer Factory
     */
    @Bean
    public ProducerFactory<String, OrderCancelledEvent> orderCancelledEventProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    /**
     * OrderCancelledEvent KafkaTemplate
     */
    @Bean
    public KafkaTemplate<String, OrderCancelledEvent> orderCancelledEventKafkaTemplate() {
        return new KafkaTemplate<>(orderCancelledEventProducerFactory());
    }
}
