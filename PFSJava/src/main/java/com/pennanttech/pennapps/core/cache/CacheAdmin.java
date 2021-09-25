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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.jfree.util.Log;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.resource.Literal;

public class CacheAdmin {
	private NamedParameterJdbcTemplate jdbcTemplate;
	private JdbcOperations jdbcOperations;

	public CacheAdmin(DataSource dataSource) {
		super();
		setDataSource(dataSource);
	}

	public List<CacheStats> getCacheList() {
		return this.jdbcOperations.query(CacheQueries.SELECT_CACHE_STATUS_LIST, (rs, rowNum) -> {
			CacheStats cs = new CacheStats();

			cs.setId(rs.getLong("Id"));
			cs.setClusterName(rs.getString("Cluster_Name"));
			cs.setCurrentNode(rs.getString("Current_Node"));
			cs.setClusterIp(rs.getString("Cluster_IP"));
			cs.setClusterSize(rs.getInt("Cluster_Size"));
			cs.setClusterMembers(rs.getString("Cluster_Members"));
			cs.setCacheCount(rs.getInt("Cache_Count"));
			cs.setCacheNames(rs.getString("Cache_Names"));
			cs.setManagerCacheStatus(rs.getString("Manager_Cache_Status"));
			cs.setEnabled(rs.getBoolean("Enabled"));
			cs.setActive(rs.getBoolean("Active"));
			cs.setNodeCount(rs.getInt("Node_Count"));
			cs.setLastMntOn(rs.getTimestamp("Last_Mnt_On"));
			cs.setLastMntBy(rs.getLong("Last_Mnt_By"));

			return cs;
		});
	}

	public void delete(String clusterName, String IP, String currentNode) {
		int recordCount = 0;

		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("ClusterName", clusterName);
		paramMap.addValue("ClusterIp", "%" + IP + "%");
		paramMap.addValue("CurrentNode", currentNode + "%");

		try {
			recordCount = this.jdbcTemplate.update(CacheQueries.DELETE_CACHE_STATUS, paramMap);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

	}

	public void delete(String clusterName) {
		int recordCount = 0;

		try {
			recordCount = this.jdbcOperations.update("delete from cache_stats Where Cluster_Name = ?",
					ps -> ps.setString(1, clusterName));
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

	}

	public void insert(CacheStats cacheStats) {
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(cacheStats);
		try {
			this.jdbcTemplate.update(CacheQueries.INSERT_CACHE_STATUS, beanParameters);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
	}

	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		this.jdbcOperations = this.jdbcTemplate.getJdbcOperations();

	}

	public CacheStats getCacheStats(String clusterName, String currentNode) {
		try {
			return this.jdbcOperations.queryForObject(CacheQueries.SELECT_CACHE_STATUS, (rs, rowNum) -> {
				CacheStats cs = new CacheStats();

				cs.setId(rs.getLong("Id"));
				cs.setClusterName(rs.getString("Cluster_Name"));
				cs.setCurrentNode(rs.getString("Current_Node"));
				cs.setClusterIp(rs.getString("Cluster_IP"));
				cs.setClusterSize(rs.getInt("Cluster_Size"));
				cs.setClusterMembers(rs.getString("Cluster_Members"));
				cs.setCacheCount(rs.getInt("Cache_Count"));
				cs.setCacheNames(rs.getString("Cache_Names"));
				cs.setManagerCacheStatus(rs.getString("Manager_Cache_Status"));
				cs.setEnabled(rs.getBoolean("Enabled"));
				cs.setActive(rs.getBoolean("Active"));
				cs.setNodeCount(rs.getInt("Node_Count"));

				return cs;
			}, clusterName, currentNode + "%");
		} catch (EmptyResultDataAccessException e) {
			//
		}

		return null;
	}

	public void update(CacheStats cs) {
		StringBuilder sql = new StringBuilder("Update Cache_Stats");
		sql.append(" Set Cluster_IP = ?, Cluster_Size = ?, Cluster_Members = ?");
		sql.append(", Cache_Count = ?, Cache_Names = ?, Manager_Cache_Status = ?");
		sql.append(", Enabled = ?, Active = ?,  Node_Count  = ?");
		sql.append(", Last_Mnt_By = ?, Last_Mnt_On = ?");
		sql.append(" Where ID = ?");

		Log.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, cs.getClusterIp());
			ps.setInt(index++, cs.getClusterSize());
			ps.setString(index++, cs.getClusterMembers());
			ps.setInt(index++, cs.getCacheCount());

			StringBuilder cache = new StringBuilder();
			for (String cacheName : cs.getCacheNames()) {
				cache.append(cacheName);
				if (cache.length() > 1) {
					cache.append(", ");
				}
			}
			ps.setString(index++, cache.toString());
			ps.setString(index++, cs.getManagerCacheStatus());
			ps.setBoolean(index++, cs.isEnabled());
			ps.setBoolean(index++, cs.isActive());
			ps.setInt(index++, cs.getNodeCount());
			ps.setLong(index++, cs.getLastMntBy());
			ps.setTimestamp(index++, cs.getLastMntOn());

			ps.setLong(index++, cs.getId());
		});
	}

	public Map<String, Object> getParameters() {
		Map<String, Object> map = new HashMap<String, Object>();

		try {
			this.jdbcTemplate.query(CacheQueries.SELECT_CACHE_PARAMETER, new RowCallbackHandler() {

				@Override
				public void processRow(ResultSet rs) throws SQLException {
					map.put(Cache.NODE_COUNT, rs.getInt(1));
					map.put(Cache.CACHE_UPDATE_SLEEP, rs.getLong(2));
					map.put(Cache.CACHE_VERIFY_SLEEP, rs.getLong(3));
					map.put(Cache.CLUSTER_SIZE, CacheManager.getClusterSize());
				}
			});
		} catch (Exception e) {
			//
		}

		return map;
	}

	public int getNodeCount() {
		return this.jdbcOperations.queryForObject("select node_count from cache_parameters", Integer.class);
	}

	public void updateParameters(CacheStats cacheStats) {
		int recordCount = 0;

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(cacheStats);
		recordCount = this.jdbcTemplate.update(CacheQueries.UPDATE_CACHE_PARAM, beanParameters);

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
	}
}
