package com.pennanttech.pff.receipt.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.service.finance.ReceiptService;

@ContextConfiguration(locations = "classpath:receipt-test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class TestReceiptTransactions {
	@Autowired
	private ReceiptService receiptService;

	@Test
	@Transactional
	@Rollback(true)
	public void testReceiptTransaction() {
		FinServiceInstruction fsi = new FinServiceInstruction();
		receiptService.receiptTransaction(fsi);
	}
}
