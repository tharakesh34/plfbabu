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
 * FileName    		:  ReportConfigurationDAOImpl.java                                                   * 	  
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

import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.dao.reports.ReportConfigurationDAO;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.reports.ReportConfiguration;
import com.pennant.backend.model.reports.ReportsMonthEndConfiguration;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>ReportConfiguration model</b> class.<br>
 * 
 */

public class ReportConfigurationDAOImpl extends BasisNextidDaoImpl<ReportConfiguration> implements ReportConfigurationDAO {

	private static Logger logger = Logger.getLogger(ReportConfigurationDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public ReportConfigurationDAOImpl() {
		super();
	}
	
	/**
	 * This method set the Work Flow id based on the module name and return the new ReportConfiguration 
	 * @return ReportConfiguration
	 */
	@Override
	public ReportConfiguration getReportConfiguration() {
		logger.debug("Entering ");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("ReportConfiguration");
		ReportConfiguration reportConfiguration= new ReportConfiguration();
		if (workFlowDetails!=null){
			reportConfiguration.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving ");
		return reportConfiguration;
	}

	/**
	 * This method get the module from method getReportConfiguration() and set the new
	 * record flag as true and return ReportConfiguration()
	 * 
	 * @return ReportConfiguration
	 */
	@Override
	public ReportConfiguration getNewReportConfiguration() {
		logger.debug("Entering ");
		ReportConfiguration reportConfiguration = getReportConfiguration();
		reportConfiguration.setNewRecord(true);
		logger.debug("Leaving ");
		return reportConfiguration;
	}

	/**
	 * Fetch the Record  ReportConfiguration details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return ReportConfiguration
	 */
	@Override
	public ReportConfiguration getReportConfigurationById(final long id, String type) {
		logger.debug("Entering ");
		ReportConfiguration reportConfiguration = new ReportConfiguration();
		reportConfiguration.setId(id);

		StringBuilder selectSql = new StringBuilder("SELECT ReportID, ReportName, ReportHeading, PromptRequired," );
		selectSql.append(" ReportJasperName, DataSourceName," );
		selectSql.append(" ShowTempLibrary, MenuItemCode,  AlwMultiFormat," );
		if(type.contains("View")){
		}
		selectSql.append(" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId" );
		selectSql.append(" FROM  ReportConfiguration");
		selectSql.append(StringUtils.trimToEmpty(type) );
		selectSql.append("  Where ReportID = :ReportID");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(reportConfiguration);
		RowMapper<ReportConfiguration> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ReportConfiguration.class);

		try{
			reportConfiguration = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			reportConfiguration = null;
		}
		logger.debug("Leaving ");
		return reportConfiguration;
	}

	/**
	 * @param dataSource the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the ReportConfiguration or ReportConfiguration_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete ReportConfiguration by key CcyCode
	 * 
	 * @param ReportConfiguration (reportConfiguration)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(ReportConfiguration reportConfiguration,String type) {
		logger.debug("Entering ");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder(" Delete From ReportConfiguration");
		deleteSql.append(StringUtils.trimToEmpty(type) );
		deleteSql.append(" Where ReportID =:ReportID");
		
		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(reportConfiguration);

		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);

			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		}catch(DataAccessException e){
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving ");
	}

	/**
	 * This method insert new Records into ReportConfiguration or ReportConfiguration_Temp.
	 *
	 * save ReportConfiguration 
	 * 
	 * @param ReportConfiguration (reportConfiguration)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return String
	 * 
	 */
	@Override
	public long save(ReportConfiguration reportConfiguration,String type) {
		logger.debug("Entering ");

		if (reportConfiguration.getId()==Long.MIN_VALUE){
			reportConfiguration.setId(getNextidviewDAO().getNextId("SeqReportConfiguration"));
			logger.debug("get NextID:"+reportConfiguration.getId());
		}
		StringBuilder insertSql = new StringBuilder("Insert Into ReportConfiguration" );
		insertSql.append(StringUtils.trimToEmpty(type) );
		insertSql.append(" (ReportID, ReportName, ReportHeading, PromptRequired, ReportJasperName," );
		insertSql.append(" DataSourceName," );
		insertSql.append(" ShowTempLibrary, MenuItemCode,  AlwMultiFormat," );
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:ReportID, :ReportName, :ReportHeading, :PromptRequired, :ReportJasperName," );
		insertSql.append(" :DataSourceName," );
		insertSql.append(" :ShowTempLibrary, :MenuItemCode,  :AlwMultiFormat," );
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode," );
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(reportConfiguration);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving ");
		return reportConfiguration.getId();
	}

	/**
	 * This method updates the Record ReportConfiguration or ReportConfiguration_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update ReportConfiguration by key CcyCode and Version
	 * 
	 * @param ReportConfiguration (reportConfiguration)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(ReportConfiguration reportConfiguration,String type) {
		int recordCount = 0;
		logger.debug("Entering ");
		
		StringBuilder updateSql = new StringBuilder("Update ReportConfiguration" );
		updateSql.append(StringUtils.trimToEmpty(type) ); 
		updateSql.append(" Set ReportName = :ReportName, ReportHeading = :ReportHeading,");
		updateSql.append(" PromptRequired = :PromptRequired, ReportJasperName = :ReportJasperName," );
		updateSql.append(" DataSourceName =:DataSourceName,");
		updateSql.append(" ShowTempLibrary = :ShowTempLibrary, MenuItemCode = :MenuItemCode, AlwMultiFormat = :AlwMultiFormat," );
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn," );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode," );
		updateSql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId," );
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId" );
		updateSql.append(" Where ReportID = :ReportID");

		if (!type.endsWith("_Temp")){
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(reportConfiguration);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving ");
	}

	/**
	 * Fetch Total Group Codes For Month Reports generation
	 */
	@Override
    public List<ValueLabel> getMonthEndReportGrpCodes() {
		logger.debug("Entering ");

		StringBuilder selectSql = new StringBuilder("SELECT DISTINCT GroupCode Value, GroupDesc Label " );
		selectSql.append(" FROM  ReportsMonthEndConfiguration");

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<ValueLabel> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ValueLabel.class);

		logger.debug("Leaving ");
		return this.namedParameterJdbcTemplate.getJdbcOperations().query(selectSql.toString(), typeRowMapper);
    }
	
	/**
	 * Fetch Total Group Codes For Month Reports generation
	 */
	@Override
    public List<ValueLabel> getReportListByGrpCode(String groupCode) {
		logger.debug("Entering ");
		
		ReportsMonthEndConfiguration configuration = new ReportsMonthEndConfiguration();
		configuration.setGroupCode(groupCode);
		configuration.setActive(true);

		StringBuilder selectSql = new StringBuilder("SELECT ReportName Value, ReportDesc Label " );
		selectSql.append(" FROM  ReportsMonthEndConfiguration ");
		selectSql.append(" WHERE GroupCode=:GroupCode AND Active =:Active ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(configuration);
		RowMapper<ValueLabel> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ValueLabel.class);

		logger.debug("Leaving ");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(),beanParameters, typeRowMapper);
    }
	
}