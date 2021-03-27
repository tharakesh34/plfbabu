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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.pennant.backend.dao.WorkFlowDetailsDAO;
import com.pennant.backend.model.WorkFlowDetails;
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
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" WorkflowId, WorkFlowType, WorkFlowSubType, WorkFlowDesc, WorkFlowXml, WorkFlowRoles");
		sql.append(", FirstTaskOwner, WorkFlowActive, Version, LastMntBy, LastMntOn");
		sql.append(" from WorkFlowDetails");
		sql.append(" where WorkFlowType = ? and WorkFlowActive= ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { workFlowType, 1 },
					new RowMapper<WorkFlowDetails>() {
						@Override
						public WorkFlowDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
							WorkFlowDetails wfd = new WorkFlowDetails();

							wfd.setWorkflowId(rs.getLong("WorkflowId"));
							wfd.setWorkFlowType(rs.getString("WorkFlowType"));
							wfd.setWorkFlowSubType(rs.getString("WorkFlowSubType"));
							wfd.setWorkFlowDesc(rs.getString("WorkFlowDesc"));
							wfd.setWorkFlowXml(rs.getString("WorkFlowXml"));
							wfd.setWorkFlowRoles(rs.getString("WorkFlowRoles"));
							wfd.setFirstTaskOwner(rs.getString("FirstTaskOwner"));
							wfd.setWorkFlowActive(rs.getBoolean("WorkFlowActive"));
							wfd.setVersion(rs.getInt("Version"));
							wfd.setLastMntBy(rs.getLong("LastMntBy"));
							wfd.setLastMntOn(rs.getTimestamp("LastMntOn"));

							return wfd;
						}
					});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

	public List<WorkFlowDetails> getActiveWorkFlowDetails() {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" WorkflowId, WorkFlowType, WorkFlowSubType, WorkFlowDesc, WorkFlowXml, WorkFlowRoles");
		sql.append(", FirstTaskOwner, WorkFlowActive, Version, LastMntBy, LastMntOn");
		sql.append(" from WorkFlowDetails");
		sql.append(" where WorkFlowActive = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int index = 1;
					ps.setInt(index++, 1);
				}
			}, new RowMapper<WorkFlowDetails>() {
				@Override
				public WorkFlowDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
					WorkFlowDetails wfd = new WorkFlowDetails();

					wfd.setWorkflowId(rs.getLong("WorkflowId"));
					wfd.setWorkFlowType(rs.getString("WorkFlowType"));
					wfd.setWorkFlowSubType(rs.getString("WorkFlowSubType"));
					wfd.setWorkFlowDesc(rs.getString("WorkFlowDesc"));
					wfd.setWorkFlowXml(rs.getString("WorkFlowXml"));
					wfd.setWorkFlowRoles(rs.getString("WorkFlowRoles"));
					wfd.setFirstTaskOwner(rs.getString("FirstTaskOwner"));
					wfd.setWorkFlowActive(rs.getBoolean("WorkFlowActive"));
					wfd.setVersion(rs.getInt("Version"));
					wfd.setLastMntBy(rs.getLong("LastMntBy"));
					wfd.setLastMntOn(rs.getTimestamp("LastMntOn"));

					return wfd;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
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
		long workFlowId = getNextValue("SeqWorkFlowDetails");
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
	private WorkFlowDetails loadWorkFlowDetails(long id) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" WorkflowId, WorkFlowType, WorkFlowSubType, WorkFlowDesc, WorkFlowXml, WorkFlowRoles");
		sql.append(", FirstTaskOwner, WorkFlowActive, Version, LastMntBy, LastMntOn, JsonDesign");
		sql.append(" from WorkFlowDetails");
		sql.append(" where WorkFlowId = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { id }, (rs, rowNum) -> {
				WorkFlowDetails wfd = new WorkFlowDetails();

				wfd.setWorkflowId(rs.getLong("WorkflowId"));
				wfd.setWorkFlowType(rs.getString("WorkFlowType"));
				wfd.setWorkFlowSubType(rs.getString("WorkFlowSubType"));
				wfd.setWorkFlowDesc(rs.getString("WorkFlowDesc"));
				wfd.setWorkFlowXml(rs.getString("WorkFlowXml"));
				wfd.setWorkFlowRoles(rs.getString("WorkFlowRoles"));
				wfd.setFirstTaskOwner(rs.getString("FirstTaskOwner"));
				wfd.setWorkFlowActive(rs.getBoolean("WorkFlowActive"));
				wfd.setVersion(rs.getInt("Version"));
				wfd.setLastMntBy(rs.getLong("LastMntBy"));
				wfd.setLastMntOn(rs.getTimestamp("LastMntOn"));
				wfd.setJsonDesign(rs.getString("JsonDesign"));

				return wfd;
			});
		} catch (EmptyResultDataAccessException e) {
			logger.warn("WorkFlow Details not found in WorkFlowDetails table for the specified WorkFlowId >> {}", id);
		}

		return null;
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
			rowCount = this.jdbcTemplate.queryForObject(selectListSql, beanParameters, Long.class);
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
			version = this.jdbcTemplate.queryForObject(selectListSql, beanParameters, Integer.class);
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
			int rowCount = this.jdbcTemplate.queryForObject(selectListSql, beanParameters, Integer.class);
			if (rowCount > 0) {
				result = true;
			}
		} catch (Exception e) {
			logger.warn("Exception: ", e);
		}
		return result;
	}
}
