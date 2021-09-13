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

import com.pennant.backend.dao.applicationmaster.BlackListCustomerDAO;
import com.pennant.backend.model.blacklist.BlackListCustomers;
import com.pennant.backend.model.blacklist.FinBlacklistCustomer;
import com.pennant.backend.model.blacklist.NegativeReasoncodes;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.App.Database;
import com.pennanttech.pff.core.TableType;

@ContextConfiguration(locations = "classpath:dao-test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class TestBlackListCustomerDAO {

	@Autowired
	private BlackListCustomerDAO blacklistCustomerDAO;

	@Test
	@Transactional
	@Rollback(true)
	public void testGetBlackListCustomers() {
		blacklistCustomerDAO.getBlackListCustomers();

		blacklistCustomerDAO.getBlacklistCustomerById("123", "");
		blacklistCustomerDAO.getBlacklistCustomerById("2145", "_View");
		blacklistCustomerDAO.getBlacklistCustomerById("109", "");

		blacklistCustomerDAO.getNewBlacklistCustomer();

		blacklistCustomerDAO.isDuplicateKey("3602", TableType.MAIN_TAB);
		blacklistCustomerDAO.isDuplicateKey("6724", TableType.TEMP_TAB);
		blacklistCustomerDAO.isDuplicateKey("6724", TableType.BOTH_TAB);
		blacklistCustomerDAO.isDuplicateKey("3", TableType.MAIN_TAB);

		blacklistCustomerDAO.deleteNegativeReasonList("3", TableType.MAIN_TAB);

		blacklistCustomerDAO.fetchOverrideBlackListData("1500AGR0006481", "CUST_DOB_RE", "3602");

		blacklistCustomerDAO.fetchFinBlackList(1423);

		blacklistCustomerDAO.deleteList(1423);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testMoveData() {
		blacklistCustomerDAO.moveData("1500AGR0006481", "");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testMoveData1() {
		// FinId column does not exist in FinBlackListDetail_pa
		blacklistCustomerDAO.moveData("1500AGR0006701", "_pa");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testSaveNegativeReason() {
		NegativeReasoncodes nrc = new NegativeReasoncodes();
		nrc.setId(1);
		blacklistCustomerDAO.saveNegativeReason(nrc, TableType.MAIN_TAB);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testSaveNegativeReason1() {
		// To Cover DuplicateKeyException
		NegativeReasoncodes nrc = new NegativeReasoncodes();
		nrc.setId(1);
		blacklistCustomerDAO.saveNegativeReason(nrc, TableType.MAIN_TAB);
		nrc.setId(1);
		blacklistCustomerDAO.saveNegativeReason(nrc, TableType.MAIN_TAB);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testSaveNegativeReason2() {
		// SeqNegativeReasonCodes is not updating
		NegativeReasoncodes nrc = new NegativeReasoncodes();
		nrc.setId(Long.MIN_VALUE);
		blacklistCustomerDAO.saveNegativeReason(nrc, TableType.MAIN_TAB);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testSaveList() {
		List<FinBlacklistCustomer> fbcList = new ArrayList<FinBlacklistCustomer>();
		FinBlacklistCustomer fbc = new FinBlacklistCustomer();
		fbc.setFinReference("1500BUS0003280");
		fbc.setCustCIF("1463");
		fbc.setSourceCIF("2998");
		fbc.setFinID(5354);
		fbcList.add(fbc);
		blacklistCustomerDAO.saveList(fbcList, "");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateList() {
		List<FinBlacklistCustomer> fbc = new ArrayList<FinBlacklistCustomer>();
		fbc = blacklistCustomerDAO.fetchFinBlackList(1423);
		blacklistCustomerDAO.updateList(fbc);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdate() {
		BlackListCustomers blc = new BlackListCustomers();
		blc = blacklistCustomerDAO.getBlacklistCustomerById("123", "");
		blc.setVersion(2);
		blacklistCustomerDAO.update(blc, TableType.MAIN_TAB);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdate1() {
		// record count == 0
		BlackListCustomers blc = new BlackListCustomers();
		blc = blacklistCustomerDAO.getBlacklistCustomerById("6724", "_temp");
		blacklistCustomerDAO.update(blc, TableType.TEMP_TAB);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testDelete() {
		BlackListCustomers blc = new BlackListCustomers();
		blc = blacklistCustomerDAO.getBlacklistCustomerById("123", "");
		blc.setVersion(2);
		blacklistCustomerDAO.delete(blc, TableType.MAIN_TAB);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testDelete1() {
		// record count == 0
		BlackListCustomers blc = new BlackListCustomers();
		blc = blacklistCustomerDAO.getBlacklistCustomerById("6724", "_temp");
		blacklistCustomerDAO.delete(blc, TableType.TEMP_TAB);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testSave() {
		BlackListCustomers blc = new BlackListCustomers();
		blc = blacklistCustomerDAO.getBlacklistCustomerById("123", "");
		blc.setCustCIF("2998");
		blacklistCustomerDAO.save(blc, TableType.MAIN_TAB);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testSave1() {
		// DuplicateKeyException
		BlackListCustomers blc = new BlackListCustomers();
		blc = blacklistCustomerDAO.getBlacklistCustomerById("123", "");
		blacklistCustomerDAO.save(blc, TableType.MAIN_TAB);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testDeleteNegativeReason() {
		NegativeReasoncodes nrc = new NegativeReasoncodes();
		nrc.setId(1);
		blacklistCustomerDAO.saveNegativeReason(nrc, TableType.MAIN_TAB);
		blacklistCustomerDAO.deleteNegativeReason(1, TableType.MAIN_TAB);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testGetNegativeReasonList() {
		NegativeReasoncodes nrc = new NegativeReasoncodes();
		nrc.setId(1);
		nrc.setBlackListCIF("123");
		blacklistCustomerDAO.saveNegativeReason(nrc, TableType.MAIN_TAB);
		blacklistCustomerDAO.getNegativeReasonList("123", "");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testFetchBlackListedCustomers() {
		BlackListCustomers blc = new BlackListCustomers();
		blc = blacklistCustomerDAO.getBlacklistCustomerById("123", "");
		App.DATABASE = Database.POSTGRES;
		blacklistCustomerDAO.fetchBlackListedCustomers(blc, "");
	}

}
