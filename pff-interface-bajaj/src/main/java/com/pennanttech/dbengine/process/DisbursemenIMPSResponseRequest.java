package com.pennanttech.dbengine.process;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.pennanttech.dataengine.constants.ExecutionStatus;
import com.pennanttech.dataengine.model.Configuration;
import com.pennanttech.dataengine.model.DBConfiguration;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.dataengine.util.DateUtil;
import com.pennanttech.dbengine.DBProcessEngine;
import com.pennanttech.dbengine.constants.DataEngineDBConstants.Status;

public class DisbursemenIMPSResponseRequest  extends DBProcessEngine {

	private static final Logger logger = Logger.getLogger(DisbursemenIMPSRequest.class);
	
	private Connection destConnection = null;
	private Connection sourceConnection = null;
	private DataEngineStatus executionStatus = null;

	public DisbursemenIMPSResponseRequest(DataSource dataSource, String appDBName, DataEngineStatus executionStatus) {
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
		try {
			saveBatchStatus();

			executionStatus.setRemarks("Loading destination database connection...");
			DBConfiguration dbConfiguration = config.getDbConfiguration();
			sourceConnection = getConnection(dbConfiguration);
			destConnection = DataSourceUtils.doGetConnection(appDataSource);
			
			executionStatus.setRemarks("Fetching data from source table...");
			resultSet = getSourceData();

			if (resultSet != null) {
				resultSet.last();
				totalRecords = resultSet.getRow();
				resultSet.beforeFirst();
				executionStatus.setTotalRecords(totalRecords);
			}
			while (resultSet.next()) {
				executionStatus.setRemarks("Saving data to destination table...");
				try {
					processedCount++;
					saveData(resultSet);
					successCount++;
				} catch (Exception e) {
					failedCount++;
					logger.error("Exception :", e);
				}
				executionStatus.setProcessedRecords(processedCount);
				executionStatus.setSuccessRecords(successCount);
				executionStatus.setFailedRecords(failedCount);
			}

			if (totalRecords > 0) {
				remarks.append("Processed successfully with record count: ");
				remarks.append(totalRecords);
				updateBatchStatus(ExecutionStatus.S.name(), remarks.toString(), processedCount, processedCount, failedCount, totalRecords);
			}
		} catch (Exception e) {
			logger.error("Exception :", e);
			updateBatchStatus(ExecutionStatus.F.name(), e.getMessage(), processedCount, processedCount, failedCount, totalRecords);
			remarks.append(e.getMessage());
			executionStatus.setStatus(ExecutionStatus.F.name());
		} finally {
			releaseResorces(resultSet, destConnection, sourceConnection);
			resultSet = null;
			executionStatus.setRemarks(remarks.toString());
		}

		logger.debug("Leaving");
	}
	
	private void saveData(ResultSet rs) throws Exception {
		logger.debug("Entering");
		PreparedStatement ps = null;
		try {
			StringBuilder sb = new StringBuilder();
			
			sb.append(" INSERT INTO table (");
			ps = destConnection.prepareStatement(sb.toString());
			
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
			sql.append(" SELECT CHANNELPARTNERREFNO, TRANSACTIONID, STATUS, DESCRIPTION, PROCESSFLAG FROM table ");
			sql.append(" WHERE  PROCESSFLAG = ?");

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
