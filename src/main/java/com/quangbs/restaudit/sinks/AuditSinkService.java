package com.quangbs.restaudit.sinks;

import com.quangbs.restaudit.models.RestAuditData;

/**
 * Interface for sending audit data to a log storage.
 * Implementations of this interface should handle the actual sending of audit data.
 */
public interface AuditSinkService {

    /**
     * Sends audit data to the log storge.
     *
     * @param auditData the audit data to send
     */
    void sendAuditData(RestAuditData auditData);
}
