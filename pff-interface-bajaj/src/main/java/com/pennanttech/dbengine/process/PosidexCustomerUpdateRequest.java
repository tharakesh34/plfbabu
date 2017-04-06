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

public class PosidexCustomerUpdateRequest extends DBProcessEngine {

	private static final Logger logger = Logger.getLogger(PosidexCustomerUpdateRequest.class);
	private Connection destConnection;
	private Connection sourceConnection= null;	
	private DataEngineStatus executionStatus = null;
	
	public PosidexCustomerUpdateRequest(DataSource dataSource, String appDBName , DataEngineStatus executionStatus) {
		super(dataSource, appDBName, executionStatus);
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
			destConnection = DataSourceUtils.doGetConnection(appDataSource);
			sourceConnection=getConnection(dbConfiguration);
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
					updateCustomer(resultSet);
					updateDataStatus(resultSet);
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
				updateBatchStatus(ExecutionStatus.S.name(), remarks.toString(), processedCount, processedCount,
						failedCount, totalRecords);
			}
		} catch (Exception e) {
			logger.error("Exception :", e);
			updateBatchStatus(ExecutionStatus.F.name(), e.getMessage(), processedCount, processedCount, failedCount,
					totalRecords);
			remarks.append(e.getMessage());
			executionStatus.setStatus(ExecutionStatus.F.name());
		} finally {
			releaseResorces(null,destConnection,sourceConnection);			
			executionStatus.setRemarks(remarks.toString());
		}

		logger.debug("Leaving");
	}

	private void updateCustomer(ResultSet rs) throws Exception {
		logger.debug("Entering");
		PreparedStatement ps = null;
		try {
			StringBuilder sb = new StringBuilder();
			sb.append(" UPDATE <<TABLE >> SET <<NEWFIELD>>=? WHERE <<CUSTOMER NUMBER>>=?");
				sb.append(" VALUES (?,?)");

			ps = destConnection.prepareStatement(sb.toString());

			ps.setInt(1, getIntValue(rs, "UCIN_NO"));
			ps.setString(2, getValue(rs, "CUSTOMER_NO"));

		
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
			sb.append(" UPDATE PSX_UCIN_REVERSE_FEED SET PROCESSED_FLAG=? WHERE UCIN_NO=?");
				sb.append(" VALUES (?,?)");

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
	private ResultSet getSourceData() throws Exception {
		logger.debug("Entering");

		ResultSet rs = null;
		StringBuilder sql = null;
		try {
			sql = new StringBuilder();
			sql.append(" SELECT UCIN_NO, CUSTOMER_NO from PSX_UCIN_REVERSE_FEED where PROCESSED_FLAG = 'N' ");
			PreparedStatement stmt = sourceConnection.prepareStatement(sql.toString(), ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
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
