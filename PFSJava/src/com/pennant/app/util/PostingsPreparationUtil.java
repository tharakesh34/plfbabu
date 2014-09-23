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
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.Interface.service.PostingsInterfaceService;
import com.pennant.backend.dao.commitment.CommitmentDAO;
import com.pennant.backend.dao.commitment.CommitmentMovementDAO;
import com.pennant.backend.dao.finance.FinContributorDetailDAO;
import com.pennant.backend.dao.finance.FinancePremiumDetailDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.dao.rulefactory.PostingsDAO;
import com.pennant.backend.model.commitment.Commitment;
import com.pennant.backend.model.commitment.CommitmentMovement;
import com.pennant.backend.model.finance.FinContributorDetail;
import com.pennant.backend.model.finance.FinancePremiumDetail;
import com.pennant.backend.model.others.JVPosting;
import com.pennant.backend.model.others.JVPostingEntry;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEAmountCodesRIA;
import com.pennant.backend.model.rulefactory.AECommitment;
import com.pennant.backend.model.rulefactory.DataSet;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.util.PennantConstants;
import com.pennant.coreinterface.exception.AccountNotFoundException;
import com.pennant.coreinterface.model.FinanceCancellation;
import com.pennant.coreinterface.service.FinanceCancellationProcess;
import com.pennant.eod.util.EODProperties;

public class PostingsPreparationUtil implements Serializable {

	private static final long serialVersionUID = 1715547921928620037L;
	private Logger logger = Logger.getLogger(PostingsPreparationUtil.class);

	private static AccountEngineExecution engineExecution;
	private static AccountEngineExecutionRIA engineExecutionRIA;
	private static FinContributorDetailDAO finContributorDetailDAO;
	private static PostingsDAO postingsDAO;
	private static AccountProcessUtil accountProcessUtil;
	private static PostingsInterfaceService postingsInterfaceService;
	private static CommitmentDAO commitmentDAO;
	private static CommitmentMovementDAO commitmentMovementDAO;
	private static FinanceCancellationProcess financeCancellationProcess;
	private static FinanceTypeDAO financeTypeDAO;
	private static FinancePremiumDetailDAO premiumDetailDAO;
	
	private List<Object> returnList = null;

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
	 * @throws AccountNotFoundException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public List<Object> processPostingDetails(DataSet dataSet, AEAmountCodes amountCodes,
	        boolean isEODProcess, boolean isRIAFinance, String isCreateNewAccount,
	        Date dateAppDate, boolean allowCmtPostings, long linkedTranId)
	        throws AccountNotFoundException, IllegalAccessException, InvocationTargetException {
		
		return new PostingsPreparationUtil(dataSet, amountCodes, isEODProcess, isRIAFinance, isCreateNewAccount, 
				dateAppDate, allowCmtPostings, linkedTranId, null, false).getReturnList();
	}
	
	/**
	 * Method for Processing Postings Details with including Fees
	 * @param dataSet
	 * @param amountCodes
	 * @param isEODProcess
	 * @param isRIAFinance
	 * @param isCreateNewAccount
	 * @param dateAppDate
	 * @param allowCmtPostings
	 * @param linkedTranId
	 * @param feeChargeMap
	 * @return
	 * @throws AccountNotFoundException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public List<Object> processPostingDetailsWithFee(DataSet dataSet, AEAmountCodes amountCodes,
	        boolean isEODProcess, boolean isRIAFinance, String isCreateNewAccount,
	        Date dateAppDate, boolean allowCmtPostings, long linkedTranId,  Map<String, FeeRule> feeChargeMap)
	        throws AccountNotFoundException, IllegalAccessException, InvocationTargetException {
		
		return new PostingsPreparationUtil(dataSet, amountCodes, isEODProcess, isRIAFinance, isCreateNewAccount, 
				dateAppDate, allowCmtPostings, linkedTranId, feeChargeMap, false).getReturnList();
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
	 * @throws AccountNotFoundException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 *             List<Object>
	 */
	public List<Object> processCmtPostingDetails(Commitment commitment, String isCreateNow,
	        Date dateAppDate, String acSetEvent) throws AccountNotFoundException,
	        IllegalAccessException, InvocationTargetException {
		
		return new PostingsPreparationUtil(commitment, isCreateNow, dateAppDate, acSetEvent).getReturnList();
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
	 * @throws AccountNotFoundException
	 */
	public List<Object> postingAccruals(List<ReturnDataSet> list, String postBranch, Date valueDate,
			String createNow, boolean isEODProcess, String isDummy, long linkedTranId) throws Exception {
		
		return new PostingsPreparationUtil(list, postBranch, valueDate,createNow, isEODProcess, isDummy, linkedTranId).getReturnList();
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
	 * @throws AccountNotFoundException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public List<Object> processDepreciatePostings(DataSet dataSet, AEAmountCodes amountCodes,
			boolean isRIAFinance, Date dateAppDate, long linkedTranId)
	        throws AccountNotFoundException, IllegalAccessException, InvocationTargetException {
		
		return new PostingsPreparationUtil(dataSet, amountCodes, true, isRIAFinance, "Y", 
				dateAppDate, false, linkedTranId, null, true).getReturnList();
	}
	
	/**
	 * Method To Process Finance Disbursement Cancellation posting
	 * IN PostingsPreparationUtil.java
	 * @param finReference
	 * @return boolean 
	 */
	public List<Object> processFinCanclPostings(String finReference, String linkedTranId) {
		return new PostingsPreparationUtil(finReference, linkedTranId).getReturnList();
	}
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++++ Constructors ++++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	private PostingsPreparationUtil(DataSet dataSet, AEAmountCodes amountCodes,
	        boolean isEODProcess, boolean isRIAFinance, String isCreateNewAccount,
	        Date dateAppDate, boolean allowCmtPostings, long linkedTranId, Map<String, FeeRule> feeChargeMap, boolean isDepreciation) 
	        		throws AccountNotFoundException, IllegalAccessException, InvocationTargetException {
		setReturnList(processPostings(dataSet, amountCodes, isEODProcess, isRIAFinance, isCreateNewAccount, 
				dateAppDate, allowCmtPostings, linkedTranId, feeChargeMap, isDepreciation));
	}
	
	private PostingsPreparationUtil(Commitment commitment, String isCreateNow,Date dateAppDate, String acSetEvent) 
					throws AccountNotFoundException, IllegalAccessException, InvocationTargetException {
		setReturnList(procCmtPostingDetails(commitment, isCreateNow, dateAppDate, acSetEvent));
	}
	
	private PostingsPreparationUtil(List<ReturnDataSet> list, String postBranch, Date valueDate,
			String createNow, boolean isEODProcess, String isDummy, long linkedTranId) 
			throws Exception {
		setReturnList(procAccrualPostings(list, postBranch, valueDate,createNow, isEODProcess, isDummy, linkedTranId));
	}
	
	private PostingsPreparationUtil(String finReference, String linkedTranId) {
		setReturnList(procFinCanclPostings(finReference, linkedTranId));
	}
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ Process Methods +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	private List<Object> processPostings(DataSet dataSet, AEAmountCodes amountCodes,
	        boolean isEODProcess, boolean isRIAFinance, String isCreateNewAccount,
	        Date dateAppDate, boolean allowCmtPostings, long linkedTranId, Map<String, FeeRule> feeChargeMap, boolean isDepreciation) 
	        		throws AccountNotFoundException, IllegalAccessException, InvocationTargetException{
		logger.debug("Entering");

		List<ReturnDataSet> list = new ArrayList<ReturnDataSet>();
		FinancePremiumDetail premiumDetail = null;
		String finAcType = null;

		// Accounting Set Execution to get Posting Details List
		if (isRIAFinance) {

			List<FinContributorDetail> contributorDetailList = getFinContributorDetailDAO()
			        .getFinContributorDetailByFinRef(dataSet.getFinReference(), "_AView");

			List<AEAmountCodesRIA> riaDetailList = getEngineExecutionRIA().prepareRIADetails(
			        contributorDetailList, dataSet.getFinReference());
			list = getEngineExecutionRIA().getAccEngineExecResults(dataSet, amountCodes, "Y",
			        riaDetailList);

		} else {
			
			FinanceType financeType = null;
			if(isEODProcess){
				financeType = EODProperties.getFinanceType(StringUtils.trim(dataSet.getFinType()));
			}else{
				financeType = getFinanceTypeDAO().getFinanceTypeByFinType(dataSet.getFinType());
			}
			
			finAcType = financeType.getFinAcType();
			
			if(financeType.getFinCategory().equals(PennantConstants.FINANCE_PRODUCT_SUKUK)){
				premiumDetail = getPremiumDetailDAO().getFinPremiumDetailsById(dataSet.getFinReference(), "");
			}
			
			list = getEngineExecution().getAccEngineExecResults(dataSet, amountCodes, "Y", feeChargeMap,false, financeType, premiumDetail);
		}
		
		// Finance Commitment Reference Posting Details
		Commitment commitment = null;
		boolean cmtEventExecuted = false;
		if(allowCmtPostings && !StringUtils.trimToEmpty(dataSet.getCmtReference()).equals("") 
				&& amountCodes.getRpPri().compareTo(BigDecimal.ZERO) > 0){
			commitment = getCommitmentDAO().getCommitmentById(dataSet.getCmtReference(), "");

			if(commitment != null && commitment.isRevolving()){
				
				//Remove Commitment Details & Movement Details from Workflow which are in maintenance
				if(isEODProcess){
					Commitment tempcommitment = getCommitmentDAO().getCommitmentByRef(dataSet.getCmtReference(), "_Temp");
					if(tempcommitment != null){
						getCommitmentMovementDAO().deleteByRef(dataSet.getCmtReference(), "_Temp");
						getCommitmentDAO().deleteByRef(dataSet.getCmtReference(), "_Temp");
					}
				}
				
				AECommitment aeCommitment = new AECommitment();
				aeCommitment.setCMTAMT(BigDecimal.ZERO);
				aeCommitment.setCHGAMT(BigDecimal.ZERO);
				aeCommitment.setDISBURSE(BigDecimal.ZERO);
				aeCommitment.setRPPRI(CalculationUtil.getConvertedAmount(dataSet.getFinCcy(), commitment.getCmtCcy(), amountCodes.getRpPri()));

				List<ReturnDataSet> cmtList = getEngineExecution().getCommitmentExecResults(aeCommitment, commitment, "CMTRPY", "Y", null);
				list.addAll(cmtList);
				
				if(cmtList != null && cmtList.size() > 0){
					 cmtEventExecuted = true;
				}
			}
		}

		List<Object> returnList = postingsExecProcess(list, dataSet.getFinBranch(), dateAppDate,
		        isCreateNewAccount, isEODProcess, false, linkedTranId, amountCodes.getRpTot(),premiumDetail, finAcType, isDepreciation);
		
		if(cmtEventExecuted && (Boolean) returnList.get(0) && amountCodes.getRpPri().compareTo(BigDecimal.ZERO) > 0){
			getCommitmentDAO().updateCommitmentAmounts(commitment.getCmtReference(), amountCodes.getRpPri().negate(),commitment.getCmtExpDate());
			CommitmentMovement cmtMovement = prepareCommitMovement(commitment, dataSet, amountCodes.getRpPri(), (Long)returnList.get(1));
			if(cmtMovement != null){
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
	 * @throws AccountNotFoundException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 *             List<Object>
	 */
	private List<Object> procCmtPostingDetails(Commitment commitment, String isCreateNow,
	        Date dateAppDate, String acSetEvent) throws AccountNotFoundException,
	        IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		List<ReturnDataSet> list = new ArrayList<ReturnDataSet>();

		AECommitment aeCommitment = new AECommitment();
		aeCommitment.setCMTAMT(commitment.getCmtAmount());
		aeCommitment.setCHGAMT(commitment.getCmtCharges());
		aeCommitment.setDISBURSE(BigDecimal.ZERO);
		aeCommitment.setRPPRI(BigDecimal.ZERO);

		// Accounting Set Execution to get Posting Details List
		list = getEngineExecution().getCommitmentExecResults(aeCommitment, commitment, acSetEvent,
		        isCreateNow, null);

		List<Object> returnList = new ArrayList<Object>();
		if(list != null && list.size() > 0){
			
			boolean iscmtPostings = false;
			if(acSetEvent.equals(PennantConstants.NEWCMT) && commitment.isOpenAccount()){
				iscmtPostings = true;
			}
			returnList = postingsExecProcess(list, commitment.getCmtBranch(), dateAppDate,
					isCreateNow, false, iscmtPostings, Long.MIN_VALUE, null, null, null, false);
		}else{
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
	 * @param commitment
	 * @param dataSet
	 * @param postAmount
	 * @param linkedtranId
	 * @return
	 */
	private CommitmentMovement prepareCommitMovement(Commitment commitment, DataSet dataSet, 
			BigDecimal postAmount, long linkedtranId){

		CommitmentMovement movement = new CommitmentMovement();
		
		Date curBussDate =(Date) SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_CUR);

		movement.setCmtReference(commitment.getCmtReference());
		movement.setFinReference(dataSet.getFinReference());
		movement.setFinBranch(dataSet.getFinBranch());
		movement.setFinType(dataSet.getFinType());
		movement.setMovementDate(curBussDate);
		movement.setMovementOrder(getCommitmentMovementDAO().getMaxMovementOrderByRef(commitment.getCmtReference())+1);
		movement.setMovementType("RA");
		movement.setMovementAmount(postAmount);
		movement.setCmtAmount(commitment.getCmtAmount());
		movement.setCmtUtilizedAmount(commitment.getCmtUtilizedAmount().subtract(postAmount));
		if(commitment.getCmtExpDate().compareTo(curBussDate) < 0){
			movement.setCmtAvailable(BigDecimal.ZERO);
		}else{
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
	 * @throws AccountNotFoundException
	 */
	private List<Object> postingsExecProcess(List<ReturnDataSet> list, String branch,
	        Date dateAppDate, String createNow, boolean isEODProcess, boolean isCmtPostings, long linkedTranId, 
	        BigDecimal totalRpyAmt, FinancePremiumDetail premiumDetail, String finAcType, boolean isDepreciation) throws AccountNotFoundException {
		logger.debug("Entering");

		//Commitment Posting Details
		String commitmentAcc = "";
		String finAccount = null;
		String acType = SystemParameterDetails.getSystemParameterValue("COMMITMENT_AC_TYPE").toString();

		//Method for Checking for Reverse Calculations Based upon Negative Amounts
		for (ReturnDataSet returnDataSet : list) {
			
			returnDataSet.setLinkedTranId(linkedTranId);
			
			if(isCmtPostings && acType.equals(returnDataSet.getAccountType())){
				commitmentAcc = returnDataSet.getAccount();
			}

			if (returnDataSet.getPostAmount().compareTo(BigDecimal.ZERO) < 0) {

				String tranCode = returnDataSet.getTranCode();
				String revTranCode = returnDataSet.getRevTranCode();
				String debitOrCredit = returnDataSet.getDrOrCr();

				returnDataSet.setTranCode(revTranCode);
				returnDataSet.setRevTranCode(tranCode);

				returnDataSet.setPostAmount(returnDataSet.getPostAmount().negate());

				if (debitOrCredit.equals("C")) {
					returnDataSet.setDrOrCr("D");
				} else {
					returnDataSet.setDrOrCr("C");
				}
			}
		}

		if (list != null && list.size() > 0) {
			// Method for validating Postings with interface program and
			// return results
			if (list.get(0).getLinkedTranId() == Long.MIN_VALUE) {
				linkedTranId = getPostingsDAO().getLinkedTransId(list.get(0));
			} else {
				linkedTranId = list.get(0).getLinkedTranId();
			}
			
			if(!isDepreciation){
				list = getPostingsInterfaceService().doFillPostingDetails(list, branch, linkedTranId, createNow);
			}else{
				list = getPostingsInterfaceService().doAccrualPosting(list, dateAppDate, branch, linkedTranId, createNow, "N");
			}
		}

		boolean isPostingSuccess = true;
		String errorMessage = null;
		boolean isFetchFinAc = false;
		
		if (list != null && list.size() > 0) {
			for (int k = 0; k < list.size(); k++) {
				ReturnDataSet set = list.get(k);
				set.setLinkedTranId(linkedTranId);
				set.setPostDate(dateAppDate);
				String errorId = StringUtils.trimToEmpty(set.getErrorId());
				if (!("0000".equals(errorId) || "".equals(errorId))) {
					set.setPostStatus("F");//TODO throw an exception to stop job/ User Action
					isPostingSuccess = false;
					if(errorMessage == null){
						errorMessage = errorId +" - "+ set.getErrorMsg();
					}
				} else {
					set.setPostStatus("S");
					
					//Commitment Account Updation Purpose
					if(isCmtPostings && acType.equals(set.getAccountType())){
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
			
			//Update Accrued Amount
			if(isPostingSuccess && premiumDetail != null){
				if(premiumDetail.getAccruedProfit().compareTo(BigDecimal.ZERO) > 0){
					if(premiumDetail.getAccruedProfit().compareTo(totalRpyAmt) > 0){
						premiumDetail.setAccruedProfit(premiumDetail.getAccruedProfit().subtract(totalRpyAmt));
					}else{
						premiumDetail.setAccruedProfit(BigDecimal.ZERO);
					}
					
					getPremiumDetailDAO().updateAccruedAmount(premiumDetail);
				}
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
	 * @throws AccountNotFoundException
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

				if (debitOrCredit.equals("C")) {
					returnDataSet.setDrOrCr("D");
				} else {
					returnDataSet.setDrOrCr("C");
				}
			}
		}
		
		boolean isPostingSuccess = true;		
		
		if(list != null && !list.isEmpty()) {
			// Method for validating Postings with interface program and
			// return results
			if (list.get(0).getLinkedTranId() == Long.MIN_VALUE) {
				linkedTranId = getPostingsDAO().getLinkedTransId(list.get(0));
			} else {
				linkedTranId = list.get(0).getLinkedTranId();
			}
			
			list = getPostingsInterfaceService().doAccrualPosting(list, valueDate, postBranch, linkedTranId, createNow, isDummy);

			for (int k = 0; k < list.size(); k++) {
				ReturnDataSet set = list.get(k);
				set.setLinkedTranId(linkedTranId);
				set.setPostDate(valueDate);
				String errorId = StringUtils.trimToEmpty(set.getErrorId());
				if (!("0000".equals(errorId) || "".equals(errorId))) {
					set.setPostStatus("F");//TODO throw an exception to stop job/ User Action
					isPostingSuccess = false;
				} else {
					set.setPostStatus("S");
				}
			}

			// save Postings
			if (isEODProcess) {

				if (isPostingSuccess) {
					if("N".equals(isDummy)) {
						getPostingsDAO().saveHeader(list.get(0), "S", "");
					} 
				} else {
					if("N".equals(isDummy)) {
						getPostingsDAO().saveHeader(list.get(0), "F", "");
					} 
				}
				if("Y".equals(isDummy)) {
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
	 * Method To Process Finance Disbursement Cancellation posting
	 * IN PostingsPreparationUtil.java
	 * @param finReference
	 * @return boolean 
	 */
	private List<Object> procFinCanclPostings(String finReference, String linkedTranId) {
		logger.debug("Entering");
		boolean postingSuccess=false;
		String errorMsg = null;
		
		List<Object> returnList = new ArrayList<Object>();
		try {
			// Call To Finance Disbursement Cancellation posting  interface 
				List<FinanceCancellation> list = getFinanceCancellationProcess().fetchCancelledFinancePostings(finReference, linkedTranId);
				if (list != null && list.size() > 0) {
					FinanceCancellation cancellation = list.get(0);
					//Check For errors
					if (StringUtils.trimToEmpty(cancellation.getDsRspErrD()).equals("")) {
						updateCancelledPosting(list);
						postingSuccess=true;
					}
				}
		} catch (AccountNotFoundException e) {
			logger.debug(e);
			errorMsg = e.getErrorMsg();
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
	 * To Update Posting with the Response from the Finance Disbursement Cancellation Interface. 
	 *  <br> IN PostingsPreparationUtil.java
	 * @param financeCancellations  
	 */
	private void updateCancelledPosting(List<FinanceCancellation> financeCancellations) {
		// Create object for postings(Posting table object)
		List<ReturnDataSet> returnDataSets = new ArrayList<ReturnDataSet>(financeCancellations.size());
		ReturnDataSet dataSet = null;
		for (FinanceCancellation finCanl : financeCancellations) {
			dataSet = new ReturnDataSet();
			dataSet.setLinkedTranId(Long.valueOf(finCanl.getDsRspLnkTID()));
			dataSet.setPostref(finCanl.getDsRspPostRef());
			dataSet.setFinReference(finCanl.getDsRspFinRef());
			dataSet.setFinEvent(finCanl.getDsRspFinEvent());
			dataSet.setPostDate(DateUtility.convertDateFromAS400(new BigDecimal(finCanl.getDsRspPOD())));
			dataSet.setAccount(finCanl.getDsRspAB() + finCanl.getDsRspAN()+ finCanl.getDsRspAS());
			dataSet.setPostStatus(finCanl.getDsRspStatus());
			dataSet.setErrorId(finCanl.getDsRspErr());
			dataSet.setErrorMsg(finCanl.getDsRspErrD());
			returnDataSets.add(dataSet);
		}

		if (returnDataSets.size()>0) {
			getPostingsDAO().updateBatch(returnDataSets, "");
		}
	}
	
	
	public static List<JVPostingEntry> prepareAccountingEntryList(List<JVPostingEntry> externalAcEntryList, String baseCcy, String baseCcyNumber, int baseCcyEditField) {
		List<JVPostingEntry> entryList = new ArrayList<JVPostingEntry>();
		for (JVPostingEntry jvPostingEntry : externalAcEntryList) {
			entryList.addAll(prepareAccountingEntry(jvPostingEntry, baseCcy, baseCcyNumber, baseCcyEditField, true));
        }
		return entryList;
	}
	
	/**
	 * Method to prepare accounting entries for FinancePostings
	 * @param JVPostingEntry (List)
	 * @param Base Currency (String)
	 * @param Base Currency Number (String)
	 * @param Base Currency Edit Field (int)
	 */
	public static List<JVPostingEntry> prepareAccountingEntry(JVPostingEntry externalAcEntry, String baseCcy, String baseCcyNumber, int baseCcyEditField, boolean addExt) {
		List<JVPostingEntry> entryList = new ArrayList<JVPostingEntry>();
		
		//Accounting Entries		
		JVPostingEntry internalAcEntryOne = null;
		JVPostingEntry internalAcEntryTwo = null;
		
		if(addExt) {
			entryList.add(externalAcEntry);
		}
		String actTranType = externalAcEntry.getTxnEntry();

		String drCr = actTranType.equals(PennantConstants.DEBIT) ? PennantConstants.CREDIT : PennantConstants.DEBIT;
		String crDr = actTranType.equals(PennantConstants.DEBIT) ? PennantConstants.DEBIT : PennantConstants.CREDIT;
		
		if(!externalAcEntry.getAccCCy().equals(baseCcy)){
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
			internalAcEntryOne.setAccCCyEditField(externalAcEntry.getAccCCyEditField());
			internalAcEntryOne.setAcEntryRef(2);				
			internalAcEntryOne.setTxnCode(SystemParameterDetails.getSystemParameterValue("CCYCNV_"+drCr+"RTRANCODE").toString());
			internalAcEntryOne.setRevTxnCode(SystemParameterDetails.getSystemParameterValue("CCYCNV_"+crDr+"RTRANCODE").toString());
			internalAcEntryOne.setAccount(externalAcEntry.getAccount().substring(0, 4)+"881"+externalAcEntry.getAcCcyNumber()+baseCcyNumber);
			internalAcEntryOne.setAcType("");
			internalAcEntryOne.setTxnAmount_Batch(CalculationUtil.getConvertedAmount(internalAcEntryOne.getTxnCCy(), baseCcy, internalAcEntryOne.getTxnAmount()));
			
			internalAcEntryTwo.setNewRecord(externalAcEntry.isNewRecord());
			internalAcEntryTwo.setTxnAmount_Ac(CalculationUtil.getConvertedAmount(internalAcEntryOne.getTxnCCy(), baseCcy, internalAcEntryOne.getTxnAmount()));
			internalAcEntryTwo.setAccountName("");
			internalAcEntryTwo.setExternalAccount(false);				
			internalAcEntryTwo.setTxnEntry(crDr);
			internalAcEntryTwo.setAccCCy(baseCcy);
			internalAcEntryTwo.setAccCCyEditField(baseCcyEditField);
			internalAcEntryTwo.setAcEntryRef(3);
			internalAcEntryTwo.setTxnCode(SystemParameterDetails.getSystemParameterValue("CCYCNV_"+crDr+"RTRANCODE").toString());
			internalAcEntryTwo.setRevTxnCode(SystemParameterDetails.getSystemParameterValue("CCYCNV_"+drCr+"RTRANCODE").toString());
			internalAcEntryTwo.setAccount(externalAcEntry.getAccount().substring(0, 4)+"881"+baseCcyNumber+externalAcEntry.getAcCcyNumber());
			internalAcEntryTwo.setAcType("");
			internalAcEntryTwo.setTxnAmount_Batch(CalculationUtil.getConvertedAmount(internalAcEntryOne.getTxnCCy(), baseCcy, internalAcEntryOne.getTxnAmount()));
			
			if(addExt && internalAcEntryOne.getTxnEntry().equals(PennantConstants.DEBIT)){
				internalAcEntryOne.setTxnAmount_Ac(internalAcEntryOne.getTxnAmount_Ac().multiply(new BigDecimal(-1)));
			}
			if(addExt && internalAcEntryTwo.getTxnEntry().equals(PennantConstants.DEBIT)){
				internalAcEntryTwo.setTxnAmount_Ac(internalAcEntryTwo.getTxnAmount_Ac().multiply(new BigDecimal(-1)));
			}
			System.out.println(internalAcEntryOne.getAccount()+" ONE "+ internalAcEntryOne.getTxnEntry()+" "+internalAcEntryTwo.getTxnAmount_Ac());
			System.out.println(internalAcEntryTwo.getAccount()+"  TWO "+internalAcEntryTwo.getTxnEntry()+" "+internalAcEntryTwo.getTxnAmount_Ac());
		}
		if(addExt && externalAcEntry.getTxnEntry().equals(PennantConstants.DEBIT)){
			externalAcEntry.setTxnAmount_Ac(externalAcEntry.getTxnAmount_Ac().multiply(new BigDecimal(-1)));
		}		
		
		if(internalAcEntryOne != null && internalAcEntryTwo != null){
			entryList.add(internalAcEntryOne);
			entryList.add(internalAcEntryTwo);
		}
		return entryList;
	}
	
	
	public static List<ReturnDataSet> processEntryList(List<JVPostingEntry> jvPostingEntryList, JVPosting jVPosting) throws AccountNotFoundException {
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
			if(jvPostingEntry.getTxnAmount_Ac().compareTo(BigDecimal.ZERO)<0){
				returnDataSet.setPostAmount(jvPostingEntry.getTxnAmount_Ac().multiply(new BigDecimal(-1)));
			}else {
				returnDataSet.setPostAmount(jvPostingEntry.getTxnAmount_Ac());
			}
			returnDataSet.setTranOrderId(String.valueOf(jvPostingEntry.getAcEntryRef()));
			
			if (jvPostingEntryList != null && jvPostingEntryList.size() > 0) {
				// Method for validating Postings with interface program and
				// return results
				if (jvPostingEntryList.get(0).getLinkedTranId() == Long.MIN_VALUE && linkedTranId == Long.MIN_VALUE) {
					linkedTranId = getPostingsDAO().getLinkedTransId(null);
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
			returnDataSet.setFinBranch(jvPostingEntry.getAccount().substring(0,4));
			
			if (jvPostingEntry.isExternalAccount()) {
				returnDataSet.setInternalAc(PennantConstants.NO);
				returnDataSet.setCustCIF(jvPostingEntry.getAccount().substring(4,10));
			}else {
				returnDataSet.setInternalAc(PennantConstants.YES);
				returnDataSet.setCustCIF("");
				returnDataSet.setAccountType("SP101");
			}
			
			
			list.add(returnDataSet);
		}
		list = getPostingsInterfaceService().doFillPostingDetails(list, jVPosting.getBranch(), linkedTranId, PennantConstants.NO);
		return list;
	}
	
	
	
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public static AccountEngineExecution getEngineExecution() {
		return engineExecution;
	}
	public void setEngineExecution(AccountEngineExecution engineExecution) {
		PostingsPreparationUtil.engineExecution = engineExecution;
	}

	public static PostingsDAO getPostingsDAO() {
		return postingsDAO;
	}
	public void setPostingsDAO(PostingsDAO postingsDAO) {
		PostingsPreparationUtil.postingsDAO = postingsDAO;
	}

	public static PostingsInterfaceService getPostingsInterfaceService() {
		return postingsInterfaceService;
	}
	public void setPostingsInterfaceService(PostingsInterfaceService postingsInterfaceService) {
		PostingsPreparationUtil.postingsInterfaceService = postingsInterfaceService;
	}

	public void setAccountProcessUtil(AccountProcessUtil accountProcessUtil) {
		PostingsPreparationUtil.accountProcessUtil = accountProcessUtil;
	}
	public static AccountProcessUtil getAccountProcessUtil() {
		return accountProcessUtil;
	}
	
	public static CommitmentDAO getCommitmentDAO() {
    	return commitmentDAO;
    }
	public void setCommitmentDAO(CommitmentDAO commitmentDAO) {
		PostingsPreparationUtil.commitmentDAO = commitmentDAO;
    }

	public static CommitmentMovementDAO getCommitmentMovementDAO() {
    	return commitmentMovementDAO;
    }
	public void setCommitmentMovementDAO(CommitmentMovementDAO commitmentMovementDAO) {
		PostingsPreparationUtil.commitmentMovementDAO = commitmentMovementDAO;
    }

	public void setEngineExecutionRIA(AccountEngineExecutionRIA engineExecutionRIA) {
		PostingsPreparationUtil.engineExecutionRIA = engineExecutionRIA;
	}
	public static AccountEngineExecutionRIA getEngineExecutionRIA() {
		return engineExecutionRIA;
	}

	public static FinContributorDetailDAO getFinContributorDetailDAO() {
		return finContributorDetailDAO;
	}
	public void setFinContributorDetailDAO(FinContributorDetailDAO finContributorDetailDAO) {
		PostingsPreparationUtil.finContributorDetailDAO = finContributorDetailDAO;
	}

	public void setFinanceCancellationProcess(FinanceCancellationProcess financeCancellationProcess) {
		PostingsPreparationUtil.financeCancellationProcess = financeCancellationProcess;
	}
	public static FinanceCancellationProcess getFinanceCancellationProcess() {
		return financeCancellationProcess;
	}

	public void setFinanceTypeDAO(FinanceTypeDAO financeTypeDAO) {
		PostingsPreparationUtil.financeTypeDAO = financeTypeDAO;
    }
	public static FinanceTypeDAO getFinanceTypeDAO() {
	    return financeTypeDAO;
    }
	
	public static FinancePremiumDetailDAO getPremiumDetailDAO() {
	    return premiumDetailDAO;
    }
	public void setPremiumDetailDAO(FinancePremiumDetailDAO premiumDetailDAO) {
		PostingsPreparationUtil.premiumDetailDAO = premiumDetailDAO;
    }

	public List<Object> getReturnList() {
	    return returnList;
    }
	public void setReturnList(List<Object> returnList) {
	    this.returnList = returnList;
    }

}
