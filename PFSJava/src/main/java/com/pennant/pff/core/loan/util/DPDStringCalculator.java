package com.pennant.pff.core.loan.util;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.core.CustEODEvent;
import com.pennant.app.core.FinEODEvent;
import com.pennant.app.util.FrequencyUtil;
import com.pennant.backend.model.eventproperties.EventProperties;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.core.util.SchdUtil;

public class DPDStringCalculator {
	private static Logger logger = LogManager.getLogger(DPDStringCalculator.class);

	/**
	 * 
	 * @param custEODEvent
	 * @param monthEnd
	 */
	public static void process(CustEODEvent custEODEvent, boolean monthEnd) {
		logger.debug(Literal.ENTERING);

		EventProperties eventProperties = custEODEvent.getEventProperties();

		int dpdStringLength = eventProperties.getDpdStringLength();
		List<FinEODEvent> finEODEvents = custEODEvent.getFinEODEvents();
		Date eodDate = custEODEvent.getEodDate();

		for (FinEODEvent finEODEvent : finEODEvents) {
			FinanceMain fm = finEODEvent.getFinanceMain();

			logger.info("DPD Calculation is proccessing for the Loan {}", fm.getFinReference());

			List<FinanceScheduleDetail> schedules = finEODEvent.getFinanceScheduleDetails();
			FinanceProfitDetail fpd = finEODEvent.getFinProfitDetail();

			String dpdString = getDpdString(monthEnd, eodDate, fm, schedules);
			fpd.setCurDPDString(deriveDPDString(dpdStringLength, fpd.getCurDPDString(), dpdString));

			logger.info("DPD Calculation is proccessed for the Loan {} and Calculated DPD String is {}",
					fm.getFinReference(), fpd.getCurDPDString());
		}

		logger.debug(Literal.LEAVING);
	}

	private static String deriveDPDString(int dpdStringLength, String curDPDStr, String dpdString) {
		if (StringUtils.isEmpty(dpdString)) {
			logger.info("Calculated DPD is Empty");
			return curDPDStr;
		}

		if (StringUtils.isNotEmpty(curDPDStr) && curDPDStr.length() == dpdStringLength) {
			curDPDStr = curDPDStr.substring(1);
		}

		if (StringUtils.isEmpty(curDPDStr)) {
			logger.info("Calculated DPD is Empty");
			return dpdString;
		}

		return curDPDStr.concat(dpdString);
	}

	private static String getDpdString(boolean monthEnd, Date eodDate, FinanceMain fm,
			List<FinanceScheduleDetail> schedules) {
		if (monthEnd) {
			return getDueBucket(SchdUtil.getPastDueDays(schedules, eodDate, 1));
		}

		int frequencyDay = FrequencyUtil.getIntFrequencyDay(fm.getRepayFrq());
		int dueDay = DateUtil.getDay(fm.getEventProperties().getBusinessDate());

		if (frequencyDay == dueDay) {
			return getDueBucket((int) Math.ceil((SchdUtil.getPastDueDays(schedules, eodDate, 0) / 30.0)));
		}

		return null;
	}

	/**
	 * 
	 * @param dueBucket
	 * @return
	 */
	private static String getDueBucket(int dueBucket) {
		if (dueBucket > 9) {
			int i = 65;

			for (int j = 10; j <= 35; j++) {
				if (j == dueBucket) {
					return String.valueOf((char) i);
				}
				i++;
			}

			return String.valueOf((char) i);
		}

		return String.valueOf(dueBucket);
	}
}
