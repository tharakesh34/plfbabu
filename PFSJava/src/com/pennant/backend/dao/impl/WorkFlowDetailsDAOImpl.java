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
 *
 * FileName    		:  WorkFlowDetailsDAOImpl.java											*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  26-04-2011															*
 *                                                                  
 * Modified Date    :  26-04-2011															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-04-2011       Pennant	                 0.1                                            * 
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
package com.pennant.backend.dao.impl;

import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.WorkFlowDetailsDAO;
import com.pennant.backend.model.WorkFlowDetails;

public class WorkFlowDetailsDAOImpl extends BasisNextidDaoImpl<WorkFlowDetails> implements WorkFlowDetailsDAO{
	
	private static Logger logger = Logger.getLogger(WorkFlowDetailsDAOImpl.class);
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public WorkFlowDetails getWorkFlowDetails (){
		logger.debug("Entering + getWorkFlowDetails()");
		return new WorkFlowDetails();
	}
	
	public WorkFlowDetails getNewWorkFlowDetails (){
		logger.debug("Entering + getNewWorkFlowDetails()");
		WorkFlowDetails workFlowDetails  = getWorkFlowDetails();
		workFlowDetails.setNewRecord(true);
		return workFlowDetails;
	}
	
	/**
	 * This method initialize the Record.
	 * 
	 * @param WorkFlowDetails
	 *            (workFlowDetails)
	 * @return WorkFlowDetails
	 */
	@Override
	public void initialize(WorkFlowDetails workFlowDetails) {
		super.initialize(workFlowDetails);
	}
	
	/**
	 * This method refresh the Record.
	 * 
	 * @param WorkFlowDetails
	 *            (workFlowDetails)
	 * @return void
	 */
	@Override
	public void refresh(WorkFlowDetails workFlowDetails) {
	}

	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * Fetch the Record Work Flow details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return workFlowDetails
	 */
	public WorkFlowDetails getWorkFlowDetailsByID(long id){

		WorkFlowDetails workFlowDetails= getWorkFlowDetails();
		workFlowDetails.setId(id);
		logger.debug("Entering + getWorkFlowDetailsByID()");
		
		String selectListSql = 	"select WorkFlowId, WorkFlowType, WorkFlowSubType, WorkFlowDesc," +
				" WorkFlowXml, WorkFlowRoles,FirstTaskOwner, WorkFlowActive, " +
				"Version , LastMntBy, LastMntOn, JsonDesign from WorkFlowDetails where WorkFlowId =:WorkFlowId"; 
		logger.debug("selectListSql: " + selectListSql);
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(workFlowDetails);
		RowMapper<WorkFlowDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(WorkFlowDetails.class);
		
		try{
			workFlowDetails = this.namedParameterJdbcTemplate.queryForObject(
					selectListSql, beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			workFlowDetails  = null;
		}
		
		return workFlowDetails;
	}
	
	/**
	 * This method get the module from method getWorkFlowDetailsByType() and set the new
	 * record flag as true and return workFlowDetails
	 * 
	 *  
	 * @return workFlowDetails
	 */
	public WorkFlowDetails getWorkFlowDetailsByFlowType(String workFlowType){
		logger.debug("Entering + getWorkFlowDetailsByFlowType()");
		
		WorkFlowDetails workFlowDetails= getWorkFlowDetails();
		workFlowDetails.setWorkFlowType(workFlowType);
		
		String selectListSql = 	"select WorkFlowId, WorkFlowType, WorkFlowSubType, WorkFlowDesc," +
				" WorkFlowXml, WorkFlowRoles,FirstTaskOwner, WorkFlowActive " +
				" Version , LastMntBy, LastMntOn from WorkFlowDetails " +
				" where WorkFlowType =:WorkFlowType AND WorkFlowActive=1"; 
		logger.debug("selectListSql: " + selectListSql);
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(workFlowDetails);
		RowMapper<WorkFlowDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper.
						newInstance(WorkFlowDetails.class);
		
		try{
			workFlowDetails = this.namedParameterJdbcTemplate.queryForObject(
					selectListSql, beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			workFlowDetails  = null;
		}
		
		return workFlowDetails;
	}

	public List<WorkFlowDetails> getActiveWorkFlowDetails(){
		logger.debug("Entering + getWorkFlowDetailsByID()");
		String selectListSql = 	"select WorkFlowId, WorkFlowType, WorkFlowSubType," +
				" WorkFlowDesc, WorkFlowXml, WorkFlowRoles, FirstTaskOwner, WorkFlowActive " +
				" Version , LastMntBy, LastMntOn from WorkFlowDetails where WorkFlowActive=1"; 
		logger.debug("selectListSql: " + selectListSql);
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(new WorkFlowDetails());
		RowMapper<WorkFlowDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(WorkFlowDetails.class);
		return this.namedParameterJdbcTemplate.query(selectListSql, beanParameters,typeRowMapper);	
		
	}

	/**
	 * This method insert new Records into WorkFlowDetails .
	 * 
	 * save WorkFlowDetails
	 * 
	 * @param WorkFlowDetails
	 *             		(workFlowDetails)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return workFlowId
	 * 
	 */
	public long save(WorkFlowDetails workFlowDetails){
		logger.debug("Entering + save()");
		long  workFlowId = getNextidviewDAO().getNextId("SeqWorkFlowDetails");
		workFlowDetails.setId(workFlowId);
		String insertSql = 	"insert into WorkFlowDetails (WorkFlowId, WorkFlowType, " +
					" WorkFlowSubType, WorkFlowDesc, WorkFlowXml, WorkFlowRoles," +
					" FirstTaskOwner , WorkFlowActive, Version , LastMntBy, LastMntOn, JsonDesign) " +
					" values(:WorkFlowId, :WorkFlowType, :WorkFlowSubType, :WorkFlowDesc, " +
					" :WorkFlowXml,:WorkFlowRoles, :FirstTaskOwner, :WorkFlowActive," +
					" :Version , :LastMntBy, :LastMntOn, :JsonDesign)";
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(workFlowDetails);
		this.namedParameterJdbcTemplate.update(insertSql, beanParameters);
		
		return workFlowId;
 	}
	
	public void update(WorkFlowDetails workFlowDetails){
		logger.debug("Entering + update()");
		int recordCount=0;
		String updateSql = 	"update WorkFlowDetails set WorkFlowActive= :WorkFlowActive  ," +
				" version=:version, lastMntBy= :lastMntBy , lastMntOn= :lastMntOn  " + 
				"where WorkFlowId= :WorkFlowId" ;
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(workFlowDetails);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql, beanParameters);
		logger.info("Number of Records Update :"+recordCount);
	}
}
