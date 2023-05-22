package com.pennant.backend.service.finance;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.CrossLoanKnockOff;
import com.pennant.backend.model.finance.CrossLoanTransfer;

public interface CrossLoanKnockOffService {

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	CrossLoanTransfer getCrossLoanTransferById(long id, String type);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

	CrossLoanKnockOff getCrossLoanHeaderById(long crossLoanHeaderId, String type);

	void executeAccounting(CrossLoanTransfer crossLoan);
}
