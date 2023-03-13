package com.pennant.backend.service.pdc;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.ChequeHeader;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public interface ChequeHeaderService {
	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	AuditHeader delete(AuditHeader auditHeader);

	ChequeHeader getChequeHeader(long headerId);

	ChequeHeader getApprovedChequeHeader(long headerId);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

	ChequeHeader getChequeHeaderByRef(long finID);

	FinanceDetail getFinanceDetailById(long finID);

	ErrorDetail chequeValidation(FinanceDetail fd, String methodName, String tableType);

	ErrorDetail chequeValidationForUpdate(FinanceDetail fd, String methodUpdate, String tableType);

	ErrorDetail chequeValidationInMaintainence(FinanceDetail fd, String methodUpdate, String tableType);

	ChequeHeader getChequeDetails(String finReference);

	ErrorDetail validateBasicDetails(FinanceDetail fd, String type);

	ErrorDetail processChequeDetail(FinanceDetail fd, String type, LoggedInUser loggedInUser);

	ChequeHeader getApprovedChequeHeaderForEnq(long finID);

}
