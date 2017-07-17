package com.pennant.mq.process.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jaxen.JaxenException;

import com.pennant.coreinterface.model.CoreBankAccountDetail;
import com.pennant.coreinterface.process.AccountDetailProcess;
import com.pennant.mq.processutil.FetchAccountDetailProcess;
import com.pennant.mq.processutil.FetchAllAccountsProcess;
import com.pennant.mq.util.InterfaceMasterConfigUtil;
import com.pennanttech.pennapps.core.InterfaceException;

public class AccountDetailServiceImpl implements AccountDetailProcess {

	private static final Logger logger = Logger.getLogger(AccountDetailServiceImpl.class);

	private FetchAllAccountsProcess fetchAllAccountsProcess;
	private FetchAccountDetailProcess fetchAccountDetailProcess;

	public AccountDetailServiceImpl() {

	}

	/**
	 * Get Customer Account Details from Interface<br>
	 * 
	 * getCustomerAccounts method do the following steps.<br>
	 * 1) Send getAccounts Request to MQ<br>
	 * 2) Receive Response from MQ
	 * 
	 * @return CoreBankAccountDetail
	 */
	@Override
	public List<CoreBankAccountDetail> fetchCustomerAccounts(CoreBankAccountDetail accountDetail)
			throws InterfaceException {
		logger.debug("Entering");

		List<CoreBankAccountDetail> accDetail = null;
		try {
			accDetail = getFetchAllAccountsProcess().fetchCustomerAccounts(accountDetail, InterfaceMasterConfigUtil.FETCH_ACCOUNTS);
		} catch (JaxenException e) {
			logger.warn("Exception: ", e);
		}

		logger.debug("Leaving");

		return accDetail;
	}

	/**
	 * Get Account Details from Interface based on given Account number<br>
	 * 
	 * fetchAccountDetails method do the following steps.<br>
	 * 1) Send getAccounts Request to MQ<br>
	 * 2) Receive Response from MQ
	 * 
	 * @return CoreBankAccountDetail
	 */
	@Override
	public List<CoreBankAccountDetail> fetchAccountDetails(CoreBankAccountDetail accountDetail) throws InterfaceException {
		logger.debug("Entering");

		List<CoreBankAccountDetail> accDetailList = null;
		try {
			accDetailList = getFetchAccountDetailProcess().fetchAccountDetails(accountDetail, InterfaceMasterConfigUtil.FETCH_ACCDETAILS);
		} catch (JaxenException e) {
			logger.warn("Exception: ", e);
		}

		logger.debug("Leaving");

		return accDetailList;
	}

	/**
	 * Method to fetch Account Number for Newly generated Internal Account Numbers
	 */
	@Override
	public List<CoreBankAccountDetail> fetchAccount(List<CoreBankAccountDetail> bankAccountDetails, String createNow)
			throws InterfaceException {

		List<CoreBankAccountDetail> accountList = new ArrayList<CoreBankAccountDetail>();
		int count = 0;
		for(CoreBankAccountDetail bankAccount : bankAccountDetails) {
			bankAccount.setAccountNumber(StringUtils.leftPad("101020025"+ ++count, 13, "1"));
			accountList.add(bankAccount);
		}

		return accountList;
	}

	@Override
	public CoreBankAccountDetail fetchAccountAvailableBal(CoreBankAccountDetail coreAcct) throws InterfaceException {
		logger.debug("Entering");

		List<CoreBankAccountDetail> coreAcctList = new ArrayList<CoreBankAccountDetail>();
		coreAcctList.add(coreAcct);
		coreAcctList = fetchAccountsListAvailableBal(coreAcctList, false);

		logger.debug("Leaving");
		if(coreAcctList != null && !coreAcctList.isEmpty()){
			return coreAcctList.get(0);
		}else{
			return null;
		}
	}

	@Override
	public List<CoreBankAccountDetail> fetchAccountsListAvailableBal(List<CoreBankAccountDetail> coreAcctList,
			boolean isCcyCheck) throws InterfaceException {
		logger.debug("Entering");

		List<CoreBankAccountDetail> accDetailList = null;
		try {
			for(CoreBankAccountDetail bankAccountDetail : coreAcctList) {
				accDetailList = getFetchAccountDetailProcess().fetchAccountDetails(bankAccountDetail, 
						InterfaceMasterConfigUtil.FETCH_ACCDETAILS);
			}
		} catch (JaxenException e) {
			logger.warn("Exception: ", e);
		}

		logger.debug("Leaving");

		return accDetailList;
	}

	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public FetchAllAccountsProcess getFetchAllAccountsProcess() {
		return fetchAllAccountsProcess;
	}
	public void setFetchAllAccountsProcess(FetchAllAccountsProcess fetchAllAccountsProcess) {
		this.fetchAllAccountsProcess = fetchAllAccountsProcess;
	}
	
	public FetchAccountDetailProcess getFetchAccountDetailProcess() {
		return fetchAccountDetailProcess;
	}
	public void setFetchAccountDetailProcess(FetchAccountDetailProcess fetchAccountDetailProcess) {
		this.fetchAccountDetailProcess = fetchAccountDetailProcess;
	}
}
