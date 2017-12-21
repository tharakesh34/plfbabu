package com.pennanttech.pff.external;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennanttech.pennapps.core.InterfaceException;

public interface LegalDeskService {
	public AuditHeader extecuteLegalDesk(AuditHeader auditHeader) throws InterfaceException;
}
