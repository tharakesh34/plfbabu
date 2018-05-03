package com.pennant.backend.dao.limits;

import java.util.List;

import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.limits.ClosedFacilityDetail;
import com.pennant.backend.model.limits.FinanceLimitProcess;
import com.pennant.backend.model.limits.LimitDetail;

public interface LimitInterfaceDAO {

	void saveFinLimitUtil(FinanceLimitProcess finLimitProcess);

	void saveCustomerLimitDetails(LimitDetail limitDetail);

	FinanceLimitProcess getLimitUtilDetails(FinanceLimitProcess financeLimitProcess);

	LimitDetail getCustomerLimitDetails(String limitRef);

	void updateCustomerLimitDetails(LimitDetail limitDetail);

	boolean saveClosedFacilityDetails(List<ClosedFacilityDetail> proClFacilityList);

	FinanceMain getFinanceMainByRef(String dealID, String string, boolean type);
}
