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
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.FinODPenaltyRateDAO;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.applicationmaster.DPDBucketConfiguration;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinODPenaltyRate;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.SMTParameterConstants;

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
			Date valueDate, String pftDayBasis, List<FinanceScheduleDetail> finScheduleDetails,
			List<FinanceRepayments> repayments, String roundingMode, int roundingTarget) {
		for (FinODDetails fod : finODDetails) {
			latePayPenaltyService.computeLPP(fod, valueDate, pftDayBasis, finScheduleDetails, repayments, roundingMode,
					roundingTarget);

			String lpiMethod = finmain.getPastduePftCalMthd();
			if (!StringUtils.equals(lpiMethod, CalculationConstants.PDPFTCAL_NOTAPP)) {
				latePayInterestService.computeLPI(fod, valueDate, pftDayBasis, finScheduleDetails, repayments,
						finmain.getPastduePftMargin(), roundingMode, roundingTarget);
			}

		}
		return finODDetails;

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

			FinanceProfitDetail pftDetail = finEODEvent.getFinProfitDetail();
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

			finEODEvent = findLatePay(finEODEvent, valueDate);
		}

		logger.debug(" Leaving ");
		return custEODEvent;
	}

	private FinEODEvent findLatePay(FinEODEvent finEODEvent, Date valueDate) throws Exception {

		List<FinanceScheduleDetail> finSchdDetails = finEODEvent.getFinanceScheduleDetails();
		int idxPD = finEODEvent.getIdxPD();

		//Get details first time from DB
		String finRef = finEODEvent.getFinanceMain().getFinReference();
		Date finStartDate = finEODEvent.getFinanceMain().getFinStartDate();
		finEODEvent.setFinODDetails(getFinODDetailsDAO().getFinODDByFinRef(finRef, finStartDate));

		for (int i = 0; i < finSchdDetails.size(); i++) {
			FinanceScheduleDetail curSchd = finSchdDetails.get(i);

			//Include Today in Late payment Calculation or NOT?
			if (curSchd.getSchDate().compareTo(valueDate) > 0) {
				break;
			}

			if (i < idxPD) {
				FinODDetails fod = new FinODDetails();
				for (int j = 0; j < finEODEvent.getFinODDetails().size(); j++) {
					fod = finEODEvent.getFinODDetails().get(j);
					if (fod.getFinODSchdDate().compareTo(curSchd.getSchDate()) >= 0) {
						break;
					}

					updateFinPftDetails(finEODEvent, fod, valueDate, false);
				}
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
				latePayMarking(finEODEvent, curSchd, valueDate);
			}

		}

		return finEODEvent;
	}

	private void latePayMarking(FinEODEvent finEODEvent, FinanceScheduleDetail curSchd, Date valueDate)
			throws Exception {
		logger.debug("Entering");

		List<FinODDetails> finODDetails = finEODEvent.getFinODDetails();
		FinanceMain finMain = finEODEvent.getFinanceMain();
		String finReference = finMain.getFinReference();
		boolean isODRecordFound = false;

		FinODDetails fod = new FinODDetails();

		for (int i = 0; i < finODDetails.size(); i++) {
			fod = finODDetails.get(i);

			//OD Schedule date before required schedule date
			if (fod.getFinODSchdDate().compareTo(curSchd.getSchDate()) < 0) {
				continue;
			}

			//OD Schedule date same as required schedule date
			if (fod.getFinODSchdDate().compareTo(curSchd.getSchDate()) == 0) {
				fod.setFinODTillDate(valueDate);
				fod.setFinCurODPri(curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid()));
				fod.setFinCurODPft(curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid()));
				fod.setFinCurODAmt(fod.getFinCurODPft().add(fod.getFinCurODPri()));
				fod.setFinCurODDays(DateUtility.getDaysBetween(fod.getFinODSchdDate(), valueDate));
				fod.setFinLMdfDate(valueDate);
				finEODEvent.getFinODDetails().set(i, fod);
				isODRecordFound = true;
			}
			break;
		}

		//OD Details not found. Create it now
		if (!isODRecordFound) {
			FinODPenaltyRate penaltyRate = finEODEvent.getPenaltyrate();

			//Load One time and keep it for finance
			if (penaltyRate == null) {
				penaltyRate = finODPenaltyRateDAO.getFinODPenaltyRateByRef(finReference, "");
				finEODEvent.setPenaltyrate(penaltyRate);
			}

			fod = createODDetails(curSchd, finMain, penaltyRate, valueDate);
			finEODEvent.getFinODDetails().add(fod);
		}

		latePayPenaltyService.computeLPP(fod, valueDate, finMain.getProfitDaysBasis(),
				finEODEvent.getFinanceScheduleDetails(), null, finMain.getCalRoundingMode(),
				finMain.getRoundingTarget());

		String lpiMethod = finEODEvent.getFinanceMain().getPastduePftCalMthd();

		if (!StringUtils.equals(lpiMethod, CalculationConstants.PDPFTCAL_NOTAPP)) {
			latePayInterestService.computeLPI(fod, valueDate, finMain.getProfitDaysBasis(),
					finEODEvent.getFinanceScheduleDetails(), null, finMain.getPastduePftMargin(),
					finMain.getCalRoundingMode(), finMain.getRoundingTarget());
		}

		updateFinPftDetails(finEODEvent, fod, valueDate, true);

		logger.debug("Leaving");
	}

	private void updateFinPftDetails(FinEODEvent finEODEvent, FinODDetails fod, Date valueDate, boolean isODRecord)
			throws Exception {
		FinanceProfitDetail pftDetail = finEODEvent.getFinProfitDetail();
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

		if (!isODRecord) {
			return;
		}

		pftDetail.setCurODDays(DateUtility.getDaysBetween(valueDate, pftDetail.getPrvODDate()));

		if (pftDetail.getCurODDays() > pftDetail.getMaxODDays()) {
			pftDetail.setMaxODDays(pftDetail.getCurODDays());
		}

		pftDetail.setNOODInst(pftDetail.getNOODInst() + 1);

	}

	/**
	 * @param connection
	 * @param custId
	 * @param date
	 * @throws Exception
	 */
	public CustEODEvent processDPDBuketing(CustEODEvent custEODEvent) throws Exception {
		logger.debug(" Entering ");
		List<FinEODEvent> finEODEvents = custEODEvent.getFinEODEvents();
		Date valueDate = custEODEvent.getEodValueDate();

		for (FinEODEvent finEODEvent : finEODEvents) {
			boolean isFinStsChanged = updateDPDBuketing(finEODEvent.getFinProfitDetail(), valueDate,
					finEODEvent.getFinanceMain());

			if (isFinStsChanged) {
				finEODEvent.setUpdFinMain(true);
				finEODEvent.addToFinMianUpdate("FinStatus");
				finEODEvent.addToFinMianUpdate("DueBucket");
				finEODEvent.getFinProfitDetail().setFinStatus(finEODEvent.getFinanceMain().getFinStatus());
				finEODEvent.getFinProfitDetail().setDueBucket(finEODEvent.getFinanceMain().getDueBucket());
			}
		}

		logger.debug(" Leaving ");
		return custEODEvent;
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

	/**
	 * @param connection
	 * @param custId
	 * @param valueDate
	 * @throws Exception
	 */
	public boolean updateDPDBuketing(FinanceProfitDetail pftDetail, Date valueDate, FinanceMain financeMain) {

		int dueDays = pftDetail.getCurODDays();
		int newDueBucket = (new BigDecimal(dueDays).divide(new BigDecimal(30), 0, RoundingMode.UP)).intValue();
		int dueBucket = financeMain.getDueBucket();
		BigDecimal minDuePerc = BigDecimal.ZERO;

		String newFinStatus = FinanceConstants.FINSTSRSN_SYSTEM;
		String finStatus = StringUtils.trimToEmpty(financeMain.getFinStatus());
		String productCode = pftDetail.getFinCategory();

		BigDecimal duePercentage = BigDecimal.ZERO;

		//No current OD Days and No change in the Bucket Status and Number of Buckets
		if (pftDetail.getCurODDays() == 0) {
			if (StringUtils.equals(newFinStatus, finStatus) && dueBucket == newDueBucket) {
				return false;
			}
		}

		//No current OD Buckets and No change in the Bucket Status and Number of Buckets
		if (newDueBucket == 0) {
			if (StringUtils.equals(newFinStatus, finStatus) && dueBucket == newDueBucket) {
				return false;
			}
		}

		BigDecimal netSchdAmount = pftDetail.getTdSchdPri().add(pftDetail.getTdSchdPft());
		BigDecimal netDueAmount = netSchdAmount.subtract(pftDetail.getTdSchdPriPaid())
				.subtract(pftDetail.getTdSchdPftPaid()).subtract(pftDetail.getEmiInAdvanceBal())
				.subtract(pftDetail.getExcessAmtBal());

		if (netSchdAmount.compareTo(BigDecimal.ZERO) > 0) {
			duePercentage = (netDueAmount.divide(netSchdAmount, 0, RoundingMode.HALF_DOWN))
					.multiply(new BigDecimal(100));
		}

		//get ignore bucket configuration from SMT parameter
		Object object = SysParamUtil.getValue(SMTParameterConstants.IGNORING_BUCKET);
		if (object != null) {
			minDuePerc = (BigDecimal) object;
		}

		if (duePercentage.compareTo(minDuePerc) <= 0) {
			newDueBucket = 0;
		}

		//No change in the Bucket Status and Number of Buckets
		if (StringUtils.equals(newFinStatus, finStatus) && dueBucket == newDueBucket) {
			return false;
		}

		long bucketID = 0;
		List<DPDBucketConfiguration> list = getBucketConfigurations(productCode);
		sortBucketConfig(list);
		for (DPDBucketConfiguration dpdBucketConfiguration : list) {

			if (dpdBucketConfiguration.getDueDays() > newDueBucket) {
				break;
			}

			bucketID = dpdBucketConfiguration.getBucketID();
		}

		if (bucketID != 0) {
			newFinStatus = getBucket(bucketID);
		}

		if (StringUtils.equals(newFinStatus, finStatus) && dueBucket == newDueBucket) {
			return false;
		}

		financeMain.setFinStatus(newFinStatus);
		financeMain.setDueBucket(newDueBucket);

		return true;
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