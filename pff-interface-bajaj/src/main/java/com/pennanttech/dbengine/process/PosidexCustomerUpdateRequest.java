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
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.dataengine.util.DateUtil;
import com.pennanttech.dbengine.DBProcessEngine;
import com.pennanttech.dbengine.constants.DataEngineDBConstants.Status;

public class PosidexCustomerUpdateRequest extends DBProcessEngine {

	private static final Logger logger = Logger.getLogger(PosidexCustomerUpdateRequest.class);
	private Connection destConnection;
	private Connection sourceConnection= null;	
	private DataEngineStatus executionStatus = null;
	
	public PosidexCustomerUpdateRequest(DataSource dataSource, String appDBName , DataEngineStatus executionStatus) {
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
			destConnection = DataSourceUtils.doGetConnection(dataSource);
			sourceConnection = getConnection(config);
			executionStatus.setRemarks("Fetching data from source table...");
			
			resultSet = getSourceData();

			if (resultSet != null) {
				resultSet.last();
				totalRecords = resultSet.getRow();
				resultSet.beforeFirst();
				executionStatus.setTotalRecords(totalRecords);
			}
			while (resultSet.next()) {
				executionStatus.setRemarks("Saving data in to destination table...");
				try {
					processedCount++;
					updateCustomer(resultSet);
					updateDataStatus(resultSet);
					
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
			releaseResorces(null, destConnection, sourceConnection);		
			executionStatus.setRemarks(remarks.toString());
		}
		logger.debug("Leaving");
	}

	
	private ResultSet getSourceData() throws Exception {
		logger.debug("Entering");

		ResultSet rs = null;
		StringBuilder sql = null;
		PreparedStatement ps = null;
		try {
			sql = new StringBuilder();
			sql.append(" SELECT UCIN_NO, CUSTOMER_ID FROM PSX_UCIN_REVERSE_FEED WHERE PROCESSED_FLAG = ? ");

			ps = sourceConnection.prepareStatement(sql.toString(), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ps.setInt(1, getIntValue(rs, Status.N.name()));
			
			rs = ps.executeQuery();
			
		} catch (SQLException e) {
			logger.error("Exception {}", e);
			throw e;
		} finally {
			ps = null;
			sql = null;
		}
		logger.debug("Leaving");
		return rs;
	}
	
	private void updateCustomer(ResultSet rs) throws Exception {
		logger.debug("Entering");
		
		PreparedStatement ps = null;
		try {
			StringBuilder sb = new StringBuilder();
			sb.append(" UPDATE CUSTOMERS SET CUSTADDLVAR1 = ? WHERE CustCIF = ? ");

			ps = destConnection.prepareStatement(sb.toString());
			ps.setString(1, getValue(rs, "UCIN_NO"));
			ps.setString(2, getValue(rs, "CUSTOMER_ID"));

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

	private void updateDataStatus(ResultSet rs) throws Exception {
		logger.debug("Entering");
		
		PreparedStatement ps = null;
		try {
			StringBuilder sb = new StringBuilder();
			sb.append(" UPDATE PSX_UCIN_REVERSE_FEED SET PROCESSED_FLAG = ? WHERE UCIN_NO = ? ");

			ps = destConnection.prepareStatement(sb.toString());
			ps.setString(1, Status.Y.name());
			ps.setString(2, getValue(rs, "UCIN_NO"));
		
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
