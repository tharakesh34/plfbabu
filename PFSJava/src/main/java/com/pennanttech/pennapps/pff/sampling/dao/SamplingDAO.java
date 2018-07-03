package com.pennanttech.pennapps.pff.sampling.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerExtLiability;
import com.pennant.backend.model.customermasters.CustomerIncome;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennanttech.pennapps.pff.sampling.model.Sampling;
import com.pennanttech.pff.core.TableType;

public interface SamplingDAO {

	void update(Sampling sampling, TableType tableType);

	void delete(Sampling sampling, TableType tableType);

	Sampling getSampling(long id, String type);

	List<CustomerIncome> getIncomes(long samplingId);

	List<CustomerExtLiability> getObligations(long samplingId);

	long save(Sampling sampling, TableType tableType);

	List<Customer> getCustomers(String keyreference, String type);

	List<CollateralSetup> getCollaterals(String keyreference);

	Sampling getSampling(String keyReference, String type);

	Map<String, String> getEligibilityRules();

	List<CollateralSetup> getCollateralsBySamplingId(Long samplingId);

	ExtendedFieldRender getCollateralExtendedFields(String collReference, String tableName, String type);

	Map<String, Object> getExtendedField(long samplingId, String reference, String tableName, String type);

	long getLinkId(Sampling sampling, String collRef, String inputSource);

	boolean isExist(String finReference,String type);

	void setLiabilitySnapLinkId(CustomerExtLiability customerIncome);

	long getIncomeLinkId(long id, long custId);

	long getLiabilityLinkId(long id, long custId);

	long getCollateralLinkId(long id, String CollateralReference);
	
	long getCollateralLinkId(String collateralreference,long samplingId,String type);

	BigDecimal getLoanEligibility(String finReference, String eligibilityRule);
	
	Map<String, Object> getRemarks(long samplingId);
	
	String getCollateralRef(Sampling sampling, String collRef, String inputSource);

	long getIncomeLinkIdByCustId(long custId, long samplinId);

	long getIncomeSnapLinkId(long samplingId, long custId);

	long getLinkId(long samplingId, String tableName);
	
	long getCollateralSnapLinkId(long samplingId, String collateralRef);

}
