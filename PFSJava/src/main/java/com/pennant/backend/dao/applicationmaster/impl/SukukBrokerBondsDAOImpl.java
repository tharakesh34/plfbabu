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
 * FileName    		:  SukukBrokerBondsDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  09-06-2015    														*
 *                                                                  						*
 * Modified Date    :  09-06-2015    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 09-06-2015       Pennant	                 0.1                                            * 
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

import com.pennant.backend.dao.applicationmaster.SukukBrokerBondsDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.applicationmasters.SukukBrokerBonds;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pff.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>SukukBrokerBonds model</b> class.<br>
 * 
 */

public class SukukBrokerBondsDAOImpl extends BasisCodeDAO<SukukBrokerBonds> implements SukukBrokerBondsDAO {

	private static Logger logger = Logger.getLogger(SukukBrokerBondsDAOImpl.class);
	
	public SukukBrokerBondsDAOImpl() {
		super();
	}
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	/**
	 * This method set the Work Flow id based on the module name and return the new SukukBrokerBonds 
	 * @return SukukBrokerBonds
	 */

	@Override
	public SukukBrokerBonds getSukukBrokerBonds() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("SukukBrokerBonds");
		SukukBrokerBonds sukukBrokerBonds= new SukukBrokerBonds();
		if (workFlowDetails!=null){
			sukukBrokerBonds.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return sukukBrokerBonds;
	}


	/**
	 * This method get the module from method getSukukBrokerBonds() and set the new record flag as true and return SukukBrokerBonds()   
	 * @return SukukBrokerBonds
	 */


	@Override
	public SukukBrokerBonds getNewSukukBrokerBonds() {
		logger.debug("Entering");
		SukukBrokerBonds sukukBrokerBonds = getSukukBrokerBonds();
		sukukBrokerBonds.setNewRecord(true);
		logger.debug("Leaving");
		return sukukBrokerBonds;
	}

	/**
	 * Fetch the Record  Sukuk Brokers details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return SukukBrokerBonds
	 */
	@Override
	public SukukBrokerBonds getSukukBrokerBondsById(final String id, String bondCode,String type) {
		logger.debug("Entering");
		SukukBrokerBonds sukukBrokerBonds = getSukukBrokerBonds();
		
		sukukBrokerBonds.setId(id);
		sukukBrokerBonds.setBondCode(bondCode);
		
		StringBuilder selectSql = new StringBuilder("Select BrokerCode, BondCode, PaymentMode, IssuerAccount, CommissionType, Commission");
		selectSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(",bondDesc,brokerDesc");
		}
		selectSql.append(" From SukukBroker_Bonds");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where BrokerCode =:BrokerCode and BondCode = :BondCode ");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(sukukBrokerBonds);
		RowMapper<SukukBrokerBonds> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(SukukBrokerBonds.class);
		
		try{
			sukukBrokerBonds = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			sukukBrokerBonds = null;
		}
		logger.debug("Leaving");
		return sukukBrokerBonds;
	}
	
	/**
	 * Fetch the Record  Sukuk Brokers details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return SukukBrokerBonds
	 */
	@Override
	public SukukBrokerBonds getUniqueBrokerByBond(String bondCode,String type) {
		logger.debug("Entering");
		SukukBrokerBonds sukukBrokerBonds = getSukukBrokerBonds();
		sukukBrokerBonds.setBondCode(bondCode);
		StringBuilder selectSql = new StringBuilder("Select BrokerCode, BondCode, PaymentMode, IssuerAccount, CommissionType, Commission");
		selectSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(",bondDesc,brokerDesc");
		}
		selectSql.append(" From SukukBroker_Bonds");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where BondCode in ( select BondCode from SukukBroker_Bonds where BondCode = :BondCode ");
		selectSql.append(" group by BondCode having COUNT(*) = 1 ) ");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(sukukBrokerBonds);
		RowMapper<SukukBrokerBonds> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(SukukBrokerBonds.class);
		
		try{
			sukukBrokerBonds = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			sukukBrokerBonds = null;
		}
		logger.debug("Leaving");
		return sukukBrokerBonds;
	}
	
	/**
	 * Fetch the Record  Sukuk Brokers details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return SukukBrokerBonds
	 */
	@Override
	public List<SukukBrokerBonds> getSukukBrokerBondsByCode(final String id, String type) {
		logger.debug("Entering");
		SukukBrokerBonds sukukBrokerBonds = getSukukBrokerBonds();
		
		sukukBrokerBonds.setId(id);
		
		StringBuilder selectSql = new StringBuilder("Select BrokerCode, BondCode, PaymentMode, IssuerAccount, CommissionType, Commission");
		selectSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(",bondDesc,brokerDesc");
		}
		selectSql.append(" From SukukBroker_Bonds");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where BrokerCode =:BrokerCode");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(sukukBrokerBonds);
		RowMapper<SukukBrokerBonds> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(SukukBrokerBonds.class);
		
		try{
			return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			sukukBrokerBonds = null;
		}
		logger.debug("Leaving");
		return null;
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the SukukBroker_Bonds or SukukBroker_Bonds_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Sukuk Brokers by key BrokerCode
	 * 
	 * @param Sukuk Brokers (sukukBrokerBonds)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(SukukBrokerBonds sukukBrokerBonds, String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete From SukukBroker_Bonds");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where BrokerCode =:BrokerCode and BondCode =:BondCode ");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(sukukBrokerBonds);
		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}
	
	/**
	 * This method Deletes the Records from the SukukBroker_Bonds or SukukBroker_Bonds_temp.
	 * delete Transaction Entry(s) by key AccountSetid
	 * 
	 * @param brokerCode (long)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return 
	 */
	@Override
	public void deleteBySukukBrokerCode(String brokerCode,String type) {
		logger.debug("Entering");
		SukukBrokerBonds sukukBrokerBonds=new SukukBrokerBonds();
		sukukBrokerBonds.setId(brokerCode);
		
		StringBuilder deleteSql = new StringBuilder("Delete From SukukBroker_Bonds");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where BrokerCode =:BrokerCode ");
		
		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(sukukBrokerBonds);
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}
	
	/**
	 * This method insert new Records into SukukBroker_Bonds or SukukBroker_Bonds_Temp.
	 *
	 * save Sukuk Brokers 
	 * 
	 * @param Sukuk Brokers (sukukBrokerBonds)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@Override
	public String save(SukukBrokerBonds sukukBrokerBonds,String type) {
		logger.debug("Entering");
		
		StringBuilder insertSql =new StringBuilder("Insert Into SukukBroker_Bonds");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (BrokerCode, BondCode, PaymentMode, IssuerAccount, CommissionType, Commission");
		insertSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:BrokerCode, :BondCode, :PaymentMode, :IssuerAccount, :CommissionType, :Commission");
		insertSql.append(", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(sukukBrokerBonds);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return sukukBrokerBonds.getId();
	}
	
	/**
	 * This method updates the Record SukukBroker_Bonds or SukukBroker_Bonds_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Sukuk Brokers by key BrokerCode and Version
	 * 
	 * @param Sukuk Brokers (sukukBrokerBonds)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(SukukBrokerBonds sukukBrokerBonds, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update SukukBroker_Bonds");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set PaymentMode = :PaymentMode, IssuerAccount = :IssuerAccount, CommissionType = :CommissionType, Commission = :Commission");
		updateSql.append(", Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where BrokerCode =:BrokerCode and BondCode=:BondCode ");
		
		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(sukukBrokerBonds);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}
}