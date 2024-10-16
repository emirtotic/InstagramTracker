package com.instagram.service.impl;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerServiceImpl {

    @KafkaListener(topics = "followers-topic", groupId = "group_id")
    public void consume(String message) {
        System.out.println("Message received: " + message);
    }
}

