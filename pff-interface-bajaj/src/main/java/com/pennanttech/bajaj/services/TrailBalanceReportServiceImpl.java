package com.pennanttech.bajaj.services;

import com.pennant.backend.model.finance.TrailBalance;
import com.pennanttech.dataengine.DataEngineExport;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.baja.BajajInterfaceConstants;
import com.pennanttech.pff.core.App;
import com.pennanttech.pff.core.services.TrailBalanceReportService;
import com.pennanttech.pff.core.util.DateUtil;
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
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.transaction.TransactionStatus;

public class TrailBalanceReportServiceImpl extends BajajService implements TrailBalanceReportService {
	private final Logger logger = Logger.getLogger(getClass());

	private long userId;
	private Date valueDate = null;
	private Date glDate = null;
	private Date monthStartDate = null;
	private Date monthEndDate = null;
	private long headerId = 0;
	private String currency = null;
	private String companyName = null;
	private String reportName = null;
	private String fileName = null;
	private int batchSize = 1000;
	private static final String LEDGER_QUERY = "select distinct HOSTACCOUNT, BRANCHCOUNTRY, BRANCHPROVINCE from ACCOUNTMAPPING, RMTBRANCHES";
	private Map<String, TrailBalance> dataMap = null;

	@Override
	public void generateReport(Object... params) throws Exception {
		this.userId = (long) params[0];
		try {
			prepareGLDates();

			clearTables();
			createHeader();
			prepareLedgerAccounts();
			prepareTrailBalace();
			prepareTransactionDetails();
			prepareTransactionSummary();

			TransactionStatus txnStatus = transManager.getTransaction(transDef);
			try {
				clearPreviousMonthTrailBalace();
				saveCurrentMonthTrailBalace();
				updateGLDates();
				transManager.commit(txnStatus);
			} catch (Exception e) {
				transManager.rollback(txnStatus);
				logger.error(Literal.EXCEPTION, e);
				throw e;
			} finally {
				txnStatus.flush();
				txnStatus = null;
			}

			generateGLReport();

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw e;
		}

	}

	private void prepareTransactionSummary() throws Exception {
		MapSqlParameterSource paramMap;

		StringBuilder sql = new StringBuilder();
		sql.append(" INSERT INTO TRANSACTION_SUMMARY_REPORT SELECT");
		sql.append("  DISTINCT LINK,");
		sql.append(" :BLDAT,");
		sql.append(" :BLART,");
		sql.append(" :BUKRS,");
		sql.append(" :BUDAT,");
		sql.append(" :MONAT,");
		sql.append(" :APP_DFT_CURR,");
		sql.append(" :XBLNR,");
		sql.append(" :BKTXT");
		sql.append(" FROM TRANSACTION_DETAIL_REPORT");

		paramMap = new MapSqlParameterSource();
		paramMap.addValue("BLDAT", monthEndDate);
		paramMap.addValue("BLART", getSMTParameter("BLART", String.class));
		paramMap.addValue("BUKRS", getSMTParameter("BUKRS", String.class));
		paramMap.addValue("BUDAT", monthEndDate);
		paramMap.addValue("MONAT", getFinancialMonth());
		paramMap.addValue("APP_DFT_CURR", getSMTParameter("APP_DFT_CURR", String.class));
		paramMap.addValue("XBLNR", StringUtils.upperCase("CF - " + DateUtil.format(glDate, "MMM YY") + " - PLF"));
		paramMap.addValue("BKTXT", StringUtils.upperCase("CF - " + DateUtil.format(glDate, "MMM YY") + " - PLF"));

		try {
			namedJdbcTemplate.update(sql.toString(), paramMap);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw new Exception("Unable to prpare the transaction summary report.");
		}
	}

	private Map<String, TrailBalance> prepareLedgerAccounts() throws Exception {
		String umskz = (String) getSMTParameter("UMSKZ", String.class);
		String businessArea = (String) getSMTParameter("GSBER", String.class);
		String businessUnit = (String) getSMTParameter("BUPLA", String.class);
		String narration1 = StringUtils.upperCase("CF - " + DateUtil.format(glDate, "MMM YY") + " - PLF");
		String narration2 = StringUtils.upperCase("CF - " + DateUtil.format(glDate, "MMM YY") + " - PLF");

		return namedJdbcTemplate.query(LEDGER_QUERY, new MapSqlParameterSource(),
				new ResultSetExtractor<Map<String, TrailBalance>>() {

					@Override
					public Map<String, TrailBalance> extractData(ResultSet rs)
							throws SQLException, DataAccessException {
						dataMap = new HashMap<>();

						while (rs.next()) {
							TrailBalance trailBalance = new TrailBalance();
							trailBalance.setHeaderId(headerId);
							trailBalance.setLedgerAccount(rs.getString("HOSTACCOUNT"));
							trailBalance.setCountryCode(rs.getString("BRANCHCOUNTRY"));
							trailBalance.setStateCode(rs.getString("BRANCHPROVINCE"));
							trailBalance.setOpeningBalance(BigDecimal.ZERO);
							trailBalance.setOpeningBalanceType("Cr");
							trailBalance.setDebitAmount(BigDecimal.ZERO);
							trailBalance.setCreditAmount(BigDecimal.ZERO);

							trailBalance.setBusinessArea(businessArea);
							trailBalance.setBusinessUnit(businessUnit);
							trailBalance.setUmskz(umskz);
							trailBalance.setNarration1(narration1);
							trailBalance.setNarration2(narration2);

							dataMap.put(trailBalance.getLedgerAccount().concat("-").concat(trailBalance.getStateCode()),
									trailBalance);

						}
						return dataMap;
					}
				});
	}

	private void prepareTrailBalace() throws Exception {

		// Get group code and description.
		Map<String, TrailBalance> groups = getAccountDetails();
		String key = null;

		for (Entry<String, TrailBalance> entry : dataMap.entrySet()) {
			key = entry.getKey().split("-")[0];
			TrailBalance item = groups.get(key);

			if (item == null || item.getAccountType() == null) {
				entry.getValue().setAccountType("NA");
				entry.getValue().setAccountTypeDes("NA");
			} else {
				entry.getValue().setAccountType(item.getAccountType());
				entry.getValue().setAccountTypeDes(item.getAccountTypeDes());
			}
		}
		groups = null;

		// Get opening balance
		List<TrailBalance> openingBals = getOpeningBalance();
		key = null;

		for (TrailBalance trailBalance : openingBals) {
			key = trailBalance.getLedgerAccount().concat("-").concat(trailBalance.getStateCode());
			TrailBalance item = dataMap.get(key);

			if (trailBalance.getOpeningBalance() == null
					|| trailBalance.getOpeningBalance().compareTo(BigDecimal.ZERO) == 0) {
				item.setOpeningBalance(BigDecimal.ZERO);
				item.setOpeningBalanceType("Cr");
			} else {
				item.setOpeningBalance(trailBalance.getOpeningBalance());
				item.setOpeningBalanceType(trailBalance.getOpeningBalanceType());
			}

			if (StringUtils.equals(trailBalance.getClosingBalanceType(), "Dr")) {
				item.setClosingBalance(BigDecimal.ZERO.subtract(trailBalance.getClosingBalance()));
			}
		}
		openingBals = null;

		// Get debit amount
		List<TrailBalance> debitAmounts = getDebitAmount();
		key = null;

		for (TrailBalance trailBalance : debitAmounts) {
			key = trailBalance.getLedgerAccount().concat("-").concat(trailBalance.getStateCode());
			TrailBalance item = dataMap.get(key);

			// FIXME: Fix the data.
			if (item == null) {
				continue;
			}

			if (trailBalance.getDebitAmount() == null) {
				item.setDebitAmount(BigDecimal.ZERO);
			} else {
				item.setDebitAmount(trailBalance.getDebitAmount());
			}
		}
		debitAmounts = null;

		// Get credit amount
		List<TrailBalance> creditAmounts = getCreditAmount();
		key = null;

		for (TrailBalance trailBalance : creditAmounts) {
			key = trailBalance.getLedgerAccount().concat("-").concat(trailBalance.getStateCode());
			TrailBalance item = dataMap.get(key);

			// FIXME: Fix the data.
			if (item == null) {
				continue;
			}

			if (trailBalance.getCreditAmount() == null) {
				item.setCreditAmount(BigDecimal.ZERO);
			} else {
				item.setCreditAmount(trailBalance.getCreditAmount());
			}
		}
		creditAmounts = null;

		// Calculate closing balance.
		for (Entry<String, TrailBalance> entry : dataMap.entrySet()) {
			entry.getValue().setClosingBalance(entry.getValue().getClosingBalance()
					.subtract(entry.getValue().getDebitAmount()).add(entry.getValue().getCreditAmount()));

			if (entry.getValue().getClosingBalance().compareTo(BigDecimal.ZERO) < 0) {
				entry.getValue().setClosingBalanceType("Dr");
				entry.getValue().setClosingBalance(BigDecimal.ZERO.subtract(entry.getValue().getClosingBalance()));
			} else {
				entry.getValue().setClosingBalanceType("Cr");
			}
		}

		// Save to database
		List<TrailBalance> list = new ArrayList<>();

		for (Entry<String, TrailBalance> entry : dataMap.entrySet()) {
			list.add(entry.getValue());

			if (list.size() >= batchSize) {
				saveTrailBalance(list);
				list.clear();
			}
		}

		if (!list.isEmpty()) {
			saveTrailBalance(list);
		}
		
		list = null;
	}

	private Map<String, TrailBalance> getAccountDetails() {
		StringBuilder sql = new StringBuilder();
		sql.append(" select AM.HOSTACCOUNT, ATG.GROUPCODE, AT.ACTYPEDESC");
		sql.append(" from ACCOUNTMAPPING AM");
		sql.append(" INNER JOIN RMTACCOUNTTYPES AT ON AT.ACTYPE = AM.ACCOUNTTYPE");
		sql.append(" INNER JOIN ACCOUNTTYPEGROUP ATG  ON ATG.GROUPID = AT.ACTYPEGRPID");

		try {
			return namedJdbcTemplate.query(sql.toString(), new MapSqlParameterSource(),
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
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		return null;
	}
	
	private Map<String, TrailBalance> getLedgerDetails() {
		StringBuilder sql = new StringBuilder();
		sql.append(" select HOSTACCOUNT, PROFITCENTERCODE, COSTCENTERCODE");
		sql.append(" from ACCOUNTMAPPING AM");
		sql.append(" LEFT JOIN PROFITCENTERS PC ON PC.PROFITCENTERID = AM.PROFITCENTERID");
		sql.append(" LEFT JOIN COSTCENTERS CC ON CC.COSTCENTERID = AM.COSTCENTERID");

		try {
			return namedJdbcTemplate.query(sql.toString(), new MapSqlParameterSource(),
					new ResultSetExtractor<Map<String, TrailBalance>>() {
						@Override
						public Map<String, TrailBalance> extractData(ResultSet rs)
								throws SQLException, DataAccessException {
							Map<String, TrailBalance> map = new HashMap<>();

							while (rs.next()) {
								TrailBalance trailBalance = new TrailBalance();
								trailBalance.setLedgerAccount(rs.getString("HOSTACCOUNT"));
								trailBalance.setProfitCenter(rs.getString("PROFITCENTERCODE"));
								trailBalance.setCostCenter(rs.getString("COSTCENTERCODE"));

								map.put(trailBalance.getLedgerAccount(), trailBalance);
							}

							return map;
						}
					});
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		return null;
	}

	private List<TrailBalance> getOpeningBalance() {
		MapSqlParameterSource paramMap = null;

		StringBuilder sql = new StringBuilder();
		sql.append(" select AM.HOSTACCOUNT ledgerAccount, LR.PROVINCE stateCode,");
		sql.append(" LR.CLOSINGBAL openingBalance, LR.CLOSINGBALTYPE openingBalanceType");
		sql.append(" from ACCOUNTMAPPING AM");
		sql.append(" INNER JOIN TRAIL_BALANCE_REPORT_LAST_RUN LR ON LR.HOSTACCOUNT = AM.HOSTACCOUNT");

		paramMap = new MapSqlParameterSource();
		RowMapper<TrailBalance> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(TrailBalance.class);

		try {
			return namedJdbcTemplate.query(sql.toString(), paramMap, typeRowMapper);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		return new ArrayList<TrailBalance>();
	}

	private List<TrailBalance> getDebitAmount() {
		MapSqlParameterSource paramMap = null;

		StringBuilder sql = new StringBuilder();
		sql.append(" select P.Account ledgerAccount, RB.BRANCHPROVINCE stateCode, sum(postAmount) debitAmount");
		sql.append(" from POSTINGS P");
		sql.append(" INNER JOIN FINANCEMAIN FM ON FM.FINREFERENCE = P.FINREFERENCE");
		sql.append(" INNER JOIN RMTBRANCHES RB ON RB.BRANCHCODE = FM.FINBRANCH");
		sql.append(" where POSTDATE BETWEEN :MONTH_STARTDATE AND :MONTH_ENDDATE");
		sql.append(" and P.DRORCR = :DRORCR");
		sql.append(" group by P.Account, RB.BRANCHPROVINCE");

		paramMap = new MapSqlParameterSource();
		paramMap.addValue("MONTH_STARTDATE", monthStartDate);
		paramMap.addValue("MONTH_ENDDATE", monthEndDate);
		paramMap.addValue("DRORCR", "D");

		RowMapper<TrailBalance> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(TrailBalance.class);

		try {
			return namedJdbcTemplate.query(sql.toString(), paramMap, typeRowMapper);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		return null;
	}

	private List<TrailBalance> getCreditAmount() {
		MapSqlParameterSource paramMap = null;

		StringBuilder sql = new StringBuilder();
		sql.append(" select P.Account ledgerAccount, RB.BRANCHPROVINCE stateCode, sum(postAmount) creditAmount");
		sql.append(" from POSTINGS P");
		sql.append(" INNER JOIN FINANCEMAIN FM ON FM.FINREFERENCE = P.FINREFERENCE");
		sql.append(" INNER JOIN RMTBRANCHES RB ON RB.BRANCHCODE = FM.FINBRANCH");
		sql.append(" where POSTDATE BETWEEN :MONTH_STARTDATE AND :MONTH_ENDDATE");
		sql.append(" and P.DRORCR = :DRORCR");
		sql.append(" group by P.Account, RB.BRANCHPROVINCE");

		paramMap = new MapSqlParameterSource();
		paramMap.addValue("MONTH_STARTDATE", monthStartDate);
		paramMap.addValue("MONTH_ENDDATE", monthEndDate);
		paramMap.addValue("DRORCR", "C");

		RowMapper<TrailBalance> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(TrailBalance.class);

		try {
			return namedJdbcTemplate.query(sql.toString(), paramMap, typeRowMapper);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		return null;
	}
	
	private Map<String, String> getStates() {
		StringBuilder sql = new StringBuilder();
		sql.append(" select CPPROVINCE, BUSINESSAREA from RMTCOUNTRYVSPROVINCE");

		try {
			return namedJdbcTemplate.query(sql.toString(), new MapSqlParameterSource(),
					new ResultSetExtractor<Map<String, String>>() {
						@Override
						public Map<String, String> extractData(ResultSet rs) throws SQLException, DataAccessException {
							Map<String, String> map = new HashMap<>();

							while (rs.next()) {
								map.put(rs.getString("CPPROVINCE"), rs.getString("BUSINESSAREA"));
							}

							return map;
						}
					});
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		return null;
	}
	
	private void saveTrailBalance(List<TrailBalance> list) throws SQLException {
		StringBuilder sql = new StringBuilder();

		sql.append("insert into TRAIL_BALANCE_REPORT values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		List<Object[]> inputList = new ArrayList<Object[]>();
		for (TrailBalance item : list) {
			Object[] object = { item.getHeaderId(), item.getAccountType(), item.getCountryCode(), item.getStateCode(),
					item.getLedgerAccount(), item.getAccountTypeDes(), item.getOpeningBalance(),
					item.getOpeningBalanceType(), item.getDebitAmount(), item.getCreditAmount(),
					item.getClosingBalance(), item.getClosingBalanceType() };
			inputList.add(object);
		}

		jdbcTemplate.batchUpdate(sql.toString(), inputList);
		
		inputList =null;
	}
	
	private void saveTransactionDetails(List<TrailBalance> list) throws SQLException {
		StringBuilder sql = new StringBuilder();

		sql.append("insert into TRANSACTION_DETAIL_REPORT_TEMP values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		List<Object[]> inputList = new ArrayList<Object[]>();
		for (TrailBalance item : list) {
			Object[] object = { item.getRowNumber(), item.getLink(), item.getTransactionAmountType(),
					item.getLedgerAccount(), item.getUmskz(), item.getTransactionAmount(), item.getBusinessArea(),
					item.getBusinessUnit(), item.getCostCenter(), item.getProfitCenter(), item.getNarration1(),
					item.getNarration2() };
			inputList.add(object);
		}

		jdbcTemplate.batchUpdate(sql.toString(), inputList);

		inputList = null;
	}

	private void clearPreviousMonthTrailBalace() {
		try {
			jdbcTemplate.execute("TRUNCATE TABLE TRAIL_BALANCE_REPORT_LAST_RUN");
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw e;
		}
	}

	private void saveCurrentMonthTrailBalace() throws Exception {
		MapSqlParameterSource paramMap;
		StringBuilder sql = new StringBuilder();
		sql.append(" INSERT INTO TRAIL_BALANCE_REPORT_LAST_RUN");
		sql.append(" SELECT * FROM TRAIL_BALANCE_REPORT WHERE HEADERID = :HEADERID");

		paramMap = new MapSqlParameterSource();
		paramMap.addValue("HEADERID", headerId);

		try {
			namedJdbcTemplate.update(sql.toString(), paramMap);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw new Exception("Unable to insert current month trail balance.");
		}
	}

	private long createHeader() throws Exception {
		logger.info("Creating the trail Balance Header..");
		createNextId();
		createFileName();

		currency = (String) getSMTParameter("APP_DFT_CURR", String.class);
		companyName = (String) getSMTParameter("SAPGL_TBR_COMPANY", String.class);
		reportName = "Consolidated Trial Balance - Ledger A/C wise";

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
		paramMap.addValue("COMPANYNAME", getSMTParameter("SAPGL_TBR_COMPANY", String.class));
		paramMap.addValue("REPORTNAME", "Trial Balance Report");
		paramMap.addValue("STARTDATE", monthStartDate);
		paramMap.addValue("ENDDATE", monthEndDate);
		paramMap.addValue("CURRENCY", getSMTParameter("APP_DFT_CURR", String.class));

		try {
			namedJdbcTemplate.update(sql.toString(), paramMap);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw new Exception("Unable to insert trail balance header.");
		}

		return headerId;
	}

	private void createFileName() {
		StringBuilder builder = new StringBuilder();
		builder.append("TRIAL_BALANCE");
		builder.append("_");
		builder.append(DateUtil.format(monthStartDate, "ddMMYYYY"));
		builder.append("_");
		builder.append(DateUtil.format(monthEndDate, "ddMMYYYY"));
		builder.append("_");
		builder.append(String.valueOf(headerId));
		builder.append(".CSV");

		fileName = builder.toString();
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

	private int getFinancialMonth() {
		int financialMonth = 0;
		int month = DateUtil.getMonth(monthEndDate);

		switch (month) {
		case 4:
			financialMonth = 1;
			break;
		case 5:
			financialMonth = 2;
			break;
		case 6:
			financialMonth = 3;
			break;
		case 7:
			financialMonth = 4;
			break;
		case 8:
			financialMonth = 5;
			break;
		case 9:
			financialMonth = 6;
			break;
		case 10:
			financialMonth = 7;
			break;
		case 11:
			financialMonth = 8;
			break;
		case 12:
			financialMonth = 9;
			break;
		case 1:
			financialMonth = 10;
			break;
		case 2:
			financialMonth = 11;
			break;
		case 3:
			financialMonth = 12;
			break;
		default:
			break;
		}

		return financialMonth;
	}

	private void prepareTransactionDetails() throws Exception {
		int totalTransactions = extractTransactionsData();
		int pageSize = (Integer) getSMTParameter("SAPGL_TRAN_RECORD_COUNT", Integer.class);

		int pages = totalTransactions / pageSize;
		if (pages == 0) {
			pages = 1;
		}

		int summaryRecordId = totalTransactions;

		int fromRange = 1;
		int toRange = pageSize;
		int pageItr = 0;
		int mainRecords = 0;
		boolean pagesInserted = false;
		for (int page = 1; page <= pages; page++) {
			++summaryRecordId;
			pageItr = page;
			mainRecords = mainRecords + saveGLTranactions(fromRange, toRange);
			pagesInserted = true;
			fromRange = toRange + 1;
			toRange = toRange + pageSize;
			update(pageItr, fromRange, toRange);

			if (pagesInserted) {
				saveGLSummary(pageItr, summaryRecordId);
				pagesInserted = false;
			}
		}

		if (totalTransactions > mainRecords) {
			mainRecords = mainRecords + saveGLTranactions(fromRange, toRange);
			pagesInserted = true;
			update(pageItr, fromRange, toRange);
		}

		if (pagesInserted) {
			++summaryRecordId;
			saveGLSummary(pageItr, summaryRecordId);
		}
	}

	private void saveGLSummary(int pageItr, int summaryRecordId) throws Exception {
		Map<Integer, BigDecimal> map = getSummaryAmounts(pageItr);

		BigDecimal summaryAmount = BigDecimal.ZERO;
		BigDecimal debitAmount = BigDecimal.ZERO;
		BigDecimal creditAmount = BigDecimal.ZERO;

		if (map.containsKey(40)) {
			debitAmount = map.get(40);
		}

		if (map.containsKey(50)) {
			creditAmount = map.get(50);
		}

		summaryAmount = creditAmount.subtract(debitAmount);

		String BSCHL = "";
		BigDecimal WRBTR = summaryAmount;

		if (summaryAmount.compareTo(BigDecimal.ZERO) < 0) {
			BSCHL = "40";
			WRBTR = WRBTR.negate();
		} else {
			BSCHL = "50";
		}

		MapSqlParameterSource parameterSource;
		StringBuilder sql = new StringBuilder();
		sql.append(" INSERT INTO TRANSACTION_DETAIL_REPORT SELECT");
		sql.append(" :ID,");
		sql.append(" :LINK,");
		sql.append(" :BSCHL,");
		sql.append(" HKONT,");
		sql.append(" UMSKZ,");
		sql.append(" :WRBTR,");
		sql.append(" GSBER,");
		sql.append(" BUPLA,");
		sql.append(" KOSTL,");
		sql.append(" PRCTR,");
		sql.append(" ZUONR,");
		sql.append(" SGTXT");
		sql.append(" FROM TRANSACTION_DETAIL_REPORT_TEMP WHERE ROWNUM =:ROWNUM");

		parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("ID", summaryRecordId);
		parameterSource.addValue("LINK", pageItr);
		parameterSource.addValue("BSCHL", BSCHL);
		parameterSource.addValue("WRBTR", WRBTR);
		parameterSource.addValue("ROWNUM", 1);

		try {
			namedJdbcTemplate.update(sql.toString(), parameterSource);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw new Exception("Unable to insert the summary records for the page " + pageItr);
		}
	}

	private Map<Integer, BigDecimal> getSummaryAmounts(int pageItr) {
		Map<Integer, BigDecimal> map = new HashMap<Integer, BigDecimal>();

		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT BSCHL, COALESCE(SUM(WRBTR), 0) WRBTR FROM TRANSACTION_DETAIL_REPORT");
		sql.append(" WHERE LINK = ? GROUP BY BSCHL");

		jdbcTemplate.query(sql.toString(), new Object[] { pageItr }, new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				map.put(rs.getInt("BSCHL"), rs.getBigDecimal("WRBTR"));
			}

		});

		return map;
	}

	private void clearTables() {
		logger.info("Clearing GL Tables..");
		jdbcTemplate.execute("TRUNCATE TABLE TRANSACTION_SUMMARY_REPORT");
		jdbcTemplate.execute("TRUNCATE TABLE TRANSACTION_DETAIL_REPORT");
		jdbcTemplate.execute("TRUNCATE TABLE TRANSACTION_DETAIL_REPORT_TEMP");
	}

	private int extractTransactionsData() throws Exception {
		logger.info("Extracting the GL Transaction Details..");
		
		int rowNum = 0;
		List<TrailBalance> list = new ArrayList<>();

		Map<String, TrailBalance> ledgers = getLedgerDetails();
		Map<String, String> states = getStates();

		for (Entry<String, TrailBalance> entry : dataMap.entrySet()) {
			entry.getValue().setRowNumber(++rowNum);
			entry.getValue().setLink(0);

			entry.getValue().setTransactionAmount(
					entry.getValue().getCreditAmount().subtract(entry.getValue().getDebitAmount()));

			if (entry.getValue().getTransactionAmount().compareTo(BigDecimal.ZERO) < 0) {
				entry.getValue().setTransactionAmount(BigDecimal.ZERO.subtract(entry.getValue().getTransactionAmount()));
				entry.getValue().setTransactionAmountType("40");
			} else {
				entry.getValue().setTransactionAmountType("50");
			}

			
			// set cost and profit center details
			TrailBalance trailBalance = ledgers.get(entry.getKey().split("-")[0]);
			if (StringUtils.trimToNull(trailBalance.getProfitCenter()) != null) {
				entry.getValue().setProfitCenter(trailBalance.getProfitCenter());
			}

			if (StringUtils.trimToNull(trailBalance.getCostCenter()) != null) {
				entry.getValue().setCostCenter(trailBalance.getCostCenter());
			}
			
			
			// set business area
			String businessArea = states.get(entry.getKey().split("-")[1]);
			if(businessArea != null) {
				entry.getValue().setBusinessArea(businessArea);
			}
			
			// Saving transaction details
			list.add(entry.getValue());

			if (list.size() >= batchSize) {
				saveTransactionDetails(list);
				list.clear();
			}

		}

		if (!list.isEmpty()) {
			saveTransactionDetails(list);
		}

		
		return rowNum;
	}

	private void prepareGLDates() throws Exception {
		logger.info("Preparing GL Dates..");
		int year = (Integer) getSMTParameter("SAPGL_LAST_RUN_YEAR", Integer.class);
		int month = (Integer) getSMTParameter("SAPGL_LAST_RUN_MONTH", Integer.class);

		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.YEAR, year);
		glDate = calendar.getTime();

		monthStartDate = DateUtil.getMonthStart(glDate);
		monthEndDate = DateUtil.getMonthEnd(glDate);

		valueDate = getValueDate();
	}

	private int saveGLTranactions(int fromRange, int toRange) {
		MapSqlParameterSource parameterSource;

		StringBuilder sql = new StringBuilder();

		sql.append(" INSERT INTO TRANSACTION_DETAIL_REPORT SELECT * FROM  TRANSACTION_DETAIL_REPORT_TEMP");
		sql.append(" WHERE ID >=:FROM_RANGE AND ID <=:TO_RANGE");

		parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("FROM_RANGE", fromRange);
		parameterSource.addValue("TO_RANGE", toRange);

		try {
			return namedJdbcTemplate.update(sql.toString(), parameterSource);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		return 0;
	}

	private void update(int page, int fromRange, int toRange) {
		MapSqlParameterSource paramMap;

		StringBuilder sql = new StringBuilder();

		sql.append(" UPDATE TRANSACTION_DETAIL_REPORT SET LINK = :LINK");
		sql.append(" WHERE LINK =0");

		paramMap = new MapSqlParameterSource();
		paramMap.addValue("LINK", page);

		try {
			namedJdbcTemplate.update(sql.toString(), paramMap);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

	}

	private void generateGLReport() throws Exception {
		logger.info("Generating GL Report..");

		generateTransactionReport();

		generateTransactionSummaryReport();

		generateTrailBalanceReportByState();

	}

	private void generateTrailBalanceReportByState() {
		String query = "select PROVINCE, count(*) from TRAIL_BALANCE_REPORT where HEADERID = ? group by PROVINCE";
		jdbcTemplate.query(query, new Object[] { headerId }, new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				try {
					generateTrailBalanceReport(rs.getString("PROVINCE"));
				} catch (Exception e) {
					logger.error(Literal.EXCEPTION, e);
				}
			}
		});
	}

	private void generateTransactionReport() throws Exception {
		DataEngineExport dataEngine = null;
		logger.info("Generating Transaction Detail Report ..");
		dataEngine = new DataEngineExport(dataSource, userId, App.DATABASE.name(), true, getValueDate(), BajajInterfaceConstants.TRAIL_BALANCE_EXPORT_STATUS);
		dataEngine.setValueDate(valueDate);
		dataEngine.exportData("GL_TRANSACTION_EXPORT");
	}

	private void generateTransactionSummaryReport() throws Exception {
		logger.info("Generating Transaction Summary Report ..");
		DataEngineExport dataEngine = null;
		dataEngine = new DataEngineExport(dataSource, userId, App.DATABASE.name(), true, getValueDate(), BajajInterfaceConstants.TRAIL_BALANCE_EXPORT_STATUS);
		dataEngine.setValueDate(valueDate);
		dataEngine.exportData("GL_TRANSACTION_SUMMARY_EXPORT");
	}

	private void generateTrailBalanceReport(String province) throws Exception {
		logger.info("Generating Trail Balance Report ..");
		DataEngineExport dataEngine = null;
		dataEngine = new DataEngineExport(dataSource, userId, App.DATABASE.name(), true, getValueDate(), BajajInterfaceConstants.TRAIL_BALANCE_EXPORT_STATUS);

		Map<String, Object> filterMap = new HashMap<>();
		filterMap.put("HEADERID", headerId);
		filterMap.put("PROVINCE", province);

		Map<String, Object> parameterMap = new HashMap<>();
		parameterMap.put("START_DATE", DateUtil.format(monthStartDate, "ddMMYYYY"));
		parameterMap.put("END_DATE", DateUtil.format(monthEndDate, "ddMMYYYY"));
		parameterMap.put("HEADER_ID", headerId);

		parameterMap.put("COMPANY_NAME", companyName);
		parameterMap.put("REPORT_NAME", reportName);
		//parameterMap.put("FILE_NAME", fileName);
		parameterMap.put("STATE_CODE", province);

		StringBuilder builder = new StringBuilder();
		builder.append("From ");
		builder.append(DateUtil.format(monthStartDate, "dd-MMM-YY").toUpperCase());
		builder.append(" To ");
		builder.append(DateUtil.format(monthEndDate, "dd-MMM-YY").toUpperCase());

		parameterMap.put("TRANSACTION_DURATION", builder.toString());
		parameterMap.put("CURRENCY", currency + " - " + currency);

		dataEngine.setFilterMap(filterMap);
		dataEngine.setParameterMap(parameterMap);
		dataEngine.setValueDate(valueDate);
		dataEngine.exportData("GL_TRAIL_BALANCE_EXPORT");

	}

	private void updateGLDates() throws Exception {
		updateParameter("SAPGL_LAST_RUN_YEAR", DateUtil.getYear(monthEndDate));
		updateParameter("SAPGL_LAST_RUN_MONTH", DateUtil.getMonth(monthEndDate));
	}
}
