package com.pennant.backend.service.excessheadmaster;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.excessheadmaster.FinExcessTransfer;
import com.pennant.backend.model.finance.FinExcessAmount;

public interface ExcessTransferService {

	FinExcessTransfer getExcessTransfer(long transferId);

	FinExcessTransfer getExcessTransferData(long finId, long transferId);

	FinExcessTransfer getFinExcessData(long finID);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

	boolean isReferenceExist(String finReference);

	FinExcessAmount getFinExcessAmountById(long excessId);

}
