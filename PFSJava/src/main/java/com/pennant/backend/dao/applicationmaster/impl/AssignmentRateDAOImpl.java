package com.pennant.backend.dao.applicationmaster.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.applicationmaster.AssignmentRateDAO;
import com.pennant.backend.model.applicationmaster.AssignmentRate;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class AssignmentRateDAOImpl extends SequenceDao<AssignmentRate> implements AssignmentRateDAO {
	private static Logger logger = LogManager.getLogger(AssignmentRateDAOImpl.class);

	@Override
	public AssignmentRate getAssignmentRateById(long id, String type) {
		logger.debug(Literal.ENTERING);
		AssignmentRate assignmentRate = null;
		StringBuilder sql = new StringBuilder();

		sql.append(" select * from ASSIGNMENTRATES").append(type);
		sql.append(" where id = :id");

		RowMapper<AssignmentRate> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(AssignmentRate.class);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("id", id);

		try {
			assignmentRate = this.jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return assignmentRate;
	}

	@Override
	public void update(AssignmentRate assignmentRate, String type) {
		logger.debug(Literal.ENTERING);

		int recordCount = 0;
		StringBuilder sql = new StringBuilder("Update ASSIGNMENTRATES");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set AssignmentId= :AssignmentId, EffectiveDate = :EffectiveDate, MclrRate=:MclrRate, ");
		sql.append(" BankSpreadRate = :BankSpreadRate, OpexRate = :OpexRate, resetfrequency = :resetFrequency,");
		sql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		sql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode,");
		sql.append(" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" Where id = :id");

		if (!type.endsWith("_Temp")) {
			sql.append("  AND Version= :Version-1");
		}

		logger.debug("Sql: " + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(assignmentRate);
		recordCount = this.jdbcTemplate.update(sql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(AssignmentRate assignmentRate, String type) {

		logger.debug(Literal.ENTERING);
		int recordCount = 0;

		StringBuilder sql = new StringBuilder("Delete From ASSIGNMENTRATES");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where Id = :Id");

		logger.debug("sql: " + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(assignmentRate);
		try {
			recordCount = this.jdbcTemplate.update(sql.toString(), beanParameters);
			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public long save(AssignmentRate assignmentRate, String type) {
		logger.debug(Literal.ENTERING);
		if (assignmentRate.getId() == Long.MIN_VALUE) {
			assignmentRate.setId(getNextValue("seqAssignmentRate"));
		}
		StringBuilder sql = new StringBuilder();
		sql.append("insert into ASSIGNMENTRATES");
		sql.append(type);
		sql.append("(id, assignmentid, effectivedate, mclrrate, bankspreadrate, opexrate, resetfrequency,");
		sql.append(" version, lastmntby, lastmnton, recordstatus,");
		sql.append(" rolecode, nextrolecode, taskid, nexttaskid, recordtype, workflowid)");
		sql.append(" values(");
		sql.append(" :id, :assignmentId, :effectiveDate, :mclrRate, :bankSpreadRate, :opexRate, :resetFrequency,");
		sql.append(" :version, :lastMntBy, :lastMntOn, :recordStatus,");
		sql.append(" :roleCode, :nextRoleCode, :taskId, :nextTaskId, :recordType, :workflowId)");

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(assignmentRate);

		logger.trace(Literal.SQL + sql.toString());
		this.jdbcTemplate.update(sql.toString(), beanParameters);

		logger.debug(Literal.LEAVING);
		return assignmentRate.getId();
	}

	@Override
	public List<AssignmentRate> getAssignmentRatesByAssignmentId(long assignmentId, String type) {
		logger.debug(Literal.ENTERING);
		StringBuilder sql = new StringBuilder();
		sql.append(" select * from ASSIGNMENTRATES").append(type);
		sql.append(" where assignmentid = :assignmentId");
		logger.trace(Literal.SQL + sql.toString());

		AssignmentRate assignmentRate = new AssignmentRate();
		assignmentRate.setAssignmentId(assignmentId);

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(assignmentRate);
		RowMapper<AssignmentRate> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(AssignmentRate.class);
		try {
			logger.debug(Literal.LEAVING);
			return jdbcTemplate.query(sql.toString(), beanParameters, typeRowMapper);
		} catch (DataAccessException e) {
			return new ArrayList<>();
		}
	}

	@Override
	public void deleteByAssignmentId(long assignmentId, String type) {
		logger.debug(Literal.ENTERING);
		StringBuilder sql = new StringBuilder("Delete From ASSIGNMENTRATES");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where assignmentid = :assignmentid");

		logger.debug("deleteSql: " + sql.toString());
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("assignmentid", assignmentId);
		this.jdbcTemplate.update(sql.toString(), source);
		logger.debug(Literal.LEAVING);
	}

}
