package com.pennanttech.bajaj.process;

import com.pennant.backend.model.finance.TrailBalance;
import com.pennanttech.dataengine.DataEngineExport;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.App;
import com.pennanttech.pff.core.util.DateUtil;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.sql.DataSource;
import org.apache.commons.lang.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

public class TrailBalanceEngine extends DataEngineExport {
	public static DataEngineStatus EXTRACT_STATUS = new DataEngineStatus();

	private int batchSize = 1000;
	private Date appDate = null;
	private Date startDate = null;
	private Date endDate = null;
	private long headerId = 0;
	private long seqNo = 0;
	private Dimention dimention = null;
	
	private static final String QUERY_CONSOLIDATE = "select distinct HOSTACCOUNT from ACCOUNTMAPPING";
	private static final String QUERY_STATE = "select distinct HOSTACCOUNT, BRANCHCOUNTRY, BRANCHPROVINCE from ACCOUNTMAPPING, RMTBRANCHES";
	
	public enum Dimention{
		CONSOLIDATE, STATE, 
	}

	private Map<String, String> parameters = new HashMap<>();

	public TrailBalanceEngine(DataSource dataSource, long userId, Date valueDate, Date appDate) {
		super(dataSource, userId, App.DATABASE.name(), true, appDate, EXTRACT_STATUS);
		this.appDate = appDate;
	}

	public void doHealthCheck() throws Exception {
		prepareTrialBalanceDate();
		validateAccountMapping();
		vlidatePostings();
	}

	private void validateAccountMapping() throws Exception {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("START_DATE", startDate);
		paramMap.addValue("END_DATE", endDate);
		String sql = "Select count (*) from Postings where POSTDATE BETWEEN :START_DATE AND :END_DATE and POSTAMOUNT <>0 and account not in(select account from AccountMapping) ";

		if (parameterJdbcTemplate.queryForObject(sql, paramMap, Integer.class) > 0) {
			throw new AppException(
					"Account mapping is not configured, please check the Account Mapping report and configure the missing accounts.");
		}
	}

	private void vlidatePostings() throws Exception {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("START_DATE", startDate);
		paramMap.addValue("END_DATE", endDate);
		paramMap.addValue("DRORCR", "C");

		String sql = "select SUM(POSTAMOUNT) from POSTINGS where POSTDATE BETWEEN :START_DATE AND :END_DATE AND DRORCR = :DRORCR";

		BigDecimal creditAmount = parameterJdbcTemplate.queryForObject(sql, paramMap, BigDecimal.class);
		paramMap.addValue("DRORCR", "D");
		BigDecimal debitAmount = parameterJdbcTemplate.queryForObject(sql, paramMap, BigDecimal.class);

		if (creditAmount.compareTo(debitAmount) != 0) {
			throw new AppException("Credit and Debit amounts not matched for the transactions between "
					.concat(DateUtil.format(startDate, DateFormat.LONG_DATE)).concat(" and ")
					.concat(DateUtil.format(endDate, DateFormat.LONG_DATE)));
		}
	}

	public void extractReport(Dimention dimention) throws Exception {
		this.dimention = dimention;
		
		doHealthCheck();
		
		extract();

		Map<String, Object> parameterMap = new HashMap<>();
		parameterMap.put("START_DATE", DateUtil.format(startDate, "ddMMyyyy"));
		parameterMap.put("END_DATE", DateUtil.format(endDate, "ddMMyyyy"));
		parameterMap.put("HEADER_ID", seqNo);
		parameterMap.put("DIMENSION", dimention.name());
		parameterMap.put("COMPANY_NAME", parameters.get("TRAIL_BALANCE_COMPANY_NAME"));
		parameterMap.put("REPORT_NAME", "Consolidated Trial Balance - Ledger A/C wise");
		
		Map<String, Object> filterMap = new HashMap<>();
		filterMap.put("HEADER_ID", seqNo);
		filterMap.put("DIMENSION", dimention.name());

		StringBuilder builder = new StringBuilder();
		builder.append("From ");
		builder.append(DateUtil.format(startDate, "dd-MMM-yy").toUpperCase());
		builder.append(" To ");
		builder.append(DateUtil.format(endDate, "dd-MMM-yy").toUpperCase());

		parameterMap.put("TRANSACTION_DURATION", builder.toString());
		parameterMap.put("CURRENCY", parameters.get("APP_DFT_CURR").concat(" - ".concat(parameters.get("APP_DFT_CURR"))));
		
		setFilterMap(filterMap);

		setParameterMap(parameterMap);
		if (dimention == Dimention.STATE) {
			exportData("TRIAL_BALANCE_EXPORT_STATE");
		} else {
			exportData("TRIAL_BALANCE_EXPORT_CONSOLIDATE");
		}
		
	}

	private void extract() throws Exception {
		logger.info("Extracting data...");
		initilize();

		Map<String, TrailBalance> accounts = getLedgerAccounts();
		totalRecords = accounts.size();
		EXTRACT_STATUS.setTotalRecords(totalRecords);
		// Get group code and description.
		Map<String, TrailBalance> groups = getAccountDetails();
		
		String key = null;
		TrailBalance group = null;
		TrailBalance trialBalance = null;

		for (Entry<String, TrailBalance> entry : accounts.entrySet()) {
			
			if (dimention == Dimention.STATE) {
				key = entry.getKey().split("-")[0];
			} else {
				key = entry.getKey();
			}
			
			trialBalance = entry.getValue();
			group = groups.get(key);

			if (group == null || group.getAccountType() == null) {
				trialBalance.setAccountType("NA");
				trialBalance.setAccountTypeDes("NA");
			} else {
				trialBalance.setAccountType(group.getAccountType());
				trialBalance.setAccountTypeDes(group.getAccountTypeDes());
			}

			EXTRACT_STATUS.setProcessedRecords(processedCount++);
			EXTRACT_STATUS.setSuccessRecords(successCount++);
		}
		groups = null;

		// Get opening balance
		List<TrailBalance> openingBals = getOpeningBalance();
		key = null;

		for (TrailBalance openingBal : openingBals) {
			key = getKey(openingBal);
			trialBalance = accounts.get(key);

			if (openingBal.getOpeningBalance() == null
					|| openingBal.getOpeningBalance().compareTo(BigDecimal.ZERO) == 0) {
				trialBalance.setOpeningBalance(BigDecimal.ZERO);
				trialBalance.setOpeningBalanceType("Cr");
			} else {
				trialBalance.setOpeningBalance(openingBal.getOpeningBalance());
				trialBalance.setOpeningBalanceType(openingBal.getOpeningBalanceType());
			}

			if (StringUtils.equals(openingBal.getClosingBalanceType(), "Dr")) {
				trialBalance.setClosingBalance(BigDecimal.ZERO.subtract(openingBal.getClosingBalance()));
			}
		}
		openingBals = null;

		// Get debit amount
		List<TrailBalance> debitAmounts = getDebitAmount();
		key = null;

		for (TrailBalance debitAmount : debitAmounts) {
			key = getKey(debitAmount);
			trialBalance = accounts.get(key);

			// FIXME: Fix the data.
			if (trialBalance == null) {
				continue;
			}

			if (debitAmount.getDebitAmount() == null) {
				trialBalance.setDebitAmount(BigDecimal.ZERO);
			} else {
				trialBalance.setDebitAmount(debitAmount.getDebitAmount());
			}
		}
		debitAmounts = null;

		// Get credit amount
		List<TrailBalance> creditAmounts = getCreditAmount();
		key = null;

		for (TrailBalance creditAmount : creditAmounts) {
			key = getKey(creditAmount);
			trialBalance = accounts.get(key);

			// FIXME: Fix the data.
			if (trialBalance == null) {
				continue;
			}

			if (creditAmount.getCreditAmount() == null) {
				trialBalance.setCreditAmount(BigDecimal.ZERO);
			} else {
				trialBalance.setCreditAmount(creditAmount.getCreditAmount());
			}
		}
		creditAmounts = null;

		// Calculate closing balance.
		for (Entry<String, TrailBalance> entry : accounts.entrySet()) {
			trialBalance = entry.getValue();
			BigDecimal openingBal = trialBalance.getOpeningBalance();

			if (trialBalance.getOpeningBalanceType().equals("Dr")) {
				openingBal = openingBal.negate();
			}

			trialBalance.setClosingBalance(
					openingBal.subtract(trialBalance.getDebitAmount()).add(trialBalance.getCreditAmount()));

			if (trialBalance.getClosingBalance().compareTo(BigDecimal.ZERO) < 0) {
				trialBalance.setClosingBalanceType("Dr");
				trialBalance.setClosingBalance(trialBalance.getClosingBalance().abs());
			} else {
				trialBalance.setClosingBalanceType("Cr");
			}
		}

		// Save to database
		save(accounts);

		// Log the derived month trail balance into separate table 
		logTrialBalace();
	}

	private String getKey(TrailBalance openingBal) {
		if (dimention == Dimention.STATE) {
			return openingBal.getLedgerAccount().concat("-").concat(openingBal.getStateCode());
		} else {
			return openingBal.getLedgerAccount();
		}
	}
	
	private void createNextId() {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("DIMENSION", dimention.name());
		
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT COALESCE(MAX(SEQNO), 0) + 1");
		sql.append(" FROM TRIAL_BALANCE_HEADER WHERE DIMENSION = :DIMENSION");
		this.seqNo = this.parameterJdbcTemplate.queryForObject(sql.toString(), paramMap, Long.class);
		
		sql = new StringBuilder();
		sql.append(" SELECT COALESCE(MAX(ID), 0) + 1");
		sql.append(" FROM TRIAL_BALANCE_HEADER");
		this.headerId = this.jdbcTemplate.queryForObject(sql.toString(), Long.class);
	}

	private long createHeader() throws Exception {
		logger.info("Creating the trail Balance Header..");
		createNextId();

		MapSqlParameterSource paramMap;
		StringBuilder sql = new StringBuilder();
		sql.append(" INSERT INTO TRIAL_BALANCE_HEADER(");
		sql.append(" ID, SEQNO, DIMENSION, COMPANYNAME, REPORTNAME, STARTDATE, ENDDATE, CURRENCY) VALUES (");
		sql.append(" :ID, :SEQNO, :DIMENSION, :COMPANYNAME, :REPORTNAME, :STARTDATE, :ENDDATE, :CURRENCY)");

		paramMap = new MapSqlParameterSource();
		paramMap.addValue("ID", headerId);
		paramMap.addValue("SEQNO", seqNo);
		paramMap.addValue("DIMENSION", dimention.name());
		paramMap.addValue("COMPANYNAME", parameters.get("TRAIL_BALANCE_COMPANY_NAME"));
		paramMap.addValue("REPORTNAME", "Trial Balance Report");
		paramMap.addValue("STARTDATE", startDate);
		paramMap.addValue("ENDDATE", endDate);
		paramMap.addValue("CURRENCY", parameters.get("APP_DFT_CURR"));
		paramMap.addValue("DIMENSION", dimention.name());

		try {
			parameterJdbcTemplate.update(sql.toString(), paramMap);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw new Exception("Unable to insert trail balance header.");
		}

		return seqNo;
	}

	private Map<String, TrailBalance> getLedgerAccounts() throws Exception {
		createHeader();

		String query = null;

		if (dimention == Dimention.STATE) {
			query = QUERY_STATE;
		} else {
			query = QUERY_CONSOLIDATE;
		}

		return jdbcTemplate.query(query, new ResultSetExtractor<Map<String, TrailBalance>>() {
			@Override
			public Map<String, TrailBalance> extractData(ResultSet rs) throws SQLException, DataAccessException {
				Map<String, TrailBalance> map = new HashMap<>();

				while (rs.next()) {
					TrailBalance item = new TrailBalance();
					item.setHeaderId(headerId);
					item.setDimention(dimention.name());
					item.setLedgerAccount(rs.getString("HOSTACCOUNT"));
					item.setOpeningBalance(BigDecimal.ZERO);
					item.setOpeningBalanceType("Cr");
					item.setDebitAmount(BigDecimal.ZERO);
					item.setCreditAmount(BigDecimal.ZERO);

					if (dimention == Dimention.STATE) {
						item.setCountryCode(rs.getString("BRANCHCOUNTRY"));
						item.setStateCode(rs.getString("BRANCHPROVINCE"));
						map.put(getKey(item), item);
					} else {
						map.put(item.getLedgerAccount(), item);
					}
				}
				return map;
			}
		});
	}

	private void prepareTrialBalanceDate() throws Exception {
		logger.info("Preparing Trailbalance Date..");
		startDate = DateUtil.getMonthStart(appDate);
		endDate = appDate;

		if (startDate.compareTo(endDate) != 0) {
			logger.info("Start Date: " + DateUtil.format(startDate, DateUtil.DateFormat.LONG_DATE));
			logger.info("End Date: " + DateUtil.format(endDate, DateUtil.DateFormat.LONG_DATE));
			return;
		}

		Date date = DateUtil.addMonths(startDate, -1);

		startDate = DateUtil.getMonthStart(date);
		endDate = DateUtil.getMonthEnd(date);

		logger.info("Start Date: " + DateUtil.format(startDate, DateUtil.DateFormat.LONG_DATE));
		logger.info("End Date: " + DateUtil.format(endDate, DateUtil.DateFormat.LONG_DATE));
	}

	private void initilize() throws Exception {
		loadParameters();
		clearTables();
	}

	private void clearTables() {
		logger.info("Clearing staging tables..");
		jdbcTemplate.execute("DELETE FROM TRIAL_BALANCE_REPORT_FILE");
		
		if (App.DATABASE == App.Database.ORACLE) {
			jdbcTemplate
					.execute("alter table TRIAL_BALANCE_REPORT_FILE modify ID generated as identity (start with 1)");
		} else if(App.DATABASE == App.Database.SQL_SERVER) {
			jdbcTemplate.execute("dbcc checkident ('TRIAL_BALANCE_REPORT_FILE', reseed, 0)");
		}

		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("START_DATE", startDate);
		paramMap.addValue("END_DATE", endDate);
		paramMap.addValue("DIMENSION", dimention.name());
		
		if (dimention == Dimention.STATE) {
			paramMap.addValue("NAME", "TRIAL_BALANCE_EXPORT_STATE");
		} else {
			paramMap.addValue("NAME", "TRIAL_BALANCE_EXPORT_CONSOLIDATE");
		}
		
		
		// delete the data between start and end date if already available
		StringBuilder sql = new StringBuilder();
		sql.append(" DELETE FROM TRIAL_BALANCE_REPORT WHERE HEADERID = ");
		sql.append(" (SELECT HEADERID FROM TRIAL_BALANCE_HEADER");
		sql.append(" WHERE STARTDATE BETWEEN :START_DATE AND :END_DATE AND DIMENSION = :DIMENSION)");
		parameterJdbcTemplate.update(sql.toString(), paramMap);

		sql = new StringBuilder();
		sql.append(" DELETE FROM TRIAL_BALANCE_REPORT_LAST_RUN WHERE HEADERID =");
		sql.append(" (SELECT HEADERID FROM TRIAL_BALANCE_HEADER");
		sql.append(" WHERE STARTDATE BETWEEN :START_DATE AND :END_DATE AND DIMENSION = :DIMENSION)");
		parameterJdbcTemplate.update(sql.toString(), paramMap);

		sql = new StringBuilder();
		sql = sql.append("DELETE FROM TRIAL_BALANCE_HEADER WHERE STARTDATE BETWEEN :START_DATE AND :END_DATE");
		sql.append(" AND DIMENSION = :DIMENSION");
		parameterJdbcTemplate.update(sql.toString(), paramMap);

		sql = new StringBuilder();
		sql = sql.append("Delete from DATA_ENGINE_LOG where ID IN (");
		sql.append("SELECT ID FROM DATA_ENGINE_STATUS where ValueDate BETWEEN :START_DATE AND :END_DATE AND NAME = :NAME)");
		parameterJdbcTemplate.update(sql.toString(), paramMap);

		sql = new StringBuilder();
		sql = sql.append("Delete from DATA_ENGINE_STATUS");
		sql.append(" where ValueDate BETWEEN :START_DATE AND :END_DATE AND NAME = :NAME");
		parameterJdbcTemplate.update(sql.toString(), paramMap);

	}

	private void loadParameters() {
		logger.info("Loading parameters..");
		MapSqlParameterSource paramMap;

		StringBuilder sql = new StringBuilder();
		paramMap = new MapSqlParameterSource();

		sql.append("SELECT SYSPARMCODE, SYSPARMVALUE FROM SMTPARAMETERS where SYSPARMCODE");
		sql.append(" IN (:TRAIL_BALANCE_COMPANY_NAME, :APP_DFT_CURR)");

		paramMap.addValue("TRAIL_BALANCE_COMPANY_NAME", "TRAIL_BALANCE_COMPANY_NAME");
		paramMap.addValue("APP_DFT_CURR", "APP_DFT_CURR");

		parameterJdbcTemplate.query(sql.toString(), paramMap, new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				parameters.put(rs.getString("SYSPARMCODE"), rs.getString("SYSPARMVALUE"));
			}
		});
	}

	private Map<String, TrailBalance> getAccountDetails() {
		StringBuilder sql = new StringBuilder();
		sql.append(" select AM.HOSTACCOUNT, ATG.GROUPCODE, AT.ACTYPEDESC");
		sql.append(" from ACCOUNTMAPPING AM");
		sql.append(" INNER JOIN RMTACCOUNTTYPES AT ON AT.ACTYPE = AM.ACCOUNTTYPE");
		sql.append(" INNER JOIN ACCOUNTTYPEGROUP ATG  ON ATG.GROUPID = AT.ACTYPEGRPID");

		try {
			return extractAccountDetails(sql);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		return null;
	}

	private Map<String, TrailBalance> extractAccountDetails(StringBuilder sql) {
		return parameterJdbcTemplate.query(sql.toString(), new MapSqlParameterSource(),
				new ResultSetExtractor<Map<String, TrailBalance>>() {
					@Override
					public Map<String, TrailBalance> extractData(ResultSet rs)
							throws SQLException, DataAccessException {
						Map<String, TrailBalance> map = new HashMap<>();

						while (rs.next()) {
							TrailBalance trailBalance = new TrailBalance();
							trailBalance.setLedgerAccount(rs.getString("HOSTACCOUNT"));
							trailBalance.setAccountType(rs.getString("GROUPCODE"));
							trailBalance.setAccountTypeDes(rs.getString("ACTYPEDESC"));

							map.put(trailBalance.getLedgerAccount(), trailBalance);
						}

						return map;
					}
				});
	}

	private List<TrailBalance> getOpeningBalance() {
		MapSqlParameterSource paramMap =  new MapSqlParameterSource();;
		
		StringBuilder sql = new StringBuilder();
		sql.append(" select AM.HOSTACCOUNT ledgerAccount, LR.PROVINCE stateCode,");
		sql.append(" LR.CLOSINGBAL openingBalance, LR.CLOSINGBALTYPE openingBalanceType");
		sql.append(" from ACCOUNTMAPPING AM");
		sql.append(" INNER JOIN TRIAL_BALANCE_REPORT_LAST_RUN LR ON LR.HOSTACCOUNT = AM.HOSTACCOUNT");
		sql.append(" INNER JOIN TRIAL_BALANCE_HEADER TH ON TH.ID = LR.HEADERID");
		sql.append(" WHERE TH.DIMENSION = :DIMENSION");
		
		paramMap.addValue("DIMENSION", dimention.name());
		RowMapper<TrailBalance> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(TrailBalance.class);

		try {
			return parameterJdbcTemplate.query(sql.toString(), paramMap, typeRowMapper);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		return new ArrayList<TrailBalance>();
	}

	private List<TrailBalance> getDebitAmount() {
		MapSqlParameterSource paramMap = null;

		StringBuilder sql = new StringBuilder();

		if (dimention == Dimention.STATE) {
			sql.append("select AM.HOSTACCOUNT ledgerAccount,RB.BRANCHPROVINCE stateCode, sum(postAmount) debitAmount");
			sql.append(" from POSTINGS P");
			sql.append(" INNER JOIN ACCOUNTMAPPING AM ON AM.Account = P.Account");
			sql.append(" INNER JOIN RMTBRANCHES RB ON RB.BRANCHCODE = P.POSTBRANCH");
			sql.append(" where POSTDATE BETWEEN :MONTH_STARTDATE AND :MONTH_ENDDATE");
			sql.append(" and P.DRORCR = :DRORCR");
			sql.append(" group by AM.HOSTACCOUNT, RB.BRANCHPROVINCE");
		} else {
			sql.append(" select AM.HOSTACCOUNT ledgerAccount, sum(postAmount) debitAmount");
			sql.append(" from POSTINGS P");
			sql.append(" INNER JOIN ACCOUNTMAPPING AM ON AM.Account = P.Account");
			sql.append(" where POSTDATE BETWEEN :MONTH_STARTDATE AND :MONTH_ENDDATE");
			sql.append(" and P.DRORCR = :DRORCR");
			sql.append(" group by AM.HOSTACCOUNT");
		}

		paramMap = new MapSqlParameterSource();
		paramMap.addValue("MONTH_STARTDATE", startDate);
		paramMap.addValue("MONTH_ENDDATE", endDate);
		paramMap.addValue("DRORCR", "D");

		RowMapper<TrailBalance> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(TrailBalance.class);

		try {
			return parameterJdbcTemplate.query(sql.toString(), paramMap, typeRowMapper);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		return null;
	}

	private List<TrailBalance> getCreditAmount() {
		MapSqlParameterSource paramMap = null;

		StringBuilder sql = new StringBuilder();
		
		if (dimention == Dimention.STATE) {
			sql.append("select AM.HOSTACCOUNT ledgerAccount,RB.BRANCHPROVINCE stateCode, sum(postAmount) creditAmount");
			sql.append(" from POSTINGS P");
			sql.append(" INNER JOIN ACCOUNTMAPPING AM ON AM.Account = P.Account");
			sql.append(" INNER JOIN RMTBRANCHES RB ON RB.BRANCHCODE = P.POSTBRANCH");
			sql.append(" where POSTDATE BETWEEN :MONTH_STARTDATE AND :MONTH_ENDDATE");
			sql.append(" and P.DRORCR = :DRORCR");
			sql.append(" group by AM.HOSTACCOUNT, RB.BRANCHPROVINCE");
		} else {
			sql.append("select AM.HOSTACCOUNT ledgerAccount, sum(postAmount) creditAmount");
			sql.append(" from POSTINGS P");
			sql.append(" INNER JOIN ACCOUNTMAPPING AM ON AM.Account = P.Account");
			sql.append(" where POSTDATE BETWEEN :MONTH_STARTDATE AND :MONTH_ENDDATE");
			sql.append(" and P.DRORCR = :DRORCR");
			sql.append(" group by AM.HOSTACCOUNT");
		}

		paramMap = new MapSqlParameterSource();
		paramMap.addValue("MONTH_STARTDATE", startDate);
		paramMap.addValue("MONTH_ENDDATE", endDate);
		paramMap.addValue("DRORCR", "C");

		RowMapper<TrailBalance> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(TrailBalance.class);

		try {
			return parameterJdbcTemplate.query(sql.toString(), paramMap, typeRowMapper);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		return null;
	}

	private void save(Map<String, TrailBalance> accounts) throws Exception {
		List<TrailBalance> list = new ArrayList<>();
		for (Entry<String, TrailBalance> entry : accounts.entrySet()) {
			list.add(entry.getValue());

			if (list.size() >= batchSize) {
				save(list);
				list.clear();
			}
		}

		if (!list.isEmpty()) {
			save(list);
		}

		list = null;
	}

	private void save(List<TrailBalance> list) throws SQLException {
		StringBuilder sql = new StringBuilder();

		sql.append("INSERT INTO TRIAL_BALANCE_REPORT VALUES(");
		sql.append(" :HeaderId,");
		sql.append(" :AccountType,");
		sql.append(" :CountryCode,");
		sql.append(" :StateCode,");
		sql.append(" :LedgerAccount,");
		sql.append(" :AccountTypeDes,");
		sql.append(" :OpeningBalance,");
		sql.append(" :OpeningBalanceType,");
		sql.append(" :DebitAmount,");
		sql.append(" :CreditAmount,");
		sql.append(" :ClosingBalance,");
		sql.append(" :ClosingBalanceType)");

		parameterJdbcTemplate.batchUpdate(sql.toString(), SqlParameterSourceUtils.createBatch(list.toArray()));
	}

	private void logTrialBalace() throws Exception {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("HEADERID", headerId);
		paramMap.addValue("DIMENSION", dimention.name());

		try {
			StringBuilder sql = new StringBuilder();
			sql.append(" DELETE FROM TRIAL_BALANCE_REPORT_LAST_RUN WHERE HEADERID =");
			sql.append(" (SELECT MAX(ID) FROM TRIAL_BALANCE_HEADER WHERE DIMENSION = :DIMENSION AND ID <> :HEADERID)");
			parameterJdbcTemplate.update(sql.toString(), paramMap);
			
			sql = new StringBuilder();
			sql.append(" INSERT INTO TRIAL_BALANCE_REPORT_LAST_RUN");
			sql.append(" SELECT * FROM TRIAL_BALANCE_REPORT WHERE HEADERID = :HEADERID");
			parameterJdbcTemplate.update(sql.toString(), paramMap);
			
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw new Exception("Unable to insert current month trail balance.");
		}
		
		if (dimention == Dimention.STATE) {
			addGroupHeader();
		} else {
			logInFileTable();
		}
	}

	private Map<String, String> getStateDescriptions() {
		StringBuilder sql = new StringBuilder();
		sql.append(" select CPPROVINCE, CPPROVINCENAME from RMTCOUNTRYVSPROVINCE");

		try {
			return extractStateDescriptions(sql);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		return null;
	}

	private Map<String, String> extractStateDescriptions(StringBuilder sql) {
		return jdbcTemplate.query(sql.toString(), new ResultSetExtractor<Map<String, String>>() {
			@Override
			public Map<String, String> extractData(ResultSet rs) throws SQLException, DataAccessException {
				Map<String, String> map = new HashMap<>();

				while (rs.next()) {
					map.put(rs.getString("CPPROVINCE"), rs.getString("CPPROVINCENAME"));
				}

				return map;
			}
		});
	}
	
	private void logInFileTable() {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("HEADERID", headerId);
		
		StringBuilder data = new StringBuilder();
		data.append("INSERT INTO TRIAL_BALANCE_REPORT_FILE(ACTYPEGRPID, COUNTRY, PROVINCE, HOSTACCOUNT, DESCRIPTION,");
		data.append(" OPENINGBAL, OPENINGBALTYPE, DEBITAMOUNT, CREDITAMOUNT, CLOSINGBAL, CLOSINGBALTYPE)");
		data.append(" select ACTYPEGRPID, COUNTRY, PROVINCE, HOSTACCOUNT, DESCRIPTION, OPENINGBAL, OPENINGBALTYPE,");
		data.append(" DEBITAMOUNT, CREDITAMOUNT, CLOSINGBAL, CLOSINGBALTYPE");
		data.append(" from TRIAL_BALANCE_REPORT_VIEW WHERE HEADERID = :HEADERID");
		parameterJdbcTemplate.update(data.toString(), paramMap);
	}

	private void addGroupHeader() {
		Map<String, String> states = getStateDescriptions();

		StringBuilder data = new StringBuilder();
		data.append("INSERT INTO TRIAL_BALANCE_REPORT_FILE(ACTYPEGRPID, COUNTRY, PROVINCE, HOSTACCOUNT, DESCRIPTION,");
		data.append(" OPENINGBAL, OPENINGBALTYPE, DEBITAMOUNT, CREDITAMOUNT, CLOSINGBAL, CLOSINGBALTYPE)");
		data.append(" select ACTYPEGRPID, COUNTRY, PROVINCE, HOSTACCOUNT, DESCRIPTION, OPENINGBAL, OPENINGBALTYPE,");
		data.append(" DEBITAMOUNT, CREDITAMOUNT, CLOSINGBAL, CLOSINGBALTYPE");
		data.append(" from TRIAL_BALANCE_REPORT_VIEW where PROVINCE=:PROVINCE");
		MapSqlParameterSource dataMap = new MapSqlParameterSource();

		String emptyLine = "INSERT INTO TRIAL_BALANCE_REPORT_FILE(CLOSINGBALTYPE) VALUES(:CLOSINGBALTYPE)";
		MapSqlParameterSource emptyLineMap = new MapSqlParameterSource();
		emptyLineMap.addValue("CLOSINGBALTYPE", null);

		StringBuilder group = new StringBuilder();
		group.append("INSERT INTO TRIAL_BALANCE_REPORT_FILE(ACTYPEGRPID, COUNTRY, PROVINCE, HOSTACCOUNT, DESCRIPTION,");
		group.append(" OPENINGBAL, OPENINGBALTYPE, DEBITAMOUNT, CREDITAMOUNT, CLOSINGBAL, CLOSINGBALTYPE)");
		group.append(" VALUES(:ACTYPEGRPID, :COUNTRY, :PROVINCE, :HOSTACCOUNT, :DESCRIPTION,");
		group.append(" :OPENINGBAL, :OPENINGBALTYPE, :DEBITAMOUNT, :CREDITAMOUNT, :CLOSINGBAL, :CLOSINGBALTYPE)");

		MapSqlParameterSource groupMap = new MapSqlParameterSource();
		groupMap.addValue("ACTYPEGRPID", "PARENT GROUP");
		groupMap.addValue("COUNTRY", "");
		groupMap.addValue("PROVINCE", "");
		groupMap.addValue("HOSTACCOUNT", "LEDGER");
		groupMap.addValue("DESCRIPTION", "DESCRIPTION");
		groupMap.addValue("OPENINGBAL", "OPENING BALANCE");
		groupMap.addValue("OPENINGBALTYPE", "CR/DR");
		groupMap.addValue("DEBITAMOUNT", "DEBIT AMOUNT");
		groupMap.addValue("CREDITAMOUNT", "CREDIT AMOUNT");
		groupMap.addValue("CLOSINGBAL", "CLOSING BALANCE");
		groupMap.addValue("CLOSINGBALTYPE", "CR/DR");

		String select = "select PROVINCE, count(*) from TRIAL_BALANCE_REPORT_LAST_RUN where HEADERID = ? group by PROVINCE";
		jdbcTemplate.query(select, new Object[] { headerId }, new RowCallbackHandler() {
			int groupId = 0;

			@Override
			public void processRow(ResultSet rs) throws SQLException {
				try {
					dataMap.addValue("PROVINCE", rs.getString("PROVINCE"));
					dataMap.addValue("DESCRIPTION", states.get(rs.getString("PROVINCE")));

					if (groupId > 0) {
						parameterJdbcTemplate.update(emptyLine, emptyLineMap);
						parameterJdbcTemplate.update(emptyLine, emptyLineMap);
					}
					parameterJdbcTemplate
							.update("INSERT INTO TRIAL_BALANCE_REPORT_FILE(DESCRIPTION) VALUES(:DESCRIPTION)", dataMap);
					parameterJdbcTemplate.update(group.toString(), groupMap);
					parameterJdbcTemplate.update(data.toString(), dataMap);
					groupId++;

				} catch (Exception e) {
					logger.error(Literal.EXCEPTION, e);
				}
			}
		});
	}
	
	
	public boolean isBatchExists() throws Exception {
		prepareTrialBalanceDate();
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("STARTDATE", startDate);
		paramMap.addValue("ENDDATE", endDate);

		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT count(*) from ");
		sql.append(" TRIAL_BALANCE_HEADER WHERE STARTDATE = :STARTDATE AND ENDDATE = :ENDDATE");

		int count = 0;
		try {
			count = this.parameterJdbcTemplate.queryForObject(sql.toString(), paramMap, Integer.class);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		if (count > 0) {
			return true;
		} else {
			return false;
		}
	}

}
