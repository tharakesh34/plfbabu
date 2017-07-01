package com.pennanttech.bajaj.process;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.TransactionStatus;

import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennanttech.dataengine.DatabaseDataEngine;
import com.pennanttech.pff.baja.BajajInterfaceConstants;
import com.pennanttech.pff.core.App;
import com.pennanttech.pff.core.Literal;
import com.pennanttech.pff.core.util.DateUtil;

public class PosidexRequestProcess extends DatabaseDataEngine {
	private static final Logger	logger					= Logger.getLogger(PosidexRequestProcess.class);

	private Date				lastRunDate;
	private long				headerId;

	private String				SOURCE_SYSTEM_ID;
	private String				SOURCE_SYSTEM;

	private String[]			customerKey				= new String[] { "CUSTOMER_NO" };
	private String[]			customerLoanKey			= new String[] { "CUSTOMER_NO", "LAN_NO", "CUSTOMER_TYPE" };
	private String[]			reportKey				= new String[] { "FILLER_STRING_1" };

	private static final String	CUSTOMER_DETAILS		= "PSX_DEDUP_EOD_CUST_DEMO_DTL";
	private static final String	CUSTOMER_ADDR_DETAILS	= "PSX_DEDUP_EOD_CUST_ADDR_DTL";
	private static final String	CUSTOMER_LOAN_DETAILS	= "PSX_DEDUP_EOD_CUST_LOAN_DTL";
	private static final String	CUSTOMER_REPORT_DETAILS	= "DEDUP_EOD_CUST_REP_DTL";

	private String				summary					= null;

	public PosidexRequestProcess(DataSource dataSource, long userId, Date valueDate, Date appDate) {
		super(dataSource, App.DATABASE.name(), userId, true, valueDate, BajajInterfaceConstants.POSIDEX_REQUEST_STATUS);
	}

	@Override
	protected void processData() {
		logger.debug(Literal.ENTERING);

		lastRunDate = getLatestRunDate();

		headerId = logHeader();

		loadCount();

		loaddefaults();

		try {
			extractData();
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		} finally {
			updateRemarks(new StringBuilder());
			updateHeader();
		}

	}

	private void loaddefaults() {
		SOURCE_SYSTEM_ID = (String) getSMTParameter("POSIDEX_SOURCE_SYSTEM_ID", String.class);
		SOURCE_SYSTEM = (String) getSMTParameter("POSIDEX_SOURCE_SYSTEM", String.class);
	}

	private CustomerPhoneNumber getPhoneNumber(long custId, String addrtype) {
		String phoneCode = (String) getSMTParameter("POSIDEX_PHONE_" + addrtype, String.class);
		if (phoneCode == null) {
			return null;
		}

		StringBuilder sql = new StringBuilder();
		MapSqlParameterSource paramMap = new MapSqlParameterSource();

		sql.append("SELECT PHONECOUNTRYCODE, PHONEAREACODE, PHONENUMBER FROM CUSTOMERPHONENUMBERS where PHONETYPECODE = :PHONETYPECODE AND PHONECUSTID = :PHONECUSTID");
		paramMap.addValue("PHONETYPECODE", phoneCode);
		paramMap.addValue("PHONECUSTID", custId);

		try {
			return jdbcTemplate.queryForObject(sql.toString(), paramMap, CustomerPhoneNumber.class);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		} finally {
			paramMap = null;
			sql = null;
		}

		return null;
	}

	private CustomerEMail getEmail(long custId, String addrtype) {
		String emailCode = (String) getSMTParameter("POSIDEX_EMAIL" + addrtype, String.class);
		if (emailCode == null) {
			return null;
		}

		StringBuilder sql = new StringBuilder();
		MapSqlParameterSource paramMap = new MapSqlParameterSource();

		sql.append("SELECT CUSTEMAIL FROM CUSTOMEREMAILS where CUSTEMAILTYPECODE = :CUSTEMAILTYPECODE");
		paramMap.addValue("CUSTEMAILTYPECODE", emailCode);

		try {
			return jdbcTemplate.queryForObject(sql.toString(), paramMap, CustomerEMail.class);
		} catch (Exception e) {
		} finally {
			paramMap = null;
			sql = null;
		}

		return null;
	}

	protected Object getSMTParameter(String sysParmCode, Class<?> type) {
		MapSqlParameterSource paramMap;

		StringBuilder sql = new StringBuilder();
		paramMap = new MapSqlParameterSource();

		sql.append("SELECT SYSPARMVALUE FROM SMTPARAMETERS where SYSPARMCODE = :SYSPARMCODE");
		paramMap.addValue("SYSPARMCODE", sysParmCode);

		try {
			return jdbcTemplate.queryForObject(sql.toString(), paramMap, type);
		} catch (Exception e) {
			logger.error("The parameter code " + sysParmCode + " not configured.");
		} finally {
			paramMap = null;
			sql = null;
		}
		return null;
	}

	public void extractData() {
		try {
			extractCustomerDetails();
			extractCustomerLoanDetails();
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
	}

	private void extractCustomerDetails() throws SQLException {
		MapSqlParameterSource parmMap = new MapSqlParameterSource();

		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT * from INT_POSIDEX_CUST_VIEW");

		if (lastRunDate != null) {
			sql.append(" WHERE LASTMNTON > :LASTMNTON");
			parmMap.addValue("LASTMNTON", lastRunDate);
		}

		jdbcTemplate.query(sql.toString(), parmMap, new RowCallbackHandler() {
			TransactionStatus	txnStatus	= null;

			@Override
			public void processRow(ResultSet rs) throws SQLException {
				executionStatus.setProcessedRecords(processedCount++);

				long customerId = 0;
				boolean isExist = false;

				customerId = rs.getLong("CUSTOMER_NO");
				try {
					MapSqlParameterSource custMap = mapCustData(rs);

					isExist = isRecordExist(custMap, CUSTOMER_DETAILS, destinationJdbcTemplate, customerKey);

					txnStatus = transManager.getTransaction(transDef);
					if (isExist) {
						custMap.addValue("PROCESS_TYPE", "U");
						update(custMap, CUSTOMER_DETAILS, destinationJdbcTemplate, customerKey);
						updateCount++;
					} else {
						custMap.addValue("PROCESS_TYPE", "I");
						save(custMap, CUSTOMER_DETAILS, destinationJdbcTemplate);
						insertCount++;
					}

					extractCustomerAddressDetails(customerId);
					custMap.addValue("FILLER_STRING_1", rs.getObject("FILLER_STRING_1"));
					extractCustomerReportDetails(customerId, custMap);
					transManager.commit(txnStatus);
					executionStatus.setSuccessRecords(successCount++);
				} catch (Exception e) {
					transManager.rollback(txnStatus);
					logger.error(Literal.EXCEPTION);
					executionStatus.setFailedRecords(failedCount++);
					saveBatchLog(String.valueOf(customerId), "F", e.getMessage());
				} finally {
					txnStatus.flush();
					txnStatus = null;
				}

			}
		});

	}

	private void extractCustomerReportDetails(long customerId, MapSqlParameterSource custMap) {
		boolean isExist;

		MapSqlParameterSource parmMap = new MapSqlParameterSource();
		parmMap.addValue("BATCHID", headerId);
		parmMap.addValue("SOURCE_SYS_ID", SOURCE_SYSTEM_ID);
		parmMap.addValue("CUSTOMER_ID", custMap.getValue("CUSTOMER_ID"));
		parmMap.addValue("FILLER_STRING_1", custMap.getValue("FILLER_STRING_1"));

		try {
			isExist = isRecordExist(custMap, CUSTOMER_REPORT_DETAILS, destinationJdbcTemplate, reportKey);
			if (!isExist) {
				save(parmMap, CUSTOMER_REPORT_DETAILS, destinationJdbcTemplate);
			}
		} catch (Exception e) {
			throw e;
		}

	}

	private void extractCustomerAddressDetails(long customerNo) throws SQLException {
		MapSqlParameterSource parmMap = new MapSqlParameterSource();

		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT * from INT_POSIDEX_CUST_ADDR_VIEW");
		sql.append(" WHERE CUSTOMER_NO = :CUSTOMER_NO");
		parmMap.addValue("CUSTOMER_NO", customerNo);

		if (lastRunDate != null) {
			sql.append(" AND LASTMNTON > :LASTMNTON");
			parmMap.addValue("LASTMNTON", lastRunDate);
		}

		jdbcTemplate.query(sql.toString(), parmMap, new RowCallbackHandler() {

			@Override
			public void processRow(ResultSet rs) throws SQLException {
				boolean isExist;
				MapSqlParameterSource adrrMap = mapAddrData(rs);

				try {
					isExist = isRecordExist(adrrMap, CUSTOMER_ADDR_DETAILS, destinationJdbcTemplate, customerKey);
					if (isExist) {
						adrrMap.addValue("PROCESS_TYPE", "U");
						update(adrrMap, CUSTOMER_ADDR_DETAILS, destinationJdbcTemplate, customerKey);
					} else {
						adrrMap.addValue("PROCESS_TYPE", "I");
						save(adrrMap, CUSTOMER_ADDR_DETAILS, destinationJdbcTemplate);
					}
				} catch (Exception e) {
					throw e;
				}

			}
		});

	}

	private void extractCustomerLoanDetails() throws SQLException {
		MapSqlParameterSource parmMap = new MapSqlParameterSource();

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * from INT_POSIDEX_CUST_LOAN_VIEW");
		
		if (lastRunDate != null) {
			sql.append(" WHERE LASTMNTON > :LASTMNTON");
			parmMap.addValue("LASTMNTON", lastRunDate);
		}

		jdbcTemplate.query(sql.toString(), parmMap, new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				executionStatus.setProcessedRecords(processedCount++);

				boolean isExist;
				String finreferenceNo = null;
				try {
					MapSqlParameterSource loanMap = mapLoanMapData(rs);
					finreferenceNo = loanMap.getValue("LAN_NO").toString();

					try {
						isExist = isRecordExist(loanMap, CUSTOMER_LOAN_DETAILS, destinationJdbcTemplate,
								customerLoanKey);
						if (isExist) {
							loanMap.addValue("PROCESS_TYPE", "U");
							update(loanMap, CUSTOMER_LOAN_DETAILS, destinationJdbcTemplate, customerLoanKey);
						} else {
							loanMap.addValue("PROCESS_TYPE", "I");
							save(loanMap, CUSTOMER_LOAN_DETAILS, destinationJdbcTemplate);
						}
						executionStatus.setSuccessRecords(successCount++);
					} catch (Exception e) {
						throw e;
					}

				} catch (Exception e) {
					logger.error(Literal.EXCEPTION, e);
					executionStatus.setFailedRecords(failedCount++);
					saveBatchLog(finreferenceNo, "F", e.getMessage());
				}
			}
		});

	}

	private void loadCount() {
		StringBuilder sql = new StringBuilder();

		MapSqlParameterSource parmMap = new MapSqlParameterSource();
		sql.append("select sum(count) from (");
		sql.append(" SELECT count(*) count from INT_POSIDEX_CUST_VIEW");

		if (lastRunDate != null) {
			sql.append(" WHERE LASTMNTON > :LASTMNTON");
		}

		sql.append(" union all ");
		sql.append("SELECT count(*) count from INT_POSIDEX_CUST_LOAN_VIEW");

		if (lastRunDate != null) {
			sql.append(" WHERE LASTMNTON > :LASTMNTON");
		}

		sql.append(") T ");

		if (lastRunDate != null) {
			parmMap.addValue("LASTMNTON", lastRunDate);
		}

		try {
			totalRecords = jdbcTemplate.queryForObject(sql.toString(), parmMap, Integer.class);
			BajajInterfaceConstants.POSIDEX_REQUEST_STATUS.setTotalRecords(totalRecords);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

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
		sql.append(" ,ERR_DESCRIPTION = :ERR_DESCRIPTION");
		sql.append(" WHERE BATCHID = :BATCHID");

		paramMap = new MapSqlParameterSource();
		paramMap.addValue("STATUS", "I");
		paramMap.addValue("COMPLETION_TIMESTAMP", DateUtil.getSysDate());
		paramMap.addValue("BATCHID", headerId);
		paramMap.addValue("ERR_DESCRIPTION", summary);

		try {
			jdbcTemplate.update(sql.toString(), paramMap);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
	}

	private MapSqlParameterSource mapLoanMapData(ResultSet rs) throws SQLException {
		MapSqlParameterSource map = new MapSqlParameterSource();

		map.addValue("BATCHID", headerId);
		map.addValue("CUSTOMER_NO", rs.getObject("CUSTOMER_NO"));
		map.addValue("SOURCE_SYS_ID", SOURCE_SYSTEM_ID);
		map.addValue("SEGMENT", rs.getObject("SEGMENT"));
		map.addValue("DEAL_ID", rs.getObject("DEAL_ID"));
		map.addValue("LAN_NO", rs.getObject("LAN_NO"));
		map.addValue("CUSTOMER_TYPE", rs.getObject("CUSTOMER_TYPE"));
		map.addValue("APPLN_NO", rs.getObject("APPLN_NO"));
		map.addValue("PRODUCT_CODE", rs.getObject("PRODUCT_CODE"));
		map.addValue("PROCESS_FLAG", rs.getObject("PROCESS_FLAG"));
		map.addValue("ERROR_CODE", rs.getObject("ERROR_CODE"));
		map.addValue("ERROR_DESC", rs.getObject("ERROR_DESC"));
		map.addValue("PSX_BATCH_ID", rs.getObject("PSX_BATCH_ID"));
		map.addValue("PSX_ID", rs.getObject("PSX_ID"));
		map.addValue("CUSTOMER_ID", rs.getObject("CUSTOMER_ID"));
		map.addValue("SOURCE_SYSTEM", SOURCE_SYSTEM);
		map.addValue("EOD_BATCH_ID", rs.getObject("EOD_BATCH_ID"));

		return map;
	}

	private MapSqlParameterSource mapAddrData(ResultSet rs) throws SQLException {
		MapSqlParameterSource map = new MapSqlParameterSource();

		String addressType = rs.getString("ADDRESS_TYPE");
		long customeId = rs.getLong("CUSTOMER_NO");

		CustomerPhoneNumber landLine1 = getPhoneNumber(customeId, addressType + "_LANDLINE_1");
		CustomerPhoneNumber landLine2 = getPhoneNumber(customeId, addressType + "_LANDLINE_2");
		CustomerPhoneNumber mobile = getPhoneNumber(customeId, addressType + "_MOBILE");

		CustomerEMail email = getEmail(customeId, addressType + "_EMAIL");

		if (landLine1 != null) {
			map.addValue("LANDLINE_1", getPhoneNumber(landLine1));
		}

		if (landLine2 != null) {
			map.addValue("LANDLINE_2", getPhoneNumber(landLine2));
		}

		if (mobile != null) {
			map.addValue("MOBILE", getPhoneNumber(mobile));
		}

		if (email != null) {
			map.addValue("EMAIL", email);

		}

		map.addValue("BATCHID", headerId);
		map.addValue("CUSTOMER_NO", rs.getObject("CUSTOMER_NO"));
		map.addValue("SOURCE_SYS_ID", SOURCE_SYSTEM_ID);
		map.addValue("SEGMENT", rs.getObject("SEGMENT"));
		map.addValue("ADDRESS_TYPE", rs.getObject("ADDRESS_TYPE"));
		map.addValue("ADDRESS_1", rs.getObject("ADDRESS_1"));
		map.addValue("ADDRESS_2", rs.getObject("ADDRESS_2"));
		map.addValue("ADDRESS_3", rs.getObject("ADDRESS_3"));
		map.addValue("STATE", rs.getObject("STATE"));
		map.addValue("CITY", rs.getObject("CITY"));
		map.addValue("PIN", rs.getObject("PIN"));
		map.addValue("AREA", rs.getObject("AREA"));
		map.addValue("LANDMARK", rs.getObject("LANDMARK"));
		map.addValue("STD", rs.getObject("STD"));
		map.addValue("PROCESS_TYPE", rs.getObject("PROCESS_TYPE"));
		map.addValue("PROCESS_FLAG", rs.getObject("PROCESS_FLAG"));
		map.addValue("ERROR_CODE", rs.getObject("ERROR_CODE"));
		map.addValue("ERROR_DESC", rs.getObject("ERROR_DESC"));
		map.addValue("CUSTOMER_ID", rs.getObject("CUSTOMER_ID"));
		map.addValue("SOURCE_SYSTEM", SOURCE_SYSTEM);
		map.addValue("PSX_BATCH_ID", rs.getObject("PSX_BATCH_ID"));
		map.addValue("EOD_BATCH_ID", rs.getObject("EOD_BATCH_ID"));

		return map;
	}

	private String getPhoneNumber(CustomerPhoneNumber landLine1) {
		return StringUtils.trimToEmpty(landLine1.getPhoneCountryCode())
				+ (StringUtils.trimToEmpty(landLine1.getPhoneAreaCode()) + StringUtils.trimToEmpty(landLine1
						.getPhoneNumber()));
	}

	private MapSqlParameterSource mapCustData(ResultSet rs) throws SQLException {
		MapSqlParameterSource map = new MapSqlParameterSource();

		map.addValue("BATCHID", headerId);
		map.addValue("CUSTOMER_NO", rs.getObject("CUSTOMER_NO"));
		map.addValue("SOURCE_SYS_ID", SOURCE_SYSTEM_ID);
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
		map.addValue("SOURCE_SYSTEM", SOURCE_SYSTEM);
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
