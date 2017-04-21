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
import com.pennanttech.dbengine.DBProcessEngine;
import com.pennanttech.dbengine.util.DateUtil;

public class ALMRequest extends DBProcessEngine {

	private static final Logger logger = Logger.getLogger(ALMRequest.class);
	private Connection destConnection;
	private Connection sourceConnection= null;
	private DataEngineStatus executionStatus = null;
	
	public ALMRequest(DataSource dataSource, String appDBName, DataEngineStatus executionStatus) {
		super(dataSource, appDBName, executionStatus);
		this.executionStatus = executionStatus;
	}

	public void process(long userId,  Configuration config) {
		logger.debug("Entering");

		executionStatus.setStartTime(DateUtil.getSysDate());
		executionStatus.setName(config.getName());
		executionStatus.setUserId(userId);
		executionStatus.setReference(config.getName());
		executionStatus.setStatus(ExecutionStatus.I.name());
		executionStatus.setRemarks("Loading configuration..");

		ResultSet resultSet = null;
		StringBuilder remarks = new StringBuilder();
		long keyValue = 0;
		long fileId;
		try {
			
			executionStatus.setFileName(getFileName(config.getName()));
			saveBatchStatus();
			fileId = executionStatus.getId();
			
			executionStatus.setRemarks("Loading destination database connection...");
			destConnection = getConnection(config);
			sourceConnection = DataSourceUtils.doGetConnection(dataSource);
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
					keyValue = getIntValue(resultSet, "AGREEMENTID");
					saveData(resultSet);
					successCount++;
					saveBatchLog(processedCount, fileId, keyValue, "DBImport", "S", "Success.", null);
				} catch (Exception e) {
					failedCount++;
					saveBatchLog(processedCount, fileId, keyValue, "DBImport", "F", e.getMessage(), null);
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
			releaseResorces(resultSet, destConnection, sourceConnection);
			executionStatus.setRemarks(remarks.toString());
		}
		logger.debug("Leaving");
	}

	private void saveData(ResultSet rs) throws Exception {
		logger.debug("Entering");
		PreparedStatement ps = null;
		try {
			StringBuilder sb = new StringBuilder();
			sb.append(" INSERT INTO ALM (");
			sb.append("	AGREEMENTID, AGREEMENTNO, PRODUCTFLAG, NPA_STAGEID, INSTLAMT, PRINCOMP,INTCOMP ,DUEDATE, ");
			sb.append(" ACCRUEDAMT, ACCRUEDON, CUMULATIVE_ACCRUAL_AMT, ADVFLAG)");
			sb.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");

			ps = destConnection.prepareStatement(sb.toString());

			ps.setInt(1, getIntValue(rs, "AGREEMENTID"));
			ps.setString(2, getValue(rs, "AGREEMENTNO"));
			ps.setString(3, getValue(rs, "PRODUCTFLAG"));
			ps.setString(4, getValue(rs, "NPA_STAGEID"));
			ps.setBigDecimal(5, getBigDecimal (rs, "INSTLAMT"));
			ps.setBigDecimal(6, getBigDecimal(rs, "PRINCOMP"));
			ps.setBigDecimal(7, getBigDecimal(rs, "INTCOMP"));
			ps.setDate(8, getDateValue(rs, "DUEDATE"));
			ps.setBigDecimal(9, getBigDecimal(rs, "ACCRUEDAMT"));
			ps.setDate(10, getDateValue(rs, "ACCRUEDON"));
			ps.setBigDecimal(11, getBigDecimal(rs, "CUMULATIVE_ACCRUAL_AMT"));
			ps.setString(12, getValue(rs, "ADVFLAG"));
		
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

	private ResultSet getSourceData() throws Exception {
		logger.debug("Entering");

		ResultSet rs = null;
		StringBuilder sql = null;
		try {
			sql = new StringBuilder();
			sql.append(" SELECT * from INT_ALM_VIEW  Where DUEDATE >= ? AND DUEDATE <= ? ");
			PreparedStatement stmt = sourceConnection.prepareStatement(sql.toString(), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			stmt.setDate(1, DateUtil.getMonthStartDate(DateUtil.getPrevMonthDate()));
			stmt.setDate(2, DateUtil.getMonthEndDate(DateUtil.getPrevMonthDate()));
			
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
