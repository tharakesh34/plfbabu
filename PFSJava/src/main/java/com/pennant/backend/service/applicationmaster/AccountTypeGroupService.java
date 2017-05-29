package com.pennant.backend.service.applicationmaster;

import com.pennant.backend.model.applicationmaster.AccountTypeGroup;
import com.pennant.backend.model.audit.AuditHeader;

public interface AccountTypeGroupService {

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	AccountTypeGroup getAccountTypeGroupById(long grpId);

	AccountTypeGroup getApprovedAccountTypeGroupById(long grpId);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

}
