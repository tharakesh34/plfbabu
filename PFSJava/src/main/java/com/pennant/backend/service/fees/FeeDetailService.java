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
 *******************************************************************************************************
 * FILE HEADER *
 *******************************************************************************************************
 *
 * FileName : FeeDetailService.java *
 * 
 * Author : PENNANT TECHONOLOGIES *
 * 
 * Creation Date : 11-07-2017 *
 * 
 * Modified Date : 11-07-2018 *
 * 
 * Description : *
 * 
 ********************************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************************
 * 11-07-2018 Pennant 0.1 * * 11-07-2018 Satya 0.2 PSD - Ticket : 127846 * Changes related to Fees calculation for the *
 * selection type DropLinePOS. * * * * * * * * * *
 ********************************************************************************************************
 */
package com.pennant.backend.service.fees;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.FeeCalculator;
import com.pennant.app.util.GSTCalculator;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.app.util.SessionUserDetails;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinFeeReceipt;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.rmtmasters.FinTypeFees;
import com.pennant.backend.model.rmtmasters.Promotion;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.service.finance.FinFeeDetailService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.rulefactory.RuleService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RuleConstants;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.constants.FinServiceEvent;

public class FeeDetailService {
	Logger logger = LogManager.getLogger(FeeDetailService.class);

	private FinanceDetailService financeDetailService;
	private RuleService ruleService;
	private FinFeeDetailService finFeeDetailService;

	private void executeFeeCharges(FinanceDetail fd, boolean isOriginationFee, FinServiceInstruction fsi,
			boolean enquiry) throws AppException {
		logger.debug(Literal.ENTERING);

		FinScheduleData schdData = fd.getFinScheduleData();

		// set FinType fees details
		FinanceMain fm = schdData.getFinanceMain();
		Long finID = fm.getFinID();
		String finReference = fm.getFinReference();

		List<FinFeeDetail> feeList = convertToFinanceFees(fd, finID, finReference);
		feeList = prepareActualFinFees(feeList, schdData.getFinFeeDetailList());

		// Organize Fee detail changes
		feeList = doSetFeeChanges(fd, isOriginationFee, feeList);

		List<String> feeRuleCodes = new ArrayList<String>();

		Map<String, BigDecimal> gstPercentages = getGSTPercentages(fd);

		for (FinFeeDetail fee : feeList) {
			fee.setRecordType(PennantConstants.RCD_ADD);
			fee.setFinID(finID);
			fee.setFinReference(finReference);
			fee.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			fee.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			fee.setLastMntBy(fd.getFinScheduleData().getFinanceMain().getLastMntBy());
			fee.setFinEvent(schdData.getFeeEvent());

			if (StringUtils.isNotEmpty(fee.getRuleCode())) {
				feeRuleCodes.add(fee.getRuleCode());
			}
			if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(fee.getTaxComponent())) {
				BigDecimal totalGST = GSTCalculator.getExclusiveGST(fee.getPaidAmount(), gstPercentages).gettGST();
				fee.setPaidAmountOriginal(fee.getPaidAmount());
				fee.setPaidAmount(fee.getPaidAmountOriginal().add(totalGST));
			}
		}

		// Execute fee rules if exists
		if (feeRuleCodes.size() > 0) {
			doExecuteFeeRules(fd, fsi, schdData, feeRuleCodes, enquiry, gstPercentages, feeList);
		}

		// calculate fee percentage if exists
		calculateFeePercentageAmount(schdData, fd, enquiry, gstPercentages, feeList);

		// Validate Provided fee details with configured fee details
		validateFeeConfig(feeList, schdData);

		// Calculating GST
		String subventionFeeCode = PennantConstants.FEETYPE_SUBVENTION;
		for (FinFeeDetail fee : feeList) {
			if (subventionFeeCode.equals(fee.getFeeTypeCode())) {
				this.finFeeDetailService.calculateFees(fee, schdData, getDealerTaxPercentages(fd));
			} else {
				this.finFeeDetailService.calculateFees(fee, fm, getGSTPercentages(fd));
			}
		}

		// add vas recording fees into actual list
		if (schdData.getVasRecordingList() != null && !schdData.getVasRecordingList().isEmpty()) {
			List<FinFeeDetail> vasFees = new ArrayList<FinFeeDetail>();
			for (FinFeeDetail detail : schdData.getFinFeeDetailList()) {
				if (StringUtils.equals(detail.getFinEvent(), AccountingEvent.VAS_FEE)) {
					detail.setRemainingFee(detail.getActualAmount().subtract(detail.getPaidAmount())
							.subtract(detail.getWaivedAmount()));
					vasFees.add(detail);
				}
			}
			feeList.addAll(vasFees);
		}

		setFeeAmount(fd.getModuleDefiner(), schdData, feeList);

		for (FinFeeDetail prvFeeDetail : schdData.getFinFeeDetailList()) {
			for (FinFeeDetail currFeeDetail : feeList) {
				if (StringUtils.equals(prvFeeDetail.getFinEvent(), currFeeDetail.getFinEvent())
						&& StringUtils.equals(prvFeeDetail.getFeeTypeCode(), currFeeDetail.getFeeTypeCode())) {
					try {
						BeanUtils.copyProperties(prvFeeDetail, currFeeDetail);
					} catch (Exception e) {
						throw new AppException("", e);
					}
				}
			}
		}

		if (AccountingEvent.EARLYSTL.equals(fm.getFinSourceID())) {
			schdData.setFinFeeDetailList(feeList);
		} else if (AccountingEvent.RESTRUCTURE.equals(fd.getAccountingEventCode())) {
			schdData.setFinFeeDetailList(feeList);
		}

		logger.debug(Literal.LEAVING);
	}

	public void setFeeAmount(String moduleDefiner, FinScheduleData schdData, List<FinFeeDetail> fees) {
		FinanceMain fm = schdData.getFinanceMain();

		BigDecimal deductFromDisbursement = BigDecimal.ZERO;
		BigDecimal feeAddToDisbTot = BigDecimal.ZERO;

		for (FinFeeDetail fee : fees) {
			if (AccountingEvent.VAS_FEE.equals(fee.getFinEvent())
					&& PennantConstants.RECORD_TYPE_CAN.equals(fee.getRecordType())) {
				continue;
			}
			String feeScheduleMethod = fee.getFeeScheduleMethod();
			switch (feeScheduleMethod) {
			case CalculationConstants.REMFEE_PART_OF_DISBURSE:
				deductFromDisbursement = deductFromDisbursement.add(fee.getRemainingFee());
				break;
			case CalculationConstants.REMFEE_PART_OF_SALE_PRICE:
				feeAddToDisbTot = feeAddToDisbTot.add(fee.getRemainingFee());
				break;
			case CalculationConstants.REMFEE_WAIVED_BY_BANK:
				if (fee.getWaivedAmount().compareTo(BigDecimal.ZERO) == 0) {
					fee.setWaivedAmount(fee.getActualAmount());
				}
				break;
			default:
				break;
			}

			// Excluding GST & TDS -- SO COMMENTED
			// fee.setRemainingFee(fee.getActualAmount().subtract(fee.getPaidAmount()).subtract(fee.getWaivedAmount()));
			// BUG FIX RELATED TO FEE IN CASE OF WAIVE REMAINING AMT RELATED
			// TODO:GANESH Need to move to core.
		}

		if (StringUtils.equals(FinServiceEvent.ORG, moduleDefiner)) {
			fm.setDeductFeeDisb(deductFromDisbursement);
			fm.setFeeChargeAmt(feeAddToDisbTot);
		} else {
			if (CollectionUtils.isNotEmpty(schdData.getDisbursementDetails())) {
				List<Integer> approvedDisbSeq = financeDetailService.getFinanceDisbSeqs(fm.getFinID(), false);
				for (FinanceDisbursement disbursement : schdData.getDisbursementDetails()) {
					if (!approvedDisbSeq.contains(disbursement.getDisbSeq())) {
						disbursement.setDeductFeeDisb(deductFromDisbursement);
						break;
					}
				}
			}
		}
	}

	private void validateFeeConfig(List<FinFeeDetail> finFeeDetails, FinScheduleData finScheduleData) {
		logger.debug(Literal.ENTERING);

		List<ErrorDetail> errorDetails = new ArrayList<>();

		for (FinFeeDetail finFeeDetail : finFeeDetails) {
			BigDecimal calcAmount = finFeeDetail.getCalculatedAmount();
			BigDecimal actualAmount = BigDecimal.ZERO;

			for (FinFeeDetail actFee : finScheduleData.getFinFeeDetailList()) {
				if (StringUtils.equals(actFee.getFeeTypeCode(), finFeeDetail.getFeeTypeCode())
						&& StringUtils.equals(actFee.getFinEvent(), finFeeDetail.getFinEvent())) {
					actualAmount = actFee.getActualAmount();
					break;
				}
			}

			if (!finFeeDetail.isAlwModifyFee() && actualAmount.compareTo(calcAmount) != 0) {
				String[] valueParm = new String[3];
				valueParm[0] = "Fee amount";
				valueParm[1] = "Actual fee amount:" + String.valueOf(calcAmount);
				valueParm[2] = finFeeDetail.getFeeTypeCode();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90258", valueParm)));
				finScheduleData.setErrorDetails(errorDetails);
				return;
			}

			if (finFeeDetail.getPaidAmount().compareTo(finFeeDetail.getActualAmount()) > 0) {
				String[] valueParm = new String[3];
				valueParm[0] = "Paid amount";
				valueParm[1] = "Actual amount:" + String.valueOf(calcAmount);
				valueParm[2] = finFeeDetail.getFeeTypeCode();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90257", valueParm)));
				finScheduleData.setErrorDetails(errorDetails);
			}

			BigDecimal maxWaiverPer = finFeeDetail.getMaxWaiverPerc();
			BigDecimal finWaiverAmount = (calcAmount.multiply(maxWaiverPer)).divide(new BigDecimal(100), 0,
					RoundingMode.HALF_DOWN);
			// finWaiverAmount = PennantApplicationUtil.unFormateAmount(finWaiverAmount, formatter);

			if (finFeeDetail.getWaivedAmount().compareTo(finWaiverAmount) > 0) {
				String[] valueParm = new String[3];
				valueParm[0] = "Waiver amount";
				valueParm[1] = "Actual waiver amount:" + String.valueOf(finWaiverAmount);
				valueParm[2] = finFeeDetail.getFeeTypeCode();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90257", valueParm)));
				finScheduleData.setErrorDetails(errorDetails);
			}

			// finFeeDetail.setRemainingFee(finFeeDetail.getActualAmount().subtract(finFeeDetail.getPaidAmount()).subtract(finFeeDetail.getWaivedAmount()));

			if (!finFeeDetail.isOriginationFee()) {
				if (finFeeDetail.getRemainingFee().compareTo(BigDecimal.ZERO) != 0) {
					String[] valueParm = new String[3];
					valueParm[0] = "Sum of waiver and paid amounts";
					valueParm[1] = "Actual fee amount:" + String.valueOf(finFeeDetail.getActualAmount());
					valueParm[2] = finFeeDetail.getFeeTypeCode();
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90268", valueParm)));
					finScheduleData.setErrorDetails(errorDetails);
				}
			}

			if (finFeeDetail.getRemainingFee().compareTo(BigDecimal.ZERO) < 0) {
				String[] valueParm = new String[3];
				valueParm[0] = "Sum of waiver and paid amounts";
				valueParm[1] = "Actual fee amount:" + String.valueOf(finFeeDetail.getActualAmount());
				valueParm[2] = finFeeDetail.getFeeTypeCode();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90257", valueParm)));
				finScheduleData.setErrorDetails(errorDetails);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	public void doProcessFeesForInquiry(FinanceDetail fd, String finEvent, FinServiceInstruction fsi, boolean enquiry)
			throws AppException {
		logger.debug(Literal.ENTERING);

		boolean isOrigination = false;
		if (fsi == null) {
			isOrigination = true;
		}

		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();
		Long finID = fm.getFinID();
		String finReference = fm.getFinReference();

		// PSD# to fix the concurrent issue
		List<FinFeeDetail> finFeeDetailList = new ArrayList<>();

		if (StringUtils.isBlank(fm.getPromotionCode()) || fm.getPromotionSeqId() == 0) {
			fd.setFinTypeFeesList(financeDetailService.getFinTypeFees(fm.getFinType(), finEvent, isOrigination,
					FinanceConstants.MODULEID_FINTYPE));
		} else {
			Promotion promotion = schdData.getPromotion();
			fd.setFinTypeFeesList(financeDetailService.getSchemeFeesList(promotion.getReferenceID(), finEvent,
					isOrigination, FinanceConstants.MODULEID_PROMOTION));
		}

		schdData.setFeeEvent(finEvent);

		// set FinType fees details

		finFeeDetailList = convertToFinanceFees(fd, finID, finReference);
		finFeeDetailList = prepareActualFinFees(finFeeDetailList, schdData.getFinFeeDetailList());

		// Organize Fee detail changes
		// doSetFeeChanges(financeDetail, isOriginationFee);

		List<String> feeRuleCodes = new ArrayList<String>();
		for (FinFeeDetail fee : finFeeDetailList) {
			fee.setRecordType(PennantConstants.RCD_ADD);
			fee.setFinID(finID);
			fee.setFinReference(finReference);
			fee.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			fee.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			fee.setLastMntBy(fm.getLastMntBy());
			fee.setFinEvent(schdData.getFeeEvent());
			if (StringUtils.isNotEmpty(fee.getRuleCode())) {
				feeRuleCodes.add(fee.getRuleCode());
			}
		}

		Map<String, BigDecimal> gstPercentages = getGSTPercentages(fd);

		// Execute fee rules if exists
		if (feeRuleCodes.size() > 0) {
			doExecuteFeeRules(fd, fsi, schdData, feeRuleCodes, enquiry, gstPercentages, finFeeDetailList);
		}

		// calculate fee percentage if exists
		calculateFeePercentageAmount(schdData, fd, enquiry, gstPercentages, finFeeDetailList);

		String subventionFeeCode = PennantConstants.FEETYPE_SUBVENTION;
		// Calculating GST
		for (FinFeeDetail fee : finFeeDetailList) {
			if (subventionFeeCode.equals(fee.getFeeTypeCode())) {
				this.finFeeDetailService.calculateFees(fee, schdData, getDealerTaxPercentages(fd));
			} else {
				this.finFeeDetailService.calculateFees(fee, fm, getGSTPercentages(fd));
			}
		}

		// set Actual calculated values into feeDetails for Inquiry purpose
		for (FinFeeDetail fee : finFeeDetailList) {
			if (!isOrigination) {
				fee.setPaidAmount(fee.getActualAmount());
			}
			fee.setRemainingFee(fee.getActualAmount().subtract(fee.getPaidAmount()).subtract(fee.getWaivedAmount()));
			if (CalculationConstants.REMFEE_SCHD_TO_N_INSTALLMENTS.equals(fee.getFeeScheduleMethod())
					&& fee.getTerms() <= 0) {
				fee.setTerms(1);
			}
		}

		setFeeAmount(fd.getModuleDefiner(), schdData, finFeeDetailList);

		for (FinFeeDetail fee : schdData.getFinFeeDetailList()) {
			for (FinFeeDetail currFeeDetail : finFeeDetailList) {
				if (StringUtils.equals(fee.getFinEvent(), currFeeDetail.getFinEvent())
						&& StringUtils.equals(fee.getFeeTypeCode(), currFeeDetail.getFeeTypeCode())) {
					try {
						BeanUtils.copyProperties(fee, currFeeDetail);
					} catch (Exception e) {
						throw new AppException("", e);
					}
				}
			}
		}

		schdData.getFinFeeDetailList().addAll(finFeeDetailList);

		// Calculating GST
		for (FinFeeDetail fee : finFeeDetailList) {
			if (StringUtils.equals(subventionFeeCode, fee.getFeeTypeCode())) {
				this.finFeeDetailService.calculateFees(fee, schdData, getDealerTaxPercentages(fd));
			} else {
				this.finFeeDetailService.calculateFees(fee, fm, getGSTPercentages(fd));
			}
		}

		logger.debug(Literal.LEAVING);

	}

	public void doProcessFeesForInquiryForUpload(FinanceDetail fd, String finEvent, FinServiceInstruction fsi,
			boolean enquiry) throws AppException {
		logger.debug(Literal.ENTERING);

		// PSD# to fix the concurrent issue
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();
		Long finID = fm.getFinID();
		String finReference = fm.getFinReference();

		if (!schdData.getFinanceType().isPromotionType()) {
			fd.setFinTypeFeesList(financeDetailService.getFinTypeFees(fm.getFinType(), finEvent, false,
					FinanceConstants.MODULEID_FINTYPE));
		} else {
			String promotionType = fd.getFinScheduleData().getFinanceType().getPromotionCode();
			fd.setFinTypeFeesList(financeDetailService.getFinTypeFees(promotionType, finEvent, false,
					FinanceConstants.MODULEID_PROMOTION));
		}
		fd.getFinScheduleData().setFeeEvent(finEvent);

		// set FinType fees details
		List<FinFeeDetail> feeList = convertToFinanceFees(fd.getFinTypeFeesList(), finID, finReference);
		feeList = prepareActualFinFees(feeList, schdData.getFinFeeDetailList());

		// Organize Fee detail changes
		// doSetFeeChanges(financeDetail, isOriginationFee);

		List<String> feeRuleCodes = new ArrayList<String>();
		for (FinFeeDetail fee : feeList) {
			fee.setRecordType(PennantConstants.RCD_ADD);
			fee.setFinID(finID);
			fee.setFinReference(finReference);
			fee.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			fee.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			fee.setLastMntBy(fm.getLastMntBy());
			fee.setFinEvent(schdData.getFeeEvent());
			if (StringUtils.isNotEmpty(fee.getRuleCode())) {
				feeRuleCodes.add(fee.getRuleCode());
			}
		}

		// Execute fee rules if exists
		if (feeRuleCodes.size() > 0) {
			doExecuteFeeRules(fd, fsi, schdData, feeRuleCodes, enquiry, new HashMap<>(), feeList);
		}

		// calculate fee percentage if exists
		calculateFeePercentageAmount(schdData, fd, enquiry, new HashMap<>(), feeList);

		// set Actual calculated values into feeDetails for Inquiry purpose
		for (FinFeeDetail fee : feeList) {
			fee.setPaidAmount(fee.getActualAmount());
			fee.setRemainingFee(fee.getActualAmount().subtract(fee.getPaidAmount()).subtract(fee.getWaivedAmount()));
			if (StringUtils.equals(fee.getFeeScheduleMethod(), CalculationConstants.REMFEE_SCHD_TO_N_INSTALLMENTS)) {
				fee.setTerms(1);
			}
		}

		BigDecimal deductFeeFromDisbTot = BigDecimal.ZERO;
		BigDecimal feeAddToDisbTot = BigDecimal.ZERO;
		for (FinFeeDetail fee : feeList) {
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

		for (FinFeeDetail fee : schdData.getFinFeeDetailList()) {
			for (FinFeeDetail currFeeDetail : feeList) {
				if (StringUtils.equals(fee.getFinEvent(), currFeeDetail.getFinEvent())
						&& StringUtils.equals(fee.getFeeTypeCode(), currFeeDetail.getFeeTypeCode())) {
					try {
						BeanUtils.copyProperties(fee, currFeeDetail);
					} catch (Exception e) {
						throw new AppException("", e);
					}
				}
			}
		}

		schdData.getFinFeeDetailList().addAll(feeList);

		// ### Ticket id:124998
		if (fsi != null && fsi.isReceiptUpload()) {
			fsi.setFinFeeDetails(schdData.getFinFeeDetailList());
		}

		logger.debug(Literal.LEAVING);

	}

	private void doExecuteFeeRules(FinanceDetail fd, FinServiceInstruction fsi, FinScheduleData finScheduleData,
			List<String> feeRuleCodes, boolean enquiry, Map<String, BigDecimal> gstPercentages,
			List<FinFeeDetail> feeList) {

		List<Rule> feeRules = ruleService.getRuleDetailList(feeRuleCodes, RuleConstants.MODULE_FEES,
				finScheduleData.getFeeEvent());

		if (feeRules == null || feeRules.isEmpty()) {
			return;
		}

		Map<String, Object> executionMap = new HashMap<String, Object>();
		Map<String, String> ruleSqlMap = new HashMap<String, String>();
		List<Object> objectList = new ArrayList<Object>();

		if (fd.getCustomerDetails() != null) {
			objectList.add(fd.getCustomerDetails().getCustomer());
			if (fd.getCustomerDetails().getCustEmployeeDetail() != null) {
				objectList.add(fd.getCustomerDetails().getCustEmployeeDetail());
			}
			List<CustomerAddres> addressList = fd.getCustomerDetails().getAddressList();
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
		FinScheduleData schdData = fd.getFinScheduleData();
		if (schdData != null) {
			objectList.add(schdData.getFinanceMain());
			objectList.add(schdData.getFinanceType());
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

		int formatter = CurrencyUtil.getFormat(finScheduleData.getFinanceMain().getFinCcy());
		FinanceMain fm = schdData.getFinanceMain();
		// FIXME:removed the && StringUtils.isNotBlank(finMain.getRcdMaintainSts()) condition for calculate Fees's
		// through API
		if (fm != null && StringUtils.isNotBlank(fm.getFinReference())) {
			FinanceProfitDetail fpd = financeDetailService.getFinProfitDetailsById(fm.getFinID());
			if (fpd != null) {
				BigDecimal outStandingFeeBal = this.financeDetailService.getOutStandingBalFromFees(fm.getFinID());
				executionMap.put("totalOutStanding", fpd.getTotalPftBal());
				executionMap.put("principalOutStanding", fpd.getTotalpriSchd().subtract(fpd.getTdSchdPri()));
				executionMap.put("principalSchdOutstanding", fpd.getTotalpriSchd().subtract(fpd.getTdSchdPri()));
				executionMap.put("totOSExcludeFees", fpd.getTotalPftBal().add(fpd.getTotalPriBal()));
				executionMap.put("totOSIncludeFees",
						fpd.getTotalPftBal().add(fpd.getTotalPriBal()).add(outStandingFeeBal));
				executionMap.put("unearnedAmount", fpd.getUnearned());
			}

			if (fsi != null) {
				executionMap.put("totalPayment", fsi.getAmount());
				BigDecimal remPartPaymentAmt = PennantApplicationUtil.formateAmount(fsi.getRemPartPayAmt(), formatter);
				executionMap.put("partialPaymentAmount", remPartPaymentAmt);
			}

			if (fm != null && fm.getFinStartDate() != null) {
				int finAge = DateUtil.getMonthsBetween(SysParamUtil.getAppDate(), fm.getFinStartDate());
				executionMap.put("finAgetilldate", finAge);
			}
		}
		if (fm != null) {
			executionMap.putAll(fm.getDeclaredFieldValues());
		}

		for (FinFeeDetail finFeeDetail : feeList) {
			if (StringUtils.isEmpty(finFeeDetail.getRuleCode())) {
				continue;
			}

			BigDecimal feeResult = this.finFeeDetailService.getFeeResult(ruleSqlMap.get(finFeeDetail.getRuleCode()),
					executionMap, finScheduleData.getFinanceMain().getFinCcy());

			// unFormating feeResult
			feeResult = PennantApplicationUtil.unFormateAmount(feeResult, formatter);

			finFeeDetail.setCalculatedAmount(feeResult);

			if (finFeeDetail.isTaxApplicable()) {
				if (enquiry) {
					this.finFeeDetailService.processGSTCalForRule(finFeeDetail, feeResult, fd, getGSTPercentages(fd),
							enquiry);
				} else {
					this.finFeeDetailService.processGSTCalForRule(finFeeDetail, finFeeDetail.getActualAmount(), fd,
							getGSTPercentages(fd), enquiry);
				}
			} else {
				if (enquiry) {
					finFeeDetail.setActualAmount(feeResult);
				}
				finFeeDetail.setRemainingFee(finFeeDetail.getActualAmount().subtract(finFeeDetail.getPaidAmount())
						.subtract(finFeeDetail.getWaivedAmount()));
			}
		}
	}

	/*
	 * private List<FinFeeDetail> prepareActualFinFees(List<FinFeeDetail> finTypeFees, List<FinFeeDetail>
	 * finFeeDetailList) { if (CollectionUtils.isNotEmpty(finFeeDetailList)) { for (FinFeeDetail finTypeFeeDetail :
	 * finTypeFees) { for (FinFeeDetail finFeeDetail : finFeeDetailList) { if
	 * (StringUtils.equals(finTypeFeeDetail.getFeeTypeCode(), finFeeDetail.getFeeTypeCode()) &&
	 * StringUtils.equals(finTypeFeeDetail.getFinEvent(), finFeeDetail.getFinEvent())) {
	 * finTypeFeeDetail.setFeeScheduleMethod(finFeeDetail.getFeeScheduleMethod());
	 * finTypeFeeDetail.setPaidAmount(finFeeDetail.getPaidAmount()); finTypeFeeDetail.setTerms(finFeeDetail.getTerms());
	 * finTypeFeeDetail.setWaivedAmount(finFeeDetail.getWaivedAmount());
	 * 
	 * if (finTypeFeeDetail.isTaxApplicable()) { if
	 * (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(finTypeFeeDetail.getTaxComponent())) {
	 * finTypeFeeDetail.setActualAmountOriginal(finFeeDetail.getActualAmount()); } else if
	 * (FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE.equals(finTypeFeeDetail.getTaxComponent())) {
	 * finTypeFeeDetail.setNetAmount(finFeeDetail.getActualAmount()); } } else {
	 * finTypeFeeDetail.setActualAmountOriginal(finFeeDetail.getActualAmount());
	 * finTypeFeeDetail.setActualAmountGST(BigDecimal.ZERO);
	 * finTypeFeeDetail.setActualAmount(finFeeDetail.getActualAmount());
	 * 
	 * BigDecimal netAmountOriginal =
	 * finTypeFeeDetail.getActualAmountOriginal().subtract(finTypeFeeDetail.getWaivedAmount());
	 * 
	 * finTypeFeeDetail.setNetAmountOriginal(netAmountOriginal); finTypeFeeDetail.setNetAmountGST(BigDecimal.ZERO);
	 * finTypeFeeDetail.setNetAmount(netAmountOriginal);
	 * 
	 * finTypeFeeDetail.setPaidAmountOriginal(finFeeDetail.getPaidAmount());
	 * finTypeFeeDetail.setPaidAmountGST(BigDecimal.ZERO); } } } } } return finTypeFees; }
	 */

	private List<FinFeeDetail> prepareActualFinFees(List<FinFeeDetail> finTypeFees, List<FinFeeDetail> feeList) {

		if (CollectionUtils.isEmpty(feeList)) {
			return finTypeFees;
		}

		for (FinFeeDetail finTypeFee : finTypeFees) {
			for (FinFeeDetail fee : feeList) {
				if (StringUtils.equals(finTypeFee.getFeeTypeCode(), fee.getFeeTypeCode())
						&& StringUtils.equals(finTypeFee.getFinEvent(), fee.getFinEvent())) {
					finTypeFee.setFeeScheduleMethod(fee.getFeeScheduleMethod());
					finTypeFee.setPaidAmount(fee.getPaidAmount());
					finTypeFee.setTerms(fee.getTerms());
					finTypeFee.setWaivedAmount(fee.getWaivedAmount());

					if (finTypeFee.isTaxApplicable()) {
						if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(finTypeFee.getTaxComponent())) {
							finTypeFee.setActualAmountOriginal(fee.getActualAmount());
							finTypeFee.setActualAmount(fee.getActualAmount());
						} else if (FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE.equals(finTypeFee.getTaxComponent())) {
							finTypeFee.setNetAmount(fee.getActualAmount());
							finTypeFee.setActualAmount(fee.getActualAmount());
						}
						finTypeFee.setPaidAmount(fee.getPaidAmount());
					} else {
						finTypeFee.setActualAmountOriginal(fee.getActualAmount());
						finTypeFee.setActualAmountGST(BigDecimal.ZERO);
						finTypeFee.setActualAmount(fee.getActualAmount());

						BigDecimal netAmountOriginal = finTypeFee.getActualAmountOriginal()
								.subtract(finTypeFee.getWaivedAmount());

						finTypeFee.setNetAmountOriginal(netAmountOriginal);
						finTypeFee.setNetAmountGST(BigDecimal.ZERO);
						finTypeFee.setNetAmount(netAmountOriginal);
						finTypeFee.setPaidAmount(fee.getPaidAmount());
					}
				}
			}
		}
		return finTypeFees;
	}

	private void calculateFeePercentageAmount(FinScheduleData schdData, FinanceDetail fd, boolean enquiry,
			Map<String, BigDecimal> gstPercentages, List<FinFeeDetail> feeList) {

		if (CollectionUtils.isEmpty(feeList)) {
			return;
		}

		Date valueDate = SysParamUtil.getAppDate();

		for (FinFeeDetail fee : feeList) {
			if (PennantConstants.FEE_CALCULATION_TYPE_PERCENTAGE.equals(fee.getCalculationType())) {
				BigDecimal calPercentageFee = getCalculatedPercentageFee(fee, schdData, valueDate);
				fee.setCalculatedAmount(calPercentageFee);

				if (CalculationConstants.REMFEE_WAIVED_BY_BANK.equals(fee.getFeeScheduleMethod())) {
					fee.setWaivedAmount(calPercentageFee);
				}

				if (fee.isTaxApplicable()) {
					if (enquiry) {
						this.finFeeDetailService.processGSTCalForPercentage(fee, calPercentageFee, fd, gstPercentages,
								enquiry);
					} else {
						this.finFeeDetailService.processGSTCalForPercentage(fee, fee.getActualAmount(), fd,
								gstPercentages, enquiry);
					}

				} else {
					if (enquiry) {
						fee.setActualAmount(calPercentageFee);
						fee.setActualAmountOriginal(calPercentageFee);
					}
					fee.setRemainingFee(
							fee.getActualAmount().subtract(fee.getPaidAmount()).subtract(fee.getWaivedAmount()));
				}
			}
		}

	}

	private BigDecimal getCalculatedPercentageFee(FinFeeDetail fee, FinScheduleData schdData, Date valueDate) {
		BigDecimal calculatedAmt = BigDecimal.ZERO;
		FinanceMain fm = schdData.getFinanceMain();
		switch (fee.getCalculateOn()) {
		case PennantConstants.FEE_CALCULATEDON_TOTALASSETVALUE:
			calculatedAmt = fm.getFinAssetValue();
			break;
		case PennantConstants.FEE_CALCULATEDON_LOANAMOUNT:
			calculatedAmt = fm.getFinAmount().subtract(fm.getDownPayment());
			break;
		case PennantConstants.FEE_CALCULATEDON_OUTSTANDINGPRCINCIPAL:
			calculatedAmt = fm.getFinCurrAssetValue().add(fm.getFeeChargeAmt()).subtract(fm.getFinRepaymentAmount());
			break;
		case PennantConstants.FEE_CALCULATEDON_OUTSTANDPRINCIFUTURE:
			List<FinanceScheduleDetail> schdList = schdData.getFinanceScheduleDetails();

			for (FinanceScheduleDetail schd : schdList) {
				if (DateUtil.compare(valueDate, schd.getSchDate()) == 0) {
					calculatedAmt = schd.getClosingBalance();

					if (calculatedAmt.compareTo(BigDecimal.ZERO) == 0) {
						List<FinanceScheduleDetail> apdSchdList = financeDetailService
								.getFinScheduleList(fm.getFinID());
						for (FinanceScheduleDetail curSchd : apdSchdList) {
							if (DateUtil.compare(valueDate, curSchd.getSchDate()) == 0) {
								calculatedAmt = curSchd.getClosingBalance();
							}
							if (DateUtil.compare(valueDate, curSchd.getSchDate()) <= 0) {
								break;
							}
							calculatedAmt = curSchd.getClosingBalance();
						}
						break;
					}
				}

				if (DateUtil.compare(valueDate, schd.getSchDate()) <= 0) {
					break;
				}

				calculatedAmt = schd.getClosingBalance();
			}
		default:
			break;
		}
		calculatedAmt = calculatedAmt.multiply(fee.getPercentage()).divide(BigDecimal.valueOf(100), 0,
				RoundingMode.HALF_DOWN);

		calculatedAmt = CalculationUtil.roundAmount(calculatedAmt, fm.getCalRoundingMode(), fm.getRoundingTarget());
		return calculatedAmt;
	}

	/**
	 * 
	 * 
	 * @param fd
	 */
	private List<FinFeeDetail> doSetFeeChanges(FinanceDetail fd, boolean isOriginationFee, List<FinFeeDetail> feeList) {
		logger.debug(Literal.ENTERING);

		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		long finID = fm.getFinID();
		String finReference = fm.getFinReference();

		for (FinFeeDetail fee : feeList) {
			fee.setRecordType(PennantConstants.RCD_ADD);
			fee.setFinID(finID);
			fee.setFinReference(finReference);
			fee.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			fee.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		}
		Map<String, FinFeeDetail> feeDetailMap = new HashMap<String, FinFeeDetail>();

		for (FinFeeDetail fee : feeList) {
			if (!fee.isNewRecord()) {
				if (!fee.isRcdVisible() && StringUtils.equals(fee.getRecordType(), PennantConstants.RECORD_TYPE_CAN)) {
					fee.setRcdVisible(true);
					fee.setDataModified(true);
					fee.setNewRecord(false);
					fee.setRecordType(PennantConstants.RCD_ADD);
					feeDetailMap.put(getUniqueID(fee), fee);
				} else {
					fee.setVersion(fee.getVersion() + 1);
					fee.setRecordType(PennantConstants.RECORD_TYPE_CAN);
					fee.setRcdVisible(false);
					fee.setDataModified(true);
					feeDetailMap.put(getUniqueID(fee), fee);
				}
			}
		}

		/*
		 * List<FinFeeDetail> finFeeDetailListNew = convertToFinanceFees(financeDetail.getFinTypeFeesList(),
		 * finScheduleData.getFinanceMain().getFinReference());
		 */
		for (FinFeeDetail fee : feeList) {
			if (!feeDetailMap.containsKey(getUniqueID(fee))) {
				feeDetailMap.put(getUniqueID(fee), fee);
			}
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<FinFeeDetail>(feeDetailMap.values());
	}

	private List<FinFeeDetail> convertToFinanceFees(FinanceDetail fd, Long finID, String reference) {
		List<FinTypeFees> finTypeFeesList = fd.getFinTypeFeesList();
		List<FinFeeDetail> finFeeDetails = new ArrayList<FinFeeDetail>();

		if (CollectionUtils.isEmpty(finTypeFeesList)) {
			return finFeeDetails;
		}

		FinFeeDetail fee = null;
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
			fee.setFixedAmount(feeType.getAmount());
			fee.setPercentage(feeType.getPercentage());
			fee.setCalculateOn(feeType.getCalculateOn());
			fee.setAlwDeviation(feeType.isAlwDeviation());
			fee.setMaxWaiverPerc(feeType.getMaxWaiverPerc());
			fee.setAlwModifyFee(feeType.isAlwModifyFee());
			fee.setAlwModifyFeeSchdMthd(feeType.isAlwModifyFeeSchdMthd());
			fee.setAlwPreIncomization(feeType.isAlwPreIncomization());

			fee.setCalculatedAmount(feeType.getAmount());

			fee.setTaxComponent(feeType.getTaxComponent());
			fee.setTaxApplicable(feeType.isTaxApplicable());

			if (feeType.isTaxApplicable()) {
				this.finFeeDetailService.convertGSTFinTypeFees(fee, feeType, fd, getGSTPercentages(fd));
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

			fee.setRecordType(PennantConstants.RCD_ADD);
			fee.setFinID(finID);
			fee.setFinReference(reference);
			fee.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			fee.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			finFeeDetails.add(fee);
		}
		return finFeeDetails;
	}

	public FinFeeDetail setFinFeeDetails(FinTypeFees feeType, FinFeeDetail fee, Map<String, BigDecimal> taxPercentages,
			String currency) {

		FinanceDetail fd = new FinanceDetail();
		FinScheduleData schdData = new FinScheduleData();
		FinanceMain fm = new FinanceMain();
		fm.setFinCcy(currency);
		schdData.setFinanceMain(fm);
		fd.setFinScheduleData(schdData);

		if (feeType == null) {
			return fee;
		}

		fee.setNewRecord(true);
		fee.setOriginationFee(feeType.isOriginationFee());
		fee.setFinEvent(feeType.getFinEvent());
		fee.setFinEventDesc(feeType.getFinEventDesc());
		fee.setFeeTypeID(feeType.getFeeTypeID());
		fee.setFeeOrder(feeType.getFeeOrder());
		fee.setFeeTypeCode(feeType.getFeeTypeCode());
		fee.setFeeTypeDesc(feeType.getFeeTypeDesc());
		fee.setAlwPreIncomization(feeType.isAlwPreIncomization());
		fee.setFeeScheduleMethod(feeType.getFeeScheduleMethod());
		fee.setCalculationType(feeType.getCalculationType());
		fee.setRuleCode(feeType.getRuleCode());
		fee.setFixedAmount(feeType.getAmount());
		fee.setPercentage(feeType.getPercentage());
		fee.setCalculateOn(feeType.getCalculateOn());
		fee.setAlwDeviation(feeType.isAlwDeviation());
		fee.setMaxWaiverPerc(feeType.getMaxWaiverPerc());
		fee.setAlwModifyFee(feeType.isAlwModifyFee());
		fee.setAlwModifyFeeSchdMthd(feeType.isAlwModifyFeeSchdMthd());
		fee.setPrvTaxComponent(feeType.getTaxComponent());
		fee.setTaxComponent(FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE);
		fee.setTaxApplicable(feeType.isTaxApplicable());
		fee.setCalculatedAmount(feeType.getAmount());

		fee.setTaxComponent(feeType.getTaxComponent());
		fee.setTaxApplicable(feeType.isTaxApplicable());

		if (feeType.isTaxApplicable()) {
			BigDecimal totalGST = BigDecimal.ZERO;
			String taxComponent = fee.getTaxComponent();
			BigDecimal taxableAmount = fee.getActualAmount();
			BigDecimal waivedAmount = fee.getWaivedAmount();

			if (FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE.equals(taxComponent)) {
				totalGST = GSTCalculator.getInclusiveGST(taxableAmount, waivedAmount, taxPercentages).gettGST();
				fee.setNetAmount(taxableAmount);
				fee.setNetAmountOriginal(totalGST);
				fee.setNetAmountGST(taxableAmount.subtract(totalGST));
				fee.setActualAmountOriginal(totalGST.add(waivedAmount));
				fee.setActualAmountGST(totalGST);
				fee.setActualAmount(fee.getActualAmountOriginal().add(totalGST));

				fee.setPaidAmountOriginal(totalGST.add(waivedAmount));
				fee.setPaidAmountGST(totalGST);
			} else if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(fee.getTaxComponent())) {
				totalGST = GSTCalculator.getExclusiveGST(taxableAmount, taxPercentages).gettGST();
				fee.setNetAmountOriginal(taxableAmount.subtract(fee.getWaivedAmount()));
				fee.setNetAmountGST(totalGST);
				fee.setNetAmount(fee.getNetAmountOriginal().add(totalGST));
				fee.setActualAmount(fee.getActualAmountOriginal().add(totalGST));

				// finFeeDetail.setPaidAmount(finFeeDetail.getActualAmountOriginal().add(actualGst));
				fee.setPaidAmountOriginal(fee.getNetAmountOriginal());
				fee.setPaidAmount(fee.getNetAmountOriginal().add(totalGST));
				fee.setPaidAmountGST(totalGST);
			}

			// this.finFeeDetailService.convertGSTFinTypeFees(finFeeDetail, finTypeFee, financeDetail,
			// gstExecutionMap);
			this.finFeeDetailService.calculateFees(fee, fm, taxPercentages);
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
			fee.setRemainingFee(fee.getActualAmount().subtract(fee.getWaivedAmount()).subtract(fee.getPaidAmount()));
		}

		fee.setTaxComponent(fee.getPrvTaxComponent());
		fee.setRecordType(PennantConstants.RCD_ADD);
		fee.setFinReference(null);
		fee.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		fee.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		return fee;
	}

	private List<FinFeeDetail> convertToFinanceFees(List<FinTypeFees> feeTypeList, Long finID, String reference) {
		List<FinFeeDetail> feeList = new ArrayList<FinFeeDetail>();

		if (feeTypeList == null) {
			return feeList;
		}

		FinFeeDetail fee = null;
		for (FinTypeFees feeType : feeTypeList) {
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
			fee.setFixedAmount(feeType.getAmount());
			fee.setPercentage(feeType.getPercentage());
			fee.setCalculateOn(feeType.getCalculateOn());
			fee.setAlwDeviation(feeType.isAlwDeviation());
			fee.setMaxWaiverPerc(feeType.getMaxWaiverPerc());
			fee.setAlwModifyFee(feeType.isAlwModifyFee());
			fee.setAlwModifyFeeSchdMthd(feeType.isAlwModifyFeeSchdMthd());

			fee.setCalculatedAmount(feeType.getAmount());
			fee.setActualAmount(feeType.getAmount());
			if (StringUtils.equals(feeType.getFeeScheduleMethod(), CalculationConstants.REMFEE_PAID_BY_CUSTOMER)) {
				fee.setPaidAmount(feeType.getAmount());
			}
			if (StringUtils.equals(feeType.getFeeScheduleMethod(), CalculationConstants.REMFEE_WAIVED_BY_BANK)) {
				fee.setWaivedAmount(feeType.getAmount());
			}
			fee.setRemainingFee(fee.getActualAmount().subtract(fee.getWaivedAmount()).subtract(fee.getPaidAmount()));

			fee.setRecordType(PennantConstants.RCD_ADD);
			fee.setFinID(finID);
			fee.setFinReference(reference);
			fee.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			fee.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			feeList.add(fee);
		}
		return feeList;
	}

	public void doExecuteFeeCharges(FinanceDetail fd, String finEvent, FinServiceInstruction fsi, boolean enquiry)
			throws AppException {
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		Long finID = fm.getFinID();
		String finReference = fm.getFinReference();
		boolean isOriginationFee = false;

		if (StringUtils.isBlank(finEvent)) {
			isOriginationFee = true;
			finEvent = PennantApplicationUtil.getEventCode(fm.getFinStartDate());
		}

		schdData.setFeeEvent(finEvent);
		if (!schdData.getFinanceType().isPromotionType()) {
			fd.setFinTypeFeesList(financeDetailService.getFinTypeFees(fm.getFinType(), finEvent, isOriginationFee,
					FinanceConstants.MODULEID_FINTYPE));
		} else {
			fd.setFinTypeFeesList(financeDetailService.getFinTypeFees(schdData.getFinanceType().getPromotionCode(),
					finEvent, isOriginationFee, FinanceConstants.MODULEID_PROMOTION));
		}
		if (isOriginationFee) {
			for (FinFeeDetail fee : schdData.getFinFeeDetailList()) {
				if (StringUtils.equals(fee.getFinEvent(), AccountingEvent.VAS_FEE)) {
					fee.setNewRecord(true);
					fee.setRecordType(PennantConstants.RCD_ADD);
					fee.setFinID(finID);
					fee.setFinReference(finReference);
					fee.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
					fee.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					fee.setLastMntBy(fm.getLastMntBy());
					fee.setOriginationFee(true);
					fee.setFeeTypeID(0);
					fee.setFeeSeq(0);
				} else {
					fee.setFinEvent(finEvent);
				}
			}
		}
		executeFeeCharges(fd, isOriginationFee, fsi, enquiry);
	}

	private String getUniqueID(FinFeeDetail finFeeDetail) {
		return StringUtils.trimToEmpty(finFeeDetail.getFinEvent()) + "_" + String.valueOf(finFeeDetail.getFeeTypeID());
	}

	private Map<String, BigDecimal> getGSTPercentages(FinanceDetail fd) {
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();
		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		String userBranch = userDetails.getBranchCode();
		String finBranch = fm.getFinBranch();
		String finCCY = fm.getFinCcy();

		long custId = 0;
		if (fd.getCustomerDetails() != null) {
			custId = fd.getCustomerDetails().getCustomer().getCustID();
		}

		return GSTCalculator.getTaxPercentages(custId, finCCY, userBranch, finBranch, fd.getFinanceTaxDetail());
	}

	private Map<String, BigDecimal> getDealerTaxPercentages(FinanceDetail fd) {
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();
		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		String userBranch = userDetails.getBranchCode();
		String finBranch = fm.getFinBranch();
		String finCCY = fm.getFinCcy();

		Map<String, BigDecimal> taxPercentages = GSTCalculator.getDealerTaxPercentages(fm.getManufacturerDealerId(),
				finCCY, userBranch, finBranch, fd.getFinanceTaxDetail());

		return taxPercentages;
	}

	public Map<String, Object> prepareFeeRulesMap(AEAmountCodes amountCodes, Map<String, Object> dataMap,
			FinanceDetail fd) {
		logger.debug(Literal.ENTERING);

		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();
		long finID = fm.getFinID();

		List<FinFeeDetail> fees = schdData.getFinFeeDetailList();

		if (CollectionUtils.isEmpty(fees)) {
			return dataMap;
		}

		FeeRule feeRule;
		BigDecimal deductFeeDisb = BigDecimal.ZERO;
		BigDecimal addFeeToFinance = BigDecimal.ZERO;
		BigDecimal paidFee = BigDecimal.ZERO;
		BigDecimal feeWaived = BigDecimal.ZERO;

		// VAS
		BigDecimal deductVasDisb = BigDecimal.ZERO;
		BigDecimal addVasToFinance = BigDecimal.ZERO;
		BigDecimal paidVasFee = BigDecimal.ZERO;
		BigDecimal vasFeeWaived = BigDecimal.ZERO;

		BigDecimal unIncomized = BigDecimal.ZERO;

		for (FinFeeDetail fee : fees) {
			feeRule = new FeeRule();
			boolean isPreIncomized = false;
			if (fee.isAlwPreIncomization()
					&& fee.getActualAmount().subtract(fee.getPaidAmount()).compareTo(BigDecimal.ZERO) == 0) {
				isPreIncomized = true;
			}

			String feeTypeCode = fee.getFeeTypeCode();
			feeRule.setFeeCode(feeTypeCode);
			feeRule.setFeeAmount(fee.getActualAmount());
			feeRule.setWaiverAmount(fee.getWaivedAmount());
			feeRule.setPaidAmount(fee.getPaidAmount());
			feeRule.setFeeToFinance(fee.getFeeScheduleMethod());
			feeRule.setFeeMethod(fee.getFeeScheduleMethod());

			if (fee.getFeeScheduleMethod().equals(CalculationConstants.REMFEE_PART_OF_DISBURSE)) {
				deductFeeDisb = deductFeeDisb.add(fee.getRemainingFee());
				if (AccountingEvent.VAS_FEE.equals(fee.getFinEvent())) {
					deductVasDisb = deductVasDisb.add(fee.getRemainingFee());
				}
			} else if (fee.getFeeScheduleMethod().equals(CalculationConstants.REMFEE_PART_OF_SALE_PRICE)) {
				addFeeToFinance = addFeeToFinance.add(fee.getRemainingFee());
				if (AccountingEvent.VAS_FEE.equals(fee.getFinEvent())) {
					addVasToFinance = addVasToFinance.add(fee.getRemainingFee());
				}
			}
			if (!isPreIncomized) {
				unIncomized = unIncomized.add(fee.getPaidAmount());
			}
			paidFee = paidFee.add(fee.getPaidAmount());
			feeWaived = feeWaived.add(fee.getWaivedAmount());

			if (AccountingEvent.VAS_FEE.equals(fee.getFinEvent())) {
				paidVasFee = paidVasFee.add(fee.getPaidAmount());
				vasFeeWaived = vasFeeWaived.add(fee.getWaivedAmount());
			}

			dataMap.putAll(FeeCalculator.getFeeRuleMap(fee));
		}

		amountCodes.setDeductFeeDisb(deductFeeDisb);
		amountCodes.setDeductVasDisb(deductVasDisb);
		amountCodes.setAddFeeToFinance(addFeeToFinance);
		amountCodes.setFeeWaived(feeWaived);
		amountCodes.setPaidFee(paidFee);
		amountCodes.setImdAmount(unIncomized);

		dataMap.put("VAS_DD", deductVasDisb);
		dataMap.put("VAS_AF", addVasToFinance);
		dataMap.put("VAS_W", vasFeeWaived);
		dataMap.put("VAS_P", paidVasFee);

		for (FinFeeDetail fee : fees) {
			String vasProductCode = fee.getVasProductCode();
			if (!AccountingEvent.VAS_FEE.equals(fee.getFinEvent())) {
				continue;
			}

			if (CalculationConstants.REMFEE_PART_OF_DISBURSE.equals(fee.getFeeScheduleMethod())) {
				dataMap.put("VAS_" + vasProductCode + "_DD", fee.getRemainingFee());
				dataMap.put("VAS_" + vasProductCode + "_AF", BigDecimal.ZERO);
			} else {
				dataMap.put("VAS_" + vasProductCode + "_DD", BigDecimal.ZERO);
				dataMap.put("VAS_" + vasProductCode + "_AF", fee.getRemainingFee());
			}

			dataMap.put("VAS_" + vasProductCode + "_W", fee.getWaivedAmount());
			dataMap.put("VAS_" + vasProductCode + "_P", fee.getActualAmount());
		}

		CustomerDetails cd = fd.getCustomerDetails();

		List<FinFeeReceipt> upfront = schdData.getFinFeeReceipts();
		Map<Long, List<FinFeeReceipt>> upfrontMap = finFeeDetailService.getUpfromtReceiptMap(upfront);
		amountCodes.setToExcessAmt(finFeeDetailService.getExcessAmount(finID, upfrontMap, cd.getCustID()));

		logger.debug(Literal.LEAVING);
		return dataMap;
	}

	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	public void setRuleService(RuleService ruleService) {
		this.ruleService = ruleService;
	}

	public void setFinFeeDetailService(FinFeeDetailService finFeeDetailService) {
		this.finFeeDetailService = finFeeDetailService;
	}
}
