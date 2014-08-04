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
 * FileName    		:  PenaltyCodeDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-05-2011    														*
 *                                                                  						*
 * Modified Date    :  26-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.rmtmasters.impl;

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
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.dao.rmtmasters.PenaltyCodeDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.rmtmasters.PenaltyCode;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>PenaltyCode model</b> class.<br>
 * 
 */
public class PenaltyCodeDAOImpl extends BasisCodeDAO<PenaltyCode> implements PenaltyCodeDAO {

	private static Logger logger = Logger.getLogger(PenaltyCodeDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	/**
	 * This method set the Work Flow id based on the module name and return the new PenaltyCode 
	 * @return PenaltyCode
	 */
	@Override
	public PenaltyCode getPenaltyCode() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("PenaltyCode");
		PenaltyCode penaltyCode= new PenaltyCode();
		if (workFlowDetails!=null){
			penaltyCode.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return penaltyCode;
	}

	/**
	 * This method get the module from method getPenaltyCode() and set the new
	 * record flag as true and return PenaltyCode()
	 * 
	 * @return PenaltyCode
	 */
	@Override
	public PenaltyCode getNewPenaltyCode() {
		logger.debug("Entering");
		PenaltyCode penaltyCode = getPenaltyCode();
		penaltyCode.setNewRecord(true);
		logger.debug("Leaving");
		return penaltyCode;
	}

	/**
	 * Fetch the Record  Penalty Codes details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return PenaltyCode
	 */
	@Override
	public PenaltyCode getPenaltyCodeById(final String id, String type) {
		logger.debug("Entering");
		PenaltyCode penaltyCode = new PenaltyCode();
		penaltyCode.setId(id);

		StringBuilder selectSql = new StringBuilder("Select PenaltyType, PenaltyDesc, PenaltyIsActive,");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		selectSql.append(" TaskId ,NextTaskId, RecordType, WorkflowId" );
		selectSql.append(" From RMTPenaltyCodes"+ StringUtils.trimToEmpty(type) );
		selectSql.append(" Where PenaltyType =:PenaltyType");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(penaltyCode);
		RowMapper<PenaltyCode> typeRowMapper = ParameterizedBeanPropertyRowMapper
							.newInstance(PenaltyCode.class);

		try{
			penaltyCode = this.namedParameterJdbcTemplate.queryForObject(
					selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.error(e);
			penaltyCode = null;
		}
		logger.debug("Leaving");
		return penaltyCode;
	}

	/**
	 * This method initialize the Record.
	 * @param PenaltyCode (penaltyCode)
	 * @return PenaltyCode
	 */
	@Override
	public void initialize(PenaltyCode penaltyCode) {
		super.initialize(penaltyCode);
	}

	/**
	 * This method refresh the Record.
	 * @param PenaltyCode (penaltyCode)
	 * @return void
	 */
	@Override
	public void refresh(PenaltyCode penaltyCode) {

	}

	/**
	 * @param dataSource the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the RMTPenaltyCodes or RMTPenaltyCodes_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Penalty Codes by key PenaltyType
	 * 
	 * @param Penalty Codes (penaltyCode)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(PenaltyCode penaltyCode,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder(" Delete From RMTPenaltyCodes" );
		deleteSql.append(StringUtils.trimToEmpty(type) );
		deleteSql.append(" Where PenaltyType =:PenaltyType");
		
		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(penaltyCode);

		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);

			if (recordCount <= 0) {
				ErrorDetails errorDetails= getError("41003",penaltyCode.getPenaltyType(),
						penaltyCode.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {};
			}
		}catch(DataAccessException e){
			logger.error(e);
			ErrorDetails errorDetails=getError("41006",penaltyCode.getPenaltyType(),
					penaltyCode.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into RMTPenaltyCodes or RMTPenaltyCodes_Temp.
	 *
	 * save Penalty Codes 
	 * 
	 * @param Penalty Codes (penaltyCode)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public String save(PenaltyCode penaltyCode,String type) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into RMTPenaltyCodes" );
		insertSql.append(StringUtils.trimToEmpty(type) );
		insertSql.append(" (PenaltyType, PenaltyDesc, PenaltyIsActive,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode," );
		insertSql.append(" NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)" );
		insertSql.append(" Values(:PenaltyType, :PenaltyDesc, :PenaltyIsActive," );
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode,");
		insertSql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(penaltyCode);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return penaltyCode.getId();
	}

	/**
	 * This method updates the Record RMTPenaltyCodes or RMTPenaltyCodes_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Penalty Codes by key PenaltyType and Version
	 * 
	 * @param Penalty Codes (penaltyCode)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	@Override
	public void update(PenaltyCode penaltyCode,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder updateSql = new StringBuilder("Update RMTPenaltyCodes" );
		updateSql.append(StringUtils.trimToEmpty(type) ); 
		updateSql.append(" Set PenaltyType = :PenaltyType, PenaltyDesc = :PenaltyDesc," );
		updateSql.append(" PenaltyIsActive = :PenaltyIsActive," );
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn," );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode," );
		updateSql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId," );
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId" );
		updateSql.append(" Where PenaltyType =:PenaltyType");

		if (!type.endsWith("_TEMP")){
			updateSql.append(" AND Version= :Version-1");
		}

		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(penaltyCode);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error in Update Method Count :"+recordCount);
			ErrorDetails errorDetails= getError("41004",penaltyCode.getPenaltyType(),
					penaltyCode.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	private ErrorDetails  getError(String errorId,String penaltyType, String userLanguage){
		String[][] parms= new String[2][1]; 
		
		parms[1][0] = penaltyType;
		parms[0][0] = PennantJavaUtil.getLabel("label_PenaltyType")+ ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, 
				errorId, parms[0],parms[1]), userLanguage);
	}
	
}