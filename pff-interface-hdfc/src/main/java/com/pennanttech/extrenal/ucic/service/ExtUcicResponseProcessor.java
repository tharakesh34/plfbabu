package com.pennanttech.extrenal.ucic.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.pennanttech.external.config.ApplicationContextProvider;
import com.pennanttech.external.constants.InterfaceConstants;
import com.pennanttech.extrenal.ucic.dao.ExtUcicDao;
import com.pennanttech.extrenal.ucic.model.ExtUcicData;
import com.pennanttech.extrenal.ucic.model.ExtUcicFile;
import com.pennanttech.pennapps.core.resource.Literal;

public class ExtUcicResponseProcessor implements InterfaceConstants {
	private static final Logger logger = LogManager.getLogger(ExtUcicResponseProcessor.class);

	private static final String FETCH_QUERY = "Select * from UCIC_RESP_FILES  Where STATUS = ? AND EXTRACTION = ?";
	private static final String FETCH_RECORDS_QUERY = "Select * from UCIC_RESP_FILE_DATA  Where STATUS = ? AND HEADER_ID = ?";

	private ExtUcicDao extUcicDao;
	private ApplicationContext applicationContext;
	private DataSource dataSource;

	private PlatformTransactionManager transactionManager;

	public void processExtractedRecords() throws Exception {
		logger.debug(Literal.ENTERING);

		applicationContext = ApplicationContextProvider.getApplicationContext();
		dataSource = applicationContext.getBean("dataSource", DataSource.class);
		transactionManager = applicationContext.getBean("transactionManager", PlatformTransactionManager.class);

		// Read 10 files at a time using file status = 0
		JdbcCursorItemReader<ExtUcicFile> cursorItemReader = new JdbcCursorItemReader<ExtUcicFile>();
		cursorItemReader.setDataSource(dataSource);
		cursorItemReader.setFetchSize(10);
		cursorItemReader.setSql(FETCH_QUERY);
		cursorItemReader.setRowMapper(new RowMapper<ExtUcicFile>() {
			@Override
			public ExtUcicFile mapRow(ResultSet rs, int rowNum) throws SQLException {
				ExtUcicFile ucicFile = new ExtUcicFile();
				ucicFile.setId(rs.getLong("ID"));
				return ucicFile;
			}
		});

		cursorItemReader.setPreparedStatementSetter(new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setLong(1, COMPLETED);// STATUS = UnProcessed-0
				ps.setLong(2, COMPLETED);// EXTRACTION = Extracted-2
			}
		});

		ExecutionContext executionContext = new ExecutionContext();
		cursorItemReader.open(executionContext);

		ExtUcicFile ucicFile;

		while ((ucicFile = cursorItemReader.read()) != null) {

			long header_id = ucicFile.getId();

			JdbcCursorItemReader<ExtUcicData> cursorItemReader1 = new JdbcCursorItemReader<ExtUcicData>();
			cursorItemReader1.setDataSource(dataSource);
			cursorItemReader1.setFetchSize(100);
			cursorItemReader1.setSql(FETCH_RECORDS_QUERY);
			cursorItemReader1.setRowMapper(new RowMapper<ExtUcicData>() {
				@Override
				public ExtUcicData mapRow(ResultSet rs, int rowNum) throws SQLException {
					ExtUcicData extUcicData = new ExtUcicData();
					extUcicData.setId(rs.getLong("HEADER_ID"));
					extUcicData.setCustId(rs.getString("CUSTID"));
					extUcicData.setUcicId(rs.getString("UCIC_ID"));
					return extUcicData;
				}
			});

			cursorItemReader1.setPreparedStatementSetter(new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					ps.setLong(1, UNPROCESSED);// STATUS = UnProcessed-0
					ps.setLong(2, header_id);// HEADER_ID
				}
			});

			ExecutionContext executionContext1 = new ExecutionContext();
			cursorItemReader1.open(executionContext1);

			DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
			txDef.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_REQUIRES_NEW);

			ExtUcicData ucicData;
			TransactionStatus txStatus = null;

			while ((ucicData = cursorItemReader1.read()) != null) {
				try {

					// begin the transaction
					txStatus = transactionManager.getTransaction(txDef);

					// Fetch existing UCIC Id for the customerId
					String existingUcicId = extUcicDao.getExistingUcicIc(ucicData.getCustId());

					if (extUcicDao.isCustomerInMakerStage(ucicData.getCustId())) {

						// Mark record processing status as FAILURE and ACK_PENDING for file writing
						extUcicDao.updateFileRecordProcessingFlagAndStatus(ucicData.getId(), ucicData.getCustId(),
								COMPLETED, UCIC_UPDATE_FAIL, "Customer is in GCD maker", ACK_PENDING);

						// commit the transaction
						transactionManager.commit(txStatus);

						continue;
					}

					// Update UCIC ID against CUST ID in customers table
					extUcicDao.updateUcicIdInCustomers(ucicData.getCustId(), ucicData.getUcicId());

					extUcicDao.updateFileRecordProcessingFlagAndStatus(ucicData.getId(), ucicData.getCustId(),
							COMPLETED, UCIC_UPDATE_SUCCESS, "", ACK_PENDING);

					// commit the transaction
					transactionManager.commit(txStatus);

					// Check if the existing ucic is having customers
					if (existingUcicId == null || "".equals(existingUcicId)) {
						continue;
					}

					List<ExtUcicData> dependentCustIds = extUcicDao.getCustBasedOnUcicId(existingUcicId);

					for (int k = 0; k < dependentCustIds.size(); k++) {
						String dependantCustId = dependentCustIds.get(k).getCustId();
						// Check if customer is in maker stage, then don't update.
						if (!extUcicDao.isCustomerInMakerStage(dependantCustId)) {
							// Update Dependent customer UCIC ID against CUST ID in customers table
							extUcicDao.updateUcicIdInCustomers(dependantCustId, ucicData.getUcicId());
							// commit the transaction
							transactionManager.commit(txStatus);
						}
					}

				} catch (Exception e) {
					if (txStatus != null) {
						transactionManager.rollback(txStatus);
					}
					e.printStackTrace();
				}
			}
			cursorItemReader1.close();
		}
		cursorItemReader.close();
		logger.debug(Literal.LEAVING);
	}

	public void setExtUcicDao(ExtUcicDao extUcicDao) {
		this.extUcicDao = extUcicDao;
	}

}
