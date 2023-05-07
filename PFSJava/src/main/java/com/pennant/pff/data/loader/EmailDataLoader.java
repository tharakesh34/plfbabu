package com.pennant.pff.data.loader;

import java.util.concurrent.CountDownLatch;

import com.pennant.backend.dao.customermasters.CustomerEMailDAO;
import com.pennant.backend.model.customermasters.CustomerDetails;

public class EmailDataLoader implements Runnable {
	private CustomerEMailDAO customerEMailDAO;
	private CountDownLatch latch;
	private long custID;
	private CustomerDetails cd;

	public EmailDataLoader(CountDownLatch latch, long custID, CustomerDetails cd) {
		this.latch = latch;
		this.custID = custID;
		this.cd = cd;
	}

	@Override
	public void run() {
		try {
			this.cd.setCustomerEMailList(customerEMailDAO.getCustomerEmailByCustomer(custID, "_AView"));
		} catch (Exception e) {
			//
		}

		latch.countDown();
	}

	public void setCustomerEMailDAO(CustomerEMailDAO customerEMailDAO) {
		this.customerEMailDAO = customerEMailDAO;
	}
}