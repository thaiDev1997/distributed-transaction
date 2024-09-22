package com.order.distributed.transaction.config;

import com.common.distributed.transaction.config.KafkaConfig;
import com.common.distributed.transaction.constant.TopicName;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import java.util.concurrent.ExecutionException;

@Configuration
public class KafkaTopicConfig {

    @Autowired
    private KafkaConfig kafkaConfig;

    @Bean(name = TopicName.ORDER_CREATED)
    public NewTopic orderCreated() throws ExecutionException, InterruptedException {
        return kafkaConfig.buildTopic(TopicName.ORDER_CREATED).build();
    }

    @Bean(name = TopicName.STOCK_RESERVE_COMMAND)
    public NewTopic stockReserveCommand() throws ExecutionException, InterruptedException {
        return kafkaConfig.buildTopic(TopicName.STOCK_RESERVE_COMMAND).build();
    }

    @Bean(name = TopicName.PAYMENT_PROCESS_COMMAND)
    public NewTopic paymentProcessCommand() throws ExecutionException, InterruptedException {
        return kafkaConfig.buildTopic(TopicName.PAYMENT_PROCESS_COMMAND).build();
    }

    @Bean(name = TopicName.DELIVERY_SCHEDULE_COMMAND)
    public NewTopic deliveryScheduleCommand() throws ExecutionException, InterruptedException {
        return kafkaConfig.buildTopic(TopicName.DELIVERY_SCHEDULE_COMMAND).build();
    }

    @Bean(name = TopicName.ORDER_CANCELLED)
    public NewTopic orderCancelled() throws ExecutionException, InterruptedException {
        return kafkaConfig.buildTopic(TopicName.ORDER_CANCELLED).build();
    }

    @Bean(name = TopicName.ORDER_PROCESSING)
    public NewTopic orderProcessing() throws ExecutionException, InterruptedException {
        return kafkaConfig.buildTopic(TopicName.ORDER_PROCESSING).build();
    }

    @Bean(name = TopicName.ORDER_COMPLETED)
    public NewTopic orderCompleted() throws ExecutionException, InterruptedException {
        return kafkaConfig.buildTopic(TopicName.ORDER_COMPLETED).build();
    }
}
