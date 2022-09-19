package com.pennanttech.pff.organization;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennanttech.pennapps.core.util.DateUtil;

public class OrganizationUtil {
	private static List<ValueLabel> schoolClassNames;
	private static List<ValueLabel> finacialYears;
	private static List<ValueLabel> collectionFrequenctList;

	public static List<ValueLabel> getSchoolClassName() {
		if (schoolClassNames == null) {
			schoolClassNames = new ArrayList<>();
			synchronized (schoolClassNames) {
				schoolClassNames.add(new ValueLabel("LKG", "LKG"));
				schoolClassNames.add(new ValueLabel("UKG", "UKG"));
				schoolClassNames.add(new ValueLabel("FIRST", "FIRST"));
				schoolClassNames.add(new ValueLabel("SECOND", "SECOND"));
				schoolClassNames.add(new ValueLabel("THIRD", "THIRD"));
				schoolClassNames.add(new ValueLabel("FOURTH", "FOURTH"));
				schoolClassNames.add(new ValueLabel("FIVTH", "FIVTH"));
				schoolClassNames.add(new ValueLabel("SIXTH", "SIXTH"));
				schoolClassNames.add(new ValueLabel("SEVENTH", "SEVENTH"));
				schoolClassNames.add(new ValueLabel("EIGHTH", "EIGHTH"));
				schoolClassNames.add(new ValueLabel("NINETH", "NINETH"));
				schoolClassNames.add(new ValueLabel("TENTH", "TENTH"));
			}
		}
		return schoolClassNames;
	}

	public static List<ValueLabel> getFinancialYears() {
		Date date = SysParamUtil.getAppDate();

		if (DateUtil.getMonth(date) == Calendar.MARCH + 1) {
			finacialYears = null;
		}

		if (finacialYears == null) {
			if (DateUtil.getMonth(date) >= Calendar.MARCH + 1) {
				date = DateUtil.addMonths(date, 12);
			}

			finacialYears = new ArrayList<>();
			synchronized (finacialYears) {
				String financialYearStart = null;
				String financialYearEnd = null;
				for (int i = 0; i < 10; i++) {
					financialYearStart = DateUtil.format(date, "yyyy");
					financialYearEnd = DateUtil.format(DateUtil.addMonths(date, -12), "yyyy");
					finacialYears.add(new ValueLabel(financialYearStart,
							financialYearEnd.concat("-".concat(financialYearStart))));
					date = DateUtil.addMonths(date, -12);
				}
			}
		}
		return finacialYears;
	}

	public static List<ValueLabel> getCollectionFrequencyList() {
		if (collectionFrequenctList == null) {
			collectionFrequenctList = new ArrayList<>(4);
			synchronized (collectionFrequenctList) {
				collectionFrequenctList.add(new ValueLabel("1", "Yearly"));
				collectionFrequenctList.add(new ValueLabel("2", "Half-Yearly"));
				collectionFrequenctList.add(new ValueLabel("4", "Quarterly"));
				collectionFrequenctList.add(new ValueLabel("12", "Monthly"));
			}
		}
		return collectionFrequenctList;
	}

}
