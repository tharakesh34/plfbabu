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

import com.pennanttech.dataengine.DataEngineExport;
import com.pennanttech.pff.core.App;
import com.pennanttech.pff.core.Literal;
import com.pennanttech.pff.core.services.generalledger.GeneralLedgerService;
import com.pennanttech.pff.core.util.DateUtil;

public class SAPGLServiceImpl extends BajajServices implements GeneralLedgerService {
	private final Logger logger = Logger.getLogger(getClass());

	private long						userId;
	private Date						valueDate			= null;
	private Date						glDate			= null;
	private Date						monthStartDate	= null;
	private Date						monthEndDate	= null;
	private long						headerId		= 0;
	private String						currency		= null;
	private String						companyName		= null;
	private String						reportName		= null;
	private String						fileName		= null;

	@Override
	public void generateGLReport(Object... params) throws Exception {
		this.userId = (long) params[0];
		try {
			prepareGLDates();

			clearTables();

			prepareTransactionDetails();

			prepareTransactionSummary();

			prepareTrailBalace();

			updateGLDates();

			generateGLReport();

		} catch (Exception e) {
			throw e;
		}

	}

	private void prepareTransactionSummary() throws Exception {
		MapSqlParameterSource paramMap;

		StringBuilder sql = new StringBuilder();
		sql.append(" INSERT INTO SAPGL_TRAN_SUMMARY_REPORT SELECT");
		sql.append("  DISTINCT LINK,");
		sql.append(" :BLDAT,");
		sql.append(" :BLART,");
		sql.append(" :BUKRS,");
		sql.append(" :BUDAT,");
		sql.append(" :MONAT,");
		sql.append(" :APP_DFT_CURR,");
		sql.append(" :XBLNR,");
		sql.append(" :BKTXT");
		sql.append(" FROM SAPGL_TRAN_DETAIL_REPORT");

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
		sql.append(" INSERT INTO SAPGL_TRAIL_BALANCE_REPORT SELECT");
		sql.append(" :HEADERID,");
		sql.append(" ACTYPEGRPID,");
		sql.append(" HOSTACCOUNT,");
		sql.append(" DESCRIPTION,");
		sql.append(" OPENINGBAL,");
		sql.append(" OPENINGBALTYPE,");
		sql.append(" DEBITAMOUNT,");
		sql.append(" CREDITAMOUNT,");
		sql.append(" CLOSINGBAL,");
		sql.append(" CLOSINGBALTYPE");
		sql.append(" FROM GL_TRAIL_BALANCE_REPORT_VIEW");

		paramMap = new MapSqlParameterSource();
		paramMap.addValue("HEADERID", headerId);

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

	private long createHeader() throws Exception {
		logger.info("Creating the trail Balance Header..");
		createNextId();
		createFileName();

		currency = (String) getSMTParameter("APP_DFT_CURR", String.class);
		companyName = (String) getSMTParameter("SAPGL_TBR_COMPANY", String.class);
		reportName = "Consolidated Trial Balance - Ledger A/C wise";

		MapSqlParameterSource paramMap;
		StringBuilder sql = new StringBuilder();
		sql.append(" INSERT INTO SAPGL_TRAIL_BALANCE_HEADER VALUES(");
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
		sql.append(" FROM SAPGL_TRAIL_BALANCE_HEADER");

		try {
			this.headerId = this.jdbcTemplate.queryForObject(sql.toString(), Long.class);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception {}", e);
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
		totalTransactions = groupTranactions();

		if (totalTransactions == 0) {
			throw new Exception("Transaction details not avialble.");
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
		sql.append(" INSERT INTO SAPGL_TRAN_DETAIL_REPORT SELECT");
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
		sql.append(" FROM SAPGL_TRAN_DETAIL_REPORT_STAGE WHERE ROWNUM =:ROWNUM");

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
		sql.append("SELECT BSCHL, COALESCE(SUM(WRBTR), 0) WRBTR FROM SAPGL_TRAN_DETAIL_REPORT WHERE LINK = :LINK GROUP BY BSCHL");

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
		jdbcTemplate.execute("TRUNCATE TABLE SAPGL_TRAN_SUMMARY_REPORT");
		jdbcTemplate.execute("TRUNCATE TABLE SAPGL_TRAN_DETAIL_REPORT");
		jdbcTemplate.execute("TRUNCATE TABLE SAPGL_TRAN_DETAIL_REPORT_STAGE");
		jdbcTemplate.execute("TRUNCATE TABLE SAPGL_TRAN_DETAIL_REPORT_TEMP");
	}

	private int extractTransactionsData() throws Exception {
		logger.info("Extracting the GL Transaction Details..");
		MapSqlParameterSource paramMap;
		StringBuilder sql = new StringBuilder();

		sql.append(" INSERT INTO SAPGL_TRAN_DETAIL_REPORT_TEMP");
		sql.append(" SELECT ROWNUM,");
		sql.append(" 0,");
		sql.append(" (CASE WHEN DRORCR=:DR THEN 40 WHEN DRORCR=:CR THEN 50 END) BSCHL,");
		sql.append(" HKONT,");
		sql.append(" UMSKZ,");
		sql.append(" COALESCE(WRBTR, 0) WRBTR,");
		sql.append(" :GSBER,");
		sql.append(" :BUPLA,");
		sql.append(" KOSTL,");
		sql.append(" PRCTR,");
		sql.append(" :ZUONR,");
		sql.append(" :SGTXT");
		sql.append(" FROM (");
		sql.append(" SELECT");
		sql.append(" DRORCR,");
		sql.append(" HKONT,");
		sql.append(" :UMSKZ UMSKZ,");
		sql.append(" KOSTL,");
		sql.append(" PRCTR,");
		sql.append(" SUM(POSTAMOUNT) WRBTR FROM(");
		sql.append(" SELECT AM.HOSTACCOUNT HKONT,DRORCR, POSTAMOUNT,");
		sql.append(" COALESCE(CC.COSTCENTERCODE, :KOSTL) KOSTL,");
		sql.append(" PC.PROFITCENTERCODE PRCTR");
		sql.append(" FROM POSTINGS P");
		sql.append(" INNER JOIN FINANCEMAIN F ON F.FINREFERENCE = P.FINREFERENCE");
		sql.append(" INNER JOIN ACCOUNTS A ON A.ACCOUNTID = P.ACCOUNT");
		sql.append(" INNER JOIN ACCOUNTMAPPING AM ON AM.ACCOUNT = P.ACCOUNT");
		sql.append(" INNER JOIN RMTFINANCETYPES RF ON RF.FINTYPE = F.FINTYPE");
		sql.append(" INNER JOIN RMTACCOUNTTYPES RC ON RC.ACTYPE = A.ACTYPE");
		sql.append(" INNER JOIN PROFITCENTERS PC ON PC.PROFITCENTERID = RC.PROFITCENTERID OR PC.PROFITCENTERID = RF.PROFITCENTERID");
		sql.append(" LEFT JOIN COSTCENTERS CC ON CC.COSTCENTERID = RC.COSTCENTERID");
		sql.append(" WHERE POSTDATE BETWEEN :MONTH_STARTDATE AND :MONTH_ENDDATE");
		sql.append(" AND POSTSTATUS =:POSTSTATUS)T");
		sql.append(" GROUP BY HKONT, DRORCR, KOSTL, PRCTR)");

		paramMap = new MapSqlParameterSource();
		paramMap.addValue("MONTH_STARTDATE", monthStartDate);
		paramMap.addValue("MONTH_ENDDATE", monthEndDate);
		paramMap.addValue("DR", "D");
		paramMap.addValue("CR", "C");
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
		
		valueDate = (Date)getSMTParameter("APP_VALUEDATE", Date.class);
	}

	private int groupTranactions() {
		logger.info("Grouping the GL Transaction..");
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT T1.*, T2.COUNT FROM SAPGL_TRAN_DETAIL_REPORT_TEMP T1");
		sql.append(" INNER JOIN (SELECT COUNT(*) COUNT, HKONT, PRCTR, KOSTL");
		sql.append(" FROM SAPGL_TRAN_DETAIL_REPORT_TEMP GROUP BY  HKONT, PRCTR, KOSTL) T2 ON T1.HKONT = T2.HKONT AND T1.PRCTR = T2.PRCTR AND T1.KOSTL = T2.KOSTL");

		return namedJdbcTemplate.query(sql.toString(), new MapSqlParameterSource(), new ResultSetExtractor<Integer>() {

			@Override
			public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
				int count = 0;
				int id = 1;
				MapSqlParameterSource paramMap;
				boolean secondRecord = false;
				BigDecimal total = BigDecimal.ZERO;

				while (rs.next()) {
					paramMap = new MapSqlParameterSource();
					paramMap.addValue("ID", id);
					paramMap.addValue("LINK", rs.getLong("LINK"));
					paramMap.addValue("BSCHL", rs.getInt("BSCHL"));
					paramMap.addValue("HKONT", rs.getString("HKONT"));
					paramMap.addValue("UMSKZ", rs.getString("UMSKZ"));
					paramMap.addValue("WRBTR", rs.getBigDecimal("WRBTR"));
					paramMap.addValue("GSBER", rs.getString("GSBER"));
					paramMap.addValue("BUPLA", rs.getString("BUPLA"));
					paramMap.addValue("KOSTL", rs.getString("KOSTL"));
					paramMap.addValue("PRCTR", rs.getString("PRCTR"));
					paramMap.addValue("ZUONR", rs.getString("ZUONR"));
					paramMap.addValue("SGTXT", rs.getString("SGTXT"));
					
					if ((Integer) paramMap.getValue("BSCHL") == 40) {
						BigDecimal amount = (BigDecimal) paramMap.getValue("WRBTR");
						paramMap.addValue("WRBTR", amount.negate());
					}
					
					

					if (rs.getInt("COUNT") == 1) {
						count = saveTransactions(count, paramMap);
						id++;
					} else {
						int creditDebit = (Integer) paramMap.getValue("BSCHL");
						BigDecimal amount = (BigDecimal) paramMap.getValue("WRBTR");

						if (creditDebit == 50) {
							total = total.add(amount);
						} else {
							total = total.subtract(amount);
						}

						if (secondRecord) {
							paramMap.addValue("WRBTR", total);
							count = saveTransactions(count, paramMap);
							id++;
							total = BigDecimal.ZERO;
							secondRecord = false;
						} else {
							secondRecord = true;
						}
					}
				}
				return count;
			}
		});

	}

	private int saveTransactions(int count, MapSqlParameterSource paramMap) {
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO SAPGL_TRAN_DETAIL_REPORT_STAGE VALUES(");
		sql.append(":ID,:LINK, :BSCHL, :HKONT, :UMSKZ, :WRBTR, :GSBER, :BUPLA, :KOSTL, :PRCTR, :ZUONR, :SGTXT)");

		try {
			count = count + namedJdbcTemplate.update(sql.toString(), paramMap);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		return count;
	}

	private int saveGLTranactions(int fromRange, int toRange) {
		MapSqlParameterSource parameterSource;

		StringBuilder sql = new StringBuilder();

		sql.append(" INSERT INTO SAPGL_TRAN_DETAIL_REPORT SELECT * FROM  SAPGL_TRAN_DETAIL_REPORT_STAGE");
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

		sql.append(" UPDATE SAPGL_TRAN_DETAIL_REPORT SET LINK = :LINK");
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
		logger.info("Generating Transaction Detail Report ..");
		DataEngineExport dataEngine = new DataEngineExport(dataSource, userId, App.DATABASE.name());
		dataEngine.setValueDate(valueDate);
		dataEngine.exportData("GL_TRANSACTION_EXPORT");
	}

	private void generateTransactionSummaryReport() throws Exception {
		logger.info("Generating Transaction Summary Report ..");
		DataEngineExport dataEngine = new DataEngineExport(dataSource, userId, App.DATABASE.name());
		dataEngine.setValueDate(valueDate);
		dataEngine.exportData("GL_TRANSACTION_SUMMARY_EXPORT");
	}

	private void generateTrailBalanceReport() throws Exception {
		logger.info("Generating Trail Balance Report ..");
		DataEngineExport dataEngine = new DataEngineExport(dataSource, userId, App.DATABASE.name());

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
