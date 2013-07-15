package com.pennant.coredb.process;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.pennant.coredb.dao.CoreDBDAO;
import com.pennant.coreinterface.exception.AccountNotFoundException;
import com.pennant.coreinterface.vo.CoreBankAccountDetail;

/**
 * @author s039
 * @param <SqlParameterSource>
 * 
 */
public class AccountProcess {

	private static Logger logger = Logger.getLogger(AccountProcess.class);

	// Spring Named JDBC Template
	private CoreDBDAO coreDBDao;

	/**
	 * Method for Fecthing List of account details depends on Parameter key
	 * fields
	 * 
	 * @param coreAcct
	 * @return
	 * @throws AccountNotFoundException
	 */
	public List<CoreBankAccountDetail> fetchAccountDetails(CoreBankAccountDetail coreAcct) throws AccountNotFoundException {
		logger.debug("Entering");

		List<CoreBankAccountDetail> accountList = new ArrayList<CoreBankAccountDetail>();	
		try {
			
			accountList = getCoreDBDao().fetchAccountDetails(coreAcct);			
			
		} catch (Exception e) {
			logger.error("Exception " + e);
			throw new AccountNotFoundException(e.getMessage());
		}

		logger.debug("Leaving");
		return accountList;
	}

	/**
	 * Method for Fecthing account detail Numbers depends on Parameter key
	 * fields
	 * 
	 * @param coreAcct
	 * @return
	 * @throws AccountNotFoundException
	 */
	public List<CoreBankAccountDetail> fetchAccount(List<CoreBankAccountDetail> bankAccountDetails, String createNow, boolean newConnection) throws AccountNotFoundException {
		logger.debug("Entering");
	
		List<CoreBankAccountDetail> list = new ArrayList<CoreBankAccountDetail>();

		try {
			//Prepare the id's
			bankAccountDetails = this.coreDBDao.updateAccountDetailsIds(bankAccountDetails);
			this.coreDBDao.saveAccountDetails(bankAccountDetails);
			int reqRefId = bankAccountDetails.get(0).getReqRefId();
			this.coreDBDao.executeAccountForFin(reqRefId, "N");			
			list = this.coreDBDao.fetchAccountForFin(bankAccountDetails);
			
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
	public CoreBankAccountDetail fetchAccountAvailableBal(CoreBankAccountDetail coreAcct, boolean newConnection) throws AccountNotFoundException {
		logger.debug("Entering");
		
		try {
			coreAcct = this.coreDBDao.fetchAccountBalance(coreAcct.getAccountNumber());
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

	

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public CoreDBDAO getCoreDBDao() {
		return coreDBDao;
	}

	public void setCoreDBDao(CoreDBDAO coreDBDao) {
		this.coreDBDao = coreDBDao;
	}

}
