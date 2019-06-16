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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.AEAmounts;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.customermasters.CustomerAddresDAO;
import com.pennant.backend.dao.feetype.FeeTypeDAO;
import com.pennant.backend.dao.finance.FinODAmzTaxDetailDAO;
import com.pennant.backend.dao.finance.FinODPenaltyRateDAO;
import com.pennant.backend.dao.finance.FinanceTaxDetailDAO;
import com.pennant.backend.dao.rulefactory.RuleDAO;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.feetype.FeeType;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinODAmzTaxDetail;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinODPenaltyRate;
import com.pennant.backend.model.finance.FinTaxDetails;
import com.pennant.backend.model.finance.FinTaxReceivable;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.service.finance.FinFeeDetailService;
import com.pennant.backend.service.finance.GSTInvoiceTxnService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.RuleReturnType;
import com.pennanttech.pennapps.core.resource.Literal;

public class LatePayMarkingService extends ServiceHelper {
	private static Logger logger = Logger.getLogger(LatePayMarkingService.class);
	private static final long serialVersionUID = 6161809223570900644L;

	private FinODPenaltyRateDAO finODPenaltyRateDAO;
	private LatePayPenaltyService latePayPenaltyService;
	private LatePayInterestService latePayInterestService;

	private RuleExecutionUtil ruleExecutionUtil;
	private FinFeeDetailService finFeeDetailService;
	private RuleDAO ruleDAO;
	private FinanceTaxDetailDAO financeTaxDetailDAO;
	private CustomerAddresDAO customerAddresDAO;
	private FeeTypeDAO feeTypeDAO;
	private FinODAmzTaxDetailDAO finODAmzTaxDetailDAO;

	//GST Invoice Report changes
	private GSTInvoiceTxnService gstInvoiceTxnService;

	/**
	 * Default constructor
	 */
	public LatePayMarkingService() {
		super();
	}

	public List<FinODDetails> calPDOnBackDatePayment(FinanceMain finmain, List<FinODDetails> finODDetails,
			Date valueDate, List<FinanceScheduleDetail> finScheduleDetails, List<FinanceRepayments> repayments,
			boolean calcwithoutDue, boolean zeroIfpaid) {
		logger.debug(" Entering ");

		//get penalty rates one time
		FinODPenaltyRate penaltyRate = finODPenaltyRateDAO.getFinODPenaltyRateByRef(finmain.getFinReference(), "");

		for (FinODDetails fod : finODDetails) {
			FinanceScheduleDetail curSchd = getODSchedule(finScheduleDetails, fod);

			if (curSchd != null) {
				if (!calcwithoutDue) {
					//check due and proceeed
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
						latePayMarking(finmain, fod, penaltyRate, finScheduleDetails, repayments, curSchd, valueDate,
								valueDate, false);
					}
				} else {
					latePayMarking(finmain, fod, penaltyRate, finScheduleDetails, repayments, curSchd, valueDate,
							valueDate, false);

				}

				// FIX only for Negative amount setting rejection.
				if (DateUtility.compare(valueDate, curSchd.getSchDate()) <= 0) {
					resetLPPToZero(fod, curSchd, repayments, zeroIfpaid, valueDate);
				}
			} else {
				//if there is no schedule for od now then there is no penlaty
				fod.setFinODTillDate(valueDate);
				fod.setFinCurODPri(BigDecimal.ZERO);
				fod.setFinCurODPft(BigDecimal.ZERO);
				fod.setFinCurODAmt(BigDecimal.ZERO);
				fod.setFinCurODDays(0);
				//TODO ###124902 - New field to be included for future use which stores the last payment date. This needs to be worked.
				fod.setFinLMdfDate(DateUtility.getAppDate());
				fod.setTotPenaltyAmt(BigDecimal.ZERO);
				fod.setTotPenaltyBal(
						fod.getTotPenaltyAmt().subtract(fod.getTotPenaltyPaid()).subtract(fod.getTotWaived()));
			}
		}

		logger.debug(" Leaving ");
		return finODDetails;
	}

	private void resetLPPToZero(FinODDetails fod, FinanceScheduleDetail curSchd, List<FinanceRepayments> repayments,
			boolean reset, Date valueDate) {
		logger.debug(" Entering ");

		BigDecimal totalDue = curSchd.getProfitSchd().add(curSchd.getPrincipalSchd()).subtract(curSchd.getSchdPftPaid())
				.subtract(curSchd.getSchdPriPaid());

		BigDecimal totPaidBefSchDate = BigDecimal.ZERO;
		for (FinanceRepayments repayment : repayments) {
			if (repayment.getFinSchdDate().compareTo(fod.getFinODSchdDate()) == 0
					&& repayment.getFinValueDate().compareTo(fod.getFinODSchdDate()) <= 0) {
				totPaidBefSchDate = totPaidBefSchDate.add(repayment.getFinSchdPriPaid())
						.add(repayment.getFinSchdPftPaid());
			}
		}

		if (fod.getFinCurODPri().add(fod.getFinCurODPft()).compareTo(BigDecimal.ZERO) > 0) {
			reset = false;
		}

		if (totPaidBefSchDate.compareTo(totalDue) >= 0 || DateUtility.compare(curSchd.getSchDate(), valueDate) >= 0) {
			fod.setFinCurODDays(0);
			if (reset) {
				fod.setTotPenaltyAmt(BigDecimal.ZERO);
				fod.setTotPenaltyBal(
						fod.getTotPenaltyAmt().subtract(fod.getTotPenaltyPaid()).subtract(fod.getTotWaived()));
				fod.setLPIAmt(BigDecimal.ZERO);
				fod.setLPIBal(fod.getLPIAmt().subtract(fod.getLPIPaid()).subtract(fod.getLPIWaived()));
			}
		}

		logger.debug(" Leaving ");

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
			finEODEvent = findLatePay(finEODEvent, custEODEvent, valueDate);
		}

		logger.debug(" Leaving ");
		return custEODEvent;
	}

	private FinEODEvent findLatePay(FinEODEvent finEODEvent, CustEODEvent custEODEvent, Date valueDate)
			throws Exception {

		FinanceMain finMain = finEODEvent.getFinanceMain();
		List<FinanceScheduleDetail> finSchdDetails = finEODEvent.getFinanceScheduleDetails();

		//Get details first time from DB
		String finRef = finMain.getFinReference();
		Date finStartDate = finMain.getFinStartDate();
		finEODEvent.setFinODDetails(getFinODDetailsDAO().getFinODDByFinRef(finRef, finStartDate));
		FinODPenaltyRate penaltyRate = null;
		boolean isPenaltyFetched = false;

		// Include Today in Late payment Calculation or NOT?
		Date lppCheckingDate = valueDate;
		if (ImplementationConstants.LP_MARK_FIRSTDAY) {
			lppCheckingDate = DateUtility.addDays(valueDate, 1);
		}

		for (int i = 0; i < finSchdDetails.size(); i++) {
			FinanceScheduleDetail curSchd = finSchdDetails.get(i);

			if (curSchd.getSchDate().compareTo(lppCheckingDate) >= 0) {
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
				FinODDetails fod = findExisingOD(finEODEvent.getFinODDetails(), curSchd);
				// if created new add it to finance
				if (fod == null) {

					// get penalty rates one time
					if (!isPenaltyFetched) {
						penaltyRate = finODPenaltyRateDAO.getFinODPenaltyRateByRef(finRef, "");
						isPenaltyFetched = true;
					}

					fod = createODDetails(curSchd, finMain, penaltyRate, valueDate);
					finEODEvent.getFinODDetails().add(fod);
				}
				//penalty calculation will done in SOD
				Date penaltyCalDate = valueDate;
				if (ImplementationConstants.LPP_CALC_SOD) {
					penaltyCalDate = DateUtility.addDays(valueDate, 1);
				}

				latePayMarking(finMain, fod, penaltyRate, finSchdDetails, null, curSchd, valueDate, penaltyCalDate,
						true);
			}
		}

		// Due Basis LPP Accrual Postings
		if (StringUtils.equals(ImplementationConstants.LPP_GST_DUE_ON, "D")) {

			List<FinODDetails> odList = finEODEvent.getFinODDetails();
			for (int i = 0; i < odList.size(); i++) {
				FinODDetails odDetail = odList.get(i);
				if (odDetail.getTotPenaltyBal().compareTo(BigDecimal.ZERO) > 0) {
					boolean isExists = finODAmzTaxDetailDAO.isDueCreatedForDate(finRef, odDetail.getFinODSchdDate(),
							"LPP");
					if (!isExists) {
						postLppAccruals(finEODEvent, custEODEvent, odDetail);
					}
				}
			}
		}

		updateFinPftDetails(finEODEvent.getFinProfitDetail(), finEODEvent.getFinODDetails(), valueDate);
		return finEODEvent;
	}

	public FinODDetails findExisingOD(List<FinODDetails> finODDetails, FinanceScheduleDetail curSchd) throws Exception {
		//FIXME: For Early exist changed the logic to read from bottom. To be incorporated in all versions
		for (int i = (finODDetails.size() - 1); i >= 0; i--) {
			if (finODDetails.get(i).getFinODSchdDate().compareTo(curSchd.getSchDate()) == 0) {
				return finODDetails.get(i);
			}
		}

		return null;
	}

	public void latePayMarking(FinanceMain finMain, FinODDetails fod, FinODPenaltyRate penaltyRate,
			List<FinanceScheduleDetail> finScheduleDetails, List<FinanceRepayments> repayments,
			FinanceScheduleDetail curSchd, Date valueDate, Date penaltyCalDate, boolean isEODprocess) {
		logger.debug("Entering");

		fod.setFinODTillDate(penaltyCalDate);
		fod.setFinCurODPri(curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid()));

		fod.setFinCurODPft(curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid()));
		fod.setFinCurODAmt(fod.getFinCurODPft().add(fod.getFinCurODPri()));

		//FIXME: PV To be verified with Chaitanya & Satish
		//fod.setFinCurODDays(DateUtility.getDaysBetween(fod.getFinODSchdDate(), valueDate));
		fod.setFinMaxODPri(curSchd.getPrincipalSchd());
		fod.setFinMaxODPft(curSchd.getProfitSchd());

		//PENALTY Issue Ref : 134715
		if (repayments == null) {
			repayments = getFinanceRepaymentsDAO().getByFinRefAndSchdDate(finMain.getFinReference(),
					fod.getFinODSchdDate());
		}

		if (repayments != null) {
			for (int i = 0; i < repayments.size(); i++) {
				FinanceRepayments repayment = repayments.get(i);

				//check the payment made against the actual schedule date 
				if (repayment.getFinSchdDate().compareTo(fod.getFinODSchdDate()) != 0) {
					continue;
				}

				//MAx OD amounts is same as repayments balance amounts
				if (repayment.getFinSchdDate().compareTo(repayment.getFinValueDate()) == 0) {
					fod.setFinMaxODPri(fod.getFinMaxODPri().subtract(repayment.getFinSchdPriPaid()));
					fod.setFinMaxODPft(fod.getFinMaxODPft().subtract(repayment.getFinSchdPftPaid()));
				}

			}
		}
		fod.setFinMaxODAmt(fod.getFinMaxODPft().add(fod.getFinMaxODPri()));
		fod.setFinCurODDays(DateUtility.getDaysBetween(fod.getFinODSchdDate(), penaltyCalDate));

		//TODO ###124902 - New field to be included for future use which stores the last payment date. This needs to be worked.
		fod.setFinLMdfDate(DateUtility.getAppDate());

		if (fod.getFinCurODPri().add(fod.getFinCurODPft()).compareTo(BigDecimal.ZERO) > 0 && !isEODprocess) {
			penaltyCalDate = DateUtility.getAppDate();
		}
		latePayPenaltyService.computeLPP(fod, penaltyCalDate, finMain, finScheduleDetails, repayments);

		String lpiMethod = finMain.getPastduePftCalMthd();

		if (StringUtils.isEmpty(lpiMethod)) {
			logger.error(" LPFT Method value Not Available for Reference :" + finMain.getFinReference());
		}

		if (!StringUtils.equals(lpiMethod, CalculationConstants.PDPFTCAL_NOTAPP)) {
			latePayInterestService.computeLPI(fod, penaltyCalDate, finMain, finScheduleDetails, repayments);
		}
		logger.debug("Leaving");
	}

	public void updateFinPftDetails(FinanceProfitDetail pftDetail, List<FinODDetails> finODDetails, Date valueDate)
			throws Exception {

		pftDetail.setODPrincipal(BigDecimal.ZERO);
		pftDetail.setODProfit(BigDecimal.ZERO);
		pftDetail.setPenaltyPaid(BigDecimal.ZERO);
		pftDetail.setPenaltyDue(BigDecimal.ZERO);
		pftDetail.setPenaltyWaived(BigDecimal.ZERO);
		pftDetail.setPrvODDate(valueDate);
		pftDetail.setNOODInst(0);
		pftDetail.setCurODDays(0);

		pftDetail.setTotPftOnPD(BigDecimal.ZERO);
		pftDetail.setTotPftOnPDDue(BigDecimal.ZERO);
		pftDetail.setTotPftOnPDPaid(BigDecimal.ZERO);
		pftDetail.setTotPftOnPDWaived(BigDecimal.ZERO);
		pftDetail.setLpiAmount(BigDecimal.ZERO);

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
			pftDetail.setLpiAmount(pftDetail.getLpiAmount().add(fod.getLPIAmt()));

			if (pftDetail.getFirstODDate() == null
					|| pftDetail.getFirstODDate().compareTo(pftDetail.getFinStartDate()) == 0) {
				pftDetail.setFirstODDate(fod.getFinODSchdDate());
			}

			//There is chance OD dates might not be in ascending order so take the least date
			if (fod.getFinODSchdDate().compareTo(pftDetail.getPrvODDate()) <= 0) {
				pftDetail.setPrvODDate(fod.getFinODSchdDate());
			}

			if (pftDetail.getCurODDays() < fod.getFinCurODDays()) {
				pftDetail.setCurODDays(fod.getFinCurODDays());
			}

			pftDetail.setNOODInst(pftDetail.getNOODInst() + 1);
		}

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

	public FinODDetails createODDetails(FinanceScheduleDetail curSchd, FinanceMain finMain,
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
		finODDetail.setRcdAction("I");

		finODDetail.setFinCurODPri(
				curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid()).add(curSchd.getCpzBalance()));
		finODDetail.setFinCurODPft(curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid()));
		finODDetail.setFinCurODAmt(finODDetail.getFinCurODPft().add(finODDetail.getFinCurODPri()));
		finODDetail.setFinMaxODPri(finODDetail.getFinCurODPri());
		finODDetail.setFinMaxODPft(finODDetail.getFinCurODPft());
		finODDetail.setFinMaxODAmt(finODDetail.getFinMaxODPft().add(finODDetail.getFinMaxODPri()));

		finODDetail.setFinCurODDays(DateUtility.getDaysBetween(finODDetail.getFinODSchdDate(), valueDate));
		//TODO ###124902 - New field to be included for future use which stores the last payment date. This needs to be worked.
		finODDetail.setFinLMdfDate(DateUtility.getAppDate());
		if (penaltyRate == null) {
			penaltyRate = new FinODPenaltyRate();
		}
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

	/**
	 * @param financeMain
	 * @param resultSet
	 * @throws Exception
	 */
	private void postLppAccruals(FinEODEvent finEODEvent, CustEODEvent custEODEvent, FinODDetails fod)
			throws Exception {
		logger.debug(" Entering ");

		String eventCode = AccountEventConstants.ACCEVENT_LPPAMZ;
		FinanceProfitDetail finPftDetail = finEODEvent.getFinProfitDetail();
		FinanceMain main = finEODEvent.getFinanceMain();

		long accountingID = getAccountingID(main, eventCode);
		if (accountingID == Long.MIN_VALUE) {
			return;
		}

		// Setting LPP Amount from Overdue Details for LPP amortization
		Map<String, BigDecimal> taxPercmap = null;
		FeeType lppFeeType = null;
		BigDecimal penaltyDue = fod.getTotPenaltyBal();
		BigDecimal penaltyDueGst = BigDecimal.ZERO;

		String taxRoundMode = SysParamUtil.getValue(CalculationConstants.TAX_ROUNDINGMODE).toString();
		int taxRoundingTarget = SysParamUtil.getValueAsInt(CalculationConstants.TAX_ROUNDINGTARGET);

		//Prepare Finance Detail
		FinanceDetail detail = new FinanceDetail();
		detail.getFinScheduleData().setFinanceMain(main);
		detail.getFinScheduleData().setFinanceType(finEODEvent.getFinType());
		prepareFinanceDetail(detail, custEODEvent);

		// Calculate GSTAmount 
		boolean gstCalReq = false;

		// LPP GST Amount calculation
		if (penaltyDue.compareTo(BigDecimal.ZERO) > 0) {
			lppFeeType = feeTypeDAO.getTaxDetailByCode(RepayConstants.ALLOCATION_ODC);
			if (lppFeeType != null) {
				if (lppFeeType.isTaxApplicable()) {
					gstCalReq = true;
				}
				if (!lppFeeType.isAmortzReq()) {
					penaltyDue = BigDecimal.ZERO;
				}
			} else {
				penaltyDue = BigDecimal.ZERO;
			}
		}

		if (penaltyDue.compareTo(BigDecimal.ZERO) == 0) {
			logger.debug("Leaving");
			return;
		}

		AEEvent aeEvent = AEAmounts.procCalAEAmounts(finPftDetail, finEODEvent.getFinanceScheduleDetails(), eventCode,
				custEODEvent.getEodValueDate(), custEODEvent.getEodValueDate());

		aeEvent.getAeAmountCodes().setdLPPAmz(penaltyDue);
		aeEvent.setDataMap(aeEvent.getAeAmountCodes().getDeclaredFieldValues());

		// GST parameters for State wise Account Number building
		String custDftBranch = null;
		String highPriorityState = null;
		String highPriorityCountry = null;

		if (detail.getCustomerDetails() != null) {
			custDftBranch = detail.getCustomerDetails().getCustomer().getCustDftBranch();
			List<CustomerAddres> addressList = detail.getCustomerDetails().getAddressList();
			if (CollectionUtils.isNotEmpty(addressList)) {
				for (CustomerAddres customerAddres : addressList) {
					if (customerAddres.getCustAddrPriority() == Integer
							.valueOf(PennantConstants.KYC_PRIORITY_VERY_HIGH)) {
						highPriorityState = customerAddres.getCustAddrProvince();
						highPriorityCountry = customerAddres.getCustAddrCountry();
						break;
					}
				}
			}
		}

		Map<String, Object> gstExecutionMap = finFeeDetailService.prepareGstMappingDetails(main.getFinBranch(),
				custDftBranch, highPriorityState, highPriorityCountry, detail.getFinanceTaxDetail(),
				main.getFinBranch());

		if (gstExecutionMap != null) {
			for (String key : gstExecutionMap.keySet()) {
				if (StringUtils.isNotBlank(key)) {
					aeEvent.getDataMap().put(key, gstExecutionMap.get(key));
				}
			}
		}

		// IF GST Calculation Required for LPI or LPP 
		if (gstCalReq) {

			taxPercmap = getTaxPercentages(gstExecutionMap, main.getFinCcy());

			// Calculate LPP GST Amount
			penaltyDueGst = getTotalTaxAmount(taxPercmap, penaltyDue, lppFeeType.getTaxComponent(), taxRoundMode,
					taxRoundingTarget);
		}

		// LPI GST Amount for Postings
		Map<String, BigDecimal> calGstMap = new HashMap<>();
		boolean addGSTInvoice = false;

		// LPP GST Amount for Postings
		if (penaltyDue.compareTo(BigDecimal.ZERO) > 0 && lppFeeType != null && lppFeeType.isTaxApplicable()) {

			if (taxPercmap == null) {
				taxPercmap = getTaxPercentages(gstExecutionMap, main.getFinCcy());
			}

			FinODAmzTaxDetail taxDetail = getTaxDetail(taxPercmap, penaltyDueGst, lppFeeType.getTaxComponent(),
					taxRoundMode, taxRoundingTarget);
			taxDetail.setFinReference(finPftDetail.getFinReference());
			taxDetail.setTaxFor("LPP");
			taxDetail.setAmount(penaltyDue);
			taxDetail.setValueDate(fod.getFinODSchdDate());
			taxDetail.setPostDate(new Timestamp(System.currentTimeMillis()));

			calGstMap.put("LPP_CGST_R", taxDetail.getCGST());
			calGstMap.put("LPP_SGST_R", taxDetail.getSGST());
			calGstMap.put("LPP_UGST_R", taxDetail.getUGST());
			calGstMap.put("LPP_IGST_R", taxDetail.getIGST());

			// Save Tax Details
			finODAmzTaxDetailDAO.save(taxDetail);

			String isGSTInvOnDue = SysParamUtil.getValueAsString("GST_INV_ON_DUE");
			if (StringUtils.equals(isGSTInvOnDue, PennantConstants.YES)) {
				addGSTInvoice = true;
			}
		} else {
			addZeroifNotContains(calGstMap, "LPP_CGST_R");
			addZeroifNotContains(calGstMap, "LPP_SGST_R");
			addZeroifNotContains(calGstMap, "LPP_UGST_R");
			addZeroifNotContains(calGstMap, "LPP_IGST_R");
		}

		// GST Details
		if (calGstMap != null) {
			aeEvent.getDataMap().putAll(calGstMap);
		}

		aeEvent.getAcSetIDList().add(accountingID);
		aeEvent.setCustAppDate(custEODEvent.getCustomer().getCustAppDate());

		//Postings Process and save all postings related to finance for one time accounts update
		aeEvent = postAccountingEOD(aeEvent);

		//GST Invoice Preparation
		if (aeEvent.getLinkedTranId() > 0) {

			// LPP Receivable Data Update for Future Accounting
			if (penaltyDue.compareTo(BigDecimal.ZERO) > 0) {

				// Save Tax Receivable Details
				FinTaxReceivable taxRcv = finODAmzTaxDetailDAO.getFinTaxReceivable(finPftDetail.getFinReference(),
						"LPP");
				boolean isSave = false;
				if (taxRcv == null) {
					taxRcv = new FinTaxReceivable();
					taxRcv.setFinReference(finPftDetail.getFinReference());
					taxRcv.setTaxFor("LPP");
					isSave = true;
				}

				if (calGstMap != null) {
					taxRcv.setCGST(taxRcv.getCGST().add(calGstMap.get("LPP_CGST_R")));
					taxRcv.setSGST(taxRcv.getSGST().add(calGstMap.get("LPP_SGST_R")));
					taxRcv.setUGST(taxRcv.getUGST().add(calGstMap.get("LPP_UGST_R")));
					taxRcv.setIGST(taxRcv.getIGST().add(calGstMap.get("LPP_IGST_R")));
				}

				taxRcv.setReceivableAmount(taxRcv.getReceivableAmount().add(penaltyDue));

				if (isSave) {
					finODAmzTaxDetailDAO.saveTaxReceivable(taxRcv);
				} else {
					finODAmzTaxDetailDAO.updateTaxReceivable(taxRcv);
				}
			}

			// GST Invoice Generation
			if (addGSTInvoice) {
				List<FinFeeDetail> feesList = prepareFeesList(lppFeeType, taxPercmap, calGstMap, penaltyDue);
				if (CollectionUtils.isNotEmpty(feesList)) {
					this.gstInvoiceTxnService.gstInvoicePreparation(aeEvent.getLinkedTranId(), detail, feesList, null,
							PennantConstants.GST_INVOICE_TRANSACTION_TYPE_DEBIT, main.getFinReference(), false);
				}
			}
		}

		finEODEvent.getReturnDataSet().addAll(aeEvent.getReturnDataSet());
		finEODEvent.setUpdLBDPostings(true);

		// LPP Due Amount , Which is already marked as Income/Receivable should be updated
		finPftDetail.setLppTillLBD(finPftDetail.getLppTillLBD().add(penaltyDue));
		finPftDetail.setGstLppTillLBD(finPftDetail.getGstLppTillLBD().add(penaltyDueGst));

		logger.debug(" Leaving ");
	}

	/**
	 * Method for Prepare FianceDetail for GST Invoice Report Preparation
	 * 
	 * @param financeDetail
	 * @param custEODEvent
	 */
	private void prepareFinanceDetail(FinanceDetail financeDetail, CustEODEvent custEODEvent) {

		// Set Tax Details if Already exists
		if (financeDetail.getFinanceTaxDetail() == null) {
			financeDetail.setFinanceTaxDetail(financeTaxDetailDAO
					.getFinanceTaxDetail(financeDetail.getFinScheduleData().getFinanceMain().getFinReference(), ""));
		}

		CustomerAddres addres = customerAddresDAO
				.getHighPriorityCustAddr(financeDetail.getFinScheduleData().getFinanceMain().getCustID(), "_AView");
		if (addres != null) {
			CustomerDetails customerDetails = new CustomerDetails();
			List<CustomerAddres> addressList = new ArrayList<CustomerAddres>();
			addressList.add(addres);
			customerDetails.setAddressList(addressList);
			customerDetails.setCustomer(custEODEvent.getCustomer());
			financeDetail.setCustomerDetails(customerDetails);
		}
	}

	/**
	 * Method for Preparing all GST fee amounts based on configurations
	 * 
	 * @param manAdvList
	 * @param financeDetail
	 * @return
	 */
	public Map<String, BigDecimal> getTaxPercentages(Map<String, Object> dataMap, String finCcy) {

		List<Rule> rules = ruleDAO.getGSTRuleDetails(RuleConstants.MODULE_GSTRULE, "");

		BigDecimal totalTaxPerc = BigDecimal.ZERO;
		Map<String, BigDecimal> taxPercMap = new HashMap<>();
		taxPercMap.put(RuleConstants.CODE_CGST, BigDecimal.ZERO);
		taxPercMap.put(RuleConstants.CODE_IGST, BigDecimal.ZERO);
		taxPercMap.put(RuleConstants.CODE_SGST, BigDecimal.ZERO);
		taxPercMap.put(RuleConstants.CODE_UGST, BigDecimal.ZERO);

		for (Rule rule : rules) {
			BigDecimal taxPerc = BigDecimal.ZERO;
			if (StringUtils.equals(RuleConstants.CODE_CGST, rule.getRuleCode())) {
				taxPerc = getRuleResult(rule.getSQLRule(), dataMap, finCcy);
				totalTaxPerc = totalTaxPerc.add(taxPerc);
				taxPercMap.put(RuleConstants.CODE_CGST, taxPerc);
			} else if (StringUtils.equals(RuleConstants.CODE_IGST, rule.getRuleCode())) {
				taxPerc = getRuleResult(rule.getSQLRule(), dataMap, finCcy);
				totalTaxPerc = totalTaxPerc.add(taxPerc);
				taxPercMap.put(RuleConstants.CODE_IGST, taxPerc);
			} else if (StringUtils.equals(RuleConstants.CODE_SGST, rule.getRuleCode())) {
				taxPerc = getRuleResult(rule.getSQLRule(), dataMap, finCcy);
				totalTaxPerc = totalTaxPerc.add(taxPerc);
				taxPercMap.put(RuleConstants.CODE_SGST, taxPerc);
			} else if (StringUtils.equals(RuleConstants.CODE_UGST, rule.getRuleCode())) {
				taxPerc = getRuleResult(rule.getSQLRule(), dataMap, finCcy);
				totalTaxPerc = totalTaxPerc.add(taxPerc);
				taxPercMap.put(RuleConstants.CODE_UGST, taxPerc);
			}
		}
		taxPercMap.put("TOTALGST", totalTaxPerc);

		return taxPercMap;
	}

	/**
	 * Method for Processing of SQL Rule and get Executed Result
	 * 
	 * @return
	 */
	private BigDecimal getRuleResult(String sqlRule, Map<String, Object> executionMap, String finCcy) {
		logger.debug("Entering");

		BigDecimal result = BigDecimal.ZERO;
		try {
			Object exereslut = ruleExecutionUtil.executeRule(sqlRule, executionMap, finCcy, RuleReturnType.DECIMAL);
			if (exereslut == null || StringUtils.isEmpty(exereslut.toString())) {
				result = BigDecimal.ZERO;
			} else {
				result = new BigDecimal(exereslut.toString());
			}
		} catch (Exception e) {
			logger.debug(e);
		}

		logger.debug("Leaving");
		return result;
	}

	/**
	 * Method for Calculating Total GST Amount with the Requested Amount
	 */
	private BigDecimal getTotalTaxAmount(Map<String, BigDecimal> taxPercmap, BigDecimal amount, String taxType,
			String roundingMode, int roundingTarget) {

		BigDecimal cgstPerc = taxPercmap.get(RuleConstants.CODE_CGST);
		BigDecimal sgstPerc = taxPercmap.get(RuleConstants.CODE_SGST);
		BigDecimal ugstPerc = taxPercmap.get(RuleConstants.CODE_UGST);
		BigDecimal igstPerc = taxPercmap.get(RuleConstants.CODE_IGST);
		BigDecimal totalGSTPerc = cgstPerc.add(sgstPerc).add(ugstPerc).add(igstPerc);

		BigDecimal gstAmount = BigDecimal.ZERO;
		if (StringUtils.equals(taxType, FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE)) {

			if (cgstPerc.compareTo(BigDecimal.ZERO) > 0) {
				BigDecimal cgst = (amount.multiply(cgstPerc)).divide(BigDecimal.valueOf(100), 9,
						RoundingMode.HALF_DOWN);
				cgst = CalculationUtil.roundAmount(cgst, roundingMode, roundingTarget);
				gstAmount = gstAmount.add(cgst);
			}
			if (sgstPerc.compareTo(BigDecimal.ZERO) > 0) {
				BigDecimal sgst = (amount.multiply(sgstPerc)).divide(BigDecimal.valueOf(100), 9,
						RoundingMode.HALF_DOWN);
				sgst = CalculationUtil.roundAmount(sgst, roundingMode, roundingTarget);
				gstAmount = gstAmount.add(sgst);
			}
			if (ugstPerc.compareTo(BigDecimal.ZERO) > 0) {
				BigDecimal ugst = (amount.multiply(ugstPerc)).divide(BigDecimal.valueOf(100), 9,
						RoundingMode.HALF_DOWN);
				ugst = CalculationUtil.roundAmount(ugst, roundingMode, roundingTarget);
				gstAmount = gstAmount.add(ugst);
			}
			if (igstPerc.compareTo(BigDecimal.ZERO) > 0) {
				BigDecimal igst = (amount.multiply(igstPerc)).divide(BigDecimal.valueOf(100), 9,
						RoundingMode.HALF_DOWN);
				igst = CalculationUtil.roundAmount(igst, roundingMode, roundingTarget);
				gstAmount = gstAmount.add(igst);
			}

		} else if (StringUtils.equals(taxType, FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE)) {

			BigDecimal percentage = (totalGSTPerc.add(new BigDecimal(100))).divide(BigDecimal.valueOf(100), 9,
					RoundingMode.HALF_DOWN);
			BigDecimal actualAmt = amount.divide(percentage, 9, RoundingMode.HALF_DOWN);
			actualAmt = CalculationUtil.roundAmount(actualAmt, roundingMode, roundingTarget);
			BigDecimal actTaxAmount = amount.subtract(actualAmt);

			if (cgstPerc.compareTo(BigDecimal.ZERO) > 0) {
				BigDecimal cgst = (actTaxAmount.multiply(cgstPerc)).divide(totalGSTPerc, 9, RoundingMode.HALF_DOWN);
				cgst = CalculationUtil.roundAmount(cgst, roundingMode, roundingTarget);
				gstAmount = gstAmount.add(cgst);
			}
			if (sgstPerc.compareTo(BigDecimal.ZERO) > 0) {
				BigDecimal sgst = (actTaxAmount.multiply(sgstPerc)).divide(totalGSTPerc, 9, RoundingMode.HALF_DOWN);
				sgst = CalculationUtil.roundAmount(sgst, roundingMode, roundingTarget);
				gstAmount = gstAmount.add(sgst);
			}
			if (ugstPerc.compareTo(BigDecimal.ZERO) > 0) {
				BigDecimal ugst = (actTaxAmount.multiply(ugstPerc)).divide(totalGSTPerc, 9, RoundingMode.HALF_DOWN);
				ugst = CalculationUtil.roundAmount(ugst, roundingMode, roundingTarget);
				gstAmount = gstAmount.add(ugst);
			}
			if (igstPerc.compareTo(BigDecimal.ZERO) > 0) {
				BigDecimal igst = (actTaxAmount.multiply(igstPerc)).divide(totalGSTPerc, 9, RoundingMode.HALF_DOWN);
				igst = CalculationUtil.roundAmount(igst, roundingMode, roundingTarget);
				gstAmount = gstAmount.add(igst);
			}
		}
		return gstAmount;
	}

	private FinODAmzTaxDetail getTaxDetail(Map<String, BigDecimal> taxPercmap, BigDecimal actTaxAmount, String taxType,
			String roundingMode, int roundingTarget) {

		BigDecimal cgstPerc = taxPercmap.get(RuleConstants.CODE_CGST);
		BigDecimal sgstPerc = taxPercmap.get(RuleConstants.CODE_SGST);
		BigDecimal ugstPerc = taxPercmap.get(RuleConstants.CODE_UGST);
		BigDecimal igstPerc = taxPercmap.get(RuleConstants.CODE_IGST);
		BigDecimal totalGSTPerc = cgstPerc.add(sgstPerc).add(ugstPerc).add(igstPerc);

		FinODAmzTaxDetail taxDetail = new FinODAmzTaxDetail();
		taxDetail.setTaxType(taxType);
		BigDecimal totalGST = BigDecimal.ZERO;

		if (cgstPerc.compareTo(BigDecimal.ZERO) > 0) {
			BigDecimal cgst = (actTaxAmount.multiply(cgstPerc)).divide(totalGSTPerc, 9, RoundingMode.HALF_DOWN);
			cgst = CalculationUtil.roundAmount(cgst, roundingMode, roundingTarget);
			taxDetail.setCGST(cgst);
			totalGST = totalGST.add(cgst);
		}
		if (sgstPerc.compareTo(BigDecimal.ZERO) > 0) {
			BigDecimal sgst = (actTaxAmount.multiply(sgstPerc)).divide(totalGSTPerc, 9, RoundingMode.HALF_DOWN);
			sgst = CalculationUtil.roundAmount(sgst, roundingMode, roundingTarget);
			taxDetail.setSGST(sgst);
			totalGST = totalGST.add(sgst);
		}
		if (ugstPerc.compareTo(BigDecimal.ZERO) > 0) {
			BigDecimal ugst = (actTaxAmount.multiply(ugstPerc)).divide(totalGSTPerc, 9, RoundingMode.HALF_DOWN);
			ugst = CalculationUtil.roundAmount(ugst, roundingMode, roundingTarget);
			taxDetail.setUGST(ugst);
			totalGST = totalGST.add(ugst);
		}
		if (igstPerc.compareTo(BigDecimal.ZERO) > 0) {
			BigDecimal igst = (actTaxAmount.multiply(igstPerc)).divide(totalGSTPerc, 9, RoundingMode.HALF_DOWN);
			igst = CalculationUtil.roundAmount(igst, roundingMode, roundingTarget);
			taxDetail.setIGST(igst);
			totalGST = totalGST.add(igst);
		}

		taxDetail.setTotalGST(totalGST);
		return taxDetail;
	}

	/**
	 * Method for Setting default Value to Zero
	 * 
	 * @param dataMap
	 * @param key
	 */
	private void addZeroifNotContains(Map<String, BigDecimal> dataMap, String key) {
		if (dataMap != null) {
			if (!dataMap.containsKey(key)) {
				dataMap.put(key, BigDecimal.ZERO);
			}
		}
	}

	private List<FinFeeDetail> prepareFeesList(FeeType lppFeeType, Map<String, BigDecimal> taxPercMap,
			Map<String, BigDecimal> calGstMap, BigDecimal penaltyDue) {
		logger.debug(Literal.ENTERING);

		List<FinFeeDetail> finFeeDetailsList = new ArrayList<FinFeeDetail>();
		FinFeeDetail finFeeDetail = null;

		//LPP Fees
		if (lppFeeType != null) {
			finFeeDetail = new FinFeeDetail();
			FinTaxDetails finTaxDetails = new FinTaxDetails();
			finFeeDetail.setFinTaxDetails(finTaxDetails);

			finFeeDetail.setFeeTypeCode(lppFeeType.getFeeTypeCode());
			finFeeDetail.setFeeTypeDesc(lppFeeType.getFeeTypeDesc());
			finFeeDetail.setTaxApplicable(true);
			finFeeDetail.setOriginationFee(false);
			finFeeDetail.setNetAmountOriginal(penaltyDue);

			if (taxPercMap != null && calGstMap != null) {
				finFeeDetail.setCgst(taxPercMap.get(RuleConstants.CODE_CGST));
				finFeeDetail.setSgst(taxPercMap.get(RuleConstants.CODE_SGST));
				finFeeDetail.setIgst(taxPercMap.get(RuleConstants.CODE_IGST));
				finFeeDetail.setUgst(taxPercMap.get(RuleConstants.CODE_UGST));

				finTaxDetails.setNetCGST(calGstMap.get("LPP_CGST_R"));
				finTaxDetails.setNetSGST(calGstMap.get("LPP_SGST_R"));
				finTaxDetails.setNetIGST(calGstMap.get("LPP_IGST_R"));
				finTaxDetails.setNetUGST(calGstMap.get("LPP_UGST_R"));

				if (FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE.equals(lppFeeType.getTaxComponent())) {
					BigDecimal gstAmount = finTaxDetails.getNetCGST().add(finTaxDetails.getNetSGST())
							.add(finTaxDetails.getNetIGST()).add(finTaxDetails.getNetUGST());
					finFeeDetail.setNetAmountOriginal(penaltyDue.subtract(gstAmount));
				}
			}

			finFeeDetailsList.add(finFeeDetail);
		}

		logger.debug(Literal.LEAVING);
		return finFeeDetailsList;
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

	public void setFinFeeDetailService(FinFeeDetailService finFeeDetailService) {
		this.finFeeDetailService = finFeeDetailService;
	}

	public void setRuleExecutionUtil(RuleExecutionUtil ruleExecutionUtil) {
		this.ruleExecutionUtil = ruleExecutionUtil;
	}

	public void setRuleDAO(RuleDAO ruleDAO) {
		this.ruleDAO = ruleDAO;
	}

	public void setFinanceTaxDetailDAO(FinanceTaxDetailDAO financeTaxDetailDAO) {
		this.financeTaxDetailDAO = financeTaxDetailDAO;
	}

	public void setCustomerAddresDAO(CustomerAddresDAO customerAddresDAO) {
		this.customerAddresDAO = customerAddresDAO;
	}

	public void setFeeTypeDAO(FeeTypeDAO feeTypeDAO) {
		this.feeTypeDAO = feeTypeDAO;
	}

	public void setFinODAmzTaxDetailDAO(FinODAmzTaxDetailDAO finODAmzTaxDetailDAO) {
		this.finODAmzTaxDetailDAO = finODAmzTaxDetailDAO;
	}

	public void setGstInvoiceTxnService(GSTInvoiceTxnService gstInvoiceTxnService) {
		this.gstInvoiceTxnService = gstInvoiceTxnService;
	}

}