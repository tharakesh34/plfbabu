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
 * FileName    		:  FinBillingDetailDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  30-01-2013    														*
 *                                                                  						*
 * Modified Date    :  30-01-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2011       Pennant	                 0.1                                            * 
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

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.finance.FinBillingDetailDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.finance.FinBillingDetail;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>FinBillingDetail model</b> class.<br>
 * 
 */
public class FinBillingDetailDAOImpl extends BasisCodeDAO<FinBillingDetail> implements FinBillingDetailDAO {

	private static Logger logger = Logger.getLogger(FinBillingDetailDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	/**
	 * This method set the Work Flow id based on the module name and return the new FinBillingDetail 
	 * @return FinBillingDetail
	 */
	@Override
	public FinBillingDetail getFinBillingDetail() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("FinBillingDetail");
		FinBillingDetail billingDetail= new FinBillingDetail();
		if (workFlowDetails!=null){
			billingDetail.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return billingDetail;
	}

	/**
	 * This method get the module from method getFinBillingDetail() and set the
	 * new record flag as true and return FinBillingDetail()
	 * 
	 * @return FinBillingDetail
	 */
	@Override
	public FinBillingDetail getNewFinBillingDetail() {
		logger.debug("Entering");
		FinBillingDetail billingDetail = getFinBillingDetail();
		billingDetail.setNewRecord(true);
		logger.debug("Leaving");
		return billingDetail;
	}

	/**
	 * Fetch the Record  Finance Billing Details by key field
	 */
	@Override
	public FinBillingDetail getFinBillingDetailByID(final String finReference, Date progClaimDate,String type) {
		logger.debug("Entering");
		
		FinBillingDetail billingDetail = getFinBillingDetail();
		billingDetail.setFinReference(finReference);
		billingDetail.setProgClaimDate(progClaimDate);
		
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT FinReference , ProgClaimDate , ProgClaimAmount , ProgClaimBilled," );
		if(type.contains("View")){
			selectSql.append(" LovDescFinFormatter, ");
		}
		selectSql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode," );
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId" );
		selectSql.append(" FROM  FinBillingDetail") ;
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference = :FinReference AND ProgClaimDate = :ProgClaimDate") ;
				
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(billingDetail);
		RowMapper<FinBillingDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(
				FinBillingDetail.class);

		try{
			billingDetail = this.namedParameterJdbcTemplate.queryForObject(
					selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			billingDetail = null;
		}
		logger.debug("Leaving");
		return billingDetail;
	}

	/** 
	 * Method For getting List of Finance Billing Details
	 */
	public List<FinBillingDetail> getFinBillingDetailByFinRef(final String finReference,String type) {
		logger.debug("Entering");
		FinBillingDetail billingDetail = getFinBillingDetail();
		billingDetail.setFinReference(finReference);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT FinReference , ProgClaimDate , ProgClaimAmount ,ProgClaimBilled, " );
		if(type.contains("View")){
			selectSql.append(" LovDescFinFormatter, ");
		}
		selectSql.append(" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode," );
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId" );
		selectSql.append(" FROM  FinBillingDetail");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference = :FinReference ") ;
				
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(billingDetail);
		RowMapper<FinBillingDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinBillingDetail.class);

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,typeRowMapper);
	}

	/**
	 * This method initialize the Record.
	 * @param FinBillingDetail (billingDetail)
	 * @return FinBillingDetail
	 */
	@Override
	public void initialize(FinBillingDetail billingDetail) {
		super.initialize(billingDetail);
	}

	/**
	 * This method refresh the Record.
	 * @param FinBillingDetail (billingDetail)
	 * @return void
	 */
	@Override
	public void refresh(FinBillingDetail billingDetail) {

	}

	/**
	 * @param dataSource the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the FinBillingDetails or FinBillingDetails_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Billing Details List by FinReference
	 * 
	 * @param Billing Details (billingDetail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(FinBillingDetail billingDetail,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append("Delete From FinBillingDetail" );
		deleteSql.append(StringUtils.trimToEmpty(type) );
		deleteSql.append(" Where FinReference =:FinReference AND ProgClaimDate =:ProgClaimDate");
		
		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(billingDetail);
		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				ErrorDetails errorDetails= getError("41003", billingDetail.getFinReference(),
						billingDetail.getProgClaimDate(), billingDetail.getUserDetails().getUsrLanguage()); 
				throw new DataAccessException(errorDetails.getError()) {};
			}
		} catch (DataAccessException e) {
			logger.error(e);
			ErrorDetails errorDetails= getError("41006", billingDetail.getFinReference(),
					billingDetail.getProgClaimDate(), billingDetail.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Deletion of Customer Related List of FinBillingDetails for the Customer
	 */
	public void deleteByFinRef(final String finReference,String type) {
		logger.debug("Entering");
		FinBillingDetail billingDetail = getFinBillingDetail();
		billingDetail.setFinReference(finReference);

		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append("Delete From FinBillingDetail" );
		deleteSql.append(StringUtils.trimToEmpty(type) );
		deleteSql.append(" Where FinReference =:FinReference ");
		
		logger.debug("deleteSql: "+ deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(billingDetail);
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into FinBillingDetails or FinBillingDetails_Temp.
	 *
	 * save Customer Ratings 
	 * 
	 * @param Customer Ratings (billingDetail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void save(FinBillingDetail billingDetail,String type) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder();
		insertSql.append(" Insert Into FinBillingDetail" );
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (FinReference , ProgClaimDate , ProgClaimAmount ,ProgClaimBilled, " );
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)" );
		insertSql.append(" VALUES (:FinReference , :ProgClaimDate , :ProgClaimAmount , :ProgClaimBilled," );
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode," );
		insertSql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(billingDetail);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
	}

	/**
	 * This method updates the Record FinBillingDetails or FinBillingDetails_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Billing Details by key FinReference and Version
	 * 
	 * @param Customer Ratings (billingDetail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	@Override
	public void update(FinBillingDetail billingDetail,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		
		StringBuilder updateSql = new StringBuilder();
		updateSql.append("Update FinBillingDetail" );
		updateSql.append(StringUtils.trimToEmpty(type) ); 
		updateSql.append(" Set FinReference=:FinReference , ProgClaimDate=:ProgClaimDate , " );
		updateSql.append(" ProgClaimAmount=:ProgClaimAmount , ProgClaimBilled=:ProgClaimBilled," );
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, " );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode,NextRoleCode = :NextRoleCode,");
		updateSql.append(" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType," );
		updateSql.append(" WorkflowId = :WorkflowId" );
		updateSql.append(" Where FinReference =:FinReference and ProgClaimDate = :ProgClaimDate ");

		if (!type.endsWith("_TEMP")){
			updateSql.append(" AND Version= :Version-1 ");
		}

		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(billingDetail);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :" + recordCount);
			ErrorDetails errorDetails= getError("41004", billingDetail.getFinReference(),
					billingDetail.getProgClaimDate(), billingDetail.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Method for update Billing after progress Claim Amount is Claimed
	 */
	@SuppressWarnings("serial")
	@Override
	public void updateClaim(FinBillingDetail billingDetail,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		
		StringBuilder updateSql = new StringBuilder();
		updateSql.append("Update FinBillingDetail" );
		updateSql.append(StringUtils.trimToEmpty(type) ); 
		updateSql.append(" Set ProgClaimBilled =:ProgClaimBilled " );
		updateSql.append(" Where FinReference =:FinReference and ProgClaimDate = :ProgClaimDate ");

		logger.debug("updateSql: "+ updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(billingDetail);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :" + recordCount);
			ErrorDetails errorDetails= getError("41004", billingDetail.getFinReference(),
					billingDetail.getProgClaimDate(), billingDetail.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	private ErrorDetails  getError(String errorId, String finReference,Date progClaimDate, String userLanguage){
		String[][] parms= new String[2][2]; 
		
		parms[1][0] = finReference;
		parms[1][1] = DateUtility.formatDate(progClaimDate, PennantConstants.dateFormat);

		parms[0][0] = PennantJavaUtil.getLabel("label_FinReference")+ ":" + parms[1][0];
		parms[0][1] = PennantJavaUtil.getLabel("label_ProgClaimDate")+ ":" + parms[1][1];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, 
				errorId, parms[0],parms[1]), userLanguage);
	}

}