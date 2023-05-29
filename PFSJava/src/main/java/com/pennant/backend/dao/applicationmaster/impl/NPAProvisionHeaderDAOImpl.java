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
 * * FileName : NPAProvisionHeaderDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 04-05-2020 * *
 * Modified Date : 04-05-2020 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 04-05-2020 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.applicationmaster.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
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

import com.pennant.backend.dao.applicationmaster.NPAProvisionHeaderDAO;
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
 * Data access layer implementation for <code>NPAProvisionHeader</code> with set of CRUD operations.
 */
public class NPAProvisionHeaderDAOImpl extends SequenceDao<NPAProvisionHeader> implements NPAProvisionHeaderDAO {
	private static Logger logger = LogManager.getLogger(NPAProvisionHeaderDAOImpl.class);

	public NPAProvisionHeaderDAOImpl() {
		super();
	}

	@Override
	public NPAProvisionHeader getNPAProvisionHeader(long id, String type) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT  id, entity, finType, NpaTemplateId, ");
		if (type.contains("View")) {
			sql.append("entityName, finTypeName, NpaTemplateCode, NpaTemplateDesc,");
		}
		sql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, ");
		sql.append("   TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From NPA_PROVISION_HEADER");
		sql.append(type);
		sql.append(" Where id = :id");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		NPAProvisionHeader provisionHeader = new NPAProvisionHeader();
		provisionHeader.setId(id);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(provisionHeader);
		RowMapper<NPAProvisionHeader> rowMapper = BeanPropertyRowMapper.newInstance(NPAProvisionHeader.class);

		try {
			provisionHeader = jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			provisionHeader = null;
		}

		logger.debug(Literal.LEAVING);
		return provisionHeader;
	}

	@Override
	public boolean isDuplicateKey(long id, String entity, String finType, Long npaTemplateId, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		String sql;
		String whereClause = "entity = :entity AND finType = :finType AND npaTemplateId = :npaTemplateId AND id != :id";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("NPA_PROVISION_HEADER", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("NPA_PROVISION_HEADER_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "NPA_PROVISION_HEADER_Temp", "NPA_PROVISION_HEADER" },
					whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("id", id);
		paramSource.addValue("entity", entity);
		paramSource.addValue("finType", finType);
		paramSource.addValue("npaTemplateId", npaTemplateId);

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}
		logger.debug(Literal.LEAVING);
		return exists;
	}

	@Override
	public String save(NPAProvisionHeader nPAProvisionHeader, TableType tableType) {
		logger.debug(Literal.ENTERING);

		if (nPAProvisionHeader.getId() == Long.MIN_VALUE) {
			nPAProvisionHeader.setId(getNextValue("SeqNPA_PROVISION_HEADER"));
			logger.debug("get NextID:" + nPAProvisionHeader.getId());
		}
		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" insert into NPA_PROVISION_HEADER");
		sql.append(tableType.getSuffix());
		sql.append(" (id, entity, finType, NpaTemplateId, ");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, ");
		sql.append(" NextTaskId, RecordType, WorkflowId)");
		sql.append(" values(");
		sql.append(" :id, :entity, :finType, :NpaTemplateId, ");
		sql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId,");
		sql.append(" :NextTaskId, :RecordType, :WorkflowId)");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(nPAProvisionHeader);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
		logger.debug(Literal.LEAVING);
		return String.valueOf(nPAProvisionHeader.getId());
	}

	@Override
	public void update(NPAProvisionHeader nPAProvisionHeader, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update NPA_PROVISION_HEADER");
		sql.append(tableType.getSuffix());
		sql.append(" set entity = :entity, finType = :finType, NpaTemplateId = :NpaTemplateId,");
		sql.append(" LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where id = :id ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(nPAProvisionHeader);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(NPAProvisionHeader nPAProvisionHeader, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from NPA_PROVISION_HEADER");
		sql.append(tableType.getSuffix());
		sql.append(" where id = :id ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(nPAProvisionHeader);
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

	///////////////////////
	@Override
	public List<AssetClassificationDetail> getAssetHeaderIdList(String finType, TableType type) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" headerid ");
		sql.append(" From ASSET_CLSSFICATN_DETAILS");
		sql.append(type.getSuffix());
		sql.append(" Where finType =:finType ");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		AssetClassificationDetail assetClassificationDetail = new AssetClassificationDetail();
		// assetClassificationDetail.setFinType(finType);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(assetClassificationDetail);
		RowMapper<AssetClassificationDetail> rowMapper = BeanPropertyRowMapper
				.newInstance(AssetClassificationDetail.class);

		List<AssetClassificationDetail> headerIdList = null;
		try {
			headerIdList = jdbcTemplate.query(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			headerIdList = new ArrayList<>();
		}

		logger.debug(Literal.LEAVING);
		return headerIdList;
	}

	@Override
	public AssetClassificationHeader getAssetClassificationCodesList(long listHeaderId, TableType type) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" id,code,description ");
		sql.append(" From ASSET_CLSSFICATN_Header");
		sql.append(type.getSuffix());
		sql.append(" Where id =:Id");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		AssetClassificationHeader assetClassificationHeader = new AssetClassificationHeader();
		assetClassificationHeader.setId(listHeaderId);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(assetClassificationHeader);
		RowMapper<AssetClassificationHeader> rowMapper = BeanPropertyRowMapper
				.newInstance(AssetClassificationHeader.class);

		try {
			assetClassificationHeader = jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			assetClassificationHeader = null;
		}

		logger.debug(Literal.LEAVING);
		return assetClassificationHeader;
	}

	@Override
	public boolean getIsFinTypeExists(String finType, Long npaTemplateId, TableType type) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = null;
		StringBuilder selectSql = new StringBuilder("Select Count(*) From NPA_PROVISION_HEADER");
		selectSql.append(type.getSuffix());
		selectSql.append(" Where finType = :FinType and NpaTemplateId = :NpaTemplateId");

		logger.debug("selectSql: " + selectSql.toString());

		source = new MapSqlParameterSource();
		source.addValue("FinType", finType);
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
	public NPAProvisionHeader getNPAProvisionByFintype(String finType, Long npaTemplateId, TableType tableType) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Id, Entity, FinType, NpaTemplateId");
		if (tableType.getSuffix().contains("View")) {
			sql.append(", EntityName, FinTypeName, NpaTemplateCode, NpaTemplateDesc");
		}
		sql.append(", Version, LastMntOn, LastMntBy, RecordStatus");
		sql.append(", RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" from NPA_PROVISION_HEADER");
		sql.append(tableType.getSuffix());
		sql.append(" Where finType = ? and npaTemplateId = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				NPAProvisionHeader pa = new NPAProvisionHeader();

				pa.setId(rs.getLong("Id"));
				pa.setEntity(rs.getString("Entity"));
				pa.setFinType(rs.getString("FinType"));
				pa.setNpaTemplateId(rs.getLong("NpaTemplateId"));

				if (tableType.getSuffix().contains("View")) {
					pa.setEntityName(rs.getString("EntityName"));
					pa.setFinTypeName(rs.getString("FinTypeName"));
					pa.setNpaTemplateCode(rs.getString("NpaTemplateCode"));
					pa.setNpaTemplateDesc(rs.getString("NpaTemplateDesc"));
				}

				pa.setVersion(rs.getInt("Version"));
				pa.setLastMntOn(rs.getTimestamp("LastMntOn"));
				pa.setLastMntBy(rs.getLong("LastMntBy"));
				pa.setRecordStatus(rs.getString("RecordStatus"));
				pa.setRoleCode(rs.getString("RoleCode"));
				pa.setNextRoleCode(rs.getString("NextRoleCode"));
				pa.setTaskId(rs.getString("TaskId"));
				pa.setNextTaskId(rs.getString("NextTaskId"));
				pa.setRecordType(rs.getString("RecordType"));
				pa.setWorkflowId(rs.getLong("WorkflowId"));

				return pa;
			}, finType, npaTemplateId);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public List<NPAProvisionHeader> getNPAProvisionsListByFintype(String finType, TableType tableType) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Id, Entity, FinType, NpaTemplateId");
		if (StringUtils.containsIgnoreCase(tableType.getSuffix(), "view")) {
			sql.append(", EntityName, FinTypeName, NpaTemplateCode, NpaTemplateDesc");
		}
		sql.append(", Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From NPA_PROVISION_HEADER");
		sql.append(tableType.getSuffix());
		sql.append(" Where finType = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> ps.setString(1, finType), (rs, i) -> {
			NPAProvisionHeader ah = new NPAProvisionHeader();

			ah.setId(rs.getLong("Id"));
			ah.setEntity(rs.getString("Entity"));
			ah.setFinType(rs.getString("FinType"));
			ah.setNpaTemplateId(rs.getLong("NpaTemplateId"));

			if (StringUtils.containsIgnoreCase(tableType.getSuffix(), "view")) {
				ah.setEntityName(rs.getString("EntityName"));
				ah.setFinTypeName(rs.getString("FinTypeName"));
				ah.setNpaTemplateDesc(rs.getString("NpaTemplateDesc"));
				ah.setNpaTemplateCode(rs.getString("NpaTemplateCode"));
			}

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

			return ah;
		});
	}
}