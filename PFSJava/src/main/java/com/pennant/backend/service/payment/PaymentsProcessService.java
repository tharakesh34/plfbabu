package com.pennant.backend.service.payment;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.PaymentInstruction;
import com.pennant.backend.model.finance.PaymentTransaction;
import com.pennant.backend.service.finance.FinAdvancePaymentsService;
import com.pennant.backend.util.DisbursementConstants;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.external.CustomerPaymentService;

public class PaymentsProcessService {
	private static final Logger logger = LogManager.getLogger(PaymentsProcessService.class);

	@Autowired(required = false)
	private CustomerPaymentService customerPaymentService;
	@Autowired
	private FinAdvancePaymentsService finAdvancePaymentsService;
	@Autowired
	private PaymentInstructionService paymentInstructionService;

	public static final String DISB_STATUS = "DISB";
	public static final String DISB_PYMT = "PYMT";
	public static final String DISB_INSR = "INSR";

	public void process(FinanceDetail financeDetail, String channel) {
		logger.debug(Literal.ENTERING);

		List<FinAdvancePayments> advancePayments = financeDetail.getAdvancePaymentsList();

		if (CollectionUtils.isEmpty(advancePayments)) {
			logger.debug("FinAdvancePayments list is emty");
			return;
		}

		if (this.customerPaymentService == null) {
			logger.debug("CustomerPaymentService is null.");
			return;
		}

		List<FinAdvancePayments> finAdvancePayments = null;

		try {
			String paymentType = null;
			if (DisbursementConstants.CHANNEL_DISBURSEMENT.equals(channel)) {
				paymentType = DISB_STATUS;
			} else if (DisbursementConstants.CHANNEL_PAYMENT.equals(channel)) {
				paymentType = DISB_PYMT;
			} else if (DisbursementConstants.CHANNEL_INSURANCE.equals(channel)) {
				paymentType = DISB_INSR;
			}

			finAdvancePayments = this.customerPaymentService.processOnlinePayment(advancePayments, paymentType);
			if (CollectionUtils.isNotEmpty(finAdvancePayments)) {
				for (FinAdvancePayments finAdvancePayment : finAdvancePayments) {
					updateStatus(finAdvancePayment, channel);
				}
			}

		} catch (InterfaceException e) {
			logger.debug(Literal.EXCEPTION, e);
			if (CollectionUtils.isNotEmpty(finAdvancePayments)) {
				for (FinAdvancePayments finAdvancePayment : finAdvancePayments) {
					finAdvancePayment.setStatus(DisbursementConstants.STATUS_REJECTED);
					updateStatus(finAdvancePayment, channel);
				}
			}
		}
		logger.debug(Literal.LEAVING);
	}

	private void updateStatus(FinAdvancePayments finAdvancePayment, String channel) {
		logger.debug(Literal.ENTERING);

		if (DisbursementConstants.CHANNEL_DISBURSEMENT.equals(channel)) {
			this.finAdvancePaymentsService.updatePaymentStatus(finAdvancePayment, "");
		} else if (DisbursementConstants.CHANNEL_PAYMENT.equals(channel)) {
			PaymentInstruction instruction = new PaymentInstruction();
			instruction.setPaymentInstructionId(finAdvancePayment.getPaymentId());
			instruction.setStatus(finAdvancePayment.getStatus());
			this.paymentInstructionService.updateStatus(instruction, "");
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Processing Payments
	 * 
	 * @param paymentTransaction
	 */
	public void processPayments(PaymentTransaction paymentTransaction) {
		logger.debug(Literal.ENTERING);
		if (this.customerPaymentService == null) {
			logger.debug("CustomerPaymentService is null.");
			return;
		}
		this.customerPaymentService.processPayments(paymentTransaction);
		logger.debug(Literal.LEAVING);
	}
}
