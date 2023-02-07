package com.pennanttech.pff.autorefund;

import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.backend.dao.financemanagement.PresentmentDetailDAO;
import com.pennant.backend.dao.mandate.MandateDAO;
import com.pennant.backend.dao.pdc.ChequeDetailDAO;
import com.pennant.backend.model.finance.ChequeDetail;
import com.pennant.backend.model.finance.PaymentInstruction;
import com.pennant.backend.util.DisbursementConstants;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.presentment.model.PresentmentDetail;

public class RefundBeneficiary {
	private static final Logger logger = LogManager.getLogger(RefundBeneficiary.class);

	private MandateDAO mandateDAO;
	private PresentmentDetailDAO presentmentDetailDAO;
	private ChequeDetailDAO chequeDetailDAO;

	public PaymentInstruction getBeneficiary(long finID, Date appDate, boolean alwRefundByCheque) {
		logger.debug(Literal.ENTERING);

		List<PresentmentDetail> pdList = presentmentDetailDAO.getPresentmentIdByFinId(finID);

		Long mandateId = null;

		if (CollectionUtils.isNotEmpty(pdList)) {
			mandateId = pdList.get(0).getMandateId();
		} else {
			List<Long> mandateList = mandateDAO.getMandateIdList(finID);
			if (CollectionUtils.isNotEmpty(mandateList)) {
				mandateId = mandateList.get(0);
			}
		}

		PaymentInstruction pi = mandateDAO.getBeneficiatyDetailsByMandateId(mandateId);

		if (pi != null) {
			if (pi.getPartnerBankCode().equals("HDFC")) {
				pi.setPaymentType(DisbursementConstants.PAYMENT_TYPE_IFT);
			} else {
				pi.setPaymentType(DisbursementConstants.PAYMENT_TYPE_NEFT);
			}

			logger.debug(Literal.LEAVING);
			return pi;
		}

		Long chequeDetailsID = null;

		List<ChequeDetail> chequeList = chequeDetailDAO.getChequeDetailIDByFinId(finID);

		if (CollectionUtils.isNotEmpty(chequeList)) {
			chequeDetailsID = chequeList.get(0).getChequeDetailsID();
		} else {
			List<Long> mandateList = mandateDAO.getChequeDetailIDByAppDate(finID, appDate);
			if (CollectionUtils.isNotEmpty(mandateList)) {
				chequeDetailsID = mandateList.get(0);
			}
		}

		pi = mandateDAO.getBeneficiatyDetailsByChequeDetailsId(chequeDetailsID);

		if (pi != null) {
			if (pi.getPartnerBankCode().equals("HDFC")) {
				pi.setPaymentType(DisbursementConstants.PAYMENT_TYPE_IFT);
			} else {
				pi.setPaymentType(DisbursementConstants.PAYMENT_TYPE_NEFT);
			}
			logger.debug(Literal.LEAVING);
			return pi;
		}

		List<PaymentInstruction> paymentInstlist = mandateDAO.getBeneficiatyDetailsByFinId(finID);
		if (paymentInstlist.size() > 0) {
			if (paymentInstlist.get(0).getPartnerBankCode().equals("HDFC")) {
				paymentInstlist.get(0).setPaymentType(DisbursementConstants.PAYMENT_TYPE_IFT);
			} else {
				paymentInstlist.get(0).setPaymentType(DisbursementConstants.PAYMENT_TYPE_NEFT);
			}
			pi = paymentInstlist.get(0);
		}

		// In case any refunds processed earlier then use the same Instruction for processing
		if (pi == null) {
			if (alwRefundByCheque) {
				List<PaymentInstruction> paymentInstlist1 = mandateDAO.getBeneficiatyDetailsRefundCheque(finID);
				if (paymentInstlist1.size() > 0 && paymentInstlist1.get(0) != null) {
					pi = paymentInstlist.get(0);
					pi.setPaymentType(DisbursementConstants.PAYMENT_TYPE_CHEQUE);
				}
			}
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

}
