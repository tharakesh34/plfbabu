package com.pennanttech.bajaj.services;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.pennanttech.pff.core.util.DateUtil;

public class SAPGLService {
	private static final Logger			logger	= Logger.getLogger(SAPGLService.class);

	private DataSource					dataSource;
	private NamedParameterJdbcTemplate	namedJdbcTemplate;
	private JdbcTemplate				jdbcTemplate;

	public void process() throws RuntimeException {
		clearDetails();

		int totalRecords = insertIntoTemp();
		int pageSize = (Integer) getParameter("SAPGL_TRAN_RECORD_COUNT", Integer.class);

		int pages = totalRecords / pageSize;

		int summaryRecordId = totalRecords;

		int fromRange = 1;
		int toRange = pageSize;
		int pageItr = 0;
		for (int page = 1; page <= pages; page++) {
			++summaryRecordId;
			pageItr = page;
			insertIntoMain(fromRange, toRange);
			fromRange = toRange + 1;
			toRange = toRange + pageSize;
			update(pageItr);

			insertSummary(pageItr, summaryRecordId);
		}

		insertIntoMain(fromRange, toRange);
		update(++pageItr);
		
		insertSummary(pageItr, summaryRecordId);
	}

	private void insertSummary(int pageItr, int summaryRecordId) {
		Map<Integer, BigDecimal> map = getSummaryAmounts(pageItr);

		BigDecimal summaryAmount = BigDecimal.ZERO;
		BigDecimal debitAmount = map.get(40);
		BigDecimal creditAmount = map.get(50);
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
		sql.append(" '' HKONT,");
		sql.append(" UMSKZ,");
		sql.append(" :WRBTR,");
		sql.append(" GSBER,");
		sql.append(" BUPLA,");
		sql.append(" KOSTL,");
		sql.append(" PRCTR,");
		sql.append(" ZUONR,");
		sql.append(" SGTXT");
		sql.append(" FROM SAPGL_TRAN_DETAIL_REPORT_TEMP WHERE ROWNUM =:ROWNUM");

		parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("ID", summaryRecordId);
		parameterSource.addValue("LINK", pageItr);
		parameterSource.addValue("BSCHL", BSCHL);
		parameterSource.addValue("WRBTR", WRBTR);
		parameterSource.addValue("ROWNUM", 1);

		try {
			namedJdbcTemplate.update(sql.toString(), parameterSource);
		} catch (Exception e) {
			logger.error("Exception: ", e);
			e.printStackTrace();
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

	private void clearDetails() {
		jdbcTemplate.execute("TRUNCATE TABLE SAPGL_TRAN_DETAIL_REPORT");
		jdbcTemplate.execute("TRUNCATE TABLE SAPGL_TRAN_DETAIL_REPORT_TEMP");
	}

	private int insertIntoTemp() {
		MapSqlParameterSource parameterSource;

		StringBuilder sql = new StringBuilder();

		sql.append(" INSERT INTO SAPGL_TRAN_DETAIL_REPORT_TEMP ");
		sql.append(" SELECT ROWNUM,");
		sql.append(" 0,");
		sql.append(" CASE WHEN DRORCR=:DR THEN 40 WHEN DRORCR=:CR THEN 50 END BSCHL,");
		sql.append(" '' HKONT,");
		sql.append(" (SELECT SYSPARMVALUE FROM SMTPARAMETERS WHERE SYSPARMCODE=:UMSKZ) UMSKZ,");
		sql.append(" T1.DRPOSTAMOUNT-T1.CRPOSTAMOUNT WRBTR,");
		sql.append(" (SELECT SYSPARMVALUE FROM SMTPARAMETERS WHERE SYSPARMCODE=:GSBER) GSBER,");
		sql.append(" (SELECT SYSPARMVALUE FROM SMTPARAMETERS WHERE SYSPARMCODE=:BUPLA) BUPLA,");
		sql.append(" COALESCE(RF.FINCCY,RC.ACPURPOSE,(SELECT SYSPARMVALUE FROM SMTPARAMETERS WHERE SYSPARMCODE=:KOSTL)) KOSTL,");
		sql.append(" COALESCE(RF.FINCCY,RC.ACPURPOSE,(SELECT SYSPARMVALUE FROM SMTPARAMETERS WHERE SYSPARMCODE=:PRCTR)) PRCTR,");
		sql.append(" :ZUONR,");
		sql.append(" :SGTXT");
		sql.append(" FROM ACCOUNTS A");
		sql.append(" INNER JOIN POSTINGS_SAPGL P ON  P.ACCOUNT = A.ACCOUNTID AND POSTDATE ");
		sql.append(" BETWEEN :MONTH_STARTDATE AND :MONTH_ENDDATE AND POSTSTATUS = :POSTSTATUS");
		sql.append(" LEFT JOIN FINANCEMAIN F ON F.FINREFERENCE =P.FINREFERENCE");
		sql.append(" LEFT JOIN RMTFINANCETYPES RF ON RF.FINTYPE =F.FINTYPE");
		sql.append(" LEFT JOIN RMTACCOUNTTYPES RC ON RC.ACTYPE = A.ACTYPE");
		sql.append(" INNER JOIN (SELECT T.ACCOUNT,T.DR,T.DRPOSTAMOUNT,COALESCE(T1.CR,:CR)CR,COALESCE(T1.CRPOSTAMOUNT,0)CRPOSTAMOUNT FROM (");
		sql.append(" SELECT ACCOUNT,SUM(POSTAMOUNT)DRPOSTAMOUNT,:DR DR FROM POSTINGS P , ACCOUNTS A WHERE P.ACCOUNT = A.ACCOUNTID AND POSTDATE ");
		sql.append(" BETWEEN :MONTH_STARTDATE AND :MONTH_ENDDATE AND POSTSTATUS =:POSTSTATUS AND DRORCR=:DR");
		sql.append(" GROUP BY ACCOUNT)T LEFT JOIN (SELECT ACCOUNT,SUM(POSTAMOUNT)CRPOSTAMOUNT,:CR CR FROM POSTINGS P , ACCOUNTS A WHERE P.ACCOUNT = A.ACCOUNTID AND POSTDATE");
		sql.append(" BETWEEN :MONTH_STARTDATE AND :MONTH_ENDDATE AND POSTSTATUS =:POSTSTATUS AND DRORCR=:CR");
		sql.append(" GROUP BY ACCOUNT)T1 ON T.ACCOUNT =T1.ACCOUNT");
		sql.append(" UNION");
		sql.append(" SELECT ACCOUNT,:DR,0,:CR CR,SUM(POSTAMOUNT)POSTAMOUNT FROM POSTINGS P , ACCOUNTS A WHERE P.ACCOUNT = A.ACCOUNTID AND POSTDATE");
		sql.append(" BETWEEN :MONTH_STARTDATE AND :MONTH_ENDDATE AND POSTSTATUS =:POSTSTATUS AND DRORCR=:CR");
		sql.append(" AND ACCOUNT NOT IN (SELECT ACCOUNT FROM POSTINGS P , ACCOUNTS A WHERE P.ACCOUNT = A.ACCOUNTID AND POSTDATE");
		sql.append(" BETWEEN :MONTH_STARTDATE AND :MONTH_ENDDATE AND POSTSTATUS =:POSTSTATUS AND DRORCR=:DR)");
		sql.append(" GROUP BY ACCOUNT) T1 ON T1.ACCOUNT=P.ACCOUNT");
		sql.append(" ORDER BY P.ACCOUNT,POSTDATE ASC");

		int year = (Integer) getParameter("SAPGL_LAST_RUN_YEAR", Integer.class);
		int month = (Integer) getParameter("SAPGL_LAST_RUN_MONTH", Integer.class);

		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.YEAR, year);
		Date date = calendar.getTime();

		parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("MONTH_STARTDATE", DateUtil.getMonthStart(date));
		parameterSource.addValue("MONTH_ENDDATE", DateUtil.getMonthEnd(date));
		parameterSource.addValue("DR", "D");
		parameterSource.addValue("CR", "C");
		parameterSource.addValue("POSTSTATUS", "S");
		parameterSource.addValue("UMSKZ", "UMSKZ");
		parameterSource.addValue("GSBER", "GSBER");
		parameterSource.addValue("BUPLA", "BUPLA");
		parameterSource.addValue("KOSTL", "KOSTL");
		parameterSource.addValue("PRCTR", "PRCTR");
		parameterSource.addValue("ZUONR", StringUtils.upperCase("CF - " + DateUtil.format(date, "MMM YY") + " - PLF"));
		parameterSource.addValue("SGTXT", StringUtils.upperCase("CF - " + DateUtil.format(date, "MMM YY") + " - PLF"));

		try {
			return namedJdbcTemplate.update(sql.toString(), parameterSource);
		} catch (Exception e) {
			logger.error("Exception: ", e);
			e.printStackTrace();
		}

		return 0;
	}

	private int insertIntoMain(int fromRange, int toRange) {
		MapSqlParameterSource parameterSource;

		StringBuilder sql = new StringBuilder();

		sql.append(" INSERT INTO SAPGL_TRAN_DETAIL_REPORT SELECT * FROM  SAPGL_TRAN_DETAIL_REPORT_TEMP");
		sql.append(" WHERE ID >=:FROM_RANGE AND ID <=:TO_RANGE");

		parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("FROM_RANGE", fromRange);
		parameterSource.addValue("TO_RANGE", toRange);

		try {
			return namedJdbcTemplate.update(sql.toString(), parameterSource);
		} catch (Exception e) {
			logger.error("Exception: ", e);
			e.printStackTrace();
		}

		return 0;
	}

	private void update(int page) {
		MapSqlParameterSource parameterSource;

		StringBuilder sql = new StringBuilder();

		sql.append(" UPDATE SAPGL_TRAN_DETAIL_REPORT SET LINK = :LINK");
		sql.append(" WHERE LINK =0");

		parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("LINK", page);

		try {
			namedJdbcTemplate.update(sql.toString(), parameterSource);
		} catch (Exception e) {
			logger.error("Exception: ", e);
			e.printStackTrace();
		}

	}

	private Object getParameter(String sysParmCode, Class<?> type) {
		MapSqlParameterSource parameterSource;

		StringBuilder sql = new StringBuilder();
		parameterSource = new MapSqlParameterSource();

		sql.append("SELECT SYSPARMVALUE FROM SMTPARAMETERS where SYSPARMCODE = :SYSPARMCODE");
		parameterSource.addValue("SYSPARMCODE", sysParmCode);

		try {
			return namedJdbcTemplate.queryForObject(sql.toString(), parameterSource, type);
		} catch (Exception e) {
			logger.error("Exception: ", e);
			e.printStackTrace();
		}

		return null;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

}
