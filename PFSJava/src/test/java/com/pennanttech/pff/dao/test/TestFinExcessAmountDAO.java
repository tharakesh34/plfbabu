package com.pennanttech.pff.dao.test;

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

@ContextConfiguration(locations = "classpath:dao-test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class TestFinExcessAmountDAO {

	@Autowired
	private FinExcessAmountDAO finExcessAmountDAO;

	@Test
	@Transactional
	@Rollback(true)
	public void testGetExcessAmountsByRef() {
		List<FinExcessAmount> list = finExcessAmountDAO.getExcessAmountsByRef(637);

		finExcessAmountDAO.updateExcess(list.get(0));

		finExcessAmountDAO.getFinExcessAmount(1);
		finExcessAmountDAO.isFinExcessAmtExists(637);
	}
}
