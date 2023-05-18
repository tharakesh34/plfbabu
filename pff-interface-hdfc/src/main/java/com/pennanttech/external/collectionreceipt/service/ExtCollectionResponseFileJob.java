package com.pennanttech.external.collectionreceipt.service;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.pennant.app.util.SysParamUtil;
import com.pennanttech.external.app.config.model.FileInterfaceConfig;
import com.pennanttech.external.app.constants.ErrorCodesConstants;
import com.pennanttech.external.app.constants.InterfaceConstants;
import com.pennanttech.external.app.util.ApplicationContextProvider;
import com.pennanttech.external.app.util.ExtSFTPUtil;
import com.pennanttech.external.app.util.FileInterfaceConfigUtil;
import com.pennanttech.external.collectionreceipt.dao.ExtCollectionReceiptDao;
import com.pennanttech.external.collectionreceipt.model.CollReceiptDetail;
import com.pennanttech.external.collectionreceipt.model.CollReceiptHeader;
import com.pennanttech.external.presentment.dao.ExtPresentmentDAO;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.ftp.FtpClient;
import com.pennanttech.pennapps.core.job.AbstractJob;
import com.pennanttech.pennapps.core.resource.Literal;

public class ExtCollectionResponseFileJob extends AbstractJob implements InterfaceConstants, ErrorCodesConstants {

	private static final Logger logger = LogManager.getLogger(ExtCollectionResponseFileJob.class);

	private static final String FETCH_QUERY = "Select * from COLL_RECEIPT_HEADER  Where WRITE_RESPONSE = ? AND RESP_FILE_STATUS =?";

	private DataSource dataSource;
	private ExtCollectionReceiptDao extCollectionReceiptDao;
	private ExtPresentmentDAO extPresentmentDAO;
	private ApplicationContext applicationContext;

	private ExtCollectionFileService collectionFileService;

	@Override
	protected void executeJob(JobExecutionContext context) throws JobExecutionException {
		logger.debug(Literal.ENTERING);
		applicationContext = ApplicationContextProvider.getApplicationContext();
		dataSource = applicationContext.getBean("extDataSource", DataSource.class);
		extCollectionReceiptDao = applicationContext.getBean("extCollectionReceiptDao", ExtCollectionReceiptDao.class);
		collectionFileService = applicationContext.getBean(ExtCollectionFileService.class);
		extPresentmentDAO = applicationContext.getBean(ExtPresentmentDAO.class);

		Date appDate = SysParamUtil.getAppDate();

		FileInterfaceConfig reqConfig = FileInterfaceConfigUtil.getFIConfig(CONFIG_COLLECTION_REQ_CONF);
		FileInterfaceConfig respConfig = FileInterfaceConfigUtil.getFIConfig(CONFIG_COLLECTION_RESP_CONF);

		// Fetch 10 files using extraction status = 0
		JdbcCursorItemReader<CollReceiptHeader> cursorItemReader = new JdbcCursorItemReader<CollReceiptHeader>();
		cursorItemReader.setDataSource(dataSource);
		cursorItemReader.setFetchSize(1);
		cursorItemReader.setSql(FETCH_QUERY);
		cursorItemReader.setRowMapper(new RowMapper<CollReceiptHeader>() {
			@Override
			public CollReceiptHeader mapRow(ResultSet rs, int rowNum) throws SQLException {
				CollReceiptHeader collectionFile = new CollReceiptHeader();
				collectionFile.setId(rs.getLong("ID"));
				collectionFile.setRequestFileName(rs.getString("REQ_FILE_NAME"));
				return collectionFile;
			}
		});

		cursorItemReader.setPreparedStatementSetter(new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setInt(1, 1);
				ps.setInt(2, UNPROCESSED);
			}
		});

		ExecutionContext executionContext = new ExecutionContext();
		cursorItemReader.open(executionContext);

		DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
		txDef.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_REQUIRES_NEW);

		CollReceiptHeader extReceiptHeader;

		try {
			while ((extReceiptHeader = cursorItemReader.read()) != null) {
				try {

					extReceiptHeader.setRespFileStatus(INPROCESS);
					extCollectionReceiptDao.updateExtCollectionRespFileWritingStatus(extReceiptHeader);

					List<CollReceiptDetail> fileRecordsList = extCollectionReceiptDao
							.fetchCollectionRecordsById(extReceiptHeader.getId());
					CollReceiptHeader errorReceiptHeader = extCollectionReceiptDao
							.getErrorFromHeader(extReceiptHeader.getId());

					if (fileRecordsList.size() > 0) {

						long fileSeq = extPresentmentDAO.getSeqNumber(SEQ_COLLECTION_RECEIPT);
						String fileSeqName = StringUtils.leftPad(String.valueOf(fileSeq), 4, "0");

						String filePath = respConfig.getFileLocation();
						String fileName = respConfig.getFilePrepend()
								+ new SimpleDateFormat(respConfig.getDateFormat()).format(appDate)
								+ respConfig.getFilePostpend() + fileSeqName + respConfig.getFileExtension();

						String fileData = App.getResourcePath(filePath) + File.separator + fileName;

						extReceiptHeader.setRespFileName(fileName);
						extReceiptHeader.setRespFileLocation(filePath);

						collectionFileService.processCollectionResponseFileWriting(fileData, appDate, fileRecordsList,
								errorReceiptHeader, respConfig);
					}

					if ("Y".equals(StringUtils.stripToEmpty(respConfig.getIsSftp()))) {
						try {
							ExtSFTPUtil extSFTPUtil = new ExtSFTPUtil(respConfig);
							String respFileTB = extReceiptHeader.getRequestFileName();

							// Backup now local file to SFTP backup location
							File localFile = new File(
									App.getResourcePath(reqConfig.getFileLocation()) + File.separator + respFileTB);
							FtpClient ftpClient = extSFTPUtil.getSFTPConnection();
							ftpClient.upload(localFile, reqConfig.getFileBackupLocation());

							respFileTB = respFileTB.substring(0, respFileTB.indexOf(reqConfig.getFileExtension()));
							String remReqPath = reqConfig.getFileSftpLocation();
							String deleteInproc = remReqPath.concat("/") + respFileTB + ".inproc";
							File file = new File(deleteInproc);

							// Delete .inproc file
							extSFTPUtil.deleteFile(file.getAbsolutePath());

						} catch (Exception e) {
							logger.debug(Literal.EXCEPTION, e);
						}
					}

					extReceiptHeader.setRespFileStatus(COMPLETED);
					extCollectionReceiptDao.updateExtCollectionRespFileWritingStatus(extReceiptHeader);

				} catch (Exception e) {
					logger.debug(Literal.EXCEPTION, e);
					extReceiptHeader.setRespFileStatus(EXCEPTION);
					extReceiptHeader.setErrorCode(F400);
					extReceiptHeader.setErrorMessage(e.getMessage());
					extCollectionReceiptDao.updateExtCollectionRespFileWritingStatus(extReceiptHeader);
				}
			}
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);

		} finally {
			if (cursorItemReader != null) {
				cursorItemReader.close();
			}
		}

		logger.debug(Literal.LEAVING);
	}

}
