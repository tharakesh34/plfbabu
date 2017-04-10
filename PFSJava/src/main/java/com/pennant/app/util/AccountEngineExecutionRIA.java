/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ******************************************************************************************** 
 * FILE HEADER *
 ******************************************************************************************** 
 * 
 * FileName : AccountEngineExecution.java *
 * 
 * Author : PENNANT TECHONOLOGIES *
 * 
 * Creation Date : 26-04-2011 *
 * 
 * Modified Date : 30-07-2011 *
 * 
 * Description : *
 * 
 ******************************************************************************************** 
 * Date Author Version Comments *
 ******************************************************************************************** 
 * 26-04-2011 Pennant 0.1 * * * * * * * * *
 ******************************************************************************************** 
 */
package com.pennant.app.util;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.Interface.model.IAccounts;
import com.pennant.Interface.service.AccountInterfaceService;
import com.pennant.app.constants.AccountConstants;
import com.pennant.app.constants.AccountEventConstants;
import com.pennant.backend.dao.applicationmaster.CurrencyDAO;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.masters.SystemInternalAccountDefinitionDAO;
import com.pennant.backend.dao.rmtmasters.FinTypeAccountingDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.dao.rmtmasters.TransactionEntryDAO;
import com.pennant.backend.dao.rulefactory.RuleDAO;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinContributorDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rmtmasters.TransactionEntry;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEAmountCodesRIA;
import com.pennant.backend.model.rulefactory.AEAmountCodesRIAFB;
import com.pennant.backend.model.rulefactory.DataSet;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.model.rulefactory.SubHeadRule;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.RuleReturnType;
import com.pennant.eod.util.EODProperties;
import com.pennant.exception.PFFInterfaceException;

public class AccountEngineExecutionRIA implements Serializable {
	private static final long serialVersionUID = 852062955563015315L;
	private Logger logger = Logger.getLogger(AccountEngineExecutionRIA.class);

	private FinanceTypeDAO financeTypeDAO;
	private FinanceMainDAO financeMainDAO;
	private TransactionEntryDAO transactionEntryDAO;
	private SystemInternalAccountDefinitionDAO internalAccountDefinitionDAO;
	private RuleDAO ruleDAO;
	private CustomerDAO customerDAO;
	private CurrencyDAO currencyDAO;
	private AccountInterfaceService accountInterfaceService;
	private RuleExecutionUtil ruleExecutionUtil;
	private FinTypeAccountingDAO finTypeAccountingDAO;

	private FinanceType financeType;
	private FinanceMain financeMain;
	private AEAmountCodesRIAFB amountCodesRIAFB;
	private SubHeadRule subHeadRule = null;
	private String currencyCode = null;
	private Customer customer = null;
	List<IAccounts> accountsList = new ArrayList<IAccounts>();
	private IAccounts newAccount = null;
	private Map<String, Object> accountsMap = new HashMap<String, Object>();
	private List<String> accountIdList = new ArrayList<String>();
	
	BigDecimal iInvestTotal = null;
	BigDecimal iAccrueTotal = null;
	BigDecimal iAccrueSTotal = null;
	BigDecimal iDAccrueTotal = null;
	BigDecimal iNAccrueTotal = null;
	BigDecimal iLAccrueTotal = null;
	BigDecimal iPftTotal = null;
	BigDecimal iPftABTotal = null;
	BigDecimal iPftAPTotal = null;
	BigDecimal iCpzChgTotal = null;
	BigDecimal iPftChgTotal = null;
	BigDecimal iPftSTotal = null;
	BigDecimal iPftSBTotal = null;
	BigDecimal iPftSPTotal = null;
	BigDecimal iPriTotal = null;
	BigDecimal iPriABTotal = null;
	BigDecimal iPriAPTotal = null;
	BigDecimal iPriSTotal = null;
	BigDecimal iPriSBTotal = null;
	BigDecimal iPriSPTotal = null;
	BigDecimal iRpPftTotal = null;
	BigDecimal iRpPriTotal = null;
	BigDecimal iRpTotTotal = null;
	BigDecimal iRefundTotal = null;
	BigDecimal iCpzTotTotal = null;
	BigDecimal iCpzPrvTotal = null;
	BigDecimal iCpzCurTotal = null;
	BigDecimal iCpzNxtTotal = null;
	BigDecimal iPftInAdvTotal = null;
	BigDecimal iAccrueMFTotal = null;
	BigDecimal iAccrueSMFTotal = null;
	BigDecimal iDAccrueMFTotal = null;
	BigDecimal iNAccrueMFTotal = null;
	BigDecimal iLAccrueMFTotal = null;
	BigDecimal iMFfeeChgTotal = null;
	BigDecimal iMFeeTotal = null;
	BigDecimal iMFRepayTotal = null;
	BigDecimal iPenaltyTotal = null;
	BigDecimal iWaiverTotal = null;

	//Default Constructor
	public AccountEngineExecutionRIA() {
		super();
	}
	
	/**
	 * Method for Execution of Accounting Sets depend on Event
	 * 
	 * @param dataSet
	 * @param aeAmountCodes
	 * @param type
	 * @return
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws PFFInterfaceException
	 */
	public List<ReturnDataSet> getAccEngineExecResults(DataSet dataSet,
	        AEAmountCodes aeAmountCodes, String createNow, List<AEAmountCodesRIA> riaDetailList,Map<String, FeeRule> map)
	        throws PFFInterfaceException, IllegalAccessException, InvocationTargetException {
		
		iInvestTotal = BigDecimal.ZERO;
		iAccrueTotal = BigDecimal.ZERO;
		iAccrueSTotal = BigDecimal.ZERO;
		iDAccrueTotal = BigDecimal.ZERO;
		iNAccrueTotal = BigDecimal.ZERO;
		iLAccrueTotal = BigDecimal.ZERO;
		iPftTotal = BigDecimal.ZERO;
		iPftABTotal = BigDecimal.ZERO;
		iPftAPTotal = BigDecimal.ZERO;
		iCpzChgTotal = BigDecimal.ZERO;
		iPftChgTotal = BigDecimal.ZERO;
		iPftSTotal = BigDecimal.ZERO;
		iPftSBTotal = BigDecimal.ZERO;
		iPftSPTotal = BigDecimal.ZERO;
		iPriTotal = BigDecimal.ZERO;
		iPriABTotal = BigDecimal.ZERO;
		iPriAPTotal = BigDecimal.ZERO;
		iPriSTotal = BigDecimal.ZERO;
		iPriSBTotal = BigDecimal.ZERO;
		iPriSPTotal = BigDecimal.ZERO;
		iRpPftTotal = BigDecimal.ZERO;
		iRpPriTotal = BigDecimal.ZERO;
		iRpTotTotal = BigDecimal.ZERO;
		iRefundTotal = BigDecimal.ZERO;
		iCpzTotTotal = BigDecimal.ZERO;
		iCpzPrvTotal = BigDecimal.ZERO;
		iCpzCurTotal = BigDecimal.ZERO;
		iCpzNxtTotal = BigDecimal.ZERO;
		iPftInAdvTotal = BigDecimal.ZERO;
		iAccrueMFTotal = BigDecimal.ZERO;
		iAccrueSMFTotal = BigDecimal.ZERO;
		iDAccrueMFTotal = BigDecimal.ZERO;
		iNAccrueMFTotal = BigDecimal.ZERO;
		iLAccrueMFTotal = BigDecimal.ZERO;
		iMFfeeChgTotal = BigDecimal.ZERO;
		iMFeeTotal = BigDecimal.ZERO;
		iMFRepayTotal = BigDecimal.ZERO;
		iPenaltyTotal = BigDecimal.ZERO;
		iWaiverTotal = BigDecimal.ZERO;

		accountIdList = new ArrayList<String>();
		accountsList = new ArrayList<IAccounts>();
		accountsMap = new HashMap<String, Object>();
		amountCodesRIAFB = new AEAmountCodesRIAFB();
		customer = new Customer();

		// Fill Amount Code Detail Object with Respect to Schedule Details
		long accountingSetId = prepareAmountCodes(dataSet, aeAmountCodes, riaDetailList);
		String phase = SysParamUtil.getValueAsString(PennantConstants.APP_PHASE);

		List<TransactionEntry> transactionEntries = null;
		if (phase.equals(PennantConstants.APP_PHASE_DAY)) {
			transactionEntries = getTransactionEntryDAO().getListTransactionEntryById(accountingSetId, "_AEView", true);
		} else {
			transactionEntries = EODProperties.getTransactionEntryList(accountingSetId);
		}

		List<ReturnDataSet> returnList = getPrepareAccountingSetResults(dataSet,transactionEntries, riaDetailList, createNow,map);
		resetVariables();
		return returnList;
	}

	/**
	 * Reset Data Objects after Compleion of Execution
	 */
	private void resetVariables() {

		financeType = null;
		financeMain = null;
		currencyCode = null;
		amountCodesRIAFB = null;
		newAccount = null;
		accountsMap = new HashMap<String, Object>();
		accountIdList = new ArrayList<String>();

	}

	/**
	 * Method for Preparing RIA Investment details List
	 * @param contributorDetails
	 * @param finRef
	 * @return
	 */
	public List<AEAmountCodesRIA> prepareRIADetails(List<FinContributorDetail> contributorDetails, String finRef){
		
		List<AEAmountCodesRIA> aeAmountCodesRIAList = new ArrayList<AEAmountCodesRIA>();
		for (FinContributorDetail finContrDetail : contributorDetails) {
	        
			AEAmountCodesRIA ria = new AEAmountCodesRIA();
			ria.setFinReference(finRef);
			ria.setAccountId(finContrDetail.getInvestAccount());
			ria.setCustCIF(finContrDetail.getLovDescContributorCIF());
			ria.setContributorId(finContrDetail.getCustID());
			ria.setInvestment(finContrDetail.getContributorInvest());
			ria.setMudaribPercent(finContrDetail.getMudaribPerc());
			
			aeAmountCodesRIAList.add(ria);
        }
		
		return aeAmountCodesRIAList;
		
	}
	
	/**
	 * Method for Preparing List of FeeRule Objects
	 * 
	 * @param dataSet
	 * @param amountCode
	 * @return
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	private Long prepareAmountCodes(DataSet dataSet, AEAmountCodes aeAmountCodes,List<AEAmountCodesRIA> riaDetailList )
	        throws IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		financeType = getFinanceTypeDAO().getFinanceTypeByFinType(dataSet.getFinType());

		//Fill Amount Code Detail Object with FinanceMain Object
		calRIAAmountCodes(dataSet, aeAmountCodes, riaDetailList);
		setAmountCodesFB(aeAmountCodes);//set data to DataSetFiller Object		
		customer = getCustomerDAO().getCustomerForPostings(dataSet.getCustId());

		logger.debug("Leaving");
		
		return  getFinTypeAccountingDAO().getAccountSetID(dataSet.getFinType(), dataSet.getFinEvent(), FinanceConstants.FINTYPEFEES_FINTYPE);
	}
	

	/**
	 * Method for preparing List of ReturnDataSet objects by executing rules
	 * 
	 * @param dataSet
	 * @param accountingSetId
	 * @return
	 * @throws PFFInterfaceException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	private List<ReturnDataSet> getPrepareAccountingSetResults(DataSet dataSet,
	        List<TransactionEntry> transactionEntries,List<AEAmountCodesRIA> amountCodesRIAList,
	        String createNow,Map<String, FeeRule> map) throws PFFInterfaceException,
	        IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");
		
		List<ReturnDataSet> returnDataSets = new ArrayList<ReturnDataSet>();

		accountIdList = new ArrayList<String>(transactionEntries.size());
		accountsMap = new HashMap<String, Object>(transactionEntries.size());
		accountsList = new ArrayList<IAccounts>(transactionEntries.size());

		//Set Account number generation
		for (TransactionEntry transactionEntry : transactionEntries) {

			if ("N".equals(createNow)) {
				if(!transactionEntry.isEntryByInvestment()){
					if (!accountIdList.contains(transactionEntry.getAccountSetid()+"-"+0+"-"+transactionEntry.getTransOrder())) {
						accountsList.add(getAccountNumber(dataSet, transactionEntry, false, 0, null));
					}
				}else{
					for (int i = 0; i < amountCodesRIAList.size(); i++) {
						if (!accountIdList.contains(transactionEntry.getAccountSetid()+"-"+i+1+"-"+transactionEntry.getTransOrder())) {
							accountsList.add(getAccountNumber(dataSet, transactionEntry, false,
									i+1,amountCodesRIAList.get(i)));
						}
					}
				}

			} else {
				if(!transactionEntry.isEntryByInvestment()){
					accountsList.add(getAccountNumber(dataSet, transactionEntry, true, 0, null));
				}else{
					for (int i = 0; i < amountCodesRIAList.size(); i++) {
						accountsList.add(getAccountNumber(dataSet, transactionEntry, true,i+1,
								amountCodesRIAList.get(i)));
                    }
				}
			}
		}

		//TODO Removed for AHB. New constant should be added in Implementation constants to check in core or not 		
		/*		if("N".equals(createNow)){
					this.accountsList = getAccountInterfaceService().fetchExistAccount(this.accountsList,createNow);
				}*/
		
 		for (IAccounts interfaceAccount : accountsList) {
			if (accountsMap.containsKey(interfaceAccount.getTransOrder().trim())) {
				accountsMap.remove(interfaceAccount.getTransOrder());
				accountsMap.put(interfaceAccount.getTransOrder(), interfaceAccount);
			}
		}
		

		for (TransactionEntry transactionEntry : transactionEntries) {
			if(!transactionEntry.isEntryByInvestment()){
				returnDataSets.add(prepareDataSet(dataSet, transactionEntry, createNow, amountCodesRIAFB, 0,map));
			}else{
				for (int i = 0; i < amountCodesRIAList.size(); i++) {
					returnDataSets.add(prepareDataSet(dataSet, transactionEntry, createNow, amountCodesRIAList.get(i),i+1,map));
                }
			}
		}

		logger.debug("Leaving");
		return returnDataSets;
	}
	
	/**
	 * Method for Preparation Of Return DataSet Objects
	 * @param dataSet
	 * @param transactionEntry
	 * @param createNow
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private ReturnDataSet prepareDataSet(DataSet dataSet, TransactionEntry transactionEntry, 
			String createNow, Object amountCodesRIA, int riaNo,Map<String, FeeRule> map) throws IllegalAccessException, InvocationTargetException{
		
		ReturnDataSet returnDataSet = new ReturnDataSet();
		
		//Set Object Data of ReturnDataSet(s)
		returnDataSet.setFinReference(dataSet.getFinReference());
		returnDataSet.setFinEvent(dataSet.getFinEvent());
		returnDataSet.setTranDesc(transactionEntry.getTransDesc());
		returnDataSet.setPostDate(dataSet.getPostDate());
		returnDataSet.setValueDate(dataSet.getValueDate());
		returnDataSet.setTranCode(transactionEntry.getTranscationCode());
		returnDataSet.setRevTranCode(transactionEntry.getRvsTransactionCode());
		returnDataSet.setDrOrCr(transactionEntry.getDebitcredit());
		returnDataSet.setShadowPosting(transactionEntry.isShadowPosting());
		returnDataSet.setPostToSys(transactionEntry.getPostToSys());
		returnDataSet.setDerivedTranOrder(transactionEntry.getDerivedTranOrder());
		if ("N".equals(createNow)) {
			returnDataSet.setAccountType(transactionEntry.getAccount());
		}

		//Post Reference
		String branch = StringUtils.isBlank(transactionEntry.getAccountBranch()) ? dataSet
		        .getFinBranch() : transactionEntry.getAccountBranch();
		String accType = StringUtils.isBlank(transactionEntry.getAccountType()) ? financeType
		        .getFinAcType() : transactionEntry.getAccountType();
		returnDataSet.setPostref(branch + "-" + accType + "-" + dataSet.getFinCcy());

		returnDataSet.setPostStatus("S");
		returnDataSet.setAmountType(transactionEntry.getChargeType());

		//Set Account Number
		IAccounts acc = (IAccounts) accountsMap.get(transactionEntry.getAccountSetid()+"-"+riaNo+"-"+transactionEntry.getTransOrder());

		returnDataSet.setAccount(acc.getAccountId());
		returnDataSet.setTranOrderId(acc.getTransOrder());
		returnDataSet.setPostStatus(acc.getFlagPostStatus());
		returnDataSet.setErrorId(acc.getErrorCode());
		returnDataSet.setErrorMsg(acc.getErrorMsg());

		//Regarding to Posting Data
		returnDataSet.setAcCcy(acc.getAcCcy());
		if (!"N".equals(createNow)) {
			returnDataSet.setAccountType(acc.getAcType());
			returnDataSet.setFinType(financeType.getFinType());
			if(riaNo == 0){
				returnDataSet.setCustCIF(customer.getCustCIF());
			}else{
				returnDataSet.setCustCIF(((AEAmountCodesRIA)amountCodesRIA).getCustCIF());
			}
			returnDataSet.setFinBranch(dataSet.getFinBranch());
			returnDataSet.setFlagCreateNew(acc.getFlagCreateNew());
			returnDataSet.setFlagCreateIfNF(acc.getFlagCreateIfNF());
			returnDataSet.setInternalAc(acc.getInternalAc());
		}

		//Execute Transaction Entry Rule
		BigDecimal amount = BigDecimal.ZERO;
		if(transactionEntry.getAmountRule() != null){
			
			String amountRule=transactionEntry.getAmountRule();
			
			HashMap<String, Object> fieldsAndValues = null;
			if(amountCodesRIA instanceof AEAmountCodesRIAFB){
				amountRule=executeAmountRule(dataSet.getFinEvent(), transactionEntry, map, dataSet.getFinCcy(), ((AEAmountCodesRIAFB)amountCodesRIA).getDeclaredFieldValues());
				fieldsAndValues = ((AEAmountCodesRIAFB)amountCodesRIA).getDeclaredFieldValues();
			}else if(amountCodesRIA instanceof AEAmountCodesRIA){
				amountRule=executeAmountRule(dataSet.getFinEvent(), transactionEntry, map, dataSet.getFinCcy(),((AEAmountCodesRIA)amountCodesRIA).getDeclaredFieldValues());
				fieldsAndValues = ((AEAmountCodesRIA)amountCodesRIA).getDeclaredFieldValues();
			}
			
			amount = (BigDecimal) getRuleExecutionUtil().executeRule(amountRule, fieldsAndValues, dataSet.getFinCcy(),
					RuleReturnType.DECIMAL);
		}
		returnDataSet.setCustId(dataSet.getCustId());
		returnDataSet.setPostAmount(amount);
		returnDataSet.setPostAmountLcCcy(CalculationUtil.getConvertedAmount(dataSet.getFinCcy(), SysParamUtil.getAppCurrency(), amount));
		
		return returnDataSet;
	}

	/**
	 * Method for Prepare Account Number based on Transaction Entry Account
	 * 
	 * @param dataSet
	 * @param transactionEntry
	 * @param finType
	 * @return
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	private IAccounts getAccountNumber(DataSet dataSet, TransactionEntry transactionEntry,
	        boolean createNow, long riaNo, AEAmountCodesRIA codesRIA) throws IllegalAccessException, InvocationTargetException {

		logger.debug("Entering");
		newAccount = new IAccounts();
		if(codesRIA == null){
			newAccount.setAcCustCIF(customer.getCustCIF());
		}else{
			newAccount.setAcCustCIF(codesRIA.getCustCIF());
		}
		newAccount.setAcBranch(dataSet.getFinBranch());
		newAccount.setAcCcy(dataSet.getFinCcy());
		newAccount.setFlagCreateIfNF(true);
		newAccount.setFlagCreateNew(false);
		newAccount.setInternalAc(false);
		
		String tranOrder = String.valueOf(transactionEntry.getAccountSetid()+"-"+riaNo + "-"+ transactionEntry.getTransOrder());
		newAccount.setTransOrder(tranOrder);

		//Set Disbursement Account
		if (transactionEntry.getAccount().equals(AccountConstants.TRANACC_DISB)) {

			newAccount.setFlagCreateIfNF(false);
			newAccount.setAccountId(dataSet.getDisburseAccount());
			newAccount.setAcType(AccountConstants.TRANACC_DISB);

			if (!createNow) {
				accountIdList.add(tranOrder);
				accountsMap.put(tranOrder, transactionEntry.getAccount()+"-"+riaNo+"-"+transactionEntry.getTransOrder());
			} else {
				accountsMap.put(tranOrder, transactionEntry.getAccount());
			}

			return newAccount;
		}

		//Set Customer Repayments Account
		if (transactionEntry.getAccount().equals(AccountConstants.TRANACC_REPAY)) {

			newAccount.setFlagCreateIfNF(false);
			newAccount.setAccountId(dataSet.getRepayAccount());
			newAccount.setAcType(AccountConstants.TRANACC_REPAY);
			
			if (!createNow) {
				accountIdList.add(tranOrder);
				accountsMap.put(tranOrder, transactionEntry.getAccount()+"-"+riaNo+"-"+transactionEntry.getTransOrder());
			} else {
				accountsMap.put(tranOrder, transactionEntry.getAccount());
			}

			logger.debug("Leaving");
			return newAccount;
		}
		
		//Set Disbursement Account
		if(transactionEntry.getAccount().equals(AccountConstants.TRANACC_DOWNPAY)){
			
			newAccount.setAcType(AccountConstants.TRANACC_DOWNPAY);
			newAccount.setFlagCreateIfNF(false);
			newAccount.setAccountId(dataSet.getDownPayAccount());
			
			if(!createNow){
				accountsMap.put(tranOrder, transactionEntry.getAccount()+"-"+transactionEntry.getTransOrder());
				accountIdList.add(tranOrder);
			}else{
				accountsMap.put(tranOrder,transactionEntry.getAccount());
			}
			
			return newAccount;
		}
		
		//Set Finance Fee Account
		if(transactionEntry.getAccount().equals(AccountConstants.TRANACC_FEEAC)){

			newAccount.setAcType(AccountConstants.TRANACC_FEEAC);
			newAccount.setFlagCreateIfNF(false);
			newAccount.setAccountId(dataSet.getFeeAccountId());

			if(!createNow){
				accountsMap.put(tranOrder, transactionEntry.getAccount()+"-"+transactionEntry.getTransOrder());
				accountIdList.add(tranOrder);
			}else{
				accountsMap.put(tranOrder,transactionEntry.getAccount());
			}

			return newAccount;
		}
		
		//Set Finance Cancellation Account
		if (transactionEntry.getAccount().equals(AccountConstants.TRANACC_CANFIN)) {

			newAccount.setFlagCreateIfNF(false);
			newAccount.setAccountId(dataSet.getFinCancelAc());
			newAccount.setAcType(AccountConstants.TRANACC_CANFIN);

			if (!createNow) {
				accountIdList.add(tranOrder);
				accountsMap.put(tranOrder, transactionEntry.getAccount()+"-"+riaNo+"-"+transactionEntry.getTransOrder());
			} else {
				accountsMap.put(tranOrder, transactionEntry.getAccount());
			}

			return newAccount;
		}
		
		//Set Writeoff Account
		if (transactionEntry.getAccount().equals(AccountConstants.TRANACC_WRITEOFF)) {
			
			newAccount.setFlagCreateIfNF(false);
			newAccount.setAccountId(dataSet.getFinWriteoffAc());
			newAccount.setAcType(AccountConstants.TRANACC_WRITEOFF);
			
			if (!createNow) {
				accountIdList.add(tranOrder);
				accountsMap.put(tranOrder, transactionEntry.getAccount()+"-"+riaNo+"-"+transactionEntry.getTransOrder());
			} else {
				accountsMap.put(tranOrder, transactionEntry.getAccount());
			}
			
			return newAccount;
		}

		//Set Writeoff Pay Account
		if (transactionEntry.getAccount().equals(AccountConstants.TRANACC_WRITEOFFPAY)) {

			newAccount.setFlagCreateIfNF(false);
			newAccount.setAccountId(dataSet.getFinWriteoffAc());
			newAccount.setAcType(AccountConstants.TRANACC_WRITEOFFPAY);

			if (!createNow) {
				accountIdList.add(tranOrder);
				accountsMap.put(tranOrder, transactionEntry.getAccount()+"-"+riaNo+"-"+transactionEntry.getTransOrder());
			} else {
				accountsMap.put(tranOrder, transactionEntry.getAccount());
			}

			return newAccount;
		}
		//Set GL&PL Account
		if (transactionEntry.getAccount().equals(AccountConstants.TRANACC_GLNPL)) {

			newAccount.setInternalAc(true);
			newAccount.setAcType(transactionEntry.getAccountType());
			
			if(riaNo != 0){
				newAccount.setAccountId(generateAccount(dataSet, transactionEntry, codesRIA.getContributorId() , false));
			}else{
				newAccount.setAccountId(generateAccount(dataSet, transactionEntry, 0, false));
			}

			if (!createNow) {
				accountIdList.add(tranOrder);
				accountsMap.put(tranOrder, transactionEntry.getAccount() +"-"+riaNo+"-"+transactionEntry.getTransOrder());
			} else {
				accountsMap.put(tranOrder, transactionEntry.getAccount()+ transactionEntry.getAccountType());
			}

			logger.debug("Leaving");
			return newAccount;
		}

		//Set Customer Loan Account
		if (transactionEntry.getAccount().equals(AccountConstants.TRANACC_FIN)) {

			newAccount.setAcType(financeType.getFinAcType());
			if (StringUtils.isNotBlank(dataSet.getFinAccount())) {
				newAccount.setAccountId(dataSet.getFinAccount());
			} else {
				if (financeType.isFinIsOpenNewFinAc()) {
					newAccount.setFlagCreateNew(true);
				}
			}

			if (!createNow) {
				accountIdList.add(tranOrder);
				accountsMap.put(tranOrder, transactionEntry.getAccount() +"-"+riaNo+"-"+transactionEntry.getTransOrder());
			} else {
				accountsMap.put(tranOrder, transactionEntry.getAccount());
			}

			logger.debug("Leaving");
			return newAccount;
		}
		
		//Finance Unearned Profit Account
		if (transactionEntry.getAccount().equals(AccountConstants.TRANACC_UNEARN)) {

			newAccount.setAcType(financeType.getPftPayAcType());

			if (!createNow) {
				accountIdList.add(tranOrder);
				accountsMap.put(tranOrder, transactionEntry.getAccount() +"-"+riaNo+"-"+transactionEntry.getTransOrder());
			} else {
				accountsMap.put(tranOrder, transactionEntry.getAccount());
			}

			logger.debug("Leaving");
			return newAccount;
		}
		
		//Finance Unearned Suspense Account
		if (transactionEntry.getAccount().equals(AccountConstants.TRANACC_SUSP)) {

			newAccount.setAcType(financeType.getFinSuspAcType());

			if (!createNow) {
				accountIdList.add(tranOrder);
				accountsMap.put(tranOrder, transactionEntry.getAccount() +"-"+riaNo+"-"+transactionEntry.getTransOrder());
			} else {
				accountsMap.put(tranOrder, transactionEntry.getAccount());
			}

			logger.debug("Leaving");
			return newAccount;
		}
		
		//Finance Provision Account
		if (transactionEntry.getAccount().equals(AccountConstants.TRANACC_PROVSN)) {

			newAccount.setAcType(financeType.getFinProvisionAcType());

			if (!createNow) {
				accountIdList.add(tranOrder);
				accountsMap.put(tranOrder, transactionEntry.getAccount() +"-"+riaNo+"-"+transactionEntry.getTransOrder());
			} else {
				accountsMap.put(tranOrder, transactionEntry.getAccount());
			}

			logger.debug("Leaving");
			return newAccount;
		}

		//Set Customer Loan Account
		if (transactionEntry.getAccount().equals(AccountConstants.TRANACC_CUSTSYS)) {

			newAccount.setFlagCreateNew(transactionEntry.isOpenNewFinAc());
			newAccount.setAcType(transactionEntry.getAccountType());

			if (!createNow) {
				accountIdList.add(tranOrder);
				accountsMap.put(tranOrder, transactionEntry.getAccount() +"-"+riaNo+"-"+transactionEntry.getTransOrder());
			} else {
				accountsMap.put(tranOrder,transactionEntry.getAccount()+ transactionEntry.getAccountType());
			}

			logger.debug("Leaving");
			return newAccount;
		}
		
		//Set Customer Loan Account
		if (transactionEntry.getAccount().equals(AccountConstants.TRANACC_INVSTR)) {

			newAccount.setFlagCreateNew(false);
			newAccount.setAccountId(codesRIA.getAccountId());
			newAccount.setAcType(AccountConstants.TRANACC_INVSTR);
			
			if (!createNow) {
				accountIdList.add(tranOrder);
				accountsMap.put(tranOrder, transactionEntry.getAccount()+"-"+riaNo+"-"+transactionEntry.getTransOrder());
			} else {
				accountsMap.put(tranOrder,transactionEntry.getAccount()+ transactionEntry.getAccountType());
			}

			logger.debug("Leaving");
			return newAccount;
		}

		//Set Customer Deffered Account
		if (transactionEntry.getAccount().equals(AccountConstants.TRANACC_SYSCUST)) {
			
			newAccount.setAcType(financeType.getPftPayAcType());

			if (StringUtils.isNotBlank(dataSet.getFinCustPftAccount())) {
				newAccount.setAccountId(dataSet.getFinCustPftAccount());
			} else {
				if (financeType.isFinIsOpenPftPayAcc()) {
					newAccount.setFlagCreateNew(true);
				}
			}

			if (!createNow) {
				accountIdList.add(tranOrder);
				accountsMap.put(tranOrder, transactionEntry.getAccount()+"-"+riaNo+"-"+transactionEntry.getTransOrder());
			} else {
				accountsMap.put(tranOrder,transactionEntry.getAccount());
			}

			logger.debug("Leaving");
			return newAccount;
		}
		
		//Set Customer Loan Account
		if(transactionEntry.getAccount().equals(AccountConstants.TRANACC_COMMIT)){

			String acType = SysParamUtil.getValueAsString("COMMITMENT_AC_TYPE");
			newAccount.setAcType(acType);
			newAccount.setFlagCreateNew(dataSet.isOpenNewCmtAc());
			newAccount.setAccountId("");
			if(!dataSet.isOpenNewCmtAc()){
				newAccount.setAccountId(StringUtils.trimToEmpty(dataSet.getCmtAccount()));
			}

			if(!dataSet.getFinEvent().equals(AccountEventConstants.ACCEVENT_NEWCMT)){
				newAccount.setFlagCreateNew(false);
				newAccount.setAccountId(StringUtils.trimToEmpty(dataSet.getCmtAccount()));
			}

			if(!createNow){
				accountsMap.put(tranOrder, transactionEntry.getAccount() +"-"+transactionEntry.getTransOrder());
			}else{
				accountsMap.put(tranOrder,transactionEntry.getAccount()+transactionEntry.getAccountType());
			}

			logger.debug("Leaving");
			return newAccount;
		}

		//Set Build Account
		if(transactionEntry.getAccount().equals(AccountConstants.TRANACC_BUILD)){

			newAccount.setAcType(transactionEntry.getAccountType());
			newAccount.setInternalAc(true);
			if(riaNo != 0){
				newAccount.setAccountId(generateAccount(dataSet, transactionEntry, codesRIA.getContributorId() , true));
			}else{
				newAccount.setAccountId(generateAccount(dataSet, transactionEntry, 0, true));
			}

			if(!createNow){
				accountsMap.put(tranOrder, transactionEntry.getAccount() +"-"+transactionEntry.getTransOrder());
			}else{
				accountsMap.put(tranOrder,transactionEntry.getAccount()+transactionEntry.getAccountType());
			}

			logger.debug("Leaving");
			return newAccount;
		}

		logger.debug("Leaving");
		return newAccount;
	}

	/**
	 * Generate Account Number For GLNPL Account
	 * 
	 * @param finBranch
	 * @param finCcy
	 * @param accountType
	 * @param subHeadRuleCode
	 * @param dbOrCr
	 * @return
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	private String generateAccount(DataSet dataSet, TransactionEntry entry, long custID, boolean isBuildAc) throws IllegalAccessException,
	        InvocationTargetException {

		//System Internal Account checking
		String sysIntAcNum = getInternalAccountDefinitionDAO().getSysIntAccNum(entry.getAccountType());

		if (currencyCode == null) {
			currencyCode = getCurrencyDAO().getCurrencyById(dataSet.getFinCcy());
		}

		int length = sysIntAcNum.length() - SysParamUtil.getValueAsInt("SYSINT_ACCOUNT_LEN");
		String accNumber = dataSet.getFinBranch() + sysIntAcNum.substring(length) + currencyCode;

		if (StringUtils.isNotBlank(entry.getAccountSubHeadRule())) {
			String amountRule = getRuleDAO().getAmountRule(entry.getAccountSubHeadRule(), 
					RuleConstants.MODULE_SUBHEAD, RuleConstants.EVENT_SUBHEAD);


			subHeadRule = new SubHeadRule();
			//FIll SubHead Rule Details
			if(entry.isEntryByInvestment()){
				Customer aCustomer = getCustomerDAO().getCustomerForPostings(custID);
				BeanUtils.copyProperties(subHeadRule, aCustomer);
			}else{
				BeanUtils.copyProperties(subHeadRule, customer);
			}

			subHeadRule.setReqProduct(getFinanceType().getFinCategory());
			subHeadRule.setReqFinType(getFinanceType().getFinType());
			
			HashMap<String, Object> fieldsAndValues = subHeadRule.getDeclaredFieldValues();
			
			String subHeadCode = (String) getRuleExecutionUtil().executeRule(amountRule, fieldsAndValues,
					dataSet.getFinCcy(), RuleReturnType.STRING);
			if (StringUtils.trimToEmpty(subHeadCode).contains(".")) {
				subHeadCode = subHeadCode.substring(0, subHeadCode.indexOf('.'));
			}
			
			if(isBuildAc){
				logger.debug("Leaving");
				return subHeadCode;
			}

			if (StringUtils.isBlank(subHeadCode)) {
				logger.debug("Leaving");
				return accNumber;
			} else {
				String sIANumber = sysIntAcNum.substring(length, sysIntAcNum.length() - subHeadCode.length());
				logger.debug("Leaving");
				return dataSet.getFinBranch() + sIANumber + subHeadCode + currencyCode;
			}

		} else {
			logger.debug("Leaving");
			return accNumber;
		}
	}
	
	/**
	 * Method for Calculate the Amount Codes By Execution Formulae
	 * 
	 * @param amountCode
	 * @return
	 */
	private void setAmountCodesFB(AEAmountCodes aeAmountCodes) {
		logger.debug("Entering");
		
		// Finance Details 
		amountCodesRIAFB.setDISBURSE(aeAmountCodes.getDisburse());
		amountCodesRIAFB.setFACRUE(aeAmountCodes.getAccrue());
		amountCodesRIAFB.setFDACRUE(aeAmountCodes.getDAccrue());
		amountCodesRIAFB.setFNACRUE(aeAmountCodes.getNAccrue());	
		amountCodesRIAFB.setFLACRUE(aeAmountCodes.getlAccrue());	
		amountCodesRIAFB.setFPFT(aeAmountCodes.getPft());	
		amountCodesRIAFB.setFPFTAB(aeAmountCodes.getPftAB());	
		amountCodesRIAFB.setFPFTAP(aeAmountCodes.getPftAP());	
		amountCodesRIAFB.setFPFTCHG(aeAmountCodes.getPftChg());	
		amountCodesRIAFB.setFPFTS(aeAmountCodes.getPftS());	
		amountCodesRIAFB.setFPFTSB(aeAmountCodes.getPftSB());	
		amountCodesRIAFB.setFPFTSP(aeAmountCodes.getPftSP());	
		amountCodesRIAFB.setFPNLTY(aeAmountCodes.getPENALTY());	
		amountCodesRIAFB.setFPRI(aeAmountCodes.getPri());	
		amountCodesRIAFB.setFPRIAB(aeAmountCodes.getPriAB());	
		amountCodesRIAFB.setFPRIAP(aeAmountCodes.getPriAP());	
		amountCodesRIAFB.setFPRIS(aeAmountCodes.getPriS());	
		amountCodesRIAFB.setFPRISB(aeAmountCodes.getPriSB());	
		amountCodesRIAFB.setFPRISP(aeAmountCodes.getPriSP());	
		amountCodesRIAFB.setFREFUND(aeAmountCodes.getRefund());	
		amountCodesRIAFB.setFRPPFT(aeAmountCodes.getRpPft());	
		amountCodesRIAFB.setFRPPRI(aeAmountCodes.getRpPri());	
		amountCodesRIAFB.setFWAIVER(aeAmountCodes.getWAIVER());	
		
		amountCodesRIAFB.setFRPMF(iMFRepayTotal);	
		amountCodesRIAFB.setFMFACR(iAccrueMFTotal);
		amountCodesRIAFB.setFMFCHG(iPftChgTotal);	
		amountCodesRIAFB.setFMFDACR(iDAccrueMFTotal);	
		amountCodesRIAFB.setFMFNACR(iNAccrueMFTotal);	
		amountCodesRIAFB.setFMUDFEE(iMFeeTotal);	

		// Bank Details 
		amountCodesRIAFB.setBACRUE(aeAmountCodes.getAccrue().subtract(iAccrueTotal));	
		amountCodesRIAFB.setBDACRUE(aeAmountCodes.getDAccrue().subtract(iDAccrueTotal));	
		amountCodesRIAFB.setBNACRUE(aeAmountCodes.getNAccrue().subtract(iNAccrueTotal));	
		amountCodesRIAFB.setBNKINV(aeAmountCodes.getDisburse().subtract(iInvestTotal));	
		amountCodesRIAFB.setBPFT(aeAmountCodes.getPft().subtract(iPftTotal));	
		amountCodesRIAFB.setBPFTAB(aeAmountCodes.getPftAB().subtract(iPftABTotal));	
		amountCodesRIAFB.setBPFTAP(aeAmountCodes.getPftAP().subtract(iPftAPTotal));	
		amountCodesRIAFB.setBPFTCHG(aeAmountCodes.getPftChg().subtract(iPftChgTotal));	
		amountCodesRIAFB.setBPFTS(aeAmountCodes.getPftS().subtract(iPftSTotal));	
		amountCodesRIAFB.setBPFTSB(aeAmountCodes.getPftSB().subtract(iPftSBTotal));	
		amountCodesRIAFB.setBPFTSP(aeAmountCodes.getPftSP().subtract(iPftSPTotal));	
		amountCodesRIAFB.setBPNLTY(aeAmountCodes.getPENALTY().subtract(iPenaltyTotal));	
		amountCodesRIAFB.setBPRI(aeAmountCodes.getPri().subtract(iPriTotal));	
		amountCodesRIAFB.setBPRIAB(aeAmountCodes.getPriAB().subtract(iPriABTotal));	
		amountCodesRIAFB.setBPRIAP(aeAmountCodes.getPriAP().subtract(iPriAPTotal));	
		amountCodesRIAFB.setBPRIS(aeAmountCodes.getPriS().subtract(iPriSTotal));	
		amountCodesRIAFB.setBPRISB(aeAmountCodes.getPriSB().subtract(iPriSBTotal));	
		amountCodesRIAFB.setBPRISP(aeAmountCodes.getPriSP().subtract(iPriSPTotal));	
		amountCodesRIAFB.setBREFUND(aeAmountCodes.getRefund().subtract(iRefundTotal));	
		amountCodesRIAFB.setBRPPFT(aeAmountCodes.getRpPft().subtract(iRpPftTotal));	
		amountCodesRIAFB.setBRPPRI(aeAmountCodes.getRpPri().subtract(iRpPriTotal));	
		amountCodesRIAFB.setBWAIVER(aeAmountCodes.getWAIVER().subtract(iWaiverTotal));	
		
		amountCodesRIAFB.setTTLDAYS(aeAmountCodes.getTtlDays());
		amountCodesRIAFB.setTTLMNTS(aeAmountCodes.getTtlMnts());
		amountCodesRIAFB.setTTLTERMS(aeAmountCodes.getTtlTerms());
		amountCodesRIAFB.setODCPLShare(aeAmountCodes.getODCPLShare());
		amountCodesRIAFB.setSUSPNOW(aeAmountCodes.getSUSPNOW());
		amountCodesRIAFB.setSUSPRLS(aeAmountCodes.getSUSPRLS());
		
		logger.debug("Leaving");
	}

	/**
	 * Method for Execution Of Fee & Charges Rules
	 * 
	 * @param dataSet
	 * @param amountCode
	 * @return
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	private List<AEAmountCodesRIA> calRIAAmountCodes(DataSet dataSet, AEAmountCodes aeAmountCodes,
	        List<AEAmountCodesRIA> riaDetailList) throws IllegalAccessException,
	        InvocationTargetException {
		logger.debug("Entering");
		
		BigDecimal actualTotalSchdProfit = BigDecimal.ZERO;
		BigDecimal actualTotalCpzProfit = BigDecimal.ZERO;

		if(!dataSet.isNewRecord()){
 			List<BigDecimal> list= getFinanceMainDAO().getActualPftBal(aeAmountCodes.getFinReference(),"");
			actualTotalSchdProfit = list.get(0);
			actualTotalCpzProfit = list.get(1);
		}

		aeAmountCodes.setDisburse(dataSet.getDisburseAmount());
		aeAmountCodes.setPftChg(aeAmountCodes.getPft().subtract(actualTotalSchdProfit));
		aeAmountCodes.setCpzChg(aeAmountCodes.getCpzTot().subtract(actualTotalCpzProfit));

		for (int i = 0; i < riaDetailList.size(); i++) {
			AEAmountCodesRIA aeRIA = riaDetailList.get(i);

			aeRIA.setINVAMT(aeRIA.getInvestment());
			if(dataSet.getDisburseAmount() != null && dataSet.getDisburseAmount().compareTo(BigDecimal.ZERO) != 0){
				aeRIA.setIACRUE(aeAmountCodes.getAccrue().multiply(
						aeRIA.getInvestment()).divide(dataSet.getDisburseAmount(),0,
								RoundingMode.HALF_DOWN));

				aeRIA.setIACRUES(aeAmountCodes.getAccrueS().multiply(
						aeRIA.getInvestment()).divide(dataSet.getDisburseAmount(),0,
								RoundingMode.HALF_DOWN));
				aeRIA.setIDACRUE(aeAmountCodes.getDAccrue().multiply(
						aeRIA.getInvestment()).divide(dataSet.getDisburseAmount(),0,
								RoundingMode.HALF_DOWN));
				aeRIA.setINACRUE(aeAmountCodes.getNAccrue().multiply(
						aeRIA.getInvestment()).divide(dataSet.getDisburseAmount(),0,
								RoundingMode.HALF_DOWN));
				aeRIA.setILACRUE(aeAmountCodes.getlAccrue().multiply(
						aeRIA.getInvestment()).divide(dataSet.getDisburseAmount(),0,
								RoundingMode.HALF_DOWN));
				aeRIA.setIPFT(aeAmountCodes.getPft().multiply(
						aeRIA.getInvestment()).divide(dataSet.getDisburseAmount(),0,
								RoundingMode.HALF_DOWN));
				aeRIA.setIPFTAB(aeAmountCodes.getPftAB().multiply(
						aeRIA.getInvestment()).divide(dataSet.getDisburseAmount(),0,
								RoundingMode.HALF_DOWN));
				aeRIA.setIPFTAP(aeAmountCodes.getPftAP().multiply(
						aeRIA.getInvestment()).divide(dataSet.getDisburseAmount(),0,
								RoundingMode.HALF_DOWN));
				aeRIA.setICPZCHG(aeAmountCodes.getCpzChg().multiply(
						aeRIA.getInvestment()).divide(dataSet.getDisburseAmount(),0,
								RoundingMode.HALF_DOWN));
				aeRIA.setIPFTCHG(aeAmountCodes.getPftChg().multiply(
						aeRIA.getInvestment()).divide(dataSet.getDisburseAmount(),0,
								RoundingMode.HALF_DOWN));
				aeRIA.setIPFTS(aeAmountCodes.getPftS().multiply(
						aeRIA.getInvestment()).divide(dataSet.getDisburseAmount(),0,
								RoundingMode.HALF_DOWN));
				aeRIA.setIPFTSB(aeAmountCodes.getPftSB().multiply(
						aeRIA.getInvestment()).divide(dataSet.getDisburseAmount(),0,
								RoundingMode.HALF_DOWN));
				aeRIA.setIPFTSP(aeAmountCodes.getPftSP().multiply(
						aeRIA.getInvestment()).divide(dataSet.getDisburseAmount(),0,
								RoundingMode.HALF_DOWN));
				aeRIA.setIPRI(aeAmountCodes.getPri().multiply(
						aeRIA.getInvestment()).divide(dataSet.getDisburseAmount(),0,
								RoundingMode.HALF_DOWN));
				aeRIA.setIPRIAB(aeAmountCodes.getPriAB().multiply(
						aeRIA.getInvestment()).divide(dataSet.getDisburseAmount(),0,
								RoundingMode.HALF_DOWN));
				aeRIA.setIPRIAP(aeAmountCodes.getPriAP().multiply(
						aeRIA.getInvestment()).divide(dataSet.getDisburseAmount(),0,
								RoundingMode.HALF_DOWN));
				aeRIA.setIPRIS(aeAmountCodes.getPriS().multiply(
						aeRIA.getInvestment()).divide(dataSet.getDisburseAmount(),0,
								RoundingMode.HALF_DOWN));
				aeRIA.setIPRISB(aeAmountCodes.getPriSB().multiply(
						aeRIA.getInvestment()).divide(dataSet.getDisburseAmount(),0,
								RoundingMode.HALF_DOWN));
				aeRIA.setIPRISP(aeAmountCodes.getPriSP().multiply(
						aeRIA.getInvestment()).divide(dataSet.getDisburseAmount(),0,
								RoundingMode.HALF_DOWN));
				aeRIA.setIRPPFT(aeAmountCodes.getRpPft().multiply(
						aeRIA.getInvestment()).divide(dataSet.getDisburseAmount(),0,
								RoundingMode.HALF_DOWN));
				aeRIA.setIRPPRI(aeAmountCodes.getRpPri().multiply(
						aeRIA.getInvestment()).divide(dataSet.getDisburseAmount(),0,
								RoundingMode.HALF_DOWN));
				aeRIA.setIRPTOT(aeAmountCodes.getRpTot().multiply(
						aeRIA.getInvestment()).divide(dataSet.getDisburseAmount(),0,
								RoundingMode.HALF_DOWN));
				aeRIA.setIREFUND(aeAmountCodes.getRefund().multiply(
						aeRIA.getInvestment()).divide(dataSet.getDisburseAmount(),0,
								RoundingMode.HALF_DOWN));
				aeRIA.setICPZTOT(aeAmountCodes.getCpzTot().multiply(
						aeRIA.getInvestment()).divide(dataSet.getDisburseAmount(),0,
								RoundingMode.HALF_DOWN));
				aeRIA.setICPZPRV(aeAmountCodes.getCpzPrv().multiply(
						aeRIA.getInvestment()).divide(dataSet.getDisburseAmount(),0,
								RoundingMode.HALF_DOWN));
				aeRIA.setICPZCUR(aeAmountCodes.getCpzCur().multiply(
						aeRIA.getInvestment()).divide(dataSet.getDisburseAmount(),0,
								RoundingMode.HALF_DOWN));
				aeRIA.setICPZNXT(aeAmountCodes.getCpzNxt().multiply(
						aeRIA.getInvestment()).divide(dataSet.getDisburseAmount(),0,
								RoundingMode.HALF_DOWN));
				aeRIA.setIPFTINADV(aeAmountCodes.getPftInAdv().multiply(
						aeRIA.getInvestment()).divide(dataSet.getDisburseAmount(),0,
								RoundingMode.HALF_DOWN));
				
				aeRIA.setIPNLTY(aeAmountCodes.getPENALTY().multiply(
				        aeRIA.getInvestment()).divide(dataSet.getDisburseAmount(),0,
				                RoundingMode.HALF_DOWN));
				aeRIA.setIWAIVER(aeAmountCodes.getWAIVER().multiply(
				        aeRIA.getInvestment()).divide(dataSet.getDisburseAmount(),0,
				                RoundingMode.HALF_DOWN));
			}

			aeRIA.setIMFACR(aeRIA.getIACRUE().multiply(aeRIA.getMudaribPercent())
			        .divide(new BigDecimal(100), 0,RoundingMode.HALF_DOWN));
			aeRIA.setIMFACRS(aeRIA.getIACRUES().multiply(aeRIA.getMudaribPercent())
			        .divide(new BigDecimal(100),0, RoundingMode.HALF_DOWN));
			aeRIA.setIMFDACR(aeRIA.getIDACRUE().multiply(aeRIA.getMudaribPercent())
			        .divide(new BigDecimal(100),0, RoundingMode.HALF_DOWN));
			aeRIA.setIMFNACR(aeRIA.getINACRUE().multiply(aeRIA.getMudaribPercent())
			        .divide(new BigDecimal(100),0, RoundingMode.HALF_DOWN));
			aeRIA.setIMFLACR(aeRIA.getILACRUE().multiply(aeRIA.getMudaribPercent())
			        .divide(new BigDecimal(100),0, RoundingMode.HALF_DOWN));
			aeRIA.setIMFCHG(aeRIA.getIPFTCHG().multiply(aeRIA.getMudaribPercent())
			        .divide(new BigDecimal(100),0, RoundingMode.HALF_DOWN));
			aeRIA.setIMUDFEE(aeRIA.getIPFT().multiply(aeRIA.getMudaribPercent())
			        .divide(new BigDecimal(100),0, RoundingMode.HALF_DOWN));

			aeRIA.setIRPMF(aeRIA.getIRPPFT().multiply(aeRIA.getMudaribPercent())
			        .divide(new BigDecimal(100),0, RoundingMode.HALF_DOWN));
			

			iInvestTotal  = iInvestTotal.add(aeRIA.getINVAMT());
			iAccrueTotal  = iAccrueTotal.add(aeRIA.getIACRUE());
			iAccrueSTotal  = iAccrueSTotal.add(aeRIA.getIACRUES());
			iDAccrueTotal  = iDAccrueTotal.add(aeRIA.getIDACRUE());
			iNAccrueTotal  = iNAccrueTotal.add(aeRIA.getINACRUE());
			iLAccrueTotal  = iLAccrueTotal.add(aeRIA.getILACRUE());
			iPftTotal  = iPftTotal.add(aeRIA.getIPFT());
			iPftABTotal  = iPftABTotal.add(aeRIA.getIPFTAB());
			iPftAPTotal  = iPftAPTotal.add(aeRIA.getIPFTAP());
			iCpzChgTotal  = iCpzChgTotal.add(aeRIA.getICPZCHG());
			iPftChgTotal  = iPftChgTotal.add(aeRIA.getIPFTCHG());
			iPftSTotal  = iPftSTotal.add(aeRIA.getIPFTS());
			iPftSBTotal  = iPftSBTotal.add(aeRIA.getIPFTSB());
			iPftSPTotal  = iPftSPTotal.add(aeRIA.getIPFTSP());
			iPriTotal  = iPriTotal.add(aeRIA.getIPRI());
			iPriABTotal  = iPriABTotal.add(aeRIA.getIPRIAB());
			iPriAPTotal  = iPriAPTotal.add(aeRIA.getIPRIAP());
			iPriSTotal  = iPriSTotal.add(aeRIA.getIPRIS());
			iPriSBTotal  = iPriSBTotal.add(aeRIA.getIPRISB());
			iPriSPTotal  = iPriSPTotal.add(aeRIA.getIPRISP());
			iRpPftTotal  = iRpPftTotal.add(aeRIA.getIRPPFT());
			iRpPriTotal  = iRpPriTotal.add(aeRIA.getIRPPRI());
			iRpTotTotal  = iRpTotTotal.add(aeRIA.getIRPTOT());
			iRefundTotal  = iRefundTotal.add(aeRIA.getIREFUND());
			iCpzTotTotal  = iCpzTotTotal.add(aeRIA.getICPZTOT());
			iCpzPrvTotal  = iCpzPrvTotal.add(aeRIA.getICPZPRV());
			iCpzCurTotal  = iCpzCurTotal.add(aeRIA.getICPZCUR());
			iCpzNxtTotal  = iCpzNxtTotal.add(aeRIA.getICPZNXT());
			iPftInAdvTotal  = iPftInAdvTotal.add(aeRIA.getIPFTINADV());
			iAccrueMFTotal  = iAccrueMFTotal.add(aeRIA.getIMFACR());
			iAccrueSMFTotal  = iAccrueSMFTotal.add(aeRIA.getIMFACRS());
			iDAccrueMFTotal  = iDAccrueMFTotal.add(aeRIA.getIMFDACR());
			iNAccrueMFTotal  = iNAccrueMFTotal.add(aeRIA.getIMFNACR());
			iLAccrueMFTotal  = iLAccrueMFTotal.add(aeRIA.getIMFLACR());
			iMFfeeChgTotal  = iMFfeeChgTotal.add(aeRIA.getIMFCHG());
			iMFeeTotal  = iMFeeTotal.add(aeRIA.getIMUDFEE());
			iMFRepayTotal  = iMFRepayTotal.add(aeRIA.getIRPMF());
			iPenaltyTotal  = iPenaltyTotal.add(aeRIA.getIPNLTY());
			iWaiverTotal  = iWaiverTotal.add(aeRIA.getIWAIVER());
			
		}

		return riaDetailList;
	}
	
	/**
	 * Method for Execution of Amount Rule and Getting Amount
	 * 
	 *  <br> IN AccountEngineExecution.java
	 * @param event
	 * @param transactionEntry
	 * @param feeRuleDetailMap
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException  BigDecimal
	 */
	private String executeAmountRule(String event, TransactionEntry transactionEntry ,
			Map<String, FeeRule> feeRuleDetailMap, String finCcy,HashMap<String, Object> declaredFieldValues) throws IllegalAccessException, InvocationTargetException{
		logger.debug("Entering");

		//Execute Transaction Entry Rule
		BigDecimal amount = BigDecimal.ZERO;
		if(event.startsWith(AccountEventConstants.ACCEVENT_ADDDBS)){
			event = AccountEventConstants.ACCEVENT_ADDDBS;
		}

		String amountRule = transactionEntry.getAmountRule();
		String[] res = StringUtils.trimToEmpty(transactionEntry.getFeeCode()).split(",");
		
		if (res.length > 0) {
			for (int i = 0; i < res.length; i++) {
				if (StringUtils.isNotBlank(res[i]) || "Result".equalsIgnoreCase(res[i])) {
					if(feeRuleDetailMap != null && feeRuleDetailMap.containsKey(res[i].trim().substring(0,
							res[i].trim().contains("_") ? res[i].trim().indexOf('_'): res[i].trim().length()))){
						FeeRule feeRule = feeRuleDetailMap.get(res[i].trim().substring(0,
								res[i].trim().contains("_") ? res[i].trim().indexOf('_'): res[i].trim().length()));
						
						if(amountRule.contains(res[i].trim()+"_W")){
							amountRule = amountRule.replace(res[i].trim()+"_W",feeRule.getWaiverAmount().toString());
						}
						if(amountRule.contains(res[i].trim()+"_C")){
							amountRule = amountRule.replace(res[i].trim()+"_C",feeRule.getFeeAmount().toString());
						}
						if(amountRule.contains(res[i].trim()+"_P")){
							amountRule = amountRule.replace(res[i].trim()+"_P",feeRule.getPaidAmount().toString());
						}
						if(amountRule.contains(res[i].trim()+"_AF")){
							if(StringUtils.equals(feeRule.getFeeToFinance(), RuleConstants.DFT_FEE_FINANCE)){
								amountRule = amountRule.replace(res[i].trim()+"_AF",(feeRule.getFeeAmount().subtract(
										feeRule.getWaiverAmount()).subtract(feeRule.getPaidAmount())).toString());
							}else{
								amountRule = amountRule.replace(res[i].trim()+"_AF","0");
							}
						}
						
					}else{

						Rule rule = getRuleDAO().getRuleByID(res[i].trim(), RuleConstants.MODULE_FEES ,event , "");


						if(rule != null){
							try {
								amount = (BigDecimal) getRuleExecutionUtil().executeRule(rule.getSQLRule(), declaredFieldValues,
										finCcy, RuleReturnType.DECIMAL);
								
								if(amountRule.contains(res[i].trim()+"_W")){
									amountRule = amountRule.replace(res[i].trim()+"_W","0");
								}
								if(amountRule.contains(res[i].trim()+"_C")){
									amountRule = amountRule.replace(res[i].trim()+"_C",amount.toString());
								}
								if(amountRule.contains(res[i].trim()+"_P")){
									amountRule = amountRule.replace(res[i].trim()+"_P","0");
								}
								if(amountRule.contains(res[i].trim()+"_AF")){
									if(StringUtils.equals(rule.getFeeToFinance(), RuleConstants.DFT_FEE_FINANCE)){
										amountRule = amountRule.replace(res[i].trim()+"_AF",amount.toString());
									}else{
										amountRule = amountRule.replace(res[i].trim()+"_AF","0");
									}
								}
								
							}catch (Exception e) {
								logger.error("Exception: ", e);
							}
						}
					
					}
				}
			}
		}

		logger.debug("Leaving");
		return amountRule;
	}
	
	

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setFinanceType(FinanceType financeType) {
		this.financeType = financeType;
	}

	public FinanceType getFinanceType() {
		return financeType;
	}

	public SystemInternalAccountDefinitionDAO getInternalAccountDefinitionDAO() {
		return internalAccountDefinitionDAO;
	}

	public void setInternalAccountDefinitionDAO(
	        SystemInternalAccountDefinitionDAO internalAccountDefinitionDAO) {
		this.internalAccountDefinitionDAO = internalAccountDefinitionDAO;
	}

	public void setRuleDAO(RuleDAO ruleDAO) {
		this.ruleDAO = ruleDAO;
	}

	public RuleDAO getRuleDAO() {
		return ruleDAO;
	}

	public void setFinanceTypeDAO(FinanceTypeDAO financeTypeDAO) {
		this.financeTypeDAO = financeTypeDAO;
	}

	public FinanceTypeDAO getFinanceTypeDAO() {
		return financeTypeDAO;
	}

	public TransactionEntryDAO getTransactionEntryDAO() {
		return transactionEntryDAO;
	}

	public void setTransactionEntryDAO(TransactionEntryDAO transactionEntryDAO) {
		this.transactionEntryDAO = transactionEntryDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public FinanceMainDAO getFinanceMainDAO() {
		return financeMainDAO;
	}

	public void setFinanceMain(FinanceMain financeMain) {
		this.financeMain = financeMain;
	}

	public FinanceMain getFinanceMain() {
		return financeMain;
	}

	public CustomerDAO getCustomerDAO() {
		return customerDAO;
	}

	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

	public void setCurrencyDAO(CurrencyDAO currencyDAO) {
		this.currencyDAO = currencyDAO;
	}

	public CurrencyDAO getCurrencyDAO() {
		return currencyDAO;
	}

	public void setAccountInterfaceService(AccountInterfaceService accountInterfaceService) {
		this.accountInterfaceService = accountInterfaceService;
	}

	public AccountInterfaceService getAccountInterfaceService() {
		return accountInterfaceService;
	}

	public void setRuleExecutionUtil(RuleExecutionUtil ruleExecutionUtil) {
		this.ruleExecutionUtil = ruleExecutionUtil;
	}

	public RuleExecutionUtil getRuleExecutionUtil() {
		return ruleExecutionUtil;
	}

	public FinTypeAccountingDAO getFinTypeAccountingDAO() {
		return finTypeAccountingDAO;
	}

	public void setFinTypeAccountingDAO(FinTypeAccountingDAO finTypeAccountingDAO) {
		this.finTypeAccountingDAO = finTypeAccountingDAO;
	}

}
