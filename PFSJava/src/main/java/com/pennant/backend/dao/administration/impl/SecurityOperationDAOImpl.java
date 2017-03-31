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
 * FileName    		:  SecurityOperationDAOImpl.java                                           * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  10-03-2014    														*
 *                                                                  						*
 * Modified Date    :  10-03-2014    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 10-03-2014       Pennant	                 0.1                                            * 
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
package com.pennant.backend.dao.administration.impl;

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

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.administration.SecurityOperationDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.administration.SecurityOperation;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>SecurityOperation model</b> class.<br>
 * 
 */
public class SecurityOperationDAOImpl extends BasisNextidDaoImpl<SecurityOperation> implements SecurityOperationDAO{
	private static Logger logger = Logger.getLogger(SecurityOperationDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	
	
	public SecurityOperationDAOImpl() {
		super();
	}
	/**
	 * This method set the Work Flow id based on the module name and return the new SecurityOperation 
	 * @return SecurityOperation
	 */
	@Override
	public SecurityOperation getSecurityOperation() {
		logger.debug("Entering ");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("SecurityOperation");
		SecurityOperation securityOperation= new SecurityOperation();
		if (workFlowDetails!=null){
			securityOperation.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving ");
		return securityOperation;
	}

	/**
	 * Fetch the Record  SecurityOperation details by key field
	 * 
	 * @param id (int)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return SecurityOperation
	 */
	@Override
	public SecurityOperation getSecurityOperationById(final long id, String type) {
		logger.debug("Entering");
		SecurityOperation securityOperation = getSecurityOperation();
		securityOperation.setId(id);

		StringBuilder   selectSql = new StringBuilder("Select OprID, OprCode, OprDesc");
		selectSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From SecOperations");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where OprID =:OprID");
		logger.debug("selectSql: " + selectSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(securityOperation);
		RowMapper<SecurityOperation> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(SecurityOperation.class);

		try{
			securityOperation = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			securityOperation = null;
		}
		logger.debug("Leaving");
		return securityOperation;
	}

	/**
	 * Fetch the Record  SecurityOperation details by key field
	 * 
	 * @param  String(grpCode),
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return SecurityOperation
	 */
	@Override
	public SecurityOperation getSecurityOperationByCode(String  oprCode, String type) {

		logger.debug("Entering ");

		MapSqlParameterSource source = null;

		StringBuilder selectSql = 	new StringBuilder("Select OprID, OprCode, OprDesc");
		selectSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From SecOperations");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where OprCode =:OprCode");

		logger.debug("selectSql: " + selectSql.toString());
		
		source = new MapSqlParameterSource();
		source.addValue("OprCode", oprCode);
		
		RowMapper<SecurityOperation> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(SecurityOperation.class);

		try{
			return this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			return  null;
		} finally {
			logger.debug("Leaving ");
			source = null;
		}
	}
	
	/**
	 * @param dataSource the dataSource to set
	 */

	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the SecOperations or SecOperations_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete SecurityOperation by key GrpID
	 * 
	 * @param SecurityOperation (securityOperation)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(SecurityOperation securityOperation,String type) {

		logger.debug("Entering ");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete From SecOperations");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where OprID =:OprID");

		logger.debug("deleteSql: " + deleteSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(securityOperation);
		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);

			if (recordCount <= 0) {
				ErrorDetails errorDetail= getError("41003", securityOperation.getOprCode(), securityOperation.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetail.getError()) {};
			}
		}catch(DataAccessException e){
			logger.warn("Exception: ", e);
			ErrorDetails errorDetail= getError("41006", securityOperation.getOprCode(), securityOperation.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetail.getError()) {};

		}

		logger.debug("Leaving ");

	}

	/**
	 * This method insert new Records into SecOperations or SecOperations_Temp.
	 * it fetches the available Sequence form SeqSecoperations by using getNextidviewDAO().getNextId() method.  
	 *
	 * save SecurityOperation 
	 * 
	 * @param SecurityOperation (securityOperation)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public long save(SecurityOperation securityOperation,String type) {
		logger.debug("Entering ");
		if (securityOperation.getId()==Long.MIN_VALUE){
			securityOperation.setId(getNextidviewDAO().getNextId("SeqSecOperations"));
			logger.debug("get NextID:"+securityOperation.getId());
		}

		StringBuilder insertSql =new StringBuilder("Insert Into SecOperations");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (OprID, OprCode, OprDesc");
		insertSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:OprID, :OprCode, :OprDesc");
		insertSql.append(", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(securityOperation);
		
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving ");
		return securityOperation.getId();
	}

	/**
	 * This method updates the Record SecOperations or SecOperations_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update SecurityOperation by key GrpID and Version
	 * 
	 * @param SecurityOperation (securityOperation)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	@Override
	public void update(SecurityOperation securityOperation,String type) {
		int recordCount = 0;
		logger.debug("Entering ");
		StringBuilder updateSql =new StringBuilder("Update SecOperations");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set OprCode = :OprCode, OprDesc = :OprDesc");
		updateSql.append(", Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus,");
		updateSql.append(" RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, ");
		updateSql.append("RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where OprID =:OprID");

		if (StringUtils.isBlank(type)){
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(securityOperation);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :"+recordCount);
			ErrorDetails errorDetail= getError("41004", securityOperation.getOprCode(), securityOperation.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetail.getError()) {};
		}
		logger.debug("Leaving ");
	}
	
	
	private ErrorDetails  getError(String errorId, String operationCode, String userLanguage){
		String[][] parms= new String[2][1];
		parms[1][0] = operationCode;
		parms[0][0] = PennantJavaUtil.getLabel("label_OprCode")+ ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId, parms[0],parms[1]), userLanguage);
	}

	@Override
	public List<SecurityOperation> getApprovedSecurityOperation() {
		logger.debug("Entering");
		String type = "_AView";
		SecurityOperation secOperations = getSecurityOperation();

		StringBuilder selectSql = 	new StringBuilder("Select OprID, OprCode, OprDesc");
		selectSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if(StringUtils.trimToEmpty(type).contains("View")){
			//selectSql.append(" ,lovDescRoleAppName ");	
		}
		selectSql.append(" From SecOperations_AView");

		logger.debug("selectSql:" + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(secOperations);
		RowMapper<SecurityOperation> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(SecurityOperation.class);
		logger.debug("Leaving");

		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);	
	}

}