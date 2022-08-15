package com.pennant.backend.dao.systemmasters.impl;

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

import com.pennant.backend.dao.systemmasters.DealerGroupDAO;
import com.pennant.backend.model.systemmasters.DealerGroup;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

public class DealerGroupDAOImpl extends SequenceDao<DealerGroup> implements DealerGroupDAO {

	private static Logger logger = LogManager.getLogger(DealerGroupDAOImpl.class);

	public DealerGroupDAOImpl() {
		super();
	}

	@Override
	public String save(DealerGroup dealerGroup, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" insert into CD_DealerGroup");
		sql.append(tableType.getSuffix());
		sql.append(" (dealerGroupId, dealerCode, dealerCategoryId, active, channel");
		sql.append(" , Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(" , RecordType, WorkflowId)");
		sql.append(" values(");
		sql.append(" :dealerGroupId, :dealerCode, :dealerCategoryId, :active, :channel");
		sql.append(
				" , :Version, :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId");
		sql.append(" , :RecordType, :WorkflowId)");

		// Get the identity sequence number.
		if (dealerGroup.getId() <= 0) {
			dealerGroup.setId(getNextValue("SEQCD_DEALERGROUP"));
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(dealerGroup);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(dealerGroup.getId());
	}

	@Override
	public void update(DealerGroup dealerGroup, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update CD_DealerGroup");
		sql.append(tableType.getSuffix());
		sql.append("  set dealerCode = :dealerCode, channel = :channel, active = :active");
		sql.append(" , dealerCategoryId=:dealerCategoryId, LastMntOn = :LastMntOn, RecordStatus = :RecordStatus");
		sql.append(" , RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId");
		sql.append(" , RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where dealerGroupId = :dealerGroupId ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(dealerGroup);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);

	}

	@Override
	public void delete(DealerGroup dealerGroup, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from CD_DealerGroup");
		sql.append(tableType.getSuffix());
		sql.append(" where DealerGroupId = :DealerGroupId ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(dealerGroup);
		int recordCount = 0;

		try {
			recordCount = jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);

	}

	@Override
	public boolean isDuplicateKey(long id, String dealercode, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		String sql;
		String whereClause = "dealercode = :dealercode AND DealerGroupId != :DealerGroupId";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("CD_DealerGroup", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("CD_DealerGroup_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "CD_DealerGroup_Temp", "CD_DealerGroup" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("DealerGroupId", id);
		paramSource.addValue("dealercode", dealercode);
		// paramSource.addValue("groupId", groupId);

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

	@Override
	public DealerGroup getDealerGroup(long id, String type) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" dealerGroupId, dealercode, channel, dealercategoryid, active,");
		/*
		 * if (type.contains("View")) { sql.append("channelName,"); }
		 */

		sql.append(
				" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From CD_DealerGroup");
		sql.append(type);
		if (type.contains("View")) {
			sql.append(" Where dealerGroupId = :dealerGroupId ");
		} else {
			sql.append(" Where dealerGroupId = :dealerGroupId");
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		DealerGroup dealerGroup = new DealerGroup();
		dealerGroup.setId(id);
		// dealerGroup.setFieldCode("CHANNEL");

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(dealerGroup);
		RowMapper<DealerGroup> rowMapper = BeanPropertyRowMapper.newInstance(DealerGroup.class);

		try {
			return jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public boolean isIdExists(long id) {
		logger.debug("Entering");

		StringBuilder sql = new StringBuilder();
		sql.append(" Select COUNT(*) from CD_DealerGroup ");
		sql.append(" Where dealerGroupId = :dealerGroupId ");
		logger.debug("Sql: " + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("dealerGroupId", id);

		return this.jdbcTemplate.queryForObject(sql.toString(), source, Integer.class) > 0;
	}
}
