package com.pennanttech.ws.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.FrequencyUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.applicationmaster.BaseRateDAO;
import com.pennant.backend.dao.applicationmaster.FlagDAO;
import com.pennant.backend.dao.rulefactory.RuleDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.applicationmaster.BankDetail;
import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.applicationmaster.RelationshipOfficer;
import com.pennant.backend.model.applicationmasters.Flag;
import com.pennant.backend.model.bmtmasters.BankBranch;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.GuarantorDetail;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.model.financemanagement.FinFlagsDetail;
import com.pennant.backend.model.mandate.Mandate;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.model.systemmasters.DocumentType;
import com.pennant.backend.service.applicationmaster.BankDetailService;
import com.pennant.backend.service.applicationmaster.BranchService;
import com.pennant.backend.service.applicationmaster.RelationshipOfficerService;
import com.pennant.backend.service.bmtmasters.BankBranchService;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.rmtmasters.FinanceTypeService;
import com.pennant.backend.service.systemmasters.DocumentTypeService;
import com.pennant.backend.service.systemmasters.GeneralDepartmentService;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.RuleConstants;
import com.pennanttech.util.APIConstants;

@Service("financeValidationService")
public class FinanceValidationService {
	private static final Logger logger = Logger.getLogger(FinanceValidationService.class);

	private CustomerDetailsService customerDetailsService;
	private DocumentTypeService documentTypeService;
	private FinanceTypeService financeTypeService;
	private BankBranchService bankBranchService;
	private BranchService branchService;
	private BankDetailService bankDetailService;
	private BaseRateDAO baseRateDAO;
	private FlagDAO flagDAO;
	private RuleDAO ruleDAO;
	private GeneralDepartmentService generalDepartmentService;
	private RelationshipOfficerService relationshipOfficerService;
	

	/**
	 * Validate the finance detail object.<br>
	 * 	Which includes below details.<br>
	 * 	Basic details.<br>
	 * 	Grace details.<br>
	 * 	Repay details.
	 * 
	 * @param financeMain
	 * @return WSReturnStatus
	 */
	public WSReturnStatus doFinanceDetailValidations(FinanceMain financeMain) {
		logger.debug("Entering");

		WSReturnStatus returnStatus = new WSReturnStatus();
		if (financeMain != null) {
			// validate customer
			String custCIF = financeMain.getLovDescCustCIF();

			if (StringUtils.isNotBlank(custCIF)) {
				Customer customer = customerDetailsService.getCustomerByCIF(custCIF);
				if (customer == null) {
					String[] valueParm = new String[1];
					valueParm[0] = custCIF;
					return getErrorDetails("90101", valueParm);
				}
			}

			// Finance start date
			Date appDate = DateUtility.getAppDate();
			Date minReqFinStartDate = DateUtility.addDays(appDate, -SysParamUtil.getValueAsInt("BACKDAYS_STARTDATE"));
			if(financeMain.getFinStartDate().compareTo(minReqFinStartDate) < 0) {
				String[] valueParm = new String[2];
				valueParm[0] = "Loan Start Date";
				valueParm[1] = DateUtility.formatDate(minReqFinStartDate, PennantConstants.XMLDateFormat);
				return getErrorDetails("90205", valueParm);
			}
			
			// validate finance branch
			if(StringUtils.isNotBlank(financeMain.getFinBranch())) {
				Branch branch = branchService.getApprovedBranchById(financeMain.getFinBranch());
				if(branch == null) {
					String[] valueParm = new String[1];
					valueParm[0] = financeMain.getFinBranch();
					return getErrorDetails("90501", valueParm);
				}
			}
			
			// validate FinanceType
			FinanceType financeType = financeTypeService.getApprovedFinanceTypeById(financeMain.getFinType());
			if (financeType == null) {
				String[] valueParm = new String[1];
				valueParm[0] = financeMain.getFinType();
				return getErrorDetails("90202", valueParm);
			}
			
			// validate currency
			if (StringUtils.isNotBlank(financeMain.getFinCcy())) {
				Currency currency = CurrencyUtil.getCurrencyObject(financeMain.getFinCcy());
				if (currency == null) {
					String[] valueParm = new String[1];
					valueParm[0] = financeMain.getFinCcy();
					return getErrorDetails("90120", valueParm);
				}
			}
			
			//finAssetValue
			if(financeMain.getFinAssetValue().compareTo(BigDecimal.ZERO) > 0)
				if(financeMain.getFinAmount().compareTo(financeMain.getFinAssetValue()) > 0) {
				 {
					String[] valueParm = new String[4];
					valueParm[0] = "finAmount";
					valueParm[1] = "finAssetValue";
					return getErrorDetails("90220", valueParm);
				}
			}
			
			// DownPayBank
			if(financeMain.getDownPayBank().compareTo(BigDecimal.ZERO) > 0 || 
					financeMain.getDownPaySupl().compareTo(BigDecimal.ZERO) > 0) {
				if(!financeType.isFinIsDwPayRequired()) {
					String[] valueParm = new String[4];
					valueParm[0] = "Down pay bank";
					valueParm[1] = "Supplier";
					valueParm[2] = financeMain.getFinType();
					return getErrorDetails("90203", valueParm);
				}
			}
			// Planned deferments
			if(financeMain.getPlanDeferCount() > 0) {
				if(!financeType.isAlwPlanDeferment()) {
					String[] valueParm = new String[2];
					valueParm[0] = "Planned deferments";
					valueParm[1] = financeMain.getFinType();
					return getErrorDetails("90204", valueParm);
				}
			}
			// FinRepayPftOnFrq
			if(financeMain.isFinRepayPftOnFrq()) {
				if(!financeType.isFinRepayPftOnFrq()) {
					String[] valueParm = new String[2];
					valueParm[0] = "FinRepayPftOnFrq";
					valueParm[1] = financeMain.getFinType();
					return getErrorDetails("90204", valueParm);
				}
			}
			
			// Allow grace
			if (financeMain.isAllowGrcPeriod()) {
				if(!financeType.isFInIsAlwGrace()) {
					String[] valueParm = new String[2];
					valueParm[0] = "Allow Grace";
					valueParm[1] = financeMain.getFinType();
					return getErrorDetails("90204", valueParm);
				}

				// grcBaseRate
				if (StringUtils.isNotBlank(financeMain.getGraceBaseRate())) {
					if (StringUtils.isBlank(financeType.getFinGrcBaseRate())) {
						String[] valueParm = new String[2];
						valueParm[0] = "grcBaseRate";
						valueParm[1] = financeMain.getFinType();
						return getErrorDetails("90204", valueParm);
					} else {
						String brCode = financeMain.getGraceBaseRate();
						String currency = financeMain.getFinCcy();
						if(StringUtils.isBlank(currency)) {
							currency = financeType.getFinCcy();
						}
						int rcdCount = baseRateDAO.getBaseRateCountById(brCode, currency, "");
						if(rcdCount <= 0) {
							String[] valueParm = new String[2];
							valueParm[0] = "grcBaseRate";
							valueParm[1] = financeMain.getGraceBaseRate();
							return getErrorDetails("90224", valueParm);
						}
					}
				}

				// grcPftRate
				if (financeMain.getGrcPftRate().compareTo(BigDecimal.ZERO) > 0) {
					if (financeType.getFinGrcIntRate().compareTo(BigDecimal.ZERO) <= 0) {
						String[] valueParm = new String[2];
						valueParm[0] = "grcPftRate";
						valueParm[1] = financeMain.getFinType();
						return getErrorDetails("90204", valueParm);
					}
				}

				if(StringUtils.equals(financeMain.getGrcRateBasis(), CalculationConstants.RATE_BASIS_F)) {
					if (StringUtils.isNotBlank(financeType.getFinGrcBaseRate())) {
						String[] valueParm = new String[1];
						valueParm[0] = financeMain.getGrcRateBasis();
						return getErrorDetails("90223", valueParm);
					}
				}
				
				if (StringUtils.isNotBlank(financeMain.getGraceSpecialRate())) {
					if (StringUtils.isBlank(financeType.getFinGrcBaseRate())) {
						String[] valueParm = new String[2];
						valueParm[0] = "grcSpecialRate";
						valueParm[1] = financeMain.getFinType();
						return getErrorDetails("90204", valueParm);
					}
				}
			}
			
			if(StringUtils.equals(financeMain.getRepayRateBasis(), CalculationConstants.RATE_BASIS_F)) {
				if (StringUtils.isNotBlank(financeMain.getRepayBaseRate()) && StringUtils.isBlank(financeType.getFinBaseRate())) {
					String[] valueParm = new String[1];
					valueParm[0] = financeMain.getRepayRateBasis();
					return getErrorDetails("90223", valueParm);
				}
			}
			
			if (StringUtils.isNotBlank(financeMain.getRepayBaseRate())) {
				if (StringUtils.isBlank(financeType.getFinBaseRate())) {
					String[] valueParm = new String[2];
					valueParm[0] = "repayBaseRate";
					valueParm[1] = financeMain.getFinType();
					return getErrorDetails("90204", valueParm);
				} else {
					String brCode = financeMain.getRepayBaseRate();
					String currency = financeMain.getFinCcy();
					if(StringUtils.isBlank(currency)) {
						currency = financeType.getFinCcy();
					}
					int rcdCount = baseRateDAO.getBaseRateCountById(brCode, currency, "");
					if(rcdCount <= 0) {
						String[] valueParm = new String[2];
						valueParm[0] = "repayBaseRate";
						valueParm[1] = financeMain.getRepayBaseRate();
						return getErrorDetails("90224", valueParm);
					}
				}
			}
			if (financeMain.getRepayProfitRate().compareTo(BigDecimal.ZERO) > 0) {
				if (financeType.getFinIntRate().compareTo(BigDecimal.ZERO) <= 0) {
					String[] valueParm = new String[2];
					valueParm[0] = "repayPftRate";
					valueParm[1] = financeMain.getFinType();
					return getErrorDetails("90204", valueParm);
				}
			}

			// finRepay method
			if(StringUtils.isNotBlank(financeMain.getFinRepayMethod())) {
				List<ValueLabel> repayMethods = PennantStaticListUtil.getRepayMethods();
				boolean repayMehodSts = false;
				for (ValueLabel value : repayMethods) {
					if (StringUtils.equals(value.getValue(), financeMain.getFinRepayMethod())) {
						repayMehodSts = true;
						break;
					}
				}
				if (!repayMehodSts) {
					String[] valueParm = new String[1];
					valueParm[0] = financeMain.getFinRepayMethod();
					return getErrorDetails("90212", valueParm);
				}
			}
			
			// validate grace profit frequency codes
			ErrorDetails errorDetail = null;
			if (financeMain.isAllowGrcPeriod()) {
				// validate grace profit frequency code
				if (StringUtils.isNotBlank(financeMain.getGrcPftFrq())) {
					errorDetail = FrequencyUtil.validateFrequency(financeMain.getGrcPftFrq());
					if (errorDetail != null && StringUtils.isNotBlank(errorDetail.getErrorCode())) {
						String[] valueParm = new String[1];
						valueParm[0] = financeMain.getGrcPftFrq();
						return getErrorDetails("90207", valueParm);
					}
				}

				// validate grace profit days basis
				if (StringUtils.isNotBlank(financeMain.getGrcProfitDaysBasis())) {
					List<ValueLabel> profitDayBasis = PennantStaticListUtil.getProfitDaysBasis();
					boolean grcProfitDaysSts = false;
					for (ValueLabel value : profitDayBasis) {
						if (StringUtils.equals(value.getValue(), financeMain.getGrcProfitDaysBasis())) {
							grcProfitDaysSts = true;
							break;
						}
					}
					if (!grcProfitDaysSts) {
						String[] valueParm = new String[1];
						valueParm[0] = financeMain.getGrcProfitDaysBasis();
						return getErrorDetails("90209", valueParm);
					}
				}

				// validate grace schedule methods
				if (StringUtils.isNotBlank(financeMain.getGrcSchdMthd())) {
					List<ValueLabel> profitDayBasis = PennantStaticListUtil.getScheduleMethods();
					boolean schdMethodSts = false;
					for (ValueLabel value : profitDayBasis) {
						if (StringUtils.equals(value.getValue(), financeMain.getGrcSchdMthd())) {
							schdMethodSts = true;
							break;
						}
					}
					if (!schdMethodSts) {
						String[] valueParm = new String[2];
						valueParm[0] = "Grace";
						valueParm[1] = financeMain.getGrcSchdMthd();
						return getErrorDetails("90210", valueParm);
					}
				}

				// validate grace repay rate basis
				if (StringUtils.isNotBlank(financeMain.getGrcRateBasis())) {
					List<ValueLabel> rateBasis = PennantStaticListUtil.getInterestRateType(true);
					boolean rateBasisSts = false;
					for (ValueLabel value : rateBasis) {
						if (StringUtils.equals(value.getValue(), financeMain.getGrcRateBasis())) {
							rateBasisSts = true;
							break;
						}
					}
					if (!rateBasisSts) {
						String[] valueParm = new String[1];
						valueParm[0] = financeMain.getGrcRateBasis();
						return getErrorDetails("90211", valueParm);
					}
				}
			}

			// validate repay frequency code
			if (StringUtils.isNotBlank(financeMain.getRepayFrq())) {
				errorDetail = FrequencyUtil.validateFrequency(financeMain.getRepayFrq());
				if (errorDetail != null && StringUtils.isNotBlank(errorDetail.getErrorCode())) {
					String[] valueParm = new String[1];
					valueParm[0] = financeMain.getRepayFrq();
					return getErrorDetails("90207", valueParm);
				}
			}

			// validate repay profit frequency code
			if (StringUtils.isNotBlank(financeMain.getRepayPftFrq())) {
				errorDetail = FrequencyUtil.validateFrequency(financeMain.getRepayPftFrq());
				if (errorDetail != null && StringUtils.isNotBlank(errorDetail.getErrorCode())) {
					String[] valueParm = new String[1];
					valueParm[0] = financeMain.getRepayPftFrq();
					return getErrorDetails("90207", valueParm);
				}
			}
			
			// validate repay review frequency code
			if (StringUtils.isNotBlank(financeMain.getRepayRvwFrq())) {
				errorDetail = FrequencyUtil.validateFrequency(financeMain.getRepayRvwFrq());
				if (errorDetail != null && StringUtils.isNotBlank(errorDetail.getErrorCode())) {
					String[] valueParm = new String[1];
					valueParm[0] = financeMain.getRepayPftFrq();
					return getErrorDetails("90207", valueParm);
				}
			}

			// validate profit days basis
			if (StringUtils.isNotBlank(financeMain.getProfitDaysBasis())) {
				List<ValueLabel> profitDayBasis = PennantStaticListUtil.getProfitDaysBasis();
				boolean profitDaysSts = false;
				for (ValueLabel value : profitDayBasis) {
					if (StringUtils.equals(value.getValue(), financeMain.getProfitDaysBasis())) {
						profitDaysSts = true;
						break;
					}
				}
				if (!profitDaysSts) {
					String[] valueParm = new String[1];
					valueParm[0] = financeMain.getProfitDaysBasis();
					return getErrorDetails("90209", valueParm);
				}
			}

			// validate schedule methods
			if (StringUtils.isNotBlank(financeMain.getScheduleMethod())) {
				List<ValueLabel> schdMethods = PennantStaticListUtil.getScheduleMethods();
				boolean schdMethodSts = false;
				for (ValueLabel value : schdMethods) {
					if (StringUtils.equals(value.getValue(), financeMain.getScheduleMethod())) {
						schdMethodSts = true;
						break;
					}
				}
				if (!schdMethodSts) {
					String[] valueParm = new String[2];
					valueParm[0] = "Repayment";
					valueParm[1] = financeMain.getScheduleMethod();
					return getErrorDetails("90210", valueParm);
				}
			}

			// validate grace repay rate basis
			if (StringUtils.isNotBlank(financeMain.getRepayRateBasis())) {
				List<ValueLabel> rateBasis = PennantStaticListUtil.getInterestRateType(true);
				boolean rateBasisSts = false;
				for (ValueLabel value : rateBasis) {
					if (StringUtils.equals(value.getValue(), financeMain.getRepayRateBasis())) {
						rateBasisSts = true;
						break;
					}
				}
				if (!rateBasisSts) {
					String[] valueParm = new String[1];
					valueParm[0] = financeMain.getRepayRateBasis();
					return getErrorDetails("90211", valueParm);
				}
			}
			
			// validate step policy code
			if (financeMain.isStepFinance() && !financeMain.isAlwManualSteps()) {
				if (StringUtils.isBlank(financeMain.getStepPolicy())) {
					String[] valueParm = new String[1];
					valueParm[0] = "Step policy";
					return getErrorDetails("90502", valueParm);
				} else {
					if(!StringUtils.containsIgnoreCase(financeType.getAlwdStepPolicies(), financeMain.getStepPolicy())) {
						String[] valueParm = new String[2];
						valueParm[0] = "step policy code";
						valueParm[1] = financeMain.getFinType();
						return getErrorDetails("90204", valueParm);
					}
				}
			}
			
			// validate step type
			if(financeMain.isStepFinance() && financeMain.isAlwManualSteps()) {
				switch(financeMain.getStepType()) {
					case FinanceConstants.STEPTYPE_PRIBAL:
					case FinanceConstants.STEPTYPE_EMI:
						break;
					default:
						String[] valueParm = new String[2];
						valueParm[0] = "stepType";
						valueParm[1] = financeMain.getStepType();
						return getErrorDetails("90701", valueParm);
				}
			}

			logger.debug("Leaving");
			return returnStatus;
		}

		returnStatus.setReturnCode(APIConstants.RES_FAILED_CODE);
		returnStatus.setReturnText(APIConstants.RES_FAILED_DESC);

		logger.debug("Leaving");

		return returnStatus;
	}

	/**
	 * 
	 * 
	 * @param finSchdData
	 * @return
	 */
	public WSReturnStatus doFinScheduleValidations(FinScheduleData finSchdData) {
		logger.debug("Entering");

		WSReturnStatus returnStatus = new WSReturnStatus();
		if (finSchdData != null) {
			// Fee code validation
			for (FinFeeDetail finFeeDetail : finSchdData.getFinFeeDetailList()) {
				Rule rule = ruleDAO.getRuleById(finFeeDetail.getFeeTypeCode(), RuleConstants.MODULE_FEES, "");
				if (rule == null) {
					String[] valueParm = new String[1];
					valueParm[0] = finFeeDetail.getFeeTypeCode();
					return getErrorDetails("90206", valueParm);
				}
				
				if (StringUtils.equals(CalculationConstants.REMFEE_SCHD_TO_N_INSTALLMENTS, finFeeDetail.getFeeScheduleMethod())
						&& finFeeDetail.getTerms() <= 0) {
					String[] valueParm = new String[1];
					valueParm[0] = "scheduleTerms";
					return getErrorDetails("90221", valueParm);
				}
			}
			
			// validate step details
			if (finSchdData.getFinanceMain().isAlwManualSteps()) {
				if (finSchdData.getStepPolicyDetails() == null || finSchdData.getStepPolicyDetails().isEmpty()) {
					String[] valueParm = new String[1];
					valueParm[0] = "step";
					return getErrorDetails("90502", valueParm);
				}
			}
		} else {
			returnStatus.setReturnCode(APIConstants.RES_FAILED_CODE);
			returnStatus.setReturnText(APIConstants.RES_FAILED_DESC);
		}

		logger.debug("Leaving");

		return returnStatus;
	}

	/**
	 * Validate the Finance details object.<br>
	 * 	which includes below details.<br>
	 * 	- FinScheduleData.<br>
	 *  - documentDetailsList.<br>
	 *  - gurantorsDetailList.<br>
	 *  - jountAccountDetailList.<br>
	 * 	- financeCollaterals.<br>
	 * 
	 * @param financeDetail
	 * @return
	 */
	public WSReturnStatus doFinanceValidations(FinanceDetail financeDetail) {
		logger.debug("Entering");

		WSReturnStatus returnStatus = new WSReturnStatus();
		if(financeDetail != null) {

			// validate disbursement details
			List<FinAdvancePayments> finAdvPayments = financeDetail.getAdvancePaymentsList();
			if (finAdvPayments != null) {
				for (FinAdvancePayments advPayment : finAdvPayments) {
					if (StringUtils.equals(advPayment.getPaymentType(), DisbursementConstants.PAYMENT_TYPE_CHEQUE)
							|| StringUtils.equals(advPayment.getPaymentType(), DisbursementConstants.PAYMENT_TYPE_DD)) {

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
								return getErrorDetails("90216", valueParm);
							}
						}
						
						// Issuer bank
						if (StringUtils.isBlank(advPayment.getBankCode())) {
							String[] valueParm = new String[1];
							valueParm[0] = "issueBank";
							return getErrorDetails("90214", valueParm);
						} else {
							BankDetail bankDetail = bankDetailService.getBankDetailById(advPayment.getBankCode());
							if (bankDetail == null) {
								String[] valueParm = new String[1];
								valueParm[0] = advPayment.getBankCode();
								return getErrorDetails("90213", valueParm);
							}
						}

						// Liability hold name
						if (StringUtils.isBlank(advPayment.getLiabilityHoldName())) {
							String[] valueParm = new String[1];
							valueParm[0] = "favourName";
							return getErrorDetails("90214", valueParm);
						}

						// Payable location
						if (StringUtils.isBlank(advPayment.getPayableLoc())) {
							String[] valueParm = new String[1];
							valueParm[0] = "payableLoc";
							return getErrorDetails("90214", valueParm);
						}

						// Printing location
						if (StringUtils.isBlank(advPayment.getPrintingLoc())) {
							String[] valueParm = new String[1];
							valueParm[0] = "printingLoc";
							return getErrorDetails("90214", valueParm);
						}

						// value date
						if (advPayment.getValueDate() == null) {
							String[] valueParm = new String[1];
							valueParm[0] = "valueDate";
							return getErrorDetails("90214", valueParm);
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
							return getErrorDetails("90215", valueParm);
						}
						if (StringUtils.isNotBlank(advPayment.getiFSC())) {
							BankBranch bankBranch = bankBranchService.getBankBrachByIFSC(advPayment.getiFSC());
							if (bankBranch == null) {
								String[] valueParm = new String[1];
								valueParm[0] = advPayment.getiFSC();
								return getErrorDetails("90301", valueParm);
							}
						}
						if (StringUtils.isNotBlank(advPayment.getBranchBankCode())
								&& StringUtils.isNotBlank(advPayment.getBranchCode())) {
							BankBranch bankBranch = bankBranchService.getBankBrachByCode(advPayment.getBranchBankCode(), advPayment.getBranchCode());
							if (bankBranch == null) {
								String[] valueParm = new String[2];
								valueParm[0] = advPayment.getBranchBankCode();
								valueParm[1] = advPayment.getBranchCode();
								return getErrorDetails("90302", valueParm);
							}
						}

						// Account number
						if (StringUtils.isBlank(advPayment.getBeneficiaryAccNo())) {
							String[] valueParm = new String[2];
							valueParm[0] = "accountNo";
							valueParm[1] = advPayment.getPaymentType();
							return getErrorDetails("90217", valueParm);
						}

						// Account holder name
						if (StringUtils.isBlank(advPayment.getBeneficiaryName())) {
							String[] valueParm = new String[2];
							valueParm[0] = "acHolderName";
							valueParm[0] = advPayment.getPaymentType();
							return getErrorDetails("90217", valueParm);
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
							return getErrorDetails("90214", valueParm);
						}

						if (StringUtils.isNotBlank(advPayment.getPhoneCountryCode())
								&& StringUtils.isBlank(advPayment.getPhoneNumber())) {
							String[] valueParm = new String[1];
							valueParm[0] = "phoneNumber";
							return getErrorDetails("90214", valueParm);
						}
					}
				}
			}
			
			// validate Mandate fields
			Mandate mandate = financeDetail.getMandate();
			if(mandate != null) {
				if(StringUtils.isNotBlank(mandate.getIFSC())) {
					BankBranch bankBranch = bankBranchService.getBankBrachByIFSC(mandate.getIFSC());
					if(bankBranch == null) {
						String[] valueParm = new String[1];
						valueParm[0] = mandate.getIFSC();
						return getErrorDetails("90301", valueParm);
					}
				} else if(StringUtils.isNotBlank(mandate.getBankCode()) && StringUtils.isNotBlank(mandate.getBranchCode())) {
					BankBranch bankBranch = bankBranchService.getBankBrachByCode(mandate.getBankCode(), mandate.getBranchCode());
					if(bankBranch == null) {
						String[] valueParm = new String[2];
						valueParm[0] = mandate.getBankCode();
						valueParm[1] = mandate.getBranchCode();
						return getErrorDetails("90302", valueParm);
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
						return getErrorDetails("90307", valueParm);
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
						return getErrorDetails("90308", valueParm);
					}
				}
				
				//validate periodicity
				if (StringUtils.isNotBlank(mandate.getPeriodicity())) {
					ErrorDetails errorDetail = FrequencyUtil.validateFrequency(mandate.getPeriodicity());
					if (errorDetail != null && StringUtils.isNotBlank(errorDetail.getErrorCode())) {
						String[] valueParm = new String[1];
						valueParm[0] = mandate.getPeriodicity();
						return getErrorDetails("90207", valueParm);
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
						return getErrorDetails("90309", valueParm);
					}
				}
			}
			
			// validate document details
			List<DocumentDetails> documentDetails = financeDetail.getDocumentDetailsList();
			if (documentDetails != null) {
				for (DocumentDetails detail : documentDetails) {
					DocumentType docType = documentTypeService.getDocumentTypeById(detail.getDocCategory());
					if (docType == null) {
						String[] valueParm = new String[1];
						valueParm[0] = detail.getDocCategory();
						return getErrorDetails("90401", valueParm);
					}

					// validate Is Customer document?
					if (docType.isDocIsCustDoc()) {
						if (StringUtils.isBlank(detail.getCustDocTitle())) {
							String[] valueParm = new String[1];
							valueParm[0] = "CustDocTitle";
							return getErrorDetails("90402", valueParm);
						}
					}

					// validate custDocIssuedCountry
/*					if (docType.isDocIssue) {
						if (StringUtils.isBlank(detail.getCustDocIssuedCountry())) {
							String[] valueParm = new String[1];
							valueParm[0] = "CustDocIssuedCountry";
							return getErrorDetails("90402", valueParm);
						}
					}*/

					// validate custDocIssuedOn
					if (docType.isDocIssueDateMand()) {
						if (detail.getCustDocIssuedOn() == null) {
							String[] valueParm = new String[1];
							valueParm[0] = "CustDocIssuedOn";
							return getErrorDetails("90402", valueParm);
						}
					}

					// validate custDocExpDate
					if (docType.isDocExpDateIsMand()) {
						if (detail.getCustDocExpDate() == null) {
							String[] valueParm = new String[1];
							valueParm[0] = "CustDocExpDate";
							return getErrorDetails("90402", valueParm);
						}
					}
				}
			}

			// validate co-applicant details
			List<JointAccountDetail> jountAccountDetails = financeDetail.getJountAccountDetailList();
			if(jountAccountDetails != null) {
				for (JointAccountDetail jointAccDetail : jountAccountDetails) {
					Customer coApplicant = customerDetailsService.getCustomerByCIF(jointAccDetail.getCustCIF());
					if (coApplicant == null) {
						String[] valueParm = new String[1];
						valueParm[0] = jointAccDetail.getCustCIF();
						return getErrorDetails("90102", valueParm);
					}
				}
			}

			// validate guarantor details
			List<GuarantorDetail> guarantorDetails = financeDetail.getGurantorsDetailList();
			if(guarantorDetails != null) {
				for(GuarantorDetail detail: guarantorDetails) {
					if(detail.isBankCustomer()) {
						String guarantorCIF = detail.getGuarantorCIF();
						Customer guarantor = customerDetailsService.getCustomerByCIF(guarantorCIF);
						if(guarantor == null) {
							String[] valueParm = new String[1];
							valueParm[0] = guarantorCIF;
							return getErrorDetails("90103", valueParm);
						}
					}
				}
			}

			// validate flags details
			List<FinFlagsDetail> finFlagDetails = financeDetail.getFinFlagsDetails();
			if(finFlagDetails != null) {
				for(FinFlagsDetail flag: finFlagDetails) {
					Flag flagDetail = flagDAO.getFlagById(flag.getFlagCode(), "");
					if(flagDetail == null) {
						String[] valueParm = new String[1];
						valueParm[0] = flag.getFlagCode();
						return getErrorDetails("91001", valueParm);
					}
				}
			}
			
			logger.debug("Leaving");
			return returnStatus;
		}

		returnStatus.setReturnCode(APIConstants.RES_FAILED_CODE);
		returnStatus.setReturnText(APIConstants.RES_FAILED_DESC);

		logger.debug("Leaving");

		return returnStatus;
	}
	/**
	 * Validate the FinanceBasic details object.<br>
	 * @param financeMain
	 * @return
	 */
	public WSReturnStatus validateFinBasicDetails(FinanceMain financeMain) {
		logger.debug("Entering");

		WSReturnStatus returnStatus = new WSReturnStatus();
		if (financeMain != null) {
			RelationshipOfficer relationshipOfficer = relationshipOfficerService
					.getApprovedRelationshipOfficerById(financeMain.getAccountsOfficer());
			if (relationshipOfficer == null) {
				String[] valueParm = new String[1];
				valueParm[0] = financeMain.getAccountsOfficer();
				return getErrorDetails("90501", valueParm);
			}
			relationshipOfficer = relationshipOfficerService.getApprovedRelationshipOfficerById(financeMain.getDsaCode());
			if (relationshipOfficer == null) {
				String[] valueParm = new String[1];
				valueParm[0] = financeMain.getDsaCode();
				return getErrorDetails("90501", valueParm);
			}

			if (StringUtils.isNotBlank(financeMain.getSalesDepartment())) {
				relationshipOfficer = relationshipOfficerService
						.getApprovedRelationshipOfficerById(financeMain.getSalesDepartment());
				if (relationshipOfficer == null) {
					String[] valueParm = new String[1];
					valueParm[0] = financeMain.getAccountsOfficer();
					return getErrorDetails("90501", valueParm);
				}
			}
			if (StringUtils.isNotBlank(financeMain.getDmaCode())) {
				RelationshipOfficer dmaCode = relationshipOfficerService.getApprovedRelationshipOfficerById(financeMain
						.getDmaCode());
				if (dmaCode == null) {
					String[] valueParm = new String[1];
					valueParm[0] = financeMain.getDsaCode();
					return getErrorDetails("90501", valueParm);
				}
			}
			if (StringUtils.isNotBlank(financeMain.getReferralId())) {
				RelationshipOfficer referralId = relationshipOfficerService
						.getApprovedRelationshipOfficerById(financeMain.getReferralId());
				if (referralId == null) {
					String[] valueParm = new String[1];
					valueParm[0] = financeMain.getDsaCode();
					return getErrorDetails("90501", valueParm);
				}
			}
		}

		logger.debug("Leaving");
		return returnStatus;

	}
	/**
	 * Method for prepare response object with errorDetails.
	 * 
	 * @param errorCode
	 * @param valueParm
	 * @return
	 */
	public WSReturnStatus getErrorDetails(String errorCode, String[] valueParm) {
		logger.debug("Entering");

		WSReturnStatus response = new WSReturnStatus();
		response = APIErrorHandlerService.getFailedStatus(errorCode, valueParm);

		// set default error code and description in case of Error code does not exists.
		if (StringUtils.isBlank(response.getReturnCode())) {
			response = APIErrorHandlerService.getFailedStatus(APIConstants.RES_FAILED_CODE,
					APIConstants.RES_FAILED_DESC);
		}

		logger.debug("Leaving");
		return response;
	}


	@Autowired
	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}

	@Autowired
	public void setFinanceTypeService(FinanceTypeService financeTypeService) {
		this.financeTypeService = financeTypeService;
	}

	@Autowired
	public void setBankBranchService(BankBranchService bankBranchService) {
		this.bankBranchService = bankBranchService;
	}

	@Autowired
	public void setDocumentTypeService(DocumentTypeService documentTypeService) {
		this.documentTypeService = documentTypeService;
	}

	@Autowired
	public void setFlagDAO(FlagDAO flagDAO) {
		this.flagDAO = flagDAO;
	}
	
	@Autowired
	public void setBranchService(BranchService branchService) {
		this.branchService = branchService;
	}
	
	@Autowired
	public void setBankDetailService(BankDetailService bankDetailService) {
		this.bankDetailService = bankDetailService;
	}
	@Autowired
	public void setRuleDAO(RuleDAO ruleDAO) {
		this.ruleDAO = ruleDAO;
	}

	@Autowired
	public void setBaseRateDAO(BaseRateDAO baseRateDAO) {
		this.baseRateDAO = baseRateDAO;
	}
	@Autowired
	public void setGeneralDepartmentService(GeneralDepartmentService generalDepartmentService) {
		this.generalDepartmentService = generalDepartmentService;
	}
	@Autowired
	public void setRelationshipOfficerService(RelationshipOfficerService relationshipOfficerService) {
		this.relationshipOfficerService = relationshipOfficerService;
	}
}
