package com.pennanttech.pff.external;

import java.text.ParseException;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennanttech.pennapps.core.InterfaceException;

public interface CriffBureauService {

	AuditHeader executeCriffBureau(AuditHeader auditHeader) throws InterfaceException, ParseException;
}
