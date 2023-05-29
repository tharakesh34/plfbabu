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
 * * FileName : DPDBucketConfigurationDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 21-04-2017 * *
 * Modified Date : 21-04-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 21-04-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.applicationmaster.impl;

import java.util.List;
import java.util.stream.Collectors;

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

import com.pennant.backend.dao.applicationmaster.DPDBucketConfigurationDAO;
import com.pennant.backend.model.applicationmaster.DPDBucketConfiguration;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * Data access layer implementation for <code>DPDBucketConfiguration</code> with set of CRUD operations.
 */
public class DPDBucketConfigurationDAOImpl extends SequenceDao<DPDBucketConfiguration>
		implements DPDBucketConfigurationDAO {
	private static Logger logger = LogManager.getLogger(DPDBucketConfigurationDAOImpl.class);

	public DPDBucketConfigurationDAOImpl() {
		super();
	}

	@Override
	public DPDBucketConfiguration getDPDBucketConfiguration(long configID, String type) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" configID, productCode, bucketID, dueDays, suspendProfit, ");
		if (type.contains("View")) {
			sql.append(" ProductCodeName, BucketIDName,BucketCode,");
		}
		sql.append(
				" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From DPDBUCKETSCONFIG");
		sql.append(type);
		sql.append(" Where configID = :configID");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		DPDBucketConfiguration dPDBucketConfiguration = new DPDBucketConfiguration();
		dPDBucketConfiguration.setConfigID(configID);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(dPDBucketConfiguration);
		RowMapper<DPDBucketConfiguration> rowMapper = BeanPropertyRowMapper.newInstance(DPDBucketConfiguration.class);

		try {
			return jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public boolean isDuplicateKey(long configID, String productCode, long bucketID, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		String sql;
		String whereClause = "productCode = :productCode AND bucketID = :bucketID AND configID != :configID";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("DPDBUCKETSCONFIG", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("DPDBUCKETSCONFIG_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "DPDBUCKETSCONFIG_Temp", "DPDBUCKETSCONFIG" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("configID", configID);
		paramSource.addValue("productCode", productCode);
		paramSource.addValue("bucketID", bucketID);

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

	@Override
	public String save(DPDBucketConfiguration dPDBucketConfiguration, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" insert into DPDBUCKETSCONFIG");
		sql.append(tableType.getSuffix());
		sql.append(" (configID, productCode, bucketID, dueDays, suspendProfit, ");
		sql.append(
				" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values(");
		sql.append(" :configID, :productCode, :bucketID, :dueDays, :suspendProfit, ");
		sql.append(
				" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		// Get the identity sequence number.
		if (dPDBucketConfiguration.getConfigID() <= 0) {
			dPDBucketConfiguration.setConfigID(getNextValue("SeqDPDBUCKETSCONFIG"));
		}
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(dPDBucketConfiguration);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(dPDBucketConfiguration.getConfigID());
	}

	@Override
	public void update(DPDBucketConfiguration dPDBucketConfiguration, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update DPDBUCKETSCONFIG");
		sql.append(tableType.getSuffix());
		sql.append("  set productCode = :productCode, bucketID = :bucketID, dueDays = :dueDays, ");
		sql.append(" suspendProfit = :suspendProfit, ");
		sql.append(" LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where configID = :configID ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(dPDBucketConfiguration);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(DPDBucketConfiguration dPDBucketConfiguration, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from DPDBUCKETSCONFIG");
		sql.append(tableType.getSuffix());
		sql.append(" where configID = :configID ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(dPDBucketConfiguration);
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
	public List<DPDBucketConfiguration> getDPDBucketConfigurations() {
		logger.debug(Literal.ENTERING);
		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" configID, productCode, bucketID, dueDays, suspendProfit ");
		sql.append(" From DPDBUCKETSCONFIG");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		RowMapper<DPDBucketConfiguration> rowMapper = BeanPropertyRowMapper.newInstance(DPDBucketConfiguration.class);
		List<DPDBucketConfiguration> list = jdbcTemplate.query(sql.toString(), rowMapper);

		logger.debug(Literal.LEAVING);
		return list;
	}

	@Override
	public int getByProductCode(String producCode, int dueDys, String type) {
		DPDBucketConfiguration dPDBucketConfiguration = new DPDBucketConfiguration();
		dPDBucketConfiguration.setProductCode(producCode);
		dPDBucketConfiguration.setDueDays(dueDys);
		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*)");
		selectSql.append(" From DPDBUCKETSCONFIG");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where productCode =:productCode AND dueDays =:dueDays ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(dPDBucketConfiguration);

		logger.debug("Leaving");
		return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
	}

	@Override
	public int getDPDBucketConfigurationDAOById(long bucketID, String type) {
		DPDBucketConfiguration dPDBucketConfiguration = new DPDBucketConfiguration();
		dPDBucketConfiguration.setBucketID(bucketID);
		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*)");
		selectSql.append(" From DPDBUCKETSCONFIG");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where BucketID =:BucketID ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(dPDBucketConfiguration);

		logger.debug("Leaving");
		return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
	}

	@Override
	public List<DPDBucketConfiguration> getDPDBucketConfigurations(String productCode) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ConfigID, ProductCode, BucketID, DueDays, SuspendProfit");
		sql.append(" From DPDBUCKETSCONFIG");
		sql.append(" Where ProductCode = ?");

		List<DPDBucketConfiguration> dpdConfig = this.jdbcOperations.query(sql.toString(),
				ps -> ps.setString(1, productCode), (rs, rowNum) -> {
					DPDBucketConfiguration bc = new DPDBucketConfiguration();

					bc.setConfigID(rs.getLong("ConfigID"));
					bc.setProductCode(rs.getString("ProductCode"));
					bc.setBucketID(rs.getLong("BucketID"));
					bc.setDueDays(rs.getInt("DueDays"));
					bc.setSuspendProfit(rs.getBoolean("SuspendProfit"));

					return bc;
				});
		return dpdConfig.stream().sorted((dpd1, dpd2) -> Integer.compare(dpd1.getDueDays(), dpd2.getDueDays()))
				.collect(Collectors.toList());
	}

}
