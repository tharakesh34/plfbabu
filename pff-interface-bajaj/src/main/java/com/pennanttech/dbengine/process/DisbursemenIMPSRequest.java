package com.pennanttech.dbengine.process;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.pennanttech.dataengine.constants.ExecutionStatus;
import com.pennanttech.dataengine.model.Configuration;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.dataengine.util.DateUtil;
import com.pennanttech.dbengine.DBProcessEngine;
import com.pennanttech.pff.baja.InterfaceConstants.Status;
import com.pennanttech.pff.core.Literal;

public class DisbursemenIMPSRequest extends DBProcessEngine {

	private static final Logger logger = Logger.getLogger(DisbursemenIMPSRequest.class);

	private Connection destConnection = null;
	private Connection sourceConnection = null;
	private DataEngineStatus executionStatus = null;

	public DisbursemenIMPSRequest(DataSource dataSource, String appDBName, DataEngineStatus executionStatus) {
		super(dataSource, appDBName, executionStatus);
		this.executionStatus = executionStatus;
	}

	public void process(long userId, Configuration config, String paymentIds) {
		logger.debug("Entering");

		executionStatus.setStartTime(DateUtil.getSysDate());
		executionStatus.setName(config.getName());
		executionStatus.setUserId(userId);
		executionStatus.setReference(config.getName());
		executionStatus.setStatus(ExecutionStatus.I.name());
		executionStatus.setRemarks("Loading configuration..");

		PreparedStatement statement = null;
		ResultSet rs = null;
		StringBuilder remarks = new StringBuilder();
		long id = 0;
		long disbursementId = 0;
		long fileId;
		try {

			executionStatus.setFileName(getFileName(config.getName()));
			saveBatchStatus();
			fileId = executionStatus.getId();

			executionStatus.setRemarks("Loading destination database connection...");
			destConnection = getConnection(config);
			sourceConnection = DataSourceUtils.doGetConnection(dataSource);

			executionStatus.setRemarks("Fetching data from source table...");
			statement = getStatement(paymentIds);
			rs = getResultSet(paymentIds, statement);

			if (rs != null) {
				rs.last();
				totalRecords = rs.getRow();
				rs.beforeFirst();
				executionStatus.setTotalRecords(totalRecords);
			}
			while (rs.next()) {
				executionStatus.setRemarks("Saving data to destination table...");
				try {
					processedCount++;
					disbursementId = getLongValue(rs, "DISBURSEMENT_ID");
					id = getLongValue(rs, "ID");
					saveDisbursement(rs);
					updateDisbursementRequest(id, fileId);
					updateDisbursement(disbursementId);
					successCount++;
					saveBatchLog(processedCount, fileId, id, "S", "Success");
				} catch (Exception e) {
					failedCount++;
					saveBatchLog(processedCount, fileId, id, "F", e.getMessage());
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
				updateBatchStatus(ExecutionStatus.S.name(), remarks.toString(), processedCount, successCount,
						failedCount, totalRecords);
			} else {
				remarks.append("No records found for the selected configuration.");
				updateBatchStatus(ExecutionStatus.F.name(), remarks.toString(), processedCount, successCount,
						failedCount, totalRecords);
			}
		} catch (Exception e) {
			logger.error("Exception :", e);
			updateBatchStatus(ExecutionStatus.F.name(), e.getMessage(), processedCount, successCount, failedCount,
					totalRecords);
			remarks.append(e.getMessage());
			executionStatus.setStatus(ExecutionStatus.F.name());
		} finally {
			releaseResorces(rs, statement, destConnection);
			rs = null;
			executionStatus.setRemarks(remarks.toString());
		}

		logger.debug("Leaving");
	}

	private void updateDisbursement(long paymentId) throws Exception {
		PreparedStatement statement = null;
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("UPDATE FINADVANCEPAYMENTS  SET STATUS  =  ? WHERE  PAYMENTID = ?");

			statement = sourceConnection.prepareStatement(sb.toString());
			statement.setString(1, "AC");
			statement.setLong(2, paymentId);

			// execute query
			statement.executeUpdate();

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw e;
		} finally {
			if (statement != null) {
				statement.close();
				statement = null;
			}
		}
	}
	
	private int updateDisbursementRequest(long id, long fileId) throws Exception {
		PreparedStatement statement = null;
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("UPDATE DISBURSEMENT_REQUESTS  SET STATUS  =  ?, BATCH_ID  = ? WHERE ID = ?");

			statement = sourceConnection.prepareStatement(sb.toString());
			statement.setString(1, "AC");
			statement.setLong(2, fileId);
			statement.setLong(3, id);

			// execute query
			return statement.executeUpdate();

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw e;
		} finally {
			if (statement != null) {
				statement.close();
				statement = null;
			}
		}
	}

	private void saveDisbursement(ResultSet rs) throws Exception {
		logger.debug("Entering");
		PreparedStatement statement = null;
		try {
			StringBuilder sb = new StringBuilder();
			sb.append(" INSERT INTO INT_DSBIMPS_REQUEST (");
			sb.append("	CHANNELPARTNERID, BCAGENTID, SENDERID, RECEIVERNAME, RECEIVERMOBILENO, RECEIVEREMAILID, IFSCODE ,BANK");
			sb.append(" , RECEVIERBANKSTATE, RECEVIERBANKCITY, RECEVIERBANKBRANCH, RECEVIERACCOUNTNUMBER, AMOUNT");
			sb.append(" , REMARKS, CHANNELPARTNERREFNO, PICKUPFLAG, AGREEMENTID)");
			sb.append(" VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

			statement = destConnection.prepareStatement(sb.toString());

			statement.setString(1, null);
			statement.setString(2, null);
			statement.setString(3, null);
			statement.setString(4, getValue(rs, "BENFICIARY_NAME") == null ? " " : getValue(rs, "BENFICIARY_NAME"));
			statement.setString(5, getValue(rs, "BENFICIARY_MOBILE") == null ? " "
					: getValue(rs, "BENFICIARY_MOBILE"));// From disb befiniciary
			statement.setString(6, getValue(rs, "CUSTOMER_EMAIL") == null ? " " : getValue(rs, "CUSTOMER_EMAIL"));// From
																													// customer
																													// email
			statement.setString(7, getValue(rs, "IFSC_CODE") == null ? " " : getValue(rs, "IFSC_CODE")); // FIXME
			statement.setString(8, getValue(rs, "BENFICIARY_BANK") == null ? " " : getValue(rs, "BENFICIARY_BANK"));
			statement.setString(9, getValue(rs, "BENFICIARY_BRANCH_STATE") == null ? " " : getValue(rs, "BENFICIARY_BRANCH_STATE"));
			statement.setString(10, getValue(rs, "BENFICIARY_BRANCH_CITY") == null ? " " : getValue(rs, "BENFICIARY_BRANCH_CITY"));
			statement.setString(11, getValue(rs, "BENFICIARY_BRANCH") == null ? " " : getValue(rs, "BENFICIARY_BRANCH"));
			statement.setString(12, getValue(rs, "BENFICIARY_ACCOUNT") == null ? " " : getValue(rs, "BENFICIARY_ACCOUNT"));
			statement.setBigDecimal(13, getBigDecimal(rs, "DISBURSEMENT_AMOUNT"));
			statement.setString(14,
					getValue(rs, "REMARKS") == null ? " " : StringUtils.substring(getValue(rs, "REMARKS"), 0, 9));
			statement.setString(15, getValue(rs, "ID"));
			statement.setString(16, Status.N.name());
			statement.setBigDecimal(17, BigDecimal.ZERO);// Discuss with required for finreference // Remove first 6
															// chars
			statement.executeUpdate();

		} catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		} finally {
			releaseResorces(statement);
		}
		logger.debug("Leaving");
	}

	private ResultSet getResultSet(String paymentIds, PreparedStatement statement) throws Exception {
		String[] paymentId = paymentIds.split(",");

		ResultSet rs = null;
		try {
			int i = 0;
			for (i = 1; i <= paymentId.length; i++) {
				statement.setLong(i, Long.parseLong(paymentId[i - 1]));
			}
			
			statement.setString(i, "APPROVED");

			rs = statement.executeQuery();

		} catch (Exception e) {
			logger.error("Exception: ", e);
		}
		return rs;
	}

	private PreparedStatement getStatement(String paymentIds) throws Exception {
		PreparedStatement statement = null;

		String[] paymentId = paymentIds.split(",");

		StringBuilder sql = null;
		try {
			sql = new StringBuilder();
			sql.append(" SELECT * FROM DISBURSEMENT_REQUESTS WHERE DISBURSEMENT_ID IN (");

			for (int i = 0; i < paymentId.length; i++) {
				if (i > 0) {
					sql.append(",");
				}
				sql.append("?");

			}
			sql.append(") AND STATUS = ?");

			statement = sourceConnection.prepareStatement(sql.toString(), ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);

		} catch (SQLException e) {
			logger.error("Exception: ", e);
		}
		return statement;
	}

	private void saveBatchLog(int seqNo, long fileId, long paymentId, String status, String remarks) throws Exception {
		MapSqlParameterSource source = null;
		StringBuilder sql = null;
		try {
			source = new MapSqlParameterSource();
			source.addValue("ID", getNextId("SEQ_DATA_ENGINE_PROCESS_LOG", true));
			source.addValue("SEQNO", Long.valueOf(seqNo));
			source.addValue("FILEID", fileId);
			source.addValue("REFID1", paymentId);
			
			source.addValue("CATEGORY", "DB_UPLOAD");
			source.addValue("STATUS", status);
			source.addValue("REMARKS", remarks.length() > 1000 ? remarks.substring(0, 998) : remarks);
			source.addValue("VALUEDATE", DateUtil.getSysDate());

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
