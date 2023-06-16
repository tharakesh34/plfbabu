package com.pennanttech.external.collectionreceipt.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.pennant.pff.receipt.model.CreateReceiptUpload;
import com.pennanttech.external.ExtReceiptServiceHook;
import com.pennanttech.external.app.constants.ErrorCodesConstants;
import com.pennanttech.external.app.constants.InterfaceConstants;
import com.pennanttech.external.app.util.ApplicationContextProvider;
import com.pennanttech.external.app.util.InterfaceErrorCodeUtil;
import com.pennanttech.external.collectionreceipt.dao.ExtCollectionReceiptDao;
import com.pennanttech.external.collectionreceipt.model.CollReceiptDetail;
import com.pennanttech.external.collectionreceipt.model.CollReceiptHeader;
import com.pennanttech.external.collectionreceipt.model.ExtCollectionReceiptData;
import com.pennanttech.pennapps.core.job.AbstractJob;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.constants.FinServiceEvent;

public class FileProcessCollectionReqJob extends AbstractJob implements InterfaceConstants, ErrorCodesConstants {

	private static final Logger logger = LogManager.getLogger(FileProcessCollectionReqJob.class);

	private static final String FETCH_QUERY = "Select * from COLL_RECEIPT_HEADER  Where STATUS=? AND EXTRACTION = ?";

	private DataSource extDataSource;
	private ExtCollectionReceiptDao extCollectionReceiptDao;
	private ApplicationContext applicationContext;
	private PlatformTransactionManager transactionManager;
	private ExtReceiptServiceHook extReceiptServiceHook;
	private CollectionReceiptService collectionReceiptService;

	@Override
	protected void executeJob(JobExecutionContext context) throws JobExecutionException {
		logger.debug(Literal.ENTERING);

		applicationContext = ApplicationContextProvider.getApplicationContext();
		extDataSource = applicationContext.getBean("extDataSource", DataSource.class);
		extCollectionReceiptDao = applicationContext.getBean("extCollectionReceiptDao", ExtCollectionReceiptDao.class);
		transactionManager = applicationContext.getBean("transactionManager", PlatformTransactionManager.class);
		extReceiptServiceHook = applicationContext.getBean(ExtReceiptServiceHook.class);
		collectionReceiptService = applicationContext.getBean(CollectionReceiptService.class);

		if (extReceiptServiceHook == null) {
			return;
		}

		// Fetch 10 files using extraction status = 0
		JdbcCursorItemReader<CollReceiptHeader> cursorItemReader = new JdbcCursorItemReader<CollReceiptHeader>();
		cursorItemReader.setDataSource(extDataSource);
		cursorItemReader.setFetchSize(1);
		cursorItemReader.setSql(FETCH_QUERY);
		cursorItemReader.setRowMapper(new RowMapper<CollReceiptHeader>() {
			@Override
			public CollReceiptHeader mapRow(ResultSet rs, int rowNum) throws SQLException {
				CollReceiptHeader collectionFile = new CollReceiptHeader();
				collectionFile.setId(rs.getLong("ID"));
				collectionFile.setRequestFileName(rs.getString("REQ_FILE_NAME"));
				collectionFile.setRequestFileLocation(rs.getString("REQ_FILE_LOCATION"));
				collectionFile.setErrorCode(rs.getString("ERROR_CODE"));
				collectionFile.setErrorMessage(rs.getString("ERROR_MESSAGE"));
				return collectionFile;
			}
		});

		cursorItemReader.setPreparedStatementSetter(new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setLong(1, COMPLETED);
				ps.setLong(2, COMPLETED);
			}
		});

		ExecutionContext executionContext = new ExecutionContext();
		cursorItemReader.open(executionContext);

		DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
		txDef.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_REQUIRES_NEW);

		CollReceiptHeader extReceiptHeader;

		try {
			while ((extReceiptHeader = cursorItemReader.read()) != null) {

				// header update to in progress
				extReceiptHeader.setStatus(INPROCESS);
				extCollectionReceiptDao.updateExtCollectionReceiptProcessStatus(extReceiptHeader);

				List<CollReceiptDetail> fileRecordsList = extCollectionReceiptDao
						.fetchCollectionRecordsById(extReceiptHeader.getId());

				if (!fileRecordsList.isEmpty()) {

					TransactionStatus txStatus = null;
					// Now for each record, generate receipt and update receipt status.
					for (CollReceiptDetail extRcd : fileRecordsList) {

						txStatus = transactionManager.getTransaction(txDef);
						// begin the transaction
						try {
							ExtCollectionReceiptData collectionData = collectionReceiptService.prepareData(extRcd);

							collectionReceiptService.dataValidations(extRcd, collectionData);

							if (extRcd.isValid()) {
								CreateReceiptUpload createReceiptUpload = collectionReceiptService
										.getCreateReceiptUploadBean(collectionData);

								extReceiptServiceHook.createExtReceipt(createReceiptUpload, FinServiceEvent.SCHDRPY);

								// Verify if receipt has generated by using progress status
								if (createReceiptUpload.getProgress() == 2) {
									extRcd.setReceiptId(createReceiptUpload.getReceiptID());
								} else {
									extRcd.setErrorCode(createReceiptUpload.getErrorCode());
									extRcd.setErrorMessage(createReceiptUpload.getErrorDesc());
								}

							} else {
								extRcd.setErrorMessage(InterfaceErrorCodeUtil.getErrorMessage(extRcd.getErrorCode()));
							}
							transactionManager.commit(txStatus);
						} catch (Exception e) {
							logger.error(Literal.EXCEPTION, e);
							if (txStatus != null) {
								transactionManager.rollback(txStatus);
							}
							extRcd.setErrorCode(CR1011);
							extRcd.setErrorMessage(e.getMessage());
						}

						extCollectionReceiptDao.updateExtCollectionReceiptDetailStatus(extRcd);
					}
				}

				extReceiptHeader.setStatus(COMPLETED);
				extReceiptHeader.setWriteResponse(ENABLED);
				extCollectionReceiptDao.updateExtCollectionReceiptProcessStatus(extReceiptHeader);

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
