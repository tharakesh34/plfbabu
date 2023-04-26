package com.pennant.backend.service.feerefund.impl;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.pennant.app.feerefundqueue.FeeRefundProcessQueuing;
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
import com.pennant.backend.service.feerefund.FeeRefundHeaderService;
import com.pennant.backend.service.finance.ManualAdviseService;
import com.pennant.backend.service.payment.PaymentHeaderService;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.ReceiptUploadConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.fee.AdviseType;
import com.pennant.pff.feerefund.FeeRefundUtil;
import com.pennant.pff.payment.model.PaymentDetail;
import com.pennant.pff.payment.model.PaymentHeader;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.core.TableType;

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
				feeRefundDetailList = feeRefundDetailDAO.getFeeRefundDetailList(feeRefundId, TableType.VIEW);
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
				frd.setPayableID(adviseId);
				updatePayableId(frd.getId(), adviseId);
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

	public void updatePayableId(long id, long adviseId) {
		feeRefundDetailDAO.updatePayableId(id, adviseId);
	}

	private long createPayableAdvise(FeeRefundDetail frd, String finreference, long finID) {

		ManualAdvise ma = new ManualAdvise();
		ma.setAdviseID(manualAdviseService.getNewAdviseID());
		ma.setFinReference(finreference);
		ma.setFinID(finID);
		ma.setAdviseType(AdviseType.PAYABLE.id());
		ma.setAdviseAmount(frd.getRefundAmount());
		ma.setBalanceAmt(BigDecimal.ZERO);
		ma.setHoldDue(false);
		ma.setFinSource(FinanceConstants.FEE_REFUND_APPROVAL);

		FeeType payableFeeType = feeTypeDAO.getFeeTypeById(frd.getPayableFeeTypeID(), "");
		ma.setFeeType(payableFeeType);
		ma.setFeeTypeCode(payableFeeType.getFeeTypeCode());
		ma.setFeeTypeID(payableFeeType.getFeeTypeID());

		ma.setValueDate(SysParamUtil.getAppDate());
		ma.setPostDate(SysParamUtil.getAppDate());
		ma.setVersion(1);
		ma.setLastMntBy(frd.getLastMntBy());
		ma.setLastMntOn(frd.getLastMntOn());
		ma.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		ma.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		ma.setNewRecord(true);
		ma.setUserDetails(frd.getUserDetails());

		AuditHeader auditHeader = manualAdviseService.doApprove(getAuditHeader(ma, PennantConstants.TRAN_WF));
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

		Timestamp sysDate = new Timestamp(System.currentTimeMillis());

		PaymentHeader ph = new PaymentHeader();
		ph.setFinReference(frh.getFinReference());
		ph.setFinID(frh.getFinID());
		ph.setPaymentType(DisbursementConstants.CHANNEL_PAYMENT);
		ph.setCreatedOn(sysDate);
		ph.setApprovedOn(sysDate);
		ph.setStatus(RepayConstants.PAYMENT_APPROVE);
		ph.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		ph.setNewRecord(true);
		ph.setVersion(1);
		ph.setUserDetails(frh.getUserDetails());
		ph.setLastMntBy(frh.getLastMntBy());
		ph.setLastMntOn(frh.getLastMntOn());
		ph.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		ph.setPaymentId(paymentHeaderDAO.getNewPaymentHeaderId());
		ph.setFinSource(FinanceConstants.FEE_REFUND_APPROVAL);

		List<PaymentDetail> paymentDetailList = new ArrayList<>();

		for (FeeRefundDetail frd : frdList) {
			PaymentDetail pd = new PaymentDetail();
			FeeType payableFeeType = feeTypeDAO.getFeeTypeById(frd.getPayableFeeTypeID(), "");
			pd.setPaymentId(ph.getPaymentId());
			pd.setAmount(frd.getRefundAmount());
			ph.setPaymentAmount(frd.getRefundAmount());
			pd.setReferenceId(frd.getPayableID());
			pd.setAvailableAmount(frd.getRefundAmount());
			pd.setAmountType(String.valueOf(AdviseType.PAYABLE.id()));
			pd.setFeeTypeCode(payableFeeType.getFeeTypeCode());
			pd.setFeeTypeDesc(payableFeeType.getFeeTypeDesc());
			pd.setRecordType(PennantConstants.RCD_ADD);
			pd.setNewRecord(true);
			pd.setVersion(1);
			pd.setUserDetails(frh.getUserDetails());
			pd.setLastMntBy(frh.getLastMntBy());
			pd.setLastMntOn(frh.getLastMntOn());
			pd.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			pd.setApiRequest(false);
			pd.setTaxApplicable(payableFeeType.isTaxApplicable());
			pd.setTaxComponent(payableFeeType.getTaxComponent());
			pd.setFinSource(FinanceConstants.FEE_REFUND_APPROVAL);
			paymentDetailList.add(pd);
		}

		PaymentInstruction pi = FeeRefundUtil.getPI(fri);
		pi.setValueDate(appDate);
		pi.setStatus(DisbursementConstants.STATUS_NEW);
		pi.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		pi.setRecordType(PennantConstants.RCD_ADD);
		pi.setNewRecord(true);
		pi.setVersion(1);
		pi.setUserDetails(frh.getUserDetails());
		pi.setLastMntBy(frh.getLastMntBy());
		pi.setLastMntOn(frh.getLastMntOn());

		ph.setPaymentDetailList(paymentDetailList);
		ph.setPaymentInstruction(pi);

		logger.debug("Leaving");
		return ph;
	}

	private AuditHeader getAuditHeader(PaymentHeader paymentHeader, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, paymentHeader.getBefImage(), paymentHeader);
		return new AuditHeader(String.valueOf(paymentHeader.getPaymentId()),
				String.valueOf(paymentHeader.getPaymentId()), null, null, auditDetail, paymentHeader.getUserDetails(),
				new HashMap<>());
	}

	private AuditHeader getAuditHeader(ManualAdvise aManualAdvise, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aManualAdvise.getBefImage(), aManualAdvise);
		return new AuditHeader(String.valueOf(aManualAdvise.getAdviseID()), String.valueOf(aManualAdvise.getAdviseID()),
				null, null, auditDetail, aManualAdvise.getUserDetails(), new HashMap<>());
	}

	private void updateFailed(long uploadHeaderId, String errorLog) {
		FeeRefundProcessQueuing auQueuing = new FeeRefundProcessQueuing();

		auQueuing.setFeeRefundHeaderId(uploadHeaderId);
		auQueuing.setEndTime(DateUtil.getSysDate());
		auQueuing.setErrorLog(errorLog);
		projectedFeeRefundProcessDAO.updateFailedQueue(auQueuing);
	}

	private void initilize() {
		jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);

		this.transactionManager = new DataSourceTransactionManager(dataSource);
		this.transactionDefinition = new DefaultTransactionDefinition();
		this.transactionDefinition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		this.transactionDefinition.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);

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
