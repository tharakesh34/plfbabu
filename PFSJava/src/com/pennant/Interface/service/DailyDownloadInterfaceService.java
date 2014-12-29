package com.pennant.Interface.service;

import com.pennant.coreinterface.model.FinIncomeAccount;

public interface DailyDownloadInterfaceService {

	boolean processCurrencyDetails();
	boolean processRelationshipOfficerDetails();
	boolean processCustomerTypeDetails();
	boolean processDepartmentDetails();
	boolean processCustomerGroupDetails();
	boolean processAccountTypeDetails();
	boolean processCustomerRatingDetails();
	
	// ++++++++++++++++++ Month End Downloads  +++++++++++++++++++//
	
	void processIncomeAccountDetails(FinIncomeAccount finIncomeAccount);
	boolean processIncomeAccTransactions(FinIncomeAccount finIncomeAcc);
	
}
