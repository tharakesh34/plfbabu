package com.pennant.backend.service.dealermapping;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.dealermapping.DealerMapping;

public interface DealerMappingService {

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

	DealerMapping getDealerMappingById(long id);

	long getDealerCode();
}
