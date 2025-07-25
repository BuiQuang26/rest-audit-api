package com.quangbs.restaudit.configs;

import com.quangbs.restaudit.filters.RestAuditFilter;
import com.quangbs.restaudit.interceptor.RestAuditInterceptor;
import com.quangbs.restaudit.sinks.AuditSinkService;
import com.quangbs.restaudit.sinks.impl.KafkaAuditSinkService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
@EnableConfigurationProperties(RestAuditProperties.class)
public class RestAuditConfiguration {

    /**
     * Provides a default implementation of AuditSinkService if no other bean is defined.
     * This bean will be used to send audit data to Kafka.
     *
     * @return an instance of KafkaAuditSinkService
     */
    @Bean
    @ConditionalOnMissingBean(AuditSinkService.class)
    public AuditSinkService auditSinkService() {
        return new KafkaAuditSinkService();
    }

    @Bean
    public RestAuditInterceptor restAuditInterceptor(RestAuditProperties restAuditProperties, AuditSinkService auditSinkService) {
        return new RestAuditInterceptor(restAuditProperties, auditSinkService);
    }

    @Bean
    public RestAuditFilter restAuditFilter() {
        return new RestAuditFilter();
    }

    @Bean
    public FilterRegistrationBean<RestAuditFilter> restAuditFilterRegistration(RestAuditFilter filter) {
        FilterRegistrationBean<RestAuditFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(filter);
        registration.setOrder(Ordered.LOWEST_PRECEDENCE);
        registration.addUrlPatterns("/*");
        return registration;
    }
}
