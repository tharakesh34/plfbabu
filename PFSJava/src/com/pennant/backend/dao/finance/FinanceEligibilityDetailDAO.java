package com.pennant.backend.dao.finance;

import java.util.List;

import com.pennant.backend.model.finance.FinanceEligibilityDetail;

public interface FinanceEligibilityDetailDAO {
	
	void saveList(List<FinanceEligibilityDetail> eligibilityDetails);
	void updateList(List<FinanceEligibilityDetail> eligibilityDetails);
	List<FinanceEligibilityDetail> getFinElgDetailByFinRef(String finReference, String type);
	int getFinElgDetailCount(FinanceEligibilityDetail financeEligibilityDetail);
}
