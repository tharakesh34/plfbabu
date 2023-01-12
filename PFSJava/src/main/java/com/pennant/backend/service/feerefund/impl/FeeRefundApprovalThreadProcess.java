package com.pennant.backend.service.feerefund.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.pennant.app.feerefundqueue.FeeRefundProcessQueuing;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.feerefund.FeeRefundDetailDAO;
import com.pennant.backend.dao.feerefund.FeeRefundHeaderDAO;
import com.pennant.backend.dao.feerefund.FeeRefundInstructionDAO;
import com.pennant.backend.dao.feerefundprocess.ProjectedFeeRefundProcessDAO;
import com.pennant.backend.dao.feetype.FeeTypeDAO;
import com.pennant.backend.dao.payment.PaymentHeaderDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.feerefund.FeeRefundDetail;
import com.pennant.backend.model.feerefund.FeeRefundHeader;
import com.pennant.backend.model.feerefund.FeeRefundInstruction;
import com.pennant.backend.model.finance.FeeType;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.PaymentInstruction;
import com.pennant.backend.model.payment.PaymentDetail;
import com.pennant.backend.model.payment.PaymentHeader;
import com.pennant.backend.service.feerefund.FeeRefundHeaderService;
import com.pennant.backend.service.finance.ManualAdviseService;
import com.pennant.backend.service.payment.PaymentHeaderService;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.ReceiptUploadConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennant.eod.constants.EodConstants;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;

public class FeeRefundApprovalThreadProcess {
	private static Logger logger = LogManager.getLogger(FeeRefundApprovalThreadProcess.class);

	private static final String QUERY = "Select FinReference, feeRefundHeaderId from FeeRefundProcessQueuing  Where ThreadID = :ThreadId and Progress = :Progress order by feeRefundHeaderId";

	private DataSource dataSource;
	private ProjectedFeeRefundProcessDAO projectedFeeRefundProcessDAO;
	private FeeRefundHeaderDAO feeRefundHeaderDAO;
	private FeeRefundDetailDAO feeRefundDetailDAO;
	private FeeRefundInstructionDAO feeRefundInstructionDAO;
	private PaymentHeaderService paymentHeaderService;
	private ManualAdviseService manualAdviseService;
	private FeeTypeDAO feeTypeDAO;
	private PaymentHeaderDAO paymentHeaderDAO;
	private FeeRefundHeaderService feeRefundHeaderService;

	private NamedParameterJdbcTemplate jdbcTemplate;
	private DataSourceTransactionManager transactionManager;
	private DefaultTransactionDefinition transactionDefinition;

	public FeeRefundApprovalThreadProcess(DataSource dataSource,
			ProjectedFeeRefundProcessDAO projectedFeeRefundProcessDAO, FeeRefundHeaderDAO feeRefundHeaderDAO,
			FeeRefundDetailDAO feeRefundDetailDAO, FeeRefundInstructionDAO feeRefundInstructionDAO,
			PaymentHeaderService paymentHeaderService, ManualAdviseService manualAdviseService, FeeTypeDAO feeTypeDAO,
			PaymentHeaderDAO paymentHeaderDAO, FeeRefundHeaderService feeRefundHeaderService) {
		super();

		this.dataSource = dataSource;
		this.projectedFeeRefundProcessDAO = projectedFeeRefundProcessDAO;
		this.feeRefundHeaderDAO = feeRefundHeaderDAO;
		this.feeRefundDetailDAO = feeRefundDetailDAO;
		this.feeRefundInstructionDAO = feeRefundInstructionDAO;
		this.paymentHeaderService = paymentHeaderService;
		this.manualAdviseService = manualAdviseService;
		this.feeTypeDAO = feeTypeDAO;
		this.paymentHeaderDAO = paymentHeaderDAO;
		this.feeRefundHeaderService = feeRefundHeaderService;

		initilize();
	}

	public void processesThread(long threadId) {
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Progress", EodConstants.PROGRESS_WAIT);
		source.addValue("ThreadId", threadId);

		jdbcTemplate.query(QUERY, source, new RowCallbackHandler() {
			FeeRefundHeader frh = null;
			List<FeeRefundDetail> feeRefundDetailList = null;
			FeeRefundInstruction fri = null;

			@Override
			public void processRow(ResultSet rs) throws SQLException {
				long feeRefundId = rs.getLong(2);

				frh = feeRefundHeaderDAO.getFeeRefundHeader(feeRefundId, "_View");
				feeRefundDetailList = feeRefundDetailDAO.getFeeRefundDetailList(feeRefundId, "_View");
				fri = feeRefundInstructionDAO.getFeeRefundInstructionDetails(feeRefundId, "_View");

				processFeeRefundApproval(frh, feeRefundDetailList, fri);
			}
		});
	}

	private void processFeeRefundApproval(FeeRefundHeader frh, List<FeeRefundDetail> feeRefundDetailList,
			FeeRefundInstruction fri) {

		logger.debug(Literal.ENTERING);

		long feeRefundId = frh.getId();
		String finReference = frh.getFinReference();

		TransactionStatus transactionStatus = this.transactionManager.getTransaction(transactionDefinition);

		try {

			for (FeeRefundDetail frd : feeRefundDetailList) {
				long adviseId = createPayableAdvise(frd, finReference, frh.getFinID());
				frd.setPayableRefId(adviseId);
				updatePayableRef(adviseId, frd.getId());
			}

			createPaymentInstruction(frh, feeRefundDetailList, fri);
			this.feeRefundHeaderService.updateApprovalStatus(frh.getId(), PennantConstants.FEE_REFUND_APPROVAL_SUCCESS);
			this.projectedFeeRefundProcessDAO.updateStatusQueue(feeRefundId, ReceiptUploadConstants.PROGRESS_SUCCESS);

			this.transactionManager.commit(transactionStatus);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			transactionManager.rollback(transactionStatus);

			String error = StringUtils.trimToEmpty(e.getMessage());

			if (error.length() > 1999) {
				error = error.substring(0, 1999);
			}
			this.feeRefundHeaderService.updateApprovalStatus(frh.getId(), PennantConstants.FEE_REFUND_APPROVAL_FAILED);
			updateFailed(feeRefundId, error);

		}

		logger.debug(Literal.LEAVING);
	}

	public void updatePayableRef(long adviseId, long id) {
		feeRefundDetailDAO.updatePayableRef(adviseId, id);
	}

	private long createPayableAdvise(FeeRefundDetail frd, String finreference, long finID) {

		ManualAdvise manualAdvise = new ManualAdvise();
		manualAdvise.setAdviseID(manualAdviseService.getNewAdviseID());
		manualAdvise.setFinReference(finreference);
		manualAdvise.setFinID(finID);
		manualAdvise.setAdviseType(FinanceConstants.MANUAL_ADVISE_PAYABLE);
		manualAdvise.setAdviseAmount(frd.getCurrRefundAmount());
		manualAdvise.setBalanceAmt(manualAdvise.getAdviseAmount());
		manualAdvise.setHoldDue(false);
		manualAdvise.setFinSource(FinanceConstants.FEE_REFUND_APPROVAL);

		FeeType payableFeeType = feeTypeDAO.getApprovedFeeTypeByFeeCode(frd.getPayableFeeTypeCode());
		FeeType feeType = new FeeType();
		BeanUtils.copyProperties(payableFeeType, feeType);
		manualAdvise.setFeeType(feeType);
		manualAdvise.setFeeTypeCode(payableFeeType.getFeeTypeCode());
		manualAdvise.setFeeTypeID(payableFeeType.getFeeTypeID());

		manualAdvise.setValueDate(SysParamUtil.getAppDate());
		manualAdvise.setPostDate(SysParamUtil.getAppDate());
		manualAdvise.setVersion(1);
		manualAdvise.setLastMntBy(frd.getLastMntBy());
		manualAdvise.setLastMntOn(frd.getLastMntOn());
		manualAdvise.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		manualAdvise.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		manualAdvise.setNewRecord(true);
		manualAdvise.setUserDetails(frd.getUserDetails());

		// Save Cashback Advice
		AuditHeader auditHeader = manualAdviseService.doApprove(getAuditHeader(manualAdvise, PennantConstants.TRAN_WF));
		ManualAdvise md = ((ManualAdvise) auditHeader.getAuditDetail().getModelData());
		logger.debug(Literal.LEAVING);
		return md.getAdviseID();

	}

	public void createPaymentInstruction(FeeRefundHeader frh, List<FeeRefundDetail> frdList, FeeRefundInstruction fri) {
		logger.debug(Literal.ENTERING);

		PaymentHeader paymentHeader = preparePaymentInst(frh, frdList, fri);
		AuditHeader paymentsAuditHeader = getAuditHeader(paymentHeader, PennantConstants.TRAN_WF);

		paymentHeaderService.doApprove(paymentsAuditHeader);

		logger.debug(Literal.LEAVING);
	}

	private PaymentHeader preparePaymentInst(FeeRefundHeader frh, List<FeeRefundDetail> frdList,
			FeeRefundInstruction fri) {
		logger.debug("Entering");

		Date appDate = SysParamUtil.getAppDate();

		// Payment Header
		PaymentHeader paymentHeader = new PaymentHeader();
		paymentHeader.setFinReference(frh.getFinReference());
		paymentHeader.setFinID(frh.getFinID());
		paymentHeader.setPaymentType(DisbursementConstants.CHANNEL_PAYMENT);
		paymentHeader.setCreatedOn(appDate);
		paymentHeader.setApprovedOn(appDate);
		paymentHeader.setStatus(RepayConstants.PAYMENT_APPROVE);
		paymentHeader.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		paymentHeader.setNewRecord(true);
		paymentHeader.setVersion(1);
		paymentHeader.setUserDetails(frh.getUserDetails());
		paymentHeader.setLastMntBy(frh.getLastMntBy());
		paymentHeader.setLastMntOn(frh.getLastMntOn());
		paymentHeader.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		paymentHeader.setPaymentId(paymentHeaderDAO.getNewPaymentHeaderId());
		paymentHeader.setFinSource(FinanceConstants.FEE_REFUND_APPROVAL);

		// Payment Details
		List<PaymentDetail> paymentDetailList = new ArrayList<PaymentDetail>();

		// Payment Instruction Details preparation
		for (FeeRefundDetail frd : frdList) {
			PaymentDetail paymentDetail = new PaymentDetail();
			FeeType payableFeeType = feeTypeDAO.getApprovedFeeTypeByFeeCode(frd.getPayableFeeTypeCode());
			paymentDetail.setPaymentId(paymentHeader.getPaymentId());
			paymentDetail.setAmount(frd.getCurrRefundAmount());
			paymentHeader.setPaymentAmount(frd.getCurrRefundAmount());
			paymentDetail.setReferenceId(frd.getPayableRefId());
			paymentDetail.setAvailableAmount(frd.getCurrRefundAmount());
			paymentDetail.setAmountType(String.valueOf(FinanceConstants.MANUAL_ADVISE_PAYABLE));
			paymentDetail.setFeeTypeCode(frd.getPayableFeeTypeCode());
			paymentDetail.setFeeTypeDesc(frd.getPayableFeeTypeDesc());
			paymentDetail.setRecordType(PennantConstants.RCD_ADD);
			paymentDetail.setNewRecord(true);
			paymentDetail.setVersion(1);
			paymentDetail.setUserDetails(frh.getUserDetails());
			paymentDetail.setLastMntBy(frh.getLastMntBy());
			paymentDetail.setLastMntOn(frh.getLastMntOn());
			paymentDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			paymentDetail.setApiRequest(false);
			paymentDetail.setTaxApplicable(payableFeeType.isTaxApplicable());
			paymentDetail.setTaxComponent(payableFeeType.getTaxComponent());
			paymentDetail.setFinSource(FinanceConstants.FEE_REFUND_APPROVAL);
			paymentDetailList.add(paymentDetail);
		}

		// Payment Instructions
		PaymentInstruction paymentInstruction = new PaymentInstruction();
		paymentInstruction.setPostDate(appDate);
		paymentInstruction.setPaymentType(fri.getPaymentType());
		paymentInstruction.setPaymentAmount(fri.getPaymentAmount());
		paymentInstruction.setBankBranchCode(fri.getBankBranchCode());
		paymentInstruction.setBankBranchId(fri.getBankBranchId());
		paymentInstruction.setAcctHolderName(fri.getAcctHolderName());
		paymentInstruction.setAccountNo(fri.getAccountNo());
		paymentInstruction.setPhoneNumber(fri.getPhoneNumber());
		paymentInstruction.setValueDate(appDate);
		paymentInstruction.setPaymentCCy(fri.getPaymentCCy());
		paymentInstruction.setPartnerBankCode(fri.getPartnerBankCode());
		paymentInstruction.setPartnerBankId(fri.getPartnerBankId());
		paymentInstruction.setStatus(DisbursementConstants.STATUS_NEW);
		paymentInstruction.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		paymentInstruction.setRecordType(PennantConstants.RCD_ADD);
		paymentInstruction.setNewRecord(true);
		paymentInstruction.setVersion(1);
		paymentInstruction.setUserDetails(frh.getUserDetails());
		paymentInstruction.setLastMntBy(frh.getLastMntBy());
		paymentInstruction.setLastMntOn(frh.getLastMntOn());

		// Extra validation fields
		paymentInstruction.setPartnerBankAcType(fri.getPartnerBankAcType());

		paymentHeader.setPaymentDetailList(paymentDetailList);
		paymentHeader.setPaymentInstruction(paymentInstruction);

		logger.debug("Leaving");
		return paymentHeader;
	}

	private AuditHeader getAuditHeader(PaymentHeader paymentHeader, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, paymentHeader.getBefImage(), paymentHeader);
		return new AuditHeader(String.valueOf(paymentHeader.getPaymentId()),
				String.valueOf(paymentHeader.getPaymentId()), null, null, auditDetail, paymentHeader.getUserDetails(),
				new HashMap<String, List<ErrorDetail>>());
	}

	private AuditHeader getAuditHeader(ManualAdvise aManualAdvise, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aManualAdvise.getBefImage(), aManualAdvise);
		return new AuditHeader(String.valueOf(aManualAdvise.getAdviseID()), String.valueOf(aManualAdvise.getAdviseID()),
				null, null, auditDetail, aManualAdvise.getUserDetails(), new HashMap<String, List<ErrorDetail>>());
	}

	private void updateFailed(long uploadHeaderId, String errorLog) {
		FeeRefundProcessQueuing auQueuing = new FeeRefundProcessQueuing();

		auQueuing.setFeeRefundHeaderId(uploadHeaderId);
		auQueuing.setEndTime(DateUtility.getSysDate());
		auQueuing.setErrorLog(errorLog);
		projectedFeeRefundProcessDAO.updateFailedQueue(auQueuing);
	}

	private void initilize() {
		jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);

		this.transactionManager = new DataSourceTransactionManager(dataSource);
		this.transactionDefinition = new DefaultTransactionDefinition();
		this.transactionDefinition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		this.transactionDefinition.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);

		// FIXME: PV change the time to 60 seocnds after code review completed
		this.transactionDefinition.setTimeout(600);

	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setProjectedFeeRefundProcessDAO(ProjectedFeeRefundProcessDAO projectedFeeRefundProcessDAO) {
		this.projectedFeeRefundProcessDAO = projectedFeeRefundProcessDAO;
	}

	public void setFeeRefundHeaderDAO(FeeRefundHeaderDAO feeRefundHeaderDAO) {
		this.feeRefundHeaderDAO = feeRefundHeaderDAO;
	}

	public void setFeeRefundDetailDAO(FeeRefundDetailDAO feeRefundDetailDAO) {
		this.feeRefundDetailDAO = feeRefundDetailDAO;
	}

	public void setFeeRefundInstructionDAO(FeeRefundInstructionDAO feeRefundInstructionDAO) {
		this.feeRefundInstructionDAO = feeRefundInstructionDAO;
	}

	public void setPaymentHeaderService(PaymentHeaderService paymentHeaderService) {
		this.paymentHeaderService = paymentHeaderService;
	}

	public void setManualAdviseService(ManualAdviseService manualAdviseService) {
		this.manualAdviseService = manualAdviseService;
	}

	public void setFeeTypeDAO(FeeTypeDAO feeTypeDAO) {
		this.feeTypeDAO = feeTypeDAO;
	}

	public void setPaymentHeaderDAO(PaymentHeaderDAO paymentHeaderDAO) {
		this.paymentHeaderDAO = paymentHeaderDAO;
	}

	public void setFeeRefundHeaderService(FeeRefundHeaderService feeRefundHeaderService) {
		this.feeRefundHeaderService = feeRefundHeaderService;
	}

}
