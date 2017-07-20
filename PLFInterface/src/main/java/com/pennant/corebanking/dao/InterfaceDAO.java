package com.pennant.corebanking.dao;

import java.util.List;
import java.util.Map;

import com.pennant.coreinterface.model.AccountPostingTemp;
import com.pennant.coreinterface.model.CoreBankAccountDetail;
import com.pennant.coreinterface.model.CoreBankAccountPosting;
import com.pennant.coreinterface.model.CoreCustomerDedup;
import com.pennant.coreinterface.model.CoreDocumentDetails;
import com.pennant.coreinterface.model.customer.InterfaceCustomerDetail;
import com.pennant.coreinterface.model.limit.CustomerLimitDetail;
import com.pennanttech.pennapps.core.InterfaceException;

public interface InterfaceDAO {

	Map<String, String> getCalendarWorkingDays();

	List<CoreBankAccountPosting> prepareAccPostinIds(List<CoreBankAccountPosting> list);

	List<CoreBankAccountDetail> updateAccountDetailsIds(List<CoreBankAccountDetail> list);

	List<CoreBankAccountDetail> fetchAccountForFin(List<CoreBankAccountDetail> list);
	
	List<CoreBankAccountPosting> fetchAccountPostingForFin(List<CoreBankAccountPosting> list);

	List<AccountPostingTemp> executeAccPosting(List<AccountPostingTemp> accPostingTempList) throws Exception;

	void saveAccountPostings(List<AccountPostingTemp> accountPostings);

	void saveAccountDetails(List<CoreBankAccountDetail> accountDetail);

	CoreBankAccountDetail fetchAccount(CoreBankAccountDetail accountDetail);

	List<CoreBankAccountDetail> fetchAccountBalance(String accountNumber);
	
	List<CoreBankAccountDetail> fetchAccountBalance(List<String> accountNumberList);

	List<CoreBankAccountPosting> validateAccount(List<CoreBankAccountPosting> accountPostings);

	List<CoreBankAccountDetail> fetchAccountDetails(CoreBankAccountDetail coreAcct);

	String executeAccountForFin(int reqRefId, String createNow);

	InterfaceCustomerDetail getCustDetails(String custCIF, String custLoc) throws InterfaceException;
	
	List<CoreCustomerDedup> fetchCustomerDedupDetails(CoreCustomerDedup customerDedup);
	
	CustomerLimitDetail getLimitDetails(String limitRef,String branchCode);
	
	List<CoreDocumentDetails> getDocumentDetailsByRef(String ref);

}
