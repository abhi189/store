package com.budderfly.sites.service;

import com.budderfly.sites.domain.enumeration.KafkaTopics;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {

    private final Logger log = LoggerFactory.getLogger(KafkaProducer.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final ObjectMapper objectMapper;

    public KafkaProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = new ObjectMapper();
    }

    public void sendMessage(KafkaTopics topic, Object message) {
        log.debug("KAFKA PRODUCING TOPIC " + topic + " and message " + message.toString());
        String json = "";

        try {
            json = objectMapper.writeValueAsString(message); // need to send as a json string
        } catch (JsonProcessingException e) {
            log.error("Could not convert kafka message to json string. " + e);
            return;
        }

        this.kafkaTemplate.send(topic.toString(), json);
    }

}
