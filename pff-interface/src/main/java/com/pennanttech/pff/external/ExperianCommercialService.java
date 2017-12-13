package com.pennanttech.pff.external;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennanttech.pennapps.core.InterfaceException;

public interface ExperianCommercialService {

	AuditHeader getBureauCommercial(AuditHeader auditHeader) throws InterfaceException;

}