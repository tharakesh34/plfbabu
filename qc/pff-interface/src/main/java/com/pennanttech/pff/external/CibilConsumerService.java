package com.pennanttech.pff.external;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennanttech.pennapps.core.InterfaceException;

public interface CibilConsumerService {

	AuditHeader getCibilConsumer(AuditHeader auditHeader) throws InterfaceException;

}
