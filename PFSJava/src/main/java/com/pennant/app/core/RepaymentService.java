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
 * FileName    		:  RepaymentService.java													*                           
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
package com.pennant.app.core;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.AEAmounts;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.dao.FinRepayQueue.FinRepayQueueDAO;
import com.pennant.backend.dao.Repayments.FinanceRepaymentsDAO;
import com.pennant.backend.dao.finance.FinLogEntryDetailDAO;
import com.pennant.backend.dao.finance.FinanceSuspHeadDAO;
import com.pennant.backend.model.FinRepayQueue.FinRepayQueue;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.finance.FinLogEntryDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceSuspHead;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.DataSet;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.RepayConstants;

public class RepaymentService extends ServiceHelper {

	private static final long			serialVersionUID	= 4165353615228874397L;
	private static Logger				logger				= Logger.getLogger(RepaymentService.class);

	private FinRepayQueueDAO			finRepayQueueDAO;
	private FinanceSuspHeadDAO			financeSuspHeadDAO;
	private FinanceRepaymentsDAO		financeRepaymentsDAO;
	private FinLogEntryDetailDAO		finLogEntryDetailDAO;

	public RepaymentService() {
		super();
	}

	/**
	 * @param finMain
	 * @param finRepay
	 * @return
	 * @throws Exception
	 */
	public List<ReturnDataSet> processRepayRequest(Date date, FinanceMain finMain, FinRepayQueue finRepay) throws Exception {
		logger.debug("Entering");

		Date schdDate = finRepay.getRpyDate();
		FinanceType financeType = getFinanceType(finRepay.getFinType());
		List<FinanceScheduleDetail> scheduleDetails = getFinanceScheduleDetailDAO().getFinSchdDetailsForBatch(finMain.getFinReference());

		FinanceProfitDetail pftDetail = new FinanceProfitDetail();
		pftDetail.setFinReference(finMain.getFinReference());
		pftDetail.setAcrTillLBD(finRepay.getAcrTillLBD());
		pftDetail.setPftAmzSusp(finRepay.getPftAmzSusp());
		pftDetail.setAmzTillLBD(finRepay.getAmzTillLBD());

		DataSet dataSet = AEAmounts.createDataSet(finMain, AccountEventConstants.ACCEVENT_REPAY, date, schdDate);
		dataSet.setNewRecord(false);

		AEAmountCodes amountCodes = AEAmounts.procAEAmounts(finMain, scheduleDetails, pftDetail, schdDate);
		// Fee Details
		amountCodes.setInsPay(finRepay.getSchdInsBal());
		amountCodes.setSuplRentPay(finRepay.getSchdSuplRentBal());
		amountCodes.setIncrCostPay(finRepay.getSchdIncrCostBal());
		amountCodes.setRefund(finRepay.getRefundAmount());
		amountCodes.setSchFeePay(finRepay.getSchdFeeBal());
		amountCodes.setRebate(finRepay.getRebate());
		dataSet.setRebate(finRepay.getRebate());
		// Set Repay Amount Codes
		amountCodes.setRpPft(finRepay.getSchdPftBal());
		amountCodes.setRpPri(finRepay.getSchdPriBal());
		amountCodes.setRpTot(amountCodes.getRpPft().add(amountCodes.getRpPri()));
		List<ReturnDataSet> list = prepareAccounting(dataSet, amountCodes, financeType);
		list = setOtherDetails(list, finMain.getSecondaryAccount(), finRepay);

		logger.debug("Leaving");
		return list;
	}

	/**
	 * @param finMain
	 * @param finRepay
	 * @param totalpaidAmount
	 * @return
	 * @throws Exception
	 */
	public List<ReturnDataSet> processRepaymentsInEOD(Date date, FinanceMain finMain, FinRepayQueue finRepay, BigDecimal totalpaidAmount) throws Exception {
		logger.debug(" Entering ");

		List<ReturnDataSet> list = processRepayments(date, finMain, finRepay, totalpaidAmount);
		finRepayQueueDAO.update(finRepay, "");
		logger.debug(" Leaving ");
		return list;
	}

	/**
	 * @param finMain
	 * @param finRepay
	 * @throws Exception
	 */
	public List<ReturnDataSet> processRepayments(Date date, FinanceMain finMain, FinRepayQueue finRepay, BigDecimal totalpaidAmount) throws Exception {
		logger.debug("Entering");

		if (totalpaidAmount.compareTo(BigDecimal.ZERO) == 0) {
			return Collections.emptyList();
		}

		Date schdDate = finRepay.getRpyDate();
		FinanceType financeType = getFinanceType(finRepay.getFinType());
		List<FinanceScheduleDetail> scheduleDetails = getFinanceScheduleDetailDAO().getFinSchdDetailsForBatch(finMain.getFinReference());

		FinanceProfitDetail pftDetail = new FinanceProfitDetail();
		pftDetail.setFinReference(finMain.getFinReference());
		pftDetail.setAcrTillLBD(finRepay.getAcrTillLBD());
		pftDetail.setPftAmzSusp(finRepay.getPftAmzSusp());
		pftDetail.setAmzTillLBD(finRepay.getAmzTillLBD());

		String repaymethod = ImplementationConstants.REPAY_HIERARCHY_METHOD;
		// if non performing customer it should
		FinanceSuspHead suspHead = financeSuspHeadDAO.getFinanceSuspHeadById(finRepay.getFinReference(), "");
		if (suspHead != null && suspHead.isFinIsInSusp()) {
			repaymethod = RepayConstants.REPAY_HIERARCHY_FPICS;
		}
		setPaidAmounts(finRepay, totalpaidAmount, repaymethod);

		DataSet dataSet = AEAmounts.createDataSet(finMain, AccountEventConstants.ACCEVENT_REPAY, date, finRepay.getRpyDate());
		dataSet.setNewRecord(false);
		AEAmountCodes amountCodes = AEAmounts.procAEAmounts(finMain, scheduleDetails, pftDetail, schdDate);
		// Set Repay Amount Codes
		amountCodes.setRpPft(finRepay.getSchdPftPayNow());
		amountCodes.setRpPri(finRepay.getSchdPriPayNow());
		amountCodes.setRpTot(amountCodes.getRpPft().add(amountCodes.getRpPri()));
		amountCodes.setRefund(finRepay.getRefundAmount());
		// Fee Details
		amountCodes.setSchFeePay(finRepay.getSchdFeePayNow());
		amountCodes.setInsPay(finRepay.getSchdInsPayNow());
		amountCodes.setSuplRentPay(finRepay.getSchdSuplRentPayNow());
		amountCodes.setIncrCostPay(finRepay.getSchdIncrCostPayNow());
		amountCodes.setRebate(finRepay.getRebate());
		dataSet.setRebate(finRepay.getRebate());

		List<ReturnDataSet> list = prepareAccounting(dataSet, amountCodes, financeType);
		long linkedid = saveAccounting(list);
		// update Schedule details depends
		FinanceScheduleDetail scheduleDetail = updateSchdlDetail(finRepay);
		// update Repay Queue Data
		updateRepayQueueData(finRepay, scheduleDetail);
		// Finance Repayments Details
		if (checkPaidAmtNotZero(finRepay)) {
			FinanceRepayments repayment = prepareRepayDetailsData(finRepay, date, linkedid, amountCodes.getRpTot());
			financeRepaymentsDAO.save(repayment, "");
			//update profit details with last pay amount
			updateProfitDetais(date, finRepay);
		}

		// Finance Main Details Update
		if (amountCodes.getRpPri().compareTo(BigDecimal.ZERO) > 0) {
			finMain.setFinRepaymentAmount(finMain.getFinRepaymentAmount().add(amountCodes.getRpPri()));
			getFinanceMainDAO().updateRepaymentAmount(finMain.getFinReference(), finMain.getFinRepaymentAmount());
		}

		/*
		 * Create log entry for Action for Schedule Repayments Modification
		 */
		FinLogEntryDetail entryDetail = new FinLogEntryDetail();
		entryDetail.setFinReference(finMain.getFinReference());
		entryDetail.setEventAction(FinanceConstants.FINSER_EVENT_SCHDRPY);
		entryDetail.setSchdlRecal(false);
		entryDetail.setPostDate(date);
		entryDetail.setReversalCompleted(false);
		finLogEntryDetailDAO.save(entryDetail);

		logger.debug("Leaving");
		return list;

	}

	private void updateProfitDetais(Date date, FinRepayQueue finRepay) {
		FinanceProfitDetail financeProfitDetail = getFinanceProfitDetailDAO().getFinPftDetailForBatch(finRepay.getFinReference());
		financeProfitDetail.setLatestRpyDate(date);
		financeProfitDetail.setLatestRpyPri(finRepay.getSchdPftPayNow());
		financeProfitDetail.setLatestRpyPft(finRepay.getSchdPriPayNow());
		getFinanceProfitDetailDAO().updateLatestRpyDetails(financeProfitDetail);
	}

	/**
	 * @param finRepay
	 * @param totalpaidAmount
	 */
	private void setPaidAmounts(FinRepayQueue finRepay, BigDecimal totalpaidAmount, String repaymethod) {

		if (totalpaidAmount.compareTo(BigDecimal.ZERO) == 0) {
			return;
		}
		// Repayments Amount calculation
		BigDecimal schdIns = finRepay.getSchdInsBal();
		BigDecimal schdSuplRent = finRepay.getSchdSuplRentBal();
		BigDecimal schdIncrCost = finRepay.getSchdIncrCostBal();
		BigDecimal schdFee = finRepay.getSchdFeeBal();
		BigDecimal latePayPftBal = finRepay.getLatePayPftBal();
		BigDecimal totalRunning = totalpaidAmount;
		BigDecimal paidNow = BigDecimal.ZERO;
		BigDecimal totRpyPft = finRepay.getSchdPftBal();
		BigDecimal totRpyPri = finRepay.getSchdPriBal();
		BigDecimal penalty = finRepay.getPenaltyBal();

		char[] recoverySeq = repaymethod.toCharArray();

		for (char recover : recoverySeq) {

			switch (recover) {

			case RepayConstants.REPAY_PRINCIPAL:
				// principal
				paidNow = adjustAmount(totRpyPri, totalRunning);
				totalRunning = totalRunning.subtract(paidNow);
				finRepay.setSchdPriPayNow(paidNow);
				break;

			case RepayConstants.REPAY_PROFIT:

				String profit = ImplementationConstants.REPAY_INTEREST_HIERARCHY;

				char[] pftRcrySeqs = profit.toCharArray();

				for (char pftRcrySeq : pftRcrySeqs) {

					switch (pftRcrySeq) {

					case RepayConstants.REPAY_PROFIT:
						// profit
						paidNow = adjustAmount(totRpyPft, totalRunning);
						totalRunning = totalRunning.subtract(paidNow);
						finRepay.setSchdPftPayNow(paidNow);
						break;

					case RepayConstants.REPAY_LATEPAY_PROFIT:

						// over due profit collected before actual profit
						if (finRepay.getAdvProfit().compareTo(BigDecimal.ZERO) == 0) {
							paidNow = adjustAmount(latePayPftBal, totalRunning);
							totalRunning = totalRunning.subtract(paidNow);
							finRepay.setLatePayPftPayNow(paidNow);
						}
						break;

					default:
						break;

					}
				}

				break;

			case RepayConstants.REPAY_PENALTY:
				// penalty
				paidNow = adjustAmount(penalty, totalRunning);
				totalRunning = totalRunning.subtract(paidNow);
				finRepay.setPenaltyPayNow(paidNow);
				break;

			case RepayConstants.REPAY_OTHERS:
				// others
				paidNow = adjustAmount(schdIns, totalRunning);
				totalRunning = totalRunning.subtract(paidNow);
				finRepay.setSchdInsPayNow(paidNow);

				paidNow = adjustAmount(schdSuplRent, totalRunning);
				totalRunning = totalRunning.subtract(paidNow);
				finRepay.setSchdSuplRentPayNow(paidNow);

				paidNow = adjustAmount(schdIncrCost, totalRunning);
				totalRunning = totalRunning.subtract(paidNow);
				finRepay.setSchdIncrCostPayNow(paidNow);

				paidNow = adjustAmount(schdFee, totalRunning);
				totalRunning = totalRunning.subtract(paidNow);
				finRepay.setSchdFeePayNow(paidNow);
				break;

			default:
				break;
			}

		}

	}

	/**
	 * @param finRepayQueue
	 * @return
	 */
	private FinanceScheduleDetail updateSchdlDetail(FinRepayQueue finRepayQueue) {
		logger.debug("Entering");

		// Finance Schedule Details Update
		FinanceScheduleDetail schedule = getFinanceScheduleDetailDAO().getFinanceScheduleDetailById(finRepayQueue.getFinReference(), finRepayQueue.getRpyDate(), "", false);

		schedule.setFinReference(finRepayQueue.getFinReference());
		schedule.setSchDate(finRepayQueue.getRpyDate());
		// Fee Details paid Amounts updation
		schedule.setSchdFeePaid(schedule.getSchdFeePaid().add(finRepayQueue.getSchdFeePayNow()));
		schedule.setSchdInsPaid(schedule.getSchdInsPaid().add(finRepayQueue.getSchdInsPayNow()));
		schedule.setSuplRentPaid(schedule.getSuplRentPaid().add(finRepayQueue.getSchdSuplRentPayNow()));
		schedule.setIncrCostPaid(schedule.getIncrCostPaid().add(finRepayQueue.getSchdIncrCostPayNow()));
		schedule.setRebate(finRepayQueue.getRebate());
		if (finRepayQueue.getFinRpyFor().equals(FinanceConstants.SCH_TYPE_SCHEDULE)) {
			schedule.setSchdPftPaid(schedule.getSchdPftPaid().add(finRepayQueue.getSchdPftPayNow()));
			schedule.setSchdPriPaid(schedule.getSchdPriPaid().add(finRepayQueue.getSchdPriPayNow()));
		}
		
		schedule.setSchPftPaid(amountEqual(schedule.getProfitSchd(), schedule.getSchdPftPaid()));
		schedule.setSchPriPaid(amountEqual(schedule.getPrincipalSchd(), schedule.getSchdPriPaid()));

		getFinanceScheduleDetailDAO().updateForRpy(schedule, finRepayQueue.getFinRpyFor());

		logger.debug("Leaving");
		return schedule;
	}

	private boolean amountEqual(BigDecimal amout1, BigDecimal amout2) {
		if (amout1.compareTo(amout2) == 0) {
			return true;
		}
		return false;
	}

	/**
	 * @param finRepayQueue
	 * @param scheduleDetail
	 */
	private void updateRepayQueueData(FinRepayQueue finRepayQueue, FinanceScheduleDetail scheduleDetail) {
		// Amounts
		finRepayQueue.setSchdPftPaid(scheduleDetail.getSchdPftPaid());
		finRepayQueue.setSchdPriPaid(scheduleDetail.getSchdPriPaid());
		finRepayQueue.setSchdFeePaid(scheduleDetail.getSchdFeePaid());
		finRepayQueue.setSchdInsPaid(scheduleDetail.getSchdInsPaid());
		finRepayQueue.setSchdSuplRentPaid(scheduleDetail.getSuplRentPaid());
		finRepayQueue.setSchdIncrCostPaid(scheduleDetail.getIncrCostPaid());
		// boolean flags
		finRepayQueue.setSchdIsPftPaid(scheduleDetail.isSchPftPaid());
		finRepayQueue.setSchdIsPriPaid(scheduleDetail.isSchPriPaid());

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
	private FinanceRepayments prepareRepayDetailsData(FinRepayQueue queue, Date valueDate, long linkedTranId, BigDecimal totalRpyAmt) {
		logger.debug("Entering");

		FinanceRepayments repayment = new FinanceRepayments();
		Date curAppDate = DateUtility.getAppDate();

		repayment.setFinReference(queue.getFinReference());
		repayment.setFinSchdDate(queue.getRpyDate());
		repayment.setFinRpyFor(queue.getFinRpyFor());
		repayment.setLinkedTranId(linkedTranId);

		repayment.setFinRpyAmount(totalRpyAmt);
		repayment.setFinPostDate(curAppDate);
		repayment.setFinValueDate(valueDate);
		repayment.setFinBranch(queue.getBranch());
		repayment.setFinType(queue.getFinType());
		repayment.setFinCustID(queue.getCustomerID());
		repayment.setFinSchdPftPaid(queue.getSchdPftPayNow());
		repayment.setFinSchdPriPaid(queue.getSchdPriPayNow());
		repayment.setFinTotSchdPaid(queue.getSchdPftPayNow().add(queue.getSchdPriPayNow()));
		repayment.setFinFee(BigDecimal.ZERO);
		repayment.setFinWaiver(queue.getWaivedAmount());
		repayment.setFinRefund(queue.getRefundAmount());

		// Fee Details
		repayment.setSchdFeePaid(queue.getSchdFeePayNow());
		repayment.setSchdInsPaid(queue.getSchdInsPayNow());
		repayment.setSchdSuplRentPaid(queue.getSchdSuplRentPayNow());
		repayment.setSchdIncrCostPaid(queue.getSchdIncrCostPayNow());

		logger.debug("Leaving");
		return repayment;
	}

	/**
	 * @param recoverAmount
	 * @param totalRunning
	 * @return
	 */
	public BigDecimal adjustAmount(BigDecimal recoverAmount, BigDecimal totalRunning) {

		if (recoverAmount.compareTo(BigDecimal.ZERO) == 0 || totalRunning.compareTo(BigDecimal.ZERO) == 0) {
			return BigDecimal.ZERO;
		}

		BigDecimal balance = totalRunning.subtract(recoverAmount);
		if (balance.compareTo(BigDecimal.ZERO) < 0) {
			return totalRunning;
		} else {
			return recoverAmount;
		}

	}

	/**
	 * @param finRepayQueue
	 * @return
	 */
	private boolean checkPaidAmtNotZero(FinRepayQueue finRepayQueue) {
		BigDecimal paidAmount = BigDecimal.ZERO;
		paidAmount = paidAmount.add(finRepayQueue.getSchdInsPayNow());
		paidAmount = paidAmount.add(finRepayQueue.getSchdSuplRentPayNow());
		paidAmount = paidAmount.add(finRepayQueue.getSchdIncrCostPayNow());
		paidAmount = paidAmount.add(finRepayQueue.getSchdFeePayNow());
		paidAmount = paidAmount.add(finRepayQueue.getSchdPftPayNow());
		paidAmount = paidAmount.add(finRepayQueue.getSchdPriPayNow());
		if (paidAmount.compareTo(BigDecimal.ZERO) > 0) {
			return true;
		}

		return false;
	}



	public void setFinRepayQueueDAO(FinRepayQueueDAO finRepayQueueDAO) {
		this.finRepayQueueDAO = finRepayQueueDAO;
	}


	public void setFinanceRepaymentsDAO(FinanceRepaymentsDAO financeRepaymentsDAO) {
		this.financeRepaymentsDAO = financeRepaymentsDAO;
	}


	public void setFinLogEntryDetailDAO(FinLogEntryDetailDAO finLogEntryDetailDAO) {
		this.finLogEntryDetailDAO = finLogEntryDetailDAO;
	}


	public void setFinanceSuspHeadDAO(FinanceSuspHeadDAO financeSuspHeadDAO) {
		this.financeSuspHeadDAO = financeSuspHeadDAO;
	}

}
