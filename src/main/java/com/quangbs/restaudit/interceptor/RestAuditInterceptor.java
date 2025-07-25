package com.quangbs.restaudit.interceptor;

import com.quangbs.restaudit.annotions.RestAudit;
import com.quangbs.restaudit.configs.RestAuditProperties;
import com.quangbs.restaudit.models.RestAuditData;
import com.quangbs.restaudit.sinks.AuditSinkService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class RestAuditInterceptor implements HandlerInterceptor {

    private static final String START_TIME = "requestStartTime";
    private final RestAuditProperties restAuditProperties;
    private final AuditSinkService auditSinkService;

    public RestAuditInterceptor(RestAuditProperties restAuditProperties, AuditSinkService auditSinkService) {
        this.restAuditProperties = restAuditProperties;
        this.auditSinkService = auditSinkService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute(START_TIME, System.currentTimeMillis());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        if (handler instanceof HandlerMethod handlerMethod) {
            Long startTimeMillis = (Long) request.getAttribute(START_TIME);
            long duration = System.currentTimeMillis() - startTimeMillis; // Calculate elapsed time if needed
            RestAudit restAudit = handlerMethod.getMethod().getAnnotation(RestAudit.class);
            if (restAudit == null) {
                Class<?> controllerClass = handlerMethod.getBeanType();
                restAudit = controllerClass.getAnnotation(RestAudit.class);
            }
            if (restAudit != null) {
                RestAuditData auditData = RestAuditData.builder()
                        .serviceId(restAuditProperties.getServiceId())
                        .method(request.getMethod())
                        .url(request.getRequestURI())
                        .statusCode(response.getStatus())
                        .headers(getHeaders(request))
                        .requestBody(getRequestBody(request))
                        .responseBody(getResponseBody(response))
                        .message(restAudit.message())
                        .durationMillis(duration)
                        .timestamp(java.time.Instant.now())
                        .build();

                // Log or store the audit data as needed
                auditSinkService.sendAuditData(auditData);
            }
        }
    }

    private Map<String, String> getHeaders(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();
        request.getHeaderNames().asIterator().forEachRemaining(headerName -> {
            String value = request.getHeader(headerName);
            headers.put(headerName, value);
        });
        return headers;
    }

    private String getRequestBody(HttpServletRequest request) {
        ContentCachingRequestWrapper wrappedRequest = (ContentCachingRequestWrapper) request;
        if (isJsonRequest(wrappedRequest)) {
            byte[] resBodyBytes = wrappedRequest.getContentAsByteArray();
            if (resBodyBytes.length > 0) {
                String responseBody = new String(resBodyBytes, StandardCharsets.UTF_8);
                responseBody = truncate(responseBody);
                return responseBody;
            }
        }
        return null;
    }

    private String getResponseBody(HttpServletResponse response) {
        ContentCachingResponseWrapper wrappedResponse = (ContentCachingResponseWrapper) response;
        if (isJsonResponse(wrappedResponse)) {
            byte[] resBodyBytes = wrappedResponse.getContentAsByteArray();
            if (resBodyBytes.length > 0) {
                String responseBody = new String(resBodyBytes, StandardCharsets.UTF_8);
                responseBody = truncate(responseBody);
                return responseBody;
            }
        }
        return null;
    }

    /**
     * Support for JSON request
     * @param request HttpServletRequest
     * @return true if the request is JSON, false otherwise
     */
    private boolean isJsonRequest(HttpServletRequest request) {
        String contentType = request.getContentType();
        return contentType != null && contentType.contains("application/json");
    }

    /**
     * Support for JSON response
     * @param response HttpServletResponse
     * @return true if the response is JSON, false otherwise
     */
    private boolean isJsonResponse(HttpServletResponse response) {
        String contentType = response.getContentType();
        return contentType != null && (contentType.contains("application/json") || contentType.contains("text/json") || contentType.contains("text/html"));
    }

    /**
     * Truncate the input string to a maximum length defined in Constants.
     * If the input string exceeds the maximum length, it appends "(truncated)" to the end.
     *
     * @param input The input string to be truncated.
     * @return The truncated string if it exceeds the maximum length, otherwise the original string.
     */
    private String truncate(String input) {
        int maxLength = restAuditProperties.getResponse().getMaxLength();
        return input.length() > maxLength
                ? input.substring(0, maxLength) + "...(truncated)"
                : input;
    }
}
