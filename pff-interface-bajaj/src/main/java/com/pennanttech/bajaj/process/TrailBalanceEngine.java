package com.pennanttech.bajaj.process;

import com.pennant.backend.model.finance.TrailBalance;
import com.pennanttech.dataengine.DataEngineExport;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.App;
import com.pennanttech.pff.core.util.DateUtil;
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
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

public class TrailBalanceEngine extends DataEngineExport {
	public static DataEngineStatus TB_STATUS = new DataEngineStatus("GL_TRAIL_BALANCE_EXPORT");

	private int batchSize = 1000;
	private Date appDate = null;
	private Date startDate = null;
	private Date endDate = null;
	private long headerId = 0;
	private String fileName = null;

	private Map<String, String> parameters = new HashMap<>();

	public TrailBalanceEngine(DataSource dataSource, long userId, Date valueDate, Date appDate) {
		super(dataSource, userId, App.DATABASE.name(), true, valueDate, TB_STATUS);
		this.appDate = appDate;
	}

	public void extractReport() throws Exception {
		generate();

		Map<String, Object> parameterMap = new HashMap<>();
		parameterMap.put("START_DATE", DateUtil.format(startDate, "ddMMyyyy"));
		parameterMap.put("END_DATE", DateUtil.format(endDate, "ddMMyyyy"));
		parameterMap.put("HEADER_ID", headerId);

		parameterMap.put("COMPANY_NAME", parameters.get("TRAIL_BALANCE_COMPANY_NAME"));
		parameterMap.put("REPORT_NAME", "Consolidated Trial Balance - Ledger A/C wise");
		parameterMap.put("FILE_NAME", fileName);

		StringBuilder builder = new StringBuilder();
		builder.append("From ");
		builder.append(DateUtil.format(startDate, "dd-MMM-yy").toUpperCase());
		builder.append(" To ");
		builder.append(DateUtil.format(endDate, "dd-MMM-yy").toUpperCase());

		parameterMap.put("TRANSACTION_DURATION", builder.toString());
		parameterMap.put("CURRENCY", parameters.get("APP_DFT_CURR").concat(" - ".concat(parameters.get("APP_DFT_CURR"))));

		setParameterMap(parameterMap);
		exportData("GL_TRAIL_BALANCE_EXPORT");
	}

	public void generate() throws Exception {
		logger.info("Extracting data...");
		initilize();

		Map<String, TrailBalance> accounts = getLedgerAccounts();
		totalRecords = accounts.size();
		TB_STATUS.setTotalRecords(totalRecords);
		// Get group code and description.
		Map<String, TrailBalance> groups = getAccountDetails();
		String key = null;
		TrailBalance group = null;
		TrailBalance trialBalance = null;

		for (Entry<String, TrailBalance> entry : accounts.entrySet()) {
			key = entry.getKey().split("-")[0];

			trialBalance = entry.getValue();
			group = groups.get(key);

			if (group == null || group.getAccountType() == null) {
				trialBalance.setAccountType("NA");
				trialBalance.setAccountTypeDes("NA");
			} else {
				trialBalance.setAccountType(group.getAccountType());
				trialBalance.setAccountTypeDes(group.getAccountTypeDes());
			}
			
			TB_STATUS.setTotalRecords(processedCount++);
			TB_STATUS.setTotalRecords(successCount++);
		}
		groups = null;

		// Get opening balance
		List<TrailBalance> openingBals = getOpeningBalance();
		key = null;

		for (TrailBalance openingBal : openingBals) {
			key = openingBal.getLedgerAccount().concat("-").concat(openingBal.getStateCode());
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
			key = debitAmount.getLedgerAccount().concat("-").concat(debitAmount.getStateCode());
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
			key = creditAmount.getLedgerAccount().concat("-").concat(creditAmount.getStateCode());
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

	private void createNextId() {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT COALESCE(MAX(ID), 0) + 1");
		sql.append(" FROM TRAIL_BALANCE_HEADER");

		try {
			this.headerId = this.jdbcTemplate.queryForObject(sql.toString(), Long.class);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}
	}

	private void createFileName() {
		StringBuilder builder = new StringBuilder();
		builder.append("TRIAL_BALANCE");
		builder.append("_");
		builder.append(DateUtil.format(startDate, "ddMMyyyy"));
		builder.append("_");
		builder.append(DateUtil.format(endDate, "ddMMyyyy"));
		builder.append("_");
		builder.append(String.valueOf(headerId));
		builder.append(".CSV");

		fileName = builder.toString();
	}

	private long createHeader() throws Exception {
		logger.info("Creating the trail Balance Header..");
		createNextId();
		createFileName();

		MapSqlParameterSource paramMap;
		StringBuilder sql = new StringBuilder();
		sql.append(" INSERT INTO TRAIL_BALANCE_HEADER VALUES(");
		sql.append(" :ID,");
		sql.append(" :FILENAME,");
		sql.append(" :COMPANYNAME,");
		sql.append(" :REPORTNAME,");
		sql.append(" :STARTDATE,");
		sql.append(" :ENDDATE,");
		sql.append(" :CURRENCY");
		sql.append(")");

		paramMap = new MapSqlParameterSource();
		paramMap.addValue("ID", headerId);
		paramMap.addValue("FILENAME", fileName);
		paramMap.addValue("COMPANYNAME", parameters.get("TRAIL_BALANCE_COMPANY_NAME"));
		paramMap.addValue("REPORTNAME", "Trial Balance Report");
		paramMap.addValue("STARTDATE", startDate);
		paramMap.addValue("ENDDATE", endDate);
		paramMap.addValue("CURRENCY", parameters.get("APP_DFT_CURR"));

		try {
			parameterJdbcTemplate.update(sql.toString(), paramMap);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw new Exception("Unable to insert trail balance header.");
		}

		return headerId;
	}

	private Map<String, TrailBalance> getLedgerAccounts() throws Exception {
		createHeader();

		String query = "select distinct HOSTACCOUNT, BRANCHCOUNTRY, BRANCHPROVINCE from ACCOUNTMAPPING, RMTBRANCHES";
		return jdbcTemplate.query(query, new ResultSetExtractor<Map<String, TrailBalance>>() {
			@Override
			public Map<String, TrailBalance> extractData(ResultSet rs) throws SQLException, DataAccessException {
				Map<String, TrailBalance> map = new HashMap<>();

				while (rs.next()) {
					TrailBalance item = new TrailBalance();
					item.setHeaderId(headerId);
					item.setLedgerAccount(rs.getString("HOSTACCOUNT"));
					item.setCountryCode(rs.getString("BRANCHCOUNTRY"));
					item.setStateCode(rs.getString("BRANCHPROVINCE"));
					item.setOpeningBalance(BigDecimal.ZERO);
					item.setOpeningBalanceType("Cr");
					item.setDebitAmount(BigDecimal.ZERO);
					item.setCreditAmount(BigDecimal.ZERO);

					map.put(item.getLedgerAccount().concat("-").concat(item.getStateCode()), item);

				}
				return map;
			}
		});
	}

	private void prepareTrialBalanceDate() throws Exception {
		logger.info("Preparing Trailbalance Date..");
		
		startDate = DateUtil.getMonthStart(appDate);
		endDate = appDate;
	}

	private void initilize() throws Exception {
		loadParameters();
		clearTables();
		prepareTrialBalanceDate();
	}

	private void clearTables() {
		logger.info("Clearing staging tables..");	
		jdbcTemplate.execute("DELETE FROM TRAIL_BALANCE_REPORT_FILE");

		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("START_DATE", DateUtil.getMonthStart(appDate));
		paramMap.addValue("END_DATE", DateUtil.getMonthEnd(appDate));
		
		
		StringBuilder sql = new StringBuilder();
		sql.append(" DELETE FROM TRAIL_BALANCE_REPORT WHERE HEADERID IN (");
		sql.append(" SELECT ID FROM TRAIL_BALANCE_HEADER WHERE STARTDATE BETWEEN :START_DATE AND :END_DATE)");
		parameterJdbcTemplate.update(sql.toString(), paramMap);
		
		sql = new StringBuilder();
		sql = sql.append("DELETE FROM TRAIL_BALANCE_HEADER WHERE STARTDATE BETWEEN :START_DATE AND :END_DATE");
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
		StringBuilder sql = new StringBuilder();
		sql.append(" select AM.HOSTACCOUNT ledgerAccount, LR.PROVINCE stateCode,");
		sql.append(" LR.CLOSINGBAL openingBalance, LR.CLOSINGBALTYPE openingBalanceType");
		sql.append(" from ACCOUNTMAPPING AM");
		sql.append(" INNER JOIN TRAIL_BALANCE_REPORT_LAST_RUN LR ON LR.HOSTACCOUNT = AM.HOSTACCOUNT");

		RowMapper<TrailBalance> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(TrailBalance.class);

		try {
			return jdbcTemplate.query(sql.toString(), typeRowMapper);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		return new ArrayList<TrailBalance>();
	}

	private List<TrailBalance> getDebitAmount() {
		MapSqlParameterSource paramMap = null;

		StringBuilder sql = new StringBuilder();
		sql.append(" select AM.HOSTACCOUNT ledgerAccount, RB.BRANCHPROVINCE stateCode, sum(postAmount) debitAmount");
		sql.append(" from POSTINGS P");
		sql.append(" INNER JOIN ACCOUNTMAPPING AM ON AM.Account = P.Account");
		sql.append(" INNER JOIN FINANCEMAIN FM ON FM.FINREFERENCE = P.FINREFERENCE");
		sql.append(" INNER JOIN RMTBRANCHES RB ON RB.BRANCHCODE = FM.FINBRANCH");
		sql.append(" where POSTDATE BETWEEN :MONTH_STARTDATE AND :MONTH_ENDDATE");
		sql.append(" and P.DRORCR = :DRORCR");
		sql.append(" group by AM.HOSTACCOUNT, RB.BRANCHPROVINCE");

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
		sql.append(" select AM.HOSTACCOUNT ledgerAccount, RB.BRANCHPROVINCE stateCode, sum(postAmount) creditAmount");
		sql.append(" from POSTINGS P");
		sql.append(" INNER JOIN ACCOUNTMAPPING AM ON AM.Account = P.Account");
		sql.append(" INNER JOIN FINANCEMAIN FM ON FM.FINREFERENCE = P.FINREFERENCE");
		sql.append(" INNER JOIN RMTBRANCHES RB ON RB.BRANCHCODE = FM.FINBRANCH");
		sql.append(" where POSTDATE BETWEEN :MONTH_STARTDATE AND :MONTH_ENDDATE");
		sql.append(" and P.DRORCR = :DRORCR");
		sql.append(" group by AM.HOSTACCOUNT, RB.BRANCHPROVINCE");

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

		sql.append("INSERT INTO TRAIL_BALANCE_REPORT VALUES(");
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
		MapSqlParameterSource paramMap;
		StringBuilder sql = new StringBuilder();
		sql.append(" INSERT INTO TRAIL_BALANCE_REPORT_LAST_RUN");
		sql.append(" SELECT * FROM TRAIL_BALANCE_REPORT WHERE HEADERID = :HEADERID");

		paramMap = new MapSqlParameterSource();
		paramMap.addValue("HEADERID", headerId);

		try {
			jdbcTemplate.execute("DELETE FROM TRAIL_BALANCE_REPORT_LAST_RUN");
			parameterJdbcTemplate.update(sql.toString(), paramMap);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw new Exception("Unable to insert current month trail balance.");
		}

		addGroupHeader();
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

	private void addGroupHeader() {
		Map<String, String> states = getStateDescriptions();

		StringBuilder data = new StringBuilder();
		data.append("INSERT INTO TRAIL_BALANCE_REPORT_FILE(ACTYPEGRPID, COUNTRY, PROVINCE, HOSTACCOUNT, DESCRIPTION,");
		data.append(" OPENINGBAL, OPENINGBALTYPE, DEBITAMOUNT, CREDITAMOUNT, CLOSINGBAL, CLOSINGBALTYPE)");
		data.append(" select ACTYPEGRPID, COUNTRY, PROVINCE, HOSTACCOUNT, DESCRIPTION, OPENINGBAL, OPENINGBALTYPE,");
		data.append(" DEBITAMOUNT, CREDITAMOUNT, CLOSINGBAL, CLOSINGBALTYPE");
		data.append(" from TRAIL_BALANCE_REPORT_VIEW where PROVINCE=:PROVINCE");
		MapSqlParameterSource dataMap = new MapSqlParameterSource();

		String emptyLine = "INSERT INTO TRAIL_BALANCE_REPORT_FILE(CLOSINGBALTYPE) VALUES(:CLOSINGBALTYPE)";
		MapSqlParameterSource emptyLineMap = new MapSqlParameterSource();
		emptyLineMap.addValue("CLOSINGBALTYPE", null);

		StringBuilder group = new StringBuilder();
		group.append("INSERT INTO TRAIL_BALANCE_REPORT_FILE(ACTYPEGRPID, COUNTRY, PROVINCE, HOSTACCOUNT, DESCRIPTION,");
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

		String select = "select PROVINCE, count(*) from TRAIL_BALANCE_REPORT_LAST_RUN where HEADERID = ? group by PROVINCE";
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
							.update("INSERT INTO TRAIL_BALANCE_REPORT_FILE(DESCRIPTION) VALUES(:DESCRIPTION)", dataMap);
					parameterJdbcTemplate.update(group.toString(), groupMap);
					parameterJdbcTemplate.update(data.toString(), dataMap);
					groupId++;

				} catch (Exception e) {
					logger.error(Literal.EXCEPTION, e);
				}
			}
		});
	}

}
