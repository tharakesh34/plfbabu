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
 * FileName    		:  WIFFinanceDisbursementDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-11-2011    														*
 *                                                                  						*
 * Modified Date    :  12-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-11-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.finance.impl;

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

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.finance.WIFFinanceDisbursementDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>WIFFinanceDisbursement model</b> class.<br>
 * 
 */

public class WIFFinanceDisbursementDAOImpl extends BasisCodeDAO<FinanceDisbursement> implements WIFFinanceDisbursementDAO {

	private static Logger logger = Logger.getLogger(WIFFinanceDisbursementDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	/**
	 * This method set the Work Flow id based on the module name and return the new WIFFinanceDisbursement 
	 * @return WIFFinanceDisbursement
	 */

	@Override
	public FinanceDisbursement getWIFFinanceDisbursement() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("WIFFinanceDisbursement");
		FinanceDisbursement wIFFinanceDisbursement= new FinanceDisbursement();
		if (workFlowDetails!=null){
			wIFFinanceDisbursement.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return wIFFinanceDisbursement;
	}


	/**
	 * This method get the module from method getWIFFinanceDisbursement() and set the new record flag as true and return WIFFinanceDisbursement()   
	 * @return WIFFinanceDisbursement
	 */


	@Override
	public FinanceDisbursement getNewWIFFinanceDisbursement() {
		logger.debug("Entering");
		FinanceDisbursement wIFFinanceDisbursement = getWIFFinanceDisbursement();
		wIFFinanceDisbursement.setNewRecord(true);
		logger.debug("Leaving");
		return wIFFinanceDisbursement;
	}

	/**
	 * Fetch the Record  Finance Disbursement Details details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return WIFFinanceDisbursement
	 */
	@Override
	public FinanceDisbursement getWIFFinanceDisbursementById(final String id, String type) {
		logger.debug("Entering");
		FinanceDisbursement wIFFinanceDisbursement = new FinanceDisbursement();
		
		wIFFinanceDisbursement.setId(id);
		
		StringBuilder selectSql = new StringBuilder("Select FinReference, DisbDate, DisbSeq, DisbDesc, DisbAmount, DisbActDate, DisbDisbursed, DisbIsActive, DisbRemarks");
		selectSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(",");
		}
		selectSql.append(" From WIFFinDisbursementDetails");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(wIFFinanceDisbursement);
		RowMapper<FinanceDisbursement> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceDisbursement.class);
		
		try{
			wIFFinanceDisbursement = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			wIFFinanceDisbursement = null;
		}
		logger.debug("Leaving");
		return wIFFinanceDisbursement;
	}
	
	/**
	 * This method initialise the Record.
	 * @param WIFFinanceDisbursement (wIFFinanceDisbursement)
 	 * @return WIFFinanceDisbursement
	 */
	@Override
	public void initialize(FinanceDisbursement wIFFinanceDisbursement) {
		super.initialize(wIFFinanceDisbursement);
	}
	/**
	 * This method refresh the Record.
	 * @param WIFFinanceDisbursement (wIFFinanceDisbursement)
 	 * @return void
	 */
	@Override
	public void refresh(FinanceDisbursement wIFFinanceDisbursement) {
		
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the WIFFinDisbursementDetails or WIFFinDisbursementDetails_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Finance Disbursement Details by key FinReference
	 * 
	 * @param Finance Disbursement Details (wIFFinanceDisbursement)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(FinanceDisbursement wIFFinanceDisbursement,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete From WIFFinDisbursementDetails");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FinReference =:FinReference");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(wIFFinanceDisbursement);
		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				ErrorDetails errorDetails= getError("41003",wIFFinanceDisbursement.getId() ,wIFFinanceDisbursement.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {};
			}
		}catch(DataAccessException e){
			logger.error(e);
			ErrorDetails errorDetails= getError("41006",wIFFinanceDisbursement.getId() ,wIFFinanceDisbursement.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	/**
	 * This method insert new Records into WIFFinDisbursementDetails or WIFFinDisbursementDetails_Temp.
	 *
	 * save Finance Disbursement Details 
	 * 
	 * @param Finance Disbursement Details (wIFFinanceDisbursement)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@Override
	public String save(FinanceDisbursement wIFFinanceDisbursement,String type) {
		logger.debug("Entering");
		
		StringBuilder insertSql =new StringBuilder("Insert Into WIFFinDisbursementDetails");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (FinReference, DisbDate, DisbSeq, DisbDesc, DisbAmount, DisbActDate, DisbDisbursed, DisbIsActive, DisbRemarks");
		insertSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:FinReference, :DisbDate, :DisbSeq, :DisbDesc, :DisbAmount, :DisbActDate, :DisbDisbursed, :DisbIsActive, :DisbRemarks");
		insertSql.append(", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(wIFFinanceDisbursement);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return wIFFinanceDisbursement.getId();
	}
	
	/**
	 * This method updates the Record WIFFinDisbursementDetails or WIFFinDisbursementDetails_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Finance Disbursement Details by key FinReference and Version
	 * 
	 * @param Finance Disbursement Details (wIFFinanceDisbursement)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@SuppressWarnings("serial")
	@Override
	public void update(FinanceDisbursement wIFFinanceDisbursement,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update WIFFinDisbursementDetails");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set FinReference = :FinReference, DisbDate = :DisbDate, DisbSeq = :DisbSeq, DisbDesc = :DisbDesc, DisbAmount = :DisbAmount, DisbActDate = :DisbActDate, DisbDisbursed = :DisbDisbursed, DisbIsActive = :DisbIsActive, DisbRemarks = :DisbRemarks");
		updateSql.append(", Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where FinReference =:FinReference");
		
		if (!type.endsWith("_TEMP")){
			updateSql.append("  AND Version= :Version-1");
		}
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(wIFFinanceDisbursement);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :"+recordCount);
			ErrorDetails errorDetails= getError("41004",wIFFinanceDisbursement.getId() ,wIFFinanceDisbursement.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}

	/**
	 * Fetch the Record  Finance Disbursement Details details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return WIFFinanceDisbursement
	 */
	@Override
	public List<FinanceDisbursement> getWIFFinanceDisbursementDetails(final String id, String type) {
		logger.debug("Entering");
		FinanceDisbursement wIFFinanceDisbursement = new FinanceDisbursement();
		
		wIFFinanceDisbursement.setId(id);
		
		StringBuilder selectSql = new StringBuilder("Select FinReference, DisbDate, DisbSeq, DisbDesc, DisbAmount,");
		selectSql.append(" DisbActDate, DisbDisbursed, DisbIsActive, DisbRemarks");
		selectSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId,");
		selectSql.append(" RecordType, WorkflowId");

		/*if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(",");
		}*/
		selectSql.append(" From WIFFinDisbursementDetails");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(wIFFinanceDisbursement);
		RowMapper<FinanceDisbursement> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceDisbursement.class);
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);	
	}
	
	
	private ErrorDetails  getError(String errorId, String FinReference, String userLanguage){
		String[][] parms= new String[2][1];
		parms[1][0] = FinReference;
		parms[0][0] = PennantJavaUtil.getLabel("label_FinReference")+ ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId, parms[0],parms[1]), userLanguage);
	}

	
	
}