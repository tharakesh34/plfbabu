package com.pennanttech.pff.autorefund;

import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.constants.AccountConstants;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.applicationmaster.ClusterDAO;
import com.pennant.backend.dao.bmtmasters.BankBranchDAO;
import com.pennant.backend.dao.finance.FinAdvancePaymentsDAO;
import com.pennant.backend.dao.financemanagement.PresentmentDetailDAO;
import com.pennant.backend.dao.mandate.MandateDAO;
import com.pennant.backend.dao.pdc.ChequeDetailDAO;
import com.pennant.backend.dao.rmtmasters.FinTypePartnerBankDAO;
import com.pennant.backend.model.bmtmasters.BankBranch;
import com.pennant.backend.model.finance.PaymentInstruction;
import com.pennant.backend.model.rmtmasters.FinTypePartnerBank;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.pff.extension.PartnerBankExtension;
import com.pennanttech.pennapps.core.resource.Literal;

public class RefundBeneficiary {
	private static final Logger logger = LogManager.getLogger(RefundBeneficiary.class);

	private MandateDAO mandateDAO;
	private PresentmentDetailDAO presentmentDetailDAO;
	private ChequeDetailDAO chequeDetailDAO;
	private FinAdvancePaymentsDAO finAdvancePaymentsDAO;
	private FinTypePartnerBankDAO finTypePartnerBankDAO;
	private ClusterDAO clusterDAO;
	private BankBranchDAO bankBranchDAO;

	public PaymentInstruction getBeneficiary(long finID, Date appDate, boolean alwRefundByCheque) {
		logger.debug(Literal.ENTERING);

		PaymentInstruction pi = getPIByPresentment(finID);

		if (pi == null) {
			pi = getPIByCheque(finID, appDate);
		}

		if (pi != null) {
			String dftBankCode = SysParamUtil.getValueAsString(SMTParameterConstants.BANK_CODE);

			if (dftBankCode.equals(pi.getBankBranchCode())) {
				pi.setPaymentType(DisbursementConstants.PAYMENT_TYPE_IFT);
			} else {
				pi.setPaymentType(DisbursementConstants.PAYMENT_TYPE_NEFT);
			}

			pi.setFinID(finID);
			setPartnerBankDetails(pi);

			return pi;
		}

		pi = finAdvancePaymentsDAO.getBeneficiary(finID);

		if (pi != null) {
			pi.setFinID(finID);
			setPartnerBankDetails(pi);

			return pi;
		}

		if (alwRefundByCheque) {
			pi = finAdvancePaymentsDAO.getBeneficiaryByPrintLoc(finID);
			if (pi != null) {
				pi.setPaymentType(DisbursementConstants.PAYMENT_TYPE_CHEQUE);
			}
		}

		if (pi != null) {
			pi.setFinID(finID);
			setPartnerBankDetails(pi);
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

	private void setPartnerBankDetails(PaymentInstruction pi) {

		Long clusterId = null;
		if (PartnerBankExtension.BRANCH_OR_CLUSTER.equals("C")) {
			clusterId = clusterDAO.getClustersFilter(pi.getFinBranch());
		}

		String paymentType = pi.getPaymentType();
		if (paymentType == null) {
			pi.setPaymentType(DisbursementConstants.PAYMENT_TYPE_NEFT);
		}

		FinTypePartnerBank fpb = new FinTypePartnerBank();
		fpb.setFinType(pi.getFinType());
		fpb.setPurpose(AccountConstants.PARTNERSBANK_PAYMENT);
		fpb.setPaymentMode(paymentType);
		fpb.setBranchCode(pi.getFinBranch());
		fpb.setClusterId(clusterId);

		List<FinTypePartnerBank> fintypePartnerbank = finTypePartnerBankDAO.getByFinTypeAndPurpose(fpb);

		if (fintypePartnerbank.size() == 1) {
			fpb = fintypePartnerbank.get(0);
			pi.setPartnerBankId(fpb.getPartnerBankID());
			pi.setPartnerBankCode(fpb.getPartnerBankCode());
			pi.setPartnerBankName(fpb.getPartnerBankName());
			pi.setIssuingBank(fpb.getIssuingBankCode());
			pi.setIssuingBankName(fpb.getIssuingBankName());

		}

		if (DisbursementConstants.PAYMENT_TYPE_CHEQUE.equals(paymentType)
				|| DisbursementConstants.PAYMENT_TYPE_DD.equals(paymentType)) {
			for (FinTypePartnerBank ftpb : fintypePartnerbank) {
				BankBranch bb = bankBranchDAO.getPrintingLoc(pi.getFinID(), ftpb.getIssuingBankCode(), paymentType);

				if (bb != null) {
					pi.setPrintingLoc(bb.getBranchCode());
					pi.setPrintingLocDesc(bb.getBranchDesc());

					fpb.setPrintingLoc(bb.getBranchCode());
					fpb.setPrintingLocDesc(bb.getBranchDesc());
				}
			}
		}

		pi.setValueDate(SysParamUtil.getAppDate());
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

	@Autowired
	public void setFinTypePartnerBankDAO(FinTypePartnerBankDAO finTypePartnerBankDAO) {
		this.finTypePartnerBankDAO = finTypePartnerBankDAO;
	}

	@Autowired
	public void setClusterDAO(ClusterDAO clusterDAO) {
		this.clusterDAO = clusterDAO;
	}

	@Autowired
	public void setBankBranchDAO(BankBranchDAO bankBranchDAO) {
		this.bankBranchDAO = bankBranchDAO;
	}

}
