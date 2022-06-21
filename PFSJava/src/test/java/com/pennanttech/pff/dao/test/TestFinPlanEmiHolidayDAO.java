package com.pennanttech.pff.dao.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.pennant.backend.dao.finance.FinPlanEmiHolidayDAO;
import com.pennant.backend.model.finance.FinPlanEmiHoliday;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;

@ContextConfiguration(locations = "classpath:dao-test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)

public class TestFinPlanEmiHolidayDAO {

	@Autowired
	private FinPlanEmiHolidayDAO finPlanEmiHolidayDAO;
	private FinPlanEmiHoliday emim;
	Date dt1 = DateUtil.parse("01/02/2019", DateFormat.SHORT_DATE);

	// ========months byref========//

	@Test
	@Transactional
	@Rollback(true)
	public void testMonthByRef() {
		emim = new FinPlanEmiHoliday();
		finPlanEmiHolidayDAO.getPlanEMIHMonthsByRef(1007, "");
		finPlanEmiHolidayDAO.getPlanEMIHMonthsByRef(0, "");

	}

	// =======datebyref========//

	@Test
	@Transactional
	@Rollback(true)
	public void testDateByRef() {
		emim = new FinPlanEmiHoliday();
		finPlanEmiHolidayDAO.getPlanEMIHDatesByRef(4593, "");
		finPlanEmiHolidayDAO.getPlanEMIHDatesByRef(0, "");

	}

	// ===DeleteByMonths===//

	@Test
	@Transactional
	@Rollback(true)
	public void testDelete() {
		emim = new FinPlanEmiHoliday();
		finPlanEmiHolidayDAO.deletePlanEMIHMonths(683, "");

	}

	// ===DeleteByDates===//

	@Test
	@Transactional
	@Rollback(true)
	public void testDeleteDate() {
		emim = new FinPlanEmiHoliday();
		finPlanEmiHolidayDAO.deletePlanEMIHDates(2056, "");

	}

	// ===========savemnts==========//

	@Test
	@Transactional
	@Rollback(true)
	public void saveMonth() {
		List<FinPlanEmiHoliday> ocr = new ArrayList<FinPlanEmiHoliday>();
		emim = new FinPlanEmiHoliday();
		emim.setFinID(8);
		emim.setFinReference("1000NPL0001422");
		emim.setPlanEMIHMonth(9);
		ocr.add(emim);
		finPlanEmiHolidayDAO.savePlanEMIHMonths(ocr, "");
	}

	// ==============savedates==========//
	@Test
	@Transactional
	@Rollback(true)
	public void saveDates() {

		List<FinPlanEmiHoliday> ocr = new ArrayList<FinPlanEmiHoliday>();
		emim = new FinPlanEmiHoliday();
		emim.setFinID(8);
		emim.setFinReference("AN00AGR0003428");

		emim.setPlanEMIHDate(dt1);

		emim.setPlanEMIHMonth(9);
		ocr.add(emim);
		finPlanEmiHolidayDAO.savePlanEMIHDates(ocr, "");

	}

}
