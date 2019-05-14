package com.pennant.backend.service.systemmasters;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.DealerGroup;

public interface DealerGroupService {
	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	DealerGroup getDealerGroup(long id);

	DealerGroup getApprovedDealerGroup(long id);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);
}
