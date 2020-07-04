package com.pennant.backend.service.applicationmaster;

import java.util.List;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinTypeKnockOff;

public interface LoanTypeKnockOffService {

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

	List<FinTypeKnockOff> getKnockOffMappingById(String finType);

	List<FinTypeKnockOff> getApprovedKnockOffMappingById(String finType);

}
