package com.pennant.backend.dao.systemmasters.impl;

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

import com.pennant.backend.dao.systemmasters.DistrictDAO;
import com.pennant.backend.model.systemmasters.District;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

public class DistrictDAOImpl extends SequenceDao<District> implements DistrictDAO {
	private static Logger logger = LogManager.getLogger(DistrictDAOImpl.class);

	public DistrictDAOImpl() {
		super();
	}

	/**
	 * This method insert new Records into RMTDistricts or RMTDistricts_Temp.
	 * 
	 * save District
	 * 
	 * @param District (district)
	 * @param type     (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public String save(District dis, final TableType tableType) {
		StringBuilder sql = new StringBuilder("Insert Into RMTDistricts");
		sql.append(tableType.getSuffix());
		sql.append(" (Id, Code, Name, HostReferenceNo, Active,");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		sql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values(?, ?, ?, ?, ?, ? , ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		if (dis.getId() == Long.MIN_VALUE) {
			dis.setId(getNextValue("SeqRmtDistricts"));
		}

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			this.jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index++, dis.getId());
				ps.setString(index++, dis.getCode());
				ps.setString(index++, dis.getName());
				ps.setString(index++, dis.getHostReferenceNo());
				ps.setBoolean(index++, dis.isActive());
				ps.setInt(index++, dis.getVersion());
				ps.setLong(index++, dis.getLastMntBy());
				ps.setTimestamp(index++, dis.getLastMntOn());
				ps.setString(index++, dis.getRecordStatus());
				ps.setString(index++, dis.getRoleCode());
				ps.setString(index++, dis.getNextRoleCode());
				ps.setString(index++, dis.getTaskId());
				ps.setString(index++, dis.getNextTaskId());
				ps.setString(index++, dis.getRecordType());
				ps.setLong(index, dis.getWorkflowId());
			});

		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		return String.valueOf(dis.getId());
	}

	/**
	 * This method updates the Record RMTDistricts or RMTDistricts_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update District by key
	 * 
	 * @param District (District)
	 * @param type     (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(final District district, final TableType tableType) {
		logger.debug(Literal.ENTERING);

		int recordCount = 0;
		StringBuilder updateSql = new StringBuilder("Update RMTDistricts");
		updateSql.append(tableType.getSuffix());
		updateSql.append(" Set Name = :Name, HostReferenceNo = :HostReferenceNo, Active = :Active");
		updateSql.append(" , Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn");
		updateSql.append(" , RecordStatus= :RecordStatus, RoleCode = :RoleCode");
		updateSql.append(" , NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId");
		updateSql.append(" , RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where Id =:Id");
		updateSql.append(QueryUtil.getConcurrencyCondition(tableType));

		logger.trace(Literal.SQL + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(district);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * This method Deletes the Record from the RMTDistricts or RMTDistricts_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete District by key PCCountry
	 * 
	 * @param District (district)
	 * @param type     (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(District district, final TableType tableType) {
		logger.debug(Literal.ENTERING);

		int recordCount = 0;
		StringBuilder deleteSql = new StringBuilder(" Delete From RMTDistricts");
		deleteSql.append(tableType.getSuffix());
		deleteSql.append(" Where Id =:Id ");
		deleteSql.append(QueryUtil.getConcurrencyCondition(tableType));

		logger.trace(Literal.SQL + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(district);

		try {
			recordCount = this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public boolean isDuplicateKey(final String code, final TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		String sql;
		String whereClause = "Code =:Code";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("RMTDistricts", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("RMTDistricts_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "RMTDistricts_Temp", "RMTDistricts" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("Code", code);

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}
		logger.debug(Literal.LEAVING);
		return exists;
	}

	@Override
	public District getDistrictById(long Id, final String type) {
		logger.debug("Entering");

		District district = new District();
		district.setId(Id);
		StringBuilder selectSql = new StringBuilder();

		selectSql.append("SELECT Id, Code, Name, HostReferenceNo, Active, Version, LastMntOn, LastMntBy, RecordStatus");
		selectSql.append(" , RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" FROM  RMTDistricts");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where Id =:Id");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(district);
		RowMapper<District> typeRowMapper = BeanPropertyRowMapper.newInstance(District.class);

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public District getDistrictByCity(String cityCode) {
		logger.debug("Entering");

		StringBuilder selectSql = new StringBuilder();
		selectSql.append("SELECT Id, Code, Name, HostReferenceNo, Active, ");
		selectSql.append(
				" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" FROM  RMTDistricts");
		selectSql.append(" Where Code IN (SELECT Code FROM RMTProvinceVsCity WHERE PCCity=:PCCity)");
		logger.debug("selectSql: " + selectSql.toString());
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("PCCity", cityCode);
		RowMapper<District> typeRowMapper = BeanPropertyRowMapper.newInstance(District.class);

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), paramMap, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public boolean isExistDistrictCode(long id) {
		String sql = "Select Count(DistrictId) From RMTProvinceVSCity Where DistrictId = ?";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.queryForObject(sql, Integer.class, id) > 0;
	}
}
