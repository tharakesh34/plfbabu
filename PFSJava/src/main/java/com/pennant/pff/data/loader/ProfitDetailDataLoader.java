package com.pennant.pff.data.loader;

import java.util.concurrent.CountDownLatch;

import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.model.finance.FinanceDetail;

public class ProfitDetailDataLoader implements Runnable {
	private FinanceProfitDetailDAO financeProfitDetailDAO;
	private CountDownLatch latch;
	private long finID;
	private FinanceDetail fd;

	public ProfitDetailDataLoader(CountDownLatch latch, long finID, FinanceDetail fd) {
		this.latch = latch;
		this.finID = finID;
		this.fd = fd;
	}

	@Override
	public void run() {
		try {
			this.fd.getFinScheduleData().setFinPftDeatil(financeProfitDetailDAO.getFinProfitDetailsById(finID));
		} catch (Exception e) {
			//
		}

		latch.countDown();
	}

	public void setFinanceProfitDetailDAO(FinanceProfitDetailDAO financeProfitDetailDAO) {
		this.financeProfitDetailDAO = financeProfitDetailDAO;
	}
}
