package com.pennant.backend.dao.financemanagement;

import java.util.List;

import com.pennant.backend.model.finance.FinanceStepPolicyDetail;

public interface FinanceStepDetailDAO {
	public FinanceStepPolicyDetail getFinStepPolicy();
	public FinanceStepPolicyDetail getNewFinStepPolicy();
	public List<FinanceStepPolicyDetail> getFinStepDetailListByFinRef(final String finReference, String type, boolean isWIF); 
	public void initialize(FinanceStepPolicyDetail finStepPolicy);
	public void saveList(List<FinanceStepPolicyDetail> stepPolicyDetails, boolean isWIF, String tableType);
	public void deleteList(String finReference, boolean isWIF, String tableType);
}
