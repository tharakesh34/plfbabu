package com.pennanttech.pennapps.pff.sampling.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerExtLiability;
import com.pennant.backend.model.customermasters.CustomerIncome;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennanttech.pennapps.pff.sampling.model.Sampling;
import com.pennanttech.pennapps.pff.sampling.model.SamplingCollateral;
import com.pennanttech.pff.core.TableType;

public interface SamplingDAO {

	void update(Sampling sampling, TableType tableType);

	void delete(Sampling sampling, TableType tableType);

	Sampling getSampling(long id, String type);

	List<CustomerIncome> getIncomes(long samplingId);

	List<CustomerExtLiability> getObligations(long samplingId);

	long save(Sampling sampling, TableType tableType);

	List<Customer> getCustomers(String keyreference, String type);

	List<SamplingCollateral> getCollaterals(String keyreference, String collateralType);

	Sampling getSampling(String keyReference, String type);

	Map<String, String> getEligibilityRules();

	List<SamplingCollateral> getCollateralTypesBySamplingId(Long samplingId);
	
	List<SamplingCollateral> getCollateralsBySamplingId(List<String> linkIds, String collateralType);

	ExtendedFieldRender getCollateralExtendedFields(String collReference, String tableName, String type);

	Map<String, Object> getExtendedField(String linkId, int seqNo, String tableName, String type);

	long getLinkId(Sampling sampling, String collRef, String inputSource);

	boolean isExist(String finReference,String type);

	void setLiabilitySnapLinkId(CustomerExtLiability customerIncome);

	long getIncomeLinkId(long id, long custId);

	long getLiabilityLinkId(long id, long custId);

	long getCollateralLinkId(long id, String CollateralReference);
	
	long getCollateralLinkId(String collateralreference,long samplingId,String type);

	BigDecimal getLoanEligibility(String finReference, String eligibilityRule);
	
	Map<String, Object> getRemarks(long samplingId);
	
	String getCollateralRef(Sampling sampling, String collRef);

	long getIncomeLinkIdByCustId(long custId, long samplinId);

	long getIncomeSnapLinkId(long samplingId, long custId);

	long getLinkId(long samplingId, String tableName);
	
	long getCollateralSnapLinkId(long samplingId, String collateralRef);
	
	void saveIncomes(long samplingId);

	void saveLiabilities(long samplingId);

	void saveCollateral(long samplingId, String collateralType);

	void updateLiabilities(Sampling sampling);

	void updateIncomes(Sampling sampling);

	void updateCollaterals(Sampling sampling, String collateralType);

	List<String> getCollateralTypes(String finReference);

	List<String> getCollateralLinkIds(long samplingId);

}
