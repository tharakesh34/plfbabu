package com.pennant.Interface.service;

import java.util.Date;
import java.util.List;

import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rmtmasters.TransactionEntry;
import com.pennant.backend.model.systemmasters.SubSector;
import com.pennant.coreinterface.model.EquationMasterMissedDetail;

public interface DailyDownloadInterfaceService {
	boolean processCurrencyDetails();

	boolean processRelationshipOfficerDetails();

	boolean processCustomerTypeDetails();

	boolean processDepartmentDetails();

	boolean processCustomerGroupDetails();

	boolean processAccountTypeDetails();

	boolean processCustomerRatingDetails();

	boolean processAbuserDetails();

	boolean processCustomerDetails();

	boolean processCountryDetails();

	boolean processCustStatusCodeDetails();

	boolean processIndustryDetails();

	boolean processBranchDetails();

	boolean processInternalAccDetails();

	boolean processTransactionCodeDetails();

	boolean processIdentityTypeDetails();

	// ****************** Month End Downloads *******************//
	List<FinanceType> fetchFinanceTypeDetails();

	List<TransactionEntry> fetchTransactionEntryDetails(long accountSetID);

	boolean processIncomeAccTransactions(Date prvMnthStartDate);

	void updateFinProfitIncomeAccounts(List<FinanceProfitDetail> accounts);

	// ****************** Single Customer Download *******************//
	void saveCustomerDetails(CustomerDetails customerDetails) throws Exception;

	void updateObjectDetails(String updateQuery, Object object);

	void saveMasterValueMissedDetails(List<EquationMasterMissedDetail> masterMissedDetails);

	List<String> fetchBranchCodes();

	List<Long> fetchCustomerGroupCodes();

	List<String> fetchCountryCodes();

	List<String> fetchSalutationCodes();

	List<String> fetchRelationshipOfficerCodes();

	List<String> fetchMaritalStatusCodes();

	List<SubSector> fetchSubSectorCodes();

	List<String> fetchEmpStsCodes();

	List<String> fetchCurrencyCodes();

	List<String> fetchCustTypeCodes();
}
