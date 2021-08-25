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

import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.App.Database;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pff.core.TableType;

@ContextConfiguration(locations = "classpath:dao-test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class TestFinanceProfitDetailDAO {

	@Autowired
	private FinanceProfitDetailDAO profitDetailsDAO;

	@Autowired
	private FinanceMainDAO financeMainDAO;

	private FinanceProfitDetail fpd;

	private FinanceMain fm;

	Date dt1 = DateUtil.parse("29/03/2035", DateFormat.SHORT_DATE);

	@Test
	@Transactional
	@Rollback(true)
	public void testGetFinProfitDetailsById() {
		profitDetailsDAO.getFinProfitDetailsById(5233);

		profitDetailsDAO.getPftDetailForEarlyStlReport(5233);
		profitDetailsDAO.getPftDetailForEarlyStlReport(5323);

		profitDetailsDAO.getFinProfitDetailsByCustId(1, true);
		profitDetailsDAO.getFinProfitDetailsByCustId(1, false);

		profitDetailsDAO.getFinProfitDetailsByFinRef(4090, true);

		profitDetailsDAO.getFinProfitDetailsByFinRef(2594);

		profitDetailsDAO.getFinProfitDetailsByRef(2594);
		profitDetailsDAO.getFinProfitDetailsByRef(500);

		profitDetailsDAO.getProfitDetailForWriteOff(2594);
		profitDetailsDAO.getProfitDetailForWriteOff(500);

		profitDetailsDAO.getFinProfitDetailsForSummary(2594);

		profitDetailsDAO.getAccrueAmount(2594);
		profitDetailsDAO.getAccrueAmount(500);

		profitDetailsDAO.UpdateActiveSts(4090, true);

		profitDetailsDAO.getCurOddays(4090, "");
		profitDetailsDAO.getCurOddays(500, "");

		profitDetailsDAO.isSuspenseFinance(4090);
		profitDetailsDAO.isSuspenseFinance(500);

		profitDetailsDAO.getTotalCustomerExposre(1);
		profitDetailsDAO.getTotalCustomerExposre(100);

		profitDetailsDAO.updateClosingSts(4090, true);

		profitDetailsDAO.getTotalCoApplicantsExposre(4090);
		profitDetailsDAO.getTotalCoApplicantsExposre(500);

		profitDetailsDAO.updateFinPftMaturity(2594, "M", false);

		profitDetailsDAO.getFirstRePayDateByFinRef(4090);
		profitDetailsDAO.getFirstRePayDateByFinRef(500);

		// profitDetailsDAO.getMaxRpyAmount(4090); //MaxRpyAmount Column does not exist in FinPftDetails

		profitDetailsDAO.getFinPftListForIncomeAMZ(JdbcUtil.getDate(dt1));

		profitDetailsDAO.getFinProfitForAMZ(4090);
		profitDetailsDAO.getFinProfitForAMZ(500);

		profitDetailsDAO.updateAMZMethod(4090, "I");

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testGetFinProfitDetailsById1() {
		// No data
		profitDetailsDAO.getFinProfitDetailsById(500);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testGetFinProfitDetailsByFinRef() {
		// parameters difference
		profitDetailsDAO.getFinProfitDetailsByFinRef(5233, false);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdate() {
		fpd = new FinanceProfitDetail();
		fpd = profitDetailsDAO.getFinProfitDetailsById(2594);
		profitDetailsDAO.update(fpd, true);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdate1() {
		fpd = new FinanceProfitDetail();
		fpd = profitDetailsDAO.getFinProfitDetailsById(2594);
		profitDetailsDAO.update(fpd, false);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateCpzDetail() {
		List<FinanceProfitDetail> list = new ArrayList<FinanceProfitDetail>();
		fpd = new FinanceProfitDetail();
		fpd = profitDetailsDAO.getFinProfitDetailsById(2901);
		list.add(fpd);
		profitDetailsDAO.updateCpzDetail(list);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testSave() {
		fpd = new FinanceProfitDetail();
		fm = new FinanceMain();
		fm = financeMainDAO.getFinanceMainById(5354, "", false);
		fpd = profitDetailsDAO.getFinProfitDetailsById(5354);
		fm.setFinID(fm.getFinID() + 1);
		fm.setFinReference(fm.getFinReference() + 1);
		financeMainDAO.save(fm, TableType.MAIN_TAB, false);
		fpd.setFinID(fpd.getFinID() + 1);
		fpd.setFinReference(fpd.getFinReference() + 1);
		profitDetailsDAO.save(fpd);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateLatestRpyDetails() {
		fpd = new FinanceProfitDetail();
		fpd = profitDetailsDAO.getFinProfitDetailsById(2594);
		profitDetailsDAO.updateLatestRpyDetails(fpd);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateEOD() {
		fpd = new FinanceProfitDetail();
		fpd = profitDetailsDAO.getFinProfitDetailsById(4090);
		profitDetailsDAO.updateEOD(fpd, true, true);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateEOD1() {
		fpd = new FinanceProfitDetail();
		fpd = profitDetailsDAO.getFinProfitDetailsById(4090);
		profitDetailsDAO.updateEOD(fpd, false, false);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testGetFinProfitListByFinRefList() {
		List<Long> list = new ArrayList<>();
		fpd = new FinanceProfitDetail();
		fpd = profitDetailsDAO.getFinProfitDetailsById(4090);
		list.add(fpd.getFinID());
		profitDetailsDAO.getFinProfitListByFinRefList(list);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testGetFinProfitListByFinRefList1() {
		// sending empty list
		List<Long> list = new ArrayList<>();
		fpd = new FinanceProfitDetail();
		fpd = profitDetailsDAO.getFinProfitDetailsById(4090);
		profitDetailsDAO.getFinProfitListByFinRefList(list);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateAssignmentBPIAmounts() {
		// AssignBPI1 & AssignBPI2 Columns does not exist in FinPftDetails
		fpd = new FinanceProfitDetail();
		fpd = profitDetailsDAO.getFinProfitDetailsById(4090);
		profitDetailsDAO.updateAssignmentBPIAmounts(fpd);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateSchPaid() {
		fpd = new FinanceProfitDetail();
		fpd = profitDetailsDAO.getFinProfitDetailsById(4090);
		profitDetailsDAO.updateSchPaid(fpd);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateSchPaid1() {
		// record count = 0
		fpd = new FinanceProfitDetail();
		fpd = profitDetailsDAO.getFinProfitDetailsById(4090);
		fpd.setFinID(500);
		profitDetailsDAO.updateSchPaid(fpd);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testGetFinProfitDetailsByCustId() {
		App.DATABASE = Database.SQL_SERVER;
		profitDetailsDAO.getFinProfitDetailsByCustId(1, true);
	}

}
