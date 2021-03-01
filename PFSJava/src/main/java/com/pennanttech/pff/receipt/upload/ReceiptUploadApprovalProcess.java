package com.pennanttech.pff.receipt.upload;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.receiptuploadqueue.ReceiptUploadQueuing;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.ReceiptUploadDetailDAO;
import com.pennant.backend.dao.finance.UploadAllocationDetailDAO;
import com.pennant.backend.dao.receiptUpload.ProjectedRUDAO;
import com.pennant.backend.service.finance.ReceiptService;
import com.pennant.backend.service.finance.ReceiptUploadHeaderService;
import com.pennant.backend.util.ReceiptUploadConstants;
import com.pennant.eod.constants.EodConstants;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;

public class ReceiptUploadApprovalProcess {
	private static final Logger logger = LogManager.getLogger(ReceiptUploadApprovalProcess.class);

	private DataSource dataSource;
	private ProjectedRUDAO projectedRUDAO;
	private ReceiptUploadDetailDAO receiptUploadDetailDAO;
	private ReceiptService receiptService;
	private UploadAllocationDetailDAO uploadAllocationDetailDAO;
	private transient ReceiptUploadHeaderService receiptUploadHeaderService;

	public void approveReceipts(List<Long> receiptIds, LoggedInUser loggedInUser) {
		logger.debug(Literal.ENTERING);

		// FIXME Delete me based on selected batch.
		projectedRUDAO.insertLogTableAndTruncate();

		List<ReceiptUploadQueuing> uploadQueuings = new ArrayList<>();
		for (Long headerId : receiptIds) {
			ReceiptUploadQueuing ruQueuing = new ReceiptUploadQueuing();
			ruQueuing.setThreadId(0);
			ruQueuing.setEodDate(DateUtility.getAppDate());
			ruQueuing.setEodProcess(false);
			ruQueuing.setProgress(EodConstants.PROGRESS_WAIT);
			ruQueuing.setUploadHeaderId(headerId);
			ruQueuing.setStartTime(DateUtility.getSysDate());

			uploadQueuings.add(ruQueuing);
		}

		// FIXME when i am increase more than 1000
		projectedRUDAO.prepareReceiptUploadQueue(uploadQueuings);

		updateThread();

		List<Long> threads = projectedRUDAO.getThreads();

		for (Long threadId : threads) {
			ReceiptUploadThreadProcess threadProcess = new ReceiptUploadThreadProcess(dataSource, projectedRUDAO,
					receiptUploadDetailDAO, receiptService, uploadAllocationDetailDAO, loggedInUser,
					receiptUploadHeaderService);
			threadProcess.processesThread(threadId);
		}

		logger.debug(Literal.LEAVING);

	}

	private void updateThread() {
		long finsCount = projectedRUDAO.getCountByProgress();

		if (finsCount == 0) {
			return;
		}

		int threadCount = SysParamUtil.getValueAsInt(ReceiptUploadConstants.RU_THREAD_COUNT);
		long noOfRows = finsCount / threadCount;

		boolean recordsLessThanThread = false;

		if (finsCount < threadCount) {
			recordsLessThanThread = true;
			noOfRows = 1;
		}

		long temprows = noOfRows;
		for (int i = 1; i <= threadCount; i++) {

			if (i == threadCount) {
				projectedRUDAO.updateThreadIDByRowNumber(0, i);
			} else {
				projectedRUDAO.updateThreadIDByRowNumber(temprows, i);
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

	public void setProjectedRUDAO(ProjectedRUDAO projectedRUDAO) {
		this.projectedRUDAO = projectedRUDAO;
	}

	public void setReceiptUploadDetailDAO(ReceiptUploadDetailDAO receiptUploadDetailDAO) {
		this.receiptUploadDetailDAO = receiptUploadDetailDAO;
	}

	public void setReceiptService(ReceiptService receiptService) {
		this.receiptService = receiptService;
	}

	public UploadAllocationDetailDAO getUploadAllocationDetailDAO() {
		return uploadAllocationDetailDAO;
	}

	public void setUploadAllocationDetailDAO(UploadAllocationDetailDAO uploadAllocationDetailDAO) {
		this.uploadAllocationDetailDAO = uploadAllocationDetailDAO;
	}

	public ReceiptUploadHeaderService getReceiptUploadHeaderService() {
		return receiptUploadHeaderService;
	}

	public void setReceiptUploadHeaderService(ReceiptUploadHeaderService receiptUploadHeaderService) {
		this.receiptUploadHeaderService = receiptUploadHeaderService;
	}

}
