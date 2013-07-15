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
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.Interface.service.PostingsInterfaceService;
import com.pennant.backend.dao.finance.FinContributorDetailDAO;
import com.pennant.backend.dao.financemanagement.ProvisionDAO;
import com.pennant.backend.dao.financemanagement.ProvisionMovementDAO;
import com.pennant.backend.dao.rulefactory.PostingsDAO;
import com.pennant.backend.model.commitment.Commitment;
import com.pennant.backend.model.finance.FinContributorDetail;
import com.pennant.backend.model.financemanagement.ProvisionMovement;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEAmountCodesRIA;
import com.pennant.backend.model.rulefactory.AECommitment;
import com.pennant.backend.model.rulefactory.DataSet;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.coreinterface.exception.AccountNotFoundException;
import com.pennant.coreinterface.vo.FinanceCancellation;
import com.pennant.equation.process.FinanceCancellationProcess;

public class PostingsPreparationUtil implements Serializable {

	private static final long serialVersionUID = 1715547921928620037L;
	private Logger logger = Logger.getLogger(PostingsPreparationUtil.class);

	private AccountEngineExecution engineExecution;
	private AccountEngineExecutionRIA engineExecutionRIA;
	private FinContributorDetailDAO finContributorDetailDAO;
	private PostingsDAO postingsDAO;
	private AccountProcessUtil accountProcessUtil;
	private PostingsInterfaceService postingsInterfaceService;
	private ProvisionDAO provisionDAO;
	private ProvisionMovementDAO provisionMovementDAO;
	private FinanceCancellationProcess financeCancellationProcess;

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
	        Date dateAppDate, ProvisionMovement movement, boolean isProvPostings)
	        throws AccountNotFoundException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		List<ReturnDataSet> list = new ArrayList<ReturnDataSet>();

		// Accounting Set Execution to get Posting Details List
		if (isRIAFinance) {

			List<FinContributorDetail> contributorDetailList = getFinContributorDetailDAO()
			        .getFinContributorDetailByFinRef(dataSet.getFinReference(), "_AView");

			List<AEAmountCodesRIA> riaDetailList = getEngineExecutionRIA().prepareRIADetails(
			        contributorDetailList, dataSet.getFinReference());
			list = getEngineExecutionRIA().getAccEngineExecResults(dataSet, amountCodes, "Y",
			        riaDetailList);

		} else {
			list = getEngineExecution().getAccEngineExecResults(dataSet, amountCodes, "Y", null);//TODO
		}

		List<Object> returnList = postingsExecProcess(list, dataSet.getFinBranch(), dateAppDate,
		        isCreateNewAccount, isEODProcess, isProvPostings, movement, dataSet.getCustId());
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
	public List<Object> processCmtPostingDetails(Commitment commitment, String isCreateNow,
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

		List<Object> returnList = postingsExecProcess(list, commitment.getCmtBranch(), dateAppDate,
		        isCreateNow, false, false, null, commitment.getCustID());
		logger.debug("Leaving");
		return returnList;

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
	        Date dateAppDate, String createNow, boolean isEODProcess, boolean isProvPostings,
	        Object object, long custId) throws AccountNotFoundException {
		logger.debug("Entering");

		long linkTransId = Long.MIN_VALUE;

		//Method for Checking for Reverse Calculations Based upon Negative Amounts
		for (ReturnDataSet returnDataSet : list) {

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
				linkTransId = getPostingsDAO().getLinkedTransId(list.get(0));
			} else {
				linkTransId = list.get(0).getLinkedTranId();
			}
			list = getPostingsInterfaceService().doFillPostingDetails(list, branch, linkTransId,
			        createNow);
		}

		boolean isPostingSuccess = true;
		if (list != null && list.size() > 0) {
			for (int k = 0; k < list.size(); k++) {
				ReturnDataSet set = list.get(k);
				set.setLinkedTranId(linkTransId);
				set.setPostDate(dateAppDate);
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
					getPostingsDAO().saveHeader(list.get(0), "S", "");
				} else {
					getPostingsDAO().saveHeader(list.get(0), "F", "");
				}
				getPostingsDAO().saveEODBatch(list, "");

			} else if (isPostingSuccess) {
				getPostingsDAO().saveBatch(list, "", false);
			}
			
			if (isProvPostings && isPostingSuccess) {
				if (object instanceof ProvisionMovement) {
					ProvisionMovement movement = (ProvisionMovement) object;

					movement.setProvisionedAmt(movement.getProvisionedAmt().add(movement.getProvisionDue()));
					movement.setProvisionDue(BigDecimal.ZERO);
					movement.setProvisionPostSts("C");
					movement.setLinkedTranId(linkTransId);

					//Update Provision Movement Details
					getProvisionDAO().updateProvAmt(movement, "");
					getProvisionMovementDAO().update(movement, "");
				}
			}

			if (isPostingSuccess) {
				//Account Details Updation
				getAccountProcessUtil().updateAccountDetails(list, custId);
			}
		}

		List<Object> returnList = new ArrayList<Object>(2);
		returnList.add(isPostingSuccess);
		returnList.add(linkTransId);

		logger.debug("Leaving");
		return returnList;
	}

	/**
	 * Method To Process Finance Disbursement Cancellation posting
	 * IN PostingsPreparationUtil.java
	 * @param finReference
	 * @return boolean 
	 */
	public boolean processFinCanclPostings(String finReference) {
		logger.debug("Entering processFinCanclPostings()");
		boolean postingSuccess=false;
		try {
			// Call To Finance Disbursement Cancellation posting  interface 
				List<FinanceCancellation> list = getFinanceCancellationProcess().fetchCancelledFinancePostings(finReference);
				if (list != null && list.size() > 0) {
					FinanceCancellation cancellation = list.get(0);
					//Check For errors
					if (StringUtils.trimToEmpty(cancellation.getDsRspErrD()).equals("")) {
						updateCancelledPosting(list);
						postingSuccess=true;
					}

				}
		} catch (Exception e) {
			logger.debug(e);
		}
		logger.debug("Leaving processFinCanclPostings()");
		return postingSuccess;
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
				dataSet.setPostDate(DateUtility.getformatAS400Date(finCanl.getDsRspPOD()));
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

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

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
	
	public ProvisionDAO getProvisionDAO() {
    	return provisionDAO;
    }
	public void setProvisionDAO(ProvisionDAO provisionDAO) {
    	this.provisionDAO = provisionDAO;
    }

	public void setProvisionMovementDAO(ProvisionMovementDAO provisionMovementDAO) {
		this.provisionMovementDAO = provisionMovementDAO;
	}
	public ProvisionMovementDAO getProvisionMovementDAO() {
		return provisionMovementDAO;
	}

	public void setEngineExecutionRIA(AccountEngineExecutionRIA engineExecutionRIA) {
		this.engineExecutionRIA = engineExecutionRIA;
	}
	public AccountEngineExecutionRIA getEngineExecutionRIA() {
		return engineExecutionRIA;
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
}
