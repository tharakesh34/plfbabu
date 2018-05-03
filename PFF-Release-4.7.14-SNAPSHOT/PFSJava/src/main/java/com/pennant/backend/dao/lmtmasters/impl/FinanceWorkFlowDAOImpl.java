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
 * FileName    		:  FinanceWorkFlowDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  19-11-2011    														*
 *                                                                  						*
 * Modified Date    :  19-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 19-11-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.lmtmasters.impl;


import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.dao.lmtmasters.FinanceWorkFlowDAO;
import com.pennant.backend.model.configuration.VASConfiguration;
import com.pennant.backend.model.lmtmasters.FinanceWorkFlow;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>FinanceWorkFlow model</b> class.<br>
 * 
 */
public class FinanceWorkFlowDAOImpl extends BasisCodeDAO<FinanceWorkFlow> implements FinanceWorkFlowDAO {

	private static Logger logger = Logger.getLogger(FinanceWorkFlowDAOImpl.class);
	
	public FinanceWorkFlowDAOImpl() {
		super();
	}
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	/**
	 * Fetch the Record  Finance Work Flow Definition details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return FinanceWorkFlow
	 */
	@Override
	public FinanceWorkFlow getFinanceWorkFlowById(final String finType, String finEvent, String moduleName, String type) {
		logger.debug("Entering");

		StringBuilder selectSql = null;
		MapSqlParameterSource source = null;

		selectSql = new StringBuilder("Select FinType, FinEvent, ScreenCode, WorkFlowType,ModuleName");
		selectSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(",lovDescFinTypeName,lovDescWorkFlowTypeName,LovDescWorkFlowRolesName, lovDescProductCodeName, LovDescFirstTaskOwner ");
			selectSql.append(",lovDescFacilityTypeName ,ProductCategory, CollateralDesc, VasProductDesc,CommitmentTypeDesc ");
		}
		selectSql.append(" From LMTFinanceWorkFlowDef");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinType =:FinType AND FinEvent=:FinEvent AND ModuleName=:ModuleName ");
		logger.debug("selectSql: " + selectSql.toString());

		source = new MapSqlParameterSource();
		source.addValue("FinType", finType);
		source.addValue("FinEvent", finEvent);
		source.addValue("ModuleName", moduleName.toUpperCase());

		RowMapper<FinanceWorkFlow> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceWorkFlow.class);
		try {
			return this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
		} finally {
			source = null;
			selectSql = null;
		}
		logger.debug("Leaving");
		return null;
	}
	
	/**
	 * Fetch the Workflow Type from the Defined prameters
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return WorkflowType
	 */
	@Override
	public String getFinanceWorkFlowType(final String finType, String finEvent, String moduleName, String type) {
		logger.debug("Entering");
		
		StringBuilder selectSql = null;
		MapSqlParameterSource source = null;
		
		selectSql = new StringBuilder();
		selectSql.append(" Select WorkFlowType From LMTFinanceWorkFlowDef");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinType =:FinType AND FinEvent=:FinEvent AND ModuleName=:ModuleName ");
		logger.debug("selectSql: " + selectSql.toString());
		
		source = new MapSqlParameterSource();
		source.addValue("FinType", finType);
		source.addValue("FinEvent", finEvent);
		source.addValue("ModuleName", moduleName);
		
		String workflowType = null;
		try {
			workflowType = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source, String.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			workflowType = null;
		} finally {
			source = null;
			selectSql = null;
		}
		logger.debug("Leaving");
		return workflowType;
	}
	
	/**
	 * Fetch the Record  Finance Work Flow Definition details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return FinanceWorkFlow
	 */
	@Override
	public List<FinanceWorkFlow> getFinanceWorkFlowListById(final String id,String moduleName, String type) {
		logger.debug("Entering");
		FinanceWorkFlow financeWorkFlow = new FinanceWorkFlow();
		financeWorkFlow.setId(id);
		financeWorkFlow.setModuleName(moduleName);
		
		
		StringBuilder selectSql = new StringBuilder("Select FinType, FinEvent, ScreenCode, WorkFlowType,ModuleName");
		selectSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(",lovDescFinTypeName,lovDescWorkFlowTypeName,LovDescWorkFlowRolesName, lovDescProductCodeName, LovDescFirstTaskOwner ");
			selectSql.append(",lovDescFacilityTypeName ");
		}
		selectSql.append(" From LMTFinanceWorkFlowDef");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinType =:FinType AND ModuleName=:ModuleName");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeWorkFlow);
		RowMapper<FinanceWorkFlow> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceWorkFlow.class);
		
		List<FinanceWorkFlow> returnList = null;
		try{
			returnList = this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			returnList = new ArrayList<>();
		}
		logger.debug("Leaving");
		return returnList;
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the LMTFinanceWorkFlowDef or LMTFinanceWorkFlowDef_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Finance Work Flow Definition by key FinType
	 * 
	 * @param Finance Work Flow Definition (financeWorkFlow)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(FinanceWorkFlow financeWorkFlow,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete From LMTFinanceWorkFlowDef");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FinType =:FinType AND FinEvent=:FinEvent AND ModuleName=:ModuleName");
		
		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeWorkFlow);
		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		}catch(DataAccessException e){
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}
	
	/**
	 * This method insert new Records into LMTFinanceWorkFlowDef or LMTFinanceWorkFlowDef_Temp.
	 *
	 * save Finance Work Flow Definition 
	 * 
	 * @param Finance Work Flow Definition (financeWorkFlow)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@Override
	public String save(FinanceWorkFlow financeWorkFlow,String type) {
		logger.debug("Entering");
		
		StringBuilder insertSql =new StringBuilder("Insert Into LMTFinanceWorkFlowDef");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (FinType, FinEvent, ScreenCode, WorkFlowType,ModuleName");
		insertSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId,");
		insertSql.append(" 	RecordType, WorkflowId)");
		insertSql.append(" Values(:FinType, :FinEvent, :ScreenCode, :WorkFlowType,:ModuleName");
		insertSql.append(", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, ");
		insertSql.append(" :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeWorkFlow);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return financeWorkFlow.getId();
	}
	
	/**
	 * Method to insert List of Workflow Details
	 */
	public void saveList(List<FinanceWorkFlow> financeWorkFlowList, String type) {
		logger.debug("Entering");
		
		StringBuilder insertSql =new StringBuilder("Insert Into LMTFinanceWorkFlowDef");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (FinType, FinEvent, ScreenCode, WorkFlowType,ModuleName, ");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId,");
		insertSql.append(" RecordType, WorkflowId)");
		insertSql.append(" Values(:FinType, :FinEvent, :ScreenCode, :WorkFlowType,:ModuleName, ");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, ");
		insertSql.append(" :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(financeWorkFlowList.toArray());
		this.namedParameterJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
	}
	
	/**
	 * This method updates the Record LMTFinanceWorkFlowDef or LMTFinanceWorkFlowDef_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Finance Work Flow Definition by key FinType and Version
	 * 
	 * @param Finance Work Flow Definition (financeWorkFlow)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(FinanceWorkFlow financeWorkFlow,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder	updateSql =new StringBuilder("Update LMTFinanceWorkFlowDef");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set ScreenCode = :ScreenCode, WorkFlowType = :WorkFlowType, ModuleName=:ModuleName");
		updateSql.append(", Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, ");
		updateSql.append(" RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, ");
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId ");
		updateSql.append(" Where FinType =:FinType AND FinEvent =:FinEvent  AND ModuleName=:ModuleName");
		if (!type.endsWith("_Temp")){
			updateSql.append("  AND Version= :Version-1");
		}
		
		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeWorkFlow);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}
	
	
	@Override
	public List<String> getFinanceWorkFlowRoles(String module,String finEvent){
		logger.debug("Entering");
		
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ModuleName", module);
		source.addValue("FinEvent", finEvent);
		StringBuilder selectSql = new StringBuilder();
		
		selectSql.append(" SELECT DISTINCT WD.WorkFlowRoles ");
		selectSql.append(" FROM  LMTFinanceWorkFlowDef FWD INNER JOIN ");
		selectSql.append(" WorkFlowDetails WD ON FWD.WorkFlowType = WD.WorkFlowType ");
		selectSql.append(" Where FWD.ModuleName=:ModuleName and FWD.FinEvent =:FinEvent ");
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.queryForList(selectSql.toString(),source,String.class);
	}
	

	@Override
	public boolean isWorkflowExists(String finType, String moduleName) {
		logger.debug("Entering");
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinType", finType);
		source.addValue("ModuleName", moduleName);
		
		StringBuilder selectSql = new StringBuilder("SELECT COUNT(FinType)");
		selectSql.append(" From LMTFinanceWorkFlowDef_View ");
		selectSql.append(" Where FinType=:FinType AND ModuleName=:ModuleName");
		
		logger.debug("selectSql: " + selectSql.toString());
		int rcdCount =  this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
		
		logger.debug("Leaving");
		return rcdCount > 0 ? true : false;
	}

	@Override
	public int getVASProductCode(String finType, String type) {
		logger.debug("Entering");
		VASConfiguration vASProductCode = new VASConfiguration();
		vASProductCode.setProductCode(finType);
		int count;

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*)");
		selectSql.append(" From VASStructure");
		selectSql.append(" Where ProductCode =:ProductCode ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(vASProductCode);

		try {
			 count= this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters,Integer.class);
		} catch(EmptyResultDataAccessException dae) {
			logger.debug("Exception: ", dae);
			return 0;
		}
		logger.debug("Leaving");
		return count;
	}

}