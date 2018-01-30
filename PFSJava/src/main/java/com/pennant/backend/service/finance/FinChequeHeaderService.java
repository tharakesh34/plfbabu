package com.pennant.backend.service.finance;

import java.util.List;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.ChequeHeader;
import com.pennanttech.pff.core.TableType;

public interface FinChequeHeaderService {
	
	AuditHeader saveOrUpdate(AuditHeader auditHeader, TableType tableType);

	AuditHeader delete(AuditHeader auditHeader);

	ChequeHeader getChequeHeader(String finRef);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

	List<AuditDetail> processingChequeDetailList(List<AuditDetail> auditDetails, TableType type, long headerID,
			String finCcy);
	
	ChequeHeader getChequeHeaderByRef(String finReference);
}
