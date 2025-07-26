package com.quangbs.restaudit.configs;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rest-audit")
public class RestAuditProperties {

    /**
     * Service ID to identify the service in audit logs.
     */
    private String serviceId = "default-service-id"; // Default service ID

    private Response response = new Response();

    private RestAuditSink sink = new RestAuditSink();

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public RestAuditSink getSink() {
        return sink;
    }

    public void setSink(RestAuditSink sink) {
        this.sink = sink;
    }

    public static class Response {
        private int maxLength = 1024*10; // Default to 10KB

        public void setMaxLength(int maxLength) {
            if (maxLength <= 0) {
                throw new IllegalArgumentException("Max length must be greater than 0");
            }
            this.maxLength = maxLength;
        }

        public int getMaxLength() {
            return maxLength;
        }
    }

    public static class RestAuditSink {
        private AuditSinkKafka kafka = new AuditSinkKafka();

        public AuditSinkKafka getKafka() {
            return kafka;
        }

        public void setKafka(AuditSinkKafka kafka) {
            this.kafka = kafka;
        }
    }

    public static class AuditSinkKafka {
        private String bootstrapServers = "localhost:9092";
        private String clientId = "rest-audit-client"; // Default client ID
        private String acks = "all"; // Default to 'all' for strong durability guarantees
        private String topic = "rest-audit-api-sink"; // Default topic name
        private int maxBlockMs = 2000; // Default to 2 seconds
        private int partitionCount = 1; // Default to 1 partition
        private short replicationFactor = 1; // Default to 1 replication factor
        private int maxIdleMs = 60000; // Default to 30 seconds

        public String getBootstrapServers() {
            return bootstrapServers;
        }

        public void setBootstrapServers(String bootstrapServers) {
            this.bootstrapServers = bootstrapServers;
        }

        public String getClientId() {
            return clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

        public String getAcks() {
            return acks;
        }

        public void setAcks(String acks) {
            this.acks = acks;
        }

        public String getTopic() {
            return topic;
        }

        public void setTopic(String topic) {
            this.topic = topic;
        }

        public int getPartitionCount() {
            return partitionCount;
        }

        public void setPartitionCount(int partitionCount) {
            this.partitionCount = partitionCount;
        }

        public short getReplicationFactor() {
            return replicationFactor;
        }

        public void setReplicationFactor(short replicationFactor) {
            this.replicationFactor = replicationFactor;
        }

        public int getMaxBlockMs() {
            return maxBlockMs;
        }

        public void setMaxBlockMs(int maxBlockMs) {
            this.maxBlockMs = maxBlockMs;
        }

        public int getMaxIdleMs() {
            return maxIdleMs;
        }

        public void setMaxIdleMs(int maxIdleMs) {
            this.maxIdleMs = maxIdleMs;
        }
    }
}
