package com.quangbs.restaudit.configs;

import com.quangbs.restaudit.filters.RestAuditFilter;
import com.quangbs.restaudit.interceptor.RestAuditInterceptor;
import com.quangbs.restaudit.models.RestAuditData;
import com.quangbs.restaudit.sinks.AuditSinkService;
import com.quangbs.restaudit.sinks.impl.KafkaAuditSinkService;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableConfigurationProperties(RestAuditProperties.class)
public class RestAuditConfiguration {

    /**
     * Provides a default implementation of AuditSinkService if no other bean is defined.
     * This bean will be used to send audit data to Kafka.
     *
     * @return an instance of KafkaAuditSinkService
     */
    @Bean(name = "auditSinkService")
    @ConditionalOnMissingBean(AuditSinkService.class)
    public AuditSinkService auditSinkService(@Qualifier(value = "restAuditDataKafkaTemplate") KafkaTemplate<String, RestAuditData> kafkaTemplate, RestAuditProperties restAuditProperties) {
        return new KafkaAuditSinkService(kafkaTemplate, restAuditProperties);
    }

    @Bean(name = "restAuditTopic")
    @ConditionalOnBean(value = AuditSinkService.class, name = "auditSinkService")
    public NewTopic restAuditTopic(RestAuditProperties restAuditProperties) {
        String topicName = restAuditProperties.getSink().getKafka().getTopic();
        int partitionCount = restAuditProperties.getSink().getKafka().getPartitionCount();
        short replicationFactor = restAuditProperties.getSink().getKafka().getReplicationFactor();
        return new NewTopic(topicName, partitionCount, replicationFactor); // 1 partition, replication factor of 1
    }

    @Bean(name = "restAuditDataKafkaTemplate")
    @ConditionalOnBean(value = AuditSinkService.class, name = "auditSinkService")
    public KafkaTemplate<String, RestAuditData> restAuditDataKafkaTemplate(RestAuditProperties auditProperties) {
        // Create and return the KafkaTemplate using the KafkaProducer
        RestAuditProperties.AuditSinkKafka config = auditProperties.getSink().getKafka();
        return new KafkaTemplate<>(getProducerFactory(config));
    }

    @Bean(name = "restAuditInterceptor")
    public RestAuditInterceptor restAuditInterceptor(RestAuditProperties restAuditProperties, AuditSinkService auditSinkService) {
        return new RestAuditInterceptor(restAuditProperties, auditSinkService);
    }

    @Bean(name = "restAuditFilter")
    public RestAuditFilter restAuditFilter() {
        return new RestAuditFilter();
    }

    @Bean(name = "restAuditFilterRegistration")
    public FilterRegistrationBean<RestAuditFilter> restAuditFilterRegistration(RestAuditFilter filter) {
        FilterRegistrationBean<RestAuditFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(filter);
        registration.setOrder(Ordered.LOWEST_PRECEDENCE);
        registration.addUrlPatterns("/*");
        return registration;
    }

    public ProducerFactory<String, RestAuditData> getProducerFactory(RestAuditProperties.AuditSinkKafka config) {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getBootstrapServers());
        props.put(ProducerConfig.CLIENT_ID_CONFIG, config.getClientId());
        props.put(ProducerConfig.ACKS_CONFIG, config.getAcks());
        props.put(ProducerConfig.CONNECTIONS_MAX_IDLE_MS_CONFIG, config.getMaxIdleMs());
        props.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, config.getMaxBlockMs());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(props);
    }
}
