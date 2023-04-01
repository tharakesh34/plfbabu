package com.pennanttech.pff.external.disbursement;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.FinCovenantTypeDAO;
import com.pennant.backend.dao.finance.covenant.CovenantsDAO;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinCovenantType;
import com.pennant.backend.model.finance.covenant.Covenant;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.dataengine.config.DataEngineConfig;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.dataengine.model.EventProperties;
import com.pennanttech.dataengine.util.DataEngineUtil;
import com.pennanttech.dataengine.util.EncryptionUtil;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.ftp.FtpClient;
import com.pennanttech.pennapps.core.ftp.SftpClient;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.disbursement.PaymentChannel;
import com.pennanttech.pff.core.disbursement.model.DisbursementRequest;
import com.pennanttech.pff.external.disbursement.dao.DisbursementDAO;
import com.pennanttech.service.AmazonS3Bucket;

public class DisbursementRequestServiceImpl implements DisbursementRequestService {
	private final Logger logger = LogManager.getLogger(DisbursementRequestServiceImpl.class);

	private DisbursementDAO disbursementRequestDAO;
	private FinCovenantTypeDAO finCovenantTypesDAO;
	private DisbursementService disbursementService;
	private IMPSDisbursement impsDisbursement;
	private DataEngineConfig dataEngineConfig;
	private PlatformTransactionManager transactionManager;
	protected CovenantsDAO covenantsDAO;
	private static Map<Long, Map<String, EventProperties>> eventProperties = new HashMap<>();
	private static String ONLINE = "ONLINE";
	private static final String ERROR_MESSAGE = "Unable to process the disbursement requests, please contact administrator.";

	protected Map<String, String> inserQueryMap = new HashMap<>();

	@Override
	public String prepareRequest(DisbursementRequest request) {
		logger.info(Literal.ENTERING);

		String status = "S";
		Long headerId = null;
		List<DataEngineStatus> list = null;

		try {
			logger.info("Locking selected payment Id's to avoid duplicate process");
			headerId = lockDisbursements(request);

			if (headerId == null) {
				throw new ConcurrencyException();
			}

			request.setHeaderId(headerId);

			logger.info("Saving the disbursement requests");
			saveDisbursementRequests(request);

			if (CollectionUtils.isEmpty(request.getDisbursementRequests())) {
				disbursementRequestDAO.deleteDisbursementBatch(headerId);

				logger.info("Disbursement Requests are empty");
				throw new ConcurrencyException();
			}

			logger.info("Processing the disbursement requests");
			list = disbursementService.sendReqest(request);

			updateBatchStatus(request, list);

		} catch (AppException e) {
			status = "F";
			logger.error(Literal.EXCEPTION, e);
			throw e;
		} finally {
			if ("F".equals(status) || (CollectionUtils.isEmpty(list))) {
				logger.error("disbursement download request processed failed.");
				request.setStatus("APPROVED");
				updateBatchFailureStatus(request);
			}

			if (headerId != null) {
				disbursementRequestDAO.clearBatch(headerId);
			}

		}

		logger.info(Literal.LEAVING);
		return status;
	}

	private void updateBatchStatus(DisbursementRequest disbursementRequest, List<DataEngineStatus> list) {
		logger.info("Updating batch status...");

		List<DisbursementRequest> disbursementRequests = disbursementRequest.getDisbursementRequests();

		for (DataEngineStatus dataEngineStatus : list) {
			if (!"S".equals(dataEngineStatus.getStatus())) {
				DisbursementRequest req = prepareReqStatus(disbursementRequest, dataEngineStatus, disbursementRequests);
				req.setStatus(PennantConstants.RCD_STATUS_APPROVED);

				updateStatus(req);
				throw new AppException(ERROR_MESSAGE);
			}
		}

		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
		TransactionStatus transactionStatus = transactionManager.getTransaction(def);

		if (PennantConstants.ONLINE.equals(disbursementRequest.getDownloadType())) {
			try {
				for (DataEngineStatus ds : list) {
					long btachId = ds.getId();
					DisbursementRequest req = prepareReqStatus(disbursementRequest, ds, disbursementRequests);

					req.setCreatedOn(DateUtil.getSysDate());
					req.setStatus(DisbursementConstants.STATUS_PAID);

					int count = disbursementRequestDAO.updateBatchStatus(req);

					logger.info("{} disbursements processed successfully  with {} batch Id", count, btachId);
				}

				transactionManager.commit(transactionStatus);
			} catch (Exception e) {
				transactionManager.rollback(transactionStatus);
				disbursementRequestDAO.deleteDisbursementBatch(disbursementRequest.getHeaderId());
				throw new AppException(ERROR_MESSAGE);
			}
		} else {
			long total = 0;

			if (!disbursementRequest.getRequestSource().equals(PennantConstants.FINSOURCE_ID_API)) {
				for (DataEngineStatus ds : list) {
					total = total + ds.getTotalRecords();
				}

				if (total != disbursementRequests.size()) {
					throw new AppException(ERROR_MESSAGE);
				}
			}

			try {
				for (DataEngineStatus ds : list) {
					Long btachId = ds.getId();
					DisbursementRequest req = prepareReqStatus(disbursementRequest, ds, disbursementRequests);

					if (ds.getFileName() != null) {
						req.setTargetType("FILE");
						req.setFileName(ds.getFileName());
						req.setFileLocation(ds.getFile().getParent());
						req.setDataEngineConfig(ds.getConfiguration().getId());
						req.setPostEvents(ds.getConfiguration().getPostEvent());
					} else if ("DISB_IMPS_EXPORT".equals(ds.getName())) {
						req.setTargetType("TABLE");
					}

					req.setCreatedOn(DateUtil.getSysDate());
					req.setStatus(DisbursementConstants.STATUS_AWAITCON);

					int count = disbursementRequestDAO.updateBatchStatus(req);

					if ("OFF_LINE".endsWith(disbursementRequest.getRequestSource())) {
						disbursementRequestDAO.logDisbursementMovement(req, false);
						logger.info("{} disbursements processed successfully  with {} batch Id", count, btachId);
					} else {
						logger.info("{} disbursements processed successfully", count);
					}
				}

				transactionManager.commit(transactionStatus);
			} catch (Exception e) {
				transactionManager.rollback(transactionStatus);
				disbursementRequestDAO.deleteDisbursementBatch(disbursementRequest.getHeaderId());
				throw new AppException(ERROR_MESSAGE);
			}

		}

	}

	private DisbursementRequest prepareReqStatus(DisbursementRequest disbursementRequest,
			DataEngineStatus dataEngineStatus, List<DisbursementRequest> disbursementRequests) {
		long btachId = dataEngineStatus.getId();
		boolean disbursements = false;
		boolean payments = false;
		String disbursementType = dataEngineStatus.getKeyAttributes().get("DISBURSEMENT_TYPE").toString();

		for (DisbursementRequest request : disbursementRequests) {
			if (PaymentChannel.Disbursement.getValue().equals(request.getChannel())) {
				disbursements = true;
			} else if (PaymentChannel.Payment.getValue().equals(request.getChannel())) {
				payments = true;
			}
		}

		DisbursementRequest req = new DisbursementRequest();
		req.setHeaderId(disbursementRequest.getHeaderId());
		req.setBatchId(btachId);
		req.setDisbursementType(disbursementType);
		req.setDisbursements(disbursements);
		req.setPayments(payments);
		req.setUserId(disbursementRequest.getUserId());

		return req;
	}

	private void saveDisbursementRequests(DisbursementRequest request) {
		logger.info("Logging disbursements...");

		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
		TransactionStatus transactionStatus = transactionManager.getTransaction(def);

		try {
			List<DisbursementRequest> list = disbursementRequestDAO.logDisbursementBatch(request);

			if (CollectionUtils.isEmpty(list)) {
				logger.info("ConcurrencyException, list is Empty.");
				throw new ConcurrencyException();
			}

			request.getDisbursementRequests().addAll(list);
			transactionManager.commit(transactionStatus);
			logger.info("{} disbursements logged with {} header Id", list.size(), request.getHeaderId());
		} catch (Exception e) {
			transactionManager.rollback(transactionStatus);

			logger.error("ConcurrencyException", e);
			throw new ConcurrencyException();
		}
	}

	private Set<Long> getPaymentIds(List<FinAdvancePayments> finAdvancePayments) {
		Set<Long> list = new HashSet<>();

		for (FinAdvancePayments finAdvancePayment : finAdvancePayments) {
			list.add(finAdvancePayment.getPaymentId());
		}

		return list;

	}

	private Long lockDisbursements(DisbursementRequest request) {
		logger.info(Literal.ENTERING);

		Set<Long> selectedPaymentIds = getPaymentIds(request.getFinAdvancePayments());
		logger.info("{} user {} Payment Id's selected", request.getUserId(), selectedPaymentIds.size());

		logger.info("Creating batch...");
		long headerId = disbursementRequestDAO.getNextBatchId();
		request.setHeaderId(headerId);
		logger.info("Batch Id {}", headerId);

		logger.info("locking Finadvance payments...");
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
		TransactionStatus status = transactionManager.getTransaction(def);

		int count = 0;
		try {
			count = lockFinAdvancePayments(headerId, request.getUserId(), selectedPaymentIds);

			if (count != selectedPaymentIds.size()) {
				logger.info("ConcurrencyException, selectedPaymentIds {}, count {} .", selectedPaymentIds, count);
				throw new ConcurrencyException();
			}

			transactionManager.commit(status);
		} catch (Exception e) {
			transactionManager.rollback(status);
			logger.info("ConcurrencyException {}", e.getMessage());
			throw new ConcurrencyException();
		}

		logger.info(Literal.LEAVING);
		return headerId;
	}

	private int lockFinAdvancePayments(long headerId, long userId, Set<Long> selectedPaymentIds) {
		logger.info(Literal.ENTERING);

		logger.info("Header Id {}, PaymentId's {}", headerId, selectedPaymentIds);

		int count = 0;
		List<Long> paymentIdList = new ArrayList<>();
		for (Long paymentId : selectedPaymentIds) {
			paymentIdList.add(paymentId);
			if (paymentIdList.size() > 499) {
				Long[] result = new Long[paymentIdList.size()];
				result = paymentIdList.toArray(result);
				count = count + disbursementRequestDAO.lockFinAdvancePayments(headerId, userId, result);
				paymentIdList.clear();
			}
		}

		if (!paymentIdList.isEmpty()) {
			Long[] result = new Long[paymentIdList.size()];
			result = paymentIdList.toArray(result);
			count = count + disbursementRequestDAO.lockFinAdvancePayments(headerId, userId, result);
			paymentIdList.clear();
		}

		logger.info(Literal.LEAVING);
		return count;
	}

	@Override
	public void processRequests() {

		List<Long> list = disbursementRequestDAO.getMovementList();

		if (list.isEmpty()) {
			return;
		}

		for (Long requestId : list) {
			processRequest(requestId);
		}

	}

	private void processRequest(long requestId) {
		logger.info("locking {} disbursement movement ...", requestId);
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
		TransactionStatus status = transactionManager.getTransaction(def);

		try {
			disbursementRequestDAO.lockMovement(requestId);
			transactionManager.commit(status);
		} catch (Exception e) {
			transactionManager.rollback(status);
		}

		DisbursementRequest request = disbursementRequestDAO.getMovementRequest(requestId);

		if ("FILE".equals(request.getTargetType())) {
			pushToInterce(requestId, request);
		} else if ("TABLE".equals(request.getTargetType())) {
			try {
				DataEngineStatus status1 = impsDisbursement.sendRequest(request);

				if (!"S".equals(status1.getStatus())) {
					disbursementRequestDAO.updateMovement(requestId, -1, status1.getRemarks());
				} else if ("S".equals(status1.getStatus())) {
					updateMovementStatus(request);
				}
			} catch (Exception e) {
				logger.debug(Literal.EXCEPTION, e);
				disbursementRequestDAO.updateMovement(requestId, -1, e.getMessage());
			}
		}
	}

	private File getFileFromStore(DisbursementRequest request, boolean job) {
		logger.info("Entering Store for File");
		File file = null;
		EventProperties property = null;
		String[] postEvents = StringUtils.trimToEmpty(request.getPostEvents()).split(",");

		if (postEvents == null || postEvents.length == 0) {
			return null;
		}

		long configId = request.getDataEngineConfig();

		Map<String, EventProperties> properties = eventProperties.computeIfAbsent(configId,
				abc -> dataEngineConfig.getEventPropertyMap(configId));

		for (String postEvent : postEvents) {
			postEvent = StringUtils.trimToEmpty(postEvent);
			property = properties.get(postEvent);

			if (property != null) {
				if (property.getStorageType().equals("S3")) {
					break;
				} else if (property.getStorageType().equals("SHARE_TO_FTP")) {
					break;
				} else if (property.getStorageType().equals("SHARE_TO_SFTP")) {
					break;
				} else if (property.getStorageType().equals("SHARED_NETWORK_FOLDER")) {
					break;
				}
			}
		}

		EventProperties s3Property = null;
		EventProperties sharedFTPProperty = null;
		EventProperties sharedSFTPProperty = null;
		EventProperties sharedNetworkFolderProperty = null;

		if (property != null) {
			if (property.getStorageType().equals("S3")) {
				s3Property = property;
			} else if (property.getStorageType().equals("SHARE_TO_FTP")) {
				sharedFTPProperty = property;
			} else if (property.getStorageType().equals("SHARE_TO_SFTP")) {
				sharedSFTPProperty = property;
			} else if (property.getStorageType().equals("SHARED_NETWORK_FOLDER")) {
				sharedNetworkFolderProperty = property;
			}
		}

		setLocalRepoLocation(request, job);

		if (s3Property != null) {
			file = getFileFromS3Bucket(s3Property, request);
		} else if (sharedSFTPProperty != null) {
			file = getFileFromFTP(sharedSFTPProperty, request, "SFTP");
		} else if (sharedFTPProperty != null) {
			file = getFileFromFTP(sharedFTPProperty, request, "FTP");
		} else if (sharedNetworkFolderProperty != null) {
			file = getFileFromSharedFolder(sharedNetworkFolderProperty, request);
		} else {
			file = getFileFromLocal(request);
		}

		return file;
	}

	private void pushToInterce(long requestId, DisbursementRequest request) {
		logger.info("Processing the {} file ", request.getFileName());
		long configId = request.getDataEngineConfig();

		String[] postEvents = StringUtils.trimToEmpty(request.getPostEvents()).split(",");

		if (postEvents == null || postEvents.length == 0) {
			updateMovementStatus(request);
			return;
		}

		Map<String, EventProperties> properties = eventProperties.computeIfAbsent(configId,
				abc -> dataEngineConfig.getEventPropertyMap(configId));

		if (properties == null || properties.isEmpty()) {
			updateMovementStatus(request);
			return;
		}

		try {

			File file = getFileFromStore(request, true);
			logger.info("File Collected From Store");

			EventProperties property = null;

			if (file != null) {
				String postEvent = "COPY_TO_FTP";
				property = getPostEvent(properties, postEvent);

				if (property == null) {
					postEvent = "COPY_TO_SFTP";
					property = getPostEvent(properties, postEvent);
				}

				if (property != null) {
					DataEngineUtil.postEvents(postEvent, property, file);
				}
			} else {
				String message = logger.getMessageFactory()
						.newMessage("Unable to find the file for the {} Movement", requestId).getFormattedMessage();
				throw new AppException(message);
			}

			updateMovementStatus(request);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			disbursementRequestDAO.updateMovement(requestId, -1, e.getMessage());
		}
	}

	private EventProperties getPostEvent(Map<String, EventProperties> properties, String postEvent) {
		return properties.get(postEvent);
	}

	private void updateMovementStatus(DisbursementRequest request) {
		request.setProcessFlag(1);
		request.setProcessedOn(DateUtil.getSysDate());
		disbursementRequestDAO.updateMovement(request, 1);
		disbursementRequestDAO.logDisbursementMovement(request, true);
		disbursementRequestDAO.deleteMovement(request.getId());
	}

	private void setLocalRepoLocation(DisbursementRequest request, boolean job) {
		String fileLocation = request.getFileLocation();
		fileLocation = fileLocation.concat(File.separator);

		if (job) {
			fileLocation = fileLocation.concat("Repository");
			fileLocation = fileLocation.concat(File.separator);
		}

		request.setLocalRepLocation(fileLocation);
	}

	private File getFile(DisbursementRequest request) {
		String fileLocation = request.getLocalRepLocation();
		fileLocation = fileLocation.concat(File.separator);
		fileLocation = fileLocation.concat(request.getFileName());
		return new File(fileLocation);
	}

	private AmazonS3Bucket getS3Bucket(EventProperties eventproperties) {
		AmazonS3Bucket bucket = new AmazonS3Bucket(eventproperties.getRegionName(), eventproperties.getBucketName(),
				EncryptionUtil.decrypt(eventproperties.getAccessKey()),
				EncryptionUtil.decrypt(eventproperties.getSecretKey()));

		return bucket;
	}

	/**
	 * This method will download the file from Amazon-s3 and store in specified local repository location
	 * 
	 * @param eventproperties The required parameters to call the Amazon-s3 client
	 * @param request         Required file details.
	 * @return The downloaded file which needs to be send to actual interface.
	 */
	private File getFileFromS3Bucket(EventProperties eventproperties, DisbursementRequest request) {
		File file = getFile(request);

		String key = eventproperties.getPrefix().concat("/").concat(file.getName());

		try {
			byte[] data = getS3Bucket(eventproperties).getObject(key);
			FileUtils.writeByteArrayToFile(file, data);
		} catch (Exception e) {
			String message = logger.getMessageFactory()
					.newMessage("Unable to download the {} file from {} location.", file.getName(), key)
					.getFormattedMessage();
			throw new AppException(message);
		}

		return file;
	}

	/**
	 * This method will download the file from configured FTP/SFTP and store in specified local repository location
	 * 
	 * @param eventproperties The required parameters to call the FTP/SFTP client
	 * @param request         Required file details
	 * 
	 * @param protoCol        FTP/SFTP Protocol
	 * @return The downloaded file which needs to be send to actual interface.
	 */
	private File getFileFromFTP(EventProperties eventproperties, DisbursementRequest request, String protoCol) {
		logger.info("Getting file from SFTP");

		File file = getFile(request);

		String hostName = eventproperties.getHostName();
		String port = eventproperties.getPort();
		String accessKey = eventproperties.getAccessKey();
		String secretKey = eventproperties.getSecretKey();
		String fileName = request.getFileName();
		String bucketName = eventproperties.getBucketName();

		try {

			FtpClient ftpClient = null;

			if ("FTP".equals(protoCol)) {
				ftpClient = new FtpClient(hostName, Integer.parseInt(port), accessKey, secretKey);
			} else if ("SFTP".equals(protoCol)) {
				logger.info("SFTP Connecting");
				ftpClient = new SftpClient(hostName, Integer.parseInt(port), accessKey, secretKey);
				logger.info("SFTP Connected");

			}

			if (ftpClient != null) {
				ftpClient.download(bucketName, file);
			}

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			String message = logger.getMessageFactory()
					.newMessage("Unable to download the {} file from {} location.", fileName, bucketName)
					.getFormattedMessage();
			throw new AppException(message);
		}

		return file;
	}

	/**
	 * This method will download the file from configured local folder and store in specified local repository location
	 * 
	 * @param request Required file details
	 * @return
	 */
	private File getFileFromLocal(DisbursementRequest request) {
		File file = getFile(request);

		DataEngineUtil.copyFile(request.getFileLocation(), file.getParent(), file.getName());

		return file;
	}

	/**
	 * 
	 * @param eventproperties The required parameters to get the file from remote folder
	 * @param request         Required file details
	 * @return
	 */
	private File getFileFromSharedFolder(EventProperties eventproperties, DisbursementRequest request) {
		File file = getFile(request);

		String remotePath = eventproperties.getBucketName();

		DataEngineUtil.copyFile(remotePath, file.getParent(), file.getName());

		return file;
	}

	@Override
	public List<FinAdvancePayments> filterDisbInstructions(List<FinAdvancePayments> disbInstructions) {
		List<FinAdvancePayments> disbursements = new ArrayList<>();

		if (CollectionUtils.isEmpty(disbInstructions)) {
			return disbursements;
		}

		String alwrepayMethods = (String) SysParamUtil.getValue("DISB_ALLOW_WITH_COVENANT_OTC_REPAY_METHOD");
		String[] repaymethod = alwrepayMethods.split(",");

		for (FinAdvancePayments finAdvancePayments : disbInstructions) {
			boolean isCovenantCheckNotReq = false;

			if (DisbursementConstants.CHANNEL_PAYMENT.equals(finAdvancePayments.getChannel())) {
				isCovenantCheckNotReq = true;
			} else {
				for (String rpymethod : repaymethod) {
					if (StringUtils.equals(finAdvancePayments.getPaymentType(), rpymethod)) {
						isCovenantCheckNotReq = true;
						break;
					}
				}
			}

			if (isCovenantCheckNotReq) {
				disbursements.add(finAdvancePayments);
			} else {
				String finReference = finAdvancePayments.getFinReference();
				List<FinCovenantType> covenants = new ArrayList<>();
				List<Covenant> covenantsList = new ArrayList<>();

				boolean isAddReq = false;
				if (ImplementationConstants.COVENANT_MODULE_NEW) {
					covenantsList = covenantsDAO.getCovenants(finReference, "Loan", TableType.AVIEW);
					for (Covenant covenant : covenantsList) {
						if (covenant.isOtc() && covenant.getDocumentReceivedDate() == null) {
							isAddReq = true;
							break;
						}
					}
				} else {
					covenants = finCovenantTypesDAO.getFinCovenantDocTypeByFinRef(finReference, "", false);
					for (FinCovenantType finCovenantType : covenants) {
						if (finCovenantType.isAlwOtc()) {
							isAddReq = true;
							break;
						}
					}
				}

				if (!isAddReq) {
					disbursements.add(finAdvancePayments);
				}
			}
		}
		return disbursements;
	}

	@Override
	public void processInstructions() {
		logger.info("Loading Disbursement/Payment/Insurance instructions..");

		Date appDate = SysParamUtil.getAppDate();
		Integer futureDays = Integer.valueOf((SysParamUtil.getValue("NO_FUTURE_DAYS_DISB_DOWNLOAD").toString()));
		Date llDate = DateUtil.addDays(appDate, futureDays - 1);

		List<FinAdvancePayments> disbInstructios = disbursementRequestDAO
				.getAutoDisbInstructions(JdbcUtil.getDate(llDate));

		logger.info("{} instructions available.", disbInstructios.size());

		logger.info("Excluding the instructions for which covenant documents are not yet received...");

		disbInstructios = filterDisbInstructions(disbInstructios);

		logger.info("{} instructions are filtered after excluding.", disbInstructios.size());

		Map<String, List<FinAdvancePayments>> dataMap = new HashMap<>();

		for (FinAdvancePayments advancePmt : disbInstructios) {
			if (PennantConstants.ONLINE.equals(advancePmt.getDownloadType())) {
				dataMap.computeIfAbsent(ONLINE, ft -> getAdvType());
				dataMap.get(ONLINE).add(advancePmt);
				continue;
			}

			long partnerBankID = advancePmt.getPartnerBankID();
			String entityCode = advancePmt.getEntityCode();
			String finType = advancePmt.getFinType();
			String channel = advancePmt.getChannel();

			String key = String.valueOf(partnerBankID).concat(entityCode).concat(finType).concat(channel);

			dataMap.computeIfAbsent(key, ft -> getAdvType());
			dataMap.get(key).add(advancePmt);
		}

		for (Entry<String, List<FinAdvancePayments>> finAdvancePayments : dataMap.entrySet()) {
			FinAdvancePayments finAdvancePayment = finAdvancePayments.getValue().get(0);

			DisbursementRequest request = new DisbursementRequest();
			request.setFinType(finAdvancePayment.getFinType());
			request.setPartnerBankCode(finAdvancePayment.getPartnerbankCode());
			request.setFinAdvancePayments(finAdvancePayments.getValue());
			request.setUserId(1000);
			request.setDataEngineConfigName(finAdvancePayment.getConfigName());
			request.setFileNamePrefix(finAdvancePayment.getFileName());
			request.setAutoDownload(true);
			request.setChannel(finAdvancePayment.getChannel());
			request.setAppValueDate(SysParamUtil.getAppValueDate());
			request.setDownloadType(finAdvancePayment.getDownloadType());

			try {
				prepareRequest(request);
			} catch (AppException e) {
				logger.error(Literal.EXCEPTION, e);
			}
		}

		logger.debug(Literal.LEAVING);
	}

	private List<FinAdvancePayments> getAdvType() {
		List<FinAdvancePayments> list = new ArrayList<>();
		return list;
	}

	private void updateBatchFailureStatus(DisbursementRequest req) {
		disbursementRequestDAO.updateBatchFailureStatus(req);
		disbursementRequestDAO.deleteDisbursementBatch(req.getHeaderId());
	}

	private void updateStatus(DisbursementRequest req) {
		disbursementRequestDAO.updateBatchStatus(req);
		disbursementRequestDAO.deleteDisbursementBatch(req.getHeaderId());
	}

	public void setDisbursementRequestDAO(DisbursementDAO disbursementRequestDAO) {
		this.disbursementRequestDAO = disbursementRequestDAO;
	}

	public void setDisbursementService(DisbursementService disbursementService) {
		this.disbursementService = disbursementService;
	}

	public void setDataEngineConfig(DataEngineConfig dataEngineConfig) {
		this.dataEngineConfig = dataEngineConfig;
	}

	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	@Autowired(required = false)
	public void setImpsDisbursement(IMPSDisbursement impsDisbursement) {
		this.impsDisbursement = impsDisbursement;
	}

	public FinCovenantTypeDAO getFinCovenantTypesDAO() {
		return finCovenantTypesDAO;
	}

	public void setFinCovenantTypesDAO(FinCovenantTypeDAO finCovenantTypesDAO) {
		this.finCovenantTypesDAO = finCovenantTypesDAO;
	}

	public void setCovenantsDAO(CovenantsDAO covenantsDAO) {
		this.covenantsDAO = covenantsDAO;
	}

}
