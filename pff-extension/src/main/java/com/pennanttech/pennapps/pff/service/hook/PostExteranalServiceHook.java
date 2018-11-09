package com.pennanttech.pennapps.pff.service.hook;

import com.pennant.backend.model.audit.AuditHeader;

/**
 * Post validation hook that uses to raise custom auto deviations.
 */
public interface PostExteranalServiceHook  {
	void doProcess(AuditHeader auditHeader, String method);
}
