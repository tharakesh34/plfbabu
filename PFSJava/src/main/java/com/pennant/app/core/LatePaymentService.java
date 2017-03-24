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
 * FileName : OverDueRecoveryPostingsUtil.java *
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
package com.pennant.app.core;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.AEAmounts;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.dao.finance.FinODPenaltyRateDAO;
import com.pennant.backend.dao.financemanagement.OverdueChargeRecoveryDAO;
import com.pennant.backend.model.FinRepayQueue.FinRepayQueue;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinODPenaltyRate;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.financemanagement.OverdueChargeRecovery;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.DataSet;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.util.FinanceConstants;

public class LatePaymentService extends ServiceHelper {

	private static final long			serialVersionUID	= 6161809223570900644L;
	private static Logger				logger				= Logger.getLogger(LatePaymentService.class);

	private FinODDetailsDAO				finODDetailsDAO;
	private FinODPenaltyRateDAO			finODPenaltyRateDAO;
	private OverdueChargeRecoveryDAO	recoveryDAO;

	/**
	 * Default constructor
	 */
	public LatePaymentService() {
		super();
	}

	/**
	 * @param finMain
	 * @param finRepay
	 * @return
	 * @throws Exception
	 */
	public List<ReturnDataSet> processLPPenaltyRequest(FinanceMain finMain, FinRepayQueue finRepay) throws Exception {
		logger.debug(" Entering ");

		Date schdDate = finRepay.getRpyDate();
		FinODDetails finODDetails = getFinODDetailsForBatch(finRepay.getFinReference(), schdDate,
				finRepay.getFinRpyFor());

		List<ReturnDataSet> list = new ArrayList<ReturnDataSet>();
		if (finODDetails != null) {

			BigDecimal penalty = finODDetails.getTotPenaltyBal();
			BigDecimal waiverAmt = finODDetails.getTotWaived();

			if (penalty.compareTo(BigDecimal.ZERO) > 0) {
				// DataSet Creation
				DataSet dataSet = AEAmounts.createDataSet(finMain, AccountEventConstants.ACCEVENT_LATEPAY,
						DateUtility.getValueDate(), schdDate);
				dataSet.setNewRecord(false);

				AEAmountCodes amountCodes = new AEAmountCodes();
				amountCodes.setFinReference(finMain.getFinReference());
				amountCodes.setPENALTY(penalty);
				amountCodes.setWAIVER(waiverAmt);
				FinanceType financeType = getFinanceType(finRepay.getFinType());

				list = prepareAccounting(dataSet, amountCodes, financeType);
				list = setOtherDetails(list, finMain.getSecondaryAccount(), finRepay);
			}
		}

		logger.debug(" Leaving ");
		return list;
	}

	/**
	 * Method for Preparation or Update of OverDue Details data
	 * 
	 * @param financeMain
	 * @param finRepayQueue
	 * @param dateValueDate
	 * @return
	 * @throws Exception
	 */
	public List<ReturnDataSet> processLatePayInEOD(FinanceMain financeMain, FinRepayQueue finRepayQueue,
			Date dateValueDate) throws Exception {
		logger.debug("Entering");

		FinODDetails finODDetails = getFinODDetailsForBatch(finRepayQueue.getFinReference(),
				finRepayQueue.getRpyDate(), finRepayQueue.getFinRpyFor());

		// Finance Overdue Details Save or Updation
		if (finODDetails == null) {
			if (!finRepayQueue.isSchdIsPftPaid() || !finRepayQueue.isSchdIsPriPaid()) {
				finODDetails = createODDetails(finRepayQueue, dateValueDate);
			}
		} else {
			finODDetails = updateODDetails(finODDetails, finRepayQueue, dateValueDate, 1);
		}

		// Preparation for Overdue Penalty Recovery Details
		if (finODDetails != null) {
			processLPPenalty(finODDetails, dateValueDate, finRepayQueue);
			processLPProfit(finODDetails, dateValueDate, finRepayQueue);

			if (finRepayQueue.getPenaltyPayNow().compareTo(BigDecimal.ZERO) > 0) {
				return processLPPenaltyPostings(financeMain, finRepayQueue, finODDetails);
			}

		}

		logger.debug("Leaving");
		return Collections.emptyList();
	}

	/**
	 * Method for Preparation or Update of OverDue Details data
	 * 
	 * @param financeMain
	 * @param finRepayQueue
	 * @return
	 * @throws Exception
	 */
	public List<ReturnDataSet> processLatePay(FinanceMain financeMain, FinRepayQueue finRepayQueue) throws Exception {
		logger.debug("Entering");

		FinODDetails finODDetails = getFinODDetailsForBatch(finRepayQueue.getFinReference(),
				finRepayQueue.getRpyDate(), finRepayQueue.getFinRpyFor());

		// Finance Overdue Details Save or Updation
		if (finODDetails != null) {
			finODDetails = updateODDetails(finODDetails, finRepayQueue, DateUtility.getValueDate(), 0);
			if (finRepayQueue.getPenaltyPayNow().compareTo(BigDecimal.ZERO) > 0) {
				return processLPPenaltyPostings(financeMain, finRepayQueue, finODDetails);
			}
		}

		logger.debug("Leaving");
		return Collections.emptyList();
	}

	/**
	 * @param finReferencem
	 * @param rpyDate
	 * @param finRpyFor
	 * @return
	 */
	public FinODDetails getFinODDetailsForBatch(String finReferencem, Date rpyDate, String finRpyFor) {
		return finODDetailsDAO.getFinODDetailsForBatch(finReferencem, rpyDate, finRpyFor);
	}

	/**
	 * @param finRepayQueue
	 * @return
	 */
	public BigDecimal getScheduledRebateAmount(FinRepayQueue finRepayQueue) {

		BigDecimal rebate = BigDecimal.ZERO;
		BigDecimal advProfit = finRepayQueue.getAdvProfit();

		if (advProfit.compareTo(BigDecimal.ZERO) > 0) {

			BigDecimal schdProfit = finRepayQueue.getSchdPft();
			BigDecimal latePayProfit = BigDecimal.ZERO;

			FinODDetails odDetails = getFinODDetailsForBatch(finRepayQueue.getFinReference(),
					finRepayQueue.getRpyDate(), finRepayQueue.getFinRpyFor());
			if (odDetails != null) {
				latePayProfit = odDetails.getTotPftBal();
			}
			rebate = schdProfit.subtract(advProfit).subtract(latePayProfit);

			if (rebate.compareTo(BigDecimal.ZERO) < 0) {
				rebate = BigDecimal.ZERO;
			}

		}
		return rebate;
	}

	/**
	 * @param finMain
	 * @param finRepay
	 * @param odDetails
	 * @return
	 * @throws Exception
	 */
	private List<ReturnDataSet> processLPPenaltyPostings(FinanceMain finMain, FinRepayQueue finRepay,
			FinODDetails odDetails) throws Exception {
		logger.debug(" Entering ");

		Date schdDate = finRepay.getRpyDate();
		List<ReturnDataSet> list = null;
		BigDecimal penalty = finRepay.getPenaltyPayNow();
		// Calculate Pending Penalty Balance
		if (penalty.compareTo(BigDecimal.ZERO) > 0) {

			// DataSet Creation
			DataSet dataSet = AEAmounts.createDataSet(finMain, AccountEventConstants.ACCEVENT_LATEPAY,
					DateUtility.getValueDate(), schdDate);
			dataSet.setNewRecord(false);
			/*
			 * AmountCodes Preparation
			 */
			AEAmountCodes amountCodes = new AEAmountCodes();
			amountCodes.setFinReference(odDetails.getFinReference());
			amountCodes.setPENALTY(penalty);
			amountCodes.setWAIVER(odDetails.getTotWaived());

			// Accounting Set Execution to get Posting Details List
			FinanceType financeType = getFinanceType(finRepay.getFinType());

			list = prepareAccounting(dataSet, amountCodes, financeType);
			saveAccounting(list);
		}
		return list;
	}

	/**
	 * Method for Preparing OverDue Details
	 * 
	 * @param queue
	 * @param valueDate
	 * @return
	 */
	private FinODDetails createODDetails(FinRepayQueue queue, Date valueDate) {
		logger.debug(" Entering ");

		FinODDetails finODDetails = new FinODDetails();
		finODDetails.setFinReference(queue.getFinReference());
		finODDetails.setFinODSchdDate(queue.getRpyDate());
		finODDetails.setFinODFor(queue.getFinRpyFor());
		finODDetails.setFinBranch(queue.getBranch());
		finODDetails.setFinType(queue.getFinType());
		finODDetails.setCustID(queue.getCustomerID());
		// Prepare Overdue Penalty rate Details & set to Finance Overdue
		// Details
		FinODPenaltyRate penaltyRate = finODPenaltyRateDAO.getFinODPenaltyRateByRef(queue.getFinReference(),
				"_AView");
		if (penaltyRate != null) {
			finODDetails.setApplyODPenalty(penaltyRate.isApplyODPenalty());
			finODDetails.setODIncGrcDays(penaltyRate.isODIncGrcDays());
			finODDetails.setODChargeType(penaltyRate.getODChargeType());
			finODDetails.setODChargeAmtOrPerc(penaltyRate.getODChargeAmtOrPerc());
			finODDetails.setODChargeCalOn(penaltyRate.getODChargeCalOn());
			finODDetails.setODGraceDays(penaltyRate.getODGraceDays());
			finODDetails.setODAllowWaiver(penaltyRate.isODAllowWaiver());
			finODDetails.setODMaxWaiverPerc(penaltyRate.getODMaxWaiverPerc());
		}

		finODDetails.setFinCurODAmt(queue.getSchdPft().add(queue.getSchdPri()).subtract(queue.getSchdPftPaid())
				.subtract(queue.getSchdPriPaid()));
		finODDetails.setFinCurODPri(queue.getSchdPri().subtract(queue.getSchdPriPaid()));
		finODDetails.setFinCurODPft(queue.getSchdPft().subtract(queue.getSchdPftPaid()));
		finODDetails.setFinMaxODAmt(finODDetails.getFinCurODAmt());
		finODDetails.setFinMaxODPri(finODDetails.getFinCurODPri());
		finODDetails.setFinMaxODPft(finODDetails.getFinCurODPft());
		if (finODDetails.getTotPenaltyPaid().compareTo(BigDecimal.ZERO) == 0) {
			finODDetails.setGraceDays(finODDetails.getODGraceDays());
			finODDetails.setIncGraceDays(finODDetails.isODIncGrcDays());
		}
		finODDetails.setFinODTillDate(valueDate);
		finODDetails.setFinCurODDays(DateUtility.getDaysBetween(valueDate, finODDetails.getFinODSchdDate()) + 1);
		finODDetails.setFinLMdfDate(valueDate);
		if (finODDetails.getFinODSchdDate().compareTo(DateUtility.getValueDate()) <= 0) {
			finODDetailsDAO.save(finODDetails);
		}

		return finODDetails;
	}

	/**
	 * Method for Preparing OverDue Details
	 * 
	 * @param details
	 * @param queue
	 * @param valueDate
	 * @param increment
	 * @return
	 */
	private FinODDetails updateODDetails(FinODDetails details, FinRepayQueue queue, Date valueDate, int increment) {

		details.setFinCurODAmt(queue.getSchdPft().add(queue.getSchdPri()).subtract(queue.getSchdPftPaid())
				.subtract(queue.getSchdPriPaid()));
		details.setFinCurODPri(queue.getSchdPri().subtract(queue.getSchdPriPaid()));
		details.setFinCurODPft(queue.getSchdPft().subtract(queue.getSchdPftPaid()));
		details.setFinODTillDate(valueDate);
		details.setFinCurODDays(DateUtility.getDaysBetween(valueDate, details.getFinODSchdDate()) + increment);
		details.setFinLMdfDate(valueDate);
		finODDetailsDAO.updateBatch(details);
		// Update overdue Penalty if paid
		processLPPenaltyAndProfit(details, queue);

		return details;
	}

	/**
	 * @param details
	 * @param queue
	 * @param valueDate
	 */
	private void processLPPenaltyAndProfit(FinODDetails details, FinRepayQueue queue) {

		BigDecimal penaltypaynow = queue.getPenaltyPayNow();
		BigDecimal latePayPftPayNow = queue.getLatePayPftPayNow();

		if (penaltypaynow.compareTo(BigDecimal.ZERO) <= 0 && latePayPftPayNow.compareTo(BigDecimal.ZERO) <= 0) {
			return;
		}

		List<OverdueChargeRecovery> chargeRecoveries = recoveryDAO.getOverdueChargeRecovery(
				queue.getFinReference(), queue.getRpyDate());
		List<OverdueChargeRecovery> profitList = new ArrayList<OverdueChargeRecovery>();

		Iterator<OverdueChargeRecovery> it = chargeRecoveries.iterator();
		while (it.hasNext()) {
			OverdueChargeRecovery recovery = it.next();
			if (FinanceConstants.SCH_TYPE_LATEPAYPROFIT.equals(recovery.getFinODFor())) {
				profitList.add(recovery);
				it.remove();
			}
		}

		if (!chargeRecoveries.isEmpty()) {
			updateLPPenaltyAndProfit(profitList, details, latePayPftPayNow);
		}

		if (!chargeRecoveries.isEmpty()) {
			updateLPPenaltyAndProfit(chargeRecoveries, details, penaltypaynow);
		}
	}

	/**
	 * @param chargeRecoveries
	 * @param details
	 * @param penaltypaynow
	 */
	private void updateLPPenaltyAndProfit(List<OverdueChargeRecovery> chargeRecoveries, FinODDetails details,
			BigDecimal penaltypaynow) {

		if (penaltypaynow.compareTo(BigDecimal.ZERO) <= 0) {
			return;
		}

		if (chargeRecoveries != null && !chargeRecoveries.isEmpty()) {

			if (!StringUtils.trimToEmpty(details.getODChargeType()).equals(FinanceConstants.PENALTYTYPE_PERCONDUEDAYS)) {
				OverdueChargeRecovery recovery = chargeRecoveries.get(0);
				recovery.setPenaltyPaid(recovery.getPenaltyPaid().add(penaltypaynow));
				recovery.setPenaltyBal(getPenaltyBal(recovery));
				if (recovery.getPenaltyBal().compareTo(BigDecimal.ZERO) == 0) {
					recovery.setRcdCanDel(false);
				}
				recoveryDAO.updateRecoveryPayments(recovery);

			} else {
				BigDecimal totalRunning = penaltypaynow;
				for (OverdueChargeRecovery recovery : chargeRecoveries) {
					BigDecimal paidNow = adjustAmount(recovery.getPenalty(), totalRunning);
					totalRunning = totalRunning.subtract(paidNow);
					recovery.setPenaltyPaid(recovery.getPenaltyPaid().add(paidNow));
					recovery.setPenaltyBal(getPenaltyBal(recovery));
					recoveryDAO.updateRecoveryPayments(recovery);
					if (totalRunning.compareTo(BigDecimal.ZERO) == 0) {
						break;
					}
				}
			}
		}
	}

	/**
	 * @param recoverAmount
	 * @param totalRunning
	 * @return
	 */
	private BigDecimal adjustAmount(BigDecimal recoverAmount, BigDecimal totalRunning) {

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
	 * Method for Preparation of Overdue Recovery Penalty Record
	 * 
	 * @param odDetails
	 * @param dateValueDate
	 * @param repayQueue
	 */
	private void processLPPenalty(FinODDetails odDetails, Date dateValueDate, FinRepayQueue repayQueue) {
		logger.debug("Entering");

		Date finODSchdDate = odDetails.getFinODSchdDate();
		String finReference = odDetails.getFinReference();
		String finODFor = odDetails.getFinODFor();
		String chargeType = odDetails.getODChargeType();
		String profitDaysBasis = repayQueue.getProfitDaysBasis();

		if (!odDetails.isApplyODPenalty()) {
			return;
		}

		if (odDetails.getODChargeAmtOrPerc().compareTo(BigDecimal.ZERO) == 0) {
			return;
		}

		Date newPenaltyDate = DateUtility.addDays(finODSchdDate, odDetails.getODGraceDays());

		if (newPenaltyDate.compareTo(dateValueDate) > 0) {
			return;
		}

		Date finODDateCal = finODSchdDate;

		// include grace days in penalty calculation
		if (!odDetails.isODIncGrcDays()) {
			finODDateCal = DateUtility.addDays(finODSchdDate, odDetails.getODGraceDays());
		}

		OverdueChargeRecovery prvRecovery = recoveryDAO.getChargeRecoveryById(finReference, finODSchdDate,
				finODFor);

		OverdueChargeRecovery recovery = null;

		if (prvRecovery != null) {

			recovery = prvRecovery;

			if (FinanceConstants.PENALTYTYPE_PERCONDUEDAYS.equals(chargeType)) {

				boolean saveNewRecord = false;

				if (odDetails.getFinCurODAmt().compareTo(recovery.getFinCurODAmt()) != 0
						|| odDetails.getODChargeAmtOrPerc().compareTo(recovery.getPenaltyAmtPerc()) != 0) {

					recovery.setMovementDate(dateValueDate);
					recovery.setRcdCanDel(false);
					// overdue changed create new record
					saveNewRecord = true;

				} else {
					recovery.setODDays(DateUtility.getDaysBetween(dateValueDate, finODDateCal) + 1);
					recovery.setPenalty(calculateAmount(odDetails, finODDateCal, dateValueDate, profitDaysBasis));
					recovery.setPenaltyBal(recovery.getPenalty().subtract(recovery.getPenaltyPaid()));
				}

				recoveryDAO.updateChargeRecovery(recovery);

				if (saveNewRecord) {
					createNewLPPenalty(odDetails, recovery.getMovementDate(), dateValueDate, profitDaysBasis,
							recovery.getSeqNo());
				}

			} else {
				recovery.setODDays(DateUtility.getDaysBetween(dateValueDate, finODDateCal) + 1);
				recoveryDAO.updateChargeRecovery(recovery);
			}

		} else {
			createNewLPPenalty(odDetails, finODDateCal, dateValueDate, profitDaysBasis, 0);
		}

		updateLPPenaltyOrProfitTotals(odDetails);

		logger.debug("Leaving");
	}

	/**
	 * @param odDetails
	 * @param dateValueDate
	 * @param repayQueue
	 */
	private void processLPProfit(FinODDetails odDetails, Date dateValueDate, FinRepayQueue repayQueue) {
		logger.debug("Entering");

		// profit Calculations
		FinanceType financeType = getFinanceType(repayQueue.getFinType());
		String calMethod = financeType.getPastduePftCalMthd();

		BigDecimal rateToApply = BigDecimal.ZERO;

		if (CalculationConstants.PDPFTCAL_NOTAPP.equals(calMethod)) {
			return;
		}

		if (CalculationConstants.PDPFTCAL_SCHRATE.equals(calMethod)) {
			rateToApply = repayQueue.getSchdRate();
		} else if (CalculationConstants.PDPFTCAL_SCHRATEMARGIN.equals(calMethod)) {
			rateToApply = repayQueue.getSchdRate().add(financeType.getPastduePftMargin());
		}

		// setDefaults
		odDetails.setApplyODPenalty(true);
		odDetails.setODChargeAmtOrPerc(rateToApply);
		odDetails.setGraceDays(0);
		odDetails.setODChargeType(FinanceConstants.PENALTYTYPE_PERCONDUEDAYS);
		odDetails.setODChargeCalOn(FinanceConstants.ODCALON_STOT);
		odDetails.setFinODFor(FinanceConstants.SCH_TYPE_LATEPAYPROFIT);

		Date finODDateCal = odDetails.getFinODSchdDate();
		String finReference = odDetails.getFinReference();
		String finODFor = odDetails.getFinODFor();
		String profitDaysBasis = repayQueue.getProfitDaysBasis();
		BigDecimal totPft = odDetails.getTotPftAmt();

		if (odDetails.getODChargeAmtOrPerc().compareTo(BigDecimal.ZERO) == 0) {
			return;
		}

		OverdueChargeRecovery prvRecovery = recoveryDAO
				.getChargeRecoveryById(finReference, finODDateCal, finODFor);

		OverdueChargeRecovery recovery = null;

		BigDecimal rebate = getRebateAmount(finReference, finODDateCal);

		if (prvRecovery != null) {

			recovery = prvRecovery;

			boolean saveNewRecord = false;

			if (totPft.compareTo(rebate) >= 0) {
				return;
			}

			if (odDetails.getFinCurODAmt().compareTo(recovery.getFinCurODAmt()) != 0
					|| odDetails.getODChargeAmtOrPerc().compareTo(recovery.getPenaltyAmtPerc()) != 0) {
				recovery.setMovementDate(dateValueDate);
				recovery.setRcdCanDel(false);
				// overdue changed create new record
				saveNewRecord = true;

			} else {
				recovery.setODDays(DateUtility.getDaysBetween(dateValueDate, finODDateCal) + 1);
				recovery.setPenalty(calculateAmount(odDetails, finODDateCal, dateValueDate, profitDaysBasis));
				recovery.setPenaltyBal(recovery.getPenalty().subtract(recovery.getPenaltyPaid()));
			}

			chekRebateCap(totPft, rebate, recovery);
			recoveryDAO.updateChargeRecovery(recovery);

			if (saveNewRecord) {
				OverdueChargeRecovery newRecovery = getNewODCRecovery(odDetails, recovery.getMovementDate(),
						dateValueDate, profitDaysBasis, recovery.getSeqNo());
				createNewLPProfit(newRecovery, totPft, rebate);
			}

		} else {

			if (!ImplementationConstants.LATEPAY_PROFIT_CAL_ON_DAYZERO) {
				int oddays = DateUtility.getDaysBetween(dateValueDate, finODDateCal) + 1;
				if (oddays == 1) {
					return;
				}
			}

			OverdueChargeRecovery newRecovery = getNewODCRecovery(odDetails, finODDateCal, dateValueDate,
					profitDaysBasis, 0);
			createNewLPProfit(newRecovery, totPft, rebate);
		}

		updateLPPenaltyOrProfitTotals(odDetails);

		logger.debug("Leaving");

	}

	/**
	 * @param recovery
	 * @param totPft
	 * @param rebate
	 */
	private void createNewLPProfit(OverdueChargeRecovery recovery, BigDecimal totPft, BigDecimal rebate) {
		if (recovery != null) {
			chekRebateCap(totPft, rebate, recovery);
			if (recovery.getPenalty().compareTo(BigDecimal.ZERO) > 0) {
				recoveryDAO.save(recovery, "");
			}

		}
	}

	/**
	 * @param odDetails
	 * @param penaltyDate
	 * @param valueDate
	 * @param profitDayBasis
	 * @param seq
	 */
	private void createNewLPPenalty(FinODDetails odDetails, Date penaltyDate, Date valueDate, String profitDayBasis,
			int seq) {
		OverdueChargeRecovery recovery = getNewODCRecovery(odDetails, penaltyDate, valueDate, profitDayBasis, seq);
		if (recovery != null) {
			recoveryDAO.save(recovery, "");
		}
	}

	/**
	 * @param odDetails
	 * @param penaltyDate
	 * @param valueDate
	 * @param profitDayBasis
	 */
	private OverdueChargeRecovery getNewODCRecovery(FinODDetails odDetails, Date penaltyDate, Date valueDate,
			String profitDayBasis, int seq) {

		if (odDetails.getFinCurODAmt().compareTo(BigDecimal.ZERO) <= 0) {
			return null;
		}

		Date finODSchdDate = odDetails.getFinODSchdDate();
		String finReference = odDetails.getFinReference();
		String finODFor = odDetails.getFinODFor();

		OverdueChargeRecovery recovery = new OverdueChargeRecovery();
		recovery.setFinReference(finReference);
		recovery.setFinODSchdDate(finODSchdDate);
		recovery.setFinODFor(finODFor);
		recovery.setPenaltyPaid(BigDecimal.ZERO);
		recovery.setPenaltyBal(BigDecimal.ZERO);
		recovery.setSeqNo(seq + 1);
		recovery.setMovementDate(valueDate);
		recovery.setODDays(DateUtility.getDaysBetween(valueDate, penaltyDate) + 1);

		recovery.setFinCurODAmt(odDetails.getFinCurODAmt());
		recovery.setFinCurODPri(odDetails.getFinCurODPri());
		recovery.setFinCurODPft(odDetails.getFinCurODPft());
		recovery.setPenaltyType(odDetails.getODChargeType());
		recovery.setPenaltyCalOn(odDetails.getODChargeCalOn());
		recovery.setPenaltyAmtPerc(odDetails.getODChargeAmtOrPerc());
		recovery.setMaxWaiver(odDetails.getODMaxWaiverPerc());
		// Check Grace
		recovery.setPenalty(calculateAmount(odDetails, penaltyDate, valueDate, profitDayBasis));
		recovery.setPenaltyBal(getPenaltyBal(recovery));
		recovery.setRcdCanDel(true);
		return recovery;

	}

	/**
	 * @param odDetails
	 */
	private void updateLPPenaltyOrProfitTotals(FinODDetails odDetails) {

		String odfor = odDetails.getFinODFor();

		OverdueChargeRecovery odctotals = recoveryDAO.getTotals(odDetails.getFinReference(),
				odDetails.getFinODSchdDate(), odfor);

		if (FinanceConstants.SCH_TYPE_LATEPAYPROFIT.equals(odfor)) {
			odDetails.setTotPftAmt(getValue(odctotals.getPenalty()));
			odDetails.setTotPftPaid(getValue(odctotals.getPenaltyPaid()));
			odDetails.setTotPftBal(odDetails.getTotPftAmt().subtract(odDetails.getTotPftPaid()));
			finODDetailsDAO.updatePenaltyTotals(odDetails);
		} else {
			odDetails.setTotPenaltyAmt(getValue(odctotals.getPenalty()));
			odDetails.setTotPenaltyPaid(getValue(odctotals.getPenaltyPaid()));
			odDetails.setTotPenaltyBal(odDetails.getTotPenaltyAmt().subtract(odDetails.getTotPenaltyPaid()));
			finODDetailsDAO.updatePenaltyTotals(odDetails);
		}

	}

	/**
	 * @param odDetails
	 * @param finODDate
	 * @param dateValueDate
	 * @param profitDayBasis
	 * @return
	 */
	private BigDecimal calculateAmount(FinODDetails odDetails, Date finODDate, Date dateValueDate, String profitDayBasis) {
		logger.debug(" Entering ");

		String chargeType = StringUtils.trimToEmpty(odDetails.getODChargeType());
		String chargeCalOn = StringUtils.trimToEmpty(odDetails.getODChargeCalOn());
		BigDecimal amtOrPercetage = odDetails.getODChargeAmtOrPerc();

		BigDecimal pft = odDetails.getFinCurODPft();
		BigDecimal pri = odDetails.getFinCurODPri();
		BigDecimal total = pft.add(pri);

		if (FinanceConstants.PENALTYTYPE_FLAT.equals(chargeType)) {
			return amtOrPercetage;
		}

		BigDecimal odPenCalon = BigDecimal.ZERO;

		if (chargeCalOn.equals(FinanceConstants.ODCALON_SPFT)) {
			odPenCalon = pft;
		} else if (chargeCalOn.equals(FinanceConstants.ODCALON_SPRI)) {
			odPenCalon = pri;
		} else if (chargeCalOn.equals(FinanceConstants.ODCALON_STOT)) {
			odPenCalon = total;
		}

		if (FinanceConstants.PENALTYTYPE_PERCONETIME.equals(chargeType)) {
			return getPercentageValue(odPenCalon, amtOrPercetage);
		}

		if (FinanceConstants.PENALTYTYPE_PERCONDUEDAYS.equals(chargeType)) {
			return getDayPercValue(odPenCalon, amtOrPercetage, finODDate, dateValueDate, profitDayBasis);
		}

		int months = DateUtility.getMonthsBetween(finODDate, dateValueDate) + 1;

		// flat amount on pas due month
		if (FinanceConstants.PENALTYTYPE_FLATAMTONPASTDUEMTH.equals(chargeType)) {
			return amtOrPercetage.multiply(new BigDecimal(months));
		}

		// Percentage on past due month
		if (FinanceConstants.PENALTYTYPE_PERCONDUEMTH.equals(chargeType)) {
			return getPercentageValue(odPenCalon, amtOrPercetage).multiply(new BigDecimal(months));
		}

		return BigDecimal.ZERO;
	}

	/**
	 * Method for get the Percentage of given value
	 * 
	 * @param amount
	 * @param percent
	 * @return
	 */
	private BigDecimal getDayPercValue(BigDecimal amount, BigDecimal percent, Date startDate, Date endDate,
			String profitDayBasis) {

		endDate = DateUtility.addDays(endDate, 1);
		BigDecimal value = ((amount.multiply(percent)).multiply(CalculationUtil.getInterestDays(startDate, endDate,
				profitDayBasis))).divide(new BigDecimal(10000), RoundingMode.HALF_DOWN);

		return value.setScale(0, RoundingMode.HALF_DOWN);
	}

	/**
	 * @param amount
	 * @param percent
	 * @return
	 */
	private BigDecimal getPercentageValue(BigDecimal amount, BigDecimal percent) {
		return (amount.multiply(percent)).divide(new BigDecimal(10000), RoundingMode.HALF_DOWN);
	}

	/**
	 * @param recovery
	 * @return
	 */
	private BigDecimal getPenaltyBal(OverdueChargeRecovery recovery) {
		return recovery.getPenalty().subtract(recovery.getPenaltyPaid()).subtract(recovery.getWaivedAmt());
	}

	/**
	 * @param totPft
	 * @param rebate
	 * @param recovery
	 */
	private void chekRebateCap(BigDecimal totPft, BigDecimal rebate, OverdueChargeRecovery recovery) {
		BigDecimal totalnow = totPft.add(recovery.getPenalty());

		if (totalnow.compareTo(rebate) > 0) {
			recovery.setPenalty(rebate.subtract(totPft));
		}
	}

	/**
	 * @param finreference
	 * @param schdDate
	 * @return
	 */
	private BigDecimal getRebateAmount(final String finreference, Date schdDate) {

		FinanceScheduleDetail scheduleDetail;
		if (ImplementationConstants.REBATE_CAPPED_BY_FINANCE) {
			scheduleDetail = getFinanceScheduleDetailDAO().getFinanceScheduleForRebate(finreference, null);
		} else {
			scheduleDetail = getFinanceScheduleDetailDAO().getFinanceScheduleForRebate(finreference, schdDate);
		}

		if (scheduleDetail != null) {
			return getValue(scheduleDetail.getProfitSchd()).subtract(getValue(scheduleDetail.getAdvProfit()));
		}

		return BigDecimal.ZERO;
	}

	/**
	 * @param value
	 * @return
	 */
	private BigDecimal getValue(BigDecimal value) {
		if (value == null) {
			return BigDecimal.ZERO;
		} else {
			return value;
		}
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setFinODDetailsDAO(FinODDetailsDAO finODDetailsDAO) {
		this.finODDetailsDAO = finODDetailsDAO;
	}

	public void setRecoveryDAO(OverdueChargeRecoveryDAO recoveryDAO) {
		this.recoveryDAO = recoveryDAO;
	}

	public void setFinODPenaltyRateDAO(FinODPenaltyRateDAO finODPenaltyRateDAO) {
		this.finODPenaltyRateDAO = finODPenaltyRateDAO;
	}

}
