package com.pennant.backend.service.payment;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.FinAdvancePaymentsService;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.external.CustomerPaymentService;

public class PaymentsProcessService extends GenericService<FinanceDetail> {
	private static final Logger logger = Logger.getLogger(PaymentsProcessService.class);

	@Autowired(required = false)
	private CustomerPaymentService customerPaymentService;
	@Autowired
	private FinAdvancePaymentsService finAdvancePaymentsService;

	public static final String DISB_STATUS = "DISB";
	public static final String DISB_PYMT = "PYMT";
	public static final String DISB_INSR = "INSR";

	public AuditHeader process(FinanceDetail financeDetail, AuditHeader auditHeader, String channel) {
		logger.debug(Literal.ENTERING);

		List<FinAdvancePayments> advancePayments = financeDetail.getAdvancePaymentsList();

		if (CollectionUtils.isEmpty(advancePayments)) {
			logger.debug("FinAdvancePayments list is emty for the reference :"
					+ financeDetail.getFinScheduleData().getFinanceMain().getFinReference());
			return auditHeader;
		}

		if (this.customerPaymentService == null) {
			logger.debug("CustomerPaymentService is null.");
			return auditHeader;
		}

		try {
			String paymentType = null;
			if (DisbursementConstants.CHANNEL_DISBURSEMENT.equals(channel)) {
				paymentType = DISB_STATUS;
			} else if (DisbursementConstants.CHANNEL_PAYMENT.equals(channel)) {
				paymentType = DISB_PYMT;
			} else if (DisbursementConstants.CHANNEL_INSURANCE.equals(channel)) {
				paymentType = DISB_INSR;
			}
			List<FinAdvancePayments> finAdvancePayments = this.customerPaymentService
					.processOnlinePayment(advancePayments, paymentType);
			for (FinAdvancePayments finAdvancePayment : finAdvancePayments) {
				this.finAdvancePaymentsService.updateStatus(finAdvancePayment, "");
				/*
				 * if (DisbursementConstants.STATUS_REJECTED.equals(
				 * finAdvancePayment.getStatus())) {
				 * this.finAdvancePaymentsService.Update(paymentId,
				 * linkedTranId); }
				 */
			}
		} catch (InterfaceException e) {
			logger.debug(Literal.EXCEPTION, e);

			AuditDetail auditDetail = auditHeader.getAuditDetail();

			auditDetail.setErrorDetail(new ErrorDetail(e.getErrorCode(), e.getMessage(), null));
			auditDetail.setErrorDetails(
					ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), PennantConstants.default_Language));

			auditHeader.setAuditDetail(auditDetail);
			auditHeader.setErrorList(auditDetail.getErrorDetails());
			auditHeader = nextProcess(auditHeader);

		}
		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

}
