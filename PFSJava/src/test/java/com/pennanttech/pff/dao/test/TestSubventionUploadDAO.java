package com.pennanttech.pff.dao.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.pff.dao.subvention.SubventionUploadDAO;
import com.pennant.pff.model.subvention.Subvention;
import com.pennant.pff.model.subvention.SubventionHeader;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.pennapps.core.model.ErrorDetail;

@ContextConfiguration(locations = "classpath:dao-test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class TestSubventionUploadDAO {

	@Autowired
	private SubventionUploadDAO subventionUploadDAO;

	@Test
	@Transactional
	@Rollback(true)
	public void testSubvention() {
		subventionUploadDAO.saveSubventionHeader("ahstuoncajsd", "SDERTF");
		subventionUploadDAO.saveSubventionHeader("Subventionreq_61working file.xlsx", "ESFB");// Dupicate value
																								// exception

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testSubventionU() {
		subventionUploadDAO.getSubventionDetails(13);
		subventionUploadDAO.getFinanceMain(14);
		subventionUploadDAO.getFinFeeDetails(12, "IBASL");// column "finid" does not exist in view FinFeeDetail_AView
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testSubventionUploadUpadte() {
		FinFeeDetail ff = new FinFeeDetail();
		ff.setFeeID(12);
		ff.setOriginationFee(true);
		ff.setFeeTypeID(0);
		ff.setFeeOrder(0);
		ff.setFeeSeq(0);
		ff.setAlwDeviation(true);
		ff.setAlwModifyFee(false);
		ff.setAlwModifyFeeSchdMthd(true);
		ff.setWaivedGST(null);
		ff.setReferenceId(0);
		subventionUploadDAO.updateFinFeeDetails(1085, ff);
		subventionUploadDAO.updateFinFeeDetails(0, null);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testSubventionKnockOff() {
		Subvention s = new Subvention();
		s.setId((long) 3);
		s.setFinID(4356);
		subventionUploadDAO.updateSubventionDetails(s);
		subventionUploadDAO.updateSubventionDetails(null);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testGetSuccess() {
		subventionUploadDAO.getSucessCount(4356, "F");
		subventionUploadDAO.getSucessCount(0, "jh");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testIsFileExcist() {
		subventionUploadDAO.isFileExists("Subventionreq_60working file.xlsx");
		subventionUploadDAO.isFileExists("jhgt");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testGetErrorDetails() {
		ErrorDetail e = new ErrorDetail();
		e.setCode(null);
		List<ErrorDetail> er = new ArrayList<ErrorDetail>();
		er.add(e);
		subventionUploadDAO.logSubvention(er, (long) 1);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateRemark() {
		SubventionHeader sb = new SubventionHeader();
		sb.setId((long) 7);
		subventionUploadDAO.updateRemarks(sb);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testListSub() {
		Subvention ss = new Subvention();
		ss.setId((long) 5);
		ss.setFinID(4356);
		List<Subvention> s = new ArrayList<Subvention>();
		s.add(ss);
		subventionUploadDAO.saveSubvention(s, 5);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testupdateDeRemarks() {
		SubventionHeader sh = new SubventionHeader();
		sh.setId((long) 103);
		sh.setSucessRecords(6);
		sh.setFailureRecords(123);
		sh.setBatchRef("wert");
		DataEngineStatus de = new DataEngineStatus();
		de.setRemarks("re");
		de.setStatus("t");
		subventionUploadDAO.updateDeRemarks(sh, de);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testGst() {

		subventionUploadDAO.getGstDetails(4356);
		subventionUploadDAO.getGstDetails(98765432);
	}

}
