package com.quangbs.restaudit.sinks.impl;

import com.quangbs.restaudit.models.RestAuditData;
import com.quangbs.restaudit.sinks.AuditSinkService;

/**
 * Implementation of AuditSinkService that sends audit data to Kafka.
 * This class is responsible for handling the actual sending of audit data to a Kafka topic.
 */
public class KafkaAuditSinkService implements AuditSinkService {

    @Override
    public void sendAuditData(RestAuditData auditData) {
        System.out.println("Sending audit data to Kafka: " + auditData);
    }
}
