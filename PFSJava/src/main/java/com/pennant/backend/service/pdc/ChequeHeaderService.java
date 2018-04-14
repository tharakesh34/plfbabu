package com.pennant.backend.service.pdc;

import java.util.List;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.ChequeHeader;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennanttech.pff.core.TableType;

public interface ChequeHeaderService {
	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	AuditHeader delete(AuditHeader auditHeader);

	ChequeHeader getChequeHeader(long headerId);
	
	ChequeHeader getApprovedChequeHeader(long headerId);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

	List<AuditDetail> processingChequeDetailList(List<AuditDetail> auditDetails, TableType type, long headerID,
			String finCcy);

	ChequeHeader getChequeHeaderByRef(String finReference);
	
	FinanceDetail getFinanceDetailById(String finReference);
}
