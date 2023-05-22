package com.pennant.backend.service.feerefund.impl;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.feerefundqueue.FeeRefundProcessQueuing;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.feerefund.FeeRefundDetailDAO;
import com.pennant.backend.dao.feerefund.FeeRefundHeaderDAO;
import com.pennant.backend.dao.feerefund.FeeRefundInstructionDAO;
import com.pennant.backend.dao.feerefundprocess.ProjectedFeeRefundProcessDAO;
import com.pennant.backend.dao.feetype.FeeTypeDAO;
import com.pennant.backend.dao.payment.PaymentHeaderDAO;
import com.pennant.backend.service.feerefund.FeeRefundHeaderService;
import com.pennant.backend.service.finance.ManualAdviseService;
import com.pennant.backend.service.payment.PaymentHeaderService;
import com.pennant.backend.util.ReceiptUploadConstants;
import com.pennant.eod.constants.EodConstants;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;

public class FeeRefundApprovalProcess {
	private static final Logger logger = LogManager.getLogger(FeeRefundApprovalProcess.class);

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

	public void approveFeeRefunds(List<Long> feeRefundIds, LoggedInUser loggedInUser) {
		logger.debug(Literal.ENTERING);

		// FIXME Delete me based on selected batch.
		projectedFeeRefundProcessDAO.insertLogTableAndTruncate();

		List<FeeRefundProcessQueuing> refundQueuings = new ArrayList<>();
		for (Long headerId : feeRefundIds) {
			FeeRefundProcessQueuing frQueuing = new FeeRefundProcessQueuing();
			frQueuing.setThreadId(0);
			frQueuing.setEodDate(SysParamUtil.getAppDate());
			frQueuing.setEodProcess(false);
			frQueuing.setProgress(EodConstants.PROGRESS_WAIT);
			frQueuing.setFeeRefundHeaderId(headerId);
			frQueuing.setStartTime(DateUtil.getSysDate());

			refundQueuings.add(frQueuing);
		}

		// FIXME when i am increase more than 1000
		projectedFeeRefundProcessDAO.prepareFeeRefundProcessQueue(refundQueuings);

		updateThread();

		List<Long> threads = projectedFeeRefundProcessDAO.getThreads();

		for (Long threadId : threads) {
			FeeRefundApprovalThreadProcess threadProcess = new FeeRefundApprovalThreadProcess(dataSource,
					projectedFeeRefundProcessDAO, feeRefundHeaderDAO, feeRefundDetailDAO, feeRefundInstructionDAO,
					paymentHeaderService, manualAdviseService, feeTypeDAO, paymentHeaderDAO, feeRefundHeaderService);
			threadProcess.processesThread(threadId);
		}

		logger.debug(Literal.LEAVING);

	}

	private void updateThread() {
		long finsCount = projectedFeeRefundProcessDAO.getCountByProgress();

		if (finsCount == 0) {
			return;
		}

		int threadCount = SysParamUtil.getValueAsInt(ReceiptUploadConstants.MULTI_REC_THREAD_COUNT);
		long noOfRows = finsCount / threadCount;

		boolean recordsLessThanThread = false;

		if (finsCount < threadCount) {
			recordsLessThanThread = true;
			noOfRows = 1;
		}

		long temprows = noOfRows;
		for (int i = 1; i <= threadCount; i++) {

			if (i == threadCount) {
				projectedFeeRefundProcessDAO.updateThreadIDByRowNumber(0, i);
			} else {
				projectedFeeRefundProcessDAO.updateThreadIDByRowNumber(temprows, i);
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
