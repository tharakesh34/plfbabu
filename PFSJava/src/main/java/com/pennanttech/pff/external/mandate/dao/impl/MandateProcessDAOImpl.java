package com.pennanttech.pff.external.mandate.dao.impl;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.util.MandateConstants;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.core.util.QueryUtil;
import com.pennanttech.pff.external.mandate.dao.MandateProcessDAO;

public class MandateProcessDAOImpl extends SequenceDao<Object> implements MandateProcessDAO {
	protected final Logger logger = LogManager.getLogger(getClass());

	@Override
	public long saveMandateRequests(List<Long> mandateIds) {
		long processId = getNextValue("SEQ_MANDATES_REQUEST_PROCESS");

		final Map<String, Integer> bankCodeSeq = getCountByProcessed();

		List<Long> mandateSet = new ArrayList<>();
		Long[] result = new Long[mandateSet.size()];
		for (Long mandateId : mandateIds) {
			mandateSet.add(mandateId);
			result = mandateSet.toArray(result);
			if (result.length > 499) {
				save(processId, result, bankCodeSeq);
				mandateSet.clear();
			}
		}

		if (!(result == null)) {
			save(processId, result, bankCodeSeq);
			mandateSet.clear();
		}
		return processId;
	}

	private void save(long processId, Long[] mandateIds, final Map<String, Integer> bankCodeSeq) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource paramMap;
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT");
		sql.append(" MANDATEID,");
		sql.append(" BANKCODE BANK_CODE,");
		sql.append(" BANKNAME BANK_NAME,");
		sql.append(" BRANCHCODE BRANCH_CODE,");
		sql.append(" BRANCHDESC BRANCH_NAME,");
		sql.append(" ADDOFBRANCH BRANCH_ADDRESS,");
		sql.append(" CUSTCIF,");
		sql.append(" CUSTSHRTNAME CUSTOMER_NAME,");
		sql.append(" CustCoreBank AUXILIARY_FIELD3,");
		sql.append(" PHONENUMBER CUSTOMER_PHONE,");
		sql.append(" CUSTEMAIL CUSTOMER_EMAIL,");
		sql.append(" FINTYPE,");
		sql.append(" FINID,");
		sql.append(" FINREFERENCE,");
		sql.append(" CUST_EMI,");
		sql.append(" EMI,");
		sql.append(" OPENMANDATE OPENFLAG,");
		sql.append(" ACCNUMBER ACCT_NUMBER,");
		sql.append(" ACCTYPE ACCT_TYPE,");
		sql.append(" ACCHOLDERNAME ACCT_HOLDER_NAME,");
		sql.append(" MICR MICR_CODE,");
		sql.append(" IFSC IFSC_CODE,");
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
		sql.append(" NUMBEROFTERMS NUMBER_OF_TERMS,");
		sql.append(" PERIODICITY FREQUENCY,");
		sql.append(" STATUS,");
		sql.append(" PARTNERBANKNAME PARTNER_BANK,");
		sql.append(" BRANCHIFSCCODE PARTNER_BANK_IFSC,");
		sql.append(" LASTMNTON REGISTERED_DATE,");
		sql.append(" BANK_BRANCH_NAME,");
		sql.append(" UTILITYCODE UTILITY_CODE,");
		sql.append(" SPONSORBANKCODE SPONSOR_BANK,");
		sql.append(" ENTITYDESC ENTITY_CODE,");
		sql.append(" BANK_BRANCH_NAME,");
		sql.append(" DOCUMENTNAME");
		sql.append(" FROM INT_MANDATE_REQUEST_VIEW");
		sql.append(" WHERE MANDATEID IN (:MANDATEID)");
		paramMap = new MapSqlParameterSource();
		paramMap.addValue("MANDATEID", Arrays.asList(mandateIds));
		final ColumnMapRowMapper rowMapper = new ColumnMapRowMapper();

		try {
			jdbcTemplate.query(sql.toString(), paramMap, new RowMapper<Long>() {
				@Override
				public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
					Map<String, Object> rowMap = rowMapper.mapRow(rs, rowNum);
					String bankCode = null;

					if (rowMap.get("BANK_CODE") != null) {
						bankCode = rowMap.get("BANK_CODE").toString();
					}

					rowMap.put("BATCH_ID", 0);
					rowMap.put("PROCESS_ID", processId);
					rowMap.put("BANK_SEQ", getSequence(bankCode, bankCodeSeq));
					rowMap.put("EXTRACTION_DATE", DateUtil.getDatePart(SysParamUtil.getAppDate()));

					String frequency = null;

					if (rowMap.get("FREQUENCY") != null) {
						String temp = rowMap.get("FREQUENCY").toString();
						if (temp.contains("D")) {
							frequency = "DIAL";
						} else if (temp.contains("M")) {
							frequency = "MNTH";
						} else if (temp.contains("W")) {
							frequency = "Week";
						} else if (temp.contains("Q")) {
							frequency = "QURT";
						} else {
							frequency = "";
						}
						rowMap.put("FREQUENCY", frequency);

					}

					String appId = null;
					String finReference = StringUtils.trimToNull(rs.getString("FINREFERENCE"));

					if (finReference != null) {
						appId = StringUtils.substring(finReference, finReference.length() - 7, finReference.length());
						appId = StringUtils.trim(appId);
						if (StringUtils.isNumeric(appId)) {
							rowMap.put("APPLICATION_NUMBER", Integer.parseInt(appId));
						}
					} else {
						rowMap.put("APPLICATION_NUMBER", null);
					}

					BigDecimal upperLimit = (BigDecimal) rowMap.get("UPPER_LIMIT");
					BigDecimal custEmi = (BigDecimal) rowMap.get("CUST_EMI");

					if (upperLimit == null) {
						upperLimit = BigDecimal.ZERO;
					} else {
						upperLimit = upperLimit.divide(new BigDecimal(100));
						rowMap.put("UPPER_LIMIT", upperLimit);
					}

					if (custEmi == null) {
						custEmi = BigDecimal.ZERO;
					}

					if (StringUtils.trimToNull((String) String.valueOf(rowMap.get("FINREFERENCE"))) == null) {

						if (custEmi.compareTo(upperLimit) > 0) {
							rowMap.put("EMI", upperLimit);
							rowMap.put("DEBIT_AMOUNT", upperLimit);
						} else {
							rowMap.put("EMI", custEmi);
							rowMap.put("DEBIT_AMOUNT", custEmi);
						}

						Date startDate = (Date) rowMap.get("START_DATE");
						Date firstDueDate = (Date) rowMap.get("FIRSTDUEDATE");
						Date endDate = DateUtil.addMonths(startDate, 240);

						rowMap.put("EFFECTIVE_DATE", startDate);
						rowMap.put("EMI_ENDDATE", endDate);

						if (firstDueDate == null) {
							rowMap.put("EFFECTIVE_DATE", startDate);
						}
					}

					rowMap.remove("CCYMINORCCYUNITS");
					rowMap.remove("CUST_EMI");
					rowMap.remove("FIRSTDUEDATE");

					long id = insertData(rowMap);

					Object obj = rowMap.get("mandateid");

					String mandateId = null;

					if (obj != null) {
						mandateId = obj.toString();
					}

					logMandateHistory(new Long(mandateId), id);
					return id;
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			throw new AppException();
		}

		logger.debug(Literal.ENTERING);
	}

	private Map<String, Integer> getCountByProcessed() {
		final Map<String, Integer> bankCodeMap = new HashMap<>();
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		StringBuilder sql = new StringBuilder();

		sql.append("Select BANK_CODE, count(*) From MANDATE_REQUESTS");
		sql.append(" Where EXTRACTION_DATE =:EXTRACTION_DATE");
		sql.append(" GROUP BY BANK_CODE");

		paramMap.addValue("EXTRACTION_DATE", SysParamUtil.getAppValueDate());

		try {
			return jdbcTemplate.query(sql.toString(), paramMap, new ResultSetExtractor<Map<String, Integer>>() {
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
		}
		return bankCodeMap;
	}

	private String getSequence(String bankCode, Map<String, Integer> bankCodeSeq) {
		int seq = 0;
		if (bankCode == null || bankCodeSeq.get(bankCode) == null) {
			bankCodeSeq.put(bankCode, 0);
		} else {
			seq = bankCodeSeq.get(bankCode);
		}

		seq = seq + 1;
		bankCodeSeq.put(bankCode, seq);

		return StringUtils.trimToEmpty(bankCode) + "-" + seq;
	}

	private long insertData(Map<String, Object> rowMap) {
		String sql = QueryUtil.getInsertQuery(rowMap.keySet(), "MANDATE_REQUESTS");
		final KeyHolder keyHolder = new GeneratedKeyHolder();
		try {
			jdbcTemplate.update(sql, getMapSqlParameterSource(rowMap), keyHolder, new String[] { "id" });
		} catch (Exception e) {
			throw new AppException("");
		}
		return keyHolder.getKey().longValue();
	}

	private void logMandateHistory(Long mandateId, long requestId) {
		logger.debug(Literal.ENTERING);
		MapSqlParameterSource paramMap = new MapSqlParameterSource();

		StringBuilder sql = new StringBuilder("Insert Into MandatesStatus");
		sql.append(" (mandateID, status, reason, changeDate, fileID)");
		sql.append(" Values(:mandateID, :STATUS, :REASON, :changeDate, :fileID)");

		paramMap.addValue("mandateID", mandateId);

		paramMap.addValue("STATUS", "AC");
		paramMap.addValue("REASON", null);
		paramMap.addValue("changeDate", SysParamUtil.getAppDate());
		paramMap.addValue("fileID", requestId);

		this.jdbcTemplate.update(sql.toString(), paramMap);
		logger.debug(Literal.LEAVING);
	}

	public static MapSqlParameterSource getMapSqlParameterSource(Map<String, Object> map) {
		MapSqlParameterSource parmMap = new MapSqlParameterSource();

		for (Entry<String, Object> entry : map.entrySet()) {
			parmMap.addValue(entry.getKey(), entry.getValue());
		}

		return parmMap;
	}

	@Override
	public List<String> getEntityCodes() {
		String sql = "Select EntityCode from Entity";
		return jdbcOperations.queryForList(sql, new Object[] {}, String.class);
	}

	@Override
	public void deleteMandateRequests(List<Long> mandateIds) {
		List<Long> mandateSet = new ArrayList<>();
		Long[] result = new Long[mandateSet.size()];
		for (Long mandateId : mandateIds) {
			mandateSet.add(mandateId);
			result = mandateSet.toArray(result);
			if (result.length > 499) {
				delete(result);
				mandateSet.clear();
			}
		}

		if (!(result == null)) {
			delete(result);
			mandateSet.clear();
		}
	}

	private void delete(Long[] result) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("DELETE FROM MANDATE_REQUESTS ");
		sql.append("WHERE MANDATEID IN (:MANDATEID)");

		logger.debug("Query--->" + sql.toString());

		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("MANDATEID", Arrays.asList(result));

		this.jdbcTemplate.update(sql.toString(), paramMap);

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void deleteMandateStatus(List<Long> mandateIds) {

		List<Long> mandateSet = new ArrayList<>();
		Long[] result = new Long[mandateSet.size()];
		for (Long mandateId : mandateIds) {
			mandateSet.add(mandateId);
			result = mandateSet.toArray(result);
			if (result.length > 499) {
				deleteStatus(result);
				mandateSet.clear();
			}
		}

		if (!(result == null)) {
			deleteStatus(result);
			mandateSet.clear();
		}
	}

	private void deleteStatus(Long[] result) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("DELETE FROM MANDATESSTATUS ");
		sql.append("WHERE MANDATEID IN (:MANDATEID) ");
		sql.append("AND STATUS = :AC");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("MANDATEID", Arrays.asList(result));
		paramMap.addValue("AC", MandateConstants.STATUS_AWAITCON);

		this.jdbcTemplate.update(sql.toString(), paramMap);

		logger.debug(Literal.LEAVING);
	}

	@Override
	public List<Long> getMandateList(String entityCode) {
		String sql = "Select MandateID from MANDATES Where Entitycode = ?";

		logger.trace(Literal.SQL + sql.toString());

		return jdbcOperations.queryForList(sql.toString(), new Object[] { entityCode }, Long.class);
	}

	@Override
	public List<Long> getMandateList(String entityCode, String partnerBankCode) {
		StringBuilder sql = new StringBuilder();
		sql.append("Select MandateID");
		sql.append(" from MANDATES M");
		sql.append(" INNER JOIN PARTNERBANKS PB ON PB.PARTNERBANKID = M.PARTNERBANKID");
		sql.append(" Where Entitycode = ? and PartnerBankCode = ?");

		logger.trace(Literal.SQL + sql.toString());

		return jdbcOperations.queryForList(sql.toString(), new Object[] { entityCode, partnerBankCode }, Long.class);
	}

	@Override
	public List<String> getPartnerBankCodeByEntity(String entityCode) {
		String sql = "Select PartnerbankCode from PartnerBanks Where Entity = ?";

		logger.trace(Literal.SQL + sql);

		return jdbcOperations.queryForList(sql, new Object[] { entityCode }, String.class);
	}

}
