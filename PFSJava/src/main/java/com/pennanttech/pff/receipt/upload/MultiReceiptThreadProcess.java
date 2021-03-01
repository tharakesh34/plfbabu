package com.pennanttech.pff.receipt.upload;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinReceiptData;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinReceiptQueueLog;
import com.pennant.backend.service.finance.ReceiptService;
import com.pennant.backend.util.ReceiptUploadConstants;
import com.pennant.eod.constants.EodConstants;

public class MultiReceiptThreadProcess {
	private static final Logger logger_ = LogManager.getLogger(MultiReceiptThreadProcess.class);
	private ReceiptService receiptService;

	public MultiReceiptThreadProcess() {
		super();
	}

	// Inner Class for Multi-Threading Process
	class MultiReceiptThread implements Runnable {
		private AuditHeader auditHeader;

		public MultiReceiptThread(AuditHeader auditHeader) {
			super();
			this.auditHeader = auditHeader;
		}

		@Override
		public void run() {
			logger_.debug("Runnable started ReceiptId:"
					+ ((FinReceiptData) auditHeader.getModelData()).getReceiptHeader().getReceiptID());
			logger_.debug("Runnable started ThreadId:" + Thread.currentThread().getId());
			logger_.debug("Run: " + Thread.currentThread().getName());

			try {
				receiptService.saveMultiReceipt(auditHeader);
			} catch (Exception e) {
				e.printStackTrace();
			}

			logger_.debug("Runnable ended Receiptd:"
					+ ((FinReceiptData) auditHeader.getModelData()).getReceiptHeader().getReceiptID());
			logger_.debug("Runnable ended ThreadId:" + Thread.currentThread().getId());
		}
	}

	public void processThread(Map<Long, FinReceiptHeader> finReceiptHeaderMap, List<AuditHeader> auditHeaderList,
			long batchId) {
		List<FinReceiptQueueLog> rejectRctQueueList = new ArrayList<>();
		List<Long> sucReceiptIdList = new ArrayList<>();
		List<Long> receiptKeyList = new ArrayList<>(finReceiptHeaderMap.keySet());
		int threadCount = SysParamUtil.getValueAsInt(ReceiptUploadConstants.MULTI_REC_THREAD_COUNT);
		int noOfThreads = receiptKeyList.size() / threadCount;

		if (receiptKeyList.size() < threadCount) {
			noOfThreads = 1;
		}

		noOfThreads = noOfThreads > 100 ? 100 : noOfThreads;

		try {
			ExecutorService executor = Executors.newFixedThreadPool(noOfThreads); // Creating Fixed size of thread pool for executing the task parallel
			for (AuditHeader audiHead : auditHeaderList) {
				executor.execute(new MultiReceiptThread(audiHead)); // submitting task to executor thread pool
				sucReceiptIdList.add(
						((FinReceiptData) audiHead.getAuditDetail().getModelData()).getReceiptHeader().getReceiptID()); // adding into success list
			}
			executor.shutdown();
		} catch (RejectedExecutionException e) {
			e.printStackTrace();
		}

		receiptKeyList.removeAll(sucReceiptIdList); // getting Rejected receipt from executor pool
		if (receiptKeyList != null && CollectionUtils.isNotEmpty(receiptKeyList)) {
			for (Long receiptId : receiptKeyList) {
				FinReceiptHeader header = finReceiptHeaderMap.get(receiptId);
				FinReceiptQueueLog receiptQueue = new FinReceiptQueueLog();
				receiptQueue.setUploadId(batchId);
				receiptQueue.setReceiptId(header.getReceiptID());
				receiptQueue.setErrorLog("RejectedExecutionException");
				receiptQueue.setProgress(EodConstants.PROGRESS_FAILED);
				receiptQueue.setThreadId(0);
				rejectRctQueueList.add(receiptQueue);
			}
		}
		if (rejectRctQueueList != null && CollectionUtils.isNotEmpty(rejectRctQueueList)) {
			receiptService.batchUpdateMultiReceiptLog(rejectRctQueueList); // Updating rejected batch status
		}
	}

	public ReceiptService getReceiptService() {
		return receiptService;
	}

	public void setReceiptService(ReceiptService receiptService) {
		this.receiptService = receiptService;
	}

}
