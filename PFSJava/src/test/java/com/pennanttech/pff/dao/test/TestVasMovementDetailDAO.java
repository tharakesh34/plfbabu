package com.pennanttech.pff.dao.test;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.pennant.backend.dao.applicationmaster.VasMovementDetailDAO;
import com.pennant.backend.model.finance.VasMovementDetail;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;

@ContextConfiguration(locations = "classpath:dao-test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class TestVasMovementDetailDAO {

	@Autowired
	private VasMovementDetailDAO vasMovementDetailDAO;

	Date dt1 = DateUtil.parse("29/03/2021", DateFormat.SHORT_DATE);

	@Test
	@Transactional
	@Rollback(true)
	public void testSave() {
		VasMovementDetail vmd = new VasMovementDetail();
		vmd.setVasMovementId(1);
		vmd.setVasMovementDetailId(1);
		vmd.setVasReference("VAS1234567");
		vmd.setMovementDate(dt1);
		vmd.setFinID(5354);
		vasMovementDetailDAO.save(vmd, "");

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testGetVasMovementDetailById() {
		VasMovementDetail vmd = new VasMovementDetail();
		vmd.setVasMovementId(1);
		vmd.setVasMovementDetailId(1);
		vmd.setVasReference("VAS1234567");
		vmd.setMovementDate(dt1);
		vmd.setFinID(5354);
		vasMovementDetailDAO.save(vmd, "");
		vasMovementDetailDAO.getVasMovementDetailById(1, "");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testGetVasMovementDetailById1() {
		vasMovementDetailDAO.getVasMovementDetailById(1, "");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testDelete() {
		VasMovementDetail vmd = new VasMovementDetail();
		vmd.setVasMovementId(1);
		vmd.setVasMovementDetailId(1);
		vmd.setVasReference("VAS1234567");
		vmd.setMovementDate(dt1);
		vmd.setFinID(5354);
		vasMovementDetailDAO.save(vmd, "");
		vasMovementDetailDAO.delete(vmd, "");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testDelete1() {
		// recordcount <= 0
		VasMovementDetail vmd = new VasMovementDetail();
		vmd.setVasMovementId(1);
		vmd.setVasMovementDetailId(1);
		vmd.setVasReference("VAS1234567");
		vmd.setMovementDate(dt1);
		vmd.setFinID(5354);
		vasMovementDetailDAO.delete(vmd, "");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testDeleteById() {
		VasMovementDetail vmd = new VasMovementDetail();
		vmd.setVasMovementId(1);
		vmd.setVasMovementDetailId(1);
		vmd.setVasReference("VAS1234567");
		vmd.setMovementDate(dt1);
		vmd.setFinID(5354);
		vasMovementDetailDAO.save(vmd, "");
		vasMovementDetailDAO.delete(1, "");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdate() {
		VasMovementDetail vmd = new VasMovementDetail();
		vmd.setVasMovementId(1);
		vmd.setVasMovementDetailId(1);
		vmd.setVasReference("VAS1234567");
		vmd.setMovementDate(dt1);
		vmd.setFinID(5354);
		vmd.setVersion(1);
		vasMovementDetailDAO.save(vmd, "");
		vmd.setVersion(2);
		vasMovementDetailDAO.update(vmd, "");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdate1() {
		VasMovementDetail vmd = new VasMovementDetail();
		vmd.setVasMovementId(1);
		vmd.setVasMovementDetailId(1);
		vmd.setVasReference("VAS1234567");
		vmd.setMovementDate(dt1);
		vmd.setFinID(5354);
		vmd.setVersion(1);
		vasMovementDetailDAO.save(vmd, "_Temp");
		vmd.setVersion(2);
		vasMovementDetailDAO.update(vmd, "_Temp");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdate2() {
		// record count <= 0
		VasMovementDetail vmd = new VasMovementDetail();
		vmd.setVasMovementId(1);
		vmd.setVasMovementDetailId(1);
		vmd.setVasReference("VAS1234567");
		vmd.setMovementDate(dt1);
		vmd.setFinID(5354);
		vmd.setVersion(1);
		vasMovementDetailDAO.update(vmd, "_Temp");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testGetVasMovementDetailByRef() {
		VasMovementDetail vmd = new VasMovementDetail();
		vmd.setVasMovementId(1);
		vmd.setVasMovementDetailId(1);
		vmd.setVasReference("VAS1234567");
		vmd.setMovementDate(dt1);
		vmd.setFinID(5354);
		vmd.setVersion(1);
		vmd.setFinReference("1500BUS0003280");
		vasMovementDetailDAO.save(vmd, "");
		vasMovementDetailDAO.getVasMovementDetailByRef("1500BUS0003280", dt1, dt1, "");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testGetVasMovementDetailByRef1() {
		vasMovementDetailDAO.getVasMovementDetailByRef("1500BUS0003280", dt1, dt1, "");
	}
}
