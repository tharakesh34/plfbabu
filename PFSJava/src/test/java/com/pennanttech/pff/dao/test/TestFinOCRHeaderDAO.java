package com.pennanttech.pff.dao.test;

import java.math.BigDecimal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.pennant.backend.dao.finance.FinOCRHeaderDAO;
import com.pennant.backend.model.finance.FinOCRHeader;

@ContextConfiguration(locations = "classpath:dao-test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)

public class TestFinOCRHeaderDAO {

	@Autowired
	private FinOCRHeaderDAO finOCRHeaderDAO;
	private FinOCRHeader ocrh;

	// =========byparentref=========//
	@Test
	@Transactional
	@Rollback(true)
	public void testref() {
		ocrh = new FinOCRHeader();
		finOCRHeaderDAO.getFinOCRHeaderByRef("1500AGR0008815", "");
		finOCRHeaderDAO.getFinOCRHeaderByRef("abcd", "");

	}

	// =========byref=========//
	@Test
	@Transactional
	@Rollback(true)
	public void test() {
		ocrh = new FinOCRHeader();
		finOCRHeaderDAO.getFinOCRHeaderByRef(3574, "");
		finOCRHeaderDAO.getFinOCRHeaderByRef(5678, "");

	}

	// =============byid==========//

	@Test
	@Transactional
	@Rollback(true)
	public void testById() {
		ocrh = new FinOCRHeader();
		finOCRHeaderDAO.getFinOCRHeaderById(20, "");

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testById1() {
		ocrh = new FinOCRHeader();
		finOCRHeaderDAO.getFinOCRHeaderById(0, "");

	}

	// ====save=======//

	@Test
	@Transactional
	@Rollback(true)
	public void testSave() {
		ocrh = new FinOCRHeader();
		ocrh.setFinReference("1500BUS0003280");
		ocrh.setFinID(5354);
		ocrh.setHeaderID(94);
		ocrh.setOcrID("12");
		ocrh.setOcrDescription("PRORATA");
		ocrh.setCustomerPortion(new BigDecimal(700));
		ocrh.setOcrType("PRORATA");
		finOCRHeaderDAO.save(ocrh, "");

	}

	// =====update===========//
	@Test
	@Transactional
	@Rollback(true)
	public void testupdate() {
		ocrh = new FinOCRHeader();
		ocrh.setFinReference("1500BUS0003280");
		ocrh.setFinID(5354);
		ocrh.setHeaderID(94);
		ocrh.setOcrID("12");
		ocrh.setOcrDescription("PRORATA");
		ocrh.setCustomerPortion(new BigDecimal(700));
		ocrh.setOcrType("PRORATA");
		finOCRHeaderDAO.save(ocrh, "");
		finOCRHeaderDAO.update(ocrh, "");
	}

	// =======delete=========//
	@Test
	@Transactional
	@Rollback(true)
	public void testDelete() {
		ocrh = new FinOCRHeader();
		ocrh.setFinReference("");
		ocrh.setHeaderID(20);
		ocrh.setOcrID("");
		ocrh.setOcrDescription("");
		ocrh.setCustomerPortion(new BigDecimal(7800));
		ocrh.setOcrType("");
		finOCRHeaderDAO.delete(ocrh, "");

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdate1() {
		ocrh = new FinOCRHeader();
		ocrh.setFinReference("1500BUS0003280");
		ocrh.setFinID(5354);
		ocrh.setHeaderID(94);
		ocrh.setOcrID("12");
		ocrh.setOcrDescription("PRORATA");
		ocrh.setCustomerPortion(new BigDecimal(700));
		ocrh.setOcrType("PRORATA");
		finOCRHeaderDAO.update(ocrh, ""); // for record count 0
	}

}
