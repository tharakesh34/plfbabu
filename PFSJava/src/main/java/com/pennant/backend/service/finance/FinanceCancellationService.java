package com.pennant.backend.service.finance;

import java.util.List;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.reason.details.ReasonHeader;

public interface FinanceCancellationService {

	FinanceDetail getFinanceDetailById(String finReference, String type, String userRole, String procEdtEvent);

	AuditHeader saveOrUpdate(AuditHeader aAuditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader aAuditHeader, boolean isValReq);

	List<ReasonHeader> getCancelReasonDetails(String reference);

}
