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
 * FileName    		:  CollateralAssignmentDAOImpl.java                                     * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  07-05-2016    														*
 *                                                                  						*
 * Modified Date    :  07-05-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 07-05-2016       Pennant	                 0.1                                            * 
 *                                                                                          * 
 * 16-05-2018       Srinivasa Varma          0.2          Development Item 82               * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
*/
package com.pennant.backend.dao.collateral.impl;


import java.math.BigDecimal;
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
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.collateral.CollateralAssignmentDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.collateral.AssignmentDetails;
import com.pennant.backend.model.collateral.CollateralAssignment;
import com.pennant.backend.model.collateral.CollateralMovement;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>CollateralAssignment model</b> class.<br>
 * 
 */

public class CollateralAssignmentDAOImpl extends BasisNextidDaoImpl<CollateralMovement> implements CollateralAssignmentDAO {

	private static Logger logger = Logger.getLogger(CollateralAssignmentDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	
	public CollateralAssignmentDAOImpl() {
		super();
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the CollateralAssignment or CollateralAssignment_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Collateral Assignment by key AssignmentId
	 * 
	 * @param Collateral Assignment (collateralAssignment)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(CollateralAssignment collateralAssignment,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete from CollateralAssignment");
 		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where Reference = :Reference and Module = :Module and CollateralRef = :CollateralRef ");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(collateralAssignment);
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
	 * 
	 * @param reference
	 * @param type
	 */
	@Override
	public void deleteByReference(String reference, String type) {
		logger.debug("Entering");

		CollateralAssignment collateralAssignment = new CollateralAssignment();
		collateralAssignment.setReference(reference);

		StringBuilder sql = new StringBuilder("Delete From CollateralAssignment");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where Reference = :Reference");

		logger.debug("deleteSql: " + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(collateralAssignment);
		this.namedParameterJdbcTemplate.update(sql.toString(), beanParameters);

		logger.debug("Leaving");
	}
	
	/**
	 * This method insert new Records into CollateralAssignment or CollateralAssignment_Temp.
	 *
	 * save Collateral Assignment 
	 * 
	 * @param Collateral Assignment (collateralAssignment)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void save(CollateralAssignment collateralAssignment,String type) {
		logger.debug("Entering");
		
		StringBuilder query =new StringBuilder("Insert Into CollateralAssignment");
		query.append(StringUtils.trimToEmpty(type));
		query.append(" (Reference, Module, CollateralRef, AssignPerc ,Active, ");
		query.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		query.append(" Values(:Reference, :Module, :CollateralRef, :AssignPerc,:Active,");
		query.append(" :Version ,:LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: " + query.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(collateralAssignment);
		this.namedParameterJdbcTemplate.update(query.toString(), beanParameters);
		logger.debug("Leaving");
	}
	
	/**
	 * This method updates the Record CollateralAssignment or CollateralAssignment_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * 
	 * @param Collateral Assignment (collateralAssignment)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(CollateralAssignment collateralAssignment,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update CollateralAssignment");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set AssignPerc = :AssignPerc, Active= :Active, Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, ");
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, ");
		updateSql.append(" NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where Reference =:Reference and Module = :Module and CollateralRef = :CollateralRef ");
		
		if (!type.endsWith("_Temp")){
			updateSql.append("  AND Version= :Version-1");
		}
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(collateralAssignment);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Fetching List of Assigned Collateral to the Reference based on Module
	 */
	@Override
	public List<CollateralAssignment> getCollateralAssignmentByFinRef(String reference, String moduleName, String type) {
		logger.debug("Entering");

		CollateralAssignment collateralAssignment = new CollateralAssignment();
		collateralAssignment.setReference(reference);
		collateralAssignment.setModule(moduleName);

		StringBuilder selectSql = new StringBuilder("Select Module , Reference , CollateralRef , AssignPerc , Active, ");
		if(type.contains("View")){
			//### 16-05-2018 Development Item 82
			selectSql.append(" CollateralCcy , CollateralValue , BankValuation , TotAssignPerc TotAssignedPerc , UtilizedAmount, bankLTV, specialLTV,");
 		}
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId,");
		selectSql.append(" NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From CollateralAssignment");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where Reference =:Reference and Module = :Module ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(collateralAssignment);
		RowMapper<CollateralAssignment> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CollateralAssignment.class);

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,
		        typeRowMapper);
	}
	
	/**
	 * Method for Fetching List of Assigned Collateral to the Reference based on Module
	 */
	@Override
	public List<AssignmentDetails> getCollateralAssignmentByColRef(String collateralRef, String collateralType) {
		logger.debug("Entering");
		
		CollateralAssignment collateralAssignment = new CollateralAssignment();
		collateralAssignment.setCollateralRef(collateralRef);
		
		StringBuilder selectSql = new StringBuilder("Select Module, CA.Reference, Coalesce(FM.FinCcy,CM.CmtCcy) Currency, AssignPerc AssignedPerc, ");
		selectSql.append(" BankValuation CollateralValue, TotAssignedPerc,  ");
		selectSql.append("Coalesce(FinCurrAssetValue+FeeChargeAmt+InsuranceAmt-FinRepaymentAmount,CmtUtilizedAmount) FinCurrAssetValue, ");
		selectSql.append("Coalesce(FinAssetValue+FeeChargeAmt+InsuranceAmt,CmtUtilizedAmount) FinAssetValue, ");
		selectSql.append(" CmtExpDate , Coalesce(FM.FinIsActive,Coalesce(CmtActive,0)) FinIsActive, TotalUtilized,FT.FinLTVCheck from CollateralAssignment CA Left Join ");
		selectSql.append(" FinanceMain FM on CA.Reference = FM.FinReference left join ");
		selectSql.append(" RMTFinanceTypes FT on  FM.FINTYPE = FT.FINTYPE Left Join ");
		selectSql.append(" Commitments CM on CM.CmtReference = CA.Reference ");
		selectSql.append(" inner join CollateralSetUp CS on CS.CollateralRef = CA.CollateralRef ");
		selectSql.append(" Left join ( Select CollateralRef, SUM(AssignPerc) TotAssignedPerc from CollateralAssignment group by CollateralRef) T ");
		selectSql.append(" on T.CollateralRef = CA.CollateralRef  ");
		selectSql.append(" LEFT JOIN (Select CA.Reference, SUM((CS.BankValuation * CA.AssignPerc)/100) TotalUtilized ");
		selectSql.append(" from CollateralAssignment CA inner join CollateralSetUp CS on CS.CollateralRef = CA.CollateralRef ");
		selectSql.append(" group by CA.Reference) T1 on CA.Reference = T1.Reference  ");
		selectSql.append(" Where CA.CollateralRef =:CollateralRef ");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(collateralAssignment);
		RowMapper<AssignmentDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(AssignmentDetails.class);
		
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,
				typeRowMapper);
	}
	
	/**
	 * Method for Fetching List of Assigned Collateral to the Reference based on Module and Collateral Reference
	 */
	@Override
	public CollateralAssignment getCollateralAssignmentbyID(CollateralAssignment collateralAssignment, String type) {
		logger.debug("Entering");

		CollateralAssignment collAssignment = null;
		StringBuilder selectSql = new StringBuilder(" Select Reference, Module, CollateralRef, AssignPerc, Active, ");
		if(type.contains("View")){
 		}
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId,");
		selectSql.append(" NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From CollateralAssignment");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where Reference =:Reference and Module = :Module and CollateralRef = :CollateralRef ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(collateralAssignment);
		RowMapper<CollateralAssignment> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CollateralAssignment.class);
		
		try{
			collAssignment	= this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.info(e);
			collAssignment = null;
		}
		logger.debug("Leaving");
		return collAssignment;
	}
	
	/**
	 * Method for Fetching Count for Assigned Collateral to Different Finances/Commitments
	 */
	@Override
	public int getAssignedCollateralCount(String collateralRef, String type) {
		logger.debug("Entering");
		
		int assignedCount = 0;
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CollateralRef", collateralRef);
		
		StringBuilder selectSql = new StringBuilder(" Select Count(CollateralRef) ");
		selectSql.append(" From CollateralAssignment");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CollateralRef = :CollateralRef ");
		
		logger.debug("selectSql: " + selectSql.toString());
		
		try{
			assignedCount	= this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);	
		}catch (EmptyResultDataAccessException e) {
			logger.info(e);
			assignedCount = 0;
		}
		logger.debug("Leaving");
		return assignedCount;
	}
	
	/**
	 * Method for Fetching List of Assigned Collateral to the Reference based on Module and Collateral Reference
	 */
	@Override
	public BigDecimal getAssignedPerc(String collateralRef, String reference, String type) {
		logger.debug("Entering");
		
		BigDecimal totAssignExptCur = BigDecimal.ZERO;
		CollateralAssignment collateralAssignment = new CollateralAssignment();
		collateralAssignment.setCollateralRef(collateralRef);
		collateralAssignment.setReference(reference);

		StringBuilder selectSql = new StringBuilder(" select COALESCE(SUM(AssignPerc),0) AssignPerc");
		selectSql.append(" From CollateralAssignment");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CollateralRef = :CollateralRef AND Active = 1 ");
		if(StringUtils.isNotEmpty(reference)){
			selectSql.append(" AND Reference != :Reference "); 
		}

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(collateralAssignment);
		
		try{
			totAssignExptCur	= this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, BigDecimal.class);	
		}catch (EmptyResultDataAccessException e) {
			logger.info(e);
			totAssignExptCur = BigDecimal.ZERO;
		}
		logger.debug("Leaving");
		return totAssignExptCur;
	}
	
	/**
	 * Method for Delinking Collaterals Details Assigned to Finance after Maturity
	 */
	@Override
	public void deLinkCollateral(String finReference) {
		logger.debug("Entering");

		CollateralAssignment collateralAssignment = new CollateralAssignment();
		collateralAssignment.setReference(finReference);

		StringBuilder sql = new StringBuilder("Delete From CollateralAssignment ");
		sql.append(" Where Reference = :Reference");

		logger.debug("deleteSql: " + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(collateralAssignment);
		this.namedParameterJdbcTemplate.update(sql.toString(), beanParameters);

		logger.debug("Leaving");
	}
	
	/**
	 * This method insert new Records into CollateralMovement.
	 *
	 * save Collateral Movement 
	 * 
	 * @param Collateral Movement (collateralMovement)
	 * @param  type (String) ""          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void save(CollateralMovement movement) {
		logger.debug("Entering");
		
		if(movement.getMovementSeq() == 0 || movement.getMovementSeq()==Long.MIN_VALUE){
			movement.setMovementSeq(getNextidviewDAO().getNextId("SeqCollateralMovement"));	
		}
		
		StringBuilder query =new StringBuilder("Insert Into CollateralMovement");
		query.append(" (MovementSeq , Module, CollateralRef, Reference, AssignPerc , ValueDate, Process)");
		query.append(" Values(:MovementSeq ,:Module, :CollateralRef, :Reference, :AssignPerc , :ValueDate, :Process)");
		
		logger.debug("insertSql: " + query.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(movement);
		this.namedParameterJdbcTemplate.update(query.toString(), beanParameters);
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Fetching Collateral Movement Details
	 */
	@Override
	public List<CollateralMovement> getCollateralMovements(String collateralRef) {
		logger.debug("Entering");
		
		CollateralMovement movement = new CollateralMovement();
		movement.setCollateralRef(collateralRef);
		
		StringBuilder selectSql = new StringBuilder("Select MovementSeq , Module, Reference, ");
		selectSql.append(" AssignPerc , ValueDate, Process  From CollateralMovement ");
		selectSql.append(" Where CollateralRef = :CollateralRef Order By MovementSeq ");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(movement);
		RowMapper<CollateralMovement> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CollateralMovement.class);
		
		List<CollateralMovement> list = this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,
				typeRowMapper);
		logger.debug("Leaving");
		return list;
	}
}