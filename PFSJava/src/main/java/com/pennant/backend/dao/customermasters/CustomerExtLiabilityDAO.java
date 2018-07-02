package com.pennant.backend.dao.customermasters;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import com.pennant.backend.model.customermasters.CustomerExtLiability;

public interface CustomerExtLiabilityDAO {

	void setLinkId(CustomerExtLiability liability);

	CustomerExtLiability getLiability(CustomerExtLiability liability, String string,String inputSource);

	boolean isBankExists(String loanBank);

	boolean isFinTypeExists(String finType);

	boolean isFinStatuExists(String finStatus);

	int getCustomerExtLiabilityByBank(String code, String string);

	int getVersion(long custId, int liabilitySeq);

	BigDecimal getExternalLiabilitySum(long custId);
	
	BigDecimal getSumAmtCustomerExtLiabilityById(Set<Long> custId);

	List<CustomerExtLiability> getLiabilityByFinReference(String finReference);

	List<CustomerExtLiability> getLiabilityBySamplingId(long samplingLinkId);
}
