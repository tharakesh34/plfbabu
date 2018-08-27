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
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.app.util.SessionUserDetails;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinInsurances;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.rmtmasters.FinTypeFees;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.service.finance.FinFeeDetailService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.rulefactory.RuleService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.InsuranceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.RuleReturnType;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;

public class FeeDetailService {
	Logger				logger	= Logger.getLogger(FeeDetailService.class);

	private FinanceDetailService financeDetailService;
	private RuleExecutionUtil ruleExecutionUtil;
	private RuleService	ruleService;
	private FinFeeDetailService	finFeeDetailService;

	private List<FinFeeDetail> finFeeDetailList = new ArrayList<FinFeeDetail>();
	
	/**
	 * Calculate and execute fee details
	 *  
	 * @param financeDetail
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	private void executeFeeCharges(FinanceDetail financeDetail, boolean isOriginationFee, FinServiceInstruction finServiceInst,boolean enquiry) 
			throws IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		String branchCode = userDetails.getBranchCode();
		String fromBranchCode = financeDetail.getFinScheduleData().getFinanceMain().getFinBranch();
		
		String custDftBranch = null;
		String highPriorityState = null;
		String highPriorityCountry = null;
		if(financeDetail.getCustomerDetails() != null){
			custDftBranch = financeDetail.getCustomerDetails().getCustomer().getCustDftBranch();
			
			List<CustomerAddres> addressList = financeDetail.getCustomerDetails().getAddressList();
			if (CollectionUtils.isNotEmpty(addressList)) {
				for (CustomerAddres customerAddres : addressList) {
					if (customerAddres.getCustAddrPriority() == Integer.valueOf(PennantConstants.KYC_PRIORITY_VERY_HIGH)) {
						highPriorityState = customerAddres.getCustAddrProvince();
						highPriorityCountry = customerAddres.getCustAddrCountry();
						break;
					}
				}
			}
		}
		
		HashMap<String, Object> gstExecutionMap = this.finFeeDetailService.prepareGstMappingDetails(fromBranchCode,custDftBranch, highPriorityState,highPriorityCountry, 
				financeDetail.getFinanceTaxDetails(), branchCode);
		
		// set FinType fees details
		String finReference = finScheduleData.getFinanceMain().getFinReference();
		setFinFeeDetailList(convertToFinanceFees(financeDetail, finReference, gstExecutionMap));
		List<FinFeeDetail> finTypeFees = getFinFeeDetailList();

		List<FinFeeDetail> actualFinFeeList = prepareActualFinFees(finTypeFees, finScheduleData.getFinFeeDetailList());

		setFinFeeDetailList(actualFinFeeList);
		
		// Organize Fee detail changes
		doSetFeeChanges(financeDetail, isOriginationFee);

		List<String> feeRuleCodes = new ArrayList<String>();
	
		for (FinFeeDetail finFeeDetail : getFinFeeDetailList()) {
			finFeeDetail.setRecordType(PennantConstants.RCD_ADD);
			finFeeDetail.setFinReference(finScheduleData.getFinanceMain().getFinReference());
			finFeeDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			finFeeDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			finFeeDetail.setLastMntBy(financeDetail.getFinScheduleData().getFinanceMain().getLastMntBy());
			finFeeDetail.setFinEvent(finScheduleData.getFeeEvent());
			if (StringUtils.isNotEmpty(finFeeDetail.getRuleCode())) {
				feeRuleCodes.add(finFeeDetail.getRuleCode());
			}
			if (StringUtils.equals(FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE, finFeeDetail.getTaxComponent())){
				finFeeDetail.setPaidAmountOriginal(getPaidOriginal(finFeeDetail, gstExecutionMap, financeDetail));
				finFeeDetail.setPaidAmount(finFeeDetail.getPaidAmountOriginal());
			}
		}
		
		// Execute fee rules if exists
		if (feeRuleCodes.size() > 0) {
			doExecuteFeeRules(financeDetail, finServiceInst, finScheduleData, feeRuleCodes, enquiry, gstExecutionMap);
		}
		
		// calculate fee percentage if exists
		calculateFeePercentageAmount(finScheduleData, financeDetail, enquiry, gstExecutionMap);
		
		// Validate Provided fee details with configured fee details
		validateFeeConfig(getFinFeeDetailList(), finScheduleData);
		
		// Calculating GST
		for (FinFeeDetail finFeeDetail : getFinFeeDetailList()) {
			this.finFeeDetailService.calculateGSTFees(finFeeDetail, finScheduleData.getFinanceMain(), gstExecutionMap);
		}
		
		// add vas recording fees into actual list
		if(finScheduleData.getVasRecordingList() != null && !finScheduleData.getVasRecordingList().isEmpty()) {
			List<FinFeeDetail> vasFees = new ArrayList<FinFeeDetail>();
			for(FinFeeDetail detail:finScheduleData.getFinFeeDetailList()) {
				if(StringUtils.equals(detail.getFinEvent(), AccountEventConstants.ACCEVENT_VAS_FEE)) {
					detail.setRemainingFee(detail.getActualAmount().subtract(detail.getPaidAmount())
							.subtract(detail.getWaivedAmount()));
					vasFees.add(detail);
				}
			}
			finFeeDetailList.addAll(vasFees);
		}

		BigDecimal deductFeeFromDisbTot = BigDecimal.ZERO;
		BigDecimal feeAddToDisbTot = BigDecimal.ZERO;
		for (FinFeeDetail finFeeDetail : getFinFeeDetailList()) {
			if (StringUtils.equals(finFeeDetail.getFeeScheduleMethod(), CalculationConstants.REMFEE_PART_OF_DISBURSE)) {
				deductFeeFromDisbTot = deductFeeFromDisbTot.add(finFeeDetail.getRemainingFee());
			} else if (StringUtils.equals(finFeeDetail.getFeeScheduleMethod(), CalculationConstants.REMFEE_PART_OF_SALE_PRICE)) {
				feeAddToDisbTot = feeAddToDisbTot.add(finFeeDetail.getRemainingFee());
			} else if (StringUtils.equals(finFeeDetail.getFeeScheduleMethod(), CalculationConstants.REMFEE_PAID_BY_CUSTOMER)) {
				if (finFeeDetail.getPaidAmount().compareTo(BigDecimal.ZERO) == 0) {
					finFeeDetail.setPaidAmount(finFeeDetail.getActualAmount());
				}
			} else if (StringUtils.equals(finFeeDetail.getFeeScheduleMethod(), CalculationConstants.REMFEE_WAIVED_BY_BANK)) {
				if (finFeeDetail.getWaivedAmount().compareTo(BigDecimal.ZERO) == 0) {
					finFeeDetail.setWaivedAmount(finFeeDetail.getActualAmount());
				}
			}
			finFeeDetail.setRemainingFee(finFeeDetail.getActualAmount().subtract(finFeeDetail.getPaidAmount())
					.subtract(finFeeDetail.getWaivedAmount()));
		}
		if (StringUtils.equals(FinanceConstants.FINSER_EVENT_ORG, financeDetail.getModuleDefiner())) {
			finScheduleData.getFinanceMain().setDeductFeeDisb(deductFeeFromDisbTot);
			finScheduleData.getFinanceMain().setFeeChargeAmt(feeAddToDisbTot);
		}

		for(FinFeeDetail prvFeeDetail: finScheduleData.getFinFeeDetailList()) {
			for(FinFeeDetail currFeeDetail: getFinFeeDetailList()) {
				if(StringUtils.equals(prvFeeDetail.getFinEvent(), currFeeDetail.getFinEvent()) 
						&& StringUtils.equals(prvFeeDetail.getFeeTypeCode(), currFeeDetail.getFeeTypeCode())) {
					BeanUtils.copyProperties(prvFeeDetail, currFeeDetail);
				}
			}
		}
		
		// Calculating GST
		for (FinFeeDetail finFeeDetail : finScheduleData.getFinFeeDetailList()) {
			this.finFeeDetailService.calculateGSTFees(finFeeDetail, finScheduleData.getFinanceMain(), gstExecutionMap);
		}
		
		

		// Insurance Amounts calculation
		List<FinInsurances> insurances = financeDetail.getFinScheduleData().getFinInsuranceList();
		BigDecimal insAddToDisb = BigDecimal.ZERO;
		BigDecimal deductInsFromDisb = BigDecimal.ZERO;
		BigDecimal finAmount = finScheduleData.getFinanceMain().getFinAmount();
		BigDecimal downPayAmt = finScheduleData.getFinanceMain().getDownPayment();
		Rule rule;

		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		Customer customer = null;
		if (financeDetail.getCustomerDetails() != null) {
			customer = financeDetail.getCustomerDetails().getCustomer();
		}
		FinanceType financeType = financeDetail.getFinScheduleData().getFinanceType();

		HashMap<String, Object> declaredFieldValues = getDataMap(financeMain, customer, financeType);

		if (insurances != null && !insurances.isEmpty()) {
			for (FinInsurances insurance : insurances) {
				if (insurance.isInsuranceReq()) {
					String payType = insurance.getPaymentMethod();
					if (StringUtils.equals(InsuranceConstants.PAYTYPE_SCH_FRQ, payType)) {
						continue;
					}

					BigDecimal insAmount = insurance.getAmount();
					insurance.setAmount(BigDecimal.ZERO);

					// Rule Based then Execute rule to Insurance Amount
					if (insurance.getCalType().equals(InsuranceConstants.CALTYPE_RULE)) {
						rule = ruleService.getRuleById(insurance.getCalRule(), RuleConstants.MODULE_INSRULE,
								RuleConstants.MODULE_INSRULE);
						if (rule != null) {
							insAmount = (BigDecimal) ruleExecutionUtil.executeRule(rule.getSQLRule(),
									declaredFieldValues, financeMain.getFinCcy(), RuleReturnType.DECIMAL);
						}
					}
					// Percentage Based then based on calculation Type, percentage Amount to be calculated
					else if (insurance.getCalType().equals(InsuranceConstants.CALTYPE_PERCENTAGE)) {
						if (insurance.getCalOn().equals(InsuranceConstants.CALCON_FINAMT)) {
							insAmount = finAmount.multiply(insurance.getCalPerc()).divide(new BigDecimal(100),
									RoundingMode.HALF_DOWN);
						} else if (insurance.getCalOn().equals(InsuranceConstants.CALCON_OSAMT)) {
							insAmount = (finAmount.subtract(downPayAmt)).multiply(insurance.getCalPerc()).divide(
									new BigDecimal(100), RoundingMode.HALF_DOWN);
						}
					}
					// Provider Rate Based then based on calculation Type, Amount to be calculated
					else if (insurance.getCalType().equals(InsuranceConstants.CALTYPE_PROVIDERRATE)) {
						if (insurance.getCalOn().equals(InsuranceConstants.CALCON_FINAMT)) {
							insAmount = finAmount.multiply(insurance.getInsuranceRate()).divide(new BigDecimal(100),
									RoundingMode.HALF_DOWN);
						} else if (insurance.getCalOn().equals(InsuranceConstants.CALCON_OSAMT)) {
							insAmount = (finAmount.subtract(downPayAmt)).multiply(insurance.getInsuranceRate()).divide(
									new BigDecimal(100), RoundingMode.HALF_DOWN);
						}
					}
					// Constant Amount not required any calculation
					if (StringUtils.equals(InsuranceConstants.PAYTYPE_DF_DISB, payType)) {
						deductInsFromDisb = deductInsFromDisb.add(insAmount);
						insurance.setAmount(insAmount);
					} else if (StringUtils.equals(InsuranceConstants.PAYTYPE_ADD_DISB, payType)) {
						insAddToDisb = insAddToDisb.add(insAmount);
						insurance.setAmount(insAmount);
					}
				}
			}
		}

		if(AccountEventConstants.ACCEVENT_EARLYSTL.equals(finScheduleData.getFinanceMain().getFinSourceID())) {
			finScheduleData.setFinFeeDetailList(actualFinFeeList);
		}
		//Insurance Amounts
		finScheduleData.getFinanceMain().setInsuranceAmt(insAddToDisb);
		finScheduleData.getFinanceMain().setDeductInsDisb(deductInsFromDisb);

		finScheduleData.setFinInsuranceList(financeDetail.getFinScheduleData().getFinInsuranceList());

		logger.debug("Leaving");
	}
	
	private BigDecimal getPaidOriginal(FinFeeDetail finFeeDetail, HashMap<String, Object> gstExecutionMap,
			FinanceDetail financeDetail) {
	    	BigDecimal gstNetOriginal=BigDecimal.ZERO;
			BigDecimal gstPerc=this.finFeeDetailService.actualGSTFees(finFeeDetail, financeDetail.getFinScheduleData().getFinanceMain().getFinCcy(), gstExecutionMap);
			String taxRoundMode = SysParamUtil.getValue(CalculationConstants.TAX_ROUNDINGMODE).toString();
			int taxRoundingTarget = SysParamUtil.getValueAsInt(CalculationConstants.TAX_ROUNDINGTARGET);
				 gstNetOriginal =  calculateInclusivePercentage(finFeeDetail.getPaidAmount(), finFeeDetail.getCgst(),finFeeDetail.getSgst(),
						finFeeDetail.getUgst(),finFeeDetail.getIgst(), taxRoundMode, taxRoundingTarget);
				
				
		return gstNetOriginal;
	}

	private void validateFeeConfig(List<FinFeeDetail> finFeeDetails, FinScheduleData finScheduleData) {
		logger.debug("Entering");
		
		List<ErrorDetail> errorDetails = new ArrayList<ErrorDetail>();
		
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
			BigDecimal finWaiverAmount = (calcAmount.multiply(maxWaiverPer)).divide(new BigDecimal(100), 0, RoundingMode.HALF_DOWN);
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

			if(!finFeeDetail.isOriginationFee()) {
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
	public void doProcessFeesForInquiry(FinanceDetail financeDetail, String finEvent, FinServiceInstruction finServiceInst, boolean enquiry) 
			throws IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		boolean isOrigination = false;
		if(finServiceInst == null) {
			isOrigination = true;
		}
		
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		String branchCode = userDetails.getBranchCode();
		String fromBranchCode = financeDetail.getFinScheduleData().getFinanceMain().getFinBranch();
		
		String custDftBranch = null;
		String highPriorityState = null;
		String highPriorityCountry = null;
		if(financeDetail.getCustomerDetails() != null){
			custDftBranch = financeDetail.getCustomerDetails().getCustomer().getCustDftBranch();
			
			List<CustomerAddres> addressList = financeDetail.getCustomerDetails().getAddressList();
			if (CollectionUtils.isNotEmpty(addressList)) {
				for (CustomerAddres customerAddres : addressList) {
					if (customerAddres.getCustAddrPriority() == Integer.valueOf(PennantConstants.KYC_PRIORITY_VERY_HIGH)) {
						highPriorityState = customerAddres.getCustAddrProvince();
						highPriorityCountry = customerAddres.getCustAddrCountry();
						break;
					}
				}
			}
		}
		
		HashMap<String, Object> gstExecutionMap = this.finFeeDetailService.prepareGstMappingDetails(fromBranchCode,custDftBranch, highPriorityState,highPriorityCountry, 
				financeDetail.getFinanceTaxDetails(), branchCode);
		
		if (!financeDetail.getFinScheduleData().getFinanceType().isPromotionType()) {
			financeDetail.setFinTypeFeesList(financeDetailService.getFinTypeFees(financeMain.getFinType(), finEvent,
					isOrigination, FinanceConstants.MODULEID_FINTYPE));
		} else {
			String promotionType = financeDetail.getFinScheduleData().getFinanceType().getPromotionCode();
			financeDetail.setFinTypeFeesList(financeDetailService.getFinTypeFees(promotionType, finEvent, isOrigination,
					FinanceConstants.MODULEID_PROMOTION));
		}
		financeDetail.getFinScheduleData().setFeeEvent(finEvent);
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();

		// set FinType fees details
		String finReference = finScheduleData.getFinanceMain().getFinReference();
		setFinFeeDetailList(convertToFinanceFees(financeDetail, finReference, gstExecutionMap));
		List<FinFeeDetail> finTypeFees = getFinFeeDetailList();
		List<FinFeeDetail> actualFinFeeList = prepareActualFinFees(finTypeFees, finScheduleData.getFinFeeDetailList());
		setFinFeeDetailList(actualFinFeeList);

		// Organize Fee detail changes
		//doSetFeeChanges(financeDetail, isOriginationFee);

		List<String> feeRuleCodes = new ArrayList<String>();
		for (FinFeeDetail finFeeDetail : getFinFeeDetailList()) {
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
			doExecuteFeeRules(financeDetail, finServiceInst, finScheduleData, feeRuleCodes, enquiry, gstExecutionMap);
		}

		// calculate fee percentage if exists
		calculateFeePercentageAmount(finScheduleData, financeDetail, enquiry, gstExecutionMap);
		
		// Calculating GST
		for (FinFeeDetail finFeeDetail : getFinFeeDetailList()) {
			this.finFeeDetailService.calculateGSTFees(finFeeDetail, financeMain, gstExecutionMap);
		}

		// set Actual calculated values into feeDetails for Inquiry purpose
		for (FinFeeDetail finFeeDetail : getFinFeeDetailList()) {
			if(!isOrigination) {
				finFeeDetail.setPaidAmount(finFeeDetail.getActualAmount());
			}
			finFeeDetail.setRemainingFee(finFeeDetail.getActualAmount().subtract(finFeeDetail.getPaidAmount()).subtract(finFeeDetail.getWaivedAmount()));
			if (CalculationConstants.REMFEE_SCHD_TO_N_INSTALLMENTS.equals(finFeeDetail.getFeeScheduleMethod()) && finFeeDetail.getTerms() <= 0) {
				finFeeDetail.setTerms(1);
			}
		}

		BigDecimal deductFeeFromDisbTot = BigDecimal.ZERO;
		BigDecimal feeAddToDisbTot = BigDecimal.ZERO;
		
		for (FinFeeDetail finFeeDetail : getFinFeeDetailList()) {
			if (StringUtils.equals(finFeeDetail.getFeeScheduleMethod(), CalculationConstants.REMFEE_PART_OF_DISBURSE)) {
				deductFeeFromDisbTot = deductFeeFromDisbTot.add(finFeeDetail.getRemainingFee());
			} else if (StringUtils.equals(finFeeDetail.getFeeScheduleMethod(), CalculationConstants.REMFEE_PART_OF_SALE_PRICE)) {
				feeAddToDisbTot = feeAddToDisbTot.add(finFeeDetail.getRemainingFee());
			} else if (StringUtils.equals(finFeeDetail.getFeeScheduleMethod(), CalculationConstants.REMFEE_PAID_BY_CUSTOMER)) {
				if (finFeeDetail.getPaidAmount().compareTo(BigDecimal.ZERO) == 0) {
					finFeeDetail.setPaidAmount(finFeeDetail.getActualAmount());
				}
			} else if (StringUtils.equals(finFeeDetail.getFeeScheduleMethod(), CalculationConstants.REMFEE_WAIVED_BY_BANK)) {
				if (finFeeDetail.getWaivedAmount().compareTo(BigDecimal.ZERO) == 0) {
					finFeeDetail.setWaivedAmount(finFeeDetail.getActualAmount());
				}
			}
			finFeeDetail.setRemainingFee(finFeeDetail.getActualAmount().subtract(finFeeDetail.getPaidAmount())
					.subtract(finFeeDetail.getWaivedAmount()));
		}
		
		if (StringUtils.equals(FinanceConstants.FINSER_EVENT_ORG, financeDetail.getModuleDefiner())) {
			finScheduleData.getFinanceMain().setDeductFeeDisb(deductFeeFromDisbTot);
			finScheduleData.getFinanceMain().setFeeChargeAmt(feeAddToDisbTot);
		}

		for(FinFeeDetail prvFeeDetail: finScheduleData.getFinFeeDetailList()) {
			for(FinFeeDetail currFeeDetail: getFinFeeDetailList()) {
				if(StringUtils.equals(prvFeeDetail.getFinEvent(), currFeeDetail.getFinEvent()) 
						&& StringUtils.equals(prvFeeDetail.getFeeTypeCode(), currFeeDetail.getFeeTypeCode())) {
					BeanUtils.copyProperties(prvFeeDetail, currFeeDetail);
				}
			}
		}
		
		finScheduleData.getFinFeeDetailList().addAll(getFinFeeDetailList());
		
		//Calculating GST
		for (FinFeeDetail finFeeDetail : finScheduleData.getFinFeeDetailList()) {
			this.finFeeDetailService.calculateGSTFees(finFeeDetail, financeMain, gstExecutionMap);
		}

		logger.debug("Leaving");

	}

	private void doExecuteFeeRules(FinanceDetail financeDetail, FinServiceInstruction finServiceInst,
			FinScheduleData finScheduleData, List<String> feeRuleCodes, boolean enquiry, HashMap<String, Object> gstExecutionMap) {
		
		List<Rule> feeRules = ruleService.getRuleDetailList(feeRuleCodes, RuleConstants.MODULE_FEES, finScheduleData.getFeeEvent());
		if (feeRules != null && !feeRules.isEmpty()) {
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
				FinanceProfitDetail finProfitDetail = financeDetailService.getFinProfitDetailsById(finMain.getFinReference());
				if (finProfitDetail != null) {
					BigDecimal outStandingFeeBal = this.financeDetailService.getOutStandingBalFromFees(finMain.getFinReference());
					executionMap.put("totalOutStanding", finProfitDetail.getTotalPftBal());
					executionMap.put("principalOutStanding", finProfitDetail.getTotalPriBal());
					executionMap.put("totOSExcludeFees",finProfitDetail.getTotalPftBal().add(finProfitDetail.getTotalPriBal()));
					executionMap.put("totOSIncludeFees", finProfitDetail.getTotalPftBal().add(finProfitDetail.getTotalPriBal()).add(outStandingFeeBal));
					executionMap.put("unearnedAmount", finProfitDetail.getUnearned());
				}
			}

			if (finServiceInst != null) {
				executionMap.put("totalPayment", finServiceInst.getAmount());
				BigDecimal remPartPaymentAmt = PennantApplicationUtil.formateAmount(finServiceInst.getRemPartPayAmt(), formatter);
				executionMap.put("partialPaymentAmount", remPartPaymentAmt);
			}
			
			if (finMain != null && finMain.getFinStartDate() != null) {
				int finAge = DateUtility.getMonthsBetween(DateUtility.getAppDate(), finMain.getFinStartDate());
				executionMap.put("finAgetilldate", finAge);
			}
		
			for (FinFeeDetail finFeeDetail : getFinFeeDetailList()) {
				if (StringUtils.isEmpty(finFeeDetail.getRuleCode())) {
					continue;
				}
				
				BigDecimal feeResult = this.finFeeDetailService.getFeeResult(ruleSqlMap.get(finFeeDetail.getRuleCode()), executionMap, finScheduleData.getFinanceMain().getFinCcy());

				//unFormating feeResult
				feeResult = PennantApplicationUtil.unFormateAmount(feeResult, formatter);

				finFeeDetail.setCalculatedAmount(feeResult);
				
				if (finFeeDetail.isTaxApplicable()) {
					this.finFeeDetailService.processGSTCalForRule(finFeeDetail, feeResult, financeDetail, gstExecutionMap, true);
				}  else {
					
					/*if (!finFeeDetail.isFeeModified()) {
						finFeeDetail.setActualAmountOriginal(feeResult);
						finFeeDetail.setActualAmountGST(BigDecimal.ZERO);
						finFeeDetail.setActualAmount(feeResult);
					}*/
					if (enquiry) {
						finFeeDetail.setActualAmount(feeResult);
					}
					finFeeDetail.setRemainingFee(finFeeDetail.getActualAmount().subtract(finFeeDetail.getPaidAmount()).
							subtract(finFeeDetail.getWaivedAmount()));
				}
			}
		}
	}

	/*private List<FinFeeDetail> prepareActualFinFees(List<FinFeeDetail> finTypeFees, List<FinFeeDetail> finFeeDetailList) {
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
							if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(finTypeFeeDetail.getTaxComponent())) {
								finTypeFeeDetail.setActualAmountOriginal(finFeeDetail.getActualAmount());
							} else if (FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE.equals(finTypeFeeDetail.getTaxComponent())) {
								finTypeFeeDetail.setNetAmount(finFeeDetail.getActualAmount());
							}
						} else {
							finTypeFeeDetail.setActualAmountOriginal(finFeeDetail.getActualAmount());
							finTypeFeeDetail.setActualAmountGST(BigDecimal.ZERO);
							finTypeFeeDetail.setActualAmount(finFeeDetail.getActualAmount());
							
							BigDecimal netAmountOriginal = finTypeFeeDetail.getActualAmountOriginal().subtract(finTypeFeeDetail.getWaivedAmount());
							
							finTypeFeeDetail.setNetAmountOriginal(netAmountOriginal);
							finTypeFeeDetail.setNetAmountGST(BigDecimal.ZERO);
							finTypeFeeDetail.setNetAmount(netAmountOriginal);
							
							finTypeFeeDetail.setPaidAmountOriginal(finFeeDetail.getPaidAmount());
							finTypeFeeDetail.setPaidAmountGST(BigDecimal.ZERO);
						}
					}
				}
			}
		}
		return finTypeFees;
	}*/
	
	private List<FinFeeDetail> prepareActualFinFees(List<FinFeeDetail> finTypeFees, List<FinFeeDetail> finFeeDetailList) {
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
							if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(finTypeFeeDetail.getTaxComponent())) {
								finTypeFeeDetail.setActualAmountOriginal(finFeeDetail.getActualAmount());
								finTypeFeeDetail.setActualAmount(finFeeDetail.getActualAmount());
							} else if (FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE.equals(finTypeFeeDetail.getTaxComponent())) {
								finTypeFeeDetail.setNetAmount(finFeeDetail.getActualAmount());
								finTypeFeeDetail.setActualAmount(finFeeDetail.getActualAmount());
							}
							finTypeFeeDetail.setPaidAmount(finFeeDetail.getPaidAmount());
						} else {
							finTypeFeeDetail.setActualAmountOriginal(finFeeDetail.getActualAmount());
							finTypeFeeDetail.setActualAmountGST(BigDecimal.ZERO);
							finTypeFeeDetail.setActualAmount(finFeeDetail.getActualAmount());
							
							BigDecimal netAmountOriginal = finTypeFeeDetail.getActualAmountOriginal().subtract(finTypeFeeDetail.getWaivedAmount());
							
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

	private void calculateFeePercentageAmount(FinScheduleData finScheduleData, FinanceDetail financeDetail, boolean enquiry, HashMap<String, Object> gstExecutionMap) {
		
		if (CollectionUtils.isNotEmpty(getFinFeeDetailList())) {

			for (FinFeeDetail finFeeDetail : getFinFeeDetailList()) {
				
				if (StringUtils.equals(finFeeDetail.getCalculationType(), PennantConstants.FEE_CALCULATION_TYPE_PERCENTAGE)) {
					BigDecimal calPercentageFee = getCalculatedPercentageFee(finFeeDetail, finScheduleData);
					finFeeDetail.setCalculatedAmount(calPercentageFee);
					
					if (StringUtils.equals(finFeeDetail.getFeeScheduleMethod(), CalculationConstants.REMFEE_WAIVED_BY_BANK)) {
						finFeeDetail.setWaivedAmount(calPercentageFee);
					}
					
					if (finFeeDetail.isTaxApplicable()) {	//if GST applicable
						this.finFeeDetailService.processGSTCalForPercentage(finFeeDetail, calPercentageFee, financeDetail, gstExecutionMap, true);
					} else {
						/*if (!finFeeDetail.isFeeModified()) {
							finFeeDetail.setActualAmountOriginal(calPercentageFee);
							finFeeDetail.setActualAmountGST(BigDecimal.ZERO);
							finFeeDetail.setActualAmount(calPercentageFee);
						}*/
						if (enquiry) {
							finFeeDetail.setActualAmount(calPercentageFee);
						}
						finFeeDetail.setRemainingFee(finFeeDetail.getActualAmount().subtract(finFeeDetail.getPaidAmount()).subtract(finFeeDetail.getWaivedAmount()));
					}
				}
			}
		}
	}
	
	private BigDecimal getCalculatedPercentageFee(FinFeeDetail finFeeDetail,FinScheduleData finScheduleData){
		BigDecimal calculatedAmt = BigDecimal.ZERO;
		FinanceMain financeMain = finScheduleData.getFinanceMain();
		switch (finFeeDetail.getCalculateOn()) {
		case PennantConstants.FEE_CALCULATEDON_TOTALASSETVALUE:
			calculatedAmt = financeMain.getFinAssetValue();
			break;
		case PennantConstants.FEE_CALCULATEDON_LOANAMOUNT:
			calculatedAmt = financeMain.getFinAmount().subtract(financeMain.getDownPayment());
			break;
		case PennantConstants.FEE_CALCULATEDON_OUTSTANDINGPRCINCIPAL:
			calculatedAmt = financeMain.getFinCurrAssetValue().add(financeMain.getFeeChargeAmt()).subtract(financeMain.getFinRepaymentAmount());
			break;
		default:
			break;
		}
		calculatedAmt = calculatedAmt.multiply(finFeeDetail.getPercentage()).divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_DOWN);
		return calculatedAmt;
	}
	
	/**
	 * 
	 * 
	 * @param financeDetail
	 */
	private void doSetFeeChanges(FinanceDetail financeDetail, boolean isOriginationFee) {
		logger.debug("Entering");
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		for(FinFeeDetail finFeeDetail:getFinFeeDetailList()){
			finFeeDetail.setRecordType(PennantConstants.RCD_ADD);
			finFeeDetail.setFinReference(finScheduleData.getFinanceMain().getFinReference());
			finFeeDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			finFeeDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		}
		Map<String, FinFeeDetail> feeDetailMap = new HashMap<String, FinFeeDetail>();

		for (FinFeeDetail finFeeDetail : getFinFeeDetailUpdateList()) {
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

/*		List<FinFeeDetail> finFeeDetailListNew = convertToFinanceFees(financeDetail.getFinTypeFeesList(),
				finScheduleData.getFinanceMain().getFinReference());*/
		for (FinFeeDetail finFeeDetail : getFinFeeDetailUpdateList()) {
			if (!feeDetailMap.containsKey(getUniqueID(finFeeDetail))) {
				feeDetailMap.put(getUniqueID(finFeeDetail), finFeeDetail);
			}
		}

		setFinFeeDetailList(new ArrayList<FinFeeDetail>(feeDetailMap.values()));
		logger.debug("Leaving");
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
	
	private List<FinFeeDetail> convertToFinanceFees(FinanceDetail financeDetail, String reference, HashMap<String, Object> gstExecutionMap) {
		
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
					this.finFeeDetailService.convertGSTFinTypeFees(finFeeDetail, finTypeFee, financeDetail, gstExecutionMap);
				} else {
					finFeeDetail.setActualAmountOriginal(finTypeFee.getAmount());
					finFeeDetail.setActualAmountGST(BigDecimal.ZERO);
					finFeeDetail.setActualAmount(finTypeFee.getAmount());
					
					BigDecimal netAmountOriginal = finFeeDetail.getActualAmountOriginal().subtract(finFeeDetail.getWaivedAmount());
					
					finFeeDetail.setNetAmountOriginal(netAmountOriginal);
					finFeeDetail.setNetAmountGST(BigDecimal.ZERO);
					finFeeDetail.setNetAmount(netAmountOriginal);
					
					if (StringUtils.equals(finTypeFee.getFeeScheduleMethod(), CalculationConstants.REMFEE_PAID_BY_CUSTOMER)) {
						finFeeDetail.setPaidAmountOriginal(finTypeFee.getAmount());
						finFeeDetail.setPaidAmountGST(BigDecimal.ZERO);
						finFeeDetail.setPaidAmount(finTypeFee.getAmount());
					}
					
					if(StringUtils.equals(finTypeFee.getFeeScheduleMethod(), CalculationConstants.REMFEE_WAIVED_BY_BANK)){
						finFeeDetail.setWaivedAmount(finTypeFee.getAmount());
					}
					
					finFeeDetail.setRemainingFeeOriginal(finFeeDetail.getActualAmount().subtract(finFeeDetail.getWaivedAmount()).subtract(finFeeDetail.getPaidAmount()));
					finFeeDetail.setRemainingFeeGST(BigDecimal.ZERO);
					finFeeDetail.setRemainingFee(finFeeDetail.getActualAmount().subtract(finFeeDetail.getWaivedAmount()).subtract(finFeeDetail.getPaidAmount()));
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
	
	public FinFeeDetail setFinFeeDetails(FinTypeFees finTypeFee,FinFeeDetail finFeeDetail, HashMap<String, Object> gstExecutionMap,String currency){
		
		FinanceDetail financeDetail= new FinanceDetail();
		FinScheduleData finScheduleData=new FinScheduleData();
		FinanceMain finMain= new FinanceMain();
		finMain.setFinCcy(currency);
		finScheduleData.setFinanceMain(finMain);
		financeDetail.setFinScheduleData(finScheduleData);
		if (finTypeFee!=null) {
			
			
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
					
						BigDecimal gstPerc=this.finFeeDetailService.actualGSTFees(finFeeDetail, currency, gstExecutionMap);
						String taxRoundMode = SysParamUtil.getValue(CalculationConstants.TAX_ROUNDINGMODE).toString();
						int taxRoundingTarget = SysParamUtil.getValueAsInt(CalculationConstants.TAX_ROUNDINGTARGET);
						if (StringUtils.equals(FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE, finFeeDetail.getTaxComponent())){
						BigDecimal gstNetOriginal =  calculateInclusivePercentage(finFeeDetail.getActualAmount(), finFeeDetail.getCgst(),finFeeDetail.getSgst(),
								finFeeDetail.getUgst(),finFeeDetail.getIgst(), taxRoundMode, taxRoundingTarget);
						
						finFeeDetail.setNetAmount(finFeeDetail.getActualAmount());
						finFeeDetail.setNetAmountOriginal(gstNetOriginal);
						finFeeDetail.setNetAmountGST(finFeeDetail.getActualAmount().subtract(gstNetOriginal));
						
						finFeeDetail.setActualAmountOriginal(gstNetOriginal.add(finFeeDetail.getWaivedAmount()));
						
						BigDecimal actualGst = this.finFeeDetailService.calculatePercentage(gstNetOriginal.subtract(finFeeDetail.getWaivedAmount()), gstPerc, taxRoundMode, taxRoundingTarget);
						finFeeDetail.setActualAmountGST(actualGst);
						finFeeDetail.setActualAmount(finFeeDetail.getActualAmountOriginal().add(actualGst));

						//finFeeDetail.setPaidAmount(finFeeDetail.getActualAmountOriginal().add(actualGst));
						finFeeDetail.setPaidAmountOriginal(gstNetOriginal.add(finFeeDetail.getWaivedAmount()));
						finFeeDetail.setPaidAmountGST(actualGst);
						}else{
							finFeeDetail.setNetAmountOriginal(finFeeDetail.getActualAmount().subtract(finFeeDetail.getWaivedAmount()));
							BigDecimal netGst = this.finFeeDetailService.calculatePercentage(finFeeDetail.getNetAmountOriginal(), gstPerc, taxRoundMode, taxRoundingTarget);
							finFeeDetail.setNetAmountGST(netGst);
							finFeeDetail.setNetAmount(finFeeDetail.getNetAmountOriginal().add(netGst));
							
							finFeeDetail.setActualAmountOriginal(finFeeDetail.getActualAmount());
							BigDecimal actualGst = this.finFeeDetailService.calculatePercentage(finFeeDetail.getActualAmountOriginal(), gstPerc, taxRoundMode, taxRoundingTarget);
							finFeeDetail.setActualAmountGST(actualGst);
							finFeeDetail.setActualAmount(finFeeDetail.getActualAmountOriginal().add(actualGst));

							//finFeeDetail.setPaidAmount(finFeeDetail.getActualAmountOriginal().add(actualGst));
							finFeeDetail.setPaidAmountOriginal(finFeeDetail.getNetAmountOriginal());
							finFeeDetail.setPaidAmount(finFeeDetail.getNetAmountOriginal().add(netGst));
							finFeeDetail.setPaidAmountGST(netGst);
						}
					//this.finFeeDetailService.convertGSTFinTypeFees(finFeeDetail, finTypeFee, financeDetail, gstExecutionMap);
					this.finFeeDetailService.calculateGSTFees(finFeeDetail, finMain, gstExecutionMap);
				} else {
					finFeeDetail.setActualAmountOriginal(finTypeFee.getAmount());
					finFeeDetail.setActualAmountGST(BigDecimal.ZERO);
					finFeeDetail.setActualAmount(finTypeFee.getAmount());
					
					BigDecimal netAmountOriginal = finFeeDetail.getActualAmountOriginal().subtract(finFeeDetail.getWaivedAmount());
					
					finFeeDetail.setNetAmountOriginal(netAmountOriginal);
					finFeeDetail.setNetAmountGST(BigDecimal.ZERO);
					finFeeDetail.setNetAmount(netAmountOriginal);
					
					if (StringUtils.equals(finTypeFee.getFeeScheduleMethod(), CalculationConstants.REMFEE_PAID_BY_CUSTOMER)) {
						finFeeDetail.setPaidAmountOriginal(finTypeFee.getAmount());
						finFeeDetail.setPaidAmountGST(BigDecimal.ZERO);
						finFeeDetail.setPaidAmount(finTypeFee.getAmount());
					}
					
					if(StringUtils.equals(finTypeFee.getFeeScheduleMethod(), CalculationConstants.REMFEE_WAIVED_BY_BANK)){
						finFeeDetail.setWaivedAmount(finTypeFee.getAmount());
					}
					
					finFeeDetail.setRemainingFeeOriginal(finFeeDetail.getActualAmount().subtract(finFeeDetail.getWaivedAmount()).subtract(finFeeDetail.getPaidAmount()));
					finFeeDetail.setRemainingFeeGST(BigDecimal.ZERO);
					finFeeDetail.setRemainingFee(finFeeDetail.getActualAmount().subtract(finFeeDetail.getWaivedAmount()).subtract(finFeeDetail.getPaidAmount()));
				}
				
				finFeeDetail.setRecordType(PennantConstants.RCD_ADD);
				finFeeDetail.setFinReference(null);
				finFeeDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				finFeeDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		}
		return finFeeDetail;
	}
	


	/**
	 * Method for execute finance fee details and validate with configured fees in loan type.<br>
	 * 	- Execute Origination fees.<br>
	 * 	- Execute Servicing fees.<br>
	 * 	FinEvent is always empty for origination fees.
	 * @param financeDetail
	 * @param finEvent
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	public void doExecuteFeeCharges(FinanceDetail financeDetail, String finEvent, FinServiceInstruction finServiceInst, boolean enquiry) 
			throws IllegalAccessException, InvocationTargetException {
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		boolean isOriginationFee = false;
		if (StringUtils.isBlank(finEvent)) {
			isOriginationFee = true;
			finEvent = PennantApplicationUtil.getEventCode(financeMain.getFinStartDate());
		}
		financeDetail.getFinScheduleData().setFeeEvent(finEvent);
		if(!financeDetail.getFinScheduleData().getFinanceType().isPromotionType()) {
			financeDetail.setFinTypeFeesList(financeDetailService.getFinTypeFees(financeMain.getFinType(), finEvent,
					isOriginationFee, FinanceConstants.MODULEID_FINTYPE));
		} else {
			financeDetail.setFinTypeFeesList(financeDetailService.getFinTypeFees(financeDetail.getFinScheduleData().getFinanceType().getPromotionCode(), finEvent,
					isOriginationFee, FinanceConstants.MODULEID_PROMOTION));
		}
		if(isOriginationFee) {
			for(FinFeeDetail finFeeDetail:financeDetail.getFinScheduleData().getFinFeeDetailList()) {
				if(StringUtils.equals(finFeeDetail.getFinEvent(), AccountEventConstants.ACCEVENT_VAS_FEE)) {
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
		
		/*for (FinFeeDetail finFeeDetail : finFeeDetailList) {
		//	if (finFeeDetail.isRcdVisible()) {
				finFeeDetailTemp.add(finFeeDetail);
			//}
		}*/
		return finFeeDetailTemp;
	}
	
	private BigDecimal calculateInclusivePercentage(BigDecimal amount, BigDecimal cgstPerc, BigDecimal sgstPerc, 
			BigDecimal ugstPerc, BigDecimal igstPerc,String taxRoundMode,int taxRoundingTarget) {
		logger.debug(Literal.ENTERING);
		
		if(amount.compareTo(BigDecimal.ZERO) == 0){
			return BigDecimal.ZERO;
		}
		
		BigDecimal totalGSTPerc = cgstPerc.add(sgstPerc).add(ugstPerc).add(igstPerc);
		BigDecimal percentage = (totalGSTPerc.add(new BigDecimal(100))).divide(BigDecimal.valueOf(100), 9, RoundingMode.HALF_DOWN);
		BigDecimal actualAmt = amount.divide(percentage, 9, RoundingMode.HALF_DOWN);
		actualAmt = CalculationUtil.roundAmount(actualAmt, taxRoundMode, taxRoundingTarget);
		
		
		
		BigDecimal actTaxAmount = amount.subtract(actualAmt);
		
		BigDecimal gstAmount = BigDecimal.ZERO;
		if(cgstPerc.compareTo(BigDecimal.ZERO) > 0){
			BigDecimal cgst = (actTaxAmount.multiply(cgstPerc)).divide(totalGSTPerc, 9, RoundingMode.HALF_DOWN);
			cgst = CalculationUtil.roundAmount(cgst, taxRoundMode, taxRoundingTarget);
			gstAmount = gstAmount.add(cgst);
		}
		if(sgstPerc.compareTo(BigDecimal.ZERO) > 0){
			BigDecimal sgst = (actTaxAmount.multiply(sgstPerc)).divide(totalGSTPerc, 9, RoundingMode.HALF_DOWN);
			sgst = CalculationUtil.roundAmount(sgst, taxRoundMode, taxRoundingTarget);
			gstAmount = gstAmount.add(sgst);
		}
		if(ugstPerc.compareTo(BigDecimal.ZERO) > 0){
			BigDecimal ugst = (actTaxAmount.multiply(ugstPerc)).divide(totalGSTPerc, 9, RoundingMode.HALF_DOWN);
			ugst = CalculationUtil.roundAmount(ugst, taxRoundMode, taxRoundingTarget);
			gstAmount = gstAmount.add(ugst);
		}
		if(igstPerc.compareTo(BigDecimal.ZERO) > 0){
			BigDecimal igst = (actTaxAmount.multiply(igstPerc)).divide(totalGSTPerc, 9, RoundingMode.HALF_DOWN);
			igst = CalculationUtil.roundAmount(igst, taxRoundMode, taxRoundingTarget);
			gstAmount = gstAmount.add(igst);
		}
		
		logger.debug(Literal.LEAVING);
		return amount.subtract(gstAmount);
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
