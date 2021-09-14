package com.pennanttech.pff.dao.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.pennant.backend.dao.finance.FinODPenaltyRateDAO;
import com.pennant.backend.model.finance.FinODPenaltyRate;

@ContextConfiguration(locations = "classpath:dao-test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)

public class TestFinODPenaltyRateDAO {

	@Autowired
	private FinODPenaltyRateDAO finODPenaltyRateDAO;
	private FinODPenaltyRate pr;

	// ===========getByRefer===========//
	@Test
	@Transactional
	@Rollback(true)
	public void testByRef() {
		pr = new FinODPenaltyRate();
		finODPenaltyRateDAO.getFinODPenaltyRateByRef(2275, "");
		finODPenaltyRateDAO.getFinODPenaltyRateByRef(0, "");

	}

	// ==========save==========//
	@Test
	@Transactional
	@Rollback(true)
	public void testSave() {
		pr = new FinODPenaltyRate();
		pr.setFinReference("10000200001781");
		pr.setApplyODPenalty(true);
		pr.setODIncGrcDays(true);
		pr.setODAllowWaiver(true);
		finODPenaltyRateDAO.save(pr, "");

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdate() {
		pr = new FinODPenaltyRate();
		pr.setFinReference("10000200001781");
		pr.setApplyODPenalty(true);
		pr.setODIncGrcDays(true);
		pr.setODAllowWaiver(true);
		finODPenaltyRateDAO.update(pr, "");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testDelete() {
		pr = new FinODPenaltyRate();
		finODPenaltyRateDAO.delete(2275, "");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void test() {
		pr = new FinODPenaltyRate();
		finODPenaltyRateDAO.getDMFinODPenaltyRateByRef(2276, "");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testSaveLog() {
		pr = new FinODPenaltyRate();
		pr.setFinReference("10000200001781");
		pr.setApplyODPenalty(true);
		pr.setODIncGrcDays(true);
		pr.setODAllowWaiver(true);
		pr.setLogKey(7);
		finODPenaltyRateDAO.saveLog(pr, "");

		// PreparedStatementCallback; bad SQL grammar
		// ERROR: column "logkey" of relation "finodpenaltyrates" does not exist
		// Position: 31
	}

}
