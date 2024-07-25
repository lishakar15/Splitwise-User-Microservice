package com.splitwise.microservices.user_service.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfiguration {

    @Bean
    public NewTopic getKafkaTopic()
    {
        return TopicBuilder.name("activity").partitions(3).replicas(1).build();
    }
}
