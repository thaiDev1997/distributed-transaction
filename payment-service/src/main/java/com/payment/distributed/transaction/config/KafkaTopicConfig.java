package com.payment.distributed.transaction.config;

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

    @Bean(name = TopicName.PAYMENT_PROCESSED)
    public NewTopic paymentProcessed() throws ExecutionException, InterruptedException {
        return kafkaConfig.buildTopic(TopicName.PAYMENT_PROCESSED).build();
    }

    @Bean(name = TopicName.PAYMENT_FAILED)
    public NewTopic paymentFailed() throws ExecutionException, InterruptedException {
        return kafkaConfig.buildTopic(TopicName.PAYMENT_FAILED).build();
    }

    @Bean(name = TopicName.PAYMENT_REFUNDED)
    public NewTopic paymentRefunded() throws ExecutionException, InterruptedException {
        return kafkaConfig.buildTopic(TopicName.PAYMENT_REFUNDED).build();
    }
}
