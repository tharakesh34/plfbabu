package com.pennant.Interface.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.Interface.model.IAccounts;
import com.pennant.Interface.service.AccountInterfaceService;
import com.pennant.backend.model.finance.AccountHoldStatus;
import com.pennant.coreinterface.exception.AccountNotFoundException;
import com.pennant.coreinterface.exception.EquationInterfaceException;
import com.pennant.coreinterface.model.CoreBankAccountDetail;
import com.pennant.coreinterface.service.AccountDetailProcess;

public class AccountInterfaceServiceCoreDBImpl implements AccountInterfaceService {
	private static Logger logger = Logger.getLogger(AccountInterfaceServiceCoreDBImpl.class);

	protected AccountDetailProcess accountDetailProcess;

	/**
	 * Method for Fetch Account detail depends on Parameter key fields
	 * @param coreAcct
	 * @return
	 * @throws AccountNotFoundException
	 */
	public List<IAccounts> fetchExistAccount(List<IAccounts> accountDetails, String createNow) throws AccountNotFoundException {
		logger.debug("Entering");
				
		IAccounts iAccount = null;
		List<CoreBankAccountDetail> coreBankAccountDetails = new ArrayList<CoreBankAccountDetail>(accountDetails.size());
		CoreBankAccountDetail coreBankAccount = null;
		for (int i = 0; i < accountDetails.size(); i++) {
			iAccount = accountDetails.get(i);
			coreBankAccount = new CoreBankAccountDetail();
			coreBankAccount.setCustCIF(iAccount.getAcCustCIF());
			coreBankAccount.setAcBranch(iAccount.getAcBranch());
			coreBankAccount.setAcCcy(iAccount.getAcCcy());
						
			if("DISB".equals(iAccount.getAcType())) {
				coreBankAccount.setAcType("DI");
			} else if("REPAY".equals(iAccount.getAcType())){ //TODO
				coreBankAccount.setAcType("RE");
			}else {
				coreBankAccount.setAcType(iAccount.getAcType());
			}
			
			coreBankAccount.setAccountNumber(iAccount.getAccountId());
			coreBankAccount.setCreateNew(iAccount.getFlagCreateNew());
			coreBankAccount.setInternalAc(iAccount.getInternalAc());
			coreBankAccount.setCreateIfNF(iAccount.getFlagCreateIfNF());
			coreBankAccountDetails.add(coreBankAccount);
		}
		
		//Connecting to CoreBanking Interface
		coreBankAccountDetails = getAccountDetailProcess().fetchAccount(coreBankAccountDetails, createNow);
		
		//Fill the Account data using Core Banking Object
		List<IAccounts> accountResList = new ArrayList<IAccounts>(coreBankAccountDetails.size());
		for (int i = 0; i < coreBankAccountDetails.size(); i++) {
			iAccount = new IAccounts();
			CoreBankAccountDetail detail = coreBankAccountDetails.get(i);

			iAccount.setAccountId(detail.getAccountNumber());			
			iAccount.setAcCustCIF(detail.getCustCIF()); 			
			iAccount.setAcCcy(detail.getAcCcy()); 			
			iAccount.setAcType(detail.getAcType()); 	
			
			if("DI".equals(detail.getAcType())) {
				iAccount.setAcType("DISB");
			} else if("RE".equals(detail.getAcType())){ //TODO
				iAccount.setAcType("REPAY");
			}else if("Y".equals(detail.getInternalAc())) {
				iAccount.setAcType(detail.getAcSPCode());
			}
			
			iAccount.setAcBranch(detail.getAcBranch()); 		
			iAccount.setFlagCreateNew(detail.getCreateNew().equals("Y")?true:false); 		
			iAccount.setFlagCreateIfNF(detail.getCreateIfNF().equals("Y")?true:false); 	
			iAccount.setInternalAc(StringUtils.trimToEmpty(detail.getInternalAc()).equals("Y")?true:false); 	
			iAccount.setFlagPostStatus(StringUtils.trimToEmpty(detail.getOpenStatus())); 		
			iAccount.setErrorCode(StringUtils.trimToEmpty(detail.getErrorCode())); 	
			iAccount.setErrorMsg(StringUtils.trimToEmpty(detail.getErrorMessage())); 	
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
	/*	IAccounts account = null;
		CoreBankAccountDetail coreAccount = new CoreBankAccountDetail();
		coreAccount.setAcCcy(processAccount.getAcCcy());
		coreAccount.setCustCIF(processAccount.getAcCustCIF());
		coreAccount.setAcType(processAccount.getAcType());
		coreAccount.setTranAc(processAccount.getTranAc());
		
		//Connecting to CoreBanking Interface
		List<CoreBankAccountDetail> coreBankingAccountList = getAccountProcess().fetchAccountDetails(coreAccount);
		
		//Fill the Account data using Core Banking Object
		for (int i = 0; i < coreBankingAccountList.size(); i++) {
			account = new IAccounts();
			account.setAccountId(coreBankingAccountList.get(i).getAccountNumber());
			account.setAcType(coreBankingAccountList.get(i).getAcType());
			account.setTranAc(coreBankingAccountList.get(i).getTranAc());
			account.setAcShortName(coreBankingAccountList.get(i).getCustShrtName());
			accountList.add(account);
		} */
		
		IAccounts account = null;
		account = new IAccounts();
		account.setAccountId("0001100101048");
		account.setAcType("CA");
		account.setTranAc("");
		account.setAcShortName("RAJAT BHATIA");
		accountList.add(account);

		
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
	public IAccounts fetchAccountAvailableBal(String processAccount) {
		logger.debug("Entering");

		IAccounts account = null;
		CoreBankAccountDetail coreBankAccountDetail = new CoreBankAccountDetail();
		coreBankAccountDetail.setAccountNumber(processAccount);

		//Connecting to CoreBanking Interface
		try {
			coreBankAccountDetail = getAccountDetailProcess().fetchAccountAvailableBal(coreBankAccountDetail);
		} catch (AccountNotFoundException e) {
			//TODO ADD ERROR TO ERROR DETAILS
		}

		account = new IAccounts();
		account.setAccountId(coreBankAccountDetail.getAccountNumber());
		account.setAvailBalSign(coreBankAccountDetail.getAmountSign());
		account.setAcAvailableBal(coreBankAccountDetail.getAcBal());
		if(coreBankAccountDetail.getAmountSign().equals("-")){
			account.setAcAvailableBal(BigDecimal.ZERO.subtract(account.getAcAvailableBal()));
		}

		logger.debug("Leaving");
		return account;
	}
	
	@Override
    public BigDecimal getAccountAvailableBal(String AccountId) {

		logger.debug("Entering");
		BigDecimal acBalance=BigDecimal.ZERO;
		CoreBankAccountDetail coreBankAccountDetail = new CoreBankAccountDetail();
		coreBankAccountDetail.setAccountNumber(AccountId);

		//Connecting to CoreBanking Interface
		try {
			coreBankAccountDetail = getAccountDetailProcess().fetchAccountAvailableBal(coreBankAccountDetail);
		} catch (AccountNotFoundException e) {
			//TODO ADD ERROR TO ERROR DETAILS
		}
		if (coreBankAccountDetail!=null && coreBankAccountDetail.getAcBal()!=null) {
			acBalance=coreBankAccountDetail.getAcBal();
			if (coreBankAccountDetail.getAmountSign().equals("-")) {
				acBalance=  BigDecimal.ZERO.subtract(coreBankAccountDetail.getAcBal());
            }
        }

		logger.debug("Leaving");
		return acBalance;
	
    }
	@Override
    public List<CoreBankAccountDetail> checkAccountID(List<CoreBankAccountDetail> coreAcctList)
            throws AccountNotFoundException {
	    // TODO Auto-generated method stub
	    return null;
    }

	public Map<String, IAccounts> getAccountsListAvailableBal(List<String> accountsList) {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public Map<String, IAccounts> getAccountsAvailableBalMap(List<String> accountsList) {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public List<IAccounts> getAccountsAvailableBalList(List<IAccounts> accountsList) {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public int removeAccountHolds() throws Exception {
	    // TODO Auto-generated method stub
	    return 0;
    }

	@Override
    public List<AccountHoldStatus> addAccountHolds(List<AccountHoldStatus> accountslIst,
            Date valueDate) throws EquationInterfaceException {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public Map<String, String> getAccountCurrencyMap(Map<String, String> accountCcyMap)
            throws AccountNotFoundException {
	    // TODO Auto-generated method stub
	    return null;
    }
	
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public AccountDetailProcess getAccountDetailProcess() {
    	return accountDetailProcess;
    }
	public void setAccountDetailProcess(AccountDetailProcess accountDetailProcess) {
    	this.accountDetailProcess = accountDetailProcess;
    }


}
