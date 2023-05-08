package com.pennanttech.pennapps.dms.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import com.pennant.backend.dao.collateral.CollateralSetupDAO;
import com.pennant.backend.dao.configuration.VASRecordingDAO;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.customermasters.CustomerDocumentDAO;
import com.pennant.backend.dao.documentdetails.DocumentDetailsDAO;
import com.pennant.backend.dao.documentdetails.DocumentManagerDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.documentdetails.DocumentManager;
import com.pennanttech.model.dms.DMSModule;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.dms.DMSProperties;
import com.pennanttech.pennapps.dms.DMSStorage;
import com.pennanttech.pennapps.dms.dao.DMSQueueDAO;
import com.pennanttech.pennapps.dms.filesystem.DocumentFileSystem;
import com.pennanttech.pennapps.dms.model.DMSQueue;
import com.pennanttech.pennapps.dms.service.DMSService;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.document.external.ExternalDocumentManager;

public class DMSServiceImpl implements DMSService {
	private static Logger logger = LogManager.getLogger(DMSServiceImpl.class);

	private DocumentManagerDAO documentManagerDAO;
	private FinanceMainDAO financeMainDAO;
	private CustomerDAO customerDAO;
	private CollateralSetupDAO collateralSetupDAO;
	private DMSQueueDAO dMSQueueDAO;
	private CustomerDocumentDAO customerDocumentDAO;
	private DocumentDetailsDAO documentDetailsDAO;
	private DocumentFileSystem documentFileSystem;
	private VASRecordingDAO vASRecordingDAO;
	private SimpleAsyncTaskExecutor taskExecutor;
	private ExternalDocumentManager externalDocumentManager;

	@Override
	public long save(DMSQueue dmsQueue) {
		logger.debug(Literal.ENTERING);

		DocumentManager documentManager = new DocumentManager();
		documentManager.setDocImage(dmsQueue.getDocImage());
		documentManager.setCustId(dmsQueue.getCustId());
		documentManager.setDocURI(StringUtils.trimToNull(dmsQueue.getDocUri()));
		boolean insert = false;

		if (dmsQueue.getDocManagerID() == 0 || dmsQueue.getDocManagerID() == Long.MIN_VALUE) {
			long docManagerId = documentManagerDAO.save(documentManager);
			dmsQueue.setDocManagerID(docManagerId);
			insert = true;
		} else if (dmsQueue.getDocManagerID() > 0 && dmsQueue.getDocUri() != null) {
			documentManagerDAO.update(dmsQueue.getDocManagerID(), dmsQueue.getCustId(), dmsQueue.getDocUri());
		}

		if (DMSStorage.FS == DMSStorage.getStorage(App.getProperty(DMSProperties.STORAGE))
				|| DMSStorage.EXTERNAL == DMSStorage.getStorage(App.getProperty(DMSProperties.STORAGE))) {
			if (insert && dmsQueue.getDocImage() != null) {
				dMSQueueDAO.log(dmsQueue);
			} else if (insert && dmsQueue.getDocUri() != null) {
				dMSQueueDAO.log(dmsQueue);
			} else if (dmsQueue.getDocManagerID() > 0) {
				dMSQueueDAO.update(dmsQueue);
			}
		}

		logger.debug(Literal.LEAVING);
		return dmsQueue.getDocManagerID();
	}

	@Override
	public DocumentManager getDocumentManager(long id) {
		logger.debug(Literal.ENTERING);
		DocumentManager dm = documentManagerDAO.getById(id);

		if (dm == null) {
			return null;
		}

		if (DMSStorage.FS == DMSStorage.getStorage(App.getProperty(DMSProperties.STORAGE))) {
			String docURI = StringUtils.trimToNull(dm.getDocURI());
			if (docURI != null) {
				dm.setDocImage(documentFileSystem.retrive(docURI));
			}
		}

		logger.debug(Literal.LEAVING);
		return dm;
	}

	@Override
	public byte[] getById(long id) {
		logger.debug(Literal.ENTERING);
		DocumentManager dm = documentManagerDAO.getById(id);

		if (dm == null) {
			return null;
		}

		if (DMSStorage.FS == DMSStorage.getStorage(App.getProperty(DMSProperties.STORAGE))) {
			String docURI = StringUtils.trimToNull(dm.getDocURI());
			if (docURI != null) {
				dm.setDocImage(documentFileSystem.retrive(docURI));
			}
		}

		logger.debug(Literal.LEAVING);
		return dm.getDocImage();
	}

	@Override
	public void processDocuments() {
		logger.debug(Literal.ENTERING);

		List<Long> queueList = new ArrayList<>();
		queueList = dMSQueueDAO.processDMSQueue();
		long[][] identifiers = null;

		taskExecutor = new SimpleAsyncTaskExecutor("Send-DMS");

		int threadCount = App.getIntegerProperty("dms.fs.thread.size");
		int batchSize = App.getIntegerProperty("dms.fs.batch.size");

		int threadNum = 0;
		int queuIDNum = 0;

		for (long id : queueList) {
			if (identifiers == null) {
				identifiers = new long[threadCount][batchSize];
			}

			identifiers[threadNum++][queuIDNum] = id;

			if (threadNum == threadCount) {
				threadNum = 0;
				queuIDNum++;
			}

			if (queuIDNum == batchSize) {
				break;
			}
		}

		DocumentStorageProcessThread.usedThreads.set(identifiers.length);
		for (int i = 0; i < identifiers.length; i++) {
			DocumentStorageProcessThread thread = new DocumentStorageProcessThread(identifiers[i]);

			thread.setCustomerDocumentDAO(customerDocumentDAO);
			thread.setdMSQueueDAO(dMSQueueDAO);
			thread.setDocumentDetailsDAO(documentDetailsDAO);
			thread.setDocumentFileSystem(documentFileSystem);
			thread.setDocumentManagerDAO(documentManagerDAO);

			taskExecutor.execute(thread);
		}

		identifiers = null;

		while (true) {
			if (DocumentStorageProcessThread.usedThreads.get() > 0) {
				logger.info("Waiting for designated threads' execution completion.");
				if (DocumentStorageProcessThread.usedThreads.get() > 0) {
					logger.info("Waiting for designated threads' execution completion.");
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						logger.error("Exception: {}", e.getMessage());
					}
				} else {
					break;
				}
			} else {
				break;
			}
		}

		logger.debug(Literal.LEAVING);
	}

	public void storeDocInFileSystem(DMSQueue dmsQueue) {
		logger.debug(Literal.ENTERING);

		Long custId = dmsQueue.getCustId();

		if (custId == null || custId <= 0) {
			setCustomerId(dmsQueue);
		}
		custId = dmsQueue.getCustId();

		if (custId == null || custId == 0) {
			return;
		}
		String docURI = StringUtils.trimToNull(documentFileSystem.store(dmsQueue));

		if (docURI == null) {
			dMSQueueDAO.updateDMSQueue(dmsQueue);
			return;
		}

		logger.debug("Updating docURI in Document Manger...");
		documentManagerDAO.update(dmsQueue.getDocManagerID(), custId, docURI);

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
		logger.debug(Literal.LEAVING);
	}

	private void setCustomerId(DMSQueue dmsQueue) {
		logger.debug(Literal.ENTERING);
		Long custId = null;
		DMSModule module = dmsQueue.getModule();
		DMSModule subModule = dmsQueue.getSubModule();
		String finRefernce = StringUtils.trimToNull(dmsQueue.getFinReference());
		String reference = StringUtils.trimToNull(dmsQueue.getReference());

		if (module == DMSModule.FINANCE) {
			if (subModule == DMSModule.COLLATERAL && reference != null) {
				custId = collateralSetupDAO.getCustomerIdByCollateral(reference);
			} else if (subModule == DMSModule.QUERY_MGMT && finRefernce != null) {
				custId = financeMainDAO.getCustomerIdByFin(finRefernce);
			} else if (subModule == DMSModule.VAS && reference != null) {
				custId = vASRecordingDAO.getCustomerId(reference);
			} else {
				custId = financeMainDAO.getCustomerIdByFin(finRefernce);
			}
		}
		dmsQueue.setCustId(custId);
		logger.debug(Literal.LEAVING);
	}

	@Override
	public DMSQueue getImageByUri(String docUri) {
		logger.debug(Literal.ENTERING);
		DMSQueue queue = new DMSQueue();
		if (DMSStorage.FS == DMSStorage.getStorage(App.getProperty(DMSProperties.STORAGE))) {
			String docURI = StringUtils.trimToNull(docUri);
			if (docURI != null) {
				queue = documentFileSystem.retriveDMS(docURI);
			}
		}

		logger.debug(Literal.LEAVING);
		return queue;
	}

	@Override
	public void updateDMSQueue(DMSQueue dmsQueue) {
		logger.debug(Literal.ENTERING);
		dMSQueueDAO.updateDMSQueue(dmsQueue);
		logger.debug(Literal.LEAVING);
	}

	@Override
	public Long getCustomerIdByFin(String finReference) {
		return financeMainDAO.getCustomerIdByFin(finReference);
	}

	@Override
	public Long getCustomerIdByCIF(String custCIF) {
		return customerDAO.getCustomerIdByCIF(custCIF);
	}

	@Override
	public DocumentDetails getExternalDocument(String custCIF, String docName, String docUri) {
		DocumentDetails detail = externalDocumentManager.getExternalDocument(docName, docUri, custCIF);
		if (detail == null) {
			detail = new DocumentDetails();
		}
		return detail;
	}

	@Override
	public Long getCustomerIdByCollateral(String collateralRef) {
		return collateralSetupDAO.getCustomerIdByCollateral(collateralRef);
	}

	@Override
	public DMSQueue getOfferIdByFin(DMSQueue dmsQueue) {
		return financeMainDAO.getOfferIdByFin(dmsQueue);
	}

	public void setDocumentManagerDAO(DocumentManagerDAO documentManagerDAO) {
		this.documentManagerDAO = documentManagerDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

	public void setCollateralSetupDAO(CollateralSetupDAO collateralSetupDAO) {
		this.collateralSetupDAO = collateralSetupDAO;
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

	public void setvASRecordingDAO(VASRecordingDAO vASRecordingDAO) {
		this.vASRecordingDAO = vASRecordingDAO;
	}

	public void setExternalDocumentManager(ExternalDocumentManager externalDocumentManager) {
		this.externalDocumentManager = externalDocumentManager;
	}
}
