package com.pennant.corebanking.dao;

import java.util.List;
import java.util.Map;

import com.pennant.coreinterface.exception.CustomerNotFoundException;
import com.pennant.coreinterface.model.AccountPostingTemp;
import com.pennant.coreinterface.model.CoreBankAccountDetail;
import com.pennant.coreinterface.model.CoreBankAccountPosting;
import com.pennant.coreinterface.model.CoreBankingCustomer;
import com.pennant.coreinterface.model.CorePoliceCase;
import com.pennant.coreinterface.model.CustomerInterfaceData;

public interface InterfaceDAO {

	Map<String, String> getCalendarWorkingDays();

	CoreBankingCustomer fetchCustomerDetails(CoreBankingCustomer customer) throws CustomerNotFoundException;

	List<CoreBankAccountPosting> prepareAccPostinIds(List<CoreBankAccountPosting> list);

	List<CoreBankAccountDetail> updateAccountDetailsIds(List<CoreBankAccountDetail> list);

	List<CoreBankAccountDetail> fetchAccountForFin(List<CoreBankAccountDetail> list);
	
	List<CoreBankAccountPosting> fetchAccountPostingForFin(List<CoreBankAccountPosting> list);

	List<AccountPostingTemp> executeAccPosting(List<AccountPostingTemp> accPostingTempList) throws Exception;

	void saveAccountPostings(List<AccountPostingTemp> accountPostings);

	void saveAccountDetails(List<CoreBankAccountDetail> accountDetail);

	CoreBankAccountDetail fetchAccount(CoreBankAccountDetail accountDetail);

	List<CoreBankAccountDetail> fetchAccountBalance(List<String> accountNumberList);

	List<CoreBankAccountPosting> validateAccount(List<CoreBankAccountPosting> accountPostings);

	List<CoreBankAccountDetail> fetchAccountDetails(CoreBankAccountDetail coreAcct);

	String executeAccountForFin(int reqRefId, String createNow);

	CustomerInterfaceData getCustDetails(String custCIF, String custLoc);
	
	List<CorePoliceCase> fetchPoliceCustInformation(CorePoliceCase corePoliceCase,String sqlQuery);

}
