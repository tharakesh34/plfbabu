package com.pennanttech.external.presentment.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
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

import com.pennanttech.external.app.config.model.FileInterfaceConfig;
import com.pennanttech.external.app.constants.ErrorCodesConstants;
import com.pennanttech.external.app.constants.ExtIntfConfigConstants;
import com.pennanttech.external.app.constants.InterfaceConstants;
import com.pennanttech.external.app.util.ApplicationContextProvider;
import com.pennanttech.external.app.util.FileInterfaceConfigUtil;
import com.pennanttech.external.app.util.InterfaceErrorCodeUtil;
import com.pennanttech.external.presentment.dao.ExtPresentmentDAO;
import com.pennanttech.external.presentment.model.ExtBounceReason;
import com.pennanttech.external.presentment.model.ExtBounceReasons;
import com.pennanttech.external.presentment.model.ExtPresentment;
import com.pennanttech.external.presentment.model.ExtPresentmentData;
import com.pennanttech.external.presentment.model.ExtPresentmentFile;
import com.pennanttech.external.presentment.model.ExtPrmntRespHeader;
import com.pennanttech.model.presentment.Presentment;
import com.pennanttech.pennapps.core.job.AbstractJob;
import com.pennanttech.pennapps.core.resource.Literal;

public class FileProcessPresentmentRespJob extends AbstractJob
		implements InterfaceConstants, ErrorCodesConstants, ExtIntfConfigConstants {

	private static final Logger logger = LogManager.getLogger(FileProcessPresentmentRespJob.class);

	private static final String FETCH_QUERY = "Select * from PRMNT_HEADER  Where STATUS = ? AND EXTRACTION = ?";

	private static final String FETCH_DATA_QUERY = "Select * from PRMNT_DETAILS  Where STATUS = ? AND HEADER_ID = ?";

	private DataSource extDataSource;
	private SIService siService;
	private SIInternalService siInternalService;
	private ACHService achService;
	private ExtPresentmentDAO externalPresentmentDAO;
	private PlatformTransactionManager transactionManager;

	private ApplicationContext applicationContext;

	@Override
	protected void executeJob(JobExecutionContext context) throws JobExecutionException {
		logger.debug(Literal.ENTERING);

		// Get all the required DAO's
		applicationContext = ApplicationContextProvider.getApplicationContext();
		externalPresentmentDAO = applicationContext.getBean(ExtPresentmentDAO.class);
		extDataSource = applicationContext.getBean("extDataSource", DataSource.class);
		siService = applicationContext.getBean(SIService.class);
		siInternalService = applicationContext.getBean(SIInternalService.class);
		achService = applicationContext.getBean(ACHService.class);
		transactionManager = applicationContext.getBean("transactionManager", PlatformTransactionManager.class);

		// Process starts here
		readAndProcessFiles();

		logger.debug(Literal.LEAVING);

	}

	public void readAndProcessFiles() {
		logger.debug(Literal.ENTERING);

		// Fetch bounce details beforehand..
		if (ExtBounceReasons.getInstance().getBounceData().isEmpty()) {
			List<ExtBounceReason> bounceReasons = externalPresentmentDAO.fetchBounceReasons();
			for (ExtBounceReason bounceReason : bounceReasons) {
				ExtBounceReasons.getInstance().getBounceData().put(bounceReason.getReturnCode(), bounceReason);
			}
		}

		// Read 10 files at a time using file status = 0
		JdbcCursorItemReader<ExtPresentment> cursorItemReader = new JdbcCursorItemReader<ExtPresentment>();
		cursorItemReader.setDataSource(extDataSource);
		cursorItemReader.setFetchSize(1);
		cursorItemReader.setSql(FETCH_QUERY);
		cursorItemReader.setRowMapper(new RowMapper<ExtPresentment>() {
			@Override
			public ExtPresentment mapRow(ResultSet rs, int rowNum) throws SQLException {
				ExtPresentment extPresentment = new ExtPresentment();
				extPresentment.setId(rs.getLong("ID"));
				extPresentment.setModule(rs.getString("MODULE"));
				extPresentment.setStatus(rs.getInt("STATUS"));
				extPresentment.setFileName(rs.getString("FILE_NAME"));
				extPresentment.setFileLocation(rs.getString("FILE_LOCATION"));
				extPresentment.setCreatedDate(rs.getDate("CREATED_DATE"));
				return extPresentment;
			}
		});

		cursorItemReader.setPreparedStatementSetter(new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setLong(1, UNPROCESSED);// STATUS = ?
				ps.setLong(2, COMPLETED);// EXTRACTION = ?
			}
		});

		ExecutionContext executionContext = new ExecutionContext();
		cursorItemReader.open(executionContext);

		ExtPresentment extPresentment;

		try {
			while ((extPresentment = cursorItemReader.read()) != null) {
				try {
					FileInterfaceConfig config = FileInterfaceConfigUtil.getFIConfig(extPresentment.getModule());

					// update the processing state as processing
					externalPresentmentDAO.updateFileStatus(extPresentment.getId(), INPROCESS);

					// Prepare Presentment Resp Header Object and set filename, event and progress.
					ExtPrmntRespHeader prh = prepareHeader(extPresentment);

					// GET new Header Id before inserting records into RESP_DTLS table
					externalPresentmentDAO.save(prh);

					if (prh.getHeaderId() <= 0) {
						// Concurrency exception may happen, process next file. continuing
						externalPresentmentDAO.updateFileStatus(extPresentment.getId(), UNPROCESSED);
						continue;
					}

					// get file records with the extPresentment
					processFileRecords(extPresentment, config, prh);

					// update presentment resp header progress as done with remarks
					prh.setProgress(PROGRESS_DONE);

					// Update Resp Header table with headerId mentioning the file is processed for receipt creation
					externalPresentmentDAO.updateHeader(prh); // UNCOMMENT ME

					externalPresentmentDAO.updateFileStatus(extPresentment.getId(), COMPLETED);

				} catch (Exception e) {
					logger.debug(Literal.EXCEPTION, e);
					externalPresentmentDAO.updateFileStatus(extPresentment.getId(), UNPROCESSED);
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

	private void processFileRecords(ExtPresentment extPresentment, FileInterfaceConfig extConfig,
			ExtPrmntRespHeader prh) {

		logger.debug(Literal.ENTERING);

		// Fetch 100 records at a time
		JdbcCursorItemReader<ExtPresentmentData> dataCursorReader = new JdbcCursorItemReader<ExtPresentmentData>();
		dataCursorReader.setDataSource(extDataSource);
		dataCursorReader.setFetchSize(100);
		dataCursorReader.setSql(FETCH_DATA_QUERY);
		dataCursorReader.setRowMapper(new RowMapper<ExtPresentmentData>() {
			@Override
			public ExtPresentmentData mapRow(ResultSet rs, int rowNum) throws SQLException {
				ExtPresentmentData extPresentmentData = new ExtPresentmentData();
				extPresentmentData.setId(rs.getLong("ID"));
				extPresentmentData.setHeaderId(rs.getLong("HEADER_ID"));
				extPresentmentData.setRecord(rs.getString("RECORD_DATA"));
				return extPresentmentData;
			}
		});

		dataCursorReader.setPreparedStatementSetter(new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setLong(1, UNPROCESSED); // STATUS = ?
				ps.setLong(2, extPresentment.getId()); // HEADER_ID = ?
			}
		});

		ExecutionContext executionContext = new ExecutionContext();
		dataCursorReader.open(executionContext);

		DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
		txDef.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_REQUIRES_NEW);

		ExtPresentmentData extPresentmentData;
		TransactionStatus txStatus = null;

		boolean isTransactionStarted = false;

		try {

			List<Presentment> extPresentmentDataList = new ArrayList<Presentment>();

			while ((extPresentmentData = dataCursorReader.read()) != null) {

				if (!isTransactionStarted) {
					// begin the transaction
					txStatus = transactionManager.getTransaction(txDef);
					isTransactionStarted = true;
				}

				prh.setTotalRecords(prh.getTotalRecords() + 1);

				long id = extPresentmentData.getId();

				// Get extPresentment object from record data
				ExtPresentmentFile extPresentmentFile = prepareAndValidate(extConfig, extPresentment,
						extPresentmentData);

				// validation extPresentment record if any error exists
				String errorCode = extPresentmentFile.getErrorCode();

				if (extPresentmentFile != null && !"".equals(errorCode)) {
					logger.debug(InterfaceErrorCodeUtil.getErrorMessage(PR1010));
					String errorMessage = extPresentmentFile.getErrorMessage();
					externalPresentmentDAO.updateExternalPresentmentRecordStatus(id, UNPROCESSED, errorCode,
							errorMessage);
					continue;
				}

				Presentment data = getRequiredData(extPresentmentFile, extPresentment);

				if (data == null) {
					logger.debug(InterfaceErrorCodeUtil.getErrorMessage(PR1011));
					externalPresentmentDAO.updateExternalPresentmentRecordStatus(id, UNPROCESSED, PR1011,
							InterfaceErrorCodeUtil.getErrorMessage(PR1011));
					continue;
				}

				// fetch presentment record details required to save into PRESENTMENT_RESP_DTLS table
				processData(extPresentmentFile, data);

				// Save the presentment details into PRESENTMENT_RESP_DTLS table
				String clearingStatus = "S";
				if (FAIL.equals(extPresentmentFile.getStatus())) {
					clearingStatus = "B";
				}

				data.setStatus(clearingStatus);

				extPresentmentDataList.add(data);

				// update record status as completed
				externalPresentmentDAO.updateExternalPresentmentRecordStatus(id, COMPLETED, "", "");

				if (extPresentmentDataList.size() == BULK_RECORD_COUNT) {
					// save bulk records at a time..
					externalPresentmentDAO.savePresentment(extPresentmentDataList, prh.getHeaderId());
					extPresentmentDataList.clear();
					// commit the transaction
					transactionManager.commit(txStatus);
					isTransactionStarted = false;
				}
			}

			if (extPresentmentDataList.size() > 0) {
				// save records remaining after bulk insert
				externalPresentmentDAO.savePresentment(extPresentmentDataList, prh.getHeaderId());
				extPresentmentDataList.clear();
			}

			if (isTransactionStarted) {
				// commit the transaction
				transactionManager.commit(txStatus);
				isTransactionStarted = false;
			}

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);

			if (txStatus != null) {
				transactionManager.rollback(txStatus);
			}
		} finally {
			if (dataCursorReader != null) {
				dataCursorReader.close();
			}
		}
		logger.debug(Literal.LEAVING);

	}

	private ExtPresentmentFile prepareAndValidate(FileInterfaceConfig extConfig, ExtPresentment extPresentment,
			ExtPresentmentData extPresentmentData) {
		logger.debug(Literal.ENTERING);
		ExtPresentmentFile extPresentmentFile = null;
		try {
			String fileName = StringUtils.stripToEmpty(extPresentment.getFileName());

			if (CONFIG_SI_RESP.equals(extConfig.getInterfaceName())) {
				extPresentmentFile = siService.prepareResponseObject(extConfig, extPresentmentData,
						extPresentment.getFileName());
			}

			if (CONFIG_IPDC_RESP.equals(extConfig.getInterfaceName())) {
				extPresentmentFile = siInternalService.prepareResponseObject(extConfig, extPresentmentData, fileName);
			}

			if (CONFIG_NACH_RESP.equals(extPresentment.getModule())) {
				extPresentmentFile = achService.prepareResponseObject(extConfig, extPresentmentData.getRecord());
			}

			if (extPresentmentFile == null) {
				extPresentmentFile = new ExtPresentmentFile();
				extPresentmentFile.setErrorCode(PR1020);
				extPresentmentFile.setErrorMessage(InterfaceErrorCodeUtil.getErrorMessage(PR1020));
			}

			// Validate Presentment.ID and BounceCode
			if (extPresentmentFile != null) {
				businessValidation(extPresentmentFile, extPresentment);
			}

		} catch (Exception e) {
			logger.debug(InterfaceErrorCodeUtil.getErrorMessage(PR1012));
			logger.debug(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return extPresentmentFile;
	}

	private void businessValidation(ExtPresentmentFile extPresentmentFile, ExtPresentment extPresentment) {

		if (extPresentmentFile.getTxnReference() == -1) {

			extPresentmentFile.setErrorCode(PR1021);
			extPresentmentFile.setErrorMessage(InterfaceErrorCodeUtil.getErrorMessage(PR1021));
			return;
		}

		if (StringUtils.endsWithIgnoreCase(extPresentmentFile.getStatus(), FAIL)) {
			String bounceReturnCode = extPresentmentFile.getBounceRetrunCode();

			if (StringUtils.isBlank(bounceReturnCode)) {
				// Bounce return code is empty or null, So report error
				extPresentmentFile.setErrorCode(PR1022);
				extPresentmentFile.setErrorMessage(InterfaceErrorCodeUtil.getErrorMessage(PR1022));
				return;
			}

			Map<String, ExtBounceReason> extBounceReasonsMap = ExtBounceReasons.getInstance().getBounceData();
			if (!extBounceReasonsMap.containsKey(bounceReturnCode)) {
				// Bounce code not found in PLF, So report error
				extPresentmentFile.setErrorCode(PR1023);
				extPresentmentFile.setErrorMessage(InterfaceErrorCodeUtil.getErrorMessage(PR1023));
				return;
			}

		}

	}

	private Presentment getRequiredData(ExtPresentmentFile extPresentmentFile, ExtPresentment extPresentment) {
		logger.debug(Literal.ENTERING);
		if (CONFIG_SI_RESP.equals(extPresentment.getModule()) || CONFIG_NACH_RESP.equals(extPresentment.getModule())) {
			return externalPresentmentDAO.getPresenementMandateRecord(extPresentmentFile.getTxnReference());
		}

		if (CONFIG_IPDC_RESP.equals(extPresentment.getModule())) {
			return externalPresentmentDAO.getPresenementPDCRecord(extPresentmentFile.getTxnReference());
		}
		logger.debug(Literal.LEAVING);
		return null;
	}

	private void processData(ExtPresentmentFile extPresentmentFile, Presentment presentment) {
		// Fetch all presentment details using presenment.ID based on module type
		Map<String, ExtBounceReason> extBounceReasonsMap = ExtBounceReasons.getInstance().getBounceData();

		if (FAIL.equals(extPresentmentFile.getStatus())) {

			presentment.setEntCode(extBounceReasonsMap.get(extPresentmentFile.getBounceRetrunCode()).getBounceCode());
			presentment.setReturnReason(
					extBounceReasonsMap.get(extPresentmentFile.getBounceRetrunCode()).getBounceReason());
		}

		presentment.setUtilityCode(extPresentmentFile.getBounceRetrunCode());

	}

	private ExtPrmntRespHeader prepareHeader(ExtPresentment extPresentment) {
		ExtPrmntRespHeader prh = new ExtPrmntRespHeader();
		prh.setFileName(extPresentment.getFileName());
		prh.setEvent("IMPORT");
		prh.setProgress(PROGRESS_INIT);
		return prh;
	}

}