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
 * FileName    		:  CollateralLocationDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  20-02-2013    														*
 *                                                                  						*
 * Modified Date    :  20-02-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 20-02-2013       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.coremasters.impl;


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

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.app.util.ErrorUtil;

import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.dao.coremasters.CollateralLocationDAO;
import com.pennant.backend.model.coremasters.CollateralLocation;

/**
 * DAO methods implementation for the <b>CollateralLocation model</b> class.<br>
 * 
 */

public class CollateralLocationDAOImpl extends BasisCodeDAO<CollateralLocation> implements CollateralLocationDAO {

	private static Logger logger = Logger.getLogger(CollateralLocationDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	/**
	 * This method set the Work Flow id based on the module name and return the new CollateralLocation 
	 * @return CollateralLocation
	 */

	@Override
	public CollateralLocation getCollateralLocation() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("CollateralLocation");
		CollateralLocation collateralLocation= new CollateralLocation();
		if (workFlowDetails!=null){
			collateralLocation.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return collateralLocation;
	}


	/**
	 * This method get the module from method getCollateralLocation() and set the new record flag as true and return CollateralLocation()   
	 * @return CollateralLocation
	 */


	@Override
	public CollateralLocation getNewCollateralLocation() {
		logger.debug("Entering");
		CollateralLocation collateralLocation = getCollateralLocation();
		collateralLocation.setNewRecord(true);
		logger.debug("Leaving");
		return collateralLocation;
	}

	/**
	 * Fetch the Record  Collateral Locations details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return CollateralLocation
	 */
	@Override
	public CollateralLocation getCollateralLocationById(final String id, String type) {
		logger.debug("Entering");
		CollateralLocation collateralLocation = new CollateralLocation();
		
		collateralLocation.setId(id);
		
		StringBuilder selectSql = new StringBuilder("Select HZCLO, HZCLC");
		selectSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append("");
		}
		selectSql.append(" From HZPF");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where HZCLO =:HZCLO");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(collateralLocation);
		RowMapper<CollateralLocation> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CollateralLocation.class);
		
		try{
			collateralLocation = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			collateralLocation = null;
		}
		logger.debug("Leaving");
		return collateralLocation;
	}
	
	/**
	 * This method initialise the Record.
	 * @param CollateralLocation (collateralLocation)
 	 * @return CollateralLocation
	 */
	@Override
	public void initialize(CollateralLocation collateralLocation) {
		super.initialize(collateralLocation);
	}
	/**
	 * This method refresh the Record.
	 * @param CollateralLocation (collateralLocation)
 	 * @return void
	 */
	@Override
	public void refresh(CollateralLocation collateralLocation) {
		
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the HZPF or HZPF_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Collateral Locations by key HZCLO
	 * 
	 * @param Collateral Locations (collateralLocation)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(CollateralLocation collateralLocation,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete From HZPF");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where HZCLO =:HZCLO");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(collateralLocation);
		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				ErrorDetails errorDetails= getError("41003",collateralLocation.getId() ,collateralLocation.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {};
			}
		}catch(DataAccessException e){
			logger.error(e);
			ErrorDetails errorDetails= getError("41006",collateralLocation.getId() ,collateralLocation.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	/**
	 * This method insert new Records into HZPF or HZPF_Temp.
	 *
	 * save Collateral Locations 
	 * 
	 * @param Collateral Locations (collateralLocation)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@Override
	public String save(CollateralLocation collateralLocation,String type) {
		logger.debug("Entering");
		
		StringBuilder insertSql =new StringBuilder("Insert Into HZPF");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (HZCLO, HZCLC");
		insertSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:HZCLO, :HZCLC");
		insertSql.append(", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(collateralLocation);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return collateralLocation.getId();
	}
	
	/**
	 * This method updates the Record HZPF or HZPF_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Collateral Locations by key HZCLO and Version
	 * 
	 * @param Collateral Locations (collateralLocation)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@SuppressWarnings("serial")
	@Override
	public void update(CollateralLocation collateralLocation,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update HZPF");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set HZCLO = :HZCLO, HZCLC = :HZCLC");
		updateSql.append(", Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where HZCLO =:HZCLO");
		
		if (!type.endsWith("_TEMP")){
			updateSql.append("  AND Version= :Version-1");
		}
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(collateralLocation);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :"+recordCount);
			ErrorDetails errorDetails= getError("41004",collateralLocation.getId() ,collateralLocation.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	private ErrorDetails  getError(String errorId, String HZCLO, String userLanguage){
		String[][] parms= new String[2][1];
		parms[1][0] = HZCLO;
		parms[0][0] = PennantJavaUtil.getLabel("label_HZCLO")+ ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId, parms[0],parms[1]), userLanguage);
	}

	
}