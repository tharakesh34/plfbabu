package com.pennant.backend.service.payment.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.TransactionStatus;

import com.pennant.app.core.FinOverDueService;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.feetype.FeeTypeDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.dao.payment.PaymentInstructionDAO;
import com.pennant.backend.dao.payment.PaymentInstructionUploadDAO;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.payment.PaymentInstUploadDetail;
import com.pennant.backend.service.feerefund.FeeRefundHeaderService;
import com.pennant.backend.service.payment.PaymentHeaderService;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.UploadConstants;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.payment.model.PaymentDetail;
import com.pennant.pff.payment.model.PaymentHeader;
import com.pennant.pff.paymentupload.exception.PaymentUploadError;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.service.impl.AUploadServiceImpl;
import com.pennanttech.dataengine.model.DataEngineAttributes;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.autorefund.RefundBeneficiary;
import com.pennanttech.pff.file.UploadTypes;
import com.pennapps.core.util.ObjectUtil;

public class PaymentInstructionUploadServiceImpl extends AUploadServiceImpl<PaymentInstUploadDetail> {
	private static final Logger logger = LogManager.getLogger(PaymentInstructionUploadServiceImpl.class);

	private FinOverDueService finOverDueService;
	private FeeRefundHeaderService feeRefundHeaderService;
	private PaymentHeaderService paymentHeaderService;
	private AuditHeaderDAO auditHeaderDAO;
	private PaymentInstructionUploadDAO paymentInstructionUploadDAO;
	private FinanceMainDAO financeMainDAO;
	private FinExcessAmountDAO finExcessAmountDAO;
	private PaymentInstructionDAO paymentInstructionDAO;
	private FeeTypeDAO feeTypeDAO;
	private ManualAdviseDAO manualAdviseDAO;
	private RefundBeneficiary refundBeneficiary;

	public PaymentInstructionUploadServiceImpl() {
		super();
	}

	@Override
	protected PaymentInstUploadDetail getDetail(Object object) {
		if (object instanceof PaymentInstUploadDetail detail) {
			return detail;
		}

		throw new AppException(IN_VALID_OBJECT);
	}

	@Override
	public void doApprove(List<FileUploadHeader> headers) {
		new Thread(() -> {
			Date appDate = SysParamUtil.getAppDate();

			for (FileUploadHeader header : headers) {
				logger.info("Processing the File {}", header.getFileName());

				List<PaymentInstUploadDetail> details = paymentInstructionUploadDAO.getDetails(header.getId());

				header.setAppDate(appDate);
				header.setTotalRecords(details.size());
				int sucessRecords = 0;
				int failRecords = 0;

				List<String> key = new ArrayList<>();

				for (PaymentInstUploadDetail detail : details) {

					String reference = detail.getReference();
					String excessType = detail.getExcessType();

					String keyRef = reference.concat("_").concat(excessType);

					if ("P".equals(excessType)) {
						keyRef = keyRef.concat("_").concat(detail.getFeeType());
					}

					if (key.contains(keyRef)) {
						setError(detail, PaymentUploadError.REFUP014);
						failRecords++;
						continue;
					}

					key.add(keyRef);

					doValidate(header, detail);

					if (detail.getProgress() == EodConstants.PROGRESS_FAILED) {
						failRecords++;
					} else {
						sucessRecords++;
						detail.setAppDate(appDate);
						processRefunds(detail);
					}

					header.getUploadDetails().add(detail);
				}

				try {

					header.setSuccessRecords(sucessRecords);
					header.setFailureRecords(failRecords);

					logger.info("Processed the File {}", header.getFileName());

					paymentInstructionUploadDAO.update(details);

					List<FileUploadHeader> headerList = new ArrayList<>();
					headerList.add(header);
					updateHeader(headerList, true);
				} catch (Exception e) {
					logger.error(Literal.EXCEPTION, e);
				}
			}
		}).start();
	}

	@Override
	public void doReject(List<FileUploadHeader> headers) {
		List<Long> headerIdList = headers.stream().map(FileUploadHeader::getId).toList();

		TransactionStatus txStatus = getTransactionStatus();
		try {
			paymentInstructionUploadDAO.update(headerIdList, ERR_CODE, ERR_DESC, EodConstants.PROGRESS_FAILED);

			headers.forEach(h1 -> {
				h1.setRemarks(ERR_DESC);
				h1.getUploadDetails().addAll(paymentInstructionUploadDAO.getDetails(h1.getId()));
			});

			updateHeader(headers, false);

			transactionManager.commit(txStatus);

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);

			if (txStatus != null) {
				transactionManager.rollback(txStatus);
			}
		}
	}

	@Override
	public void doValidate(FileUploadHeader header, Object object) {
		PaymentInstUploadDetail detail = getDetail(object);

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

		if (fm.isWriteoffLoan()) {
			setError(detail, PaymentUploadError.REFUP015);
			return;
		}

		if (!"N".equals(detail.getOverRide()) && !"Y".equals(detail.getOverRide())) {
			setError(detail, PaymentUploadError.REFUP013);
			return;
		}

		detail.setFm(fm);
		detail.setReferenceID(fm.getFinID());

		if (!(RepayConstants.EXAMOUNTTYPE_EXCESS.equals(detail.getExcessType())
				|| RepayConstants.EXAMOUNTTYPE_PAYABLE.equals(detail.getExcessType()))) {
			setError(detail, PaymentUploadError.REFUP004);
			return;
		}

		String feeTypeCode = StringUtils.trimToNull(detail.getFeeType());
		Long feeTypeId = null;
		if (RepayConstants.EXAMOUNTTYPE_PAYABLE.equals(detail.getExcessType())) {
			feeTypeId = feeTypeDAO.getPayableFeeTypeID(feeTypeCode);

			if (feeTypeId == null) {
				setError(detail, PaymentUploadError.REFUP005);
				return;
			}
		} else if (feeTypeCode != null) {
			setError(detail, PaymentUploadError.REFUP012);
			return;
		}

		BigDecimal dueAganistCustomer = finOverDueService.getDueAgnistCustomer(fm.getFinID());

		if (detail.getOverRide().equals("N") && dueAganistCustomer.compareTo(BigDecimal.ZERO) > 0) {
			setError(detail, PaymentUploadError.REFUP009);
			return;
		}

		if (paymentInstructionDAO.isInProgress(fm.getFinID())) {
			setError(detail, PaymentUploadError.REFUP006);
			return;
		}

		if (this.feeRefundHeaderService.isInProgress(fm.getFinID())) {
			setError(detail, PaymentUploadError.REFUP011);
			return;
		}

		BigDecimal balanceAmount = BigDecimal.ZERO;

		if (RepayConstants.EXAMOUNTTYPE_EXCESS.equals(detail.getExcessType())) {
			balanceAmount = finExcessAmountDAO.getExcessBalance(fm.getFinID());
		} else {
			balanceAmount = manualAdviseDAO.getPayableBalance(fm.getFinID(), feeTypeId);
		}

		if (balanceAmount.compareTo(detail.getPayAmount()) < 0) {
			setError(detail, PaymentUploadError.REFUP008);
			return;
		}

		setSuccesStatus(detail);

		logger.info("Validated the Data for the reference {}", detail.getReference());
	}

	@Override
	public String getSqlQuery() {
		return paymentInstructionUploadDAO.getSqlQuery();
	}

	private void processRefunds(PaymentInstUploadDetail detail) {
		Date appDate = detail.getAppDate();

		Long finId = detail.getReferenceID();

		PaymentHeader ph = preparePH(detail);

		ph.setPaymentInstruction(refundBeneficiary.getBeneficiary(finId, appDate, true));

		ph.getPaymentInstruction().setPaymentAmount(detail.getPayAmount());
		ph.getPaymentInstruction().setPostDate(appDate);

		if (RepayConstants.EXAMOUNTTYPE_PAYABLE.equals(detail.getExcessType())) {
			ph.getPaymentDetailList().addAll(preparePayable(detail));
		}

		if (RepayConstants.EXAMOUNTTYPE_EXCESS.equals(detail.getExcessType())) {
			ph.getPaymentDetailList().addAll(prepareExcess(detail));
		}

		TransactionStatus transactionStatus = getTransactionStatus();

		try {
			if (EodConstants.PROGRESS_FAILED != detail.getProgress()) {
				processPayIns(ph, detail);
			}

			if (EodConstants.PROGRESS_FAILED == detail.getProgress()) {
				this.paymentInstructionUploadDAO.update(detail);
			}

			this.transactionManager.commit(transactionStatus);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);

			if (transactionStatus != null) {
				transactionManager.rollback(transactionStatus);
			}

			String error = StringUtils.trimToEmpty(e.getMessage());

			if (error.length() > 1999) {
				error = error.substring(0, 1999);
			}

			detail.setProgress(EodConstants.PROGRESS_FAILED);
			detail.setErrorCode(ERR_CODE);
			detail.setErrorDesc(error);
		}
	}

	private PaymentHeader preparePH(PaymentInstUploadDetail detail) {
		Timestamp sysDate = new Timestamp(System.currentTimeMillis());

		String finreference = detail.getReference();
		Long finId = detail.getReferenceID();

		PaymentHeader ph = new PaymentHeader();
		ph.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		ph.setFinSource(UploadConstants.FINSOURCE_ID_UPLOAD);
		ph.setFinReference(finreference);
		ph.setFinID(finId);
		ph.setPaymentType(DisbursementConstants.CHANNEL_PAYMENT);
		ph.setCreatedOn(sysDate);
		ph.setPaymentAmount(detail.getPayAmount());
		ph.setApprovedOn(sysDate);
		ph.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		return ph;
	}

	private List<PaymentDetail> preparePayable(PaymentInstUploadDetail bud) {
		List<PaymentDetail> pdList = new ArrayList<>();

		List<ManualAdvise> malist = paymentHeaderService.getManualAdvise(bud.getReferenceID());

		if (CollectionUtils.isEmpty(malist)) {
			bud.setProgress(EodConstants.PROGRESS_FAILED);
			bud.setErrorDesc("Payable Advises are not found for the Loan Reference");
		}

		boolean payableExists = false;

		String finreference = bud.getReference();

		String feeType = bud.getFeeType();
		for (ManualAdvise ma : malist) {
			if (!feeType.equals(ma.getFeeTypeCode())) {
				continue;
			}

			payableExists = true;

			if (!ma.isRefundable()) {
				bud.setProgress(EodConstants.PROGRESS_FAILED);
				bud.setErrorDesc("Payable Advise Fee is not a Refundable fee");
				continue;
			}

			if (ma.isHoldDue()) {
				bud.setProgress(EodConstants.PROGRESS_FAILED);
				bud.setErrorDesc("Payable Advise is a Hold Due");
				continue;
			}

			PaymentDetail pd = getNewPD(ma);

			if (pd.getAvailableAmount().compareTo(bud.getPayAmount()) >= 0) {
				pd.setAmount(bud.getPayAmount());
				bud.setProgress(EodConstants.PROGRESS_SUCCESS);
				pdList.add(pd);
			} else {
				bud.setProgress(EodConstants.PROGRESS_FAILED);
				bud.setErrorDesc("Receipt Amount Should not be greater than Payable Amount");
			}
		}

		if (!payableExists) {
			bud.setProgress(EodConstants.PROGRESS_FAILED);
			bud.setErrorDesc("Payable Advises are not found for the Loan Reference : " + finreference
					+ " and Fee Type Code : " + feeType);
		}

		return pdList;
	}

	private PaymentDetail getNewPD(ManualAdvise ma) {
		PaymentDetail pd = new PaymentDetail();

		pd.setNewRecord(true);
		pd.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		pd.setReferenceId(ma.getAdviseID());
		pd.setAvailableAmount(ma.getAdviseAmount().subtract(ma.getPaidAmount()).subtract(ma.getWaivedAmount()));
		pd.setAmountType(String.valueOf(ma.getAdviseType()));
		pd.setFeeTypeCode(ma.getFeeTypeCode());
		pd.setFeeTypeDesc(ma.getFeeTypeDesc());
		pd.setAdviseAmount(ma.getAdviseAmount());
		pd.setFinSource(UploadConstants.FINSOURCE_ID_UPLOAD);

		BigDecimal paidTGST = CalculationUtil.getTotalPaidGST(ma);
		BigDecimal waivedTGST = CalculationUtil.getTotalWaivedGST(ma);

		pd.setPrvGST(paidTGST.add(waivedTGST));
		pd.setManualAdvise(ma);
		pd.setTaxApplicable(ma.isTaxApplicable());
		pd.setTaxComponent(ma.getTaxComponent());

		return pd;
	}

	private List<PaymentDetail> prepareExcess(PaymentInstUploadDetail bud) {
		List<PaymentDetail> pdList = new ArrayList<>();

		List<FinExcessAmount> feaList = paymentHeaderService.getfinExcessAmount(bud.getReferenceID());

		boolean excessExists = false;

		BigDecimal balAmt = bud.getPayAmount();

		for (FinExcessAmount fea : feaList) {
			if (balAmt.compareTo(BigDecimal.ZERO) <= 0) {
				break;
			}

			if (!RepayConstants.EXAMOUNTTYPE_EXCESS.equals(fea.getAmountType())) {
				continue;
			}

			if (fea.getBalanceAmt().compareTo(BigDecimal.ZERO) <= 0) {
				continue;
			}

			excessExists = true;

			PaymentDetail pd = getNewPD(fea);

			if (fea.getBalanceAmt().compareTo(bud.getPayAmount()) <= 0) {
				pd.setAmount(fea.getBalanceAmt());
				balAmt = bud.getPayAmount().subtract(pd.getAvailableAmount());
			} else {
				pd.setAmount(balAmt);
				balAmt = BigDecimal.ZERO;
			}

			pdList.add(pd);
		}

		if (!excessExists || balAmt.compareTo(BigDecimal.ZERO) > 0) {
			bud.setProgress(EodConstants.PROGRESS_FAILED);
			bud.setErrorDesc("Excess Details are not found for the Loan Reference :" + bud.getReference());
		} else {
			bud.setProgress(EodConstants.PROGRESS_SUCCESS);
			bud.setErrorCode("");
			bud.setErrorDesc("");
		}

		return pdList;
	}

	private PaymentDetail getNewPD(FinExcessAmount fea) {
		PaymentDetail pd = new PaymentDetail();

		pd.setNewRecord(true);
		pd.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		pd.setReferenceId(fea.getId());
		pd.setAvailableAmount(fea.getBalanceAmt());
		pd.setAmountType(fea.getAmountType());
		pd.setFinSource(UploadConstants.FINSOURCE_ID_UPLOAD);

		return pd;
	}

	private void processPayIns(PaymentHeader paymentHeader, PaymentInstUploadDetail pid) {
		String tranType = PennantConstants.TRAN_WF;

		AuditHeader ah = getAuditDetail(paymentHeader, tranType);
		ah.setAuditTranType(PennantConstants.TRAN_WF);
		ah.getAuditDetail().setModelData(paymentHeader);
		auditHeaderDAO.addAudit(ah);

		AuditHeader audH = paymentHeaderService.doApprove(ah);
		if (audH.getErrorMessage() != null) {
			pid.setProgress(EodConstants.PROGRESS_FAILED);
			for (ErrorDetail errorDetail : audH.getErrorMessage()) {
				pid.setErrorCode(errorDetail.getCode());
				pid.setErrorDesc(errorDetail.getMessage());
			}
		}
	}

	private AuditHeader getAuditDetail(PaymentHeader ph, String tranType) {
		AuditDetail ad = new AuditDetail(tranType, 1, ph.getBefImage(), ph);

		return new AuditHeader(String.valueOf(ph.getId()), null, null, null, ad, ph.getUserDetails(), new HashMap<>());
	}

	private void setError(PaymentInstUploadDetail detail, PaymentUploadError error) {
		detail.setProgress(EodConstants.PROGRESS_FAILED);
		detail.setErrorCode(error.name());
		detail.setErrorDesc(error.description());
	}

	@Override
	public void uploadProcess() {
		uploadProcess(UploadTypes.PAYMINS.name(), this, "PaymentInstructionUploadHeader");
	}

	@Override
	public void validate(DataEngineAttributes attributes, MapSqlParameterSource paramSource) throws Exception {
		logger.debug(Literal.ENTERING);

		PaymentInstUploadDetail pd = (PaymentInstUploadDetail) ObjectUtil.valueAsObject(paramSource,
				PaymentInstUploadDetail.class);

		pd.setReference(ObjectUtil.valueAsString(paramSource.getValue("finReference")));

		Map<String, Object> parameterMap = attributes.getParameterMap();

		FileUploadHeader header = (FileUploadHeader) parameterMap.get("FILE_UPLAOD_HEADER");

		pd.setHeaderId(header.getId());
		pd.setAppDate(header.getAppDate());

		doValidate(header, pd);

		updateProcess(header, pd, paramSource);

		logger.debug(Literal.LEAVING);
	}

	@Autowired
	public void setFinOverDueService(FinOverDueService finOverDueService) {
		this.finOverDueService = finOverDueService;
	}

	@Autowired
	public void setFeeRefundHeaderService(FeeRefundHeaderService feeRefundHeaderService) {
		this.feeRefundHeaderService = feeRefundHeaderService;
	}

	@Autowired
	public void setPaymentHeaderService(PaymentHeaderService paymentHeaderService) {
		this.paymentHeaderService = paymentHeaderService;
	}

	@Autowired
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	@Autowired
	public void setPaymentInstructionUploadDAO(PaymentInstructionUploadDAO paymentInstructionUploadDAO) {
		this.paymentInstructionUploadDAO = paymentInstructionUploadDAO;
	}

	@Autowired
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	@Autowired
	public void setFinExcessAmountDAO(FinExcessAmountDAO finExcessAmountDAO) {
		this.finExcessAmountDAO = finExcessAmountDAO;
	}

	@Autowired
	public void setPaymentInstructionDAO(PaymentInstructionDAO paymentInstructionDAO) {
		this.paymentInstructionDAO = paymentInstructionDAO;
	}

	@Autowired
	public void setFeeTypeDAO(FeeTypeDAO feeTypeDAO) {
		this.feeTypeDAO = feeTypeDAO;
	}

	@Autowired
	public void setManualAdviseDAO(ManualAdviseDAO manualAdviseDAO) {
		this.manualAdviseDAO = manualAdviseDAO;
	}

	@Autowired
	public void setRefundBeneficiary(RefundBeneficiary refundBeneficiary) {
		this.refundBeneficiary = refundBeneficiary;
	}

}