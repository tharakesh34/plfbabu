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
 * FileName    		:  FinanceDisbursementDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  15-11-2011    														*
 *                                                                  						*
 * Modified Date    :  15-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 15-11-2011       Pennant	                 0.1                                            * 
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
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.finance.FinanceDisbursementDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>FinanceDisbursement model</b> class.<br>
 * 
 */

public class FinanceDisbursementDAOImpl extends BasisCodeDAO<FinanceDisbursement> implements FinanceDisbursementDAO {

	private static Logger logger = Logger.getLogger(FinanceDisbursementDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	/**
	 * This method set the Work Flow id based on the module name and return the new FinanceDisbursement 
	 * @return FinanceDisbursement
	 */

	@Override
	public FinanceDisbursement getFinanceDisbursement(boolean isWIF) {
		logger.debug("Entering");
		String wifName="";
		
		if(isWIF){
			wifName = "WIFFinScheduleDetails";	
		}else{
			wifName = "FinScheduleDetails";
		}
		
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails(wifName);
		FinanceDisbursement financeDisbursement= new FinanceDisbursement();
		if (workFlowDetails!=null){
			financeDisbursement.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return financeDisbursement;
	}


	/**
	 * This method get the module from method getFinanceDisbursement() and set the new record flag as true and return FinanceDisbursement()   
	 * @return FinanceDisbursement
	 */


	@Override
	public FinanceDisbursement getNewFinanceDisbursement(boolean isWIF) {
		logger.debug("Entering");
		FinanceDisbursement financeDisbursement = getFinanceDisbursement(isWIF);
		financeDisbursement.setNewRecord(true);
		logger.debug("Leaving");
		return financeDisbursement;
	}

	/**
	 * Fetch the Record  Finance Disbursement Details details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return FinanceDisbursement
	 */
	@Override
	public FinanceDisbursement getFinanceDisbursementById(final String id, String type,boolean isWIF) {
		logger.debug("Entering");
		
		FinanceDisbursement financeDisbursement = new FinanceDisbursement();
		financeDisbursement.setId(id);
		
		StringBuilder selectSql = new StringBuilder("Select FinReference, DisbDate, DisbSeq, DisbDesc,");
		selectSql.append(" DisbAccountId, DisbAmount, DisbReqDate, DisbDisbursed, DisbIsActive , FeeChargeAmt,");
		selectSql.append(" DisbRemarks, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId");

		if(StringUtils.trimToEmpty(type).contains("View")){
			if(!isWIF){
				selectSql.append(" , lovDescDisbExpType, lovdescDisbBenificiary, lovDescDisbBenfShrtName ");
			}
		}
		if(isWIF){
			selectSql.append(" From WIFFinDisbursementDetails");	
		}else{
			selectSql.append(" , DisbType, DisbClaim, DisbExpType, DisbBeneficiary, DisbRetPerc, DisbRetAmount, " );
			selectSql.append(" AutoDisb, NetAdvDue, NetRetDue, DisbRetPaid, RetPaidDate ");
			selectSql.append(" From FinDisbursementDetails");	
		}
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeDisbursement);
		RowMapper<FinanceDisbursement> typeRowMapper = ParameterizedBeanPropertyRowMapper
		                .newInstance(FinanceDisbursement.class);
		
		try{
			financeDisbursement = this.namedParameterJdbcTemplate
			.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			financeDisbursement = null;
		}
		logger.debug("Leaving");
		return financeDisbursement;
	}
	
	/**
	 * This method initialise the Record.
	 * @param FinanceDisbursement (financeDisbursement)
 	 * @return FinanceDisbursement
	 */
	@Override
	public void initialize(FinanceDisbursement financeDisbursement) {
		super.initialize(financeDisbursement);
	}
	/**
	 * This method refresh the Record.
	 * @param FinanceDisbursement (financeDisbursement)
 	 * @return void
	 */
	@Override
	public void refresh(FinanceDisbursement financeDisbursement) {
		
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the FinDisbursementDetails or FinDisbursementDetails_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Finance Disbursement Details by key FinReference
	 * 
	 * @param Finance Disbursement Details (financeDisbursement)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	public void deleteByFinReference(String id,String type, boolean isWIF, long logKey) {
		logger.debug("Entering");
		FinanceDisbursement financeDisbursement = new FinanceDisbursement();
		financeDisbursement.setId(id);
		
		StringBuilder deleteSql = new StringBuilder("Delete From ");
		
		if(isWIF){
			deleteSql.append(" WIFFinDisbursementDetails");	
		}else{
			deleteSql.append(" FinDisbursementDetails");	
		}
		
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FinReference =:FinReference");
		if(logKey != 0){
			deleteSql.append(" AND LogKey =:LogKey");
		}
		
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeDisbursement);
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}
	
	/**
	 * This method Deletes the Record from the FinDisbursementDetails or FinDisbursementDetails_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Finance Disbursement Details by key FinReference
	 * 
	 * @param Finance Disbursement Details (financeDisbursement)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(FinanceDisbursement financeDisbursement,String type, boolean isWIF) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete From ");
		
		if(isWIF){
			deleteSql.append(" WIFFinDisbursementDetails");	
		}else{
			deleteSql.append(" FinDisbursementDetails");	
		}
		
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FinReference =:FinReference and DisbDate = :DisbDate");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeDisbursement);
		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				ErrorDetails errorDetails= getError("41003",financeDisbursement.getId() 
						,financeDisbursement.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {};
			}
		}catch(DataAccessException e){
			logger.error(e);
			ErrorDetails errorDetails= getError("41006",financeDisbursement.getId()
					,financeDisbursement.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	/**
	 * This method insert new Records into FinDisbursementDetails or FinDisbursementDetails_Temp.
	 *
	 * save Finance Disbursement Details 
	 * 
	 * @param Finance Disbursement Details (financeDisbursement)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@Override
	public String save(FinanceDisbursement financeDisbursement,String type, boolean isWIF) {
		logger.debug("Entering");
		
		StringBuilder insertSql =new StringBuilder("Insert Into ");
		if(isWIF){
			insertSql.append(" WIFFinDisbursementDetails");	
		}else{
			insertSql.append(" FinDisbursementDetails");	
		}
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (FinReference, DisbDate, DisbSeq, DisbDesc, DisbAccountId, DisbAmount, DisbReqDate, FeeChargeAmt,");
		if(!isWIF){
			insertSql.append(" DisbType, DisbClaim, DisbExpType, DisbBeneficiary, DisbRetPerc, DisbRetAmount, " );
			insertSql.append(" AutoDisb, NetAdvDue, NetRetDue, DisbRetPaid, RetPaidDate, ");
		}
		insertSql.append(" DisbDisbursed, DisbIsActive, DisbRemarks, Version , LastMntBy, LastMntOn, RecordStatus,");
		insertSql.append(" RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:FinReference, :DisbDate, :DisbSeq, :DisbDesc, :DisbAccountId, :DisbAmount,:DisbReqDate, :FeeChargeAmt, ");
		if(!isWIF){
			insertSql.append(" :DisbType, :DisbClaim, :DisbExpType, :DisbBeneficiary, :DisbRetPerc, :DisbRetAmount, " );
			insertSql.append(" :AutoDisb, :NetAdvDue, :NetRetDue, :DisbRetPaid, :RetPaidDate, ");
		}
		insertSql.append(" :DisbDisbursed, :DisbIsActive, :DisbRemarks, :Version , :LastMntBy, ");
		insertSql.append(" :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType,");
		insertSql.append(" :WorkflowId)");
		
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeDisbursement);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return financeDisbursement.getId();
	}
	
	/**
	 * This method inserts List of Records into FinDisbursementDetails or FinDisbursementDetails_Temp.
	 *
	 * save Finance Disbursement Details 
	 * 
	 * @param Finance Disbursement Details (financeDisbursement)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@Override
	public void saveList(List<FinanceDisbursement> financeDisbursement,String type, boolean isWIF) {
		logger.debug("Entering");
		
		StringBuilder insertSql =new StringBuilder("Insert Into ");
		if(isWIF){
			insertSql.append(" WIFFinDisbursementDetails");	
		}else{
			insertSql.append(" FinDisbursementDetails");	
		}
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (FinReference, DisbDate, DisbSeq, DisbDesc, DisbAccountId, DisbAmount, DisbReqDate, FeeChargeAmt,");
		if(!isWIF){
			insertSql.append(" DisbType, DisbClaim, DisbExpType, DisbBeneficiary, DisbRetPerc, DisbRetAmount, " );
			insertSql.append(" AutoDisb, NetAdvDue, NetRetDue, DisbRetPaid, RetPaidDate, ");	
			if(type.contains("Log")){
				insertSql.append(" LogKey , ");
			}
		}
		insertSql.append(" DisbDisbursed, DisbIsActive, DisbRemarks, Version , LastMntBy, LastMntOn, RecordStatus,");
		insertSql.append(" RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:FinReference, :DisbDate, :DisbSeq, :DisbDesc, :DisbAccountId, :DisbAmount,:DisbReqDate, :FeeChargeAmt, ");
		if(!isWIF){
			insertSql.append(" :DisbType, :DisbClaim, :DisbExpType, :DisbBeneficiary, :DisbRetPerc, :DisbRetAmount, " );
			insertSql.append(" :AutoDisb, :NetAdvDue, :NetRetDue, :DisbRetPaid, :RetPaidDate, ");
			if(type.contains("Log")){
				insertSql.append(" :LogKey , ");
			}
		}
		insertSql.append("  :DisbDisbursed, :DisbIsActive, :DisbRemarks, :Version , :LastMntBy, ");
		insertSql.append(" :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType,");
		insertSql.append(" :WorkflowId)");
		
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(financeDisbursement.toArray());
		this.namedParameterJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
	}
	
	/**
	 * This method updates the Record FinDisbursementDetails or FinDisbursementDetails_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Finance Disbursement Details by key FinReference and Version
	 * 
	 * @param Finance Disbursement Details (financeDisbursement)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@SuppressWarnings("serial")
	@Override
	public void update(FinanceDisbursement financeDisbursement,String type, boolean isWIF) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update ");
		if(isWIF){
			updateSql.append(" WIFFinDisbursementDetails");	
		}else{
			updateSql.append(" FinDisbursementDetails");	
		}
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set FinReference = :FinReference, DisbDate = :DisbDate, DisbSeq = :DisbSeq," );
		updateSql.append(" DisbDesc = :DisbDesc,DisbAccountId = :DisbAccountId, DisbAmount = :DisbAmount, FeeChargeAmt=:FeeChargeAmt, ");
		if(!isWIF){
			updateSql.append(" DisbType=:DisbType, DisbClaim=:DisbClaim, DisbExpType=:DisbExpType, " );
			updateSql.append(" DisbBeneficiary=:DisbBeneficiary, DisbRetPerc=:DisbRetPerc, DisbRetAmount=:DisbRetAmount, " );
			updateSql.append(" AutoDisb=:AutoDisb, NetAdvDue=:NetAdvDue, NetRetDue=:NetRetDue, DisbRetPaid=:DisbRetPaid, RetPaidDate=:RetPaidDate, ");	
		}
		updateSql.append(" DisbReqDate = :DisbReqDate, DisbDisbursed = :DisbDisbursed, DisbIsActive = :DisbIsActive,");
		updateSql.append(" DisbRemarks = :DisbRemarks, Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode,");
		updateSql.append(" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where FinReference =:FinReference and DisbDate = :DisbDate AND DisbSeq = :DisbSeq");
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeDisbursement);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :"+recordCount);
			ErrorDetails errorDetails= getError("41004",financeDisbursement.getId() ,financeDisbursement.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	@SuppressWarnings("serial")
	@Override
	public void updateBatchDisb(FinanceDisbursement financeDisbursement,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update FinDisbursementDetails");	
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set DisbDisbursed = :DisbDisbursed " );
		updateSql.append(" Where FinReference =:FinReference and DisbDate = :DisbDate AND DisbSeq = :DisbSeq");
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeDisbursement);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :"+recordCount);
			ErrorDetails errorDetails= getError("41004",financeDisbursement.getId() ,financeDisbursement.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	/**
	 * This method updates the LinkedTranId 
	 * 
	 * @param Finance Disbursement Details (financeDisbursement)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */	
	@Override
	public void updateLinkedTranId(String finReference, long linkedTranId, String type) {
		logger.debug("Entering");
		FinanceDisbursement financeDisbursement = new FinanceDisbursement();
		financeDisbursement.setFinReference(finReference);
		financeDisbursement.setLinkedTranId(linkedTranId);
		
		StringBuilder updateSql = new StringBuilder("Update FinDisbursementDetails");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set LinkedTranId = :LinkedTranId" );
		updateSql.append(" Where FinReference =:FinReference");
		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeDisbursement);
		this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		logger.debug("Leaving");
	}
	
	/**
	 * Fetch the List of Finance Disbursement Detail Records by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return WIFFinanceDisbursement
	 */
	@Override
	public List<FinanceDisbursement> getFinanceDisbursementDetails(final String id, String type, boolean isWIF) {
		logger.debug("Entering");
		
		FinanceDisbursement wIFFinanceDisbursement = new FinanceDisbursement();
		wIFFinanceDisbursement.setId(id);
		
		StringBuilder selectSql = new StringBuilder("Select FinReference, DisbDate, DisbSeq, DisbDesc,FeeChargeAmt, ");
		selectSql.append(" DisbAccountId, DisbAmount, DisbReqDate, DisbDisbursed, DisbIsActive, DisbRemarks,");
		if(!isWIF){
			selectSql.append(" DisbType, DisbClaim, DisbExpType, DisbBeneficiary, DisbRetPerc, DisbRetAmount, " );
			selectSql.append(" AutoDisb, NetAdvDue, NetRetDue, DisbRetPaid, RetPaidDate, ");
		}
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, ");
		selectSql.append(" NextTaskId, RecordType, WorkflowId");

		if(StringUtils.trimToEmpty(type).contains("View")){
			if(!isWIF){
				selectSql.append(" , lovDescDisbExpType, lovdescDisbBenificiary, lovDescDisbBenfShrtName ");
			}
		}
		
		if(isWIF){
			selectSql.append(" From WIFFinDisbursementDetails");	
		}else{
			selectSql.append(" From FinDisbursementDetails");	
		}
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(wIFFinanceDisbursement);
		RowMapper<FinanceDisbursement> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceDisbursement.class);
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);	
	}
	
	/**
	 * Fetch the List of Finance Disbursement Detail Records by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return WIFFinanceDisbursement
	 */
	@Override
	public List<FinanceDisbursement> getFinanceDisbursementDetails(final String id, String type, boolean isWIF, long logKey) {
		logger.debug("Entering");
		
		FinanceDisbursement wIFFinanceDisbursement = new FinanceDisbursement();
		wIFFinanceDisbursement.setId(id);
		wIFFinanceDisbursement.setLogKey(logKey);
		
		StringBuilder selectSql = new StringBuilder("Select FinReference, DisbDate, DisbSeq, DisbDesc,FeeChargeAmt, ");
		selectSql.append(" DisbAccountId, DisbAmount, DisbReqDate, DisbDisbursed, DisbIsActive, DisbRemarks,");
		if(!isWIF){
			selectSql.append(" DisbType, DisbClaim, DisbExpType, DisbBeneficiary, DisbRetPerc, DisbRetAmount, " );
			selectSql.append(" AutoDisb, NetAdvDue, NetRetDue, DisbRetPaid, RetPaidDate, ");
		}
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, ");
		selectSql.append(" NextTaskId, RecordType, WorkflowId");
		
		if(StringUtils.trimToEmpty(type).contains("View")){
			if(!isWIF){
				selectSql.append(" , lovDescDisbExpType, lovdescDisbBenificiary, lovDescDisbBenfShrtName ");
			}
		}
		
		if(isWIF){
			selectSql.append(" From WIFFinDisbursementDetails");	
		}else{
			selectSql.append(" From FinDisbursementDetails");	
		}
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference AND LogKey =:LogKey");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(wIFFinanceDisbursement);
		RowMapper<FinanceDisbursement> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceDisbursement.class);
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);	
	}
	
	
	private ErrorDetails  getError(String errorId, String finReference, String userLanguage){
		String[][] parms= new String[2][1];
		parms[1][0] = finReference;
		parms[0][0] = PennantJavaUtil.getLabel("label_FinReference")+ ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId, parms[0],parms[1]), userLanguage);
	}

	
}