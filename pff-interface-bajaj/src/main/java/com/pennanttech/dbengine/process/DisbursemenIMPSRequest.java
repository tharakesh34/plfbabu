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

public class DisbursemenIMPSRequest extends DBProcessEngine {

	private static final Logger logger = Logger.getLogger(DisbursemenIMPSRequest.class);
	
	private Connection destConnection = null;
	private Connection sourceConnection = null;
	private DataEngineStatus executionStatus = null;
	
	public DisbursemenIMPSRequest(DataSource dataSource, String appDBName, DataEngineStatus executionStatus) {
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
			destConnection = getConnection(dbConfiguration);
			sourceConnection = DataSourceUtils.doGetConnection(appDataSource);
			
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
					updateData(resultSet);
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
			}
		} catch (Exception e) {
			logger.error("Exception :", e);
			updateBatchStatus(ExecutionStatus.F.name(), e.getMessage(), processedCount, successCount, failedCount, totalRecords);
			remarks.append(e.getMessage());
			executionStatus.setStatus(ExecutionStatus.F.name());
		} finally {
			releaseResorces(resultSet, destConnection, sourceConnection);
			resultSet = null;
			executionStatus.setRemarks(remarks.toString());
		}

		logger.debug("Leaving");
	}

	
	private void updateData(ResultSet rs) throws Exception {
		
		logger.debug("Entering");
		PreparedStatement ps = null;
		try {
			StringBuilder sb = new StringBuilder();
			sb.append(" update FinAdvancePayments  set STATUS  =  ? Where AGREEMENTID = ?  AND  PaymentSeq = ? AND  DisbSeq = ? ");
			
			ps = sourceConnection.prepareStatement(sb.toString());
			ps.setString(1, Status.AC.name());
			ps.setString(2, getValue(rs, "AGREEMENTID"));
			ps.setInt(3, getIntValue(rs, "PaymentSeq"));
			ps.setInt(4, getIntValue(rs, "DisbSeq"));
			
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
	
	private void saveData(ResultSet rs) throws Exception {
		logger.debug("Entering");
		PreparedStatement ps = null;
		try {
			StringBuilder sb = new StringBuilder();
			sb.append(" INSERT INTO INT_DSBIMPS_REQUEST (");
			sb.append("	CHANNELPARTNERID, BCAGENTID, SENDERID, RECEIVERNAME, RECEIVER_MOBILE_NO, RECEIVEREMAILID,IFSCODE ,BANK ");
			sb.append(" RECEVIER_BANK_STATE, RECEVIER_BANK_CITY, RECEVIER_BANK_BRANCH, RECEVIERACCOUNTNUMBER, AMOUNT, REMARKS,");
			sb.append(" REMARKS, CHANNELPARTNERREFNO, PICKUP_FLAG, AGREEMENTID)");
			sb.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");

			ps = destConnection.prepareStatement(sb.toString());

			ps.setString(1, getValue(rs, "CHANNELPARTNERID"));
			ps.setString(2, getValue(rs, "BCAGENTID"));
			ps.setString(3, getValue(rs, "SENDERID"));
			ps.setString(4, getValue(rs, "RECEIVERNAME"));
			ps.setString(5, getValue (rs, "RECEIVER_MOBILE_NO"));
			ps.setString(6, getValue(rs, "RECEIVEREMAILID"));
			ps.setString(7, getValue(rs, "IFSCODE"));
			ps.setString(8, getValue(rs, "BANK"));
			ps.setString(9, getValue(rs, "RECEVIER_BANK_STATE"));
			ps.setString(10, getValue(rs, "RECEVIER_BANK_CITY"));
			ps.setString(11, getValue(rs, "RECEVIER_BANK_BRANCH"));
			ps.setString(12, getValue(rs, "RECEVIERACCOUNTNUMBER"));
			ps.setBigDecimal(13, getBigDecimal(rs, "AMOUNT"));
			ps.setString(14, getValue(rs, "CHANNELPARTNERREFNO"));
			ps.setString(15, getValue(rs, "N"));
			ps.setBigDecimal(16, getBigDecimal(rs, "AGREEMENTID"));
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
			sql.append(" SELECT * FROM INT_DISB_IMPS_VIEW ");

			PreparedStatement stmt = sourceConnection.prepareStatement(sql.toString(), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
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
