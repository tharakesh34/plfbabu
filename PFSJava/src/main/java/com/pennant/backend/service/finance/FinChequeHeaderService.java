package com.pennant.backend.service.finance;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.ChequeHeader;
import com.pennanttech.pff.core.TableType;

public interface FinChequeHeaderService {

	AuditHeader saveOrUpdate(AuditHeader auditHeader, TableType tableType);

	AuditHeader delete(AuditHeader auditHeader);

	ChequeHeader getChequeHeader(String finRef);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

	ChequeHeader getChequeHeaderByRef(String finReference);
}
