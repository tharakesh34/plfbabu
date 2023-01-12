package com.pennant.backend.service.payment.impl;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.paymentinstuploadqueue.PaymentInstBulkUploadQueuing;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.payment.PaymentInstructionUploadDAO;
import com.pennant.backend.dao.paymentinstupload.ProjectedPaymentInstUploadDAO;
import com.pennant.backend.service.payment.PaymentHeaderService;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.eod.constants.EodConstants;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.autorefund.RefundBeneficiary;

public class PaymentInstUploadApprovalProcess {
	private static final Logger logger = LogManager.getLogger(PaymentInstUploadApprovalProcess.class);

	private DataSource dataSource;
	private ProjectedPaymentInstUploadDAO projectedPaymentInstUploadDAO;
	private PaymentInstructionUploadDAO paymentInstructionUploadDAO;
	private PaymentHeaderService paymentHeaderService;
	private AuditHeaderDAO auditHeaderDAO;
	private RefundBeneficiary refundBeneficiary;

	public void approvePaymentInst(List<Long> uploadIds) {
		logger.debug(Literal.ENTERING);

		projectedPaymentInstUploadDAO.insertLogTableAndTruncate();

		List<PaymentInstBulkUploadQueuing> uploadQueuings = new ArrayList<>();
		for (Long headerId : uploadIds) {
			PaymentInstBulkUploadQueuing payInstQueuing = new PaymentInstBulkUploadQueuing();
			payInstQueuing.setThreadId(0);
			payInstQueuing.setEodDate(SysParamUtil.getAppDate());
			payInstQueuing.setEodProcess(false);
			payInstQueuing.setProgress(EodConstants.PROGRESS_WAIT);
			payInstQueuing.setUploadHeaderId(headerId);
			payInstQueuing.setStartTime(DateUtility.getSysDate());

			uploadQueuings.add(payInstQueuing);
		}

		projectedPaymentInstUploadDAO.preparePaymentInstBulkUploadQueue(uploadQueuings);

		updateThread();

		List<Long> threads = projectedPaymentInstUploadDAO.getThreads();

		for (Long threadId : threads) {
			PaymentInstUploadThreadProcess threadProcess = new PaymentInstUploadThreadProcess(dataSource,
					projectedPaymentInstUploadDAO, paymentInstructionUploadDAO, paymentHeaderService, auditHeaderDAO,
					refundBeneficiary);
			threadProcess.processesThread(threadId);
		}

		logger.debug(Literal.LEAVING);

	}

	private void updateThread() {
		long finsCount = projectedPaymentInstUploadDAO.getCountByProgress();

		if (finsCount == 0) {
			return;
		}

		int threadCount = SysParamUtil.getValueAsInt(SMTParameterConstants.REFUND_UPLOAD_THREAD_COUNT);
		long noOfRows = finsCount / threadCount;

		boolean recordsLessThanThread = false;

		if (finsCount < threadCount) {
			recordsLessThanThread = true;
			noOfRows = 1;
		}

		long temprows = noOfRows;
		for (int i = 1; i <= threadCount; i++) {

			if (i == threadCount) {
				projectedPaymentInstUploadDAO.updateThreadIDByRowNumber(0, i);
			} else {
				projectedPaymentInstUploadDAO.updateThreadIDByRowNumber(temprows, i);
			}

			if (recordsLessThanThread && i == finsCount) {
				break;
			}

			temprows = temprows + noOfRows;
		}
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setProjectedPaymentInstUploadDAO(ProjectedPaymentInstUploadDAO projectedPaymentInstUploadDAO) {
		this.projectedPaymentInstUploadDAO = projectedPaymentInstUploadDAO;
	}

	public void setPaymentInstructionUploadDAO(PaymentInstructionUploadDAO paymentInstructionUploadDAO) {
		this.paymentInstructionUploadDAO = paymentInstructionUploadDAO;
	}

	public void setPaymentHeaderService(PaymentHeaderService paymentHeaderService) {
		this.paymentHeaderService = paymentHeaderService;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public void setRefundBeneficiary(RefundBeneficiary refundBeneficiary) {
		this.refundBeneficiary = refundBeneficiary;
	}

}
