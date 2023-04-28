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
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.pff.mandate.MandateUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.overdue.constants.ChargeType;

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

	public FinanceDetail defaultFinance(String vldGroup, FinanceDetail fd) {
		List<ErrorDetail> errors = new ArrayList<>();

		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();
		fd.setCustomerDetails(new CustomerDetails());
		fd.getCustomerDetails().setCustomer(null);

		String coreBankId = fm.getCoreBankId();
		String custCIF = fm.getCustCIF();

		// Get the logged in users one time and set to avoid multiple calls
		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		fm.setUserDetails(userDetails);

		if (!PennantConstants.VLD_CRT_SCHD.equals(vldGroup) || StringUtils.isNotEmpty(fm.getCustCIF())) {
			if (StringUtils.isNotBlank(coreBankId)) {
				if (!customerDAO.getCustomerByCoreBankId(coreBankId)) {
					String[] valueParm = new String[2];
					valueParm[0] = "CoreBankId";
					valueParm[1] = coreBankId;
					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90701", valueParm)));
					schdData.setErrorDetails(errors);

					return fd;
				}
			}

			Customer customer = customerDAO.getCustomerByCIF(custCIF, "");

			if (customer == null) {
				String[] valueParm = new String[1];
				valueParm[0] = fm.getLovDescCustCIF();
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90101", valueParm)));
				schdData.setErrorDetails(errors);
				return fd;
			}

			fm.setLovDescCustCIF(custCIF);
			fm.setLovDescCustCoreBank(customer.getCustCoreBank());
			fm.setCustID(customer.getCustID());
			fd.getCustomerDetails().setCustomer(customer);
		}

		// Date formats
		setDefaultDateFormats(fm);

		if (fm.getFinStartDate() == null) {
			fm.setFinStartDate(SysParamUtil.getAppDate());
		}

		// Validate Fields data (Excluding Base & Special rates Validations)
		validateMasterData(vldGroup, fd);

		if (!schdData.getErrorDetails().isEmpty()) {
			return fd;
		}

		// Basic Details Defaulting
		basicDefaulting(vldGroup, fd);

		// Grace Details Defaulting
		graceDefaulting(vldGroup, fd);

		// Repayments Details Defaulting
		repayDefaulting(vldGroup, fd);

		// Overdue penalty rates defaulting
		if (PennantConstants.VLD_CRT_LOAN.equals(vldGroup)) {
			overdueDefaulting(vldGroup, fd);
		}

		return fd;
	}

	/*
	 * ################################################################################################################
	 * MAIN METHODS
	 * ################################################################################################################
	 */

	private void setDefaultDateFormats(FinanceMain fm) {
		fm.setFinStartDate(DateUtil.getDatePart(fm.getFinStartDate()));
		fm.setGrcPeriodEndDate(DateUtil.getDatePart(fm.getGrcPeriodEndDate()));
		fm.setNextGrcPftDate(DateUtil.getDatePart(fm.getNextGrcPftDate()));
		fm.setNextGrcPftRvwDate(DateUtil.getDatePart(fm.getNextGrcPftRvwDate()));
		fm.setNextGrcCpzDate(DateUtil.getDatePart(fm.getNextGrcCpzDate()));
		fm.setNextRepayDate(DateUtil.getDatePart(fm.getNextRepayDate()));
		fm.setNextRepayPftDate(DateUtil.getDatePart(fm.getNextRepayPftDate()));
		fm.setNextRepayRvwDate(DateUtil.getDatePart(fm.getNextRepayRvwDate()));
		fm.setNextRepayCpzDate(DateUtil.getDatePart(fm.getNextRepayCpzDate()));
		fm.setFirstDroplineDate(DateUtil.getDatePart(fm.getFirstDroplineDate()));
		fm.setMaturityDate(DateUtil.getDatePart(fm.getMaturityDate()));
	}

	/*
	 * ================================================================================================================
	 * VALIDATE STATIC DATA
	 * ================================================================================================================
	 */
	private void validateMasterData(String vldGroup, FinanceDetail fd) {
		List<ErrorDetail> errorDetails = new ArrayList<>();

		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		FinanceType financeType = null;

		String finType = fm.getFinType();
		String promotionCode = fm.getPromotionCode();
		String finCcy = fm.getFinCcy();
		String finBranch = fm.getFinBranch();
		String profitDaysBasis = fm.getProfitDaysBasis();
		String repayMethod = fm.getFinRepayMethod();
		String stepType = fm.getStepType();
		String grcProfitDaysBasis = fm.getGrcProfitDaysBasis();
		String grcPftFrq = fm.getGrcPftFrq();
		String grcRateBasis = fm.getGrcRateBasis();
		String grcPftRvwFrq = fm.getGrcPftRvwFrq();
		String grcCpzFrq = fm.getGrcCpzFrq();
		String grcSchdMthd = fm.getGrcSchdMthd();
		String repayRateBasis = fm.getRepayRateBasis();
		String scheduleMethod = fm.getScheduleMethod();
		String repayFrq = fm.getRepayFrq();
		String repayPftFrq = fm.getRepayPftFrq();
		String repayRvwFrq = fm.getRepayRvwFrq();
		String repayCpzFrq = fm.getRepayCpzFrq();
		String bpiTreatment = fm.getBpiTreatment();

		if (!StringUtils.isBlank(finType)) {
			financeType = financeTypeDAO.getOrgFinanceTypeByID(finType, "_ORGView");
			if (financeType == null) {
				String[] valueParm = new String[2];
				valueParm[0] = "Loan Type";
				valueParm[1] = finType;
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90701", valueParm)));
				schdData.setErrorDetails(errorDetails);

				return;
			}
		}

		schdData.setFinanceType(financeType);

		if (StringUtils.isBlank(finCcy)) {
			finCcy = SysParamUtil.getAppCurrency();
			fm.setFinCcy(finCcy);
		}

		if (StringUtils.isEmpty(fm.getAdvStage()) && StringUtils.equals(fm.getAdvType(), financeType.getAdvType())
				&& fm.getAdvTerms() > 0) {
			fm.setAdvStage(financeType.getAdvStage());
		}

		if (StringUtils.isNotBlank(promotionCode)) {
			errorDetails.addAll(validatePromotionData(fd));
			schdData.setErrorDetails(errorDetails);

			if (!errorDetails.isEmpty()) {
				return;
			}
		}

		String calcOfSteps = financeType.getCalcOfSteps();

		if (PennantConstants.VLD_CRT_LOAN.equals(vldGroup) && StringUtils.isBlank(finBranch)) {
			CustomerDetails cd = fd.getCustomerDetails();
			Customer customer = cd.getCustomer();
			finBranch = customer.getCustDftBranch();
			fm.setFinBranch(finBranch);
		}

		if (StringUtils.isNotBlank(finBranch)) {
			Branch branch = branchService.getBranch(finBranch);
			if (branch == null) {
				String[] valueParm = new String[1];
				valueParm[0] = finBranch;
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90129", valueParm)));
				schdData.setErrorDetails(errorDetails);

				return;
			}

			fm.setSwiftBranchCode(branch.getBranchSwiftBrnCde());
		}

		if (StringUtils.isNotBlank(profitDaysBasis) && !isValidateIDB(profitDaysBasis)) {
			String[] valueParm = new String[1];
			valueParm[0] = profitDaysBasis;
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90209", valueParm)));
			schdData.setErrorDetails(errorDetails);

			return;
		}

		if (StringUtils.isBlank(repayMethod)) {
			repayMethod = financeType.getFinRepayMethod();
			fm.setFinRepayMethod(repayMethod);
		}

		if (StringUtils.isNotBlank(repayMethod)) {
			List<ValueLabel> mandateType = MandateUtil.getRepayMethods();
			boolean mandateTypeSts = false;

			for (ValueLabel value : mandateType) {
				if (repayMethod.equals(value.getValue())) {
					mandateTypeSts = true;
					break;
				}
			}

			if (!mandateTypeSts) {
				String[] valueParm = new String[1];
				valueParm[0] = repayMethod;
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90307", valueParm)));
				schdData.setErrorDetails(errorDetails);

				return;
			}
		}

		if (PennantConstants.STEPPING_CALC_PERC.equals(calcOfSteps) && StringUtils.isNotBlank(stepType)) {
			if (!FinanceConstants.STEPTYPE_EMI.equals(stepType) && !FinanceConstants.STEPTYPE_PRIBAL.equals(stepType)) {
				String[] valueParm = new String[2];
				valueParm[0] = new StringBuilder(10).append(FinanceConstants.STEPTYPE_EMI).append(" & ")
						.append(FinanceConstants.STEPTYPE_PRIBAL).toString();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90148", valueParm)));
				schdData.setErrorDetails(errorDetails);

				return;
			}
		}

		if (StringUtils.isNotBlank(grcProfitDaysBasis) && !isValidateIDB(grcProfitDaysBasis)) {
			String[] valueParm = new String[1];
			valueParm[0] = grcProfitDaysBasis;
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90209", valueParm)));
			schdData.setErrorDetails(errorDetails);

			return;
		}

		if (StringUtils.isNotBlank(grcPftFrq)) {
			ErrorDetail tempError = FrequencyUtil.validateFrequency(grcPftFrq);
			if (tempError != null) {
				String[] valueParm = new String[2];
				valueParm[0] = "Grace";
				valueParm[1] = grcPftFrq;
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90156", valueParm)));
				schdData.setErrorDetails(errorDetails);

				return;
			}
		}

		if (StringUtils.isNotBlank(grcRateBasis) && !PennantConstants.List_Select.equals(grcRateBasis)
				&& !CalculationConstants.RATE_BASIS_F.equals(grcRateBasis)
				&& !CalculationConstants.RATE_BASIS_R.equals(grcRateBasis)) {
			String[] valueParm = new String[1];
			valueParm[0] = grcRateBasis;
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90211", valueParm)));
			schdData.setErrorDetails(errorDetails);

			return;
		}

		if (StringUtils.isNotBlank(grcPftRvwFrq)) {
			ErrorDetail tempError = FrequencyUtil.validateFrequency(grcPftRvwFrq);
			if (tempError != null) {
				String[] valueParm = new String[2];
				valueParm[0] = "Grace";
				valueParm[1] = grcPftRvwFrq;
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90157", valueParm)));
				schdData.setErrorDetails(errorDetails);

				return;
			}
		}

		if (StringUtils.isNotBlank(grcCpzFrq)) {
			ErrorDetail tempError = FrequencyUtil.validateFrequency(grcCpzFrq);
			if (tempError != null) {
				String[] valueParm = new String[2];
				valueParm[0] = "Grace";
				valueParm[1] = grcCpzFrq;
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90158", valueParm)));
				schdData.setErrorDetails(errorDetails);

				return;
			}
		}

		if (StringUtils.isNotBlank(grcSchdMthd)) {
			if (!CalculationConstants.SCHMTHD_NOPAY.equals(grcSchdMthd)
					&& !CalculationConstants.SCHMTHD_PFT.equals(grcSchdMthd)
					&& !CalculationConstants.SCHMTHD_PFTCPZ.equals(grcSchdMthd)
					&& !CalculationConstants.SCHMTHD_GRCENDPAY.equals(grcSchdMthd)
					&& !CalculationConstants.SCHMTHD_PFTCAP.equals(grcSchdMthd)) {
				String[] valueParm = new String[2];
				valueParm[0] = "Grace";
				valueParm[1] = grcSchdMthd;
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90210", valueParm)));
				schdData.setErrorDetails(errorDetails);

				return;
			}
		}

		if (StringUtils.isNotBlank(repayRateBasis)) {
			if (!CalculationConstants.RATE_BASIS_C.equals(repayRateBasis)
					&& !CalculationConstants.RATE_BASIS_F.equals(repayRateBasis)
					&& !CalculationConstants.RATE_BASIS_R.equals(repayRateBasis)) {
				String[] valueParm = new String[1];
				valueParm[0] = repayRateBasis;
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90211", null)));
				schdData.setErrorDetails(errorDetails);

				return;
			}
		}

		if (StringUtils.isNotBlank(scheduleMethod)) {
			if (!CalculationConstants.SCHMTHD_NOPAY.equals(scheduleMethod)
					&& !CalculationConstants.SCHMTHD_EQUAL.equals(scheduleMethod)
					&& !CalculationConstants.SCHMTHD_PFT.equals(scheduleMethod)
					&& !CalculationConstants.SCHMTHD_PFTCPZ.equals(scheduleMethod)
					&& !CalculationConstants.SCHMTHD_PRI.equals(scheduleMethod)
					&& !CalculationConstants.SCHMTHD_PRI_PFT.equals(scheduleMethod)
					&& !CalculationConstants.SCHMTHD_POS_INT.equals(scheduleMethod)) {
				String[] valueParm = new String[2];
				valueParm[0] = "Repayment";
				valueParm[1] = scheduleMethod;
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90210", valueParm)));
				schdData.setErrorDetails(errorDetails);

				return;
			}
		}

		if (StringUtils.isNotBlank(repayFrq)) {
			ErrorDetail tempError = FrequencyUtil.validateFrequency(repayFrq);
			if (tempError != null) {
				String[] valueParm = new String[2];
				valueParm[0] = "Repay";
				valueParm[1] = repayFrq;
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90159", valueParm)));
				schdData.setErrorDetails(errorDetails);

				return;
			}
		}

		if (StringUtils.isNotBlank(repayPftFrq)) {
			ErrorDetail tempError = FrequencyUtil.validateFrequency(repayPftFrq);
			if (tempError != null) {
				String[] valueParm = new String[2];
				valueParm[0] = "Repay";
				valueParm[1] = repayPftFrq;
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90156", valueParm)));
				schdData.setErrorDetails(errorDetails);

				return;
			}
		}

		if (StringUtils.isNotBlank(repayRvwFrq)) {
			ErrorDetail tempError = FrequencyUtil.validateFrequency(repayRvwFrq);
			if (tempError != null) {
				String[] valueParm = new String[2];
				valueParm[0] = "Repay";
				valueParm[1] = repayRvwFrq;
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90157", valueParm)));
				schdData.setErrorDetails(errorDetails);

				return;
			}
		}

		if (StringUtils.isNotBlank(repayCpzFrq)) {
			ErrorDetail tempError = FrequencyUtil.validateFrequency(repayCpzFrq);
			if (tempError != null) {
				String[] valueParm = new String[2];
				valueParm[0] = "Repay";
				valueParm[1] = repayCpzFrq;
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90158", valueParm)));
				schdData.setErrorDetails(errorDetails);

				return;
			}
		}

		if (StringUtils.isNotBlank(bpiTreatment)) {
			if (!FinanceConstants.BPI_NO.equals(bpiTreatment) && !FinanceConstants.BPI_DISBURSMENT.equals(bpiTreatment)
					&& !FinanceConstants.BPI_SCHEDULE.equals(bpiTreatment)
					&& !FinanceConstants.BPI_CAPITALIZE.equals(bpiTreatment)
					&& !FinanceConstants.BPI_SCHD_FIRSTEMI.equals(bpiTreatment)) {

				String[] valueParm = new String[2];
				valueParm[0] = bpiTreatment;
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90185", valueParm)));
				schdData.setErrorDetails(errorDetails);

				return;
			}
		}

		if (StringUtils.isNotEmpty(fm.getAdvType())) {
			int minTerms = financeType.getAdvMinTerms();
			int maxTerms = financeType.getAdvMaxTerms();
			int advTerms = fm.getAdvTerms();

			if (advTerms < minTerms || advTerms > maxTerms) {
				String[] valueParm = new String[3];
				valueParm[0] = "AdvTerms: ".concat(String.valueOf(advTerms));
				valueParm[1] = "MinTerms: ".concat(String.valueOf(minTerms));
				valueParm[2] = "MaxTerms: ".concat(String.valueOf(maxTerms));
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("ADVEMI01", valueParm)));
				schdData.setErrorDetails(errorDetails);

				return;
			}

			if (advTerms > fm.getNumberOfTerms()) {
				String[] valueParm = new String[2];
				valueParm[0] = "AdvTerms: ".concat(String.valueOf(advTerms));
				valueParm[1] = "NumberOfTerms: ".concat(String.valueOf(fm.getNumberOfTerms()));
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("30568", valueParm)));
				schdData.setErrorDetails(errorDetails);

				return;
			}

		}

	}

	private List<ErrorDetail> validatePromotionData(FinanceDetail fd) {
		List<ErrorDetail> errors = new ArrayList<>();

		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		FinanceType financeType = schdData.getFinanceType();

		String finType = fm.getFinType();
		String promotionCode = fm.getPromotionCode();
		Date finStartDate = fm.getFinStartDate();
		String finCcy = fm.getFinCcy();
		int ccyEditField = CurrencyUtil.getFormat(finCcy);

		Promotion promotion = promotionService.getActiveSchemeForTxn(promotionCode, FinanceConstants.MODULEID_PROMOTION,
				finStartDate, true);
		schdData.setPromotion(promotion);
		fd.setPromotion(promotion);

		if (promotion == null) {
			String[] valueParm = new String[2];
			valueParm[0] = "Promotion";
			valueParm[1] = promotionCode;
			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90701", valueParm)));

			return errors;
		}

		if (StringUtils.isBlank(finType)) {
			finType = promotion.getFinType();
			fm.setFinType(finType);
			financeType = financeTypeDAO.getOrgFinanceTypeByID(finType, "_ORGView");
		}

		if (!StringUtils.equals(finType, promotion.getFinType())) {
			String[] valueParm = new String[1];
			valueParm[0] = finType;
			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90202", valueParm)));

			return errors;
		}

		BigDecimal proMinAmount = promotion.getFinMinAmount();
		BigDecimal finAmount = fm.getFinAmount();
		BigDecimal proMaxAmount = promotion.getFinMaxAmount();

		if (proMinAmount.compareTo(BigDecimal.ZERO) > 0 && finAmount.compareTo(proMinAmount) < 0) {
			String[] valueParm = new String[1];
			valueParm[0] = PennantApplicationUtil.amountFormate(proMinAmount, ccyEditField);
			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90132", valueParm)));

			return errors;
		}

		if (proMaxAmount.compareTo(BigDecimal.ZERO) > 0 && finAmount.compareTo(proMaxAmount) > 0) {
			String[] valueParm = new String[1];
			valueParm[0] = PennantApplicationUtil.amountFormate(proMaxAmount, ccyEditField);
			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90133", valueParm)));

			return errors;
		}

		String productCategory = financeType.getProductCategory();
		if (FinanceConstants.PRODUCT_CD.equals(productCategory) && StringUtils.isBlank(promotionCode)) {
			String[] valueParm = new String[1];
			valueParm[0] = "PromotionCode";
			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));

			return errors;
		}

		financeType.setPromotionType(true);
		financeType.setPromotionCode(promotion.getPromotionCode());
		fm.setPromotionSeqId(promotion.getReferenceID());

		return errors;
	}

	/*
	 * ================================================================================================================
	 * DEFAULT FINANCE BASIC DETAILS
	 * ================================================================================================================
	 */

	private void basicDefaulting(String vldGroup, FinanceDetail fd) {
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();
		FinanceType finType = schdData.getFinanceType();

		Date appDate = SysParamUtil.getAppDate();

		if (fm.getFinStartDate() == null) {
			fm.setFinStartDate(appDate);
		}

		if (fm.getFinAmount() == null) {
			fm.setFinAmount(BigDecimal.ZERO);
		}

		fm.setProductCategory(finType.getProductCategory());
		fm.setFinCategory(finType.getFinCategory());

		if (StringUtils.isBlank(fm.getFinCcy())) {
			fm.setFinCcy(finType.getFinCcy());
		}

		if (StringUtils.isBlank(fm.getProfitDaysBasis())) {
			fm.setProfitDaysBasis(finType.getFinDaysCalType());
		}

		fm.setRvwRateApplFor(finType.getFinRvwRateApplFor());
		fm.setRateChgAnyDay(finType.isRateChgAnyDay());
		fm.setPastduePftCalMthd(finType.getPastduePftCalMthd());
		fm.setPastduePftMargin(finType.getPastduePftMargin());
		fm.setSchCalOnRvw(finType.getFinSchCalCodeOnRvw());
		fm.setFinIsAlwMD(finType.isFinIsAlwMD());
		fm.setAlwMultiDisb(finType.isFinIsAlwMD());

		if (StringUtils.isBlank(fm.getFinRepayMethod())) {
			fm.setFinRepayMethod(finType.getFinRepayMethod());
		}

		if (finType.isStepFinance() && finType.isSteppingMandatory()) {
			fm.setStepFinance(true);
		}

		if (fm.isStepFinance()) {
			stepDefaulting(vldGroup, schdData);
		}

		fm.setFinStsReason(FinanceConstants.FINSTSRSN_SYSTEM);
		fm.setInitiateUser(fm.getUserDetails().getUserId());
		fm.setInitiateDate(appDate);
		fm.setCalRoundingMode(finType.getRoundingMode());
		fm.setRoundingTarget(finType.getRoundingTarget());
		fm.setTDSApplicable(finType.isTdsApplicable());

		if (finType.isGrcAdvIntersetReq()) {
			fm.setGrcAdvType(finType.getGrcAdvType());
			fm.setGrcAdvTerms(finType.getGrcAdvDefaultTerms());
		} else {
			fm.setGrcAdvType(PennantConstants.List_Select);
			fm.setGrcAdvTerms(0);
		}

		if (finType.isCashCollateralReq()) {
			if (finType.isAdvIntersetReq()) {
				fm.setAdvType(finType.getAdvType());
				fm.setAdvTerms(finType.getAdvDefaultTerms());
				fm.setAdvStage(finType.getAdvStage());
			} else {
				fm.setAdvType(PennantConstants.List_Select);
				fm.setAdvTerms(0);
				fm.setAdvStage(PennantConstants.List_Select);
			}
		}

		if (StringUtils.isBlank(fm.getTdsType()) && !PennantConstants.TDS_USER_SELECTION.equals(finType.getTdsType())) {
			fm.setTdsType(finType.getTdsType());
		}

		fm.setInstBasedSchd(finType.isInstBasedSchd());
	}

	/*
	 * ================================================================================================================
	 * DEFAULT FINANCE GRACE DETAILS
	 * ================================================================================================================
	 */
	private void graceDefaulting(String vldGroup, FinanceDetail fd) {
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();
		FinanceType finType = schdData.getFinanceType();

		String finGrcDftIntFrq = finType.getFinGrcDftIntFrq();

		boolean isValidPftFrq = false;
		boolean isValidOtherFrq = false;

		Date finStartDate = fm.getFinStartDate();

		if (!fm.isAllowGrcPeriod()) {
			fm.setGraceTerms(0);
			fm.setGrcPeriodEndDate(finStartDate);
			fm.setGrcRateBasis(null);
			fm.setGrcPftRate(BigDecimal.ZERO);
			fm.setGraceBaseRate(null);
			fm.setGraceSpecialRate(null);
			fm.setGrcMargin(BigDecimal.ZERO);
			fm.setGrcProfitDaysBasis(null);
			fm.setGrcPftFrq(null);
			fm.setNextGrcPftDate(null);
			fm.setAllowGrcPftRvw(false);
			fm.setGrcPftRvwFrq(null);
			fm.setNextGrcPftRvwDate(null);
			fm.setAllowGrcCpz(false);
			fm.setGrcCpzFrq(null);
			fm.setNextGrcCpzDate(null);
			fm.setAllowGrcRepay(false);
			fm.setGrcSchdMthd(null);
			fm.setGrcMinRate(BigDecimal.ZERO);
			fm.setGrcMaxRate(BigDecimal.ZERO);
			fm.setCalGrcEndDate(finStartDate);
			fm.setGrcRateBasis(PennantConstants.List_Select);
			return;
		}

		// set default values from financeType
		fm.setAllowGrcPftRvw(finType.isFinGrcIsRvwAlw());
		fm.setAllowGrcCpz(finType.isFinGrcIsIntCpz());
		fm.setEndGrcPeriodAftrFullDisb(finType.isGrcPeriodAftrFullDisb());

		// Grace Rate Type
		if (StringUtils.isBlank(fm.getGrcRateBasis())) {
			fm.setGrcRateBasis(finType.getFinGrcRateType());
		}

		// Grace schedule method
		if (StringUtils.isBlank(fm.getGrcSchdMthd())) {
			fm.setGrcSchdMthd("");
		}

		// Grace Rate
		if (StringUtils.isBlank(fm.getGraceBaseRate()) && fm.getGrcPftRate().compareTo(BigDecimal.ZERO) == 0
				&& StringUtils.isBlank(fm.getGraceSpecialRate()) && fm.getGrcMargin().compareTo(BigDecimal.ZERO) == 0) {
			if (StringUtils.isNotBlank(finType.getFinGrcBaseRate())) {
				fm.setGraceBaseRate(finType.getFinGrcBaseRate());
				fm.setGraceSpecialRate(finType.getFinGrcSplRate());
				fm.setGrcMargin(finType.getFinGrcMargin());
			}
		}

		if (StringUtils.isBlank(fm.getGrcProfitDaysBasis())) {
			fm.setGrcProfitDaysBasis(finType.getFinDaysCalType());
		}

		String grcPftFrq = fm.getGrcPftFrq();

		if (StringUtils.isBlank(grcPftFrq)) {
			if (fm.getNextGrcPftDate() != null) {
				fm.setGrcPftFrq(getDftFrequency(finGrcDftIntFrq, fm.getNextGrcPftDate()));

			} else if (fm.getGrcPeriodEndDate() != null) {
				fm.setGrcPftFrq(getDftFrequency(finGrcDftIntFrq, fm.getGrcPeriodEndDate()));

			} else if (StringUtils.isNotEmpty(finType.getFrequencyDays())) {
				fm.setGrcPftFrq(getDftFrequency(finGrcDftIntFrq, finType.getFrequencyDays()));

			} else {
				fm.setGrcPftFrq(getDftFrequency(finGrcDftIntFrq, finStartDate));
			}
		}

		ErrorDetail error = FrequencyUtil.validateFrequency(grcPftFrq);

		if (error == null) {
			isValidPftFrq = true;
		}

		if (isValidPftFrq && fm.getNextGrcPftDate() == null) {
			Date nextDate = FrequencyUtil.getNextDate(grcPftFrq, 1, finStartDate, HolidayHandlerTypes.MOVE_NONE, false,
					finType.getFddLockPeriod()).getNextFrequencyDate();

			fm.setNextGrcPftDate(nextDate);
		}

		if (isValidPftFrq) {
			graceEndDefaulting(schdData);
		}

		if (fm.isAllowGrcPftRvw()) {
			if (StringUtils.isBlank(fm.getGrcPftRvwFrq()) && isValidPftFrq) {
				String frq = new StringBuilder(5).append(finType.getFinGrcRvwFrq().substring(0, 3))
						.append(grcPftFrq.substring(3, 5)).toString();
				fm.setGrcPftRvwFrq(frq);
			}

			error = FrequencyUtil.validateFrequency(fm.getGrcPftRvwFrq());

			if (error == null) {
				isValidOtherFrq = true;
			}

			if (isValidOtherFrq && fm.getNextGrcPftRvwDate() == null) {
				Date rvwDate = FrequencyUtil
						.getNextDate(fm.getGrcPftRvwFrq(), 1, finStartDate, HolidayHandlerTypes.MOVE_NONE, false)
						.getNextFrequencyDate();
				rvwDate = DateUtil.getDatePart(rvwDate);

				if (fm.getCalGrcEndDate() != null && rvwDate != null) {
					if (fm.getCalGrcEndDate().compareTo(rvwDate) < 0) {
						rvwDate = fm.getCalGrcEndDate();
					}
				}

				fm.setNextGrcPftRvwDate(rvwDate);
			}
		}

		if (fm.isAllowGrcCpz()) {
			if (StringUtils.isBlank(fm.getGrcCpzFrq()) && isValidPftFrq) {
				String frq = new StringBuilder(5).append(finType.getFinGrcCpzFrq().substring(0, 3))
						.append(grcPftFrq.substring(3, 5)).toString();
				fm.setGrcCpzFrq(frq);
			}

			error = FrequencyUtil.validateFrequency(fm.getGrcCpzFrq());

			if (error == null) {
				isValidOtherFrq = true;
			} else {
				isValidOtherFrq = false;
			}

			if (isValidOtherFrq && fm.getNextGrcCpzDate() == null) {
				Date cpzDate = FrequencyUtil
						.getNextDate(fm.getGrcCpzFrq(), 1, finStartDate, HolidayHandlerTypes.MOVE_NONE, false)
						.getNextFrequencyDate();
				cpzDate = DateUtil.getDatePart(cpzDate);

				if (fm.getCalGrcEndDate() != null && cpzDate != null && fm.getCalGrcEndDate().compareTo(cpzDate) < 0) {
					cpzDate = fm.getCalGrcEndDate();
				}

				fm.setNextGrcCpzDate(cpzDate);
			}
		}

		if (fm.isAllowGrcRepay() && StringUtils.isBlank(fm.getGrcSchdMthd()) && finType.isFinIsAlwGrcRepay()) {
			fm.setGrcSchdMthd(finType.getFinGrcSchdMthd());
		}
	}

	/*
	 * ================================================================================================================
	 * DEFAULT FINANCE REPAYMENT DETAILS
	 * ================================================================================================================
	 */
	private void repayDefaulting(String vldGroup, FinanceDetail fd) {
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();
		FinanceType finType = schdData.getFinanceType();
		Promotion promotion = schdData.getPromotion();

		boolean isValidRpyFrq = false;
		boolean isValidOtherFrq = false;

		fm.setAllowRepayRvw(finType.isFinIsRvwAlw());
		fm.setAllowRepayCpz(finType.isFinIsIntCpz());

		if (fm.isAllowGrcPeriod() && fm.getCalGrcEndDate() == null) {
			return;
		}

		if (StringUtils.isBlank(fm.getRepayRateBasis())) {
			fm.setRepayRateBasis(finType.getFinRateType());
		}

		if (StringUtils.isBlank(fm.getRepayBaseRate()) && fm.getRepayProfitRate().compareTo(BigDecimal.ZERO) == 0) {
			fm.setRepayProfitRate(finType.getFinIntRate());
		}

		if (promotion != null) {
			fm.setRepayBaseRate(null);
			fm.setRepaySpecialRate(null);
			fm.setRepayMargin(BigDecimal.ZERO);
			fm.setRepayProfitRate(promotion.getActualInterestRate());
		} else {
			if (StringUtils.isBlank(fm.getRepayBaseRate()) && fm.getRepayProfitRate().compareTo(BigDecimal.ZERO) == 0
					&& StringUtils.isBlank(fm.getRepaySpecialRate())
					&& fm.getRepayMargin().compareTo(BigDecimal.ZERO) == 0) {
				if (StringUtils.isNotBlank(finType.getFinBaseRate())) {
					fm.setRepayBaseRate(finType.getFinBaseRate());
					fm.setRepaySpecialRate(finType.getFinSplRate());
					fm.setRepayMargin(finType.getFinMargin());
				}
			}
		}

		if (StringUtils.isBlank(fm.getScheduleMethod())) {
			fm.setScheduleMethod(finType.getFinSchdMthd());
		}

		if (promotion != null) {
			fm.setNumberOfTerms(promotion.getTenor() - promotion.getAdvEMITerms());
			fm.setCalTerms(fm.getNumberOfTerms());
		} else {
			if (fm.getNumberOfTerms() == 0 && fm.getMaturityDate() == null) {
				fm.setNumberOfTerms(finType.getFinDftTerms());
				fm.setCalTerms(fm.getNumberOfTerms());
			}
		}

		if (StringUtils.isBlank(fm.getRepayFrq())) {
			if (fm.getNextRepayDate() != null) {
				fm.setRepayFrq(getDftFrequency(finType.getFinRpyFrq(), fm.getNextRepayDate()));

			} else if (fm.getMaturityDate() != null) {
				fm.setRepayFrq(getDftFrequency(finType.getFinRpyFrq(), fm.getMaturityDate()));

			} else if (StringUtils.isNotEmpty(finType.getFrequencyDays())) {
				fm.setRepayFrq(getDftFrequency(finType.getFinRpyFrq(), finType.getFrequencyDays()));

			} else if (fm.getCalGrcEndDate() != null) {
				fm.setRepayFrq(getDftFrequency(finType.getFinRpyFrq(), fm.getCalGrcEndDate()));

			} else {
				fm.setRepayFrq(getDftFrequency(finType.getFinRpyFrq(), fm.getFinStartDate()));
			}
		}

		ErrorDetail error = FrequencyUtil.validateFrequency(fm.getRepayFrq());

		if (error == null) {
			isValidRpyFrq = true;
		}

		if (isValidRpyFrq && fm.getNextRepayDate() == null) {
			Date nextDate = FrequencyUtil.getNextDate(fm.getRepayFrq(), 1, fm.getCalGrcEndDate(),
					HolidayHandlerTypes.MOVE_NONE, false, fm.isAllowGrcPeriod() ? 0 : finType.getFddLockPeriod())
					.getNextFrequencyDate();

			fm.setNextRepayDate(DateUtil.getDatePart(nextDate));
		}

		maturityDefaulting(schdData);

		if (StringUtils.isBlank(fm.getRepayPftFrq()) && isValidRpyFrq) {
			String frq = new StringBuilder(5).append(finType.getFinDftIntFrq().substring(0, 3))
					.append(fm.getRepayFrq().substring(3, 5)).toString();
			fm.setRepayPftFrq(frq);
		}

		error = FrequencyUtil.validateFrequency(fm.getRepayPftFrq());
		if (error == null) {
			isValidOtherFrq = true;
		}

		if (isValidOtherFrq && fm.getNextRepayPftDate() == null) {
			Date nextRpyPftDate = FrequencyUtil.getNextDate(fm.getRepayPftFrq(), 1, fm.getCalGrcEndDate(),
					HolidayHandlerTypes.MOVE_NONE, false, fm.isAllowGrcPeriod() ? 0 : finType.getFddLockPeriod())
					.getNextFrequencyDate();
			nextRpyPftDate = DateUtil.getDatePart(nextRpyPftDate);

			if (DateUtil.compare(fm.getCalMaturity(), nextRpyPftDate) < 0) {
				nextRpyPftDate = fm.getCalMaturity();
			}

			fm.setNextRepayPftDate(nextRpyPftDate);
		}

		if (fm.isAllowRepayRvw()) {
			if (StringUtils.isBlank(fm.getRepayRvwFrq()) && isValidRpyFrq) {
				if (!StringUtils.startsWith(finType.getFinRvwFrq(), FrequencyCodeTypes.FRQ_DAILY)
						&& SysParamUtil.isAllowed(SMTParameterConstants.RESET_FREQUENCY_DATES_REQ)) {
					String frq = new StringBuilder(5).append(finType.getFinRvwFrq().substring(0, 3))
							.append(fm.getRepayFrq().substring(3, 5)).toString();
					fm.setRepayRvwFrq(frq);
				} else {
					fm.setRepayRvwFrq(finType.getFinRvwFrq());
				}
			}

			error = FrequencyUtil.validateFrequency(fm.getRepayRvwFrq());
			if (error == null) {
				isValidOtherFrq = true;
			} else {
				isValidOtherFrq = false;
			}

			if (isValidOtherFrq && fm.getNextRepayRvwDate() == null) {
				Date nextRpyRvwDate = null;
				if (ImplementationConstants.ALLOW_FDD_ON_RVW_DATE) {
					nextRpyRvwDate = FrequencyUtil.getNextDate(fm.getRepayRvwFrq(), 1, fm.getCalGrcEndDate(),
							HolidayHandlerTypes.MOVE_NONE, false, finType.getFddLockPeriod()).getNextFrequencyDate();
				} else {
					nextRpyRvwDate = FrequencyUtil.getNextDate(fm.getRepayRvwFrq(), 1, fm.getCalGrcEndDate(),
							HolidayHandlerTypes.MOVE_NONE, false, 0).getNextFrequencyDate();
				}

				nextRpyRvwDate = DateUtil.getDatePart(nextRpyRvwDate);

				if (DateUtil.compare(fm.getCalMaturity(), nextRpyRvwDate) < 0) {
					nextRpyRvwDate = fm.getCalMaturity();
				}

				fm.setNextRepayRvwDate(nextRpyRvwDate);
			}
		}

		if (fm.isAllowRepayCpz()) {
			if (StringUtils.isBlank(fm.getRepayCpzFrq()) && isValidRpyFrq) {
				String frq = new StringBuilder(5).append(finType.getFinCpzFrq().substring(0, 3))
						.append(fm.getRepayFrq().substring(3, 5)).toString();
				fm.setRepayCpzFrq(frq);
			}

			error = FrequencyUtil.validateFrequency(fm.getRepayCpzFrq());
			if (error == null) {
				isValidOtherFrq = true;
			} else {
				isValidOtherFrq = false;
			}

			if (isValidOtherFrq && fm.getNextRepayCpzDate() == null) {
				Date cpzDate = FrequencyUtil.getNextDate(fm.getRepayCpzFrq(), 1, fm.getCalGrcEndDate(),
						HolidayHandlerTypes.MOVE_NONE, false, finType.getFddLockPeriod()).getNextFrequencyDate();
				cpzDate = DateUtil.getDatePart(cpzDate);

				if (DateUtil.compare(fm.getCalMaturity(), cpzDate) < 0) {
					cpzDate = fm.getCalMaturity();
				}

				fm.setNextRepayCpzDate(cpzDate);
			}
		}

		if (fm.isAlwBPI()) {
			if (StringUtils.isBlank(fm.getBpiTreatment())) {
				fm.setBpiTreatment(finType.getBpiTreatment());
			}
			fm.setBpiPftDaysBasis(finType.getBpiPftDaysBasis());
		} else {
			fm.setBpiTreatment(FinanceConstants.BPI_NO);
		}

		fm.setFixedRateTenor(finType.getFixedRateTenor());
		fm.setEqualRepay(finType.isEqualRepayment());

		if (finType.isAlwUnPlanEmiHoliday()) {
			if (fm.getMaxUnplannedEmi() <= 0) {
				fm.setMaxUnplannedEmi(finType.getMaxUnplannedEmi());
			}
			if (fm.getUnPlanEMIHLockPeriod() <= 0) {
				fm.setUnPlanEMIHLockPeriod(finType.getUnPlanEMIHLockPeriod());
			}
			if (!fm.isUnPlanEMICpz()) {
				fm.setUnPlanEMICpz(finType.isUnPlanEMICpz());
			}
		}
	}

	private void overdueDefaulting(String vldGroup, FinanceDetail fd) {
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceType finType = schdData.getFinanceType();

		if (schdData.getFinODPenaltyRate() == null && finType.isApplyODPenalty()) {
			FinODPenaltyRate opr = new FinODPenaltyRate();

			opr.setApplyODPenalty(finType.isApplyODPenalty());
			opr.setODIncGrcDays(finType.isODIncGrcDays());
			opr.setODChargeCalOn(finType.getODChargeCalOn());
			opr.setODGraceDays(finType.getODGraceDays());
			opr.setODChargeType(finType.getODChargeType());
			opr.setODChargeAmtOrPerc(finType.getODChargeAmtOrPerc());
			opr.setODAllowWaiver(finType.isODAllowWaiver());
			opr.setODMaxWaiverPerc(finType.getODMaxWaiverPerc());
			opr.setODRuleCode(finType.getODRuleCode());
			opr.setOdMinAmount(finType.getOdMinAmount());

			schdData.setFinODPenaltyRate(opr);

		} else if (schdData.getFinODPenaltyRate() != null && !finType.isApplyODPenalty()) {
			FinODPenaltyRate opr = schdData.getFinODPenaltyRate();
			String odChargeType = opr.getODChargeType();
			int ccyFormat = CurrencyUtil.getFormat(finType.getFinCcy());

			if (ChargeType.PERC_ONE_TIME.equals(odChargeType) || ChargeType.PERC_ON_DUE_DAYS.equals(odChargeType)
					|| ChargeType.PERC_ON_EFF_DUE_DAYS.equals(odChargeType)
					|| ChargeType.PERC_ON_PD_MTH.equals(odChargeType)) {

				opr.setODChargeAmtOrPerc(PennantApplicationUtil.unFormateAmount(opr.getODChargeAmtOrPerc(), ccyFormat));
			}
		}
	}

	/*
	 * _______________________________________________________________________________________________________________
	 * DEFAULT STEP DETAILS
	 * _______________________________________________________________________________________________________________
	 */

	private void stepDefaulting(String vldGroup, FinScheduleData schdData) {
		FinanceMain fm = schdData.getFinanceMain();
		FinanceType financeType = schdData.getFinanceType();

		// Manual Steps? Default Step type
		if (fm.isAlwManualSteps()) {
			if (PennantConstants.STEPPING_CALC_PERC.equals(fm.getCalcOfSteps())) {
				if (StringUtils.isBlank(fm.getStepType())) {
					fm.setStepType(financeType.getDftStepPolicyType());
				}
			}
		} else {
			// Default Step Policy
			if (StringUtils.isBlank(fm.getStepPolicy())) {
				fm.setStepPolicy(financeType.getDftStepPolicy());
				fm.setStepType(financeType.getDftStepPolicyType());
			}
		}

		if (StringUtils.isBlank(fm.getCalcOfSteps())) {
			fm.setCalcOfSteps(financeType.getCalcOfSteps());
		}

		if (StringUtils.isBlank(fm.getStepsAppliedFor())) {
			fm.setStepsAppliedFor(financeType.getStepsAppliedFor());
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

				// Sorting available list
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
				String frqDay = String.valueOf(DateUtil.getDay(frqDate));
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
				&& !StringUtils.equals(IDB, CalculationConstants.IDB_30E360IA)
				&& !StringUtils.equals(IDB, CalculationConstants.IDB_15E360IA)
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
	private void graceEndDefaulting(FinScheduleData schdData) {
		FinanceMain fm = schdData.getFinanceMain();
		FinanceType finType = schdData.getFinanceType();

		Date grcPEndDate = fm.getGrcPeriodEndDate();
		int graceTerms = fm.getGraceTerms();
		String grcPftFrq = fm.getGrcPftFrq();
		Date nxtGrcPftDate = fm.getNextGrcPftDate();
		Date finStartDate = fm.getFinStartDate();

		int fddLockPeriod = finType.getFddLockPeriod();

		if (graceTerms == 0 && grcPEndDate == null) {
			return;
		}

		if (graceTerms != 0 && grcPEndDate != null) {
			return;
		}

		if (graceTerms <= 0) {
			fm.setCalGrcEndDate(grcPEndDate);

			if (StringUtils.isNotBlank(grcPftFrq) && nxtGrcPftDate != null) {
				fm.setCalGrcTerms(FrequencyUtil.getTerms(grcPftFrq, nxtGrcPftDate, grcPEndDate, true, true).getTerms());
			}

			return;
		}

		fm.setCalGrcTerms(graceTerms);

		if (StringUtils.isNotBlank(grcPftFrq) && grcPEndDate == null) {
			List<Calendar> schDates = FrequencyUtil.getNextDate(grcPftFrq, graceTerms, finStartDate,
					HolidayHandlerTypes.MOVE_NONE, false, fddLockPeriod).getScheduleList();

			if (schDates != null) {
				fm.setCalGrcEndDate(DateUtil.getDatePart(schDates.get(schDates.size() - 1).getTime()));
			}
		}
	}

	/*
	 * _______________________________________________________________________________________________________________
	 * DEFAULT Terms and Maturity Date
	 * _______________________________________________________________________________________________________________
	 */
	private void maturityDefaulting(FinScheduleData schdData) {
		FinanceMain fm = schdData.getFinanceMain();
		FinanceType finType = schdData.getFinanceType();

		if (fm.getNumberOfTerms() == 0 && fm.getMaturityDate() == null) {
			if (finType.getFinDftTerms() > 0) {
				fm.setNumberOfTerms(finType.getFinDftTerms());
			} else {
				return;
			}
		}

		int numberOfTerms = fm.getNumberOfTerms();
		String repayFrq = fm.getRepayFrq();
		Date nextRepayDate = fm.getNextRepayDate();
		String productCategory = fm.getProductCategory();

		if (numberOfTerms != 0 && fm.getMaturityDate() != null) {
			return;
		}

		if (numberOfTerms <= 0) {
			fm.setCalMaturity(fm.getMaturityDate());
			if (StringUtils.isNotBlank(repayFrq) && nextRepayDate != null) {
				if (!schdData.getFinanceMain().getProductCategory().equals(FinanceConstants.PRODUCT_ODFACILITY)) {
					int terms = FrequencyUtil.getTerms(repayFrq, nextRepayDate, fm.getMaturityDate(), true, true)
							.getTerms();
					fm.setCalTerms(terms);
				}
			}

			return;
		}

		if (StringUtils.isNotBlank(repayFrq) && nextRepayDate != null) {
			boolean includeBaseDate = false;
			Date baseDate = null;

			if (!FinanceConstants.PRODUCT_ODFACILITY.equals(productCategory)) {
				fm.setCalTerms(numberOfTerms);
				includeBaseDate = true;
				baseDate = nextRepayDate;
			} else {
				baseDate = fm.getFinStartDate();
			}

			List<Calendar> schdDates = FrequencyUtil
					.getNextDate(repayFrq, numberOfTerms, baseDate, HolidayHandlerTypes.MOVE_NONE, includeBaseDate, 0)
					.getScheduleList();

			if (schdDates != null) {
				fm.setCalMaturity(DateUtil.getDatePart(schdDates.get(schdDates.size() - 1).getTime()));
			}
		}
	}

	public void doFinanceDetailDefaulting(FinanceDetail fd) {
		FinScheduleData schdData = fd.getFinScheduleData();
		long finID = schdData.getFinanceMain().getFinID();

		FinanceMain fm = financeMainDAO.getFinanceDetailsForService(finID, "_Temp", false);

		if (fd.getAdvancePaymentsList() != null) {
			for (FinAdvancePayments payment : fd.getAdvancePaymentsList()) {
				payment.setLLDate(payment.getLlDate() == null ? fm.getFinStartDate() : payment.getLlDate());
			}
		}

		if (fd.getMandate() != null) {
			Mandate mandate = fd.getMandate();
			mandate.setStartDate(mandate.getStartDate() == null ? fm.getFinStartDate() : mandate.getStartDate());
			if (!mandate.isOpenMandate() && mandate.getExpiryDate() == null) {
				mandate.setExpiryDate(DateUtil.addDays(fm.getMaturityDate(), 1));
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
