package com.pennant.backend.dao.settlementschedule.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.settlementschedule.SettlementScheduleDAO;
import com.pennant.backend.model.settlement.SettlementSchedule;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class SettlementScheduleDAOImpl extends SequenceDao<SettlementSchedule> implements SettlementScheduleDAO {
	private static Logger logger = LogManager.getLogger(SettlementScheduleDAOImpl.class);

	public SettlementScheduleDAOImpl() {
		super();
	}

	@Override
	public String save(SettlementSchedule settlementSchedule, String type) {
		logger.debug(Literal.ENTERING);

		if (settlementSchedule.getId() == 0 || settlementSchedule.getId() == Long.MIN_VALUE) {
			settlementSchedule.setId(getNextValue("SeqSettlementSchedule"));
			logger.debug("get NextValue:" + settlementSchedule.getId());
		}

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" insert into Settlement_Schedule");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append("(HeaderID, Id, SettlementInstalDate, SettlementAmount,");
		sql.append(" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		sql.append(" TaskId, NextTaskId, RecordType, WorkflowId )");
		sql.append("values( ");
		sql.append(":SettlementHeaderID, :SettlementDetailID, :SettlementInstalDate, :SettlementAmount,");
		sql.append(" :Version, :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode,");
		sql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId )");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(settlementSchedule);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(settlementSchedule.getSettlementDetailID());
	}

	@Override
	public void update(SettlementSchedule settlementSchedule, String type) {

		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update SETTLEMENT_SCHEDULE");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append("  set SettlementInstalDate = :SettlementInstalDate, SettlementAmount =:SettlementAmount,");
		sql.append(
				" Version =:Version, LastMntBy=:LastMntBy, LastMntOn=:LastMntOn, RecordStatus=:RecordStatus, RoleCode=:RoleCode, NextRoleCode=:NextRoleCode,");
		sql.append(" TaskId=:TaskId, NextTaskId=:NextTaskId, RecordType=:RecordType, WorkflowId =:WorkflowId");
		sql.append(" Where HeaderID = :SettlementHeaderID and ID = :SettlementDetailID");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(settlementSchedule);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);

	}

	@Override
	public List<SettlementSchedule> getSettlementScheduleDetails(long id, String type) {
		logger.debug("Entering");
		SettlementSchedule settlementSchedule = new SettlementSchedule();
		settlementSchedule.setSettlementHeaderID(id);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" HeaderID, ID, SettlementInstalDate, SettlementAmount,");
		sql.append(" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		sql.append(" TaskId, NextTaskId, RecordType, WorkflowId ");
		sql.append(" From SETTLEMENT_SCHEDULE");
		sql.append(type);
		sql.append(" Where HeaderID = :SettlementHeaderID");
		logger.debug("sql: " + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(settlementSchedule);
		RowMapper<SettlementSchedule> typeRowMapper = BeanPropertyRowMapper.newInstance(SettlementSchedule.class);

		try {
			return this.jdbcTemplate.query(sql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			settlementSchedule = null;
		}
		logger.debug("Leaving");
		return null;
	}

	@Override
	public void delete(SettlementSchedule settlementSchedule, String type) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from SETTLEMENT_SCHEDULE");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where HeaderID = :SettlementHeaderID and ID = :SettlementDetailID");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(settlementSchedule);
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

}
