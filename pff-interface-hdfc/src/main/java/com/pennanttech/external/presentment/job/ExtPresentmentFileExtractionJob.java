package com.pennanttech.external.presentment.job;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
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
	private static final String FETCH_QUERY = "Select * from PRMNT_HEADER  Where EXTRACTION = ? AND STATUS = ?";

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
				ps.setLong(2, UNPROCESSED);
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

					boolean isTransactionStarted = false;

					// Read file line by line
					while (sc.hasNextLine()) {

						if (!isTransactionStarted) {
							// begin the transaction
							txStatus = transactionManager.getTransaction(txDef);
							isTransactionStarted = true;
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
							isTransactionStarted = false;
						}

					}

					if (extPresentmentDataList.size() > 0) {
						// save records remaining after bulk insert
						externalPresentmentDAO.saveExternalPresentmentRecordsData(extPresentmentDataList);
						if (isTransactionStarted) {
							// commit the transaction
							transactionManager.commit(txStatus);
							isTransactionStarted = false;
						}
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
		logger.debug(Literal.ENTERING);
		try {

			long fileLines = countLineInFile(filePath);
			logger.debug("EXT_VR: fileLines:" + fileLines);
			long recSize = fileLines - 2;
			logger.debug("EXT_VR: recSize" + recSize);
			File file = new File(filePath);
			logger.debug("EXT_VR: file" + file.getName());
			String data = readLastLine(file);
			logger.debug("EXT_VR: Data" + data);
			if (data != null && !"".equals(data)) {
				logger.debug("EXT_VR: IF");
				if (data.startsWith("F") && data.length() > 1) {
					logger.debug("EXT_VR: IFF");
					int fileRecSize = 0;
					fileRecSize = Integer.parseInt(data.substring(1));
					logger.debug("EXT_VR: IFF fileRecSize:" + fileRecSize);
					if (fileRecSize == recSize) {
						logger.debug("EXT_VR: IFF IF TRUE");
						return true;
					}
				} else {
					logger.debug("EXT_VR: IFE");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return false;
	}

	private long countLineInFile(String fileName) {
		long lines = 0;
		try (BufferedInputStream is = new BufferedInputStream(new FileInputStream(fileName))) {
			byte[] c = new byte[1024];
			int count = 0;
			int readChars = 0;
			boolean endsWithoutNewLine = false;
			while ((readChars = is.read(c)) != -1) {
				for (int i = 0; i < readChars; ++i) {
					if (c[i] == '\n')
						++count;
				}
				endsWithoutNewLine = (c[readChars - 1] != '\n');
			}
			if (endsWithoutNewLine) {
				++count;
			}
			lines = count;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return lines;
	}

	public String readLastLine(File file) throws Exception {
		String last = null, line;
		try (BufferedReader in = new BufferedReader(new FileReader(file))) {
			while ((line = in.readLine()) != null) {
				if (line != null) {
					last = line;
				}
			}
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
		} finally {
		}
		return last;
	}
}
