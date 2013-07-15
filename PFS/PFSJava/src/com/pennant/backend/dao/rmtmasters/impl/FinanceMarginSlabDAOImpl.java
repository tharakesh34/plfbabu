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
 * FileName    		:  FinanceMarginSlabDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-11-2011    														*
 *                                                                  						*
 * Modified Date    :  14-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-11-2011       Pennant~	                 0.1                                            * 
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


import java.math.BigDecimal;
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
import com.pennant.backend.dao.rmtmasters.FinanceMarginSlabDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.rmtmasters.FinanceMarginSlab;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>FinanceMarginSlab model</b> class.<br>
 * 
 */

public class FinanceMarginSlabDAOImpl extends BasisCodeDAO<FinanceMarginSlab> implements FinanceMarginSlabDAO {

	private static Logger logger = Logger.getLogger(FinanceMarginSlabDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	/**
	 * This method set the Work Flow id based on the module name and return the new FinanceMarginSlab 
	 * @return FinanceMarginSlab
	 */
	@Override
	public FinanceMarginSlab getFinanceMarginSlab() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("FinanceMarginSlab");
		FinanceMarginSlab financeMarginSlab= new FinanceMarginSlab();
		if (workFlowDetails!=null){
			financeMarginSlab.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return financeMarginSlab;
	}

	/**
	 * This method get the module from method getFinanceMarginSlab() and set the new record flag as true and return FinanceMarginSlab()   
	 * @return FinanceMarginSlab
	 */
	@Override
	public FinanceMarginSlab getNewFinanceMarginSlab() {
		logger.debug("Entering");
		FinanceMarginSlab financeMarginSlab = getFinanceMarginSlab();
		financeMarginSlab.setNewRecord(true);
		logger.debug("Leaving");
		return financeMarginSlab;
	}

	/**
	 * Fetch the Record  Finance Margin Slab Details details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return FinanceMarginSlab
	 */
	@Override
	public FinanceMarginSlab getFinanceMarginSlabById(final String id, String type) {
		logger.debug("Entering");
		FinanceMarginSlab financeMarginSlab = getFinanceMarginSlab();
		
		financeMarginSlab.setId(id);
		
		StringBuilder selectSql = new StringBuilder("Select FinType, SlabAmount, SlabMargin");
		selectSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(",");
		}
		selectSql.append(" From FCMTFinanceMarginSlab");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinType =:FinType");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMarginSlab);
		RowMapper<FinanceMarginSlab> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceMarginSlab.class);
		
		try{
			financeMarginSlab = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			financeMarginSlab = null;
		}
		logger.debug("Leaving");
		return financeMarginSlab;
	}
	
	/**
	 * Fetch the Record  Finance Margin Slab details by fin type field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return FinanceMarginSlab
	 */
	@Override
	public List<FinanceMarginSlab> getFinanceMarginSlabByFinType(final String finType, String type) {
		logger.debug("Entering");
		FinanceMarginSlab financeMarginSlab = getFinanceMarginSlab();
		
		financeMarginSlab.setId(finType);
		
		List<FinanceMarginSlab> financeMarginSlabList;
		
		StringBuilder selectSql = new StringBuilder("Select FinType, SlabAmount, SlabMargin");
		selectSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		/*if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(",");
		}*/
		selectSql.append(" From FCMTFinanceMarginSlab");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinType =:FinType");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMarginSlab);
		RowMapper<FinanceMarginSlab> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceMarginSlab.class);
		
		try{
			financeMarginSlabList = this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			financeMarginSlabList = null;
		}
		logger.debug("Leaving");
		return financeMarginSlabList;
	}

	
	/**
	 * This method initialize the Record.
	 * @param FinanceMarginSlab (financeMarginSlab)
 	 * @return FinanceMarginSlab
	 */
	@Override
	public void initialize(FinanceMarginSlab financeMarginSlab) {
		super.initialize(financeMarginSlab);
	}
	
	/**
	 * This method refresh the Record.
	 * @param FinanceMarginSlab (financeMarginSlab)
 	 * @return void
	 */
	@Override
	public void refresh(FinanceMarginSlab financeMarginSlab) {
		
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the FCMTFinanceMarginSlab or FCMTFinanceMarginSlab_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Finance Margin Slab Details by key FinType
	 * 
	 * @param Finance Margin Slab Details (financeMarginSlab)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(FinanceMarginSlab financeMarginSlab,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete From FCMTFinanceMarginSlab");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FinType =:FinType and SlabAmount = :SlabAmount");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMarginSlab);
		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				ErrorDetails errorDetails= getError("41003",financeMarginSlab.getId(),financeMarginSlab.getSlabAmount() ,financeMarginSlab.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {};
			}
		}catch(DataAccessException e){
			logger.error(e);
			ErrorDetails errorDetails= getError("41006",financeMarginSlab.getId(),financeMarginSlab.getSlabAmount() ,financeMarginSlab.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	@SuppressWarnings("serial")
	public void deleteAll(FinanceMarginSlab financeMarginSlab,String type) {
		logger.debug("Entering");		
		StringBuilder deleteSql = new StringBuilder("Delete From FCMTFinanceMarginSlab");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FinType =:FinType ");
		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMarginSlab);
		try{
		 this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);	
		}catch(DataAccessException e){
			logger.error(e);
			ErrorDetails errorDetails= getError("41006",financeMarginSlab.getId(),financeMarginSlab.getSlabAmount() ,financeMarginSlab.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	/**
	 * This method insert new Records into FCMTFinanceMarginSlab or FCMTFinanceMarginSlab_Temp.
	 *
	 * save Finance Margin Slab Details 
	 * 
	 * @param Finance Margin Slab Details (financeMarginSlab)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public String save(FinanceMarginSlab financeMarginSlab,String type) {
		logger.debug("Entering");
		
		StringBuilder insertSql =new StringBuilder("Insert Into FCMTFinanceMarginSlab");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (FinType, SlabAmount, SlabMargin");
		insertSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:FinType, :SlabAmount, :SlabMargin");
		insertSql.append(", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMarginSlab);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return financeMarginSlab.getId();
	}
	
	/**
	 * This method updates the Record FCMTFinanceMarginSlab or FCMTFinanceMarginSlab_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Finance Margin Slab Details by key FinType and Version
	 * 
	 * @param Finance Margin Slab Details (financeMarginSlab)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	@Override
	public void update(FinanceMarginSlab financeMarginSlab,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update FCMTFinanceMarginSlab");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set FinType = :FinType, SlabAmount = :SlabAmount, SlabMargin = :SlabMargin");
		updateSql.append(", Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where FinType =:FinType and SlabAmount = :SlabAmount ");
		
		if (!type.endsWith("_TEMP")){
			updateSql.append("  AND Version= :Version-1");
		}
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeMarginSlab);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :"+recordCount);
			ErrorDetails errorDetails= getError("41004",financeMarginSlab.getId(), financeMarginSlab.getSlabAmount(), financeMarginSlab.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	private ErrorDetails  getError(String errorId, String FinType, BigDecimal SlabAmount, String userLanguage){
		String[][] parms= new String[2][1];
		parms[1][0] = FinType;
		parms[1][1] = String.valueOf(SlabAmount);
		
		parms[0][0] = PennantJavaUtil.getLabel("label_FinType")+ ":" + parms[1][0];
		parms[0][1] = PennantJavaUtil.getLabel("label_SlabAmount")+ ":" + parms[1][1];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId, parms[0],parms[1]), userLanguage);
	}
	
}