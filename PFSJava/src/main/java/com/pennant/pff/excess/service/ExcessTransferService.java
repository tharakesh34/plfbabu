package com.pennant.pff.excess.service;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.pff.excess.model.FinExcessTransfer;

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

	AEEvent executeAccounting(FinExcessTransfer excessTransfer);

}
