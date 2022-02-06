package com.pennant.backend.dao.customermasters;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import com.pennant.backend.model.customermasters.CustomerExtLiability;
import com.pennant.backend.model.customermasters.ExtLiabilityPaymentdetails;

public interface CustomerExtLiabilityDAO {

	void setLinkId(CustomerExtLiability liability);

	CustomerExtLiability getLiability(CustomerExtLiability liability, String string, String inputSource);

	boolean isBankExists(String loanBank);

	boolean isFinTypeExists(String finType);

	boolean isFinStatuExists(String finStatus);

	int getCustomerExtLiabilityByBank(String code, String string);

	int getVersion(long linkId, int liabilitySeq);

	BigDecimal getExternalLiabilitySum(long custId);

	BigDecimal getSumAmtCustomerExtLiabilityById(Set<Long> custId);

	List<CustomerExtLiability> getLiabilityByFinReference(String finReference);

	List<CustomerExtLiability> getLiabilityBySamplingId(long samplingLinkId);

	long getLinkId(long custId);

	void delete(List<ExtLiabilityPaymentdetails> extLiabilitiesPaymentdetails, String type);

	void save(List<ExtLiabilityPaymentdetails> installmentDetails, String type);

	List<ExtLiabilityPaymentdetails> getExtLiabilitySubDetailById(long custId, String type);

	void update(ExtLiabilityPaymentdetails installmentDetails, String type);

	int getExtLiabilityVersion(long linkId, int liabilitySeq);

	BigDecimal getSumAmtCustomerInternalExtLiabilityById(Set<Long> custids);

	BigDecimal getSumCredtAmtCustomerBankInfoById(Set<Long> custId);

	void delete(long liabilityId, String type);

	boolean getExtendedComboData(String sql, String code);
}
