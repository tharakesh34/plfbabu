package com.pennanttech.pff.dao.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.pennant.backend.dao.systemmasters.PMAYDAO;
import com.pennant.backend.model.finance.PMAY;
import com.pennant.backend.model.finance.PmayEligibilityLog;
import com.pennanttech.pff.core.TableType;

@ContextConfiguration(locations = "classpath:dao-test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class TestPMAYDAO {

	@Autowired
	private PMAYDAO pmayDAO;
	private PMAY p;

	@Test
	@Transactional
	@Rollback(true)
	public void testSave() {
		pmayDAO.getPMAY(5351, "_Temp");
		pmayDAO.getPMAY(5351, "");
		pmayDAO.getPMAY(5351654, "_Temp");
		pmayDAO.getPMAY(5351867, "");
		pmayDAO.getPMAY(5351, "_View");// finid column does not exist in "pmay_view"

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testDuplicate() {
		pmayDAO.isDuplicateKey(5351, TableType.MAIN_TAB);
		pmayDAO.isDuplicateKey(5351, TableType.TEMP_TAB);
		pmayDAO.isDuplicateKey(535187, TableType.TEMP_TAB);
		pmayDAO.isDuplicateKey(535187, TableType.MAIN_TAB);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testDuplicates() {
		pmayDAO.isDuplicateKey(5351, TableType.BOTH_TAB); // only sending single parameter in default case(changed)
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdate() {
		p = new PMAY();
		p = pmayDAO.getPMAY(5351, "");
		pmayDAO.update(p, TableType.TEMP_TAB);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdates() {
		p = new PMAY();
		p = pmayDAO.getPMAY(5351, "");
		p.setFinID(500);
		pmayDAO.update(p, TableType.MAIN_TAB);// in Pmay table column "lastmnton" is of type timestamp without time zone
												// but expression is of type bigint
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testValidations() {
		pmayDAO.isFinReferenceExists(5351);
		p = new PMAY();
		p = pmayDAO.getPMAY(5351, "");
		pmayDAO.delete(p, TableType.MAIN_TAB);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testValidatio() {
		pmayDAO.isFinReferenceExists(5351);
		pmayDAO.isFinReferenceExists(500);
		p = new PMAY();
		p = pmayDAO.getPMAY(5351, "");
		p.setFinID(500);
		p.setFinReference("9876");
		pmayDAO.delete(p, TableType.MAIN_TAB);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testPmay() {
		p = new PMAY();
		p = pmayDAO.getPMAY(4477, "_Temp");
		pmayDAO.save(p, TableType.TEMP_TAB);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testPmaySave() {
		p = pmayDAO.getPMAY(5351, "");
		p.setFinID(p.getFinID() + 1);
		p.setFinReference(p.getFinReference() + 1);
		pmayDAO.save(p, TableType.MAIN_TAB);
		pmayDAO.getEligibilityLog(3, "");
	}

	@Test
	@Transactional
	@Rollback(true) // no data in PmayEligibilityLog table so duplicate exception is not covered
	public void testPmayEligible() {
		PmayEligibilityLog peg = new PmayEligibilityLog();
		peg.setFinID(5354);
		pmayDAO.save(peg, TableType.MAIN_TAB);// column "applicantid" is of type bigint but expression is of type
		// character varying
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testPmayEligibilityLogUpdate() {
		PmayEligibilityLog peg = new PmayEligibilityLog();
		peg.setFinID(3);
		pmayDAO.update(peg, TableType.MAIN_TAB);
	}

	@Test
	@Transactional
	@Rollback(true) // no data in PmayEligibilityLog table so exception only covered
	public void testPmayEligibilityLogById() {
		pmayDAO.getEligibilityLog(3, "");
		pmayDAO.getEligibilityLogList(3, "");
		pmayDAO.getEligibilityLog(3, "_Temp");
		pmayDAO.getEligibilityLogList(3, "_Temp");

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testPELUpdate() {
		PmayEligibilityLog peg = new PmayEligibilityLog();
		peg.setFinID(3);
		peg.setPmayStatus(null);
		peg.setApplicantId(null);
		peg.setRecordId(0);
		pmayDAO.update(peg);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUp() {
		pmayDAO.update("608", null);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpl() {
		pmayDAO.update("60898", null);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testGetCif() {
		pmayDAO.getCustCif(2);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testGetpamy() {
		pmayDAO.getAllRecordIdForPmay();
	}
}
