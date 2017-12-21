package com.pennanttech.pff.external;

import java.text.ParseException;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennanttech.pennapps.core.InterfaceException;

public interface ExperianBureauService {

	AuditHeader executeExperianBureau(AuditHeader auditHeader) throws InterfaceException,ParseException;
}
