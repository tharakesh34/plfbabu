package com.pennant.backend.dao.finance.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;

import com.pennant.backend.dao.finance.ExposureLinkingDAO;
import com.pennant.backend.model.finance.ExposureLinking;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class ExposureLinkingDAOImpl extends BasicDao<ExposureLinking> implements ExposureLinkingDAO {
	private static Logger logger = LogManager.getLogger(ExposureLinkingDAOImpl.class);

	@Override
	public String save(List<ExposureLinking> exposureLinkings) {

		logger.debug(Literal.ENTERING);

		if (CollectionUtils.isEmpty(exposureLinkings)) {
			return "";
		}

		deleteExistingData(exposureLinkings.get(0).getFinReference());

		// Prepare the SQL.
		StringBuilder insertSql = new StringBuilder("insert into ExposureLinking");
		insertSql.append(" (FinReference, ExpReference, POS)");
		insertSql.append(" values (:FinReference, :ExpReference, :Pos)");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + insertSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(exposureLinkings.toArray());

		try {
			this.jdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		} catch (Exception e) {
			logger.error("Exception", e);
			throw e;
		}

		logger.debug(Literal.LEAVING);
		return "";

	}

	private void deleteExistingData(String finReference) {
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);

		StringBuilder sql = new StringBuilder("delete from ExposureLinking");
		sql.append(" where FinReference = :FinReference");

		logger.trace(Literal.SQL + sql.toString());

		this.jdbcTemplate.update(sql.toString(), source);
	}

	@Override
	public List<ExposureLinking> getExposureLinkgs(String finReference) {

		logger.debug("Entering");

		ExposureLinking exposureLinking = new ExposureLinking();
		exposureLinking.setFinReference(finReference);

		StringBuilder selectSql = new StringBuilder("SELECT FinReference, ExpReference, Pos ");
		selectSql.append(" FROM ExposureLinking");
		selectSql.append(" Where FinReference = :finReference");

		logger.debug("selectListSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(exposureLinking);
		RowMapper<ExposureLinking> typeRowMapper = BeanPropertyRowMapper.newInstance(ExposureLinking.class);

		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);

	}
}
