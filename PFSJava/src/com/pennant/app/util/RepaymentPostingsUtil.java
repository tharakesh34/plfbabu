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
 * FileName    		:  RepaymentPostingsUtil.java													*                           
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
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.backend.dao.FinRepayQueue.FinRepayQueueDAO;
import com.pennant.backend.dao.Repayments.FinanceRepaymentsDAO;
import com.pennant.backend.dao.finance.DefermentDetailDAO;
import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.model.FinRepayQueue.FinRepayQueue;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.finance.DefermentDetail;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.DataSet;
import com.pennant.backend.util.PennantConstants;
import com.pennant.coreinterface.exception.AccountNotFoundException;

public class RepaymentPostingsUtil implements Serializable {

    private static final long serialVersionUID = 4165353615228874397L;
	private static Logger logger = Logger.getLogger(RepaymentPostingsUtil.class);

	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private FinODDetailsDAO finODDetailsDAO;
	private FinanceMainDAO financeMainDAO;
	private FinRepayQueueDAO finRepayQueueDAO;
	private FinanceRepaymentsDAO financeRepaymentsDAO;
	private DefermentDetailDAO defermentDetailDAO;
	private SuspensePostingUtil suspensePostingUtil;
	private PostingsPreparationUtil postingsPreparationUtil;

	private AEAmountCodes amountCodes = null;

	/**
	 * Method for Posting Repayments and Update Repayment related Tables.
	 * 
	 * @param financeMain
	 * @param scheduleDetails
	 * @param financeProfitDetail
	 * @param dateValueDate
	 * @param curSchDate
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws AccountNotFoundException
	 */
	public boolean postingsRepayProcess(FinanceMain financeMain,
	        List<FinanceScheduleDetail> scheduleDetails, FinanceProfitDetail financeProfitDetail,
	        Date dateValueDate, FinRepayQueue finRepayQueue, BigDecimal repayAmountBal, boolean isRIAFinance)
	        throws AccountNotFoundException, IllegalAccessException, InvocationTargetException {

		logger.debug("Entering");
		
		boolean isPartialRepay = false;
		String phase = StringUtils.trimToEmpty(SystemParameterDetails.getSystemParameterValue(
		        "PHASE").toString());
		boolean isEODProcess = true;
		Date valueDate = finRepayQueue.getRpyDate();
		if (!phase.equals("EOD")) {
			isEODProcess = false;
			valueDate = dateValueDate;
		}

		AEAmounts aeAmounts = new AEAmounts();

		// DataSet Creation
		DataSet dataSet = aeAmounts.createDataSet(financeMain, "REPAY", dateValueDate,
		        finRepayQueue.getRpyDate());
		dataSet.setNewRecord(false);

		// AmountCodes Preparation
		// EOD Repayments should pass the value date as schedule for which
		// repayment is processing
		amountCodes = aeAmounts.procAEAmounts(financeMain, scheduleDetails, financeProfitDetail,
		        valueDate);

		//Set Repay Amount Codes
		amountCodes.setRpTot(repayAmountBal);
		if (repayAmountBal.compareTo(finRepayQueue.getSchdPftBal()) >= 0) {
			amountCodes.setRpPft(finRepayQueue.getSchdPftBal());
			amountCodes.setRpPri(amountCodes.getRpTot().subtract(amountCodes.getRpPft()));
		} else {
			amountCodes.setRpPft(amountCodes.getRpTot());
			amountCodes.setRpPri(BigDecimal.ZERO);
		}
		
		//Partial Repay Check
		if(amountCodes.getRpPft().compareTo(BigDecimal.ZERO) > 0){
			isPartialRepay = true;
		}

		amountCodes.setRefund(finRepayQueue.getRefundAmount());

		Date dateAppDate = DateUtility.getDBDate(SystemParameterDetails.getSystemParameterValue(
		        "APP_DATE").toString());
		List<Object> resultList = getPostingsPreparationUtil().processPostingDetails(dataSet,
		        amountCodes, true,isRIAFinance, "Y", dateAppDate, null, false);

		boolean isPostingSuccess = (Boolean) resultList.get(0);
		long linkedTranId = (Long) resultList.get(1);

		// if Postings Success then Updates will perform
		if (isPostingSuccess) {

			// 1. Finance Main Details Update
			financeMain.setFinRepaymentAmount(financeMain.getFinRepaymentAmount().add(
			        amountCodes.getRpPri()));
			if (amountCodes.getRpPri().compareTo(BigDecimal.ZERO) > 0) {
				getFinanceMainDAO().updateRepaymentAmount(financeMain.getFinReference(),
				        financeMain.getFinRepaymentAmount());
			}

			// 2. Finance Schedule Details Update
			FinanceScheduleDetail scheduleDetail = getFinanceScheduleDetailDAO()
			        .getFinanceScheduleDetailById(finRepayQueue.getFinReference(),
			                finRepayQueue.getRpyDate(), "", false);
			scheduleDetail = updateScheduleDetailsData(scheduleDetail, finRepayQueue.getFinRpyFor());
			getFinanceScheduleDetailDAO().update(scheduleDetail, "", false);

			// 3. Finance Deferment Details Update
			DefermentDetail defermentDetail = null;
			if (finRepayQueue.getFinRpyFor().equals(PennantConstants.DEFERED)) {
				defermentDetail = getDefermentDetailDAO().getDefermentDetailById(
				        finRepayQueue.getFinReference(), finRepayQueue.getRpyDate(), "", false);
				defermentDetail = updateDefermentDetailsData(defermentDetail);
				getDefermentDetailDAO().update(defermentDetail, "", false);
			}

			// 4. Finance Repayments Details
			FinanceRepayments repayment = prepareRepayDetailsData(finRepayQueue, dateValueDate,
			        linkedTranId, isEODProcess);
			getFinanceRepaymentsDAO().save(repayment, "");

			// 5. Finance Repay Queue Update
			finRepayQueue = prepareQueueData(finRepayQueue);
			if (finRepayQueue.isRcdNotExist()) {
				getFinRepayQueueDAO().save(finRepayQueue, "");
			} else {
				getFinRepayQueueDAO().update(finRepayQueue, "");
			}

			//Check for Schedule is Completely paid or not
			boolean isCompletlyPaid = false;

			if (scheduleDetail.isSchPftPaid()
			        && scheduleDetail.isSchPriPaid()
			        && (scheduleDetail.getDefProfitSchd()
			                .equals(scheduleDetail.getDefSchdPftPaid()))
			        && (scheduleDetail.getDefPrincipalSchd().equals(scheduleDetail
			                .getDefSchdPriPaid()))) {
				isCompletlyPaid = true;
				isPartialRepay = false;
			}

			boolean isLatePay = false;

			if (isEODProcess) {
				if (!isCompletlyPaid || finRepayQueue.getRpyDate().compareTo(dateValueDate) < 0) {
					isLatePay = true;
				}
			} else {
				if ((finRepayQueue.getRpyDate().compareTo(dateValueDate) < 0)) {
					isLatePay = true;
				}
			}

			FinODDetails finODDetails = null;

			if (isLatePay) {
				//OVERDUE DETAILS UPDATE
				finODDetails = overDuesPreparation(finRepayQueue, scheduleDetail, dateValueDate);

				//SUSPENSE
				boolean isDueSuspNow = false;
				if (isEODProcess) {
					isDueSuspNow = getSuspensePostingUtil().suspensePreparation(financeMain, financeProfitDetail,
					        finODDetails, dateValueDate, isRIAFinance);
				}
				
				//SUSPENSE RELEASE
				if (!isDueSuspNow && (isCompletlyPaid || isPartialRepay)) {
					getSuspensePostingUtil().suspReleasePreparation(financeMain,
							amountCodes.getRpPft(), finRepayQueue, dateValueDate, true, isRIAFinance);
				}
			}

		} else {
			// 6. Finance OverDue Details Save/Update
			if (isEODProcess || finRepayQueue.getRpyDate().compareTo(dateValueDate) <= 0) {
				FinanceScheduleDetail scheduleDetail = getFinanceScheduleDetailDAO()
				        .getFinanceScheduleDetailById(finRepayQueue.getFinReference(),
				                finRepayQueue.getRpyDate(), "", false);
				overDuesPreparation(finRepayQueue, scheduleDetail, dateValueDate);
			}

		}
		logger.debug("Leaving");
		return isPostingSuccess;
	}

	/**
	 * Method for Upadte Data for Finance schedule Details Object
	 * 
	 * @param detail
	 * @param main
	 * @param valueDate
	 * @param repayAmtBal
	 * @return
	 */
	private FinanceScheduleDetail updateScheduleDetailsData(FinanceScheduleDetail scheduleDetail,
	        String finRepayFor) {
		logger.debug("Entering");
		FinanceScheduleDetail schedule = scheduleDetail;

		// Finance Repayment for Defferment & Schedule 
		if (finRepayFor.equals(PennantConstants.DEFERED)) {

			schedule.setDefSchdPftPaid(schedule.getDefSchdPftPaid().add(amountCodes.getRpPft()));
			schedule.setDefSchdPriPaid(schedule.getDefSchdPriPaid().add(amountCodes.getRpPri()));

			// Finance Deffered Schedule Profit Balance Check
			if ((schedule.getDefProfitSchd().subtract(schedule.getDefSchdPftPaid()))
			        .compareTo(BigDecimal.ZERO) == 0) {
				schedule.setDefSchPftPaid(true);

				// Finance Deffered Schedule Principal Balance Check
				if ((schedule.getDefPrincipalSchd().subtract(schedule.getDefSchdPriPaid()))
				        .compareTo(BigDecimal.ZERO) == 0) {
					schedule.setDefSchPriPaid(true);
				}
			}

		} else if (finRepayFor.equals(PennantConstants.SCHEDULE)) {
			
			schedule.setDefSchPftPaid(true);
			schedule.setDefSchPriPaid(true);

			schedule.setSchdPftPaid(schedule.getSchdPftPaid().add(amountCodes.getRpPft()));
			schedule.setSchdPriPaid(schedule.getSchdPriPaid().add(amountCodes.getRpPri()));

			// Finance Schedule Profit Balance Check
			if ((schedule.getProfitSchd().subtract(schedule.getSchdPftPaid())).compareTo(BigDecimal.ZERO) == 0) {
				schedule.setSchPftPaid(true);

				// Finance Schedule Principal Balance Check
				if ((schedule.getPrincipalSchd().subtract(schedule.getSchdPriPaid()))
				        .compareTo(BigDecimal.ZERO) == 0) {
					schedule.setSchPriPaid(true);
				}
			}
		}
		logger.debug("Leaving");
		return schedule;
	}

	/**
	 * Method for Upadte Data for Finance Deferment Details Object
	 * 
	 * @param scheduleDetail
	 * @param finRepayQueue
	 * @return
	 */
	private DefermentDetail updateDefermentDetailsData(DefermentDetail detail) {
		logger.debug("Entering");
		detail.setDefPaidPftTillDate(detail.getDefPaidPftTillDate().add(amountCodes.getRpPft()));
		detail.setDefPaidPriTillDate(detail.getDefPaidPriTillDate().add(amountCodes.getRpPri()));
		detail.setDefPftBalance(detail.getDefPftBalance().subtract(amountCodes.getRpPft()));
		detail.setDefPriBalance(detail.getDefPriBalance().subtract(amountCodes.getRpPri()));
		logger.debug("Leaving");
		return detail;
	}

	/**
	 * Method for Preparing Data for Finance Repay Details Object
	 * 
	 * @param detail
	 * @param main
	 * @param valueDate
	 * @param repayAmtBal
	 * @return
	 */
	private FinanceRepayments prepareRepayDetailsData(FinRepayQueue queue, Date valueDate,
	        long linkedTranId, boolean isEODProcess) {

		logger.debug("Entering");
		FinanceRepayments repayment = new FinanceRepayments();
		Date curSchDate = DateUtility.getDBDate(SystemParameterDetails.getSystemParameterValue(
		        "APP_DATE").toString());

		repayment.setFinReference(queue.getFinReference());
		repayment.setFinPostDate(curSchDate);
		repayment.setFinRpyFor(queue.getFinRpyFor());
		repayment.setLinkedTranId(linkedTranId);

		repayment.setFinRpyAmount(amountCodes.getRpTot());
		repayment.setFinSchdDate(queue.getRpyDate());
		repayment.setFinValueDate(valueDate);
		repayment.setFinBranch(queue.getBranch());
		repayment.setFinType(queue.getFinType());
		repayment.setFinCustID(queue.getCustomerID());
		repayment.setFinSchdPftPaid(amountCodes.getRpPft());
		repayment.setFinSchdPriPaid(amountCodes.getRpPri());
		repayment.setFinTotSchdPaid(amountCodes.getRpTot());
		repayment.setFinFee(BigDecimal.ZERO);
		repayment.setFinWaiver(amountCodes.getWAIVER());
		repayment.setFinRefund(amountCodes.getRefund());

		logger.debug("Leaving");
		return repayment;
	}

	/**
	 * Method for Updating the Finance RepayQueue Data
	 * 
	 * @param repayQueue
	 * @param repayAmtBal
	 * @return
	 */
	private FinRepayQueue prepareQueueData(FinRepayQueue repayQueue) {
		logger.debug("Entering");
		repayQueue.setSchdPftPaid(repayQueue.getSchdPftPaid().add(amountCodes.getRpPft()));
		repayQueue.setSchdPriPaid(repayQueue.getSchdPriPaid().add(amountCodes.getRpPri()));
		repayQueue.setSchdPftBal(repayQueue.getSchdPftBal().subtract(amountCodes.getRpPft()));
		repayQueue.setSchdPriBal(repayQueue.getSchdPriBal().subtract(amountCodes.getRpPri()));

		// Modified Conditions for Balances Paid or not
		if (repayQueue.getSchdPftBal().compareTo(BigDecimal.ZERO) == 0) {

			repayQueue.setSchdIsPftPaid(true);
			if (repayQueue.getSchdPriBal().compareTo(BigDecimal.ZERO) == 0) {
				repayQueue.setSchdIsPriPaid(true);
			}
		}

		logger.debug("Leaving");
		return repayQueue;
	}

	/**
	 * Method for Preparing OverDue Details
	 * 
	 * @param odDetails
	 * @param scheduleDetail
	 * @param valueDate
	 * @param queue
	 * @return
	 */
	private FinODDetails prepareOverDueData(FinODDetails odDetails,
	        FinanceScheduleDetail scheduleDetail, Date valueDate, FinRepayQueue queue) {

		logger.debug("Entering");

		FinODDetails details = null;
		boolean isSave = false;
		if (odDetails != null) {
			details = odDetails;
		} else {
			isSave = true;
			details = new FinODDetails();
			details.setFinReference(queue.getFinReference());
			details.setFinBranch(queue.getBranch());
			details.setFinType(queue.getFinType());
			details.setCustID(queue.getCustomerID());
			details.setFinODSchdDate(queue.getRpyDate());
			details.setFinODFor(queue.getFinRpyFor());
		}

		if (queue.getFinRpyFor().equals(PennantConstants.DEFERED)) {
			details.setFinCurODAmt(scheduleDetail.getDefProfit()
			        .add(scheduleDetail.getDefPrincipal())
			        .subtract(scheduleDetail.getDefSchdPftPaid())
			        .subtract(scheduleDetail.getDefSchdPriPaid()));
		} else if (queue.getFinRpyFor().equals(PennantConstants.SCHEDULE)) {
			details.setFinCurODAmt(scheduleDetail.getProfitSchd()
			        .add(scheduleDetail.getPrincipalSchd())
			        .subtract(scheduleDetail.getSchdPftPaid())
			        .subtract(scheduleDetail.getSchdPriPaid()));
		}
		if (isSave) {
			details.setFinMaxODAmt(details.getFinCurODAmt());
		}
		details.setFinODTillDate(valueDate);
		details.setFinCurODDays(DateUtility.getDaysBetween(details.getFinODTillDate(),
		        details.getFinODSchdDate()));
		if (details.getFinCurODAmt().compareTo(BigDecimal.ZERO) == 0) {
			details.setFinCurODDays(0);
		}
		details.setFinLMdfDate(valueDate);
		details.setFinODSchdDate(details.getFinODSchdDate());

		logger.debug("Leaving");
		return details;
	}

	/**
	 * Method for Preparation or Update of OverDue Details data
	 * 
	 * @param finRepayQueue
	 * @param scheduleDetail
	 * @param dateValueDate
	 */
	public FinODDetails overDuesPreparation(FinRepayQueue finRepayQueue,
	        FinanceScheduleDetail scheduleDetail, Date dateValueDate) {
		logger.debug("Entering");

		FinODDetails odDetails = getFinODDetailsDAO().getFinODDetailsById(
		        finRepayQueue.getFinReference(), finRepayQueue.getRpyDate(),
		        finRepayQueue.getFinRpyFor());

		if (odDetails != null) {
			odDetails = prepareOverDueData(odDetails, scheduleDetail, dateValueDate, finRepayQueue);
			getFinODDetailsDAO().update(odDetails);
		} else {
			odDetails = prepareOverDueData(odDetails, scheduleDetail, dateValueDate, finRepayQueue);
			getFinODDetailsDAO().save(odDetails);
		}
		logger.debug("Leaving");
		return odDetails;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setFinRepayQueueDAO(FinRepayQueueDAO finRepayQueueDAO) {
		this.finRepayQueueDAO = finRepayQueueDAO;
	}
	public FinRepayQueueDAO getFinRepayQueueDAO() {
		return finRepayQueueDAO;
	}

	public FinanceRepaymentsDAO getFinanceRepaymentsDAO() {
		return financeRepaymentsDAO;
	}
	public void setFinanceRepaymentsDAO(FinanceRepaymentsDAO financeRepaymentsDAO) {
		this.financeRepaymentsDAO = financeRepaymentsDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}
	public FinanceMainDAO getFinanceMainDAO() {
		return financeMainDAO;
	}

	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}
	public FinanceScheduleDetailDAO getFinanceScheduleDetailDAO() {
		return financeScheduleDetailDAO;
	}

	public void setFinODDetailsDAO(FinODDetailsDAO finODDetailsDAO) {
		this.finODDetailsDAO = finODDetailsDAO;
	}
	public FinODDetailsDAO getFinODDetailsDAO() {
		return finODDetailsDAO;
	}

	public void setDefermentDetailDAO(DefermentDetailDAO defermentDetailDAO) {
		this.defermentDetailDAO = defermentDetailDAO;
	}
	public DefermentDetailDAO getDefermentDetailDAO() {
		return defermentDetailDAO;
	}

	public void setSuspensePostingUtil(SuspensePostingUtil suspensePostingUtil) {
		this.suspensePostingUtil = suspensePostingUtil;
	}
	public SuspensePostingUtil getSuspensePostingUtil() {
		return suspensePostingUtil;
	}

	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}
	public PostingsPreparationUtil getPostingsPreparationUtil() {
		return postingsPreparationUtil;
	}

}
