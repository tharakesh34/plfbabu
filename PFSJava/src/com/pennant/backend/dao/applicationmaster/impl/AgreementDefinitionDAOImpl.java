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
 * FileName    		:  AgreementDefinitionDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  23-11-2011    														*
 *                                                                  						*
 * Modified Date    :  23-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 23-11-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.applicationmaster.impl;


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
import com.pennant.backend.dao.applicationmaster.AgreementDefinitionDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.applicationmaster.AgreementDefinition;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>AgreementDefinition model</b> class.<br>
 * 
 */
public class AgreementDefinitionDAOImpl extends BasisNextidDaoImpl<AgreementDefinition> implements AgreementDefinitionDAO {

	private static Logger logger = Logger.getLogger(AgreementDefinitionDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	/**
	 * This method set the Work Flow id based on the module name and return the new AgreementDefinition 
	 * @return AgreementDefinition
	 */
	@Override
	public AgreementDefinition getAgreementDefinition() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("AgreementDefinition");
		AgreementDefinition agreementDefinition= new AgreementDefinition();
		if (workFlowDetails!=null){
			agreementDefinition.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return agreementDefinition;
	}


	/**
	 * This method get the module from method getAgreementDefinition() and set the new record flag as true and return AgreementDefinition()   
	 * @return AgreementDefinition
	 */
	@Override
	public AgreementDefinition getNewAgreementDefinition() {
		logger.debug("Entering");
		AgreementDefinition agreementDefinition = getAgreementDefinition();
		agreementDefinition.setNewRecord(true);
		logger.debug("Leaving");
		return agreementDefinition;
	}

	/**
	 * Fetch the Record  Agreement Definition details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return AgreementDefinition
	 */
	@Override
	public AgreementDefinition getAgreementDefinitionById(final long id, String type) {
		logger.debug("Entering");
		AgreementDefinition agreementDefinition = new AgreementDefinition();
		
		agreementDefinition.setId(id);
		
		StringBuilder selectSql = new StringBuilder("Select AggId, AggCode, AggName, " );
		selectSql.append(" AggDesc, AggReportName, AggReportPath, AggIsActive , Aggtype, AggImage, ");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, " );
		selectSql.append(" NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From BMTAggrementDef");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where AggId =:AggId");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(agreementDefinition);
		RowMapper<AgreementDefinition> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(
				AgreementDefinition.class);
		
		try{
			agreementDefinition = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(),
					beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			agreementDefinition = null;
		}
		logger.debug("Leaving");
		return agreementDefinition;
	}

	/**
	 * Fetch the Record  Agreement Definition details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return AgreementDefinition
	 */
	@Override
	public AgreementDefinition getAgreementDefinitionByCode(final String aggCode, String type) {
		logger.debug("Entering");
		
		AgreementDefinition agreementDefinition = new AgreementDefinition();
		agreementDefinition.setAggCode(aggCode);
		
		StringBuilder selectSql = new StringBuilder("Select AggId, AggCode, AggName, AggDesc, " );
		selectSql.append(" AggReportName, AggReportPath, AggIsActive, Aggtype, AggImage, ");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, " );
		selectSql.append(" NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From BMTAggrementDef");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where AggCode =:AggCode");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(agreementDefinition);
		RowMapper<AgreementDefinition> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(AgreementDefinition.class);
		
		try{
			agreementDefinition = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			agreementDefinition = null;
		}
		logger.debug("Leaving");
		return agreementDefinition;
	}

	/**
	 * This method initialize the Record.
	 * @param AgreementDefinition (agreementDefinition)
 	 * @return AgreementDefinition
	 */
	@Override
	public void initialize(AgreementDefinition agreementDefinition) {
		super.initialize(agreementDefinition);
	}

	/**
	 * This method refresh the Record.
	 * @param AgreementDefinition (agreementDefinition)
 	 * @return void
	 */
	@Override
	public void refresh(AgreementDefinition agreementDefinition) {
		
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the BMTAggrementDef or BMTAggrementDef_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Agreement Definition by key AggCode
	 * 
	 * @param Agreement Definition (agreementDefinition)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(AgreementDefinition agreementDefinition,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete From BMTAggrementDef");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where AggCode =:AggCode");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(agreementDefinition);
		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				ErrorDetails errorDetails= getError("41003",agreementDefinition.getAggCode() ,
						agreementDefinition.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {};
			}
		}catch(DataAccessException e){
			logger.error(e);
			ErrorDetails errorDetails= getError("41006",agreementDefinition.getAggCode() ,
					agreementDefinition.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	/**
	 * This method insert new Records into BMTAggrementDef or BMTAggrementDef_Temp.
	 *
	 * save Agreement Definition 
	 * 
	 * @param Agreement Definition (agreementDefinition)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public long save(AgreementDefinition agreementDefinition,String type) {
		logger.debug("Entering");
		
		if (agreementDefinition.getId()==Long.MIN_VALUE){
			agreementDefinition.setId(getNextidviewDAO().getNextId("SeqBMTAggrementDef"));
			logger.debug("get NextID:"+agreementDefinition.getId());
		}
		
		StringBuilder insertSql =new StringBuilder("Insert Into BMTAggrementDef");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (AggId, AggCode, AggName, AggDesc, AggReportName, AggReportPath, " );
		insertSql.append(" AggIsActive , Aggtype, AggImage, ");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, " );
		insertSql.append(" NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:AggId, :AggCode, :AggName, :AggDesc, :AggReportName, ");
		insertSql.append(" :AggReportPath, :AggIsActive, :Aggtype, :AggImage, ");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, " );
		insertSql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(agreementDefinition);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return agreementDefinition.getId();
	}
	
	/**
	 * This method updates the Record BMTAggrementDef or BMTAggrementDef_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Agreement Definition by key AggCode and Version
	 * 
	 * @param Agreement Definition (agreementDefinition)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	@Override
	public void update(AgreementDefinition agreementDefinition,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update BMTAggrementDef");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set AggCode = :AggCode, AggName = :AggName, AggDesc = :AggDesc, " );
		updateSql.append(" AggReportName = :AggReportName, AggReportPath = :AggReportPath, " );
		updateSql.append(" AggIsActive = :AggIsActive , Aggtype = :Aggtype, AggImage = :AggImage,  ");
		updateSql.append(" Version = :Version, LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, " );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, " );
		updateSql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, " );
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where AggId =:AggId");
		
		if (!type.endsWith("_TEMP")){
			updateSql.append("  AND Version= :Version-1");
		}
		
		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(agreementDefinition);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :"+recordCount);
			ErrorDetails errorDetails= getError("41004",agreementDefinition.getAggCode() ,
					agreementDefinition.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	private ErrorDetails  getError(String errorId, String aggCode, String userLanguage){
		String[][] parms= new String[2][1];
		parms[1][0] = aggCode;
		parms[0][0] = PennantJavaUtil.getLabel("label_AggCode")+ ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, 
				errorId, parms[0],parms[1]), userLanguage);
	}

	
}