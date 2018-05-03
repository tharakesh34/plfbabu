package com.pennanttech.pff.external;

import com.pennant.backend.model.audit.AuditHeader;

public interface CreditInformation {

	AuditHeader getCreditEnquiryDetails(AuditHeader auditHeader) throws Exception;

}
