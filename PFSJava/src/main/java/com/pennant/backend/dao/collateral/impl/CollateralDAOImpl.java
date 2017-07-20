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
 * FileName    		:  CollateralDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  04-12-2013    														*
 *                                                                  						*
 * Modified Date    :  04-12-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 04-12-2013       Pennant	                 0.1                                            * 
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
package com.pennant.backend.dao.collateral.impl;


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

import com.pennant.backend.dao.collateral.CollateralDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.collateral.Collateral;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>Collateral model</b> class.<br>
 * 
 */

public class CollateralDAOImpl extends BasisNextidDaoImpl<Collateral> implements CollateralDAO {

	private static Logger logger = Logger.getLogger(CollateralDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	public CollateralDAOImpl() {
		super();
	}
	
	/**
	 * This method set the Work Flow id based on the module name and return the new Collateral 
	 * @return Collateral
	 */

	@Override
	public Collateral getCollateral() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("Collateral");
		Collateral collateral= new Collateral();
		if (workFlowDetails!=null){
			collateral.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return collateral;
	}


	/**
	 * This method get the module from method getCollateral() and set the new record flag as true and return Collateral()   
	 * @return Collateral
	 */


	@Override
	public Collateral getNewCollateral() {
		logger.debug("Entering");
		Collateral collateral = getCollateral();
		collateral.setNewRecord(true);
		collateral.setReference(String.valueOf(getNextidviewDAO().getNextId("SeqCollateral")));
		logger.debug("Leaving");
		return collateral;
	}

	/**
	 * Fetch the Record  Collateral Details details by key field
	 * 
	 * @param caf (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return Collateral
	 */
	@Override
	public Collateral getCollateralById(final String caf,String ref, String type) {
		logger.debug("Entering");
		Collateral collateral = getCollateral();
		
		collateral.setCAFReference(caf);
		collateral.setReference(ref);
		
		StringBuilder selectSql = new StringBuilder("Select CAFReference, Reference, LastReview, Currency, Value, Bankvaluation, Bankmargin, ActualCoverage, ProposedCoverage, Description,CustID");
		selectSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(",CcyFormat");
		}
		selectSql.append(" From Collateral");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CAFReference =:CAFReference and Reference=:Reference");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(collateral);
		RowMapper<Collateral> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Collateral.class);
		
		try{
			collateral = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			collateral = null;
		}
		logger.debug("Leaving");
		return collateral;
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the Collateral or Collateral_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Collateral Details by key CAFReference
	 * 
	 * @param Collateral Details (collateral)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(Collateral collateral,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete From Collateral");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where CAFReference =:CAFReference and Reference=:Reference");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(collateral);
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
	 * This method insert new Records into Collateral or Collateral_Temp.
	 *
	 * save Collateral Details 
	 * 
	 * @param Collateral Details (collateral)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@Override
	public long save(Collateral collateral,String type) {
		logger.debug("Entering");
		if (collateral.getId()==Long.MIN_VALUE){
			collateral.setId(getNextidviewDAO().getNextId("SeqCollateral"));
			logger.debug("get NextID:"+collateral.getId());
		}
		
		StringBuilder insertSql =new StringBuilder("Insert Into Collateral");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (CAFReference, Reference, LastReview, Currency, Value, Bankvaluation, Bankmargin, ActualCoverage, ProposedCoverage, Description,CustID");
		insertSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:CAFReference, :Reference, :LastReview, :Currency, :Value, :Bankvaluation, :Bankmargin, :ActualCoverage, :ProposedCoverage, :Description, :CustID");
		insertSql.append(", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(collateral);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return collateral.getId();
	}
	
	/**
	 * This method updates the Record Collateral or Collateral_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Collateral Details by key CAFReference and Version
	 * 
	 * @param Collateral Details (collateral)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(Collateral collateral,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update Collateral");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set LastReview = :LastReview, Currency = :Currency, Value = :Value, Bankvaluation = :Bankvaluation, Bankmargin = :Bankmargin, ActualCoverage = :ActualCoverage, ProposedCoverage = :ProposedCoverage, Description = :Description, CustID = :CustID");
		updateSql.append(", Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where CAFReference =:CAFReference and Reference=:Reference");
		
		if (!type.endsWith("_Temp")){
			updateSql.append("  AND Version= :Version-1");
		}
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(collateral);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}
	@Override
	public List<Collateral> getCollateralsByCAF(String cafReference, String type){
		logger.debug("Entering");
		Collateral collateral = getCollateral();
		
		collateral.setCAFReference(cafReference);
		
		StringBuilder selectSql = new StringBuilder("Select CAFReference, Reference, LastReview, Currency, Value, Bankvaluation, Bankmargin, ActualCoverage, ProposedCoverage, Description, CustID");
		selectSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(",CcyFormat");
		}
		selectSql.append(" From Collateral");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CAFReference =:CAFReference ");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(collateral);
		RowMapper<Collateral> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Collateral.class);
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);	
	}
	@Override
	public void deleteByCAF(String cafReference, String type) {
		logger.debug("Entering");
		Collateral collateral = new Collateral();
		collateral.setCAFReference(cafReference);
		StringBuilder deleteSql = new StringBuilder("Delete From Collateral");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where CAFReference =:CAFReference");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(collateral);
		try {
			this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);

		} catch (DataAccessException e) {
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");
	}
}