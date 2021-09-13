package com.pennanttech.pff.dao.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.pennant.backend.dao.applicationmaster.VasMovementDAO;
import com.pennant.backend.model.finance.VasMovement;

@ContextConfiguration(locations = "classpath:dao-test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class TestVasMovementDAO {

	@Autowired
	private VasMovementDAO vasMovementDAO;

	@Test
	@Transactional
	@Rollback(true)
	public void testSave() {
		VasMovement vm = new VasMovement();
		vm.setId(1);
		vm.setFinID(5354);
		vasMovementDAO.save(vm, "");

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testSave1() {
		// SeqBMTCheckList is not updating
		VasMovement vm = new VasMovement();
		vm.setId(0);
		vm.setFinID(5354);
		vasMovementDAO.save(vm, "");

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testGetVasMovementById() {
		VasMovement vm = new VasMovement();
		vm.setId(1);
		vm.setFinID(5354);
		vasMovementDAO.save(vm, "");
		vasMovementDAO.getVasMovementById(5354, "");

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testGetVasMovementById1() {
		// FindId column does not exist in VasMovement_View
		VasMovement vm = new VasMovement();
		vm.setId(1);
		vm.setFinID(5354);
		vasMovementDAO.save(vm, "");
		vasMovementDAO.getVasMovementById(5354, "_View");

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testGetVasMovementById2() {
		vasMovementDAO.getVasMovementById(5354, "");

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testDelete() {
		VasMovement vm = new VasMovement();
		vm.setId(1);
		vm.setFinID(5354);
		vasMovementDAO.save(vm, "");
		vasMovementDAO.delete(vm, "");

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testDelete1() {
		// recordcount <= 0
		VasMovement vm = new VasMovement();
		vm.setId(1);
		vm.setFinID(5354);
		vasMovementDAO.delete(vm, "");

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdate() {
		VasMovement vm = new VasMovement();
		vm.setId(1);
		vm.setFinID(5354);
		vm.setVersion(1);
		vasMovementDAO.save(vm, "");
		vm.setVersion(2);
		vasMovementDAO.update(vm, "");

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdate1() {
		VasMovement vm = new VasMovement();
		vm.setId(1);
		vm.setFinID(5354);
		vm.setVersion(1);
		vasMovementDAO.save(vm, "_Temp");
		vm.setVersion(1);
		vasMovementDAO.update(vm, "_Temp");

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdate2() {
		// recordcount <= 0
		VasMovement vm = new VasMovement();
		vm.setId(1);
		vm.setFinID(5354);
		vm.setVersion(1);
		vasMovementDAO.update(vm, "");

	}
}
