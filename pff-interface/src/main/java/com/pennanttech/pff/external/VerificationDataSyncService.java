package com.pennanttech.pff.external;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennanttech.pennapps.core.InterfaceException;

public interface VerificationDataSyncService {
	public AuditHeader executeDataSync(AuditHeader auditHeader) throws InterfaceException, Exception;
}
