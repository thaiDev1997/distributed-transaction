package com.common.distributed.transaction.config;

import com.common.distributed.transaction.constant.TopicName;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.Node;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.support.converter.JsonMessageConverter;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public JsonMessageConverter jsonMessageConverter() {
        return new JsonMessageConverter();
    }

    public int determineReplicationFactor() throws ExecutionException, InterruptedException {
        Map<String, Object> conf = Map.of(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        try (AdminClient adminClient = AdminClient.create(conf)) {
            Collection<Node> nodes = adminClient.describeCluster().nodes().get();
            int numberOfBrokers = nodes.size();
            // set replication factor as 2 or the number of brokers, whichever is smaller
            return Math.min(2, numberOfBrokers);
        }
    }

    public TopicBuilder buildTopic(String name) throws ExecutionException, InterruptedException {
        int numReplications = this.determineReplicationFactor();
        int numPartitions = 2 * numReplications;
        return TopicBuilder.name(name).replicas(numReplications).partitions(numPartitions);
    }

}
