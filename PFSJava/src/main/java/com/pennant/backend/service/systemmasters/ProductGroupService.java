package com.pennant.backend.service.systemmasters;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.ProductGroup;

public interface ProductGroupService {
	
	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	ProductGroup getProductGroup(long id);

	ProductGroup getApprovedProductGroup(long id);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

}
