package com.pennanttech.pff.dao.test;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.pennant.backend.model.finance.FinAutoApprovalDetails;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pff.logging.dao.FinAutoApprovalDetailDAO;

@ContextConfiguration(locations = "classpath:dao-test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class TestFinAutoApprovalDetailDAO {

	@Autowired
	private FinAutoApprovalDetailDAO finAutoApprovalDetailDAO;

	Date dt1 = DateUtil.parse("06/02/2022", DateFormat.SHORT_DATE);

	@Test
	@Transactional
	@Rollback(true)
	public void testGetFinanceIfApproved() {

		finAutoApprovalDetailDAO.getFinanceIfApproved(5354);
		finAutoApprovalDetailDAO.getFinanceIfApproved(500);

		finAutoApprovalDetailDAO.loadQDPValidityDays();

		finAutoApprovalDetailDAO.getFinanceServiceInstruction(5354);
		finAutoApprovalDetailDAO.getFinanceServiceInstruction(2620);

		finAutoApprovalDetailDAO.isQuickDisb(4202);
		finAutoApprovalDetailDAO.isQuickDisb(500);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testSave() {
		FinAutoApprovalDetails fap = new FinAutoApprovalDetails();
		fap.setBatchId(2165);
		fap.setFinID(5354);
		fap.setFinReference("1500BUS0003280");
		fap.setDisbId(5968);
		fap.setRealizedDate(dt1);
		fap.setStatus("S");
		fap.setErrorDesc("");
		fap.setUserId(44734);
		fap.setDownloadedOn(dt1);
		finAutoApprovalDetailDAO.save(fap);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testSave1() {
		// To Cover Exception
		FinAutoApprovalDetails fap = new FinAutoApprovalDetails();
		fap.setBatchId(2165);
		fap.setFinID(5354);
		fap.setDisbId(5968);
		fap.setRealizedDate(dt1);
		fap.setStatus("S");
		fap.setErrorDesc("");
		fap.setUserId(44734);
		fap.setDownloadedOn(dt1);
		finAutoApprovalDetailDAO.save(fap);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdate() {
		FinAutoApprovalDetails fap = new FinAutoApprovalDetails();
		fap.setStatus("S");
		fap.setErrorDesc("");
		fap.setId(180);
		finAutoApprovalDetailDAO.update(fap);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testDelete() {
		FinAutoApprovalDetails fap = new FinAutoApprovalDetails();
		fap.setFinID(964);
		fap.setDisbId(5967);
		finAutoApprovalDetailDAO.delete(fap);
	}

}
