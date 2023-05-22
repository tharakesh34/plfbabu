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
import com.pennant.backend.model.rmtmasters.FinTypeFees;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.service.finance.FinFeeDetailService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RuleConstants;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.util.FeesUtil;
import com.pennanttech.pff.receipt.constants.ReceiptMode;

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
		FinanceDetail fd = rd.getFinanceDetail();
		FinScheduleData schdData = fd.getFinScheduleData();

		FinanceMain fm = schdData.getFinanceMain();
		Map<String, BigDecimal> taxPercentages = GSTCalculator.getTaxPercentages(fm);

		convertToFinanceFees(rd, taxPercentages);

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
		List<FinFeeDetail> feeList = new ArrayList<>();

		List<FinFeeConfig> feeConfigList = fd.getFinFeeConfigList();

		if (CollectionUtils.isNotEmpty(feeConfigList)) {
			feeList.addAll(calculateFeeOnRule(rd));
			schdData.setFinFeeDetailList(feeList);

			logger.debug(Literal.LEAVING);
			return;
		}

		if (CollectionUtils.isEmpty(finTypeFeesList)) {
			schdData.setFinFeeDetailList(feeList);

			logger.debug(Literal.LEAVING);
			return;
		}

		String calRoundingMode = fm.getCalRoundingMode();
		int roundingTarget = fm.getRoundingTarget();
		Date appDate = SysParamUtil.getAppDate();

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

			if (fee.isOriginationFee()) {
				fd.setValueDate(fm.getFinStartDate());
			} else {
				fd.setValueDate(appDate);
			}

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

				Rule feeRules = ruleDAO.getRuleByID(finFeeConfig.getReferenceId(), "");
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
				Map<String, BigDecimal> taxPercentages = GSTCalculator.getTaxPercentages(fm);
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
		FinanceDetail fd = rd.getFinanceDetail();
		FinScheduleData schdData = fd.getFinScheduleData();

		List<FinFeeDetail> feeList = schdData.getFinFeeDetailList();

		List<String> rules = new ArrayList<>();

		FinanceMain fm = schdData.getFinanceMain();

		for (FinFeeDetail fee : feeList) {
			if (StringUtils.isNotEmpty(fee.getRuleCode())) {
				rules.add(fee.getRuleCode());
			}
		}

		if (rules.isEmpty()) {
			return;
		}

		String feeEvent = schdData.getFeeEvent();
		List<Rule> feeRules = ruleDAO.getRuleDetailList(rules, RuleConstants.MODULE_FEES, feeEvent);

		if (feeRules.isEmpty()) {
			return;
		}

		Map<String, Object> dataMap = new HashMap<>();
		Map<String, String> ruleSqlMap = new HashMap<>();
		List<Object> objectList = new ArrayList<>();

		CustomerDetails custDetails = fd.getCustomerDetails();
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

		if (fd.getFinScheduleData() != null) {
			objectList.add(schdData.getFinanceMain());
			objectList.add(fd.getFinScheduleData().getFinanceType());
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

		prepareExecutionMap(rd, fm, dataMap);

		String finCcy = fm.getFinCcy();

		for (FinFeeDetail fee : feeList) {
			if (StringUtils.isEmpty(fee.getRuleCode())) {
				continue;
			}

			BigDecimal feeResult = this.finFeeDetailService.getFeeResult(ruleSqlMap.get(fee.getRuleCode()), dataMap,
					finCcy);

			fee.setCalculatedAmount(feeResult);

			if (fee.isTaxApplicable()) {
				this.finFeeDetailService.processGSTCalForRule(fee, feeResult, fd, taxPercentages, false);
			} else {
				if (!fee.isFeeModified() || !fee.isAlwModifyFee()) {
					fee.setActualAmountOriginal(feeResult);
					fee.setActualAmountGST(BigDecimal.ZERO);
					fee.setActualAmount(feeResult);
				}

				fee.setRemainingFee(
						fee.getActualAmount().subtract(fee.getPaidAmount()).subtract(fee.getWaivedAmount()));
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

			if (calculatedAmt == null || calculatedAmt.compareTo(BigDecimal.ZERO) < 0) {
				calculatedAmt = BigDecimal.ZERO;
			}

			if (calculatedAmt.compareTo(BigDecimal.ZERO) > 0) {
				calculatedAmt = calculatedAmt.add(rd.getReceiptHeader().getTotalFees().getPaidAmount());
			}

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
		return FeesUtil.getFeeRuleMap(fd);
	}

	public static void setValue(Map<String, BigDecimal> dataMap, String prefix, String suffix, BigDecimal amount) {
		if (amount == null) {
			amount = BigDecimal.ZERO;
		}

		dataMap.put(prefix.concat(suffix), amount);
	}

	public static Map<String, BigDecimal> getFeeRuleMap(FinFeeDetail fd, String payType) {
		Map<String, BigDecimal> dataMap = getFeeRuleMap(fd);

		if (ReceiptMode.EXCESS.equals(payType)) {
			payType = "EX_";
		} else if (ReceiptMode.EMIINADV.equals(payType)) {
			payType = "EA_";
		} else if (ReceiptMode.PAYABLE.equals(payType)) {
			payType = "PA_";
		} else {
			payType = "PB_";
		}

		String feeTypeCode = fd.getFeeTypeCode();
		setValue(dataMap, payType.concat(feeTypeCode), "_P", fd.getPaidAmountOriginal());

		return dataMap;
	}

	private void prepareExecutionMap(FinReceiptData rd, FinanceMain fm, Map<String, Object> dataMap) {
		FinanceDetail fd = rd.getFinanceDetail();
		FinScheduleData schdData = fd.getFinScheduleData();
		List<FinanceScheduleDetail> schedules = schdData.getFinanceScheduleDetails();

		Date valDate = rd.getValueDate();
		if (valDate == null) {
			valDate = SysParamUtil.getAppDate();
		}

		if (fm.getFinStartDate() != null) {
			int finAge = DateUtil.getMonthsBetween(valDate, fm.getFinStartDate());
			dataMap.put("finAgetilldate", finAge);
			dataMap.put("completedTenure", finAge);
		}

		int instNO = 0;

		for (FinanceScheduleDetail detail : schedules) {
			if (detail.getSchDate().compareTo(valDate) <= 0) {
				instNO = detail.getInstNumber();
			} else {
				break;
			}
		}

		dataMap.put("completedInstallments", instNO);

		long finID = fm.getFinID();

		BigDecimal totReceiptAmount = rd.getTotReceiptAmount();

		FinanceProfitDetail pft = profitDetailsDAO.getFinProfitDetailsById(finID);

		if (pft == null) {
			pft = new FinanceProfitDetail();
		}

		BigDecimal outStandingFeeBal = financeScheduleDetailDAO.getOutStandingBalFromFees(finID);

		BigDecimal totalOutStanding = pft.getTotalPftBal();
		BigDecimal principalOutStanding = pft.getTotalpriSchd().subtract(pft.getTdSchdPri());
		BigDecimal principalAmtFutPrincipalAmt = pft.getTotalPriBal();
		BigDecimal totOSExcludeFees = totalOutStanding.add(principalAmtFutPrincipalAmt);
		BigDecimal totOSIncludeFees = totOSExcludeFees.add(outStandingFeeBal);
		BigDecimal unearnedAmount = pft.getUnearned();

		dataMap.put("totalOutStanding", pft.getTotalPftBal());
		dataMap.put("principalOutStanding", principalOutStanding);
		dataMap.put("principalSchdOutstanding", principalOutStanding);
		dataMap.put("principalAmtFutPrincipalAmt", principalAmtFutPrincipalAmt);
		dataMap.put("totOSExcludeFees", totOSExcludeFees);

		dataMap.put("totOSIncludeFees", totOSIncludeFees);
		dataMap.put("unearnedAmount", unearnedAmount);
		dataMap.put("eligibilityMethod", fm.getEligibilityMethod());

		if (rd.isForeClosureEnq()) {
			principalOutStanding = principalAmtFutPrincipalAmt.subtract(rd.getOrgFinPftDtls().getTdSchdPriBal());
			dataMap.put("principalOutStanding", principalOutStanding);
			dataMap.put("principalAmtFutPrincipalAmt", principalAmtFutPrincipalAmt);
		}

		dataMap.put("totalPayment", totReceiptAmount);

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
		BigDecimal partPayAmount = rd.getReceiptHeader().getReceiptAmount();

		if ((partPayAmount.compareTo(totalDues) > 0)) {
			partialPaymentAmount = partPayAmount
					.subtract(totalDues.subtract(rd.getReceiptHeader().getTotalFees().getTotalDue()));
		}

		dataMap.put("partialPaymentAmount", partialPaymentAmount);

		Date fixedTenorEndDate = DateUtil.addMonths(fm.getGrcPeriodEndDate(), fm.getFixedRateTenor());

		String financeFixedTenor = PennantConstants.NO;
		if (fm.getFixedRateTenor() > 0 && fixedTenorEndDate.compareTo(SysParamUtil.getAppDate()) > 0) {
			financeFixedTenor = PennantConstants.YES;
		}

		dataMap.put("Finance_Fixed_Tenor", financeFixedTenor);

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

	@Autowired
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