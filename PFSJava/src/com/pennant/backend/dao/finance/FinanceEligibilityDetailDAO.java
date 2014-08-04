package com.pennant.backend.dao.finance;

import java.util.List;

import com.pennant.backend.model.finance.FinanceEligibilityDetail;

public interface FinanceEligibilityDetailDAO {
	public void saveList(List<FinanceEligibilityDetail> eligibilityDetails);
	public void updateList(List<FinanceEligibilityDetail> eligibilityDetails);
	public List<FinanceEligibilityDetail> getFinElgDetailByFinRef(String finReference, String type);
	public int getFinElgDetailCount(FinanceEligibilityDetail financeEligibilityDetail);
}
