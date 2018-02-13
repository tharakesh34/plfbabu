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

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.core.AccrualService;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.backend.dao.finance.FinFeeDetailDAO;
import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.dao.finance.FinanceDisbursementDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
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
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.util.APIConstants;

public class SummaryDetailService {
	private static final Logger		logger	= Logger.getLogger(SummaryDetailService.class);

	private FinanceDisbursementDAO	financeDisbursementDAO;
	protected FinODDetailsDAO		finODDetailsDAO;
	private FinanceProfitDetailDAO	financeProfitDetailDAO;
	private AccrualService			accrualService;
	protected FinExcessAmountDAO	finExcessAmountDAO;
	private FinFeeDetailDAO			finFeeDetailDAO;

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
			
			// calculate total paid fees
			BigDecimal totFeeAmount = calculateTotFeeChargeAmt(financeDetail.getFinScheduleData());
			summary.setFeeChargeAmt(totFeeAmount);

			// fetch summary details from FinPftDetails
			FinanceProfitDetail finPftDetail = new FinanceProfitDetail();
			financeMain.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			FinScheduleData curSchd = resetScheduleDetail(financeDetail.getFinScheduleData());
			finPftDetail = accrualService.calProfitDetails(financeMain, curSchd.getFinanceScheduleDetails(), 
					finPftDetail, DateUtility.getAppDate());
			summary.setTotalCpz(finPftDetail.getTotalPftCpz());
			summary.setTotalProfit(finPftDetail.getTotalPftSchd());
			summary.setTotalRepayAmt(finPftDetail.getTotalpriSchd().add(finPftDetail.getTotalPftSchd()));
			summary.setNumberOfTerms(finPftDetail.getNOInst());
			summary.setLoanTenor(DateUtility.getMonthsBetween(financeMain.getFinStartDate(), financeMain.getMaturityDate()));
			summary.setMaturityDate(finPftDetail.getMaturityDate());
			summary.setFirstEmiAmount(finPftDetail.getFirstRepayAmt());
			summary.setNextSchDate(finPftDetail.getNSchdDate());
			summary.setNextRepayAmount(finPftDetail.getNSchdPri().add(finPftDetail.getNSchdPft()));

			// Total future Installments
			//int futureInst = financeMain.getCalTerms() - (finPftDetail.getNOPaidInst() + finPftDetail.getNOODInst());
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
			
			// As part of Bajaj implementation this field is required for GetLoan & SOA API's only.
			summary.setAdvPaymentAmount(BigDecimal.ZERO);

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
				}
				BigDecimal assetValue = financeMain.getFinAssetValue() == null ? BigDecimal.ZERO : financeMain.getFinAssetValue();
				if (assetValue.compareTo(BigDecimal.ZERO) == 0 || assetValue.compareTo(totDisbAmt) == 0) {
					summary.setFullyDisb(true);
				}
			}

			// calculate OD Details
			BigDecimal overDuePrincipal = BigDecimal.ZERO;
			BigDecimal overDueProfit = BigDecimal.ZERO;
			int odInst = 0;
			List<FinODDetails> finODDetailsList = finODDetailsDAO.getFinODDByFinRef(finReference, null);
			if (finODDetailsList != null) {
				for (FinODDetails odDetail : finODDetailsList) {
					overDuePrincipal = overDuePrincipal.add(odDetail.getFinCurODPri());
					overDueProfit = overDueProfit.add(odDetail.getFinCurODPft());
					if(odDetail.getFinCurODAmt().compareTo(BigDecimal.ZERO) > 0) {
						odInst++;
					}
				}
				summary.setOverDuePrincipal(overDuePrincipal);
				summary.setOverDueProfit(overDueProfit);
				summary.setTotalOverDue(overDuePrincipal.add(overDueProfit));
				summary.setFinODDetail(finODDetailsList);
				summary.setOverDueInstlments(odInst);
				
				financeDetail.getFinScheduleData().setFinODDetails(finODDetailsList);
			}
		}
		logger.debug("Leaving");
		return summary;
	}
	
	/**
	 * Method for calculate total fees paid by customer.
	 * 
	 * @param finScheduleData
	 * @return
	 */
	private BigDecimal calculateTotFeeChargeAmt(FinScheduleData finScheduleData) {
		String finReference = finScheduleData.getFinanceMain().getFinReference();
		BigDecimal totFeeAmount = BigDecimal.ZERO;
		// Fetch total fee details to capture total fee paid by customer
		List<FinFeeDetail> feeDetails = finFeeDetailDAO.getFinFeeDetailByFinRef(finReference, false, "");
		if (feeDetails != null) {
			for (FinFeeDetail feeDetail : feeDetails) {
				if (StringUtils.equals(feeDetail.getFeeScheduleMethod(), CalculationConstants.REMFEE_PART_OF_DISBURSE)
						|| StringUtils.equals(feeDetail.getFeeScheduleMethod(), CalculationConstants.REMFEE_PART_OF_SALE_PRICE)) {
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
								finScheduleData.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91111",
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
			valueParm[0] = DateUtility.formatDate(lockPeriodDate, PennantConstants.XMLDateFormat);
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
	
	public List<FinanceRepayments> getRepaymentDetails(FinScheduleData finScheduleData, BigDecimal orgReceiptAmount,Date valueDate) {
		// Repayment Details
		List<FinanceRepayments> repayments = new ArrayList<>();
		BigDecimal totReceiptAmt = orgReceiptAmount;

		FinanceMain financeMain = finScheduleData.getFinanceMain();
		// Newly Paid Amount Repayment Details
		List<FinanceScheduleDetail> schdList = finScheduleData.getFinanceScheduleDetails();
		if (totReceiptAmt.compareTo(BigDecimal.ZERO) > 0) {
			char[] rpyOrder = finScheduleData.getFinanceType().getRpyHierarchy().replace("CS", "C").toCharArray();
			FinanceScheduleDetail curSchd = null;
			for (int i = 0; i < schdList.size(); i++) {
				curSchd = schdList.get(i);
				if (curSchd.getSchDate().compareTo(valueDate) > 0) {
					break;
				}

				if (totReceiptAmt.compareTo(BigDecimal.ZERO) == 0) {
					break;
				}

				if ((curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid())).compareTo(BigDecimal.ZERO) > 0
						|| (curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid()))
								.compareTo(BigDecimal.ZERO) > 0
						|| (curSchd.getFeeSchd().subtract(curSchd.getSchdFeePaid())).compareTo(BigDecimal.ZERO) > 0) {

					FinanceRepayments repayment = new FinanceRepayments();
					repayment.setFinValueDate(valueDate);
					repayment.setFinRpyFor(FinanceConstants.SCH_TYPE_SCHEDULE);
					repayment.setFinSchdDate(curSchd.getSchDate());
					repayment.setFinRpyAmount(orgReceiptAmount);

					for (int j = 0; j < rpyOrder.length; j++) {

						char repayTo = rpyOrder[j];
						if (repayTo == RepayConstants.REPAY_PENALTY) {
							continue;
						}
						BigDecimal balAmount = BigDecimal.ZERO;
						if (repayTo == RepayConstants.REPAY_PRINCIPAL) {
							balAmount = curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid());
							if (totReceiptAmt.compareTo(balAmount) < 0) {
								balAmount = totReceiptAmt;
							}
							repayment.setFinSchdPriPaid(balAmount);
							totReceiptAmt = totReceiptAmt.subtract(balAmount);

						} else if (repayTo == RepayConstants.REPAY_PROFIT) {

							balAmount = curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid());
							if (totReceiptAmt.compareTo(balAmount) < 0) {
								balAmount = totReceiptAmt;
							}
							repayment.setFinSchdPftPaid(balAmount);
							totReceiptAmt = totReceiptAmt.subtract(balAmount);

						} else if (repayTo == RepayConstants.REPAY_FEE) {

							balAmount = curSchd.getFeeSchd().subtract(curSchd.getSchdFeePaid());
							if (totReceiptAmt.compareTo(balAmount) < 0) {
								balAmount = totReceiptAmt;
							}
							repayment.setSchdFeePaid(balAmount);
							totReceiptAmt = totReceiptAmt.subtract(balAmount);
						}

					}
					repayment.setFinTotSchdPaid(repayment.getFinSchdPftPaid().add(repayment.getFinSchdPriPaid()));
					repayment.setFinType(financeMain.getFinType());
					repayment.setFinBranch(financeMain.getFinBranch());
					repayment.setFinCustID(financeMain.getCustID());
					repayment.setFinPaySeq(100);
					repayments.add(repayment);
				}
			}
		}
		return repayments;
	}
	
	/**
	 * Method for calculate and set the paid amounts for fees with below schedule methods.<br>
	 * 	- DISB(Deduct from disbursemnet).<br>
	 *  - POSP(Include fees to loan amount).
	 * 
	 * @param actualFees
	 * @return
	 */
	public List<FinFeeDetail> getUpdatedFees(List<FinFeeDetail> actualFees) {
		if (actualFees != null) {
			for (FinFeeDetail feeDetail : actualFees) {
				if(StringUtils.isBlank(feeDetail.getFeeCategory())) {
					feeDetail.setFeeCategory(FinanceConstants.FEES_AGAINST_LOAN);
				}
				if (StringUtils.isNotBlank(feeDetail.getVasReference())) {
					feeDetail.setFeeTypeCode(feeDetail.getVasReference());
				}
				if (StringUtils.equals(feeDetail.getFeeScheduleMethod(), CalculationConstants.REMFEE_PART_OF_DISBURSE)
						|| StringUtils.equals(feeDetail.getFeeScheduleMethod(),
								CalculationConstants.REMFEE_PART_OF_SALE_PRICE)) {
					feeDetail.setPaidAmount(feeDetail.getActualAmount().subtract(feeDetail.getWaivedAmount()));
					BigDecimal remBalFee = feeDetail.getActualAmount().subtract(feeDetail.getWaivedAmount()).subtract(feeDetail.getPaidAmount());
					feeDetail.setRemainingFee(remBalFee);
				}
				if(StringUtils.equals(feeDetail.getFeeScheduleMethod(), PennantConstants.List_Select)) {
					feeDetail.setFeeScheduleMethod("");
				}
			}
		}
		return actualFees;
	}
	
	/**
	 * Method for calculating total Advance payment amount(Excess+EMI in Adv)
	 * 
	 * @param finReference
	 * @return
	 */
	public BigDecimal getTotalAdvAmount(String finReference) {
		BigDecimal totAdvAmount = BigDecimal.ZERO;
		List<FinExcessAmount> finExcessAmounts = finExcessAmountDAO.getExcessAmountsByRef(finReference);
		if (finExcessAmounts != null && !finExcessAmounts.isEmpty()) {
			for (FinExcessAmount excessAmount : finExcessAmounts) {
				totAdvAmount = totAdvAmount.add(excessAmount.getBalanceAmt());
			}
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

	@Autowired
	public void setFinExcessAmountDAO(FinExcessAmountDAO finExcessAmountDAO) {
		this.finExcessAmountDAO = finExcessAmountDAO;
	}

	public void setFinFeeDetailDAO(FinFeeDetailDAO finFeeDetailDAO) {
		this.finFeeDetailDAO = finFeeDetailDAO;
	}
}