package com.pennant.backend.service.payment;

import java.util.ArrayList;
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
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.external.CustomerPaymentService;

public class PaymentsProcessService extends GenericService<FinanceDetail>{
	private static final Logger logger = Logger.getLogger(PaymentsProcessService.class);

	@Autowired(required = false)
	private CustomerPaymentService customerPaymentService;

	public AuditHeader process(FinanceDetail financeDetail, AuditHeader auditHeader, String channel) {
		logger.debug(Literal.ENTERING);

		List<FinAdvancePayments> advancePayments = financeDetail.getAdvancePaymentsList();

		AuditDetail auditDetail = auditHeader.getAuditDetail();

		if (CollectionUtils.isEmpty(advancePayments)) {
			logger.debug("FinAdvancePayments list is emty for the reference :"
					+ financeDetail.getFinScheduleData().getFinanceMain().getFinReference());
			return auditHeader;
		}

		if (this.customerPaymentService == null) {
			logger.debug("CustomerPaymentService is null.");
			return auditHeader;
		}

		StringBuilder errMsg = new StringBuilder();
		try {
			String paymentType = null;
			if (DisbursementConstants.CHANNEL_DISBURSEMENT.equals(channel)) {
				paymentType = "DISB";
			} else if (DisbursementConstants.CHANNEL_PAYMENT.equals(channel)) {
				paymentType = "PAMT";
			} else if (DisbursementConstants.CHANNEL_INSURANCE.equals(channel)) {
				paymentType = "INS";
			}

			List<FinAdvancePayments> finAdvancePayments = this.customerPaymentService
					.processOnlinePayment(advancePayments, paymentType);
			for (FinAdvancePayments finAdvancePayment : finAdvancePayments) {
				if (DisbursementConstants.STATUS_REJECTED.equals(finAdvancePayment.getStatus())) {
					errMsg.append(finAdvancePayment.getRejectReason());
					break;
				}
			}
		} catch (InterfaceException e) {
			logger.debug(Literal.EXCEPTION, e);
			errMsg.append(e.getErrorCode()).append(" - ").append(e.getErrorMessage());
		}
		logger.debug(Literal.LEAVING);

		if (errMsg.length() > 0) {
			ErrorDetail errorDetail = new ErrorDetail(PennantConstants.ERR_9999, errMsg.toString(), null);
			List<ErrorDetail> errorDetails = new ArrayList<ErrorDetail>();
			errorDetails.add(ErrorUtil.getErrorDetail(errorDetail, PennantConstants.default_Language));
			auditHeader.setErrorList(errorDetails);
			auditHeader = nextProcess(auditHeader);
		}
		return auditHeader;
	}

}
