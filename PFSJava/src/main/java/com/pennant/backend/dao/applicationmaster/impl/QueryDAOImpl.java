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
 * FileName    		:  QueryDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  04-07-2013    														*
 *                                                                  						*
 * Modified Date    :  04-07-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 04-07-2013       Pennant	                 0.1                                            * 
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


import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.applicationmaster.QueryDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.applicationmaster.Query;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;



/**
 * DAO methods implementation for the <b>Query model</b> class.<br>
 * 
 */

public class QueryDAOImpl extends BasisCodeDAO<Query> implements QueryDAO {

	private static Logger logger = Logger.getLogger(QueryDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	public QueryDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record  Query Details details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return Query
	 */
	@Override
	public Query getQueryById(String queryCode, String queryModule, String type) {
		logger.debug("Entering");
		
		Query query = new Query();
		query.setQueryCode(queryCode);
		query.setQueryModule(queryModule);
		
		StringBuilder selectSql = new StringBuilder("Select  QueryCode, QueryModule, QueryDesc, SqlQuery, ActualBlock, SubQuery");
		selectSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId,Active");
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(",QueryModuleDesc , TableName, ResultColumns, DisplayColumns");
			selectSql.append(",QueryModule");
		}
		selectSql.append(" From Queries");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where  QueryCode =:QueryCode AND QueryModule=:QueryModule");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(query);
		RowMapper<Query> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Query.class);
		
		try{
			query = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			query = null;
		}
		logger.debug("Leaving");
		return query;
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the Queries or Queries_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Query Details by key QueryCode
	 * 
	 * @param Query Details (query)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(Query query, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete From Queries");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where QueryCode =:QueryCode");
	
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(query);
		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}
	
	/**
	 * This method insert new Records into Queries or Queries_Temp.
	 *
	 * save Query Details 
	 * 
	 * @param Query Details (query)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@Override
	public String save(Query query,String type) {
		logger.debug("Entering");
		
		StringBuilder insertSql =new StringBuilder("Insert Into Queries");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (QueryCode, QueryModule, QueryDesc, SqlQuery, ActualBlock, SubQuery");
		insertSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId, Active)");
		insertSql.append(" Values(:QueryCode, :QueryModule, :QueryDesc, :SqlQuery, :ActualBlock, :SubQuery");
		insertSql.append(", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId, :Active)");
		
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(query);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return query.getId();
	}
	
	/**
	 * This method updates the Record Queries or Queries_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Query Details by key QueryCode and Version
	 * 
	 * @param Query Details (query)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(Query query, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update Queries");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set QueryModule = :QueryModule, QueryDesc = :QueryDesc, SqlQuery = :SqlQuery, ActualBlock = :ActualBlock, SubQuery = :SubQuery");
		updateSql.append(", Version= :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId, Active = :Active");
		updateSql.append(" Where QueryCode =:QueryCode");
		
		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(query);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}
}