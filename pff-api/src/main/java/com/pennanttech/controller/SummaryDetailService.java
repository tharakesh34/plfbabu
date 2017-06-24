package com.pennanttech.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.core.AccrualService;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.dao.finance.FinanceDisbursementDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinPlanEmiHoliday;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceSummary;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.util.APIConstants;

public class SummaryDetailService {
	private static final Logger		logger	= Logger.getLogger(SummaryDetailService.class);

	private FinanceDisbursementDAO	financeDisbursementDAO;
	protected FinODDetailsDAO		finODDetailsDAO;
	private FinanceProfitDetailDAO	financeProfitDetailDAO;
	private AccrualService			accrualService;

	public FinanceSummary getFinanceSummary(FinanceDetail financeDetail) {
		logger.debug("Entering");

		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		String finReference = financeMain.getFinReference();
		FinanceSummary summary = new FinanceSummary();
		if (financeMain != null) {
			financeDetail.setFinReference(finReference);
			summary.setEffectiveRateOfReturn(financeMain.getEffectiveRateOfReturn());
			summary.setTotalGracePft(financeMain.getTotalGracePft());
			summary.setTotalGraceCpz(financeMain.getTotalGraceCpz());
			summary.setTotalGrossGrcPft(financeMain.getTotalGrossGrcPft());
			summary.setFeeChargeAmt(financeMain.getFeeChargeAmt());

			// fetch summary details from FinPftDetails
			FinanceProfitDetail finPftDetail = new FinanceProfitDetail();
			financeMain.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			finPftDetail = accrualService.calProfitDetails(financeMain, financeDetail.getFinScheduleData()
					.getFinanceScheduleDetails(), finPftDetail, DateUtility.getAppDate());
			summary.setTotalCpz(finPftDetail.getTotalPftCpz());
			summary.setTotalProfit(finPftDetail.getTotalPftSchd());
			summary.setTotalRepayAmt(finPftDetail.getTotalpriSchd().add(finPftDetail.getTotalPftSchd()));
			summary.setNumberOfTerms(finPftDetail.getNOInst());
			summary.setLoanTenor(finPftDetail.getTotalTenor());
			summary.setMaturityDate(finPftDetail.getMaturityDate());
			summary.setFirstEmiAmount(finPftDetail.getFirstRepayAmt());
			summary.setNextSchDate(finPftDetail.getNSchdDate());
			summary.setNextRepayAmount(finPftDetail.getNSchdPri().add(finPftDetail.getNSchdPft()));

			// Total future Installments
			//int futureInst = finPftDetail.getNOInst() - (finPftDetail.getNOPaidInst() + finPftDetail.getNOODInst());
			summary.setFutureInst(finPftDetail.getFutureInst());
			summary.setFutureTenor(DateUtility.getMonthsBetween(finPftDetail.getNSchdDate(),
					finPftDetail.getMaturityDate()));
			summary.setFirstInstDate(finPftDetail.getFirstRepayDate());
			summary.setSchdPriPaid(finPftDetail.getTotalPriPaid());
			summary.setSchdPftPaid(finPftDetail.getTotalPftPaid());
			summary.setPaidTotal(finPftDetail.getTotalPriPaid().add(finPftDetail.getTotalPftPaid()));
			summary.setFinLastRepayDate(finPftDetail.getPrvRpySchDate());
			summary.setOutStandPrincipal(finPftDetail.getTotalPriBal());
			summary.setOutStandProfit(finPftDetail.getTotalPftBal());
			summary.setTotalOutStanding(finPftDetail.getTotalPriBal().add(finPftDetail.getTotalPftBal()));

			// overdue details
			FinODDetails finODDetails = finODDetailsDAO.getFinODSummary(finReference);
			if (finODDetails != null) {
				summary.setOverDuePrincipal(finODDetails.getFinCurODPri());
				summary.setOverDueProfit(finODDetails.getFinCurODPft());
				summary.setTotalOverDue(finODDetails.getFinCurODPri().add(finODDetails.getFinCurODPft()));
			}
			summary.setAdvPaymentAmount(finPftDetail.getTotalPftPaidInAdv().add(finPftDetail.getTotalPriPaidInAdv()));

			// set Finance closing status
			if (StringUtils.isBlank(financeMain.getClosingStatus())) {
				summary.setFinStatus(APIConstants.CLOSE_STATUS_ACTIVE);
			} else {
				summary.setFinStatus(financeMain.getClosingStatus());
			}


			// setting first and last disbursement dates
			List<FinanceDisbursement> disbList = getFinanceDisbursementDAO().getFinanceDisbursementDetails(finReference, "", false);
			if (disbList != null && disbList.size() > 0) {
				if (disbList.size() == 1) {
					summary.setFirstDisbDate(disbList.get(0).getDisbDate());
					summary.setLastDisbDate(disbList.get(0).getDisbDate());
				} else {
					Collections.sort(disbList, new Comparator<FinanceDisbursement>() {
						@Override
						public int compare(FinanceDisbursement b1, FinanceDisbursement b2) {
							return (new Integer(b1.getDisbSeq()).compareTo(new Integer(b2.getDisbSeq())));
						}
					});

					summary.setFirstDisbDate(disbList.get(0).getDisbDate());
					summary.setLastDisbDate(disbList.get(disbList.size() - 1).getDisbDate());
				}

				BigDecimal totDisbAmt = BigDecimal.ZERO;
				for (FinanceDisbursement finDisb : disbList) {
					totDisbAmt = totDisbAmt.add(finDisb.getDisbAmount());
					//totfeeChrgAmt = totfeeChrgAmt.add(finDisb.getFeeChargeAmt());
				}
				BigDecimal assetValue = financeMain.getFinAssetValue() == null ? BigDecimal.ZERO : financeMain.getFinAssetValue();
				if (assetValue.compareTo(BigDecimal.ZERO) == 0 || assetValue.compareTo(totDisbAmt) == 0) {
					summary.setFullyDisb(true);
				}
			}

			// Get FinODDetails
			List<FinODDetails> finODDetailsList = finODDetailsDAO.getFinODDByFinRef(finReference, null);
			if (finODDetailsList != null) {
				// ODcharges
				summary.setFinODDetail(finODDetailsList);
				summary.setOverDueInstlments(finODDetailsList.size());
			}
		}
		logger.debug("Leaving");
		return summary;
	}
	
	public void doProcessPlanEMIHDays(FinScheduleData finScheduleData) {
		FinanceMain financeMain = finScheduleData.getFinanceMain();
		List<Date> planEMIHDates = new ArrayList<Date>();
		List<Integer> planEMIHmonths = new ArrayList<Integer>();
		// Plan EMI Holidays Resetting after Rescheduling
		boolean isValidSchDate = false;
		boolean alwEMIHoliday = true;
		Date lockPeriodDate = null;
		if (finScheduleData.getApiPlanEMIHDates() != null) {
			if (StringUtils.equals(finScheduleData.getFinanceMain().getPlanEMIHMethod(),
					FinanceConstants.PLANEMIHMETHOD_FRQ)) {
				for (FinPlanEmiHoliday detail : finScheduleData.getApiPlanEMIHmonths()) {
					planEMIHmonths.add(detail.getPlanEMIHMonth());
				}
			} else {
				for (FinPlanEmiHoliday detail : finScheduleData.getApiPlanEMIHDates()) {
					planEMIHDates.add(detail.getPlanEMIHDate());
					List<FinanceScheduleDetail> schedules = finScheduleData.getFinanceScheduleDetails();
					for (FinanceScheduleDetail schDetail : schedules) {
						Date planEMIHStart = DateUtility.addMonths(financeMain.getGrcPeriodEndDate(),
								financeMain.getPlanEMIHLockPeriod());

						if (DateUtility.compare(detail.getPlanEMIHDate(), schDetail.getSchDate()) == 0) {
							if (!(DateUtility.compare(schDetail.getSchDate(), planEMIHStart) > 0)) {
								alwEMIHoliday = false;
								lockPeriodDate = detail.getPlanEMIHDate();
								break;
							}
							isValidSchDate = true;
							if (schDetail.getInstNumber() == 1
									|| DateUtility.compare(financeMain.getMaturityDate(), schDetail.getSchDate()) == 0
									|| DateUtility.compare(financeMain.getFinStartDate(), schDetail.getSchDate()) == 0
									|| DateUtility.compare(financeMain.getGrcPeriodEndDate(), schDetail.getSchDate()) >= 0) {
								String[] valueParm = new String[1];
								valueParm[0] = "holidayDate";
								finScheduleData.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("91111",
										valueParm)));
								return;
							}
						}
					}
					int dateCount = 0;
					for (Date date : planEMIHDates) {
						if (DateUtility.getYear(detail.getPlanEMIHDate()) == DateUtility.getYear(date)) {
							dateCount++;
						}
					}
					if (dateCount > finScheduleData.getFinanceMain().getPlanEMIHMaxPerYear()) {
						String[] valueParm = new String[2];
						valueParm[0] = "Planned EMI Holidays";
						valueParm[1] = String.valueOf(finScheduleData.getFinanceMain().getPlanEMIHMaxPerYear())+ " per Year";
						finScheduleData.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("30570", valueParm)));
						return;
					}
				}
			}
		}
		if (!isValidSchDate && StringUtils.equals(finScheduleData.getFinanceMain().getPlanEMIHMethod(),
						FinanceConstants.PLANEMIHMETHOD_ADHOC) && financeMain.isPlanEMIHAlw()) {
			String[] valueParm = new String[1];
			valueParm[0] = "holidayDate";
			finScheduleData.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("91111", valueParm)));
			return;
		}
		if (!alwEMIHoliday && StringUtils.equals(finScheduleData.getFinanceMain().getPlanEMIHMethod(),
						FinanceConstants.PLANEMIHMETHOD_ADHOC) && financeMain.isPlanEMIHAlw()) {
			String[] valueParm = new String[1];
			valueParm[0] = DateUtility.formatDate(lockPeriodDate, PennantConstants.XMLDateFormat);
			finScheduleData.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("91111", valueParm)));
			return;
		}

		finScheduleData.setPlanEMIHDates(planEMIHDates);
		finScheduleData.setPlanEMIHmonths(planEMIHmonths);
		if (finScheduleData.getFinanceMain().isPlanEMIHAlw()) {
			finScheduleData.getFinanceMain().setEventFromDate(financeMain.getFinStartDate());
			finScheduleData.getFinanceMain().setEventToDate(finScheduleData.getFinanceMain().getMaturityDate());
			finScheduleData.getFinanceMain().setRecalFromDate(financeMain.getNextRepayPftDate());
			finScheduleData.getFinanceMain().setRecalToDate(finScheduleData.getFinanceMain().getMaturityDate());
			finScheduleData.getFinanceMain().setRecalSchdMethod(finScheduleData.getFinanceMain().getScheduleMethod());
			
			finScheduleData.getFinanceMain().setCalRoundingMode(finScheduleData.getFinanceType().getRoundingMode());
			finScheduleData.getFinanceMain().setRoundingTarget(finScheduleData.getFinanceType().getRoundingTarget());

			if (StringUtils.equals(finScheduleData.getFinanceMain().getPlanEMIHMethod(),
					FinanceConstants.PLANEMIHMETHOD_FRQ)) {
				finScheduleData = ScheduleCalculator.getFrqEMIHoliday(finScheduleData);
			} else {
				finScheduleData = ScheduleCalculator.getAdhocEMIHoliday(finScheduleData);
			}
		}
	}

	public FinanceDisbursementDAO getFinanceDisbursementDAO() {
		return financeDisbursementDAO;
	}

	@Autowired
	public void setFinanceDisbursementDAO(FinanceDisbursementDAO financeDisbursementDAO) {
		this.financeDisbursementDAO = financeDisbursementDAO;
	}

	@Autowired
	public void setFinanceProfitDetailDAO(FinanceProfitDetailDAO financeProfitDetailDAO) {
		this.financeProfitDetailDAO = financeProfitDetailDAO;
	}
	
	@Autowired
	public void setFinODDetailsDAO(FinODDetailsDAO finODDetailsDAO) {
		this.finODDetailsDAO = finODDetailsDAO;
	}
	
	public FinanceProfitDetailDAO getFinanceProfitDetailDAO() {
		return financeProfitDetailDAO;
	}

	public AccrualService getAccrualService() {
		return accrualService;
	}

	public void setAccrualService(AccrualService accrualService) {
		this.accrualService = accrualService;
	}

}