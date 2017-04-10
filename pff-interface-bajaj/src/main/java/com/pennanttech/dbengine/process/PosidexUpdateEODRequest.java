package com.pennanttech.dbengine.process;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.pennanttech.dataengine.constants.ExecutionStatus;
import com.pennanttech.dataengine.model.Configuration;
import com.pennanttech.dataengine.model.DBConfiguration;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.dbengine.DBProcessEngine;
import com.pennanttech.dbengine.util.DateUtil;

public class PosidexUpdateEODRequest extends DBProcessEngine {

	private static final Logger logger = Logger.getLogger(PosidexUpdateEODRequest.class);
	private Connection destConnection;
	private Connection sourceConnection= null;
	private DataEngineStatus executionStatus;

	public PosidexUpdateEODRequest(DataSource dataSource, String appDBName, DataEngineStatus executionStatus) {
		super(dataSource, appDBName, executionStatus);
		this.executionStatus = executionStatus;
	}

	public void process(long userId, Configuration config) {
		logger.debug("Entering");

		executionStatus.setStartTime(DateUtil.getSysDate());
		executionStatus.setName(config.getName());
		executionStatus.setUserId(userId);
		executionStatus.setReference(config.getName());
		executionStatus.setStatus(ExecutionStatus.I.name());
		executionStatus.setRemarks("Loading configuration..");

		ResultSet resultSet = null;
		StringBuilder remarks = new StringBuilder();
		long fileId;

		try {
			
			executionStatus.setFileName(getFileName(config.getName()));
			saveBatchStatus();
			fileId = executionStatus.getId();

			executionStatus.setRemarks("Loading destination database connection...");
			DBConfiguration dbConfiguration = config.getDbConfiguration();
			destConnection = getConnection(dbConfiguration);
			sourceConnection=DataSourceUtils.doGetConnection(appDataSource);
			executionStatus.setRemarks("Fetching data from source table...");

			resultSet = getSourceData();

			if (resultSet != null) {
				resultSet.last();
				totalRecords = resultSet.getRow();
				resultSet.beforeFirst();
				executionStatus.setTotalRecords(totalRecords);
			}

			long batchId = 1;
			while (resultSet.next()) {
				try {
					executionStatus.setRemarks("Saving data in to destination table...");
					processedCount++;

					batchId = getNextId("PosidexUpdateSeq", true);
					saveCustData(resultSet, batchId);
					saveCustAddress(resultSet, batchId);
					saveCustControl(resultSet, batchId);					
					saveCustLoan(resultSet, batchId);

					successCount++;
					saveBatchLog(processedCount, fileId, processedCount, "DBExport", "S", "Success.", null);
				} catch (Exception e) {
					failedCount++;
					saveBatchLog(processedCount, fileId, processedCount, "DBExport", "F", e.getMessage(), null);
					logger.error("Exception :", e);
				}
				executionStatus.setProcessedRecords(processedCount);
				executionStatus.setSuccessRecords(successCount);
				executionStatus.setFailedRecords(failedCount);
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
				updateBatchStatus(ExecutionStatus.S.name(), remarks.toString(), processedCount, successCount, failedCount, totalRecords);
			} else {
				remarks.append("No records found for the selected configuration.");
				updateBatchStatus(ExecutionStatus.F.name(), remarks.toString(), processedCount, successCount, failedCount, totalRecords);
			}
		} catch (Exception e) {
			logger.error("Exception :", e);
			updateBatchStatus(ExecutionStatus.F.name(), e.getMessage(), processedCount, successCount, failedCount, totalRecords);
			remarks.append(e.getMessage());
			executionStatus.setStatus(ExecutionStatus.F.name());
		} finally {
			releaseResorces(null,destConnection,sourceConnection);			
			executionStatus.setRemarks(remarks.toString());
		}
		logger.debug("Leaving");
	}

	
	private ResultSet getSourceData() throws Exception {
		logger.debug("Entering");

		ResultSet rs = null;
		StringBuilder sql = null;
		try {
			sql = new StringBuilder();
			sql.append(" SELECT * from INT_POSIDEX_UPDATE_EOD_VIEW Where LastMntOn > ? AND LastMntOn < ?");

			PreparedStatement stmt = sourceConnection.prepareStatement(sql.toString(), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			stmt.setDate(1, new java.sql.Date(DateUtil.getPreviousDate().getTime()));
			stmt.setDate(2, new java.sql.Date(DateUtil.getAfterDate().getTime()));
			
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
	
	private void saveCustData(ResultSet rs,long batchId) throws Exception {
		logger.debug("Entering");
		
		PreparedStatement ps = null;
		try {
			StringBuilder sb = new StringBuilder();
			sb.append(" INSERT INTO DEDUP_EOD_CUST_DEMO_DTL (");
			sb.append("	BATCHID, CUSTOMER_NO, SOURCE_SYS_ID, FIRST_NAME, MIDDLE_NAME, LAST_NAME,DOB ,PAN, DRIVING_LICENSE_NUMBER, VOTER_ID, ");
			sb.append(" DATE_OF_INCORPORATION, TAN_NO, PROCESS_TYPE, APPLICANT_TYPE, EMPOYER_NAME, FATHER_NAME, PASSPORT_NO, ACCOUNT_NUMBER,CREDIT_CARD_NUMBER,");
			sb.append(" PROCESS_FLAG, ERROR_CODE,ERROR_DESC, CUSTOMER_ID, SOURCE_SYSTEM, PSX_BATCH_ID, UCIN_FLAG, EOD_BATCH_ID, INSERT_TS, GENDER, ");
			sb.append(" AADHAR_NO, CIN, DIN, REGISTRATION_NO, CA_NUMBER, SEGMENT)");
			sb.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

			ps = destConnection.prepareStatement(sb.toString());

			ps.setLong(1, batchId);
			ps.setBigDecimal(2, getBigDecimal(rs, "CUSTOMER_NO"));
			ps.setString(3, getValue(rs, "SOURCE_SYS_ID"));
			ps.setString(4, getValue(rs, "FIRST_NAME"));
			ps.setString(5, getValue(rs, "MIDDLE_NAME"));
			ps.setString(6, getValue(rs, "LAST_NAME"));
			ps.setDate(7, getDateValue(rs, "DOB"));
			ps.setString(8, getValue(rs, "PAN"));
			ps.setString(9, getValue(rs, "DRIVING_LICENSE_NUMBER"));
			ps.setString(10, getValue(rs, "VOTER_ID"));
			
			ps.setDate(11, getDateValue(rs, "DATE_OF_INCORPORATION"));
			ps.setString(12, getValue(rs, "TAN_NO"));
			ps.setString(13, getValue(rs, "PROCESS_TYPE"));
			ps.setString(14, getValue(rs, "APPLICANT_TYPE"));
			ps.setString(15, getValue(rs, "EMPOYER_NAME"));
			ps.setString(16, getValue(rs, "FATHER_NAME"));
			ps.setString(17, getValue(rs, "PASSPORT_NO"));
			ps.setString(18, getValue(rs, "ACCOUNT_NUMBER"));
			ps.setString(19, getValue(rs, "CREDIT_CARD_NUMBER"));
			
			ps.setString(20, getValue(rs, "PROCESS_FLAG"));
			ps.setString(21, getValue(rs, "ERROR_CODE"));
			ps.setString(22, getValue(rs, "ERROR_DESC"));
			ps.setString(23, getValue(rs, "CUSTOMER_ID"));
			ps.setString(24, getValue(rs, "SOURCE_SYSTEM"));
			ps.setString(25, getValue(rs, "PSX_BATCH_ID"));
			ps.setString(26, getValue(rs, "UCIN_FLAG"));
			ps.setString(27, getValue(rs, "EOD_BATCH_ID"));
			ps.setDate(28, getDateValue(rs, "INSERT_TS"));
			ps.setString(29, getValue(rs, "GENDER"));
			
			ps.setString(30, getValue(rs, "AADHAR_NO"));
			ps.setString(31, getValue(rs, "CIN"));
			ps.setString(32, getValue(rs, "DIN"));
			ps.setString(33, getValue(rs, "REGISTRATION_NO"));
			ps.setString(34, getValue(rs, "CA_NUMBER"));
			ps.setString(35, getValue(rs, "SEGMENT"));

			// execute query
			ps.executeUpdate();
			
		} catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		} finally {
			ps = null;
		}
		logger.debug("Leaving");
	}

	private void saveCustAddress(ResultSet rs,long batchId) throws Exception {
		logger.debug("Entering");
		
		PreparedStatement ps = null;
		try {
			StringBuilder sb = new StringBuilder();
			sb.append(" INSERT DEDUP_EOD_CUST_ADDR_DTL (");
			sb.append(" BATCHID, CUSTOMER_NO,, SOURCE_SYS_ID, SEGMENT, ADDRESS_TYPE, ADDRESS_1, ADDRESS_2, ADDRESS_3, STATE,");
			sb.append(" CITY, PIN, LANDLINE_1, LANDLINE_2, MOBILE, AREA, LANDMARK, STD, PROCESS_TYPE, EMAIL,");
			sb.append(" PROCESS_FLAG, ERROR_CODE, ERROR_DESC, CUSTOMER_ID, SOURCE_SYSTEM, PSX_BATCH_ID, EOD_BATCH_ID)");
			sb.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

			ps = destConnection.prepareStatement(sb.toString());

			ps.setLong(1, batchId);
			ps.setBigDecimal(2, getBigDecimal(rs, "CUSTOMER_NO"));
			ps.setString(3, getValue(rs, "SOURCE_SYS_ID"));
			ps.setString(4, getValue(rs, "SEGMENT"));
			ps.setString(5, getValue(rs, "ADDRESS_TYPE"));
			ps.setString(6, getValue(rs, "ADDRESS_1"));
			ps.setString(7, getValue(rs, "ADDRESS_2"));
			ps.setString(8, getValue(rs, "ADDRESS_3"));
			ps.setString(9, getValue(rs, "STATE"));
			
			ps.setString(10, getValue(rs, "CITY"));
			ps.setString(11, getValue(rs, "PIN"));
			ps.setString(12, getValue(rs, "LANDLINE_1"));
			ps.setString(13, getValue(rs, "LANDLINE_2"));
			ps.setString(14, getValue(rs, "MOBILE"));
			ps.setString(15, getValue(rs, "AREA"));
			ps.setString(16, getValue(rs, "LANDMARK"));
			ps.setString(17, getValue(rs, "STD"));
			ps.setString(18, getValue(rs, "PROCESS_TYPE"));
			ps.setString(19, getValue(rs, "EMAIL"));
			
			ps.setString(20, getValue(rs, "PROCESS_FLAG"));
			ps.setString(21, getValue(rs, "ERROR_CODE"));
			ps.setString(22, getValue(rs, "ERROR_DESC"));
			ps.setString(23, getValue(rs, "CUSTOMER_ID"));
			ps.setString(24, getValue(rs, "SOURCE_SYSTEM"));
			ps.setBigDecimal(25, getBigDecimal(rs, "PSX_BATCH_ID"));
			ps.setBigDecimal(26, getBigDecimal(rs, "EOD_BATCH_ID"));
			
			// execute query
			ps.executeUpdate();
		} catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		} finally {
			ps = null;
		}
		logger.debug("Leaving");
	}

	private void saveCustControl(ResultSet rs,long batchId) throws Exception {
		logger.debug("Entering");
		
		PreparedStatement ps = null;
		try {
			StringBuilder sb = new StringBuilder();
			sb.append(" INSERT PUSH_PULL_CONTROL_T (");
			sb.append(" BATCHID, STATUS, INSERT_TIMESTAMP, COMPLETION_TIMESTAMP, ERR_DESCRIPTION, ERROR_CODE)");
			sb.append(" VALUES (?,?,?,?,?,?)");
			
			ps = destConnection.prepareStatement(sb.toString());

			ps.setLong(1, batchId);
			ps.setString(2, getValue(rs, "STATUS"));
			ps.setDate(3, getDateValue(rs, "INSERT_TIMESTAMP"));
			ps.setDate(4, getDateValue(rs, "COMPLETION_TIMESTAMP"));
			ps.setString(5, getValue(rs, "ERR_DESCRIPTION"));
			ps.setString(6, getValue(rs, "ERROR_CODE"));
			
			// execute query
			ps.executeUpdate();
		} catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		} finally {
			ps = null;
		}
		logger.debug("Leaving");
	}


	private void saveCustLoan(ResultSet rs,long batchId) throws Exception {
		logger.debug("Entering");
		
		PreparedStatement ps = null;
		try {
			StringBuilder sb = new StringBuilder();
			sb.append(" INSERT DEDUP_EOD_CUST_LOAN_DTL (");
			sb.append(" BATCHID, CUSTOMER_NO, SOURCE_SYS_ID, SEGMENT, DEAL_ID, LAN_NO, CUSTOMER_TYPE, APPLN_NO,PRODUCT_CODE,)");
			sb.append(" PROCESS_TYPE, PROCESS_FLAG, ERROR_CODE, ERROR_DESC, PSX_BATCH_ID, PSX_ID, CUSTOMER_ID, SOURCE_SYSTEM, EOD_BATCH_ID)");
			sb.append(" VALUES (?,?,?,?,?,?)");
			
			ps = destConnection.prepareStatement(sb.toString());

			ps.setLong(1, batchId);
			ps.setBigDecimal(2, getBigDecimal(rs, "CUSTOMER_NO"));
			ps.setString(3, getValue(rs, "SOURCE_SYS_ID"));
			ps.setString(4, getValue(rs, "SEGMENT"));
			ps.setString(5, getValue(rs, "DEAL_ID"));
			ps.setString(6, getValue(rs, "LAN_NO"));
			ps.setString(7, getValue(rs, "CUSTOMER_TYPE"));
			ps.setInt(8, getIntValue(rs, "APPLN_NO"));
			ps.setString(9, getValue(rs, "PRODUCT_CODE"));
			ps.setString(10, getValue(rs, "PROCESS_TYPE"));
			ps.setString(11, getValue(rs, "PROCESS_FLAG"));
			ps.setString(12, getValue(rs, "ERROR_CODE"));
			ps.setString(13, getValue(rs, "ERROR_DESC"));
			ps.setBigDecimal(14, getBigDecimal(rs, "PSX_BATCH_ID"));
			ps.setBigDecimal(15, getBigDecimal(rs, "PSX_ID"));
			ps.setString(16, getValue(rs, "CUSTOMER_ID"));
			ps.setString(17, getValue(rs, "SOURCE_SYSTEM"));
			ps.setBigDecimal(18, getBigDecimal(rs, "EOD_BATCH_ID"));

			// execute query
			ps.executeUpdate();
		} catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		} finally {
			ps = null;
		}
		logger.debug("Leaving");
	}
	

	private void saveBatchLog(int seqNo, long fileId, long ref, String category, String status, String remarks, Date valueDate) throws Exception {

		MapSqlParameterSource source = null;
		StringBuilder sql = null;
		try {
			source = new MapSqlParameterSource();
			source.addValue("ID", getNextId("SEQ_DATA_ENGINE_PROCESS_LOG", true));
			source.addValue("SEQNO", Long.valueOf(seqNo));
			source.addValue("FILEID", fileId);
			source.addValue("REFID1", ref);
			source.addValue("CATEGORY", category);
			source.addValue("STATUS", status);
			source.addValue("REMARKS", remarks.length() > 1000 ? remarks.substring(0, 998) : remarks);
			source.addValue("VALUEDATE",  DateUtil.getSysDate());

			sql = new StringBuilder();
			sql.append(" INSERT INTO DATA_ENGINE_PROCESS_LOG (ID, SEQNO, FILEID, REFID1, CATEGORY, STATUS, REMARKS, VALUEDATE)");
			sql.append(" Values (:ID, :SEQNO, :FILEID, :REFID1, :CATEGORY, :STATUS, :REMARKS, :VALUEDATE)");

			saveBatchLog(source, sql.toString());
		} finally {
			sql = null;
			source = null;
		}
	}
}
