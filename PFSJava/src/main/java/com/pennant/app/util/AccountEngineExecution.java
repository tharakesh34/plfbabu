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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.Interface.model.IAccounts;
import com.pennant.Interface.service.AccountInterfaceService;
import com.pennant.app.constants.AccountConstants;
import com.pennant.app.constants.AccountEventConstants;
import com.pennant.backend.dao.applicationmaster.CurrencyDAO;
import com.pennant.backend.dao.collateral.CollateralSetupDAO;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.FinanceSuspHeadDAO;
import com.pennant.backend.dao.lmtmasters.FinanceReferenceDetailDAO;
import com.pennant.backend.dao.rmtmasters.AccountingSetDAO;
import com.pennant.backend.dao.rmtmasters.FinTypeAccountingDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.dao.rmtmasters.TransactionEntryDAO;
import com.pennant.backend.dao.rulefactory.PostingsDAO;
import com.pennant.backend.dao.rulefactory.RuleDAO;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.rmtmasters.TransactionEntry;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.RuleReturnType;
import com.pennant.cache.util.AccountingConfigCache;
import com.pennanttech.pennapps.core.FactoryException;
import com.pennanttech.pennapps.core.InterfaceException;
import com.rits.cloning.Cloner;

public class AccountEngineExecution implements Serializable {
	private static final long			serialVersionUID	= 852062955563015315L;
	private Logger						logger				= Logger.getLogger(AccountEngineExecution.class);

	private FinanceTypeDAO				financeTypeDAO;
	private FinanceMainDAO				financeMainDAO;
	private TransactionEntryDAO			transactionEntryDAO;
	private RuleDAO						ruleDAO;
	private CustomerDAO					customerDAO;
	private FinanceScheduleDetailDAO	financeScheduleDetailDAO;
	private CurrencyDAO					currencyDAO;
	private FinanceSuspHeadDAO			financeSuspHeadDAO;
	private AccountInterfaceService		accountInterfaceService;
	private RuleExecutionUtil			ruleExecutionUtil;
	private AccountingSetDAO			accountingSetDAO;
	private FinanceReferenceDetailDAO	financeReferenceDetailDAO;
	private FinTypeAccountingDAO		finTypeAccountingDAO;
	private CollateralSetupDAO			collateralSetupDAO;
	private PostingsDAO					postingsDAO;
	private AccountProcessUtil			accountProcessUtil;

	//Default Constructor
	public AccountEngineExecution() {
		super();
	}

	/**
	 * Method for Execution of Accounting Sets depend on Event
	 * 
	 * @param createNow
	 * @param dataMap
	 * @return
	 */
	public AEEvent getAccEngineExecResults(AEEvent aeEvent) {
		logger.debug("Entering");

		List<ReturnDataSet> returnList = prepareAccountingSetResults(aeEvent);

		aeEvent.setReturnDataSet(returnList);
		logger.debug("Leaving");

		return aeEvent;
	}
	
	/**
	 * 
	 * @param returnDataSet
	 */
	public void getReversePostings(List<ReturnDataSet> returnDataSetList, long newLinkedTranID){
		logger.debug("Entering");
		String tranCode = "";
		//Method for Checking for Reverse Calculations Based upon Negative Amounts
		int seq= 1;
		for (ReturnDataSet returnDataSet : returnDataSetList) {

			returnDataSet.setOldLinkedTranId(returnDataSet.getLinkedTranId());
			returnDataSet.setLinkedTranId(newLinkedTranID);
			returnDataSet.setPostAmount(returnDataSet.getPostAmount());
			tranCode = returnDataSet.getTranCode();
			returnDataSet.setTranCode(returnDataSet.getRevTranCode());
			returnDataSet.setRevTranCode(tranCode);
			//FIXME CH to be discussed with PV
			returnDataSet.setPostDate(DateUtility.getAppDate());
			if (returnDataSet.getDrOrCr().equals(AccountConstants.TRANTYPE_CREDIT)) {
				returnDataSet.setDrOrCr(AccountConstants.TRANTYPE_DEBIT);
			} else {
				returnDataSet.setDrOrCr(AccountConstants.TRANTYPE_CREDIT);
			}
			returnDataSet.setTransOrder(seq);
			seq++;
		}
		logger.debug("Leaving");
	}
	
	

	/**
	 * Method for Execution Of Fee & Charges Rules
	 * 
	 * @param formatter
	 * @param isWIF
	 * @param dataMap
	 * @return
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws InterfaceException
	 */
	public List<FeeRule> getFeeChargesExecResults(int formatter, boolean isWIF, HashMap<String, Object> dataMap)
			throws IllegalAccessException, InvocationTargetException, InterfaceException {
		logger.debug("Entering");

		//Prepare AmountCode Details

		prepareAmountCodes(isWIF, dataMap);

		//Execute entries depend on Finance Event
		long accountingSetId;

		String promotionCode = (String) dataMap.get("fm_promotionCode");

		if (StringUtils.isNotBlank(promotionCode)) {
			accountingSetId = getFinTypeAccountingDAO().getAccountSetID(promotionCode,
					(String) dataMap.get("ae_finEvent"), FinanceConstants.MODULEID_PROMOTION);
		} else {
			accountingSetId = getFinTypeAccountingDAO().getAccountSetID((String) dataMap.get("ft_finType"),
					(String) dataMap.get("ae_finEvent"), FinanceConstants.MODULEID_FINTYPE);
		}

		List<FeeRule> feeRules = new ArrayList<FeeRule>();
		String ruleEvent = (String) dataMap.get("ae_finEvent");
		if (ruleEvent.startsWith(AccountEventConstants.ACCEVENT_ADDDBS)
				|| ruleEvent.startsWith(AccountEventConstants.ACCEVENT_DEFAULT)) {
			ruleEvent = AccountEventConstants.ACCEVENT_ADDDBS;
		}

		if (dataMap.get("ae_disburse") == null) {
			dataMap.put("ae_disburse", BigDecimal.ZERO);
		}

		//Fetch Stage Accounting AccountingSetId List 
		List<Long> accSetIdList = new ArrayList<Long>();
		accSetIdList.addAll(getFinanceReferenceDetailDAO().getRefIdListByFinType(
				(String) dataMap.get("ft_finType"),
				("").equals((String) dataMap.get("moduleDefiner")) ? FinanceConstants.FINSER_EVENT_ORG
						: (String) dataMap.get("moduleDefiner"), null, "_ACView"));
		if (!FinanceConstants.FINSER_EVENT_PREAPPROVAL.equals((String) dataMap.get("moduleDefiner"))) {
			accSetIdList.add(accountingSetId);
		}

		if (!accSetIdList.isEmpty()) {

			List<Rule> ruleList = getTransactionEntryDAO().getListFeeChargeRules(accSetIdList, ruleEvent, "_AView", 0);

			FeeRule feeRule;
			BigDecimal totalSchdFeeAmt = BigDecimal.ZERO;
			for (Rule rule : ruleList) {
				feeRule = new FeeRule();

				//Set Object Data of ReturnDataSet(s)
				feeRule.setFeeCode(rule.getRuleCode());
				feeRule.setFeeCodeDesc(rule.getRuleCodeDesc());
				feeRule.setFeeToFinance(rule.getFeeToFinance());
				feeRule.setFeeOrder(rule.getSeqOrder());
				feeRule.setAllowWaiver(rule.isWaiver());
				feeRule.setWaiverPerc(rule.getWaiverPerc());
				feeRule.setCalFeeModify(rule.isCalFeeModify());

				BigDecimal amount = BigDecimal.ZERO;
				amount = (BigDecimal) getRuleExecutionUtil().executeRule(rule.getSQLRule(), dataMap,
						(String) dataMap.get("fm_finCcy"), RuleReturnType.DECIMAL);

				totalSchdFeeAmt = totalSchdFeeAmt.add(amount);
				dataMap.put("FEETOSCHD", totalSchdFeeAmt);
				feeRule.setFeeAmount(amount);
				feeRules.add(feeRule);
			}
		}

		logger.debug("Leaving");

		return feeRules;
	}

	/**
	 * Method for Execution Of Fee & Charges Rules
	 * 
	 * @param formatter
	 * @param isWIF
	 * @param existFeeList
	 * @param dataMap
	 * @return
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws InterfaceException
	 */
	public List<FeeRule> getReExecFeeResults(int formatter, boolean isWIF, List<FeeRule> existFeeList,
			HashMap<String, Object> dataMap) throws IllegalAccessException, InvocationTargetException,
			InterfaceException {
		logger.debug("Entering");

		//Prepare AmountCode Details
		prepareAmountCodes(isWIF, dataMap);

		//Execute entries depend on Finance Event
		long accountingSetId;
		String promotionCode = (String) dataMap.get("fm_promotionCode");

		if (StringUtils.isNotBlank(promotionCode)) {
			accountingSetId = getFinTypeAccountingDAO().getAccountSetID(promotionCode,
					(String) dataMap.get("ae_finEvent"), FinanceConstants.MODULEID_PROMOTION);
		} else {
			accountingSetId = getFinTypeAccountingDAO().getAccountSetID((String) dataMap.get("ft_finType"),
					(String) dataMap.get("ae_finEvent"), FinanceConstants.MODULEID_FINTYPE);
		}

		//Adding Existing Fees
		int feeOrder = 0;
		if (existFeeList != null && !existFeeList.isEmpty()) {
			for (FeeRule feeRule : existFeeList) {
				dataMap.put(
						"ae_disburse",
						((BigDecimal) dataMap.get("ae_disburse")).add(feeRule.getFeeAmount()
								.subtract(feeRule.getWaiverAmount()).subtract(feeRule.getPaidAmount())));
				feeOrder = feeRule.getFeeOrder();
			}
		}

		List<FeeRule> feeRules = existFeeList;
		if(feeRules == null){
			feeRules = new ArrayList<>();
		}
		String ruleEvent = (String) dataMap.get("ae_finEvent");
		if (ruleEvent.startsWith(AccountEventConstants.ACCEVENT_ADDDBS)) {
			ruleEvent = AccountEventConstants.ACCEVENT_ADDDBS;
		}

		//Fetch Stage Accounting AccountingSetId List 
		List<Long> accSetIdList = new ArrayList<Long>();
		if ((boolean) dataMap.get("ae_newRecord") && ruleEvent.startsWith(AccountEventConstants.ACCEVENT_ADDDBS)) {
			accSetIdList.addAll(getFinanceReferenceDetailDAO().getRefIdListByFinType(
					(String) dataMap.get("ft_finType"), FinanceConstants.FINSER_EVENT_ORG, null, "_ACView"));
		}
		accSetIdList.add(accountingSetId);

		if (!accSetIdList.isEmpty()) {
			List<Rule> ruleList = getTransactionEntryDAO().getListFeeChargeRules(accSetIdList, ruleEvent, "_AView",
					feeOrder);
			FeeRule feeRule;
			for (Rule rule : ruleList) {
				feeRule = new FeeRule();

				//Set Object Data of ReturnDataSet(s)
				feeRule.setFeeCode(rule.getRuleCode());
				feeRule.setFeeCodeDesc(rule.getRuleCodeDesc());
				feeRule.setCalFeeModify(rule.isCalFeeModify());
				feeRule.setFeeToFinance(rule.getFeeToFinance());
				feeRule.setFeeOrder(rule.getSeqOrder());
				feeRule.setAllowWaiver(rule.isWaiver());
				feeRule.setWaiverPerc(rule.getWaiverPerc());

				BigDecimal amount = (BigDecimal) getRuleExecutionUtil().executeRule(rule.getSQLRule(), dataMap,
						(String) dataMap.get("fm_finCcy"), RuleReturnType.DECIMAL);

				dataMap.put("ae_disburse", ((BigDecimal) dataMap.get("ae_disburse")).add(amount));
				feeRule.setFeeAmount(amount);
				feeRules.add(feeRule);
			}
		}

		logger.debug("Leaving");

		return feeRules;
	}

	/**
	 * Method For execution of Provision Rule For Provision Calculated Amount
	 * 
	 * @param dataMap
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws InterfaceException
	 */
	public BigDecimal getProvisionExecResults(HashMap<String, Object> dataMap) throws IllegalAccessException,
			InvocationTargetException, InterfaceException {

		doFilldataMap(dataMap);
		setAmountCodes(dataMap, false);
		BigDecimal provCalAmount = BigDecimal.ZERO;

		String rule = getRuleDAO().getAmountRule("PROV", RuleConstants.MODULE_PROVSN, RuleConstants.EVENT_PROVSN);

		if (rule != null) {
			Object result = getRuleExecutionUtil().executeRule(rule, dataMap, (String) dataMap.get("fm_finCcy"),
					RuleReturnType.DECIMAL);
			provCalAmount = new BigDecimal(result == null ? "0" : result.toString());
		}

		return provCalAmount;
	}

	/**
	 * Method for VasRecording posting Entries Execution
	 * 
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws InterfaceException
	 */
	public List<ReturnDataSet> getVasExecResults(AEEvent aeEvent, HashMap<String, Object> dataMap)
			throws InterfaceException, IllegalAccessException, InvocationTargetException {

		logger.debug("Entering");
		//Accounting Set Details
		List<Long> acSetIDList = aeEvent.getAcSetIDList();
		List<TransactionEntry> transactionEntries = new ArrayList<>();
		for (int i = 0; i < acSetIDList.size(); i++) {
			transactionEntries.addAll(AccountingConfigCache.getTransactionEntry(acSetIDList.get(i)));
		}
		List<ReturnDataSet> returnDataSets = null;
		aeEvent.setDataMap(dataMap);

		if (!transactionEntries.isEmpty()) {
			returnDataSets = prepareAccountingSetResults(aeEvent);
		}

		//Method for Checking for Reverse Calculations Based upon Negative Amounts
		for (ReturnDataSet set : returnDataSets) {
			if (set.getPostAmount().compareTo(BigDecimal.ZERO) < 0) {
				String tranCode = set.getTranCode();
				String revTranCode = set.getRevTranCode();
				String debitOrCredit = set.getDrOrCr();

				set.setTranCode(revTranCode);
				set.setRevTranCode(tranCode);

				set.setPostAmount(set.getPostAmount().negate());

				if (debitOrCredit.equals(AccountConstants.TRANTYPE_CREDIT)) {
					set.setDrOrCr(AccountConstants.TRANTYPE_DEBIT);
				} else {
					set.setDrOrCr(AccountConstants.TRANTYPE_CREDIT);
				}
			}
		}

		logger.debug("Leaving");

		return returnDataSets;
	}

	public List<ReturnDataSet> processAccountingByEvent(AEEvent aeEvent, HashMap<String, Object> dataMap)
			throws IllegalAccessException, InvocationTargetException, InterfaceException {
		logger.debug("Entering");

		String acSetEvent = (String) dataMap.get("ae_finEvent");

		//Accounting Set Details
		List<TransactionEntry> transactionEntries = null;
		//Accounting set code will be hard coded
		long accountingSetId = getAccountingSetDAO().getAccountingSetId(acSetEvent, acSetEvent);
		if (accountingSetId != 0) {
			//get List of transaction entries
			transactionEntries = getTransactionEntryDAO().getListTransactionEntryById(accountingSetId, "_AEView", true);
		}

		List<ReturnDataSet> returnDataSets = null;
		aeEvent.setDataMap(dataMap);
		if (transactionEntries != null && transactionEntries.size() > 0) {
			returnDataSets = prepareAccountingSetResults(aeEvent);
		}

		logger.debug("Leaving");

		return returnDataSets;
	}

	/**
	 * Method for Preparing List of FeeRule Objects
	 * 
	 * @param isWIF
	 * @param premiumDetail
	 * @param dataMap
	 * @return
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	private HashMap<String, Object> prepareAmountCodes(boolean isWIF, HashMap<String, Object> dataMap)
			throws IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		// Fill Amount Code Detail Object with FinanceMain Object
		doFilldataMap(dataMap);
		setAmountCodes(dataMap, isWIF);

		logger.debug("Leaving");

		return dataMap;
	}

	/**
	 * Method for preparing List of ReturnDataSet objects by executing rules
	 * 
	 * @param aeCommitmentMap
	 * @param transactionEntries
	 * @param createNow
	 * @param isCommitment
	 * @param dataMap
	 * @param aeCommitment
	 * @return
	 */
	private List<ReturnDataSet> prepareAccountingSetResults(AEEvent aeEvent) {
		logger.debug("Entering");
		logger.trace("FIN REFERENCE: " + aeEvent.getFinReference());
		
		HashMap<String, Object> dataMap = aeEvent.getDataMap();
		List<Long> acSetIDList = aeEvent.getAcSetIDList();
		List<ReturnDataSet> returnDataSets = new ArrayList<ReturnDataSet>();
		List<TransactionEntry> transactionEntries = new ArrayList<>();
		for (int i = 0; i < acSetIDList.size(); i++) {
			if (aeEvent.isEOD()) {
				transactionEntries.addAll(AccountingConfigCache.getCacheTransactionEntry(acSetIDList.get(i)));
			}else{
				transactionEntries.addAll(AccountingConfigCache.getTransactionEntry(acSetIDList.get(i)));
			}
		}
		
		//FIXME CH To be discussed if this is required here
		// Dates Setting
		if (aeEvent.getPostDate() == null) {
			aeEvent.setPostDate(DateUtility.getPostDate());
		}
		aeEvent.setAppDate(DateUtility.getAppDate());
		aeEvent.setAppValueDate(DateUtility.getAppValueDate());
		
		//FIXME: PV 04MAY17: Why it is required here?
		Map<String, Object> accountsMap = new HashMap<String, Object>(transactionEntries.size());
		List<IAccounts> accountsList = new ArrayList<IAccounts>(transactionEntries.size());
		logger.trace("Refactoring of Account Engine: accountsList Size := " + accountsList.size());
		Map<String, String> accountCcyMap = new HashMap<String, String>(transactionEntries.size());

		// Prepare list of account types from tranaactionEntries object
		for (TransactionEntry transactionEntry : transactionEntries) {

			//Place fee (codes) used in Transaction Entries to executing map
			String feeCodes = transactionEntry.getFeeCode();
			if (feeCodes != null) {
				for (String feeCode : feeCodes.split(",")) {
					if (!dataMap.containsKey(feeCode + "_C")) {
						dataMap.put(feeCode + "_C", BigDecimal.ZERO);
						dataMap.put(feeCode + "_W", BigDecimal.ZERO);
						dataMap.put(feeCode + "_P", BigDecimal.ZERO);
						dataMap.put(feeCode + "_SCH", BigDecimal.ZERO);
						dataMap.put(feeCode + "_AF", BigDecimal.ZERO);
					}
				}
			}
		}

		//Set Account number generation
		for (TransactionEntry transactionEntry : transactionEntries) {

			IAccounts account = getAccountNumber(aeEvent, transactionEntry, accountsMap, dataMap);
			if (account != null) {
				logger.trace("Refactoring of Account Engine: accountsList Size := " + accountsList.size());
				accountsList.add(account);
			}

			account = null;
		}

		if (accountCcyMap.size() > 0) {
			accountCcyMap = getAccountInterfaceService().getAccountCurrencyMap(accountCcyMap);
		}

		//FIXME: PV 04MAY17 needs understanding
		for (IAccounts interfaceAccount : accountsList) {
			if (accountsMap.containsKey(interfaceAccount.getTransOrder().trim())) {
				accountsMap.remove(interfaceAccount.getTransOrder());
				accountsMap.put(interfaceAccount.getTransOrder(), interfaceAccount);
			}
		}

		accountsList = null;

		ReturnDataSet returnDataSet;
		//This is to maintain the multiple transactions with in the same linked train ID. 
		//Late pay and Repay will be coming separately and will have same linked tranID.
		int seq = aeEvent.getTransOrder();
		for (TransactionEntry transactionEntry : transactionEntries) {

			returnDataSet = new ReturnDataSet();
			//Set Object Data of ReturnDataSet(s)
			returnDataSet.setLinkedTranId(aeEvent.getLinkedTranId());
			returnDataSet.setFinReference(aeEvent.getFinReference());
			returnDataSet.setFinEvent(aeEvent.getAccountingEvent());
			returnDataSet.setLovDescEventCodeName(transactionEntry.getLovDescEventCodeDesc());
			returnDataSet.setAccSetCodeName(transactionEntry.getLovDescAccSetCodeName());
			returnDataSet.setAccSetId(transactionEntry.getAccountSetid());
			returnDataSet.setCustId(aeEvent.getCustID());
			returnDataSet.setTranDesc(transactionEntry.getTransDesc());
			returnDataSet.setPostDate(aeEvent.getPostDate());
			returnDataSet.setValueDate(aeEvent.getValueDate());
			returnDataSet.setShadowPosting(transactionEntry.isShadowPosting());
			returnDataSet.setPostToSys(transactionEntry.getPostToSys());
			returnDataSet.setDerivedTranOrder(transactionEntry.getDerivedTranOrder());
			returnDataSet.setTransOrder(++seq);
			String ref = aeEvent.getFinReference() + "/" + aeEvent.getAccountingEvent() + "/"
					+ transactionEntry.getTransOrder();
			returnDataSet.setPostingId(ref);

			returnDataSet.setPostref(aeEvent.getBranch() + "-" + transactionEntry.getAccountType() + "-"
					+ aeEvent.getCcy());
			returnDataSet.setPostStatus(AccountConstants.POSTINGS_SUCCESS);
			returnDataSet.setAmountType(transactionEntry.getChargeType());
			returnDataSet.setUserBranch(aeEvent.getPostingUserBranch());
			returnDataSet.setPostBranch(aeEvent.getBranch());

			//Set Account Number
			IAccounts acc = (IAccounts) accountsMap.get(String.valueOf(transactionEntry.getTransOrder()));
			BigDecimal postAmt = executeAmountRule(aeEvent.getAccountingEvent(), transactionEntry, aeEvent.getCcy(), dataMap);
			
			if ((acc == null ||  StringUtils.isBlank(acc.getAccountId())) && BigDecimal.ZERO.compareTo(postAmt) != 0) {
				throw new FactoryException("Invalid accounting configuration, please contact administrator");
			} 
			
			if (acc == null) {
				continue;
			}
			returnDataSet.setTranOrderId(acc.getTransOrder());
			returnDataSet.setAccount(acc.getAccountId());
		//	returnDataSet.setPostStatus(acc.getFlagPostStatus());
			returnDataSet.setErrorId(acc.getErrorCode());
			returnDataSet.setErrorMsg(acc.getErrorMsg());

			//Regarding to Posting Data
			returnDataSet.setAccountType(acc.getAcType());
			returnDataSet.setFinType(aeEvent.getFinType());

			returnDataSet.setCustCIF(aeEvent.getCustCIF());
			returnDataSet.setPostBranch(aeEvent.getBranch());
			returnDataSet.setFlagCreateNew(acc.getFlagCreateNew());
			returnDataSet.setFlagCreateIfNF(acc.getFlagCreateIfNF());
			returnDataSet.setInternalAc(acc.getInternalAc());

			//Amount Rule Execution for Amount Calculation
			if (postAmt.compareTo(BigDecimal.ZERO) >= 0) {
				returnDataSet.setPostAmount(postAmt);
				returnDataSet.setTranCode(transactionEntry.getTranscationCode());
				returnDataSet.setRevTranCode(transactionEntry.getRvsTransactionCode());
				returnDataSet.setDrOrCr(transactionEntry.getDebitcredit());
			} else {
				returnDataSet.setPostAmount(postAmt.abs());
				returnDataSet.setRevTranCode(transactionEntry.getTranscationCode());
				returnDataSet.setTranCode(transactionEntry.getRvsTransactionCode());
				if (transactionEntry.getDebitcredit().equals(AccountConstants.TRANTYPE_CREDIT)) {
					returnDataSet.setDrOrCr(AccountConstants.TRANTYPE_DEBIT);
				} else {
					returnDataSet.setDrOrCr(AccountConstants.TRANTYPE_CREDIT);
				}
			}
			//post amount in Local currency
			returnDataSet.setPostAmountLcCcy(CalculationUtil.getConvertedAmount(aeEvent.getCcy(),
					SysParamUtil.getAppCurrency(), postAmt));
			returnDataSet.setExchangeRate(CurrencyUtil.getExChangeRate(aeEvent.getCcy()));

			//Converting Post Amount Based on Account Currency
			returnDataSet.setAcCcy(aeEvent.getCcy());
			returnDataSet.setFormatter(CurrencyUtil.getFormat(aeEvent.getCcy()));

			BigDecimal postAmount = null;
			List<ReturnDataSet> newEntries = null;

			if (accountCcyMap.containsKey(acc.getAccountId())) {
				String acCcy = accountCcyMap.get(acc.getAccountId());

				if (!StringUtils.equals(aeEvent.getCcy(), acCcy)) {
					postAmount = returnDataSet.getPostAmount();
					returnDataSet
							.setPostAmount(CalculationUtil.getConvertedAmount(aeEvent.getCcy(), acCcy, postAmount));

					//Add Extra Entries For Debit & Credit
					newEntries = createAccOnCCyConversion(returnDataSet, acCcy, postAmount, dataMap);

					returnDataSet.setAcCcy(acCcy);
				}
			}

			// Dates Setting
			returnDataSet.setPostDate(aeEvent.getPostDate());
			returnDataSet.setAppDate(aeEvent.getAppDate());
			returnDataSet.setAppValueDate(aeEvent.getAppValueDate());
			if (aeEvent.isEOD()) {
				returnDataSet.setPostCategory(AccountConstants.POSTING_CATEGORY_EOD);
				returnDataSet.setCustAppDate(aeEvent.getCustAppDate());
			}else{
				returnDataSet.setCustAppDate(getCustomerDAO().getCustAppDate(aeEvent.getCustID()));
			}

			returnDataSets.add(returnDataSet);
			if (newEntries != null) {
				returnDataSets.addAll(newEntries);
			}
		}
		aeEvent.setTransOrder(seq);
		
		accountsMap = null;
		accountCcyMap = null;

		logger.debug("Leaving");

		return returnDataSets;
	}

	private List<ReturnDataSet> createAccOnCCyConversion(ReturnDataSet existDataSet, String acCcy,
			BigDecimal unconvertedPostAmt, HashMap<String, Object> dataMap) {

		List<ReturnDataSet> newEntries = new ArrayList<ReturnDataSet>(2);

		String finBranch = (String) dataMap.get("fm_finBranch");
		String finCcy = (String) dataMap.get("fm_finCcy");

		String actTranType = existDataSet.getDrOrCr();
		String phase = SysParamUtil.getValueAsString(PennantConstants.APP_PHASE);
		String finCcyNum = "";
		String acCcyNum = "";
		int formatter = 0;
		if (phase.equals(PennantConstants.APP_PHASE_EOD)) {
			finCcyNum = CurrencyUtil.getCcyNumber(finCcy);
			acCcyNum = CurrencyUtil.getCcyNumber(acCcy);
		} else {
			finCcyNum = getCurrencyDAO().getCurrencyById(finCcy);

			Currency currency = getCurrencyDAO().getCurrencyByCode(acCcy);
			acCcyNum = currency.getCcyNumber();
			formatter = currency.getCcyEditField();
		}

		String drCr = actTranType.equals(AccountConstants.TRANTYPE_DEBIT) ? AccountConstants.TRANTYPE_CREDIT
				: AccountConstants.TRANTYPE_DEBIT;
		String crDr = actTranType.equals(AccountConstants.TRANTYPE_DEBIT) ? AccountConstants.TRANTYPE_DEBIT
				: AccountConstants.TRANTYPE_CREDIT;

		Cloner cloner = new Cloner();
		ReturnDataSet newDataSet1 = cloner.deepClone(existDataSet);
		newDataSet1.setDrOrCr(drCr);
		newDataSet1.setAcCcy(acCcy);
		newDataSet1.setTransOrder(existDataSet.getTransOrder() + 1);
		newDataSet1.setTranCode(SysParamUtil.getValueAsString("CCYCNV_" + drCr + "RTRANCODE"));
		newDataSet1.setRevTranCode(SysParamUtil.getValueAsString("CCYCNV_" + crDr + "RTRANCODE"));
		newDataSet1.setAccount(existDataSet.getAccount().substring(0, 4) + "881" + acCcyNum + finCcyNum);
		newDataSet1.setFormatter(formatter);
		newEntries.add(newDataSet1);

		cloner = new Cloner();
		ReturnDataSet newDataSet2 = cloner.deepClone(existDataSet);
		newDataSet2.setDrOrCr(crDr);
		newDataSet2.setTransOrder(existDataSet.getTransOrder() + 2);
		newDataSet2.setTranCode(SysParamUtil.getValueAsString("CCYCNV_" + crDr + "RTRANCODE"));
		newDataSet2.setRevTranCode(SysParamUtil.getValueAsString("CCYCNV_" + drCr + "RTRANCODE"));
		newDataSet2.setAccount(finBranch + "881" + finCcyNum + acCcyNum);
		newDataSet2.setPostAmount(unconvertedPostAmt);
		newDataSet2.setPostAmountLcCcy(CalculationUtil.getConvertedAmount(finCcy, SysParamUtil.getAppCurrency(),
				unconvertedPostAmt));
		newEntries.add(newDataSet2);

		existDataSet.setFormatter(formatter);

		return newEntries;
	}

	/**
	 * Fill Data For DataFiller Object depend on Event Condition
	 * 
	 * @param dataSet
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	private void doFilldataMap(HashMap<String, Object> dataMap) throws IllegalAccessException,
			InvocationTargetException {
		logger.debug("Entering");

		//boolean isNewFinance = (boolean)dataMap.get("NEWRECORD");
		Customer customer = getCustomerDAO().getCustomerForPostings((long) dataMap.get("fm_custID"));
		if (customer != null) {
			customer.getDeclaredFieldValues(dataMap);
		}

		logger.debug("Leaving");
	}

	/**
	 * Method for Prepare Account Number based on Transaction Entry Account
	 * 
	 * @param txnEntry
	 * @param isCommitment
	 * @param createNow
	 * @param accountsMap
	 * @param accountCcyMap
	 * @param sysIntAccMap
	 * @param ruleResultMap
	 * @param dataMap
	 * @return
	 */
	private IAccounts getAccountNumber(AEEvent aeEvent, TransactionEntry txnEntry, Map<String, Object> accountsMap,
			HashMap<String, Object> dataMap) {
		logger.debug("Entering");

		String txnOrder = String.valueOf(txnEntry.getTransOrder());
		IAccounts newAccount = new IAccounts();
		newAccount.setAcCustCIF(aeEvent.getCustCIF());
		newAccount.setAcBranch(aeEvent.getBranch());
		newAccount.setAcCcy(aeEvent.getCcy());
		newAccount.setTransOrder(txnOrder);
		newAccount.setTranAc(txnEntry.getAccount());
		newAccount.setAcType(txnEntry.getAccountType());
		newAccount.setInternalAc(true);

		Rule rule =null;
		if (aeEvent.isEOD()) {
			rule = AccountingConfigCache.getCacheRule(txnEntry.getAccountSubHeadRule(), RuleConstants.MODULE_SUBHEAD,
					RuleConstants.MODULE_SUBHEAD);
		}else{
			rule = AccountingConfigCache.getRule(txnEntry.getAccountSubHeadRule(), RuleConstants.MODULE_SUBHEAD,
					RuleConstants.MODULE_SUBHEAD);
		}
		
		
		dataMap.put("acType", txnEntry.getAccountType());
		if (rule != null) {
			String accountNumber = (String) getRuleExecutionUtil().executeRule(rule.getSQLRule(), dataMap,
					aeEvent.getCcy(), RuleReturnType.STRING);
			newAccount.setAccountId(accountNumber);
		}

		accountsMap.put(txnOrder, txnEntry.getAccount() + txnEntry.getAccountType());

		logger.debug("Leaving");
		return newAccount;
	}

	/**
	 * Method for Execution of Amount Rule and Getting Amount
	 * 
	 * <br>
	 * IN AccountEngineExecution.java
	 * 
	 * @param event
	 * @param transactionEntry
	 * @param feeRuleDetailMap
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 *             BigDecimal
	 */
	private BigDecimal executeAmountRule(String event, TransactionEntry transactionEntry, String finCcy,
			HashMap<String, Object> dataMap) {
		logger.debug("Entering");

		// Execute Transaction Entry Rule
		BigDecimal amount = BigDecimal.ZERO;
		if (event.startsWith(AccountEventConstants.ACCEVENT_ADDDBS)) {
			event = AccountEventConstants.ACCEVENT_ADDDBS;
		}

		String amountRule = transactionEntry.getAmountRule();
		amount = (BigDecimal)getRuleExecutionUtil().executeRule(amountRule, dataMap, finCcy, RuleReturnType.DECIMAL);
		logger.debug("Leaving");
		return amount;
	}

	/**
	 * Method for Calculate the Amount Codes By Execution Formulae
	 * 
	 * @param amountCode
	 * @return
	 */
	private void setAmountCodes(HashMap<String, Object> dataMap, boolean isWIF) {
		logger.debug("Entering");

		boolean finOverDueInPast = false;

		if ((int) dataMap.get("ae_ODInst") > 0) {
			finOverDueInPast = true;
		}

		BigDecimal actualTotalSchdProfit = BigDecimal.ZERO;
		BigDecimal actualTotalCpzProfit = BigDecimal.ZERO;

		if (!(boolean) dataMap.get("ae_newRecord") && !isWIF) {
			List<BigDecimal> list = getFinanceMainDAO().getActualPftBal((String) dataMap.get("fm_finReference"), "");
			actualTotalSchdProfit = list.get(0);
			actualTotalCpzProfit = list.get(1);
		}

		BigDecimal PFTCHG = ((BigDecimal) dataMap.get("ae_pft")).subtract(actualTotalSchdProfit);
		BigDecimal CPZCHG = ((BigDecimal) dataMap.get("ae_cpzTot")).subtract(actualTotalCpzProfit);

		dataMap.put("PFTCHG", PFTCHG);
		dataMap.put("CPZCHG", CPZCHG);
		dataMap.put("finOverDueInPast", finOverDueInPast);

		logger.debug("Leaving");
	}

 

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
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

	public CustomerDAO getCustomerDAO() {
		return customerDAO;
	}

	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

	public FinanceScheduleDetailDAO getFinanceScheduleDetailDAO() {
		return financeScheduleDetailDAO;
	}

	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	public void setCurrencyDAO(CurrencyDAO currencyDAO) {
		this.currencyDAO = currencyDAO;
	}

	public CurrencyDAO getCurrencyDAO() {
		return currencyDAO;
	}

	public void setFinanceSuspHeadDAO(FinanceSuspHeadDAO financeSuspHeadDAO) {
		this.financeSuspHeadDAO = financeSuspHeadDAO;
	}

	public FinanceSuspHeadDAO getFinanceSuspHeadDAO() {
		return financeSuspHeadDAO;
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

	public FinanceReferenceDetailDAO getFinanceReferenceDetailDAO() {
		return financeReferenceDetailDAO;
	}

	public void setFinanceReferenceDetailDAO(FinanceReferenceDetailDAO financeReferenceDetailDAO) {
		this.financeReferenceDetailDAO = financeReferenceDetailDAO;
	}

	public FinTypeAccountingDAO getFinTypeAccountingDAO() {
		return finTypeAccountingDAO;
	}

	public void setFinTypeAccountingDAO(FinTypeAccountingDAO finTypeAccountingDAO) {
		this.finTypeAccountingDAO = finTypeAccountingDAO;
	}

	public CollateralSetupDAO getCollateralSetupDAO() {
		return collateralSetupDAO;
	}

	public void setCollateralSetupDAO(CollateralSetupDAO collateralSetupDAO) {
		this.collateralSetupDAO = collateralSetupDAO;
	}

	public PostingsDAO getPostingsDAO() {
		return postingsDAO;
	}

	public void setPostingsDAO(PostingsDAO postingsDAO) {
		this.postingsDAO = postingsDAO;
	}

	public AccountProcessUtil getAccountProcessUtil() {
		return accountProcessUtil;
	}

	public void setAccountProcessUtil(AccountProcessUtil accountProcessUtil) {
		this.accountProcessUtil = accountProcessUtil;
	}

}
