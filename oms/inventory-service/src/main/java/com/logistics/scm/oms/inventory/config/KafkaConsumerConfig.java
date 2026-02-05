package com.logistics.scm.oms.inventory.config;

import com.logistics.scm.oms.inventory.event.order.OrderCancelledEvent;
import com.logistics.scm.oms.inventory.event.order.OrderCreatedEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka Consumer 설정
 * 
 * Inventory Service에서 Order 이벤트를 구독하기 위한 설정
 */
@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    /**
     * Consumer 공통 설정
     */
    private Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.logistics.scm.oms.inventory.event.order");
        return props;
    }

    /**
     * OrderCreatedEvent Consumer Factory
     */
    @Bean
    public ConsumerFactory<String, OrderCreatedEvent> orderCreatedEventConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(
                consumerConfigs(),
                new StringDeserializer(),
                new JsonDeserializer<>(OrderCreatedEvent.class, false)
        );
    }

    /**
     * OrderCreatedEvent Listener Container Factory
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderCreatedEvent> 
            orderCreatedEventKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, OrderCreatedEvent> factory = 
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(orderCreatedEventConsumerFactory());
        return factory;
    }

    /**
     * OrderCancelledEvent Consumer Factory
     */
    @Bean
    public ConsumerFactory<String, OrderCancelledEvent> orderCancelledEventConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(
                consumerConfigs(),
                new StringDeserializer(),
                new JsonDeserializer<>(OrderCancelledEvent.class, false)
        );
    }

    /**
     * OrderCancelledEvent Listener Container Factory
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderCancelledEvent> 
            orderCancelledEventKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, OrderCancelledEvent> factory = 
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(orderCancelledEventConsumerFactory());
        return factory;
    }
}
