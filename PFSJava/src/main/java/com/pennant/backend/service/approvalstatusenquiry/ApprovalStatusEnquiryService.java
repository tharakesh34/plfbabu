package com.pennant.backend.service.approvalstatusenquiry;

import com.pennant.backend.model.finance.CustomerFinanceDetail;

public interface ApprovalStatusEnquiryService {

	CustomerFinanceDetail getApprovedCustomerFinanceById(String financeReference,String moduleDefiner);
 	CustomerFinanceDetail getCustomerFinanceById(String financeReference, String moduleDefiner);
 	CustomerFinanceDetail getApprovedCustomerFacilityById(String facilityReference);
 	CustomerFinanceDetail getCustomerFacilityById(String facilityReference);
 	
}
