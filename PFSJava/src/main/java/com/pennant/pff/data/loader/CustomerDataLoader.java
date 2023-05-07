package com.pennant.pff.data.loader;

import java.util.concurrent.CountDownLatch;

import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennanttech.pff.core.TableType;

public class CustomerDataLoader implements Runnable {
	private CustomerDAO customerDAO;
	private CountDownLatch latch;
	private long custID;
	private CustomerDetails cd;

	public CustomerDataLoader(CountDownLatch latch, long custID, CustomerDetails cd) {
		this.latch = latch;
		this.custID = custID;
		this.cd = cd;
	}

	@Override
	public void run() {
		try {
			this.cd.setCustomer(customerDAO.getBasicDetails(custID, TableType.MAIN_TAB));
		} catch (Exception e) {
			//
		}

		latch.countDown();
	}

	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}
}
