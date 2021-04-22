/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  AbstractMandateProcess.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  01-07-2017    														*
 *                                                                  						*
 * Modified Date    :  28-05-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-07-2017       Pennant	                 0.1                                            * 
 * 28-05-2018       Srikanth.m	             0.2          Add additional fields             * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennanttech.pff.external.mandate;

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
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
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
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.mandate.Mandate;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennanttech.dataengine.DataEngineExport;
import com.pennanttech.dataengine.DataEngineImport;
import com.pennanttech.dataengine.ValidateRecord;
import com.pennanttech.dataengine.config.DataEngineConfig;
import com.pennanttech.dataengine.model.Configuration;
import com.pennanttech.dataengine.model.DataEngineLog;
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
import com.pennanttech.pff.external.AbstractInterface;
import com.pennanttech.pff.external.MandateProcesses;
import com.pennanttech.pff.external.service.ExternalInterfaceService;
import com.pennanttech.pff.model.mandate.MandateData;

public class DefaultMandateProcess extends AbstractInterface implements MandateProcesses {
	protected final Logger logger = LogManager.getLogger(getClass());
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

	public DefaultMandateProcess() {
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

		Map<String, Object> filterMap = new HashMap<>();
		Map<String, Object> parameterMap = new HashMap<>();
		filterMap.put("PROCESS_ID", processId);
		filterMap.put("FROMDATE", fromDate);
		filterMap.put("TODATE", toDate);

		if (StringUtils.isNotBlank(selectedBranchs)) {
			filterMap.put("BRANCHCODE", Arrays.asList(selectedBranchs.split(",")));
		}

		parameterMap.put("USER_NAME", userName);
		parameterMap.put("ENTITY_CODE", entity);
		parameterMap.put("ddMMyy", DateUtil.getSysDate("ddMMyy"));
		parameterMap.put("MMddyyyy", DateUtil.getSysDate("MMddyyyyHHmmss"));

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
			return dataEngine.exportData("MANDATES_EXPORT");
		} catch (Exception e) {
			e.printStackTrace();
			throw new AppException("MANDATES_EXPORT", e);
		}
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
				fileNames = ftpClient.getFileNameList(bucketName);
			} else if ("SFTP".equals(protocol)) {
				logger.info("Connecting to SFTP..");
				ftpClient = new SftpClient(hostName, Integer.parseInt(port), accessKey, secretKey);
				logger.info("Connected to SFTP..");
				fileNames = getFileNameList(bucketName, hostName, Integer.parseInt(port), accessKey, secretKey);
				logger.info("Taken Files from SFTP..");
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
	 * @param pathname
	 *            The path name in the FTP server.
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
		MapSqlParameterSource paramMap = null;
		StringBuilder sql = null;
		List<Mandate> mandates = null;
		RowMapper<Mandate> rowMapper = null;

		long approved = 0;
		long rejected = 0;
		long notMatched = 0;

		sql = new StringBuilder();
		sql.append(" SELECT MANDATEID, FINREFERENCE, CUSTCIF, MICR_CODE MICR, IFSC_CODE IFSC, ACCT_NUMBER AccNumber,");
		sql.append(" case when OPENFLAG = 'Y' THEN 'New Open ECS' ELSE 'No Open ECS' END lovValue,");
		sql.append(" MANDATE_TYPE MandateType, MANDATE_REG_NO mandateRef, STATUS, REMARKS reason");
		sql.append(" FROM MANDATE_RESPONSE");
		sql.append(" WHERE RESP_BATCH_ID = :RESP_BATCH_ID");

		paramMap = new MapSqlParameterSource();
		paramMap.addValue("RESP_BATCH_ID", respBatchId);

		rowMapper = BeanPropertyRowMapper.newInstance(Mandate.class);
		mandates = namedJdbcTemplate.query(sql.toString(), paramMap, rowMapper);

		if (mandates == null || mandates.isEmpty()) {
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
			logger.error(Literal.EXCEPTION, e);
		} finally {
			updateRemarks(respBatchId, approved, rejected, notMatched, status);
		}
	}

	protected Mandate getMandateById(final long id) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = null;
		StringBuilder sql = new StringBuilder();

		sql.append("SELECT ID RequestID, MandateID, FINREFERENCE, CUSTCIF,  MICR_CODE MICR, IFSC_CODE IFSC");
		sql.append(", ACCT_NUMBER AccNumber, OPENFLAG lovValue, MANDATE_TYPE MandateType, STATUS ");
		sql.append(" From MANDATE_REQUESTS");
		sql.append(" Where MandateID =:MandateID and RESP_BATCH_ID IS NULL");
		source = new MapSqlParameterSource();
		source.addValue("MandateID", id);

		RowMapper<Mandate> typeRowMapper = BeanPropertyRowMapper.newInstance(Mandate.class);
		try {
			return this.namedJdbcTemplate.queryForObject(sql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

	protected void updateMandateResponse(Mandate respmandate) {
		logger.debug(Literal.ENTERING);
		MapSqlParameterSource paramMap = new MapSqlParameterSource();

		StringBuilder sql = new StringBuilder();
		sql.append("update MANDATE_RESPONSE");
		sql.append(" set REMARKS = :REMARKS , STATUS = :STATUS");
		sql.append(" where MANDATEID = :MANDATEID");

		paramMap.addValue("MANDATEID", respmandate.getMandateID());
		paramMap.addValue("REMARKS", respmandate.getReason());
		paramMap.addValue("STATUS", respmandate.getStatus());

		try {
			this.namedJdbcTemplate.update(sql.toString(), paramMap);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
	}

	protected void logMandate(long respBatchId, Mandate respMandate) {
		SqlParameterSource beanParameters = null;
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

		StringBuffer query = new StringBuffer();
		query.append(" INSERT INTO DATA_ENGINE_LOG");
		query.append(" (StatusId, KeyId, Status, Reason)");
		query.append(" VALUES(:StatusId, :KeyId, :Status, :Reason)");

		try {
			beanParameters = new BeanPropertySqlParameterSource(log);
			this.namedJdbcTemplate.update(query.toString(), beanParameters);
		} catch (Exception e) {
			logger.error("Exception:", e);
		}
	}

	protected void validateMandate(Mandate respMandate, Mandate mandate, StringBuilder remarks) {
		if (!SysParamUtil.isAllowed(SMTParameterConstants.MANDATE_DOWNLOAD_STOP_CIF_VALIDATION)) {
			if (!StringUtils.equals(mandate.getCustCIF(), respMandate.getCustCIF())) {
				if (remarks.length() > 0) {
					remarks.append(", ");
				}
				remarks.append("Customer Code");
			}

			if (respMandate.getMICR() != null && !mandate.getMICR().equals(respMandate.getMICR())) {
				if (remarks.length() > 0) {
					remarks.append(", ");
				}
				remarks.append("MICR Code");
			}
		}

		if (!StringUtils.equals(mandate.getFinReference(), respMandate.getFinReference())) {
			if (remarks.length() > 0) {
				remarks.append(", ");
			}
			remarks.append("Fin Reference");
		}

		if (!StringUtils.equals(mandate.getAccNumber(), respMandate.getAccNumber())) {
			if (remarks.length() > 0) {
				remarks.append(", ");
			}
			remarks.append("Account No.");
		}

	}

	protected void updateMandates(Mandate respmandate) {
		logger.debug(Literal.ENTERING);
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

		logger.debug(Literal.LEAVING);
	}

	protected void logMandateHistory(Mandate respmandate, long requestId) {
		logger.debug(Literal.ENTERING);
		MapSqlParameterSource paramMap = new MapSqlParameterSource();

		StringBuilder sql = new StringBuilder("Insert Into MandatesStatus");
		sql.append(" (mandateID, status, reason, changeDate, fileID)");
		sql.append(" Values(:mandateID, :STATUS, :REASON, :changeDate,:fileID)");

		paramMap.addValue("mandateID", respmandate.getMandateID());

		if ("Y".equals(respmandate.getStatus())) {
			paramMap.addValue("STATUS", "REJECTED");
		} else {
			paramMap.addValue("STATUS", "APPROVED");
		}

		paramMap.addValue("REASON", respmandate.getReason());
		paramMap.addValue("changeDate", SysParamUtil.getAppDate());
		paramMap.addValue("fileID", requestId);

		this.namedJdbcTemplate.update(sql.toString(), paramMap);
		logger.debug(Literal.LEAVING);
	}

	protected void updateMandateRequest(Mandate respmandate, long id) {
		logger.debug(Literal.ENTERING);
		MapSqlParameterSource paramMap = new MapSqlParameterSource();

		StringBuilder sql = new StringBuilder();
		sql.append("Update Mandate_Requests");
		sql.append(" Set STATUS = :STATUS, REJECT_REASON = :REASON, RESP_BATCH_ID = :RESP_BATCH_ID");
		sql.append("  Where MANDATEID = :MANDATEID");

		paramMap.addValue("MANDATEID", respmandate.getMandateID());
		paramMap.addValue("STATUS", respmandate.getStatus());
		paramMap.addValue("MANDATEREF", respmandate.getMandateRef());
		paramMap.addValue("REASON", respmandate.getReason());
		paramMap.addValue("RESP_BATCH_ID", id);

		this.namedJdbcTemplate.update(sql.toString(), paramMap);

		logger.debug(Literal.LEAVING);
	}

	protected void updateRemarks(long respBatchId, long approved, long rejected, long notMatched,
			DataEngineStatus status) {
		MapSqlParameterSource parameterSource = new MapSqlParameterSource();

		StringBuilder remarks = new StringBuilder(status.getRemarks());
		remarks.append(", Approved: ");
		remarks.append(approved);
		remarks.append(", Rejected: ");
		remarks.append(rejected);
		remarks.append(", Not Matched: ");
		remarks.append(notMatched);

		status.setRemarks(remarks.toString());

		StringBuffer query = new StringBuffer();
		query.append(" UPDATE DATA_ENGINE_STATUS set EndTime = :EndTime, Remarks = :Remarks ");
		query.append(" WHERE Id = :Id");

		parameterSource.addValue("EndTime", DateUtil.getSysDate());
		parameterSource.addValue("Remarks", remarks.toString());
		parameterSource.addValue("Id", respBatchId);

		try {
			this.namedJdbcTemplate.update(query.toString(), parameterSource);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
	}

	protected void processSecondaryMandate(Mandate respMandate) {

		boolean secondaryMandate = checkSecondaryMandate(respMandate.getMandateID());
		if (secondaryMandate) {
			makeSecondaryMandateInActive(respMandate.getMandateID());
			loanMandateSwapping(respMandate.getFinReference(), respMandate.getMandateID(),
					respMandate.getMandateType());

		}

	}

	protected void processSwappedMandate(Mandate respMandate) {

		boolean swappedMandate = checkSwappedMandate(respMandate.getMandateID());
		if (swappedMandate) {
			loanMandateSwapping(respMandate.getFinReference(), respMandate.getMandateID(),
					respMandate.getMandateType());

		}
	}

	private boolean checkSecondaryMandate(long mandateID) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();

		StringBuilder selectSql = new StringBuilder("SELECT Count(*) FROM MANDATES");
		selectSql.append(" WHERE PRIMARYMANDATEID = :PRIMARYMANDATEID AND ACTIVE = :ACTIVE");
		paramMap.addValue("PRIMARYMANDATEID", mandateID);
		paramMap.addValue("ACTIVE", 1);

		try {
			if (namedJdbcTemplate.queryForObject(selectSql.toString(), paramMap, Integer.class) > 0) {
				return true;
			}
		} catch (Exception e) {
			throw e;
		}
		return false;
	}

	private boolean checkSwappedMandate(long mandateID) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();

		StringBuilder selectSql = new StringBuilder("SELECT SWAPISACTIVE  FROM MANDATES");
		selectSql.append(" WHERE MANDATEID = :MANDATEID");
		paramMap.addValue("MANDATEID", mandateID);

		try {
			return namedJdbcTemplate.queryForObject(selectSql.toString(), paramMap, Boolean.class);
		} catch (Exception e) {
			throw e;
		}
	}

	private void loanMandateSwapping(String finReference, long mandateId, String repayMethod) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();

		StringBuilder sql = new StringBuilder("Update FinanceMain");
		sql.append(" Set MandateID =:MandateID ");
		sql.append(" ,FinRepayMethod =:FinRepayMethod");
		sql.append(" Where FinReference =:FinReference");

		source.addValue("MandateID", mandateId);
		source.addValue("FinReference", finReference);
		source.addValue("FinRepayMethod", repayMethod);

		try {
			namedJdbcTemplate.update(sql.toString(), source);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
		}

		logger.debug("updateSql: " + source.toString());

	}

	private void makeSecondaryMandateInActive(long mandateID) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();

		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE MANDATES SET ACTIVE = :ACTIVE WHERE  PRIMARYMANDATEID = :MANDATEID");

		paramMap.addValue("MANDATEID", mandateID);
		paramMap.addValue("ACTIVE", 0);

		try {
			namedJdbcTemplate.update(sql.toString(), paramMap);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
		}
	}

	@Override
	public void processUploadToDownLoadFile(long userId, File file, Media media, DataEngineStatus status)
			throws Exception {
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
		status.setRemarks("initiated Mandate Upload To Download file [ " + name + " ] processing..");
		dataEngine = new DataEngineImport(dataSource, userId, App.DATABASE.name(), true, SysParamUtil.getAppValueDate(),
				status);
		dataEngine.setFile(file);
		dataEngine.setMedia(media);
		dataEngine.setValueDate(SysParamUtil.getAppValueDate());
		dataEngine.setValidateRecord(uploadToDownloadValidationImpl);
		Map<String, Object> parameterMap = new HashMap<>();
		parameterMap.put("APP_DATE", SysParamUtil.getAppDate());
		dataEngine.setParameterMap(parameterMap);
		dataEngine.importData(configName);

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

		setExceptionLog(status);

		MapSqlParameterSource paramMap = null;
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT MANDATEID");
		sql.append(" FROM MANDATEUPLOADTODOWNLOAD");
		sql.append(" WHERE RESP_BATCH_ID = :RESP_BATCH_ID");
		paramMap = new MapSqlParameterSource();
		paramMap.addValue("RESP_BATCH_ID", respBatchId);

		List<Long> mandateIdList = namedJdbcTemplate.queryForList(sql.toString(), paramMap, Long.class);

		if (mandateIdList == null || mandateIdList.isEmpty()) {
			return;
		}
		//Updating the mandate status as 'NEW' for download
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
		logger.debug(Literal.ENTERING);
		try {
			this.namedJdbcTemplate.getJdbcOperations().batchUpdate(
					"update MANDATEUPLOADTODOWNLOAD set status = ?, remarks = ? where mandateId=? and RESP_BATCH_ID=?",
					new BatchPreparedStatementSetter() {
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
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
		logger.debug(Literal.LEAVING);
	}

	public void updateMandateStatus(List<Long> mandateIdList) throws Exception {
		logger.debug(Literal.ENTERING);
		try {
			this.namedJdbcTemplate.getJdbcOperations().batchUpdate(
					"update mandates set status = ? where mandateId=? and status!='NEW'",
					new BatchPreparedStatementSetter() {
						public void setValues(PreparedStatement ps, int i) throws SQLException {
							ps.setString(1, "NEW");
							ps.setLong(2, mandateIdList.get(i));
						}

						public int getBatchSize() {
							return mandateIdList.size();
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.debug(Literal.LEAVING);
	}

	//Setting the exception log data engine status.
	private void setExceptionLog(DataEngineStatus status) {
		List<DataEngineLog> engineLogs = getExceptions(status.getId());
		if (CollectionUtils.isNotEmpty(engineLogs)) {
			status.setDataEngineLogList(engineLogs);
		}
	}

	// Getting the exception log
	public List<DataEngineLog> getExceptions(long batchId) {
		RowMapper<DataEngineLog> rowMapper = null;
		MapSqlParameterSource parameterMap = null;
		StringBuilder sql = null;

		try {
			sql = new StringBuilder("Select * from DATA_ENGINE_LOG where StatusId = :ID");
			parameterMap = new MapSqlParameterSource();
			parameterMap.addValue("ID", batchId);
			rowMapper = BeanPropertyRowMapper.newInstance(DataEngineLog.class);
			return namedJdbcTemplate.query(sql.toString(), parameterMap, rowMapper);
		} catch (Exception e) {
		} finally {
			rowMapper = null;
			sql = null;
		}
		return null;
	}

	protected void addCustomParameter(Map<String, Object> parameterMap) {

	}

	public boolean registerMandate(Mandate mandate) throws Exception {
		return false;
	}

	public void updateMandateStatus() throws Exception {

	}

	public void processMandateResponse() throws Exception {

	}

	public DataEngineConfig getDataEngineConfig() {
		return dataEngineConfig;
	}

	public void setDataEngineConfig(DataEngineConfig dataEngineConfig) {
		this.dataEngineConfig = dataEngineConfig;
	}

	@Autowired
	public void setExternalInterfaceService(ExternalInterfaceService externalInterfaceService) {
		this.externalInterfaceService = externalInterfaceService;
	}
}
