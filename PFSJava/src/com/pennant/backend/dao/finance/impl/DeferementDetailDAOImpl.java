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
 * FileName    		:  DeferementDetailDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  02-12-2011    														*
 *                                                                  						*
 * Modified Date    :  02-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 02-12-2011       Pennant	                 0.1                                            * 
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
import com.pennant.backend.dao.finance.DeferementDetailDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.finance.DeferementDetail;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>DeferementDetail model</b> class.<br>
 * 
 */

public class DeferementDetailDAOImpl extends BasisCodeDAO<DeferementDetail> implements DeferementDetailDAO {

	private static Logger logger = Logger.getLogger(DeferementDetailDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	/**
	 * This method set the Work Flow id based on the module name and return the new DeferementDetail 
	 * @return DeferementDetail
	 */

	@Override
	public DeferementDetail getDeferementDetail() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("DeferementDetail");
		DeferementDetail deferementDetail= new DeferementDetail();
		if (workFlowDetails!=null){
			deferementDetail.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return deferementDetail;
	}


	/**
	 * This method get the module from method getDeferementDetail() and set the new record flag as true and return DeferementDetail()   
	 * @return DeferementDetail
	 */


	@Override
	public DeferementDetail getNewDeferementDetail() {
		logger.debug("Entering");
		DeferementDetail deferementDetail = getDeferementDetail();
		deferementDetail.setNewRecord(true);
		logger.debug("Leaving");
		return deferementDetail;
	}

	/**
	 * Fetch the Record  Deferement Details details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return DeferementDetail
	 */
	@Override
	public DeferementDetail getDeferementDetailById(final String id, String type) {
		logger.debug("Entering");
		DeferementDetail deferementDetail = getDeferementDetail();
		
		deferementDetail.setId(id);
		
		StringBuilder selectSql = new StringBuilder("Select FinReference, DeferedSchdDate, DefSchdProfit, DefSchdPrincipal, DeferedRpyDate, DefRpySchdPft, DefRpySchdPri, DefRpySchdPftBal, DefRpySchdPriBal, DefPaidPftTillDate, DefPaidPriTillDate, DefPftBalance, DefPriBalance");
		selectSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(",lovDescFinReferenceName,");
		}
		selectSql.append(" From FinDefermentDetail");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(deferementDetail);
		RowMapper<DeferementDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(DeferementDetail.class);
		
		try{
			deferementDetail = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			deferementDetail = null;
		}
		logger.debug("Leaving");
		return deferementDetail;
	}
	
	/**
	 * This method initialise the Record.
	 * @param DeferementDetail (deferementDetail)
 	 * @return DeferementDetail
	 */
	@Override
	public void initialize(DeferementDetail deferementDetail) {
		super.initialize(deferementDetail);
	}
	/**
	 * This method refresh the Record.
	 * @param DeferementDetail (deferementDetail)
 	 * @return void
	 */
	@Override
	public void refresh(DeferementDetail deferementDetail) {
		
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the FinDefermentDetail or FinDefermentDetail_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Deferement Details by key FinReference
	 * 
	 * @param Deferement Details (deferementDetail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(DeferementDetail deferementDetail,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete From FinDefermentDetail");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FinReference =:FinReference");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(deferementDetail);
		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				ErrorDetails errorDetails= getError("41003",deferementDetail.getId() ,deferementDetail.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {};
			}
		}catch(DataAccessException e){
			logger.error(e);
			ErrorDetails errorDetails= getError("41006",deferementDetail.getId() ,deferementDetail.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	/**
	 * This method insert new Records into FinDefermentDetail or FinDefermentDetail_Temp.
	 *
	 * save Deferement Details 
	 * 
	 * @param Deferement Details (deferementDetail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@Override
	public String save(DeferementDetail deferementDetail,String type) {
		logger.debug("Entering");
		
		StringBuilder insertSql =new StringBuilder("Insert Into FinDefermentDetail");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (FinReference, DeferedSchdDate, DefSchdProfit, DefSchdPrincipal, DeferedRpyDate, DefRpySchdPft, DefRpySchdPri, DefRpySchdPftBal, DefRpySchdPriBal, DefPaidPftTillDate, DefPaidPriTillDate, DefPftBalance, DefPriBalance");
		insertSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:FinReference, :DeferedSchdDate, :DefSchdProfit, :DefSchdPrincipal, :DeferedRpyDate, :DefRpySchdPft, :DefRpySchdPri, :DefRpySchdPftBal, :DefRpySchdPriBal, :DefPaidPftTillDate, :DefPaidPriTillDate, :DefPftBalance, :DefPriBalance");
		insertSql.append(", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(deferementDetail);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return deferementDetail.getId();
	}
	
	/**
	 * This method updates the Record FinDefermentDetail or FinDefermentDetail_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Deferement Details by key FinReference and Version
	 * 
	 * @param Deferement Details (deferementDetail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@SuppressWarnings("serial")
	@Override
	public void update(DeferementDetail deferementDetail,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update FinDefermentDetail");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set FinReference = :FinReference, DeferedSchdDate = :DeferedSchdDate, DefSchdProfit = :DefSchdProfit, DefSchdPrincipal = :DefSchdPrincipal, DeferedRpyDate = :DeferedRpyDate, DefRpySchdPft = :DefRpySchdPft, DefRpySchdPri = :DefRpySchdPri, DefRpySchdPftBal = :DefRpySchdPftBal, DefRpySchdPriBal = :DefRpySchdPriBal, DefPaidPftTillDate = :DefPaidPftTillDate, DefPaidPriTillDate = :DefPaidPriTillDate, DefPftBalance = :DefPftBalance, DefPriBalance = :DefPriBalance");
		updateSql.append(", Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where FinReference =:FinReference");
		
		if (!type.endsWith("_TEMP")){
			updateSql.append("  AND Version= :Version-1");
		}
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(deferementDetail);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :"+recordCount);
			ErrorDetails errorDetails= getError("41004",deferementDetail.getId() ,deferementDetail.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	private ErrorDetails  getError(String errorId, String FinReference, String userLanguage){
		String[][] parms= new String[2][1];
		parms[1][0] = FinReference;
		parms[0][0] = PennantJavaUtil.getLabel("label_FinReference")+ ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId, parms[0],parms[1]), userLanguage);
	}

	
}