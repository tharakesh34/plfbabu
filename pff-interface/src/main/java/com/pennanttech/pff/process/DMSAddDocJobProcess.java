package com.pennanttech.pff.process;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.backend.dao.dms.DMSIdentificationDAO;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.util.DmsDocumentConstants;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.external.DocumentManagementService;

public class DMSAddDocJobProcess {
	private static final Logger logger = LogManager.getLogger(DMSAddDocJobProcess.class);
	private int retryCount;
	private int threadCount;
	private int threadAwaitPeroid;

	public DMSAddDocJobProcess() {
		super();
	}

	private DMSIdentificationDAO identificationDAO;
	private DocumentManagementService documentManagementService;

	public void process() {
		logger.debug(Literal.ENTERING);
		List<DocumentDetails> dmsDocRefList = identificationDAO.retrieveDMSDocumentReference();
		ExecutorService executor = Executors.newFixedThreadPool(threadCount);
		if (CollectionUtils.isNotEmpty(dmsDocRefList)) {
			for (DocumentDetails dmsDocumentDetails : dmsDocRefList) {
				if (null != dmsDocumentDetails) {
					Runnable dmsRunnable = new DmsRunnable(dmsDocumentDetails);
					executor.execute(dmsRunnable);
				}
			}
		}
		executor.shutdown();
		try {
			executor.awaitTermination(threadAwaitPeroid, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			logger.debug(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
	}

	private class DmsRunnable implements Runnable {
		private DocumentDetails dmsDocumentDetails;

		public DmsRunnable(DocumentDetails dmsDocumentDetails) {
			this.dmsDocumentDetails = dmsDocumentDetails;
		}

		@Override
		public void run() {
			dmsDocumentDetails.setRetryCount(dmsDocumentDetails.getRetryCount() + 1);
			dmsDocumentDetails.setLastMntOn(new Timestamp(Calendar.getInstance().getTimeInMillis()));
			dmsDocumentDetails.setState("Initated");
			dmsDocumentDetails.setStatus(DmsDocumentConstants.DMS_DOCUMENT_STATUS_PROCESSING);

			boolean success = true;
			String errorMsg = null;
			String docUri = null;
			try {
				docUri = documentManagementService.insertExternalDocument(dmsDocumentDetails);
			} catch (Exception exception) {
				success = false;
				if (null != exception.getMessage()) {
					if (exception.getMessage().length() > 200) {
						errorMsg = exception.getMessage().substring(0, 200);
					} else {
						errorMsg = exception.getMessage();
					}
				}
			}

			if (success) {
				if (null != docUri) {
					//dmsDocumentDetails.setDocRefId(null);
					dmsDocumentDetails.setDocUri(docUri);
					dmsDocumentDetails.setDocImage(null);
					identificationDAO.processSuccessResponse(dmsDocumentDetails);
				}
			} else {
				dmsDocumentDetails.setState("Error");
				dmsDocumentDetails.setErrorDesc(errorMsg);
				identificationDAO.processFailure(dmsDocumentDetails, retryCount);
			}
		}
	}

	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}

	public void setThreadCount(int threadCount) {
		this.threadCount = threadCount;
	}

	public void setThreadAwaitPeroid(int threadAwaitPeroid) {
		this.threadAwaitPeroid = threadAwaitPeroid;
	}

	@Autowired(required = false)
	public void setIdentificationDAO(DMSIdentificationDAO identificationDAO) {
		this.identificationDAO = identificationDAO;
	}

	@Autowired(required = false)
	public void setDocumentManagementService(DocumentManagementService documentManagementService) {
		this.documentManagementService = documentManagementService;
	}
}
