package com.pennant.Interface.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.Interface.model.IAccounts;
import com.pennant.Interface.service.AccountInterfaceService;
import com.pennant.coreinterface.exception.AccountNotFoundException;
import com.pennant.coreinterface.vo.CoreBankAccountDetail;
import com.pennant.equation.process.AccountProcess;

public class AccountInterfaceServiceEquationImpl implements AccountInterfaceService{
	private static Logger logger = Logger.getLogger(AccountInterfaceServiceEquationImpl.class);

	protected AccountProcess accountProcess;

	/**
	 * Method for Fetch Account detail depends on Parameter key fields
	 * @param coreAcct
	 * @return
	 * @throws AccountNotFoundException
	 */
	public List<IAccounts> fetchExistAccount(List<IAccounts> accountDetails,
			String createNow ,boolean newConnection) throws AccountNotFoundException {
		logger.debug("Entering");
				
		IAccounts iAccount = null;
		List<CoreBankAccountDetail> coreBankAccountDetails = new ArrayList<CoreBankAccountDetail>(accountDetails.size());
		CoreBankAccountDetail coreBankAccount = null;
		for (int i = 0; i < accountDetails.size(); i++) {
			iAccount = accountDetails.get(i);
			coreBankAccount = new CoreBankAccountDetail();
			coreBankAccount.setCustCIF(iAccount.getAcCustCIF());
			coreBankAccount.setAcBranch(iAccount.getAcBranch());
			coreBankAccount.setTransOrder(iAccount.getTransOrder());
			coreBankAccount.setAcCcy(iAccount.getAcCcy());
			coreBankAccount.setAcType(iAccount.getAcType());
			coreBankAccount.setAccountNumber(iAccount.getAccountId());
			coreBankAccount.setCreateNew(iAccount.getFlagCreateNew());
			coreBankAccount.setInternalAc(iAccount.getInternalAc());
			coreBankAccount.setCreateIfNF(iAccount.getFlagCreateIfNF());
			coreBankAccountDetails.add(coreBankAccount);
		}
		
		//Connecting to CoreBanking Interface
		coreBankAccountDetails = accountProcess.fetchAccount(coreBankAccountDetails,createNow,newConnection);
		
		//Fill the Account data using Core Banking Object
		List<IAccounts> accountResList = new ArrayList<IAccounts>(coreBankAccountDetails.size());
		for (int i = 0; i < coreBankAccountDetails.size(); i++) {
			iAccount = new IAccounts();
			CoreBankAccountDetail detail = coreBankAccountDetails.get(i);

			iAccount.setAccountId(detail.getAccountNumber());			
			iAccount.setAcCustCIF(detail.getCustCIF()); 			
			iAccount.setAcCcy(detail.getAcCcy()); 			
			iAccount.setAcType(detail.getAcType()); 		
			iAccount.setTransOrder(detail.getTransOrder());
			iAccount.setAcBranch(detail.getAcBranch()); 		
			iAccount.setFlagCreateNew(detail.getCreateNew().equals("Y")?true:false); 		
			iAccount.setFlagCreateIfNF(detail.getCreateIfNF().equals("Y")?true:false); 	
			iAccount.setInternalAc(StringUtils.trimToEmpty(detail.getInternalAc()).equals("Y")?true:false); 	
			iAccount.setFlagPostStatus(detail.getOpenStatus()); 		
			iAccount.setErrorCode(detail.getErrorCode()); 	
			iAccount.setErrorMsg(detail.getErrorMessage()); 	
			accountResList.add(iAccount);
		}
		logger.debug("Leaving");
		return accountResList;
	}
	
	/**
	 * Method for Fetch Account details list depends on Parameter key fields
	 * @param processAccount
	 * @returnList<IAccounts>
	 * 
	 * @throws AccountNotFoundException
	 */
	public List<IAccounts> fetchExistAccountList(IAccounts processAccount) throws AccountNotFoundException {
		logger.debug("Entering");
				
		List<IAccounts> accountList = new ArrayList<IAccounts>();
		IAccounts account = null;
		CoreBankAccountDetail coreAccount = new CoreBankAccountDetail();
		coreAccount.setAcCcy(processAccount.getAcCcy());
		coreAccount.setCustCIF(processAccount.getAcCustCIF());
		coreAccount.setAcType(processAccount.getAcType());
		
		//Connecting to CoreBanking Interface
		List<CoreBankAccountDetail> coreBankingAccountList = getAccountProcess().fetchAccountDetails(coreAccount);
		
		//Fill the Account data using Core Banking Object
		for (int i = 0; i < coreBankingAccountList.size(); i++) {
			account = new IAccounts();
			account.setAccountId(coreBankingAccountList.get(i).getAccountNumber());
			account.setAcType(coreBankingAccountList.get(i).getAcType());
			account.setAcShortName(coreBankingAccountList.get(i).getCustShrtName());
			accountList.add(account);
		}
		
		logger.debug("Leaving");
		return accountList;
	}
	
	/**
	 * Method for Fetch Funding Account Balance depends on Parameter key fields
	 * @param processAccount
	 * @returnList<IAccounts>
	 * 
	 * @throws AccountNotFoundException
	 */
	public IAccounts fetchAccountAvailableBal(IAccounts processAccount,boolean newConnection) {
		logger.debug("Entering");

		IAccounts account = null;
		CoreBankAccountDetail coreBankAccountDetail = new CoreBankAccountDetail();
		coreBankAccountDetail.setAccountNumber(processAccount.getAccountId());

		//Connecting to CoreBanking Interface
		try {
			coreBankAccountDetail = getAccountProcess().fetchAccountAvailableBal(coreBankAccountDetail,newConnection);
		} catch (AccountNotFoundException e) {
			//TODO ADD ERROR TO ERROR DETAILS
		}

		account = new IAccounts();
		account.setAccountId(coreBankAccountDetail.getAccountNumber());
		account.setAvailBalSign(coreBankAccountDetail.getAmountSign());
		account.setAcAvailableBal(coreBankAccountDetail.getAcBal());
		if(coreBankAccountDetail.getAmountSign().equals("-")){
			account.setAcAvailableBal(new BigDecimal(0).subtract(account.getAcAvailableBal()));
		}

		logger.debug("Leaving");
		return account;
	}
	
	/**
	 * Method for Fetch Funding Account Balance depends on Parameter key fields
	 * @param processAccount
	 * @returnList<IAccounts>
	 * 
	 * @throws AccountNotFoundException
	 */
	public BigDecimal getAccountAvailableBal(String AccountId) {
		logger.debug("Entering");
		BigDecimal acBalance=BigDecimal.ZERO;
		CoreBankAccountDetail coreBankAccountDetail = new CoreBankAccountDetail();
		coreBankAccountDetail.setAccountNumber(AccountId);

		//Connecting to CoreBanking Interface
		try {
			coreBankAccountDetail = getAccountProcess().fetchAccountAvailableBal(coreBankAccountDetail,false);
		} catch (AccountNotFoundException e) {
			//TODO ADD ERROR TO ERROR DETAILS
		}
		if (coreBankAccountDetail!=null && coreBankAccountDetail.getAcBal()!=null) {
			acBalance=coreBankAccountDetail.getAcBal();
			if (coreBankAccountDetail.getAmountSign().equals("-")) {
				acBalance=  new BigDecimal(0).subtract(coreBankAccountDetail.getAcBal());
            }
        }

		logger.debug("Leaving");
		return acBalance;
	}
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public AccountProcess getAccountProcess() {
    	return accountProcess;
    }
	public void setAccountProcess(AccountProcess accountProcess) {
    	this.accountProcess = accountProcess;
    }
	
}
