package com.splitwise.microservices.user_service.kafka;

import com.splitwise.microservices.user_service.external.ActivityRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {
    @Autowired
    KafkaTemplate kafkaTemplate;
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaProducer.class);

    public void sendMessage(String activityMsg)
    {
        try
        {
            kafkaTemplate.send("activity",activityMsg);
        }
        catch(Exception ex)
        {
            LOGGER.error("Error occurred while sending User message to Kafka ",ex);
        }
    }
}
