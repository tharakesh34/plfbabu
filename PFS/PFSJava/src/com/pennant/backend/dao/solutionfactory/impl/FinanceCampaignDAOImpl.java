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
 * FileName    		:  FinanceCampaignDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  30-12-2011    														*
 *                                                                  						*
 * Modified Date    :  30-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 30-12-2011       Pennant	                 0.1                                            * 
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
import com.pennant.backend.dao.solutionfactory.FinanceCampaignDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.solutionfactory.FinanceCampaign;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>FinanceCampaign model</b> class.<br>
 * 
 */

public class FinanceCampaignDAOImpl extends BasisCodeDAO<FinanceCampaign> implements FinanceCampaignDAO {

	private static Logger logger = Logger.getLogger(FinanceCampaignDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	/**
	 * This method set the Work Flow id based on the module name and return the new FinanceCampaign 
	 * @return FinanceCampaign
	 */

	@Override
	public FinanceCampaign getFinanceCampaign() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("FinanceCampaign");
		FinanceCampaign financeCampaign= new FinanceCampaign();
		if (workFlowDetails!=null){
			financeCampaign.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return financeCampaign;
	}


	/**
	 * This method get the module from method getFinanceCampaign() and set the new record flag as true and return FinanceCampaign()   
	 * @return FinanceCampaign
	 */


	@Override
	public FinanceCampaign getNewFinanceCampaign() {
		logger.debug("Entering");
		FinanceCampaign financeCampaign = getFinanceCampaign();
		financeCampaign.setNewRecord(true);
		logger.debug("Leaving");
		return financeCampaign;
	}

	/**
	 * Fetch the Record  Finance Campaign details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return FinanceCampaign
	 */
	@Override
	public FinanceCampaign getFinanceCampaignById(final String id, String type) {
		logger.debug("Entering");
		FinanceCampaign financeCampaign = getFinanceCampaign();
		
		financeCampaign.setId(id);
		
		StringBuilder selectSql = new StringBuilder("Select FCCode, FCDesc, FCFinType, FCIsAlwMD, FCIsAlwGrace, FCOrgPrfUnchanged, FCRateType, FCBaseRate, FCSplRate, FCIntRate, FCDftIntFrq, FCIsIntCpz, FCCpzFrq, FCIsRvwAlw, FCRvwFrq, FCGrcRateType, FCGrcBaseRate, FCGrcSplRate, FCGrcIntRate, FCGrcDftIntFrq, FCGrcIsIntCpz, FCGrcCpzFrq, FCGrcIsRvwAlw, FCGrcRvwFrq, FCMinTerm, FCMaxTerm, FCDftTerms, FCRpyFrq, FCRepayMethod, FCIsAlwPartialRpy, FCIsAlwDifferment, FCMaxDifferment, FCIsAlwFrqDifferment, FCMaxFrqDifferment, FCIsAlwEarlyRpy, FCIsAlwEarlySettle, FCIsDwPayRequired, FCRvwRateApplFor, FCAlwRateChangeAnyDate, FCGrcRvwRateApplFor, FCIsIntCpzAtGrcEnd, FCGrcAlwRateChgAnyDate, FCMinDownPayAmount, FCSchCalCodeOnRvw");
		selectSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(",");
		}
		selectSql.append(" From FinanceCampaign");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FCCode =:FCCode");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeCampaign);
		RowMapper<FinanceCampaign> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceCampaign.class);
		
		try{
			financeCampaign = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			financeCampaign = null;
		}
		logger.debug("Leaving");
		return financeCampaign;
	}
	
	/**
	 * This method initialise the Record.
	 * @param FinanceCampaign (financeCampaign)
 	 * @return FinanceCampaign
	 */
	@Override
	public void initialize(FinanceCampaign financeCampaign) {
		super.initialize(financeCampaign);
	}
	/**
	 * This method refresh the Record.
	 * @param FinanceCampaign (financeCampaign)
 	 * @return void
	 */
	@Override
	public void refresh(FinanceCampaign financeCampaign) {
		
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the RMTFinCampaign or RMTFinCampaign_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Finance Campaign by key FCCode
	 * 
	 * @param Finance Campaign (financeCampaign)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(FinanceCampaign financeCampaign,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete From FinanceCampaign");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FCCode =:FCCode");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeCampaign);
		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				ErrorDetails errorDetails= getError("41003",financeCampaign.getId() ,financeCampaign.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {};
			}
		}catch(DataAccessException e){
			logger.error(e);
			ErrorDetails errorDetails= getError("41006",financeCampaign.getId() ,financeCampaign.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	/**
	 * This method insert new Records into FinanceCampaign or FinanceCampaign_Temp.
	 *
	 * save Finance Campaign 
	 * 
	 * @param Finance Campaign (financeCampaign)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@Override
	public String save(FinanceCampaign financeCampaign,String type) {
		logger.debug("Entering");
		
		StringBuilder insertSql =new StringBuilder("Insert Into FinanceCampaign");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (FCCode, FCDesc, FCFinType, FCIsAlwMD, FCIsAlwGrace, FCOrgPrfUnchanged, FCRateType, FCBaseRate, FCSplRate, FCIntRate, FCDftIntFrq, FCIsIntCpz, FCCpzFrq, FCIsRvwAlw, FCRvwFrq, FCGrcRateType, FCGrcBaseRate, FCGrcSplRate, FCGrcIntRate, FCGrcDftIntFrq, FCGrcIsIntCpz, FCGrcCpzFrq, FCGrcIsRvwAlw, FCGrcRvwFrq, FCMinTerm, FCMaxTerm, FCDftTerms, FCRpyFrq, FCRepayMethod, FCIsAlwPartialRpy, FCIsAlwDifferment, FCMaxDifferment, FCIsAlwFrqDifferment, FCMaxFrqDifferment, FCIsAlwEarlyRpy, FCIsAlwEarlySettle, FCIsDwPayRequired, FCRvwRateApplFor, FCAlwRateChangeAnyDate, FCGrcRvwRateApplFor, FCIsIntCpzAtGrcEnd, FCGrcAlwRateChgAnyDate, FCMinDownPayAmount, FCSchCalCodeOnRvw");
		insertSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:FCCode, :FCDesc, :FCFinType, :FCIsAlwMD, :FCIsAlwGrace, :FCOrgPrfUnchanged, :FCRateType, :FCBaseRate, :FCSplRate, :FCIntRate, :FCDftIntFrq, :FCIsIntCpz, :FCCpzFrq, :FCIsRvwAlw, :FCRvwFrq, :FCGrcRateType, :FCGrcBaseRate, :FCGrcSplRate, :FCGrcIntRate, :FCGrcDftIntFrq, :FCGrcIsIntCpz, :FCGrcCpzFrq, :FCGrcIsRvwAlw, :FCGrcRvwFrq, :FCMinTerm, :FCMaxTerm, :FCDftTerms, :FCRpyFrq, :FCRepayMethod, :FCIsAlwPartialRpy, :FCIsAlwDifferment, :FCMaxDifferment, :FCIsAlwFrqDifferment, :FCMaxFrqDifferment, :FCIsAlwEarlyRpy, :FCIsAlwEarlySettle, :FCIsDwPayRequired, :FCRvwRateApplFor, :FCAlwRateChangeAnyDate, :FCGrcRvwRateApplFor, :FCIsIntCpzAtGrcEnd, :FCGrcAlwRateChgAnyDate, :FCMinDownPayAmount, :FCSchCalCodeOnRvw");
		insertSql.append(", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeCampaign);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return financeCampaign.getId();
	}
	
	/**
	 * This method updates the Record FinanceCampaign or FinanceCampaign_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Finance Campaign by key FCCode and Version
	 * 
	 * @param Finance Campaign (financeCampaign)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@SuppressWarnings("serial")
	@Override
	public void update(FinanceCampaign financeCampaign,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update FinanceCampaign");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set FCCode = :FCCode, FCDesc = :FCDesc, FCFinType = :FCFinType, FCIsAlwMD = :FCIsAlwMD, FCIsAlwGrace = :FCIsAlwGrace, FCOrgPrfUnchanged = :FCOrgPrfUnchanged, FCRateType = :FCRateType, FCBaseRate = :FCBaseRate, FCSplRate = :FCSplRate, FCIntRate = :FCIntRate, FCDftIntFrq = :FCDftIntFrq, FCIsIntCpz = :FCIsIntCpz, FCCpzFrq = :FCCpzFrq, FCIsRvwAlw = :FCIsRvwAlw, FCRvwFrq = :FCRvwFrq, FCGrcRateType = :FCGrcRateType, FCGrcBaseRate = :FCGrcBaseRate, FCGrcSplRate = :FCGrcSplRate, FCGrcIntRate = :FCGrcIntRate, FCGrcDftIntFrq = :FCGrcDftIntFrq, FCGrcIsIntCpz = :FCGrcIsIntCpz, FCGrcCpzFrq = :FCGrcCpzFrq, FCGrcIsRvwAlw = :FCGrcIsRvwAlw, FCGrcRvwFrq = :FCGrcRvwFrq, FCMinTerm = :FCMinTerm, FCMaxTerm = :FCMaxTerm, FCDftTerms = :FCDftTerms, FCRpyFrq = :FCRpyFrq, FCRepayMethod = :FCRepayMethod, FCIsAlwPartialRpy = :FCIsAlwPartialRpy, FCIsAlwDifferment = :FCIsAlwDifferment, FCMaxDifferment = :FCMaxDifferment, FCIsAlwFrqDifferment = :FCIsAlwFrqDifferment, FCMaxFrqDifferment = :FCMaxFrqDifferment, FCIsAlwEarlyRpy = :FCIsAlwEarlyRpy, FCIsAlwEarlySettle = :FCIsAlwEarlySettle, FCIsDwPayRequired = :FCIsDwPayRequired, FCRvwRateApplFor = :FCRvwRateApplFor, FCAlwRateChangeAnyDate = :FCAlwRateChangeAnyDate, FCGrcRvwRateApplFor = :FCGrcRvwRateApplFor, FCIsIntCpzAtGrcEnd = :FCIsIntCpzAtGrcEnd, FCGrcAlwRateChgAnyDate = :FCGrcAlwRateChgAnyDate, FCMinDownPayAmount = :FCMinDownPayAmount, FCSchCalCodeOnRvw = :FCSchCalCodeOnRvw");
		updateSql.append(", Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where FCCode =:FCCode");
		
		if (!type.endsWith("_TEMP")){
			updateSql.append("  AND Version= :Version-1");
		}
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeCampaign);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :"+recordCount);
			ErrorDetails errorDetails= getError("41004",financeCampaign.getId() ,financeCampaign.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	private ErrorDetails  getError(String errorId, String FCCode, String userLanguage){
		String[][] parms= new String[2][1];
		parms[1][0] = FCCode;
		parms[0][0] = PennantJavaUtil.getLabel("label_FCCode")+ ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId, parms[0],parms[1]), userLanguage);
	}

	
}