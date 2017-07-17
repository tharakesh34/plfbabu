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
 * FileName    		:  ProvisionDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  31-05-2012    														*
 *                                                                  						*
 * Modified Date    :  31-05-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 31-05-2012       Pennant	                 0.1                                            * 
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
package com.pennant.backend.dao.financemanagement.impl;

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

import com.pennant.backend.dao.financemanagement.ProvisionDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.financemanagement.Provision;
import com.pennant.backend.model.financemanagement.ProvisionMovement;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>Provision model</b> class.<br>
 * 
 */
public class ProvisionDAOImpl extends BasisCodeDAO<Provision> implements ProvisionDAO {
	private static Logger logger = Logger.getLogger(ProvisionDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	public ProvisionDAOImpl() {
		super();
	}
	
	/**
	 * This method set the Work Flow id based on the module name and return the new Provision 
	 * @return Provision
	 */

	@Override
	public Provision getProvision() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("Provision");
		Provision provision= new Provision();
		if (workFlowDetails!=null){
			provision.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return provision;
	}


	/**
	 * This method get the module from method getProvision() and set the new record flag as true and return Provision()   
	 * @return Provision
	 */


	@Override
	public Provision getNewProvision() {
		logger.debug("Entering");
		Provision provision = getProvision();
		provision.setNewRecord(true);
		logger.debug("Leaving");
		return provision;
	}

	/**
	 * Fetch the Record  Provision details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return Provision
	 */
	@Override
	public Provision getProvisionById(final String id, String type) {
		logger.debug("Entering");
		Provision provision = new Provision();
		
		provision.setId(id);
		
		StringBuilder selectSql = new StringBuilder("Select FinReference, FinBranch, FinType, " );
		selectSql.append(" CustID, ProvisionCalDate, ProvisionedAmt, ProvisionAmtCal, ProvisionDue, " );
		selectSql.append(" NonFormulaProv, UseNFProv, AutoReleaseNFP, PrincipalDue, ProfitDue, " );
		selectSql.append(" DueFromDate, LastFullyPaidDate , ");
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(" FinCcy, lovDescCustCIF, lovDescCustShrtName , ");
		}
		selectSql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId" );
		selectSql.append(" From FinProvisions");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(provision);
		RowMapper<Provision> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Provision.class);
		
		try{
			provision = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			provision = null;
		}
		logger.debug("Leaving");
		return provision;
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the FinProvisions or FinProvisions_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Provision by key FinReference
	 * 
	 * @param Provision (provision)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(Provision provision,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete From FinProvisions");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FinReference =:FinReference");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(provision);
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
	 * This method insert new Records into FinProvisions or FinProvisions_Temp.
	 *
	 * save Provision 
	 * 
	 * @param Provision (provision)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@Override
	public String save(Provision provision,String type) {
		logger.debug("Entering");
		
		StringBuilder insertSql =new StringBuilder("Insert Into FinProvisions");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (FinReference, FinBranch, FinType, CustID, ProvisionCalDate,");
		insertSql.append(" ProvisionedAmt, ProvisionAmtCal, ProvisionDue, NonFormulaProv, UseNFProv, AutoReleaseNFP,");
		insertSql.append(" PrincipalDue, ProfitDue, DueFromDate, LastFullyPaidDate, ");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId," );
		insertSql.append(" RecordType, WorkflowId,");
		insertSql.append(" Duedays,DpdBucketID,NpaBucketID,PftBal,PriBal,PrvovisionRate)");
		insertSql.append(" Values(:FinReference, :FinBranch, :FinType, :CustID, :ProvisionCalDate,");
		insertSql.append(" :ProvisionedAmt, :ProvisionAmtCal, :ProvisionDue, :NonFormulaProv,");
		insertSql.append(" :UseNFProv, :AutoReleaseNFP, :PrincipalDue, :ProfitDue, :DueFromDate, :LastFullyPaidDate, " );
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, ");
		insertSql.append(" :RecordType, :WorkflowId,");
		insertSql.append(" :Duedays,:DpdBucketID,:NpaBucketID,:PftBal,:PriBal,:PrvovisionRate)");
		
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(provision);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return provision.getId();
	}
	
	/**
	 * This method updates the Record FinProvisions or FinProvisions_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Provision by key FinReference and Version
	 * 
	 * @param Provision (provision)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@Override
	public void update(Provision provision,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update FinProvisions");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set FinBranch = :FinBranch,");
		updateSql.append(" FinType = :FinType, CustID = :CustID, ProvisionCalDate = :ProvisionCalDate,");
		updateSql.append(" ProvisionedAmt = :ProvisionedAmt, ProvisionAmtCal = :ProvisionAmtCal,");
		updateSql.append(" ProvisionDue = :ProvisionDue, NonFormulaProv = :NonFormulaProv,");
		updateSql.append(" UseNFProv = :UseNFProv, AutoReleaseNFP = :AutoReleaseNFP,");
		updateSql.append(" PrincipalDue = :PrincipalDue, ProfitDue = :ProfitDue, ");
		updateSql.append(" DueFromDate = :DueFromDate, LastFullyPaidDate = :LastFullyPaidDate, ");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, " );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode,NextRoleCode = :NextRoleCode, TaskId = :TaskId," );
		updateSql.append(" NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId" );
		updateSql.append(" Where FinReference =:FinReference");
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(provision);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Method for updating Provisioned Amount in Finance Provision Record
	 * @param provisionMovement
	 * @param type
	 */
	@Override
	public void updateProvAmt(ProvisionMovement provisionMovement,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update FinProvisions");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set ProvisionedAmt = :ProvisionedAmt, ProvisionDue = :ProvisionDue " );
		updateSql.append(" Where FinReference =:FinReference " );
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(provisionMovement);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}
	
	@Override
	public List<Provision> getProcessedProvisions() {
		logger.debug("Entering");
		StringBuilder selectSql = new StringBuilder(
		        "Select  FinReference, provisionCalDate,provisionAmt as provisionedAmt, provisionAmtCal, nonFormulaProv, useNFProv, prevProvisionCalDate, prevProvisionedAmt, transRef");
		
		selectSql.append(" From FinProcessedprovisions");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(new Provision());
		RowMapper<Provision> typeRowMapper = ParameterizedBeanPropertyRowMapper
		        .newInstance(Provision.class);

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,
		        typeRowMapper);
	
		
	}

	@Override
	public String saveProcessedProvisions(Provision provision) {
		logger.debug("Entering");
		
		StringBuilder insertSql =new StringBuilder("Insert Into FinProcessedprovisions");
		
		insertSql.append(" (FinReference, provisionCalDate, provisionAmt, provisionAmtCal, nonFormulaProv,");
		insertSql.append(" useNFProv, prevProvisionCalDate, prevProvisionedAmt, transRef)");
		
		insertSql.append(" Values(:FinReference, :ProvisionCalDate, :ProvisionedAmt, :ProvisionAmtCal, :NonFormulaProv,");
		insertSql.append(" :UseNFProv, :PrevProvisionCalDate, :PrevProvisionedAmt, :TransRef)");
		
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(provision);
		try {
			this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		} catch (DataAccessException e) {
			logger.debug(e);
		}
		logger.debug("Leaving");
		return provision.getId();
	}
	
	@Override
	public Provision getCurNPABucket(final String id) {
		logger.debug("Entering");
		Provision provision = new Provision();
		
		provision.setId(id);
		
		StringBuilder selectSql = new StringBuilder("Select NpaBucketID " );
		selectSql.append(" From FinProvisions");
		selectSql.append(" Where FinReference =:FinReference");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(provision);
		RowMapper<Provision> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Provision.class);
		
		try{
			provision = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			provision = null;
		}
		logger.debug("Leaving");
		return provision;
	}
	
	@Override
	public void updateProvisonAmounts(Provision provision) {
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update FinProvisions");
		updateSql.append(" Set ProvisionCalDate = :ProvisionCalDate,ProvisionAmtCal=:ProvisionAmtCal,");
		updateSql.append(" ProvisionedAmt = :ProvisionedAmt, Duedays = :Duedays, DpdBucketID = :DpdBucketID,");
		updateSql.append(" NpaBucketID = :NpaBucketID, PftBal = :PftBal, PriBal = :PriBal, PrvovisionRate = :PrvovisionRate " );
		updateSql.append(" Where FinReference =:FinReference " );
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(provision);
		 this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	
}