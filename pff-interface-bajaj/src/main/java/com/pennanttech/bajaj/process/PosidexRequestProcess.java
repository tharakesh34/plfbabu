package com.pennanttech.bajaj.process;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.pennanttech.dataengine.DatabaseDataEngine;
import com.pennanttech.pff.core.App;
import com.pennanttech.pff.core.Literal;
import com.pennanttech.pff.core.util.DateUtil;

public class PosidexRequestProcess extends DatabaseDataEngine {
	private static final Logger logger = Logger.getLogger(PosidexRequestProcess.class);

	private Date lastRunDate;
	private long batchId;
	private long totalLoans;

	private int loanSuccessCount;
	private int loanFailedCount;
	private int loanInsertCount;
	private int loanUpdateCount;

	public PosidexRequestProcess(DataSource dataSource, long userId, Date valueDate, Date appDate) {
		super(dataSource, App.DATABASE.name(), userId,true, valueDate);
	}

	@Override
	protected void processData() {
		logger.debug(Literal.ENTERING);

		lastRunDate = getLatestRunDate();

		batchId = logHeader();

		totalRecords = getUpdatedCustomerCount();

		totalLoans = getLoanCount();

		try {
			extractData();
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION);

		} finally {
			updateHeader();
		}

	}

	public void extractData() {
		try {
			extractCustomerDetails();
			extractCustomerLoanDetails();
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION);
			saveBatchLog(String.valueOf(batchId), "F", e.getMessage());
		} finally {
		}
	}

	private void extractCustomerDetails() throws SQLException {
		MapSqlParameterSource parmMap = new MapSqlParameterSource();

		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT * from INT_POSIDEX_CUST_VIEW");

		if (lastRunDate != null) {
			sql.append(" WHERE CUST_LASTMNTON > :CUST_LASTMNTON");
			parmMap.addValue("CUST_LASTMNTON", lastRunDate);
		}

		jdbcTemplate.query(sql.toString(), parmMap, new ResultSetExtractor<Integer>() {

			@Override
			public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
				while (rs.next()) {
					BigDecimal customerId = new BigDecimal(0);
					try {
						MapSqlParameterSource custMap = mapCustData(rs);
						customerId = (BigDecimal) custMap.getValue("CUSTOMER_NO");
						String[] keyField = new String[] { "CUSTOMER_NO" };
						boolean isExist = isRecordExist(custMap, "DEDUP_EOD_CUST_DEMO_DTL", destinationJdbcTemplate,
								keyField);

						if (isExist) {
							custMap.addValue("PROCESS_TYPE", "U");
							updateCount++;
						} else {
							custMap.addValue("PROCESS_TYPE", "I");
							insertCount++;
						}
						saveOrUpdate(custMap, "DEDUP_EOD_CUST_DEMO_DTL", destinationJdbcTemplate, keyField);
						extractCustomerAddressDetails((BigDecimal) custMap.getValue("CUSTOMER_NO"));
						successCount++;
						processedCount++;
					} catch (Exception e) {
						logger.error(Literal.EXCEPTION);
						saveBatchLog(String.valueOf(customerId), "F", e.getMessage());
						failedCount++;
					}
				}
				return 0;
			}
		});

	}

	private void extractCustomerAddressDetails(BigDecimal customerNo) throws SQLException {
		MapSqlParameterSource parmMap = new MapSqlParameterSource();

		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT * from INT_POSIDEX_CUST_ADDR_VIEW");
		sql.append(" WHERE CUSTOMER_NO = :CUSTOMER_NO");
		parmMap.addValue("CUSTOMER_NO", customerNo);

		jdbcTemplate.query(sql.toString(), parmMap, new ResultSetExtractor<Integer>() {

			@Override
			public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
				while (rs.next()) {
					MapSqlParameterSource adrrMap = mapAddrData(rs);

					String[] keyField = new String[] { "CUSTOMER_NO" };
					boolean isExist = isRecordExist(adrrMap, "DEDUP_EOD_CUST_ADDR_DTL", destinationJdbcTemplate,
							keyField);

					if (isExist) {
						adrrMap.addValue("PROCESS_TYPE", "U");
					} else {
						adrrMap.addValue("PROCESS_TYPE", "I");
					}
					saveOrUpdate(adrrMap, "DEDUP_EOD_CUST_ADDR_DTL", destinationJdbcTemplate, keyField);
				}
				return 0;
			}
		});

	}

	private void extractCustomerLoanDetails() throws SQLException {
		MapSqlParameterSource parmMap = new MapSqlParameterSource();

		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT * from INT_POSIDEX_CUST_LOAN_VIEW");

		if (lastRunDate != null) {
			sql.append(" WHERE FIN_LASTMNTON > :FIN_LASTMNTON");
			parmMap.addValue("FIN_LASTMNTON", lastRunDate);
		}

		jdbcTemplate.query(sql.toString(), parmMap, new ResultSetExtractor<Integer>() {

			@Override
			public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
				while (rs.next()) {
					String finreferenceNo = null;
					try {
						MapSqlParameterSource loanMap = mapLoanMapData(rs);
						finreferenceNo = loanMap.getValue("LAN_NO").toString();

						String[] keyField = new String[] { "LAN_NO" };
						boolean isExist = isRecordExist(loanMap, "DEDUP_EOD_CUST_LOAN_DTL", destinationJdbcTemplate,
								keyField);

						if (isExist) {
							loanMap.addValue("PROCESS_TYPE", "U");
							loanUpdateCount++;
						} else {
							loanMap.addValue("PROCESS_TYPE", "I");
							loanInsertCount++;
						}
						saveOrUpdate(loanMap, "DEDUP_EOD_CUST_LOAN_DTL", destinationJdbcTemplate, keyField);
						loanSuccessCount++;
					} catch (Exception e) {
						loanFailedCount++;
						logger.error(Literal.EXCEPTION);
						saveBatchLog(finreferenceNo, "F", e.getMessage());
					}
				}
				return 0;
			}
		});

	}

	private int getUpdatedCustomerCount() {
		StringBuilder sql = new StringBuilder();
		MapSqlParameterSource parmMap = new MapSqlParameterSource();

		sql.append(" SELECT count(*) from INT_POSIDEX_CUST_VIEW");

		if (lastRunDate != null) {
			sql.append(" WHERE CUST_LASTMNTON > :CUST_LASTMNTON");
			sql.append(" OR FIN_LASTMNTON > :FIN_LASTMNTON");
			parmMap.addValue("CUST_LASTMNTON", lastRunDate);
			parmMap.addValue("FIN_LASTMNTON", lastRunDate);
		}

		try {
			return jdbcTemplate.queryForObject(sql.toString(), parmMap, Integer.class);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		return 0;
	}

	private int getLoanCount() {
		StringBuilder sql = new StringBuilder();
		MapSqlParameterSource parmMap = new MapSqlParameterSource();

		sql.append("SELECT count(*) from INT_POSIDEX_CUST_LOAN_VIEW");

		if (lastRunDate != null) {
			sql.append(" WHERE FIN_LASTMNTON > :FIN_LASTMNTON");
			parmMap.addValue("FIN_LASTMNTON", lastRunDate);
		}

		try {
			return jdbcTemplate.queryForObject(sql.toString(), parmMap, Integer.class);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		return 0;
	}

	private Date getLatestRunDate() {
		StringBuilder sql = new StringBuilder();
		sql.append("select Max(INSERT_TIMESTAMP) from PUSH_PULL_CONTROL_T");
		try {
			return jdbcTemplate.queryForObject(sql.toString(), new MapSqlParameterSource(), Date.class);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		return null;
	}

	private long logHeader() {
		final KeyHolder keyHolder = new GeneratedKeyHolder();

		try {
			MapSqlParameterSource paramMap;
			StringBuilder sql = new StringBuilder();
			sql.append(" INSERT INTO PUSH_PULL_CONTROL_T (STATUS, INSERT_TIMESTAMP) VALUES(");
			sql.append(":STATUS, :INSERT_TIMESTAMP)");

			paramMap = new MapSqlParameterSource();
			paramMap.addValue("STATUS", "S");
			paramMap.addValue("INSERT_TIMESTAMP", DateUtil.getSysDate());

			jdbcTemplate.update(sql.toString(), paramMap, keyHolder, new String[] { "BATCHID" });

		} catch (Exception e) {
			logger.error("Exception :", e);
		}
		return keyHolder.getKey().longValue();
	}

	private void updateHeader() {
		MapSqlParameterSource paramMap;
		StringBuilder sql = new StringBuilder();

		sql.append(" UPDATE PUSH_PULL_CONTROL_T  SET STATUS = :STATUS, COMPLETION_TIMESTAMP = :COMPLETION_TIMESTAMP");
		sql.append(" WHERE BATCHID = :BATCHID");

		paramMap = new MapSqlParameterSource();
		paramMap.addValue("STATUS", "I");
		paramMap.addValue("COMPLETION_TIMESTAMP", DateUtil.getSysDate());
		paramMap.addValue("BATCHID", batchId);


		try {
			jdbcTemplate.update(sql.toString(), paramMap);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
	}

	
	@Override
	public void updateRemarks(StringBuilder remarks) {
		super.updateRemarks(remarks);
		StringBuilder builder = new StringBuilder();
		if (failedCount > 0) {
			builder.append("Completed with exceptions total customers: " + totalRecords + ", total loans : "
					+ totalLoans);
			builder.append("success customers: " + successCount + ", loans : " + loanSuccessCount);
			builder.append(" inserted: " + insertCount + ", updated: " + updateCount + " customers");
			builder.append(" inserted: " + loanInsertCount + ", updated: " + loanUpdateCount + " loans");
			builder.append(" failed customers : " + failedCount + ", failed loans: " + loanFailedCount);
		} else {
			builder.append("Completed succesfully total customers: " + totalRecords + ", total loans : " + totalLoans);
			builder.append("success customers: " + successCount + ", loans : " + loanSuccessCount);
			builder.append(" inserted: " + insertCount + ", updated: " + updateCount + " customers");
			builder.append(" inserted: " + loanInsertCount + ", updated: " + loanUpdateCount + " loans");
		}
		executionStatus.setRemarks(builder.toString());

	}
	
	
	private MapSqlParameterSource mapLoanMapData(ResultSet rs) throws SQLException {
		MapSqlParameterSource map = new MapSqlParameterSource();

		map.addValue("BATCHID", batchId);
		map.addValue("CUSTOMER_NO", rs.getObject("CUSTOMER_NO"));
		map.addValue("SOURCE_SYS_ID", rs.getObject("SOURCE_SYS_ID"));
		map.addValue("SEGMENT", rs.getObject("SEGMENT"));
		map.addValue("DEAL_ID", rs.getObject("DEAL_ID"));
		map.addValue("LAN_NO", rs.getObject("LAN_NO"));
		map.addValue("CUSTOMER_TYPE", rs.getObject("CUSTOMER_TYPE"));
		map.addValue("APPLN_NO", rs.getObject("APPLN_NO"));
		map.addValue("PRODUCT_CODE", rs.getObject("PRODUCT_CODE"));

		map.addValue("PROCESS_TYPE", rs.getObject("PROCESS_TYPE"));
		map.addValue("PROCESS_FLAG", rs.getObject("PROCESS_FLAG"));
		map.addValue("ERROR_CODE", rs.getObject("ERROR_CODE"));
		map.addValue("ERROR_DESC", rs.getObject("ERROR_DESC"));
		map.addValue("PSX_BATCH_ID", rs.getObject("PSX_BATCH_ID"));
		map.addValue("PSX_ID", rs.getObject("PSX_ID"));
		map.addValue("CUSTOMER_ID", rs.getObject("CUSTOMER_ID"));
		map.addValue("SOURCE_SYSTEM", rs.getObject("SOURCE_SYSTEM"));
		map.addValue("EOD_BATCH_ID", rs.getObject("EOD_BATCH_ID"));

		return map;
	}

	private MapSqlParameterSource mapAddrData(ResultSet rs) throws SQLException {
		MapSqlParameterSource map = new MapSqlParameterSource();

		map.addValue("BATCHID", batchId);
		map.addValue("CUSTOMER_NO", rs.getObject("CUSTOMER_NO"));
		map.addValue("SOURCE_SYS_ID", rs.getObject("SOURCE_SYS_ID"));
		map.addValue("SEGMENT", rs.getObject("SEGMENT"));
		map.addValue("ADDRESS_TYPE", rs.getObject("ADDRESS_TYPE"));
		map.addValue("ADDRESS_1", rs.getObject("ADDRESS_1"));
		map.addValue("ADDRESS_2", rs.getObject("ADDRESS_2"));
		map.addValue("ADDRESS_3", rs.getObject("ADDRESS_3"));
		map.addValue("STATE", rs.getObject("STATE"));
		map.addValue("CITY", rs.getObject("CITY"));
		map.addValue("PIN", rs.getObject("PIN"));
		map.addValue("LANDLINE_1", rs.getObject("LANDLINE_1"));
		map.addValue("LANDLINE_2", rs.getObject("LANDLINE_2"));
		map.addValue("MOBILE", rs.getObject("MOBILE"));
		map.addValue("AREA", rs.getObject("AREA"));
		map.addValue("LANDMARK", rs.getObject("LANDMARK"));
		map.addValue("STD", rs.getObject("STD"));
		map.addValue("PROCESS_TYPE", rs.getObject("PROCESS_TYPE"));
		map.addValue("EMAIL", rs.getObject("EMAIL"));
		map.addValue("PROCESS_FLAG", rs.getObject("PROCESS_FLAG"));
		map.addValue("ERROR_CODE", rs.getObject("ERROR_CODE"));
		map.addValue("ERROR_DESC", rs.getObject("ERROR_DESC"));
		map.addValue("CUSTOMER_ID", rs.getObject("CUSTOMER_ID"));
		map.addValue("SOURCE_SYSTEM", rs.getObject("SOURCE_SYSTEM"));
		map.addValue("PSX_BATCH_ID", rs.getObject("PSX_BATCH_ID"));
		map.addValue("EOD_BATCH_ID", rs.getObject("EOD_BATCH_ID"));

		return map;
	}

	private MapSqlParameterSource mapCustData(ResultSet rs) throws SQLException {
		MapSqlParameterSource map = new MapSqlParameterSource();

		map.addValue("BATCHID", batchId);
		map.addValue("CUSTOMER_NO", rs.getObject("CUSTOMER_NO"));
		map.addValue("SOURCE_SYS_ID", rs.getObject("SOURCE_SYS_ID"));
		map.addValue("FIRST_NAME", rs.getObject("FIRST_NAME"));
		map.addValue("MIDDLE_NAME", rs.getObject("MIDDLE_NAME"));
		map.addValue("LAST_NAME", rs.getObject("LAST_NAME"));
		map.addValue("DOB", rs.getObject("DOB"));
		map.addValue("PAN", rs.getObject("PAN"));
		map.addValue("DRIVING_LICENSE_NUMBER", rs.getObject("DRIVING_LICENSE_NUMBER"));
		map.addValue("VOTER_ID", rs.getObject("VOTER_ID"));
		map.addValue("DATE_OF_INCORPORATION", rs.getObject("DATE_OF_INCORPORATION"));
		map.addValue("TAN_NO", rs.getObject("TAN_NO"));
		map.addValue("PROCESS_TYPE", rs.getObject("PROCESS_TYPE"));
		map.addValue("APPLICANT_TYPE", rs.getObject("APPLICANT_TYPE"));
		map.addValue("EMPOYER_NAME", rs.getObject("EMPOYER_NAME"));
		map.addValue("FATHER_NAME", rs.getObject("FATHER_NAME"));
		map.addValue("PASSPORT_NO", rs.getObject("PASSPORT_NO"));
		map.addValue("ACCOUNT_NUMBER", rs.getObject("ACCOUNT_NUMBER"));
		map.addValue("CREDIT_CARD_NUMBER", rs.getObject("CREDIT_CARD_NUMBER"));
		map.addValue("PROCESS_FLAG", rs.getObject("PROCESS_FLAG"));
		map.addValue("ERROR_CODE", rs.getObject("ERROR_CODE"));
		map.addValue("ERROR_DESC", rs.getObject("ERROR_DESC"));
		map.addValue("CUSTOMER_ID", rs.getObject("CUSTOMER_ID"));
		map.addValue("SOURCE_SYSTEM", rs.getObject("SOURCE_SYSTEM"));
		map.addValue("PSX_BATCH_ID", rs.getObject("PSX_BATCH_ID"));
		map.addValue("UCIN_FLAG", rs.getObject("UCIN_FLAG"));
		map.addValue("EOD_BATCH_ID", rs.getObject("EOD_BATCH_ID"));
		map.addValue("INSERT_TS", rs.getObject("INSERT_TS"));
		map.addValue("GENDER", rs.getObject("GENDER"));
		map.addValue("AADHAR_NO", rs.getObject("AADHAR_NO"));
		map.addValue("CIN", rs.getObject("CIN"));
		map.addValue("DIN", rs.getObject("DIN"));
		map.addValue("REGISTRATION_NO", rs.getObject("REGISTRATION_NO"));
		map.addValue("CA_NUMBER", rs.getObject("CA_NUMBER"));
		map.addValue("SEGMENT", rs.getObject("SEGMENT"));

		return map;
	}

	@Override
	protected MapSqlParameterSource mapData(ResultSet rs) throws Exception {
		return null;
	}

}
