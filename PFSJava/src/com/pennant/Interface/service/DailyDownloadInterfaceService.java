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
	// ++++++++++++++++++ Month End Downloads  +++++++++++++++++++//
	
	public List<FinanceType> fetchFinanceTypeDetails();
	
	public List<TransactionEntry> fetchTransactionEntryDetails(long accountSetID);
	
	public boolean processIncomeAccTransactions(Date prvMnthStartDate);

	public void updateFinProfitIncomeAccounts(List<FinanceProfitDetail> accounts);
	
	// ++++++++++++++++++ Single Customer Download  +++++++++++++++++++//
	public void saveCustomerDetails(CustomerDetails customerDetails) throws Exception;
	
	public void updateObjectDetails(String updateQuery,Object object);
	
	public void saveMasterValueMissedDetails(List<EquationMasterMissedDetail> masterMissedDetails);
	
    public List<String> fetchBranchCodes();
    
	public List<Long> fetchCustomerGroupCodes();
	
	public List<String> fetchCountryCodes();
	
	public List<String> fetchSalutationCodes();
	
	public List<String> fetchRelationshipOfficerCodes();
	
	public List<String> fetchMaritalStatusCodes();
	
	public List<SubSector> fetchSubSectorCodes();
	
	public List<String> fetchEmpStsCodes();
	
	public List<String> fetchCurrencyCodes();
	
	public List<String> fetchCustTypeCodes();
}