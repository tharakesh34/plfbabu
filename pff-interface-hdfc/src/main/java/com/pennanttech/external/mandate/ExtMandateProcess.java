package com.pennanttech.external.mandate;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.TransactionStatus;
import org.zkoss.util.media.AMedia;
import org.zkoss.util.media.Media;
import org.zkoss.util.resource.Labels;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.pennant.app.constants.DataEngineConstants;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.mandate.Mandate;
import com.pennant.pff.extension.MandateExtension;
import com.pennanttech.dataengine.DataEngineExport;
import com.pennanttech.dataengine.DataEngineImport;
import com.pennanttech.dataengine.ValidateRecord;
import com.pennanttech.dataengine.config.DataEngineConfig;
import com.pennanttech.dataengine.model.Configuration;
import com.pennanttech.dataengine.model.DataEngineLog;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.dataengine.model.EventProperties;
import com.pennanttech.dataengine.util.DataEngineUtil;
import com.pennanttech.external.config.dao.ExtGenericDao;
import com.pennanttech.external.util.ApplicationContextProvider;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.ftp.FtpClient;
import com.pennanttech.pennapps.core.ftp.SftpClient;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.external.AbstractInterface;
import com.pennanttech.pff.external.MandateProcesses;
import com.pennanttech.pff.external.service.ExternalInterfaceService;
import com.pennanttech.pff.model.mandate.MandateData;

public class ExtMandateProcess extends AbstractInterface implements MandateProcesses {
	protected final Logger logger = LogManager.getLogger(getClass());

	private ExtGenericDao extGenericDao;
	private ApplicationContext applicationContext;

	@Autowired
	private DataEngineConfig dataEngineConfig;
	private DataEngineImport dataEngine;

	private DataEngineStatus status = new DataEngineStatus();
	private static List<Configuration> MANDATE_CONFIG = new ArrayList<>();
	private static Map<Long, Map<String, EventProperties>> eventProperties = new HashMap<>();
	String localLocation = "";
	String job = "";

	Channel channel = null;
	ChannelSftp channelSftp = null;
	Session session = null;

	@Autowired(required = false)
	@Qualifier("mandateUploadValidationImpl")
	private ValidateRecord mandateUploadValidationImpl;
	@Autowired(required = false)
	@Qualifier("uploadToDownloadValidationImpl")
	private ValidateRecord uploadToDownloadValidationImpl;
	private ExternalInterfaceService externalInterfaceService;

	public ExtMandateProcess() {
		super();

	}

	@Override
	public DataEngineStatus sendReqest(MandateData mandateData) {

		long processId = mandateData.getProcess_Id();
		Date fromDate = mandateData.getFromDate();
		Date toDate = mandateData.getToDate();
		long userId = mandateData.getUserId();
		String userName = mandateData.getUserName();
		String selectedBranchs = mandateData.getSelectedBranchs();
		String entity = mandateData.getEntity();
		String type = mandateData.getType();
		long partnerBankId = mandateData.getPartnerBankId();

		Map<String, Object> filterMap = new HashMap<>();
		Map<String, Object> parameterMap = new HashMap<>();
		filterMap.put("PROCESS_ID", processId);
		filterMap.put("FROMDATE", fromDate);
		filterMap.put("TODATE", toDate);
		filterMap.put("MANDATETYPE", type);
		if (partnerBankId > 0) {
			filterMap.put("PARTNERBANKID", partnerBankId);
		}

		if (StringUtils.isNotBlank(selectedBranchs)) {
			filterMap.put("BRANCHCODE", Arrays.asList(selectedBranchs.split(",")));
		}

		parameterMap.put("USER_NAME", userName);
		parameterMap.put("ENTITY_CODE", entity);
		parameterMap.put("ddMMyy", DateUtil.getSysDate("ddMMyy"));
		parameterMap.put("MMddyyyy", DateUtil.getSysDate("MMddyyyyHHmmss"));
		parameterMap.put("Remarks", mandateData.getRemarks());
		parameterMap.put("ROW_NUM", 0);

		parameterMap.put("SEQ_LPAD_SIZE", 4);
		parameterMap.put("SEQ_LPAD_VALUE", "0");

		parameterMap.put("SYS_DATE_ddMMYYYY", DateUtil.format(SysParamUtil.getAppDate(), "ddMMYYYY"));

		addCustomParameter(parameterMap);

		DataEngineExport dataEngine = null;
		dataEngine = new DataEngineExport(dataSource, userId, App.DATABASE.name(), true,
				SysParamUtil.getAppValueDate());

		return genetare(dataEngine, userName, filterMap, parameterMap);
	}

	/**
	 * @param userId
	 * @param userName
	 * @param filterMap
	 * @param parameterMap
	 * @throws Exception
	 */
	protected DataEngineStatus genetare(DataEngineExport dataEngine, String userName, Map<String, Object> filterMap,
			Map<String, Object> parameterMap) {
		dataEngine.setFilterMap(filterMap);
		dataEngine.setParameterMap(parameterMap);
		dataEngine.setUserName(userName);
		dataEngine.setValueDate(SysParamUtil.getAppValueDate());
		try {
			String configName = null;

			if (MandateExtension.PARTNER_BANK_WISE_EXTARCTION) {
				Long partnerBankId = (Long) filterMap.get("PARTNERBANKID");
				if (partnerBankId == null) {
					partnerBankId = 0L;
				}

				configName = getConfigName(filterMap.get("MANDATETYPE").toString(),
						Long.valueOf(partnerBankId.toString()));
			}
			if (configName == null) {
				configName = "MANDATES_EXPORT";
			}

			return dataEngine.exportData(configName);
		} catch (Exception e) {
			throw new AppException("MANDATES_EXPORT", e);
		}
	}

	public String getConfigName(String mode, long partnerBank) {
		String sql = "Select Config_Name from Partnerbanks_Data_Engine Where PayMode = ? and PartnerBankId= ? and Type = ? and RequestType = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return namedJdbcTemplate.getJdbcOperations().queryForObject(sql, String.class, mode, partnerBank,
					DataEngineConstants.MANDATE, DataEngineConstants.EXPORT);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
		}

		return null;
	}

	public void processAutoResponseFiles(String job) {
		logger.info("Processing Mandate Respone files..");

		loadConfig();

		for (Configuration configuration : MANDATE_CONFIG) {
			if (job.equals(configuration.getName())) {
				status.setName(configuration.getName());
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
		logger.error(Literal.LEAVING);

	}

	private List<File> getListOfFilesFromFTP(EventProperties eventProperty, String protocol, Configuration config) {
		logger.info("Connecting into SFTP Shared location to Retreive Files..");

		if (eventProperty == null) {
			return null;
		}

		List<String> fileNames = null;
		FtpClient ftpClient = null;
		try {
			String hostName = eventProperty.getHostName();
			String port = eventProperty.getPort();
			String accessKey = eventProperty.getAccessKey();
			String secretKey = eventProperty.getSecretKey();
			String bucketName = eventProperty.getBucketName();

			if ("FTP".equals(protocol)) {
				ftpClient = new FtpClient(hostName, Integer.parseInt(port), accessKey, secretKey);
				fileNames = ftpClient.getFileNameList(bucketName);
			} else if ("SFTP".equals(protocol)) {
				logger.info("Connecting to SFTP..");
				ftpClient = new SftpClient(hostName, Integer.parseInt(port), accessKey, secretKey);
				logger.info("Connected to SFTP..");
				fileNames = getFileNameList(bucketName, hostName, Integer.parseInt(port), accessKey, secretKey);
			}

			for (String fileName : fileNames) {
				validateFileProperties(config, fileName);
				ftpClient.download(eventProperty.getBucketName(), localLocation, fileName);
				File file = new File(localLocation.concat(File.separator).concat(fileName));
				if (file.exists()) {
					byte[] data = FileUtils.readFileToByteArray(file);
					Media aMedia = new AMedia(file.getName(), "xls", null, data);
					logger.info("Started mandate Processing");
					processResponseFile(1000L, file, aMedia, status);
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

					logger.info("{} file processed successfully.." + fileName);
				} else {
					logger.info(fileName + " does not exists");
				}
			}
		} catch (Exception e) {
			logger.info(Literal.EXCEPTION, e);
			throw new AppException("" + e);
		} finally {
			// FIXME:: Gopal.p
			/*
			 * if (ftpClient != null) { ftpClient.disconnect(); }
			 */
		}
		return null;
	}

	private EventProperties getPostEvent(Map<String, EventProperties> properties, String postEvent) {
		return properties.get(postEvent);
	}

	public void validateFileProperties(Configuration config, String fileName) {
		// Get the selected configuration details.
		String prefix = config.getFilePrefixName();
		String extension = config.getFileExtension();
		List<String> entityCodes = getEntityCodes();
		for (String entity : entityCodes) {
			String fileName2 = entity.concat(prefix);
			if (!(StringUtils.containsAny(fileName, fileName2))) {
				MessageUtil.showError("Invalid File Name");
				return;
			}
		}
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

	public List<String> getEntityCodes() {
		logger.debug(Literal.ENTERING);
		StringBuilder sql = null;
		try {
			sql = new StringBuilder("Select EntityCode ");
			sql.append("from  Entity ");

			logger.debug("Query--->" + sql.toString());
			return jdbcTemplate.queryForList(sql.toString(), String.class);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return null;
	}

	/**
	 * Returns list of files from the FTP server contained in the given path
	 * 
	 * @param pathname The path name in the FTP server.
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
			logger.info(Literal.EXCEPTION, e1);
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
			if (StringUtils.isNotEmpty(FilenameUtils.getExtension(entry.getFilename()))
					&& !entry.getFilename().startsWith(".")) {
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
		if (CollectionUtils.isEmpty(MANDATE_CONFIG)) {
			try {
				for (Configuration config : dataEngineConfig.getConfigurationList()) {
					String configName = config.getName();
					if (configName.equals("MANDATES_IMPORT") || configName.equals("MANDATES_ACK")) {
						MANDATE_CONFIG.add(config);
					}
				}
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			}
		}
	}

	@Override
	public void processResponseFile(long userId, File file, Media media, DataEngineStatus status) throws Exception {
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
		status.setRemarks("initiated Mandate response file [ " + name + " ] processing..");

		dataEngine = new DataEngineImport(dataSource, userId, App.DATABASE.name(), true, SysParamUtil.getAppValueDate(),
				status);
		dataEngine.setFile(file);
		dataEngine.setMedia(media);
		dataEngine.setValueDate(SysParamUtil.getAppValueDate());
		dataEngine.setValidateRecord(mandateUploadValidationImpl);
		dataEngine.importData(configName);

		do {
			if ("S".equals(status.getStatus()) || "F".equals(status.getStatus())) {
				receiveResponse(status.getId(), status);
				break;
			}
		} while ("S".equals(status.getStatus()) || "F".equals(status.getStatus()));

		logger.debug(Literal.LEAVING);

	}

	@Override
	public void receiveResponse(long respBatchId, DataEngineStatus status) throws Exception {

		if (applicationContext == null) {
			applicationContext = ApplicationContextProvider.getApplicationContext();
		}
		if (extGenericDao == null) {
			extGenericDao = applicationContext.getBean("extGenericDao", ExtGenericDao.class);
		}

		long approved = 0;
		long rejected = 0;
		long notMatched = 0;

		StringBuilder sql = new StringBuilder("SELECT");
		sql.append(" MANDATEID, FinID, FINREFERENCE, CUSTCIF, MICR_CODE, IFSC_CODE, ACCT_NUMBER,");
		sql.append(" case when OPENFLAG = 'Y' THEN 'New Open ECS' ELSE 'No Open ECS' END LovValue,");
		sql.append(" MANDATE_TYPE, MANDATE_REG_NO, STATUS, REMARKS");
		sql.append(" FROM MANDATE_RESPONSE");
		sql.append(" WHERE RESP_BATCH_ID = ?");

		logger.debug(Literal.SQL + sql.toString());

		List<Mandate> mandates = jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index, respBatchId);
		}, (rs, rowNum) -> {
			Mandate m = new Mandate();

			m.setMandateID(rs.getLong("MANDATEID"));
			m.setFinID(rs.getLong("FinID"));
			m.setFinReference(rs.getString("FINREFERENCE"));
			m.setCustCIF(rs.getString("CUSTCIF"));
			m.setMICR(rs.getString("MICR_CODE"));
			m.setIFSC(rs.getString("IFSC_CODE"));
			m.setAccNumber(rs.getString("ACCT_NUMBER"));
			m.setLovValue(rs.getString("LovValue"));
			m.setMandateType(rs.getString("MANDATE_TYPE"));
			m.setMandateRef(rs.getString("MANDATE_REG_NO"));
			m.setStatus(rs.getString("STATUS"));
			m.setReason(rs.getString("REMARKS"));

			return m;
		});

		if (mandates.isEmpty()) {
			return;
		}

		try {
			for (Mandate respMandate : mandates) {
				boolean matched = true;
				boolean reject = false;

				Mandate mandate = getMandateById(respMandate.getMandateID());

				StringBuilder remarks = new StringBuilder();
				if (mandate == null) {
					respMandate.setReason("Mandate request not exist or already processed.");
					respMandate.setStatus("F");
					updateMandateResponse(respMandate);
					logMandate(respBatchId, respMandate);
				} else {
					validateMandate(respMandate, mandate, remarks);

					if (remarks.length() > 0) {
						respMandate.setReason(remarks.toString());
						respMandate.setStatus("F");
						updateMandateResponse(respMandate);
						matched = false;
					}

					if (matched) {
						TransactionStatus txnStatus = null;
						try {
							txnStatus = transManager.getTransaction(transDef);

							if (!StringUtils.equals("N", respMandate.getStatus())
									&& StringUtils.isNotEmpty(respMandate.getReason())) {
								// Get reason code and get remark
								// FIXME errors
								// if (extError != null) {
								// String reasonData = extError.getName();
								// reasonData = respMandate.getReason() + " - " + reasonData;
								// respMandate.setReason(reasonData);
								// }
							}

							updateMandates(respMandate);

							try {
								if ("N".equals(respMandate.getStatus())) {
									processSecondaryMandate(mandate);
									processSwappedMandate(mandate);
								}
							} catch (EmptyResultDataAccessException e) {
								logger.warn(Literal.EXCEPTION, e);
							}

							logMandateHistory(respMandate, mandate.getRequestID());
							updateMandateRequest(respMandate, respBatchId);
							updateMandateResponse(respMandate);
							transManager.commit(txnStatus);
						} catch (Exception e) {
							logger.error(Literal.EXCEPTION, e);
							respMandate.setReason(e.getMessage());
							logMandate(respBatchId, respMandate);
							transManager.rollback(txnStatus);
						} finally {
							txnStatus.flush();
							txnStatus = null;
						}

						if ("Y".equals(respMandate.getStatus())) {
							rejected++;
							reject = true;
						} else {
							approved++;
						}
					} else {
						notMatched++;
					}

					if (!matched || reject) {
						logMandate(respBatchId, respMandate);
					}
				}

			}
		} catch (Exception e) {
			//
		} finally {
			updateRemarks(respBatchId, approved, rejected, notMatched, status);
		}
	}

	protected Mandate getMandateById(final long id) {
		StringBuilder sql = new StringBuilder("SELECT");
		sql.append(" ID, MandateID, FINID, FINREFERENCE, CUSTCIF,  MICR_CODE, IFSC_CODE");
		sql.append(", ACCT_NUMBER, OPENFLAG, MANDATE_TYPE, STATUS");
		sql.append(" From MANDATE_REQUESTS");
		sql.append(" Where MandateID = ? and RESP_BATCH_ID IS NULL");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				Mandate m = new Mandate();

				m.setRequestID(rs.getLong("ID"));
				m.setMandateID(rs.getLong("MandateID"));
				m.setFinID(rs.getLong("FINID"));
				m.setFinReference(rs.getString("FINREFERENCE"));
				m.setCustCIF(rs.getString("CUSTCIF"));
				m.setMICR(rs.getString("MICR_CODE"));
				m.setIFSC(rs.getString("IFSC_CODE"));
				m.setAccNumber(rs.getString("ACCT_NUMBER"));
				m.setLovValue(rs.getString("OPENFLAG"));
				m.setMandateType(rs.getString("MANDATE_TYPE"));
				m.setStatus(rs.getString("STATUS"));

				return m;
			}, id);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
		}

		return null;
	}

	protected void updateMandateResponse(Mandate respmandate) {
		String sql = "Update MANDATE_RESPONSE  set REMARKS = ?, STATUS = ? Where MANDATEID = ?";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setString(index++, respmandate.getReason());
			ps.setString(index++, respmandate.getStatus());
			ps.setLong(index, respmandate.getMandateID());
		});
	}

	protected void logMandate(long respBatchId, Mandate respMandate) {
		prepareLog(respBatchId, respMandate);

		String sql = "INSERT INTO DATA_ENGINE_LOG (StatusId, KeyId, Status, Reason) VALUES(?, ?, ?, ?)";

		try {
			this.jdbcOperations.update(sql, ps -> {
				int index = 1;

				ps.setLong(index++, respBatchId);
				ps.setString(index++, String.valueOf(respMandate.getMandateID()));

				if (respMandate.getStatus() != null && respMandate.getStatus().length() == 1) {
					ps.setString(index++, respMandate.getStatus());
				} else {
					ps.setString(index++, "Y");
				}

				ps.setString(index, respMandate.getReason());

			});
		} catch (Exception e) {
			//
		}
	}

	private void prepareLog(long respBatchId, Mandate respMandate) {
		DataEngineLog log = new DataEngineLog();

		log.setStatusId(respBatchId);
		log.setKeyId(String.valueOf(respMandate.getMandateID()));
		log.setReason(respMandate.getReason());

		if (respMandate.getStatus() != null && respMandate.getStatus().length() == 1) {
			log.setStatus(respMandate.getStatus());
		} else {
			log.setStatus("Y");
		}

		MANDATES_IMPORT.getDataEngineLogList().add(log);
	}

	protected void validateMandate(Mandate respMandate, Mandate mandate, StringBuilder remarks) {

		if (!StringUtils.equals(mandate.getFinReference(), respMandate.getFinReference())) {
			if (remarks.length() > 0) {
				remarks.append(", ");
			}
			remarks.append("Fin Reference");
		}

		// if (!StringUtils.equals(mandate.getAccNumber(), respMandate.getAccNumber())) {
		// if (remarks.length() > 0) {
		// remarks.append(", ");
		// }
		// remarks.append("Account No.");
		// }

		// Added for HDFC
		if (!StringUtils.equals("N", respMandate.getStatus()) && StringUtils.isEmpty(respMandate.getReason())) {
			if (remarks.length() > 0) {
				remarks.append(", ");
			}
			remarks.append("Remarks should be mandatory.");
		}
	}

	protected void updateMandates(Mandate respmandate) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();

		StringBuilder sql = new StringBuilder();

		sql.append("Update Mandates");
		sql.append(" Set MANDATEREF = :MANDATEREF, STATUS = :STATUS, REASON = :REASON");
		sql.append("  Where MANDATEID = :MANDATEID");
		if (respmandate.getFinReference() == null) {
			sql.append(" AND ORGREFERENCE is NULL");
		} else {
			sql.append(" AND ORGREFERENCE = :FINREFERENCE");
		}
		sql.append(" AND STATUS = :AC");

		logger.debug(Literal.SQL + sql.toString());

		paramMap.addValue("MANDATEID", respmandate.getMandateID());

		if ("Y".equals(respmandate.getStatus())) {
			paramMap.addValue("STATUS", "REJECTED");
			paramMap.addValue("AC", "AC");
			paramMap.addValue("MANDATEREF", null);
			paramMap.addValue("FINREFERENCE", respmandate.getFinReference());
		} else {
			paramMap.addValue("STATUS", "APPROVED");
			paramMap.addValue("MANDATEREF", respmandate.getMandateRef());
			paramMap.addValue("AC", "AC");
			paramMap.addValue("FINREFERENCE", respmandate.getFinReference());
		}

		paramMap.addValue("REASON", respmandate.getReason());

		this.namedJdbcTemplate.update(sql.toString(), paramMap);

	}

	protected void logMandateHistory(Mandate respmandate, long requestId) {
		String sql = "Insert Into MandatesStatus (MandateID, Status, Reason, ChangeDate, FileID) Values (?, ?, ?, ?, ?)";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setLong(index++, respmandate.getMandateID());

			if ("Y".equals(respmandate.getStatus())) {
				ps.setString(index++, "REJECTED");
			} else {
				ps.setString(index++, "APPROVED");
			}

			ps.setString(index++, respmandate.getReason());
			ps.setDate(index++, JdbcUtil.getDate(SysParamUtil.getAppDate()));
			ps.setLong(index, requestId);

		});
	}

	protected void updateMandateRequest(Mandate respmandate, long id) {
		String sql = "Update Mandate_Requests Set STATUS = ?, REJECT_REASON = ?, RESP_BATCH_ID = ? Where MANDATEID = ?";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setString(index++, respmandate.getStatus());
			ps.setString(index++, respmandate.getReason());
			ps.setLong(index++, id);
			ps.setLong(index, respmandate.getMandateID());
		});
	}

	protected void updateRemarks(long respBatchId, long approved, long rejected, long notMatched,
			DataEngineStatus status) {
		StringBuilder remarks = new StringBuilder(status.getRemarks());
		remarks.append(", Approved: ");
		remarks.append(approved);
		remarks.append(", Rejected: ");
		remarks.append(rejected);
		remarks.append(", Not Matched: ");
		remarks.append(notMatched);

		status.setRemarks(remarks.toString());

		String sql = "UPDATE DATA_ENGINE_STATUS set EndTime = ?, Remarks = ? WHERE Id = ?";

		this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setDate(index++, JdbcUtil.getDate(DateUtil.getSysDate()));
			ps.setString(index++, remarks.toString());
			ps.setLong(index, respBatchId);
		});
	}

	protected void processSecondaryMandate(Mandate respMandate) {
		if (checkSecondaryMandate(respMandate.getMandateID())) {
			makeSecondaryMandateInActive(respMandate.getMandateID());
			loanMandateSwapping(respMandate.getFinID(), respMandate.getMandateID(), respMandate.getMandateType());
		}

	}

	protected void processSwappedMandate(Mandate respMandate) {
		if (checkSwappedMandate(respMandate.getMandateID())) {
			loanMandateSwapping(respMandate.getFinID(), respMandate.getMandateID(), respMandate.getMandateType());
		}
	}

	private boolean checkSecondaryMandate(long mandateID) {
		String sql = "SELECT Count(PRIMARYMANDATEID) FROM MANDATES WHERE PRIMARYMANDATEID = ? AND ACTIVE = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return jdbcOperations.queryForObject(sql, Integer.class, mandateID, 1) > 0;
		} catch (Exception e) {
			throw e;
		}
	}

	private boolean checkSwappedMandate(long mandateID) {
		String sql = "SELECT SWAPISACTIVE FROM MANDATES WHERE MANDATEID = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return jdbcOperations.queryForObject(sql, Boolean.class, mandateID);
		} catch (Exception e) {
			throw e;
		}
	}

	private void loanMandateSwapping(long finID, long mandateId, String repayMethod) {
		String sql = "Update FinanceMain Set MandateID = ?, FinRepayMethod = ? Where FinID = ?";

		logger.debug(Literal.SQL + sql);

		jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setLong(index++, mandateId);
			ps.setString(index++, repayMethod);
			ps.setLong(index, finID);

		});
	}

	private void makeSecondaryMandateInActive(long mandateID) {
		String sql = "UPDATE MANDATES SET ACTIVE = ? WHERE PRIMARYMANDATEID = ?";

		logger.debug(Literal.SQL + sql);

		jdbcOperations.update(sql, ps -> {
			ps.setInt(1, 0);
			ps.setLong(2, mandateID);
		});
	}

	@Override
	public void processUploadToDownLoadFile(long userId, File file, Media media, DataEngineStatus status)
			throws Exception {
		logger.debug(Literal.ENTERING);

		Date appValueDate = SysParamUtil.getAppValueDate();

		Map<String, Object> parameterMap = new HashMap<>();
		parameterMap.put("APP_DATE", SysParamUtil.getAppDate());

		if (file != null) {
			status.setFileName(file.getName());
		} else if (media != null) {
			status.setFileName(media.getName());
		} else {
			status.setFileName("");
		}

		status.reset();
		status.setRemarks("initiated Mandate Upload To Download file [ " + status.getFileName() + " ] processing..");

		dataEngine = new DataEngineImport(dataSource, userId, App.DATABASE.name(), true, appValueDate, status);
		dataEngine.setFile(file);
		dataEngine.setMedia(media);
		dataEngine.setValueDate(appValueDate);
		dataEngine.setValidateRecord(uploadToDownloadValidationImpl);
		dataEngine.setParameterMap(parameterMap);
		dataEngine.importData(status.getName());

		do {
			if ("S".equals(status.getStatus()) || "F".equals(status.getStatus())) {
				processResponse(status.getId(), status);
				break;
			}
		} while ("S".equals(status.getStatus()) || "F".equals(status.getStatus()));

		logger.debug(Literal.LEAVING);
	}

	public void processResponse(long respBatchId, DataEngineStatus status) throws Exception {
		logger.debug(Literal.ENTERING);

		status.setDataEngineLogList(getExceptions(status.getId()));

		String sql = "SELECT MANDATEID FROM MANDATEUPLOADTODOWNLOAD WHERE RESP_BATCH_ID = ?";

		List<Long> mandateIdList = jdbcOperations.queryForList(sql, Long.class, respBatchId);

		if (mandateIdList.isEmpty()) {
			return;
		}

		updateMandateStatus(mandateIdList);

		try {
			MandateData mandateData = new MandateData();
			mandateData.setMandateIdList(mandateIdList);
			MandateProcessThread process = new MandateProcessThread(mandateData, respBatchId);

			Thread thread = new Thread(process);
			thread.start();
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
	}

	public class MandateProcessThread extends Thread {
		MandateData mandateData;
		long respBatchId;

		public MandateProcessThread(MandateData mandateData, long respBatchId) {
			this.mandateData = mandateData;
			this.respBatchId = respBatchId;
		}

		@Override
		public void run() {
			try {
				externalInterfaceService.processMandateRequest(mandateData);
				updateResponseStatus(mandateData.getMandateIdList(), respBatchId);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			}
		}
	}

	private void updateResponseStatus(List<Long> mandateIdList, long batchId) {
		String sql = "Update MandateUploadToDownload set Status = ?, Remarks = ? Where MandateId = ? and Resp_Batch_Id = ?";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.batchUpdate(sql, new BatchPreparedStatementSetter() {
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ps.setString(1, "SUCCESS");
				ps.setString(2, "Sent for Registration Process");
				ps.setLong(3, mandateIdList.get(i));
				ps.setLong(4, batchId);
			}

			public int getBatchSize() {
				return mandateIdList.size();
			}
		});
	}

	public void updateMandateStatus(List<Long> mandateIdList) throws Exception {
		String sql = "Update Mandates Set Status = ? Where MandateId = ? and Status != ?";

		logger.debug(Literal.SQL + sql);

		try {
			this.jdbcOperations.batchUpdate(sql, new BatchPreparedStatementSetter() {
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					ps.setString(1, "NEW");
					ps.setLong(2, mandateIdList.get(i));
					ps.setString(3, "NEW");
				}

				public int getBatchSize() {
					return mandateIdList.size();
				}
			});
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
	}

	public List<DataEngineLog> getExceptions(long batchId) {
		String sql = "Select * from DATA_ENGINE_LOG where StatusId = ?";

		logger.debug(Literal.SQL + sql);

		RowMapper<DataEngineLog> rowMapper = BeanPropertyRowMapper.newInstance(DataEngineLog.class);

		return jdbcOperations.query(sql, rowMapper, batchId);
	}

	protected void addCustomParameter(Map<String, Object> parameterMap) {
		//
	}

	public boolean registerMandate(Mandate mandate) throws Exception {
		return false;
	}

	public void updateMandateStatus() throws Exception {
		//
	}

	public void processMandateResponse() throws Exception {
		//
	}

	public void setDataEngineConfig(DataEngineConfig dataEngineConfig) {
		this.dataEngineConfig = dataEngineConfig;
	}

	@Autowired
	public void setExternalInterfaceService(ExternalInterfaceService externalInterfaceService) {
		this.externalInterfaceService = externalInterfaceService;
	}
}
