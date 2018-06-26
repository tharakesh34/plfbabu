package com.pennanttech.pff.external;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.finance.FinanceMain;

public interface CreditInformation {

	AuditHeader getCreditEnquiryDetails(AuditHeader auditHeader, boolean isFromCustomer) throws Exception;

	CustomerDetails procesCreditEnquiry(CustomerDetails customerDetails, FinanceMain financeMain, boolean override) throws Exception;

}
