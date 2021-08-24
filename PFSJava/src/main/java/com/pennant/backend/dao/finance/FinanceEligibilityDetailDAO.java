package com.pennant.backend.dao.finance;

import java.util.List;

import com.pennant.backend.model.finance.FinanceEligibilityDetail;

public interface FinanceEligibilityDetailDAO {

	void saveList(List<FinanceEligibilityDetail> eligibilityDetails, String type);

	void updateList(List<FinanceEligibilityDetail> eligibilityDetails);

	List<FinanceEligibilityDetail> getFinElgDetailByFinRef(long finID, String type);

	int getFinElgDetailCount(FinanceEligibilityDetail financeEligibilityDetail);

	void deleteByFinRef(long finID);
}
