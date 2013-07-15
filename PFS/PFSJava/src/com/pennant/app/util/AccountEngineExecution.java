/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *
 * FileName    		:  AccountEngineExecution.java													*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  26-04-2011															*
 *                                                                  
 * Modified Date    :  30-07-2011															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-04-2011       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennant.app.util;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.Interface.model.IAccounts;
import com.pennant.Interface.service.AccountInterfaceService;
import com.pennant.backend.dao.applicationmaster.CurrencyDAO;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.finance.DefermentHeaderDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.FinanceSuspHeadDAO;
import com.pennant.backend.dao.masters.SystemInternalAccountDefinitionDAO;
import com.pennant.backend.dao.rmtmasters.AccountingSetDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.dao.rmtmasters.TransactionEntryDAO;
import com.pennant.backend.dao.rulefactory.OverdueChargeDAO;
import com.pennant.backend.dao.rulefactory.RuleDAO;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.commitment.Commitment;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.masters.SystemInternalAccountDefinition;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rmtmasters.TransactionEntry;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AECommitment;
import com.pennant.backend.model.rulefactory.DataSet;
import com.pennant.backend.model.rulefactory.DataSetFiller;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.model.rulefactory.Fees;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.model.rulefactory.SubHeadRule;
import com.pennant.backend.util.PennantConstants;
import com.pennant.coreinterface.exception.AccountNotFoundException;

public class AccountEngineExecution implements Serializable {

    private static final long serialVersionUID = 852062955563015315L;
	private Logger logger = Logger.getLogger(AccountEngineExecution.class);

	private FinanceTypeDAO financeTypeDAO;	
	private FinanceMainDAO financeMainDAO;	
	private TransactionEntryDAO transactionEntryDAO;
	private SystemInternalAccountDefinitionDAO internalAccountDefinitionDAO;
	private RuleDAO ruleDAO;
	private CustomerDAO customerDAO;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private CurrencyDAO currencyDAO;
	private DefermentHeaderDAO defermentHeaderDAO;
	private FinanceSuspHeadDAO financeSuspHeadDAO;
	private OverdueChargeDAO overdueChargeDAO;
	private AccountInterfaceService accountInterfaceService;
	private RuleExecutionUtil ruleExecutionUtil;
	private AccountingSetDAO accountingSetDAO;

	private FinanceType financeType;
	private FinanceMain financeMain;
	private Customer customer = null;
	private Fees fees = null;
	private DataSetFiller dataSetFiller = new DataSetFiller();
	private AECommitment aeCommitment = null;
	private SubHeadRule subHeadRule = null;
	private ValueLabel currency = null;
	List<IAccounts> accountsList = new ArrayList<IAccounts>();  
	private IAccounts newAccount = null;
	private Map<String, Object> accountsMap = new HashMap<String, Object>();
	private List<String> accountIdList = new ArrayList<String>();  

	//Default Constructor
	public AccountEngineExecution() {
		super();
	}

	/**
	 * Method for Execution of Accounting Sets depend on Event 
	 * @param dataSet
	 * @param aeAmountCodes
	 * @param type
	 * @return
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws AccountNotFoundException 
	 */
	public List<ReturnDataSet> getAccEngineExecResults(DataSet dataSet,AEAmountCodes aeAmountCodes
			,String createNow, Map<String, FeeRule> feeRuleDetailMap) throws AccountNotFoundException, IllegalAccessException, InvocationTargetException{
		
		accountIdList = new ArrayList<String>(); 
		accountsList = new ArrayList<IAccounts>();  
		accountsMap = new HashMap<String, Object>();
		dataSetFiller = new DataSetFiller();

		//Fill Amount Code Detail Object with Respect to Schedule Details
		String accountingSetId = prepareAmountCodes(dataSet,aeAmountCodes);
		
		List<TransactionEntry> transactionEntries = getTransactionEntryDAO().getListTransactionEntryById(
				Long.valueOf(accountingSetId), "_AEView",true);
		
		List<ReturnDataSet> returnList = getPrepareAccountingSetResults(dataSet,
				transactionEntries,createNow,false, feeRuleDetailMap);
		resetVariables();
		return returnList;
	}
	
	/**
	 * Method for Execution Stage entries for RIA Investments
	 * @param dataSet
	 * @param aeAmountCodes
	 * @param createNow
	 * @param feeRuleDetailMap
	 * @return
	 * @throws AccountNotFoundException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public List<ReturnDataSet> getStageExecResults(DataSet dataSet,AEAmountCodes aeAmountCodes
			,String createNow, String roleCode, Map<String, FeeRule> feeRuleDetailMap) throws AccountNotFoundException, IllegalAccessException, InvocationTargetException{
		
		accountIdList = new ArrayList<String>(); 
		accountsList = new ArrayList<IAccounts>();  
		accountsMap = new HashMap<String, Object>();
		dataSetFiller = new DataSetFiller();

		//Fill Amount Code Detail Object with Respect to Schedule Details
		fees = new Fees(); 
		subHeadRule = new SubHeadRule();
		financeType = getFinanceTypeDAO().getFinanceTypeByFinType(dataSet.getFinType());

		//Fill Amount Code Detail Object with FinanceMain Object
		doFillDataSetFiller(dataSet);
		setAmountCodes(aeAmountCodes,dataSet.isNewRecord());//set data to DataSetFiller Object		

		List<TransactionEntry> transactionEntries = getTransactionEntryDAO().getListTransactionEntryByRefType(
				dataSet.getFinType(), "5", roleCode,  "_AEView",true);
		
		List<ReturnDataSet> returnList = getPrepareAccountingSetResults(dataSet,
				transactionEntries,createNow,false, feeRuleDetailMap);
		resetVariables();
		return returnList;
	}

	/**
	 * Reset Data Objects after Compleion of Execution
	 */
	private void resetVariables() {
		
		financeType = null;
		financeMain = null;
		customer = null;
		fees = null;
		dataSetFiller = new DataSetFiller();
		subHeadRule = null;
		currency = null;
		newAccount = null;
		accountsMap = new HashMap<String, Object>();
		accountIdList = new ArrayList<String>();  
	    
    }

	/**
	 * Method for Execution Of Fee & Charges Rules
	 * @param dataSet
	 * @param amountCode
	 * @return
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	public List<FeeRule> getFeeChargesExecResults(DataSet dataSet,
			AEAmountCodes aeAmountCodes, int formatter) throws IllegalAccessException, InvocationTargetException{
		logger.debug("Entering");
		
		//Initialization of Bean Properties
		accountIdList = new ArrayList<String>(); 
		accountsList = new ArrayList<IAccounts>();  
		accountsMap = new HashMap<String, Object>();
		dataSetFiller = new DataSetFiller();

		//Prepare AmountCode Details
		String accountingSetId = prepareAmountCodes(dataSet, aeAmountCodes);

		List<FeeRule> feeRules = new ArrayList<FeeRule>();
		String ruleEvent= dataSet.getFinEvent();
		if(ruleEvent.startsWith("ADDDBS")){
			ruleEvent = "ADDDBS";
		}
		
		List<Rule> ruleList = getTransactionEntryDAO().getListFeeChargeRules(Long.valueOf(accountingSetId),
				ruleEvent, "_AView");
		
		FeeRule feeRule ;
		for (Rule rule : ruleList) {
			feeRule = new FeeRule();
			
			//Set Object Data of ReturnDataSet(s)
			feeRule.setFeeCode(rule.getRuleCode());
			feeRule.setFeeCodeDesc(rule.getRuleCodeDesc());
			feeRule.setAddFeeCharges(rule.isAddFeeCharges());
			feeRule.setFeeOrder(rule.getSeqOrder());
			
			BigDecimal amount = BigDecimal.ZERO;
			if(!fees.isProcessed()){
				fees.setProcessed(true);
				BeanUtils.copyProperties(fees, dataSetFiller);
			}

			if(rule != null){
				Object result = getRuleExecutionUtil().executeRule(rule.getSQLRule(), fees, SystemParameterDetails.getGlobaVariableList());
				amount = new BigDecimal(result == null ? "0" : result.toString());
				
				if(rule.isAddFeeCharges()){
					fees.setDISBURSE(fees.getDISBURSE().add(amount));
				}
			}
			
			feeRule.setFeeAmount(amount);
			feeRules.add(feeRule);
		}
		
		resetVariables();
		logger.debug("Leaving");
		return feeRules;
	}

	/**
	 * Method For execution of Provision Rule For Provision Calculated Amount
	 * @param dataSet
	 * @param aeAmountCodes
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public BigDecimal getProvisionExecResults(DataSet dataSet, 
			AEAmountCodes aeAmountCodes) throws IllegalAccessException, InvocationTargetException{

		//Fill Amount Code Detail Object with FinanceMain Object
		dataSetFiller = new DataSetFiller();
		financeType = getFinanceTypeDAO().getFinanceTypeByFinType(dataSet.getFinType());
		doFillDataSetFiller(dataSet);
		setAmountCodes(aeAmountCodes,dataSet.isNewRecord());//set data to DataSetFiller Object	
		BigDecimal provCalAmount = BigDecimal.ZERO;

		Rule rule = getRuleDAO().getRuleByID("PROV", "PROVSN", "", "_AView");
		if(rule != null){
			String sqlRule = rule.getSQLRule();
			Object result = getRuleExecutionUtil().executeRule(sqlRule, dataSetFiller,SystemParameterDetails.getGlobaVariableList());
			provCalAmount = new BigDecimal(result == null ? "0" : result.toString());
		}
		
		resetVariables();
		return provCalAmount;
	}
	
	/**
	 * Method for Commitment posting Entries Execution
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws AccountNotFoundException 
	 */
	public List<ReturnDataSet> getCommitmentExecResults(AECommitment aeCommitment, Commitment commitment, String acSetEvent, 
			String createNow, Map<String, FeeRule> feeRuleDetailMap) throws AccountNotFoundException, IllegalAccessException, InvocationTargetException{
		
		logger.debug("Entering");
		
		//DataSet Object preparation
		DataSet dataSet = new DataSet();
		dataSet.setFinReference(commitment.getCmtReference());
		dataSet.setFinEvent(acSetEvent);
		dataSet.setPostDate(DateUtility.getSystemDate());
		dataSet.setValueDate(DateUtility.getSystemDate());
		dataSet.setFinBranch(commitment.getCmtBranch());
		dataSet.setFinCcy(commitment.getCmtCcy());
		dataSet.setCmtAccount(commitment.getCmtAccount());
		dataSet.setCustId(commitment.getCustID());		
		dataSet.setOpenNewCmtAc(commitment.isOpenAccount());
		dataSet.setCmtChargeAccount(commitment.getChargesAccount());
		dataSet.setCmtChargeAmount(commitment.getCmtCharges());
		
		//Commitment Execution Data Preparation
		doFillCommitmentData(dataSet, commitment, aeCommitment);
			
		//Accounting Set Details
		List<TransactionEntry> transactionEntries = null;
		long accountingSetId = getAccountingSetDAO().getAccountingSetId(acSetEvent, acSetEvent);
		if (accountingSetId != 0) {
			//get List of transaction entries
			transactionEntries = getTransactionEntryDAO().getListTransactionEntryById(accountingSetId, "_AEView",true);
		}
		
		List<ReturnDataSet> returnDataSets = null;
		if(transactionEntries != null && transactionEntries.size() > 0){
			returnDataSets =  getPrepareAccountingSetResults(dataSet, transactionEntries, createNow,true, feeRuleDetailMap);
		}
		
		logger.debug("Leaving");
		return returnDataSets;
	}
	
	
	
	
	/**
	 * Method for Preparing List of FeeRule Objects 
	 * @param dataSet
	 * @param amountCode
	 * @return
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	private String prepareAmountCodes(DataSet dataSet,AEAmountCodes aeAmountCodes) 
			throws IllegalAccessException, InvocationTargetException{
		logger.debug("Entering");
		
		fees = new Fees(); 
		subHeadRule = new SubHeadRule();
		financeType = getFinanceTypeDAO().getFinanceTypeByFinType(dataSet.getFinType());

		//Fill Amount Code Detail Object with FinanceMain Object
		doFillDataSetFiller(dataSet);
		setAmountCodes(aeAmountCodes,dataSet.isNewRecord());//set data to DataSetFiller Object		

		//Execute entries depend on Finance Event
		String accountingSetId = "";
		if(dataSet.getFinEvent().equals("ADDDBSF")){
			accountingSetId = financeType.getFinAEAddDsbFD();
		}else if(dataSet.getFinEvent().equals("ADDDBSN")){
			accountingSetId = financeType.getFinAEAddDsbFDA();
		}else if(dataSet.getFinEvent().equals("ADDDBSP")){
			accountingSetId = financeType.getFinAEAddDsbOD();
		}else if(dataSet.getFinEvent().equals("AMZ")){
			accountingSetId = financeType.getFinAEAmzNorm();
		}else if(dataSet.getFinEvent().equals("AMZSUSP")){
			accountingSetId = financeType.getFinAEAmzSusp();
		}else if(dataSet.getFinEvent().equals("DEFRPY")){
			accountingSetId = financeType.getFinDefRepay();
		}else if(dataSet.getFinEvent().equals("DEFFRQ")){
			accountingSetId = financeType.getFinDeffreq();
		}else if(dataSet.getFinEvent().equals("EARLYPAY")){
			accountingSetId = financeType.getFinAEEarlyPay();
		}else if(dataSet.getFinEvent().equals("EARLYSTL")){
			accountingSetId = financeType.getFinAEEarlySettle();
		}else if(dataSet.getFinEvent().equals("LATEPAY")){
			accountingSetId = financeType.getFinLatePayRule();
		}else if(dataSet.getFinEvent().equals("M_AMZ")){
			accountingSetId = financeType.getFinToAmz();
		}else if(dataSet.getFinEvent().equals("M_NONAMZ")){
			accountingSetId = financeType.getFinAEToNoAmz();
		}else if(dataSet.getFinEvent().equals("RATCHG")){
			accountingSetId = financeType.getFinAERateChg();
		}else if(dataSet.getFinEvent().equals("REPAY")){
			accountingSetId = financeType.getFinAERepay();
		}else if(dataSet.getFinEvent().equals("WRITEOFF")){
			accountingSetId = financeType.getFinAEWriteOff();
		}else if(dataSet.getFinEvent().equals("SCDCHG")){
			accountingSetId = financeType.getFinSchdChange();
		}else if(dataSet.getFinEvent().equals("COMPOUND")){
			accountingSetId = financeType.getFinAECapitalize();
		}else if(dataSet.getFinEvent().equals("PROVSN")){
			accountingSetId = financeType.getFinProvision();
		}else if(dataSet.getFinEvent().equals("DPRCIATE")){ 
			accountingSetId = financeType.getFinDepreciationRule();
		}else if(dataSet.getFinEvent().equals("PRGCLAIM")){ 
			accountingSetId = financeType.getFinAEProgClaim();
		}

		logger.debug("Leaving");
		return accountingSetId;
	}

	/**
	 * Method for preparing List of ReturnDataSet objects by executing rules
	 * @param dataSet
	 * @param accountingSetId
	 * @return
	 * @throws AccountNotFoundException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	private List<ReturnDataSet> getPrepareAccountingSetResults(DataSet dataSet, List<TransactionEntry> transactionEntries,
			String createNow,boolean isCommitment, Map<String, FeeRule> feeRuleDetailMap) throws AccountNotFoundException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");
		
		List<ReturnDataSet> returnDataSets = new ArrayList<ReturnDataSet>();
		
		accountIdList = new ArrayList<String>(transactionEntries.size());
		accountsMap = new HashMap<String, Object>(transactionEntries.size());
		accountsList= new ArrayList<IAccounts>(transactionEntries.size());

		//Set Account number generation
		for (TransactionEntry transactionEntry : transactionEntries) {
			
			if("N".equals(createNow)){
				if(!accountIdList.contains(String.valueOf(transactionEntry.getAccount()+transactionEntry.getAccountType()))){
					IAccounts account = getAccountNumber(dataSet,transactionEntry,isCommitment,false);
					if(account != null){
						accountsList.add(account);
					}
				}
				
			}else{
				IAccounts account = getAccountNumber(dataSet,transactionEntry,isCommitment,true);
				if(account != null){
					accountsList.add(account);
				}
			}
		}

		//Calling Core Banking Interface Service
		if("N".equals(createNow)){
			accountsList = getAccountInterfaceService().fetchExistAccount(accountsList,createNow,true);
			for (IAccounts interfaceAccount : accountsList) {
				if(accountsMap.containsKey(interfaceAccount.getAcType().trim())){
					String valueToKey = accountsMap.get(interfaceAccount.getAcType()).toString();
					accountsMap.remove(interfaceAccount.getAcType());
					accountsMap.put(valueToKey,interfaceAccount);
				}
			}
			
		}else{
			
			for (IAccounts interfaceAccount : accountsList) {
				if(accountsMap.containsKey(interfaceAccount.getTransOrder().trim())){
					accountsMap.remove(interfaceAccount.getTransOrder());
					accountsMap.put(interfaceAccount.getTransOrder(),interfaceAccount);
				}
			}
			
		}

		ReturnDataSet returnDataSet ;
		for (TransactionEntry transactionEntry : transactionEntries) {
			
			returnDataSet = new ReturnDataSet();
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
			if("N".equals(createNow)){
				returnDataSet.setAccountType(transactionEntry.getAccount());
			}

			//Post Reference
			String branch = StringUtils.trimToEmpty(transactionEntry.getAccountBranch()).equals("")? 
					dataSet.getFinBranch():transactionEntry.getAccountBranch();
					
			String accType = "";
			if(financeType != null){
				accType = StringUtils.trimToEmpty(transactionEntry.getAccountType()).equals("")? 
						financeType.getFinAcType():transactionEntry.getAccountType();
			}else{
				accType = StringUtils.trimToEmpty(transactionEntry.getAccountType()).equals("")? 
						"":transactionEntry.getAccountType();
			}
			
			returnDataSet.setPostref(branch+"-"+accType+"-"+dataSet.getFinCcy());

			returnDataSet.setPostStatus("S");
			returnDataSet.setRuleDecider(transactionEntry.getRuleDecider());
			returnDataSet.setAmountType(transactionEntry.getChargeType());
			
			//Set Account Number
			IAccounts acc = null;
			if("N".equals(createNow)){
				if(accountsMap.containsKey(transactionEntry.getAccount()+transactionEntry.getAccountType())){
					acc = (IAccounts)accountsMap.get(transactionEntry.getAccount()+transactionEntry.getAccountType());
				}else{
					continue;
				}
			}else{
				if(accountsMap.containsKey(String.valueOf(transactionEntry.getAccountSetid()+"-"+transactionEntry.getTransOrder()))){
					acc = (IAccounts)accountsMap.get(String.valueOf(transactionEntry.getAccountSetid()+"-"+transactionEntry.getTransOrder()));
				}else{
					continue;
				}
			}
			
			returnDataSet.setTranOrderId(acc.getTransOrder());
			returnDataSet.setAccount(acc.getAccountId());
			returnDataSet.setPostStatus(acc.getFlagPostStatus());
			returnDataSet.setErrorId(acc.getErrorCode());
			returnDataSet.setErrorMsg(acc.getErrorMsg());

			//Regarding to Posting Data
			if(!"N".equals(createNow)){
				returnDataSet.setAccountType(acc.getAcType());
				returnDataSet.setFinType(financeType == null ? "" :financeType.getFinType());
				returnDataSet.setCustCIF(customer.getCustCIF());
				returnDataSet.setFinCcy(dataSet.getFinCcy());
				returnDataSet.setFinBranch(dataSet.getFinBranch());
				returnDataSet.setFlagCreateNew(acc.getFlagCreateNew());
				returnDataSet.setFlagCreateIfNF(acc.getFlagCreateIfNF());
				returnDataSet.setInternalAc(acc.getInternalAc());
			}
			
			//Amount Rule Execution for Amount Calculation
			returnDataSet.setPostAmount(executeAmountRule(dataSet.getFinEvent(), transactionEntry, feeRuleDetailMap));
			returnDataSets.add(returnDataSet);
		}

		logger.debug("Leaving");
		return returnDataSets;
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
	private BigDecimal executeAmountRule(String event, TransactionEntry transactionEntry , Map<String, FeeRule> feeRuleDetailMap) throws IllegalAccessException, InvocationTargetException{
		logger.debug("Entering");
		
		//Execute Transaction Entry Rule
		BigDecimal amount = BigDecimal.ZERO;
		if(transactionEntry.getRuleDecider().equals(PennantConstants.FEES)){
			if(event.startsWith("ADDDBS")){
				event = "ADDDBS";
			}
			
			String amountRule = transactionEntry.getAmountRule();
			String[] res = transactionEntry.getFeeCode().split(",");
			for (int i = 0; i < res.length; i++) {
				
				if(!(StringUtils.trimToEmpty(res[i]).equals("") || res[i].equalsIgnoreCase("Result"))){
					
					if(feeRuleDetailMap != null && feeRuleDetailMap.containsKey(res[i].trim())){
						amount = feeRuleDetailMap.get(res[i].trim()).getFeeAmount();
					}else{
						
						Rule rule = getRuleDAO().getRuleByID(res[i].trim(), "FEES",event , "");
						if (event.equals(PennantConstants.NEWCMT)|| event.equals(PennantConstants.MNTCMT)) {
							
							if(rule != null){
								try {
									Object result = getRuleExecutionUtil().executeRule(rule.getSQLRule(), aeCommitment,
											SystemParameterDetails.getGlobaVariableList());
									amount = new BigDecimal(result == null ? "0" : result.toString());
								}catch (Exception e) {
									e.printStackTrace();
								}
							}
							
						}else{
							if(!fees.isProcessed()){
								fees.setProcessed(true);
								BeanUtils.copyProperties(fees, dataSetFiller);
							}

							if(rule != null){
								try {
									Object result = getRuleExecutionUtil().executeRule(rule.getSQLRule(), fees,
											SystemParameterDetails.getGlobaVariableList());
									amount = new BigDecimal(result == null ? "0" : result.toString());
								}catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
					}
					
					amountRule = amountRule.replace(res[i].trim(), amount.toString());
				}
			}
			
			if(amountRule.contains("Result")){
				Object result = getRuleExecutionUtil().executeRule(amountRule, null,
						SystemParameterDetails.getGlobaVariableList());
				amount = new BigDecimal(result == null ? "0" : result.toString());
			}
			
		}else {
			
			Object result = null;
			
			if (event.contains("CMT")) {
				result = getRuleExecutionUtil().executeRule(transactionEntry.getAmountRule(), aeCommitment,
						SystemParameterDetails.getGlobaVariableList());
			}else{
				result = getRuleExecutionUtil().executeRule(transactionEntry.getAmountRule(), dataSetFiller,
						SystemParameterDetails.getGlobaVariableList());
			}
			amount = new BigDecimal(result == null ? "0" : result.toString());
		}
		
		logger.debug("Leaving");
		return amount;
	}

	/**
	 * Fill Data For DataFiller Object depend on Event Condition
	 * @param dataSet
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	private void doFillDataSetFiller(DataSet dataSet) throws IllegalAccessException, InvocationTargetException{

		logger.debug("Entering");
		customer = getCustomerDAO().getCustomerForPostings(dataSet.getCustId());
		if(customer != null){
			BeanUtils.copyProperties(dataSetFiller, customer);
		}

		dataSetFiller.setReqCampaign("");//TODO Future USE
		dataSetFiller.setReqFinAcType(getFinanceType().getFinAcType());
		dataSetFiller.setReqFinCcy(dataSet.getFinCcy());
		dataSetFiller.setReqFinType(getFinanceType().getFinType());
		dataSetFiller.setReqProduct(getFinanceType().getLovDescProductCodeName());
		dataSetFiller.setTerms(dataSet.getNoOfTerms());
		dataSetFiller.setDOWNPAY(dataSet.getDownPayment());

		int frqDfrCount = 0;
		if(dataSet.getSchdDate() != null){
			frqDfrCount = getFinanceScheduleDetailDAO().getFrqDfrCount(dataSet.getFinReference(),
					DateUtility.formatDate(dataSet.getSchdDate(), PennantConstants.DBDateFormat) );
		}

		dataSetFiller.setFrqDfrCount(frqDfrCount);

		int rpyDfrCount = getDefermentHeaderDAO().getRpyDfrCount(dataSet.getFinReference());
		dataSetFiller.setRpyDfrCount(rpyDfrCount);

		dataSetFiller.setReqFinBranch(dataSet.getFinBranch());
		dataSetFiller.setFinAmount(dataSet.getFinAmount());
		dataSetFiller.setNewLoan(dataSet.isNewRecord());

		logger.debug("Leaving");
	}
	
	private void doFillCommitmentData(DataSet dataSet, Commitment commitment, AECommitment aeCmt) throws IllegalAccessException, InvocationTargetException{

		logger.debug("Entering");
		aeCommitment = aeCmt;
		customer = getCustomerDAO().getCustomerForPostings(dataSet.getCustId());
		if(customer != null){
			BeanUtils.copyProperties(aeCommitment, customer);
		}
	
		aeCommitment.setCmtAmount(commitment.getCmtAmount());
		aeCommitment.setCmtAmountOther(commitment.getCmtAvailable());//TODO
		aeCommitment.setCmtUtilized(commitment.getCmtUtilizedAmount());
		aeCommitment.setCmtUtilizedOther(commitment.getCmtUtilizedAmount());//TODO
		aeCommitment.setCmtMultiBrach(commitment.isMultiBranch());
		aeCommitment.setCmtRevolving(commitment.isRevolving());
		aeCommitment.setCmtShared(commitment.isSharedCmt());
		aeCommitment.setCmtUsedEarlier(commitment.isNonperformingStatus());//TODO

		logger.debug("Leaving");
	}

	/**
	 * Method for Prepare Account Number based on  Transaction Entry Account
	 * @param dataSet
	 * @param transactionEntry
	 * @param finType
	 * @return
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	private IAccounts getAccountNumber(DataSet dataSet,TransactionEntry transactionEntry, boolean isCommitment,boolean createNow) throws IllegalAccessException, InvocationTargetException{

		logger.debug("Entering");
		newAccount = new IAccounts();
		newAccount.setAcCustCIF(customer.getCustCIF());
		newAccount.setAcBranch(dataSet.getFinBranch());
		newAccount.setAcCcy(dataSet.getFinCcy());
		newAccount.setFlagCreateIfNF(true);
		newAccount.setFlagCreateNew(false);
		newAccount.setInternalAc(false);
		
		String tranOrder = String.valueOf(transactionEntry.getAccountSetid()+"-"+transactionEntry.getTransOrder());
		newAccount.setTransOrder(tranOrder);

		//Set Disbursement Account
		if(transactionEntry.getAccount().equals(PennantConstants.DISB)){
			
			newAccount.setAcType("DISB");
			newAccount.setFlagCreateIfNF(false);
			newAccount.setAccountId(dataSet.getDisburseAccount());
			
			if(!createNow){
				accountsMap.put("DISB",transactionEntry.getAccount());
				accountIdList.add(transactionEntry.getAccount());
			}else{
				accountsMap.put(tranOrder,transactionEntry.getAccount());
			}
			
			return newAccount;
		}

		//Set Customer Repayments Account
		if(transactionEntry.getAccount().equals(PennantConstants.REPAY)){
			
			newAccount.setAcType("REPAY");
			
			if(isCommitment){
				if(dataSet.getCmtChargeAmount().compareTo(BigDecimal.ZERO) != 0){
					newAccount.setFlagCreateIfNF(true);
					newAccount.setAccountId(dataSet.getCmtChargeAccount());
				}else{
					logger.debug("Leaving");
					return null;
				}
			}else{
				newAccount.setFlagCreateIfNF(false);
				newAccount.setAccountId(dataSet.getRepayAccount());
			}
			
			if(!createNow){
				accountsMap.put("REPAY",transactionEntry.getAccount());
				accountIdList.add(transactionEntry.getAccount());
			}else{
				accountsMap.put(tranOrder,transactionEntry.getAccount());
			}
			
			logger.debug("Leaving");
			return newAccount;
		}
		
		//Set GL&PL Account
		if(transactionEntry.getAccount().equals(PennantConstants.GLNPL)){
			
			newAccount.setAcType(transactionEntry.getAccountType());
			newAccount.setInternalAc(true);
			newAccount.setAccountId(generateAccount(dataSet, transactionEntry.getAccountType(), 
					transactionEntry.getAccountSubHeadRule(), transactionEntry.getDebitcredit(),true));
			
			if(!createNow){
				accountsMap.put(transactionEntry.getAccountType(),transactionEntry.getAccount()+transactionEntry.getAccountType());
				accountIdList.add(transactionEntry.getAccount()+transactionEntry.getAccountType());
			}else{
				accountsMap.put(tranOrder,transactionEntry.getAccount()+transactionEntry.getAccountType());
			}
			
			logger.debug("Leaving");
			return newAccount;
		}
		
		//Set Customer Loan Account
		if(transactionEntry.getAccount().equals(PennantConstants.FIN)){
			
			newAccount.setAcType(financeType.getFinAcType());
			if(!StringUtils.trimToEmpty(dataSet.getFinAccount()).equals("")){
				newAccount.setAccountId(dataSet.getFinAccount());
			}else{
				
				if(financeType.isFinIsOpenNewFinAc()== true){
					newAccount.setFlagCreateNew(true);
				}
			}

			if(!createNow){
				accountIdList.add(transactionEntry.getAccount());
				accountsMap.put(financeType.getFinAcType(),transactionEntry.getAccount());
			}else{
				accountsMap.put(tranOrder,transactionEntry.getAccount());
			}
			
			logger.debug("Leaving");
			return newAccount;
		}

		//Set Customer Loan Account
		if(transactionEntry.getAccount().equals(PennantConstants.CUSTSYS)){
			
			newAccount.setAcType(transactionEntry.getAccountType());
			newAccount.setFlagCreateNew(transactionEntry.isOpenNewFinAc());

			if(!createNow){
				accountIdList.add(transactionEntry.getAccount()+transactionEntry.getAccountType());
				accountsMap.put(transactionEntry.getAccountType(),transactionEntry.getAccount()+transactionEntry.getAccountType());
			}else{
				accountsMap.put(tranOrder,transactionEntry.getAccount()+transactionEntry.getAccountType());
			}
			
			logger.debug("Leaving");
			return newAccount;
		}
		
		//Set Customer Loan Account
		if(transactionEntry.getAccount().equals(PennantConstants.COMMIT)){
			
			String acType = SystemParameterDetails.getSystemParameterValue("COMMITMENT_AC_TYPE").toString();
			newAccount.setAcType(acType);
			newAccount.setFlagCreateNew(dataSet.isOpenNewCmtAc());
			newAccount.setAccountId("");
			if(!dataSet.isOpenNewCmtAc()){
				newAccount.setAccountId(StringUtils.trimToEmpty(dataSet.getCmtAccount()));
			}
			
			if(!dataSet.getFinEvent().equals(PennantConstants.NEWCMT)){
				newAccount.setFlagCreateNew(false);
				newAccount.setAccountId(StringUtils.trimToEmpty(dataSet.getCmtAccount()));
			}

			if(!createNow){
				accountIdList.add(transactionEntry.getAccount()+transactionEntry.getAccountType());
				accountsMap.put(acType,transactionEntry.getAccount()+transactionEntry.getAccountType());
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
	 * @param finBranch
	 * @param finCcy
	 * @param accountType
	 * @param subHeadRuleCode
	 * @param dbOrCr
	 * @return
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	public String generateAccount(DataSet dataSet, String accountType, String subHeadRuleCode,
			String dbOrCr, boolean alreadyProcessed) throws IllegalAccessException, InvocationTargetException{

		//System Internal Account checking
		SystemInternalAccountDefinition accountDefinition = getInternalAccountDefinitionDAO().getSystemInternalAccountDefinitionById(
				accountType, "_AView");

		if(currency == null){
			currency = getCurrencyDAO().getCurrencyById(dataSet.getFinCcy());
		}
		
		int length = accountDefinition.getSIANumber().length() - Integer.parseInt(
				SystemParameterDetails.getSystemParameterValue("SYSINT_ACCOUNT_LEN").toString());
		String accNumber = dataSet.getFinBranch()+accountDefinition.getSIANumber().substring(length)+currency.getValue();

		if(!(StringUtils.trimToEmpty(subHeadRuleCode).equals(""))){
			Rule rule = getRuleDAO().getRuleByID(subHeadRuleCode, "SUBHEAD", "", "_View");
			
			if(subHeadRule == null){
				
				subHeadRule = new SubHeadRule();
				if(!alreadyProcessed){
					financeType = getFinanceTypeDAO().getFinanceTypeByFinType(dataSet.getFinType());
					doFillDataSetFiller(dataSet);
				}
				
			}

			//Data Already Fill or not
			if(!subHeadRule.isProcessed()){
				subHeadRule.setProcessed(true);
				BeanUtils.copyProperties(subHeadRule, dataSetFiller);
			}
			
			subHeadRule.setDebitOrCredit(dbOrCr);
			if(SystemParameterDetails.getSystemParameterValue("CBI_AVAIL").equals("Y")){
				subHeadRule.setReqGLHead(accountDefinition.getSIANumber().substring(0, length));
			}else{
				subHeadRule.setReqGLHead("00");
			}

			Object result = getRuleExecutionUtil().executeRule(rule.getSQLRule(), subHeadRule,
					SystemParameterDetails.getGlobaVariableList());
			String subHeadCode = result == null ? "" : result.toString();
			if (StringUtils.trimToEmpty(subHeadCode).contains(".")) {
				subHeadCode = subHeadCode.substring(0, subHeadCode.indexOf('.'));
			}
			
			if(StringUtils.trimToEmpty(subHeadCode).equals("")){
				logger.debug("Leaving");
				return accNumber;
			}else{
				String sIANumber = accountDefinition.getSIANumber().substring(length,
						(accountDefinition.getSIANumber().length() - subHeadCode.length()));
				logger.debug("Leaving");
				return (dataSet.getFinBranch()+sIANumber+subHeadCode+currency.getValue());
			}
			
		}else{
			logger.debug("Leaving");
			return accNumber;
		}
	}

	/**
	 * Method for Calculate the Amount Codes By Execution Formulae
	 * @param amountCode
	 * @return
	 */
	private void setAmountCodes(AEAmountCodes aeAmountCodes,boolean isNewFinance){
		logger.debug("Entering");

		dataSetFiller.setDISBURSE(dataSetFiller.getFinAmount());
		dataSetFiller.setPFT(aeAmountCodes.getPft());
		dataSetFiller.setPFTS(aeAmountCodes.getPftS());
		dataSetFiller.setPFTSP(aeAmountCodes.getPftSP());
		dataSetFiller.setPFTSB(aeAmountCodes.getPftSB());
		dataSetFiller.setPFTAP(aeAmountCodes.getPftAP());
		dataSetFiller.setPFTAB(aeAmountCodes.getPftAB());
		dataSetFiller.setPRI(aeAmountCodes.getPri());
		dataSetFiller.setPRIS(aeAmountCodes.getPriS());
		dataSetFiller.setPRISP(aeAmountCodes.getPriSP());
		dataSetFiller.setPRISB(aeAmountCodes.getPriSB());
		dataSetFiller.setPRIAP(aeAmountCodes.getPriAP());
		dataSetFiller.setPRIAB(aeAmountCodes.getPriAB());
		dataSetFiller.setDACCRUE(aeAmountCodes.getDAccrue());
		dataSetFiller.setNACCRUE(aeAmountCodes.getNAccrue());
		dataSetFiller.setRPPFT(aeAmountCodes.getRpPft());
		dataSetFiller.setRPPRI(aeAmountCodes.getRpPri());
		dataSetFiller.setRPTOT(aeAmountCodes.getRpTot());
		dataSetFiller.setACCRUE(aeAmountCodes.getAccrue());
		dataSetFiller.setACCRUE_S(aeAmountCodes.getAccrueS());
		dataSetFiller.setREFUND(aeAmountCodes.getRefund());
		dataSetFiller.setCPZTOT(aeAmountCodes.getCpzTot());
		dataSetFiller.setCPZPRV(aeAmountCodes.getCpzPrv());
		dataSetFiller.setCPZCUR(aeAmountCodes.getCpzCur());
		dataSetFiller.setCPZNXT(aeAmountCodes.getCpzNxt());

		dataSetFiller.setCPNoOfDays(aeAmountCodes.getCPNoOfDays());
		dataSetFiller.setCpDaysTill(aeAmountCodes.getCpDaysTill());
		dataSetFiller.setTPPNoOfDays(aeAmountCodes.getTtlDays());
		dataSetFiller.setDaysDiff(aeAmountCodes.getDaysDiff());

		dataSetFiller.setFinOverDueCntInPast(aeAmountCodes.getODInst());
		dataSetFiller.setODDays(aeAmountCodes.getODDays());
		dataSetFiller.setODInst(aeAmountCodes.getODInst());
		
		if(aeAmountCodes.getODInst() > 0){
			dataSetFiller.setFinOverDueInPast(true);
		}else{
			dataSetFiller.setFinOverDueInPast(false);
		}

		BigDecimal actualTotalSchdProfit = BigDecimal.ZERO;
		BigDecimal actualTotalCpzProfit = BigDecimal.ZERO;

		if(!isNewFinance){
			List<BigDecimal> list= getFinanceMainDAO().getActualPftBal(aeAmountCodes.getFinReference(),"");
			actualTotalSchdProfit = list.get(0);
			actualTotalCpzProfit = list.get(1);;
		}

		dataSetFiller.setPFTCHG(aeAmountCodes.getPft().subtract(actualTotalSchdProfit));
		dataSetFiller.setCPZCHG(aeAmountCodes.getCpzTot().subtract(actualTotalCpzProfit));
		dataSetFiller.setPROVAMT(aeAmountCodes.getProvAmt());
		
		dataSetFiller.setELPDAYS(aeAmountCodes.getElpDays());
		dataSetFiller.setELPMNTS(aeAmountCodes.getElpMnts());
		dataSetFiller.setELPTERMS(aeAmountCodes.getElpTerms());
		dataSetFiller.setTTLDAYS(aeAmountCodes.getTtlDays());
		dataSetFiller.setTTLMNTS(aeAmountCodes.getTtlMnts());
		dataSetFiller.setTTLTERMS(aeAmountCodes.getTtlTerms());
		
		dataSetFiller.setPROVDUE(aeAmountCodes.getPROVDUE());
		dataSetFiller.setSUSPNOW(aeAmountCodes.getSUSPNOW());
		dataSetFiller.setSUSPRLS(aeAmountCodes.getSUSPRLS());
		dataSetFiller.setPENALTY(aeAmountCodes.getPENALTY());
		dataSetFiller.setWAIVER(aeAmountCodes.getWAIVER());
		dataSetFiller.setODCPLShare(aeAmountCodes.getODCPLShare());
		dataSetFiller.setAstValO(aeAmountCodes.getAstValO());
		
		dataSetFiller.setCLAIMAMT(aeAmountCodes.getCLAIMAMT());
		dataSetFiller.setDEFFEREDCOST(aeAmountCodes.getDEFFEREDCOST());
		dataSetFiller.setCURRETBILL(aeAmountCodes.getCURRETBILL());
		dataSetFiller.setTTLRETBILL(aeAmountCodes.getTTLRETBILL());
		
		logger.debug("Leaving");
	}

	/**
	 * Method for Retrieving data for Fee Objects with AmountCodes For FinanceMain Execution
	 * @param dataSet
	 * @param amountCode
	 * @return
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	public HashMap<String, Object> getFeedata(DataSet dataSet,AEAmountCodes amountCodes) throws IllegalAccessException, InvocationTargetException{
		logger.debug("Entering");

		HashMap<String, Object> paymentsRuleMap=new HashMap<String, Object>();

		Fees fees = new Fees(); 	
		financeType = getFinanceTypeDAO().getFinanceTypeByID(dataSet.getFinType(),"_AView");

		doFillDataSetFiller(dataSet);
		setAmountCodes(amountCodes,dataSet.isNewRecord());

		BeanUtils.copyProperties(fees, dataSetFiller);
		paymentsRuleMap.put("amountcodes", amountCodes);
		paymentsRuleMap.put("fees", fees);
		logger.debug("Leaving");
		return paymentsRuleMap;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setFinanceType(FinanceType financeType) {
		this.financeType = financeType;
	}
	public FinanceType getFinanceType() {
		return financeType;
	}

	public SystemInternalAccountDefinitionDAO getInternalAccountDefinitionDAO() {
		return internalAccountDefinitionDAO;
	}
	public void setInternalAccountDefinitionDAO(SystemInternalAccountDefinitionDAO internalAccountDefinitionDAO) {
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
	public void setTransactionEntryDAO(
			TransactionEntryDAO transactionEntryDAO) {
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
	public FinanceScheduleDetailDAO getFinanceScheduleDetailDAO() {
		return financeScheduleDetailDAO;
	}
	public void setFinanceScheduleDetailDAO(
			FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	public void setCurrencyDAO(CurrencyDAO currencyDAO) {
		this.currencyDAO = currencyDAO;
	}
	public CurrencyDAO getCurrencyDAO() {
		return currencyDAO;
	}

	public DefermentHeaderDAO getDefermentHeaderDAO() {
		return defermentHeaderDAO;
	}
	public void setDefermentHeaderDAO(DefermentHeaderDAO defermentHeaderDAO) {
		this.defermentHeaderDAO = defermentHeaderDAO;
	}

	public void setFinanceSuspHeadDAO(FinanceSuspHeadDAO financeSuspHeadDAO) {
		this.financeSuspHeadDAO = financeSuspHeadDAO;
	}
	public FinanceSuspHeadDAO getFinanceSuspHeadDAO() {
		return financeSuspHeadDAO;
	}

	public void setOverdueChargeDAO(OverdueChargeDAO overdueChargeDAO) {
		this.overdueChargeDAO = overdueChargeDAO;
	}
	public OverdueChargeDAO getOverdueChargeDAO() {
		return overdueChargeDAO;
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

	public AccountingSetDAO getAccountingSetDAO() {
    	return accountingSetDAO;
    }
	public void setAccountingSetDAO(AccountingSetDAO accountingSetDAO) {
    	this.accountingSetDAO = accountingSetDAO;
    }

}
