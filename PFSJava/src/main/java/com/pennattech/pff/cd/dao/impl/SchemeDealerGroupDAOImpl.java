package com.pennattech.pff.cd.dao.impl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;

import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.cd.model.SchemeDealerGroup;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;
import com.pennattech.pff.cd.dao.SchemeDealerGroupDAO;

public class SchemeDealerGroupDAOImpl extends SequenceDao<SchemeDealerGroup> implements SchemeDealerGroupDAO {
	private static Logger logger = LogManager.getLogger(SchemeDealerGroupDAOImpl.class);

	public SchemeDealerGroupDAOImpl() {
		super();
	}

	@Override
	public SchemeDealerGroup getSchemeDealerGroup(long id, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("Select SchemeDealerGroupId, PromotionId, DealerGroupCode, Active");
		if (type.contains("View")) {
			sql.append(" ");
		}
		sql.append(", Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(", RecordType, WorkflowId");
		sql.append(" From CD_SCHEME_DEALERGROUP");
		sql.append(type);
		sql.append(" Where SchemeDealerGroupId = :schemeDealerGroupId");

		SchemeDealerGroup schemeDealerGroup = new SchemeDealerGroup();
		schemeDealerGroup.setSchemeDealerGroupId(id);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(schemeDealerGroup);
		RowMapper<SchemeDealerGroup> rowMapper = BeanPropertyRowMapper.newInstance(SchemeDealerGroup.class);

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

		String query = getQuery(tableType.getSuffix());

		if (schemeDealerGroup.getSchemeDealerGroupId() == Long.MIN_VALUE) {
			schemeDealerGroup.setSchemeDealerGroupId(getGrpIdSeq());
		}

		logger.trace(Literal.SQL + query.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(schemeDealerGroup);

		try {
			jdbcTemplate.update(query.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(schemeDealerGroup.getSchemeDealerGroupId());
	}

	@Override
	public void update(SchemeDealerGroup schemeDealerGroup, TableType tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Update CD_SCHEME_DEALERGROUP");
		sql.append(tableType.getSuffix());
		sql.append(" set PromotionId = :promotionId, DealerGroupCode = :dealerGroupCode, Active = :active");
		sql.append(", LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode");
		sql.append(", NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId");
		sql.append(", RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where SchemeDealerGroupId = :schemeDealerGroupId ");
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
		sql.append(" where SchemeDealerGroupId = :schemeDealerGroupId ");
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
		String whereClause = "DealerGroupCode = :dealerGroupCode AND PromotionId = :promotionId ";

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
		paramSource.addValue("promotionId", schemeDealerGroup.getPromotionId());

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

	/*
	 * 
	 */
	@Override
	public long getGrpIdSeq() {
		return getNextValue("SEQCD_Scheme_DealerGroup");
	}

	@Override
	public void saveDealerGrpBatch(List<SchemeDealerGroup> sdgList, TableType tableType) {

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(sdgList.toArray());
		try {

			this.jdbcTemplate.batchUpdate(getQuery(tableType.getSuffix()), beanParameters);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw e;
		}
	}

	public String getQuery(String tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("insert into CD_SCHEME_DEALERGROUP");
		sql.append(tableType);
		sql.append("(SchemeDealerGroupId, PromotionId, DealerGroupCode, Active, Version, LastMntBy");
		sql.append(", LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values");
		sql.append("(:schemeDealerGroupId, :promotionId, :dealerGroupCode, :active, :Version , :LastMntBy, :LastMntOn");
		sql.append(", :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug(Literal.LEAVING);
		return sql.toString();
	}
}
