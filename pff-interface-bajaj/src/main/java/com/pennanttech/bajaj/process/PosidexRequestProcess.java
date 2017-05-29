package com.pennanttech.bajaj.process;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.TransactionStatus;

import com.pennanttech.dataengine.DatabaseDataEngine;
import com.pennanttech.pff.core.App;
import com.pennanttech.pff.core.Literal;

public class PosidexRequestProcess extends DatabaseDataEngine {

	private static final Logger logger = Logger.getLogger(PosidexRequestProcess.class);

	private Date lastMntOnFromDate;
	private Date lastMntOnToDate;

	public PosidexRequestProcess(DataSource dataSource, Date lastMntOnFromDate, Date lastMntOnToDate, long userId, Date valueDate) {
		super(dataSource, App.DATABASE.name(), userId, valueDate);
		this.lastMntOnFromDate = lastMntOnFromDate;
		this.lastMntOnToDate = lastMntOnToDate;
	}

	@Override
	protected void processData() {
		logger.debug(Literal.ENTERING);

		executionStatus.setRemarks("Loading data..");
		MapSqlParameterSource parmMap;
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT * from INT_POSIDEX_UPDATE_EOD_VIEW Where LastMntOn > :LastMntOnFromDate AND LastMntOn < :LastMntOnToDate ");
		parmMap = new MapSqlParameterSource();

		parmMap.addValue("LastMntOnFromDate", lastMntOnFromDate);
		parmMap.addValue("LastMntOnToDate", lastMntOnToDate);

		jdbcTemplate.query(sql.toString(), parmMap, new ResultSetExtractor<Integer>() {
			TransactionStatus txnStatus = null;

			@Override
			public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
				while (rs.next()) {

					MapSqlParameterSource custMap = null;
					MapSqlParameterSource adrrMap = null;
					MapSqlParameterSource ctrlMap = null;
					MapSqlParameterSource loanMap = null;
					long batchId = 1;

					executionStatus.setRemarks("processing the record " + ++totalRecords);
					processedCount++;
					txnStatus = transManager.getTransaction(transDef);
					try {
						batchId = getNextId("PosidexUpdateSeq", true);

						custMap = mapCustData(rs, batchId);
						adrrMap = mapAddrData(rs, batchId);
						ctrlMap = mapCtrlMapData(rs, batchId);
						loanMap = mapLoanMapData(rs, batchId);

						save(custMap, "DEDUP_EOD_CUST_DEMO_DTL", destinationJdbcTemplate);
						save(adrrMap, "DEDUP_EOD_CUST_ADDR_DTL", destinationJdbcTemplate);
						save(ctrlMap, "PUSH_PULL_CONTROL_T", destinationJdbcTemplate);
						save(loanMap, "DEDUP_EOD_CUST_LOAN_DTL", destinationJdbcTemplate);

						successCount++;
						transManager.commit(txnStatus);

					} catch (Exception e) {
						logger.error(Literal.ENTERING);
						transManager.rollback(txnStatus);
						failedCount++;
						saveBatchLog(String.valueOf(batchId), "F", e.getMessage());
					} finally {
						txnStatus.flush();
						txnStatus = null;
					}
				}
				return totalRecords;
			}

			private MapSqlParameterSource mapLoanMapData(ResultSet rs, long batchId) throws SQLException {
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

			private MapSqlParameterSource mapCtrlMapData(ResultSet rs, long batchId) throws SQLException {
				MapSqlParameterSource map = new MapSqlParameterSource();

				map.addValue("BATCHID", batchId);
				map.addValue("STATUS", rs.getObject("STATUS"));
				map.addValue("INSERT_TIMESTAMP", rs.getObject("INSERT_TIMESTAMP"));
				map.addValue("COMPLETION_TIMESTAMP", rs.getObject("COMPLETION_TIMESTAMP"));
				map.addValue("ERR_DESCRIPTION", rs.getObject("ERR_DESCRIPTION"));
				map.addValue("ERROR_CODE", rs.getObject("ERROR_CODE"));

				return map;
			}

			private MapSqlParameterSource mapAddrData(ResultSet rs, long batchId) throws SQLException {
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

			private MapSqlParameterSource mapCustData(ResultSet rs, long batchId) throws SQLException {
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
		});
	}

	@Override
	protected MapSqlParameterSource mapData(ResultSet rs) throws Exception {
		return null;
	}

}
