package com.pennant.backend.service.payment.impl;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.pennant.app.paymentinstuploadqueue.PaymentInstBulkUploadQueuing;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.payment.PaymentInstructionUploadDAO;
import com.pennant.backend.dao.paymentinstupload.ProjectedPaymentInstUploadDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.payment.PaymentDetail;
import com.pennant.backend.model.payment.PaymentHeader;
import com.pennant.backend.model.payment.PaymentInstUploadDetail;
import com.pennant.backend.service.payment.PaymentHeaderService;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.backend.util.UploadConstants;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.paymentupload.exception.PaymentUploadError;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.autorefund.RefundBeneficiary;

public class PaymentInstUploadThreadProcess {
	private static Logger logger = LogManager.getLogger(PaymentInstUploadThreadProcess.class);

	private static final String QUERY = "Select Finreference, uploadheaderid, uploaddetailid from PaymentInstUploadQueuing  Where ThreadID = :ThreadId and Progress = :Progress order by Finreference,uploaddetailid";

	private DataSource dataSource;
	private ProjectedPaymentInstUploadDAO projectedPaymentInstUploadDAO;

	private NamedParameterJdbcTemplate jdbcTemplate;
	private DataSourceTransactionManager transactionManager;
	private DefaultTransactionDefinition transactionDefinition;

	private PaymentInstructionUploadDAO paymentInstructionUploadDAO;
	private PaymentHeaderService paymentHeaderService;
	private AuditHeaderDAO auditHeaderDAO;
	private RefundBeneficiary refundBeneficiary;

	public PaymentInstUploadThreadProcess(DataSource dataSource,
			ProjectedPaymentInstUploadDAO projectedPaymentInstUploadDAO,
			PaymentInstructionUploadDAO paymentInstructionUploadDAO, PaymentHeaderService paymentHeaderService,
			AuditHeaderDAO auditHeaderDAO, RefundBeneficiary refundBeneficiary) {
		super();

		this.dataSource = dataSource;
		this.projectedPaymentInstUploadDAO = projectedPaymentInstUploadDAO;
		this.paymentInstructionUploadDAO = paymentInstructionUploadDAO;
		this.paymentHeaderService = paymentHeaderService;
		this.auditHeaderDAO = auditHeaderDAO;
		this.refundBeneficiary = refundBeneficiary;

		initilize();
	}

	public void processesThread(long threadId) {
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Progress", EodConstants.PROGRESS_WAIT);
		source.addValue("ThreadId", threadId);

		jdbcTemplate.query(QUERY, source, new RowCallbackHandler() {
			PaymentInstUploadDetail uploadDetail = null;

			@Override
			public void processRow(ResultSet rs) throws SQLException {
				long uploadheaderid = rs.getLong(2);
				long uploaddetailid = rs.getLong(3);

				uploadDetail = paymentInstructionUploadDAO.getDetails(uploadheaderid, uploaddetailid);

				processRefunds(uploadDetail);
			}
		});
	}

	private void processRefunds(PaymentInstUploadDetail detail) {

		// Process Payment Header

		long uploadheaderid = detail.getHeaderId();
		long uploaddetailid = detail.getId();

		TransactionStatus transactionStatus = this.transactionManager.getTransaction(transactionDefinition);

		try {
			List<ManualAdvise> malist = new ArrayList<>();
			List<FinExcessAmount> feaList = new ArrayList<>();

			Date appDate = SysParamUtil.getAppDate();

			PaymentHeader ph = new PaymentHeader();

			String finreference = detail.getReference();
			Long finId = detail.getReferenceID();

			ph = new PaymentHeader();
			ph.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			ph.setFinSource(UploadConstants.FINSOURCE_ID_UPLOAD);

			ph.setFinReference(finreference);
			ph.setFinID(finId);
			ph.setPaymentType(DisbursementConstants.CHANNEL_PAYMENT);
			ph.setCreatedOn(appDate);
			ph.setPaymentAmount(detail.getPayAmount());
			ph.setApprovedOn(appDate);
			ph.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			boolean alwRefundByCheque = SysParamUtil.isAllowed(SMTParameterConstants.AUTO_REFUND_THROUGH_CHEQUE);
			ph.setPaymentInstruction(refundBeneficiary.getBeneficiary(finId, appDate, alwRefundByCheque));
			ph.getPaymentInstruction().setPaymentAmount(detail.getPayAmount());
			ph.getPaymentInstruction().setPostDate(appDate);
			if (RepayConstants.EXAMOUNTTYPE_PAYABLE.equals(detail.getExcessType())) {
				preparePayable(ph, malist, detail, finId, finreference);
			}

			if (RepayConstants.EXAMOUNTTYPE_EXCESS.equals(detail.getExcessType())) {
				prepareExcess(ph, feaList, detail, finId, finreference);
			}

			if (EodConstants.PROGRESS_FAILED != detail.getProgress()) {
				processPayIns(ph, detail);
			}

			if (EodConstants.PROGRESS_FAILED == detail.getProgress()) {
				this.paymentInstructionUploadDAO.update(detail);
			}

			this.transactionManager.commit(transactionStatus);

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			transactionManager.rollback(transactionStatus);

			String error = StringUtils.trimToEmpty(e.getMessage());

			if (error.length() > 1999) {
				error = error.substring(0, 1999);
			}
			updateFailed(uploadheaderid, uploaddetailid, error);

			detail.setProgress(EodConstants.PROGRESS_FAILED);
			detail.setErrorDesc(error);
			this.paymentInstructionUploadDAO.update(detail);
		}
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

			balAmt = pd.getAvailableAmount().subtract(bud.getPayAmount());
			if (balAmt.compareTo(BigDecimal.ZERO) < 0) {
				bud.setProgress(EodConstants.PROGRESS_FAILED);
				bud.setErrorCode(PaymentUploadError.REFUP008.name());
				bud.setErrorDesc(PaymentUploadError.REFUP008.description());
			}

			bud.setProgress(EodConstants.PROGRESS_SUCCESS);
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
		pd.setFinSource(UploadConstants.FINSOURCE_ID_UPLOAD);
		return pd;
	}

	private void preparePayable(PaymentHeader ph, List<ManualAdvise> malist, PaymentInstUploadDetail bud, Long finId,
			String finreference) {

		if (CollectionUtils.isEmpty(malist)) {
			malist = paymentHeaderService.getManualAdvise(finId);
		}

		if (CollectionUtils.isEmpty(malist)) {
			bud.setProgress(EodConstants.PROGRESS_FAILED);
			bud.setErrorDesc("Payable Advises are not found for the Loan Reference");
		}

		boolean payableExists = false;

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
			bud.setProgress(EodConstants.PROGRESS_FAILED);
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
		pd.setFinSource(UploadConstants.FINSOURCE_ID_UPLOAD);

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

	private void updateFailed(long uploadHeaderId, long uploadDetailId, String errorLog) {
		PaymentInstBulkUploadQueuing auQueuing = new PaymentInstBulkUploadQueuing();

		auQueuing.setUploadHeaderId(uploadHeaderId);
		auQueuing.setUploadDetailId(uploadDetailId);
		auQueuing.setEndTime(DateUtility.getSysDate());
		auQueuing.setErrorLog(errorLog);
		projectedPaymentInstUploadDAO.updateFailedQueue(auQueuing);
	}

	private void initilize() {
		jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);

		this.transactionManager = new DataSourceTransactionManager(dataSource);
		this.transactionDefinition = new DefaultTransactionDefinition();
		this.transactionDefinition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		this.transactionDefinition.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);

		this.transactionDefinition.setTimeout(600);

	}

}
