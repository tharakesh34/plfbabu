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

import com.pennant.app.constants.CalculationConstants;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.rulefactory.RuleDAO;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinReceiptData;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.financetaxdetail.FinanceTaxDetail;
import com.pennant.backend.model.rmtmasters.FinTypeFees;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.service.finance.FinFeeDetailService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RuleConstants;

public class FeeCalculator implements Serializable {
	private static final long serialVersionUID = 8062681791631293126L;
	private static Logger logger = Logger.getLogger(FeeCalculator.class);

	private FinFeeDetailService finFeeDetailService;
	private RuleExecutionUtil ruleExecutionUtil;
	private FinanceProfitDetailDAO profitDetailsDAO;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private RuleDAO ruleDAO;

	public FinReceiptData calculateFees(FinReceiptData receiptData) {
		receiptData = convertToFinanceFees(receiptData);
		List<FinFeeDetail> finFeeDetailList = receiptData.getFinanceDetail().getFinScheduleData().getFinFeeDetailList();
		if (finFeeDetailList != null && finFeeDetailList.size() > 0) {
			receiptData = calculateFeeDetail(receiptData);
		}
		return receiptData;
	}

	private FinReceiptData convertToFinanceFees(FinReceiptData receiptData) {
		logger.debug("Entering");
		FinanceDetail financeDetail = receiptData.getFinanceDetail();
		List<FinTypeFees> feeTypes = financeDetail.getFinTypeFeesList();
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		FinanceMain fm = finScheduleData.getFinanceMain();
		List<FinFeeDetail> fees = new ArrayList<FinFeeDetail>();

		FinFeeDetail fee = null;
		if (feeTypes == null || feeTypes.isEmpty()) {
			receiptData.getFinanceDetail().getFinScheduleData().setFinFeeDetailList(fees);
			logger.debug("Leaving ");
			return receiptData;
		}

		Map<String, Object> gstExecutionMap = new HashMap<>();

		if (!financeDetail.getFinScheduleData().getGstExecutionMap().isEmpty()) {
			gstExecutionMap = (HashMap<String, Object>) financeDetail.getFinScheduleData().getGstExecutionMap();
		} else {
			gstExecutionMap = getGstMappingDetails(financeDetail);
			financeDetail.getFinScheduleData().setGstExecutionMap(gstExecutionMap);
		}

		for (FinTypeFees feeType : feeTypes) {
			fee = new FinFeeDetail();
			fee.setNewRecord(true);
			fee.setOriginationFee(feeType.isOriginationFee());
			fee.setFinEvent(feeType.getFinEvent());
			fee.setFinEventDesc(feeType.getFinEventDesc());
			fee.setFeeTypeID(feeType.getFeeTypeID());
			fee.setFeeOrder(feeType.getFeeOrder());
			fee.setFeeTypeCode(feeType.getFeeTypeCode());
			fee.setFeeTypeDesc(feeType.getFeeTypeDesc());
			fee.setFeeScheduleMethod(feeType.getFeeScheduleMethod());
			fee.setCalculationType(feeType.getCalculationType());
			fee.setRuleCode(feeType.getRuleCode());

			BigDecimal finAmount = CalculationUtil.roundAmount(feeType.getAmount(), fm.getCalRoundingMode(),
					fm.getRoundingTarget());
			feeType.setAmount(finAmount);

			fee.setFixedAmount(feeType.getAmount());
			fee.setPercentage(feeType.getPercentage());
			fee.setCalculateOn(feeType.getCalculateOn());
			fee.setAlwDeviation(feeType.isAlwDeviation());
			fee.setMaxWaiverPerc(feeType.getMaxWaiverPerc());
			fee.setAlwModifyFee(feeType.isAlwModifyFee());
			fee.setAlwModifyFeeSchdMthd(feeType.isAlwModifyFeeSchdMthd());
			fee.setCalculatedAmount(feeType.getAmount());
			fee.setTaxComponent(feeType.getTaxComponent());
			fee.setTaxApplicable(feeType.isTaxApplicable());

			if (feeType.isTaxApplicable()) {
				this.finFeeDetailService.convertGSTFinTypeFees(fee, feeType, financeDetail, gstExecutionMap);
			} else {
				fee.setActualAmountOriginal(feeType.getAmount());
				fee.setActualAmountGST(BigDecimal.ZERO);
				fee.setActualAmount(feeType.getAmount());

				BigDecimal netAmountOriginal = fee.getActualAmountOriginal()
						.subtract(fee.getWaivedAmount());

				fee.setNetAmountOriginal(netAmountOriginal);
				fee.setNetAmountGST(BigDecimal.ZERO);
				fee.setNetAmount(netAmountOriginal);

				if (StringUtils.equals(feeType.getFeeScheduleMethod(),
						CalculationConstants.REMFEE_PAID_BY_CUSTOMER)) {
					fee.setPaidAmountOriginal(feeType.getAmount());
					fee.setPaidAmountGST(BigDecimal.ZERO);
					fee.setPaidAmount(feeType.getAmount());
				}

				if (StringUtils.equals(feeType.getFeeScheduleMethod(), CalculationConstants.REMFEE_WAIVED_BY_BANK)) {
					fee.setWaivedAmount(feeType.getAmount());
				}

				fee.setRemainingFeeOriginal(fee.getActualAmount()
						.subtract(fee.getWaivedAmount()).subtract(fee.getPaidAmount()));
				fee.setRemainingFeeGST(BigDecimal.ZERO);
				fee.setRemainingFee(fee.getActualAmount().subtract(fee.getWaivedAmount())
						.subtract(fee.getPaidAmount()));
			}

			fees.add(fee);
		}

		receiptData.getFinanceDetail().getFinScheduleData().setFinFeeDetailList(fees);
		logger.debug("Leaving ");

		return receiptData;
	}

	private FinReceiptData calculateFeeDetail(FinReceiptData receiptData) {
		FinanceDetail financeDetail = receiptData.getFinanceDetail();
		logger.debug("Entering");
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		Map<String, Object> gstExecutionMap = new HashMap<>();

		if (!financeDetail.getFinScheduleData().getGstExecutionMap().isEmpty()) {
			gstExecutionMap = (HashMap<String, Object>) financeDetail.getFinScheduleData().getGstExecutionMap();
		} else {
			gstExecutionMap = getGstMappingDetails(financeDetail);
			financeDetail.getFinScheduleData().setGstExecutionMap(gstExecutionMap);
		}

		// Calculate Fee Rules
		calculateFeeRules(receiptData);

		// Calculate the fee Percentage
		calculateFeePercentageAmount(receiptData);

		List<FinFeeDetail> fees = finScheduleData.getFinFeeDetailList();

		BigDecimal deductFeeFromDisbTot = BigDecimal.ZERO;
		BigDecimal feeAddToDisbTot = BigDecimal.ZERO;

		for (FinFeeDetail fee : fees) {
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

			if (fee.isNewRecord() && !fee.isOriginationFee()) {
				fee.setPaidAmount(fee.getActualAmount());
			}

			fee.setRemainingFee(fee.getActualAmount().subtract(fee.getPaidAmount()).subtract(fee.getWaivedAmount()));
		}

		// FIXME as discussed should be added in finance main table
		FinanceMain fm = finScheduleData.getFinanceMain();
		if (StringUtils.equals(FinanceConstants.FINSER_EVENT_ORG, financeDetail.getModuleDefiner())) {
			fm.setDeductFeeDisb(deductFeeFromDisbTot);
			fm.setFeeChargeAmt(feeAddToDisbTot);
		}
		
		for (FinanceDisbursement disbursement : finScheduleData.getDisbursementDetails()) {
			if(disbursement.getInstructionUID() == Long.MIN_VALUE) {
				disbursement.setDeductFromDisb(deductFeeFromDisbTot);
			}
		}

		for (FinFeeDetail fee : fees) {
			this.finFeeDetailService.calculateFees(fee, fm, gstExecutionMap);
		}

		logger.debug("Leaving");
		return receiptData;
	}

	private void calculateFeeRules(FinReceiptData receiptData) {
		FinanceDetail financeDetail = receiptData.getFinanceDetail();
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		HashMap<String, Object> gstExecutionMap = (HashMap<String, Object>) finScheduleData.getGstExecutionMap();
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
				}
				if (financeMain != null && StringUtils.isNotBlank(financeMain.getFinReference())
						&& StringUtils.isNotBlank(financeDetail.getModuleDefiner())) {
					FinanceProfitDetail finProfitDetail = profitDetailsDAO
							.getFinProfitDetailsById(financeMain.getFinReference());
					if (finProfitDetail != null) {
						BigDecimal outStandingFeeBal = financeScheduleDetailDAO
								.getOutStandingBalFromFees(financeMain.getFinReference());
						executionMap.put("totalOutStanding", finProfitDetail.getTotalPftBal());
						executionMap.put("principalOutStanding", finProfitDetail.getTotalPriBal());
						executionMap.put("totOSExcludeFees",
								finProfitDetail.getTotalPftBal().add(finProfitDetail.getTotalPriBal()));
						executionMap.put("totOSIncludeFees", finProfitDetail.getTotalPftBal()
								.add(finProfitDetail.getTotalPriBal()).add(outStandingFeeBal));
						executionMap.put("unearnedAmount", finProfitDetail.getUnearned());
					}
				}

				if (receiptData != null) {
					executionMap.put("totalPayment", receiptData.getTotReceiptAmount());
					executionMap.put("partialPaymentAmount", receiptData.getReceiptHeader().getPartPayAmount());
					executionMap.put("totalDueAmount", receiptData.getTotalDueAmount());

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
								gstExecutionMap, false);
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

	private void calculateFeePercentageAmount(FinReceiptData receiptData) {
		logger.debug("Entering");
		FinanceDetail financeDetail = receiptData.getFinanceDetail();
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		List<FinFeeDetail> finFees = finScheduleData.getFinFeeDetailList();

		if (finFees == null || finFees.isEmpty()) {
			logger.debug("Leaving");
			return;
		}

		Map<String, Object> gstExecutionMap = new HashMap<>();
		if (!financeDetail.getFinScheduleData().getGstExecutionMap().isEmpty()) {
			gstExecutionMap = (HashMap<String, Object>) financeDetail.getFinScheduleData().getGstExecutionMap();
		} else {
			gstExecutionMap = getGstMappingDetails(financeDetail);
			financeDetail.getFinScheduleData().setGstExecutionMap(gstExecutionMap);
		}

		for (FinFeeDetail finFee : finFees) {
			if (StringUtils.equals(finFee.getCalculationType(), PennantConstants.FEE_CALCULATION_TYPE_PERCENTAGE)) {

				BigDecimal calPercentageFee = getCalculatedPercentageFee(finFee, receiptData);
				finFee.setCalculatedAmount(calPercentageFee);

				if (StringUtils.equals(finFee.getFeeScheduleMethod(), CalculationConstants.REMFEE_WAIVED_BY_BANK)) {
					finFee.setWaivedAmount(calPercentageFee);
				}

				if (finFee.isTaxApplicable()) { // if GST applicable
					this.finFeeDetailService.processGSTCalForPercentage(finFee, calPercentageFee, financeDetail,
							gstExecutionMap, false);
				} else {
					if (!finFee.isFeeModified() || !finFee.isAlwModifyFee()) {
						finFee.setActualAmountOriginal(calPercentageFee);
						finFee.setActualAmountGST(BigDecimal.ZERO);
						finFee.setActualAmount(calPercentageFee);
					}
					finFee.setRemainingFee(finFee.getActualAmount().subtract(finFee.getPaidAmount())
							.subtract(finFee.getWaivedAmount()));
				}
			}
		}

		logger.debug("Leaving");
	}

	private BigDecimal getCalculatedPercentageFee(FinFeeDetail finFeeDetail, FinReceiptData receiptData) {
		logger.debug("Entering");
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
			calculatedAmt = finPftDetail.getTotalPriBal().subtract(finPftDetail.getTdSchdPriBal());
			break;
		case PennantConstants.FEE_CALCULATEDON_PAYAMOUNT:
			calculatedAmt = receiptData.getReceiptHeader().getPartPayAmount();
			break;

		// ### 11-07-2018 - PSD Ticket ID : 127846
		case PennantConstants.FEE_CALCULATEDON_DROPLINEPOS:
			calculatedAmt = getDropLinePOS(DateUtility.getAppDate(), finScheduleData);
			break;

		default:
			break;
		}

		calculatedAmt = calculatedAmt.multiply(finFeeDetail.getPercentage()).divide(BigDecimal.valueOf(100), 2,
				RoundingMode.HALF_DOWN);
		calculatedAmt = CalculationUtil.roundAmount(calculatedAmt, financeMain.getCalRoundingMode(),
				financeMain.getRoundingTarget());

		logger.debug("Leaving");

		return calculatedAmt;
	}
	
	public Map<String, Object> getGSTExecutionMap(String finBranch, String custBranch, String custState,
			String custCountry, FinanceTaxDetail finTax) {

		return finFeeDetailService.prepareGstMappingDetails(finBranch, custBranch, custState, custCountry, finTax, "");

	}

	public Map<String, Object> getGstMappingDetails(FinanceDetail financeDetail) {

		// String branch = getUserWorkspace().getLoggedInUser().getBranchCode();
		String branch = "";
		String finBranch = financeDetail.getFinScheduleData().getFinanceMain().getFinBranch();

		String custBranch = null;
		String custState = null;
		String custCountry = null;
		if (financeDetail.getCustomerDetails() != null) {
			custBranch = financeDetail.getCustomerDetails().getCustomer().getCustDftBranch();

			List<CustomerAddres> addressList = financeDetail.getCustomerDetails().getAddressList();
			if (CollectionUtils.isNotEmpty(addressList)) {
				for (CustomerAddres customerAddres : addressList) {
					if (customerAddres.getCustAddrPriority() == Integer
							.valueOf(PennantConstants.KYC_PRIORITY_VERY_HIGH)) {
						custState = customerAddres.getCustAddrProvince();
						custCountry = customerAddres.getCustAddrCountry();
						break;
					}
				}
			}
		}

		return getGSTExecutionMap(finBranch, custBranch, custState, custCountry, financeDetail.getFinanceTaxDetail());
	}
	
	//  ### 11-07-2018 - Start - PSD Ticket ID : 127846
	/**
	 * Method to get DroplinePOS.
	 * 
	 * @param valueDate
	 * @param finScheduleData
	 * 
	 */
	public  BigDecimal getDropLinePOS(Date valueDate, FinScheduleData finScheduleData) {

		BigDecimal dropLinePOS = BigDecimal.ZERO;
		String finReference = finScheduleData.getFinanceMain().getFinReference();
		List<FinanceScheduleDetail> scheduleList = finScheduleData.getFinanceScheduleDetails();

		if (scheduleList == null || scheduleList.isEmpty()) {
			scheduleList =financeScheduleDetailDAO.getFinSchdDetailsForBatch(finReference);
		}

		for (FinanceScheduleDetail schedule : scheduleList) {
			dropLinePOS = schedule.getODLimit();
			if (valueDate.compareTo(schedule.getSchDate()) <= 0) {
				break;
			}
		}

		return dropLinePOS;
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
}