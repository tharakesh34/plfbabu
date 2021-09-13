package com.pennanttech.pff.dao.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.pennant.backend.dao.finance.financialSummary.RecommendationNotesDetailsDAO;
import com.pennant.backend.model.finance.financialsummary.RecommendationNotes;

@ContextConfiguration(locations = "classpath:dao-test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class TestRecommendationNotesDetailsDAO {

	@Autowired
	private RecommendationNotesDetailsDAO recommendationNotesDetailsDAO;

	@Test
	@Transactional
	@Rollback(true)
	public void TestSave() {
		RecommendationNotes rn = new RecommendationNotes();
		rn.setId(1);
		rn.setFinReference("1500BUS0003280");
		rn.setFinID(5354);
		rn.setParticularId(1);
		rn.setWorkflowId(0);
		recommendationNotesDetailsDAO.save(rn, "");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void TestSave1() {
		// DuplicateKeyException
		RecommendationNotes rn = new RecommendationNotes();
		rn.setId(1);
		rn.setFinReference("1500BUS0003280");
		rn.setFinID(5354);
		rn.setParticularId(1);
		rn.setWorkflowId(0);
		recommendationNotesDetailsDAO.save(rn, "");
		RecommendationNotes rn1 = new RecommendationNotes();
		rn1.setId(1);
		rn1.setFinReference("1500BUS0003280");
		rn1.setFinID(5354);
		rn1.setParticularId(1);
		rn1.setWorkflowId(0);
		recommendationNotesDetailsDAO.save(rn1, "");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void TestGetRecommendationNotesDetails() {
		RecommendationNotes rn = new RecommendationNotes();
		rn.setId(1);
		rn.setFinReference("1500AGR0005959");
		rn.setFinID(5345);
		rn.setParticularId(1);
		rn.setWorkflowId(0);
		recommendationNotesDetailsDAO.save(rn, "_Temp");
		recommendationNotesDetailsDAO.getRecommendationNotesDetails(5345);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void TestDelete() {
		RecommendationNotes rn = new RecommendationNotes();
		rn.setId(1);
		rn.setFinReference("1500BUS0003280");
		rn.setFinID(5354);
		rn.setParticularId(1);
		rn.setWorkflowId(0);
		recommendationNotesDetailsDAO.save(rn, "");
		recommendationNotesDetailsDAO.delete(rn, "");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void TestDelete1() {
		// recordCount <= 0
		RecommendationNotes rn = new RecommendationNotes();
		rn.setId(1);
		rn.setFinReference("1500BUS0003280");
		rn.setFinID(5354);
		rn.setParticularId(1);
		rn.setWorkflowId(0);
		recommendationNotesDetailsDAO.delete(rn, "");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void TestUpdate() {
		RecommendationNotes rn = new RecommendationNotes();
		rn.setId(1);
		rn.setFinReference("1500BUS0003280");
		rn.setFinID(5354);
		rn.setParticularId(1);
		rn.setWorkflowId(0);
		rn.setVersion(1);
		recommendationNotesDetailsDAO.save(rn, "");
		rn.setVersion(2);
		recommendationNotesDetailsDAO.update(rn, "");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void TestUpdate1() {
		RecommendationNotes rn = new RecommendationNotes();
		rn.setId(1);
		rn.setFinReference("1500BUS0003280");
		rn.setFinID(5354);
		rn.setParticularId(1);
		rn.setWorkflowId(0);
		rn.setVersion(1);
		recommendationNotesDetailsDAO.save(rn, "_Temp");
		recommendationNotesDetailsDAO.update(rn, "_Temp");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void TestUpdate2() {
		// recordCount <= 0
		RecommendationNotes rn = new RecommendationNotes();
		rn.setId(1);
		rn.setFinReference("1500BUS0003280");
		rn.setFinID(5354);
		rn.setParticularId(1);
		rn.setWorkflowId(0);
		rn.setVersion(1);
		recommendationNotesDetailsDAO.update(rn, "_Temp");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void TestGetVersion() {
		// Using finReference (Have to send finReference)
		RecommendationNotes rn = new RecommendationNotes();
		rn.setId(1);
		rn.setFinReference("1500BUS0003280");
		rn.setFinID(5354);
		rn.setParticularId(1);
		rn.setWorkflowId(0);
		rn.setVersion(1);
		recommendationNotesDetailsDAO.save(rn, "");
		recommendationNotesDetailsDAO.getVersion(1, "1500BUS0003280");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void TestGetRecommendationNotesConfigurationDetails() {
		recommendationNotesDetailsDAO.getRecommendationNotesConfigurationDetails();
	}

}
