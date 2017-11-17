package com.pennanttech.pff.external;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennanttech.pennapps.core.InterfaceException;

public interface ExternalDedup {
	
	public AuditHeader checkDedup(AuditHeader auditHeader) throws InterfaceException;

}
