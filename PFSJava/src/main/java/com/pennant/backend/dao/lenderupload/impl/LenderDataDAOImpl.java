package com.pennant.backend.dao.lenderupload.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.lenderupload.LenderDataDAO;
import com.pennant.backend.model.lenderdataupload.LenderDataUpload;
import com.pennanttech.pennapps.core.jdbc.BasicDao;

public class LenderDataDAOImpl extends BasicDao<LenderDataUpload> implements LenderDataDAO {

	private static Logger logger = LogManager.getLogger(LenderDataDAOImpl.class);

	@Override
	public int update(LenderDataUpload dataUpload, String tableName, String type) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update ");
		updateSql.append(tableName);
		updateSql.append(type);
		updateSql.append(" set LenderId = :LenderId,");
		updateSql.append(" Version = :Version,LastMntBy = :LastMntBy, LastMntOn = :LastMntOn");
		updateSql.append(" Where Reference = :FinReference");

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(dataUpload);

		return this.jdbcTemplate.update(updateSql.toString(), paramSource);
	}

	@Override
	public boolean isLenderExist(String finReference, String tableName, String type) {
		logger.debug("Entering");

		int count = 0;
		StringBuilder sql = new StringBuilder();
		sql.append("select count(*) from ");
		sql.append(tableName);
		sql.append(type);
		sql.append(" where Reference=:reference");

		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("reference", finReference);

		logger.debug("SQL :" + sql.toString());

		count = this.jdbcTemplate.queryForObject(sql.toString(), parameterSource, Integer.class);
		if (count > 0) {
			return true;
		} else {
			return false;
		}

	}

}
