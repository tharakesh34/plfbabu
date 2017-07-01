package com.pennanttech.bajaj.services;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.pennanttech.dataengine.DataEngineExport;
import com.pennanttech.pff.core.App;
import com.pennanttech.pff.core.Literal;
import com.pennanttech.pff.core.services.MandateRequestService;
import com.pennanttech.pff.core.util.DateUtil;
import com.pennanttech.pff.core.util.QueryUtil;

public class MandateRequestServiceImpl extends BajajService implements MandateRequestService {
	private final Logger	logger	= Logger.getLogger(getClass());

	@Override
	public void sendReqest(Object... params) throws Exception {
		@SuppressWarnings("unchecked")
		List<Long> mandateIdList = (List<Long>) params[0];
		Date fromDate = (Date) params[1];
		Date toDate = (Date) params[2];
		long userId = (Long) params[3];
		String userName = (String) params[4];
		String selectedBranchs = (String) params[5];

		String[] mandateIds = new String[mandateIdList.size()];

		int i = 0;
		for (Long mandateId : mandateIdList) {
			mandateIds[i++] = String.valueOf(mandateId);
		}

		List<String> mandates = prepareRequest(mandateIds);

		if (mandates == null || mandates.isEmpty()) {
			return;
		}

		Map<String, Object> filterMap = new HashMap<>();
		Map<String, Object> parameterMap = new HashMap<>();
		filterMap.put("ID", mandates);
		filterMap.put("FROMDATE", fromDate);
		filterMap.put("TODATE", toDate);

		if (StringUtils.isNotBlank(selectedBranchs)) {
			filterMap.put("BRANCHCODE", Arrays.asList(selectedBranchs.split(",")));
		}

		parameterMap.put("USER_NAME", userName);
		DataEngineExport dataEngine = null;
		dataEngine = new DataEngineExport(dataSource, userId, App.DATABASE.name(), true, getValueDate());

		dataEngine.setFilterMap(filterMap);
		dataEngine.setParameterMap(parameterMap);
		dataEngine.setUserName(userName);
		dataEngine.setValueDate(getValueDate());
		dataEngine.exportData("MANDATES_EXPORT");
	}

	private List<String> prepareRequest(String[] mandateIds) throws Exception {
		logger.debug(Literal.ENTERING);
		final Map<String, Integer> bankCodeSeq = getCountByProcessed();

		MapSqlParameterSource paramMap;
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT");
		sql.append(" MANDATEID,");
		sql.append(" BANKCODE BANK_CODE,");
		sql.append(" BANKNAME BANK_NAME,");
		sql.append(" BRANCHDESC BRANCH_NAME,");
		sql.append(" CUSTCIF,");
		sql.append(" CUSTSHRTNAME CUSTOMER_NAME,");
		sql.append(" FINTYPE,");
		sql.append(" FINREFERENCE,");
		sql.append(" CUST_EMI,");
		sql.append(" EMI,");
		sql.append(" OPENMANDATE OPENFLAG,");
		sql.append(" ACCNUMBER ACCT_NUMBER,");
		sql.append(" ACCTYPE ACCT_TYPE,");
		sql.append(" ACCHOLDERNAME ACCT_HOLDER_NAME,");
		sql.append(" MICR MICR_CODE,");
		sql.append(" FIRSTDUEDATE EFFECTIVE_DATE,");
		sql.append(" EMIENDDATE EMI_ENDDATE,");
		sql.append(" EXPIRYDATE OPEN_ENDDATE,");
		sql.append(" MAXLIMIT UPPER_LIMIT,");
		sql.append(" CCYMINORCCYUNITS,");
		sql.append(" DEBITAMOUNT DEBIT_AMOUNT,");
		sql.append(" STARTDATE START_DATE,");
		sql.append(" EXPIRYDATE END_DATE,");
		sql.append(" APPLICATIONNO APPLICATION_NUMBER,");
		sql.append(" MANDATETYPE MANDATE_TYPE,");
		sql.append(" STATUS");
		sql.append(" FROM INT_MANDATE_REQUEST_VIEW");
		sql.append(" WHERE MANDATEID IN (:MANDATEID)");

		paramMap = new MapSqlParameterSource();
		paramMap.addValue("MANDATEID", Arrays.asList(mandateIds));

		final ColumnMapRowMapper rowMapper = new ColumnMapRowMapper();
		try {
			return namedJdbcTemplate.query(sql.toString(), paramMap, new RowMapper<String>() {
				@Override
				public String mapRow(ResultSet rs, int rowNum) throws SQLException {
					String id = null;
					Map<String, Object> rowMap = rowMapper.mapRow(rs, rowNum);
					rowMap.put("BATCH_ID", 0);
					rowMap.put("BANK_SEQ", getSequence((String) rowMap.get("BANK_CODE"), bankCodeSeq));
					rowMap.put("EXTRACTION_DATE", getAppDate());

					String appId = null;
					String finReference = StringUtils.trimToNull(rs.getString("FINREFERENCE"));
					if (finReference != null) {
						appId = StringUtils.substring(finReference, finReference.length() - 8, finReference.length());
						appId = StringUtils.trim(appId);
						rowMap.put("APPLICATION_NUMBER", Integer.parseInt(appId));
					} else {
						rowMap.put("APPLICATION_NUMBER", null);

					}

					BigDecimal UPPER_LIMIT = (BigDecimal) rowMap.get("UPPER_LIMIT");
					BigDecimal CUST_EMI = (BigDecimal) rowMap.get("CUST_EMI");

					if (UPPER_LIMIT == null) {
						UPPER_LIMIT = BigDecimal.ZERO;
					}

					if (CUST_EMI == null) {
						CUST_EMI = BigDecimal.ZERO;
					}

					if (StringUtils.trimToNull((String) rowMap.get("FINREFERENCE")) == null) {
						if (CUST_EMI.compareTo(UPPER_LIMIT) > 0) {
							rowMap.put("EMI", UPPER_LIMIT);
							rowMap.put("DEBIT_AMOUNT", UPPER_LIMIT);
						} else {
							rowMap.put("EMI", CUST_EMI);
							rowMap.put("DEBIT_AMOUNT", CUST_EMI);
						}
						
						Date startDate = (Date) rowMap.get("START_DATE");
						Date firstDueDate = (Date) rowMap.get("FIRSTDUEDATE");
						Date endDate = DateUtil.addMonths(startDate, 240);
						
						rowMap.put("EFFECTIVE_DATE", startDate);
						rowMap.put("EMI_ENDDATE", endDate);
						
						if(firstDueDate == null) {
							rowMap.put("EFFECTIVE_DATE", startDate);
						}
					}

					rowMap.remove("CCYMINORCCYUNITS");
					rowMap.remove("CUST_EMI");
					rowMap.remove("FIRSTDUEDATE");
					
					id = String.valueOf(insertData(rowMap));
					logMandateHistory((BigDecimal)rowMap.get("MANDATEID"), id);
					rowMap = null;
					return id;
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(Literal.EXCEPTION, e);
		} finally {
			paramMap = null;
			sql = null;
		}

		logger.debug(Literal.ENTERING);
		return null;
	}

	private long insertData(Map<String, Object> rowMap) {
		String sql = QueryUtil.getInsertQuery(rowMap.keySet(), "MANDATE_REQUESTS");
		final KeyHolder keyHolder = new GeneratedKeyHolder();
		try {
			namedJdbcTemplate.update(sql, getMapSqlParameterSource(rowMap), keyHolder, new String[] { "ID" });
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		return keyHolder.getKey().longValue();
	}

	private String getSequence(String bankCode, Map<String, Integer> bankCodeSeq) {
		int seq = 0;
		if (bankCodeSeq.get(bankCode) == null) {
			bankCodeSeq.put(bankCode, 0);
		} else {
			seq = bankCodeSeq.get(bankCode);
		}

		seq = seq + 1;
		bankCodeSeq.put(bankCode, seq);

		return StringUtils.trimToEmpty(bankCode) + "-" + seq;
	}

	private Map<String, Integer> getCountByProcessed() {
		final Map<String, Integer> bankCodeMap = new HashMap<>();
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		StringBuilder sql = new StringBuilder();

		sql.append(" Select BANK_CODE, count(*) From MANDATE_REQUESTS");
		sql.append(" Where EXTRACTION_DATE =:EXTRACTION_DATE");
		sql.append(" GROUP BY BANK_CODE");

		paramMap.addValue("EXTRACTION_DATE", getValueDate());

		try {
			return namedJdbcTemplate.query(sql.toString(), paramMap, new ResultSetExtractor<Map<String, Integer>>() {
				@Override
				public Map<String, Integer> extractData(ResultSet rs) throws SQLException, DataAccessException {
					while (rs.next()) {
						bankCodeMap.put(rs.getString(1), rs.getInt(2));
					}
					return bankCodeMap;
				}

			});
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		} finally {
			paramMap = null;
			sql = null;
		}
		return bankCodeMap;
	}
	
	private void logMandateHistory(BigDecimal mandateId, String requestId) {
		logger.debug(Literal.ENTERING);
		MapSqlParameterSource paramMap = new MapSqlParameterSource();

		StringBuilder sql = new StringBuilder("Insert Into MandatesStatus");
		sql.append(" (mandateID, status, reason, changeDate, fileID)");
		sql.append(" Values(:mandateID, :STATUS, :REASON, :changeDate,:fileID)");

		paramMap.addValue("mandateID", mandateId);

		paramMap.addValue("STATUS", "AC");
		paramMap.addValue("REASON", null);
		paramMap.addValue("changeDate", getAppDate());
		paramMap.addValue("fileID", requestId);

		this.namedJdbcTemplate.update(sql.toString(), paramMap);
		logger.debug(Literal.LEAVING);
	}

}
