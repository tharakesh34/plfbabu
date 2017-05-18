package com.pennanttech.dbengine.process;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.TransactionStatus;

import com.pennanttech.dataengine.DatabaseDataEngine;
import com.pennanttech.pff.baja.BajajInterfaceConstants.Status;
import com.pennanttech.pff.core.App;
import com.pennanttech.pff.core.Literal;

public class DisbursemenIMPSResponseProcess extends DatabaseDataEngine {
	private static final Logger logger = Logger.getLogger(DisbursemenIMPSResponseProcess.class);

	private String status;

	public DisbursemenIMPSResponseProcess(DataSource dataSource, String status, Date valueDate) {
		super(dataSource, App.DATABASE.name());
		this.status = status;
		this.valueDate = valueDate;
	}

	@Override
	protected void processData() {
		logger.debug(Literal.ENTERING);

		executionStatus.setRemarks("Loading data..");
		MapSqlParameterSource parmMap;
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT CHANNELPARTNERREFNO, TRANSACTIONID, STATUS, DESCRIPTION, PROCESSFLAG FROM INT_DSBIMPS_RESPONSE ");
		sql.append(" WHERE  PROCESSFLAG = :PROCESSFLAG ");

		parmMap = new MapSqlParameterSource();
		parmMap.addValue("PROCESSFLAG", status);

		jdbcTemplate.query(sql.toString(), parmMap, new ResultSetExtractor<Integer>() {
			TransactionStatus txnStatus = null;
			long headerId = 0;

			@Override
			public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
				if (rs.getRow() > 0) {
					headerId = saveHeader(config.getId(), totalRecords);
				}
				while (rs.next()) {
					executionStatus.setRemarks("processing the record " + ++totalRecords);
					processedCount++;
					txnStatus = transManager.getTransaction(transDef);
					try {
						processRecord(rs, headerId);
						successCount++;

						transManager.commit(txnStatus);

					} catch (Exception e) {
						logger.error(Literal.ENTERING);
						transManager.rollback(txnStatus);
						failedCount++;
						saveBatchLog(rs.getString("AGREEMENTID"), "F", e.getMessage());
					} finally {
						txnStatus.flush();
						txnStatus = null;
					}
				}
				return totalRecords;
			}
		});

		logger.debug(Literal.LEAVING);
	}

	private void processRecord(ResultSet rs, long headerId) throws Exception {

		String channelRefNum = (String) rs.getObject("CHANNELPARTNERREFNO");
		MapSqlParameterSource insertSource = new MapSqlParameterSource();
		MapSqlParameterSource updateSource = new MapSqlParameterSource();

		if (channelRefNum.isEmpty()) {
			throw new Exception("Channnel Partener RefNo not available in source table.");
		}

		if (isChannelRefExists(channelRefNum)) {
			insertSource.addValue("TransactionStatus", Status.E.name());
			updateSource.addValue("PROCESSFLAG", Status.Y.name());
		} else {
			insertSource.addValue("TransactionStatus", Status.R.name());
			updateSource.addValue("PROCESSFLAG", Status.R.name());
		}

		insertSource.addValue("ID", getNextId("DISB_PAY_DET_SEQ", true));
		insertSource.addValue("HeaderID", headerId);
		insertSource.addValue("DisbursementReference", Long.valueOf(channelRefNum));
		insertSource.addValue("DisbursementAmount", 0.00);// FIXME
		insertSource.addValue("TransactionDate", new Timestamp(System.currentTimeMillis()));// FIXME
		insertSource.addValue("BankReferenceNumber", rs.getObject("TRANSACTIONID"));
		insertSource.addValue("PLFStatus", Status.I.name());
		insertSource.addValue("InputTime", new Timestamp(System.currentTimeMillis()));

		String description = (String) rs.getObject("DESCRIPTION");
		if (!description.isEmpty()) {
			insertSource.addValue("PaymentRemarks1", StringUtils.substring(description, 0, 49));
			insertSource.addValue("PaymentRemarks2", StringUtils.substring(description, 50, 99));
			insertSource.addValue("PaymentRemarks3", StringUtils.substring(description, 100, 149));
			insertSource.addValue("PaymentRemarks4", StringUtils.substring(description, 150, 199));
		}
		save(insertSource);

		updateSource.addValue("CHANNELPARTNERREFNO", channelRefNum);
		update(updateSource);
	}

	private void update(MapSqlParameterSource updateSource) {

		StringBuilder sql = new StringBuilder("Update INT_DSBIMPS_RESPONSE");
		sql.append(" Set PROCESSFLAG = :PROCESSFLAG ");
		sql.append(" Where CHANNELPARTNERREFNO = :CHANNELPARTNERREFNO");

		this.jdbcTemplate.update(sql.toString(), updateSource);
	}

	private void save(MapSqlParameterSource insertSource) {

		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO DISBURSEMENT_PAYMENT_DETAILS VALUES (");
		sql.append(" ID, HeaderID, DisbursementReference, TransactionDate, BankReferenceNumber,");
		sql.append(" PaymentRemarks1, PaymentRemarks2, PaymentRemarks3, PaymentRemarks4, TransactionStatus, PLFStatus, InputTime)");
		sql.append(" Values (");
		sql.append(" :ID, :HeaderID, :DisbursementReference, :TransactionDate, :BankReferenceNumber,");
		sql.append(" :PaymentRemarks1, :PaymentRemarks2, :PaymentRemarks3, :PaymentRemarks4, :TransactionStatus, :PLFStatus, :InputTime)");

		this.jdbcTemplate.update(sql.toString(), insertSource);
	}

	private long saveHeader(long configID, int totalRecords) {
		MapSqlParameterSource source = new MapSqlParameterSource();
		StringBuffer sql = new StringBuffer();
		long id = getNextId("INTERFACE_HEADER_SEQ", true);

		source.addValue("ID", id);
		source.addValue("Category", "DB");
		source.addValue("InterfaceType", "DataBase");
		source.addValue("FileName", StringUtils.leftPad(String.valueOf(id), 10, '0'));
		source.addValue("ConfigID", configID);
		source.addValue("Status", "");
		source.addValue("TotalCount", totalRecords);
		source.addValue("InputTime", new Timestamp(System.currentTimeMillis()));

		sql.append(" Insert INTERFACE_HEADER Values (ID, Category, InterfaceType, FileName, ConfigID, Status, TotalCount, InputTime)");
		sql.append(" VAlues (:ID, :Category, :InterfaceType, :FileName, :ConfigID, :Status, :TotalCount, :InputTime)");

		this.jdbcTemplate.update(sql.toString(), source);
		return id;
	}

	private void updateHeader(long id, int processedCount) {
		MapSqlParameterSource source = new MapSqlParameterSource();
		StringBuffer sql = new StringBuffer();

		source.addValue("NoofRecordsProcessed", processedCount);
		source.addValue("ProcessedTime", new Timestamp(System.currentTimeMillis()));
		source.addValue("ID", id);

		sql.append(" Update INTERFACE_HEADER set NoofRecordsProcessed = :NoofRecordsProcessed,");
		sql.append(" ProcessedTime = :ProcessedTime Where ID = :ID");

		this.jdbcTemplate.update(sql.toString(), source);

	}

	private boolean isChannelRefExists(String channelRefNum) throws Exception {
		logger.debug("Entering");
		MapSqlParameterSource parameter = null;

		StringBuilder sql = new StringBuilder();
		sql.append(" Select Count(*) from FinAdvancePayments where PaymentId = :PaymentId");

		parameter = new MapSqlParameterSource();
		parameter.addValue("PaymentId", channelRefNum);

		try {
			if (this.jdbcTemplate.queryForObject(sql.toString(), parameter, Integer.class) > 0) {
				return true;
			}
		} catch (Exception e) {
			throw e;
		} finally {
			parameter = null;
			logger.debug("Leaving");
		}
		return false;
	}

	@Override
	protected MapSqlParameterSource mapData(ResultSet rs) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
