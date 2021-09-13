package com.pennanttech.pff.dao.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.pennant.backend.dao.collateralmark.CollateralMarkDAO;
import com.pennant.backend.model.collateral.FinCollateralMark;

@ContextConfiguration(locations = "classpath:dao-test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class TestCollateralMarkDAO {

	@Autowired
	private CollateralMarkDAO collateralMarkDAO;

	@Test
	@Transactional
	@Rollback(true)
	public void testSave() {
		FinCollateralMark fcm = new FinCollateralMark();
		fcm.setFinCollateralId(1);
		fcm.setFinReference("1500BUS0003280");
		fcm.setReferenceNum("123");
		fcm.setStatus("S");
		fcm.setDepositID("123");
		fcm.setFinID(5354);
		collateralMarkDAO.save(fcm);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testSave1() {
		// SeqCollateralMarkLog is not updating
		FinCollateralMark fcm = new FinCollateralMark();
		fcm.setFinReference("1500BUS0003280");
		fcm.setReferenceNum("123");
		fcm.setStatus("S");
		fcm.setDepositID("123");
		collateralMarkDAO.save(fcm);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testGetCollateralById() {
		FinCollateralMark fcm = new FinCollateralMark();
		fcm.setFinCollateralId(1);
		fcm.setFinReference("1500BUS0003280");
		fcm.setReferenceNum("123");
		fcm.setStatus("S");
		fcm.setDepositID("123");
		fcm.setFinID(5354);
		collateralMarkDAO.save(fcm);
		collateralMarkDAO.getCollateralById("123");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testGetCollateralById1() {
		collateralMarkDAO.getCollateralById("123");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testGetCollatDeMarkStatus() {
		FinCollateralMark fcm = new FinCollateralMark();
		fcm.setFinCollateralId(1);
		fcm.setFinReference("1500BUS0003280");
		fcm.setReferenceNum("123");
		fcm.setStatus("S");
		fcm.setDepositID("123");
		fcm.setFinID(5354);
		fcm.setProcessed(true);
		collateralMarkDAO.save(fcm);
		collateralMarkDAO.getCollatDeMarkStatus(5354, "S");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testGetCollatDeMarkStatus1() {
		collateralMarkDAO.getCollatDeMarkStatus(5354, "S");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testGetCollateralList() {
		FinCollateralMark fcm = new FinCollateralMark();
		fcm.setFinCollateralId(1);
		fcm.setFinReference("1500BUS0003280");
		fcm.setReferenceNum("123");
		fcm.setStatus("MARK");
		fcm.setDepositID("123");
		fcm.setFinID(5354);
		fcm.setProcessed(true);
		collateralMarkDAO.save(fcm);
		collateralMarkDAO.getCollateralList(5354);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testGetCollateralList1() {
		collateralMarkDAO.getCollateralList(5354);
	}
}
