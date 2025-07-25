package com.quangbs.restaudit.models;

import lombok.*;

import java.time.Instant;
import java.util.Map;

@Builder
@Data
public class RestAuditData {
    /**
     * ID of the service that is being audited. config this in application properties
     */
    private String serviceId;

    /**
     * Duration of the request in milliseconds.
     */
    private long durationMillis;

    /**
     * HTTP method used for the request (e.g., GET, POST).
     */
    private String method;

    /**
     * URL of the request.
     */
    private String url;

    /**
     * HTTP status code returned by the request.
     */
    private int statusCode;

    /**
     * Headers of the request.
     */
    private Map<String, String> headers;

    /**
     * Request body of the HTTP request, if the request body is present and JSON format.
     */
    private String requestBody;

    /**
     * Response body of the HTTP request, if the response body is present and JSON format or text.
     */
    private String responseBody;

    /**
     * Message associated with the audit event, typically used for logging or debugging.
     */
    private String message;

    /**
     * Timestamp of when the audit event occurred.
     */
    private Instant timestamp;
}
