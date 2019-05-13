package com.pennant.backend.dao.finance.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.finance.FinanceScoreHeaderDAO;
import com.pennant.backend.model.finance.FinanceScoreDetail;
import com.pennant.backend.model.finance.FinanceScoreHeader;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;

public class FinanceScoreHeaderDAOImpl extends SequenceDao<FinanceScoreHeader> implements FinanceScoreHeaderDAO {
	private static Logger logger = Logger.getLogger(FinanceScoreHeaderDAOImpl.class);

	public FinanceScoreHeaderDAOImpl() {
		super();
	}

	@Override
	public List<FinanceScoreHeader> getFinScoreHeaderList(String finReference, String type) {
		logger.debug("Entering");
		FinanceScoreHeader scoreHeader = new FinanceScoreHeader();
		scoreHeader.setFinReference(finReference);

		StringBuilder selectSql = new StringBuilder("SELECT HeaderId , FinReference , ");
		selectSql.append(" GroupId , MinScore , Override , OverrideScore , CreditWorth ");
		if (type.contains("View")) {
			selectSql.append(" , GroupCode , GroupCodeDesc ");
		}
		selectSql.append(" From FinanceScoreHeader");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(scoreHeader);
		RowMapper<FinanceScoreHeader> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinanceScoreHeader.class);

		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	@Override
	public long saveHeader(FinanceScoreHeader scoreHeader, String type) {
		logger.debug("Entering");

		if (scoreHeader.getHeaderId() == Long.MIN_VALUE) {
			scoreHeader.setId(getNextValue("SeqFinanceScoreHeader"));
			logger.debug("get NextID:" + scoreHeader.getId());
		}

		StringBuilder insertSql = new StringBuilder("INSERT INTO FinanceScoreHeader");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (HeaderId , FinReference , GroupId , MinScore , ");
		insertSql.append(" Override , OverrideScore , CreditWorth) ");
		insertSql.append(" VALUES (:HeaderId , :FinReference , :GroupId , :MinScore , ");
		insertSql.append(" :Override , :OverrideScore , :CreditWorth) ");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(scoreHeader);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return scoreHeader.getId();
	}

	@Override
	public void deleteHeaderList(String finReferecne, String type) {
		logger.debug("Entering");

		FinanceScoreHeader header = new FinanceScoreHeader();
		header.setFinReference(finReferecne);

		StringBuilder deleteSql = new StringBuilder(" DELETE FROM FinanceScoreHeader");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" WHERE FinReference=:FinReference");

		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(header);
		this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	@Override
	public List<FinanceScoreDetail> getFinScoreDetailList(List<Long> headerIds, String type) {
		logger.debug("Entering");

		StringBuilder selectSql = new StringBuilder(" SELECT HeaderId , SubGroupId , ");
		selectSql.append(" RuleId , MaxScore , ExecScore ");
		if (type.contains("View")) {
			selectSql.append(" , SubGrpCodeDesc , RuleCode , RuleCodeDesc , CategoryType ");
		}
		selectSql.append(" From FinanceScoreDetail");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where HeaderId  IN(:HeaderId )");

		Map<String, List<Long>> parameterMap = new HashMap<String, List<Long>>();
		parameterMap.put("HeaderId", headerIds);

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<FinanceScoreDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinanceScoreDetail.class);

		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql.toString(), parameterMap, typeRowMapper);
	}

	@Override
	public void saveDetailList(List<FinanceScoreDetail> scoreDetails, String type) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("INSERT INTO FinanceScoreDetail");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (HeaderId , SubGroupId , RuleId , MaxScore , ExecScore) ");
		insertSql.append(" VALUES (:HeaderId , :SubGroupId , :RuleId , :MaxScore , :ExecScore) ");

		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(scoreDetails.toArray());

		logger.debug("Leaving");
		this.jdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
	}

	@Override
	public void deleteDetailList(List<Long> headerIdList, String type) {
		logger.debug("Entering");
		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("HeaderIdList", headerIdList);

		StringBuilder deleteSql = new StringBuilder(" DELETE FROM FinanceScoreDetail");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" WHERE HeaderId IN(:HeaderIdList) ");

		logger.debug("deleteSql: " + deleteSql.toString());
		logger.debug("Leaving");
		this.jdbcTemplate.update(deleteSql.toString(), parameterSource);

	}

	@Override
	public boolean deleteHeader(FinanceScoreHeader scoreHeader, String type) {
		logger.debug("Entering");

		StringBuilder deleteSql = new StringBuilder(" DELETE FROM FinanceScoreHeader");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" WHERE HeaderId=:HeaderId AND FinReference=:FinReference");

		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(scoreHeader);
		int recordCount = this.jdbcTemplate.update(deleteSql.toString(), beanParameters);

		logger.debug("Leaving");
		if (recordCount <= 0) {
			return false;
		}
		return true;
	}

	@Override
	public void deleteDetailList(long headerId, String type) {
		logger.debug("Entering");

		FinanceScoreDetail detail = new FinanceScoreDetail();
		detail.setHeaderId(headerId);

		StringBuilder deleteSql = new StringBuilder(" DELETE FROM FinanceScoreDetail");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" WHERE HeaderId=:HeaderId");

		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(detail);

		logger.debug("Leaving");
		this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
	}

}
