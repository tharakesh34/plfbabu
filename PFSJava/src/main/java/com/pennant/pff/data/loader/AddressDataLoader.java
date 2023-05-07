package com.pennant.pff.data.loader;

import java.util.concurrent.CountDownLatch;

import com.pennant.backend.dao.customermasters.CustomerAddresDAO;
import com.pennant.backend.model.customermasters.CustomerDetails;

public class AddressDataLoader implements Runnable {
	private CustomerAddresDAO customerAddresDAO;
	private CountDownLatch latch;
	private long custID;
	private CustomerDetails cd;

	public AddressDataLoader(CountDownLatch latch, long custID, CustomerDetails cd) {
		this.latch = latch;
		this.custID = custID;
		this.cd = cd;
	}

	@Override
	public void run() {
		try {
			this.cd.setAddressList(customerAddresDAO.getCustomerAddresByCustomer(custID, "_AView"));
		} catch (Exception e) {
			//
		}

		latch.countDown();
	}

	public void setCustomerAddresDAO(CustomerAddresDAO customerAddresDAO) {
		this.customerAddresDAO = customerAddresDAO;
	}

}
