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

import com.pennant.app.constants.AccountConstants;
import com.pennant.app.constants.AccountEventConstants;
import com.pennant.backend.dao.commitment.CommitmentDAO;
import com.pennant.backend.dao.commitment.CommitmentMovementDAO;
import com.pennant.backend.dao.finance.FinContributorDetailDAO;
import com.pennant.backend.dao.rmtmasters.FinTypeAccountingDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.dao.rulefactory.PostingsDAO;
import com.pennant.backend.model.commitment.Commitment;
import com.pennant.backend.model.commitment.CommitmentMovement;
import com.pennant.backend.model.others.JVPosting;
import com.pennant.backend.model.others.JVPostingEntry;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennanttech.pennapps.core.InterfaceException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.security.auth.login.AccountNotFoundException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

/**
 * @author chaitanya.ch
 *
 */
/**
 * @author chaitanya.ch
 *
 */
public class PostingsPreparationUtil implements Serializable {
	private static final long			serialVersionUID	= 1715547921928620037L;
	private Logger						logger				= Logger.getLogger(PostingsPreparationUtil.class);

	private AccountEngineExecution		engineExecution;
	private FinContributorDetailDAO		finContributorDetailDAO;
	private PostingsDAO					postingsDAO;
	private AccountProcessUtil			accountProcessUtil;
	private CommitmentDAO				commitmentDAO;
	private CommitmentMovementDAO		commitmentMovementDAO;
	//private FinanceCancellationProcess	financeCancellationProcess;
	private FinanceTypeDAO				financeTypeDAO;
	private FinTypeAccountingDAO		finTypeAccountingDAO;

	public PostingsPreparationUtil() {
		super();
	}
	
	public AEEvent processPostingDetails(AEEvent aeEvent, HashMap<String, Object> dataMap)
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
	 * @throws InvocationTargetException
	 *             List<Object>
	 */
	public AEEvent processCmtPostingDetails(Commitment commitment, Date dateAppDate, String acSetEvent)
			throws InterfaceException, IllegalAccessException, InvocationTargetException {

		return procCmtPostingDetails(commitment, dateAppDate, acSetEvent);
	}


	/**
	 * Method To Process Finance Disbursement Cancellation posting IN PostingsPreparationUtil.java
	 * 
	 * @param finReference
	 * @return boolean
	 */
	public List<Object> processFinCanclPostings(String finReference, String linkedTranId) {
		return procFinCanclPostings(finReference, linkedTranId);
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
 

	public AEEvent processPostings(AEEvent aeEvent) throws AccountNotFoundException, IllegalAccessException,
	InvocationTargetException, InterfaceException {
		return processPostingDetails(aeEvent);
	}

	// ******************************************************//
	// ****************** Process Methods *******************//
	// ******************************************************//

	public AEEvent processPostingDetails(AEEvent aeEvent) throws AccountNotFoundException, InterfaceException {
		// Preparation for Commitment Postings
		long linkedTranId = getPostingsDAO().getLinkedTransId();
		aeEvent.setLinkedTranId(linkedTranId);

		List<ReturnDataSet> returnDatasetList = aeEvent.getReturnDataSet();
		//FIXME: PV: Prepare Return Data Set

		getPostingsDAO().saveBatch(returnDatasetList);
		getAccountProcessUtil().procAccountUpdate(returnDatasetList);


		return aeEvent;
	}

	private AEEvent processPostings(AEEvent aeEvent, HashMap<String, Object> dataMap) throws InterfaceException,
	IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		List<ReturnDataSet> list = new ArrayList<ReturnDataSet>();
		aeEvent = getEngineExecution().getAccEngineExecResults(aeEvent);
		list = aeEvent.getReturnDataSet();

		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();

		// Finance Commitment Reference Posting Details
		Commitment commitment = null;
		boolean cmtEventExecuted = false;
		if (aeEvent.isAlwCmtPostings() && StringUtils.isNotBlank(aeEvent.getCmtReference())
				&& (amountCodes.getRpPri().compareTo(BigDecimal.ZERO) > 0)) {
			commitment = getCommitmentDAO().getCommitmentById(aeEvent.getCmtReference(), "");

			if (commitment != null && commitment.isRevolving()) {

				//Remove Commitment Details & Movement Details from Workflow which are in maintenance
				if (aeEvent.isEOD()) {
					Commitment tempcommitment = getCommitmentDAO().getCommitmentByRef(aeEvent.getCmtReference(),
							"_Temp");
					if (tempcommitment != null) {
						getCommitmentMovementDAO().deleteByRef(aeEvent.getCmtReference(), "_Temp");
						getCommitmentDAO().deleteByRef(aeEvent.getCmtReference(), "_Temp");
					}
				}

				amountCodes.setCmtAmt(BigDecimal.ZERO);
				amountCodes.setChgAmt(BigDecimal.ZERO);
				amountCodes.setDisburse(BigDecimal.ZERO);
				amountCodes.setRpPri(CalculationUtil.getConvertedAmount(aeEvent.getCcy(), commitment.getCmtCcy(),
						amountCodes.getRpPri()));
				aeEvent.setAccountingEvent(AccountEventConstants.ACCEVENT_CMTRPY);
				dataMap = amountCodes.getDeclaredFieldValues(dataMap);
				aeEvent.setDataMap(dataMap);
				aeEvent = getEngineExecution().getAccEngineExecResults(aeEvent);

				List<ReturnDataSet> cmtList = aeEvent.getReturnDataSet();
				list.addAll(cmtList);

				if (cmtList != null && cmtList.size() > 0) {
					cmtEventExecuted = true;
				}
			}
		}

		aeEvent = postingsExecProcess(aeEvent);

		//FIXME: PV 05MAY17 Update Commitment Movement

		/*
		 * if (cmtEventExecuted && (Boolean) returnList.get(0) && ((BigDecimal)
		 * executingMap.get("ae_rpPri")).compareTo(BigDecimal.ZERO) > 0) {
		 * getCommitmentDAO().updateCommitmentAmounts(commitment.getCmtReference(), ((BigDecimal)
		 * executingMap.get("ae_rpPri")).negate(), commitment.getCmtExpDate()); CommitmentMovement cmtMovement =
		 * prepareCommitMovement(commitment, (Long) returnList.get(1), executingMap); if (cmtMovement != null) {
		 * getCommitmentMovementDAO().save(cmtMovement, ""); } }
		 */
		logger.debug("Leaving");
		return aeEvent;
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
	 * @throws InvocationTargetException
	 *             List<Object>
	 */
	private AEEvent procCmtPostingDetails(Commitment commitment, Date dateAppDate, String acSetEvent)
			throws InterfaceException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		List<ReturnDataSet> list = new ArrayList<ReturnDataSet>();

		AEEvent aeEvent = new AEEvent();
		aeEvent.setAccountingEvent(AccountEventConstants.ACCEVENT_NEWCMT);

		//FIXME: PV dates to be set properly
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
		HashMap<String, Object> dataMap = aeEvent.getDataMap();
		aeEvent = getEngineExecution().getAccEngineExecResults(aeEvent);

		// Accounting Set Execution to get Posting Details List
		list = aeEvent.getReturnDataSet();

		if (list != null && list.size() > 0) {
			aeEvent.setCommitment(false);
			if (acSetEvent.equals(AccountEventConstants.ACCEVENT_NEWCMT) && commitment.isOpenAccount()) {
				aeEvent.setCommitment(true);
			}
			aeEvent = postingsExecProcess(aeEvent);
		}

		logger.debug("Leaving");
		return aeEvent;

	}

	/**
	 * Method for Add a Movement Entry for Commitment Repayment Event, if Only for Revolving Commitment
	 * 
	 * @param commitment
	 * @param dataSet
	 * @param postAmount
	 * @param linkedtranId
	 * @return
	 */
	private CommitmentMovement prepareCommitMovement(Commitment commitment, long linkedtranId,
			HashMap<String, Object> dataMap) {
		logger.debug("Entering");
		CommitmentMovement movement = new CommitmentMovement();

		BigDecimal postAmount = ((BigDecimal) dataMap.get("ae_rpPri"));

		Date curBussDate = DateUtility.getAppDate();

		movement.setCmtReference(commitment.getCmtReference());
		movement.setFinReference((String) dataMap.get("fm_finReference"));
		movement.setFinBranch((String) dataMap.get("fm_finBranch"));
		movement.setFinType((String) dataMap.get("fm_finType"));
		movement.setMovementDate(curBussDate);
		movement.setMovementOrder(getCommitmentMovementDAO().getMaxMovementOrderByRef(commitment.getCmtReference()) + 1);
		movement.setMovementType("RA");
		movement.setMovementAmount(postAmount);
		movement.setCmtAmount(commitment.getCmtAmount());
		movement.setCmtUtilizedAmount(commitment.getCmtUtilizedAmount().subtract(postAmount));
		if (commitment.getCmtExpDate().compareTo(curBussDate) < 0) {
			movement.setCmtAvailable(BigDecimal.ZERO);
		} else {
			movement.setCmtAvailable(commitment.getCmtAvailable().add(postAmount));
		}
		movement.setCmtCharges(BigDecimal.ZERO);
		movement.setLinkedTranId(linkedtranId);
		movement.setVersion(1);
		movement.setLastMntBy(9999);
		movement.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		movement.setRecordStatus("Approved");
		movement.setRoleCode("");
		movement.setNextRoleCode("");
		movement.setTaskId("");
		movement.setNextTaskId("");
		movement.setRecordType("");
		movement.setWorkflowId(0);
		logger.debug("Leaving");
		return movement;

	}

	private AEEvent postingsExecProcess(AEEvent aeEvent) throws InterfaceException {
		logger.debug("Entering");

		List<ReturnDataSet> list = aeEvent.getReturnDataSet();

		//Method for Checking for Reverse Calculations Based upon Negative Amounts
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
				getPostingsDAO().saveBatch(list);
				//getAccountProcessUtil().updateAccountInfo(list);
			}
		}

		logger.debug("Leaving");
		return aeEvent;
	}

	/**
	 * Method To Process Finance Disbursement Cancellation posting IN PostingsPreparationUtil.java
	 * 
	 * @param finReference
	 * @return boolean
	 */
	private List<Object> procFinCanclPostings(String finReference, String linkedTranId) {
		logger.debug("Entering");
		boolean postingSuccess = true;// interface not implemented for postings so after developed need to change as false
		String errorMsg = null;

		List<Object> returnList = new ArrayList<Object>();
		/*try {
			// Call To Finance Disbursement Cancellation posting  interface 
			List<FinanceCancellation> list = getFinanceCancellationProcess().fetchCancelledFinancePostings(
					finReference, linkedTranId);
			if (list != null && list.size() > 0) {
				FinanceCancellation cancellation = list.get(0);
				//Check For errors
				if (StringUtils.isBlank(cancellation.getDsRspErrD())) {
					if (!StringUtils.equals(cancellation.getDsReqLnkTID(), "XXXX")) {
						updateCancelledPosting(list);
					}
					postingSuccess = true;
				}
			}
		} catch (InterfaceException e) {
			logger.debug(e);
			errorMsg = e.getErrorMessage();
		} catch (Exception e) {
			logger.debug(e);
			errorMsg = e.getMessage();
		}

		returnList.add(postingSuccess);
		returnList.add(errorMsg);

		logger.debug("Leaving");*/
		return returnList;
	}

	/**
	 * To Update Posting with the Response from the Finance Disbursement Cancellation Interface. <br>
	 * IN PostingsPreparationUtil.java
	 * 
	 * @param financeCancellations
	 *//*
	private void updateCancelledPosting(List<FinanceCancellation> financeCancellations) {
		logger.debug("Entering");

		// Create object for postings(Posting table object)
		List<ReturnDataSet> returnDataSets = new ArrayList<ReturnDataSet>(financeCancellations.size());
		ReturnDataSet dataSet = null;
		for (FinanceCancellation finCanl : financeCancellations) {
			dataSet = new ReturnDataSet();
			dataSet.setLinkedTranId(Long.parseLong(finCanl.getDsRspLnkTID()));
			dataSet.setPostref(finCanl.getDsRspPostRef());
			dataSet.setFinReference(finCanl.getDsRspFinRef());
			dataSet.setFinEvent(finCanl.getDsRspFinEvent());
			dataSet.setPostDate(DateUtility.convertDateFromAS400(new BigDecimal(finCanl.getDsRspPOD())));
			dataSet.setAccount(finCanl.getDsRspAB() + finCanl.getDsRspAN() + finCanl.getDsRspAS());
			dataSet.setPostStatus(finCanl.getDsRspStatus());
			dataSet.setErrorId(finCanl.getDsRspErr());
			dataSet.setErrorMsg(finCanl.getDsRspErrD());
			returnDataSets.add(dataSet);
		}

		if (!returnDataSets.isEmpty()) {
			getPostingsDAO().updateBatch(returnDataSets, "");
		}
		logger.debug("Leaving");
	}*/

	/**
	 * Method to prepare accounting entries for FinancePostings
	 * 
	 * @param JVPostingEntry
	 *            (List)
	 * @param Base
	 *            Currency (String)
	 * @param Base
	 *            Currency Number (String)
	 * @param Base
	 *            Currency Edit Field (int)
	 */
	private List<JVPostingEntry> procJVPostings(List<JVPostingEntry> externalAcEntryList, String baseCcy,
			String baseCcyNumber, int baseCcyEditField) {
		logger.debug("Entering");
		List<JVPostingEntry> entryList = new ArrayList<JVPostingEntry>();
		for (JVPostingEntry jvPostingEntry : externalAcEntryList) {
			entryList.addAll(procJVPostingEntry(jvPostingEntry, baseCcy, baseCcyNumber, baseCcyEditField, true));
		}
		logger.debug("Leaving");
		return entryList;
	}

	private List<JVPostingEntry> procJVPostingEntry(JVPostingEntry externalAcEntry, String baseCcy,
			String baseCcyNumber, int baseCcyEditField, boolean addExt) {
		logger.debug("Entering");
		List<JVPostingEntry> entryList = new ArrayList<JVPostingEntry>();

		//Accounting Entries		
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
			internalAcEntryOne.setTxnCode(SysParamUtil.getValueAsString("CCYCNV_" + drCr + "RTRANCODE"));
			internalAcEntryOne.setRevTxnCode(SysParamUtil.getValueAsString("CCYCNV_" + crDr + "RTRANCODE"));

			internalAcEntryOne.setAccount((externalAcEntry.getAccount().length() > 4 ? externalAcEntry.getAccount()
					.substring(0, 4) : externalAcEntry.getAccount())
					+ "881"
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
			internalAcEntryTwo.setTxnCode(SysParamUtil.getValueAsString("CCYCNV_" + crDr + "RTRANCODE"));
			internalAcEntryTwo.setRevTxnCode(SysParamUtil.getValueAsString("CCYCNV_" + drCr + "RTRANCODE"));
			internalAcEntryTwo.setAccount((externalAcEntry.getAccount().length() > 4 ? externalAcEntry.getAccount()
					.substring(0, 4) : externalAcEntry.getAccount())
					+ "881"
					+ baseCcyNumber
					+ CurrencyUtil.getFormat(externalAcEntry.getAccCCy()));
			internalAcEntryTwo.setAcType("");
			internalAcEntryTwo.setTxnAmount_Batch(CalculationUtil.getConvertedAmount(internalAcEntryOne.getTxnCCy(),
					baseCcy, internalAcEntryOne.getTxnAmount()));

			if (addExt && internalAcEntryOne.getTxnEntry().equals(AccountConstants.TRANTYPE_DEBIT)) {
				internalAcEntryOne.setTxnAmount_Ac(internalAcEntryOne.getTxnAmount_Ac().multiply(new BigDecimal(-1)));
			}
			if (addExt && internalAcEntryTwo.getTxnEntry().equals(AccountConstants.TRANTYPE_DEBIT)) {
				internalAcEntryTwo.setTxnAmount_Ac(internalAcEntryTwo.getTxnAmount_Ac().multiply(new BigDecimal(-1)));
			}
			logger.debug(internalAcEntryOne.getAccount() + " ONE " + internalAcEntryOne.getTxnEntry() + " "
					+ internalAcEntryTwo.getTxnAmount_Ac());
			logger.debug(internalAcEntryTwo.getAccount() + "  TWO " + internalAcEntryTwo.getTxnEntry() + " "
					+ internalAcEntryTwo.getTxnAmount_Ac());
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
		logger.debug("Leaving");
		return entryList;
	}

	/**
	 * Method to Processing accounting entries for FinancePostings
	 * 
	 * @param JVPostingEntry
	 *            (List)
	 * @param JVPostingEntry
	 */
	private List<ReturnDataSet> procJVPostingEntryList(List<JVPostingEntry> jvPostingEntryList, JVPosting jVPosting)
			throws InterfaceException {
		logger.debug("Entering");

		List<ReturnDataSet> list = new ArrayList<ReturnDataSet>();
		long linkedTranId = Long.MIN_VALUE;
		ReturnDataSet returnDataSet = null;
		for (JVPostingEntry jvPostingEntry : jvPostingEntryList) {
			returnDataSet = new ReturnDataSet();
			//Set Object Data of ReturnDataSet(s)
			returnDataSet.setFinReference(jVPosting.getBatch());
			returnDataSet.setAccount(jvPostingEntry.getAccount());
			returnDataSet.setAcCcy(jvPostingEntry.getAccCCy());
			returnDataSet.setAccountType(jvPostingEntry.getAcType());
			returnDataSet.setPostAmount(jvPostingEntry.getTxnAmount());
			returnDataSet.setTranOrderId(String.valueOf(jvPostingEntry.getAcEntryRef()));
			returnDataSet.setPostAmountLcCcy(CalculationUtil.getConvertedAmount(returnDataSet.getAcCcy(),
					SysParamUtil.getAppCurrency(), returnDataSet.getPostAmount()));

			if (!jvPostingEntryList.isEmpty()) {
				// Method for validating Postings with interface program and
				// return results
				if (jvPostingEntryList.get(0).getLinkedTranId() == Long.MIN_VALUE && linkedTranId == Long.MIN_VALUE) {
					linkedTranId = getPostingsDAO().getLinkedTransId();
				}
			}

			returnDataSet.setLinkedTranId(linkedTranId);
			returnDataSet.setFinEvent("JVPOST");
			returnDataSet.setTranDesc(jvPostingEntry.getAccountName());
			returnDataSet.setPostDate(jvPostingEntry.getPostingDate());
			returnDataSet.setValueDate(jvPostingEntry.getValueDate());
			returnDataSet.setAppDate(DateUtility.getAppDate());
			returnDataSet.setAppValueDate(DateUtility.getAppDate());
			returnDataSet.setTranCode(jvPostingEntry.getTxnCode());
			returnDataSet.setRevTranCode(jvPostingEntry.getRevTxnCode());
			returnDataSet.setDrOrCr(jvPostingEntry.getTxnEntry());
			returnDataSet.setShadowPosting(false);
			returnDataSet.setFlagCreateIfNF(true);
			returnDataSet.setFlagCreateNew(false);
			returnDataSet.setPostBranch(jVPosting.getBranch());

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

		//FIXME: PV: 05MAY17 needs to fill return dataset
		logger.debug("Leaving");
		return list;
	}


	/**
	 * Method to Prepare the accounting entries and save the postings to the Postings and accounts table
	 * @param aeEvent
	 * @param dataMap
	 * @return
	 */
	public AEEvent postAccounting(AEEvent aeEvent) {
		logger.debug("Entering");

		if (aeEvent.getLinkedTranId() <= 0) {
			aeEvent.setLinkedTranId(getPostingsDAO().getLinkedTransId());
		}

		getEngineExecution().getAccEngineExecResults(aeEvent);

		List<ReturnDataSet> returnDatasetList = aeEvent.getReturnDataSet();
		if (!aeEvent.isPostingSucess()) {
			return aeEvent;
		}

		if (returnDatasetList == null || returnDatasetList.isEmpty()) {
			return aeEvent;
		}

		getPostingsDAO().saveBatch(returnDatasetList);

		getAccountProcessUtil().procAccountUpdate(returnDatasetList);

		logger.debug("Leaving");
		return aeEvent;
	}
	
	/**
	 * Method to Prepare the accounting entries and save the postings to the Postings and accounts table
	 * @param aeEvent
	 * @param dataMap
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws InterfaceException
	 */
	public AEEvent getAccounting(AEEvent aeEvent, HashMap<String, Object> dataMap) throws IllegalAccessException, InvocationTargetException, InterfaceException {
		logger.debug("Entering");
		
		if (aeEvent.getLinkedTranId() <= 0) {
			aeEvent.setLinkedTranId(getPostingsDAO().getLinkedTransId());
		}
		
		getEngineExecution().getAccEngineExecResults(aeEvent);
		
		logger.debug("Leaving");
		return aeEvent;
	}
 
 
	public AEEvent postAccountingEOD(AEEvent aeEvent) throws IllegalAccessException, InvocationTargetException, InterfaceException {
		logger.debug("Entering");

		if (aeEvent.getLinkedTranId() <= 0) {
			aeEvent.setLinkedTranId(getPostingsDAO().getLinkedTransId());
		}
		
		getEngineExecution().getAccEngineExecResults(aeEvent);

		if (!aeEvent.isPostingSucess()) {
			return aeEvent;
		}

		logger.debug("Leaving");
		return aeEvent;

	}
	
	public void saveAccountingEOD(List<ReturnDataSet> returnDatasetList) {
		logger.debug("Entering");

		if (returnDatasetList != null && !returnDatasetList.isEmpty()) {
			getPostingsDAO().saveBatch(returnDatasetList);
		}

		logger.debug("Leaving");
	}

	
	/**
	 * @param finReference
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws InterfaceException
	 */
	public List<ReturnDataSet> postReveralsByFinreference(String finReference)  {
		logger.debug("Entering");
		
		
		List<ReturnDataSet> returnDataSets =  getReveralsByFinreference(finReference);

		getPostingsDAO().updateStatusByFinRef(finReference, AccountConstants.POSTINGS_REVERSE);

		getPostingsDAO().saveBatch(returnDataSets);

		getAccountProcessUtil().procAccountUpdate(returnDataSets);

		logger.debug("Leaving");
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
		logger.debug("Entering");

		List<ReturnDataSet> returnDataSets =  getReversalsByLinkedTranID(linkedTranId);
		
		getPostingsDAO().updateStatusByLinkedTranId(linkedTranId, AccountConstants.POSTINGS_REVERSE);

		getPostingsDAO().saveBatch(returnDataSets);

		getAccountProcessUtil().procAccountUpdate(returnDataSets);

		logger.debug("Leaving");
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
		logger.debug("Entering");
		
		long newLinkedTranID = getPostingsDAO().getLinkedTransId();

		List<ReturnDataSet> returnDataSets =  getPostingsDAO().getPostingsByLinkTransId(linkedTranId);

		getEngineExecution().getReversePostings(returnDataSets, newLinkedTranID);

		logger.debug("Leaving");
		return returnDataSets;
	}



	/**
	 * @param finReference
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws InterfaceException
	 */
	public List<ReturnDataSet> getReveralsByFinreference(String finReference) {
		logger.debug("Entering");
		
		long newLinkedTranID = getPostingsDAO().getLinkedTransId();
		List<ReturnDataSet> returnDataSets =  getPostingsDAO().getPostingsByFinRef(finReference);

		getEngineExecution().getReversePostings(returnDataSets, newLinkedTranID);

		logger.debug("Leaving");
		return returnDataSets;
	}



	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public AccountEngineExecution getEngineExecution() {
		return engineExecution;
	}

	public void setEngineExecution(AccountEngineExecution engineExecution) {
		this.engineExecution = engineExecution;
	}

	public PostingsDAO getPostingsDAO() {
		return postingsDAO;
	}

	public void setPostingsDAO(PostingsDAO postingsDAO) {
		this.postingsDAO = postingsDAO;
	}

	public void setAccountProcessUtil(AccountProcessUtil accountProcessUtil) {
		this.accountProcessUtil = accountProcessUtil;
	}

	public AccountProcessUtil getAccountProcessUtil() {
		return accountProcessUtil;
	}

	public CommitmentDAO getCommitmentDAO() {
		return commitmentDAO;
	}

	public void setCommitmentDAO(CommitmentDAO commitmentDAO) {
		this.commitmentDAO = commitmentDAO;
	}

	public CommitmentMovementDAO getCommitmentMovementDAO() {
		return commitmentMovementDAO;
	}

	public void setCommitmentMovementDAO(CommitmentMovementDAO commitmentMovementDAO) {
		this.commitmentMovementDAO = commitmentMovementDAO;
	}

	public FinContributorDetailDAO getFinContributorDetailDAO() {
		return finContributorDetailDAO;
	}

	public void setFinContributorDetailDAO(FinContributorDetailDAO finContributorDetailDAO) {
		this.finContributorDetailDAO = finContributorDetailDAO;
	}

	/*public void setFinanceCancellationProcess(FinanceCancellationProcess financeCancellationProcess) {
		this.financeCancellationProcess = financeCancellationProcess;
	}

	public FinanceCancellationProcess getFinanceCancellationProcess() {
		return financeCancellationProcess;
	}*/

	public void setFinanceTypeDAO(FinanceTypeDAO financeTypeDAO) {
		this.financeTypeDAO = financeTypeDAO;
	}

	public FinanceTypeDAO getFinanceTypeDAO() {
		return financeTypeDAO;
	}

	public FinTypeAccountingDAO getFinTypeAccountingDAO() {
		return finTypeAccountingDAO;
	}

	public void setFinTypeAccountingDAO(FinTypeAccountingDAO finTypeAccountingDAO) {
		this.finTypeAccountingDAO = finTypeAccountingDAO;
	}

}
