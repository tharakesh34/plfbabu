package com.pennant.backend.dao.systemmasters.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.systemmasters.ProjectUnitsDAO;
import com.pennant.backend.model.systemmasters.ProjectUnits;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class ProjectUnitsDAOImpl extends SequenceDao<ProjectUnits> implements ProjectUnitsDAO {
	private static Logger logger = Logger.getLogger(ProjectUnitsDAOImpl.class);

	public ProjectUnitsDAOImpl() {
		super();
	}

	@Override
	public long save(ProjectUnits projectUnit, String tableType) {
		logger.debug(Literal.ENTERING);
		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" insert into ProjectUnits");
		sql.append(tableType);
		sql.append(" (UnitId, UnitType, Tower, FloorNumber, UnitNumber, UnitArea, Rate,");
		sql.append("Price, OtherCharges, TotalPrice, UnitRpsf, UnitPlotArea, UnitSuperBuiltUp,");
		sql.append("ProjectId, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, ");
		sql.append("NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId,");
		sql.append("UnitAreaConsidered, CarpetArea, UnitBuiltUpArea, RateConsidered, RateAsPerCarpetArea,");
		sql.append("RateAsPerBuiltUpArea, RateAsPerSuperBuiltUpArea, RateAsPerBranchAPF,");
		sql.append("RateAsPerCostSheet, FloorRiseCharges, OpenCarParkingCharges,");
		sql.append("ClosedCarParkingCharges, Gst, Remarks)");
		sql.append(" values(");
		sql.append(":Id, :UnitType, :Tower, :FloorNumber, :UnitNumber, :UnitArea, :Rate,");
		sql.append(":Price, :OtherCharges, :TotalPrice, :UnitRpsf, :UnitPlotArea, :UnitSuperBuiltUp,");
		sql.append(":ProjectId, :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId,");
		sql.append(":NextTaskId, :RecordType, :WorkflowId,");
		sql.append(":UnitAreaConsidered, :CarpetArea, :UnitBuiltUpArea, :RateConsidered, :RateAsPerCarpetArea,");
		sql.append(":RateAsPerBuiltUpArea, :RateAsPerSuperBuiltUpArea, :RateAsPerBranchAPF,");
		sql.append(":RateAsPerCostSheet, :FloorRiseCharges, :OpenCarParkingCharges,");
		sql.append(":ClosedCarParkingCharges, :Gst, :Remarks)");

		// Get the identity sequence number.
		if (projectUnit.getId() <= 0) {
			projectUnit.setId(getNextValue("SeqProjectUnit"));
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(projectUnit);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return projectUnit.getId();
	}

	@Override
	public void update(ProjectUnits projectUnit, String tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update ProjectUnits");
		sql.append(tableType);
		sql.append(" set UnitType= :UnitType, Tower= :Tower, FloorNumber= :FloorNumber, UnitNumber= :UnitNumber,");
		sql.append("UnitArea= :UnitArea, Rate= :Rate, Price= :Price, OtherCharges= :OtherCharges,");
		sql.append("TotalPrice= :TotalPrice, UnitRpsf= :UnitRpsf, UnitPlotArea= :UnitPlotArea,");
		sql.append(" UnitSuperBuiltUp= :UnitSuperBuiltUp, ProjectId= :ProjectId, Version = :Version,");
		sql.append(" LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId,");
		sql.append(
				" UnitAreaConsidered= :UnitAreaConsidered, CarpetArea= :CarpetArea, UnitBuiltUpArea= :UnitBuiltUpArea,");
		sql.append(" RateConsidered= :RateConsidered, RateAsPerCarpetArea= :RateAsPerCarpetArea,");
		sql.append(
				" RateAsPerBuiltUpArea= :RateAsPerBuiltUpArea, RateAsPerSuperBuiltUpArea= :RateAsPerSuperBuiltUpArea, RateAsPerBranchAPF= :RateAsPerBranchAPF,");

		sql.append(
				" RateAsPerCostSheet= :RateAsPerCostSheet, FloorRiseCharges= :FloorRiseCharges, OpenCarParkingCharges= :OpenCarParkingCharges,");
		sql.append(" ClosedCarParkingCharges= :ClosedCarParkingCharges, Gst= :Gst, Remarks= :Remarks");
		sql.append(" where UnitId = :Id ");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(projectUnit);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(ProjectUnits projectUnit, String tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from ProjectUnits");
		sql.append(tableType);
		sql.append(" where UnitId = :Id ");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(projectUnit);
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
	public ProjectUnits getProjectUnitsByID(long id, String type) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append("UnitId, UnitType, Tower, FloorNumber, UnitNumber, UnitArea, Rate");
		sql.append("Price, OtherCharges, TotalPrice, UnitRpsf, UnitPlotArea, UnitSuperBuiltUp, ProjectId,");
		sql.append("Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId,");
		sql.append("RecordType, WorkflowId,");
		sql.append("UnitAreaConsidered, CarpetArea, UnitBuiltUpArea, RateConsidered, RateAsPerCarpetArea,");
		sql.append("RateAsPerBuiltUpArea, RateAsPerSuperBuiltUpArea, RateAsPerBranchAPF,");
		sql.append("RateAsPerCostSheet, FloorRiseCharges, OpenCarParkingCharges,");
		sql.append("ClosedCarParkingCharges, Gst, Remarks,  From ProjectUnits");
		sql.append(type);
		sql.append(" Where UnitId = :Id");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		ProjectUnits projectUnit = new ProjectUnits();
		projectUnit.setId(id);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(projectUnit);
		RowMapper<ProjectUnits> rowMapper = BeanPropertyRowMapper.newInstance(ProjectUnits.class);

		try {
			projectUnit = jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			projectUnit = null;
		}

		logger.debug(Literal.LEAVING);
		return projectUnit;
	}

	@Override
	public List<ProjectUnits> getProjectUnitsByProjectID(long projectId, String type) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append("UnitId, UnitType, Tower, FloorNumber, UnitNumber, UnitArea, Rate,");
		sql.append("Price, OtherCharges, TotalPrice, UnitRpsf, UnitPlotArea, UnitSuperBuiltUp, ProjectId,");
		sql.append("Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId,");
		sql.append("RecordType, WorkflowId,");
		sql.append("UnitAreaConsidered, CarpetArea, UnitBuiltUpArea, RateConsidered, RateAsPerCarpetArea,");
		sql.append("RateAsPerBuiltUpArea, RateAsPerSuperBuiltUpArea, RateAsPerBranchAPF,");
		sql.append("RateAsPerCostSheet, FloorRiseCharges, OpenCarParkingCharges,");
		sql.append("ClosedCarParkingCharges, Gst, Remarks From ProjectUnits");
		sql.append(type);
		sql.append(" Where ProjectId = :ProjectId");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		ProjectUnits projectUnit = new ProjectUnits();
		projectUnit.setProjectId(projectId);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(projectUnit);
		List<ProjectUnits> projectUnits = null;
		RowMapper<ProjectUnits> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ProjectUnits.class);
		try {
			projectUnits = jdbcTemplate.query(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			projectUnits = null;
		}

		logger.debug(Literal.LEAVING);
		return projectUnits;
	}

}
