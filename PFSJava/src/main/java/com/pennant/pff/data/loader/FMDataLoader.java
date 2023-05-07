package com.pennant.pff.data.loader;

import java.util.concurrent.CountDownLatch;

import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.model.finance.FinanceDetail;

public class FMDataLoader implements Runnable {
	private FinanceMainDAO financeMainDAO;
	private CountDownLatch latch;
	private long finID;
	private FinanceDetail fd;

	public FMDataLoader(CountDownLatch latch, long finID, FinanceDetail fd) {
		this.latch = latch;
		this.finID = finID;
		this.fd = fd;
	}

	@Override
	public void run() {
		try {
			this.fd.getFinScheduleData().setFinanceMain(financeMainDAO.getFinanceMainById(finID, "_AView", false));
		} catch (Exception e) {
			//
		}

		latch.countDown();
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}
}
