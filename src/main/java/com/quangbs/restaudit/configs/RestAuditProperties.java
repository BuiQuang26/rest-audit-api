package com.quangbs.restaudit.configs;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rest-audit")
public class RestAuditProperties {

    /**
     * Service ID to identify the service in audit logs.
     */
    private String serviceId = "default-service-id"; // Default service ID

    private Response response;

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
}
