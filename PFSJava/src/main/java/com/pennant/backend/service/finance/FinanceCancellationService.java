package com.pennant.backend.service.finance;

import java.util.List;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.reason.details.ReasonHeader;
import com.pennant.pff.fincancelupload.exception.FinCancelUploadError;

public interface FinanceCancellationService {

	FinanceDetail getFinanceDetailById(long finID, String type, String userRole, String procEdtEvent);

	AuditHeader saveOrUpdate(AuditHeader aAuditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader aAuditHeader, boolean isValReq);

	List<ReasonHeader> getCancelReasonDetails(String reference);

	List<FinAdvancePayments> getFinAdvancePaymentsByFinRef(long finID);

	FinCancelUploadError validLoan(FinanceMain fm);

}
