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
 * FileName    		:  DeferementHeaderDAOImpl.java                                                   * 	  
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
import com.pennant.backend.dao.finance.DefermentHeaderDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.finance.DefermentHeader;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>DeferementHeader model</b> class.<br>
 * 
 */

public class DefermentHeaderDAOImpl extends BasisCodeDAO<DefermentHeader> implements DefermentHeaderDAO {

	private static Logger logger = Logger.getLogger(DefermentHeaderDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	/**
	 * This method set the Work Flow id based on the module name and return the new DeferementHeader 
	 * @return DeferementHeader
	 */

	@Override
	public DefermentHeader getDefermentHeader(boolean isWIF) {
		logger.debug("Entering");
		String wifName="";
		
		if(isWIF){
			wifName = "WIFFinDefermentHeader";	
		}else{
			wifName = "FinDefermentHeader";
		}
		
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails(wifName);
		DefermentHeader defermentHeader= new DefermentHeader();
		if (workFlowDetails!=null){
			defermentHeader.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return defermentHeader;
	}


	/**
	 * This method get the module from method getDeferementHeader() and set the new record flag as true and return DeferementHeader()   
	 * @return DeferementHeader
	 */


	@Override
	public DefermentHeader getNewDefermentHeader(boolean isWIF) {
		logger.debug("Entering");
		DefermentHeader defermentHeader = getDefermentHeader(isWIF);
		defermentHeader.setNewRecord(true);
		logger.debug("Leaving");
		return defermentHeader;
	}

	/**
	 * Fetch the Record  Deferement Header details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return DeferementHeader
	 */
	@Override
	public DefermentHeader getDefermentHeaderById(final String id, String type,boolean isWIF) {
		logger.debug("Entering");
		DefermentHeader defermentHeader = getDefermentHeader(isWIF);
		
		defermentHeader.setId(id);
		
		StringBuilder selectSql = new StringBuilder("Select FinReference, DeferedSchdDate, DefSchdProfit, DefSchdPrincipal, DefRecalType, DefTillDate");
		selectSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		if(isWIF){
			selectSql.append(" From WIFFinDefermentHeader");	
		}else{
			selectSql.append(" From FinDefermentHeader");	
		}
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(defermentHeader);
		RowMapper<DefermentHeader> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(DefermentHeader.class);
		
		try{
			defermentHeader = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			defermentHeader = null;
		}
		logger.debug("Leaving");
		return defermentHeader;
	}
	
	/**
	 * This method initialise the Record.
	 * @param DeferementHeader (deferementHeader)
 	 * @return DeferementHeader
	 */
	@Override
	public void initialize(DefermentHeader defermentHeader) {
		super.initialize(defermentHeader);
	}
	/**
	 * This method refresh the Record.
	 * @param DeferementHeader (deferementHeader)
 	 * @return void
	 */
	@Override
	public void refresh(DefermentHeader defermentHeader) {
		
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the FinDefermentHeader or FinDefermentHeader_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Deferement Header by key FinReference
	 * 
	 * @param Deferement Header (deferementHeader)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	public void deleteByFinReference(String id,String type,boolean isWIF) {
		logger.debug("Entering");
		DefermentHeader defermentHeader = new DefermentHeader();
		defermentHeader.setId(id);
		
		StringBuilder deleteSql = new StringBuilder("Delete ");
		if(isWIF){
			deleteSql.append(" From WIFFinDefermentHeader");	
		}else{
			deleteSql.append(" From FinDefermentHeader");	
		}
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FinReference =:FinReference");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(defermentHeader);
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}
	
	/**
	 * This method Deletes the Record from the FinDefermentHeader or FinDefermentHeader_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Deferement Header by key FinReference
	 * 
	 * @param Deferement Header (deferementHeader)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(DefermentHeader defermentHeader,String type,boolean isWIF) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete ");
		if(isWIF){
			deleteSql.append(" From WIFFinDefermentHeader");	
		}else{
			deleteSql.append(" From FinDefermentHeader");	
		}
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FinReference =:FinReference and DeferedSchdDate = :DeferedSchdDate");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(defermentHeader);
		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				ErrorDetails errorDetails= getError("41003",defermentHeader.getId() ,defermentHeader.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {};
			}
		}catch(DataAccessException e){
			logger.error(e);
			ErrorDetails errorDetails= getError("41006",defermentHeader.getId() ,defermentHeader.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	/**
	 * This method insert new Records into FinDefermentHeader or FinDefermentHeader_Temp.
	 *
	 * save Deferement Header 
	 * 
	 * @param Deferement Header (deferementHeader)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@Override
	public String save(DefermentHeader defermentHeader,String type,boolean isWIF) {
		logger.debug("Entering");
		
		StringBuilder insertSql =new StringBuilder("Insert Into ");
		if(isWIF){
			insertSql.append(" WIFFinDefermentHeader");	
		}else{
			insertSql.append(" FinDefermentHeader");	
		}
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (FinReference, DeferedSchdDate, DefSchdProfit, DefSchdPrincipal, DefRecalType, DefTillDate");
		insertSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:FinReference, :DeferedSchdDate, :DefSchdProfit, :DefSchdPrincipal, :DefRecalType, :DefTillDate");
		insertSql.append(", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(defermentHeader);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return defermentHeader.getId();
	}
	
	/**
	 * This method insert list of new Records into FinDefermentHeader or FinDefermentHeader_Temp.
	 *
	 * save Deferement Header 
	 * 
	 * @param Deferement Header (deferementHeader)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@Override
	public void saveList(List<DefermentHeader> defermentHeader,String type,boolean isWIF) {
		logger.debug("Entering");
		
		StringBuilder insertSql =new StringBuilder("Insert Into ");
		if(isWIF){
			insertSql.append(" WIFFinDefermentHeader");	
		}else{
			insertSql.append(" FinDefermentHeader");	
		}
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (FinReference, DeferedSchdDate, DefSchdProfit, DefSchdPrincipal, DefRecalType, DefTillDate");
		insertSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:FinReference, :DeferedSchdDate, :DefSchdProfit, :DefSchdPrincipal, :DefRecalType, :DefTillDate");
		insertSql.append(", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(defermentHeader.toArray());
		this.namedParameterJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
	}
	
	/**
	 * Method for updating List of data after RateChanges
	 */
	@Override
	public void updateList(List<DefermentHeader> defermentHeader,String type,boolean isWIF) {
		logger.debug("Entering");
		
		StringBuilder	updateSql =new StringBuilder("Update ");
		if(isWIF){
			updateSql.append(" WIFFinDefermentHeader");	
		}else{
			updateSql.append(" FinDefermentHeader");	
		}
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set FinReference = :FinReference, DeferedSchdDate = :DeferedSchdDate, " );
		updateSql.append(" DefSchdProfit = :DefSchdProfit, DefSchdPrincipal = :DefSchdPrincipal, " );
		updateSql.append(" DefRecalType = :DefRecalType, DefTillDate = :DefTillDate ");
		updateSql.append(", Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, " );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, " );
		updateSql.append(" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where FinReference =:FinReference");
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(defermentHeader.toArray());
		this.namedParameterJdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);
		logger.debug("Leaving");
	}
	
	/**
	 * This method updates the Record FinDefermentHeader or FinDefermentHeader_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Deferement Header by key FinReference and Version
	 * 
	 * @param Deferement Header (deferementHeader)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@SuppressWarnings("serial")
	@Override
	public void update(DefermentHeader defermentHeader,String type,boolean isWIF) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update ");
		if(isWIF){
			updateSql.append(" WIFFinDefermentHeader");	
		}else{
			updateSql.append(" FinDefermentHeader");	
		}
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set FinReference = :FinReference, DeferedSchdDate = :DeferedSchdDate, DefSchdProfit = :DefSchdProfit, DefSchdPrincipal = :DefSchdPrincipal, DefRecalType = :DefRecalType, DefTillDate = :DefTillDate");
		updateSql.append(", Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where FinReference =:FinReference");
		
		if (!type.endsWith("_TEMP")){
			updateSql.append("  AND Version= :Version-1");
		}
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(defermentHeader);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :"+recordCount);
			ErrorDetails errorDetails= getError("41004",defermentHeader.getId() ,defermentHeader.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	
	/**
	 * Fetch the Record  Deferement Header details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return DeferementHeader
	 */
	@Override
	public List<DefermentHeader> getDefermentHeaders(final String id, String type,boolean isWIF) {
		logger.debug("Entering");
		DefermentHeader defermentHeader = getDefermentHeader(isWIF);
		
		defermentHeader.setId(id);
		
		StringBuilder selectSql = new StringBuilder("Select FinReference, DeferedSchdDate, DefSchdProfit, DefSchdPrincipal, DefRecalType, DefTillDate");
		selectSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		
		if(isWIF){
			selectSql.append(" From WIFFinDefermentHeader");	
		}else{
			selectSql.append(" From FinDefermentHeader");	
		}
		
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(defermentHeader);
		RowMapper<DefermentHeader> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(DefermentHeader.class);

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);	
	}	
	
	/**
	 * Method for getting Count of records in FinODDetails
	 * @param finReference
	 * @return
	 */
	public int getRpyDfrCount(String finReference){
		logger.debug("Entering");
		StringBuilder selectQry = new StringBuilder(" Select count(FinReference)  " );
		selectQry.append(" from FinDefermentHeader " );
		selectQry.append(" where FinReference = '" );
		selectQry.append(finReference+"'");
		logger.debug("selectSql: " + selectQry.toString());

		int recordCount =  this.namedParameterJdbcTemplate.getJdbcOperations().queryForInt(selectQry.toString());
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