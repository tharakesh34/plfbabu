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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.constants.AccountConstants;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.rmtmasters.AccountingSetDAO;
import com.pennant.backend.dao.rmtmasters.TransactionEntryDAO;
import com.pennant.backend.model.eventproperties.EventProperties;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.rmtmasters.TransactionEntry;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.RuleReturnType;
import com.pennant.cache.util.AccountingConfigCache;
import com.pennant.pff.accounting.SingelFeeUtil;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.resource.Literal;

public class AccountEngineExecution {
	private Logger logger = LogManager.getLogger(AccountEngineExecution.class);

	private TransactionEntryDAO transactionEntryDAO;
	private CustomerDAO customerDAO;
	private AccountingSetDAO accountingSetDAO;

	public AccountEngineExecution() {
		super();
	}

	public void getAccEngineExecResults(AEEvent aeEvent) {
		List<ReturnDataSet> returnList = prepareAccountingSetResults(aeEvent);
		aeEvent.setReturnDataSet(returnList);
	}

	public void getReversePostings(List<ReturnDataSet> returnDataSetList, long newLinkedTranID) {
		logger.debug(Literal.ENTERING);

		String tranCode = "";
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
			returnDataSet.setPostDate(appDate);
			returnDataSet.setDrOrCr(
					returnDataSet.getDrOrCr().equals(AccountConstants.TRANTYPE_CREDIT) ? AccountConstants.TRANTYPE_DEBIT
							: AccountConstants.TRANTYPE_CREDIT);

			returnDataSet.setTransOrder(seq);
			seq++;
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * 
	 * @param returnDataSet
	 */
	public void getReversePostings(List<ReturnDataSet> returnDataSetList, long newLinkedTranID, long postingId,
			Date appDate) {
		logger.debug(Literal.ENTERING);

		String tranCode = "";
		// Method for Checking for Reverse Calculations Based upon Negative Amounts
		int seq = 1;

		if (appDate == null) {
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
			returnDataSet.setPostDate(appDate);
			returnDataSet.setDrOrCr(
					returnDataSet.getDrOrCr().equals(AccountConstants.TRANTYPE_CREDIT) ? AccountConstants.TRANTYPE_DEBIT
							: AccountConstants.TRANTYPE_CREDIT);
			returnDataSet.setTransOrder(seq);
			seq++;
		}
		logger.debug(Literal.LEAVING);
	}

	public List<ReturnDataSet> getVasExecResults(AEEvent aeEvent, Map<String, Object> dataMap) {
		logger.debug(Literal.ENTERING);

		// Accounting Set Details
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

		// Method for Checking for Reverse Calculations Based upon Negative Amounts
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

	public List<ReturnDataSet> processAccountingByEvent(AEEvent aeEvent, Map<String, Object> dataMap) {
		logger.debug(Literal.ENTERING);

		String acSetEvent = (String) dataMap.get("ae_finEvent");

		List<TransactionEntry> transactionEntries = null;
		long accountingSetId = accountingSetDAO.getAccountingSetId(acSetEvent, acSetEvent);

		if (accountingSetId != 0) {
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

		List<TransactionEntry> txnEntries = SingelFeeUtil.getTransactionEntries(aeEvent);

		for (TransactionEntry txnEntry : txnEntries) {
			addFeeCodes(dataMap, txnEntry);
		}

		for (TransactionEntry txnEntry : txnEntries) {
			setAccountNumber(aeEvent, txnEntry, dataMap);
		}

		return getReturnDataSet(aeEvent, txnEntries);
	}

	private List<ReturnDataSet> getReturnDataSet(AEEvent aeEvent, List<TransactionEntry> txnEntries) {
		List<ReturnDataSet> returnDataSets = new ArrayList<>();

		Map<String, Object> dataMap = aeEvent.getDataMap();

		int seq = aeEvent.getTransOrder();

		EventProperties eventProperties = aeEvent.getEventProperties();

		boolean isPostZeroEntries = false;
		String appCurrency = null;

		if (eventProperties.isParameterLoaded()) {
			aeEvent.setAppDate(eventProperties.getAppDate());
			aeEvent.setAppValueDate(eventProperties.getAppValueDate());
			aeEvent.setPostDate(aeEvent.getPostDate() == null ? eventProperties.getPostDate() : aeEvent.getPostDate());

			if (StringUtils.equals(eventProperties.getAllowZeroPostings(), "Y")) {
				isPostZeroEntries = true;
			}

			appCurrency = eventProperties.getAppCurrency();
		} else {
			aeEvent.setAppDate(SysParamUtil.getAppDate());
			aeEvent.setAppValueDate(SysParamUtil.getAppValueDate());
			if (aeEvent.getPostDate() == null) {
				aeEvent.setPostDate(SysParamUtil.getPostDate());
			}

			if (StringUtils.equals(SysParamUtil.getValueAsString("ALLOW_ZERO_POSTINGS"), "Y")) {
				isPostZeroEntries = true;
			}

			appCurrency = SysParamUtil.getAppCurrency();
		}

		for (TransactionEntry txnEntry : txnEntries) {
			ReturnDataSet rds = new ReturnDataSet();

			// Set Object Data of ReturnDataSet(s)
			rds.setLinkedTranId(aeEvent.getLinkedTranId());
			rds.setFinID(aeEvent.getFinID());
			rds.setFinReference(aeEvent.getFinReference());
			rds.setFinEvent(aeEvent.getAccountingEvent());
			rds.setLovDescEventCodeName(txnEntry.getLovDescEventCodeDesc());
			rds.setAccSetCodeName(txnEntry.getLovDescAccSetCodeName());
			rds.setAccSetId(txnEntry.getAccountSetid());
			rds.setCustId(aeEvent.getCustID());
			rds.setTranDesc(txnEntry.getTransDesc());
			rds.setPostDate(aeEvent.getPostDate());
			rds.setValueDate(aeEvent.getValueDate());
			rds.setShadowPosting(txnEntry.isShadowPosting());
			rds.setPostToSys(txnEntry.getPostToSys());
			rds.setDerivedTranOrder(txnEntry.getDerivedTranOrder());
			rds.setTransOrder(++seq);
			if (aeEvent.getPostingId() > 0) {
				rds.setPostingId(String.valueOf(aeEvent.getPostingId()));
			} else {
				String ref = aeEvent.getFinReference() + "/" + aeEvent.getAccountingEvent() + "/"
						+ txnEntry.getTransOrder();
				rds.setPostingId(ref);

			}
			String ccy = aeEvent.getCcy();
			if (aeEvent.getPostRefId() <= 0) {
				rds.setPostref(aeEvent.getBranch() + "-" + txnEntry.getAccountType() + "-" + ccy);
			} else {
				rds.setPostref(String.valueOf(aeEvent.getPostRefId()));
			}
			rds.setPostStatus(AccountConstants.POSTINGS_SUCCESS);
			rds.setAmountType(txnEntry.getChargeType());
			rds.setUserBranch(aeEvent.getPostingUserBranch());
			rds.setPostBranch(aeEvent.getBranch());
			rds.setEntityCode(aeEvent.getEntityCode());

			BigDecimal postAmt = executeAmountRule(txnEntry, ccy, dataMap);

			// If parameter flag is 'N' for zero postings not allow to insert zero postings
			if (BigDecimal.ZERO.compareTo(postAmt) == 0 && !isPostZeroEntries) {
				continue;
			}

			if (txnEntry.getAccount() == null || StringUtils.isEmpty(StringUtils.trimToEmpty(txnEntry.getAccount()))) {
				if (BigDecimal.ZERO.compareTo(postAmt) != 0) {
					throw new AppException(String.format(
							"Accounting for %S Event is invalid for order id : %S , please contact administrator",
							aeEvent.getAccountingEvent(), txnEntry.getTransOrder()));
				}
				continue;
			}

			// Un Realized Amortization Income Field Exists
			if (StringUtils.contains(txnEntry.getAmountRule(), "dAmz")) {
				aeEvent.setuAmzExists(true);
			}

			// Un Realized Amortization LPP Field Exists
			if (StringUtils.contains(txnEntry.getAmountRule(), "bpi")) {
				aeEvent.setBpiIncomized(true);
			}

			rds.setTranOrderId(String.valueOf(txnEntry.getTransOrder()));
			rds.setGlCode(txnEntry.getGlCode());
			rds.setAccount(txnEntry.getAccount());

			rds.setAccountType(txnEntry.getAccountType());
			rds.setFinType(aeEvent.getFinType());

			rds.setCustCIF(aeEvent.getCustCIF());
			rds.setPostBranch(aeEvent.getBranch());
			rds.setInternalAc(true);

			// Amount Rule Execution for Amount Calculation
			if (postAmt.compareTo(BigDecimal.ZERO) >= 0) {
				rds.setPostAmount(postAmt);
				rds.setTranCode(txnEntry.getTranscationCode());
				rds.setRevTranCode(txnEntry.getRvsTransactionCode());
				rds.setDrOrCr(txnEntry.getDebitcredit());
			} else {
				rds.setPostAmount(postAmt.abs());
				rds.setRevTranCode(txnEntry.getTranscationCode());
				rds.setTranCode(txnEntry.getRvsTransactionCode());
				if (txnEntry.getDebitcredit().equals(AccountConstants.TRANTYPE_CREDIT)) {
					rds.setDrOrCr(AccountConstants.TRANTYPE_DEBIT);
				} else {
					rds.setDrOrCr(AccountConstants.TRANTYPE_CREDIT);
				}
			}

			// post amount in Local currency
			rds.setPostAmountLcCcy(CalculationUtil.getConvertedAmount(ccy, appCurrency, postAmt));
			rds.setExchangeRate(CurrencyUtil.getExChangeRate(ccy));

			// Converting Post Amount Based on Account Currency
			rds.setAcCcy(ccy);
			rds.setFormatter(CurrencyUtil.getFormat(ccy));

			rds.setPostDate(aeEvent.getPostDate());
			rds.setAppDate(aeEvent.getAppDate());
			rds.setAppValueDate(aeEvent.getAppValueDate());
			rds.setCustAppDate(aeEvent.getCustAppDate());

			if (aeEvent.isEOD()) {
				rds.setPostCategory(AccountConstants.POSTING_CATEGORY_EOD);
			}

			returnDataSets.add(rds);
		}

		if (!aeEvent.isEOD()) {
			if (aeEvent.getCustID() <= 0) {
				aeEvent.setCustAppDate(aeEvent.getPostDate());
			} else {
				aeEvent.setCustAppDate(customerDAO.getCustAppDate(aeEvent.getCustID()));
			}
		}

		aeEvent.setTransOrder(seq);
		return returnDataSets;
	}

	private void addFeeCodes(Map<String, Object> dataMap, TransactionEntry txnEntry) {
		String feeCodes = txnEntry.getFeeCode();

		if (StringUtils.isEmpty(feeCodes)) {
			return;
		}

		for (String feeTypeCode : feeCodes.split(",")) {
			boolean feeExecuted = false;
			for (String code : dataMap.keySet()) {
				if (code.contains(feeTypeCode)) {
					feeExecuted = true;
					break;
				}
			}

			if (feeExecuted) {
				continue;
			}

			FinFeeDetail fd = new FinFeeDetail();

			fd.setFeeTypeCode(feeTypeCode);

			dataMap.putAll(FeeCalculator.getFeeRuleMap(fd));
		}

	}

	private void setAccountNumber(AEEvent aeEvent, TransactionEntry txnEntry, Map<String, Object> dataMap) {
		String accountType = StringUtils.trimToEmpty(txnEntry.getAccountType());
		String subHeadRule = StringUtils.trimToEmpty(txnEntry.getAccountSubHeadRule());

		txnEntry.setAccountType(accountType);
		txnEntry.setAccountSubHeadRule(subHeadRule);
		txnEntry.setGlCode(txnEntry.getGlCode());
		txnEntry.setAccount(txnEntry.getAccount());

		Rule rule = null;
		String accountSubHeadRule = subHeadRule;
		String moduleSubhead = RuleConstants.MODULE_SUBHEAD;

		dataMap.put("acType", txnEntry.getAccountType());
		if (aeEvent.isEOD()) {
			rule = AccountingConfigCache.getCacheRule(accountSubHeadRule, moduleSubhead, moduleSubhead);
		} else {
			rule = AccountingConfigCache.getRule(accountSubHeadRule, moduleSubhead, moduleSubhead);
		}

		if (rule != null) {
			String sqlRule = rule.getSQLRule();
			String ccy = aeEvent.getCcy();

			String accountNumber = (String) RuleExecutionUtil.executeRule(sqlRule, dataMap, ccy, RuleReturnType.STRING);

			if ("null".equalsIgnoreCase(accountNumber)) {
				accountNumber = "";
			}

			txnEntry.setAccount(accountNumber);

			if (aeEvent.isEOD()) {
				txnEntry.setGlCode(AccountingConfigCache.getCacheAccountMapping(txnEntry.getAccount()));
			} else {
				txnEntry.setGlCode(AccountingConfigCache.getAccountMapping(txnEntry.getAccount()));
			}
		}
	}

	private BigDecimal executeAmountRule(TransactionEntry txnEntry, String finCcy, Map<String, Object> dataMap) {
		String amountRule = txnEntry.getAmountRule();
		BigDecimal amount = (BigDecimal) RuleExecutionUtil.executeRule(amountRule, dataMap, finCcy,
				RuleReturnType.DECIMAL);

		return amount == null ? BigDecimal.ZERO : amount;
	}

	public void setTransactionEntryDAO(TransactionEntryDAO transactionEntryDAO) {
		this.transactionEntryDAO = transactionEntryDAO;
	}

	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

	public void setAccountingSetDAO(AccountingSetDAO accountingSetDAO) {
		this.accountingSetDAO = accountingSetDAO;
	}

}
