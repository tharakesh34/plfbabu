package com.pennanttech.pff.dao.test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.pennant.backend.dao.rulefactory.FinFeeChargesDAO;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;

@ContextConfiguration(locations = "classpath:dao-test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class TestFinFeeChargesDAO {

	@Autowired
	private FinFeeChargesDAO finFeeChargesDAO;
	Date dt1 = DateUtil.parse("01/02/2019", DateFormat.SHORT_DATE);

	@Test
	@Transactional
	@Rollback(true)
	public void testSave() {
		FeeRule fr = new FeeRule();
		fr.setFinID(1);
		fr.setFinReference("12");
		fr.setSchDate(dt1);
		fr.setFeeCode("abcd");
		fr.setSeqNo(3);
		fr.setFeeCodeDesc("23");
		fr.setFeeOrder(5);
		fr.setFeeAmount(new BigDecimal(234));
		fr.setWaiverAmount(new BigDecimal(24));
		fr.setPaidAmount(new BigDecimal(124));
		fr.setAllowWaiver(true);
		fr.setExcludeFromRpt(true);
		fr.setCalFeeModify(true);
		fr.setFinEvent("T");
		List<FeeRule> er = new ArrayList<FeeRule>();
		er.add(fr);
		finFeeChargesDAO.saveChargesBatch(er, true, "");
		finFeeChargesDAO.saveChargesBatch(er, false, "");

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testSaving() {
		FeeRule fr = new FeeRule();
		fr.setFinID(1);
		fr.setFinReference("12");
		fr.setSchDate(dt1);
		fr.setFeeCode("abcd");
		fr.setSeqNo(3);
		fr.setFeeCodeDesc("23");
		fr.setFeeOrder(5);
		fr.setFeeAmount(new BigDecimal(234));
		fr.setWaiverAmount(new BigDecimal(24));
		fr.setPaidAmount(new BigDecimal(124));
		fr.setAllowWaiver(true);
		fr.setExcludeFromRpt(true);
		fr.setCalFeeModify(true);
		fr.setFinEvent("T");
		List<FeeRule> er = new ArrayList<FeeRule>();
		er.add(fr);
		finFeeChargesDAO.saveChargesBatch(er, true, "");
		finFeeChargesDAO.getFeeChargesByFinRef(1, "T", true, "");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testSavings() {
		FeeRule fr = new FeeRule();
		fr.setFinID(1);
		fr.setFinReference("12");
		fr.setSchDate(dt1);
		fr.setFeeCode("abcd");
		fr.setSeqNo(3);
		fr.setFeeCodeDesc("23");
		fr.setFeeOrder(5);
		fr.setFeeAmount(new BigDecimal(234));
		fr.setWaiverAmount(new BigDecimal(24));
		fr.setPaidAmount(new BigDecimal(124));
		fr.setAllowWaiver(true);
		fr.setExcludeFromRpt(true);
		fr.setCalFeeModify(true);
		fr.setFinEvent("T");
		List<FeeRule> er = new ArrayList<FeeRule>();
		er.add(fr);
		finFeeChargesDAO.saveChargesBatch(er, true, "");
		finFeeChargesDAO.getFeeChargesByFinRef(1, "T", true, "_Temp");// The column name ExcludeFromRpt was not found in
																		// this ResultSet.
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testGet() {
		FeeRule fr = new FeeRule();
		fr.setFinID(1);
		fr.setFinReference("12");
		fr.setSchDate(dt1);
		fr.setFeeCode("abcd");
		fr.setSeqNo(3);
		fr.setFeeCodeDesc("23");
		fr.setFeeOrder(5);
		fr.setFeeAmount(new BigDecimal(234));
		fr.setWaiverAmount(new BigDecimal(24));
		fr.setPaidAmount(new BigDecimal(124));
		fr.setAllowWaiver(true);
		fr.setExcludeFromRpt(true);
		fr.setCalFeeModify(true);
		fr.setFinEvent("T");
		List<FeeRule> er = new ArrayList<FeeRule>();
		er.add(fr);
		finFeeChargesDAO.saveChargesBatch(er, true, "");
		finFeeChargesDAO.getFeeChargesByFinRefAndFee(1, "abcd", "");// Incorrect result size: expected 1, actual 0
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdate() {
		FeeRule fr = new FeeRule();
		fr.setFinID(1);
		fr.setFinReference("12");
		fr.setSchDate(dt1);
		fr.setFeeCode("abcd");
		fr.setSeqNo(3);
		fr.setFeeCodeDesc("23");
		fr.setFeeOrder(5);
		fr.setFeeAmount(new BigDecimal(234));
		fr.setWaiverAmount(new BigDecimal(24));
		fr.setPaidAmount(new BigDecimal(124));
		fr.setAllowWaiver(true);
		fr.setExcludeFromRpt(true);
		fr.setCalFeeModify(true);
		fr.setFinEvent("T");
		List<FeeRule> er = new ArrayList<FeeRule>();
		er.add(fr);
		finFeeChargesDAO.saveChargesBatch(er, true, "");
		finFeeChargesDAO.updateFeeChargesByFinRefAndFee(fr, "");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testDelete() {
		FeeRule fr = new FeeRule();
		fr.setFinID(1);
		fr.setFinReference("12");
		fr.setSchDate(dt1);
		fr.setFeeCode("abcd");
		fr.setSeqNo(3);
		fr.setFeeCodeDesc("23");
		fr.setFeeOrder(5);
		fr.setFeeAmount(new BigDecimal(234));
		fr.setWaiverAmount(new BigDecimal(24));
		fr.setPaidAmount(new BigDecimal(124));
		fr.setAllowWaiver(true);
		fr.setExcludeFromRpt(true);
		fr.setCalFeeModify(true);
		fr.setFinEvent("T");
		List<FeeRule> er = new ArrayList<FeeRule>();
		er.add(fr);
		finFeeChargesDAO.saveChargesBatch(er, true, "");
		finFeeChargesDAO.deleteChargesBatch(1, "T", true, "");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testDeleteFalse() {
		FeeRule fr = new FeeRule();
		fr.setFinID(1);
		fr.setFinReference("12");
		fr.setSchDate(dt1);
		fr.setFeeCode("abcd");
		fr.setSeqNo(3);
		fr.setFeeCodeDesc("23");
		fr.setFeeOrder(5);
		fr.setFeeAmount(new BigDecimal(234));
		fr.setWaiverAmount(new BigDecimal(24));
		fr.setPaidAmount(new BigDecimal(124));
		fr.setAllowWaiver(true);
		fr.setExcludeFromRpt(true);
		fr.setCalFeeModify(true);
		fr.setFinEvent("T");
		List<FeeRule> er = new ArrayList<FeeRule>();
		er.add(fr);
		finFeeChargesDAO.saveChargesBatch(er, true, "");
		finFeeChargesDAO.deleteChargesBatch(1, "T", false, "");
	}

}
