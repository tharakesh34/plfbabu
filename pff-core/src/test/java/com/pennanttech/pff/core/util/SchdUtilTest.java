package com.pennanttech.pff.core.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.schdule.RepaymentStatus;

public class SchdUtilTest {
	@Test
	public void testGetOverDueEMI1() {

		List<FinanceScheduleDetail> schedules = new ArrayList<>();

		FinanceScheduleDetail schd = new FinanceScheduleDetail();
		schd.setSchDate(DateUtil.getDate(2023, 0, 05));
		schd.setRepayOnSchDate(false);
		schedules.add(schd);

		schd = new FinanceScheduleDetail();
		schd.setSchDate(DateUtil.getDate(2023, 0, 05));
		schd.setRepayOnSchDate(true);
		schd.setProfitSchd(new BigDecimal("100"));
		schd.setPrincipalSchd(new BigDecimal("900"));
		schedules.add(schd);

		schd = new FinanceScheduleDetail();
		schd.setSchDate(DateUtil.getDate(2023, 1, 05));
		schd.setRepayOnSchDate(true);
		schd.setProfitSchd(new BigDecimal("100"));
		schd.setPrincipalSchd(new BigDecimal("900"));
		schedules.add(schd);

		BigDecimal overDueEMI = SchdUtil.getOverDueEMI(DateUtil.getDate(2023, 1, 06), schedules);

		Assert.assertEquals(overDueEMI, new BigDecimal("2000"));

	}

	@Test
	public void testGetOverDueEMI2() {
		List<FinanceScheduleDetail> schedules = new ArrayList<>();

		FinanceScheduleDetail schd = new FinanceScheduleDetail();
		schd.setSchDate(DateUtil.getDate(2023, 0, 05));
		schd.setRepayOnSchDate(true);
		schd.setProfitSchd(new BigDecimal("100"));
		schd.setPrincipalSchd(new BigDecimal("900"));
		schd.setSchdPftPaid(new BigDecimal("100"));
		schd.setSchdPriPaid(new BigDecimal("900"));
		schedules.add(schd);

		schd = new FinanceScheduleDetail();
		schd.setSchDate(DateUtil.getDate(2023, 1, 05));
		schd.setRepayOnSchDate(true);
		schd.setProfitSchd(new BigDecimal("100"));
		schd.setPrincipalSchd(new BigDecimal("900"));
		schedules.add(schd);

		BigDecimal overDueEMI = SchdUtil.getOverDueEMI(DateUtil.getDate(2023, 1, 06), schedules);

		Assert.assertEquals(overDueEMI, new BigDecimal("1000"));
	}

	@Test
	public void testGetOverDueEMI3() {
		List<FinanceScheduleDetail> schedules = new ArrayList<>();

		FinanceScheduleDetail schd = new FinanceScheduleDetail();
		schd.setSchDate(DateUtil.getDate(2023, 0, 05));
		schd.setRepayOnSchDate(true);
		schd.setProfitSchd(new BigDecimal("100"));
		schd.setPrincipalSchd(new BigDecimal("900"));
		schd.setSchdPftPaid(new BigDecimal("100"));
		schd.setSchdPriPaid(new BigDecimal("900"));
		schedules.add(schd);

		schd = new FinanceScheduleDetail();
		schd.setSchDate(DateUtil.getDate(2023, 1, 05));
		schd.setRepayOnSchDate(true);
		schd.setProfitSchd(new BigDecimal("100"));
		schd.setPrincipalSchd(new BigDecimal("900"));
		schedules.add(schd);

		BigDecimal overDueEMI = SchdUtil.getOverDueEMI(DateUtil.getDate(2022, 1, 06), schedules);

		Assert.assertEquals(overDueEMI, new BigDecimal("0"));
	}

	@Test
	public void testGetOverDueEMI4() {
		List<FinanceScheduleDetail> schedules = new ArrayList<>();

		FinanceScheduleDetail schd = new FinanceScheduleDetail();
		schd.setSchDate(DateUtil.getDate(2023, 0, 05));
		schd.setRepayOnSchDate(true);
		schd.setProfitSchd(new BigDecimal("100"));
		schd.setPrincipalSchd(new BigDecimal("900"));
		schd.setSchdPftPaid(new BigDecimal("100"));
		schd.setSchdPriPaid(new BigDecimal("900"));
		schedules.add(schd);

		schd = new FinanceScheduleDetail();
		schd.setSchDate(DateUtil.getDate(2023, 1, 05));
		schd.setRepayOnSchDate(true);
		schd.setProfitSchd(new BigDecimal("100"));
		schd.setPrincipalSchd(new BigDecimal("900"));
		schd.setSchdPftPaid(new BigDecimal("100"));
		schd.setSchdPriPaid(new BigDecimal("950"));
		schedules.add(schd);

		BigDecimal overDueEMI = SchdUtil.getOverDueEMI(DateUtil.getDate(2023, 1, 06), schedules);

		Assert.assertEquals(overDueEMI, new BigDecimal("0"));
	}

	@Test
	public void testGetFutureInstalments1() {
		List<FinanceScheduleDetail> schedules = new ArrayList<>();

		FinanceScheduleDetail schd = new FinanceScheduleDetail();
		schd.setSchDate(DateUtil.getDate(2023, 0, 05));
		schd.setRepayOnSchDate(true);

		schedules.add(schd);

		schd = new FinanceScheduleDetail();
		schd.setSchDate(DateUtil.getDate(2023, 1, 05));
		schd.setRepayOnSchDate(true);

		schedules.add(schd);

		int futureInstalments = SchdUtil.getFutureInstalments(DateUtil.getDate(2023, 1, 06), schedules);

		Assert.assertEquals(futureInstalments, 0);
	}

	@Test
	public void testGetFutureInstalments2() {
		List<FinanceScheduleDetail> schedules = new ArrayList<>();

		FinanceScheduleDetail schd = new FinanceScheduleDetail();
		schd.setSchDate(DateUtil.getDate(2023, 0, 05));
		schd.setRepayOnSchDate(true);

		schedules.add(schd);

		schd = new FinanceScheduleDetail();
		schd.setSchDate(DateUtil.getDate(2023, 1, 05));
		schd.setRepayOnSchDate(true);

		schedules.add(schd);

		int futureInstalments = SchdUtil.getFutureInstalments(DateUtil.getDate(2023, 0, 06), schedules);

		Assert.assertEquals(futureInstalments, 1);
	}

	@Test
	public void testGetFutureInstalments3() {
		List<FinanceScheduleDetail> schedules = new ArrayList<>();

		FinanceScheduleDetail schd = new FinanceScheduleDetail();
		schd.setSchDate(DateUtil.getDate(2023, 0, 05));
		schd.setRepayOnSchDate(true);

		schedules.add(schd);

		schd = new FinanceScheduleDetail();
		schd.setSchDate(DateUtil.getDate(2023, 1, 05));
		schd.setRepayOnSchDate(true);

		schedules.add(schd);

		int futureInstalments = SchdUtil.getFutureInstalments(DateUtil.getDate(2022, 0, 06), schedules);

		Assert.assertEquals(futureInstalments, 2);
	}

	@Test
	public void testGetFutureInstalments4() {
		List<FinanceScheduleDetail> schedules = new ArrayList<>();

		FinanceScheduleDetail schd = new FinanceScheduleDetail();
		schd.setSchDate(DateUtil.getDate(2023, 0, 05));
		schd.setRepayOnSchDate(false);

		schedules.add(schd);

		schd = new FinanceScheduleDetail();
		schd.setSchDate(DateUtil.getDate(2023, 1, 05));
		schd.setRepayOnSchDate(false);

		schedules.add(schd);

		int futureInstalments = SchdUtil.getFutureInstalments(DateUtil.getDate(2023, 0, 06), schedules);

		Assert.assertEquals(futureInstalments, 0);
	}

	@Test
	public void testGetNextInstalment1() {
		List<FinanceScheduleDetail> schedules = new ArrayList<>();

		FinanceScheduleDetail schd = new FinanceScheduleDetail();
		schd.setSchDate(DateUtil.getDate(2023, 0, 05));
		schd.setRepayOnSchDate(true);

		schedules.add(schd);

		schd = new FinanceScheduleDetail();
		schd.setSchDate(DateUtil.getDate(2023, 1, 05));
		schd.setRepayOnSchDate(true);

		schedules.add(schd);

		FinanceScheduleDetail nextInstalment = SchdUtil.getNextInstalment(DateUtil.getDate(2023, 0, 04), schedules);

		Assert.assertEquals(nextInstalment, schedules.get(0));
	}

	@Test
	public void testGetNextInstalment2() {
		List<FinanceScheduleDetail> schedules = new ArrayList<>();

		FinanceScheduleDetail schd = new FinanceScheduleDetail();
		schd.setSchDate(DateUtil.getDate(2023, 0, 05));
		schd.setRepayOnSchDate(false);

		schedules.add(schd);

		schd = new FinanceScheduleDetail();
		schd.setSchDate(DateUtil.getDate(2023, 1, 05));
		schd.setRepayOnSchDate(false);

		schedules.add(schd);

		FinanceScheduleDetail nextInstalment = SchdUtil.getNextInstalment(DateUtil.getDate(2023, 0, 04), schedules);

		Assert.assertEquals(nextInstalment, null);
	}

	@Test
	public void testGetNextInstalment3() {
		List<FinanceScheduleDetail> schedules = new ArrayList<>();

		FinanceScheduleDetail schd = new FinanceScheduleDetail();
		schd.setSchDate(DateUtil.getDate(2023, 0, 05));
		schd.setRepayOnSchDate(true);

		schedules.add(schd);

		schd = new FinanceScheduleDetail();
		schd.setSchDate(DateUtil.getDate(2023, 1, 05));
		schd.setRepayOnSchDate(true);

		schedules.add(schd);

		FinanceScheduleDetail nextInstalment = SchdUtil.getNextInstalment(DateUtil.getDate(2023, 1, 04), schedules);

		Assert.assertEquals(nextInstalment, schedules.get(1));
	}

	@Test
	public void testGetTotalPriSchd1() {
		List<FinanceScheduleDetail> schedules = new ArrayList<>();

		FinanceScheduleDetail schd = new FinanceScheduleDetail();
		schd.setPrincipalSchd(new BigDecimal("900"));
		schd.setRepayOnSchDate(true);

		schedules.add(schd);

		schd = new FinanceScheduleDetail();
		schd.setPrincipalSchd(new BigDecimal("900"));
		schd.setRepayOnSchDate(true);

		schedules.add(schd);

		BigDecimal totalSchdPri = SchdUtil.getTotalPrincipalSchd(schedules);

		Assert.assertEquals(totalSchdPri, new BigDecimal("1800"));
	}

	@Test
	public void testGetTotalPriSchd2() {
		List<FinanceScheduleDetail> schedules = new ArrayList<>();

		FinanceScheduleDetail schd = new FinanceScheduleDetail();
		schd.setPrincipalSchd(new BigDecimal("900"));
		schd.setRepayOnSchDate(true);

		schedules.add(schd);

		schd = new FinanceScheduleDetail();
		schd.setPrincipalSchd(new BigDecimal("900"));
		schd.setRepayOnSchDate(false);

		schedules.add(schd);

		BigDecimal totalSchdPri = SchdUtil.getTotalPrincipalSchd(schedules);

		Assert.assertEquals(totalSchdPri, new BigDecimal("900"));
	}

	@Test
	public void testGetTotalRepayAmount1() {
		List<FinanceScheduleDetail> schedules = new ArrayList<>();

		FinanceScheduleDetail schd = new FinanceScheduleDetail();
		schd.setProfitSchd(new BigDecimal("100"));
		schd.setPrincipalSchd(new BigDecimal("900"));
		schd.setFeeSchd(new BigDecimal("1000"));

		schd.setSchdPftPaid(new BigDecimal("100"));
		schd.setSchdPriPaid(new BigDecimal("900"));
		schd.setSchdFeePaid(new BigDecimal("1000"));

		schd.setRepayOnSchDate(true);

		schedules.add(schd);

		schd = new FinanceScheduleDetail();
		schd.setProfitSchd(new BigDecimal("100"));
		schd.setPrincipalSchd(new BigDecimal("900"));
		schd.setFeeSchd(new BigDecimal("1000"));

		schd.setSchdPftPaid(new BigDecimal("100"));
		schd.setSchdPriPaid(new BigDecimal("500"));
		schd.setSchdFeePaid(new BigDecimal("1000"));

		schd.setRepayOnSchDate(true);

		schedules.add(schd);

		BigDecimal totalSchdPri = SchdUtil.getTotalRepayAmount(schedules);

		Assert.assertEquals(totalSchdPri, new BigDecimal("400"));
	}

	@Test
	public void testGetNextEMI1() {

		List<FinanceScheduleDetail> schedules = new ArrayList<>();

		FinanceScheduleDetail schd = new FinanceScheduleDetail();
		schd.setSchDate(DateUtil.getDate(2023, 0, 05));
		schd.setRepayAmount(new BigDecimal("1000"));
		schd.setRepayOnSchDate(true);
		schedules.add(schd);

		schd = new FinanceScheduleDetail();
		schd.setSchDate(DateUtil.getDate(2023, 1, 05));
		schd.setRepayAmount(new BigDecimal("2000"));
		schd.setRepayOnSchDate(true);
		schedules.add(schd);

		schd = new FinanceScheduleDetail();
		schd.setSchDate(DateUtil.getDate(2023, 2, 05));
		schd.setRepayAmount(new BigDecimal("3000"));
		schd.setRepayOnSchDate(true);
		schedules.add(schd);

		BigDecimal nextEMI = SchdUtil.getNextEMI(DateUtil.getDate(2023, 1, 06), schedules);

		Assert.assertEquals(nextEMI, new BigDecimal("3000"));

	}

	@Test
	public void testGetNextEMI2() {

		List<FinanceScheduleDetail> schedules = new ArrayList<>();

		FinanceScheduleDetail schd = new FinanceScheduleDetail();
		schd.setSchDate(DateUtil.getDate(2023, 0, 05));
		schd.setRepayAmount(new BigDecimal("1000"));
		schd.setRepayOnSchDate(true);
		schedules.add(schd);

		schd = new FinanceScheduleDetail();
		schd.setSchDate(DateUtil.getDate(2023, 1, 05));
		schd.setRepayAmount(new BigDecimal("2000"));
		schd.setRepayOnSchDate(true);
		schedules.add(schd);

		schd = new FinanceScheduleDetail();
		schd.setSchDate(DateUtil.getDate(2023, 2, 05));
		schd.setRepayAmount(new BigDecimal("3000"));
		schd.setRepayOnSchDate(true);
		schedules.add(schd);

		BigDecimal nextEMI = SchdUtil.getNextEMI(DateUtil.getDate(2023, 2, 06), schedules);

		Assert.assertEquals(nextEMI, new BigDecimal("0"));

	}

	@Test
	public void testGetRepaymentStatus1() {

		FinanceScheduleDetail schd = new FinanceScheduleDetail();
		schd.setProfitSchd(new BigDecimal("100"));
		schd.setPrincipalSchd(new BigDecimal("900"));
		schd.setFeeSchd(new BigDecimal("1000"));

		schd.setSchdPftPaid(new BigDecimal("100"));
		schd.setSchdPriPaid(new BigDecimal("900"));
		schd.setSchdFeePaid(new BigDecimal("1000"));

		schd.setRepayOnSchDate(true);

		RepaymentStatus repaymentStatus = SchdUtil.getRepaymentStatus(schd);

		Assert.assertEquals(repaymentStatus, RepaymentStatus.PAID);
	}

	@Test
	public void testGetRepaymentStatus2() {

		FinanceScheduleDetail schd = new FinanceScheduleDetail();
		schd.setProfitSchd(new BigDecimal("100"));
		schd.setPrincipalSchd(new BigDecimal("900"));
		schd.setFeeSchd(new BigDecimal("1000"));

		schd.setSchdPftPaid(new BigDecimal("100"));
		schd.setSchdPriPaid(new BigDecimal("900"));
		schd.setSchdFeePaid(new BigDecimal("10000"));

		schd.setRepayOnSchDate(true);

		RepaymentStatus repaymentStatus = SchdUtil.getRepaymentStatus(schd);

		Assert.assertEquals(repaymentStatus, RepaymentStatus.PAID);
	}

	@Test
	public void testGetRepaymentStatus3() {

		FinanceScheduleDetail schd = new FinanceScheduleDetail();
		schd.setProfitSchd(new BigDecimal("100"));
		schd.setPrincipalSchd(new BigDecimal("900"));
		schd.setFeeSchd(new BigDecimal("1000"));

		schd.setSchdPftPaid(new BigDecimal("0"));
		schd.setSchdPriPaid(new BigDecimal("0"));
		schd.setSchdFeePaid(new BigDecimal("1000"));

		schd.setRepayOnSchDate(true);

		RepaymentStatus repaymentStatus = SchdUtil.getRepaymentStatus(schd);

		Assert.assertEquals(repaymentStatus, RepaymentStatus.PARTIALLY_PAID);
	}

	@Test
	public void testGetRepaymentStatus4() {

		FinanceScheduleDetail schd = new FinanceScheduleDetail();
		schd.setProfitSchd(new BigDecimal("100"));
		schd.setPrincipalSchd(new BigDecimal("900"));
		schd.setFeeSchd(new BigDecimal("1000"));

		schd.setSchdPftPaid(new BigDecimal("0"));
		schd.setSchdPriPaid(new BigDecimal("0"));
		schd.setSchdFeePaid(new BigDecimal("0"));

		schd.setRepayOnSchDate(true);

		RepaymentStatus repaymentStatus = SchdUtil.getRepaymentStatus(schd);

		Assert.assertEquals(repaymentStatus, RepaymentStatus.UNPAID);
	}

}
