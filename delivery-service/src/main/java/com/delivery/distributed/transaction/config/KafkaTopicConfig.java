package com.delivery.distributed.transaction.config;

import com.common.distributed.transaction.config.KafkaConfig;
import com.common.distributed.transaction.constant.TopicName;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutionException;

@Configuration
public class KafkaTopicConfig {

    @Autowired
    private KafkaConfig kafkaConfig;

    @Bean(name = TopicName.DELIVERY_SCHEDULED)
    public NewTopic deliveryScheduled() throws ExecutionException, InterruptedException {
        return kafkaConfig.buildTopic(TopicName.DELIVERY_SCHEDULED).build();
    }

    @Bean(name = TopicName.DELIVERY_FAILED)
    public NewTopic deliveryFailed() throws ExecutionException, InterruptedException {
        return kafkaConfig.buildTopic(TopicName.DELIVERY_FAILED).build();
    }

    @Bean(name = TopicName.DELIVERY_SHIPPING_STATUS_UPDATED)
    public NewTopic deliveryShippingStatusUpdated() throws ExecutionException, InterruptedException {
        return kafkaConfig.buildTopic(TopicName.DELIVERY_SHIPPING_STATUS_UPDATED).build();
    }
}
