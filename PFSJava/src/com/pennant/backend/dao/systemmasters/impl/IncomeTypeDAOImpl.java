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
 * FileName    		:  IncomeTypeDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-05-2011    														*
 *                                                                  						*
 * Modified Date    :  05-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.systemmasters.impl;

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
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.dao.systemmasters.IncomeTypeDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.systemmasters.IncomeType;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>IncomeType model</b> class.<br>
 * 
 */
public class IncomeTypeDAOImpl extends BasisCodeDAO<IncomeType> implements IncomeTypeDAO {

	private static Logger logger = Logger.getLogger(IncomeTypeDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	/**
	 * This method set the Work Flow id based on the module name and return the
	 * new IncomeType
	 * 
	 * @return IncomeType
	 */
	@Override
	public IncomeType getIncomeType() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("IncomeType");
		IncomeType incomeType = new IncomeType();
		if (workFlowDetails != null) {
			incomeType.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return incomeType;
	}

	/**
	 * This method get the module from method getIncomeType() and set the new
	 * record flag as true and return IncomeType()
	 * 
	 * @return IncomeType
	 */
	@Override
	public IncomeType getNewIncomeType() {
		logger.debug("Entering");
		IncomeType incomeType = getIncomeType();
		incomeType.setNewRecord(true);
		logger.debug("Leaving");
		return incomeType;
	}

	/**
	 * Fetch the Record Income Types details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return IncomeType
	 */
	@Override
	public IncomeType getIncomeTypeById(final String id,String incomeExpense,String category, String type) {
		logger.debug("Entering");
		IncomeType incomeType = new IncomeType();
		incomeType.setId(id);
		incomeType.setCategory(category);
		incomeType.setIncomeExpense(incomeExpense);
		StringBuilder selectSql = new StringBuilder();

		selectSql.append("SELECT IncomeExpense,Category,IncomeTypeCode, IncomeTypeDesc,Margin ,IncomeTypeIsActive, " );
		if(type.contains("View")){
			selectSql.append("lovDescCategoryName,");
		}
		selectSql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId" );
		selectSql.append(" FROM  BMTIncomeTypes");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where IncomeTypeCode =:IncomeTypeCode and IncomeExpense=:IncomeExpense and Category=:Category") ;
				
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(incomeType);
		RowMapper<IncomeType> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(IncomeType.class);

		try {
			incomeType = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(e);
			incomeType = null;
		}
		logger.debug("Leaving");
		return incomeType;
	}
	
	/**
	 * Fetch the Record Income Types details 
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return IncomeType
	 */
	@Override
	public List<IncomeType> getIncomeTypeList() {
		logger.debug("Entering");
		
		StringBuilder selectSql = new StringBuilder();
		selectSql.append("SELECT IncomeExpense,Category,IncomeTypeCode, IncomeTypeDesc,Margin ,IncomeTypeIsActive, lovDescCategoryName " );
		selectSql.append(" FROM  BMTIncomeTypes_AView");
				
		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<IncomeType> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(IncomeType.class);

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.getJdbcOperations().query(selectSql.toString(), typeRowMapper);
	}

	/**
	 * This method initialize the Record.
	 * 
	 * @param IncomeType
	 *            (incomeType)
	 * @return IncomeType
	 */
	@Override
	public void initialize(IncomeType incomeType) {
		super.initialize(incomeType);
	}

	/**
	 * This method refresh the Record.
	 * 
	 * @param IncomeType
	 *            (incomeType)
	 * @return void
	 */
	@Override
	public void refresh(IncomeType incomeType) {

	}

	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the BMTIncomeTypes or
	 * BMTIncomeTypes_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete Income Types by key
	 * IncomeTypeCode
	 * 
	 * @param Income
	 *            Types (incomeType)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(IncomeType incomeType, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder deleteSql = new StringBuilder();

		deleteSql.append("Delete From BMTIncomeTypes");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where IncomeTypeCode =:IncomeTypeCode");
		
		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(incomeType);

		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(),beanParameters);

			if (recordCount <= 0) {
				ErrorDetails errorDetails= getError("41004", incomeType.getIncomeTypeCode(), incomeType.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {
				};
			}
		} catch (DataAccessException e) {
			logger.debug("Error in delete Method");
			logger.error(e);
			ErrorDetails errorDetails= getError("41006", incomeType.getIncomeTypeCode(), incomeType.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into BMTIncomeTypes or
	 * BMTIncomeTypes_Temp.
	 * 
	 * save Income Types
	 * 
	 * @param Income
	 *            Types (incomeType)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public String save(IncomeType incomeType, String type) {
		logger.debug("Entering");
		StringBuilder insertSql = new StringBuilder();

		insertSql.append("Insert Into BMTIncomeTypes");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" ( IncomeExpense, Category,IncomeTypeCode, IncomeTypeDesc,Margin, IncomeTypeIsActive," );
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId," );
		insertSql.append(" RecordType, WorkflowId)");
		insertSql.append(" Values(:IncomeExpense,:Category,:IncomeTypeCode, :IncomeTypeDesc,:Margin, :IncomeTypeIsActive, " );
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, ");
		insertSql.append(" :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(incomeType);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return incomeType.getId();
	}

	/**
	 * This method updates the Record BMTIncomeTypes or BMTIncomeTypes_Temp. if
	 * Record not updated then throws DataAccessException with error 41004.
	 * update Income Types by key IncomeTypeCode and Version
	 * 
	 * @param Income
	 *            Types (incomeType)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	@Override
	public void update(IncomeType incomeType, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		StringBuilder updateSql = new StringBuilder();

		updateSql.append("Update BMTIncomeTypes");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set  IncomeTypeDesc = :IncomeTypeDesc, IncomeTypeIsActive = :IncomeTypeIsActive,");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, " );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode,NextRoleCode = :NextRoleCode, TaskId = :TaskId," );
		updateSql.append(" NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId" );
		updateSql.append(" Where IncomeTypeCode =:IncomeTypeCode and IncomeExpense=:IncomeExpense and Category=:Category");
		
		if (!type.endsWith("_TEMP")){
			updateSql.append(" AND Version= :Version-1");
		}
		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(incomeType);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error in Update Method Count :" + recordCount);

			ErrorDetails errorDetails= getError("41003", incomeType.getIncomeTypeCode(), incomeType.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
		}
		logger.debug("Leaving");
	}
	
	/**
	 * This method for getting the error details
	 * @param errorId (String)
	 * @param Id (String)
	 * @param userLanguage (String)
	 * @return ErrorDetails
	 */
	private ErrorDetails  getError(String errorId, String incomeTypeCode,String userLanguage){
		String[][] parms= new String[2][2]; 
		parms[1][0] = incomeTypeCode;
		parms[0][0] = PennantJavaUtil.getLabel("label_IncomeTypeCode")+ ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId, parms[0],parms[1]), userLanguage);
	}
}