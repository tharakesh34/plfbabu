package com.pennant.backend.service.applicationmaster;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.blacklist.BlackListCustomers;

public interface BlacklistCustomerService {

	BlackListCustomers getBlacklistCustomerById(String id);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

	BlackListCustomers getApprovedBlacklistById(String id);
}
