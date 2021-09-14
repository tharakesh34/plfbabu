package com.pennanttech.pff.dao.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.pennant.backend.dao.finance.FinStageAccountingLogDAO;
import com.pennant.backend.model.finance.FinStageAccountingLog;

@ContextConfiguration(locations = "classpath:dao-test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class TestFinStageAccountingLogDAO {

	@Autowired
	private FinStageAccountingLogDAO finStageAccountingLogDAO;
	private FinStageAccountingLog fcal;

	@Test
	@Transactional
	@Rollback(true)
	public void testLti() {
		fcal = new FinStageAccountingLog();
		finStageAccountingLogDAO.getLinkedTranId(4, "abc", "a22");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void getList() {
		fcal = new FinStageAccountingLog();
		finStageAccountingLogDAO.getLinkedTranIdList(1, "abc");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void save() {
		fcal = new FinStageAccountingLog();
		fcal.setFinReference("a");
		fcal.setRoleCode("b");
		fcal.setLinkedTranId(9);
		fcal.setFinEvent("abcd");
		fcal.setProcessed(false);
		fcal.setFinID(2);
		finStageAccountingLogDAO.saveStageAccountingLog(fcal);

	}

	// ========deleteByRef&Role=====//

	@Test
	@Transactional
	@Rollback(true)
	public void deleteByRole() {
		fcal = new FinStageAccountingLog();
		fcal.setFinReference("a");
		fcal.setRoleCode("b");
		fcal.setLinkedTranId(9);
		fcal.setFinEvent("abcd");
		fcal.setProcessed(false);
		fcal.setFinID(2);
		finStageAccountingLogDAO.deleteByRefandRole(23, "", "");
		// DataIntegrityViolationException: PreparedStatementCallback
		// No value specified for parameter 3.
	}

	// =========Update===========//

	@Test
	@Transactional
	@Rollback(true)
	public void update() {
		fcal = new FinStageAccountingLog();
		fcal.setFinReference("a");
		fcal.setRoleCode("b");
		fcal.setLinkedTranId(9);
		fcal.setFinEvent("abcd");
		fcal.setProcessed(false);
		fcal.setFinID(2);
		finStageAccountingLogDAO.update(77, "", false);
	}
	//// DataIntegrityViolationException: PreparedStatementCallback
	// No value specified for parameter 4.

	// ===byrcno========//

	@Test
	@Transactional
	@Rollback(true)
	public void testRcNo() {
		fcal = new FinStageAccountingLog();
		finStageAccountingLogDAO.getTranCountByReceiptNo("a229");
	}

	// =====by receipt====//

	@Test
	@Transactional
	@Rollback(true)
	public void testReceipt() {
		fcal = new FinStageAccountingLog();
		finStageAccountingLogDAO.getTranIdListByReceipt("229");
	}
	// =====by receiptNo====//

	@Test
	@Transactional
	@Rollback(true)
	public void testReceiptNo() {
		fcal = new FinStageAccountingLog();
		finStageAccountingLogDAO.deleteByReceiptNo("99");
	}
}
