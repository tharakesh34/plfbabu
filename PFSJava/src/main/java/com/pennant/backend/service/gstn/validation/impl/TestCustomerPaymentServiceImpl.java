package com.pennant.backend.service.gstn.validation.impl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.external.CustomerPaymentService;

public class TestCustomerPaymentServiceImpl implements TestCustomerPaymentService {
	private static Logger logger = LogManager.getLogger(TestCustomerPaymentServiceImpl.class);

	@Autowired(required = false)
	private CustomerPaymentService customerPaymentService;

	@Override
	public void processOnlinePayment(List<FinAdvancePayments> finAdvPaymentList, String paymentType) {
		logger.debug(Literal.ENTERING);
		if (customerPaymentService == null) {
			logger.debug("customerPaymentService is null");
		}
		customerPaymentService.processOnlinePayment(finAdvPaymentList, paymentType);
		logger.debug(Literal.LEAVING);
	}

}
