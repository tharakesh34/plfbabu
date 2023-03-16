package com.pennanttech.external.presentment.job;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
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
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.pennanttech.external.config.ApplicationContextProvider;
import com.pennanttech.external.config.ExtErrorCodes;
import com.pennanttech.external.config.ExternalConfig;
import com.pennanttech.external.config.InterfaceErrorCode;
import com.pennanttech.external.constants.InterfaceConstants;
import com.pennanttech.external.dao.ExtInterfaceDao;
import com.pennanttech.external.presentment.dao.ExtPresentmentDAO;
import com.pennanttech.external.presentment.model.ExtPresentment;
import com.pennanttech.external.presentment.model.ExtPresentmentData;
import com.pennanttech.external.presentment.service.ACHService;
import com.pennanttech.external.presentment.service.SIService;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.job.AbstractJob;
import com.pennanttech.pennapps.core.resource.Literal;

public class ExtPresentmentFileExtractionJob extends AbstractJob implements InterfaceConstants {
	private static final Logger logger = LogManager.getLogger(ExtPresentmentFileExtractionJob.class);
	private static final String FETCH_QUERY = "Select * from PRMNT_HEADER  Where EXTRACTION = ?";

	private DataSource dataSource;
	private ExtInterfaceDao extInterfaceDao;
	private ExtPresentmentDAO externalPresentmentDAO;
	private PlatformTransactionManager transactionManager;
	private ApplicationContext applicationContext;

	@Override
	protected void executeJob(JobExecutionContext context) throws JobExecutionException {
		logger.debug(Literal.ENTERING);
		try {
			// Fetch all required DAO's
			applicationContext = ApplicationContextProvider.getApplicationContext();
			externalPresentmentDAO = applicationContext.getBean(ExtPresentmentDAO.class);
			extInterfaceDao = applicationContext.getBean(ExtInterfaceDao.class);
			dataSource = applicationContext.getBean("extDataSource", DataSource.class);
			transactionManager = applicationContext.getBean("transactionManager", PlatformTransactionManager.class);

			readAndExtractFiles();
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);

	}

	public void readAndExtractFiles() throws UnexpectedInputException, ParseException, TransactionException, Exception {
		logger.debug(Literal.ENTERING);

		List<ExternalConfig> mainConfig = extInterfaceDao.getExternalConfig();

		// get error codes handy
		if (ExtErrorCodes.getInstance().getInterfaceErrorsList().isEmpty()) {
			List<InterfaceErrorCode> interfaceErrorsList = extInterfaceDao.fetchInterfaceErrorCodes();
			ExtErrorCodes.getInstance().setInterfaceErrorsList(interfaceErrorsList);
		}

		// Fetch 10 files using extraction status = 0
		JdbcCursorItemReader<ExtPresentment> cursorItemReader = new JdbcCursorItemReader<ExtPresentment>();
		cursorItemReader.setDataSource(dataSource);
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
				ps.setLong(1, UNPROCESSED);
			}
		});

		ExecutionContext executionContext = new ExecutionContext();
		cursorItemReader.open(executionContext);

		DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
		txDef.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_REQUIRES_NEW);

		ExtPresentment extPresentment;
		try {
			while ((extPresentment = cursorItemReader.read()) != null) {
				TransactionStatus txStatus = null;

				Scanner sc = null;
				try {

					// update the extract state as processing
					externalPresentmentDAO.updateFileExtractionStatus(extPresentment.getId(), INPROCESS);

					String fileData = App.getResourcePath(extPresentment.getFileLocation()) + File.separator
							+ extPresentment.getFileName();

					// Validation on reject file 501
					if (CONFIG_SI_RESP.equals(extPresentment.getModule())
							|| CONFIG_IPDC_RESP.equals(extPresentment.getModule())) {

						ExternalConfig config = getDataFromList(mainConfig, extPresentment.getModule());

						String fileName = extPresentment.getFileName();
						String filePrepend = config.getFilePrepend();
						String fileExtension = config.getFileExtension();
						if (fileName.startsWith(filePrepend.concat(config.getFailIndicator()))
								&& fileName.endsWith(fileExtension)) {
							if (!validateRejectFile(fileData)) {
								// update the file extraction status as unprocessed because of invalid record count
								InterfaceErrorCode interfaceErrorCode = getErrorFromList(
										ExtErrorCodes.getInstance().getInterfaceErrorsList(), F606);

								externalPresentmentDAO.updateFileExtractionStatusWithError(extPresentment.getId(),
										FAILED, interfaceErrorCode.getErrorCode(),
										interfaceErrorCode.getErrorMessage());
								continue;
							}
						}
					}

					File file = new File(fileData);
					// Fetch list of record data from the file
					List<ExtPresentmentData> extPresentmentDataList = new ArrayList<ExtPresentmentData>();
					sc = new Scanner(file);

					boolean isTrue = true;

					// Read file line by line
					while (sc.hasNextLine()) {

						if (isTrue) {
							// begin the transaction
							txStatus = transactionManager.getTransaction(txDef);
							isTrue = false;
						}

						String lineData = sc.nextLine();

						// ACH header line validation (We don't consider header line as record)
						if (CONFIG_NACH_RESP.equals(extPresentment.getModule())) {
							if (lineData.trim().startsWith(ACHService.ACH_HEADER_LINE_CODE)) {
								continue;
							}
						}

						// SI or SI Internal(IPDC) EOF line validation (We don't consider EOF line as record)
						if (CONFIG_SI_RESP.equals(extPresentment.getModule())
								|| CONFIG_IPDC_RESP.equals(extPresentment.getModule())) {
							if (lineData.trim().startsWith(SIService.SI_END_LINE)) {
								continue;
							}
							if (lineData.trim().startsWith(SIService.SI_LINE_CASA)) {
								continue;
							}
							if (lineData.trim().startsWith(SIService.SI_LINE_ACCOUNT)) {
								continue;
							}
						}

						// Prepare ExtPresentmentData bean for saving record into table
						ExtPresentmentData extPresentmentData = new ExtPresentmentData();
						extPresentmentData.setHeaderId(extPresentment.getId());
						extPresentmentData.setRecord(lineData);
						extPresentmentData.setStatus(UNPROCESSED);
						extPresentmentDataList.add(extPresentmentData);

						if (extPresentmentDataList.size() == BULK_RECORD_COUNT) {
							// save bulk records at a time..
							externalPresentmentDAO.saveExternalPresentmentRecordsData(extPresentmentDataList);
							extPresentmentDataList.clear();
							// commit the transaction
							transactionManager.commit(txStatus);
							isTrue = true;
						}

					}

					if (extPresentmentDataList.size() > 0) {
						// save records remaining after bulk insert
						externalPresentmentDAO.saveExternalPresentmentRecordsData(extPresentmentDataList);
						extPresentmentDataList.clear();
					}

					// update the file extraction as completed
					externalPresentmentDAO.updateFileExtractionStatus(extPresentment.getId(), COMPLETED);

				} catch (Exception e) {
					if (txStatus != null) {
						transactionManager.rollback(txStatus);
					}
					// update the file extraction status as unprocessed
					externalPresentmentDAO.updateFileExtractionStatus(extPresentment.getId(), UNPROCESSED);
				} finally {
					if (sc != null) {
						sc.close();
					}
				}
			}

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		} finally {
			if (cursorItemReader != null) {
				cursorItemReader.close();
			}
		}

		logger.debug(Literal.LEAVING);
	}

	private boolean validateRejectFile(String filePath) {
		try {
			List<String> fileLines = Files.readAllLines(Paths.get(filePath));
			if (fileLines != null && fileLines.size() > 0) {
				int size = fileLines.size();
				String data = fileLines.get(size - 1);
				int recSize = size - 2;
				if (data != null && !"".equals(data)) {
					if (data.startsWith("F") && data.length() > 1) {
						int fileRecSize = 0;
						try {
							fileRecSize = Integer.parseInt(data.substring(1));
							if (fileRecSize == recSize) {
								return true;
							}
						} catch (Exception e) {
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}
}
