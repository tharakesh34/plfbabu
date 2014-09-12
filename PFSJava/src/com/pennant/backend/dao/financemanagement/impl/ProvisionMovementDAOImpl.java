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
 * FileName    		:  ProvisionMovementDAOImpl.java                                                   * 	  
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


import java.util.Date;
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
import com.pennant.backend.dao.financemanagement.ProvisionMovementDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.financemanagement.ProvisionMovement;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>ProvisionMovement model</b> class.<br>
 * 
 */

public class ProvisionMovementDAOImpl extends BasisCodeDAO<ProvisionMovement> implements ProvisionMovementDAO {

	private static Logger logger = Logger.getLogger(ProvisionMovementDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	/**
	 * This method set the Work Flow id based on the module name and return the new ProvisionMovement 
	 * @return ProvisionMovement
	 */
	@Override
	public ProvisionMovement getProvisionMovement() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("ProvisionMovement");
		ProvisionMovement provisionMovement= new ProvisionMovement();
		if (workFlowDetails!=null){
			provisionMovement.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return provisionMovement;
	}

	/**
	 * This method get the module from method getProvisionMovement() and set the new record flag as true and return
	 * ProvisionMovement()
	 * 
	 * @return ProvisionMovement
	 */
	@Override
	public ProvisionMovement getNewProvisionMovement() {
		logger.debug("Entering");
		ProvisionMovement provisionMovement = getProvisionMovement();
		provisionMovement.setNewRecord(true);
		logger.debug("Leaving");
		return provisionMovement;
	}

	/**
	 * Fetch the Record  Provision Movement Detail details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return ProvisionMovement
	 */
	@Override
	public ProvisionMovement getProvisionMovementById(final String id,final Date movementDate, String type) {
		logger.debug("Entering");
		ProvisionMovement provisionMovement = new ProvisionMovement();
		
		provisionMovement.setId(id);
		provisionMovement.setProvMovementDate(movementDate);
		
		StringBuilder selectSql = new StringBuilder("Select FinReference, ProvMovementDate," );
		selectSql.append(" ProvMovementSeq, ProvCalDate, ProvisionedAmt, ProvisionAmtCal, ProvisionDue," );
		selectSql.append(" ProvisionPostSts, NonFormulaProv, UseNFProv, AutoReleaseNFP, PrincipalDue," );
		selectSql.append(" ProfitDue, DueFromDate, LastFullyPaidDate, LinkedTranId ");
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append("");
		}
		selectSql.append(" From FinProvMovements");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference AND ProvMovementDate =:ProvMovementDate ");
		selectSql.append(" and ProvMovementSeq =(SELECT MAX(ProvMovementSeq) FROM FinProvMovements " );
		selectSql.append(" Where FinReference =:FinReference AND ProvMovementDate =:ProvMovementDate ) ");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(provisionMovement);
		RowMapper<ProvisionMovement> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ProvisionMovement.class);
		
		try{
			provisionMovement = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(),
					beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			provisionMovement = null;
		}
		logger.debug("Leaving");
		return provisionMovement;
	}
	
	/**
	 * Fetch the Record  Provision Movement Detail details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return ProvisionMovement
	 */
	@Override
	public List<ProvisionMovement> getProvisionMovementListById(final String id, String type) {
		logger.debug("Entering");
		ProvisionMovement provisionMovement = new ProvisionMovement();
		
		provisionMovement.setId(id);
		
		StringBuilder selectSql = new StringBuilder("Select FinReference, ProvMovementDate," );
		selectSql.append(" ProvMovementSeq, ProvCalDate, ProvisionedAmt, ProvisionAmtCal, ProvisionDue," );
		selectSql.append(" ProvisionPostSts, NonFormulaProv, UseNFProv, AutoReleaseNFP, PrincipalDue," );
		selectSql.append(" ProfitDue, DueFromDate, LastFullyPaidDate, LinkedTranId ");
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append("");
		}
		selectSql.append(" From FinProvMovements");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference ");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(provisionMovement);
		RowMapper<ProvisionMovement> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ProvisionMovement.class);
		
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);	
	}
	
	/**
	 * This method initialise the Record.
	 * @param ProvisionMovement (provisionMovement)
 	 * @return ProvisionMovement
	 */
	@Override
	public void initialize(ProvisionMovement provisionMovement) {
		super.initialize(provisionMovement);
	}
	
	/**
	 * This method refresh the Record.
	 * @param ProvisionMovement (provisionMovement)
 	 * @return void
	 */
	@Override
	public void refresh(ProvisionMovement provisionMovement) {
		
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the FinProvMovements or FinProvMovements_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Provision Movement Detail by key FinReference
	 * 
	 * @param Provision Movement Detail (provisionMovement)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(ProvisionMovement provisionMovement,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete From FinProvMovements");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FinReference =:FinReference");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(provisionMovement);
		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				ErrorDetails errorDetails= getError("41003",provisionMovement.getId() ,
						provisionMovement.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {};
			}
		}catch(DataAccessException e){
			logger.error(e);
			ErrorDetails errorDetails= getError("41006",provisionMovement.getId() ,provisionMovement.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	/**
	 * This method insert new Records into FinProvMovements or FinProvMovements_Temp.
	 *
	 * save Provision Movement Detail 
	 * 
	 * @param Provision Movement Detail (provisionMovement)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public String save(ProvisionMovement provisionMovement,String type) {
		logger.debug("Entering");
		
		StringBuilder insertSql =new StringBuilder("Insert Into FinProvMovements");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (FinReference, ProvMovementDate, ProvMovementSeq, ProvCalDate, ProvisionedAmt," );
		insertSql.append(" ProvisionAmtCal, ProvisionDue, ProvisionPostSts, NonFormulaProv, UseNFProv," );
		insertSql.append(" AutoReleaseNFP, PrincipalDue, ProfitDue, DueFromDate, LastFullyPaidDate, LinkedTranId) ");
		insertSql.append(" Values(:FinReference, :ProvMovementDate, :ProvMovementSeq, :ProvCalDate," );
		insertSql.append(" :ProvisionedAmt, :ProvisionAmtCal, :ProvisionDue, :ProvisionPostSts, :NonFormulaProv," );
		insertSql.append(" :UseNFProv, :AutoReleaseNFP, :PrincipalDue, :ProfitDue, :DueFromDate, :LastFullyPaidDate, :LinkedTranId)");
		
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(provisionMovement);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return provisionMovement.getId();
	}
	
	/**
	 * This method updates the Record FinProvMovements or FinProvMovements.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Provision Movement Detail by key FinReference and Version
	 * 
	 * @param Provision Movement Detail (provisionMovement)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	@Override
	public void update(ProvisionMovement provisionMovement,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update FinProvMovements");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set ProvisionedAmt = :ProvisionedAmt, ProvisionDue = :ProvisionDue, " );
		updateSql.append(" ProvisionPostSts = :ProvisionPostSts, LinkedTranId = :LinkedTranId ");
		updateSql.append(" Where FinReference =:FinReference AND ProvMovementDate = :ProvMovementDate" );
		updateSql.append(" AND ProvMovementSeq = :ProvMovementSeq");
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(provisionMovement);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :"+recordCount);
			ErrorDetails errorDetails= getError("41004",provisionMovement.getId() ,
					provisionMovement.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	private ErrorDetails  getError(String errorId, String finReference, String userLanguage){
		String[][] parms= new String[2][1];
		parms[1][0] = finReference;
		parms[0][0] = PennantJavaUtil.getLabel("label_FinReference")+ ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, 
				errorId, parms[0],parms[1]), userLanguage);
	}

	
}