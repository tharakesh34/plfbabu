package com.pennanttech.pff.dao.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.pennant.backend.dao.finance.financialSummary.DueDiligenceDetailsDAO;
import com.pennant.backend.model.finance.financialsummary.DueDiligenceDetails;

@ContextConfiguration(locations = "classpath:dao-test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class TestDueDiligenceDetailsDAO {

	@Autowired
	private DueDiligenceDetailsDAO dueDiligenceDetailsDAO;

	@Test
	@Transactional
	@Rollback(true)
	public void TestSave() {
		DueDiligenceDetails dd = new DueDiligenceDetails();
		dd.setId(1);
		dd.setFinReference("1500BUS0003280");
		dd.setParticularId(1);
		dd.setWorkflowId(0);
		dd.setFinID(5354);
		dueDiligenceDetailsDAO.save(dd, "");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void TestSave1() {
		// DuplicateKeyException
		DueDiligenceDetails dd = new DueDiligenceDetails();
		dd.setId(1);
		dd.setFinReference("1500BUS0003280");
		dd.setParticularId(1);
		dd.setWorkflowId(0);
		dd.setFinID(5354);
		dueDiligenceDetailsDAO.save(dd, "");
		DueDiligenceDetails dd1 = new DueDiligenceDetails();
		dd1.setId(1);
		dd1.setFinReference("1500BUS0003280");
		dd1.setParticularId(1);
		dd1.setWorkflowId(0);
		dd1.setFinID(5354);
		dueDiligenceDetailsDAO.save(dd1, "");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void TestGetDueDiligenceDetails() {
		DueDiligenceDetails dd = new DueDiligenceDetails();
		dd.setId(1);
		dd.setFinReference("1500BUS0003280");
		dd.setParticularId(1);
		dd.setWorkflowId(0);
		dd.setFinID(5354);
		dueDiligenceDetailsDAO.save(dd, "");
		dueDiligenceDetailsDAO.getDueDiligenceDetails(5354);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void TestDelete() {
		DueDiligenceDetails dd = new DueDiligenceDetails();
		dd.setId(1);
		dd.setFinReference("1500BUS0003280");
		dd.setParticularId(1);
		dd.setWorkflowId(0);
		dd.setFinID(5354);
		dueDiligenceDetailsDAO.save(dd, "");
		dueDiligenceDetailsDAO.delete(dd, "");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void TestDelete1() {
		// recordCount <= 0
		DueDiligenceDetails dd = new DueDiligenceDetails();
		dd.setId(1);
		dd.setFinReference("1500BUS0003280");
		dd.setParticularId(1);
		dd.setWorkflowId(0);
		dd.setFinID(5354);
		dueDiligenceDetailsDAO.delete(dd, "");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void TesUpdate() {
		DueDiligenceDetails dd = new DueDiligenceDetails();
		dd.setId(1);
		dd.setFinReference("1500BUS0003280");
		dd.setParticularId(1);
		dd.setWorkflowId(0);
		dd.setFinID(5354);
		dd.setVersion(1);
		dueDiligenceDetailsDAO.save(dd, "");
		dd.setVersion(2);
		dueDiligenceDetailsDAO.update(dd, "");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void TesUpdate1() {
		DueDiligenceDetails dd = new DueDiligenceDetails();
		dd.setId(1);
		dd.setFinReference("1500BUS0003280");
		dd.setParticularId(1);
		dd.setWorkflowId(0);
		dd.setFinID(5354);
		dd.setVersion(1);
		dueDiligenceDetailsDAO.save(dd, "_Temp");
		dueDiligenceDetailsDAO.update(dd, "_Temp");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void TesUpdate2() {
		// recordCount <= 0
		DueDiligenceDetails dd = new DueDiligenceDetails();
		dd.setId(1);
		dd.setFinReference("1500BUS0003280");
		dd.setParticularId(1);
		dd.setWorkflowId(0);
		dd.setFinID(5354);
		dd.setVersion(1);
		dueDiligenceDetailsDAO.update(dd, "_Temp");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void TestGetVersion() {
		DueDiligenceDetails dd = new DueDiligenceDetails();
		dd.setId(1);
		dd.setFinReference("1500BUS0003280");
		dd.setParticularId(1);
		dd.setWorkflowId(0);
		dd.setFinID(5354);
		dd.setVersion(1);
		dueDiligenceDetailsDAO.save(dd, "");
		dueDiligenceDetailsDAO.getVersion(1, 5354);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void TestGetStatus() {
		// No data found(Even insertion through change-log only)
		dueDiligenceDetailsDAO.getStatus(1);
	}

}
