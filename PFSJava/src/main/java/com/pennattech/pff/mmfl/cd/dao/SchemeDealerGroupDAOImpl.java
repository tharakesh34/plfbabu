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
import com.pennanttech.pff.mmfl.cd.model.SchemeDealerGroup;

public class SchemeDealerGroupDAOImpl extends SequenceDao<SchemeDealerGroup> implements SchemeDealerGroupDAO {
	private static Logger logger = Logger.getLogger(SchemeDealerGroupDAOImpl.class);

	public SchemeDealerGroupDAOImpl() {
		super();
	}

	@Override
	public SchemeDealerGroup getSchemeDealerGroup(long id, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("Select PromotionId, SchemeId, DealerGroupCode, Active");
		if (type.contains("View")) {
			sql.append(" ");
		}
		sql.append(", Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(", RecordType, WorkflowId");
		sql.append(" From CD_SCHEME_DEALERGROUP");
		sql.append(type);
		sql.append(" Where PromotionId = :promotionId");

		SchemeDealerGroup schemeDealerGroup = new SchemeDealerGroup();
		schemeDealerGroup.setPromotionId(id);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(schemeDealerGroup);
		RowMapper<SchemeDealerGroup> rowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(SchemeDealerGroup.class);

		try {
			schemeDealerGroup = jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return schemeDealerGroup;
	}

	@Override
	public String save(SchemeDealerGroup schemeDealerGroup, TableType tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("insert into CD_SCHEME_DEALERGROUP");
		sql.append(tableType.getSuffix());
		sql.append("(PromotionId, SchemeId, DealerGroupCode, Active, Version, LastMntBy, LastMntOn, RecordStatus");
		sql.append(", RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values");
		sql.append("(:promotionId, :schemeId, :dealerGroupCode, :active, :Version , :LastMntBy, :LastMntOn");
		sql.append(", :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		if (schemeDealerGroup.getPromotionId() == Long.MIN_VALUE) {
			schemeDealerGroup.setPromotionId(getNextValue("SEQCD_Scheme_DealerGroup"));
		}

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(schemeDealerGroup);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(schemeDealerGroup.getPromotionId());
	}

	@Override
	public void update(SchemeDealerGroup schemeDealerGroup, TableType tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Update CD_SCHEME_DEALERGROUP");
		sql.append(tableType.getSuffix());
		sql.append(" set SchemeId = :schemeId, DealerGroupCode = :dealerGroupCode, Active = :active");
		sql.append(", LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode");
		sql.append(", NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId");
		sql.append(", RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where PromotionId = :promotionId ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(schemeDealerGroup);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(SchemeDealerGroup schemeDealerGroup, TableType tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("delete from CD_SCHEME_DEALERGROUP");
		sql.append(tableType.getSuffix());
		sql.append(" where PromotionId = :promotionId ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(schemeDealerGroup);
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
	public boolean isDuplicateKey(SchemeDealerGroup schemeDealerGroup, TableType tableType) {
		logger.debug(Literal.ENTERING);

		String sql;
		String whereClause = "DealerGroupCode = :dealerGroupCode ";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("CD_SCHEME_DEALERGROUP", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("CD_SCHEME_DEALERGROUP_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "CD_SCHEME_DEALERGROUP_Temp", "CD_SCHEME_DEALERGROUP" },
					whereClause);
			break;
		}

		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("dealerGroupCode", schemeDealerGroup.getDealerGroupCode());

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

}
