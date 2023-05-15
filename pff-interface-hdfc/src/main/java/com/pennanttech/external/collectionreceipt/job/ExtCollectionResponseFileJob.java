package com.pennanttech.external.collectionreceipt.job;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

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
import com.pennanttech.external.collectionreceipt.dao.ExtCollectionReceiptDao;
import com.pennanttech.external.collectionreceipt.model.CollReceiptHeader;
import com.pennanttech.external.collectionreceipt.service.ExtCollectionFileService;
import com.pennanttech.external.config.ApplicationContextProvider;
import com.pennanttech.external.config.ExtErrorCodes;
import com.pennanttech.external.config.ExternalConfig;
import com.pennanttech.external.config.InterfaceErrorCode;
import com.pennanttech.external.constants.InterfaceConstants;
import com.pennanttech.external.dao.ExtInterfaceDao;
import com.pennanttech.external.presentment.dao.ExtPresentmentDAO;
import com.pennanttech.pennapps.core.job.AbstractJob;
import com.pennanttech.pennapps.core.resource.Literal;

public class ExtCollectionResponseFileJob extends AbstractJob implements InterfaceConstants {

	private static final Logger logger = LogManager.getLogger(ExtCollectionResponseFileJob.class);

	private static final String FETCH_QUERY = "Select * from COLL_RECEIPT_HEADER  Where WRITE_RESPONSE = ? AND RESP_FILE_WRITTEN =?";

	private DataSource dataSource;
	private ExtCollectionReceiptDao extCollectionReceiptDao;
	private ExtInterfaceDao extInterfaceDao;
	private ExtPresentmentDAO extPresentmentDAO;
	private ApplicationContext applicationContext;

	private ExtCollectionFileService collectionFileService;

	@Override
	protected void executeJob(JobExecutionContext context) throws JobExecutionException {
		logger.debug(Literal.ENTERING);
		applicationContext = ApplicationContextProvider.getApplicationContext();
		dataSource = applicationContext.getBean("extDataSource", DataSource.class);
		extInterfaceDao = applicationContext.getBean(ExtInterfaceDao.class);
		extCollectionReceiptDao = applicationContext.getBean("extCollectionReceiptDao", ExtCollectionReceiptDao.class);
		collectionFileService = applicationContext.getBean(ExtCollectionFileService.class);
		extPresentmentDAO = applicationContext.getBean(ExtPresentmentDAO.class);

		Date appDate = SysParamUtil.getAppDate();

		// get error codes handy
		if (ExtErrorCodes.getInstance().getInterfaceErrorsList().isEmpty()) {
			List<InterfaceErrorCode> interfaceErrorsList = extInterfaceDao.fetchInterfaceErrorCodes();
			ExtErrorCodes.getInstance().setInterfaceErrorsList(interfaceErrorsList);
		}

		// Fetch External configuration once for all the interfaces types
		List<ExternalConfig> configList = extInterfaceDao.getExternalConfig();

		JdbcCursorItemReader<CollReceiptHeader> cursorItemReader = new JdbcCursorItemReader<CollReceiptHeader>();
		cursorItemReader.setDataSource(dataSource);
		cursorItemReader.setFetchSize(1);
		cursorItemReader.setSql(FETCH_QUERY);
		cursorItemReader.setRowMapper(new RowMapper<CollReceiptHeader>() {
			@Override
			public CollReceiptHeader mapRow(ResultSet rs, int rowNum) throws SQLException {
				CollReceiptHeader collectionFile = new CollReceiptHeader();
				collectionFile.setId(rs.getLong("ID"));
				collectionFile.setRequestFileName("REQUEST_FILE_NAME");
				return collectionFile;
			}
		});

		cursorItemReader.setPreparedStatementSetter(new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setLong(1, 1);// 0-false,1-true-ready to write file
				ps.setLong(1, UNPROCESSED);
			}
		});

		ExecutionContext executionContext = new ExecutionContext();
		cursorItemReader.open(executionContext);

		DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
		txDef.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_REQUIRES_NEW);

		// CollReceiptHeader receiptHeader;
		// ExternalConfig reqConfig = getDataFromList(configList, CONFIG_COLLECTION_REQ_CONF);
		// ExternalConfig respConfig = getDataFromList(configList, CONFIG_COLLECTION_RESP_CONF);
		// try {
		// while ((receiptHeader = cursorItemReader.read()) != null) {
		// try {
		//
		// receiptHeader.setRespFileStatus(INPROCESS);
		// extCollectionReceiptDao.updateExtCollectionRespFileWritingStatus(receiptHeader);
		//
		// List<CollReceiptDetail> fileRecordsList = extCollectionReceiptDao
		// .fetchCollectionRecordsById(receiptHeader.getId());
		//
		// if (fileRecordsList.size() > 0) {
		//
		// long fileSeq = extPresentmentDAO.getSeqNumber(SEQ_COLLECTION_RECEIPT);
		// String fileSeqName = StringUtils.leftPad(String.valueOf(fileSeq), 4, "0");
		//
		// String filePath = respConfig.getFileLocation();
		// String fileName = respConfig.getFilePrepend()
		// + new SimpleDateFormat(respConfig.getDateFormat()).format(appDate)
		// + respConfig.getFilePostpend() + fileSeqName + respConfig.getFileExtension();
		//
		// String fileData = App.getResourcePath(filePath) + File.separator + fileName;
		//
		// receiptHeader.setRespFileName(fileName);
		// receiptHeader.setRespFileLocation(filePath);
		//
		// collectionFileService.processCollectionResponseFileWriting(fileData, appDate, fileRecordsList,
		// respConfig);
		// }
		//
		// if ("Y".equals(StringUtils.stripToEmpty(respConfig.getIsSftp()))) {
		// try {
		// ExtSFTPUtil extSFTPUtil = new ExtSFTPUtil(respConfig);
		// String fileNamee = receiptHeader.getRequestFileName();
		// fileNamee = fileNamee.substring(0, fileNamee.indexOf(reqConfig.getFileExtension()));
		// File file = new File(
		// respConfig.getFileSftpLocation() + File.separator + fileNamee + ".inproc");
		//
		// // Delete .inproc file
		// extSFTPUtil.deleteFile(file.getAbsolutePath());
		//
		// // Backup now local file to SFTP backup location
		// File localFile = new File(respConfig.getFileLocation() + File.separator + fileNamee);
		// FtpClient ftpClient = extSFTPUtil.getSFTPConnection();
		// ftpClient.upload(localFile, respConfig.getFileBackupLocation());
		//
		// } catch (Exception e) {
		// logger.debug(Literal.EXCEPTION, e);
		// }
		// }
		//
		// receiptHeader.setRespFileStatus(COMPLETED);
		// extCollectionReceiptDao.updateExtCollectionRespFileWritingStatus(receiptHeader);
		//
		// } catch (Exception e) {
		// logger.debug(Literal.EXCEPTION, e);
		// receiptHeader.setRespFileStatus(EXCEPTION);
		// receiptHeader.setErrorCode(F400);
		// receiptHeader.setErrorMessage(e.getMessage());
		// extCollectionReceiptDao.updateExtCollectionRespFileWritingStatus(receiptHeader);
		// }
		// }
		// } catch (Exception e) {
		// logger.debug(Literal.EXCEPTION, e);
		//
		// } finally {
		// if (cursorItemReader != null) {
		// cursorItemReader.close();
		// }
		// }

		logger.debug(Literal.LEAVING);
	}

}
