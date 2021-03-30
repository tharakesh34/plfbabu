package com.pennant.backend.service.finance.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.resource.Labels;

import com.pennant.app.constants.AccountConstants;
import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.FrequencyCodeTypes;
import com.pennant.app.constants.HolidayHandlerTypes;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.constants.LengthConstants;
import com.pennant.app.model.RateDetail;
import com.pennant.app.util.CurrencyUtil;
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
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.finance.FinCovenantTypeDAO;
import com.pennant.backend.dao.finance.FinTypeVASProductsDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.covenant.CovenantTypeDAO;
import com.pennant.backend.dao.limit.LimitHeaderDAO;
import com.pennant.backend.dao.loanquery.QueryCategoryDAO;
import com.pennant.backend.dao.partnerbank.PartnerBankDAO;
import com.pennant.backend.dao.psl.PSLDetailDAO;
import com.pennant.backend.dao.receipts.FinReceiptHeaderDAO;
import com.pennant.backend.dao.systemmasters.CityDAO;
import com.pennant.backend.dao.systemmasters.DocumentTypeDAO;
import com.pennant.backend.dao.systemmasters.GenderDAO;
import com.pennant.backend.dao.systemmasters.LoanPurposeDAO;
import com.pennant.backend.dao.systemmasters.ProvinceDAO;
import com.pennant.backend.dao.systemmasters.SalutationDAO;
import com.pennant.backend.model.Property;
import com.pennant.backend.model.ScriptError;
import com.pennant.backend.model.ScriptErrors;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.administration.SecurityRole;
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
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.customermasters.CustomerEligibilityCheck;
import com.pennant.backend.model.customermasters.CustomerEmploymentDetail;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.extendedfield.ExtendedField;
import com.pennant.backend.model.extendedfield.ExtendedFieldData;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinOCRCapture;
import com.pennant.backend.model.finance.FinOCRDetail;
import com.pennant.backend.model.finance.FinOCRHeader;
import com.pennant.backend.model.finance.FinODPenaltyRate;
import com.pennant.backend.model.finance.FinPlanEmiHoliday;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceStepPolicyDetail;
import com.pennant.backend.model.finance.GuarantorDetail;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.model.finance.covenant.Covenant;
import com.pennant.backend.model.finance.covenant.CovenantDocument;
import com.pennant.backend.model.finance.covenant.CovenantType;
import com.pennant.backend.model.finance.financetaxdetail.FinanceTaxDetail;
import com.pennant.backend.model.finance.psl.PSLDetail;
import com.pennant.backend.model.financemanagement.FinFlagsDetail;
import com.pennant.backend.model.financemanagement.FinTypeVASProducts;
import com.pennant.backend.model.legal.LegalApplicantDetail;
import com.pennant.backend.model.legal.LegalDetail;
import com.pennant.backend.model.legal.LegalDocument;
import com.pennant.backend.model.legal.LegalPropertyDetail;
import com.pennant.backend.model.loanquery.QueryCategory;
import com.pennant.backend.model.loanquery.QueryDetail;
import com.pennant.backend.model.mandate.Mandate;
import com.pennant.backend.model.ocrmaster.OCRDetail;
import com.pennant.backend.model.ocrmaster.OCRHeader;
import com.pennant.backend.model.partnerbank.PartnerBank;
import com.pennant.backend.model.rmtmasters.FinTypeFees;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.smtmasters.PFSParameter;
import com.pennant.backend.model.solutionfactory.ExtendedFieldDetail;
import com.pennant.backend.model.solutionfactory.StepPolicyHeader;
import com.pennant.backend.model.systemmasters.City;
import com.pennant.backend.model.systemmasters.Department;
import com.pennant.backend.model.systemmasters.DocumentType;
import com.pennant.backend.model.systemmasters.LoanPurpose;
import com.pennant.backend.model.systemmasters.Province;
import com.pennant.backend.model.systemmasters.Salutation;
import com.pennant.backend.service.amtmasters.VehicleDealerService;
import com.pennant.backend.service.applicationmaster.BankDetailService;
import com.pennant.backend.service.applicationmaster.RelationshipOfficerService;
import com.pennant.backend.service.bmtmasters.BankBranchService;
import com.pennant.backend.service.collateral.CollateralSetupService;
import com.pennant.backend.service.collateral.impl.ScriptValidationService;
import com.pennant.backend.service.configuration.VASConfigurationService;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.customermasters.CustomerDocumentService;
import com.pennant.backend.service.customermasters.CustomerEMailService;
import com.pennant.backend.service.extendedfields.ExtendedFieldDetailsService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.mandate.MandateService;
import com.pennant.backend.service.pdc.ChequeHeaderService;
import com.pennant.backend.service.rmtmasters.FinTypePartnerBankService;
import com.pennant.backend.service.rulefactory.RuleService;
import com.pennant.backend.service.solutionfactory.StepPolicyService;
import com.pennant.backend.service.systemmasters.DepartmentService;
import com.pennant.backend.service.systemmasters.DocumentTypeService;
import com.pennant.backend.service.systemmasters.GeneralDepartmentService;
import com.pennant.backend.service.systemmasters.LovFieldDetailService;
import com.pennant.backend.service.systemmasters.OCRHeaderService;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.ExtendedFieldConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.MandateConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.RuleReturnType;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.backend.util.VASConsatnts;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.DocType;
import com.pennanttech.pennapps.core.feature.ModuleUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.pff.document.DocumentCategories;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.document.DocumentService;
import com.pennanttech.pff.staticlist.AppStaticList;

public class FinanceDataValidation {
	private static final Logger logger = LogManager.getLogger(FinanceDataValidation.class);

	private BaseRateDAO baseRateDAO;
	private SplRateDAO splRateDAO;
	private BranchDAO branchDAO;
	private CustomerDAO customerDAO;
	private FinanceMainDAO financeMainDAO;
	private FinanceDetailService financeDetailService;
	private BankDetailService bankDetailService;
	private BankBranchService bankBranchService;
	private DocumentTypeService documentTypeService;
	private CustomerDetailsService customerDetailsService;
	private FlagDAO flagDAO;
	private CollateralSetupService collateralSetupService;
	private MandateService mandateService;
	private StepPolicyService stepPolicyService;
	private RelationshipOfficerService relationshipOfficerService;
	private FinTypePartnerBankService finTypePartnerBankService;
	private VASConfigurationService vASConfigurationService;
	private ScriptValidationService scriptValidationService;
	private ExtendedFieldDetailsService extendedFieldDetailsService;
	private DocumentService documentService;
	private VehicleDealerService vehicleDealerService;
	private RuleService ruleService;
	private FinTypeVASProductsDAO finTypeVASProductsDAO;
	private ProvinceDAO provinceDAO;
	private CityDAO cityDAO;
	private CustomerDocumentService customerDocumentService;
	private PartnerBankDAO partnerBankDAO;
	private CurrencyDAO currencyDAO;
	private LoanPurposeDAO loanPurposeDAO;
	private PinCodeDAO pinCodeDAO;
	private FinReceiptHeaderDAO finReceiptHeaderDAO;
	private GeneralDepartmentService generalDepartmentService;
	private LovFieldDetailService lovFieldDetailService;
	private SalutationDAO salutationDAO;
	private DepartmentService departmentService;
	private DocumentTypeDAO documentTypeDAO;
	private QueryCategoryDAO queryCategoryDAO;
	private LimitHeaderDAO limitHeaderDAO;
	private PSLDetailDAO pSLDetailDAO;
	private GenderDAO genderDAO;
	private CustomerEMailService customerEMailService;
	private OCRHeaderService ocrHeaderService;
	private CovenantTypeDAO covenantTypeDAO;
	private FinCovenantTypeDAO finCovenantTypeDAO;
	private ChequeHeaderService chequeHeaderService;

	public void setQueryCategoryDAO(QueryCategoryDAO queryCategoryDAO) {
		this.queryCategoryDAO = queryCategoryDAO;
	}

	public FinanceDataValidation() {
		super();
	}

	/**
	 * Method for Validating Finance Schedule Prepared Data against application masters.
	 * 
	 * @param vldGroup
	 * @param finScheduleData
	 * @param apiFlag
	 * @param financeDetail
	 * @return
	 */
	public FinScheduleData financeDataValidation(String vldGroup, FinScheduleData finScheduleData, boolean apiFlag,
			FinanceDetail finDetail, boolean isEMI) {

		FinanceMain finMain = finScheduleData.getFinanceMain();
		FinanceType financeType = finScheduleData.getFinanceType();
		int ccyFormat = CurrencyUtil.getFormat(finMain.getFinCcy());

		List<ErrorDetail> errorDetails = null;
		boolean isAPICall = apiFlag;
		BigDecimal zeroAmount = BigDecimal.ZERO;

		// Non Finance validation
		errorDetails = nonFinanceValidation(vldGroup, finScheduleData, isAPICall, finDetail);

		if (financeType.isFinIsGenRef())
			finMain.setFinReference(null);

		if (!errorDetails.isEmpty()) {
			finScheduleData.setErrorDetails(errorDetails);
			return finScheduleData;
		}

		// Basic Details validation
		errorDetails = basicValidation(vldGroup, finScheduleData, isAPICall, finDetail, isEMI);
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
		if (finScheduleData.getVasRecordingList() != null && !finScheduleData.getVasRecordingList().isEmpty()) {
			errorDetails = vasFeeValidations(vldGroup, finScheduleData);
			if (!errorDetails.isEmpty()) {
				finScheduleData.setErrorDetails(errorDetails);
				return finScheduleData;
			}
		}

		// Fee validations
		// TODO FIX
		boolean stp;
		if (finDetail == null) {
			stp = true;
		} else {
			stp = finDetail.isStp();
		}

		//As per IMD Changes paid can be done through IMD only
		List<FinFeeDetail> feeList = finScheduleData.getFinFeeDetailList();
		if (PennantConstants.VLD_CRT_LOAN.equals(vldGroup) && !CollectionUtils.isEmpty(feeList)) {
			for (FinFeeDetail feeDetail : feeList) {
				//As per IMD Changes PAID can be happen through IMD only
				if (feeDetail.getPaidAmount() != null && BigDecimal.ZERO.compareTo(feeDetail.getPaidAmount()) != 0) {
					String[] valueParm = new String[2];
					valueParm[0] = feeDetail.getFeeTypeCode();
					valueParm[1] = "0";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("IMD006", valueParm)));
				}
			}
		}
		if (!errorDetails.isEmpty()) {
			finScheduleData.setErrorDetails(errorDetails);
			return finScheduleData;
		} //IMD

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

		if (finMain.getFinAssetValue().compareTo(BigDecimal.ZERO) > 0) {
			if (finMain.getFinAssetValue().compareTo(financeType.getFinMinAmount()) == -1) {
				String[] valueParm = new String[1];
				valueParm[0] = PennantApplicationUtil.amountFormate(financeType.getFinMinAmount(), ccyFormat);
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90132", valueParm)));
			}
		}
		// Net Loan Amount
		BigDecimal netLoanAmount = finMain.getFinAmount().subtract(finMain.getDownPayment());
		// This is violating Over Draft Loan
		if (finScheduleData.getFinanceMain().getProductCategory().equals(FinanceConstants.PRODUCT_ODFACILITY)) {
			if (netLoanAmount.compareTo(financeType.getFinMinAmount()) < 0) {
				String[] valueParm = new String[1];
				valueParm[0] = PennantApplicationUtil.amountFormate(financeType.getFinMinAmount(), ccyFormat);
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90132", valueParm)));
			}
		}

		if (financeType.getFinMaxAmount().compareTo(zeroAmount) > 0) {
			if (finMain.getFinAssetValue().compareTo(financeType.getFinMaxAmount()) > 0) {
				String[] valueParm = new String[1];
				valueParm[0] = PennantApplicationUtil.amountFormate(financeType.getFinMaxAmount(), ccyFormat);
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90133", valueParm)));
			}
		}

		if (financeType.getFinMaxAmount().compareTo(zeroAmount) > 0) {
			if (netLoanAmount.compareTo(financeType.getFinMaxAmount()) > 0) {
				String[] valueParm = new String[1];
				valueParm[0] = PennantApplicationUtil.amountFormate(financeType.getFinMaxAmount(), ccyFormat);
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
		//OCR Validations
		errorDetails.addAll(finOCRValidation(finDetail));
		if (!errorDetails.isEmpty()) {
			finScheduleData.setErrorDetails(errorDetails);
			return finScheduleData;
		}

		return finScheduleData;
	}

	/**
	 * This method will validate the OCR Details
	 * 
	 * @param finDetail
	 * @return
	 */
	private List<ErrorDetail> finOCRValidation(FinanceDetail finDetail) {
		List<ErrorDetail> errorDetails = new ArrayList<ErrorDetail>();
		if (finDetail != null) {
			String[] valueParm = new String[1];
			FinanceMain financeMain = finDetail.getFinScheduleData().getFinanceMain();
			FinanceType financeType = finDetail.getFinScheduleData().getFinanceType();
			if (financeMain != null) {
				//check OCR Header details are passed over the request if it required?
				FinOCRHeader finOCRHeader = finDetail.getFinOCRHeader();
				if (financeMain.isFinOcrRequired() && finOCRHeader == null) {
					//check default OCR configured in the loan type or not
					if (financeType != null && !StringUtils.isEmpty(financeType.getDefaultOCR())) {
						//get default OCR header details from loan type
						OCRHeader ocrHeader = ocrHeaderService.getOCRHeaderByOCRId(financeType.getDefaultOCR(),
								TableType.AVIEW.getSuffix());
						finDetail.setFinOCRHeader(copyOCRHeaderProperties(ocrHeader));
					} else {
						//If default OCR not configured in Loan Type?
						valueParm[0] = "finOCRHeader";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
						return errorDetails;
					}
				} else if (!financeMain.isFinOcrRequired() && finOCRHeader != null) {
					//If OCR required marked as false but passed OCR details in request
					valueParm[0] = "finOcrRequired";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
					return errorDetails;
				} else if (finOCRHeader != null) {
					//OCR ID
					String ocrID = finOCRHeader.getOcrID();
					if (StringUtils.isEmpty(ocrID)) {
						valueParm[0] = "ocrID";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
						return errorDetails;
					}
					//Check OCR ID is available in Loan type allowed OCR's
					if (financeType != null && !StringUtils.isEmpty(financeType.getAllowedOCRS())) {
						List<String> detailsList = Arrays.asList(financeType.getAllowedOCRS().split(","));
						if (detailsList != null && !detailsList.contains(ocrID)) {
							valueParm = new String[2];
							valueParm[0] = "ocrID: " + ocrID;
							valueParm[1] = financeType.getFinType();
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90285", valueParm)));
							return errorDetails;
						} else if (detailsList != null && detailsList.contains(ocrID)) {
							//get  OCR header details
							OCRHeader ocrHeader = ocrHeaderService.getOCRHeaderByOCRId(ocrID,
									TableType.AVIEW.getSuffix());
							//Overriding with Master Data 
							FinOCRHeader finOCRHeaderTemp = copyOCRHeaderProperties(ocrHeader);
							//check OCR Description is Passed in the request if not override with Master Data 
							if (StringUtils.isEmpty(finOCRHeader.getOcrDescription())) {
								finOCRHeader.setOcrDescription(finOCRHeaderTemp.getOcrDescription());
							}
							//check CustomerPorsion is Passed in the request if not override with Master Data 
							if (finOCRHeader.getCustomerPortion().compareTo(BigDecimal.ZERO) <= 0) {
								finOCRHeader.setCustomerPortion(finOCRHeaderTemp.getCustomerPortion());
							}
							//check OCR Type is Passed in the request if not override with Master Data 
							if (StringUtils.isEmpty(finOCRHeader.getOcrType())) {
								finOCRHeader.setOcrType(finOCRHeaderTemp.getOcrType());
							}

							//check totalDemand is Passed in the request if not override with Master Data  
							if (BigDecimal.ZERO.compareTo(finOCRHeader.getTotalDemand()) >= 0) {
								finOCRHeader.setTotalDemand(finOCRHeaderTemp.getTotalDemand());
							}

							if (PennantConstants.SEGMENTED_VALUE.equals(finOCRHeader.getOcrType())) {
								//OCR Step details are overriding with Master data if not available in the request
								if (CollectionUtils.isEmpty(finOCRHeader.getOcrDetailList())) {
									finOCRHeader.setOcrDetailList(finOCRHeaderTemp.getOcrDetailList());
								}
							}
						}
					}
					//customerPortion
					if (finOCRHeader.getCustomerPortion().compareTo(BigDecimal.ZERO) <= 0) {
						valueParm[0] = "customerPortion";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
						return errorDetails;
					}
					//ocrType
					if (StringUtils.isEmpty(finOCRHeader.getOcrType())) {
						valueParm[0] = "ocrType";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
						return errorDetails;
					} else {
						List<ValueLabel> detail = PennantStaticListUtil.getOCRApplicableList();

						boolean ocrType = false;
						for (ValueLabel value : detail) {
							if (StringUtils.equals(value.getValue(), finOCRHeader.getOcrType())) {
								ocrType = true;
								break;
							}
						}
						if (!ocrType) {
							String acceptedModes = "";
							for (ValueLabel valueLabel : detail) {
								acceptedModes = acceptedModes.concat(valueLabel.getValue()).concat(", ");
							}
							if (acceptedModes.endsWith(", ")) {
								acceptedModes = StringUtils.substring(acceptedModes, 0, acceptedModes.length() - 2);
							}
							valueParm = new String[3];
							valueParm[0] = "ocrType";
							valueParm[1] = "ocrTypes";
							valueParm[2] = acceptedModes;
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90264", valueParm)));
							return errorDetails;
						}
						//totalDemand
						if (BigDecimal.ZERO.compareTo(finOCRHeader.getTotalDemand()) >= 0) {
							valueParm = new String[2];
							valueParm[0] = "totalDemand";
							valueParm[1] = "/ equal to zero";
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("30507", valueParm)));
							return errorDetails;
						}

						//OCR Definition Validations
						if (PennantConstants.SEGMENTED_VALUE.equals(finOCRHeader.getOcrType())) {
							//OCR Step details
							if (CollectionUtils.isEmpty(finOCRHeader.getOcrDetailList())) {
								valueParm = new String[1];
								valueParm[0] = "ocrDetailList";
								errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
								return errorDetails;
							} else {
								final Set<Integer> duplicate = new HashSet<>();
								//check step sequence for definition
								for (FinOCRDetail finOCRDetail : finOCRHeader.getOcrDetailList()) {
									int stepSequence = finOCRDetail.getStepSequence();
									if (stepSequence > 0 && !duplicate.add(stepSequence)) {
										valueParm = new String[2];
										valueParm[0] = "stepSequence";
										valueParm[1] = String.valueOf(stepSequence);
										errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("41001", valueParm)));
										return errorDetails;
									} else if (stepSequence <= 0) {
										valueParm = new String[2];
										valueParm[0] = "stepSequence";
										valueParm[1] = "1";
										errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90205", valueParm)));
										return errorDetails;
									}
								}
							}
							//Can OCR Step details are acceptable for Prorata?	
						} else if (PennantConstants.PRORATA_VALUE.equals(finOCRHeader.getOcrType())) {
							if (!CollectionUtils.isEmpty(finOCRHeader.getOcrDetailList())) {
								valueParm = new String[2];
								valueParm[0] = "ocrDetailList";
								valueParm[1] = "OCR Type:" + PennantConstants.SEGMENTED_VALUE;
								errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90298", valueParm)));
								return errorDetails;
							}
						}

						//OCR Capture validations
						if (CollectionUtils.isNotEmpty(finOCRHeader.getFinOCRCapturesList())) {
							final Set<Integer> duplicate = new HashSet<>();
							//check step sequence for definition
							for (FinOCRCapture finOCRCapture : finOCRHeader.getFinOCRCapturesList()) {
								int receiptSequence = finOCRCapture.getDisbSeq();
								if (receiptSequence > 0 && !duplicate.add(receiptSequence)) {
									valueParm = new String[2];
									valueParm[0] = "receiptSeq in finOCRCapturesList";
									valueParm[1] = String.valueOf(receiptSequence);
									errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("41001", valueParm)));
									return errorDetails;
								} else if (receiptSequence <= 0) {
									valueParm = new String[2];
									valueParm[0] = "receiptSeq in finOCRCapturesList";
									valueParm[1] = "1";
									errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90205", valueParm)));
									return errorDetails;
								}
								//Demand Amount
								if (BigDecimal.ZERO.compareTo(finOCRCapture.getDemandAmount()) >= 0) {
									valueParm = new String[2];
									valueParm[0] = "demandAmount in finOCRCapturesList";
									valueParm[1] = "zero";
									errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("91121", valueParm)));
									return errorDetails;
								}
								//Paid amount
								if (BigDecimal.ZERO.compareTo(finOCRCapture.getPaidAmount()) >= 0) {
									valueParm = new String[2];
									valueParm[0] = "paidAmount in finOCRCapturesList";
									valueParm[1] = "zero";
									errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("91121", valueParm)));
									return errorDetails;
								}
								//Receipt date
								if (finOCRCapture.getReceiptDate() == null) {
									valueParm = new String[1];
									valueParm[0] = "receiptDate in finOCRCapturesList";
									errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
									return errorDetails;
								}
							}
						}
					}
				}
			}
		}
		return errorDetails;
	}

	/**
	 * This method will map the master data to fin ocr
	 * 
	 * @param ocrHeader
	 * @return
	 */
	private FinOCRHeader copyOCRHeaderProperties(OCRHeader ocrHeader) {
		FinOCRHeader finOCRHeader = new FinOCRHeader();
		finOCRHeader.setNewRecord(true);
		List<FinOCRDetail> finOCRDetailList = new ArrayList<>();
		if (ocrHeader != null) {
			finOCRHeader.setOcrType(ocrHeader.getOcrType());
			finOCRHeader.setOcrID(ocrHeader.getOcrID());
			finOCRHeader.setOcrDescription(ocrHeader.getOcrDescription());
			finOCRHeader.setCustomerPortion(ocrHeader.getCustomerPortion());
			if (StringUtils.isBlank(finOCRHeader.getRecordType())) {
				finOCRHeader.setVersion(finOCRHeader.getVersion() + 1);
				finOCRHeader.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			}
			//setting the work flow values for 
			if (!CollectionUtils.isEmpty(ocrHeader.getOcrDetailList())) {
				for (OCRDetail ocrDetail : ocrHeader.getOcrDetailList()) {
					FinOCRDetail finOCRDetail = new FinOCRDetail();
					finOCRDetail.setStepSequence(ocrDetail.getStepSequence());
					finOCRDetail.setContributor(ocrDetail.getContributor());
					finOCRDetail.setCustomerContribution(ocrDetail.getCustomerContribution());
					finOCRDetail.setFinancerContribution(ocrDetail.getFinancerContribution());
					finOCRDetail.setNewRecord(true);
					if (StringUtils.isBlank(finOCRDetail.getRecordType())) {
						finOCRDetail.setVersion(finOCRDetail.getVersion() + 1);
						finOCRDetail.setRecordType(PennantConstants.RCD_ADD);
					}
					finOCRDetailList.add(finOCRDetail);
				}
			}
			finOCRHeader.setOcrDetailList(finOCRDetailList);
		}
		return finOCRHeader;
	}

	private List<ErrorDetail> vasFeeValidations(String vldGroup, FinScheduleData finScheduleData) {
		List<ErrorDetail> errorDetails = finScheduleData.getErrorDetails();
		int vasFeeCount = 0;
		if (finScheduleData.getFinFeeDetailList() != null && !finScheduleData.getFinFeeDetailList().isEmpty()) {
			for (FinFeeDetail feeDetail : finScheduleData.getFinFeeDetailList()) {
				for (VASRecording vasRecording : finScheduleData.getVasRecordingList()) {
					String feeTypeCode = feeDetail.getFeeTypeCode();
					String productCode = vasRecording.getProductCode();
					feeTypeCode = extractFeeCode(feeTypeCode);
					productCode = extractFeeCode(productCode);

					if (StringUtils.equals(feeTypeCode, productCode)) {
						feeDetail.setFinEvent(AccountEventConstants.ACCEVENT_VAS_FEE);
						vasFeeCount++;
					}
				}
			}

			//Duplicate Fee Code check
			for (FinFeeDetail feeDetail : finScheduleData.getFinFeeDetailList()) {
				int count = 0;
				String feeTypeCode2 = feeDetail.getFeeTypeCode();
				feeTypeCode2 = extractFeeCode(feeTypeCode2);
				for (FinFeeDetail detail : finScheduleData.getFinFeeDetailList()) {
					String feeTypeCode = detail.getFeeTypeCode();
					feeTypeCode = extractFeeCode(feeTypeCode);
					if (StringUtils.equals(feeTypeCode, feeTypeCode2)) {
						count++;
						if (count > 1) {
							String[] valueParm = new String[1];
							valueParm[0] = "Fee Code: " + feeTypeCode;
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90273", valueParm)));
							return errorDetails;
						}
					}
				}
			}

			if (finScheduleData.getVasRecordingList().size() <= 0 && vasFeeCount > 0) {
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90327", null)));
			} else if (finScheduleData.getVasRecordingList().size() != vasFeeCount) {
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90328", null)));
			}

			if (errorDetails.size() > 0) {
				return errorDetails;
			}
		} else {
			//setting validation for vas fees which are available in vas Block
			if (finScheduleData.getVasRecordingList().size() != vasFeeCount) {
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90328", null)));
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
					finFeeDetail.getFeeScheduleMethod()) && finFeeDetail.getTerms() <= 0) {
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
			String feeTypeCode = feeDetail.getFeeTypeCode();
			feeTypeCode = extractFeeCode(feeTypeCode);

			for (VASRecording vasRecording : finScheduleData.getVasRecordingList()) {
				String productCode = vasRecording.getProductCode();
				productCode = extractFeeCode(productCode);

				if (StringUtils.equals(feeTypeCode, productCode)) {
					// validate negative values
					if (feeDetail.getActualAmount().compareTo(BigDecimal.ZERO) < 0
							|| feeDetail.getPaidAmount().compareTo(BigDecimal.ZERO) < 0
							|| feeDetail.getWaivedAmount().compareTo(BigDecimal.ZERO) < 0) {
						String[] valueParm = new String[1];
						valueParm[0] = feeTypeCode;
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90259", valueParm)));
						return errorDetails;
					}

					// validate vas fee amount
					if (feeDetail.getActualAmount().compareTo(vasRecording.getFee()) != 0) {
						String[] valueParm = new String[3];
						valueParm[0] = "Fee amount";
						valueParm[1] = "VAS recording fee:" + String.valueOf(vasRecording.getFee());
						valueParm[2] = feeTypeCode;
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90268", valueParm)));
						return errorDetails;
					}
					// validate actual fee amount with waiver+paid amount
					BigDecimal remainingFee = feeDetail.getActualAmount()
							.subtract(feeDetail.getWaivedAmount().add(feeDetail.getPaidAmount()));
					if (remainingFee.compareTo(BigDecimal.ZERO) < 0) {
						String[] valueParm = new String[3];
						valueParm[0] = "Sum of waiver and paid amounts";
						valueParm[1] = "Actual fee amount:" + String.valueOf(feeDetail.getActualAmount());
						valueParm[2] = feeTypeCode;
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90268", valueParm)));
						return errorDetails;
					}
				}
			}
		}
		return errorDetails;
	}

	private String extractFeeCode(String feeTypeCode) {
		if (StringUtils.startsWith(feeTypeCode, "{") && StringUtils.endsWith(feeTypeCode, "}")) {
			feeTypeCode = feeTypeCode.replace("{", "");
			feeTypeCode = feeTypeCode.replace("}", "");
		}
		return feeTypeCode;
	}

	private List<String> getVasFeeCodes(FinScheduleData finScheduleData) {
		List<String> feeCodes = new ArrayList<String>();
		if (finScheduleData.getFinanceType() == null) {
			FinanceType financeType = new FinanceType();
			// fetch the vasProduct list based on the FinanceType
			financeType.setFinTypeVASProductsList(
					finTypeVASProductsDAO.getVASProductsByFinType(finScheduleData.getFinanceMain().getFinType(), ""));
			finScheduleData.setFinanceType(financeType);
		}
		List<FinTypeVASProducts> finTypeVASProductsList = finScheduleData.getFinanceType().getFinTypeVASProductsList();
		//Vas Products configured in FinType
		if (CollectionUtils.isNotEmpty(finTypeVASProductsList)) {
			for (FinTypeVASProducts finTypeVASProducts : finTypeVASProductsList) {
				feeCodes.add(finTypeVASProducts.getVasProduct());
			}
		}
		return feeCodes;
	}

	private List<ErrorDetail> vasRecordingValidations(String vldGroup, FinScheduleData finScheduleData,
			boolean isAPICall, String string) {

		List<ErrorDetail> errorDetails = new ArrayList<ErrorDetail>();
		FinanceType financeType = finScheduleData.getFinanceType();
		// fetch the vasProduct list based on the FinanceType
		financeType
				.setFinTypeVASProductsList(finTypeVASProductsDAO.getVASProductsByFinType(financeType.getFinType(), ""));
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
				/*
				 * for (FinTypeVASProducts vasProduct : financeType.getFinTypeVASProductsList()) { if
				 * (vasProduct.isMandatory()) { mandatoryVasCount++; } }
				 */
				for (FinTypeVASProducts vasProduct : financeType.getFinTypeVASProductsList()) {
					for (VASRecording detail : finScheduleData.getVasRecordingList()) {
						String productCode = detail.getProductCode();
						productCode = extractFeeCode(productCode);
						if (StringUtils.equals(productCode, vasProduct.getVasProduct())) {
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
					if (StringUtils.equals("Loan", detail.getPostingAgainst())) {
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
						if (detail.getValueDate()
								.before(SysParamUtil.getValueAsDate(PennantConstants.APP_DFT_START_DATE))
								|| detail.getValueDate().after(DateUtility.getAppDate())) {
							String[] valueParm = new String[3];
							valueParm[0] = "Value Date";
							valueParm[1] = DateUtility
									.formatToLongDate(SysParamUtil.getValueAsDate(PennantConstants.APP_DFT_START_DATE));
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
							if (detail.getAccrualTillDate().before(DateUtility.getAppDate()) || detail
									.getAccrualTillDate().after(SysParamUtil.getValueAsDate("APP_DFT_END_DATE"))) {
								String[] valueParm = new String[3];
								valueParm[0] = "AccrualTillDate";
								valueParm[1] = DateUtility.formatToLongDate(DateUtility.getAppDate());
								valueParm[2] = DateUtility
										.formatToLongDate(SysParamUtil.getValueAsDate("APP_DFT_END_DATE"));
								errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90318", valueParm)));
								return errorDetails;
							}
						}
					} else {
						if (detail.getAccrualTillDate() != null) {
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
						} else {
							if (detail.getRecurringDate().before(DateUtility.getAppDate()) || detail.getRecurringDate()
									.after(SysParamUtil.getValueAsDate("APP_DFT_END_DATE"))) {
								String[] valueParm = new String[3];
								valueParm[0] = "RecurringDate";
								valueParm[2] = DateUtility
										.formatToLongDate(SysParamUtil.getValueAsDate("APP_DFT_END_DATE"));
								valueParm[1] = DateUtility.formatToLongDate(DateUtility.getAppDate());
								errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90318", "", valueParm)));
								return errorDetails;
							}
						}
					} else {
						if (detail.getRecurringDate() != null) {
							String[] valueParm = new String[2];
							valueParm[0] = "RecurringDate";
							valueParm[1] = "RecurringType is Active";
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90298", "", valueParm), "EN"));
							return errorDetails;
						}
						detail.setRecurringDate(DateUtility.getAppDate());
						detail.setRenewalFee(BigDecimal.ZERO);
					}
					if (StringUtils.isNotBlank(detail.getDsaId())) {
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
					List<ExtendedFieldDetail> exdFldConfig = vASConfiguration.getExtendedFieldHeader()
							.getExtendedFieldDetails();
					if (exdFldConfig != null) {
						for (ExtendedFieldDetail extended : exdFldConfig) {
							if (extended.isFieldMandatory()) {
								extendedDetailsCount++;
							}
						}
					}
					if (extendedDetailsCount > 0
							&& (detail.getExtendedDetails() == null || detail.getExtendedDetails().isEmpty())) {
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
								if (StringUtils.isBlank(Objects.toString(extendedFieldData.getFieldValue(), ""))) {
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
											if (extendedDetail.isFieldMandatory()) {
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
					if (detail.getExtendedDetails() != null) {
						for (ExtendedField details : detail.getExtendedDetails()) {
							for (ExtendedFieldData extFieldData : details.getExtendedFieldDataList()) {
								for (ExtendedFieldDetail detail1 : exdFldConfig) {
									if (StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_BASERATE,
											detail1.getFieldType())
											&& StringUtils.equals(extFieldData.getFieldName(),
													detail1.getFieldName())) {
										extFieldData.setFieldName(extFieldData.getFieldName().concat("_BR"));
									}
									if (StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_PHONE,
											detail1.getFieldType())
											&& StringUtils.equals(extFieldData.getFieldName(),
													detail1.getFieldName())) {
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

		//Validating duplicate product codes in vas recording
		List<VASRecording> vasRecordingList = finScheduleData.getVasRecordingList();
		if (CollectionUtils.isNotEmpty(vasRecordingList)) {
			for (VASRecording vasRecording : vasRecordingList) {
				int count = 0;
				String productCode = vasRecording.getProductCode();
				productCode = extractFeeCode(productCode);
				for (VASRecording vasRcding : vasRecordingList) {
					String vasCode = vasRcding.getProductCode();
					vasCode = extractFeeCode(vasCode);
					if (StringUtils.equals(productCode, vasCode)) {
						count++;
						if (count > 1) {
							String[] valueParm = new String[2];
							valueParm[0] = "Product Code: " + vasRecording.getProductCode();
							valueParm[1] = "VAS Recording";
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("41018", valueParm)));
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
			if (finODPenaltyRate.getODChargeAmtOrPerc() == null) {
				finODPenaltyRate.setODChargeAmtOrPerc(BigDecimal.ZERO);
			}
			if (finODPenaltyRate.getODMaxWaiverPerc() == null) {
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
				if ((StringUtils.isBlank(finODPenaltyRate.getODChargeType())
						|| finODPenaltyRate.getODChargeAmtOrPerc().compareTo(BigDecimal.ZERO) <= 0)
						&& !(StringUtils.equals(finScheduleData.getFinanceMain().getProductCategory(),
								FinanceConstants.PRODUCT_ODFACILITY))) {
					String[] valueParm = new String[1];
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90314", valueParm)));
				}
				if (StringUtils.equals(finODPenaltyRate.getODChargeType(), FinanceConstants.PENALTYTYPE_FLAT)
						|| StringUtils.equals(finODPenaltyRate.getODChargeType(),
								FinanceConstants.PENALTYTYPE_FLAT_ON_PD_MTH)) {
					finODPenaltyRate.setODChargeCalOn("");
				}
				if ((StringUtils.isBlank(finODPenaltyRate.getODChargeCalOn())) && (StringUtils
						.equals(finODPenaltyRate.getODChargeType(), FinanceConstants.PENALTYTYPE_PERC_ONETIME)
						|| StringUtils.equals(finODPenaltyRate.getODChargeType(),
								FinanceConstants.PENALTYTYPE_PERC_ON_DUEDAYS)
						|| StringUtils.equals(finODPenaltyRate.getODChargeType(),
								FinanceConstants.PENALTYTYPE_PERC_ON_PD_MTH))) {
					String[] valueParm = new String[2];
					valueParm[0] = "odChargeCalOn";
					valueParm[1] = "odChargeType" + FinanceConstants.PENALTYTYPE_PERC_ONETIME + ","
							+ FinanceConstants.PENALTYTYPE_PERC_ON_DUEDAYS + ","
							+ FinanceConstants.PENALTYTYPE_PERC_ON_PD_MTH;
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
			if (StringUtils.equals(finODPenaltyRate.getODChargeType(), FinanceConstants.PENALTYTYPE_PERC_ONETIME)
					|| StringUtils.equals(finODPenaltyRate.getODChargeType(),
							FinanceConstants.PENALTYTYPE_PERC_ON_DUEDAYS)
					|| StringUtils.equals(finODPenaltyRate.getODChargeType(),
							FinanceConstants.PENALTYTYPE_PERC_ON_PD_MTH)) {
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
				if (!finODCCalculatedOnSts && (StringUtils.equals(finODPenaltyRate.getODChargeType(),
						FinanceConstants.PENALTYTYPE_PERC_ONETIME)
						|| StringUtils.equals(finODPenaltyRate.getODChargeType(),
								FinanceConstants.PENALTYTYPE_PERC_ON_DUEDAYS)
						|| StringUtils.equals(finODPenaltyRate.getODChargeType(),
								FinanceConstants.PENALTYTYPE_PERC_ON_PD_MTH))) {
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
	 * ========================================================================= =======================================
	 * VALIDATE Finance Details =========================================================================
	 * =======================================
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

		if (!financeDetail.isStp()) {
			/*
			 * if(StringUtils.isBlank(financeDetail.getProcessStage())){ String[] valueParm = new String[1];
			 * valueParm[0] = "ProcessStage"; errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90502",
			 * valueParm))); finScheduleData.setErrorDetails(errorDetails); return finScheduleData; }
			 */
			if (financeDetail.getFinScheduleData().getFinanceMain().isQuickDisb()) {
				String[] valueParm = new String[2];
				valueParm[0] = "QuickDisb";
				valueParm[1] = "stp";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90298", valueParm)));
				finScheduleData.setErrorDetails(errorDetails);
				return finScheduleData;
			}
		}

		if (isCreateLoan && financeDetail.isStp()) {
			if (financeDetail.getFinScheduleData().getFinanceMain().isLegalRequired()) {
				String[] valueParm = new String[2];
				valueParm[0] = "LegalRequired";
				valueParm[1] = "stp Process";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90329", valueParm)));
				finScheduleData.setErrorDetails(errorDetails);
				return finScheduleData;
			}
		}

		if (isCreateLoan && !financeDetail.isStp()) {
			List<LegalDetail> legalDetails = financeDetail.getLegalDetailsList();
			if (!finMain.isLegalRequired()) {
				if (legalDetails != null && !CollectionUtils.isEmpty(legalDetails)) {
					String[] valueParm = new String[2];
					valueParm[0] = "Legal Details";
					valueParm[1] = "LegalRequired";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90298", valueParm)));
					finScheduleData.setErrorDetails(errorDetails);
					return finScheduleData;
				}
			}

			// validate Legal Details
			errorDetails = doLegalDetailsValidation(financeDetail);
			if (!CollectionUtils.isEmpty(errorDetails)) {
				finScheduleData.setErrorDetails(errorDetails);
				return finScheduleData;
			}

			if (legalDetails != null && !CollectionUtils.isEmpty(legalDetails)) {
				for (LegalDetail legalDetail : legalDetails) {

					// validate applicant details
					if (legalDetail.getApplicantDetailList() != null
							&& !CollectionUtils.isEmpty(legalDetail.getApplicantDetailList())) {
						errorDetails = validateLegalApplicant(legalDetail.getApplicantDetailList());
						if (!CollectionUtils.isEmpty(errorDetails)) {
							finScheduleData.setErrorDetails(errorDetails);
							return finScheduleData;
						}
					}

					// validate Legal Property Details
					errorDetails = validatePropertyDetails(legalDetail.getPropertyDetailList());
					if (!CollectionUtils.isEmpty(errorDetails)) {
						finScheduleData.setErrorDetails(errorDetails);
						return finScheduleData;
					}

					// validate Legal Document Details
					errorDetails = validateLegalDocument(legalDetail.getDocumentList());
					if (!CollectionUtils.isEmpty(errorDetails)) {
						finScheduleData.setErrorDetails(errorDetails);
						return finScheduleData;
					}

					// validate Legal Query Details
					if (legalDetail.getQueryDetail() != null) {
						errorDetails = validateLegalQueryDetail(legalDetail.getQueryDetail());
						if (!CollectionUtils.isEmpty(errorDetails)) {
							finScheduleData.setErrorDetails(errorDetails);
							return finScheduleData;
						}
					}
				}

			}

		}

		// validate FinReference
		String financeReference = null;
		if (financeDetail.getFinScheduleData().getFinReference() != null) {
			financeReference = financeDetail.getFinScheduleData().getFinReference();
		} else {
			financeReference = finMain.getFinReference();
		}

		// Temp comment

		ErrorDetail error = validateFinReference(financeReference, finScheduleData, vldGroup);
		if (error != null) {
			errorDetails.add(error);
		}

		// Temp comment
		// validate external reference
		if (financeDetail.getFinScheduleData().getExternalReference() != null
				&& !financeDetail.getFinScheduleData().getExternalReference().isEmpty()) {
			boolean isExtAssigned = finReceiptHeaderDAO
					.isExtRefAssigned(financeDetail.getFinScheduleData().getExternalReference());
			if (isExtAssigned) {
				String[] valueParm = new String[1];
				valueParm[0] = " External Reference Already Assigned to Finance ";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("30550", valueParm)));
				finScheduleData.setErrorDetails(errorDetails);
				return finScheduleData;
			}
		}

		//FIXME: PV 28AUG19: Already taken care in defaulting?
		/*
		 * // Validate customer if ((isCreateLoan || StringUtils.isNotBlank(finMain.getLovDescCustCIF()))) { Customer
		 * customer = customerDAO.getCustomerByCIF(finMain.getLovDescCustCIF(), ""); if (customer == null) { String[]
		 * valueParm = new String[1]; valueParm[0] = finMain.getLovDescCustCIF();
		 * errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90101", valueParm)));
		 * finScheduleData.setErrorDetails(errorDetails); return finScheduleData; } else { CustomerDetails
		 * customerDetails = new CustomerDetails(); customerDetails.setCustomer(customer);
		 * financeDetail.setCustomerDetails(customerDetails);
		 * financeDetail.getFinScheduleData().getFinanceMain().setCustID(customer.getCustID()); } }
		 */

		if ((isCreateLoan && StringUtils.isNotBlank(finMain.getOldFinReference()))) {
			int count = financeMainDAO.getCountByOldFinReference(finMain.getOldFinReference());
			if (count > 0) {
				String[] valueParm = new String[2];
				valueParm[0] = "HostReference";
				valueParm[1] = finMain.getOldFinReference();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("30506", valueParm)));
			}

		}

		FinanceType financeType = finScheduleData.getFinanceType();
		finScheduleData.setFinanceType(financeType);
		if (finMain.getFinContractDate() == null) {
			finMain.setFinContractDate(financeType.getStartDate());
		} else {
			if (finMain.getFinContractDate().compareTo(finMain.getFinStartDate()) > 0) {
				String[] valueParm = new String[2];
				valueParm[0] = DateUtility.format(finMain.getFinContractDate(), PennantConstants.XMLDateFormat);
				valueParm[1] = DateUtility.format(finMain.getFinStartDate(), PennantConstants.XMLDateFormat);
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("65030", valueParm)));
			}
		}

		if (financeType.isLimitRequired() && ImplementationConstants.LIMIT_INTERNAL) {
			/*
			 * if (StringUtils.isBlank(finMain.getFinLimitRef())) { String[] valueParm = new String[1]; valueParm[0] =
			 * "finLimitRef"; errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90502", valueParm))); } else {
			 * //TODO }
			 */
			long count = limitHeaderDAO.isLimitBlock(finMain.getCustID(), "", true);
			if (count > 0) {
				String[] valueParm = new String[2];
				valueParm[0] = "Loan";
				valueParm[1] = "blocked limit Customer";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90204", valueParm)));
			}

		}

		if (financeType.isFinCollateralReq() && financeDetail.isStp()) {
			if (financeDetail.getCollateralAssignmentList() == null
					|| financeDetail.getCollateralAssignmentList().isEmpty()) {
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

		String dsaCodeRef = finMain.getDsaCodeReference();

		if (StringUtils.isNotBlank(dsaCodeRef)) {
			/*
			 * RelationshipOfficer relationshipOfficer = relationshipOfficerService
			 * .getApprovedRelationshipOfficerById(finMain.getDsaCode()); if (relationshipOfficer == null) { String[]
			 * valueParm = new String[1]; valueParm[0] = finMain.getDsaCode();
			 * errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90501", valueParm))); }
			 */

			VehicleDealer vehicleDealer = vehicleDealerService.getApprovedVehicleDealerById(dsaCodeRef, "DSA", "");
			if (vehicleDealer == null) {
				String[] valueParm = new String[1];
				valueParm[0] = finMain.getDsaCode();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90501", valueParm)));
			} else {
				finMain.setDsaCode(String.valueOf(vehicleDealer.getDealerId()));
			}
		}
		if (StringUtils.isNotBlank(finMain.getAccountsOfficerReference())) {
			/*
			 * VehicleDealer vehicleDealer = vehicleDealerService.getApprovedVehicleDealerById(finMain
			 * .getAccountsOfficer()); if (vehicleDealer == null) { String[] valueParm = new String[1]; valueParm[0] =
			 * String.valueOf(finMain.getAccountsOfficer()); errorDetails.add(ErrorUtil.getErrorDetail(new
			 * ErrorDetail("90501", valueParm))); }
			 */
			VehicleDealer vehicleDealer = vehicleDealerService.getApprovedVehicleDealerById(
					finMain.getAccountsOfficerReference(), VASConsatnts.VASAGAINST_PARTNER, "");
			if (vehicleDealer == null) {
				String[] valueParm = new String[1];
				valueParm[0] = String.valueOf(finMain.getAccountsOfficer());
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90501", valueParm)));
			} else {
				finMain.setAccountsOfficer(vehicleDealer.getDealerId());
			}
		}
		if (StringUtils.isNotBlank(finMain.getSalesDepartment())) {
			/*
			 * RelationshipOfficer relationshipOfficer = relationshipOfficerService.getApprovedRelationshipOfficerById(
			 * finMain .getSalesDepartment()); if (relationshipOfficer == null) { String[] valueParm = new String[1];
			 * valueParm[0] = finMain.getSalesDepartment(); errorDetails.add(ErrorUtil.getErrorDetail(new
			 * ErrorDetail("90501", valueParm))); }
			 */
			/*
			 * GeneralDepartment generalDepartment = generalDepartmentService
			 * .getApprovedGeneralDepartmentById(finMain.getSalesDepartment());
			 * 
			 * if (generalDepartment == null) { String[] valueParm = new String[1]; valueParm[0] =
			 * finMain.getSalesDepartment(); errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90501",
			 * valueParm))); }
			 */
			Department department = departmentService.getApprovedDepartmentById(finMain.getSalesDepartment());

			if (department == null) {
				String[] valueParm = new String[1];
				valueParm[0] = finMain.getSalesDepartment();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90501", valueParm)));
			}
		}
		if (StringUtils.isNotBlank(finMain.getDmaCodeReference())) {
			/*
			 * RelationshipOfficer relationshipOfficer = relationshipOfficerService
			 * .getApprovedRelationshipOfficerById(finMain.getDmaCode()); if (relationshipOfficer == null) { String[]
			 * valueParm = new String[1]; valueParm[0] = finMain.getDsaCode();
			 * errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90501", valueParm))); }
			 */
			VehicleDealer vehicleDealer = vehicleDealerService
					.getApprovedVehicleDealerById(finMain.getDmaCodeReference(), "DMA", "");
			if (vehicleDealer == null) {
				String[] valueParm = new String[1];
				valueParm[0] = finMain.getDmaCode();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90501", valueParm)));
			} else {
				finMain.setDmaCode(String.valueOf(vehicleDealer.getDealerId()));
			}
		}
		if (StringUtils.isNotBlank(finMain.getReferralId())) {
			RelationshipOfficer relationshipOfficer = relationshipOfficerService
					.getApprovedRelationshipOfficerById(finMain.getReferralId());
			if (relationshipOfficer == null) {
				String[] valueParm = new String[1];
				valueParm[0] = finMain.getReferralId();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90501", valueParm)));
			}
		}
		// employee Name Validation
		if (StringUtils.isNotBlank(finMain.getEmployeeName())) {
			RelationshipOfficer relationshipOfficer = relationshipOfficerService
					.getApprovedRelationshipOfficerById(finMain.getEmployeeName());
			if (relationshipOfficer == null) {
				String[] valueParm = new String[1];
				valueParm[0] = finMain.getEmployeeName();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90501", valueParm)));
			}
		}
		// eligibulity method Validation
		if (finMain.getEligibilityMethod() > 0) {
			int count = lovFieldDetailService.getApprovedLovFieldDetailCountById(finMain.getEligibilityMethod(),
					"ELGMETHOD");
			if (count <= 0) {
				String[] valueParm = new String[2];
				valueParm[0] = "ELGMETHOD";
				valueParm[1] = String.valueOf(finMain.getEligibilityMethod());
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90701", valueParm)));
			}
		}
		// Validate Connecter
		if (StringUtils.isNotBlank(finMain.getConnectorReference())) {
			VehicleDealer vehicleDealer = vehicleDealerService.getApprovedVehicleDealerById(finMain.getConnector());
			String[] valueParm = new String[1];
			if (vehicleDealer == null) {
				valueParm[0] = String.valueOf(finMain.getConnector());
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90501", valueParm)));
			} else if (vehicleDealer != null && !"CONN".equals(vehicleDealer.getDealerType())) {
				valueParm[0] = String.valueOf(finMain.getConnector());
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90501", valueParm)));
			} else {
				finMain.setConnector(vehicleDealer.getDealerId());
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

		// Validate Repayment Method
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

			// Disbursement is not mandatory for Over Draft schedule
			if (!financeDetail.getFinScheduleData().getFinanceMain().getProductCategory()
					.equals(FinanceConstants.PRODUCT_ODFACILITY)) {
				errorDetails = disbursementValidation(financeDetail);
				if (!errorDetails.isEmpty()) {
					finScheduleData.setErrorDetails(errorDetails);
					return finScheduleData;
				}
			}

			errorDetails = mandateValidation(financeDetail, PennantConstants.VLD_CRT_LOAN);
			if (!errorDetails.isEmpty()) {
				finScheduleData.setErrorDetails(errorDetails);
				return finScheduleData;
			}
			if (CollectionUtils.isNotEmpty(financeDetail.getCovenants()))
				errorDetails = covenantValidation(financeDetail.getFinScheduleData().getFinanceMain(),
						financeDetail.getCovenants(), "LOS");
			if (!errorDetails.isEmpty()) {
				finScheduleData.setErrorDetails(errorDetails);
				return finScheduleData;
			}

			errorDetails = documentValidation(financeDetail);
			if (!errorDetails.isEmpty()) {
				finScheduleData.setErrorDetails(errorDetails);
				return finScheduleData;
			}

			if (financeDetail.getChequeHeader() != null)
				errorDetails = chequeHeaderService.chequeValidation(financeDetail, PennantConstants.VLD_CRT_LOAN, "");
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

			if (financeDetail.getPslDetail() != null) {
				errorDetails = pslValidation(financeDetail);
				if (!CollectionUtils.isEmpty(errorDetails)) {
					finScheduleData.setErrorDetails(errorDetails);
					return finScheduleData;
				}
			}
			// ExtendedFieldDetails Validation
			String subModule = financeDetail.getFinScheduleData().getFinanceMain().getFinCategory();
			// ### 02-05-2018-Start- story #334 Extended fields for loan
			// servicing
			if ((financeDetail.isStp()
					|| (!financeDetail.isStp() && CollectionUtils.isNotEmpty(financeDetail.getExtendedDetails())))) {
				errorDetails = extendedFieldDetailsService.validateExtendedFieldDetails(
						financeDetail.getExtendedDetails(), ExtendedFieldConstants.MODULE_LOAN, subModule,
						FinanceConstants.FINSER_EVENT_ORG);
			}

			// ### 02-05-2018-END
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
	private ErrorDetail validateFinReference(String finReference, FinScheduleData finScheduleData, String vldGroup) {
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
	 * 
	 * @param financeDetail
	 * 
	 * @return
	 */
	private List<ErrorDetail> validateUpdateFinance(FinanceDetail financeDetail) {
		List<ErrorDetail> errorDetails = new ArrayList<>();

		String type = TableType.TEMP_TAB.getSuffix();
		FinanceMain finMain = financeMainDAO.getFinanceMainById(financeDetail.getFinReference(), type, false);
		if (finMain == null) {
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90409", null)));
			return errorDetails;
		}
		// fetch Finance Schedule details
		FinScheduleData finScheduleData = financeDetailService.getFinSchDataById(finMain.getFinReference(), type,
				false);
		financeDetail.setFinScheduleData(finScheduleData);

		// validate disbursement details
		if (financeDetail.getAdvancePaymentsList() != null && !financeDetail.getAdvancePaymentsList().isEmpty()) {
			errorDetails = disbursementValidation(financeDetail);
			if (!errorDetails.isEmpty()) {
				return errorDetails;
			}
		}

		// validate Mandate details
		if (financeDetail.getMandate() != null) {
			errorDetails = mandateValidation(financeDetail, PennantConstants.VLD_UPD_LOAN);
			if (!errorDetails.isEmpty()) {
				return errorDetails;
			}
		}

		// Extended Field Details Validation
		if ((financeDetail.isStp()) || (financeDetail.getExtendedDetails() != null
				&& !financeDetail.getExtendedDetails().isEmpty() && !financeDetail.isStp())) {
			String subModule = financeDetail.getFinScheduleData().getFinanceMain().getFinCategory();
			errorDetails = extendedFieldDetailsService.validateExtendedFieldDetails(financeDetail.getExtendedDetails(),
					ExtendedFieldConstants.MODULE_LOAN, subModule, FinanceConstants.FINSER_EVENT_ORG);
			if (!errorDetails.isEmpty()) {
				return errorDetails;
			}
		}

		// Finance document details Validation
		if (financeDetail.getDocumentDetailsList() != null && !financeDetail.getDocumentDetailsList().isEmpty()) {
			errorDetails = documentService.validateFinanceDocuments(financeDetail);
			if (!errorDetails.isEmpty()) {
				return errorDetails;
			}
		}

		// validate coApplicants details
		if (financeDetail.getJountAccountDetailList() != null && !financeDetail.getJountAccountDetailList().isEmpty()) {
			errorDetails = jointAccountDetailsValidation(financeDetail);
			if (!errorDetails.isEmpty()) {
				return errorDetails;
			}
		}
		return errorDetails;
	}

	private List<ErrorDetail> finCollateralValidation(FinanceDetail financeDetail) {
		List<ErrorDetail> errorDetails = new ArrayList<ErrorDetail>();
		List<CollateralAssignment> finCollateralAssignmentDetails = financeDetail.getCollateralAssignmentList();
		if ((financeDetail.isStp())
				|| (!financeDetail.isStp() && CollectionUtils.isNotEmpty(finCollateralAssignmentDetails))) {
			if (CollectionUtils.isNotEmpty(finCollateralAssignmentDetails)) {
				boolean finColltReq = financeDetail.getFinScheduleData().getFinanceType().isFinCollateralReq();
				if (!finColltReq) {
					String[] valueParm = new String[2];
					valueParm[0] = App.getLabel("label_Collateral");
					valueParm[1] = financeDetail.getFinScheduleData().getFinanceMain().getFinType();
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90329", valueParm)));
					return errorDetails;
				}
				BigDecimal curAssignValue = BigDecimal.ZERO;
				BigDecimal totalAvailAssignValue = BigDecimal.ZERO;
				boolean requiredAssignValidation = false;
				List<String> assignReferences = new ArrayList<>();
				List<String> collReferences = new ArrayList<>();

				for (CollateralAssignment collateralAssignment : finCollateralAssignmentDetails) {

					boolean avilId = false;
					boolean bothAvialble = false;
					List<String> colltype = new ArrayList<>();
					if (StringUtils.isBlank(collateralAssignment.getCollateralRef())) {
						if (CollectionUtils.isEmpty(financeDetail.getCollaterals())) {
							// collateral setup is mandatory
							String[] valueParm = new String[2];
							valueParm[0] = "CollateralSetup";
							valueParm[1] = "CollateralRef";
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90123", valueParm)));
							return errorDetails;
						}
					}

					if (collReferences.contains(collateralAssignment.getCollateralRef())) {
						String[] valueParm = new String[2];
						valueParm[1] = collateralAssignment.getCollateralRef();
						valueParm[0] = "CollateralReference" + ":";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("41001", valueParm)));
						return errorDetails;
					} else {
						if (StringUtils.isNotBlank(collateralAssignment.getCollateralRef())) {
							collReferences.add(collateralAssignment.getCollateralRef());
						}
					}
					if (financeDetail.getCollaterals() != null) {
						if (assignReferences.contains(collateralAssignment.getAssignmentReference())) {
							String[] valueParm = new String[2];
							valueParm[1] = collateralAssignment.getAssignmentReference();
							valueParm[0] = "AssignmentReference" + ":";
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("41001", valueParm)));
							return errorDetails;
						} else {
							if (StringUtils.isNotBlank(collateralAssignment.getAssignmentReference())) {
								assignReferences.add(collateralAssignment.getAssignmentReference());
							}
						}
						if (finCollateralAssignmentDetails.size() != financeDetail.getCollaterals().size()) {
							String[] valueParm = new String[1];
							valueParm[0] = "CollateralSetupSize Equal";
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
							return errorDetails;
						}
						List<String> collAssignReferences = new ArrayList<>();
						for (CollateralSetup setupDetails : financeDetail.getCollaterals()) {
							if (StringUtils.isBlank(setupDetails.getAssignmentReference())) {
								String[] valueParm = new String[2];
								valueParm[0] = "CollateralDetails:AssignmentReference";
								errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
								return errorDetails;
							}

							if (StringUtils.isNotBlank(collateralAssignment.getCollateralRef())
									&& StringUtils.isNotBlank(collateralAssignment.getAssignmentReference())) {
								bothAvialble = true;
								break;
							}
							if (StringUtils.equals(collateralAssignment.getAssignmentReference(),
									setupDetails.getAssignmentReference())) {
								avilId = true;
							}

							if (colltype.contains(setupDetails.getCollateralType())) {
								String[] valueParm = new String[2];
								valueParm[1] = setupDetails.getCollateralType();
								valueParm[0] = PennantJavaUtil.getLabel("label_CollateralType") + ":";
								errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("41001", valueParm)));
								return errorDetails;
							} else {
								colltype.add(setupDetails.getCollateralType());
							}

							if (collAssignReferences.contains(setupDetails.getAssignmentReference())) {
								String[] valueParm = new String[2];
								valueParm[1] = setupDetails.getAssignmentReference();
								valueParm[0] = "AssignmentReference" + ":";
								errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("41001", valueParm)));
								return errorDetails;
							} else {
								if (StringUtils.isNotBlank(setupDetails.getAssignmentReference())) {
									collAssignReferences.add(setupDetails.getAssignmentReference());
								}
							}
							String collateralType = financeDetail.getFinScheduleData().getFinanceType()
									.getCollateralType();
							if (StringUtils.isNotBlank(collateralType)) {
								boolean isCollateralFound = false;
								String[] types = collateralType.split(PennantConstants.DELIMITER_COMMA);
								for (String type : types) {
									if (StringUtils.equals(type, setupDetails.getCollateralType())) {
										isCollateralFound = true;
									}
								}
								if (!isCollateralFound) {
									String[] valueParm = new String[2];
									valueParm[0] = "collateralType";
									valueParm[1] = "LoanType";
									errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90329", valueParm)));
									return errorDetails;
								}
							}
							AuditDetail auditDetail = collateralSetupService.doValidations(setupDetails, "create");

							if (auditDetail.getErrorDetails() != null && !auditDetail.getErrorDetails().isEmpty()) {
								return auditDetail.getErrorDetails();
							}
						}
					}

					// validation
					if (bothAvialble) {
						String[] valueParm = new String[2];
						valueParm[0] = "Collateral Reference";
						valueParm[1] = "Assignment Reference";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("30511", valueParm)));
						return errorDetails;
					}
					if (!avilId && StringUtils.isBlank(collateralAssignment.getCollateralRef())) {
						String[] valueParm = new String[2];
						valueParm[0] = "AssignmentReference";
						valueParm[1] = "CollateralSetup or Assingment";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90329", valueParm)));
						return errorDetails;
					}

					/*
					 * if (StringUtils.isEmpty(collateralAssignment. getCollateralRef()) ) { String[] valueParm = new
					 * String[1]; valueParm[0] = "collateralRef"; errorDetails.add(ErrorUtil.getErrorDetail(new
					 * ErrorDetail("90502", valueParm))); return errorDetails; }
					 */
					BigDecimal assignPerc = collateralAssignment.getAssignPerc() == null ? BigDecimal.ZERO
							: collateralAssignment.getAssignPerc();
					if (assignPerc.compareTo(BigDecimal.ZERO) <= 0) {
						String[] valueParm = new String[2];
						valueParm[0] = "assignPerc";
						valueParm[1] = "1";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90205", valueParm)));
						return errorDetails;
					}
					if (StringUtils.isNotBlank(collateralAssignment.getCollateralRef())) {
						requiredAssignValidation = true;
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

						BigDecimal totAssignedPerc = collateralSetupService
								.getAssignedPerc(collateralSetup.getCollateralRef(), "");// TODO:Add
																																	// reference
						curAssignValue = curAssignValue.add(collateralSetup.getBankValuation()
								.multiply(collateralAssignment.getAssignPerc() == null ? BigDecimal.ZERO
										: collateralAssignment.getAssignPerc())
								.divide(new BigDecimal(100), 0, RoundingMode.HALF_DOWN));

						BigDecimal totAssignedValue = collateralSetup.getBankValuation().multiply(totAssignedPerc)
								.divide(new BigDecimal(100), 0, RoundingMode.HALF_DOWN);
						BigDecimal availAssignValue = collateralSetup.getBankValuation().subtract(totAssignedValue);
						/*
						 * if (financeDetail.getFinScheduleData().getFinanceMain().
						 * getFinAmount().compareTo(curAssignValue) > 0) { String[] valueParm = new String[2];
						 * valueParm[0] = "Collateral available assign value(" + String.valueOf(curAssignValue) + ")";
						 * valueParm[1] = "current assign value(" + financeDetail.getFinScheduleData().getFinanceMain().
						 * getFinAmount() + ")"; errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("65012",
						 * valueParm))); return errorDetails; }
						 */
						totalAvailAssignValue = totalAvailAssignValue.add(availAssignValue);
						/*
						 * if (availAssignValue.compareTo(financeDetail.
						 * getFinScheduleData().getFinanceMain().getFinAmount()) < 0) { String[] valueParm = new
						 * String[2]; valueParm[0] = "Available assign value(" + String.valueOf(availAssignValue) + ")";
						 * valueParm[1] = "loan amount(" + String.valueOf(financeDetail.getFinScheduleData().
						 * getFinanceMain().getFinAmount()) + ")"; errorDetails.add(ErrorUtil.getErrorDetail(new
						 * ErrorDetail("65012", valueParm))); return errorDetails; }
						 */
					} else {

					}
				}
				if (requiredAssignValidation) {
					// Collateral coverage will be calculated based on the flag
					// "Partially Secured?” defined loan type.
					if (!financeDetail.getFinScheduleData().getFinanceType().isPartiallySecured()) {
						FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
						if (financeMain.getFinAmount().compareTo(curAssignValue) > 0) {
							String[] valueParm = new String[2];
							valueParm[0] = "Collateral available assign value(" + String.valueOf(curAssignValue) + ")";
							valueParm[1] = "current assign value(" + financeMain.getFinAmount() + ")";
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("65012", valueParm)));
							return errorDetails;
						}
						if (PennantConstants.COLLATERAL_LTV_CHECK_FINAMT
								.equals(financeDetail.getFinScheduleData().getFinanceType().getFinLTVCheck())) {
							if (totalAvailAssignValue.compareTo(financeMain.getFinAssetValue()) < 0) {
								String[] valueParm = new String[2];
								valueParm[0] = "Available assign value(" + String.valueOf(totalAvailAssignValue) + ")";
								valueParm[1] = "loan amount(" + String.valueOf(financeMain.getFinAssetValue()) + ")";
								errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("65012", valueParm)));
								return errorDetails;
							}
						} else {
							if (totalAvailAssignValue.compareTo(financeMain.getFinAmount()) < 0) {
								String[] valueParm = new String[2];
								valueParm[0] = "Available assign value(" + String.valueOf(totalAvailAssignValue) + ")";
								valueParm[1] = "loan amount(" + String.valueOf(financeMain.getFinAmount()) + ")";
								errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("65012", valueParm)));
								return errorDetails;
							}
						}
					}
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
				if (detail.getGuranteePercentage().compareTo(new BigDecimal(100)) == 1) {
					String[] valueParm = new String[2];
					valueParm[0] = "GuranteePercentage";
					valueParm[1] = "100";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("30508", valueParm)));
					return errorDetails;
				}
				if (detail.isBankCustomer()) {
					String guarantorCIF = detail.getGuarantorCIF();
					if (StringUtils.equals(guarantorCIF,
							financeDetail.getFinScheduleData().getFinanceMain().getLovDescCustCIF())) {
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
						if (StringUtils.isBlank(guarantor.getEmailID())) {
							CustomerEMail customerEMail = customerEMailService
									.getCustomerEMailById(guarantor.getCustID(), "OFFICE");
							if (customerEMail != null) {
								detail.setEmailId(customerEMail.getCustEMail());
							}
						} else {
							detail.setEmailId(guarantor.getEmailID());
						}
						detail.setGuarantorIDNumber(guarantor.getCustCRCPR());
						detail.setMobileNo(guarantor.getPhoneNumber());
						detail.setCustID(guarantor.getCustID());
						detail.setGuarantorCIFName(guarantor.getCustShrtName());
					}
				} else {
					// validate Phone number
					String mobileNumber = detail.getMobileNo();
					if (StringUtils.isNotBlank(mobileNumber)) {
						if (!(mobileNumber.matches("\\d{10}"))) {
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90278", null)));
							return errorDetails;
						}
					}
					boolean validRegex = EmailValidator.getInstance().isValid(detail.getEmailId());
					if (!validRegex) {
						String[] valueParm = new String[1];
						valueParm[0] = detail.getEmailId();
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90237", valueParm)));
						return errorDetails;
					}
					Province province = provinceDAO.getProvinceById(detail.getAddrCountry(), detail.getAddrProvince(),
							"");
					if (province == null) {
						String[] valueParm = new String[2];
						valueParm[0] = detail.getAddrProvince();
						valueParm[1] = detail.getAddrCountry();
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90701", valueParm)));
						return errorDetails;
					}
					if (StringUtils.isNotBlank(detail.getAddrCity())) {
						City city = cityDAO.getCityById(detail.getAddrCountry(), detail.getAddrProvince(),
								detail.getAddrCity(), "");
						if (city == null) {
							String[] valueParm = new String[2];
							valueParm[0] = detail.getAddrCity();
							valueParm[1] = detail.getAddrProvince();
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90701", valueParm)));
							return errorDetails;
						}
					} else {
						detail.setAddrCity(StringUtils.trimToNull(detail.getAddrCity()));
					}

					if (StringUtils.isNotBlank(detail.getName())) {
						detail.setGuarantorCIFName(detail.getName());
					}
					if (StringUtils.isBlank(detail.getGuarantorGenderCode())) {
						String[] valueParm = new String[1];
						valueParm[0] = "gender";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
						return errorDetails;
					} else {
						if (!genderDAO.isValidGenderCode(detail.getGuarantorGenderCode())) {
							String[] valueParm = new String[1];
							valueParm[0] = detail.getGuarantorGenderCode();
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90701", valueParm)));
							return errorDetails;
						}
					}
					if (detail.getGuarantorProof().length > 0) {
						if (StringUtils.isBlank(detail.getGuarantorProofName())) {
							String[] valueParm = new String[1];
							valueParm[0] = "idDocName";
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm), "EN"));
							return errorDetails;
						}
						String docName = Objects.toString(detail.getGuarantorProofName(), "").toLowerCase();
						if (!docName.contains(".")) {
							String[] valueParm = new String[1];
							valueParm[0] = "docName: " + docName;
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90291", "", valueParm), "EN"));
							return errorDetails;
						} else {
							detail.setGuarantorProofName(detail.getGuarantorProofName().toLowerCase());
						}
					}

					if (StringUtils.isNotBlank(detail.getName())) {
						detail.setGuarantorCIFName(detail.getName());
					}
					if (StringUtils.isBlank(detail.getGuarantorGenderCode())) {
						String[] valueParm = new String[1];
						valueParm[0] = "gender";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
						return errorDetails;
					} else {
						if (!genderDAO.isValidGenderCode(detail.getGuarantorGenderCode())) {
							String[] valueParm = new String[1];
							valueParm[0] = detail.getGuarantorGenderCode();
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90701", valueParm)));
							return errorDetails;
						}
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
				} else if (StringUtils.isNotBlank(jointAccDetail.getRepayAccountId())) {
					String[] valueParm = new String[2];
					valueParm[0] = "RepayAccountId";
					valueParm[1] = "includeRepay";
					// {0} is only applicable for {1} customer.
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
				// for authoritySignatory and sequence
				if (jointAccDetail.isAuthoritySignatory()) {

					if (jointAccDetail.getSequence() <= 0 || jointAccDetail.getSequence() >= 10) {
						// {0} should between or including {1} and {2}.
						String[] valueParm = new String[3];
						valueParm[0] = "sequence";
						valueParm[1] = "1";
						valueParm[2] = "9";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90282", valueParm)));

					}
				} else if (jointAccDetail.getSequence() != 0) {
					// {0} is only applicable for {1}.
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
				// Duplicate {0} are not allowed.
				if (duplicateSeqCount >= 2) {
					String[] valueParm = new String[1];
					valueParm[0] = "sequence id";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90273", valueParm)));
				}
				// Duplicate {0} are not allowed.
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
				// validate Dates
				if (detail.getCustDocIssuedOn() != null && detail.getCustDocExpDate() != null) {
					if (detail.getCustDocIssuedOn().compareTo(detail.getCustDocExpDate()) > 0) {
						String[] valueParm = new String[2];
						valueParm[0] = "custDocExpDate: "
								+ DateUtility.format(detail.getCustDocExpDate(), PennantConstants.XMLDateFormat);
						valueParm[1] = "custDocIssuedOn: "
								+ DateUtility.format(detail.getCustDocIssuedOn(), PennantConstants.XMLDateFormat);
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

				/*
				 * //validate PAN Customer customer = financeDetail.getCustomerDetails().getCustomer(); if(customer !=
				 * null) { if(StringUtils.equals("03", detail.getDocCategory())){
				 * if(!StringUtils.equals(detail.getCustDocTitle(), customer.getCustCRCPR())){ String[] valueParm = new
				 * String[1]; valueParm[0] = customer.getCustCRCPR(); errorDetails.add(ErrorUtil.getErrorDetail(new
				 * ErrorDetails("90296", valueParm))); return errorDetails; } } }
				 */

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
				if (!(DocumentCategories.CUSTOMER.getKey().equals(docType.getCategoryCode()))
						&& docType.isDocIsMandatory()) {
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
					} else if (!StringUtils.equalsIgnoreCase(detail.getDoctype(), "jpg")
							&& !StringUtils.equalsIgnoreCase(detail.getDoctype(), "png")
							&& !StringUtils.equalsIgnoreCase(detail.getDoctype(), "pdf")) {
						String[] valueParm = new String[1];
						valueParm[0] = "docFormat, Available formats are jpg,png,PDF";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90122", valueParm)));
					}

					// TODO: Need to add password protected field in
					// documentdetails
				}

				if (StringUtils.equals(detail.getDocCategory(), "03")) {
					Pattern pattern = Pattern.compile("^[A-Za-z]{5}\\d{4}[A-Za-z]{1}");
					if (detail.getCustDocTitle() != null) {
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

	private List<ErrorDetail> mandateValidation(FinanceDetail financeDetail, String vldGroup) {
		List<ErrorDetail> errorDetails = new ArrayList<ErrorDetail>();
		Mandate mandate = financeDetail.getMandate();
		// if it is stp process mandate is mandatory
		/*
		 * if (financeDetail.isStp() && mandate == null) { String[] valueParm = new String[1]; valueParm[0] = "Mandate";
		 * errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm))); return errorDetails; }
		 */
		// Validate mandate details
		if (mandate != null) {
			if (StringUtils.equals(financeDetail.getFinScheduleData().getFinanceMain().getFinRepayMethod(),
					FinanceConstants.REPAYMTH_MANUAL)) {
				String[] valueParm = new String[2];
				valueParm[0] = "Mandate";
				valueParm[1] = "finRepayMethod is " + FinanceConstants.REPAYMTH_MANUAL;
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
						boolean updateFlag = false;
						if (StringUtils.equals(vldGroup, PennantConstants.VLD_UPD_LOAN)) {
							updateFlag = true;
						}
						if (!updateFlag && !StringUtils.equalsIgnoreCase(curMandate.getCustCIF(),
								financeDetail.getFinScheduleData().getFinanceMain().getLovDescCustCIF())) {
							String[] valueParm = new String[2];
							valueParm[0] = financeDetail.getFinScheduleData().getFinanceMain().getLovDescCustCIF();
							valueParm[1] = curMandate.getCustCIF();
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90310", valueParm)));
							return errorDetails;
						} else {
							if (curMandate.getCustID() != financeDetail.getFinScheduleData().getFinanceMain()
									.getCustID()) {
								String[] valueParm = new String[2];
								valueParm[0] = financeDetail.getFinScheduleData().getFinanceMain().getLovDescCustCIF();
								valueParm[1] = curMandate.getCustCIF();
								errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90310", valueParm)));
								return errorDetails;
							}
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
				if (SysParamUtil.isAllowed(SMTParameterConstants.MANDATE_ALW_PARTNER_BANK)) {
					if (mandate.getPartnerBankId() <= 0) {
						String[] valueParm1 = new String[1];
						valueParm1[0] = "partnerBankId";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm1)));
					} else {
						PartnerBank partnerBank = partnerBankDAO.getPartnerBankById(mandate.getPartnerBankId(), "");
						if (partnerBank == null) {
							String[] valueParm1 = new String[1];
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90263", valueParm1)));
						}
					}
				}

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
				if (ImplementationConstants.ALLOW_BARCODE) {
					if (StringUtils.isBlank(mandate.getBarCodeNumber())) {
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

				// barcode
				if (StringUtils.isNotBlank(mandate.getBarCodeNumber())) {
					Pattern pattern = Pattern.compile(
							PennantRegularExpressions.getRegexMapper(PennantRegularExpressions.REGEX_BARCODE_NUMBER));
					Matcher matcher = pattern.matcher(mandate.getBarCodeNumber());

					if (matcher.matches() == false) {
						String[] valueParm = new String[1];
						valueParm[0] = mandate.getBarCodeNumber();
						errorDetails.add(ErrorUtil
								.getErrorDetail(new ErrorDetail("barCodeNumber", "90404", valueParm, valueParm)));
						return errorDetails;
					}
				}
				if (mandate.getMaxLimit() == null) {
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

				if (mandate.getExpiryDate() != null) {
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
				// validate AccNumber length
				if (StringUtils.isNotBlank(mandate.getBankCode()) && StringUtils.isNotBlank(mandate.getAccNumber())) {
					BankDetail bankDetail = bankDetailService.getAccNoLengthByCode(mandate.getBankCode());
					if (bankDetail != null) {
						int maxAccNoLength = bankDetail.getAccNoLength();
						int minAccNoLength = bankDetail.getMinAccNoLength();
						if (mandate.getAccNumber().length() < minAccNoLength
								|| mandate.getAccNumber().length() > maxAccNoLength) {
							String[] valueParm = new String[3];
							valueParm[0] = "AccountNumber(Mandate)";
							valueParm[1] = String.valueOf(minAccNoLength) + " characters";
							valueParm[2] = String.valueOf(maxAccNoLength) + " characters";
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("BNK001", valueParm)));
							return errorDetails;
						}
					}
				}
				// validate Phone number
				String mobileNumber = mandate.getPhoneNumber();
				if (StringUtils.isNotBlank(mobileNumber)) {
					if (!(mobileNumber.matches("\\d{10}"))) {
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90278", null)));
					}
				}

				String acc_holder_regix = "^$|^[A-Za-z]+[A-Za-z.\\s]*";
				// validate names
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

				// validate periodicity
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

				// validate status
				if (StringUtils.isNotBlank(mandate.getStatus())) {
					List<ValueLabel> status = PennantStaticListUtil
							.getStatusTypeList(SysParamUtil.getValueAsString(MandateConstants.MANDATE_CUSTOM_STATUS));
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
				if (mandate.getDocImage() == null && StringUtils.isBlank(mandate.getExternalRef())) {
					String[] valueParm = new String[2];
					valueParm[0] = "docContent";
					valueParm[1] = "docRefId";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90123", valueParm)));
				} else if (StringUtils.isBlank(mandate.getDocumentName())) {
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
					// document Name Extension validation
					if (!docName.endsWith(".jpg") && !docName.endsWith(".jpeg") && !docName.endsWith(".png")
							&& !docName.endsWith(".pdf")) {
						String[] valueParm = new String[1];
						valueParm[0] = "Document Extension available ext are:JPG,JPEG,PNG,PDF ";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90122", valueParm)));
					}
				}
				if (SysParamUtil.isAllowed(SMTParameterConstants.MANDATE_ALW_PARTNER_BANK)) {
					if (mandate.getPartnerBankId() <= 0) {
						String[] valueParm1 = new String[1];
						valueParm1[0] = "partnerBankId";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm1)));
					} else {
						PartnerBank partnerBank = partnerBankDAO.getPartnerBankById(mandate.getPartnerBankId(), "");
						if (partnerBank == null) {
							String[] valueParm1 = new String[1];
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90263", valueParm1)));
						}
					}
				}
			}
			if (StringUtils.equals(mandate.getMandateType(), MandateConstants.TYPE_EMANDATE)) {
				if (StringUtils.isBlank(mandate.geteMandateReferenceNo())) {
					String[] valueParm1 = new String[1];
					valueParm1[0] = "eMandateReferenceNo";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm1)));
				}
				if (StringUtils.isBlank(mandate.geteMandateSource())) {
					String[] valueParm1 = new String[1];
					valueParm1[0] = "eMandateSource";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm1)));
				} else {
					int count = mandateService.validateEmandateSource(mandate.geteMandateSource());
					if (count == 0) {
						String[] valueParm1 = new String[1];
						valueParm1[0] = "eMandateSource " + mandate.geteMandateSource();
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90501", valueParm1)));
					}
				}
			}
		} else {
			if (!StringUtils.equals(financeDetail.getFinScheduleData().getFinanceMain().getFinRepayMethod(),
					FinanceConstants.REPAYMTH_MANUAL)
					&& !StringUtils.equals(financeDetail.getFinScheduleData().getFinanceMain().getFinRepayMethod(),
							FinanceConstants.REPAYMTH_PDC)
					&& financeDetail.isStp()) {
				String[] valueParm = new String[1];
				valueParm[0] = "mandate";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
			}
		}
		return errorDetails;
	}

	private boolean validateBranchCode(Mandate mandate, boolean isValidBranch, BankBranch bankBranch) {
		if (StringUtils.equals(MandateConstants.TYPE_ECS, mandate.getMandateType())) {
			if (!bankBranch.isEcs()) {
				isValidBranch = false;
			}
		} else if (StringUtils.equals(MandateConstants.TYPE_DDM, mandate.getMandateType())) {
			if (!bankBranch.isDda()) {
				isValidBranch = false;
			}
		} else if (StringUtils.equals(MandateConstants.TYPE_NACH, mandate.getMandateType())) {
			if (!bankBranch.isNach()) {
				isValidBranch = false;
			}
		}
		return isValidBranch;
	}

	public List<ErrorDetail> disbursementValidation(FinanceDetail financeDetail) {
		List<ErrorDetail> errorDetails = new ArrayList<ErrorDetail>();
		List<FinAdvancePayments> finAdvPayments = financeDetail.getAdvancePaymentsList();

		// if it is stp process disbursement is mandatory
		if (financeDetail.isStp() && CollectionUtils.isEmpty(finAdvPayments)
				&& !StringUtils.equals(financeDetail.getFinScheduleData().getFinanceMain().getProductCategory(),
						FinanceConstants.PRODUCT_CD)) {
			String[] valueParm = new String[1];
			valueParm[0] = "disbursement";
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
			return errorDetails;
		}

		if (financeDetail.isDisbStp() && CollectionUtils.isEmpty(finAdvPayments)) {
			String[] valueParm = new String[1];
			valueParm[0] = "disbursement";
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
			return errorDetails;
		}
		// validate disbursement details
		if (finAdvPayments != null) {
			for (FinAdvancePayments advPayment : finAdvPayments) {
				// partnerbankid
				if (advPayment.getPartnerBankID() <= 0 && StringUtils.isBlank(advPayment.getPartnerbankCode())) {
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
					List<ValueLabel> paymentTypes = PennantStaticListUtil.getPaymentTypesWithIST();
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
				if (advPayment.getPartnerBankID() <= 0 && StringUtils.isNotBlank(advPayment.getPartnerbankCode())) {
					PartnerBank partnerBank = partnerBankDAO.getPartnerBankByCode(advPayment.getPartnerbankCode(), "");
					if (partnerBank == null) {
						String[] valueParm = new String[1];
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90263", valueParm)));
						return errorDetails;
					} else {
						advPayment.setPartnerBankID(partnerBank.getPartnerBankId());
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
				if (partnerBank != null) {
					advPayment.setPartnerBankAc(partnerBank.getAccountNo());
					advPayment.setPartnerBankAcType(partnerBank.getAcType());
				}
				FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
				if (financeMain != null) {
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
						|| StringUtils.equals(advPayment.getPaymentType(), DisbursementConstants.PAYMENT_TYPE_RTGS)
						|| StringUtils.equals(advPayment.getPaymentType(), DisbursementConstants.PAYMENT_TYPE_IFT)) {

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
					} /*
						 * else { //validate AccNumber length int accNoLength =
						 * bankDetailService.getAccNoLengthByCode(advPayment. getBankCode()); if
						 * (advPayment.getBeneficiaryAccNo().length() != accNoLength) { String[] valueParm = new
						 * String[2]; valueParm[0] = "AccountNumber(Disbursement)"; valueParm[1] =
						 * String.valueOf(accNoLength) + " characters"; errorDetails.add(ErrorUtil.getErrorDetail(new
						 * ErrorDetails("30570", valueParm))); return errorDetails; } }
						 */
					// Account holder name
					if (StringUtils.isBlank(advPayment.getBeneficiaryName())) {
						String[] valueParm = new String[2];
						valueParm[0] = "acHolderName";
						valueParm[1] = advPayment.getBeneficiaryName();
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90217", valueParm)));
					} else {
						Pattern pattern = Pattern.compile(PennantRegularExpressions
								.getRegexMapper(PennantRegularExpressions.REGEX_ACCOUNT_HOLDER_NAME));

						Matcher matcher = pattern.matcher(advPayment.getBeneficiaryName());

						if (!matcher.matches()) {
							String[] valueParm = new String[1];
							valueParm[0] = "AccHolderName";
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90237", "", valueParm), "EN"));
						}
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

					//Validating Vas Disb Instructions
					if (PennantConstants.FINSOURCE_ID_API.equals(financeMain.getFinSourceID())) {
						if (DisbursementConstants.PAYMENT_DETAIL_VAS.equals(advPayment.getPaymentDetail())) {
							//Product code is mandatory when disbParty is VAS
							if (StringUtils.isBlank(advPayment.getVasProductCode())
									&& advPayment.getVasProductCode() == null) {
								String[] valueParm = new String[2];
								valueParm[0] = "Disb Party: " + advPayment.getPaymentDetail();
								valueParm[1] = "VAS Product Code";
								errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("91132", valueParm)));
								return errorDetails;
							}
							FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
							List<VASRecording> vasRecordingList = finScheduleData.getVasRecordingList();
							int productCount = 0;
							if (advPayment.getVasProductCode() != null) {
								for (VASRecording vasRecording : vasRecordingList) {
									if (advPayment.getVasProductCode().equals(vasRecording.getProductCode())) {
										if (vasRecording.getFee().compareTo(advPayment.getAmtToBeReleased()) != 0) {
											//Validating VAS Disbursement Amount and configured VAS Amount equal r not
											String[] valueParm = new String[2];
											valueParm[0] = " VAS Disbursement Amount";
											valueParm[1] = " configured VAS Amount";
											errorDetails
													.add(ErrorUtil.getErrorDetail(new ErrorDetail("90277", valueParm)));
										}
										productCount++;
										break;
									}
								}

							}
							// Validating the product code is valid or not
							if (productCount < 1) {
								String[] valueParm = new String[2];
								valueParm[0] = "Product";
								valueParm[1] = advPayment.getVasProductCode();
								errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90224", valueParm)));
							}
							//Validating duplicate product codes in Disbursement Instruction
							int duplicateCount = 0;
							if (CollectionUtils.isNotEmpty(finAdvPayments)) {
								for (FinAdvancePayments finAdvancePayments : finAdvPayments) {
									if (StringUtils.equals(finAdvancePayments.getVasProductCode(),
											advPayment.getVasProductCode())) {
										duplicateCount++;
										if (duplicateCount > 1) {
											String[] valueParm = new String[2];
											valueParm[0] = "Product Code: " + finAdvancePayments.getVasProductCode();
											valueParm[1] = "Disbursement Instruction";
											errorDetails
													.add(ErrorUtil.getErrorDetail(new ErrorDetail("41018", valueParm)));
										}
									}
								}
							}
						}
					}
				} else if (StringUtils.equals(advPayment.getPaymentType(), DisbursementConstants.PAYMENT_TYPE_IST)) {
					advPayment.setBankCode("");
					advPayment.setLiabilityHoldName("");
					advPayment.setPayableLoc("");
					advPayment.setPrintingLoc("");
					advPayment.setValueDate(null);
					advPayment.setLLReferenceNo("");
					advPayment.setBankName("");
					advPayment.setBranchCode("");
					advPayment.setBeneficiaryAccNo("");
					advPayment.setBeneficiaryName("");
					advPayment.setiFSC(null);
					advPayment.setPhoneNumber("");
				}
				// validate finance documents
				if (advPayment.getDocImage() != null && advPayment.getDocImage().length >= 0) {
					if (StringUtils.isBlank(advPayment.getDocumentName())) {
						String[] valueParm = new String[1];
						valueParm[0] = "docName";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
					}
					if (StringUtils.isBlank(advPayment.getDocType())) {
						String[] valueParm = new String[1];
						valueParm[0] = "docFormat";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
					}
					String docName = advPayment.getDocumentName().toLowerCase();
					// document name has no extension
					if (!docName.contains(".")) {
						String[] valueParm = new String[1];
						valueParm[0] = advPayment.getDocumentName();
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90291", valueParm)));
					}
					// document name has only extension
					else if (StringUtils.isEmpty(docName.substring(0, docName.lastIndexOf(".")))) {
						String[] valueParm = new String[2];
						valueParm[0] = "Document Name";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
					}
					// document Name Extension validation
					if (!docName.endsWith(PennantConstants.DOC_TYPE_PDF_EXT)
							&& !docName.endsWith(PennantConstants.DOC_TYPE_JPG_EXT)
							&& !docName.endsWith(PennantConstants.DOC_TYPE_PNG_EXT)) {
						String[] valueParm = new String[1];
						valueParm[0] = "Document Extension available ext are: .pdf, .jpg, .png";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90122", valueParm)));
					}
					if (!PennantConstants.DOC_TYPE_JPG.equalsIgnoreCase(advPayment.getDocType())
							&& !PennantConstants.DOC_TYPE_PDF.equalsIgnoreCase(advPayment.getDocType())
							&& !PennantConstants.DOC_TYPE_IMAGE.equalsIgnoreCase(advPayment.getDocType())
							&& !PennantConstants.DOC_TYPE_PNG.equalsIgnoreCase(advPayment.getDocType())) {
						String[] valueParm = new String[1];
						valueParm[0] = "docFormat, Available formats are: IMG, JPG, PDF, PNG,";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90122", valueParm)));
					}
				}
			}
		}
		return errorDetails;
	}

	private List<ErrorDetail> nonFinanceValidation(String vldGroup, FinScheduleData finScheduleData, boolean isAPICall,
			FinanceDetail finDetail) {
		// Re-Initialize Error Details
		List<ErrorDetail> errorDetails = new ArrayList<ErrorDetail>();
		FinanceMain finMain = finScheduleData.getFinanceMain();
		boolean isCreateLoan = false;

		if (StringUtils.equals(vldGroup, PennantConstants.VLD_CRT_LOAN)) {
			isCreateLoan = true;
		}

		// Validate Finance Currency if not default currency
		if (!StringUtils.equals(finMain.getFinCcy(), SysParamUtil.getAppCurrency())) {
			boolean currencyExists = currencyDAO.isExistsCurrencyCode(finMain.getFinCcy());
			if (!currencyExists) {
				String[] valueParm = new String[1];
				valueParm[0] = finMain.getFinCcy();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90120", valueParm)));
				return errorDetails;
			}
		}

		// Validate finance branch (Swift Branch was set after validation)
		// Theoretically, it should never go inside in the if condition
		if (StringUtils.isBlank(finMain.getSwiftBranchCode())) {
			if (isCreateLoan || StringUtils.isNotBlank(finMain.getFinBranch())) {
				Branch branch = branchDAO.getBranchById(finMain.getFinBranch(), "");
				if (branch == null) {
					String[] valueParm = new String[1];
					valueParm[0] = finMain.getFinBranch();
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90129", valueParm)));
				}
			}
		}

		// Validate Repayment Method
		// TODO: To be confirmed from where it should be taken?
		// PennantStaticListUtil.getRepayMethods() OR MandateConstants or
		// FinanceConstants??
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

		if (isCreateLoan) {
			String finPurpose = finMain.getFinPurpose();
			if (StringUtils.isNotBlank(finPurpose)) {
				LoanPurpose loanPurpose = loanPurposeDAO.getLoanPurposeById(finPurpose, "");
				if (loanPurpose == null || !loanPurpose.isLoanPurposeIsActive()) {
					String[] valueParm = new String[1];
					valueParm[0] = finPurpose;
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90701", valueParm)));
				}
			}
		}
		return errorDetails;
	}

	/*
	 * ========================================================================= =======================================
	 * VALIDATE BASIC FINANCE DATA =========================================================================
	 * =======================================
	 */

	private List<ErrorDetail> basicValidation(String vldGroup, FinScheduleData finScheduleData, boolean isAPICall,
			FinanceDetail finDetail, boolean isEMI) {
		List<ErrorDetail> errorDetails = finScheduleData.getErrorDetails();
		FinanceMain finMain = finScheduleData.getFinanceMain();
		FinanceType financeType = finScheduleData.getFinanceType();
		BigDecimal zeroAmount = BigDecimal.ZERO;

		// Application number
		if (!ImplementationConstants.CLIENT_NFL) {
			if (StringUtils.isNotBlank(finMain.getApplicationNo())
					&& finMain.getApplicationNo().length() > LengthConstants.LEN_REF) {
				String[] valueParm = new String[2];
				valueParm[0] = "Application Number";
				valueParm[1] = LengthConstants.LEN_REF + " characters";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("30568", valueParm)));
			}
		}

		// Finance start date
		Date appDate = SysParamUtil.getAppDate();
		if (!isEMI) {
			Date minReqFinStartDate = DateUtility.addDays(appDate, -SysParamUtil.getValueAsInt("BACKDAYS_STARTDATE"));
			if (finMain.getFinStartDate().compareTo(minReqFinStartDate) <= 0) {
				String[] valueParm = new String[2];
				valueParm[0] = SysParamUtil.getValueAsString("BACKDAYS_STARTDATE");
				valueParm[1] = DateUtility.format(DateUtility.addDays(minReqFinStartDate, 1),
						PennantConstants.XMLDateFormat);
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90134", valueParm)));
			}
		}
		Date maxReqFinStartDate = DateUtility.addDays(appDate, +SysParamUtil.getValueAsInt("FUTUREDAYS_STARTDATE") + 1);
		if (finMain.getFinStartDate().compareTo(maxReqFinStartDate) > 0) {
			String[] valueParm = new String[2];
			valueParm[0] = "Loan Start Date";
			valueParm[1] = DateUtility.format(DateUtility.addDays(maxReqFinStartDate, 1),
					PennantConstants.XMLDateFormat);
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("65027", valueParm)));
		}

		// Loan Amount Validation
		if (!finScheduleData.getFinanceMain().getProductCategory().equals(FinanceConstants.PRODUCT_ODFACILITY)) {
			if (finMain.getFinAmount().compareTo(zeroAmount) <= 0) {
				String[] valueParm = new String[1];
				valueParm[0] = String.valueOf(finMain.getFinAmount().doubleValue());
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90127", valueParm)));
			}
		}

		//
		if (financeType.isAlwMaxDisbCheckReq() && finMain.getFinAssetValue().compareTo(zeroAmount) <= 0) {
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
		if (financeType.isFinIsDwPayRequired()) {
			if (finDetail != null) {
				setDownpaymentRulePercentage(financeType, finMain, finDetail);
				BigDecimal reqDwnPay = getPercentageValue(finMain.getFinAmount(), finMain.getMinDownPayPerc());
				BigDecimal downPayment = finMain.getDownPayBank().add(finMain.getDownPaySupl());

				if (downPayment.compareTo(finMain.getFinAmount()) >= 0) {
					String[] valueParm = new String[3];
					valueParm[0] = "Sum of Bank & Supplier Down payments";
					valueParm[1] = String.valueOf(reqDwnPay);
					valueParm[2] = String.valueOf(finMain.getFinAmount());
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("30567", valueParm)));
				}

				if (downPayment.compareTo(reqDwnPay) < 0) {
					String[] valueParm = new String[2];
					valueParm[0] = "Sum of Bank & Supplier Down payments";
					valueParm[1] = String.valueOf(reqDwnPay);
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("30569", valueParm)));
				}
			}
		} else if (finMain.getDownPayBank().compareTo(zeroAmount) != 0
				|| finMain.getDownPaySupl().compareTo(zeroAmount) != 0) {
			String[] valueParm = new String[3];
			valueParm[0] = "Down pay bank";
			valueParm[1] = "Supplier";
			valueParm[2] = finMain.getFinType();
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90203", valueParm)));
		}
		String tdsType = finMain.getTdsType();
		if (finMain.isTDSApplicable()) {
			//Loan Queue TDS is Applicable but in Loan Type it is marked as False
			if (!financeType.isTdsApplicable()) {
				String[] valueParm = new String[3];
				valueParm[0] = "tds";
				valueParm[1] = financeType.getFinType();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90329", valueParm)));
			}
			//Validating TDS Type
			String loanTypeTdsType = financeType.getTdsType();
			if (StringUtils.isNotBlank(tdsType)) {
				//Loan type TDS Type as USER or AUTO or MANUAL
				if (!"#".equals(loanTypeTdsType)) {
					//If TDS Type is not != User selection 
					if (!PennantConstants.TDS_USER_SELECTION.equals(loanTypeTdsType)) {
						//Loan Type value and Loan Queue Value should be same
						if (!StringUtils.equals(tdsType, loanTypeTdsType)) {
							String[] valueParm = new String[2];
							valueParm[0] = "tdsType";
							valueParm[1] = loanTypeTdsType;
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90337", valueParm)));
						}
						//From Loan Queue passing Different Values
					} else if (!(StringUtils.equalsIgnoreCase(tdsType, PennantConstants.TDS_AUTO)
							|| StringUtils.equalsIgnoreCase(tdsType, PennantConstants.TDS_MANUAL))) {
						String[] valueParm = new String[2];
						valueParm[0] = "tdsType";
						valueParm[1] = PennantConstants.TDS_AUTO + " , " + PennantConstants.TDS_MANUAL;
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90337", valueParm)));
					}
				}
				//With out passing TDS Type in Loan Queue but in Loan Type TDS type is USER or AUTO or MANUAL	
			} else if (StringUtils.isBlank(tdsType) && !"#".equals(loanTypeTdsType)) {
				String[] valueParm = new String[1];
				valueParm[0] = "tdsType";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("30561", valueParm)));
			}
			//TDS is not applicable in loan queue but passing TDS Type value from API	
		} else if (StringUtils.isNotBlank(tdsType) && !"#".equals(tdsType)) {
			String[] valueParm = new String[2];
			valueParm[0] = "tdsType";
			valueParm[1] = "tdsApplicable is true";
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90298", valueParm)));
		}
		if (finMain.isQuickDisb()) {
			if (!financeType.isQuickDisb()) {
				String[] valueParm = new String[3];
				valueParm[0] = "quickDisb";
				valueParm[1] = financeType.getFinType();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90329", valueParm)));
			}
		}
		// RETURN IF ANY ERROR AFTER VERY BASIC VALIDATION
		if (!errorDetails.isEmpty()) {
			return errorDetails;
		}

		// Allow Manual Schedule
		if (finMain.isManualSchedule()) {
			errorDetails = manualScheduleValidation(vldGroup, finScheduleData);
			if (!errorDetails.isEmpty()) {
				return errorDetails;
			}
		}

		// Planned Deferments
		if (finMain.getPlanDeferCount() > 0) {
			errorDetails = planDefermentValidation(vldGroup, finScheduleData);
			if (!errorDetails.isEmpty()) {
				return errorDetails;
			}
		}
		// planned EMI
		errorDetails = planEMIHolidayValidation(vldGroup, finScheduleData);
		if (!errorDetails.isEmpty()) {
			return errorDetails;
		}

		// Step Loan?
		errorDetails = stepLoanValidation(vldGroup, finScheduleData);
		if (!errorDetails.isEmpty()) {
			return errorDetails;
		}

		return errorDetails;
	}

	/*
	 * ========================================================================= =======================================
	 * VALIDATE FINANCE GRACE DETAILS =========================================================================
	 * =======================================
	 */

	private BigDecimal getPercentageValue(BigDecimal finAmount, BigDecimal minDownPayPerc) {
		BigDecimal returnAmount = BigDecimal.ZERO;

		if (finAmount != null) {
			returnAmount = (finAmount.multiply(unFormateAmount(minDownPayPerc, 2).divide(new BigDecimal(100))))
					.divide(new BigDecimal(100), RoundingMode.HALF_DOWN);
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

	private void setDownpaymentRulePercentage(FinanceType finType, FinanceMain finMain, FinanceDetail finDetail) {
		if (finType.getDownPayRule() != 0 && finType.getDownPayRule() != Long.MIN_VALUE
				&& StringUtils.isNotEmpty(finType.getDownPayRuleDesc())) {

			CustomerEligibilityCheck customerEligibilityCheck = prepareCustElgDetail(false, finDetail)
					.getCustomerEligibilityCheck();
			String sqlRule = ruleService.getAmountRule(finType.getDownPayRuleDesc(), RuleConstants.MODULE_DOWNPAYRULE,
					RuleConstants.EVENT_DOWNPAYRULE);
			BigDecimal downpayPercentage = BigDecimal.ZERO;
			if (StringUtils.isNotEmpty(sqlRule)) {
				HashMap<String, Object> fieldsAndValues = customerEligibilityCheck.getDeclaredFieldValues();
				downpayPercentage = (BigDecimal) RuleExecutionUtil.executeRule(sqlRule, fieldsAndValues,
						finMain.getFinCcy(), RuleReturnType.DECIMAL);
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

		// Allow Grace?
		if (!financeType.isFInIsAlwGrace()) {
			String[] valueParm = new String[1];
			valueParm[0] = finMain.getFinType();
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90135", valueParm)));
			return errorDetails;
		}

		// Grace Terms & Grace End Date are Mutually Exclusive
		if (finMain.getGraceTerms() > 0 && finMain.getGrcPeriodEndDate() != null) {
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90152", null)));
			return errorDetails;
		}

		// Both Grace Terms & Grace End Date are not present
		if (finMain.getGraceTerms() == 0 && finMain.getGrcPeriodEndDate() == null) {
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90184", null)));
			return errorDetails;
		}

		// Validate Profit Details
		errorDetails = gracePftFrqValidation(finScheduleData);
		if (!errorDetails.isEmpty()) {
			return errorDetails;
		}

		// Grace Rate Validations
		errorDetails = graceRateValidation(finScheduleData);

		// Validate Review Details
		errorDetails = gracePftReviewValidation(finScheduleData);

		// Validate Capitalization Details
		errorDetails = gracePftCpzValidation(finScheduleData);

		// Validate Grace Payment and Methods
		errorDetails = graceSchdValidation(finScheduleData);

		// Validate Grace Advised Rates
		errorDetails = graceAdvRateValidation(finScheduleData);

		// Validate Grace Dates
		errorDetails = graceDatesValidation(finScheduleData);

		return errorDetails;
	}

	/*
	 * ========================================================================= =======================================
	 * VALIDATE FINANCE REPAY DETAILS =========================================================================
	 * =======================================
	 */

	private List<ErrorDetail> repayValidation(String vldGroup, FinScheduleData finScheduleData, boolean isAPICall) {
		List<ErrorDetail> errorDetails = finScheduleData.getErrorDetails();
		FinanceMain finMain = finScheduleData.getFinanceMain();
		FinanceType financeType = finScheduleData.getFinanceType();

		// Number of Terms & Maturity Date are Mutually Exclusive
		// This is not applicable for OverDraft Web services
		if (!finScheduleData.getFinanceMain().getProductCategory().equals(FinanceConstants.PRODUCT_ODFACILITY)) {
			if (finMain.getNumberOfTerms() > 0 && finMain.getMaturityDate() != null) {
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90190", null)));
				return errorDetails;
			}
		}

		// Both Grace Terms & Grace End Date are not present
		if (finMain.getNumberOfTerms() == 0 && finMain.getMaturityDate() == null) {
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90191", null)));
			return errorDetails;
		}

		// validate min and max terms with loanType config.
		if (financeType.getFinMinTerm() > 0 && financeType.getFinMaxTerm() > 0) {
			if (finMain.getNumberOfTerms() < financeType.getFinMinTerm()
					|| finMain.getNumberOfTerms() > financeType.getFinMaxTerm()) {
				String[] valueParm = new String[3];
				valueParm[0] = "Repay";
				valueParm[1] = String.valueOf(financeType.getFinMinTerm());
				valueParm[2] = String.valueOf(financeType.getFinMaxTerm());
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90272", valueParm)));
				return errorDetails;
			}
		}

		// Repay Rate Validations
		errorDetails = repayRateValidation(finScheduleData);

		// Repayment Schedule Method (If not blanks validation already happens
		// in defaulting)
		if (!StringUtils.equals(finMain.getScheduleMethod(), CalculationConstants.SCHMTHD_NOPAY)
				&& !StringUtils.equals(finMain.getScheduleMethod(), CalculationConstants.SCHMTHD_EQUAL)
				&& !StringUtils.equals(finMain.getScheduleMethod(), CalculationConstants.SCHMTHD_PFT)
				&& !StringUtils.equals(finMain.getScheduleMethod(), CalculationConstants.SCHMTHD_PFTCPZ)
				&& !StringUtils.equals(finMain.getScheduleMethod(), CalculationConstants.SCHMTHD_PRI)
				&& !StringUtils.equals(finMain.getScheduleMethod(), CalculationConstants.SCHMTHD_PRI_PFT)
				&& !StringUtils.equals(finMain.getScheduleMethod(), CalculationConstants.SCHMTHD_POS_INT)) {
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90189", null)));
		}

		// Validate Repayment Details
		errorDetails = repayFrqValidation(finScheduleData);
		if (!errorDetails.isEmpty()) {
			return errorDetails;
		}

		// Validate Profit Details
		errorDetails = repayPftFrqValidation(finScheduleData);

		// Validate Review Details
		errorDetails = repayPftReviewValidation(finScheduleData);

		// Validate Capitalization Details
		errorDetails = repayPftCpzValidation(finScheduleData);

		// Pay on interest frequency
		if (finMain.isFinRepayPftOnFrq() && !financeType.isFinRepayPftOnFrq()) {
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90227", null)));
		}

		// Validate Repay Dates
		errorDetails = repayDatesValidation(finScheduleData);

		// validation for od Loan
		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, financeType.getProductCategory())) {
			errorDetails = odDatesValidation(finScheduleData);
		}

		// Validate BPI
		if (financeType.isAlwBPI()) {
			errorDetails = bpiValidation(finScheduleData);
		} else if (finMain.isAlwBPI()) {
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90228", null)));
			return errorDetails;
		}

		// Advance EMI Validation
		if (StringUtils.isNotBlank(finMain.getAdvType())) {
			if (financeType.getAdvMinTerms() > 0 && financeType.getAdvMaxTerms() > 0) {
				if (finMain.getAdvTerms() < financeType.getAdvMinTerms()
						|| finMain.getAdvTerms() > financeType.getAdvMaxTerms()) {
					String[] valueParm = new String[3];
					valueParm[0] = "Advance Intrest/EMI";
					valueParm[1] = String.valueOf(financeType.getAdvMinTerms());
					valueParm[2] = String.valueOf(financeType.getAdvMaxTerms());
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90272", valueParm)));
					return errorDetails;
				}
			}
			if (finMain.getAdvTerms() >= finMain.getNumberOfTerms()) {
				String[] valueParm = new String[3];
				valueParm[0] = "Advance Intrest/EMI" + String.valueOf(finMain.getAdvTerms());
				valueParm[1] = " Number of terms : " + String.valueOf(finMain.getNumberOfTerms());
				;
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("30565", valueParm)));
				return errorDetails;
			}
		} else if (finMain.getAdvTerms() > 0) {
			String[] valueParm = new String[3];
			valueParm[0] = "Advance Intrest/EMI";
			valueParm[1] = financeType.getFinType();
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90329", valueParm)));
			return errorDetails;
		}

		if (financeType.isAlwHybridRate()) {
			if (finMain.getFixedRateTenor() < 0) {
				String[] valueParm = new String[2];
				valueParm[0] = "Fixed Rate Tenor";
				valueParm[1] = "0";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("65012", valueParm)));
				return errorDetails;
			}

			if (finMain.getFixedRateTenor() >= finMain.getNumberOfTerms()) {
				String[] valueParm = new String[2];
				valueParm[0] = "Fixed Rate Tenor : " + String.valueOf(finMain.getFixedRateTenor());
				valueParm[1] = " Number of terms : " + String.valueOf(finMain.getNumberOfTerms());
				;
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("30565", valueParm)));
				return errorDetails;
			}

			// validate isAlwUnderConstruction
			if (!financeType.isGrcAdjReq()) {
				if (finMain.isAlwGrcAdj()) {
					String[] valueParm = new String[2];
					valueParm[0] = "Alw Under Construction";
					valueParm[1] = finMain.getFinType();
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90329", valueParm)));
				}
			}

			if (finMain.getFixedRateTenor() > 0 && finMain.getFixedTenorRate().compareTo(BigDecimal.ZERO) <= 0) {
				String[] valueParm = new String[2];
				valueParm[0] = "Fixed Tenor Rate";
				valueParm[1] = "0";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("91121", valueParm)));
				return errorDetails;
			}
		}

		return errorDetails;
	}

	/*
	 * _______________________________________________________________________________________________________________
	 * MANUAL SCHEDULE
	 * _______________________________________________________________________________________________________________
	 */

	private List<ErrorDetail> odDatesValidation(FinScheduleData finScheduleData) {

		List<ErrorDetail> errorDetails = finScheduleData.getErrorDetails();
		FinanceMain finMain = finScheduleData.getFinanceMain();

		// it should match with frequency
		if ((StringUtils.isNotBlank(finMain.getDroplineFrq())) && (finMain.getFirstDroplineDate() != null)) {
			if (!FrequencyUtil.isFrqDate(finMain.getDroplineFrq(), finMain.getFirstDroplineDate())) {
				String[] valueParm = new String[1];
				valueParm[0] = "FirstDroplineDate: " + finMain.getFirstDroplineDate();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("91123", valueParm)));
			}
		}
		return errorDetails;

	}

	private List<ErrorDetail> manualScheduleValidation(String vldGroup, FinScheduleData finScheduleData) {
		List<ErrorDetail> errorDetails = finScheduleData.getErrorDetails();
		FinanceMain finMain = finScheduleData.getFinanceMain();
		FinanceType financeType = finScheduleData.getFinanceType();

		// Finance Type allow Manual Schedule?
		if (!financeType.isManualSchedule()) {
			String[] valueParm = new String[1];
			valueParm[0] = finMain.getFinType();
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90138", valueParm)));
		}

		// Manual Schedule with Grace
		if (!finMain.isAllowGrcPeriod()) {
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90186", null)));
		}

		// Planned Deferment Requested
		if (finMain.getPlanDeferCount() > 0) {
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90140", null)));
		}

		// Finance Type allow Step?
		if (!finMain.isStepFinance()) {
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90144", null)));
		}

		// Planned EMI Holiday Requested
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

		// Finance Type allow Planned Deferments?
		if (!financeType.isAlwPlanDeferment()) {
			String[] valueParm = new String[1];
			valueParm[0] = finMain.getFinType();
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90139", valueParm)));
		}

		// Repay Rate Basis not Flat Converting to Reducing
		if (!StringUtils.equals(finMain.getRepayRateBasis(), CalculationConstants.RATE_BASIS_C)) {
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90151", null)));
		}

		// Requested more deferments than defined in finance type?
		if (finMain.getPlanDeferCount() > financeType.getPlanDeferCount()) {
			String[] valueParm = new String[3];
			valueParm[0] = Integer.toString(finMain.getPlanDeferCount());
			valueParm[1] = Integer.toString(financeType.getPlanDeferCount());
			valueParm[2] = finMain.getFinType();
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90141", valueParm)));
		}

		// Planned EMI Holidays also requested?
		if (finMain.isPlanEMIHAlw()) {
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90142", null)));
		}

		// Stepping also requested?
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

		// Finance Type allow Step?
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

		if (finMain.isStepFinance()) {
			// ScheduleMethod
			if (StringUtils.equals(finMain.getScheduleMethod(), CalculationConstants.SCHMTHD_PFT)
					|| StringUtils.equals(finMain.getScheduleMethod(), CalculationConstants.SCHMTHD_PFTCPZ)
					|| StringUtils.equals(finMain.getScheduleMethod(), CalculationConstants.SCHMTHD_PFTCAP)) {
				String[] valueParm = new String[1];
				valueParm[0] = "Interest only on Frequency";
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

			if (finMain.isPlanEMIHAlw()) {
				String[] valueParm = new String[2];
				valueParm[0] = "Planned EMI";
				valueParm[1] = "step";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90283", valueParm)));
			}
		}

		// Planned EMI Holidays also requested?
		/*
		 * if (finMain.isPlanEMIHAlw()) { errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90150", null))); }
		 */

		// Manual Steps Requested?
		if (finMain.isAlwManualSteps()) {
			// Allow Manual Step?
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
			// Step Type
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
			// Step Policy requested
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

		if (finMain.isPlanEMIHAlwInGrace()) {
			if (!financeType.isalwPlannedEmiInGrc()) {
				String[] valueParm = new String[2];
				valueParm[0] = "Planned EMI Holiday(planEMIHAlw)";
				valueParm[1] = finMain.getFinType();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90204", valueParm)));
				return errorDetails;
			}
		}

		if (finMain.isPlanEMIHAlwInGrace()) {
			if (!finMain.isAllowGrcPeriod()) {
				String[] valueParm = new String[2];
				valueParm[0] = "Planned EMI Holiday(planEMIHAlw)";
				valueParm[1] = "allowGrcPeriod is false";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90204", valueParm)));
				return errorDetails;
			}
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
			if (!finMain.isPlanEMICpz()) {
				finMain.setPlanEMICpz(financeType.isPlanEMICpz());
			}
			if (finMain.getPlanEMIHMax() == 0) {
				finMain.setPlanEMIHMax(financeType.getPlanEMIHMax());
			}

			if (finMain.getPlanEMIHMax() < 0) {
				String[] valueParm = new String[2];
				valueParm[0] = "PlanEMIHMax";
				valueParm[1] = "0";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("91121", valueParm)));
				return errorDetails;
			}
			if (finMain.getPlanEMIHLockPeriod() == 0) {
				finMain.setPlanEMIHLockPeriod(financeType.getPlanEMIHLockPeriod());
			}
			if (finMain.getPlanEMIHLockPeriod() < 0) {
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
			if (finMain.getPlanEMIHMax() > financeType.getPlanEMIHMax()) {
				String[] valueParm = new String[2];
				valueParm[0] = "PlanEMIHMax";
				valueParm[1] = String.valueOf(financeType.getPlanEMIHMax());
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("30568", valueParm)));
				return errorDetails;
			}
			if (finMain.getPlanEMIHMaxPerYear() > financeType.getPlanEMIHMaxPerYear()) {
				String[] valueParm = new String[2];
				valueParm[0] = "PlanEMIHMaxPerYear";
				valueParm[1] = String.valueOf(financeType.getPlanEMIHMaxPerYear());
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("30568", valueParm)));
				return errorDetails;
			}
			if (!(financeType.getPlanEMIHLockPeriod() >= finMain.getPlanEMIHLockPeriod())) {
				String[] valueParm = new String[2];
				valueParm[0] = "PlanEMIHLockPeriod";
				valueParm[1] = String.valueOf(financeType.getPlanEMIHLockPeriod());
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("30568", valueParm)));
				return errorDetails;
			}
			if (StringUtils.equals(finMain.getPlanEMIHMethod(), FinanceConstants.PLANEMIHMETHOD_FRQ)) {
				if (finScheduleData.getApiPlanEMIHmonths() == null
						|| finScheduleData.getApiPlanEMIHmonths().isEmpty()) {
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
						int count = 0;
						if (!(detail.getPlanEMIHMonth() > 0 && detail.getPlanEMIHMonth() <= 12)) {
							String[] valueParm = new String[3];
							valueParm[0] = "holidayMonth";
							valueParm[1] = "1";
							valueParm[2] = "12";
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("65031", valueParm)));
							return errorDetails;
						}
						for (FinPlanEmiHoliday planEmiMnths : finScheduleData.getApiPlanEMIHmonths()) {
							if (detail.getPlanEMIHMonth() == planEmiMnths.getPlanEMIHMonth()) {
								count++;
							}
						}
						if (count >= 2) {
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
					if (finScheduleData.getApiPlanEMIHDates().size() > finMain.getPlanEMIHMax()) {
						String[] valueParm = new String[2];
						valueParm[0] = "PlanEMIHDates";
						valueParm[1] = "PlanEMIHMax";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90220", valueParm)));
						return errorDetails;
					}

					for (FinPlanEmiHoliday emiDates : finScheduleData.getApiPlanEMIHDates()) {
						int count = 0;
						for (FinPlanEmiHoliday duplicateDates : finScheduleData.getApiPlanEMIHDates()) {
							if (emiDates.getPlanEMIHDate().compareTo(duplicateDates.getPlanEMIHDate()) == 0) {
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
		if (finMain.getGrcMinRate().compareTo(finMain.getGrcMaxRate()) > 0) {
			String[] valueParm = new String[2];
			valueParm[0] = "Grace Max Rate:" + finMain.getGrcMaxRate();
			valueParm[1] = "Grace Min Rate:" + finMain.getGrcMinRate();
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("91121", valueParm)));
		}
		// Rate Type/Rate Basis
		if (!StringUtils.equals(finMain.getGrcRateBasis(), CalculationConstants.RATE_BASIS_F)
				&& !StringUtils.equals(finMain.getGrcRateBasis(), CalculationConstants.RATE_BASIS_R)) {
			String[] valueParm = new String[1];
			valueParm[0] = finMain.getGrcRateBasis();
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90211", valueParm)));
		}

		// Actual Rate
		if (finMain.getGrcPftRate().compareTo(zeroValue) < 0) {
			String[] valueParm = new String[1];
			valueParm[0] = "Grace";
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90153", valueParm)));
		}

		// Both Grace and Base Rates found
		if (StringUtils.isNotBlank(finMain.getGraceBaseRate()) && finMain.getGrcPftRate().compareTo(zeroValue) != 0) {
			String[] valueParm = new String[1];
			valueParm[0] = "Grace";
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90154", valueParm)));
		}

		// Base Rate requested?
		if (StringUtils.isBlank(finMain.getGraceBaseRate())
				&& StringUtils.isNotBlank(financeType.getFinGrcBaseRate())) {
			String[] valueParm = new String[2];
			valueParm[0] = "Grace";
			valueParm[1] = finMain.getFinType();
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90232", valueParm)));
		}

		if (StringUtils.isNotBlank(finMain.getGraceBaseRate())) {
			// Allow Base Rate?
			if (StringUtils.isBlank(financeType.getFinGrcBaseRate())) {
				String[] valueParm = new String[2];
				valueParm[0] = "Grace";
				valueParm[1] = finMain.getFinType();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90136", valueParm)));
			} else {
				// Base Rate code found?
				String brCode = finMain.getGraceBaseRate();
				String currency = finMain.getFinCcy();// TODO: discuss with
														// pradeep
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

		// Special Rate code
		if (StringUtils.isNotBlank(finMain.getGraceSpecialRate()) && StringUtils.isBlank(finMain.getGraceBaseRate())) {
			String[] valueParm = new String[1];
			valueParm[0] = "Grace";
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90155", valueParm)));
		}

		// Margin
		if (finMain.getGrcMargin().compareTo(zeroValue) != 0 && StringUtils.isBlank(finMain.getGraceBaseRate())) {
			String[] valueParm = new String[1];
			valueParm[0] = "Grace";
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90155", valueParm)));
		}

		if (!errorDetails.isEmpty()) {
			return errorDetails;
		}

		// Validate Against Minimum and Maximum Rate
		if (finMain.getGrcMinRate().compareTo(zeroValue) == 0 && finMain.getGrcMaxRate().compareTo(zeroValue) == 0) {
			// No further validation required
			return errorDetails;
		}

		BigDecimal netRate = BigDecimal.ZERO;
		// Base Rate
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

			// Check Against Minimum Rate
			if (finMain.getGrcMinRate().compareTo(zeroValue) != 0) {
				if (netRate.compareTo(finMain.getGrcMinRate()) < 0) {
					String[] valueParm = new String[2];
					valueParm[0] = round4(netRate).toString();
					valueParm[1] = round4(finMain.getGrcMinRate()).toString();
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90172", valueParm)));
				}
			}

			// Check Against Maximum Rate
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

		// Validate Profit Frequency
		ErrorDetail tempError = FrequencyUtil.validateFrequency(finMain.getGrcPftFrq());
		if (tempError != null) {
			String[] valueParm = new String[2];
			valueParm[0] = "Grace";
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90156", valueParm)));
		}

		// Validate with Allowed frequency days.
		boolean isValid = validateAlwFrqDays(finMain.getGrcPftFrq(),
				finScheduleData.getFinanceType().getFrequencyDays());
		if (!isValid) {
			String[] valueParm = new String[3];
			valueParm[0] = "Grace";
			valueParm[1] = finMain.getGrcPftFrq();
			valueParm[2] = finScheduleData.getFinanceType().getFrequencyDays();
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90271", valueParm)));
		}

		// First Interest Frequency Date Vs Start Date
		if (finMain.getNextGrcPftDate().compareTo(finMain.getFinStartDate()) <= 0) {
			String[] valueParm = new String[2];
			valueParm[0] = DateUtility.formatToShortDate(finMain.getNextGrcPftDate());
			valueParm[1] = DateUtility.formatToShortDate(finMain.getFinStartDate());
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90162", valueParm)));
		}

		// Return if any error
		if (!errorDetails.isEmpty()) {
			return errorDetails;
		}

		// Default Calculated Grace End Date using terms
		if (finMain.getGraceTerms() > 0) {
			finMain.setCalGrcTerms(finMain.getGraceTerms());
			List<Calendar> scheduleDateList = FrequencyUtil.getNextDate(finMain.getGrcPftFrq(), finMain.getGraceTerms(),
					finMain.getNextGrcPftDate(), HolidayHandlerTypes.MOVE_NONE, true).getScheduleList();

			Date geDate = null;
			if (scheduleDateList != null) {
				Calendar calendar = scheduleDateList.get(scheduleDateList.size() - 1);
				geDate = DateUtility.getDBDate(DateUtility.format(calendar.getTime(), PennantConstants.DBDateFormat));
			}

			finMain.setCalGrcEndDate(geDate);
		} else {
			finMain.setCalGrcEndDate(finMain.getGrcPeriodEndDate());
			int terms = FrequencyUtil.getTerms(finMain.getGrcPftFrq(), finMain.getNextGrcPftDate(),
					finMain.getGrcPeriodEndDate(), true, true).getTerms();
			finMain.setCalGrcTerms(terms);
		}

		// First Interest Frequency Date Vs Grace Period End Date
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

		// Allow Profit Rate Review
		if (!financeType.isFinGrcIsRvwAlw()) {
			if (finMain.isAllowGrcPftRvw() || StringUtils.isNotBlank(finMain.getGrcPftRvwFrq())
					|| finMain.getNextGrcPftRvwDate() != null) {
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90164", null)));
				return errorDetails;
			} else {
				return errorDetails;
			}
		}

		// Validate Profit Review Frequency
		ErrorDetail tempError = FrequencyUtil.validateFrequency(finMain.getGrcPftRvwFrq());
		if (tempError != null) {
			String[] valueParm = new String[2];
			valueParm[0] = "Grace";
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90157", valueParm)));
		}

		// Validate with Allowed frequency days.
		boolean isValid = validateAlwFrqDays(finMain.getGrcPftRvwFrq(),
				finScheduleData.getFinanceType().getFrequencyDays());
		if (!isValid) {
			String[] valueParm = new String[3];
			valueParm[0] = "Grace";
			valueParm[1] = finMain.getGrcPftRvwFrq();
			valueParm[2] = finScheduleData.getFinanceType().getFrequencyDays();
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90271", valueParm)));
		}

		// First Interest Review Frequency Date Vs Start Date
		if (finMain.getNextGrcPftRvwDate().compareTo(finMain.getFinStartDate()) <= 0) {
			String[] valueParm = new String[2];
			valueParm[0] = DateUtility.formatToShortDate(finMain.getNextGrcPftRvwDate());
			valueParm[1] = DateUtility.formatToShortDate(finMain.getFinStartDate());
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90166", valueParm)));
		}

		// First Interest Review Frequency Date Vs Grace Period End Date
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

		// Allow Profit Capitalization
		if (!financeType.isFinGrcIsIntCpz()) {
			if (finMain.isAllowGrcCpz() || StringUtils.isNotBlank(finMain.getGrcCpzFrq())
					|| finMain.getNextGrcCpzDate() != null) {
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90167", null)));
				return errorDetails;
			} else {
				return errorDetails;
			}
		}

		// Validate Profit Capitalization Frequency
		ErrorDetail tempError = FrequencyUtil.validateFrequency(finMain.getGrcCpzFrq());
		if (tempError != null) {
			String[] valueParm = new String[2];
			valueParm[0] = "Grace";
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90158", valueParm)));
		}

		// Validate with Allowed frequency days.
		boolean isValid = validateAlwFrqDays(finMain.getGrcCpzFrq(),
				finScheduleData.getFinanceType().getFrequencyDays());
		if (!isValid) {
			String[] valueParm = new String[3];
			valueParm[0] = "Grace";
			valueParm[1] = finMain.getGrcCpzFrq();
			valueParm[2] = finScheduleData.getFinanceType().getFrequencyDays();
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90271", valueParm)));
		}

		// First Interest Capitalization Frequency Date Vs Start Date
		if (finMain.getNextGrcCpzDate().compareTo(finMain.getFinStartDate()) <= 0) {
			String[] valueParm = new String[2];
			valueParm[0] = DateUtility.formatToShortDate(finMain.getNextGrcCpzDate());
			valueParm[1] = DateUtility.formatToShortDate(finMain.getFinStartDate());
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90168", valueParm)));
		}

		// First Interest Capitalization Frequency Date Vs Grace End Date
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
						&& !StringUtils.equals(finMain.getGrcSchdMthd(), CalculationConstants.SCHMTHD_PFTCPZ)
						&& !StringUtils.equals(finMain.getGrcSchdMthd(), CalculationConstants.SCHMTHD_GRCENDPAY)
						&& !StringUtils.equals(finMain.getGrcSchdMthd(), CalculationConstants.SCHMTHD_PFTCAP)) {
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
		if (!StringUtils.equals(finMain.getGrcSchdMthd(), CalculationConstants.SCHMTHD_PFTCAP)) {

			if (finMain.getGrcMaxAmount() != null && finMain.getGrcMaxAmount().compareTo(BigDecimal.ZERO) > 0) {
				String[] valueParm = new String[2];
				valueParm[0] = "GrcMaxAmount";
				valueParm[1] = CalculationConstants.SCHMTHD_PFTCAP;
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90298", valueParm)));

			}
		}
		if (StringUtils.equals(finMain.getGrcSchdMthd(), CalculationConstants.SCHMTHD_PFTCAP)) {
			if (finMain.getGrcMaxAmount() != null && finMain.getGrcMaxAmount().compareTo(BigDecimal.ZERO) <= 0) {
				String[] valueParm = new String[2];
				valueParm[0] = "GrcMaxAmount";
				valueParm[1] = "0";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("91121", valueParm)));

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

		// Actual Rate
		if (finMain.getGrcAdvPftRate().compareTo(zeroValue) < 0) {
			String[] valueParm = new String[1];
			valueParm[0] = "Grace Advice";
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90153", valueParm)));
		}

		// Both Grace and Base Rates found
		if (StringUtils.isNotBlank(finMain.getGrcAdvBaseRate())) {
			if (finMain.getGrcAdvPftRate().compareTo(zeroValue) != 0) {
				String[] valueParm = new String[1];
				valueParm[0] = "Grace Advise";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90154", valueParm)));
			}
		}

		// Base Rate requested?
		if (StringUtils.isNotBlank(finMain.getGrcAdvBaseRate())) {
			// Allow Base Rate?
			if (StringUtils.isBlank(financeType.getGrcAdvBaseRate())) {
				String[] valueParm = new String[2];
				valueParm[0] = "Grace Advised";
				valueParm[1] = finMain.getFinType();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90136", valueParm)));
			} else {
				// Base Rate code found?
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

		// Margin
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

		// If Next Profit date is not as Grace End Date, it should match with
		// frequency
		if (finMain.getNextGrcPftDate().compareTo(geDate) != 0
				&& !FrequencyUtil.isFrqDate(finMain.getGrcPftFrq(), finMain.getNextGrcPftDate())) {
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90177", null)));
		}

		// If Next Profit Review date is not as Grace End Date, it should match
		// with frequency
		if (financeType.isFinGrcIsRvwAlw()) {
			if (finMain.getNextGrcPftRvwDate().compareTo(geDate) != 0
					&& !FrequencyUtil.isFrqDate(finMain.getGrcPftRvwFrq(), finMain.getNextGrcPftRvwDate())) {
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90178", null)));
			}
		}

		// If Next Profit capitalization date is not as Grace End Date, it
		// should match with frequency
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

		// Consumer durables will take the defaulted rate. No validation
		// required
		if (StringUtils.equals(FinanceConstants.PRODUCT_CD, finMain.getProductCategory())) {
			return errorDetails;
		}

		// Rate Type/Rate Basis
		if (!StringUtils.equals(finMain.getRepayRateBasis(), CalculationConstants.RATE_BASIS_F)
				&& !StringUtils.equals(finMain.getRepayRateBasis(), CalculationConstants.RATE_BASIS_C)
				&& !StringUtils.equals(finMain.getRepayRateBasis(), CalculationConstants.RATE_BASIS_R)) {
			String[] valueParm = new String[1];
			valueParm[0] = finMain.getRepayRateBasis();
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90211", valueParm)));
		}

		// Actual Rate
		if (StringUtils.isBlank(financeType.getFinBaseRate())
				&& finMain.getRepayProfitRate().compareTo(zeroValue) < 0) {
			String[] valueParm = new String[1];
			valueParm[0] = REPAY;
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90153", valueParm)));
		}

		// Both Grace and Base Rates found
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

		// Base Rate requested?
		if (StringUtils.isNotBlank(finMain.getRepayBaseRate())) {
			// Allow Base Rate?
			if (StringUtils.isBlank(financeType.getFinBaseRate())) {
				String[] valueParm = new String[2];
				valueParm[0] = REPAY;
				valueParm[1] = finMain.getFinType();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90136", valueParm)));
			} else {
				// Base Rate code found?
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

		// Special Rate code
		if (StringUtils.isNotBlank(finMain.getRepaySpecialRate()) && StringUtils.isBlank(finMain.getRepayBaseRate())) {
			String[] valueParm = new String[1];
			valueParm[0] = REPAY;
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90155", valueParm)));
		}

		// Margin
		if (finMain.getRepayMargin().compareTo(zeroValue) != 0 && StringUtils.isBlank(finMain.getRepayBaseRate())) {
			String[] valueParm = new String[1];
			valueParm[0] = REPAY;
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90155", valueParm)));
		}

		if (!errorDetails.isEmpty()) {
			return errorDetails;
		}

		// Validate Against Minimum and Maximum Rate
		if (finMain.getRpyMinRate().compareTo(zeroValue) == 0 && finMain.getRpyMaxRate().compareTo(zeroValue) == 0) {
			// No further validation required
			return errorDetails;
		}

		BigDecimal netRate = BigDecimal.ZERO;
		// Base Rate
		if (StringUtils.isNotBlank(finMain.getRepayBaseRate())) {
			RateDetail rate = new RateDetail();
			rate.setBaseRateCode(finMain.getRepayBaseRate());
			rate.setCurrency(finMain.getFinCcy());
			rate.setSplRateCode(finMain.getRepaySpecialRate());
			rate.setMargin(finMain.getRepayMargin());
			rate.setValueDate(DateUtility.getAppDate());
			rate = RateUtil.getRefRate(rate);
			if (rate.getErrorDetails() != null) {
				errorDetails.add(rate.getErrorDetails());
			}
			if (errorDetails != null && !errorDetails.isEmpty()) {
				return errorDetails;
			}
			netRate = rate.getNetRefRateLoan();

			// Check Against Minimum Rate
			if (!finScheduleData.getFinanceMain().getProductCategory().equals(FinanceConstants.PRODUCT_ODFACILITY)) {
				if (netRate.compareTo(finMain.getRpyMinRate()) < 0) {
					String[] valueParm = new String[2];
					valueParm[0] = round4(netRate).toString();
					valueParm[1] = round4(finMain.getRpyMinRate()).toString();
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90174", valueParm)));
				}
			}

			// Check Against Maximum Rate
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

		// Validate Repayment Frequency
		ErrorDetail tempError = FrequencyUtil.validateFrequency(finMain.getRepayFrq());
		if (tempError != null) {
			String[] valueParm = new String[2];
			valueParm[0] = "Repay";
			valueParm[1] = finMain.getRepayFrq();
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90159", valueParm)));
		}

		boolean isValid = validateAlwFrqDays(finMain.getRepayFrq(),
				finScheduleData.getFinanceType().getFrequencyDays());
		if (!isValid) {
			String[] valueParm = new String[3];
			valueParm[0] = "Repay";
			valueParm[1] = finMain.getRepayFrq();
			valueParm[2] = financeType.getFrequencyDays();
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90271", valueParm)));
		}

		//Interest Days Basis should be 15E/360 ISDA OR Actual/365 Fixed when 
		if (ImplementationConstants.FRQ_15DAYS_REQ) {
			String repay = FrequencyUtil.getFrequencyCode(finMain.getRepayFrq());
			if (StringUtils.isNotEmpty(repay)) {
				//Frequency Code
				if (!FrequencyCodeTypes.FRQ_15DAYS.equals(repay)
						&& CalculationConstants.IDB_15E360IA.equals(finMain.getProfitDaysBasis())) {
					String frqCode = Labels.getLabel("label_Select_15DAYS");
					String[] valueParm = new String[2];
					valueParm[0] = "Repay Frequency ";
					valueParm[1] = "'" + frqCode + "'";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90337", valueParm)));
					return errorDetails;
				}

				//Interest Days Basis
				if (!CalculationConstants.IDB_15E360IA.equals(finMain.getProfitDaysBasis())
						&& FrequencyCodeTypes.FRQ_15DAYS.equals(repay)) {
					String profitDaysBasis = Labels.getLabel("label_ProfitDaysBasis_15E_360IA");//
					String[] valueParm = new String[2];
					valueParm[0] = "profitDaysBasis";
					valueParm[1] = "'" + profitDaysBasis + "'";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90337", valueParm)));
					return errorDetails;

				}
			}
		}

		// First Repayment Date Vs Start Date
		if (finMain.getNextRepayDate().compareTo(finMain.getCalGrcEndDate()) <= 0) {
			String[] valueParm = new String[2];
			valueParm[0] = DateUtility.formatToShortDate(finMain.getNextRepayDate());
			valueParm[1] = DateUtility.formatToShortDate(finMain.getCalGrcEndDate());
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90192", valueParm)));
		}

		// Return if any error
		if (!errorDetails.isEmpty()) {
			return errorDetails;
		}

		// Default Calculated Maturity Date using terms
		if (finMain.getNumberOfTerms() > 0) {
			if (!finScheduleData.getFinanceMain().getProductCategory().equals(FinanceConstants.PRODUCT_ODFACILITY)) {
				finMain.setCalTerms(finMain.getNumberOfTerms());
				List<Calendar> scheduleDateList = FrequencyUtil.getNextDate(finMain.getRepayFrq(),
						finMain.getNumberOfTerms(), finMain.getNextRepayDate(), HolidayHandlerTypes.MOVE_NONE, true)
						.getScheduleList();

				Date matDate = null;
				if (scheduleDateList != null) {
					Calendar calendar = scheduleDateList.get(scheduleDateList.size() - 1);
					matDate = DateUtility
							.getDBDate(DateUtility.format(calendar.getTime(), PennantConstants.DBDateFormat));
				}

				finMain.setCalMaturity(matDate);

			} else {

				if (StringUtils.isNotBlank(finMain.getRepayFrq()) && finMain.getNextRepayDate() != null) {
					List<Calendar> scheduleDateList = FrequencyUtil
							.getNextDate(finMain.getRepayFrq(), finMain.getNumberOfTerms(), finMain.getFinStartDate(),
									HolidayHandlerTypes.MOVE_NONE, false, 0)
							.getScheduleList();

					if (scheduleDateList != null) {
						Calendar calendar = scheduleDateList.get(scheduleDateList.size() - 1);
						Date matDate = DateUtility
								.getDBDate(DateUtility.format(calendar.getTime(), PennantConstants.DBDateFormat));
						finMain.setCalMaturity(matDate);
					}
				}
			}

			// }
		} else {
			// Default Calculated Terms based on Maturity Date
			finMain.setCalMaturity(finMain.getMaturityDate());
			int terms = FrequencyUtil
					.getTerms(finMain.getRepayFrq(), finMain.getNextRepayDate(), finMain.getMaturityDate(), true, true)
					.getTerms();
			finMain.setCalTerms(terms);
		}

		// First Repayment Date Vs Maturity Date
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

		// Validate Profit Frequency Frequency
		ErrorDetail tempError = FrequencyUtil.validateFrequency(finMain.getRepayPftFrq());
		if (tempError != null) {
			String[] valueParm = new String[2];
			valueParm[0] = "Repay";
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90156", valueParm)));
		}

		// Validate with Allowed frequency days.
		boolean isValid = validateAlwFrqDays(finMain.getRepayPftFrq(),
				finScheduleData.getFinanceType().getFrequencyDays());
		if (!isValid) {
			String[] valueParm = new String[3];
			valueParm[0] = "Repay";
			valueParm[1] = finMain.getRepayPftFrq();
			valueParm[2] = finScheduleData.getFinanceType().getFrequencyDays();
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90271", valueParm)));
		}

		// First Repayment Frequency Date Vs Start Date/Grace End Date
		if (finMain.getNextRepayPftDate().compareTo(finMain.getCalGrcEndDate()) <= 0) {
			String[] valueParm = new String[2];
			valueParm[0] = DateUtility.formatToShortDate(finMain.getNextRepayPftDate());
			valueParm[1] = DateUtility.formatToShortDate(finMain.getCalGrcEndDate());
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90194", valueParm)));
		}

		// First Interest Frequency Date Vs Next Repayment Date
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

		// Allow Profit Rate Review
		if (!financeType.isFinIsRvwAlw()) {
			if (finMain.isAllowRepayRvw() || StringUtils.isNotBlank(finMain.getRepayRvwFrq())
					|| finMain.getNextRepayRvwDate() != null) {
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90196", null)));
				return errorDetails;
			} else {
				return errorDetails;
			}
		}

		// Validate Profit Review Frequency
		ErrorDetail tempError = FrequencyUtil.validateFrequency(finMain.getRepayRvwFrq());
		if (tempError != null) {
			String[] valueParm = new String[2];
			valueParm[0] = "Repay";
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90157", valueParm)));
		}

		// Validate with Allowed frequency days.
		boolean isValid = validateAlwFrqDays(finMain.getRepayRvwFrq(),
				finScheduleData.getFinanceType().getFrequencyDays());
		if (!isValid) {
			String[] valueParm = new String[3];
			valueParm[0] = "Repay";
			valueParm[1] = finMain.getRepayRvwFrq();
			valueParm[2] = finScheduleData.getFinanceType().getFrequencyDays();
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90271", valueParm)));
		}
		// First Repayment Profit Review Date Vs Start Date/Grace End Date
		if (finMain.getNextRepayRvwDate().compareTo(finMain.getCalGrcEndDate()) <= 0) {
			String[] valueParm = new String[2];
			valueParm[0] = DateUtility.formatToShortDate(finMain.getNextRepayRvwDate());
			valueParm[1] = DateUtility.formatToShortDate(finMain.getCalGrcEndDate());
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90197", valueParm)));
		}

		// First Repayment Profit Review Date Vs Maturity Date
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

		// Allow Profit Capitalization
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
			// Validate Profit Capitalization Frequency
			ErrorDetail tempError = FrequencyUtil.validateFrequency(finMain.getRepayCpzFrq());
			if (tempError != null) {
				String[] valueParm = new String[2];
				valueParm[0] = "Repay";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90158", valueParm)));
			}

			// Validate with Allowed frequency days.
			boolean isValid = validateAlwFrqDays(finMain.getRepayCpzFrq(),
					finScheduleData.getFinanceType().getFrequencyDays());
			if (!isValid) {
				String[] valueParm = new String[3];
				valueParm[0] = "Repay";
				valueParm[1] = finMain.getRepayCpzFrq();
				valueParm[2] = finScheduleData.getFinanceType().getFrequencyDays();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90271", valueParm)));
			}

			// First Interest Capitalization Frequency Date Vs Start Date/GE
			// Date
			if (finMain.getNextRepayCpzDate().compareTo(finMain.getCalGrcEndDate()) <= 0) {
				String[] valueParm = new String[2];
				valueParm[0] = DateUtility.formatToShortDate(finMain.getNextRepayCpzDate());
				valueParm[1] = DateUtility.formatToShortDate(finMain.getCalGrcEndDate());
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90225", valueParm)));
			}

			// First Interest Capitalization Frequency Date Vs M
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
	 * RepayDates Validation
	 * _______________________________________________________________________________________________________________
	 */

	private List<ErrorDetail> repayDatesValidation(FinScheduleData finScheduleData) {

		List<ErrorDetail> errorDetails = finScheduleData.getErrorDetails();
		FinanceMain finMain = finScheduleData.getFinanceMain();

		// it should match with frequency
		if ((StringUtils.isNotBlank(finMain.getRepayFrq())) && (finMain.getNextRepayDate() != null)) {
			if (!FrequencyUtil.isFrqDate(finMain.getRepayFrq(), finMain.getNextRepayDate())) {
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90183", null)));
			}
		}
		// it should match with frequency
		if ((StringUtils.isNotBlank(finMain.getRepayRvwFrq())) && (finMain.getNextRepayRvwDate() != null)) {
			if (!FrequencyUtil.isFrqDate(finMain.getRepayRvwFrq(), finMain.getNextRepayRvwDate())) {
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90181", null)));
			}
		}

		// it should match with frequency
		if ((StringUtils.isNotBlank(finMain.getRepayCpzFrq())) && (finMain.getNextRepayCpzDate() != null)) {
			if (!FrequencyUtil.isFrqDate(finMain.getRepayCpzFrq(), finMain.getNextRepayCpzDate())) {
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90182", null)));
			}
		}
		if ((StringUtils.isNotBlank(finMain.getRepayPftFrq())) && (finMain.getNextRepayPftDate() != null)) {
			if (!FrequencyUtil.isFrqDate(finMain.getRepayPftFrq(), finMain.getNextRepayPftDate())) {
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90180", null)));
			}
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
		if (finMain.isAlwBPI()) {
			Date bpiDate = DateUtility
					.getDate(DateUtility.format(
							FrequencyUtil.getNextDate(frqBPI, 1, finMain.getFinStartDate(),
									HolidayHandlerTypes.MOVE_NONE, false).getNextFrequencyDate(),
							PennantConstants.dateFormat));
			if (DateUtility.compare(bpiDate, frqDate) >= 0) {
				/*
				 * errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("30571", null))); return errorDetails;
				 */
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
	private List<ErrorDetail> feeValidations(String vldGroup, FinScheduleData finSchdData, boolean isAPICall,
			String eventCode) {
		List<ErrorDetail> errorDetails = finSchdData.getErrorDetails();
		String finEvent = eventCode;
		boolean isOrigination = false;
		int vasFeeCount = 0;
		if (!StringUtils.equals(PennantConstants.VLD_SRV_LOAN, vldGroup)) {
			for (FinFeeDetail finFeeDetail : finSchdData.getFinFeeDetailList()) {
				if (StringUtils.equals(finFeeDetail.getFinEvent(), AccountEventConstants.ACCEVENT_VAS_FEE)) {
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
						finFeeDetail.getFeeScheduleMethod()) && finFeeDetail.getTerms() <= 0) {
					String[] valueParm = new String[1];
					valueParm[0] = "ScheduleTerms";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90221", valueParm)));
				}

				if (StringUtils.equals(CalculationConstants.REMFEE_SCHD_TO_N_INSTALLMENTS,
						finFeeDetail.getFeeScheduleMethod())
						&& finFeeDetail.getTerms() > finSchdData.getFinanceMain().getNumberOfTerms()) {
					String[] valueParm = new String[2];
					valueParm[0] = "Schedule Terms";
					valueParm[1] = "Number of terms:" + finSchdData.getFinanceMain().getNumberOfTerms();
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("30568", valueParm)));
				}
			}

			isOrigination = true;
			finEvent = PennantApplicationUtil.getEventCode(finSchdData.getFinanceMain().getFinStartDate());
		} else {
			for (FinFeeDetail finFeeDetail : finSchdData.getFinFeeDetailList()) {
				if (StringUtils.isNotBlank(finFeeDetail.getFeeScheduleMethod())) {
					String[] valueParm = new String[2];
					valueParm[0] = "Fee Schedule Method";
					valueParm[1] = finFeeDetail.getFeeTypeCode();
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90269", valueParm)));
				}
			}
		}

		List<FinTypeFees> finTypeFeeDetail = null;
		if (StringUtils.equals(PennantConstants.VLD_SRV_LOAN, vldGroup)
				|| !finSchdData.getFinanceType().isPromotionType()) {
			finTypeFeeDetail = getFinanceDetailService().getFinTypeFees(finSchdData.getFinanceMain().getFinType(),
					finEvent, isOrigination, FinanceConstants.MODULEID_FINTYPE);
		} else {
			finTypeFeeDetail = getFinanceDetailService().getFinTypeFees(finSchdData.getFinanceType().getPromotionCode(),
					finEvent, isOrigination, FinanceConstants.MODULEID_PROMOTION);
		}
		if (finTypeFeeDetail != null) {
			if (finTypeFeeDetail.size() == finSchdData.getFinFeeDetailList().size() - vasFeeCount) {
				for (FinFeeDetail feeDetail : finSchdData.getFinFeeDetailList()) {
					BigDecimal finWaiverAmount = BigDecimal.ZERO;
					boolean isFeeCodeFound = false;
					String feeTypeCode = feeDetail.getFeeTypeCode();
					feeTypeCode = extractFeeCode(feeTypeCode);
					for (FinTypeFees finTypeFee : finTypeFeeDetail) {
						String feeTypeCode2 = finTypeFee.getFeeTypeCode();
						feeTypeCode2 = extractFeeCode(feeTypeCode2);
						if (StringUtils.equals(feeTypeCode, feeTypeCode2)
								|| AccountEventConstants.ACCEVENT_VAS_FEE.equals(feeDetail.getFinEvent())) {
							isFeeCodeFound = true;

							// validate negative values
							if (feeDetail.getActualAmount().compareTo(BigDecimal.ZERO) < 0
									|| feeDetail.getPaidAmount().compareTo(BigDecimal.ZERO) < 0
									|| feeDetail.getWaivedAmount().compareTo(BigDecimal.ZERO) < 0) {
								String[] valueParm = new String[1];
								valueParm[0] = feeTypeCode;
								errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90259", valueParm)));
								return errorDetails;
							}

							// validate fee schedule method
							if (!AccountEventConstants.ACCEVENT_VAS_FEE.equals(feeDetail.getFinEvent())) {
								if (!finTypeFee.isAlwModifyFeeSchdMthd() && !StringUtils
										.equals(feeDetail.getFeeScheduleMethod(), finTypeFee.getFeeScheduleMethod())) {
									String[] valueParm = new String[1];
									valueParm[0] = feeTypeCode;
									errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90246", valueParm)));
									return errorDetails;
								}
							}

							// validate paid by Customer method
							if (StringUtils.equals(finTypeFee.getFeeScheduleMethod(),
									CalculationConstants.REMFEE_PAID_BY_CUSTOMER)) {
								if (feeDetail.getPaidAmount().compareTo(finTypeFee.getAmount()) != 0) {
									String[] valueParm = new String[1];
									valueParm[0] = feeTypeCode;
									errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90254", valueParm)));
									return errorDetails;
								}
							}
							// validate waived by bank method
							if (StringUtils.equals(finTypeFee.getFeeScheduleMethod(),
									CalculationConstants.REMFEE_WAIVED_BY_BANK)) {
								if (feeDetail.getWaivedAmount().compareTo(finWaiverAmount) != 0) {
									String[] valueParm = new String[3];
									valueParm[0] = "Waiver amount";
									valueParm[1] = "Actual waiver amount:" + String.valueOf(finWaiverAmount);
									valueParm[2] = feeTypeCode;
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
			//If we pass vas FEES details with out Loan type FEES configuration and VAS details in VAS block
			List<VASRecording> vasRecordingList = finSchdData.getVasRecordingList();
			List<String> feeCodes = getVasFeeCodes(finSchdData);
			if (CollectionUtils.isEmpty(vasRecordingList) && CollectionUtils.isNotEmpty(feeCodes)) {
				for (FinFeeDetail finFeeDetail : finSchdData.getFinFeeDetailList()) {
					//Setting validation for vas recording and fees block
					String feeTypeCode = finFeeDetail.getFeeTypeCode();
					feeTypeCode = extractFeeCode(feeTypeCode);
					if (feeCodes.contains(feeTypeCode)) {
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90328", null)));
						return errorDetails;
					} else {
						//Setting validation fees
						String[] valueParm = new String[1];
						valueParm[0] = finSchdData.getFinanceMain().getFinType();
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90245", valueParm)));
						return errorDetails;
					}
				}
				//Validation if no Fee configured in Loan type except vas and providing invalid fee code in Fee Block
			} else if (CollectionUtils.isNotEmpty(feeCodes)) {
				for (FinFeeDetail finFeeDetail : finSchdData.getFinFeeDetailList()) {
					String feeTypeCode = finFeeDetail.getFeeTypeCode();
					feeTypeCode = extractFeeCode(feeTypeCode);
					if (!feeCodes.contains(feeTypeCode)) {
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90244", null)));
						return errorDetails;
					}
				}
			}
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
	private List<ErrorDetail> insuranceValidations(String vldGroup, FinScheduleData finScheduleData,
			boolean isAPICall) {
		// TODO: write insurance validations
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

	public List<ErrorDetail> doFeeValidations(String vldSrvLoan, FinServiceInstruction finServiceInstruction,
			String eventCode) {
		FinScheduleData finSchdData = new FinScheduleData();
		finSchdData.setFinFeeDetailList(finServiceInstruction.getFinFeeDetails() == null ? new ArrayList<FinFeeDetail>()
				: finServiceInstruction.getFinFeeDetails());
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
		FinanceTaxDetail finTaxDetail = financeDetail.getFinanceTaxDetail();
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

			if (StringUtils.isBlank(finTaxDetail.getPinCode()) && finTaxDetail.getPinCodeId() == null) {
				String[] valueParm = new String[2];
				valueParm[0] = "PinCodeId or PinCode";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
				return errorDetails;
			}

			PinCode pincode = null;
			if (finTaxDetail.getPinCodeId() != null && finTaxDetail.getPinCodeId() < 0) {
				String[] valueParm = new String[2];
				valueParm[0] = "PinCodeId";
				valueParm[1] = "0";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("91121", "", valueParm)));
			} else {
				if (StringUtils.isNotBlank(finTaxDetail.getPinCode()) && (finTaxDetail.getPinCodeId() != null)) {
					pincode = pinCodeDAO.getPinCodeById(finTaxDetail.getPinCodeId(), "_AView");
					if (pincode == null) {
						String[] valueParm = new String[1];
						valueParm[0] = "PinCodeId " + String.valueOf(finTaxDetail.getPinCodeId());
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("RU0040", "", valueParm)));
					} else if (!pincode.getPinCode().equals(finTaxDetail.getPinCode())) {
						String[] valueParm = new String[2];
						valueParm[0] = "PinCode " + finTaxDetail.getPinCode();
						valueParm[1] = "PinCodeId " + String.valueOf(finTaxDetail.getPinCodeId());
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("99017", "", valueParm)));
					}
				} else {
					if (StringUtils.isNotBlank(finTaxDetail.getPinCode()) && (finTaxDetail.getPinCodeId() == null)) {
						int pinCodeCount = pinCodeDAO.getPinCodeCount(finTaxDetail.getPinCode(), "_AView");
						String[] valueParm = new String[1];
						switch (pinCodeCount) {
						case 0:
							valueParm[0] = "PinCode " + finTaxDetail.getPinCode();
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("RU0040", "", valueParm)));
							break;
						case 1:
							pincode = pinCodeDAO.getPinCode(finTaxDetail.getPinCode(), "_AView");
							finTaxDetail.setPinCodeId(pincode.getPinCodeId());
							break;
						default:
							valueParm[0] = "PinCodeId";
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("51004", "", valueParm)));
						}
					} else if (finTaxDetail.getPinCodeId() != null && StringUtils.isBlank(finTaxDetail.getPinCode())) {
						pincode = pinCodeDAO.getPinCodeById(finTaxDetail.getPinCodeId(), "_AView");
						if (pincode != null) {
							finTaxDetail.setPinCode(pincode.getPinCode());
						} else {
							String[] valueParm = new String[1];
							valueParm[0] = "PinCodeId " + String.valueOf(finTaxDetail.getPinCodeId());
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("RU0040", "", valueParm)));
						}
					}
				}
			}

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
					// validate CustomerPAN
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
							CustomerDocument customerDocument = null;
							if (customer != null) {
								customerDocument = customerDocumentService
										.getApprovedCustomerDocumentById(customer.getCustID(), panCardType);
							}
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
							/// GST number {0} should be matched with {1}
							/// properties. 90911
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
		if (StringUtils.isNotBlank(allowedFrqDays)) {
			if (!StringUtils.startsWith(frquency, FrequencyCodeTypes.FRQ_DAILY)) {

				String[] alwFrqDay = allowedFrqDays.split(PennantConstants.DELIMITER_COMMA);
				boolean isValid = false;
				for (String frqDay : alwFrqDay) {
					if (StringUtils.contains(frquency.substring(3, 5), frqDay)) {
						isValid = true;
						break;
					}
				}

				return isValid;
			}
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
	 * @param finDetail
	 * 
	 * @param detail
	 * @return
	 */
	public FinanceDetail prepareCustElgDetail(Boolean isLoadProcess, FinanceDetail detail) {
		if (detail != null) {
			// Stop Resetting data multiple times on Load Processing on Record
			// or Double click the record
			if (isLoadProcess && detail.getCustomerEligibilityCheck() != null) {
				return detail;
			}

			FinanceMain financeMain = detail.getFinScheduleData().getFinanceMain();
			Customer customer = detail.getCustomerDetails().getCustomer();
			// Current Finance Monthly Installment Calculation
			BigDecimal totalRepayAmount = financeMain.getTotalRepayAmt();
			BigDecimal curFinRepayAmt = BigDecimal.ZERO;
			int installmentMnts = DateUtility.getMonthsBetween(financeMain.getFinStartDate(),
					financeMain.getMaturityDate(), false);
			if (installmentMnts > 0) {
				curFinRepayAmt = totalRepayAmount.divide(new BigDecimal(installmentMnts), 0, RoundingMode.HALF_DOWN);
			}
			int months = DateUtility.getMonthsBetween(financeMain.getFinStartDate(), financeMain.getMaturityDate());

			// Get Customer Employee Designation
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
					custEmpDesg = StringUtils
							.trimToEmpty(detail.getCustomerDetails().getCustEmployeeDetail().getEmpDesg());
					custEmpSector = StringUtils
							.trimToEmpty(detail.getCustomerDetails().getCustEmployeeDetail().getEmpSector());
					custEmpAlocType = StringUtils
							.trimToEmpty(detail.getCustomerDetails().getCustEmployeeDetail().getEmpAlocType());
					custOtherIncome = StringUtils
							.trimToEmpty(detail.getCustomerDetails().getCustEmployeeDetail().getOtherIncome());
					custOtherIncomeAmt = detail.getCustomerDetails().getCustEmployeeDetail().getAdditionalIncome();
					int custMonthsofExp = DateUtility.getMonthsBetween(
							detail.getCustomerDetails().getCustEmployeeDetail().getEmpFrom(), DateUtility.getAppDate());
					custYearOfExp = BigDecimal.valueOf(custMonthsofExp).divide(BigDecimal.valueOf(12), 2,
							RoundingMode.CEILING);
				}
				if (ImplementationConstants.ALLOW_MULTIPLE_EMPLOYMENTS
						&& detail.getCustomerDetails().getEmploymentDetailsList() != null
						&& !detail.getCustomerDetails().getEmploymentDetailsList().isEmpty()) {
					Date custEmpFromDate = null;
					Date custEmpToDate = null;
					boolean isCurrentEmp = false;
					for (CustomerEmploymentDetail custEmpDetail : detail.getCustomerDetails()
							.getEmploymentDetailsList()) {
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
							int custMonthsofExp = DateUtility.getMonthsBetween(custEmpFromDate,
									DateUtility.getAppDate());
							custYearOfExp = BigDecimal.valueOf(custMonthsofExp).divide(BigDecimal.valueOf(12), 2,
									RoundingMode.CEILING);
						}
					}
				}
				custNationality = StringUtils
						.trimToEmpty(detail.getCustomerDetails().getCustomer().getCustNationality());
				custEmpSts = StringUtils.trimToEmpty(detail.getCustomerDetails().getCustomer().getCustEmpSts());
				custSector = StringUtils.trimToEmpty(detail.getCustomerDetails().getCustomer().getCustSector());
				custCtgCode = StringUtils.trimToEmpty(detail.getCustomerDetails().getCustomer().getCustCtgCode());
			}

			// Set Customer Data to check the eligibility
			detail.setCustomerEligibilityCheck(getFinanceDetailService().getCustEligibilityDetail(customer,
					detail.getFinScheduleData().getFinanceType().getFinCategory(), financeMain.getFinReference(),
					financeMain.getFinCcy(), curFinRepayAmt, months, null, detail.getJountAccountDetailList()));

			detail.getCustomerEligibilityCheck().setReqFinAmount(financeMain.getFinAmount());
			detail.getCustomerEligibilityCheck()
					.setDisbursedAmount(financeMain.getFinAmount().subtract(financeMain.getDownPayment()));
			detail.getCustomerEligibilityCheck().setReqFinType(financeMain.getFinType());
			detail.getCustomerEligibilityCheck().setFinProfitRate(financeMain.getEffectiveRateOfReturn());
			detail.getCustomerEligibilityCheck().setDownpayBank(financeMain.getDownPayBank());
			detail.getCustomerEligibilityCheck().setDownpaySupl(financeMain.getDownPaySupl());
			detail.getCustomerEligibilityCheck().setStepFinance(financeMain.isStepFinance());
			detail.getCustomerEligibilityCheck().setFinRepayMethod(financeMain.getFinRepayMethod());
			detail.getCustomerEligibilityCheck()
					.setAlwDPSP(detail.getFinScheduleData().getFinanceType().isAllowDownpayPgm());
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
				detail.getCustomerEligibilityCheck()
						.setCustMonthlyIncome(detail.getCustomerDetails().getCustEmployeeDetail().getMonthlyIncome());
				detail.getCustomerEligibilityCheck().setCustEmpName(
						String.valueOf(detail.getCustomerDetails().getCustEmployeeDetail().getEmpName()));

			}

			detail.getCustomerEligibilityCheck().setReqFinPurpose(financeMain.getFinPurpose());
			financeMain.setCustDSR(detail.getCustomerEligibilityCheck().getDSCR());
			detail.getCustomerEligibilityCheck().setAgreeName(financeMain.getAgreeName());
			detail.getFinScheduleData().setFinanceMain(financeMain);
		}
		return detail;
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
		if ((finScheduleData.getFinFeeDetailList() != null && !finScheduleData.getFinFeeDetailList().isEmpty())) {
			errors = feeValidations(PennantConstants.VLD_CRT_LOAN, finScheduleData, true, "");
		}
		return errors;
	}

	/**
	 * Method for do Legal Details Validation
	 * 
	 * @param financeDetail
	 * @return List<ErrorDetail>
	 */
	private List<ErrorDetail> doLegalDetailsValidation(FinanceDetail financeDetail) {
		List<ErrorDetail> errors = new ArrayList<ErrorDetail>();
		List<LegalDetail> legalDetails = financeDetail.getLegalDetailsList();
		List<CollateralAssignment> collateralAssignments = financeDetail.getCollateralAssignmentList();

		// When collateral is not available legal details are not applicable
		if (collateralAssignments == null && CollectionUtils.isEmpty(collateralAssignments)) {
			if (legalDetails != null && !legalDetails.isEmpty()) {
				String[] param = new String[2];
				param[0] = "Legal Details ";
				param[1] = "Collateral Assignment/Collateral Setup";
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("91132", param)));
				return errors;
			}
		}

		if (collateralAssignments != null && !CollectionUtils.isEmpty(collateralAssignments) && legalDetails != null
				&& !CollectionUtils.isEmpty(legalDetails)) {

			// No. of legal details should less than/equal to no. of Collateral
			// Details
			if (legalDetails.size() > collateralAssignments.size()) {
				String[] param = new String[2];
				param[0] = "No. of Legaldetails";
				param[1] = "No. of Collateral Details";
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("30508", param)));
				return errors;
			}

			// both legal sequence and Collateral Sequence are Mandatory when
			// Legal Details are Available
			// legalSeq is Mandatory
			for (LegalDetail legal : legalDetails) {
				if (legal.getLegalSeq() <= 0) {
					String[] param = new String[1];
					param[0] = "legalSeq ";
					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", param)));
					return errors;
				}
			}

			// Collateral Sequence is Mandatory
			for (CollateralAssignment assignment : collateralAssignments) {
				if (assignment.getAssignmentSeq() <= 0) {
					String[] param = new String[1];
					param[0] = "Collateral Seq ";
					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", param)));
					return errors;
				}
			}

			// validate against duplicate Collateral Sequence
			int seqCount = 0;
			for (CollateralAssignment collateralAssignment : collateralAssignments) {
				for (CollateralAssignment assignment : collateralAssignments) {
					if (collateralAssignment.getAssignmentSeq() == assignment.getAssignmentSeq()) {
						seqCount++;
					}
				}
				if (seqCount > 1) {
					String[] param = new String[2];
					param[0] = "Collateral Seq: ";
					param[1] = String.valueOf(collateralAssignment.getAssignmentSeq());
					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("41001", param)));
					return errors;
				}
				seqCount = 0;
			}

			// validate against duplicate Legal Sequence
			int count = 0;
			for (LegalDetail legalDetail : legalDetails) {
				for (LegalDetail legal : legalDetails) {
					if (legalDetail.getLegalSeq() == legal.getLegalSeq()) {
						count++;
					}
				}
				if (count > 1) {
					String[] param = new String[2];
					param[0] = "Legal Seq: ";
					param[1] = String.valueOf(legalDetail.getLegalSeq());
					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("41001", param)));
					return errors;
				}
				count = 0;
			}

			boolean isLegal = false;
			for (LegalDetail legalDetail : legalDetails) {
				for (CollateralAssignment assignment : collateralAssignments) {
					if (assignment.getAssignmentSeq() == legalDetail.getLegalSeq()) {
						isLegal = true;
						break;
					}
				}
				if (!isLegal) {
					String[] param = new String[2];
					param[0] = "Legal Seq";
					param[1] = "Collateral Seq";
					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("99017", param)));
					return errors;
				}
				isLegal = false;
			}
		}
		return errors;
	}

	private List<ErrorDetail> validatePropertyDetails(List<LegalPropertyDetail> propertyDetails) {
		List<ErrorDetail> errorDetails = new ArrayList<>();
		List<ValueLabel> yesNoList = PennantStaticListUtil.getYesNo();

		if (propertyDetails != null && !CollectionUtils.isEmpty(propertyDetails)) {
			for (LegalPropertyDetail property : propertyDetails) {
				if (StringUtils.isBlank(property.getRegistrationDistrict())) {
					String[] param = new String[1];
					param[0] = "RegistrationDistrict ";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", param)));
					return errorDetails;
				}

				if (StringUtils.isBlank(property.getRegistrationOffice())) {
					String[] param = new String[1];
					param[0] = "RegistrationOffice ";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", param)));
					return errorDetails;
				}

				if (StringUtils.isBlank(property.getNorthSideEastByWest())) {
					String[] param = new String[1];
					param[0] = "NorthSideEastByWest ";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", param)));
					return errorDetails;
				}

				if (StringUtils.isBlank(property.getSouthSideWestByEast())) {
					String[] param = new String[1];
					param[0] = "SouthSideWestByEast ";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", param)));
					return errorDetails;
				}

				if (StringUtils.isBlank(property.getEastSideNorthBySouth())) {
					String[] param = new String[1];
					param[0] = "EastSideNorthBySouth ";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", param)));
					return errorDetails;
				}

				if (StringUtils.isBlank(property.getWestSideSouthByNorth())) {
					String[] param = new String[1];
					param[0] = "WestSideSouthByNorth ";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", param)));
					return errorDetails;
				}

				if (StringUtils.isBlank(property.getUrbanLandCeiling())) {
					String[] param = new String[1];
					param[0] = "UrbanLandCeiling ";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", param)));
					return errorDetails;
				} else {
					boolean urbanLandCeiling = false;
					for (ValueLabel yesNo : yesNoList) {
						if (StringUtils.equals(property.getUrbanLandCeiling(), yesNo.getValue())) {
							urbanLandCeiling = true;
							break;
						}

					}
					if (!urbanLandCeiling) {
						String[] param = new String[2];
						param[0] = "UrbanLandCeiling ";
						param[0] = property.getUrbanLandCeiling();
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90224", param)));
						return errorDetails;
					}
				}

				if (StringUtils.isBlank(property.getMinorshareInvolved())) {
					String[] param = new String[1];
					param[0] = "MinorshareInvolved ";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", param)));
					return errorDetails;
				} else {
					boolean minorshareInvolved = false;
					for (ValueLabel yesNo : yesNoList) {
						if (StringUtils.equals(property.getMinorshareInvolved(), yesNo.getValue())) {
							minorshareInvolved = true;
							break;
						}

					}
					if (!minorshareInvolved) {
						String[] param = new String[2];
						param[0] = "MinorshareInvolved ";
						param[0] = property.getMinorshareInvolved();
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90224", param)));
						return errorDetails;
					}
				}

				if (StringUtils.isBlank(property.getPropertyIsGramanatham())) {
					String[] param = new String[1];
					param[0] = "PropertyIsGramanatham ";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", param)));
					return errorDetails;
				} else {
					boolean propertyIsGramanatham = false;
					for (ValueLabel yesNo : yesNoList) {
						if (StringUtils.equals(property.getPropertyIsGramanatham(), yesNo.getValue())) {
							propertyIsGramanatham = true;
							break;
						}

					}
					if (!propertyIsGramanatham) {
						String[] param = new String[2];
						param[0] = "PropertyIsGramanatham ";
						param[0] = property.getPropertyIsGramanatham();
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90224", param)));
						return errorDetails;
					}
				}

				if (StringUtils.isBlank(property.getPropertyReleased())) {
					String[] param = new String[1];
					param[0] = "PropertyReleased ";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", param)));
					return errorDetails;
				} else {
					boolean propertyReleased = false;
					for (ValueLabel yesNo : yesNoList) {
						if (StringUtils.equals(property.getPropertyReleased(), yesNo.getValue())) {
							propertyReleased = true;
							break;
						}

					}
					if (!propertyReleased) {
						String[] param = new String[2];
						param[0] = "PropertyReleased ";
						param[0] = property.getPropertyReleased();
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90224", param)));
						return errorDetails;
					}
				}

				if (StringUtils.isBlank(property.getPropOriginalsAvailable())) {
					String[] param = new String[1];
					param[0] = "PropOriginalsAvailable ";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", param)));
					return errorDetails;
				} else {
					boolean propOriginalsAvailable = false;
					for (ValueLabel yesNo : yesNoList) {
						if (StringUtils.equals(property.getPropOriginalsAvailable(), yesNo.getValue())) {
							propOriginalsAvailable = true;
							break;
						}

					}
					if (!propOriginalsAvailable) {
						String[] param = new String[2];
						param[0] = "PropOriginalsAvailable ";
						param[0] = property.getPropOriginalsAvailable();
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90224", param)));
						return errorDetails;
					}
				}

				if (StringUtils.isBlank(property.getPropertyIsAgricultural())) {
					String[] param = new String[1];
					param[0] = "PropertyIsAgricultural ";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", param)));
					return errorDetails;
				} else {
					boolean propertyIsAgricultural = false;
					for (ValueLabel yesNo : yesNoList) {
						if (StringUtils.equals(property.getPropertyIsAgricultural(), yesNo.getValue())) {
							propertyIsAgricultural = true;
							break;
						}

					}
					if (!propertyIsAgricultural) {
						String[] param = new String[2];
						param[0] = "PropertyIsAgricultural ";
						param[0] = property.getPropertyIsAgricultural();
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90224", param)));
						return errorDetails;
					}
				}

				if (StringUtils.isBlank(property.getNocObtainedFromLPA())) {
					String[] param = new String[1];
					param[0] = "NocObtainedFromLPA ";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", param)));
					return errorDetails;
				} else {
					boolean nocObtainedFromLPA = false;
					for (ValueLabel yesNo : yesNoList) {
						if (StringUtils.equals(property.getNocObtainedFromLPA(), yesNo.getValue())) {
							nocObtainedFromLPA = true;
							break;
						}

					}
					if (!nocObtainedFromLPA) {
						String[] param = new String[2];
						param[0] = "NocObtainedFromLPA ";
						param[0] = property.getNocObtainedFromLPA();
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90224", param)));
						return errorDetails;
					}
				}

				if (StringUtils.isBlank(property.getAnyMortgagePending())) {
					String[] param = new String[1];
					param[0] = "AnyMortgagePending ";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", param)));
					return errorDetails;
				} else {
					boolean anyMortgagePending = false;
					for (ValueLabel yesNo : yesNoList) {
						if (StringUtils.equals(property.getAnyMortgagePending(), yesNo.getValue())) {
							anyMortgagePending = true;
							break;
						}

					}
					if (!anyMortgagePending) {
						String[] param = new String[2];
						param[0] = "AnyMortgagePending ";
						param[0] = property.getAnyMortgagePending();
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90224", param)));
						return errorDetails;
					}
				}

				if (StringUtils.isNotBlank(property.getScheduleType())) {
					List<ValueLabel> scheduleTypes = PennantStaticListUtil.getScheduleTypes();
					boolean isSchedule = false;
					for (ValueLabel scheduleType : scheduleTypes) {
						if (StringUtils.equals(property.getScheduleType(), scheduleType.getValue())) {
							isSchedule = true;
							break;
						}

					}
					if (!isSchedule) {
						String[] param = new String[2];
						param[0] = "ScheduleType ";
						param[0] = property.getScheduleType();
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90224", param)));
						return errorDetails;
					}
				}

				if (StringUtils.isNotBlank(property.getPropertyType())) {
					List<ValueLabel> propertyTypes = PennantStaticListUtil.getLegalPropertyTypes();
					boolean isProperty = false;
					for (ValueLabel propertyType : propertyTypes) {
						if (StringUtils.equals(property.getPropertyType(), propertyType.getValue())) {
							isProperty = true;
							break;
						}

					}
					if (!isProperty) {
						String[] param = new String[2];
						param[0] = "PropertyType ";
						param[0] = property.getPropertyType();
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90224", param)));
						return errorDetails;
					}
				}
			}
		}

		return errorDetails;
	}

	private List<ErrorDetail> validateLegalApplicant(List<LegalApplicantDetail> applicantDetails) {
		List<ErrorDetail> errorDetails = new ArrayList<>();

		for (LegalApplicantDetail applicantDetail : applicantDetails) {
			if (StringUtils.isNotBlank(applicantDetail.getTitle())) {
				Salutation salutation = salutationDAO.getSalutationById(applicantDetail.getTitle(), "", "_AView");
				if (salutation == null) {
					String[] param = new String[2];
					param[0] = "Title";
					param[1] = applicantDetail.getTitle();
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90224", param)));
					return errorDetails;
				}
			}

			if (StringUtils.isNotBlank(applicantDetail.getIDType())) {
				DocumentType documentType = documentTypeDAO.getDocumentTypeById(applicantDetail.getIDType(), "_AView");
				if (documentType == null) {
					String[] param = new String[2];
					param[0] = "IdType";
					param[1] = applicantDetail.getIDType();
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90224", param)));
					return errorDetails;
				}
			}
		}
		return errorDetails;
	}

	private List<ErrorDetail> validateLegalDocument(List<LegalDocument> documents) {
		List<ErrorDetail> errorDetails = new ArrayList<>();

		if (documents != null && CollectionUtils.isEmpty(documents)) {
			for (LegalDocument document : documents) {
				if (StringUtils.isBlank(document.getDocumentNo())) {
					String[] param = new String[1];
					param[0] = "docNo";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", param)));
					return errorDetails;
				}

				if (document.getDocumentDate() == null) {
					String[] param = new String[1];
					param[0] = "docDate";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", param)));
					return errorDetails;
				}

				if (StringUtils.isBlank(document.getDocumentType())) {
					String[] param = new String[1];
					param[0] = "docType";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", param)));
					return errorDetails;
				} else {
					boolean isType = false;
					List<ValueLabel> documentTypes = PennantStaticListUtil.getDocumentTypes();
					for (ValueLabel docType : documentTypes) {
						if (StringUtils.equals(document.getDocumentType(), docType.getValue())) {
							isType = true;
							break;
						}
					}
					if (!isType) {
						String[] param = new String[2];
						param[0] = "docType";
						param[1] = document.getDocumentType();
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90224", param)));
						return errorDetails;
					}
				}

				if (StringUtils.isNotBlank(document.getDocumentCategory())) {
					DocumentType documentType = documentTypeDAO.getDocumentTypeById(document.getDocumentCategory(),
							"_AView");
					if (documentType == null) {
						String[] param = new String[2];
						param[0] = "docCategory";
						param[1] = document.getDocumentCategory();
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90224", param)));
						return errorDetails;
					}
				}

				if (StringUtils.isNotBlank(document.getScheduleType())) {
					List<ValueLabel> scheduleTypes = PennantStaticListUtil.getScheduleTypes();
					boolean isSchedule = false;
					for (ValueLabel scheduleType : scheduleTypes) {
						if (StringUtils.equals(document.getScheduleType(), scheduleType.getValue())) {
							isSchedule = true;
							break;
						}

					}
					if (!isSchedule) {
						String[] param = new String[2];
						param[0] = "ScheduleType ";
						param[0] = document.getScheduleType();
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90224", param)));
						return errorDetails;
					}
				}

				if (StringUtils.isBlank(document.getDocumentPropertyAddress())) {
					String[] param = new String[1];
					param[0] = "docPropertyAddrs";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", param)));
					return errorDetails;
				}

				if (StringUtils.isBlank(document.getDocumentBriefTracking())) {
					String[] param = new String[1];
					param[0] = "docBriefTracking";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", param)));
					return errorDetails;
				}

				if (StringUtils.isBlank(document.getDocumentHolderProperty())) {
					String[] param = new String[1];
					param[0] = "docHolder";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", param)));
					return errorDetails;
				}
			}
		}
		return errorDetails;
	}

	private List<ErrorDetail> validateLegalQueryDetail(QueryDetail queryDetail) {
		List<ErrorDetail> errorDetails = new ArrayList<>();

		if (StringUtils.isBlank(queryDetail.getCategoryCode())) {
			String[] param = new String[1];
			param[0] = "categoryCode";
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", param)));
			return errorDetails;
		} else {
			QueryCategory category = queryCategoryDAO.getQueryCategoryByCode(queryDetail.getCategoryCode(), "_AView");
			if (category == null) {
				String[] param = new String[2];
				param[0] = "categoryCode";
				param[1] = queryDetail.getCategoryCode();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90224", param)));
				return errorDetails;
			} else {
				queryDetail.setCategoryId(category.getId());
			}
		}

		if (StringUtils.isBlank(queryDetail.getAssignedRole())) {
			String[] param = new String[1];
			param[0] = "assignedRole";
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", param)));
			return errorDetails;
		} else {
			String workflowType = ModuleUtil.getWorkflowType("LegalDetail");
			WorkFlowDetails workFlowDetails = WorkFlowUtil.getDetailsByType(workflowType);
			String[] roles = workFlowDetails.getRoles();
			boolean isRole = false;
			for (String role : roles) {
				if (StringUtils.equals(queryDetail.getAssignedRole(), role)) {
					isRole = true;
					break;
				}
			}
			if (!isRole) {
				String[] param = new String[2];
				param[0] = "AssignedRole ";
				param[1] = Arrays.toString(roles);
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90281", param)));
				return errorDetails;
			}
		}

		List<DocumentDetails> documentDetails = queryDetail.getDocumentDetailsList();
		for (DocumentDetails documents : documentDetails) {

			if (StringUtils.isNotBlank(documents.getDocCategory())) {
				DocumentType documentType = documentTypeDAO.getDocumentTypeById(documents.getDocCategory(), "_AView");
				if (documentType == null) {
					String[] param = new String[1];
					param[0] = documents.getDocCategory();
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90401", param)));
					return errorDetails;
				}
			}

			if (!(StringUtils.equals(documents.getDoctype(), PennantConstants.DOC_TYPE_PDF)
					|| StringUtils.equals(documents.getDoctype(), PennantConstants.DOC_TYPE_DOC)
					|| StringUtils.equals(documents.getDoctype(), PennantConstants.DOC_TYPE_DOCX)
					|| StringUtils.equals(documents.getDoctype(), PennantConstants.DOC_TYPE_IMAGE)
					|| StringUtils.equals(documents.getDoctype(), PennantConstants.DOC_TYPE_ZIP)
					|| StringUtils.equals(documents.getDoctype(), PennantConstants.DOC_TYPE_7Z)
					|| StringUtils.equals(documents.getDoctype(), PennantConstants.DOC_TYPE_RAR))) {
				String[] valueParm = new String[1];
				valueParm[0] = documents.getDoctype();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90122", "", valueParm), "EN"));
				return errorDetails;
			}

			String docName = documents.getDocName();
			boolean isImage = false;
			if (StringUtils.equals(documents.getDoctype(), PennantConstants.DOC_TYPE_IMAGE)) {
				isImage = true;
				if (!docName.endsWith(".jpg") && !docName.endsWith(".jpeg") && !docName.endsWith(".png")) {
					String[] valueParm = new String[2];
					valueParm[0] = "document type: " + documents.getDocName();
					valueParm[1] = documents.getDoctype();
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90289", "", valueParm), "EN"));
					return errorDetails;
				}
			}

			// if docName has no extension.
			if (!docName.contains(".")) {
				String[] valueParm = new String[1];
				valueParm[0] = "docName: " + docName;
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90291", "", valueParm), "EN"));
				return errorDetails;
			} else {
				// document name is only extension
				String docNameExtension = docName.substring(docName.lastIndexOf("."));
				if (StringUtils.equalsIgnoreCase(documents.getDocName(), docNameExtension)) {
					String[] valueParm = new String[1];
					valueParm[0] = "docName: ";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm), "EN"));
					return errorDetails;
				}
			}
			String docExtension = docName.substring(docName.lastIndexOf(".") + 1);
			// if doc type and doc Extension are invalid
			if (!isImage) {
				if (!StringUtils.equalsIgnoreCase(documents.getDoctype(), docExtension)) {
					String[] valueParm = new String[2];
					valueParm[0] = "document type: " + documents.getDocName();
					valueParm[1] = documents.getDoctype();
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90289", "", valueParm), "EN"));
					return errorDetails;
				}
			}
		}
		return errorDetails;
	}

	private List<ErrorDetail> pslValidation(FinanceDetail financeDetail) {
		List<ErrorDetail> errorDetails = new ArrayList<>();

		PSLDetail pslDetail = financeDetail.getPslDetail();

		if (StringUtils.isBlank(pslDetail.getCategoryCode())) {
			String[] valueParm = new String[1];
			valueParm[0] = "CategoryCode";
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
		}

		String category = pSLDetailDAO.getPslCategoryCodes(pslDetail.getCategoryCode());

		if (StringUtils.isBlank(category)) {
			String[] valueParm = new String[1];
			valueParm[0] = "CategoryCode :" + pslDetail.getCategoryCode();
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90501", valueParm)));// norecords
		} else {
			if (StringUtils.equalsIgnoreCase("AGRI", pslDetail.getCategoryCode())) {
				if (StringUtils.isBlank(pslDetail.getLandHolding())) {
					String[] valueParm = new String[1];
					valueParm[0] = "landHolding";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
				} else {
					List<ValueLabel> listLandHolding = PennantStaticListUtil.getYesNo();
					boolean land = false;
					for (ValueLabel value : listLandHolding) {

						if (StringUtils.equals(value.getValue(), pslDetail.getLandHolding())) {

							land = true;
							break;
						}
					}
					if (!land) {
						String[] valueParm = new String[2];
						valueParm[0] = "landHolding";
						valueParm[1] = "Y, N";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90281", valueParm)));
					}
				}

				if (StringUtils.isBlank(pslDetail.getLandArea())) {
					String[] valueParm = new String[1];
					valueParm[0] = "LandArea";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
				} else {
					List<ValueLabel> landArea = PennantStaticListUtil.getLandAreaList();
					boolean landAreas = false;

					for (ValueLabel value : landArea) {
						if (StringUtils.equals(value.getValue(), pslDetail.getLandArea())) {
							landAreas = true;
							break;
						}
					}
					if (!landAreas) {
						String[] valueParm = new String[2];
						valueParm[0] = "landArea";
						valueParm[1] = "1, 2, 3";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90281", valueParm)));
					}
				}

				if (StringUtils.isBlank(pslDetail.getSubCategory())) {
					String[] valueParm = new String[1];
					valueParm[0] = "SubCategory";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
				} else {
					List<ValueLabel> subcategory = PennantStaticListUtil.getSubCategoryList();
					boolean categ = false;

					for (ValueLabel value : subcategory) {
						if (StringUtils.equals(value.getValue(), pslDetail.getSubCategory())) {
							categ = true;
							break;
						}
					}
					if (!categ) {
						String[] valueParm = new String[2];
						valueParm[0] = "subCategory :" + pslDetail.getSubCategory();
						valueParm[1] = "LL, TF, OL, SC";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90281", valueParm)));
					}

				}
				if (StringUtils.isBlank(pslDetail.getPurpose())) {
					String[] valueParm = new String[1];
					valueParm[0] = "purpose";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
				} else {
					int countPurp = pSLDetailDAO.getPurposeCount(pslDetail.getPurpose(), pslDetail.getCategoryCode());
					if (countPurp <= 0) {
						String[] valueParm = new String[1];
						valueParm[0] = "purpose :" + pslDetail.getPurpose();
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90501", valueParm)));
					}
				}
				if (StringUtils.isBlank(pslDetail.getEndUse())) {
					String[] valueParm = new String[1];
					valueParm[0] = "endUse";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
				} else {
					int countEnd = pSLDetailDAO.getEndUseCode(pslDetail.getEndUse(), pslDetail.getPurpose());
					if (countEnd <= 0) {
						String[] valueParm = new String[1];
						valueParm[0] = "endUse :" + pslDetail.getEndUse();
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90501", valueParm)));
					}
				}
			}

			if (StringUtils.equalsIgnoreCase("MSME", pslDetail.getCategoryCode())) {
				if (StringUtils.isBlank(pslDetail.getSector())) {
					String[] valueParm = new String[1];
					valueParm[0] = "sector";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
				} else {
					List<ValueLabel> sectorList = PennantStaticListUtil.getPSLSectorList();
					boolean sector = false;
					for (ValueLabel value : sectorList) {
						if (StringUtils.equals(value.getValue(), pslDetail.getSector())) {
							sector = true;
							break;
						}
					}
					if (!sector) {
						String[] valueParm = new String[2];
						valueParm[0] = "sector";
						valueParm[1] = "MNF, SVS, KVI";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90281", valueParm)));
					}

				}
				if (pslDetail.getAmount() <= -1) {
					String[] valueParm = new String[1];
					valueParm[0] = "Amount";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
				}
				if (StringUtils.isBlank(pslDetail.getSubCategory())) {
					String[] valueParm = new String[1];
					valueParm[0] = "subCategory";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
				} else {

					List<ValueLabel> subcategory = PennantStaticListUtil.getSubSectorList();
					boolean categ = false;

					for (ValueLabel value : subcategory) {
						if (StringUtils.equals(value.getValue(), pslDetail.getSubCategory())) {
							categ = true;
							break;
						}
					}
					if (!categ) {
						String[] valueParm = new String[2];
						valueParm[0] = "subCategory";
						valueParm[1] = "MI, SI, ME, HF";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90281", valueParm)));
					}
				}

				if (StringUtils.isBlank(pslDetail.getPurpose())) {
					String[] valueParm = new String[1];
					valueParm[0] = "purpose";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
				} else {
					int countPu = pSLDetailDAO.getPurposeCount(pslDetail.getPurpose(), pslDetail.getCategoryCode());
					if (countPu < 0) {
						String[] valueParm = new String[1];
						valueParm[0] = "purpose :" + pslDetail.getPurpose();
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90501", valueParm)));
					}
				}
			}

			if (StringUtils.equalsIgnoreCase("HF", pslDetail.getCategoryCode())) {
				if (StringUtils.isBlank(pslDetail.getSubCategory())) {
					String[] valueParm = new String[1];
					valueParm[0] = "subCategory";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
				} else {

					List<ValueLabel> subcategory = PennantStaticListUtil.getSubCategoriesList();
					boolean categ = false;

					for (ValueLabel value : subcategory) {
						if (StringUtils.equals(value.getValue(), pslDetail.getSubCategory())) {
							categ = true;
							break;
						}
					}
					if (!categ) {
						String[] valueParm = new String[1];
						valueParm[0] = pslDetail.getSubCategory();
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90501", valueParm)));
					}

				}
				if (StringUtils.isBlank(pslDetail.getPurpose())) {
					String[] valueParm = new String[1];
					valueParm[0] = "purpose";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
				} else {
					int countHs = pSLDetailDAO.getPurposeCount(pslDetail.getPurpose(), pslDetail.getCategoryCode());
					if (countHs <= 0) {
						String[] valueParm = new String[1];
						valueParm[0] = "purpose :" + pslDetail.getPurpose();
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90501", valueParm)));
					}
				}
			}
			if (StringUtils.equalsIgnoreCase("GNL", pslDetail.getCategoryCode())) {
				if (StringUtils.isBlank(pslDetail.getCategoryCode())) {
					String[] valueParm = new String[1];
					valueParm[0] = "categoryCode";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
				}
				if (StringUtils.isBlank(pslDetail.getSubCategory())) {
					String[] valueParm = new String[1];
					valueParm[0] = "subCategory";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
				} else {

					List<ValueLabel> subcategory = PennantStaticListUtil.getSubCategoryGeneralList();
					boolean categ = false;

					for (ValueLabel value : subcategory) {
						if (StringUtils.equals(value.getValue(), pslDetail.getSubCategory())) {
							categ = true;
							break;
						}
					}
					if (!categ) {
						String[] valueParm = new String[1];
						valueParm[0] = pslDetail.getSubCategory();
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90501", valueParm)));
					}

				}
				if (StringUtils.isBlank(pslDetail.getPurpose())) {
					String[] valueParm = new String[1];
					valueParm[0] = "purpose";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
				} else {
					int countGen = pSLDetailDAO.getPurposeCount(pslDetail.getPurpose(), pslDetail.getCategoryCode());
					if (countGen <= 0) {
						String[] valueParm = new String[1];
						valueParm[0] = "purpose: " + pslDetail.getPurpose();
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90501", valueParm)));
					}
				}
			}
			if (StringUtils.equalsIgnoreCase("NPSL", pslDetail.getCategoryCode())) {
				if (StringUtils.isBlank(pslDetail.getPurpose())) {
					String[] valueParm = new String[1];
					valueParm[0] = "purpose";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
				} else {
					int countNon = pSLDetailDAO.getPurposeCount(pslDetail.getPurpose(), pslDetail.getCategoryCode());
					if (countNon <= 0) {
						String[] valueParm = new String[1];
						valueParm[0] = "purpose :" + pslDetail.getPurpose();
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90501", valueParm)));
					}
				}
			}
		}
		if (!StringUtils.equalsIgnoreCase("NPSL", pslDetail.getCategoryCode())) {
			int countWea = pSLDetailDAO.getWeakerSection(pslDetail.getWeakerSection());
			if (StringUtils.isBlank(pslDetail.getWeakerSection())) {
				String[] valueParm = new String[1];
				valueParm[0] = "weakerSection";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
			} else {
				if (countWea <= 0) {
					String[] valueParm = new String[1];
					valueParm[0] = "weakerSection :" + pslDetail.getWeakerSection();
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90501", valueParm)));
				}
			}
		}
		return errorDetails;
	}

	//CovenantValidations
	public List<ErrorDetail> covenantValidation(FinanceMain financeMain, List<Covenant> covenantsList, String module) {
		logger.debug(Literal.ENTERING);
		List<ErrorDetail> errorDetails = new ArrayList<ErrorDetail>();
		CovenantType aCovenantType = null;
		//Category Validations
		if (covenantsList.size() > 1) {
			Set<String> uniqueCovenantSet = new HashSet<String>();
			long count = covenantsList.stream()
					.filter(covenant -> uniqueCovenantSet.add(covenant.getCategory() + covenant.getCovenantTypeId()))
					.count();
			if (count < covenantsList.size()) {
				String[] valueParm = new String[1];
				valueParm[0] = "Combination of Catergory & CovenantTypeId";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90273", valueParm)));
				return errorDetails;
			}
		}
		for (Covenant covenant : covenantsList) {

			List<CovenantType> covenantTypeList = covenantTypeDAO.getCvntTypesByCatgy(covenant.getCategory(), "");

			if (StringUtils.isBlank(covenant.getCategory())) {
				String[] valueParm = new String[1];
				valueParm[0] = "Category";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
				return errorDetails;
			} else {
				List<Property> covenantCategories = AppStaticList.getCovenantCategories();
				List<Object> keys = new ArrayList<Object>();
				for (Property property : covenantCategories) {
					keys.add(property.getKey());
				}
				if (!(keys.contains(covenant.getCategory()))) {
					String[] valueParm = new String[1];
					valueParm[0] = "CategoryName";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("RU0040", valueParm)));
					return errorDetails;
				}
			}

			//validating the covenant Id
			if (covenant.getCovenantTypeId() <= 0) {
				String[] valueParm = new String[1];
				valueParm[0] = "CovenantTypeId";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
				return errorDetails;
			} else {
				aCovenantType = covenantTypeDAO.getCovenantType(covenant.getCovenantTypeId(), "");
				if (aCovenantType == null) {
					String[] valueParm = new String[1];
					valueParm[0] = "CovenantType";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("RU0040", valueParm)));
					return errorDetails;
				}
			}

			long count = covenantTypeList.stream()
					.filter(covenantType -> covenantType.getId() == covenant.getCovenantTypeId()).count();
			covenant.setCovenantType(aCovenantType.getCovenantType());
			if (count == 0) {
				String[] valueParm = new String[2];
				valueParm[0] = "CovenantTypeId " + covenant.getCovenantTypeId();
				valueParm[1] = "Matched With Category " + covenant.getCategory();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("41000", valueParm)));
				return errorDetails;
			}

			if (StringUtils.isNotBlank(covenant.getDescription())
					&& covenant.getDescription().toCharArray().length > 500) {
				String[] valueParm = new String[2];
				valueParm[0] = "Description";
				valueParm[1] = "500";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90220", valueParm)));
				return errorDetails;
			}

			String covenantType = aCovenantType.getCovenantType();

			boolean aPDD = false;
			boolean aOTC = false;
			switch (covenantType) {
			case "PDD":
				aPDD = true;
				break;
			case "OTC":
				aOTC = true;
				break;
			}

			boolean isPdd = StringUtils.equals("Y", covenant.getStrPdd());
			boolean isOtc = StringUtils.equals("Y", covenant.getStrOtc());

			//validating the PDD 
			if (covenant.getStrPdd() != null && !(isPdd || StringUtils.equals("N", covenant.getStrPdd()))) {
				String[] valueParm = new String[2];
				valueParm[0] = "Pdd";
				valueParm[1] = "Y or N";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90337", valueParm)));
				return errorDetails;
			}

			//validating the OTC
			if (covenant.getStrOtc() != null && !(isOtc || StringUtils.equals("N", covenant.getStrOtc()))) {
				String[] valueParm = new String[2];
				valueParm[0] = "Otc";
				valueParm[1] = "Y or N";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90337", valueParm)));
				return errorDetails;
			}

			if ((StringUtils.isBlank(covenant.getMandatoryRole()) && !isPdd && !isOtc) && !(aPDD || aOTC)
					&& !covenant.isAllowWaiver()) {
				String[] valueParm = new String[2];
				valueParm[0] = "OTC";
				StringBuilder sb = new StringBuilder();
				sb.append("OR PDD");
				if (StringUtils.equals("LOS", covenant.getModule())) {
					sb.append(" OR Mandatory Role");
				}
				valueParm[1] = sb.toString();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90123", valueParm)));
				return errorDetails;
			}

			if (covenant.getStrDocumentReceived() != null
					&& !(StringUtils.equals("Y", covenant.getStrDocumentReceived())
							|| StringUtils.equals("N", covenant.getStrDocumentReceived()))) {
				String[] valueParm = new String[2];
				valueParm[0] = "DocumentReceived";
				valueParm[1] = "Y or N";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90337", valueParm)));
				return errorDetails;
			}

			if (isPdd && isOtc) {
				String[] valueParm = new String[2];
				valueParm[0] = "PDD";
				valueParm[1] = "OTC";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90123", valueParm)));
				return errorDetails;
			}
			if (StringUtils.isNotBlank(covenant.getMandatoryRole()) && isPdd) {

				String[] valueParm = new String[2];
				valueParm[0] = "PDD";
				valueParm[1] = "Mandatory Role";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90123", valueParm)));
				return errorDetails;
			}
			if (StringUtils.isNotBlank(covenant.getMandatoryRole()) && isOtc) {
				String[] valueParm = new String[2];
				valueParm[0] = "OTC";
				valueParm[1] = "Mandatory Role";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90123", valueParm)));
				return errorDetails;
			}

			if (StringUtils.equals("LOS", module)) {
				if ("LOS".equals(covenant.getCovenantType()) && covenant.getMandatoryRole() == null) {
					if (!isPdd || isOtc) {
						String[] valueParm = new String[1];
						valueParm[0] = "Mandatory Role";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
						return errorDetails;
					}

				}
			}

			if (StringUtils.isNotBlank(covenant.getMandatoryRole())) {
				SecurityRole secRole = finCovenantTypeDAO.isMandRoleExists(covenant.getMandatoryRole());
				if (secRole == null) {
					String[] valueParm = new String[1];
					valueParm[0] = "MandatoryRole";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("RU0040", valueParm)));
					return errorDetails;
				} else {
					covenant.setMandRoleDescription(secRole.getRoleDesc());
				}
			}
			Date maturityDate = null;
			if (StringUtils.equals("LOS", module)) {
				maturityDate = financeMain.getCalMaturity();
			} else
				maturityDate = financeMain.getMaturityDate();
			Date loanStartDate = financeMain.getFinStartDate();

			//validating the Receivable Date
			if (isPdd) {
				if (covenant.getReceivableDate() == null) {
					String[] valueParm = new String[2];
					valueParm[0] = "PDD - Y";
					valueParm[1] = "ReceivableDate";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("91132", valueParm)));
					return errorDetails;
				} else if (DateUtil.compare(covenant.getReceivableDate(), loanStartDate) < 0
						|| DateUtil.compare(covenant.getReceivableDate(), maturityDate) > 0) {
					String[] valueParm = new String[3];
					valueParm[0] = "ReceivableDate";
					valueParm[1] = "Loan StartDate";
					valueParm[2] = "MaturityDate";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("30567", valueParm)));
					return errorDetails;
				}
			} else if (covenant.getReceivableDate() != null) {
				String[] valueParm = new String[2];
				valueParm[0] = "ReceivableDate";
				valueParm[1] = "When PDD is N or Not Passed";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90204", valueParm)));
				return errorDetails;
			} else if (isOtc && covenant.getReceivableDate() != null) {
				String[] valueParm = new String[2];
				valueParm[0] = "ReceivableDate";
				valueParm[1] = "When Otc is Y";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90204", valueParm)));
				return errorDetails;
			}

			if (isPdd && covenant.getStrDocumentReceived() != null) {
				String[] valueParm = new String[2];
				valueParm[0] = "DocumentReceived";
				valueParm[1] = "When PDD is Y";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90204", valueParm)));
				return errorDetails;
			}

			//validating the Frequency
			if (StringUtils.isNotBlank(covenant.getFrequency())) {
				List<Property> listFrequency = AppStaticList.getFrequencies();
				List<Object> Frequency = new ArrayList<Object>();
				for (Property property : listFrequency) {
					Frequency.add(property.getKey());
				}
				if (!(Frequency.contains(covenant.getFrequency()))) {
					String[] valueParm = new String[1];
					valueParm[0] = "Frequency";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("RU0040", valueParm)));
					return errorDetails;
				}
			}

			//validating the Alerts
			boolean alertReqd = StringUtils.equals("Y", covenant.getStrAlertsRequired());
			if (covenant.getFrequency() == null && covenant.getStrAlertsRequired() != null) {
				String[] valueParm = new String[2];
				valueParm[0] = "AlertsRequired";
				valueParm[1] = "When Frequency is Empty";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90204", valueParm)));
				return errorDetails;
			}
			if (covenant.getStrAlertsRequired() != null
					&& !(alertReqd || StringUtils.equals("N", covenant.getStrAlertsRequired()))) {
				String[] valueParm = new String[2];
				valueParm[0] = "AlertsRequired";
				valueParm[1] = "Y or N";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90337", valueParm)));
				return errorDetails;
			}

			//validating the alerts required fields---non mandatory field
			if (alertReqd && StringUtils.isNotBlank(covenant.getAlertType())) {
				List<Property> listAlertType = AppStaticList.getAlertsFor();
				List<Object> alertUsers = new ArrayList<Object>();
				for (Property property : listAlertType) {
					alertUsers.add(property.getKey());
				}
				if (!(alertUsers.contains(covenant.getAlertType()))) {
					String[] valueParm = new String[1];
					valueParm[0] = "Alert Type";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("RU0040", valueParm)));
					return errorDetails;
				}
			} else if (StringUtils.isNotBlank(covenant.getAlertType())) {
				String[] valueParm = new String[2];
				valueParm[0] = "Alert Type";
				valueParm[1] = "AlertsRequired is N";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90204", valueParm)));
				return errorDetails;
			}

			boolean isExist;
			//validating the alerts required field
			if (alertReqd && StringUtils.isNotBlank(covenant.getNotifyTo())
					&& !StringUtils.equals(covenant.getAlertType(), "Customer")) {
				String[] roles = covenant.getNotifyTo().split(",");
				if (roles.length > 5) {
					String[] valueParm = new String[2];
					valueParm[0] = "Number of Roles to Notify";
					valueParm[1] = "5";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90220", valueParm)));
					return errorDetails;
				}
				for (String role : roles) {
					isExist = false;
					List<String> rules = covenantTypeDAO.getRules();
					for (String newRole : rules) {
						if (StringUtils.equals(newRole, role)) {
							isExist = true;
							break;
						}
					}
					if (!isExist) {
						String[] valueParm = new String[1];
						valueParm[0] = "NotifyTo:" + role;
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("RU0040", valueParm)));
						return errorDetails;
					}
				}
			} else if (StringUtils.isNotBlank(covenant.getNotifyTo())) {
				String[] valueParm = new String[2];
				valueParm[0] = "NotifyTo";
				valueParm[1] = "AlertsRequired is N or Not Passed";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90204", valueParm)));
				return errorDetails;
			} else if (alertReqd && StringUtils.isBlank(covenant.getNotifyTo())
					&& StringUtils.equals(covenant.getAlertType(), "User")) {
				String[] valueParm = new String[2];
				valueParm[0] = "For this AlertType : User";
				valueParm[1] = "Notify to";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("91132", valueParm)));
				return errorDetails;
			}

			// grace days
			if (alertReqd && covenant.getlGraceDays() != null) {
				if (covenant.getlGraceDays() >= 30) {
					String[] valueParm = new String[2];
					valueParm[0] = "GraceDays";
					valueParm[1] = "30";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90220", valueParm)));
					return errorDetails;
				}

			} else if (covenant.getlGraceDays() != null) {
				String[] valueParm = new String[2];
				valueParm[0] = "GraceDays";
				valueParm[1] = "AlertsRequired is N or Not Passed";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90204", valueParm)));
				return errorDetails;
			}

			if (alertReqd && covenant.getlAlertDays() != null) {
				if (covenant.getlAlertDays() >= 180) {
					String[] valueParm = new String[2];
					valueParm[0] = "AlertDays";
					valueParm[1] = "180";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90220", valueParm)));
					return errorDetails;
				}

			} else if (covenant.getlAlertDays() != null) {
				String[] valueParm = new String[2];
				valueParm[0] = "AlertDays";
				valueParm[1] = "AlertsRequired is N or Not Passed";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90204", valueParm)));
				return errorDetails;
			}

			//validating the CovenantDocuments
			List<CovenantDocument> covenantDocumentsList = covenant.getCovenantDocuments();
			for (CovenantDocument covenantDocument : covenantDocumentsList) {

				if (StringUtils.isBlank(covenantDocument.getDoctype())) {
					String[] valueParm = new String[1];
					valueParm[0] = "DocType";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
					return errorDetails;
				}
				String category = documentTypeDAO.getDocCategoryByDocType(covenantDocument.getDoctype(), "_aView");
				if (!StringUtils.equals(category, DocumentCategories.COVENANT.getKey())) {
					String[] valueParm = new String[1];
					valueParm[0] = "DocType";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("RU0040", valueParm)));
					return errorDetails;
				}
				Date frequencyDate = covenantDocument.getFrequencyDate();

				if (covenant.getFrequency() == null && frequencyDate != null) {
					String[] valueParm = new String[2];
					valueParm[0] = "FrequencyDate	";
					valueParm[1] = "When Frequency is Empty";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90204", valueParm)));
					return errorDetails;
				}

				List<Date> freqList = null;
				if (covenant.getFrequency() != null) {
					boolean freqDateSent = frequencyDate == null;
					switch (covenant.getFrequency()) {
					case "M":
						if (freqDateSent) {
							String[] valueParm = new String[1];
							valueParm[0] = "FrequencyDate";
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
							return errorDetails;
						}
						freqList = getFrequency(loanStartDate, maturityDate, 1);
						break;

					case "Q":
						if (freqDateSent) {
							String[] valueParm = new String[1];
							valueParm[0] = "FrequencyDate";
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
							return errorDetails;
						}
						freqList = getFrequency(loanStartDate, maturityDate, 3);
						break;

					case "H":
						if (freqDateSent) {
							String[] valueParm = new String[1];
							valueParm[0] = "FrequencyDate";
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
							return errorDetails;
						}
						freqList = getFrequency(loanStartDate, maturityDate, 6);
						break;

					case "A":
						if (freqDateSent) {
							String[] valueParm = new String[1];
							valueParm[0] = "FrequencyDate";
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
							return errorDetails;
						}
						freqList = getFrequency(loanStartDate, maturityDate, 12);
						break;

					default:
						if (frequencyDate != null) {
							String[] valueParm = new String[2];
							valueParm[0] = "Frequency Date";
							valueParm[1] = "Frequency" + covenant.getFrequency();
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90204", valueParm)));
							return errorDetails;
						}
					}
				}
				if (frequencyDate != null && freqList != null && (!freqList.contains(frequencyDate))) {
					String[] valueParm = new String[1];
					valueParm[0] = "FrequencyDate";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("RU0040", valueParm)));
					return errorDetails;
				}
				if (covenantDocument.getDocImage() != null || covenantDocument.getDocImage().length <= 0) {
					if (StringUtils.isBlank(covenantDocument.getDocName())) {
						String[] valueParm = new String[2];
						valueParm[0] = "Document Name";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
						return errorDetails;
					}
					if (StringUtils.isNotBlank(covenantDocument.getDocName())) {
						String docName = covenantDocument.getDocName().toLowerCase();
						// document name has no extension
						String extension = FilenameUtils.getExtension(docName);
						if (StringUtils.isEmpty(extension)) {
							String[] valueParm = new String[1];
							valueParm[0] = covenantDocument.getDocName();
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90291", valueParm)));
							return errorDetails;
						}
						// document name has only extension
						else if (StringUtils.isEmpty(docName.substring(0, docName.lastIndexOf(".")))) {
							String[] valueParm = new String[2];
							valueParm[0] = "Document Name";
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
							return errorDetails;
						}
						ErrorDetail errorDetail = null;
						String errorMsg = null;
						// document Name Extension validation
						docName = docName.substring(docName.lastIndexOf("."));
						errorMsg = "(Document Extension) available ext are:JPG,JPEG,PNG,PDF,MSG,DOC,DOCX,XLS,XLSX,ZIP,7Z,RAR,TXT";
						errorDetail = docExtensionValidation(docName, errorMsg);
						if (errorDetail != null) {
							errorDetails.add(errorDetail);
							return errorDetails;
						}
					}

				}

			}
			Set<String> documentTypeSet = new HashSet<String>();

			long uniqueCount = covenantDocumentsList.stream()
					.filter(covenantDocument -> documentTypeSet.add(covenantDocument.getDoctype())).count();

			if (uniqueCount < covenantDocumentsList.size()) {
				String[] valueParm = new String[1];
				valueParm[0] = "DocType";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90273", valueParm)));
				return errorDetails;
			}

			if (StringUtils.isNotBlank(covenant.getAdditionalField2())
					&& covenant.getAdditionalField2().toCharArray().length > 500) {
				String[] valueParm = new String[2];
				valueParm[0] = "Standard Value";
				valueParm[1] = "500";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90220", valueParm)));
				return errorDetails;
			}

			if (StringUtils.isNotBlank(covenant.getAdditionalField3())
					&& covenant.getAdditionalField3().toCharArray().length > 500) {
				String[] valueParm = new String[2];
				valueParm[0] = "Actual Value";
				valueParm[1] = "500";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90220", valueParm)));
				return errorDetails;
			}

			if (!StringUtils.equals(module, "LOS")) {

				if (covenant.getStrAllowPostPonement() != null
						&& !(StringUtils.equals("Y", covenant.getStrAllowPostPonement())
								|| StringUtils.equals("N", covenant.getStrAllowPostPonement()))) {
					String[] valueParm = new String[2];
					valueParm[0] = "AllowPostPonement";
					valueParm[1] = "Y or N";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90337", valueParm)));
					return errorDetails;
				}

				boolean allowPostPonement = StringUtils.equals("Y", covenant.getStrAllowPostPonement());

				if (!isPdd && covenant.getStrAllowPostPonement() != null) {
					String[] valueParm = new String[2];
					valueParm[0] = "AllowPostPonement";
					valueParm[1] = "When PDD is N  or Not Passed";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90204", valueParm)));
					return errorDetails;
				}

				if (covenant.getExtendedDate() == null && allowPostPonement) {
					String[] valueParm = new String[1];
					valueParm[0] = "ExtendedDate";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
					return errorDetails;
				} else if (covenant.getExtendedDate() != null && !allowPostPonement) {
					String[] valueParm = new String[2];
					valueParm[0] = "ExtendedDate";
					valueParm[1] = "When AllowPostPonement is N or Not Passed";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90204", valueParm)));
					return errorDetails;
				} else if (DateUtil.compare(covenant.getExtendedDate(), SysParamUtil.getAppDate()) > 0) {
					String[] valueParm = new String[2];
					valueParm[0] = "ExtendedDate";
					valueParm[1] = "Application Date";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90220", valueParm)));
					return errorDetails;
				}
			}
		}
		if (errorDetails.isEmpty()) {
			processCovenantDetails(covenantsList, financeMain, aCovenantType);
		}
		logger.debug(Literal.LEAVING);
		return errorDetails;
	}

	/**
	 * @param errorDetails
	 * @param docName
	 */
	private ErrorDetail docExtensionValidation(String docName, String errorMsg) {
		ErrorDetail errorDetail = null;
		if (!(docName.equalsIgnoreCase(DocType.PDF.getExtension())
				|| docName.equalsIgnoreCase(DocType.JPG.getExtension())
				|| docName.equalsIgnoreCase(DocType.JPEG.getExtension())
				|| docName.equalsIgnoreCase(DocType.PNG.getExtension())
				|| docName.equalsIgnoreCase(DocType.MSG.getExtension())
				|| docName.equalsIgnoreCase(DocType.DOC.getExtension())
				|| docName.equalsIgnoreCase(DocType.DOCX.getExtension())
				|| docName.equalsIgnoreCase(DocType.XLS.getExtension())
				|| docName.equalsIgnoreCase(DocType.XLSX.getExtension())
				|| docName.equalsIgnoreCase(DocType.ZIP.getExtension())
				|| docName.equalsIgnoreCase(DocType.Z7.getExtension())
				|| docName.equalsIgnoreCase(DocType.RAR.getExtension())
				|| docName.equalsIgnoreCase(DocType.TXT.getExtension()))) {
			String[] valueParm = new String[1];
			valueParm[0] = errorMsg;
			errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90122", valueParm));
		}
		return errorDetail;
	}

	private List<Date> getFrequency(final Date startDate, final Date endDate, int frequency) {
		logger.debug(Literal.ENTERING);
		List<Date> list = new ArrayList<>();
		if (startDate == null || endDate == null) {
			return list;
		}

		Date tempStartDate = (Date) startDate.clone();
		Date tempEndDate = (Date) endDate.clone();

		while (DateUtility.compare(tempStartDate, tempEndDate) <= 0) {
			//			String key = DateUtil.format(tempStartDate, DateFormat.LONG_DATE);
			list.add(tempStartDate);
			tempStartDate = DateUtil.addMonths(tempStartDate, frequency);
		}
		logger.debug(Literal.LEAVING);
		return list;

	}

	private void processCovenantDetails(List<Covenant> covenantsList, FinanceMain financeMain,
			CovenantType aCovenantType) {
		logger.debug(Literal.ENTERING);
		for (Covenant covenant : covenantsList) {

			if (StringUtils.isBlank(covenant.getDescription())) {
				covenant.setRemarks(aCovenantType.getDescription());
			} else {
				covenant.setRemarks(covenant.getDescription());
			}
			covenant.setCovenantType(aCovenantType.getCovenantType());

			if (covenant.isAllowWaiver()) {
				covenant.setPdd(false);
				covenant.setOtc(false);
				covenant.setReceivableDate(null);
				covenant.setDocumentReceived(false);
				covenant.setDocumentReceivedDate(null);
				covenant.setFrequency(null);
				covenant.setNextFrequencyDate(null);
				covenant.setGraceDueDate(null);
				covenant.setNotifyTo(null);
				covenant.setAlertsRequired(false);
				covenant.setAlertType(null);
				covenant.setAlertDays(0);
				covenant.setGraceDays(0);
				covenant.setMandatoryRole(null);
				covenant.setExtendedDate(null);
				covenant.setAdditionalField3(null);
				covenant.setAdditionalField2(null);
				covenant.setAllowPostPonement(false);
				covenant.setExtendedDate(null);
			}

			else {
				if (StringUtils.isBlank(covenant.getStrAlertsRequired())) {
					covenant.setAlertsRequired(aCovenantType.isAlertsRequired());
				} else if (covenant.getStrAlertsRequired().equals("Y"))
					covenant.setAlertsRequired(true);

				if (!covenant.isAlertsRequired()) {
					covenant.setAlertType(null);
					covenant.setNotifyTo(null);
					covenant.setGraceDays(0);
					covenant.setAlertDays(0);
				}

				setFrequencyDateField(covenant.getFrequency(), financeMain, covenant, aCovenantType);

				if (StringUtils.isBlank(covenant.getStrPdd())) {
					if ("PDD".equals(aCovenantType.getCovenantType())) {
						covenant.setPdd(true);
					}
				} else if (StringUtils.equals("Y", covenant.getStrPdd())) {
					covenant.setPdd(true);
				}

				if (covenant.isPdd()) {
					covenant.setMandatoryRole(null);
					covenant.setOtc(false);
				} else {
					covenant.setReceivableDate(null);
				}

				if (StringUtils.isBlank(covenant.getStrOtc())) {
					if ("OTC".equals(aCovenantType.getCovenantType())) {
						covenant.setOtc(true);
					}
				} else if (StringUtils.equals("Y", covenant.getStrOtc())) {
					covenant.setOtc(true);
				}

				if (covenant.isOtc()) {
					if (StringUtils.isBlank(covenant.getStrPdd())) {
						covenant.setPdd(false);
					}
					covenant.setMandatoryRole(null);
				}

				if (StringUtils.isBlank(covenant.getNotifyTo())) {
					covenant.setAlertToRoles(null);
				} else {
					covenant.setAlertToRoles(covenant.getNotifyTo());
				}

				if (StringUtils.isBlank(covenant.getFrequency())) {
					covenant.setFrequency(aCovenantType.getFrequency());
				}

				if (StringUtils.isBlank(covenant.getAlertType())) {
					covenant.setAlertType(aCovenantType.getAlertType());
				}

				if (StringUtils.equals(covenant.getAlertType(), "Customer")) {
					covenant.setAlertToRoles(null);
				}

				if (covenant.getlGraceDays() == null) {
					covenant.setGraceDays(aCovenantType.getGraceDays());
				} else
					covenant.setGraceDays(covenant.getlGraceDays());

				if (covenant.getlAlertDays() == null) {
					covenant.setAlertDays(aCovenantType.getAlertDays());
				} else
					covenant.setAlertDays(covenant.getlAlertDays());

				if (covenant.getNotifyTo() == null) {
					covenant.setAlertToRoles(aCovenantType.getAlertToRoles());
				}

				if (!covenant.isAlertsRequired()) {
					covenant.setAlertType(null);
					covenant.setNotifyTo(null);
					covenant.setGraceDays(0);
					covenant.setAlertDays(0);
				}

				if (StringUtils.equals("Y", covenant.getStrAllowPostPonement())) {
					covenant.setAllowPostPonement(true);
				}

			}

		}

	}

	public void onCheckLOS(String covenantType, Covenant covenant) {
		if ("LOS".equals(covenantType)) {
			covenant.setPdd(true);
			covenant.setOtc(true);

		} else {
			covenant.setPdd(false);
			covenant.setOtc(false);
		}
	}

	public void setFrequencyDateField(String strFrequencyType, FinanceMain financeMain, Covenant covenant,
			CovenantType aCovenant) {

		Date frequencyDate = financeMain.getFinStartDate();

		if (strFrequencyType == null || frequencyDate == null) {
			return;
		}
		Date appDate = SysParamUtil.getAppDate();

		if (DateUtility.compare(appDate, frequencyDate) < 0) {
			frequencyDate = DateUtil.addMonths(frequencyDate, 1);
		}

		if ("M".equals(strFrequencyType)) {
			frequencyDate = DateUtil.addMonths(frequencyDate, 1);
		} else if ("Q".equals(strFrequencyType)) {
			frequencyDate = DateUtil.addMonths(frequencyDate, 3);
		} else if ("H".equals(strFrequencyType)) {
			frequencyDate = DateUtil.addMonths(frequencyDate, 6);
		} else if ("A".equals(strFrequencyType)) {
			frequencyDate = DateUtil.addMonths(frequencyDate, 12);
		} else if ("O".equals(strFrequencyType)) {
			if (covenant != null && !covenant.isAlertsRequired()) {
				frequencyDate = null;
			}
		}

		if (frequencyDate != null) {
			if ("O".equals(strFrequencyType) && covenant.isAlertsRequired()) {
				covenant.setNextFrequencyDate(covenant.getReceivableDate());
			} else {
				covenant.setNextFrequencyDate(frequencyDate);
			}

			Date covenantNextFrequencyDate = covenant.getNextFrequencyDate();

			int covenantGraceDays = 0;
			if (covenant.getlGraceDays() != null) {
				covenantGraceDays = aCovenant.getGraceDays();
			}

			if (covenantNextFrequencyDate != null) {
				covenant.setGraceDueDate(DateUtil.addDays(covenantNextFrequencyDate, covenantGraceDays));
			}
		} else {
			covenant.setNextFrequencyDate(null);
			covenant.setGraceDueDate(null);
		}
		logger.debug(Literal.LEAVING);
	}
	/*
	 * ######################################################################### #######################################
	 * DEFAULT SETTER GETTER METHODS #########################################################################
	 * #######################################
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

	public void setFinReceiptHeaderDAO(FinReceiptHeaderDAO finReceiptHeaderDAO) {
		this.finReceiptHeaderDAO = finReceiptHeaderDAO;
	}

	public GeneralDepartmentService getGeneralDepartmentService() {
		return generalDepartmentService;
	}

	public void setGeneralDepartmentService(GeneralDepartmentService generalDepartmentService) {
		this.generalDepartmentService = generalDepartmentService;
	}

	public void setLovFieldDetailService(LovFieldDetailService lovFieldDetailService) {
		this.lovFieldDetailService = lovFieldDetailService;
	}

	public void setSalutationDAO(SalutationDAO salutationDAO) {
		this.salutationDAO = salutationDAO;
	}

	public void setDocumentTypeDAO(DocumentTypeDAO documentTypeDAO) {
		this.documentTypeDAO = documentTypeDAO;
	}

	public void setLimitHeaderDAO(LimitHeaderDAO limitHeaderDAO) {
		this.limitHeaderDAO = limitHeaderDAO;
	}

	public DepartmentService getDepartmentService() {
		return departmentService;
	}

	public void setDepartmentService(DepartmentService departmentService) {
		this.departmentService = departmentService;
	}

	public void setpSLDetailDAO(PSLDetailDAO pSLDetailDAO) {
		this.pSLDetailDAO = pSLDetailDAO;
	}

	public void setGenderDAO(GenderDAO genderDAO) {
		this.genderDAO = genderDAO;
	}

	public void setCustomerEMailService(CustomerEMailService customerEMailService) {
		this.customerEMailService = customerEMailService;
	}

	public OCRHeaderService getOcrHeaderService() {
		return ocrHeaderService;
	}

	public void setOcrHeaderService(OCRHeaderService ocrHeaderService) {
		this.ocrHeaderService = ocrHeaderService;
	}

	public void setCovenantTypeDAO(CovenantTypeDAO covenantTypeDAO) {
		this.covenantTypeDAO = covenantTypeDAO;
	}

	public void setFinCovenantTypeDAO(FinCovenantTypeDAO finCovenantTypeDAO) {
		this.finCovenantTypeDAO = finCovenantTypeDAO;
	}
}
