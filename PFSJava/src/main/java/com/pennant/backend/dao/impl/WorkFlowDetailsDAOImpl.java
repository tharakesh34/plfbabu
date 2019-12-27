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
 *
 * FileName    		:  WorkFlowDetailsDAOImpl.java											*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  26-04-2011															*
 *                                                                  
 * Modified Date    :  26-04-2011															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-04-2011       Pennant	                 0.1                                            * 
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
package com.pennant.backend.dao.impl;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.pennant.backend.dao.WorkFlowDetailsDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class WorkFlowDetailsDAOImpl extends SequenceDao<WorkFlowDetails> implements WorkFlowDetailsDAO {
	private static Logger logger = LogManager.getLogger(WorkFlowDetailsDAOImpl.class);

	// Adding a new cache property :
	private LoadingCache<String, WorkFlowDetails> workflowCache = CacheBuilder.newBuilder().maximumSize(100)
			.expireAfterAccess(30, TimeUnit.MINUTES).build(new CacheLoader<String, WorkFlowDetails>() {
				@Override
				public WorkFlowDetails load(String parameter) throws Exception {
					logger.debug("parameter = " + parameter);
					// Make the expensive call
					return loadWorkFlowDetails(Long.parseLong(parameter));
				}
			});

	public WorkFlowDetailsDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record Work Flow details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return workFlowDetails
	 */
	// Updating the existing methods :
	public WorkFlowDetails getWorkFlowDetailsByID(long id) {
		try {
			return workflowCache.get(String.valueOf(id));
		} catch (ExecutionException e) {
			logger.error("Exception: Loading data from cache ", e);
		}
		return loadWorkFlowDetails(id);
	}

	/**
	 * This method get the module from method getWorkFlowDetailsByType() and set the new record flag as true and return
	 * workFlowDetails
	 * 
	 * 
	 * @return workFlowDetails
	 */
	public WorkFlowDetails getWorkFlowDetailsByFlowType(String workFlowType, boolean api) {
		StringBuilder sql = new StringBuilder();
		sql.append("select WorkFlowId, WorkFlowType, WorkFlowSubType, WorkFlowDesc");
		sql.append(", WorkFlowXml, WorkFlowRoles, FirstTaskOwner, WorkFlowActive");
		sql.append(", Version, LastMntBy, LastMntOn from WorkFlowDetails");
		sql.append(" where WorkFlowType = :WorkFlowType and WorkFlowActive= :WorkFlowActive");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("WorkFlowType", workFlowType);
		parameterSource.addValue("WorkFlowActive", 1);

		RowMapper<WorkFlowDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(WorkFlowDetails.class);

		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), parameterSource, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			if (!api) {
				throw new AppException("Workflow details not avilable for the workflow type " + workFlowType);
			} else {
				return null;
			}
		}
	}

	public List<WorkFlowDetails> getActiveWorkFlowDetails() {
		logger.debug("Entering + getWorkFlowDetailsByID()");
		String selectListSql = "select WorkFlowId, WorkFlowType, WorkFlowSubType,"
				+ " WorkFlowDesc, WorkFlowXml, WorkFlowRoles, FirstTaskOwner, WorkFlowActive, "
				+ " Version , LastMntBy, LastMntOn from WorkFlowDetails where WorkFlowActive=1";
		logger.debug("selectListSql: " + selectListSql);
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(new WorkFlowDetails());
		RowMapper<WorkFlowDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(WorkFlowDetails.class);
		return this.jdbcTemplate.query(selectListSql, beanParameters, typeRowMapper);

	}

	/**
	 * This method insert new Records into WorkFlowDetails .
	 * 
	 * save WorkFlowDetails
	 * 
	 * @param WorkFlowDetails
	 *            (workFlowDetails)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return workFlowId
	 * 
	 */
	public long save(WorkFlowDetails workFlowDetails) {
		logger.debug("Entering + save()");
		long workFlowId = getNextId("SeqWorkFlowDetails");
		workFlowDetails.setId(workFlowId);
		String insertSql = "insert into WorkFlowDetails (WorkFlowId, WorkFlowType, "
				+ " WorkFlowSubType, WorkFlowDesc, WorkFlowXml, WorkFlowRoles,"
				+ " FirstTaskOwner , WorkFlowActive, Version , LastMntBy, LastMntOn, JsonDesign,roleCode,nextRoleCode,taskId,nextTaskId) "
				+ " values(:WorkFlowId, :WorkFlowType, :WorkFlowSubType, :WorkFlowDesc, "
				+ " :WorkFlowXml,:WorkFlowRoles, :FirstTaskOwner, :WorkFlowActive,"
				+ " :Version , :LastMntBy, :LastMntOn, :JsonDesign, :roleCode, :nextRoleCode, :taskId, :nextTaskId)";
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(workFlowDetails);
		this.jdbcTemplate.update(insertSql, beanParameters);

		return workFlowId;
	}

	public void update(WorkFlowDetails workFlowDetails) {
		logger.debug("Entering + update()");
		int recordCount = 0;
		String updateSql = "update WorkFlowDetails set WorkFlowActive= :WorkFlowActive  ,"
				+ " version=:version, lastMntBy= :lastMntBy ,lastMntOn= :lastMntOn  " + "where WorkFlowId= :WorkFlowId";
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(workFlowDetails);
		recordCount = this.jdbcTemplate.update(updateSql, beanParameters);
		logger.info("Number of Records Update :" + recordCount);
		clearWorkflowCache(workFlowDetails.getWorkFlowId()); // added this line to clear the cache after update.
	}

	// Adding new private methods :
	private WorkFlowDetails loadWorkFlowDetails(long id) { // same code from the current method getWorkflowDetailsById()
		logger.debug("id = " + id);
		WorkFlowDetails workFlowDetails = new WorkFlowDetails();
		workFlowDetails.setId(id);
		logger.debug("Entering + getWorkFlowDetailsByID()");

		String selectListSql = "select WorkFlowId, WorkFlowType, WorkFlowSubType, WorkFlowDesc,"
				+ " WorkFlowXml, WorkFlowRoles,FirstTaskOwner, WorkFlowActive, "
				+ "Version , LastMntBy, LastMntOn, JsonDesign from WorkFlowDetails where WorkFlowId =:WorkFlowId";
		logger.debug("selectListSql: " + selectListSql);

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(workFlowDetails);
		RowMapper<WorkFlowDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(WorkFlowDetails.class);
		try {
			workFlowDetails = this.jdbcTemplate.queryForObject(selectListSql, beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			workFlowDetails = null;
		}
		return workFlowDetails;
	}

	public void clearWorkflowCache(long id) {
		try {
			workflowCache.invalidate(String.valueOf(id));
		} catch (Exception ex) {
			logger.warn("Exception: ", ex);
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public long getWorkFlowDetailsCountByID(long WorkFlowId) {
		long rowCount = 0;
		WorkFlowDetails workFlowDetails = new WorkFlowDetails();
		workFlowDetails.setId(WorkFlowId);
		String selectListSql = "select count(WorkFlowId) from WorkFlowDetails where WorkFlowId =:WorkFlowId AND WorkFlowActive=1";
		logger.debug("selectListSql: " + selectListSql);
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(workFlowDetails);
		try {
			rowCount = this.jdbcTemplate.queryForLong(selectListSql, beanParameters);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			workFlowDetails = null;
			rowCount = 0;
		}
		return rowCount;
	}

	@SuppressWarnings("deprecation")
	@Override
	public int getWorkFlowDetailsVersionByID(long WorkFlowId) {
		int version = 0;
		WorkFlowDetails workFlowDetails = new WorkFlowDetails();
		workFlowDetails.setId(WorkFlowId);
		String selectListSql = "select version from WorkFlowDetails where WorkFlowId =:WorkFlowId AND WorkFlowActive=1";
		logger.debug("selectListSql: " + selectListSql);
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(workFlowDetails);
		try {
			version = this.jdbcTemplate.queryForInt(selectListSql, beanParameters);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			workFlowDetails = null;
			version = 0;
		}
		System.out.println("Returning version = " + version);
		return version;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean isworkFlowTypeExist(String workFlowType) {
		logger.debug("Entering + isworkFlowTypeExist()");

		WorkFlowDetails workFlowDetails = new WorkFlowDetails();
		workFlowDetails.setWorkFlowType(workFlowType);

		String selectListSql = "select count(*) from WorkFlowDetails  where WorkFlowType =:WorkFlowType";
		logger.debug("selectListSql: " + selectListSql);

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(workFlowDetails);
		boolean result = false;
		try {
			int rowCount = this.jdbcTemplate.queryForInt(selectListSql, beanParameters);
			if (rowCount > 0) {
				result = true;
			}
		} catch (Exception e) {
			logger.warn("Exception: ", e);
		}
		return result;
	}
}
