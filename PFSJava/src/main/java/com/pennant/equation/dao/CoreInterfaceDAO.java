package com.pennant.equation.dao;

import java.util.Date;
import java.util.List;

import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.customermasters.CustomerEmploymentDetail;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.customermasters.CustomerRating;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.rmtmasters.FinTypeAccounting;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rmtmasters.TransactionEntry;
import com.pennant.backend.model.systemmasters.SubSector;
import com.pennant.coreinterface.model.EquationAbuser;
import com.pennant.coreinterface.model.EquationAccountType;
import com.pennant.coreinterface.model.EquationBranch;
import com.pennant.coreinterface.model.EquationCountry;
import com.pennant.coreinterface.model.EquationCurrency;
import com.pennant.coreinterface.model.EquationCustStatusCode;
import com.pennant.coreinterface.model.EquationCustomerGroup;
import com.pennant.coreinterface.model.EquationCustomerRating;
import com.pennant.coreinterface.model.EquationCustomerType;
import com.pennant.coreinterface.model.EquationDepartment;
import com.pennant.coreinterface.model.EquationIdentityType;
import com.pennant.coreinterface.model.EquationIndustry;
import com.pennant.coreinterface.model.EquationInternalAccount;
import com.pennant.coreinterface.model.EquationMasterMissedDetail;
import com.pennant.coreinterface.model.EquationRelationshipOfficer;
import com.pennant.coreinterface.model.EquationTransactionCode;
import com.pennant.coreinterface.model.IncomeAccountTransaction;

public interface CoreInterfaceDAO {


	List<EquationCurrency> fetchCurrecnyDetails() ;
	List<EquationRelationshipOfficer> fetchRelationshipOfficerDetails(); 
	List<EquationCustomerType> fetchCustomerTypeDetails(); 
	List<EquationDepartment> fetchDepartmentDetails(); 
	List<EquationCustomerGroup> fetchCustomerGroupDetails();
	List<EquationAccountType> fetchAccountTypeDetails();
	List<EquationCustomerRating> fetchCustomerRatingDetails();
	List<EquationCountry> fetchCountryDetails();
	List<EquationCustStatusCode> fetchCustStatusCodeDetails();
	List<EquationIndustry> fetchIndustryDetails();
	List<EquationBranch> fetchBranchDetails();
	List<EquationInternalAccount> fetchInternalAccDetails();
	List<EquationTransactionCode> fetchTransactionCodeDetails();
	List<EquationIdentityType> fetchIdentityTypeDetails();
	List<String> fetchExistingCustomers(Date valuedate);
	List<String> fetchExistingOldCustomers();
	List<String> fetchBranchCodes();
	List<Long> fetchCustomerGroupCodes();
	List<String> fetchCountryCodes();
	List<String> fetchSalutationCodes();
	List<String> fetchRelationshipOfficerCodes();
	List<SubSector> fetchSubSectorCodes();
	List<String> fetchMaritalStatusCodes();
	List<String> fetchEmpStsCodes();
	List<String> fetchCurrencyCodes();
	List<String> fetchCustTypeCodes();
	List<String> fetchAddressTypes();
	List<String> fetchEMailTypes();
	List<Long> fetchCustomerIdDetails();
	List<String> fetchAccountTypes();
	void saveCurrecnyDetails(List<EquationCurrency> currencyList)  throws Exception;
	void saveRelationShipOfficerDetails(List<EquationRelationshipOfficer> relationshipOfficerList)  throws Exception;
	void saveCustomerTypeDetails(List<EquationCustomerType> customerTypes)  throws Exception;
	void saveDepartmentDetails(List<EquationDepartment> departments)  throws Exception;
	void saveCustomerGroupDetails(List<EquationCustomerGroup> customerGroups)  throws Exception;
	void saveAccountTypeDetails(List<EquationAccountType> accountTypes)  throws Exception;
	void saveAccountTypeNatureDetails(List<EquationAccountType> accountTypeNatures)  throws Exception;
	void saveCustomerRatingDetails(List<EquationCustomerRating> customerRatings)  throws Exception;
	void saveCountryDetails(List<EquationCountry> countryList)  throws Exception;
	void saveCustStatusCodeDetails(List<EquationCustStatusCode> custList)  throws Exception;
	void saveIndustryDetails(List<EquationIndustry> industryList)  throws Exception;
	void saveBranchDetails(List<EquationBranch> branchList)  throws Exception;
	void saveInternalAccDetails(List<EquationInternalAccount> intAccList)  throws Exception;
	void saveAbuserDetails(List<EquationAbuser> abuserList)  throws Exception;
	void saveMasterValueMissedDetail(EquationMasterMissedDetail masterMissedDetail);
	void saveMasterValueMissedDetails(List<EquationMasterMissedDetail> masterMissedDetails) ;
	void saveTransactionCodeDetails(List<EquationTransactionCode> transactionCodes)  throws Exception;
	void saveIdentityTypeDetails(List<EquationIdentityType> identityTypes)  throws Exception;
	void updateCurrecnyDetails(List<EquationCurrency> currencyList)  throws Exception;
	void updateRelationShipOfficerDetails(List<EquationRelationshipOfficer> relationshipOfficerList)  throws Exception;
	void updateCustomerTypeDetails(List<EquationCustomerType> customerTypes)  throws Exception;
	void updateDepartmentDetails(List<EquationDepartment> departments)  throws Exception;
	void updateCustomerGroupDetails(List<EquationCustomerGroup> customerGroups)  throws Exception;
	void updateAccountTypeDetails(List<EquationAccountType> accountTypes)  throws Exception;
	void updateAccountTypeNatureDetails(List<EquationAccountType> accountTypeNatures)  throws Exception;
	void updateCustomerRatingDetails(List<EquationCustomerRating> customerRatings)  throws Exception;
	void updateCountryDetails(List<EquationCountry> countryList)  throws Exception;
	void updateCustStatusCodeDetails(List<EquationCustStatusCode> custStsList)  throws Exception;
	void updateIndustryDetails(List<EquationIndustry> industryList)  throws Exception;
	void updateBranchDetails(List<EquationBranch> branchList)  throws Exception;
	void updateInternalAccDetails(List<EquationInternalAccount> intAccList)  throws Exception;
	void updateCustomerDetails(List<Customer> customerList)  throws Exception;
	void updateAddressDetails(List<CustomerAddres> addressList) throws Exception;
	void updatePhoneNumberDetails(List<CustomerPhoneNumber> phNumList)  throws Exception;
	void updateEMailDetails(List<CustomerEMail> emailList)  throws Exception;
	void updateEmploymentDetails(List<CustomerEmploymentDetail> empList)  throws Exception;
	void updateTransactionCodes(List<EquationTransactionCode> transactionCodes)  throws Exception;
	void updateIdentityTypes(List<EquationIdentityType> identityTypes)  throws Exception;
	void deleteAbuserDetails() throws Exception;
	
	//****************** Month End Downloads  *******************//
	List<FinanceType> fetchFinanceTypeDetails();
	List<FinTypeAccounting> fetchFinTypeAccountings(String event);
	List<TransactionEntry> fetchTransactionEntryDetails(long accountSetID);
	void updateFinProfitIncomeAccounts(List<FinanceProfitDetail> accounts);
	List<IncomeAccountTransaction> fetchIncomeAccountDetails() ;
	boolean checkIncomeTransactionsExist(IncomeAccountTransaction incomeAccountTransaction);
	void saveIncomeAccTransactions(List<IncomeAccountTransaction> incomeAccountTransactions);

	List<CustomerAddres> fetchExisitingCustomerAddress();
	List<CustomerPhoneNumber> fetchExisitingCustPhoneNumbers();
	List<CustomerEMail> fetchExisitingCustEmails();
	
	//****************** Single Customer Download  *******************//
	void saveCustomerAddresses(List<CustomerAddres> customerAddres) throws Exception;
	void saveCustomerPhoneNumbers(List<CustomerPhoneNumber> customerPhoneNumbers)  throws Exception;
	void saveCustomerEmails(List<CustomerEMail> customerEMails)  throws Exception;
	void saveRatingDetails(List<CustomerRating> customerRatings);
	void updateObjectDetails(String updateQuery,Object object); 
}
