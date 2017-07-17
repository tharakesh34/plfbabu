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
 * FileName    		:  OverdueChargeDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  10-05-2012    														*
 *                                                                  						*
 * Modified Date    :  10-05-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 10-05-2012       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.rulefactory.impl;


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
import com.pennant.backend.dao.rulefactory.OverdueChargeDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.rulefactory.OverdueCharge;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pff.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>OverdueCharge model</b> class.<br>
 * 
 */

public class OverdueChargeDAOImpl extends BasisCodeDAO<OverdueCharge> implements OverdueChargeDAO {

	private static Logger logger = Logger.getLogger(OverdueChargeDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	public OverdueChargeDAOImpl() {
		super();
	}
	
	/**
	 * This method set the Work Flow id based on the module name and return the
	 * new OverdueCharge
	 * 
	 * @return OverdueCharge
	 */
	@Override
	public OverdueCharge getOverdueCharge() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("OverdueCharge");
		OverdueCharge overdueCharge= new OverdueCharge();
		if (workFlowDetails!=null){
			overdueCharge.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return overdueCharge;
	}

	/**
	 * This method get the module from method getOverdueCharge() and set the new
	 * record flag as true and return OverdueCharge()
	 * 
	 * @return OverdueCharge
	 */
	@Override
	public OverdueCharge getNewOverdueCharge() {
		logger.debug("Entering");
		OverdueCharge overdueCharge = getOverdueCharge();
		overdueCharge.setNewRecord(true);
		logger.debug("Leaving");
		return overdueCharge;
	}

	/**
	 * Fetch the Record  Overdue Charge Details details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return OverdueCharge
	 */
	@Override
	public OverdueCharge getOverdueChargeById(final String id, String type) {
		logger.debug("Entering");
		OverdueCharge overdueCharge = new OverdueCharge();
		
		overdueCharge.setId(id);
		
		StringBuilder selectSql = new StringBuilder("Select ODCRuleCode, ODCPLAccount, ODCCharityAccount, ");
		selectSql.append(" ODCPLSubHead,ODCCharitySubHead, ODCPLShare,ODCSweepCharges,ODCRuleDescription");
		selectSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId,");
		selectSql.append(" RecordType, WorkflowId");
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(",lovDescODCPLAccountName,lovDescODCCharityAccountName");
			selectSql.append(",lovDescODCPLSubHeadName,lovDescODCCharitySubHeadName");
		}
		selectSql.append(" From FinODCHeader");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where ODCRuleCode =:ODCRuleCode");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(overdueCharge);
		RowMapper<OverdueCharge> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(OverdueCharge.class);
		
		try{
			overdueCharge = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			overdueCharge = null;
		}
		logger.debug("Leaving");
		return overdueCharge;
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the FinODCHeader or FinODCHeader_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Overdue Charge Details by key ODCRuleCode
	 * 
	 * @param Overdue Charge Details (overdueCharge)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(OverdueCharge overdueCharge,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete From FinODCHeader");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where ODCRuleCode =:ODCRuleCode");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(overdueCharge);
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
	 * This method insert new Records into FinODCHeader or FinODCHeader_Temp.
	 *
	 * save Overdue Charge Details 
	 * 
	 * @param Overdue Charge Details (overdueCharge)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@Override
	public String save(OverdueCharge overdueCharge,String type) {
		logger.debug("Entering");
		
		StringBuilder insertSql =new StringBuilder("Insert Into FinODCHeader");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (ODCRuleCode, ODCPLAccount, ODCCharityAccount, ODCPLSubHead, " );
		insertSql.append(" ODCCharitySubHead,ODCPLShare,ODCSweepCharges,ODCRuleDescription");
		insertSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId,");
		insertSql.append(" NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:ODCRuleCode, :ODCPLAccount, :ODCCharityAccount, :ODCPLSubHead, ");
		insertSql.append(" :ODCCharitySubHead, :ODCPLShare,:ODCSweepCharges, :ODCRuleDescription");
		insertSql.append(", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId,");
		insertSql.append(" :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(overdueCharge);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return overdueCharge.getId();
	}
	
	/**
	 * This method updates the Record FinODCHeader or FinODCHeader_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Overdue Charge Details by key ODCRuleCode and Version
	 * 
	 * @param Overdue Charge Details (overdueCharge)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@Override
	public void update(OverdueCharge overdueCharge,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update FinODCHeader");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set ODCPLAccount = :ODCPLAccount, ODCCharityAccount = :ODCCharityAccount,");
		updateSql.append(" ODCPLSubHead = :ODCPLSubHead, ODCCharitySubHead = :ODCCharitySubHead, ODCPLShare = :ODCPLShare,");
		updateSql.append(" ODCSweepCharges = :ODCSweepCharges, ODCRuleDescription = :ODCRuleDescription, Version = :Version , ");
		updateSql.append(" RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId," );
		updateSql.append(" LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus,");
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where ODCRuleCode =:ODCRuleCode");
		
		if (!type.endsWith("_Temp")){
			updateSql.append("  AND Version= :Version-1");
		}
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(overdueCharge);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}
	
}