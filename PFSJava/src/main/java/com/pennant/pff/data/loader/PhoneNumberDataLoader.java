package com.pennant.pff.data.loader;

import java.util.concurrent.CountDownLatch;

import com.pennant.backend.dao.customermasters.CustomerPhoneNumberDAO;
import com.pennant.backend.model.customermasters.CustomerDetails;

public class PhoneNumberDataLoader implements Runnable {
	private CustomerPhoneNumberDAO customerPhoneNumberDAO;
	private CountDownLatch latch;
	private long custID;
	private CustomerDetails cd;

	public PhoneNumberDataLoader(CountDownLatch latch, long custID, CustomerDetails cd) {
		this.latch = latch;
		this.custID = custID;
		this.cd = cd;
	}

	@Override
	public void run() {
		try {
			this.cd.setCustomerPhoneNumList(customerPhoneNumberDAO.getCustomerPhoneNumberByCustomer(custID, "_AView"));
		} catch (Exception e) {
			//
		}

		latch.countDown();
	}

	public void setCustomerPhoneNumberDAO(CustomerPhoneNumberDAO customerPhoneNumberDAO) {
		this.customerPhoneNumberDAO = customerPhoneNumberDAO;
	}
}
