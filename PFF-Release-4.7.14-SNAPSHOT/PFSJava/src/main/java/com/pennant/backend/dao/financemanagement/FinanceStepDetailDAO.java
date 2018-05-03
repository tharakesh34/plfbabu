package com.pennant.backend.dao.financemanagement;

import java.util.List;

import com.pennant.backend.model.finance.FinanceStepPolicyDetail;

public interface FinanceStepDetailDAO {
	
	FinanceStepPolicyDetail getFinStepPolicy();
	FinanceStepPolicyDetail getNewFinStepPolicy();
	List<FinanceStepPolicyDetail> getFinStepDetailListByFinRef(final String finReference, String type, boolean isWIF);
	void saveList(List<FinanceStepPolicyDetail> stepPolicyDetails, boolean isWIF, String tableType);
	void deleteList(String finReference, boolean isWIF, String tableType);
}
