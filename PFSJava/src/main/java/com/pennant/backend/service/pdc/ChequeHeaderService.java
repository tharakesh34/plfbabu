package com.pennant.backend.service.pdc;

import java.util.List;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.ChequeHeader;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennanttech.pennapps.core.model.ErrorDetail;

public interface ChequeHeaderService {
	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	AuditHeader delete(AuditHeader auditHeader);

	ChequeHeader getChequeHeader(long headerId);

	ChequeHeader getApprovedChequeHeader(long headerId);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

	ChequeHeader getChequeHeaderByRef(long finID);

	FinanceDetail getFinanceDetailById(long finID);

	List<ErrorDetail> chequeValidation(FinanceDetail fd, String methodName, String tableType);

	List<ErrorDetail> chequeValidationForUpdate(FinanceDetail fd, String methodUpdate, String tableType);

	List<ErrorDetail> chequeValidationInMaintainence(FinanceDetail fd, String methodUpdate, String tableType);
}
