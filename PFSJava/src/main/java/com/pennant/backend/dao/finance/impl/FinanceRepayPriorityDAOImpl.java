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
 * FileName    		:  FinanceRepayPriorityDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  16-03-2012    														*
 *                                                                  						*
 * Modified Date    :  16-03-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 16-03-2012       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.finance.impl;


import java.util.List;

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

import com.pennant.backend.dao.finance.FinanceRepayPriorityDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.finance.FinanceRepayPriority;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>FinanceRepayPriority model</b> class.<br>
 * 
 */

public class FinanceRepayPriorityDAOImpl extends BasisCodeDAO<FinanceRepayPriority> implements FinanceRepayPriorityDAO {

	private static Logger logger = Logger.getLogger(FinanceRepayPriorityDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	public FinanceRepayPriorityDAOImpl() {
		super();
	}


	/**
	 * Fetch the Record  Finance Repay Priority Details details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return FinanceRepayPriority
	 */
	@Override
	public FinanceRepayPriority getFinanceRepayPriorityById(final String id, String type) {
		logger.debug("Entering");
		FinanceRepayPriority financeRepayPriority = new FinanceRepayPriority();
		
		financeRepayPriority.setId(id);
		
		StringBuilder selectSql = new StringBuilder("Select FinType, FinPriority");
		selectSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(",lovDescFinTypeName");
		}
		selectSql.append(" From FinRpyPriority");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinType =:FinType");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeRepayPriority);
		RowMapper<FinanceRepayPriority> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceRepayPriority.class);
		
		try{
			financeRepayPriority = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			financeRepayPriority = null;
		}
		logger.debug("Leaving");
		return financeRepayPriority;
	}
	@Override
	public List<String> getFinanceRpyPriorByPriority(String finType, final int priority, String type) {
		logger.debug("Entering");
		
		FinanceRepayPriority financeRepayPriority = new FinanceRepayPriority();
		financeRepayPriority.setFinType(finType);
		financeRepayPriority.setFinPriority(priority);
		
		StringBuilder selectSql = new StringBuilder("Select FinType From FinRpyPriority");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinPriority =:FinPriority AND FinType != :FinType");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeRepayPriority);
		
		logger.debug("Leaving");
		return  this.namedParameterJdbcTemplate.queryForList(selectSql.toString(), beanParameters, String.class);	
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the FinRpyPriority or FinRpyPriority_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Finance Repay Priority Details by key FinType
	 * 
	 * @param Finance Repay Priority Details (financeRepayPriority)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(FinanceRepayPriority financeRepayPriority,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete From FinRpyPriority");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FinType =:FinType");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeRepayPriority);
		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		}catch(DataAccessException e){
			throw new DependencyFoundException(e);

		}
		logger.debug("Leaving");
	}
	
	/**
	 * This method insert new Records into FinRpyPriority or FinRpyPriority_Temp.
	 *
	 * save Finance Repay Priority Details 
	 * 
	 * @param Finance Repay Priority Details (financeRepayPriority)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@Override
	public String save(FinanceRepayPriority financeRepayPriority,String type) {
		logger.debug("Entering");
		
		StringBuilder insertSql =new StringBuilder("Insert Into FinRpyPriority");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (FinType, FinPriority");
		insertSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:FinType, :FinPriority");
		insertSql.append(", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeRepayPriority);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return financeRepayPriority.getId();
	}
	
	/**
	 * This method updates the Record FinRpyPriority or FinRpyPriority_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Finance Repay Priority Details by key FinType and Version
	 * 
	 * @param Finance Repay Priority Details (financeRepayPriority)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@Override
	public void update(FinanceRepayPriority financeRepayPriority,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update FinRpyPriority");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set FinPriority = :FinPriority");
		updateSql.append(", Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where FinType =:FinType");
		
		if (!type.endsWith("_Temp")){
			updateSql.append("  AND Version= :Version-1");
		}
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeRepayPriority);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}
	
	@Override
	public List<FinanceRepayPriority> getFinanceRepayPriorities() {
		logger.debug("Entering");
		FinanceRepayPriority financeRepayPriority=new FinanceRepayPriority();
		StringBuilder selectSql = new StringBuilder("Select FinType, FinPriority ");
		selectSql.append(" From FinRpyPriority");
		selectSql.append(" order by FinPriority asc");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeRepayPriority);
		RowMapper<FinanceRepayPriority> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceRepayPriority.class);
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);	
	}
	
}