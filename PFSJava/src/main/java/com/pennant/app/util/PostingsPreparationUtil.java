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
 * FileName : PostingsPreparationUtil.java *
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
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.security.auth.login.AccountNotFoundException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.constants.AccountConstants;
import com.pennant.backend.dao.commitment.CommitmentDAO;
import com.pennant.backend.dao.commitment.CommitmentMovementDAO;
import com.pennant.backend.dao.rulefactory.PostingsDAO;
import com.pennant.backend.dao.systemmasters.DivisionDetailDAO;
import com.pennant.backend.model.commitment.Commitment;
import com.pennant.backend.model.others.JVPosting;
import com.pennant.backend.model.others.JVPostingEntry;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.pff.accounting.TransactionType;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.constants.AccountingEvent;

public class PostingsPreparationUtil implements Serializable {
	private static final long serialVersionUID = 1715547921928620037L;
	private Logger logger = LogManager.getLogger(PostingsPreparationUtil.class);

	private AccountEngineExecution engineExecution;
	private PostingsDAO postingsDAO;
	private CommitmentDAO commitmentDAO;
	private CommitmentMovementDAO commitmentMovementDAO;
	private DivisionDetailDAO divisionDetailDAO;

	public PostingsPreparationUtil() {
		super();
	}

	public AEEvent processPostingDetails(AEEvent aeEvent, Map<String, Object> dataMap)
			throws InterfaceException, IllegalAccessException, InvocationTargetException {

		return processPostings(aeEvent, dataMap);
	}

	/**
	 * Method for Process Commitment Posting Details
	 * 
	 * @param commitment
	 * @param aeCommitment
	 * @param isCreateNow
	 * @param dateAppDate
	 * @param acSetEvent
	 * @return
	 * @throws InterfaceException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException List<Object>
	 */
	public AEEvent processCmtPostingDetails(Commitment commitment, Date dateAppDate, String acSetEvent)
			throws InterfaceException, IllegalAccessException, InvocationTargetException {

		return procCmtPostingDetails(commitment, dateAppDate, acSetEvent);
	}

	/**
	 * Method To Process Finance JV Postings IN PostingsPreparationUtil.java
	 * 
	 * @param finReference
	 * @return boolean
	 */
	public List<JVPostingEntry> prepareAccountingEntryList(List<JVPostingEntry> externalAcEntryList, String baseCcy,
			String baseCcyNumber, int baseCcyEditField) {
		return procJVPostings(externalAcEntryList, baseCcy, baseCcyNumber, baseCcyEditField);
	}

	public List<JVPostingEntry> prepareJVPostingEntry(JVPostingEntry externalAcEntry, String baseCcy,
			String baseCcyNumber, int baseCcyEditField, boolean addExt) {
		return procJVPostingEntry(externalAcEntry, baseCcy, baseCcyNumber, baseCcyEditField, addExt);
	}

	public List<ReturnDataSet> processEntryList(List<JVPostingEntry> jvPostingEntryList, JVPosting jVPosting)
			throws InterfaceException {
		return procJVPostingEntryList(jvPostingEntryList, jVPosting);
	}

	public AEEvent processPostings(AEEvent aeEvent)
			throws AccountNotFoundException, IllegalAccessException, InvocationTargetException, InterfaceException {
		return processPostingDetails(aeEvent);
	}

	// ******************************************************//
	// ****************** Process Methods *******************//
	// ******************************************************//

	public AEEvent processPostingDetails(AEEvent aeEvent) throws InterfaceException {
		// Preparation for Commitment Postings
		long linkedTranId = postingsDAO.getLinkedTransId();
		aeEvent.setLinkedTranId(linkedTranId);

		List<ReturnDataSet> returnDatasetList = aeEvent.getReturnDataSet();
		// FIXME: PV: Prepare Return Data Set

		postingsDAO.saveBatch(returnDatasetList);

		return aeEvent;
	}

	private AEEvent processPostings(AEEvent aeEvent, Map<String, Object> dataMap)
			throws InterfaceException, IllegalAccessException, InvocationTargetException {

		List<ReturnDataSet> list = new ArrayList<>();
		engineExecution.getAccEngineExecResults(aeEvent);
		list = aeEvent.getReturnDataSet();

		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();

		// Finance Commitment Reference Posting Details
		Commitment commitment = null;
		boolean cmtEventExecuted = false;
		String cmtReference = aeEvent.getCmtReference();
		if (aeEvent.isAlwCmtPostings() && StringUtils.isNotBlank(cmtReference)
				&& (amountCodes.getRpPri().compareTo(BigDecimal.ZERO) > 0)) {
			commitment = commitmentDAO.getCommitmentById(cmtReference, "");

			if (commitment != null && commitment.isRevolving()) {

				// Remove Commitment Details & Movement Details from Workflow
				// which are in maintenance
				if (aeEvent.isEOD()) {
					Commitment tempcommitment = commitmentDAO.getCommitmentByRef(cmtReference, "_Temp");
					if (tempcommitment != null) {
						commitmentMovementDAO.deleteByRef(cmtReference, "_Temp");
						commitmentDAO.deleteByRef(cmtReference, "_Temp");
					}
				}

				amountCodes.setCmtAmt(BigDecimal.ZERO);
				amountCodes.setChgAmt(BigDecimal.ZERO);
				amountCodes.setDisburse(BigDecimal.ZERO);
				amountCodes.setRpPri(CalculationUtil.getConvertedAmount(aeEvent.getCcy(), commitment.getCmtCcy(),
						amountCodes.getRpPri()));
				aeEvent.setAccountingEvent(AccountingEvent.CMTRPY);
				dataMap = amountCodes.getDeclaredFieldValues(dataMap);
				aeEvent.setDataMap(dataMap);
				engineExecution.getAccEngineExecResults(aeEvent);

				List<ReturnDataSet> cmtList = aeEvent.getReturnDataSet();
				list.addAll(cmtList);

				if (cmtList != null && cmtList.size() > 0) {
					cmtEventExecuted = true;
				}
			}
		}

		aeEvent = postingsExecProcess(aeEvent);

		// FIXME: PV 05MAY17 Update Commitment Movement

		/*
		 * if (cmtEventExecuted && (Boolean) returnList.get(0) && ((BigDecimal)
		 * executingMap.get("ae_rpPri")).compareTo(BigDecimal.ZERO) > 0) {
		 * getCommitmentDAO().updateCommitmentAmounts(commitment.getCmtReference (), ((BigDecimal)
		 * executingMap.get("ae_rpPri")).negate(), commitment.getCmtExpDate()); CommitmentMovement cmtMovement =
		 * prepareCommitMovement(commitment, (Long) returnList.get(1), executingMap); if (cmtMovement != null) {
		 * getCommitmentMovementDAO().save(cmtMovement, ""); } }
		 */
		return aeEvent;
	}

	private AEEvent procCmtPostingDetails(Commitment commitment, Date dateAppDate, String acSetEvent)
			throws InterfaceException, IllegalAccessException, InvocationTargetException {

		List<ReturnDataSet> list = new ArrayList<>();

		AEEvent aeEvent = new AEEvent();
		aeEvent.setAccountingEvent(AccountingEvent.NEWCMT);

		// FIXME: PV dates to be set properly
		aeEvent.setAppDate(dateAppDate);
		aeEvent.setValueDate(dateAppDate);
		aeEvent.setAppValueDate(dateAppDate);
		aeEvent.setPostDate(dateAppDate);

		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
		amountCodes.setCmtAmt(commitment.getCmtAmount());
		amountCodes.setChgAmt(commitment.getCmtCharges());
		amountCodes.setDisburse(BigDecimal.ZERO);
		amountCodes.setRpPri(BigDecimal.ZERO);

		aeEvent.setDataMap(amountCodes.getDeclaredFieldValues());
		engineExecution.getAccEngineExecResults(aeEvent);

		// Accounting Set Execution to get Posting Details List
		list = aeEvent.getReturnDataSet();

		if (list != null && list.size() > 0) {
			aeEvent.setCommitment(false);
			if (acSetEvent.equals(AccountingEvent.NEWCMT) && commitment.isOpenAccount()) {
				aeEvent.setCommitment(true);
			}
			aeEvent = postingsExecProcess(aeEvent);
		}

		return aeEvent;

	}

	private AEEvent postingsExecProcess(AEEvent aeEvent) throws InterfaceException {

		List<ReturnDataSet> list = aeEvent.getReturnDataSet();

		// Method for Checking for Reverse Calculations Based upon Negative
		// Amounts
		for (ReturnDataSet returnDataSet : list) {
			returnDataSet.setLinkedTranId(aeEvent.getLinkedTranId());
			returnDataSet.setUserBranch(aeEvent.getPostingUserBranch());

			if (returnDataSet.getPostAmount().compareTo(BigDecimal.ZERO) < 0) {
				String tranCode = returnDataSet.getTranCode();
				String revTranCode = returnDataSet.getRevTranCode();
				String debitOrCredit = returnDataSet.getDrOrCr();

				returnDataSet.setTranCode(revTranCode);
				returnDataSet.setRevTranCode(tranCode);

				returnDataSet.setPostAmount(returnDataSet.getPostAmount().negate());

				if (debitOrCredit.equals(AccountConstants.TRANTYPE_CREDIT)) {
					returnDataSet.setDrOrCr(AccountConstants.TRANTYPE_DEBIT);
				} else {
					returnDataSet.setDrOrCr(AccountConstants.TRANTYPE_CREDIT);
				}
			}
		}

		if (!list.isEmpty()) {
			if (aeEvent.isPostingSucess()) {
				postingsDAO.saveBatch(list);
			}
		}

		return aeEvent;
	}

	/**
	 * Method to prepare accounting entries for FinancePostings
	 * 
	 * @param JVPostingEntry (List)
	 * @param Base           Currency (String)
	 * @param Base           Currency Number (String)
	 * @param Base           Currency Edit Field (int)
	 */
	private List<JVPostingEntry> procJVPostings(List<JVPostingEntry> jvPostings, String baseCcy, String baseCcyNumber,
			int baseCcyEditField) {
		List<JVPostingEntry> entryList = new ArrayList<>();

		jvPostings.stream().forEach(
				jv -> entryList.addAll(procJVPostingEntry(jv, baseCcy, baseCcyNumber, baseCcyEditField, true)));

		return entryList;
	}

	private List<JVPostingEntry> procJVPostingEntry(JVPostingEntry externalAcEntry, String baseCcy,
			String baseCcyNumber, int baseCcyEditField, boolean addExt) {
		List<JVPostingEntry> entryList = new ArrayList<>();

		// Accounting Entries
		JVPostingEntry internalAcEntryOne = null;
		JVPostingEntry internalAcEntryTwo = null;

		if (addExt) {
			entryList.add(externalAcEntry);
		}

		String actTranType = externalAcEntry.getTxnEntry();

		String drCr = actTranType.equals(AccountConstants.TRANTYPE_DEBIT) ? AccountConstants.TRANTYPE_CREDIT
				: AccountConstants.TRANTYPE_DEBIT;
		String crDr = actTranType.equals(AccountConstants.TRANTYPE_DEBIT) ? AccountConstants.TRANTYPE_DEBIT
				: AccountConstants.TRANTYPE_CREDIT;

		String drCrTranCode = SysParamUtil.getValueAsString("CCYCNV_" + drCr + "RTRANCODE");
		String crDrTranCode = SysParamUtil.getValueAsString("CCYCNV_" + crDr + "RTRANCODE");

		if (!externalAcEntry.getAccCCy().equals(baseCcy)) {
			// Internal Account Entry
			internalAcEntryOne = new JVPostingEntry();
			internalAcEntryTwo = new JVPostingEntry();

			BeanUtils.copyProperties(externalAcEntry, internalAcEntryOne);
			BeanUtils.copyProperties(externalAcEntry, internalAcEntryTwo);

			internalAcEntryOne.setNewRecord(externalAcEntry.isNewRecord());
			internalAcEntryOne.setTxnAmount_Ac(externalAcEntry.getTxnAmount_Ac());
			internalAcEntryOne.setAccountName("");
			internalAcEntryOne.setExternalAccount(false);
			internalAcEntryOne.setTxnEntry(drCr);
			internalAcEntryOne.setAccCCy(externalAcEntry.getAccCCy());
			internalAcEntryOne.setAcEntryRef(2);
			internalAcEntryOne.setTxnCode(drCrTranCode);
			internalAcEntryOne.setRevTxnCode(crDrTranCode);
			internalAcEntryOne.setAccount(
					(externalAcEntry.getAccount().length() > 4 ? externalAcEntry.getAccount().substring(0, 4)
							: externalAcEntry.getAccount()) + "881"
							+ CurrencyUtil.getFormat(externalAcEntry.getAccCCy()) + baseCcyNumber);
			internalAcEntryOne.setAcType("");
			internalAcEntryOne.setTxnAmount_Batch(CalculationUtil.getConvertedAmount(internalAcEntryOne.getTxnCCy(),
					baseCcy, internalAcEntryOne.getTxnAmount()));
			internalAcEntryTwo.setNewRecord(externalAcEntry.isNewRecord());
			internalAcEntryTwo.setTxnAmount_Ac(CalculationUtil.getConvertedAmount(internalAcEntryOne.getTxnCCy(),
					baseCcy, internalAcEntryOne.getTxnAmount()));
			internalAcEntryTwo.setAccountName("");
			internalAcEntryTwo.setExternalAccount(false);
			internalAcEntryTwo.setTxnEntry(crDr);
			internalAcEntryTwo.setAccCCy(baseCcy);
			internalAcEntryTwo.setAcEntryRef(3);
			internalAcEntryTwo.setTxnCode(crDrTranCode);
			internalAcEntryTwo.setRevTxnCode(drCrTranCode);
			internalAcEntryTwo.setAccount(
					(externalAcEntry.getAccount().length() > 4 ? externalAcEntry.getAccount().substring(0, 4)
							: externalAcEntry.getAccount()) + "881" + baseCcyNumber
							+ CurrencyUtil.getFormat(externalAcEntry.getAccCCy()));
			internalAcEntryTwo.setAcType("");
			internalAcEntryTwo.setTxnAmount_Batch(CalculationUtil.getConvertedAmount(internalAcEntryOne.getTxnCCy(),
					baseCcy, internalAcEntryOne.getTxnAmount()));

			String txnEntry = internalAcEntryOne.getTxnEntry();
			if (addExt && txnEntry.equals(AccountConstants.TRANTYPE_DEBIT)) {
				internalAcEntryOne.setTxnAmount_Ac(internalAcEntryOne.getTxnAmount_Ac().multiply(new BigDecimal(-1)));
			}
			BigDecimal txnAmount_Ac = internalAcEntryTwo.getTxnAmount_Ac();
			String txnEntry2 = internalAcEntryTwo.getTxnEntry();
			if (addExt && txnEntry2.equals(AccountConstants.TRANTYPE_DEBIT)) {
				internalAcEntryTwo.setTxnAmount_Ac(txnAmount_Ac.multiply(new BigDecimal(-1)));
			}

			logger.info(internalAcEntryOne.getAccount() + " ONE " + txnEntry + " " + txnAmount_Ac);
			logger.info(internalAcEntryTwo.getAccount() + "  TWO " + txnEntry2 + " " + txnAmount_Ac);
		}
		if (addExt && externalAcEntry.getTxnEntry().equals(AccountConstants.TRANTYPE_DEBIT)) {
			if (externalAcEntry.getTxnAmount_Ac().compareTo(BigDecimal.ZERO) > 0) {
				externalAcEntry.setTxnAmount_Ac(externalAcEntry.getTxnAmount_Ac().multiply(new BigDecimal(-1)));
			}
		}

		if (internalAcEntryOne != null && internalAcEntryTwo != null) {
			entryList.add(internalAcEntryOne);
			entryList.add(internalAcEntryTwo);
		}

		return entryList;
	}

	private List<ReturnDataSet> procJVPostingEntryList(List<JVPostingEntry> jvPostingEntryList, JVPosting jVPosting)
			throws InterfaceException {

		List<ReturnDataSet> list = new ArrayList<>();
		long linkedTranId = Long.MIN_VALUE;
		ReturnDataSet returnDataSet = null;
		String appCurrency = SysParamUtil.getAppCurrency();
		Date appDate = SysParamUtil.getAppDate();

		for (JVPostingEntry jvPostingEntry : jvPostingEntryList) {
			returnDataSet = new ReturnDataSet();
			// Set Object Data of ReturnDataSet(s)
			returnDataSet.setFinReference(String.valueOf(jVPosting.getBatchReference()));
			returnDataSet.setAccount(jvPostingEntry.getAccount());
			returnDataSet.setAcCcy(jvPostingEntry.getAccCCy());
			returnDataSet.setAccountType(StringUtils.trim(jvPostingEntry.getAcType()));
			returnDataSet.setPostAmount(jvPostingEntry.getTxnAmount());
			returnDataSet.setTranOrderId(String.valueOf(jvPostingEntry.getAcEntryRef()));

			returnDataSet.setPostAmountLcCcy(CalculationUtil.getConvertedAmount(returnDataSet.getAcCcy(), appCurrency,
					returnDataSet.getPostAmount()));

			if (!jvPostingEntryList.isEmpty()) {
				// Method for validating Postings with interface program and
				// return results
				if (jvPostingEntryList.get(0).getLinkedTranId() == Long.MIN_VALUE && linkedTranId == Long.MIN_VALUE) {
					linkedTranId = postingsDAO.getLinkedTransId();
				}
			}

			returnDataSet.setLinkedTranId(linkedTranId);
			returnDataSet.setFinEvent("JVPOST");
			returnDataSet.setTranDesc(jvPostingEntry.getAccountName());
			returnDataSet.setPostDate(jvPostingEntry.getPostingDate());
			returnDataSet.setValueDate(jvPostingEntry.getValueDate());
			returnDataSet.setAppDate(appDate);
			returnDataSet.setAppValueDate(appDate);
			returnDataSet.setTranCode(jvPostingEntry.getTxnCode());
			returnDataSet.setDrOrCr(jvPostingEntry.getTxnEntry());
			returnDataSet.setShadowPosting(false);
			returnDataSet.setFlagCreateIfNF(true);
			returnDataSet.setFlagCreateNew(false);
			returnDataSet.setPostBranch(jVPosting.getBranch());

			if (TransactionType.CREDIT.code().equals(jvPostingEntry.getTxnEntry())) {
				returnDataSet.setRevTranCode(AccountConstants.TRANCODE_DEBIT);
			} else {
				returnDataSet.setRevTranCode(AccountConstants.TRANCODE_CREDIT);
			}

			returnDataSet.setPostToSys("E");
			returnDataSet.setAmountType("D");
			returnDataSet.setUserBranch(jVPosting.getUserDetails().getBranchCode());
			returnDataSet.setCustAppDate(appDate);
			returnDataSet.setEntityCode(divisionDetailDAO.getEntityCodeByDivision(jVPosting.getPostingDivision(), ""));
			String ref = returnDataSet.getFinReference() + "/" + returnDataSet.getFinEvent() + "/";
			returnDataSet.setPostingId(ref);

			returnDataSet.setPostref(jVPosting.getBranch() + "-" + StringUtils.trim(jvPostingEntry.getAcType()) + "-"
					+ jvPostingEntry.getTxnCCy());

			if (jvPostingEntry.isExternalAccount()) {
				returnDataSet.setInternalAc(false);
				returnDataSet.setCustCIF(jVPosting.getBranch());
			} else {
				returnDataSet.setInternalAc(true);
				returnDataSet.setCustCIF("");
				returnDataSet.setAccountType("SP101");
			}
			list.add(returnDataSet);
		}

		// FIXME: PV: 05MAY17 needs to fill return dataset
		return list;
	}

	/**
	 * Method to Prepare the accounting entries and save the postings to the Postings and accounts table
	 * 
	 * @param aeEvent
	 * @param dataMap
	 * @return
	 */
	public AEEvent postAccounting(AEEvent aeEvent) {
		logger.debug(Literal.ENTERING);

		if (aeEvent.getLinkedTranId() <= 0) {
			aeEvent.setLinkedTranId(postingsDAO.getLinkedTransId());
		}

		engineExecution.getAccEngineExecResults(aeEvent);

		List<ReturnDataSet> returnDatasetList = aeEvent.getReturnDataSet();
		if (!aeEvent.isPostingSucess()) {
			return aeEvent;
		}

		if (CollectionUtils.isEmpty(returnDatasetList) || aeEvent.isSimulateAccounting()) {
			return aeEvent;
		}

		validateCreditandDebitAmounts(aeEvent);

		returnDatasetList.stream().forEach(r -> {
			String entityCode = aeEvent.getEntityCode();
			if (entityCode == null) {
				entityCode = aeEvent.getEventProperties().getEntityCode();
			}
			r.setEntityCode(entityCode);
		});

		postingsDAO.saveBatch(returnDatasetList);

		logger.debug(Literal.LEAVING);
		return aeEvent;
	}

	/**
	 * Method to Prepare the accounting entries and save the postings to the Postings and accounts table
	 * 
	 * @param aeEvent
	 * @param dataMap
	 * @return
	 */
	public AEEvent getAccounting(AEEvent aeEvent) {
		logger.debug(Literal.ENTERING);

		/*
		 * if (aeEvent.getLinkedTranId() <= 0) { aeEvent.setLinkedTranId(getPostingsDAO().getLinkedTransId()); }
		 */
		engineExecution.getAccEngineExecResults(aeEvent);

		logger.debug(Literal.LEAVING);
		return aeEvent;
	}

	public void postAccountingEOD(AEEvent aeEvent) {
		if (aeEvent.getLinkedTranId() <= 0) {
			aeEvent.setLinkedTranId(postingsDAO.getLinkedTransId());
		}

		engineExecution.getAccEngineExecResults(aeEvent);

	}

	public void saveAccountingEOD(List<ReturnDataSet> returnDatasetList) {
		if (CollectionUtils.isEmpty(returnDatasetList)) {
			return;
		}
		postingsDAO.saveBatch(returnDatasetList);
	}

	public List<ReturnDataSet> postReveralsByFinreference(String reference) {
		logger.debug(Literal.ENTERING);

		List<ReturnDataSet> returnDataSets = getReveralsByFinreference(reference);

		postingsDAO.updateStatusByFinRef(reference, AccountConstants.POSTINGS_REVERSE);

		postingsDAO.saveBatch(returnDataSets);

		logger.debug(Literal.LEAVING);
		return returnDataSets;
	}

	/**
	 * 
	 * @param linkedTranId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws InterfaceException
	 */
	public List<ReturnDataSet> postReversalsByLinkedTranID(long linkedTranId) {
		logger.debug(Literal.ENTERING);

		List<ReturnDataSet> returnDataSets = getReversalsByLinkedTranID(linkedTranId);

		postingsDAO.updateStatusByLinkedTranId(linkedTranId, AccountConstants.POSTINGS_REVERSE);

		postingsDAO.saveBatch(returnDataSets);

		logger.debug(Literal.LEAVING);
		return returnDataSets;
	}

	public long reversalByLinkedTranID(long linkedTranId) {
		logger.debug(Literal.ENTERING);

		long newLinkedTranID = postingsDAO.getLinkedTransId();

		List<ReturnDataSet> returnDataSets = postingsDAO.getPostingsByLinkTransId(linkedTranId);

		engineExecution.getReversePostings(returnDataSets, newLinkedTranID);

		logger.debug(Literal.ENTERING);
		return newLinkedTranID;
	}

	/**
	 * 
	 * @param linkedTranId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws InterfaceException
	 */
	public List<ReturnDataSet> postReversalsByPostRef(String postRef, long postingId, Date appDate) {
		logger.debug(Literal.ENTERING);

		List<ReturnDataSet> returnDataSets = getReversalsByPostRef(postRef, postingId, appDate);

		postingsDAO.updateStatusByPostRef(postRef, AccountConstants.POSTINGS_REVERSE);

		postingsDAO.saveBatch(returnDataSets);

		logger.debug(Literal.LEAVING);
		return returnDataSets;
	}

	/**
	 * 
	 * @param linkedTranId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws InterfaceException
	 */
	public List<ReturnDataSet> getReversalsByLinkedTranID(long linkedTranId) {
		logger.debug(Literal.ENTERING);

		long newLinkedTranID = postingsDAO.getLinkedTransId();

		List<ReturnDataSet> returnDataSets = postingsDAO.getPostingsByLinkTransId(linkedTranId);

		engineExecution.getReversePostings(returnDataSets, newLinkedTranID);

		logger.debug(Literal.LEAVING);
		return returnDataSets;
	}

	/**
	 * 
	 * @param postingId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws InterfaceException
	 */
	public List<ReturnDataSet> getReversalsByPostRef(String postRef, long postingId, Date appDate) {
		logger.debug(Literal.ENTERING);

		long newLinkedTranID = postingsDAO.getLinkedTransId();

		List<ReturnDataSet> returnDataSets = postingsDAO.getPostingsByPostRef(postRef);

		if (returnDataSets.isEmpty()) {
			logger.debug(Literal.LEAVING);
			return returnDataSets;
		}

		engineExecution.getReversePostings(returnDataSets, newLinkedTranID, postingId, appDate);

		logger.debug(Literal.LEAVING);
		return returnDataSets;
	}

	public List<ReturnDataSet> getReveralsByFinreference(String reference) {
		logger.debug(Literal.ENTERING);

		long newLinkedTranID = postingsDAO.getLinkedTransId();
		List<ReturnDataSet> returnDataSets = postingsDAO.getPostingsByFinRef(reference, false);

		engineExecution.getReversePostings(returnDataSets, newLinkedTranID);

		logger.debug(Literal.LEAVING);
		return returnDataSets;
	}

	public void validateCreditandDebitAmounts(AEEvent aeEvent) {

		BigDecimal creditAmt = BigDecimal.ZERO;
		BigDecimal debitAmt = BigDecimal.ZERO;

		List<ReturnDataSet> dataset = aeEvent.getReturnDataSet();

		for (ReturnDataSet returnDataSet : dataset) {
			if ("C".equals(returnDataSet.getDrOrCr())) {
				creditAmt = creditAmt.add(returnDataSet.getPostAmount());
			} else {
				debitAmt = debitAmt.add(returnDataSet.getPostAmount());
			}
		}

		if (creditAmt.compareTo(debitAmt) != 0) {
			throw new InterfaceException("9998",
					"Total credits and Total debits are not matched.Please check accounting configuration.");
		}
	}

	public List<ReturnDataSet> postReveralsExceptFeePay(String reference) {
		logger.debug(Literal.ENTERING);

		List<ReturnDataSet> returnDataSets = new ArrayList<>();
		Set<Long> linkedTranIds = new LinkedHashSet<>();
		List<ReturnDataSet> dataSetList = getReveralsByFinreference(reference);

		for (ReturnDataSet returnDataSet : dataSetList) {
			if (!AccountingEvent.FEEPAY.equalsIgnoreCase(returnDataSet.getFinEvent())
					&& !AccountingEvent.INDAS.equalsIgnoreCase(returnDataSet.getFinEvent())) {
				returnDataSets.add(returnDataSet);
				linkedTranIds.add(returnDataSet.getLinkedTranId());
			}
		}

		for (Long linkedTranId : linkedTranIds) {
			postingsDAO.updateStatusByLinkedTranId(linkedTranId, AccountConstants.POSTINGS_REVERSE);
		}

		postingsDAO.saveBatch(returnDataSets);

		logger.debug(Literal.LEAVING);
		return returnDataSets;
	}

	public AccountEngineExecution getEngineExecution() {
		return engineExecution;
	}

	public void setEngineExecution(AccountEngineExecution engineExecution) {
		this.engineExecution = engineExecution;
	}

	public void setPostingsDAO(PostingsDAO postingsDAO) {
		this.postingsDAO = postingsDAO;
	}

	public void setCommitmentDAO(CommitmentDAO commitmentDAO) {
		this.commitmentDAO = commitmentDAO;
	}

	public void setCommitmentMovementDAO(CommitmentMovementDAO commitmentMovementDAO) {
		this.commitmentMovementDAO = commitmentMovementDAO;
	}

	public void setDivisionDetailDAO(DivisionDetailDAO divisionDetailDAO) {
		this.divisionDetailDAO = divisionDetailDAO;
	}
}
