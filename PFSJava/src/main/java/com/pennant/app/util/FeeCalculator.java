package com.pennant.app.util;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.rulefactory.RuleDAO;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinReceiptData;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.TaxHeader;
import com.pennant.backend.model.finance.Taxes;
import com.pennant.backend.model.rmtmasters.FinTypeFees;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.service.finance.FinFeeDetailService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.RuleConstants;
import com.pennanttech.pennapps.core.resource.Literal;

public class FeeCalculator implements Serializable {
	private static final long serialVersionUID = 8062681791631293126L;
	private static Logger logger = Logger.getLogger(FeeCalculator.class);

	private FinFeeDetailService finFeeDetailService;
	private RuleExecutionUtil ruleExecutionUtil;
	private FinanceProfitDetailDAO profitDetailsDAO;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private RuleDAO ruleDAO;
	private FinanceDetailService financeDetailService;

	public FinReceiptData calculateFees(FinReceiptData receiptData) {
		Map<String, BigDecimal> taxPercentages = GSTCalculator.getTaxPercentages(receiptData.getFinReference());

		receiptData = convertToFinanceFees(receiptData, taxPercentages);
		List<FinFeeDetail> finFeeDetailList = receiptData.getFinanceDetail().getFinScheduleData().getFinFeeDetailList();
		if (finFeeDetailList != null && finFeeDetailList.size() > 0) {
			receiptData = calculateFeeDetail(receiptData, taxPercentages);
		}
		return receiptData;
	}

	private FinReceiptData convertToFinanceFees(FinReceiptData receiptData, Map<String, BigDecimal> taxPercentages) {
		logger.debug("Entering");
		FinanceDetail financeDetail = receiptData.getFinanceDetail();
		List<FinTypeFees> finTypeFeesList = financeDetail.getFinTypeFeesList();
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		FinanceMain financeMain = finScheduleData.getFinanceMain();
		List<FinFeeDetail> finFeeDetailList = new ArrayList<FinFeeDetail>();

		FinFeeDetail finFeeDetail = null;
		if (finTypeFeesList == null || finTypeFeesList.isEmpty()) {
			receiptData.getFinanceDetail().getFinScheduleData().setFinFeeDetailList(finFeeDetailList);
			logger.debug("Leaving ");
			return receiptData;
		}

		for (FinTypeFees finTypeFee : finTypeFeesList) {
			finFeeDetail = new FinFeeDetail();
			finFeeDetail.setNewRecord(true);
			finFeeDetail.setOriginationFee(finTypeFee.isOriginationFee());
			finFeeDetail.setFinEvent(finTypeFee.getFinEvent());
			finFeeDetail.setFinEventDesc(finTypeFee.getFinEventDesc());
			finFeeDetail.setFeeTypeID(finTypeFee.getFeeTypeID());
			finFeeDetail.setFeeOrder(finTypeFee.getFeeOrder());
			finFeeDetail.setFeeTypeCode(finTypeFee.getFeeTypeCode());
			finFeeDetail.setFeeTypeDesc(finTypeFee.getFeeTypeDesc());
			finFeeDetail.setFeeScheduleMethod(finTypeFee.getFeeScheduleMethod());
			finFeeDetail.setCalculationType(finTypeFee.getCalculationType());
			finFeeDetail.setRuleCode(finTypeFee.getRuleCode());
			finFeeDetail.setTdsReq(finTypeFee.isTdsReq());

			BigDecimal finAmount = CalculationUtil.roundAmount(finTypeFee.getAmount(), financeMain.getCalRoundingMode(),
					financeMain.getRoundingTarget());
			finTypeFee.setAmount(finAmount);

			finFeeDetail.setFixedAmount(finTypeFee.getAmount());
			finFeeDetail.setPercentage(finTypeFee.getPercentage());
			finFeeDetail.setCalculateOn(finTypeFee.getCalculateOn());
			finFeeDetail.setAlwDeviation(finTypeFee.isAlwDeviation());
			finFeeDetail.setMaxWaiverPerc(finTypeFee.getMaxWaiverPerc());
			finFeeDetail.setAlwModifyFee(finTypeFee.isAlwModifyFee());
			finFeeDetail.setAlwModifyFeeSchdMthd(finTypeFee.isAlwModifyFeeSchdMthd());
			finFeeDetail.setCalculatedAmount(finTypeFee.getAmount());
			finFeeDetail.setTaxComponent(finTypeFee.getTaxComponent());
			finFeeDetail.setTaxApplicable(finTypeFee.isTaxApplicable());

			if (finTypeFee.isTaxApplicable()) {
				this.finFeeDetailService.convertGSTFinTypeFees(finFeeDetail, finTypeFee, financeDetail, taxPercentages);
			} else {
				finFeeDetail.setActualAmountOriginal(finTypeFee.getAmount());
				finFeeDetail.setActualAmountGST(BigDecimal.ZERO);
				finFeeDetail.setActualAmount(finTypeFee.getAmount());

				BigDecimal netAmountOriginal = finFeeDetail.getActualAmountOriginal()
						.subtract(finFeeDetail.getWaivedAmount());

				finFeeDetail.setNetAmountOriginal(netAmountOriginal);
				finFeeDetail.setNetAmountGST(BigDecimal.ZERO);
				finFeeDetail.setNetAmount(netAmountOriginal);

				if (StringUtils.equals(finTypeFee.getFeeScheduleMethod(),
						CalculationConstants.REMFEE_PAID_BY_CUSTOMER)) {
					finFeeDetail.setPaidAmountOriginal(finTypeFee.getAmount());
					finFeeDetail.setPaidAmountGST(BigDecimal.ZERO);
					finFeeDetail.setPaidAmount(finTypeFee.getAmount());
				}

				if (StringUtils.equals(finTypeFee.getFeeScheduleMethod(), CalculationConstants.REMFEE_WAIVED_BY_BANK)) {
					finFeeDetail.setWaivedAmount(finTypeFee.getAmount());
				}

				finFeeDetail.setRemainingFeeOriginal(finFeeDetail.getActualAmount()
						.subtract(finFeeDetail.getWaivedAmount()).subtract(finFeeDetail.getPaidAmount()));
				finFeeDetail.setRemainingFeeGST(BigDecimal.ZERO);
				finFeeDetail.setRemainingFee(finFeeDetail.getActualAmount().subtract(finFeeDetail.getWaivedAmount())
						.subtract(finFeeDetail.getPaidAmount()));
			}

			finFeeDetailList.add(finFeeDetail);
		}

		receiptData.getFinanceDetail().getFinScheduleData().setFinFeeDetailList(finFeeDetailList);
		logger.debug("Leaving ");

		return receiptData;
	}

	public FinReceiptData calculateFeeDetail(FinReceiptData receiptData, Map<String, BigDecimal> taxPercentages) {
		logger.debug(Literal.ENTERING);
		FinanceDetail financeDetail = receiptData.getFinanceDetail();
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();

		// Calculate Fee Rules
		calculateFeeRules(receiptData, taxPercentages);

		// Calculate the fee Percentage
		calculateFeePercentageAmount(receiptData, taxPercentages);

		List<FinFeeDetail> fees = finScheduleData.getFinFeeDetailList();

		BigDecimal deductFeeFromDisbTot = BigDecimal.ZERO;
		BigDecimal feeAddToDisbTot = BigDecimal.ZERO;

		FinanceMain fm = finScheduleData.getFinanceMain();
		for (FinFeeDetail fee : fees) {
			this.finFeeDetailService.calculateFees(fee, fm, taxPercentages);

			if (StringUtils.equals(fee.getFeeScheduleMethod(), CalculationConstants.REMFEE_PART_OF_DISBURSE)) {
				deductFeeFromDisbTot = deductFeeFromDisbTot.add(fee.getRemainingFee());
			} else if (StringUtils.equals(fee.getFeeScheduleMethod(), CalculationConstants.REMFEE_PART_OF_SALE_PRICE)) {
				feeAddToDisbTot = feeAddToDisbTot.add(fee.getRemainingFee());
			} else if (StringUtils.equals(fee.getFeeScheduleMethod(), CalculationConstants.REMFEE_PAID_BY_CUSTOMER)) {
				if (fee.getPaidAmount().compareTo(BigDecimal.ZERO) == 0) {
					fee.setPaidAmount(fee.getActualAmount());
				}
			} else if (StringUtils.equals(fee.getFeeScheduleMethod(), CalculationConstants.REMFEE_WAIVED_BY_BANK)) {
				if (fee.getWaivedAmount().compareTo(BigDecimal.ZERO) == 0) {
					fee.setWaivedAmount(fee.getActualAmount());
				}
			}
			fee.setRemainingFee(fee.getActualAmount().subtract(fee.getPaidAmount()).subtract(fee.getWaivedAmount()));
		}

		if (FinanceConstants.FINSER_EVENT_ORG.equals(financeDetail.getModuleDefiner())) {
			fm.setDeductFeeDisb(deductFeeFromDisbTot);
			fm.setFeeChargeAmt(feeAddToDisbTot);
		} else {
			if (CollectionUtils.isNotEmpty(finScheduleData.getDisbursementDetails())) {
				List<Integer> approvedDisbSeq = financeDetailService
						.getFinanceDisbSeqs(finScheduleData.getFinanceMain().getFinReference(), false);
				for (FinanceDisbursement disbursement : finScheduleData.getDisbursementDetails()) {
					if (!approvedDisbSeq.contains(disbursement.getDisbSeq())) {
						disbursement.setDeductFeeDisb(deductFeeFromDisbTot);
						break;
					}
				}
			}

		}

		logger.debug(Literal.LEAVING);
		return receiptData;
	}

	private void calculateFeeRules(FinReceiptData receiptData, Map<String, BigDecimal> taxPercentages) {
		FinanceDetail financeDetail = receiptData.getFinanceDetail();
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		List<FinFeeDetail> finFeeDetailsList = finScheduleData.getFinFeeDetailList();
		List<String> feeRuleCodes = new ArrayList<String>();
		FinanceMain financeMain = finScheduleData.getFinanceMain();
		for (FinFeeDetail finFeeDetail : finFeeDetailsList) {
			if (StringUtils.isNotEmpty(finFeeDetail.getRuleCode())) {
				feeRuleCodes.add(finFeeDetail.getRuleCode());
			}
		}

		if (feeRuleCodes.size() > 0) {
			List<Rule> feeRules = ruleDAO.getRuleDetailList(feeRuleCodes, RuleConstants.MODULE_FEES,
					finScheduleData.getFeeEvent());

			if (CollectionUtils.isNotEmpty(feeRules)) {
				HashMap<String, Object> executionMap = new HashMap<String, Object>();
				Map<String, String> ruleSqlMap = new HashMap<String, String>();
				List<Object> objectList = new ArrayList<Object>();

				if (financeDetail.getCustomerDetails() != null) {
					objectList.add(financeDetail.getCustomerDetails().getCustomer());
					if (financeDetail.getCustomerDetails().getCustEmployeeDetail() != null) {
						objectList.add(financeDetail.getCustomerDetails().getCustEmployeeDetail());
					}
					List<CustomerAddres> addressList = financeDetail.getCustomerDetails().getAddressList();
					if (CollectionUtils.isNotEmpty(addressList)) {
						for (CustomerAddres customerAddres : addressList) {
							if (customerAddres.getCustAddrPriority() == Integer
									.valueOf(PennantConstants.KYC_PRIORITY_VERY_HIGH)) {
								executionMap.put("customerProvince", customerAddres.getCustAddrProvince());
								break;
							}
						}
					} else {
						executionMap.put("customerProvince", "");
					}
				}

				if (financeDetail.getFinScheduleData() != null) {
					objectList.add(finScheduleData.getFinanceMain());
					objectList.add(financeDetail.getFinScheduleData().getFinanceType());
				}

				for (Rule feeRule : feeRules) {
					if (feeRule.getFields() != null) {
						String[] fields = feeRule.getFields().split(",");
						for (String field : fields) {
							if (!executionMap.containsKey(field)) {
								this.ruleExecutionUtil.setExecutionMap(field, objectList, executionMap);
							}
						}
					}
					ruleSqlMap.put(feeRule.getRuleCode(), feeRule.getSQLRule());
				}

				if (financeMain != null && financeMain.getFinStartDate() != null) {
					int finAge = DateUtility.getMonthsBetween(DateUtility.getAppDate(), financeMain.getFinStartDate());
					executionMap.put("finAgetilldate", finAge);
					executionMap.put("completedTenure", finAge);
				}
				if (financeMain != null && StringUtils.isNotBlank(financeMain.getFinReference())
						&& StringUtils.isNotBlank(financeDetail.getModuleDefiner())) {
					FinanceProfitDetail finProfitDetail = profitDetailsDAO
							.getFinProfitDetailsById(financeMain.getFinReference());
					if (finProfitDetail != null) {
						BigDecimal outStandingFeeBal = financeScheduleDetailDAO
								.getOutStandingBalFromFees(financeMain.getFinReference());
						executionMap.put("totalOutStanding", finProfitDetail.getTotalPftBal());
						// PSD: 138255 PrincipalOutStanding will be future
						// Amount to be paid.
						executionMap.put("principalOutStanding",
								finProfitDetail.getTotalpriSchd().subtract(finProfitDetail.getTdSchdPri()));

						executionMap.put("principalSchdOutstanding",
								finProfitDetail.getTotalpriSchd().subtract(finProfitDetail.getTdSchdPri()));
						executionMap.put("totOSExcludeFees",
								finProfitDetail.getTotalPftBal().add(finProfitDetail.getTotalPriBal()));
						executionMap.put("totOSIncludeFees", finProfitDetail.getTotalPftBal()
								.add(finProfitDetail.getTotalPriBal()).add(outStandingFeeBal));
						executionMap.put("unearnedAmount", finProfitDetail.getUnearned());
					}

					if (receiptData.isForeClosureEnq()) {
						executionMap.put("principalOutStanding", finProfitDetail.getTotalPriBal()
								.subtract(receiptData.getOrgFinPftDtls().getTdSchdPriBal()));
					}
				}

				if (receiptData != null) {
					executionMap.put("totalPayment", receiptData.getTotReceiptAmount());

					BigDecimal totalDues = BigDecimal.ZERO;

					if (receiptData.getTotalDueAmount().compareTo(BigDecimal.ZERO) > 0) {
						executionMap.put("totalDueAmount", receiptData.getTotalDueAmount());
						totalDues = receiptData.getTotalDueAmount();
					} else {
						// Calculating due amount start
						FinReceiptHeader rch = receiptData.getReceiptHeader();
						totalDues = rch.getTotalPastDues().getTotalDue().add(rch.getTotalBounces().getTotalDue())
								.add(rch.getTotalRcvAdvises().getTotalDue()).add(rch.getTotalFees().getTotalDue())
								.subtract(receiptData.getExcessAvailable());
						executionMap.put("totalDueAmount", totalDues);
						// Calculating due amount end
					}

					if ((receiptData.getReceiptHeader().getPartPayAmount().compareTo(totalDues) > 0)) {
						executionMap.put("partialPaymentAmount",
								receiptData.getReceiptHeader().getPartPayAmount().subtract(totalDues));
					} else {
						executionMap.put("partialPaymentAmount", BigDecimal.ZERO);
					}

					Date fixedTenorEndDate = DateUtility.addMonths(financeMain.getGrcPeriodEndDate(),
							financeMain.getFixedRateTenor());

					if (financeMain.getFixedRateTenor() > 0
							&& fixedTenorEndDate.compareTo(DateUtility.getAppDate()) > 0) {
						executionMap.put("Finance_Fixed_Tenor", PennantConstants.YES);
					} else {
						executionMap.put("Finance_Fixed_Tenor", PennantConstants.NO);
					}
				}

				String finCcy = financeMain.getFinCcy();
				int formatter = CurrencyUtil.getFormat(finCcy);

				for (FinFeeDetail finFeeDetail : finFeeDetailsList) {
					if (StringUtils.isEmpty(finFeeDetail.getRuleCode())) {
						continue;
					}

					BigDecimal feeResult = this.finFeeDetailService
							.getFeeResult(ruleSqlMap.get(finFeeDetail.getRuleCode()), executionMap, finCcy);
					// unFormating feeResult
					feeResult = PennantApplicationUtil.unFormateAmount(feeResult, formatter);

					finFeeDetail.setCalculatedAmount(feeResult);

					if (finFeeDetail.isTaxApplicable()) {
						this.finFeeDetailService.processGSTCalForRule(finFeeDetail, feeResult, financeDetail,
								taxPercentages, false);
					} else {
						if (!finFeeDetail.isFeeModified() || !finFeeDetail.isAlwModifyFee()) {
							finFeeDetail.setActualAmountOriginal(feeResult);
							finFeeDetail.setActualAmountGST(BigDecimal.ZERO);
							finFeeDetail.setActualAmount(feeResult);
						}

						finFeeDetail.setRemainingFee(finFeeDetail.getActualAmount()
								.subtract(finFeeDetail.getPaidAmount()).subtract(finFeeDetail.getWaivedAmount()));
					}
				}
			}
		}
	}

	public void calculateFeePercentageAmount(FinReceiptData receiptData, Map<String, BigDecimal> taxPercentages) {
		logger.debug("Entering");
		FinanceDetail financeDetail = receiptData.getFinanceDetail();
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		List<FinFeeDetail> finFeeDetailList = finScheduleData.getFinFeeDetailList();

		if (CollectionUtils.isEmpty(finFeeDetailList)) {
			logger.debug("Leaving");
			return;
		}

		for (FinFeeDetail finFeeDetail : finFeeDetailList) {
			if (StringUtils.equals(finFeeDetail.getCalculationType(),
					PennantConstants.FEE_CALCULATION_TYPE_PERCENTAGE)) {
				receiptData.setEventFeePercent(true);
				receiptData.setCurEventFeePercent(finFeeDetail.getPercentage());

				BigDecimal calPercentageFee = getCalculatedPercentageFee(finFeeDetail, receiptData, taxPercentages);
				finFeeDetail.setCalculatedAmount(calPercentageFee);

				if (StringUtils.equals(finFeeDetail.getFeeScheduleMethod(),
						CalculationConstants.REMFEE_WAIVED_BY_BANK)) {
					finFeeDetail.setWaivedAmount(calPercentageFee);
				}

				if (finFeeDetail.isTaxApplicable()) { // if GST applicable
					this.finFeeDetailService.processGSTCalForPercentage(finFeeDetail, calPercentageFee, financeDetail,
							taxPercentages, false);

				} else {
					if (!finFeeDetail.isFeeModified() || !finFeeDetail.isAlwModifyFee()) {
						finFeeDetail.setActualAmountOriginal(calPercentageFee);
						finFeeDetail.setActualAmountGST(BigDecimal.ZERO);
						finFeeDetail.setActualAmount(calPercentageFee);
					}
					finFeeDetail.setRemainingFee(finFeeDetail.getActualAmount().subtract(finFeeDetail.getPaidAmount())
							.subtract(finFeeDetail.getWaivedAmount()));
				}
			}
		}

		logger.debug("Leaving");
	}

	private BigDecimal getCalculatedPercentageFee(FinFeeDetail finFeeDetail, FinReceiptData receiptData,
			Map<String, BigDecimal> taxPercentages) {
		logger.debug(Literal.ENTERING);
		FinScheduleData finScheduleData = receiptData.getFinanceDetail().getFinScheduleData();
		FinanceMain financeMain = finScheduleData.getFinanceMain();
		FinanceProfitDetail finPftDetail = finScheduleData.getFinPftDeatil();

		BigDecimal calculatedAmt = BigDecimal.ZERO;
		switch (finFeeDetail.getCalculateOn()) {
		case PennantConstants.FEE_CALCULATEDON_TOTALASSETVALUE:
			calculatedAmt = financeMain.getFinAssetValue();
			break;
		case PennantConstants.FEE_CALCULATEDON_LOANAMOUNT:
			calculatedAmt = financeMain.getFinAmount().subtract(financeMain.getDownPayment());
			break;
		case PennantConstants.FEE_CALCULATEDON_OUTSTANDINGPRCINCIPAL:
			calculatedAmt = finPftDetail.getTotalPriBal();
			break;
		case PennantConstants.FEE_CALCULATEDON_OUTSTANDPRINCIFUTURE:
			calculatedAmt = finPftDetail.getTotalPriBal().subtract(receiptData.getTdPriBal());
			break;
		case PennantConstants.FEE_CALCULATEDON_PAYAMOUNT:
			calculatedAmt = receiptData.getReceiptHeader().getPartPayAmount();
			break;
		// part payment fee calculation
		case PennantConstants.FEE_CALCULATEDON_ADJUSTEDPRINCIPAL:
			calculatedAmt = receiptData.getReceiptHeader().getPartPayAmount();
			if (calculatedAmt != null) {
				finFeeDetail.setActualOldAmount(calculatedAmt);
			}
			if (calculatedAmt.compareTo(BigDecimal.ZERO) < 0) {
				calculatedAmt = BigDecimal.ZERO;
			}

			BigDecimal calcPerc = BigDecimal.ONE;
			if (finFeeDetail.getPercentage().compareTo(BigDecimal.ZERO) > 0
					&& calculatedAmt.compareTo(BigDecimal.ZERO) > 0) {

				BigDecimal feePercent = finFeeDetail.getPercentage().divide(BigDecimal.valueOf(100), 4,
						RoundingMode.HALF_DOWN);
				if (StringUtils.equals(finFeeDetail.getTaxComponent(), FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE)) {
					BigDecimal gstPercentage = taxPercentages.get(RuleConstants.CODE_TOTAL_GST);
					BigDecimal gstCalPercentage = gstPercentage.divide(BigDecimal.valueOf(100), 4,
							RoundingMode.HALF_DOWN);
					BigDecimal totFeePay = gstCalPercentage.multiply(feePercent);
					calcPerc = calcPerc.add(feePercent).add(totFeePay);
				} else {
					calcPerc = calcPerc.add(feePercent);
				}

				// Fee Amount Calculation
				calculatedAmt = calculatedAmt.divide(calcPerc, 0, RoundingMode.HALF_DOWN);

			}

			break;

		// ### 11-07-2018 - PSD Ticket ID : 127846
		case PennantConstants.FEE_CALCULATEDON_DROPLINEPOS:
			calculatedAmt = getDropLinePOS(SysParamUtil.getAppDate(), finScheduleData);
			break;

		default:
			break;
		}
		finFeeDetail.setCalculatedOn(calculatedAmt);
		finFeeDetail.setActPercentage(finFeeDetail.getPercentage());

		calculatedAmt = calculatedAmt.multiply(finFeeDetail.getPercentage()).divide(BigDecimal.valueOf(100), 2,
				RoundingMode.HALF_DOWN);
		calculatedAmt = CalculationUtil.roundAmount(calculatedAmt, financeMain.getCalRoundingMode(),
				financeMain.getRoundingTarget());

		logger.debug(Literal.LEAVING);

		return calculatedAmt;
	}

	// ### 11-07-2018 - Start - PSD Ticket ID : 127846

	/**
	 * Method to get DroplinePOS.
	 * 
	 * @param valueDate
	 * @param finScheduleData
	 * 
	 */
	public BigDecimal getDropLinePOS(Date valueDate, FinScheduleData finScheduleData) {

		BigDecimal dropLinePOS = BigDecimal.ZERO;
		String finReference = finScheduleData.getFinanceMain().getFinReference();
		List<FinanceScheduleDetail> scheduleList = finScheduleData.getFinanceScheduleDetails();

		if (scheduleList == null || scheduleList.isEmpty()) {
			scheduleList = financeScheduleDetailDAO.getFinSchdDetailsForBatch(finReference);
		}

		for (FinanceScheduleDetail schedule : scheduleList) {
			dropLinePOS = schedule.getODLimit();
			if (valueDate.compareTo(schedule.getSchDate()) <= 0) {
				break;
			}
		}

		return dropLinePOS;
	}

	public static Map<String, BigDecimal> getFeeRuleMap(List<FinFeeDetail> feeDetails, String payType) {
		Map<String, BigDecimal> dataMap = new HashMap<>();
		for (FinFeeDetail finFeeDetail : feeDetails) {
			dataMap.putAll(getFeeRuleMap(payType, finFeeDetail));
		}

		return null;

	}

	public static Map<String, BigDecimal> getFeeRuleMap(String payType, FinFeeDetail finFeeDetail) {
		Map<String, BigDecimal> dataMap = new HashMap<>();

		TaxHeader taxHeader = finFeeDetail.getTaxHeader();
		Taxes cgstTax = new Taxes();
		Taxes sgstTax = new Taxes();
		Taxes igstTax = new Taxes();
		Taxes ugstTax = new Taxes();
		Taxes cessTax = new Taxes();

		if (taxHeader != null) {
			List<Taxes> taxDetails = taxHeader.getTaxDetails();
			if (CollectionUtils.isNotEmpty(taxDetails)) {
				for (Taxes taxes : taxDetails) {
					if (StringUtils.equals(RuleConstants.CODE_CGST, taxes.getTaxType())) {
						cgstTax = taxes;
					} else if (StringUtils.equals(RuleConstants.CODE_SGST, taxes.getTaxType())) {
						sgstTax = taxes;
					} else if (StringUtils.equals(RuleConstants.CODE_IGST, taxes.getTaxType())) {
						igstTax = taxes;
					} else if (StringUtils.equals(RuleConstants.CODE_UGST, taxes.getTaxType())) {
						ugstTax = taxes;
					} else if (StringUtils.equals(RuleConstants.CODE_CESS, taxes.getTaxType())) {
						cessTax = taxes;
					}
				}
			}
		}

		String feeTypeCode = finFeeDetail.getFeeTypeCode();

		dataMap.put(feeTypeCode + "_C", finFeeDetail.getActualAmount());
		dataMap.put(feeTypeCode + "_P", finFeeDetail.getPaidAmountOriginal());
		dataMap.put(feeTypeCode + "_N", finFeeDetail.getNetAmount());

		if (StringUtils.equals(payType, RepayConstants.RECEIPTMODE_EXCESS)) {
			payType = "EX_";
		} else if (StringUtils.equals(payType, RepayConstants.RECEIPTMODE_EMIINADV)) {
			payType = "EA_";
		} else if (StringUtils.equals(payType, RepayConstants.RECEIPTMODE_PAYABLE)) {
			payType = "PA_";
		} else {
			payType = "PB_";
		}
		dataMap.put(payType + feeTypeCode + "_P", finFeeDetail.getPaidAmountOriginal());

		if (FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE.equals(finFeeDetail.getTaxComponent())) {
			dataMap.put(feeTypeCode + "_W",
					finFeeDetail.getWaivedAmount().subtract(cgstTax.getWaivedTax().add(sgstTax.getWaivedTax())
							.add(igstTax.getWaivedTax()).add(ugstTax.getWaivedTax()).add(cessTax.getWaivedTax())));
		} else {
			dataMap.put(feeTypeCode + "_W", finFeeDetail.getWaivedAmount());
		}

		// Calculated Amount
		dataMap.put(feeTypeCode + "_CGST_C", cgstTax.getActualTax());
		dataMap.put(feeTypeCode + "_SGST_C", sgstTax.getActualTax());
		dataMap.put(feeTypeCode + "_IGST_C", igstTax.getActualTax());
		dataMap.put(feeTypeCode + "_UGST_C", ugstTax.getActualTax());
		dataMap.put(feeTypeCode + "_CESS_C", cessTax.getActualTax());

		// Paid Amount
		dataMap.put(feeTypeCode + "_CGST_P", cgstTax.getPaidTax());
		dataMap.put(feeTypeCode + "_SGST_P", sgstTax.getPaidTax());
		dataMap.put(feeTypeCode + "_IGST_P", igstTax.getPaidTax());
		dataMap.put(feeTypeCode + "_UGST_P", ugstTax.getPaidTax());
		dataMap.put(feeTypeCode + "_CESS_P", cessTax.getPaidTax());

		// Net Amount
		dataMap.put(feeTypeCode + "_CGST_N", cgstTax.getNetTax());
		dataMap.put(feeTypeCode + "_SGST_N", sgstTax.getNetTax());
		dataMap.put(feeTypeCode + "_IGST_N", igstTax.getNetTax());
		dataMap.put(feeTypeCode + "_UGST_N", ugstTax.getNetTax());
		dataMap.put(feeTypeCode + "_CESS_N", cessTax.getNetTax());

		// Waiver GST Amounts (GST Waiver Changes)
		dataMap.put(feeTypeCode + "_CGST_W", cgstTax.getWaivedTax());
		dataMap.put(feeTypeCode + "_SGST_W", sgstTax.getWaivedTax());
		dataMap.put(feeTypeCode + "_IGST_W", igstTax.getWaivedTax());
		dataMap.put(feeTypeCode + "_UGST_W", ugstTax.getWaivedTax());
		dataMap.put(feeTypeCode + "_CESS_W", cessTax.getWaivedTax());

		return dataMap;
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

	public FinanceProfitDetailDAO getProfitDetailsDAO() {
		return profitDetailsDAO;
	}

	public void setProfitDetailsDAO(FinanceProfitDetailDAO profitDetailsDAO) {
		this.profitDetailsDAO = profitDetailsDAO;
	}

	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	@Autowired
	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}
}