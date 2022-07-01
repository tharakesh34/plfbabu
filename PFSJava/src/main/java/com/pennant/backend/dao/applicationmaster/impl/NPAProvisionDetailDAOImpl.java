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
 * * FileName : NPAProvisionDetailDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 04-05-2020 * *
 * Modified Date : 04-05-2020 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 04-05-2020 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.applicationmaster.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
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
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;

import com.pennant.backend.dao.applicationmaster.NPAProvisionDetailDAO;
import com.pennant.backend.model.applicationmaster.AssetClassificationDetail;
import com.pennant.backend.model.applicationmaster.NPAProvisionDetail;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.core.TableType;

/**
 * Data access layer implementation for <code>NPAProvisionDetail</code> with set of CRUD operations.
 */
public class NPAProvisionDetailDAOImpl extends SequenceDao<NPAProvisionDetail> implements NPAProvisionDetailDAO {
	private static Logger logger = LogManager.getLogger(NPAProvisionDetailDAOImpl.class);

	public NPAProvisionDetailDAOImpl() {
		super();
	}

	public List<AssetClassificationDetail> getAssetHeaderIdList(String finType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" headerid ");
		sql.append(" From ASSET_CLSSFICATN_DETAILS_Temp");
		sql.append(" Where finType =:finType ");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		AssetClassificationDetail assetClassificationDetail = new AssetClassificationDetail();
		assetClassificationDetail.setFinType(finType);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(assetClassificationDetail);
		RowMapper<AssetClassificationDetail> rowMapper = BeanPropertyRowMapper
				.newInstance(AssetClassificationDetail.class);

		return jdbcTemplate.query(sql.toString(), paramSource, rowMapper);
	}

	@Override
	public List<NPAProvisionDetail> getNPAProvisionDetailList(long id, TableType tableType) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Id, HeaderId, AssetClassificationId, NPAActive, DPDdays, NPARepayApprtnmnt, IntSecPerc");
		sql.append(", IntUnSecPerc, RegSecPerc, RegUnSecPerc, Version, LastMntOn, LastMntBy, RecordStatus");
		sql.append(", RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(", RuleId, Active");
		if (StringUtils.containsIgnoreCase(tableType.getSuffix(), "view")) {
			sql.append(", AssetCode, AssetStageOrder");
		}
		sql.append(" from NPA_PROVISION_DETAILS");
		sql.append(tableType.getSuffix());
		sql.append(" Where headerId = ?");

		logger.trace(Literal.SQL + sql.toString());

		List<NPAProvisionDetail> npaProvsionDetails = null;

		try {
			npaProvsionDetails = this.jdbcOperations.query(sql.toString(), ps -> {
				int index = 1;
				ps.setLong(index, id);
			}, (rs, rowNum) -> {
				NPAProvisionDetail pd = new NPAProvisionDetail();

				pd.setId(rs.getLong("Id"));
				pd.setHeaderId(rs.getLong("HeaderId"));
				pd.setAssetClassificationId(rs.getLong("AssetClassificationId"));
				pd.setNPAActive(rs.getBoolean("NPAActive"));
				pd.setDPDdays(rs.getInt("DPDdays"));
				pd.setNPARepayApprtnmnt(rs.getString("NPARepayApprtnmnt"));
				pd.setIntSecPerc(rs.getBigDecimal("IntSecPerc"));
				pd.setIntUnSecPerc(rs.getBigDecimal("IntUnSecPerc"));
				pd.setRegSecPerc(rs.getBigDecimal("RegSecPerc"));
				pd.setRegUnSecPerc(rs.getBigDecimal("RegUnSecPerc"));
				pd.setVersion(rs.getInt("Version"));
				pd.setLastMntOn(rs.getTimestamp("LastMntOn"));
				pd.setLastMntBy(rs.getLong("LastMntBy"));
				pd.setRecordStatus(rs.getString("RecordStatus"));
				pd.setRoleCode(rs.getString("RoleCode"));
				pd.setNextRoleCode(rs.getString("NextRoleCode"));
				pd.setTaskId(rs.getString("TaskId"));
				pd.setNextTaskId(rs.getString("NextTaskId"));
				pd.setRecordType(rs.getString("RecordType"));
				pd.setWorkflowId(rs.getLong("WorkflowId"));
				pd.setRuleId(JdbcUtil.getLong(rs.getObject("RuleId")));
				pd.setActive(rs.getBoolean("Active"));

				if (StringUtils.containsIgnoreCase(tableType.getSuffix(), "view")) {
					pd.setAssetCode(rs.getString("AssetCode"));
					pd.setAssetStageOrder(rs.getInt("AssetStageOrder"));
				}

				return pd;

			});
		} catch (EmptyResultDataAccessException e) {
			npaProvsionDetails = new ArrayList<>();
			logger.warn("Records not found in NPA_PROVISION_DETAILS{} for the specified id {}", tableType.getSuffix(),
					id);
		}

		return sortByAssetStageOrder(npaProvsionDetails);

	}

	@Override
	public String save(NPAProvisionDetail nPAProvisionDetail, TableType tableType) {
		logger.debug(Literal.ENTERING);

		if (nPAProvisionDetail.getId() == Long.MIN_VALUE) {
			nPAProvisionDetail.setId(getNextValue("SeqNPA_PROVISION_DETAILS"));
			logger.debug("get NextID:" + nPAProvisionDetail.getId());
		}

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" insert into NPA_PROVISION_DETAILS");
		sql.append(tableType.getSuffix());
		sql.append("(id, headerId, assetClassificationId, nPAActive, dPDdays, nPARepayApprtnmnt, ");
		sql.append(" intSecPerc, intUnSecPerc, regSecPerc, regUnSecPerc, ");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, ");
		sql.append(" TaskId, NextTaskId, RecordType, WorkflowId, RuleId, Active)");

		sql.append(" values(");
		sql.append(" :id, :headerId, :assetClassificationId, :nPAActive, :dPDdays, :nPARepayApprtnmnt, ");
		sql.append(" :intSecPerc, :intUnSecPerc, :regSecPerc, :regUnSecPerc, ");
		sql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, ");
		sql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId, :RuleId, :Active)");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(nPAProvisionDetail);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(nPAProvisionDetail.getId());
	}

	@Override
	public void update(NPAProvisionDetail nPAProvisionDetail, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update NPA_PROVISION_DETAILS");
		sql.append(tableType.getSuffix());
		sql.append(
				"  set headerId = :headerId, assetClassificationId = :assetClassificationId, nPAActive = :nPAActive, ");
		sql.append(" dPDdays = :dPDdays, nPARepayApprtnmnt = :nPARepayApprtnmnt, intSecPerc = :intSecPerc, ");
		sql.append(" intUnSecPerc = :intUnSecPerc, regSecPerc = :regSecPerc, regUnSecPerc = :regUnSecPerc, ");
		sql.append(" LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId, RuleId = :RuleId, Active = :Active");
		sql.append(" where id = :Id ");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(nPAProvisionDetail);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(NPAProvisionDetail nPAProvisionDetail, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from NPA_PROVISION_DETAILS");
		sql.append(tableType.getSuffix());
		sql.append(" where id = :id ");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(nPAProvisionDetail);
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
	public void saveList(List<NPAProvisionDetail> npaProvisionDetailsList, TableType tableType) {
		logger.debug(Literal.ENTERING);

		for (NPAProvisionDetail npaProvisionDetail : npaProvisionDetailsList) {
			if (npaProvisionDetail.getId() == Long.MIN_VALUE) {
				npaProvisionDetail.setId(getNextValue("SeqNPA_PROVISION_DETAILS"));
				logger.debug("get NextID:" + npaProvisionDetail.getId());
			}
		}

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" insert into NPA_PROVISION_DETAILS");
		sql.append(tableType.getSuffix());
		sql.append("(id, headerId, assetClassificationId, nPAActive, dPDdays, nPARepayApprtnmnt, ");
		sql.append(" intSecPerc, intUnSecPerc, regSecPerc, regUnSecPerc, ");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, ");
		sql.append("  TaskId, NextTaskId, RecordType, WorkflowId, RuleId, Active)");
		sql.append(" values(");
		sql.append(" :id, :headerId, :assetClassificationId, :nPAActive, :dPDdays, :nPARepayApprtnmnt, ");
		sql.append(" :intSecPerc, :intUnSecPerc, :regSecPerc, :regUnSecPerc, ");
		sql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, ");
		sql.append("  :NextTaskId, :RecordType, :WorkflowId, :RuleId, :Active)");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(npaProvisionDetailsList.toArray());

		try {
			this.jdbcTemplate.batchUpdate(sql.toString(), beanParameters);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);

	}

	@Override
	public void deleteProvisionList(long id, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from NPA_PROVISION_DETAILS");
		sql.append(tableType.getSuffix());
		sql.append(" where headerId = :HeaderId ");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		NPAProvisionDetail npaProvisionDetail = new NPAProvisionDetail();
		npaProvisionDetail.setHeaderId(id);
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(npaProvisionDetail);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public NPAProvisionDetail getNPAProvisionDetail(long headerId, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" id, headerId, assetClassificationId, npaactive, dPDdays, nPARepayApprtnmnt");
		sql.append(", intSecPerc, intUnSecPerc, regSecPerc, regUnSecPerc");
		sql.append(", Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId");
		sql.append(", NextTaskId,RecordType, WorkflowId, RuleId, Active");

		if (tableType.getSuffix().contains("View")) {
			sql.append(", assetCode, assetStageOrder");
		}

		sql.append(" From NPA_PROVISION_DETAILS");
		sql.append(tableType.getSuffix());
		sql.append(" Where id = :id");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		NPAProvisionDetail nPAProvisionDetail = new NPAProvisionDetail();
		nPAProvisionDetail.setId(headerId);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(nPAProvisionDetail);
		RowMapper<NPAProvisionDetail> rowMapper = BeanPropertyRowMapper.newInstance(NPAProvisionDetail.class);

		try {
			return jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	public static List<NPAProvisionDetail> sortByAssetStageOrder(List<NPAProvisionDetail> npaProvisionDetails) {
		if (CollectionUtils.isNotEmpty(npaProvisionDetails)) {
			Collections.sort(npaProvisionDetails, new Comparator<NPAProvisionDetail>() {
				@Override
				public int compare(NPAProvisionDetail detail1, NPAProvisionDetail detail2) {
					int asset1 = detail1.getAssetStageOrder();
					int asset2 = detail2.getAssetStageOrder();
					if (asset1 == asset2)
						return 0;
					else if (asset1 > asset2)
						return 1;
					else
						return -1;
				}
			});
		}

		return npaProvisionDetails;
	}

}
