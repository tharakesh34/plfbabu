package com.pennanttech.pff.external.disbursment;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.media.AMedia;
import org.zkoss.util.media.Media;
import org.zkoss.util.resource.Labels;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.PaymentInstruction;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennanttech.dataengine.DataEngineImport;
import com.pennanttech.dataengine.config.DataEngineConfig;
import com.pennanttech.dataengine.constants.ExecutionStatus;
import com.pennanttech.dataengine.model.Configuration;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.dataengine.model.EventProperties;
import com.pennanttech.dataengine.util.DataEngineUtil;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.ftp.FtpClient;
import com.pennanttech.pennapps.core.ftp.SftpClient;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.process.DisbursementProcess;
import com.pennanttech.pff.core.process.PaymentProcess;
import com.pennanttech.pff.external.AbstractInterface;
import com.pennanttech.pff.external.DisbursementResponse;

public class DefaultDisbursementResponse extends AbstractInterface implements DisbursementResponse {
	protected final Logger logger = LogManager.getLogger(getClass());

	private DisbursementProcess disbursementProcess;
	private PaymentProcess paymentProcess;
	private DataEngineConfig dataEngineConfig;

	private static List<Configuration> disbRespConfig = new ArrayList<>();
	private static Map<Long, Map<String, EventProperties>> eventProperties = new HashMap<>();

	private static String customPaidStatus;
	static {
		String disbStatus = SysParamUtil.getValueAsString(SMTParameterConstants.DISB_PAID_STATUS);
		if (StringUtils.isNotBlank(disbStatus)) {
			customPaidStatus = disbStatus;
		}
	}

	public DefaultDisbursementResponse() {
		super();
	}

	@Override
	public void receiveResponse(DataEngineStatus dataEngineStatus) {
		logger.info(Literal.ENTERING);
		long respBatchID = dataEngineStatus.getId();
		long userId = dataEngineStatus.getUserId();

		dataEngineStatus.setRemarks("Start updating the dibursement/payment instrunctions status..");
		logger.info(dataEngineStatus.getRemarks());

		logger.info("Response-Batch-ID: {}", respBatchID);
		logger.info("User-ID: {}", userId);

		List<FinAdvancePayments> finAdvPayments = disbursementProcess.getDisbRequestsByRespBatchId(respBatchID);
		List<PaymentInstruction> instructions = disbursementProcess.getPaymentInstructionsByRespBatchId(respBatchID);

		long totalRecords = finAdvPayments.size() + instructions.size();
		long processedRecords = 0;
		long failureRecords = 0;
		long successRecords = 0;

		dataEngineStatus.setTotalRecords(totalRecords);
		dataEngineStatus.setProcessedRecords(processedRecords);
		dataEngineStatus.setSuccessRecords(successRecords);
		dataEngineStatus.setFailedRecords(failureRecords);
		dataEngineStatus.setStatus(ExecutionStatus.E.name());

		logger.info("Total dibursemnt instructions: {}", totalRecords);

		for (FinAdvancePayments fap : finAdvPayments) {
			String finReference = fap.getFinReference();
			long paymentId = fap.getPaymentId();
			String status = fap.getStatus();
			String clearingStatus = fap.getClearingStatus();

			logger.info("FinReference: {}", finReference);
			logger.info("PaymentId: {}", paymentId);
			logger.info("Disbursement instruction status: {}", status);
			logger.info("Disbursement instruction clearing status: {}", clearingStatus);

			FinanceMain fm = disbursementProcess.getDisbursmentFinMainById(finReference, TableType.MAIN_TAB);
			if (fm == null) {
				fm = disbursementProcess.getDisbursmentFinMainById(finReference, TableType.TEMP_TAB);
			}

			fap.setDisbResponseBatchId(respBatchID);
			fap.setUserId(userId);

			try {
				dataEngineStatus.setProcessedRecords(processedRecords++);
				validate(status, clearingStatus);

				disbursementProcess.process(fm, fap);
				
				dataEngineStatus.setSuccessRecords(successRecords++);
				
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
				dataEngineStatus.setFailedRecords(failureRecords++);
				dataEngineStatus.setProcessedRecords(processedRecords++);
				logError(respBatchID, String.valueOf(paymentId), "F", e.getMessage());
			}
		}

		// Payments..
		for (PaymentInstruction pi : instructions) {
			String finReference = pi.getFinReference();
			long paymentId = pi.getPaymentId();
			String status = pi.getStatus();
			String clearingStatus = pi.getClearingStatus();

			logger.info("FinReference: {}", finReference);
			logger.info("PaymentId: {}", paymentId);
			logger.info("Payment instruction status: {}", status);
			logger.info("Payment instruction clearing status: {}", clearingStatus);

			try {
				validate(status, clearingStatus);

				paymentProcess.process(pi);
				
				dataEngineStatus.setSuccessRecords(successRecords++);
				dataEngineStatus.setProcessedRecords(processedRecords++);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
				dataEngineStatus.setFailedRecords(failureRecords++);
				dataEngineStatus.setProcessedRecords(processedRecords++);
				logError(respBatchID, String.valueOf(paymentId), "F", e.getMessage());
			}
		}

		StringBuilder remarks = new StringBuilder();
		dataEngineStatus.setStatus(ExecutionStatus.S.name());

		if (failureRecords > 0) {
			remarks.append("Completed with exceptions, total Records: ");
			remarks.append(totalRecords);
			remarks.append(", Success: ");
			remarks.append(successRecords);
			remarks.append(", Failure: ");
			remarks.append(failureRecords);
			dataEngineStatus.setStatus(ExecutionStatus.F.name());

		} else {
			remarks.append("Completed successfully, total Records: ");
			remarks.append(totalRecords);
			remarks.append(", Success: ");
			remarks.append(successRecords);
		}
		
		dataEngineStatus.setRemarks(remarks.toString());
		updateDEStatus(dataEngineStatus);

		dataEngineStatus.setDataEngineLogList(dataEngineConfig.getExceptions(respBatchID));
		
		logger.debug(Literal.LEAVING);
	}

	public void validate(String status, String clearingStatus) {
		String paidStatus = "E";
		if (StringUtils.isNotBlank(customPaidStatus)) {
			paidStatus = customPaidStatus;
		}

		status = status.toUpperCase();
		clearingStatus = clearingStatus.toUpperCase();

		String statusDes = getStatus(status);
		String message = "Payment is in " + statusDes + " status";

		if (paidStatus.equals(clearingStatus)) {
			if (!DisbursementConstants.STATUS_AWAITCON.equals(status)) {// for
				throw new AppException(message);
			}
		} else if ("REJECTED".equals(clearingStatus) || "REJECT".equals(clearingStatus) || "R".equals(clearingStatus)) {
			if (!DisbursementConstants.STATUS_AWAITCON.equals(status)) {
				throw new AppException("Payment is in " + statusDes + " status cannot reject");
			}
		} else if ("P".equals(clearingStatus)) {
			if (!DisbursementConstants.STATUS_AWAITCON.equals(status)
					&& !DisbursementConstants.STATUS_PAID.equals(status)) {
				throw new AppException(message);
			}
		} else {
			throw new AppException(
					clearingStatus + " is not valid. Valid status codes are " + paidStatus + "/P/REJECTED/REJECT.");
		}
	}

	public String getStatus(String type) {
		String status = "";
		switch (type) {
		case "AC":
			status = "Awaiting Confirmation";
			break;
		case "E":
			status = "Executed";
			break;
		case "P":
			status = "Realized";
			break;
		case "R":
			status = "Rejected";
			break;
		// PSD Ticket: 139134
		case "PAID":
			status = "Paid";
			break;
		case "REALIZED":
			status = "Realized";
			break;
		case "REJECTED":
			status = "Rejected";
			break;
		default:
			status = "Invalid";
			break;
		}
		return status;
	}

	@Override
	public void processAutoResponseFiles() {
		logger.info("Processing Disbursement Respone files..");
		DataEngineStatus des = loadConfig();

		for (Configuration configuration : disbRespConfig) {
			executeDataEngine(des, configuration);
		}

	}

	private void executeDataEngine(DataEngineStatus des, Configuration configuration) {
		String localLocation = setLocalRepoLocation(configuration.getUploadPath());

		Map<String, EventProperties> properties = eventProperties.computeIfAbsent(configuration.getId(),
				abc -> dataEngineConfig.getEventPropertyMap(configuration.getId()));

		String[] postEvents = StringUtils.trimToEmpty(configuration.getPostEvent()).split(",");
		EventProperties property = null;

		EventProperties s3Property = null;
		EventProperties sharedFTPProperty = null;
		EventProperties sharedSFTPProperty = null;
		EventProperties sharedNetworkFolderProperty = null;

		for (String postEvent : postEvents) {
			postEvent = StringUtils.trimToEmpty(postEvent);
			property = properties.get(postEvent);

			if (property == null) {
				continue;
			}

			String storageType = StringUtils.trimToEmpty(property.getStorageType());
			if (storageType.equals("S3")) {
				s3Property = property;
			} else if (storageType.equals("SHARE_TO_FTP")) {
				sharedFTPProperty = property;
			} else if (storageType.equals("SHARE_TO_SFTP")) {
				sharedSFTPProperty = property;
			} else if (storageType.equals("SHARED_NETWORK_FOLDER")) {
				sharedNetworkFolderProperty = property;
			}
		}

		if (s3Property != null) {
			//
		} else if (sharedSFTPProperty != null) {
			processDisbRespFilesFromFTP(sharedSFTPProperty, "SFTP", configuration, des, localLocation);
		} else if (sharedFTPProperty != null) {
			processDisbRespFilesFromFTP(sharedFTPProperty, "FTP", configuration, des, localLocation);
		} else if (sharedNetworkFolderProperty != null) {
			//
		}
	}

	private void processDisbRespFilesFromFTP(EventProperties eventProperty, String protocol, Configuration config,
			DataEngineStatus des, String localLocation) {

		List<String> fileNames = new ArrayList<>();

		String hostName = eventProperty.getHostName();
		String port = eventProperty.getPort();
		String accessKey = eventProperty.getAccessKey();
		String secretKey = eventProperty.getSecretKey();
		String bucketName = eventProperty.getBucketName();

		logger.info("Connecting into {} shared location to Retreive Files..", protocol);

		try {
			FtpClient ftpClient = null;
			if ("FTP".equals(protocol)) {
				ftpClient = new FtpClient(hostName, Integer.parseInt(port), accessKey, secretKey);
				logger.info("Connected to FTP Location");
				fileNames.addAll(ftpClient.getFileNameList(bucketName));
			} else if ("SFTP".equals(protocol)) {
				ftpClient = new SftpClient(hostName, Integer.parseInt(port), accessKey, secretKey);
				logger.info("Connected to SFTP Location");
				fileNames.addAll(getFileNameList(bucketName, hostName, Integer.parseInt(port), accessKey, secretKey));
			}

			logger.info("Total {} Files are available to Upload in Shared Location.", fileNames.size());

			for (String fileName : fileNames) {
				logger.info("Processing {} response file", fileName);

				validateFileProperties(config, fileName);

				ftpClient.download(bucketName, localLocation, fileName);

				File file = new File(localLocation.concat(File.separator).concat(fileName));

				if (!file.exists()) {
					logger.info("{} does not exists", fileName);
					continue;
				}

				byte[] data = FileUtils.readFileToByteArray(file);
				Media aMedia = new AMedia(file.getName(), null, "application/octet-stream", data);

				processResponseFile(1000L, des, file, aMedia);

				Map<String, EventProperties> properties = eventProperties.computeIfAbsent(config.getId(),
						abc -> dataEngineConfig.getEventPropertyMap(config.getId()));

				if (file != null) {
					String postEvent = "COPY_TO_FTP";
					eventProperty = getPostEvent(properties, postEvent);

					if (eventProperty == null) {
						postEvent = "COPY_TO_SFTP";
						eventProperty = getPostEvent(properties, postEvent);
					}

					if (eventProperty != null) {
						DataEngineUtil.postEvents(postEvent, eventProperty, file);
					}
				}

				new SftpClient(hostName, Integer.parseInt(port), accessKey, secretKey)
						.deleteFile(bucketName.concat("/").concat(fileName));

				logger.info("{} file Processed successfully..", fileName);
			}

		} catch (Exception e) {
			throw new AppException("" + e);
		}

	}

	public void validateFileProperties(Configuration config, String fileName) {
		// Get the selected configuration details.
		String prefix = config.getFilePrefixName();
		String extension = config.getFileExtension();

		// Validate the file extension.
		if (extension != null && !(StringUtils.endsWithIgnoreCase(fileName, extension))) {
			MessageUtil.showError(Labels.getLabel("invalid_file_ext", new String[] { extension }));
			return;
		}

		// Validate the file prefix.

		if (prefix != null && !(StringUtils.startsWith(fileName, prefix))) {
			MessageUtil.showError(Labels.getLabel("invalid_file_prefix", new String[] { prefix }));
		}
	}

	private EventProperties getPostEvent(Map<String, EventProperties> properties, String postEvent) {
		return properties.get(postEvent);
	}

	private List<String> getFileNameList(String pathname, String hostName, int port, String accessKey,
			String secretKey) {
		JSch jsch = new JSch();
		Session session = null;
		Channel channel = null;
		try {
			session = jsch.getSession(accessKey, hostName, port);
			session.setPassword(secretKey);
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.connect();
			channel = session.openChannel("sftp");
			channel.connect();
		} catch (JSchException e1) {
			//
		}

		if (channel == null) {
			return new ArrayList<>();
		}

		LsEntry entry = null;
		List<String> fileName = new ArrayList<>();
		Vector<?> filelist = null;
		try {
			filelist = ((ChannelSftp) channel).ls(pathname);
		} catch (Exception e) {
			throw new AppException(e.getMessage());
		}
		for (int i = 0; i < filelist.size(); i++) {
			entry = (LsEntry) filelist.get(i);
			if (!entry.getFilename().startsWith(".")) {
				fileName.add(entry.getFilename());
			}
		}
		return fileName;
	}

	private String setLocalRepoLocation(String localPath) {
		StringBuilder fileLocation = new StringBuilder(localPath);
		fileLocation.append(File.separator);
		fileLocation.append("repository");
		fileLocation.append(File.separator);
		fileLocation.append(DateUtil.format(DateUtil.getSysDate(), "yyyyMMdd"));
		new File(fileLocation.toString()).mkdirs();
		return fileLocation.toString();
	}

	private DataEngineStatus loadConfig() {
		DataEngineStatus dataEngineStatus = null;
		if (CollectionUtils.isEmpty(disbRespConfig)) {
			try {
				for (Configuration config : dataEngineConfig.getConfigurationList()) {
					String configName = config.getName();
					if (configName.startsWith("DISB_") && configName.endsWith("_IMPORT")) {
						disbRespConfig.add(config);
						dataEngineStatus = dataEngineConfig.getLatestExecution(configName);
					}
				}
			} catch (Exception e) {
				dataEngineStatus = new DataEngineStatus();
			}
		}
		return dataEngineStatus;
	}

	@Override
	public void processResponseFile(long userId, DataEngineStatus status, File file, Media media) {
		logger.debug(Literal.ENTERING);

		String configName = status.getName();

		String name = "";

		if (file != null) {
			name = file.getName();
		} else if (media != null) {
			name = media.getName();
		}

		status.reset();
		status.setFileName(name);
		status.setRemarks("Initiated disbursement response file [ " + name + " ] processing..");

		logger.info(status.getRemarks());

		DataEngineImport dataEngine;
		dataEngine = new DataEngineImport(dataSource, userId, App.DATABASE.name(), true, SysParamUtil.getAppValueDate(),
				status);
		dataEngine.setFile(file);
		dataEngine.setMedia(media);
		dataEngine.setValueDate(SysParamUtil.getAppValueDate());

		Map<String, Object> filterMap = new HashMap<>();
		filterMap.put(DisbursementConstants.STATUS_AWAITCON, DisbursementConstants.STATUS_AWAITCON);
		dataEngine.setFilterMap(filterMap);

		try {
			dataEngine.importData(configName);
		} catch (Exception e) {
			status.setRemarks(e.getMessage());
			throw new AppException(e.getMessage());
		}

		do {
			if ("S".equals(status.getStatus()) || "F".equals(status.getStatus())) {
				if (status.getSuccessRecords() > 0) {
					receiveResponse(status);
				}
				break;
			}
		} while ("S".equals(status.getStatus()) || "F".equals(status.getStatus()));

		logger.info("{} file processing completed", name);

		logger.debug(Literal.LEAVING);
	}

	public void setDisbursementProcess(DisbursementProcess disbursementProcess) {
		this.disbursementProcess = disbursementProcess;
	}

	public void setPaymentProcess(PaymentProcess paymentProcess) {
		this.paymentProcess = paymentProcess;
	}

	public void setDataEngineConfig(DataEngineConfig dataEngineConfig) {
		this.dataEngineConfig = dataEngineConfig;
	}

}
