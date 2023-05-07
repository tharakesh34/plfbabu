package com.pennanttech.external.presentment.job;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.pennanttech.external.config.ApplicationContextProvider;
import com.pennanttech.external.config.ExtErrorCodes;
import com.pennanttech.external.config.InterfaceErrorCode;
import com.pennanttech.external.constants.InterfaceConstants;
import com.pennanttech.external.dao.ExtStagingDao;
import com.pennanttech.external.dao.impl.ExtStagingDaoImpl;
import com.pennanttech.external.presentment.dao.ExtPresentmentDAO;
import com.pennanttech.external.presentment.model.ExtBounceReason;
import com.pennanttech.external.presentment.model.ExtBounceReasons;
import com.pennanttech.external.presentment.model.ExtPresentmentFile;
import com.pennanttech.external.presentment.model.ExtPrmntRespHeader;
import com.pennanttech.model.presentment.Presentment;
import com.pennanttech.pennapps.core.job.AbstractJob;
import com.pennanttech.pennapps.core.resource.Literal;

public class ExtPresentmentTableReaderJob extends AbstractJob implements InterfaceConstants {

	private static final Logger logger = LogManager.getLogger(ExtPresentmentTableReaderJob.class);
	private static final String FETCH_QUERY = "Select * from PDC_BATCH_D_STG  Where PICK_FINNONE = ?";
	private ExtStagingDao extStageDao;
	private ExtPresentmentDAO externalPresentmentDAO;
	private DataSource stagingDataSource;
	private PlatformTransactionManager transactionManager;

	private ApplicationContext applicationContext;

	@Override
	protected void executeJob(JobExecutionContext context) throws JobExecutionException {
		logger.debug(Literal.ENTERING);
		try {

			applicationContext = ApplicationContextProvider.getApplicationContext();
			extStageDao = applicationContext.getBean(ExtStagingDaoImpl.class);
			externalPresentmentDAO = applicationContext.getBean(ExtPresentmentDAO.class);
			stagingDataSource = applicationContext.getBean("stagingDataSource", DataSource.class);
			transactionManager = applicationContext.getBean("transactionManager", PlatformTransactionManager.class);

			readAndProcessTable();

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);

	}

	private void readAndProcessTable() throws Exception {
		logger.debug(Literal.ENTERING);

		boolean isHeaderImported = false;
		ExtPrmntRespHeader prh = null;

		// Fetch bounce details beforehand..
		if (ExtBounceReasons.getInstance().getBounceData().isEmpty()) {
			List<ExtBounceReason> bounceReasons = externalPresentmentDAO.fetchBounceReasons();
			for (ExtBounceReason bounceReason : bounceReasons) {
				ExtBounceReasons.getInstance().getBounceData().put(bounceReason.getReturnCode(), bounceReason);
			}
		}

		// Fetch 10 files using extraction status = 0
		JdbcCursorItemReader<ExtPresentmentFile> cursorItemReader = new JdbcCursorItemReader<ExtPresentmentFile>();
		cursorItemReader.setDataSource(stagingDataSource);
		cursorItemReader.setFetchSize(10);
		cursorItemReader.setSql(FETCH_QUERY);
		cursorItemReader.setRowMapper(new RowMapper<ExtPresentmentFile>() {
			@Override
			public ExtPresentmentFile mapRow(ResultSet rs, int rowNum) throws SQLException {
				ExtPresentmentFile details = new ExtPresentmentFile();
				details.setClusterId(StringUtils.trimToEmpty(rs.getString("FINNONE_BATCHID")));
				details.setAgreementId(rs.getLong("AGREEMENTID"));
				details.setChequeSerialNo(rs.getString("CHEQUESNO"));
				details.setChequeDate(rs.getDate("CHEQUEDATE"));
				details.setStatus(rs.getString("CHEQUESTATUS"));

				String bounceData = rs.getString("BOUNCE_REASON");

				if (StringUtils.isAllBlank(bounceData)) {
					details.setStatus(InterfaceConstants.SUCCESS);
				} else {
					details.setStatus(InterfaceConstants.FAIL);
					String[] strs = StringUtils.split(bounceData, "-");
					details.setBounceRetrunCode(strs[0]);
					details.setBounceReason(strs[1]);
				}

				return details;
			}
		});

		cursorItemReader.setPreparedStatementSetter(new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, "N");
			}
		});

		ExecutionContext executionContext = new ExecutionContext();
		cursorItemReader.open(executionContext);

		DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
		txDef.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_REQUIRES_NEW);

		ExtPresentmentFile extPresentmentFile;

		while ((extPresentmentFile = cursorItemReader.read()) != null) {

			String chqNo = StringUtils.stripToEmpty(extPresentmentFile.getChequeSerialNo());
			Date chqDate = extPresentmentFile.getChequeDate();
			long loanReff = extPresentmentFile.getAgreementId();

			if ("".equals(chqNo) || chqDate == null || loanReff <= 0) {

				logger.debug(
						"CMS_ERROR:Invalid cheque no or cheque date or loan number received. Unable to process data from stage table.");
				continue;

			}

			if (!isHeaderImported) {
				isHeaderImported = true;
				prh = prepareHeader();
				externalPresentmentDAO.save(prh);
			}

			TransactionStatus txStatus = null;
			try {

				// begin the transaction
				txStatus = transactionManager.getTransaction(txDef);

				// update the pick flag and date
				extStageDao.updatePickupStatus("Y", extPresentmentFile.getAgreementId(),
						extPresentmentFile.getChequeSerialNo());

				// validation extPresentment record if any error exists
				String errorCode = extPresentmentFile.getErrorCode();
				if (extPresentmentFile != null && !"".equals(errorCode)) {

					String errorMessage = extPresentmentFile.getErrorMessage();
					extStageDao.updateErrorDetails(extPresentmentFile.getAgreementId(),
							extPresentmentFile.getChequeSerialNo(), "Y", errorMessage);
					// commit the transaction
					transactionManager.commit(txStatus);
					continue;
				}

				Presentment data = externalPresentmentDAO.getPDCStagingPresentmentDetails(
						String.valueOf(extPresentmentFile.getAgreementId()), extPresentmentFile.getChequeSerialNo(),
						extPresentmentFile.getChequeDate());

				if (data == null) {
					InterfaceErrorCode interfaceErrorCode = getErrorFromList(
							ExtErrorCodes.getInstance().getInterfaceErrorsList(), F703);

					extStageDao.updateErrorDetails(extPresentmentFile.getAgreementId(),
							extPresentmentFile.getChequeSerialNo(), "Y", interfaceErrorCode.getErrorMessage());
					// commit the transaction
					transactionManager.commit(txStatus);
					continue;
				}

				// check if presentment.ID is already inserted into resp_dtls table
				boolean isInserted = externalPresentmentDAO.isRecordInserted(data.getBatchId(), prh.getHeaderId());

				if (isInserted) {
					// already proceed
					continue;
				}

				// fetch presentment record details required to save into PRESENTMENT_RESP_DTLS table
				processData(extPresentmentFile, data);

				// Save the presentment details into PRESENTMENT_RESP_DTLS table
				String clearingStatus = "S";
				if (FAIL.equals(extPresentmentFile.getStatus())) {
					clearingStatus = "B";
				}
				externalPresentmentDAO.savePresentment(data, prh.getHeaderId(), clearingStatus);

				// update record status as completed
				extStageDao.updateErrorDetails(extPresentmentFile.getAgreementId(),
						extPresentmentFile.getChequeSerialNo(), "N", "");

				// commit the transaction
				transactionManager.commit(txStatus);

			} catch (Exception e) {
				logger.debug("Exception:", e);
				if (txStatus != null) {
					transactionManager.rollback(txStatus);
				}
			}
		}
		cursorItemReader.close();

		if (isHeaderImported && prh.getHeaderId() > 0) {
			// update presentment resp header progress as done with remarks
			prh.setProgress(PROGRESS_DONE);

			// Update Resp Header table with headerId mentioning the file is processed for receipt creation
			externalPresentmentDAO.updateHeader(prh);
		}

		logger.debug(Literal.LEAVING);
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

	private ExtPrmntRespHeader prepareHeader() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyyHHmmss");
		String fileName = "PDC_RESPONSE_" + dateFormat.format(new Date()) + ".txt";
		ExtPrmntRespHeader prh = new ExtPrmntRespHeader();
		prh.setFileName(fileName);
		prh.setEvent("IMPORT");
		prh.setProgress(PROGRESS_INIT);
		return prh;
	}
}
