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
 * FileName    		:  CacheDAOImpl.java                                                   	* 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-06-2017    														*
 *                                                                  						*
 * Modified Date    :  27-06-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 27-06-2017       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.cacheadministration.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.dao.cacheadministration.CacheDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennanttech.cache.CacheStats;
import com.pennanttech.dataengine.util.DateUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pff.core.Literal;
import com.pennanttech.pff.core.TableType;

public class CacheDAOImpl extends BasisCodeDAO<CacheStats> implements CacheDAO {
	private static Logger logger = Logger.getLogger(CacheDAOImpl.class);

	private DataSource dataSource;
	private NamedParameterJdbcTemplate namedJdbcTemplate;

	public CacheDAOImpl() {
		super();
	}

	@Override
	public List<CacheStats> getCacheList() {
		logger.debug(Literal.ENTERING);

		StringBuilder selectSql = new StringBuilder();
		MapSqlParameterSource source = new MapSqlParameterSource();

		selectSql.append(
				" Select Cluster_Name, Current_Node, Cluster_Ip,Cluster_Size,Cluster_Members,Cache_Count,Cache_Names,Manager_Cache_Status, ");
		selectSql.append(" Enabled,Active,Node_Count,LastMntOn, LastMntBy ");
		selectSql.append(" FROM  CACHE_STATS");

		RowMapper<CacheStats> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CacheStats.class);

		logger.debug("selectSql: " + selectSql.toString());
		logger.debug("Leaving");
		return this.namedJdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
	}

	@Override
	public String save(CacheStats entity, TableType tableType) {
		return null;
	}

	@Override
	public void update(CacheStats entity, TableType tableType) {

	}

	@Override
	public void delete(CacheStats cacheStats, TableType tableType) {

	}

	public void delete(String clusterName,String IP,String currentNode) {
		logger.trace(Literal.ENTERING);
		int recordCount = 0;				
		CacheStats cacheStats = new CacheStats();
		cacheStats.setClusterName(clusterName);
		cacheStats.setIpAddress("%"+IP+"%");
		cacheStats.setClusterNode("%"+StringUtils.substringBefore(currentNode, "-")+"%");
				
		StringBuilder deleteSql = new StringBuilder(" DELETE FROM CACHE_STATS WHERE CLUSTER_NAME =:ClusterName AND CLUSTER_IP like :IpAddress AND CURRENT_NODE like :ClusterNode ");
		logger.trace(Literal.SQL + deleteSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(cacheStats);
		
		try {			
			recordCount = this.namedJdbcTemplate.update(deleteSql.toString(), beanParameters);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.trace(Literal.LEAVING);
	}

	@Override
	public int getNodeCount() {
		logger.trace(Literal.ENTERING);
		StringBuilder selectSql = new StringBuilder(
				"SELECT NODE_COUNT FROM CACHE_STATS WHERE ID=(SELECT MAX(ID) FROM CACHE_STATS)");
		logger.trace(Literal.SQL + selectSql.toString());
		logger.trace(Literal.LEAVING);
		return this.namedJdbcTemplate.queryForObject(selectSql.toString(), new MapSqlParameterSource(), Integer.class);

	}

	@Override
	public void insert(CacheStats cacheStats) {
		logger.trace(Literal.ENTERING);

		StringBuilder insertSql = new StringBuilder("Insert Into CACHE_STATS");
		insertSql.append(
				" (Cluster_Name,Current_Node,Cluster_Ip,Cluster_Size,Cluster_Members,Cache_Count,Cache_Names, ");
		insertSql.append(" Manager_Cache_Status,Enabled,Active,Node_Count,LastMntBy,LastMntOn )");
		insertSql.append(
				" Values(:ClusterName, :ClusterNode, :IpAddress, :ClusterSize, :ClusterMembers, :CacheCount, :CacheNamesDet,");
		insertSql.append(" :ManagerStatus , :Enabled, :Active, :NodeCount, :LastMntBy,:LastMntOn) ");

		logger.trace(Literal.SQL + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(cacheStats);
		try {
			this.namedJdbcTemplate.update(insertSql.toString(), beanParameters);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.trace(Literal.LEAVING);

	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.namedJdbcTemplate = new NamedParameterJdbcTemplate(this.dataSource);
	}

	@Override
	public CacheStats getCacheStats(String clusterName, String currentNode) {
		logger.trace(Literal.ENTERING);
		CacheStats cacheStats = new CacheStats();
		cacheStats.setClusterName(clusterName);
		cacheStats.setClusterNode(currentNode);
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(
				" Select Cluster_Name, Current_Node, Cluster_Ip,Cluster_Size,Cluster_Members,Cache_Count,Cache_Names,Manager_Cache_Status, ");
		selectSql.append(" Enabled,Active,Node_Count,LastMntOn, LastMntBy");
		selectSql.append(" FROM  CACHE_STATS");
		selectSql.append(" Where Cluster_Name =:ClusterName and Current_Node=:ClusterNode ");

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(cacheStats);
		RowMapper<CacheStats> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CacheStats.class);

		try {
			cacheStats = this.namedJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			cacheStats = null;
		}

		logger.trace(Literal.LEAVING);
		return cacheStats;
	}

	@Override
	public void update(CacheStats cacheStats) {
		logger.trace(Literal.ENTERING);

		int recordCount = 0;
		StringBuilder updateSql = new StringBuilder("Update CACHE_STATS ");
		updateSql.append(
				" Set Cluster_Ip  =:IpAddress, Cluster_Size  =:ClusterSize, Cluster_Members =:ClusterMembers, Cache_Count =:CacheCount,");
		updateSql.append(
				" Cache_Names =:CacheNamesDet, Manager_Cache_Status =:ManagerStatus, Enabled =:Enabled, Active =:Active,  ");
		updateSql.append(" Node_Count  =:NodeCount, LastMntBy =:LastMntBy, LastMntOn =:LastMntOn ");
		updateSql.append(" Where Cluster_Name =:ClusterName and Current_Node =:ClusterNode ");

		logger.trace(Literal.SQL + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(cacheStats);
		recordCount = this.namedJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public Map<String, Object> getCacheParameters() {
		logger.trace(Literal.ENTERING);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" Select NODE_COUNT, CACHE_VERIFY_SLEEP, CACHE_UPDATE_SLEEP  ");
		selectSql.append(" FROM  CACHE_PARAMETERS ");
		Map<String, Object> map = new HashMap<String, Object>();
		map = this.namedJdbcTemplate.queryForMap(selectSql.toString(), map);
		logger.debug(Literal.LEAVING);
		return map;

	}

}
