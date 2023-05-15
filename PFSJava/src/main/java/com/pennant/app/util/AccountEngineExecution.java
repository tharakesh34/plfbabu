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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.constants.AccountConstants;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.rmtmasters.AccountingSetDAO;
import com.pennant.backend.dao.rmtmasters.TransactionEntryDAO;
import com.pennant.backend.model.eventproperties.EventProperties;
import com.pennant.backend.model.finance.FeeType;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.rmtmasters.TransactionEntry;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.RuleReturnType;
import com.pennant.cache.util.AccountingConfigCache;
import com.pennant.pff.accounting.SingleFee;
import com.pennant.pff.fee.AdviseType;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.advancepayment.AdvancePaymentUtil.AdvanceRuleCode;
import com.pennapps.core.util.ObjectUtil;

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

	private void addTxnEntry(TransactionEntry txnEntry, List<TransactionEntry> txnEntries, List<FeeType> feeTypes) {
		int index = txnEntries.indexOf(txnEntry);
		txnEntries.addAll(index, createEntries(txnEntry, feeTypes));
		txnEntries.remove(txnEntry);
	}

	private List<TransactionEntry> getBulkingEntries(Map<String, List<TransactionEntry>> bulkingEntries) {
		List<TransactionEntry> list = new ArrayList<>();

		for (Entry<String, List<TransactionEntry>> items : bulkingEntries.entrySet()) {
			TransactionEntry newTxnEntry = null;

			StringBuilder tsb = new StringBuilder();
			StringBuilder arsb = new StringBuilder();

			for (TransactionEntry item : items.getValue()) {
				if (newTxnEntry == null) {
					newTxnEntry = ObjectUtil.clone(item);
				}

				String amountRule = item.getAmountRule();
				tsb = tsb.append("(" + amountRule.substring(7, amountRule.length() - 1)).append(")+");
			}

			arsb.append(PennantConstants.RESULT.concat(tsb.replace(tsb.length() - 1, tsb.length(), ";").toString()));

			if (newTxnEntry != null) {
				newTxnEntry.setAmountRule(arsb.toString());

				list.add(newTxnEntry);
			}

		}

		return list;
	}

	private List<TransactionEntry> getBulkingEntries(List<TransactionEntry> txnEntries) {
		List<TransactionEntry> list = new ArrayList<>();

		Map<String, List<TransactionEntry>> bulkingEntries = new HashMap<>();

		for (TransactionEntry item : txnEntries) {
			if (item.isBulking()) {
				String key = item.getAccountType().concat(item.getTranscationCode());

				List<TransactionEntry> entries = bulkingEntries.get(key);

				if (entries == null) {
					entries = new ArrayList<>();
				}

				entries.add(item);

				bulkingEntries.put(key, entries);
			} else {
				list.add(item);
			}
		}

		list.addAll(getBulkingEntries(bulkingEntries));

		return list.stream().sorted((b1, b2) -> Integer.compare(b1.getTransOrder(), b2.getTransOrder())).toList();

	}

	private List<ReturnDataSet> prepareAccountingSetResults(AEEvent aeEvent) {
		Map<String, Object> dataMap = aeEvent.getDataMap();
		List<Long> acSetList = aeEvent.getAcSetIDList();
		List<ReturnDataSet> returnDataSets = new ArrayList<>();
		List<TransactionEntry> txnEntries = new ArrayList<>();

		for (Long accountSetId : acSetList) {
			if (aeEvent.isEOD()) {
				txnEntries.addAll(AccountingConfigCache.getCacheTransactionEntry(accountSetId));
			} else {
				txnEntries.addAll(AccountingConfigCache.getTransactionEntry(accountSetId));
			}
		}

		List<FeeType> feeTypes = getNotConfigurerFeeTypes(aeEvent.getFeesList(), txnEntries);

		List<TransactionEntry> list = getSingleFeeTxn(SingleFee.FEE, txnEntries);

		for (TransactionEntry singleFeeTxn : list) {
			for (FeeType feeType : feeTypes) {

				if (!feeType.isManualAdvice()) {
					feeType.setAdviseType(AdviseType.RECEIVABLE.id());
				}

				if (singleFeeTxn.getReceivableOrPayable() == feeType.getAdviseType()) {
					String feeTypeCode = feeType.getFeeTypeCode();

					if ((AdvanceRuleCode.ADVINT.name().equals(feeTypeCode)
							|| AdvanceRuleCode.ADVEMI.name().equals(feeTypeCode)
							|| AdvanceRuleCode.CASHCLT.name().equals(feeTypeCode)
							|| AdvanceRuleCode.DSF.name().equals(feeTypeCode)
							|| FinanceConstants.SUBVEN_FEE.equals(feeTypeCode))) {
						continue;
					}

				}

				if (feeType.isTaxApplicable()) {
					setSingleFeeGSTTxn(singleFeeTxn, txnEntries);
				}

				if (feeType.isTdsReq()) {
					setSingleFeeTDSTxn(singleFeeTxn, txnEntries);
				}

				setSingleFeeWaiverOrRefundTxn(singleFeeTxn, txnEntries);

				String feeIncomeOrExpenseAcType = StringUtils.trimToNull(feeType.getIncomeOrExpenseAcType());
				if (feeIncomeOrExpenseAcType == null) {
					throw new AppException("SingelFee", "Account Type for the Fee Type [" + feeType.getFeeTypeCode()
							+ "] not configured. Please contact the system administrator.");
				}

			}

			addTxnEntry(singleFeeTxn, txnEntries, feeTypes);

			List<TransactionEntry> feeWaiverOrRefundTxnList = singleFeeTxn.getSingleFeeWaiverOrRefundTxn();

			for (TransactionEntry feeWaiverOrRefundTxn : feeWaiverOrRefundTxnList) {
				addTxnEntry(feeWaiverOrRefundTxn, txnEntries, feeTypes);
			}

			List<TransactionEntry> cgstTxnList = singleFeeTxn.getSingleFeeCGSTTxn();
			List<TransactionEntry> sgstTxnList = singleFeeTxn.getSingleFeeSGSTTxn();
			List<TransactionEntry> igstTxnList = singleFeeTxn.getSingleFeeIGSTTxn();
			List<TransactionEntry> ugstTxnListList = singleFeeTxn.getSingleFeeUGSTTxn();
			List<TransactionEntry> cessTxnList = singleFeeTxn.getSingleFeeCESSTxn();
			List<TransactionEntry> tdsTxnList = singleFeeTxn.getSingleFeeTDSTxn();

			for (TransactionEntry cgstTxn : cgstTxnList) {
				addTxnEntry(cgstTxn, txnEntries, feeTypes);
			}

			for (TransactionEntry sgstTxn : sgstTxnList) {
				addTxnEntry(sgstTxn, txnEntries, feeTypes);
			}

			for (TransactionEntry igstTxn : igstTxnList) {
				addTxnEntry(igstTxn, txnEntries, feeTypes);
			}

			for (TransactionEntry ugstTxn : ugstTxnListList) {
				addTxnEntry(ugstTxn, txnEntries, feeTypes);
			}

			for (TransactionEntry cessTxn : cessTxnList) {
				addTxnEntry(cessTxn, txnEntries, feeTypes);
			}

			for (TransactionEntry tdsTxn : tdsTxnList) {
				addTxnEntry(tdsTxn, txnEntries, feeTypes);
			}
		}

		txnEntries = getBulkingEntries(txnEntries);

		int transOrder = 10;

		for (TransactionEntry txn : txnEntries) {
			txn.setTransOrder(transOrder);
			transOrder = transOrder + 10;
		}

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
			addFeeCodes(dataMap, txnEntry);
		}

		for (TransactionEntry txnEntry : txnEntries) {
			setAccountNumber(aeEvent, txnEntry, dataMap);
		}

		int seq = aeEvent.getTransOrder();

		if (!aeEvent.isEOD()) {
			if (aeEvent.getCustID() <= 0) {
				aeEvent.setCustAppDate(aeEvent.getPostDate());
			} else {
				aeEvent.setCustAppDate(customerDAO.getCustAppDate(aeEvent.getCustID()));
			}
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

		aeEvent.setTransOrder(seq);

		return returnDataSets;
	}

	private void setSingleFeeGSTTxn(TransactionEntry singleFeeTxn, List<TransactionEntry> txnEntries) {
		singleFeeTxn.setSingleFeeCGSTTxn(getSingleFeeTxn(SingleFee.CGST, txnEntries));
		singleFeeTxn.setSingleFeeSGSTTxn(getSingleFeeTxn(SingleFee.SGST, txnEntries));
		singleFeeTxn.setSingleFeeUGSTTxn(getSingleFeeTxn(SingleFee.UGST, txnEntries));
		singleFeeTxn.setSingleFeeIGSTTxn(getSingleFeeTxn(SingleFee.IGST, txnEntries));
		singleFeeTxn.setSingleFeeCESSTxn(getSingleFeeTxn(SingleFee.CESS, txnEntries));
	}

	private void setSingleFeeTDSTxn(TransactionEntry singleFeeTxn, List<TransactionEntry> txnEntries) {
		singleFeeTxn.setSingleFeeTDSTxn(getSingleFeeTxn(SingleFee.TDS, txnEntries));
	}

	private void setSingleFeeWaiverOrRefundTxn(TransactionEntry singleFeeTxn, List<TransactionEntry> txnEntries) {
		singleFeeTxn.setSingleFeeWaiverOrRefundTxn(getSingleFeeTxn(SingleFee.WR, txnEntries));
	}

	private List<TransactionEntry> createEntries(TransactionEntry singleFeeTxn, List<FeeType> feeTypes) {
		List<TransactionEntry> list = new ArrayList<>();

		for (FeeType feeType : feeTypes) {
			if (singleFeeTxn.getReceivableOrPayable() == feeType.getAdviseType()) {
				String feeTypeCode = feeType.getFeeTypeCode();

				if ((AdvanceRuleCode.ADVINT.name().equals(feeTypeCode)
						|| AdvanceRuleCode.ADVEMI.name().equals(feeTypeCode)
						|| AdvanceRuleCode.CASHCLT.name().equals(feeTypeCode)
						|| AdvanceRuleCode.DSF.name().equals(feeTypeCode)
						|| FinanceConstants.SUBVEN_FEE.equals(feeTypeCode))) {
					continue;
				}

			}

			list.add(createNewTE(singleFeeTxn, feeType));
		}

		return list;
	}

	private List<FeeType> getNotConfigurerFeeTypes(List<FeeType> feesList, List<TransactionEntry> txnEntries) {
		List<FeeType> feeTypes = new ArrayList<>();

		for (FeeType feeType : feesList) {
			boolean exists = false;
			for (TransactionEntry txnEntry : txnEntries) {
				if (StringUtils.equals(feeType.getFeeTypeCode(), txnEntry.getFeeCode())) {
					exists = true;
					break;
				}
			}

			if (!exists) {
				feeTypes.add(feeType);
			}
		}

		return feeTypes;
	}

	private List<TransactionEntry> getSingleFeeTxn(String feeType, List<TransactionEntry> txnEntries) {
		List<TransactionEntry> list = new ArrayList<>();

		for (TransactionEntry transactionEntry : txnEntries) {
			if (transactionEntry.isSingelFeeEntry()) {
				continue;
			}

			String accountType = transactionEntry.getAccountType();
			if (accountType.equals(feeType) || isSingleFee(transactionEntry.getAmountRule())) {
				list.add(transactionEntry);
				transactionEntry.setSingelFeeEntry(true);
			}
		}

		return list;
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

	private TransactionEntry createNewTE(final TransactionEntry transactionEntry, final FeeType feeType) {
		String feeTypeCode = feeType.getFeeTypeCode();

		logger.debug("Creating transaction entry for feeCode - {}", feeTypeCode);

		TransactionEntry txnEntry = ObjectUtil.clone(transactionEntry);

		String transDesc = txnEntry.getTransDesc();

		String accountType = StringUtils.trimToEmpty(txnEntry.getAccountType());

		if (accountType.startsWith("FEE_IE")) {
			switch (accountType) {
			case SingleFee.CGST:
				txnEntry.setAccountType(feeType.getCgstAcType());
				transDesc = feeType.getCgstAcTypeDesc();
				break;
			case SingleFee.SGST:
				txnEntry.setAccountType(feeType.getSgstAcType());
				transDesc = feeType.getSgstAcTypeDesc();
				break;
			case SingleFee.IGST:
				txnEntry.setAccountType(feeType.getIgstAcType());
				transDesc = feeType.getIgstAcTypeDesc();
				break;
			case SingleFee.UGST:
				txnEntry.setAccountType(feeType.getUgstAcType());
				transDesc = feeType.getUgstAcTypeDesc();
				break;
			case SingleFee.CESS:
				txnEntry.setAccountType(feeType.getCessAcType());
				transDesc = feeType.getCessAcTypeDesc();
				break;
			case SingleFee.TDS:
				txnEntry.setAccountType(feeType.getTdsAcType());
				transDesc = feeType.getTdsAcTypeDesc();
				break;
			case SingleFee.WR:
				txnEntry.setAccountType(feeType.getWaiverOrRefundAcType());
				transDesc = feeType.getWaiverOrRefundAcTypeDesc();
				break;
			default:
				txnEntry.setAccountType(feeType.getIncomeOrExpenseAcType());
				transDesc = feeType.getIncomeOrExpenseAcTypeDesc();
				break;
			}
		} else {
			txnEntry.setAccountType(accountType);

		}

		txnEntry.setTransDesc(transDesc);

		accountType = StringUtils.trimToNull(txnEntry.getAccountType());

		if (accountType == null) {
			throw new AppException("Account Type should configured for the Fee Type: " + feeTypeCode);
		}

		String amountRule = txnEntry.getAmountRule();

		if (isSingleFee(amountRule)) {
			amountRule = amountRule.replace("FEE", feeTypeCode.toUpperCase());
		}

		txnEntry.setAmountRule(amountRule);
		txnEntry.setFeeCode(feeTypeCode);
		return txnEntry;
	}

	private boolean isSingleFee(String amountRule) {
		String[] amountCodes = getAmountCodes(amountRule);

		for (String feeCode : amountCodes) {
			if (feeCode.startsWith("FEE_")) {
				return true;
			}
		}

		return false;
	}

	private String[] getAmountCodes(String amountRule) {
		amountRule = amountRule.replace("Result=", "");
		amountRule = amountRule.replace("Result =", "");
		amountRule = amountRule.replace("Result= ", "");
		amountRule = amountRule.replace("Result = ", "");
		amountRule = amountRule.replace("Result = ", "");

		amountRule = amountRule.trim();

		amountRule = amountRule.replace("(", "");
		amountRule = amountRule.replace(")", "");

		amountRule = amountRule.trim();

		amountRule = amountRule.replace("+", "#");
		amountRule = amountRule.replace(" +", "#");
		amountRule = amountRule.replace("+ ", "#");
		amountRule = amountRule.replace(" + ", "#");

		amountRule = amountRule.trim();

		amountRule = amountRule.replace("-", "#");
		amountRule = amountRule.replace(" -", "#");
		amountRule = amountRule.replace("- ", "#");
		amountRule = amountRule.replace(" - ", "#");

		amountRule = amountRule.trim();

		return amountRule.split("#");
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
