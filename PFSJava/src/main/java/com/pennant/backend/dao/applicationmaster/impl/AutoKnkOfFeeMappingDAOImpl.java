package com.pennant.backend.dao.applicationmaster.impl;

import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import com.pennant.backend.dao.applicationmaster.AutoKnkOfFeeMappingDAO;
import com.pennant.backend.model.finance.AutoKnockOffFeeMapping;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

public class AutoKnkOfFeeMappingDAOImpl extends SequenceDao<AutoKnockOffFeeMapping> implements AutoKnkOfFeeMappingDAO {
	private static Logger logger = LogManager.getLogger(AutoKnkOfFeeMappingDAOImpl.class);

	public AutoKnkOfFeeMappingDAOImpl() {
		super();
	}

	@Override
	public AutoKnockOffFeeMapping getKnockOffMappingByID(AutoKnockOffFeeMapping feeMapping, TableType tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select KnockOffId, FeeTypeId, FeeOrder, Version, LastMntBy");
		sql.append(", LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType");
		sql.append(", WorkflowId");
		sql.append(" FROM Auto_KnockOff_Fee_Types");
		sql.append(StringUtils.trimToEmpty(tableType.getSuffix()));
		sql.append(" Where Id = :Id And KnockOffId = :KnockOffId");

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(feeMapping);
		RowMapper<AutoKnockOffFeeMapping> typeRowMapper = BeanPropertyRowMapper
				.newInstance(AutoKnockOffFeeMapping.class);
		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Auto Knock Mapping details not found  with Id {} and KnockId {}", feeMapping.getId(),
					feeMapping.getKnockOffId());
		}

		logger.debug(Literal.LEAVING);

		return null;
	}

	@Override
	public List<AutoKnockOffFeeMapping> getKnockOffMappingListByPayableName(long knockOffId, TableType tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select Id, KnockOffId, FeeTypeId, FeeOrder, Version");
		sql.append(", LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(", RecordType, WorkflowId");
		sql.append(" FROM Auto_KnockOff_Fee_Types");
		sql.append(StringUtils.trimToEmpty(tableType.getSuffix()));
		sql.append(" Where KnockOffId = :KnockOffId ");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("KnockOffId", knockOffId);

		RowMapper<AutoKnockOffFeeMapping> typeRowMapper = BeanPropertyRowMapper
				.newInstance(AutoKnockOffFeeMapping.class);

		logger.debug(Literal.LEAVING);

		return this.jdbcTemplate.query(sql.toString(), mapSqlParameterSource, typeRowMapper);
	}

	@Override
	public void update(AutoKnockOffFeeMapping feeMapping, TableType tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Update Auto_KnockOff_Fee_Types");
		sql.append(StringUtils.trimToEmpty(tableType.getSuffix()));
		sql.append(" Set Id = :Id, KnockOffId = :KnockOffId, FeeTypeId = :FeeTypeId, FeeOrder = :FeeOrder");
		sql.append(", Version = :Version, LastMntBy = :LastMntBy, LastMntOn = :LastMntOn");
		sql.append(", RecordStatus = :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode");
		sql.append(", TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" Where FeeTypeId = :FeeTypeId And Id = :Id");

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(feeMapping);
		if (this.jdbcTemplate.update(sql.toString(), beanParameters) < 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public long save(AutoKnockOffFeeMapping feeMapping, TableType tableType) {
		logger.debug(Literal.ENTERING);

		if (feeMapping.getId() == Long.MIN_VALUE) {
			feeMapping.setId(getNextValue("SeqAuto_KnockOff_Fee_Types"));
		}

		StringBuilder sql = new StringBuilder("Insert Into Auto_KnockOff_Fee_Types");
		sql.append(StringUtils.trimToEmpty(tableType.getSuffix()));
		sql.append(" (Id, KnockOffId, FeeTypeId, FeeOrder, Version, LastMntBy, LastMntOn, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values(");
		sql.append(":Id, :KnockOffId, :FeeTypeId, :FeeOrder, :Version , :LastMntBy, :LastMntOn, :RecordStatus");
		sql.append(", :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(feeMapping);
		this.jdbcTemplate.update(sql.toString(), beanParameters);

		logger.debug(Literal.LEAVING);

		return feeMapping.getId();
	}

	@Override
	public void delete(AutoKnockOffFeeMapping feeMapping, TableType tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Delete From Auto_KnockOff_Fee_Types");
		sql.append(StringUtils.trimToEmpty(tableType.getSuffix()));
		sql.append(" Where FeeTypeId = :FeeTypeId And Id = :Id");

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(feeMapping);
		try {
			this.jdbcTemplate.update(sql.toString(), beanParameters);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void deleteByPayableType(String feeTypeCode, TableType tableType) {
		logger.debug(Literal.ENTERING);

		AutoKnockOffFeeMapping feeMapping = new AutoKnockOffFeeMapping();
		feeMapping.setFeeTypeCode(feeTypeCode);

		StringBuilder sql = new StringBuilder("Delete From AutoKnockOffFeeMapping");
		sql.append(StringUtils.trimToEmpty(tableType.getSuffix()));
		sql.append(" Where FeeTypeId =:FeeTypeId");

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(feeMapping);
		try {
			this.jdbcTemplate.update(sql.toString(), beanParameters);
		} catch (DataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(long knockOffId, TableType tableType) {
		logger.debug(Literal.ENTERING);

		AutoKnockOffFeeMapping feeMapping = new AutoKnockOffFeeMapping();
		feeMapping.setKnockOffId(knockOffId);

		StringBuilder sql = new StringBuilder("Delete From Auto_KnockOff_Fee_Types");
		sql.append(StringUtils.trimToEmpty(tableType.getSuffix()));
		sql.append(" Where KnockOffId =:KnockOffId");

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(feeMapping);
		this.jdbcTemplate.update(sql.toString(), beanParameters);

		logger.debug(Literal.LEAVING);
	}

	@Override
	public boolean isDuplicatefeeTypeId(long id, int feeTypeId, TableType tableType) {
		logger.debug(Literal.ENTERING);

		String sql;
		String whereClause = "FeeTypeId = :FeeTypeId AND Id != :Id";
		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("Auto_KnockOff_Fee_Types", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("Auto_KnockOff_Fee_Types_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "Auto_KnockOff_Fee_Types_Temp", "Auto_KnockOff_Fee_Types" },
					whereClause);
			break;
		}
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);

		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("Id", id);
		paramSource.addValue("FeeTypeId", feeTypeId);

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

	@Override
	public boolean isDuplicatefeeTypeOrder(long id, int feeOrder, TableType tableType) {
		logger.debug(Literal.ENTERING);

		String sql;
		String whereClause = "FeeTypeId = :FeeTypeId AND Id != :Id";
		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("Auto_KnockOff_Fee_Types", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("Auto_KnockOff_Fee_Types_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "Auto_KnockOff_Fee_Types_Temp", "Auto_KnockOff_Fee_Types" },
					whereClause);
			break;
		}
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);

		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("Id", id);
		paramSource.addValue("FeeOrder", feeOrder);

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);

		return exists;
	}

}
