package com.pennanttech.pff.external.sapgl;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.finance.TrailBalance;
import com.pennanttech.dataengine.DataEngineExport;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.App.Database;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;

public class SAPGLExtract extends DataEngineExport {
	public static DataEngineStatus SAP_GL_STATUS = new DataEngineStatus("GL_TRANSACTION_SUMMARY_EXPORT");

	private Map<String, String> parameters = new HashMap<>();
	private Date startDate;
	private Date endDate;
	private Date appDate;
	private String entityCode = null;

	public SAPGLExtract(DataSource dataSource, long userId, Date valueDate, Date appDate) {
		super(dataSource, userId, App.DATABASE.name(), true, valueDate, SAP_GL_STATUS);
		this.appDate = appDate;
	}

	public void extractReport(String[] entityDetails, Date startDate, Date endDate) throws Exception {
		this.entityCode = entityDetails[0];
		this.startDate = startDate;
		this.endDate = endDate;

		try {
			generate();
			exportSummaryReport();
			exportTransactionReport();
			SAP_GL_STATUS.setStatus("S");
		} catch (Exception e) {
			SAP_GL_STATUS.setStatus("F");
			logger.error(Literal.EXCEPTION, e);
		}
	}

	public void extractReport(Date startDate, Date endDate) throws Exception {
		generate();

		exportSummaryReport();

		exportTransactionReport();
	}

	public void generate() throws Exception {
		logger.info("Extracting data...");
		initilize();

		Map<String, TrailBalance> transactions = getTransactions();

		SAP_GL_STATUS.setTotalRecords(transactions.size());

		saveTransactionDetails(transactions.values());

		groupTransactions();

		saveTransactionSummary();
	}

	private void initilize() throws Exception {
		loadParameters();

		if (startDate == null || endDate == null) {
			prepareDates();
		}

		clearTables();
	}

	private void prepareDates() throws Exception {
		startDate = getCurrentTrialBalanceStartDate();
		endDate = DateUtil.getMonthEnd(startDate);
	}

	private Date getCurrentTrialBalanceStartDate() throws Exception {
		String query = "SELECT STARTDATE from TRIAL_BALANCE_HEADER WHERE ID = (select MAX(ID) from TRIAL_BALANCE_HEADER WHERE DIMENSION = ?) AND DIMENSION = ? AND ENTITYCODE = ?";
		return jdbcTemplate.queryForObject(query, Date.class, "STATE", "STATE", entityCode);
	}

	private Map<String, TrailBalance> getTransactions() throws Exception {
		StringBuilder sql = new StringBuilder();

		sql.append(" SELECT  ENTITYCODE, AM.HOSTACCOUNT, S.BUSINESSAREA,");

		sql.append(" PC.PROFITCENTERCODE, CC.COSTCENTERCODE, SUM(POSTAMOUNT) POSTAMOUNT, P.DRORCR  FROM POSTINGS P");
		sql.append(" INNER JOIN RMTBRANCHES B ON B.BRANCHCODE = P.POSTBRANCH");
		sql.append(" INNER JOIN RMTCOUNTRYVSPROVINCE S ON S.CPPROVINCE = B.BRANCHPROVINCE");
		sql.append(" INNER JOIN ACCOUNTMAPPING AM ON AM.ACCOUNT = P.ACCOUNT");
		sql.append(" LEFT JOIN PROFITCENTERS PC ON PC.PROFITCENTERID = AM.PROFITCENTERID");
		sql.append(" LEFT JOIN COSTCENTERS CC ON CC.COSTCENTERID = AM.COSTCENTERID");
		sql.append(" WHERE POSTDATE BETWEEN :MONTH_STARTDATE AND :MONTH_ENDDATE AND ENTITYCODE = :ENTITYCODE");
		sql.append(" GROUP BY ENTITYCODE, AM.HOSTACCOUNT, S.BUSINESSAREA,");

		sql.append(" PC.PROFITCENTERCODE, CC.COSTCENTERCODE, P.DRORCR ORDER BY S.BUSINESSAREA");

		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("MONTH_STARTDATE", startDate);
		paramMap.addValue("MONTH_ENDDATE", endDate);
		paramMap.addValue("ENTITYCODE", entityCode);

		return parameterJdbcTemplate.query(sql.toString(), paramMap,
				new ResultSetExtractor<Map<String, TrailBalance>>() {
					@Override
					public Map<String, TrailBalance> extractData(ResultSet rs)
							throws SQLException, DataAccessException {
						Map<String, TrailBalance> map = new LinkedHashMap<>();

						String ZUONR = StringUtils.upperCase(SysParamUtil.getValueAsString("SAP_GL_CATGRY_CODE") + " - "
								+ DateUtil.format(startDate, "MMM yy") + " - PLF");
						String SGTXT = StringUtils.upperCase(SysParamUtil.getValueAsString("SAP_GL_CATGRY_CODE") + " - "
								+ DateUtil.format(startDate, "MMM yy") + " - PLF");

						TrailBalance item = null;
						String key = null;

						String costCenter;
						String profitCenter;
						while (rs.next()) {
							key = "";
							key = key.concat(StringUtils.trimToEmpty(rs.getString("ENTITYCODE")));

							// FIXME ENTITYCODE is hard coded other than loans.
							if (StringUtils.equals("", key)) {
								key = "01";
							}

							key = key.concat(StringUtils.trimToEmpty(rs.getString("HOSTACCOUNT")));
							key = key.concat(StringUtils.trimToEmpty(rs.getString("BUSINESSAREA")));

							costCenter = StringUtils.trimToNull(rs.getString("COSTCENTERCODE"));
							profitCenter = StringUtils.trimToNull(rs.getString("PROFITCENTERCODE"));

							if (costCenter == null) {
								costCenter = parameters.get("PRCTR");
							}

							if (profitCenter == null) {
								profitCenter = parameters.get("KOSTL");
							}

							key = key.concat(costCenter);
							key = key.concat(profitCenter);

							item = map.get(key);

							if (item == null) {
								item = new TrailBalance();
								item.setLink(0);
								item.setEntity(rs.getString("ENTITYCODE"));
								item.setLedgerAccount(rs.getString("HOSTACCOUNT"));
								item.setBusinessArea(rs.getString("BUSINESSAREA"));
								item.setProfitCenter(profitCenter);
								item.setCostCenter(costCenter);

								item.setUmskz(parameters.get("UMSKZ"));
								item.setBusinessUnit(parameters.get("GSBER"));
								item.setNarration1(ZUONR);
								item.setNarration2(SGTXT);

								map.put(key, item);
							}

							if ("C".equals(rs.getObject("DRORCR"))) {
								item.setCreditAmount(getAmount(rs, "POSTAMOUNT"));
							} else {
								item.setDebitAmount(getAmount(rs, "POSTAMOUNT"));
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
		sql.append(" IN (:HKONT, :BLART, :BUKRS, :BUPLA, :UMSKZ, :GSBER, :PRCTR, :KOSTL,");
		sql.append(" :APP_DFT_CURR, :SAPGL_TRAN_RECORD_COUNT)");

		paramMap.addValue("HKONT", "HKONT");
		paramMap.addValue("BLART", "BLART");
		paramMap.addValue("BUKRS", "BUKRS");
		paramMap.addValue("BUPLA", "BUPLA");
		paramMap.addValue("UMSKZ", "UMSKZ");
		paramMap.addValue("GSBER", "GSBER");
		paramMap.addValue("PRCTR", "PRCTR");
		paramMap.addValue("KOSTL", "KOSTL");
		paramMap.addValue("APP_DFT_CURR", "APP_DFT_CURR");
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

		sql.append(" insert into TRANSACTION_DETAIL_REPORT_TEMP(ID, ENTITY, LINK, BSCHL, HKONT, UMSKZ,");
		sql.append(" WRBTR, GSBER, BUPLA, KOSTL, PRCTR, ZUONR, SGTXT)");
		sql.append(" VALUES(:Id, :Entity, :Link, :TransactionAmountType, :LedgerAccount, :Umskz,");
		sql.append(" :TransactionAmount, :BusinessUnit, :BusinessArea, :CostCenter, :ProfitCenter,");
		sql.append(" :Narration1, :Narration1)");

		Map<String, List<TrailBalance>> entityMap = new HashMap<>();
		List<TrailBalance> transactions = null;
		for (TrailBalance item : list) {
			SAP_GL_STATUS.setProcessedRecords(processedCount++);
			item.setTransactionAmount(item.getCreditAmount().subtract(item.getDebitAmount()));

			if (item.getTransactionAmount().compareTo(BigDecimal.ZERO) < 0) {
				item.setTransactionAmount(BigDecimal.ZERO.subtract(item.getTransactionAmount()));
				item.setTransactionAmountType(40);
			} else {
				item.setTransactionAmountType(50);
			}

			transactions = entityMap.get(item.getEntity());

			if (transactions == null) {
				transactions = new ArrayList<>();
				entityMap.put(item.getEntity(), transactions);
			}

			transactions.add(item);
		}

		List<TrailBalance> nonZeorList = new ArrayList<>();
		for (List<TrailBalance> trailBalances : entityMap.values()) {
			int i = 0;
			for (TrailBalance tb : trailBalances) {
				if (BigDecimal.ZERO.compareTo(tb.getTransactionAmount()) < 0) {
					tb.setId(++i);
					nonZeorList.add(tb);
				}
			}
		}

		parameterJdbcTemplate.batchUpdate(sql.toString(), SqlParameterSourceUtils.createBatch(nonZeorList.toArray()));
	}

	private void clearTables() {
		logger.info("Clearing GL Tables..");
		jdbcTemplate.execute("DELETE FROM TRANSACTION_SUMMARY_REPORT");
		jdbcTemplate.execute("DELETE FROM TRANSACTION_DETAIL_REPORT");
		jdbcTemplate.execute("DELETE FROM TRANSACTION_DETAIL_REPORT_TEMP");

		// jdbcTemplate.execute("alter table TRANSACTION_DETAIL_REPORT modify ID generated as identity (start with 1)");
		jdbcTemplate.execute("ALTER SEQUENCE seq_transaction_detail_report RESTART WITH 1");

		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("START_DATE", startDate);
		paramMap.addValue("END_DATE", endDate);
		paramMap.addValue("GL_TRANSACTION_EXPORT", "GL_TRANSACTION_EXPORT");
		paramMap.addValue("GL_TRANSACTION_SUMMARY_EXPORT", "GL_TRANSACTION_SUMMARY_EXPORT");

		StringBuilder sql = new StringBuilder();
		sql = sql.append("Delete from DATA_ENGINE_LOG where StatusId IN (");
		sql.append("SELECT ID FROM DATA_ENGINE_STATUS where ValueDate BETWEEN :START_DATE AND :END_DATE AND NAME IN(");
		sql.append(":GL_TRANSACTION_EXPORT, :GL_TRANSACTION_SUMMARY_EXPORT))");
		parameterJdbcTemplate.update(sql.toString(), paramMap);

		sql = new StringBuilder();
		sql = sql.append("Delete from DATA_ENGINE_STATUS");
		sql.append(" where ValueDate BETWEEN :START_DATE AND :END_DATE AND NAME IN(");
		sql.append(":GL_TRANSACTION_EXPORT, :GL_TRANSACTION_SUMMARY_EXPORT)");
		parameterJdbcTemplate.update(sql.toString(), paramMap);
	}

	private int saveTranactions(int fromRange, int toRange, String entity) {
		MapSqlParameterSource parameterSource;

		StringBuilder sql = new StringBuilder();
		sql.append(" INSERT INTO TRANSACTION_DETAIL_REPORT (ENTITY, LINK, BSCHL, HKONT, UMSKZ, WRBTR, GSBER, BUPLA,");
		sql.append(" KOSTL, PRCTR, ZUONR, SGTXT)");
		sql.append(" SELECT ENTITY, LINK, BSCHL, HKONT, UMSKZ, WRBTR, GSBER, BUPLA, KOSTL, PRCTR, ZUONR, SGTXT");
		sql.append(" FROM TRANSACTION_DETAIL_REPORT_TEMP");
		sql.append(" WHERE ENTITY = :ENTITY AND ID >=:FROM_RANGE AND ID <=:TO_RANGE");

		parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("ENTITY", entity);
		parameterSource.addValue("FROM_RANGE", fromRange);
		parameterSource.addValue("TO_RANGE", toRange);

		try {
			return parameterJdbcTemplate.update(sql.toString(), parameterSource);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		return 0;
	}

	private void update(int page) {
		MapSqlParameterSource paramMap;

		StringBuilder sql = new StringBuilder();

		sql.append(" UPDATE TRANSACTION_DETAIL_REPORT SET LINK = :LINK");
		sql.append(" WHERE LINK =0");

		paramMap = new MapSqlParameterSource();
		paramMap.addValue("LINK", page);

		try {
			parameterJdbcTemplate.update(sql.toString(), paramMap);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
	}

	private void exportSummaryReport() {
		logger.info("Generating Transaction summary report ..");
		String query = "select count(*) count, ENTITY from TRANSACTION_SUMMARY_REPORT GROUP BY ENTITY";

		jdbcTemplate.query(query, new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				try {
					Map<String, Object> filterMap = new HashMap<>();
					Map<String, Object> parameterMap = new HashMap<>();
					DataEngineExport export = new DataEngineExport(dataSource, userId, App.DATABASE.name(), true,
							appDate);
					// parameterMap.put("ENTITY", entityCode);
					// filterMap.put("ENTITY", entityCode);

					parameterMap.put("ENTITY_CODE", entityCode + "_"
							+ SysParamUtil.getValueAsString("SAP_GL_CATGRY_CODE") + "_SAP_GL_REPORT_HDR_PA");

					export.setParameterMap(parameterMap);
					export.setFilterMap(filterMap);

					export.exportData("GL_TRANSACTION_SUMMARY_EXPORT");
				} catch (Exception e) {
					throw new SQLException();
				}
			}
		});
	}

	private void exportTransactionReport() {
		logger.info("Generating Transaction detail report ..");
		String query = "select count(*) count, ENTITY from TRANSACTION_DETAIL_REPORT_TEMP GROUP BY ENTITY";

		jdbcTemplate.query(query, new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				try {
					Map<String, Object> filterMap = new HashMap<>();
					Map<String, Object> parameterMap = new HashMap<>();
					SAP_GL_STATUS.setName("GL_TRANSACTION_EXPORT");
					DataEngineExport export = new DataEngineExport(dataSource, userId, App.DATABASE.name(), true,
							appDate);
					// parameterMap.put("ENTITY", entityCode);
					filterMap.put("ENTITY", entityCode);

					parameterMap.put("ENTITY_CODE", entityCode + "_"
							+ SysParamUtil.getValueAsString("SAP_GL_CATGRY_CODE") + "_SAP_GL_REPORT_LINE_PA");

					export.setParameterMap(parameterMap);
					export.setFilterMap(filterMap);

					export.exportData("GL_TRANSACTION_EXPORT");
				} catch (Exception e) {
					throw new SQLException();
				}
			}
		});
	}

	private void groupTransactions() throws Exception {
		int pageSize = Integer.parseInt(parameters.get("SAPGL_TRAN_RECORD_COUNT"));

		String query = "select count(*) count, ENTITY from TRANSACTION_DETAIL_REPORT_TEMP GROUP BY ENTITY";

		jdbcTemplate.query(query, new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				int totalTransactions = rs.getInt("count");

				int pages = totalTransactions / pageSize;
				if (pages == 0) {
					pages = 1;
				}

				int fromRange = 1;
				int toRange = pageSize;
				int pageItr = 0;
				int mainRecords = 0;
				boolean pagesInserted = false;
				for (int page = 1; page <= pages; page++) {
					pageItr = page;
					mainRecords = mainRecords + saveTranactions(fromRange, toRange, rs.getString("ENTITY"));
					pagesInserted = true;
					fromRange = toRange + 1;
					toRange = toRange + pageSize;
					update(pageItr);

					if (pagesInserted && totalTransactions > mainRecords) {
						saveTransactionSummary(pageItr, rs.getString("ENTITY"));
						pagesInserted = false;
					}
				}

				if (totalTransactions > mainRecords) {
					mainRecords = mainRecords + saveTranactions(fromRange, toRange, rs.getString("ENTITY"));
					pagesInserted = true;
					fromRange = toRange + 1;
					toRange = toRange + pageSize;
					pageItr = pageItr + 1;
					update(pageItr);
				}
			}

		});
	}

	private Map<Integer, BigDecimal> getSummaryAmounts(int pageItr, String entity) {
		Map<Integer, BigDecimal> map = new HashMap<Integer, BigDecimal>();

		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT BSCHL, SUM(WRBTR) WRBTR FROM TRANSACTION_DETAIL_REPORT");
		sql.append(" WHERE ENTITY = ? AND LINK = ? GROUP BY BSCHL");

		jdbcTemplate.query(sql.toString(), new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				map.put(rs.getInt("BSCHL"), rs.getBigDecimal("WRBTR"));
			}
		}, entity, pageItr);

		return map;
	}

	private void saveTransactionSummary(int pageItr, String entity) throws SQLException {
		Map<Integer, BigDecimal> map = getSummaryAmounts(pageItr, entity);

		BigDecimal summaryAmount = BigDecimal.ZERO;
		BigDecimal debitAmount = BigDecimal.ZERO;
		BigDecimal creditAmount = BigDecimal.ZERO;

		if (map.containsKey(40)) {
			debitAmount = map.get(40);
		}

		if (map.containsKey(50)) {
			creditAmount = map.get(50);
		}

		summaryAmount = debitAmount.subtract(creditAmount);

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
		sql.append(" INSERT INTO TRANSACTION_DETAIL_REPORT(ENTITY, LINK, BSCHL, HKONT, UMSKZ, WRBTR, GSBER,");
		sql.append(" BUPLA, KOSTL, PRCTR, ZUONR, SGTXT) SELECT");
		sql.append(" :ENTITY,");
		sql.append(" :LINK,");
		sql.append(" :BSCHL,");
		sql.append(" :HKONT,");
		sql.append(" :UMSKZ,");
		sql.append(" :WRBTR,");
		sql.append(" :GSBER,");
		sql.append(" BUPLA,");
		sql.append(" :KOSTL,");
		sql.append(" :PRCTR,");
		sql.append(" ZUONR,");
		sql.append(" SGTXT");
		sql.append(" FROM TRANSACTION_DETAIL_REPORT");
		sql.append(" WHERE ID = (select MAX(ID) from TRANSACTION_DETAIL_REPORT)");

		parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("ENTITY", entity);
		parameterSource.addValue("LINK", pageItr);
		parameterSource.addValue("BSCHL", BSCHL);
		parameterSource.addValue("HKONT", parameters.get("HKONT"));
		parameterSource.addValue("UMSKZ", parameters.get("UMSKZ"));
		parameterSource.addValue("WRBTR", WRBTR);
		parameterSource.addValue("GSBER", parameters.get("GSBER"));
		parameterSource.addValue("KOSTL", parameters.get("KOSTL"));
		parameterSource.addValue("PRCTR", parameters.get("PRCTR"));

		try {
			parameterJdbcTemplate.update(sql.toString(), parameterSource);

			if ("40".equals(BSCHL)) {
				BSCHL = "50";
			} else {
				BSCHL = "40";
			}
			parameterSource.addValue("BSCHL", BSCHL);
			parameterSource.addValue("LINK", pageItr + 1);

			parameterJdbcTemplate.update(sql.toString(), parameterSource);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw new SQLException("Unable to insert the summary records for the page " + pageItr);
		}
	}

	private int getFinancialMonth() {
		int financialMonth = 0;
		int month = DateUtil.getMonth(endDate);

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

	private void saveTransactionSummary() throws Exception {
		MapSqlParameterSource paramMap;

		StringBuilder sql = new StringBuilder();
		sql.append(" INSERT INTO TRANSACTION_SUMMARY_REPORT SELECT");
		sql.append("  DISTINCT ENTITY, LINK, ");
		if (App.DATABASE == Database.POSTGRES) {
			sql.append("to_date(:BLDAT,'yyyy-MM-dd'),");
		} else {
			sql.append(" :BLDAT,");
		}

		sql.append(" :BLART,");
		sql.append(" :BUKRS,");

		if (App.DATABASE == Database.POSTGRES) {
			sql.append("to_date(:BUDAT,'yyyy-MM-dd'),");
		} else {
			sql.append(" :BUDAT,");
		}

		sql.append(" :MONAT,");
		sql.append(" :APP_DFT_CURR,");
		sql.append(" :XBLNR,");
		sql.append(" :BKTXT");
		sql.append(" FROM TRANSACTION_DETAIL_REPORT");

		paramMap = new MapSqlParameterSource();
		paramMap.addValue("BLDAT", endDate);
		paramMap.addValue("BLART", parameters.get("BLART"));
		paramMap.addValue("BUKRS", parameters.get("BUKRS"));
		paramMap.addValue("BUDAT", endDate);
		paramMap.addValue("MONAT", getFinancialMonth());
		paramMap.addValue("APP_DFT_CURR", parameters.get("APP_DFT_CURR"));
		paramMap.addValue("XBLNR", StringUtils.upperCase(SysParamUtil.getValueAsString("SAP_GL_CATGRY_CODE") + " - "
				+ DateUtil.format(startDate, "MMM yy") + " - PLF"));
		paramMap.addValue("BKTXT", StringUtils.upperCase(SysParamUtil.getValueAsString("SAP_GL_CATGRY_CODE") + " - "
				+ DateUtil.format(startDate, "MMM yy") + " - PLF"));

		try {
			parameterJdbcTemplate.update(sql.toString(), paramMap);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw new Exception("Unable to prpare the transaction summary report.");
		}
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
