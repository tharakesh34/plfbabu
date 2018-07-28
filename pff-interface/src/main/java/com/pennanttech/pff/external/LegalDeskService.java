package com.pennanttech.pff.external;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennanttech.pennapps.core.InterfaceException;

public interface LegalDeskService {
	public AuditHeader executeLegalDesk(AuditHeader auditHeader,String apiType) throws InterfaceException;
}
