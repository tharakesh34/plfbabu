package com.pennant.backend.service.approvalstatusenquiry;

import java.util.List;
import java.util.Map;

import com.pennant.backend.model.finance.CustomerFinanceDetail;

public interface ApprovalStatusEnquiryService {

	CustomerFinanceDetail getApprovedCustomerFinanceById(String financeReference, String moduleDefiner);

	CustomerFinanceDetail getCustomerFinanceById(String financeReference, String moduleDefiner);

	CustomerFinanceDetail getApprovedCustomerFacilityById(String facilityReference);

	CustomerFinanceDetail getCustomerFacilityById(String facilityReference);

	List<Map<String, Object>> getResonDetailsLog(String financeReference);

}
