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
 * FileName    		:  NextIdViewSQLServerDaoImpl.java										*                           
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

import java.util.Collections;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.pennant.backend.dao.ErrorDetailsDAO;
import com.pennant.backend.dao.NextidviewDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.util.PennantConstants.SeqTables;
import com.pennanttech.pennapps.core.App;

public class NextIdViewSQLServerDaoImpl implements NextidviewDAO {
	private static Logger logger = Logger.getLogger(NextIdViewSQLServerDaoImpl.class);
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	private NamedParameterJdbcTemplate namedExtParameterJdbcTemplate;
	private ErrorDetailsDAO errorDetailsDAO;

	public NextIdViewSQLServerDaoImpl() {
		super();
	}

	public long getNextId(String seqName) throws DataAccessException {
		logger.debug("Entering");
		boolean identity = false;

		seqName = StringUtils.trimToEmpty(seqName);

		for (SeqTables seqTable : SeqTables.values()) {
			if (StringUtils.equals(seqName, seqTable.name())) {
				identity = true;
				break;
			}
		}

		switch (App.DATABASE) {
		case SQL_SERVER:
		case DB2:
			if (identity) {
				return getIdentitySeq(seqName);
			} else {
				return getSeq(seqName);
			}
		case ORACLE:
			if (identity) {
				return getOracleSeq(seqName);
			} else {
				return getSeq(seqName);
			}
		case POSTGRES:
			if (identity) {
				return getPsqlSeq(seqName);
			} else {
				return getSeq(seqName);
			}
		default:
			return getSeq(seqName);
		}
	}

	@Override
	public long getNextExtId(String seqName) {
		logger.debug("Entering");
		long count = 0;

		try {
			String updateSql = "update " + seqName + " set seqNo= seqNo+1 ";

			SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(
					seqName);
			this.namedExtParameterJdbcTemplate
					.update(updateSql, beanParameters);

			String selectCountSql = "select seqNo from " + seqName;
			count = this.namedExtParameterJdbcTemplate.getJdbcOperations()
					.queryForObject(selectCountSql, Long.class);

		} catch (Exception e) {
			logger.error("Exception: ", e);

		}

		logger.debug("Leaving" + count);
		return count;
	}

	/**
	 * gets sequence number from
	 */
	public long getSeqNumber(String seqName) {
		logger.debug("Entering");

		long count = 0;

		try {
			String selectCountSql = "select seqNo from " + seqName;
			count = this.namedParameterJdbcTemplate.getJdbcOperations()
					.queryForObject(selectCountSql, Long.class);
		} catch (Exception e) {
			logger.error("Exception: ", e);

		}
		logger.debug("Leaving" + count);
		return count;
	}

	/**
	 * updates table
	 */
	public void setSeqNumber(String seqName, long seqNumber) {
		logger.debug("Entering");

		try {
			Map<String, Long> namedParamters = Collections.singletonMap(
					"seqNumber", seqNumber);

			String updateSql = "update " + seqName + " set seqNo = :seqNumber";
			this.namedParameterJdbcTemplate.update(updateSql, namedParamters);
		} catch (Exception e) {
			logger.error("Exception: ", e);

		}
		logger.debug("Leaving");
	}

	private long getSeq(String seqName) throws DataAccessException {
		logger.debug("Entering");
		StringBuilder sql = null;

		// Update the sequence table
		sql = new StringBuilder("update ").append(seqName).append(
				" set seqNo = seqNo + 1");

		SqlParameterSource parameters = new BeanPropertySqlParameterSource(
				seqName);
		this.namedParameterJdbcTemplate.update(sql.toString(), parameters);

		// Get the newly created sequence
		sql = null;
		sql = new StringBuilder("select seqNo from ").append(seqName);

		return this.namedParameterJdbcTemplate.getJdbcOperations()
				.queryForObject(sql.toString(), Long.class);
	}

	private long getIdentitySeq(String seqName) throws DataAccessException {
		logger.debug("Entering");
		StringBuilder sql = null;
		MapSqlParameterSource parameters = null;

		sql = new StringBuilder("insert into ");
		sql.append(seqName);
		sql.append(" (Value)  values (:Value)");

		parameters = new MapSqlParameterSource();
		parameters.addValue("Value", 1);

		final KeyHolder keyHolder = new GeneratedKeyHolder();

		this.namedParameterJdbcTemplate.update(sql.toString(), parameters,
				keyHolder);

		parameters = null;
		sql = null;

		return keyHolder.getKey().longValue();
	}

	private long getOracleSeq(String seqName) throws DataAccessException {
		StringBuilder sql = null;

		sql = new StringBuilder("select ").append(seqName).append(
				".NEXTVAL from DUAL");

		return this.namedParameterJdbcTemplate.getJdbcOperations()
				.queryForObject(sql.toString(), Long.class);
	}
	
	private long getPsqlSeq(String seqName) throws DataAccessException {
		StringBuilder sql = null;

		sql = new StringBuilder("select nextval('").append(seqName).append("')");

		return this.namedParameterJdbcTemplate.getJdbcOperations()
				.queryForObject(sql.toString(), Long.class);
	}

	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(
				dataSource);
	}

	public void setExtDataSource(DataSource dataSource) {
		this.namedExtParameterJdbcTemplate = new NamedParameterJdbcTemplate(
				dataSource);
	}

	public ErrorDetails getErrorDetail(String errorId, String errorLanguage,
			String[] parameters) {
		return getErrorDetailsDAO().getErrorDetail(errorId, errorLanguage,
				parameters);
	}

	public ErrorDetailsDAO getErrorDetailsDAO() {
		return errorDetailsDAO;
	}

	public void setErrorDetailsDAO(ErrorDetailsDAO errorDetailsDAO) {
		this.errorDetailsDAO = errorDetailsDAO;
	}
}
