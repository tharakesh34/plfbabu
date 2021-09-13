package com.pennanttech.pff.dao.test;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.pennant.backend.model.finance.InstBasedSchdDetails;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pff.logging.dao.InstBasedSchdDetailDAO;

@ContextConfiguration(locations = "classpath:dao-test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class TestInstBasedSchdDetailDAO {

	@Autowired
	private InstBasedSchdDetailDAO instBasedSchdDetailDAO;

	Date dt1 = DateUtil.parse("06/02/2022", DateFormat.SHORT_DATE);

	@Test
	@Transactional
	@Rollback(true)
	public void testSave() {
		InstBasedSchdDetails isd = new InstBasedSchdDetails();
		isd.setBatchId(2159);
		isd.setFinID(5354);
		isd.setFinReference("1500BUS0003280");
		isd.setDisbId(5956);
		isd.setRealizedDate(dt1);
		isd.setStatus("S");
		isd.setErrorDesc("");
		isd.setUserId(44713);
		isd.setDownloadedOn(dt1);
		isd.setDisbAmount(new BigDecimal(20000000));
		isd.setLinkedTranId(267386);
		instBasedSchdDetailDAO.save(isd);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testSave1() {
		// To Cover Exception
		InstBasedSchdDetails isd = new InstBasedSchdDetails();
		isd.setBatchId(2159);
		isd.setFinID(5354);
		isd.setDisbId(5956);
		isd.setRealizedDate(dt1);
		isd.setStatus("S");
		isd.setErrorDesc("");
		isd.setUserId(44713);
		isd.setDownloadedOn(dt1);
		isd.setDisbAmount(new BigDecimal(20000000));
		isd.setLinkedTranId(267386);
		instBasedSchdDetailDAO.save(isd);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdate() {
		InstBasedSchdDetails isd = new InstBasedSchdDetails();
		isd.setStatus("P");
		isd.setErrorDesc("");
		isd.setId(10);
		instBasedSchdDetailDAO.update(isd);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testDelete() {
		InstBasedSchdDetails isd = new InstBasedSchdDetails();
		isd.setFinID(137);
		isd.setDisbId(5955);
		instBasedSchdDetailDAO.delete(isd);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testGetFinanceIfApproved() {
		instBasedSchdDetailDAO.getFinanceIfApproved(137);
		instBasedSchdDetailDAO.getFinanceIfApproved(150);
	}

}
