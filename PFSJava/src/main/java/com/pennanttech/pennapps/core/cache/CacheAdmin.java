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

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.resource.Literal;

public class CacheAdmin {
	private static final Logger log = LogManager.getLogger(CacheAdmin.class);

	private NamedParameterJdbcTemplate jdbcTemplate;

	public CacheAdmin(DataSource dataSource) {
		super();
		setDataSource(dataSource);
	}

	public List<CacheStats> getCacheList() {
		log.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();

		RowMapper<CacheStats> romapper = ParameterizedBeanPropertyRowMapper.newInstance(CacheStats.class);

		log.debug(Literal.SQL + CacheQueries.SELECT_CACHE_STATUS_LIST);

		try {
			return this.jdbcTemplate.query(CacheQueries.SELECT_CACHE_STATUS_LIST, source, romapper);
		} catch (Exception e) {
			log.error(Literal.EXCEPTION, e);
		}

		log.debug(Literal.LEAVING);
		return null;
	}

	public void delete(String clusterName, String IP, String currentNode) {
		log.debug(Literal.ENTERING);
		int recordCount = 0;

		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("ClusterName", clusterName);
		paramMap.addValue("ClusterIp", "%" + IP + "%");
		paramMap.addValue("CurrentNode", "%" + StringUtils.substringBefore(currentNode, "-") + "%");

		log.debug(Literal.SQL + CacheQueries.DELETE_CACHE_STATUS);

		try {
			recordCount = this.jdbcTemplate.update(CacheQueries.DELETE_CACHE_STATUS, paramMap);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		log.debug(Literal.LEAVING);
	}

	public int getNodeCount() {
		log.debug(Literal.ENTERING);

		log.debug(Literal.SQL + CacheQueries.SELECT_CACHE_STATUS_NODE_COUNT);

		log.debug(Literal.LEAVING);

		return this.jdbcTemplate.queryForObject(CacheQueries.SELECT_CACHE_STATUS_NODE_COUNT,
				new MapSqlParameterSource(), Integer.class);
	}

	public void insert(CacheStats cacheStats) {
		log.debug(Literal.ENTERING);

		log.debug(Literal.SQL + CacheQueries.INSERT_CACHE_STATUS);

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(cacheStats);
		try {
			this.jdbcTemplate.update(CacheQueries.INSERT_CACHE_STATUS, beanParameters);
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
		paramMap.addValue("ClusterName", clusterName);
		paramMap.addValue("ClusterNode", currentNode);

		RowMapper<CacheStats> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CacheStats.class);

		log.debug(Literal.SQL + CacheQueries.SELECT_CACHE_STATUS_LIST);

		try {
			return this.jdbcTemplate.queryForObject(CacheQueries.SELECT_CACHE_STATUS_LIST, paramMap, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			log.error(Literal.EXCEPTION, e);
		}

		log.debug(Literal.LEAVING);
		return null;
	}

	public void update(CacheStats cacheStats) {
		log.debug(Literal.ENTERING);

		int recordCount = 0;

		log.debug(Literal.SQL + CacheQueries.UPDATE_CACHE_STATUS);
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(cacheStats);
		recordCount = this.jdbcTemplate.update(CacheQueries.UPDATE_CACHE_STATUS, beanParameters);

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		log.debug(Literal.LEAVING);
	}

	public Map<String, Object> getParameters() {
		log.debug(Literal.ENTERING);

		Map<String, Object> map = new HashMap<String, Object>();

		log.debug(Literal.SQL + CacheQueries.SELECT_CACHE_PARAMETER);

		try {
			this.jdbcTemplate.query(CacheQueries.SELECT_CACHE_PARAMETER, new RowCallbackHandler() {

				@Override
				public void processRow(ResultSet rs) throws SQLException {
					map.put(Cache.NODE_COUNT, rs.getInt(1));
					map.put(Cache.CACHE_UPDATE_SLEEP, rs.getLong(2));
					map.put(Cache.CACHE_VERIFY_SLEEP, rs.getLong(3));
				}
			});
		} catch (Exception e) {
			log.error(Literal.EXCEPTION, e);
		}

		log.debug(Literal.LEAVING);
		return map;
	}

	public void updateParameters(CacheStats cacheStats) {
		log.debug(Literal.ENTERING);

		int recordCount = 0;

		log.debug(Literal.SQL + CacheQueries.UPDATE_CACHE_PARAM);

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(cacheStats);
		recordCount = this.jdbcTemplate.update(CacheQueries.UPDATE_CACHE_PARAM, beanParameters);

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		log.debug(Literal.LEAVING);
	}
}
