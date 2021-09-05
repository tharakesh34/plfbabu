package com.pennant.backend.service.finance;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinMaintainInstruction;

public interface FinOptionMaintanceService {

	FinMaintainInstruction getFinMaintainInstructionByFinRef(long finID, String event);

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

}
