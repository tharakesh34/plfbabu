package com.pennanttech.pennapps.pff.service.hook;

import java.util.List;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennanttech.pennapps.core.model.ErrorDetail;

public interface PostValidationHook {
	List<ErrorDetail> validation(AuditHeader auditHeader);
}
