package com.pennanttech.pff.dao.test;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.pennant.backend.dao.commitment.CommitmentMovementDAO;
import com.pennant.backend.model.commitment.CommitmentMovement;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;

@ContextConfiguration(locations = "classpath:dao-test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class TestCommitmentMovementDAO {

	@Autowired
	private CommitmentMovementDAO commitmentMovementDAO;

	Date dt1 = DateUtil.parse("30/01/2021", DateFormat.SHORT_DATE);

	@Test
	@Transactional
	@Rollback(true)
	public void TestGetCommitmentMovement() {
		// Module Registration not available
		commitmentMovementDAO.getCommitmentMovement();
	}

	@Test
	@Transactional
	@Rollback(true)
	public void TestGetNewCommitmentMovement() {
		// Module Registration not available
		commitmentMovementDAO.getNewCommitmentMovement();
	}

	@Test
	@Transactional
	@Rollback(true)
	public void TestSave() {
		CommitmentMovement cm = new CommitmentMovement();
		cm.setCmtReference("123456");
		cm.setMovementDate(dt1);
		cm.setMovementType("S");
		cm.setVersion(1);
		cm.setId("1");
		cm.setFinID(5354);
		commitmentMovementDAO.save(cm, "");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void TestGetCommitmentMovementById() {
		CommitmentMovement cm = new CommitmentMovement();
		cm.setCmtReference("123456");
		cm.setMovementDate(dt1);
		cm.setMovementType("S");
		cm.setVersion(1);
		cm.setId("1");
		cm.setFinID(5354);
		cm.setMovementOrder(1);
		commitmentMovementDAO.save(cm, "");
		commitmentMovementDAO.getCommitmentMovementById("123456", "");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void TestGetCommitmentMovementById1() {
		CommitmentMovement cm = new CommitmentMovement();
		cm.setCmtReference("123456");
		cm.setMovementDate(dt1);
		cm.setMovementType("S");
		cm.setVersion(1);
		cm.setId("1");
		cm.setFinID(5354);
		cm.setMovementOrder(1);
		commitmentMovementDAO.save(cm, "");
		CommitmentMovement cm1 = new CommitmentMovement();
		cm1.setCmtReference("123456");
		cm1.setMovementDate(dt1);
		cm1.setMovementType("S");
		cm1.setVersion(1);
		cm1.setId("2");
		cm1.setFinID(5354);
		cm1.setMovementOrder(1);
		commitmentMovementDAO.save(cm1, "");
		commitmentMovementDAO.getCommitmentMovementById("123456", "");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void TestGetCommitmentMovementById2() {
		commitmentMovementDAO.getCommitmentMovementById("123456", "");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void TestDelete() {
		CommitmentMovement cm = new CommitmentMovement();
		cm.setCmtReference("123456");
		cm.setMovementDate(dt1);
		cm.setMovementType("S");
		cm.setVersion(1);
		cm.setId("1");
		cm.setFinID(5354);
		cm.setMovementOrder(1);
		commitmentMovementDAO.save(cm, "");
		commitmentMovementDAO.delete(cm, "");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void TestDelete1() {
		// recordCount <= 0
		CommitmentMovement cm = new CommitmentMovement();
		cm.setCmtReference("123456");
		cm.setMovementDate(dt1);
		cm.setMovementType("S");
		cm.setVersion(1);
		cm.setId("1");
		cm.setFinID(5354);
		cm.setMovementOrder(1);
		commitmentMovementDAO.delete(cm, "");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void TestDeleteByRef() {
		CommitmentMovement cm = new CommitmentMovement();
		cm.setCmtReference("123456");
		cm.setMovementDate(dt1);
		cm.setMovementType("S");
		cm.setVersion(1);
		cm.setId("1");
		cm.setFinID(5354);
		cm.setMovementOrder(1);
		commitmentMovementDAO.save(cm, "");
		commitmentMovementDAO.deleteByRef("123456", "");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void TestUpdate() {
		CommitmentMovement cm = new CommitmentMovement();
		cm.setCmtReference("123456");
		cm.setMovementDate(dt1);
		cm.setMovementType("S");
		cm.setVersion(1);
		cm.setId("1");
		cm.setFinID(5354);
		cm.setMovementOrder(1);
		commitmentMovementDAO.save(cm, "");
		cm.setVersion(2);
		commitmentMovementDAO.update(cm, "");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void TestUpdate1() {
		CommitmentMovement cm = new CommitmentMovement();
		cm.setCmtReference("123456");
		cm.setMovementDate(dt1);
		cm.setMovementType("S");
		cm.setVersion(1);
		cm.setId("1");
		cm.setFinID(5354);
		cm.setMovementOrder(1);
		commitmentMovementDAO.save(cm, "_Temp");
		commitmentMovementDAO.update(cm, "_Temp");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void TestUpdate2() {
		// recordCount <= 0
		CommitmentMovement cm = new CommitmentMovement();
		cm.setCmtReference("123456");
		cm.setMovementDate(dt1);
		cm.setMovementType("S");
		cm.setVersion(1);
		cm.setId("1");
		cm.setFinID(5354);
		cm.setMovementOrder(1);
		commitmentMovementDAO.update(cm, "_Temp");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void TestGetMaxMovementOrderByRef() {
		CommitmentMovement cm = new CommitmentMovement();
		cm.setCmtReference("123456");
		cm.setMovementDate(dt1);
		cm.setMovementType("S");
		cm.setVersion(1);
		cm.setId("1");
		cm.setFinID(5354);
		cm.setMovementOrder(1);
		commitmentMovementDAO.save(cm, "");
		commitmentMovementDAO.getMaxMovementOrderByRef("123456");
	}

}
