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
 * FileName : LatePayMarkingService.java *
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
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.dao.finance.FinODPenaltyRateDAO;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinODPenaltyRate;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.util.FinanceConstants;

public class LatePayMarkingService extends ServiceHelper {

	private static final long		serialVersionUID	= 6161809223570900644L;
	private static Logger			logger				= Logger.getLogger(LatePayMarkingService.class);
	private FinODPenaltyRateDAO		finODPenaltyRateDAO;
	private LatePayPenaltyService	latePayPenaltyService;
	private LatePayInterestService	latePayInterestService;

	/**
	 * Default constructor
	 */
	public LatePayMarkingService() {
		super();
	}

	public List<FinODDetails> calPDOnBackDatePayment(FinanceMain finmain, List<FinODDetails> finODDetails,
			Date valueDate, List<FinanceScheduleDetail> finScheduleDetails, List<FinanceRepayments> repayments) {

		//get penalty rates one time
		FinODPenaltyRate penaltyRate = finODPenaltyRateDAO.getFinODPenaltyRateByRef(finmain.getFinReference(), "");

		for (FinODDetails fod : finODDetails) {
			FinanceScheduleDetail curSchd = getODSchedule(finScheduleDetails, fod);
			
			if(curSchd!=null){
				latePayMarking(finmain, fod, penaltyRate, finScheduleDetails, repayments, curSchd, valueDate);
			}else{
				fod.setFinODTillDate(valueDate);
				fod.setFinCurODPri(BigDecimal.ZERO);
				fod.setFinCurODPft(BigDecimal.ZERO);
				fod.setFinCurODAmt(BigDecimal.ZERO);
				fod.setFinCurODDays(0);
				fod.setFinLMdfDate(valueDate);
				fod.setTotPenaltyAmt(BigDecimal.ZERO);
				fod.setTotPenaltyBal(fod.getTotPenaltyAmt().subtract(fod.getTotPenaltyPaid()).subtract(fod.getTotWaived()));
			}
		}
		return finODDetails;

	}
	
	public List<FinODDetails> calPDOnPayment(FinanceMain finmain, List<FinODDetails> finODDetails,
			Date valueDate, List<FinanceScheduleDetail> finScheduleDetails, List<FinanceRepayments> repayments) {
		
		//get penalty rates one time
		FinODPenaltyRate penaltyRate = finODPenaltyRateDAO.getFinODPenaltyRateByRef(finmain.getFinReference(), "");
		
		for (FinODDetails fod : finODDetails) {
			FinanceScheduleDetail curSchd = getODSchedule(finScheduleDetails, fod);
			
			if(curSchd!=null){
				
				boolean isAmountDue = false;
				//Paid Principal OR Paid Interest Less than scheduled amounts 
				if (curSchd.getSchdPriPaid().compareTo(curSchd.getPrincipalSchd()) < 0
						|| curSchd.getSchdPftPaid().compareTo(curSchd.getProfitSchd()) < 0) {
					isAmountDue = true;
				} else {
					//Islamic Implementation
					if (ImplementationConstants.IMPLEMENTATION_ISLAMIC) {
						//Paid Supplementary rent OR Paid Increase Cost less than scheduled amounts 
						if (curSchd.getSuplRentPaid().compareTo(curSchd.getSuplRent()) < 0
								|| curSchd.getIncrCostPaid().compareTo(curSchd.getIncrCost()) < 0) {
							isAmountDue = true;
						}
					}
				}

				if (isAmountDue) {
					latePayMarking(finmain, fod, penaltyRate, finScheduleDetails, repayments, curSchd, valueDate);
				}
			}
		}
		return finODDetails;
		
	}

	private FinanceScheduleDetail getODSchedule(List<FinanceScheduleDetail> finScheduleDetails, FinODDetails fod) {
		for (FinanceScheduleDetail financeScheduleDetail : finScheduleDetails) {
			if (fod.getFinODSchdDate().compareTo(financeScheduleDetail.getSchDate()) == 0) {
				return financeScheduleDetail;
			}
		}
		return null;

	}

	/**
	 * @param connection
	 * @param custId
	 * @param date
	 * @throws Exception
	 */
	public CustEODEvent processLatePayMarking(CustEODEvent custEODEvent) throws Exception {
		logger.debug(" Entering ");
		List<FinEODEvent> finEODEvents = custEODEvent.getFinEODEvents();
		Date valueDate = custEODEvent.getEodValueDate();

		for (FinEODEvent finEODEvent : finEODEvents) {
			if (finEODEvent.getIdxPD() <= 0) {
				continue;
			}
			finEODEvent = findLatePay(finEODEvent, valueDate);
		}

		logger.debug(" Leaving ");
		return custEODEvent;
	}

	private FinEODEvent findLatePay(FinEODEvent finEODEvent, Date valueDate) throws Exception {

		FinanceMain finMain = finEODEvent.getFinanceMain();
		List<FinanceScheduleDetail> finSchdDetails = finEODEvent.getFinanceScheduleDetails();

		//Get details first time from DB
		String finRef = finMain.getFinReference();
		Date finStartDate = finMain.getFinStartDate();
		finEODEvent.setFinODDetails(getFinODDetailsDAO().getFinODDByFinRef(finRef, finStartDate));
		FinODPenaltyRate penaltyRate = null;

		for (int i = 0; i < finSchdDetails.size(); i++) {
			FinanceScheduleDetail curSchd = finSchdDetails.get(i);

			//Include Today in Late payment Calculation or NOT?
			if (curSchd.getSchDate().compareTo(valueDate) > 0) {
				break;
			}

			boolean isAmountDue = false;
			//Paid Principal OR Paid Interest Less than scheduled amounts 
			if (curSchd.getSchdPriPaid().compareTo(curSchd.getPrincipalSchd()) < 0
					|| curSchd.getSchdPftPaid().compareTo(curSchd.getProfitSchd()) < 0) {
				isAmountDue = true;
			} else {
				//Islamic Implementation
				if (ImplementationConstants.IMPLEMENTATION_ISLAMIC) {
					//Paid Supplementary rent OR Paid Increase Cost less than scheduled amounts 
					if (curSchd.getSuplRentPaid().compareTo(curSchd.getSuplRent()) < 0
							|| curSchd.getIncrCostPaid().compareTo(curSchd.getIncrCost()) < 0) {
						isAmountDue = true;
					}
				}
			}

			if (isAmountDue) {
				//get penalty rates one time
				if (penaltyRate == null) {
					penaltyRate = finODPenaltyRateDAO.getFinODPenaltyRateByRef(finRef, "");
				}

				FinODDetails fod = findExisingOD(finEODEvent.getFinODDetails(), curSchd);
				//if created new add it to finance
				if (fod == null) {
					fod = createODDetails(curSchd, finMain, penaltyRate, valueDate);
					finEODEvent.getFinODDetails().add(fod);
				}
				latePayMarking(finMain, fod, penaltyRate, finSchdDetails, null, curSchd, valueDate);
			}
		}

		updateFinPftDetails(finEODEvent.getFinProfitDetail(), finEODEvent.getFinODDetails(), valueDate);
		return finEODEvent;
	}

	public FinODDetails findExisingOD(List<FinODDetails> finODDetails, FinanceScheduleDetail curSchd) throws Exception {
		logger.debug("Entering");

		for (FinODDetails fod : finODDetails) {
			if (fod.getFinODSchdDate().compareTo(curSchd.getSchDate()) == 0) {
				return fod;
			}
		}

		logger.debug("Leaving");
		return null;
	}

	private void latePayMarking(FinanceMain finMain, FinODDetails fod, FinODPenaltyRate penaltyRate,
			List<FinanceScheduleDetail> finScheduleDetails, List<FinanceRepayments> repayments,
			FinanceScheduleDetail curSchd, Date valueDate) {
		logger.debug("Entering");

		fod.setFinODTillDate(valueDate);
		fod.setFinCurODPri(curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid()));
		fod.setFinCurODPft(curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid()));
		fod.setFinCurODAmt(fod.getFinCurODPft().add(fod.getFinCurODPri()));
		if (fod.getFinCurODAmt().compareTo(BigDecimal.ZERO)>0) {
			fod.setFinCurODDays(DateUtility.getDaysBetween(fod.getFinODSchdDate(), valueDate));
		}else{
			fod.setFinCurODDays(0);
		}
		fod.setFinLMdfDate(valueDate);

		latePayPenaltyService.computeLPP(fod, valueDate, finMain.getProfitDaysBasis(), finScheduleDetails, repayments,
				finMain.getCalRoundingMode(), finMain.getRoundingTarget());

		String lpiMethod = finMain.getPastduePftCalMthd();

		if (!StringUtils.equals(lpiMethod, CalculationConstants.PDPFTCAL_NOTAPP)) {
			latePayInterestService.computeLPI(fod, valueDate, finMain.getProfitDaysBasis(), finScheduleDetails,
					repayments, finMain.getPastduePftMargin(), finMain.getCalRoundingMode(),
					finMain.getRoundingTarget());
		}
		logger.debug("Leaving");
	}

	private void updateFinPftDetails(FinanceProfitDetail pftDetail, List<FinODDetails> finODDetails, Date valueDate)
			throws Exception {

		pftDetail.setODPrincipal(BigDecimal.ZERO);
		pftDetail.setODProfit(BigDecimal.ZERO);
		pftDetail.setPenaltyPaid(BigDecimal.ZERO);
		pftDetail.setPenaltyDue(BigDecimal.ZERO);
		pftDetail.setPenaltyWaived(BigDecimal.ZERO);
		pftDetail.setPrvODDate(valueDate);
		pftDetail.setNOODInst(0);

		pftDetail.setTotPftOnPD(BigDecimal.ZERO);
		pftDetail.setTotPftOnPDDue(BigDecimal.ZERO);
		pftDetail.setTotPftOnPDPaid(BigDecimal.ZERO);
		pftDetail.setTotPftOnPDWaived(BigDecimal.ZERO);

		for (FinODDetails fod : finODDetails) {
			pftDetail.setODPrincipal(pftDetail.getODPrincipal().add(fod.getFinCurODPri()));
			pftDetail.setODProfit(pftDetail.getODProfit().add(fod.getFinCurODPft()));

			pftDetail.setPenaltyPaid(pftDetail.getPenaltyPaid().add(fod.getTotPenaltyPaid()));
			pftDetail.setPenaltyDue(pftDetail.getPenaltyDue().add(fod.getTotPenaltyBal()));
			pftDetail.setPenaltyWaived(pftDetail.getPenaltyWaived().add(fod.getTotWaived()));
			pftDetail.setTotPftOnPD(pftDetail.getTotPftOnPD().add(fod.getLPIAmt()));
			pftDetail.setTotPftOnPDDue(pftDetail.getTotPftOnPDDue().add(fod.getLPIBal()));
			pftDetail.setTotPftOnPDPaid(pftDetail.getTotPftOnPDPaid().add(fod.getLPIPaid()));
			pftDetail.setTotPftOnPDWaived(pftDetail.getTotPftOnPDWaived().add(fod.getLPIWaived()));

			if (pftDetail.getFirstODDate() == null
					|| pftDetail.getFirstODDate().compareTo(pftDetail.getFinStartDate()) == 0) {
				pftDetail.setFirstODDate(fod.getFinODSchdDate());
			}

			//There is chance OD dates might not be in ascending order so take the least date
			if (fod.getFinODSchdDate().compareTo(pftDetail.getPrvODDate()) <= 0) {
				pftDetail.setPrvODDate(fod.getFinODSchdDate());
			}

			pftDetail.setNOODInst(pftDetail.getNOODInst() + 1);
		}

		pftDetail.setCurODDays(DateUtility.getDaysBetween(valueDate, pftDetail.getPrvODDate()));

		if (pftDetail.getCurODDays() > pftDetail.getMaxODDays()) {
			pftDetail.setMaxODDays(pftDetail.getCurODDays());
		}

	}

	public CustEODEvent processCustomerStatus(CustEODEvent custEODEvent) {
		logger.debug(" Entering ");
		Date valueDate = custEODEvent.getEodValueDate();
		String newBucketCode = FinanceConstants.FINSTSRSN_SYSTEM;
		int maxBuckets = 0;

		Customer customer = custEODEvent.getCustomer();
		String currentBucketCode = StringUtils.trimToEmpty(customer.getCustSts());

		List<FinEODEvent> finEODEvents = custEODEvent.getFinEODEvents();

		for (int i = 0; i < finEODEvents.size(); i++) {
			FinEODEvent finEODEvent = finEODEvents.get(i);
			if (finEODEvent.getFinanceMain().getDueBucket() > maxBuckets) {
				maxBuckets = finEODEvent.getFinanceMain().getDueBucket();
				newBucketCode = finEODEvent.getFinanceMain().getFinStatus();
			}
		}

		if (!StringUtils.equals(newBucketCode, currentBucketCode)) {
			custEODEvent.setUpdCustomer(true);
			custEODEvent.getCustomer().setCustSts(newBucketCode);
			custEODEvent.getCustomer().setCustStsChgDate(valueDate);
		}

		logger.debug(" Leaving ");

		return custEODEvent;

	}

	private FinODDetails createODDetails(FinanceScheduleDetail curSchd, FinanceMain finMain,
			FinODPenaltyRate penaltyRate, Date valueDate) {
		logger.debug(" Entering ");

		FinODDetails finODDetail = new FinODDetails();
		String finReference = finMain.getFinReference();

		finODDetail.setFinReference(finReference);
		finODDetail.setFinODSchdDate(curSchd.getSchDate());
		finODDetail.setFinODFor(FinanceConstants.SCH_TYPE_SCHEDULE);
		finODDetail.setFinBranch(finMain.getFinBranch());
		finODDetail.setFinType(finMain.getFinType());
		finODDetail.setCustID(finMain.getCustID());
		finODDetail.setFinODTillDate(valueDate);

		finODDetail.setFinCurODPri(curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid()));
		finODDetail.setFinCurODPft(curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid()));
		finODDetail.setFinCurODAmt(finODDetail.getFinCurODPft().add(finODDetail.getFinCurODPri()));
		finODDetail.setFinMaxODPri(finODDetail.getFinCurODPri());
		finODDetail.setFinMaxODPft(finODDetail.getFinCurODPft());
		finODDetail.setFinMaxODAmt(finODDetail.getFinMaxODPft().add(finODDetail.getFinMaxODPri()));

		finODDetail.setFinCurODDays(DateUtility.getDaysBetween(finODDetail.getFinODSchdDate(), valueDate));
		finODDetail.setFinLMdfDate(valueDate);
		finODDetail.setApplyODPenalty(penaltyRate.isApplyODPenalty());
		finODDetail.setODIncGrcDays(penaltyRate.isODIncGrcDays());
		finODDetail.setODChargeType(penaltyRate.getODChargeType());
		finODDetail.setODGraceDays(penaltyRate.getODGraceDays());
		finODDetail.setODChargeCalOn(penaltyRate.getODChargeCalOn());
		finODDetail.setODChargeAmtOrPerc(getDecimal(penaltyRate.getODChargeAmtOrPerc()));
		finODDetail.setODAllowWaiver(penaltyRate.isODAllowWaiver());
		finODDetail.setODMaxWaiverPerc(penaltyRate.getODMaxWaiverPerc());

		return finODDetail;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setFinODPenaltyRateDAO(FinODPenaltyRateDAO finODPenaltyRateDAO) {
		this.finODPenaltyRateDAO = finODPenaltyRateDAO;
	}

	public void setLatePayPenaltyService(LatePayPenaltyService latePayPenaltyService) {
		this.latePayPenaltyService = latePayPenaltyService;
	}

	public void setLatePayInterestService(LatePayInterestService latePayInterestService) {
		this.latePayInterestService = latePayInterestService;
	}

}