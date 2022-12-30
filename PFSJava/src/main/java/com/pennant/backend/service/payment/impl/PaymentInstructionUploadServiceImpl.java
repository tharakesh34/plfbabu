package com.pennant.backend.service.payment.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.payment.PaymentInstructionDAO;
import com.pennant.backend.dao.payment.PaymentInstructionUploadDAO;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.payment.PaymentDetail;
import com.pennant.backend.model.payment.PaymentHeader;
import com.pennant.backend.model.payment.PaymentInstUploadDetail;
import com.pennant.backend.service.payment.PaymentHeaderService;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.UploadConstants;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.paymentupload.exception.PaymentUploadError;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.service.impl.AUploadServiceImpl;
import com.pennanttech.pennapps.core.AppException;

public class PaymentInstructionUploadServiceImpl extends AUploadServiceImpl {
	private static final Logger logger = LogManager.getLogger(PaymentInstructionUploadServiceImpl.class);

	private PaymentInstructionUploadDAO paymentInstructionUploadDAO;
	private FinanceMainDAO financeMainDAO;
	private FinExcessAmountDAO finExcessAmountDAO;
	private PaymentInstructionDAO paymentInstructionDAO;
	private PaymentHeaderService paymentHeaderService;
	private AuditHeaderDAO auditHeaderDAO;

	protected static final String ERR_CODE = "9999";
	protected static final String ERR_DESC = "User rejected the record";

	@Override
	public void doApprove(List<FileUploadHeader> headers) {
		new Thread(() -> {

			DefaultTransactionDefinition txDef = new DefaultTransactionDefinition(
					TransactionDefinition.PROPAGATION_REQUIRES_NEW);
			TransactionStatus txStatus = null;

			List<Long> headerIdList = headers.stream().map(FileUploadHeader::getId).collect(Collectors.toList());

			Date appDate = SysParamUtil.getAppDate();

			for (FileUploadHeader header : headers) {
				logger.info("Processing the File {}", header.getFileName());

				List<PaymentInstUploadDetail> details = paymentInstructionUploadDAO.getDetails(header.getId());

				header.setAppDate(appDate);
				header.setTotalRecords(details.size());
				int sucessRecords = 0;
				int failRecords = 0;

				for (PaymentInstUploadDetail detail : details) {

					doValidate(header, detail);

					if (detail.getProgress() == EodConstants.PROGRESS_FAILED) {
						failRecords++;
					} else {
						sucessRecords++;
					}
				}

				try {
					txStatus = transactionManager.getTransaction(txDef);

					paymentInstructionUploadDAO.update(details);

					transactionManager.commit(txStatus);
				} catch (Exception e) {
					logger.error(ERROR_LOG, e.getCause(), e.getMessage(), e.getLocalizedMessage(), e);

					if (txStatus != null) {
						transactionManager.rollback(txStatus);
					}
				} finally {
					txStatus = null;
				}

				header.setSuccessRecords(sucessRecords);
				header.setFailureRecords(failRecords);

				StringBuilder remarks = new StringBuilder("Process Completed");

				if (failRecords > 0) {
					remarks.append(" with exceptions, ");
				}

				remarks.append(" Total Records : ").append(header.getTotalRecords());
				remarks.append(" Success Records : ").append(sucessRecords);
				remarks.append(" Failed Records : ").append(failRecords);

				logger.info("Processed the File {}", header.getFileName());
			}

			try {
				txStatus = transactionManager.getTransaction(txDef);

				updateHeader(headers, true);

				logger.info("Payment Instruction Process is Initiated");

				processRefunds(headerIdList);

				transactionManager.commit(txStatus);
			} catch (Exception e) {
				logger.error(ERROR_LOG, e.getCause(), e.getMessage(), e.getLocalizedMessage(), e);

				if (txStatus != null) {
					transactionManager.rollback(txStatus);
				}
			} finally {
				txStatus = null;
			}

		}).start();
	}

	private void processRefunds(List<Long> headerIdList) {

		// Process Payment Header
		for (Long headerId : headerIdList) {

			List<PaymentInstUploadDetail> uploadList = paymentInstructionUploadDAO.getDetails(headerId);

			List<PaymentHeader> phList = new ArrayList<>();

			List<String> finReferences = new ArrayList<>();

			PaymentHeader ph = new PaymentHeader();

			List<ManualAdvise> malist = new ArrayList<>();
			List<FinExcessAmount> feaList = new ArrayList<>();

			Date appDate = SysParamUtil.getAppDate();

			for (PaymentInstUploadDetail detail : uploadList) {

				if (detail.getProgress() == EodConstants.PROGRESS_FAILED) {
					continue;
				}

				String finreference = detail.getReference();
				Long finId = detail.getReferenceID();

				if (!finReferences.contains(finreference)) {
					ph = new PaymentHeader();
					ph.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					ph.setFinSource(UploadConstants.FINSOURCE_ID_CD_PAY_UPLOAD);

					malist = new ArrayList<>();
					feaList = new ArrayList<>();

					// ph.setPaymentInstruction(prepareInstructions(appDate, detail));
					// Need Service from Vijay
				}

				ph.setFinReference(finreference);
				ph.setPaymentType(DisbursementConstants.CHANNEL_PAYMENT);
				ph.setCreatedOn(appDate);
				if (!finReferences.contains(finreference)) {
					ph.setPaymentAmount(detail.getPayAmount());
				} else {
					ph.setPaymentAmount(ph.getPaymentAmount().add(detail.getPayAmount()));
				}
				ph.setApprovedOn(appDate);
				ph.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);

				if (RepayConstants.EXAMOUNTTYPE_PAYABLE.equals(detail.getExcessType())) {
					preparePayable(ph, malist, detail, finId, finreference);
				}

				if (RepayConstants.EXAMOUNTTYPE_EXCESS.equals(detail.getExcessType())) {
					prepareExcess(ph, feaList, detail, finId, finreference);
				}

				if (!finReferences.contains(finreference) && EodConstants.PROGRESS_FAILED != detail.getProgress()) {
					phList.add(ph);
					finReferences.add(finreference);
				}

				this.paymentInstructionUploadDAO.update(detail);
			}

			processPayIns(phList);

		}
	}

	private void processPayIns(List<PaymentHeader> phList) {
		String tranType = PennantConstants.TRAN_WF;

		for (PaymentHeader paymentHeader : phList) {
			if (paymentHeader.getPaymentDetailList().isEmpty()) {
				continue;
			}

			AuditHeader ah = getAuditDetail(paymentHeader, tranType);
			ah.setAuditTranType(PennantConstants.TRAN_WF);
			ah.getAuditDetail().setModelData(paymentHeader);
			auditHeaderDAO.addAudit(ah);

			ah.setAuditTranType(tranType);
			ah.getAuditDetail().setAuditTranType(tranType);
			ah.getAuditDetail().setModelData(paymentHeader);
			auditHeaderDAO.addAudit(ah);

			AuditHeader audH = paymentHeaderService.doApprove(ah);
			if (audH.getErrorMessage() != null) {

			}
		}
	}

	private AuditHeader getAuditDetail(PaymentHeader ph, String tranType) {
		AuditDetail ad = new AuditDetail(tranType, 1, ph.getBefImage(), ph);

		return new AuditHeader(String.valueOf(ph.getId()), null, null, null, ad, ph.getUserDetails(), new HashMap<>());
	}

	private void prepareExcess(PaymentHeader ph, List<FinExcessAmount> feaList, PaymentInstUploadDetail bud, Long finId,
			String finreference) {

		if (CollectionUtils.isEmpty(feaList)) {
			feaList = paymentHeaderService.getfinExcessAmount(finId);
		}

		boolean excessExists = false;
		BigDecimal balAmt = bud.getPayAmount();
		for (FinExcessAmount fea : feaList) {
			if (!RepayConstants.EXAMOUNTTYPE_EXCESS.equals(fea.getAmountType())) {
				continue;
			}

			excessExists = true;

			PaymentDetail pd = prepareDetail(bud, fea, balAmt);
			bud.setProgress(EodConstants.PROGRESS_SUCCESS);
			balAmt = bud.getPayAmount().subtract(pd.getAvailableAmount());
			ph.getPaymentDetailList().add(pd);
		}

		if (!excessExists) {
			bud.setProgress(EodConstants.PROGRESS_FAILED);
			bud.setErrorDesc("Excess Details are not found for the Loan Reference :" + finreference);
		}
	}

	private PaymentDetail prepareDetail(PaymentInstUploadDetail bud, FinExcessAmount finExcessAmount,
			BigDecimal balAmt) {
		PaymentDetail pd = new PaymentDetail();

		pd.setNewRecord(true);
		pd.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		pd.setReferenceId(finExcessAmount.getId());
		pd.setAvailableAmount(finExcessAmount.getBalanceAmt());
		pd.setAmountType(finExcessAmount.getAmountType());
		if (finExcessAmount.getBalanceAmt().compareTo(bud.getPayAmount()) <= 0) {
			pd.setAmount(finExcessAmount.getBalanceAmt());
		} else {
			pd.setAmount(balAmt);
		}
		pd.setFinSource(UploadConstants.FINSOURCE_ID_CD_PAY_UPLOAD);
		return pd;
	}

	private void preparePayable(PaymentHeader ph, List<ManualAdvise> malist, PaymentInstUploadDetail bud, Long finId,
			String finreference) {

		if (CollectionUtils.isEmpty(malist)) {
			malist = paymentHeaderService.getManualAdvise(finId);
		}

		if (CollectionUtils.isEmpty(malist)) {
			setError(bud, PaymentUploadError.REFUP007);
		}

		boolean payableExists = false;

		String feeType = bud.getFeeType();
		for (ManualAdvise ma : malist) {
			if (!feeType.equals(ma.getFeeTypeCode())) {
				continue;
			}

			payableExists = true;

			PaymentDetail pd = prepareDetail(bud, ma);

			if (pd.getAvailableAmount().compareTo(bud.getPayAmount()) >= 0) {
				bud.setProgress(EodConstants.PROGRESS_SUCCESS);
				ph.getPaymentDetailList().add(pd);
			} else {
				bud.setProgress(EodConstants.PROGRESS_FAILED);
				bud.setErrorDesc("Receipt Amount Should not be greater than Payable Amount");
			}
		}

		if (!payableExists) {
			String ErrorDesc = "Payable Advises are not found for the Loan Reference : " + finreference
					+ " and Fee Type Code : " + feeType;
			bud.setErrorDesc(ErrorDesc);
		}
	}

	private PaymentDetail prepareDetail(PaymentInstUploadDetail bud, ManualAdvise ma) {
		PaymentDetail pd = new PaymentDetail();

		pd.setNewRecord(true);
		pd.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		pd.setReferenceId(ma.getAdviseID());
		pd.setAvailableAmount(ma.getAdviseAmount().subtract(ma.getPaidAmount()).subtract(ma.getWaivedAmount()));
		pd.setAmountType(String.valueOf(ma.getAdviseType()));
		pd.setFeeTypeCode(ma.getFeeTypeCode());
		pd.setFeeTypeDesc(ma.getFeeTypeDesc());
		pd.setAdviseAmount(ma.getAdviseAmount());
		pd.setAmount(bud.getPayAmount());
		pd.setFinSource(UploadConstants.FINSOURCE_ID_CD_PAY_UPLOAD);

		BigDecimal paidTGST = ma.getPaidCGST().add(ma.getPaidSGST()).add(ma.getPaidIGST()).add(ma.getPaidUGST())
				.add(ma.getPaidCESS());
		BigDecimal waivedTGST = ma.getWaivedCGST().add(ma.getWaivedSGST()).add(ma.getWaivedIGST())
				.add(ma.getWaivedUGST()).add(ma.getWaivedCESS());

		pd.setPrvGST(paidTGST.add(waivedTGST));
		pd.setManualAdvise(ma);
		pd.setTaxApplicable(ma.isTaxApplicable());
		pd.setTaxComponent(ma.getTaxComponent());

		return pd;
	}

	@Override
	public void doReject(List<FileUploadHeader> headers) {
		List<Long> headerIdList = headers.stream().map(FileUploadHeader::getId).collect(Collectors.toList());

		DefaultTransactionDefinition txDef = new DefaultTransactionDefinition(
				TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		TransactionStatus txStatus = null;
		try {

			txStatus = transactionManager.getTransaction(txDef);

			paymentInstructionUploadDAO.update(headerIdList, ERR_CODE, ERR_DESC, EodConstants.PROGRESS_FAILED);

			headers.forEach(h1 -> h1.setRemarks(ERR_DESC));

			updateHeader(headers, false);

			transactionManager.commit(txStatus);

		} catch (Exception e) {
			logger.error(ERROR_LOG, e.getCause(), e.getMessage(), e.getLocalizedMessage(), e);

			if (txStatus != null) {
				transactionManager.rollback(txStatus);
			}
		}
	}

	@Override
	public void doValidate(FileUploadHeader header, Object object) {
		PaymentInstUploadDetail detail = null;

		if (object instanceof PaymentInstUploadDetail) {
			detail = (PaymentInstUploadDetail) object;
		}

		if (detail == null) {
			throw new AppException("Invalid Data transferred...");
		}

		logger.info("Validating the Data for the reference {}", detail.getReference());

		detail.setHeaderId(header.getId());

		String reference = detail.getReference();

		if (StringUtils.isBlank(reference)) {
			setError(detail, PaymentUploadError.REFUP001);
			return;
		}

		FinanceMain fm = financeMainDAO.getFinanceMain(reference, header.getEntityCode());

		if (fm == null) {
			setError(detail, PaymentUploadError.REFUP002);
			return;
		}

		if (!fm.isFinIsActive()) {
			setError(detail, PaymentUploadError.REFUP003);
			return;
		}

		detail.setFm(fm);
		detail.setReferenceID(fm.getFinID());

		if (!(RepayConstants.EXAMOUNTTYPE_EXCESS.equals(detail.getExcessType())
				|| RepayConstants.EXAMOUNTTYPE_PAYABLE.equals(detail.getExcessType()))) {
			setError(detail, PaymentUploadError.REFUP004);
			return;
		}

		if (RepayConstants.EXAMOUNTTYPE_PAYABLE.equals(detail.getExcessType())) {
			if (StringUtils.trimToNull(detail.getFeeType()) == null) {
				setError(detail, PaymentUploadError.REFUP005);
				return;
			}
		}

		boolean payInstInProgess = paymentInstructionDAO.isInstructionInProgress(fm.getFinReference());
		if (payInstInProgess) {
			setError(detail, PaymentUploadError.REFUP006);
		}

		BigDecimal fea = finExcessAmountDAO.getTotalExcessByRefAndType(fm.getFinID(),
				RepayConstants.EXAMOUNTTYPE_EXCESS);

		if (fea.compareTo(detail.getPayAmount()) > 0) {
			setError(detail, PaymentUploadError.REFUP006);
		}

		detail.setProgress(EodConstants.PROGRESS_SUCCESS);
		detail.setErrorCode("");
		detail.setErrorDesc("");

		logger.info("Validated the Data for the reference {}", detail.getReference());
	}

	private void setError(PaymentInstUploadDetail detail, PaymentUploadError error) {
		detail.setProgress(EodConstants.PROGRESS_FAILED);
		detail.setErrorCode(error.name());
		detail.setErrorDesc(error.description());
	}

	@Override
	public String getSqlQuery() {
		return paymentInstructionUploadDAO.getSqlQuery();
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public void setPaymentInstructionUploadDAO(PaymentInstructionUploadDAO paymentInstructionUploadDAO) {
		this.paymentInstructionUploadDAO = paymentInstructionUploadDAO;
	}

	public void setFinExcessAmountDAO(FinExcessAmountDAO finExcessAmountDAO) {
		this.finExcessAmountDAO = finExcessAmountDAO;
	}

	public void setPaymentInstructionDAO(PaymentInstructionDAO paymentInstructionDAO) {
		this.paymentInstructionDAO = paymentInstructionDAO;
	}

	public void setPaymentHeaderService(PaymentHeaderService paymentHeaderService) {
		this.paymentHeaderService = paymentHeaderService;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

}
