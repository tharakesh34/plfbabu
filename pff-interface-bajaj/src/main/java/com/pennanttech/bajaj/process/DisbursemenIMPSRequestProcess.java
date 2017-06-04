package com.pennanttech.bajaj.process;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

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

public class DisbursemenIMPSRequestProcess extends DatabaseDataEngine {
	private static final Logger	logger	= Logger.getLogger(DisbursemenIMPSRequestProcess.class);

	private List<String> disbursments;
	
	public DisbursemenIMPSRequestProcess(DataSource dataSource, long userId, Date valueDate, Date appDate) {
		super(dataSource, App.DATABASE.name(), userId, true, valueDate);
	}

	@Override
	protected void processData() {
		executionStatus.setRemarks("Loading data..");
		MapSqlParameterSource parmMap;
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT * FROM DISBURSEMENT_REQUESTS WHERE ID IN (:ID) AND STATUS = :STATUS");
		parmMap = new MapSqlParameterSource();

		parmMap.addValue("ID", disbursments);
		parmMap.addValue("STATUS", "Approved");

		jdbcTemplate.query(sql.toString(), parmMap, new ResultSetExtractor<Long>() {
			MapSqlParameterSource	map			= null;
			TransactionStatus		txnStatus	= null;

			@Override
			public Long extractData(ResultSet rs) throws SQLException, DataAccessException {
				while (rs.next()) {
					executionStatus.setRemarks("processing the record " + ++totalRecords);
					processedCount++;
					txnStatus = transManager.getTransaction(transDef);
					try {
						map = mapData(rs);

						updateDisbursement(rs.getLong("DISBURSEMENT_ID"));

						updateDisbursementRequest(rs.getLong("ID"), batchId);

						save(map, "INT_DSBIMPS_REQUEST", destinationJdbcTemplate);

						successCount++;

						transManager.commit(txnStatus);

					} catch (Exception e) {
						transManager.rollback(txnStatus);
						failedCount++;
						saveBatchLog(rs.getString("DISBURSEMENT_ID"), "F", e.getMessage());
					} finally {
						map = null;
						txnStatus.flush();
						txnStatus = null;
					}
				}
				return totalRecords;
			}
		});
	}

	@Override
	protected MapSqlParameterSource mapData(ResultSet rs) throws Exception {
		MapSqlParameterSource map = new MapSqlParameterSource();

		map.addValue("BCAGENTID", null);
		map.addValue("SENDERID", null);
		map.addValue("RECEIVERNAME", rs.getString("BENFICIARY_NAME"));
		
		map.addValue("RECEIVERMOBILENO", rs.getString("BENFICIARY_MOBILE"));
		map.addValue("RECEIVEREMAILID", rs.getString("CUSTOMER_EMAIL"));
		map.addValue("IFSCODE", rs.getString("IFSC_CODE"));
		map.addValue("BANK", rs.getString("BENFICIARY_BANK"));
		map.addValue("RECEVIERBANKSTATE", rs.getString("BENFICIARY_BRANCH_STATE"));
		map.addValue("RECEVIERBANKCITY", rs.getString("BENFICIARY_BRANCH_CITY"));
		map.addValue("RECEVIERBANKBRANCH", rs.getString("BENFICIARY_BRANCH"));
		map.addValue("RECEVIERACCOUNTNUMBER", rs.getString("BENFICIARY_ACCOUNT"));
		map.addValue("AMOUNT", rs.getString("DISBURSEMENT_AMOUNT"));
		map.addValue("REMARKS", StringUtils.substring(rs.getString("REMARKS"), 0, 9));
		map.addValue("CHANNELPARTNERREFNO", rs.getString("ID"));
		map.addValue("PICKUPFLAG", Status.N.name());
		map.addValue("AGREEMENTID", BigDecimal.ZERO);

		return map;
	}

	private int updateDisbursement(long paymentId) {
		StringBuilder sql = new StringBuilder();
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		try {
			sql.append("UPDATE FINADVANCEPAYMENTS  SET STATUS  =  :STATUS WHERE  PAYMENTID = :PAYMENTID");

			paramMap.addValue("STATUS", "AC");
			paramMap.addValue("PAYMENTID", paymentId);

			return jdbcTemplate.update(sql.toString(), paramMap);

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw e;
		}
	}

	private int updateDisbursementRequest(long id, long batchId) {
		StringBuilder sql = new StringBuilder();
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		try {
			sql.append("UPDATE DISBURSEMENT_REQUESTS  SET STATUS  = :STATUS, BATCH_ID  = :BATCH_ID  WHERE ID = :ID");

			paramMap.addValue("STATUS", "AC");
			paramMap.addValue("BATCH_ID", batchId);
			paramMap.addValue("ID", id);

			return jdbcTemplate.update(sql.toString(), paramMap);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw e;
		}
	}

	public void setDisbursments(List<String> disbursments) {
		this.disbursments = disbursments;
	}
}
