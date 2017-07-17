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
 * FileName    		:  DedupFieldsDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  23-08-2011    														*
 *                                                                  						*
 * Modified Date    :  23-08-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 23-08-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.dedup.impl;

import java.util.ArrayList;
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

import com.pennant.backend.dao.dedup.DedupFieldsDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.BuilderTable;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.dedup.DedupFields;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pff.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>DedupFields model</b> class.<br>
 * 
 */
public class DedupFieldsDAOImpl extends BasisCodeDAO<DedupFields> implements DedupFieldsDAO {

	private static Logger logger = Logger.getLogger(DedupFieldsDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	public DedupFieldsDAOImpl() {
		super();
	}
	
	/**
	 * This method set the Work Flow id based on the module name and return the
	 * new DedupFields
	 * 
	 * @return DedupFields
	 */
	@Override
	public DedupFields getDedupFields() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("DedupFields");
		DedupFields dedupFields= new DedupFields();
		if (workFlowDetails!=null){
			dedupFields.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return dedupFields;
	}

	/**
	 * This method get the module from method getDedupFields() and set the new
	 * record flag as true and return DedupFields()
	 * 
	 * @return DedupFields
	 */
	@Override
	public DedupFields getNewDedupFields() {
		logger.debug("Entering");
		DedupFields dedupFields = getDedupFields();
		dedupFields.setNewRecord(true);
		logger.debug("Leaving");
		return dedupFields;
	}

	/**
	 * Fetch the Record Dedup Fields details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return DedupFields
	 */
	@Override
	public DedupFields getDedupFieldsByID(final String id, String type) {
		logger.debug("Entering");
		DedupFields dedupFields = new DedupFields();
		dedupFields.setId(id);
		
		StringBuilder selectListSql = new StringBuilder("Select FieldName, FieldControl,RefType , ");
		selectListSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, ");
		selectListSql.append(" TaskId, NextTaskId, RecordType, WorkflowId ");
		selectListSql.append(" From DedupFields");
		selectListSql.append(StringUtils.trimToEmpty(type));
		selectListSql.append(" Where FieldName = :FieldName ");
		
		logger.debug("selectListSql: " + selectListSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(dedupFields);
		RowMapper<DedupFields> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(DedupFields.class);
		
		try{
			dedupFields = this.namedParameterJdbcTemplate.queryForObject(
					selectListSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			dedupFields = null;
		}
		logger.debug("Leaving");
		return dedupFields;
	}
	
	/**
	 * To Set dataSource
	 * 
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(
				dataSource);
	}
	
	/**
	 * This method Deletes the Record from the DedupFields or DedupFields_Temp.
	 * if Record not deleted then throws DataAccessException with error 41003.
	 * delete Dedup Fields by key FieldName
	 * 
	 * @param Dedup
	 *            Fields (dedupFields)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	public void delete(DedupFields dedupFields,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete From DedupFields");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FieldName =:FieldName");

		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(dedupFields);
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
	 * This method insert new Records into DedupFields or DedupFields_Temp.
	 * 
	 * save Dedup Fields
	 * 
	 * @param Dedup
	 *            Fields (dedupFields)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public String save(DedupFields dedupFields,String type) {
		logger.debug("Entering");
		
		StringBuilder insertSql =new StringBuilder("Insert Into DedupFields");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (FieldName, FieldControl,RefType , ");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode,");
		insertSql.append(" NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:FieldName, :FieldControl,:RefType,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode,");
		insertSql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(dedupFields);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return dedupFields.getId();
	}
	
	/**
	 * This method updates the Record DedupFields or DedupFields_Temp. if Record
	 * not updated then throws DataAccessException with error 41004. update
	 * Dedup Fields by key FieldName and Version
	 * 
	 * @param Dedup
	 *            Fields (dedupFields)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(DedupFields dedupFields,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		
		StringBuilder	updateSql =new StringBuilder("Update DedupFields");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set FieldControl = :FieldControl,RefType=:RefType , ");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode,");
		updateSql.append("TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where FieldName =:FieldName");

		if (!type.endsWith("_Temp")){
			updateSql.append(" AND Version= :Version-1");
		}

		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(dedupFields);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}
	
	/**
	 * This method return the columns of the DedupFields table *
	 * 
	 * @return List
	 * 
	 * @throws EmptyResultDataAccessException
	 * 
	 */
	public List<BuilderTable> getFieldList(String queryModule) {
		logger.debug("Entering");		
		
		List<BuilderTable> fieldList = new ArrayList<BuilderTable>();
		
		String selectListSql = " select fieldName,fieldDesc ,fieldControl from DedupFields where QueryModule='"+queryModule+"'";
		RowMapper<BuilderTable> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(BuilderTable.class);
		logger.debug("selectSql: "+ selectListSql.toString());


		try {
			fieldList = this.namedParameterJdbcTemplate.getJdbcOperations()
					.query(selectListSql, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			fieldList = null;
		}

		logger.debug("Leaving");
		return fieldList;
	}
	
	
}