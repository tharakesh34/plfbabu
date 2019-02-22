package com.pennanttech.ws.model.eligibility;

import java.util.List;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.finance.FinanceEligibilityDetail;

public class EligibilityDetailResponse {
	
	private List<FinanceEligibilityDetail> eligibilityDetails;

	private WSReturnStatus returnStatus;
	
	public EligibilityDetailResponse() {
		super();
	}

	public List<FinanceEligibilityDetail> getEligibilityDetails() {
		return eligibilityDetails;
	}

	public void setEligibilityDetails(List<FinanceEligibilityDetail> eligibilityDetails) {
		this.eligibilityDetails = eligibilityDetails;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}
}
