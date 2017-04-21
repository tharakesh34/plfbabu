package com.pennanttech.dbengine.process;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.pennanttech.dataengine.constants.ExecutionStatus;
import com.pennanttech.dataengine.model.Configuration;
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
			sb.append(" update FinAdvancePayments  set STATUS  =  ? Where  PaymentId = ? ");
			
			ps = sourceConnection.prepareStatement(sb.toString());
			ps.setString(1, Status.AC.name());
			ps.setLong(2, getLongValue(rs, "PaymentId"));
			
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
			sb.append("	CHANNELPARTNERID, BCAGENTID, SENDERID, RECEIVERNAME, RECEIVERMOBILENO, RECEIVEREMAILID, IFSCODE ,BANK");
			sb.append(" , RECEVIERBANKSTATE, RECEVIERBANKCITY, RECEVIERBANKBRANCH, RECEVIERACCOUNTNUMBER, AMOUNT");
			sb.append(" , REMARKS, CHANNELPARTNERREFNO, PICKUPFLAG, AGREEMENTID)");
			sb.append(" VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

			ps = destConnection.prepareStatement(sb.toString());

			ps.setString(1, null);
			ps.setString(2, null);
			ps.setString(3, null);
			ps.setString(4, getValue(rs, "BENEFICIARYNAME"));
			ps.setString(5, getValue(rs, "BENEFICIARY_MOBILE") == null ? " " : getValue(rs, "BENEFICIARY_MOBILE"));//From disb befiniciary 
			ps.setString(6, getValue(rs, "CUSTOMER_EMAIL") == null ? " " : getValue(rs, "CUSTOMER_EMAIL"));//From customer email
			ps.setString(7, getValue(rs, "IFSC"));
			ps.setString(8, getValue(rs, "BANKNAME"));
			ps.setString(9, getValue(rs, "CPPROVINCENAME") == null ? " " : getValue(rs, "CPPROVINCENAME"));
			ps.setString(10, getValue(rs, "PCCITYNAME") == null ? " " : getValue(rs, "PCCITYNAME"));
			ps.setString(11, getValue(rs, "BRANCHDESC") == null ? " " : getValue(rs, "BRANCHDESC"));
			ps.setString(12, getValue(rs, "BENEFICIARYACCNO") == null ? " " : getValue(rs, "BENEFICIARYACCNO"));
			ps.setBigDecimal(13, getBigDecimal(rs, "AMTTOBERELEASED"));
			ps.setString(14, getValue(rs, "REMARKS") == null ? " " : StringUtils.substring(getValue(rs, "REMARKS"), 0, 9));
			ps.setString(15, getValue(rs, "PAYMENTID"));
			ps.setString(16, Status.N.name());
			ps.setBigDecimal(17, BigDecimal.ZERO);//Discuss with required for finreference // Remove first 6 chars
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
			sql.append(" SELECT * FROM INT_DISBURSEMENT_EXPORT_VIEW ");

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
