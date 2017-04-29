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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.login.AccountNotFoundException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.Interface.service.PostingsInterfaceService;
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
import com.pennant.backend.model.payorderissue.PayOrderIssueHeader;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.AECommitment;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.util.PennantConstants;
import com.pennant.coreinterface.model.FinanceCancellation;
import com.pennant.coreinterface.process.FinanceCancellationProcess;
import com.pennant.eod.util.EODProperties;
import com.pennant.exception.PFFInterfaceException;

public class PostingsPreparationUtil implements Serializable {
	private static final long			serialVersionUID	= 1715547921928620037L;
	private Logger						logger				= Logger.getLogger(PostingsPreparationUtil.class);

	private AccountEngineExecution		engineExecution;
	private FinContributorDetailDAO		finContributorDetailDAO;
	private PostingsDAO					postingsDAO;
	private AccountProcessUtil			accountProcessUtil;
	private PostingsInterfaceService	postingsInterfaceService;
	private CommitmentDAO				commitmentDAO;
	private CommitmentMovementDAO		commitmentMovementDAO;
	private FinanceCancellationProcess	financeCancellationProcess;
	private FinanceTypeDAO				financeTypeDAO;
	private FinTypeAccountingDAO		finTypeAccountingDAO;

	public PostingsPreparationUtil() {
		super();
	}

	/**
	 * Method for Process Posting Details
	 * 
	 * @param dataSet
	 * @param amountCodes
	 * @param isEODProcess
	 * @param isCreateNewAccount
	 * @param dateAppDate
	 * @param movement
	 * @param isProvPostings
	 * @return
	 * @throws PFFInterfaceException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public List<Object> processPostingDetails(HashMap<String, Object> executingMap, boolean isEODProcess,
			String isCreateNewAccount, Date dateAppDate, boolean allowCmtPostings, long linkedTranId)
			throws PFFInterfaceException, IllegalAccessException, InvocationTargetException {

		return processPostings(executingMap, isEODProcess, isCreateNewAccount, dateAppDate, allowCmtPostings,
				linkedTranId, null, false);
	}

	/**
	 * Method for Processing Postings Details with including Fees
	 * 
	 * @param dataSet
	 * @param amountCodes
	 * @param isEODProcess
	 * @param isCreateNewAccount
	 * @param dateAppDate
	 * @param allowCmtPostings
	 * @param linkedTranId
	 * @param feeChargeMap
	 * @return
	 * @throws PFFInterfaceException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public List<Object> processPostingDetailsWithFee(HashMap<String, Object> executingMap, boolean isEODProcess,
			String isCreateNewAccount, Date dateAppDate, boolean allowCmtPostings, long linkedTranId,
			Map<String, FeeRule> feeChargeMap) throws PFFInterfaceException, IllegalAccessException,
			InvocationTargetException {

		return processPostings(executingMap, isEODProcess, isCreateNewAccount, dateAppDate, allowCmtPostings,
				linkedTranId, feeChargeMap, false);
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
	 * @throws PFFInterfaceException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 *             List<Object>
	 */
	public List<Object> processCmtPostingDetails(Commitment commitment, String isCreateNow, Date dateAppDate,
			String acSetEvent) throws PFFInterfaceException, IllegalAccessException, InvocationTargetException {

		return procCmtPostingDetails(commitment, isCreateNow, dateAppDate, acSetEvent);
	}

	/**
	 * Method for Execution of Posting in Core Banking Side Depends on Creation Flag
	 * 
	 * @param list
	 * @param postBranch
	 * @param dateAppDate
	 * @param createNow
	 * @param isEODProcess
	 * @param object
	 * @return List<Object>
	 * @throws PFFInterfaceException
	 */
	public List<Object> postingAccruals(List<ReturnDataSet> list, String postBranch, Date valueDate, String createNow,
			boolean isEODProcess, String isDummy, long linkedTranId) throws Exception {

		return procAccrualPostings(list, postBranch, valueDate, createNow, isEODProcess, isDummy, linkedTranId);
	}

	/**
	 * Method for Process Depreciation Posting Details
	 * 
	 * @param dataSet
	 * @param amountCodes
	 * @param isEODProcess
	 * @param isCreateNewAccount
	 * @param dateAppDate
	 * @param movement
	 * @param isProvPostings
	 * @return
	 * @throws PFFInterfaceException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public List<Object> processDepreciatePostings(HashMap<String, Object> executingMap, Date dateAppDate,
			long linkedTranId) throws PFFInterfaceException, IllegalAccessException, InvocationTargetException {

		return processPostings(executingMap, true, "Y", dateAppDate, false, linkedTranId, null, true);
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
			throws PFFInterfaceException {
		return procJVPostingEntryList(jvPostingEntryList, jVPosting);
	}

	public List<Object> processPostings(List<ReturnDataSet> returnDataSetList) throws AccountNotFoundException,
			IllegalAccessException, InvocationTargetException, PFFInterfaceException {
		return processPostingDetails(returnDataSetList);
	}

	public List<ReturnDataSet> prepareAccountingDataSet(PayOrderIssueHeader inventory, String acSetEvent,
			String createNow) throws IllegalAccessException, InvocationTargetException, PFFInterfaceException {
		HashMap<String, Object> executingMap = new HashMap<String, Object>();

		executingMap.put("PostDate", DateUtility.getSysDate());
		executingMap.put("ValueDate", DateUtility.getSysDate());
		executingMap.put("fm_finCcy", SysParamUtil.getAppCurrency());
		executingMap.put("fm_finBranch", PennantConstants.IBD_Branch);
		executingMap.put("BrokerAccount", null);
		executingMap.put("PURAMOUNT", BigDecimal.ZERO);
		executingMap.put("QUANTITY", BigDecimal.ZERO);
		executingMap.put("UNITPRICE", BigDecimal.ZERO);
		executingMap.put("SETTLEAMT", BigDecimal.ZERO);
		executingMap.put("UNSOLDFEE", BigDecimal.ZERO);
		executingMap.put("finEvent", acSetEvent);

		return getEngineExecution().processAccountingByEvent(executingMap, createNow);
	}

	// ******************************************************//
	// ****************** Process Methods *******************//
	// ******************************************************//

	private List<Object> processPostingDetails(List<ReturnDataSet> returnDataSetList) throws AccountNotFoundException,
			PFFInterfaceException {
		List<Object> returnList = new ArrayList<Object>();
		// Preparation for Commitment Postings
		Date dateAppDate = DateUtility.getAppDate();
		boolean isPostingSuccess = true;
		String errorMsg = null;
		long linkedTranId = getPostingsDAO().getLinkedTransId();

		returnDataSetList = getPostingsInterfaceService().doFillPostingDetails(returnDataSetList,
				returnDataSetList.get(0).getFinBranch(), linkedTranId, PennantConstants.NO);

		for (int k = 0; k < returnDataSetList.size(); k++) {
			ReturnDataSet set = returnDataSetList.get(k);
			set.setLinkedTranId(linkedTranId);
			set.setPostDate(dateAppDate);
			String errorId = StringUtils.trimToEmpty(set.getErrorId());
			if (!("0000".equals(errorId) || "".equals(errorId))) {
				set.setPostStatus("F");//TODO throw an exception to stop job/ User Action
				isPostingSuccess = false;
				errorMsg = set.getErrorMsg();
			} else {
				set.setPostStatus("S");
			}
		}
		if (isPostingSuccess) {
			getPostingsDAO().saveBatch(returnDataSetList, "", false);
		}
		returnList.add(isPostingSuccess);
		returnList.add(errorMsg);
		returnList.add(linkedTranId);

		return returnList;
	}

	private List<Object> processPostings(HashMap<String, Object> executingMap, boolean isEODProcess,
			String isCreateNewAccount, Date dateAppDate, boolean allowCmtPostings, long linkedTranId,
			Map<String, FeeRule> feeChargeMap, boolean isDepreciation) throws PFFInterfaceException,
			IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		List<ReturnDataSet> list = new ArrayList<ReturnDataSet>();
		String finAcType = null;

		// Accounting Set Execution to get Posting Details List

		FinanceType financeType = null;
		if (isEODProcess) {
			financeType = EODProperties.getFinanceType(StringUtils.trim((String) executingMap.get("fm_finType")));
		} else {
			financeType = getFinanceTypeDAO().getFinanceTypeByFinType((String) executingMap.get("fm_finType"));
		}

		finAcType = financeType.getFinAcType();

		if(feeChargeMap != null){
			executingMap.putAll(feeChargeMap);
		}
		financeType.getDeclaredFieldValues(executingMap);

		list = getEngineExecution().getAccEngineExecResults("Y", executingMap, false);

		// Finance Commitment Reference Posting Details
		Commitment commitment = null;
		boolean cmtEventExecuted = false;
		if (allowCmtPostings && StringUtils.isNotBlank((String) executingMap.get("fm_finCommitmentRef"))
				&& ((BigDecimal) executingMap.get("ae_rpPri")).compareTo(BigDecimal.ZERO) > 0) {
			commitment = getCommitmentDAO().getCommitmentById((String) executingMap.get("fm_finCommitmentRef"), "");

			if (commitment != null && commitment.isRevolving()) {

				//Remove Commitment Details & Movement Details from Workflow which are in maintenance
				if (isEODProcess) {
					Commitment tempcommitment = getCommitmentDAO().getCommitmentByRef(
							(String) executingMap.get("fm_finCommitmentRef"), "_Temp");
					if (tempcommitment != null) {
						getCommitmentMovementDAO().deleteByRef((String) executingMap.get("fm_finCommitmentRef"),
								"_Temp");
						getCommitmentDAO().deleteByRef((String) executingMap.get("fm_finCommitmentRef"), "_Temp");
					}
				}

				AECommitment aeCommitment = new AECommitment();
				aeCommitment.setCMTAMT(BigDecimal.ZERO);
				aeCommitment.setCHGAMT(BigDecimal.ZERO);
				aeCommitment.setDISBURSE(BigDecimal.ZERO);
				aeCommitment.setRPPRI(CalculationUtil.getConvertedAmount((String) executingMap.get("fm_finCcy"),
						commitment.getCmtCcy(), ((BigDecimal) executingMap.get("ae_rpPri"))));

				List<ReturnDataSet> cmtList = getEngineExecution().getCommitmentExecResults(aeCommitment, commitment,
						AccountEventConstants.ACCEVENT_CMTRPY, "Y", executingMap);
				list.addAll(cmtList);

				if (cmtList != null && cmtList.size() > 0) {
					cmtEventExecuted = true;
				}
			}
		}

		List<Object> returnList = postingsExecProcess(list, (String) executingMap.get("fm_finBranch"), dateAppDate,
				isCreateNewAccount, isEODProcess, false, linkedTranId, ((BigDecimal) executingMap.get("ae_rpTot")),
				finAcType, isDepreciation);

		if (cmtEventExecuted && (Boolean) returnList.get(0)
				&& ((BigDecimal) executingMap.get("ae_rpPri")).compareTo(BigDecimal.ZERO) > 0) {
			getCommitmentDAO().updateCommitmentAmounts(commitment.getCmtReference(),
					((BigDecimal) executingMap.get("ae_rpPri")).negate(), commitment.getCmtExpDate());
			CommitmentMovement cmtMovement = prepareCommitMovement(commitment, (Long) returnList.get(1), executingMap);
			if (cmtMovement != null) {
				getCommitmentMovementDAO().save(cmtMovement, "");
			}
		}

		logger.debug("Leaving");
		return returnList;
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
	 * @throws PFFInterfaceException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 *             List<Object>
	 */
	private List<Object> procCmtPostingDetails(Commitment commitment, String isCreateNow, Date dateAppDate,
			String acSetEvent) throws PFFInterfaceException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		List<ReturnDataSet> list = new ArrayList<ReturnDataSet>();

		AECommitment aeCommitment = new AECommitment();
		aeCommitment.setCMTAMT(commitment.getCmtAmount());
		aeCommitment.setCHGAMT(commitment.getCmtCharges());
		aeCommitment.setDISBURSE(BigDecimal.ZERO);
		aeCommitment.setRPPRI(BigDecimal.ZERO);

		// Accounting Set Execution to get Posting Details List
		list = getEngineExecution().getCommitmentExecResults(aeCommitment, commitment, acSetEvent, isCreateNow, null);

		List<Object> returnList = new ArrayList<Object>();
		if (list != null && list.size() > 0) {

			boolean iscmtPostings = false;
			if (acSetEvent.equals(AccountEventConstants.ACCEVENT_NEWCMT) && commitment.isOpenAccount()) {
				iscmtPostings = true;
			}
			returnList = postingsExecProcess(list, commitment.getCmtBranch(), dateAppDate, isCreateNow, false,
					iscmtPostings, Long.MIN_VALUE, null, null, false);
		} else {
			returnList.add(false);
			returnList.add(0);
			returnList.add("");
			returnList.add("0000 - Empty Accounting Set Details");
		}
		logger.debug("Leaving");
		return returnList;

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
			HashMap<String, Object> executingMap) {
		logger.debug("Entering");
		CommitmentMovement movement = new CommitmentMovement();

		BigDecimal postAmount = ((BigDecimal) executingMap.get("ae_rpPri"));

		Date curBussDate = DateUtility.getAppDate();

		movement.setCmtReference(commitment.getCmtReference());
		movement.setFinReference((String) executingMap.get("fm_finReference"));
		movement.setFinBranch((String) executingMap.get("fm_finBranch"));
		movement.setFinType((String) executingMap.get("fm_finType"));
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

	/**
	 * Method for Execution of Posting in Core Banking Side Depends on Creation Flag
	 * 
	 * @param list
	 * @param branch
	 * @param dateAppDate
	 * @param createNow
	 * @param isEODProcess
	 * @param isProvPostings
	 * @param object
	 * @return List<Object>
	 * @throws PFFInterfaceException
	 */
	private List<Object> postingsExecProcess(List<ReturnDataSet> list, String branch, Date dateAppDate,
			String createNow, boolean isEODProcess, boolean isCmtPostings, long linkedTranId, BigDecimal totalRpyAmt,
			String finAcType, boolean isDepreciation) throws PFFInterfaceException {
		logger.debug("Entering");

		//Commitment Posting Details
		String commitmentAcc = "";
		String finAccount = null;
		String acType = SysParamUtil.getValueAsString("COMMITMENT_AC_TYPE");

		//Method for Checking for Reverse Calculations Based upon Negative Amounts
		for (ReturnDataSet returnDataSet : list) {

			returnDataSet.setLinkedTranId(linkedTranId);

			if (isCmtPostings && acType.equals(returnDataSet.getAccountType())) {
				commitmentAcc = returnDataSet.getAccount();
			}

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
			// Method for validating Postings with interface program and
			// return results
			if (list.get(0).getLinkedTranId() == Long.MIN_VALUE) {
				linkedTranId = getPostingsDAO().getLinkedTransId();
			} else {
				linkedTranId = list.get(0).getLinkedTranId();
			}

			if (!isDepreciation) {
				list = getPostingsInterfaceService().doFillPostingDetails(list, branch, linkedTranId, createNow);
			} else {
				list = getPostingsInterfaceService().doAccrualPosting(list, dateAppDate, branch, linkedTranId,
						createNow, "N");
			}
		}

		boolean isPostingSuccess = true;
		String errorMessage = null;
		boolean isFetchFinAc = false;

		if (!list.isEmpty()) {
			for (int k = 0; k < list.size(); k++) {
				ReturnDataSet set = list.get(k);
				set.setLinkedTranId(linkedTranId);
				set.setPostDate(dateAppDate);
				String errorId = StringUtils.trimToEmpty(set.getErrorId());
				if (!("0000".equals(errorId) || StringUtils.isEmpty(errorId))) {
					set.setPostStatus("F");
					isPostingSuccess = false;
					if (errorMessage == null) {
						errorMessage = errorId + " - " + set.getErrorMsg();
					}
				} else {
					set.setPostStatus("S");

					//Commitment Account Updation Purpose
					if (isCmtPostings && acType.equals(set.getAccountType())) {
						commitmentAcc = set.getAccount();
					}
				}

				if (!isFetchFinAc && StringUtils.trimToEmpty(finAcType).equals(set.getAccountType())) {
					isFetchFinAc = true;
					finAccount = set.getAccount();
				}
			}

			// save Postings
			if (isEODProcess) {

				if (isPostingSuccess) {
					getPostingsDAO().saveHeader(list.get(0), "S", "");
				} else {
					getPostingsDAO().saveHeader(list.get(0), "F", "");
				}
				getPostingsDAO().saveEODBatch(list, "", "N");

			} else if (isPostingSuccess) {
				getPostingsDAO().saveBatch(list, "", false);
			}

			if (isPostingSuccess) {
				//Account Details Updation
				getAccountProcessUtil().updateAccountInfo(list);
			}
		}

		List<Object> returnList = new ArrayList<Object>(5);
		returnList.add(isPostingSuccess);
		returnList.add(linkedTranId);
		returnList.add(commitmentAcc);
		returnList.add(errorMessage);
		returnList.add(finAccount);

		logger.debug("Leaving");
		return returnList;
	}

	/**
	 * Method for Execution of Posting in Core Banking Side Depends on Creation Flag
	 * 
	 * @param list
	 * @param postBranch
	 * @param dateAppDate
	 * @param createNow
	 * @param isEODProcess
	 * @param object
	 * @return List<Object>
	 * @throws PFFInterfaceException
	 */
	private List<Object> procAccrualPostings(List<ReturnDataSet> list, String postBranch, Date valueDate,
			String createNow, boolean isEODProcess, String isDummy, long linkedTranId) throws Exception {
		logger.debug("Entering");

		//Method for Checking for Reverse Calculations Based upon Negative Amounts
		for (ReturnDataSet returnDataSet : list) {

			returnDataSet.setLinkedTranId(linkedTranId);

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

		boolean isPostingSuccess = true;

		if (!list.isEmpty()) {
			// Method for validating Postings with interface program and
			// return results
			if (list.get(0).getLinkedTranId() == Long.MIN_VALUE) {
				linkedTranId = getPostingsDAO().getLinkedTransId();
			} else {
				linkedTranId = list.get(0).getLinkedTranId();
			}

			list = getPostingsInterfaceService().doAccrualPosting(list, valueDate, postBranch, linkedTranId, createNow,
					isDummy);

			for (int k = 0; k < list.size(); k++) {
				ReturnDataSet set = list.get(k);
				set.setLinkedTranId(linkedTranId);
				set.setPostDate(valueDate);
				String errorId = StringUtils.trimToEmpty(set.getErrorId());
				if (!("0000".equals(errorId) || StringUtils.isEmpty(errorId))) {
					set.setPostStatus("F");
					isPostingSuccess = false;
				} else {
					set.setPostStatus("S");
				}
			}

			// save Postings
			if (isEODProcess) {

				if (isPostingSuccess) {
					if ("N".equals(isDummy)) {
						getPostingsDAO().saveHeader(list.get(0), "S", "");
					}
				} else {
					if ("N".equals(isDummy)) {
						getPostingsDAO().saveHeader(list.get(0), "F", "");
					}
				}
				if ("Y".equals(isDummy)) {
					getPostingsDAO().saveEODBatch(list, "_Temp", isDummy);
				} else {
					getPostingsDAO().saveEODBatch(list, "", isDummy);
				}

			} else if (isPostingSuccess) {
				getPostingsDAO().saveBatch(list, "", false);
			}

			if ("N".equals(isDummy) && isPostingSuccess) {
				//Account Details Updation
				getAccountProcessUtil().updateAccountInfo(list);
			}
		}

		List<Object> returnList = new ArrayList<Object>(2);
		returnList.add(isPostingSuccess);
		returnList.add(linkedTranId);

		logger.debug("Leaving");
		return returnList;
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
		try {
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
		} catch (PFFInterfaceException e) {
			logger.debug(e);
			errorMsg = e.getErrorMessage();
		} catch (Exception e) {
			logger.debug(e);
			errorMsg = e.getMessage();
		}

		returnList.add(postingSuccess);
		returnList.add(errorMsg);

		logger.debug("Leaving");
		return returnList;
	}

	/**
	 * To Update Posting with the Response from the Finance Disbursement Cancellation Interface. <br>
	 * IN PostingsPreparationUtil.java
	 * 
	 * @param financeCancellations
	 */
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
	}

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
			throws PFFInterfaceException {
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
			if (jvPostingEntry.getTxnAmount_Ac().compareTo(BigDecimal.ZERO) < 0) {
				returnDataSet.setPostAmount(jvPostingEntry.getTxnAmount_Ac().multiply(new BigDecimal(-1)));
			} else {
				returnDataSet.setPostAmount(jvPostingEntry.getTxnAmount_Ac());
			}
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
			returnDataSet.setTranCode(jvPostingEntry.getTxnCode());
			returnDataSet.setRevTranCode(jvPostingEntry.getRevTxnCode());
			returnDataSet.setDrOrCr(jvPostingEntry.getTxnEntry());
			returnDataSet.setShadowPosting(false);
			returnDataSet.setFlagCreateIfNF(PennantConstants.YES);
			returnDataSet.setFlagCreateNew(PennantConstants.NO);
			returnDataSet.setFinBranch(jVPosting.getBranch());

			if (jvPostingEntry.isExternalAccount()) {
				returnDataSet.setInternalAc(PennantConstants.NO);
				returnDataSet.setCustCIF(jVPosting.getBranch());
			} else {
				returnDataSet.setInternalAc(PennantConstants.YES);
				returnDataSet.setCustCIF("");
				returnDataSet.setAccountType("SP101");
			}
			list.add(returnDataSet);
		}
		list = getPostingsInterfaceService().doFillPostingDetails(list, jVPosting.getBranch(), linkedTranId,
				PennantConstants.NO);
		logger.debug("Leaving");
		return list;
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

	public PostingsInterfaceService getPostingsInterfaceService() {
		return postingsInterfaceService;
	}

	public void setPostingsInterfaceService(PostingsInterfaceService postingsInterfaceService) {
		this.postingsInterfaceService = postingsInterfaceService;
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

	public void setFinanceCancellationProcess(FinanceCancellationProcess financeCancellationProcess) {
		this.financeCancellationProcess = financeCancellationProcess;
	}

	public FinanceCancellationProcess getFinanceCancellationProcess() {
		return financeCancellationProcess;
	}

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
