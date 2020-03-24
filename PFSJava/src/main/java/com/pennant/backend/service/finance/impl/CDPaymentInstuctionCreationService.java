package com.pennant.backend.service.finance.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.bmtmasters.BankBranchDAO;
import com.pennant.backend.dao.finance.CashBackDetailDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.dao.mandate.MandateDAO;
import com.pennant.backend.dao.partnerbank.PartnerBankDAO;
import com.pennant.backend.dao.payment.PaymentHeaderDAO;
import com.pennant.backend.dao.rmtmasters.FinTypePartnerBankDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.BankBranch;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.PaymentInstruction;
import com.pennant.backend.model.mandate.Mandate;
import com.pennant.backend.model.payment.PaymentDetail;
import com.pennant.backend.model.payment.PaymentHeader;
import com.pennant.backend.model.rmtmasters.FinTypePartnerBank;
import com.pennant.backend.service.payment.PaymentHeaderService;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.UploadConstants;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;

@Component(value = "cdPaymentInstuctionCreationService")
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class CDPaymentInstuctionCreationService {
	private static final Logger logger = Logger.getLogger(CDPaymentInstuctionCreationService.class);

	private FinanceMainDAO financeMainDAO;
	private FinTypePartnerBankDAO finTypePartnerBankDAO;
	private BankBranchDAO bankBranchDAO;
	private MandateDAO mandateDAO;
	private ManualAdviseDAO manualAdviseDAO;
	private PaymentHeaderService paymentHeaderService;
	private PartnerBankDAO partnerBankDAO;
	private PaymentHeaderDAO paymentHeaderDAO;
	private CashBackDetailDAO cashBackDetailDAO;

	/**
	 * Method for creating Auto Payment Instruction using default beneficiary against Loan
	 * 
	 * @param finMain
	 * @param feeypeCode
	 * @param adviseId
	 */
	public void createPaymentInstruction(FinanceMain finMain, String feeypeCode, long adviseId) {
		logger.debug(Literal.ENTERING);

		PaymentHeader paymentHeader = preparePaymentInst(finMain, feeypeCode, adviseId);
		AuditHeader paymentsAuditHeader = getAuditHeader(paymentHeader, PennantConstants.TRAN_WF);

		paymentHeaderService.doApprove(paymentsAuditHeader);
		cashBackDetailDAO.updateCashBackDetail(adviseId);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for preparing Instruction data against cash back Payable record
	 * 
	 * @param finMain
	 * @param feeTypeCode
	 * @return
	 */
	private PaymentHeader preparePaymentInst(FinanceMain finMain, String feeTypeCode, long adviseId) {
		logger.debug("Entering");

		long bankBranchId = 0;
		Date appDate = SysParamUtil.getAppDate();

		// Payment Header
		PaymentHeader paymentHeader = new PaymentHeader();
		paymentHeader.setFinReference(finMain.getFinReference());
		paymentHeader.setPaymentType(DisbursementConstants.CHANNEL_PAYMENT);
		paymentHeader.setCreatedOn(appDate);
		paymentHeader.setApprovedOn(appDate);
		paymentHeader.setStatus(RepayConstants.PAYMENT_APPROVE);
		paymentHeader.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		paymentHeader.setNewRecord(true);
		paymentHeader.setVersion(1);
		paymentHeader.setUserDetails(finMain.getUserDetails());
		paymentHeader.setLastMntBy(finMain.getLastMntBy());
		paymentHeader.setLastMntOn(finMain.getLastMntOn());
		paymentHeader.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		paymentHeader.setPaymentId(paymentHeaderDAO.getNewPaymentHeaderId());
		paymentHeader.setFinSource(UploadConstants.FINSOURCE_ID_CD_PAY_UPLOAD);

		FinanceMain financeMain = financeMainDAO.getFinanceForIncomeAMZ(finMain.getFinReference());

		if (financeMain == null) {
			throw new InterfaceException("9999", "Loan Reference should not exist.");
		}
		long partnerBankId = Long.valueOf(SysParamUtil.getValueAsInt("DISB_PARTNERBANK"));
		String partnerBankcode = partnerBankDAO.getPartnerBankCodeById(partnerBankId);

		FinTypePartnerBank finTypePartnerBank = finTypePartnerBankDAO.getFinTypePartnerBankByPartnerBankCode(
				partnerBankcode, financeMain.getFinType(), DisbursementConstants.PAYMENT_TYPE_NEFT);

		if (finTypePartnerBank == null) {
			throw new InterfaceException("9999", "Partner banks should not linked to Loan Type.");
		}

		Mandate mandate = mandateDAO.getMandateById(finMain.getMandateID(), "_AView");
		if (mandate == null) {
			throw new InterfaceException("9999", "Please link Mandate.");
		}
		BankBranch bankBranch = bankBranchDAO.getBankBrachByIFSCandMICR(mandate.getIFSC(), mandate.getMICR(), "");

		if (bankBranch == null) {
			throw new InterfaceException("9999", "Invalid IFSC/MICR.");
		} else {
			bankBranchId = bankBranch.getBankBranchID();
		}

		// Payment Details
		List<PaymentDetail> paymentDetailList = new ArrayList<PaymentDetail>();

		ManualAdvise advise = getManualAdviseDAO().getManualAdviseById(adviseId, "_AView");
		if (advise == null) {
			return null;
		}

		// Payment Instruction Details preparation
		PaymentDetail paymentDetail = new PaymentDetail();
		paymentDetail.setAmount(advise.getAdviseAmount());
		paymentDetail.setReferenceId(advise.getAdviseID());
		paymentDetail.setAvailableAmount(advise.getBalanceAmt());
		paymentDetail.setAmountType(String.valueOf(advise.getAdviseType()));
		paymentDetail.setFeeTypeCode(advise.getFeeTypeCode());
		paymentDetail.setFeeTypeDesc(advise.getFeeTypeDesc());
		paymentDetail.setRecordType(PennantConstants.RCD_ADD);
		paymentDetail.setNewRecord(true);
		paymentDetail.setVersion(1);
		paymentDetail.setUserDetails(finMain.getUserDetails());
		paymentDetail.setLastMntBy(finMain.getLastMntBy());
		paymentDetail.setLastMntOn(finMain.getLastMntOn());
		paymentDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		paymentDetail.setApiRequest(true);
		paymentDetailList.add(paymentDetail);

		// Payment Instructions
		PaymentInstruction paymentInstruction = new PaymentInstruction();
		paymentInstruction.setPostDate(appDate);
		paymentInstruction.setPaymentType(DisbursementConstants.PAYMENT_TYPE_NEFT);
		paymentInstruction.setPaymentAmount(advise.getBalanceAmt());
		paymentInstruction.setBankBranchCode(mandate.getBranchCode());
		paymentInstruction.setBankBranchId(bankBranchId);
		paymentInstruction.setAcctHolderName(mandate.getAccHolderName());
		paymentInstruction.setAccountNo(mandate.getAccNumber());
		paymentInstruction.setPhoneNumber(mandate.getPhoneNumber());
		paymentInstruction.setValueDate(appDate);
		paymentInstruction.setPaymentCCy(financeMain.getFinCcy());
		paymentInstruction.setPartnerBankCode(partnerBankcode);
		paymentInstruction.setPartnerBankId(partnerBankId);
		paymentInstruction.setStatus(DisbursementConstants.STATUS_NEW);
		paymentInstruction.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		paymentInstruction.setRecordType(PennantConstants.RCD_ADD);
		paymentInstruction.setNewRecord(true);
		paymentInstruction.setVersion(1);
		paymentInstruction.setUserDetails(finMain.getUserDetails());
		paymentInstruction.setLastMntBy(finMain.getLastMntBy());
		paymentInstruction.setLastMntOn(finMain.getLastMntOn());

		// Extra validation fields
		paymentInstruction.setPartnerBankAcType(finTypePartnerBank.getAccountType());
		paymentInstruction.setApiRequest(true);

		paymentHeader.setPaymentDetailList(paymentDetailList);
		paymentHeader.setPaymentInstruction(paymentInstruction);

		logger.debug("Leaving");
		return paymentHeader;
	}

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */
	private AuditHeader getAuditHeader(PaymentHeader paymentHeader, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, paymentHeader.getBefImage(), paymentHeader);
		return new AuditHeader(String.valueOf(paymentHeader.getPaymentId()),
				String.valueOf(paymentHeader.getPaymentId()), null, null, auditDetail, paymentHeader.getUserDetails(),
				new HashMap<String, ArrayList<ErrorDetail>>());
	}

	public FinTypePartnerBankDAO getFinTypePartnerBankDAO() {
		return finTypePartnerBankDAO;
	}

	@Autowired(required = false)
	public void setFinTypePartnerBankDAO(FinTypePartnerBankDAO finTypePartnerBankDAO) {
		this.finTypePartnerBankDAO = finTypePartnerBankDAO;
	}

	public BankBranchDAO getBankBranchDAO() {
		return bankBranchDAO;
	}

	@Autowired(required = false)
	public void setBankBranchDAO(BankBranchDAO bankBranchDAO) {
		this.bankBranchDAO = bankBranchDAO;
	}

	public MandateDAO getMandateDAO() {
		return mandateDAO;
	}

	@Autowired(required = false)
	public void setMandateDAO(MandateDAO mandateDAO) {
		this.mandateDAO = mandateDAO;
	}

	public ManualAdviseDAO getManualAdviseDAO() {
		return manualAdviseDAO;
	}

	@Autowired(required = false)
	public void setManualAdviseDAO(ManualAdviseDAO manualAdviseDAO) {
		this.manualAdviseDAO = manualAdviseDAO;
	}

	public PaymentHeaderService getPaymentHeaderService() {
		return paymentHeaderService;
	}

	@Autowired(required = false)
	public void setPaymentHeaderService(PaymentHeaderService paymentHeaderService) {
		this.paymentHeaderService = paymentHeaderService;
	}

	public PartnerBankDAO getPartnerBankDAO() {
		return partnerBankDAO;
	}

	@Autowired(required = false)
	public void setPartnerBankDAO(PartnerBankDAO partnerBankDAO) {
		this.partnerBankDAO = partnerBankDAO;
	}

	public FinanceMainDAO getFinanceMainDAO() {
		return financeMainDAO;
	}

	@Autowired(required = false)
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public PaymentHeaderDAO getPaymentHeaderDAO() {
		return paymentHeaderDAO;
	}

	@Autowired(required = false)
	public void setPaymentHeaderDAO(PaymentHeaderDAO paymentHeaderDAO) {
		this.paymentHeaderDAO = paymentHeaderDAO;
	}

	public CashBackDetailDAO getCashBackDetailDAO() {
		return cashBackDetailDAO;
	}

	@Autowired(required = false)
	public void setCashBackDetailDAO(CashBackDetailDAO cashBackDetailDAO) {
		this.cashBackDetailDAO = cashBackDetailDAO;
	}

}
