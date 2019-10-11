package com.pennanttech.pff.external;

import com.pennant.backend.model.audit.AuditHeader;

@Deprecated
/**
 * 
 * @author varaprasad.k
 * use DocumentManagementService instead.
 */
public interface DMSIntegrationService {
	public AuditHeader insertExternalDocument(AuditHeader auditHeader) throws Exception;
}
