package com.pennant.backend.service.applicationmaster;

import java.util.List;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinTypeWriteOff;

public interface LoanTypeWriteOffService {

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

	List<FinTypeWriteOff> getWriteOffMappingById(String finType);

	List<FinTypeWriteOff> getApprovedWriteOffMappingById(String finType);

}
