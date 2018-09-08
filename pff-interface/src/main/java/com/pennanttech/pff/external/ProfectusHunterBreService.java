package com.pennanttech.pff.external;

import com.pennant.backend.model.audit.AuditHeader;

public interface ProfectusHunterBreService {
	public AuditHeader getOnlineMatchDetails(AuditHeader auditHeader) throws Exception;
}
