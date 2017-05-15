package com.pennanttech.dbengine.process;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.pennanttech.dataengine.constants.ExecutionStatus;
import com.pennanttech.dataengine.model.Configuration;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.dataengine.util.DateUtil;
import com.pennanttech.dbengine.DBProcessEngine;
import com.pennanttech.pff.baja.BajajInterfaceConstants.Status;
import com.pennanttech.pff.core.Literal;

public class DisbursemenIMPSResponse extends DBProcessEngine {
	private static final Logger logger = Logger.getLogger(DisbursemenIMPSResponse.class);
	
	private Connection destConnection = null;
	private Connection sourceConnection = null;
	private DataEngineStatus executionStatus = null;
	private NamedParameterJdbcTemplate jdbcTemplate = null;

	public DisbursemenIMPSResponse(DataSource dataSource, String appDBName, DataEngineStatus executionStatus) {
		super(dataSource, appDBName, executionStatus);
		this.executionStatus = executionStatus;
	}

	public void process(long userId, Configuration config) {
		logger.debug(Literal.ENTERING);

		executionStatus.setStartTime(DateUtil.getSysDate());
		executionStatus.setName(config.getName());
		executionStatus.setUserId(userId);
		executionStatus.setReference(config.getName());
		executionStatus.setStatus(ExecutionStatus.I.name());
		executionStatus.setRemarks("Loading configuration..");

		ResultSet resultSet = null;
		StringBuilder remarks = new StringBuilder();
		long headerId = 0;
		try {
			saveBatchStatus();

			executionStatus.setRemarks("Loading destination database connection...");
			sourceConnection = getConnection(config);
			destConnection = DataSourceUtils.doGetConnection(dataSource);

			executionStatus.setRemarks("Fetching data from source table...");
			resultSet = getSourceData();

			this.jdbcTemplate = getJdbcTemplate(dataSource);

			if (resultSet != null) {
				resultSet.last();
				totalRecords = resultSet.getRow();
				resultSet.beforeFirst();
				executionStatus.setTotalRecords(totalRecords);

				if (resultSet.getRow() > 0) {
					headerId = saveHeader(config.getId(), totalRecords);
					while (resultSet.next()) {
						executionStatus.setRemarks("Saving data to destination table...");
						try {
							processedCount++;
							processRecord(resultSet, headerId);
							successCount++;
						} catch (Exception e) {
							failedCount++;
							logger.error("Exception :", e);
						}
						executionStatus.setProcessedRecords(processedCount);
						executionStatus.setSuccessRecords(successCount);
						executionStatus.setFailedRecords(failedCount);
					}
				}
			}

			if (totalRecords > 0) {
				if (failedCount > 0) {
					remarks.append("Completed with exceptions, Total records:  ");
					remarks.append(totalRecords);
					remarks.append(", Processed: ");
					remarks.append(processedCount);
					remarks.append(", Sucess: ");
					remarks.append(successCount);
					remarks.append(", Failure: ");
					remarks.append(failedCount + ".");
				} else {
					remarks.append("Processed successfully , Total records: ");
					remarks.append(totalRecords);
					remarks.append(", Processed: ");
					remarks.append(processedCount);
					remarks.append(", Sucess: ");
					remarks.append(successCount + ".");
				}
				updateBatchStatus(ExecutionStatus.S.name(), remarks.toString());
			}
		} catch (Exception e) {
			logger.error("Exception :", e);
			updateBatchStatus(ExecutionStatus.F.name(), e.getMessage());
			remarks.append(e.getMessage());
			executionStatus.setStatus(ExecutionStatus.F.name());
		} finally {
			releaseResorces(resultSet, destConnection, sourceConnection);
			resultSet = null;
			executionStatus.setRemarks(remarks.toString());
			updateHeader(headerId, processedCount);
		}

		logger.debug("Leaving");
	}
	

	private void processRecord(ResultSet rs, long headerId) throws Exception {

		String channelRefNum = getValue(rs, "CHANNELPARTNERREFNO");
		MapSqlParameterSource insertSource = new MapSqlParameterSource();
		MapSqlParameterSource updateSource = new MapSqlParameterSource();

		if (channelRefNum.isEmpty()) {
			throw new Exception("Channnel Partener RefNo not available in source table.");
		}

		if (isChannelRefExists(channelRefNum)) {
			insertSource.addValue("TransactionStatus", Status.E.name());
			updateSource.addValue("PROCESSFLAG", Status.Y.name());
		} else {
			insertSource.addValue("TransactionStatus", Status.R.name());
			updateSource.addValue("PROCESSFLAG", Status.R.name());
		}
		
		insertSource.addValue("ID", getNextId("DISB_PAY_DET_SEQ", true));
		insertSource.addValue("HeaderID", headerId);
		insertSource.addValue("DisbursementReference", Long.valueOf(channelRefNum));
		insertSource.addValue("DisbursementAmount", 0.00);//FIXME
		insertSource.addValue("TransactionDate", new Timestamp(System.currentTimeMillis()));//FIXME
		insertSource.addValue("BankReferenceNumber",  getValue(rs, "TRANSACTIONID"));
		insertSource.addValue("PLFStatus", Status.I.name());
		insertSource.addValue("InputTime", new Timestamp(System.currentTimeMillis()));
		
		String description = getValue(rs, "DESCRIPTION");
		if (!description.isEmpty()) {
			insertSource.addValue("PaymentRemarks1", StringUtils.substring(description, 0, 49));
			insertSource.addValue("PaymentRemarks2", StringUtils.substring(description, 50, 99));
			insertSource.addValue("PaymentRemarks3", StringUtils.substring(description, 100, 149));
			insertSource.addValue("PaymentRemarks4", StringUtils.substring(description, 150, 199));
		}
		save(insertSource);
		
		updateSource.addValue("CHANNELPARTNERREFNO", channelRefNum);
		update(updateSource);
	}


	private void update(MapSqlParameterSource updateSource) {

		StringBuilder sql = new StringBuilder("Update INT_DSBIMPS_RESPONSE");
		sql.append(" Set PROCESSFLAG = :PROCESSFLAG ");
		sql.append(" Where CHANNELPARTNERREFNO = :CHANNELPARTNERREFNO");

		this.jdbcTemplate.update(sql.toString(), updateSource);
	}

	private void save(MapSqlParameterSource insertSource) {

		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO DISBURSEMENT_PAYMENT_DETAILS VALUES (");
		sql.append(" ID, HeaderID, DisbursementReference, TransactionDate, BankReferenceNumber,");
		sql.append(" PaymentRemarks1, PaymentRemarks2, PaymentRemarks3, PaymentRemarks4, TransactionStatus, PLFStatus, InputTime)");
		sql.append(" Values (");
		sql.append(" :ID, :HeaderID, :DisbursementReference, :TransactionDate, :BankReferenceNumber,");
		sql.append(" :PaymentRemarks1, :PaymentRemarks2, :PaymentRemarks3, :PaymentRemarks4, :TransactionStatus, :PLFStatus, :InputTime)");

		this.jdbcTemplate.update(sql.toString(), insertSource);
	}
	
	private long saveHeader(long configID, int totalRecords) {
		MapSqlParameterSource source = new MapSqlParameterSource();
		StringBuffer sql = new StringBuffer();
		long id = getNextId("INTERFACE_HEADER_SEQ", true);

		source.addValue("ID", id);
		source.addValue("Category", "DB");
		source.addValue("InterfaceType", "DataBase");
		source.addValue("FileName", StringUtils.leftPad(String.valueOf(id), 10, '0'));
		source.addValue("ConfigID", configID);
		source.addValue("Status", "");
		source.addValue("TotalCount", totalRecords);
		source.addValue("InputTime", new Timestamp(System.currentTimeMillis()));

		sql.append(" Insert INTERFACE_HEADER Values (ID, Category, InterfaceType, FileName, ConfigID, Status, TotalCount, InputTime)");
		sql.append(" VAlues (:ID, :Category, :InterfaceType, :FileName, :ConfigID, :Status, :TotalCount, :InputTime)");
		
		this.jdbcTemplate.update(sql.toString(), source);
		return id;
	}
	
	private void updateHeader(long id, int processedCount) {
		MapSqlParameterSource source = new MapSqlParameterSource();
		StringBuffer sql = new StringBuffer();

		source.addValue("NoofRecordsProcessed", processedCount);
		source.addValue("ProcessedTime",new Timestamp(System.currentTimeMillis()));
		source.addValue("ID", id);

		sql.append(" Update INTERFACE_HEADER set NoofRecordsProcessed = :NoofRecordsProcessed,");
		sql.append(" ProcessedTime = :ProcessedTime Where ID = :ID");

		this.jdbcTemplate.update(sql.toString(), source);

	}
	
	private boolean isChannelRefExists(String channelRefNum) throws Exception {
		logger.debug("Entering");
		MapSqlParameterSource parameter = null;

		StringBuilder sql = new StringBuilder();
		sql.append(" Select Count(*) from FinAdvancePayments where PaymentId = :PaymentId");

		parameter = new MapSqlParameterSource();
		parameter.addValue("PaymentId", channelRefNum);

		try {
			if (this.jdbcTemplate.queryForObject(sql.toString(), parameter, Integer.class) > 0) {
				return true;
			}
		} catch (Exception e) {
			throw e;
		} finally {
			parameter = null;
			logger.debug("Leaving");
		}
		return false;
	}

	private ResultSet getSourceData() throws Exception {
		logger.debug("Entering");

		ResultSet rs = null;
		StringBuilder sql = null;
		try {
			sql = new StringBuilder();
			sql.append(" SELECT CHANNELPARTNERREFNO, TRANSACTIONID, STATUS, DESCRIPTION, PROCESSFLAG FROM INT_DSBIMPS_RESPONSE ");
			sql.append(" WHERE  PROCESSFLAG = ? ");

			PreparedStatement stmt = sourceConnection.prepareStatement(sql.toString(), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			stmt.setString(1, Status.N.name());
			
			rs = stmt.executeQuery();
		} catch (SQLException e) {
			logger.error("Exception {}", e);
			throw e;
		} finally {
			sql = null;
		}
		logger.debug("Leaving");
		return rs;
	}
	
}
