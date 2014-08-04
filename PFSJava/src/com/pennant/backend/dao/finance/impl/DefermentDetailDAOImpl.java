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
 * FileName    		:  DefermentDetailDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  02-12-2011    														*
 *                                                                  						*
 * Modified Date    :  02-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 02-12-2011       Pennant	                 0.1                                            * 
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


import java.util.Date;
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
import com.pennant.backend.dao.finance.DefermentDetailDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.finance.DefermentDetail;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>DeferementDetail model</b> class.<br>
 * 
 */

public class DefermentDetailDAOImpl extends BasisCodeDAO<DefermentDetail> implements DefermentDetailDAO {

	private static Logger logger = Logger.getLogger(DefermentDetailDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	/**
	 * This method set the Work Flow id based on the module name and return the new DeferementDetail 
	 * @return DeferementDetail
	 */
	@Override
	public DefermentDetail getDefermentDetail(boolean isWIF) {
		logger.debug("Entering");
		String wifName="";
		
		if(isWIF){
			wifName = "WIFFinDefermentDetail";	
		}else{
			wifName = "FinDefermentDetail";
		}
		
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails(wifName);
		DefermentDetail defermentDetail= new DefermentDetail();
		if (workFlowDetails!=null){
			defermentDetail.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return defermentDetail;
	}

	/**
	 * This method get the module from method getDeferementDetail() and set the
	 * new record flag as true and return DeferementDetail()
	 * 
	 * @return DeferementDetail
	 */
	@Override
	public DefermentDetail getNewDefermentDetail(boolean isWIF) {
		logger.debug("Entering");
		DefermentDetail defermentDetail = getDefermentDetail(isWIF);
		defermentDetail.setNewRecord(true);
		logger.debug("Leaving");
		return defermentDetail;
	}

	/**
	 * Fetch the Record  Deferement Details details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return DeferementDetail
	 */
	@Override
	public DefermentDetail getDefermentDetailById(final String id, final Date schdDate,String type,boolean isWIF) {
		logger.debug("Entering");
		
		DefermentDetail defermentDetail = new DefermentDetail();
		defermentDetail.setId(id);
		defermentDetail.setDeferedSchdDate(schdDate);
		
		StringBuilder selectSql = new StringBuilder("Select FinReference, DeferedSchdDate," );
		selectSql.append(" DefSchdProfit, DefSchdPrincipal, DeferedRpyDate, DefRpySchdPft," );
		selectSql.append(" DefRpySchdPri, DefRpySchdPftBal, DefRpySchdPriBal, DefPaidPftTillDate," );
		selectSql.append(" DefPaidPriTillDate, DefPftBalance, DefPriBalance, ");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId ");
		if(isWIF){
			selectSql.append(" From WIFFinDefermentDetail");	
		}else{
			selectSql.append(" From FinDefermentDetail");	
		}
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference AND DeferedSchdDate =:DeferedSchdDate ");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(defermentDetail);
		RowMapper<DefermentDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(DefermentDetail.class);
		
		try{
			defermentDetail = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			defermentDetail = null;
		}
		logger.debug("Leaving");
		return defermentDetail;
	}
	
	/**
	 * Fetch the Record  Deferement Details details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return DeferementDetail
	 */
	@Override
	public DefermentDetail getDefermentDetailForBatch(final String id, final Date schdDate) {
		logger.debug("Entering");
		
		DefermentDetail defermentDetail = new DefermentDetail();
		defermentDetail.setId(id);
		defermentDetail.setDeferedRpyDate(schdDate);
		
		StringBuilder selectSql = new StringBuilder("Select FinReference, DeferedSchdDate," );
		selectSql.append(" DefPaidPftTillDate, DefPaidPriTillDate, DefPftBalance, DefPriBalance ");
		selectSql.append(" From FinDefermentDetail");	
		selectSql.append(" Where FinReference =:FinReference AND DeferedRpyDate =:DeferedRpyDate ");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(defermentDetail);
		RowMapper<DefermentDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(DefermentDetail.class);
		
		try{
			defermentDetail = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			defermentDetail = null;
		}
		logger.debug("Leaving");
		return defermentDetail;
	}
	
	/**
	 * This method initialise the Record.
	 * @param DeferementDetail (deferementDetail)
 	 * @return DeferementDetail
	 */
	@Override
	public void initialize(DefermentDetail defermentDetail) {
		super.initialize(defermentDetail);
	}
	/**
	 * This method refresh the Record.
	 * @param DeferementDetail (deferementDetail)
 	 * @return void
	 */
	@Override
	public void refresh(DefermentDetail defermentDetail) {
		
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the FinDefermentDetail or FinDefermentDetail_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Deferement Details by key FinReference
	 * 
	 * @param Deferement Details (deferementDetail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	public void deleteByFinReference(String id,String type, boolean isWIF, long logKey) {
		logger.debug("Entering");
		DefermentDetail defermentDetail = new DefermentDetail();
		defermentDetail.setId(id);
		StringBuilder deleteSql = new StringBuilder("Delete From ");
		if(isWIF){
			deleteSql.append(" WIFFinDefermentDetail");	
		}else{
			deleteSql.append(" FinDefermentDetail");	
		}
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FinReference =:FinReference");
		if(logKey != 0){
			deleteSql.append(" AND LogKey =:LogKey");
		}
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(defermentDetail);
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}
	
	/**
	 * This method Deletes the Record from the FinDefermentDetail or FinDefermentDetail_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Deferement Details by key FinReference
	 * 
	 * @param Deferement Details (deferementDetail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(DefermentDetail defermentDetail,String type, boolean isWIF) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete From ");
		if(isWIF){
			deleteSql.append(" WIFFinDefermentDetail");	
		}else{
			deleteSql.append(" FinDefermentDetail");	
		}
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FinReference =:FinReference and DeferedSchdDate = :DeferedSchdDate");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(defermentDetail);
		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				ErrorDetails errorDetails= getError("41003",defermentDetail.getId() ,defermentDetail.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {};
			}
		}catch(DataAccessException e){
			logger.error(e);
			ErrorDetails errorDetails= getError("41006",defermentDetail.getId() ,defermentDetail.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	/**
	 * This method insert new Records into FinDefermentDetail or FinDefermentDetail_Temp.
	 *
	 * save Deferement Details 
	 * 
	 * @param Deferement Details (deferementDetail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@Override
	public String save(DefermentDetail defermentDetail,String type, boolean isWIF) {
		logger.debug("Entering");
		
		StringBuilder insertSql =new StringBuilder("Insert Into ");
		if(isWIF){
			insertSql.append(" WIFFinDefermentDetail");	
		}else{
			insertSql.append(" FinDefermentDetail");	
		}
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (FinReference, DeferedSchdDate, DefSchdProfit, DefSchdPrincipal, DeferedRpyDate, DefRpySchdPft, DefRpySchdPri, DefRpySchdPftBal, DefRpySchdPriBal, DefPaidPftTillDate, DefPaidPriTillDate, DefPftBalance, DefPriBalance");
		insertSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:FinReference, :DeferedSchdDate, :DefSchdProfit, :DefSchdPrincipal, :DeferedRpyDate, :DefRpySchdPft, :DefRpySchdPri, :DefRpySchdPftBal, :DefRpySchdPriBal, :DefPaidPftTillDate, :DefPaidPriTillDate, :DefPftBalance, :DefPriBalance");
		insertSql.append(", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(defermentDetail);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return defermentDetail.getId();
	}
	
	/**
	 * This method insert list of new Records into FinDefermentDetail or FinDefermentDetail_Temp.
	 *
	 * save Deferement Details 
	 * 
	 * @param Deferement Details (deferementDetail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@Override
	public void saveList(List<DefermentDetail> defermentDetail,String type, boolean isWIF) {
		logger.debug("Entering");
		
		StringBuilder insertSql =new StringBuilder("Insert Into ");
		if(isWIF){
			insertSql.append(" WIFFinDefermentDetail");	
		}else{
			insertSql.append(" FinDefermentDetail");	
		}
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (FinReference, DeferedSchdDate, DefSchdProfit, DefSchdPrincipal, DeferedRpyDate, DefRpySchdPft, DefRpySchdPri, DefRpySchdPftBal, DefRpySchdPriBal, DefPaidPftTillDate, DefPaidPriTillDate, DefPftBalance, DefPriBalance,");
		if(type.contains("Log")){
			insertSql.append(" LogKey , ");
		}
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:FinReference, :DeferedSchdDate, :DefSchdProfit, :DefSchdPrincipal, :DeferedRpyDate, :DefRpySchdPft, :DefRpySchdPri, :DefRpySchdPftBal, :DefRpySchdPriBal, :DefPaidPftTillDate, :DefPaidPriTillDate, :DefPftBalance, :DefPriBalance,");
		if(type.contains("Log")){
			insertSql.append(" :LogKey , ");
		}
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(defermentDetail.toArray());
		this.namedParameterJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Updation of Deferement Details list after Rate Changes
	 */
	@Override
	public void updateList(List<DefermentDetail> defermentDetail,String type, boolean isWIF) {
		logger.debug("Entering");
		
		StringBuilder	updateSql =new StringBuilder("Update ");
		if(isWIF){
			updateSql.append(" WIFFinDefermentDetail");	
		}else{
			updateSql.append(" FinDefermentDetail");	
		}
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set FinReference = :FinReference, DeferedSchdDate = :DeferedSchdDate, " );
		updateSql.append(" DefSchdProfit = :DefSchdProfit, DefSchdPrincipal = :DefSchdPrincipal, " );
		updateSql.append(" DeferedRpyDate = :DeferedRpyDate, DefRpySchdPft = :DefRpySchdPft, " );
		updateSql.append(" DefRpySchdPri = :DefRpySchdPri, DefRpySchdPftBal = :DefRpySchdPftBal, " );
		updateSql.append(" DefRpySchdPriBal = :DefRpySchdPriBal, DefPaidPftTillDate = :DefPaidPftTillDate, " );
		updateSql.append(" DefPaidPriTillDate = :DefPaidPriTillDate, DefPftBalance = :DefPftBalance, " );
		updateSql.append(" DefPriBalance = :DefPriBalance ,");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn," );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, " );
		updateSql.append(" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where FinReference =:FinReference");
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(defermentDetail.toArray());
		this.namedParameterJdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);
		logger.debug("Leaving");
	}
	
	/**
	 * This method updates the Record FinDefermentDetail or FinDefermentDetail_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Deferement Details by key FinReference and Version
	 * 
	 * @param Deferement Details (deferementDetail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@SuppressWarnings("serial")
	@Override
	public void update(DefermentDetail defermentDetail,String type, boolean isWIF) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update ");
		if(isWIF){
			updateSql.append(" WIFFinDefermentDetail");	
		}else{
			updateSql.append(" FinDefermentDetail");	
		}
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set FinReference = :FinReference, DeferedSchdDate = :DeferedSchdDate, DefSchdProfit = :DefSchdProfit, DefSchdPrincipal = :DefSchdPrincipal, DeferedRpyDate = :DeferedRpyDate, DefRpySchdPft = :DefRpySchdPft, DefRpySchdPri = :DefRpySchdPri, DefRpySchdPftBal = :DefRpySchdPftBal, DefRpySchdPriBal = :DefRpySchdPriBal, DefPaidPftTillDate = :DefPaidPftTillDate, DefPaidPriTillDate = :DefPaidPriTillDate, DefPftBalance = :DefPftBalance, DefPriBalance = :DefPriBalance");
		updateSql.append(", Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where FinReference =:FinReference");
		
		/*if (!type.endsWith("_TEMP")){
			updateSql.append("  AND Version= :Version-1");
		}*/
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(defermentDetail);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :"+recordCount);
			ErrorDetails errorDetails= getError("41004",defermentDetail.getId() ,defermentDetail.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	@SuppressWarnings("serial")
	@Override
	public void updateBatch(DefermentDetail defermentDetail) {
		int recordCount = 0;
		
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update FinDefermentDetail");	
		updateSql.append(" Set DefPaidPftTillDate = :DefPaidPftTillDate, DefPaidPriTillDate = :DefPaidPriTillDate, " );
		updateSql.append(" DefPftBalance = :DefPftBalance, DefPriBalance = :DefPriBalance");
		updateSql.append(" Where FinReference =:FinReference  AND DeferedSchdDate =:DeferedSchdDate ");
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(defermentDetail);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :"+recordCount);
			ErrorDetails errorDetails= getError("41004",defermentDetail.getId() ,defermentDetail.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
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
	public List<DefermentDetail> getDefermentDetails(final String id, String type, boolean isWIF) {
		logger.debug("Entering");
		
		DefermentDetail defermentDetail = new DefermentDetail();
		defermentDetail.setId(id);
		
		StringBuilder selectSql = new StringBuilder("Select FinReference, DeferedSchdDate, DefSchdProfit, DefSchdPrincipal, DeferedRpyDate, DefRpySchdPft, DefRpySchdPri, DefRpySchdPftBal, DefRpySchdPriBal, DefPaidPftTillDate, DefPaidPriTillDate, DefPftBalance, DefPriBalance");
		selectSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");


		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append("");
		}
		
		if(isWIF){
			selectSql.append(" From WIFFinDefermentDetail");	
		}else{
			selectSql.append(" From FinDefermentDetail");	
		}
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(defermentDetail);
		RowMapper<DefermentDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(DefermentDetail.class);
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
	public List<DefermentDetail> getDefermentDetails(final String id, String type, boolean isWIF, long logKey) {
		logger.debug("Entering");
		
		DefermentDetail defermentDetail = new DefermentDetail();
		defermentDetail.setId(id);
		defermentDetail.setLogKey(logKey);
		
		StringBuilder selectSql = new StringBuilder("Select FinReference, DeferedSchdDate, DefSchdProfit, DefSchdPrincipal, DeferedRpyDate, DefRpySchdPft, DefRpySchdPri, DefRpySchdPftBal, DefRpySchdPriBal, DefPaidPftTillDate, DefPaidPriTillDate, DefPftBalance, DefPriBalance");
		selectSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		
		if(isWIF){
			selectSql.append(" From WIFFinDefermentDetail");	
		}else{
			selectSql.append(" From FinDefermentDetail");	
		}
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference AND LogKey =:LogKey");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(defermentDetail);
		RowMapper<DefermentDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(DefermentDetail.class);
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);	
	}
	
	/**
	 * Method for get the count of FinDefermentDetail records depend on condition
	 * @param defSchdDate
	 * @param defRpyDate
	 * @return
	 */
	@Override
	public int getFinReferenceCount(String finReference, Date defSchdDate, Date defRpyDate) {
		logger.debug("Entering");
		StringBuilder selectQry = new StringBuilder("select COUNT(FinReference) FROM FinDefermentDetail");
		selectQry.append(" WHERE FinReference ='"+finReference+"'");
		selectQry.append(" AND DeferedSchdDate <= '" + defSchdDate + "' AND DeferedRpyDate > '" + defRpyDate + "'");
		
		logger.debug("selectSql: " + selectQry.toString());
		int recordCount = this.namedParameterJdbcTemplate.getJdbcOperations().queryForInt(selectQry.toString());
		logger.debug("Leaving");
		return recordCount;
	}
	
	private ErrorDetails  getError(String errorId, String FinReference, String userLanguage){
		String[][] parms= new String[2][1];
		parms[1][0] = FinReference;
		parms[0][0] = PennantJavaUtil.getLabel("label_FinReference")+ ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId, parms[0],parms[1]), userLanguage);
	}

	
}