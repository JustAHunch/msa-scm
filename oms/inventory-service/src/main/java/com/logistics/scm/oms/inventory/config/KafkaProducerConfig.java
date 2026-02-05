package com.logistics.scm.oms.inventory.config;

import com.logistics.scm.oms.inventory.event.InventoryReleasedEvent;
import com.logistics.scm.oms.inventory.event.InventoryReservationFailedEvent;
import com.logistics.scm.oms.inventory.event.InventoryReservedEvent;
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
 * Inventory Service에서 이벤트를 Kafka로 발행하기 위한 설정
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
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, 3);
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        return props;
    }

    /**
     * InventoryReservedEvent Producer Factory
     */
    @Bean
    public ProducerFactory<String, InventoryReservedEvent> inventoryReservedEventProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    /**
     * InventoryReservedEvent KafkaTemplate
     */
    @Bean
    public KafkaTemplate<String, InventoryReservedEvent> inventoryReservedEventKafkaTemplate() {
        return new KafkaTemplate<>(inventoryReservedEventProducerFactory());
    }

    /**
     * InventoryReservationFailedEvent Producer Factory
     */
    @Bean
    public ProducerFactory<String, InventoryReservationFailedEvent> 
            inventoryReservationFailedEventProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    /**
     * InventoryReservationFailedEvent KafkaTemplate
     */
    @Bean
    public KafkaTemplate<String, InventoryReservationFailedEvent> 
            inventoryReservationFailedEventKafkaTemplate() {
        return new KafkaTemplate<>(inventoryReservationFailedEventProducerFactory());
    }

    /**
     * InventoryReleasedEvent Producer Factory
     */
    @Bean
    public ProducerFactory<String, InventoryReleasedEvent> inventoryReleasedEventProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    /**
     * InventoryReleasedEvent KafkaTemplate
     */
    @Bean
    public KafkaTemplate<String, InventoryReleasedEvent> inventoryReleasedEventKafkaTemplate() {
        return new KafkaTemplate<>(inventoryReleasedEventProducerFactory());
    }
}
