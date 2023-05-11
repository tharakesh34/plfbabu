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
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.FrequencyCodeTypes;
import com.pennant.app.constants.HolidayHandlerTypes;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.constants.LengthConstants;
import com.pennant.app.model.FrequencyDetails;
import com.pennant.app.model.RateDetail;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.FrequencyUtil;
import com.pennant.app.util.RateUtil;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.applicationmaster.BaseRateDAO;
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
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinCovenantType;
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
import com.pennant.backend.model.lmtmasters.FinanceWorkFlow;
import com.pennant.backend.model.loanquery.QueryCategory;
import com.pennant.backend.model.loanquery.QueryDetail;
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
import com.pennant.backend.service.applicationmaster.BranchService;
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
import com.pennant.backend.service.lmtmasters.FinanceWorkFlowService;
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
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.RuleReturnType;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.backend.util.VASConsatnts;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.pff.mandate.InstrumentType;
import com.pennant.pff.mandate.MandateUtil;
import com.pennant.ws.exception.ServiceException;
import com.pennant.ws.exception.ServiceExceptionDetails;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.DocType;
import com.pennanttech.pennapps.core.feature.ModuleUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.pff.document.DocumentCategories;
import com.pennanttech.pff.advancepayment.AdvancePaymentUtil.AdvanceType;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.FinanceUtil;
import com.pennanttech.pff.document.DocumentService;
import com.pennanttech.pff.overdue.constants.ChargeType;
import com.pennanttech.pff.staticlist.AppStaticList;

public class FinanceDataValidation {
	private static final Logger logger = LogManager.getLogger(FinanceDataValidation.class);

	private BaseRateDAO baseRateDAO;
	private SplRateDAO splRateDAO;
	private BranchService branchService;
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
	private FinanceWorkFlowService financeWorkFlowService;

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
	 * @param schdData
	 * @param apiFlag
	 * @param financeDetail
	 * @return
	 */
	public FinScheduleData financeDataValidation(String vldGroup, FinScheduleData schdData, boolean apiFlag,
			FinanceDetail fd, boolean isEMI) {

		FinanceMain fm = schdData.getFinanceMain();
		FinanceType finType = schdData.getFinanceType();
		int ccyFormat = CurrencyUtil.getFormat(fm.getFinCcy());

		BigDecimal zeroAmount = BigDecimal.ZERO;

		List<ErrorDetail> errors = new ArrayList<>();
		schdData.setErrorDetails(errors);

		nonFinanceValidation(vldGroup, schdData, fd);

		if (finType.isFinIsGenRef())
			fm.setFinReference(null);

		if (!errors.isEmpty()) {
			schdData.setErrorDetails(errors);

			return schdData;
		}

		basicValidation(schdData, fd, isEMI);

		if (!errors.isEmpty()) {
			schdData.setErrorDetails(errors);

			return schdData;
		}

		if (fm.isAllowGrcPeriod()) {
			graceValidation(schdData);

			if (!errors.isEmpty()) {
				schdData.setErrorDetails(errors);

				return schdData;
			}
		}

		repayValidation(schdData);

		if (!errors.isEmpty()) {
			schdData.setErrorDetails(errors);

			return schdData;
		}

		vasRecordingValidations(schdData);

		if (!errors.isEmpty()) {
			schdData.setErrorDetails(errors);
			return schdData;
		}

		if (CollectionUtils.isNotEmpty(schdData.getVasRecordingList())) {
			vasFeeValidations(schdData);

			if (!errors.isEmpty()) {
				schdData.setErrorDetails(errors);
				return schdData;
			}
		}

		// TODO FIX
		boolean stp;
		if (fd == null) {
			stp = true;
		} else {
			stp = fd.isStp();
		}

		if (!ImplementationConstants.IMD_EXT_REFERENCE) {
			List<FinFeeDetail> feeList = schdData.getFinFeeDetailList();
			if (PennantConstants.VLD_CRT_LOAN.equals(vldGroup) && !CollectionUtils.isEmpty(feeList)) {
				for (FinFeeDetail feeDetail : feeList) {
					BigDecimal paidAmount = feeDetail.getPaidAmount();

					// As per IMD Changes PAID can be happen through IMD only
					if (paidAmount != null && BigDecimal.ZERO.compareTo(paidAmount) != 0) {
						String[] valueParm = new String[2];
						valueParm[0] = feeDetail.getFeeTypeCode();
						valueParm[1] = "0";
						errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("IMD006", valueParm)));
					}
				}
			}
		}

		if (!errors.isEmpty()) {
			schdData.setErrorDetails(errors);
			return schdData;
		} // IMD

		doValidateFees(schdData, stp);

		if (!errors.isEmpty()) {
			schdData.setErrorDetails(errors);
			return schdData;
		}

		// Step validations
		if (CollectionUtils.isNotEmpty(schdData.getStepPolicyDetails())) {
			schdData.setStepPolicyDetails(schdData.getStepPolicyDetails(), true);
			stepValidations(schdData);

			if (!errors.isEmpty()) {
				schdData.setErrorDetails(errors);
				return schdData;
			}
		}

		String productCategory = fm.getProductCategory();

		BigDecimal finAssetValue = fm.getFinAssetValue();
		BigDecimal finMinAmount = finType.getFinMinAmount();
		BigDecimal finMaxAmount = finType.getFinMaxAmount();

		// Net Loan Amount
		BigDecimal netLoanAmount = fm.getFinAmount().subtract(fm.getDownPayment());

		if (finAssetValue.compareTo(BigDecimal.ZERO) > 0 && finAssetValue.compareTo(finMinAmount) == -1) {
			String[] valueParm = new String[1];
			valueParm[0] = PennantApplicationUtil.amountFormate(finMinAmount, ccyFormat);
			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90132", valueParm)));
		}

		// This is violating Over Draft Loan
		if (FinanceConstants.PRODUCT_ODFACILITY.equals(productCategory) && netLoanAmount.compareTo(finMinAmount) < 0) {
			String[] valueParm = new String[1];
			valueParm[0] = PennantApplicationUtil.amountFormate(finMinAmount, ccyFormat);
			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90132", valueParm)));
		}

		if (finMaxAmount.compareTo(zeroAmount) > 0 && finAssetValue.compareTo(finMaxAmount) > 0) {
			String[] valueParm = new String[1];
			valueParm[0] = PennantApplicationUtil.amountFormate(finMaxAmount, ccyFormat);
			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90133", valueParm)));
		}

		if (finMaxAmount.compareTo(zeroAmount) > 0 && netLoanAmount.compareTo(finMaxAmount) > 0) {
			String[] valueParm = new String[1];
			valueParm[0] = PennantApplicationUtil.amountFormate(finMaxAmount, ccyFormat);
			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90133", valueParm)));
		}

		if (fm.getReqRepayAmount().compareTo(BigDecimal.ZERO) < 0) {
			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90187", null)));
		}

		if (fm.getReqRepayAmount().compareTo(netLoanAmount) > 0) {
			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90188", null)));
		}

		finODPenaltyRateValidation(schdData);

		finOCRValidation(fd);

		if (!errors.isEmpty()) {
			schdData.setErrorDetails(errors);
			return schdData;
		}

		return schdData;
	}

	private void finOCRValidation(FinanceDetail fd) {
		if (fd == null) {
			return;
		}

		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();
		FinanceType finType = schdData.getFinanceType();

		List<ErrorDetail> errors = schdData.getErrorDetails();

		String[] valueParm = new String[1];

		if (fm == null) {
			return;
		}

		FinOCRHeader finOCRHeader = fd.getFinOCRHeader();
		boolean finOcrRequired = fm.isFinOcrRequired();

		// check OCR Header details are passed over the request if it required?
		if (finOcrRequired && finOCRHeader == null) {
			// check default OCR configured in the loan type or not
			if (finType != null && !StringUtils.isEmpty(finType.getDefaultOCR())) {
				// get default OCR header details from loan type
				OCRHeader ocrHeader = ocrHeaderService.getOCRHeaderByOCRId(finType.getDefaultOCR(),
						TableType.AVIEW.getSuffix());
				fd.setFinOCRHeader(copyOCRHeaderProperties(ocrHeader));
			} else {
				// If default OCR not configured in Loan Type?
				valueParm[0] = "finOCRHeader";
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
				return;
			}
		} else if (!finOcrRequired && finOCRHeader != null) {
			// If OCR required marked as false but passed OCR details in request
			valueParm[0] = "finOcrRequired";
			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
			return;
		} else if (finOCRHeader != null) {
			// OCR ID
			String ocrID = finOCRHeader.getOcrID();
			if (StringUtils.isEmpty(ocrID)) {
				valueParm[0] = "ocrID";
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
				return;
			}
			// Check OCR ID is available in Loan type allowed OCR's
			String allowedOCRS = finType.getAllowedOCRS();

			if (finType != null && !StringUtils.isEmpty(allowedOCRS)) {
				List<String> detailsList = Arrays.asList(allowedOCRS.split(","));
				if (detailsList != null && !detailsList.contains(ocrID)) {
					valueParm = new String[2];
					valueParm[0] = "ocrID: " + ocrID;
					valueParm[1] = finType.getFinType();
					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90285", valueParm)));
					return;
				} else if (detailsList != null && detailsList.contains(ocrID)) {
					// get OCR header details
					OCRHeader ocrHeader = ocrHeaderService.getOCRHeaderByOCRId(ocrID, TableType.AVIEW.getSuffix());
					// Overriding with Master Data
					FinOCRHeader finOCRHeaderTemp = copyOCRHeaderProperties(ocrHeader);
					// check OCR Description is Passed in the request if not override with Master Data
					if (StringUtils.isEmpty(finOCRHeader.getOcrDescription())) {
						finOCRHeader.setOcrDescription(finOCRHeaderTemp.getOcrDescription());
					}
					// check CustomerPorsion is Passed in the request if not override with Master Data
					if (finOCRHeader.getCustomerPortion().compareTo(BigDecimal.ZERO) <= 0) {
						finOCRHeader.setCustomerPortion(finOCRHeaderTemp.getCustomerPortion());
					}
					// check OCR Type is Passed in the request if not override with Master Data
					if (StringUtils.isEmpty(finOCRHeader.getOcrType())) {
						finOCRHeader.setOcrType(finOCRHeaderTemp.getOcrType());
					}

					// check totalDemand is Passed in the request if not override with Master Data
					if (BigDecimal.ZERO.compareTo(finOCRHeader.getTotalDemand()) >= 0) {
						finOCRHeader.setTotalDemand(finOCRHeaderTemp.getTotalDemand());
					}

					if (PennantConstants.SEGMENTED_VALUE.equals(finOCRHeader.getOcrType())) {
						// OCR Step details are overriding with Master data if not available in the request
						if (CollectionUtils.isEmpty(finOCRHeader.getOcrDetailList())) {
							finOCRHeader.setOcrDetailList(finOCRHeaderTemp.getOcrDetailList());
						}
					}
				}
			}
			// customerPortion
			if (finOCRHeader.getCustomerPortion().compareTo(BigDecimal.ZERO) <= 0) {
				valueParm[0] = "customerPortion";
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
				return;
			}
			// ocrType
			if (StringUtils.isEmpty(finOCRHeader.getOcrType())) {
				valueParm[0] = "ocrType";
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
				return;
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
					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90264", valueParm)));
					return;
				}
				// totalDemand
				if (BigDecimal.ZERO.compareTo(finOCRHeader.getTotalDemand()) >= 0) {
					valueParm = new String[2];
					valueParm[0] = "totalDemand";
					valueParm[1] = "/ equal to zero";
					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("30507", valueParm)));
					return;
				}

				// OCR Definition Validations
				if (PennantConstants.SEGMENTED_VALUE.equals(finOCRHeader.getOcrType())) {
					// OCR Step details
					if (CollectionUtils.isEmpty(finOCRHeader.getOcrDetailList())) {
						valueParm = new String[1];
						valueParm[0] = "ocrDetailList";
						errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
						return;
					} else {
						final Set<Integer> duplicate = new HashSet<>();
						// check step sequence for definition
						for (FinOCRDetail finOCRDetail : finOCRHeader.getOcrDetailList()) {
							int stepSequence = finOCRDetail.getStepSequence();
							if (stepSequence > 0 && !duplicate.add(stepSequence)) {
								valueParm = new String[2];
								valueParm[0] = "stepSequence";
								valueParm[1] = String.valueOf(stepSequence);
								errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("41001", valueParm)));
								return;
							} else if (stepSequence <= 0) {
								valueParm = new String[2];
								valueParm[0] = "stepSequence";
								valueParm[1] = "1";
								errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90205", valueParm)));
								return;
							}
						}
					}
					// Can OCR Step details are acceptable for Prorata?
				} else if (PennantConstants.PRORATA_VALUE.equals(finOCRHeader.getOcrType())) {
					if (!CollectionUtils.isEmpty(finOCRHeader.getOcrDetailList())) {
						valueParm = new String[2];
						valueParm[0] = "ocrDetailList";
						valueParm[1] = "OCR Type:" + PennantConstants.SEGMENTED_VALUE;
						errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90298", valueParm)));
						return;
					}
				}

				// OCR Capture validations
				if (CollectionUtils.isNotEmpty(finOCRHeader.getFinOCRCapturesList())) {
					final Set<Integer> duplicate = new HashSet<>();
					// check step sequence for definition
					for (FinOCRCapture finOCRCapture : finOCRHeader.getFinOCRCapturesList()) {
						int receiptSequence = finOCRCapture.getDisbSeq();
						if (receiptSequence > 0 && !duplicate.add(receiptSequence)) {
							valueParm = new String[2];
							valueParm[0] = "receiptSeq in finOCRCapturesList";
							valueParm[1] = String.valueOf(receiptSequence);
							errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("41001", valueParm)));
							return;
						} else if (receiptSequence <= 0) {
							valueParm = new String[2];
							valueParm[0] = "receiptSeq in finOCRCapturesList";
							valueParm[1] = "1";
							errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90205", valueParm)));
							return;
						}
						// Demand Amount
						if (BigDecimal.ZERO.compareTo(finOCRCapture.getDemandAmount()) >= 0) {
							valueParm = new String[2];
							valueParm[0] = "demandAmount in finOCRCapturesList";
							valueParm[1] = "zero";
							errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("91121", valueParm)));
							return;
						}
						// Paid amount
						if (BigDecimal.ZERO.compareTo(finOCRCapture.getPaidAmount()) >= 0) {
							valueParm = new String[2];
							valueParm[0] = "paidAmount in finOCRCapturesList";
							valueParm[1] = "zero";
							errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("91121", valueParm)));
							return;
						}
						// Receipt date
						if (finOCRCapture.getReceiptDate() == null) {
							valueParm = new String[1];
							valueParm[0] = "receiptDate in finOCRCapturesList";
							errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
							return;
						}
					}
				}
			}
		}

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
			// setting the work flow values for
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

	private void vasFeeValidations(FinScheduleData schdData) {
		List<ErrorDetail> errors = schdData.getErrorDetails();
		int vasFeeCount = 0;

		List<FinFeeDetail> finFeeDetailList = schdData.getFinFeeDetailList();
		List<VASRecording> vasRecordingList = schdData.getVasRecordingList();

		FinanceMain fm = schdData.getFinanceMain();

		int numberOfTerms = fm.getNumberOfTerms();

		if (CollectionUtils.isNotEmpty(finFeeDetailList)) {
			for (FinFeeDetail feeDetail : finFeeDetailList) {
				for (VASRecording vasRecording : vasRecordingList) {
					String feeTypeCode = feeDetail.getFeeTypeCode();
					String productCode = vasRecording.getProductCode();
					feeTypeCode = StringUtils.trimToEmpty(extractFeeCode(feeTypeCode));
					productCode = StringUtils.trimToEmpty(extractFeeCode(productCode));

					if (feeTypeCode.equals(productCode)) {
						feeDetail.setFinEvent(AccountingEvent.VAS_FEE);
						vasFeeCount++;
					}
				}
			}

			// Duplicate Fee Code check
			for (FinFeeDetail feeDetail : finFeeDetailList) {
				int count = 0;
				String feeTypeCode2 = StringUtils.trimToEmpty(extractFeeCode(feeDetail.getFeeTypeCode()));

				for (FinFeeDetail detail : finFeeDetailList) {
					String feeTypeCode = extractFeeCode(detail.getFeeTypeCode());
					if (feeTypeCode2.equals(feeTypeCode)) {
						count++;

						if (count > 1) {
							String[] valueParm = new String[1];
							valueParm[0] = "Fee Code: " + feeTypeCode;
							errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90273", valueParm)));

							return;
						}
					}
				}
			}

			if (vasRecordingList.size() <= 0 && vasFeeCount > 0) {
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90327", null)));
			} else if (vasRecordingList.size() != vasFeeCount) {
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90328", null)));
			}

			if (errors.size() > 0) {
				return;
			}
		} else {
			// setting validation for vas fees which are available in vas Block
			if (vasRecordingList.size() != vasFeeCount) {
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90328", null)));
				return;
			}
		}

		for (FinFeeDetail finFeeDetail : finFeeDetailList) {
			int feeTerms = finFeeDetail.getTerms();
			String feeScheduleMethod = finFeeDetail.getFeeScheduleMethod();

			// validate feeMethod
			if (!FinanceConstants.BPI_NO.equals(feeScheduleMethod)
					&& !CalculationConstants.REMFEE_PART_OF_DISBURSE.equals(feeScheduleMethod)
					&& !CalculationConstants.REMFEE_PART_OF_SALE_PRICE.equals(feeScheduleMethod)
					&& !CalculationConstants.REMFEE_SCHD_TO_FIRST_INSTALLMENT.equals(feeScheduleMethod)
					&& !CalculationConstants.REMFEE_SCHD_TO_ENTIRE_TENOR.equals(feeScheduleMethod)
					&& !CalculationConstants.REMFEE_SCHD_TO_N_INSTALLMENTS.equals(feeScheduleMethod)
					&& !CalculationConstants.REMFEE_PAID_BY_CUSTOMER.equals(feeScheduleMethod)
					&& !CalculationConstants.REMFEE_WAIVED_BY_BANK.equals(feeScheduleMethod)) {

				String[] valueParm = new String[2];
				valueParm[0] = feeScheduleMethod;
				valueParm[1] = feeScheduleErrorMsg();

				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90243", valueParm)));
			}

			// validate scheduleTerms
			if (CalculationConstants.REMFEE_SCHD_TO_N_INSTALLMENTS.equals(feeScheduleMethod)) {
				if (feeTerms <= 0) {
					String[] valueParm = new String[1];
					valueParm[0] = "ScheduleTerms";
					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90221", valueParm)));
				}

				if (feeTerms > numberOfTerms) {
					String[] valueParm = new String[2];
					valueParm[0] = "Schedule Terms";
					valueParm[1] = "Number of terms:" + numberOfTerms;
					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("30568", valueParm)));
				}
			}
		}

		for (FinFeeDetail feeDetail : finFeeDetailList) {
			String feeTypeCode = StringUtils.trimToEmpty(extractFeeCode(feeDetail.getFeeTypeCode()));

			BigDecimal actualAmount = feeDetail.getActualAmount();
			BigDecimal paidAmount = feeDetail.getPaidAmount();
			BigDecimal waivedAmount = feeDetail.getWaivedAmount();

			for (VASRecording vasRecording : vasRecordingList) {
				String productCode = extractFeeCode(vasRecording.getProductCode());
				BigDecimal fee = vasRecording.getFee();
				BigDecimal remainingFee = actualAmount.subtract(waivedAmount.add(paidAmount));

				if (feeTypeCode.equals(productCode)) {
					// validate negative values
					if (actualAmount.compareTo(BigDecimal.ZERO) < 0 || paidAmount.compareTo(BigDecimal.ZERO) < 0
							|| waivedAmount.compareTo(BigDecimal.ZERO) < 0) {
						String[] valueParm = new String[1];
						valueParm[0] = feeTypeCode;
						errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90259", valueParm)));
						return;
					}

					// validate vas fee amount
					if (actualAmount.compareTo(fee) != 0) {
						String[] valueParm = new String[3];
						valueParm[0] = "Fee amount";
						valueParm[1] = "VAS recording fee:" + String.valueOf(fee);
						valueParm[2] = feeTypeCode;
						errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90268", valueParm)));

						return;
					}

					// validate actual fee amount with waiver+paid amount
					if (remainingFee.compareTo(BigDecimal.ZERO) < 0) {
						String[] valueParm = new String[3];
						valueParm[0] = "Sum of waiver and paid amounts";
						valueParm[1] = "Actual fee amount:" + String.valueOf(actualAmount);
						valueParm[2] = feeTypeCode;
						errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90268", valueParm)));

						return;
					}
				}
			}
		}
	}

	private String feeScheduleErrorMsg() {
		StringBuilder value = new StringBuilder();
		value.append(CalculationConstants.REMFEE_PART_OF_DISBURSE).append(",");
		value.append(CalculationConstants.REMFEE_PART_OF_SALE_PRICE).append(",");
		value.append(CalculationConstants.REMFEE_SCHD_TO_FIRST_INSTALLMENT).append(",");
		value.append(CalculationConstants.REMFEE_SCHD_TO_ENTIRE_TENOR).append(",");
		value.append(CalculationConstants.REMFEE_SCHD_TO_N_INSTALLMENTS).append(",");
		value.append(CalculationConstants.REMFEE_PAID_BY_CUSTOMER).append(",");
		value.append(CalculationConstants.REMFEE_WAIVED_BY_BANK);

		return value.toString();
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
		// Vas Products configured in FinType
		if (CollectionUtils.isNotEmpty(finTypeVASProductsList)) {
			for (FinTypeVASProducts finTypeVASProducts : finTypeVASProductsList) {
				feeCodes.add(finTypeVASProducts.getVasProduct());
			}
		}
		return feeCodes;
	}

	private void vasRecordingValidations(FinScheduleData schdData) {
		List<ErrorDetail> errors = schdData.getErrorDetails();

		Date appDate = SysParamUtil.getAppDate();
		Date dftStartDate = SysParamUtil.getValueAsDate(PennantConstants.APP_DFT_START_DATE);
		Date dftEndDate = SysParamUtil.getValueAsDate("APP_DFT_END_DATE");

		FinanceType finType = schdData.getFinanceType();

		String loanType = finType.getFinType();

		int mandatoryVasCount = 0;

		// fetch the vasProduct list based on the FinanceType
		finType.setFinTypeVASProductsList(finTypeVASProductsDAO.getVASProductsByFinType(loanType, ""));

		List<FinTypeVASProducts> finTypeVASProductsList = finType.getFinTypeVASProductsList();

		for (FinTypeVASProducts vasProduct : finTypeVASProductsList) {
			if (vasProduct.isMandatory()) {
				mandatoryVasCount++;
			}
		}

		List<VASRecording> vasRecordingList = schdData.getVasRecordingList();

		if (mandatoryVasCount > 0) {
			if (CollectionUtils.isEmpty(vasRecordingList)) {
				String[] valueParm = new String[1];
				valueParm[0] = "VAS";
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
				return;
			}
		}

		if (CollectionUtils.isEmpty(vasRecordingList)) {
			return;
		}

		int userVasCount = 0;

		boolean isVasProduct = false;

		for (FinTypeVASProducts vasProduct : finTypeVASProductsList) {
			for (VASRecording detail : vasRecordingList) {
				String productCode = StringUtils.trimToEmpty(extractFeeCode(detail.getProductCode()));

				if (productCode.equals(vasProduct.getVasProduct())) {
					isVasProduct = true;
					if (vasProduct.isMandatory()) {
						userVasCount++;
					}
				}
			}
		}

		if (!isVasProduct) {
			String[] valueParm = new String[1];
			valueParm[0] = loanType;

			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90284", valueParm)));

			return;
		}

		if (userVasCount != mandatoryVasCount) {
			String[] valueParm = new String[1];
			valueParm[0] = loanType;

			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90284", valueParm)));

			return;
		}

		for (VASRecording detail : vasRecordingList) {
			String productCode = detail.getProductCode();

			if (StringUtils.isBlank(productCode)) {
				String[] valueParm = new String[1];
				valueParm[0] = "product";
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));

				return;
			}

			if (detail.getFee() == null) {
				String[] valueParm = new String[1];
				valueParm[0] = "Fee";
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));

				return;
			}

			VASConfiguration vasConfig = vASConfigurationService.getVASConfigurationByCode(productCode);

			if (vasConfig == null || !vasConfig.isActive()) {
				String[] valueParm = new String[2];
				valueParm[0] = "Product";
				valueParm[1] = productCode;
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90224", valueParm)));

				return;
			}

			if ("Loan".equals(detail.getPostingAgainst())) {
				detail.setPostingAgainst(VASConsatnts.VASAGAINST_FINANCE);
			}

			if (!StringUtils.equals(vasConfig.getRecAgainst(), detail.getPostingAgainst())) {
				String[] valueParm = new String[2];
				valueParm[0] = "PostingAgainst";
				valueParm[1] = productCode;

				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90224", valueParm)));

				return;
			}

			if (!vasConfig.isAllowFeeToModify()) {
				if (detail.getFee().compareTo(vasConfig.getVasFee()) != 0) {
					String[] valueParm = new String[2];
					valueParm[0] = "Fee:" + detail.getFee();
					valueParm[1] = "VasConfig Fee:" + vasConfig.getVasFee();

					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("30570", valueParm)));

					return;
				}
			} else if (detail.getFee().compareTo(BigDecimal.ZERO) < 0) {
				String[] valueParm = new String[2];
				valueParm[0] = "VAS Fee";
				valueParm[1] = "Zero";

				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90205", valueParm)));

				return;
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
					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90264", valueParm)));

					return;
				}
			} else {
				String[] valueParm = new String[1];
				valueParm[0] = "feePaymentMode";

				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));

				return;
			}

			if (detail.getValueDate() != null) {
				if (detail.getValueDate().before(dftStartDate) || detail.getValueDate().after(appDate)) {
					String[] valueParm = new String[3];
					valueParm[0] = "Value Date";
					valueParm[1] = DateUtil.formatToLongDate(dftStartDate);
					valueParm[2] = DateUtil.formatToLongDate(appDate);
					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90318", valueParm)));

					return;
				}
			} else {
				detail.setValueDate(appDate);
			}

			Date accrualTillDate = detail.getAccrualTillDate();
			Date recurringDate = detail.getRecurringDate();

			if (vasConfig.isFeeAccrued()) {
				if (accrualTillDate == null) {
					String[] valueParm = new String[1];
					valueParm[0] = "accrualTillDate";
					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));

					return;
				} else {
					if (accrualTillDate.before(appDate) || accrualTillDate.after(dftEndDate)) {
						String[] valueParm = new String[3];
						valueParm[0] = "AccrualTillDate";
						valueParm[1] = DateUtil.formatToLongDate(appDate);
						valueParm[2] = DateUtil.formatToLongDate(dftEndDate);
						errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90318", valueParm)));

						return;
					}
				}
			} else {
				if (accrualTillDate != null) {
					String[] valueParm = new String[2];
					valueParm[0] = "accrualTillDate";
					valueParm[1] = "FeeAccrued";
					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90298", "", valueParm), "EN"));

					return;
				}

				accrualTillDate = appDate;
				detail.setAccrualTillDate(appDate);
			}

			if (vasConfig.isRecurringType()) {
				if (recurringDate == null) {
					String[] valueParm = new String[1];
					valueParm[0] = "recurringDate";
					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));

					return;
				} else {
					if (recurringDate.before(appDate) || recurringDate.after(dftEndDate)) {
						String[] valueParm = new String[3];
						valueParm[0] = "RecurringDate";
						valueParm[2] = DateUtil.formatToLongDate(dftEndDate);
						valueParm[1] = DateUtil.formatToLongDate(appDate);
						errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90318", "", valueParm)));

						return;
					}
				}
			} else {
				if (recurringDate != null) {
					String[] valueParm = new String[2];
					valueParm[0] = "RecurringDate";
					valueParm[1] = "RecurringType is Active";
					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90298", "", valueParm), "EN"));

					return;
				}

				recurringDate = appDate;
				detail.setRecurringDate(appDate);
				detail.setRenewalFee(BigDecimal.ZERO);
			}

			String dsaId = detail.getDsaId();
			String dmaId = detail.getDmaId();
			String ofcrId = detail.getFulfilOfficerId();
			String referralId = detail.getReferralId();

			if (StringUtils.isNotBlank(dsaId)
					&& relationshipOfficerService.getApprovedRelationshipOfficerById(dsaId) == null) {
				String[] valueParm = new String[1];
				valueParm[0] = dsaId;
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90501", valueParm)));

				return;
			}

			if (StringUtils.isNotBlank(dmaId)
					&& relationshipOfficerService.getApprovedRelationshipOfficerById(dmaId) == null) {
				String[] valueParm = new String[1];
				valueParm[0] = dmaId;
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90501", valueParm)));

				return;
			}

			if (StringUtils.isNotBlank(ofcrId)
					&& relationshipOfficerService.getApprovedRelationshipOfficerById(ofcrId) == null) {
				String[] valueParm = new String[1];
				valueParm[0] = ofcrId;
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90501", "", valueParm), "EN"));

				return;
			}

			if (StringUtils.isNotBlank(referralId)
					&& relationshipOfficerService.getApprovedRelationshipOfficerById(referralId) == null) {
				String[] valueParm = new String[1];
				valueParm[0] = referralId;
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90501", valueParm)));

				return;
			}

			detail.setFeeAccounting(vasConfig.getFeeAccounting());

			int extendedDetailsCount = 0;
			ExtendedFieldHeader efh = vasConfig.getExtendedFieldHeader();
			List<ExtendedFieldDetail> extFldDtls = efh.getExtendedFieldDetails();
			List<ExtendedFieldDetail> exdFldConfig = extFldDtls;

			if (exdFldConfig != null) {
				for (ExtendedFieldDetail extended : exdFldConfig) {
					if (extended.isFieldMandatory()) {
						extendedDetailsCount++;
					}
				}
			}

			List<ExtendedField> extndDtls = detail.getExtendedDetails();
			if (extendedDetailsCount > 0 && (CollectionUtils.isEmpty(extndDtls))) {
				String[] valueParm = new String[1];
				valueParm[0] = "ExtendedDetails";
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm)));

				return;
			}

			if (CollectionUtils.isNotEmpty(extndDtls)) {
				for (ExtendedField details : extndDtls) {
					int exdMandConfigCount = 0;

					for (ExtendedFieldData efd : details.getExtendedFieldDataList()) {
						if (StringUtils.isBlank(efd.getFieldName())) {
							String[] valueParm = new String[1];
							valueParm[0] = "fieldName";
							errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));

							return;
						}

						if (StringUtils.isBlank(Objects.toString(efd.getFieldValue(), ""))) {
							String[] valueParm = new String[1];
							valueParm[0] = "fieldValue";
							errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));

							return;
						}

						boolean isFeild = false;

						if (extFldDtls != null) {
							for (ExtendedFieldDetail fldDt : extFldDtls) {
								if (StringUtils.equals(fldDt.getFieldName(), efd.getFieldName())) {
									if (fldDt.isFieldMandatory()) {
										exdMandConfigCount++;
									}

									errors.addAll(extendedFieldDetailsService.validateExtendedFieldData(fldDt, efd));
									isFeild = true;
								}
							}

							if (!isFeild) {
								String[] valueParm = new String[1];
								valueParm[0] = "vas setup";
								errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90265", valueParm)));

								return;
							}
						}
					}

					if (extendedDetailsCount != exdMandConfigCount) {
						errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90297", "", null)));
						return;
					}
				}

			}

			Map<String, Object> mapValues = new HashMap<>();
			if (extndDtls != null) {
				for (ExtendedField details : extndDtls) {
					for (ExtendedFieldData extFieldData : details.getExtendedFieldDataList()) {

						String fieldName = StringUtils.trimToEmpty(extFieldData.getFieldName());
						for (ExtendedFieldDetail efd : exdFldConfig) {

							if (fieldName.equals(efd.getFieldName())) {
								if (ExtendedFieldConstants.FIELDTYPE_BASERATE.equals(efd.getFieldType())) {
									extFieldData.setFieldName(fieldName.concat("_BR"));
								}

								if (ExtendedFieldConstants.FIELDTYPE_PHONE.equals(efd.getFieldType())) {
									extFieldData.setFieldName(fieldName.concat("_SC"));
								}
							}

							mapValues.put(fieldName, extFieldData.getFieldValue());
						}
					}
				}
			}

			// do script pre validation and post validation
			ScriptErrors error = null;
			if (vasConfig.isPostValidationReq()) {
				error = scriptValidationService.getPostValidationErrors(vasConfig.getPostValidation(), mapValues);
			}
			if (error != null) {
				List<ScriptError> errorsList = error.getAll();

				for (ScriptError err : errorsList) {
					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("", "90909", "", err.getValue(), null, null)));

					return;
				}
			}
		}

		for (VASRecording vr : vasRecordingList) {
			int count = 0;
			String productCode = vr.getProductCode();
			productCode = StringUtils.trimToEmpty(extractFeeCode(productCode));

			for (VASRecording vasRcding : vasRecordingList) {
				String vasCode = extractFeeCode(vasRcding.getProductCode());

				if (productCode.equals(vasCode)) {
					count++;

					if (count > 1) {
						String[] valueParm = new String[2];
						valueParm[0] = "Product Code: " + productCode;
						valueParm[1] = "VAS Recording";
						errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("41018", valueParm)));

						return;
					}
				}
			}
		}

	}

	private void finODPenaltyRateValidation(FinScheduleData schdData) {
		List<ErrorDetail> errors = schdData.getErrorDetails();
		FinODPenaltyRate odPenalRate = schdData.getFinODPenaltyRate();

		FinanceType finType = schdData.getFinanceType();
		FinanceMain fm = schdData.getFinanceMain();

		String productCategory = fm.getProductCategory();

		if (odPenalRate == null) {
			return;
		}

		if (!finType.isApplyODPenalty()) {
			String[] valueParm = new String[2];
			valueParm[0] = "overdue";
			valueParm[1] = "loan type" + fm.getFinType();
			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90204", valueParm)));
			return;
		}

		BigDecimal odChargeAmtOrPerc = odPenalRate.getODChargeAmtOrPerc();
		BigDecimal odMaxWaiverPerc = odPenalRate.getODMaxWaiverPerc();

		if (odChargeAmtOrPerc == null) {
			odChargeAmtOrPerc = BigDecimal.ZERO;
			odPenalRate.setODChargeAmtOrPerc(BigDecimal.ZERO);
		}

		if (odMaxWaiverPerc == null) {
			odMaxWaiverPerc = BigDecimal.ZERO;
			odPenalRate.setODMaxWaiverPerc(BigDecimal.ZERO);
		}

		boolean oDPenalty = odPenalRate.isApplyODPenalty();
		boolean odIncGrcDays = odPenalRate.isODIncGrcDays();
		String odChargeType = odPenalRate.getODChargeType();

		boolean odAllowWaiver = odPenalRate.isODAllowWaiver();
		String odChargeCalOn = odPenalRate.getODChargeCalOn();
		if ((!oDPenalty) && (odIncGrcDays || StringUtils.isNotBlank(odChargeType)
				|| odChargeAmtOrPerc.compareTo(BigDecimal.ZERO) > 0 || odAllowWaiver)) {
			String[] valueParm = new String[1];
			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90315", valueParm)));
		} else {
			if ((StringUtils.isBlank(odChargeType) || odChargeAmtOrPerc.compareTo(BigDecimal.ZERO) <= 0)
					&& !(FinanceConstants.PRODUCT_ODFACILITY.equals(productCategory))) {
				String[] valueParm = new String[1];
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90314", valueParm)));
			}
			if (ChargeType.FLAT.equals(odChargeType) || ChargeType.FLAT_ON_PD_MTH.equals(odChargeType)) {
				odPenalRate.setODChargeCalOn("");
			}
			if ((StringUtils.isBlank(odChargeCalOn)) && (ChargeType.PERC_ONE_TIME.equals(odChargeType)
					|| ChargeType.PERC_ON_DUE_DAYS.equals(odChargeType)
					|| ChargeType.PERC_ON_EFF_DUE_DAYS.equals(odChargeType)
					|| ChargeType.PERC_ON_PD_MTH.equals(odChargeType))) {
				String[] valueParm = new String[2];
				valueParm[0] = "odChargeCalOn";
				valueParm[1] = "odChargeType" + ChargeType.PERC_ONE_TIME + "," + ChargeType.PERC_ON_DUE_DAYS + ","
						+ ChargeType.PERC_ON_EFF_DUE_DAYS + "," + ChargeType.PERC_ON_PD_MTH;
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90298", valueParm)));
			}
		}

		if (!(oDPenalty && odAllowWaiver)) {
			if ((odMaxWaiverPerc.compareTo(BigDecimal.ZERO) > 0)) {
				String[] valueParm = new String[2];
				valueParm[0] = "ODMaxWaiverPerc";
				valueParm[1] = "ODAllowWaiver is disabled";
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90329", valueParm)));
			}
		} else {
			if (odMaxWaiverPerc.compareTo(BigDecimal.ZERO) <= 0) {
				String[] valueParm = new String[2];
				valueParm[0] = "ODMaxWaiverPerc";
				valueParm[1] = "Zero";
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("91121", valueParm)));
			} else if (odMaxWaiverPerc.compareTo(new BigDecimal(100)) > 0) {
				String[] valueParm = new String[2];
				valueParm[0] = "ODChargeAmtOrPerc";
				valueParm[1] = "100";
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("30565", valueParm)));
			}
		}

		if (ChargeType.PERC_ONE_TIME.equals(odChargeType) || ChargeType.PERC_ON_DUE_DAYS.equals(odChargeType)
				|| ChargeType.PERC_ON_EFF_DUE_DAYS.equals(odChargeType)
				|| ChargeType.PERC_ON_PD_MTH.equals(odChargeType)) {
			BigDecimal totPerc = PennantApplicationUtil.formateAmount(odChargeAmtOrPerc, 2);
			if (totPerc.compareTo(new BigDecimal(100)) > 0) {
				String[] valueParm = new String[2];
				valueParm[0] = "ODChargeAmtOrPerc";
				valueParm[1] = "100";
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("30565", valueParm)));
			}
		}

		if (StringUtils.isNotBlank(odChargeType)) {
			List<ValueLabel> finODChargeType = PennantStaticListUtil.getODCChargeType();
			boolean finODChargeTypeSts = false;
			for (ValueLabel value : finODChargeType) {
				if (StringUtils.equals(value.getValue(), odChargeType)) {
					finODChargeTypeSts = true;
					break;
				}
			}

			if (!finODChargeTypeSts) {
				String[] valueParm = new String[2];
				valueParm[0] = odChargeType;
				valueParm[1] = ChargeType.FLAT + "," + ChargeType.FLAT_ON_PD_MTH + "," + ChargeType.PERC_ON_DUE_DAYS
						+ "," + ChargeType.PERC_ON_EFF_DUE_DAYS + "," + ChargeType.PERC_ON_PD_MTH + ","
						+ ChargeType.PERC_ONE_TIME;
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90316", valueParm)));
			}
		}

		if (StringUtils.isNotBlank(odChargeCalOn)) {
			List<ValueLabel> finODCCalculatedOn = PennantStaticListUtil.getODCCalculatedOn();
			boolean finODCCalculatedOnSts = false;
			for (ValueLabel value : finODCCalculatedOn) {
				if (StringUtils.equals(value.getValue(), odChargeCalOn)) {
					finODCCalculatedOnSts = true;
					break;
				}
			}
			if (!finODCCalculatedOnSts && (ChargeType.PERC_ONE_TIME.equals(odChargeType)
					|| ChargeType.PERC_ON_DUE_DAYS.equals(odChargeType)
					|| ChargeType.PERC_ON_EFF_DUE_DAYS.equals(odChargeType)
					|| ChargeType.PERC_ON_PD_MTH.equals(odChargeType))) {
				String[] valueParm = new String[2];
				valueParm[0] = odChargeCalOn;
				valueParm[1] = FinanceConstants.ODCALON_STOT + "," + FinanceConstants.ODCALON_SPFT + ","
						+ FinanceConstants.ODCALON_SPRI;
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90317", valueParm)));
			}

			if (!FinanceUtil.isMinimunODCChargeReq(odChargeType)) {
				if (FinanceConstants.ODCALON_INST.equals(odChargeCalOn)) {
					String[] valueParm = new String[2];
					valueParm[0] = " odChargeCalOn INST is allowed only when odChargeType is P or M ";
					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90505", valueParm)));
				}
			}
		}
	}

	/*
	 * ========================================================================= =======================================
	 * VALIDATE Finance Details =========================================================================
	 * =======================================
	 */
	public FinScheduleData financeDetailValidation(String vldGroup, FinanceDetail fd, boolean apiFlag) {

		List<ErrorDetail> errorDetails = new ArrayList<ErrorDetail>();
		FinanceMain finMain = fd.getFinScheduleData().getFinanceMain();
		FinScheduleData schdData = fd.getFinScheduleData();
		boolean isCreateLoan = false;

		if (StringUtils.equals(vldGroup, PennantConstants.VLD_CRT_LOAN)) {
			isCreateLoan = true;
		}

		if (StringUtils.equals(vldGroup, PennantConstants.VLD_UPD_LOAN)) {
			errorDetails = validateUpdateFinance(fd);
			if (!errorDetails.isEmpty()) {
				schdData.setErrorDetails(errorDetails);
				return schdData;
			}
			return new FinScheduleData();
		}

		if (!fd.isStp()) {
			/*
			 * if(StringUtils.isBlank(financeDetail.getProcessStage())){ String[] valueParm = new String[1];
			 * valueParm[0] = "ProcessStage"; errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90502",
			 * valueParm))); finScheduleData.setErrorDetails(errorDetails); return finScheduleData; }
			 */
			if (fd.getFinScheduleData().getFinanceMain().isQuickDisb()) {
				String[] valueParm = new String[2];
				valueParm[0] = "QuickDisb";
				valueParm[1] = "stp";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90298", valueParm)));
				schdData.setErrorDetails(errorDetails);
				return schdData;
			}
		}

		if (isCreateLoan && fd.isStp()) {
			if (fd.getFinScheduleData().getFinanceMain().isLegalRequired()) {
				String[] valueParm = new String[2];
				valueParm[0] = "LegalRequired";
				valueParm[1] = "stp Process";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90329", valueParm)));
				schdData.setErrorDetails(errorDetails);
				return schdData;
			}
		}

		if (isCreateLoan && !fd.isStp()) {
			List<LegalDetail> legalDetails = fd.getLegalDetailsList();
			if (!finMain.isLegalRequired()) {
				if (legalDetails != null && !CollectionUtils.isEmpty(legalDetails)) {
					String[] valueParm = new String[2];
					valueParm[0] = "Legal Details";
					valueParm[1] = "LegalRequired";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90298", valueParm)));
					schdData.setErrorDetails(errorDetails);
					return schdData;
				}
			}

			// validate Legal Details
			errorDetails = doLegalDetailsValidation(fd);
			if (!CollectionUtils.isEmpty(errorDetails)) {
				schdData.setErrorDetails(errorDetails);
				return schdData;
			}

			if (legalDetails != null && !CollectionUtils.isEmpty(legalDetails)) {
				for (LegalDetail legalDetail : legalDetails) {

					// validate applicant details
					if (legalDetail.getApplicantDetailList() != null
							&& !CollectionUtils.isEmpty(legalDetail.getApplicantDetailList())) {
						errorDetails = validateLegalApplicant(legalDetail.getApplicantDetailList());
						if (!CollectionUtils.isEmpty(errorDetails)) {
							schdData.setErrorDetails(errorDetails);
							return schdData;
						}
					}

					// validate Legal Property Details
					errorDetails = validatePropertyDetails(legalDetail.getPropertyDetailList());
					if (!CollectionUtils.isEmpty(errorDetails)) {
						schdData.setErrorDetails(errorDetails);
						return schdData;
					}

					// validate Legal Document Details
					errorDetails = validateLegalDocument(legalDetail.getDocumentList());
					if (!CollectionUtils.isEmpty(errorDetails)) {
						schdData.setErrorDetails(errorDetails);
						return schdData;
					}

					// validate Legal Query Details
					if (legalDetail.getQueryDetail() != null) {
						errorDetails = validateLegalQueryDetail(legalDetail.getQueryDetail());
						if (!CollectionUtils.isEmpty(errorDetails)) {
							schdData.setErrorDetails(errorDetails);
							return schdData;
						}
					}
				}

			}

		}

		// validate FinReference
		String financeReference = null;
		if (fd.getFinScheduleData().getFinReference() != null) {
			financeReference = fd.getFinScheduleData().getFinReference();
		} else {
			financeReference = finMain.getFinReference();
		}

		// Temp comment

		ErrorDetail error = validateFinReference(financeReference, schdData, vldGroup);
		if (error != null) {
			errorDetails.add(error);
		}

		if (StringUtils.isNotEmpty(financeReference)) {
			String custCIF = finReceiptHeaderDAO.getCustCIF(financeReference);

			if (custCIF != null && !custCIF.equals(finMain.getLovDescCustCIF())) {
				String[] valueParm = new String[2];
				valueParm[0] = "Cif: " + finMain.getLovDescCustCIF() + " with External Reference: " + financeReference;
				valueParm[1] = "Combination is Invalid";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("21005", valueParm)));
				schdData.setErrorDetails(errorDetails);
				return schdData;
			}
		}

		// Temp comment
		// validate external reference
		if (StringUtils.isNotEmpty(fd.getFinScheduleData().getExternalReference())) {
			boolean isExtAssigned = finReceiptHeaderDAO
					.isExtRefAssigned(fd.getFinScheduleData().getExternalReference());
			if (isExtAssigned) {
				String[] valueParm = new String[1];
				valueParm[0] = " External Reference Already Assigned to Finance ";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("30550", valueParm)));
				schdData.setErrorDetails(errorDetails);
				return schdData;
			}
		}

		// FIXME: PV 28AUG19: Already taken care in defaulting?
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

		FinanceType financeType = schdData.getFinanceType();
		schdData.setFinanceType(financeType);
		if (finMain.getFinContractDate() == null) {
			finMain.setFinContractDate(financeType.getStartDate());
		} else {
			if (finMain.getFinContractDate().compareTo(finMain.getFinStartDate()) > 0) {
				String[] valueParm = new String[2];
				valueParm[0] = DateUtil.format(finMain.getFinContractDate(), PennantConstants.XMLDateFormat);
				valueParm[1] = DateUtil.format(finMain.getFinStartDate(), PennantConstants.XMLDateFormat);
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

		if (financeType.isFinCollateralReq() && fd.isStp()) {
			if (fd.getCollateralAssignmentList() == null || fd.getCollateralAssignmentList().isEmpty()) {
				String[] valueParm = new String[1];
				valueParm[0] = "Collateral";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
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
				valueParm[0] = "dsaCode";
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
			Branch branch = branchService.getBranch(finMain.getFinBranch());
			if (branch == null) {
				String[] valueParm = new String[1];
				valueParm[0] = finMain.getFinBranch();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90129", valueParm)));
			}
		}

		if (!StringUtils.contains(financeType.getAlwdRpyMethods(), finMain.getFinRepayMethod())) {
			String[] valueParm = new String[1];
			valueParm[0] = finMain.getFinRepayMethod();
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("RU0040", valueParm)));
		}

		schdData.setErrorDetails(errorDetails);

		// Validate Repayment Method
		if (isCreateLoan) {
			String repayMethod = finMain.getFinRepayMethod();

			// finRepay method
			if (StringUtils.isNotBlank(repayMethod)) {
				List<ValueLabel> repayMethods = MandateUtil.getRepayMethods();
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
				schdData.setErrorDetails(errorDetails);
				return schdData;
			}

			// Disbursement is not mandatory for Over Draft schedule
			if (!fd.getFinScheduleData().getFinanceMain().getProductCategory()
					.equals(FinanceConstants.PRODUCT_ODFACILITY)) {
				errorDetails = disbursementValidation(fd);
				if (!errorDetails.isEmpty()) {
					schdData.setErrorDetails(errorDetails);
					return schdData;
				}
			}

			error = mandateService.validate(fd, PennantConstants.VLD_CRT_LOAN);
			if (error != null) {
				errorDetails.add(error);
				schdData.setErrorDetails(errorDetails);
				return schdData;
			}
			if (CollectionUtils.isNotEmpty(fd.getCovenants()))
				errorDetails = covenantValidation(fd.getFinScheduleData().getFinanceMain(), fd.getCovenants(), "LOS");
			if (!errorDetails.isEmpty()) {
				schdData.setErrorDetails(errorDetails);
				return schdData;
			}

			errorDetails = doCovenantValidation(fd);
			if (!errorDetails.isEmpty()) {
				schdData.setErrorDetails(errorDetails);
				return schdData;
			}

			errorDetails = documentValidation(fd);
			if (!errorDetails.isEmpty()) {
				schdData.setErrorDetails(errorDetails);
				return schdData;
			}

			if (fd.getChequeHeader() != null)
				error = chequeHeaderService.chequeValidation(fd, PennantConstants.VLD_CRT_LOAN, "");
			if (error != null) {
				errorDetails.add(error);
				schdData.setErrorDetails(errorDetails);
				return schdData;
			}

			errorDetails = jointAccountDetailsValidation(fd);
			if (!errorDetails.isEmpty()) {
				schdData.setErrorDetails(errorDetails);
				return schdData;
			}

			errorDetails = gurantorsDetailValidation(fd);
			if (!errorDetails.isEmpty()) {
				schdData.setErrorDetails(errorDetails);
				return schdData;
			}
			errorDetails = finFlagsDetailValidation(fd);
			if (!errorDetails.isEmpty()) {
				schdData.setErrorDetails(errorDetails);
				return schdData;
			}
			errorDetails = finCollateralValidation(fd);
			if (!errorDetails.isEmpty()) {
				schdData.setErrorDetails(errorDetails);
				return schdData;
			}

			errorDetails = finTaxDetailValidation(fd);
			if (!errorDetails.isEmpty()) {
				schdData.setErrorDetails(errorDetails);
				return schdData;
			}

			if (StringUtils.equalsIgnoreCase(PennantConstants.YES,
					SysParamUtil.getValueAsString(SMTParameterConstants.PSL_DATA_REQUIRED))) {
				if (fd.getPslDetail() == null) {
					String[] valueParm = new String[1];
					valueParm[0] = "PSL Details";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
				}

				if (!errorDetails.isEmpty()) {
					schdData.setErrorDetails(errorDetails);
					return schdData;
				}

				if (fd.getPslDetail() != null) {
					errorDetails = pslValidation(fd);
					if (!CollectionUtils.isEmpty(errorDetails)) {
						schdData.setErrorDetails(errorDetails);
						return schdData;
					}
				}
			}
			// ExtendedFieldDetails Validation
			String subModule = fd.getFinScheduleData().getFinanceMain().getFinCategory();
			// ### 02-05-2018-Start- story #334 Extended fields for loan
			// servicing
			if ((fd.isStp() || (!fd.isStp() && CollectionUtils.isNotEmpty(fd.getExtendedDetails())))) {
				errorDetails = extendedFieldDetailsService.validateExtendedFieldDetails(fd.getExtendedDetails(),
						ExtendedFieldConstants.MODULE_LOAN, subModule, FinServiceEvent.ORG);
			}

			// ### 02-05-2018-END
			if (!errorDetails.isEmpty()) {
				schdData.setErrorDetails(errorDetails);
				return schdData;
			}
		}

		return schdData;

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
	 * @param fd
	 * 
	 * @return
	 */
	private List<ErrorDetail> validateUpdateFinance(FinanceDetail fd) {
		List<ErrorDetail> errorDetails = new ArrayList<>();

		String type = TableType.TEMP_TAB.getSuffix();
		Long finID = financeMainDAO.getFinID(fd.getFinReference(), TableType.TEMP_TAB);
		if (finID == null) {
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90409", null)));
			return errorDetails;
		}

		// fetch Finance Schedule details
		FinScheduleData schdData = financeDetailService.getFinSchDataById(finID, type, false);
		fd.setFinScheduleData(schdData);

		// validate disbursement details
		if (fd.getAdvancePaymentsList() != null && !fd.getAdvancePaymentsList().isEmpty()) {
			errorDetails = disbursementValidation(fd);
			if (!errorDetails.isEmpty()) {
				return errorDetails;
			}
		}

		// validate Mandate details
		if (fd.getMandate() != null) {
			ErrorDetail error = mandateService.validate(fd, PennantConstants.VLD_UPD_LOAN);
			if (error != null) {
				errorDetails.add(error);
				return errorDetails;
			}
		}

		// Extended Field Details Validation
		if ((fd.isStp()) || (fd.getExtendedDetails() != null && !fd.getExtendedDetails().isEmpty() && !fd.isStp())) {
			String subModule = fd.getFinScheduleData().getFinanceMain().getFinCategory();
			errorDetails = extendedFieldDetailsService.validateExtendedFieldDetails(fd.getExtendedDetails(),
					ExtendedFieldConstants.MODULE_LOAN, subModule, FinServiceEvent.ORG);
			if (!errorDetails.isEmpty()) {
				return errorDetails;
			}
		}

		// Finance document details Validation
		if (fd.getDocumentDetailsList() != null && !fd.getDocumentDetailsList().isEmpty()) {
			errorDetails = documentService.validateFinanceDocuments(fd);
			if (!errorDetails.isEmpty()) {
				return errorDetails;
			}
		}

		// validate coApplicants details
		if (fd.getJointAccountDetailList() != null && !fd.getJointAccountDetailList().isEmpty()) {
			errorDetails = jointAccountDetailsValidation(fd);
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

							if (!colltype.contains(setupDetails.getCollateralType())) {
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
							AuditDetail auditDetail = collateralSetupService.doValidations(setupDetails, "create",
									false);

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

					if (assignPerc.compareTo(new BigDecimal(100)) > 0) {
						String[] valueParm = new String[2];
						valueParm[0] = "assignPerc";
						valueParm[1] = "100";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("30565", valueParm)));
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
								valueParm[0] = collateralSetup.getCollateralRef();
								valueParm[1] = financeDetail.getFinScheduleData().getFinanceMain().getLovDescCustCIF();
								errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90249", valueParm)));
								return errorDetails;
							}

						}

						BigDecimal totAssignedPerc = collateralSetupService
								.getAssignedPerc(collateralSetup.getCollateralRef(), "");// TODO:Add
																							// reference

						if (totAssignedPerc.add(assignPerc).compareTo(new BigDecimal(100)) > 0) {
							String[] valueParm = new String[2];
							valueParm[0] = "Available assign Percentage "
									+ (new BigDecimal(100).subtract(totAssignedPerc)) + " for "
									+ collateralAssignment.getCollateralRef();
							valueParm[1] = "given Collateral Percentage " + assignPerc;
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("65012", valueParm)));

							return errorDetails;
						}

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
		List<JointAccountDetail> jointAccountDetails = financeDetail.getJointAccountDetailList();
		if (jointAccountDetails != null) {
			for (JointAccountDetail jointAccDetail : jointAccountDetails) {
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
				for (JointAccountDetail detail : jointAccountDetails) {
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

	private List<ErrorDetail> documentValidation(FinanceDetail fd) {
		List<ErrorDetail> errors = new ArrayList<ErrorDetail>();

		List<DocumentDetails> documents = fd.getDocumentDetailsList();

		if (documents == null) {
			return errors;
		}

		AuditDetail auditDetails = null;
		for (DocumentDetails dd : documents) {
			// validate Dates
			if (dd.getCustDocIssuedOn() != null && dd.getCustDocExpDate() != null) {
				if (dd.getCustDocIssuedOn().compareTo(dd.getCustDocExpDate()) > 0) {
					String[] valueParm = new String[2];
					valueParm[0] = "custDocExpDate: "
							+ DateUtil.format(dd.getCustDocExpDate(), PennantConstants.XMLDateFormat);
					valueParm[1] = "custDocIssuedOn: "
							+ DateUtil.format(dd.getCustDocIssuedOn(), PennantConstants.XMLDateFormat);
					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("65030", valueParm)));
					return errors;
				}
			}

			DocumentType docType = documentTypeService.getDocumentTypeById(dd.getDocCategory());
			if (docType == null) {
				String[] valueParm = new String[1];
				valueParm[0] = dd.getDocCategory();
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90401", valueParm)));
				return errors;
			}

			if (DocumentCategories.CUSTOMER.getKey().equals(docType.getCategoryCode())) {
				CustomerDocument cd = new CustomerDocument();
				cd.setCustDocCategory(dd.getDocCategory());
				cd.setCustDocName(dd.getDocName());
				cd.setCustDocIssuedOn(dd.getCustDocIssuedOn());
				cd.setCustDocExpDate(dd.getCustDocExpDate());
				cd.setCustDocTitle(dd.getCustDocTitle());
				cd.setCustDocIssuedCountry(dd.getCustDocIssuedCountry());
				cd.setCustDocSysName(dd.getCustDocSysName());
				cd.setCustDocIssuedOn(dd.getCustDocIssuedOn());
				cd.setCustDocExpDate(dd.getCustDocExpDate());
				cd.setDocUri(dd.getDocUri());
				cd.setCustDocImage(dd.getDocImage());
				cd.setCustDocType(dd.getDoctype());
				Customer cust = fd.getCustomerDetails().getCustomer();

				if (cust == null || cust.getCustCtgCode() == null) {
					FinScheduleData schdData = fd.getFinScheduleData();
					FinanceMain fm = schdData.getFinanceMain();
					cust = customerDAO.getCustomerByCIF(fm.getLovDescCustCIF(), "");
				}

				auditDetails = customerDocumentService.validateCustomerDocuments(cd, cust);
			}

			// validate finance documents
			if (!(DocumentCategories.CUSTOMER.getKey().equals(docType.getCategoryCode()))
					&& docType.isDocIsMandatory()) {
				if (StringUtils.isBlank(dd.getDocUri())) {
					if (dd.getDocImage() == null || dd.getDocImage().length <= 0) {
						String[] valueParm = new String[2];
						valueParm[0] = "docContent";
						valueParm[1] = "docRefId";
						errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90123", valueParm)));
					}
				}
				if (StringUtils.isBlank(dd.getDocName())) {
					String[] valueParm = new String[1];
					valueParm[0] = "docName";
					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
				}
				if (StringUtils.isBlank(dd.getDoctype())) {
					String[] valueParm = new String[1];
					valueParm[0] = "docFormat";
					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
				} else if (!StringUtils.equalsIgnoreCase(dd.getDoctype(), "jpg")
						&& !StringUtils.equalsIgnoreCase(dd.getDoctype(), "png")
						&& !StringUtils.equalsIgnoreCase(dd.getDoctype(), "pdf")) {
					String[] valueParm = new String[1];
					valueParm[0] = "docFormat, Available formats are jpg,png,PDF";
					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90122", valueParm)));
				}
			}

			if (StringUtils.equals(dd.getDocCategory(), "03")) {
				Pattern pattern = Pattern.compile("^[A-Za-z]{5}\\d{4}[A-Za-z]{1}");
				if (dd.getCustDocTitle() != null) {
					Matcher matcher = pattern.matcher(dd.getCustDocTitle());
					if (matcher.find() == false) {
						String[] valueParm = new String[0];
						errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90251", valueParm)));
					}
				}
			}

			String doctype = dd.getDoctype();
			if ("JPG".equalsIgnoreCase(doctype) || "PNG".equalsIgnoreCase(doctype) || "JPEG".equalsIgnoreCase(doctype)
					|| "JFIF".equalsIgnoreCase(doctype)) {
				dd.setDoctype(PennantConstants.DOC_TYPE_IMAGE);
			}
		}

		if (auditDetails != null && auditDetails.getErrorDetails() != null
				&& !auditDetails.getErrorDetails().isEmpty()) {
			return auditDetails.getErrorDetails();
		}
		return errors;
	}

	private List<ErrorDetail> doCovenantValidation(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);

		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		String finEvent = FinServiceEvent.ORG;
		FinanceWorkFlow financeWorkFlow = financeWorkFlowService.getApprovedFinanceWorkFlowById(
				financeMain.getFinType(), finEvent, PennantConstants.WORFLOW_MODULE_FINANCE);
		String rolesName = "";
		if (financeWorkFlow != null && financeWorkFlow.getLovDescWorkFlowRolesName() != null
				&& !financeWorkFlow.getLovDescWorkFlowRolesName().isEmpty()) {
			rolesName = financeWorkFlow.getLovDescWorkFlowRolesName();
		}

		List<ErrorDetail> errorDetails = new ArrayList<ErrorDetail>();

		if (StringUtils.isBlank(rolesName)) {
			String[] valueParm = new String[1];
			valueParm[0] = "Invalid Workflow Configuration";
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("92021", valueParm)));
			return errorDetails;
		}

		Set<String> set = new HashSet<>();
		List<FinCovenantType> covenantTypeList = financeDetail.getCovenantTypeList();
		if (CollectionUtils.isNotEmpty(covenantTypeList)) {
			for (FinCovenantType finCovenantType : covenantTypeList) {

				String covenantType = finCovenantType.getCovenantType();
				if (StringUtils.isBlank(covenantType)) {
					String[] valueParm = new String[1];
					valueParm[0] = "covenantType";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
					return errorDetails;
				} else {
					boolean isExists = false;
					if (set.contains(covenantType)) {
						isExists = true;
					} else {
						set.add(covenantType);
					}
					if (isExists) {
						String[] valueParm = new String[1];
						valueParm[0] = "covenantType: " + covenantType;
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90273", valueParm)));
						return errorDetails;
					}
				}
				DocumentType documentTypeByCode = documentTypeDAO.getDocumentTypeById(covenantType, "_View");
				if (documentTypeByCode == null) {
					String[] valueParm = new String[2];
					valueParm[0] = "covenantType";
					valueParm[1] = covenantType;
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90224", valueParm)));
					return errorDetails;
				} else {
					CustomerDocument custdocuments = customerDocumentService
							.getApprovedCustomerDocumentById(financeMain.getCustID(), covenantType);
					if (custdocuments != null) {
						if (StringUtils.equals(covenantType, custdocuments.getCustDocCategory())) {
							String[] valueParm = new String[2];
							valueParm[0] = "CustomerDocument" + covenantType;
							valueParm[1] = covenantType;
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("41018", valueParm)));
							return errorDetails;
						}
					}
				}

				if (StringUtils.isNotBlank(finCovenantType.getMandRole())) {
					if ((finCovenantType.isAlwPostpone() || finCovenantType.isAlwOtc()
							|| finCovenantType.isAlwWaiver())) {
						String[] valueParm = new String[2];
						valueParm[0] = "alwPostpone or alwOtc or alwWaiver";
						valueParm[1] = "mandRole";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("API002", valueParm)));
						return errorDetails;
					}
				}
				if (finCovenantType.isAlwWaiver()) {
					if (StringUtils.isNotBlank(finCovenantType.getMandRole()) || finCovenantType.isAlwPostpone()
							|| finCovenantType.isAlwOtc()) {
						String[] valueParm = new String[2];
						valueParm[0] = "alwPostpone or alwOtc";
						valueParm[1] = "alwWaiver";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("API002", valueParm)));
						return errorDetails;
					}
				}
				if (finCovenantType.isAlwPostpone() && finCovenantType.isAlwOtc()) {
					String[] valueParm = new String[2];
					valueParm[0] = "alwPostpone";
					valueParm[1] = "alwOtc";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("30566", valueParm)));
					return errorDetails;
				}

				if ((!finCovenantType.isAlwPostpone() && !finCovenantType.isAlwOtc()
						&& !finCovenantType.isAlwWaiver())) {
					if (StringUtils.isBlank(finCovenantType.getMandRole())) {
						String[] valueParm = new String[1];
						valueParm[0] = "mandRole";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
						return errorDetails;
					}

					boolean isRoleExts = false;
					if (StringUtils.contains(rolesName, ";")) {
						String[] roles = rolesName.split(";");
						for (String workFlowRoles : roles) {
							if (StringUtils.equalsIgnoreCase(workFlowRoles, finCovenantType.getMandRole())) {
								isRoleExts = true;
								break;
							}
						}
					}
					if (!isRoleExts) {
						String[] valueParm = new String[2];
						valueParm[0] = "mandRole";
						valueParm[1] = finCovenantType.getMandRole();
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90224", valueParm)));
						return errorDetails;
					}
					if (financeDetail.isStp()) {
						List<DocumentDetails> documentDetailsList = financeDetail.getDocumentDetailsList();
						boolean documentExist = isCovenantDocumentExist(documentDetailsList, covenantType);
						if (!documentExist) {
							String[] valueParm = new String[1];
							valueParm[0] = covenantType;
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("CVN001", valueParm)));
							return errorDetails;
						}
						for (DocumentDetails detail : documentDetailsList) {
							if (!StringUtils.equals(covenantType, detail.getDocCategory())) {
								continue;
							}
							if (!(DocumentCategories.CUSTOMER.getKey().equals(covenantType))) {
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
							}
						}
					}
				} else {
					finCovenantType.setMandRole("");
				}

				if (finCovenantType.isAlwPostpone()) {
					if (finCovenantType.getReceivableDate() == null) {
						String[] valueParm = new String[1];
						valueParm[0] = "receivableDate";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
						return errorDetails;
					} else {
						if (finCovenantType.getReceivableDate() != null) {
							java.util.Date appDate = SysParamUtil.getAppDate();
							Date allowedDate = DateUtil.addDays(appDate,
									+SysParamUtil.getValueAsInt("FUTUREDAYS_COV_RECEIVED_DATE"));
							if (DateUtil.compare(finCovenantType.getReceivableDate(), appDate) == -1) {
								String[] valueParm = new String[2];
								valueParm[0] = "receivableDate";
								valueParm[1] = String.valueOf(appDate);
								errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("65030", "", valueParm)));
							} else if (DateUtil.compare(finCovenantType.getReceivableDate(), allowedDate) == 1) {
								String[] valueParm = new String[2];
								valueParm[0] = "receivableDate";
								valueParm[1] = String.valueOf(allowedDate);
								errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("65029", "", valueParm)));
							}
						}
					}
				}
			}
		}
		logger.debug(Literal.LEAVING);
		return errorDetails;
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
						valueParm[1] = DateUtil.formatToLongDate(financeMain.getFinStartDate());
						valueParm[2] = DateUtil.formatToLongDate(financeMain.getCalMaturity());
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90318", "", valueParm)));
						return errorDetails;
					}
				}

				if (StringUtils.isNotEmpty(advPayment.getLei())) {
					if (advPayment.getLei().length() > 50) {
						String[] valueParm = new String[4];
						valueParm[0] = "LEI ";
						valueParm[1] = "length should be ";
						valueParm[2] = "less than ";
						valueParm[3] = "or equal to 50. ";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("21005", valueParm)));
					}
					Pattern pattern = Pattern.compile(
							PennantRegularExpressions.getRegexMapper(PennantRegularExpressions.REGEX_ALPHANUM));

					Matcher matcher = pattern.matcher(advPayment.getLei());

					if (!matcher.matches()) {
						String[] valueParm = new String[1];
						valueParm[0] = "Lei";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90347", "", valueParm), "EN"));
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
						Date appDate = SysParamUtil.getAppDate();
						Date todate = DateUtil.addMonths(appDate, 6);
						if (advPayment.getValueDate().compareTo(appDate) < 0
								|| advPayment.getValueDate().after(todate)) {
							String[] valueParm = new String[3];
							valueParm[0] = "disbursement ValueDate";
							valueParm[1] = DateUtil.formatToLongDate(appDate);
							valueParm[2] = DateUtil.formatToLongDate(todate);
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90318", "", valueParm)));
						}
					}
				} else if (StringUtils.equals(advPayment.getPaymentType(), DisbursementConstants.PAYMENT_TYPE_IMPS)
						|| StringUtils.equals(advPayment.getPaymentType(), DisbursementConstants.PAYMENT_TYPE_NEFT)
						|| StringUtils.equals(advPayment.getPaymentType(), DisbursementConstants.PAYMENT_TYPE_RTGS)
						|| StringUtils.equals(advPayment.getPaymentType(), DisbursementConstants.PAYMENT_TYPE_IFT)) {

					// Ifsc, bank or branch codes
					String ifsc = advPayment.getiFSC();
					String micr = advPayment.getMicr();
					String bankCode = advPayment.getBranchBankCode();
					String branchCode = advPayment.getBranchCode();

					BankBranch bankBranch = bankBranchService.getBankBranch(ifsc, micr, bankCode, branchCode);

					if (bankBranch.getError() != null) {
						errorDetails.add(bankBranch.getError());
						return errorDetails;
					}

					advPayment.setBankCode(bankBranch.getBankCode());

					// Account number
					if (StringUtils.isBlank(advPayment.getBeneficiaryAccNo())) {
						String[] valueParm = new String[2];
						valueParm[0] = "accountNo";
						valueParm[1] = advPayment.getBeneficiaryAccNo();
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90217", valueParm)));
					} else { // validate AccNumber length
						BankDetail bankdetail = bankDetailService.getAccNoLengthByCode(advPayment.getBankCode());
						int length = advPayment.getBeneficiaryAccNo().length();
						if (bankdetail != null) {
							int accNoLength = bankdetail.getAccNoLength();
							int accMinLength = bankdetail.getMinAccNoLength();

							if (length < accMinLength || length > accNoLength) {
								if (accMinLength == accNoLength) {
									String[] valueParm = new String[2];
									valueParm[0] = "accountNo ";
									valueParm[1] = String.valueOf(accNoLength) + " characters";
									errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("30570", valueParm)));
									return errorDetails;
								} else {
									String[] valueParm = new String[3];
									valueParm[0] = "accountNo ";
									valueParm[1] = String.valueOf(accMinLength) + " characters";
									valueParm[2] = String.valueOf(accNoLength) + " characters";
									errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("BNK001", valueParm)));

									return errorDetails;
								}

							}
						}
					}
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

					// Validating Vas Disb Instructions
					if (PennantConstants.FINSOURCE_ID_API.equals(financeMain.getFinSourceID())) {
						if (DisbursementConstants.PAYMENT_DETAIL_VAS.equals(advPayment.getPaymentDetail())) {
							// Product code is mandatory when disbParty is VAS
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
											// Validating VAS Disbursement Amount and configured VAS Amount equal r not
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
							// Validating duplicate product codes in Disbursement Instruction
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

	private void nonFinanceValidation(String vldGroup, FinScheduleData schdData, FinanceDetail fd) {
		List<ErrorDetail> errors = schdData.getErrorDetails();

		FinanceMain fm = schdData.getFinanceMain();

		String finCcy = fm.getFinCcy();
		String swiftBranchCode = fm.getSwiftBranchCode();
		String finBranch = fm.getFinBranch();
		String repayMethod = fm.getFinRepayMethod();
		String finPurpose = fm.getFinPurpose();

		boolean isCreateLoan = PennantConstants.VLD_CRT_LOAN.equals(vldGroup);

		String appCurrency = SysParamUtil.getAppCurrency();

		if (!appCurrency.equals(finCcy)) {
			// FIXME :: move to cache
			if (!currencyDAO.isExistsCurrencyCode(finCcy)) {
				String[] valueParm = new String[1];
				valueParm[0] = finCcy;
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90120", valueParm)));
				return;
			}
		}

		if (StringUtils.isBlank(swiftBranchCode) && (isCreateLoan || StringUtils.isNotBlank(finBranch))) {
			Branch branch = branchService.getBranch(finBranch);

			if (branch == null) {
				String[] valueParm = new String[1];
				valueParm[0] = finBranch;
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90129", valueParm)));
			}
		}

		if (!isCreateLoan) {
			return;
		}

		if (StringUtils.isNotBlank(repayMethod)) {
			List<ValueLabel> repayMethods = MandateUtil.getRepayMethods();
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
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90307", valueParm)));
			}
		}

		if (StringUtils.isNotBlank(finPurpose)) {
			// FIXME :: move to cache
			LoanPurpose loanPurpose = loanPurposeDAO.getLoanPurposeById(finPurpose, "");
			if (loanPurpose == null || !loanPurpose.isLoanPurposeIsActive()) {
				String[] valueParm = new String[1];
				valueParm[0] = finPurpose;
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90701", valueParm)));
			}
		}
	}

	/*
	 * ========================================================================= =======================================
	 * VALIDATE BASIC FINANCE DATA =========================================================================
	 * =======================================
	 */

	private void basicValidation(FinScheduleData schdData, FinanceDetail fd, boolean isEMI) {
		List<ErrorDetail> errors = schdData.getErrorDetails();

		Date appDate = SysParamUtil.getAppDate();

		FinanceMain fm = schdData.getFinanceMain();

		FinanceType finType = schdData.getFinanceType();

		String applicationNo = fm.getApplicationNo();
		Date finStartDate = fm.getFinStartDate();
		String productCategory = fm.getProductCategory();
		BigDecimal finAmount = fm.getFinAmount();
		BigDecimal finAssetValue = fm.getFinAssetValue();
		BigDecimal downPayBank = fm.getDownPayBank();
		BigDecimal downPaySupl = fm.getDownPaySupl();
		BigDecimal downPayment = downPayBank.add(downPaySupl);

		BigDecimal zeroAmount = BigDecimal.ZERO;

		if (!ImplementationConstants.CLIENT_NFL) {
			if (StringUtils.isNotBlank(applicationNo) && applicationNo.length() > LengthConstants.LEN_APP_NO) {
				String[] valueParm = new String[2];
				valueParm[0] = "Application Number";
				valueParm[1] = LengthConstants.LEN_APP_NO + " characters";
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("30568", valueParm)));
			}
		}

		if (!isEMI) {
			int backDatedDays = SysParamUtil.getValueAsInt(SMTParameterConstants.LOAN_START_DATE_BACK_DAYS);
			Date minReqFinStartDate = DateUtil.addDays(appDate, -backDatedDays);

			if (finStartDate.compareTo(minReqFinStartDate) < 0) {
				String[] valueParm = new String[2];
				valueParm[0] = String.valueOf(backDatedDays);
				valueParm[1] = DateUtil.format(DateUtil.addDays(minReqFinStartDate, 0), PennantConstants.XMLDateFormat);

				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90134", valueParm)));
			}
		}

		Date maxReqFinStartDate = DateUtil.addDays(appDate,
				+SysParamUtil.getValueAsInt(SMTParameterConstants.LOAN_START_DATE_FUTURE_DAYS));

		if (finStartDate.compareTo(maxReqFinStartDate) > 0) {
			String[] valueParm = new String[2];
			valueParm[0] = "Loan Start Date";
			valueParm[1] = DateUtil.format(DateUtil.addDays(maxReqFinStartDate, 1), PennantConstants.XMLDateFormat);

			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("65027", valueParm)));
		}

		if (!FinanceConstants.PRODUCT_ODFACILITY.equals(productCategory) && finAmount.compareTo(BigDecimal.ZERO) <= 0) {
			String[] valueParm = new String[1];
			valueParm[0] = String.valueOf(finAmount.doubleValue());

			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90127", valueParm)));
		}

		if (finType.isAlwMaxDisbCheckReq() && finAssetValue.compareTo(zeroAmount) <= 0) {
			String[] valueParm = new String[1];
			valueParm[0] = "finAssetValue";

			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
		}

		if (finAssetValue.compareTo(BigDecimal.ZERO) > 0 && finAmount.compareTo(finAssetValue) > 0) {
			String[] valueParm = new String[2];
			valueParm[0] = "finAmount";
			valueParm[1] = "finAssetValue";

			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90220", valueParm)));
		}

		fm.setDownPayment(downPayment);

		if (finType.isFinIsDwPayRequired() && fd != null) {
			setDownpaymentRulePercentage(finType, fm, fd);
			BigDecimal reqDwnPay = getPercentageValue(finAmount, fm.getMinDownPayPerc());

			if (downPayment.compareTo(finAmount) >= 0) {
				String[] valueParm = new String[3];
				valueParm[0] = "Sum of Bank & Supplier Down payments";
				valueParm[1] = String.valueOf(reqDwnPay);
				valueParm[2] = String.valueOf(finAmount);

				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("30567", valueParm)));
			}

			if (downPayment.compareTo(reqDwnPay) < 0) {
				String[] valueParm = new String[2];
				valueParm[0] = "Sum of Bank & Supplier Down payments";
				valueParm[1] = String.valueOf(reqDwnPay);
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("30569", valueParm)));
			}

			BigDecimal feeResult = financeDetailService.getDownPayRuleAmount(finType, fm);

			if (downPayment.compareTo(feeResult) < 0) {
				String[] valueParm = new String[3];
				valueParm[0] = "Down pay Amount";
				valueParm[1] = feeResult.toString();
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("65012", valueParm)));
			}

		} else if (downPayBank.compareTo(BigDecimal.ZERO) != 0 || downPaySupl.compareTo(BigDecimal.ZERO) != 0) {
			String[] valueParm = new String[3];
			valueParm[0] = "Down pay bank";
			valueParm[1] = "Supplier";
			valueParm[2] = fm.getFinType();
			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90203", valueParm)));
		}

		String tdsType = fm.getTdsType();

		if (fm.isTDSApplicable()) {

			if (!finType.isTdsApplicable()) {
				String[] valueParm = new String[3];
				valueParm[0] = "tds";
				valueParm[1] = finType.getFinType();

				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90329", valueParm)));
			}

			String loanTypeTdsType = finType.getTdsType();
			if (StringUtils.isNotBlank(tdsType) && !"#".equals(loanTypeTdsType)) {
				if (!PennantConstants.TDS_USER_SELECTION.equals(loanTypeTdsType) && !loanTypeTdsType.equals(tdsType)) {
					String[] valueParm = new String[2];
					valueParm[0] = "tdsType";
					valueParm[1] = loanTypeTdsType;

					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90337", valueParm)));
				} else if (!(PennantConstants.TDS_AUTO.equalsIgnoreCase(tdsType)
						|| PennantConstants.TDS_MANUAL.equalsIgnoreCase(tdsType))) {
					String[] valueParm = new String[2];
					valueParm[0] = "tdsType";
					valueParm[1] = PennantConstants.TDS_AUTO + " , " + PennantConstants.TDS_MANUAL;

					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90337", valueParm)));
				}
			} else if (StringUtils.isBlank(tdsType) && !"#".equals(loanTypeTdsType)) {
				String[] valueParm = new String[1];
				valueParm[0] = "tdsType";

				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("30561", valueParm)));
			}
		} else if (StringUtils.isNotBlank(tdsType) && !"#".equals(tdsType)) {
			String[] valueParm = new String[2];
			valueParm[0] = "tdsType";
			valueParm[1] = "tdsApplicable is true";

			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90298", valueParm)));
		}

		if (fm.isQuickDisb() && !finType.isQuickDisb()) {
			String[] valueParm = new String[3];
			valueParm[0] = "quickDisb";
			valueParm[1] = finType.getFinType();

			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90329", valueParm)));
		}

		if (ImplementationConstants.ALLOW_ESCROW_MODE) {
			if (InstrumentType.isManual(fm.getFinRepayMethod())) {
				if (fm.isEscrow() && fm.getCustBankId() == null) {
					String[] valueParm = new String[1];
					valueParm[0] = "CustBankId";
					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
					return;
				}
				if (!fm.isEscrow() && fm.getCustBankId() != null) {
					String[] valueParm = new String[4];
					valueParm[0] = "Escrow";
					valueParm[1] = "should ";
					valueParm[2] = "be ";
					valueParm[3] = "true ";
					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("21005", valueParm)));
					return;
				}
				if (fm.isEscrow() && fm.getCustBankId() != null) {
					int count = financeMainDAO.getCustomerBankCountById(fm.getCustBankId(), fm.getCustID());
					if (count <= 0) {
						String[] valueParm = new String[1];
						valueParm[0] = "CustBankId";
						errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90405", valueParm)));
						return;
					}
				}
			} else {
				if (fm.isEscrow() || fm.getCustBankId() != null) {
					String[] valueParm = new String[4];
					valueParm[0] = "Escrow ";
					valueParm[1] = "is not applicable ";
					valueParm[2] = "for repay method: ";
					valueParm[3] = fm.getFinRepayMethod();
					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("30550", valueParm)));
					return;
				}
			}
		} else {
			if (fm.isEscrow() || fm.getCustBankId() != null) {
				String[] valueParm = new String[4];
				valueParm[0] = "Escrow ";
				valueParm[1] = "is ";
				valueParm[2] = "not ";
				valueParm[3] = "applicable. ";
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("30550", valueParm)));
				return;
			}
		}

		if (!errors.isEmpty()) {
			return;
		}

		if (fm.isManualSchedule()) {
			manualScheduleValidation(schdData);

			if (!errors.isEmpty()) {
				return;
			}
		}

		if (fm.getPlanDeferCount() > 0) {
			planDefermentValidation(schdData);

			if (!errors.isEmpty()) {
				return;
			}
		}

		planEMIHolidayValidation(schdData);

		if (!errors.isEmpty()) {
			return;
		}

		stepLoanValidation(schdData);
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
				Map<String, Object> fieldsAndValues = customerEligibilityCheck.getDeclaredFieldValues();
				downpayPercentage = (BigDecimal) RuleExecutionUtil.executeRule(sqlRule, fieldsAndValues,
						finMain.getFinCcy(), RuleReturnType.DECIMAL);
			}
			finMain.setMinDownPayPerc(downpayPercentage);
		} else {
			finMain.setMinDownPayPerc(BigDecimal.ZERO);
		}
	}

	private void graceValidation(FinScheduleData schdData) {
		List<ErrorDetail> errors = schdData.getErrorDetails();

		FinanceMain fm = schdData.getFinanceMain();
		FinanceType finType = schdData.getFinanceType();

		// Allow Grace?
		if (!finType.isFInIsAlwGrace()) {
			String[] valueParm = new String[1];
			valueParm[0] = fm.getFinType();
			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90135", valueParm)));

			return;
		}

		// Grace Terms & Grace End Date are Mutually Exclusive
		if (fm.getGraceTerms() > 0 && fm.getGrcPeriodEndDate() != null) {
			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90152", null)));

			return;
		}

		// Both Grace Terms & Grace End Date are not present
		if (fm.getGraceTerms() == 0 && fm.getGrcPeriodEndDate() == null) {
			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90184", null)));

			return;
		}

		// Validate Profit Details
		gracePftFrqValidation(schdData);

		if (CollectionUtils.isNotEmpty(errors)) {
			return;
		}

		// Grace Rate Validations
		graceRateValidation(schdData);

		// Validate Review Details
		gracePftReviewValidation(schdData);

		// Validate Capitalization Details
		gracePftCpzValidation(schdData);

		// Validate Grace Payment and Methods
		graceSchdValidation(schdData);

		// Validate Grace Dates
		graceDatesValidation(schdData);
	}

	/*
	 * ========================================================================= =======================================
	 * VALIDATE FINANCE REPAY DETAILS =========================================================================
	 * =======================================
	 */

	private void repayValidation(FinScheduleData schdData) {
		List<ErrorDetail> errors = schdData.getErrorDetails();

		FinanceMain fm = schdData.getFinanceMain();
		FinanceType finType = schdData.getFinanceType();

		String productCategory = fm.getProductCategory();
		Date maturityDate = fm.getMaturityDate();
		int numberOfTerms = fm.getNumberOfTerms();
		String subVentionFrom = fm.getSubVentionFrom();
		Long manufacturerDealerId = fm.getManufacturerDealerId();
		String scheduleMethod = fm.getScheduleMethod();

		int finMinTerm = finType.getFinMinTerm();
		int finMaxTerm = finType.getFinMaxTerm();
		boolean subventionReq = finType.isSubventionReq();

		/* Number of Terms & Maturity Date are Mutually Exclusive This is not applicable for OverDraft Web services */
		if (!FinanceConstants.PRODUCT_ODFACILITY.equals(productCategory)) {
			if (numberOfTerms > 0 && maturityDate != null) {
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90190", null)));

				return;
			}

			if (fm.getNumberOfTerms() == 0 && fm.getMaturityDate() != null) {
				fm.setNumberOfTerms(fm.getCalTerms());
			}
		}

		// Both Grace Terms & Grace End Date are not present
		if (numberOfTerms == 0 && maturityDate == null) {
			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90191", null)));

			return;
		}

		// validate min and max terms with loanType config.
		if (numberOfTerms > 0) {
			if (finMinTerm > 0 && finMaxTerm > 0) {
				if (numberOfTerms < finMinTerm || numberOfTerms > finMaxTerm) {
					String[] valueParm = new String[3];
					valueParm[0] = "Repay";
					valueParm[1] = String.valueOf(finMinTerm);
					valueParm[2] = String.valueOf(finMaxTerm);

					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90272", valueParm)));

					return;
				}
			}
		}

		if (subventionReq) {
			if (StringUtils.isBlank(subVentionFrom)) {
				String[] valueParm = new String[1];
				valueParm[0] = "subVentionFrom";

				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));

				return;
			}

			if (StringUtils.isNotBlank(subVentionFrom) && manufacturerDealerId == null) {
				String[] valueParm = new String[1];
				valueParm[0] = "ManufacturerDealerId";

				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));

				return;
			}

			List<ValueLabel> subVenFrom = PennantStaticListUtil.getSubVentionFrom();
			boolean isSubVenFrom = false;

			for (ValueLabel value : subVenFrom) {
				if (StringUtils.equals(value.getValue(), subVentionFrom)) {
					isSubVenFrom = true;
					break;
				}
			}

			if (!isSubVenFrom) {
				String[] valueParm = new String[2];
				valueParm[0] = App.getLabel("label_ApplicableFor");
				valueParm[1] = Labels.getLabel("label_Dealer") + "," + Labels.getLabel("label_Manufacturer");

				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90281", valueParm)));
			}

			int count = vehicleDealerService.getApprovedVehicleDealerCountById(manufacturerDealerId,
					subVentionFrom.equals("DSM") ? "DSM" : "MANF");

			if (count <= 0) {
				String[] valueParm = new String[1];
				valueParm[0] = fm.getDmaCode();

				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90501", valueParm)));

				return;
			}
		}

		// Repay Rate Validations
		repayRateValidation(schdData);

		/* Repayment Schedule Method (If not blanks validation already happens in defaulting) */
		if (!CalculationConstants.SCHMTHD_NOPAY.equals(scheduleMethod)
				&& !CalculationConstants.SCHMTHD_EQUAL.equals(scheduleMethod)
				&& !CalculationConstants.SCHMTHD_PFT.equals(scheduleMethod)
				&& !CalculationConstants.SCHMTHD_PFTCPZ.equals(scheduleMethod)
				&& !CalculationConstants.SCHMTHD_PRI.equals(scheduleMethod)
				&& !CalculationConstants.SCHMTHD_PRI_PFT.equals(scheduleMethod)
				&& !CalculationConstants.SCHMTHD_POS_INT.equals(scheduleMethod)) {

			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90189", null)));
		}

		// Validate Repayment Details
		repayFrqValidation(schdData);

		if (!errors.isEmpty()) {
			return;
		}

		// Validate Profit Details
		repayPftFrqValidation(schdData);

		// Validate Review Details
		repayPftReviewValidation(schdData);

		// Validate Capitalization Details
		repayPftCpzValidation(schdData);

		// Pay on interest frequency
		if (fm.isFinRepayPftOnFrq() && !finType.isFinRepayPftOnFrq()) {
			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90227", null)));
		}

		// Validate Repay Dates
		repayDatesValidation(schdData);

		// validation for od Loan
		if (FinanceConstants.PRODUCT_ODFACILITY.equals(productCategory)) {
			odDatesValidation(schdData);
		}

		// Validate BPI
		if (finType.isAlwBPI()) {
			bpiValidation(schdData);
		} else if (fm.isAlwBPI()) {
			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90228", null)));
			return;
		}

		// Advance EMI Validation
		int advTerms = fm.getAdvTerms();
		int advMaxTerms = finType.getAdvMaxTerms();
		int advMinTerms = finType.getAdvMinTerms();

		if (StringUtils.isNotBlank(fm.getAdvType())) {
			AdvanceType advanceType = AdvanceType.getType(fm.getAdvType());

			if (advanceType == null) {
				String[] valueParm = new String[2];
				valueParm[0] = "AdvanceType";
				valueParm[1] = fm.getAdvType();
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90224", valueParm)));
				return;
			}

			switch (advanceType) {
			case UF:
				if (advTerms > 0) {
					String[] valueParm = new String[3];
					valueParm[0] = "Upfront Interest Full Tenor the  Terms " + fm.getAdvTerms();
					valueParm[1] = "0";
					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("30565", valueParm)));
					return;
				}
				break;
			case AF:
				if (advTerms > 0) {
					String[] valueParm = new String[3];
					valueParm[0] = "Advance at Interest Frequency the Terms " + advTerms;
					valueParm[1] = "0";
					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("30565", valueParm)));
					return;
				}
				break;
			case UT:
				if (advTerms <= 0) {
					String[] valueParm = new String[3];
					valueParm[0] = "Advance Intrest/EMI";
					valueParm[1] = String.valueOf(advMinTerms);
					valueParm[2] = String.valueOf(advMaxTerms);

					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90272", valueParm)));

					return;
				}
				break;
			default:
				break;
			}

			if (fm.getAdvTerms() >= fm.getNumberOfTerms() && AdvanceType.UT.name().equals(fm.getAdvType())
					&& fm.getMaturityDate() == null) {
				String[] valueParm = new String[3];
				valueParm[0] = "Advance Intrest/EMI" + String.valueOf(advTerms);
				valueParm[1] = " Number of terms : " + String.valueOf(numberOfTerms);

				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("30565", valueParm)));

				return;
			}
		} else if (advTerms > 0) {
			String[] valueParm = new String[3];
			valueParm[0] = "Advance Intrest/EMI";
			valueParm[1] = finType.getFinType();

			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90329", valueParm)));

			return;
		} else {
			fm.setAdvType(finType.getAdvType());
			fm.setAdvTerms(finType.getAdvDefaultTerms());
			fm.setAdvStage(finType.getAdvStage());
		}

		if (!schdData.getFinanceType().isAdvIntersetReq() && !"#".equals(fm.getAdvType())) {
			String[] valueParm = new String[1];
			valueParm[0] = " Advance Type not configured in Loan Type";
			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("30550", valueParm)));
			return;
		}

		if (finType.isAlwHybridRate()) {
			if (fm.getFixedRateTenor() < 0) {
				String[] valueParm = new String[2];
				valueParm[0] = "Fixed Rate Tenor";
				valueParm[1] = "0";

				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("65012", valueParm)));

				return;
			}

			if (fm.getFixedRateTenor() >= numberOfTerms) {
				String[] valueParm = new String[2];
				valueParm[0] = "Fixed Rate Tenor : " + String.valueOf(fm.getFixedRateTenor());
				valueParm[1] = " Number of terms : " + String.valueOf(numberOfTerms);

				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("30565", valueParm)));

				return;
			}

			// validate isAlwUnderConstruction
			if (!finType.isGrcAdjReq()) {
				if (fm.isAlwGrcAdj()) {
					String[] valueParm = new String[2];
					valueParm[0] = "Alw Under Construction";
					valueParm[1] = fm.getFinType();

					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90329", valueParm)));
				}
			}

			if (fm.getFixedRateTenor() > 0 && fm.getFixedTenorRate().compareTo(BigDecimal.ZERO) <= 0) {
				String[] valueParm = new String[2];
				valueParm[0] = "Fixed Tenor Rate";
				valueParm[1] = "0";

				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("91121", valueParm)));

				return;
			}
		}
	}

	/*
	 * _______________________________________________________________________________________________________________
	 * MANUAL SCHEDULE
	 * _______________________________________________________________________________________________________________
	 */

	private void odDatesValidation(FinScheduleData schdData) {
		List<ErrorDetail> errors = schdData.getErrorDetails();

		FinanceMain fm = schdData.getFinanceMain();

		String droplineFrq = fm.getDroplineFrq();
		Date firstDroplineDate = fm.getFirstDroplineDate();

		// it should match with frequency
		if ((StringUtils.isNotBlank(droplineFrq)) && (firstDroplineDate != null)) {
			if (!FrequencyUtil.isFrqDate(droplineFrq, firstDroplineDate)) {
				String[] valueParm = new String[1];
				valueParm[0] = "FirstDroplineDate: " + firstDroplineDate;

				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("91123", valueParm)));
			}
		}
	}

	private void manualScheduleValidation(FinScheduleData schdData) {
		List<ErrorDetail> errors = schdData.getErrorDetails();
		FinanceMain fm = schdData.getFinanceMain();
		FinanceType finType = schdData.getFinanceType();

		if (!finType.isManualSchedule()) {
			String[] valueParm = new String[1];
			valueParm[0] = fm.getFinType();

			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90138", valueParm)));
		}

		if (!fm.isAllowGrcPeriod()) {
			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90186", null)));
		}

		if (fm.getPlanDeferCount() > 0) {
			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90140", null)));
		}

		if (!fm.isStepFinance()) {
			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90144", null)));
		}

		if (fm.isPlanEMIHAlw()) {
			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90149", null)));
		}
	}

	/*
	 * _______________________________________________________________________________________________________________
	 * PLANNED DEFERMENTS
	 * _______________________________________________________________________________________________________________
	 */

	private void planDefermentValidation(FinScheduleData schdData) {
		List<ErrorDetail> errors = schdData.getErrorDetails();

		FinanceMain fm = schdData.getFinanceMain();
		FinanceType finType = schdData.getFinanceType();

		if (!finType.isAlwPlanDeferment()) {
			String[] valueParm = new String[1];
			valueParm[0] = fm.getFinType();

			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90139", valueParm)));
		}

		if (!CalculationConstants.RATE_BASIS_C.equals(fm.getRepayRateBasis())) {
			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90151", null)));
		}

		if (fm.getPlanDeferCount() > finType.getPlanDeferCount()) {
			String[] valueParm = new String[3];
			valueParm[0] = Integer.toString(fm.getPlanDeferCount());
			valueParm[1] = Integer.toString(finType.getPlanDeferCount());
			valueParm[2] = fm.getFinType();

			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90141", valueParm)));
		}

		if (fm.isPlanEMIHAlw()) {
			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90142", null)));
		}

		if (fm.isStepFinance()) {
			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90143", null)));
		}
	}

	/*
	 * _______________________________________________________________________________________________________________
	 * STEP LOAN
	 * _______________________________________________________________________________________________________________
	 */

	private void stepLoanValidation(FinScheduleData schdData) {
		List<ErrorDetail> errors = schdData.getErrorDetails();

		FinanceMain fm = schdData.getFinanceMain();
		FinanceType finType = schdData.getFinanceType();

		boolean fmStepFinance = fm.isStepFinance();
		boolean ftStepFinance = finType.isStepFinance();
		String scheduleMethod = fm.getScheduleMethod();
		String stepType = fm.getStepType();

		if (!fmStepFinance && (fm.isAlwManualSteps() || StringUtils.isNotBlank(fm.getStepPolicy())
				|| StringUtils.isNotBlank(stepType))) {
			String[] valueParm = new String[1];
			valueParm[0] = fm.getFinType();
			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90163", valueParm)));

			return;
		}

		if (fmStepFinance && !ftStepFinance) {
			String[] valueParm = new String[1];
			valueParm[0] = fm.getFinType();
			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("91129", valueParm)));

			return;
		}

		if (!fmStepFinance && finType.isSteppingMandatory()) {
			String[] valueParm = new String[1];
			valueParm[0] = fm.getFinType();
			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("91128", valueParm)));

			return;
		}

		if (fmStepFinance) {
			if (CalculationConstants.SCHMTHD_PFT.equals(scheduleMethod)
					|| CalculationConstants.SCHMTHD_PFTCPZ.equals(scheduleMethod)
					|| CalculationConstants.SCHMTHD_PFTCAP.equals(scheduleMethod)) {
				String[] valueParm = new String[1];
				valueParm[0] = "Interest only on Frequency";
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("30552", valueParm)));

				return;
			}

			if (PennantConstants.STEPPING_CALC_PERC.equals(fm.getCalcOfSteps())) {
				if (FinanceConstants.STEPTYPE_EMI.equals(fm.getStepType())
						&& !CalculationConstants.SCHMTHD_EQUAL.equals(fm.getScheduleMethod())) {
					String[] valueParm = new String[1];
					valueParm[0] = "Equal Installments (Principal and Interest)";
					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("30703", valueParm)));

					return;
				}
				if (PennantConstants.STEPPING_CALC_PERC.equals(fm.getCalcOfSteps())) {
					if (StringUtils.equals(fm.getStepType(), FinanceConstants.STEPTYPE_EMI)
							&& !StringUtils.equals(fm.getScheduleMethod(), CalculationConstants.SCHMTHD_EQUAL)) {
						String[] valueParm = new String[1];
						valueParm[0] = "Equal Installments (Principal and Interest)";
						errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("30703", valueParm)));
						return;
					}

					// PSD#174857, 178929(I) allow planned EMI for step loan if step calculate on amount.
					if (fm.isPlanEMIHAlw()) {
						String[] valueParm = new String[2];
						valueParm[0] = "Planned EMI";
						valueParm[1] = "step";
						errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90283", valueParm)));
					}
				}
			}

			if (StringUtils.isNotBlank(fm.getStepsAppliedFor())) {
				List<ValueLabel> stepsApplForList = PennantStaticListUtil.getStepsAppliedFor();
				boolean stepsApplForSts = false;
				for (ValueLabel value : stepsApplForList) {
					if (StringUtils.equals(value.getValue(), fm.getStepsAppliedFor())) {
						stepsApplForSts = true;
						break;
					}
				}
				if (!stepsApplForSts) {
					String[] valueParm = new String[2];
					valueParm[0] = "stepsAppliedFor";
					valueParm[1] = fm.getStepsAppliedFor();
					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("STP0012", valueParm)));
				}
			}

			if (!PennantConstants.STEPPING_APPLIED_EMI.equals(fm.getStepsAppliedFor())) {
				if (!PennantConstants.STEPPING_CALC_AMT.equals(fm.getCalcOfSteps())) {
					String[] valueParm = new String[1];
					valueParm[0] = fm.getStepsAppliedFor();
					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("STP0013", valueParm)));
				}
			} else {
				List<ValueLabel> calcOfStepsList = PennantStaticListUtil.getCalcOfStepsList();
				boolean calcOfStepsSts = false;
				for (ValueLabel value : calcOfStepsList) {
					if (StringUtils.equals(value.getValue(), fm.getCalcOfSteps())) {
						calcOfStepsSts = true;
						break;
					}
				}
				if (!calcOfStepsSts) {
					String[] valueParm = new String[2];
					valueParm[0] = "calcOfSteps";
					valueParm[0] = fm.getCalcOfSteps();
					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("STP0012", valueParm)));
				}
			}
			if (StringUtils.isNotBlank(fm.getStepPolicy())
					&& PennantConstants.STEPPING_CALC_AMT.equals(fm.getCalcOfSteps())) {
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("STP0015", null)));
			}
		}

		if (fm.isAlwManualSteps()) {
			if (!finType.isAlwManualSteps()) {
				String[] valueParm = new String[1];
				valueParm[0] = fm.getFinType();
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90145", valueParm)));

				return;
			}

			if (StringUtils.isNotBlank(fm.getStepPolicy())) {
				String[] valueParm = new String[2];
				valueParm[0] = fm.getStepPolicy();
				valueParm[1] = "loan with allow manualSteps";
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90204", valueParm)));

				return;
			}

			if (PennantConstants.STEPPING_CALC_PERC.equals(fm.getCalcOfSteps())
					&& !FinanceConstants.STEPTYPE_EMI.equals(stepType)
					&& !FinanceConstants.STEPTYPE_PRIBAL.equals(stepType)) {
				String[] valueParm = new String[2];
				valueParm[0] = new StringBuilder(10).append(FinanceConstants.STEPTYPE_EMI).append(" & ")
						.append(FinanceConstants.STEPTYPE_PRIBAL).toString();

				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90148", valueParm)));
			}

			if (schdData.getStepPolicyDetails() == null || schdData.getStepPolicyDetails().isEmpty()) {
				String[] valueParm = new String[1];
				valueParm[0] = "step";

				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
			}

		} else if (fmStepFinance) {
			if (StringUtils.isBlank(fm.getStepPolicy())) {
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90146", null)));
			} else {
				StepPolicyHeader stepHeader = stepPolicyService.getApprovedStepPolicyHeaderById(fm.getStepPolicy());
				if (stepHeader == null) {
					String[] valueParm = new String[1];
					valueParm[0] = fm.getStepPolicy();
					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90252", valueParm)));
				}

				if (PennantConstants.STEPPING_CALC_PERC.equals(finType.getCalcOfSteps())) {
					if (!StringUtils.containsIgnoreCase(finType.getAlwdStepPolicies(), fm.getStepPolicy())) {
						String[] valueParm = new String[2];
						valueParm[0] = fm.getFinType();
						valueParm[1] = finType.getAlwdStepPolicies();
						errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90147", valueParm)));
					}
				}

				// Validate stepType
				if (stepHeader != null) {
					if (!stepHeader.getStepType().equals(stepType)) {
						String[] valueParm = new String[2];
						valueParm[0] = fm.getStepPolicy();
						valueParm[1] = stepHeader.getStepType();
						errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("STP0014", valueParm)));
					}
				}
			}
		}
	}

	/*
	 * _______________________________________________________________________________________________________________
	 * PLAN EMI HOLIDAY
	 * _______________________________________________________________________________________________________________
	 */

	private void planEMIHolidayValidation(FinScheduleData schdData) {
		List<ErrorDetail> errors = schdData.getErrorDetails();

		FinanceMain fm = schdData.getFinanceMain();
		FinanceType finType = schdData.getFinanceType();

		if (!finType.isPlanEMIHAlw() && fm.isPlanEMIHAlw()) {
			String[] valueParm = new String[2];
			valueParm[0] = "Planned EMI Holiday(planEMIHAlw)";
			valueParm[1] = fm.getFinType();
			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90204", valueParm)));

			return;
		}

		if (fm.isPlanEMIHAlwInGrace() && !finType.isalwPlannedEmiInGrc()) {
			String[] valueParm = new String[2];
			valueParm[0] = "Planned EMI Holiday(planEMIHAlw)";
			valueParm[1] = fm.getFinType();
			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90204", valueParm)));

			return;
		}

		if (fm.isPlanEMIHAlwInGrace() && !fm.isAllowGrcPeriod()) {
			String[] valueParm = new String[2];
			valueParm[0] = "Planned EMI Holiday(planEMIHAlw)";
			valueParm[1] = "allowGrcPeriod is false";
			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90204", valueParm)));

			return;
		}

		String planEMIHMethod = fm.getPlanEMIHMethod();
		if (!fm.isPlanEMIHAlw() && (StringUtils.isNotBlank(planEMIHMethod) || fm.getPlanEMIHMaxPerYear() > 0
				|| fm.getPlanEMIHMax() > 0 || fm.getPlanEMIHLockPeriod() > 0 || fm.isPlanEMICpz())) {
			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90241", null)));

			return;
		}

		if (fm.isPlanEMIHAlw() && StringUtils.isBlank(planEMIHMethod)) {
			String[] valueParm = new String[1];
			valueParm[0] = "planEMIHMethod";
			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));

			return;
		}

		if (fm.getPlanEMIHMaxPerYear() == 0) {
			fm.setPlanEMIHMaxPerYear(finType.getPlanEMIHMaxPerYear());
		}

		if (fm.getPlanEMIHMaxPerYear() < 0) {
			String[] valueParm = new String[2];
			valueParm[0] = "PlanEMIHMaxPerYear";
			valueParm[1] = "0";
			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("91121", valueParm)));

			return;
		}

		if (!finType.isPlanEMICpz() && fm.isPlanEMICpz()) {
			String[] valueParm = new String[2];
			valueParm[0] = "planEMICpz";
			valueParm[1] = finType.getFinType();
			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90329", valueParm)));

			return;
		}

		if (!fm.isPlanEMICpz()) {
			fm.setPlanEMICpz(finType.isPlanEMICpz());
		}

		if (fm.getPlanEMIHMax() == 0) {
			fm.setPlanEMIHMax(finType.getPlanEMIHMax());
		}

		if (fm.getPlanEMIHMax() < 0) {
			String[] valueParm = new String[2];
			valueParm[0] = "PlanEMIHMax";
			valueParm[1] = "0";
			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("91121", valueParm)));

			return;
		}

		if (fm.getPlanEMIHLockPeriod() == 0) {
			fm.setPlanEMIHLockPeriod(finType.getPlanEMIHLockPeriod());
		}

		if (fm.getPlanEMIHLockPeriod() < 0) {
			String[] valueParm = new String[2];
			valueParm[0] = "PlanEMIHLockPeriod";
			valueParm[1] = "0";
			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("91121", valueParm)));

			return;
		}

		if (StringUtils.isNotEmpty(planEMIHMethod) && !FinanceConstants.PLANEMIHMETHOD_FRQ.equals(planEMIHMethod)
				&& !FinanceConstants.PLANEMIHMETHOD_ADHOC.equals(planEMIHMethod)) {
			String[] valueParm = new String[1];
			valueParm[0] = FinanceConstants.PLANEMIHMETHOD_FRQ + "," + FinanceConstants.PLANEMIHMETHOD_ADHOC;
			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90238", valueParm)));

			return;
		}

		if (fm.getPlanEMIHMaxPerYear() > 11) {
			String[] valueParm = new String[1];
			valueParm[0] = String.valueOf(11);
			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90239", valueParm)));

			return;
		}

		if (fm.getNumberOfTerms() > 0 && fm.getPlanEMIHMax() > (fm.getNumberOfTerms() - 1)) {
			String[] valueParm = new String[1];
			valueParm[0] = String.valueOf(fm.getNumberOfTerms() - 1);
			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90240", valueParm)));

			return;
		}

		if (fm.getPlanEMIHMax() > finType.getPlanEMIHMax()) {
			String[] valueParm = new String[2];
			valueParm[0] = "PlanEMIHMax";
			valueParm[1] = String.valueOf(finType.getPlanEMIHMax());
			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("30568", valueParm)));

			return;
		}

		if (fm.getPlanEMIHMaxPerYear() > finType.getPlanEMIHMaxPerYear()) {
			String[] valueParm = new String[2];
			valueParm[0] = "PlanEMIHMaxPerYear";
			valueParm[1] = String.valueOf(finType.getPlanEMIHMaxPerYear());
			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("30568", valueParm)));

			return;
		}

		if (!(finType.getPlanEMIHLockPeriod() >= fm.getPlanEMIHLockPeriod())) {
			String[] valueParm = new String[2];
			valueParm[0] = "PlanEMIHLockPeriod";
			valueParm[1] = String.valueOf(finType.getPlanEMIHLockPeriod());
			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("30568", valueParm)));

			return;
		}

		if (FinanceConstants.PLANEMIHMETHOD_FRQ.equals(planEMIHMethod)) {
			if (CollectionUtils.isEmpty(schdData.getApiPlanEMIHmonths())) {
				String[] valueParm = new String[1];
				valueParm[0] = "planEMIHmonths";
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));

				return;
			}

			if (schdData.getApiPlanEMIHmonths().size() > fm.getPlanEMIHMaxPerYear()) {
				String[] valueParm = new String[2];
				valueParm[0] = "PlanEMIHmonths";
				valueParm[1] = "PlanEMIHMaxPerYear";
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90220", valueParm)));

				return;
			}

			for (FinPlanEmiHoliday detail : schdData.getApiPlanEMIHmonths()) {
				int count = 0;

				if (!(detail.getPlanEMIHMonth() > 0 && detail.getPlanEMIHMonth() <= 12)) {
					String[] valueParm = new String[3];
					valueParm[0] = "holidayMonth";
					valueParm[1] = "1";
					valueParm[2] = "12";
					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("65031", valueParm)));

					return;
				}

				for (FinPlanEmiHoliday planEmiMnths : schdData.getApiPlanEMIHmonths()) {
					if (detail.getPlanEMIHMonth() == planEmiMnths.getPlanEMIHMonth()) {
						count++;
					}
				}

				if (count >= 2) {
					String[] valueParm = new String[1];
					valueParm[0] = "holidayMonth";
					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90273", valueParm)));

					return;
				}
			}

		} else if (FinanceConstants.PLANEMIHMETHOD_ADHOC.equals(planEMIHMethod)) {
			if (CollectionUtils.isEmpty(schdData.getApiPlanEMIHDates())) {
				String[] valueParm = new String[1];
				valueParm[0] = "planEMIHDates";
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));

				return;
			}
			if (schdData.getApiPlanEMIHDates().size() > fm.getPlanEMIHMax()) {
				String[] valueParm = new String[2];
				valueParm[0] = "PlanEMIHDates";
				valueParm[1] = "PlanEMIHMax";
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90220", valueParm)));

				return;
			}

			for (FinPlanEmiHoliday emiDates : schdData.getApiPlanEMIHDates()) {
				int count = 0;
				for (FinPlanEmiHoliday duplicateDates : schdData.getApiPlanEMIHDates()) {
					if (emiDates.getPlanEMIHDate().compareTo(duplicateDates.getPlanEMIHDate()) == 0) {
						count++;
					}
				}

				if (count >= 2) {
					String[] valueParm = new String[1];
					valueParm[0] = "PlanEMIHDates";
					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90273", valueParm)));

					return;
				}
			}
		}
	}

	/*
	 * _______________________________________________________________________________________________________________
	 * GRACE RATE
	 * _______________________________________________________________________________________________________________
	 */

	private void graceRateValidation(FinScheduleData schdData) {
		List<ErrorDetail> errors = schdData.getErrorDetails();

		FinanceMain fm = schdData.getFinanceMain();
		FinanceType finType = schdData.getFinanceType();

		Date appDate = SysParamUtil.getAppDate();

		BigDecimal grcMaxRate = fm.getGrcMaxRate();
		BigDecimal grcMinRate = fm.getGrcMinRate();
		String grcRateBasis = fm.getGrcRateBasis();
		BigDecimal grcPftRate = fm.getGrcPftRate();
		String graceBaseRate = fm.getGraceBaseRate();
		BigDecimal grcMargin = fm.getGrcMargin();

		String finGrcBaseRate = finType.getFinGrcBaseRate();

		// validate MinRate and MaxRate fields
		if (grcMinRate.compareTo(grcMaxRate) > 0) {
			String[] valueParm = new String[2];
			valueParm[0] = "Grace Max Rate:" + grcMaxRate;
			valueParm[1] = "Grace Min Rate:" + grcMinRate;

			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("91121", valueParm)));
		}

		// Rate Type/Rate Basis
		if (!CalculationConstants.RATE_BASIS_F.equals(grcRateBasis)
				&& !CalculationConstants.RATE_BASIS_R.equals(grcRateBasis)) {
			String[] valueParm = new String[1];
			valueParm[0] = grcRateBasis;

			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90211", valueParm)));
		}

		// Actual Rate
		if (grcPftRate.compareTo(BigDecimal.ZERO) < 0) {
			String[] valueParm = new String[1];
			valueParm[0] = "Grace";

			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90153", valueParm)));
		}

		// Both Grace and Base Rates found
		if (StringUtils.isNotBlank(graceBaseRate) && grcPftRate.compareTo(BigDecimal.ZERO) != 0) {
			String[] valueParm = new String[1];
			valueParm[0] = "Grace";

			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90154", valueParm)));
		}

		// Base Rate requested?
		if (StringUtils.isBlank(graceBaseRate) && StringUtils.isNotBlank(finGrcBaseRate)) {
			String[] valueParm = new String[2];
			valueParm[0] = "Grace";
			valueParm[1] = fm.getFinType();

			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90232", valueParm)));
		}

		if (StringUtils.isBlank(graceBaseRate)) {
			if (grcPftRate.compareTo(BigDecimal.ZERO) > 0 && StringUtils.isNotBlank(finGrcBaseRate)) {
				String[] valueParm = new String[2];
				valueParm[0] = "Grace Profit Rate";
				valueParm[1] = fm.getFinType();

				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90204", valueParm)));
			}
		}

		// Allow Base Rate?
		if (StringUtils.isNotBlank(graceBaseRate)) {
			if (StringUtils.isBlank(finGrcBaseRate)) {
				String[] valueParm = new String[2];
				valueParm[0] = "Grace";
				valueParm[1] = fm.getFinType();

				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90136", valueParm)));
			} else {
				// Base Rate code found?
				String currency = finType.getFinCcy();
				// FIXME :: Move to cache
				if (baseRateDAO.getBaseRateCountById(graceBaseRate, currency, "") <= 0) {
					String[] valueParm = new String[2];
					valueParm[0] = graceBaseRate;
					valueParm[1] = fm.getFinCcy();

					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90137", valueParm)));
				}

				// validate special rate code
				if (StringUtils.isNotBlank(fm.getGraceSpecialRate())) {
					if (splRateDAO.getSpecialRateCountById(fm.getGraceSpecialRate(), "") <= 0) {
						String[] valueParm = new String[2];
						valueParm[0] = fm.getGraceSpecialRate();
						valueParm[1] = "Grace";

						errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90230", valueParm)));
					}
				}
			}
		}

		// Special Rate code
		if (StringUtils.isNotBlank(fm.getGraceSpecialRate()) && StringUtils.isBlank(graceBaseRate)) {
			String[] valueParm = new String[1];
			valueParm[0] = "Grace";

			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90155", valueParm)));
		}

		// Margin
		if (grcMargin.compareTo(BigDecimal.ZERO) != 0 && StringUtils.isBlank(graceBaseRate)) {
			String[] valueParm = new String[1];
			valueParm[0] = "Grace";

			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90155", valueParm)));
		}

		if (CollectionUtils.isNotEmpty(errors)) {
			return;
		}

		// Validate Against Minimum and Maximum Rate
		if (grcMinRate.compareTo(BigDecimal.ZERO) == 0 && grcMaxRate.compareTo(BigDecimal.ZERO) == 0) {
			return;
		}

		BigDecimal netRate = BigDecimal.ZERO;

		if (StringUtils.isBlank(graceBaseRate)) {
			return;
		}

		// Base Rate
		RateDetail rate = new RateDetail();
		rate.setBaseRateCode(graceBaseRate);
		rate.setCurrency(fm.getFinCcy());
		rate.setSplRateCode(fm.getGraceSpecialRate());
		rate.setMargin(grcMargin);
		rate.setValueDate(appDate);
		RateUtil.getRefRate(rate);

		if (rate.getErrorDetails() != null) {
			errors.add(rate.getErrorDetails());
		}

		if (CollectionUtils.isNotEmpty(errors)) {
			return;
		}

		netRate = rate.getNetRefRateLoan();

		// Check Against Minimum Rate
		if (grcMinRate.compareTo(BigDecimal.ZERO) != 0 && netRate.compareTo(grcMinRate) < 0) {
			String[] valueParm = new String[2];
			valueParm[0] = round4(netRate).toString();
			valueParm[1] = round4(grcMinRate).toString();

			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90172", valueParm)));
		}

		// Check Against Maximum Rate
		if (grcMaxRate.compareTo(BigDecimal.ZERO) != 0 && netRate.compareTo(grcMaxRate) > 0) {
			String[] valueParm = new String[2];
			valueParm[0] = round4(netRate).toString();
			valueParm[1] = round4(grcMaxRate).toString();

			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90173", valueParm)));
		}
	}

	/*
	 * _______________________________________________________________________________________________________________
	 * VALIDATE GRACE PROFIT FREQUENCY AND NEXT PROFIT DATE
	 * _______________________________________________________________________________________________________________
	 */

	private void gracePftFrqValidation(FinScheduleData schdData) {
		List<ErrorDetail> errors = schdData.getErrorDetails();

		String moveNone = HolidayHandlerTypes.MOVE_NONE;

		FinanceMain fm = schdData.getFinanceMain();
		FinanceType finType = schdData.getFinanceType();

		String grcPftFrq = fm.getGrcPftFrq();
		Date finStartDate = fm.getFinStartDate();
		int grcTerms = fm.getGraceTerms();
		Date nextGrcPftDate = fm.getNextGrcPftDate();
		Date grcPrdEndDate = fm.getGrcPeriodEndDate();

		// Validate Profit Frequency
		ErrorDetail tempError = FrequencyUtil.validateFrequency(grcPftFrq);
		if (tempError != null) {
			String[] valueParm = new String[2];
			valueParm[0] = "Grace";

			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90156", valueParm)));
		}

		// Validate with Allowed frequency days.
		if (!validateAlwFrqDays(grcPftFrq, finType.getFrequencyDays())) {
			String[] valueParm = new String[3];
			valueParm[0] = "Grace";
			valueParm[1] = grcPftFrq;
			valueParm[2] = finType.getFrequencyDays();

			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90271", valueParm)));
		}

		// First Interest Frequency Date Vs Start Date
		if (nextGrcPftDate.compareTo(finStartDate) <= 0) {
			String[] valueParm = new String[2];
			valueParm[0] = DateUtil.formatToShortDate(nextGrcPftDate);
			valueParm[1] = DateUtil.formatToShortDate(finStartDate);

			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90162", valueParm)));
		}

		// Return if any error
		if (!errors.isEmpty()) {
			return;
		}

		// Default Calculated Grace End Date using terms
		if (grcTerms > 0) {
			fm.setCalGrcTerms(grcTerms);
			FrequencyDetails frequency = FrequencyUtil.getNextDate(grcPftFrq, grcTerms, nextGrcPftDate, moveNone, true);

			List<Calendar> schdDates = frequency.getScheduleList();

			if (schdDates != null) {
				fm.setCalGrcEndDate(DateUtil.getDatePart(schdDates.get(schdDates.size() - 1).getTime()));
			}

		} else {
			fm.setCalGrcEndDate(grcPrdEndDate);
			fm.setCalGrcTerms(FrequencyUtil.getTerms(grcPftFrq, nextGrcPftDate, grcPrdEndDate, true, true).getTerms());
		}

		// First Interest Frequency Date Vs Grace Period End Date
		if (nextGrcPftDate.compareTo(fm.getCalGrcEndDate()) > 0) {
			String[] valueParm = new String[2];
			valueParm[0] = DateUtil.formatToShortDate(fm.getNextGrcPftRvwDate());
			valueParm[1] = DateUtil.formatToShortDate(fm.getCalGrcEndDate());

			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90161", valueParm)));
		}
	}

	/*
	 * _______________________________________________________________________________________________________________
	 * GRACE PROFIT REVIEW
	 * _______________________________________________________________________________________________________________
	 */

	private void gracePftReviewValidation(FinScheduleData schData) {
		List<ErrorDetail> errors = schData.getErrorDetails();

		FinanceMain fm = schData.getFinanceMain();
		FinanceType finType = schData.getFinanceType();

		boolean finGrcIsRvwAlw = finType.isFinGrcIsRvwAlw();
		String frequencyDays = finType.getFrequencyDays();

		String grcPftRvwFrq = fm.getGrcPftRvwFrq();
		Date nextGrcPftRvwDate = fm.getNextGrcPftRvwDate();
		boolean allowGrcPftRvw = fm.isAllowGrcPftRvw();
		Date finStartDate = fm.getFinStartDate();
		Date calGrcEndDate = fm.getCalGrcEndDate();

		// Allow Profit Rate Review
		if (!finGrcIsRvwAlw) {
			if (allowGrcPftRvw || StringUtils.isNotBlank(grcPftRvwFrq) || nextGrcPftRvwDate != null) {
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90164", null)));
			}

			return;
		}

		// Validate Profit Review Frequency
		ErrorDetail error = FrequencyUtil.validateFrequency(grcPftRvwFrq);
		if (error != null) {
			String[] valueParm = new String[2];
			valueParm[0] = "Grace";

			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90157", valueParm)));
		}

		// Validate with Allowed frequency days.
		if (!validateAlwFrqDays(grcPftRvwFrq, frequencyDays)) {
			String[] valueParm = new String[3];
			valueParm[0] = "Grace";
			valueParm[1] = grcPftRvwFrq;
			valueParm[2] = frequencyDays;

			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90271", valueParm)));
		}

		// First Interest Review Frequency Date Vs Start Date
		if (nextGrcPftRvwDate.compareTo(finStartDate) <= 0) {
			String[] valueParm = new String[2];
			valueParm[0] = DateUtil.formatToShortDate(nextGrcPftRvwDate);
			valueParm[1] = DateUtil.formatToShortDate(finStartDate);

			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90166", valueParm)));
		}

		// First Interest Review Frequency Date Vs Grace Period End Date
		if (nextGrcPftRvwDate.compareTo(calGrcEndDate) > 0) {
			String[] valueParm = new String[2];
			valueParm[0] = DateUtil.formatToShortDate(nextGrcPftRvwDate);
			valueParm[1] = DateUtil.formatToShortDate(calGrcEndDate);

			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90165", valueParm)));
		}
	}

	/*
	 * _______________________________________________________________________________________________________________
	 * GRACE PROFIT CAPITALIZATION
	 * _______________________________________________________________________________________________________________
	 */

	private void gracePftCpzValidation(FinScheduleData schdData) {
		List<ErrorDetail> errors = schdData.getErrorDetails();

		FinanceMain fm = schdData.getFinanceMain();
		FinanceType finType = schdData.getFinanceType();

		boolean finGrcIsIntCpz = finType.isFinGrcIsIntCpz();
		String frequencyDays = finType.getFrequencyDays();

		String grcCpzFrq = fm.getGrcCpzFrq();
		Date nextGrcCpzDate = fm.getNextGrcCpzDate();
		boolean allowGrcCpz = fm.isAllowGrcCpz();
		Date finStartDate = fm.getFinStartDate();
		Date calGrcEndDate = fm.getCalGrcEndDate();

		// Allow Profit Capitalization
		if (!finGrcIsIntCpz) {
			if (allowGrcCpz || StringUtils.isNotBlank(grcCpzFrq) || nextGrcCpzDate != null) {
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90167", null)));
			}

			return;
		}

		// Validate Profit Capitalization Frequency
		ErrorDetail error = FrequencyUtil.validateFrequency(grcCpzFrq);
		if (error != null) {
			String[] valueParm = new String[2];
			valueParm[0] = "Grace";

			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90158", valueParm)));
		}

		// Validate with Allowed frequency days.
		if (!validateAlwFrqDays(grcCpzFrq, frequencyDays)) {
			String[] valueParm = new String[3];
			valueParm[0] = "Grace";
			valueParm[1] = grcCpzFrq;
			valueParm[2] = frequencyDays;

			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90271", valueParm)));
		}

		// First Interest Capitalization Frequency Date Vs Start Date
		if (nextGrcCpzDate.compareTo(finStartDate) <= 0) {
			String[] valueParm = new String[2];
			valueParm[0] = DateUtil.formatToShortDate(nextGrcCpzDate);
			valueParm[1] = DateUtil.formatToShortDate(finStartDate);

			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90168", valueParm)));
		}

		// First Interest Capitalization Frequency Date Vs Grace End Date
		if (nextGrcCpzDate.compareTo(calGrcEndDate) > 0) {
			String[] valueParm = new String[2];
			valueParm[0] = DateUtil.formatToShortDate(nextGrcCpzDate);
			valueParm[1] = DateUtil.formatToShortDate(calGrcEndDate);

			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90169", valueParm)));
		}
	}

	/*
	 * _______________________________________________________________________________________________________________
	 * GRACE SCHEDULE VALIDATION
	 * _______________________________________________________________________________________________________________
	 */

	private void graceSchdValidation(FinScheduleData schdData) {
		List<ErrorDetail> errors = schdData.getErrorDetails();

		FinanceMain fm = schdData.getFinanceMain();
		FinanceType finType = schdData.getFinanceType();

		String grcSchdMthd = fm.getGrcSchdMthd();
		BigDecimal grcMaxAmount = fm.getGrcMaxAmount();

		if (!fm.isAllowGrcRepay()) {
			if (StringUtils.isNotBlank(grcSchdMthd)) {
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90171", null)));
			}
		} else {
			if (!finType.isFinIsAlwGrcRepay()) {
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90170", null)));
			} else {
				if (!CalculationConstants.SCHMTHD_NOPAY.equals(grcSchdMthd)
						&& !CalculationConstants.SCHMTHD_PFT.equals(grcSchdMthd)
						&& !CalculationConstants.SCHMTHD_PFTCPZ.equals(grcSchdMthd)
						&& !CalculationConstants.SCHMTHD_GRCENDPAY.equals(grcSchdMthd)
						&& !CalculationConstants.SCHMTHD_PFTCAP.equals(grcSchdMthd)) {

					String[] valueParm = new String[2];
					valueParm[0] = "Grace";
					valueParm[1] = grcSchdMthd;

					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90210", valueParm)));
				}
			}
		}

		if (!CalculationConstants.SCHMTHD_PFTCAP.equals(grcSchdMthd)) {
			if (grcMaxAmount != null && grcMaxAmount.compareTo(BigDecimal.ZERO) > 0) {
				String[] valueParm = new String[2];
				valueParm[0] = "GrcMaxAmount";
				valueParm[1] = CalculationConstants.SCHMTHD_PFTCAP;

				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90298", valueParm)));
			}
		}

		if (CalculationConstants.SCHMTHD_PFTCAP.equals(grcSchdMthd)) {
			if (grcMaxAmount != null && grcMaxAmount.compareTo(BigDecimal.ZERO) <= 0) {
				String[] valueParm = new String[2];
				valueParm[0] = "GrcMaxAmount";
				valueParm[1] = "0";

				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("91121", valueParm)));
			}
		}
	}

	/*
	 * _______________________________________________________________________________________________________________
	 * GRACE DATES
	 * _______________________________________________________________________________________________________________
	 */

	private void graceDatesValidation(FinScheduleData schdData) {
		List<ErrorDetail> errors = schdData.getErrorDetails();

		FinanceMain fm = schdData.getFinanceMain();
		FinanceType finType = schdData.getFinanceType();

		Date calGrcEndDate = fm.getCalGrcEndDate();
		Date nextGrcPftDate = fm.getNextGrcPftDate();
		String grcPftFrq = fm.getGrcPftFrq();
		Date pftRvwDate = fm.getNextGrcPftRvwDate();
		String grcPftRvwFrq = fm.getGrcPftRvwFrq();
		Date nextGrcCpzDate = fm.getNextGrcCpzDate();
		String grcCpzFrq = fm.getGrcCpzFrq();

		boolean finGrcIsRvwAlw = finType.isFinGrcIsRvwAlw();
		boolean finGrcIsIntCpz = finType.isFinGrcIsIntCpz();

		/* If Next Profit date is not as Grace End Date, it should match with frequency */
		if (nextGrcPftDate.compareTo(calGrcEndDate) != 0 && !FrequencyUtil.isFrqDate(grcPftFrq, nextGrcPftDate)) {
			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90177", null)));
		}

		/* If Next Profit Review date is not as Grace End Date, it should match with frequency */
		if (finGrcIsRvwAlw) {
			if (pftRvwDate.compareTo(calGrcEndDate) != 0 && !FrequencyUtil.isFrqDate(grcPftRvwFrq, pftRvwDate)) {
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90178", null)));
			}
		}

		/* If Next Profit capitalization date is not as Grace End Date, it should match with frequency */
		if (finGrcIsIntCpz) {
			if (nextGrcCpzDate.compareTo(calGrcEndDate) != 0 && !FrequencyUtil.isFrqDate(grcCpzFrq, nextGrcCpzDate)) {
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90179", null)));
			}
		}
	}

	/*
	 * _______________________________________________________________________________________________________________
	 * GRACE RATE
	 * _______________________________________________________________________________________________________________
	 */

	private void repayRateValidation(FinScheduleData schdData) {
		Date appDate = SysParamUtil.getAppDate();

		List<ErrorDetail> errors = schdData.getErrorDetails();

		FinanceMain fm = schdData.getFinanceMain();
		FinanceType finType = schdData.getFinanceType();

		BigDecimal zeroValue = BigDecimal.ZERO;
		final String REPAY = "Repay";

		String finBaseRate = finType.getFinBaseRate();
		BigDecimal repayProfitRate = fm.getRepayProfitRate();
		String repayRateBasis = fm.getRepayRateBasis();
		String repayBaseRate = fm.getRepayBaseRate();
		String productCategory = fm.getProductCategory();
		String repaySpecialRate = fm.getRepaySpecialRate();
		String loanType = fm.getFinType();

		/* Consumer durables will take the defaulted rate. No validation required */
		if (FinanceConstants.PRODUCT_CD.equals(productCategory)) {
			return;
		}

		// Rate Type/Rate Basis
		if (!CalculationConstants.RATE_BASIS_F.equals(repayRateBasis)
				&& !CalculationConstants.RATE_BASIS_C.equals(repayRateBasis)
				&& !CalculationConstants.RATE_BASIS_R.equals(repayRateBasis)) {

			String[] valueParm = new String[1];
			valueParm[0] = repayRateBasis;

			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90211", valueParm)));
		}

		// Actual Rate
		if (StringUtils.isBlank(finBaseRate) && repayProfitRate.compareTo(BigDecimal.ZERO) < 0) {
			String[] valueParm = new String[1];
			valueParm[0] = REPAY;

			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90153", valueParm)));
		}

		// Both Grace and Base Rates found
		if (StringUtils.isNotBlank(repayBaseRate) && repayProfitRate.compareTo(BigDecimal.ZERO) != 0) {
			String[] valueParm = new String[1];
			valueParm[0] = REPAY;

			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90154", valueParm)));
		}

		// validate base rate
		if (StringUtils.isBlank(repayBaseRate) && StringUtils.isNotBlank(finBaseRate)) {
			String[] valueParm = new String[2];
			valueParm[0] = REPAY;
			valueParm[1] = loanType;

			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90232", valueParm)));
		}

		// Base Rate requested?
		if (StringUtils.isNotBlank(repayBaseRate)) {
			// Allow Base Rate?
			if (StringUtils.isBlank(finBaseRate)) {
				String[] valueParm = new String[2];
				valueParm[0] = REPAY;
				valueParm[1] = loanType;

				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90136", valueParm)));
			} else {
				// Base Rate code found?
				String brCode = repayBaseRate;
				String currency = finType.getFinCcy();
				// FIXME :: move to cache
				if (baseRateDAO.getBaseRateCountById(brCode, currency, "") <= 0) {
					String[] valueParm = new String[2];
					valueParm[0] = brCode;
					valueParm[1] = fm.getFinCcy();

					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90137", valueParm)));
				}

				// validate special rate code
				if (StringUtils.isNotBlank(repaySpecialRate)) {
					// FIXME :: move to cache
					if (splRateDAO.getSpecialRateCountById(repaySpecialRate, "") <= 0) {
						String[] valueParm = new String[2];
						valueParm[0] = repaySpecialRate;
						valueParm[1] = REPAY;

						errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90230", valueParm)));
					}
				}
			}
		} else if (repayProfitRate.compareTo(BigDecimal.ZERO) > 0 && StringUtils.isNotBlank(finBaseRate)) {
			String[] valueParm = new String[2];
			valueParm[0] = "repayPftRate";
			valueParm[1] = loanType;

			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90204", valueParm)));
		}

		// Special Rate code
		if (StringUtils.isNotBlank(repaySpecialRate) && StringUtils.isBlank(repayBaseRate)) {
			String[] valueParm = new String[1];
			valueParm[0] = REPAY;

			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90155", valueParm)));
		}

		// Margin
		if (fm.getRepayMargin().compareTo(BigDecimal.ZERO) != 0 && StringUtils.isBlank(repayBaseRate)) {
			String[] valueParm = new String[1];
			valueParm[0] = REPAY;

			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90155", valueParm)));
		}

		if (!errors.isEmpty()) {
			return;
		}

		// Validate Against Minimum and Maximum Rate
		BigDecimal rpyMinRate = fm.getRpyMinRate();
		BigDecimal rpyMaxRate = fm.getRpyMaxRate();
		if (rpyMinRate.compareTo(BigDecimal.ZERO) == 0 && rpyMaxRate.compareTo(BigDecimal.ZERO) == 0) {
			return;
		}

		if (StringUtils.isBlank(repayBaseRate)) {
			return;
		}

		// Base Rate
		RateDetail rate = new RateDetail();
		rate.setBaseRateCode(repayBaseRate);
		rate.setCurrency(fm.getFinCcy());
		rate.setSplRateCode(repaySpecialRate);
		rate.setMargin(fm.getRepayMargin());
		rate.setValueDate(appDate);
		RateUtil.getRefRate(rate);

		if (rate.getErrorDetails() != null) {
			errors.add(rate.getErrorDetails());
		}

		if (CollectionUtils.isNotEmpty(errors)) {
			return;
		}

		BigDecimal netRate = rate.getNetRefRateLoan();

		// Check Against Minimum Rate
		if (!FinanceConstants.PRODUCT_ODFACILITY.equals(productCategory) && netRate.compareTo(rpyMinRate) < 0) {
			String[] valueParm = new String[2];
			valueParm[0] = round4(netRate).toString();
			valueParm[1] = round4(rpyMinRate).toString();
			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90174", valueParm)));
		}

		// Check Against Maximum Rate
		if (rpyMaxRate.compareTo(zeroValue) != 0 && netRate.compareTo(rpyMaxRate) > 0) {
			String[] valueParm = new String[2];
			valueParm[0] = round4(netRate).toString();
			valueParm[1] = round4(rpyMaxRate).toString();
			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90175", valueParm)));
		}

	}

	/*
	 * _______________________________________________________________________________________________________________
	 * VALIDATE REPAY FREQUENCY AND NEXT PROFIT DATE
	 * _______________________________________________________________________________________________________________
	 */

	private void repayFrqValidation(FinScheduleData schdData) {
		List<ErrorDetail> errors = schdData.getErrorDetails();

		String moveNone = HolidayHandlerTypes.MOVE_NONE;

		FinanceMain fm = schdData.getFinanceMain();
		FinanceType finType = schdData.getFinanceType();

		String repayFrq = fm.getRepayFrq();
		String profitDaysBasis = fm.getProfitDaysBasis();
		Date nextRepayDate = fm.getNextRepayDate();
		Date calGrcEndDate = fm.getCalGrcEndDate();
		int numberOfTerms = fm.getNumberOfTerms();
		String productCategory = fm.getProductCategory();

		String frequencyDays = finType.getFrequencyDays();

		// Validate Repayment Frequency
		ErrorDetail error = FrequencyUtil.validateFrequency(repayFrq);

		if (error != null) {
			String[] valueParm = new String[2];
			valueParm[0] = "Repay";
			valueParm[1] = repayFrq;

			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90159", valueParm)));
		}

		if (!validateAlwFrqDays(repayFrq, frequencyDays)) {
			String[] valueParm = new String[3];
			valueParm[0] = "Repay";
			valueParm[1] = repayFrq;
			valueParm[2] = frequencyDays;

			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90271", valueParm)));
		}

		// Interest Days Basis should be 15E/360 ISDA OR Actual/365 Fixed when
		if (ImplementationConstants.FRQ_15DAYS_REQ) {
			String repay = FrequencyUtil.getFrequencyCode(repayFrq);
			if (StringUtils.isNotEmpty(repay)) {
				// Frequency Code
				if (!FrequencyCodeTypes.FRQ_15DAYS.equals(repay)
						&& CalculationConstants.IDB_15E360IA.equals(profitDaysBasis)) {
					String frqCode = Labels.getLabel("label_Select_15DAYS");
					String[] valueParm = new String[2];
					valueParm[0] = "Repay Frequency ";
					valueParm[1] = "'" + frqCode + "'";

					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90337", valueParm)));

					return;
				}

				// Interest Days Basis
				if (!CalculationConstants.IDB_15E360IA.equals(profitDaysBasis)
						&& FrequencyCodeTypes.FRQ_15DAYS.equals(repay)) {
					String label = Labels.getLabel("label_ProfitDaysBasis_15E_360IA");//
					String[] valueParm = new String[2];
					valueParm[0] = "profitDaysBasis";
					valueParm[1] = "'" + label + "'";

					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90337", valueParm)));

					return;

				}
			}
		}

		// First Repayment Date Vs Start Date
		if (nextRepayDate.compareTo(calGrcEndDate) <= 0) {
			String[] valueParm = new String[2];
			valueParm[0] = DateUtil.formatToShortDate(nextRepayDate);
			valueParm[1] = DateUtil.formatToShortDate(calGrcEndDate);

			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90192", valueParm)));
		}

		// Return if any error
		if (!errors.isEmpty()) {
			return;
		}

		Date baseDate = null;
		boolean isBaseDt = false;

		// Default Calculated Maturity Date using terms
		Date maturityDate = fm.getMaturityDate();
		if (numberOfTerms > 0) {
			if (!FinanceConstants.PRODUCT_ODFACILITY.equals(productCategory)) {
				fm.setCalTerms(numberOfTerms);
				baseDate = nextRepayDate;
				isBaseDt = true;

			} else {
				if (StringUtils.isNotBlank(repayFrq) && nextRepayDate != null) {
					baseDate = fm.getFinStartDate();
				}
			}

			FrequencyDetails frq = FrequencyUtil.getNextDate(repayFrq, numberOfTerms, baseDate, moveNone, isBaseDt);

			List<Calendar> schdDates = frq.getScheduleList();
			if (schdDates != null) {
				fm.setCalMaturity(DateUtil.getDatePart(schdDates.get(schdDates.size() - 1).getTime()));
			}
		} else {
			// Default Calculated Terms based on Maturity Date
			fm.setCalMaturity(maturityDate);
			fm.setCalTerms(FrequencyUtil.getTerms(repayFrq, nextRepayDate, maturityDate, true, true).getTerms());
		}

		// First Repayment Date Vs Maturity Date
		if (nextRepayDate.compareTo(fm.getCalMaturity()) > 0) {
			String[] valueParm = new String[2];
			valueParm[0] = DateUtil.formatToShortDate(nextRepayDate);
			valueParm[1] = DateUtil.formatToShortDate(maturityDate);

			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90193", valueParm)));
		}
	}

	/*
	 * _______________________________________________________________________________________________________________
	 * REPAY PROFIT FREQUENCY VALIDATION
	 * _______________________________________________________________________________________________________________
	 */

	private void repayPftFrqValidation(FinScheduleData schdData) {
		List<ErrorDetail> errors = schdData.getErrorDetails();
		FinanceMain fm = schdData.getFinanceMain();
		FinanceType finType = schdData.getFinanceType();

		String repayPftFrq = fm.getRepayPftFrq();
		String frequencyDays = finType.getFrequencyDays();
		Date nextRepayPftDate = fm.getNextRepayPftDate();
		Date calGrcEndDate = fm.getCalGrcEndDate();
		Date nextRepayDate = fm.getNextRepayDate();

		// Validate Profit Frequency Frequency
		ErrorDetail error = FrequencyUtil.validateFrequency(repayPftFrq);
		if (error != null) {
			String[] valueParm = new String[2];
			valueParm[0] = "Repay";
			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90156", valueParm)));
		}

		// Validate with Allowed frequency days.
		if (!validateAlwFrqDays(repayPftFrq, frequencyDays)) {
			String[] valueParm = new String[3];
			valueParm[0] = "Repay";
			valueParm[1] = repayPftFrq;
			valueParm[2] = frequencyDays;

			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90271", valueParm)));
		}

		// First Repayment Frequency Date Vs Start Date/Grace End Date
		if (nextRepayPftDate.compareTo(calGrcEndDate) <= 0) {
			String[] valueParm = new String[2];
			valueParm[0] = DateUtil.formatToShortDate(nextRepayPftDate);
			valueParm[1] = DateUtil.formatToShortDate(calGrcEndDate);

			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90194", valueParm)));
		}

		// First Interest Frequency Date Vs Next Repayment Date
		if (nextRepayPftDate.compareTo(nextRepayDate) > 0) {
			String[] valueParm = new String[2];
			valueParm[0] = DateUtil.formatToShortDate(nextRepayPftDate);
			valueParm[1] = DateUtil.formatToShortDate(nextRepayDate);

			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90195", valueParm)));
		}
	}

	/*
	 * _______________________________________________________________________________________________________________
	 * REPAY PROFIT REVIEW FREQUENCY VALIDATION
	 * _______________________________________________________________________________________________________________
	 */

	private void repayPftReviewValidation(FinScheduleData schdData) {
		List<ErrorDetail> errors = schdData.getErrorDetails();

		FinanceMain fm = schdData.getFinanceMain();
		FinanceType finType = schdData.getFinanceType();

		boolean finIsRvwAlw = finType.isFinIsRvwAlw();
		String frequencyDays = finType.getFrequencyDays();

		String repayRvwFrq = fm.getRepayRvwFrq();
		boolean allowRepayRvw = fm.isAllowRepayRvw();
		Date nextRepayRvwDate = fm.getNextRepayRvwDate();
		Date calGrcEndDate = fm.getCalGrcEndDate();
		Date calMaturity = fm.getCalMaturity();

		// Allow Profit Rate Review
		if (!finIsRvwAlw) {
			if (allowRepayRvw || StringUtils.isNotBlank(repayRvwFrq) || nextRepayRvwDate != null) {
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90196", null)));
			}

			return;
		}

		// Validate Profit Review Frequency
		ErrorDetail error = FrequencyUtil.validateFrequency(repayRvwFrq);

		if (error != null) {
			String[] valueParm = new String[2];
			valueParm[0] = "Repay";

			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90157", valueParm)));
		}

		// Validate with Allowed frequency days.
		if (!validateAlwFrqDays(repayRvwFrq, frequencyDays)) {
			String[] valueParm = new String[3];
			valueParm[0] = "Repay";
			valueParm[1] = repayRvwFrq;
			valueParm[2] = frequencyDays;

			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90271", valueParm)));
		}

		// First Repayment Profit Review Date Vs Start Date/Grace End Date
		if (nextRepayRvwDate.compareTo(calGrcEndDate) <= 0) {
			String[] valueParm = new String[2];
			valueParm[0] = DateUtil.formatToShortDate(nextRepayRvwDate);
			valueParm[1] = DateUtil.formatToShortDate(calGrcEndDate);

			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90197", valueParm)));
		}

		// First Repayment Profit Review Date Vs Maturity Date
		if (nextRepayRvwDate.compareTo(calMaturity) > 0) {
			String[] valueParm = new String[2];
			valueParm[0] = DateUtil.formatToShortDate(nextRepayRvwDate);
			valueParm[1] = DateUtil.formatToShortDate(calMaturity);

			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90198", valueParm)));
		}
	}

	/*
	 * _______________________________________________________________________________________________________________
	 * REPAY PROFIT CAPITALIZATION
	 * _______________________________________________________________________________________________________________
	 */

	private void repayPftCpzValidation(FinScheduleData schdData) {
		List<ErrorDetail> errors = schdData.getErrorDetails();

		FinanceMain fm = schdData.getFinanceMain();
		FinanceType finType = schdData.getFinanceType();

		boolean finIsIntCpz = finType.isFinIsIntCpz();
		String frequencyDays = finType.getFrequencyDays();

		String repayCpzFrq = fm.getRepayCpzFrq();
		Date nextRepayCpzDate = fm.getNextRepayCpzDate();
		boolean allowRepayCpz = fm.isAllowRepayCpz();
		Date calMaturity = fm.getCalMaturity();
		Date calGrcEndDate = fm.getCalGrcEndDate();

		// Allow Profit Capitalization
		if (!finIsIntCpz) {
			if (allowRepayCpz || StringUtils.isNotBlank(repayCpzFrq) || nextRepayCpzDate != null) {
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90199", null)));
			}

			return;
		}

		if (finIsIntCpz) {
			// Validate Profit Capitalization Frequency
			ErrorDetail error = FrequencyUtil.validateFrequency(repayCpzFrq);

			if (error != null) {
				String[] valueParm = new String[2];
				valueParm[0] = "Repay";

				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90158", valueParm)));
			}

			// Validate with Allowed frequency days.
			if (!validateAlwFrqDays(repayCpzFrq, frequencyDays)) {
				String[] valueParm = new String[3];
				valueParm[0] = "Repay";
				valueParm[1] = repayCpzFrq;
				valueParm[2] = frequencyDays;

				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90271", valueParm)));
			}

			/* First Interest Capitalization Frequency Date Vs Start Date/GE Date */
			if (nextRepayCpzDate.compareTo(calGrcEndDate) <= 0) {
				String[] valueParm = new String[2];
				valueParm[0] = DateUtil.formatToShortDate(nextRepayCpzDate);
				valueParm[1] = DateUtil.formatToShortDate(calGrcEndDate);

				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90225", valueParm)));
			}

			// First Interest Capitalization Frequency Date Vs M
			if (nextRepayCpzDate.compareTo(calMaturity) > 0) {
				String[] valueParm = new String[2];
				valueParm[0] = DateUtil.formatToShortDate(nextRepayCpzDate);
				valueParm[1] = DateUtil.formatToShortDate(calMaturity);

				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90226", valueParm)));
			}
		}

		return;
	}

	/*
	 * _______________________________________________________________________________________________________________
	 * RepayDates Validation
	 * _______________________________________________________________________________________________________________
	 */

	private void repayDatesValidation(FinScheduleData schdData) {
		List<ErrorDetail> errors = schdData.getErrorDetails();

		FinanceMain fm = schdData.getFinanceMain();

		String repayFrq = fm.getRepayFrq();
		String repayRvwFrq = fm.getRepayRvwFrq();
		String repayCpzFrq = fm.getRepayCpzFrq();
		String repayPftFrq = fm.getRepayPftFrq();

		Date nextRepayDate = fm.getNextRepayDate();
		Date nextRepayRvwDate = fm.getNextRepayRvwDate();
		Date nextRepayCpzDate = fm.getNextRepayCpzDate();
		Date nextRepayPftDate = fm.getNextRepayPftDate();

		Date maturityDate = fm.getMaturityDate();
		Date calMaturity = fm.getCalMaturity();

		// it should match with frequency
		if (StringUtils.isNotBlank(repayFrq) && nextRepayDate != null
				&& DateUtil.compare(maturityDate, nextRepayDate) != 0) {
			if (!FrequencyUtil.isFrqDate(repayFrq, nextRepayDate)) {
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90183", null)));
			}
		}

		// it should match with frequency
		if (StringUtils.isNotBlank(repayRvwFrq) && nextRepayRvwDate != null
				&& DateUtil.compare(calMaturity, nextRepayRvwDate) != 0) {
			if (!FrequencyUtil.isFrqDate(repayRvwFrq, nextRepayRvwDate)) {
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90181", null)));
			}
		}

		// it should match with frequency
		if ((StringUtils.isNotBlank(repayCpzFrq)) && (nextRepayCpzDate != null)
				&& DateUtil.compare(maturityDate, nextRepayCpzDate) != 0) {
			if (!FrequencyUtil.isFrqDate(repayCpzFrq, nextRepayCpzDate)) {
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90182", null)));
			}
		}

		if ((StringUtils.isNotBlank(repayPftFrq)) && (nextRepayPftDate != null)
				&& DateUtil.compare(maturityDate, nextRepayPftDate) != 0) {
			if (!FrequencyUtil.isFrqDate(repayPftFrq, nextRepayPftDate)) {
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90180", null)));
			}
		}
	}

	/*
	 * _______________________________________________________________________________________________________________
	 * BPI
	 * _______________________________________________________________________________________________________________
	 */

	private void bpiValidation(FinScheduleData schdData) {
		List<ErrorDetail> errors = schdData.getErrorDetails();
		FinanceMain fm = schdData.getFinanceMain();

		String bpiTreatment = fm.getBpiTreatment();
		if (!fm.isAlwBPI() && !FinanceConstants.BPI_NO.equals(bpiTreatment)) {
			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90229", null)));
			return;
		}

		String frqBPI = null;
		Date frqDate;

		if (fm.isAllowGrcPeriod()) {
			frqBPI = fm.getGrcPftFrq();
			frqDate = fm.getNextGrcPftDate();
		} else {
			frqBPI = fm.getRepayPftFrq();
			frqDate = fm.getNextRepayPftDate();
		}

		if (fm.isAlwBPI()) {
			FrequencyDetails frequency = FrequencyUtil.getNextDate(frqBPI, 1, fm.getFinStartDate(),
					HolidayHandlerTypes.MOVE_NONE, false);
			Date bpiDate = DateUtil.getDatePart(frequency.getNextFrequencyDate());
			if (DateUtil.compare(bpiDate, frqDate) >= 0) {
				fm.setBpiTreatment(FinanceConstants.BPI_NO);
			}
		}

		if (StringUtils.isNotBlank(bpiTreatment)) {
			if (!FinanceConstants.BPI_NO.equals(bpiTreatment) && !FinanceConstants.BPI_DISBURSMENT.equals(bpiTreatment)
					&& !FinanceConstants.BPI_SCHEDULE.equals(bpiTreatment)
					&& !FinanceConstants.BPI_CAPITALIZE.equals(bpiTreatment)
					&& !FinanceConstants.BPI_SCHD_FIRSTEMI.equals(bpiTreatment)) {
				String[] valueParm = new String[1];
				valueParm[0] = bpiTreatment;

				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90185", valueParm)));
			}
		}
	}

	/**
	 * Method for validate fee details
	 * 
	 * @param vldGroup
	 * @param finScheduleData
	 * @param isAPICall
	 * @return List<ErrorDetails>
	 */
	private List<ErrorDetail> feeValidations(String vldGroup, FinScheduleData schdData, boolean isAPICall,
			String eventCode) {
		List<ErrorDetail> errors = schdData.getErrorDetails();

		String finEvent = eventCode;
		boolean isOrigination = false;
		int vasFeeCount = 0;

		List<FinFeeDetail> fees = schdData.getFinFeeDetailList();

		FinanceMain fm = schdData.getFinanceMain();
		FinanceType finType = schdData.getFinanceType();

		int numberOfTerms = fm.getNumberOfTerms();

		if (!PennantConstants.VLD_SRV_LOAN.equals(vldGroup)) {
			for (FinFeeDetail fee : fees) {
				int feeTerms = fee.getTerms();

				if (AccountingEvent.VAS_FEE.equals(fee.getFinEvent())) {
					vasFeeCount++;
				}

				String feeScheduleMethod = fee.getFeeScheduleMethod();

				if (StringUtils.isBlank(feeScheduleMethod)) {
					String[] valueParm = new String[1];
					valueParm[0] = "feeMethod";
					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
					return errors;
				}
				// validate feeMethod
				if (!FinanceConstants.BPI_NO.equals(feeScheduleMethod)
						&& !CalculationConstants.REMFEE_PART_OF_DISBURSE.equals(feeScheduleMethod)
						&& !CalculationConstants.REMFEE_PART_OF_SALE_PRICE.equals(feeScheduleMethod)
						&& !CalculationConstants.REMFEE_SCHD_TO_FIRST_INSTALLMENT.equals(feeScheduleMethod)
						&& !CalculationConstants.REMFEE_SCHD_TO_ENTIRE_TENOR.equals(feeScheduleMethod)
						&& !CalculationConstants.REMFEE_SCHD_TO_N_INSTALLMENTS.equals(feeScheduleMethod)
						&& !CalculationConstants.REMFEE_PAID_BY_CUSTOMER.equals(feeScheduleMethod)
						&& !CalculationConstants.REMFEE_WAIVED_BY_BANK.equals(feeScheduleMethod)
						&& !CalculationConstants.FEE_SUBVENTION.equals(feeScheduleMethod)) {

					String[] valueParm = new String[2];
					valueParm[0] = feeScheduleMethod;
					valueParm[1] = feeScheduleErrorMsg();

					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90243", valueParm)));
				}

				// validate scheduleTerms
				if (CalculationConstants.REMFEE_SCHD_TO_N_INSTALLMENTS.equals(feeScheduleMethod)) {
					if (feeTerms <= 0) {
						String[] valueParm = new String[1];
						valueParm[0] = "ScheduleTerms";
						errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90221", valueParm)));
					} else if (feeTerms > numberOfTerms) {
						String[] valueParm = new String[2];
						valueParm[0] = "Schedule Terms";
						valueParm[1] = "Number of terms:" + numberOfTerms;
						errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("30568", valueParm)));
					}
				}
			}

			isOrigination = true;
			finEvent = PennantApplicationUtil.getEventCode(fm.getFinStartDate());
		} else {
			for (FinFeeDetail fee : fees) {
				String feeScheduleMethod = fee.getFeeScheduleMethod();

				if (StringUtils.isNotBlank(feeScheduleMethod) && !AccountingEvent.ADDDBSN.equals(eventCode)) {
					String[] valueParm = new String[2];
					valueParm[0] = "Fee Schedule Method";
					valueParm[1] = fee.getFeeTypeCode();
					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90269", valueParm)));
				}
			}
		}

		String loanType = null;
		int moduleId = 0;

		if (PennantConstants.VLD_SRV_LOAN.equals(vldGroup) || !finType.isPromotionType()) {
			loanType = fm.getFinType();
			moduleId = FinanceConstants.MODULEID_FINTYPE;
		} else {
			loanType = finType.getPromotionCode();
			moduleId = FinanceConstants.MODULEID_PROMOTION;
		}

		List<FinTypeFees> finTypeFeeList = financeDetailService.getFinTypeFees(loanType, finEvent, isOrigination,
				moduleId);

		if (!finTypeFeeList.isEmpty()) {
			if (finTypeFeeList.size() != fees.size() - vasFeeCount) {
				String[] valueParm = new String[1];
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90244", valueParm)));
				return errors;
			}

			for (FinFeeDetail feeDetail : fees) {
				BigDecimal finWaiverAmount = BigDecimal.ZERO;
				boolean isFeeCodeFound = false;
				String feeTypeCode = StringUtils.trimToEmpty(extractFeeCode(feeDetail.getFeeTypeCode()));
				BigDecimal actualAmount = feeDetail.getActualAmount();
				BigDecimal paidAmount = feeDetail.getPaidAmount();
				BigDecimal waivedAmount = feeDetail.getWaivedAmount();
				String feeEvent = feeDetail.getFinEvent();
				String feeScheduleMethod = StringUtils.trimToEmpty(feeDetail.getFeeScheduleMethod());

				for (FinTypeFees finTypeFee : finTypeFeeList) {
					String feeTypeCode2 = extractFeeCode(finTypeFee.getFeeTypeCode());
					String finTypeSchdMtd = finTypeFee.getFeeScheduleMethod();
					boolean alwModifyFeeSchdMthd = finTypeFee.isAlwModifyFeeSchdMthd();

					if (feeTypeCode.equals(feeTypeCode2) || AccountingEvent.VAS_FEE.equals(feeEvent)) {
						isFeeCodeFound = true;

						// validate negative values
						if (actualAmount.compareTo(BigDecimal.ZERO) < 0 || paidAmount.compareTo(BigDecimal.ZERO) < 0
								|| waivedAmount.compareTo(BigDecimal.ZERO) < 0) {
							String[] valueParm = new String[1];
							valueParm[0] = feeTypeCode;
							errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90259", valueParm)));
							return errors;
						}

						// validate fee schedule method
						if ((!AccountingEvent.VAS_FEE.equals(feeEvent))
								&& (!alwModifyFeeSchdMthd && !feeScheduleMethod.equals(finTypeSchdMtd))) {
							String[] valueParm = new String[1];
							valueParm[0] = feeTypeCode;
							errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90246", valueParm)));
							return errors;
						}

						// validate paid by Customer method
						if (CalculationConstants.REMFEE_PAID_BY_CUSTOMER.equals(finTypeSchdMtd)) {
							if (paidAmount.compareTo(finTypeFee.getAmount()) != 0 && !isAPICall) {
								String[] valueParm = new String[1];
								valueParm[0] = feeTypeCode;
								errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90254", valueParm)));
								return errors;
							}
						}
						// validate waived by bank method
						if (CalculationConstants.REMFEE_WAIVED_BY_BANK.equals(finTypeSchdMtd)) {
							if (waivedAmount.compareTo(finWaiverAmount) != 0) {
								String[] valueParm = new String[3];
								valueParm[0] = "Waiver amount";
								valueParm[1] = "Actual waiver amount:" + String.valueOf(finWaiverAmount);
								valueParm[2] = feeTypeCode;
								errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90258", valueParm)));
								return errors;
							}
						}
					}
				}
				if (!isFeeCodeFound) {
					String[] valueParm = new String[1];
					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90247", valueParm)));
					return errors;
				}
			}
		} else {
			// If we pass vas FEES details with out Loan type FEES configuration and VAS details in VAS block
			List<VASRecording> vasRecordingList = schdData.getVasRecordingList();
			List<String> feeCodes = getVasFeeCodes(schdData);
			if (CollectionUtils.isEmpty(vasRecordingList) && CollectionUtils.isNotEmpty(feeCodes)) {
				for (FinFeeDetail finFeeDetail : fees) {
					// Setting validation for vas recording and fees block
					String feeTypeCode = finFeeDetail.getFeeTypeCode();
					feeTypeCode = extractFeeCode(feeTypeCode);
					if (feeCodes.contains(feeTypeCode)) {
						errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90328", null)));
						return errors;
					} else {
						// Setting validation fees
						String[] valueParm = new String[1];
						valueParm[0] = fm.getFinType();
						errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90245", valueParm)));
						return errors;
					}
				}
				// Validation if no Fee configured in Loan type except vas and providing invalid fee code in Fee Block
			} else if (CollectionUtils.isNotEmpty(feeCodes)) {
				for (FinFeeDetail finFeeDetail : fees) {
					String feeTypeCode = finFeeDetail.getFeeTypeCode();
					feeTypeCode = extractFeeCode(feeTypeCode);
					if (!feeCodes.contains(feeTypeCode)) {
						errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90244", null)));
						return errors;
					}
				}
			}
		}

		return errors;
	}

	private void stepValidations(FinScheduleData schdData) {
		List<ErrorDetail> errors = schdData.getErrorDetails();

		FinanceMain fm = schdData.getFinanceMain();
		List<FinanceStepPolicyDetail> stepPolicyDetails = schdData.getStepPolicyDetails();

		BigDecimal emiPercTotal = BigDecimal.ZERO;
		int totalSteps = 0;

		String stepType = fm.getStepType();

		if (PennantConstants.STEPPING_CALC_PERC.equals(fm.getCalcOfSteps())) {
			// validate number of steps
			if (stepPolicyDetails.size() < 2) {
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90233", null)));
				return;
			}

			if (!fm.isAlwManualSteps()) {
				if (fm.getNoOfSteps() != 0) {
					String[] valueParm = new String[2];
					valueParm[0] = "NoOfSteps";
					valueParm[1] = "Zero";
					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90277", valueParm)));
					return;
				}
			} else {
				if (fm.getNoOfSteps() == 0) {
					String[] valueParm = new String[2];
					valueParm[0] = "NoOfSteps";
					valueParm[1] = "Zero";
					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("91121", valueParm)));
					return;
				}
			}

			if (fm.getNoOfGrcSteps() != 0) {
				String[] valueParm = new String[2];
				valueParm[0] = "NoOfGrcSteps";
				valueParm[1] = "Zero";
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90277", valueParm)));
				return;
			}

			if (fm.getNoOfSteps() != schdData.getStepPolicyDetails().size()) {
				String[] valueParm = new String[2];
				valueParm[0] = Labels.getLabel("label_FinanceMainDialog_RepaySteps.value");
				valueParm[1] = Labels.getLabel("label_FinanceMainDialog_RepaySteps.value");
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("STP005", valueParm)));
				return;
			}

			for (FinanceStepPolicyDetail policyDetail : stepPolicyDetails) {
				if (policyDetail.getEmiSplitPerc().compareTo(BigDecimal.ZERO) <= 0) {
					String[] valueParm = new String[2];
					valueParm[0] = "EMI split perc";
					valueParm[1] = "zero";
					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("91121", valueParm)));
					return;
				} else if (policyDetail.getSteppedEMI().compareTo(BigDecimal.ZERO) != 0) {
					String[] valueParm = new String[2];
					valueParm[0] = "Stepped EMI";
					valueParm[1] = "Zero";
					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90277", valueParm)));
					return;
				} else if (StringUtils.isNotBlank(policyDetail.getStepSpecifier())) {
					String[] valueParm = new String[2];
					valueParm[0] = "Step Specifier";
					valueParm[1] = "step calculated on percentage";
					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("STP006", valueParm)));
					return;
				} else if (policyDetail.isAutoCal()) {
					String[] valueParm = new String[1];
					valueParm[0] = "Step No " + policyDetail.getStepNo();
					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("STP004", valueParm)));
					return;
				} else if (policyDetail.getInstallments() == 0) {
					String[] valueParm = new String[2];
					valueParm[0] = "Installments";
					valueParm[1] = "Zero";
					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("91121", valueParm)));
					return;
				}
			}

			for (FinanceStepPolicyDetail policyDetail : stepPolicyDetails) {
				emiPercTotal = emiPercTotal.add(policyDetail.getEmiSplitPerc());
				totalSteps++;
			}

			if (FinanceConstants.STEPTYPE_EMI.equals(stepType)) {
				BigDecimal emiPerc = emiPercTotal.divide(new BigDecimal(totalSteps), RoundingMode.HALF_UP);
				if (emiPerc.compareTo(new BigDecimal(100)) != 0) {
					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90235", null)));
					return;
				}
			} else if (FinanceConstants.STEPTYPE_PRIBAL.equals(stepType)) {
				BigDecimal priPerc = emiPercTotal;
				if (priPerc.compareTo(new BigDecimal(100)) != 0) {
					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90236", null)));
					return;
				}
			}
		}

		if (PennantConstants.STEPPING_CALC_AMT.equals(fm.getCalcOfSteps())) {
			if (!fm.isAlwManualSteps()) {
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("STP009", null)));
				return;
			}

			if (StringUtils.isNotBlank(fm.getStepType())) {
				String[] valueParm = new String[2];
				valueParm[0] = "Step type";
				valueParm[1] = "step calculated on amount";
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("STP006", valueParm)));
				return;
			}

			if (StringUtils.isNotBlank(fm.getStepPolicy())) {
				String[] valueParm = new String[2];
				valueParm[0] = "Step policy";
				valueParm[1] = "step calculated on amount";
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("STP006", valueParm)));
				return;
			}

			if (CollectionUtils.isEmpty(stepPolicyDetails)) {
				String[] valueParm = new String[1];
				valueParm[0] = "Step";
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
				return;
			}

			// Mandatory Data validations
			for (FinanceStepPolicyDetail spd : stepPolicyDetails) {
				if (spd.getStepNo() == 0) {
					String[] valueParm = new String[2];
					valueParm[0] = "Step Number";
					valueParm[1] = "Zero";
					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("91121", valueParm)));
					return;
				} else if (StringUtils.isBlank(spd.getStepSpecifier())) {
					String[] valueParm = new String[1];
					valueParm[0] = "Step Speicfier";
					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("WFEE08", valueParm)));
					return;
				} else if (!(PennantConstants.STEP_SPECIFIER_GRACE.equals(spd.getStepSpecifier())
						|| PennantConstants.STEP_SPECIFIER_REG_EMI.equals(spd.getStepSpecifier()))) {
					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("STP007", null)));
					return;
				} else if (spd.getInstallments() == 0) {
					String[] valueParm = new String[2];
					valueParm[0] = "Number of Installments";
					valueParm[1] = "Zero";
					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("91121", valueParm)));
					return;
				} else if (spd.getRateMargin().compareTo(BigDecimal.ZERO) != 0) {
					String[] valueParm = new String[2];
					valueParm[0] = "Rate margin";
					valueParm[1] = "Zero";
					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90277", valueParm)));
					return;
				} else if (spd.getEmiSplitPerc().compareTo(BigDecimal.ZERO) != 0) {
					String[] valueParm = new String[2];
					valueParm[0] = "EMI split perc";
					valueParm[1] = "Zero";
					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90277", valueParm)));
					return;
				}
			}

			List<FinanceStepPolicyDetail> graceSpdList = new ArrayList<>();
			List<FinanceStepPolicyDetail> rpySpdList = new ArrayList<>();
			int rpyTerms = 0;
			int grcTerms = 0;
			int repaySteps = 0;
			int grcSteps = 0;

			fm.setRpyStps(false);
			fm.setGrcStps(false);
			for (FinanceStepPolicyDetail spd : stepPolicyDetails) {
				if (PennantConstants.STEP_SPECIFIER_REG_EMI.equals(spd.getStepSpecifier())) {
					repaySteps = repaySteps + 1;
					rpyTerms = rpyTerms + spd.getInstallments();
					rpySpdList.add(spd);
					fm.setRpyStps(true);
				} else if (PennantConstants.STEP_SPECIFIER_GRACE.equals(spd.getStepSpecifier())) {
					grcSteps = grcSteps + 1;
					graceSpdList.add(spd);
					grcTerms = grcTerms + spd.getInstallments();
					fm.setGrcStps(true);
				}
			}

			if (StringUtils.equals(fm.getStepsAppliedFor(), PennantConstants.STEPPING_APPLIED_EMI)
					&& CollectionUtils.isEmpty(rpySpdList)) {
				String[] valueParm = new String[2];
				valueParm[0] = "Repay steps";
				valueParm[1] = Labels.getLabel("label_FinanceTypeDialog_StepsAppliedFor_RgrEMIOnly.value");
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("STP012", valueParm)));
				return;
			}

			if (StringUtils.equals(fm.getStepsAppliedFor(), PennantConstants.STEPPING_APPLIED_GRC)
					&& CollectionUtils.isEmpty(graceSpdList)) {
				String[] valueParm = new String[2];
				valueParm[0] = "Grace steps";
				valueParm[1] = Labels.getLabel("label_FinanceTypeDialog_StepsAppliedFor_GrcPeriodOnly.value");
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("STP012", valueParm)));
				return;
			}

			if (PennantConstants.STEPPING_APPLIED_GRC.equals(fm.getStepsAppliedFor())
					&& CollectionUtils.isNotEmpty(rpySpdList)) {
				String[] valueParm = new String[2];
				valueParm[0] = "Grace steps";
				valueParm[1] = Labels.getLabel("label_FinanceTypeDialog_StepsAppliedFor_GrcPeriodOnly.value");
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("STP012", valueParm)));
				return;
			}

			if (PennantConstants.STEPPING_APPLIED_EMI.equals(fm.getStepsAppliedFor())
					&& CollectionUtils.isNotEmpty(graceSpdList)) {
				String[] valueParm = new String[2];
				valueParm[0] = "Repay steps";
				valueParm[1] = Labels.getLabel("label_FinanceTypeDialog_StepsAppliedFor_RgrEMIOnly.value");
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("STP012", valueParm)));
				return;
			}

			if (PennantConstants.STEPPING_APPLIED_GRC.equals(fm.getStepsAppliedFor())
					&& CollectionUtils.isNotEmpty(rpySpdList)) {
				String[] valueParm = new String[2];
				valueParm[0] = "Grace steps";
				valueParm[1] = fm.getFinType();
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("STP0010", valueParm)));
				return;
			}

			if (PennantConstants.STEPPING_APPLIED_EMI.equals(fm.getStepsAppliedFor())
					&& CollectionUtils.isNotEmpty(graceSpdList)) {
				String[] valueParm = new String[2];
				valueParm[0] = "Repay steps";
				valueParm[1] = fm.getFinType();
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("STP0010", valueParm)));
				return;
			}

			if (!fm.isAllowGrcPeriod() && CollectionUtils.isNotEmpty(graceSpdList)) {
				String[] valueParm = new String[2];
				valueParm[0] = "Grace steps";
				valueParm[1] = "No Grace Period LAN";
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("STP006", valueParm)));
				return;
			}

			if (fm.getNoOfSteps() != rpySpdList.size()) {
				String[] valueParm = new String[2];
				valueParm[0] = Labels.getLabel("label_FinanceMainDialog_RepaySteps.value");
				valueParm[1] = Labels.getLabel("label_FinanceMainDialog_RepaySteps.value");
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("STP005", valueParm)));
				return;
			}

			if (fm.getNoOfGrcSteps() != graceSpdList.size()) {
				String[] valueParm = new String[2];
				valueParm[0] = Labels.getLabel("label_FinanceMainDialog_GrcSteps.value");
				valueParm[1] = Labels.getLabel("label_FinanceMainDialog_GrcSteps.value");
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("STP005", valueParm)));
				return;
			}

			if (CollectionUtils.isNotEmpty(rpySpdList) && fm.getNumberOfTerms() != rpyTerms) {
				String[] valueParm = new String[2];
				valueParm[0] = Labels.getLabel("label_TotStepInstallments", new String[] { "Repay" });
				valueParm[1] = Labels.getLabel("label_TotalTerms");
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("30540", valueParm)));
				return;
			}

			if (CollectionUtils.isNotEmpty(graceSpdList) && fm.getGraceTerms() != grcTerms) {
				String[] valueParm = new String[2];
				valueParm[0] = Labels.getLabel("label_TotStepInstallments", new String[] { "Grace" });
				valueParm[1] = Labels.getLabel("label_GrcTotalTerms");
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("30540", valueParm)));
				return;
			}

			// Duplicate step number validation for repay steps
			FinanceStepPolicyDetail duplicateStep = null;
			duplicateStep = checkDuplicateStp(rpySpdList);
			if (duplicateStep == null) {
				duplicateStep = checkDuplicateStp(graceSpdList);
			}
			if (duplicateStep != null) {
				String[] valueParm = new String[2];
				valueParm[0] = "Step no " + duplicateStep.getStepNo();
				valueParm[1] = "step specifier " + duplicateStep.getStepSpecifier();
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("41015", valueParm)));
				return;
			}

			// Step No and Amount validation For Repay steps
			for (FinanceStepPolicyDetail spd : rpySpdList) {
				if (spd.getStepNo() > repaySteps) {
					String[] valueParm = new String[1];
					valueParm[0] = "Repay step no" + " " + String.valueOf(spd.getStepNo());
					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90405", valueParm)));
					return;
				} else if (spd.getStepNo() < repaySteps && spd.getSteppedEMI().compareTo(BigDecimal.ZERO) <= 0) {
					String[] valueParm = new String[2];
					valueParm[0] = Labels.getLabel("label_FinStepPolicyDialog_SteppedEMI.value");
					valueParm[1] = String.valueOf(BigDecimal.ZERO);
					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("91121", valueParm)));
					return;
				} else if (spd.getStepNo() == repaySteps && spd.getSteppedEMI().compareTo(BigDecimal.ZERO) > 0) {
					String[] valueParm = new String[2];
					valueParm[0] = "Last " + Labels.getLabel("label_FinStepPolicyDialog_SteppedEMI.value");
					valueParm[1] = String.valueOf(BigDecimal.ZERO) + " in repay steps";
					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90277", valueParm)));
					return;
				} else if (spd.isAutoCal()) {
					String[] valueParm = new String[1];
					valueParm[0] = "Repay Step No " + String.valueOf(spd.getStepNo());
					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("STP004", valueParm)));
					return;
				}
			}

			// Step no and Amount Validation For Grace steps
			for (FinanceStepPolicyDetail spd : graceSpdList) {
				if (spd.getStepNo() > grcSteps) {
					String[] valueParm = new String[1];
					valueParm[0] = "Grace step no" + " " + String.valueOf(spd.getStepNo());
					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90405", valueParm)));
					return;
				} else if (spd.getStepNo() < grcSteps && spd.isAutoCal()) {
					String[] valueParm = new String[1];
					valueParm[0] = Labels.getLabel("listheader_StepFinanceGrace_StepNo.label") + " "
							+ String.valueOf(spd.getStepNo());
					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("STP004", valueParm)));
					return;
				} else if (spd.isAutoCal() && spd.getSteppedEMI().compareTo(BigDecimal.ZERO) != 0) {
					String[] valueParm = new String[2];
					valueParm[0] = "Stepped EMI";
					valueParm[1] = "zero for auto calculate step";
					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90277", valueParm)));
					return;
				}
			}
		}
	}

	private FinanceStepPolicyDetail checkDuplicateStp(List<FinanceStepPolicyDetail> spdList) {
		if (CollectionUtils.isNotEmpty(spdList)) {
			Map<Integer, FinanceStepPolicyDetail> newMap = new HashMap<>();
			for (FinanceStepPolicyDetail spd : spdList) {
				if (newMap.containsKey(spd.getStepNo())) {
					return spd;
				} else {
					newMap.put(spd.getStepNo(), spd);
				}
			}
		}
		return null;
	}

	public List<ErrorDetail> doFeeValidations(String vldSrvLoan, FinServiceInstruction fsi, String eventCode) {
		FinScheduleData schdData = new FinScheduleData();

		List<FinFeeDetail> finFeeDetails = fsi.getFinFeeDetails();

		if (finFeeDetails == null) {
			finFeeDetails = new ArrayList<FinFeeDetail>();
		}

		schdData.setFinFeeDetailList(finFeeDetails);
		FinanceMain fm = financeMainDAO.getFinanceMainById(fsi.getFinID(), "", fsi.isWif());
		schdData.setFinanceMain(fm);

		return feeValidations(vldSrvLoan, schdData, true, eventCode);
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
					List<JointAccountDetail> jointAccountDetails = financeDetail.getJointAccountDetailList();
					if (jointAccountDetails != null && !jointAccountDetails.isEmpty()) {
						for (JointAccountDetail coApplicant : jointAccountDetails) {
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
			int installmentMnts = DateUtil.getMonthsBetweenInclusive(financeMain.getFinStartDate(),
					financeMain.getMaturityDate());
			if (installmentMnts > 0) {
				curFinRepayAmt = totalRepayAmount.divide(new BigDecimal(installmentMnts), 0, RoundingMode.HALF_DOWN);
			}
			int months = DateUtil.getMonthsBetween(financeMain.getFinStartDate(), financeMain.getMaturityDate());

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
					int custMonthsofExp = DateUtil.getMonthsBetween(
							detail.getCustomerDetails().getCustEmployeeDetail().getEmpFrom(),
							SysParamUtil.getAppDate());
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
							int custMonthsofExp = DateUtil.getMonthsBetween(custEmpFromDate, SysParamUtil.getAppDate());
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
					financeMain.getFinCcy(), curFinRepayAmt, months, null, detail.getJointAccountDetailList()));

			detail.getCustomerEligibilityCheck().setReqFinAmount(financeMain.getFinAmount());
			detail.getCustomerEligibilityCheck()
					.setDisbursedAmount(financeMain.getFinAmount().subtract(financeMain.getDownPayment()));
			detail.getCustomerEligibilityCheck().setReqFinType(financeMain.getFinType());
			detail.getCustomerEligibilityCheck().setFinProfitRate(financeMain.getEffectiveRateOfReturn());
			detail.getCustomerEligibilityCheck().setDownpayBank(financeMain.getDownPayBank());
			detail.getCustomerEligibilityCheck().setDownpaySupl(financeMain.getDownPaySupl());
			detail.getCustomerEligibilityCheck().setStepFinance(financeMain.isStepFinance());
			detail.getCustomerEligibilityCheck().setFinRepayMethod(financeMain.getFinRepayMethod());
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
					DateUtil.getYearsBetween(financeMain.getFinStartDate(), financeMain.getGrcPeriodEndDate()));

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
			detail.getFinScheduleData().setFinanceMain(financeMain);
		}
		return detail;
	}

	private void doValidateFees(FinScheduleData schdData, boolean stp) {
		if (CollectionUtils.isNotEmpty(schdData.getFinFeeDetailList())) {
			feeValidations(PennantConstants.VLD_CRT_LOAN, schdData, true, "");
		}
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
				if (StringUtils.isNotBlank(pslDetail.getSubCategory())) {
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

	private boolean isCovenantDocumentExist(List<DocumentDetails> documents, String docType) {
		if (CollectionUtils.isEmpty(documents)) {
			return false;
		}

		for (DocumentDetails document : documents) {
			if (StringUtils.equals(docType, document.getDocCategory())) {
				if (StringUtils.equals(PennantConstants.RECORD_TYPE_CAN, document.getRecordType())) {
					continue;
				}
				return true;
			}
		}
		return false;
	}

	// CovenantValidations
	public List<ErrorDetail> covenantValidation(FinanceMain financeMain, List<Covenant> covenantsList, String module) {
		logger.debug(Literal.ENTERING);
		List<ErrorDetail> errorDetails = new ArrayList<>();
		CovenantType aCovenantType = null;
		// Category Validations
		if (covenantsList.size() > 1) {
			Set<String> uniqueCovenantSet = new HashSet<>();
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
				List<Object> keys = new ArrayList<>();
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
			// validating the covenant Id
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
			default:
				break;
			}

			boolean isPdd = StringUtils.equals("Y", covenant.getStrPdd());
			boolean isOtc = StringUtils.equals("Y", covenant.getStrOtc());

			// validating the PDD
			if (covenant.getStrPdd() != null && !(isPdd || StringUtils.equals("N", covenant.getStrPdd()))) {
				String[] valueParm = new String[2];
				valueParm[0] = "Pdd";
				valueParm[1] = "Y or N";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90337", valueParm)));
				return errorDetails;
			}

			// validating the OTC
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
				SecurityRole secRole = finCovenantTypeDAO.isMandRoleExists(covenant.getMandatoryRole(), null);
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

			// validating the Receivable Date
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
			// validating the Frequency
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

			// validating the Alerts
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

			// validating the alerts required fields---non mandatory field
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
			// validating the alerts required field
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
			// validating the CovenantDocuments
			List<CovenantDocument> covenantDocumentsList = covenant.getCovenantDocuments();
			for (CovenantDocument covenantDocument : covenantDocumentsList) {

				if (StringUtils.isBlank(covenantDocument.getDoctype())) {
					String[] valueParm = new String[1];
					valueParm[0] = "DocType";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
					return errorDetails;
				}
				String category = documentTypeDAO.getDocCategoryByDocType(covenantDocument.getDoctype());
				if (category == null) {
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
			Set<String> documentTypeSet = new HashSet<>();

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

		while (DateUtil.compare(tempStartDate, tempEndDate) <= 0) {
			// String key = DateUtil.format(tempStartDate, DateFormat.LONG_DATE);
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

		if (DateUtil.compare(appDate, frequencyDate) < 0) {
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
		} else if ("O".equals(strFrequencyType) && covenant != null && !covenant.isAlertsRequired()) {
			frequencyDate = null;
		}

		if (covenant != null) {
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
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * All the java script validations moved from FinanceMain.xml to below method.
	 * 
	 * @param fd
	 */
	public void doBasicMandatoryValidations(FinanceDetail fd) {
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		List<ErrorDetail> errors = new ArrayList<>();

		if (StringUtils.isEmpty(fm.getFinType())) {
			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90126")));
		}

		validateBigDecimal(fm.getDownPayBank(), "DownPayBank", errors);
		validateBigDecimal(fm.getDownPaySupl(), "DownPaySupl", errors);
		validateBigDecimal(fm.getGrcPftRate(), "GrcPftRate", errors);
		validateBigDecimal(fm.getGrcMargin(), "GrcMargin", errors);
		validateBigDecimal(fm.getGrcMinRate(), "GrcMinRate", errors);
		validateBigDecimal(fm.getGrcMaxRate(), "GrcMaxRate", errors);
		validateBigDecimal(fm.getGrcMaxAmount(), "GrcMaxAmount", errors);
		validateBigDecimal(fm.getReqRepayAmount(), "ReqRepayAmount", errors);
		validateBigDecimal(fm.getRepayProfitRate(), "RepayProfitRate", errors);
		validateBigDecimal(fm.getRpyMinRate(), "RpyMinRate", errors);
		validateBigDecimal(fm.getRpyMaxRate(), "RpyMaxRate", errors);
		validateBigDecimal(fm.getFinAssetValue(), "FinAssetValue", errors);

		ServiceExceptionDetails exceptions[] = new ServiceExceptionDetails[errors.size()];

		int errorCount = 0;
		for (ErrorDetail error : errors) {
			ServiceExceptionDetails exception = new ServiceExceptionDetails();

			exception.setFaultCode(error.getCode());
			exception.setFaultMessage(error.getError());

			exceptions[errorCount++] = exception;
		}

		if (exceptions.length > 0) {
			throw new ServiceException(exceptions);
		}
	}

	private void validateBigDecimal(BigDecimal amount, String field, List<ErrorDetail> errors) {
		if (amount == null) {
			String[] valueParm = new String[1];
			valueParm[0] = field;
			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90242", valueParm)));
		}
	}

	public void setBaseRateDAO(BaseRateDAO baseRateDAO) {
		this.baseRateDAO = baseRateDAO;
	}

	public void setBranchService(BranchService branchService) {
		this.branchService = branchService;
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
	 * @param extendedFieldDetailsService the extendedFieldDetailsService to set
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

	public void setFinanceWorkFlowService(FinanceWorkFlowService financeWorkFlowService) {
		this.financeWorkFlowService = financeWorkFlowService;
	}

	public void setChequeHeaderService(ChequeHeaderService chequeHeaderService) {
		this.chequeHeaderService = chequeHeaderService;
	}
}
