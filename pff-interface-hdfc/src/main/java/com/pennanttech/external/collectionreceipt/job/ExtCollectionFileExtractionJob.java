package com.pennanttech.external.collectionreceipt.job;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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

import com.pennanttech.external.app.config.dao.ExtGenericDao;
import com.pennanttech.external.app.config.model.InterfaceErrorCode;
import com.pennanttech.external.app.constants.InterfaceConstants;
import com.pennanttech.external.app.util.ApplicationContextProvider;
import com.pennanttech.external.app.util.InterfaceErrorCodeUtil;
import com.pennanttech.external.collectionreceipt.dao.ExtCollectionReceiptDao;
import com.pennanttech.external.collectionreceipt.model.CollReceiptDetail;
import com.pennanttech.external.collectionreceipt.model.CollReceiptHeader;
import com.pennanttech.external.collectionreceipt.model.ExtCollectionReceiptData;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.job.AbstractJob;
import com.pennanttech.pennapps.core.resource.Literal;

public class ExtCollectionFileExtractionJob extends AbstractJob implements InterfaceConstants {

	private static final Logger logger = LogManager.getLogger(ExtCollectionFileExtractionJob.class);

	private static final String ROW1 = "'H'";
	private static final String ROW2 = "H";
	private static final String HEADER = "Agr";
	private static final String FETCH_QUERY = "Select * from COLL_RECEIPT_HEADER  Where STATUS=? AND EXTRACTION=?";

	private DataSource dataSource;
	private ExtCollectionReceiptDao extCollectionReceiptDao;
	private ExtGenericDao extGenericDao;
	private ApplicationContext applicationContext;

	/**
	 *
	 */
	@Override
	protected void executeJob(JobExecutionContext context) throws JobExecutionException {
		logger.debug(Literal.ENTERING);

		applicationContext = ApplicationContextProvider.getApplicationContext();
		dataSource = applicationContext.getBean("extDataSource", DataSource.class);
		extCollectionReceiptDao = applicationContext.getBean("extCollectionReceiptDao", ExtCollectionReceiptDao.class);
		extGenericDao = applicationContext.getBean(ExtGenericDao.class);

		// get error codes handy
		if (InterfaceErrorCodeUtil.getInstance().getInterfaceErrorsList().isEmpty()) {
			List<InterfaceErrorCode> interfaceErrorsList = extGenericDao.fetchInterfaceErrorCodes();
			InterfaceErrorCodeUtil.getInstance().setInterfaceErrorsList(interfaceErrorsList);
		}

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

				try {

					Scanner sc = null;
					// try {
					// update the file status as processing
					extReceiptHeader.setExtraction(INPROCESS);
					extCollectionReceiptDao.updateFileExtraction(extReceiptHeader);

					String fileData = App.getResourcePath(extReceiptHeader.getRequestFileLocation()) + File.separator
							+ extReceiptHeader.getRequestFileName();

					File file = new File(fileData);
					// Fetch list of record data from the file

					sc = new Scanner(file);

					boolean dataValid = true;

					String errorCode = "";

					List<CollReceiptDetail> extCollectionLineList = new ArrayList<CollReceiptDetail>();

					int rowNumber = 0;
					while (sc.hasNextLine()) {
						rowNumber = rowNumber + 1;

						String lineData = sc.nextLine();

						if (rowNumber == 1 && !lineData.trim().startsWith(ROW1)) {
							dataValid = false;
							errorCode = F402;
						}

						if (dataValid && rowNumber == 1 && lineData.trim().startsWith(ROW1)) {

							if (!validateRow1(lineData, "Filename")) {
								dataValid = false;
								errorCode = F402;
							}

						}

						if (dataValid && rowNumber == 2 && !lineData.trim().startsWith(ROW2)) {
							dataValid = false;
							errorCode = F403;

						}

						if (dataValid && rowNumber == 2 && lineData.trim().startsWith(ROW2)) {

							if (!validateRow2(lineData, extReceiptHeader.getRequestFileName())) {
								dataValid = false;
								errorCode = F403;
							}

						}

						if (dataValid && rowNumber == 3 && !lineData.trim().startsWith(HEADER)) {
							dataValid = false;
							errorCode = F404;

						}

						if (rowNumber > 3) {
							CollReceiptDetail crd = new CollReceiptDetail();
							crd.setHeaderId(extReceiptHeader.getId());
							crd.setRecordData(lineData);
							crd.setReceiptId(0);
							extCollectionLineList.add(crd);
						} else {
							// set header status as error
							extReceiptHeader.setErrorCode(errorCode);
							extReceiptHeader.setErrorMessage(getErrorMessage(errorCode));
						}
					}

					sc.close();

					// Start the transaction
					if (dataValid && extCollectionLineList.size() > 0) {

						// total check sum
						int gTotalChk = 0;
						String mainChksum = extCollectionLineList.get(extCollectionLineList.size() - 1).getRecordData();
						extCollectionLineList.remove(extCollectionLineList.size() - 1);

						for (CollReceiptDetail lineData : extCollectionLineList) {

							ExtCollectionReceiptData collectionData = splitAndSetData(lineData.getRecordData());
							int agreementCHK = generateChecksum(String.valueOf(collectionData.getAgreementNumber()));
							int grTotalCHK = generateChecksum(String.valueOf(collectionData.getGrandTotal()));
							int chqDateCHK = generateChecksum(String.valueOf(collectionData.getChequeDate()));
							int receiptDateCHK = generateChecksum(String.valueOf(collectionData.getReceiptDate()));
							int chqTypeCHK = generateChecksum(String.valueOf(collectionData.getReceiptType()));

							int totalChk = agreementCHK + grTotalCHK + chqDateCHK + receiptDateCHK + chqTypeCHK;

							String qualifiedChk = collectionData.getRowNum() + "" + totalChk;

							gTotalChk = gTotalChk + Integer.parseInt(collectionData.getRowNum() + "" + totalChk);

							if (!qualifiedChk.equals(collectionData.getChecksum())) {
								dataValid = false;
								errorCode = F401;
								break;
							}
						}

						if (dataValid && !mainChksum.equals(String.valueOf(gTotalChk))) {
							dataValid = false;
						}

						// Validate checksum. Checksum is invalid, so updating error to all the beans.
						if (dataValid) {
							saveDetails(extCollectionLineList, extReceiptHeader.getId());
							extReceiptHeader.setStatus(COMPLETED);
							extReceiptHeader.setWriteResponse(DISABLED);
							extReceiptHeader.setExtraction(COMPLETED);
						} else {
							// set header status as error
							extReceiptHeader.setErrorCode(F401);
							extReceiptHeader.setExtraction(COMPLETED);
							extReceiptHeader.setErrorMessage(getErrorMessage(F401));
						}

					}

					if (!dataValid) {
						extCollectionLineList.remove(extCollectionLineList.size() - 1);
						saveDetails(extCollectionLineList, extReceiptHeader.getId());
						extReceiptHeader.setStatus(FAILED);
						extReceiptHeader.setWriteResponse(ENABLED);
						extReceiptHeader.setExtraction(COMPLETED);
					}

					extCollectionReceiptDao.updateFileExtraction(extReceiptHeader);

				} catch (Exception e) {
					extReceiptHeader.setStatus(FAILED);
					extReceiptHeader.setWriteResponse(DISABLED);
					extReceiptHeader.setExtraction(UNPROCESSED);
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

	private String getErrorMessage(String errorCode) {
		if ("".equals(StringUtils.stripToEmpty(errorCode))) {
			return "";
		}

		InterfaceErrorCode interfaceErrorCode = getErrorFromList(
				InterfaceErrorCodeUtil.getInstance().getInterfaceErrorsList(), errorCode);
		return interfaceErrorCode.getErrorMessage();
	}

	private void saveDetails(List<CollReceiptDetail> detailList, long id) {
		logger.debug(Literal.ENTERING);
		extCollectionReceiptDao.saveFileExtractionList(detailList, id);
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
