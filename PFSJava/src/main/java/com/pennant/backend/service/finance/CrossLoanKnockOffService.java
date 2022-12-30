package com.pennant.backend.service.finance;

import java.util.List;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.CrossLoanKnockOffHeader;
import com.pennant.backend.model.finance.CrossLoanTransfer;

public interface CrossLoanKnockOffService {

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	CrossLoanTransfer getCrossLoanTransferById(long id, String type);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

	List<CrossLoanTransfer> getExcessAmountsByRefAndType(String finReference, String amountType);

	// FinExcessAmount getExcessAmountById(long excessId, String amountType);

	CrossLoanKnockOffHeader getCrossLoanHeaderById(long crossLoanHeaderId, String type);

}
