/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : BuilderCompanyDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 22-05-2017 * *
 * Modified Date : 22-05-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 22-05-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.systemmasters.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.systemmasters.BuilderCompanyDAO;
import com.pennant.backend.model.systemmasters.BuilderCompany;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * Data access layer implementation for <code>BuilderCompany</code> with set of CRUD operations.
 */
public class BuilderCompanyDAOImpl extends SequenceDao<BuilderCompany> implements BuilderCompanyDAO {
	private static Logger logger = LogManager.getLogger(BuilderCompanyDAOImpl.class);

	public BuilderCompanyDAOImpl() {
		super();
	}

	@Override
	public BuilderCompany getBuilderCompany(long id, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Id, Name, Segmentation, CustId, GroupId, ApfType, PeDevId, EntityType, EmailId");
		sql.append(", CityType, Address1, Address2, Address3, City, State, Code, Devavailablity, Magnitude");
		sql.append(", Absavailablity, TotalProj, Approved, Remarks, PanDetails, BenfName, AccountNo");
		sql.append(", BankBranchId, LimitOnAmt, LimitOnUnits, CurrentExpUni, CurrentExpAmt, DateOfInCop");
		sql.append(", NoOfProj, AssHLPlayers, OnGoingProj, ExpInBusiness, Recommendation, MagintudeInLacs");
		sql.append(", NoOfProjCons, PinCodeId");

		if (type.contains("View")) {
			sql.append(", SegmentationName, GroupIdName, FieldCode, CityName");
			sql.append(", CodeName, BranDesc, EntyDesc, BankName, Ifsc, AreaName, LovDescCIFName, CustCIF, Active");
		}

		sql.append(", Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" from BuilderCompany");
		sql.append(type);
		sql.append(" Where id = ?");

		Object[] object = new Object[] { id };

		if (type.contains("View")) {
			sql.append(" and FieldCode = ?");

			object = new Object[] { id, "SEGMENT" };
		}

		logger.trace(Literal.SQL + sql.toString());

		try {

			return this.jdbcOperations.queryForObject(sql.toString(), new RowMapper<BuilderCompany>() {
				@Override
				public BuilderCompany mapRow(ResultSet rs, int rowNum) throws SQLException {
					BuilderCompany ca = new BuilderCompany();

					ca.setId(rs.getLong("Id"));
					ca.setName(rs.getString("Name"));
					ca.setSegmentation(rs.getString("Segmentation"));
					ca.setCustId(JdbcUtil.getLong(rs.getObject("CustId")));
					ca.setGroupId(rs.getLong("GroupId"));
					ca.setApfType(rs.getString("ApfType"));
					ca.setPeDevId(rs.getString("PeDevId"));
					ca.setEntityType(rs.getString("EntityType"));
					ca.setEmailId(rs.getString("EmailId"));
					ca.setCityType(rs.getString("CityType"));
					ca.setAddress1(rs.getString("Address1"));
					ca.setAddress2(rs.getString("Address2"));
					ca.setAddress3(rs.getString("Address3"));
					ca.setCity(rs.getString("City"));
					ca.setState(rs.getString("State"));
					ca.setCode(rs.getString("Code"));
					ca.setDevavailablity(rs.getBigDecimal("Devavailablity"));
					ca.setMagnitude(rs.getBigDecimal("Magnitude"));
					ca.setAbsavailablity(rs.getBigDecimal("Absavailablity"));
					ca.setTotalProj(rs.getBigDecimal("TotalProj"));
					ca.setApproved(rs.getString("Approved"));
					ca.setRemarks(rs.getString("Remarks"));
					ca.setPanDetails(rs.getString("PanDetails"));
					ca.setBenfName(rs.getString("BenfName"));
					ca.setAccountNo(rs.getString("AccountNo"));
					ca.setBankBranchId(JdbcUtil.getLong(rs.getObject("BankBranchId")));
					ca.setLimitOnAmt(rs.getBigDecimal("LimitOnAmt"));
					ca.setLimitOnUnits(rs.getBigDecimal("LimitOnUnits"));
					ca.setCurrentExpUni(rs.getBigDecimal("CurrentExpUni"));
					ca.setCurrentExpAmt(rs.getBigDecimal("CurrentExpAmt"));
					ca.setDateOfInCop(rs.getTimestamp("DateOfInCop"));
					ca.setNoOfProj(rs.getBigDecimal("NoOfProj"));
					ca.setAssHLPlayers(rs.getBigDecimal("AssHLPlayers"));
					ca.setOnGoingProj(rs.getBigDecimal("OnGoingProj"));
					ca.setExpInBusiness(rs.getBigDecimal("ExpInBusiness"));
					ca.setRecommendation(rs.getString("Recommendation"));
					ca.setMagintudeInLacs(rs.getBigDecimal("MagintudeInLacs"));
					ca.setNoOfProjCons(rs.getBigDecimal("NoOfProjCons"));
					ca.setPinCodeId(JdbcUtil.getLong(rs.getObject("PinCodeId")));

					if (type.contains("View")) {
						ca.setSegmentationName(rs.getString("SegmentationName"));
						ca.setGroupIdName(rs.getString("GroupIdName"));
						ca.setFieldCode(rs.getString("FieldCode"));
						ca.setCityName(rs.getString("CityName"));
						ca.setCodeName(rs.getString("CodeName"));
						ca.setBranDesc(rs.getString("BranDesc"));
						ca.setEntyDesc(rs.getString("EntyDesc"));
						ca.setBankName(rs.getString("BankName"));
						ca.setIfsc(rs.getString("Ifsc"));
						ca.setAreaName(rs.getString("AreaName"));
						ca.setLovDescCIFName(rs.getString("LovDescCIFName"));
						ca.setCustCIF(rs.getString("CustCIF"));
						ca.setActive(rs.getBoolean("Active"));
					}

					ca.setVersion(rs.getInt("Version"));
					ca.setLastMntOn(rs.getTimestamp("LastMntOn"));
					ca.setLastMntBy(rs.getLong("LastMntBy"));
					ca.setRecordStatus(rs.getString("RecordStatus"));
					ca.setRoleCode(rs.getString("RoleCode"));
					ca.setNextRoleCode(rs.getString("NextRoleCode"));
					ca.setTaskId(rs.getString("TaskId"));
					ca.setNextTaskId(rs.getString("NextTaskId"));
					ca.setRecordType(rs.getString("RecordType"));
					ca.setWorkflowId(rs.getLong("WorkflowId"));

					return ca;
				}
			}, object);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public boolean isDuplicateKey(long id, String name, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		String sql;
		String whereClause = "name = :name AND id != :id";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("BuilderCompany", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("BuilderCompany_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "BuilderCompany_Temp", "BuilderCompany" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("id", id);
		paramSource.addValue("name", name);
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
	public String save(BuilderCompany builderCompany, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" insert into BuilderCompany");
		sql.append(tableType.getSuffix());
		sql.append(" (id, name, segmentation, CustId , groupId, apfType, peDevId, entityType");
		sql.append(", emailId, cityType, address1, address2, address3, city, state, code, devavailablity");
		sql.append(", magnitude, absavailablity, totalProj, approved, remarks, panDetails, benfName");
		sql.append(", accountNo, bankBranchId, limitOnAmt, limitOnUnits, currentExpUni, currentExpAmt");
		sql.append(", dateOfInCop, noOfProj, assHLPlayers, onGoingProj, expInBusiness, recommendation");
		sql.append(", magintudeInLacs, noOfProjCons, active, pinCodeId");
		sql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId");
		sql.append(", NextTaskId, RecordType, WorkflowId)");
		sql.append("  values(");
		sql.append(" :id, :name, :segmentation, :CustId ,:groupId, :apfType, :peDevId, :entityType");
		sql.append(", :emailId, :cityType, :address1, :address2, :address3, :city, :state, :code, :devavailablity");
		sql.append(", :magnitude, :absavailablity, :totalProj, :approved, :remarks, :panDetails");
		sql.append(", :benfName, :accountNo, :bankBranchId, :limitOnAmt, :limitOnUnits, :currentExpUni");
		sql.append(", :currentExpAmt, :dateOfInCop, :noOfProj, :assHLPlayers, :onGoingProj, :expInBusiness");
		sql.append(", :recommendation, :magintudeInLacs, :noOfProjCons, :active, :pinCodeId");
		sql.append(", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode");
		sql.append(", :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		// Get the identity sequence number.
		if (builderCompany.getId() <= 0) {
			builderCompany.setId(getNextValue("SeqBuilderCompany"));
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(builderCompany);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(builderCompany.getId());
	}

	@Override
	public void update(BuilderCompany builderCompany, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update BuilderCompany");
		sql.append(tableType.getSuffix());
		sql.append("  set name = :name, segmentation = :segmentation,CustId = :CustId , groupId = :groupId");
		sql.append(", apfType= :apfType, peDevId= :peDevId, entityType= :entityType");
		sql.append(", emailId= :emailId, cityType= :cityType, address1 = :address1, address2= :address2");
		sql.append(", address3= address3, city= :city, state= :state, code= :code, devavailablity= :devavailablity");
		sql.append(", magnitude= :magnitude, absavailablity= :absavailablity, totalProj= :totalProj");
		sql.append(", approved= :approved, remarks= :remarks, panDetails= :panDetails, benfName= :benfName");
		sql.append(", accountNo= :accountNo, bankBranchId= :bankBranchId, limitOnAmt= :limitOnAmt");
		sql.append(", limitOnUnits= :limitOnUnits, currentExpUni= :currentExpUni");
		sql.append(", currentExpAmt= :currentExpAmt, dateOfInCop= :dateOfInCop, noOfProj= :noOfProj");
		sql.append(", assHLPlayers= :assHLPlayers, onGoingProj= :onGoingProj, expInBusiness= :expInBusiness");
		sql.append(", recommendation= :recommendation, magintudeInLacs= :magintudeInLacs");
		sql.append(", noOfProjCons= :noOfProjCons, pinCodeId = :pinCodeId");
		sql.append(", LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode");
		sql.append(", NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, active = :active");
		sql.append(", RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where id = :id ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(builderCompany);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(BuilderCompany builderCompany, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from BuilderCompany");
		sql.append(tableType.getSuffix());
		sql.append(" where id = :id ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(builderCompany);
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
	public boolean isIdExists(long id) {
		logger.debug("Entering");

		StringBuilder sql = new StringBuilder();
		sql.append(" Select COUNT(*) from BuilderProjcet ");
		sql.append(" Where BuilderId = :BuilderId ");
		logger.debug("Sql: " + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("BuilderId", id);

		return this.jdbcTemplate.queryForObject(sql.toString(), source, Integer.class) > 0;
	}
}