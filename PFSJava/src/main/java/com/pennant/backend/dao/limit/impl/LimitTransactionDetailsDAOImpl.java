package com.pennant.backend.dao.limit.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.dao.limit.LimitTransactionDetailsDAO;
import com.pennant.backend.model.limit.LimitTransactionDetail;
import com.pennant.backend.util.LimitConstants;

public class LimitTransactionDetailsDAOImpl extends BasisNextidDaoImpl<LimitTransactionDetail> implements
		LimitTransactionDetailsDAO {

	private static Logger				logger	= Logger.getLogger(LimitTransactionDetailsDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate	namedParameterJdbcTemplate;

	public LimitTransactionDetailsDAOImpl() {
		super();
	}

	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	@Override
	public List<LimitTransactionDetail> getLimitTranDetails(String code, String ref,long headerId) {
		logger.debug("Entering");

		MapSqlParameterSource source = null;

		StringBuilder selectSql = new StringBuilder("Select TransactionId, ReferenceCode, ");
		selectSql.append("ReferenceNumber, HeaderId,TransactionType, TransactionDate,SchSeq,");
		selectSql.append(" OverrideFlag, TransactionAmount, TransactionCurrency, LimitCurrency, LimitAmount");
		selectSql.append(", CreatedBy, CreatedOn, LastMntBy, LastMntOn");
		selectSql.append(" From LimitTransactionDetails");
		selectSql.append(" Where ReferenceCode = :ReferenceCode And ReferenceNumber = :ReferenceNumber AND HeaderId=:HeaderId order by TransactionDate");

		logger.debug("selectSql: " + selectSql.toString());
		source = new MapSqlParameterSource();
		source.addValue("ReferenceCode", code);
		source.addValue("ReferenceNumber", ref);
		source.addValue("HeaderId", headerId);

		RowMapper<LimitTransactionDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(LimitTransactionDetail.class);
		try {
			return this.namedParameterJdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(e);
		} finally {
			source = null;
			selectSql = null;
			logger.debug("Leaving");
		}
		return null;
	}

	@Override
	public LimitTransactionDetail getTransaction(String referenceCode, String referenceNumber, String tranType,
			long headerId,int schSeq) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ReferenceCode", referenceCode);
		source.addValue("ReferenceNumber", referenceNumber);
		source.addValue("TransactionType", tranType);
		source.addValue("HeaderId", headerId);
		source.addValue("SchSeq", schSeq);

		StringBuilder selectSql = new StringBuilder("Select  TransactionId, ReferenceCode, ReferenceNumber, ");
		selectSql.append(" TransactionType,TransactionDate, OverrideFlag, TransactionAmount,SchSeq, ");
		selectSql.append(" TransactionCurrency, LimitCurrency, LimitAmount, CreatedBy, CreatedOn,");
		selectSql.append("  LastMntBy, LastMntOn From LimitTransactionDetails");
		selectSql.append(" Where ReferenceCode = :ReferenceCode And ReferenceNumber = :ReferenceNumber AND ");
		selectSql.append("TransactionType = :TransactionType AND HeaderId=:HeaderId AND  SchSeq=:SchSeq ");
		logger.debug("selectSql: " + selectSql.toString());

		RowMapper<LimitTransactionDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(LimitTransactionDetail.class);
		try {
			return this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
		} finally {
			source = null;
			selectSql = null;
			logger.debug("Leaving");
		}
		return null;
	}
	
	
	@Override
	public LimitTransactionDetail geLoantAvaliableReserve(String referenceNumber, String tranType,long headerId) {
		logger.debug("Entering");
		
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ReferenceCode", LimitConstants.FINANCE);
		source.addValue("ReferenceNumber", referenceNumber);
		source.addValue("TransactionType", tranType);
		source.addValue("HeaderId", headerId);
		
		StringBuilder selectSql = new StringBuilder("Select  SUM(LimitAmount) LimitAmount");
		selectSql.append(" From LimitTransactionDetails");
		selectSql.append(" Where ReferenceCode = :ReferenceCode And ReferenceNumber = :ReferenceNumber AND ");
		selectSql.append("TransactionType = :TransactionType AND HeaderId=:HeaderId");
		logger.debug("selectSql: " + selectSql.toString());
		
		RowMapper<LimitTransactionDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(LimitTransactionDetail.class);
		try {
			LimitTransactionDetail limitTranDetail= this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source, typeRowMapper);
			if (limitTranDetail.getLimitAmount()==null) {
				limitTranDetail.setLimitAmount(BigDecimal.ZERO);
			}
			return limitTranDetail;
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
		} finally {
			source = null;
			selectSql = null;
			logger.debug("Leaving");
		}
		return null;
	}

	@Override
	public long save(LimitTransactionDetail limitTransactionDetail) {
		logger.debug("Entering");
		if (limitTransactionDetail.getId() == Long.MIN_VALUE) {
			limitTransactionDetail.setId(getNextidviewDAO().getNextId("SeqLimitTransactionDetails"));
			logger.debug("get NextID:" + limitTransactionDetail.getId());
		}

		StringBuilder insertSql = new StringBuilder("Insert Into LimitTransactionDetails");
		insertSql.append(" (TransactionId, HeaderId, ReferenceCode, ReferenceNumber,TransactionType,SchSeq,");
		insertSql.append(" TransactionDate, OverrideFlag ,TransactionAmount ,TransactionCurrency ,LimitCurrency,");
		insertSql.append(" LimitAmount ,CreatedBy ,CreatedOn ,LastMntBy ,LastMntOn) ");
		insertSql.append(" Values(:TransactionId, :HeaderId, :ReferenceCode, :ReferenceNumber,:TransactionType,:SchSeq,");
		insertSql
				.append(" :TransactionDate ,:OverrideFlag ,:TransactionAmount ,:TransactionCurrency ,:LimitCurrency ,");
		insertSql.append(" :LimitAmount ,:CreatedBy ,:CreatedOn ,:LastMntBy ,:LastMntOn )");
		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(limitTransactionDetail);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return limitTransactionDetail.getId();
	}
	
	@Override
	public void updateSeq(long transactionId, int schSeq) {
		logger.debug("Entering");
		MapSqlParameterSource map = new MapSqlParameterSource();
		map.addValue("TransactionId", transactionId);
		map.addValue("SchSeq", schSeq);

		StringBuilder sql = new StringBuilder("update LimitTransactionDetails");
		sql.append("  set SchSeq=:SchSeq  where TransactionId=:TransactionId ");
		logger.debug("insertSql: " + sql.toString());

		this.namedParameterJdbcTemplate.update(sql.toString(), map);
		logger.debug("Leaving");
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
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("TransactionId", transactionId);
		StringBuilder deleteSql = new StringBuilder("Delete From LimitTransactionDetails");
		deleteSql.append(" Where TransactionId = :TransactionId");
		logger.debug("deleteSql: " + deleteSql.toString());

		try {
			this.namedParameterJdbcTemplate.update(deleteSql.toString(), source);

		} catch (DataAccessException e) {
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");
	}
	
	
	// need to be verified in institution limits
	@Override
	public long saveLimitRuleTransactiondetails(LimitTransactionDetail limitTransactionDetail, String type) {
		logger.debug("Entering");
		if (limitTransactionDetail.getId() == Long.MIN_VALUE) {
			limitTransactionDetail.setId(getNextidviewDAO().getNextId("SeqLimitRuleTransactions"));
			logger.debug("get NextID:" + limitTransactionDetail.getId());
		}

		StringBuilder insertSql = new StringBuilder("Insert Into LimitRuleTransactionDetails");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql
				.append(" (TransactionId ,LimitLine,RuleCode ,RuleValue ,ReferenceCode ,ReferenceNumber ,TransactionType ,TransactionDate ,TransactionAmount ,TransactionCurrency ,LimitCurrency ,LimitAmount ,CreatedBy ,CreatedOn ,LastMntBy ,LastMntOn) ");
		insertSql
				.append(" Values(:TransactionId ,:LimitLine,:RuleCode ,:RuleValue ,:ReferenceCode ,:ReferenceNumber ,:TransactionType ,:TransactionDate ,:TransactionAmount ,:TransactionCurrency ,:LimitCurrency ,:LimitAmount ,:CreatedBy ,:CreatedOn ,:LastMntBy ,:LastMntOn )");
		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(limitTransactionDetail);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
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

		utilizedLimit = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters,
				BigDecimal.class);
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
			this.namedParameterJdbcTemplate.update(deleteSql.toString(), source);

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
		selectSql.append(" Where ReferenceCode = :ReferenceCode And ReferenceNumber = :ReferenceNumber AND HeaderId = :HeaderId");
		selectSql.append(" group by TransactionType");
		logger.debug("selectSql: " + selectSql.toString());
		
		source = new MapSqlParameterSource();
		source.addValue("ReferenceCode", LimitConstants.FINANCE);
		source.addValue("ReferenceNumber", finReference);
		source.addValue("TransactionType", transtype);
		source.addValue("HeaderId", limitId);

		RowMapper<LimitTransactionDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(LimitTransactionDetail.class);
		try {
			return this.namedParameterJdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
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
			this.namedParameterJdbcTemplate.update(deleteSql.toString(), source);
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
			this.namedParameterJdbcTemplate.update(deleteSql.toString(), source);
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
			this.namedParameterJdbcTemplate.update(deleteSql.toString(), source);
		} catch (DataAccessException e) {
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");
		
	}

}
