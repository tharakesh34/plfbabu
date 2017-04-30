package com.pennanttech.dbengine.process;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.pennanttech.dataengine.constants.ExecutionStatus;
import com.pennanttech.dataengine.model.Configuration;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.dataengine.util.DateUtil;
import com.pennanttech.dbengine.DBProcessEngine;
import com.pennanttech.dbengine.constants.DataEngineDBConstants.Status;
import com.pennanttech.pff.core.Literal;

public class PresentmentRequest extends DBProcessEngine {

	private static final Logger logger = Logger.getLogger(PresentmentRequest.class);

	private Connection destConnection = null;;
	private Connection sourceConnection = null;
	private DataEngineStatus executionStatus = null;
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public PresentmentRequest(DataSource dataSource, String appDBName, DataEngineStatus executionStatus) {
		super(dataSource, appDBName, executionStatus);
		setDataSource(dataSource);
		this.executionStatus = executionStatus;
	}

	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	public void process(long userId, Configuration config, String presentmentIds) {
		logger.debug("Entering");

		executionStatus.setStartTime(DateUtil.getSysDate());
		executionStatus.setName(config.getName());
		executionStatus.setUserId(userId);
		executionStatus.setReference(config.getName());
		executionStatus.setStatus(ExecutionStatus.I.name());
		executionStatus.setRemarks("Loading configuration..");

		PreparedStatement statement = null;
		ResultSet resultSet = null;
		StringBuilder remarks = new StringBuilder();
		long fileId;
		try {
			executionStatus.setFileName(getFileName(config.getName()));
			saveBatchStatus();
			fileId = executionStatus.getId();

			executionStatus.setRemarks("Loading destination database connection...");
			destConnection = getConnection(config);
			sourceConnection = DataSourceUtils.doGetConnection(dataSource);
			executionStatus.setRemarks("Fetching data from source table...");

			statement = getStatement(presentmentIds);
			resultSet = getResultSet(presentmentIds, statement);

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
					saveBatchLog(processedCount, fileId, processedCount, "DBImport", "S", "Success.", null);
				} catch (Exception e) {
					failedCount++;
					logger.error("Exception :", e);
					saveBatchLog(processedCount, fileId, processedCount, "DBImport", "F", e.getMessage(), null);
				}
				executionStatus.setProcessedRecords(processedCount);
				executionStatus.setSuccessRecords(successCount);
				executionStatus.setFailedRecords(failedCount);
			}
			if (totalRecords > 0) {
				updatePresentmentHeader(presentmentIds, 4);
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

			try {
				if (resultSet != null) {
					resultSet.close();
				}
			} catch (Exception e) {
				logger.info("Exception :", e);
			}
			releaseResorces(resultSet, sourceConnection, sourceConnection);
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
			sb.append(" INSERT INTO PDC_CONSL_EMI_DTL (");
			sb.append("	PR_KEY, BR_CODE, AGREEMENTNO, MICR_CODE, ACC_TYPE, LEDGER_FOLIO, FINWARE_ACNO, DEST_ACC_HOLDER, PDC_BY_NAME,");
			sb.append(" BANK_NAME, BANK_ADDRESS, EMI_NO, BFL_REF, BATCHID, CHEQUEAMOUNT, PRESENTATIONDATE, RESUB_FLAG, UMRN_NO,");
			sb.append(" IFSC_CODE, SPONSER_BANK_CODE, UTILITY_CODE, MANDATE_START_DT, MANDATE_END_DT, INSTRUMENT_MODE, PRODUCT_CODE,");
			sb.append(" LESSEEID, PICKUP_BATCHID, ENTITY_CODE, POSTING_DATETIME, STATUS)");
			sb.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

			ps = destConnection.prepareStatement(sb.toString());

			ps.setString(1, String.valueOf(getLongValue(rs, "PRESENTMENTID")));
			ps.setString(2, getValue(rs, "BRANCHSWIFTBRNCDE"));
			ps.setString(3, getValue(rs, "FINREFERENCE"));
			ps.setString(4, getValue(rs, "MICR"));
			ps.setInt(5, getIntValue(rs, "ACCTYPE"));
			ps.setString(6, "000");// Always 000
			ps.setString(7, getValue(rs, "ACCNUMBER"));
			ps.setString(8, getValue(rs, "CUSTSHRTNAME"));
			ps.setString(9, getValue(rs, "ACCHOLDERNAME"));
			
			ps.setString(10, getValue(rs, "BANKNAME"));
			ps.setString(11, getValue(rs, "BRANCHDESC"));
			ps.setInt(12, getIntValue(rs, "EMINO"));

			String mnadteType = getValue(rs, "MANDATETYPE");
			if (StringUtils.equals(mnadteType, "ECS") || StringUtils.equals(mnadteType, "DDM")) {
				ps.setString(13, "405");
			} else {
				ps.setString(11, getValue(rs, "BRANCHSWIFTBRNCDE"));
			}

			ps.setString(14, getValue(rs, "PresentmentRef"));
			ps.setBigDecimal(15, getBigDecimal(rs, "PRESENTMENTAMT"));
			ps.setDate(16, getDateValue(rs, "PRESENTMENTDATE"));
			ps.setString(17, Status.N.name());
			ps.setString(18, getValue(rs, "MANDATEREF"));

			ps.setString(19, getValue(rs, "IFSC"));
			ps.setString(20, getValue(rs, "PARTNERBANKCODE"));
			ps.setString(21, getValue(rs, "UTILITYCODE"));
			ps.setDate(22, getDateValue(rs, "STARTDATE"));
			ps.setDate(23, getDateValue(rs, "EXPIRYDATE"));

			if (StringUtils.equals(mnadteType, "ECS")) {//FIXME consants
				ps.setString(24, "E");
			} else if (StringUtils.equals(mnadteType, "DDM")) {
				ps.setString(24, "D");
			} else if (StringUtils.equals(mnadteType, "NACH")) {
				ps.setString(24, "Z");
			}
			ps.setString(25, getValue(rs, "FINTYPE"));
			ps.setInt(26, getIntValue(rs, "CUSTID"));
			ps.setInt(27, -1);
			// TXN_TYPE_CODE
			// SOURCE_CODE
			ps.setInt(28, 1);//getIntValue(rs, "ENTITYCODE")
			ps.setDate(29, com.pennanttech.pff.core.util.DateUtil.getSqlDate(com.pennanttech.pff.core.util.DateUtil.getSysDate()));
			ps.setString(30, Status.N.name());//FIXME remove fileld

			// execute query
			ps.executeUpdate();
		} catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		} finally {
			if (ps != null) {
				ps.close();
				ps = null;
			}
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

	private PreparedStatement getStatement(String presentmentIds) throws Exception {
		PreparedStatement statement = null;

		String[] presentmentId = presentmentIds.split(",");

		StringBuilder sql = null;
		try {
			sql = new StringBuilder();
			sql.append(" SELECT  T2.FINBRANCH, T1.FINREFERENCE, T4.MICR, T3.ACCTYPE, ");
			sql.append(" T3.ACCNUMBER, T5.CUSTSHRTNAME, T3.ACCHOLDERNAME, T6.BANKNAME, ");
			sql.append(" T6.BANKNAME, T1.PRESENTMENTID, T1.PRESENTMENTAMT,");
			sql.append(" T0.PRESENTMENTDATE, T3.MANDATEREF, T4.IFSC, ");
			sql.append(" T7.PARTNERBANKCODE, T7.UTILITYCODE, T3.STARTDATE, T3.EXPIRYDATE, T3.MANDATETYPE, ");
			sql.append(" T2.FINTYPE, T2.CUSTID , T7.PARTNERBANKCODE, T1.EMINO, T4.BRANCHDESC, T4.BRANCHCODE, T1.ID, T1.PresentmentRef, ");
			sql.append(" T8.BRANCHSWIFTBRNCDE, T9.FINDIVISION ENTITYCODE FROM PRESENTMENTHEADER T0 ");
			sql.append(" INNER JOIN PRESENTMENTDETAILS T1 ON T0.ID = T1.PRESENTMENTID ");
			sql.append(" INNER JOIN FINANCEMAIN T2 ON T1.FINREFERENCE = T2.FINREFERENCE ");
			sql.append(" INNER JOIN CuSTOMERS T5 ON T5.CUSTID = T2.CUSTID ");
			sql.append(" INNER JOIN MANDATES T3 ON T2.MANDATEID = T3.MANDATEID ");
			sql.append(" INNER JOIN BANKBRANCHES T4 ON T3.BANKBRANCHID = T4.BANKBRANCHID ");
			sql.append(" INNER JOIN BMTBANKDETAIL T6 ON T4.BANKCODE = T6.BANKCODE ");
			sql.append(" INNER JOIN PARTNERBANKS T7 ON T7.PARTNERBANKID = T0.PARTNERBANKID ");
			sql.append(" INNER JOIN RMTBRANCHES T8 ON T8.BRANCHCODE = T2.FINBRANCH ");
			sql.append(" INNER JOIN RMTFINANCETYPES T9 ON T9.FINTYPE = T2.FINTYPE");
			sql.append(" WHERE T1.PRESENTMENTID IN (");

			for (int i = 0; i < presentmentId.length; i++) {
				if (i > 0) {
					sql.append(",");
				}
				sql.append("?");
			}
			sql.append(")");
			sql.append(" AND (T1.EXCLUDEREASON = 0)");
			statement = sourceConnection.prepareStatement(sql.toString(), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		} catch (SQLException e) {
			logger.error("Exception: ", e);
		}
		return statement;
	}

	private void saveBatchLog(int seqNo, long fileId, long ref, String category, String status, String remarks,
			Date valueDate) throws Exception {

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
	
	public void updatePresentmentHeader(String ids, int manualEcclude) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = null;
		MapSqlParameterSource source = null;

		sql = new StringBuilder();
		sql.append(" update PRESENTMENTHEADER Set STATUS = :STATUS Where ID IN (:ID) ");
		logger.trace(Literal.SQL + sql.toString());

		source = new MapSqlParameterSource();
		source.addValue("STATUS", manualEcclude);
		source.addValue("ID", Arrays.asList(ids));

		try {
			this.namedParameterJdbcTemplate.update(sql.toString(), source);
		} catch (Exception e) {
			logger.error("Exception :", e);
			throw e;
		}
		logger.debug(Literal.LEAVING);
	}
}
