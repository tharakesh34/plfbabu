package com.pennanttech.pennapps.dms.service.impl;

import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.backend.dao.customermasters.CustomerDocumentDAO;
import com.pennant.backend.dao.documentdetails.DocumentDetailsDAO;
import com.pennant.backend.dao.documentdetails.DocumentManagerDAO;
import com.pennanttech.model.dms.DMSModule;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.dms.dao.DMSQueueDAO;
import com.pennanttech.pennapps.dms.filesystem.DocumentFileSystem;
import com.pennanttech.pennapps.dms.model.DMSQueue;
import com.pennanttech.pff.core.TableType;

public class DocumentStorageProcessThread implements Runnable {
	private static final Logger logger = LogManager.getLogger(DocumentStorageProcessThread.class);

	private DocumentManagerDAO documentManagerDAO;
	private DMSQueueDAO dMSQueueDAO;
	private CustomerDocumentDAO customerDocumentDAO;
	private DocumentDetailsDAO documentDetailsDAO;
	private DocumentFileSystem documentFileSystem;

	public static AtomicLong usedThreads = new AtomicLong(0L);

	long[] dmsQueueList = null;

	public DocumentStorageProcessThread(long[] dmsQueueList) {
		super();
		this.dmsQueueList = dmsQueueList;
	}

	@Override
	public void run() {

		for (long queueID : dmsQueueList) {
			if (queueID != 0) {
				try {
					store(queueID);
				} catch (Exception e) {
					logger.debug(Literal.EXCEPTION, e);
				}
			} else {
				break;
			}
		}

		usedThreads.decrementAndGet();
		logger.debug(Literal.LEAVING);

	}

	private void store(long queueID) {
		DMSQueue dmsQueue = dMSQueueDAO.getDMSQueue(queueID);
		String docURI = null;

		try {
			docURI = StringUtils.trimToNull(documentFileSystem.store(dmsQueue));
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			dmsQueue.setErrorCode("DMS-99");
			dmsQueue.setErrorDesc(e.getMessage());
		} finally {
			int incre = dmsQueue.getAttemptNum() + 1;
			dmsQueue.setAttemptNum(incre);
			if (dmsQueue.getAttemptNum() >= 5) {
				dmsQueue.setProcessingFlag(-1);
			} else {
				dmsQueue.setProcessingFlag(0);
			}
		}

		if (docURI == null) {
			dMSQueueDAO.updateDMSQueue(dmsQueue);
			return;
		}

		dmsQueue.setProcessingFlag(1);

		logger.debug("Updating docURI in Document Manger...");
		documentManagerDAO.update(dmsQueue.getDocManagerID(), queueID, docURI);

		if (DMSModule.CUSTOMER == dmsQueue.getModule()) {
			logger.debug("Updating docURI in Customer Documents");
			int count = customerDocumentDAO.updateDocURI(docURI, dmsQueue.getDocManagerID(), TableType.TEMP_TAB);

			if (count == 0) {
				customerDocumentDAO.updateDocURI(docURI, dmsQueue.getDocManagerID(), TableType.MAIN_TAB);
			}
		} else if (DMSModule.FINANCE == dmsQueue.getModule()) {
			logger.debug("Updating docURI in Document Details");
			int count = documentDetailsDAO.updateDocURI(docURI, dmsQueue.getDocManagerID(), TableType.TEMP_TAB);
			if (count == 0) {
				documentDetailsDAO.updateDocURI(docURI, dmsQueue.getDocManagerID(), TableType.MAIN_TAB);
			}
		}

		logger.debug("Updating DMS_QUEUE...");
		dMSQueueDAO.updateDMSQueue(dmsQueue);
		if (dmsQueue.getProcessingFlag() == 1) {
			dMSQueueDAO.insertDMSQueueLog(dmsQueue);
			dMSQueueDAO.delete(queueID);
		}

	}

	public void setDocumentManagerDAO(DocumentManagerDAO documentManagerDAO) {
		this.documentManagerDAO = documentManagerDAO;
	}

	public void setdMSQueueDAO(DMSQueueDAO dMSQueueDAO) {
		this.dMSQueueDAO = dMSQueueDAO;
	}

	public void setCustomerDocumentDAO(CustomerDocumentDAO customerDocumentDAO) {
		this.customerDocumentDAO = customerDocumentDAO;
	}

	public void setDocumentDetailsDAO(DocumentDetailsDAO documentDetailsDAO) {
		this.documentDetailsDAO = documentDetailsDAO;
	}

	public void setDocumentFileSystem(DocumentFileSystem documentFileSystem) {
		this.documentFileSystem = documentFileSystem;
	}

	public void setDmsQueueList(long[] dmsQueueList) {
		this.dmsQueueList = dmsQueueList;
	}

}
