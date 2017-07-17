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
 * FileName    		:  LiabilityRequestDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  31-12-2015    														*
 *                                                                  						*
 * Modified Date    :  31-12-2015    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 31-12-2015       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.finance.liability.impl;


import java.util.HashMap;
import java.util.Map;

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

import com.pennant.backend.dao.finance.liability.LiabilityRequestDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.finance.liability.LiabilityRequest;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>LiabilityRequest model</b> class.<br>
 * 
 */

public class LiabilityRequestDAOImpl extends BasisCodeDAO<LiabilityRequest> implements LiabilityRequestDAO {

	public LiabilityRequestDAOImpl() {
	    super();
    }

	private static Logger logger = Logger.getLogger(LiabilityRequestDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	/**
	 * This method set the Work Flow id based on the module name and return the new LiabilityRequest 
	 * @return LiabilityRequest
	 */

	@Override
	public LiabilityRequest getLiabilityRequest() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("LiabilityRequest");
		LiabilityRequest liabilityRequest= new LiabilityRequest();
		if (workFlowDetails!=null){
			liabilityRequest.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return liabilityRequest;
	}


	/**
	 * This method get the module from method getLiabilityRequest() and set the new record flag as true and return LiabilityRequest()   
	 * @return LiabilityRequest
	 */


	@Override
	public LiabilityRequest getNewLiabilityRequest() {
		logger.debug("Entering");
		LiabilityRequest liabilityRequest = getLiabilityRequest();
		liabilityRequest.setNewRecord(true);
		logger.debug("Leaving");
		return liabilityRequest;
	}

	/**
	 * Fetch the Record  LiabilityRequest details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return LiabilityRequest
	 */
	@Override
	public LiabilityRequest getLiabilityRequestById(final String id, String finEvent, String type) {
		logger.debug("Entering");
		LiabilityRequest liabilityRequest = getLiabilityRequest();
		
		liabilityRequest.setId(id);
		liabilityRequest.setFinEvent(finEvent); 
		
		StringBuilder selectSql = new StringBuilder("Select FinReference, InitiatedBy, FinEvent,InsPaidStatus,InsClaimAmount,InsClaimReason, ");
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append("  FinType, CustCIF, FinBranch, FinStartDate, NumberOfTerms, MaturityDate, FinCcy, FinAmount, CustShrtName , BranchDesc, ");
		}
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From FinLiabilityReq");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference AND FinEvent=:FinEvent ");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(liabilityRequest);
		RowMapper<LiabilityRequest> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(LiabilityRequest.class);
		
		try{
			liabilityRequest = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			liabilityRequest = null;
		}
		logger.debug("Leaving");
		return liabilityRequest;
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the FinLiabilityReq or FinLiabilityReq_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete LiabilityRequest by key FinReference
	 * 
	 * @param LiabilityRequest (liabilityRequest)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(LiabilityRequest liabilityRequest,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete From FinLiabilityReq");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FinReference =:FinReference");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(liabilityRequest);
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
	 * This method insert new Records into FinLiabilityReq or FinLiabilityReq_Temp.
	 *
	 * save LiabilityRequest 
	 * 
	 * @param LiabilityRequest (liabilityRequest)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@Override
	public String save(LiabilityRequest liabilityRequest, String type) {
		logger.debug("Entering");
		
		StringBuilder insertSql =new StringBuilder("Insert Into FinLiabilityReq");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (FinReference, FinEvent,InsPaidStatus,InsClaimAmount,InsClaimReason, InitiatedBy, Version , LastMntBy, LastMntOn, RecordStatus");
		insertSql.append(", RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:FinReference,  :FinEvent,:InsPaidStatus,:InsClaimAmount,:InsClaimReason, :InitiatedBy, :Version , :LastMntBy, :LastMntOn, :RecordStatus");
		insertSql.append(", :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(liabilityRequest);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return liabilityRequest.getId();
	}
	
	/**
	 * This method updates the Record FinLiabilityReq or FinLiabilityReq_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update LiabilityRequest by key FinReference and Version
	 * 
	 * @param LiabilityRequest (liabilityRequest)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@Override
	public void update(LiabilityRequest liabilityRequest,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update FinLiabilityReq");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set InsPaidStatus=:InsPaidStatus,InsClaimAmount=:InsClaimAmount,InsClaimReason=:InsClaimReason,InitiatedBy = :InitiatedBy");
		updateSql.append(", Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where FinReference =:FinReference AND FinEvent=:FinEvent " );
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(liabilityRequest);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Fetch the Record  LiabilityRequest details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return LiabilityRequest
	 */
	@Override
	public String getProceedingWorkflow(String finType, String finEvent) {
		logger.debug("Entering");
		
		String proceedWorkflowType = null;
		Map<String,String> map=new HashMap<String, String>();
		map.put("FinType", finType);
		map.put("FinEvent", finEvent);
		
		StringBuilder selectSql = new StringBuilder("Select NextFinEvent From ProceedWorkflowType");
		selectSql.append(" Where FinType =:FinType AND FinEvent=:FinEvent ");
		
		logger.debug("selectSql: " + selectSql.toString());
		try{
			proceedWorkflowType = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), map, String.class);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			proceedWorkflowType = null;
		}
		logger.debug("Leaving");
		return proceedWorkflowType;
	}
}