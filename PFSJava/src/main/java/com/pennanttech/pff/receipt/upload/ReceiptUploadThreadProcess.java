package com.pennanttech.pff.receipt.upload;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.pennant.backend.dao.finance.ReceiptUploadDetailDAO;
import com.pennant.backend.dao.finance.UploadAllocationDetailDAO;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.receiptupload.ReceiptUploadDetail;
import com.pennant.backend.model.receiptupload.ReceiptUploadLog;
import com.pennant.backend.model.receiptupload.UploadAlloctionDetail;
import com.pennant.backend.service.finance.ReceiptService;
import com.pennant.backend.util.ReceiptUploadConstants.ReceiptDetailStatus;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.RequestSource;

public class ReceiptUploadThreadProcess implements Runnable {
	private static Logger logger = LogManager.getLogger(ReceiptUploadThreadProcess.class);

	private DataSource dataSource;
	private ReceiptUploadDetailDAO receiptUploadDetailDAO;
	private UploadAllocationDetailDAO uploadAllocationDetailDAO;
	private ReceiptService receiptService;
	private LoggedInUser loggedInUser;
	private DataSourceTransactionManager transactionManager;
	private List<Long> headerIdList;
	private Integer threadId;
	private CountDownLatch latch;
	private Map<Long, ReceiptUploadLog> attemptMap;

	public ReceiptUploadThreadProcess() {
		super();
	}

	@Override
	public void run() {
		processesThread();
	}

	private void processesThread() {
		initilize();

		List<ReceiptUploadDetail> rudList = receiptUploadDetailDAO.getUploadReceiptDetailsByThreadId(headerIdList,
				threadId);

		int total = rudList.size();
		int success = 0;
		int failed = 0;
		logger.info("Processing ThreadId {} with batchSize{}", threadId, total);

		for (ReceiptUploadDetail rud : rudList) {
			rud.setLoggedInUser(loggedInUser);

			if (StringUtils.equals(rud.getAllocationType(), "M")) {
				List<UploadAlloctionDetail> listAllocationDetails = new ArrayList<>();
				listAllocationDetails = uploadAllocationDetailDAO.getUploadedAllocatations(rud.getUploadDetailId());
				rud.setListAllocationDetails(listAllocationDetails);
			}

			processReceipt(rud);

			if (ReceiptDetailStatus.FAILED.getValue() == rud.getProcessingStatus()) {
				failed++;
			} else {
				success++;
			}

		}
		latch.countDown();

		logger.info("Total Receipts >> {} Success >> {} Failures >> {} for ThreadId {}", total, success, failed,
				threadId);
	}

	public void updateAttempt(Long headerId, Consumer<ReceiptUploadLog> consumer) {
		consumer.accept(attemptMap.get(headerId));
	}

	private void processReceipt(ReceiptUploadDetail rud) {

		updateAttempt(rud.getUploadheaderId(), e -> e.incProcessedRecords());

		long headerId = rud.getUploadheaderId();
		long detailId = rud.getUploadDetailId();
		logger.info("Receipt creation started with HeaderId >> {} and DetailId >> {}", headerId, detailId);
		DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();

		txDef.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

		TransactionStatus transactionStatus = this.transactionManager.getTransaction(txDef);

		try {
			postExternalReceipt(rud);

			this.receiptUploadDetailDAO.updateStatus(rud);
			this.transactionManager.commit(transactionStatus);

			updateAttempt(rud.getUploadheaderId(), e -> e.incSuccessRecords());
			logger.info("Receipt created successfully.");
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			transactionManager.rollback(transactionStatus);

			String error = e.getMessage();

			if (e.getMessage() == null)
				error = "Unable to Process Request, Reason:" + e;

			if (error.length() > 1999) {
				error = error.substring(0, 1999);
			}
			updateAttempt(rud.getUploadheaderId(), u -> u.incFailRecords());

			rud.setProcessingStatus(ReceiptDetailStatus.FAILED.getValue());
			rud.setReceiptId(null);
			rud.setReason(error);
			this.receiptUploadDetailDAO.updateStatus(rud);
		} finally {
			if (transactionStatus != null) {
				transactionStatus.flush();
			}
		}
	}

	private void postExternalReceipt(ReceiptUploadDetail rud) {
		FinServiceInstruction fsi = receiptService.buildFinServiceInstruction(rud, "");
		fsi.setReqType("Post");
		fsi.setReceiptUpload(true);
		fsi.setLoggedInUser(rud.getLoggedInUser());
		fsi.setRequestSource(RequestSource.UPLOAD);
		FinanceDetail financeDetail = receiptService.receiptTransaction(fsi);

		WSReturnStatus returnStatus = financeDetail.getReturnStatus();
		if (returnStatus != null) {
			rud.setProcessingStatus(ReceiptDetailStatus.FAILED.getValue());

			String code = StringUtils.trimToEmpty(returnStatus.getReturnCode());
			String description = StringUtils.trimToEmpty(returnStatus.getReturnText());

			rud.setReason(String.format("%s %s %s", code, "-", description));
			throw new AppException("Unable to create receipt for the FinReference " + rud.getReference() + ", Reason "
					+ rud.getReason());
		} else {
			rud.setProcessingStatus(ReceiptDetailStatus.SUCCESS.getValue());
			rud.setReason("");
		}
	}

	private void initilize() {
		this.transactionManager = new DataSourceTransactionManager(dataSource);
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setReceiptUploadDetailDAO(ReceiptUploadDetailDAO receiptUploadDetailDAO) {
		this.receiptUploadDetailDAO = receiptUploadDetailDAO;
	}

	public void setUploadAllocationDetailDAO(UploadAllocationDetailDAO uploadAllocationDetailDAO) {
		this.uploadAllocationDetailDAO = uploadAllocationDetailDAO;
	}

	public void setReceiptService(ReceiptService receiptService) {
		this.receiptService = receiptService;
	}

	public void setLoggedInUser(LoggedInUser loggedInUser) {
		this.loggedInUser = loggedInUser;
	}

	public void setHeaderIdList(List<Long> headerIdList) {
		this.headerIdList = headerIdList;
	}

	public void setThreadId(Integer threadId) {
		this.threadId = threadId;
	}

	public void setLatch(CountDownLatch latch) {
		this.latch = latch;
	}

	public void setAttemptMap(Map<Long, ReceiptUploadLog> attemptMap) {
		this.attemptMap = attemptMap;
	}

}
