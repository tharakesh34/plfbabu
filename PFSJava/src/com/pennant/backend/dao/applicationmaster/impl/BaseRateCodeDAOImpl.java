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
 * FileName    		:  BaseRateCodeDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  03-05-2011    														*
 *                                                                  						*
 * Modified Date    :  03-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-05-2011       Pennant	                 0.1                                            * 
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
import com.pennant.backend.dao.applicationmaster.BaseRateCodeDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.applicationmaster.BaseRateCode;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>BaseRateCode model</b> class.<br>
 */
public class BaseRateCodeDAOImpl extends BasisCodeDAO<BaseRateCode> implements
		BaseRateCodeDAO {

	private static Logger logger = Logger.getLogger(BaseRateCodeDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	/**
	 * This method set the Work Flow id based on the module name and return the
	 * new BaseRateCode
	 * 
	 * @return BaseRateCode
	 */
	@Override
	public BaseRateCode getBaseRateCode() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("BaseRateCode");
		BaseRateCode baseRateCode= new BaseRateCode();
		if (workFlowDetails!=null){
			baseRateCode.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return baseRateCode;
	}

	/**
	 * This method get the module from method getBaseRateCode() and set the new
	 * record flag as true and return BaseRateCode()
	 * 
	 * @return BaseRateCode
	 */
	@Override
	public BaseRateCode getNewBaseRateCode() {
		logger.debug("Entering");
		BaseRateCode baseRateCode = getBaseRateCode();
		baseRateCode.setNewRecord(true);
		logger.debug("Leaving");
		return baseRateCode;
	}

	/**
	 * Fetch the Record  Base Rate Codes details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return BaseRateCode
	 */
	@Override
	public BaseRateCode getBaseRateCodeById(final String id, String type) {
		logger.debug("Entering");
		BaseRateCode baseRateCode = new BaseRateCode();
		baseRateCode.setId(id);
		
		StringBuilder selectSql = new StringBuilder("Select BRType, BRTypeDesc," );
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode," );
		selectSql.append(" NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId" );
		selectSql.append(" From RMTBaseRateCodes"+ StringUtils.trimToEmpty(type) );
		selectSql.append(" Where BRType =:BRType");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(
				baseRateCode);
		RowMapper<BaseRateCode> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(BaseRateCode.class);
		
		try{
			baseRateCode = this.namedParameterJdbcTemplate.queryForObject(
					selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			baseRateCode = null;
		}
		logger.debug("Leaving");
		return baseRateCode;
	}
	
	/**
	 * This method initialize the Record.
	 * @param BaseRateCode (baseRateCode)
 	 * @return BaseRateCode
	 */
	@Override
	public void initialize(BaseRateCode baseRateCode) {
		super.initialize(baseRateCode);
	}
	
	/**
	 * This method refresh the Record.
	 * @param BaseRateCode (baseRateCode)
 	 * @return void
	 */
	@Override
	public void refresh(BaseRateCode baseRateCode) {
		
	}
	
	/**
	 * @param dataSource the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the RMTBaseRateCodes or RMTBaseRateCodes_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Base Rate Codes by key BRType
	 * 
	 * @param Base Rate Codes (baseRateCode)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(BaseRateCode baseRateCode,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete From RMTBaseRateCodes" );
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where BRType =:BRType");
		
		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(baseRateCode);
		
		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			
			if (recordCount <= 0) {
				ErrorDetails errorDetails=  getError("41003", baseRateCode.getBRType(),
						baseRateCode.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {};
			}
		}catch(DataAccessException e){
			logger.error(e);
			ErrorDetails errorDetails=  getError("41006", baseRateCode.getBRType(),
					baseRateCode.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	/**
	 * This method insert new Records into RMTBaseRateCodes or RMTBaseRateCodes_Temp.
	 *
	 * save Base Rate Codes 
	 * 
	 * @param Base Rate Codes (baseRateCode)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@Override
	public String save(BaseRateCode baseRateCode,String type) {
		logger.debug("Entering");
		
		StringBuilder insertSql = new StringBuilder("Insert Into RMTBaseRateCodes");
		insertSql.append(StringUtils.trimToEmpty(type) );
		insertSql.append(" (BRType, BRTypeDesc," );
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:BRType, :BRTypeDesc, :Version , :LastMntBy, :LastMntOn,:RecordStatus," );
		insertSql.append(" :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(
				baseRateCode);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		
		logger.debug("Leaving");
		return baseRateCode.getId();
	}
	
	/**
	 * This method updates the Record RMTBaseRateCodes or RMTBaseRateCodes_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Base Rate Codes by key BRType and Version
	 * 
	 * @param Base Rate Codes (baseRateCode)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	@Override
	public void update(BaseRateCode baseRateCode,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		
		StringBuilder updateSql = new StringBuilder("Update RMTBaseRateCodes");
		updateSql.append(StringUtils.trimToEmpty(type) ); 
		updateSql.append(" Set BRType = :BRType, BRTypeDesc = :BRTypeDesc,");
		updateSql.append(" Version = :Version ,LastMntBy = :LastMntBy, LastMntOn = :LastMntOn," );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode," );
		updateSql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId" );
		updateSql.append(" Where BRType =:BRType ");
		
		if (!type.endsWith("_TEMP")){
			updateSql.append(" AND Version= :Version-1");
		}
		
		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(
				baseRateCode);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			logger.debug("Error in Update Method Count :"+recordCount);
			ErrorDetails errorDetails= getError("41004", baseRateCode.getBRType(),
					baseRateCode.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	private ErrorDetails  getError(String errorId,String baseRateType, String userLanguage){
		String[][] parms= new String[2][1]; 
		
		parms[1][0] = baseRateType;
		parms[0][0] = PennantJavaUtil.getLabel("label_BRType")+ ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, 
				errorId, parms[0],parms[1]), userLanguage);
	}

}