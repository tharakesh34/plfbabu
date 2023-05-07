package com.pennant.pff.data.loader;

import java.util.concurrent.CountDownLatch;

import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennanttech.pff.core.TableType;

public class ScheduleDataLoader implements Runnable {
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private CountDownLatch latch;
	private long finID;
	private FinanceDetail fd;

	public ScheduleDataLoader(CountDownLatch latch, long finID, FinanceDetail fd) {
		this.latch = latch;
		this.finID = finID;
		this.fd = fd;
	}

	@Override
	public void run() {
		try {
			fd.getFinScheduleData()
					.setFinanceScheduleDetails(financeScheduleDetailDAO.getFinSchedules(finID, TableType.AVIEW));
		} catch (Exception e) {
			//
		}

		latch.countDown();
	}

	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

}
