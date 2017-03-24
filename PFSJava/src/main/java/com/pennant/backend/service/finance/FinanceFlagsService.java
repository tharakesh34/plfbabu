package com.pennant.backend.service.finance;




import java.util.List;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.financemanagement.FinanceFlag;

public interface FinanceFlagsService {
	FinanceFlag getNewFinanceFlags();
	FinanceFlag getFinanceFlagsByRef(String ref, String type);
	FinanceFlag getApprovedFinanceFlagsById(String finReference);
	AuditHeader saveOrUpdate(AuditHeader auditHeader);
	AuditHeader delete(AuditHeader auditHeader);
	AuditHeader doApprove(AuditHeader auditHeader);
	AuditHeader doReject(AuditHeader auditHeader);
	List<String> getScheduleEffectModuleList(boolean schdChangeReq);
	AuditDetail doValidations(FinanceFlag financeFlag);
	AuditHeader deleteFinanceFlag(AuditHeader auditHeader);
}

