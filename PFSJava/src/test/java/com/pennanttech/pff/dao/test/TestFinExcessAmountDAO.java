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

import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinExcessAmountReserve;
import com.pennant.backend.model.finance.FinExcessMovement;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.presentment.model.PresentmentDetail;

@ContextConfiguration(locations = "classpath:dao-test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class TestFinExcessAmountDAO {

	@Autowired
	private FinExcessAmountDAO finExcessAmountDAO;

	private FinExcessAmount fe;
	private FinExcessMovement fm;

	@Test
	@Transactional
	@Rollback(true)
	public void testGetExcessAmountsByRef() {
		List<FinExcessAmount> list = finExcessAmountDAO.getExcessAmountsByRef(637);

		finExcessAmountDAO.updateExcess(list.get(0));

		finExcessAmountDAO.getFinExcessAmount(2);
		finExcessAmountDAO.getFinExcessAmount(-1);

		finExcessAmountDAO.isFinExcessAmtExists(3468);
		finExcessAmountDAO.isFinExcessAmtExists(1);

		fe = finExcessAmountDAO.getFinExcessAmount(3468, "E");
		finExcessAmountDAO.getFinExcessAmount(367, "");

		finExcessAmountDAO.getFinExcessAmount(822, 5088);
		finExcessAmountDAO.getFinExcessAmount(3032, 10161);

		finExcessAmountDAO.updateUtilise(351, new BigDecimal(100));

		finExcessAmountDAO.updateUtiliseOnly(351, new BigDecimal(100));
		finExcessAmountDAO.updateExcessBal(294, new BigDecimal(10000));

		finExcessAmountDAO.updateExcessBalByRef(112, "E", new BigDecimal(10000));

		finExcessAmountDAO.updateExcessReserve(377, new BigDecimal(10000));

		finExcessAmountDAO.getExcessReserve(890, 88);
		finExcessAmountDAO.getExcessReserve(891, 88);

		finExcessAmountDAO.getExcessReserveList(58);

		finExcessAmountDAO.updateExcessReserveLog(646, 107, new BigDecimal(10000), "R");

		finExcessAmountDAO.deleteExcessReserve(122, 300, "R");
		finExcessAmountDAO.deleteExcessReserve(122, 0, "R");

		finExcessAmountDAO.updateExcessAmount(340, "R", new BigDecimal(10000));
		finExcessAmountDAO.updateExcessAmount(340, "U", new BigDecimal(10000));

		finExcessAmountDAO.updateExcessAmount(444, new BigDecimal(10000));

		finExcessAmountDAO.getExcessAmountsByRefAndType(1197, "DSF");
		finExcessAmountDAO.getExcessAmountsByRefAndType(1197, "A");

		finExcessAmountDAO.getAllExcessAmountsByRef(1197, "");

		finExcessAmountDAO.deductExcessReserve(300, new BigDecimal(10000));

		finExcessAmountDAO.updateExcessReserveByRef(543, "ADVINT", new BigDecimal(10000));

		finExcessAmountDAO.updExcessAfterRealize(3049, "E", new BigDecimal(10000));

		Date dt = DateUtil.getSysDate();

		finExcessAmountDAO.getFinExcessMovement(518, "UPFRONT", dt);
		finExcessAmountDAO.getFinExcessMovement(500, "UPFRONT", dt);

		finExcessAmountDAO.getFinExcessByID(298);

		finExcessAmountDAO.deleteMovemntByPrdID(5);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testSaveExcess() {
		fe = new FinExcessAmount();
		fe = finExcessAmountDAO.getFinExcessAmount(2671, "CASHCLT");
		fe.setId(0);
		fe.setExcessID(fe.getExcessID() + 1);
		fe.setFinID(5353);
		finExcessAmountDAO.saveExcess(fe);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testSaveExcess1() {
		fe = new FinExcessAmount();
		fe = finExcessAmountDAO.getFinExcessAmount(2671, "CASHCLT");
		fe.setId(Long.MIN_VALUE);
		fe.setExcessID(fe.getExcessID() + 2);
		fe.setFinID(5353);
		finExcessAmountDAO.saveExcess(fe);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testSaveExcess2() {
		fe = new FinExcessAmount();
		fe = finExcessAmountDAO.getFinExcessAmount(2671, "CASHCLT");
		fe.setId(1431);
		fe.setExcessID(1431);
		fe.setFinID(5353);
		finExcessAmountDAO.saveExcess(fe);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateUtilizedAndBalance() {
		fe = new FinExcessAmount();
		fe = finExcessAmountDAO.getFinExcessAmount(2671, "CASHCLT");
		fe.setId(Long.MIN_VALUE);
		fe.setExcessID(fe.getExcessID() + 2);
		fe.setFinID(5353);
		finExcessAmountDAO.updateUtilizedAndBalance(fe);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateExcessReserve1() {
		fe = new FinExcessAmount();
		fe = finExcessAmountDAO.getFinExcessAmount(2671, "CASHCLT");
		fe.setId(Long.MIN_VALUE);
		fe.setExcessID(fe.getExcessID() + 1);
		fe.setFinID(5353);
		finExcessAmountDAO.updateExcessReserve(fe);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateReserveUtilization() {
		fe = new FinExcessAmount();
		fe = finExcessAmountDAO.getFinExcessAmount(2671, "CASHCLT");
		fe.setId(Long.MIN_VALUE);
		fe.setExcessID(fe.getExcessID() + 1);
		fe.setFinID(5353);
		finExcessAmountDAO.updateReserveUtilization(fe);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testsaveExcessList() {
		List<FinExcessAmount> list = new ArrayList<FinExcessAmount>();
		fe = new FinExcessAmount();
		fe = finExcessAmountDAO.getFinExcessAmount(2671, "CASHCLT");
		fe.setId(Long.MIN_VALUE);
		fe.setExcessID(fe.getExcessID() + 2);
		fe.setFinID(5353);
		list.add(fe);
		finExcessAmountDAO.saveExcessList(list);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateExcessReserveList() {
		List<FinExcessAmount> list = new ArrayList<FinExcessAmount>();
		fe = new FinExcessAmount();
		fe = finExcessAmountDAO.getFinExcessAmount(2671, "CASHCLT");
		fe.setId(Long.MIN_VALUE);
		fe.setExcessID(fe.getExcessID() + 2);
		fe.setFinID(5353);
		list.add(fe);
		finExcessAmountDAO.updateExcessReserveList(list);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateExcessEMIAmount() {
		List<FinExcessAmount> list = new ArrayList<FinExcessAmount>();
		fe = new FinExcessAmount();
		fe = finExcessAmountDAO.getFinExcessAmount(2671, "CASHCLT");
		fe.setId(Long.MIN_VALUE);
		fe.setExcessID(fe.getExcessID() + 2);
		fe.setFinID(5353);
		list.add(fe);
		finExcessAmountDAO.updateExcessEMIAmount(list, "R");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateExcessEMIAmount1() {
		List<FinExcessAmount> list = new ArrayList<FinExcessAmount>();
		fe = new FinExcessAmount();
		fe = finExcessAmountDAO.getFinExcessAmount(2671, "CASHCLT");
		fe.setId(Long.MIN_VALUE);
		fe.setExcessID(fe.getExcessID() + 2);
		fe.setFinID(5353);
		list.add(fe);
		finExcessAmountDAO.updateExcessEMIAmount(list, "U");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateExcessEMIAmount2() {
		// Amount Type Not Of U & R
		List<FinExcessAmount> list = new ArrayList<FinExcessAmount>();
		fe = new FinExcessAmount();
		fe = finExcessAmountDAO.getFinExcessAmount(2671, "CASHCLT");
		fe.setId(Long.MIN_VALUE);
		fe.setExcessID(fe.getExcessID() + 2);
		fe.setFinID(5353);
		list.add(fe);
		finExcessAmountDAO.updateExcessEMIAmount(list, "E");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateExcessAmtList() {
		List<FinExcessAmount> list = new ArrayList<FinExcessAmount>();
		fe = new FinExcessAmount();
		fe = finExcessAmountDAO.getFinExcessAmount(2671, "CASHCLT");
		fe.setId(Long.MIN_VALUE);
		fe.setExcessID(fe.getExcessID() + 2);
		fe.setFinID(5353);
		list.add(fe);
		finExcessAmountDAO.updateExcessAmtList(list);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testBatchUpdateExcessAmount() {
		List<PresentmentDetail> list = new ArrayList<PresentmentDetail>();
		PresentmentDetail pd = new PresentmentDetail();
		pd = new PresentmentDetail();
		pd.setAdvanceAmt(new BigDecimal(10000));
		pd.setExcessID(1430);
		list.add(pd);
		finExcessAmountDAO.batchUpdateExcessAmount(list);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testSaveExcessMovement() {
		// Issue in getFinExcessMovement(for SchDate parameter)
		fm = new FinExcessMovement();
		fm = finExcessAmountDAO.getFinExcessMovement(518, "UPFRONT", null);
		finExcessAmountDAO.saveExcessMovement(fm);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testSaveExcessMovements() {
		// Issue in getFinExcessMovement(for SchDate parameter)
		List<FinExcessMovement> list = new ArrayList<FinExcessMovement>();
		fm = new FinExcessMovement();
		fm = finExcessAmountDAO.getFinExcessMovement(518, "UPFRONT", null);
		list.add(fm);
		finExcessAmountDAO.saveExcessMovements(list);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testSaveExcessMovementList() {
		// Issue in getFinExcessMovement(for SchDate parameter)
		List<FinExcessMovement> list = new ArrayList<FinExcessMovement>();
		fm = new FinExcessMovement();
		fm = finExcessAmountDAO.getFinExcessMovement(518, "UPFRONT", null);
		list.add(fm);
		finExcessAmountDAO.saveExcessMovementList(list);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testSaveExcessReserveLog() {
		FinExcessAmountReserve fear = new FinExcessAmountReserve();
		fear.setExcessID(1370);
		fear.setReceiptSeqID(10015);
		fear.setPaymentType("R");
		fear.setReservedAmt(new BigDecimal(10000));
		finExcessAmountDAO.saveExcessReserveLog(fear.getReceiptSeqID(), fear.getExcessID(), fear.getReservedAmt(),
				fear.getPaymentType());
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateUtilise() {
		// record count
		finExcessAmountDAO.updateUtilise(351, new BigDecimal(92000));
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateExcessBal() {
		// record count
		finExcessAmountDAO.updateExcessBal(200, new BigDecimal(10000));

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateExcessReserve() {
		// record count
		finExcessAmountDAO.updateExcessReserve(295, new BigDecimal(210000));
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateExcessReserveLog() {
		// record count
		finExcessAmountDAO.updateExcessReserveLog(646, 107, new BigDecimal(10000), "P");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateExcessAmount() {
		// record count
		finExcessAmountDAO.updateExcessAmount(200, "R", new BigDecimal(10000));
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateExcessAmount1() {
		// Amount Type Not Of U & R
		finExcessAmountDAO.updateExcessAmount(340, "E", new BigDecimal(10000));
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testDeductExcessReserve() {
		// record count
		finExcessAmountDAO.deductExcessReserve(200, new BigDecimal(10000));
	}
}
