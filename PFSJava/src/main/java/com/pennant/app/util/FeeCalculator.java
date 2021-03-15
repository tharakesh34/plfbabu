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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.JountAccountDetailDAO;
import com.pennant.backend.dao.rmtmasters.FinTypeFeesDAO;
import com.pennant.backend.dao.rulefactory.RuleDAO;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.finance.FinFeeConfig;
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
	private static Logger logger = LogManager.getLogger(FeeCalculator.class);

	private FinFeeDetailService finFeeDetailService;
	private FinanceProfitDetailDAO profitDetailsDAO;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private RuleDAO ruleDAO;
	private FinanceDetailService financeDetailService;
	private JountAccountDetailDAO jountAccountDetailDAO;
	private FinTypeFeesDAO finTypeFeesDAO;

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
		FinanceMain fm = finScheduleData.getFinanceMain();
		List<FinFeeDetail> feeList = new ArrayList<FinFeeDetail>();

		if (ImplementationConstants.FEE_CAL_ON_RULE) {
			calculateFeeOnRule(receiptData, feeList);
			receiptData.getFinanceDetail().getFinScheduleData().setFinFeeDetailList(feeList);
			return receiptData;
		}

		FinFeeDetail fee = null;
		if (finTypeFeesList == null || finTypeFeesList.isEmpty()) {
			receiptData.getFinanceDetail().getFinScheduleData().setFinFeeDetailList(feeList);
			logger.debug("Leaving ");
			return receiptData;
		}

		for (FinTypeFees feeType : finTypeFeesList) {
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
			fee.setTdsReq(feeType.isTdsReq());

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
				this.finFeeDetailService.convertGSTFinTypeFees(fee, feeType, financeDetail, taxPercentages);
			} else {
				fee.setActualAmountOriginal(feeType.getAmount());
				fee.setActualAmountGST(BigDecimal.ZERO);
				fee.setActualAmount(feeType.getAmount());

				BigDecimal netAmountOriginal = fee.getActualAmountOriginal().subtract(fee.getWaivedAmount());

				fee.setNetAmountOriginal(netAmountOriginal);
				fee.setNetAmountGST(BigDecimal.ZERO);
				fee.setNetAmount(netAmountOriginal);

				if (StringUtils.equals(feeType.getFeeScheduleMethod(), CalculationConstants.REMFEE_PAID_BY_CUSTOMER)) {
					fee.setPaidAmountOriginal(feeType.getAmount());
					fee.setPaidAmountGST(BigDecimal.ZERO);
					fee.setPaidAmount(feeType.getAmount());
				}

				if (StringUtils.equals(feeType.getFeeScheduleMethod(), CalculationConstants.REMFEE_WAIVED_BY_BANK)) {
					fee.setWaivedAmount(feeType.getAmount());
				}

				fee.setRemainingFeeOriginal(
						fee.getActualAmount().subtract(fee.getWaivedAmount()).subtract(fee.getPaidAmount()));
				fee.setRemainingFeeGST(BigDecimal.ZERO);
				fee.setRemainingFee(
						fee.getActualAmount().subtract(fee.getWaivedAmount()).subtract(fee.getPaidAmount()));
			}

			feeList.add(fee);
		}

		receiptData.getFinanceDetail().getFinScheduleData().setFinFeeDetailList(feeList);
		logger.debug("Leaving ");

		return receiptData;
	}

	private FinReceiptData calculateFeeOnRule(FinReceiptData receiptData, List<FinFeeDetail> feeList) {
		FinanceDetail financeDetail = receiptData.getFinanceDetail();
		List<FinTypeFees> finTypeFeesList = financeDetail.getFinTypeFeesList();
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		FinanceMain fm = finScheduleData.getFinanceMain();

		List<FinFeeConfig> feeConfigList = financeDetail.getFinFeeConfigList();

		if (CollectionUtils.isEmpty(feeConfigList)) {
			return receiptData;
		}

		FinFeeDetail fee = null;
		HashMap<String, Object> gstExecutionMap = new HashMap<>();
		if (!financeDetail.getFinScheduleData().getGstExecutionMap().isEmpty()) {
			gstExecutionMap = (HashMap<String, Object>) financeDetail.getFinScheduleData().getGstExecutionMap();
		} else {
			//gstExecutionMap = (HashMap<String, Object>) getGstMappingDetails(financeDetail);
			gstExecutionMap = (HashMap<String, Object>) GSTCalculator.getGSTDataMap(fm.getFinReference());
			financeDetail.getFinScheduleData().setGstExecutionMap(gstExecutionMap);
		}
		List<Object> objectList = new ArrayList<Object>();
		int retailCount = 0;
		int corpCount = 0;
		int smeCount = 0;
		HashMap<String, Object> executionMap = new HashMap<String, Object>();
		CustomerDetails customerDetails = financeDetail.getCustomerDetails();
		executionMap.put("custCtgCode", "");
		if (customerDetails != null) {
			Customer customer = customerDetails.getCustomer();
			executionMap.put("custCtgCode", customer.getCustCtgCode());
			objectList.add(customer);
			if (PennantConstants.PFF_CUSTCTG_INDIV.equals(customer.getCustCtgCode())) {
				retailCount++;
			} else if (PennantConstants.PFF_CUSTCTG_CORP.equals(customer.getCustCtgCode())) {
				corpCount++;
			} else if (PennantConstants.PFF_CUSTCTG_SME.equals(customer.getCustCtgCode())) {
				smeCount++;
			}
		}
		Map<String, Integer> custCtgCount = jountAccountDetailDAO.getCustCtgCount(fm.getFinReference());
		if (custCtgCount != null) {
			if (custCtgCount.containsKey(PennantConstants.PFF_CUSTCTG_INDIV)) {
				retailCount = retailCount + custCtgCount.get(PennantConstants.PFF_CUSTCTG_INDIV);
			}
			if (custCtgCount.containsKey(PennantConstants.PFF_CUSTCTG_CORP)) {
				corpCount = corpCount + custCtgCount.get(PennantConstants.PFF_CUSTCTG_CORP);
			}
			if (custCtgCount.containsKey(PennantConstants.PFF_CUSTCTG_SME)) {
				smeCount = smeCount + custCtgCount.get(PennantConstants.PFF_CUSTCTG_SME);
			}
		}

		prepareExecutionMap(receiptData, fm, executionMap);
		//To Be configured based on requirement. Merged from BHFL trunk revision : /Products/PFF/bajaj/BHFL/trunk/ 130332
		BigDecimal dropLineAmt = finFeeDetailService.calDropLineLPOS(finScheduleData, SysParamUtil.getAppDate());
		executionMap.put("dropLineAmt", dropLineAmt);

		objectList.add(fm);
		for (FinFeeConfig finFeeConfig : feeConfigList) {
			fee = new FinFeeDetail();
			fee.setNewRecord(true);
			fee.setOriginationFee(finFeeConfig.isOriginationFee());
			fee.setFinEvent(finFeeConfig.getFinEvent());
			fee.setFinEventDesc(finFeeConfig.getFinEventDesc());
			fee.setFeeTypeID(finFeeConfig.getFeeTypeID());
			fee.setFeeOrder(finFeeConfig.getFeeOrder());
			fee.setFeeTypeCode(finFeeConfig.getFeeTypeCode());
			fee.setFeeTypeDesc(finFeeConfig.getFeeTypeDesc());
			fee.setFeeScheduleMethod(finFeeConfig.getFeeScheduleMethod());
			fee.setCalculationType(finFeeConfig.getCalculationType());
			fee.setRuleCode(finFeeConfig.getRuleCode());
			//fee.setTdsReq(finFeeConfig.isTdsReq());

			BigDecimal finAmount = CalculationUtil.roundAmount(finFeeConfig.getAmount(), fm.getCalRoundingMode(),
					fm.getRoundingTarget());
			finFeeConfig.setAmount(finAmount);

			fee.setFixedAmount(finFeeConfig.getAmount());
			fee.setPercentage(finFeeConfig.getPercentage());
			fee.setCalculateOn(finFeeConfig.getCalculateOn());
			fee.setAlwDeviation(finFeeConfig.isAlwDeviation());
			fee.setMaxWaiverPerc(finFeeConfig.getMaxWaiverPerc());
			fee.setAlwModifyFee(finFeeConfig.isAlwModifyFee());
			fee.setAlwModifyFeeSchdMthd(finFeeConfig.isAlwModifyFeeSchdMthd());
			fee.setCalculatedAmount(finFeeConfig.getAmount());
			fee.setTaxComponent(finFeeConfig.getTaxComponent());
			fee.setTaxApplicable(finFeeConfig.isTaxApplicable());

			if (PennantConstants.PERC_TYPE_VARIABLE.equals(finFeeConfig.getPercType())
					&& StringUtils.isNotBlank(finFeeConfig.getPercRule())) {
				BigDecimal feeResult = BigDecimal.ZERO;

				Rule feeRules = ruleDAO.getRuleByID(finFeeConfig.getPercRuleId(), "");
				if (feeRules != null) {
					finFeeConfig.setPercRuleId(feeRules.getRuleId());
					Map<String, String> ruleSqlMap = new HashMap<String, String>();
					ruleSqlMap.put(feeRules.getRuleCode(), feeRules.getSQLRule());
					if (feeRules.getFields() != null) {
						String[] fields = feeRules.getFields().split(",");
						for (String field : fields) {
							if (!executionMap.containsKey(field)) {
								RuleExecutionUtil.setExecutionMap(field, objectList, executionMap);
							}
						}
					}
					executionMap.put("RetailCount", retailCount);
					executionMap.put("CorpCount", corpCount);
					executionMap.put("SmeCount", smeCount);
					feeResult = this.finFeeDetailService.getFeeResult(ruleSqlMap.get(finFeeConfig.getPercRule()),
							executionMap, fm.getFinCcy());
				}
				fee.setPercentage(feeResult);
			}

			if (finFeeConfig.isTaxApplicable()) {
				Map<String, BigDecimal> taxPercentages = GSTCalculator.getTaxPercentages(fm.getFinReference());
				this.finFeeDetailService.convertGSTFinFeeConfig(fee, finFeeConfig, financeDetail, taxPercentages);
			} else {
				fee.setActualAmountOriginal(finFeeConfig.getAmount());
				fee.setActualAmountGST(BigDecimal.ZERO);
				fee.setActualAmount(finFeeConfig.getAmount());

				BigDecimal netAmountOriginal = fee.getActualAmountOriginal().subtract(fee.getWaivedAmount());

				fee.setNetAmountOriginal(netAmountOriginal);
				fee.setNetAmountGST(BigDecimal.ZERO);
				fee.setNetAmount(netAmountOriginal);

				if (StringUtils.equals(finFeeConfig.getFeeScheduleMethod(),
						CalculationConstants.REMFEE_PAID_BY_CUSTOMER)) {
					fee.setPaidAmountOriginal(finFeeConfig.getAmount());
					fee.setPaidAmountGST(BigDecimal.ZERO);
					fee.setPaidAmount(finFeeConfig.getAmount());
				}

				if (StringUtils.equals(finFeeConfig.getFeeScheduleMethod(),
						CalculationConstants.REMFEE_WAIVED_BY_BANK)) {
					fee.setWaivedAmount(finFeeConfig.getAmount());
				}

				fee.setRemainingFeeOriginal(
						fee.getActualAmount().subtract(fee.getWaivedAmount()).subtract(fee.getPaidAmount()));
				fee.setRemainingFeeGST(BigDecimal.ZERO);
				fee.setRemainingFee(
						fee.getActualAmount().subtract(fee.getWaivedAmount()).subtract(fee.getPaidAmount()));
			}

			feeList.add(fee);
		}
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
								RuleExecutionUtil.setExecutionMap(field, objectList, executionMap);
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

				int instNO = 0;
				for (FinanceScheduleDetail detail : finScheduleData.getFinanceScheduleDetails()) {
					if (detail.getSchDate().compareTo(DateUtility.getAppDate()) <= 0) {
						instNO = detail.getInstNumber();
					} else {
						break;
					}
				}

				executionMap.put("completedInstallments", instNO);

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
						// Fore closure charges calculation should be sum of principal amount and future principal amount.
						executionMap.put("principalAmtFutPrincipalAmt", finProfitDetail.getTotalPriBal());
						executionMap.put("totOSExcludeFees",
								finProfitDetail.getTotalPftBal().add(finProfitDetail.getTotalPriBal()));
						executionMap.put("totOSIncludeFees", finProfitDetail.getTotalPftBal()
								.add(finProfitDetail.getTotalPriBal()).add(outStandingFeeBal));
						executionMap.put("unearnedAmount", finProfitDetail.getUnearned());
						executionMap.put("eligibilityMethod", financeMain.getEligibilityMethod());
					}

					if (receiptData.isForeClosureEnq()) {
						executionMap.put("principalOutStanding", finProfitDetail.getTotalPriBal()
								.subtract(receiptData.getOrgFinPftDtls().getTdSchdPriBal()));
						// Fore closure charges calculation should be sum of principal amount and future principal amount.
						executionMap.put("principalAmtFutPrincipalAmt", finProfitDetail.getTotalPriBal());
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
							&& fixedTenorEndDate.compareTo(SysParamUtil.getAppDate()) > 0) {
						executionMap.put("Finance_Fixed_Tenor", PennantConstants.YES);
					} else {
						executionMap.put("Finance_Fixed_Tenor", PennantConstants.NO);
					}
				}
				prepareExecutionMap(receiptData, financeMain, executionMap);
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

		//TDS
		dataMap.put(feeTypeCode + "_TDS_N", finFeeDetail.getNetTDS());
		dataMap.put(feeTypeCode + "_TDS_P", finFeeDetail.getPaidTDS());

		return dataMap;
	}

	private void prepareExecutionMap(FinReceiptData receiptData, FinanceMain fm, Map<String, Object> dataMap) {
		Date appDate = SysParamUtil.getAppDate();

		if (fm != null && fm.getFinStartDate() != null) {
			int finAge = DateUtility.getMonthsBetween(appDate, fm.getFinStartDate());
			dataMap.put("finAgetilldate", finAge);
		}

		if (fm != null && StringUtils.isNotBlank(fm.getFinReference())) {
			FinanceProfitDetail finPft = profitDetailsDAO.getFinProfitDetailsById(fm.getFinReference());
			if (finPft != null) {
				BigDecimal outStandingFeeBal = financeScheduleDetailDAO.getOutStandingBalFromFees(fm.getFinReference());
				dataMap.put("totalOutStanding", finPft.getTotalPftBal());
				dataMap.put("principalOutStanding", finPft.getTotalpriSchd().subtract(finPft.getTdSchdPri()));
				if (receiptData.isForeClosureEnq()) {
					dataMap.put("principalOutStanding",
							finPft.getTotalPriBal().subtract(receiptData.getOrgFinPftDtls().getTdSchdPriBal()));
				}
				dataMap.put("principalSchdOutstanding", finPft.getTotalpriSchd().subtract(finPft.getTdSchdPri()));
				dataMap.put("totOSExcludeFees", finPft.getTotalPftBal().add(finPft.getTotalPriBal()));
				dataMap.put("totOSIncludeFees",
						finPft.getTotalPftBal().add(finPft.getTotalPriBal()).add(outStandingFeeBal));
				dataMap.put("unearnedAmount", finPft.getUnearned());
			}
		}

		if (receiptData != null) {
			dataMap.put("totalPayment", receiptData.getTotReceiptAmount());
			dataMap.put("partialPaymentAmount", receiptData.getReceiptHeader().getPartPayAmount());
			dataMap.put("totalDueAmount", receiptData.getTotalDueAmount());

			Date fixedTenorEndDate = DateUtility.addMonths(fm.getGrcPeriodEndDate(), fm.getFixedRateTenor());

			if (fm.getFixedRateTenor() > 0 && fixedTenorEndDate.compareTo(appDate) > 0) {
				dataMap.put("Finance_Fixed_Tenor", PennantConstants.YES);
			} else {
				dataMap.put("Finance_Fixed_Tenor", PennantConstants.NO);
			}
		}
		if (fm != null) {
			if (StringUtils.isBlank(fm.getRepayBaseRate())) {
				dataMap.put("rateType", "FIXED");
			} else {
				dataMap.put("rateType", "FLOATING");
			}

		}
	}

	public List<FinFeeConfig> convertToFinanceFees(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);

		List<FinFeeConfig> finfeeDetailConfigList = new ArrayList<FinFeeConfig>();
		FinFeeConfig feeConfig;
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		FinanceMain financeMain = finScheduleData.getFinanceMain();
		List<FinTypeFees> feeTypes = finTypeFeesDAO.getFinTypeFeesList(financeMain.getFinType(), false, "_AView");

		if (CollectionUtils.isEmpty(feeTypes)) {
			return finfeeDetailConfigList;
		}

		for (FinTypeFees feeType : feeTypes) {
			feeConfig = new FinFeeConfig();
			feeConfig.setFinReference(financeMain.getFinReference());
			feeConfig.setOriginationFee(feeType.isOriginationFee());
			feeConfig.setFinEvent(feeType.getFinEvent());
			feeConfig.setFeeTypeID(feeType.getFeeTypeID());
			feeConfig.setFeeOrder(feeType.getFeeOrder());
			feeConfig.setFeeScheduleMethod(feeType.getFeeScheduleMethod());
			feeConfig.setCalculationType(feeType.getCalculationType());
			feeConfig.setRuleCode(feeType.getRuleCode());
			feeConfig.setAmount(feeType.getAmount());
			feeConfig.setPercentage(feeType.getPercentage());
			feeConfig.setCalculateOn(feeType.getCalculateOn());
			feeConfig.setAlwDeviation(feeType.isAlwDeviation());
			feeConfig.setAlwModifyFeeSchdMthd(feeType.isAlwModifyFeeSchdMthd());
			feeConfig.setAlwModifyFee(feeType.isAlwModifyFee());
			feeConfig.setMaxWaiverPerc(feeType.getMaxWaiverPerc());
			feeConfig.setModuleId(feeType.getModuleId());
			feeConfig.setReferenceId(feeType.getReferenceId());
			feeConfig.setFinTypeFeeId(feeType.getFinTypeFeeId());
			feeConfig.setAlwPreIncomization(feeType.isAlwPreIncomization());
			feeConfig.setPercType(feeType.getPercType());
			feeConfig.setPercRule(feeType.getPercRule());
			feeConfig.setPercRuleId(0);

			if (PennantConstants.PERC_TYPE_VARIABLE.equals(feeType.getPercType())
					&& StringUtils.isNotBlank(feeType.getPercRule())) {
				Rule feeRules = ruleDAO.getActiveRuleByID(feeConfig.getPercRule(), RuleConstants.MODULE_FEEPERC,
						feeConfig.getFinEvent(), "", true);
				if (feeRules != null) {
					feeConfig.setPercRuleId(feeRules.getRuleId());
				}
			}
			feeConfig.setOriginationFee(feeType.isOriginationFee());
			finfeeDetailConfigList.add(feeConfig);
		}
		logger.debug(Literal.LEAVING);
		return finfeeDetailConfigList;
	}

	public void setFinFeeDetailService(FinFeeDetailService finFeeDetailService) {
		this.finFeeDetailService = finFeeDetailService;
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

	public JountAccountDetailDAO getJountAccountDetailDAO() {
		return jountAccountDetailDAO;
	}

	public void setJountAccountDetailDAO(JountAccountDetailDAO jountAccountDetailDAO) {
		this.jountAccountDetailDAO = jountAccountDetailDAO;
	}

	public FinTypeFeesDAO getFinTypeFeesDAO() {
		return finTypeFeesDAO;
	}

	public void setFinTypeFeesDAO(FinTypeFeesDAO finTypeFeesDAO) {
		this.finTypeFeesDAO = finTypeFeesDAO;
	}

}