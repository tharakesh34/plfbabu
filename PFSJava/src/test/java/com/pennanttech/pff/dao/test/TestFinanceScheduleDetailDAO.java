package com.pennanttech.pff.dao.test;

import java.math.BigDecimal;
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
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.ScheduleDueTaxDetail;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.App.Database;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pff.core.TableType;

@ContextConfiguration(locations = "classpath:dao-test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class TestFinanceScheduleDetailDAO {

	@Autowired
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;

	@Autowired
	private FinanceMainDAO financeMainDAO;

	private FinanceScheduleDetail fsd;

	private FinanceMain fm;

	Date dt1 = DateUtil.parse("06/02/2022", DateFormat.SHORT_DATE);
	Date dt2 = DateUtil.parse("01/11/2032", DateFormat.SHORT_DATE);
	Date dt3 = DateUtil.parse("17/01/2034", DateFormat.SHORT_DATE);

	@Test
	@Transactional
	@Rollback(true)
	public void testGetFinanceScheduleDetailById() {

		financeScheduleDetailDAO.getFinanceScheduleDetailById(5354, dt1, "", false);
		financeScheduleDetailDAO.getFinanceScheduleDetailById(5323, dt2, "", true);

		financeScheduleDetailDAO.deleteByFinReference(5323, "", true, 0);

		financeScheduleDetailDAO.getFinScheduleDetails(5354, "", false);

		financeScheduleDetailDAO.getFinScheduleDetails(1463, true);
		financeScheduleDetailDAO.getFinScheduleDetails(1463, false);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testGetFinScheduleDetails() {
		// logkey column does not exist
		financeScheduleDetailDAO.getFinScheduleDetails(5354, "", false, 0);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testGetFinSchdDetailsForBatch() {
		financeScheduleDetailDAO.getFinSchdDetailsForBatch(5354);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testGetSuspenseAmount() {
		financeScheduleDetailDAO.getSuspenseAmount(5354, dt1);

		BigDecimal amt = financeScheduleDetailDAO.getTotalRepayAmount(5354);
		BigDecimal amt1 = financeScheduleDetailDAO.getTotalRepayAmount(500);

		financeScheduleDetailDAO.getTotalUnpaidPriAmount(5354);
		financeScheduleDetailDAO.getTotalUnpaidPftAmount(5354);

		financeScheduleDetailDAO.getTotals(5354);

		financeScheduleDetailDAO.getNextSchPayment(5354, dt1);
		financeScheduleDetailDAO.getNextSchPayment(500, dt1);

		financeScheduleDetailDAO.getFinScheduleCountByDate(5354, dt1, false);
		financeScheduleDetailDAO.getFinScheduleCountByDate(5323, dt2, true);

		financeScheduleDetailDAO.getPriPaidAmount(5354);

		financeScheduleDetailDAO.getOutStandingBalFromFees(5354);

		financeScheduleDetailDAO.getPrevSchdDate(5354, dt1);

		financeScheduleDetailDAO.isInstallSchd(5354, dt1);
		financeScheduleDetailDAO.isInstallSchd(500, dt1);

		financeScheduleDetailDAO.getClosingBalance(5354, dt1);
		financeScheduleDetailDAO.getClosingBalance(500, dt1);

		financeScheduleDetailDAO.getFirstRepayAmt(5354);

		financeScheduleDetailDAO.getPrvSchd(5354, dt1);
		financeScheduleDetailDAO.getPrvSchd(500, dt1);

		financeScheduleDetailDAO.getFinSchdDetailsForRateReport(5354);

		financeScheduleDetailDAO.getFinanceMainForRateReport(5354, "");
		financeScheduleDetailDAO.getFinanceMainForRateReport(500, "");

		financeScheduleDetailDAO.isScheduleInQueue(5354);

		financeScheduleDetailDAO.getDueBucket(5354);

		financeScheduleDetailDAO.getDueSchedulesByFacilityRef(100, dt1);

		financeScheduleDetailDAO.getSchdDueInvoiceID(5043, dt3);

		financeScheduleDetailDAO.getFinScheduleDetailsBySchPriPaid(5354, "", false);

		financeScheduleDetailDAO.getNextUnpaidSchPayment(5354, dt1);
		financeScheduleDetailDAO.getNextUnpaidSchPayment(500, dt1);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testGetFinScheduleDetails1() {
		financeScheduleDetailDAO.getFinScheduleDetails(5354, "", 0);
		// logkey column doesnot exist
		financeScheduleDetailDAO.getFinScheduleDetails(5354, "", 1);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testGetWriteoffTotals() {
		financeScheduleDetailDAO.getWriteoffTotals(5354);
		financeScheduleDetailDAO.getWriteoffTotals(500);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testGetFirstRepayDate() {
		// cloumn RepayOnSchDate does not exist
		financeScheduleDetailDAO.getFirstRepayDate(5354);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testDeleteByFinReference() {
		// logkey column does not exist
		financeScheduleDetailDAO.deleteByFinReference(5354, "", false, 1);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testDelete() {
		fsd = new FinanceScheduleDetail();
		fsd.setFinID(5354);
		fsd.setSchDate(dt1);
		financeScheduleDetailDAO.delete(fsd, "", false);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testDeleteWIF() {
		fsd = new FinanceScheduleDetail();
		fsd.setFinID(5323);
		fsd.setSchDate(dt2);
		financeScheduleDetailDAO.delete(fsd, "", true);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testDelete1() {
		// record count = 0
		fsd = new FinanceScheduleDetail();
		fsd.setFinID(500);
		fsd.setSchDate(dt1);
		financeScheduleDetailDAO.delete(fsd, "", false);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testSave() {
		fsd = new FinanceScheduleDetail();
		fm = new FinanceMain();
		fm = financeMainDAO.getFinanceMainById(5354, "", false);
		fsd = financeScheduleDetailDAO.getFinanceScheduleDetailById(5354, dt1, "", false);
		fm.setFinID(fm.getFinID() + 1);
		fm.setFinReference(fm.getFinReference() + 1);
		financeMainDAO.save(fm, TableType.MAIN_TAB, false);
		fsd.setFinID(fsd.getFinID() + 1);
		fsd.setFinReference(fsd.getFinReference() + 1);
		financeScheduleDetailDAO.save(fsd, "", false);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testSaveWIF() {
		fsd = new FinanceScheduleDetail();
		fm = new FinanceMain();
		fm = financeMainDAO.getFinanceMainById(5323, "", true);
		fsd = financeScheduleDetailDAO.getFinanceScheduleDetailById(5323, dt2, "", true);
		fm.setFinID(fm.getFinID() + 1);
		fm.setFinReference(fm.getFinReference() + 1);
		financeMainDAO.save(fm, TableType.MAIN_TAB, true);
		fsd.setFinID(fsd.getFinID() + 1);
		fsd.setFinReference(fsd.getFinReference() + 1);
		financeScheduleDetailDAO.save(fsd, "", true);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateForRpy() {
		fsd = new FinanceScheduleDetail();
		fsd = financeScheduleDetailDAO.getFinanceScheduleDetailById(5354, dt1, "", false);
		financeScheduleDetailDAO.updateForRpy(fsd);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateForRpy1() {
		// recordcount = 0
		fsd = new FinanceScheduleDetail();
		fsd = financeScheduleDetailDAO.getFinanceScheduleDetailById(5354, dt1, "", false);
		fsd.setFinID(500);
		financeScheduleDetailDAO.updateForRpy(fsd);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateListForRpy() {
		List<FinanceScheduleDetail> list = new ArrayList<FinanceScheduleDetail>();
		fsd = new FinanceScheduleDetail();
		fsd = financeScheduleDetailDAO.getFinanceScheduleDetailById(5354, dt1, "", false);
		list.add(fsd);
		financeScheduleDetailDAO.updateListForRpy(list);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateForRateReview() {
		List<FinanceScheduleDetail> list = new ArrayList<FinanceScheduleDetail>();
		fsd = new FinanceScheduleDetail();
		fsd = financeScheduleDetailDAO.getFinanceScheduleDetailById(5354, dt1, "", false);
		list.add(fsd);
		financeScheduleDetailDAO.updateForRateReview(list);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateTDS() {
		List<FinanceScheduleDetail> list = new ArrayList<FinanceScheduleDetail>();
		fsd = new FinanceScheduleDetail();
		fsd = financeScheduleDetailDAO.getFinanceScheduleDetailById(5354, dt1, "", false);
		list.add(fsd);
		financeScheduleDetailDAO.updateTDS(list);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateSchPaid() {
		fsd = new FinanceScheduleDetail();
		fsd = financeScheduleDetailDAO.getFinanceScheduleDetailById(5354, dt1, "", false);
		financeScheduleDetailDAO.updateSchPaid(fsd);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateSchPaid1() {
		// record count = 0
		fsd = new FinanceScheduleDetail();
		fsd = financeScheduleDetailDAO.getFinanceScheduleDetailById(5354, dt1, "", false);
		fsd.setFinID(500);
		financeScheduleDetailDAO.updateSchPaid(fsd);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testSaveSchDueTaxDetail() {
		ScheduleDueTaxDetail sdtd = new ScheduleDueTaxDetail();
		sdtd.setFinID(5352);
		sdtd.setFinReference("1500AGR0006412");
		sdtd.setSchDate(JdbcUtil.getDate(dt1));
		sdtd.setTaxType("B");
		sdtd.setTaxCalcOn("I");
		sdtd.setAmount(new BigDecimal(100000));
		sdtd.setInvoiceID(Long.valueOf(29675));
		financeScheduleDetailDAO.saveSchDueTaxDetail(sdtd);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testSaveSchDueTaxDetail1() {
		ScheduleDueTaxDetail sdtd = new ScheduleDueTaxDetail();
		sdtd.setFinID(5352);
		sdtd.setFinReference("1500AGR0006412");
		sdtd.setSchDate(JdbcUtil.getDate(dt1));
		sdtd.setTaxType("B");
		sdtd.setTaxCalcOn("I");
		sdtd.setAmount(new BigDecimal(100000));
		sdtd.setInvoiceID(null);
		financeScheduleDetailDAO.saveSchDueTaxDetail(sdtd);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateTDSChange() {
		List<FinanceScheduleDetail> list = new ArrayList<FinanceScheduleDetail>();
		fsd = new FinanceScheduleDetail();
		fsd = financeScheduleDetailDAO.getFinanceScheduleDetailById(5354, dt1, "", false);
		list.add(fsd);
		financeScheduleDetailDAO.updateTDSChange(list);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testGetFinanceScheduleDetailById1() {
		App.DATABASE = Database.SQL_SERVER;
		financeScheduleDetailDAO.getFinanceScheduleDetailById(5354, dt1, "", false);

	}

}
