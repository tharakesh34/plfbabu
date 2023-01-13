package com.pennant.backend.dao.applicationmaster.impl;

import org.apache.commons.lang.StringUtils;
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

import com.pennant.backend.dao.applicationmaster.SettlementTypeDetailDAO;
import com.pennant.backend.model.applicationmaster.SettlementTypeDetail;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

public class SettlementTypeDetailDAOImpl extends SequenceDao<SettlementTypeDetail> implements SettlementTypeDetailDAO {
	private static Logger logger = LogManager.getLogger(SettlementTypeDetailDAOImpl.class);

	public SettlementTypeDetailDAOImpl() {
		super();
	}

	@Override
	public long save(SettlementTypeDetail settlementTypeDetail, TableType tableType) {
		logger.debug(Literal.ENTERING);

		if (settlementTypeDetail.getId() == 0 || settlementTypeDetail.getId() == Long.MIN_VALUE) {
			settlementTypeDetail.setId(getNextValue("SeqSettlementTypes"));
		}

		settlementTypeDetail.setId(settlementTypeDetail.getId());

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("insert into Settlement_Types");
		sql.append(tableType.getSuffix());
		sql.append(" (Id, SettlementCode, SettlementDesc, AlwGracePeriod, Active,");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId,");
		sql.append(" RecordType, WorkflowId)");
		sql.append(" values(:Id, :SettlementCode, :SettlementDesc, :AlwGracePeriod, :Active, ");
		sql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId,");
		sql.append(" :NextTaskId, :RecordType, :WorkflowId)");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(settlementTypeDetail);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return settlementTypeDetail.getId();
	}

	@Override
	public void update(SettlementTypeDetail settlementTypeDetail, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL, ensure primary key will not be updated.
		StringBuilder sql = new StringBuilder("update Settlement_Types");
		sql.append(tableType.getSuffix());
		sql.append(" set SettlementDesc = :SettlementDesc, AlwGracePeriod = :AlwGracePeriod, Active=:Active,");
		sql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, ");
		sql.append(
				" RecordStatus= :RecordStatus, RoleCode = :RoleCode,NextRoleCode = :NextRoleCode, TaskId = :TaskId,");
		sql.append(" NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where ID = :ID and SettlementCode =:SettlementCode");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(settlementTypeDetail);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(SettlementTypeDetail settlementTypeDetail, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL;
		StringBuilder sql = new StringBuilder("delete from Settlement_Types");
		sql.append(tableType.getSuffix());
		sql.append(" where SettlementCode =:SettlementCode");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(settlementTypeDetail);
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
	public SettlementTypeDetail getSettlementByCode(String code, String type) {
		logger.debug(Literal.ENTERING);

		SettlementTypeDetail settlementTypeDetail = new SettlementTypeDetail();
		settlementTypeDetail.setSettlementCode(code);
		StringBuilder selectSql = new StringBuilder();

		selectSql.append("SELECT ID, SettlementCode, SettlementDesc, AlwGracePeriod, Active,");
		selectSql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId,");
		selectSql.append(" RecordType, WorkflowId");
		selectSql.append(" FROM  Settlement_Types");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where SettlementCode =:SettlementCode");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(settlementTypeDetail);
		RowMapper<SettlementTypeDetail> typeRowMapper = BeanPropertyRowMapper.newInstance(SettlementTypeDetail.class);

		try {
			settlementTypeDetail = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters,
					typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			settlementTypeDetail = null;
		}

		logger.debug(Literal.LEAVING);
		return settlementTypeDetail;
	}

	@Override
	public boolean isDuplicateKey(String settlementCode, long ID, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		String sql;
		String whereClause = "SettlementCode = :settlementCode and ID != ID";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("Settlement_Types", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("Settlement_Types_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "Settlement_Types_Temp", "Settlement_Types" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("settlementCode", settlementCode);
		paramSource.addValue("ID", ID);

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

	@Override
	public SettlementTypeDetail getSettlementById(long id, String type) {
		logger.debug(Literal.ENTERING);

		SettlementTypeDetail settlementTypeDetail = new SettlementTypeDetail();
		settlementTypeDetail.setId(id);

		StringBuilder selectSql = new StringBuilder();

		selectSql.append("Select ID, SettlementCode, SettlementDesc, AlwGracePeriod, Active,");
		selectSql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId,");
		selectSql.append(" RecordType, WorkflowId");
		selectSql.append(" FROM  Settlement_Types");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where id =:id");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(settlementTypeDetail);
		RowMapper<SettlementTypeDetail> typeRowMapper = BeanPropertyRowMapper.newInstance(SettlementTypeDetail.class);

		try {
			settlementTypeDetail = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters,
					typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			settlementTypeDetail = null;
		}

		logger.debug(Literal.LEAVING);
		return settlementTypeDetail;
	}

}
