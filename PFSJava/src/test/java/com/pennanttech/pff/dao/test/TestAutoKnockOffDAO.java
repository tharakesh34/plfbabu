package com.pennanttech.pff.dao.test;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.pennant.backend.dao.applicationmaster.AutoKnkOfFeeMappingDAO;
import com.pennant.backend.dao.applicationmaster.AutoKnockOffDAO;
import com.pennant.backend.dao.applicationmaster.LoanTypeKnockOffDAO;
import com.pennant.backend.model.finance.AutoKnockOff;
import com.pennant.backend.model.finance.AutoKnockOffFeeMapping;
import com.pennant.backend.model.finance.FinTypeKnockOff;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pff.core.TableType;

@ContextConfiguration(locations = "classpath:dao-test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class TestAutoKnockOffDAO {

	@Autowired
	private AutoKnockOffDAO autoKnockOffDAO;

	@Autowired
	private LoanTypeKnockOffDAO loanTypeKnockOffDAO;

	@Autowired
	private AutoKnkOfFeeMappingDAO autoKnkOfFeeMappingDAO;

	Date dt1 = DateUtil.parse("30/01/2034", DateFormat.SHORT_DATE);
	Date dt2 = DateUtil.parse("17/01/2034", DateFormat.SHORT_DATE);

	@Test
	@Transactional
	@Rollback(true)
	public void testGetKnockOffDetails() {

		List<AutoKnockOff> akf = autoKnockOffDAO.getKnockOffDetails(4819);
		List<AutoKnockOff> akf1 = autoKnockOffDAO.getKnockOffDetails(0);

		autoKnockOffDAO.getAutoKnockOffCode(26, TableType.MAIN_TAB);
		autoKnockOffDAO.getAutoKnockOffCode(1, TableType.MAIN_TAB);

		autoKnockOffDAO.getAutoKnockOffCode("ADVEMI", TableType.MAIN_TAB);
		autoKnockOffDAO.getAutoKnockOffCode("ADV", TableType.MAIN_TAB);

		autoKnockOffDAO.isDuplicateKey(26, "ADVEMI", TableType.MAIN_TAB);
		autoKnockOffDAO.isDuplicateKey(30, "123", TableType.TEMP_TAB);
		autoKnockOffDAO.isDuplicateKey(26, "ADVEMI", TableType.BOTH_TAB);
		autoKnockOffDAO.isDuplicateKey(2, "", TableType.BOTH_TAB);

		autoKnockOffDAO.logExcessForKnockOff(dt1, null, null);
		autoKnockOffDAO.logExcessForKnockOff(null, null, null);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testLogKnockOffDetails() {
		autoKnockOffDAO.logKnockOffDetails(dt2, "01");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testLogKnockOffDetails1() {
		autoKnockOffDAO.logKnockOffDetails(null, "00");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testSave() {
		AutoKnockOff akf = new AutoKnockOff();
		akf = autoKnockOffDAO.getAutoKnockOffCode(26, TableType.MAIN_TAB);
		akf.setId(31);
		akf.setCode("abc");
		autoKnockOffDAO.save(akf, TableType.MAIN_TAB);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testSave1() {
		// To Cover DuplicateKeyException
		AutoKnockOff akf = new AutoKnockOff();
		akf = autoKnockOffDAO.getAutoKnockOffCode(26, TableType.MAIN_TAB);
		akf.setId(26);
		akf.setCode("ADVEMI");
		autoKnockOffDAO.save(akf, TableType.MAIN_TAB);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdate() {
		AutoKnockOff akf = new AutoKnockOff();
		akf = autoKnockOffDAO.getAutoKnockOffCode(26, TableType.MAIN_TAB);
		autoKnockOffDAO.update(akf, TableType.MAIN_TAB);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdate1() {
		// To Cover ConcurrencyException
		AutoKnockOff akf = new AutoKnockOff();
		akf = autoKnockOffDAO.getAutoKnockOffCode(26, TableType.MAIN_TAB);
		akf.setId(31);
		autoKnockOffDAO.update(akf, TableType.MAIN_TAB);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testDelete() {
		// To Cover DependencyFoundException
		AutoKnockOff akf = new AutoKnockOff();
		akf = autoKnockOffDAO.getAutoKnockOffCode(26, TableType.MAIN_TAB);
		autoKnockOffDAO.delete(akf, TableType.MAIN_TAB);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testDelete1() {
		AutoKnockOff akf = new AutoKnockOff();
		FinTypeKnockOff ftk = new FinTypeKnockOff();
		ftk.setId(19);
		ftk.setKnockOffId(26);
		AutoKnockOffFeeMapping akof = new AutoKnockOffFeeMapping();
		akof.setId(57);
		akof.setFeeTypeId(75);
		akf = autoKnockOffDAO.getAutoKnockOffCode(26, TableType.MAIN_TAB);
		loanTypeKnockOffDAO.delete(ftk, "");
		autoKnkOfFeeMappingDAO.delete(akof, TableType.MAIN_TAB);
		autoKnockOffDAO.delete(akf, TableType.MAIN_TAB);
	}

}
