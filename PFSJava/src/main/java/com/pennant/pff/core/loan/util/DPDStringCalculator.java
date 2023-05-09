package com.pennant.pff.core.loan.util;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.pennant.app.core.CustEODEvent;
import com.pennant.app.core.FinEODEvent;
import com.pennant.app.util.FrequencyUtil;
import com.pennant.app.util.LookupMethods;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.pff.extension.DPDExtension;
import com.pennanttech.pennapps.core.util.DateUtil;

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
		if (StringUtils.isEmpty(dpdString) && StringUtils.isEmpty(curDPDStr)) {
			return curDPDStr;
		}

		if (StringUtils.isNotEmpty(curDPDStr) && curDPDStr.length() == DPDExtension.DPD_STRING_LENGTH) {
			curDPDStr = curDPDStr.substring(1);
		}

		if (StringUtils.isEmpty(curDPDStr)) {
			return dpdString;
		}

		return curDPDStr.concat(dpdString);
	}

	private static String getDpdString(boolean monthEnd, FinanceMain fm, List<FinanceScheduleDetail> schedules,
			String curDPDStr) {
		int dueBucket = fm.getDueBucket();

		if (monthEnd) {
			if (dueBucket == 0 && StringUtils.isNotEmpty(curDPDStr)) {
				return null;
			}
			return getDueBucket(dueBucket);
		}

		int frequencyDay = FrequencyUtil.getIntFrequencyDay(fm.getRepayFrq());
		Date nextBusinessDate = fm.getEventProperties().getBusinessDate();
		int dueDay = DateUtil.getDay(nextBusinessDate);

		if (LookupMethods.lookupFSD(schedules, nextBusinessDate, 0) > 0) {
			frequencyDay = dueDay;
		}

		if (frequencyDay == dueDay) {
			if (dueBucket == 0 && StringUtils.isNotEmpty(curDPDStr)) {
				return null;
			}

			return getDueBucket(dueBucket);
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
