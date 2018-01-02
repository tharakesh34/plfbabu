package com.pennant.backend.service.approvalstatusenquiry;

import java.util.List;

import com.pennant.backend.model.finance.CustomerFinanceDetail;
import com.pennant.backend.model.reason.details.ReasonDetailsLog;

public interface ApprovalStatusEnquiryService {

	CustomerFinanceDetail getApprovedCustomerFinanceById(String financeReference, String moduleDefiner);

	CustomerFinanceDetail getCustomerFinanceById(String financeReference, String moduleDefiner);

	CustomerFinanceDetail getApprovedCustomerFacilityById(String facilityReference);

	CustomerFinanceDetail getCustomerFacilityById(String facilityReference);

	List<ReasonDetailsLog> getResonDetailsLog(String financeReference);

}
