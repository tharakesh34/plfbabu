package com.pennant.pff.core.loan.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.resource.LabelsLoader;
import com.pennanttech.pennapps.core.util.DateUtil;

public class TestDPDStringCalculator {
	private static final String LABEL_RESOURCE_SUFFIX = "label.properties";

	@BeforeTest
	public void initilize() {
		App.CODE = "PFF";
		App.HOME_PATH = "D:\\Pennant\\CORE\\HDFC\\BASE";

		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		LabelsLoader laLoader = new LabelsLoader(true);

		String appCode = App.CODE.concat("-").toLowerCase();
		String i3Labels = appCode.concat(LABEL_RESOURCE_SUFFIX).toLowerCase();
		Resource[] resources = getResources(i3Labels);

		if (resources.length == 0) {
			resources = getResources(LABEL_RESOURCE_SUFFIX);
		}

		for (Resource resource : resources) {
			if (resource.getFilename().endsWith(LABEL_RESOURCE_SUFFIX)) {
				App.setLabels(laLoader.getProperties(classLoader.getResourceAsStream(resource.getFilename())));
			}
		}

		if (App.getLabels().isEmpty()) {
			App.setLabels(laLoader.getProperties(classLoader.getResourceAsStream("i3-label.properties")));
		}

		LabelsLoader.registerLabels();
	}

	@Test
	public void dueDateMonthly() {
		String curDPDStr = "111";
		FinanceMain fm = new FinanceMain();
		fm.setDueBucket(2);
		fm.setRepayFrq("M0005");
		fm.setMaturityDate(DateUtil.getDate(2021, 06, 05));
		fm.getEventProperties().setBusinessDate(DateUtil.getDate(2021, 02, 05));

		List<FinanceScheduleDetail> schedules = new ArrayList<>();

		schedules.add(getSchedule(DateUtil.getDate(2021, 02, 05)));
		schedules.add(getSchedule(DateUtil.getDate(2021, 03, 05)));
		schedules.add(getSchedule(DateUtil.getDate(2021, 04, 05)));
		schedules.add(getSchedule(DateUtil.getDate(2021, 05, 05)));
		schedules.add(getSchedule(DateUtil.getDate(2021, 06, 05)));

		Assert.assertEquals("2", DPDStringCalculator.getDpdString(false, fm, schedules, curDPDStr));
	}

	@Test
	public void dueDateMonthlyAfterClearingDueOnFirstInst() {
		String curDPDStr = null;
		FinanceMain fm = new FinanceMain();
		fm.setDueBucket(0);
		fm.setRepayFrq("M0005");
		fm.setMaturityDate(DateUtil.getDate(2021, 06, 05));
		fm.getEventProperties().setBusinessDate(DateUtil.getDate(2021, 02, 05));

		List<FinanceScheduleDetail> schedules = new ArrayList<>();

		schedules.add(getSchedule(DateUtil.getDate(2021, 02, 05)));
		schedules.add(getSchedule(DateUtil.getDate(2021, 03, 05)));
		schedules.add(getSchedule(DateUtil.getDate(2021, 04, 05)));
		schedules.add(getSchedule(DateUtil.getDate(2021, 05, 05)));
		schedules.add(getSchedule(DateUtil.getDate(2021, 06, 05)));

		Assert.assertEquals("0", DPDStringCalculator.getDpdString(false, fm, schedules, curDPDStr));
	}

	@Test
	public void dueDateMonthlyBeforeClearingDueOnFirstInst() {
		String curDPDStr = null;
		FinanceMain fm = new FinanceMain();
		fm.setDueBucket(0);
		fm.setRepayFrq("M0005");
		fm.setMaturityDate(DateUtil.getDate(2021, 06, 05));
		fm.getEventProperties().setBusinessDate(DateUtil.getDate(2021, 02, 01));

		List<FinanceScheduleDetail> schedules = new ArrayList<>();

		schedules.add(getSchedule(DateUtil.getDate(2021, 02, 05)));
		schedules.add(getSchedule(DateUtil.getDate(2021, 03, 05)));
		schedules.add(getSchedule(DateUtil.getDate(2021, 04, 05)));
		schedules.add(getSchedule(DateUtil.getDate(2021, 05, 05)));
		schedules.add(getSchedule(DateUtil.getDate(2021, 06, 05)));

		Assert.assertEquals(null, DPDStringCalculator.getDpdString(false, fm, schedules, curDPDStr));
	}

	@Test
	public void dueDateMonthlyAftrMaturity() {
		String curDPDStr = "11111";
		FinanceMain fm = new FinanceMain();
		fm.setDueBucket(2);
		fm.setRepayFrq("M0005");
		fm.getEventProperties().setBusinessDate(DateUtil.getDate(2021, 06, 05));
		fm.setMaturityDate(DateUtil.getDate(2021, 05, 05));
		List<FinanceScheduleDetail> schedules = new ArrayList<>();

		schedules.add(getSchedule(DateUtil.getDate(2021, 02, 05)));
		schedules.add(getSchedule(DateUtil.getDate(2021, 03, 05)));
		schedules.add(getSchedule(DateUtil.getDate(2021, 04, 05)));
		schedules.add(getSchedule(DateUtil.getDate(2021, 05, 05)));

		Assert.assertEquals("2", DPDStringCalculator.getDpdString(false, fm, schedules, curDPDStr));
	}

	@Test
	public void dueDateDaily() {
		String curDPDStr = "11111";
		FinanceMain fm = new FinanceMain();
		fm.setDueBucket(0);
		fm.setRepayFrq("D0000");
		fm.setMaturityDate(DateUtil.getDate(2018, 05, 19));
		fm.getEventProperties().setBusinessDate(DateUtil.getDate(2018, 05, 18));

		List<FinanceScheduleDetail> schedules = new ArrayList<>();

		schedules.add(getSchedule(DateUtil.getDate(2018, 05, 15)));
		schedules.add(getSchedule(DateUtil.getDate(2018, 05, 16)));
		schedules.add(getSchedule(DateUtil.getDate(2018, 05, 17)));
		schedules.add(getSchedule(DateUtil.getDate(2018, 05, 18)));
		schedules.add(getSchedule(DateUtil.getDate(2018, 05, 19)));

		Assert.assertEquals("0", DPDStringCalculator.getDpdString(false, fm, schedules, curDPDStr));
	}

	@Test
	public void dueDateDailyForFirstInst() {
		String curDPDStr = null;
		FinanceMain fm = new FinanceMain();
		fm.setDueBucket(1);
		fm.setRepayFrq("D0000");
		fm.setMaturityDate(DateUtil.getDate(2018, 05, 19));
		fm.getEventProperties().setBusinessDate(DateUtil.getDate(2018, 05, 16));

		List<FinanceScheduleDetail> schedules = new ArrayList<>();

		schedules.add(getSchedule(DateUtil.getDate(2018, 05, 15)));
		schedules.add(getSchedule(DateUtil.getDate(2018, 05, 16)));
		schedules.add(getSchedule(DateUtil.getDate(2018, 05, 17)));
		schedules.add(getSchedule(DateUtil.getDate(2018, 05, 18)));
		schedules.add(getSchedule(DateUtil.getDate(2018, 05, 19)));

		Assert.assertEquals("1", DPDStringCalculator.getDpdString(false, fm, schedules, curDPDStr));
	}

	@Test
	public void dueDateDailyAftrMaturity() {
		String curDPDStr = "11111";
		FinanceMain fm = new FinanceMain();
		fm.setDueBucket(1);
		fm.setRepayFrq("D0000");
		fm.setMaturityDate(DateUtil.getDate(2018, 05, 19));
		fm.getEventProperties().setBusinessDate(DateUtil.getDate(2018, 05, 20));

		List<FinanceScheduleDetail> schedules = new ArrayList<>();

		schedules.add(getSchedule(DateUtil.getDate(2018, 05, 15)));
		schedules.add(getSchedule(DateUtil.getDate(2018, 05, 16)));
		schedules.add(getSchedule(DateUtil.getDate(2018, 05, 17)));
		schedules.add(getSchedule(DateUtil.getDate(2018, 05, 18)));
		schedules.add(getSchedule(DateUtil.getDate(2018, 05, 19)));

		Assert.assertEquals("1", DPDStringCalculator.getDpdString(false, fm, schedules, curDPDStr));
	}

	@Test
	public void dueDateDailyDueCleared() {
		String curDPDStr = "11111";
		FinanceMain fm = new FinanceMain();
		fm.setDueBucket(0);
		fm.setRepayFrq("D0000");
		fm.setMaturityDate(DateUtil.getDate(2018, 05, 19));
		fm.getEventProperties().setBusinessDate(DateUtil.getDate(2018, 05, 18));

		List<FinanceScheduleDetail> schedules = new ArrayList<>();

		schedules.add(getSchedule(DateUtil.getDate(2018, 05, 15)));
		schedules.add(getSchedule(DateUtil.getDate(2018, 05, 16)));
		schedules.add(getSchedule(DateUtil.getDate(2018, 05, 17)));
		schedules.add(getSchedule(DateUtil.getDate(2018, 05, 18)));
		schedules.add(getSchedule(DateUtil.getDate(2018, 05, 19)));

		Assert.assertEquals("0", DPDStringCalculator.getDpdString(false, fm, schedules, curDPDStr));
	}

	@Test
	public void dueDateWeekly() {
		String curDPDStr = "111";
		FinanceMain fm = new FinanceMain();
		fm.setDueBucket(2);
		fm.setRepayFrq("W0001");
		fm.getEventProperties().setBusinessDate(DateUtil.getDate(2020, 06, 20));
		fm.setMaturityDate(DateUtil.getDate(2020, 07, 10));
		List<FinanceScheduleDetail> schedules = new ArrayList<>();

		schedules.add(getSchedule(DateUtil.getDate(2020, 06, 13)));
		schedules.add(getSchedule(DateUtil.getDate(2020, 06, 20)));
		schedules.add(getSchedule(DateUtil.getDate(2020, 06, 27)));
		schedules.add(getSchedule(DateUtil.getDate(2020, 07, 03)));
		schedules.add(getSchedule(DateUtil.getDate(2020, 07, 10)));

		Assert.assertEquals("2", DPDStringCalculator.getDpdString(false, fm, schedules, curDPDStr));
	}

	@Test
	public void dueDateWeeklyAftrMaturity() {
		String curDPDStr = "1234";
		FinanceMain fm = new FinanceMain();
		fm.setDueBucket(2);
		fm.setRepayFrq("W0005");
		fm.getEventProperties().setBusinessDate(DateUtil.getDate(2018, 05, 01));
		fm.setMaturityDate(DateUtil.getDate(2018, 04, 18));
		List<FinanceScheduleDetail> schedules = new ArrayList<>();

		schedules.add(getSchedule(DateUtil.getDate(2018, 03, 06)));
		schedules.add(getSchedule(DateUtil.getDate(2018, 03, 13)));
		schedules.add(getSchedule(DateUtil.getDate(2018, 03, 20)));
		schedules.add(getSchedule(DateUtil.getDate(2018, 04, 04)));
		schedules.add(getSchedule(DateUtil.getDate(2018, 04, 11)));
		schedules.add(getSchedule(DateUtil.getDate(2018, 04, 18)));

		Assert.assertEquals("2", DPDStringCalculator.getDpdString(false, fm, schedules, curDPDStr));
	}

	@Test
	public void dueDateForeNightlyAftrMaturity() {
		String curDPDStr = "11111";
		FinanceMain fm = new FinanceMain();
		fm.setDueBucket(2);
		fm.setRepayFrq("F0001");
		fm.getEventProperties().setBusinessDate(DateUtil.getDate(2020, 06, 12));
		fm.setMaturityDate(DateUtil.getDate(2020, 03, 01));
		List<FinanceScheduleDetail> schedules = new ArrayList<>();

		schedules.add(getSchedule(DateUtil.getDate(2020, 01, 01)));
		schedules.add(getSchedule(DateUtil.getDate(2020, 01, 15)));
		schedules.add(getSchedule(DateUtil.getDate(2020, 02, 01)));
		schedules.add(getSchedule(DateUtil.getDate(2020, 02, 15)));
		schedules.add(getSchedule(DateUtil.getDate(2020, 03, 01)));

		Assert.assertEquals("2", DPDStringCalculator.getDpdString(false, fm, schedules, curDPDStr));
	}

	private FinanceScheduleDetail getSchedule(Date schdDate) {
		FinanceScheduleDetail fsd = new FinanceScheduleDetail();
		fsd.setSchDate(schdDate);
		return fsd;
	}

	private Resource[] getResources(String labelResourcePrefix) {
		ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		try {
			return resolver.getResources("classpath*:*".concat(labelResourcePrefix));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
