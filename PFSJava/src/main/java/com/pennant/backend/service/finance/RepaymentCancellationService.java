package com.pennant.backend.service.finance;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennanttech.pennapps.core.AppException;

public interface RepaymentCancellationService {

	FinanceDetail getFinanceDetailById(long finID, String type);

	AuditHeader doApprove(AuditHeader aAuditHeader) throws AppException;

	AuditHeader doReject(AuditHeader auditHeader);

	AuditHeader saveOrUpdate(AuditHeader aAuditHeader) throws AppException;

}
