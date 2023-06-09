package com.pennanttech.external.collectionreceipt.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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

import com.ibm.icu.text.SimpleDateFormat;
import com.pennant.app.util.SysParamUtil;
import com.pennanttech.external.app.config.model.FileInterfaceConfig;
import com.pennanttech.external.app.constants.ErrorCodesConstants;
import com.pennanttech.external.app.constants.ExtIntfConfigConstants;
import com.pennanttech.external.app.constants.InterfaceConstants;
import com.pennanttech.external.app.util.ApplicationContextProvider;
import com.pennanttech.external.app.util.FileInterfaceConfigUtil;
import com.pennanttech.external.app.util.FileTransferUtil;
import com.pennanttech.external.app.util.TextFileUtil;
import com.pennanttech.external.collectionreceipt.dao.ExtCollectionReceiptDao;
import com.pennanttech.external.collectionreceipt.model.CollReceiptDetail;
import com.pennanttech.external.collectionreceipt.model.CollReceiptHeader;
import com.pennanttech.external.collectionreceipt.model.ExtCollectionReceiptData;
import com.pennanttech.external.presentment.dao.ExtPresentmentDAO;
import com.pennanttech.pennapps.core.job.AbstractJob;
import com.pennanttech.pennapps.core.resource.Literal;

public class FileWriteCollectionRespJob extends AbstractJob
		implements InterfaceConstants, ErrorCodesConstants, ExtIntfConfigConstants {

	private static final Logger logger = LogManager.getLogger(FileWriteCollectionRespJob.class);

	private static final String FETCH_QUERY = "Select * from COLL_RECEIPT_HEADER  Where WRITE_RESPONSE = ? AND RESP_FILE_STATUS =?";

	private DataSource dataSource;
	private ExtCollectionReceiptDao extCollectionReceiptDao;
	private ExtPresentmentDAO extPresentmentDAO;
	private ApplicationContext applicationContext;

	private CollectionReceiptService collectionReceiptService;

	private static final String REJECTED_RECORDS_HEADER = "REJECTED RECORDS:||||||||||||||||||||||||||||||||||";
	private static final String Reject_RECORDS_FOOTER = "||||||||||||||||||||||||||||||||||";
	private static final String SUCESS_RECORDS_FOOTER = "|||||||||||||||||||||||||||||||||";
	private static final String UNDERLINE_HEADER = "-----------------||||||||||||||||||||||||||||||||||";
	private static final String MAIN_HEADER = "AGREEMENTNO|RECEIPTNO|RECEIPT_CHANNEL|AGENCYID|CHEQUE NO.|DEALINGBANKID|DRAWNON|TOWARDS|RECEIPT AMT|CHEQUEDATE|CITY|RECEIPT DATE|RECEIPT";
	private static final String VALID_RECORDS_HEADER = "VALID RECORDS:||||||||||||||||||||||||||||||||||";

	@Override
	protected void executeJob(JobExecutionContext context) throws JobExecutionException {
		logger.debug(Literal.ENTERING);
		applicationContext = ApplicationContextProvider.getApplicationContext();
		dataSource = applicationContext.getBean("extDataSource", DataSource.class);
		extCollectionReceiptDao = applicationContext.getBean("extCollectionReceiptDao", ExtCollectionReceiptDao.class);
		extPresentmentDAO = applicationContext.getBean(ExtPresentmentDAO.class);
		collectionReceiptService = applicationContext.getBean(CollectionReceiptService.class);

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

					processFileWriting(respConfig, extReceiptHeader);

					processSFTP(reqConfig, respConfig, extReceiptHeader);

					extReceiptHeader.setRespFileStatus(COMPLETED);
					extCollectionReceiptDao.updateExtCollectionRespFileWritingStatus(extReceiptHeader);

				} catch (Exception e) {
					logger.debug(Literal.EXCEPTION, e);
					extReceiptHeader.setRespFileStatus(EXCEPTION);
					extReceiptHeader.setErrorCode(CR1012);
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

	private void processFileWriting(FileInterfaceConfig respConfig, CollReceiptHeader extReceiptHeader) {
		extReceiptHeader.setRespFileStatus(INPROCESS);
		extCollectionReceiptDao.updateExtCollectionRespFileWritingStatus(extReceiptHeader);

		List<CollReceiptDetail> fileRecordsList = extCollectionReceiptDao
				.fetchCollectionRecordsById(extReceiptHeader.getId());
		long fileSeq = extPresentmentDAO.getSeqNumber(SEQ_COLLECTION_RECEIPT);

		Date appDate = SysParamUtil.getAppDate();

		String fileSeqName = StringUtils.leftPad(String.valueOf(fileSeq), 4, "0");

		String filePath = respConfig.getFileLocation();
		String fileName = respConfig.getFilePrepend() + new SimpleDateFormat(respConfig.getDateFormat()).format(appDate)
				+ respConfig.getFilePostpend() + fileSeqName + respConfig.getFileExtension();

		fileName = TextFileUtil.fileName(filePath, fileName);

		extReceiptHeader.setRespFileName(fileName);
		extReceiptHeader.setRespFileLocation(filePath);

		List<CollReceiptDetail> successList = new ArrayList<>();
		List<CollReceiptDetail> failedList = new ArrayList<>();

		for (CollReceiptDetail collectionDetail : fileRecordsList) {
			if (collectionDetail.getReceiptId() > 0) {
				successList.add(collectionDetail);
			} else {
				failedList.add(collectionDetail);
			}
		}

		// failed records preparation
		List<StringBuilder> itemList = new ArrayList<StringBuilder>();

		StringBuilder firstRow = new StringBuilder();
		firstRow.append(REJECTED_RECORDS_HEADER);
		itemList.add(firstRow);

		StringBuilder lineRow = new StringBuilder();
		lineRow.append(UNDERLINE_HEADER);
		itemList.add(lineRow);

		StringBuilder headingRow = new StringBuilder();
		headingRow.append(MAIN_HEADER);
		itemList.add(headingRow);

		int rejectRowNum = 0;
		for (CollReceiptDetail rejectDetail : failedList) {

			String[] dataArray = rejectDetail.getRecordData().toString().split("\\|");
			rejectRowNum = rejectRowNum + 1;
			String qualifiedChk = collectionReceiptService.calculateCheckSum(dataArray, rejectRowNum);

			ExtCollectionReceiptData collectionReceiptData = collectionReceiptService.prepareData(rejectDetail);

			collectionReceiptData.setReceiptID(rejectDetail.getReceiptId());
			collectionReceiptData.setUploadDate(appDate);
			collectionReceiptData.setChecksum(qualifiedChk);

			collectionReceiptData.setErrorCode(rejectDetail.getErrorMessage());
			if (collectionReceiptData.getErrorCode() == null) {
				collectionReceiptData.setErrorCode(extReceiptHeader.getErrorMessage());
			}

			StringBuilder itemStr = prepareLine(collectionReceiptData, rejectRowNum);
			itemList.add(itemStr);
		}

		itemList.add(new StringBuilder(Reject_RECORDS_FOOTER));
		itemList.add(new StringBuilder(Reject_RECORDS_FOOTER));

		// Success records preparation
		StringBuilder validRow = new StringBuilder();
		validRow.append(VALID_RECORDS_HEADER);
		itemList.add(validRow);

		StringBuilder validLineRow = new StringBuilder();
		validLineRow.append(UNDERLINE_HEADER);
		itemList.add(validLineRow);

		headingRow = new StringBuilder();
		headingRow.append(MAIN_HEADER);
		itemList.add(headingRow);

		int successRowNum = 0;
		int totalSChecksum = 0;
		for (CollReceiptDetail successDetail : successList) {

			String[] dataArray = successDetail.getRecordData().toString().split("\\|");
			successRowNum = successRowNum + 1;
			String qualifiedChk = collectionReceiptService.calculateCheckSum(dataArray, successRowNum);

			ExtCollectionReceiptData collectionReceiptData = collectionReceiptService.prepareData(successDetail);

			collectionReceiptData.setReceiptID(successDetail.getReceiptId());
			collectionReceiptData.setUploadDate(appDate);
			collectionReceiptData.setChecksum(qualifiedChk);
			totalSChecksum = totalSChecksum + Integer.parseInt(qualifiedChk);

			StringBuilder itemStr = prepareLine(collectionReceiptData, successRowNum);
			itemList.add(itemStr);

		}

		StringBuilder sucessFooter = new StringBuilder();
		sucessFooter.append(successRowNum);
		appendSeperator(sucessFooter, totalSChecksum);
		sucessFooter.append(SUCESS_RECORDS_FOOTER);
		itemList.add(sucessFooter);

		try {
			// super.writeDataToFile(fileName, itemList);
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
		}
	}

	private void processSFTP(FileInterfaceConfig reqConfig, FileInterfaceConfig respConfig,
			CollReceiptHeader extReceiptHeader) {
		if ("Y".equals(StringUtils.stripToEmpty(respConfig.getFileTransfer()))) {
			try {
				FileTransferUtil fileTransferUtil = new FileTransferUtil(respConfig);
				String localReqFileName = extReceiptHeader.getRequestFileName();

				fileTransferUtil.backupToSFTP(reqConfig.getFileLocation(), localReqFileName);

				String reqFileTB = localReqFileName.substring(0,
						localReqFileName.indexOf(reqConfig.getFileExtension()));
				String finalFile = reqFileTB + ".inproc";
				String deleteInproc = reqConfig.getFileTransferConfig().getSftpLocation().concat("/") + finalFile;

				// Delete .inproc file
				fileTransferUtil.deleteFileFromSFTP(deleteInproc);

			} catch (Exception e) {
				logger.debug(Literal.EXCEPTION, e);
			}
		}
	}

	private StringBuilder prepareLine(ExtCollectionReceiptData detail, int rejectRowNum) {
		StringBuilder item = new StringBuilder();
		append(item, detail.getAgreementNumber());
		appendSeperator(item, "");
		appendSeperator(item, detail.getReceiptChannel());
		appendSeperator(item, detail.getAgencyId());
		appendSeperator(item, detail.getChequeNumber());
		appendSeperator(item, detail.getDealingBankId());
		appendSeperator(item, detail.getDrawnOn());
		appendSeperator(item, detail.getTowards());
		appendSeperator(item, detail.getGrandTotal());
		appendSeperator(item, detail.getChequeDate());
		appendSeperator(item, "");
		appendSeperator(item, detail.getReceiptDate());
		appendSeperator(item, detail.getReceiptType());
		appendSeperator(item, detail.getReceiptNumber());
		appendSeperator(item, detail.getChequeStatus());
		appendSeperator(item, detail.getAutoAlloc());
		appendSeperator(item, detail.getEmiAmount());
		appendSeperator(item, detail.getLppAmount());
		appendSeperator(item, detail.getBccAmount());
		appendSeperator(item, detail.getExcessAmount());
		appendSeperator(item, detail.getOthercharge1());
		appendSeperator(item, detail.getOtherAmt1());
		appendSeperator(item, detail.getOtherCharge2());
		appendSeperator(item, detail.getOtherAmt2());
		appendSeperator(item, detail.getOtherCharge3());
		appendSeperator(item, detail.getOtherAmt3());
		appendSeperator(item, detail.getOtherCharge4());
		appendSeperator(item, detail.getOtherAmt4());
		appendSeperator(item, detail.getRemarks());
		appendSeperator(item, "1000");// System Admin Id
		appendSeperator(item, new SimpleDateFormat("dd-MMM-yy").format(detail.getUploadDate()));
		appendSeperator(item, StringUtils.stripToEmpty(detail.getErrorCode()));// Reason
		appendSeperator(item, "");// Redepositing flag
		appendSeperator(item, rejectRowNum);
		item.append(detail.getChecksum());// Checksum
		return item;
	}

	private void append(StringBuilder item, Object data) {
		item.append(data);
	}

	private void appendSeperator(StringBuilder item, Object data) {
		item.append(pipeSeperator);
		item.append(data);

	}

}
