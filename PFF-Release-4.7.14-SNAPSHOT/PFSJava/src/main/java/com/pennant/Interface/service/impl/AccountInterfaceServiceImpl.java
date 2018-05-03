package com.pennant.Interface.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.Interface.model.IAccounts;
import com.pennant.Interface.service.AccountInterfaceService;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.AccountHoldStatus;
import com.pennant.coreinterface.model.AccountBalance;
import com.pennant.coreinterface.model.CoreBankAccountDetail;
import com.pennant.coreinterface.model.account.InterfaceAccount;
import com.pennant.coreinterface.model.collateral.CollateralMark;
import com.pennant.coreinterface.process.AccountDataProcess;
import com.pennant.coreinterface.process.AccountDetailProcess;
import com.pennanttech.pennapps.core.InterfaceException;

public class AccountInterfaceServiceImpl implements AccountInterfaceService{
	private static Logger logger = Logger.getLogger(AccountInterfaceServiceImpl.class);

	protected AccountDetailProcess accountDetailProcess;
	protected AccountDataProcess accountDataProcess;


	public AccountInterfaceServiceImpl(){
		super();
	}
	
	/**
	 * Method for Fetch Account detail depends on Parameter key fields
	 * @param coreAcct
	 * @return
	 * @throws InterfaceException
	 */
	public List<IAccounts> fetchExistAccount(List<IAccounts> accountDetails,
			String createNow ) throws InterfaceException {
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
		coreBankAccountDetails = getAccountDetailProcess().fetchAccount(coreBankAccountDetails,createNow);
		
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
			iAccount.setFlagCreateNew(detail.getCreateNew()); 		
			iAccount.setFlagCreateIfNF(detail.getCreateIfNF()); 	
			iAccount.setInternalAc(detail.getInternalAc()); 	
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
	 * @return {@link List} of {@link IAccounts}
	 * 
	 * @throws InterfaceException
	 */
	public List<IAccounts> fetchExistAccountList(IAccounts processAccount) throws InterfaceException {
		logger.debug("Entering");
				
		List<IAccounts> accountList = new ArrayList<IAccounts>();
		IAccounts account = null;
		CoreBankAccountDetail coreAccount = new CoreBankAccountDetail();
		coreAccount.setAcCcy(processAccount.getAcCcy());
		coreAccount.setCustCIF(processAccount.getAcCustCIF());
		coreAccount.setAcType(processAccount.getAcType());
		coreAccount.setDivision(processAccount.getDivision());
		
		//Connecting to CoreBanking Interface
		List<CoreBankAccountDetail> coreBankingAccountList = getAccountDetailProcess().fetchCustomerAccounts(coreAccount);
		CoreBankAccountDetail coreBankAccountDetail = null;
		//Fill the Account data using Core Banking Object
		if(coreBankingAccountList != null){
			for (int i = 0; i < coreBankingAccountList.size(); i++) {
				coreBankAccountDetail = coreBankingAccountList.get(i); 
				account = new IAccounts();
				account.setAccountId(coreBankAccountDetail.getAccountNumber());
				account.setAcType(coreBankAccountDetail.getAcType());
				account.setAcCcy(coreBankAccountDetail.getAcCcy());
				account.setAcShortName(coreBankAccountDetail.getCustShrtName());
				account.setAcAvailableBal(coreBankAccountDetail.getAcBal());
				if(StringUtils.equals(coreBankAccountDetail.getAmountSign(),"-")){
					account.setAcAvailableBal(BigDecimal.ZERO.subtract(account.getAcAvailableBal()));
				}

				accountList.add(account);
			}
		}
		
		logger.debug("Leaving");
		return accountList;
	}
	
	/**
	 * Method for Fetch Funding Account Balance depends on Parameter key fields
	 * @param processAccount
	 * @return {@link List} of {@link IAccounts}
	 * 
	 * @throws InterfaceException
	 */
	public IAccounts fetchAccountAvailableBal(String processAccount) throws InterfaceException {
		logger.debug("Entering");
		
		IAccounts iAccount = null;
		CoreBankAccountDetail coreAccount = new CoreBankAccountDetail();
		coreAccount.setAccountNumber(processAccount);
		
		//Connecting to CoreBanking Interface
		List<CoreBankAccountDetail> coreBankingAccountList = getAccountDetailProcess().fetchAccountDetails(coreAccount);
		CoreBankAccountDetail coreBankAccountDetail = null;
		//Fill the Account data using Core Banking Object
		if(coreBankingAccountList != null) {
			for (int i = 0; i < coreBankingAccountList.size(); i++) {
				coreBankAccountDetail = coreBankingAccountList.get(i); 
				iAccount = new IAccounts();
				iAccount.setAccountId(coreBankAccountDetail.getAccountNumber());
				iAccount.setAcType(coreBankAccountDetail.getAcType());
				iAccount.setAcCcy(coreBankAccountDetail.getAcCcy());
				iAccount.setAcShortName(coreBankAccountDetail.getCustShrtName());
				iAccount.setAcAvailableBal(coreBankAccountDetail.getAcBal());
				if(StringUtils.equals(coreBankAccountDetail.getAmountSign(),"-")){
					iAccount.setAcAvailableBal(BigDecimal.ZERO.subtract(iAccount.getAcAvailableBal()));
				}
				iAccount.setIban(coreBankAccountDetail.getIBAN());
			}
		}
		
		logger.debug("Leaving");
		return iAccount;
	}
	
	/**
	 * Method for Fetch Funding Account Balance depends on Parameter key fields
	 * @param processAccount
	 * @return {@link List} of {@link IAccounts}
	 * 
	 * @throws InterfaceException
	 */
	public BigDecimal getAccountAvailableBal(String accountId) {
		logger.debug("Entering");
		BigDecimal acBalance=BigDecimal.ZERO;
		CoreBankAccountDetail coreBankAccountDetail = new CoreBankAccountDetail();
		coreBankAccountDetail.setAccountNumber(accountId);

		//Connecting to CoreBanking Interface
		try {
			coreBankAccountDetail = getAccountDetailProcess().fetchAccountAvailableBal(coreBankAccountDetail);
		} catch (InterfaceException e) {
			logger.error("Exception: ", e);
		}
		if (coreBankAccountDetail!=null && coreBankAccountDetail.getAcBal()!=null) {
			acBalance=coreBankAccountDetail.getAcBal();
			if ("-".equals(coreBankAccountDetail.getAmountSign())) {
				acBalance=  BigDecimal.ZERO.subtract(coreBankAccountDetail.getAcBal());
            }
        }

		logger.debug("Leaving");
		return acBalance;
	}
	
	/**
	 * Method for Fetch Funding Account Balance depends on Parameter key fields
	 * @param processAccount
	 * @return {@link List} of {@link IAccounts}
	 * 
	 * @throws InterfaceException
	 */
	@Override
	public List<CoreBankAccountDetail> checkAccountID(List<CoreBankAccountDetail> coreAcctList) throws InterfaceException {
		return getAccountDetailProcess().fetchAccountsListAvailableBal(coreAcctList, false);

	}
	
	/**
	 * Method for Fetch Funding Accounts Balance depends on Parameter key fields
	 * @param processAccount
	 * @return {@link List} of {@link IAccounts}
	 * 
	 * @throws InterfaceException
	 */
	public Map<String, IAccounts> getAccountsAvailableBalMap(List<String> accountsList) {
		logger.debug("Entering");
		IAccounts account = null;
 		List<CoreBankAccountDetail> coreBankAccountDetailList = new ArrayList<CoreBankAccountDetail>();
		CoreBankAccountDetail accountDetail = null;
		for (String accountNumber : accountsList) {
	     	accountDetail = new CoreBankAccountDetail();
			accountDetail.setAccountNumber( accountNumber );
			coreBankAccountDetailList.add(accountDetail);
         }

		//Connecting to CoreBanking Interface
		try {
			coreBankAccountDetailList = getAccountDetailProcess().fetchAccountsListAvailableBal(coreBankAccountDetailList , false);
		} catch (InterfaceException e) {
			logger.error("Exception: ", e);
			//TODO ADD ERROR TO ERROR DETAILS
		}
		Map<String, IAccounts>  accountsMap = new HashMap<String, IAccounts>();
		if (coreBankAccountDetailList != null && !coreBankAccountDetailList.isEmpty()) {
			for (CoreBankAccountDetail coreBankAccountDetail : coreBankAccountDetailList) {
				account = new IAccounts();
				account.setAccountId(coreBankAccountDetail.getAccountNumber());
				account.setAvailBalSign(coreBankAccountDetail.getAmountSign());
				account.setAcAvailableBal(coreBankAccountDetail.getAcBal());
				if ("-".equals(coreBankAccountDetail.getAmountSign())) {
					account.setAcAvailableBal(BigDecimal.ZERO.subtract(coreBankAccountDetail.getAcBal()));
				}
				accountsMap.put(coreBankAccountDetail.getAccountNumber(), account);
             }
        }

		logger.debug("Leaving");
		return accountsMap;
	}
	/**
	 * Method for Fetch Funding Accounts Balance depends on Parameter key fields
	 * @param processAccount
	 * @return {@link List} of {@link IAccounts}
	 * 
	 * @throws InterfaceException
	 */
	public List<IAccounts> getAccountsAvailableBalList(List<IAccounts> accountsList) throws InterfaceException {
		logger.debug("Entering");
		
		IAccounts account = null;
		List<CoreBankAccountDetail> coreBankAccountDetailList = new ArrayList<CoreBankAccountDetail>();
		CoreBankAccountDetail accountDetail = null;
		for (IAccounts accountNumber : accountsList) {
			accountDetail = new CoreBankAccountDetail();
			accountDetail.setAccountNumber( accountNumber.getAccountId() );
			coreBankAccountDetailList.add(accountDetail);
		}
		
		//Connecting to CoreBanking Interface
		try {
			coreBankAccountDetailList = getAccountDetailProcess().fetchAccountsListAvailableBal(coreBankAccountDetailList, false);
		} catch (InterfaceException e) {
			throw e;
 		}
		
		List<IAccounts> accountList = null;
		accountList = new ArrayList<IAccounts>();
		if (coreBankAccountDetailList != null && !coreBankAccountDetailList.isEmpty()) {
			for (CoreBankAccountDetail coreBankAccountDetail : coreBankAccountDetailList) {
				account = new IAccounts();
				account.setAccountId(coreBankAccountDetail.getAccountNumber());
				account.setAcType(coreBankAccountDetail.getAcType());
				account.setAcCcy(coreBankAccountDetail.getAcCcy());
				account.setAcShortName(coreBankAccountDetail.getAcShrtName());
				account.setAvailBalSign(coreBankAccountDetail.getAmountSign());
				account.setAcAvailableBal(coreBankAccountDetail.getAcBal());
				if (StringUtils.equals(coreBankAccountDetail.getAmountSign(), "-")) {
					account.setAcAvailableBal(BigDecimal.ZERO.subtract(coreBankAccountDetail.getAcBal()));
				}
				accountList.add(account);
 			}
		}
		
		logger.debug("Leaving");
		return accountList;
	}
	
	/**
	 * Method for Fetch Funding Accounts Balance depends on Parameter key fields
	 * @param processAccount
	 * @return {@link List} of {@link IAccounts}
	 * 
	 * @throws InterfaceException
	 */
	@Override
	public Map<String,String> getAccountCurrencyMap(Map<String,String> accountCcyMap) throws InterfaceException {
		logger.debug("Entering");
		
		List<CoreBankAccountDetail> coreBankAccountDetailList = new ArrayList<CoreBankAccountDetail>();
		CoreBankAccountDetail accountDetail = null;
		
		List<String> accountsList = new ArrayList<String>(accountCcyMap.keySet());
		for (String accountNumber : accountsList) {
			accountDetail = new CoreBankAccountDetail();
			accountDetail.setAccountNumber(accountNumber);
			coreBankAccountDetailList.add(accountDetail);
		}
		
		//Connecting to CoreBanking Interface
		try {
			coreBankAccountDetailList = getAccountDetailProcess().fetchAccountsListAvailableBal(coreBankAccountDetailList, true);
		} catch (InterfaceException e) {
			throw e;
 		}
		
		accountCcyMap = null;
		if (coreBankAccountDetailList != null && !coreBankAccountDetailList.isEmpty()) {
			accountCcyMap = new HashMap<String, String>(coreBankAccountDetailList.size());
			for (CoreBankAccountDetail coreBankAccountDetail : coreBankAccountDetailList) {
				accountCcyMap.put(coreBankAccountDetail.getAccountNumber(), coreBankAccountDetail.getAcCcy());
 			}
		}else{
			accountCcyMap = new HashMap<String, String>(1);
		}
		
		logger.debug("Leaving");
		return accountCcyMap;
	}
	
	
	/**
	 * Method for Removing Holds on Accounts Before Payments Recovery
	 */
	@Override
    public int removeAccountHolds() throws Exception {
	    return getAccountDataProcess().removeAccountHolds();
    }
	
	/**
	 * Method for Removing Account Holds on Based on List of Repay Account Details
	 * @return 
	 * @throws EquationInterfaceException 
	 */
	@Override
    public List<AccountHoldStatus> addAccountHolds(List<AccountHoldStatus> accountslIst, Date valueDate, String holdType) 
    		throws InterfaceException {
		logger.debug("Entering");
		
		//Preparing List Of account Balance Details
		List<AccountBalance> acBalList = new ArrayList<AccountBalance>();
		List<AccountHoldStatus> acBalStatusList = new ArrayList<AccountHoldStatus>();
		AccountBalance accBal = null;
		for (AccountHoldStatus holdStatus : accountslIst) {
			accBal = new AccountBalance();
			accBal.setRepayAccount(holdStatus.getAccount());
			accBal.setAccBalance(holdStatus.getCurODAmount());
			acBalList.add(accBal);
        }
		
		if(!acBalList.isEmpty()){
			List<AccountBalance> returnAcBalList = getAccountDataProcess().addAccountHolds(acBalList, holdType);
			AccountHoldStatus holdStatus = null;
			for (AccountBalance accountBalance : returnAcBalList) {
	            holdStatus = new AccountHoldStatus();
	            holdStatus.setAccount(accountBalance.getRepayAccount());
	            holdStatus.setCurODAmount(accountBalance.getAccBalance());
	            holdStatus.setHoldStatus(accountBalance.getAcHoldStatus());
	            holdStatus.setStatusDesc(accountBalance.getStatusDesc());
	            holdStatus.setValueDate(valueDate);
	            holdStatus.setHoldType(holdType);
	            acBalStatusList.add(holdStatus);
            }
		}
		
		logger.debug("Leaving");
		return acBalStatusList;
    }

	/**
	 * Method for creating customer Account in T24
	 */
	@Override
    public InterfaceAccount createAccount(Customer customer) throws InterfaceException {
		logger.debug("Entering");
		
		InterfaceAccount interfaceAccount = new InterfaceAccount();
		interfaceAccount.setCustCIF(customer.getCustCIF());
		interfaceAccount.setBranchCode(customer.getCustDftBranch());
		interfaceAccount.setCustomerType(customer.getCustTypeCode());
		interfaceAccount.setProductCode("1010");
		interfaceAccount.setCurrency(customer.getCustBaseCcy());
		interfaceAccount.setModeOfOperation("1");
		interfaceAccount.setMinNoOfSignatory(1);
		interfaceAccount.setPowerOfAttorneyFlag("N");
		interfaceAccount.setIntroducer("1234567");
		interfaceAccount.setShoppingCardIssue("NO");
		
		logger.debug("Leaving");
	    return getAccountDataProcess().createAccount(interfaceAccount);
    }

	/**
	 * Method for send Collateral Mark request to interface
	 * 
	 * @param coltralMarkReq
	 * @return CollateralMark
	 * 
	 *@throws InterfaceException
	 */
	@Override
	public CollateralMark collateralMarking(CollateralMark coltralMarkReq) throws InterfaceException {
		logger.debug("Entering");
		logger.debug("Leaving");
		return getAccountDataProcess().collateralMarking(coltralMarkReq);
	}
	
	/**
	 * Method for send Collateral De-Mark request to interface
	 * 
	 * @param coltralMarkReq
	 * @return CollateralMark
	 * 
	 *@throws InterfaceException
	 */
	@Override
	public CollateralMark collateralDeMarking(CollateralMark coltralMarkReq) throws InterfaceException {
		logger.debug("Entering");
		logger.debug("Leaving");
		return getAccountDataProcess().collateralDeMarking(coltralMarkReq);
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public AccountDetailProcess getAccountDetailProcess() {
		return accountDetailProcess;
	}
	public void setAccountDetailProcess(AccountDetailProcess accountDetailProcess) {
		this.accountDetailProcess = accountDetailProcess;
	}

	public AccountDataProcess getAccountDataProcess() {
		return accountDataProcess;
	}

	public void setAccountDataProcess(AccountDataProcess accountDataProcess) {
		this.accountDataProcess = accountDataProcess;
	}

}
