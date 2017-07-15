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
 * FileName    		:  FinCovenantTypeDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-08-2013    														*
 *                                                                  						*
 * Modified Date    :  14-08-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-08-2013       Pennant	                 0.1                                            * 
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
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.finance.FinCovenantTypeDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.finance.FinCovenantType;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pff.core.ConcurrencyException;
import com.pennanttech.pff.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>FinCovenantType model</b> class.<br>
 * 
 */

public class FinCovenantTypeDAOImpl extends BasisCodeDAO<FinCovenantType> implements FinCovenantTypeDAO {

	private static Logger logger = Logger.getLogger(FinCovenantTypeDAOImpl.class);
	
	public FinCovenantTypeDAOImpl() {
		super();
	}
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	/**
	 * This method set the Work Flow id based on the module name and return the new FinCovenantType 
	 * @return FinCovenantType
	 */

	@Override
	public FinCovenantType getFinCovenantType() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("FinCovenantType");
		FinCovenantType finCovenantType= new FinCovenantType();
		if (workFlowDetails!=null){
			finCovenantType.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return finCovenantType;
	}


	/**
	 * This method get the module from method getFinCovenantType() and set the new record flag as true and return FinCovenantType()   
	 * @return FinCovenantType
	 */


	@Override
	public FinCovenantType getNewFinCovenantType() {
		logger.debug("Entering");
		FinCovenantType finCovenantType = getFinCovenantType();
		finCovenantType.setNewRecord(true);
		logger.debug("Leaving");
		return finCovenantType;
	}

	/**
	 * Fetch the Record  Goods Details details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return FinCovenantType
	 */
	@Override
	public FinCovenantType getFinCovenantTypeById(FinCovenantType finCovenantType, String type) {
		logger.debug("Entering");
		
		StringBuilder selectSql = new StringBuilder();
		selectSql.append("Select FinReference, CovenantType, Description, MandRole, AlwWaiver, AlwPostpone, PostponeDays,ReceivableDate, ");
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append("CovenantTypeDesc,");
		}
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		
		selectSql.append(" From FinCovenantType");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference = :FinReference and CovenantType = :CovenantType");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finCovenantType);
		RowMapper<FinCovenantType> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinCovenantType.class);
		
		try{
			finCovenantType = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			finCovenantType = null;
		}
		logger.debug("Leaving");
		return finCovenantType;
	}
	
	@Override
	public List<FinCovenantType> getFinCovenantTypeByFinRef(final String id, String type,boolean isEnquiry) {
		logger.debug("Entering");
		FinCovenantType finCovenantType = new FinCovenantType();
		finCovenantType.setId(id);
		
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" Select FinReference, CovenantType, Description, MandRole, AlwWaiver, AlwPostpone, PostponeDays,ReceivableDate,lovdescIsCustDoc,");
		if(isEnquiry){
			selectSql.append(" CovenantTypeDesc,");
		}else{
			if (StringUtils.trimToEmpty(type).contains("View")){
				selectSql.append(" CovenantTypeDesc,MandRoleDesc,");
			}
		}
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From FinCovenantType");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference = :FinReference");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finCovenantType);
		RowMapper<FinCovenantType> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinCovenantType.class);
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
	 * This method Deletes the Record from the FinCovenantType or FinCovenantType_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Goods Details by key LoanRefNumber
	 * 
	 * @param Goods Details (FinCovenantType)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(FinCovenantType finCovenantType,String type) {
		logger.debug("Entering");
		StringBuilder deleteSql = new StringBuilder("Delete From FinCovenantType");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FinReference =:FinReference and CovenantType = :CovenantType");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finCovenantType);
		try{
			this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
		}catch(DataAccessException e){
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}
	
	/**
	 * This method insert new Records into FinCovenantType or FinCovenantType_Temp.
	 *
	 * save Goods Details 
	 * 
	 * @param Goods Details (FinCovenantType)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@Override
	public String save(FinCovenantType finCovenantType,String type) {
		logger.debug("Entering");
		StringBuilder insertSql =new StringBuilder();
		
		insertSql.append(" Insert Into FinCovenantType");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (FinReference, CovenantType , Description, MandRole, AlwWaiver, AlwPostpone, PostponeDays,ReceivableDate," );
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values( :FinReference, :CovenantType , :Description, :MandRole, :AlwWaiver,:AlwPostpone, :PostponeDays, :ReceivableDate,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode,");
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finCovenantType);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return finCovenantType.getId();
	}
	
	/**
	 * This method updates the Record FinCovenantType or FinCovenantType_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Goods Details by key LoanRefNumber and Version
	 * 
	 * @param Goods Details (FinCovenantType)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@Override
	public void update(FinCovenantType finCovenantType,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		
		StringBuilder	updateSql =new StringBuilder("Update FinCovenantType");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append("  Set Description = :Description,");
		updateSql.append("  MandRole = :MandRole, AlwWaiver = :AlwWaiver, AlwPostpone = :AlwPostpone, PostponeDays = :PostponeDays, ReceivableDate =:ReceivableDate,");
		updateSql.append("  Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append("  Where FinReference = :FinReference and CovenantType = :CovenantType");
		
		if (!type.endsWith("_Temp")){
			updateSql.append("  AND Version= :Version-1");
		}
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finCovenantType);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	@Override
    public void deleteByFinRef(String loanReference, String tableType) {
		logger.debug("Entering");
		FinCovenantType finCovenantType = new FinCovenantType();
		finCovenantType.setId(loanReference);
		
		StringBuilder deleteSql = new StringBuilder("Delete From FinCovenantType");
		deleteSql.append(StringUtils.trimToEmpty(tableType));
		deleteSql.append(" Where FinReference = :FinReference ");
		logger.debug("deleteSql: " + deleteSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finCovenantType);
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	    
    }
	
	
}