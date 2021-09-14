package com.pennanttech.pff.dao.test;

import java.math.BigDecimal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.pennant.backend.dao.finance.FinOCRCaptureDAO;
import com.pennant.backend.model.finance.FinOCRCapture;

@ContextConfiguration(locations = "classpath:dao-test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)

public class TestFinOCRCaptureDAO {

	@Autowired
	private FinOCRCaptureDAO finOCRCaptureDAO;
	private FinOCRCapture ocr;

	// ===============getByRef=================//
	@Test
	@Transactional
	@Rollback(true)
	public void testByRef() {
		ocr = new FinOCRCapture();
		finOCRCaptureDAO.getFinOCRCaptureDetailsByRef(2933, "");

	}

	// =============DeleteList=========//
	@Test
	@Transactional
	@Rollback(true)
	public void testDeleteList() {
		finOCRCaptureDAO.deleteList(2933, "_Temp");
	}

	// =======================//
	@Test
	@Transactional
	@Rollback(true)
	public void test() {
		ocr = new FinOCRCapture();
		ocr.setId(65);
		ocr.setFinID(5354);
		ocr.setFinReference("");
		ocr.setDemandAmount(new BigDecimal(56700));
		ocr.setPaidAmount(new BigDecimal(4700));
		ocr.setDisbSeq(34);
		finOCRCaptureDAO.save(ocr, "");
		finOCRCaptureDAO.delete(ocr, "");
		finOCRCaptureDAO.update(ocr, "");

	}

}

/*
 * save-->List<FinOCRCapture> ocr = new ArrayList<FinOCRCapture>(); ocr =
 * finOCRCaptureDAO.getFinOCRCaptureDetailsByRef(2933, ""); finOCRCaptureDAO.save(ocr, "");
 */
