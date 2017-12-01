package com.pennanttech.pff.external;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennanttech.pennapps.core.InterfaceException;

public interface CrifConsumerService {
	AuditHeader getCrifBureauConsumer(AuditHeader auditHeader) throws InterfaceException;
}
