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
 * FileName    		:  RepayInstructionDAOImpl.java                                                   * 	  
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
import com.pennant.backend.dao.finance.RepayInstructionDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.finance.RepayInstruction;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * DAO methods implementation for the <b>RepayInstruction model</b> class.<br>
 * 
 */

public class RepayInstructionDAOImpl extends BasisCodeDAO<RepayInstruction> implements RepayInstructionDAO {

	private static Logger logger = Logger.getLogger(RepayInstructionDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	public RepayInstructionDAOImpl() {
		super();
	}


	/**
	 * Fetch the Record  Repay Instruction Detail details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return RepayInstruction
	 */
	@Override
	public RepayInstruction getRepayInstructionById(final String id, String type,boolean isWIF) {
		logger.debug("Entering");
		
		RepayInstruction repayInstruction = new RepayInstruction();
		repayInstruction.setId(id);
		
		StringBuilder selectSql = new StringBuilder("Select FinReference, RepayDate, RepayAmount, RepaySchdMethod");
		selectSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		if(isWIF){
			selectSql.append(" From WIFFinRepayInstruction");	
		}else{
			selectSql.append(" From FinRepayInstruction");	
		}
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(repayInstruction);
		RowMapper<RepayInstruction> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(RepayInstruction.class);
		
		try{
			repayInstruction = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			repayInstruction = null;
		}
		logger.debug("Leaving");
		return repayInstruction;
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the FinRepayInstruction or FinRepayInstruction_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Repay Instruction Detail by key FinReference
	 * 
	 * @param Repay Instruction Detail (repayInstruction)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	public void deleteByFinReference(String id,String type,boolean isWIF, long logKey) {
		logger.debug("Entering");
		RepayInstruction repayInstruction = new RepayInstruction();
		repayInstruction.setId(id);
		
		StringBuilder deleteSql = new StringBuilder("Delete ");
		if(isWIF){
			deleteSql.append(" From WIFFinRepayInstruction");	
		}else{
			deleteSql.append(" From FinRepayInstruction");	
		}
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FinReference =:FinReference");
		if(logKey != 0){
			deleteSql.append(" AND LogKey =:LogKey");
		}
		
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(repayInstruction);
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}
	
	
	/**
	 * This method Deletes the Record from the FinRepayInstruction or FinRepayInstruction_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Repay Instruction Detail by key FinReference
	 * 
	 * @param Repay Instruction Detail (repayInstruction)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(RepayInstruction repayInstruction,String type,boolean isWIF) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete ");
		if(isWIF){
			deleteSql.append(" From WIFFinRepayInstruction");	
		}else{
			deleteSql.append(" From FinRepayInstruction");	
		}
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FinReference =:FinReference and RepayDate= :RepayDate");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(repayInstruction);
		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				ErrorDetails errorDetails= getError("41003",repayInstruction.getId() ,repayInstruction.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {};
			}
		}catch(DataAccessException e){
			logger.error("Exception: ", e);
			ErrorDetails errorDetails= getError("41006",repayInstruction.getId() ,repayInstruction.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	/**
	 * This method insert new Records into FinRepayInstruction or FinRepayInstruction_Temp.
	 *
	 * save Repay Instruction Detail 
	 * 
	 * @param Repay Instruction Detail (repayInstruction)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@Override
	public String save(RepayInstruction repayInstruction,String type,boolean isWIF) {
		logger.debug("Entering");
		
		StringBuilder insertSql =new StringBuilder("Insert Into ");
		if(isWIF){
			insertSql.append(" WIFFinRepayInstruction");	
		}else{
			insertSql.append(" FinRepayInstruction");	
		}
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (FinReference, RepayDate, RepayAmount, RepaySchdMethod");
		insertSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:FinReference, :RepayDate, :RepayAmount, :RepaySchdMethod");
		insertSql.append(", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(repayInstruction);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return repayInstruction.getId();
	}
	
	/**
	 * This method insert list of new Records into FinRepayInstruction or FinRepayInstruction_Temp.
	 *
	 * save Repay Instruction Detail 
	 * 
	 * @param Repay Instruction Detail (repayInstruction)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@Override
	public void saveList(List<RepayInstruction> repayInstruction,String type,boolean isWIF) {
		logger.debug("Entering");
		
		StringBuilder insertSql =new StringBuilder("Insert Into ");
		if(isWIF){
			insertSql.append(" WIFFinRepayInstruction");	
		}else{
			insertSql.append(" FinRepayInstruction");	
		}
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (FinReference, RepayDate, RepayAmount, RepaySchdMethod, ");
		if(type.contains("Log")){
			insertSql.append(" LogKey , ");
		}
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:FinReference, :RepayDate, :RepayAmount, :RepaySchdMethod, ");
		if(type.contains("Log")){
			insertSql.append(" :LogKey , ");
		}
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(repayInstruction.toArray());
		try {
			this.namedParameterJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		} catch(Exception e) {
			logger.error("Exception", e);
			throw e;
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Updation of RepaymentInstruction Details after Rate Changes
	 */
	@Override
	public void updateList(List<RepayInstruction> repayInstruction,String type,boolean isWIF) {
		logger.debug("Entering");
		
		StringBuilder	updateSql =new StringBuilder("Update ");
		if(isWIF){
			updateSql.append(" WIFFinRepayInstruction");	
		}else{
			updateSql.append(" FinRepayInstruction");	
		}
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set RepayDate = :RepayDate, " );
		updateSql.append(" RepayAmount = :RepayAmount, RepaySchdMethod= :RepaySchdMethod ");
		updateSql.append(" Where FinReference =:FinReference");
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(repayInstruction.toArray());
		this.namedParameterJdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);
		logger.debug("Leaving");
	}
	
	/**
	 * This method updates the Record FinRepayInstruction or FinRepayInstruction_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Repay Instruction Detail by key FinReference and Version
	 * 
	 * @param Repay Instruction Detail (repayInstruction)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@SuppressWarnings("serial")
	@Override
	public void update(RepayInstruction repayInstruction,String type,boolean isWIF) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update ");
		if(isWIF){
			updateSql.append(" WIFFinRepayInstruction");	
		}else{
			updateSql.append(" FinRepayInstruction");	
		}
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set RepayDate = :RepayDate, RepayAmount = :RepayAmount, RepaySchdMethod= :RepaySchdMethod");
		updateSql.append(", Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where FinReference =:FinReference");
		
		if (!type.endsWith("_Temp")){
			updateSql.append("  AND Version= :Version-1");
		}
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(repayInstruction);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :"+recordCount);
			ErrorDetails errorDetails= getError("41004",repayInstruction.getId() ,repayInstruction.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Fetch the Record  Repay Instruction Detail details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return RepayInstruction
	 */
	@Override
	public List<RepayInstruction> getRepayInstructions(final String id, String type, boolean isWIF) {
		logger.debug("Entering");
		
		RepayInstruction repayInstruction = new RepayInstruction();
		repayInstruction.setId(id);
		
		StringBuilder selectSql = new StringBuilder("Select FinReference, RepayDate, RepayAmount, RepaySchdMethod");
		selectSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		if(isWIF){
			selectSql.append(" From WIFFinRepayInstruction");	
		}else{
			selectSql.append(" From FinRepayInstruction");	
		}
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(repayInstruction);
		RowMapper<RepayInstruction> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(RepayInstruction.class);
		
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}
	
	/**
	 * Fetch the Record  Repay Instruction Detail details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return RepayInstruction
	 */
	@Override
	public List<RepayInstruction> getRepayInstructions(final String id, String type, boolean isWIF, long logKey) {
		logger.debug("Entering");
		
		RepayInstruction repayInstruction = new RepayInstruction();
		repayInstruction.setId(id);
		repayInstruction.setLogKey(logKey);
		
		StringBuilder selectSql = new StringBuilder("Select FinReference, RepayDate, RepayAmount, RepaySchdMethod");
		selectSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		
		if(isWIF){
			selectSql.append(" From WIFFinRepayInstruction");	
		}else{
			selectSql.append(" From FinRepayInstruction");	
		}
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference AND LogKey =:LogKey");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(repayInstruction);
		RowMapper<RepayInstruction> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(RepayInstruction.class);
		
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}
	
	private ErrorDetails  getError(String errorId, String finReference, String userLanguage){
		String[][] parms= new String[2][1];
		parms[1][0] = finReference;
		parms[0][0] = PennantJavaUtil.getLabel("label_FinReference")+ ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId, parms[0],parms[1]), userLanguage);
	}


	@Override
	public List<RepayInstruction> getRepayInstrEOD(String id) {
		logger.debug("Entering");

		RepayInstruction repayInstruction = new RepayInstruction();
		repayInstruction.setId(id);

		StringBuilder selectSql = new StringBuilder("Select FinReference, RepayDate, RepayAmount, RepaySchdMethod");
		selectSql.append(" From FinRepayInstruction  Where FinReference =:FinReference");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(repayInstruction);
		RowMapper<RepayInstruction> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(RepayInstruction.class);

		List<RepayInstruction> repayInstructions = this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
		logger.debug("Leaving");
		return repayInstructions;	}

	
}