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
		String curDPDStr = null;
		FinanceMain fm = new FinanceMain();
		fm.setDueBucket(2);
		fm.setRepayFrq("M0005");
		fm.setMaturityDate(DateUtil.getDate(2021, 06, 05));
		fm.getEventProperties().setBusinessDate(DateUtil.getDate(2021, 04, 05));

		List<FinanceScheduleDetail> schedules = new ArrayList<>();

		schedules.add(getScheduleWithRepayFalse(DateUtil.getDate(2021, 02, 05)));// disb Date
		schedules.add(getSchedule(DateUtil.getDate(2021, 03, 05)));// 1st Inst
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
		fm.getEventProperties().setBusinessDate(DateUtil.getDate(2021, 04, 05));

		List<FinanceScheduleDetail> schedules = new ArrayList<>();

		schedules.add(getScheduleWithRepayFalse(DateUtil.getDate(2021, 02, 05)));// disb Date
		schedules.add(getSchedule(DateUtil.getDate(2021, 03, 05)));// 1st Inst
		schedules.add(getSchedule(DateUtil.getDate(2021, 04, 05)));
		schedules.add(getSchedule(DateUtil.getDate(2021, 05, 05)));
		schedules.add(getSchedule(DateUtil.getDate(2021, 06, 05)));

		Assert.assertEquals("0", DPDStringCalculator.getDpdString(false, fm, schedules, curDPDStr));
	}

	@Test
	public void dueDateMonthlyAfter1stInstDueCleared() {
		String curDPDStr = null;
		FinanceMain fm = new FinanceMain();
		fm.setDueBucket(0);
		fm.setRepayFrq("M0005");
		fm.setMaturityDate(DateUtil.getDate(2021, 06, 05));
		fm.getEventProperties().setBusinessDate(DateUtil.getDate(2021, 03, 05));

		List<FinanceScheduleDetail> schedules = new ArrayList<>();

		schedules.add(getScheduleWithRepayFalse(DateUtil.getDate(2021, 02, 05)));// disb Date
		schedules.add(getSchedule(DateUtil.getDate(2021, 03, 05)));// 1st Inst
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

		schedules.add(getScheduleWithRepayFalse(DateUtil.getDate(2021, 02, 05)));// disb Date
		schedules.add(getSchedule(DateUtil.getDate(2021, 03, 05)));// 1st Inst
		schedules.add(getSchedule(DateUtil.getDate(2021, 04, 05)));
		schedules.add(getSchedule(DateUtil.getDate(2021, 05, 05)));

		Assert.assertEquals("2", DPDStringCalculator.getDpdString(false, fm, schedules, curDPDStr));
	}

	@Test
	public void dueDateMonthlyNotDueDate() {
		String curDPDStr = "11111";
		FinanceMain fm = new FinanceMain();
		fm.setDueBucket(2);
		fm.setRepayFrq("M0005");
		fm.getEventProperties().setBusinessDate(DateUtil.getDate(2021, 04, 07));
		fm.setMaturityDate(DateUtil.getDate(2021, 05, 05));
		List<FinanceScheduleDetail> schedules = new ArrayList<>();

		schedules.add(getScheduleWithRepayFalse(DateUtil.getDate(2021, 02, 05)));// disb Date
		schedules.add(getSchedule(DateUtil.getDate(2021, 03, 05)));// 1st Inst
		schedules.add(getSchedule(DateUtil.getDate(2021, 04, 05)));
		schedules.add(getSchedule(DateUtil.getDate(2021, 05, 05)));

		Assert.assertEquals(null, DPDStringCalculator.getDpdString(false, fm, schedules, curDPDStr));
	}

	@Test
	public void dueDateDaily() {
		String curDPDStr = null;
		FinanceMain fm = new FinanceMain();
		fm.setDueBucket(0);
		fm.setRepayFrq("D0000");
		fm.setMaturityDate(DateUtil.getDate(2018, 05, 19));
		fm.getEventProperties().setBusinessDate(DateUtil.getDate(2018, 05, 17));

		List<FinanceScheduleDetail> schedules = new ArrayList<>();

		schedules.add(getScheduleWithRepayFalse(DateUtil.getDate(2018, 05, 15)));// disb Date
		schedules.add(getSchedule(DateUtil.getDate(2018, 05, 16)));// 1st Inst
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

		schedules.add(getScheduleWithRepayFalse(DateUtil.getDate(2018, 05, 15)));// disb Date
		schedules.add(getSchedule(DateUtil.getDate(2018, 05, 16)));// 1st Inst
		schedules.add(getSchedule(DateUtil.getDate(2018, 05, 17)));
		schedules.add(getSchedule(DateUtil.getDate(2018, 05, 18)));
		schedules.add(getSchedule(DateUtil.getDate(2018, 05, 19)));

		Assert.assertEquals(null, DPDStringCalculator.getDpdString(false, fm, schedules, curDPDStr));
	}

	@Test
	public void dueDateDailyAfter1stInstDueCleared() {
		String curDPDStr = null;
		FinanceMain fm = new FinanceMain();
		fm.setDueBucket(0);
		fm.setRepayFrq("D0000");
		fm.setMaturityDate(DateUtil.getDate(2018, 05, 19));
		fm.getEventProperties().setBusinessDate(DateUtil.getDate(2018, 05, 17));

		List<FinanceScheduleDetail> schedules = new ArrayList<>();

		schedules.add(getScheduleWithRepayFalse(DateUtil.getDate(2018, 05, 15)));// disb Date
		schedules.add(getSchedule(DateUtil.getDate(2018, 05, 16)));// 1st Inst
		schedules.add(getSchedule(DateUtil.getDate(2018, 05, 17)));
		schedules.add(getSchedule(DateUtil.getDate(2018, 05, 18)));
		schedules.add(getSchedule(DateUtil.getDate(2018, 05, 19)));

		Assert.assertEquals("0", DPDStringCalculator.getDpdString(false, fm, schedules, curDPDStr));
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

		schedules.add(getScheduleWithRepayFalse(DateUtil.getDate(2018, 05, 15)));// disb Date
		schedules.add(getSchedule(DateUtil.getDate(2018, 05, 16)));// 1st Inst
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
		fm.getEventProperties().setBusinessDate(DateUtil.getDate(2018, 05, 17));

		List<FinanceScheduleDetail> schedules = new ArrayList<>();

		schedules.add(getScheduleWithRepayFalse(DateUtil.getDate(2018, 05, 15)));// disb Date
		schedules.add(getSchedule(DateUtil.getDate(2018, 05, 16)));// 1st Inst
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
		fm.getEventProperties().setBusinessDate(DateUtil.getDate(2020, 06, 27));
		fm.setMaturityDate(DateUtil.getDate(2020, 07, 10));
		List<FinanceScheduleDetail> schedules = new ArrayList<>();

		schedules.add(getScheduleWithRepayFalse(DateUtil.getDate(2020, 06, 13)));// disb Date
		schedules.add(getSchedule(DateUtil.getDate(2020, 06, 20)));// 1st Inst
		schedules.add(getSchedule(DateUtil.getDate(2020, 06, 27)));
		schedules.add(getSchedule(DateUtil.getDate(2020, 07, 03)));
		schedules.add(getSchedule(DateUtil.getDate(2020, 07, 10)));

		Assert.assertEquals("2", DPDStringCalculator.getDpdString(false, fm, schedules, curDPDStr));
	}

	@Test
	public void dueDateWeeklyForFirstInst() {
		String curDPDStr = null;
		FinanceMain fm = new FinanceMain();
		fm.setDueBucket(2);
		fm.setRepayFrq("W0001");
		fm.getEventProperties().setBusinessDate(DateUtil.getDate(2020, 06, 20));
		fm.setMaturityDate(DateUtil.getDate(2020, 07, 10));
		List<FinanceScheduleDetail> schedules = new ArrayList<>();

		schedules.add(getScheduleWithRepayFalse(DateUtil.getDate(2020, 06, 13)));// disb Date
		schedules.add(getSchedule(DateUtil.getDate(2020, 06, 20)));// 1st Inst
		schedules.add(getSchedule(DateUtil.getDate(2020, 06, 27)));
		schedules.add(getSchedule(DateUtil.getDate(2020, 07, 03)));
		schedules.add(getSchedule(DateUtil.getDate(2020, 07, 10)));

		Assert.assertEquals(null, DPDStringCalculator.getDpdString(false, fm, schedules, curDPDStr));
	}

	@Test
	public void dueDateWeeklyAfter1stInstDueCleared() {
		String curDPDStr = null;
		FinanceMain fm = new FinanceMain();
		fm.setDueBucket(0);
		fm.setRepayFrq("W0001");
		fm.getEventProperties().setBusinessDate(DateUtil.getDate(2020, 06, 27));
		fm.setMaturityDate(DateUtil.getDate(2020, 07, 10));
		List<FinanceScheduleDetail> schedules = new ArrayList<>();

		schedules.add(getScheduleWithRepayFalse(DateUtil.getDate(2020, 06, 13)));// disb Date
		schedules.add(getSchedule(DateUtil.getDate(2020, 06, 20)));// 1st Inst
		schedules.add(getSchedule(DateUtil.getDate(2020, 06, 27)));
		schedules.add(getSchedule(DateUtil.getDate(2020, 07, 03)));
		schedules.add(getSchedule(DateUtil.getDate(2020, 07, 10)));

		Assert.assertEquals("0", DPDStringCalculator.getDpdString(false, fm, schedules, curDPDStr));
	}

	@Test
	public void dueDateWeeklyAftrMaturity() {
		String curDPDStr = "1234";
		FinanceMain fm = new FinanceMain();
		fm.setDueBucket(2);
		fm.setRepayFrq("W0005");
		fm.getEventProperties().setBusinessDate(DateUtil.getDate(2018, 04, 25));
		fm.setMaturityDate(DateUtil.getDate(2018, 04, 18));
		List<FinanceScheduleDetail> schedules = new ArrayList<>();

		schedules.add(getScheduleWithRepayFalse(DateUtil.getDate(2018, 03, 06)));// disb Date
		schedules.add(getSchedule(DateUtil.getDate(2018, 03, 13)));// 1st Inst
		schedules.add(getSchedule(DateUtil.getDate(2018, 03, 20)));
		schedules.add(getSchedule(DateUtil.getDate(2018, 04, 04)));
		schedules.add(getSchedule(DateUtil.getDate(2018, 04, 11)));
		schedules.add(getSchedule(DateUtil.getDate(2018, 04, 18)));

		Assert.assertEquals("2", DPDStringCalculator.getDpdString(false, fm, schedules, curDPDStr));
	}

	@Test
	public void dueDateWeeklyNonDueDay() {
		String curDPDStr = "1234";
		FinanceMain fm = new FinanceMain();
		fm.setDueBucket(2);
		fm.setRepayFrq("W0005");
		fm.getEventProperties().setBusinessDate(DateUtil.getDate(2018, 04, 13));
		fm.setMaturityDate(DateUtil.getDate(2018, 04, 18));
		List<FinanceScheduleDetail> schedules = new ArrayList<>();

		schedules.add(getScheduleWithRepayFalse(DateUtil.getDate(2018, 03, 06)));// disb Date
		schedules.add(getSchedule(DateUtil.getDate(2018, 03, 13)));// 1st Inst
		schedules.add(getSchedule(DateUtil.getDate(2018, 03, 20)));
		schedules.add(getSchedule(DateUtil.getDate(2018, 04, 04)));
		schedules.add(getSchedule(DateUtil.getDate(2018, 04, 11)));
		schedules.add(getSchedule(DateUtil.getDate(2018, 04, 18)));

		Assert.assertEquals(null, DPDStringCalculator.getDpdString(false, fm, schedules, curDPDStr));
	}

	@Test
	public void dueDateForeNightly() {
		String curDPDStr = null;
		FinanceMain fm = new FinanceMain();
		fm.setDueBucket(1);
		fm.setRepayFrq("F0001");
		fm.getEventProperties().setBusinessDate(DateUtil.getDate(2020, 02, 01));
		fm.setMaturityDate(DateUtil.getDate(2020, 03, 01));
		List<FinanceScheduleDetail> schedules = new ArrayList<>();

		schedules.add(getScheduleWithRepayFalse(DateUtil.getDate(2020, 01, 01)));// disb Date
		schedules.add(getSchedule(DateUtil.getDate(2020, 01, 15)));// 1st Inst
		schedules.add(getSchedule(DateUtil.getDate(2020, 02, 01)));
		schedules.add(getSchedule(DateUtil.getDate(2020, 02, 15)));
		schedules.add(getSchedule(DateUtil.getDate(2020, 03, 01)));

		Assert.assertEquals("1", DPDStringCalculator.getDpdString(false, fm, schedules, curDPDStr));
	}

	@Test
	public void dueDateForeNightlyForFirstInst() {
		String curDPDStr = null;
		FinanceMain fm = new FinanceMain();
		fm.setDueBucket(1);
		fm.setRepayFrq("F0001");
		fm.getEventProperties().setBusinessDate(DateUtil.getDate(2020, 01, 15));
		fm.setMaturityDate(DateUtil.getDate(2020, 03, 01));
		List<FinanceScheduleDetail> schedules = new ArrayList<>();

		schedules.add(getScheduleWithRepayFalse(DateUtil.getDate(2020, 01, 01)));// disb Date
		schedules.add(getSchedule(DateUtil.getDate(2020, 01, 15)));// 1st Inst
		schedules.add(getSchedule(DateUtil.getDate(2020, 02, 01)));
		schedules.add(getSchedule(DateUtil.getDate(2020, 02, 15)));
		schedules.add(getSchedule(DateUtil.getDate(2020, 03, 01)));

		Assert.assertEquals(null, DPDStringCalculator.getDpdString(false, fm, schedules, curDPDStr));
	}

	@Test
	public void dueDateForeNightlyAfter1stInstDueCleared() {
		String curDPDStr = null;
		FinanceMain fm = new FinanceMain();
		fm.setDueBucket(0);
		fm.setRepayFrq("F0001");
		fm.getEventProperties().setBusinessDate(DateUtil.getDate(2020, 02, 01));
		fm.setMaturityDate(DateUtil.getDate(2020, 03, 01));
		List<FinanceScheduleDetail> schedules = new ArrayList<>();

		schedules.add(getScheduleWithRepayFalse(DateUtil.getDate(2020, 01, 01)));// disb Date
		schedules.add(getSchedule(DateUtil.getDate(2020, 01, 15)));// 1st Inst
		schedules.add(getSchedule(DateUtil.getDate(2020, 02, 01)));
		schedules.add(getSchedule(DateUtil.getDate(2020, 02, 15)));
		schedules.add(getSchedule(DateUtil.getDate(2020, 03, 01)));

		Assert.assertEquals("0", DPDStringCalculator.getDpdString(false, fm, schedules, curDPDStr));
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

		schedules.add(getScheduleWithRepayFalse(DateUtil.getDate(2020, 01, 01)));// disb Date
		schedules.add(getSchedule(DateUtil.getDate(2020, 01, 15)));// 1st Inst
		schedules.add(getSchedule(DateUtil.getDate(2020, 02, 01)));
		schedules.add(getSchedule(DateUtil.getDate(2020, 02, 15)));
		schedules.add(getSchedule(DateUtil.getDate(2020, 03, 01)));

		Assert.assertEquals(null, DPDStringCalculator.getDpdString(false, fm, schedules, curDPDStr));
	}

	@Test
	public void dueDateForeNightlyNonDueDate() {
		String curDPDStr = "11111";
		FinanceMain fm = new FinanceMain();
		fm.setDueBucket(2);
		fm.setRepayFrq("F0001");
		fm.getEventProperties().setBusinessDate(DateUtil.getDate(2020, 02, 17));
		fm.setMaturityDate(DateUtil.getDate(2020, 03, 01));
		List<FinanceScheduleDetail> schedules = new ArrayList<>();

		schedules.add(getScheduleWithRepayFalse(DateUtil.getDate(2020, 01, 01)));// disb Date
		schedules.add(getSchedule(DateUtil.getDate(2020, 01, 15)));// 1st Inst
		schedules.add(getSchedule(DateUtil.getDate(2020, 02, 01)));
		schedules.add(getSchedule(DateUtil.getDate(2020, 02, 15)));
		schedules.add(getSchedule(DateUtil.getDate(2020, 03, 01)));

		Assert.assertEquals(null, DPDStringCalculator.getDpdString(false, fm, schedules, curDPDStr));
	}

	@Test
	public void monthlyDaily() {
		String curDPDStr = null;
		FinanceMain fm = new FinanceMain();
		fm.setDueBucket(1);
		fm.setRepayFrq("D0000");
		fm.getEventProperties().setBusinessDate(DateUtil.getDate(2020, 02, 01));
		fm.setMaturityDate(DateUtil.getDate(2020, 03, 01));
		List<FinanceScheduleDetail> schedules = new ArrayList<>();

		schedules.add(getScheduleWithRepayFalse(DateUtil.getDate(2020, 01, 25)));// disb Date
		schedules.add(getSchedule(DateUtil.getDate(2020, 01, 26)));// 1st Inst
		schedules.add(getSchedule(DateUtil.getDate(2020, 01, 27)));
		schedules.add(getSchedule(DateUtil.getDate(2020, 01, 28)));
		schedules.add(getSchedule(DateUtil.getDate(2020, 01, 29)));

		Assert.assertEquals(null, DPDStringCalculator.getDpdString(false, fm, schedules, curDPDStr));
	}

	@Test
	public void monthlyMonthly() {
		String curDPDStr = null;
		FinanceMain fm = new FinanceMain();
		fm.setDueBucket(2);
		fm.setRepayFrq("M0005");
		fm.setMaturityDate(DateUtil.getDate(2021, 06, 05));
		fm.getEventProperties().setBusinessDate(DateUtil.getDate(2021, 02, 01));

		List<FinanceScheduleDetail> schedules = new ArrayList<>();

		schedules.add(getScheduleWithRepayFalse(DateUtil.getDate(2021, 02, 05)));// disb Date
		schedules.add(getSchedule(DateUtil.getDate(2021, 03, 05)));// 1st Inst
		schedules.add(getSchedule(DateUtil.getDate(2021, 04, 05)));
		schedules.add(getSchedule(DateUtil.getDate(2021, 05, 05)));
		schedules.add(getSchedule(DateUtil.getDate(2021, 06, 05)));

		Assert.assertEquals("2", DPDStringCalculator.getDpdString(false, fm, schedules, curDPDStr));
	}

	private FinanceScheduleDetail getSchedule(Date schdDate) {
		FinanceScheduleDetail fsd = new FinanceScheduleDetail();
		fsd.setSchDate(schdDate);
		fsd.setRepayOnSchDate(true);
		return fsd;
	}

	private FinanceScheduleDetail getScheduleWithRepayFalse(Date schdDate) {
		FinanceScheduleDetail fsd = new FinanceScheduleDetail();
		fsd.setSchDate(schdDate);
		fsd.setRepayOnSchDate(false);
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
