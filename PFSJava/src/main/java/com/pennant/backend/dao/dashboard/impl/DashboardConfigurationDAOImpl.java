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
 * FileName    		:  DashboardConfigurationDAOImpl.java                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-06-2011    														*
 *                                                                  						*
 * Modified Date    :  14-06-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-06-2011       Pennant	                 0.1                                            * 
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
package com.pennant.backend.dao.dashboard.impl;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.dashboard.DashboardConfigurationDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.dashboard.DashboardConfiguration;
import com.pennant.backend.model.dashboarddetail.DashboardPosition;
import com.pennant.fusioncharts.ChartSetElement;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * DAO methods implementation for the <b>DashboardConfiguration model</b> class.<br>
 * 
 */
public class DashboardConfigurationDAOImpl extends BasisCodeDAO<DashboardConfiguration> implements
		DashboardConfigurationDAO {
	private static Logger logger = Logger.getLogger(DashboardConfigurationDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public DashboardConfigurationDAOImpl() {
		super();
	}

	/**
	 * This method set the Work Flow id based on the module name and return the
	 * new DashboardConfiguration
	 * 
	 * @return DashboardConfiguration
	 */

	@Override
	public DashboardConfiguration getNewDashboardDetail() {
		logger.debug("Entering ");

		DashboardConfiguration dashboardConfiguration = new DashboardConfiguration();
		dashboardConfiguration.setNewRecord(true);
		logger.debug("Leaving");
		return dashboardConfiguration;
	}

	/**
	 * Fetch the Record Masters details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return DashboardConfiguration
	 */
	@Override
	public DashboardConfiguration getDashboardDetailByID(final String id, String type) {
		logger.debug("Entering");
		DashboardConfiguration dashboardConfiguration = getNewDashboardDetail();
		dashboardConfiguration.setId(id);
		StringBuilder selectSql = new StringBuilder();

		selectSql.append("Select DashboardCode, DashboardDesc, DashboardType,");
		selectSql.append("	dimension, caption, subCaption,");
		selectSql.append("  Query,remarks,DrillDownChart,multiSeries,SeriesType,SeriesValues,FieldQuery,DataXML, AdtDataSource,");
		selectSql.append(" Version,LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId,");
		selectSql.append(" NextTaskId, RecordType, WorkflowId From DashboardConfiguration");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where DashboardCode =:DashboardCode");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(
				dashboardConfiguration);
		RowMapper<DashboardConfiguration> typeRowMapper = ParameterizedBeanPropertyRowMapper
		.newInstance(DashboardConfiguration.class);

		try {
			dashboardConfiguration = this.namedParameterJdbcTemplate.queryForObject(
					selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			dashboardConfiguration = null;
		}
		logger.debug("Leaving");
		return dashboardConfiguration;
	}

	/**
	 * This method Deletes the Record from the DashboardConfigurations or
	 * DashboardConfiguration_Temp. if Record not deleted then throws
	 * DataAccessException with error 41004. delete Masters by key DashboardCode
	 * 
	 * @param Masters
	 *            (dashboardConfiguration)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(DashboardConfiguration dashboardConfiguration, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder deleteSql = new StringBuilder();

		deleteSql.append("Delete From DashboardConfiguration");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where DashboardCode =:DashboardCode");

		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(
				dashboardConfiguration);
		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(),
					beanParameters);
			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into DashboardConfigurations or
	 * DashboardConfigurations_Temp.
	 * 
	 * save Masters
	 * 
	 * @param Masters
	 *            (dashboardDetail)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public String save(DashboardConfiguration dashboardConfiguration, String type) {
		logger.debug("Entering");
		StringBuilder insertSql = new StringBuilder();

		insertSql.append("Insert Into DashboardConfiguration");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (DashboardCode, DashboardDesc, DashboardType,");
		insertSql.append("  Dimension,Caption, SubCaption, Query, Remarks, DrillDownChart, MultiSeries, SeriesType, SeriesValues, FieldQuery, DataXML, AdtDataSource,");
		insertSql.append(" Version,LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:DashboardCode, :DashboardDesc, :DashboardType,");
		insertSql.append(" :Dimension,:Caption, :SubCaption, :Query, :Remarks, :DrillDownChart, :MultiSeries, :SeriesType, :SeriesValues, :FieldQuery, :DataXML, :AdtDataSource,");
		insertSql.append(" :Version,:LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode,");
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(
				dashboardConfiguration);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return dashboardConfiguration.getId();
	}

	/**
	 * This method updates the Record DashboardConfigurations or DashboardConfigurations_Temp.
	 * if Record not updated then throws DataAccessException with error 41003.
	 * update Masters by key DashboardCode and Version
	 * 
	 * @param Masters
	 *            (dashboardDetail)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public void update(DashboardConfiguration dashboardConfiguration, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder();

		updateSql.append("Update DashboardConfiguration");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set DashboardDesc = :DashboardDesc,DashboardType = :DashboardType,");
		updateSql.append(" Dimension=:Dimension,Caption=:Caption, SubCaption=:SubCaption, Query=:Query, Remarks=:Remarks, DrillDownChart=:DrillDownChart, MultiSeries=:MultiSeries,");
		updateSql.append(" SeriesType=:SeriesType, SeriesValues=:SeriesValues, FieldQuery=:FieldQuery, DataXML=:DataXML, AdtDataSource=:AdtDataSource,  ");
		updateSql.append(" Version=:Version, LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus,");
		updateSql.append(" RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId,");
		updateSql.append(" NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where DashboardCode =:DashboardCode ");
		
		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(
				dashboardConfiguration);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(),
				beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
			
		}
		logger.debug("Leaving");
	}

	/**
	 * @param dataSource
	 *            the dataSource to set
	 */

	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(
				dataSource);
	}

	/**
	 * Save the positions of the DashBoard for the user
	 * 
	 * @param dashboardPosition (DashboardPosition)
	 */
	@Override
	public void SavePositions(DashboardPosition dashboardPosition) {
		logger.debug("Entering" + dashboardPosition.toString());
		StringBuilder insertSql = new StringBuilder();

		insertSql.append("Insert Into DashboardPositions");
		insertSql.append(" (UsrId, DashboardRef, DashboardCol, DashboardRow, DashboardColIndex, ");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus,");
		insertSql.append(" RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:UsrId, :DashboardRef, :DashboardCol, :DashboardRow, :DashboardColIndex, ");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus,");
		insertSql.append(" :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(
				dashboardPosition);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	
	/**
	 * Get the DashBoard Details from the database with the following conditions
	 * User should have the rights and get the positions from the Dashboard positions  
	 * 
	 * @param userId (long)
	 */
	@Override
	public List<DashboardPosition> getDashboardPositionsByUser(long userId) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("select UsrId, DashboardRef,");
		sql.append(" DashboardCol, DashboardRow, DashboardColIndex from DashboardPositions");
		sql.append(" where UsrId = :userId ORDER BY DashboardRef");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("userId", userId);

		RowMapper<DashboardPosition> rowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(DashboardPosition.class);

		List<DashboardPosition> positions = new ArrayList<>();

		try {
			positions = namedParameterJdbcTemplate.query(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return positions;
	}

	/**
	 * Delete the Dash board positions for the user
	 * 
	 * @param userId (long)
	 * 
	 */
	@Override
	public void delete(long userId) {
		logger.debug("Entering");
		DashboardPosition dashboardPosition = new DashboardPosition();
		dashboardPosition.setUsrId(userId);
		StringBuilder deleteSql = new StringBuilder();

		deleteSql.append("Delete From DashboardPositions");
		deleteSql.append(" Where UsrId = :UsrId  ");

		logger.debug("deleteSql: " + deleteSql);
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(
				dashboardPosition);
		
		 this.namedParameterJdbcTemplate.update(deleteSql.toString(),
					beanParameters);

		logger.debug("Leaving");
	}

	/**
	 * Get the DashBoard Details from the database with the following conditions
	 * User should have the rights and get the positions from the Dashboard positions  
	 * 
	 * @param userId (long)
	 */
	@Override
	public List<DashboardConfiguration> getDashboardConfigurations(long userId) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("select DashboardCode, DashboardDesc, DashboardType,");
		sql.append(" dimension, caption, subCaption, Query, remarks, DrillDownChart, multiSeries,");
		sql.append(" SeriesType, SeriesValues, FieldQuery, DataXML, AdtDataSource");
		sql.append(" from DashboardConfiguration ORDER BY DashboardDesc Asc");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		RowMapper<DashboardConfiguration> rowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(DashboardConfiguration.class);

		List<DashboardConfiguration> dashboards = new ArrayList<>();

		try {
			dashboards = this.namedParameterJdbcTemplate.query(sql.toString(), rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return dashboards;
	}

	/**
	 * Get the chart Details From the Database
	 * 
	 * @param DashboardConfiguration (DashboardDetail)
	 */
	@Override 
	public List<ChartSetElement> getLabelAndValues(DashboardConfiguration dashboardDetail)
	throws DataAccessException{
		logger.debug("Entering");
		String selectSql = dashboardDetail.getQuery();
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(dashboardDetail);
		ParameterizedBeanPropertyRowMapper<ChartSetElement> typeRowMapper = ParameterizedBeanPropertyRowMapper
		.newInstance(ChartSetElement.class);
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql,
				beanParameters, typeRowMapper);
	}
}