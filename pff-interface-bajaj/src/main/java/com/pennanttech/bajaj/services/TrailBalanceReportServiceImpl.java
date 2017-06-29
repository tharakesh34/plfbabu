package com.pennanttech.bajaj.services;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.TransactionStatus;

import com.pennanttech.dataengine.DataEngineExport;
import com.pennanttech.pff.baja.BajajInterfaceConstants;
import com.pennanttech.pff.core.App;
import com.pennanttech.pff.core.Literal;
import com.pennanttech.pff.core.services.generalledger.TrailBalanceReportService;
import com.pennanttech.pff.core.util.DateUtil;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;

public class TrailBalanceReportServiceImpl extends BajajService implements TrailBalanceReportService {
	private final Logger	logger			= Logger.getLogger(getClass());

	private long			userId;
	private Date			valueDate		= null;
	private Date			glDate			= null;
	private Date			monthStartDate	= null;
	private Date			monthEndDate	= null;
	private long			headerId		= 0;
	private String			currency		= null;
	private String			companyName		= null;
	private String			reportName		= null;
	private String			fileName		= null;

	@Override
	public void generateReport(Object... params) throws Exception {
		this.userId = (long) params[0];
		try {
			prepareGLDates();

			clearTables();
			prepareTransactionDetails();
			prepareTransactionSummary();
			prepareTrailBalace();

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
		} finally {
			paramMap = null;
			sql = null;
		}
	}

	private void prepareTrailBalace() throws Exception {
		logger.info("Extracting the GL Trail Balances..");
		createHeader();

		MapSqlParameterSource paramMap;

		StringBuilder sql = new StringBuilder();
		sql.append(" INSERT INTO TRAIL_BALANCE_REPORT SELECT");		
		sql.append(" :HEADERID,");
		sql.append(" ATG.GROUPCODE,");
		sql.append(" AM.HOSTACCOUNT HOSTACCOUNT,");
		sql.append(" AT.ACTYPEDESC DESCRIPTION,");
		sql.append(" ABS(COALESCE(LR.CLOSINGBAL, 0)) OPENINGBAL,");
		sql.append(" COALESCE(LR.CLOSINGBALTYPE, 'Dr') OPENINGBALTYPE,");
		sql.append(" ABS(TODAYDEBITS) DEBITAMOUNT,");
		sql.append(" ABS(TODAYCREDITS) CREDITAMOUNT,");
		sql.append(" ABS(CASE when LR.CLOSINGBALTYPE='Cr' then COALESCE(LR.CLOSINGBAL, 0)+AH.TODAYNET ELSE  (COALESCE(LR.CLOSINGBAL, 0)*-1) + AH.TODAYNET END)  CLOSINGBAL,");
		sql.append(" CASE");
		sql.append(" WHEN  CASE when LR.CLOSINGBALTYPE='Cr' then COALESCE(LR.CLOSINGBAL, 0)+ AH.TODAYNET ELSE  (COALESCE(LR.CLOSINGBAL, 0)*-1) + AH.TODAYNET END <= 0");
		sql.append(" THEN 'Dr'");
		sql.append(" ELSE 'Cr'");
		sql.append(" END CR_DR");
		sql.append(" FROM");
		sql.append(" (SELECT ACCOUNTID,");
		sql.append(" SUM(TODAYDEBITS) TODAYDEBITS,");
		sql.append(" SUM(TODAYCREDITS) TODAYCREDITS,");
		sql.append(" SUM(TODAYNET) TODAYNET");
		sql.append(" FROM ACCOUNTSHISTORY AH");
		sql.append(" WHERE POSTDATE BETWEEN :MONTH_STARTDATE AND :MONTH_ENDDATE");
		sql.append(" GROUP BY ACCOUNTID");
		sql.append(" ) AH");
		sql.append(" INNER JOIN ACCOUNTMAPPING AM  ON AM.ACCOUNT = AH.ACCOUNTID");
		sql.append(" INNER JOIN RMTACCOUNTTYPES AT   ON AT.ACTYPE = AM.ACCOUNTTYPE");
		sql.append(" INNER JOIN ACCOUNTTYPEGROUP ATG   ON ATG.GROUPID = AT.ACTYPEGRPID");
		sql.append(" LEFT JOIN TRAIL_BALANCE_REPORT_LAST_RUN LR   ON LR.ACTYPEGRPID  = ATG.GROUPCODE   AND LR.HOSTACCOUNT = AM.HOSTACCOUNT");
		
		paramMap = new MapSqlParameterSource();
		paramMap.addValue("HEADERID", headerId);
		paramMap.addValue("MONTH_STARTDATE", monthStartDate);
		paramMap.addValue("MONTH_ENDDATE", monthEndDate);

		try {
			namedJdbcTemplate.update(sql.toString(), paramMap);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw new Exception("Unable to prpare the transaction summary report.");
		}
	}

	private void clearPreviousMonthTrailBalace() {
		try {
			jdbcTemplate.execute("TRUNCATE TABLE TRAIL_BALANCE_REPORT_LAST_RUN");
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
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
		} finally {
			paramMap = null;
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
		} finally {
			paramMap = null;
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
		} finally {
			sql = null;
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
		//totalTransactions = groupTranactions();

		if (totalTransactions == 0) {
			throw new Exception("Transaction details not avialble for the dates between  "
					+ DateUtil.format(monthStartDate, DateFormat.LONG_DATE) + " and "
					+ DateUtil.format(monthEndDate, DateFormat.LONG_DATE));
		}

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
		MapSqlParameterSource parameterSource;
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT BSCHL, COALESCE(SUM(WRBTR), 0) WRBTR FROM TRANSACTION_DETAIL_REPORT WHERE LINK = :LINK GROUP BY BSCHL");

		parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("LINK", pageItr);

		return namedJdbcTemplate.query(sql.toString(), parameterSource,
				new ResultSetExtractor<Map<Integer, BigDecimal>>() {

					@Override
					public Map<Integer, BigDecimal> extractData(ResultSet rs) throws SQLException, DataAccessException {
						Map<Integer, BigDecimal> map = new HashMap<Integer, BigDecimal>();
						while (rs.next()) {
							map.put(rs.getInt("BSCHL"), rs.getBigDecimal("WRBTR"));
						}
						return map;
					}

				});
	}

	private void clearTables() {
		logger.info("Clearing GL Tables..");
		jdbcTemplate.execute("TRUNCATE TABLE TRANSACTION_SUMMARY_REPORT");
		jdbcTemplate.execute("TRUNCATE TABLE TRANSACTION_DETAIL_REPORT");
		jdbcTemplate.execute("TRUNCATE TABLE TRANSACTION_DETAIL_REPORT_TEMP");
	}

	private int extractTransactionsData() throws Exception {
		logger.info("Extracting the GL Transaction Details..");
		MapSqlParameterSource paramMap;
		StringBuilder sql = new StringBuilder();

		sql.append(" INSERT INTO TRANSACTION_DETAIL_REPORT_TEMP");
		sql.append(" SELECT");
		sql.append(" ROWNUM,");
		sql.append(" 0,");
		sql.append(" CASE WHEN AH.POSTAMOUNT < 0 THEN '40' ELSE '50' END BSCHL,");
		sql.append(" AM.HOSTACCOUNT HKONT,");
		sql.append(" :UMSKZ,");
		sql.append(" AH.POSTAMOUNT WRBTR,");
		sql.append(" :GSBER,");
		sql.append(" :BUPLA,");
		sql.append(" COALESCE(CC.COSTCENTERCODE, :KOSTL) KOSTL,");
		sql.append(" PC.PROFITCENTERCODE PRCTR,");
		sql.append(" :ZUONR,");
		sql.append(" :SGTXT");
		sql.append(" FROM (SELECT AH.ACCOUNTID, SUM(AH.TODAYNET) POSTAMOUNT FROM ACCOUNTSHISTORY AH");
		sql.append(" WHERE AH.POSTDATE BETWEEN :MONTH_STARTDATE AND :MONTH_ENDDATE");
		sql.append(" GROUP BY AH.ACCOUNTID) AH");
		sql.append(" INNER JOIN ACCOUNTMAPPING AM ON AM.ACCOUNT = AH.ACCOUNTID");
		sql.append(" LEFT JOIN PROFITCENTERS PC ON PC.PROFITCENTERID = AM.PROFITCENTERID");
		sql.append(" LEFT JOIN COSTCENTERS CC ON CC.COSTCENTERID = AM.COSTCENTERID");

		paramMap = new MapSqlParameterSource();
		paramMap.addValue("MONTH_STARTDATE", monthStartDate);
		paramMap.addValue("MONTH_ENDDATE", monthEndDate);
		paramMap.addValue("POSTSTATUS", "S");
		paramMap.addValue("UMSKZ", getSMTParameter("UMSKZ", String.class));
		paramMap.addValue("GSBER", getSMTParameter("GSBER", String.class));
		paramMap.addValue("BUPLA", getSMTParameter("BUPLA", String.class));
		paramMap.addValue("KOSTL", getSMTParameter("KOSTL", String.class));
		paramMap.addValue("ZUONR", StringUtils.upperCase("CF - " + DateUtil.format(glDate, "MMM YY") + " - PLF"));
		paramMap.addValue("SGTXT", StringUtils.upperCase("CF - " + DateUtil.format(glDate, "MMM YY") + " - PLF"));

		try {
			return namedJdbcTemplate.update(sql.toString(), paramMap);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		} finally {
			paramMap = null;
			sql = null;
		}

		return 0;
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

		generateTrailBalanceReport();
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

	private void generateTrailBalanceReport() throws Exception {
		logger.info("Generating Trail Balance Report ..");
		DataEngineExport dataEngine = null;
		dataEngine = new DataEngineExport(dataSource, userId, App.DATABASE.name(), true, getValueDate(), BajajInterfaceConstants.TRAIL_BALANCE_EXPORT_STATUS);

		Map<String, Object> filterMap = new HashMap<>();
		filterMap.put("HEADERID", headerId);

		Map<String, Object> parameterMap = new HashMap<>();
		parameterMap.put("START_DATE", DateUtil.format(monthStartDate, "ddMMYYYY"));
		parameterMap.put("END_DATE", DateUtil.format(monthEndDate, "ddMMYYYY"));
		parameterMap.put("HEADER_ID", headerId);

		parameterMap.put("COMPANY_NAME", companyName);
		parameterMap.put("REPORT_NAME", reportName);
		parameterMap.put("FILE_NAME", fileName);

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
