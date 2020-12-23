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

	ChequeHeader getChequeHeaderByRef(String finReference);

	FinanceDetail getFinanceDetailById(String finReference);

	List<ErrorDetail> chequeValidation(FinanceDetail financeDetail, String methodName, String tableType);

	List<ErrorDetail> chequeValidationForUpdate(FinanceDetail financeDetail, String methodUpdate, String tableType);

	List<ErrorDetail> chequeValidationInMaintainence(FinanceDetail financeDetail, String methodUpdate,
			String tableType);
}
