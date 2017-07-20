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
 * FileName    		:  GeneralDepartmentDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-05-2011    														*
 *                                                                  						*
 * Modified Date    :  05-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2011       Pennant	                 0.1                                            * 
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
import com.pennant.backend.dao.systemmasters.GeneralDepartmentDAO;
import com.pennant.backend.model.systemmasters.GeneralDepartment;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * DAO methods implementation for the <b>GeneralDepartment model</b> class.<br>
 * 
 */
public class GeneralDepartmentDAOImpl extends BasisCodeDAO<GeneralDepartment>
		implements GeneralDepartmentDAO {

	private static Logger logger = Logger.getLogger(GeneralDepartmentDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public GeneralDepartmentDAOImpl() {
		super();
	}
	
	/**
	 * Fetch the Record  GeneralDepartment details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return GeneralDepartment
	 */
	@Override
	public GeneralDepartment getGeneralDepartmentById(final String id, String type) {
		logger.debug("Entering");
		GeneralDepartment generalDepartment = new GeneralDepartment();
		generalDepartment.setId(id);
		
		StringBuilder selectSql = new StringBuilder("Select GenDepartment, GenDeptDesc, GenDeptIsActive," );
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId" );
		selectSql.append(" From RMTGenDepartments");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where GenDepartment =:GenDepartment");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(generalDepartment);
		RowMapper<GeneralDepartment> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(GeneralDepartment.class);
		
		try{
			generalDepartment = this.namedParameterJdbcTemplate.queryForObject(
					selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			generalDepartment = null;
		}
		logger.debug("Leaving");
		return generalDepartment;
	}
	
	/**
	 * @param dataSource the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the RMTGenDepartments or RMTGenDepartments_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete GeneralDepartment by key GenDepartment
	 * 
	 * @param GeneralDepartment (generalDepartment)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	public void delete(GeneralDepartment generalDepartment, TableType tableType) {
		logger.debug(Literal.ENTERING);
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder(" Delete From RMTGenDepartments" );
		deleteSql.append(tableType.getSuffix());
		deleteSql.append(" Where GenDepartment =:GenDepartment");
		deleteSql.append(QueryUtil.getConcurrencyCondition(tableType));
		
		logger.trace(Literal.SQL + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(generalDepartment);

		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
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
	 * This method insert new Records into RMTGenDepartments or RMTGenDepartments_Temp.
	 *
	 * save GeneralDepartment 
	 * 
	 * @param GeneralDepartment (generalDepartment)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public String save(GeneralDepartment generalDepartment, TableType tableType) {
		logger.debug(Literal.ENTERING);
		StringBuilder insertSql = new StringBuilder("Insert Into RMTGenDepartments" );
		insertSql.append(tableType.getSuffix());
		insertSql.append(" (GenDepartment, GenDeptDesc, GenDeptIsActive,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)" );
		insertSql.append(" Values(:GenDepartment, :GenDeptDesc, :GenDeptIsActive," );
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode," );
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.trace(Literal.SQL + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(generalDepartment);
		
		try{
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
		logger.debug(Literal.LEAVING);
		return generalDepartment.getId();
	}
	
	/**
	 * This method updates the Record RMTGenDepartments or RMTGenDepartments_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update GeneralDepartment by key GenDepartment and Version
	 * 
	 * @param GeneralDepartment (generalDepartment)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(GeneralDepartment generalDepartment, TableType tableType) {
		logger.debug(Literal.ENTERING);
		int recordCount = 0;
		
		StringBuilder updateSql = new StringBuilder("Update RMTGenDepartments");
		updateSql.append(tableType.getSuffix());
		updateSql.append(" Set GenDeptDesc = :GenDeptDesc, GenDeptIsActive = :GenDeptIsActive," );
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode," );
		updateSql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId," );
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId" );
		updateSql.append(" Where GenDepartment =:GenDepartment");
		updateSql.append(QueryUtil.getConcurrencyCondition(tableType));
		
		logger.trace(Literal.SQL +  updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(generalDepartment);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public boolean isDuplicateKey(String genDepartmentCode, TableType tableType) {
		logger.debug(Literal.ENTERING);
		// Prepare the SQL.
		String sql;
		String whereClause = "GenDepartment = :genDepartmentId";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("RMTGenDepartments", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("RMTGenDepartments_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "RMTGenDepartments_Temp", "RMTGenDepartments" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("genDepartmentId", genDepartmentCode);

		Integer count = namedParameterJdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}
	
}