package com.pennanttech.pff.receipt.upload;

import java.util.List;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.backend.dao.finance.ReceiptUploadDetailDAO;
import com.pennant.backend.dao.finance.UploadAllocationDetailDAO;
import com.pennant.backend.dao.receiptUpload.ProjectedRUDAO;
import com.pennant.backend.service.finance.ReceiptService;
import com.pennant.backend.service.finance.ReceiptUploadHeaderService;
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

		ReceiptUploadThreadProcess threadProcess = new ReceiptUploadThreadProcess(dataSource, projectedRUDAO,
				receiptUploadDetailDAO, receiptService, uploadAllocationDetailDAO, loggedInUser);
		threadProcess.processesThread(receiptIds);

		logger.debug(Literal.LEAVING);

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
