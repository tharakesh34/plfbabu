package com.pennant.backend.service.finance;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.resource.Labels;

import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.finance.FinReceiptData;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.ReceiptAllocationDetail;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.core.RequestSource;
import com.pennanttech.pff.receipt.constants.Allocation;

public class PartPayAndEarlySettleValidator implements Serializable {
	private static final long serialVersionUID = -3026697995639794179L;

	private static final Logger logger = LogManager.getLogger(PartPayAndEarlySettleValidator.class);

	private static final BigDecimal BG_ZERO = BigDecimal.ZERO;

	public ErrorDetail validatePartPay(FinReceiptData receiptData) {
		logger.debug(Literal.ENTERING);

		ErrorDetail error = null;
		FinScheduleData schdData = receiptData.getFinanceDetail().getFinScheduleData();
		Date appDate = SysParamUtil.getAppDate();

		if (schdData == null || schdData.getFinanceMain() == null || schdData.getFinanceType() == null
				|| schdData.getFinanceScheduleDetails() == null) {
			return error;
		}

		FinanceMain fm = schdData.getFinanceMain();
		FinanceType ft = schdData.getFinanceType();

		fm.setAppDate(appDate);

		BigDecimal recieptAmount = BG_ZERO;

		RequestSource requestSource = receiptData.getRequestSource();
		if (RequestSource.API == requestSource || RequestSource.UPLOAD == requestSource
				|| RequestSource.EOD == requestSource) {
			List<ReceiptAllocationDetail> allocations = receiptData.getReceiptHeader().getAllocationsSummary();
			if (CollectionUtils.isNotEmpty(allocations)) {
				for (ReceiptAllocationDetail alloc : allocations) {
					if (Allocation.PP.equals(alloc.getAllocationType())) {
						recieptAmount = recieptAmount.add(alloc.getPaidAmount());
					}
				}
			}
		} else {
			recieptAmount = recieptAmount.add(receiptData.getReceiptHeader().getBalAmount());
		}

		if (recieptAmount.compareTo(BG_ZERO) == 0) {
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

		error = validatePartPaymentAmount(receiptData, recieptAmount, ccyFormat);

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
			return ErrorUtil.getError("92021", "Early Settlement not allowed in lock period");
		}

		return null;
	}

	private ErrorDetail validateLockPeriod(int ppLockPeriod, Date finStartDate, Date appDate) {
		if (ppLockPeriod <= 0) {
			return null;
		}

		int months = DateUtil.getMonthsBetween(finStartDate, appDate);

		if (months < ppLockPeriod) {
			return ErrorUtil.getError("92021", Labels.getLabel("label_pplockinperiod"));
		}

		return null;
	}

	private ErrorDetail validateMinAmount(FinanceType ft, FinanceMain fm, BigDecimal recieptAmount, int ccyFormat) {
		String paymentMethod = ft.getMinPPCalType();
		String paymentOn = ft.getMinPPCalOn();
		BigDecimal ppAmount = ft.getMinPPAmount();
		BigDecimal ppPercentage = ft.getMinPPPercentage();

		BigDecimal currAssetValue = fm.getFinCurrAssetValue();

		switch (paymentMethod) {
		case PennantConstants.PREPYMT_CALCTN_TYPE_PERCENTAGE:
			if (PennantConstants.PREPYMT_CALCULATEDON_SANCTIONLOANAMOUNT.equals(paymentOn)) {
				BigDecimal perAmount = calculateAmount(fm, currAssetValue, ppPercentage);

				if (recieptAmount.compareTo(perAmount) < 0) {
					getError("30550", "Minimum Part Payment Amount Allowed ", ppAmount, ccyFormat);
				}
			}

			break;
		case PennantConstants.PREPYMT_CALCTN_TYPE_FIXEDAMT:
			if (recieptAmount.compareTo(ppAmount) < 0) {
				return getError("30550", "Minimum Part Payment Amount Allowed ", ppAmount, ccyFormat);
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

		BigDecimal currAssetValue = fm.getFinCurrAssetValue();

		switch (paymentMethod) {
		case PennantConstants.PREPYMT_CALCTN_TYPE_PERCENTAGE:
			if (PennantConstants.PREPYMT_CALCULATEDON_SANCTIONLOANAMOUNT.equals(paymentOn)) {
				BigDecimal perAmount = calculateAmount(fm, currAssetValue, ppPercentage);

				if (recieptAmount.compareTo(perAmount) > 0) {
					return getError("30550", "Maximum Part Payment Amount Allowed ", perAmount, ccyFormat);
				}
			}
			break;
		case PennantConstants.PREPYMT_CALCTN_TYPE_FIXEDAMT:
			if (recieptAmount.compareTo(ppAmount) > 0) {
				return getError("30550", "Maximum Part Payment Amount Allowed ", ppAmount, ccyFormat);
			}
			break;
		default:
			break;
		}

		return null;
	}

	private ErrorDetail validatePartPaymentAmount(FinReceiptData receiptData, BigDecimal recieptAmount, int ccyFormat) {
		FinScheduleData schdData = receiptData.getFinanceDetail().getFinScheduleData();
		FinanceType ft = schdData.getFinanceType();
		FinanceMain fm = schdData.getFinanceMain();
		List<FinanceScheduleDetail> schedules = schdData.getFinanceScheduleDetails();

		String paymentMethod = ft.getMaxFPPCalType();
		String paymentOn = ft.getMaxFPPCalOn();
		BigDecimal ppAmount = ft.getMaxFPPAmount();
		BigDecimal ppPercentage = ft.getMaxFPPPer();

		Date appDate = fm.getAppDate();

		Date fStartDate = getFinancialYearStart(appDate);
		Date fEndDate = getFinancialYearEnd(appDate);

		BigDecimal totalAmount = null;
		BigDecimal partPayAmount = BG_ZERO;
		BigDecimal partPayDisAmount = BG_ZERO;

		partPayAmount = receiptData.getRemBal();

		for (FinanceScheduleDetail schd : schedules) {
			if (DateUtil.compare(schd.getSchDate(), fStartDate) >= 0
					&& DateUtil.compare(schd.getSchDate(), fEndDate) <= 0) {

				if (totalAmount == null) {
					totalAmount = schd.getClosingBalance();
				}
				if ("R".equals(schd.getSpecifier())) {
					partPayDisAmount = partPayDisAmount.add(schd.getDisbAmount());
				}
			}
		}

		if (PennantConstants.PREPYMT_CALCTN_TYPE_PERCENTAGE.equals(paymentMethod)) {
			if (PennantConstants.PARTPAYMENT_CALCULATEDON_POS.equals(paymentOn)) {
				if (totalAmount == null) {
					totalAmount = BG_ZERO;
				}
				partPayAmount = partPayAmount.subtract(recieptAmount);

				totalAmount = totalAmount.add(partPayDisAmount);

				BigDecimal perAmount = calculateAmount(fm, totalAmount, ppPercentage);

				perAmount = perAmount.subtract(partPayAmount);

				if (recieptAmount.compareTo(perAmount) > 0) {
					return getError("30550", "Maximum Part Payment Amount Allowed Rs ", perAmount, ccyFormat);
				}
			}
		} else if (PennantConstants.PREPYMT_CALCTN_TYPE_FIXEDAMT.equals(paymentMethod)) {
			if (partPayAmount.compareTo(ppAmount) > 0) {
				BigDecimal currentpaid = partPayAmount.subtract(recieptAmount);
				return getError("30550", "Maximum Part Payment Amount Allowed Rs ", ppAmount.subtract(currentpaid),
						ccyFormat);
			}
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

	private ErrorDetail getError(String code, String message, BigDecimal perAmount, int ccyFormat) {
		String amount = PennantApplicationUtil.amountFormate(perAmount, ccyFormat);

		return ErrorUtil.getError(code, message, " : ", amount);
	}

}
