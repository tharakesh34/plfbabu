package com.pennant.backend.service.finance.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;

import com.pennant.app.constants.AccountConstants;
import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.HolidayHandlerTypes;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.constants.LengthConstants;
import com.pennant.app.model.RateDetail;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.FrequencyUtil;
import com.pennant.app.util.RateUtil;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.applicationmaster.BaseRateDAO;
import com.pennant.backend.dao.applicationmaster.BranchDAO;
import com.pennant.backend.dao.applicationmaster.CurrencyDAO;
import com.pennant.backend.dao.applicationmaster.FlagDAO;
import com.pennant.backend.dao.applicationmaster.PinCodeDAO;
import com.pennant.backend.dao.applicationmaster.SplRateDAO;
import com.pennant.backend.dao.collateral.ExtendedFieldRenderDAO;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.finance.FinTypeVASProductsDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.partnerbank.PartnerBankDAO;
import com.pennant.backend.dao.systemmasters.CityDAO;
import com.pennant.backend.dao.systemmasters.LoanPurposeDAO;
import com.pennant.backend.dao.systemmasters.ProvinceDAO;
import com.pennant.backend.model.ScriptError;
import com.pennant.backend.model.ScriptErrors;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.amtmasters.VehicleDealer;
import com.pennant.backend.model.applicationmaster.BankDetail;
import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.model.applicationmaster.PinCode;
import com.pennant.backend.model.applicationmaster.RelationshipOfficer;
import com.pennant.backend.model.applicationmasters.Flag;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.bmtmasters.BankBranch;
import com.pennant.backend.model.collateral.CollateralAssignment;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.collateral.CollateralThirdParty;
import com.pennant.backend.model.configuration.VASConfiguration;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.customermasters.CustomerEligibilityCheck;
import com.pennant.backend.model.customermasters.CustomerEmploymentDetail;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.extendedfield.ExtendedField;
import com.pennant.backend.model.extendedfield.ExtendedFieldData;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinODPenaltyRate;
import com.pennant.backend.model.finance.FinPlanEmiHoliday;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceStepPolicyDetail;
import com.pennant.backend.model.finance.GuarantorDetail;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.model.finance.financetaxdetail.FinanceTaxDetail;
import com.pennant.backend.model.financemanagement.FinFlagsDetail;
import com.pennant.backend.model.financemanagement.FinTypeVASProducts;
import com.pennant.backend.model.mandate.Mandate;
import com.pennant.backend.model.partnerbank.PartnerBank;
import com.pennant.backend.model.rmtmasters.FinTypeFees;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.smtmasters.PFSParameter;
import com.pennant.backend.model.solutionfactory.ExtendedFieldDetail;
import com.pennant.backend.model.solutionfactory.StepPolicyHeader;
import com.pennant.backend.model.systemmasters.City;
import com.pennant.backend.model.systemmasters.DocumentType;
import com.pennant.backend.model.systemmasters.LoanPurpose;
import com.pennant.backend.model.systemmasters.Province;
import com.pennant.backend.service.amtmasters.VehicleDealerService;
import com.pennant.backend.service.applicationmaster.BankDetailService;
import com.pennant.backend.service.applicationmaster.RelationshipOfficerService;
import com.pennant.backend.service.bmtmasters.BankBranchService;
import com.pennant.backend.service.collateral.CollateralSetupService;
import com.pennant.backend.service.collateral.impl.ExtendedFieldDetailsValidation;
import com.pennant.backend.service.collateral.impl.ScriptValidationService;
import com.pennant.backend.service.configuration.VASConfigurationService;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.customermasters.CustomerDocumentService;
import com.pennant.backend.service.extendedfields.ExtendedFieldDetailsService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.mandate.MandateService;
import com.pennant.backend.service.rmtmasters.FinTypePartnerBankService;
import com.pennant.backend.service.rulefactory.RuleService;
import com.pennant.backend.service.solutionfactory.StepPolicyService;
import com.pennant.backend.service.systemmasters.DocumentTypeService;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.ExtendedFieldConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.MandateConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.RuleReturnType;
import com.pennant.backend.util.VASConsatnts;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.pff.document.DocumentCategories;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.document.DocumentService;

public class FinanceDataValidation {

	
	private BaseRateDAO						baseRateDAO;
	private SplRateDAO						splRateDAO;
	private BranchDAO						branchDAO;
	private CustomerDAO						customerDAO;
	private FinanceMainDAO					financeMainDAO;
	private FinanceDetailService			financeDetailService;
	private BankDetailService				bankDetailService;
	private BankBranchService				bankBranchService;
	private DocumentTypeService				documentTypeService;
	private CustomerDetailsService			customerDetailsService;
	private FlagDAO							flagDAO;
	private CollateralSetupService			collateralSetupService;
	private MandateService					mandateService;
	private StepPolicyService				stepPolicyService;
	private RelationshipOfficerService		relationshipOfficerService;
	private FinTypePartnerBankService		finTypePartnerBankService;
	private VASConfigurationService			vASConfigurationService;
	private ScriptValidationService			scriptValidationService;
	private ExtendedFieldDetailsService		extendedFieldDetailsService;
	private DocumentService					documentService;
	private VehicleDealerService        	vehicleDealerService;
	private RuleExecutionUtil				ruleExecutionUtil;
	private RuleService						ruleService;
	private FinTypeVASProductsDAO			finTypeVASProductsDAO;
	private ProvinceDAO						provinceDAO;
	private CityDAO							cityDAO;
	private FinanceDetail					financeDetail;
	private CustomerDocumentService			customerDocumentService;
	private ExtendedFieldDetailsValidation	extendedFieldDetailsValidation;
	private ExtendedFieldRenderDAO			extendedFieldRenderDAO;
	private PartnerBankDAO					partnerBankDAO;
	private CurrencyDAO						currencyDAO;
	private LoanPurposeDAO					loanPurposeDAO;
	private PinCodeDAO						pinCodeDAO;

	public FinanceDataValidation() {
		super();
	}

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}

	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
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

		List<ErrorDetail> errorDetails = null;
		boolean isAPICall = apiFlag;
		BigDecimal zeroAmount = BigDecimal.ZERO;

		// Non Finance validation
		errorDetails = nonFinanceValidation(vldGroup, finScheduleData, isAPICall);

		// validate FinReference
		ErrorDetail error = validateFinReference(finScheduleData.getFinReference(), finScheduleData,vldGroup);
		if(error != null) {
			errorDetails.add(error);
		}

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

		// Vas Recording validations
		errorDetails = vasRecordingValidations(vldGroup, finScheduleData, isAPICall, "");
		if (!errorDetails.isEmpty()) {
			finScheduleData.setErrorDetails(errorDetails);
			return finScheduleData;
		}
		
		// Vas Fee validations
		if(finScheduleData.getVasRecordingList() != null && !finScheduleData.getVasRecordingList().isEmpty()) {
			errorDetails = vasFeeValidations(vldGroup, finScheduleData);
			if (!errorDetails.isEmpty()) {
				finScheduleData.setErrorDetails(errorDetails);
				return finScheduleData;
			}
		}
		
		// Fee validations
		//TODO FIX
		boolean stp;
		if(financeDetail==null) {
			stp =true;
		} else {
			stp = financeDetail.isStp();
		}
		errorDetails = doValidateFees(finScheduleData, stp);
		if (!errorDetails.isEmpty()) {
			finScheduleData.setErrorDetails(errorDetails);
			return finScheduleData;
		}

		// Insurance validations
		if (finScheduleData.getInsuranceList() != null && !finScheduleData.getInsuranceList().isEmpty()) {
			errorDetails = insuranceValidations(vldGroup, finScheduleData, isAPICall);
			if (!errorDetails.isEmpty()) {
				finScheduleData.setErrorDetails(errorDetails);
				return finScheduleData;
			}
		}

		// Step validations
		if (finScheduleData.getStepPolicyDetails() != null && !finScheduleData.getStepPolicyDetails().isEmpty()) {
			errorDetails = stepValidations(vldGroup, finScheduleData, isAPICall);
			if (!errorDetails.isEmpty()) {
				finScheduleData.setErrorDetails(errorDetails);
				return finScheduleData;
			}
		}

		//Net Loan Amount
		BigDecimal netLoanAmount = finMain.getFinAmount().subtract(finMain.getDownPayment());
		if (netLoanAmount.compareTo(financeType.getFinMinAmount()) < 0) {
			String[] valueParm = new String[1];
			valueParm[0] = String.valueOf(financeType.getFinMinAmount());
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90132", valueParm)));
		}

		if (financeType.getFinMaxAmount().compareTo(zeroAmount) > 0) {
			if (netLoanAmount.compareTo(financeType.getFinMaxAmount()) > 0) {
				String[] valueParm = new String[1];
				valueParm[0] = String.valueOf(financeType.getFinMaxAmount());
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90133", valueParm)));
			}
		}

		if (finMain.getReqRepayAmount().compareTo(BigDecimal.ZERO) < 0) {
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90187", null)));
		}

		if (finMain.getReqRepayAmount().compareTo(netLoanAmount) > 0) {
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90188", null)));
		}

		errorDetails.addAll(finODPenaltyRateValidation(finScheduleData));

		if (!errorDetails.isEmpty()) {
			finScheduleData.setErrorDetails(errorDetails);
			return finScheduleData;
		}

		return finScheduleData;
	}

	private List<ErrorDetail> vasFeeValidations(String vldGroup, FinScheduleData finScheduleData) {
		List<ErrorDetail> errorDetails = finScheduleData.getErrorDetails();
		int vasFeeCount = 0;
		if (finScheduleData.getFinFeeDetailList() != null && !finScheduleData.getFinFeeDetailList().isEmpty()) {
			for (FinFeeDetail feeDetail : finScheduleData.getFinFeeDetailList()) {
				for(VASRecording vasRecording:finScheduleData.getVasRecordingList())
				if (StringUtils.contains(feeDetail.getFeeTypeCode(), vasRecording.getProductCode())) {
					feeDetail.setFinEvent(AccountEventConstants.ACCEVENT_VAS_FEE);
					vasFeeCount++;
				}
			}
			for (FinFeeDetail feeDetail : finScheduleData.getFinFeeDetailList()) {
				int count = 0;
				for (FinFeeDetail detail : finScheduleData.getFinFeeDetailList()) {
					if (StringUtils.contains(detail.getFeeTypeCode(), "{"))
						if (StringUtils.equals(detail.getFeeTypeCode(), feeDetail.getFeeTypeCode())) {
							count++;
							if (count > 1) {
								errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90326", null)));
							}
						}
				}
			}
			
			if (finScheduleData.getVasRecordingList().size() > 0 && vasFeeCount <= 0) {
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90326", null)));
			} else if (finScheduleData.getVasRecordingList().size() <= 0 && vasFeeCount > 0) {
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90327", null)));
			} else if (finScheduleData.getVasRecordingList().size() != vasFeeCount) {
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90328", null)));
			}

			if (errorDetails.size() > 0) {
				return errorDetails;
			}
		}

		for (FinFeeDetail finFeeDetail : finScheduleData.getFinFeeDetailList()) {
			// validate feeMethod
			if (!StringUtils.equals(finFeeDetail.getFeeScheduleMethod(), FinanceConstants.BPI_NO)
					&& !StringUtils.equals(finFeeDetail.getFeeScheduleMethod(),
							CalculationConstants.REMFEE_PART_OF_DISBURSE)
					&& !StringUtils.equals(finFeeDetail.getFeeScheduleMethod(),
							CalculationConstants.REMFEE_PART_OF_SALE_PRICE)
					&& !StringUtils.equals(finFeeDetail.getFeeScheduleMethod(),
							CalculationConstants.REMFEE_SCHD_TO_FIRST_INSTALLMENT)
					&& !StringUtils.equals(finFeeDetail.getFeeScheduleMethod(),
							CalculationConstants.REMFEE_SCHD_TO_ENTIRE_TENOR)
					&& !StringUtils.equals(finFeeDetail.getFeeScheduleMethod(),
							CalculationConstants.REMFEE_SCHD_TO_N_INSTALLMENTS)
					&& !StringUtils.equals(finFeeDetail.getFeeScheduleMethod(),
							CalculationConstants.REMFEE_PAID_BY_CUSTOMER)
					&& !StringUtils.equals(finFeeDetail.getFeeScheduleMethod(),
							CalculationConstants.REMFEE_WAIVED_BY_BANK)) {
				String[] valueParm = new String[2];
				valueParm[0] = finFeeDetail.getFeeScheduleMethod();
				valueParm[1] = CalculationConstants.REMFEE_PART_OF_DISBURSE + ","
						+ CalculationConstants.REMFEE_PART_OF_SALE_PRICE + ","
						+ CalculationConstants.REMFEE_SCHD_TO_FIRST_INSTALLMENT + ","
						+ CalculationConstants.REMFEE_SCHD_TO_ENTIRE_TENOR + ","
						+ CalculationConstants.REMFEE_SCHD_TO_N_INSTALLMENTS + ","
						+ CalculationConstants.REMFEE_PAID_BY_CUSTOMER + ","
						+ CalculationConstants.REMFEE_WAIVED_BY_BANK;
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90243", valueParm)));
			}

			// validate scheduleTerms
			if (StringUtils.equals(CalculationConstants.REMFEE_SCHD_TO_N_INSTALLMENTS,
					finFeeDetail.getFeeScheduleMethod())
					&& finFeeDetail.getTerms() <= 0) {
				String[] valueParm = new String[1];
				valueParm[0] = "ScheduleTerms";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90221", valueParm)));
			}

			if (StringUtils.equals(CalculationConstants.REMFEE_SCHD_TO_N_INSTALLMENTS,
					finFeeDetail.getFeeScheduleMethod())
					&& finFeeDetail.getTerms() > finScheduleData.getFinanceMain().getNumberOfTerms()) {
				String[] valueParm = new String[2];
				valueParm[0] = "Schedule Terms";
				valueParm[1] = "Number of terms:" + finScheduleData.getFinanceMain().getNumberOfTerms();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("30568", valueParm)));
			}
		}
		for (FinFeeDetail feeDetail : finScheduleData.getFinFeeDetailList()) {
			boolean isVasFeeProduct = false;
			for (VASRecording vasRecording : finScheduleData.getVasRecordingList()) {
				if (StringUtils.equals(feeDetail.getFeeTypeCode(), "{" + vasRecording.getProductCode() + "}")) {
					isVasFeeProduct = true;
					// validate negative values
					if (feeDetail.getActualAmount().compareTo(BigDecimal.ZERO) < 0
							|| feeDetail.getPaidAmount().compareTo(BigDecimal.ZERO) < 0
							|| feeDetail.getWaivedAmount().compareTo(BigDecimal.ZERO) < 0) {
						String[] valueParm = new String[1];
						valueParm[0] = feeDetail.getFeeTypeCode();
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90259", valueParm)));
						return errorDetails;
					}

					// validate vas fee amount
					if(feeDetail.getActualAmount().compareTo(vasRecording.getFee()) != 0) {
						String[] valueParm = new String[3];
						valueParm[0] = "Fee amount";
						valueParm[1] = "VAS recording fee:" + String.valueOf(vasRecording.getFee());
						valueParm[2] = feeDetail.getFeeTypeCode();
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90268", valueParm)));
						return errorDetails;
					}
					// validate actual fee amount with waiver+paid amount
					BigDecimal remainingFee = feeDetail.getActualAmount().subtract(
							feeDetail.getWaivedAmount().add(feeDetail.getPaidAmount()));
					if (remainingFee.compareTo(BigDecimal.ZERO) < 0) {
						String[] valueParm = new String[3];
						valueParm[0] = "Sum of waiver and paid amounts";
						valueParm[1] = "Actual fee amount:" + String.valueOf(feeDetail.getActualAmount());
						valueParm[2] = feeDetail.getFeeTypeCode();
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90268", valueParm)));
						return errorDetails;
					}
				}
			}
			
			if(!isVasFeeProduct && StringUtils.contains(feeDetail.getFeeTypeCode(), "{")) {
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90326", null)));
				return errorDetails;
			}
		}
		return errorDetails;
	}

	private List<ErrorDetail> vasRecordingValidations(String vldGroup, FinScheduleData finScheduleData, boolean isAPICall, String string) {
		
		List<ErrorDetail> errorDetails = new ArrayList<ErrorDetail>();
		FinanceType financeType = finScheduleData.getFinanceType();
		//fetch the vasProduct list based on the FinanceType
		financeType.setFinTypeVASProductsList(finTypeVASProductsDAO.getVASProductsByFinType(financeType.getFinType(), ""));
		int mandatoryVasCount = 0;
		if (financeType.getFinTypeVASProductsList() != null) {
			for (FinTypeVASProducts vasProduct : financeType.getFinTypeVASProductsList()) {
				if (vasProduct.isMandatory()) {
					mandatoryVasCount++;
				}
			}
		}
		if (financeType.getFinTypeVASProductsList() != null && mandatoryVasCount > 0) {
			if (finScheduleData.getVasRecordingList() == null || finScheduleData.getVasRecordingList().isEmpty()) {
				String[] valueParm = new String[1];
				valueParm[0] = "VAS";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
				return errorDetails;
			}
		}
		if (finScheduleData.getVasRecordingList() != null && !finScheduleData.getVasRecordingList().isEmpty()) {
			int userVasCount = 0;

			boolean isVasProduct = false;
			if (financeType.getFinTypeVASProductsList() != null) {
				/*for (FinTypeVASProducts vasProduct : financeType.getFinTypeVASProductsList()) {
					if (vasProduct.isMandatory()) {
						mandatoryVasCount++;
					}
				}*/
				for (FinTypeVASProducts vasProduct : financeType.getFinTypeVASProductsList()) {
					for (VASRecording detail : finScheduleData.getVasRecordingList()) {
						if (StringUtils.equals(detail.getProductCode(), vasProduct.getVasProduct())) {
							isVasProduct = true;
							if (vasProduct.isMandatory()) {
								userVasCount++;
							}
						}

					}
				}
			}

			if (!isVasProduct) {
				String[] valueParm = new String[1];
				valueParm[0] = financeType.getFinType();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90284", valueParm)));
				return errorDetails;
			}
			if (userVasCount != mandatoryVasCount) {
				String[] valueParm = new String[1];
				valueParm[0] = financeType.getFinType();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90284", valueParm)));
				return errorDetails;
			}
			
			if (finScheduleData.getVasRecordingList() != null && !finScheduleData.getVasRecordingList().isEmpty()) {
			for (VASRecording detail : finScheduleData.getVasRecordingList()) {
				if (StringUtils.isBlank(detail.getProductCode())) {
					String[] valueParm = new String[1];
					valueParm[0] = "product";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
					return errorDetails;
				}
				if (detail.getFee() == null) {
					String[] valueParm = new String[1];
					valueParm[0] = "Fee";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
					return errorDetails;
				}

				VASConfiguration vASConfiguration = vASConfigurationService
						.getVASConfigurationByCode(detail.getProductCode());
				if (vASConfiguration == null || !vASConfiguration.isActive()) {
					String[] valueParm = new String[2];
					valueParm[0] = "Product";
					valueParm[1] = detail.getProductCode();
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90224", valueParm)));
					return errorDetails;
				}
				if(StringUtils.equals("Loan", detail.getPostingAgainst())){
					detail.setPostingAgainst(VASConsatnts.VASAGAINST_FINANCE);
				}
				if (!StringUtils.equals(vASConfiguration.getRecAgainst(), detail.getPostingAgainst())) {
					String[] valueParm = new String[2];
					valueParm[0] = "PostingAgainst";
					valueParm[1] = detail.getProductCode();
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90224", valueParm)));
					return errorDetails;
				}
				if (!vASConfiguration.isAllowFeeToModify()) {
					if (detail.getFee().compareTo(vASConfiguration.getVasFee()) != 0) {
						String[] valueParm = new String[2];
						valueParm[0] = "Fee:" + detail.getFee();
						valueParm[1] = "VasConfig Fee:" + vASConfiguration.getVasFee();
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("30570", valueParm)));
						return errorDetails;
					}
				} else if (detail.getFee().compareTo(BigDecimal.ZERO) < 0) {
					String[] valueParm = new String[2];
					valueParm[0] = "VAS Fee";
					valueParm[1] = "Zero";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90205", valueParm)));
					return errorDetails;
				}

				// validate FeePaymentMode
				if (StringUtils.isNotBlank(detail.getFeePaymentMode())) {
					List<ValueLabel> paymentModes = PennantStaticListUtil.getFeeTypes();
					boolean paymentSts = false;
					for (ValueLabel value : paymentModes) {
						if (StringUtils.equals(value.getValue(), detail.getFeePaymentMode())) {
							paymentSts = true;
							break;
						}
					}
					if (!paymentSts) {
						String[] valueParm = new String[3];
						valueParm[0] = "paymentMode";
						valueParm[1] = "paymentModes";
						valueParm[2] = FinanceConstants.RECFEETYPE_CASH + "," + FinanceConstants.RECFEETYPE_CHEQUE;
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90264", valueParm)));
						return errorDetails;
					}
				} else {
					String[] valueParm = new String[1];
					valueParm[0] = "feePaymentMode";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
					return errorDetails;
				}
				if (detail.getValueDate() == null) {
					detail.setValueDate(DateUtility.getAppDate());
				} else {
					if (detail.getValueDate().before(SysParamUtil.getValueAsDate(PennantConstants.APP_DFT_START_DATE))
							|| detail.getValueDate().after(DateUtility.getAppDate())) {
						String[] valueParm = new String[3];
						valueParm[0] = "Value Date";
						valueParm[1] = DateUtility.formatToLongDate(SysParamUtil.getValueAsDate(PennantConstants.APP_DFT_START_DATE));
						valueParm[2] = DateUtility.formatToLongDate(DateUtility.getAppDate());
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90318", valueParm)));
						return errorDetails;
					}
				}
				if (vASConfiguration.isFeeAccrued()) {
					if (detail.getAccrualTillDate() == null) {
						String[] valueParm = new String[1];
						valueParm[0] = "accrualTillDate";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
						return errorDetails;
					} else {
						if(detail.getAccrualTillDate().before(DateUtility.getAppDate())
								|| detail.getAccrualTillDate().after(SysParamUtil.getValueAsDate("APP_DFT_END_DATE"))){
									String[] valueParm = new String[3];
									valueParm[0] = "AccrualTillDate";
									valueParm[1] = DateUtility.formatToLongDate(DateUtility.getAppDate());
									valueParm[2] = DateUtility.formatToLongDate(SysParamUtil.getValueAsDate("APP_DFT_END_DATE"));
									errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90318", valueParm)));
									return errorDetails;
								}
					}
				} else {
					if(detail.getAccrualTillDate() != null){
						String[] valueParm = new String[2];
						valueParm[0] = "accrualTillDate";
						valueParm[1] = "FeeAccrued";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90298", "", valueParm), "EN"));
						return errorDetails;
					}
					detail.setAccrualTillDate(DateUtility.getAppDate());
				}
				if (vASConfiguration.isRecurringType()) {
					if (detail.getRecurringDate() == null) {
						String[] valueParm = new String[1];
						valueParm[0] = "recurringDate";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
						return errorDetails;
					}else {
						if(detail.getRecurringDate().before(DateUtility.getAppDate())
								|| detail.getRecurringDate().after(SysParamUtil.getValueAsDate("APP_DFT_END_DATE"))){
									String[] valueParm = new String[3];
									valueParm[0] = "RecurringDate";
									valueParm[2] = DateUtility.formatToLongDate(SysParamUtil.getValueAsDate("APP_DFT_END_DATE"));
									valueParm[1] = DateUtility.formatToLongDate(DateUtility.getAppDate());
									errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90318", "", valueParm)));
									return errorDetails;
								}
					}
				} else {
					if(detail.getRecurringDate() != null){
						String[] valueParm = new String[2];
						valueParm[0] = "RecurringDate";
						valueParm[1] = "RecurringType is Active";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90298", "", valueParm), "EN"));
						return errorDetails;
					}
					detail.setRecurringDate(DateUtility.getAppDate());
					detail.setRenewalFee(BigDecimal.ZERO);
				}
				if(StringUtils.isNotBlank(detail.getDsaId())){
					RelationshipOfficer relationshipOfficer = relationshipOfficerService
							.getApprovedRelationshipOfficerById(detail.getDsaId());
					if (relationshipOfficer == null) {
						String[] valueParm = new String[1];
						valueParm[0] = detail.getDsaId();
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90501", valueParm)));
						return errorDetails;
					}
				}
				if (StringUtils.isNotBlank(detail.getDmaId())) {
					RelationshipOfficer dmaCode = relationshipOfficerService
							.getApprovedRelationshipOfficerById(detail.getDmaId());
					if (dmaCode == null) {
						String[] valueParm = new String[1];
						valueParm[0] = detail.getDmaId();
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90501", valueParm)));
						return errorDetails;
					}
				}
				if (StringUtils.isNotBlank(detail.getFulfilOfficerId())) {
					RelationshipOfficer dmaCode = relationshipOfficerService
							.getRelationshipOfficerById(detail.getFulfilOfficerId());
					if (dmaCode == null) {
						String[] valueParm = new String[1];
						valueParm[0] = detail.getFulfilOfficerId();
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90501", "", valueParm), "EN"));
						return errorDetails;
					}
				}
				if (StringUtils.isNotBlank(detail.getReferralId())) {
					RelationshipOfficer referralId = relationshipOfficerService
							.getApprovedRelationshipOfficerById(detail.getReferralId());
					if (referralId == null) {
						String[] valueParm = new String[1];
						valueParm[0] = detail.getReferralId();
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90501", valueParm)));
						return errorDetails;
					}
				}
				detail.setFeeAccounting(vASConfiguration.getFeeAccounting());
				int extendedDetailsCount = 0;
				List<ExtendedFieldDetail> exdFldConfig = vASConfiguration.getExtendedFieldHeader().getExtendedFieldDetails();
				if (exdFldConfig != null) {
					for (ExtendedFieldDetail extended : exdFldConfig) {
						if (extended.isFieldMandatory()) {
							extendedDetailsCount++;
						}
					}
				}
				if (extendedDetailsCount > 0 && (detail.getExtendedDetails() == null || detail.getExtendedDetails().isEmpty())) {
					String[] valueParm = new String[1];
					valueParm[0] = "ExtendedDetails";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm)));
					 return errorDetails;
				}
				if (detail.getExtendedDetails() != null && !detail.getExtendedDetails().isEmpty()) {
					for (ExtendedField details : detail.getExtendedDetails()) {
						int exdMandConfigCount = 0;
						for (ExtendedFieldData extendedFieldData : details.getExtendedFieldDataList()) {
							if (StringUtils.isBlank(extendedFieldData.getFieldName())) {
								String[] valueParm = new String[1];
								valueParm[0] = "fieldName";
								errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
								return errorDetails;
							}
							if (StringUtils.isBlank(Objects.toString(extendedFieldData.getFieldValue(),""))) {
								String[] valueParm = new String[1];
								valueParm[0] = "fieldValue";
								errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
								return errorDetails;
							}
							boolean isFeild = false;
							if (vASConfiguration.getExtendedFieldHeader().getExtendedFieldDetails() != null) {
								for (ExtendedFieldDetail extendedDetail : vASConfiguration.getExtendedFieldHeader()
										.getExtendedFieldDetails()) {
									if (StringUtils.equals(extendedDetail.getFieldName(),
											extendedFieldData.getFieldName())) {
										if(extendedDetail.isFieldMandatory()) {
											exdMandConfigCount++;
										}
											List<ErrorDetail> errList = extendedFieldDetailsService
													.validateExtendedFieldData(extendedDetail, extendedFieldData);
										errorDetails.addAll(errList);
										isFeild = true;
									}
								}
								if (!isFeild) {
									String[] valueParm = new String[1];
									valueParm[0] = "vas setup";
									errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90265", valueParm)));
									return errorDetails;
								}
							}
						}
						if (extendedDetailsCount != exdMandConfigCount) {
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90297", "", null)));
							return errorDetails;
						}
					}

				}
				Map<String, Object> mapValues = new HashMap<String, Object>();
				if(detail.getExtendedDetails() != null){
				for (ExtendedField details : detail.getExtendedDetails()) {
					for (ExtendedFieldData extFieldData : details.getExtendedFieldDataList()) {
						for (ExtendedFieldDetail detail1 : exdFldConfig) {
							if (StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_BASERATE, detail1.getFieldType())
									&& StringUtils.equals(extFieldData.getFieldName(), detail1.getFieldName())) {
								extFieldData.setFieldName(extFieldData.getFieldName().concat("_BR"));
							}
							if (StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_PHONE, detail1.getFieldType())
									&& StringUtils.equals(extFieldData.getFieldName(), detail1.getFieldName())) {
								extFieldData.setFieldName(extFieldData.getFieldName().concat("_SC"));
							}
							mapValues.put(extFieldData.getFieldName(), extFieldData.getFieldValue());
						}
					}
				}
				}

				// do script pre validation and post validation
				ScriptErrors errors = null;
				if (vASConfiguration.isPostValidationReq()) {
					errors = scriptValidationService.getPostValidationErrors(vASConfiguration.getPostValidation(),
							mapValues);
				}
				if (errors != null) {
					List<ScriptError> errorsList = errors.getAll();
					for (ScriptError error : errorsList) {
						errorDetails.add(ErrorUtil
								.getErrorDetail(new ErrorDetail("", "90909", "", error.getValue(), null, null)));
						return errorDetails;
					}
				}
			}
		}
	}
		return errorDetails;

	}


	/**
	 * 
	 * @param finScheduleData
	 * @return
	 */
	private List<ErrorDetail> finODPenaltyRateValidation(FinScheduleData finScheduleData) {
		List<ErrorDetail> errorDetails = new ArrayList<ErrorDetail>();
		FinODPenaltyRate finODPenaltyRate = finScheduleData.getFinODPenaltyRate();
		if (!finScheduleData.getFinanceType().isApplyODPenalty() && finODPenaltyRate != null) {
			String[] valueParm = new String[2];
			valueParm[0] = "overdue";
			valueParm[1] = "loan type" + finScheduleData.getFinanceMain().getFinType();
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90204", valueParm)));
			return errorDetails;
		}
		if (finODPenaltyRate != null) {
			if(finODPenaltyRate.getODChargeAmtOrPerc() == null ){
				finODPenaltyRate.setODChargeAmtOrPerc(BigDecimal.ZERO);
			}
			if(finODPenaltyRate.getODMaxWaiverPerc() == null ){
				finODPenaltyRate.setODMaxWaiverPerc(BigDecimal.ZERO);
			}
			if (!finODPenaltyRate.isApplyODPenalty()) {
				if (finODPenaltyRate.isODIncGrcDays() || StringUtils.isNotBlank(finODPenaltyRate.getODChargeType())
						|| finODPenaltyRate.getODChargeAmtOrPerc().compareTo(BigDecimal.ZERO) > 0
						|| finODPenaltyRate.isODAllowWaiver()) {
					String[] valueParm = new String[1];
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90315", valueParm)));
				}
			} else {
				if (StringUtils.isBlank(finODPenaltyRate.getODChargeType())
						|| finODPenaltyRate.getODChargeAmtOrPerc().compareTo(BigDecimal.ZERO) <= 0) {
					String[] valueParm = new String[1];
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90314", valueParm)));
				}
				if (StringUtils.equals(finODPenaltyRate.getODChargeType(), FinanceConstants.PENALTYTYPE_FLAT)
						|| StringUtils.equals(finODPenaltyRate.getODChargeType(),
								FinanceConstants.PENALTYTYPE_FLAT_ON_PD_MTH)) {
					finODPenaltyRate.setODChargeCalOn("");
				}
				if ((StringUtils.isBlank(finODPenaltyRate.getODChargeCalOn()))&& (StringUtils.equals(finODPenaltyRate.getODChargeType(),
						FinanceConstants.PENALTYTYPE_PERC_ONETIME)|| StringUtils.equals(finODPenaltyRate.getODChargeType(),
						FinanceConstants.PENALTYTYPE_PERC_ON_DUEDAYS)|| StringUtils.equals(finODPenaltyRate.getODChargeType(),
						FinanceConstants.PENALTYTYPE_PERC_ON_PD_MTH))) {
						String[] valueParm = new String[2];
						valueParm[0] = "odChargeCalOn";
						valueParm[1] = "odChargeType"+ FinanceConstants.PENALTYTYPE_PERC_ONETIME +","
						+ FinanceConstants.PENALTYTYPE_PERC_ON_DUEDAYS +","+FinanceConstants.PENALTYTYPE_PERC_ON_PD_MTH;
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90298", valueParm)));
					} 
			}

			if (!(finODPenaltyRate.isApplyODPenalty() && finODPenaltyRate.isODAllowWaiver())) {
				if (finODPenaltyRate.getODMaxWaiverPerc().compareTo(BigDecimal.ZERO) > 0) {
					String[] valueParm = new String[2];
					valueParm[0] = "ODMaxWaiverPerc";
					valueParm[1] = "ODAllowWaiver is disabled";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90329", valueParm)));
				}
			} else {
				if (finODPenaltyRate.getODMaxWaiverPerc().compareTo(BigDecimal.ZERO) <= 0) {
					String[] valueParm = new String[2];
					valueParm[0] = "ODMaxWaiverPerc";
					valueParm[1] = "Zero";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("91121", valueParm)));
				} else {
					if (finODPenaltyRate.getODMaxWaiverPerc().compareTo(new BigDecimal(100)) > 0) {
						String[] valueParm = new String[2];
						valueParm[0] = "ODChargeAmtOrPerc";
						valueParm[1] = "100";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("30565", valueParm)));
					}
				}
			}
			if (StringUtils.equals(finODPenaltyRate.getODChargeType(), FinanceConstants.PENALTYTYPE_PERC_ONETIME)||
				StringUtils.equals(finODPenaltyRate.getODChargeType(), FinanceConstants.PENALTYTYPE_PERC_ON_DUEDAYS)
			 || StringUtils.equals(finODPenaltyRate.getODChargeType(),FinanceConstants.PENALTYTYPE_PERC_ON_PD_MTH)) {
				BigDecimal totPerc = PennantApplicationUtil.formateAmount(finODPenaltyRate.getODChargeAmtOrPerc(), 2);
				if (totPerc.compareTo(new BigDecimal(100)) > 0) {
					String[] valueParm = new String[2];
					valueParm[0] = "ODChargeAmtOrPerc";
					valueParm[1] = "100";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("30565", valueParm)));
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
					valueParm[1] = FinanceConstants.PENALTYTYPE_FLAT + "," + FinanceConstants.PENALTYTYPE_FLAT_ON_PD_MTH
							+ "," + FinanceConstants.PENALTYTYPE_PERC_ON_DUEDAYS + ","
							+ FinanceConstants.PENALTYTYPE_PERC_ON_PD_MTH + ","
							+ FinanceConstants.PENALTYTYPE_PERC_ONETIME;
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90316", valueParm)));
				}
			}

			if (StringUtils.isNotBlank(finODPenaltyRate.getODChargeCalOn())) {
				List<ValueLabel> finODCCalculatedOn = PennantStaticListUtil.getODCCalculatedOn();
				boolean finODCCalculatedOnSts = false;
				for (ValueLabel value : finODCCalculatedOn) {
					if (StringUtils.equals(value.getValue(), finODPenaltyRate.getODChargeCalOn())) {
						finODCCalculatedOnSts = true;
						break;
					}
				}
				if (!finODCCalculatedOnSts && (StringUtils.equals(finODPenaltyRate.getODChargeType(), FinanceConstants.PENALTYTYPE_PERC_ONETIME)||
						StringUtils.equals(finODPenaltyRate.getODChargeType(), FinanceConstants.PENALTYTYPE_PERC_ON_DUEDAYS)
						 || StringUtils.equals(finODPenaltyRate.getODChargeType(),FinanceConstants.PENALTYTYPE_PERC_ON_PD_MTH))) {
					String[] valueParm = new String[2];
					valueParm[0] = finODPenaltyRate.getODChargeCalOn();
					valueParm[1] = FinanceConstants.ODCALON_STOT + "," + FinanceConstants.ODCALON_SPFT + ","
							+ FinanceConstants.ODCALON_SPRI;
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90317", valueParm)));
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

		List<ErrorDetail> errorDetails = new ArrayList<ErrorDetail>();
		FinanceMain finMain = financeDetail.getFinScheduleData().getFinanceMain();
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		boolean isCreateLoan = false;

		if (StringUtils.equals(vldGroup, PennantConstants.VLD_CRT_LOAN)) {
			isCreateLoan = true;
		}
		
		if (StringUtils.equals(vldGroup, PennantConstants.VLD_UPD_LOAN)) {
			errorDetails = validateUpdateFinance(financeDetail);
			if (!errorDetails.isEmpty()) {
				finScheduleData.setErrorDetails(errorDetails);
				return finScheduleData;
			}
			return new FinScheduleData();
		}

		if(!financeDetail.isStp()){
			/*if(StringUtils.isBlank(financeDetail.getProcessStage())){
				String[] valueParm = new String[1];
				valueParm[0] = "ProcessStage";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90502", valueParm)));
				finScheduleData.setErrorDetails(errorDetails);
				return finScheduleData;
			}*/
			if (financeDetail.getFinScheduleData().getFinanceMain().isQuickDisb()) {
				String[] valueParm = new String[2];
				valueParm[0] = "QuickDisb";
				valueParm[1] = "stp";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90298", valueParm)));
				finScheduleData.setErrorDetails(errorDetails);
				return finScheduleData;
			}
		}
		// validate FinReference
		ErrorDetail error = validateFinReference(financeDetail.getFinScheduleData().getFinReference(), finScheduleData,vldGroup);
		if(error != null) {
			errorDetails.add(error);
		}

		// Validate customer
		if ((isCreateLoan || StringUtils.isNotBlank(finMain.getLovDescCustCIF()))) {
			Customer customer = customerDAO.getCustomerByCIF(finMain.getLovDescCustCIF(), "");
			if (customer == null) {
				String[] valueParm = new String[1];
				valueParm[0] = finMain.getLovDescCustCIF();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90101", valueParm)));
				finScheduleData.setErrorDetails(errorDetails);
				return finScheduleData;
			} else {
				CustomerDetails customerDetails = new CustomerDetails();
				customerDetails.setCustomer(customer);
				getFinanceDetail().setCustomerDetails(customerDetails);
				getFinanceDetail().getFinScheduleData().getFinanceMain().setCustID(customer.getCustID());
			}
		}

		FinanceType financeType = finScheduleData.getFinanceType();
		finScheduleData.setFinanceType(financeType);
		if (finMain.getFinContractDate() == null) {
			finMain.setFinContractDate(financeType.getStartDate());
		} else {
			if (finMain.getFinContractDate().compareTo(finMain.getFinStartDate()) > 0) {
				String[] valueParm = new String[2];
				valueParm[0] = DateUtility.formatDate(finMain.getFinContractDate(), PennantConstants.XMLDateFormat);
				valueParm[1] = DateUtility.formatDate(finMain.getFinStartDate(), PennantConstants.XMLDateFormat);
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("65030", valueParm)));
			}
		}
		if (financeType.isLimitRequired() && ImplementationConstants.LIMIT_INTERNAL) {
			/*
			 * if (StringUtils.isBlank(finMain.getFinLimitRef())) { String[] valueParm = new String[1]; valueParm[0]
			 * = "finLimitRef"; errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90502", valueParm))); }
			 * else { //TODO }
			 */
		}
		if(financeType.isFinCollateralReq()){
			if(financeDetail.getCollateralAssignmentList()==null ||  financeDetail.getCollateralAssignmentList().isEmpty()){
				String[] valueParm = new String[1];
				valueParm[0] = "Collateral";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
			}
		}

		if (StringUtils.equals(finMain.getFinRepayMethod(), FinanceConstants.REPAYMTH_AUTO)) {
			if (StringUtils.isBlank(finMain.getRepayAccountId())) {
				String[] valueParm = new String[1];
				valueParm[0] = "repayAccountId";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
			}
		}
		if (financeType.isFinDepreciationReq()) {
			if (StringUtils.isBlank(finMain.getDepreciationFrq())) {
				String[] valueParm = new String[1];
				valueParm[0] = "depreciationFrq";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
			} else {
				ErrorDetail errorDetail = FrequencyUtil.validateFrequency(finMain.getDepreciationFrq());
				if (errorDetail != null && StringUtils.isNotBlank(errorDetail.getCode())) {
					String[] valueParm = new String[1];
					valueParm[0] = finMain.getDepreciationFrq();
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90207", valueParm)));
				}
			}
		}
		if (StringUtils.isNotBlank(finMain.getDsaCode())) {
			RelationshipOfficer relationshipOfficer = relationshipOfficerService
					.getApprovedRelationshipOfficerById(finMain.getDsaCode());
			if (relationshipOfficer == null) {
				String[] valueParm = new String[1];
				valueParm[0] = finMain.getDsaCode();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90501", valueParm)));
			}
		}
		if (finMain.getAccountsOfficer() != 0) {
			VehicleDealer vehicleDealer = vehicleDealerService.getApprovedVehicleDealerById(finMain
					.getAccountsOfficer());
			if (vehicleDealer == null) {
				String[] valueParm = new String[1];
				valueParm[0] = String.valueOf(finMain.getAccountsOfficer());
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90501", valueParm)));
			}
		}
		if (StringUtils.isNotBlank(finMain.getSalesDepartment())) {
			RelationshipOfficer relationshipOfficer = relationshipOfficerService.getApprovedRelationshipOfficerById(finMain
					.getSalesDepartment());
			if (relationshipOfficer == null) {
				String[] valueParm = new String[1];
				valueParm[0] = finMain.getSalesDepartment();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90501", valueParm)));
			}
		}
		if (StringUtils.isNotBlank(finMain.getDmaCode())) {
			RelationshipOfficer relationshipOfficer = relationshipOfficerService
					.getApprovedRelationshipOfficerById(finMain.getDmaCode());
			if (relationshipOfficer == null) {
				String[] valueParm = new String[1];
				valueParm[0] = finMain.getDsaCode();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90501", valueParm)));
			}
		}
		if (StringUtils.isNotBlank(finMain.getReferralId())) {
			RelationshipOfficer relationshipOfficer = relationshipOfficerService
					.getApprovedRelationshipOfficerById(finMain.getReferralId());
			if (relationshipOfficer == null) {
				String[] valueParm = new String[1];
				valueParm[0] = finMain.getDsaCode();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90501", valueParm)));
			}
		}

		// validate finance branch
		if (isCreateLoan || StringUtils.isNotBlank(finMain.getFinBranch())) {
			Branch branch = branchDAO.getBranchById(finMain.getFinBranch(), "");
			if (branch == null) {
				String[] valueParm = new String[1];
				valueParm[0] = finMain.getFinBranch();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90129", valueParm)));
			}
		}
		finScheduleData.setErrorDetails(errorDetails);
 
		//Validate Repayment Method
		if (isCreateLoan) {
			String repayMethod = finMain.getFinRepayMethod();

			// finRepay method
			if (StringUtils.isNotBlank(repayMethod)) {
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
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90307", valueParm)));
				}
			}

			if (!errorDetails.isEmpty()) {
				finScheduleData.setErrorDetails(errorDetails);
				return finScheduleData;
			}

			errorDetails = disbursementValidation(financeDetail);
			if (!errorDetails.isEmpty()) {
				finScheduleData.setErrorDetails(errorDetails);
				return finScheduleData;
			}

			errorDetails = mandateValidation(financeDetail);
			if (!errorDetails.isEmpty()) {
				finScheduleData.setErrorDetails(errorDetails);
				return finScheduleData;
			}

			errorDetails = documentValidation(financeDetail);
			if (!errorDetails.isEmpty()) {
				finScheduleData.setErrorDetails(errorDetails);
				return finScheduleData;
			}

			errorDetails = jointAccountDetailsValidation(financeDetail);
			if (!errorDetails.isEmpty()) {
				finScheduleData.setErrorDetails(errorDetails);
				return finScheduleData;
			}
			errorDetails = gurantorsDetailValidation(financeDetail);
			if (!errorDetails.isEmpty()) {
				finScheduleData.setErrorDetails(errorDetails);
				return finScheduleData;
			}
			errorDetails = finFlagsDetailValidation(financeDetail);
			if (!errorDetails.isEmpty()) {
				finScheduleData.setErrorDetails(errorDetails);
				return finScheduleData;
			}
			errorDetails = finCollateralValidation(financeDetail);
			if (!errorDetails.isEmpty()) {
				finScheduleData.setErrorDetails(errorDetails);
				return finScheduleData;
			}
			
			errorDetails = finTaxDetailValidation(financeDetail);
			if (!errorDetails.isEmpty()) {
				finScheduleData.setErrorDetails(errorDetails);
				return finScheduleData;
			}
			
			//ExtendedFieldDetails Validation
			String subModule = financeDetail.getFinScheduleData().getFinanceMain().getFinCategory();
			//### 02-05-2018-Start- story #334 Extended fields for loan servicing
			errorDetails = extendedFieldDetailsService.validateExtendedFieldDetails(financeDetail.getExtendedDetails(),
					ExtendedFieldConstants.MODULE_LOAN, subModule, FinanceConstants.FINSER_EVENT_ORG);
			//### 02-05-2018-END
			if (!errorDetails.isEmpty()) {
				finScheduleData.setErrorDetails(errorDetails);
				return finScheduleData;
			}
		}

		return finScheduleData;

	}

	/**
	 * Method for validating finance reference
	 * 
	 * @param finReference
	 * @param finScheduleData
	 * @return
	 */
	private ErrorDetail validateFinReference(String finReference, FinScheduleData finScheduleData, String vldGroup ) {
		ErrorDetail errorDetail = null;
		if (!finScheduleData.getFinanceType().isFinIsGenRef()) {
			if (StringUtils.isBlank(finReference)) {
				String[] valueParm = new String[2];
				valueParm[0] = "FinReference";
				errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm));
			} else {
				if (finReference.length() > LengthConstants.LEN_REF) {
					String[] valueParm = new String[2];
					valueParm[0] = "FinReference";
					valueParm[1] = LengthConstants.LEN_REF + " characters";
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("30568", valueParm));
				}
				boolean exists = financeDetailService.isFinReferenceExits(finReference, "_View",
						StringUtils.equals(PennantConstants.VLD_CRT_LOAN, vldGroup) ? false : true);
				if (exists) {
					String[] valueParm = new String[2];
					valueParm[0] = "FinReference";
					valueParm[1] = finReference;
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("30506", valueParm));
				}
			}
		}
		finScheduleData.getFinanceMain().setFinReference(finReference);
		return errorDetail;
	}

	/**
	 * Method for validate Update Finance details
	 * @param financeDetail 
	 * 
	 * @return
	 */
	private List<ErrorDetail> validateUpdateFinance(FinanceDetail financeDetail) {
		List<ErrorDetail> errorDetails = new ArrayList<>();

		String type = TableType.TEMP_TAB.getSuffix();
		FinanceMain finMain = financeMainDAO.getFinanceMainById(financeDetail.getFinReference(), type, false);
		if(finMain == null) {
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90409", null)));
			return errorDetails;
		}
		// fetch Finance Schedule details
		FinScheduleData finScheduleData = financeDetailService.getFinSchDataById(finMain.getFinReference(), type, false);
		financeDetail.setFinScheduleData(finScheduleData);
		
		// validate disbursement details
		if(financeDetail.getAdvancePaymentsList() != null && !financeDetail.getAdvancePaymentsList().isEmpty()) {
			errorDetails = disbursementValidation(financeDetail);
			if (!errorDetails.isEmpty()) {
				return errorDetails;
			}
		}
		
		// validate Mandate details
		if(financeDetail.getMandate() != null) {
			errorDetails = mandateValidation(financeDetail);
			if (!errorDetails.isEmpty()) {
				return errorDetails;
			}
		}

		//Extended Field Details Validation
		if(financeDetail.getExtendedDetails() != null && !financeDetail.getExtendedDetails().isEmpty()) {
			String subModule = financeDetail.getFinScheduleData().getFinanceMain().getFinCategory();
			errorDetails = extendedFieldDetailsService.validateExtendedFieldDetails(financeDetail.getExtendedDetails(),
					ExtendedFieldConstants.MODULE_LOAN,  subModule, FinanceConstants.FINSER_EVENT_ORG);
			if (!errorDetails.isEmpty()) {
				return errorDetails;
			}
		}
		
		//Finance document details Validation
		if(financeDetail.getDocumentDetailsList() != null && !financeDetail.getDocumentDetailsList().isEmpty()) {
			errorDetails = documentService.validateFinanceDocuments(financeDetail);
			if (!errorDetails.isEmpty()) {
				return errorDetails;
			}
		}
		return errorDetails;
	}

	private List<ErrorDetail> finCollateralValidation(FinanceDetail financeDetail) {
		List<ErrorDetail> errorDetails = new ArrayList<ErrorDetail>();
		List<CollateralAssignment> finCollateralAssignmentDetails = financeDetail.getCollateralAssignmentList();
		if (finCollateralAssignmentDetails != null && !finCollateralAssignmentDetails.isEmpty() ) {
			boolean finColltReq = financeDetail.getFinScheduleData().getFinanceType().isFinCollateralReq();
			if (!finColltReq) {
				String[] valueParm = new String[2];
				valueParm[0] = App.getLabel("label_Collateral");
				valueParm[1] = financeDetail.getFinScheduleData().getFinanceMain().getFinType();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90329", valueParm)));
				return errorDetails;
			}
			for (CollateralAssignment collateralAssignment : finCollateralAssignmentDetails) {
				if (StringUtils.isEmpty(collateralAssignment.getCollateralRef())) {
					String[] valueParm = new String[1];
					valueParm[0] = "collateralRef";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
					return errorDetails;
				}
				BigDecimal assignPerc = collateralAssignment.getAssignPerc() == null ? BigDecimal.ZERO
						: collateralAssignment.getAssignPerc();
				if (assignPerc.compareTo(BigDecimal.ZERO) <= 0) {
					String[] valueParm = new String[2];
					valueParm[0] = "assignPerc";
					valueParm[1] = "1";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90205", valueParm)));
					return errorDetails;
				}
				CollateralSetup collateralSetup = collateralSetupService
						.getApprovedCollateralSetupById(collateralAssignment.getCollateralRef());
				if (collateralSetup == null) {
					String[] valueParm = new String[1];
					valueParm[0] = collateralAssignment.getCollateralRef();
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90906", valueParm)));
					return errorDetails;
				}
				String collateralType = financeDetail.getFinScheduleData().getFinanceType().getCollateralType();
				if (StringUtils.isNotBlank(collateralType)) {
					boolean isCollateralFound = false;
					String[] types = collateralType.split(PennantConstants.DELIMITER_COMMA);
					for (String type : types) {
						if (StringUtils.equals(type, collateralSetup.getCollateralType())) {
							isCollateralFound = true;
						}
					}
					if (!isCollateralFound) {
						String[] valueParm = new String[2];
						valueParm[0] = "collateralref";
						valueParm[1] = "LoanType";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90329", valueParm)));
						return errorDetails;
					}
				}
				if (!StringUtils.equalsIgnoreCase(collateralSetup.getDepositorCif(),
						financeDetail.getFinScheduleData().getFinanceMain().getLovDescCustCIF())) {
					boolean isNotValidCif = true;
					if (collateralSetup.getCollateralThirdPartyList() != null
							&& !collateralSetup.getCollateralThirdPartyList().isEmpty()) {
						for (CollateralThirdParty collateralThirdParty : collateralSetup
								.getCollateralThirdPartyList()) {
							if (StringUtils.equalsIgnoreCase(collateralThirdParty.getCustCIF(),
									financeDetail.getFinScheduleData().getFinanceMain().getLovDescCustCIF())) {
								isNotValidCif = false;
								break;
							}
						}
					}

					if (isNotValidCif) {
						String[] valueParm = new String[2];
						valueParm[0] = collateralSetup.getDepositorCif();
						valueParm[1] = financeDetail.getFinScheduleData().getFinanceMain().getLovDescCustCIF();
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90249", valueParm)));
						return errorDetails;
					}

				}
				BigDecimal totAssignedPerc = collateralSetupService.getAssignedPerc(collateralSetup.getCollateralRef(), "");
				BigDecimal curAssignValue = collateralSetup.getBankValuation()
						.multiply(collateralAssignment.getAssignPerc() == null ? BigDecimal.ZERO
								: collateralAssignment.getAssignPerc())
						.divide(new BigDecimal(100), 0, RoundingMode.HALF_DOWN);
				BigDecimal totAssignedValue = collateralSetup.getBankValuation().multiply(totAssignedPerc)
						.divide(new BigDecimal(100), 0, RoundingMode.HALF_DOWN);
				BigDecimal availAssignValue = collateralSetup.getBankValuation().subtract(totAssignedValue);
				if (availAssignValue.compareTo(curAssignValue) < 0) {
					String[] valueParm = new String[2];
					valueParm[0] = "Collateral available assign value(" + String.valueOf(availAssignValue) + ")";
					valueParm[1] = "current assign value(" + String.valueOf(curAssignValue) + ")";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("65012", valueParm)));
					return errorDetails;
				}

				if (availAssignValue.compareTo(financeDetail.getFinScheduleData().getFinanceMain().getFinAmount()) < 0) {
					String[] valueParm = new String[2];
					valueParm[0] = "Available assign value(" + String.valueOf(availAssignValue) + ")";
					valueParm[1] = "loan amount("
							+ String.valueOf(financeDetail.getFinScheduleData().getFinanceMain().getFinAmount()) + ")";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("65012", valueParm)));
					return errorDetails;
				}
			}
		}
		return errorDetails;
	}

	private List<ErrorDetail> finFlagsDetailValidation(FinanceDetail financeDetail) {
		List<ErrorDetail> errorDetails = new ArrayList<ErrorDetail>();
		// validate flags details
		List<FinFlagsDetail> finFlagDetails = financeDetail.getFinFlagsDetails();
		if (finFlagDetails != null) {
			for (FinFlagsDetail flag : finFlagDetails) {
				Flag flagDetail = flagDAO.getFlagById(flag.getFlagCode(), "");
				if (flagDetail == null || !flagDetail.isActive()) {
					String[] valueParm = new String[1];
					valueParm[0] = flag.getFlagCode();
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("91001", valueParm)));
				}
			}
		}
		return errorDetails;
	}

	private List<ErrorDetail> gurantorsDetailValidation(FinanceDetail financeDetail) {
		List<ErrorDetail> errorDetails = new ArrayList<ErrorDetail>();
		List<GuarantorDetail> guarantorDetails = financeDetail.getGurantorsDetailList();
		if (guarantorDetails != null) {
			for (GuarantorDetail detail : guarantorDetails) {
				if(detail.getGuranteePercentage().compareTo(new BigDecimal(100)) == 1) {
					String[] valueParm = new String[2];
					valueParm[0] = "GuranteePercentage";
					valueParm[1] = "100";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("30508", valueParm)));
					return errorDetails;
				}
				if (detail.isBankCustomer()) {
					String guarantorCIF = detail.getGuarantorCIF();
					if (StringUtils.equals(guarantorCIF, financeDetail.getFinScheduleData().getFinanceMain()
							.getLovDescCustCIF())) {
						String[] valueParm = new String[2];
						valueParm[0] = guarantorCIF;
						valueParm[1] = "guarantor";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90250", valueParm)));
						return errorDetails;
					}
					Customer guarantor = customerDetailsService.getCustomerByCIF(guarantorCIF);
					if (guarantor == null) {
						String[] valueParm = new String[1];
						valueParm[0] = guarantorCIF;
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90103", valueParm)));
					} else {
						detail.setGuarantorIDNumber(guarantor.getCustCRCPR());
						detail.setMobileNo(guarantor.getPhoneNumber());
						detail.setEmailId(guarantor.getEmailID());
						detail.setCustID(guarantor.getCustID());
					}
				} else {
					//validate Phone number
					String mobileNumber = detail.getMobileNo();
					if (StringUtils.isNotBlank(mobileNumber)) {
						if (!(mobileNumber.matches("\\d{10}"))) {
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90278", null)));
							return errorDetails;
						}
					}
					 boolean validRegex =  EmailValidator.getInstance().isValid(detail.getEmailId());
						if(!validRegex){
							String[] valueParm = new String[1];
							valueParm[0] = detail.getEmailId();
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90237", valueParm)));
							return errorDetails;
						}
					Province province = provinceDAO.getProvinceById(detail.getAddrCountry(),
							detail.getAddrProvince(), "");
					if (province == null) {
						String[] valueParm = new String[2];
						valueParm[0] = detail.getAddrProvince();
						valueParm[1] = detail.getAddrCountry();
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90701", valueParm)));
						return errorDetails;
					}
					City city = cityDAO.getCityById(detail.getAddrCountry(), detail.getAddrProvince(),
							detail.getAddrCity(), "");
					if (city == null) {
						String[] valueParm = new String[2];
						valueParm[0] = detail.getAddrCity();
						valueParm[1] = detail.getAddrProvince();
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90701", valueParm)));
						return errorDetails;
					}
				}
			}
		}
		return errorDetails;
	}

	private List<ErrorDetail> jointAccountDetailsValidation(FinanceDetail financeDetail) {
		List<ErrorDetail> errorDetails = new ArrayList<ErrorDetail>();
		List<JointAccountDetail> jountAccountDetails = financeDetail.getJountAccountDetailList();
		if (jountAccountDetails != null) {
			for (JointAccountDetail jointAccDetail : jountAccountDetails) {
				if (jointAccDetail.isIncludeRepay()) {
					if (StringUtils.isBlank(jointAccDetail.getRepayAccountId())) {
						String[] valueParm = new String[2];
						valueParm[0] = "RepayAccountId";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
						return errorDetails;
					}
				}else if(StringUtils.isNotBlank(jointAccDetail.getRepayAccountId())){
					String[] valueParm = new String[2];
					valueParm[0] = "RepayAccountId";
					valueParm[1] = "includeRepay";
					//{0} is only applicable for {1} customer.
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90124", valueParm)));
				}
				if (StringUtils.equals(jointAccDetail.getCustCIF(), 
						financeDetail.getFinScheduleData().getFinanceMain().getLovDescCustCIF())) {
					String[] valueParm = new String[2];
					valueParm[0] = jointAccDetail.getCustCIF();
					valueParm[1] = "co-applicant";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90250", valueParm)));
					return errorDetails;
				}
				Customer coApplicant = customerDetailsService.getCustomerByCIF(jointAccDetail.getCustCIF());
				if (coApplicant == null) {
					String[] valueParm = new String[1];
					valueParm[0] = jointAccDetail.getCustCIF();
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90102", valueParm)));
				}
				//for authoritySignatory and sequence		
				if (jointAccDetail.isAuthoritySignatory()) {

					if (jointAccDetail.getSequence() <= 0 || jointAccDetail.getSequence() >= 10) {
						//{0} should between or including {1} and {2}.
						String[] valueParm = new String[3];
						valueParm[0] = "sequence";
						valueParm[1] = "1";
						valueParm[2] = "9";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90282", valueParm)));

					}
				} else if (jointAccDetail.getSequence() != 0) {
					//{0} is only applicable for {1}.
					String[] valueParm = new String[2];
					valueParm[0] = "sequence";
					valueParm[1] = "authoritySignatory";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90298", valueParm)));
				}
				int duplicateSeqCount = 0;
				int duplicateCifCount = 0;
				for (JointAccountDetail detail : jountAccountDetails) {
					if (jointAccDetail.getSequence() == detail.getSequence() && detail.getSequence() != 0) {
						duplicateSeqCount++;
					}
					if (StringUtils.equals(jointAccDetail.getCustCIF(), detail.getCustCIF())) {
						duplicateCifCount++;
					}
				}
				//Duplicate {0} are not allowed.
				if (duplicateSeqCount >= 2) {
					String[] valueParm = new String[1];
					valueParm[0] = "sequence id";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90273", valueParm)));
				}
				//Duplicate {0} are not allowed.
				if (duplicateCifCount >= 2) {
					String[] valueParm = new String[1];
					valueParm[0] = "CIF";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90273", valueParm)));
				}
			}
		}
		return errorDetails;
	}

	private List<ErrorDetail> documentValidation(FinanceDetail financeDetail) {
		List<ErrorDetail> errorDetails = new ArrayList<ErrorDetail>();
		// validate document details
		List<DocumentDetails> documentDetails = financeDetail.getDocumentDetailsList();
		AuditDetail auditDetails = null;
		if (documentDetails != null) {
			for (DocumentDetails detail : documentDetails) {
				//validate Dates
				if (detail.getCustDocIssuedOn() != null && detail.getCustDocExpDate() != null) {
					if (detail.getCustDocIssuedOn().compareTo(detail.getCustDocExpDate()) > 0) {
						String[] valueParm = new String[2];
						valueParm[0] = "custDocExpDate: " +DateUtility.formatDate(detail.getCustDocExpDate(),
								PennantConstants.XMLDateFormat);
						valueParm[1] = "custDocIssuedOn: " +DateUtility.formatDate(detail.getCustDocIssuedOn(),
								PennantConstants.XMLDateFormat);
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("65030", valueParm)));
						return errorDetails;
					}
				}

				DocumentType docType = documentTypeService.getDocumentTypeById(detail.getDocCategory());
				if (docType == null) {
					String[] valueParm = new String[1];
					valueParm[0] = detail.getDocCategory();
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90401", valueParm)));
					return errorDetails;
				}

/*				//validate PAN
				Customer customer = financeDetail.getCustomerDetails().getCustomer();
				if(customer != null) {
					if(StringUtils.equals("03", detail.getDocCategory())){
						if(!StringUtils.equals(detail.getCustDocTitle(), customer.getCustCRCPR())){
							String[] valueParm = new String[1];
							valueParm[0] = customer.getCustCRCPR();
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90296", valueParm)));
							return errorDetails;
						}
					}
				}*/

				// validate Is Customer document?
				if (DocumentCategories.CUSTOMER.getKey().equals(docType.getCategoryCode())) {
					CustomerDocument custDocs = new CustomerDocument();
					custDocs.setCustDocCategory(detail.getDocCategory());
					custDocs.setCustDocName(detail.getDocName());
					custDocs.setCustDocIssuedOn(detail.getCustDocIssuedOn());
					custDocs.setCustDocExpDate(detail.getCustDocExpDate());
					custDocs.setCustDocTitle(detail.getCustDocTitle());
					custDocs.setCustDocIssuedCountry(detail.getCustDocIssuedCountry());
					custDocs.setCustDocSysName(detail.getCustDocSysName());
					custDocs.setCustDocIssuedOn(detail.getCustDocIssuedOn());
					custDocs.setCustDocExpDate(detail.getCustDocExpDate());
					custDocs.setDocUri(detail.getDocUri());
					custDocs.setCustDocImage(detail.getDocImage());
					custDocs.setCustDocType(detail.getDoctype());
					Customer cust = financeDetail.getCustomerDetails().getCustomer();
					auditDetails = customerDocumentService.validateCustomerDocuments(custDocs, cust);
				}

				// validate finance documents
				if (!(DocumentCategories.CUSTOMER.getKey().equals(docType.getCategoryCode())) && docType.isDocIsMandatory()) {
					if (StringUtils.isBlank(detail.getDocUri())) {
						if (detail.getDocImage() == null || detail.getDocImage().length <= 0) {
							String[] valueParm = new String[2];
							valueParm[0] = "docContent";
							valueParm[1] = "docRefId";
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90123", valueParm)));
						}
					}
					if (StringUtils.isBlank(detail.getDocName())) {
						String[] valueParm = new String[1];
						valueParm[0] = "docName";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
					}
					if (StringUtils.isBlank(detail.getDoctype())) {
						String[] valueParm = new String[1];
						valueParm[0] = "docFormat";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
					} else if(!StringUtils.equalsIgnoreCase(detail.getDoctype(), "jpg") 
							&& !StringUtils.equalsIgnoreCase(detail.getDoctype(), "png")
							&& !StringUtils.equalsIgnoreCase(detail.getDoctype(), "pdf")) {
						String[] valueParm = new String[1];
						valueParm[0] = "docFormat, Available formats are jpg,png,PDF";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90122", valueParm)));
					}
					
					//TODO: Need to add password protected field in documentdetails
				}
				
				if (StringUtils.equals(detail.getDocCategory(), "03")) {
					Pattern pattern = Pattern.compile("^[A-Za-z]{5}\\d{4}[A-Za-z]{1}");
					if(detail.getCustDocTitle() != null){
						Matcher matcher = pattern.matcher(detail.getCustDocTitle());
						if (matcher.find() == false) {
							String[] valueParm = new String[0];
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90251", valueParm)));
						}
					}
				}
			}
		}
		if (auditDetails != null && auditDetails.getErrorDetails() != null
				&& !auditDetails.getErrorDetails().isEmpty()) {
			return auditDetails.getErrorDetails();
		}
		return errorDetails;
	}

	private List<ErrorDetail> mandateValidation(FinanceDetail financeDetail) {
		List<ErrorDetail> errorDetails = new ArrayList<ErrorDetail>();
		Mandate mandate = financeDetail.getMandate();
		// if it is stp process mandate is mandatory
		if (financeDetail.isStp() && mandate == null) {
			String[] valueParm = new String[1];
			valueParm[0] = "Mandate";
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
			return errorDetails;
		}
		// Validate mandate details
		if (mandate != null) {
			if(StringUtils.equals(financeDetail.getFinScheduleData().getFinanceMain().getFinRepayMethod(), 
					FinanceConstants.REPAYMTH_MANUAL)){
				String[] valueParm = new String[2];
				valueParm[0] = "Mandate";
				valueParm[1] = "finRepayMethod is "+ FinanceConstants.REPAYMTH_MANUAL;
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90329", valueParm)));
				return errorDetails;
			}
			if (mandate.isUseExisting()) {
				if (mandate.getMandateID() == Long.MIN_VALUE) {
					String[] valueParm = new String[1];
					valueParm[0] = "MandateID";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
					return errorDetails;
				} else {
					Mandate curMandate = mandateService.getApprovedMandateById(mandate.getMandateID());
					if (curMandate == null) {
						String[] valueParm = new String[1];
						valueParm[0] = String.valueOf(mandate.getMandateID());
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90303", valueParm)));
						return errorDetails;
					} else {
						if (!StringUtils.equalsIgnoreCase(curMandate.getCustCIF(),
								financeDetail.getFinScheduleData().getFinanceMain().getLovDescCustCIF())) {
							String[] valueParm = new String[2];
							valueParm[0] = financeDetail.getFinScheduleData().getFinanceMain().getLovDescCustCIF();
							valueParm[1] = curMandate.getCustCIF();
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90310", valueParm)));
							return errorDetails;
						}
						if (!StringUtils.equalsIgnoreCase(curMandate.getMandateType(),
								financeDetail.getFinScheduleData().getFinanceMain().getFinRepayMethod())) {
							String[] valueParm = new String[2];
							valueParm[0] = financeDetail.getFinScheduleData().getFinanceMain().getFinRepayMethod();
							valueParm[1] = curMandate.getMandateType();
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90311", valueParm)));
							return errorDetails;
						}
						if (!(curMandate.isOpenMandate() || (curMandate.getOrgReference() == null))) {
							String[] valueParm = new String[1];
							valueParm[0] = String.valueOf(mandate.getMandateID());
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90312", valueParm)));
							return errorDetails;
						}
						if (!curMandate.isActive()) {
							String[] valueParm = new String[2];
							valueParm[0] = "mandate:";
							valueParm[1] = String.valueOf(mandate.getMandateID());
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("81004", valueParm)));
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
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
				}

				if (StringUtils.isBlank(mandate.getIFSC())) {
					if ((StringUtils.isBlank(mandate.getBankCode()) || StringUtils.isBlank(mandate.getBranchCode()))) {
						String[] valueParm = new String[1];
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90313", valueParm)));
					}
				}
				if (StringUtils.isBlank(mandate.getAccType())) {
					String[] valueParm = new String[1];
					valueParm[0] = "accType";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
				}
				if (StringUtils.isBlank(mandate.getAccNumber())) {
					String[] valueParm = new String[1];
					valueParm[0] = "accNumber";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
					return errorDetails;
				}
				if(ImplementationConstants.ALLOW_BARCODE) {
					if(StringUtils.isBlank(mandate.getBarCodeNumber())) {
						String[] valueParm = new String[1];
						valueParm[0] = "BarCodeNumber";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
						return errorDetails;
					}
				}
				if (mandate.getAccNumber().length() > 50) {
					String[] valueParm = new String[2];
					valueParm[0] = "accNumber length";
					valueParm[1] = "50";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("30568", valueParm)));
					return errorDetails;
				}
				if (StringUtils.isBlank(mandate.getAccHolderName())) {
					String[] valueParm = new String[1];
					valueParm[0] = "accHolderName";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
				}
				if (mandate.getStartDate() == null) {
					String[] valueParm = new String[1];
					valueParm[0] = "startDate";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
					return errorDetails;
				}
				if (!mandate.isOpenMandate()) {
					if (mandate.getExpiryDate() == null) {
						String[] valueParm = new String[1];
						valueParm[0] = "expiryDate";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
						return errorDetails;
					}
				} else {
					if (mandate.getExpiryDate() != null) {
						String[] valueParm = new String[2];
						valueParm[0] = "expiryDate";
						valueParm[1] = "open mandate";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90329", valueParm)));
						return errorDetails;
					}
				}

				//barcode
				if(StringUtils.isNotBlank(mandate.getBarCodeNumber())) {
					Pattern pattern = Pattern.compile(PennantRegularExpressions.getRegexMapper(PennantRegularExpressions.REGEX_BARCODE_NUMBER));
					Matcher matcher = pattern.matcher(mandate.getBarCodeNumber());
					
					if (matcher.matches() == false) {
						String[] valueParm = new String[1];
						valueParm[0] = mandate.getBarCodeNumber();
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("barCodeNumber", "90404", valueParm, valueParm)));
						return errorDetails;
					}
				}
				if(mandate.getMaxLimit() == null) {
					String[] valueParm = new String[1];
					valueParm[0] = "maxLimit";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90242", valueParm)));
					return errorDetails;
				}

				if (mandate.getMaxLimit().compareTo(BigDecimal.ZERO) <= 0) {
					String[] valueParm = new String[2];
					valueParm[0] = "maxLimit";
					valueParm[1] = "0";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("91121", valueParm)));
					return errorDetails;
				}

				if(mandate.getExpiryDate() != null){
				if (mandate.getExpiryDate().compareTo(mandate.getStartDate()) <= 0
						|| mandate.getExpiryDate().after(SysParamUtil.getValueAsDate("APP_DFT_END_DATE"))) {
					String[] valueParm = new String[3];
					valueParm[0] = "Mandate ExpiryDate";
					valueParm[1] = DateUtility.formatToLongDate(DateUtility.addDays(mandate.getStartDate(), 1));
					valueParm[2] = DateUtility.formatToLongDate(SysParamUtil.getValueAsDate("APP_DFT_END_DATE"));
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90318", valueParm)));
					return errorDetails;
				}	
				}
				if (mandate.getStartDate() != null) {
					Date mandbackDate = DateUtility.addDays(DateUtility.getAppDate(),
							-SysParamUtil.getValueAsInt("MANDATE_STARTDATE"));
					if (mandate.getStartDate().before(mandbackDate)
							|| mandate.getStartDate().after(SysParamUtil.getValueAsDate("APP_DFT_END_DATE"))) {
						String[] valueParm = new String[3];
						valueParm[0] = "mandate start date";
						valueParm[1] = DateUtility.formatToLongDate(mandbackDate);
						valueParm[2] = DateUtility.formatToLongDate(SysParamUtil.getValueAsDate("APP_DFT_END_DATE"));
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90318", valueParm)));
					}
				}
				boolean isValidBranch = true;
				if (StringUtils.isNotBlank(mandate.getIFSC())) {
					BankBranch bankBranch = bankBranchService.getBankBrachByIFSC(mandate.getIFSC());
					if (bankBranch == null) {
						String[] valueParm = new String[1];
						valueParm[0] = mandate.getIFSC();
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90301", valueParm)));
					} else {
						isValidBranch = validateBranchCode(mandate, isValidBranch, bankBranch);
						mandate.setBankCode(bankBranch.getBankCode());
						if (StringUtils.isBlank(mandate.getMICR())) {
							mandate.setMICR(bankBranch.getMICR());
						} else {
							if (!StringUtils.equals(bankBranch.getMICR(), mandate.getMICR())) {
								String[] valueParm = new String[2];
								valueParm[0] = "MICR";
								valueParm[1] = mandate.getMICR();
								errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90701", valueParm)));
							}
						}
					}
				} else if (StringUtils.isNotBlank(mandate.getBankCode())
						&& StringUtils.isNotBlank(mandate.getBranchCode())) {
					BankBranch bankBranch = bankBranchService.getBankBrachByCode(mandate.getBankCode(),
							mandate.getBranchCode());
					if (bankBranch == null) {
						String[] valueParm = new String[2];
						valueParm[0] = mandate.getBankCode();
						valueParm[1] = mandate.getBranchCode();
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90302", valueParm)));
					} else {
						isValidBranch = validateBranchCode(mandate, isValidBranch, bankBranch);
						mandate.setBankCode(bankBranch.getBankCode());
						if (StringUtils.isBlank(mandate.getMICR())) {
							mandate.setMICR(bankBranch.getMICR());
						} else {
							if (!StringUtils.equals(bankBranch.getMICR(), mandate.getMICR())) {
								String[] valueParm = new String[2];
								valueParm[0] = "MICR";
								valueParm[1] = mandate.getMICR();
								errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90701", valueParm)));
								return errorDetails;
							}
						}

					}
				}
				if (!isValidBranch) {
					String[] valueParm = new String[1];
					valueParm[0] = mandate.getMandateType();
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90333", valueParm)));
					return errorDetails;
				}
				//validate AccNumber length
				if (StringUtils.isNotBlank(mandate.getBankCode())) {
					int accNoLength = bankDetailService.getAccNoLengthByCode(mandate.getBankCode());
					if (accNoLength != 0) {
						if (mandate.getAccNumber().length() != accNoLength) {
							String[] valueParm = new String[2];
							valueParm[0] = "AccountNumber(Mandate)";
							valueParm[1] = String.valueOf(accNoLength) + " characters";
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("30570", valueParm)));
							return errorDetails;
						}
					}
				}
				//validate Phone number
				String mobileNumber = mandate.getPhoneNumber();
				if (StringUtils.isNotBlank(mobileNumber)) {
					if (!(mobileNumber.matches("\\d{10}"))) {
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90278", null)));
					}
				}

				String acc_holder_regix = "^$|^[A-Za-z]+[A-Za-z.\\s]*";
				//validate names
				String accHolderName = mandate.getAccHolderName();
				if (StringUtils.isNotBlank(accHolderName)) {
					if (!(accHolderName.matches(acc_holder_regix))) {
						String[] valueParm = new String[1];
						valueParm[0] = "AccHolderName";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90237", valueParm)));
					}
				}
				String jointAccHolderName = mandate.getJointAccHolderName();
				if (StringUtils.isNotBlank(jointAccHolderName)) {
					if (!(jointAccHolderName.matches(acc_holder_regix))) {
						String[] valueParm = new String[1];
						valueParm[0] = "JointAccHolderName";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90237", valueParm)));
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
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90307", valueParm)));
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
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90308", valueParm)));
					}
				}

				//validate periodicity
				if (StringUtils.isNotBlank(mandate.getPeriodicity())) {
					ErrorDetail errorDetail = FrequencyUtil.validateFrequency(mandate.getPeriodicity());
					if (errorDetail != null && StringUtils.isNotBlank(errorDetail.getCode())) {
						String[] valueParm = new String[1];
						valueParm[0] = mandate.getPeriodicity();
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90207", valueParm)));
					}
				} else {
					mandate.setPeriodicity(MandateConstants.MANDATE_DEFAULT_FRQ);
				}

				//validate status
				if (StringUtils.isNotBlank(mandate.getStatus())) {
					List<ValueLabel> status = PennantStaticListUtil.getStatusTypeList(SysParamUtil.getValueAsString(MandateConstants.MANDATE_CUSTOM_STATUS));
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
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90309", valueParm)));
					}
				}
				if (!StringUtils.equalsIgnoreCase(mandate.getMandateType(),
						financeDetail.getFinScheduleData().getFinanceMain().getFinRepayMethod())) {
					String[] valueParm = new String[2];
					valueParm[0] = financeDetail.getFinScheduleData().getFinanceMain().getFinRepayMethod();
					valueParm[1] = mandate.getMandateType();
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90311", valueParm)));
					return errorDetails;
				}
			}
		
			if (mandate.getDocImage() == null && StringUtils.isBlank(mandate.getExternalRef())) {
				String[] valueParm = new String[2];
				valueParm[0] = "docContent";
				valueParm[1] = "docRefId";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90123", valueParm)));
			} else if(StringUtils.isBlank(mandate.getDocumentName())) {
				String[] valueParm = new String[2];
				valueParm[0] = "Document Name";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
			}
			
			if (StringUtils.isNotBlank(mandate.getDocumentName())) {
				String docName = mandate.getDocumentName().toLowerCase();
				// document name has no extension
				if (!docName.contains(".")) {
					String[] valueParm = new String[1];
					valueParm[0] = mandate.getDocumentName();
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90291", valueParm)));
				}
				// document name has only extension
				else if (StringUtils.isEmpty(docName.substring(0, docName.lastIndexOf(".")))) {
					String[] valueParm = new String[2];
					valueParm[0] = "Document Name";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
				}
				//document Name Extension validation
				if (!docName.endsWith(".jpg") && !docName.endsWith(".jpeg") && !docName.endsWith(".png")
						&& !docName.endsWith(".pdf")) {
					String[] valueParm = new String[1];
					valueParm[0] = "Document Extension available ext are:JPG,JPEG,PNG,PDF ";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90122", valueParm)));
				}
			}
		} else {
			if(!StringUtils.equals(financeDetail.getFinScheduleData().getFinanceMain().getFinRepayMethod(), 
					FinanceConstants.REPAYMTH_MANUAL) && financeDetail.isStp()){
			String[] valueParm = new String[1];
			valueParm[0] = "mandate";
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
			}
		}
		return errorDetails;
	}

	private boolean validateBranchCode(Mandate mandate, boolean isValidBranch, BankBranch bankBranch) {
		if(StringUtils.equals(MandateConstants.TYPE_ECS, mandate.getMandateType())){
			if(!bankBranch.isEcs()){
				isValidBranch = false;
			}
		} else if(StringUtils.equals(MandateConstants.TYPE_DDM, mandate.getMandateType())){
			if(!bankBranch.isDda()){
				isValidBranch = false;
			}
		}else if(StringUtils.equals(MandateConstants.TYPE_NACH, mandate.getMandateType())){
			if(!bankBranch.isNach()){
				isValidBranch = false;
			}
		}
		return isValidBranch;
	}

	public List<ErrorDetail> disbursementValidation(FinanceDetail financeDetail) {
		List<ErrorDetail> errorDetails = new ArrayList<ErrorDetail>();
		List<FinAdvancePayments> finAdvPayments = financeDetail.getAdvancePaymentsList();

		// if it is stp process disbursement is mandatory
		if (financeDetail.isStp() && finAdvPayments == null) {
			String[] valueParm = new String[1];
			valueParm[0] = "disbursement";
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
			return errorDetails;
		}
		// validate disbursement details
		if (finAdvPayments != null) {
			for (FinAdvancePayments advPayment : finAdvPayments) {
				// partnerbankid
				if (advPayment.getPartnerBankID() <= 0) {
					String[] valueParm = new String[1];
					valueParm[0] = "PartnerBankID";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
					return errorDetails;
				}

				// validate disbParty
				if (StringUtils.isBlank(advPayment.getPaymentDetail())) {
					String[] valueParm = new String[1];
					valueParm[0] = "disbParty";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
					return errorDetails;
				} else {
					List<ValueLabel> disbPartys = PennantStaticListUtil.getPaymentDetails();
					boolean isValidDisbParty = false;
					for (ValueLabel value : disbPartys) {
						if (StringUtils.equals(value.getValue(), advPayment.getPaymentDetail())) {
							isValidDisbParty = true;
							break;
						}
					}
					// Invalid {0} code {1}.
					if (!isValidDisbParty) {
						String[] valueParm = new String[2];
						valueParm[0] = "disbParty";
						valueParm[1] = advPayment.getPaymentDetail();
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90224", valueParm)));
					}
				}

				// validate disbType
				if (StringUtils.isBlank(advPayment.getPaymentType())) {
					String[] valueParm = new String[1];
					valueParm[0] = "disbType";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
				} else {
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
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90216", valueParm)));
					}
				}
				String finType = financeDetail.getFinScheduleData().getFinanceMain().getFinType();
				int count = finTypePartnerBankService.getPartnerBankCount(finType, advPayment.getPaymentType(), 
						AccountConstants.PARTNERSBANK_DISB, advPayment.getPartnerBankID());
				if (count <= 0) {
					String[] valueParm = new String[1];
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90263", valueParm)));
					return errorDetails;
				}
				
				// fetch partner bank details
				PartnerBank partnerBank = partnerBankDAO.getPartnerBankById(advPayment.getPartnerBankID(), "");
				if(partnerBank != null) {
					advPayment.setPartnerBankAc(partnerBank.getAccountNo());
					advPayment.setPartnerBankAcType(partnerBank.getAcType());
				}
				FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
				if(financeMain != null){
				if (advPayment.getLlDate().before(financeMain.getFinStartDate())
						|| advPayment.getLlDate().after(financeMain.getCalMaturity())) {
					String[] valueParm = new String[3];
					valueParm[0] = "disbursement Date";
					valueParm[1] = DateUtility.formatToLongDate(financeMain.getFinStartDate());
					valueParm[2] = DateUtility.formatToLongDate(financeMain.getCalMaturity());
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90318", "", valueParm)));
					return errorDetails;
				}
				}
				if (StringUtils.equals(advPayment.getPaymentType(), DisbursementConstants.PAYMENT_TYPE_CHEQUE)
						|| StringUtils.equals(advPayment.getPaymentType(), DisbursementConstants.PAYMENT_TYPE_DD)) {

					// Issuer bank
					if (StringUtils.isBlank(advPayment.getBankCode())) {
						String[] valueParm = new String[1];
						valueParm[0] = "issueBank";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90214", valueParm)));
					} else {
						BankDetail bankDetail = bankDetailService.getBankDetailById(advPayment.getBankCode());
						if (bankDetail == null) {
							String[] valueParm = new String[1];
							valueParm[0] = advPayment.getBankCode();
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90213", valueParm)));
						}
					}

					// Liability hold name
					if (StringUtils.isBlank(advPayment.getLiabilityHoldName())) {
						String[] valueParm = new String[1];
						valueParm[0] = "favourName";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90214", valueParm)));
					}

					// Payable location
					if (StringUtils.isBlank(advPayment.getPayableLoc())) {
						String[] valueParm = new String[1];
						valueParm[0] = "payableLoc";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90214", valueParm)));
					} else {
						Pattern pattern = Pattern.compile(
								PennantRegularExpressions.getRegexMapper(PennantRegularExpressions.REGEX_ADDRESS));
						Matcher matcher = pattern.matcher(advPayment.getPayableLoc());
						if (matcher.matches() == false) {
							String[] valueParm = new String[1];
							valueParm[0] = "payableLoc";
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90909", "", valueParm), "EN"));
						}
					}

					// Printing location
					if (StringUtils.isBlank(advPayment.getPrintingLoc())) {
						String[] valueParm = new String[1];
						valueParm[0] = "printingLoc";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90214", valueParm)));
					} else {
						Pattern pattern = Pattern.compile(
								PennantRegularExpressions.getRegexMapper(PennantRegularExpressions.REGEX_ADDRESS));
						Matcher matcher = pattern.matcher(advPayment.getPrintingLoc());
						if (matcher.matches() == false) {
							String[] valueParm = new String[1];
							valueParm[0] = "printingLoc";
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90909", "", valueParm), "EN"));
						}
					}

					// value date
					if (advPayment.getValueDate() == null) {
						String[] valueParm = new String[1];
						valueParm[0] = "valueDate";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90214", valueParm)));
					} else {
						Date todate = DateUtility.addMonths(DateUtility.getAppDate(), 6);
						if (advPayment.getValueDate().compareTo(DateUtility.getAppDate()) < 0
								|| advPayment.getValueDate().after(todate)) {
							String[] valueParm = new String[3];
							valueParm[0] = "disbursement ValueDate";
							valueParm[1] = DateUtility.formatToLongDate(DateUtility.getAppDate());
							valueParm[2] = DateUtility.formatToLongDate(todate);
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90318", "", valueParm)));
						}
					}
				} else if (StringUtils.equals(advPayment.getPaymentType(), DisbursementConstants.PAYMENT_TYPE_IMPS)
						|| StringUtils.equals(advPayment.getPaymentType(), DisbursementConstants.PAYMENT_TYPE_NEFT)
						|| StringUtils.equals(advPayment.getPaymentType(), DisbursementConstants.PAYMENT_TYPE_RTGS)) {

					// Ifsc, bank or branch codes
					if (StringUtils.isBlank(advPayment.getiFSC())
							&& (StringUtils.isBlank(advPayment.getBranchBankCode())
									|| StringUtils.isBlank(advPayment.getBranchCode()))) {
						String[] valueParm = new String[2];
						valueParm[0] = "Ifsc";
						valueParm[1] = "Bank/Branch code";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90215", valueParm)));
					}
					if (StringUtils.isNotBlank(advPayment.getiFSC())) {
						BankBranch bankBranch = bankBranchService.getBankBrachByIFSC(advPayment.getiFSC());
						if (bankBranch == null) {
							String[] valueParm = new String[1];
							valueParm[0] = advPayment.getiFSC();
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90301", valueParm)));
						} else {
							advPayment.setBankCode(bankBranch.getBankCode());
						}
					}
					if (StringUtils.isNotBlank(advPayment.getBranchBankCode())
							&& StringUtils.isNotBlank(advPayment.getBranchCode())) {
						BankBranch bankBranch = bankBranchService.getBankBrachByCode(advPayment.getBranchBankCode(),
								advPayment.getBranchCode());
						if (bankBranch == null) {
							String[] valueParm = new String[2];
							valueParm[0] = advPayment.getBranchBankCode();
							valueParm[1] = advPayment.getBranchCode();
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90302", valueParm)));
						} else {
							advPayment.setBankCode(bankBranch.getBankCode());
						}
					}

					// Account number
					if (StringUtils.isBlank(advPayment.getBeneficiaryAccNo())) {
						String[] valueParm = new String[2];
						valueParm[0] = "accountNo";
						valueParm[1] = advPayment.getBeneficiaryAccNo();
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90217", valueParm)));
					} /*else {
						//validate AccNumber length
						int accNoLength = bankDetailService.getAccNoLengthByCode(advPayment.getBankCode());
						if (advPayment.getBeneficiaryAccNo().length() != accNoLength) {
							String[] valueParm = new String[2];
							valueParm[0] = "AccountNumber(Disbursement)";
							valueParm[1] = String.valueOf(accNoLength) + " characters";
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("30570", valueParm)));
							return errorDetails;
						}
					}*/
					// Account holder name
					if (StringUtils.isBlank(advPayment.getBeneficiaryName())) {
						String[] valueParm = new String[2];
						valueParm[0] = "acHolderName";
						valueParm[1] = advPayment.getBeneficiaryName();
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90217", valueParm)));
					}

					// phone number
					if (StringUtils.equals(advPayment.getPaymentType(), DisbursementConstants.PAYMENT_TYPE_IMPS)
							&& StringUtils.isBlank(advPayment.getPhoneNumber())) {
						String[] valueParm = new String[2];
						valueParm[0] = "phoneNumber";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
					}
					if (StringUtils.isNotBlank(advPayment.getPhoneNumber())) {
						if (!(advPayment.getPhoneNumber().matches("\\d{10}"))) {
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90278", null)));
						}
					}
				}
			}
		}
		return errorDetails;
	}

	private List<ErrorDetail> nonFinanceValidation(String vldGroup, FinScheduleData finScheduleData, boolean isAPICall) {
		// Re-Initialize Error Details
		List<ErrorDetail> errorDetails = new ArrayList<ErrorDetail>();
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
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90101", valueParm)));
			} else {
				finScheduleData.getFinanceMain().setCustID(customer.getCustID());
				getFinanceDetail().getCustomerDetails().setCustomer(customer);
			}
		}

		// Validate Finance Currency
		boolean currencyExists = currencyDAO.isExistsCurrencyCode(finMain.getFinCcy());
		if (!currencyExists) {
			String[] valueParm = new String[1];
			valueParm[0] = finMain.getFinCcy();
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90120", valueParm)));
			return errorDetails;
		}

		// validate finance branch
		if (isCreateLoan || StringUtils.isNotBlank(finMain.getFinBranch())) {
			Branch branch = branchDAO.getBranchById(finMain.getFinBranch(), "");
			if (branch == null) {
				String[] valueParm = new String[1];
				valueParm[0] = finMain.getFinBranch();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90129", valueParm)));
			}
		}

		//Validate Repayment Method
		//TODO: To be confirmed from where it should be taken? PennantStaticListUtil.getRepayMethods() OR MandateConstants or FinanceConstants??
		if (isCreateLoan) {
			String repayMethod = finMain.getFinRepayMethod();

			// finRepay method
			if (StringUtils.isNotBlank(repayMethod)) {
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
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90307", valueParm)));
				}
			}
		}
		if(isCreateLoan) {
			String finPurpose = finMain.getFinPurpose();
			if(StringUtils.isNotBlank(finPurpose)) {
			LoanPurpose loanPurpose=loanPurposeDAO.getLoanPurposeById(finPurpose, "");
			if(loanPurpose == null || !loanPurpose.isLoanPurposeIsActive() ) {
				String[] valueParm = new String[1];
				valueParm[0] = finPurpose;
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90701", valueParm)));
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

	private List<ErrorDetail> basicValidation(String vldGroup, FinScheduleData finScheduleData, boolean isAPICall) {
		List<ErrorDetail> errorDetails = finScheduleData.getErrorDetails();
		FinanceMain finMain = finScheduleData.getFinanceMain();
		FinanceType financeType = finScheduleData.getFinanceType();
		BigDecimal zeroAmount = BigDecimal.ZERO;

		// Application number
		if(!ImplementationConstants.CLIENT_NFL) {
			if(StringUtils.isNotBlank(finMain.getApplicationNo()) && finMain.getApplicationNo().length() > LengthConstants.LEN_REF) {
				String[] valueParm = new String[2];
				valueParm[0] = "Application Number";
				valueParm[1] = LengthConstants.LEN_REF+ " characters";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("30568", valueParm)));
			}
		}
		// Finance start date
		Date appDate = DateUtility.getAppDate();
		Date minReqFinStartDate = DateUtility.addDays(appDate, -SysParamUtil.getValueAsInt("BACKDAYS_STARTDATE"));
		if (finMain.getFinStartDate().compareTo(minReqFinStartDate) <= 0) {
			String[] valueParm = new String[2];
			valueParm[0] = SysParamUtil.getValueAsString("BACKDAYS_STARTDATE");
			valueParm[1] = DateUtility.formatDate(DateUtility.addDays(minReqFinStartDate, 1), PennantConstants.XMLDateFormat);
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90134", valueParm)));
		}
		
		Date maxReqFinStartDate = DateUtility.addDays(appDate, +SysParamUtil.getValueAsInt("FUTUREDAYS_STARTDATE") + 1);
		if (finMain.getFinStartDate().compareTo(maxReqFinStartDate) > 0) {
			String[] valueParm = new String[2];
			valueParm[0] = "Loan Start Date";
			valueParm[1] = DateUtility.formatDate(DateUtility.addDays(maxReqFinStartDate, 1), PennantConstants.XMLDateFormat);
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("65027", valueParm)));
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
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90209", valueParm)));
		}

		//Loan Amount Validation
		if (finMain.getFinAmount().compareTo(zeroAmount) <= 0) {
			String[] valueParm = new String[1];
			valueParm[0] = String.valueOf(finMain.getFinAmount().doubleValue());
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90127", valueParm)));
		}

		// 
		if(financeType.isAlwMaxDisbCheckReq() && finMain.getFinAssetValue().compareTo(zeroAmount) <= 0) {
			String[] valueParm = new String[1];
			valueParm[0] = "finAssetValue";
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
		}
		
		// finAssetValue
		if (finMain.getFinAssetValue().compareTo(zeroAmount) > 0) {
			if (finMain.getFinAmount().compareTo(finMain.getFinAssetValue()) > 0) {
				String[] valueParm = new String[2];
				valueParm[0] = "finAmount";
				valueParm[1] = "finAssetValue";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90220", valueParm)));
			}
		}

		finMain.setDownPayment(finMain.getDownPayBank().add(finMain.getDownPaySupl()));

		// validate downpay Bank and supplier
		if(financeType.isFinIsDwPayRequired()) {
			if(getFinanceDetail() != null) {
				setDownpaymentRulePercentage(financeType, finMain);
				BigDecimal reqDwnPay = getPercentageValue(finMain.getFinAmount(), finMain.getMinDownPayPerc());
				BigDecimal downPayment = finMain.getDownPayBank().add(finMain.getDownPaySupl());

				if (downPayment.compareTo(finMain.getFinAmount()) >= 0 ) {
					String[] valueParm = new String[3];
					valueParm[0] = "Sum of Bank & Supplier Down payments";
					valueParm[1] = String.valueOf(reqDwnPay);
					valueParm[2] = String.valueOf(finMain.getFinAmount());
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("30567", valueParm)));
				}

				if (downPayment.compareTo(reqDwnPay) < 0 ) {
					String[] valueParm = new String[2];
					valueParm[0] = "Sum of Bank & Supplier Down payments";
					valueParm[1] = String.valueOf(reqDwnPay);
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("30569", valueParm)));
				}
			}
		} else if(finMain.getDownPayBank().compareTo(zeroAmount) != 0 || finMain.getDownPaySupl().compareTo(zeroAmount) != 0) {
			String[] valueParm = new String[3];
			valueParm[0] = "Down pay bank";
			valueParm[1] = "Supplier";
			valueParm[2] = finMain.getFinType();
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90203", valueParm)));
		}
		if (finMain.isTDSApplicable()) {
			if (!financeType.isTDSApplicable()) {
				String[] valueParm = new String[3];
				valueParm[0] = "tds";
				valueParm[1] = financeType.getFinType();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90329", valueParm)));
			}
		}
		if (finMain.isQuickDisb()) {
			if (!financeType.isQuickDisb()) {
				String[] valueParm = new String[3];
				valueParm[0] = "quickDisb";
				valueParm[1] = financeType.getFinType();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90329", valueParm)));
			}
		}
		//RETURN IF ANY ERROR AFTER VERY BASIC VALIDATION
		if (!errorDetails.isEmpty()) {
			return errorDetails;
		}

		//Allow Manual Schedule
		if (finMain.isManualSchedule()) {
			errorDetails = manualScheduleValidation(vldGroup, finScheduleData);
			if (!errorDetails.isEmpty()) {
				return errorDetails;
			}
		}

		//Planned Deferments
		if (finMain.getPlanDeferCount() > 0) {
			errorDetails = planDefermentValidation(vldGroup, finScheduleData);
			if (!errorDetails.isEmpty()) {
				return errorDetails;
			}
		}
		//planned EMI
		errorDetails = planEMIHolidayValidation(vldGroup, finScheduleData);
		if (!errorDetails.isEmpty()) {
			return errorDetails;
		}

		//Step Loan?
		errorDetails = stepLoanValidation(vldGroup, finScheduleData);
		if (!errorDetails.isEmpty()) {
			return errorDetails;
		}

		return errorDetails;
	}

	/*
	 * ================================================================================================================
	 * VALIDATE FINANCE GRACE DETAILS
	 * ================================================================================================================
	 */

	private BigDecimal getPercentageValue(BigDecimal finAmount, BigDecimal minDownPayPerc) {
		BigDecimal returnAmount = BigDecimal.ZERO;

		if (finAmount != null) {
			returnAmount = (finAmount.multiply(unFormateAmount(minDownPayPerc,2).divide(
					new BigDecimal(100)))).divide(new BigDecimal(100),RoundingMode.HALF_DOWN);
		}
		return returnAmount;
	}
	
	public static BigDecimal unFormateAmount(BigDecimal amount, int dec) {
		if (amount == null) {
			return BigDecimal.ZERO;
		}
		BigInteger bigInteger = amount.multiply(BigDecimal.valueOf(Math.pow(10, dec))).toBigInteger();
		return new BigDecimal(bigInteger);
	}

	private void setDownpaymentRulePercentage(FinanceType finType, FinanceMain finMain) {
		if (finType.getDownPayRule() != 0 && finType.getDownPayRule() != Long.MIN_VALUE 
				&& StringUtils.isNotEmpty(finType.getDownPayRuleDesc())) {

			CustomerEligibilityCheck customerEligibilityCheck = prepareCustElgDetail(false).getCustomerEligibilityCheck();
			String sqlRule = ruleService.getAmountRule(finType.getDownPayRuleDesc(), RuleConstants.MODULE_DOWNPAYRULE,
					RuleConstants.EVENT_DOWNPAYRULE);
			BigDecimal downpayPercentage = BigDecimal.ZERO;
			if (StringUtils.isNotEmpty(sqlRule)) {
				HashMap<String, Object> fieldsAndValues = customerEligibilityCheck.getDeclaredFieldValues();
				downpayPercentage = (BigDecimal) ruleExecutionUtil.executeRule(sqlRule, fieldsAndValues, finMain.getFinCcy(), 
						RuleReturnType.DECIMAL);
			}
			finMain.setMinDownPayPerc(downpayPercentage);
		} else {
			finMain.setMinDownPayPerc(BigDecimal.ZERO);
		}
	}

	private List<ErrorDetail> graceValidation(String vldGroup, FinScheduleData finScheduleData, boolean isAPICall) {
		List<ErrorDetail> errorDetails = finScheduleData.getErrorDetails();
		FinanceMain finMain = finScheduleData.getFinanceMain();
		FinanceType financeType = finScheduleData.getFinanceType();

		//Allow Grace?
		if (!financeType.isFInIsAlwGrace()) {
			String[] valueParm = new String[1];
			valueParm[0] = finMain.getFinType();
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90135", valueParm)));
			return errorDetails;
		}

		//Grace Terms & Grace End Date are Mutually Exclusive
		if (finMain.getGraceTerms() > 0 && finMain.getGrcPeriodEndDate() != null) {
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90152", null)));
			return errorDetails;
		}

		//Both Grace Terms & Grace End Date are not present
		if (finMain.getGraceTerms() == 0 && finMain.getGrcPeriodEndDate() == null) {
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90184", null)));
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
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90209", valueParm)));
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

	private List<ErrorDetail> repayValidation(String vldGroup, FinScheduleData finScheduleData, boolean isAPICall) {
		List<ErrorDetail> errorDetails = finScheduleData.getErrorDetails();
		FinanceMain finMain = finScheduleData.getFinanceMain();
		FinanceType financeType = finScheduleData.getFinanceType();

		//Number of Terms & Maturity Date are Mutually Exclusive
		if (finMain.getNumberOfTerms() > 0 && finMain.getMaturityDate() != null) {
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90190", null)));
			return errorDetails;
		}

		//Both Grace Terms & Grace End Date are not present
		if (finMain.getNumberOfTerms() == 0 && finMain.getMaturityDate() == null) {
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90191", null)));
			return errorDetails;
		}

		// validate min and max terms with loanType config.
		if(financeType.getFinMinTerm() > 0 && financeType.getFinMaxTerm() > 0) {
			if(finMain.getNumberOfTerms() < financeType.getFinMinTerm() || finMain.getNumberOfTerms() > financeType.getFinMaxTerm()) {
				String[] valueParm = new String[3];
				valueParm[0] = "Repay";
				valueParm[1] = String.valueOf(financeType.getFinMinTerm());
				valueParm[2] = String.valueOf(financeType.getFinMaxTerm());
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90272", valueParm)));
				return errorDetails;
			}
		}
		
		//Repay Rate Validations
		errorDetails = repayRateValidation(finScheduleData);

		//Repayment Schedule Method (If not blanks validation already happens in defaulting)
		if (!StringUtils.equals(finMain.getScheduleMethod(), CalculationConstants.SCHMTHD_NOPAY)
				&& !StringUtils.equals(finMain.getScheduleMethod(), CalculationConstants.SCHMTHD_EQUAL)
				&& !StringUtils.equals(finMain.getScheduleMethod(), CalculationConstants.SCHMTHD_PFT)
				&& !StringUtils.equals(finMain.getScheduleMethod(), CalculationConstants.SCHMTHD_PRI)
				&& !StringUtils.equals(finMain.getScheduleMethod(), CalculationConstants.SCHMTHD_PRI_PFT)) {
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90189", null)));
		}

		//Validate Repayment Details
		errorDetails = repayFrqValidation(finScheduleData);
		if (!errorDetails.isEmpty()) {
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
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90227", null)));
		}

		//Validate Advised Rates
		errorDetails = repayAdvRateValidation(finScheduleData);

		//Validate BPI
		if (financeType.isAlwBPI()) {
		errorDetails = bpiValidation(finScheduleData);
		} else if (finMain.isAlwBPI()) {
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90228", null)));
			return errorDetails;
		}

		return errorDetails;
	}

	/*
	 * _______________________________________________________________________________________________________________
	 * MANUAL SCHEDULE
	 * _______________________________________________________________________________________________________________
	 */

	private List<ErrorDetail> manualScheduleValidation(String vldGroup, FinScheduleData finScheduleData) {
		List<ErrorDetail> errorDetails = finScheduleData.getErrorDetails();
		FinanceMain finMain = finScheduleData.getFinanceMain();
		FinanceType financeType = finScheduleData.getFinanceType();

		//Finance Type allow Manual Schedule?
		if (!financeType.isManualSchedule()) {
			String[] valueParm = new String[1];
			valueParm[0] = finMain.getFinType();
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90138", valueParm)));
		}

		//Manual Schedule with Grace
		if (!finMain.isAllowGrcPeriod()) {
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90186", null)));
		}

		//Planned Deferment Requested
		if (finMain.getPlanDeferCount() > 0) {
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90140", null)));
		}

		//Finance Type allow Step?
		if (!finMain.isStepFinance()) {
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90144", null)));
		}

		//Planned EMI Holiday Requested
		if (finMain.isPlanEMIHAlw()) {
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90149", null)));
		}

		return errorDetails;
	}

	/*
	 * _______________________________________________________________________________________________________________
	 * PLANNED DEFERMENTS
	 * _______________________________________________________________________________________________________________
	 */

	private List<ErrorDetail> planDefermentValidation(String vldGroup, FinScheduleData finScheduleData) {
		List<ErrorDetail> errorDetails = finScheduleData.getErrorDetails();
		FinanceMain finMain = finScheduleData.getFinanceMain();
		FinanceType financeType = finScheduleData.getFinanceType();

		//Finance Type allow Planned Deferments?
		if (!financeType.isAlwPlanDeferment()) {
			String[] valueParm = new String[1];
			valueParm[0] = finMain.getFinType();
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90139", valueParm)));
		}

		//Repay Rate Basis not Flat Converting to Reducing
		if (!StringUtils.equals(finMain.getRepayRateBasis(), CalculationConstants.RATE_BASIS_C)) {
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90151", null)));
		}

		//Requested more deferments than defined in finance type?
		if (finMain.getPlanDeferCount() > financeType.getPlanDeferCount()) {
			String[] valueParm = new String[3];
			valueParm[0] = Integer.toString(finMain.getPlanDeferCount());
			valueParm[1] = Integer.toString(financeType.getPlanDeferCount());
			valueParm[2] = finMain.getFinType();
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90141", valueParm)));
		}

		//Planned EMI Holidays also requested?
		if (finMain.isPlanEMIHAlw()) {
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90142", null)));
		}

		//Stepping also requested?
		if (finMain.isStepFinance()) {
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90143", null)));
		}

		return errorDetails;
	}

	/*
	 * _______________________________________________________________________________________________________________
	 * STEP LOAN
	 * _______________________________________________________________________________________________________________
	 */

	private List<ErrorDetail> stepLoanValidation(String vldGroup, FinScheduleData finScheduleData) {
		List<ErrorDetail> errorDetails = finScheduleData.getErrorDetails();
		FinanceMain finMain = finScheduleData.getFinanceMain();
		FinanceType financeType = finScheduleData.getFinanceType();

		if (!finMain.isStepFinance()) {
			if (finMain.isAlwManualSteps() || StringUtils.isNotBlank(finMain.getStepPolicy())
					|| StringUtils.isNotBlank(finMain.getStepType())) {
				String[] valueParm = new String[1];
				valueParm[0] = finMain.getFinType();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90163", valueParm)));
				return errorDetails;
			}
		}

		//Finance Type allow Step?
		if (finMain.isStepFinance() && !financeType.isStepFinance()) {
			String[] valueParm = new String[1];
			valueParm[0] = finMain.getFinType();
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("91129", valueParm)));
			return errorDetails;
		} else if (!finMain.isStepFinance() && financeType.isSteppingMandatory()) {
			String[] valueParm = new String[1];
			valueParm[0] = finMain.getFinType();
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("91128", valueParm)));
			return errorDetails;
		}
		
		if(finMain.isStepFinance()) {
			// ScheduleMethod
			if (StringUtils.equals(finMain.getScheduleMethod(), CalculationConstants.SCHMTHD_PFT)) {
				String[] valueParm = new String[1];
				valueParm[0] = "Calculated Interest on Frequency";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("30552", valueParm)));
				return errorDetails;
			}

			if (StringUtils.equals(finMain.getStepType(), FinanceConstants.STEPTYPE_PRIBAL)
					&& StringUtils.equals(finMain.getScheduleMethod(), CalculationConstants.SCHMTHD_EQUAL)) {
				String[] valueParm = new String[1];
				valueParm[0] = "Equal Installments (Principal and Interest)";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("30555", valueParm)));
				return errorDetails;
			}

			if(finMain.isPlanEMIHAlw()) {
				String[] valueParm = new String[2];
				valueParm[0] = "Planned EMI";
				valueParm[1] = "step";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90283", valueParm)));
			}
		}
		
		//Planned EMI Holidays also requested?
		/*
		 * if (finMain.isPlanEMIHAlw()) { errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90150", null))); }
		 */

		//Manual Steps Requested?
		if (finMain.isAlwManualSteps()) {
			//Allow Manual Step?
			if (!financeType.isAlwManualSteps()) {
				String[] valueParm = new String[1];
				valueParm[0] = finMain.getFinType();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90145", valueParm)));
				return errorDetails;
			}

			if (StringUtils.isNotBlank(finMain.getStepPolicy())) {
				String[] valueParm = new String[2];
				valueParm[0] = finMain.getStepPolicy();
				valueParm[1] = "loan with allow manualSteps";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90204", valueParm)));
				return errorDetails;
			}
			//Step Type
			if (!StringUtils.equals(finMain.getStepType(), FinanceConstants.STEPTYPE_EMI)
					&& !StringUtils.equals(finMain.getStepType(), FinanceConstants.STEPTYPE_PRIBAL)) {
				String[] valueParm = new String[2];
				valueParm[0] = new StringBuilder(10).append(FinanceConstants.STEPTYPE_EMI).append(" & ")
						.append(FinanceConstants.STEPTYPE_PRIBAL).toString();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90148", valueParm)));
			}

			// step aggregate
			if (finScheduleData.getStepPolicyDetails() == null || finScheduleData.getStepPolicyDetails().isEmpty()) {
				String[] valueParm = new String[1];
				valueParm[0] = "step";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
			}

		} else if (finMain.isStepFinance()) {
			//Step Policy requested
			if (StringUtils.isBlank(finMain.getStepPolicy())) {
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90146", null)));
			} else {
				if (!StringUtils.containsIgnoreCase(financeType.getAlwdStepPolicies(), finMain.getStepPolicy())) {
					String[] valueParm = new String[2];
					valueParm[0] = finMain.getFinType();
					valueParm[1] = financeType.getAlwdStepPolicies();
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90147", valueParm)));
				}

				// Validate stepPolicy code
				StepPolicyHeader stepHeader = stepPolicyService
						.getApprovedStepPolicyHeaderById(finMain.getStepPolicy());
				if (stepHeader == null) {
					String[] valueParm = new String[1];
					valueParm[0] = finMain.getStepPolicy();
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90252", valueParm)));
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

	private List<ErrorDetail> planEMIHolidayValidation(String vldGroup, FinScheduleData finScheduleData) {
		List<ErrorDetail> errorDetails = finScheduleData.getErrorDetails();
		FinanceMain finMain = finScheduleData.getFinanceMain();
		FinanceType financeType = finScheduleData.getFinanceType();

		if (!financeType.isPlanEMIHAlw() && finMain.isPlanEMIHAlw()) {
			String[] valueParm = new String[2];
			valueParm[0] = "Planned EMI Holiday(planEMIHAlw)";
			valueParm[1] = finMain.getFinType();
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90204", valueParm)));
			return errorDetails;
		}

		if (finMain.isPlanEMIHAlw()) {
			if (StringUtils.isBlank(finMain.getPlanEMIHMethod())) {
				String[] valueParm = new String[1];
				valueParm[0] = "planEMIHMethod";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
				return errorDetails;
			}
			if (finMain.getPlanEMIHMaxPerYear() == 0) {
				finMain.setPlanEMIHMaxPerYear(financeType.getPlanEMIHMaxPerYear());
			}
			if (finMain.getPlanEMIHMaxPerYear() < 0) {
				String[] valueParm = new String[2];
				valueParm[0] = "PlanEMIHMaxPerYear";
				valueParm[1] = "0";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("91121", valueParm)));
				return errorDetails;
			}
			if (!financeType.isPlanEMICpz()) {
				if (finMain.isPlanEMICpz()) {
					String[] valueParm = new String[2];
					valueParm[0] = "planEMICpz";
					valueParm[1] = financeType.getFinType();
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90329", valueParm)));
					return errorDetails;
				}
			}
			if(!finMain.isPlanEMICpz()){
			finMain.setPlanEMICpz(financeType.isPlanEMICpz());
			}
			if (finMain.getPlanEMIHMax() == 0) {
				finMain.setPlanEMIHMax(financeType.getPlanEMIHMax());
			}
			
			if(finMain.getPlanEMIHMax() < 0){
				String[] valueParm = new String[2];
				valueParm[0] = "PlanEMIHMax";
				valueParm[1] = "0";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("91121", valueParm)));
				return errorDetails;
			}
			if (finMain.getPlanEMIHLockPeriod() == 0) {
				finMain.setPlanEMIHLockPeriod(financeType.getPlanEMIHLockPeriod());
			}
			if(finMain.getPlanEMIHLockPeriod() < 0){
				String[] valueParm = new String[2];
				valueParm[0] = "PlanEMIHLockPeriod";
				valueParm[1] = "0";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("91121", valueParm)));
				return errorDetails;
			}
			// planEMIHMethod
			if (!StringUtils.equals(finMain.getPlanEMIHMethod(), FinanceConstants.PLANEMIHMETHOD_FRQ)
					&& !StringUtils.equals(finMain.getPlanEMIHMethod(), FinanceConstants.PLANEMIHMETHOD_ADHOC)) {
				String[] valueParm = new String[1];
				valueParm[0] = FinanceConstants.PLANEMIHMETHOD_FRQ + "," + FinanceConstants.PLANEMIHMETHOD_ADHOC;
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90238", valueParm)));
				return errorDetails;
			}
			// planEMIHMaxPerYear
			if (finMain.getPlanEMIHMaxPerYear() > 11) {
				String[] valueParm = new String[1];
				valueParm[0] = String.valueOf(11);
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90239", valueParm)));
				return errorDetails;
			}
			// planEMIHMax
			if (finMain.getPlanEMIHMax() > (finMain.getNumberOfTerms() - 1)) {
				String[] valueParm = new String[1];
				valueParm[0] = String.valueOf(finMain.getNumberOfTerms() - 1);
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90240", valueParm)));
				return errorDetails;
			}
			if(finMain.getPlanEMIHMax() > financeType.getPlanEMIHMax()){
				String[] valueParm = new String[2];
				valueParm[0] = "PlanEMIHMax";
				valueParm[1] = String.valueOf(financeType.getPlanEMIHMax());
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("30568", valueParm)));
				return errorDetails;	
			}
			if(finMain.getPlanEMIHMaxPerYear() > financeType.getPlanEMIHMaxPerYear()){
				String[] valueParm = new String[2];
				valueParm[0] = "PlanEMIHMaxPerYear";
				valueParm[1] = String.valueOf(financeType.getPlanEMIHMaxPerYear());
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("30568", valueParm)));
				return errorDetails;	
			}
			if(!(financeType.getPlanEMIHLockPeriod() >= finMain.getPlanEMIHLockPeriod())){
				String[] valueParm = new String[2];
				valueParm[0] = "PlanEMIHLockPeriod";
				valueParm[1] = String.valueOf(financeType.getPlanEMIHLockPeriod());
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("30568", valueParm)));
				return errorDetails;	
			}
			if (StringUtils.equals(finMain.getPlanEMIHMethod(), FinanceConstants.PLANEMIHMETHOD_FRQ)) {
				if (finScheduleData.getApiPlanEMIHmonths() == null || finScheduleData.getApiPlanEMIHmonths().isEmpty()) {
					String[] valueParm = new String[1];
					valueParm[0] = "planEMIHmonths";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
					return errorDetails;
				} else {
					if (finScheduleData.getApiPlanEMIHmonths().size() > finMain.getPlanEMIHMaxPerYear()) {
						String[] valueParm = new String[2];
						valueParm[0] = "PlanEMIHmonths";
						valueParm[1] = "PlanEMIHMaxPerYear";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90220", valueParm)));
						return errorDetails;
					}
					for (FinPlanEmiHoliday detail : finScheduleData.getApiPlanEMIHmonths()) {
						int count=0;
						if (!(detail.getPlanEMIHMonth() > 0 && detail.getPlanEMIHMonth() <= 12)) {
							String[] valueParm = new String[3];
							valueParm[0] = "holidayMonth";
							valueParm[1] = "1";
							valueParm[2] = "12";
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("65031", valueParm)));
							return errorDetails;
						}
						for(FinPlanEmiHoliday planEmiMnths : finScheduleData.getApiPlanEMIHmonths()){
							if(detail.getPlanEMIHMonth()==planEmiMnths.getPlanEMIHMonth()){
								count++;
							}
						}
						if(count>=2){
							String[] valueParm = new String[1];
							valueParm[0] = "holidayMonth";
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90273", valueParm)));
							return errorDetails;
						}
					}
				}
			} else if (StringUtils.equals(finMain.getPlanEMIHMethod(), FinanceConstants.PLANEMIHMETHOD_ADHOC)) {
				if (finScheduleData.getApiPlanEMIHDates() == null || finScheduleData.getApiPlanEMIHDates().isEmpty()) {
					String[] valueParm = new String[1];
					valueParm[0] = "planEMIHDates";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
					return errorDetails;
				} else {
					if (finScheduleData.getApiPlanEMIHDates().size() >  finMain.getPlanEMIHMax()) {
						String[] valueParm = new String[2];
						valueParm[0] = "PlanEMIHDates";
						valueParm[1] = "PlanEMIHMax";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90220", valueParm)));
						return errorDetails;
					}
					
					for (FinPlanEmiHoliday emiDates : finScheduleData.getApiPlanEMIHDates()) {
						int count=0;
						for(FinPlanEmiHoliday duplicateDates : finScheduleData.getApiPlanEMIHDates()){
							if(emiDates.getPlanEMIHDate().compareTo(duplicateDates.getPlanEMIHDate())==0){
								count++;
							}
						}
						if (count >= 2) {
							String[] valueParm = new String[1];
							valueParm[0] = "PlanEMIHDates";
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90273", valueParm)));
							return errorDetails;
						}
					}
					
				}
			}
		} else if (StringUtils.isNotBlank(finMain.getPlanEMIHMethod()) || finMain.getPlanEMIHMaxPerYear() > 0
				|| finMain.getPlanEMIHMax() > 0 || finMain.getPlanEMIHLockPeriod() > 0 || finMain.isPlanEMICpz()) {
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90241", null)));
			return errorDetails;
		}

		return errorDetails;
	}

	/*
	 * _______________________________________________________________________________________________________________
	 * GRACE RATE
	 * _______________________________________________________________________________________________________________
	 */

	private List<ErrorDetail> graceRateValidation(FinScheduleData finScheduleData) {
		List<ErrorDetail> errorDetails = finScheduleData.getErrorDetails();
		FinanceMain finMain = finScheduleData.getFinanceMain();
		FinanceType financeType = finScheduleData.getFinanceType();
		BigDecimal zeroValue = BigDecimal.ZERO;

		// validate MinRate and MaxRate fields
		if(finMain.getGrcMinRate().compareTo(finMain.getGrcMaxRate()) > 0) {
			String[] valueParm = new String[2];
			valueParm[0] = "Grace Max Rate:"+finMain.getGrcMaxRate();
			valueParm[1] = "Grace Min Rate:"+finMain.getGrcMinRate();
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("91121", valueParm)));
		}
		//Rate Type/Rate Basis
		if (!StringUtils.equals(finMain.getGrcRateBasis(), CalculationConstants.RATE_BASIS_F)
				&& !StringUtils.equals(finMain.getGrcRateBasis(), CalculationConstants.RATE_BASIS_R)) {
			String[] valueParm = new String[1];
			valueParm[0] = finMain.getGrcRateBasis();
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90211", valueParm)));
		}

		//Actual Rate
		if (finMain.getGrcPftRate().compareTo(zeroValue) < 0) {
			String[] valueParm = new String[1];
			valueParm[0] = "Grace";
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90153", valueParm)));
		}

		//Both Grace and Base Rates found
		if (StringUtils.isNotBlank(finMain.getGraceBaseRate()) && finMain.getGrcPftRate().compareTo(zeroValue) != 0) {
			String[] valueParm = new String[1];
			valueParm[0] = "Grace";
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90154", valueParm)));
		}

		//Base Rate requested?
		if (StringUtils.isBlank(finMain.getGraceBaseRate()) && StringUtils.isNotBlank(financeType.getFinGrcBaseRate())) {
			String[] valueParm = new String[2];
			valueParm[0] = "Grace";
			valueParm[1] = finMain.getFinType();
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90232", valueParm)));
		}

		if (StringUtils.isNotBlank(finMain.getGraceBaseRate())) {
			//Allow Base Rate?
			if (StringUtils.isBlank(financeType.getFinGrcBaseRate())) {
				String[] valueParm = new String[2];
				valueParm[0] = "Grace";
				valueParm[1] = finMain.getFinType();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90136", valueParm)));
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
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90137", valueParm)));
				}

				// validate special rate code
				if (StringUtils.isNotBlank(finMain.getGraceSpecialRate())) {
					rcdCount = splRateDAO.getSpecialRateCountById(finMain.getGraceSpecialRate(), "");
					if (rcdCount <= 0) {
						String[] valueParm = new String[2];
						valueParm[0] = finMain.getGraceSpecialRate();
						valueParm[1] = "Grace";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90230", valueParm)));
					}
				}
			}
		} else if (finMain.getGrcPftRate().compareTo(BigDecimal.ZERO) > 0
				&& StringUtils.isNotBlank(financeType.getFinGrcBaseRate())) {
			String[] valueParm = new String[2];
			valueParm[0] = "Grace Profit Rate";
			valueParm[1] = finMain.getFinType();
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90204", valueParm)));
		}

		//Special Rate code
		if (StringUtils.isNotBlank(finMain.getGraceSpecialRate()) && StringUtils.isBlank(finMain.getGraceBaseRate())) {
			String[] valueParm = new String[1];
			valueParm[0] = "Grace";
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90155", valueParm)));
		}

		//Margin
		if (finMain.getGrcMargin().compareTo(zeroValue) != 0 && StringUtils.isBlank(finMain.getGraceBaseRate())) {
			String[] valueParm = new String[1];
			valueParm[0] = "Grace";
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90155", valueParm)));
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
			rate.setValueDate(DateUtility.getAppDate());
			rate = RateUtil.getRefRate(rate);
			if (rate.getErrorDetails() != null) {
				errorDetails.add(rate.getErrorDetails());
			}
			if (errorDetails != null && !errorDetails.isEmpty()) {
				return errorDetails;
			}
			netRate = rate.getNetRefRateLoan();
			
			//Check Against Minimum Rate
			if (finMain.getGrcMinRate().compareTo(zeroValue) != 0) {
				if (netRate.compareTo(finMain.getGrcMinRate()) < 0) {
					String[] valueParm = new String[2];
					valueParm[0] = round4(netRate).toString();
					valueParm[1] = round4(finMain.getGrcMinRate()).toString();
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90172", valueParm)));
				}
			}

			//Check Against Maximum Rate 
			if (finMain.getGrcMaxRate().compareTo(zeroValue) != 0) {
				if (netRate.compareTo(finMain.getGrcMaxRate()) > 0) {
					String[] valueParm = new String[2];
					valueParm[0] = round4(netRate).toString();
					valueParm[1] = round4(finMain.getGrcMaxRate()).toString();
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90173", valueParm)));
				}
			}
		} else {
			netRate = finMain.getGrcPftRate();
		}

		return errorDetails;
	}

	/*
	 * _______________________________________________________________________________________________________________
	 * VALIDATE GRACE PROFIT FREQUENCY AND NEXT PROFIT DATE
	 * _______________________________________________________________________________________________________________
	 */

	private List<ErrorDetail> gracePftFrqValidation(FinScheduleData finScheduleData) {
		List<ErrorDetail> errorDetails = finScheduleData.getErrorDetails();
		FinanceMain finMain = finScheduleData.getFinanceMain();

		//Validate Profit Frequency
		ErrorDetail tempError = FrequencyUtil.validateFrequency(finMain.getGrcPftFrq());
		if (tempError != null) {
			String[] valueParm = new String[2];
			valueParm[0] = "Grace";
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90156", valueParm)));
		}
		
		// Validate with Allowed frequency days.
		boolean isValid = validateAlwFrqDays(finMain.getGrcPftFrq(), finScheduleData.getFinanceType().getFrequencyDays());
		if(!isValid) {
			String[] valueParm = new String[3];
			valueParm[0] = "Grace";
			valueParm[1] = finMain.getGrcPftFrq();
			valueParm[2] = finScheduleData.getFinanceType().getFrequencyDays();
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90271", valueParm)));
		}
		
		//First Interest Frequency Date Vs Start Date
		if (finMain.getNextGrcPftDate().compareTo(finMain.getFinStartDate()) <= 0) {
			String[] valueParm = new String[2];
			valueParm[0] = DateUtility.formatToShortDate(finMain.getNextGrcPftDate());
			valueParm[1] = DateUtility.formatToShortDate(finMain.getFinStartDate());
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90162", valueParm)));
		}

		//Return if any error
		if (!errorDetails.isEmpty()) {
			return errorDetails;
		}

		//Default Calculated Grace End Date using terms
		if (finMain.getGraceTerms() > 0) {
			finMain.setCalGrcTerms(finMain.getGraceTerms());
			List<Calendar> scheduleDateList = FrequencyUtil.getNextDate(finMain.getGrcPftFrq(),
					finMain.getGraceTerms(), finMain.getNextGrcPftDate(), HolidayHandlerTypes.MOVE_NONE, true)
					.getScheduleList();

			Date geDate = null;
			if (scheduleDateList != null) {
				Calendar calendar = scheduleDateList.get(scheduleDateList.size() - 1);
				geDate = DateUtility
						.getDBDate(DateUtility.formatDate(calendar.getTime(), PennantConstants.DBDateFormat));
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
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90161", valueParm)));
		}

		return errorDetails;

	}

	/*
	 * _______________________________________________________________________________________________________________
	 * GRACE PROFIT REVIEW
	 * _______________________________________________________________________________________________________________
	 */

	private List<ErrorDetail> gracePftReviewValidation(FinScheduleData finScheduleData) {
		List<ErrorDetail> errorDetails = finScheduleData.getErrorDetails();
		FinanceMain finMain = finScheduleData.getFinanceMain();
		FinanceType financeType = finScheduleData.getFinanceType();

		//Allow Profit Rate Review
		if (!financeType.isFinGrcIsRvwAlw()) {
			if (finMain.isAllowGrcPftRvw() || StringUtils.isNotBlank(finMain.getGrcPftRvwFrq())
					|| finMain.getNextGrcPftRvwDate() != null) {
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90164", null)));
				return errorDetails;
			} else {
				return errorDetails;
			}
		}

		//Validate Profit Review Frequency
		ErrorDetail tempError = FrequencyUtil.validateFrequency(finMain.getGrcPftRvwFrq());
		if (tempError != null) {
			String[] valueParm = new String[2];
			valueParm[0] = "Grace";
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90157", valueParm)));
		}
		
		// Validate with Allowed frequency days.
		boolean isValid = validateAlwFrqDays(finMain.getGrcPftRvwFrq(), finScheduleData.getFinanceType().getFrequencyDays());
		if(!isValid) {
			String[] valueParm = new String[3];
			valueParm[0] = "Grace";
			valueParm[1] = finMain.getGrcPftRvwFrq();
			valueParm[2] = finScheduleData.getFinanceType().getFrequencyDays();
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90271", valueParm)));
		}

		//First Interest Review Frequency Date Vs Start Date
		if (finMain.getNextGrcPftRvwDate().compareTo(finMain.getFinStartDate()) <= 0) {
			String[] valueParm = new String[2];
			valueParm[0] = DateUtility.formatToShortDate(finMain.getNextGrcPftRvwDate());
			valueParm[1] = DateUtility.formatToShortDate(finMain.getFinStartDate());
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90166", valueParm)));
		}

		//First Interest Review Frequency Date Vs Grace Period End Date
		if (finMain.getNextGrcPftRvwDate().compareTo(finMain.getCalGrcEndDate()) > 0) {
			String[] valueParm = new String[2];
			valueParm[0] = DateUtility.formatToShortDate(finMain.getNextGrcPftRvwDate());
			valueParm[1] = DateUtility.formatToShortDate(finMain.getCalGrcEndDate());
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90165", valueParm)));
		}

		return errorDetails;
	}

	/*
	 * _______________________________________________________________________________________________________________
	 * GRACE PROFIT CAPITALIZATION
	 * _______________________________________________________________________________________________________________
	 */

	private List<ErrorDetail> gracePftCpzValidation(FinScheduleData finScheduleData) {
		List<ErrorDetail> errorDetails = finScheduleData.getErrorDetails();
		FinanceMain finMain = finScheduleData.getFinanceMain();
		FinanceType financeType = finScheduleData.getFinanceType();

		//Allow Profit Capitalization
		if (!financeType.isFinGrcIsIntCpz()) {
			if (finMain.isAllowGrcCpz() || StringUtils.isNotBlank(finMain.getGrcCpzFrq())
					|| finMain.getNextGrcCpzDate() != null) {
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90167", null)));
				return errorDetails;
			} else {
				return errorDetails;
			}
		}

		//Validate Profit Capitalization Frequency
		ErrorDetail tempError = FrequencyUtil.validateFrequency(finMain.getGrcCpzFrq());
		if (tempError != null) {
			String[] valueParm = new String[2];
			valueParm[0] = "Grace";
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90158", valueParm)));
		}
		
		// Validate with Allowed frequency days.
		boolean isValid = validateAlwFrqDays(finMain.getGrcCpzFrq(), finScheduleData.getFinanceType().getFrequencyDays());
		if(!isValid) {
			String[] valueParm = new String[3];
			valueParm[0] = "Grace";
			valueParm[1] = finMain.getGrcCpzFrq();
			valueParm[2] = finScheduleData.getFinanceType().getFrequencyDays();
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90271", valueParm)));
		}
	
		//First Interest Capitalization Frequency Date Vs Start Date
		if (finMain.getNextGrcCpzDate().compareTo(finMain.getFinStartDate()) <= 0) {
			String[] valueParm = new String[2];
			valueParm[0] = DateUtility.formatToShortDate(finMain.getNextGrcCpzDate());
			valueParm[1] = DateUtility.formatToShortDate(finMain.getFinStartDate());
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90168", valueParm)));
		}

		//First Interest Capitalization Frequency Date Vs Grace End Date
		if (finMain.getNextGrcCpzDate().compareTo(finMain.getCalGrcEndDate()) > 0) {
			String[] valueParm = new String[2];
			valueParm[0] = DateUtility.formatToShortDate(finMain.getNextGrcCpzDate());
			valueParm[1] = DateUtility.formatToShortDate(finMain.getCalGrcEndDate());
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90169", valueParm)));
		}

		return errorDetails;
	}

	/*
	 * _______________________________________________________________________________________________________________
	 * GRACE SCHEDULE VALIDATION
	 * _______________________________________________________________________________________________________________
	 */

	private List<ErrorDetail> graceSchdValidation(FinScheduleData finScheduleData) {
		List<ErrorDetail> errorDetails = finScheduleData.getErrorDetails();
		FinanceMain finMain = finScheduleData.getFinanceMain();
		FinanceType financeType = finScheduleData.getFinanceType();

		if (finMain.isAllowGrcRepay()) {
			if (!financeType.isFinIsAlwGrcRepay()) {
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90170", null)));
			} else {
				if (!StringUtils.equals(finMain.getGrcSchdMthd(), CalculationConstants.SCHMTHD_NOPAY)
						&& !StringUtils.equals(finMain.getGrcSchdMthd(), CalculationConstants.SCHMTHD_PFT)
						&& !StringUtils.equals(finMain.getGrcSchdMthd(), CalculationConstants.SCHMTHD_GRCENDPAY)) {
					String[] valueParm = new String[2];
					valueParm[0] = "Grace";
					valueParm[1] = finMain.getGrcSchdMthd();
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90210", valueParm)));
				}
			}
		} else {
			if (StringUtils.isNotBlank(finMain.getGrcSchdMthd())) {
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90171", null)));
			}
		}

		return errorDetails;
	}

	/*
	 * _______________________________________________________________________________________________________________
	 * ADVISED GRACE RATE
	 * _______________________________________________________________________________________________________________
	 */

	private List<ErrorDetail> graceAdvRateValidation(FinScheduleData finScheduleData) {
		List<ErrorDetail> errorDetails = finScheduleData.getErrorDetails();
		FinanceMain finMain = finScheduleData.getFinanceMain();
		FinanceType financeType = finScheduleData.getFinanceType();
		BigDecimal zeroValue = BigDecimal.ZERO;

		if (!StringUtils.equals(finMain.getProductCategory(), FinanceConstants.PRODUCT_STRUCTMUR)) {
			if (finMain.getGrcAdvPftRate().compareTo(zeroValue) != 0
					|| StringUtils.isNotBlank(finMain.getGrcAdvBaseRate())
					|| finMain.getGrcAdvMargin().compareTo(zeroValue) != 0) {
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90176", null)));
			}
		}

		//Actual Rate
		if (finMain.getGrcAdvPftRate().compareTo(zeroValue) < 0) {
			String[] valueParm = new String[1];
			valueParm[0] = "Grace Advice";
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90153", valueParm)));
		}

		//Both Grace and Base Rates found
		if (StringUtils.isNotBlank(finMain.getGrcAdvBaseRate())) {
			if (finMain.getGrcAdvPftRate().compareTo(zeroValue) != 0) {
				String[] valueParm = new String[1];
				valueParm[0] = "Grace Advise";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90154", valueParm)));
			}
		}

		//Base Rate requested?
		if (StringUtils.isNotBlank(finMain.getGrcAdvBaseRate())) {
			//Allow Base Rate?
			if (StringUtils.isBlank(financeType.getGrcAdvBaseRate())) {
				String[] valueParm = new String[2];
				valueParm[0] = "Grace Advised";
				valueParm[1] = finMain.getFinType();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90136", valueParm)));
			} else {
				//Base Rate code found?
				String brCode = finMain.getGrcAdvBaseRate();
				String currency = finMain.getFinCcy();

				int rcdCount = baseRateDAO.getBaseRateCountById(brCode, currency, "");
				if (rcdCount <= 0) {
					String[] valueParm = new String[2];
					valueParm[0] = brCode;
					valueParm[1] = finMain.getFinCcy();
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90137", valueParm)));
				}
			}
		}

		//Margin
		if (finMain.getGrcAdvMargin().compareTo(zeroValue) != 0 && StringUtils.isBlank(finMain.getGrcAdvBaseRate())) {
			String[] valueParm = new String[1];
			valueParm[0] = "Grace Advise";
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90155", valueParm)));
		}

		return errorDetails;

	}

	/*
	 * _______________________________________________________________________________________________________________
	 * GRACE DATES
	 * _______________________________________________________________________________________________________________
	 */

	private List<ErrorDetail> graceDatesValidation(FinScheduleData finScheduleData) {
		List<ErrorDetail> errorDetails = finScheduleData.getErrorDetails();
		FinanceMain finMain = finScheduleData.getFinanceMain();
		FinanceType financeType = finScheduleData.getFinanceType();

		Date geDate = finMain.getCalGrcEndDate();

		//If Next Profit date is not as Grace End Date, it should match with frequency
		if (finMain.getNextGrcPftDate().compareTo(geDate) != 0
				&& !FrequencyUtil.isFrqDate(finMain.getGrcPftFrq(), finMain.getNextGrcPftDate())) {
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90177", null)));
		}

		//If Next Profit Review date is not as Grace End Date, it should match with frequency
		if (financeType.isFinGrcIsRvwAlw()) {
			if (finMain.getNextGrcPftRvwDate().compareTo(geDate) != 0
					&& !FrequencyUtil.isFrqDate(finMain.getGrcPftRvwFrq(), finMain.getNextGrcPftRvwDate())) {
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90178", null)));
			}
		}

		//If Next Profit capitalization date is not as Grace End Date, it should match with frequency
		if (financeType.isFinGrcIsIntCpz()) {
			if (finMain.getNextGrcCpzDate().compareTo(geDate) != 0
					&& !FrequencyUtil.isFrqDate(finMain.getGrcCpzFrq(), finMain.getNextGrcCpzDate())) {
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90179", null)));
			}
		}
		return errorDetails;
	}

	/*
	 * _______________________________________________________________________________________________________________
	 * GRACE RATE
	 * _______________________________________________________________________________________________________________
	 */

	private List<ErrorDetail> repayRateValidation(FinScheduleData finScheduleData) {
		List<ErrorDetail> errorDetails = finScheduleData.getErrorDetails();
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
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90211", valueParm)));
		}

		//Actual Rate
		if (StringUtils.isBlank(financeType.getFinBaseRate()) && finMain.getRepayProfitRate().compareTo(zeroValue) < 0) {
			String[] valueParm = new String[1];
			valueParm[0] = REPAY;
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90153", valueParm)));
		}

		//Both Grace and Base Rates found
		if (StringUtils.isNotBlank(finMain.getRepayBaseRate())
				&& finMain.getRepayProfitRate().compareTo(zeroValue) != 0) {
			String[] valueParm = new String[1];
			valueParm[0] = REPAY;
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90154", valueParm)));
		}

		// validate base rate
		if (StringUtils.isBlank(finMain.getRepayBaseRate()) && StringUtils.isNotBlank(financeType.getFinBaseRate())) {
			String[] valueParm = new String[2];
			valueParm[0] = REPAY;
			valueParm[1] = finMain.getFinType();
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90232", valueParm)));
		}

		//Base Rate requested?
		if (StringUtils.isNotBlank(finMain.getRepayBaseRate())) {
			//Allow Base Rate?
			if (StringUtils.isBlank(financeType.getFinBaseRate())) {
				String[] valueParm = new String[2];
				valueParm[0] = REPAY;
				valueParm[1] = finMain.getFinType();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90136", valueParm)));
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
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90137", valueParm)));
				}

				// validate special rate code
				if (StringUtils.isNotBlank(finMain.getRepaySpecialRate())) {
					rcdCount = splRateDAO.getSpecialRateCountById(finMain.getRepaySpecialRate(), "");
					if (rcdCount <= 0) {
						String[] valueParm = new String[2];
						valueParm[0] = finMain.getRepaySpecialRate();
						valueParm[1] = REPAY;
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90230", valueParm)));
					}
				}
			}
		} else if (finMain.getRepayProfitRate().compareTo(BigDecimal.ZERO) > 0
				&& StringUtils.isNotBlank(financeType.getFinBaseRate())) {
			String[] valueParm = new String[2];
			valueParm[0] = "repayPftRate";
			valueParm[1] = finMain.getFinType();
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90204", valueParm)));
		}

		//Special Rate code
		if (StringUtils.isNotBlank(finMain.getRepaySpecialRate()) && StringUtils.isBlank(finMain.getRepayBaseRate())) {
			String[] valueParm = new String[1];
			valueParm[0] = REPAY;
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90155", valueParm)));
		}

		//Margin
		if (finMain.getRepayMargin().compareTo(zeroValue) != 0 && StringUtils.isBlank(finMain.getRepayBaseRate())) {
			String[] valueParm = new String[1];
			valueParm[0] = REPAY;
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90155", valueParm)));
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
			rate.setValueDate(DateUtility.getAppDate());
			rate = RateUtil.getRefRate(rate);
			if(rate.getErrorDetails()!=null){
			errorDetails.add(rate.getErrorDetails());
			}
			if (errorDetails != null && !errorDetails.isEmpty()) {
				return errorDetails;
			}
			netRate = rate.getNetRefRateLoan();
			
			//Check Against Minimum Rate 
			if (netRate.compareTo(finMain.getRpyMinRate()) < 0) {
				String[] valueParm = new String[2];
				valueParm[0] = round4(netRate).toString();
				valueParm[1] = round4(finMain.getRpyMinRate()).toString();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90174", valueParm)));
			}

			//Check Against Maximum Rate 
			if (finMain.getRpyMaxRate().compareTo(zeroValue) != 0) {
				if (netRate.compareTo(finMain.getRpyMaxRate()) > 0) {
					String[] valueParm = new String[2];
					valueParm[0] = round4(netRate).toString();
					valueParm[1] = round4(finMain.getRpyMaxRate()).toString();
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90175", valueParm)));
				}
			}
			
		} else {
			netRate = finMain.getRepayProfitRate();
		}

		return errorDetails;
	}

	/*
	 * _______________________________________________________________________________________________________________
	 * VALIDATE REPAY FREQUENCY AND NEXT PROFIT DATE
	 * _______________________________________________________________________________________________________________
	 */

	private List<ErrorDetail> repayFrqValidation(FinScheduleData finScheduleData) {
		List<ErrorDetail> errorDetails = finScheduleData.getErrorDetails();
		FinanceMain finMain = finScheduleData.getFinanceMain();
		FinanceType financeType = finScheduleData.getFinanceType();

		//Validate Repayment Frequency
		ErrorDetail tempError = FrequencyUtil.validateFrequency(finMain.getRepayFrq());
		if (tempError != null) {
			String[] valueParm = new String[2];
			valueParm[0] = "Repay";
			valueParm[1] = finMain.getRepayFrq();
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90159", valueParm)));
		}
		
		boolean isValid = validateAlwFrqDays(finMain.getRepayFrq(), finScheduleData.getFinanceType().getFrequencyDays());
		if(!isValid) {
			String[] valueParm = new String[3];
			valueParm[0] = "Repay";
			valueParm[1] = finMain.getRepayFrq();
			valueParm[2] = financeType.getFrequencyDays();
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90271", valueParm)));
		}

		//First Repayment Date Vs Start Date
		if (finMain.getNextRepayDate().compareTo(finMain.getCalGrcEndDate()) <= 0) {
			String[] valueParm = new String[2];
			valueParm[0] = DateUtility.formatToShortDate(finMain.getNextRepayDate());
			valueParm[1] = DateUtility.formatToShortDate(finMain.getCalGrcEndDate());
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90192", valueParm)));
		}

		//Return if any error
		if (!errorDetails.isEmpty()) {
			return errorDetails;
		}

		//Default Calculated Maturity Date using terms
		if (finMain.getNumberOfTerms() > 0) {
			finMain.setCalTerms(finMain.getNumberOfTerms());
			List<Calendar> scheduleDateList = FrequencyUtil.getNextDate(finMain.getRepayFrq(),
					finMain.getNumberOfTerms(), finMain.getNextRepayDate(), HolidayHandlerTypes.MOVE_NONE, true)
					.getScheduleList();

			Date matDate = null;
			if (scheduleDateList != null) {
				Calendar calendar = scheduleDateList.get(scheduleDateList.size() - 1);
				matDate = DateUtility.getDBDate(DateUtility.formatDate(calendar.getTime(),
						PennantConstants.DBDateFormat));
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
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90193", valueParm)));
		}

		return errorDetails;

	}

	/*
	 * _______________________________________________________________________________________________________________
	 * REPAY PROFIT FREQUENCY VALIDATION
	 * _______________________________________________________________________________________________________________
	 */

	private List<ErrorDetail> repayPftFrqValidation(FinScheduleData finScheduleData) {
		List<ErrorDetail> errorDetails = finScheduleData.getErrorDetails();
		FinanceMain finMain = finScheduleData.getFinanceMain();

		//Validate Profit Frequency Frequency
		ErrorDetail tempError = FrequencyUtil.validateFrequency(finMain.getRepayPftFrq());
		if (tempError != null) {
			String[] valueParm = new String[2];
			valueParm[0] = "Repay";
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90156", valueParm)));
		}

		// Validate with Allowed frequency days.
		boolean isValid = validateAlwFrqDays(finMain.getRepayPftFrq(), finScheduleData.getFinanceType().getFrequencyDays());
		if(!isValid) {
			String[] valueParm = new String[3];
			valueParm[0] = "Repay";
			valueParm[1] = finMain.getRepayPftFrq();
			valueParm[2] = finScheduleData.getFinanceType().getFrequencyDays();
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90271", valueParm)));
		}
		
		//First Repayment Frequency Date Vs Start Date/Grace End Date
		if (finMain.getNextRepayPftDate().compareTo(finMain.getCalGrcEndDate()) <= 0) {
			String[] valueParm = new String[2];
			valueParm[0] = DateUtility.formatToShortDate(finMain.getNextRepayPftDate());
			valueParm[1] = DateUtility.formatToShortDate(finMain.getCalGrcEndDate());
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90194", valueParm)));
		}

		//First Interest Frequency Date Vs Next Repayment Date
		if (finMain.getNextRepayPftDate().compareTo(finMain.getNextRepayDate()) > 0) {
			String[] valueParm = new String[2];
			valueParm[0] = DateUtility.formatToShortDate(finMain.getNextRepayPftDate());
			valueParm[1] = DateUtility.formatToShortDate(finMain.getNextRepayDate());
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90195", valueParm)));
		}

		return errorDetails;
	}

	/*
	 * _______________________________________________________________________________________________________________
	 * REPAY PROFIT REVIEW FREQUENCY VALIDATION
	 * _______________________________________________________________________________________________________________
	 */

	private List<ErrorDetail> repayPftReviewValidation(FinScheduleData finScheduleData) {
		List<ErrorDetail> errorDetails = finScheduleData.getErrorDetails();
		FinanceMain finMain = finScheduleData.getFinanceMain();
		FinanceType financeType = finScheduleData.getFinanceType();

		//Allow Profit Rate Review
		if (!financeType.isFinIsRvwAlw()) {
			if (finMain.isAllowRepayRvw() || StringUtils.isNotBlank(finMain.getRepayRvwFrq())
					|| finMain.getNextRepayRvwDate() != null) {
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90196", null)));
				return errorDetails;
			} else {
				return errorDetails;
			}
		}

		//Validate Profit Review Frequency
		ErrorDetail tempError = FrequencyUtil.validateFrequency(finMain.getRepayRvwFrq());
		if (tempError != null) {
			String[] valueParm = new String[2];
			valueParm[0] = "Repay";
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90157", valueParm)));
		}
		
		// Validate with Allowed frequency days.
		boolean isValid = validateAlwFrqDays(finMain.getRepayRvwFrq(), finScheduleData.getFinanceType().getFrequencyDays());
		if(!isValid) {
			String[] valueParm = new String[3];
			valueParm[0] = "Repay";
			valueParm[1] = finMain.getRepayRvwFrq();
			valueParm[2] = finScheduleData.getFinanceType().getFrequencyDays();
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90271", valueParm)));
		}
		//First Repayment Profit Review Date Vs Start Date/Grace End Date
		if (finMain.getNextRepayRvwDate().compareTo(finMain.getCalGrcEndDate()) <= 0) {
			String[] valueParm = new String[2];
			valueParm[0] = DateUtility.formatToShortDate(finMain.getNextRepayRvwDate());
			valueParm[1] = DateUtility.formatToShortDate(finMain.getCalGrcEndDate());
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90197", valueParm)));
		}

		//First Repayment Profit Review Date Vs Maturity Date
		if (finMain.getNextRepayRvwDate().compareTo(finMain.getCalMaturity()) > 0) {
			String[] valueParm = new String[2];
			valueParm[0] = DateUtility.formatToShortDate(finMain.getNextRepayRvwDate());
			valueParm[1] = DateUtility.formatToShortDate(finMain.getCalMaturity());
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90198", valueParm)));
		}

		return errorDetails;
	}

	/*
	 * _______________________________________________________________________________________________________________
	 * REPAY PROFIT CAPITALIZATION
	 * _______________________________________________________________________________________________________________
	 */

	private List<ErrorDetail> repayPftCpzValidation(FinScheduleData finScheduleData) {
		List<ErrorDetail> errorDetails = finScheduleData.getErrorDetails();
		FinanceMain finMain = finScheduleData.getFinanceMain();
		FinanceType financeType = finScheduleData.getFinanceType();

		//Allow Profit Capitalization
		if (!financeType.isFinIsIntCpz()) {
			if (finMain.isAllowRepayCpz() || StringUtils.isNotBlank(finMain.getRepayCpzFrq())
					|| finMain.getNextRepayCpzDate() != null) {
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90199", null)));
				return errorDetails;
			} else {
				return errorDetails;
			}
		}

		if (financeType.isFinIsIntCpz()) {
			//Validate Profit Capitalization Frequency
			ErrorDetail tempError = FrequencyUtil.validateFrequency(finMain.getRepayCpzFrq());
			if (tempError != null) {
				String[] valueParm = new String[2];
				valueParm[0] = "Repay";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90158", valueParm)));
			}

			// Validate with Allowed frequency days.
			boolean isValid = validateAlwFrqDays(finMain.getRepayCpzFrq(), finScheduleData.getFinanceType().getFrequencyDays());
			if(!isValid) {
				String[] valueParm = new String[3];
				valueParm[0] = "Repay";
				valueParm[1] = finMain.getRepayCpzFrq();
				valueParm[2] = finScheduleData.getFinanceType().getFrequencyDays();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90271", valueParm)));
			}
			
			//First Interest Capitalization Frequency Date Vs Start Date/GE Date
			if (finMain.getNextRepayCpzDate().compareTo(finMain.getCalGrcEndDate()) <= 0) {
				String[] valueParm = new String[2];
				valueParm[0] = DateUtility.formatToShortDate(finMain.getNextRepayCpzDate());
				valueParm[1] = DateUtility.formatToShortDate(finMain.getCalGrcEndDate());
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90225", valueParm)));
			}

			//First Interest Capitalization Frequency Date Vs M
			if (finMain.getNextRepayCpzDate().compareTo(finMain.getCalMaturity()) > 0) {
				String[] valueParm = new String[2];
				valueParm[0] = DateUtility.formatToShortDate(finMain.getNextRepayCpzDate());
				valueParm[1] = DateUtility.formatToShortDate(finMain.getCalMaturity());
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90226", valueParm)));
			}
		}

		return errorDetails;
	}

	/*
	 * _______________________________________________________________________________________________________________
	 * ADVISED REPAYMENT RATE
	 * _______________________________________________________________________________________________________________
	 */

	private List<ErrorDetail> repayAdvRateValidation(FinScheduleData finScheduleData) {
		List<ErrorDetail> errorDetails = finScheduleData.getErrorDetails();
		FinanceMain finMain = finScheduleData.getFinanceMain();
		FinanceType financeType = finScheduleData.getFinanceType();
		BigDecimal zeroValue = BigDecimal.ZERO;

		if (!StringUtils.equals(finMain.getProductCategory(), FinanceConstants.PRODUCT_STRUCTMUR)) {
			if (finMain.getRpyAdvPftRate().compareTo(zeroValue) != 0
					|| StringUtils.isNotBlank(finMain.getRpyAdvBaseRate())
					|| finMain.getRpyAdvMargin().compareTo(zeroValue) != 0) {
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90176", null)));
			}
		}

		//Actual Rate
		if (finMain.getRpyAdvPftRate().compareTo(zeroValue) < 0) {
			String[] valueParm = new String[1];
			valueParm[0] = "Repay Advice";
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90153", valueParm)));
		}

		//Both Grace and Base Rates found
		if (StringUtils.isNotBlank(finMain.getRpyAdvBaseRate())) {
			if (finMain.getRpyAdvPftRate().compareTo(zeroValue) != 0) {
				String[] valueParm = new String[1];
				valueParm[0] = "Repay Advise";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90154", valueParm)));
			}
		}

		//Base Rate requested?
		if (StringUtils.isNotBlank(finMain.getRpyAdvBaseRate())) {
			//Allow Base Rate?
			if (StringUtils.isBlank(financeType.getRpyAdvBaseRate())) {
				String[] valueParm = new String[2];
				valueParm[0] = "Repay Advised";
				valueParm[1] = finMain.getFinType();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90136", valueParm)));
			} else {
				//Base Rate code found?
				String brCode = finMain.getRpyAdvBaseRate();
				String currency = finMain.getFinCcy();

				int rcdCount = baseRateDAO.getBaseRateCountById(brCode, currency, "");
				if (rcdCount <= 0) {
					String[] valueParm = new String[2];
					valueParm[0] = brCode;
					valueParm[1] = finMain.getFinCcy();
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90137", valueParm)));
				}
			}
		}

		//Margin
		if (finMain.getRpyAdvMargin().compareTo(zeroValue) != 0 && StringUtils.isBlank(finMain.getRpyAdvBaseRate())) {
			String[] valueParm = new String[1];
			valueParm[0] = "Repay Advise";
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90155", valueParm)));
		}

		return errorDetails;

	}

	/*
	 * _______________________________________________________________________________________________________________
	 * BPI
	 * _______________________________________________________________________________________________________________
	 */

	private List<ErrorDetail> bpiValidation(FinScheduleData finScheduleData) {
		List<ErrorDetail> errorDetails = finScheduleData.getErrorDetails();
		FinanceMain finMain = finScheduleData.getFinanceMain();

		if (!finMain.isAlwBPI()) {
			if (!StringUtils.equals(finMain.getBpiTreatment(), FinanceConstants.BPI_NO)) {
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90229", null)));
				return errorDetails;
			}
		}
		String frqBPI = null;
		Date frqDate;
		if (finMain.isAllowGrcPeriod()) {
			frqBPI = finMain.getGrcPftFrq();
			frqDate = finMain.getNextGrcPftDate();
		} else {
			frqBPI = finMain.getRepayPftFrq();
			frqDate = finMain.getNextRepayPftDate();
		}
		if(finMain.isAlwBPI()){
		Date bpiDate = DateUtility.getDate(	DateUtility.formatUtilDate(FrequencyUtil.getNextDate(frqBPI, 1, finMain.getFinStartDate(),
										HolidayHandlerTypes.MOVE_NONE, false).getNextFrequencyDate(),PennantConstants.dateFormat));
		if (DateUtility.compare(bpiDate, frqDate) >= 0) {
			/*errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("30571", null)));
			return errorDetails;*/
			finMain.setBpiTreatment(FinanceConstants.BPI_NO);
		}
		}
		if (StringUtils.isNotBlank(finMain.getBpiTreatment())) {
			if (!StringUtils.equals(finMain.getBpiTreatment(), FinanceConstants.BPI_NO)
					&& !StringUtils.equals(finMain.getBpiTreatment(), FinanceConstants.BPI_DISBURSMENT)
					&& !StringUtils.equals(finMain.getBpiTreatment(), FinanceConstants.BPI_SCHEDULE)
					&& !StringUtils.equals(finMain.getBpiTreatment(), FinanceConstants.BPI_CAPITALIZE)
					&& !StringUtils.equals(finMain.getBpiTreatment(), FinanceConstants.BPI_SCHD_FIRSTEMI)) {
				String[] valueParm = new String[1];
				valueParm[0] = finMain.getBpiTreatment();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90185", valueParm)));
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
	private List<ErrorDetail> feeValidations(String vldGroup, FinScheduleData finSchdData, boolean isAPICall, String eventCode) {
		List<ErrorDetail> errorDetails = finSchdData.getErrorDetails();
		String finEvent = eventCode;
		boolean isOrigination = false;
		int vasFeeCount = 0;
		if(!StringUtils.equals(PennantConstants.VLD_SRV_LOAN, vldGroup)) {
			for (FinFeeDetail finFeeDetail : finSchdData.getFinFeeDetailList()) {
				if(StringUtils.equals(finFeeDetail.getFinEvent(), AccountEventConstants.ACCEVENT_VAS_FEE)) {
					vasFeeCount++;
				}
				if (StringUtils.isBlank(finFeeDetail.getFeeScheduleMethod())) {
					String[] valueParm = new String[1];
					valueParm[0] = "feeMethod";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
					return errorDetails;
				}
				// validate feeMethod
				if (!StringUtils.equals(finFeeDetail.getFeeScheduleMethod(), FinanceConstants.BPI_NO)
						&& !StringUtils.equals(finFeeDetail.getFeeScheduleMethod(), CalculationConstants.REMFEE_PART_OF_DISBURSE)
						&& !StringUtils.equals(finFeeDetail.getFeeScheduleMethod(), CalculationConstants.REMFEE_PART_OF_SALE_PRICE)
						&& !StringUtils.equals(finFeeDetail.getFeeScheduleMethod(),	CalculationConstants.REMFEE_SCHD_TO_FIRST_INSTALLMENT)
						&& !StringUtils.equals(finFeeDetail.getFeeScheduleMethod(), CalculationConstants.REMFEE_SCHD_TO_ENTIRE_TENOR)
						&& !StringUtils.equals(finFeeDetail.getFeeScheduleMethod(),	CalculationConstants.REMFEE_SCHD_TO_N_INSTALLMENTS)
						&& !StringUtils.equals(finFeeDetail.getFeeScheduleMethod(), CalculationConstants.REMFEE_PAID_BY_CUSTOMER)
						&& !StringUtils.equals(finFeeDetail.getFeeScheduleMethod(), CalculationConstants.REMFEE_WAIVED_BY_BANK)) {
					String[] valueParm = new String[2];
					valueParm[0] = finFeeDetail.getFeeScheduleMethod();
					valueParm[1] = CalculationConstants.REMFEE_PART_OF_DISBURSE + ","
							+ CalculationConstants.REMFEE_PART_OF_SALE_PRICE + ","
							+ CalculationConstants.REMFEE_SCHD_TO_FIRST_INSTALLMENT + ","
							+ CalculationConstants.REMFEE_SCHD_TO_ENTIRE_TENOR + ","
							+ CalculationConstants.REMFEE_SCHD_TO_N_INSTALLMENTS + ","
							+ CalculationConstants.REMFEE_PAID_BY_CUSTOMER + ","
							+ CalculationConstants.REMFEE_WAIVED_BY_BANK;
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90243", valueParm)));
				}

				// validate scheduleTerms
				if (StringUtils.equals(CalculationConstants.REMFEE_SCHD_TO_N_INSTALLMENTS, finFeeDetail.getFeeScheduleMethod())
						&& finFeeDetail.getTerms() <= 0) {
					String[] valueParm = new String[1];
					valueParm[0] = "ScheduleTerms";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90221", valueParm)));
				}
				
				if (StringUtils.equals(CalculationConstants.REMFEE_SCHD_TO_N_INSTALLMENTS, finFeeDetail.getFeeScheduleMethod())
						&& finFeeDetail.getTerms() > finSchdData.getFinanceMain().getNumberOfTerms()) {
					String[] valueParm = new String[2];
					valueParm[0] = "Schedule Terms";
					valueParm[1] = "Number of terms:"+finSchdData.getFinanceMain().getNumberOfTerms();
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("30568", valueParm)));
				}
			}

			isOrigination = true;
			finEvent = PennantApplicationUtil.getEventCode(finSchdData.getFinanceMain().getFinStartDate());
		} else {
			for (FinFeeDetail finFeeDetail : finSchdData.getFinFeeDetailList()) {
				if(StringUtils.isNotBlank(finFeeDetail.getFeeScheduleMethod())) {
					String[] valueParm = new String[2];
					valueParm[0] = "Fee Schedule Method";
					valueParm[1] = finFeeDetail.getFeeTypeCode();
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90269", valueParm)));
				}
			}
		}

		List<FinTypeFees> finTypeFeeDetail = null;
		if(StringUtils.equals(PennantConstants.VLD_SRV_LOAN, vldGroup) || !finSchdData.getFinanceType().isPromotionType()) {
			finTypeFeeDetail = getFinanceDetailService().getFinTypeFees(finSchdData.getFinanceMain().getFinType(), 
					finEvent, isOrigination, FinanceConstants.MODULEID_FINTYPE);
		} else {
			finTypeFeeDetail = getFinanceDetailService().getFinTypeFees(finSchdData.getFinanceType().getPromotionCode(), 
					finEvent, isOrigination, FinanceConstants.MODULEID_PROMOTION);
		}
		if (finTypeFeeDetail != null) {
			if (finTypeFeeDetail.size() == finSchdData.getFinFeeDetailList().size()-vasFeeCount) {
				for (FinFeeDetail feeDetail : finSchdData.getFinFeeDetailList()) {
					BigDecimal finWaiverAmount = BigDecimal.ZERO;
					boolean isFeeCodeFound = false;
					for (FinTypeFees finTypeFee : finTypeFeeDetail) {
						if (StringUtils.equals(feeDetail.getFeeTypeCode(), finTypeFee.getFeeTypeCode())
								|| StringUtils.equals(feeDetail.getFinEvent(), AccountEventConstants.ACCEVENT_VAS_FEE)) {
							isFeeCodeFound = true;

							// validate negative values
							if (feeDetail.getActualAmount().compareTo(BigDecimal.ZERO) < 0
									|| feeDetail.getPaidAmount().compareTo(BigDecimal.ZERO) < 0
									|| feeDetail.getWaivedAmount().compareTo(BigDecimal.ZERO) < 0) {
								String[] valueParm = new String[1];
								valueParm[0] = feeDetail.getFeeTypeCode();
								errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90259", valueParm)));
								return errorDetails;
							}

							// validate fee schedule method
							if (!finTypeFee.isAlwModifyFeeSchdMthd() && !StringUtils.equals(feeDetail.getFeeScheduleMethod(),
									finTypeFee.getFeeScheduleMethod())) {
								String[] valueParm = new String[1];
								valueParm[0] = feeDetail.getFeeTypeCode();
								errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90246", valueParm)));
								return errorDetails;
							}

							// validate paid by Customer method
							if (StringUtils.equals(finTypeFee.getFeeScheduleMethod(), CalculationConstants.REMFEE_PAID_BY_CUSTOMER)) {
								if (feeDetail.getPaidAmount().compareTo(finTypeFee.getAmount()) != 0) {
									String[] valueParm = new String[1];
									valueParm[0] = finTypeFee.getFeeTypeCode();
									errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90254", valueParm)));
									return errorDetails;
								}
							}
							// validate waived by bank method
							if (StringUtils.equals(finTypeFee.getFeeScheduleMethod(), CalculationConstants.REMFEE_WAIVED_BY_BANK)) {
								if (feeDetail.getWaivedAmount().compareTo(finWaiverAmount) != 0) {
									String[] valueParm = new String[3];
									valueParm[0] = "Waiver amount";
									valueParm[1] = "Actual waiver amount:" + String.valueOf(finWaiverAmount);
									valueParm[2] = feeDetail.getFeeTypeCode();
									errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90258", valueParm)));
									return errorDetails;
								}
							}
						}
					}
					if (!isFeeCodeFound) {
						String[] valueParm = new String[1];
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90247", valueParm)));
						return errorDetails;
					}
				}

			} else {
				String[] valueParm = new String[1];
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90244", valueParm)));
				return errorDetails;
			}
		} else {
			String[] valueParm = new String[1];
			valueParm[0] = finSchdData.getFinanceMain().getFinType();
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90245", valueParm)));
			return errorDetails;
		}

		return errorDetails;
	}

	/**
	 * 
	 * @param vldGroup
	 * @param finScheduleData
	 * @param isAPICall
	 * @return
	 */
	private List<ErrorDetail> insuranceValidations(String vldGroup, FinScheduleData finScheduleData, boolean isAPICall) {
		//TODO: write insurance validations
		return new ArrayList<ErrorDetail>();
	}

	/**
	 * Method for validate stepping details
	 * 
	 * @param vldGroup
	 * @param finScheduleData
	 * @param isAPICall
	 * @return List<ErrorDetails>
	 */
	private List<ErrorDetail> stepValidations(String vldGroup, FinScheduleData finScheduleData, boolean isAPICall) {
		List<ErrorDetail> errorDetails = finScheduleData.getErrorDetails();
		BigDecimal tenorPercTotal = BigDecimal.ZERO;
		BigDecimal emiPercTotal = BigDecimal.ZERO;
		int totalSteps = 0;
		String stepType = finScheduleData.getFinanceMain().getStepType();

		// validate number of steps
		if (finScheduleData.getStepPolicyDetails().size() < 2) {
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90233", null)));
			return errorDetails;
		}

		// validate split percentages
		for (FinanceStepPolicyDetail policyDetail : finScheduleData.getStepPolicyDetails()) {
			tenorPercTotal = tenorPercTotal.add(policyDetail.getTenorSplitPerc());
			emiPercTotal = emiPercTotal.add(policyDetail.getEmiSplitPerc());
			totalSteps++;
		}
		if (tenorPercTotal.compareTo(new BigDecimal(100)) != 0) {
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90234", null)));
			return errorDetails;
		}

		if (StringUtils.equals(stepType, FinanceConstants.STEPTYPE_EMI)) {
			BigDecimal emiPerc = emiPercTotal.divide(new BigDecimal(totalSteps), RoundingMode.HALF_UP);
			if (emiPerc.compareTo(new BigDecimal(100)) != 0) {
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90235", null)));
				return errorDetails;
			}
		} else if (StringUtils.equals(stepType, FinanceConstants.STEPTYPE_PRIBAL)) {
			BigDecimal priPerc = emiPercTotal;
			if (priPerc.compareTo(new BigDecimal(100)) != 0) {
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90236", null)));
				return errorDetails;
			}
		}

		return errorDetails;
	}

	public List<ErrorDetail> doFeeValidations(String vldSrvLoan, FinServiceInstruction finServiceInstruction, String eventCode) {
		FinScheduleData finSchdData = new FinScheduleData();
		finSchdData.setFinFeeDetailList(finServiceInstruction.getFinFeeDetails() == null?new ArrayList<FinFeeDetail>():
			finServiceInstruction.getFinFeeDetails());
		FinanceMain financeMain = financeMainDAO.getFinanceMainById(finServiceInstruction.getFinReference(), "", 
				finServiceInstruction.isWif());
		finSchdData.setFinanceMain(financeMain);
		
		return feeValidations(vldSrvLoan, finSchdData, true, eventCode);
	}
	
	/**
	 * Method for validating the Finance Tax Details
	 * 
	 * @param financeDetail
	 * @return
	 */
	private List<ErrorDetail> finTaxDetailValidation(FinanceDetail financeDetail) {
		List<ErrorDetail> errorDetails = new ArrayList<ErrorDetail>();
		FinanceTaxDetail finTaxDetail = financeDetail.getFinanceTaxDetails();
		if (finTaxDetail != null) {
			if (StringUtils.isBlank(finTaxDetail.getApplicableFor())) {
				String[] valueParm = new String[1];
				valueParm[0] = App.getLabel("label_ApplicableFor");
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
			} else {
				String applicableFor = finTaxDetail.getApplicableFor();
				List<ValueLabel> taxApplicableFor = PennantStaticListUtil.getTaxApplicableFor();
				boolean isInValidApplicablefor = false;
				for (ValueLabel value : taxApplicableFor) {
					if (StringUtils.equals(value.getValue(), applicableFor)) {
						isInValidApplicablefor = true;
						break;
					}
				}
				if (!isInValidApplicablefor) {
					String[] valueParm = new String[2];
					valueParm[0] = App.getLabel("label_ApplicableFor");
					valueParm[1] = PennantConstants.TAXAPPLICABLEFOR_PRIMAYCUSTOMER + ","
							+ PennantConstants.TAXAPPLICABLEFOR_COAPPLICANT;
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90281", valueParm)));
				}
			}
			if (StringUtils.isBlank(finTaxDetail.getCustCIF())) {
				String[] valueParm = new String[2];
				valueParm[0] = "cif";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
			} else {
				boolean isValidCustCif = false;
				if (StringUtils.equals(finTaxDetail.getApplicableFor(),
						PennantConstants.TAXAPPLICABLEFOR_PRIMAYCUSTOMER)) {
					FinanceMain finMain = financeDetail.getFinScheduleData().getFinanceMain();
					if (StringUtils.equals(finTaxDetail.getCustCIF(), finMain.getLovDescCustCIF())) {
						isValidCustCif = true;
					}
				} else if (StringUtils.equals(finTaxDetail.getApplicableFor(),
						PennantConstants.TAXAPPLICABLEFOR_COAPPLICANT)) {
					List<JointAccountDetail> jountAccountDetails = financeDetail.getJountAccountDetailList();
					if (jountAccountDetails != null && !jountAccountDetails.isEmpty()) {
						for (JointAccountDetail coApplicant : jountAccountDetails) {
							if (StringUtils.equals(finTaxDetail.getCustCIF(), coApplicant.getCustCIF())) {
								isValidCustCif = true;
								break;
							}
						}
					}
				}
				if (!isValidCustCif) {
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90910")));
					return errorDetails;
				}
			}
			if (StringUtils.isBlank(finTaxDetail.getAddrLine1())) {
				String[] valueParm = new String[1];
				valueParm[0] = App.getLabel("label_AddrLine1");
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
			} else {
				if (finTaxDetail.getAddrLine1().length() > 100) {
					String[] valueParm = new String[2];
					valueParm[0] = App.getLabel("label_AddrLine1");
					valueParm[1] = "100";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("30568", valueParm)));
				}
			}
			if (StringUtils.isNotBlank(finTaxDetail.getAddrLine2()) && finTaxDetail.getAddrLine2().length() > 100) {
				String[] valueParm = new String[2];
				valueParm[0] = App.getLabel("label_AddrLine2");
				valueParm[1] = "100";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("30568", valueParm)));
			}
			if (StringUtils.isNotBlank(finTaxDetail.getAddrLine3()) && finTaxDetail.getAddrLine3().length() > 100) {
				String[] valueParm = new String[2];
				valueParm[0] = App.getLabel("label_AddrLine3");
				valueParm[1] = "100";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("30568", valueParm)));
			}
			if (StringUtils.isNotBlank(finTaxDetail.getAddrLine4()) && finTaxDetail.getAddrLine4().length() > 100) {
				String[] valueParm = new String[2];
				valueParm[0] = App.getLabel("label_AddrLine4");
				valueParm[1] = "100";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("30568", valueParm)));
			}

			if (StringUtils.isBlank(finTaxDetail.getPinCode())) {
				String[] valueParm = new String[2];
				valueParm[0] = App.getLabel("label_PinCode");
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
				return errorDetails;
			}

			PinCode pincode = pinCodeDAO.getPinCode(finTaxDetail.getPinCode(), "_AView");
			Province province = null;
			if (pincode != null) {
				if (StringUtils.isNotBlank(finTaxDetail.getCountry())
						&& !finTaxDetail.getCountry().equalsIgnoreCase(pincode.getpCCountry())) {

					String[] valueParm = new String[2];
					valueParm[0] = finTaxDetail.getCountry();
					valueParm[1] = finTaxDetail.getPinCode();
					errorDetails.add(ErrorUtil
							.getErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90701", "", valueParm))));
				} else {
					finTaxDetail.setCountry(pincode.getpCCountry());
				}
				province = provinceDAO.getProvinceById(finTaxDetail.getCountry(), pincode.getpCProvince(), "");
				if (province != null && StringUtils.isNotBlank(finTaxDetail.getProvince())
						&& !finTaxDetail.getProvince().equalsIgnoreCase(province.getCPProvince())) {

					String[] valueParm = new String[2];
					valueParm[0] = finTaxDetail.getProvince();
					valueParm[1] = finTaxDetail.getPinCode();
					errorDetails.add(ErrorUtil
							.getErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90701", "", valueParm))));
				} else {
					finTaxDetail.setProvince(pincode.getpCProvince());
				}

				if (StringUtils.isNotBlank(finTaxDetail.getCity())
						&& !finTaxDetail.getCity().equalsIgnoreCase(pincode.getCity())) {

					String[] valueParm = new String[2];
					valueParm[0] = finTaxDetail.getCity();
					valueParm[1] = finTaxDetail.getPinCode();
					errorDetails.add(ErrorUtil
							.getErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90701", "", valueParm))));

				} else {
					finTaxDetail.setCity(pincode.getCity());
				}

			} else {
				String[] valueParm = new String[2];
				valueParm[0] = App.getLabel("label_PinCode");
				valueParm[1] = finTaxDetail.getPinCode();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90224", valueParm)));
				return errorDetails;
			}
			
			Customer customer = customerDetailsService.getCustomerByCIF(finTaxDetail.getCustCIF());
			if (customer != null) {
				finTaxDetail.setTaxCustId(customer.getCustID());
			}
			
			if (StringUtils.isNotBlank(finTaxDetail.getTaxNumber())) {
				Pattern pattern = Pattern
						.compile(PennantRegularExpressions.getRegexMapper(PennantRegularExpressions.REGEX_GSTIN));
				Matcher matcher = pattern.matcher(finTaxDetail.getTaxNumber());
				if (matcher.matches() == false) {
					String[] valueParm = new String[1];
					valueParm[0] = App.getLabel("label_FinanceTaxDetailList_TaxNumber.value");
					errorDetails.add(ErrorUtil
							.getErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90912", "", valueParm))));
					return errorDetails;
				} else {
					if (province != null && !StringUtils.equalsIgnoreCase(finTaxDetail.getTaxNumber().substring(0, 2),
							province.getTaxStateCode())) {
						String[] valueParm = new String[2];
						valueParm[0] = finTaxDetail.getTaxNumber();
						valueParm[1] = "TAX StateCode";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90911", valueParm)));
						return errorDetails;
					}
					//validate CustomerPAN
					PFSParameter pfsParameter = SysParamUtil.getSystemParameterObject("PAN_DOC_TYPE");
					if (pfsParameter != null && StringUtils.isNotBlank(pfsParameter.getSysParmValue())) {
						String[] panCardTypes = null;
						if (pfsParameter.getSysParmValue().contains(",")) {
							panCardTypes = pfsParameter.getSysParmValue().split(",");
						} else {
							panCardTypes = new String[1];
							panCardTypes[0] = pfsParameter.getSysParmValue();
						}

						boolean isValidPan = false;
						boolean isCustomerContainPan = false;
						for (String panCardType : panCardTypes) {
							CustomerDocument customerDocument = customerDocumentService
									.getApprovedCustomerDocumentById(customer.getCustID(), panCardType);
							if (customerDocument == null) {
								continue;
							}
							isCustomerContainPan = true;
							if (StringUtils.equalsIgnoreCase(customerDocument.getCustDocTitle(),
									finTaxDetail.getTaxNumber().substring(2, 12))) {
								isValidPan = true;
								break;
							}
						}
						if (isCustomerContainPan && !isValidPan) {
							///GST number {0} should be matched with {1} properties. 90911
							String[] valueParm = new String[2];
							valueParm[0] = finTaxDetail.getTaxNumber();
							valueParm[1] = "PAN Number";
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90911", valueParm)));
						}
					}

				}
			}
		}
		return errorDetails;
	}

	/**
	 * 
	 * @param frquency
	 * @param allowedFrqDays
	 * @return
	 */
	private boolean validateAlwFrqDays(String frquency, String allowedFrqDays) {
		if(StringUtils.isNotBlank(allowedFrqDays)) {
			String[] alwFrqDay = allowedFrqDays.split(PennantConstants.DELIMITER_COMMA);
			boolean isValid = false;
			for(String frqDay: alwFrqDay) {
				if(StringUtils.contains(frquency.substring(3, 5), frqDay)) {
					isValid = true;
					break;
				}
			}
			return isValid;
		}
		return true;
	}
	
	/*
	 * _______________________________________________________________________________________________________________
	 * ROUNDING
	 * _______________________________________________________________________________________________________________
	 */
	private BigDecimal round4(BigDecimal value) {
		return value.setScale(4, RoundingMode.HALF_DOWN);
	}

	/**
	 * Method for Preparation of Eligibility Data
	 * 
	 * @param detail
	 * @return
	 */
	public FinanceDetail prepareCustElgDetail(Boolean isLoadProcess) {
		FinanceDetail detail = getFinanceDetail();
		if(detail != null) {
			//Stop Resetting data multiple times on Load Processing on Record or Double click the record
			if (isLoadProcess && getFinanceDetail().getCustomerEligibilityCheck() != null) {
				return detail;
			}

			FinanceMain financeMain = detail.getFinScheduleData().getFinanceMain();
			Customer customer = detail.getCustomerDetails().getCustomer();
			//Current Finance Monthly Installment Calculation
			BigDecimal totalRepayAmount = financeMain.getTotalRepayAmt();
			BigDecimal curFinRepayAmt = BigDecimal.ZERO;
			int installmentMnts = DateUtility.getMonthsBetween(financeMain.getFinStartDate(),
					financeMain.getMaturityDate(), false);
			if (installmentMnts > 0) {
				curFinRepayAmt = totalRepayAmount.divide(new BigDecimal(installmentMnts), 0, RoundingMode.HALF_DOWN);
			}
			int months = DateUtility.getMonthsBetween(financeMain.getFinStartDate(), financeMain.getMaturityDate());

			//Get Customer Employee Designation
			String custEmpDesg = "";
			String custEmpSector = "";
			String custEmpAlocType = "";
			String custOtherIncome = "";
			BigDecimal custOtherIncomeAmt = BigDecimal.ZERO;
			String custNationality = "";
			String custEmpSts = "";
			String custSector = "";
			String custCtgCode = "";
			String custEmpType = "";
			BigDecimal custYearOfExp = BigDecimal.ZERO;
			if (detail.getCustomerDetails() != null) {
				if (detail.getCustomerDetails().getCustEmployeeDetail() != null) {
					custEmpDesg = StringUtils.trimToEmpty(detail.getCustomerDetails().getCustEmployeeDetail().getEmpDesg());
					custEmpSector = StringUtils.trimToEmpty(detail.getCustomerDetails().getCustEmployeeDetail()
							.getEmpSector());
					custEmpAlocType = StringUtils.trimToEmpty(detail.getCustomerDetails().getCustEmployeeDetail()
							.getEmpAlocType());
					custOtherIncome = StringUtils.trimToEmpty(detail.getCustomerDetails().getCustEmployeeDetail()
							.getOtherIncome());
					custOtherIncomeAmt = detail.getCustomerDetails().getCustEmployeeDetail().getAdditionalIncome();
					int custMonthsofExp = DateUtility.getMonthsBetween(detail.getCustomerDetails().getCustEmployeeDetail()
							.getEmpFrom(), DateUtility.getAppDate());
					custYearOfExp = BigDecimal.valueOf(custMonthsofExp).divide(BigDecimal.valueOf(12), 2,
							RoundingMode.CEILING);
				}
				if (ImplementationConstants.ALLOW_MULTIPLE_EMPLOYMENTS
						&& detail.getCustomerDetails().getEmploymentDetailsList() != null
						&& !detail.getCustomerDetails().getEmploymentDetailsList().isEmpty()) {
					Date custEmpFromDate = null;
					Date custEmpToDate = null;
					boolean isCurrentEmp = false;
					for (CustomerEmploymentDetail custEmpDetail : detail.getCustomerDetails().getEmploymentDetailsList()) {
						if (custEmpDetail.isCurrentEmployer()) {
							isCurrentEmp = true;
							custEmpDesg = custEmpDetail.getCustEmpDesg();
							custEmpFromDate = custEmpDetail.getCustEmpFrom();
							custEmpType = custEmpDetail.getCustEmpType();
						} else {
							if (custEmpFromDate == null) {
								custEmpFromDate = custEmpDetail.getCustEmpFrom();
							} else {
								if (custEmpDetail.getCustEmpFrom() != null
										&& custEmpDetail.getCustEmpFrom().compareTo(custEmpFromDate) < 0) {
									custEmpFromDate = custEmpDetail.getCustEmpFrom();
								}
							}
							if (!isCurrentEmp) {
								if (custEmpToDate == null) {
									custEmpToDate = custEmpDetail.getCustEmpTo();
									custEmpDesg = custEmpDetail.getCustEmpDesg();
								} else {
									if (custEmpDetail.getCustEmpTo() != null
											&& custEmpDetail.getCustEmpTo().compareTo(custEmpToDate) > 0) {
										custEmpToDate = custEmpDetail.getCustEmpTo();
										custEmpDesg = custEmpDetail.getCustEmpDesg();
									}
								}
							}
						}
						if (custEmpFromDate != null) {
							int custMonthsofExp = DateUtility.getMonthsBetween(custEmpFromDate, DateUtility.getAppDate());
							custYearOfExp = BigDecimal.valueOf(custMonthsofExp).divide(BigDecimal.valueOf(12), 2,
									RoundingMode.CEILING);
						}
					}
				}
				custNationality = StringUtils.trimToEmpty(detail.getCustomerDetails().getCustomer().getCustNationality());
				custEmpSts = StringUtils.trimToEmpty(detail.getCustomerDetails().getCustomer().getCustEmpSts());
				custSector = StringUtils.trimToEmpty(detail.getCustomerDetails().getCustomer().getCustSector());
				custCtgCode = StringUtils.trimToEmpty(detail.getCustomerDetails().getCustomer().getCustCtgCode());
			}

			// Set Customer Data to check the eligibility
			detail.setCustomerEligibilityCheck(getFinanceDetailService().getCustEligibilityDetail(customer,
					detail.getFinScheduleData().getFinanceType().getFinCategory(), financeMain.getFinReference(),
					financeMain.getFinCcy(), curFinRepayAmt, months, null, detail.getJountAccountDetailList()));

			detail.getCustomerEligibilityCheck().setReqFinAmount(financeMain.getFinAmount());
			detail.getCustomerEligibilityCheck().setDisbursedAmount(
					financeMain.getFinAmount().subtract(financeMain.getDownPayment()));
			detail.getCustomerEligibilityCheck().setReqFinType(financeMain.getFinType());
			detail.getCustomerEligibilityCheck().setFinProfitRate(financeMain.getEffectiveRateOfReturn());
			detail.getCustomerEligibilityCheck().setDownpayBank(financeMain.getDownPayBank());
			detail.getCustomerEligibilityCheck().setDownpaySupl(financeMain.getDownPaySupl());
			detail.getCustomerEligibilityCheck().setStepFinance(financeMain.isStepFinance());
			detail.getCustomerEligibilityCheck().setFinRepayMethod(financeMain.getFinRepayMethod());
			detail.getCustomerEligibilityCheck().setAlwDPSP(
					detail.getFinScheduleData().getFinanceType().isAllowDownpayPgm());
			detail.getCustomerEligibilityCheck().setAlwPlannedDefer(financeMain.getPlanDeferCount() > 0 ? true : false);
			detail.getCustomerEligibilityCheck().setInstallmentAmount(financeMain.getFirstRepay());
			detail.getCustomerEligibilityCheck().setSalariedCustomer(customer.isSalariedCustomer());
			detail.getCustomerEligibilityCheck().setCustTotalIncome(customer.getCustTotalIncome());
			detail.getCustomerEligibilityCheck().setCustOtherIncome(custOtherIncome);
			detail.getCustomerEligibilityCheck().setCustOtherIncomeAmt(custOtherIncomeAmt);
			detail.getCustomerEligibilityCheck().setCustEmpDesg(custEmpDesg);
			detail.getCustomerEligibilityCheck().setCustEmpType(custEmpType);
			detail.getCustomerEligibilityCheck().setCustEmpSector(custEmpSector);
			detail.getCustomerEligibilityCheck().setCustEmpAloc(custEmpAlocType);
			detail.getCustomerEligibilityCheck().setCustNationality(custNationality);
			detail.getCustomerEligibilityCheck().setCustEmpSts(custEmpSts);
			detail.getCustomerEligibilityCheck().setCustYearOfExp(custYearOfExp);
			detail.getCustomerEligibilityCheck().setCustSector(custSector);
			detail.getCustomerEligibilityCheck().setCustCtgCode(custCtgCode);
			detail.getCustomerEligibilityCheck().setGraceTenure(
					DateUtility.getYearsBetween(financeMain.getFinStartDate(), financeMain.getGrcPeriodEndDate()));

			detail.getCustomerEligibilityCheck().setReqFinCcy(financeMain.getFinCcy());
			detail.getCustomerEligibilityCheck().setNoOfTerms(financeMain.getNumberOfTerms());

			if (detail.getCustomerDetails().getCustEmployeeDetail() != null) {
				detail.getCustomerEligibilityCheck().setCustMonthlyIncome(
						detail.getCustomerDetails().getCustEmployeeDetail().getMonthlyIncome());
				detail.getCustomerEligibilityCheck().setCustEmpName(
						String.valueOf(detail.getCustomerDetails().getCustEmployeeDetail().getEmpName()));

			}

			detail.getCustomerEligibilityCheck().setReqFinPurpose(financeMain.getFinPurpose());
			financeMain.setCustDSR(detail.getCustomerEligibilityCheck().getDSCR());
			detail.getCustomerEligibilityCheck().setAgreeName(financeMain.getAgreeName());
			detail.getFinScheduleData().setFinanceMain(financeMain);
			setFinanceDetail(detail);
		}
		return getFinanceDetail();
	}
	/**
	 * Method for process fees
	 * 
	 * @param finScheduleData
	 * @param stp
	 * @return
	 */
	private List<ErrorDetail> doValidateFees(FinScheduleData finScheduleData, boolean stp) {
		List<ErrorDetail> errors = new ArrayList<ErrorDetail>();
		if ((finScheduleData.getFinFeeDetailList() != null && !finScheduleData.getFinFeeDetailList().isEmpty()) ) {
			errors = feeValidations(PennantConstants.VLD_CRT_LOAN, finScheduleData, true, "");
		}
		return errors;
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

	public void setRelationshipOfficerService(RelationshipOfficerService relationshipOfficerService) {
		this.relationshipOfficerService = relationshipOfficerService;
	}
	public void setFinTypePartnerBankService(FinTypePartnerBankService finTypePartnerBankService) {
		this.finTypePartnerBankService = finTypePartnerBankService;
	}
	
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}
	public void setvASConfigurationService(VASConfigurationService vASConfigurationService) {
		this.vASConfigurationService = vASConfigurationService;
	}

	public void setScriptValidationService(ScriptValidationService scriptValidationService) {
		this.scriptValidationService = scriptValidationService;
	}
	
	public void setRuleService(RuleService ruleService) {
		this.ruleService = ruleService;
	}
	
	public void setRuleExecutionUtil(RuleExecutionUtil ruleExecutionUtil) {
		this.ruleExecutionUtil = ruleExecutionUtil;
	}
	
	public void setFinTypeVASProductsDAO(FinTypeVASProductsDAO finTypeVASProductsDAO) {
		this.finTypeVASProductsDAO = finTypeVASProductsDAO;
	}
	public void setProvinceDAO(ProvinceDAO provinceDAO) {
		this.provinceDAO = provinceDAO;
	}
	public void setCityDAO(CityDAO cityDAO) {
		this.cityDAO = cityDAO;
	}
	public void setCustomerDocumentService(CustomerDocumentService customerDocumentService) {
		this.customerDocumentService = customerDocumentService;
	}
	
	public void setPartnerBankDAO(PartnerBankDAO partnerBankDAO) {
		this.partnerBankDAO = partnerBankDAO;
	}

	/**
	 * @param extendedFieldDetailsService
	 *            the extendedFieldDetailsService to set
	 */
	public void setExtendedFieldDetailsService(ExtendedFieldDetailsService extendedFieldDetailsService) {
		this.extendedFieldDetailsService = extendedFieldDetailsService;
	}
	
	public void setDocumentService(DocumentService documentService) {
		this.documentService = documentService;
	}
	public CurrencyDAO getCurrencyDAO() {
		return currencyDAO;
	}

	public void setCurrencyDAO(CurrencyDAO currencyDAO) {
		this.currencyDAO = currencyDAO;
	}
	
	public void setVehicleDealerService(VehicleDealerService vehicleDealerService) {
		this.vehicleDealerService = vehicleDealerService;
	}

	public LoanPurposeDAO getLoanPurposeDAO() {
		return loanPurposeDAO;
	}

	public void setLoanPurposeDAO(LoanPurposeDAO loanPurposeDAO) {
		this.loanPurposeDAO = loanPurposeDAO;
	}

	public void setPinCodeDAO(PinCodeDAO pinCodeDAO) {
		this.pinCodeDAO = pinCodeDAO;
	}

}
