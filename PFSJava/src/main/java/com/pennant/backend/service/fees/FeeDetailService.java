/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 *******************************************************************************************************
 *                                 FILE HEADER                                              			*
 *******************************************************************************************************
 *
 * FileName    		:  FeeDetailService.java															*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES															*
 *                                                                  
 * Creation Date    :  11-07-2017																		*
 *                                                                  
 * Modified Date    :  11-07-2018																		*
 *                                                                  
 * Description 		:												 									*                                 
 *                                                                                          
 ********************************************************************************************************
 * Date             Author                   Version      Comments                          			*
 ********************************************************************************************************
 * 11-07-2018       Pennant	                 0.1                                            			*	 
 *                                                                                          			* 
 * 11-07-2018       Satya	                 0.2          PSD - Ticket : 127846							*
 * 														  Changes related to Fees calculation for the 	*
 * 														  selection type DropLinePOS.			 		*
 * 																										* 
 *                                                                                          			* 
 *                                                                                          			* 
 *                                                                                          			* 
 *                                                                                          			* 
 *                                                                                          			* 
 *                                                                                   					*
 *                                                                                          			*
 *                                                                                          			* 
 ********************************************************************************************************
 */
package com.pennant.backend.service.fees;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.GSTCalculator;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.app.util.SessionUserDetails;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.rmtmasters.FinTypeFees;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rmtmasters.Promotion;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.service.finance.FinFeeDetailService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.rulefactory.RuleService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RuleConstants;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;

public class FeeDetailService {
	Logger logger = Logger.getLogger(FeeDetailService.class);

	private FinanceDetailService financeDetailService;
	private RuleExecutionUtil ruleExecutionUtil;
	private RuleService ruleService;
	private FinFeeDetailService finFeeDetailService;

	private List<FinFeeDetail> finFeeDetailList = new ArrayList<FinFeeDetail>();

	/**
	 * Calculate and execute fee details
	 * 
	 * @param financeDetail
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	private void executeFeeCharges(FinanceDetail financeDetail, boolean isOriginationFee,
			FinServiceInstruction finServiceInst, boolean enquiry)
			throws IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();

		// set FinType fees details
		String finReference = finScheduleData.getFinanceMain().getFinReference();
		finFeeDetailList = convertToFinanceFees(financeDetail, finReference);

		finFeeDetailList = prepareActualFinFees(finFeeDetailList, finScheduleData.getFinFeeDetailList());

		// Organize Fee detail changes
		finFeeDetailList = doSetFeeChanges(financeDetail, isOriginationFee, finFeeDetailList);

		List<String> feeRuleCodes = new ArrayList<String>();

		Map<String, BigDecimal> gstPercentages = getGSTPercentages(financeDetail);

		for (FinFeeDetail finFeeDetail : finFeeDetailList) {
			finFeeDetail.setRecordType(PennantConstants.RCD_ADD);
			finFeeDetail.setFinReference(finScheduleData.getFinanceMain().getFinReference());
			finFeeDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			finFeeDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			finFeeDetail.setLastMntBy(financeDetail.getFinScheduleData().getFinanceMain().getLastMntBy());
			finFeeDetail.setFinEvent(finScheduleData.getFeeEvent());
			if (StringUtils.isNotEmpty(finFeeDetail.getRuleCode())) {
				feeRuleCodes.add(finFeeDetail.getRuleCode());
			}
			if (StringUtils.equals(FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE, finFeeDetail.getTaxComponent())) {
				BigDecimal totalGST = GSTCalculator.getExclusiveGST(finFeeDetail.getPaidAmount(), gstPercentages)
						.gettGST();
				finFeeDetail.setPaidAmountOriginal(totalGST);
				finFeeDetail.setPaidAmount(finFeeDetail.getPaidAmountOriginal());
			}
		}

		// Execute fee rules if exists
		if (feeRuleCodes.size() > 0) {
			doExecuteFeeRules(financeDetail, finServiceInst, finScheduleData, feeRuleCodes, enquiry, gstPercentages,
					finFeeDetailList);
		}

		// calculate fee percentage if exists
		calculateFeePercentageAmount(finScheduleData, financeDetail, enquiry, gstPercentages, finFeeDetailList);

		// Validate Provided fee details with configured fee details
		validateFeeConfig(finFeeDetailList, finScheduleData);

		// Calculating GST
		for (FinFeeDetail finFeeDetail : finFeeDetailList) {
			this.finFeeDetailService.calculateFees(finFeeDetail, finScheduleData.getFinanceMain(),
					getGSTPercentages(financeDetail));
		}

		// add vas recording fees into actual list
		if (finScheduleData.getVasRecordingList() != null && !finScheduleData.getVasRecordingList().isEmpty()) {
			List<FinFeeDetail> vasFees = new ArrayList<FinFeeDetail>();
			for (FinFeeDetail detail : finScheduleData.getFinFeeDetailList()) {
				if (StringUtils.equals(detail.getFinEvent(), AccountEventConstants.ACCEVENT_VAS_FEE)) {
					detail.setRemainingFee(detail.getActualAmount().subtract(detail.getPaidAmount())
							.subtract(detail.getWaivedAmount()));
					vasFees.add(detail);
				}
			}
			finFeeDetailList.addAll(vasFees);
		}

		setFeeAmount(financeDetail.getModuleDefiner(), finScheduleData, finFeeDetailList);

		for (FinFeeDetail prvFeeDetail : finScheduleData.getFinFeeDetailList()) {
			for (FinFeeDetail currFeeDetail : finFeeDetailList) {
				if (StringUtils.equals(prvFeeDetail.getFinEvent(), currFeeDetail.getFinEvent())
						&& StringUtils.equals(prvFeeDetail.getFeeTypeCode(), currFeeDetail.getFeeTypeCode())) {
					BeanUtils.copyProperties(prvFeeDetail, currFeeDetail);
				}
			}
		}

		// Calculating GST
		for (FinFeeDetail finFeeDetail : finFeeDetailList) {
			this.finFeeDetailService.calculateFees(finFeeDetail, finScheduleData.getFinanceMain(),
					getGSTPercentages(financeDetail));
		}

		if (AccountEventConstants.ACCEVENT_EARLYSTL.equals(finScheduleData.getFinanceMain().getFinSourceID())) {
			finScheduleData.setFinFeeDetailList(finFeeDetailList);
		}

		logger.debug("Leaving");
	}

	public void setFeeAmount(String moduleDefiner, FinScheduleData finScheduleData, List<FinFeeDetail> fees) {
		FinanceMain financeMain = finScheduleData.getFinanceMain();

		BigDecimal deductFromDisbursement = BigDecimal.ZERO;
		BigDecimal feeAddToDisbTot = BigDecimal.ZERO;

		for (FinFeeDetail fee : fees) {
			if (AccountEventConstants.ACCEVENT_VAS_FEE.equals(fee.getFinEvent())
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

			// Excluding GST & TDS -- SO COMMENTED
			//fee.setRemainingFee(fee.getActualAmount().subtract(fee.getPaidAmount()).subtract(fee.getWaivedAmount()));
		}

		if (StringUtils.equals(FinanceConstants.FINSER_EVENT_ORG, moduleDefiner)) {
			financeMain.setDeductFeeDisb(deductFromDisbursement);
			financeMain.setFeeChargeAmt(feeAddToDisbTot);
		} else {
			if (CollectionUtils.isNotEmpty(finScheduleData.getDisbursementDetails())) {
				List<Integer> approvedDisbSeq = financeDetailService
						.getFinanceDisbSeqs(finScheduleData.getFinanceMain().getFinReference(), false);
				for (FinanceDisbursement disbursement : finScheduleData.getDisbursementDetails()) {
					if (!approvedDisbSeq.contains(disbursement.getDisbSeq())) {
						disbursement.setDeductFeeDisb(deductFromDisbursement);
						break;
					}
				}
			}
		}
	}

	private void validateFeeConfig(List<FinFeeDetail> finFeeDetails, FinScheduleData finScheduleData) {
		logger.debug("Entering");

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
			//finWaiverAmount = PennantApplicationUtil.unFormateAmount(finWaiverAmount, formatter);

			if (finFeeDetail.getWaivedAmount().compareTo(finWaiverAmount) > 0) {
				String[] valueParm = new String[3];
				valueParm[0] = "Waiver amount";
				valueParm[1] = "Actual waiver amount:" + String.valueOf(finWaiverAmount);
				valueParm[2] = finFeeDetail.getFeeTypeCode();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90257", valueParm)));
				finScheduleData.setErrorDetails(errorDetails);
			}

			//finFeeDetail.setRemainingFee(finFeeDetail.getActualAmount().subtract(finFeeDetail.getPaidAmount()).subtract(finFeeDetail.getWaivedAmount()));

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
		logger.debug("Leaving");
	}

	/**
	 * Method to process fees and charge details for Inquiry services
	 * 
	 * @param financeDetail
	 * @param finEvent
	 * @param finServiceInst
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public void doProcessFeesForInquiry(FinanceDetail financeDetail, String finEvent,
			FinServiceInstruction finServiceInst, boolean enquiry)
			throws IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		boolean isOrigination = false;
		if (finServiceInst == null) {
			isOrigination = true;
		}

		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

		if (StringUtils.isBlank(financeMain.getPromotionCode()) || financeMain.getPromotionSeqId() == 0) {
			financeDetail.setFinTypeFeesList(financeDetailService.getFinTypeFees(financeMain.getFinType(), finEvent,
					isOrigination, FinanceConstants.MODULEID_FINTYPE));
		} else {
			Promotion promotion = financeDetail.getFinScheduleData().getPromotion();
			financeDetail.setFinTypeFeesList(financeDetailService.getSchemeFeesList(promotion.getReferenceID(),
					finEvent, isOrigination, FinanceConstants.MODULEID_PROMOTION));
		}

		financeDetail.getFinScheduleData().setFeeEvent(finEvent);
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();

		// set FinType fees details
		String finReference = finScheduleData.getFinanceMain().getFinReference();
		finFeeDetailList = convertToFinanceFees(financeDetail, finReference);
		finFeeDetailList = prepareActualFinFees(finFeeDetailList, finScheduleData.getFinFeeDetailList());

		// Organize Fee detail changes
		//doSetFeeChanges(financeDetail, isOriginationFee);

		List<String> feeRuleCodes = new ArrayList<String>();
		for (FinFeeDetail finFeeDetail : finFeeDetailList) {
			finFeeDetail.setRecordType(PennantConstants.RCD_ADD);
			finFeeDetail.setFinReference(finScheduleData.getFinanceMain().getFinReference());
			finFeeDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			finFeeDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			finFeeDetail.setLastMntBy(financeDetail.getFinScheduleData().getFinanceMain().getLastMntBy());
			finFeeDetail.setFinEvent(finScheduleData.getFeeEvent());
			if (StringUtils.isNotEmpty(finFeeDetail.getRuleCode())) {
				feeRuleCodes.add(finFeeDetail.getRuleCode());
			}
		}

		Map<String, BigDecimal> gstPercentages = getGSTPercentages(financeDetail);

		// Execute fee rules if exists
		if (feeRuleCodes.size() > 0) {
			doExecuteFeeRules(financeDetail, finServiceInst, finScheduleData, feeRuleCodes, enquiry, gstPercentages,
					finFeeDetailList);
		}

		// calculate fee percentage if exists
		calculateFeePercentageAmount(finScheduleData, financeDetail, enquiry, gstPercentages, finFeeDetailList);

		// Calculating GST
		for (FinFeeDetail finFeeDetail : finFeeDetailList) {
			this.finFeeDetailService.calculateFees(finFeeDetail, financeMain, getGSTPercentages(financeDetail));
		}

		// set Actual calculated values into feeDetails for Inquiry purpose
		for (FinFeeDetail finFeeDetail : finFeeDetailList) {
			if (!isOrigination) {
				finFeeDetail.setPaidAmount(finFeeDetail.getActualAmount());
			}
			finFeeDetail.setRemainingFee(finFeeDetail.getActualAmount().subtract(finFeeDetail.getPaidAmount())
					.subtract(finFeeDetail.getWaivedAmount()));
			if (CalculationConstants.REMFEE_SCHD_TO_N_INSTALLMENTS.equals(finFeeDetail.getFeeScheduleMethod())
					&& finFeeDetail.getTerms() <= 0) {
				finFeeDetail.setTerms(1);
			}
		}

		setFeeAmount(financeDetail.getModuleDefiner(), finScheduleData, finFeeDetailList);

		for (FinFeeDetail prvFeeDetail : finScheduleData.getFinFeeDetailList()) {
			for (FinFeeDetail currFeeDetail : finFeeDetailList) {
				if (StringUtils.equals(prvFeeDetail.getFinEvent(), currFeeDetail.getFinEvent())
						&& StringUtils.equals(prvFeeDetail.getFeeTypeCode(), currFeeDetail.getFeeTypeCode())) {
					BeanUtils.copyProperties(prvFeeDetail, currFeeDetail);
				}
			}
		}

		finScheduleData.getFinFeeDetailList().addAll(finFeeDetailList);

		//Calculating GST
		for (FinFeeDetail finFeeDetail : finScheduleData.getFinFeeDetailList()) {
			this.finFeeDetailService.calculateFees(finFeeDetail, financeMain, gstPercentages);
		}

		logger.debug("Leaving");

	}

	public void doProcessFeesForInquiryForUpload(FinanceDetail financeDetail, String finEvent,
			FinServiceInstruction finServiceInst, boolean enquiry)
			throws IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");
		//PSD# to fix the concurrent issue
		List<FinFeeDetail> finFeeDetailList = new ArrayList<FinFeeDetail>();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		if (!financeDetail.getFinScheduleData().getFinanceType().isPromotionType()) {
			financeDetail.setFinTypeFeesList(financeDetailService.getFinTypeFees(financeMain.getFinType(), finEvent,
					false, FinanceConstants.MODULEID_FINTYPE));
		} else {
			String promotionType = financeDetail.getFinScheduleData().getFinanceType().getPromotionCode();
			financeDetail.setFinTypeFeesList(financeDetailService.getFinTypeFees(promotionType, finEvent, false,
					FinanceConstants.MODULEID_PROMOTION));
		}
		financeDetail.getFinScheduleData().setFeeEvent(finEvent);
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();

		// set FinType fees details
		String finReference = finScheduleData.getFinanceMain().getFinReference();
		finFeeDetailList = convertToFinanceFees(financeDetail.getFinTypeFeesList(), finReference);
		finFeeDetailList = prepareActualFinFees(finFeeDetailList, finScheduleData.getFinFeeDetailList());

		// Organize Fee detail changes
		//doSetFeeChanges(financeDetail, isOriginationFee);

		List<String> feeRuleCodes = new ArrayList<String>();
		for (FinFeeDetail finFeeDetail : finFeeDetailList) {
			finFeeDetail.setRecordType(PennantConstants.RCD_ADD);
			finFeeDetail.setFinReference(finScheduleData.getFinanceMain().getFinReference());
			finFeeDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			finFeeDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			finFeeDetail.setLastMntBy(financeDetail.getFinScheduleData().getFinanceMain().getLastMntBy());
			finFeeDetail.setFinEvent(finScheduleData.getFeeEvent());
			if (StringUtils.isNotEmpty(finFeeDetail.getRuleCode())) {
				feeRuleCodes.add(finFeeDetail.getRuleCode());
			}
		}

		// Execute fee rules if exists
		if (feeRuleCodes.size() > 0) {
			doExecuteFeeRules(financeDetail, finServiceInst, finScheduleData, feeRuleCodes, enquiry, new HashMap<>(),
					finFeeDetailList);
		}

		// calculate fee percentage if exists
		calculateFeePercentageAmount(finScheduleData, financeDetail, enquiry, new HashMap<>(), finFeeDetailList);

		// set Actual calculated values into feeDetails for Inquiry purpose
		for (FinFeeDetail finFeeDetail : finFeeDetailList) {
			finFeeDetail.setPaidAmount(finFeeDetail.getActualAmount());
			finFeeDetail.setRemainingFee(finFeeDetail.getActualAmount().subtract(finFeeDetail.getPaidAmount())
					.subtract(finFeeDetail.getWaivedAmount()));
			if (StringUtils.equals(finFeeDetail.getFeeScheduleMethod(),
					CalculationConstants.REMFEE_SCHD_TO_N_INSTALLMENTS)) {
				finFeeDetail.setTerms(1);
			}
		}

		BigDecimal deductFeeFromDisbTot = BigDecimal.ZERO;
		BigDecimal feeAddToDisbTot = BigDecimal.ZERO;
		for (FinFeeDetail finFeeDetail : finFeeDetailList) {
			if (StringUtils.equals(finFeeDetail.getFeeScheduleMethod(), CalculationConstants.REMFEE_PART_OF_DISBURSE)) {
				deductFeeFromDisbTot = deductFeeFromDisbTot.add(finFeeDetail.getRemainingFee());
			} else if (StringUtils.equals(finFeeDetail.getFeeScheduleMethod(),
					CalculationConstants.REMFEE_PART_OF_SALE_PRICE)) {
				feeAddToDisbTot = feeAddToDisbTot.add(finFeeDetail.getRemainingFee());
			} else if (StringUtils.equals(finFeeDetail.getFeeScheduleMethod(),
					CalculationConstants.REMFEE_PAID_BY_CUSTOMER)) {
				if (finFeeDetail.getPaidAmount().compareTo(BigDecimal.ZERO) == 0) {
					finFeeDetail.setPaidAmount(finFeeDetail.getActualAmount());
				}
			} else if (StringUtils.equals(finFeeDetail.getFeeScheduleMethod(),
					CalculationConstants.REMFEE_WAIVED_BY_BANK)) {
				if (finFeeDetail.getWaivedAmount().compareTo(BigDecimal.ZERO) == 0) {
					finFeeDetail.setWaivedAmount(finFeeDetail.getActualAmount());
				}
			}
			finFeeDetail.setRemainingFee(finFeeDetail.getActualAmount().subtract(finFeeDetail.getPaidAmount())
					.subtract(finFeeDetail.getWaivedAmount()));
		}

		for (FinFeeDetail prvFeeDetail : finScheduleData.getFinFeeDetailList()) {
			for (FinFeeDetail currFeeDetail : finFeeDetailList) {
				if (StringUtils.equals(prvFeeDetail.getFinEvent(), currFeeDetail.getFinEvent())
						&& StringUtils.equals(prvFeeDetail.getFeeTypeCode(), currFeeDetail.getFeeTypeCode())) {
					BeanUtils.copyProperties(prvFeeDetail, currFeeDetail);
				}
			}
		}

		finScheduleData.getFinFeeDetailList().addAll(finFeeDetailList);

		//### Ticket id:124998
		if (finServiceInst != null && finServiceInst.isReceiptUpload()) {
			finServiceInst.setFinFeeDetails(finScheduleData.getFinFeeDetailList());
		}

		logger.debug("Leaving");

	}

	private void doExecuteFeeRules(FinanceDetail financeDetail, FinServiceInstruction finServiceInst,
			FinScheduleData finScheduleData, List<String> feeRuleCodes, boolean enquiry,
			Map<String, BigDecimal> gstPercentages, List<FinFeeDetail> finFeeDetailList) {

		List<Rule> feeRules = ruleService.getRuleDetailList(feeRuleCodes, RuleConstants.MODULE_FEES,
				finScheduleData.getFeeEvent());

		if (feeRules == null || feeRules.isEmpty()) {
			return;
		}

		Map<String, Object> executionMap = new HashMap<String, Object>();
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
			objectList.add(financeDetail.getFinScheduleData().getFinanceMain());
			objectList.add(financeDetail.getFinScheduleData().getFinanceType());
		}
		for (Rule feeRule : feeRules) {
			if (feeRule.getFields() != null) {
				String[] fields = feeRule.getFields().split(",");
				for (String field : fields) {
					if (!executionMap.containsKey(field)) {
						ruleExecutionUtil.setExecutionMap(field, objectList, executionMap);
					}
				}
			}
			ruleSqlMap.put(feeRule.getRuleCode(), feeRule.getSQLRule());
		}

		int formatter = CurrencyUtil.getFormat(finScheduleData.getFinanceMain().getFinCcy());
		FinanceMain finMain = financeDetail.getFinScheduleData().getFinanceMain();
		if (finMain != null && StringUtils.isNotBlank(finMain.getFinReference())
				&& StringUtils.isNotBlank(finMain.getRcdMaintainSts())) {
			FinanceProfitDetail finProfitDetail = financeDetailService
					.getFinProfitDetailsById(finMain.getFinReference());
			if (finProfitDetail != null) {
				BigDecimal outStandingFeeBal = this.financeDetailService
						.getOutStandingBalFromFees(finMain.getFinReference());
				executionMap.put("totalOutStanding", finProfitDetail.getTotalPftBal());
				executionMap.put("principalOutStanding",
						finProfitDetail.getTotalpriSchd().subtract(finProfitDetail.getTdSchdPri()));
				executionMap.put("principalSchdOutstanding",
						finProfitDetail.getTotalpriSchd().subtract(finProfitDetail.getTdSchdPri()));
				executionMap.put("totOSExcludeFees",
						finProfitDetail.getTotalPftBal().add(finProfitDetail.getTotalPriBal()));
				executionMap.put("totOSIncludeFees",
						finProfitDetail.getTotalPftBal().add(finProfitDetail.getTotalPriBal()).add(outStandingFeeBal));
				executionMap.put("unearnedAmount", finProfitDetail.getUnearned());
			}

			if (finServiceInst != null) {
				executionMap.put("totalPayment", finServiceInst.getAmount());
				BigDecimal remPartPaymentAmt = PennantApplicationUtil.formateAmount(finServiceInst.getRemPartPayAmt(),
						formatter);
				executionMap.put("partialPaymentAmount", remPartPaymentAmt);
			}

			if (finMain != null && finMain.getFinStartDate() != null) {
				int finAge = DateUtility.getMonthsBetween(SysParamUtil.getAppDate(), finMain.getFinStartDate());
				executionMap.put("finAgetilldate", finAge);
			}

			for (FinFeeDetail finFeeDetail : finFeeDetailList) {
				if (StringUtils.isEmpty(finFeeDetail.getRuleCode())) {
					continue;
				}

				BigDecimal feeResult = this.finFeeDetailService.getFeeResult(ruleSqlMap.get(finFeeDetail.getRuleCode()),
						executionMap, finScheduleData.getFinanceMain().getFinCcy());

				//unFormating feeResult
				feeResult = PennantApplicationUtil.unFormateAmount(feeResult, formatter);

				finFeeDetail.setCalculatedAmount(feeResult);

				if (finFeeDetail.isTaxApplicable()) {
					this.finFeeDetailService.processGSTCalForRule(finFeeDetail, feeResult, financeDetail,
							getGSTPercentages(financeDetail), enquiry);
				} else {
					if (enquiry) {
						finFeeDetail.setActualAmount(feeResult);
					}
					finFeeDetail.setRemainingFee(finFeeDetail.getActualAmount().subtract(finFeeDetail.getPaidAmount())
							.subtract(finFeeDetail.getWaivedAmount()));
				}
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

	private List<FinFeeDetail> prepareActualFinFees(List<FinFeeDetail> finTypeFees,
			List<FinFeeDetail> finFeeDetailList) {
		if (CollectionUtils.isNotEmpty(finFeeDetailList)) {
			for (FinFeeDetail finTypeFeeDetail : finTypeFees) {
				for (FinFeeDetail finFeeDetail : finFeeDetailList) {
					if (StringUtils.equals(finTypeFeeDetail.getFeeTypeCode(), finFeeDetail.getFeeTypeCode())
							&& StringUtils.equals(finTypeFeeDetail.getFinEvent(), finFeeDetail.getFinEvent())) {
						finTypeFeeDetail.setFeeScheduleMethod(finFeeDetail.getFeeScheduleMethod());
						finTypeFeeDetail.setPaidAmount(finFeeDetail.getPaidAmount());
						finTypeFeeDetail.setTerms(finFeeDetail.getTerms());
						finTypeFeeDetail.setWaivedAmount(finFeeDetail.getWaivedAmount());

						if (finTypeFeeDetail.isTaxApplicable()) {
							if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE
									.equals(finTypeFeeDetail.getTaxComponent())) {
								finTypeFeeDetail.setActualAmountOriginal(finFeeDetail.getActualAmount());
								finTypeFeeDetail.setActualAmount(finFeeDetail.getActualAmount());
							} else if (FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE
									.equals(finTypeFeeDetail.getTaxComponent())) {
								finTypeFeeDetail.setNetAmount(finFeeDetail.getActualAmount());
								finTypeFeeDetail.setActualAmount(finFeeDetail.getActualAmount());
							}
							finTypeFeeDetail.setPaidAmount(finFeeDetail.getPaidAmount());
						} else {
							finTypeFeeDetail.setActualAmountOriginal(finFeeDetail.getActualAmount());
							finTypeFeeDetail.setActualAmountGST(BigDecimal.ZERO);
							finTypeFeeDetail.setActualAmount(finFeeDetail.getActualAmount());

							BigDecimal netAmountOriginal = finTypeFeeDetail.getActualAmountOriginal()
									.subtract(finTypeFeeDetail.getWaivedAmount());

							finTypeFeeDetail.setNetAmountOriginal(netAmountOriginal);
							finTypeFeeDetail.setNetAmountGST(BigDecimal.ZERO);
							finTypeFeeDetail.setNetAmount(netAmountOriginal);
							finTypeFeeDetail.setPaidAmount(finFeeDetail.getPaidAmount());
						}
					}
				}
			}
		}
		return finTypeFees;
	}

	private void calculateFeePercentageAmount(FinScheduleData finScheduleData, FinanceDetail financeDetail,
			boolean enquiry, Map<String, BigDecimal> gstPercentages, List<FinFeeDetail> finFeeDetailList) {

		if (CollectionUtils.isEmpty(finFeeDetailList)) {
			return;
		}

		for (FinFeeDetail fee : finFeeDetailList) {
			if (StringUtils.equals(fee.getCalculationType(), PennantConstants.FEE_CALCULATION_TYPE_PERCENTAGE)) {
				BigDecimal calPercentageFee = getCalculatedPercentageFee(fee, finScheduleData);
				fee.setCalculatedAmount(calPercentageFee);

				if (StringUtils.equals(fee.getFeeScheduleMethod(), CalculationConstants.REMFEE_WAIVED_BY_BANK)) {
					fee.setWaivedAmount(calPercentageFee);
				}

				if (fee.isTaxApplicable()) {
					if (enquiry) {
						this.finFeeDetailService.processGSTCalForPercentage(fee, calPercentageFee, financeDetail,
								gstPercentages, enquiry);
					} else {
						this.finFeeDetailService.processGSTCalForPercentage(fee, fee.getActualAmount(), financeDetail,
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

	private BigDecimal getCalculatedPercentageFee(FinFeeDetail fee, FinScheduleData finScheduleData) {
		BigDecimal calculatedAmt = BigDecimal.ZERO;
		FinanceMain financeMain = finScheduleData.getFinanceMain();
		switch (fee.getCalculateOn()) {
		case PennantConstants.FEE_CALCULATEDON_TOTALASSETVALUE:
			calculatedAmt = financeMain.getFinAssetValue();
			break;
		case PennantConstants.FEE_CALCULATEDON_LOANAMOUNT:
			calculatedAmt = financeMain.getFinAmount().subtract(financeMain.getDownPayment());
			break;
		case PennantConstants.FEE_CALCULATEDON_OUTSTANDINGPRCINCIPAL:
			calculatedAmt = financeMain.getFinCurrAssetValue().add(financeMain.getFeeChargeAmt())
					.subtract(financeMain.getFinRepaymentAmount());
			break;
		default:
			break;
		}
		calculatedAmt = calculatedAmt.multiply(fee.getPercentage()).divide(BigDecimal.valueOf(100), 0,
				RoundingMode.HALF_DOWN);
		return calculatedAmt;
	}

	/**
	 * 
	 * 
	 * @param financeDetail
	 */
	private List<FinFeeDetail> doSetFeeChanges(FinanceDetail financeDetail, boolean isOriginationFee,
			List<FinFeeDetail> finFeeDetailsList) {
		logger.debug("Entering");
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		for (FinFeeDetail finFeeDetail : finFeeDetailsList) {
			finFeeDetail.setRecordType(PennantConstants.RCD_ADD);
			finFeeDetail.setFinReference(finScheduleData.getFinanceMain().getFinReference());
			finFeeDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			finFeeDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		}
		Map<String, FinFeeDetail> feeDetailMap = new HashMap<String, FinFeeDetail>();

		for (FinFeeDetail finFeeDetail : finFeeDetailsList) {
			if (!finFeeDetail.isNewRecord()) {
				if (!finFeeDetail.isRcdVisible()
						&& StringUtils.equals(finFeeDetail.getRecordType(), PennantConstants.RECORD_TYPE_CAN)) {
					finFeeDetail.setRcdVisible(true);
					finFeeDetail.setDataModified(true);
					finFeeDetail.setNewRecord(false);
					finFeeDetail.setRecordType(PennantConstants.RCD_ADD);
					feeDetailMap.put(getUniqueID(finFeeDetail), finFeeDetail);
				} else {
					finFeeDetail.setVersion(finFeeDetail.getVersion() + 1);
					finFeeDetail.setRecordType(PennantConstants.RECORD_TYPE_CAN);
					finFeeDetail.setRcdVisible(false);
					finFeeDetail.setDataModified(true);
					feeDetailMap.put(getUniqueID(finFeeDetail), finFeeDetail);
				}
			}
		}

		/*
		 * List<FinFeeDetail> finFeeDetailListNew = convertToFinanceFees(financeDetail.getFinTypeFeesList(),
		 * finScheduleData.getFinanceMain().getFinReference());
		 */
		for (FinFeeDetail finFeeDetail : finFeeDetailsList) {
			if (!feeDetailMap.containsKey(getUniqueID(finFeeDetail))) {
				feeDetailMap.put(getUniqueID(finFeeDetail), finFeeDetail);
			}
		}

		logger.debug("Leaving");
		return new ArrayList<FinFeeDetail>(feeDetailMap.values());
	}

	/**
	 * @param financeMain
	 * @param customer
	 * @param financeType
	 * @return
	 */
	private HashMap<String, Object> getDataMap(FinanceMain financeMain, Customer customer, FinanceType financeType) {
		HashMap<String, Object> dataMap = new HashMap<String, Object>();
		if (financeMain != null) {
			dataMap.putAll(financeMain.getDeclaredFieldValues());
		}
		if (customer != null) {
			dataMap.putAll(customer.getDeclaredFieldValues());
		}
		if (financeType != null) {
			dataMap.putAll(financeType.getDeclaredFieldValues());
		}
		return dataMap;
	}

	private List<FinFeeDetail> convertToFinanceFees(FinanceDetail financeDetail, String reference) {

		List<FinTypeFees> finTypeFeesList = financeDetail.getFinTypeFeesList();
		List<FinFeeDetail> finFeeDetails = new ArrayList<FinFeeDetail>();

		if (CollectionUtils.isNotEmpty(finTypeFeesList)) {

			FinFeeDetail finFeeDetail = null;
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
				finFeeDetail.setFixedAmount(finTypeFee.getAmount());
				finFeeDetail.setPercentage(finTypeFee.getPercentage());
				finFeeDetail.setCalculateOn(finTypeFee.getCalculateOn());
				finFeeDetail.setAlwDeviation(finTypeFee.isAlwDeviation());
				finFeeDetail.setMaxWaiverPerc(finTypeFee.getMaxWaiverPerc());
				finFeeDetail.setAlwModifyFee(finTypeFee.isAlwModifyFee());
				finFeeDetail.setAlwModifyFeeSchdMthd(finTypeFee.isAlwModifyFeeSchdMthd());
				finFeeDetail.setAlwPreIncomization(finTypeFee.isAlwPreIncomization());

				finFeeDetail.setCalculatedAmount(finTypeFee.getAmount());

				finFeeDetail.setTaxComponent(finTypeFee.getTaxComponent());
				finFeeDetail.setTaxApplicable(finTypeFee.isTaxApplicable());

				if (finTypeFee.isTaxApplicable()) {
					this.finFeeDetailService.convertGSTFinTypeFees(finFeeDetail, finTypeFee, financeDetail,
							getGSTPercentages(financeDetail));
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

					if (StringUtils.equals(finTypeFee.getFeeScheduleMethod(),
							CalculationConstants.REMFEE_WAIVED_BY_BANK)) {
						finFeeDetail.setWaivedAmount(finTypeFee.getAmount());
					}

					finFeeDetail.setRemainingFeeOriginal(finFeeDetail.getActualAmount()
							.subtract(finFeeDetail.getWaivedAmount()).subtract(finFeeDetail.getPaidAmount()));
					finFeeDetail.setRemainingFeeGST(BigDecimal.ZERO);
					finFeeDetail.setRemainingFee(finFeeDetail.getActualAmount().subtract(finFeeDetail.getWaivedAmount())
							.subtract(finFeeDetail.getPaidAmount()));
				}

				finFeeDetail.setRecordType(PennantConstants.RCD_ADD);
				finFeeDetail.setFinReference(reference);
				finFeeDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				finFeeDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				finFeeDetails.add(finFeeDetail);
			}
		}
		return finFeeDetails;
	}

	public FinFeeDetail setFinFeeDetails(FinTypeFees finTypeFee, FinFeeDetail finFeeDetail,
			Map<String, BigDecimal> taxPercentages, String currency) {

		FinanceDetail financeDetail = new FinanceDetail();
		FinScheduleData finScheduleData = new FinScheduleData();
		FinanceMain finMain = new FinanceMain();
		finMain.setFinCcy(currency);
		finScheduleData.setFinanceMain(finMain);
		financeDetail.setFinScheduleData(finScheduleData);
		if (finTypeFee != null) {
			finFeeDetail.setNewRecord(true);
			finFeeDetail.setOriginationFee(finTypeFee.isOriginationFee());
			finFeeDetail.setFinEvent(finTypeFee.getFinEvent());
			finFeeDetail.setFinEventDesc(finTypeFee.getFinEventDesc());
			finFeeDetail.setFeeTypeID(finTypeFee.getFeeTypeID());
			finFeeDetail.setFeeOrder(finTypeFee.getFeeOrder());
			finFeeDetail.setFeeTypeCode(finTypeFee.getFeeTypeCode());
			finFeeDetail.setFeeTypeDesc(finTypeFee.getFeeTypeDesc());
			finFeeDetail.setAlwPreIncomization(finTypeFee.isAlwPreIncomization());
			finFeeDetail.setFeeScheduleMethod(finTypeFee.getFeeScheduleMethod());
			finFeeDetail.setCalculationType(finTypeFee.getCalculationType());
			finFeeDetail.setRuleCode(finTypeFee.getRuleCode());
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
				BigDecimal totalGST = BigDecimal.ZERO;
				String taxComponent = finFeeDetail.getTaxComponent();
				BigDecimal taxableAmount = finFeeDetail.getActualAmount();
				BigDecimal waivedAmount = finFeeDetail.getWaivedAmount();

				if (FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE.equals(taxComponent)) {
					totalGST = GSTCalculator.getInclusiveGST(taxableAmount, waivedAmount, taxPercentages).gettGST();
					finFeeDetail.setNetAmount(taxableAmount);
					finFeeDetail.setNetAmountOriginal(totalGST);
					finFeeDetail.setNetAmountGST(taxableAmount.subtract(totalGST));
					finFeeDetail.setActualAmountOriginal(totalGST.add(waivedAmount));
					finFeeDetail.setActualAmountGST(totalGST);
					finFeeDetail.setActualAmount(finFeeDetail.getActualAmountOriginal().add(totalGST));

					finFeeDetail.setPaidAmountOriginal(totalGST.add(waivedAmount));
					finFeeDetail.setPaidAmountGST(totalGST);
				} else if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(finFeeDetail.getTaxComponent())) {
					totalGST = GSTCalculator.getExclusiveGST(taxableAmount, taxPercentages).gettGST();
					finFeeDetail.setNetAmountOriginal(taxableAmount.subtract(finFeeDetail.getWaivedAmount()));
					finFeeDetail.setNetAmountGST(totalGST);
					finFeeDetail.setNetAmount(finFeeDetail.getNetAmountOriginal().add(totalGST));
					finFeeDetail.setActualAmount(finFeeDetail.getActualAmountOriginal().add(totalGST));

					//finFeeDetail.setPaidAmount(finFeeDetail.getActualAmountOriginal().add(actualGst));
					finFeeDetail.setPaidAmountOriginal(finFeeDetail.getNetAmountOriginal());
					finFeeDetail.setPaidAmount(finFeeDetail.getNetAmountOriginal().add(totalGST));
					finFeeDetail.setPaidAmountGST(totalGST);
				}

				//this.finFeeDetailService.convertGSTFinTypeFees(finFeeDetail, finTypeFee, financeDetail, gstExecutionMap);
				this.finFeeDetailService.calculateFees(finFeeDetail, finMain, taxPercentages);
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

			finFeeDetail.setRecordType(PennantConstants.RCD_ADD);
			finFeeDetail.setFinReference(null);
			finFeeDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			finFeeDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		}
		return finFeeDetail;
	}

	private List<FinFeeDetail> convertToFinanceFees(List<FinTypeFees> finTypeFeesList, String reference) {
		List<FinFeeDetail> finFeeDetails = new ArrayList<FinFeeDetail>();
		if (finTypeFeesList != null && !finTypeFeesList.isEmpty()) {
			FinFeeDetail finFeeDetail = null;
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
				finFeeDetail.setFixedAmount(finTypeFee.getAmount());
				finFeeDetail.setPercentage(finTypeFee.getPercentage());
				finFeeDetail.setCalculateOn(finTypeFee.getCalculateOn());
				finFeeDetail.setAlwDeviation(finTypeFee.isAlwDeviation());
				finFeeDetail.setMaxWaiverPerc(finTypeFee.getMaxWaiverPerc());
				finFeeDetail.setAlwModifyFee(finTypeFee.isAlwModifyFee());
				finFeeDetail.setAlwModifyFeeSchdMthd(finTypeFee.isAlwModifyFeeSchdMthd());

				finFeeDetail.setCalculatedAmount(finTypeFee.getAmount());
				finFeeDetail.setActualAmount(finTypeFee.getAmount());
				if (StringUtils.equals(finTypeFee.getFeeScheduleMethod(),
						CalculationConstants.REMFEE_PAID_BY_CUSTOMER)) {
					finFeeDetail.setPaidAmount(finTypeFee.getAmount());
				}
				if (StringUtils.equals(finTypeFee.getFeeScheduleMethod(), CalculationConstants.REMFEE_WAIVED_BY_BANK)) {
					finFeeDetail.setWaivedAmount(finTypeFee.getAmount());
				}
				finFeeDetail.setRemainingFee(finFeeDetail.getActualAmount().subtract(finFeeDetail.getWaivedAmount())
						.subtract(finFeeDetail.getPaidAmount()));

				finFeeDetail.setRecordType(PennantConstants.RCD_ADD);
				finFeeDetail.setFinReference(reference);
				finFeeDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				finFeeDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				finFeeDetails.add(finFeeDetail);
			}
		}
		return finFeeDetails;
	}

	/**
	 * Method for execute finance fee details and validate with configured fees in loan type.<br>
	 * - Execute Origination fees.<br>
	 * - Execute Servicing fees.<br>
	 * FinEvent is always empty for origination fees.
	 * 
	 * @param financeDetail
	 * @param finEvent
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public void doExecuteFeeCharges(FinanceDetail financeDetail, String finEvent, FinServiceInstruction finServiceInst,
			boolean enquiry) throws IllegalAccessException, InvocationTargetException {
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		boolean isOriginationFee = false;
		if (StringUtils.isBlank(finEvent)) {
			isOriginationFee = true;
			finEvent = PennantApplicationUtil.getEventCode(financeMain.getFinStartDate());
		}
		financeDetail.getFinScheduleData().setFeeEvent(finEvent);
		if (!financeDetail.getFinScheduleData().getFinanceType().isPromotionType()) {
			financeDetail.setFinTypeFeesList(financeDetailService.getFinTypeFees(financeMain.getFinType(), finEvent,
					isOriginationFee, FinanceConstants.MODULEID_FINTYPE));
		} else {
			financeDetail.setFinTypeFeesList(financeDetailService.getFinTypeFees(
					financeDetail.getFinScheduleData().getFinanceType().getPromotionCode(), finEvent, isOriginationFee,
					FinanceConstants.MODULEID_PROMOTION));
		}
		if (isOriginationFee) {
			for (FinFeeDetail finFeeDetail : financeDetail.getFinScheduleData().getFinFeeDetailList()) {
				if (StringUtils.equals(finFeeDetail.getFinEvent(), AccountEventConstants.ACCEVENT_VAS_FEE)) {
					finFeeDetail.setNewRecord(true);
					finFeeDetail.setRecordType(PennantConstants.RCD_ADD);
					finFeeDetail.setFinReference(financeDetail.getFinScheduleData().getFinanceMain().getFinReference());
					finFeeDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
					finFeeDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					finFeeDetail.setLastMntBy(financeDetail.getFinScheduleData().getFinanceMain().getLastMntBy());
					finFeeDetail.setOriginationFee(true);
					finFeeDetail.setFeeTypeID(0);
					finFeeDetail.setFeeSeq(0);
				} else {
					finFeeDetail.setFinEvent(finEvent);
				}
			}
		}
		executeFeeCharges(financeDetail, isOriginationFee, finServiceInst, enquiry);
	}

	private String getUniqueID(FinFeeDetail finFeeDetail) {
		return StringUtils.trimToEmpty(finFeeDetail.getFinEvent()) + "_" + String.valueOf(finFeeDetail.getFeeTypeID());
	}

	public List<FinFeeDetail> getFinFeeDetailUpdateList() {
		return this.finFeeDetailList;
	}

	public List<FinFeeDetail> getFinFeeDetailList() {
		List<FinFeeDetail> finFeeDetailTemp = new ArrayList<FinFeeDetail>();
		finFeeDetailTemp.addAll(finFeeDetailList);

		/*
		 * for (FinFeeDetail finFeeDetail : finFeeDetailList) { // if (finFeeDetail.isRcdVisible()) {
		 * finFeeDetailTemp.add(finFeeDetail); //} }
		 */
		return finFeeDetailTemp;
	}

	private BigDecimal calculateInclusivePercentage(BigDecimal amount, BigDecimal cgstPerc, BigDecimal sgstPerc,
			BigDecimal ugstPerc, BigDecimal igstPerc, String taxRoundMode, int taxRoundingTarget) {
		logger.debug(Literal.ENTERING);

		if (amount.compareTo(BigDecimal.ZERO) == 0) {
			return BigDecimal.ZERO;
		}

		BigDecimal totalGSTPerc = cgstPerc.add(sgstPerc).add(ugstPerc).add(igstPerc);
		BigDecimal percentage = (totalGSTPerc.add(new BigDecimal(100))).divide(BigDecimal.valueOf(100), 9,
				RoundingMode.HALF_DOWN);
		BigDecimal actualAmt = amount.divide(percentage, 9, RoundingMode.HALF_DOWN);
		actualAmt = CalculationUtil.roundAmount(actualAmt, taxRoundMode, taxRoundingTarget);

		BigDecimal actTaxAmount = amount.subtract(actualAmt);

		BigDecimal gstAmount = BigDecimal.ZERO;
		if (cgstPerc.compareTo(BigDecimal.ZERO) > 0) {
			BigDecimal cgst = (actTaxAmount.multiply(cgstPerc)).divide(totalGSTPerc, 9, RoundingMode.HALF_DOWN);
			cgst = CalculationUtil.roundAmount(cgst, taxRoundMode, taxRoundingTarget);
			gstAmount = gstAmount.add(cgst);
		}
		if (sgstPerc.compareTo(BigDecimal.ZERO) > 0) {
			BigDecimal sgst = (actTaxAmount.multiply(sgstPerc)).divide(totalGSTPerc, 9, RoundingMode.HALF_DOWN);
			sgst = CalculationUtil.roundAmount(sgst, taxRoundMode, taxRoundingTarget);
			gstAmount = gstAmount.add(sgst);
		}
		if (ugstPerc.compareTo(BigDecimal.ZERO) > 0) {
			BigDecimal ugst = (actTaxAmount.multiply(ugstPerc)).divide(totalGSTPerc, 9, RoundingMode.HALF_DOWN);
			ugst = CalculationUtil.roundAmount(ugst, taxRoundMode, taxRoundingTarget);
			gstAmount = gstAmount.add(ugst);
		}
		if (igstPerc.compareTo(BigDecimal.ZERO) > 0) {
			BigDecimal igst = (actTaxAmount.multiply(igstPerc)).divide(totalGSTPerc, 9, RoundingMode.HALF_DOWN);
			igst = CalculationUtil.roundAmount(igst, taxRoundMode, taxRoundingTarget);
			gstAmount = gstAmount.add(igst);
		}

		logger.debug(Literal.LEAVING);
		return amount.subtract(gstAmount);
	}

	private Map<String, BigDecimal> getGSTPercentages(FinanceDetail financeDetail) {
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		FinanceMain financeMain = finScheduleData.getFinanceMain();
		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		String userBranch = userDetails.getBranchCode();
		String finBranch = financeMain.getFinBranch();
		String finCCY = financeMain.getFinCcy();

		long custId = 0;
		if (financeDetail.getCustomerDetails() != null) {
			custId = financeDetail.getCustomerDetails().getCustomer().getCustID();
		}

		return GSTCalculator.getTaxPercentages(custId, finCCY, userBranch, finBranch,
				financeDetail.getFinanceTaxDetail());
	}

	public void setFinFeeDetailList(List<FinFeeDetail> finFeeDetailList) {
		this.finFeeDetailList = finFeeDetailList;
	}

	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	public void setRuleService(RuleService ruleService) {
		this.ruleService = ruleService;
	}

	public void setRuleExecutionUtil(RuleExecutionUtil ruleExecutionUtil) {
		this.ruleExecutionUtil = ruleExecutionUtil;
	}

	public void setFinFeeDetailService(FinFeeDetailService finFeeDetailService) {
		this.finFeeDetailService = finFeeDetailService;
	}
}
