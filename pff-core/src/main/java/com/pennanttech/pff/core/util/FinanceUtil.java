package com.pennanttech.pff.core.util;

import java.util.Date;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.eventproperties.EventProperties;
import com.pennant.backend.model.finance.FinReceiptData;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.util.DisbursementConstants;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pff.overdue.constants.ChargeType;

public class FinanceUtil {

	private FinanceUtil() {
		super();
	}

	public static Date deriveClosedDate(final FinanceMain fm) {
		Date closedDate = fm.getClosedDate();

		if (closedDate == null) {
			EventProperties eventProperties = fm.getEventProperties();

			if (eventProperties.isParameterLoaded()) {
				closedDate = eventProperties.getAppDate();
			} else {
				closedDate = SysParamUtil.getAppDate();
			}
		}

		return closedDate;
	}

	public static Date deriveClosedDate(Date closedDate) {
		FinanceMain fm = new FinanceMain();
		fm.setClosedDate(closedDate);

		return deriveClosedDate(fm);
	}

	public static boolean isMinimunODCChargeReq(String chargeType) {
		return ChargeType.PERC_ONE_TIME.equals(chargeType) || ChargeType.PERC_ON_PD_MTH.equals(chargeType);
	}

	public static boolean isValidDisbStatus(String status) {
		if (status == null) {
			return false;
		}

		switch (status) {
		case DisbursementConstants.STATUS_REVERSED, DisbursementConstants.STATUS_REJECTED,
				DisbursementConstants.STATUS_APPROVED, DisbursementConstants.STATUS_CANCEL:
			return true;
		default:
			return false;
		}
	}

	public static boolean isInValidDisbStatus(String status) {
		return !isValidDisbStatus(status);
	}

	public static String getRepayHierarchy(FinReceiptData rd, FinanceMain fm, FinanceType finType) {
		FinReceiptHeader rch = rd.getReceiptHeader();
		Date valueDate = rch.getValueDate();

		String rpyHierarchy = null;

		String loanType = "[" + fm.getFinType() + "]";

		if (fm.isWriteoffLoan()) {
			rpyHierarchy = finType.getWriteOffRepayHry();

			if (rpyHierarchy == null) {
				throw new AppException(
						"Repayment Hierarchy for [Write-Off] Loans' not configured for the Loan Type " + loanType);
			}

			return rpyHierarchy;
		}

		if (fm.isUnderNpa()) {
			rpyHierarchy = finType.getNpaRpyHierarchy();

			if (rpyHierarchy == null) {
				throw new AppException(
						"Repayment Hierarchy for [NPA Loans] not configured for the Loan Type " + loanType);
			}

			return rpyHierarchy;
		}

		if (fm.isFinIsActive() && fm.getMaturityDate().compareTo(valueDate) <= 0) {
			if (rd.isPresentment()) {
				rpyHierarchy = finType.getPresentmentRepayHry();

				if (rpyHierarchy == null) {
					throw new AppException(
							"Repayment Hierarchy for [Presentment Receipts] not configured for the Loan Type "
									+ loanType);
				}

				return rpyHierarchy;
			}

			rpyHierarchy = finType.getMatureRepayHry();

			if (rpyHierarchy == null) {
				throw new AppException(
						"Repayment Hierarchy for [Matured Loans] not configured for the Loan Type " + loanType);
			}

			return rpyHierarchy;
		}

		if (rd.isPresentment()) {
			rpyHierarchy = finType.getPresentmentRepayHry();

			if (rpyHierarchy == null) {
				throw new AppException(
						"Repayment Hierarchy for [Presentment Receipts] not configured for the Loan Type " + loanType);
			}

			return rpyHierarchy;
		}

		rpyHierarchy = finType.getRpyHierarchy();

		if (rpyHierarchy == null) {
			throw new AppException("Repayment Hierarchy not configured for the Loan Type " + loanType);
		}

		return rpyHierarchy;
	}
}