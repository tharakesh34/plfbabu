package com.pennanttech.external.collectionreceipt.service;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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

import com.pennanttech.external.app.constants.ErrorCodesConstants;
import com.pennanttech.external.app.constants.InterfaceConstants;
import com.pennanttech.external.app.util.ApplicationContextProvider;
import com.pennanttech.external.app.util.TextFileUtil;
import com.pennanttech.external.collectionreceipt.dao.ExtCollectionReceiptDao;
import com.pennanttech.external.collectionreceipt.model.CollReceiptDetail;
import com.pennanttech.external.collectionreceipt.model.CollReceiptHeader;
import com.pennanttech.pennapps.core.job.AbstractJob;
import com.pennanttech.pennapps.core.resource.Literal;

public class FileExtractCollectionReqJob extends AbstractJob implements InterfaceConstants, ErrorCodesConstants {

	private static final Logger logger = LogManager.getLogger(FileExtractCollectionReqJob.class);

	private static final String ROW1 = "'H'";
	private static final String ROW2 = "H";
	private static final String HEADER = "Agr";
	private static final String FETCH_QUERY = "Select * from COLL_RECEIPT_HEADER  Where STATUS=? AND EXTRACTION=?";

	/**
	 *
	 */
	@Override
	protected void executeJob(JobExecutionContext context) throws JobExecutionException {
		logger.debug(Literal.ENTERING);

		ApplicationContext applicationContext = ApplicationContextProvider.getApplicationContext();
		DataSource dataSource = applicationContext.getBean("extDataSource", DataSource.class);
		ExtCollectionReceiptDao extCollectionReceiptDao = applicationContext.getBean("extCollectionReceiptDao",
				ExtCollectionReceiptDao.class);
		CollectionReceiptService collectionReceiptService = applicationContext.getBean(CollectionReceiptService.class);

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
				collectionFile.setRequestFileLocation(rs.getString("REQ_FILE_LOCATION"));
				return collectionFile;
			}
		});

		cursorItemReader.setPreparedStatementSetter(new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setLong(1, UNPROCESSED);
				ps.setLong(2, UNPROCESSED);
			}
		});

		ExecutionContext executionContext = new ExecutionContext();
		cursorItemReader.open(executionContext);

		DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
		txDef.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_REQUIRES_NEW);

		CollReceiptHeader extReceiptHeader;

		try {
			while ((extReceiptHeader = cursorItemReader.read()) != null) {

				// update the file status as processing
				extReceiptHeader.setExtraction(INPROCESS);
				extCollectionReceiptDao.updateFileExtraction(extReceiptHeader);

				String requestFileLocation = extReceiptHeader.getRequestFileLocation();
				String requestFileName = extReceiptHeader.getRequestFileName();
				String fileData = TextFileUtil.fileName(requestFileLocation, requestFileName);
				File file = new File(fileData);
				// Fetch list of record data from the file

				try (Scanner sc = new Scanner(file)) {

					List<CollReceiptDetail> extCollectionLineList = new ArrayList<CollReceiptDetail>();
					int rowNumber = 0;
					while (sc.hasNextLine()) {

						String lineData = sc.nextLine();
						rowNumber = rowNumber + 1;
						if (extReceiptHeader.isValid()) {

							if (rowNumber == 1) {

								if (!lineData.trim().startsWith(ROW1)) {
									extReceiptHeader.setErrorCode(CR1006);
									continue;
								}

								if (!validateRow1(lineData, "Filename")) {
									extReceiptHeader.setErrorCode(CR1006);
									continue;
								}

							}

							if (rowNumber == 2) {

								if (!lineData.trim().startsWith(ROW2)) {
									extReceiptHeader.setErrorCode(CR1007);
									continue;
								}

								if (!validateRow2(lineData, requestFileName)) {
									extReceiptHeader.setErrorCode(CR1007);
									continue;
								}

							}

							if (rowNumber == 3 && !lineData.trim().startsWith(HEADER)) {
								extReceiptHeader.setErrorCode(CR1008);
							}
							if (rowNumber > 3) {
								CollReceiptDetail crd = new CollReceiptDetail();
								crd.setHeaderId(extReceiptHeader.getId());
								crd.setRecordData(lineData);
								crd.setReceiptId(0);
								extCollectionLineList.add(crd);
							}
						}

					}

					if (!extCollectionLineList.isEmpty()) {
						String sharedTotChecksum = extCollectionLineList.get(extCollectionLineList.size() - 1)
								.getRecordData();
						String[] row1Str = sharedTotChecksum.split("\\|", -1);
						if (row1Str != null && row1Str.length > 2) {
							sharedTotChecksum = row1Str[1];
						}
						extCollectionLineList.remove(extCollectionLineList.size() - 1);

						int gTotalChk = 0;
						for (CollReceiptDetail lineData : extCollectionLineList) {

							String[] dataArray = lineData.getRecordData().split("\\|");
							String checksum = TextFileUtil.getItem(dataArray, 32);
							long rowNum = TextFileUtil.getLongItem(dataArray, 31);

							String qualifiedChk = collectionReceiptService.calculateCheckSum(dataArray, rowNum);

							if (!qualifiedChk.equals(checksum)) {
								extReceiptHeader.setErrorCode(CR1009);
							}

							gTotalChk = gTotalChk + Integer.parseInt(qualifiedChk);

						}

						if (!sharedTotChecksum.equals((extCollectionLineList.size()) + "" + gTotalChk)) {
							extReceiptHeader.setErrorCode(CR1013);
						}

						extCollectionReceiptDao.saveFileExtractionList(extCollectionLineList, extReceiptHeader.getId());
						extReceiptHeader.setExtraction(COMPLETED);
					}

					if (!extReceiptHeader.isValid()) {
						extReceiptHeader.setWriteResponse(ENABLED);
						extReceiptHeader.setStatus(FAILED);
					} else {
						extReceiptHeader.setStatus(COMPLETED);
					}
					extCollectionReceiptDao.updateFileExtraction(extReceiptHeader);

				} catch (Exception e) {
					logger.debug(Literal.EXCEPTION, e);
					extReceiptHeader.setStatus(FAILED);
					extReceiptHeader.setExtraction(UNPROCESSED);
					extReceiptHeader.setErrorCode(CR1010);
					extReceiptHeader.setErrorMessage(e.getMessage());
					extCollectionReceiptDao.updateFileExtraction(extReceiptHeader);
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

	private boolean validateRow2(String lineData, String fileName) {
		logger.debug(Literal.ENTERING);
		String[] row1Str = lineData.split("\\|", -1);
		if (row1Str.length == 32) {
			if (row1Str[1].equals(fileName)) {
				return true;
			}
		}
		logger.debug(Literal.LEAVING);
		return false;
	}

	private boolean validateRow1(String lineData, String fileName) {
		logger.debug(Literal.ENTERING);
		String[] row1Str = lineData.split("\\|", -1);
		if (row1Str.length == 32) {
			if (row1Str[1].equals(fileName)) {
				return true;
			}
		}
		logger.debug(Literal.LEAVING);
		return false;
	}

}
