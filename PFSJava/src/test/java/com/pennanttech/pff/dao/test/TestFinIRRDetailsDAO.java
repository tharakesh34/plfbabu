package com.pennanttech.pff.dao.test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.pennant.backend.dao.applicationmaster.FinIRRDetailsDAO;
import com.pennant.backend.model.finance.FinIRRDetails;
import com.pennanttech.pff.core.TableType;

@ContextConfiguration(locations = "classpath:dao-test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class TestFinIRRDetailsDAO {

	@Autowired
	private FinIRRDetailsDAO finIRRDetailsDAO;

	@Test
	@Transactional
	@Rollback(true)
	public void testGetFinIRRList() {
		finIRRDetailsDAO.getFinIRRList(3676, "");
		// FinId does not exist in FinIRRDetails_View
		finIRRDetailsDAO.getFinIRRList(3676, "_View");

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testSave() {
		FinIRRDetails fid = new FinIRRDetails();
		fid.setFinID(5354);
		fid.setiRRID(69);
		finIRRDetailsDAO.delete(fid, TableType.MAIN_TAB);
		fid.setiRRID(104);
		fid.setFinReference("1500BUS0003280");
		fid.setIRR(new BigDecimal(13.01));
		fid.setWorkflowId(0);
		finIRRDetailsDAO.save(fid, TableType.MAIN_TAB);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testSave1() {
		// DuplicateKeyException
		FinIRRDetails fid = new FinIRRDetails();
		fid.setiRRID(69);
		fid.setFinReference("1500BUS0003280");
		fid.setIRR(new BigDecimal(13.01));
		fid.setWorkflowId(0);
		finIRRDetailsDAO.save(fid, TableType.MAIN_TAB);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdate() {
		FinIRRDetails fid = new FinIRRDetails();
		fid.setiRRID(69);
		fid.setFinID(5354);
		fid.setFinReference("1500BUS0003280");
		fid.setIRR(new BigDecimal(13.01));
		fid.setWorkflowId(0);
		finIRRDetailsDAO.update(fid, TableType.MAIN_TAB);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdate1() {
		// recordCount == 0
		FinIRRDetails fid = new FinIRRDetails();
		fid.setiRRID(70);
		fid.setFinID(5354);
		fid.setFinReference("1500BUS0003280");
		fid.setIRR(new BigDecimal(13.01));
		fid.setWorkflowId(0);
		finIRRDetailsDAO.update(fid, TableType.MAIN_TAB);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testDeleteList() {
		finIRRDetailsDAO.deleteList(5354, TableType.MAIN_TAB);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testSaveList() {
		List<FinIRRDetails> fidList = new ArrayList<FinIRRDetails>();
		FinIRRDetails fid = new FinIRRDetails();
		fid.setFinID(5354);
		fid.setiRRID(69);
		finIRRDetailsDAO.delete(fid, TableType.MAIN_TAB);
		fid.setiRRID(104);
		fid.setFinReference("1500BUS0003280");
		fid.setIRR(new BigDecimal(13.01));
		fid.setWorkflowId(0);
		fidList.add(fid);
		finIRRDetailsDAO.saveList(fidList, TableType.MAIN_TAB);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testSaveList1() {
		// DuplicateKeyException
		List<FinIRRDetails> fidList = new ArrayList<FinIRRDetails>();
		FinIRRDetails fid = new FinIRRDetails();
		fid.setiRRID(69);
		fid.setFinReference("1500BUS0003280");
		fid.setIRR(new BigDecimal(13.01));
		fid.setWorkflowId(0);
		fidList.add(fid);
		finIRRDetailsDAO.saveList(fidList, TableType.MAIN_TAB);
	}

}
