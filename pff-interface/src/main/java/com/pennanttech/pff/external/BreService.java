package com.pennanttech.pff.external;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennanttech.pennapps.core.InterfaceException;

public interface BreService {
	public AuditHeader executeBRE(AuditHeader auditHeader) throws InterfaceException;
}
