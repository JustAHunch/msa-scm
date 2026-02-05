package com.logistics.scm.oms.order.config;

import com.logistics.scm.oms.order.event.inventory.InventoryReleasedEvent;
import com.logistics.scm.oms.order.event.inventory.InventoryReservationFailedEvent;
import com.logistics.scm.oms.order.event.inventory.InventoryReservedEvent;
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
 * Order Service에서 Inventory 이벤트를 구독하기 위한 설정
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
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.logistics.scm.oms.order.event.inventory");
        return props;
    }

    /**
     * InventoryReservedEvent Consumer Factory
     */
    @Bean
    public ConsumerFactory<String, InventoryReservedEvent> inventoryReservedEventConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(
                consumerConfigs(),
                new StringDeserializer(),
                new JsonDeserializer<>(InventoryReservedEvent.class, false)
        );
    }

    /**
     * InventoryReservedEvent Listener Container Factory
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, InventoryReservedEvent> 
            inventoryReservedEventKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, InventoryReservedEvent> factory = 
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(inventoryReservedEventConsumerFactory());
        return factory;
    }

    /**
     * InventoryReservationFailedEvent Consumer Factory
     */
    @Bean
    public ConsumerFactory<String, InventoryReservationFailedEvent> 
            inventoryReservationFailedEventConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(
                consumerConfigs(),
                new StringDeserializer(),
                new JsonDeserializer<>(InventoryReservationFailedEvent.class, false)
        );
    }

    /**
     * InventoryReservationFailedEvent Listener Container Factory
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, InventoryReservationFailedEvent> 
            inventoryReservationFailedEventKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, InventoryReservationFailedEvent> factory = 
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(inventoryReservationFailedEventConsumerFactory());
        return factory;
    }

    /**
     * InventoryReleasedEvent Consumer Factory
     */
    @Bean
    public ConsumerFactory<String, InventoryReleasedEvent> inventoryReleasedEventConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(
                consumerConfigs(),
                new StringDeserializer(),
                new JsonDeserializer<>(InventoryReleasedEvent.class, false)
        );
    }

    /**
     * InventoryReleasedEvent Listener Container Factory
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, InventoryReleasedEvent> 
            inventoryReleasedEventKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, InventoryReleasedEvent> factory = 
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(inventoryReleasedEventConsumerFactory());
        return factory;
    }
}
