/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  NPABucketConfigurationDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  21-04-2017    														*
 *                                                                  						*
 * Modified Date    :  21-04-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 21-04-2017       PENNANT	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennant.backend.dao.applicationmaster.impl;

import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.applicationmaster.NPABucketConfigurationDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.applicationmaster.NPABucketConfiguration;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * Data access layer implementation for <code>NPABucketConfiguration</code> with set of CRUD operations.
 */
public class NPABucketConfigurationDAOImpl extends BasisNextidDaoImpl<NPABucketConfiguration> implements
		NPABucketConfigurationDAO {
	private static Logger				logger	= Logger.getLogger(NPABucketConfigurationDAOImpl.class);

	private NamedParameterJdbcTemplate	namedParameterJdbcTemplate;

	public NPABucketConfigurationDAOImpl() {
		super();
	}

	@Override
	public NPABucketConfiguration getNPABucketConfiguration(long configID, String type) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" configID, productCode, bucketID, dueDays, suspendProfit, ");
		if (type.contains("View")) {
			sql.append(" ProductCodeName, BucketIDName, BucketCode,");
		}
		sql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From NPABUCKETSCONFIG");
		sql.append(type);
		sql.append(" Where configID = :configID");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		NPABucketConfiguration nPABucketConfiguration = new NPABucketConfiguration();
		nPABucketConfiguration.setConfigID(configID);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(nPABucketConfiguration);
		RowMapper<NPABucketConfiguration> rowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(NPABucketConfiguration.class);

		try {
			nPABucketConfiguration = namedParameterJdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			nPABucketConfiguration = null;
		}

		logger.debug(Literal.LEAVING);
		return nPABucketConfiguration;
	}

	@Override
	public boolean isDuplicateKey(long configID, String productCode, long bucketID, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		String sql;
		String whereClause = "productCode = :productCode AND bucketID = :bucketID AND configID != :configID";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("NPABUCKETSCONFIG", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("NPABUCKETSCONFIG_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "NPABUCKETSCONFIG_Temp", "NPABUCKETSCONFIG" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("configID", configID);
		paramSource.addValue("productCode", productCode);
		paramSource.addValue("bucketID", bucketID);

		Integer count = namedParameterJdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

	@Override
	public String save(NPABucketConfiguration nPABucketConfiguration, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" insert into NPABUCKETSCONFIG");
		sql.append(tableType.getSuffix());
		sql.append(" (configID, productCode, bucketID, dueDays, suspendProfit, ");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values(");
		sql.append(" :configID, :productCode, :bucketID, :dueDays, :suspendProfit, ");
		sql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		if (nPABucketConfiguration.getConfigID() <= 0) {
			nPABucketConfiguration.setConfigID(getNextidviewDAO().getNextId("SeqNPABUCKETSCONFIG"));
		}
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(nPABucketConfiguration);

		try {
			namedParameterJdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(nPABucketConfiguration.getConfigID());
	}

	@Override
	public void update(NPABucketConfiguration nPABucketConfiguration, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update NPABUCKETSCONFIG");
		sql.append(tableType.getSuffix());
		sql.append("  set productCode = :productCode, bucketID = :bucketID, dueDays = :dueDays, ");
		sql.append(" suspendProfit = :suspendProfit, ");
		sql.append(" LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append("  where configID = :configID ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(nPABucketConfiguration);
		int recordCount = namedParameterJdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(NPABucketConfiguration nPABucketConfiguration, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from NPABUCKETSCONFIG");
		sql.append(tableType.getSuffix());
		sql.append(" where configID = :configID ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(nPABucketConfiguration);
		int recordCount = 0;

		try {
			recordCount = namedParameterJdbcTemplate.update(sql.toString(), paramSource);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets a new <code>JDBC Template</code> for the given data source.
	 * 
	 * @param dataSource
	 *            The JDBC data source to access.
	 */
	public void setDataSource(DataSource dataSource) {
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	@Override
	public int getByProductCode(String producCode, int dueDys, String type) {
		NPABucketConfiguration nPABucketConfiguration = new NPABucketConfiguration();
		nPABucketConfiguration.setProductCode(producCode);
		nPABucketConfiguration.setDueDays(dueDys);

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*)");
		selectSql.append(" From NPABUCKETSCONFIG");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where productCode =:productCode AND dueDays =:dueDays ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(nPABucketConfiguration);

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
	}

	@Override
	public int getNPABucketConfigurationById(long bucketID, String type) {
		NPABucketConfiguration nPABucketConfiguration = new NPABucketConfiguration();
		nPABucketConfiguration.setBucketID(bucketID);

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*)");
		selectSql.append(" From NPABUCKETSCONFIG");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where BucketID =:BucketID");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(nPABucketConfiguration);

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
	}

	@Override
	public List<NPABucketConfiguration> getNPABucketConfigurations() {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" configID, productCode, bucketID, dueDays, suspendProfit ");
		sql.append(" From NPABUCKETSCONFIG");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		RowMapper<NPABucketConfiguration> rowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(NPABucketConfiguration.class);

		List<NPABucketConfiguration> list = namedParameterJdbcTemplate.query(sql.toString(), rowMapper);

		logger.debug(Literal.LEAVING);
		return list;
	}

	@Override
	public List<NPABucketConfiguration> getNPABucketConfigByProducts(String productCode) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ProductCode", productCode);
		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" configID, productCode, bucketID, dueDays, suspendProfit ");
		sql.append(" From NPABUCKETSCONFIG Where ProductCode = :ProductCode");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		RowMapper<NPABucketConfiguration> rowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(NPABucketConfiguration.class);

		List<NPABucketConfiguration> list = namedParameterJdbcTemplate.query(sql.toString(), source, rowMapper);

		logger.debug(Literal.LEAVING);
		return list;
	}

}
