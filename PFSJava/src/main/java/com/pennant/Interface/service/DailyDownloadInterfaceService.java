package com.pennant.Interface.service;

import java.util.Date;

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

	boolean processIncomeAccTransactions(Date prvMnthStartDate);
}
