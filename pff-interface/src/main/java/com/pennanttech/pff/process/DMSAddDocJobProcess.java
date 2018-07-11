package com.pennanttech.pff.process;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.backend.dao.dms.DMSIdentificationDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.util.DmsDocumentConstants;
import com.pennanttech.model.dms.DMSDocumentDetails;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.external.creditInformation.AbstractDMSIntegrationService;

public class DMSAddDocJobProcess {
	private static final Logger logger = Logger.getLogger(DMSAddDocJobProcess.class);
	private static final int retryCount = Integer.valueOf(App.getProperty("dms.document.retrycount"));
	private static final int dmsThreadCount = Integer.valueOf(App.getProperty("dms.thread.count"));
	private static final int dmsThreadAwaitPeroid = Integer.valueOf(App.getProperty("dms.thread.seconds.timeout"));
	@Autowired
	private DMSIdentificationDAO identificationDAO;
	@Autowired(required=false)
	private AbstractDMSIntegrationService abstractDMSIntegrationService;
	
	public void process() {
		logger.debug(Literal.ENTERING);
		List<DMSDocumentDetails> dmsDocRefList = identificationDAO.retrieveDMSDocumentReference();
		ExecutorService executor = Executors.newFixedThreadPool(dmsThreadCount);
		if (CollectionUtils.isNotEmpty(dmsDocRefList)) {
			for (DMSDocumentDetails dmsDocumentDetails : dmsDocRefList) {
				if (null != dmsDocumentDetails) {
					Runnable dmsRunnable = new DmsRunnable(identificationDAO, abstractDMSIntegrationService,
							dmsDocumentDetails);
					executor.execute(dmsRunnable);
				}
			}
		}
		executor.shutdown();
		try {
			executor.awaitTermination(dmsThreadAwaitPeroid, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			logger.debug(Literal.EXCEPTION, e);
		}
		
		logger.debug(Literal.LEAVING);
	}
	
	
	private class DmsRunnable implements Runnable {

		private DMSIdentificationDAO identificationDAO;
		private AbstractDMSIntegrationService abstractDMSIntegrationService;
		private DMSDocumentDetails dmsDocumentDetails;

		public DmsRunnable(DMSIdentificationDAO identificationDAO,
				AbstractDMSIntegrationService abstractDMSIntegrationService, DMSDocumentDetails dmsDocumentDetails) {
			this.identificationDAO = identificationDAO;
			this.abstractDMSIntegrationService = abstractDMSIntegrationService;
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
			AuditHeader auditHeader = new AuditHeader();
			auditHeader.setAuditDetail(new AuditDetail());
			auditHeader.getAuditDetail().setModelData(dmsDocumentDetails);
			AuditHeader responseAuditHeader = null;
			try {
				responseAuditHeader = abstractDMSIntegrationService.insertExternalDocument(auditHeader);
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
				if (null != responseAuditHeader && null != responseAuditHeader.getAuditDetail()
						&& null != responseAuditHeader.getAuditDetail().getModelData()) {
					Object object = responseAuditHeader.getAuditDetail().getModelData();
					DMSDocumentDetails responseDmsDocumentDetails = null;
					if (object instanceof DMSDocumentDetails) {
						responseDmsDocumentDetails = (DMSDocumentDetails) object;
					}
					if (null != responseDmsDocumentDetails) {
						identificationDAO.processSuccessResponse(dmsDocumentDetails, responseDmsDocumentDetails);
					}
				}
			} else {
				dmsDocumentDetails.setState("Error");
				dmsDocumentDetails.setErrorDesc(errorMsg);
				identificationDAO.processFailure(dmsDocumentDetails, retryCount);
			}
		}

	}
}
