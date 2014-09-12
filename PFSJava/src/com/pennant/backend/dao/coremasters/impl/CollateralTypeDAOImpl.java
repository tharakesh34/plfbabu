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
 * FileName    		:  CollateralTypeDAOImpl.java                                                   * 	  
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
import com.pennant.backend.dao.coremasters.CollateralTypeDAO;
import com.pennant.backend.model.coremasters.CollateralType;

/**
 * DAO methods implementation for the <b>CollateralType model</b> class.<br>
 * 
 */

public class CollateralTypeDAOImpl extends BasisCodeDAO<CollateralType> implements CollateralTypeDAO {

	private static Logger logger = Logger.getLogger(CollateralTypeDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	/**
	 * This method set the Work Flow id based on the module name and return the new CollateralType 
	 * @return CollateralType
	 */

	@Override
	public CollateralType getCollateralType() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("CollateralType");
		CollateralType collateralType= new CollateralType();
		if (workFlowDetails!=null){
			collateralType.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return collateralType;
	}


	/**
	 * This method get the module from method getCollateralType() and set the new record flag as true and return CollateralType()   
	 * @return CollateralType
	 */


	@Override
	public CollateralType getNewCollateralType() {
		logger.debug("Entering");
		CollateralType collateralType = getCollateralType();
		collateralType.setNewRecord(true);
		logger.debug("Leaving");
		return collateralType;
	}

	/**
	 * Fetch the Record  Collateral Types details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return CollateralType
	 */
	@Override
	public CollateralType getCollateralTypeById(final String id, String type) {
		logger.debug("Entering");
		CollateralType collateralType = new CollateralType();
		
		collateralType.setId(id);
		
		StringBuilder selectSql = new StringBuilder("Select HWCLP, HWCPD, HWBVM, HWINS");
		selectSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if(StringUtils.trimToEmpty(type).contains("View")){
			//selectSql.append(",lovDescHWINSName");
		}
		selectSql.append(" From HWPF");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where HWCLP =:HWCLP");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(collateralType);
		RowMapper<CollateralType> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CollateralType.class);
		
		try{
			collateralType = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			collateralType = null;
		}
		logger.debug("Leaving");
		return collateralType;
	}
	
	/**
	 * This method initialise the Record.
	 * @param CollateralType (collateralType)
 	 * @return CollateralType
	 */
	@Override
	public void initialize(CollateralType collateralType) {
		super.initialize(collateralType);
	}
	/**
	 * This method refresh the Record.
	 * @param CollateralType (collateralType)
 	 * @return void
	 */
	@Override
	public void refresh(CollateralType collateralType) {
		
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the HWPF or HWPF_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Collateral Types by key HWCLP
	 * 
	 * @param Collateral Types (collateralType)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(CollateralType collateralType,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete From HWPF");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where HWCLP =:HWCLP");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(collateralType);
		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				ErrorDetails errorDetails= getError("41003",collateralType.getId() ,collateralType.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {};
			}
		}catch(DataAccessException e){
			logger.error(e);
			ErrorDetails errorDetails= getError("41006",collateralType.getId() ,collateralType.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	/**
	 * This method insert new Records into HWPF or HWPF_Temp.
	 *
	 * save Collateral Types 
	 * 
	 * @param Collateral Types (collateralType)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@Override
	public String save(CollateralType collateralType,String type) {
		logger.debug("Entering");
		
		StringBuilder insertSql =new StringBuilder("Insert Into HWPF");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (HWCLP, HWCPD, HWBVM, HWINS");
		insertSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:HWCLP, :HWCPD, :HWBVM, :HWINS");
		insertSql.append(", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(collateralType);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return collateralType.getId();
	}
	
	/**
	 * This method updates the Record HWPF or HWPF_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Collateral Types by key HWCLP and Version
	 * 
	 * @param Collateral Types (collateralType)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@SuppressWarnings("serial")
	@Override
	public void update(CollateralType collateralType,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update HWPF");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set HWCLP = :HWCLP, HWCPD = :HWCPD, HWBVM = :HWBVM, HWINS = :HWINS");
		updateSql.append(", Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where HWCLP =:HWCLP");
		
		if (!type.endsWith("_TEMP")){
			updateSql.append("  AND Version= :Version-1");
		}
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(collateralType);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :"+recordCount);
			ErrorDetails errorDetails= getError("41004",collateralType.getId() ,collateralType.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	private ErrorDetails  getError(String errorId, String hWCLP, String userLanguage){
		String[][] parms= new String[2][1];
		parms[1][0] = hWCLP;
		parms[0][0] = PennantJavaUtil.getLabel("label_HWCLP")+ ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId, parms[0],parms[1]), userLanguage);
	}

	
}