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
 * FileName    		:  AuthorizationDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  20-08-2013    														*
 *                                                                  						*
 * Modified Date    :  20-08-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 20-08-2013       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.amtmasters.impl;


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

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.amtmasters.AuthorizationDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.amtmasters.Authorization;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>Authorization model</b> class.<br>
 * 
 */

public class AuthorizationDAOImpl extends BasisNextidDaoImpl<Authorization> implements AuthorizationDAO {

	private static Logger logger = Logger.getLogger(AuthorizationDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	/**
	 * This method set the Work Flow id based on the module name and return the new Authorization 
	 * @return Authorization
	 */

	@Override
	public Authorization getAuthorization() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("Authorization");
		Authorization authorization= new Authorization();
		if (workFlowDetails!=null){
			authorization.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return authorization;
	}


	/**
	 * This method get the module from method getAuthorization() and set the new record flag as true and return Authorization()   
	 * @return Authorization
	 */


	@Override
	public Authorization getNewAuthorization() {
		logger.debug("Entering");
		Authorization authorization = getAuthorization();
		authorization.setNewRecord(true);
		logger.debug("Leaving");
		return authorization;
	}

	/**
	 * Fetch the Record  Authorization Details details by key field
	 * 
	 * @param id (int)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return Authorization
	 */
	@Override
	public Authorization getAuthorizationById(final long id, String type) {
		logger.debug("Entering");
		Authorization authorization = new Authorization();
		authorization.setId(id);
		StringBuilder selectSql = new StringBuilder("Select AuthorizedId, AuthUserId, AuthType, AuthName, AuthDept, AuthDesig, AuthSignature");
		selectSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(",AuthUserIdName,AuthDeptName,AuthDesigName");
		}
		selectSql.append(" From AMTAuthorization");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where AuthorizedId =:AuthorizedId");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(authorization);
		RowMapper<Authorization> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Authorization.class);
		
		try{
			authorization = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			authorization = null;
		}
		logger.debug("Leaving");
		return authorization;
	}
	
	@Override
	public Authorization getAuthorization(long authUserId,String authType,String type) {
		logger.debug("Entering");
		Authorization authorization = new Authorization();
		authorization.setAuthUserId(authUserId);
		authorization.setAuthType(authType);
		StringBuilder selectSql = new StringBuilder();

		selectSql.append(" Select AuthorizedId, AuthUserId, AuthType, AuthName, AuthDept, AuthDesig, AuthSignature" );
		/*if(type.contains("View")){
			selectSql.append("");
		}*/
		selectSql.append(" ,Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId" );
		selectSql.append(" FROM  AMTAuthorization");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where  AuthUserId=:AuthUserId AND  AuthType=:AuthType") ;

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(authorization);
		RowMapper<Authorization> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Authorization.class);

		try {
			authorization = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(e);
			authorization = null;
		}
		logger.debug("Leaving");
		return authorization;
	}
	
	
	/**
	 * This method initialise the Record.
	 * @param Authorization (authorization)
 	 * @return Authorization
	 */
	@Override
	public void initialize(Authorization authorization) {
		super.initialize(authorization);
	}
	/**
	 * This method refresh the Record.
	 * @param Authorization (authorization)
 	 * @return void
	 */
	@Override
	public void refresh(Authorization authorization) {
		
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the AMTAuthorization or AMTAuthorization_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Authorization Details by key AuthorizedId
	 * 
	 * @param Authorization Details (authorization)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(Authorization authorization,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete From AMTAuthorization");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where AuthorizedId =:AuthorizedId");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(authorization);
		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {                                      
				ErrorDetails errorDetails= getError("41003",String.valueOf(authorization.getAuthorizedId()) ,authorization.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {};
			}
		}catch(DataAccessException e){
			logger.error(e);
			ErrorDetails errorDetails= getError("41006",String.valueOf(authorization.getAuthorizedId()) ,authorization.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	/**
	 * This method insert new Records into AMTAuthorization or AMTAuthorization_Temp.
	 * it fetches the available Sequence form SeqAMTAuthorization by using getNextidviewDAO().getNextId() method.  
	 *
	 * save Authorization Details 
	 * 
	 * @param Authorization Details (authorization)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@Override
	public long save(Authorization authorization,String type) {
		logger.debug("Entering");
		if (authorization.getId()==Long.MIN_VALUE){
			authorization.setId(getNextidviewDAO().getNextId("SeqAMTAuthorization"));
			logger.debug("get NextID:"+authorization.getId());
		}
		
		StringBuilder insertSql =new StringBuilder("Insert Into AMTAuthorization");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (AuthorizedId, AuthUserId, AuthType, AuthName, AuthDept, AuthDesig, AuthSignature");
		insertSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:AuthorizedId, :AuthUserId, :AuthType, :AuthName, :AuthDept, :AuthDesig, :AuthSignature");
		insertSql.append(", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(authorization);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return authorization.getId();
	}
	
	/**
	 * This method updates the Record AMTAuthorization or AMTAuthorization_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Authorization Details by key AuthorizedId and Version
	 * 
	 * @param Authorization Details (authorization)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@SuppressWarnings("serial")
	@Override
	public void update(Authorization authorization,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update AMTAuthorization");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set AuthorizedId = :AuthorizedId, AuthUserId = :AuthUserId, AuthType = :AuthType, AuthName = :AuthName, AuthDept = :AuthDept, AuthDesig = :AuthDesig, AuthSignature = :AuthSignature");
		updateSql.append(", Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where AuthorizedId =:AuthorizedId");
		
		if (!type.endsWith("_TEMP")){
			updateSql.append("  AND Version= :Version-1");
		}
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(authorization);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :"+recordCount);
			ErrorDetails errorDetails= getError("41004",String.valueOf(authorization.getAuthorizedId()) ,authorization.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	private ErrorDetails  getError(String errorId, String authorizedId, String userLanguage){
		String[][] parms= new String[2][1];
		parms[1][0] = authorizedId;
		parms[0][0] = PennantJavaUtil.getLabel("label_AuthorizedId")+ ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId, parms[0],parms[1]), userLanguage);
	}

	
}