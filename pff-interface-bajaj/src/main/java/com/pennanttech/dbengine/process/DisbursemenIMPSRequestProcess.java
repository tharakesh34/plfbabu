package com.pennanttech.dbengine.process;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
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
import com.pennanttech.pff.core.Literal;

public class DisbursemenIMPSRequestProcess extends DatabaseDataEngine {
	private static final Logger	logger	= Logger.getLogger(DisbursemenIMPSRequestProcess.class);

	private List<String> disbursments;
	
	public DisbursemenIMPSRequestProcess(DataSource dataSource, String database) {
		super(dataSource, database);
	}

	@Override
	protected void processData() {
		executionStatus.setRemarks("Loading data..");
		MapSqlParameterSource parmMap;
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT * FROM DISBURSEMENT_REQUESTS WHERE DISBURSEMENT_ID IN (:DISBURSEMENT_ID) AND STATUS = :STATUS");
		parmMap = new MapSqlParameterSource();

		parmMap.addValue("DISBURSEMENT_ID", Arrays.asList(disbursments));
		parmMap.addValue("STATUS", "APPROVED");

		jdbcTemplate.query(sql.toString(), parmMap, new ResultSetExtractor<Integer>() {
			MapSqlParameterSource	map			= null;
			TransactionStatus		txnStatus	= null;

			@Override
			public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
				while (rs.next()) {
					executionStatus.setRemarks("processing the record " + ++totalRecords);
					processedCount++;
					txnStatus = transManager.getTransaction(transDef);
					try {
						map = mapData(rs);

						updateDisbursement(rs.getLong("DISBURSEMENT_ID"));

						updateDisbursementRequest(rs.getLong("ID"), batchId);

						insertData(map, "INT_DSBIMPS_REQUEST", destinationJdbcTemplate);

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

		String mobileNo = rs.getString("BENFICIARY_MOBILE");
		if (StringUtils.isEmpty(mobileNo)) {
			throw new Exception("Customer Mobile Number cannot be blank");
		}

		String emailId = rs.getString("CUSTOMER_EMAIL");
		if (StringUtils.isEmpty(emailId)) {
			throw new Exception("Customer Email cannot be blank");
		}

		String branchState = rs.getString("BENFICIARY_BRANCH_STATE");
		if (StringUtils.isEmpty(branchState)) {
			throw new Exception("Bank State cannot be blank");
		}

		String branchCity = rs.getString("BENFICIARY_BRANCH_CITY");
		if (StringUtils.isEmpty(branchCity)) {
			throw new Exception("Bank City cannot be blank");
		}

		String remarks = rs.getString("REMARKS");
		if (StringUtils.isEmpty(remarks)) {
			throw new Exception("Remarks cannot be blank");
		}

		map.addValue("RECEIVERMOBILENO", mobileNo);
		map.addValue("RECEIVEREMAILID", emailId);
		map.addValue("IFSCODE", rs.getString("IFSC_CODE"));
		map.addValue("BANK", rs.getString("BENFICIARY_BANK"));
		map.addValue("RECEVIERBANKSTATE", branchState);
		map.addValue("RECEVIERBANKCITY", branchCity);
		map.addValue("RECEVIERBANKBRANCH", rs.getString("BENFICIARY_BRANCH"));
		map.addValue("RECEVIERACCOUNTNUMBER", rs.getString("BENFICIARY_ACCOUNT"));
		map.addValue("AMOUNT", rs.getString("DISBURSEMENT_AMOUNT"));
		map.addValue("REMARKS", StringUtils.substring(remarks, 0, 9));
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
