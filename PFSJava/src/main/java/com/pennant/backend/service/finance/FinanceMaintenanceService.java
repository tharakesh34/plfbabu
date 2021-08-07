package com.pennant.backend.service.finance;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinanceDetail;

public interface FinanceMaintenanceService {

	FinanceDetail getFinanceDetailById(String finReference, String type, String userRole, String procEdtEvent,
			String eventCode);

	AuditHeader saveOrUpdate(AuditHeader aAuditHeader) throws Exception;

	AuditHeader doReject(AuditHeader auditHeader) throws Exception;

	AuditHeader doApprove(AuditHeader aAuditHeader) throws Exception;

	boolean isFinActive(String finReference);

	int getSchdVersion(String finReference);

}
