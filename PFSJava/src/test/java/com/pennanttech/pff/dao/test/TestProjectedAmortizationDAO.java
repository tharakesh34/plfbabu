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

import com.pennant.backend.dao.amortization.ProjectedAmortizationDAO;
import com.pennant.backend.model.amortization.AmortizationQueuing;
import com.pennant.backend.model.amortization.ProjectedAmortization;
import com.pennant.backend.model.finance.ProjectedAccrual;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.App.Database;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;

@ContextConfiguration(locations = "classpath:dao-test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class TestProjectedAmortizationDAO {

	@Autowired
	private ProjectedAmortizationDAO projectedAmortizationDAO;

	private ProjectedAmortization pam;

	Date dt1 = DateUtil.parse("31/10/2032", DateFormat.SHORT_DATE);
	Date dt2 = DateUtil.parse("31/07/2031", DateFormat.SHORT_DATE);

	@Test
	@Transactional
	@Rollback(true)
	public void testGetIncomeAMZDetailsByRef() {
		projectedAmortizationDAO.getIncomeAMZDetailsByRef(490);

		projectedAmortizationDAO.getAMZMethodByFinRef(1117);
		projectedAmortizationDAO.getAMZMethodByFinRef(500);

		projectedAmortizationDAO.getPrvProjectedAccrual(962, null, "");
		projectedAmortizationDAO.getPrvProjectedAccrual(0, null, "");

		projectedAmortizationDAO.preparePrvProjectedAccruals(dt1);

		projectedAmortizationDAO.getProjectedAccrualsByFinRef(5055);

		projectedAmortizationDAO.getFutureProjectedAccrualsByFinRef(5055, dt2);

		projectedAmortizationDAO.deleteFutureProjAccrualsByFinRef(5055, dt2);

		projectedAmortizationDAO.deleteAllProjAccrualsByFinRef(962);

		projectedAmortizationDAO.deleteFutureProjAccruals(dt2);

		projectedAmortizationDAO.deleteAllProjAccruals();

		projectedAmortizationDAO.prepareAMZFeeDetails(null, null);

		projectedAmortizationDAO.prepareAMZExpenseDetails(null, null);

		projectedAmortizationDAO.deleteFutureProjAMZByMonthEnd(dt2);

		projectedAmortizationDAO.copyPrvProjAMZ();

		projectedAmortizationDAO.createIndexProjIncomeAMZ(); // IDX_PROJINCAMZ_EOM already exists

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testSaveBatchIncomeAMZ() {
		List<ProjectedAmortization> pam = new ArrayList<ProjectedAmortization>();
		pam = projectedAmortizationDAO.getIncomeAMZDetailsByRef(490);
		projectedAmortizationDAO.saveBatchIncomeAMZ(pam);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateBatchIncomeAMZ() {
		List<ProjectedAmortization> pam = new ArrayList<ProjectedAmortization>();
		pam = projectedAmortizationDAO.getIncomeAMZDetailsByRef(490);
		projectedAmortizationDAO.updateBatchIncomeAMZ(pam);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateBatchIncomeAMZAmounts() {
		List<ProjectedAmortization> pam = new ArrayList<ProjectedAmortization>();
		pam = projectedAmortizationDAO.getIncomeAMZDetailsByRef(490);
		projectedAmortizationDAO.updateBatchIncomeAMZAmounts(pam);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testSaveBatchProjAccruals() {
		List<ProjectedAccrual> pacList = new ArrayList<ProjectedAccrual>();
		ProjectedAccrual pac = new ProjectedAccrual();
		pac = projectedAmortizationDAO.getPrvProjectedAccrual(962, null, "");
		pac.setFinID(5354);
		pac.setFinReference("1500BUS0003280");
		pacList.add(pac);
		projectedAmortizationDAO.saveBatchProjAccruals(pacList);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testSaveBatchProjAccruals1() {
		// To Cover Exception
		List<ProjectedAccrual> pacList = new ArrayList<ProjectedAccrual>();
		ProjectedAccrual pac = new ProjectedAccrual();
		pac = projectedAmortizationDAO.getPrvProjectedAccrual(962, null, "");
		pac.setFinID(500);
		pacList.add(pac);
		projectedAmortizationDAO.saveBatchProjAccruals(pacList);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testGetPrvAMZMonthLog() {
		pam = new ProjectedAmortization();
		pam.setAmzLogId(11);
		pam.setStatus(2);
		projectedAmortizationDAO.saveAmortizationLog(pam);
		projectedAmortizationDAO.getPrvAMZMonthLog();
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testGetPrvAMZMonthLog1() {
		pam = new ProjectedAmortization();
		pam.setAmzLogId(11);
		pam.setStatus(3);
		projectedAmortizationDAO.saveAmortizationLog(pam);
		projectedAmortizationDAO.getPrvAMZMonthLog();
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testSaveAmortizationLog() {
		pam = new ProjectedAmortization();
		pam.setAmzLogId(11);
		projectedAmortizationDAO.saveAmortizationLog(pam);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testSaveAmortizationLog1() {
		pam = new ProjectedAmortization();
		pam.setAmzLogId(0);
		projectedAmortizationDAO.saveAmortizationLog(pam);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testIsAmortizationLogExist() {
		pam = new ProjectedAmortization();
		pam.setAmzLogId(11);
		pam.setStatus(1);
		projectedAmortizationDAO.saveAmortizationLog(pam);
		projectedAmortizationDAO.isAmortizationLogExist();
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testIsAmortizationLogExist1() {
		pam = new ProjectedAmortization();
		pam.setAmzLogId(11);
		pam.setStatus(2);
		projectedAmortizationDAO.saveAmortizationLog(pam);
		projectedAmortizationDAO.isAmortizationLogExist();
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testGetAmortizationLog() {
		// Issue in query
		App.DATABASE = Database.SQL_SERVER;
		pam = new ProjectedAmortization();
		pam.setAmzLogId(1);
		pam.setStatus(3);
		projectedAmortizationDAO.saveAmortizationLog(pam);
		projectedAmortizationDAO.getAmortizationLog();
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testGetAmortizationLog1() {
		// Issue with RowNum
		App.DATABASE = Database.ORACLE;
		pam = new ProjectedAmortization();
		pam.setAmzLogId(1);
		pam.setStatus(3);
		projectedAmortizationDAO.saveAmortizationLog(pam);
		projectedAmortizationDAO.getAmortizationLog();
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateAmzStatus() {
		pam = new ProjectedAmortization();
		pam.setAmzLogId(11);
		pam.setStatus(2);
		projectedAmortizationDAO.saveAmortizationLog(pam);
		projectedAmortizationDAO.updateAmzStatus(1, 11);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testGetCalAvgPOSLog() {
		// Issue with RowNum
		projectedAmortizationDAO.getCalAvgPOSLog();
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testSaveCalAvgPOSLog() {
		pam = new ProjectedAmortization();
		pam.setAmzLogId(11);
		projectedAmortizationDAO.saveCalAvgPOSLog(pam);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testSaveCalAvgPOSLog1() {
		// SeqCalAvgPOSLog(NextValue is not updating)
		pam = new ProjectedAmortization();
		pam.setAmzLogId(0);
		projectedAmortizationDAO.saveCalAvgPOSLog(pam);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateCalAvgPOSStatus() {
		pam = new ProjectedAmortization();
		pam.setAmzLogId(11);
		pam.setStatus(2);
		projectedAmortizationDAO.saveCalAvgPOSLog(pam);
		projectedAmortizationDAO.updateCalAvgPOSStatus(1, 11);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateBatchCalAvgPOS() {
		List<ProjectedAccrual> pacList = new ArrayList<ProjectedAccrual>();
		ProjectedAccrual pac = new ProjectedAccrual();
		pac = projectedAmortizationDAO.getPrvProjectedAccrual(962, null, "");
		pacList.add(pac);
		projectedAmortizationDAO.updateBatchCalAvgPOS(pacList);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testGetTotalCountByProgress() {
		// Progress column is character varying in db,cannot cast to int
		projectedAmortizationDAO.delete();
		projectedAmortizationDAO.prepareAmortizationQueue(dt2, true);
		projectedAmortizationDAO.getTotalCountByProgress();
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateActualAmount() {
		App.DATABASE = Database.POSTGRES;
		projectedAmortizationDAO.updateActualAmount(null);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateActualAmount1() {
		App.DATABASE = Database.SQL_SERVER;
		projectedAmortizationDAO.updateActualAmount(null);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateActualAmount2() {
		App.DATABASE = Database.ORACLE;
		projectedAmortizationDAO.updateActualAmount(null);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testTruncateAndInsertProjAMZ() {
		App.DATABASE = Database.SQL_SERVER;
		projectedAmortizationDAO.truncateAndInsertProjAMZ(dt2);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testTruncateAndInsertProjAMZ1() {
		App.DATABASE = Database.POSTGRES;
		projectedAmortizationDAO.truncateAndInsertProjAMZ(dt2);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testPrepareAmortizationQueue() {
		projectedAmortizationDAO.delete();
		projectedAmortizationDAO.prepareAmortizationQueue(dt2, true);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testLogAmortizationQueuing() {
		projectedAmortizationDAO.logAmortizationQueuing();
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testGetCountByProgress() {
		// Progress column is character varying in db,cannot cast to int
		projectedAmortizationDAO.getCountByProgress();
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateStatus() {
		projectedAmortizationDAO.updateStatus(1595, 2);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateFailed() {
		AmortizationQueuing aq = new AmortizationQueuing();
		aq.setFinID(1595);
		aq.setProgress(2);
		projectedAmortizationDAO.updateFailed(aq);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateThreadIDByRowNumber() {
		// ThreadId column is character varying in db,cannot cast to int
		projectedAmortizationDAO.updateThreadIDByRowNumber(dt2, 0, 1);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testSaveBatchProjIncomeAMZ() {
		ProjectedAmortization pa = new ProjectedAmortization();
		List<ProjectedAmortization> paList = new ArrayList<ProjectedAmortization>();
		pa.setFinID(5354);
		pa.setFinReference("1500BUS0003280");
		paList.add(pa);
		projectedAmortizationDAO.saveBatchProjIncomeAMZ(paList);
	}

}
