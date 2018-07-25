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
 * FileName    		:  ReportFilterFieldsDAOImpl.java                                                   * 	  
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

package com.pennant.backend.dao.reports.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.reports.ReportFilterFieldsDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.reports.ReportFilterFields;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;

/**
 * DAO methods implementation for the <b>ReportFilterFields model</b> class.<br>
 * 
 */

public class ReportFilterFieldsDAOImpl extends SequenceDao<ReportFilterFields> implements ReportFilterFieldsDAO {

	private static Logger logger = Logger.getLogger(ReportFilterFieldsDAOImpl.class);

	
	public ReportFilterFieldsDAOImpl() {
		super();
	}
	
	/**
	 * This method set the Work Flow id based on the module name and return the new ReportFilterFields 
	 * @return ReportFilterFields
	 */
	@Override
	public ReportFilterFields getReportFilterFields() {
		logger.debug("Entering ");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("ReportFilterFields");
		ReportFilterFields reportFilterFields= new ReportFilterFields();
		if (workFlowDetails!=null){
			reportFilterFields.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving ");
		return reportFilterFields;
	}

	/**
	 * This method get the module from method getReportFilterFields() and set the new
	 * record flag as true and return ReportFilterFields()
	 * 
	 * @return ReportFilterFields
	 */
	@Override
	public ReportFilterFields getNewReportFilterFields() {
		logger.debug("Entering ");
		ReportFilterFields reportFilterFields = getReportFilterFields();
		reportFilterFields.setNewRecord(true);
		logger.debug("Leaving ");
		return reportFilterFields;
	}

	/**
	 * Fetch the Record  ReportFilterFields details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return ReportFilterFields
	 */
	@Override
	public ReportFilterFields getReportFilterFieldsById(final long id, String type) {
		logger.debug("Entering ");
		ReportFilterFields reportFilterFields = new ReportFilterFields();
		reportFilterFields.setId(id);

		StringBuilder selectSql = new StringBuilder("SELECT ReportID, FieldID, FieldName, FieldType, FieldLabel, FieldDBName," );
		selectSql.append(" AppUtilMethodName, ModuleName, LovHiddenFieldMethod, LovTextFieldMethod, MultiSelectSearch, FieldLength," );
		selectSql.append(" FieldMaxValue, FieldMinValue, SeqOrder, Mandatory, FieldConstraint, FieldErrorMessage," );
		selectSql.append(" WhereCondition, StaticValue, FieldWidth, FilterRequired, DefaultFilter,FilterFileds," );
		if(type.contains("View")){
		}
		selectSql.append(" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId" );
		selectSql.append(" FROM  ReportFilterFields");
		selectSql.append(StringUtils.trimToEmpty(type) );
		selectSql.append("  Where FieldID = :FieldID");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(reportFilterFields);
		RowMapper<ReportFilterFields> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ReportFilterFields.class);

		try{
			reportFilterFields = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			reportFilterFields = null;
		}
		logger.debug("Leaving ");
		return reportFilterFields;
	}

	/**
	 * Fetch the Record  ReportFilterFields details by key field
	 * 
	 * @param id (long)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return ReportFilterFields
	 */
	@Override
	public List<ReportFilterFields> getReportFilterFieldsByReportId(final long reportID,String type){
		logger.debug("Entering");
		ReportFilterFields reportFilterFields = new ReportFilterFields();
		reportFilterFields.setReportID(reportID);

		StringBuilder selectSql = new StringBuilder("SELECT ReportID, FieldID, FieldName, FieldType, FieldLabel, FieldDBName," );
		selectSql.append(" AppUtilMethodName, ModuleName, LovHiddenFieldMethod, LovTextFieldMethod, MultiSelectSearch, FieldLength," );
		selectSql.append(" FieldMaxValue, FieldMinValue, SeqOrder, Mandatory, FieldConstraint, FieldErrorMessage," );
		selectSql.append(" WhereCondition, StaticValue, FieldWidth, FilterRequired, DefaultFilter, FilterFileds," );
		if(type.contains("View")){
		}
		selectSql.append(" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId" );
		selectSql.append(" FROM  ReportFilterFields");
		selectSql.append(StringUtils.trimToEmpty(type) );
		selectSql.append("  Where ReportID =:ReportID ");
		selectSql.append(" order by SeqOrder ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(reportFilterFields);
		RowMapper<ReportFilterFields> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ReportFilterFields.class);

		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql.toString(), beanParameters,typeRowMapper);
	}


	/**
	 * This method Deletes the Record from the ReportFilterFields or ReportFilterFields_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete ReportFilterFields by key CcyCode
	 * 
	 * @param ReportFilterFields (reportFilterFields)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(ReportFilterFields reportFilterFields,String type) {
		logger.debug("Entering ");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder(" Delete From ReportFilterFields");
		deleteSql.append(StringUtils.trimToEmpty(type) );
		deleteSql.append(" Where ReportID =:ReportID AND  FieldID = :FieldID");
		
		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(reportFilterFields);

		try{
			recordCount = this.jdbcTemplate.update(deleteSql.toString(), beanParameters);

			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		}catch(DataAccessException e){
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving ");
	}

	
	/**
	 * Method for Deletion of ReportConfiguration Related List of ReportFilterFields
	 */
	public void deleteByReportId(final long reportID,String type){
		logger.debug("Entering");
		ReportFilterFields reportFilterFields = new ReportFilterFields();
		reportFilterFields.setReportID(reportID);

		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append("Delete From ReportFilterFields" );
		deleteSql.append(StringUtils.trimToEmpty(type) );
		deleteSql.append(" Where ReportID = :ReportID ");

		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(reportFilterFields);
		this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
		
	}
	/**
	 * This method insert new Records into ReportFilterFields or ReportFilterFields_Temp.
	 *
	 * save ReportFilterFields 
	 * 
	 * @param ReportFilterFields (reportFilterFields)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return String
	 * 
	 */
	@Override
	public long save(ReportFilterFields reportFilterFields,String type) {
		logger.debug("Entering ");

		if (reportFilterFields.getId()==Long.MIN_VALUE){
			reportFilterFields.setId(getNextId("SeqReportFilterFields"));
			logger.debug("get NextID:"+reportFilterFields.getId());
		}
		StringBuilder insertSql = new StringBuilder("Insert Into ReportFilterFields" );
		insertSql.append(StringUtils.trimToEmpty(type) );
		insertSql.append(" (ReportID, FieldID, FieldName, FieldType, FieldLabel, FieldDBName,");
		insertSql.append(" AppUtilMethodName, ModuleName, LovHiddenFieldMethod, LovTextFieldMethod, MultiSelectSearch, FieldLength," );
		insertSql.append(" FieldMaxValue, FieldMinValue, SeqOrder, Mandatory, FieldConstraint, FieldErrorMessage," );
		insertSql.append(" WhereCondition, StaticValue, FieldWidth, FilterRequired, DefaultFilter, FilterFileds," );
		insertSql.append(" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)" );
		insertSql.append(" Values( :ReportID, :FieldID, :FieldName, :FieldType, :FieldLabel, :FieldDBName,");
		insertSql.append(" :AppUtilMethodName, :ModuleName, :LovHiddenFieldMethod, :LovTextFieldMethod, :MultiSelectSearch, :FieldLength," );
		insertSql.append(" :FieldMaxValue, :FieldMinValue, :SeqOrder, :Mandatory, :FieldConstraint, :FieldErrorMessage," );
		insertSql.append(" :WhereCondition, :StaticValue, :FieldWidth, :FilterRequired, :DefaultFilter, :FilterFileds," );
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode," );
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(reportFilterFields);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving ");
		return reportFilterFields.getId();
	}

	/**
	 * This method updates the Record ReportFilterFields or ReportFilterFields_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update ReportFilterFields by key CcyCode and Version
	 * 
	 * @param ReportFilterFields (reportFilterFields)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(ReportFilterFields reportFilterFields,String type) {
		int recordCount = 0;
		logger.debug("Entering ");
		
		StringBuilder updateSql = new StringBuilder("Update ReportFilterFields" );
		updateSql.append(StringUtils.trimToEmpty(type) ); 
		updateSql.append(" Set FieldName = :FieldName, FieldType = :FieldType, FieldLabel = :FieldLabel");
		updateSql.append(" ,FieldDBName = :FieldDBName, AppUtilMethodName = :AppUtilMethodName, ModuleName = :ModuleName," );
		updateSql.append(" LovHiddenFieldMethod = :LovHiddenFieldMethod, LovTextFieldMethod = :LovTextFieldMethod,");
		updateSql.append(" MultiSelectSearch = :MultiSelectSearch, FieldLength = :FieldLength, FieldMaxValue = :FieldMaxValue," );
		updateSql.append(" FieldMinValue = :FieldMinValue, SeqOrder = :SeqOrder, Mandatory = :Mandatory," );
		updateSql.append(" FieldConstraint = :FieldConstraint, FieldErrorMessage = :FieldErrorMessage,");
		updateSql.append(" WhereCondition = :WhereCondition, StaticValue = :StaticValue, FieldWidth = :FieldWidth," );
		updateSql.append(" FilterRequired = :FilterRequired, DefaultFilter = :DefaultFilter, FilterFileds =:FilterFileds," );
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn," );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode," );
		updateSql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId," );
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId" );
		updateSql.append(" Where ReportID =:ReportID AND  FieldID = :FieldID");

		if (!type.endsWith("_Temp")){
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(reportFilterFields);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving ");
	}

}