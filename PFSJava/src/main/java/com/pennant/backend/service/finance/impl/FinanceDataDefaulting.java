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
import com.pennant.app.constants.HolidayHandlerTypes;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.FrequencyUtil;
import com.pennant.app.util.SessionUserDetails;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.applicationmaster.BranchDAO;
import com.pennant.backend.dao.applicationmaster.CurrencyDAO;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.LoggedInUser;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinODPenaltyRate;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rmtmasters.Promotion;
import com.pennant.backend.service.rmtmasters.PromotionService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;

public class FinanceDataDefaulting {

	private CustomerDAO			customerDAO;
	private FinanceTypeDAO		financeTypeDAO;
	private BranchDAO			branchDAO;
	private PromotionService	promotionService;
	private CurrencyDAO			currencyDAO;


	public FinanceDataDefaulting() {
		super();
	}

	// Constructor Details for Methods
	public FinScheduleData defaultFinance(String vldGroup, FinScheduleData finScheduleData) {

		//Validate Fields data (Excluding Base & Special rates Validations)
		validateMasterData(vldGroup, finScheduleData);
		
		if (!finScheduleData.getErrorDetails().isEmpty()) {
			return finScheduleData;
		}

		// Date formats
		setDefaultDateFormats(finScheduleData.getFinanceMain());
		
		// Basic Details Defaulting
		basicDefaulting(vldGroup, finScheduleData);

		// Grace Details Defaulting
		graceDefaulting(vldGroup, finScheduleData);

		// Repayments Details Defaulting
		repayDefaulting(vldGroup, finScheduleData);

		// Overdue penalty rates defaulting
		if(StringUtils.equals(PennantConstants.VLD_CRT_LOAN, vldGroup)){
		overdueDefaulting(vldGroup, finScheduleData);
		}
		return finScheduleData;

	}

	/*
	 * ################################################################################################################
	 * MAIN METHODS
	 * ################################################################################################################
	 */

	private void setDefaultDateFormats(FinanceMain financeMain) {
		if(financeMain.getFinStartDate() != null) {
			financeMain.setFinStartDate(DateUtility.getDBDate(DateUtility.formatDate(financeMain.getFinStartDate(),
					PennantConstants.DBDateFormat)));
		}
		
		if(financeMain.getGrcPeriodEndDate() != null){
			financeMain.setGrcPeriodEndDate(DateUtility.getDBDate(DateUtility.formatDate(financeMain.getGrcPeriodEndDate(),
					PennantConstants.DBDateFormat)));
		}
		if(financeMain.getNextGrcPftDate() != null){
			financeMain.setNextGrcPftDate(DateUtility.getDBDate(DateUtility.formatDate(financeMain.getNextGrcPftDate(),
					PennantConstants.DBDateFormat)));
		}
		if(financeMain.getNextGrcPftRvwDate() != null){
			financeMain.setNextGrcPftRvwDate(DateUtility.getDBDate(DateUtility.formatDate(financeMain.getNextGrcPftRvwDate(),
					PennantConstants.DBDateFormat)));
		}
		if(financeMain.getNextGrcCpzDate()!= null){
			financeMain.setNextGrcCpzDate(DateUtility.getDBDate(DateUtility.formatDate(financeMain.getNextGrcCpzDate(),
					PennantConstants.DBDateFormat)));
		}
		if(financeMain.getNextRepayDate()!= null){
			financeMain.setNextRepayDate(DateUtility.getDBDate(DateUtility.formatDate(financeMain.getNextRepayDate(),
					PennantConstants.DBDateFormat)));
		}
		if(financeMain.getNextRepayPftDate()!= null){
			financeMain.setNextRepayPftDate(DateUtility.getDBDate(DateUtility.formatDate(financeMain.getNextRepayPftDate(),
					PennantConstants.DBDateFormat)));
		}
		if(financeMain.getNextRepayRvwDate()!= null){
			financeMain.setNextRepayRvwDate(DateUtility.getDBDate(DateUtility.formatDate(financeMain.getNextRepayRvwDate(),
					PennantConstants.DBDateFormat)));
		}
		if(financeMain.getNextRepayCpzDate()!= null){
			financeMain.setNextRepayCpzDate(DateUtility.getDBDate(DateUtility.formatDate(financeMain.getNextRepayCpzDate(),
					PennantConstants.DBDateFormat)));
		}
		if(financeMain.getMaturityDate()!= null){
			financeMain.setMaturityDate(DateUtility.getDBDate(DateUtility.formatDate(financeMain.getMaturityDate(),
					PennantConstants.DBDateFormat)));
		}
		if(financeMain.getNextRolloverDate()!= null){
			financeMain.setNextRolloverDate(DateUtility.getDBDate(DateUtility.formatDate(financeMain.getNextRolloverDate(),
					PennantConstants.DBDateFormat)));
		}
	}

	/*
	 * ================================================================================================================
	 * VALIDATE STATIC DATA
	 * ================================================================================================================
	 */
	private void validateMasterData(String vldGroup, FinScheduleData finScheduleData) {
		List<ErrorDetails> errorDetails = new ArrayList<ErrorDetails>();
		FinanceMain finMain = finScheduleData.getFinanceMain();

		//Validate Finance Type (Mandatory for Defaulting)
		FinanceType financeType = financeTypeDAO.getOrgFinanceTypeByID(finMain.getFinType(), "_ORGView");
		if (financeType == null || !financeType.isFinIsActive()) {
			Promotion promotion = promotionService.getApprovedPromotionById(finMain.getFinType(),
					FinanceConstants.MODULEID_PROMOTION, true);
			if (promotion == null || !promotion.isActive()) {
				String[] valueParm = new String[1];
				valueParm[0] = finMain.getFinType();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90202", valueParm)));
			} else {
				financeType = financeTypeDAO.getOrgFinanceTypeByID(promotion.getFinType(), "_ORGView");
				if (financeType != null) {
					financeType.setPromotionType(true);
					financeType.setFinTypeFeesList(promotion.getFinTypeFeesList());
					financeType.setFInTypeFromPromotiion(promotion);
					financeType.setFinTypeInsurances(promotion.getFinTypeInsurancesList());
					financeType.setFinTypeAccountingList(promotion.getFinTypeAccountingList());
				} else {
					String[] valueParm = new String[1];
					valueParm[0] = promotion.getFinType();
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90202", valueParm)));

				}
			}
		}
		if (financeType != null) {
			//Validate Finance Currency
			finScheduleData.setFinanceType(financeType);
			if (StringUtils.isBlank(finMain.getFinCcy())) {
				String ccy = SysParamUtil.getAppCurrency();
				finMain.setFinCcy(ccy);
			}

			// validate finance branch
			if (StringUtils.isNotBlank(finMain.getFinBranch())) {
				Branch branch = branchDAO.getBranchById(finMain.getFinBranch(), "");
				if (branch == null) {
					String[] valueParm = new String[1];
					valueParm[0] = finMain.getFinBranch();
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90129", valueParm)));
				}
			}

			//Validate Interest Days basis
			if (StringUtils.isNotBlank(finMain.getProfitDaysBasis()) && !isValidateIDB(finMain.getProfitDaysBasis())) {
				String[] valueParm = new String[1];
				valueParm[0] = finMain.getProfitDaysBasis();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90209", valueParm)));
			}

			//Validate Repayment Method
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
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90307", valueParm)));
				}
			}

			//Step Policy
			if (StringUtils.isNotBlank(finMain.getStepPolicy())) {
				if (!StringUtils.containsIgnoreCase(financeType.getAlwdStepPolicies(), finMain.getStepPolicy())) {
					String[] valueParm = new String[2];
					valueParm[0] = finMain.getFinType();
					valueParm[1] = financeType.getAlwdStepPolicies();
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90147", valueParm)));
				}
			}

			//Step Type
			if (StringUtils.isNotBlank(finMain.getStepType())) {
				if (!StringUtils.equals(finMain.getStepType(), FinanceConstants.STEPTYPE_EMI)
						&& !StringUtils.equals(finMain.getStepType(), FinanceConstants.STEPTYPE_PRIBAL)) {
					String[] valueParm = new String[2];
					valueParm[0] = new StringBuilder(10).append(FinanceConstants.STEPTYPE_EMI).append(" & ")
							.append(FinanceConstants.STEPTYPE_PRIBAL).toString();
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90148", valueParm)));
				}
			}

			//Validate Grace Interest Days basis
			if (StringUtils.isNotBlank(finMain.getGrcProfitDaysBasis())
					&& !isValidateIDB(finMain.getGrcProfitDaysBasis())) {
				String[] valueParm = new String[1];
				valueParm[0] = finMain.getGrcProfitDaysBasis();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90209", valueParm)));
			}

			//Validate Grace Profit Frequency
			if (StringUtils.isNotBlank(finMain.getGrcPftFrq())) {
				ErrorDetails tempError = FrequencyUtil.validateFrequency(finMain.getGrcPftFrq());
				if (tempError != null) {
					String[] valueParm = new String[2];
					valueParm[0] = "Grace";
					valueParm[1] = finMain.getGrcPftFrq();
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90156", valueParm)));
				}
			}

			//Grace Rate Type/Rate Basis
			if (StringUtils.isNotBlank(finMain.getGrcRateBasis()) && 
					!StringUtils.equals(finMain.getGrcRateBasis(), PennantConstants.List_Select)) {
				if (!StringUtils.equals(finMain.getGrcRateBasis(), CalculationConstants.RATE_BASIS_F)
						&& !StringUtils.equals(finMain.getGrcRateBasis(), CalculationConstants.RATE_BASIS_R)) {
					String[] valueParm = new String[1];
					valueParm[0] = finMain.getGrcRateBasis();
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90211", valueParm)));
				}
			}

			//Validate Grace Profit Review Frequency
			if (StringUtils.isNotBlank(finMain.getGrcPftRvwFrq())) {
				ErrorDetails tempError = FrequencyUtil.validateFrequency(finMain.getGrcPftRvwFrq());
				if (tempError != null) {
					String[] valueParm = new String[2];
					valueParm[0] = "Grace";
					valueParm[1] = finMain.getGrcPftRvwFrq();
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90157", valueParm)));
				}
			}

			//Validate Grace Capitalize Frequency
			if (StringUtils.isNotBlank(finMain.getGrcCpzFrq())) {
				ErrorDetails tempError = FrequencyUtil.validateFrequency(finMain.getGrcCpzFrq());
				if (tempError != null) {
					String[] valueParm = new String[2];
					valueParm[0] = "Grace";
					valueParm[1] = finMain.getGrcCpzFrq();
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90158", valueParm)));
				}
			}

			//Grace Payment Method (Schedule Method)
			if (StringUtils.isNotBlank(finMain.getGrcSchdMthd())) {
				if (!StringUtils.equals(finMain.getGrcSchdMthd(), CalculationConstants.SCHMTHD_NOPAY)
						&& !StringUtils.equals(finMain.getGrcSchdMthd(), CalculationConstants.SCHMTHD_PFT)
						&& !StringUtils.equals(finMain.getGrcSchdMthd(), CalculationConstants.SCHMTHD_GRCENDPAY)) {
					String[] valueParm = new String[2];
					valueParm[0] = "Grace";
					valueParm[1] = finMain.getGrcSchdMthd();
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90210", valueParm)));
				}
			}

			//Rate Basis
			if (StringUtils.isNotBlank(finMain.getRepayRateBasis())) {
				if (!StringUtils.equals(finMain.getRepayRateBasis(), CalculationConstants.RATE_BASIS_C)
						&& !StringUtils.equals(finMain.getRepayRateBasis(), CalculationConstants.RATE_BASIS_F)
						&& !StringUtils.equals(finMain.getRepayRateBasis(), CalculationConstants.RATE_BASIS_R)) {
					String[] valueParm = new String[1];
					valueParm[0] = finMain.getRepayRateBasis();
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90211", null)));
				}
			}

			//Repayment Schedule Method
			if (StringUtils.isNotBlank(finMain.getScheduleMethod())) {
				if (!StringUtils.equals(finMain.getScheduleMethod(), CalculationConstants.SCHMTHD_NOPAY)
						&& !StringUtils.equals(finMain.getScheduleMethod(), CalculationConstants.SCHMTHD_EQUAL)
						&& !StringUtils.equals(finMain.getScheduleMethod(), CalculationConstants.SCHMTHD_PFT)
						&& !StringUtils.equals(finMain.getScheduleMethod(), CalculationConstants.SCHMTHD_PRI)
						&& !StringUtils.equals(finMain.getScheduleMethod(), CalculationConstants.SCHMTHD_PRI_PFT)) {
					String[] valueParm = new String[2];
					valueParm[0] = "Repayment";
					valueParm[1] = finMain.getScheduleMethod();
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90210", valueParm)));
				}
			}

			//Validate Payment Frequency
			if (StringUtils.isNotBlank(finMain.getRepayFrq())) {
				ErrorDetails tempError = FrequencyUtil.validateFrequency(finMain.getRepayFrq());
				if (tempError != null) {
					String[] valueParm = new String[2];
					valueParm[0] = "Repay";
					valueParm[1] = finMain.getRepayFrq();
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90159", valueParm)));
				}
			}

			//Validate Repayment Interest Frequency
			if (StringUtils.isNotBlank(finMain.getRepayPftFrq())) {
				ErrorDetails tempError = FrequencyUtil.validateFrequency(finMain.getRepayPftFrq());
				if (tempError != null) {
					String[] valueParm = new String[2];
					valueParm[0] = "Repay";
					valueParm[1] = finMain.getRepayPftFrq();
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90156", valueParm)));
				}
			}

			//Validate Repayment Interest Review Frequency
			if (StringUtils.isNotBlank(finMain.getRepayRvwFrq())) {
				ErrorDetails tempError = FrequencyUtil.validateFrequency(finMain.getRepayRvwFrq());
				if (tempError != null) {
					String[] valueParm = new String[2];
					valueParm[0] = "Repay";
					valueParm[1] = finMain.getRepayRvwFrq();
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90157", valueParm)));
				}
			}

			//Validate Repayment Capitalize Frequency
			if (StringUtils.isNotBlank(finMain.getRepayCpzFrq())) {
				ErrorDetails tempError = FrequencyUtil.validateFrequency(finMain.getRepayCpzFrq());
				if (tempError != null) {
					String[] valueParm = new String[2];
					valueParm[0] = "Repay";
					valueParm[1] = finMain.getRepayCpzFrq();
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90158", valueParm)));
				}
			}

			//Validate Rollover Frequency
			if (StringUtils.isNotBlank(finMain.getRolloverFrq())) {
				ErrorDetails tempError = FrequencyUtil.validateFrequency(finMain.getRolloverFrq());
				if (tempError != null) {
					String[] valueParm = new String[2];
					valueParm[0] = "Rollover";
					valueParm[1] = finMain.getRolloverFrq();
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90160", valueParm)));
				}
			}

			//BPI Treatment
			if (StringUtils.isNotBlank(finMain.getBpiTreatment())) {
				if (!StringUtils.equals(finMain.getBpiTreatment(), FinanceConstants.BPI_NO)
						&& !StringUtils.equals(finMain.getBpiTreatment(), FinanceConstants.BPI_DISBURSMENT)
						&& !StringUtils.equals(finMain.getBpiTreatment(), FinanceConstants.BPI_SCHEDULE)
						&& !StringUtils.equals(finMain.getBpiTreatment(), FinanceConstants.BPI_CAPITALIZE)
						&& !StringUtils.equals(finMain.getBpiTreatment(), FinanceConstants.BPI_SCHD_FIRSTEMI)) {
					String[] valueParm = new String[2];
					valueParm[0] = finMain.getBpiTreatment();
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90185", valueParm)));

				}
			}
		} else {
			String[] valueParm = new String[1];
			valueParm[0] = finMain.getFinType();
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails("90202", valueParm)));
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

	private void basicDefaulting(String vldGroup, FinScheduleData finScheduleData) {
		FinanceMain finMain = finScheduleData.getFinanceMain();
		FinanceType financeType = finScheduleData.getFinanceType();

		//Default Loan Start Date
		if (finMain.getFinStartDate() == null) {
			finMain.setFinStartDate(DateUtility.getAppDate());
		}

		// Finance amount 
		if(finMain.getFinAmount() == null) {
			finMain.setFinAmount(BigDecimal.ZERO);
		}
		
		//Set Product Category
		finMain.setProductCategory(financeType.getProductCategory());
		finMain.setFinCategory(financeType.getFinCategory());

		//Default Finance Currency
		if (StringUtils.isBlank(finMain.getFinCcy())) {
			finMain.setFinCcy(financeType.getFinCcy());
		}

		// Default finance branch
		if (StringUtils.isBlank(finMain.getFinBranch())) {
			Customer customer = null;
			// Get Customer
			if (StringUtils.isNotBlank(finMain.getLovDescCustCIF())) {
				customer = customerDAO.getCustomerByCIF(finMain.getLovDescCustCIF(), "");
			}

			if (customer != null) {
				finMain.setFinBranch(customer.getCustDftBranch());
			} else {
				LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
				finMain.setFinBranch(userDetails.getBranchCode());
			}
		}

		Branch branch = branchDAO.getBranchById(finMain.getFinBranch(), "");
		if (branch != null) {
			finMain.setSwiftBranchCode(branch.getBranchSwiftBrnCde());
		}
		
		//Default Interest Days Basis
		if (StringUtils.isBlank(finMain.getProfitDaysBasis())) {
			finMain.setProfitDaysBasis(financeType.getFinDaysCalType());
		}
		
		// Review rate applied for
		finMain.setRvwRateApplFor(financeType.getFinRvwRateApplFor());
		finMain.setFinCategory(financeType.getFinCategory());
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
		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		finMain.setInitiateUser(userDetails.getLoginUsrID());
		finMain.setInitiateDate(DateUtility.getAppDate());
		finMain.setShariaStatus(PennantConstants.SHARIA_STATUS_NOTREQUIRED);
		finMain.setCalRoundingMode(financeType.getRoundingMode());
		finMain.setRoundingTarget(financeType.getRoundingTarget());
	}

	/*
	 * ================================================================================================================
	 * DEFAULT FINANCE GRACE DETAILS
	 * ================================================================================================================
	 */
	private void graceDefaulting(String vldGroup, FinScheduleData finScheduleData) {
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
		if(StringUtils.isBlank(finMain.getGrcSchdMthd())) {
			finMain.setGrcSchdMthd("");
		}
		
		//Grace Rate
		if (StringUtils.isBlank(finMain.getGraceBaseRate()) && finMain.getGrcPftRate().compareTo(zeroValue) == 0
				&& StringUtils.isBlank(finMain.getGraceSpecialRate()) && finMain.getGrcMargin().compareTo(zeroValue) == 0) {
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

		ErrorDetails tempError = FrequencyUtil.validateFrequency(finMain.getGrcPftFrq());
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
				Date rvwDate = FrequencyUtil.getNextDate(finMain.getGrcPftRvwFrq(), 1,
						finMain.getFinStartDate(), HolidayHandlerTypes.MOVE_NONE, false).getNextFrequencyDate();
				rvwDate = DateUtility.getDBDate(DateUtility.formatDate(rvwDate, PennantConstants.DBDateFormat));
				
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
				Date cpzDate = FrequencyUtil.getNextDate(finMain.getGrcCpzFrq(), 1, 
						finMain.getFinStartDate(), HolidayHandlerTypes.MOVE_NONE, false).getNextFrequencyDate();
				cpzDate = DateUtility.getDBDate(DateUtility.formatDate(cpzDate, PennantConstants.DBDateFormat));

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
	private void repayDefaulting(String vldGroup, FinScheduleData finScheduleData) {
		FinanceMain finMain = finScheduleData.getFinanceMain();
		FinanceType financeType = finScheduleData.getFinanceType();
		boolean isValidRpyFrq = false;
		boolean isValidOtherFrq = false;

		// set default values from financeType
		finMain.setAllowRepayRvw(financeType.isFinIsRvwAlw());
		finMain.setAllowRepayCpz(financeType.isFinIsIntCpz());
		
		// return in case of CalGrcEndDate is null for allow grace finance
		if(finMain.isAllowGrcPeriod() && finMain.getCalGrcEndDate() == null) {
			return;
		}
		
		//Repay Rate Type
		if (StringUtils.isBlank(finMain.getRepayRateBasis())) {
			finMain.setRepayRateBasis(financeType.getFinRateType());
		}

		//Repay Rate
		if (StringUtils.isBlank(finMain.getRepayBaseRate()) && finMain.getRepayProfitRate().compareTo(BigDecimal.ZERO) == 0
				&& StringUtils.isBlank(finMain.getRepaySpecialRate()) && finMain.getRepayMargin().compareTo(BigDecimal.ZERO) == 0) {
			if (StringUtils.isNotBlank(financeType.getFinBaseRate())) {
				finMain.setRepayBaseRate(financeType.getFinBaseRate());
				finMain.setRepaySpecialRate(financeType.getFinSplRate());
				finMain.setRepayMargin(financeType.getFinMargin());
			}
		}

		//Schedule Method
		if (StringUtils.isBlank(finMain.getScheduleMethod())) {
			finMain.setScheduleMethod(financeType.getFinSchdMthd());
		}

		//Default Terms
		if (finMain.getNumberOfTerms() == 0 && finMain.getMaturityDate() == null) {
			finMain.setNumberOfTerms(financeType.getFinDftTerms());
			finMain.setCalTerms(finMain.getNumberOfTerms());
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

		ErrorDetails tempError = FrequencyUtil.validateFrequency(finMain.getRepayFrq());
		if (tempError == null) {
			isValidRpyFrq = true;
		}

		//Set Default Next Repayment Date
		if (isValidRpyFrq && finMain.getNextRepayDate() == null) {
		//	Date nextDate = getNextDftDate(finMain.getRepayFrq(), finMain.getCalGrcEndDate(),financeType.getFddLockPeriod());
			Date nextDate = FrequencyUtil.getNextDate(finMain.getRepayFrq(), 1, finMain.getCalGrcEndDate(),
					HolidayHandlerTypes.MOVE_NONE, false, financeType.getFddLockPeriod()).getNextFrequencyDate();
			nextDate = DateUtility.getDBDate(DateUtility.formatDate(nextDate, PennantConstants.DBDateFormat));
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
			Date nextRpyPftDate = FrequencyUtil.getNextDate(finMain.getRepayPftFrq(), 1, finMain.getCalGrcEndDate(),
					HolidayHandlerTypes.MOVE_NONE, false, financeType.getFddLockPeriod()).getNextFrequencyDate();
			nextRpyPftDate = DateUtility.getDBDate(DateUtility.formatDate(nextRpyPftDate, PennantConstants.DBDateFormat));

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
				String frq = new StringBuilder(5).append(financeType.getFinRvwFrq().substring(0, 3))
						.append(finMain.getRepayFrq().substring(3, 5)).toString();
				finMain.setRepayRvwFrq(frq);
			}

			tempError = FrequencyUtil.validateFrequency(finMain.getRepayRvwFrq());
			if (tempError == null) {
				isValidOtherFrq = true;
			} else {
				isValidOtherFrq = false;
			}

			//Next Profit Review Date
			if (isValidOtherFrq && finMain.getNextRepayRvwDate() == null) {
				Date nextRpyRvwDate = FrequencyUtil.getNextDate(finMain.getRepayRvwFrq(), 1, 
						finMain.getCalGrcEndDate(), HolidayHandlerTypes.MOVE_NONE, false, financeType.getFddLockPeriod()).getNextFrequencyDate();
				nextRpyRvwDate = DateUtility.getDBDate(DateUtility.formatDate(nextRpyRvwDate, PennantConstants.DBDateFormat));

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
				Date cpzDate = FrequencyUtil.getNextDate(finMain.getRepayCpzFrq(), 1,
						finMain.getCalGrcEndDate(), HolidayHandlerTypes.MOVE_NONE, false, financeType.getFddLockPeriod()).getNextFrequencyDate();
				cpzDate = DateUtility.getDBDate(DateUtility.formatDate(cpzDate, PennantConstants.DBDateFormat));
				
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
		} else {
			finMain.setBpiTreatment(FinanceConstants.BPI_NO);
		}
	}

	private void overdueDefaulting(String vldGroup, FinScheduleData finScheduleData) {
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
			finScheduleData.setFinODPenaltyRate(finODPenaltyRate);
		} else if (finScheduleData.getFinODPenaltyRate() != null && !financeType.isApplyODPenalty()) {
			FinODPenaltyRate finODPenaltyRate = finScheduleData.getFinODPenaltyRate();
			if (StringUtils.equals(finODPenaltyRate.getODChargeType(), FinanceConstants.PENALTYTYPE_PERC_ONETIME)
					|| StringUtils.equals(finODPenaltyRate.getODChargeType(),FinanceConstants.PENALTYTYPE_PERC_ON_DUEDAYS)
					|| StringUtils.equals(finODPenaltyRate.getODChargeType(),FinanceConstants.PENALTYTYPE_PERC_ON_PD_MTH)) {
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

		if(StringUtils.isNotBlank(frq)) {
			// Making into List
			List<String> alwdDays = Arrays.asList(alwdFrqDays.split(","));
			
			//Sorting available list
			Collections.sort(alwdDays);
			return frq.substring(0, 3).concat(StringUtils.leftPad(alwdDays.get(0), 2, "0"));
		}
		
		return frq;
	}

	/*
	 * _______________________________________________________________________________________________________________
	 * GET Frequency based on Date
	 * _______________________________________________________________________________________________________________
	 */
	private String getDftFrequency(String frq, Date frqDate) {

		if(StringUtils.isNotBlank(frq)) {
			String frqDay = String.valueOf(DateUtility.getDay(frqDate));
			frqDay = StringUtils.leftPad(frqDay, 2, "0");
			frq = new StringBuilder(5).append(frq.substring(0, 3)).append(frqDay).toString();
		}

		return frq;
	}

	/*
	 * _______________________________________________________________________________________________________________
	 * Validate Interest Days Basis
	 * _______________________________________________________________________________________________________________
	 */
	private boolean isValidateIDB(String IDB) {
		if (!StringUtils.equals(IDB, CalculationConstants.IDB_30E360)
				&& !StringUtils.equals(IDB, CalculationConstants.IDB_30E360I)
				&& !StringUtils.equals(IDB, CalculationConstants.IDB_30EP360)
				&& !StringUtils.equals(IDB, CalculationConstants.IDB_30U360)
				&& !StringUtils.equals(IDB, CalculationConstants.IDB_ACT_360)
				&& !StringUtils.equals(IDB, CalculationConstants.IDB_ACT_365FIXED)
				&& !StringUtils.equals(IDB, CalculationConstants.IDB_ACT_365LEAP)
				&& !StringUtils.equals(IDB, CalculationConstants.IDB_ACT_365LEAPS)
				&& !StringUtils.equals(IDB, CalculationConstants.IDB_ACT_ISDA)) {

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
				List<Calendar> scheduleDateList = FrequencyUtil.getNextDate(finMain.getGrcPftFrq(), finMain.getGraceTerms(),
						finMain.getFinStartDate(), HolidayHandlerTypes.MOVE_NONE, false, financeType.getFddLockPeriod()).getScheduleList();

				Date geDate = null;
				if (scheduleDateList != null) {
					Calendar calendar = scheduleDateList.get(scheduleDateList.size() - 1);
					geDate = DateUtility.getDBDate(DateUtility.formatDate(calendar.getTime(), PennantConstants.DBDateFormat));
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
			finMain.setCalTerms(finMain.getNumberOfTerms());
			if (StringUtils.isNotBlank(finMain.getRepayFrq()) && finMain.getNextRepayDate() != null) {
				List<Calendar> scheduleDateList = FrequencyUtil.getNextDate(finMain.getRepayFrq(),finMain.getNumberOfTerms(),
						finMain.getNextRepayDate(), HolidayHandlerTypes.MOVE_NONE, true, financeType.getFddLockPeriod()).getScheduleList();

				if (scheduleDateList != null) {
					Calendar calendar = scheduleDateList.get(scheduleDateList.size() - 1);
					Date matDate = DateUtility.getDBDate(DateUtility.formatDate(calendar.getTime(), PennantConstants.DBDateFormat));
					finMain.setCalMaturity(matDate);
				}
			}
			return;
		}

		//Default Calculated Terms based on Maturity Date
		finMain.setCalMaturity(finMain.getMaturityDate());
		if (StringUtils.isNotBlank(finMain.getRepayFrq()) && finMain.getNextRepayDate() != null) {
			int terms = FrequencyUtil.getTerms(finMain.getRepayFrq(), finMain.getNextRepayDate(),
					finMain.getMaturityDate(), true, true).getTerms();
			finMain.setCalTerms(terms);
		}

		return;

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

	public void setBranchDAO(BranchDAO branchDAO) {
		this.branchDAO = branchDAO;
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
}
