package com.pennanttech.pff.external;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennanttech.pennapps.core.InterfaceException;

public interface LoanDataSyncService {
	public AuditHeader executeDataSync(AuditHeader auditHeader) throws InterfaceException, Exception;
}
