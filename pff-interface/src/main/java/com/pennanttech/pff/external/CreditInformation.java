package com.pennanttech.pff.external;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennanttech.pennapps.core.InterfaceException;

public interface CreditInformation {

	AuditHeader getCreditEnquiryDetails(AuditHeader auditHeader) throws Exception;

}
