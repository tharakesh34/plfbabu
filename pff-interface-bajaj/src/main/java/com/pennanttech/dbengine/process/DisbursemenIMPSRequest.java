package com.pennanttech.dbengine.process;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
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
		long keyValue = 0;
		long partnerBankId = 0;
		String finReferenceNo = null;
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
					keyValue = getLongValue(rs, "PAYMENTID");
					partnerBankId = getLongValue(rs, "PARTNERBANKID");
					finReferenceNo = getValue(rs, "FINREFERENCE");
					saveDisbursement(rs);
					updateDisbursement(keyValue);
					successCount++;
					saveBatchLog(processedCount, fileId, keyValue, partnerBankId, finReferenceNo, "DBImport", "S", "Success.", null);
				} catch (Exception e) {
					failedCount++;
					saveBatchLog(processedCount, fileId, keyValue, partnerBankId, finReferenceNo, "DBImport", "F", e.getMessage(), null);
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
			logger.error("Exception: ", e);
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
			statement.setString(4, getValue(rs, "BENEFICIARYNAME") == null ? " " : getValue(rs, "BENEFICIARYNAME"));
			statement.setString(5, getValue(rs, "BENEFICIARY_MOBILE") == null ? " "
					: getValue(rs, "BENEFICIARY_MOBILE"));// From disb befiniciary
			statement.setString(6, getValue(rs, "CUSTOMER_EMAIL") == null ? " " : getValue(rs, "CUSTOMER_EMAIL"));// From
																													// customer
																													// email
			statement.setString(7, getValue(rs, "IFSC") == null ? " " : getValue(rs, "IFSC")); // FIXME
			statement.setString(8, getValue(rs, "BANKNAME") == null ? " " : getValue(rs, "BANKNAME"));
			statement.setString(9, getValue(rs, "CPPROVINCENAME") == null ? " " : getValue(rs, "CPPROVINCENAME"));
			statement.setString(10, getValue(rs, "PCCITYNAME") == null ? " " : getValue(rs, "PCCITYNAME"));
			statement.setString(11, getValue(rs, "BRANCHDESC") == null ? " " : getValue(rs, "BRANCHDESC"));
			statement.setString(12, getValue(rs, "BENEFICIARYACCNO") == null ? " " : getValue(rs, "BENEFICIARYACCNO"));
			statement.setBigDecimal(13, getBigDecimal(rs, "AMTTOBERELEASED"));
			statement.setString(14,
					getValue(rs, "REMARKS") == null ? " " : StringUtils.substring(getValue(rs, "REMARKS"), 0, 9));
			statement.setString(15, getValue(rs, "PAYMENTID"));
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

			for (int i = 1; i <= paymentId.length; i++) {
				statement.setLong(i, Long.parseLong(paymentId[i - 1]));
			}

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
			sql.append(" SELECT * FROM INT_DISBURSEMENT_EXPORT_VIEW WHERE PAYMENTID IN (");

			for (int i = 0; i < paymentId.length; i++) {
				if (i > 0) {
					sql.append(",");
				}
				sql.append("?");

			}
			sql.append(")");

			statement = sourceConnection.prepareStatement(sql.toString(), ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);

		} catch (SQLException e) {
			logger.error("Exception: ", e);
		}
		return statement;
	}

	private void saveBatchLog(int seqNo, long fileId, long paymentId, long partnerBankId, String finReferenceNo, String category, String status, String remarks,
			Date valueDate) throws Exception {

		MapSqlParameterSource source = null;
		StringBuilder sql = null;
		try {
			source = new MapSqlParameterSource();
			source.addValue("ID", getNextId("SEQ_DATA_ENGINE_PROCESS_LOG", true));
			source.addValue("SEQNO", Long.valueOf(seqNo));
			source.addValue("FILEID", fileId);
			source.addValue("REFID1", paymentId);
			source.addValue("REFID2", partnerBankId);
			source.addValue("REFNO1", finReferenceNo);
			source.addValue("REFNO2", "IMPS");
			source.addValue("CATEGORY", category);
			source.addValue("STATUS", status);
			source.addValue("REMARKS", remarks.length() > 1000 ? remarks.substring(0, 998) : remarks);
			source.addValue("VALUEDATE", DateUtil.getSysDate());

			sql = new StringBuilder();
			sql.append(" INSERT INTO DATA_ENGINE_PROCESS_LOG (ID, SEQNO, FILEID, REFID1, REFID2, REFNO1, REFNO2, CATEGORY, STATUS, REMARKS, VALUEDATE)");
			sql.append(" Values (:ID, :SEQNO, :FILEID, :REFID1, :REFID2, :REFNO1, :REFNO2, :CATEGORY, :STATUS, :REMARKS, :VALUEDATE)");

			saveBatchLog(source, sql.toString());
		} finally {
			sql = null;
			source = null;
		}
	}
}
