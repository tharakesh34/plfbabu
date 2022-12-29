package com.pennant.backend.service.finance;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;

import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.receipts.ReceiptAllocationDetailDAO;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;

public class PartPayAndEarlySettleValidator implements Serializable {
	private static final long serialVersionUID = -3026697995639794179L;

	private static final Logger logger = LogManager.getLogger(PartPayAndEarlySettleValidator.class);

	private ReceiptAllocationDetailDAO receiptAllocationDetailDAO;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;

	public ErrorDetail validatePartPay(FinScheduleData schdData, BigDecimal recieptAmount) {
		logger.debug(Literal.ENTERING);

		ErrorDetail error = null;

		Date appDate = SysParamUtil.getAppDate();

		if (schdData == null || schdData.getFinanceMain() == null || schdData.getFinanceType() == null) {
			return error;
		}

		FinanceMain fm = schdData.getFinanceMain();
		FinanceType ft = schdData.getFinanceType();

		fm.setAppDate(appDate);

		if (recieptAmount.compareTo(BigDecimal.ZERO) == 0) {
			return null;
		}

		int ccyFormat = CurrencyUtil.getFormat(SysParamUtil.getAppCurrency());

		error = validateLockPeriod(ft.getPpLockInPeriod(), fm.getFinStartDate(), appDate);
		if (error != null) {
			return error;
		}

		error = validateMinAmount(ft, fm, recieptAmount, ccyFormat);
		if (error != null) {
			return error;
		}

		error = validateMaxAmount(ft, fm, recieptAmount, ccyFormat);
		if (error != null) {
			return error;
		}

		error = validatePartPaymentAmount(ft, fm, recieptAmount, ccyFormat);

		logger.debug(Literal.LEAVING);
		return error;
	}

	public ErrorDetail validateEarlyPay(FinScheduleData schdData) {
		Date appDate = SysParamUtil.getAppDate();

		FinanceMain fm = schdData.getFinanceMain();
		FinanceType ft = schdData.getFinanceType();

		int esLockPeriod = ft.getEsLockInPeriod();
		if (esLockPeriod <= 0) {
			return null;
		}

		int months = DateUtil.getMonthsBetween(fm.getFinStartDate(), appDate);
		if (months <= esLockPeriod) {
			return new ErrorDetail("ESL", "Early Settlement not allowed in lock period", null);
		}

		return null;
	}

	private ErrorDetail validateLockPeriod(int ppLockPeriod, Date finStartDate, Date appDate) {
		if (ppLockPeriod <= 0) {
			return null;
		}

		int months = DateUtil.getMonthsBetween(finStartDate, appDate);

		if (months < ppLockPeriod) {
			return new ErrorDetail("PPL", Labels.getLabel("label_pplockinperiod"), new String[1]);
		}

		return null;
	}

	private ErrorDetail validateMinAmount(FinanceType ft, FinanceMain fm, BigDecimal recieptAmount, int ccyFormat) {
		String paymentMethod = ft.getMinPPCalType();
		String paymentOn = ft.getMinPPCalOn();
		BigDecimal ppAmount = ft.getMinPPAmount();
		BigDecimal ppPercentage = ft.getMinPPPercentage();

		BigDecimal currAssetValue = PennantApplicationUtil.formateAmount(fm.getFinCurrAssetValue(), ccyFormat);

		switch (paymentMethod) {
		case PennantConstants.PREPYMT_CALCTN_TYPE_PERCENTAGE:
			if (PennantConstants.PREPYMT_CALCULATEDON_SANCTIONLOANAMOUNT.equals(paymentOn)) {
				BigDecimal perAmount = calculateAmount(fm, currAssetValue, ppPercentage);

				if (recieptAmount.compareTo(perAmount) < 0) {
					return new ErrorDetail("PPA", "Minimum Part Payment Amount Allowed : " + perAmount, null);
				}
			}

			break;
		case PennantConstants.PREPYMT_CALCTN_TYPE_FIXEDAMT:
			BigDecimal fixAmount = PennantApplicationUtil.formateAmount(ppAmount, ccyFormat);

			if (recieptAmount.compareTo(fixAmount) < 0) {
				return new ErrorDetail("PPA", "Minimum Part Payment Amount Allowed : " + fixAmount, null);
			}

			break;
		default:
			break;
		}

		return null;
	}

	private ErrorDetail validateMaxAmount(FinanceType ft, FinanceMain fm, BigDecimal recieptAmount, int ccyFormat) {
		String paymentMethod = ft.getMaxPPCalType();
		String paymentOn = ft.getMaxPPCalOn();
		BigDecimal ppAmount = ft.getMaxPPAmount();
		BigDecimal ppPercentage = ft.getMaxPPPercentage();

		BigDecimal currAssetValue = PennantApplicationUtil.formateAmount(fm.getFinCurrAssetValue(), ccyFormat);

		switch (paymentMethod) {
		case PennantConstants.PREPYMT_CALCTN_TYPE_PERCENTAGE:
			if (PennantConstants.PREPYMT_CALCULATEDON_SANCTIONLOANAMOUNT.equals(paymentOn)) {
				BigDecimal perAmount = calculateAmount(fm, currAssetValue, ppPercentage);

				if (recieptAmount.compareTo(perAmount) > 0) {
					return new ErrorDetail("PPA", "Maximum Part Payment Amount Allowed : " + perAmount, null);
				}
			}
			break;
		case PennantConstants.PREPYMT_CALCTN_TYPE_FIXEDAMT:
			BigDecimal fixAmount = PennantApplicationUtil.formateAmount(ppAmount, ccyFormat);

			if (recieptAmount.compareTo(fixAmount) > 0) {
				return new ErrorDetail("PPA", "Maximum Part Payment Amount Allowed : " + fixAmount, null);
			}
			break;
		default:
			break;
		}

		return null;
	}

	private ErrorDetail validatePartPaymentAmount(FinanceType ft, FinanceMain fm, BigDecimal recieptAmount,
			int ccyFormat) {
		String paymentMethod = ft.getMaxFPPCalType();
		String paymentOn = ft.getMaxFPPCalOn();
		BigDecimal ppAmount = ft.getMaxFPPAmount();
		BigDecimal ppPercentage = ft.getMaxFPPPer();

		Date finStartDate = fm.getFinStartDate();
		BigDecimal finCurAssetValue = fm.getFinCurrAssetValue();
		long finID = fm.getFinID();

		Date appDate = fm.getAppDate();

		Date fStartDate = getFinancialYearStart(finStartDate);
		Date fEndDate = getFinancialYearEnd(finStartDate);

		boolean isInFincialYear = DateUtil.compare(appDate, fEndDate) <= 0;

		switch (paymentMethod) {
		case PennantConstants.PREPYMT_CALCTN_TYPE_PERCENTAGE:
			if (PennantConstants.PARTPAYMENT_CALCULATEDON_POS.equals(paymentOn)) {
				BigDecimal totalAmount = finCurAssetValue;

				if (isInFincialYear) {
					BigDecimal partPayAmount = receiptAllocationDetailDAO.getPartPayAmount(finID, fStartDate, fEndDate);
					BigDecimal partPayDisAmount = financeScheduleDetailDAO.getPartPayDisAmount(finID, fStartDate,
							fEndDate);

					totalAmount = totalAmount.subtract(partPayAmount).add(partPayDisAmount);
				} else {
					totalAmount = financeScheduleDetailDAO.getClosureBalance(finID, appDate);
				}

				totalAmount = PennantApplicationUtil.formateAmount(totalAmount, ccyFormat);

				BigDecimal perAmount = calculateAmount(fm, totalAmount, ppPercentage);

				if (recieptAmount.compareTo(perAmount) > 0) {
					return new ErrorDetail("PPA", "Maximum Part Payment Amount Allowed Rs : " + perAmount, null);
				}
			}
			break;
		case PennantConstants.PREPYMT_CALCTN_TYPE_FIXEDAMT:
			BigDecimal fixAmount = PennantApplicationUtil.formateAmount(ppAmount, ccyFormat);

			if (recieptAmount.compareTo(fixAmount) > 0) {
				return new ErrorDetail("PPA", "Maximum Part Payment Amount Allowed : " + fixAmount, null);
			}

			break;
		}

		return null;
	}

	private BigDecimal calculateAmount(FinanceMain fm, BigDecimal currAssetValue, BigDecimal ppPercentage) {
		BigDecimal perAmount = (currAssetValue.multiply(ppPercentage)).divide(BigDecimal.valueOf(100), 9,
				RoundingMode.HALF_DOWN);

		String roundingMode = fm.getCalRoundingMode();
		int roundingTarget = fm.getRoundingTarget();

		perAmount = CalculationUtil.roundAmount(perAmount, roundingMode, roundingTarget);
		return perAmount;
	}

	private static Date getFinancialYearStart(Date date) {
		int year = DateUtil.getYear(date);

		int month = DateUtil.getMonth(date);

		if (month < 3) {
			year = year - 1;
		}

		return DateUtil.getDate(year, 3, 1);
	}

	private static Date getFinancialYearEnd(Date date) {
		int year = DateUtil.getYear(date);

		int month = DateUtil.getMonth(date);

		if (month < 3) {
			year = year - 1;
		}

		return DateUtil.getDate(year + 1, 2, 31);
	}

	@Autowired
	public void setReceiptAllocationDetailDAO(ReceiptAllocationDetailDAO receiptAllocationDetailDAO) {
		this.receiptAllocationDetailDAO = receiptAllocationDetailDAO;
	}

	@Autowired
	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

}
