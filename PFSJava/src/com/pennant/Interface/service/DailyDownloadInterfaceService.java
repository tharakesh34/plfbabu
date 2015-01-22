package com.pennant.Interface.service;

import java.util.Date;
import java.util.List;

import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rmtmasters.TransactionEntry;

public interface DailyDownloadInterfaceService {

	boolean processCurrencyDetails();
	boolean processRelationshipOfficerDetails();
	boolean processCustomerTypeDetails();
	boolean processDepartmentDetails();
	boolean processCustomerGroupDetails();
	boolean processAccountTypeDetails();
	boolean processCustomerRatingDetails(Date valuedate);
	boolean processAbuserDetails();
	boolean processCustomerDetails(Date valuedate);
	boolean processCountryDetails();
	boolean processCustStatusCodeDetails();
	boolean processIndustryDetails();
	boolean processBranchDetails();
	boolean processInternalAccDetails(Date valuedate);
	boolean processTransactionCodeDetails();
	boolean processIdentityTypeDetails();

	// ++++++++++++++++++ Month End Downloads  +++++++++++++++++++//
	List<FinanceType> fetchFinanceTypeDetails();
	List<TransactionEntry> fetchTransactionEntryDetails(long accountSetID);
	boolean processIncomeAccTransactions(Date prvMnthStartDate);
	void updateFinProfitIncomeAccounts(List<FinanceProfitDetail> accounts);
	
}
