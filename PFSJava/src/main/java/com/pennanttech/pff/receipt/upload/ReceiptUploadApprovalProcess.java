package com.pennanttech.pff.receipt.upload;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.receiptupload.ReceiptUploadLog;
import com.pennant.backend.service.finance.ReceiptUploadHeaderService;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class ReceiptUploadApprovalProcess {
	private static final Logger logger = LogManager.getLogger(ReceiptUploadApprovalProcess.class);

	private ReceiptUploadHeaderService receiptUploadHeaderService;

	public void approveReceipts(List<Long> headerIdList, LoggedInUser loggedInUser,
			Map<Long, ReceiptUploadLog> attemptMap) {
		int maxThreadCount = SysParamUtil.getValueAsInt(SMTParameterConstants.RECEIPT_UPLOAD_THREAD_SIZE);

		logger.info("Started Thread allocation process for the HeaderId's{}..", headerIdList);
		int threadCount = receiptUploadHeaderService.updateThread(headerIdList);
		logger.info("Thread allocation process for the HeaderId's{} completed..", headerIdList);

		if (maxThreadCount >= threadCount) {
			receiptUploadHeaderService.executeThreads(headerIdList, loggedInUser, 0, threadCount, attemptMap);
			return;
		}

		for (int i = 0; i < threadCount; i = i + maxThreadCount) {
			int endThread = i + maxThreadCount;

			if (threadCount < endThread) {
				endThread = threadCount;
			}

			receiptUploadHeaderService.executeThreads(headerIdList, loggedInUser, i, endThread, attemptMap);
		}

	}

	public void setReceiptUploadHeaderService(ReceiptUploadHeaderService receiptUploadHeaderService) {
		this.receiptUploadHeaderService = receiptUploadHeaderService;
	}

}
