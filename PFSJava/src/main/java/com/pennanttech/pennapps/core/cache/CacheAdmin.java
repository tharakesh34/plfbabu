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
package com.pennanttech.pennapps.core.cache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
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

import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.resource.Literal;

public class CacheAdmin  {
	private static final Logger log = LogManager.getLogger(Cache.class);

	private NamedParameterJdbcTemplate jdbcTemplate;

	public CacheAdmin(DataSource dataSource) {
		super();
		setDataSource(dataSource);
	}

	public List<CacheStats> getCacheList() {
		log.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		MapSqlParameterSource source = new MapSqlParameterSource();

		sql.append(" select cluster_name, current_node, cluster_ip, cluster_size, cluster_members,");
		sql.append(" cache_count, cache_names, manager_cache_status, enabled, active,node_count, last_mnt_on, last_mnt_by");
		sql.append(" from cache_stats");

		RowMapper<CacheStats> romapper = ParameterizedBeanPropertyRowMapper.newInstance(CacheStats.class);

		log.trace(Literal.SQL + sql.toString());
		log.trace(Literal.LEAVING);
		try {
			return this.jdbcTemplate.query(sql.toString(), source, romapper);
		} catch (Exception e) {
			log.error(Literal.EXCEPTION, e);
		}

		return null;
	}

	
	public void delete(String clusterName, String IP, String currentNode) {
		log.debug(Literal.ENTERING);
		int recordCount = 0;
		
		MapSqlParameterSource paramMap = new MapSqlParameterSource();		
		paramMap.addValue("ClusterName", clusterName);
		paramMap.addValue("ClusterIp", "%" + IP + "%");
		paramMap.addValue("CurrentNode", "%" + StringUtils.substringBefore(currentNode, "-")+ "%");
		
		StringBuilder sql = new StringBuilder();
		sql.append("delete from cache_stats");
		sql.append(" where cluster_name =:ClusterName");
		sql.append(" and cluster_ip like :ClusterIp and current_node like :CurrentNode");
		log.trace(Literal.SQL + sql.toString());

		try {
			recordCount = this.jdbcTemplate.update(sql.toString(), paramMap);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		log.trace(Literal.LEAVING);
	}


	public int getNodeCount() {
		log.debug(Literal.ENTERING);
		StringBuilder sql = new StringBuilder();
		sql.append("select node_count from cache_stats where id=(select max(id) from cache_stats)");
		log.trace(Literal.SQL + sql.toString());
		log.debug(Literal.LEAVING);
		return this.jdbcTemplate.queryForObject(sql.toString(), new MapSqlParameterSource(), Integer.class);

	}


	public void insert(CacheStats cacheStats) {
		log.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("insert into cache_stats");
		sql.append("(cluster_name, current_node, cluster_ip, cluster_size, cluster_members, cache_count, cache_names,");
		sql.append(" manager_cache_status, enabled, active, node_count, last_mnt_by, last_mnt_on)");
		sql.append(" values(:ClusterName, :ClusterNode, :ClusterIp, :ClusterSize, :ClusterMembers, :CacheCount, :CacheNamesDet,");
		sql.append(" :ManagerCacheStatus, :Enabled, :Active, :NodeCount, :LastMntBy, :LastMntOn)");

		log.trace(Literal.SQL + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(cacheStats);
		try {
			this.jdbcTemplate.update(sql.toString(), beanParameters);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		log.debug(Literal.LEAVING);

	}

	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	
	public CacheStats getCacheStats(String clusterName, String currentNode) {
		log.debug(Literal.ENTERING);
		MapSqlParameterSource paramMap = new MapSqlParameterSource();

		StringBuilder sql = new StringBuilder();

		sql.append(" select cluster_name, current_node, cluster_ip,cluster_size,cluster_members, cache_count,");
		sql.append(" cache_names, manager_cache_status, enabled,active,node_count,last_mnt_on, last_mnt_by");
		sql.append(" from  cache_stats");
		sql.append(" where cluster_name =:ClusterName and current_node=:ClusterNode ");

		paramMap.addValue("ClusterName", clusterName);
		paramMap.addValue("ClusterNode", currentNode);
		RowMapper<CacheStats> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CacheStats.class);

		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), paramMap, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			log.error(Literal.EXCEPTION, e);
		}

		log.debug(Literal.LEAVING);
		return null;
	}

	
	public void update(CacheStats cacheStats) {
		log.debug(Literal.ENTERING);

		int recordCount = 0;
		
		StringBuilder sql = new StringBuilder();
		sql.append(" update cache_stats ");
		sql.append(" set cluster_ip  =:ClusterIp, cluster_size  =:ClusterSize, cluster_members =:ClusterMembers,");
		sql.append(" cache_count =:CacheCount, cache_names =:CacheNamesDet, manager_cache_status =:ManagerCacheStatus,");
		sql.append(" enabled =:Enabled, active =:Active,  node_count  =:NodeCount,");
		sql.append(" last_mnt_by =:LastMntBy, last_mnt_on=:LastMntOn");
		sql.append(" where cluster_name =:ClusterName and current_node =:ClusterNode ");

		log.trace(Literal.SQL + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(cacheStats);
		recordCount = this.jdbcTemplate.update(sql.toString(), beanParameters);

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		log.debug(Literal.LEAVING);
	}

	public Map<String, Object> getParameters() {
		log.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append(" select node_count, cache_verify_sleep, cache_update_sleep");
		sql.append(" from cache_parameters ");

		Map<String, Object> map = new HashMap<String, Object>();

		try {
			map = this.jdbcTemplate.queryForMap(sql.toString(), map);
		} catch (Exception e) {
			log.error(Literal.EXCEPTION, e);
		}
		log.debug(Literal.LEAVING);
		return map;
	}

}
