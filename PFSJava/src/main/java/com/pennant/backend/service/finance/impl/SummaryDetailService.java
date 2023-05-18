package com.pennant.backend.service.finance.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.core.AccrualService;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.GSTCalculator;
import com.pennant.app.util.ReceiptCalculator;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.FinFeeDetailDAO;
import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.dao.finance.FinanceDisbursementDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinPlanEmiHoliday;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceSummary;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.TaxAmountSplit;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.core.util.SchdUtil;

public class SummaryDetailService {
	private static final Logger logger = LogManager.getLogger(SummaryDetailService.class);

	protected FinanceDisbursementDAO financeDisbursementDAO;
	protected FinODDetailsDAO finODDetailsDAO;

	protected AccrualService accrualService;
	protected FinExcessAmountDAO finExcessAmountDAO;
	protected FinFeeDetailDAO finFeeDetailDAO;
	protected ReceiptCalculator receiptCalculator;
	protected ManualAdviseDAO manualAdviseDAO;

	public FinanceSummary getFinanceSummary(FinanceDetail fd) {
		logger.debug("Entering");

		FinanceMain fm = fd.getFinScheduleData().getFinanceMain();

		if (fm == null) {
			return null;
		}

		long finID = fm.getFinID();
		String finReference = fm.getFinReference();
		FinanceSummary summary = new FinanceSummary();
		List<FinanceScheduleDetail> schedules = fd.getFinScheduleData().getFinanceScheduleDetails();

		fd.setFinID(finID);
		fd.setFinReference(finReference);
		summary.setEffectiveRateOfReturn(fm.getEffectiveRateOfReturn());
		summary.setTotalGracePft(fm.getTotalGracePft());
		summary.setTotalGraceCpz(fm.getTotalGraceCpz());
		summary.setTotalGrossGrcPft(fm.getTotalGrossGrcPft());

		// calculate total paid fees
		BigDecimal totFeeAmount = calculateTotFeeChargeAmt(fd.getFinScheduleData());
		summary.setFeeChargeAmt(totFeeAmount);

		// fetch summary details from FinPftDetails
		FinanceProfitDetail finPftDetail = new FinanceProfitDetail();
		fm.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		FinScheduleData curSchd = resetScheduleDetail(fd.getFinScheduleData());
		Date appDate = SysParamUtil.getAppDate();
		finPftDetail = accrualService.calProfitDetails(fm, curSchd.getFinanceScheduleDetails(), finPftDetail, appDate);

		// override repay profit rate with FinProfitdetail calculated value(which is latest).
		fm.setRepayProfitRate(finPftDetail.getCurReducingRate());
		summary.setTotalCpz(finPftDetail.getTotalPftCpz());
		summary.setTotalProfit(finPftDetail.getTotalPftSchd());
		summary.setTotalRepayAmt(finPftDetail.getTotalpriSchd().add(finPftDetail.getTotalPftSchd()));
		summary.setNumberOfTerms(finPftDetail.getNOInst());
		summary.setLoanTenor(DateUtil.getMonthsBetween(fm.getFinStartDate(), fm.getMaturityDate()));
		summary.setMaturityDate(finPftDetail.getMaturityDate());
		summary.setFirstEmiAmount(finPftDetail.getFirstRepayAmt());
		summary.setNextSchDate(finPftDetail.getNSchdDate());
		summary.setNextRepayAmount(finPftDetail.getNSchdPri().add(finPftDetail.getNSchdPft()));

		// Total future Installments
		// int futureInst = financeMain.getCalTerms() - (finPftDetail.getNOPaidInst() + finPftDetail.getNOODInst());
		summary.setFutureInst(finPftDetail.getFutureInst());
		summary.setFutureTenor(DateUtil.getMonthsBetween(finPftDetail.getNSchdDate(), finPftDetail.getMaturityDate()));
		summary.setFirstInstDate(finPftDetail.getFirstRepayDate());
		summary.setSchdPriPaid(finPftDetail.getTotalPriPaid());
		summary.setSchdPftPaid(finPftDetail.getTotalPftPaid());
		summary.setPaidTotal(finPftDetail.getTotalPriPaid().add(finPftDetail.getTotalPftPaid()));
		summary.setFinLastRepayDate(finPftDetail.getPrvRpySchDate());
		summary.setOutStandPrincipal(finPftDetail.getTotalPriBal());
		summary.setOutStandProfit(finPftDetail.getTotalPftBal());
		summary.setTotalOutStanding(finPftDetail.getTotalPriBal().add(finPftDetail.getTotalPftBal()));
		summary.setPrincipal(finPftDetail.getTdSchdPriBal());
		summary.setFuturePrincipal(finPftDetail.getTotalPriBal().subtract(finPftDetail.getTdSchdPriBal()));
		summary.setInterest(finPftDetail.getTdSchdPftBal());
		summary.setFutureInterest(finPftDetail.getPftAccrued().subtract(finPftDetail.getTdSchdPftBal()));

		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, fm.getProductCategory())) {
			summary.setSanctionAmt(fm.getFinAssetValue());
			summary.setUtilizedAmt(finPftDetail.getTotalPriBal());
			summary.setAvailableAmt(summary.getSanctionAmt().subtract(summary.getUtilizedAmt()));
		}

		// As part of Bajaj implementation this field is required for GetLoan & SOA API's only.
		summary.setAdvPaymentAmount(BigDecimal.ZERO);

		// set Finance closing status
		if (StringUtils.isBlank(fm.getClosingStatus())) {
			summary.setFinStatus(FinanceConstants.CLOSE_STATUS_ACTIVE);
		} else {
			summary.setFinStatus(fm.getClosingStatus());
		}

		// setting first and last disbursement dates
		List<FinanceDisbursement> disbList = financeDisbursementDAO.getFinanceDisbursementDetails(finID, "", false);
		if (disbList != null && disbList.size() > 0) {
			if (disbList.size() == 1) {
				summary.setFirstDisbDate(disbList.get(0).getDisbDate());
				summary.setLastDisbDate(disbList.get(0).getDisbDate());
			} else {
				Collections.sort(disbList, new Comparator<FinanceDisbursement>() {
					@Override
					public int compare(FinanceDisbursement b1, FinanceDisbursement b2) {
						return (Integer.valueOf(b1.getDisbSeq()).compareTo(Integer.valueOf(b2.getDisbSeq())));
					}
				});

				summary.setFirstDisbDate(disbList.get(0).getDisbDate());
				summary.setLastDisbDate(disbList.get(disbList.size() - 1).getDisbDate());
			}

			BigDecimal totDisbAmt = BigDecimal.ZERO;
			for (FinanceDisbursement finDisb : disbList) {
				totDisbAmt = totDisbAmt.add(finDisb.getDisbAmount());
			}
			BigDecimal assetValue = fm.getFinAssetValue() == null ? BigDecimal.ZERO : fm.getFinAssetValue();
			if (assetValue.compareTo(BigDecimal.ZERO) == 0 || assetValue.compareTo(totDisbAmt) == 0) {
				summary.setFullyDisb(true);
			}
		}

		// calculate OD Details
		BigDecimal overDuePrincipal = BigDecimal.ZERO;
		BigDecimal overDueProfit = BigDecimal.ZERO;
		BigDecimal overDueCharges = BigDecimal.ZERO;
		BigDecimal latePayPftBal = BigDecimal.ZERO;
		BigDecimal totPenaltyBal = BigDecimal.ZERO;
		int odInst = 0;
		List<FinODDetails> finODDetailsList = finODDetailsDAO.getFinODDByFinRef(finID, null);
		if (finODDetailsList != null) {
			for (FinODDetails odDetail : finODDetailsList) {
				overDuePrincipal = overDuePrincipal.add(odDetail.getFinCurODPri());
				overDueProfit = overDueProfit.add(odDetail.getFinCurODPft());
				overDueCharges = overDueCharges.add(odDetail.getTotPenaltyAmt());
				totPenaltyBal = totPenaltyBal.add(odDetail.getTotPenaltyBal());
				latePayPftBal = latePayPftBal.add(odDetail.getLPIBal());
				if (odDetail.getFinCurODAmt().compareTo(BigDecimal.ZERO) > 0) {
					odInst++;
				}
			}
			summary.setOverDuePrincipal(overDuePrincipal);
			summary.setOverDueProfit(overDueProfit);
			summary.setOverDueCharges(overDueCharges);
			summary.setTotalOverDue(overDuePrincipal.add(overDueProfit));
			summary.setDueCharges(totPenaltyBal.add(latePayPftBal));
			summary.setTotalOverDueIncCharges(summary.getTotalOverDue().add(summary.getDueCharges()));
			summary.setFinODDetail(finODDetailsList);
			summary.setOverDueInstlments(odInst);
			summary.setOverDueAmount(summary.getTotalOverDueIncCharges());
			summary.setAdvPaymentAmount(getTotalAdvAmount(fm));
			summary.setOverDueEMI(SchdUtil.getOverDueEMI(appDate, schedules));
			summary.setAdvPaymentAmount(getTotalAdvAmount(fm));

			fd.getFinScheduleData().setFinODDetails(finODDetailsList);
		}
		logger.debug("Leaving");
		return summary;
	}

	/**
	 * Method for calculate total fees paid by customer.
	 * 
	 * @param schdData
	 * @return
	 */
	private BigDecimal calculateTotFeeChargeAmt(FinScheduleData schdData) {
		FinanceMain fm = schdData.getFinanceMain();
		long finID = fm.getFinID();
		BigDecimal totFeeAmount = BigDecimal.ZERO;
		// Fetch total fee details to capture total fee paid by customer
		List<FinFeeDetail> feeDetails = finFeeDetailDAO.getFinFeeDetailByFinRef(finID, false, "");
		if (feeDetails != null) {
			for (FinFeeDetail feeDetail : feeDetails) {
				if (StringUtils.equals(feeDetail.getFeeScheduleMethod(), CalculationConstants.REMFEE_PART_OF_DISBURSE)
						|| StringUtils.equals(feeDetail.getFeeScheduleMethod(),
								CalculationConstants.REMFEE_PART_OF_SALE_PRICE)) {
					totFeeAmount = totFeeAmount.add(feeDetail.getActualAmount().subtract(feeDetail.getWaivedAmount()));
				} else {
					totFeeAmount = totFeeAmount.add(feeDetail.getPaidAmount());
				}
			}
		}
		return totFeeAmount;
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
						Date planEMIHStart = DateUtil.addMonths(financeMain.getGrcPeriodEndDate(),
								financeMain.getPlanEMIHLockPeriod());

						if (DateUtil.compare(detail.getPlanEMIHDate(), schDetail.getSchDate()) == 0) {
							if (!(DateUtil.compare(schDetail.getSchDate(), planEMIHStart) > 0)) {
								alwEMIHoliday = false;
								lockPeriodDate = detail.getPlanEMIHDate();
								break;
							}
							isValidSchDate = true;
							if (schDetail.getInstNumber() == 1
									|| DateUtil.compare(financeMain.getMaturityDate(), schDetail.getSchDate()) == 0
									|| DateUtil.compare(financeMain.getFinStartDate(), schDetail.getSchDate()) == 0
									|| DateUtil.compare(financeMain.getGrcPeriodEndDate(),
											schDetail.getSchDate()) >= 0) {
								String[] valueParm = new String[1];
								valueParm[0] = "holidayDate";
								finScheduleData
										.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91111", valueParm)));
								return;
							}
						}
					}
					int dateCount = 0;
					for (Date date : planEMIHDates) {
						if (DateUtil.getYear(detail.getPlanEMIHDate()) == DateUtil.getYear(date)) {
							dateCount++;
						}
					}
					if (dateCount > finScheduleData.getFinanceMain().getPlanEMIHMaxPerYear()) {
						String[] valueParm = new String[2];
						valueParm[0] = "Planned EMI Holidays";
						valueParm[1] = String.valueOf(finScheduleData.getFinanceMain().getPlanEMIHMaxPerYear())
								+ " per Year";
						finScheduleData.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("30570", valueParm)));
						return;
					}
				}
			}
		}
		if (!isValidSchDate && StringUtils.equals(finScheduleData.getFinanceMain().getPlanEMIHMethod(),
				FinanceConstants.PLANEMIHMETHOD_ADHOC) && financeMain.isPlanEMIHAlw()) {
			String[] valueParm = new String[1];
			valueParm[0] = "holidayDate";
			finScheduleData.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91111", valueParm)));
			return;
		}
		if (!alwEMIHoliday && StringUtils.equals(finScheduleData.getFinanceMain().getPlanEMIHMethod(),
				FinanceConstants.PLANEMIHMETHOD_ADHOC) && financeMain.isPlanEMIHAlw()) {
			String[] valueParm = new String[1];
			valueParm[0] = DateUtil.format(lockPeriodDate, PennantConstants.XMLDateFormat);
			finScheduleData.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91111", valueParm)));
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

			if (StringUtils.equals(finScheduleData.getFinanceMain().getPlanEMIHMethod(),
					FinanceConstants.PLANEMIHMETHOD_FRQ)) {
				finScheduleData = ScheduleCalculator.getFrqEMIHoliday(finScheduleData);
			} else {
				finScheduleData = ScheduleCalculator.getAdhocEMIHoliday(finScheduleData);
			}
		}
	}

	public List<FinanceRepayments> getRepaymentDetails(FinScheduleData schdData, BigDecimal receiptAmount,
			Date valueDate) {
		List<FinanceRepayments> repayments = new ArrayList<>();

		if (receiptAmount.compareTo(BigDecimal.ZERO) > 0) {
			repayments.addAll(receiptCalculator.getRepayListByHierarchy(schdData, receiptAmount, valueDate));
		}

		return repayments;
	}

	/**
	 * Method for calculate and set the paid amounts for fees with below schedule methods.<br>
	 * - DISB(Deduct from disbursemnet).<br>
	 * - POSP(Include fees to loan amount).
	 * 
	 * @param actualFees
	 * @return
	 */
	public List<FinFeeDetail> getUpdatedFees(List<FinFeeDetail> actualFees) {
		if (actualFees != null) {
			for (FinFeeDetail feeDetail : actualFees) {
				if (StringUtils.isBlank(feeDetail.getFeeCategory())) {
					feeDetail.setFeeCategory(FinanceConstants.FEES_AGAINST_LOAN);
				}
				if (StringUtils.isNotBlank(feeDetail.getVasReference())) {
					feeDetail.setFeeTypeCode(feeDetail.getVasReference());
				}
				if (StringUtils.equals(feeDetail.getFeeScheduleMethod(), CalculationConstants.REMFEE_PART_OF_DISBURSE)
						|| StringUtils.equals(feeDetail.getFeeScheduleMethod(),
								CalculationConstants.REMFEE_PART_OF_SALE_PRICE)) {
					feeDetail.setPaidAmount(feeDetail.getActualAmount().subtract(feeDetail.getWaivedAmount()));
					BigDecimal remBalFee = feeDetail.getActualAmount().subtract(feeDetail.getWaivedAmount())
							.subtract(feeDetail.getPaidAmount());
					feeDetail.setRemainingFee(remBalFee);
				}
				if (StringUtils.equals(feeDetail.getFeeScheduleMethod(), PennantConstants.List_Select)) {
					feeDetail.setFeeScheduleMethod("");
				}
			}
		}
		return actualFees;
	}

	public BigDecimal getTotalAdvAmount(FinanceMain fm) {
		long finID = fm.getFinID();

		BigDecimal totAdvAmount = BigDecimal.ZERO;

		List<FinExcessAmount> excessAmounts = finExcessAmountDAO.getExcessAmountsByRef(finID);
		for (FinExcessAmount excessAmount : excessAmounts) {
			totAdvAmount = totAdvAmount.add(excessAmount.getBalanceAmt());
		}

		List<ManualAdvise> payables = manualAdviseDAO.getPaybleAdvises(finID, "_View");

		for (ManualAdvise manualAdvise : payables) {
			String taxType = "";

			BigDecimal adviseAmount = manualAdvise.getAdviseAmount();

			if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(manualAdvise.getTaxComponent())) {
				taxType = FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE;
			}

			TaxAmountSplit taxSplit = GSTCalculator.calculateGST(finID, fm.getFinCcy(), taxType,
					manualAdvise.getAdviseAmount());

			totAdvAmount = totAdvAmount.add(adviseAmount.add(taxSplit.gettGST()));
		}

		return totAdvAmount;
	}

	public FinScheduleData resetScheduleDetail(FinScheduleData finScheduleData) {
		// Resetting Maturity Terms & Summary details rendering in case of Reduce maturity cases
		if (finScheduleData == null || finScheduleData.getFinanceScheduleDetails() == null) {
			return finScheduleData;
		}
		int size = finScheduleData.getFinanceScheduleDetails().size();
		if (!StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY,
				finScheduleData.getFinanceMain().getProductCategory())) {
			for (int i = size - 1; i >= 0; i--) {
				FinanceScheduleDetail curSchd = finScheduleData.getFinanceScheduleDetails().get(i);
				if (curSchd.getClosingBalance().compareTo(BigDecimal.ZERO) == 0
						&& curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0) {
					finScheduleData.getFinanceMain().setMaturityDate(curSchd.getSchDate());
					break;
				} else if (curSchd.getClosingBalance().compareTo(BigDecimal.ZERO) == 0
						&& curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) == 0) {
					finScheduleData.getFinanceScheduleDetails().remove(i);
				}
			}
		}
		return finScheduleData;
	}

	@Autowired
	public void setFinanceDisbursementDAO(FinanceDisbursementDAO financeDisbursementDAO) {
		this.financeDisbursementDAO = financeDisbursementDAO;
	}

	@Autowired
	public void setAccrualService(AccrualService accrualService) {
		this.accrualService = accrualService;
	}

	@Autowired
	public void setFinFeeDetailDAO(FinFeeDetailDAO finFeeDetailDAO) {
		this.finFeeDetailDAO = finFeeDetailDAO;
	}

	@Autowired
	public void setReceiptCalculator(ReceiptCalculator receiptCalculator) {
		this.receiptCalculator = receiptCalculator;
	}

	@Autowired
	public void setManualAdviseDAO(ManualAdviseDAO manualAdviseDAO) {
		this.manualAdviseDAO = manualAdviseDAO;
	}

	@Autowired
	public void setFinODDetailsDAO(FinODDetailsDAO finODDetailsDAO) {
		this.finODDetailsDAO = finODDetailsDAO;
	}

	@Autowired
	public void setFinExcessAmountDAO(FinExcessAmountDAO finExcessAmountDAO) {
		this.finExcessAmountDAO = finExcessAmountDAO;
	}

}
