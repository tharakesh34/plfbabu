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
 * FileName    		:  OtherBankFinanceTypeDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  03-04-2015    														*
 *                                                                  						*
 * Modified Date    :  03-04-2015    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-04-2015       Pennant	                 0.1                                            * 
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
import com.pennant.backend.dao.applicationmaster.OtherBankFinanceTypeDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.applicationmaster.OtherBankFinanceType;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * DAO methods implementation for the <b>OtherBankFinanceType model</b> class.<br>
 * 
 */
public class OtherBankFinanceTypeDAOImpl extends BasisCodeDAO<OtherBankFinanceType> implements OtherBankFinanceTypeDAO {
	private static Logger logger = Logger.getLogger(OtherBankFinanceTypeDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	public OtherBankFinanceTypeDAOImpl(){
		super();
	}
	

	/**
	 * Fetch the Record  Other Bank Finance Type details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return OtherBankFinanceType
	 */
	@Override
	public OtherBankFinanceType getOtherBankFinanceTypeById(final String id, String type) {
		logger.debug("Entering");
		OtherBankFinanceType otherBankFinanceType = new OtherBankFinanceType();
		
		otherBankFinanceType.setId(id);
		
		StringBuilder selectSql = new StringBuilder("Select FinType, FinTypeDesc, Active");
		selectSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append("");
		}
		selectSql.append(" From OtherBankFinanceType");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinType =:FinType");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(otherBankFinanceType);
		RowMapper<OtherBankFinanceType> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(OtherBankFinanceType.class);
		
		try{
			otherBankFinanceType = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			otherBankFinanceType = null;
		}
		logger.debug("Leaving");
		return otherBankFinanceType;
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the OtherBankFinanceTypes or OtherBankFinanceTypes_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Other Bank Finance Type by key FinType
	 * 
	 * @param Other Bank Finance Type (otherBankFinanceType)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(OtherBankFinanceType otherBankFinanceType, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete From OtherBankFinanceType");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FinType =:FinType");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(otherBankFinanceType);
		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				ErrorDetails errorDetails= getError("41003",otherBankFinanceType.getId() ,otherBankFinanceType.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {
				};
			}
		} catch (DataAccessException e) {
			logger.error("Exception: ", e);
			ErrorDetails errorDetails= getError("41006",otherBankFinanceType.getId() ,otherBankFinanceType.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving");
	}
	
	/**
	 * This method insert new Records into OtherBankFinanceTypes or OtherBankFinanceTypes_Temp.
	 *
	 * save Other Bank Finance Type 
	 * 
	 * @param Other Bank Finance Type (otherBankFinanceType)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@Override
	public String save(OtherBankFinanceType otherBankFinanceType,String type) {
		logger.debug("Entering");
		
		StringBuilder insertSql =new StringBuilder("Insert Into OtherBankFinanceType");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (FinType, FinTypeDesc, Active");
		insertSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:FinType, :FinTypeDesc, :Active");
		insertSql.append(", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(otherBankFinanceType);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return otherBankFinanceType.getId();
	}
	
	/**
	 * This method updates the Record OtherBankFinanceTypes or OtherBankFinanceTypes_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Other Bank Finance Type by key FinType and Version
	 * 
	 * @param Other Bank Finance Type (otherBankFinanceType)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	@Override
	public void update(OtherBankFinanceType otherBankFinanceType, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update OtherBankFinanceType");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set FinType = :FinType, FinTypeDesc = :FinTypeDesc, Active = :Active");
		updateSql.append(", Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where FinType =:FinType");
		
		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(otherBankFinanceType);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :"+recordCount);
			ErrorDetails errorDetails= getError("41004",otherBankFinanceType.getId() ,otherBankFinanceType.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving");
	}
	
	private ErrorDetails  getError(String errorId, String finType, String userLanguage){
		String[][] parms= new String[2][1];
		parms[1][0] = finType;
		parms[0][0] = PennantJavaUtil.getLabel("label_FinType")+ ":" + parms[1][0];

		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId, parms[0],parms[1]), userLanguage);
	}
}