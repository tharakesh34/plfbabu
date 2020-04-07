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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
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
import com.pennant.backend.model.finance.FinAutoApprovalDetails;
import com.pennant.backend.model.finance.PaymentInstruction;
import com.pennant.backend.model.insurance.InsurancePaymentInstructions;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.util.DisbursementConstants;
import com.pennanttech.dataengine.DataEngineImport;
import com.pennanttech.dataengine.ValidateRecord;
import com.pennanttech.dataengine.config.DataEngineConfig;
import com.pennanttech.dataengine.model.Configuration;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.dataengine.model.EventProperties;
import com.pennanttech.dataengine.util.DataEngineUtil;
import com.pennanttech.dataengine.util.DateUtil;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.ftp.FtpClient;
import com.pennanttech.pennapps.core.ftp.SftpClient;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.core.process.DisbursementProcess;
import com.pennanttech.pff.core.process.PaymentProcess;
import com.pennanttech.pff.external.AbstractInterface;
import com.pennanttech.pff.external.DisbursementResponse;
import com.pennanttech.pff.logging.dao.FinAutoApprovalDetailDAO;

public class DefaultDisbursementResponse extends AbstractInterface implements DisbursementResponse {
	protected final Logger logger = LogManager.getLogger(getClass());

	private DisbursementProcess disbursementProcess;
	private PaymentProcess paymentProcess;
	private LoggedInUser loggedInUser;
	@Autowired(required = false)
	private ValidateRecord disbursementRespDataValidation;

	@Autowired(required = false)
	private FinAutoApprovalDetailDAO finAutoApprovalDetailDAO;
	@Autowired
	private DataEngineConfig dataEngineConfig;
	private static List<Configuration> DISB_RESP_CONFIG = new ArrayList<>();
	private DataEngineStatus DISB_IMPORT_STATUS = null;
	private static Map<Long, Map<String, EventProperties>> eventProperties = new HashMap<>();
	String localLocation = "";

	Channel channel = null;
	ChannelSftp channelSftp = null;
	Session session = null;

	public DefaultDisbursementResponse() {
		super();
	}

	/*
	 * @Override public void receiveResponse(Object... params) throws Exception { logger.debug(Literal.ENTERING); long
	 * userId = (Long) params[0]; DataEngineStatus status = (DataEngineStatus) params[1]; File file = (File) params[2];
	 * Media media = (Media) params[3];
	 * 
	 * String configName = status.getName();
	 * 
	 * String name = "";
	 * 
	 * if (file != null) { name = file.getName(); } else if (media != null) { name = media.getName(); }
	 * 
	 * status.reset(); status.setFileName(name); status.setRemarks("initiated disbursement response file [ " + name +
	 * " ] processing..");
	 * 
	 * DataEngineImport dataEngine; dataEngine = new DataEngineImport(dataSource, userId, App.DATABASE.name(), true,
	 * getValueDate(), status); dataEngine.setFile(file); dataEngine.setMedia(media);
	 * dataEngine.setValueDate(getValueDate());
	 * 
	 * Map<String, Object> filterMap = new HashMap<>(); filterMap.put(DisbursementConstants.STATUS_AWAITCON,
	 * DisbursementConstants.STATUS_AWAITCON); dataEngine.setFilterMap(filterMap);
	 * 
	 * dataEngine.importData(configName);
	 * 
	 * do { if ("S".equals(status.getStatus()) || "F".equals(status.getStatus())) { receiveResponse(status.getId());
	 * break; } } while ("S".equals(status.getStatus()) || "F".equals(status.getStatus()));
	 * 
	 * logger.info(name + " file processing completed"); }
	 */

	@Override
	public void processAutoResponseFiles() {
		logger.info("Processing Disbursement Respone files..");
		loadConfig();
		for (Configuration configuration : DISB_RESP_CONFIG) {
			localLocation = setLocalRepoLocation(configuration.getUploadPath());

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

				if (property != null) {
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
			}

			if (s3Property != null) {
				// FIXME
			} else if (sharedSFTPProperty != null) {
				getListOfFilesFromFTP(sharedSFTPProperty, "SFTP", configuration);
			} else if (sharedFTPProperty != null) {
				getListOfFilesFromFTP(sharedFTPProperty, "FTP", configuration);
			} else if (sharedNetworkFolderProperty != null) {
				// FIXME
			}

		}

	}

	private List<File> getListOfFilesFromFTP(EventProperties eventProperty, String protocol, Configuration config) {
		logger.info("Connecting into SFTP Shared location to Retreive Files..");
		List<String> fileNames = null;
		try {
			String hostName = eventProperty.getHostName();
			String port = eventProperty.getPort();
			String accessKey = eventProperty.getAccessKey();
			String secretKey = eventProperty.getSecretKey();
			String bucketName = eventProperty.getBucketName();

			FtpClient ftpClient = null;
			if ("FTP".equals(protocol)) {
				ftpClient = new FtpClient(hostName, Integer.parseInt(port), accessKey, secretKey);
				logger.info("Connected to FTP Location");
				fileNames = ftpClient.getFileNameList(bucketName);
			} else if ("SFTP".equals(protocol)) {
				ftpClient = new SftpClient(hostName, Integer.parseInt(port), accessKey, secretKey);
				logger.info("Connected to SFTP Location");
				fileNames = getFileNameList(bucketName, hostName, Integer.parseInt(port), accessKey, secretKey);
			}

			for (String fileName : fileNames) {
				logger.info("Total {} Files are available to Upload in Shared Location.", fileNames.size());
				logger.info("Processing {} response file", fileName);
				validateFileProperties(config, fileName);
				ftpClient.download(eventProperty.getBucketName(), localLocation, fileName);
				File file = new File(localLocation.concat(File.separator).concat(fileName));
				if (file.exists()) {
					byte[] data = FileUtils.readFileToByteArray(file);
					Media aMedia = new AMedia(file.getName(), null, "application/octet-stream", data);
					processResponseFile(1000L, DISB_IMPORT_STATUS, file, aMedia, false, loggedInUser);
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
				} else {
					logger.info(fileName + " does not exists");
				}
			}

		} catch (Exception e) {
			throw new AppException("" + e);
		}
		return null;

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

			return;
		}
	}

	private EventProperties getPostEvent(Map<String, EventProperties> properties, String postEvent) {
		return properties.get(postEvent);
	}

	/**
	 * Returns list of files from the FTP server contained in the given path
	 * 
	 * @param pathname
	 *        The path name in the FTP server.
	 * @param secretKey
	 * @param accessKey
	 * @param port
	 * @return Returns list of files from the FTP server contained in the given path
	 */
	@SuppressWarnings("rawtypes")
	public List<String> getFileNameList(String pathname, String hostName, int port, String accessKey,
			String secretKey) {
		JSch jsch = new JSch();
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
			e1.printStackTrace();
		}
		channelSftp = (ChannelSftp) channel;
		LsEntry entry = null;
		List<String> fileName = new ArrayList<String>();
		Vector filelist = null;
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

	private void loadConfig() {
		if (CollectionUtils.isEmpty(DISB_RESP_CONFIG)) {
			try {
				for (Configuration config : dataEngineConfig.getConfigurationList()) {
					String configName = config.getName();
					if (configName.startsWith("DISB_") && configName.endsWith("_IMPORT")) {
						DISB_RESP_CONFIG.add(config);
						DISB_IMPORT_STATUS = dataEngineConfig.getLatestExecution(configName);
					}
				}
			} catch (Exception e) {
				//
			}
		}
	}

	@Override
	public void processResponseFile(Object... params) throws Exception {
		logger.debug(Literal.ENTERING);
		long userId = (Long) params[0];
		DataEngineStatus status = (DataEngineStatus) params[1];
		File file = (File) params[2];
		Media media = (Media) params[3];
		loggedInUser = (LoggedInUser) params[5];

		String configName = status.getName();

		String name = "";

		if (file != null) {
			name = file.getName();
		} else if (media != null) {
			name = media.getName();
		}

		status.reset();
		status.setFileName(name);
		status.setRemarks("initiated disbursement response file [ " + name + " ] processing..");

		DataEngineImport dataEngine;
		dataEngine = new DataEngineImport(dataSource, userId, App.DATABASE.name(), true, SysParamUtil.getAppValueDate(),
				status);
		dataEngine.setFile(file);
		dataEngine.setMedia(media);
		dataEngine.setValueDate(SysParamUtil.getAppValueDate());
		dataEngine.setValidateRecord(disbursementRespDataValidation);
		Map<String, Object> filterMap = new HashMap<>();
		filterMap.put(DisbursementConstants.STATUS_AWAITCON, DisbursementConstants.STATUS_AWAITCON);
		dataEngine.setFilterMap(filterMap);

		dataEngine.importData(configName);

		do {
			if ("S".equals(status.getStatus()) || "F".equals(status.getStatus())) {
				receiveResponse(status.getId(), status.getStatus(), userId);
				break;
			}
		} while ("S".equals(status.getStatus()) || "F".equals(status.getStatus()));

		logger.info(name + " file processing completed");
	}

	private void receiveResponse(long batchId) throws Exception {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource paramMap = null;
		StringBuilder sql = null;

		// Disbursements
		List<FinAdvancePayments> disbursements = null;
		RowMapper<FinAdvancePayments> rowMapper = null;
		try {
			sql = new StringBuilder();
			sql.append(
					" SELECT FA.PAYMENTID,FA.FINREFERENCE, FA.LINKEDTRANID, DR.PAYMENT_DATE DISBDATE, FA.PAYMENTTYPE, DR.STATUS,");
			sql.append(" FA.BENEFICIARYACCNO, FA.BENEFICIARYNAME, FA.BANKBRANCHID, FA.BANKCODE,");
			sql.append(" FA.PHONECOUNTRYCODE, FA.PHONENUMBER, FA.PHONEAREACODE,");
			sql.append(" DR.CHEQUE_NUMBER LLREFERENCENO, DR.REJECT_REASON REJECTREASON,");
			sql.append(" DR.PAYMENT_DATE CLEARINGDATE, DR.TRANSACTIONREF, FA.PAYMENTSEQ, FA.AMTTOBERELEASED, ");
			sql.append(" PB.ACTYPE AS PARTNERBANKACTYPE, PB.ACCOUNTNO AS PARTNERBANKAC, FA.DISBCCY, FA.LLDATE");
			sql.append(" FROM DISBURSEMENT_REQUESTS DR");
			sql.append(" INNER JOIN FINADVANCEPAYMENTS FA ON FA.PAYMENTID = DR.DISBURSEMENT_ID");
			sql.append(" LEFT JOIN partnerbanks PB ON PB.partnerbankid = FA.partnerbankid");
			sql.append(" WHERE RESP_BATCH_ID = :RESP_BATCH_ID AND CHANNEL = :CHANNEL");
			paramMap = new MapSqlParameterSource();
			paramMap.addValue("RESP_BATCH_ID", batchId);
			paramMap.addValue("CHANNEL", DisbursementConstants.CHANNEL_DISBURSEMENT);

			rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinAdvancePayments.class);
			disbursements = namedJdbcTemplate.query(sql.toString(), paramMap, rowMapper);

			for (FinAdvancePayments disbursement : disbursements) {
				try {
					disbursementProcess.process(disbursement);
				} catch (Exception e) {
					logger.error(Literal.EXCEPTION, e);
				}
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		// Payments..
		List<PaymentInstruction> instructions = null;
		RowMapper<PaymentInstruction> instructionRowMapper = null;
		try {
			sql = new StringBuilder();
			sql.append(" SELECT PH.FINREFERENCE, PH.LINKEDTRANID, PI.PAYMENTID, PI.BANKBRANCHID, PI.ACCOUNTNO, ");
			sql.append(
					" PI.ACCTHOLDERNAME, PI.PHONECOUNTRYCODE, PI.PHONENUMBER, PI.PAYMENTINSTRUCTIONID, PI.PAYMENTAMOUNT,");
			sql.append(" PI.PAYMENTAMOUNT, PI.PAYMENTTYPE, DR.STATUS, DR.REJECT_REASON REJECTREASON,");
			sql.append(" DR.PAYMENT_DATE CLEARINGDATE, DR.TRANSACTIONREF");
			sql.append(" FROM DISBURSEMENT_REQUESTS DR");
			sql.append(" INNER JOIN PAYMENTINSTRUCTIONS PI ON PI.PAYMENTINSTRUCTIONID = DR.DISBURSEMENT_ID");
			sql.append(" INNER JOIN PAYMENTHEADER PH ON PH.PAYMENTID = PI.PAYMENTID");
			sql.append(" WHERE RESP_BATCH_ID = :RESP_BATCH_ID  AND CHANNEL = :CHANNEL");
			paramMap = new MapSqlParameterSource();
			paramMap.addValue("RESP_BATCH_ID", batchId);
			paramMap.addValue("CHANNEL", DisbursementConstants.CHANNEL_PAYMENT);

			instructionRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(PaymentInstruction.class);
			instructions = namedJdbcTemplate.query(sql.toString(), paramMap, instructionRowMapper);

			for (PaymentInstruction instruction : instructions) {
				try {
					paymentProcess.process(instruction);
				} catch (Exception e) {
					logger.error(Literal.EXCEPTION, e);
				}
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		// Insurance payments..
		List<InsurancePaymentInstructions> insPaymentInstructions = null;
		RowMapper<InsurancePaymentInstructions> insPaymentInstructionRowMapper = null;
		try {
			sql = new StringBuilder();
			sql.append(" SELECT PI.LINKEDTRANID, PI.ID, VPA.BANKBRANCHID, VPA.ACCOUNTNUMBER, ");
			sql.append(" AVD.DEALERNAME, PI.PAYMENTAMOUNT, PI.PAYMENTTYPE, DR.STATUS,");
			sql.append(" DR.REJECT_REASON REJECTREASON, PI.PROVIDERID,");
			sql.append(" DR.PAYMENT_DATE RESPDATE, DR.TRANSACTIONREF, DR.FINREFERENCE FROM DISBURSEMENT_REQUESTS DR");
			sql.append(" INNER JOIN INSURANCEPAYMENTINSTRUCTIONS PI ON PI.ID = DR.DISBURSEMENT_ID");
			sql.append(" INNER JOIN VASPROVIDERACCDETAIL VPA ON VPA.PROVIDERID = PI.PROVIDERID");
			sql.append(" INNER JOIN BANKBRANCHES BB ON BB.BANKBRANCHID = VPA.BANKBRANCHID");
			sql.append(" INNER JOIN AMTVEHICLEDEALER AVD ON AVD.DEALERID = VPA.PROVIDERID");
			sql.append(" WHERE RESP_BATCH_ID = :RESP_BATCH_ID  AND CHANNEL = :CHANNEL");
			paramMap = new MapSqlParameterSource();
			paramMap.addValue("RESP_BATCH_ID", batchId);
			paramMap.addValue("CHANNEL", DisbursementConstants.CHANNEL_INSURANCE);

			insPaymentInstructionRowMapper = ParameterizedBeanPropertyRowMapper
					.newInstance(InsurancePaymentInstructions.class);
			insPaymentInstructions = namedJdbcTemplate.query(sql.toString(), paramMap, insPaymentInstructionRowMapper);

			for (InsurancePaymentInstructions instruction : insPaymentInstructions) {
				try {
					// For VAS Account postings
					instruction.setUserDetails(loggedInUser);
					paymentProcess.processInsPayments(instruction);
				} catch (Exception e) {
					logger.error(Literal.EXCEPTION, e);
				}
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
	}

	@Autowired
	public void setDisbursementProcess(DisbursementProcess disbursementProcess) {
		this.disbursementProcess = disbursementProcess;
	}

	@Autowired
	public void setPaymentProcess(PaymentProcess paymentProcess) {
		this.paymentProcess = paymentProcess;
	}

	@Override
	public void receiveResponse(Object... params) throws Exception {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource paramMap = null;
		StringBuilder sql = null;
		List<String> channelList = new ArrayList<>();
		channelList.add(DisbursementConstants.CHANNEL_DISBURSEMENT);

		// Disbursements
		//Below fields in select query not to be removed.
		List<FinAdvancePayments> finAdvPayments = null;
		RowMapper<FinAdvancePayments> rowMapper = null;
		try {
			sql = new StringBuilder();
			sql.append(" SELECT FA.PAYMENTID,FA.FINREFERENCE, FA.LINKEDTRANID, DR.PAYMENT_DATE DISBDATE");
			sql.append(", FA.PAYMENTTYPE, DR.STATUS, FA.BENEFICIARYACCNO, FA.BENEFICIARYNAME, FA.BANKBRANCHID");
			sql.append(", FA.BANKCODE, FA.PHONECOUNTRYCODE, FA.PHONENUMBER, FA.PHONEAREACODE, FA.AMTTOBERELEASED");
			sql.append(", FA.RECORDTYPE, DR.CHEQUE_NUMBER LLREFERENCENO, DR.REJECT_REASON REJECTREASON");
			sql.append(", DR.REALIZATION_DATE REALIZATIONDATE, DR.DOWNLOADED_ON DOWNLOADEDON");
			sql.append(", DR.PAYMENT_DATE CLEARINGDATE, DR.TRANSACTIONREF, FA.PAYMENTSEQ");
			sql.append(", PB.ACTYPE AS PARTNERBANKACTYPE, PB.ACCOUNTNO AS PARTNERBANKAC, FA.DISBCCY, FA.LLDATE");
			sql.append(" FROM DISBURSEMENT_REQUESTS DR");
			sql.append(" INNER JOIN FINADVANCEPAYMENTS FA ON FA.PAYMENTID = DR.DISBURSEMENT_ID");
			sql.append(" LEFT JOIN partnerbanks PB ON PB.partnerbankid = FA.partnerbankid");
			sql.append(" WHERE RESP_BATCH_ID = :RESP_BATCH_ID AND CHANNEL IN(:CHANNEL) ");
			paramMap = new MapSqlParameterSource();
			paramMap.addValue("RESP_BATCH_ID", params[0]);
			paramMap.addValue("CHANNEL", channelList);

			rowMapper = BeanPropertyRowMapper.newInstance(FinAdvancePayments.class);
			finAdvPayments = namedJdbcTemplate.query(sql.toString(), paramMap, rowMapper);

			List<FinAutoApprovalDetails> autoAppList = new ArrayList<FinAutoApprovalDetails>();

			for (FinAdvancePayments finAdvPayment : finAdvPayments) {
				try {
					disbursementProcess.process(finAdvPayment);
					boolean autoApprove = false;

					// get the QDP flag from Loan level.
					FinanceType financeType = finAutoApprovalDetailDAO
							.getQDPflagByFinref(finAdvPayment.getFinReference());

					if (financeType != null) {
						// Check for the AutoApprove flag from LoanType.
						if (financeType.isQuickDisb() && financeType.isAutoApprove()) {
							autoApprove = true;
						}
					}
					/*
					 * //FIXME: Need to check Payment Mode based AutoApprove Required or not. if
					 * ((StringUtils.equals(finAdvPayment.getPaymentType(), DisbursementConstants.PAYMENT_TYPE_NEFT) ||
					 * StringUtils.equals(finAdvPayment.getPaymentType(), DisbursementConstants.PAYMENT_TYPE_RTGS)) &&
					 * StringUtils.equals(DisbursementConstants.STATUS_PAID, finAdvPayment.getStatus())) { autoApprove =
					 * true; } else if ((StringUtils.equals(finAdvPayment.getPaymentType(),
					 * DisbursementConstants.PAYMENT_TYPE_CHEQUE) || StringUtils.equals(finAdvPayment.getPaymentType(),
					 * DisbursementConstants.PAYMENT_TYPE_DD)) &&
					 * StringUtils.equals(DisbursementConstants.STATUS_REALIZED, finAdvPayment.getStatus())) {
					 * autoApprove = true; }
					 */

					if (autoApprove) {

						FinAutoApprovalDetails detail = new FinAutoApprovalDetails();
						detail.setFinReference(finAdvPayment.getFinReference());
						detail.setBatchId(Long.valueOf(params[0].toString()));
						detail.setDisbId(finAdvPayment.getPaymentId());
						detail.setRealizedDate(finAdvPayment.getRealizationDate());
						if (detail.getRealizedDate() == null) {
							detail.setRealizedDate(finAdvPayment.getClearingDate());
						}
						detail.setStatus(DisbursementConstants.AUTODISB_STATUS_PENDING);
						detail.setUserId(Long.valueOf(params[2].toString()));
						detail.setDownloadedon(finAdvPayment.getDownloadedon());
						autoAppList.add(detail);
					}

				} catch (Exception e) {
					logger.error(Literal.EXCEPTION, e);
				}
			}
			finAutoApprovalDetailDAO.logFinAutoApprovalDetails(autoAppList);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		// Payments..
		List<PaymentInstruction> instructions = null;
		RowMapper<PaymentInstruction> instructionRowMapper = null;
		try {
			sql = new StringBuilder();
			sql.append(" SELECT PH.FINREFERENCE, PH.LINKEDTRANID, PI.PAYMENTID, PI.BANKBRANCHID, PI.ACCOUNTNO, ");
			sql.append(
					" PI.ACCTHOLDERNAME, PI.PHONECOUNTRYCODE, PI.PHONENUMBER, PI.PAYMENTINSTRUCTIONID, PI.PAYMENTAMOUNT,");
			sql.append(
					" PI.PAYMENTAMOUNT, PI.PAYMENTTYPE, DR.STATUS, DR.REJECT_REASON REJECTREASON,DR.REALIZATION_DATE REALIZATIONDATE,");
			sql.append(" DR.PAYMENT_DATE CLEARINGDATE, DR.TRANSACTIONREF");
			sql.append(" FROM DISBURSEMENT_REQUESTS DR");
			sql.append(" INNER JOIN PAYMENTINSTRUCTIONS PI ON PI.PAYMENTINSTRUCTIONID = DR.DISBURSEMENT_ID");
			sql.append(" INNER JOIN PAYMENTHEADER PH ON PH.PAYMENTID = PI.PAYMENTID");
			sql.append(" WHERE RESP_BATCH_ID = :RESP_BATCH_ID AND CHANNEL = :CHANNEL");
			paramMap = new MapSqlParameterSource();
			paramMap.addValue("RESP_BATCH_ID", params[0]);
			paramMap.addValue("CHANNEL", DisbursementConstants.CHANNEL_PAYMENT);

			instructionRowMapper = BeanPropertyRowMapper.newInstance(PaymentInstruction.class);
			instructions = namedJdbcTemplate.query(sql.toString(), paramMap, instructionRowMapper);

			for (PaymentInstruction instruction : instructions) {
				try {
					paymentProcess.process(instruction);
				} catch (Exception e) {
					logger.error(Literal.EXCEPTION, e);
				}
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		// Insurance payments..
		List<InsurancePaymentInstructions> insPaymentInstructions = null;
		RowMapper<InsurancePaymentInstructions> insPaymentInstructionRowMapper = null;
		try {
			sql = new StringBuilder();
			sql.append(" SELECT PI.LINKEDTRANID, PI.ID, VPA.BANKBRANCHID, VPA.ACCOUNTNUMBER, ");
			sql.append(" AVD.DEALERNAME, AVD.DEALERTELEPHONE, PI.PAYMENTAMOUNT, PI.PAYMENTTYPE, DR.STATUS,");
			sql.append(" DR.REJECT_REASON REJECTREASON,DR.REALIZATION_DATE REALIZATIONDATE,");
			sql.append(" DR.PAYMENT_DATE RESPDATE, DR.TRANSACTIONREF,");
			sql.append(" PI.PROVIDERID, DR.FINREFERENCE FROM DISBURSEMENT_REQUESTS DR ");
			sql.append(" INNER JOIN INSURANCEPAYMENTINSTRUCTIONS PI ON PI.ID = DR.DISBURSEMENT_ID");
			sql.append(" INNER JOIN VASPROVIDERACCDETAIL VPA ON VPA.PROVIDERID = PI.PROVIDERID");
			sql.append(" INNER JOIN BANKBRANCHES BB ON BB.BANKBRANCHID = VPA.BANKBRANCHID");
			sql.append(" INNER JOIN AMTVEHICLEDEALER AVD ON AVD.DEALERID = VPA.PROVIDERID");
			sql.append(" WHERE RESP_BATCH_ID = :RESP_BATCH_ID AND CHANNEL = :CHANNEL");
			paramMap = new MapSqlParameterSource();
			paramMap.addValue("RESP_BATCH_ID", params[0]);
			paramMap.addValue("CHANNEL", DisbursementConstants.CHANNEL_INSURANCE);

			insPaymentInstructionRowMapper = BeanPropertyRowMapper.newInstance(InsurancePaymentInstructions.class);
			insPaymentInstructions = namedJdbcTemplate.query(sql.toString(), paramMap, insPaymentInstructionRowMapper);

			for (InsurancePaymentInstructions instruction : insPaymentInstructions) {
				try {
					// For VAS Account postings
					instruction.setUserDetails(loggedInUser);
					paymentProcess.processInsPayments(instruction);
				} catch (Exception e) {
					logger.error(Literal.EXCEPTION, e);
				}
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
	}
}