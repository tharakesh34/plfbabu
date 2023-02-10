package com.pennanttech.pff.autorefund;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.backend.dao.finance.FinAdvancePaymentsDAO;
import com.pennant.backend.dao.financemanagement.PresentmentDetailDAO;
import com.pennant.backend.dao.mandate.MandateDAO;
import com.pennant.backend.dao.pdc.ChequeDetailDAO;
import com.pennant.backend.model.finance.PaymentInstruction;
import com.pennant.backend.util.DisbursementConstants;
import com.pennanttech.pennapps.core.resource.Literal;

public class RefundBeneficiary {
	private static final Logger logger = LogManager.getLogger(RefundBeneficiary.class);

	private MandateDAO mandateDAO;
	private PresentmentDetailDAO presentmentDetailDAO;
	private ChequeDetailDAO chequeDetailDAO;
	private FinAdvancePaymentsDAO finAdvancePaymentsDAO;

	public PaymentInstruction getBeneficiary(long finID, Date appDate, boolean alwRefundByCheque) {
		logger.debug(Literal.ENTERING);

		PaymentInstruction pi = getPIByPresentment(finID);

		if (pi == null) {
			pi = getPIByCheque(finID, appDate);
		}

		if (pi == null) {
			pi = finAdvancePaymentsDAO.getBeneficiary(finID);
		}

		if (pi != null && "HDFC".equals(pi.getPartnerBankCode())) {
			pi.setPaymentType(DisbursementConstants.PAYMENT_TYPE_IFT);
		}

		if (pi == null && alwRefundByCheque) {
			pi = finAdvancePaymentsDAO.getBeneficiaryByPrintLoc(finID);
			if (pi != null) {
				pi.setPaymentType(DisbursementConstants.PAYMENT_TYPE_CHEQUE);
			}
		}

		logger.debug(Literal.LEAVING);
		return pi;
	}

	private PaymentInstruction getPIByPresentment(long finID) {
		logger.debug(Literal.ENTERING);

		Long mandateId = presentmentDetailDAO.getLatestMandateId(finID);

		if (mandateId == null) {
			mandateId = mandateDAO.getMandateId(finID);
		}

		PaymentInstruction pi = null;
		if (mandateId != null) {
			pi = mandateDAO.getBeneficiary(mandateId);
		}

		logger.debug(Literal.LEAVING);
		return pi;
	}

	private PaymentInstruction getPIByCheque(long finID, Date appDate) {
		logger.debug(Literal.ENTERING);

		Long id = chequeDetailDAO.getChequeDetailID(finID);

		if (id == null) {
			id = chequeDetailDAO.getChequeDetailIDByAppDate(finID, appDate);
		}

		PaymentInstruction pi = null;
		if (id != null) {
			pi = chequeDetailDAO.getBeneficiary(id);
		}

		logger.debug(Literal.LEAVING);
		return pi;
	}

	@Autowired
	public void setMandateDAO(MandateDAO mandateDAO) {
		this.mandateDAO = mandateDAO;
	}

	@Autowired
	public void setPresentmentDetailDAO(PresentmentDetailDAO presentmentDetailDAO) {
		this.presentmentDetailDAO = presentmentDetailDAO;
	}

	@Autowired
	public void setChequeDetailDAO(ChequeDetailDAO chequeDetailDAO) {
		this.chequeDetailDAO = chequeDetailDAO;
	}

	@Autowired
	public void setFinAdvancePaymentsDAO(FinAdvancePaymentsDAO finAdvancePaymentsDAO) {
		this.finAdvancePaymentsDAO = finAdvancePaymentsDAO;
	}

}
