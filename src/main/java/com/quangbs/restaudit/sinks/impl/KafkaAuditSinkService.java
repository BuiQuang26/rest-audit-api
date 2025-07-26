package com.quangbs.restaudit.sinks.impl;

import com.quangbs.restaudit.configs.RestAuditProperties;
import com.quangbs.restaudit.models.RestAuditData;
import com.quangbs.restaudit.sinks.AuditSinkService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * Implementation of AuditSinkService that sends audit data to Kafka.
 * This class is responsible for handling the actual sending of audit data to a Kafka topic.
 */
public class KafkaAuditSinkService implements AuditSinkService {

    private final Logger log = LoggerFactory.getLogger(KafkaAuditSinkService.class);
    private final KafkaTemplate<String, RestAuditData> kafkaTemplate;
    private final RestAuditProperties restAuditProperties;

    public KafkaAuditSinkService(KafkaTemplate<String, RestAuditData> kafkaTemplate, RestAuditProperties restAuditProperties) {
        this.kafkaTemplate = kafkaTemplate;
        this.restAuditProperties = restAuditProperties;
    }

    @Override
    public void sendAuditData(RestAuditData auditData) {
        log.debug("Sending rest audit log ==> Kafka: {}", auditData);
        String topic = restAuditProperties.getSink().getKafka().getTopic();
        kafkaTemplate.send(topic, auditData);
    }
}
