package com.pennant.pff.data.loader;

import java.util.concurrent.CountDownLatch;

import com.pennant.backend.dao.customermasters.CustomerEmploymentDetailDAO;
import com.pennant.backend.model.customermasters.CustomerDetails;

public class EmploymentDataLoader implements Runnable {
	private CustomerEmploymentDetailDAO customerEmploymentDetailDAO;
	private CountDownLatch latch;
	private long custID;
	private CustomerDetails cd;

	public EmploymentDataLoader(CountDownLatch latch, long custID, CustomerDetails cd) {
		this.latch = latch;
		this.custID = custID;
		this.cd = cd;
	}

	@Override
	public void run() {
		try {
			this.cd.setEmploymentDetailsList(
					customerEmploymentDetailDAO.getCustomerEmploymentDetailsByID(custID, "_AView"));
		} catch (Exception e) {
			//
		}

		latch.countDown();
	}

	public void setCustomerEmploymentDetailDAO(CustomerEmploymentDetailDAO customerEmploymentDetailDAO) {
		this.customerEmploymentDetailDAO = customerEmploymentDetailDAO;
	}
}