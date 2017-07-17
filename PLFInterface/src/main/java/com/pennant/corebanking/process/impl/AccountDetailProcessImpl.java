package com.pennant.corebanking.process.impl;

import java.util.ArrayList;
import java.util.List;

import javax.security.auth.login.AccountNotFoundException;

import org.apache.log4j.Logger;

import com.pennant.corebanking.dao.InterfaceDAO;
import com.pennant.coreinterface.model.CoreBankAccountDetail;
import com.pennant.coreinterface.process.AccountDetailProcess;
import com.pennanttech.pennapps.core.InterfaceException;

public class AccountDetailProcessImpl extends GenericProcess implements AccountDetailProcess{

	private static Logger logger = Logger.getLogger(AccountDetailProcessImpl.class);

	private InterfaceDAO interfaceDAO;

	public AccountDetailProcessImpl() {
		super();
	}
	
	/**
	 * Method for Fetching List of account details depends on Parameter key
	 * fields
	 * 
	 * @param coreAcct
	 * @return
	 * @throws AccountNotFoundException
	 */
	@Override
	public List<CoreBankAccountDetail> fetchCustomerAccounts(CoreBankAccountDetail coreAcct) throws InterfaceException {
		logger.debug("Entering");

		List<CoreBankAccountDetail> accountList = null;	
		try {
			accountList = getInterfaceDAO().fetchAccountDetails(coreAcct);			
		} catch (Exception e) {
			logger.error("Exception: ", e);
			throw new InterfaceException("9999", e.getMessage());
		}

		logger.debug("Leaving");
		return accountList;
	}

	/**
	 * Method for Fetching account detail Numbers depends on Parameter key
	 * fields
	 * 
	 * @param coreAcct
	 * @return
	 * @throws AccountNotFoundException
	 */
	@Override
	public List<CoreBankAccountDetail> fetchAccount(List<CoreBankAccountDetail> bankAccountDetails, String createNow) throws InterfaceException {
		logger.debug("Entering");
	
		List<CoreBankAccountDetail> list = new ArrayList<CoreBankAccountDetail>();

		try {
			//Prepare the id's
			bankAccountDetails = getInterfaceDAO().updateAccountDetailsIds(bankAccountDetails);
			getInterfaceDAO().saveAccountDetails(bankAccountDetails);
			int reqRefId = bankAccountDetails.get(0).getReqRefId();
			getInterfaceDAO().executeAccountForFin(reqRefId, "N");			
			list = getInterfaceDAO().fetchAccountForFin(bankAccountDetails);
			
		} catch (Exception e) {
			logger.error("Exception: ", e);
			throw new InterfaceException("9999",e.getMessage());
		}

		logger.debug("Leaving");
		return list;
	}

	/**
	 * Method for Fetch Account Balance Amount
	 * 
	 * @param coreAcct
	 * @param newConnection
	 * @return
	 * @throws AccountNotFoundException
	 */
	@Override
	public CoreBankAccountDetail fetchAccountAvailableBal(CoreBankAccountDetail coreAcct) throws InterfaceException {
		logger.debug("Entering");
		
		try {
			List<CoreBankAccountDetail> acList = getInterfaceDAO().fetchAccountBalance(coreAcct.getAccountNumber());
		//	coreAcct = acList.get(0); changed to the below code because if the list is null  when we say get(0) throws an error so
			if(acList!=null && !acList.isEmpty()){
			acList.get(0).setErrorCode("0000");
			coreAcct = acList.get(0);
			}else{
				coreAcct = null;
			}
			
		} catch (Exception e) { 
			logger.error("Exception: ", e);
			throw new InterfaceException("9999",e.getMessage());
		}
		
		if (coreAcct == null){
			throw new InterfaceException("9999","Account not found.");
		}
		logger.debug("Leaving");
		return coreAcct;
	}
	
	@Override
	public List<CoreBankAccountDetail> fetchAccountsListAvailableBal(
			List<CoreBankAccountDetail> coreAcctList, boolean isCcyCheck)
			throws InterfaceException {
		logger.debug("Entering");
		
		List<String> accNumList = new ArrayList<String>();
		for (int i = 0; i < coreAcctList.size(); i++) {
			accNumList.add(coreAcctList.get(i).getAccountNumber());
		}
		
		List<CoreBankAccountDetail>  list = getInterfaceDAO().fetchAccountBalance(accNumList);
		
		// To be changed with Interface changes for showing specific error message.
		if(list == null){
			list = new ArrayList<CoreBankAccountDetail>();
		}
		
		logger.debug("Leaving");
		return list;
	}

	

	/**
	 * Method for fetch Account details from core bank
	 * 
	 */
	@Override
	public List<CoreBankAccountDetail> fetchAccountDetails(CoreBankAccountDetail accountDetail) throws InterfaceException {
		logger.debug("Entering");
		
		List<CoreBankAccountDetail>  list = getInterfaceDAO().fetchAccountBalance(accountDetail.getAccountNumber());
		
		// To be changed with Interface changes for showing specific error message.
		if(list == null){
			list = new ArrayList<CoreBankAccountDetail>();
		}
		logger.debug("Leaving");
		return list;
	}
	
	
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public InterfaceDAO getInterfaceDAO() {
		return interfaceDAO;
	}
	public void setInterfaceDAO(InterfaceDAO interfaceDAO) {
		this.interfaceDAO = interfaceDAO;
	}
}
