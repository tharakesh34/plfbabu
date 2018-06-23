package com.pennanttech.pff.trialbalance;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.model.finance.TrailBalance;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.app.util.DateUtility;
import com.pennanttech.dataengine.DataEngineExport;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.util.DateUtil;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;

public class TrailBalanceEngine extends DataEngineExport {
	public static DataEngineStatus EXTRACT_STATUS = new DataEngineStatus();
	private static final Logger logger = Logger.getLogger(TrailBalanceEngine.class);

	private int batchSize = 1000;
	private Date appDate = null;
	private Date startDate = null;
	private Date endDate = null;
	private long headerId = 0;
	private long seqNo = 0;
	private Dimension dimension = null;
	private String entityCode = null;
	private String entityDescription = null;
	Date financialYearStart = null;
	Date financialYearEnd = null;
	
	private static final String QUERY_CONSOLIDATE = "select distinct HOSTACCOUNT, ACCOUNT, FINTYPE from ACCOUNTMAPPING";
	private static final String QUERY_STATE = "select distinct HOSTACCOUNT, ACCOUNT, FINTYPE, BRANCHCOUNTRY, BRANCHPROVINCE from ACCOUNTMAPPING, RMTBRANCHES";
	
	public enum Dimension{
		CONSOLIDATE, STATE, 
	}

	private Map<String, String> parameters = new HashMap<>();

	public TrailBalanceEngine(DataSource dataSource, long userId, Date valueDate, Date appDate) {
		super(dataSource, userId, App.DATABASE.name(), true, appDate, EXTRACT_STATUS);
		this.appDate = appDate;
	}
	/**
	 *  Validate the Account Mapping and Posting data
	 *  
	 * @throws Exception
	 */
	public void doHealthCheck() throws Exception {
		logger.debug(Literal.ENTERING);
		prepareTrialBalanceDate();
		validateAccountMapping();
		validatePostings();
		logger.debug(Literal.LEAVING);
	}
	/**
	 *  validate Account Mapping
	 * @throws Exception
	 */
	private void validateAccountMapping() throws Exception {
		logger.debug(Literal.ENTERING);
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("START_DATE", startDate);
		paramMap.addValue("END_DATE", endDate);
		paramMap.addValue("ENTITYCODE", entityCode);
		String sql = "Select count (*) from POSTINGS where POSTDATE BETWEEN :START_DATE AND :END_DATE and POSTAMOUNT <>0  AND ENTITYCODE = :ENTITYCODE and account not in(select account from AccountMapping) ";
		logger.trace(Literal.SQL + sql.toString());
		if (parameterJdbcTemplate.queryForObject(sql, paramMap, Integer.class) > 0) {
			EXTRACT_STATUS.setStatus("F");
			EXTRACT_STATUS.setRemarks("Account mapping is not configured, please check the Account Mapping report and configure the missing accounts.");
			throw new AppException(EXTRACT_STATUS.getRemarks());
		}
		logger.debug(Literal.LEAVING);
	}
	/**
	 * validate Postings Data
	 * @throws Exception
	 */
	private void validatePostings() throws Exception {
		logger.debug(Literal.ENTERING);
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("START_DATE", startDate);
		paramMap.addValue("END_DATE", endDate);
		paramMap.addValue("ENTITYCODE", entityCode);

		String sql = "select SUM(CASE WHEN DRORCR = 'D' Then POSTAMOUNT * -1 Else PostAmount END) from POSTINGS where POSTDATE BETWEEN :START_DATE AND :END_DATE AND ENTITYCODE = :ENTITYCODE";
		logger.trace(Literal.SQL + sql.toString());
		BigDecimal amount = parameterJdbcTemplate.queryForObject(sql, paramMap, BigDecimal.class);

		if (amount != null && BigDecimal.ZERO.compareTo(amount) != 0) {
			EXTRACT_STATUS.setStatus("F");
			EXTRACT_STATUS.setRemarks("Credit and Debit amounts not matched for the transactions between "
					.concat(DateUtil.format(startDate, DateFormat.LONG_DATE)).concat(" and ")
					.concat(DateUtil.format(endDate, DateFormat.LONG_DATE)));
			throw new AppException(EXTRACT_STATUS.getRemarks());
		}
		logger.debug(Literal.LEAVING);
	}

	public void extractReport(Dimension dimention, String[] entityDetails) throws Exception {
		this.dimension = dimention;
		this.entityCode = entityDetails[0];
		this.entityDescription = entityDetails[1];
		
		extractReport();
	}
	/**
	 * TrailBalance Data Moved to the data engine 
	 * 
	 * @throws Exception
	 */
	private void extractReport() throws Exception {
		logger.debug(Literal.ENTERING);
		//Validate the Account Mapping And Posting data
		doHealthCheck();
		
		extract();

		Map<String, Object> parameterMap = new HashMap<>();
		parameterMap.put("START_DATE", DateUtil.format(startDate, "ddMMyyyy"));
		parameterMap.put("END_DATE", DateUtil.format(endDate, "ddMMyyyy"));
		parameterMap.put("HEADER_ID", seqNo);
		parameterMap.put("DIMENSION", dimension.name());
		parameterMap.put("COMPANY_NAME", entityDescription);

		if (dimension == Dimension.STATE) {
			parameterMap.put("REPORT_NAME", "State Wise Trial Balance - Ledger A/C wise");
			parameterMap.put("ENTITY_CODE", entityCode + "_TRAIL_BALANCE_");

		} else {
			parameterMap.put("REPORT_NAME", "Consolidated Trial Balance - Ledger A/C wise");
			parameterMap.put("ENTITY_CODE", entityCode + "_TRIAL_BALANCE_CONSOLIDATE_");

		}
		
		Map<String, Object> filterMap = new HashMap<>();
		filterMap.put("HEADER_ID", seqNo);
		filterMap.put("DIMENSION", dimension.name());
		filterMap.put("ID", headerId);

		StringBuilder builder = new StringBuilder();
		builder.append("From ");
		builder.append(DateUtil.format(startDate, "dd-MMM-yy").toUpperCase());
		builder.append(" To ");
		builder.append(DateUtil.format(endDate, "dd-MMM-yy").toUpperCase());

		parameterMap.put("TRANSACTION_DURATION", builder.toString());
		parameterMap.put("CURRENCY", parameters.get("APP_DFT_CURR").concat(" - ".concat(parameters.get("APP_DFT_CURR"))));
		
		setFilterMap(filterMap);

		setParameterMap(parameterMap);
		if (dimension == Dimension.STATE) {
			exportData("TRIAL_BALANCE_EXPORT_STATE");
		} else {
			exportData("TRIAL_BALANCE_EXPORT_CONSOLIDATE");
		}
		logger.debug(Literal.LEAVING);
	}
	/**
	 * Set the Debit,Credit, Opening balalce and Closing bal to TrailBalance
	 * 
	 * @throws Exception
	 */
	private void extract() throws Exception {
		logger.debug(Literal.ENTERING);
		logger.info("Extracting data...");
		//Load system parameters and clear the table data
		initilize();
		getPreviousFinancialYearDates(0);
		//Set  Plfledger Account And Hostaccount to TrialBalance 
		Map<String, TrailBalance> accounts = getLedgerAccounts();
		totalRecords = accounts.size();
		EXTRACT_STATUS.setTotalRecords(totalRecords);
		// Get group code and description.
		Map<String, TrailBalance> groups = getAccountDetails();
		
		String key = null;
		TrailBalance group = null;
		TrailBalance trialBalance = null;

		for (Entry<String, TrailBalance> entry : accounts.entrySet()) {
			
			if (dimension == Dimension.STATE) {
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

			if (openingBal.getOpeningBalance() == null || openingBal.getOpeningBalance().compareTo(BigDecimal.ZERO) == 0) {
				trialBalance.setOpeningBalance(BigDecimal.ZERO);
				trialBalance.setOpeningBalanceType("Cr");
			} else {
				trialBalance.setOpeningBalance(openingBal.getOpeningBalance());
				trialBalance.setOpeningBalanceType(openingBal.getOpeningBalanceType());
			}
			//Post year end activity,income and expense GL's opening balance  will be updated to 0
			if(String.join("|", "INCOME,EXPENSE").contains(trialBalance.getAccountType()) && DateUtility.compare(financialYearStart, startDate) == 0){
				trialBalance.setOpeningBalance(BigDecimal.ZERO);
				trialBalance.setOpeningBalanceType("Cr");
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
		logger.debug(Literal.LEAVING);
	}

	private String getKey(TrailBalance openingBal) {
		if (dimension == Dimension.STATE) {
			return openingBal.getAccount().concat("-").concat(openingBal.getStateCode());
		} else {
			return openingBal.getAccount();
		}
	}
	/**
	 * Generate the HeaderId and SeqNo for TRIAL_BALANCE_HEADER table
	 */
	private void createNextId() {
		logger.debug(Literal.ENTERING);
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("DIMENSION", dimension.name());
		
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT COALESCE(MAX(SEQNO), 0) + 1");
		sql.append(" FROM TRIAL_BALANCE_HEADER WHERE DIMENSION = :DIMENSION");
		logger.trace(Literal.SQL + sql.toString());
		this.seqNo = this.parameterJdbcTemplate.queryForObject(sql.toString(), paramMap, Long.class);
		
		sql = new StringBuilder();
		sql.append(" SELECT COALESCE(MAX(ID), 0) + 1");
		sql.append(" FROM TRIAL_BALANCE_HEADER");
		logger.trace(Literal.SQL + sql.toString());
		this.headerId = this.jdbcTemplate.queryForObject(sql.toString(), Long.class);
	}
	/**
	 * Insert TrailBal Data into  
	 * 				TRIAL_BALANCE_HEADER Table
	 * @return
	 * @throws Exception
	 */
	private long createHeader() throws Exception {
		logger.debug(Literal.ENTERING);
		logger.info("Creating the trail Balance Header..");
		createNextId();

		MapSqlParameterSource paramMap;
		StringBuilder sql = new StringBuilder();
		sql.append(" INSERT INTO TRIAL_BALANCE_HEADER(");
		sql.append(" ID, SEQNO, DIMENSION, COMPANYNAME, REPORTNAME, STARTDATE, ENDDATE, CURRENCY, ENTITYCODE) VALUES (");
		sql.append(" :ID, :SEQNO, :DIMENSION, :COMPANYNAME, :REPORTNAME, :STARTDATE, :ENDDATE, :CURRENCY, :ENTITYCODE)");

		paramMap = new MapSqlParameterSource();
		paramMap.addValue("ID", headerId);
		paramMap.addValue("SEQNO", seqNo);
		paramMap.addValue("DIMENSION", dimension.name());
		paramMap.addValue("COMPANYNAME", parameters.get("TRAIL_BALANCE_COMPANY_NAME"));
		paramMap.addValue("REPORTNAME", "Trial Balance Report");
		paramMap.addValue("STARTDATE", startDate);
		paramMap.addValue("ENDDATE", endDate);
		paramMap.addValue("CURRENCY", parameters.get("APP_DFT_CURR"));
		paramMap.addValue("ENTITYCODE", this.entityCode);

		try {
			logger.trace(Literal.SQL + sql.toString());
			parameterJdbcTemplate.update(sql.toString(), paramMap);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw new Exception("Unable to insert trail balance header.");
		}
		logger.debug(Literal.LEAVING + "SEQNO : " + seqNo);
		return seqNo;
	}
	/**
	 * Accounting Details mapped to the Trailbalalnce
	 * 
	 * @return
	 * @throws Exception
	 */
	private Map<String, TrailBalance> getLedgerAccounts() throws Exception {
		logger.debug(Literal.ENTERING);
		createHeader();

		String query = null;
		if (dimension == Dimension.STATE) {
			query = QUERY_STATE;
		} else {
			query = QUERY_CONSOLIDATE;
		}
		logger.trace(Literal.SQL + query.toString());
		logger.debug(Literal.LEAVING);
		return jdbcTemplate.query(query, new ResultSetExtractor<Map<String, TrailBalance>>() {
			@Override
			public Map<String, TrailBalance> extractData(ResultSet rs) throws SQLException, DataAccessException {
				Map<String, TrailBalance> map = new HashMap<>();

				while (rs.next()) {
					TrailBalance item = new TrailBalance();
					item.setHeaderId(headerId);
					item.setDimention(dimension.name());
					item.setLedgerAccount(rs.getString("HOSTACCOUNT"));
					item.setAccount(rs.getString("ACCOUNT"));
					item.setFinType(rs.getString("FINTYPE"));
					item.setOpeningBalance(BigDecimal.ZERO);
					item.setOpeningBalanceType("Cr");
					item.setDebitAmount(BigDecimal.ZERO);
					item.setCreditAmount(BigDecimal.ZERO);

					if (dimension == Dimension.STATE) {
						item.setCountryCode(rs.getString("BRANCHCOUNTRY"));
						item.setStateCode(rs.getString("BRANCHPROVINCE"));
						map.put(getKey(item), item);
					} else {
						map.put(item.getAccount(), item);
					}
				}
				return map;
			}
		});
	}
	/**
	 * prepare TrialBalance Month Startdata and Enddate
	 * 
	 * @throws Exception
	 */
	private void prepareTrialBalanceDate() throws Exception {
		logger.info("Preparing Trailbalance Date..");
		logger.debug(Literal.ENTERING);
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
		
		startDate = DateUtil.getDatePart(startDate);
		endDate = DateUtil.getDatePart(endDate);


		logger.info("Start Date: " + DateUtil.format(startDate, DateUtil.DateFormat.LONG_DATE));
		logger.info("End Date: " + DateUtil.format(endDate, DateUtil.DateFormat.LONG_DATE));
		logger.debug(Literal.LEAVING);
	}

	private void initilize() throws Exception {
		loadParameters();
		clearTables();
	}
	
	private void clearTables() {
		logger.info("Clearing staging tables..");
		logger.debug(Literal.ENTERING);
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
		paramMap.addValue("DIMENSION", dimension.name());
		paramMap.addValue("ENTITYCODE",this.entityCode);
		
		if (dimension == Dimension.STATE) {
			paramMap.addValue("NAME", "TRIAL_BALANCE_EXPORT_STATE");
		} else {
			paramMap.addValue("NAME", "TRIAL_BALANCE_EXPORT_CONSOLIDATE");
		}
		
		
		// delete the data between start and end date if already available
		StringBuilder sql = new StringBuilder();
		sql.append(" DELETE FROM TRIAL_BALANCE_REPORT WHERE HEADERID = ");
		sql.append(" (SELECT ID FROM TRIAL_BALANCE_HEADER");
		sql.append(" WHERE STARTDATE = :START_DATE AND ENDDATE = :END_DATE AND DIMENSION = :DIMENSION AND ENTITYCODE = :ENTITYCODE)");
		logger.trace(Literal.SQL + sql.toString());
		parameterJdbcTemplate.update(sql.toString(), paramMap);

		sql = new StringBuilder();
		sql.append(" DELETE FROM TRIAL_BALANCE_REPORT_LAST_RUN WHERE HEADERID =");
		sql.append(" (SELECT ID FROM TRIAL_BALANCE_HEADER");
		sql.append(" WHERE STARTDATE = :START_DATE AND ENDDATE = :END_DATE AND DIMENSION = :DIMENSION AND ENTITYCODE = :ENTITYCODE)");
		logger.trace(Literal.SQL + sql.toString());
		parameterJdbcTemplate.update(sql.toString(), paramMap);

		sql = new StringBuilder();
		sql = sql.append("DELETE FROM TRIAL_BALANCE_HEADER WHERE STARTDATE = :START_DATE AND ENDDATE = :END_DATE");
		sql.append(" AND DIMENSION = :DIMENSION AND ENTITYCODE = :ENTITYCODE");
		logger.trace(Literal.SQL + sql.toString());
		parameterJdbcTemplate.update(sql.toString(), paramMap);

		sql = new StringBuilder();
		sql = sql.append("Delete from DATA_ENGINE_LOG where ID IN (");
		sql.append("SELECT ID FROM DATA_ENGINE_STATUS where ValueDate BETWEEN :START_DATE AND :END_DATE AND NAME = :NAME)");
		logger.trace(Literal.SQL + sql.toString());
		parameterJdbcTemplate.update(sql.toString(), paramMap);

		sql = new StringBuilder();
		sql = sql.append("Delete from DATA_ENGINE_STATUS");
		sql.append(" where ValueDate BETWEEN :START_DATE AND :END_DATE AND NAME = :NAME");
		logger.trace(Literal.SQL + sql.toString());
		parameterJdbcTemplate.update(sql.toString(), paramMap);
		logger.debug(Literal.LEAVING);

	}
	/**
	 * Load PreviousFinancial Account Code And Description from SMTParameters
	 * 
	 */
	private void loadParameters() {
		logger.info("Loading parameters..");
		logger.debug(Literal.ENTERING);
		MapSqlParameterSource paramMap;

		StringBuilder sql = new StringBuilder();
		paramMap = new MapSqlParameterSource();

		sql.append("SELECT SYSPARMCODE, SYSPARMVALUE, SYSPARMDESCRIPTION FROM SMTPARAMETERS where SYSPARMCODE");
		sql.append(" IN (:TRAIL_BALANCE_COMPANY_NAME, :APP_DFT_CURR, :PRFT_LOSS_GLCODE)");

		paramMap.addValue("TRAIL_BALANCE_COMPANY_NAME", "TRAIL_BALANCE_COMPANY_NAME");
		paramMap.addValue("APP_DFT_CURR", "APP_DFT_CURR");
		paramMap.addValue("PRFT_LOSS_GLCODE", "PRFT_LOSS_GLCODE");
		logger.trace(Literal.SQL + sql.toString());
		parameterJdbcTemplate.query(sql.toString(), paramMap, new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				if(StringUtils.equals(rs.getString("SYSPARMCODE"),"PRFT_LOSS_GLCODE")){
					parameters.put(rs.getString("SYSPARMCODE"), rs.getString("SYSPARMVALUE"));
					parameters.put(rs.getString("SYSPARMVALUE"), rs.getString("SYSPARMDESCRIPTION"));
				}else{
					parameters.put(rs.getString("SYSPARMCODE"), rs.getString("SYSPARMVALUE"));
				}
			}
		});
		logger.debug(Literal.LEAVING);
	}

	private Map<String, TrailBalance> getAccountDetails() {
		logger.debug(Literal.ENTERING);
		StringBuilder sql = new StringBuilder();
		sql.append(" select AM.HOSTACCOUNT, AM.ACCOUNT, ATG.GROUPCODE, AT.ACTYPEDESC");
		sql.append(" from ACCOUNTMAPPING AM");
		sql.append(" INNER JOIN RMTACCOUNTTYPES AT ON AT.ACTYPE = AM.ACCOUNTTYPE");
		sql.append(" INNER JOIN ACCOUNTTYPEGROUP ATG  ON ATG.GROUPID = AT.ACTYPEGRPID");

		try {
			logger.trace(Literal.SQL + sql.toString());
			return extractAccountDetails(sql);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
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
							trailBalance.setAccount(rs.getString("ACCOUNT"));
							trailBalance.setAccountType(rs.getString("GROUPCODE"));
							trailBalance.setAccountTypeDes(rs.getString("ACTYPEDESC"));

							map.put(trailBalance.getAccount(), trailBalance);
						}
						return map;
					}
				});
	}
	/**
	 * get Opening balance from Previous Month Closing balance
	 *  And closing balance is the sum of credits and debits
	 * 
	 * @return
	 * 		List<TrailBalance>
	 */
	private List<TrailBalance> getOpeningBalance() {
		logger.debug(Literal.ENTERING);
		MapSqlParameterSource paramMap =  new MapSqlParameterSource();;
		
		Date previousMonth = DateUtil.addMonths(startDate, -1);
		
		StringBuilder sql = new StringBuilder();
		sql.append(" select AM.HOSTACCOUNT ledgerAccount, AM.ACCOUNT Account, LR.PROVINCE stateCode,");
		sql.append(" LR.CLOSINGBAL openingBalance, LR.CLOSINGBALTYPE openingBalanceType");
		sql.append(" from ACCOUNTMAPPING AM");
		sql.append(" INNER JOIN TRIAL_BALANCE_REPORT_LAST_RUN LR ON LR.HOSTACCOUNT = AM.HOSTACCOUNT");
		sql.append(" INNER JOIN TRIAL_BALANCE_HEADER TH ON TH.ID = LR.HEADERID");
		sql.append(" WHERE TH.DIMENSION = :DIMENSION");
		sql.append(" AND TH.STARTDATE =  :STARTDATE_MINUS_1M and TH.ENDDATE = :ENDDATE_MINUS_1M");
		
		paramMap.addValue("DIMENSION", dimension.name());
		paramMap.addValue("STARTDATE_MINUS_1M", DateUtil.getMonthStart(previousMonth));
		paramMap.addValue("ENDDATE_MINUS_1M", DateUtil.getMonthEnd(previousMonth));
		RowMapper<TrailBalance> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(TrailBalance.class);

		try {
			logger.trace(Literal.SQL + sql.toString());
			return parameterJdbcTemplate.query(sql.toString(), paramMap, typeRowMapper);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return new ArrayList<TrailBalance>();
	}
	
	private List<TrailBalance> getDebitAmount() {
		logger.debug(Literal.ENTERING);
		MapSqlParameterSource paramMap = null;

		StringBuilder sql = new StringBuilder();

		if (dimension == Dimension.STATE) {
			sql.append("select AM.HOSTACCOUNT ledgerAccount, AM.ACCOUNT Account, RB.BRANCHPROVINCE stateCode, sum(postAmount) debitAmount");
			sql.append(" from POSTINGS P");
			sql.append(" INNER JOIN ACCOUNTMAPPING AM ON AM.Account = P.Account");
			sql.append(" INNER JOIN RMTBRANCHES RB ON RB.BRANCHCODE = P.POSTBRANCH");
			sql.append(" where POSTDATE BETWEEN :MONTH_STARTDATE AND :MONTH_ENDDATE AND ENTITYCODE = :ENTITYCODE");
			sql.append(" and P.DRORCR = :DRORCR");
			sql.append(" group by AM.HOSTACCOUNT, AM.ACCOUNT, RB.BRANCHPROVINCE");
		} else {
			sql.append(" select AM.HOSTACCOUNT ledgerAccount, AM.ACCOUNT Account, sum(postAmount) debitAmount");
			sql.append(" from POSTINGS P");
			sql.append(" INNER JOIN ACCOUNTMAPPING AM ON AM.Account = P.Account");
			sql.append(" where POSTDATE BETWEEN :MONTH_STARTDATE AND :MONTH_ENDDATE AND ENTITYCODE = :ENTITYCODE");
			sql.append(" and P.DRORCR = :DRORCR");
			sql.append(" group by AM.HOSTACCOUNT, AM.ACCOUNT");
		}

		paramMap = new MapSqlParameterSource();
		paramMap.addValue("MONTH_STARTDATE", startDate);
		paramMap.addValue("MONTH_ENDDATE", endDate);
		paramMap.addValue("DRORCR", "D");
		paramMap.addValue("ENTITYCODE", entityCode);

		RowMapper<TrailBalance> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(TrailBalance.class);

		try {
			logger.trace(Literal.SQL + sql.toString());
			return parameterJdbcTemplate.query(sql.toString(), paramMap, typeRowMapper);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return null;
	}

	private List<TrailBalance> getCreditAmount() {
		logger.debug(Literal.ENTERING);
		MapSqlParameterSource paramMap = null;

		StringBuilder sql = new StringBuilder();
		
		if (dimension == Dimension.STATE) {
			sql.append("select AM.HOSTACCOUNT ledgerAccount, AM.ACCOUNT Account, RB.BRANCHPROVINCE stateCode, sum(postAmount) creditAmount");
			sql.append(" from POSTINGS P");
			sql.append(" INNER JOIN ACCOUNTMAPPING AM ON AM.Account = P.Account");
			sql.append(" INNER JOIN RMTBRANCHES RB ON RB.BRANCHCODE = P.POSTBRANCH");
			sql.append(" where POSTDATE BETWEEN :MONTH_STARTDATE AND :MONTH_ENDDATE AND ENTITYCODE = :ENTITYCODE");
			sql.append(" and P.DRORCR = :DRORCR");
			sql.append(" group by AM.HOSTACCOUNT, AM.ACCOUNT, RB.BRANCHPROVINCE");
		} else {
			sql.append("select AM.HOSTACCOUNT ledgerAccount,  AM.ACCOUNT Account, sum(postAmount) creditAmount");
			sql.append(" from POSTINGS P");
			sql.append(" INNER JOIN ACCOUNTMAPPING AM ON AM.Account = P.Account");
			sql.append(" where POSTDATE BETWEEN :MONTH_STARTDATE AND :MONTH_ENDDATE AND ENTITYCODE = :ENTITYCODE");
			sql.append(" and P.DRORCR = :DRORCR");
			sql.append(" group by AM.HOSTACCOUNT, AM.ACCOUNT");
		}

		paramMap = new MapSqlParameterSource();
		paramMap.addValue("MONTH_STARTDATE", startDate);
		paramMap.addValue("MONTH_ENDDATE", endDate);
		paramMap.addValue("DRORCR", "C");
		paramMap.addValue("ENTITYCODE", entityCode);

		RowMapper<TrailBalance> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(TrailBalance.class);

		try {
			logger.trace(Literal.SQL + sql.toString());
			return parameterJdbcTemplate.query(sql.toString(), paramMap, typeRowMapper);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return null;
	}

	private void save(Map<String, TrailBalance> accounts) throws Exception {
		logger.debug(Literal.ENTERING);
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
		logger.debug(Literal.LEAVING);
	}
	/**
	 * save the TrailBalance month end data to the TRIAL_BALANCE_REPORT
	 * @param list
	 * @throws SQLException
	 */
	private void save(List<TrailBalance> list) throws SQLException {
		logger.debug(Literal.ENTERING);
		StringBuilder sql = new StringBuilder();
		
		List<TrailBalance> tempList = new ArrayList<TrailBalance>();

		for (TrailBalance tb : list) {
			if ((BigDecimal.ZERO.compareTo(tb.getOpeningBalance()) == 0)
					&& (BigDecimal.ZERO.compareTo(tb.getClosingBalance()) == 0)
					&& (BigDecimal.ZERO.compareTo(tb.getCreditAmount()) == 0)
					&& (BigDecimal.ZERO.compareTo(tb.getDebitAmount()) == 0)) {

			} else if ((BigDecimal.ZERO.compareTo(tb.getOpeningBalance()) != 0
					&& BigDecimal.ZERO.compareTo(tb.getOpeningBalance()) != 0
					&& BigDecimal.ZERO.compareTo(tb.getCreditAmount()) != 0
					&& BigDecimal.ZERO.compareTo(tb.getDebitAmount()) != 0)) {
				tempList.add(tb);
			} else if ((BigDecimal.ZERO.compareTo(tb.getOpeningBalance()) != 0)
					|| (BigDecimal.ZERO.compareTo(tb.getOpeningBalance()) != 0)
					|| (BigDecimal.ZERO.compareTo(tb.getCreditAmount()) != 0)
					|| (BigDecimal.ZERO.compareTo(tb.getDebitAmount()) != 0)) {
				tempList.add(tb);
			}
		}
		
		sql.append("INSERT INTO TRIAL_BALANCE_REPORT VALUES(");
		sql.append(" :HeaderId,");
		sql.append(" :AccountType,");
		sql.append(" :CountryCode,");
		sql.append(" :StateCode,");
		sql.append(" :FinType,");
		sql.append(" :Account,");
		sql.append(" :LedgerAccount,");
		sql.append(" :AccountTypeDes,");
		sql.append(" :OpeningBalance,");
		sql.append(" :OpeningBalanceType,");
		sql.append(" :DebitAmount,");
		sql.append(" :CreditAmount,");
		sql.append(" :ClosingBalance,");
		sql.append(" :ClosingBalanceType)");
		
		parameterJdbcTemplate.batchUpdate(sql.toString(), SqlParameterSourceUtils.createBatch(tempList.toArray()));
		logger.debug(Literal.LEAVING);
	}

	private void logTrialBalace() throws Exception {	
		logger.debug(Literal.ENTERING);
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("HEADERID", headerId);
		paramMap.addValue("DIMENSION", dimension.name());
		paramMap.addValue("STARTDATE_MINUS_12M", DateUtil.addMonths(startDate, -11));

		try {
			StringBuilder sql = new StringBuilder();
			sql.append(" DELETE FROM TRIAL_BALANCE_REPORT_LAST_RUN WHERE HEADERID IN(");
			sql.append(" (SELECT ID FROM TRIAL_BALANCE_HEADER WHERE DIMENSION = :DIMENSION AND STARTDATE < :STARTDATE_MINUS_12M))");
			parameterJdbcTemplate.update(sql.toString(), paramMap);
			
			sql = new StringBuilder();
			sql.append(" INSERT INTO TRIAL_BALANCE_REPORT_LAST_RUN");
			sql.append(" SELECT * FROM TRIAL_BALANCE_REPORT WHERE HEADERID = :HEADERID");
			logger.trace(Literal.SQL + sql.toString());
			parameterJdbcTemplate.update(sql.toString(), paramMap);
			
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw new Exception("Unable to insert current month trail balance.");
		}
		
		if (dimension == Dimension.STATE) {
			loginfileTableForState();
		} else {
			logInFileTable();
			addFinancialSummaryForConsolidate();
			addSummaryRecord();
		}

		logger.debug(Literal.LEAVING);
	}
	/**
	 * Second last row shall be the sum of balances of income (Account type group code) 
	 * 				minus 
	 * sum of balances of expense (Account type group code) ledgers for the previous financial year 
	 */
	private void addFinancialSummaryForConsolidate() {
		logger.debug(Literal.ENTERING);

		String closingBaltype = "";
		StringBuilder sql = new StringBuilder();;
		MapSqlParameterSource  parmsource = new MapSqlParameterSource();
		BigDecimal closingBal = getPreviousFinancialYearBalances();
		closingBal = closingBal.divide(new BigDecimal(Math.pow(10, PennantConstants.defaultCCYDecPos)));
		if (BigDecimal.ZERO.compareTo(closingBal) <= 0) {
			closingBaltype = "Cr";
		} else {
			closingBaltype = "Dr";
			 closingBal = closingBal.negate();
		}
		sql = new StringBuilder();
		String accountCode = parameters.get("PRFT_LOSS_GLCODE");
		sql.append("INSERT INTO TRIAL_BALANCE_REPORT_FILE (ACCOUNT, DESCRIPTION, OPENINGBAL, CLOSINGBAL, CLOSINGBALTYPE, OPENINGBALTYPE)");
		sql.append("VALUES (:ACCOUNT, :DESCRIPTION, :CLOSINGBAL, :CLOSINGBAL, :CLOSINGBALTYPE, :CLOSINGBALTYPE)");
		
		parmsource.addValue("ACCOUNT", accountCode);
		parmsource.addValue("DESCRIPTION", parameters.get(accountCode));
		parmsource.addValue("CLOSINGBAL", closingBal.toString());
		parmsource.addValue("CLOSINGBALTYPE", closingBaltype);
		logger.trace(Literal.SQL + sql.toString());
		parameterJdbcTemplate.update(sql.toString(), parmsource);
		logger.debug(Literal.LEAVING);
		
	}

	private void getPreviousFinancialYearDates(int previous) {
		Date tempDate = startDate;
		int month = DateUtil.getMonth(tempDate);
		int year = DateUtil.getYear(tempDate)-previous;
		int date = DateUtil.getDate(tempDate);

		if (month < 4) {
			tempDate = DateUtil.addMonths(tempDate, -12);
			year = DateUtil.getYear(tempDate)-previous;
			date = DateUtil.getDate(tempDate);

			financialYearStart = DateUtil.getDate(year, Calendar.APRIL, date);
			tempDate = DateUtil.addMonths(tempDate, 12);
			year = DateUtil.getYear(tempDate)-previous;
			financialYearEnd = DateUtil.getDate(year, Calendar.MARCH, 31);

		} else {
			financialYearStart = DateUtil.getDate(year, Calendar.APRIL, date);
			tempDate = DateUtil.addMonths(tempDate, 12);
			year = DateUtil.getYear(tempDate)-previous;
			financialYearEnd = DateUtil.getDate(year, Calendar.MARCH, 31);
			
		}
	}
	
	/**
	 * Sum of Debits And Credits for present Month
	 */
	private void addSummaryRecord() {
		logger.debug(Literal.ENTERING);
		StringBuilder sql = new StringBuilder();
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("HEADERID", headerId);
		
		sql.append(" INSERT INTO TRIAL_BALANCE_REPORT_FILE(DEBITAMOUNT, CREDITAMOUNT) values");
		sql.append(" ((select sum(debitamount) from TRIAL_BALANCE_REPORT WHERE HEADERID = :HEADERID) ,(select sum(CREDITAMOUNT) from  TRIAL_BALANCE_REPORT WHERE HEADERID = :HEADERID))");
		logger.trace(Literal.SQL + sql.toString());
		parameterJdbcTemplate.update(sql.toString(), paramMap);
		logger.debug(Literal.LEAVING);
	}
	
	private void logInFileTable() {
		logger.debug(Literal.ENTERING);
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("HEADERID", headerId);
		
		StringBuilder data = new StringBuilder();
		data.append("INSERT INTO TRIAL_BALANCE_REPORT_FILE(ACTYPEGRPID, COUNTRY, PROVINCE, HOSTACCOUNT, DESCRIPTION,");
		data.append(" OPENINGBAL, OPENINGBALTYPE, DEBITAMOUNT, CREDITAMOUNT, CLOSINGBAL, CLOSINGBALTYPE, ACCOUNT, FINTYPE)");
		data.append(" select ACTYPEGRPID, COUNTRY, PROVINCE, HOSTACCOUNT, DESCRIPTION, OPENINGBAL, OPENINGBALTYPE,");
		data.append(" DEBITAMOUNT, CREDITAMOUNT, CLOSINGBAL, CLOSINGBALTYPE, ACCOUNT, FINTYPE");
		data.append(" from TRIAL_BALANCE_REPORT_VIEW WHERE HEADERID = :HEADERID");
		data.append(" AND(OPENINGBAL>0 OR CLOSINGBAL>0 OR DEBITAMOUNT>0 OR CREDITAMOUNT>0) ORDER BY PROVINCE");
		logger.trace(Literal.SQL + data.toString());
		parameterJdbcTemplate.update(data.toString(), paramMap);
		logger.debug(Literal.LEAVING);
	}

	private void loginfileTableForState() {
		logger.debug(Literal.ENTERING);
		StringBuilder data = new StringBuilder();
		data.append("INSERT INTO TRIAL_BALANCE_REPORT_FILE(ACTYPEGRPID, COUNTRY, PROVINCE, HOSTACCOUNT, DESCRIPTION,");
		data.append(" OPENINGBAL, OPENINGBALTYPE, DEBITAMOUNT, CREDITAMOUNT, CLOSINGBAL, CLOSINGBALTYPE, ACCOUNT, FINTYPE)");
		data.append(" select ACTYPEGRPID, COUNTRY, PROVINCE, HOSTACCOUNT, DESCRIPTION, OPENINGBAL, OPENINGBALTYPE,");
		data.append(" DEBITAMOUNT, CREDITAMOUNT, CLOSINGBAL, CLOSINGBALTYPE, ACCOUNT, FINTYPE");
		data.append(" from TRIAL_BALANCE_REPORT_VIEW WHERE HEADERID = :HEADERID AND PROVINCE=:PROVINCE");
		MapSqlParameterSource dataMap = new MapSqlParameterSource();

		String emptyLine = "INSERT INTO TRIAL_BALANCE_REPORT_FILE(CLOSINGBALTYPE) VALUES(:CLOSINGBALTYPE)";
		MapSqlParameterSource emptyLineMap = new MapSqlParameterSource();
		emptyLineMap.addValue("CLOSINGBALTYPE", null);
		
		getPreviousFinancialYearDates(1);
		logger.trace(Literal.SQL + data.toString());
		String select = "select PROVINCE, count(*) from TRIAL_BALANCE_REPORT_LAST_RUN where HEADERID = ? group by PROVINCE";
		jdbcTemplate.query(select, new Object[] { headerId }, new RowCallbackHandler() {
			int groupId = 0;
		
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				try {
					StringBuilder sql = new StringBuilder();
					MapSqlParameterSource datesMap = new MapSqlParameterSource();
					String closingBaltype = "";
					BigDecimal closingBal = BigDecimal.ZERO;
					sql.append(" Select Coalesce(sum(case when CLOSINGBALTYPE = 'Dr' then CLOSINGBAL * -1 else CLOSINGBAL end ),0) ClosingBal");
					sql.append(" From TRIAL_BALANCE_REPORT Where HeaderId in (select id from TRIAL_BALANCE_HEADER where startdate Between ");
					sql.append(" :STARTDATE AND :ENDDATE) And ACTYPEGRPID IN('EXPENSE','INCOME')And PROVINCE = :PROVINCE And PROVINCE IS Not Null ");
					Date startDate = DateUtil.getMonthStart(financialYearEnd);
					datesMap.addValue("STARTDATE", startDate);
					datesMap.addValue("ENDDATE", financialYearEnd);
					datesMap.addValue("PROVINCE", rs.getString("PROVINCE"));
						
					logger.trace(Literal.SQL + sql.toString());
					closingBal = parameterJdbcTemplate.queryForObject(sql.toString(),datesMap, BigDecimal.class);
					closingBal = closingBal.divide(new BigDecimal(Math.pow(10, PennantConstants.defaultCCYDecPos)));
					
					if (BigDecimal.ZERO.compareTo(closingBal) <= 0) {
						closingBaltype = "Cr";
					} else {
						 closingBal = closingBal.negate();
						closingBaltype = "Dr";
					}
					
					String accountCode = parameters.get("PRFT_LOSS_GLCODE");
					dataMap.addValue("ACCOUNT", parameters.get("PRFT_LOSS_GLCODE"));
					dataMap.addValue("DESCRIPTION", parameters.get(accountCode));
					dataMap.addValue("CLOSINGBAL", closingBal.toString());
					dataMap.addValue("CLOSINGBALTYPE", closingBaltype);
					dataMap.addValue("HEADERID", headerId);
					dataMap.addValue("PROVINCE", rs.getString("PROVINCE"));
					if (groupId > 0) {
						logger.trace(Literal.SQL + emptyLine.toString());
						parameterJdbcTemplate.update(emptyLine, emptyLineMap);
					}
					logger.trace(Literal.SQL + data.toString());
					parameterJdbcTemplate.update(data.toString(), dataMap);
					
					sql = new StringBuilder();
					
					sql.append(" INSERT INTO TRIAL_BALANCE_REPORT_FILE");
					sql.append(" (ACCOUNT, DESCRIPTION, OPENINGBAL, CLOSINGBAL, CLOSINGBALTYPE, OPENINGBALTYPE)");
					sql.append(" VALUES(:ACCOUNT, :DESCRIPTION, :CLOSINGBAL, :CLOSINGBAL, :CLOSINGBALTYPE, :CLOSINGBALTYPE)");
					
					logger.trace(Literal.SQL + sql.toString());
					parameterJdbcTemplate.update(sql.toString(), dataMap);
					
					datesMap = new MapSqlParameterSource();
					sql = new StringBuilder();
					
					datesMap.addValue("PROVINCE", rs.getString("PROVINCE"));
					
					sql.append(" INSERT INTO TRIAL_BALANCE_REPORT_FILE(DEBITAMOUNT, CREDITAMOUNT) values");
					sql.append(" ((select sum(debitamount) from TRIAL_BALANCE_REPORT_FILE  where hostaccount is not null and PROVINCE=:PROVINCE),");
					sql.append(" (select sum(CREDITAMOUNT) from TRIAL_BALANCE_REPORT_FILE  where hostaccount is not null and PROVINCE=:PROVINCE))");
					
					logger.trace(Literal.SQL + sql.toString());
					parameterJdbcTemplate.update(sql.toString(), datesMap);
					
					groupId++;

				} catch (Exception e) {
					logger.error(Literal.EXCEPTION, e);
				}
			}
		});
		logger.debug(Literal.LEAVING);
	}
	
	public BigDecimal getPreviousFinancialYearBalances(){
		logger.debug("Entering");
		getPreviousFinancialYearDates(1);
		StringBuilder sql = new StringBuilder();
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		Date startDate = DateUtil.getMonthStart(financialYearEnd);
		paramMap.addValue("STARTDATE", startDate);
		paramMap.addValue("ENDDATE", financialYearEnd);
		
		sql.append(" Select Coalesce(sum(case when CLOSINGBALTYPE = 'Dr' then CLOSINGBAL * -1 else CLOSINGBAL end ),0) ClosingBal ");
		sql.append(" From TRIAL_BALANCE_REPORT where headerid in (Select id from TRIAL_BALANCE_HEADER where startdate between ");
		sql.append(" :STARTDATE AND :ENDDATE) And ACTYPEGRPID IN('EXPENSE','INCOME') And PROVINCE IS NULL ");
		logger.trace(Literal.SQL + sql.toString());
		logger.debug("Leaving");
		return parameterJdbcTemplate.queryForObject(sql.toString(),paramMap, BigDecimal.class);
		
	}
	
	public boolean isBatchExists(String entityCode, String dimention) throws Exception {
		logger.debug(Literal.ENTERING);
		prepareTrialBalanceDate();
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("STARTDATE", startDate);
		paramMap.addValue("ENDDATE", endDate);
		paramMap.addValue("DIMENSION", dimention);
		paramMap.addValue("ENTITYCODE", entityCode);
		
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT count(*) from ");
		sql.append(" TRIAL_BALANCE_HEADER WHERE DIMENSION = :DIMENSION AND STARTDATE = :STARTDATE AND ENDDATE = :ENDDATE AND ENTITYCODE = :ENTITYCODE");

		int count = 0;
		try {
			count = this.parameterJdbcTemplate.queryForObject(sql.toString(), paramMap, Integer.class);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		if (count > 0) {
			return true;
		} else {
			return false;
		}
	}

}