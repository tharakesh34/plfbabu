package com.pennant.backend.service.transactionmapping;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennanttech.pff.cd.model.TransactionMapping;

public interface TransactionMappingService {

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

	TransactionMapping getTransactionMappingById(long id);

}
