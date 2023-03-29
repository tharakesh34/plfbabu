package com.pennanttech.pff.autorefund;

import java.util.Date;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.constants.AccountConstants;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.applicationmaster.ClusterDAO;
import com.pennant.backend.dao.bmtmasters.BankBranchDAO;
import com.pennant.backend.dao.customermasters.CustomerDAO;
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
import com.pennant.pff.mandate.InstrumentType;
import com.pennanttech.pennapps.core.AppException;
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
	private CustomerDAO customerDAO;

	public PaymentInstruction getBeneficiary(long finID, Date appDate, boolean alwRefundByCheque) {
		logger.debug(Literal.ENTERING);

		PaymentInstruction pi = getPIByPresentment(finID);

		if (pi == null) {
			pi = getPIByCheque(finID, appDate);
		}

		if (pi != null) {
			setBankDetails(finID, pi);

			return pi;
		}

		pi = finAdvancePaymentsDAO.getBeneficiary(finID);

		if (pi != null) {
			setBankDetails(finID, pi);

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

	private void setBankDetails(long finID, PaymentInstruction pi) {
		String dftBankCode = SysParamUtil.getValueAsString(SMTParameterConstants.BANK_CODE);

		if (dftBankCode.equals(pi.getBankBranchCode())) {
			pi.setPaymentType(DisbursementConstants.PAYMENT_TYPE_IFT);
		} else {
			pi.setPaymentType(DisbursementConstants.PAYMENT_TYPE_NEFT);
		}

		pi.setFinID(finID);
		setPartnerBankDetails(pi);
	}

	private PaymentInstruction getPIByPresentment(long finID) {
		logger.debug(Literal.ENTERING);

		Long mandateId = presentmentDetailDAO.getLatestMandateId(finID);

		if (mandateId == null) {
			mandateId = mandateDAO.getMandateId(finID);
		}

		PaymentInstruction pi = null;
		if (mandateId != null) {
			String mandateType = mandateDAO.getMandateTypeById(mandateId, "");

			if (InstrumentType.SI.name().equals(mandateType)) {
				pi = mandateDAO.getBeneficiaryForSI(mandateId);
			} else {
				pi = mandateDAO.getBeneficiary(mandateId);
			}
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

		List<FinTypePartnerBank> fintypePartnerbank = finTypePartnerBankDAO.getFinTypePartnerBanks(fpb);

		if (CollectionUtils.isEmpty(fintypePartnerbank)) {
			throw new AppException(
					"Loan Type Partner Bank configuration not found with the following mapping " + fpb.getMapping());
		}

		if (fintypePartnerbank.size() >= 1) {
			fpb = fintypePartnerbank.get(0);
			pi.setPartnerBankId(fpb.getPartnerBankID());
			pi.setPartnerBankCode(fpb.getPartnerBankCode());
			pi.setPartnerBankName(fpb.getPartnerBankName());
			pi.setIssuingBank(fpb.getIssuingBankCode());
			pi.setIssuingBankName(fpb.getIssuingBankName());
			pi.setPartnerBankAcType(fpb.getAccountType());
			pi.setPartnerBankAc(fpb.getAccountNo());
		}

		if (DisbursementConstants.PAYMENT_TYPE_CHEQUE.equals(paymentType)
				|| DisbursementConstants.PAYMENT_TYPE_DD.equals(paymentType)) {

			String payableLoc = getPayableLoc(paymentType);

			String custShrtName = customerDAO.getCustShrtNameByFinID(pi.getFinID());

			for (FinTypePartnerBank ftpb : fintypePartnerbank) {
				BankBranch bb = bankBranchDAO.getPrintingLoc(pi.getFinID(), ftpb.getIssuingBankCode(), paymentType);

				pi.setPartnerBankAcType(ftpb.getAccountType());
				pi.setPartnerBankAc(ftpb.getAccountNo());

				if (bb != null) {
					pi.setPrintingLoc(bb.getBranchCode());
					pi.setPrintingLocDesc(bb.getBranchDesc());
					pi.setFavourName(custShrtName);
					pi.setPayableLoc(payableLoc);

					fpb.setPrintingLoc(bb.getBranchCode());
					fpb.setPrintingLocDesc(bb.getBranchDesc());
					fpb.setFavourName(custShrtName);
					fpb.setPayableLoc(payableLoc);
				}
			}
		}

		pi.setValueDate(SysParamUtil.getAppDate());
	}

	private String getPayableLoc(String paymentMode) {
		String sysParamCode = SMTParameterConstants.PAYMENT_INSTRUCTION_CHEQUE_PAYABLE_LOCATION;

		if (DisbursementConstants.PAYMENT_TYPE_DD.equals(paymentMode)) {
			sysParamCode = SMTParameterConstants.PAYMENT_INSTRUCTION_DD_PAYABLE_LOCATION;
		}

		return SysParamUtil.getValueAsString(sysParamCode);
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

	@Autowired
	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

}
