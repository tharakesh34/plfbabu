package com.pennanttech.pff.dao.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.pennant.backend.dao.finance.financialSummary.SanctionConditionsDAO;
import com.pennant.backend.model.finance.financialsummary.SanctionConditions;

@ContextConfiguration(locations = "classpath:dao-test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class TestSanctionConditionsDAO {

	@Autowired
	private SanctionConditionsDAO sanctionConditionsDAO;

	@Test
	@Transactional
	@Rollback(true)
	public void TestSave() {
		SanctionConditions sc = new SanctionConditions();
		sc.setId(1);
		sc.setFinReference("1500BUS0003280");
		sc.setFinID(5354);
		sc.setSeqNo(1);
		sc.setWorkflowId(0);
		sanctionConditionsDAO.save(sc, "");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void TestSave1() {
		SanctionConditions sc = new SanctionConditions();
		sc.setId(1);
		sc.setFinReference("1500BUS0003280");
		sc.setFinID(5354);
		sc.setSeqNo(1);
		sc.setWorkflowId(0);
		sanctionConditionsDAO.save(sc, "_Temp");
		sanctionConditionsDAO.getSanctionConditions(5354);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void TestDelete() {
		SanctionConditions sc = new SanctionConditions();
		sc.setId(1);
		sc.setFinReference("1500BUS0003280");
		sc.setFinID(5354);
		sc.setSeqNo(1);
		sc.setWorkflowId(0);
		sanctionConditionsDAO.save(sc, "");
		sanctionConditionsDAO.delete(sc, "");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void TestDelete1() {
		// recordCount <= 0
		SanctionConditions sc = new SanctionConditions();
		sc.setId(1);
		sc.setFinReference("1500BUS0003280");
		sc.setFinID(5354);
		sc.setSeqNo(1);
		sc.setWorkflowId(0);
		sanctionConditionsDAO.delete(sc, "");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void TestUpdate() {
		SanctionConditions sc = new SanctionConditions();
		sc.setId(1);
		sc.setFinReference("1500BUS0003280");
		sc.setFinID(5354);
		sc.setSeqNo(1);
		sc.setWorkflowId(0);
		sc.setVersion(1);
		sanctionConditionsDAO.save(sc, "");
		sc.setVersion(2);
		sanctionConditionsDAO.update(sc, "");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void TestUpdate1() {
		SanctionConditions sc = new SanctionConditions();
		sc.setId(1);
		sc.setFinReference("1500BUS0003280");
		sc.setFinID(5354);
		sc.setSeqNo(1);
		sc.setWorkflowId(0);
		sc.setVersion(1);
		sanctionConditionsDAO.save(sc, "_Temp");
		sanctionConditionsDAO.update(sc, "_Temp");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void TestUpdate2() {
		// recordCount <= 0
		SanctionConditions sc = new SanctionConditions();
		sc.setId(1);
		sc.setFinReference("1500BUS0003280");
		sc.setFinID(5354);
		sc.setSeqNo(1);
		sc.setWorkflowId(0);
		sc.setVersion(1);
		sanctionConditionsDAO.update(sc, "_Temp");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void TestGetVersion() {
		// Have to send finReference
		SanctionConditions sc = new SanctionConditions();
		sc.setId(1);
		sc.setFinReference("1500BUS0003280");
		sc.setFinID(5354);
		sc.setSeqNo(1);
		sc.setWorkflowId(0);
		sanctionConditionsDAO.save(sc, "");
		sanctionConditionsDAO.getVersion(1, "1500BUS0003280");
	}
}
