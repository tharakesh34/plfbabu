package com.pennanttech.pff.external;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennanttech.pennapps.core.InterfaceException;

public interface HunterService {
	public AuditHeader getHunterStatus(AuditHeader auditHeader) throws InterfaceException, Exception;
}
