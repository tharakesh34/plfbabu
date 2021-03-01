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
 * FileName    		:  AccountEngineExecution.java											*                           
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.Interface.model.IAccounts;
import com.pennant.Interface.service.AccountInterfaceService;
import com.pennant.app.constants.AccountConstants;
import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.backend.dao.applicationmaster.CurrencyDAO;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.rmtmasters.AccountingSetDAO;
import com.pennant.backend.dao.rmtmasters.TransactionEntryDAO;
import com.pennant.backend.dao.rulefactory.RuleDAO;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.eventproperties.EventProperties;
import com.pennant.backend.model.rmtmasters.TransactionEntry;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.RuleReturnType;
import com.pennant.cache.util.AccountingConfigCache;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.resource.Literal;

public class AccountEngineExecution implements Serializable {
	private static final long serialVersionUID = 852062955563015315L;
	private Logger logger = LogManager.getLogger(AccountEngineExecution.class);

	private FinanceMainDAO financeMainDAO;
	private TransactionEntryDAO transactionEntryDAO;
	private RuleDAO ruleDAO;
	private CustomerDAO customerDAO;
	private CurrencyDAO currencyDAO;
	private AccountInterfaceService accountInterfaceService;
	private AccountingSetDAO accountingSetDAO;

	//Default Constructor
	public AccountEngineExecution() {
		super();
	}

	public void getAccEngineExecResults(AEEvent aeEvent) {
		List<ReturnDataSet> returnList = prepareAccountingSetResults(aeEvent);
		aeEvent.setReturnDataSet(returnList);
	}

	/**
	 * 
	 * @param returnDataSet
	 */
	public void getReversePostings(List<ReturnDataSet> returnDataSetList, long newLinkedTranID) {
		logger.debug(Literal.ENTERING);

		String tranCode = "";
		//Method for Checking for Reverse Calculations Based upon Negative Amounts
		int seq = 1;

		if (CollectionUtils.isEmpty(returnDataSetList)) {
			logger.debug(Literal.LEAVING);
			return;
		}
		EventProperties eventProperties = returnDataSetList.get(0).getEventProperties();

		Date appDate = null;
		if (eventProperties.isParameterLoaded()) {
			appDate = eventProperties.getAppDate();
		} else {
			appDate = SysParamUtil.getAppDate();
		}

		for (ReturnDataSet returnDataSet : returnDataSetList) {

			returnDataSet.setOldLinkedTranId(returnDataSet.getLinkedTranId());
			returnDataSet.setLinkedTranId(newLinkedTranID);
			returnDataSet.setPostAmount(returnDataSet.getPostAmount());
			tranCode = returnDataSet.getTranCode();
			returnDataSet.setTranCode(returnDataSet.getRevTranCode());
			returnDataSet.setRevTranCode(tranCode);
			//FIXME CH to be discussed with PV
			returnDataSet.setPostDate(appDate);
			returnDataSet.setDrOrCr(returnDataSet.getDrOrCr().equals(AccountConstants.TRANTYPE_CREDIT)
					? AccountConstants.TRANTYPE_DEBIT : AccountConstants.TRANTYPE_CREDIT);

			returnDataSet.setTransOrder(seq);
			seq++;
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * 
	 * @param returnDataSet
	 */
	public void getReversePostings(List<ReturnDataSet> returnDataSetList, long newLinkedTranID, long postingId) {
		logger.debug(Literal.ENTERING);

		String tranCode = "";
		//Method for Checking for Reverse Calculations Based upon Negative Amounts
		int seq = 1;
		EventProperties eventProperties = returnDataSetList.get(0).getEventProperties();
		Date appDate = null;
		if (eventProperties.isParameterLoaded()) {
			appDate = eventProperties.getAppDate();
		} else {
			appDate = SysParamUtil.getAppDate();
		}
		for (ReturnDataSet returnDataSet : returnDataSetList) {
			returnDataSet.setOldLinkedTranId(returnDataSet.getLinkedTranId());
			returnDataSet.setLinkedTranId(newLinkedTranID);
			returnDataSet.setPostAmount(returnDataSet.getPostAmount());
			returnDataSet.setPostingId(String.valueOf(postingId));
			tranCode = returnDataSet.getTranCode();
			returnDataSet.setTranCode(returnDataSet.getRevTranCode());
			returnDataSet.setRevTranCode(tranCode);
			//FIXME CH to be discussed with PV
			returnDataSet.setPostDate(appDate);
			returnDataSet.setDrOrCr(returnDataSet.getDrOrCr().equals(AccountConstants.TRANTYPE_CREDIT)
					? AccountConstants.TRANTYPE_DEBIT : AccountConstants.TRANTYPE_CREDIT);
			returnDataSet.setTransOrder(seq);
			seq++;
		}
		logger.debug(Literal.LEAVING);
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
	public BigDecimal getProvisionExecResults(Map<String, Object> dataMap)
			throws IllegalAccessException, InvocationTargetException, InterfaceException {

		doFilldataMap(dataMap);
		setAmountCodes(dataMap, false);
		BigDecimal provCalAmount = BigDecimal.ZERO;

		String rule = ruleDAO.getAmountRule("PROV", RuleConstants.MODULE_PROVSN, RuleConstants.EVENT_PROVSN);

		if (rule != null) {
			Object result = RuleExecutionUtil.executeRule(rule, dataMap, (String) dataMap.get("fm_finCcy"),
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
	public List<ReturnDataSet> getVasExecResults(AEEvent aeEvent, HashMap<String, Object> dataMap) {
		logger.debug(Literal.ENTERING);

		//Accounting Set Details
		List<Long> acSetIDList = aeEvent.getAcSetIDList();
		List<TransactionEntry> entries = new ArrayList<>();

		acSetIDList.stream().forEach(acSet -> entries.addAll(AccountingConfigCache.getTransactionEntry(acSet)));

		List<ReturnDataSet> returnDataSets = null;
		aeEvent.setDataMap(dataMap);

		if (!entries.isEmpty()) {
			returnDataSets = prepareAccountingSetResults(aeEvent);
		}

		if (returnDataSets == null) {
			returnDataSets = new ArrayList<>();
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
				set.setDrOrCr(debitOrCredit.equals(AccountConstants.TRANTYPE_CREDIT) ? AccountConstants.TRANTYPE_DEBIT
						: AccountConstants.TRANTYPE_CREDIT);
			}
		}

		logger.debug(Literal.LEAVING);
		return returnDataSets;
	}

	public List<ReturnDataSet> processAccountingByEvent(AEEvent aeEvent, HashMap<String, Object> dataMap)
			throws IllegalAccessException, InvocationTargetException, InterfaceException {
		logger.debug(Literal.ENTERING);

		String acSetEvent = (String) dataMap.get("ae_finEvent");

		//Accounting Set Details
		List<TransactionEntry> transactionEntries = null;
		//Accounting set code will be hard coded
		long accountingSetId = accountingSetDAO.getAccountingSetId(acSetEvent, acSetEvent);
		if (accountingSetId != 0) {
			//get List of transaction entries
			transactionEntries = transactionEntryDAO.getListTransactionEntryById(accountingSetId, "_AEView", true);
		}

		List<ReturnDataSet> returnDataSets = null;
		aeEvent.setDataMap(dataMap);

		if (CollectionUtils.isNotEmpty(transactionEntries)) {
			returnDataSets = prepareAccountingSetResults(aeEvent);
		}

		logger.debug(Literal.LEAVING);
		return returnDataSets;
	}

	private List<ReturnDataSet> prepareAccountingSetResults(AEEvent aeEvent) {
		Map<String, Object> dataMap = aeEvent.getDataMap();
		List<Long> list = aeEvent.getAcSetIDList();
		List<ReturnDataSet> returnDataSets = new ArrayList<ReturnDataSet>();
		List<TransactionEntry> entries = new ArrayList<>();

		if (aeEvent.isEOD()) {
			list.stream().forEach(acSet -> entries.addAll(AccountingConfigCache.getCacheTransactionEntry(acSet)));
		} else {
			list.stream().forEach(acSet -> entries.addAll(AccountingConfigCache.getTransactionEntry(acSet)));
		}

		EventProperties eventProperties = aeEvent.getEventProperties();

		String zeroPostingFlag = null;
		String appCurrency = null;

		if (eventProperties.isParameterLoaded()) {
			aeEvent.setAppDate(eventProperties.getAppDate());
			aeEvent.setAppValueDate(eventProperties.getAppValueDate());
			aeEvent.setPostDate(eventProperties.getPostDate());
			zeroPostingFlag = eventProperties.getAllowZeroPostings();
			appCurrency = eventProperties.getAppCurrency();
		} else {
			aeEvent.setAppDate(SysParamUtil.getAppDate());
			aeEvent.setAppValueDate(SysParamUtil.getAppValueDate());
			if (aeEvent.getPostDate() == null) {
				aeEvent.setPostDate(SysParamUtil.getPostDate());
			}
			zeroPostingFlag = SysParamUtil.getValueAsString("ALLOW_ZERO_POSTINGS");
			appCurrency = SysParamUtil.getAppCurrency();
		}

		Map<String, Object> accountsMap = new HashMap<>();
		List<IAccounts> accountsList = new ArrayList<>();
		Map<String, String> accountCcyMap = new HashMap<>();

		for (TransactionEntry transactionEntry : entries) {
			dataMap = addFeeCodesToDataMap(dataMap, transactionEntry);
		}

		/**
		 * For temporary use
		 */
		try {
			dataMap.forEach((k, v) -> logger.trace("Fee Code = {}, Value = {}", k, v));
		} catch (Exception e) {
			logger.warn(Literal.EXCEPTION, e);
		}

		for (TransactionEntry transactionEntry : entries) {
			IAccounts account = getAccountNumber(aeEvent, transactionEntry, accountsMap, dataMap);

			if (account != null) {
				accountsList.add(account);
			}

			account = null;
		}

		if (accountCcyMap.size() > 0) {
			accountCcyMap = accountInterfaceService.getAccountCurrencyMap(accountCcyMap);
		}

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

		Date custAppdate = customerDAO.getCustAppDate(aeEvent.getCustID());

		for (TransactionEntry transactionEntry : entries) {
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
			if (aeEvent.getPostingId() > 0) {
				returnDataSet.setPostingId(String.valueOf(aeEvent.getPostingId()));
			} else {
				String ref = aeEvent.getFinReference() + "/" + aeEvent.getAccountingEvent() + "/"
						+ transactionEntry.getTransOrder();
				returnDataSet.setPostingId(ref);

			}
			String ccy = aeEvent.getCcy();
			if (aeEvent.getPostRefId() <= 0) {
				returnDataSet.setPostref(aeEvent.getBranch() + "-" + transactionEntry.getAccountType() + "-" + ccy);
			} else {
				returnDataSet.setPostref(String.valueOf(aeEvent.getPostRefId()));
			}
			returnDataSet.setPostStatus(AccountConstants.POSTINGS_SUCCESS);
			returnDataSet.setAmountType(transactionEntry.getChargeType());
			returnDataSet.setUserBranch(aeEvent.getPostingUserBranch());
			returnDataSet.setPostBranch(aeEvent.getBranch());
			returnDataSet.setEntityCode(aeEvent.getEntityCode());
			//Set Account Number
			IAccounts acc = (IAccounts) accountsMap.get(String.valueOf(transactionEntry.getTransOrder()));
			BigDecimal postAmt = executeAmountRule(aeEvent.getAccountingEvent(), transactionEntry, ccy, dataMap);

			//If parameter flag is 'N' for zero postings not allow to insert zero postings
			if (BigDecimal.ZERO.compareTo(postAmt) == 0 && "N".equalsIgnoreCase(zeroPostingFlag)) {
				continue;
			}

			if (acc == null || StringUtils.isBlank(acc.getAccountId())) {
				if (BigDecimal.ZERO.compareTo(postAmt) != 0) {
					throw new AppException(String.format(
							"Accounting for %S Event is invalid for order id : %S , please contact administrator",
							aeEvent.getAccountingEvent(), transactionEntry.getTransOrder()));
				}
				continue;
			}

			// Un Realized Amortization Income Field Exists
			if (StringUtils.contains(transactionEntry.getAmountRule(), "dAmz")) {
				aeEvent.setuAmzExists(true);
			}

			// Un Realized Amortization LPP Field Exists
			if (StringUtils.contains(transactionEntry.getAmountRule(), "bpi")) {
				aeEvent.setBpiIncomized(true);
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
			returnDataSet.setPostAmountLcCcy(CalculationUtil.getConvertedAmount(ccy, appCurrency, postAmt));
			returnDataSet.setExchangeRate(CurrencyUtil.getExChangeRate(ccy));

			//Converting Post Amount Based on Account Currency
			returnDataSet.setAcCcy(ccy);
			returnDataSet.setFormatter(CurrencyUtil.getFormat(ccy));

			BigDecimal postAmount = null;
			List<ReturnDataSet> newEntries = null;

			if (accountCcyMap.containsKey(acc.getAccountId())) {
				String acCcy = accountCcyMap.get(acc.getAccountId());

				if (!StringUtils.equals(ccy, acCcy)) {
					postAmount = returnDataSet.getPostAmount();
					returnDataSet.setPostAmount(CalculationUtil.getConvertedAmount(ccy, acCcy, postAmount));
					returnDataSet.setEventProperties(eventProperties);

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
			} else {
				//TODO: Cash Management Change check it once
				if (aeEvent.getCustID() <= 0) {
					returnDataSet.setCustAppDate(returnDataSet.getPostDate());
				} else {
					returnDataSet.setCustAppDate(custAppdate);
				}
			}

			returnDataSets.add(returnDataSet);
			if (newEntries != null) {
				returnDataSets.addAll(newEntries);
			}
		}
		aeEvent.setTransOrder(seq);

		accountsMap = null;
		accountCcyMap = null;

		return returnDataSets;
	}

	private Map<String, Object> addFeeCodesToDataMap(Map<String, Object> dataMap, TransactionEntry transactionEntry) {
		//Place fee (codes) used in Transaction Entries to executing map
		String feeCodes = transactionEntry.getFeeCode();

		if (feeCodes == null) {
			return dataMap;
		}

		if (feeCodes.isEmpty()) {
			return dataMap;
		}

		for (String feeCode : feeCodes.split(",")) {

			addZeroifNotContains(dataMap, feeCode + "_C");
			addZeroifNotContains(dataMap, feeCode + "_W");
			addZeroifNotContains(dataMap, feeCode + "_P");
			addZeroifNotContains(dataMap, feeCode + "_SCH");
			addZeroifNotContains(dataMap, feeCode + "_AF");
			addZeroifNotContains(dataMap, feeCode + "_N");
			addZeroifNotContains(dataMap, feeCode + "_R");

			// GST
			addZeroifNotContains(dataMap, feeCode + "_UGST_N");
			addZeroifNotContains(dataMap, feeCode + "_SGST_N");
			addZeroifNotContains(dataMap, feeCode + "_IGST_N");
			addZeroifNotContains(dataMap, feeCode + "_CGST_N");
			addZeroifNotContains(dataMap, feeCode + "_CESS_N");

			addZeroifNotContains(dataMap, feeCode + "_UGST_C");
			addZeroifNotContains(dataMap, feeCode + "_SGST_C");
			addZeroifNotContains(dataMap, feeCode + "_IGST_C");
			addZeroifNotContains(dataMap, feeCode + "_CGST_C");
			addZeroifNotContains(dataMap, feeCode + "_CESS_C");

			addZeroifNotContains(dataMap, feeCode + "_UGST_P");
			addZeroifNotContains(dataMap, feeCode + "_SGST_P");
			addZeroifNotContains(dataMap, feeCode + "_IGST_P");
			addZeroifNotContains(dataMap, feeCode + "_CGST_P");
			addZeroifNotContains(dataMap, feeCode + "_CESS_P");

			addZeroifNotContains(dataMap, feeCode + "_UGST_W");
			addZeroifNotContains(dataMap, feeCode + "_SGST_W");
			addZeroifNotContains(dataMap, feeCode + "_IGST_W");
			addZeroifNotContains(dataMap, feeCode + "_CGST_W");
			addZeroifNotContains(dataMap, feeCode + "_CESS_W");

			addZeroifNotContains(dataMap, feeCode + "_UGST_R");
			addZeroifNotContains(dataMap, feeCode + "_SGST_R");
			addZeroifNotContains(dataMap, feeCode + "_IGST_R");
			addZeroifNotContains(dataMap, feeCode + "_CGST_R");

			addZeroifNotContains(dataMap, feeCode + "_UGST_R");
			addZeroifNotContains(dataMap, feeCode + "_SGST_R");
			addZeroifNotContains(dataMap, feeCode + "_IGST_R");
			addZeroifNotContains(dataMap, feeCode + "_CGST_R");

			addZeroifNotContains(dataMap, feeCode + "_UGST_SCH");
			addZeroifNotContains(dataMap, feeCode + "_SGST_SCH");
			addZeroifNotContains(dataMap, feeCode + "_IGST_SCH");
			addZeroifNotContains(dataMap, feeCode + "_CGST_SCH");
			addZeroifNotContains(dataMap, feeCode + "_CESS_SCH");

			addZeroifNotContains(dataMap, feeCode + "_UGST_AF");
			addZeroifNotContains(dataMap, feeCode + "_SGST_AF");
			addZeroifNotContains(dataMap, feeCode + "_IGST_AF");
			addZeroifNotContains(dataMap, feeCode + "_CGST_AF");
			addZeroifNotContains(dataMap, feeCode + "_CESS_AF");

			if (ImplementationConstants.ALLOW_TDS_ON_FEE) {
				addZeroifNotContains(dataMap, feeCode + "_TDS_N");
				addZeroifNotContains(dataMap, feeCode + "_TDS_P");
				addZeroifNotContains(dataMap, feeCode + "_TDS_R");
			}

			String[] payTypes = { "EX_", "EA_", "PA_", "PB_" };
			String key;
			for (String payType : payTypes) {
				key = payType + feeCode + "_P";
				if (!dataMap.containsKey(key)) {
					dataMap.put(key, BigDecimal.ZERO);
				}
			}
		}

		return dataMap;
	}

	private void addZeroifNotContains(Map<String, Object> dataMap, String key) {
		if (dataMap != null) {
			if (!dataMap.containsKey(key)) {
				dataMap.put(key, BigDecimal.ZERO);
			}
		}
	}

	private List<ReturnDataSet> createAccOnCCyConversion(ReturnDataSet existDataSet, String acCcy,
			BigDecimal unconvertedPostAmt, Map<String, Object> dataMap) {

		List<ReturnDataSet> newEntries = new ArrayList<>(2);

		String finBranch = (String) dataMap.get("fm_finBranch");
		String finCcy = (String) dataMap.get("fm_finCcy");

		String actTranType = existDataSet.getDrOrCr();

		EventProperties eventProperties = existDataSet.getEventProperties();
		String phase = null;
		String appCurrency = null;

		if (eventProperties.isParameterLoaded()) {
			phase = eventProperties.getPhase();
			appCurrency = eventProperties.getAppCurrency();
		} else {
			phase = SysParamUtil.getValueAsString(PennantConstants.APP_PHASE);
			appCurrency = SysParamUtil.getAppCurrency();
		}

		String finCcyNum = "";
		String acCcyNum = "";
		int formatter = 0;
		if (phase.equals(PennantConstants.APP_PHASE_EOD)) {
			finCcyNum = CurrencyUtil.getCcyNumber(finCcy);
			acCcyNum = CurrencyUtil.getCcyNumber(acCcy);
		} else {
			finCcyNum = currencyDAO.getCurrencyById(finCcy);

			Currency currency = currencyDAO.getCurrencyByCode(acCcy);
			acCcyNum = currency.getCcyNumber();
			formatter = currency.getCcyEditField();
		}

		String drCr = actTranType.equals(AccountConstants.TRANTYPE_DEBIT) ? AccountConstants.TRANTYPE_CREDIT
				: AccountConstants.TRANTYPE_DEBIT;
		String crDr = actTranType.equals(AccountConstants.TRANTYPE_DEBIT) ? AccountConstants.TRANTYPE_DEBIT
				: AccountConstants.TRANTYPE_CREDIT;

		String crDrTranCode = SysParamUtil.getValueAsString("CCYCNV_" + crDr + "RTRANCODE");
		String drCrTranCode = SysParamUtil.getValueAsString("CCYCNV_" + drCr + "RTRANCODE");

		ReturnDataSet newDataSet1 = existDataSet.copyEntity();
		newDataSet1.setDrOrCr(drCr);
		newDataSet1.setAcCcy(acCcy);
		newDataSet1.setTransOrder(existDataSet.getTransOrder() + 1);
		newDataSet1.setTranCode(drCrTranCode);
		newDataSet1.setRevTranCode(crDrTranCode);
		newDataSet1.setAccount(existDataSet.getAccount().substring(0, 4) + "881" + acCcyNum + finCcyNum);
		newDataSet1.setFormatter(formatter);
		newEntries.add(newDataSet1);

		ReturnDataSet newDataSet2 = existDataSet.copyEntity();
		newDataSet2.setDrOrCr(crDr);
		newDataSet2.setTransOrder(existDataSet.getTransOrder() + 2);
		newDataSet2.setTranCode(crDrTranCode);
		newDataSet2.setRevTranCode(drCrTranCode);
		newDataSet2.setAccount(finBranch + "881" + finCcyNum + acCcyNum);
		newDataSet2.setPostAmount(unconvertedPostAmt);
		newDataSet2.setPostAmountLcCcy(CalculationUtil.getConvertedAmount(finCcy, appCurrency, unconvertedPostAmt));
		newEntries.add(newDataSet2);

		existDataSet.setFormatter(formatter);

		return newEntries;
	}

	private void doFilldataMap(Map<String, Object> dataMap) throws IllegalAccessException, InvocationTargetException {
		Customer customer = customerDAO.getCustomerForPostings((long) dataMap.get("fm_custID"));
		if (customer != null) {
			customer.getDeclaredFieldValues(dataMap);
		}
	}

	private IAccounts getAccountNumber(AEEvent aeEvent, TransactionEntry txnEntry, Map<String, Object> accountsMap,
			Map<String, Object> dataMap) {

		String txnOrder = String.valueOf(txnEntry.getTransOrder());
		IAccounts newAccount = new IAccounts();
		newAccount.setAcCustCIF(aeEvent.getCustCIF());
		newAccount.setAcBranch(aeEvent.getBranch());
		newAccount.setAcCcy(aeEvent.getCcy());
		newAccount.setTransOrder(txnOrder);
		newAccount.setTranAc(txnEntry.getAccount());
		newAccount.setAcType(txnEntry.getAccountType());
		newAccount.setInternalAc(true);

		Rule rule = null;
		String accountSubHeadRule = txnEntry.getAccountSubHeadRule();
		String moduleSubhead = RuleConstants.MODULE_SUBHEAD;

		if (aeEvent.isEOD()) {
			rule = AccountingConfigCache.getCacheRule(accountSubHeadRule, moduleSubhead, moduleSubhead);
		} else {
			rule = AccountingConfigCache.getRule(accountSubHeadRule, moduleSubhead, moduleSubhead);
		}

		dataMap.put("acType", txnEntry.getAccountType());
		if (rule != null) {
			String accountNumber = (String) RuleExecutionUtil.executeRule(rule.getSQLRule(), dataMap, aeEvent.getCcy(),
					RuleReturnType.STRING);
			newAccount.setAccountId(accountNumber);
		}

		accountsMap.put(txnOrder, txnEntry.getAccount() + txnEntry.getAccountType());

		return newAccount;
	}

	private BigDecimal executeAmountRule(String event, TransactionEntry transactionEntry, String finCcy,
			Map<String, Object> dataMap) {

		BigDecimal amount = BigDecimal.ZERO;
		if (event.startsWith(AccountEventConstants.ACCEVENT_ADDDBS)) {
			event = AccountEventConstants.ACCEVENT_ADDDBS;
		}

		String amountRule = transactionEntry.getAmountRule();
		amount = (BigDecimal) RuleExecutionUtil.executeRule(amountRule, dataMap, finCcy, RuleReturnType.DECIMAL);

		return amount == null ? BigDecimal.ZERO : amount;
	}

	private void setAmountCodes(Map<String, Object> dataMap, boolean isWIF) {

		boolean finOverDueInPast = false;

		if ((int) dataMap.get("ae_ODInst") > 0) {
			finOverDueInPast = true;
		}

		BigDecimal actualTotalSchdProfit = BigDecimal.ZERO;
		BigDecimal actualTotalCpzProfit = BigDecimal.ZERO;

		if (!(boolean) dataMap.get("ae_newRecord") && !isWIF) {
			List<BigDecimal> list = financeMainDAO.getActualPftBal((String) dataMap.get("fm_finReference"), "");
			actualTotalSchdProfit = list.get(0);
			actualTotalCpzProfit = list.get(1);
		}

		BigDecimal PFTCHG = ((BigDecimal) dataMap.get("ae_pft")).subtract(actualTotalSchdProfit);
		BigDecimal CPZCHG = ((BigDecimal) dataMap.get("ae_cpzTot")).subtract(actualTotalCpzProfit);

		dataMap.put("PFTCHG", PFTCHG);
		dataMap.put("CPZCHG", CPZCHG);
		dataMap.put("finOverDueInPast", finOverDueInPast);

	}

	public void setRuleDAO(RuleDAO ruleDAO) {
		this.ruleDAO = ruleDAO;
	}

	public void setTransactionEntryDAO(TransactionEntryDAO transactionEntryDAO) {
		this.transactionEntryDAO = transactionEntryDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

	public void setCurrencyDAO(CurrencyDAO currencyDAO) {
		this.currencyDAO = currencyDAO;
	}

	public void setAccountInterfaceService(AccountInterfaceService accountInterfaceService) {
		this.accountInterfaceService = accountInterfaceService;
	}

	public void setAccountingSetDAO(AccountingSetDAO accountingSetDAO) {
		this.accountingSetDAO = accountingSetDAO;
	}

}
