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
package com.pennant.app.util;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.security.auth.login.AccountNotFoundException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.Interface.model.IAccounts;
import com.pennant.Interface.service.AccountInterfaceService;
import com.pennant.app.constants.AccountEventConstants;
import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.dao.finance.FinODPenaltyRateDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.financemanagement.OverdueChargeRecoveryDAO;
import com.pennant.backend.model.FinRepayQueue.FinRepayQueue;
import com.pennant.backend.model.FinRepayQueue.FinRepayQueueHeader;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinODPenaltyRate;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.financemanagement.OverdueChargeRecovery;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.cache.util.AccountingConfigCache;
import com.pennanttech.pennapps.core.InterfaceException;

public class OverDueRecoveryPostingsUtil implements Serializable {
	private static final long			serialVersionUID	= 6161809223570900644L;
	private static Logger				logger				= Logger.getLogger(OverDueRecoveryPostingsUtil.class);

	private FinanceMainDAO				financeMainDAO;
	private FinODDetailsDAO				finODDetailsDAO;
	private OverdueChargeRecoveryDAO	recoveryDAO;
	private FinODPenaltyRateDAO			finODPenaltyRateDAO;
	private AccountInterfaceService		accountInterfaceService;
	private PostingsPreparationUtil		postingsPreparationUtil;

	private AEEvent						aeEvent				= null;

	/**
	 * Default constructor
	 */
	public OverDueRecoveryPostingsUtil() {
		super();
	}

	/**
	 * Method for Posting OverDue Recoveries .
	 * 
	 * @param financeMain
	 * @param scheduleDetails
	 * @param financeProfitDetail
	 * @param dateValueDate
	 * @param curSchDate
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws InterfaceException
	 */
	public AEEvent recoveryPayment(FinanceMain financeMain, Date dateValueDate, Date postDate, Date schDate, String finODFor,
			Date movementDate, BigDecimal penaltyPaidNow, BigDecimal penaltyWaived, String chargeType,
			AEEvent aeEvent, FinRepayQueueHeader rpyQueueHeader, boolean isFirstTimePay) throws InterfaceException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		String finReference = financeMain.getFinReference();

		// AmountCodes Preparation
		// EOD Repayments should pass the value date as schedule for which
		// Repayment is processing
		AEAmountCodes amountCodes = null;
		if(aeEvent == null){

			aeEvent = new AEEvent();
			aeEvent.setAeAmountCodes(new AEAmountCodes());
			amountCodes = aeEvent.getAeAmountCodes();
			aeEvent.setFinReference(financeMain.getFinReference());
			aeEvent.setCustID(financeMain.getCustID());
			aeEvent.setFinType(financeMain.getFinType());
			aeEvent.setBranch(financeMain.getFinBranch());
			aeEvent.setCcy(financeMain.getFinCcy());
			aeEvent.setPostingUserBranch(rpyQueueHeader.getPostBranch());
			aeEvent.setAccountingEvent(AccountEventConstants.ACCEVENT_LATEPAY);
			aeEvent.setValueDate(dateValueDate);
			aeEvent.setPostDate(postDate);
			aeEvent.setEntityCode(financeMain.getLovDescEntityCode());
			amountCodes.setPartnerBankAc(rpyQueueHeader.getPartnerBankAc());
			amountCodes.setPartnerBankAcType(rpyQueueHeader.getPartnerBankAcType());
			amountCodes.setFinType(financeMain.getFinType());

		}else{
			amountCodes = aeEvent.getAeAmountCodes();
		}

		amountCodes.setPenaltyPaid(penaltyPaidNow);
		amountCodes.setPenaltyWaived(penaltyWaived);
        aeEvent.setPostRefId(rpyQueueHeader.getReceiptId());
        aeEvent.setPostingId(financeMain.getPostingId());
		aeEvent.setSchdDate(schDate);
		aeEvent.setEOD(false);

		String phase = SysParamUtil.getValueAsString(PennantConstants.APP_PHASE);

		if (!phase.equals(PennantConstants.APP_PHASE_DAY)) {
			aeEvent.setEOD(true);
		}

		HashMap<String, Object> dataMap = amountCodes.getDeclaredFieldValues();
		
		if (rpyQueueHeader.getProfit().compareTo(BigDecimal.ZERO) == 0
				&& rpyQueueHeader.getPrincipal().compareTo(BigDecimal.ZERO) == 0
				&& rpyQueueHeader.getTds().compareTo(BigDecimal.ZERO) == 0
				&& rpyQueueHeader.getLateProfit().compareTo(BigDecimal.ZERO) == 0
				&& rpyQueueHeader.getPenalty().compareTo(BigDecimal.ZERO) > 0 && isFirstTimePay) {

			if(rpyQueueHeader.getExtDataMap() != null){
				dataMap.putAll(rpyQueueHeader.getExtDataMap());
			}
		}
		
		aeEvent.setAccountingEvent(AccountEventConstants.ACCEVENT_LATEPAY);
		aeEvent.setDataMap(dataMap);
		aeEvent.getAcSetIDList().clear();
		
		if (StringUtils.isNotBlank(financeMain.getPromotionCode())) {
			aeEvent.getAcSetIDList().add(AccountingConfigCache.getAccountSetID(financeMain.getPromotionCode(), 
					aeEvent.getAccountingEvent(), FinanceConstants.MODULEID_PROMOTION));
		} else {
			aeEvent.getAcSetIDList().add(AccountingConfigCache.getAccountSetID(financeMain.getFinType(), 
					aeEvent.getAccountingEvent(), FinanceConstants.MODULEID_FINTYPE));
		}

		// Posting details calling
		aeEvent = getPostingsPreparationUtil().postAccounting(aeEvent);
		aeEvent.getAeAmountCodes().setPenaltyPaid(BigDecimal.ZERO);
		aeEvent.getAeAmountCodes().setPenaltyWaived(BigDecimal.ZERO);

		//Overdue Details Updation for Paid Penalty
		if (aeEvent.isPostingSucess()) {

			//Overdue Details Updation for Totals
			FinODDetails detail = new FinODDetails();
			detail.setFinReference(finReference);
			detail.setFinODSchdDate(schDate);
			detail.setFinODFor(finODFor);
			detail.setTotPenaltyAmt(BigDecimal.ZERO);
			detail.setTotPenaltyPaid(penaltyPaidNow);
			detail.setTotPenaltyBal((penaltyPaidNow.add(penaltyWaived)).negate());
			detail.setTotWaived(penaltyWaived);
			getFinODDetailsDAO().updateTotals(detail);

		}

		logger.debug("Leaving");
		return aeEvent;
	}

	/**
	 * Method for Preparation or Update of OverDue Details data
	 * 
	 * @param finRepayQueue
	 * @param scheduleDetail
	 * @param dateValueDate
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws InterfaceException
	 */
	public FinODDetails overDueDetailPreparation(FinRepayQueue finRepayQueue, String profitDayBasis,
			Date dateValueDate, boolean isAfterRecovery, boolean isEnqPurpose) throws InterfaceException,
			IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		Date curBussDate = DateUtility.getAppValueDate();

		FinODDetails odDetails = getFinODDetailsDAO().getFinODDetailsForBatch(finRepayQueue.getFinReference(),
				finRepayQueue.getRpyDate());

		//Finance Overdue Details Save or Updation
		if (odDetails != null) {
			odDetails = prepareOverDueData(odDetails, dateValueDate, finRepayQueue, isAfterRecovery);
			if (!isEnqPurpose) {
				getFinODDetailsDAO().updateBatch(odDetails);
			}
		} else {
			odDetails = prepareOverDueData(odDetails, dateValueDate, finRepayQueue, isAfterRecovery);
			if (!isEnqPurpose && odDetails.getFinODSchdDate().compareTo(curBussDate) <= 0) {
				getFinODDetailsDAO().save(odDetails);
			}
		}

		logger.debug("Leaving");
		return odDetails;
	}

	/**
	 * Method for Preparation or Update of OverDue Details data
	 * 
	 * @param finRepayQueue
	 * @param pftDaysBasis
	 * @param dateValueDate
	 * @param isAfterRecovery
	 * @param isEnqPurpose
	 * @return
	 * @throws InterfaceException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public List<Object> recoveryCalculation(FinRepayQueue finRepayQueue, String pftDaysBasis, Date dateValueDate,
			boolean isAfterRecovery, boolean isEnqPurpose) throws InterfaceException, IllegalAccessException,
			InvocationTargetException {
		logger.debug("Entering");

		List<Object> odObjDetails = new ArrayList<Object>();

		//Overdue Detail Calculation
		FinODDetails odDetails = overDueDetailPreparation(finRepayQueue, pftDaysBasis, dateValueDate, isAfterRecovery,
				isEnqPurpose);

		//Preparation for Overdue Penalty Recovery Details
		OverdueChargeRecovery rec = overdueRecoverCalculation(odDetails, dateValueDate, pftDaysBasis, isAfterRecovery,
				isEnqPurpose, false);

		odObjDetails.add(odDetails);
		odObjDetails.add(rec);

		logger.debug("Leaving");
		return odObjDetails;
	}

	/**
	 * Method for Preparation of Overdue Recovery Penalty Record
	 * 
	 * @param odDetails
	 * @param penaltyRate
	 * @param dateValueDate
	 * @param profitDayBasis
	 */
	private OverdueChargeRecovery overdueRecoverCalculation(FinODDetails odDetails, Date dateValueDate,
			String profitDayBasis, boolean isAfterRecovery, boolean isEnqPurpose, boolean doPostings) {
		logger.debug("Entering");

		Date newPenaltyDate = DateUtility.addDays(odDetails.getFinODSchdDate(), odDetails.getODGraceDays());

		OverdueChargeRecovery recovery = null;
		//Check Condition for Overdue Recovery Details Entries
		if (newPenaltyDate.compareTo(dateValueDate) <= 0) {

			boolean searchForPenalty = true;
			//Condition Checking for Overdue Penalty Exist or not
			if (!odDetails.isApplyODPenalty()) {
				searchForPenalty = false;
			} else {
				if (odDetails.getODChargeAmtOrPerc().compareTo(BigDecimal.ZERO) == 0) {
					searchForPenalty = false;
				}
			}

			//Delete Max Finance Effective Date Recovery Record if Overdue Payment
			//will not happen, only for Percentage on Due Days Charge type
			OverdueChargeRecovery prvRecovery = null;
			if (searchForPenalty) {

				prvRecovery = getRecoveryDAO().getMaxOverdueChargeRecoveryById(odDetails.getFinReference(),
						odDetails.getFinODSchdDate(), odDetails.getFinODFor(), "_AMView");

				if (FinanceConstants.PENALTYTYPE_PERC_ON_DUEDAYS.equals(odDetails.getODChargeType())
						&& prvRecovery != null && prvRecovery.isRcdCanDel() && !isEnqPurpose) {
					getRecoveryDAO().deleteUnpaid(odDetails.getFinReference(), odDetails.getFinODSchdDate(),
							odDetails.getFinODFor(), "");
				}
			} else if (!searchForPenalty) {

				prvRecovery = getRecoveryDAO().getMaxOverdueChargeRecoveryById(odDetails.getFinReference(),
						odDetails.getFinODSchdDate(), odDetails.getFinODFor(), "_AMView");

				if (prvRecovery != null && prvRecovery.isRcdCanDel()) {
					if (!isEnqPurpose) {
						getRecoveryDAO().deleteUnpaid(odDetails.getFinReference(), odDetails.getFinODSchdDate(),
								odDetails.getFinODFor(), "");
					}

					BigDecimal prvPenalty = BigDecimal.ZERO.subtract(prvRecovery.getPenalty());
					BigDecimal prvPenaltyBal = BigDecimal.ZERO.subtract(prvRecovery.getPenaltyBal());

					//Overdue Details Updation for Totals
					FinODDetails detail = new FinODDetails();
					detail.setFinReference(odDetails.getFinReference());
					detail.setFinODSchdDate(odDetails.getFinODSchdDate());
					detail.setFinODFor(odDetails.getFinODFor());
					detail.setTotPenaltyAmt(prvPenalty);
					detail.setTotPenaltyPaid(BigDecimal.ZERO);
					detail.setTotPenaltyBal(prvPenaltyBal);
					detail.setTotWaived(BigDecimal.ZERO);

					if (!isEnqPurpose) {
						getFinODDetailsDAO().updateTotals(detail);
					}
				}
			}

			//Save/Update Overdue Recovery Details based upon Search Criteria
			if (searchForPenalty) {

				BigDecimal prvPenalty = BigDecimal.ZERO;
				BigDecimal prvPenaltyBal = BigDecimal.ZERO;

				if (FinanceConstants.PENALTYTYPE_PERC_ON_DUEDAYS.equals(odDetails.getODChargeType())
						&& prvRecovery != null && prvRecovery.isRcdCanDel()) {

					String tableType = "_AMView";
					if (isEnqPurpose) {
						tableType = "_ATView";
					}

					recovery = getRecoveryDAO().getMaxOverdueChargeRecoveryById(odDetails.getFinReference(),
							odDetails.getFinODSchdDate(), odDetails.getFinODFor(), tableType);
				} else {
					recovery = prvRecovery;
				}

				boolean resetTotals = true;
				int seqNo = 1;
				//Stop calculation for paid penalty for Charge Type 'FLAT' & 'PERCONETIME'
				if (!FinanceConstants.PENALTYTYPE_PERC_ON_DUEDAYS.equals(odDetails.getODChargeType())
						&& recovery != null
						&& (recovery.getPenaltyPaid().compareTo(BigDecimal.ZERO) > 0 || recovery.getWaivedAmt()
								.compareTo(BigDecimal.ZERO) > 0)) {

					return recovery;

				} else {

					//Store Previous values for Total Calculations
					if (recovery != null) {
						seqNo = recovery.getSeqNo() + 1;

						if (prvRecovery != null && prvRecovery.isRcdCanDel()) {
							prvPenalty = prvPenalty.subtract(prvRecovery.getPenalty());
							prvPenaltyBal = prvPenaltyBal.subtract(prvRecovery.getPenalty());
						}
						resetTotals = false;
					}

					if (!FinanceConstants.PENALTYTYPE_PERC_ON_DUEDAYS.equals(odDetails.getODChargeType())
							&& prvRecovery != null) {
						if (!isEnqPurpose) {
							getRecoveryDAO().deleteUnpaid(odDetails.getFinReference(), odDetails.getFinODSchdDate(),
									odDetails.getFinODFor(), "");
						}
						recovery = null;
					}
				}

				Date finODDate = null;
				boolean isRecordSave = true;

				if (recovery == null) {

					recovery = new OverdueChargeRecovery();
					recovery.setFinReference(odDetails.getFinReference());
					recovery.setFinODSchdDate(odDetails.getFinODSchdDate());
					recovery.setFinODFor(odDetails.getFinODFor());
					recovery.setODDays(DateUtility.getDaysBetween(dateValueDate, odDetails.getFinODSchdDate()));
					finODDate = odDetails.getFinODSchdDate();

					if (odDetails.isODIncGrcDays()) {
						finODDate = odDetails.getFinODSchdDate();
					} else {
						finODDate = DateUtility.addDays(odDetails.getFinODSchdDate(), odDetails.getODGraceDays());
					}
					recovery.setPenaltyPaid(BigDecimal.ZERO);
					recovery.setPenaltyBal(BigDecimal.ZERO);

				} else {
					finODDate = recovery.getMovementDate();
				}

				recovery.setSeqNo(seqNo);
				recovery.setMovementDate(dateValueDate);
				recovery.setODDays(DateUtility.getDaysBetween(dateValueDate, finODDate));
				if (isAfterRecovery) {
					recovery.setMovementDate(DateUtility.addDays(dateValueDate, 1));
					recovery.setODDays(recovery.getODDays() + 1);
				}

				recovery.setFinCurODAmt(odDetails.getFinCurODAmt());
				recovery.setFinCurODPri(odDetails.getFinCurODPri());
				recovery.setFinCurODPft(odDetails.getFinCurODPft());
				recovery.setPenaltyType(odDetails.getODChargeType());
				recovery.setPenaltyCalOn(odDetails.getODChargeCalOn());
				recovery.setPenaltyAmtPerc(odDetails.getODChargeAmtOrPerc());
				recovery.setMaxWaiver(odDetails.getODMaxWaiverPerc());

				//Overdue Penalty Amount Calculation Depends on applied Charge Type
				if (FinanceConstants.PENALTYTYPE_FLAT.equals(odDetails.getODChargeType())) {

					recovery.setPenalty(odDetails.getODChargeAmtOrPerc());

				} else if (FinanceConstants.PENALTYTYPE_PERC_ONETIME.equals(odDetails.getODChargeType())) {

					if (odDetails.getODChargeCalOn().equals(FinanceConstants.ODCALON_SPFT)) {

						recovery.setPenalty(getPercentageValue(recovery.getFinCurODPft(),
								odDetails.getODChargeAmtOrPerc()));

					} else if (odDetails.getODChargeCalOn().equals(FinanceConstants.ODCALON_SPRI)) {

						recovery.setPenalty(getPercentageValue(recovery.getFinCurODPri(),
								odDetails.getODChargeAmtOrPerc()));

					} else {
						recovery.setPenalty(getPercentageValue(recovery.getFinCurODAmt(),
								odDetails.getODChargeAmtOrPerc()));
					}

				} else if (FinanceConstants.PENALTYTYPE_PERC_ON_DUEDAYS.equals(odDetails.getODChargeType())) {

					if (odDetails.getODChargeCalOn().equals(FinanceConstants.ODCALON_SPFT)) {

						recovery.setPenalty(getDayPercValue(recovery.getFinCurODPft(),
								odDetails.getODChargeAmtOrPerc(), finODDate, dateValueDate, isAfterRecovery,
								profitDayBasis));

					} else if (odDetails.getODChargeCalOn().equals(FinanceConstants.ODCALON_SPRI)) {

						recovery.setPenalty(getDayPercValue(recovery.getFinCurODPri(),
								odDetails.getODChargeAmtOrPerc(), finODDate, dateValueDate, isAfterRecovery,
								profitDayBasis));

					} else {
						recovery.setPenalty(getDayPercValue(recovery.getFinCurODAmt(),
								odDetails.getODChargeAmtOrPerc(), finODDate, dateValueDate, isAfterRecovery,
								profitDayBasis));
					}
				}

				//Total Penalty Details Recalculation 
				prvPenalty = recovery.getPenalty().add(prvPenalty);
				prvPenaltyBal = recovery.getPenalty().add(prvPenaltyBal);

				if (FinanceConstants.PENALTYTYPE_PERC_ON_DUEDAYS.equals(odDetails.getODChargeType())) {
					recovery.setPenaltyBal(recovery.getPenalty().add(recovery.getPenaltyBal()));
					recovery.setPenaltyPaid(BigDecimal.ZERO);
					recovery.setWaivedAmt(BigDecimal.ZERO);
				} else {
					recovery.setPenaltyBal(recovery.getPenalty().subtract(recovery.getPenaltyPaid()));
				}

				recovery.setRcdCanDel(true);

				Date curBussDate = DateUtility.getAppValueDate();
				if (!isEnqPurpose && odDetails.getFinODSchdDate().compareTo(curBussDate) <= 0) {
					//Recovery Record Saving : Check to Add the Record/Not with "ZERO" penalty balance Amount while Recovery calculation
					if (isRecordSave) {
						if (recovery.getPenaltyBal().compareTo(BigDecimal.ZERO) > 0
								&& recovery.getFinCurODAmt().compareTo(BigDecimal.ZERO) > 0) {
							getRecoveryDAO().save(recovery, "");
						}
					} else {
						getRecoveryDAO().update(recovery, "");
					}

					//Overdue Details Updation for Totals
					FinODDetails detail = new FinODDetails();
					detail.setFinReference(odDetails.getFinReference());
					detail.setFinODSchdDate(odDetails.getFinODSchdDate());
					detail.setFinODFor(odDetails.getFinODFor());
					detail.setTotPenaltyAmt(prvPenalty);
					detail.setTotPenaltyPaid(BigDecimal.ZERO);
					detail.setTotPenaltyBal(prvPenaltyBal);
					detail.setTotWaived(BigDecimal.ZERO);

					if (resetTotals) {
						getFinODDetailsDAO().resetTotals(detail);
					} else {
						getFinODDetailsDAO().updateTotals(detail);
					}
				}
			}
		}

		logger.debug("Leaving");
		return recovery;
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
	private FinODDetails prepareOverDueData(FinODDetails odDetails, Date valueDate, FinRepayQueue queue,
			boolean isAfterRecovery) {
		logger.debug("Entering");

		FinODDetails details = null;
		boolean isSave = false;
		if (odDetails != null) {
			details = odDetails;
		} else {

			isSave = true;
			details = new FinODDetails();
			details.setFinReference(queue.getFinReference());
			details.setFinODSchdDate(queue.getRpyDate());
			details.setFinODFor(queue.getFinRpyFor());
			details.setFinBranch(queue.getBranch());
			details.setFinType(queue.getFinType());
			details.setCustID(queue.getCustomerID());

			//Prepare Overdue Penalty rate Details & set to Finance Overdue Details
			FinODPenaltyRate penaltyRate = getFinODPenaltyRateDAO().getFinODPenaltyRateByRef(queue.getFinReference(),
					"");

			if (penaltyRate != null) {
				details.setApplyODPenalty(penaltyRate.isApplyODPenalty());
				details.setODIncGrcDays(penaltyRate.isODIncGrcDays());
				details.setODChargeType(penaltyRate.getODChargeType());
				details.setODChargeAmtOrPerc(penaltyRate.getODChargeAmtOrPerc());
				details.setODChargeCalOn(penaltyRate.getODChargeCalOn());
				details.setODGraceDays(penaltyRate.getODGraceDays());
				details.setODAllowWaiver(penaltyRate.isODAllowWaiver());
				details.setODMaxWaiverPerc(penaltyRate.getODMaxWaiverPerc());
			} else {
				details.setApplyODPenalty(false);
				details.setODIncGrcDays(false);
				details.setODChargeType("");
				details.setODChargeAmtOrPerc(BigDecimal.ZERO);
				details.setODChargeCalOn("");
				details.setODGraceDays(0);
				details.setODAllowWaiver(false);
				details.setODMaxWaiverPerc(BigDecimal.ZERO);
			}

		}

		details.setFinCurODAmt(queue.getSchdPft().add(queue.getSchdPri()).subtract(queue.getSchdPftPaid())
				.subtract(queue.getSchdPriPaid()));
		details.setFinCurODPri(queue.getSchdPri().subtract(queue.getSchdPriPaid()));
		details.setFinCurODPft(queue.getSchdPft().subtract(queue.getSchdPftPaid()));

		if (isSave) {
			details.setFinMaxODAmt(details.getFinCurODAmt());
			details.setFinMaxODPri(details.getFinCurODPri());
			details.setFinMaxODPft(details.getFinCurODPft());
			details.setTotPenaltyAmt(BigDecimal.ZERO);
			details.setTotWaived(BigDecimal.ZERO);
			details.setTotPenaltyPaid(BigDecimal.ZERO);
			details.setTotPenaltyBal(BigDecimal.ZERO);
		}

		if (details.getTotPenaltyPaid().compareTo(BigDecimal.ZERO) == 0) {
			details.setGraceDays(details.getODGraceDays());
			details.setIncGraceDays(details.isODIncGrcDays());
		}

		details.setFinODTillDate(valueDate);
		details.setFinCurODDays(DateUtility.getDaysBetween(valueDate, details.getFinODSchdDate()));
		if (isAfterRecovery) {
			details.setFinCurODDays(details.getFinCurODDays() + 1);
		}
		//TODO ###124902 - New field to be included for future use which stores the last payment date. This needs to be worked.
		details.setFinLMdfDate(DateUtility.getAppDate());

		logger.debug("Leaving");
		return details;
	}

	/**
	 * Method for get the Percentage of given value
	 * 
	 * @param odCalculatedBalance
	 * @param odPercent
	 * @return
	 */
	private BigDecimal getDayPercValue(BigDecimal odCalculatedBalance, BigDecimal odPercent, Date odEffectiveDate,
			Date dateValueDate, boolean isAfterRecovery, String profitDayBasis) {

		if (isAfterRecovery) {
			dateValueDate = DateUtility.addDays(dateValueDate, 1);
		}

		BigDecimal value = ((odCalculatedBalance.multiply(odPercent)).multiply(CalculationUtil.getInterestDays(
				odEffectiveDate, dateValueDate, profitDayBasis))).divide(new BigDecimal(10000), RoundingMode.HALF_DOWN);

		return value.setScale(0, RoundingMode.HALF_DOWN);
	}

	/**
	 * Method for get the Percentage of given value
	 * 
	 * @param odCalculatedBalance
	 * @param odPercent
	 * @return
	 */
	private BigDecimal getPercentageValue(BigDecimal odCalculatedBalance, BigDecimal odPercent) {
		return (odCalculatedBalance.multiply(odPercent)).divide(new BigDecimal(10000), RoundingMode.HALF_DOWN);
	}

	/**
	 * Method for Posting OverDue Recoveries .
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
	public List<Object> oDRPostingProcess(FinanceMain financeMain, Date dateValueDate, Date schdDate, String finODFor,
			Date movementDate, BigDecimal penalty, BigDecimal prvPenaltyPaid, BigDecimal waiverAmt, String chargeType,
			long linkedTranId, String finDivision) throws InterfaceException, IllegalAccessException,
			InvocationTargetException {

		logger.debug("Entering" + "', financeMain='" + financeMain + "', dateValueDate='" + dateValueDate
				+ "', SchdDate='" + schdDate + "', finODFor='" + finODFor + "', movementDate='" + movementDate
				+ "', penalty='" + penalty + "', prvPenaltyPaid='" + prvPenaltyPaid + "', waiverAmt='" + waiverAmt
				+ "', chargeType='" + chargeType + "', linkedTranId='" + linkedTranId + "', finDivision='"
				+ finDivision + "'");
		boolean isPostingSuccess = true;
		String errorCode = null;
		BigDecimal paidAmount = BigDecimal.ZERO;

		String finReference = financeMain.getFinReference();
		//Calculate Pending Penalty Balance
		BigDecimal pendingPenalty = penalty.subtract(prvPenaltyPaid);

		if (pendingPenalty.compareTo(BigDecimal.ZERO) > 0) {

			//Get Finance Details From DB
			//financeMain = getFinanceMainDAO().getFinanceMainForBatch(finReference);
			boolean isPayNow = false;

			// Check Available Funding Account Balance
			//### 06-11-2015 Start - PSD Ticket ID 123992
			//As per the new approach in case of failure from equation., application should not stop the EOD process and should proceed to the next Repayments 
			IAccounts iAccount = null;
			try {
				iAccount = getAccountInterfaceService().fetchAccountAvailableBal(financeMain.getRepayAccountId());
			} catch (InterfaceException e) {
				logger.error("Exception: ", e);
				List<Object> returnList = new ArrayList<Object>(2);
				returnList.add(isPostingSuccess);
				returnList.add(linkedTranId);
				returnList.add(e.getErrorMessage());
				returnList.add(paidAmount.subtract(BigDecimal.ONE));
				return returnList;
			}
			//### 06-11-2015 End
			BigDecimal penaltyPaidNow = BigDecimal.ZERO;
			boolean isPaidClear = false;
			boolean accFound = false;

			// Account Type Check
			if ("TREASURY".equals(StringUtils.trimToEmpty(finDivision))) {
				String acType = SysParamUtil.getValueAsString("ALWFULLPAY_TSR_ACTYPE");

				String[] acTypeList = acType.split(",");
				for (int i = 0; i < acTypeList.length; i++) {
					if (iAccount.getAcType().equals(acTypeList[i].trim())) {
						accFound = true;
						break;
					}
				}
			} else {
				String acType = SysParamUtil.getValueAsString("ALWFULLPAY_NONTSR_ACTYPE");

				String[] acTypeList = acType.split(",");
				for (int i = 0; i < acTypeList.length; i++) {/*
															 * if(iAccount.getAcType().equals(acTypeList[i].trim())){
															 * accFound = true; break; }
															 */
				}
			}

			// Set Requested Repayment Amount as RepayAmount Balance
			if (iAccount.getAcAvailableBal().compareTo(pendingPenalty) >= 0) {
				penaltyPaidNow = pendingPenalty;
				isPayNow = true;
				isPaidClear = true;
			} else if (accFound) {

				penaltyPaidNow = pendingPenalty;
				isPayNow = true;
				isPaidClear = true;

			} else {
				if (iAccount.getAcAvailableBal().compareTo(BigDecimal.ZERO) > 0) {
					penaltyPaidNow = iAccount.getAcAvailableBal();
					isPayNow = true;
				}
			}
			paidAmount = penaltyPaidNow;
			if (isPayNow) {
				// AmountCodes Preparation
				// EOD Repayments should pass the value date as schedule for which
				// Repayment is processing
				aeEvent = new AEEvent();
				AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
				aeEvent.setFinReference(financeMain.getFinReference());
				amountCodes.setPenalty(penaltyPaidNow);
				amountCodes.setWaiver(waiverAmt);
				aeEvent.setAccountingEvent(AccountEventConstants.ACCEVENT_LATEPAY);
				aeEvent.setValueDate(dateValueDate);
				aeEvent.setSchdDate(schdDate);
				aeEvent.setDataMap(amountCodes.getDeclaredFieldValues());

				/*String phase = SysParamUtil.getValueAsString("PHASE");
				boolean isEODProcess = false;
				if (!"DAY".equals(phase)) {
					isEODProcess = true;
				}*/

				Date dateAppDate = DateUtility.getAppDate();
				aeEvent.setPostDate(dateAppDate);
				aeEvent.setValueDate(dateValueDate);
				try {
					aeEvent = getPostingsPreparationUtil().processPostingDetails(aeEvent);
				} catch (AccountNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}


				isPostingSuccess = aeEvent.isPostingSucess();
				linkedTranId = aeEvent.getLinkedTranId();
				errorCode = aeEvent.getErrorMessage();

				//Overdue Details Updation for Paid Penalty
				if (isPostingSuccess) {

					//Overdue Recovery Details Updation for Paid Amounts & Record Deletion Status
					doUpdateRecoveryData(finReference, schdDate, finODFor, movementDate, chargeType, penaltyPaidNow,
							waiverAmt, isPaidClear);

					//Overdue Details Updation for Totals
					FinODDetails detail = new FinODDetails();
					detail.setFinReference(finReference);
					detail.setFinODSchdDate(schdDate);
					detail.setFinODFor(finODFor);
					detail.setTotPenaltyAmt(BigDecimal.ZERO);
					detail.setTotPenaltyPaid(penaltyPaidNow);
					detail.setTotPenaltyBal(penaltyPaidNow.negate());
					detail.setTotWaived(waiverAmt);
					getFinODDetailsDAO().updateTotals(detail);

				}
			}
		}

		List<Object> returnList = new ArrayList<Object>(2);
		returnList.add(isPostingSuccess);
		returnList.add(linkedTranId);
		returnList.add(errorCode);
		returnList.add(paidAmount);

		logger.debug("Leaving");
		return returnList;
	}

	/**
	 * Method for Overdue Recovery Penalty Detail Updation
	 * 
	 * @param set
	 * @param isPaidClear
	 * @param penaltyPaid
	 * @param waiverPaid
	 * @param dbUpdate
	 * @return
	 */
	private OverdueChargeRecovery doUpdateRecoveryData(String finReference, Date schdDate, String finODFor,
			Date movementDate, String chargeType, BigDecimal penaltyPaid, BigDecimal waiverPaid, boolean isPaidClear) {
		logger.debug("Entering");

		OverdueChargeRecovery recovery = new OverdueChargeRecovery();
		recovery.setFinReference(finReference);
		recovery.setFinODSchdDate(schdDate);
		recovery.setFinODFor(finODFor);
		recovery.setMovementDate(movementDate);
		recovery.setPenaltyPaid(penaltyPaid);
		recovery.setPenaltyBal(penaltyPaid);
		recovery.setWaivedAmt(waiverPaid);
		if ("D".equals(chargeType)) {
			recovery.setRcdCanDel(false);
		} else {
			recovery.setRcdCanDel(!isPaidClear);
		}
		getRecoveryDAO().updatePenaltyPaid(recovery, "");

		logger.debug("Leaving");
		return recovery;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public FinanceMainDAO getFinanceMainDAO() {
		return financeMainDAO;
	}

	public void setFinODDetailsDAO(FinODDetailsDAO finODDetailsDAO) {
		this.finODDetailsDAO = finODDetailsDAO;
	}

	public FinODDetailsDAO getFinODDetailsDAO() {
		return finODDetailsDAO;
	}

	public OverdueChargeRecoveryDAO getRecoveryDAO() {
		return recoveryDAO;
	}

	public void setRecoveryDAO(OverdueChargeRecoveryDAO recoveryDAO) {
		this.recoveryDAO = recoveryDAO;
	}

	public FinODPenaltyRateDAO getFinODPenaltyRateDAO() {
		return finODPenaltyRateDAO;
	}

	public void setFinODPenaltyRateDAO(FinODPenaltyRateDAO finODPenaltyRateDAO) {
		this.finODPenaltyRateDAO = finODPenaltyRateDAO;
	}

	public void setAccountInterfaceService(AccountInterfaceService accountInterfaceService) {
		this.accountInterfaceService = accountInterfaceService;
	}

	public AccountInterfaceService getAccountInterfaceService() {
		return accountInterfaceService;
	}

	public PostingsPreparationUtil getPostingsPreparationUtil() {
		return postingsPreparationUtil;
	}

	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}

}
