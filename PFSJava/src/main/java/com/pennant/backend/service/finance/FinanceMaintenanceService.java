package com.pennant.backend.service.finance;

import java.util.List;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;

public interface FinanceMaintenanceService {

	FinanceDetail getFinanceDetailById(long finID, String type, String userRole, String procEdtEvent, String eventCode);

	AuditHeader saveOrUpdate(AuditHeader aAuditHeader) throws Exception;

	AuditHeader doReject(AuditHeader auditHeader) throws Exception;

	AuditHeader doApprove(AuditHeader aAuditHeader) throws Exception;

	boolean isFinActive(long finID);

	int getSchdVersion(long finID);

	List<FinServiceInstruction> getFinServiceInstructions(long finID, String finEvent);

}
