package com.pennant.backend.service.finance.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.FrequencyCodeTypes;
import com.pennant.app.constants.HolidayHandlerTypes;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.FrequencyUtil;
import com.pennant.app.util.SessionUserDetails;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.applicationmaster.CurrencyDAO;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinODPenaltyRate;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.mandate.Mandate;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rmtmasters.Promotion;
import com.pennant.backend.service.applicationmaster.BranchService;
import com.pennant.backend.service.rmtmasters.PromotionService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class FinanceDataDefaulting {

	private CustomerDAO customerDAO;
	private FinanceTypeDAO financeTypeDAO;
	private BranchService branchService;
	private PromotionService promotionService;
	private CurrencyDAO currencyDAO;
	private FinanceMainDAO financeMainDAO;

	public FinanceDataDefaulting() {
		super();
	}

	// Constructor Details for Methods
	public FinanceDetail defaultFinance(String vldGroup, FinanceDetail finDetail) {
		List<ErrorDetail> errorDetails = new ArrayList<ErrorDetail>();
		FinScheduleData finScheduleData = finDetail.getFinScheduleData();
		FinanceMain finMain = finScheduleData.getFinanceMain();
		finDetail.setCustomerDetails(new CustomerDetails());
		finDetail.getCustomerDetails().setCustomer(null);
		Customer customer = null;

		//Get the logged in users one time and set to avoid multiple calls
		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		finMain.setUserDetails(userDetails);

		//customer Defaulting
		if (PennantConstants.VLD_CRT_LOAN.equals(vldGroup)) {
			if (StringUtils.isNotBlank(finMain.getCoreBankId())) {
				customer = customerDAO.getCustomerByCoreBankId(finMain.getCoreBankId(), "");
				if (customer != null) {
					finMain.setLovDescCustCIF(customer.getCustCIF());
					finMain.setLovDescCustCIF(customer.getCustCIF());
				} else {
					String[] valueParm = new String[2];
					valueParm[0] = "CoreBankId";
					valueParm[1] = finMain.getCoreBankId();
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90701", valueParm)));
					finScheduleData.setErrorDetails(errorDetails);
					return finDetail;
				}
			}

			//Get Customer information
			if (!StringUtils.equals("CRTSCHD", vldGroup)) {
				if (customer == null) {
					customer = customerDAO.getCustomerByCIF(finMain.getCustCIF(), "");
					if (customer == null) {
						String[] valueParm = new String[1];
						valueParm[0] = finMain.getLovDescCustCIF();
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90101", valueParm)));
						finScheduleData.setErrorDetails(errorDetails);
						return finDetail;
					}
				}
				finMain.setCustID(customer.getCustID());
				finDetail.getCustomerDetails().setCustomer(customer);
			}
		}

		// Date formats
		setDefaultDateFormats(finMain);

		if (finMain.getFinStartDate() == null) {
			finMain.setFinStartDate(SysParamUtil.getAppDate());
		}

		//Validate Fields data (Excluding Base & Special rates Validations)
		validateMasterData(vldGroup, finDetail);

		if (!finScheduleData.getErrorDetails().isEmpty()) {
			return finDetail;
		}

		// Basic Details Defaulting
		basicDefaulting(vldGroup, finDetail);

		// Grace Details Defaulting
		graceDefaulting(vldGroup, finDetail);

		// Repayments Details Defaulting
		repayDefaulting(vldGroup, finDetail);

		// Overdue penalty rates defaulting
		if (StringUtils.equals(PennantConstants.VLD_CRT_LOAN, vldGroup)) {
			overdueDefaulting(vldGroup, finDetail);
		}
		return finDetail;

	}

	/*
	 * ################################################################################################################
	 * MAIN METHODS
	 * ################################################################################################################
	 */

	private void setDefaultDateFormats(FinanceMain financeMain) {
		if (financeMain.getFinStartDate() != null) {
			financeMain.setFinStartDate(DateUtility.getTimestamp(financeMain.getFinStartDate()));
		}

		if (financeMain.getGrcPeriodEndDate() != null) {
			financeMain.setGrcPeriodEndDate(DateUtility
					.getDBDate(DateUtility.format(financeMain.getGrcPeriodEndDate(), PennantConstants.DBDateFormat)));
		}
		if (financeMain.getNextGrcPftDate() != null) {
			financeMain.setNextGrcPftDate(DateUtility
					.getDBDate(DateUtility.format(financeMain.getNextGrcPftDate(), PennantConstants.DBDateFormat)));
		}
		if (financeMain.getNextGrcPftRvwDate() != null) {
			financeMain.setNextGrcPftRvwDate(DateUtility
					.getDBDate(DateUtility.format(financeMain.getNextGrcPftRvwDate(), PennantConstants.DBDateFormat)));
		}
		if (financeMain.getNextGrcCpzDate() != null) {
			financeMain.setNextGrcCpzDate(DateUtility
					.getDBDate(DateUtility.format(financeMain.getNextGrcCpzDate(), PennantConstants.DBDateFormat)));
		}
		if (financeMain.getNextRepayDate() != null) {
			financeMain.setNextRepayDate(DateUtility
					.getDBDate(DateUtility.format(financeMain.getNextRepayDate(), PennantConstants.DBDateFormat)));
		}
		if (financeMain.getNextRepayPftDate() != null) {
			financeMain.setNextRepayPftDate(DateUtility
					.getDBDate(DateUtility.format(financeMain.getNextRepayPftDate(), PennantConstants.DBDateFormat)));
		}
		if (financeMain.getNextRepayRvwDate() != null) {
			financeMain.setNextRepayRvwDate(DateUtility
					.getDBDate(DateUtility.format(financeMain.getNextRepayRvwDate(), PennantConstants.DBDateFormat)));
		}
		if (financeMain.getNextRepayCpzDate() != null) {
			financeMain.setNextRepayCpzDate(DateUtility
					.getDBDate(DateUtility.format(financeMain.getNextRepayCpzDate(), PennantConstants.DBDateFormat)));
		}
		if (financeMain.getFirstDroplineDate() != null) {
			financeMain.setFirstDroplineDate(DateUtility
					.getDBDate(DateUtility.format(financeMain.getFirstDroplineDate(), PennantConstants.DBDateFormat)));
		}
		if (financeMain.getMaturityDate() != null) {
			financeMain.setMaturityDate(DateUtility
					.getDBDate(DateUtility.format(financeMain.getMaturityDate(), PennantConstants.DBDateFormat)));
		}
		if (financeMain.getNextRolloverDate() != null) {
			financeMain.setNextRolloverDate(DateUtility
					.getDBDate(DateUtility.format(financeMain.getNextRolloverDate(), PennantConstants.DBDateFormat)));
		}
	}

	/*
	 * ================================================================================================================
	 * VALIDATE STATIC DATA
	 * ================================================================================================================
	 */
	private void validateMasterData(String vldGroup, FinanceDetail finDeail) {
		List<ErrorDetail> errorDetails = new ArrayList<ErrorDetail>();

		FinScheduleData finScheduleData = finDeail.getFinScheduleData();
		FinanceMain finMain = finScheduleData.getFinanceMain();

		// Validate Finance Type (Mandatory for Defaulting)
		FinanceType financeType = null;

		if (!StringUtils.isBlank(finMain.getFinType())) {
			financeType = financeTypeDAO.getOrgFinanceTypeByID(finMain.getFinType(), "_ORGView");
			if (financeType == null) {
				String[] valueParm = new String[2];
				valueParm[0] = "Loan Type";
				valueParm[1] = finMain.getFinType();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90701", valueParm)));
				finScheduleData.setErrorDetails(errorDetails);
				return;
			}
		}

		if (StringUtils.equals(financeType.getProductCategory(), FinanceConstants.PRODUCT_CD)
				&& StringUtils.isBlank(finMain.getPromotionCode())) {
			String[] valueParm = new String[1];
			valueParm[0] = "PromotionCode";
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
			finScheduleData.setErrorDetails(errorDetails);
			return;
		}

		if (StringUtils.isNotBlank(finMain.getPromotionCode())) {
			Promotion promotion = promotionService.getActiveSchemeForTxn(finMain.getPromotionCode(),
					FinanceConstants.MODULEID_PROMOTION, finMain.getFinStartDate(), true);
			finScheduleData.setPromotion(promotion);
			finDeail.setPromotion(promotion);

			if (promotion == null) {
				String[] valueParm = new String[2];
				valueParm[0] = "Promotion";
				valueParm[1] = finMain.getPromotionCode();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90701", valueParm)));
			} else {
				if (StringUtils.isBlank(finMain.getFinType())) {
					finMain.setFinType(promotion.getFinType());
					financeType = financeTypeDAO.getOrgFinanceTypeByID(finMain.getFinType(), "_ORGView");
				}

				if (!StringUtils.equals(finMain.getFinType(), promotion.getFinType())) {
					String[] valueParm = new String[1];
					valueParm[0] = finMain.getFinType();
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90202", valueParm)));
					finScheduleData.setErrorDetails(errorDetails);
					return;
				}
				if (promotion.getFinMinAmount().compareTo(BigDecimal.ZERO) > 0
						&& finMain.getFinAmount().compareTo(promotion.getFinMinAmount()) < 0) {
					String[] valueParm = new String[1];
					valueParm[0] = PennantApplicationUtil.amountFormate(promotion.getFinMinAmount(),
							CurrencyUtil.getFormat(finMain.getFinCcy()));
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90132", valueParm)));
				}

				if (promotion.getFinMaxAmount().compareTo(BigDecimal.ZERO) > 0) {
					if (finMain.getFinAmount().compareTo(promotion.getFinMaxAmount()) > 0) {
						String[] valueParm = new String[1];
						valueParm[0] = PennantApplicationUtil.amountFormate(promotion.getFinMaxAmount(),
								CurrencyUtil.getFormat(finMain.getFinCcy()));
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90133", valueParm)));
					}
				}

				financeType.setPromotionType(true);
				financeType.setPromotionCode(promotion.getPromotionCode());
				finMain.setPromotionSeqId(promotion.getReferenceID());

				// FIXME: PV 28AUG19: AS THE SCHEME SCREENS ARE NOT READY,
				// CONFIGURATION TAKEN FROM LOAN TYPE
				/*
				 * financeType.setFinTypeFeesList(promotion.getFinTypeFeesList() );
				 * financeType.setFInTypeFromPromotiion(promotion); financeType.setFinTypeInsurances(promotion.
				 * getFinTypeInsurancesList()); financeType.setFinTypeAccountingList(promotion.
				 * getFinTypeAccountingList());
				 */
			}
		}

		// Validate Finance Currency
		finScheduleData.setFinanceType(financeType);
		if (StringUtils.isBlank(finMain.getFinCcy())) {
			String ccy = SysParamUtil.getAppCurrency();
			finMain.setFinCcy(ccy);
		}

		// If Finance Branch is NULL get it from customer (Without customer it
		// would not have reached this point)	
		if (PennantConstants.VLD_CRT_LOAN.equals(vldGroup)) {
			if (StringUtils.isBlank(finMain.getFinBranch())) {
				finMain.setFinBranch(finDeail.getCustomerDetails().getCustomer().getCustDftBranch());
			}
		}

		// validate finance branch
		if (StringUtils.isNotBlank(finMain.getFinBranch())) {
			Branch branch = branchService.getBranch(finMain.getFinBranch());
			if (branch == null) {
				String[] valueParm = new String[1];
				valueParm[0] = finMain.getFinBranch();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90129", valueParm)));
			} else {
				finMain.setSwiftBranchCode(branch.getBranchSwiftBrnCde());
			}
		}

		// Validate Interest Days basis
		if (StringUtils.isNotBlank(finMain.getProfitDaysBasis()) && !isValidateIDB(finMain.getProfitDaysBasis())) {
			String[] valueParm = new String[1];
			valueParm[0] = finMain.getProfitDaysBasis();
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90209", valueParm)));
		}

		// Validate Repayment Method
		String repayMethod = finMain.getFinRepayMethod();

		if (StringUtils.isBlank(repayMethod)) {
			repayMethod = financeType.getFinRepayMethod();
			finMain.setFinRepayMethod(repayMethod);
		}

		if (StringUtils.isNotBlank(repayMethod)) {
			List<ValueLabel> mandateType = PennantStaticListUtil.getRepayMethods();
			boolean mandateTypeSts = false;
			for (ValueLabel value : mandateType) {
				if (StringUtils.equals(value.getValue(), repayMethod)) {
					mandateTypeSts = true;
					break;
				}
			}
			if (!mandateTypeSts) {
				String[] valueParm = new String[1];
				valueParm[0] = repayMethod;
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90307", valueParm)));
			}
		}

		// Step Policy
		if (StringUtils.isNotBlank(finMain.getStepPolicy())) {
			if (!StringUtils.containsIgnoreCase(financeType.getAlwdStepPolicies(), finMain.getStepPolicy())) {
				String[] valueParm = new String[2];
				valueParm[0] = finMain.getFinType();
				valueParm[1] = financeType.getAlwdStepPolicies();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90147", valueParm)));
			}
		}

		// Step Type
		if (StringUtils.isNotBlank(finMain.getStepType())) {
			if (!StringUtils.equals(finMain.getStepType(), FinanceConstants.STEPTYPE_EMI)
					&& !StringUtils.equals(finMain.getStepType(), FinanceConstants.STEPTYPE_PRIBAL)) {
				String[] valueParm = new String[2];
				valueParm[0] = new StringBuilder(10).append(FinanceConstants.STEPTYPE_EMI).append(" & ")
						.append(FinanceConstants.STEPTYPE_PRIBAL).toString();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90148", valueParm)));
			}
		}

		// Validate Grace Interest Days basis
		if (StringUtils.isNotBlank(finMain.getGrcProfitDaysBasis())
				&& !isValidateIDB(finMain.getGrcProfitDaysBasis())) {
			String[] valueParm = new String[1];
			valueParm[0] = finMain.getGrcProfitDaysBasis();
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90209", valueParm)));
		}

		// Validate Grace Profit Frequency
		if (StringUtils.isNotBlank(finMain.getGrcPftFrq())) {
			ErrorDetail tempError = FrequencyUtil.validateFrequency(finMain.getGrcPftFrq());
			if (tempError != null) {
				String[] valueParm = new String[2];
				valueParm[0] = "Grace";
				valueParm[1] = finMain.getGrcPftFrq();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90156", valueParm)));
			}
		}

		// Grace Rate Type/Rate Basis
		if (StringUtils.isNotBlank(finMain.getGrcRateBasis())
				&& !StringUtils.equals(finMain.getGrcRateBasis(), PennantConstants.List_Select)) {
			if (!StringUtils.equals(finMain.getGrcRateBasis(), CalculationConstants.RATE_BASIS_F)
					&& !StringUtils.equals(finMain.getGrcRateBasis(), CalculationConstants.RATE_BASIS_R)) {
				String[] valueParm = new String[1];
				valueParm[0] = finMain.getGrcRateBasis();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90211", valueParm)));
			}
		}

		// Validate Grace Profit Review Frequency
		if (StringUtils.isNotBlank(finMain.getGrcPftRvwFrq())) {
			ErrorDetail tempError = FrequencyUtil.validateFrequency(finMain.getGrcPftRvwFrq());
			if (tempError != null) {
				String[] valueParm = new String[2];
				valueParm[0] = "Grace";
				valueParm[1] = finMain.getGrcPftRvwFrq();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90157", valueParm)));
			}
		}

		// Validate Grace Capitalize Frequency
		if (StringUtils.isNotBlank(finMain.getGrcCpzFrq())) {
			ErrorDetail tempError = FrequencyUtil.validateFrequency(finMain.getGrcCpzFrq());
			if (tempError != null) {
				String[] valueParm = new String[2];
				valueParm[0] = "Grace";
				valueParm[1] = finMain.getGrcCpzFrq();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90158", valueParm)));
			}
		}

		// Grace Payment Method (Schedule Method)
		if (StringUtils.isNotBlank(finMain.getGrcSchdMthd())) {
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

		// Rate Basis
		if (StringUtils.isNotBlank(finMain.getRepayRateBasis())) {
			if (!StringUtils.equals(finMain.getRepayRateBasis(), CalculationConstants.RATE_BASIS_C)
					&& !StringUtils.equals(finMain.getRepayRateBasis(), CalculationConstants.RATE_BASIS_F)
					&& !StringUtils.equals(finMain.getRepayRateBasis(), CalculationConstants.RATE_BASIS_R)) {
				String[] valueParm = new String[1];
				valueParm[0] = finMain.getRepayRateBasis();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90211", null)));
			}
		}

		// Repayment Schedule Method
		if (StringUtils.isNotBlank(finMain.getScheduleMethod())) {
			if (!StringUtils.equals(finMain.getScheduleMethod(), CalculationConstants.SCHMTHD_NOPAY)
					&& !StringUtils.equals(finMain.getScheduleMethod(), CalculationConstants.SCHMTHD_EQUAL)
					&& !StringUtils.equals(finMain.getScheduleMethod(), CalculationConstants.SCHMTHD_PFT)
					&& !StringUtils.equals(finMain.getScheduleMethod(), CalculationConstants.SCHMTHD_PFTCPZ)
					&& !StringUtils.equals(finMain.getScheduleMethod(), CalculationConstants.SCHMTHD_PRI)
					&& !StringUtils.equals(finMain.getScheduleMethod(), CalculationConstants.SCHMTHD_PRI_PFT)
					&& !StringUtils.equals(finMain.getScheduleMethod(), CalculationConstants.SCHMTHD_POS_INT)) {
				String[] valueParm = new String[2];
				valueParm[0] = "Repayment";
				valueParm[1] = finMain.getScheduleMethod();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90210", valueParm)));
			}
		}

		// Validate Payment Frequency
		if (StringUtils.isNotBlank(finMain.getRepayFrq())) {
			ErrorDetail tempError = FrequencyUtil.validateFrequency(finMain.getRepayFrq());
			if (tempError != null) {
				String[] valueParm = new String[2];
				valueParm[0] = "Repay";
				valueParm[1] = finMain.getRepayFrq();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90159", valueParm)));
			}
		}

		// Validate Repayment Interest Frequency
		if (StringUtils.isNotBlank(finMain.getRepayPftFrq())) {
			ErrorDetail tempError = FrequencyUtil.validateFrequency(finMain.getRepayPftFrq());
			if (tempError != null) {
				String[] valueParm = new String[2];
				valueParm[0] = "Repay";
				valueParm[1] = finMain.getRepayPftFrq();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90156", valueParm)));
			}
		}

		// Validate Repayment Interest Review Frequency
		if (StringUtils.isNotBlank(finMain.getRepayRvwFrq())) {
			ErrorDetail tempError = FrequencyUtil.validateFrequency(finMain.getRepayRvwFrq());
			if (tempError != null) {
				String[] valueParm = new String[2];
				valueParm[0] = "Repay";
				valueParm[1] = finMain.getRepayRvwFrq();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90157", valueParm)));
			}
		}

		// Validate Repayment Capitalize Frequency
		if (StringUtils.isNotBlank(finMain.getRepayCpzFrq())) {
			ErrorDetail tempError = FrequencyUtil.validateFrequency(finMain.getRepayCpzFrq());
			if (tempError != null) {
				String[] valueParm = new String[2];
				valueParm[0] = "Repay";
				valueParm[1] = finMain.getRepayCpzFrq();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90158", valueParm)));
			}
		}

		// Validate Rollover Frequency
		if (StringUtils.isNotBlank(finMain.getRolloverFrq())) {
			ErrorDetail tempError = FrequencyUtil.validateFrequency(finMain.getRolloverFrq());
			if (tempError != null) {
				String[] valueParm = new String[2];
				valueParm[0] = "Rollover";
				valueParm[1] = finMain.getRolloverFrq();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90160", valueParm)));
			}
		}

		// BPI Treatment
		if (StringUtils.isNotBlank(finMain.getBpiTreatment())) {
			if (!StringUtils.equals(finMain.getBpiTreatment(), FinanceConstants.BPI_NO)
					&& !StringUtils.equals(finMain.getBpiTreatment(), FinanceConstants.BPI_DISBURSMENT)
					&& !StringUtils.equals(finMain.getBpiTreatment(), FinanceConstants.BPI_SCHEDULE)
					&& !StringUtils.equals(finMain.getBpiTreatment(), FinanceConstants.BPI_CAPITALIZE)
					&& !StringUtils.equals(finMain.getBpiTreatment(), FinanceConstants.BPI_SCHD_FIRSTEMI)) {
				String[] valueParm = new String[2];
				valueParm[0] = finMain.getBpiTreatment();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90185", valueParm)));

			}
		}

		// set errorDetails to schedule object
		if (errorDetails.size() > 0) {
			finScheduleData.setErrorDetails(errorDetails);
		}
	}

	/*
	 * ================================================================================================================
	 * DEFAULT FINANCE BASIC DETAILS
	 * ================================================================================================================
	 */

	private void basicDefaulting(String vldGroup, FinanceDetail finDetail) {
		FinScheduleData finScheduleData = finDetail.getFinScheduleData();
		FinanceMain finMain = finScheduleData.getFinanceMain();
		FinanceType financeType = finScheduleData.getFinanceType();

		//Default Loan Start Date
		if (finMain.getFinStartDate() == null) {
			finMain.setFinStartDate(SysParamUtil.getAppDate());
		}

		// Finance amount 
		if (finMain.getFinAmount() == null) {
			finMain.setFinAmount(BigDecimal.ZERO);
		}

		//Set Product Category
		finMain.setProductCategory(financeType.getProductCategory());
		finMain.setFinCategory(financeType.getFinCategory());

		//Default Finance Currency
		if (StringUtils.isBlank(finMain.getFinCcy())) {
			finMain.setFinCcy(financeType.getFinCcy());
		}

		//Default Interest Days Basis
		if (StringUtils.isBlank(finMain.getProfitDaysBasis())) {
			finMain.setProfitDaysBasis(financeType.getFinDaysCalType());
		}

		// Review rate applied for
		finMain.setRvwRateApplFor(financeType.getFinRvwRateApplFor());
		finMain.setRateChgAnyDay(financeType.isRateChgAnyDay());
		finMain.setPastduePftCalMthd(financeType.getPastduePftCalMthd());
		finMain.setPastduePftMargin(financeType.getPastduePftMargin());
		finMain.setSchCalOnRvw(financeType.getFinSchCalCodeOnRvw());
		finMain.setFinIsAlwMD(financeType.isFinIsAlwMD());
		finMain.setAlwMultiDisb(financeType.isFinIsAlwMD());

		//Repayment Method
		if (StringUtils.isBlank(finMain.getFinRepayMethod())) {
			finMain.setFinRepayMethod(financeType.getFinRepayMethod());
		}

		//Mandatory Stepping
		if (financeType.isStepFinance() && financeType.isSteppingMandatory()) {
			finMain.setStepFinance(true);
		}

		if (finMain.isStepFinance()) {
			stepDefaulting(vldGroup, finScheduleData);
		}
		//defaults from application
		finMain.setFinStsReason(FinanceConstants.FINSTSRSN_SYSTEM);
		finMain.setInitiateUser(finMain.getUserDetails().getUserId());
		finMain.setInitiateDate(SysParamUtil.getAppDate());
		finMain.setShariaStatus(PennantConstants.SHARIA_STATUS_NOTREQUIRED);
		finMain.setCalRoundingMode(financeType.getRoundingMode());
		finMain.setRoundingTarget(financeType.getRoundingTarget());
		finMain.setTDSApplicable(financeType.isTdsApplicable());

		// tasks # >>Start Advance EMI and DSF
		if (financeType.isGrcAdvIntersetReq()) {
			finMain.setGrcAdvType(financeType.getGrcAdvType());
			finMain.setGrcAdvTerms(financeType.getGrcAdvDefaultTerms());
		} else {
			finMain.setGrcAdvType(PennantConstants.List_Select);
			finMain.setGrcAdvTerms(0);
		}

		if (financeType.isCashCollateralReq()) {
			if (financeType.isAdvIntersetReq()) {
				finMain.setAdvType(financeType.getAdvType());
				finMain.setAdvTerms(financeType.getAdvDefaultTerms());
				finMain.setAdvStage(financeType.getAdvStage());
			} else {
				finMain.setAdvType(PennantConstants.List_Select);
				finMain.setAdvTerms(0);
				finMain.setAdvStage(PennantConstants.List_Select);
			}
		}
		// tasks # >>End Advance EMI and DSF

		//Setting Default TDS Type
		if (StringUtils.isBlank(finMain.getTdsType())
				&& !PennantConstants.TDS_USER_SELECTION.equals(financeType.getTdsType())) {
			finMain.setTdsType(financeType.getTdsType());
		}

		finMain.setInstBasedSchd(financeType.isInstBasedSchd());
	}

	/*
	 * ================================================================================================================
	 * DEFAULT FINANCE GRACE DETAILS
	 * ================================================================================================================
	 */
	private void graceDefaulting(String vldGroup, FinanceDetail finDetail) {
		FinScheduleData finScheduleData = finDetail.getFinScheduleData();
		FinanceMain finMain = finScheduleData.getFinanceMain();
		FinanceType financeType = finScheduleData.getFinanceType();
		BigDecimal zeroValue = BigDecimal.ZERO;
		boolean isValidPftFrq = false;
		boolean isValidOtherFrq = false;

		if (!finMain.isAllowGrcPeriod()) {
			finMain.setGraceTerms(0);
			finMain.setGrcPeriodEndDate(finMain.getFinStartDate());
			finMain.setGrcRateBasis(null);
			finMain.setGrcPftRate(zeroValue);
			finMain.setGraceBaseRate(null);
			finMain.setGraceSpecialRate(null);
			finMain.setGrcMargin(zeroValue);
			finMain.setGrcProfitDaysBasis(null);
			finMain.setGrcPftFrq(null);
			finMain.setNextGrcPftDate(null);
			finMain.setAllowGrcPftRvw(false);
			finMain.setGrcPftRvwFrq(null);
			finMain.setNextGrcPftRvwDate(null);
			finMain.setAllowGrcCpz(false);
			finMain.setGrcCpzFrq(null);
			finMain.setNextGrcCpzDate(null);
			finMain.setAllowGrcRepay(false);
			finMain.setGrcSchdMthd(null);
			finMain.setGrcMinRate(zeroValue);
			finMain.setGrcMaxRate(zeroValue);
			finMain.setGrcAdvBaseRate(null);
			finMain.setGrcAdvMargin(zeroValue);
			finMain.setGrcAdvPftRate(zeroValue);
			finMain.setCalGrcEndDate(finMain.getFinStartDate());
			finMain.setGrcRateBasis(PennantConstants.List_Select);
			return;
		}

		// set default values from financeType
		finMain.setAllowGrcPftRvw(financeType.isFinGrcIsRvwAlw());
		finMain.setAllowGrcCpz(financeType.isFinGrcIsIntCpz());

		//Grace Rate Type
		if (StringUtils.isBlank(finMain.getGrcRateBasis())) {
			finMain.setGrcRateBasis(financeType.getFinGrcRateType());
		}

		// Grace schedule method
		if (StringUtils.isBlank(finMain.getGrcSchdMthd())) {
			finMain.setGrcSchdMthd("");
		}

		//Grace Rate
		if (StringUtils.isBlank(finMain.getGraceBaseRate()) && finMain.getGrcPftRate().compareTo(zeroValue) == 0
				&& StringUtils.isBlank(finMain.getGraceSpecialRate())
				&& finMain.getGrcMargin().compareTo(zeroValue) == 0) {
			if (StringUtils.isNotBlank(financeType.getFinGrcBaseRate())) {
				finMain.setGraceBaseRate(financeType.getFinGrcBaseRate());
				finMain.setGraceSpecialRate(financeType.getFinGrcSplRate());
				finMain.setGrcMargin(financeType.getFinGrcMargin());
			}
		}

		//Grace Interest Days Basis
		if (StringUtils.isBlank(finMain.getGrcProfitDaysBasis())) {
			finMain.setGrcProfitDaysBasis(financeType.getFinDaysCalType());
		}

		//Set Default Profit Frequency
		if (StringUtils.isBlank(finMain.getGrcPftFrq())) {
			//Set Frequency based on Next Profit Date
			if (finMain.getNextGrcPftDate() != null) {
				finMain.setGrcPftFrq(getDftFrequency(financeType.getFinGrcDftIntFrq(), finMain.getNextGrcPftDate()));

				//Set Frequency based on Grace End Date
			} else if (finMain.getGrcPeriodEndDate() != null) {
				finMain.setGrcPftFrq(getDftFrequency(financeType.getFinGrcDftIntFrq(), finMain.getGrcPeriodEndDate()));

				//Set Frequency based on allowed Frequency Days
			} else if (StringUtils.isNotEmpty(financeType.getFrequencyDays())) {
				finMain.setGrcPftFrq(getDftFrequency(financeType.getFinGrcDftIntFrq(), financeType.getFrequencyDays()));

				//Set Frequency based on Finance Start Date
			} else {
				finMain.setGrcPftFrq(getDftFrequency(financeType.getFinGrcDftIntFrq(), finMain.getFinStartDate()));
			}
		}

		ErrorDetail tempError = FrequencyUtil.validateFrequency(finMain.getGrcPftFrq());
		if (tempError == null) {
			isValidPftFrq = true;
		}

		//Set Default Next Profit Date
		if (isValidPftFrq && finMain.getNextGrcPftDate() == null) {
			Date nextDate = FrequencyUtil.getNextDate(finMain.getGrcPftFrq(), 1, finMain.getFinStartDate(),
					HolidayHandlerTypes.MOVE_NONE, false, financeType.getFddLockPeriod()).getNextFrequencyDate();
			finMain.setNextGrcPftDate(nextDate);
		}

		//Default Terms and Grace End Date
		if (isValidPftFrq) {
			graceEndDefaulting(finScheduleData);
		}

		//Grace Review Allowed
		if (finMain.isAllowGrcPftRvw()) {
			//Review Frequency
			if (StringUtils.isBlank(finMain.getGrcPftRvwFrq()) && isValidPftFrq) {
				String frq = new StringBuilder(5).append(financeType.getFinGrcRvwFrq().substring(0, 3))
						.append(finMain.getGrcPftFrq().substring(3, 5)).toString();
				finMain.setGrcPftRvwFrq(frq);
			}

			tempError = FrequencyUtil.validateFrequency(finMain.getGrcPftRvwFrq());
			if (tempError == null) {
				isValidOtherFrq = true;
			}

			//Review Date
			if (isValidOtherFrq && finMain.getNextGrcPftRvwDate() == null) {
				Date rvwDate = FrequencyUtil.getNextDate(finMain.getGrcPftRvwFrq(), 1, finMain.getFinStartDate(),
						HolidayHandlerTypes.MOVE_NONE, false).getNextFrequencyDate();
				rvwDate = DateUtility.getDBDate(DateUtility.format(rvwDate, PennantConstants.DBDateFormat));

				if (finMain.getCalGrcEndDate() != null && rvwDate != null) {
					if (finMain.getCalGrcEndDate().compareTo(rvwDate) < 0) {
						rvwDate = finMain.getCalGrcEndDate();
					}
				}
				finMain.setNextGrcPftRvwDate(rvwDate);
			}
		}

		//Grace Capitalize Allowed
		if (finMain.isAllowGrcCpz()) {
			//Capitalize Frequency
			if (StringUtils.isBlank(finMain.getGrcCpzFrq()) && isValidPftFrq) {
				String frq = new StringBuilder(5).append(financeType.getFinGrcCpzFrq().substring(0, 3))
						.append(finMain.getGrcPftFrq().substring(3, 5)).toString();
				finMain.setGrcCpzFrq(frq);
			}

			tempError = FrequencyUtil.validateFrequency(finMain.getGrcCpzFrq());
			if (tempError == null) {
				isValidOtherFrq = true;
			} else {
				isValidOtherFrq = false;
			}

			//Capitalize Date
			if (isValidOtherFrq && finMain.getNextGrcCpzDate() == null) {
				Date cpzDate = FrequencyUtil.getNextDate(finMain.getGrcCpzFrq(), 1, finMain.getFinStartDate(),
						HolidayHandlerTypes.MOVE_NONE, false).getNextFrequencyDate();
				cpzDate = DateUtility.getDBDate(DateUtility.format(cpzDate, PennantConstants.DBDateFormat));

				if (finMain.getCalGrcEndDate() != null && cpzDate != null) {
					if (finMain.getCalGrcEndDate().compareTo(cpzDate) < 0) {
						cpzDate = finMain.getCalGrcEndDate();
					}
				}
				finMain.setNextGrcCpzDate(cpzDate);
			}
		}

		//Grace Payment Method
		if (finMain.isAllowGrcRepay() && StringUtils.isBlank(finMain.getGrcSchdMthd())) {
			if (financeType.isFinIsAlwGrcRepay()) {
				finMain.setGrcSchdMthd(financeType.getFinGrcSchdMthd());
			}
		}
	}

	/*
	 * ================================================================================================================
	 * DEFAULT FINANCE REPAYMENT DETAILS
	 * ================================================================================================================
	 */
	private void repayDefaulting(String vldGroup, FinanceDetail finDetail) {
		FinScheduleData finScheduleData = finDetail.getFinScheduleData();
		FinanceMain finMain = finScheduleData.getFinanceMain();
		FinanceType financeType = finScheduleData.getFinanceType();
		Promotion promotion = finScheduleData.getPromotion();

		boolean isValidRpyFrq = false;
		boolean isValidOtherFrq = false;

		// set default values from financeType
		finMain.setAllowRepayRvw(financeType.isFinIsRvwAlw());
		finMain.setAllowRepayCpz(financeType.isFinIsIntCpz());

		// return in case of CalGrcEndDate is null for allow grace finance
		if (finMain.isAllowGrcPeriod() && finMain.getCalGrcEndDate() == null) {
			return;
		}

		//Repay Rate Type
		if (StringUtils.isBlank(finMain.getRepayRateBasis())) {
			finMain.setRepayRateBasis(financeType.getFinRateType());
		}

		// 29-08-19 : Defaulting INterst Rate from Loan Type if not Provided
		if (StringUtils.isBlank(finMain.getRepayBaseRate())
				&& finMain.getRepayProfitRate().compareTo(BigDecimal.ZERO) == 0) {
			finMain.setRepayProfitRate(financeType.getFinIntRate());
		}

		//Repay Rate
		if (promotion != null) {
			finMain.setRepayBaseRate(null);
			finMain.setRepaySpecialRate(null);
			finMain.setRepayMargin(BigDecimal.ZERO);
			finMain.setRepayProfitRate(promotion.getActualInterestRate());
		} else {
			if (StringUtils.isBlank(finMain.getRepayBaseRate())
					&& finMain.getRepayProfitRate().compareTo(BigDecimal.ZERO) == 0
					&& StringUtils.isBlank(finMain.getRepaySpecialRate())
					&& finMain.getRepayMargin().compareTo(BigDecimal.ZERO) == 0) {
				if (StringUtils.isNotBlank(financeType.getFinBaseRate())) {
					finMain.setRepayBaseRate(financeType.getFinBaseRate());
					finMain.setRepaySpecialRate(financeType.getFinSplRate());
					finMain.setRepayMargin(financeType.getFinMargin());
				}
			}
		}

		//Schedule Method
		if (StringUtils.isBlank(finMain.getScheduleMethod())) {
			finMain.setScheduleMethod(financeType.getFinSchdMthd());
		}

		//Default Terms
		if (promotion != null) {
			finMain.setNumberOfTerms(promotion.getTenor() - promotion.getAdvEMITerms());
			finMain.setCalTerms(finMain.getNumberOfTerms());
		} else {
			if (finMain.getNumberOfTerms() == 0 && finMain.getMaturityDate() == null) {
				finMain.setNumberOfTerms(financeType.getFinDftTerms());
				finMain.setCalTerms(finMain.getNumberOfTerms());
			}
		}

		//Set Default Repayment Frequency
		if (StringUtils.isBlank(finMain.getRepayFrq())) {
			//Set Frequency based on Next Repayment Date
			if (finMain.getNextRepayDate() != null) {
				finMain.setRepayFrq(getDftFrequency(financeType.getFinRpyFrq(), finMain.getNextRepayDate()));

				//Set Frequency based on Maturity Date
			} else if (finMain.getMaturityDate() != null) {
				finMain.setRepayFrq(getDftFrequency(financeType.getFinRpyFrq(), finMain.getMaturityDate()));

				//Set Frequency based on allowed Frequency Days
			} else if (StringUtils.isNotEmpty(financeType.getFrequencyDays())) {
				finMain.setRepayFrq(getDftFrequency(financeType.getFinRpyFrq(), financeType.getFrequencyDays()));

				//Set Frequency based on Grace End Date
			} else if (finMain.getCalGrcEndDate() != null) {
				finMain.setRepayFrq(getDftFrequency(financeType.getFinRpyFrq(), finMain.getCalGrcEndDate()));

				//Set Frequency based on Finance Start Date
			} else {
				finMain.setRepayFrq(getDftFrequency(financeType.getFinRpyFrq(), finMain.getFinStartDate()));
			}
		}

		ErrorDetail tempError = FrequencyUtil.validateFrequency(finMain.getRepayFrq());
		if (tempError == null) {
			isValidRpyFrq = true;
		}

		//Set Default Next Repayment Date
		if (isValidRpyFrq && finMain.getNextRepayDate() == null) {
			//	Date nextDate = getNextDftDate(finMain.getRepayFrq(), finMain.getCalGrcEndDate(),financeType.getFddLockPeriod());
			Date nextDate = FrequencyUtil
					.getNextDate(finMain.getRepayFrq(), 1, finMain.getCalGrcEndDate(), HolidayHandlerTypes.MOVE_NONE,
							false, finMain.isAllowGrcPeriod() ? 0 : financeType.getFddLockPeriod())
					.getNextFrequencyDate();
			nextDate = DateUtility.getDBDate(DateUtility.format(nextDate, PennantConstants.DBDateFormat));
			finMain.setNextRepayDate(nextDate);
		}

		//Default Terms and Maturity Date
		maturityDefaulting(finScheduleData);

		//Repay Profit Frequency & Date
		if (StringUtils.isBlank(finMain.getRepayPftFrq()) && isValidRpyFrq) {
			String frq = new StringBuilder(5).append(financeType.getFinDftIntFrq().substring(0, 3))
					.append(finMain.getRepayFrq().substring(3, 5)).toString();
			finMain.setRepayPftFrq(frq);
		}

		tempError = FrequencyUtil.validateFrequency(finMain.getRepayPftFrq());
		if (tempError == null) {
			isValidOtherFrq = true;
		}

		//Next Profit Date
		if (isValidOtherFrq && finMain.getNextRepayPftDate() == null) {
			Date nextRpyPftDate = FrequencyUtil
					.getNextDate(finMain.getRepayPftFrq(), 1, finMain.getCalGrcEndDate(), HolidayHandlerTypes.MOVE_NONE,
							false, finMain.isAllowGrcPeriod() ? 0 : financeType.getFddLockPeriod())
					.getNextFrequencyDate();
			nextRpyPftDate = DateUtility.getDBDate(DateUtility.format(nextRpyPftDate, PennantConstants.DBDateFormat));

			if (finMain.getCalMaturity() != null && nextRpyPftDate != null) {
				if (finMain.getCalMaturity().compareTo(nextRpyPftDate) < 0) {
					nextRpyPftDate = finMain.getCalMaturity();
				}
			}
			finMain.setNextRepayPftDate(nextRpyPftDate);
		}

		//Repay Profit Review Frequency & Date
		if (finMain.isAllowRepayRvw()) {
			if (StringUtils.isBlank(finMain.getRepayRvwFrq()) && isValidRpyFrq) {
				if (!StringUtils.startsWith(financeType.getFinRvwFrq(), FrequencyCodeTypes.FRQ_DAILY)) {
					String frq = new StringBuilder(5).append(financeType.getFinRvwFrq().substring(0, 3))
							.append(finMain.getRepayFrq().substring(3, 5)).toString();
					finMain.setRepayRvwFrq(frq);
				} else {
					finMain.setRepayRvwFrq(financeType.getFinRvwFrq());
				}
			}

			tempError = FrequencyUtil.validateFrequency(finMain.getRepayRvwFrq());
			if (tempError == null) {
				isValidOtherFrq = true;
			} else {
				isValidOtherFrq = false;
			}

			//Next Profit Review Date
			if (isValidOtherFrq && finMain.getNextRepayRvwDate() == null) {
				Date nextRpyRvwDate = null;
				if (ImplementationConstants.ALLOW_FDD_ON_RVW_DATE) {
					nextRpyRvwDate = FrequencyUtil
							.getNextDate(finMain.getRepayRvwFrq(), 1, finMain.getCalGrcEndDate(),
									HolidayHandlerTypes.MOVE_NONE, false, financeType.getFddLockPeriod())
							.getNextFrequencyDate();
				} else {
					nextRpyRvwDate = FrequencyUtil.getNextDate(finMain.getRepayRvwFrq(), 1, finMain.getCalGrcEndDate(),
							HolidayHandlerTypes.MOVE_NONE, false, 0).getNextFrequencyDate();
				}

				nextRpyRvwDate = DateUtility
						.getDBDate(DateUtility.format(nextRpyRvwDate, PennantConstants.DBDateFormat));

				if (finMain.getCalMaturity() != null && nextRpyRvwDate != null) {
					if (finMain.getCalMaturity().compareTo(nextRpyRvwDate) < 0) {
						nextRpyRvwDate = finMain.getCalMaturity();
					}
				}
				finMain.setNextRepayRvwDate(nextRpyRvwDate);
			}
		}

		//Repay capitalize Frequency & Date
		if (finMain.isAllowRepayCpz()) {
			if (StringUtils.isBlank(finMain.getRepayCpzFrq()) && isValidRpyFrq) {
				String frq = new StringBuilder(5).append(financeType.getFinCpzFrq().substring(0, 3))
						.append(finMain.getRepayFrq().substring(3, 5)).toString();
				finMain.setRepayCpzFrq(frq);
			}

			tempError = FrequencyUtil.validateFrequency(finMain.getRepayCpzFrq());
			if (tempError == null) {
				isValidOtherFrq = true;
			} else {
				isValidOtherFrq = false;
			}
			//Next Capitalize Date
			if (isValidOtherFrq && finMain.getNextRepayCpzDate() == null) {
				Date cpzDate = FrequencyUtil
						.getNextDate(finMain.getRepayCpzFrq(), 1, finMain.getCalGrcEndDate(),
								HolidayHandlerTypes.MOVE_NONE, false, financeType.getFddLockPeriod())
						.getNextFrequencyDate();
				cpzDate = DateUtility.getDBDate(DateUtility.format(cpzDate, PennantConstants.DBDateFormat));

				if (finMain.getCalMaturity() != null && cpzDate != null) {
					if (finMain.getCalMaturity().compareTo(cpzDate) < 0) {
						cpzDate = finMain.getCalMaturity();
					}
				}
				finMain.setNextRepayCpzDate(cpzDate);
			}
		}

		//BPI Treatment
		if (finMain.isAlwBPI()) {
			if (StringUtils.isBlank(finMain.getBpiTreatment())) {
				finMain.setBpiTreatment(financeType.getBpiTreatment());
			}
			finMain.setBpiPftDaysBasis(financeType.getBpiPftDaysBasis());
		} else {
			finMain.setBpiTreatment(FinanceConstants.BPI_NO);
		}

		finMain.setFixedRateTenor(financeType.getFixedRateTenor());
		finMain.setEqualRepay(financeType.isEqualRepayment());
		//UnPlanned EMI Holiday defaulting
		if (financeType.isAlwUnPlanEmiHoliday()) {
			if (finMain.getMaxUnplannedEmi() <= 0) {
				finMain.setMaxUnplannedEmi(financeType.getMaxUnplannedEmi());
			}
			if (finMain.getUnPlanEMIHLockPeriod() <= 0) {
				finMain.setUnPlanEMIHLockPeriod(financeType.getUnPlanEMIHLockPeriod());
			}
			if (!finMain.isUnPlanEMICpz()) {
				finMain.setUnPlanEMICpz(financeType.isUnPlanEMICpz());
			}
		}

		//UnPlanned EMI Holiday defaulting
		if (financeType.isAlwUnPlanEmiHoliday()) {
			if (finMain.getMaxUnplannedEmi() <= 0) {
				finMain.setMaxUnplannedEmi(financeType.getMaxUnplannedEmi());
			}
			if (finMain.getUnPlanEMIHLockPeriod() <= 0) {
				finMain.setUnPlanEMIHLockPeriod(financeType.getUnPlanEMIHLockPeriod());
			}
			if (!finMain.isUnPlanEMICpz()) {
				finMain.setUnPlanEMICpz(financeType.isUnPlanEMICpz());
			}
		}
	}

	private void overdueDefaulting(String vldGroup, FinanceDetail finDetail) {
		FinScheduleData finScheduleData = finDetail.getFinScheduleData();
		FinanceType financeType = finScheduleData.getFinanceType();
		if (finScheduleData.getFinODPenaltyRate() == null && financeType.isApplyODPenalty()) {
			FinODPenaltyRate finODPenaltyRate = new FinODPenaltyRate();
			finODPenaltyRate.setApplyODPenalty(financeType.isApplyODPenalty());
			finODPenaltyRate.setODIncGrcDays(financeType.isODIncGrcDays());
			finODPenaltyRate.setODChargeCalOn(financeType.getODChargeCalOn());
			finODPenaltyRate.setODGraceDays(financeType.getODGraceDays());
			finODPenaltyRate.setODChargeType(financeType.getODChargeType());
			finODPenaltyRate.setODChargeAmtOrPerc(financeType.getODChargeAmtOrPerc());
			finODPenaltyRate.setODAllowWaiver(financeType.isODAllowWaiver());
			finODPenaltyRate.setODMaxWaiverPerc(financeType.getODMaxWaiverPerc());
			finODPenaltyRate.setODRuleCode(financeType.getODRuleCode());
			finScheduleData.setFinODPenaltyRate(finODPenaltyRate);
		} else if (finScheduleData.getFinODPenaltyRate() != null && !financeType.isApplyODPenalty()) {
			FinODPenaltyRate finODPenaltyRate = finScheduleData.getFinODPenaltyRate();
			if (StringUtils.equals(finODPenaltyRate.getODChargeType(), FinanceConstants.PENALTYTYPE_PERC_ONETIME)
					|| StringUtils.equals(finODPenaltyRate.getODChargeType(),
							FinanceConstants.PENALTYTYPE_PERC_ON_DUEDAYS)
					|| StringUtils.equals(finODPenaltyRate.getODChargeType(),
							FinanceConstants.PENALTYTYPE_PERC_ON_PD_MTH)) {
				BigDecimal totPerc = PennantApplicationUtil.unFormateAmount(finODPenaltyRate.getODChargeAmtOrPerc(), 2);
				finODPenaltyRate.setODChargeAmtOrPerc(totPerc);
			}
		}
	}

	/*
	 * _______________________________________________________________________________________________________________
	 * DEFAULT STEP DETAILS
	 * _______________________________________________________________________________________________________________
	 */

	private void stepDefaulting(String vldGroup, FinScheduleData finScheduleData) {
		FinanceMain finMain = finScheduleData.getFinanceMain();
		FinanceType financeType = finScheduleData.getFinanceType();

		//Manual Steps? Default Step type
		if (finMain.isAlwManualSteps()) {
			if (StringUtils.isBlank(finMain.getStepType())) {
				finMain.setStepType(financeType.getDftStepPolicyType());
			}
		} else {
			//Default Step Policy
			if (StringUtils.isBlank(finMain.getStepPolicy())) {
				finMain.setStepPolicy(financeType.getDftStepPolicy());
				finMain.setStepType(financeType.getDftStepPolicyType());
			}
		}
	}

	/*
	 * _______________________________________________________________________________________________________________
	 * GET Frequency based on Allowed Frequency Days
	 * _______________________________________________________________________________________________________________
	 */
	private String getDftFrequency(String frq, String alwdFrqDays) {

		if (StringUtils.isNotBlank(frq)) {
			if (!StringUtils.startsWith(frq, FrequencyCodeTypes.FRQ_DAILY)) {

				// Making into List
				List<String> alwdDays = Arrays.asList(alwdFrqDays.split(","));

				//Sorting available list
				Collections.sort(alwdDays);
				return frq.substring(0, 3).concat(StringUtils.leftPad(alwdDays.get(0), 2, "0"));
			}
		}

		return frq;
	}

	/*
	 * _______________________________________________________________________________________________________________
	 * GET Frequency based on Date
	 * _______________________________________________________________________________________________________________
	 */
	private String getDftFrequency(String frq, Date frqDate) {

		if (StringUtils.isNotBlank(frq)) {
			if (!StringUtils.startsWith(frq, FrequencyCodeTypes.FRQ_DAILY)) {
				String frqDay = String.valueOf(DateUtility.getDay(frqDate));
				frqDay = StringUtils.leftPad(frqDay, 2, "0");
				frq = new StringBuilder(5).append(frq.substring(0, 3)).append(frqDay).toString();
			}
		}

		return frq;
	}

	/*
	 * _______________________________________________________________________________________________________________
	 * Validate Interest Days Basis
	 * _______________________________________________________________________________________________________________
	 */
	public static boolean isValidateIDB(String IDB) {
		if (!StringUtils.equals(IDB, CalculationConstants.IDB_30E360)
				&& !StringUtils.equals(IDB, CalculationConstants.IDB_30E360I)
				&& !StringUtils.equals(IDB, CalculationConstants.IDB_30E360IH)
				&& !StringUtils.equals(IDB, CalculationConstants.IDB_30EP360)
				&& !StringUtils.equals(IDB, CalculationConstants.IDB_30U360)
				&& !StringUtils.equals(IDB, CalculationConstants.IDB_ACT_360)
				&& !StringUtils.equals(IDB, CalculationConstants.IDB_ACT_365FIXED)
				&& !StringUtils.equals(IDB, CalculationConstants.IDB_ACT_365LEAP)
				&& !StringUtils.equals(IDB, CalculationConstants.IDB_ACT_365LEAPS)
				&& !StringUtils.equals(IDB, CalculationConstants.IDB_ACT_ISDA)
				&& !StringUtils.equals(IDB, CalculationConstants.IDB_BY_PERIOD)) {
			return false;
		}

		return true;
	}

	/*
	 * _______________________________________________________________________________________________________________
	 * DEFAULT Grace Terms and Grace End Date
	 * _______________________________________________________________________________________________________________
	 */
	private void graceEndDefaulting(FinScheduleData finScheduleData) {
		FinanceMain finMain = finScheduleData.getFinanceMain();
		FinanceType financeType = finScheduleData.getFinanceType();

		//Both Terms and Grace End Date not present
		if (finMain.getGraceTerms() == 0 && finMain.getGrcPeriodEndDate() == null) {
			return;
		}

		//Both Terms and Grace End Date present
		if (finMain.getGraceTerms() != 0 && finMain.getGrcPeriodEndDate() != null) {
			return;
		}

		//Default Calculated Grace End Date using terms
		if (finMain.getGraceTerms() > 0) {
			finMain.setCalGrcTerms(finMain.getGraceTerms());
			if (StringUtils.isNotBlank(finMain.getGrcPftFrq()) && finMain.getGrcPeriodEndDate() == null) {
				List<Calendar> scheduleDateList = FrequencyUtil
						.getNextDate(finMain.getGrcPftFrq(), finMain.getGraceTerms(), finMain.getFinStartDate(),
								HolidayHandlerTypes.MOVE_NONE, false, financeType.getFddLockPeriod())
						.getScheduleList();

				Date geDate = null;
				if (scheduleDateList != null) {
					Calendar calendar = scheduleDateList.get(scheduleDateList.size() - 1);
					geDate = DateUtility
							.getDBDate(DateUtility.format(calendar.getTime(), PennantConstants.DBDateFormat));
				}

				finMain.setCalGrcEndDate(geDate);
			}
			return;
		}

		//Default Calculated Terms based on Grace End Date
		finMain.setCalGrcEndDate(finMain.getGrcPeriodEndDate());
		if (StringUtils.isNotBlank(finMain.getGrcPftFrq()) && finMain.getNextGrcPftDate() != null) {
			int terms = FrequencyUtil.getTerms(finMain.getGrcPftFrq(), finMain.getNextGrcPftDate(),
					finMain.getGrcPeriodEndDate(), true, true).getTerms();
			finMain.setCalGrcTerms(terms);
		}

		return;

	}

	/*
	 * _______________________________________________________________________________________________________________
	 * DEFAULT Terms and Maturity Date
	 * _______________________________________________________________________________________________________________
	 */
	private void maturityDefaulting(FinScheduleData finScheduleData) {
		FinanceMain finMain = finScheduleData.getFinanceMain();
		FinanceType financeType = finScheduleData.getFinanceType();

		//Both Terms and Maturity not present
		if (finMain.getNumberOfTerms() == 0 && finMain.getMaturityDate() == null) {
			if (financeType.getFinDftTerms() > 0) {
				finMain.setNumberOfTerms(financeType.getFinDftTerms());
			} else {
				return;
			}
		}

		//Both Terms and Maturity Date present
		if (finMain.getNumberOfTerms() != 0 && finMain.getMaturityDate() != null) {
			return;
		}

		//Default Calculated Maturity Date using terms
		if (finMain.getNumberOfTerms() > 0) {
			if (!finScheduleData.getFinanceMain().getProductCategory().equals(FinanceConstants.PRODUCT_ODFACILITY)) {
				finMain.setCalTerms(finMain.getNumberOfTerms());
				if (StringUtils.isNotBlank(finMain.getRepayFrq()) && finMain.getNextRepayDate() != null) {
					List<Calendar> scheduleDateList = FrequencyUtil
							.getNextDate(finMain.getRepayFrq(), finMain.getNumberOfTerms(), finMain.getNextRepayDate(),
									HolidayHandlerTypes.MOVE_NONE, true, 0)
							.getScheduleList();

					if (scheduleDateList != null) {
						Calendar calendar = scheduleDateList.get(scheduleDateList.size() - 1);
						Date matDate = DateUtility
								.getDBDate(DateUtility.format(calendar.getTime(), PennantConstants.DBDateFormat));
						finMain.setCalMaturity(matDate);
					}
				}
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

			return;
		}

		//Default Calculated Terms based on Maturity Date
		finMain.setCalMaturity(finMain.getMaturityDate());
		if (StringUtils.isNotBlank(finMain.getRepayFrq()) && finMain.getNextRepayDate() != null) {
			if (!finScheduleData.getFinanceMain().getProductCategory().equals(FinanceConstants.PRODUCT_ODFACILITY)) {
				int terms = FrequencyUtil.getTerms(finMain.getRepayFrq(), finMain.getNextRepayDate(),
						finMain.getMaturityDate(), true, true).getTerms();
				finMain.setCalTerms(terms);
			}

		}

		return;

	}

	/**
	 * 
	 * @param financeDetail
	 */
	public void doFinanceDetailDefaulting(FinanceDetail financeDetail) {
		if (financeDetail != null) {
			String finReference = financeDetail.getFinReference();
			FinanceMain finMain = financeMainDAO.getFinanceDetailsForService(finReference, "_Temp", false);
			if (financeDetail.getAdvancePaymentsList() != null) {
				for (FinAdvancePayments payment : financeDetail.getAdvancePaymentsList()) {
					payment.setLLDate(payment.getLlDate() == null ? finMain.getFinStartDate() : payment.getLlDate());
				}
			}

			if (financeDetail.getMandate() != null) {
				Mandate mandate = financeDetail.getMandate();
				mandate.setStartDate(
						mandate.getStartDate() == null ? finMain.getFinStartDate() : mandate.getStartDate());
				if (!mandate.isOpenMandate() && mandate.getExpiryDate() == null) {
					mandate.setExpiryDate(DateUtility.addDays(finMain.getMaturityDate(), 1));
				}

			}
		}
	}

	/*
	 * ################################################################################################################
	 * DEFAULT SETTER GETTER METHODS
	 * ################################################################################################################
	 */

	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

	public void setFinanceTypeDAO(FinanceTypeDAO financeTypeDAO) {
		this.financeTypeDAO = financeTypeDAO;
	}

	public void setBranchService(BranchService branchService) {
		this.branchService = branchService;
	}

	public PromotionService getPromotionService() {
		return promotionService;
	}

	public void setPromotionService(PromotionService promotionService) {
		this.promotionService = promotionService;
	}

	public CurrencyDAO getCurrencyDAO() {
		return currencyDAO;
	}

	public void setCurrencyDAO(CurrencyDAO currencyDAO) {
		this.currencyDAO = currencyDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}
}
