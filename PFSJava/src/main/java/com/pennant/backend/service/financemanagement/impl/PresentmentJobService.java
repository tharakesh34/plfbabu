package com.pennant.backend.service.financemanagement.impl;

import java.io.File;
import java.util.ArrayList;
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
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.zkoss.util.media.AMedia;
import org.zkoss.util.media.Media;
import org.zkoss.util.resource.Labels;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.financemanagement.PresentmentDetailService;
import com.pennant.backend.util.RepayConstants;
import com.pennant.pff.mandate.InstrumentType;
import com.pennanttech.dataengine.config.DataEngineConfig;
import com.pennanttech.dataengine.model.Configuration;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.dataengine.model.EventProperties;
import com.pennanttech.dataengine.util.DataEngineUtil;
import com.pennanttech.interfacebajaj.fileextract.PresentmentDetailExtract;
import com.pennanttech.model.presentment.Presentment;
import com.pennanttech.pennapps.core.ftp.FtpClient;
import com.pennanttech.pennapps.core.ftp.SftpClient;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.external.AbstractInterface;
import com.pennanttech.pff.presentment.model.PresentmentHeader;

public class PresentmentJobService extends AbstractInterface {
	protected final Logger logger = LogManager.getLogger(getClass());

	public static final String STATUS_SUBMIT = "Submit";
	public static final String STATUS_APPROVE = "Approve";

	private PresentmentDetailService presentmentDetailService;
	private DataEngineConfig dataEngineConfig;
	private static List<Configuration> PRESENTMENT_CONFIG = new ArrayList<>();
	private static Map<Long, Map<String, EventProperties>> eventProperties = new HashMap<>();

	public void extractPresentment(PresentmentHeader header) {
		Date fromDate = header.getFromDate();
		Date toDate = header.getToDate();
		LoggedInUser loggedInUser = new LoggedInUser();

		logger.info("Presentment Extraction Process Started...");

		List<PresentmentHeader> headerList = null;
		try {

			presentmentDetailService.savePresentmentDetails(header);

			headerList = presentmentDetailService.getPresenmentHeaderList(fromDate, toDate,
					RepayConstants.PEXC_EXTRACT);
			logger.debug("No of Records Extracted : {}", headerList.size());

			for (PresentmentHeader ph : headerList) {
				long id = ph.getId();

				List<Long> includeList = presentmentDetailService.getIncludeList(id);
				ph.setIncludeList(includeList);

				logger.info("No of Records in Include List  : {}", includeList.size());

				boolean searchIncludeList = presentmentDetailService.searchIncludeList(id, 0);

				if (!searchIncludeList) {
					logger.debug("No Records are there to Create Presentment Batch : {}", includeList.size());
					continue;
				}

				List<Long> excludeList = presentmentDetailService.getExcludeList(id);
				ph.setExcludeList(excludeList);

				logger.debug("No of Records in Exclude List  : {}", excludeList.size());

				if (StringUtils.isEmpty(ph.getPartnerAcctNumber())
						&& (ph.getPartnerBankId() == null || ph.getPartnerBankId() <= 0)) {
					Presentment pb = getPartnerBankId(ph.getLoanType(), ph.getMandateType());
					ph.setPartnerAcctNumber(pb.getAccountNo());
					ph.setPartnerBankId(pb.getPartnerBankId());
				} else {
					ph.setPartnerAcctNumber(ph.getPartnerAcctNumber());
					ph.setPartnerBankId(ph.getPartnerBankId());
				}

				logger.debug("Presentment Batch Creation Process Started...");

				ph.setUserDetails(loggedInUser);

				ph.setUserAction(STATUS_SUBMIT);
				presentmentDetailService.updatePresentmentDetails(ph);

				logger.debug("No of Presentment Records Created  : {}", includeList.size());

				logger.debug("Presentment Batch Approval Process...... ");

				ph.setUserAction(STATUS_APPROVE);
				presentmentDetailService.updatePresentmentDetails(ph);
				logger.debug("No of Presentment Records Approved  : {}", includeList.size());
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.info("Presentment Extraction Process compled.");
	}

	public void uploadPresentment(String jobName) {
		logger.debug(Literal.ENTERING);
		try {
			loadConfig();
			for (Configuration configuration : PRESENTMENT_CONFIG) {

				if (!jobName.equals(configuration.getName())) {
					continue;
				}
				DataEngineStatus status = new DataEngineStatus();
				status.setName(configuration.getName());
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
					getListOfFilesFromFTP(sharedSFTPProperty, "SFTP", configuration, localLocation, status);
				} else if (sharedFTPProperty != null) {
					getListOfFilesFromFTP(sharedFTPProperty, "FTP", configuration, localLocation, status);
				} else if (sharedNetworkFolderProperty != null) {
					// FIXME
				}
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
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

	private List<File> getListOfFilesFromFTP(EventProperties eventProperty, String protocol, Configuration config,
			String localLocation, DataEngineStatus status) {
		logger.debug(Literal.ENTERING);
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
				ftpClient = new SftpClient(hostName, Integer.parseInt(port), accessKey, secretKey);
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
					String instrumentType = getInstrumentType(file.getName());
					Media aMedia = new AMedia(file.getName(), "xlsx", null, data);
					if (config.getName().endsWith(instrumentType)) {
						upload(aMedia, instrumentType, status);

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
					}
					new SftpClient(hostName, Integer.parseInt(port), accessKey, secretKey)
							.deleteFile(bucketName.concat("/").concat(fileName));
					logger.info("{} file processed successfully..", fileName);
				} else {
					logger.info("{} file name does not exists", fileName);
				}
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		} finally {
			// FIXME:: Gopal.p
			/*
			 * if (ftpClient != null) { ftpClient.disconnect(); }
			 */
		}
		return null;
	}

	private String getInstrumentType(String name) {
		if (InstrumentType.isNACH(name)) {
			return InstrumentType.NACH.name();
		} else if (InstrumentType.isPDC(name)) {
			return InstrumentType.PDC.name();
		}

		return null;
	}

	private EventProperties getPostEvent(Map<String, EventProperties> properties, String postEvent) {
		return properties.get(postEvent);
	}

	@SuppressWarnings("rawtypes")
	public List<String> getFileNameList(String pathname, String hostName, int port, String accessKey,
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
			logger.error(Literal.EXCEPTION, e1);
		}

		LsEntry entry = null;
		List<String> fileName = new ArrayList<String>();
		Vector filelist = null;
		try {
			filelist = ((ChannelSftp) channel).ls(pathname);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
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

	private void loadConfig() {
		if (CollectionUtils.isEmpty(PRESENTMENT_CONFIG)) {
			try {
				for (Configuration config : dataEngineConfig.getConfigurationList()) {
					if (config.getName().equals("PRESENTMENT_RESPONSE_NACH")
							|| config.getName().equals("PRESENTMENT_RESPONSE_PDC")) {
						PRESENTMENT_CONFIG.add(config);
					}
				}
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			}
		}
	}

	private void upload(Media aMedia, String instrumentType, DataEngineStatus status) {
		logger.debug(Literal.ENTERING);

		PresentmentDetailExtract detailExtract = new PresentmentDetailExtract(dataSource);
		detailExtract.setInstrumentType(instrumentType);
		detailExtract.setUserDetails(new LoggedInUser());
		detailExtract.setStatus(status);
		presentmentDetailService.setProperties(detailExtract);
		detailExtract.setMediaOnly(aMedia);
		Thread thread = new Thread(detailExtract);
		thread.start();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
	}

	public List<FinanceType> getFinanceTypeList(String entityCode) {
		logger.debug(Literal.ENTERING);
		StringBuilder sql = null;
		try {
			sql = new StringBuilder("Select rmt.fintype from rmtfinancetypes rmt ");
			sql.append("inner join smtdivisiondetail smt on smt.divisionCode = rmt.findivision ");
			sql.append("where smt.entitycode = :entityCode and rmt.finisactive = :active");

			MapSqlParameterSource source = new MapSqlParameterSource();
			source.addValue("entityCode", entityCode);
			source.addValue("active", 1);
			RowMapper<FinanceType> typeRowMapper = BeanPropertyRowMapper.newInstance(FinanceType.class);
			return namedJdbcTemplate.query(sql.toString(), source, typeRowMapper);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return null;
	}

	public List<String> getEntityCodes() {
		String sql = "Select EntityCode From SMTDivisionDetail";

		logger.debug(Literal.SQL + sql);
		return jdbcOperations.queryForList(sql, String.class);
	}

	public Presentment getPartnerBankId(String finType, String mandateType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" PartnerBankID, AccountNo");
		sql.append(" From PresentmentPartnerBank");
		sql.append(" Where FinType = ? and MandateType = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return jdbcOperations.queryForObject(sql.toString(), (rs, i) -> {
				Presentment p = new Presentment();
				p.setPartnerBankId(rs.getLong("PartnerBankID"));
				p.setAccountNo(rs.getString("AccountNo"));
				return p;
			}, finType, mandateType);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

	public PresentmentDetailService getPresentmentDetailService() {
		return presentmentDetailService;
	}

	public void setPresentmentDetailService(PresentmentDetailService presentmentDetailService) {
		this.presentmentDetailService = presentmentDetailService;
	}

	public DataEngineConfig getDataEngineConfig() {
		return dataEngineConfig;
	}

	@Autowired
	public void setDataEngineConfig(DataEngineConfig dataEngineConfig) {
		this.dataEngineConfig = dataEngineConfig;
	}
}
