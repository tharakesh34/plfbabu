package com.pennant.pff.core.loan.util;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.constants.FrequencyCodeTypes;
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

	private DPDStringCalculator() {
		super();
	}

	/**
	 * <p>
	 * Processes the DPD String Calculation for all the active loans in @param custEODEvent while running the EOD.
	 * </p>
	 * <p>
	 * DPD String will be stored in {@link FinanceProfitDetail#setCurDPDString(String)}
	 * </p>
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

			String dpdString = getDpdString(monthEnd, eodDate, fm, schedules, fpd.getCurODDays());
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
			List<FinanceScheduleDetail> schedules, int curduedays) {
		if (monthEnd) {
			if (curduedays == 0) {
				return null;
			}
			return getDueBucket((int) Math.ceil((curduedays / 30.0)));
		}

		String frqCode = fm.getRepayFrq().substring(0, 1);

		int frequencyDay = FrequencyUtil.getIntFrequencyDay(fm.getRepayFrq());
		int dueDay = DateUtil.getDay(fm.getEventProperties().getBusinessDate());

		if (FrequencyCodeTypes.FRQ_DAILY.equals(frqCode) || FrequencyCodeTypes.FRQ_WEEKLY.equals(frqCode)
				|| FrequencyCodeTypes.FRQ_FORTNIGHTLY.equals(frqCode)
				|| FrequencyCodeTypes.FRQ_BIWEEKLY.equals(frqCode)) {
			if (schedules.stream()
					.anyMatch(s -> s.getSchDate().compareTo(fm.getEventProperties().getBusinessDate()) == 0)) {
				frequencyDay = dueDay;
			}
		}

		if (frequencyDay == dueDay) {
			int pastDueDays = 0;
			if (FrequencyCodeTypes.FRQ_DAILY.equals(frqCode)) {
				if (curduedays == 0) {
					return null;
				}
				pastDueDays = curduedays;
			} else {
				pastDueDays = SchdUtil.getPastDueDays(schedules, eodDate);
			}

			return getDueBucket((int) Math.ceil((pastDueDays / 30.0)));
		}

		return null;
	}

	/**
	 * <p>
	 * 
	 * Returns the DPD String for the specified DueBucket.
	 * </p>
	 * <p>
	 * <li>When the Due Bucket is less than 9 then due @param dueBucket will be considered as DPD string.</li>
	 * <li>When the specified @param dueBucket is greater than 35 then DPD String will be considered as
	 * <strong>Z</strong></li>
	 * <li>When the specified @param dueBucket is between 9 to 34 then DPD String will considered as ASCII Equalent of
	 * the @param dueBucket</li>
	 * <p>
	 * Example :
	 * <table>
	 * <th>Example</th>
	 * <tr>
	 * <td>Due Bucket</td>
	 * <td>DPD String</td>
	 * </tr>
	 * <tr>
	 * <td>1</td>
	 * <td>1</td>
	 * </tr>
	 * <tr>
	 * <td>2</td>
	 * <td>2</td>
	 * </tr>
	 * <tr>
	 * <td></td>
	 * <td></td>
	 * </tr>
	 * <tr>
	 * <td>9</td>
	 * <td>9</td>
	 * </tr>
	 * <tr>
	 * <td>10</td>
	 * <td>A</td>
	 * </tr>
	 * <tr>
	 * <td>11</td>
	 * <td>B</td>
	 * </tr>
	 * <tr>
	 * <td>34</td>
	 * <td>Y</td>
	 * </tr>
	 * <tr>
	 * <td>35</td>
	 * <td>Z</td>
	 * </tr>
	 * <tr>
	 * <td>36</td>
	 * <td>Z</td>
	 * </tr>
	 * <tr>
	 * <td>99</td>
	 * <td>Z</td>
	 * </tr>
	 * </table>
	 * 
	 * @param dueBucket
	 * @return Returns the DPD String for the specified DueBucket.
	 */
	private static String getDueBucket(int dueBucket) {
		if (dueBucket <= 9) {
			return String.valueOf(dueBucket);
		}

		int i = 65;

		for (int j = 10; j <= 35; j++) {
			if (j == dueBucket) {
				return String.valueOf((char) i);
			}
			i++;
		}
		return String.valueOf((char) i);
	}
}
