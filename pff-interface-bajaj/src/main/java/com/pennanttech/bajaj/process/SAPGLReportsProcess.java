package com.pennanttech.bajaj.process;

import com.pennant.backend.model.finance.TrailBalance;
import com.pennanttech.dataengine.DataEngineExport;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.pff.core.App;
import com.pennanttech.pff.core.util.DateUtil;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.apache.commons.lang.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;

public class SAPGLReportsProcess extends DataEngineExport {
	private static DataEngineStatus SAP_GL_STATUS = new DataEngineStatus("GL_TRAIL_BALANCE_EXPORT");

	private Map<String, String> parameters = new HashMap<>();
	private Date startDate;
	private Date endDate;

	public SAPGLReportsProcess(DataSource dataSource, long userId, Date valueDate, Date appDate) {
		super(dataSource, userId, App.DATABASE.name(), true, valueDate, SAP_GL_STATUS);
	}

	public void extractReport() throws Exception {
		generate();
	}
	
	public void extractReport(Date startDate, Date endDate) throws Exception {
		generate();
	}

	public void generate() throws Exception {
		logger.info("Extracting data...");
		initilize();

		Map<String, TrailBalance> transactions = getTransactions();

		saveTransactionDetails(transactions.values());

	}

	private void initilize() throws Exception {
		loadParameters();
		clearTables();

		if (startDate == null || endDate == null) {
			prepareDates();
		}

	}

	private void prepareDates() throws Exception {
		startDate = getCurrentTrialBalanceStartDate();

		endDate = DateUtil.getMonthEnd(startDate);
	}

	private Date getCurrentTrialBalanceStartDate() throws Exception {
		String query = "SELECT STARTDATE from TRAIL_BALANCE_HEADER WHERE ID = (select MAX(ID) from TRAIL_BALANCE_HEADER)";

		return jdbcTemplate.queryForObject(query, Date.class);
	}

	private Map<String, TrailBalance> getTransactions() throws Exception {
		StringBuilder sql = new StringBuilder();

		sql.append(" SELECT DD.ENTITYCODE, AM.HOSTACCOUNT,S.BUSINESSAREA,");
		sql.append(" PC.PROFITCENTERCODE, CC.COSTCENTERCODE, SUM(POSTAMOUNT) POSTAMOUNT, P.DRORCR  FROM POSTINGS P");
		sql.append(" INNER JOIN FINANCEMAIN FM ON FM.FINREFERENCE = P.FINREFERENCE");
		sql.append(" INNER JOIN RMTFINANCETYPES FT ON FT.FINTYPE = FM.FINTYPE");
		sql.append(" INNER JOIN SMTDIVISIONDETAIL DD ON DD.DIVISIONCODE = FT.FINDIVISION");
		sql.append(" INNER JOIN RMTBRANCHES B ON B.BRANCHCODE = FM.FINBRANCH");
		sql.append(" INNER JOIN RMTCOUNTRYVSPROVINCE S ON S.CPPROVINCE = B.BRANCHPROVINCE");
		sql.append(" INNER JOIN ACCOUNTMAPPING AM ON AM.ACCOUNT = P.ACCOUNT");
		sql.append(" LEFT JOIN PROFITCENTERS PC ON PC.PROFITCENTERID = AM.PROFITCENTERID");
		sql.append(" LEFT JOIN COSTCENTERS CC ON CC.COSTCENTERID = AM.COSTCENTERID");
		sql.append(" GROUP BY DD.ENTITYCODE, AM.HOSTACCOUNT, S.BUSINESSAREA,");
		sql.append(" PC.PROFITCENTERCODE, CC.COSTCENTERCODE, P.DRORCR");
		sql.append(" WHERE POSTDATE BETWEEN :MONTH_STARTDATE AND :MONTH_ENDDATE");

		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("MONTH_STARTDATE", startDate);
		paramMap.addValue("MONTH_ENDDATE", endDate);

		return parameterJdbcTemplate.query(sql.toString(), paramMap,
				new ResultSetExtractor<Map<String, TrailBalance>>() {
					@Override
					public Map<String, TrailBalance> extractData(ResultSet rs)
							throws SQLException, DataAccessException {
						Map<String, TrailBalance> map = new HashMap<>();

						String ZUONR = StringUtils.upperCase("CF - " + DateUtil.format(startDate, "MMM yy") + " - PLF");
						String SGTXT = StringUtils.upperCase("CF - " + DateUtil.format(startDate, "MMM yy") + " - PLF");

						TrailBalance item = null;
						String key = null;
						while (rs.next()) {
							key = "";
							key = key.concat(StringUtils.trimToEmpty(rs.getString("ENTITYCODE")));
							key = key.concat(StringUtils.trimToEmpty(rs.getString("HOSTACCOUNT")));
							key = key.concat(StringUtils.trimToEmpty(rs.getString("BUSINESSAREA")));
							key = key.concat(StringUtils.trimToEmpty(rs.getString("PROFITCENTERCODE")));
							key = key.concat(StringUtils.trimToEmpty(rs.getString("COSTCENTERCODE")));

							item = map.get(key);

							if (item == null) {
								item = new TrailBalance();
								item.setLink(0);
								item.setEntity(rs.getString("ENTITYCODE"));
								item.setLedgerAccount(rs.getString("HOSTACCOUNT"));
								item.setBusinessArea(rs.getString("BUSINESSAREA"));
								item.setProfitCenter(rs.getString("PROFITCENTERCODE"));
								item.setCostCenter(rs.getString("COSTCENTERCODE"));

								item.setUmskz(parameters.get("UMSKZ"));
								item.setBusinessArea(parameters.get("GSBER"));
								item.setNarration1(ZUONR);
								item.setNarration2(SGTXT);

								map.put(key, item);
							}

							if ("C".equals(rs.getObject("DRORCR"))) {
								item.setCreditAmount(getAmount(rs, "POSTAMOUNT"));
							} else {
								item.setDebitAmount(getAmount(rs, "POSTAMOUNT"));
							}

							item.setTransactionAmount(item.getCreditAmount().subtract(item.getDebitAmount()));

							if (item.getTransactionAmount().compareTo(BigDecimal.ZERO) < 0) {
								item.setTransactionAmount(BigDecimal.ZERO.subtract(item.getTransactionAmount()));
								item.setTransactionAmountType("40");
							} else {
								item.setTransactionAmountType("50");
							}
						}
						return map;
					}

				});
	}

	private void loadParameters() {
		logger.info("Loading parameters..");
		MapSqlParameterSource paramMap;

		StringBuilder sql = new StringBuilder();
		paramMap = new MapSqlParameterSource();
	
		sql.append("SELECT SYSPARMCODE, SYSPARMVALUE FROM SMTPARAMETERS where SYSPARMCODE");
		sql.append(" IN (:HKONT, :BLART, :BUKRS, :BUPLA, :UMSKZ, :GSBER, :PRCTR, :KOSTL, :SAPGL_TRAN_RECORD_COUNT)");

		paramMap.addValue("HKONT", "HKONT");
		paramMap.addValue("BLART", "BLART");
		paramMap.addValue("BUKRS", "BUKRS");
		paramMap.addValue("BUPLA", "BUPLA");
		paramMap.addValue("UMSKZ", "UMSKZ");
		paramMap.addValue("GSBER", "GSBER");
		paramMap.addValue("PRCTR", "PRCTR");
		paramMap.addValue("KOSTL", "KOSTL");
		paramMap.addValue("SAPGL_TRAN_RECORD_COUNT", "SAPGL_TRAN_RECORD_COUNT");

		parameterJdbcTemplate.query(sql.toString(), paramMap, new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				parameters.put(rs.getString("SYSPARMCODE"), rs.getString("SYSPARMVALUE"));
			}
		});
	}

	private void saveTransactionDetails(Collection<TrailBalance> list) throws SQLException {
		StringBuilder sql = new StringBuilder();

		sql.append(" insert into TRANSACTION_DETAIL_REPORT_TEMP(LINK, BSCHL, HKONT, UMSKZ,");
		sql.append(" WRBTR, GSBER, BUPLA, KOSTL, PRCTR, ZUONR, SGTXT)");
		sql.append(" values(:Link, :TransactionAmountType, :LedgerAccount, :Umskz,");
		sql.append(" :TransactionAmount, BusinessArea, :BusinessUnit, :CostCenter, :ProfitCenter,");
		sql.append(" :Narration1, :Narration1)");

		parameterJdbcTemplate.batchUpdate(sql.toString(), SqlParameterSourceUtils.createBatch(list.toArray()));
	}

	private void clearTables() {
		logger.info("Clearing GL Tables..");
		jdbcTemplate.execute("DELETE FROM TRANSACTION_SUMMARY_REPORT");
		jdbcTemplate.execute("DELETE FROM TRANSACTION_DETAIL_REPORT");
		jdbcTemplate.execute("DELETE FROM TRANSACTION_DETAIL_REPORT_TEMP");
		jdbcTemplate.execute("DELETE FROM TRANSACTION_DETAIL_REPORT_STGE");

		jdbcTemplate.execute("alter table TRANSACTION_DETAIL_REPORT modify ID generated as identity (start with 1)");
		jdbcTemplate
				.execute("alter table TRANSACTION_DETAIL_REPORT_TEMP modify ID generated as identity (start with 1)");
		jdbcTemplate
				.execute("alter table TRANSACTION_DETAIL_REPORT_STGE modify ID generated as identity (start with 1)");

	}

	private BigDecimal getAmount(ResultSet rs, String columnName) throws SQLException {
		BigDecimal amount;

		amount = rs.getBigDecimal(columnName);

		if (amount == null) {
			amount = BigDecimal.ZERO;
		}
		return amount;
	}

}
