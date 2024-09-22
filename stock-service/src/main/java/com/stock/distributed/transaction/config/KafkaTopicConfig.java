package com.stock.distributed.transaction.config;

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

    @Bean(name = TopicName.STOCK_REVERSED)
    public NewTopic stockReserved() throws ExecutionException, InterruptedException {
        return kafkaConfig.buildTopic(TopicName.STOCK_REVERSED).build();
    }

    @Bean(name = TopicName.STOCK_RESERVATION_FAILED)
    public NewTopic stockReservationFailed() throws ExecutionException, InterruptedException {
        return kafkaConfig.buildTopic(TopicName.STOCK_RESERVATION_FAILED).build();
    }

    @Bean(name = TopicName.STOCK_RELEASED)
    public NewTopic stockReleased() throws ExecutionException, InterruptedException {
        return kafkaConfig.buildTopic(TopicName.STOCK_RELEASED).build();
    }
}
