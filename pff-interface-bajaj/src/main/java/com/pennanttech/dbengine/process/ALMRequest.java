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
		try {
			saveBatchStatus();

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
			releaseResorces(resultSet,destConnection, sourceConnection);
			executionStatus.setRemarks(remarks.toString());
		}

		logger.debug("Leaving");
	}

	private void saveData(ResultSet rs) throws Exception {
		logger.debug("Entering");
		PreparedStatement ps = null;
		try {
			StringBuilder sb = new StringBuilder();
			sb.append(" INSERT INTO AML_DATA (");
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
			sql.append(" SELECT * from INT_ALM_VIEW  ");
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
