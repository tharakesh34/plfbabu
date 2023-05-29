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
 * * FileName : AssetClassificationHeaderDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 04-05-2020
 * * * Modified Date : 04-05-2020 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 04-05-2020 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.applicationmaster.impl;

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

import com.pennant.backend.dao.applicationmaster.AssetClassificationHeaderDAO;
import com.pennant.backend.model.applicationmaster.AssetClassificationDetail;
import com.pennant.backend.model.applicationmaster.AssetClassificationHeader;
import com.pennant.backend.model.applicationmaster.NPAProvisionHeader;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * Data access layer implementation for <code>AssetClassificationHeader</code> with set of CRUD operations.
 */
public class AssetClassificationHeaderDAOImpl extends SequenceDao<AssetClassificationHeader>
		implements AssetClassificationHeaderDAO {
	private static Logger logger = LogManager.getLogger(AssetClassificationHeaderDAOImpl.class);

	public AssetClassificationHeaderDAOImpl() {
		super();
	}

	@Override
	public AssetClassificationHeader getAssetClassificationHeader(long id, String type) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = getSQLQuery(type);
		sql.append(" Where id = :id");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		AssetClassificationHeader header = new AssetClassificationHeader();
		header.setId(id);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(header);
		RowMapper<AssetClassificationHeader> rowMapper = BeanPropertyRowMapper
				.newInstance(AssetClassificationHeader.class);

		try {
			header = jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			header = null;
		}

		logger.debug(Literal.LEAVING);
		return header;
	}

	@Override
	public boolean isDuplicateKey(long id, String code, int stageOrder, Long npaTemplateId, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		String sql;
		String whereClause = "code = :code AND stageOrder = :stageOrder AND NpaTemplateId = :NpaTemplateId AND id != :id";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("ASSET_CLSSFICATN_HEADER", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("ASSET_CLSSFICATN_HEADER_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "ASSET_CLSSFICATN_HEADER_Temp", "ASSET_CLSSFICATN_HEADER" },
					whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("id", id);
		paramSource.addValue("code", code);
		paramSource.addValue("stageOrder", stageOrder);
		paramSource.addValue("NpaTemplateId", npaTemplateId);

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

	@Override
	public String save(AssetClassificationHeader assetClassificationHeader, TableType tableType) {
		logger.debug(Literal.ENTERING);

		if (assetClassificationHeader.getId() == Long.MIN_VALUE) {
			assetClassificationHeader.setId(getNextValue("SEQ_ASSET_CLSSFICATN_HEADER"));
			logger.debug("get NextID:" + assetClassificationHeader.getId());
		}

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" insert into ASSET_CLSSFICATN_HEADER");
		sql.append(tableType.getSuffix());
		sql.append(" (id, code, description, stageOrder, active, Version , LastMntBy, LastMntOn,");
		sql.append(
				"  RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId, NpaTemplateId)");
		sql.append(" values(");
		sql.append(" :id, :code, :description, :stageOrder, :active, :Version , :LastMntBy, :LastMntOn, ");
		sql.append(
				" :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId, :NpaTemplateId)");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(assetClassificationHeader);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(assetClassificationHeader.getId());
	}

	@Override
	public void update(AssetClassificationHeader assetClassificationHeader, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update ASSET_CLSSFICATN_HEADER");
		sql.append(tableType.getSuffix());
		sql.append(" set code = :code, description = :description, stageOrder = :stageOrder, active = :active, ");
		sql.append(" LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId, NpaTemplateId=:NpaTemplateId");
		sql.append(" where id = :id ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(assetClassificationHeader);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(AssetClassificationHeader assetClassificationHeader, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from ASSET_CLSSFICATN_HEADER");
		sql.append(tableType.getSuffix());
		sql.append(" where Id =:Id");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(assetClassificationHeader);
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
	public void saveFinType(AssetClassificationDetail detail, TableType tableType) {

		logger.debug(Literal.ENTERING);

		if (detail.getId() == Long.MIN_VALUE) {
			detail.setId(getNextValue("SEQ_ASSET_CLSSFICATN_DETAILS"));
			logger.debug("get NextID:" + detail.getId());
		}

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" insert into ASSET_CLSSFICATN_DETAILS");
		sql.append(tableType.getSuffix());
		sql.append(" (id, headerId, finType, Version , LastMntBy, LastMntOn,");
		sql.append("  RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values(");
		sql.append(" :id, :headerId, :finType, :Version , :LastMntBy, :LastMntOn,");
		sql.append("  :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(detail);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);

	}

	@Override
	public void updateFinType(AssetClassificationDetail detail, TableType tableType) {

		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update ASSET_CLSSFICATN_DETAILS");
		sql.append(tableType.getSuffix());
		sql.append(" set headerId = :headerId, finType = :finType, ");
		sql.append(" LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where id = :id ");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(detail);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);

	}

	@Override
	public void deleteFinType(AssetClassificationDetail detail, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from ASSET_CLSSFICATN_DETAILS");
		sql.append(tableType.getSuffix());
		sql.append(" where id =:id ");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("id", detail.getId());
		int recordCount = 0;

		try {
			recordCount = jdbcTemplate.update(sql.toString(), source);
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
	public void deleteFinTypeList(long headerId, TableType tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder deleteSql = new StringBuilder("Delete From ASSET_CLSSFICATN_DETAILS");
		deleteSql.append(tableType.getSuffix());
		deleteSql.append(" Where HeaderId =:HeaderId");
		logger.debug("deleteSql: " + deleteSql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("HeaderId", headerId);

		jdbcTemplate.update(deleteSql.toString(), source);
		logger.debug(Literal.LEAVING);

	}

	@Override
	public List<AssetClassificationDetail> getAssetDetailList(long headerId, TableType tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT Id, HeaderId, FinType, Version, LastMntOn, LastMntBy,");
		sql.append(" RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From ASSET_CLSSFICATN_DETAILS");
		sql.append(tableType.getSuffix());
		sql.append(" Where headerId =:headerId");

		logger.debug("selectSql: " + sql.toString());
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("headerId", headerId);

		RowMapper<AssetClassificationDetail> typeRowMapper = BeanPropertyRowMapper
				.newInstance(AssetClassificationDetail.class);
		try {
			return jdbcTemplate.query(sql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException dae) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	public AssetClassificationDetail getAssetClassificationDetail(long id, String type) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" id, headerId, finType, ");
		if (type.contains("View")) {
			sql.append("id, headerId, finType, headerId,finType,");
		}

		sql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, ");
		sql.append(" NextTaskId, RecordType, WorkflowId");
		sql.append(" From ASSET_CLSSFICATN_DETAILS");
		sql.append(type);
		sql.append(" Where id = :id");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		AssetClassificationDetail detail = new AssetClassificationDetail();
		detail.setId(id);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(detail);
		RowMapper<AssetClassificationDetail> rowMapper = BeanPropertyRowMapper
				.newInstance(AssetClassificationDetail.class);

		try {
			detail = jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			detail = null;
		}

		logger.debug(Literal.LEAVING);
		return detail;
	}

	@Override
	public boolean isAssetCodeExists(String code, TableType type) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = null;
		StringBuilder selectSql = new StringBuilder("Select Count(*) From ASSET_CLSSFICATN_HEADER");
		selectSql.append(type.getSuffix());
		selectSql.append(" Where Code = :Code");

		logger.debug("selectSql: " + selectSql.toString());

		source = new MapSqlParameterSource();
		source.addValue("Code", code);

		try {
			if (jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class) > 0) {
				return true;
			}
		} catch (DataAccessException e) {
			logger.error(e);
		}
		logger.debug(Literal.LEAVING);
		return false;
	}

	@Override
	public boolean isStageOrderExists(int stageOrder, Long npaTemplateId, TableType type) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = null;
		StringBuilder selectSql = new StringBuilder("Select Count(*) From ASSET_CLSSFICATN_HEADER");
		selectSql.append(type.getSuffix());
		selectSql.append(" Where StageOrder = :StageOrder AND NpaTemplateId = :NpaTemplateId");

		logger.debug("selectSql: " + selectSql.toString());

		source = new MapSqlParameterSource();
		source.addValue("StageOrder", stageOrder);
		source.addValue("NpaTemplateId", npaTemplateId);
		try {
			if (jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class) > 0) {
				return true;
			}
		} catch (DataAccessException e) {
			logger.error(e);
		}
		logger.debug(Literal.LEAVING);
		return false;
	}

	@Override
	public List<AssetClassificationDetail> getAssetClassificationDetails(String finType, TableType tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT  id, headerId, finType, ");
		if (tableType.getSuffix().contains("View")) {
			sql.append("id, headerId, finType, headerId,finType,");
		}

		sql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, ");
		sql.append(" NextTaskId, RecordType, WorkflowId");
		sql.append(" From ASSET_CLSSFICATN_DETAILS");
		sql.append(tableType.getSuffix());
		sql.append(" Where finType = :finType");
		logger.debug("selectSql: " + sql.toString());

		AssetClassificationDetail detail = new AssetClassificationDetail();
		detail.setFinType(finType);

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(detail);
		RowMapper<AssetClassificationDetail> typeRowMapper = BeanPropertyRowMapper
				.newInstance(AssetClassificationDetail.class);

		try {
			return this.jdbcTemplate.query(sql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public int getCountByFinType(String fintype, TableType type) {
		logger.debug(Literal.ENTERING);

		NPAProvisionHeader header = new NPAProvisionHeader();
		header.setFinType(fintype);

		StringBuilder sql = new StringBuilder("SELECT COUNT(*)");
		sql.append(" From npa_provision_header");
		sql.append(type.getSuffix());
		sql.append(" Where FinType = :FinType");

		logger.debug("sql1 : " + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(header);

		logger.debug(Literal.LEAVING);
		return this.jdbcTemplate.queryForObject(sql.toString(), beanParameters, Integer.class);
	}

	@Override
	public List<AssetClassificationHeader> getAssetClassificationHeaderByTemplate(long templateId, String type) {
		StringBuilder sql = getSQLQuery(type);
		sql.append("  Where NpaTemplateId = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> ps.setLong(1, templateId), (rs, rowNum) -> {
			AssetClassificationHeader ah = new AssetClassificationHeader();

			ah.setId(rs.getLong("Id"));
			ah.setCode(rs.getString("Code"));
			ah.setDescription(rs.getString("Description"));
			ah.setStageOrder(rs.getInt("StageOrder"));
			ah.setActive(rs.getBoolean("Active"));
			ah.setVersion(rs.getInt("Version"));
			ah.setLastMntOn(rs.getTimestamp("LastMntOn"));
			ah.setLastMntBy(rs.getLong("LastMntBy"));
			ah.setRecordStatus(rs.getString("RecordStatus"));
			ah.setRoleCode(rs.getString("RoleCode"));
			ah.setNextRoleCode(rs.getString("NextRoleCode"));
			ah.setTaskId(rs.getString("TaskId"));
			ah.setNextTaskId(rs.getString("NextTaskId"));
			ah.setRecordType(rs.getString("RecordType"));
			ah.setWorkflowId(rs.getLong("WorkflowId"));
			ah.setNpaTemplateId(rs.getLong("NpaTemplateId"));

			if (type.contains("View")) {
				ah.setNpaTemplateDesc(rs.getString("NpaTemplateDesc"));
				ah.setNpaTemplateCode(rs.getString("NpaTemplateCode"));
			}

			return ah;
		});
	}

	private StringBuilder getSQLQuery(String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Id, Code, Description, StageOrder, Active, Version, LastMntOn, LastMntBy, RecordStatus");
		sql.append(", RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId, NpaTemplateId");

		if (type.contains("View")) {
			sql.append(", NpaTemplateDesc, NpaTemplateCode");
		}

		sql.append(" from ASSET_CLSSFICATN_HEADER");
		sql.append(type);

		return sql;
	}

}
