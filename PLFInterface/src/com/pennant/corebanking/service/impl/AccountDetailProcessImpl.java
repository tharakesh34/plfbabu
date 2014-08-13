package com.pennant.corebanking.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.pennant.corebanking.dao.impl.InterfaceDAOImpl;
import com.pennant.coreinterface.exception.AccountNotFoundException;
import com.pennant.coreinterface.exception.EquationInterfaceException;
import com.pennant.coreinterface.model.AccountBalance;
import com.pennant.coreinterface.model.CoreBankAccountDetail;
import com.pennant.coreinterface.service.AccountDetailProcess;

public class AccountDetailProcessImpl extends GenericProcess implements AccountDetailProcess{

	private static Logger logger = Logger.getLogger(AccountDetailProcessImpl.class);

	private InterfaceDAOImpl interfaceDAO;

	/**
	 * Method for Fetching List of account details depends on Parameter key
	 * fields
	 * 
	 * @param coreAcct
	 * @return
	 * @throws AccountNotFoundException
	 */
	@Override
	public List<CoreBankAccountDetail> fetchAccountDetails(CoreBankAccountDetail coreAcct) throws AccountNotFoundException {
		logger.debug("Entering");

		List<CoreBankAccountDetail> accountList = new ArrayList<CoreBankAccountDetail>();	
		try {
			
			accountList = getInterfaceDAO().fetchAccountDetails(coreAcct);			
			
		} catch (Exception e) {
			logger.error("Exception " + e);
			throw new AccountNotFoundException(e.getMessage());
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
	public List<CoreBankAccountDetail> fetchAccount(List<CoreBankAccountDetail> bankAccountDetails, String createNow) throws AccountNotFoundException {
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
			logger.error("Exception " + e);
			throw new AccountNotFoundException(e.getMessage());
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
	public CoreBankAccountDetail fetchAccountAvailableBal(CoreBankAccountDetail coreAcct) throws AccountNotFoundException {
		logger.debug("Entering");
		
		try {
			coreAcct = getInterfaceDAO().fetchAccountBalance(coreAcct.getAccountNumber());
		} catch (Exception e) { 
			logger.error("Exception " + e);
			throw new AccountNotFoundException(e.getMessage());
		}
		
		if (coreAcct == null){
			throw new AccountNotFoundException();
		}
		logger.debug("Leaving");
		return coreAcct;
	}
	
	@Override
	public List<CoreBankAccountDetail> fetchAccountsListAvailableBal(
			List<CoreBankAccountDetail> coreAcctList, boolean isCcyCheck)
			throws AccountNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int removeAccountHolds() throws EquationInterfaceException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<AccountBalance> addAccountHolds(
			List<AccountBalance> accountslIst)
			throws EquationInterfaceException {
		// TODO Auto-generated method stub
		return null;
	}


	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public InterfaceDAOImpl getInterfaceDAO() {
		return interfaceDAO;
	}
	public void setInterfaceDAO(InterfaceDAOImpl interfaceDAO) {
		this.interfaceDAO = interfaceDAO;
	}

}
