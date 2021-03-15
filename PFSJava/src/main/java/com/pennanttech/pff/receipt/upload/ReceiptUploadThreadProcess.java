package com.pennanttech.pff.receipt.upload;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.pennant.app.receiptuploadqueue.ReceiptUploadQueuing;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.dao.finance.ReceiptUploadDetailDAO;
import com.pennant.backend.dao.finance.UploadAllocationDetailDAO;
import com.pennant.backend.dao.receiptUpload.ProjectedRUDAO;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.receiptupload.ReceiptUploadDetail;
import com.pennant.backend.model.receiptupload.UploadAlloctionDetail;
import com.pennant.backend.service.finance.ReceiptService;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;

public class ReceiptUploadThreadProcess {
	private static Logger logger = LogManager.getLogger(ReceiptUploadThreadProcess.class);

	private DataSource dataSource;
	private ProjectedRUDAO projectedRUDAO;
	private ReceiptUploadDetailDAO receiptUploadDetailDAO;
	private UploadAllocationDetailDAO uploadAllocationDetailDAO;
	private ReceiptService receiptService;
	private LoggedInUser loggedInUser;
	private DataSourceTransactionManager transactionManager;

	public ReceiptUploadThreadProcess(DataSource dataSource, ProjectedRUDAO projectedRUDAO,
			ReceiptUploadDetailDAO receiptUploadDetailDAO, ReceiptService receiptService,
			UploadAllocationDetailDAO uploadAllocationDetailDAO, LoggedInUser loggedInUser) {
		super();

		this.dataSource = dataSource;
		this.projectedRUDAO = projectedRUDAO;
		this.receiptUploadDetailDAO = receiptUploadDetailDAO;
		this.receiptService = receiptService;
		this.loggedInUser = loggedInUser;
		this.uploadAllocationDetailDAO = uploadAllocationDetailDAO;

		initilize();
	}

	public void processesThread(List<Long> headerIdList) {
		logger.info("Selected receipt upload batch count >> {}", headerIdList.size());
		List<Long> details = receiptUploadDetailDAO.getReceiptDetails(headerIdList);

		int total = details.size();
		int success = 0;
		int failed = 0;

		logger.info("{} receipts needs to be created for the selected upload headers {}", total,
				headerIdList.toString());

		for (Long detailId : details) {
			ReceiptUploadDetail rud = receiptUploadDetailDAO.getUploadReceiptDetail(detailId);
			rud.setLoggedInUser(loggedInUser);

			if (StringUtils.equals(rud.getAllocationType(), "M")) {
				List<UploadAlloctionDetail> listAllocationDetails = new ArrayList<>();
				listAllocationDetails = uploadAllocationDetailDAO.getUploadedAllocatations(detailId);
				rud.setListAllocationDetails(listAllocationDetails);
			}

			processReceipt(rud);

			if (PennantConstants.UPLOAD_STATUS_FAIL.equals(rud.getUploadStatus())) {
				failed++;
			} else {
				success++;
			}

		}

		logger.info("Total Receipts >> {} Success >> {} Failures >> {}", total, success, failed);
	}

	private void processReceipt(ReceiptUploadDetail rud) {
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
			logger.info("Receipt created successfully.");
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			transactionManager.rollback(transactionStatus);

			String error = StringUtils.trimToEmpty(e.getMessage());

			if (error.length() > 1999) {
				error = error.substring(0, 1999);
			}
			updateFailed(headerId, detailId, error);

			rud.setUploadStatus(PennantConstants.UPLOAD_STATUS_FAIL);
			rud.setReceiptId(null);
			rud.setReason(error);
			this.receiptUploadDetailDAO.updateStatus(rud);
		}
	}

	private void postExternalReceipt(ReceiptUploadDetail rud) {
		FinServiceInstruction fsi = receiptService.buildFinServiceInstruction(rud, "");
		fsi.setReqType("Post");
		fsi.setReceiptUpload(true);
		fsi.setLoggedInUser(rud.getLoggedInUser());
		FinanceDetail financeDetail = receiptService.receiptTransaction(fsi, fsi.getReceiptPurpose());

		WSReturnStatus returnStatus = financeDetail.getReturnStatus();
		if (returnStatus != null) {
			rud.setUploadStatus(PennantConstants.UPLOAD_STATUS_FAIL);

			String code = StringUtils.trimToEmpty(returnStatus.getReturnCode());
			String description = StringUtils.trimToEmpty(returnStatus.getReturnText());

			rud.setReason(String.format("%s %s %s", code, "-", description));
			throw new AppException("Unable to create receipt for the FinReference " + rud.getReference() + ", Reason "
					+ rud.getReason());
		} else {
			rud.setUploadStatus(PennantConstants.UPLOAD_STATUS_SUCCESS);
			rud.setReason("");
		}
	}

	private void updateFailed(long uploadHeaderId, long uploadDetailId, String errorLog) {
		ReceiptUploadQueuing ruQueuing = new ReceiptUploadQueuing();

		ruQueuing.setUploadHeaderId(uploadHeaderId);
		ruQueuing.setUploadDetailId(uploadDetailId);
		ruQueuing.setEndTime(DateUtility.getSysDate());
		ruQueuing.setErrorLog(errorLog);
		projectedRUDAO.updateFailedQueue(ruQueuing);
	}

	private void initilize() {
		this.transactionManager = new DataSourceTransactionManager(dataSource);
	}

}
