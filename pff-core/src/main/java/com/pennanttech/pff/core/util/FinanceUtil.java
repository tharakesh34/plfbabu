package com.pennanttech.pff.core.util;

import java.util.Date;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.eventproperties.EventProperties;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.util.DisbursementConstants;
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
		case DisbursementConstants.STATUS_REVERSED:
		case DisbursementConstants.STATUS_REJECTED:
		case DisbursementConstants.STATUS_APPROVED:
		case DisbursementConstants.STATUS_CANCEL:
			return true;
		default:
			return false;
		}
	}

	public static boolean isInValidDisbStatus(String status) {
		return !isValidDisbStatus(status);
	}

}
