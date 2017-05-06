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

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.FinODPenaltyRateDAO;
import com.pennant.backend.model.applicationmaster.DPDBucketConfiguration;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinODPenaltyRate;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;

public class LatePayMarkingService extends ServiceHelper {

	private static final long	serialVersionUID	= 6161809223570900644L;
	private static Logger		logger				= Logger.getLogger(LatePayMarkingService.class);
	private FinODPenaltyRateDAO	finODPenaltyRateDAO;

	/**
	 * Default constructor
	 */
	public LatePayMarkingService() {
		super();
	}

	/**
	 * @param connection
	 * @param custId
	 * @param date
	 * @throws Exception
	 */
	public CustEODEvent processLatePayMarking(CustEODEvent custEODEvent) throws Exception {
		List<FinEODEvent> finEODEvents = custEODEvent.getFinEODEvents();
		Date valueDate = custEODEvent.getEodValueDate();

		for (FinEODEvent finEODEvent : finEODEvents) {

			finEODEvent = findLatePay(finEODEvent, valueDate);

			if (finEODEvent.isOdFiance()) {
				updateFinPftDetails(finEODEvent, valueDate);
				finEODEvent.setUpdFinPft(true);
			}

		}

		return custEODEvent;
	}

	private FinEODEvent findLatePay(FinEODEvent finEODEvent, Date valueDate) throws Exception {

		List<FinanceScheduleDetail> finSchdDetails = finEODEvent.getFinanceScheduleDetails();

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
			}

			//Islamic Implementation
			if (ImplementationConstants.IMPLEMENTATION_ISLAMIC) {
				//Paid Supplementary rent OR Paid Increase Cost less than scheduled amounts 
				if (curSchd.getSuplRentPaid().compareTo(curSchd.getSuplRent()) < 0
						|| curSchd.getIncrCostPaid().compareTo(curSchd.getIncrCost()) < 0) {
					isAmountDue = true;
				}
			}

			if (isAmountDue) {
				finEODEvent = latePayMarking(finEODEvent, curSchd, valueDate);
				finEODEvent.setOdFiance(true);
			}

		}

		return finEODEvent;
	}

	private FinEODEvent latePayMarking(FinEODEvent finEODEvent, FinanceScheduleDetail curSchd, Date valueDate)
			throws Exception {
		logger.debug("Entering");

		String finReference = finEODEvent.getFinanceMain().getFinReference();
		List<FinODDetails> finODDetails = finEODEvent.getFinODDetails();

		//Get details first time from DB and keep it for later updation
		if (finODDetails == null || finODDetails.size() == 0) {
			finODDetails = getFinODDetailsDAO().getFinODDetailsByFinReference(finReference);
			if (finODDetails != null) {
				finEODEvent.setFinODDetails(finODDetails);
			}
		}

		finODDetails = finEODEvent.getFinODDetails();
		FinODDetails finODDetail = new FinODDetails();

		for (int i = 0; i < finODDetails.size(); i++) {
			finODDetail = finODDetails.get(i);

			//OD Schedule date before required schedule date
			if (finODDetail.getFinODSchdDate().compareTo(curSchd.getSchDate()) < 0) {
				continue;
			}

			//OD Schedule date same as required schedule date
			if (finODDetail.getFinODSchdDate().compareTo(curSchd.getSchDate()) == 0) {
				finODDetail.setFinODTillDate(valueDate);
				finODDetail.setFinCurODPri(curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid()));
				finODDetail.setFinCurODPft(curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid()));
				finODDetail.setFinCurODAmt(finODDetail.getFinCurODPft().add(finODDetail.getFinCurODPri()));
				finODDetail.setFinCurODDays(DateUtility.getDaysBetween(finODDetail.getFinODSchdDate(), valueDate));
				finODDetail.setFinLMdfDate(valueDate);
				finODDetail.setRcdAction(PennantConstants.RECORD_UPDATE);
				finEODEvent.getFinODDetails().set(i, finODDetail);
				return finEODEvent;
			}
		}

		finODDetail = createODDetails(curSchd, finEODEvent, valueDate);

		logger.debug("Leaving");
		return finEODEvent;
	}

	private void updateFinPftDetails(FinEODEvent finEODEvent, Date valueDate) throws Exception {

		List<FinODDetails> finODDetails = finEODEvent.getFinODDetails();
		FinanceProfitDetail pftDetail = finEODEvent.getFinProfitDetail();

		pftDetail.setODPrincipal(BigDecimal.ZERO);
		pftDetail.setODProfit(BigDecimal.ZERO);
		pftDetail.setPenaltyPaid(BigDecimal.ZERO);
		pftDetail.setPenaltyDue(BigDecimal.ZERO);
		pftDetail.setPenaltyWaived(BigDecimal.ZERO);
		pftDetail.setPrvODDate(valueDate);

		for (int i = 0; i < finODDetails.size(); i++) {
			FinODDetails fod = finODDetails.get(i);

			if (fod.getFinCurODPri().compareTo(BigDecimal.ZERO) == 0
					&& fod.getFinCurODPft().compareTo(BigDecimal.ZERO) == 0) {
				continue;
			}

			pftDetail.setODPrincipal(pftDetail.getODPrincipal().add(fod.getFinCurODPri()));
			pftDetail.setODProfit(pftDetail.getODProfit().add(fod.getFinCurODPft()));
			pftDetail.setPenaltyPaid(pftDetail.getPenaltyPaid().add(fod.getTotPenaltyPaid()));
			pftDetail.setPenaltyDue(pftDetail.getPenaltyDue().add(fod.getTotPenaltyBal()));
			pftDetail.setPenaltyWaived(pftDetail.getPenaltyWaived().add(fod.getTotWaived()));
			pftDetail.setPrvODDate(pftDetail.getFinStartDate());

			if (pftDetail.getFirstODDate() == null
					|| pftDetail.getFirstODDate().compareTo(pftDetail.getFinStartDate()) == 0) {
				pftDetail.setFirstODDate(fod.getFinODSchdDate());
			}

			//There is chance OD dates might not be in ascending order so take the least date
			if (pftDetail.getPrvODDate().compareTo(fod.getFinODSchdDate()) <= 0) {
				pftDetail.setPrvODDate(fod.getFinODSchdDate());
			}
		}

		pftDetail.setCurODDays(DateUtility.getDaysBetween(valueDate, pftDetail.getPrvODDate()));

	}

	/**
	 * @param connection
	 * @param custId
	 * @param date
	 * @throws Exception
	 */
	public CustEODEvent processDPDBuketing(CustEODEvent custEODEvent) throws Exception {
		List<FinEODEvent> finEODEvents = custEODEvent.getFinEODEvents();
		Date valueDate = custEODEvent.getEodValueDate();

		for (FinEODEvent finEODEvent : finEODEvents) {
			boolean isFinStsChanged = updateDPDBuketing(finEODEvent.getFinProfitDetail(), valueDate,
					finEODEvent.getFinanceMain());

			if (isFinStsChanged) {
				finEODEvent.setUpdFinMain(true);
				finEODEvent.setUpdFinPft(true);
				finEODEvent.getFinProfitDetail().setFinStatus(finEODEvent.getFinanceMain().getFinStatus());
				finEODEvent.getFinProfitDetail().setDueBucket(finEODEvent.getFinanceMain().getDueBucket());
			}
		}

		return custEODEvent;
	}

	public CustEODEvent processCustomerStatus(CustEODEvent custEODEvent) {

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
		int newDueBucket = 0;
		int dueBucket = financeMain.getDueBucket();
		BigDecimal minDuePerc = BigDecimal.ZERO;

		String newFinStatus = FinanceConstants.FINSTSRSN_SYSTEM;
		String finStatus = StringUtils.trimToEmpty(financeMain.getFinStatus());
		String productCode = pftDetail.getFinCategory();

		BigDecimal duePercentgae = BigDecimal.ZERO;

		//No current OD Days and No change in the Bucket Status and Number of Buckets
		if (pftDetail.getCurODDays() == 0) {
			if (StringUtils.equals(newFinStatus, finStatus) && dueBucket == newDueBucket) {
				return false;
			}
		}

		newDueBucket = (new BigDecimal(dueDays).divide(new BigDecimal(30), 0, RoundingMode.UP)).intValue();

		//No current OD Buckets and No change in the Bucket Status and Number of Buckets
		if (newDueBucket == 0) {
			if (StringUtils.equals(newFinStatus, finStatus) && dueBucket == newDueBucket) {
				return false;
			}
		} else {
			BigDecimal netSchdAmount = pftDetail.getTdSchdPri().add(pftDetail.getTdSchdPft());
			BigDecimal netDueAmount = netSchdAmount.subtract(pftDetail.getTdSchdPriPaid())
					.subtract(pftDetail.getTdSchdPftPaid()).subtract(pftDetail.getEmiInAdvanceBal())
					.subtract(pftDetail.getExcessAmtBal());

			if (netSchdAmount.compareTo(BigDecimal.ZERO) > 0) {
				duePercentgae = (netDueAmount.divide(netSchdAmount, 0, RoundingMode.HALF_DOWN))
						.multiply(new BigDecimal(100));
			}

			//get ignore bucket configuration from SMT parameter
			Object object = SysParamUtil.getValue("IGNORING_BUCKET");
			if (object != null) {
				minDuePerc = (BigDecimal) object;
			}

			if (duePercentgae.compareTo(minDuePerc) <= 0) {
				newDueBucket = 0;
			}

			//No change in the Bucket Status and Number of Buckets
			if (StringUtils.equals(newFinStatus, finStatus) && dueBucket == newDueBucket) {
				return false;
			}

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

	private FinODDetails createODDetails(FinanceScheduleDetail curSchd, FinEODEvent finEODEvent, Date valueDate) {
		logger.debug(" Entering ");

		FinODDetails finODDetail = new FinODDetails();
		FinODPenaltyRate penaltyRate = finEODEvent.getPenaltyrate();
		String finReference = finEODEvent.getFinanceMain().getFinReference();

		if (penaltyRate == null) {
			penaltyRate = finODPenaltyRateDAO.getFinODPenaltyRateByRef(finReference, "");
			finEODEvent.setPenaltyrate(penaltyRate);
		}

		finODDetail.setFinReference(finReference);
		finODDetail.setFinODSchdDate(curSchd.getSchDate());
		finODDetail.setFinODFor(FinanceConstants.SCH_TYPE_SCHEDULE);
		finODDetail.setFinBranch(finEODEvent.getFinanceMain().getFinBranch());
		finODDetail.setFinType(finEODEvent.getFinanceMain().getFinType());
		finODDetail.setCustID(finEODEvent.getFinanceMain().getCustID());
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
		finODDetail.setODChargeAmtOrPerc(penaltyRate.getODChargeAmtOrPerc());
		finODDetail.setODAllowWaiver(penaltyRate.isODAllowWaiver());
		finODDetail.setODMaxWaiverPerc(penaltyRate.getODMaxWaiverPerc());
		finODDetail.setRcdAction(PennantConstants.RECORD_INSERT);
		finEODEvent.getFinODDetails().add(finODDetail);

		return finODDetail;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setFinODPenaltyRateDAO(FinODPenaltyRateDAO finODPenaltyRateDAO) {
		this.finODPenaltyRateDAO = finODPenaltyRateDAO;
	}

}