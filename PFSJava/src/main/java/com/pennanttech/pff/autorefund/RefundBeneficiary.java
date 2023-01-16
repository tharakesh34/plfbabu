package com.pennanttech.pff.autorefund;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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

	public PaymentInstruction fetchBeneficiaryForRefund(long finID, Date appDate, boolean alwRefundByCheque) {
		logger.debug(Literal.ENTERING);
		PaymentInstruction paymentInst = null;

		// Verification of Successful presentments against Repayment Method (NACH/SI/EMANDATE)
		// Method for the Loan is NACH, E Mandate, PDC or SI
		List<PresentmentDetail> prsnIdList = presentmentDetailDAO.getPresentmentIdByFinId(finID);

		if (CollectionUtils.isNotEmpty(prsnIdList)) {
			List<PresentmentDetail> prsntDetail = prsnIdList.stream()
					.sorted((l1, l2) -> Long.valueOf(l2.getId()).compareTo(Long.valueOf(l1.getId())))
					.collect(Collectors.toList());
			// if Success presentment Exists , then Collect beneficiary Details of Mandate from presentment
			paymentInst = mandateDAO.getBeneficiatyDetailsByMandateId(prsntDetail.get(0).getMandateId());
		} else {
			// Mandate (NACH, E Mandate or SI)
			List<Long> mandateList = mandateDAO.getMandateIDByFinId(finID);
			if (CollectionUtils.isNotEmpty(mandateList)) {
				paymentInst = mandateDAO.getBeneficiatyDetailsByMandateId(mandateList.get(0));
			}
		}

		if (paymentInst != null) {
			if (paymentInst.getPartnerBankCode().equals("HDFC")) {
				paymentInst.setPaymentType(DisbursementConstants.PAYMENT_TYPE_IFT);
			} else {
				paymentInst.setPaymentType(DisbursementConstants.PAYMENT_TYPE_NEFT);
			}
			logger.debug(Literal.LEAVING);
			return paymentInst;
		}
		// Verification of Cheque Details
		// PDC it will consider Bank Details
		List<ChequeDetail> chequeList = chequeDetailDAO.getChequeDetailIDByFinId(finID);
		if (CollectionUtils.isNotEmpty(chequeList)) {
			List<ChequeDetail> cd = chequeList.stream()
					.sorted((l1, l2) -> Long.valueOf(l2.getId()).compareTo(Long.valueOf(l1.getId())))
					.collect(Collectors.toList());
			paymentInst = mandateDAO.getBeneficiatyDetailsByChequeDetailsId(cd.get(0).getChequeDetailsID());

		} else {
			// In Case of non-availability of Approved Mandates linked to loan, or non-availability of PDC instruments
			List<Long> mandateList = mandateDAO.getChequeDetailIDByAppDate(finID, appDate);
			if (CollectionUtils.isNotEmpty(mandateList)) {
				paymentInst = mandateDAO.getBeneficiatyDetailsByChequeDetailsId(mandateList.get(0));
			}
		}

		if (paymentInst != null) {
			if (paymentInst.getPartnerBankCode().equals("HDFC")) {
				paymentInst.setPaymentType(DisbursementConstants.PAYMENT_TYPE_IFT);
			} else {
				paymentInst.setPaymentType(DisbursementConstants.PAYMENT_TYPE_NEFT);
			}
			logger.debug(Literal.LEAVING);
			return paymentInst;
		}

		// In case no details available against Presentment & Cheque Details the Use the Mandate presented against Loan
		List<PaymentInstruction> paymentInstlist = mandateDAO.getBeneficiatyDetailsByFinId(finID);
		if (paymentInstlist.size() > 0) {
			if (paymentInstlist.get(0).getPartnerBankCode().equals("HDFC")) {
				paymentInstlist.get(0).setPaymentType(DisbursementConstants.PAYMENT_TYPE_IFT);
			} else {
				paymentInstlist.get(0).setPaymentType(DisbursementConstants.PAYMENT_TYPE_NEFT);
			}
			paymentInst = paymentInstlist.get(0);
		}

		// In case any refunds processed earlier then use the same Instruction for processing
		if (paymentInst == null) {
			if (alwRefundByCheque) {
				List<PaymentInstruction> paymentInstlist1 = mandateDAO.getBeneficiatyDetailsRefundCheque(finID);
				if (paymentInstlist1.size() > 0 && paymentInstlist1.get(0) != null) {
					paymentInst = paymentInstlist.get(0);
					paymentInst.setPaymentType(DisbursementConstants.PAYMENT_TYPE_CHEQUE);
				}
			}
		}
		logger.debug(Literal.LEAVING);
		return paymentInst;
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
