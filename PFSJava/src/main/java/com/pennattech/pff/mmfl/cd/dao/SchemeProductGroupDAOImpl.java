package com.pennattech.pff.mmfl.cd.dao;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;
import com.pennanttech.pff.mmfl.cd.model.SchemeProductGroup;

public class SchemeProductGroupDAOImpl extends SequenceDao<SchemeProductGroup> implements SchemeProductGroupDAO {
	private static Logger logger = Logger.getLogger(SchemeProductGroupDAOImpl.class);

	public SchemeProductGroupDAOImpl() {
		super();
	}

	@Override
	public SchemeProductGroup getSchemeProductGroup(long id, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("Select SchemeProductGroupId, PromotionId, ProductGroupCode, POSVendor, Active");
		if (type.contains("View")) {
			sql.append(" ");
		}
		sql.append(", Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(", RecordType, WorkflowId");
		sql.append(" From CD_SCHEME_PRODUCTGROUP");
		sql.append(type);
		sql.append(" Where SchemeProductGroupId = :schemeProductGroupId");

		SchemeProductGroup schemeProductGroup = new SchemeProductGroup();
		schemeProductGroup.setSchemeProductGroupId(id);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(schemeProductGroup);
		RowMapper<SchemeProductGroup> rowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(SchemeProductGroup.class);

		try {
			schemeProductGroup = jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return schemeProductGroup;
	}

	@Override
	public String save(SchemeProductGroup schemeProductGroup, TableType tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("insert into CD_SCHEME_PRODUCTGROUP");
		sql.append(tableType.getSuffix());
		sql.append("(SchemeProductGroupId, PromotionId, ProductGroupCode, POSVendor, Active, Version, LastMntBy");
		sql.append(", LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values");
		sql.append(
				"(:schemeProductGroupId, :promotionId, :productGroupCode, :POSVendor , :active, :Version , :LastMntBy, :LastMntOn");
		sql.append(", :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		if (schemeProductGroup.getSchemeProductGroupId() == Long.MIN_VALUE) {
			schemeProductGroup.setSchemeProductGroupId(getNextValue("SEQCD_Scheme_ProductGroup"));
		}

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(schemeProductGroup);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(schemeProductGroup.getSchemeProductGroupId());
	}

	@Override
	public void update(SchemeProductGroup schemeProductGroup, TableType tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Update CD_SCHEME_PRODUCTGROUP");
		sql.append(tableType.getSuffix());
		sql.append(" set PromotionId = :promotionId, ProductGroupCode = :productGroupCode, Active = :active");
		sql.append(", LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode");
		sql.append(", NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId");
		sql.append(", RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where SchemeProductGroupId = :schemeProductGroupId ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(schemeProductGroup);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(SchemeProductGroup schemeProductGroup, TableType tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("delete from CD_SCHEME_PRODUCTGROUP");
		sql.append(tableType.getSuffix());
		sql.append(" where SchemeProductGroupId = :schemeProductGroupId ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(schemeProductGroup);
		int recordCount = 0;

		try {
			recordCount = jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public boolean isDuplicateKey(SchemeProductGroup schemeProductGroup, TableType tableType) {
		logger.debug(Literal.ENTERING);

		String sql;
		String whereClause = "ProductGroupCode = :productGroupCode ";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("CD_SCHEME_PRODUCTGROUP", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("CD_SCHEME_PRODUCTGROUP_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "CD_SCHEME_PRODUCTGROUP_Temp", "CD_SCHEME_PRODUCTGROUP" },
					whereClause);
			break;
		}

		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("productGroupCode", schemeProductGroup.getProductGroupCode());

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

}
