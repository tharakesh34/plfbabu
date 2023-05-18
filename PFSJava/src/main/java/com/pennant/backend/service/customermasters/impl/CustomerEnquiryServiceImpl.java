package com.pennant.backend.service.customermasters.impl;

import java.util.Comparator;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import com.pennant.backend.dao.customermasters.CustomerAddresDAO;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.customermasters.CustomerEMailDAO;
import com.pennant.backend.dao.customermasters.CustomerEmploymentDetailDAO;
import com.pennant.backend.dao.customermasters.CustomerPhoneNumberDAO;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.service.customermasters.CustomerEnquiryService;
import com.pennant.backend.service.finance.impl.FinanceEnquiryServiceImpl;
import com.pennant.pff.data.loader.AddressDataLoader;
import com.pennant.pff.data.loader.CustomerDataLoader;
import com.pennant.pff.data.loader.EmailDataLoader;
import com.pennant.pff.data.loader.EmploymentDataLoader;
import com.pennant.pff.data.loader.PhoneNumberDataLoader;
import com.pennanttech.pennapps.core.resource.Literal;

public class CustomerEnquiryServiceImpl implements CustomerEnquiryService {
	private static final Logger logger = LogManager.getLogger(FinanceEnquiryServiceImpl.class);

	private CustomerDAO customerDAO;
	private CustomerAddresDAO customerAddresDAO;
	private CustomerPhoneNumberDAO customerPhoneNumberDAO;
	private CustomerEMailDAO customerEMailDAO;
	private CustomerEmploymentDetailDAO customerEmploymentDetailDAO;

	@Override
	public CustomerDetails getCustomerDetails(long custID) {
		logger.debug(Literal.ENTERING);

		CustomerDetails cd = new CustomerDetails();

		CountDownLatch latch = new CountDownLatch(5);

		SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor("getCustomerDetails");

		CustomerDataLoader cdLoader = new CustomerDataLoader(latch, custID, cd);
		cdLoader.setCustomerDAO(customerDAO);
		taskExecutor.execute(cdLoader);

		AddressDataLoader addressLoader = new AddressDataLoader(latch, custID, cd);
		addressLoader.setCustomerAddresDAO(customerAddresDAO);
		taskExecutor.execute(addressLoader);

		PhoneNumberDataLoader phoneDataLoader = new PhoneNumberDataLoader(latch, custID, cd);
		phoneDataLoader.setCustomerPhoneNumberDAO(customerPhoneNumberDAO);
		taskExecutor.execute(phoneDataLoader);

		EmailDataLoader emailDataLoader = new EmailDataLoader(latch, custID, cd);
		emailDataLoader.setCustomerEMailDAO(customerEMailDAO);
		taskExecutor.execute(emailDataLoader);

		EmploymentDataLoader employmentDataLoader = new EmploymentDataLoader(latch, custID, cd);
		employmentDataLoader.setCustomerEmploymentDetailDAO(customerEmploymentDetailDAO);
		taskExecutor.execute(employmentDataLoader);

		try {
			latch.await();
		} catch (InterruptedException e) {
			logger.warn("Interrupted!", e);
			Thread.currentThread().interrupt();
		}

		cd.setCustomer(cd.getCustomer());
		cd.setAddressList(cd.getAddressList().stream()
				.sorted(Comparator.comparingInt(CustomerAddres::getCustAddrPriority).reversed())
				.collect(Collectors.toList()));
		cd.setCustomerPhoneNumList(cd.getCustomerPhoneNumList().stream()
				.sorted(Comparator.comparingInt(CustomerPhoneNumber::getPhoneTypePriority).reversed())
				.collect(Collectors.toList()));
		cd.setCustomerEMailList(cd.getCustomerEMailList().stream()
				.sorted(Comparator.comparingInt(CustomerEMail::getCustEMailPriority).reversed())
				.collect(Collectors.toList()));
		cd.setEmploymentDetailsList(cd.getEmploymentDetailsList());

		logger.debug(Literal.LEAVING);

		return cd;
	}

	@Autowired
	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

	@Autowired
	public void setCustomerAddresDAO(CustomerAddresDAO customerAddresDAO) {
		this.customerAddresDAO = customerAddresDAO;
	}

	@Autowired
	public void setCustomerPhoneNumberDAO(CustomerPhoneNumberDAO customerPhoneNumberDAO) {
		this.customerPhoneNumberDAO = customerPhoneNumberDAO;
	}

	@Autowired
	public void setCustomerEMailDAO(CustomerEMailDAO customerEMailDAO) {
		this.customerEMailDAO = customerEMailDAO;
	}

	@Autowired
	public void setCustomerEmploymentDetailDAO(CustomerEmploymentDetailDAO customerEmploymentDetailDAO) {
		this.customerEmploymentDetailDAO = customerEmploymentDetailDAO;
	}

}
