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
 * FileName    		:  ReportListDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  23-01-2012    														*
 *                                                                  						*
 * Modified Date    :  23-01-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 23-01-2012       Pennant	                 0.1                                            * 
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

import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.dao.reports.ReportListDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.reports.ReportList;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pff.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>ReportList model</b> class.<br>
 * 
 */
public class ReportListDAOImpl extends BasisCodeDAO<ReportList> implements ReportListDAO {

	private static Logger logger = Logger.getLogger(ReportListDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	public ReportListDAOImpl() {
		super();
	}
	
	/**
	 * This method set the Work Flow id based on the module name and return the new ReportList 
	 * @return ReportList
	 */
	@Override
	public ReportList getReportList() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("ReportList");
		ReportList reportList= new ReportList();
		if (workFlowDetails!=null){
			reportList.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return reportList;
	}

	/**
	 * This method get the module from method getReportList() and set the new record flag as true and return ReportList()   
	 * @return ReportList
	 */
	@Override
	public ReportList getNewReportList() {
		logger.debug("Entering");
		ReportList reportList = getReportList();
		reportList.setNewRecord(true);
		logger.debug("Leaving");
		return reportList;
	}

	/**
	 * Fetch the Record  List Report Configuration details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return ReportList
	 */
	@Override
	public ReportList getReportListById(final String id, String type) {
		logger.debug("Entering");
		ReportList reportList = new ReportList();
		reportList.setId(id);
		StringBuilder selectSql = new StringBuilder();
		
		selectSql.append("Select Code,Module, FieldLabels, FieldValues, FieldType, Addfields, ReportFileName, ReportHeading, ModuleType, FormatReq, ");
		selectSql.append(" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId" );
		selectSql.append(" From ReportList");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where Code =:Code");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(reportList);
		RowMapper<ReportList> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ReportList.class);
		
		try{
			reportList = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			reportList = null;
		}
		logger.debug("Leaving");
		return reportList;
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the ReportList or ReportList_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete List Report Configuration by key Module
	 * 
	 * @param List Report Configuration (reportList)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(ReportList reportList,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete From ReportList");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where Code =:Code");
		
		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(reportList);
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
	 * This method insert new Records into ReportList or ReportList_Temp.
	 *
	 * save List Report Configuration 
	 * 
	 * @param List Report Configuration (reportList)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public String save(ReportList reportList,String type) {
		logger.debug("Entering");
		
		StringBuilder insertSql =new StringBuilder("Insert Into ReportList");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (Code, Module, FieldLabels, FieldValues, FieldType, Addfields, ReportFileName, ReportHeading, ModuleType,FormatReq, ");
		insertSql.append("  Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, ");
		insertSql.append("  RecordType, WorkflowId)");
		insertSql.append("  Values(:Code, :Module, :FieldLabels, :FieldValues, :FieldType, :Addfields, :ReportFileName, :ReportHeading, :ModuleType,:FormatReq, ");
		insertSql.append("  :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, ");
		insertSql.append(" :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(reportList);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return reportList.getId();
	}
	
	/**
	 * This method updates the Record ReportList or ReportList_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update List Report Configuration by key Module and Version
	 * 
	 * @param List Report Configuration (reportList)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(ReportList reportList,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update ReportList");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set Module = :Module, FieldLabels = :FieldLabels, FieldValues = :FieldValues, FieldType = :FieldType, ");
		updateSql.append(" Addfields = :Addfields, ReportFileName = :ReportFileName, ReportHeading = :ReportHeading, ModuleType = :ModuleType,FormatReq=:FormatReq, ");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, " );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode,NextRoleCode = :NextRoleCode, TaskId = :TaskId," );
		updateSql.append(" NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId" );
		updateSql.append(" Where Code =:Code");
		if (!type.endsWith("_Temp")){
			updateSql.append("  AND Version= :Version-1");
		}
		
		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(reportList);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}
	
}