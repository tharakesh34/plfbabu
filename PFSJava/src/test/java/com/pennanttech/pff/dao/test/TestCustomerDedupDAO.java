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

import com.pennant.backend.dao.custdedup.CustomerDedupDAO;
import com.pennant.backend.model.customermasters.CustomerDedup;

@ContextConfiguration(locations = "classpath:dao-test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class TestCustomerDedupDAO {

	@Autowired
	private CustomerDedupDAO customerDedupDAO;

	@Test
	@Transactional
	@Rollback(true)
	public void TestSaveList() {
		List<CustomerDedup> cdList = new ArrayList<CustomerDedup>();
		CustomerDedup cd = new CustomerDedup();
		cd.setCustCIF("2998");
		cd.setFinReference("1500BUS0003280");
		cd.setOverride(false);
		cd.setFinID(5354);
		cdList.add(cd);
		customerDedupDAO.saveList(cdList, "");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void TestUpdateList() {
		List<CustomerDedup> cdList = new ArrayList<CustomerDedup>();
		CustomerDedup cd = new CustomerDedup();
		cd.setCustCIF("2998");
		cd.setFinReference("1500BUS0003280");
		cd.setOverride(false);
		cd.setFinID(5354);
		cdList.add(cd);
		customerDedupDAO.saveList(cdList, "");
		customerDedupDAO.updateList(cdList);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void TestFetchOverrideCustDedupData() {
		List<CustomerDedup> cdList = new ArrayList<CustomerDedup>();
		CustomerDedup cd = new CustomerDedup();
		cd.setCustCIF("2998");
		cd.setFinReference("1500BUS0003280");
		cd.setOverride(false);
		cd.setFinID(5354);
		cd.setDedupRule("");
		cd.setModule("");
		cdList.add(cd);
		customerDedupDAO.saveList(cdList, "");
		customerDedupDAO.fetchOverrideCustDedupData("1500BUS0003280", "", "");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void TestFetchCustomerDedupDetails() {
		List<CustomerDedup> cdList = new ArrayList<CustomerDedup>();
		CustomerDedup cd = new CustomerDedup();
		cd.setCustCIF("2998");
		cd.setFinReference("1500BUS0003280");
		cd.setOverride(false);
		cd.setFinID(5354);
		cd.setDedupRule("");
		cd.setModule("");
		cdList.add(cd);
		customerDedupDAO.saveList(cdList, "");
		customerDedupDAO.fetchCustomerDedupDetails(cd, "");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void TestFetchCustomerDedupDetails1() {
		List<CustomerDedup> cdList = new ArrayList<CustomerDedup>();
		CustomerDedup cd = new CustomerDedup();
		cd.setCustCIF("2998");
		cd.setFinReference("1500BUS0003280");
		cd.setOverride(false);
		cd.setFinID(5354);
		cd.setDedupRule("");
		cd.setModule("");
		cdList.add(cd);
		customerDedupDAO.saveList(cdList, "");
		customerDedupDAO.fetchCustomerDedupDetails(cd, "abc");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void TestMoveData() {
		// Fetching data with finReference
		List<CustomerDedup> cdList = new ArrayList<CustomerDedup>();
		CustomerDedup cd = new CustomerDedup();
		cd.setCustCIF("2998");
		cd.setFinReference("1500BUS0003280");
		cd.setOverride(false);
		cd.setFinID(5354);
		cd.setDedupRule("");
		cd.setModule("");
		cdList.add(cd);
		customerDedupDAO.saveList(cdList, "");
		customerDedupDAO.moveData("1500BUS0003280", "");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void TestMoveData1() {
		// FinId does not exist in CustomerDedupDetail_pa
		List<CustomerDedup> cdList = new ArrayList<CustomerDedup>();
		CustomerDedup cd = new CustomerDedup();
		cd.setCustCIF("2998");
		cd.setFinReference("1500BUS0003280");
		cd.setOverride(false);
		cd.setFinID(5354);
		cd.setDedupRule("");
		cd.setModule("");
		cdList.add(cd);
		customerDedupDAO.saveList(cdList, "");
		customerDedupDAO.moveData("1500BUS0003280", "_pa");
	}

}
