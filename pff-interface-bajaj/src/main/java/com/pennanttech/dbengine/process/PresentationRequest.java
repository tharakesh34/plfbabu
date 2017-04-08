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

public class PresentationRequest extends DBProcessEngine {

	private static final Logger logger = Logger.getLogger(PresentationRequest.class);
	
	private Connection destConnection = null;;
	private Connection sourceConnection = null;
	private DataEngineStatus executionStatus = null;

	public PresentationRequest(DataSource dataSource, String appDBName, DataEngineStatus executionStatus) {
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

	private void saveData(ResultSet rs) throws Exception {
		logger.debug("Entering");
		PreparedStatement ps = null;
		try {
			StringBuilder sb = new StringBuilder();
			sb.append(" INSERT INTO PresentationRequest (");
			sb.append("	PR_KEY, BR_CODE, AGREEMENTNO, MICR_CODE, ACC_TYPE, LEDGER_FOLIO, FINWARE_ACNO, DEST_ACC_HOLDER, PDC_BY_NAME,");
			sb.append(" BANK_NAME, BANK_ADDRESS, EMI_NO, BFL_REF, BATCHID, CHEQUEAMOUNT, PRESENTATIONDATE, RESUB_FLAG, UMRN_NO,");
			sb.append(" IFSC_CODE, SPONSER_BANK_CODE, UTILITY_CODE, MANDATE_START_DT, MANDATE_END_DT, INSTRUMENT_MODE, PRODUCT_CODE,");
			sb.append(" LESSEEID, PICKUP_BATCHID, TXN_TYPE_CODE, SOURCE_CODE, ENTITY_CODE, POSTING_DATETIME, STATUS)");
			sb.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

			ps = destConnection.prepareStatement(sb.toString());

			ps.setString(1, getValue(rs, "PR_KEY"));
			ps.setString(2, getValue(rs, "BR_CODE"));
			ps.setString(3, getValue(rs, "AGREEMENTNO"));
			ps.setString(4, getValue(rs, "MICR_CODE"));
			ps.setInt(5, getIntValue(rs, "ACC_TYPE"));
			ps.setString(6, getValue(rs, "LEDGER_FOLIO"));
			ps.setString(7, getValue(rs, "FINWARE_ACNO"));
			ps.setString(8, getValue(rs, "DEST_ACC_HOLDER"));
			ps.setString(9, getValue(rs, "PDC_BY_NAME"));
			ps.setString(10, getValue(rs, "BANK_NAME"));
			ps.setString(11, getValue(rs, "BANK_ADDRESS"));
			ps.setInt(12, getIntValue(rs, "EMI_NO"));
			ps.setString(13, getValue(rs, "BFL_REF"));
			ps.setString(14, getValue(rs, "BATCHID"));
			ps.setBigDecimal(15, getBigDecimal(rs, "CHEQUEAMOUNT"));
			ps.setDate(16, getDateValue(rs, "PRESENTATIONDATE"));
			ps.setString(17, getValue(rs, "RESUB_FLAG"));
			ps.setString(18, getValue(rs, "UMRN_NO"));
			ps.setString(19, getValue(rs, "IFSC_CODE"));
			ps.setString(20, getValue(rs, "SPONSER_BANK_CODE"));
			ps.setString(21, getValue(rs, "UTILITY_CODE"));
			ps.setDate(22, getDateValue(rs, "MANDATE_START_DT"));
			ps.setDate(23, getDateValue(rs, "MANDATE_END_DT"));
			ps.setString(24, getValue(rs, "INSTRUMENT_MODE"));
			ps.setString(25, getValue(rs, "PRODUCT_CODE"));
			ps.setInt(26, getIntValue(rs, "LESSEEID"));
			ps.setInt(27, getIntValue(rs, "PICKUP_BATCHID"));
			ps.setInt(28, getIntValue(rs, "TXN_TYPE_CODE"));
			ps.setInt(29, getIntValue(rs, "SOURCE_CODE"));
			ps.setInt(30, getIntValue(rs, "ENTITY_CODE"));
			ps.setDate(31, getDateValue(rs, "POSTING_DATETIME"));
			ps.setString(32, Status.N.name());

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
			sourceConnection = DataSourceUtils.doGetConnection(appDataSource);
			sql = new StringBuilder();
			sql.append(" SELECT * from PresentationRequest_Staging ");

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
