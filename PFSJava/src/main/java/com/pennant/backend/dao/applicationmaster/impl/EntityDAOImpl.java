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
 * * FileName : EntityDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 15-06-2017 * * Modified Date :
 * 15-06-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 15-06-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.applicationmaster.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;

import com.pennant.backend.dao.applicationmaster.EntityDAO;
import com.pennant.backend.model.applicationmaster.Entity;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * Data access layer implementation for <code>Entity</code> with set of CRUD operations.
 */
public class EntityDAOImpl extends BasicDao<Entity> implements EntityDAO {

	public EntityDAOImpl() {
		super();
	}

	@Override
	public Entity getEntity(String entityCode, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" EntityCode, EntityDesc, PANNumber, Country, StateCode, CityCode, PinCode, EntityAddrLine1");
		sql.append(", EntityAddrLine2, EntityAddrHNbr, EntityFlatNbr, EntityAddrStreet, EntityPOBox");
		sql.append(", Active, GstinAvailable, Version, LastMntOn, LastMntBy, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId, CINNumber, PinCodeId");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", CountryName, ProvinceName, CityName, PinCodeName");
		}

		sql.append(" From Entity");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where EntityCode = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				Entity e = new Entity();

				e.setEntityCode(rs.getString("EntityCode"));
				e.setEntityDesc(rs.getString("EntityDesc"));
				e.setPANNumber(rs.getString("PANNumber"));
				e.setCountry(rs.getString("Country"));
				e.setStateCode(rs.getString("StateCode"));
				e.setCityCode(rs.getString("CityCode"));
				e.setPinCode(rs.getString("PinCode"));
				e.setEntityAddrLine1(rs.getString("EntityAddrLine1"));
				e.setEntityAddrLine2(rs.getString("EntityAddrLine2"));
				e.setEntityAddrHNbr(rs.getString("EntityAddrHNbr"));
				e.setEntityFlatNbr(rs.getString("EntityFlatNbr"));
				e.setEntityAddrStreet(rs.getString("EntityAddrStreet"));
				e.setEntityPOBox(rs.getString("EntityPOBox"));
				e.setActive(rs.getBoolean("Active"));
				e.setGstinAvailable(rs.getBoolean("GstinAvailable"));
				e.setVersion(rs.getInt("Version"));
				e.setLastMntOn(rs.getTimestamp("LastMntOn"));
				e.setLastMntBy(rs.getLong("LastMntBy"));
				e.setRecordStatus(rs.getString("RecordStatus"));
				e.setRoleCode(rs.getString("RoleCode"));
				e.setNextRoleCode(rs.getString("NextRoleCode"));
				e.setTaskId(rs.getString("TaskId"));
				e.setNextTaskId(rs.getString("NextTaskId"));
				e.setRecordType(rs.getString("RecordType"));
				e.setWorkflowId(rs.getLong("WorkflowId"));
				e.setcINNumber(rs.getString("CINNumber"));
				e.setPinCodeId(JdbcUtil.getLong(rs.getObject("PinCodeId")));

				if (StringUtils.trimToEmpty(type).contains("View")) {
					e.setCountryName(rs.getString("CountryName"));
					e.setProvinceName(rs.getString("ProvinceName"));
					e.setCityName(rs.getString("CityName"));
					e.setPinCodeName(rs.getString("PinCodeName"));
				}

				return e;
			}, entityCode);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public String save(Entity entity, TableType tableType) {
		StringBuilder sql = new StringBuilder("Insert into Entity");
		sql.append(tableType.getSuffix());
		sql.append("(EntityCode, EntityDesc, PanNumber, Country, StateCode, CityCode");
		sql.append(", PinCode, EntityAddrLine1, EntityAddrLine2, EntityAddrHNbr, EntityFlatNbr");
		sql.append(", EntityAddrStreet, EntityPOBox, Active, GstInAvailable, PinCodeId, CINNumber");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values( ?, ?, ?, ?, ?, ?,  ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? , ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setString(index++, entity.getEntityCode());
				ps.setString(index++, entity.getEntityDesc());
				ps.setString(index++, entity.getPANNumber());
				ps.setString(index++, entity.getCountry());
				ps.setString(index++, entity.getStateCode());
				ps.setString(index++, entity.getCityCode());
				ps.setString(index++, entity.getPinCode());
				ps.setString(index++, entity.getEntityAddrLine1());
				ps.setString(index++, entity.getEntityAddrLine2());
				ps.setString(index++, entity.getEntityAddrHNbr());
				ps.setString(index++, entity.getEntityFlatNbr());
				ps.setString(index++, entity.getEntityAddrStreet());
				ps.setString(index++, entity.getEntityPOBox());
				ps.setBoolean(index++, entity.isActive());
				ps.setBoolean(index++, entity.isGstinAvailable());
				ps.setLong(index++, entity.getPinCodeId());
				ps.setString(index++, entity.getcINNumber());
				ps.setInt(index++, entity.getVersion());
				ps.setLong(index++, entity.getLastMntBy());
				ps.setTimestamp(index++, entity.getLastMntOn());
				ps.setString(index++, entity.getRecordStatus());
				ps.setString(index++, entity.getRoleCode());
				ps.setString(index++, entity.getNextRoleCode());
				ps.setString(index++, entity.getTaskId());
				ps.setString(index++, entity.getNextTaskId());
				ps.setString(index++, entity.getRecordType());
				ps.setLong(index, entity.getWorkflowId());
			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		return String.valueOf(entity.getEntityCode());
	}

	@Override
	public void update(Entity entity, TableType tableType) {
		StringBuilder sql = new StringBuilder("Update Entity");
		sql.append(tableType.getSuffix());
		sql.append(" Set EntityDesc = ?, PANNumber = ?, Country = ?");
		sql.append(", StateCode = ?, CityCode = ?, PinCode = ?, EntityAddrLine1 = ?, EntityAddrLine2 = ?");
		sql.append(", EntityAddrHNbr = ?, EntityFlatNbr = ?, EntityAddrStreet = ?, EntityPOBox = ?");
		sql.append(", GSTINAvailable = ?, PinCodeId = ?, CINNumber = ?");
		sql.append(", Active = ?, Version = ?, LastMntOn = ?, LastMntBy = ?, RecordStatus = ?");
		sql.append(", RoleCode = ?, NextRoleCode = ?, TaskId = ?, NextTaskId = ?");
		sql.append(", RecordType = ?, WorkflowId = ?");
		sql.append(" Where EntityCode = ?");
		sql.append(QueryUtil.getConcurrencyClause(tableType));

		logger.debug(Literal.SQL.concat(sql.toString()));

		int recordCount = jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, entity.getEntityDesc());
			ps.setString(index++, entity.getPANNumber());
			ps.setString(index++, entity.getCountry());
			ps.setString(index++, entity.getStateCode());
			ps.setString(index++, entity.getCityCode());
			ps.setString(index++, entity.getPinCode());
			ps.setString(index++, entity.getEntityAddrLine1());
			ps.setString(index++, entity.getEntityAddrLine2());
			ps.setString(index++, entity.getEntityAddrHNbr());
			ps.setString(index++, entity.getEntityFlatNbr());
			ps.setString(index++, entity.getEntityAddrStreet());
			ps.setString(index++, entity.getEntityPOBox());
			ps.setBoolean(index++, entity.isGstinAvailable());
			ps.setLong(index++, entity.getPinCodeId());
			ps.setString(index++, entity.getcINNumber());
			ps.setBoolean(index++, entity.isActive());
			ps.setInt(index++, entity.getVersion());
			ps.setTimestamp(index++, entity.getLastMntOn());
			ps.setLong(index++, entity.getLastMntBy());
			ps.setString(index++, entity.getRecordStatus());
			ps.setString(index++, entity.getRoleCode());
			ps.setString(index++, entity.getNextRoleCode());
			ps.setString(index++, entity.getTaskId());
			ps.setString(index++, entity.getNextTaskId());
			ps.setString(index++, entity.getRecordType());
			ps.setLong(index++, entity.getWorkflowId());

			ps.setString(index++, entity.getEntityCode());

			if (tableType == TableType.TEMP_TAB) {
				ps.setTimestamp(index, entity.getPrevMntOn());
			} else {
				ps.setInt(index, entity.getVersion() - 1);
			}
		});

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public void delete(Entity entity, TableType tableType) {
		StringBuilder sql = new StringBuilder("Delete From Entity");
		sql.append(tableType.getSuffix());
		sql.append(" Where entityCode = ?");
		sql.append(QueryUtil.getConcurrencyClause(tableType));

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			int recordCount = jdbcOperations.update(sql.toString(), ps -> {
				ps.setString(1, entity.getEntityCode());

				if (tableType == TableType.TEMP_TAB) {
					ps.setTimestamp(2, entity.getPrevMntOn());
				} else {
					ps.setInt(2, entity.getVersion() - 1);
				}
			});

			if (recordCount == 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	@Override
	public boolean count(String entityCode, String pANNumber, TableType tableType) {
		String sql;
		String whereClause = null;

		Object[] obj = new Object[] {};
		if (StringUtils.isNotBlank(entityCode)) {
			whereClause = "EntityCode = ?";
			obj = new Object[] { entityCode };
		} else if (StringUtils.isNotBlank(pANNumber)) {
			whereClause = "PanNumber = ?";
			obj = new Object[] { pANNumber };
		}

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("Entity", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("Entity_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "Entity_Temp", "Entity" }, whereClause);

			if (StringUtils.isNotBlank(entityCode)) {
				obj = new Object[] { entityCode, entityCode };
			} else if (StringUtils.isNotBlank(pANNumber)) {
				obj = new Object[] { pANNumber, pANNumber };
			}
			break;
		}

		logger.debug(Literal.SQL.concat(sql));

		return jdbcOperations.queryForObject(sql, Integer.class, obj) > 0;
	}

	@Override
	public boolean panNumberExist(String pANNumber, String entityCode, TableType tableType) {
		String sql;
		String whereClause = "pANNumber = ? and entityCode = ?";

		Object[] obj = new Object[] { pANNumber, entityCode };

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("Entity", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("Entity_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "Entity_Temp", "Entity" }, whereClause);
			obj = new Object[] { pANNumber, entityCode, pANNumber, entityCode };
			break;
		}

		logger.debug(Literal.SQL.concat(sql));

		return jdbcOperations.queryForObject(sql, Integer.class, obj) > 0;
	}

	@Override
	public Entity getEntityByFinDivision(String divisionCode, String type) {
		String sql = "Select EntityCode, EntityDesc, PANNumber From Entity Where EntityCode = (Select EntityCode from SMTDivisionDetail where DivisionCode = ?)";

		logger.debug(Literal.SQL.concat(sql));

		try {
			return this.jdbcOperations.queryForObject(sql, (rs, rowNum) -> {
				Entity e = new Entity();

				e.setEntityCode(rs.getString("EntityCode"));
				e.setEntityDesc(rs.getString("EntityDesc"));
				e.setPANNumber(rs.getString("PANNumber"));

				return e;
			}, divisionCode);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public Entity getEntityByFinType(String finType, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" EntityCode, EntityDesc, PANNumber");
		sql.append(" From Entity");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where EntityCode = (Select D.EntityCode From RMTFinanceTypes F ");
		sql.append(" Inner Join SMTDivisionDetail D ON D.DivisionCode = F.FinDivision");
		sql.append(" Where FinType = ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				Entity e = new Entity();

				e.setEntityCode(rs.getString("EntityCode"));
				e.setEntityDesc(rs.getString("EntityDesc"));
				e.setPANNumber(rs.getString("PANNumber"));

				return e;
			}, finType);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public List<Entity> getEntites() {
		String sql = "Select EntityCode, EntityDesc FROM Entity";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.query(sql, (rs, rowNum) -> {
			Entity et = new Entity();
			et.setEntityCode(rs.getString("EntityCode"));
			et.setEntityDesc(rs.getString("EntityDesc"));
			return et;
		});
	}

	@Override
	public int getEntityCount(String entityCode) {
		String sql = "Select count(EntityCode) From Entity Where EntityCode = ? and Active = ?";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.queryForObject(sql, Integer.class, entityCode, 1);
	}
}