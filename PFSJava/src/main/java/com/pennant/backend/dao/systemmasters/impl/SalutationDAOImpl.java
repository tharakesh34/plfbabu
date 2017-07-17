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
 * FileName    		:  SalutationDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  03-05-2011    														*
 *                                                                  						*
 * Modified Date    :  03-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-05-2011       Pennant	                 0.1                                            * 
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
package com.pennant.backend.dao.systemmasters.impl;

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

import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.dao.systemmasters.SalutationDAO;
import com.pennant.backend.model.systemmasters.Salutation;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pff.core.DependencyFoundException;
import com.pennanttech.pff.core.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * DAO methods implementation for the <b>Salutation model</b> class.<br>
 * 
 */
public class SalutationDAOImpl extends BasisCodeDAO<Salutation> implements SalutationDAO {

	private static Logger logger = Logger.getLogger(SalutationDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public SalutationDAOImpl() {
		super();
	}
	
	/**
	 * Fetch the Record Salutations details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return Salutation
	 */
	@Override
	public Salutation getSalutationById(final String id, String type) {
		logger.debug(Literal.ENTERING);
		
		Salutation salutation = new Salutation();
		salutation.setId(id);
		StringBuilder selectSql = new StringBuilder();
		
		selectSql.append("Select SalutationCode, SaluationDesc, SalutationIsActive,SalutationGenderCode,SystemDefault,");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From BMTSalutations");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where SalutationCode =:SalutationCode");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(salutation);
		RowMapper<Salutation> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Salutation.class);

		try {
			salutation = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			salutation = null;
		}
		
		logger.debug(Literal.LEAVING);
		return salutation;
	}
	
	@Override
	public boolean isDuplicateKey(String salutationCode, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		String sql;
		String whereClause = "SalutationCode = :salutationCode";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("BMTSalutations", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("BMTSalutations_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "BMTSalutations_Temp", "BMTSalutations" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("salutationCode", salutationCode);

		Integer count = namedParameterJdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}
	
	@Override
	public String save(Salutation salutation, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("insert into BMTSalutations");
		sql.append(tableType.getSuffix());
		sql.append(" (SalutationCode, SaluationDesc, SalutationIsActive,SalutationGenderCode,SystemDefault,");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId,");
		sql.append(" RecordType, WorkflowId)");
		sql.append(" values(:SalutationCode, :SaluationDesc, :SalutationIsActive, :SalutationGenderCode,:SystemDefault,");
		sql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId,");
		sql.append(" :RecordType, :WorkflowId)");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(salutation);

		try {
			namedParameterJdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
		
		logger.debug(Literal.LEAVING);
		return salutation.getId();
	}
	
	@Override
	public void update(Salutation salutation, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL, ensure primary key will not be updated.
		StringBuilder sql = new StringBuilder("update BMTSalutations");
		sql.append(tableType.getSuffix());
		sql.append(" set SaluationDesc = :SaluationDesc,");
		sql.append(" SalutationIsActive = :SalutationIsActive, SalutationGenderCode = :SalutationGenderCode,SystemDefault=:SystemDefault,");
		sql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, ");
		sql.append(" RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId ");
		sql.append(" where SalutationCode =:SalutationCode");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(salutation);
		int recordCount = namedParameterJdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(Salutation salutation, TableType tableType) {
		logger.debug(Literal.ENTERING);
		
		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from BMTSalutations");
		sql.append(tableType.getSuffix());
		sql.append(" where SalutationCode =:SalutationCode");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));
		
		// Execute the SQL, binding the arguments.
	    logger.trace(Literal.SQL + sql.toString());		
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(salutation);
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
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	@Override
	public String getSystemDefaultCount(String salutationCode) {
		logger.debug(Literal.ENTERING);
		
		Salutation salutation = new Salutation();
		salutation.setSalutationCode(salutationCode);
		salutation.setSystemDefault(true);

		StringBuilder selectSql = new StringBuilder();

		selectSql.append("SELECT SalutationCode FROM  BMTSalutations_View ");
		selectSql.append(" Where SalutationCode != :SalutationCode and SystemDefault = :SystemDefault");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(salutation);
		String dftSalutationCode = "";
		try {
			dftSalutationCode = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, String.class);
        } catch (Exception e) {
        	logger.warn("Exception: ", e);
        	dftSalutationCode = "";
        }
		
		logger.debug(Literal.LEAVING);
		return dftSalutationCode;
	}

	@Override
    public void updateSytemDefaultByGender(String genderCode, boolean systemDefault) {
		logger.debug(Literal.ENTERING);
		
		StringBuilder updateSql = new StringBuilder();		
		updateSql.append("Update BMTSalutations  set SystemDefault=:SystemDefault ");
		updateSql.append(" Where SalutationGenderCode = :SalutationGenderCode");

		logger.debug("updateSql: "+ updateSql.toString());
		
		Salutation salutation = new Salutation();
		salutation.setSalutationGenderCode(genderCode);
		salutation.setSystemDefault(systemDefault);
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(salutation);
		this.namedParameterJdbcTemplate.update(updateSql.toString(),beanParameters);
		
		logger.debug(Literal.LEAVING);
    }
}