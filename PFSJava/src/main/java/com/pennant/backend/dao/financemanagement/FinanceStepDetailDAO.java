package com.pennant.backend.dao.financemanagement;

import java.util.List;

import com.pennant.backend.model.finance.FinanceStepPolicyDetail;

public interface FinanceStepDetailDAO {

	FinanceStepPolicyDetail getFinStepPolicy();

	FinanceStepPolicyDetail getNewFinStepPolicy();

	List<FinanceStepPolicyDetail> getFinStepDetailListByFinRef(long finID, String type, boolean isWIF);

	void saveList(List<FinanceStepPolicyDetail> stepPolicyDetails, boolean isWIF, String tableType);

	void deleteList(long finID, boolean isWIF, String tableType);

	List<FinanceStepPolicyDetail> getStepDetailsForLMSEvent(long finID);

}
