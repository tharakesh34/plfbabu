package com.pennanttech.pff.dao.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.pennant.backend.dao.collateral.CollateralSetupDAO;
import com.pennant.backend.model.collateral.CollateralSetup;

@ContextConfiguration(locations = "classpath:dao-test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class TestCollateralSetupDAO {

	@Autowired
	private CollateralSetupDAO collateralSetupDAO;

	@Test
	@Transactional
	@Rollback(true)
	public void testGetCollateralSetupByRef() {
		collateralSetupDAO.getCollateralSetupByRef("CT3234000001", "");
		collateralSetupDAO.getCollateralSetupByRef("CT3234000001", "_View");
		collateralSetupDAO.getCollateralSetupByRef("CT3234000000", "");

		collateralSetupDAO.getCollateralSetup("CT3234000001", 16, "");
		collateralSetupDAO.getCollateralSetup("CT3234000000", 16, "");

		collateralSetupDAO.getApprovedCollateralByCustId(16, "");

		collateralSetupDAO.getCollateralSetupByFinRef("", ""); // fetching data with finReference only

		collateralSetupDAO.getCollateralByRef("1500LAP0000030", 1, "");

		collateralSetupDAO.getVersion("CT3234000001", "");
		collateralSetupDAO.getVersion("CT3234000000", "");

		collateralSetupDAO.isCollateralInMaintenance("CT3234000001", "");
		collateralSetupDAO.isCollateralInMaintenance("CT3234000000", "");

		collateralSetupDAO.getCollateralCountByref("CT3234000001", "");
		collateralSetupDAO.getCollateralCountByref("CT3234000000", "");

		collateralSetupDAO.getCountByCollateralRef("CT3234000001");

		collateralSetupDAO.isCollReferenceExists("CT3234000001", "");
		collateralSetupDAO.isCollReferenceExists("CT3234000000", "");

		collateralSetupDAO.getCustomerIdByCollateral("CT3234000001");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testSave() {
		CollateralSetup cs = new CollateralSetup();
		cs = collateralSetupDAO.getCollateralSetupByRef("CT3403300006", "");
		cs.setCollateralRef(cs.getCollateralRef() + 1);
		cs.setFinID(5354);
		collateralSetupDAO.save(cs, "");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdate() {
		CollateralSetup cs = new CollateralSetup();
		cs = collateralSetupDAO.getCollateralSetupByRef("CT3403300006", "");
		collateralSetupDAO.update(cs, "");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdate1() {
		// recordCount <= 0
		CollateralSetup cs = new CollateralSetup();
		cs = collateralSetupDAO.getCollateralSetupByRef("CT3403300006", "");
		cs.setCollateralRef("CT3234000000");
		collateralSetupDAO.update(cs, "");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateCollReferene() {
		// column Seqno does not exist in SeqCollateralSetup
		collateralSetupDAO.updateCollReferene(4, 3);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateCollateralSetup() {
		CollateralSetup cs = new CollateralSetup();
		cs = collateralSetupDAO.getCollateralSetupByRef("CT3403300006", "");
		collateralSetupDAO.updateCollateralSetup(cs, "");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testDelete() {
		CollateralSetup cs = new CollateralSetup();
		cs = collateralSetupDAO.getCollateralSetupByRef("CT3403300006", "");
		collateralSetupDAO.delete(cs, "");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testDelete1() {
		// ConcurrencyException
		CollateralSetup cs = new CollateralSetup();
		cs = collateralSetupDAO.getCollateralSetupByRef("CT3403300006", "");
		cs.setCollateralRef("CT3403300000");
		collateralSetupDAO.delete(cs, "");
	}

}
