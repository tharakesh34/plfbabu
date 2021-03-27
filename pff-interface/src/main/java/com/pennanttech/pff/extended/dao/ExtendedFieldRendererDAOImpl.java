package com.pennanttech.pff.extended.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennanttech.pennapps.core.jdbc.BasicDao;

public class ExtendedFieldRendererDAOImpl extends BasicDao<ExtendedFieldRender> implements ExtendedFieldRendererDAO {
	private static Logger logger = LogManager.getLogger(ExtendedFieldRendererDAOImpl.class);

	public Map<String, Object> getExtendedField(String reference, String tableName, String type) {
		logger.debug("Entering");

		Map<String, Object> renderMap = null;
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Reference", reference);

		StringBuilder selectSql = null;
		selectSql = new StringBuilder("Select * from ");
		selectSql.append(tableName);
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" where  Reference = :Reference ");

		logger.debug("selectSql: " + selectSql.toString());
		try {
			renderMap = this.jdbcTemplate.queryForMap(selectSql.toString(), source);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exceprtion ", e);
			renderMap = null;
		}

		logger.debug("Leaving");
		return renderMap;
	}

	@Override
	public Map<String, List<ExtendedFieldRender>> getCollateralExtendedFields(String reference, String tableName,
			String type) {
		Map<String, List<ExtendedFieldRender>> renderMap = new HashMap<String, List<ExtendedFieldRender>>();
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Reference", reference);

		StringBuilder selectSql = null;
		selectSql = new StringBuilder("Select * from ");
		selectSql.append(tableName);
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" where  Reference = :Reference ");

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<ExtendedFieldRender> typeRowMapper = BeanPropertyRowMapper.newInstance(ExtendedFieldRender.class);

		try {
			//renderMap = this.jdbcTemplate.query(selectSql, source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exceprtion ", e);
			renderMap = null;
		}

		logger.debug("Leaving");
		return renderMap;
	}

}
