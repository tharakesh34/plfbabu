package com.pennant.backend.dao.limit.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.limit.LimitTransactionDetailsDAO;
import com.pennant.backend.model.limit.LimitTransactionDetail;
import com.pennant.backend.util.LimitConstants;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class LimitTransactionDetailsDAOImpl extends SequenceDao<LimitTransactionDetail>
		implements LimitTransactionDetailsDAO {
	private static Logger logger = Logger.getLogger(LimitTransactionDetailsDAOImpl.class);

	public LimitTransactionDetailsDAOImpl() {
		super();
	}

	@Override
	public List<LimitTransactionDetail> getLimitTranDetails(String code, String ref, long headerId) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select TransactionId, ReferenceCode");
		sql.append(", ReferenceNumber, HeaderId, TransactionType, TransactionDate, SchSeq");
		sql.append(", OverrideFlag, TransactionAmount, TransactionCurrency, LimitCurrency, LimitAmount");
		sql.append(", CreatedBy, CreatedOn, LastMntBy, LastMntOn");
		sql.append(" From LimitTransactionDetails");
		sql.append(" where ReferenceCode = :ReferenceCode and ReferenceNumber = :ReferenceNumber");
		sql.append(" and HeaderId=:HeaderId order by TransactionDate");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ReferenceCode", code);
		source.addValue("ReferenceNumber", ref);
		source.addValue("HeaderId", headerId);

		RowMapper<LimitTransactionDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(LimitTransactionDetail.class);
		try {
			logger.debug(Literal.LEAVING);
			return this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);

		return new ArrayList<>();
	}

	@Override
	public LimitTransactionDetail getTransaction(String referenceCode, String referenceNumber, String tranType,
			long headerId, int schSeq) {

		StringBuilder sql = new StringBuilder("Select TransactionId, ReferenceCode, ReferenceNumber");
		sql.append(", TransactionType, TransactionDate, OverrideFlag, TransactionAmount, SchSeq");
		sql.append(", TransactionCurrency, LimitCurrency, LimitAmount, CreatedBy, CreatedOn");
		sql.append(", LastMntBy, LastMntOn");
		sql.append(" From LimitTransactionDetails");
		sql.append(" where ReferenceCode = :ReferenceCode and ReferenceNumber = :ReferenceNumber");
		sql.append(" and TransactionType = :TransactionType and HeaderId = :HeaderId and SchSeq = :SchSeq");
		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ReferenceCode", referenceCode);
		source.addValue("ReferenceNumber", referenceNumber);
		source.addValue("TransactionType", tranType);
		source.addValue("HeaderId", headerId);
		source.addValue("SchSeq", schSeq);

		RowMapper<LimitTransactionDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(LimitTransactionDetail.class);
		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			//
		}

		return null;
	}

	@Override
	public LimitTransactionDetail geLoantAvaliableReserve(String referenceNumber, String tranType, long headerId) {
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ReferenceCode", LimitConstants.FINANCE);
		source.addValue("ReferenceNumber", referenceNumber);
		source.addValue("TransactionType", tranType);
		source.addValue("HeaderId", headerId);

		StringBuilder sql = new StringBuilder("Select  SUM(LimitAmount) LimitAmount");
		sql.append(" From LimitTransactionDetails");
		sql.append(" Where ReferenceCode = :ReferenceCode And ReferenceNumber = :ReferenceNumber AND ");
		sql.append("TransactionType = :TransactionType AND HeaderId=:HeaderId");
		logger.debug("selectSql: " + sql.toString());

		RowMapper<LimitTransactionDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(LimitTransactionDetail.class);
		try {
			LimitTransactionDetail limitTranDetail = this.jdbcTemplate.queryForObject(sql.toString(), source,
					typeRowMapper);
			if (limitTranDetail.getLimitAmount() == null) {
				limitTranDetail.setLimitAmount(BigDecimal.ZERO);
			}
			return limitTranDetail;
		} catch (EmptyResultDataAccessException e) {
			//
		}
		return null;
	}

	@Override
	public long save(LimitTransactionDetail limitTransactionDetail) {
		if (limitTransactionDetail.getId() == Long.MIN_VALUE) {
			limitTransactionDetail.setId(getNextValue("SeqLimitTransactionDetails"));
		}

		StringBuilder sql = new StringBuilder("Insert Into LimitTransactionDetails");
		sql.append(" (TransactionId, HeaderId, ReferenceCode, ReferenceNumber,TransactionType,SchSeq,");
		sql.append(" TransactionDate, OverrideFlag ,TransactionAmount ,TransactionCurrency ,LimitCurrency,");
		sql.append(" LimitAmount ,CreatedBy ,CreatedOn ,LastMntBy ,LastMntOn) ");
		sql.append(" Values(:TransactionId, :HeaderId, :ReferenceCode, :ReferenceNumber,:TransactionType,:SchSeq,");
		sql.append(" :TransactionDate ,:OverrideFlag ,:TransactionAmount ,:TransactionCurrency ,:LimitCurrency ,");
		sql.append(" :LimitAmount ,:CreatedBy ,:CreatedOn ,:LastMntBy ,:LastMntOn )");

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(limitTransactionDetail);
		this.jdbcTemplate.update(sql.toString(), beanParameters);

		return limitTransactionDetail.getId();
	}

	@Override
	public void updateSeq(long transactionId, int schSeq) {
		MapSqlParameterSource map = new MapSqlParameterSource();
		map.addValue("TransactionId", transactionId);
		map.addValue("SchSeq", schSeq);

		StringBuilder sql = new StringBuilder("update LimitTransactionDetails");
		sql.append("  set SchSeq=:SchSeq where TransactionId=:TransactionId");

		logger.trace(Literal.SQL + sql.toString());

		this.jdbcTemplate.update(sql.toString(), map);
	}

	/**
	 * This method Deletes the Record from the LIMIT_DETAILS or LIMIT_DETAILS_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete Limit Details by key DetailId
	 * 
	 * @param Limit
	 *            Details (limitDetail)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(long transactionId) {
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("TransactionId", transactionId);

		StringBuilder sql = new StringBuilder("Delete From LimitTransactionDetails");
		sql.append(" Where TransactionId = :TransactionId");

		logger.debug(Literal.SQL + sql.toString());

		try {
			this.jdbcTemplate.update(sql.toString(), source);
		} catch (DataAccessException e) {
			//
		}
	}

	// need to be verified in institution limits
	@Override
	public long saveLimitRuleTransactiondetails(LimitTransactionDetail limitTransactionDetail, String type) {
		logger.debug("Entering");
		if (limitTransactionDetail.getId() == Long.MIN_VALUE) {
			limitTransactionDetail.setId(getNextId("SeqLimitRuleTransactions"));
			logger.debug("get NextID:" + limitTransactionDetail.getId());
		}

		StringBuilder insertSql = new StringBuilder("Insert Into LimitRuleTransactionDetails");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(
				" (TransactionId ,LimitLine,RuleCode ,RuleValue ,ReferenceCode ,ReferenceNumber ,TransactionType ,TransactionDate ,TransactionAmount ,TransactionCurrency ,LimitCurrency ,LimitAmount ,CreatedBy ,CreatedOn ,LastMntBy ,LastMntOn) ");
		insertSql.append(
				" Values(:TransactionId ,:LimitLine,:RuleCode ,:RuleValue ,:ReferenceCode ,:ReferenceNumber ,:TransactionType ,:TransactionDate ,:TransactionAmount ,:TransactionCurrency ,:LimitCurrency ,:LimitAmount ,:CreatedBy ,:CreatedOn ,:LastMntBy ,:LastMntOn )");
		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(limitTransactionDetail);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return limitTransactionDetail.getId();

	}

	@Override
	public BigDecimal getUtilizedSumByRulecode(String ruleCode, String limitLine, String type) {
		BigDecimal utilizedLimit = BigDecimal.ZERO;
		LimitTransactionDetail transactionDetails = new LimitTransactionDetail();
		transactionDetails.setLimitLine(limitLine);
		StringBuilder selectSql = new StringBuilder(
				"Select SUM(LimitAmount) from LimitRuleTransactionDetails where RuleCode =:RuleCode AND LimitLine= :LimitLine ");
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(transactionDetails);

		utilizedLimit = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, BigDecimal.class);
		if (utilizedLimit == null) {
			utilizedLimit = BigDecimal.ZERO;

		}
		return utilizedLimit;

	}

	@Override
	public void deleteAllRuleTransactions(String type) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();

		StringBuilder deleteSql = new StringBuilder("Delete From LimitRuleTransactionDetails");
		deleteSql.append(StringUtils.trimToEmpty(type));
		logger.debug("deleteSql: " + deleteSql.toString());

		try {
			this.jdbcTemplate.update(deleteSql.toString(), source);

		} catch (DataAccessException e) {
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");

	}

	/**
	 * 
	 * 
	 */
	@Override
	public List<LimitTransactionDetail> getPreviousReservedAmt(String finReference, String transtype, long limitId) {
		logger.debug("Entering");

		MapSqlParameterSource source = null;

		StringBuilder selectSql = new StringBuilder("Select TransactionType, SUM(LimitAmount) LimitAmount");
		selectSql.append(" From LimitTransactionDetails");
		selectSql.append(
				" Where ReferenceCode = :ReferenceCode And ReferenceNumber = :ReferenceNumber AND HeaderId = :HeaderId");
		selectSql.append(" group by TransactionType");
		logger.debug("selectSql: " + selectSql.toString());

		source = new MapSqlParameterSource();
		source.addValue("ReferenceCode", LimitConstants.FINANCE);
		source.addValue("ReferenceNumber", finReference);
		source.addValue("TransactionType", transtype);
		source.addValue("HeaderId", limitId);

		RowMapper<LimitTransactionDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(LimitTransactionDetail.class);
		try {
			return this.jdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(e);
		} finally {
			source = null;
			selectSql = null;
			logger.debug("Leaving");
		}
		return null;
	}

	/**
	 * Method for delete logged information, When Reserve Limit service called from API.
	 * 
	 * @param referenceNumber
	 */
	@Override
	public void deleteReservedLogs(String referenceNumber) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ReferenceNumber", referenceNumber);
		List<String> transTypes = new ArrayList<String>();
		transTypes.add(LimitConstants.BLOCK);
		transTypes.add(LimitConstants.UNBLOCK);
		source.addValue("Transactiontype", transTypes);

		StringBuilder deleteSql = new StringBuilder("Delete From LimitTransactionDetails");
		deleteSql.append(" Where ReferenceNumber = :ReferenceNumber AND Transactiontype IN(:Transactiontype)");

		logger.debug("deleteSql: " + deleteSql.toString());

		try {
			this.jdbcTemplate.update(deleteSql.toString(), source);
		} catch (DataAccessException e) {
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");

	}

	@Override
	public void updateHeaderIDWithFin(String finReference, long updateFrom, long updateTo) {
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ReferenceNumber", finReference);
		source.addValue("UpdateFrom", updateFrom);
		source.addValue("UpdateTo", updateTo);

		StringBuilder deleteSql = new StringBuilder("Update LimitTransactionDetails");
		deleteSql.append(" set HEADERID=:UpdateTo");
		deleteSql.append(" Where ReferenceNumber = :ReferenceNumber AND HEADERID =:UpdateFrom");

		logger.debug("deleteSql: " + deleteSql.toString());

		try {
			this.jdbcTemplate.update(deleteSql.toString(), source);
		} catch (DataAccessException e) {
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");

	}

	@Override
	public void updateHeaderID(long updateFrom, long updateTo) {
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("UpdateFrom", updateFrom);
		source.addValue("UpdateTo", updateTo);

		StringBuilder deleteSql = new StringBuilder("Update LimitTransactionDetails");
		deleteSql.append(" set HEADERID=:UpdateTo");
		deleteSql.append(" Where  HEADERID =:UpdateFrom");

		logger.debug("deleteSql: " + deleteSql.toString());

		try {
			this.jdbcTemplate.update(deleteSql.toString(), source);
		} catch (DataAccessException e) {
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");

	}

}
