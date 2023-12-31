package com.pennant.pff.core.loan.util;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.pennant.app.util.FrequencyUtil;
import com.pennant.app.util.LookupMethods;
import com.pennant.backend.model.finance.CustEODEvent;
import com.pennant.backend.model.finance.FinEODEvent;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.pff.extension.DPDExtension;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.core.util.SchdUtil;

public class DPDStringCalculator {
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
		List<FinEODEvent> finEODEvents = custEODEvent.getFinEODEvents();

		for (FinEODEvent finEODEvent : finEODEvents) {
			FinanceMain fm = finEODEvent.getFinanceMain();

			List<FinanceScheduleDetail> schedules = finEODEvent.getFinanceScheduleDetails();
			FinanceProfitDetail fpd = finEODEvent.getFinProfitDetail();

			String curDPDString = fpd.getCurDPDString();
			String dpdString = getDpdString(monthEnd, fm, schedules, curDPDString);
			fpd.setCurDPDString(deriveDPDString(curDPDString, dpdString));

		}

	}

	private static String deriveDPDString(String curDPDStr, String dpdString) {
		if (StringUtils.isEmpty(dpdString)) {
			return curDPDStr;
		}

		if (StringUtils.isEmpty(curDPDStr)) {
			return dpdString;
		}

		if (curDPDStr.length() == DPDExtension.DPD_STRING_LENGTH) {
			curDPDStr = curDPDStr.substring(1);
		}

		return curDPDStr.concat(dpdString);
	}

	public static String getDpdString(boolean monthEnd, FinanceMain fm, List<FinanceScheduleDetail> schedules,
			String curDPDStr) {
		int dueBucket = fm.getDueBucket();

		if (monthEnd) {
			return getDueBucket(dueBucket);
		}

		int frequencyDay = FrequencyUtil.getIntFrequencyDay(fm.getRepayFrq());
		Date nextBusinessDate = fm.getEventProperties().getBusinessDate();
		int dueDay = DateUtil.getDay(nextBusinessDate);
		int weekBusDay = getWeekDay(nextBusinessDate);
		String frqlable = FrequencyUtil.getRepayFrequencyLabel(fm.getRepayFrq());

		if (FrequencyUtil.WEEKLY.equals(frqlable)) {
			dueDay = weekBusDay;
		}

		if (LookupMethods.lookupFSD(schedules, nextBusinessDate, 0) > 0 || considerDPDForMatured(fm)) {
			frequencyDay = dueDay;
		}

		if (frequencyDay == dueDay && isNotFirstInstallment(schedules, nextBusinessDate)) {
			return getDueBucket(dueBucket);
		}

		return null;
	}

	private static boolean isNotFirstInstallment(List<FinanceScheduleDetail> schedules, Date nextBusinessDate) {
		return SchdUtil.getFirstInstalment(schedules).getSchDate().compareTo(nextBusinessDate) != 0;
	}

	private static boolean considerDPDForMatured(FinanceMain fm) {
		Date nextBusinessDate = fm.getEventProperties().getBusinessDate();

		if (fm.getMaturityDate().compareTo(nextBusinessDate) > 0) {
			return false;
		}

		int dueDay = FrequencyUtil.getIntFrequencyDay(fm.getRepayFrq());
		int businessDay = DateUtil.getDay(nextBusinessDate);

		String frqlable = FrequencyUtil.getRepayFrequencyLabel(fm.getRepayFrq());

		boolean isValid = false;

		switch (frqlable) {
		case FrequencyUtil.DAILY: {
			isValid = true;
			break;
		}
		case FrequencyUtil.WEEKLY: {
			if (dueDay == getWeekDay(nextBusinessDate)) {
				isValid = true;
			}
			break;
		}
		case FrequencyUtil.MONTHLY, FrequencyUtil.QUARTERLY, FrequencyUtil.HALF_YEARLY, FrequencyUtil.YEARLY: {
			if (dueDay == businessDay) {
				isValid = true;
			}
			break;
		}
		case FrequencyUtil.FORT_NIGHTLY: {
			if (businessDay == dueDay || businessDay == dueDay + 14) {
				isValid = true;
			}
			break;
		}
		default:
			break;
		}

		return isValid;
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
		return String.valueOf((char) --i);
	}

	public static int getWeekDay(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int day = cal.get(Calendar.DAY_OF_WEEK) - 1;
		if (day == 0) {
			return 07;
		}
		return day;
	}
}
