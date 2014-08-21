package com.pennant.backend.dao.financemanagement;

import java.util.List;
import com.pennant.backend.model.finance.FinanceStepPolicyDetail;

public interface FinanceStepDetailDAO {
	public FinanceStepPolicyDetail getFinStepPolicy();
	public FinanceStepPolicyDetail getNewFinStepPolicy();
	public List<FinanceStepPolicyDetail> getFinStepDetailListByFinRef(final String finReference, String type); 
	public void update(FinanceStepPolicyDetail finStepPolicy, String type);
	public String save(FinanceStepPolicyDetail finStepPolicy, String type);
	public void delete(FinanceStepPolicyDetail finStepPolicy, String type);
	public void initialize(FinanceStepPolicyDetail finStepPolicy);
	public FinanceStepPolicyDetail getFinStepPolicy(String finReference, int stepNumber, String type);
}
