package com.pennant.app.util;

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
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.JointAccountDetailDAO;
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
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.constants.FinServiceEvent;

public class FeeCalculator {
	private static Logger logger = LogManager.getLogger(FeeCalculator.class);

	private FinFeeDetailService finFeeDetailService;
	private FinanceProfitDetailDAO profitDetailsDAO;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private RuleDAO ruleDAO;
	private FinanceDetailService financeDetailService;
	private JointAccountDetailDAO jointAccountDetailDAO;
	private FinTypeFeesDAO finTypeFeesDAO;

	public FinReceiptData calculateFees(FinReceiptData rd) {
		long finID = rd.getFinID();
		String finReference = rd.getFinReference();
		Map<String, BigDecimal> taxPercentages = GSTCalculator.getTaxPercentages(finID);

		convertToFinanceFees(rd, taxPercentages);

		FinanceDetail fd = rd.getFinanceDetail();
		FinScheduleData schdData = fd.getFinScheduleData();
		List<FinFeeDetail> finFeeDetailList = schdData.getFinFeeDetailList();

		if (CollectionUtils.isNotEmpty(finFeeDetailList)) {
			calculateFeeDetail(rd, taxPercentages);
		}

		return rd;
	}

	private void convertToFinanceFees(FinReceiptData rd, Map<String, BigDecimal> taxPercentages) {
		logger.debug(Literal.ENTERING);

		FinanceDetail fd = rd.getFinanceDetail();
		List<FinTypeFees> finTypeFeesList = fd.getFinTypeFeesList();
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();
		List<FinFeeDetail> feeList = new ArrayList<FinFeeDetail>();

		List<FinFeeConfig> feeConfigList = fd.getFinFeeConfigList();

		if (CollectionUtils.isNotEmpty(feeConfigList)) {
			feeList.addAll(calculateFeeOnRule(rd));
			schdData.setFinFeeDetailList(feeList);
			return;
		}

		if (finTypeFeesList == null || finTypeFeesList.isEmpty()) {
			schdData.setFinFeeDetailList(feeList);
			logger.debug(Literal.LEAVING);
			return;
		}

		String calRoundingMode = fm.getCalRoundingMode();
		int roundingTarget = fm.getRoundingTarget();

		for (FinTypeFees feeType : finTypeFeesList) {
			String feeScheduleMethod = feeType.getFeeScheduleMethod();
			FinFeeDetail fee = new FinFeeDetail();
			fee.setNewRecord(true);
			fee.setOriginationFee(feeType.isOriginationFee());
			fee.setFinEvent(feeType.getFinEvent());
			fee.setFinEventDesc(feeType.getFinEventDesc());
			fee.setFeeTypeID(feeType.getFeeTypeID());
			fee.setFeeOrder(feeType.getFeeOrder());
			fee.setFeeTypeCode(feeType.getFeeTypeCode());
			fee.setFeeTypeDesc(feeType.getFeeTypeDesc());

			fee.setFeeScheduleMethod(feeScheduleMethod);
			fee.setCalculationType(feeType.getCalculationType());
			fee.setRuleCode(feeType.getRuleCode());
			fee.setTdsReq(feeType.isTdsReq());

			BigDecimal finAmount = CalculationUtil.roundAmount(feeType.getAmount(), calRoundingMode, roundingTarget);
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
				this.finFeeDetailService.convertGSTFinTypeFees(fee, feeType, fd, taxPercentages);
			} else {
				fee.setActualAmountOriginal(feeType.getAmount());
				fee.setActualAmountGST(BigDecimal.ZERO);
				fee.setActualAmount(feeType.getAmount());

				BigDecimal waivedAmount = fee.getWaivedAmount();
				BigDecimal netAmountOriginal = fee.getActualAmountOriginal().subtract(waivedAmount);

				fee.setNetAmountOriginal(netAmountOriginal);
				fee.setNetAmountGST(BigDecimal.ZERO);
				fee.setNetAmount(netAmountOriginal);

				if (CalculationConstants.REMFEE_PAID_BY_CUSTOMER.equals(feeScheduleMethod)) {
					fee.setPaidAmountOriginal(feeType.getAmount());
					fee.setPaidAmountGST(BigDecimal.ZERO);
					fee.setPaidAmount(feeType.getAmount());
				}

				if (CalculationConstants.REMFEE_WAIVED_BY_BANK.equals(feeScheduleMethod)) {
					fee.setWaivedAmount(feeType.getAmount());
				}

				BigDecimal actualAmount = fee.getActualAmount();
				BigDecimal paidAmount = fee.getPaidAmount();

				fee.setRemainingFeeOriginal(actualAmount.subtract(waivedAmount).subtract(paidAmount));
				fee.setRemainingFeeGST(BigDecimal.ZERO);
				fee.setRemainingFee(actualAmount.subtract(waivedAmount).subtract(paidAmount));
			}

			feeList.add(fee);
		}

		schdData.setFinFeeDetailList(feeList);
		logger.debug(Literal.LEAVING);
	}

	private List<FinFeeDetail> calculateFeeOnRule(FinReceiptData receiptData) {
		List<FinFeeDetail> list = new ArrayList<>();

		FinanceDetail fd = receiptData.getFinanceDetail();
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		List<FinFeeConfig> feeConfigList = fd.getFinFeeConfigList();

		Map<String, Object> gstExecutionMap = new HashMap<>();
		if (!schdData.getGstExecutionMap().isEmpty()) {
			gstExecutionMap = schdData.getGstExecutionMap();
		} else {
			gstExecutionMap = GSTCalculator.getGSTDataMap(fm.getFinID());
			schdData.setGstExecutionMap(gstExecutionMap);
		}
		List<Object> objectList = new ArrayList<Object>();
		int retailCount = 0;
		int corpCount = 0;
		int smeCount = 0;

		Map<String, Object> executionMap = new HashMap<String, Object>();
		CustomerDetails customerDetails = fd.getCustomerDetails();
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

		Map<String, Integer> custCtgCount = jointAccountDetailDAO.getCustCtgCount(fm.getFinID());
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
		// To Be configured based on requirement. Merged from BHFL trunk revision : /Products/PFF/bajaj/BHFL/trunk/
		// 130332
		BigDecimal dropLineAmt = finFeeDetailService.calDropLineLPOS(schdData, SysParamUtil.getAppDate());
		executionMap.put("dropLineAmt", dropLineAmt);

		objectList.add(fm);

		for (FinFeeConfig finFeeConfig : feeConfigList) {
			FinFeeDetail fee = new FinFeeDetail();
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
			// fee.setTdsReq(finFeeConfig.isTdsReq());

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
				Map<String, BigDecimal> taxPercentages = GSTCalculator.getTaxPercentages(fm.getFinID());
				this.finFeeDetailService.convertGSTFinFeeConfig(fee, finFeeConfig, fd, taxPercentages);
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

			list.add(fee);
		}

		return list;
	}

	public void calculateFeeDetail(FinReceiptData receiptData, Map<String, BigDecimal> taxPercentages) {
		logger.debug(Literal.ENTERING);
		FinanceDetail financeDetail = receiptData.getFinanceDetail();
		FinScheduleData schdData = financeDetail.getFinScheduleData();

		// Calculate Fee Rules
		calculateFeeRules(receiptData, taxPercentages);

		// Calculate the fee Percentage
		calculateFeePercentageAmount(receiptData, taxPercentages);

		List<FinFeeDetail> fees = schdData.getFinFeeDetailList();

		BigDecimal deductFeeFromDisbTot = BigDecimal.ZERO;
		BigDecimal feeAddToDisbTot = BigDecimal.ZERO;

		FinanceMain fm = schdData.getFinanceMain();
		long finID = fm.getFinID();
		String finReference = fm.getFinReference();

		for (FinFeeDetail fee : fees) {
			this.finFeeDetailService.calculateFees(fee, fm, taxPercentages);

			switch (fee.getFeeScheduleMethod()) {
			case CalculationConstants.REMFEE_PART_OF_DISBURSE:
				deductFeeFromDisbTot = deductFeeFromDisbTot.add(fee.getRemainingFee());
				break;
			case CalculationConstants.REMFEE_PART_OF_SALE_PRICE:
				feeAddToDisbTot = feeAddToDisbTot.add(fee.getRemainingFee());
				break;
			case CalculationConstants.REMFEE_PAID_BY_CUSTOMER:
				if (fee.getPaidAmount().compareTo(BigDecimal.ZERO) == 0) {
					fee.setPaidAmount(fee.getActualAmount());
				}
				break;
			case CalculationConstants.REMFEE_WAIVED_BY_BANK:
				if (fee.getWaivedAmount().compareTo(BigDecimal.ZERO) == 0) {
					fee.setWaivedAmount(fee.getActualAmount());
				}
				break;
			default:
				break;
			}

			fee.setRemainingFee(fee.getActualAmount().subtract(fee.getPaidAmount()).subtract(fee.getWaivedAmount()));
		}

		if (FinServiceEvent.ORG.equals(financeDetail.getModuleDefiner())) {
			fm.setDeductFeeDisb(deductFeeFromDisbTot);
			fm.setFeeChargeAmt(feeAddToDisbTot);
			logger.debug(Literal.LEAVING);
			return;
		}

		if (CollectionUtils.isEmpty(schdData.getDisbursementDetails())) {
			logger.debug(Literal.LEAVING);
			return;
		}

		List<Integer> approvedDisbSeq = financeDetailService.getFinanceDisbSeqs(finID, false);
		for (FinanceDisbursement disbursement : schdData.getDisbursementDetails()) {
			if (!approvedDisbSeq.contains(disbursement.getDisbSeq())) {
				disbursement.setDeductFeeDisb(deductFeeFromDisbTot);
				break;
			}
		}

		logger.debug(Literal.LEAVING);
	}

	private void calculateFeeRules(FinReceiptData rd, Map<String, BigDecimal> taxPercentages) {
		FinanceDetail financeDetail = rd.getFinanceDetail();
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		List<FinFeeDetail> finFeeDetailsList = finScheduleData.getFinFeeDetailList();
		List<String> feeRuleCodes = new ArrayList<>();
		FinanceMain fm = finScheduleData.getFinanceMain();
		for (FinFeeDetail finFeeDetail : finFeeDetailsList) {
			if (StringUtils.isNotEmpty(finFeeDetail.getRuleCode())) {
				feeRuleCodes.add(finFeeDetail.getRuleCode());
			}
		}

		if (feeRuleCodes.isEmpty()) {
			return;
		}

		String feeEvent = finScheduleData.getFeeEvent();
		List<Rule> feeRules = ruleDAO.getRuleDetailList(feeRuleCodes, RuleConstants.MODULE_FEES, feeEvent);

		if (feeRules.isEmpty()) {
			return;
		}

		Map<String, Object> dataMap = new HashMap<>();
		Map<String, String> ruleSqlMap = new HashMap<>();
		List<Object> objectList = new ArrayList<>();

		CustomerDetails custDetails = financeDetail.getCustomerDetails();
		if (custDetails != null) {
			objectList.add(custDetails.getCustomer());
			if (custDetails.getCustEmployeeDetail() != null) {
				objectList.add(custDetails.getCustEmployeeDetail());
			}
			List<CustomerAddres> addressList = custDetails.getAddressList();

			String custAddrProvince = "";
			for (CustomerAddres customerAddres : addressList) {
				if (customerAddres.getCustAddrPriority() == Integer.valueOf(PennantConstants.KYC_PRIORITY_VERY_HIGH)) {
					custAddrProvince = customerAddres.getCustAddrProvince();
					break;
				}
			}

			dataMap.put("customerProvince", custAddrProvince);
		}

		if (financeDetail.getFinScheduleData() != null) {
			objectList.add(finScheduleData.getFinanceMain());
			objectList.add(financeDetail.getFinScheduleData().getFinanceType());
		}

		for (Rule feeRule : feeRules) {
			if (feeRule.getFields() != null) {
				String[] fields = feeRule.getFields().split(",");
				for (String field : fields) {
					if (!dataMap.containsKey(field)) {
						RuleExecutionUtil.setExecutionMap(field, objectList, dataMap);
					}
				}
			}
			ruleSqlMap.put(feeRule.getRuleCode(), feeRule.getSQLRule());
		}

		Date appDate = SysParamUtil.getAppDate();
		int finAge = 0;
		if (fm.getFinStartDate() != null) {
			finAge = DateUtility.getMonthsBetween(appDate, fm.getFinStartDate());
		}

		dataMap.put("finAgetilldate", finAge);
		dataMap.put("completedTenure", finAge);

		int instNO = 0;
		for (FinanceScheduleDetail detail : finScheduleData.getFinanceScheduleDetails()) {
			if (detail.getSchDate().compareTo(appDate) <= 0) {
				instNO = detail.getInstNumber();
			} else {
				break;
			}
		}

		dataMap.put("completedInstallments", instNO);

		long finID = fm.getFinID();

		if (StringUtils.isNotBlank(financeDetail.getModuleDefiner())) {
			FinanceProfitDetail pft = profitDetailsDAO.getFinProfitDetailsById(finID);
			BigDecimal outStandingFeeBal = financeScheduleDetailDAO.getOutStandingBalFromFees(finID);
			dataMap.put("totalOutStanding", pft.getTotalPftBal());
			// PSD: 138255 PrincipalOutStanding will be future
			// Amount to be paid.
			dataMap.put("principalOutStanding", pft.getTotalpriSchd().subtract(pft.getTdSchdPri()));

			dataMap.put("principalSchdOutstanding", pft.getTotalpriSchd().subtract(pft.getTdSchdPri()));
			// Fore closure charges calculation should be sum of principal amount and future principal amount.
			dataMap.put("principalAmtFutPrincipalAmt", pft.getTotalPriBal());
			dataMap.put("totOSExcludeFees", pft.getTotalPftBal().add(pft.getTotalPriBal()));
			dataMap.put("totOSIncludeFees", pft.getTotalPftBal().add(pft.getTotalPriBal()).add(outStandingFeeBal));
			dataMap.put("unearnedAmount", pft.getUnearned());
			dataMap.put("eligibilityMethod", fm.getEligibilityMethod());

			if (rd.isForeClosureEnq()) {
				dataMap.put("principalOutStanding",
						pft.getTotalPriBal().subtract(rd.getOrgFinPftDtls().getTdSchdPriBal()));
				// Fore closure charges calculation should be sum of principal amount and future principal amount.
				dataMap.put("principalAmtFutPrincipalAmt", pft.getTotalPriBal());
			}
		}

		dataMap.put("totalPayment", rd.getTotReceiptAmount());

		BigDecimal totalDues = BigDecimal.ZERO;

		if (rd.getTotalDueAmount().compareTo(BigDecimal.ZERO) > 0) {
			totalDues = rd.getTotalDueAmount();
		} else {
			FinReceiptHeader rch = rd.getReceiptHeader();
			totalDues = totalDues.add(rch.getTotalPastDues().getTotalDue());
			totalDues = totalDues.add(rch.getTotalBounces().getTotalDue());
			totalDues = totalDues.add(rch.getTotalRcvAdvises().getTotalDue());
			totalDues = totalDues.add(rch.getTotalFees().getTotalDue());
			totalDues = totalDues.subtract(rd.getExcessAvailable());
		}

		dataMap.put("totalDueAmount", totalDues);

		BigDecimal partialPaymentAmount = BigDecimal.ZERO;
		BigDecimal partPayAmount = rd.getReceiptHeader().getPartPayAmount();
		if ((partPayAmount.compareTo(totalDues) > 0)) {
			partialPaymentAmount = partPayAmount.subtract(totalDues);
		}

		dataMap.put("partialPaymentAmount", partialPaymentAmount);

		Date fixedTenorEndDate = DateUtility.addMonths(fm.getGrcPeriodEndDate(), fm.getFixedRateTenor());

		String financeFixedTenor = PennantConstants.NO;
		if (fm.getFixedRateTenor() > 0 && fixedTenorEndDate.compareTo(SysParamUtil.getAppDate()) > 0) {
			financeFixedTenor = PennantConstants.YES;
		}

		dataMap.put("Finance_Fixed_Tenor", financeFixedTenor);

		prepareExecutionMap(rd, fm, dataMap);
		String finCcy = fm.getFinCcy();
		int formatter = CurrencyUtil.getFormat(finCcy);

		for (FinFeeDetail finFeeDetail : finFeeDetailsList) {
			if (StringUtils.isEmpty(finFeeDetail.getRuleCode())) {
				continue;
			}

			BigDecimal feeResult = this.finFeeDetailService.getFeeResult(ruleSqlMap.get(finFeeDetail.getRuleCode()),
					dataMap, finCcy);
			// unFormating feeResult
			feeResult = PennantApplicationUtil.unFormateAmount(feeResult, formatter);

			finFeeDetail.setCalculatedAmount(feeResult);

			if (finFeeDetail.isTaxApplicable()) {
				this.finFeeDetailService.processGSTCalForRule(finFeeDetail, feeResult, financeDetail, taxPercentages,
						false);
			} else {
				if (!finFeeDetail.isFeeModified() || !finFeeDetail.isAlwModifyFee()) {
					finFeeDetail.setActualAmountOriginal(feeResult);
					finFeeDetail.setActualAmountGST(BigDecimal.ZERO);
					finFeeDetail.setActualAmount(feeResult);
				}

				finFeeDetail.setRemainingFee(finFeeDetail.getActualAmount().subtract(finFeeDetail.getPaidAmount())
						.subtract(finFeeDetail.getWaivedAmount()));
			}
		}

	}

	public void calculateFeePercentageAmount(FinReceiptData receiptData, Map<String, BigDecimal> taxPercentages) {
		logger.debug(Literal.ENTERING);
		FinanceDetail fd = receiptData.getFinanceDetail();
		FinScheduleData finScheduleData = fd.getFinScheduleData();
		List<FinFeeDetail> finFeeDetailList = finScheduleData.getFinFeeDetailList();

		if (CollectionUtils.isEmpty(finFeeDetailList)) {
			logger.debug(Literal.LEAVING);
			return;
		}

		for (FinFeeDetail fee : finFeeDetailList) {
			String calculationType = fee.getCalculationType();

			if (!PennantConstants.FEE_CALCULATION_TYPE_PERCENTAGE.equals(calculationType)) {
				continue;
			}

			receiptData.setEventFeePercent(true);
			receiptData.setCurEventFeePercent(fee.getPercentage());

			BigDecimal calPercentageFee = getCalculatedPercentageFee(fee, receiptData, taxPercentages);
			fee.setCalculatedAmount(calPercentageFee);

			if (CalculationConstants.REMFEE_WAIVED_BY_BANK.equals(fee.getFeeScheduleMethod())) {
				fee.setWaivedAmount(calPercentageFee);
			}

			if (fee.isTaxApplicable()) {
				this.finFeeDetailService.processGSTCalForPercentage(fee, calPercentageFee, fd, taxPercentages, false);
			} else {
				if (!fee.isFeeModified() || !fee.isAlwModifyFee()) {
					fee.setActualAmountOriginal(calPercentageFee);
					fee.setActualAmountGST(BigDecimal.ZERO);
					fee.setActualAmount(calPercentageFee);
				}
				fee.setRemainingFee(
						fee.getActualAmount().subtract(fee.getPaidAmount()).subtract(fee.getWaivedAmount()));
			}
		}

		logger.debug(Literal.LEAVING);

	}

	private BigDecimal getCalculatedPercentageFee(FinFeeDetail fee, FinReceiptData rd,
			Map<String, BigDecimal> taxPercentages) {

		logger.debug(Literal.ENTERING);
		FinScheduleData finScheduleData = rd.getFinanceDetail().getFinScheduleData();
		FinanceMain financeMain = finScheduleData.getFinanceMain();
		FinanceProfitDetail finPftDetail = finScheduleData.getFinPftDeatil();

		BigDecimal calculatedAmt = BigDecimal.ZERO;
		BigDecimal percentage = fee.getPercentage();
		switch (fee.getCalculateOn()) {
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
			calculatedAmt = finPftDetail.getTotalPriBal().subtract(rd.getTdPriBal());
			break;
		case PennantConstants.FEE_CALCULATEDON_PAYAMOUNT:
			calculatedAmt = rd.getReceiptHeader().getPartPayAmount();
			break;
		// part payment fee calculation
		case PennantConstants.FEE_CALCULATEDON_ADJUSTEDPRINCIPAL:
			calculatedAmt = rd.getReceiptHeader().getPartPayAmount();

			if (calculatedAmt == null || calculatedAmt.compareTo(BigDecimal.ZERO) < 0) {
				calculatedAmt = BigDecimal.ZERO;
			}

			fee.setActualOldAmount(calculatedAmt);

			BigDecimal calcPerc = BigDecimal.ONE;
			if (percentage.compareTo(BigDecimal.ZERO) > 0 && calculatedAmt.compareTo(BigDecimal.ZERO) > 0) {
				BigDecimal feePercent = percentage.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_DOWN);
				if (StringUtils.equals(fee.getTaxComponent(), FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE)) {
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
		fee.setCalculatedOn(calculatedAmt);
		fee.setActPercentage(percentage);

		calculatedAmt = calculatedAmt.multiply(percentage).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_DOWN);
		calculatedAmt = CalculationUtil.roundAmount(calculatedAmt, financeMain.getCalRoundingMode(),
				financeMain.getRoundingTarget());

		logger.debug(Literal.LEAVING);

		return calculatedAmt;

	}

	// ### 11-07-2018 - Start - PSD Ticket ID : 127846

	public BigDecimal getDropLinePOS(Date valueDate, FinScheduleData schdData) {
		BigDecimal dropLinePOS = BigDecimal.ZERO;
		FinanceMain fm = schdData.getFinanceMain();
		long finID = fm.getFinID();
		List<FinanceScheduleDetail> scheduleList = schdData.getFinanceScheduleDetails();

		if (scheduleList == null || scheduleList.isEmpty()) {
			scheduleList = financeScheduleDetailDAO.getFinSchdDetailsForBatch(finID);
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
		for (FinFeeDetail fd : feeDetails) {
			dataMap.putAll(getFeeRuleMap(fd, payType));
		}

		return dataMap;
	}

	public static Map<String, BigDecimal> getFeeRuleMap(FinFeeDetail fd) {
		Map<String, BigDecimal> dataMap = new HashMap<>();

		Taxes cgstTax = new Taxes();
		Taxes sgstTax = new Taxes();
		Taxes igstTax = new Taxes();
		Taxes ugstTax = new Taxes();
		Taxes cessTax = new Taxes();

		TaxHeader taxHeader = fd.getTaxHeader();
		if (taxHeader == null) {
			taxHeader = new TaxHeader();
		}

		for (Taxes tax : taxHeader.getTaxDetails()) {
			String taxType = tax.getTaxType();

			switch (taxType) {
			case RuleConstants.CODE_CGST:
				cgstTax = tax;
				break;
			case RuleConstants.CODE_SGST:
				sgstTax = tax;
				break;
			case RuleConstants.CODE_IGST:
				igstTax = tax;
				break;
			case RuleConstants.CODE_UGST:
				ugstTax = tax;
				break;
			case RuleConstants.CODE_CESS:
				cessTax = tax;
				break;
			default:
				break;
			}

		}

		String feeTypeCode = fd.getFeeTypeCode();

		setValue(dataMap, feeTypeCode, "_C", fd.getActualAmount());
		setValue(dataMap, feeTypeCode, "_C", fd.getActualAmount());
		setValue(dataMap, feeTypeCode, "_P", fd.getPaidAmountOriginal());
		setValue(dataMap, feeTypeCode, "_TDS_P", fd.getPaidTDS());
		setValue(dataMap, feeTypeCode, "_N", fd.getNetAmount());
		setValue(dataMap, feeTypeCode, "_TDS_N", fd.getNetTDS());

		setValue(dataMap, "EX_".concat(feeTypeCode), "_P", BigDecimal.ZERO);
		setValue(dataMap, "EA_".concat(feeTypeCode), "_P", BigDecimal.ZERO);
		setValue(dataMap, "PA_".concat(feeTypeCode), "_P", BigDecimal.ZERO);
		setValue(dataMap, "PB_".concat(feeTypeCode), "_P", BigDecimal.ZERO);

		BigDecimal totWaivedTax = BigDecimal.ZERO;
		BigDecimal waivedAmount = fd.getWaivedAmount();

		if (FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE.equals(fd.getTaxComponent())) {
			totWaivedTax = totWaivedTax.add(cgstTax.getWaivedTax());
			totWaivedTax = totWaivedTax.add(sgstTax.getWaivedTax());
			totWaivedTax = totWaivedTax.add(igstTax.getWaivedTax());
			totWaivedTax = totWaivedTax.add(ugstTax.getWaivedTax());
			totWaivedTax = totWaivedTax.add(cessTax.getWaivedTax());
		}

		setValue(dataMap, feeTypeCode, "_W", waivedAmount.subtract(totWaivedTax));

		// Calculated Amount
		setValue(dataMap, feeTypeCode, "_CGST_C", cgstTax.getActualTax());
		setValue(dataMap, feeTypeCode, "_SGST_C", sgstTax.getActualTax());
		setValue(dataMap, feeTypeCode, "_IGST_C", igstTax.getActualTax());
		setValue(dataMap, feeTypeCode, "_UGST_C", ugstTax.getActualTax());
		setValue(dataMap, feeTypeCode, "_CESS_C", cessTax.getActualTax());

		// Paid Amount
		setValue(dataMap, feeTypeCode, "_CGST_P", cgstTax.getPaidTax());
		setValue(dataMap, feeTypeCode, "_SGST_P", sgstTax.getPaidTax());
		setValue(dataMap, feeTypeCode, "_IGST_P", igstTax.getPaidTax());
		setValue(dataMap, feeTypeCode, "_UGST_P", ugstTax.getPaidTax());
		setValue(dataMap, feeTypeCode, "_CESS_P", cessTax.getPaidTax());

		// Net Amount
		setValue(dataMap, feeTypeCode, "_CGST_N", cgstTax.getNetTax());
		setValue(dataMap, feeTypeCode, "_SGST_N", sgstTax.getNetTax());
		setValue(dataMap, feeTypeCode, "_IGST_N", igstTax.getNetTax());
		setValue(dataMap, feeTypeCode, "_UGST_N", ugstTax.getNetTax());
		setValue(dataMap, feeTypeCode, "_CESS_N", cessTax.getNetTax());

		// Waiver GST Amounts (GST Waiver Changes)
		setValue(dataMap, feeTypeCode, "_CGST_W", cgstTax.getWaivedTax());
		setValue(dataMap, feeTypeCode, "_SGST_W", sgstTax.getWaivedTax());
		setValue(dataMap, feeTypeCode, "_IGST_W", igstTax.getWaivedTax());
		setValue(dataMap, feeTypeCode, "_UGST_W", ugstTax.getWaivedTax());
		setValue(dataMap, feeTypeCode, "_CESS_W", cessTax.getWaivedTax());

		String feeScheduleMethod = fd.getFeeScheduleMethod();

		BigDecimal remainingFeeOriginal = BigDecimal.ZERO;
		BigDecimal remainingCGST = BigDecimal.ZERO;
		BigDecimal remainingSGST = BigDecimal.ZERO;
		BigDecimal remainingIGST = BigDecimal.ZERO;
		BigDecimal remainingUSGT = BigDecimal.ZERO;
		BigDecimal remainingCESS = BigDecimal.ZERO;

		if (CalculationConstants.REMFEE_SCHD_TO_ENTIRE_TENOR.equals(feeScheduleMethod)
				|| CalculationConstants.REMFEE_SCHD_TO_FIRST_INSTALLMENT.equals(feeScheduleMethod)
				|| CalculationConstants.REMFEE_SCHD_TO_N_INSTALLMENTS.equals(feeScheduleMethod)) {
			remainingFeeOriginal = fd.getRemainingFeeOriginal();
			remainingCGST = cgstTax.getRemFeeTax();
			remainingSGST = sgstTax.getRemFeeTax();
			remainingIGST = igstTax.getRemFeeTax();
			remainingUSGT = ugstTax.getRemFeeTax();
			remainingCESS = cessTax.getRemFeeTax();
		}

		setValue(dataMap, feeTypeCode, "_SCH", remainingFeeOriginal);
		setValue(dataMap, feeTypeCode, "_CGST_SCH", remainingCGST);
		setValue(dataMap, feeTypeCode, "_SGST_SCH", remainingSGST);
		setValue(dataMap, feeTypeCode, "_IGST_SCH", remainingIGST);
		setValue(dataMap, feeTypeCode, "_UGST_SCH", remainingUSGT);
		setValue(dataMap, feeTypeCode, "_CESS_SCH", remainingCESS);

		remainingFeeOriginal = BigDecimal.ZERO;
		remainingCGST = BigDecimal.ZERO;
		remainingSGST = BigDecimal.ZERO;
		remainingIGST = BigDecimal.ZERO;
		remainingUSGT = BigDecimal.ZERO;
		remainingCESS = BigDecimal.ZERO;

		if (RuleConstants.DFT_FEE_FINANCE.equals(feeScheduleMethod)
				|| CalculationConstants.REMFEE_PART_OF_SALE_PRICE.equals(feeScheduleMethod)) {
			remainingFeeOriginal = fd.getRemainingFeeOriginal();
			remainingCGST = cgstTax.getRemFeeTax();
			remainingSGST = sgstTax.getRemFeeTax();
			remainingIGST = igstTax.getRemFeeTax();
			remainingUSGT = ugstTax.getRemFeeTax();
			remainingCESS = cessTax.getRemFeeTax();
		}

		setValue(dataMap, feeTypeCode, "_AF", remainingFeeOriginal);
		setValue(dataMap, feeTypeCode, "_CGST_AF", remainingCGST);
		setValue(dataMap, feeTypeCode, "_SGST_AF", remainingSGST);
		setValue(dataMap, feeTypeCode, "_IGST_AF", remainingIGST);
		setValue(dataMap, feeTypeCode, "_UGST_AF", remainingUSGT);
		setValue(dataMap, feeTypeCode, "_CESS_AF", remainingCESS);

		// TDS
		setValue(dataMap, feeTypeCode, "_TDS_N", fd.getNetTDS());
		setValue(dataMap, feeTypeCode, "_TDS_P", fd.getPaidTDS());

		return dataMap;

	}

	public static void setValue(Map<String, BigDecimal> dataMap, String prefix, String suffix, BigDecimal amount) {
		if (amount == null) {
			amount = BigDecimal.ZERO;
		}

		dataMap.put(prefix.concat(suffix), amount);
	}

	public static Map<String, BigDecimal> getFeeRuleMap(FinFeeDetail fd, String payType) {
		Map<String, BigDecimal> dataMap = getFeeRuleMap(fd);

		if (RepayConstants.RECEIPTMODE_EXCESS.equals(payType)) {
			payType = "EX_";
		} else if (RepayConstants.RECEIPTMODE_EMIINADV.equals(payType)) {
			payType = "EA_";
		} else if (RepayConstants.RECEIPTMODE_PAYABLE.equals(payType)) {
			payType = "PA_";
		} else {
			payType = "PB_";
		}

		String feeTypeCode = fd.getFeeTypeCode();
		setValue(dataMap, payType.concat(feeTypeCode), "_P", fd.getPaidAmountOriginal());

		return dataMap;
	}

	private void prepareExecutionMap(FinReceiptData rd, FinanceMain fm, Map<String, Object> dataMap) {

		Date appDate = SysParamUtil.getAppDate();

		if (fm.getFinStartDate() != null) {
			int finAge = DateUtility.getMonthsBetween(appDate, fm.getFinStartDate());
			dataMap.put("finAgetilldate", finAge);
		}

		long finID = fm.getFinID();
		FinanceProfitDetail finPft = profitDetailsDAO.getFinProfitDetailsById(finID);
		FinanceProfitDetail orgFinPftDtls = rd.getOrgFinPftDtls();
		BigDecimal outStandingFeeBal = financeScheduleDetailDAO.getOutStandingBalFromFees(finID);

		dataMap.put("totalOutStanding", finPft.getTotalPftBal());
		dataMap.put("principalOutStanding", finPft.getTotalpriSchd().subtract(finPft.getTdSchdPri()));

		if (rd.isForeClosureEnq()) {
			dataMap.put("principalOutStanding", finPft.getTotalPriBal().subtract(orgFinPftDtls.getTdSchdPriBal()));
		}
		dataMap.put("principalSchdOutstanding", finPft.getTotalpriSchd().subtract(finPft.getTdSchdPri()));
		dataMap.put("totOSExcludeFees", finPft.getTotalPftBal().add(finPft.getTotalPriBal()));
		dataMap.put("totOSIncludeFees", finPft.getTotalPftBal().add(finPft.getTotalPriBal()).add(outStandingFeeBal));
		dataMap.put("unearnedAmount", finPft.getUnearned());

		dataMap.put("totalPayment", rd.getTotReceiptAmount());
		dataMap.put("partialPaymentAmount", rd.getReceiptHeader().getPartPayAmount());
		dataMap.put("totalDueAmount", rd.getTotalDueAmount());

		Date fixedTenorEndDate = DateUtil.addMonths(fm.getGrcPeriodEndDate(), fm.getFixedRateTenor());

		if (fm.getFixedRateTenor() > 0 && fixedTenorEndDate.compareTo(appDate) > 0) {
			dataMap.put("Finance_Fixed_Tenor", PennantConstants.YES);
		} else {
			dataMap.put("Finance_Fixed_Tenor", PennantConstants.NO);
		}

		if (StringUtils.isBlank(fm.getRepayBaseRate())) {
			dataMap.put("rateType", "FIXED");
		} else {
			dataMap.put("rateType", "FLOATING");
		}

	}

	public List<FinFeeConfig> convertToFinanceFees(FinanceDetail fd) {
		logger.debug(Literal.ENTERING);

		List<FinFeeConfig> feeConfigList = new ArrayList<>();
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();
		String finType = fm.getFinType();

		List<FinTypeFees> feeTypes = finTypeFeesDAO.getFinTypeFeesList(finType, false, "_AView");

		if (CollectionUtils.isEmpty(feeTypes)) {
			return feeConfigList;
		}

		for (FinTypeFees feeType : feeTypes) {
			FinFeeConfig feeConfig = new FinFeeConfig();
			feeConfig.setFinID(fm.getFinID());
			feeConfig.setFinReference(fm.getFinReference());
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
				String percRule = feeConfig.getPercRule();
				String finEvent = feeConfig.getFinEvent();
				Rule feeRules = ruleDAO.getActiveRuleByID(percRule, RuleConstants.MODULE_FEEPERC, finEvent, "", true);
				if (feeRules != null) {
					feeConfig.setPercRuleId(feeRules.getRuleId());
				}
			}

			feeConfig.setOriginationFee(feeType.isOriginationFee());
			feeConfigList.add(feeConfig);
		}

		logger.debug(Literal.LEAVING);
		return feeConfigList;
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

	public JointAccountDetailDAO getJointAccountDetailDAO() {
		return jointAccountDetailDAO;
	}

	public void setJointAccountDetailDAO(JointAccountDetailDAO jointAccountDetailDAO) {
		this.jointAccountDetailDAO = jointAccountDetailDAO;
	}

	public FinTypeFeesDAO getFinTypeFeesDAO() {
		return finTypeFeesDAO;
	}

	public void setFinTypeFeesDAO(FinTypeFeesDAO finTypeFeesDAO) {
		this.finTypeFeesDAO = finTypeFeesDAO;
	}

}