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
 * FileName    		:  DeviationHeaderDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  22-06-2015    														*
 *                                                                  						*
 * Modified Date    :  22-06-2015    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 22-06-2015       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.solutionfactory.impl;


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

import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.dao.solutionfactory.DeviationHeaderDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.solutionfactory.DeviationHeader;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pff.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>DeviationHeader model</b> class.<br>
 * 
 */

public class DeviationHeaderDAOImpl extends BasisNextidDaoImpl<DeviationHeader> implements DeviationHeaderDAO {

	private static Logger logger = Logger.getLogger(DeviationHeaderDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	public DeviationHeaderDAOImpl() {
		super();
	}
	
	/**
	 * This method set the Work Flow id based on the module name and return the new DeviationHeader 
	 * @return DeviationHeader
	 */

	@Override
	public DeviationHeader getDeviationHeader() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("DeviationHeader");
		DeviationHeader deviationHeader= new DeviationHeader();
		if (workFlowDetails!=null){
			deviationHeader.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return deviationHeader;
	}


	/**
	 * This method get the module from method getDeviationHeader() and set the new record flag as true and return DeviationHeader()   
	 * @return DeviationHeader
	 */


	@Override
	public DeviationHeader getNewDeviationHeader() {
		logger.debug("Entering");
		DeviationHeader deviationHeader = getDeviationHeader();
		deviationHeader.setNewRecord(true);
		logger.debug("Leaving");
		return deviationHeader;
	}

	/**
	 * Fetch the Record  Deviation Header details by key field
	 * 
	 * @param id (int)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return DeviationHeader
	 */
	@Override
	public DeviationHeader getDeviationHeaderById(final long id, String type) {
		logger.debug("Entering");
		DeviationHeader deviationHeader = getDeviationHeader();
		
		deviationHeader.setId(id);
		
		StringBuilder selectSql = new StringBuilder("Select DeviationID, FinType, Module, ModuleCode, ValueType");
		selectSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append("");
		}
		selectSql.append(" From DeviationHeader");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where DeviationID =:DeviationID");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(deviationHeader);
		RowMapper<DeviationHeader> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(DeviationHeader.class);
		
		try{
			deviationHeader = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			deviationHeader = null;
		}
		logger.debug("Leaving");
		return deviationHeader;
	}
	
	/**
	 * Fetch the Record  Deviation Header details by key field
	 * 
	 * @param id (int)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return DeviationHeader
	 */
	@Override
	public List<DeviationHeader> getDeviationHeaderByFinType(final String finType, String type) {
		logger.debug("Entering");
		DeviationHeader deviationHeader = getDeviationHeader();
		
		deviationHeader.setFinType(finType);
		
		StringBuilder selectSql = new StringBuilder("Select DeviationID, FinType, Module, ModuleCode, ValueType");
		selectSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append("");
		}
		selectSql.append(" From DeviationHeader");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinType =:FinType");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(deviationHeader);
		RowMapper<DeviationHeader> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(DeviationHeader.class);
		logger.debug("Leaving");
		 return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}
	/**
	 * Fetch the Record  Deviation Header details by key field
	 * 
	 * @param id (int)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return DeviationHeader
	 */
	@Override
	public List<DeviationHeader> getDeviationHeader(final String finType,String module, String type) {
		logger.debug("Entering");
		DeviationHeader deviationHeader = getDeviationHeader();
		
		deviationHeader.setFinType(finType);
		deviationHeader.setModule(module);
		
		StringBuilder selectSql = new StringBuilder("Select DeviationID, FinType, Module, ModuleCode, ValueType");
		selectSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append("");
		}
		selectSql.append(" From DeviationHeader");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinType =:FinType and Module=:Module");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(deviationHeader);
		RowMapper<DeviationHeader> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(DeviationHeader.class);
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the DeviationHeader or DeviationHeader_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Deviation Header by key DeviationID
	 * 
	 * @param Deviation Header (deviationHeader)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(DeviationHeader deviationHeader,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete From DeviationHeader");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where DeviationID =:DeviationID");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(deviationHeader);
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
	 * This method insert new Records into DeviationHeader or DeviationHeader_Temp.
	 * it fetches the available Sequence form SeqDeviationHeader by using getNextidviewDAO().getNextId() method.  
	 *
	 * save Deviation Header 
	 * 
	 * @param Deviation Header (deviationHeader)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@Override
	public long save(DeviationHeader deviationHeader,String type) {
		logger.debug("Entering");
		if (deviationHeader.getId()==Long.MIN_VALUE){
			deviationHeader.setId(getNextidviewDAO().getNextId("SeqDeviationHeader"));
			logger.debug("get NextID:"+deviationHeader.getId());
		}
		
		StringBuilder insertSql =new StringBuilder("Insert Into DeviationHeader");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (DeviationID, FinType, Module, ModuleCode, ValueType");
		insertSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:DeviationID, :FinType, :Module, :ModuleCode, :ValueType");
		insertSql.append(", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(deviationHeader);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return deviationHeader.getId();
	}
	
	/**
	 * This method updates the Record DeviationHeader or DeviationHeader_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Deviation Header by key DeviationID and Version
	 * 
	 * @param Deviation Header (deviationHeader)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@Override
	public void update(DeviationHeader deviationHeader,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update DeviationHeader");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set FinType = :FinType, Module = :Module, ModuleCode = :ModuleCode, ValueType = :ValueType");
		updateSql.append(", Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where DeviationID =:DeviationID");
		
		if (!type.endsWith("_Temp")){
			updateSql.append("  AND Version= :Version-1");
		}
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(deviationHeader);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}
	
}