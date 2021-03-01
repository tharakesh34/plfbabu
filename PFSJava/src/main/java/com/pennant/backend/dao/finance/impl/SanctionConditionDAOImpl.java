package com.pennant.backend.dao.finance.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.finance.SanctionConditionDAO;
import com.pennant.backend.model.finance.ExposureLinking;
import com.pennant.backend.model.finance.SanctionCondition;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class SanctionConditionDAOImpl extends BasicDao<ExposureLinking> implements SanctionConditionDAO {
	private static Logger logger = LogManager.getLogger(ExposureLinkingDAOImpl.class);

	@Override
	public String save(List<SanctionCondition> conditions) {
		logger.debug("Entering");

		if (CollectionUtils.isEmpty(conditions)) {
			return "";
		}

		deleteExistingData(conditions.get(0).getFinReference());

		StringBuilder insertSql = new StringBuilder("Insert Into SanctionCondition");

		insertSql.append(" (finReference, condition, status, applicable) ");
		insertSql.append(" values (:finReference, :condition, :status, :applicable)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(conditions.toArray());
		try {
			this.jdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		} catch (Exception e) {
			logger.error("Exception", e);
			throw e;
		}
		logger.debug("Leaving");

		return "";
	}

	private void deleteExistingData(String finReference) {
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);

		StringBuilder sql = new StringBuilder("delete from SanctionCondition");
		sql.append(" where FinReference = :FinReference");

		logger.trace(Literal.SQL + sql.toString());

		this.jdbcTemplate.update(sql.toString(), source);
	}

	@Override
	public List<SanctionCondition> getSanctionConditions(String finReference) {
		logger.debug("Entering");

		SanctionCondition exposureLinking = new SanctionCondition();
		exposureLinking.setFinReference(finReference);

		StringBuilder selectSql = new StringBuilder("SELECT FinReference, Condition, Status, Applicable ");
		selectSql.append(" FROM SanctionCondition");
		selectSql.append(" Where FinReference = :finReference");

		logger.debug("selectListSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(exposureLinking);
		RowMapper<SanctionCondition> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(SanctionCondition.class);

		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);

	}
}
