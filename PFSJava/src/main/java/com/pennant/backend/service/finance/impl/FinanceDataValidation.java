package com.pennant.backend.service.finance.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.HolidayHandlerTypes;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.model.RateDetail;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.FrequencyUtil;
import com.pennant.app.util.RateUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.applicationmaster.BaseRateDAO;
import com.pennant.backend.dao.applicationmaster.BranchDAO;
import com.pennant.backend.dao.applicationmaster.FlagDAO;
import com.pennant.backend.dao.applicationmaster.SplRateDAO;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.BankDetail;
import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.applicationmaster.RelationshipOfficer;
import com.pennant.backend.model.applicationmasters.Flag;
import com.pennant.backend.model.bmtmasters.BankBranch;
import com.pennant.backend.model.collateral.CollateralAssignment;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.collateral.CollateralThirdParty;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinODPenaltyRate;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceStepPolicyDetail;
import com.pennant.backend.model.finance.GuarantorDetail;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.model.financemanagement.FinFlagsDetail;
import com.pennant.backend.model.mandate.Mandate;
import com.pennant.backend.model.rmtmasters.FinTypeFees;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.solutionfactory.StepPolicyHeader;
import com.pennant.backend.model.systemmasters.DocumentType;
import com.pennant.backend.model.systemmasters.GeneralDepartment;
import com.pennant.backend.service.applicationmaster.BankDetailService;
import com.pennant.backend.service.applicationmaster.RelationshipOfficerService;
import com.pennant.backend.service.bmtmasters.BankBranchService;
import com.pennant.backend.service.collateral.CollateralSetupService;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.mandate.MandateService;
import com.pennant.backend.service.solutionfactory.StepPolicyService;
import com.pennant.backend.service.systemmasters.DocumentTypeService;
import com.pennant.backend.service.systemmasters.GeneralDepartmentService;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;

public class FinanceDataValidation {

	private BaseRateDAO		baseRateDAO;
	private SplRateDAO		splRateDAO;
	private BranchDAO		branchDAO;
	private FinanceTypeDAO	financeTypeDAO;
	private CustomerDAO		customerDAO;
	private FinanceDetailService financeDetailService; 
	private BankDetailService bankDetailService;
	private BankBranchService bankBranchService;
	private DocumentTypeService documentTypeService;
	private CustomerDetailsService customerDetailsService;
	private FlagDAO flagDAO;
	private CollateralSetupService collateralSetupService;
	private MandateService mandateService;
	private StepPolicyService stepPolicyService;
	private GeneralDepartmentService generalDepartmentService;
	private RelationshipOfficerService relationshipOfficerService;


	public FinanceDataValidation() {
		super();
	}

	/**
	 * Method for Validating Finance Schedule Prepared Data against application masters.
	 * 
	 * @param vldGroup
	 * @param finScheduleData
	 * @param apiFlag
	 * @return
	 */
	public FinScheduleData financeDataValidation(String vldGroup, FinScheduleData finScheduleData, boolean apiFlag) {

		FinanceMain finMain = finScheduleData.getFinanceMain();
		FinanceType financeType = finScheduleData.getFinanceType();

		List<ErrorDetails> errorDetails = null;
		boolean isAPICall = apiFlag;
		BigDecimal zeroAmount = BigDecimal.ZERO;

		// Non Finance validation
		errorDetails = nonFinanceValidation(vldGroup, finScheduleData, isAPICall);

		if (!errorDetails.isEmpty()) {
			finScheduleData.setErrorDetails(errorDetails);
			return finScheduleData;
		}

		// Basic Details validation
		errorDetails = basicValidation(vldGroup, finScheduleData, isAPICall);
		if (!errorDetails.isEmpty()) {
			finScheduleData.setErrorDetails(errorDetails);
			return finScheduleData;
		}

		// Grace Details Validation
		if (finMain.isAllowGrcPeriod()) {
			errorDetails = graceValidation(vldGroup, finScheduleData, isAPICall);
			if (!errorDetails.isEmpty()) {
				finScheduleData.setErrorDetails(errorDetails);
				return finScheduleData;
			}
		}

		// Repayments Details Validation
		errorDetails = repayValidation(vldGroup, finScheduleData, isAPICall);
		if (!errorDetails.isEmpty()) {
			finScheduleData.setErrorDetails(errorDetails);
			return finScheduleData;
		}

		// Fee validations
		errorDetails = feeValidations(vldGroup, finScheduleData, isAPICall);
		if(!errorDetails.isEmpty()) {
			finScheduleData.setErrorDetails(errorDetails);
			return finScheduleData;
		}


		// Insurance validations
		if(finScheduleData.getInsuranceList() != null && !finScheduleData.getInsuranceList().isEmpty()) {
			errorDetails = insuranceValidations(vldGroup, finScheduleData, isAPICall);
			if(!errorDetails.isEmpty()) {
				finScheduleData.setErrorDetails(errorDetails);
				return finScheduleData;
			}
		}

		// Step validations
		if(finScheduleData.getStepPolicyDetails() != null && !finScheduleData.getStepPolicyDetails().isEmpty()) {
			errorDetails = stepValidations(vldGroup, finScheduleData, isAPICall);
			if(!errorDetails.isEmpty()) {
				finScheduleData.setErrorDetails(errorDetails);
				return finScheduleData;
			}
		}

		//TODO: Below code and respective declarations should be moved to a specific portion
		//Net Loan Amount
		BigDecimal netLoanAmount = finMain.getFinAmount().subtract(finMain.getDownPayment());
		if (netLoanAmount.compareTo(financeType.getFinMinAmount()) < 0) {
			String[] valueParm = new String[1];
			valueParm[0] = String.valueOf(financeType.getFinMinAmount());
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90132", valueParm)));
		}

		if (financeType.getFinMaxAmount().compareTo(zeroAmount) > 0) {
			if (netLoanAmount.compareTo(financeType.getFinMaxAmount()) > 0) {
				String[] valueParm = new String[1];
				valueParm[0] = String.valueOf(financeType.getFinMaxAmount());
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90133", valueParm)));
			}
		}

		if (finMain.getReqRepayAmount().compareTo(BigDecimal.ZERO) < 0) {
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90187", null)));
		}

		if (finMain.getReqRepayAmount().compareTo(netLoanAmount) > 0) {
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90188", null)));
		}

		errorDetails = finODPenaltyRateValidation(finScheduleData);

		if(!errorDetails.isEmpty()) {
			finScheduleData.setErrorDetails(errorDetails);
			return finScheduleData;
		}

		return finScheduleData;
	}
	
	/**
	 * 
	 * @param finScheduleData
	 * @return
	 */
	private List<ErrorDetails> finODPenaltyRateValidation(FinScheduleData finScheduleData) {
		List<ErrorDetails> errorDetails = new ArrayList<ErrorDetails>();
		FinODPenaltyRate finODPenaltyRate = finScheduleData.getFinODPenaltyRate();
		if(finODPenaltyRate != null) {
			if (!finODPenaltyRate.isApplyODPenalty()) {
				if (finODPenaltyRate.isODIncGrcDays() || StringUtils.isNotBlank(finODPenaltyRate.getODChargeType())
						|| StringUtils.isNotBlank(finODPenaltyRate.getODChargeCalOn())
						|| finODPenaltyRate.getODChargeAmtOrPerc().compareTo(BigDecimal.ZERO) > 0
						|| finODPenaltyRate.isODAllowWaiver()) {
					String[] valueParm = new String[1];
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90315", valueParm)));
				}
			} else {
				if (!finODPenaltyRate.isODIncGrcDays() || StringUtils.isBlank(finODPenaltyRate.getODChargeType())
						|| StringUtils.isBlank(finODPenaltyRate.getODChargeCalOn())
						|| finODPenaltyRate.getODChargeAmtOrPerc().compareTo(BigDecimal.ZERO) <= 0
						|| !finODPenaltyRate.isODAllowWaiver()) {
					String[] valueParm = new String[1];
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90314", valueParm)));
				}
			}

			if (!(finODPenaltyRate.isApplyODPenalty() && finODPenaltyRate.isODAllowWaiver())) {
				if (finODPenaltyRate.getODMaxWaiverPerc().compareTo(BigDecimal.ZERO) > 0) {
					String[] valueParm = new String[1];
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90315", valueParm)));
				}
			} else {
				if (finODPenaltyRate.getODMaxWaiverPerc().compareTo(BigDecimal.ZERO) <= 0) {
					String[] valueParm = new String[2];
					valueParm[0] = "ODMaxWaiverPerc";
					valueParm[1] = "Zero";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("91121", valueParm)));
				}
			}
			if (StringUtils.equals(finODPenaltyRate.getODChargeType(), FinanceConstants.PENALTYTYPE_PERCONETIME)) {
				if (finODPenaltyRate.getODChargeAmtOrPerc().compareTo(new BigDecimal(99)) > 0) {
					String[] valueParm = new String[2];
					valueParm[0] = "ODChargeAmtOrPerc";
					valueParm[1] = "99";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("30565", valueParm)));
				}
			}
			if (StringUtils.isNotBlank(finODPenaltyRate.getODChargeType())) {
				List<ValueLabel> finODChargeType = PennantStaticListUtil.getODCChargeType();
				boolean finODChargeTypeSts = false;
				for (ValueLabel value : finODChargeType) {
					if (StringUtils.equals(value.getValue(), finODPenaltyRate.getODChargeType())) {
						finODChargeTypeSts = true;
						break;
					}
				}
				if (!finODChargeTypeSts) {
					String[] valueParm = new String[2];
					valueParm[0] = finODPenaltyRate.getODChargeType();
					valueParm[1] = FinanceConstants.PENALTYTYPE_FLAT +","+FinanceConstants.PENALTYTYPE_FLATAMTONPASTDUEMTH 
							+","+FinanceConstants.PENALTYTYPE_PERCONDUEDAYS+","+FinanceConstants.PENALTYTYPE_PERCONDUEMTH 
							+","+FinanceConstants.PENALTYTYPE_PERCONETIME;
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90316", valueParm)));
				}
			}

			if(StringUtils.isNotBlank(finODPenaltyRate.getODChargeCalOn())){
				List<ValueLabel> finODCCalculatedOn = PennantStaticListUtil.getODCCalculatedOn();
				boolean finODCCalculatedOnSts = false;
				for (ValueLabel value : finODCCalculatedOn) {
					if (StringUtils.equals(value.getValue(), finODPenaltyRate.getODChargeCalOn())) {
						finODCCalculatedOnSts = true;
						break;
					}
				}
				if (!finODCCalculatedOnSts) {
					String[] valueParm = new String[2];
					valueParm[0] = finODPenaltyRate.getODChargeCalOn();
					valueParm[1] = FinanceConstants.ODCALON_STOT +","+FinanceConstants.ODCALON_SPFT
							+","+FinanceConstants.ODCALON_SPRI;
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90317", valueParm)));
				}
			}
		}
		return errorDetails;		
	}
	
	/*
	 * ================================================================================================================
	 * VALIDATE Finance Details
	 * ================================================================================================================
	 */
	public FinScheduleData financeDetailValidation(String vldGroup, FinanceDetail financeDetail, boolean apiFlag) {

		List<ErrorDetails> errorDetails = new ArrayList<ErrorDetails>();
		FinanceMain finMain = financeDetail.getFinScheduleData().getFinanceMain();
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		Customer customer=null;
		boolean isCreateLoan = false;

		if (StringUtils.equals(vldGroup, PennantConstants.VLD_CRT_LOAN)) {
			isCreateLoan = true;
		}

		// Validate customer
		if (isCreateLoan || StringUtils.isNotBlank(finMain.getLovDescCustCIF())) {
			customer = customerDAO.getCustomerByCIF(finMain.getLovDescCustCIF(), "");
			if (customer == null) {
				String[] valueParm = new String[1];
				valueParm[0] = finMain.getLovDescCustCIF();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90101", valueParm)));
				finScheduleData.setErrorDetails(errorDetails);
				return finScheduleData;
			}
		}

		//Validate Finance Type
		FinanceType financeType = financeTypeDAO.getFinanceTypeByID(finMain.getFinType(), "");
		if (financeType == null) {
			String[] valueParm = new String[1];
			valueParm[0] = finMain.getFinType();
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90202", valueParm)));
		} else {
			finScheduleData.setFinanceType(financeType);
			if (finMain.getFinContractDate() == null) {
				finMain.setFinContractDate(financeType.getStartDate());
			} else {
				if (finMain.getFinContractDate().compareTo(finMain.getFinStartDate()) > 0) {
					String[] valueParm = new String[2];
					valueParm[0] = DateUtility.formatDate(finMain.getFinContractDate(), PennantConstants.XMLDateFormat);
					valueParm[1] = DateUtility.formatDate(finMain.getFinStartDate(), PennantConstants.XMLDateFormat);
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90205", valueParm)));
				}
			}
			if (financeType.isLimitRequired() && ImplementationConstants.LIMIT_MODULE) {
				/*if (StringUtils.isBlank(finMain.getFinLimitRef())) {
					String[] valueParm = new String[1];
					valueParm[0] = "finLimitRef";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90502", valueParm)));
				} else {
					//TODO 
				}*/
			}
	/*		if (financeType.isFinCommitmentReq()) {
				if (StringUtils.isBlank(finMain.getFinCommitmentRef())) {
					String[] valueParm = new String[1];
					valueParm[0] = "finCommitmentRef";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90502", valueParm)));
				} else {
					Commitment commitment = commitmentService.getApprovedCommitmentById(finMain.getFinCommitmentRef());
					if (commitment == null) {
						String[] valueParm = new String[2];
						valueParm[0] = "finCommitmentRef";
						valueParm[1] = finMain.getFinCommitmentRef();
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90224", valueParm)));
					} else {
						if (customer.getCustID() != commitment.getCustID()) {
							String[] valueParm = new String[2];
							valueParm[0] = String.valueOf(commitment.getCustID());
							valueParm[1] = "finCommitmentRef";
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90250", valueParm)));
						}
					}

				}
			}*/
			if (StringUtils.equals(finMain.getFinRepayMethod(), FinanceConstants.REPAYMTH_AUTO)) {
				if (StringUtils.isBlank(finMain.getRepayAccountId())) {
					String[] valueParm = new String[1];
					valueParm[0] = "repayAccountId";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90502", valueParm)));
				}
			}
			if (financeType.isFinDepreciationReq()) {
				if (StringUtils.isBlank(finMain.getDepreciationFrq())) {
					String[] valueParm = new String[1];
					valueParm[0] = "depreciationFrq";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90502", valueParm)));
				} else {
					ErrorDetails errorDetail = FrequencyUtil.validateFrequency(finMain.getDepreciationFrq());
					if (errorDetail != null && StringUtils.isNotBlank(errorDetail.getErrorCode())) {
						String[] valueParm = new String[1];
						valueParm[0] = finMain.getDepreciationFrq();
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90207", valueParm)));
					}
				}
			}
			if (StringUtils.isNotBlank(finMain.getDsaCode())) {
				RelationshipOfficer relationshipOfficer = relationshipOfficerService
						.getApprovedRelationshipOfficerById(finMain.getDsaCode());
				if (relationshipOfficer == null) {
					String[] valueParm = new String[1];
					valueParm[0] = finMain.getDsaCode();
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90501", valueParm)));
				}
			}
			if (StringUtils.isNotBlank(finMain.getAccountsOfficer())) {
				GeneralDepartment generalDepartment = generalDepartmentService.getApprovedGeneralDepartmentById(finMain
						.getAccountsOfficer());
				if (generalDepartment == null) {
					String[] valueParm = new String[1];
					valueParm[0] = finMain.getAccountsOfficer();
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90501", valueParm)));
				}
			}
			if (StringUtils.isNotBlank(finMain.getSalesDepartment())) {
				GeneralDepartment generalDepartment = generalDepartmentService.getApprovedGeneralDepartmentById(finMain
						.getSalesDepartment());
				if (generalDepartment == null) {
					String[] valueParm = new String[1];
					valueParm[0] = finMain.getAccountsOfficer();
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90501", valueParm)));
				}
			}
			if (StringUtils.isNotBlank(finMain.getDmaCode())) {
				RelationshipOfficer relationshipOfficer = relationshipOfficerService
						.getApprovedRelationshipOfficerById(finMain.getDmaCode());
				if (relationshipOfficer == null) {
					String[] valueParm = new String[1];
					valueParm[0] = finMain.getDsaCode();
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90501", valueParm)));
				}
			}
			if (StringUtils.isNotBlank(finMain.getReferralId())) {
				RelationshipOfficer relationshipOfficer = relationshipOfficerService
						.getApprovedRelationshipOfficerById(finMain.getReferralId());
				if (relationshipOfficer == null) {
					String[] valueParm = new String[1];
					valueParm[0] = finMain.getDsaCode();
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90501", valueParm)));
				}
			}
		}

		//Validate Finance Currency
		Currency currency = CurrencyUtil.getCurrencyObject(finMain.getFinCcy());
		if (currency == null) {
			String[] valueParm = new String[1];
			valueParm[0] = finMain.getFinCcy();
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90120", valueParm)));
		}

		// validate finance branch
		if (isCreateLoan || StringUtils.isNotBlank(finMain.getFinBranch())) {
			Branch branch = branchDAO.getBranchById(finMain.getFinBranch(), "");
			if (branch == null) {
				String[] valueParm = new String[1];
				valueParm[0] = finMain.getFinBranch();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90129", valueParm)));
			}
		}

		//Validate Repayment Method
		//TODO: To be confirmed from where it should be taken? PennantStaticListUtil.getRepayMethods() OR MandateConstants or FinanceConstants??
		if (isCreateLoan) {
			String repayMethod = finMain.getFinRepayMethod();

			// finRepay method
			if(StringUtils.isNotBlank(repayMethod)) {
				List<ValueLabel> repayMethods = PennantStaticListUtil.getRepayMethods();
				boolean repayMehodSts = false;
				for (ValueLabel value : repayMethods) {
					if (StringUtils.equals(value.getValue(), repayMethod)) {
						repayMehodSts = true;
						break;
					}
				}
				if (!repayMehodSts) {
					String[] valueParm = new String[1];
					valueParm[0] = repayMethod;
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90307", valueParm)));
				}
			}

			finScheduleData.setErrorDetails(errorDetails);

			errorDetails = disbursementValidation(financeDetail);
			if(!errorDetails.isEmpty()) {
				finScheduleData.setErrorDetails(errorDetails);
				return finScheduleData;
			}

			errorDetails = mandateValidation(financeDetail);
			if(!errorDetails.isEmpty()) {
				finScheduleData.setErrorDetails(errorDetails);
				return finScheduleData;
			}

			errorDetails = documentValidation(financeDetail);
			if(!errorDetails.isEmpty()) {
				finScheduleData.setErrorDetails(errorDetails);
				return finScheduleData;
			}

			errorDetails = jountAccountDetailsValidation(financeDetail);
			if(!errorDetails.isEmpty()) {
				finScheduleData.setErrorDetails(errorDetails);
				return finScheduleData;
			}
			errorDetails = gurantorsDetailValidation(financeDetail);
			if(!errorDetails.isEmpty()) {
				finScheduleData.setErrorDetails(errorDetails);
				return finScheduleData;
			}
			errorDetails = finFlagsDetailValidation(financeDetail);
			if(!errorDetails.isEmpty()) {
				finScheduleData.setErrorDetails(errorDetails);
				return finScheduleData;
			}
			errorDetails = finCollateralValidation(financeDetail);
			if(!errorDetails.isEmpty()) {
				finScheduleData.setErrorDetails(errorDetails);
				return finScheduleData;
			}
		}

		return finScheduleData;


	}

	/*
	 * ################################################################################################################
	 * MAIN METHODS
	 * ################################################################################################################
	 */

	/*
	 * ================================================================================================================
	 * VALIDATE NON FINANCE DATA
	 * ================================================================================================================
	 */

	private List<ErrorDetails> finCollateralValidation(FinanceDetail financeDetail) {
		List<ErrorDetails> errorDetails = new ArrayList<ErrorDetails>();
		List<CollateralAssignment> finCollateralAssignmentDetails = financeDetail.getCollateralAssignmentList();
		if (finCollateralAssignmentDetails != null) {
			for (CollateralAssignment collateralAssignment : finCollateralAssignmentDetails) {
				if(StringUtils.isEmpty(collateralAssignment.getCollateralRef())){
					String[] valueParm = new String[1];
					valueParm[0] = "collateralRef";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90502", valueParm)));
					return errorDetails;
				}
				BigDecimal assignPerc = collateralAssignment.getAssignPerc() == null?BigDecimal.ZERO:collateralAssignment.getAssignPerc();
				if(assignPerc.compareTo(BigDecimal.ZERO) <= 0){
					String[] valueParm = new String[2];
					valueParm[0] = "assignPerc";
					valueParm[1] = "1";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90205", valueParm)));
					return errorDetails;
				}
				CollateralSetup collateralSetup = collateralSetupService.getApprovedCollateralSetupById(collateralAssignment.getCollateralRef());
				if (collateralSetup == null) {
					String[] valueParm = new String[1];
					valueParm[0] = collateralAssignment.getCollateralRef();
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90906", valueParm)));
					return errorDetails;
				}
				if (!StringUtils.equalsIgnoreCase(collateralSetup.getDepositorCif(), 
						financeDetail.getFinScheduleData().getFinanceMain().getLovDescCustCIF())) {
					boolean isNotValidCif = true;
					if (collateralSetup.getCollateralThirdPartyList() != null
							&& !collateralSetup.getCollateralThirdPartyList().isEmpty()) {
						for (CollateralThirdParty collateralThirdParty : collateralSetup.getCollateralThirdPartyList()) {
							if (StringUtils.equalsIgnoreCase(collateralThirdParty.getCustCIF(), financeDetail
									.getFinScheduleData().getFinanceMain().getLovDescCustCIF())) {
								isNotValidCif = false;
								break;
							}
						}
					}

					if (isNotValidCif) {
						String[] valueParm = new String[2];
						valueParm[0] = collateralSetup.getDepositorCif();
						valueParm[1] = financeDetail.getFinScheduleData().getFinanceMain().getLovDescCustCIF();
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90249", valueParm)));
						return errorDetails;
					}

				}
				BigDecimal totAssignedPerc = collateralSetupService.getAssignedPerc(collateralSetup.getCollateralRef(), "");//TODO:Add reference
				BigDecimal curAssignValue = collateralSetup.getBankValuation().multiply(collateralAssignment.getAssignPerc() == null ? 
						BigDecimal.ZERO : collateralAssignment.getAssignPerc()).divide(new BigDecimal(100), 0, RoundingMode.HALF_DOWN);
				BigDecimal totAssignedValue = collateralSetup.getBankValuation().multiply(totAssignedPerc)
						.divide(new BigDecimal(100), 0, RoundingMode.HALF_DOWN);
				BigDecimal availAssignValue = collateralSetup.getBankValuation().subtract(totAssignedValue);
				if (availAssignValue.compareTo(curAssignValue) < 0) {
					String[] valueParm = new String[2];
					valueParm[0] = "Collateral available assign value("+String.valueOf(availAssignValue)+")";
					valueParm[1] = "current assign value("+String.valueOf(curAssignValue)+")";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("65012", valueParm)));
					return errorDetails;
				}

				if (availAssignValue.compareTo(financeDetail.getFinScheduleData().getFinanceMain().getFinAmount()) < 0) {
					String[] valueParm = new String[2];
					valueParm[0] = "Available assign value("+String.valueOf(availAssignValue)+")";
					valueParm[1] = "loan amount("+String.valueOf(financeDetail.getFinScheduleData().getFinanceMain().getFinAmount())+")";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("65012", valueParm)));
					return errorDetails;
				}
			}
		}
		return errorDetails;
	}
	private List<ErrorDetails> finFlagsDetailValidation(FinanceDetail financeDetail) {
		List<ErrorDetails> errorDetails = new ArrayList<ErrorDetails>();
		// validate flags details
		List<FinFlagsDetail> finFlagDetails = financeDetail.getFinFlagsDetails();
		if(finFlagDetails != null) {
			for(FinFlagsDetail flag: finFlagDetails) {
				Flag flagDetail = flagDAO.getFlagById(flag.getFlagCode(), "");
				if(flagDetail == null) {
					String[] valueParm = new String[1];
					valueParm[0] = flag.getFlagCode();
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("91001", valueParm)));
				}
			}
		}
		return errorDetails;
	}

	private List<ErrorDetails> gurantorsDetailValidation(FinanceDetail financeDetail) {
		List<ErrorDetails> errorDetails = new ArrayList<ErrorDetails>();
		List<GuarantorDetail> guarantorDetails = financeDetail.getGurantorsDetailList();
		if(guarantorDetails != null) {
			for(GuarantorDetail detail: guarantorDetails) {
				if(detail.isBankCustomer()) {
					String guarantorCIF = detail.getGuarantorCIF();
					if(StringUtils.equals(guarantorCIF, financeDetail.getFinScheduleData().getFinanceMain().getLovDescCustCIF())) {
						String[] valueParm = new String[2];
						valueParm[0] = guarantorCIF;
						valueParm[1] = "guarantor";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90250", valueParm)));
						return errorDetails;
					}
					Customer guarantor = customerDetailsService.getCustomerByCIF(guarantorCIF);
					if(guarantor == null) {
						String[] valueParm = new String[1];
						valueParm[0] = guarantorCIF;
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90103", valueParm)));
					}
				}
			}
		}
		return errorDetails;
	}

	private List<ErrorDetails> jountAccountDetailsValidation(FinanceDetail financeDetail) {
		List<ErrorDetails> errorDetails = new ArrayList<ErrorDetails>();
		List<JointAccountDetail> jountAccountDetails = financeDetail.getJountAccountDetailList();
		if(jountAccountDetails != null) {
			for (JointAccountDetail jointAccDetail : jountAccountDetails) {
				if(jointAccDetail.isIncludeRepay()){
					if(StringUtils.isBlank(jointAccDetail.getRepayAccountId())){
						String[] valueParm = new String[2];
						valueParm[0] = "RepayAccountId";
						valueParm[1] = "IncludeRepay";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90124", valueParm)));
						return errorDetails;
					}
				}
				if(StringUtils.equals(jointAccDetail.getCustCIF(), financeDetail.getFinScheduleData().getFinanceMain().getLovDescCustCIF())) {
					String[] valueParm = new String[2];
					valueParm[0] = jointAccDetail.getCustCIF();
					valueParm[1] = "co-applicant";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90250", valueParm)));
					return errorDetails;
				}
				Customer coApplicant = customerDetailsService.getCustomerByCIF(jointAccDetail.getCustCIF());
				if (coApplicant == null) {
					String[] valueParm = new String[1];
					valueParm[0] = jointAccDetail.getCustCIF();
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90102", valueParm)));
				}
			}
		}
		return errorDetails;
	}

	private List<ErrorDetails> documentValidation(FinanceDetail financeDetail) {
		List<ErrorDetails> errorDetails = new ArrayList<ErrorDetails>();
		// validate document details
		List<DocumentDetails> documentDetails = financeDetail.getDocumentDetailsList();
		if (documentDetails != null) {
			for (DocumentDetails detail : documentDetails) {
				
				 //validate Dates
				if (detail.getCustDocIssuedOn() != null && detail.getCustDocExpDate() != null) {
					if (detail.getCustDocIssuedOn().compareTo(detail.getCustDocExpDate()) > 0) {
						String[] valueParm = new String[2];
						valueParm[0] = DateUtility.formatDate(detail.getCustDocIssuedOn(),
								PennantConstants.XMLDateFormat);
						valueParm[1] = DateUtility.formatDate(detail.getCustDocExpDate(),
								PennantConstants.XMLDateFormat);
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90205", valueParm)));
						return errorDetails;
					}
				}
				
				if (StringUtils.equals(detail.getDocCategory(), "01")) {
					Pattern r = Pattern.compile("^[0-9]{12}$");
					Matcher m = r.matcher(detail.getCustDocTitle());
					if(m.find() == false ){
						String[] valueParm = new String[0];
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90251", valueParm)));
					}
				}

				if (StringUtils.isBlank(detail.getDocUri())) {
					if (detail.getDocImage() == null || detail.getDocImage().length <= 0) {
						String[] valueParm = new String[2];
						valueParm[0] = "docContent";
						valueParm[1] = "docRefId";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90123", valueParm)));
					}
				}
				
				DocumentType docType = documentTypeService.getDocumentTypeById(detail.getDocCategory());
				if (docType == null) {
					String[] valueParm = new String[1];
					valueParm[0] = detail.getDocCategory();
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90401", valueParm)));
					return errorDetails;
				}

				// validate Is Customer document?
				if (docType.isDocIsCustDoc()) {
					if (StringUtils.isBlank(detail.getCustDocTitle())) {
						String[] valueParm = new String[2];
						valueParm[0] = "CustDocTitle";
						valueParm[1] = docType.getDocTypeCode();
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90402", valueParm)));
					}
				}

				// validate custDocIssuedCountry
				if (docType.isDocIsCustDoc() && docType.isDocIssuedAuthorityMand()) {
					if (StringUtils.isBlank(detail.getCustDocIssuedCountry())) {
						String[] valueParm = new String[1];
						valueParm[0] = "CustDocIssuedCountry";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90402", valueParm)));
					}
				}
				// validate custDocIssuedOn
				if (docType.isDocIssueDateMand()) {
					if (detail.getCustDocIssuedOn() == null) {
						String[] valueParm = new String[2];
						valueParm[0] = "CustDocIssuedOn";
						valueParm[1] = docType.getDocTypeCode();
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90402", valueParm)));
					}
				}

				// validate custDocExpDate
				if (docType.isDocExpDateIsMand()) {
					if (detail.getCustDocExpDate() == null) {
						String[] valueParm = new String[2];
						valueParm[0] = "CustDocExpDate";
						valueParm[1] = docType.getDocTypeCode();
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90402", valueParm)));
					}
				}
			}
		}
		return errorDetails;
	}

	private List<ErrorDetails> mandateValidation(FinanceDetail financeDetail) {

		List<ErrorDetails> errorDetails = new ArrayList<ErrorDetails>();
		Mandate mandate = financeDetail.getMandate();
		if (mandate != null) {
			if (mandate.isUseExisting()) {
				if (mandate.getMandateID() == Long.MIN_VALUE) {
					String[] valueParm = new String[1];
					valueParm[0] = "MandateID";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90502", valueParm)));
					return errorDetails;
				} else {
					Mandate curMandate = mandateService.getApprovedMandateById(mandate.getMandateID());
					if (curMandate == null) {
						String[] valueParm = new String[1];
						valueParm[0] = String.valueOf(mandate.getMandateID());
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90303", valueParm)));
						return errorDetails;
					} else {
						if (!StringUtils.equalsIgnoreCase(curMandate.getCustCIF(), financeDetail.getFinScheduleData()
								.getFinanceMain().getLovDescCustCIF())) {
							String[] valueParm = new String[2];
							valueParm[0] = financeDetail.getFinScheduleData().getFinanceMain().getLovDescCustCIF();
							valueParm[1] = curMandate.getCustCIF();
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90310", valueParm)));
							return errorDetails;
						}
						if (!StringUtils.equalsIgnoreCase(curMandate.getMandateType(), financeDetail
								.getFinScheduleData().getFinanceMain().getFinRepayMethod())) {
							String[] valueParm = new String[2];
							valueParm[0] = financeDetail.getFinScheduleData().getFinanceMain().getFinRepayMethod();
							valueParm[1] = curMandate.getMandateType();
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90311", valueParm)));
							return errorDetails;
						}
						if (!(curMandate.isOpenMandate() || (curMandate.getOrgReference() == null))) {
							String[] valueParm = new String[1];
							valueParm[0] = String.valueOf(mandate.getMandateID());
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90312", valueParm)));
							return errorDetails;
						}
						financeDetail.setMandate(curMandate);
					}
				}
			} else {
				mandate.setMandateID(Long.MIN_VALUE);
				// validate Mandate fields
				if (StringUtils.isBlank(mandate.getMandateType())) {
					String[] valueParm = new String[1];
					valueParm[0] = "mandateType";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90502", valueParm)));
				}

				if (StringUtils.isBlank(mandate.getIFSC())) {
					if ((StringUtils.isBlank(mandate.getBankCode()) && StringUtils.isBlank(mandate.getBranchCode()))) {
						String[] valueParm = new String[1];
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90313", valueParm)));
					}
				}
				if (StringUtils.isBlank(mandate.getAccType())) {
					String[] valueParm = new String[1];
					valueParm[0] = "accType";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90502", valueParm)));
				}
				if (StringUtils.isBlank(mandate.getAccNumber())) {
					String[] valueParm = new String[1];
					valueParm[0] = "accNumber";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90502", valueParm)));
				}
				if (StringUtils.isBlank(mandate.getAccHolderName())) {
					String[] valueParm = new String[1];
					valueParm[0] = "accHolderName";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90502", valueParm)));
				}
				if (mandate.getStartDate() == null) {
					String[] valueParm = new String[1];
					valueParm[0] = "startDate";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90502", valueParm)));
				}
				if (mandate.getExpiryDate() == null) {
					String[] valueParm = new String[1];
					valueParm[0] = "expiryDate";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90502", valueParm)));
				}
				/*if (mandate.getMaxLimit().compareTo(BigDecimal.ZERO) < 0) {
					String[] valueParm = new String[1];
					valueParm[0] = "maxLimit";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90502", valueParm)));
				}*/
				if (StringUtils.isNotBlank(mandate.getPhoneCountryCode())) {
					if (StringUtils.isBlank(mandate.getPhoneAreaCode())) {
						String[] valueParm = new String[1];
						valueParm[0] = "phoneAreaCode";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90502", valueParm)));
					}
					if (StringUtils.isBlank(mandate.getPhoneNumber())) {
						String[] valueParm = new String[1];
						valueParm[0] = "phoneNumber";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90502", valueParm)));
					}
				}
				//validate Dates
				if (mandate.getStartDate().compareTo(mandate.getExpiryDate()) > 0) {
					String[] valueParm = new String[2];
					valueParm[0] = DateUtility.formatDate(mandate.getStartDate(), PennantConstants.XMLDateFormat);
					valueParm[1] = DateUtility.formatDate(mandate.getExpiryDate(), PennantConstants.XMLDateFormat);
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90205", valueParm)));
				}
				if (StringUtils.isNotBlank(mandate.getIFSC())) {
					BankBranch bankBranch = bankBranchService.getBankBrachByIFSC(mandate.getIFSC());
					if (bankBranch == null) {
						String[] valueParm = new String[1];
						valueParm[0] = mandate.getIFSC();
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90301", valueParm)));
					}
				} else if (StringUtils.isNotBlank(mandate.getBankCode())
						&& StringUtils.isNotBlank(mandate.getBranchCode())) {
					BankBranch bankBranch = bankBranchService.getBankBrachByCode(mandate.getBankCode(),
							mandate.getBranchCode());
					if (bankBranch == null) {
						String[] valueParm = new String[2];
						valueParm[0] = mandate.getBankCode();
						valueParm[1] = mandate.getBranchCode();
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90302", valueParm)));
					}
				}

				// validate MandateType
				if (StringUtils.isNotBlank(mandate.getMandateType())) {
					List<ValueLabel> mandateType = PennantStaticListUtil.getMandateTypeList();
					boolean mandateTypeSts = false;
					for (ValueLabel value : mandateType) {
						if (StringUtils.equals(value.getValue(), mandate.getMandateType())) {
							mandateTypeSts = true;
							break;
						}
					}
					if (!mandateTypeSts) {
						String[] valueParm = new String[1];
						valueParm[0] = mandate.getMandateType();
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90307", valueParm)));
					}
				}

				// validate AccType
				if (StringUtils.isNotBlank(mandate.getAccType())) {
					List<ValueLabel> accType = PennantStaticListUtil.getAccTypeList();
					boolean accTypeSts = false;
					for (ValueLabel value : accType) {
						if (StringUtils.equals(value.getValue(), mandate.getAccType())) {
							accTypeSts = true;
							break;
						}
					}
					if (!accTypeSts) {
						String[] valueParm = new String[1];
						valueParm[0] = mandate.getAccType();
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90308", valueParm)));
					}
				}

				//validate periodicity
				if (StringUtils.isNotBlank(mandate.getPeriodicity())) {
					ErrorDetails errorDetail = FrequencyUtil.validateFrequency(mandate.getPeriodicity());
					if (errorDetail != null && StringUtils.isNotBlank(errorDetail.getErrorCode())) {
						String[] valueParm = new String[1];
						valueParm[0] = mandate.getPeriodicity();
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90207", valueParm)));
					}
				}

				//validate status
				if (StringUtils.isNotBlank(mandate.getStatus())) {
					List<ValueLabel> status = PennantStaticListUtil.getStatusTypeList();
					boolean sts = false;
					for (ValueLabel value : status) {
						if (StringUtils.equals(value.getValue(), mandate.getStatus())) {
							sts = true;
							break;
						}
					}
					if (!sts) {
						String[] valueParm = new String[1];
						valueParm[0] = mandate.getStatus();
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90309", valueParm)));
					}
				}
				if (!StringUtils.equalsIgnoreCase(mandate.getMandateType(), financeDetail
						.getFinScheduleData().getFinanceMain().getFinRepayMethod())) {
					String[] valueParm = new String[2];
					valueParm[0] = financeDetail.getFinScheduleData().getFinanceMain().getFinRepayMethod();
					valueParm[1] = mandate.getMandateType();
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90311", valueParm)));
					return errorDetails;
				}
			}
		}
		return errorDetails;
	}

	private List<ErrorDetails> disbursementValidation(FinanceDetail financeDetail) {
		List<ErrorDetails> errorDetails = new ArrayList<ErrorDetails>();
		// validate disbursement details
		List<FinAdvancePayments> finAdvPayments = financeDetail.getAdvancePaymentsList();
		if (finAdvPayments != null) {
			for (FinAdvancePayments advPayment : finAdvPayments) {

				// validate disbType
				if (StringUtils.isNotBlank(advPayment.getPaymentType())) {
					List<ValueLabel> paymentTypes = PennantStaticListUtil.getPaymentTypes(false);
					boolean paymentTypeSts = false;
					for (ValueLabel value : paymentTypes) {
						if (StringUtils.equals(value.getValue(), advPayment.getPaymentType())) {
							paymentTypeSts = true;
							break;
						}
					}
					if (!paymentTypeSts) {
						String[] valueParm = new String[1];
						valueParm[0] = advPayment.getPaymentType();
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90216", valueParm)));
					}
				}

				if (StringUtils.equals(advPayment.getPaymentType(), DisbursementConstants.PAYMENT_TYPE_CHEQUE)
						|| StringUtils.equals(advPayment.getPaymentType(), DisbursementConstants.PAYMENT_TYPE_DD)) {

					// Issuer bank
					if (StringUtils.isBlank(advPayment.getBankCode())) {
						String[] valueParm = new String[1];
						valueParm[0] = "issueBank";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90214", valueParm)));
					} else {
						BankDetail bankDetail = bankDetailService.getBankDetailById(advPayment.getBankCode());
						if (bankDetail == null) {
							String[] valueParm = new String[1];
							valueParm[0] = advPayment.getBankCode();
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90213", valueParm)));
						}
					}

					// Liability hold name
					if (StringUtils.isBlank(advPayment.getLiabilityHoldName())) {
						String[] valueParm = new String[1];
						valueParm[0] = "favourName";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90214", valueParm)));
					}

					// Payable location
					if (StringUtils.isBlank(advPayment.getPayableLoc())) {
						String[] valueParm = new String[1];
						valueParm[0] = "payableLoc";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90214", valueParm)));
					}

					// Printing location
					if (StringUtils.isBlank(advPayment.getPrintingLoc())) {
						String[] valueParm = new String[1];
						valueParm[0] = "printingLoc";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90214", valueParm)));
					}

					// value date
					if (advPayment.getValueDate() == null) {
						String[] valueParm = new String[1];
						valueParm[0] = "valueDate";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90214", valueParm)));
					}
				} else if (StringUtils.equals(advPayment.getPaymentType(), DisbursementConstants.PAYMENT_TYPE_IMPS)
						|| StringUtils.equals(advPayment.getPaymentType(), DisbursementConstants.PAYMENT_TYPE_NEFT)
						|| StringUtils.equals(advPayment.getPaymentType(), DisbursementConstants.PAYMENT_TYPE_RTGS)) {

					// Ifsc, bank or branch codes
					if (StringUtils.isBlank(advPayment.getiFSC())
							&& (StringUtils.isBlank(advPayment.getBranchBankCode()) || StringUtils
									.isBlank(advPayment.getBranchCode()))) {
						String[] valueParm = new String[2];
						valueParm[0] = "Ifsc";
						valueParm[1] = "Bank/Branch code";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90215", valueParm)));
					}
					if (StringUtils.isNotBlank(advPayment.getiFSC())) {
						BankBranch bankBranch = bankBranchService.getBankBrachByIFSC(advPayment.getiFSC());
						if (bankBranch == null) {
							String[] valueParm = new String[1];
							valueParm[0] = advPayment.getiFSC();
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90301", valueParm)));
						}
					}
					if (StringUtils.isNotBlank(advPayment.getBranchBankCode())
							&& StringUtils.isNotBlank(advPayment.getBranchCode())) {
						BankBranch bankBranch = bankBranchService.getBankBrachByCode(advPayment.getBranchBankCode(), advPayment.getBranchCode());
						if (bankBranch == null) {
							String[] valueParm = new String[2];
							valueParm[0] = advPayment.getBranchBankCode();
							valueParm[1] = advPayment.getBranchCode();
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90302", valueParm)));
						}
					}

					// Account number
					if (StringUtils.isBlank(advPayment.getBeneficiaryAccNo())) {
						String[] valueParm = new String[2];
						valueParm[0] = "accountNo";
						valueParm[1] = advPayment.getBeneficiaryAccNo();
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90217", valueParm)));
					}

					// Account holder name
					if (StringUtils.isBlank(advPayment.getBeneficiaryName())) {
						String[] valueParm = new String[2];
						valueParm[0] = "acHolderName";
						valueParm[1] = advPayment.getBeneficiaryName();
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90217", valueParm)));
					}

					// phone country code
					/*						if (StringUtils.equals(advPayment.getPaymentType(), RepayConstants.PAYMENT_TYPE_IMPS)
										&& StringUtils.isBlank(advPayment.getPhoneCountryCode())) {
									String[] valueParm = new String[2];
									valueParm[0] = "phoneCountryCode";
									valueParm[1] = advPayment.getPaymentType();
									return getErrorDetails("90217", valueParm);
								}*/

					if (StringUtils.isNotBlank(advPayment.getPhoneCountryCode())
							&& StringUtils.isBlank(advPayment.getPhoneAreaCode())) {
						String[] valueParm = new String[1];
						valueParm[0] = "phoneAreaCode";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90214", valueParm)));
					}

					if (StringUtils.isNotBlank(advPayment.getPhoneCountryCode())
							&& StringUtils.isBlank(advPayment.getPhoneNumber())) {
						String[] valueParm = new String[1];
						valueParm[0] = "phoneNumber";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90214", valueParm)));
					}
				}
			}
		}
		return errorDetails;
	}

	private List<ErrorDetails> nonFinanceValidation(String vldGroup, FinScheduleData finScheduleData, boolean isAPICall) {
		// Re-Initialize Error Details
		List<ErrorDetails> errorDetails = new ArrayList<ErrorDetails>();
		FinanceMain finMain = finScheduleData.getFinanceMain();
		boolean isCreateLoan = false;

		if (StringUtils.equals(vldGroup, PennantConstants.VLD_CRT_LOAN)) {
			isCreateLoan = true;
		}

		// Validate customer
		if (isCreateLoan || StringUtils.isNotBlank(finMain.getLovDescCustCIF())) {
			Customer customer = customerDAO.getCustomerByCIF(finMain.getLovDescCustCIF(), "");
			if (customer == null) {
				String[] valueParm = new String[1];
				valueParm[0] = finMain.getLovDescCustCIF();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90101", valueParm)));
			}
		}

		//Validate Finance Type
		FinanceType financeType = financeTypeDAO.getFinanceTypeByID(finMain.getFinType(), "");
		if (financeType == null) {
			String[] valueParm = new String[1];
			valueParm[0] = finMain.getFinType();
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90202", valueParm)));
		} else {
			finScheduleData.setFinanceType(financeType);
		}

		//Validate Finance Currency
		Currency currency = CurrencyUtil.getCurrencyObject(finMain.getFinCcy());
		if (currency == null) {
			String[] valueParm = new String[1];
			valueParm[0] = finMain.getFinCcy();
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90120", valueParm)));
		}

		// validate finance branch
		if (isCreateLoan || StringUtils.isNotBlank(finMain.getFinBranch())) {
			Branch branch = branchDAO.getBranchById(finMain.getFinBranch(), "");
			if (branch == null) {
				String[] valueParm = new String[1];
				valueParm[0] = finMain.getFinBranch();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90129", valueParm)));
			}
		}

		//Validate Repayment Method
		//TODO: To be confirmed from where it should be taken? PennantStaticListUtil.getRepayMethods() OR MandateConstants or FinanceConstants??
		if (isCreateLoan) {
			String repayMethod = finMain.getFinRepayMethod();

			// finRepay method
			if(StringUtils.isNotBlank(repayMethod)) {
				List<ValueLabel> repayMethods = PennantStaticListUtil.getRepayMethods();
				boolean repayMehodSts = false;
				for (ValueLabel value : repayMethods) {
					if (StringUtils.equals(value.getValue(), repayMethod)) {
						repayMehodSts = true;
						break;
					}
				}
				if (!repayMehodSts) {
					String[] valueParm = new String[1];
					valueParm[0] = repayMethod;
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90307", valueParm)));
				}
			}
		}

		return errorDetails;
	}

	/*
	 * ================================================================================================================
	 * VALIDATE BASIC FINANCE DATA
	 * ================================================================================================================
	 */

	private List<ErrorDetails> basicValidation(String vldGroup, FinScheduleData finScheduleData, boolean isAPICall) {
		List<ErrorDetails> errorDetails = finScheduleData.getErrorDetails();
		FinanceMain finMain = finScheduleData.getFinanceMain();
		FinanceType financeType = finScheduleData.getFinanceType();
		BigDecimal zeroAmount = BigDecimal.ZERO;

		// Finance start date
		Date appDate = DateUtility.getAppDate();
		Date minReqFinStartDate = DateUtility.addDays(appDate, -SysParamUtil.getValueAsInt("BACKDAYS_STARTDATE"));
		if (finMain.getFinStartDate().compareTo(minReqFinStartDate) < 0) {
			String[] valueParm = new String[2];
			valueParm[0] = SysParamUtil.getValueAsString("BACKDAYS_STARTDATE");
			valueParm[1] = DateUtility.formatDate(minReqFinStartDate, PennantConstants.XMLDateFormat);
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90134", valueParm)));
		}

		String IDB = finMain.getProfitDaysBasis();
		//Validate Interest Days Basis
		if (!StringUtils.equals(IDB, CalculationConstants.IDB_30E360)
				&& !StringUtils.equals(IDB, CalculationConstants.IDB_30E360I)
				&& !StringUtils.equals(IDB, CalculationConstants.IDB_30EP360)
				&& !StringUtils.equals(IDB, CalculationConstants.IDB_30U360)
				&& !StringUtils.equals(IDB, CalculationConstants.IDB_ACT_360)
				&& !StringUtils.equals(IDB, CalculationConstants.IDB_ACT_365FIXED)
				&& !StringUtils.equals(IDB, CalculationConstants.IDB_ACT_365LEAP)
				&& !StringUtils.equals(IDB, CalculationConstants.IDB_ACT_365LEAPS)
				&& !StringUtils.equals(IDB, CalculationConstants.IDB_ACT_ISDA)) {

			String[] valueParm = new String[1];
			valueParm[0] = IDB;
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90209", valueParm)));
		}

		//Loan Amount Validation
		if (finMain.getFinAmount().compareTo(zeroAmount) <= 0) {
			String[] valueParm = new String[1];
			valueParm[0] = String.valueOf(finMain.getFinAmount().doubleValue());
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90127", valueParm)));
		}

		//finAssetValue
		if (finMain.getFinAssetValue().compareTo(zeroAmount) > 0) {
			if (finMain.getFinAmount().compareTo(finMain.getFinAssetValue()) > 0) {
				String[] valueParm = new String[4];
				valueParm[0] = "finAmount";
				valueParm[1] = "finAssetValue";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90220", valueParm)));
			}
		}

		finMain.setDownPayment(finMain.getDownPayBank().add(finMain.getDownPaySupl()));

		// DownPayBank
		if (finMain.getDownPayBank().compareTo(zeroAmount) != 0 || finMain.getDownPaySupl().compareTo(zeroAmount) != 0) {
			if (!financeType.isFinIsDwPayRequired()) {
				String[] valueParm = new String[3];
				valueParm[0] = "Down pay bank";
				valueParm[1] = "Supplier";
				valueParm[2] = finMain.getFinType();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90203", valueParm)));
			} else {
				//Downpayment to Bank
				if (finMain.getDownPayBank().compareTo(BigDecimal.ZERO) < 0) {
					String[] valueParm = new String[1];
					valueParm[0] = String.valueOf(finMain.getDownPayBank().doubleValue());
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90130", valueParm)));
				}

				//Downpayment to Supplier
				if (finMain.getDownPaySupl().compareTo(BigDecimal.ZERO) < 0) {
					String[] valueParm = new String[1];
					valueParm[0] = String.valueOf(finMain.getDownPaySupl().doubleValue());
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90131", valueParm)));
				}

			}
		}

		//RETURN IF ANY ERROR AFTER VERY BASIC VALIDATION
		if (!errorDetails.isEmpty()) {
			return errorDetails;
		}

		//Allow Manual Schedule
		if (finMain.isManualSchedule()) {
			errorDetails = manualScheduleValidation(vldGroup, finScheduleData);
			if (errorDetails != null) {
				return errorDetails;
			}
		}

		//Planned Deferments
		if (finMain.getPlanDeferCount() > 0) {
			errorDetails = planDefermentValidation(vldGroup, finScheduleData);
			if (errorDetails != null) {
				return errorDetails;
			}
		}

		//Step Loan?
		errorDetails = stepLoanValidation(vldGroup, finScheduleData);
		if (errorDetails != null) {
			return errorDetails;
		}

		//Planned EMI Holidays
		if (finMain.isStepFinance()) {
			errorDetails = planEMIHolidayValidation(vldGroup, finScheduleData);
			if (errorDetails != null) {
				return errorDetails;
			}
		}
		return errorDetails;
	}

	/*
	 * ================================================================================================================
	 * VALIDATE FINANCE GRACE DETAILS
	 * ================================================================================================================
	 */

	private List<ErrorDetails> graceValidation(String vldGroup, FinScheduleData finScheduleData, boolean isAPICall) {
		List<ErrorDetails> errorDetails = finScheduleData.getErrorDetails();
		FinanceMain finMain = finScheduleData.getFinanceMain();
		FinanceType financeType = finScheduleData.getFinanceType();

		//Allow Grace?
		if (!financeType.isFInIsAlwGrace()) {
			String[] valueParm = new String[1];
			valueParm[0] = finMain.getFinType();
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90135", valueParm)));
			return errorDetails;
		}

		//Grace Terms & Grace End Date are Mutually Exclusive
		if (finMain.getGraceTerms() > 0 && finMain.getGrcPeriodEndDate() != null) {
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90152", null)));
			return errorDetails;
		}

		//Both Grace Terms & Grace End Date are not present
		if (finMain.getGraceTerms() == 0 && finMain.getGrcPeriodEndDate() == null) {
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90184", null)));
			return errorDetails;
		}

		//Validate Profit Details
		errorDetails = gracePftFrqValidation(finScheduleData);
		if (!errorDetails.isEmpty()) {
			return errorDetails;
		}

		//Grace Rate Validations
		errorDetails = graceRateValidation(finScheduleData);

		String IDB = finMain.getGrcProfitDaysBasis();
		//Validate Interest Days Basis
		if (!StringUtils.equals(IDB, CalculationConstants.IDB_30E360)
				&& !StringUtils.equals(IDB, CalculationConstants.IDB_30E360I)
				&& !StringUtils.equals(IDB, CalculationConstants.IDB_30EP360)
				&& !StringUtils.equals(IDB, CalculationConstants.IDB_30U360)
				&& !StringUtils.equals(IDB, CalculationConstants.IDB_ACT_360)
				&& !StringUtils.equals(IDB, CalculationConstants.IDB_ACT_365FIXED)
				&& !StringUtils.equals(IDB, CalculationConstants.IDB_ACT_365LEAP)
				&& !StringUtils.equals(IDB, CalculationConstants.IDB_ACT_365LEAPS)
				&& !StringUtils.equals(IDB, CalculationConstants.IDB_ACT_ISDA)) {

			String[] valueParm = new String[1];
			valueParm[0] = IDB;
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90209", valueParm)));
		}

		//Validate Review Details
		errorDetails = gracePftReviewValidation(finScheduleData);

		//Validate Capitalization Details
		errorDetails = gracePftCpzValidation(finScheduleData);

		//Validate Grace Payment and Methods
		errorDetails = graceSchdValidation(finScheduleData);

		//Validate Grace Advised Rates
		errorDetails = graceAdvRateValidation(finScheduleData);

		//Validate Grace Dates
		errorDetails = graceDatesValidation(finScheduleData);

		return errorDetails;
	}

	/*
	 * ================================================================================================================
	 * VALIDATE FINANCE REPAY DETAILS
	 * ================================================================================================================
	 */

	private List<ErrorDetails> repayValidation(String vldGroup, FinScheduleData finScheduleData, boolean isAPICall) {
		List<ErrorDetails> errorDetails = finScheduleData.getErrorDetails();
		FinanceMain finMain = finScheduleData.getFinanceMain();
		FinanceType financeType = finScheduleData.getFinanceType();

		//Number of Terms & Maturity Date are Mutually Exclusive
		if (finMain.getNumberOfTerms() > 0 && finMain.getMaturityDate() != null) {
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90190", null)));
			return errorDetails;
		}

		//Both Grace Terms & Grace End Date are not present
		if (finMain.getNumberOfTerms() == 0 && finMain.getMaturityDate() == null) {
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90191", null)));
			return errorDetails;
		}

		//Repay Rate Validations
		errorDetails = repayRateValidation(finScheduleData);

		//Repayment Schedule Method (If not blanks validation already happens in defaulting)
		if (!StringUtils.equals(finMain.getScheduleMethod(), CalculationConstants.SCHMTHD_NOPAY)
				&& !StringUtils.equals(finMain.getScheduleMethod(), CalculationConstants.SCHMTHD_EQUAL)
				&& !StringUtils.equals(finMain.getScheduleMethod(), CalculationConstants.SCHMTHD_PFT)
				&& !StringUtils.equals(finMain.getScheduleMethod(), CalculationConstants.SCHMTHD_PRI)
				&& !StringUtils.equals(finMain.getScheduleMethod(), CalculationConstants.SCHMTHD_PRI_PFT)) {
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90189", null)));
		}

		//Validate Repayment Details
		errorDetails = repayFrqValidation(finScheduleData);
		if (errorDetails != null) {
			return errorDetails;
		}

		//Validate Profit Details
		errorDetails = repayPftFrqValidation(finScheduleData);

		//Validate Review Details
		errorDetails = repayPftReviewValidation(finScheduleData);

		//Validate Capitalization Details
		errorDetails = repayPftCpzValidation(finScheduleData);

		//Pay on interest frequency
		if (finMain.isFinRepayPftOnFrq() && !financeType.isFinRepayPftOnFrq()) {
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90227", null)));
		}

		//Validate Advised Rates
		errorDetails = repayAdvRateValidation(finScheduleData);

		//Validate BPI
		errorDetails = bpiValidation(finScheduleData);

		return errorDetails;
	}

	/*
	 * _______________________________________________________________________________________________________________
	 * MANUAL SCHEDULE
	 * _______________________________________________________________________________________________________________
	 */

	private List<ErrorDetails> manualScheduleValidation(String vldGroup, FinScheduleData finScheduleData) {
		List<ErrorDetails> errorDetails = finScheduleData.getErrorDetails();
		FinanceMain finMain = finScheduleData.getFinanceMain();
		FinanceType financeType = finScheduleData.getFinanceType();

		//Finance Type allow Manual Schedule?
		if (!financeType.isManualSchedule()) {
			String[] valueParm = new String[1];
			valueParm[0] = finMain.getFinType();
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90138", valueParm)));
		}

		//Manual Schedule with Grace
		if (!finMain.isAllowGrcPeriod()) {
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90186", null)));
		}

		//Planned Deferment Requested
		if (finMain.getPlanDeferCount() > 0) {
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90140", null)));
		}

		//Finance Type allow Step?
		if (!finMain.isStepFinance()) {
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90144", null)));
		}

		//Planned EMI Holiday Requested
		if (finMain.isPlanEMIHAlw()) {
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90149", null)));
		}

		return errorDetails;
	}

	/*
	 * _______________________________________________________________________________________________________________
	 * PLANNED DEFERMENTS
	 * _______________________________________________________________________________________________________________
	 */

	private List<ErrorDetails> planDefermentValidation(String vldGroup, FinScheduleData finScheduleData) {
		List<ErrorDetails> errorDetails = finScheduleData.getErrorDetails();
		FinanceMain finMain = finScheduleData.getFinanceMain();
		FinanceType financeType = finScheduleData.getFinanceType();

		//Finance Type allow Planned Deferments?
		if (!financeType.isAlwPlanDeferment()) {
			String[] valueParm = new String[1];
			valueParm[0] = finMain.getFinType();
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90139", valueParm)));
		}

		//Repay Rate Basis not Flat Converting to Reducing
		if (!StringUtils.equals(finMain.getRepayRateBasis(), CalculationConstants.RATE_BASIS_C)) {
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90151", null)));
		}

		//Requested more deferments than defined in finance type?
		if (finMain.getPlanDeferCount() > financeType.getPlanDeferCount()) {
			String[] valueParm = new String[3];
			valueParm[0] = Integer.toString(finMain.getPlanDeferCount());
			valueParm[1] = Integer.toString(financeType.getPlanDeferCount());
			valueParm[2] = finMain.getFinType();
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90141", valueParm)));
		}

		//Planned EMI Holidays also requested?
		if (finMain.isPlanEMIHAlw()) {
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90142", null)));
		}

		//Stepping also requested?
		if (finMain.isStepFinance()) {
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90143", null)));
		}

		return errorDetails;
	}

	/*
	 * _______________________________________________________________________________________________________________
	 * STEP LOAN
	 * _______________________________________________________________________________________________________________
	 */

	private List<ErrorDetails> stepLoanValidation(String vldGroup, FinScheduleData finScheduleData) {
		List<ErrorDetails> errorDetails = finScheduleData.getErrorDetails();
		FinanceMain finMain = finScheduleData.getFinanceMain();
		FinanceType financeType = finScheduleData.getFinanceType();

		if (!finMain.isStepFinance()) {
			if (finMain.isAlwManualSteps() || StringUtils.isNotBlank(finMain.getStepPolicy())
					|| StringUtils.isNotBlank(finMain.getStepType())) {
				String[] valueParm = new String[1];
				valueParm[0] = finMain.getFinType();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90163", valueParm)));
				return errorDetails;
			}
		}

		//Finance Type allow Step?
		if (finMain.isStepFinance() && !financeType.isStepFinance()) {
			String[] valueParm = new String[1];
			valueParm[0] = finMain.getFinType();
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("91129", valueParm)));
		} else if(!finMain.isStepFinance() && financeType.isSteppingMandatory()) {
			String[] valueParm = new String[1];
			valueParm[0] = finMain.getFinType();
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("91128", valueParm)));
		}

		//Planned EMI Holidays also requested?
		if (finMain.isPlanEMIHAlw()) {
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90150", null)));
		}

		//Manual Steps Requested?
		if (finMain.isAlwManualSteps()) {
			//Allow Manual Step?
			if (!financeType.isAlwManualSteps()) {
				String[] valueParm = new String[1];
				valueParm[0] = finMain.getFinType();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90145", valueParm)));
			}

			if(StringUtils.isNotBlank(finMain.getStepPolicy())) {
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90237", null)));
			}

			//Step Type
			if (!StringUtils.equals(finMain.getStepType(), FinanceConstants.STEPTYPE_EMI)
					&& !StringUtils.equals(finMain.getStepType(), FinanceConstants.STEPTYPE_PRIBAL)) {
				String[] valueParm = new String[2];
				valueParm[0] = new StringBuilder(10).append(FinanceConstants.STEPTYPE_EMI).append(" & ")
						.append(FinanceConstants.STEPTYPE_PRIBAL).toString();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90148", valueParm)));
			}

			// step aggregate
			if (finScheduleData.getStepPolicyDetails() == null || finScheduleData.getStepPolicyDetails().isEmpty()) {
				String[] valueParm = new String[1];
				valueParm[0] = "step";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90502", valueParm)));
			}

		} else if(finMain.isStepFinance()){
			//Step Policy requested
			if (StringUtils.isBlank(finMain.getStepPolicy())) {
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90146", null)));
			} else {
				if (!StringUtils.containsIgnoreCase(financeType.getAlwdStepPolicies(), finMain.getStepPolicy())) {
					String[] valueParm = new String[2];
					valueParm[0] = finMain.getFinType();
					valueParm[1] = financeType.getAlwdStepPolicies();
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90147", valueParm)));
				}
				
				// Validate stepPolicy code
				StepPolicyHeader stepHeader = stepPolicyService.getApprovedStepPolicyHeaderById(finMain.getStepPolicy());
				if(stepHeader == null) {
					String[] valueParm = new String[1];
					valueParm[0] = finMain.getStepPolicy();
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90252", valueParm)));
				}
			}
		}

		return errorDetails;
	}

	/*
	 * _______________________________________________________________________________________________________________
	 * PLAN EMI HOLIDAY
	 * _______________________________________________________________________________________________________________
	 */

	private List<ErrorDetails> planEMIHolidayValidation(String vldGroup, FinScheduleData finScheduleData) {
		List<ErrorDetails> errorDetails = finScheduleData.getErrorDetails();
		FinanceMain finMain = finScheduleData.getFinanceMain();
		FinanceType financeType = finScheduleData.getFinanceType();

		if(!financeType.isPlanEMIHAlw() && finMain.isPlanEMIHAlw()) {
			String[] valueParm = new String[2];
			valueParm[0] = "Planned EMI Holiday(planEMIHAlw)";
			valueParm[1] = finMain.getFinType();
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90204", valueParm)));
			return errorDetails;
		}

		if(finMain.isPlanEMIHAlw()) {
			// planEMIHMethod
			if(!StringUtils.equals(finMain.getPlanEMIHMethod(), FinanceConstants.PLANEMIHMETHOD_FRQ)
					&& !StringUtils.equals(finMain.getPlanEMIHMethod(), FinanceConstants.PLANEMIHMETHOD_ADHOC)) {
				String[] valueParm = new String[1];
				valueParm[0] = FinanceConstants.PLANEMIHMETHOD_FRQ +","+ FinanceConstants.PLANEMIHMETHOD_ADHOC ;
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90238", valueParm)));
				return errorDetails;
			}
			// planEMIHMaxPerYear
			if(finMain.getPlanEMIHMaxPerYear() > 11) {
				String[] valueParm = new String[1];
				valueParm[0] = String.valueOf(11);
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90239", valueParm)));
				return errorDetails;
			}
			// planEMIHMax
			if(finMain.getPlanEMIHMax() > (finMain.getNumberOfTerms() - 1)) {
				String[] valueParm = new String[1];
				valueParm[0] = String.valueOf(finMain.getNumberOfTerms() - 1);
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90240", valueParm)));
				return errorDetails;
			}
		} else if(StringUtils.isNotBlank(finMain.getPlanEMIHMethod()) || finMain.getPlanEMIHMaxPerYear() > 0
				|| finMain.getPlanEMIHMax() > 0 || finMain.getPlanEMIHLockPeriod() > 0
				|| finMain.isPlanEMICpz()) {
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90241", null)));
			return errorDetails;
		}

		return errorDetails;
	}

	/*
	 * _______________________________________________________________________________________________________________
	 * GRACE RATE
	 * _______________________________________________________________________________________________________________
	 */

	private List<ErrorDetails> graceRateValidation(FinScheduleData finScheduleData) {
		List<ErrorDetails> errorDetails = finScheduleData.getErrorDetails();
		FinanceMain finMain = finScheduleData.getFinanceMain();
		FinanceType financeType = finScheduleData.getFinanceType();
		BigDecimal zeroValue = BigDecimal.ZERO;

		//Rate Type/Rate Basis
		if (!StringUtils.equals(finMain.getGrcRateBasis(), CalculationConstants.RATE_BASIS_F)
				&& !StringUtils.equals(finMain.getGrcRateBasis(), CalculationConstants.RATE_BASIS_R)) {
			String[] valueParm = new String[1];
			valueParm[0] = finMain.getGrcRateBasis();
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90211", valueParm)));
		}

		//Actual Rate
		if (finMain.getGrcPftRate().compareTo(zeroValue) < 0) {
			String[] valueParm = new String[1];
			valueParm[0] = "Grace";
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90153", valueParm)));
		}

		//Both Grace and Base Rates found
		if (StringUtils.isNotBlank(finMain.getGraceBaseRate()) && finMain.getGrcPftRate().compareTo(zeroValue) != 0) {
			String[] valueParm = new String[1];
			valueParm[0] = "Grace";
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90154", valueParm)));
		}

		//Base Rate requested?
		if(StringUtils.isBlank(finMain.getGraceBaseRate()) && StringUtils.isNotBlank(financeType.getFinGrcBaseRate())) {
			String[] valueParm = new String[2];
			valueParm[0] = "Grace";
			valueParm[1] = finMain.getFinType();
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90232", valueParm)));
		}

		if (StringUtils.isNotBlank(finMain.getGraceBaseRate())) {
			//Allow Base Rate?
			if (StringUtils.isBlank(financeType.getFinGrcBaseRate())) {
				String[] valueParm = new String[2];
				valueParm[0] = "Grace";
				valueParm[1] = finMain.getFinType();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90136", valueParm)));
			} else {
				//Base Rate code found?
				String brCode = finMain.getGraceBaseRate();
				String currency = finMain.getFinCcy();//TODO: discuss with pradeep
				currency = financeType.getFinCcy();

				int rcdCount = baseRateDAO.getBaseRateCountById(brCode, currency, "");
				if (rcdCount <= 0) {
					String[] valueParm = new String[2];
					valueParm[0] = brCode;
					valueParm[1] = finMain.getFinCcy();
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90137", valueParm)));
				}

				// validate special rate code
				if(StringUtils.isNotBlank(finMain.getGraceSpecialRate())) {
					rcdCount = splRateDAO.getSpecialRateCountById(finMain.getGraceSpecialRate(), "");
					if (rcdCount <= 0) {
						String[] valueParm = new String[2];
						valueParm[0] = finMain.getGraceSpecialRate();
						valueParm[1] = "Grace";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90230", valueParm)));
					}
				}
			}
		}  else if(finMain.getGrcPftRate().compareTo(BigDecimal.ZERO) > 0 &&
				StringUtils.isNotBlank(financeType.getFinGrcBaseRate())) {
			String[] valueParm = new String[2];
			valueParm[0] = "Grace Profit Rate";
			valueParm[1] = finMain.getFinType();
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90204", valueParm)));
		}

		//Special Rate code
		if (StringUtils.isNotBlank(finMain.getGraceSpecialRate()) && StringUtils.isBlank(finMain.getGraceBaseRate())) {
			String[] valueParm = new String[1];
			valueParm[0] = "Grace";
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90155", valueParm)));
		}

		//Margin
		if (finMain.getGrcMargin().compareTo(zeroValue) != 0 && StringUtils.isBlank(finMain.getGraceBaseRate())) {
			String[] valueParm = new String[1];
			valueParm[0] = "Grace";
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90155", valueParm)));
		}

		if (!errorDetails.isEmpty()) {
			return errorDetails;
		}

		//Validate Against Minimum and Maximum Rate
		if (finMain.getGrcMinRate().compareTo(zeroValue) == 0 && finMain.getGrcMaxRate().compareTo(zeroValue) == 0) {
			//No further validation required
			return errorDetails;
		}

		BigDecimal netRate = BigDecimal.ZERO;
		//Base Rate
		if (StringUtils.isNotBlank(finMain.getGraceBaseRate())) {
			RateDetail rate = new RateDetail();
			rate.setBaseRateCode(finMain.getGraceBaseRate());
			rate.setCurrency(finMain.getFinCcy());
			rate.setSplRateCode(finMain.getGraceSpecialRate());
			rate.setMargin(finMain.getGrcMargin());
			rate.setValueDate(finMain.getFinStartDate());
			rate = RateUtil.getRefRate(rate);
			netRate = rate.getNetRefRateLoan();
		} else {
			netRate = finMain.getGrcPftRate();
		}

		//Check Against Minimum Rate 
		if (netRate.compareTo(finMain.getGrcMinRate()) < 0) {
			String[] valueParm = new String[2];
			valueParm[0] = round4(netRate).toString();
			valueParm[1] = round4(finMain.getGrcMinRate()).toString();
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90172", valueParm)));
		}

		//Check Against Maximum Rate 
		if (finMain.getGrcMaxRate().compareTo(zeroValue) != 0) {
			if (netRate.compareTo(finMain.getGrcMaxRate()) > 0) {
				String[] valueParm = new String[2];
				valueParm[0] = round4(netRate).toString();
				valueParm[1] = round4(finMain.getGrcMinRate()).toString();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90173", valueParm)));
			}
		}

		return errorDetails;

	}

	/*
	 * _______________________________________________________________________________________________________________
	 * VALIDATE GRACE PROFIT FREQUENCY AND NEXT PROFIT DATE
	 * _______________________________________________________________________________________________________________
	 */

	private List<ErrorDetails> gracePftFrqValidation(FinScheduleData finScheduleData) {
		List<ErrorDetails> errorDetails = finScheduleData.getErrorDetails();
		FinanceMain finMain = finScheduleData.getFinanceMain();

		//Validate Profit Frequency
		ErrorDetails tempError = FrequencyUtil.validateFrequency(finMain.getGrcPftFrq());
		if (tempError != null) {
			String[] valueParm = new String[2];
			valueParm[0] = "Grace";
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90156", valueParm)));
		}

		//First Interest Frequency Date Vs Start Date
		if (finMain.getNextGrcPftDate().compareTo(finMain.getFinStartDate()) <= 0) {
			String[] valueParm = new String[2];
			valueParm[0] = DateUtility.formatToShortDate(finMain.getNextGrcPftDate());
			valueParm[1] = DateUtility.formatToShortDate(finMain.getFinStartDate());
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90162", valueParm)));
		}

		//Return if any error
		if (!errorDetails.isEmpty()) {
			return errorDetails;
		}

		//Default Calculated Grace End Date using terms
		if (finMain.getGraceTerms() > 0) {
			finMain.setCalGrcTerms(finMain.getGraceTerms());
			List<Calendar> scheduleDateList = FrequencyUtil.getNextDate(finMain.getGrcPftFrq(), finMain.getGraceTerms(),
					finMain.getNextGrcPftDate(), HolidayHandlerTypes.MOVE_NONE, true).getScheduleList();

			Date geDate = null;
			if (scheduleDateList != null) {
				Calendar calendar = scheduleDateList.get(scheduleDateList.size() - 1);
				geDate = DateUtility.getDBDate(DateUtility.formatDate(calendar.getTime(), PennantConstants.DBDateFormat));
			}

			finMain.setCalGrcEndDate(geDate);
		} else {
			finMain.setCalGrcEndDate(finMain.getGrcPeriodEndDate());
			int terms = FrequencyUtil.getTerms(finMain.getGrcPftFrq(), finMain.getNextGrcPftDate(),
					finMain.getGrcPeriodEndDate(), true, true).getTerms();
			finMain.setCalGrcTerms(terms);
		}

		//First Interest Frequency Date Vs Grace Period End Date
		if (finMain.getNextGrcPftDate().compareTo(finMain.getCalGrcEndDate()) > 0) {
			String[] valueParm = new String[2];
			valueParm[0] = DateUtility.formatToShortDate(finMain.getNextGrcPftRvwDate());
			valueParm[1] = DateUtility.formatToShortDate(finMain.getCalGrcEndDate());
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90161", valueParm)));
		}

		return errorDetails;

	}

	/*
	 * _______________________________________________________________________________________________________________
	 * GRACE PROFIT REVIEW
	 * _______________________________________________________________________________________________________________
	 */

	private List<ErrorDetails> gracePftReviewValidation(FinScheduleData finScheduleData) {
		List<ErrorDetails> errorDetails = finScheduleData.getErrorDetails();
		FinanceMain finMain = finScheduleData.getFinanceMain();
		FinanceType financeType = finScheduleData.getFinanceType();

		//Allow Profit Rate Review
		if (!financeType.isFinGrcIsRvwAlw()) {
			if (finMain.isAllowGrcPftRvw() || StringUtils.isNotBlank(finMain.getGrcPftRvwFrq())
					|| finMain.getNextGrcPftRvwDate() != null) {
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90164", null)));
				return errorDetails;
			} else {
				return errorDetails;
			}
		}

		//Validate Profit Review Frequency
		ErrorDetails tempError = FrequencyUtil.validateFrequency(finMain.getGrcPftRvwFrq());
		if (tempError != null) {
			String[] valueParm = new String[2];
			valueParm[0] = "Grace";
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90157", valueParm)));
		}

		//First Interest Review Frequency Date Vs Start Date
		if (finMain.getNextGrcPftRvwDate().compareTo(finMain.getFinStartDate()) <= 0) {
			String[] valueParm = new String[2];
			valueParm[0] = DateUtility.formatToShortDate(finMain.getNextGrcPftRvwDate());
			valueParm[1] = DateUtility.formatToShortDate(finMain.getFinStartDate());
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90166", valueParm)));
		}

		//First Interest Review Frequency Date Vs Grace Period End Date
		if (finMain.getNextGrcPftRvwDate().compareTo(finMain.getCalGrcEndDate()) > 0) {
			String[] valueParm = new String[2];
			valueParm[0] = DateUtility.formatToShortDate(finMain.getNextGrcPftRvwDate());
			valueParm[1] = DateUtility.formatToShortDate(finMain.getCalGrcEndDate());
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90165", valueParm)));
		}

		return errorDetails;
	}

	/*
	 * _______________________________________________________________________________________________________________
	 * GRACE PROFIT CAPITALIZATION
	 * _______________________________________________________________________________________________________________
	 */

	private List<ErrorDetails> gracePftCpzValidation(FinScheduleData finScheduleData) {
		List<ErrorDetails> errorDetails = finScheduleData.getErrorDetails();
		FinanceMain finMain = finScheduleData.getFinanceMain();
		FinanceType financeType = finScheduleData.getFinanceType();

		//Allow Profit Capitalization
		if (!financeType.isFinGrcIsIntCpz()) {
			if (finMain.isAllowGrcCpz() || StringUtils.isNotBlank(finMain.getGrcCpzFrq())
					|| finMain.getNextGrcCpzDate() != null) {
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90167", null)));
				return errorDetails;
			} else {
				return errorDetails;
			}
		}

		//Validate Profit Capitalization Frequency
		ErrorDetails tempError = FrequencyUtil.validateFrequency(finMain.getGrcCpzFrq());
		if (tempError != null) {
			String[] valueParm = new String[2];
			valueParm[0] = "Grace";
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90158", valueParm)));
		}

		//First Interest Capitalization Frequency Date Vs Start Date
		if (finMain.getNextGrcCpzDate().compareTo(finMain.getFinStartDate()) <= 0) {
			String[] valueParm = new String[2];
			valueParm[0] = DateUtility.formatToShortDate(finMain.getNextGrcCpzDate());
			valueParm[1] = DateUtility.formatToShortDate(finMain.getFinStartDate());
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90168", valueParm)));
		}

		//First Interest Capitalization Frequency Date Vs Grace End Date
		if (finMain.getNextGrcCpzDate().compareTo(finMain.getCalGrcEndDate()) > 0) {
			String[] valueParm = new String[2];
			valueParm[0] = DateUtility.formatToShortDate(finMain.getNextGrcCpzDate());
			valueParm[1] = DateUtility.formatToShortDate(finMain.getCalGrcEndDate());
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90169", valueParm)));
		}

		return errorDetails;
	}

	/*
	 * _______________________________________________________________________________________________________________
	 * GRACE SCHEDULE VALIDATION
	 * _______________________________________________________________________________________________________________
	 */

	private List<ErrorDetails> graceSchdValidation(FinScheduleData finScheduleData) {
		List<ErrorDetails> errorDetails = finScheduleData.getErrorDetails();
		FinanceMain finMain = finScheduleData.getFinanceMain();
		FinanceType financeType = finScheduleData.getFinanceType();

		if (finMain.isAllowGrcRepay()) {
			if (!financeType.isFinIsAlwGrcRepay()) {
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90170", null)));
			} else {
				if (!StringUtils.equals(finMain.getGrcSchdMthd(), CalculationConstants.SCHMTHD_NOPAY)
						&& !StringUtils.equals(finMain.getGrcSchdMthd(), CalculationConstants.SCHMTHD_PFT)
						&& !StringUtils.equals(finMain.getGrcSchdMthd(), CalculationConstants.SCHMTHD_GRCENDPAY)) {
					String[] valueParm = new String[2];
					valueParm[0] = "Grace";
					valueParm[1] = finMain.getGrcSchdMthd();
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90210", null)));
				}
			}
		} else {
			if (StringUtils.isNotBlank(finMain.getGrcSchdMthd())) {
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90171", null)));
			}
		}

		return errorDetails;
	}

	/*
	 * _______________________________________________________________________________________________________________
	 * ADVISED GRACE RATE
	 * _______________________________________________________________________________________________________________
	 */

	private List<ErrorDetails> graceAdvRateValidation(FinScheduleData finScheduleData) {
		List<ErrorDetails> errorDetails = finScheduleData.getErrorDetails();
		FinanceMain finMain = finScheduleData.getFinanceMain();
		FinanceType financeType = finScheduleData.getFinanceType();
		BigDecimal zeroValue = BigDecimal.ZERO;

		if (!StringUtils.equals(finMain.getProductCategory(), FinanceConstants.PRODUCT_STRUCTMUR)) {
			if (finMain.getGrcAdvPftRate().compareTo(zeroValue) != 0
					|| StringUtils.isNotBlank(finMain.getGrcAdvBaseRate())
					|| finMain.getGrcAdvMargin().compareTo(zeroValue) != 0) {
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90176", null)));
			}
		}

		//Actual Rate
		if (finMain.getGrcAdvPftRate().compareTo(zeroValue) < 0) {
			String[] valueParm = new String[1];
			valueParm[0] = "Grace Advice";
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90153", valueParm)));
		}

		//Both Grace and Base Rates found
		if (StringUtils.isNotBlank(finMain.getGrcAdvBaseRate())) {
			if (finMain.getGrcAdvPftRate().compareTo(zeroValue) != 0) {
				String[] valueParm = new String[1];
				valueParm[0] = "Grace Advise";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90154", valueParm)));
			}
		}

		//Base Rate requested?
		if (StringUtils.isNotBlank(finMain.getGrcAdvBaseRate())) {
			//Allow Base Rate?
			if (StringUtils.isBlank(financeType.getGrcAdvBaseRate())) {
				String[] valueParm = new String[2];
				valueParm[0] = "Grace Advised";
				valueParm[1] = finMain.getFinType();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90136", valueParm)));
			} else {
				//Base Rate code found?
				String brCode = finMain.getGrcAdvBaseRate();
				String currency = finMain.getFinCcy();

				int rcdCount = baseRateDAO.getBaseRateCountById(brCode, currency, "");
				if (rcdCount <= 0) {
					String[] valueParm = new String[2];
					valueParm[0] = brCode;
					valueParm[1] = finMain.getFinCcy();
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90137", valueParm)));
				}
			}
		}

		//Margin
		if (finMain.getGrcAdvMargin().compareTo(zeroValue) != 0 && StringUtils.isBlank(finMain.getGrcAdvBaseRate())) {
			String[] valueParm = new String[1];
			valueParm[0] = "Grace Advise";
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90155", valueParm)));
		}

		return errorDetails;

	}

	/*
	 * _______________________________________________________________________________________________________________
	 * GRACE DATES
	 * _______________________________________________________________________________________________________________
	 */

	private List<ErrorDetails> graceDatesValidation(FinScheduleData finScheduleData) {
		List<ErrorDetails> errorDetails = finScheduleData.getErrorDetails();
		FinanceMain finMain = finScheduleData.getFinanceMain();
		FinanceType financeType = finScheduleData.getFinanceType();

		Date geDate = finMain.getCalGrcEndDate();

		//If Next Profit date is not as Grace End Date, it should match with frequency
		if (finMain.getNextGrcPftDate().compareTo(geDate) != 0
				&& !FrequencyUtil.isFrqDate(finMain.getGrcPftFrq(), finMain.getNextGrcPftDate())) {
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90177", null)));
		}

		//If Next Profit Review date is not as Grace End Date, it should match with frequency
		if(financeType.isFinGrcIsRvwAlw()) {
			if (finMain.getNextGrcPftRvwDate().compareTo(geDate) != 0
					&& !FrequencyUtil.isFrqDate(finMain.getGrcPftRvwFrq(), finMain.getNextGrcPftRvwDate())) {
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90178", null)));
			}
		}

		//If Next Profit capitalization date is not as Grace End Date, it should match with frequency
		if(financeType.isFinGrcIsIntCpz()) {
			if (finMain.getNextGrcCpzDate().compareTo(geDate) != 0
					&& !FrequencyUtil.isFrqDate(finMain.getGrcCpzFrq(), finMain.getNextGrcCpzDate())) {
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90179", null)));
			}
		}
		return errorDetails;
	}

	/*
	 * _______________________________________________________________________________________________________________
	 * GRACE RATE
	 * _______________________________________________________________________________________________________________
	 */

	private List<ErrorDetails> repayRateValidation(FinScheduleData finScheduleData) {
		List<ErrorDetails> errorDetails = finScheduleData.getErrorDetails();
		FinanceMain finMain = finScheduleData.getFinanceMain();
		FinanceType financeType = finScheduleData.getFinanceType();
		BigDecimal zeroValue = BigDecimal.ZERO;
		final String REPAY = "Repay";

		//Rate Type/Rate Basis
		if (!StringUtils.equals(finMain.getRepayRateBasis(), CalculationConstants.RATE_BASIS_F)
				&& !StringUtils.equals(finMain.getRepayRateBasis(), CalculationConstants.RATE_BASIS_C)
				&& !StringUtils.equals(finMain.getRepayRateBasis(), CalculationConstants.RATE_BASIS_R)) {
			String[] valueParm = new String[1];
			valueParm[0] = finMain.getRepayRateBasis();
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90211", valueParm)));
		}

		//Actual Rate
		if (StringUtils.isBlank(financeType.getFinBaseRate()) && finMain.getRepayProfitRate().compareTo(zeroValue) < 0) {
			String[] valueParm = new String[1];
			valueParm[0] = REPAY;
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90153", valueParm)));
		}

		//Both Grace and Base Rates found
		if (StringUtils.isNotBlank(finMain.getRepayBaseRate()) && finMain.getRepayProfitRate().compareTo(zeroValue) != 0) {
			String[] valueParm = new String[1];
			valueParm[0] = REPAY;
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90154", valueParm)));
		}

		// validate base rate
		if(StringUtils.isBlank(finMain.getRepayBaseRate()) && StringUtils.isNotBlank(financeType.getFinBaseRate())) {
			String[] valueParm = new String[2];
			valueParm[0] = REPAY;
			valueParm[1] = finMain.getFinType();
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90232", valueParm)));
		}

		//Base Rate requested?
		if (StringUtils.isNotBlank(finMain.getRepayBaseRate())) {
			//Allow Base Rate?
			if (StringUtils.isBlank(financeType.getFinBaseRate())) {
				String[] valueParm = new String[2];
				valueParm[0] = REPAY;
				valueParm[1] = finMain.getFinType();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90136", valueParm)));
			} else {
				//Base Rate code found?
				String brCode = finMain.getRepayBaseRate();
				String currency = finMain.getFinCcy();
				currency = financeType.getFinCcy();

				int rcdCount = baseRateDAO.getBaseRateCountById(brCode, currency, "");
				if (rcdCount <= 0) {
					String[] valueParm = new String[2];
					valueParm[0] = brCode;
					valueParm[1] = finMain.getFinCcy();
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90137", valueParm)));
				}

				// validate special rate code
				if(StringUtils.isNotBlank(finMain.getRepaySpecialRate())) {
					rcdCount = splRateDAO.getSpecialRateCountById(finMain.getRepaySpecialRate(), "");
					if (rcdCount <= 0) {
						String[] valueParm = new String[2];
						valueParm[0] = finMain.getRepaySpecialRate();
						valueParm[1] = REPAY;
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90230", valueParm)));
					}
				}
			}
		} else if(finMain.getRepayProfitRate().compareTo(BigDecimal.ZERO) > 0 && 
				StringUtils.isNotBlank(financeType.getFinBaseRate())) {
			String[] valueParm = new String[2];
			valueParm[0] = "repayPftRate";
			valueParm[1] = finMain.getFinType();
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90204", valueParm)));
		}

		//Special Rate code
		if (StringUtils.isNotBlank(finMain.getRepaySpecialRate()) && StringUtils.isBlank(finMain.getRepayBaseRate())) {
			String[] valueParm = new String[1];
			valueParm[0] = REPAY;
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90155", valueParm)));
		}

		//Margin
		if (finMain.getRepayMargin().compareTo(zeroValue) != 0 && StringUtils.isBlank(finMain.getRepayBaseRate())) {
			String[] valueParm = new String[1];
			valueParm[0] = REPAY;
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90155", valueParm)));
		}

		if (!errorDetails.isEmpty()) {
			return errorDetails;
		}

		//Validate Against Minimum and Maximum Rate
		if (finMain.getRpyMinRate().compareTo(zeroValue) == 0 && finMain.getRpyMaxRate().compareTo(zeroValue) == 0) {
			//No further validation required
			return errorDetails;
		}

		BigDecimal netRate = BigDecimal.ZERO;
		//Base Rate
		if (StringUtils.isNotBlank(finMain.getRepayBaseRate())) {
			RateDetail rate = new RateDetail();
			rate.setBaseRateCode(finMain.getRepayBaseRate());
			rate.setCurrency(finMain.getFinCcy());
			rate.setSplRateCode(finMain.getRepaySpecialRate());
			rate.setMargin(finMain.getRepayMargin());
			rate.setValueDate(finMain.getGrcPeriodEndDate());
			rate = RateUtil.getRefRate(rate);
			netRate = rate.getNetRefRateLoan();
		} else {
			netRate = finMain.getRepayProfitRate();
		}

		//Check Against Minimum Rate 
		if (netRate.compareTo(finMain.getRpyMinRate()) < 0) {
			String[] valueParm = new String[2];
			valueParm[0] = round4(netRate).toString();
			valueParm[1] = round4(finMain.getRpyMinRate()).toString();
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90174", valueParm)));
		}

		//Check Against Maximum Rate 
		if (finMain.getRpyMaxRate().compareTo(zeroValue) != 0) {
			if (netRate.compareTo(finMain.getRpyMaxRate()) > 0) {
				String[] valueParm = new String[2];
				valueParm[0] = round4(netRate).toString();
				valueParm[1] = round4(finMain.getRpyMaxRate()).toString();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90175", valueParm)));
			}
		}

		return errorDetails;

	}

	/*
	 * _______________________________________________________________________________________________________________
	 * VALIDATE REPAY FREQUENCY AND NEXT PROFIT DATE
	 * _______________________________________________________________________________________________________________
	 */

	private List<ErrorDetails> repayFrqValidation(FinScheduleData finScheduleData) {
		List<ErrorDetails> errorDetails = finScheduleData.getErrorDetails();
		FinanceMain finMain = finScheduleData.getFinanceMain();

		//Validate Repayment Frequency
		ErrorDetails tempError = FrequencyUtil.validateFrequency(finMain.getRepayFrq());
		if (tempError != null) {
			String[] valueParm = new String[2];
			valueParm[0] = "Repay";
			valueParm[1] = finMain.getRepayFrq();
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90159", valueParm)));
		}

		//First Repayment Date Vs Start Date
		if (finMain.getNextRepayDate().compareTo(finMain.getCalGrcEndDate()) <= 0) {
			String[] valueParm = new String[2];
			valueParm[0] = DateUtility.formatToShortDate(finMain.getNextRepayDate());
			valueParm[1] = DateUtility.formatToShortDate(finMain.getCalGrcEndDate());
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90192", valueParm)));
		}

		//Return if any error
		if (!errorDetails.isEmpty()) {
			return errorDetails;
		}

		//Default Calculated Maturity Date using terms
		if (finMain.getNumberOfTerms() > 0) {
			finMain.setCalTerms(finMain.getNumberOfTerms());
			List<Calendar> scheduleDateList = FrequencyUtil.getNextDate(finMain.getRepayFrq(), finMain.getNumberOfTerms(),
					finMain.getNextRepayDate(), HolidayHandlerTypes.MOVE_NONE, true).getScheduleList();

			Date matDate = null;
			if (scheduleDateList != null) {
				Calendar calendar = scheduleDateList.get(scheduleDateList.size() - 1);
				matDate = DateUtility.getDBDate(DateUtility.formatDate(calendar.getTime(), PennantConstants.DBDateFormat));
			}

			finMain.setCalMaturity(matDate);
		} else {
			//Default Calculated Terms based on Maturity Date
			finMain.setCalMaturity(finMain.getMaturityDate());
			int terms = FrequencyUtil.getTerms(finMain.getRepayFrq(), finMain.getNextRepayDate(),
					finMain.getMaturityDate(), true, true).getTerms();
			finMain.setCalTerms(terms);
		}

		//First Repayment Date Vs Maturity Date
		if (finMain.getNextRepayDate().compareTo(finMain.getCalMaturity()) > 0) {
			String[] valueParm = new String[2];
			valueParm[0] = DateUtility.formatToShortDate(finMain.getNextRepayDate());
			valueParm[1] = DateUtility.formatToShortDate(finMain.getMaturityDate());
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90193", valueParm)));
		}

		return errorDetails;

	}

	/*
	 * _______________________________________________________________________________________________________________
	 * REPAY PROFIT FREQUENCY VALIDATION
	 * _______________________________________________________________________________________________________________
	 */

	private List<ErrorDetails> repayPftFrqValidation(FinScheduleData finScheduleData) {
		List<ErrorDetails> errorDetails = finScheduleData.getErrorDetails();
		FinanceMain finMain = finScheduleData.getFinanceMain();

		//Validate Profit Frequency Frequency
		ErrorDetails tempError = FrequencyUtil.validateFrequency(finMain.getRepayPftFrq());
		if (tempError != null) {
			String[] valueParm = new String[2];
			valueParm[0] = "Repay";
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90156", valueParm)));
		}

		//First Repayment Frequency Date Vs Start Date/Grace End Date
		if (finMain.getNextRepayPftDate().compareTo(finMain.getCalGrcEndDate()) <= 0) {
			String[] valueParm = new String[2];
			valueParm[0] = DateUtility.formatToShortDate(finMain.getNextRepayPftDate());
			valueParm[1] = DateUtility.formatToShortDate(finMain.getCalGrcEndDate());
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90194", valueParm)));
		}

		//First Interest Frequency Date Vs Next Repayment Date
		if (finMain.getNextRepayPftDate().compareTo(finMain.getNextRepayDate()) > 0) {
			String[] valueParm = new String[2];
			valueParm[0] = DateUtility.formatToShortDate(finMain.getNextRepayPftDate());
			valueParm[1] = DateUtility.formatToShortDate(finMain.getNextRepayDate());
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90195", valueParm)));
		}

		return errorDetails;
	}

	/*
	 * _______________________________________________________________________________________________________________
	 * REPAY PROFIT REVIEW FREQUENCY VALIDATION
	 * _______________________________________________________________________________________________________________
	 */

	private List<ErrorDetails> repayPftReviewValidation(FinScheduleData finScheduleData) {
		List<ErrorDetails> errorDetails = finScheduleData.getErrorDetails();
		FinanceMain finMain = finScheduleData.getFinanceMain();
		FinanceType financeType = finScheduleData.getFinanceType();

		//Allow Profit Rate Review
		if (!financeType.isFinIsRvwAlw()) {
			if (finMain.isAllowRepayRvw() || StringUtils.isNotBlank(finMain.getRepayRvwFrq()) 
					|| finMain.getNextRepayRvwDate() != null) {
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90196", null)));
				return errorDetails;
			} else {
				return errorDetails;
			}
		}

		//Validate Profit Review Frequency
		ErrorDetails tempError = FrequencyUtil.validateFrequency(finMain.getRepayRvwFrq());
		if (tempError != null) {
			String[] valueParm = new String[2];
			valueParm[0] = "Repay";
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90157", valueParm)));
		}

		//First Repayment Profit Review Date Vs Start Date/Grace End Date
		if (finMain.getNextRepayRvwDate().compareTo(finMain.getCalGrcEndDate()) <= 0) {
			String[] valueParm = new String[2];
			valueParm[0] = DateUtility.formatToShortDate(finMain.getNextRepayRvwDate());
			valueParm[1] = DateUtility.formatToShortDate(finMain.getCalGrcEndDate());
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90197", valueParm)));
		}

		//First Repayment Profit Review Date Vs Maturity Date
		if (finMain.getNextRepayRvwDate().compareTo(finMain.getMaturityDate()) > 0) {
			String[] valueParm = new String[2];
			valueParm[0] = DateUtility.formatToShortDate(finMain.getNextRepayRvwDate());
			valueParm[1] = DateUtility.formatToShortDate(finMain.getMaturityDate());
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90198", valueParm)));
		}

		return errorDetails;
	}

	/*
	 * _______________________________________________________________________________________________________________
	 * REPAY PROFIT CAPITALIZATION
	 * _______________________________________________________________________________________________________________
	 */

	private List<ErrorDetails> repayPftCpzValidation(FinScheduleData finScheduleData) {
		List<ErrorDetails> errorDetails = finScheduleData.getErrorDetails();
		FinanceMain finMain = finScheduleData.getFinanceMain();
		FinanceType financeType = finScheduleData.getFinanceType();

		//Allow Profit Capitalization
		if (!financeType.isFinIsIntCpz()) {
			if (finMain.isAllowRepayCpz() || StringUtils.isNotBlank(finMain.getRepayCpzFrq())
					|| finMain.getNextRepayCpzDate() != null) {
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90199", null)));
				return errorDetails;
			} else {
				return errorDetails;
			}
		}

		if(financeType.isFinIsIntCpz()) {
			//Validate Profit Capitalization Frequency
			ErrorDetails tempError = FrequencyUtil.validateFrequency(finMain.getRepayCpzFrq());
			if (tempError != null) {
				String[] valueParm = new String[2];
				valueParm[0] = "Repay";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90158", valueParm)));
			}

			//First Interest Capitalization Frequency Date Vs Start Date/GE Date
			if (finMain.getNextRepayCpzDate().compareTo(finMain.getCalGrcEndDate()) <= 0) {
				String[] valueParm = new String[2];
				valueParm[0] = DateUtility.formatToShortDate(finMain.getNextRepayCpzDate());
				valueParm[1] = DateUtility.formatToShortDate(finMain.getCalGrcEndDate());
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90225", valueParm)));
			}

			//First Interest Capitalization Frequency Date Vs M
			if (finMain.getNextRepayCpzDate().compareTo(finMain.getCalMaturity()) > 0) {
				String[] valueParm = new String[2];
				valueParm[0] = DateUtility.formatToShortDate(finMain.getNextRepayCpzDate());
				valueParm[1] = DateUtility.formatToShortDate(finMain.getCalMaturity());
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90226", valueParm)));
			}
		}

		return errorDetails;
	}

	/*
	 * _______________________________________________________________________________________________________________
	 * ADVISED REPAYMENT RATE
	 * _______________________________________________________________________________________________________________
	 */

	private List<ErrorDetails> repayAdvRateValidation(FinScheduleData finScheduleData) {
		List<ErrorDetails> errorDetails = finScheduleData.getErrorDetails();
		FinanceMain finMain = finScheduleData.getFinanceMain();
		FinanceType financeType = finScheduleData.getFinanceType();
		BigDecimal zeroValue = BigDecimal.ZERO;

		if (!StringUtils.equals(finMain.getProductCategory(), FinanceConstants.PRODUCT_STRUCTMUR)) {
			if (finMain.getRpyAdvPftRate().compareTo(zeroValue) != 0
					|| StringUtils.isNotBlank(finMain.getRpyAdvBaseRate())
					|| finMain.getRpyAdvMargin().compareTo(zeroValue) != 0) {
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90176", null)));
			}
		}

		//Actual Rate
		if (finMain.getRpyAdvPftRate().compareTo(zeroValue) < 0) {
			String[] valueParm = new String[1];
			valueParm[0] = "Repay Advice";
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90153", valueParm)));
		}

		//Both Grace and Base Rates found
		if (StringUtils.isNotBlank(finMain.getRpyAdvBaseRate())) {
			if (finMain.getRpyAdvPftRate().compareTo(zeroValue) != 0) {
				String[] valueParm = new String[1];
				valueParm[0] = "Repay Advise";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90154", valueParm)));
			}
		}

		//Base Rate requested?
		if (StringUtils.isNotBlank(finMain.getRpyAdvBaseRate())) {
			//Allow Base Rate?
			if (StringUtils.isBlank(financeType.getRpyAdvBaseRate())) {
				String[] valueParm = new String[2];
				valueParm[0] = "Repay Advised";
				valueParm[1] = finMain.getFinType();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90136", valueParm)));
			} else {
				//Base Rate code found?
				String brCode = finMain.getRpyAdvBaseRate();
				String currency = finMain.getFinCcy();

				int rcdCount = baseRateDAO.getBaseRateCountById(brCode, currency, "");
				if (rcdCount <= 0) {
					String[] valueParm = new String[2];
					valueParm[0] = brCode;
					valueParm[1] = finMain.getFinCcy();
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90137", valueParm)));
				}
			}
		}

		//Margin
		if (finMain.getRpyAdvMargin().compareTo(zeroValue) != 0 && StringUtils.isBlank(finMain.getRpyAdvBaseRate())) {
			String[] valueParm = new String[1];
			valueParm[0] = "Repay Advise";
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90155", valueParm)));
		}

		return errorDetails;

	}

	/*
	 * _______________________________________________________________________________________________________________
	 * BPI
	 * _______________________________________________________________________________________________________________
	 */

	private List<ErrorDetails> bpiValidation(FinScheduleData finScheduleData) {
		List<ErrorDetails> errorDetails = finScheduleData.getErrorDetails();
		FinanceMain finMain = finScheduleData.getFinanceMain();
		FinanceType financeType = finScheduleData.getFinanceType();

		if (!finMain.isAlwBPI()) {
			if (!StringUtils.equals(finMain.getBpiTreatment(), FinanceConstants.BPI_NO)) {
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90229", null)));
				return errorDetails;
			}
		}

		if (finMain.isAlwBPI() && !financeType.isAlwBPI()) {
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90228", null)));
			return errorDetails;
		}

		if (StringUtils.isNotBlank(finMain.getBpiTreatment())) {
			if (!StringUtils.equals(finMain.getBpiTreatment(), FinanceConstants.BPI_NO)
					&& !StringUtils.equals(finMain.getBpiTreatment(), FinanceConstants.BPI_DISBURSMENT)
					&& !StringUtils.equals(finMain.getBpiTreatment(), FinanceConstants.BPI_SCHEDULE)
					&& !StringUtils.equals(finMain.getBpiTreatment(), FinanceConstants.BPI_CAPITALIZE)
					&& !StringUtils.equals(finMain.getBpiTreatment(), FinanceConstants.BPI_SCHD_FIRSTEMI)) {
				String[] valueParm = new String[1];
				valueParm[0] = finMain.getBpiTreatment();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90185", valueParm)));
			}
		}
		return errorDetails;

	}

	/**
	 * Method for validate fee details
	 * 
	 * @param vldGroup
	 * @param finScheduleData
	 * @param isAPICall
	 * @return List<ErrorDetails>
	 */
	private List<ErrorDetails> feeValidations(String vldGroup, FinScheduleData finSchdData, boolean isAPICall) {
		List<ErrorDetails> errorDetails = finSchdData.getErrorDetails();
		int formatter = CurrencyUtil.getFormat(finSchdData.getFinanceMain().getFinCcy());

		for (FinFeeDetail finFeeDetail : finSchdData.getFinFeeDetailList()) {

			if(StringUtils.isBlank(finFeeDetail.getFeeScheduleMethod())) {
				String[] valueParm = new String[1];
				valueParm[0] = "feeMethod";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90502", valueParm)));
				return errorDetails;
			}
			// validate feeMethod
			if (!StringUtils.equals(finFeeDetail.getFeeScheduleMethod(), FinanceConstants.BPI_NO)
					&& !StringUtils.equals(finFeeDetail.getFeeScheduleMethod(), CalculationConstants.REMFEE_PART_OF_DISBURSE)
					&& !StringUtils.equals(finFeeDetail.getFeeScheduleMethod(), CalculationConstants.REMFEE_PART_OF_SALE_PRICE)
					&& !StringUtils.equals(finFeeDetail.getFeeScheduleMethod(), CalculationConstants.REMFEE_SCHD_TO_FIRST_INSTALLMENT)
					&& !StringUtils.equals(finFeeDetail.getFeeScheduleMethod(), CalculationConstants.REMFEE_SCHD_TO_ENTIRE_TENOR)
					&& !StringUtils.equals(finFeeDetail.getFeeScheduleMethod(), CalculationConstants.REMFEE_SCHD_TO_N_INSTALLMENTS)
					&& !StringUtils.equals(finFeeDetail.getFeeScheduleMethod(), CalculationConstants.REMFEE_PAID_BY_CUSTOMER)
					&& !StringUtils.equals(finFeeDetail.getFeeScheduleMethod(), CalculationConstants.REMFEE_WAIVED_BY_BANK)) {
				String[] valueParm = new String[2];
				valueParm[0] = finFeeDetail.getFeeScheduleMethod();
				valueParm[1] = CalculationConstants.REMFEE_PART_OF_DISBURSE+","+
						CalculationConstants.REMFEE_PART_OF_SALE_PRICE+","+
						CalculationConstants.REMFEE_SCHD_TO_FIRST_INSTALLMENT+","+
						CalculationConstants.REMFEE_SCHD_TO_ENTIRE_TENOR+","+
						CalculationConstants.REMFEE_SCHD_TO_N_INSTALLMENTS+","+
						CalculationConstants.REMFEE_PAID_BY_CUSTOMER+","+
						CalculationConstants.REMFEE_WAIVED_BY_BANK;
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90243", valueParm)));
			}

			// validate scheduleTerms
			if (StringUtils.equals(CalculationConstants.REMFEE_SCHD_TO_N_INSTALLMENTS, finFeeDetail.getFeeScheduleMethod())
					&& finFeeDetail.getTerms() <= 0) {
				String[] valueParm = new String[1];
				valueParm[0] = "ScheduleTerms";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90221", valueParm)));
			}
		}

		// validate fee code
		String finEvent = "";
		if (finSchdData.getFinanceMain().getFinStartDate().after(DateUtility.getAppDate())) {
			if (AccountEventConstants.ACCEVENT_ADDDBSF_REQ) {
				finEvent = AccountEventConstants.ACCEVENT_ADDDBSF;
			} else {
				finEvent = AccountEventConstants.ACCEVENT_ADDDBSP;
			}
		} else {
			finEvent = AccountEventConstants.ACCEVENT_ADDDBSP;
		}

		List<FinTypeFees> finTypeFeeDetail = getFinanceDetailService().getFinTypeFees(finSchdData.getFinanceMain().getFinType(), 
				finEvent, true);
		if (finTypeFeeDetail != null) {
			if (finTypeFeeDetail.size() == finSchdData.getFinFeeDetailList().size()) {
				for (FinFeeDetail feeDetail : finSchdData.getFinFeeDetailList()) {
					BigDecimal finWaiverAmount = BigDecimal.ZERO;
					boolean isFeeCodeFound = false;
					for (FinTypeFees finTypeFee : finTypeFeeDetail) {
						if (StringUtils.equals(feeDetail.getFeeTypeCode(), finTypeFee.getFeeTypeCode())) {
							isFeeCodeFound = true;

							// validate negative values
							if(feeDetail.getActualAmount().compareTo(BigDecimal.ZERO) < 0 ||
									feeDetail.getPaidAmount().compareTo(BigDecimal.ZERO) < 0 ||
									feeDetail.getWaivedAmount().compareTo(BigDecimal.ZERO) < 0 ) {
								String[] valueParm = new String[1];
								valueParm[0] = feeDetail.getFeeTypeCode();
								errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90259", valueParm)));
								return errorDetails;
							}
							
							// validate fee schedule method
							if (!finTypeFee.isAlwModifyFeeSchdMthd() && !StringUtils.equals(feeDetail.getFeeScheduleMethod(),
									finTypeFee.getFeeScheduleMethod())) {
								String[] valueParm = new String[1];
								valueParm[0] = feeDetail.getFeeTypeCode();
								errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90246", valueParm)));
								return errorDetails;
							}

							// calculate percentage amount
							if (StringUtils.equals(finTypeFee.getCalculationType(), PennantConstants.FEE_CALCULATION_TYPE_PERCENTAGE)) {
								BigDecimal calPercentageFee = getCalculatedPercentageFee(finTypeFee, finSchdData, formatter);
								finTypeFee.setAmount(calPercentageFee);
							}

							// validate allow modify fee amount
							if (!StringUtils.equals(finTypeFee.getCalculationType(), PennantConstants.FEE_CALCULATION_TYPE_RULE)) {
								if (!finTypeFee.isAlwModifyFee() && feeDetail.getActualAmount().compareTo(finTypeFee.getAmount()) != 0) {
									String[] valueParm = new String[3];
									valueParm[0] = "Fee amount";
									valueParm[1] = "Actual fee amount:"+String.valueOf(finTypeFee.getAmount());
									valueParm[2] = feeDetail.getFeeTypeCode();
									errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90258", valueParm)));
									return errorDetails;
								}
							}

							// validate paid amount with with actual fee amount
							if (!StringUtils.equals(finTypeFee.getCalculationType(), PennantConstants.FEE_CALCULATION_TYPE_RULE)) {
								if (feeDetail.getPaidAmount().compareTo(finTypeFee.getAmount()) > 0) {
									String[] valueParm = new String[3];
									valueParm[0] = "Paid amount";
									valueParm[1] = "Actual amount:"+String.valueOf(finTypeFee.getAmount());
									valueParm[2] = feeDetail.getFeeTypeCode();
									errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90257", valueParm)));
									return errorDetails;
								}

								// validate waiver amount
								BigDecimal maxWaiverPer = finTypeFee.getMaxWaiverPerc();
								finWaiverAmount = (finTypeFee.getAmount().multiply(maxWaiverPer)).divide(new BigDecimal(100));
								finWaiverAmount = PennantApplicationUtil.unFormateAmount(finWaiverAmount, formatter);
								if (feeDetail.getWaivedAmount().compareTo(finWaiverAmount) > 0) {
									String[] valueParm = new String[3];
									valueParm[0] = "Waiver amount";
									valueParm[1] = "Actual waiver amount:"+String.valueOf(finWaiverAmount);
									valueParm[2] = feeDetail.getFeeTypeCode();
									errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90257", valueParm)));
									return errorDetails;
								}
								
								// validate actual fee amount with waiver+paid amount
								BigDecimal remainingFee = feeDetail.getActualAmount().subtract(feeDetail.getWaivedAmount().add(feeDetail.getPaidAmount()));
								if(remainingFee.compareTo(BigDecimal.ZERO) < 0) {
									String[] valueParm = new String[3];
									valueParm[0] = "Sum of waiver and paid amounts";
									valueParm[1] = "Actual fee amount:"+String.valueOf(feeDetail.getActualAmount());
									valueParm[2] = feeDetail.getFeeTypeCode();
									errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90257", valueParm)));
									return errorDetails;
								}
							}
							// validate paid by Customer method
							if (StringUtils.equals(finTypeFee.getFeeScheduleMethod(), CalculationConstants.REMFEE_PAID_BY_CUSTOMER)) {
								if (feeDetail.getPaidAmount().compareTo(finTypeFee.getAmount()) != 0) {
									String[] valueParm = new String[1];
									valueParm[0] = finTypeFee.getFeeTypeCode();
									errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90254", valueParm)));
									return errorDetails;
								}
							}
							// validate waived by bank method
							if (StringUtils.equals(finTypeFee.getFeeScheduleMethod(), CalculationConstants.REMFEE_WAIVED_BY_BANK)) {
								if (feeDetail.getWaivedAmount().compareTo(finWaiverAmount) != 0) {
									String[] valueParm = new String[3];
									valueParm[0] = "Waiver amount";
									valueParm[1] = "Actual waiver amount:"+String.valueOf(finWaiverAmount);
									valueParm[2] = feeDetail.getFeeTypeCode();
									errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90258", valueParm)));
									return errorDetails;
								}
							}
						}
					}
					if (!isFeeCodeFound) {
						String[] valueParm = new String[1];
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90247", valueParm)));
						return errorDetails;
					}
				}

			} else {
				String[] valueParm = new String[1];
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90244", valueParm)));
				return errorDetails;
			}
		} else {
			String[] valueParm = new String[1];
			valueParm[0] = finSchdData.getFinanceMain().getFinType();
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90245", valueParm)));
			return errorDetails;
		}

		return errorDetails;
	}
	
	/**
	 * Method for calculate Actual fee amount for percentage configured in loan type.
	 * 
	 * @param finFeeDetail
	 * @param finScheduleData
	 * @param formatter
	 * @return
	 */
	private BigDecimal getCalculatedPercentageFee(FinTypeFees finFeeDetail, FinScheduleData finScheduleData, int formatter) {
		BigDecimal calculatedAmt = BigDecimal.ZERO;
		FinanceMain financeMain = finScheduleData.getFinanceMain();
		if (StringUtils.equals(PennantConstants.FEE_CALCULATEDON_TOTALASSETVALUE, finFeeDetail.getCalculateOn())) {
			calculatedAmt = financeMain.getFinAssetValue();
		} else {
			calculatedAmt = financeMain.getFinAmount().subtract(financeMain.getDownPayment());
		}
		calculatedAmt = calculatedAmt.multiply(finFeeDetail.getPercentage()).divide(BigDecimal.valueOf(100), 2,
				RoundingMode.HALF_DOWN);
		calculatedAmt = PennantApplicationUtil.unFormateAmount(calculatedAmt, formatter);
		return calculatedAmt;
	}
	
	/**
	 * 
	 * @param vldGroup
	 * @param finScheduleData
	 * @param isAPICall
	 * @return
	 */
	private List<ErrorDetails> insuranceValidations(String vldGroup, FinScheduleData finScheduleData, boolean isAPICall) {
		//TODO: write insurance validations
		return new ArrayList<ErrorDetails>();
	}

	/**
	 * Method for validate stepping details
	 * 
	 * @param vldGroup
	 * @param finScheduleData
	 * @param isAPICall
	 * @return List<ErrorDetails>
	 */
	private List<ErrorDetails> stepValidations(String vldGroup, FinScheduleData finScheduleData, boolean isAPICall) {
		List<ErrorDetails> errorDetails = finScheduleData.getErrorDetails();
		BigDecimal tenorPercTotal = BigDecimal.ZERO;
		BigDecimal emiPercTotal = BigDecimal.ZERO;
		int totalSteps = 0;
		String stepType = finScheduleData.getFinanceMain().getStepType();

		// validate number of steps
		if(finScheduleData.getStepPolicyDetails().size() < 2) {
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90233", null)));
			return errorDetails;
		}

		// validate split percentages
		for (FinanceStepPolicyDetail policyDetail: finScheduleData.getStepPolicyDetails()) {
			tenorPercTotal = tenorPercTotal.add(policyDetail.getTenorSplitPerc());
			emiPercTotal = emiPercTotal.add(policyDetail.getEmiSplitPerc());
			totalSteps++;
		}
		if(tenorPercTotal.compareTo(new BigDecimal(100)) != 0) {
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90234", null)));
			return errorDetails;
		}

		if(StringUtils.equals(stepType, FinanceConstants.STEPTYPE_EMI)){
			BigDecimal emiPerc = emiPercTotal.divide(new BigDecimal(totalSteps),RoundingMode.HALF_UP);
			if(emiPerc.compareTo(new BigDecimal(100)) != 0) {
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90235", null)));
				return errorDetails;
			}
		} else if(StringUtils.equals(stepType, FinanceConstants.STEPTYPE_PRIBAL)){
			BigDecimal priPerc = emiPercTotal;
			if(priPerc.compareTo(new BigDecimal(100)) != 0) {
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90236", null)));
				return errorDetails;
			}
		}

		return errorDetails;
	}

	/*
	 * _______________________________________________________________________________________________________________
	 * ROUNDING
	 * _______________________________________________________________________________________________________________
	 */
	private BigDecimal round4(BigDecimal value) {
		return value.setScale(4, RoundingMode.HALF_DOWN);
	}

	/*
	 * ################################################################################################################
	 * DEFAULT SETTER GETTER METHODS
	 * ################################################################################################################
	 */

	public void setBaseRateDAO(BaseRateDAO baseRateDAO) {
		this.baseRateDAO = baseRateDAO;
	}

	public void setBranchDAO(BranchDAO branchDAO) {
		this.branchDAO = branchDAO;
	}

	public void setFinanceTypeDAO(FinanceTypeDAO financeTypeDAO) {
		this.financeTypeDAO = financeTypeDAO;
	}

	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

	public void setSplRateDAO(SplRateDAO splRateDAO) {
		this.splRateDAO = splRateDAO;
	}

	public FinanceDetailService getFinanceDetailService() {
		return financeDetailService;
	}
	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}
	public void setBankDetailService(BankDetailService bankDetailService) {
		this.bankDetailService = bankDetailService;
	}

	public void setBankBranchService(BankBranchService bankBranchService) {
		this.bankBranchService = bankBranchService;
	}
	public void setDocumentTypeService(DocumentTypeService documentTypeService) {
		this.documentTypeService = documentTypeService;
	}
	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}
	public void setFlagDAO(FlagDAO flagDAO) {
		this.flagDAO = flagDAO;
	}
	public void setCollateralSetupService(CollateralSetupService collateralSetupService) {
		this.collateralSetupService = collateralSetupService;
	}
	public void setMandateService(MandateService mandateService) {
		this.mandateService = mandateService;
	}
	public void setStepPolicyService(StepPolicyService stepPolicyService) {
		this.stepPolicyService = stepPolicyService;
	}
	public void setGeneralDepartmentService(GeneralDepartmentService generalDepartmentService) {
		this.generalDepartmentService = generalDepartmentService;
	}
	public void setRelationshipOfficerService(RelationshipOfficerService relationshipOfficerService) {
		this.relationshipOfficerService = relationshipOfficerService;
	}
}
