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
 * FileName    		:  FinAgreementDetailDAOImpl.java                                          * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  04-02-2012    														*
 *                                                                  						*
 * Modified Date    :  04-02-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 04-02-2012       Pennant	                 0.1                                            * 
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

import com.pennant.backend.dao.finance.FinAgreementDetailDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.finance.FinAgreementDetail;

public class FinAgreementDetailDAOImpl  extends BasisCodeDAO<FinAgreementDetail> implements FinAgreementDetailDAO{

	private static Logger logger = Logger.getLogger(FinAgreementDetailDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	public FinAgreementDetailDAOImpl(){
		super();
	}
	
	/**
	 * This method set the Work Flow id based on the module name and return the new FinAgreementDetail 
	 * @return FinAgreementDetail
	 */

	@Override
	public FinAgreementDetail getFinAgreementDetail() {
		return new FinAgreementDetail();
	}

	/**
	 * This method get the module from method getFinAgreementDetail() 
	 * and set the new record flag as true and return FinAgreementDetail()
	 * 
	 * @return FinAgreementDetail
	 */
	@Override
	public FinAgreementDetail getNewFinAgreementDetail() {
		logger.debug("Entering");
		FinAgreementDetail agreementDetail = getFinAgreementDetail();
		logger.debug("Leaving");
		return agreementDetail;
	}
	
	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	@Override
	public FinAgreementDetail getFinAgreementDetailById(String finReference, long agrId, String type) {
		logger.debug("Entering");
		
		FinAgreementDetail finAgreementDetail = new FinAgreementDetail();
		finAgreementDetail.setFinReference(finReference);
		finAgreementDetail.setAgrId(agrId);

		StringBuilder selectSql = new StringBuilder(" SELECT FinReference ,AgrId ,FinType,AgrName ,AgrContent , " );
		selectSql.append(" Version ,LastMntBy ,LastMntOn ,RecordStatus ,RoleCode ," );
		selectSql.append(" NextRoleCode ,TaskId ,NextTaskId ,RecordType ,WorkflowId ");
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(" , LovDescAgrName ");
		}
		selectSql.append(" FROM FinAgreementDetail");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference AND AgrId =:AgrId ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finAgreementDetail);
		RowMapper<FinAgreementDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinAgreementDetail.class);

		try {
			finAgreementDetail = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			finAgreementDetail = null;
		}
		logger.debug("Leaving");
		return finAgreementDetail;
	}
	
	/**
	 * This method insert new Records into FinAgreementDetail .
	 *
	 * save FinAgreementDetail 
	 * 
	 * @param FinAgreementDetail (finAgreementDetail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public long save(FinAgreementDetail finAgreementDetail,String type) {
		logger.debug("Entering");
		
		StringBuilder insertSql =new StringBuilder("Insert Into FinAgreementDetail");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (FinReference ,AgrId ,FinType, AgrName ,AgrContent ,  ");
		insertSql.append(" Version ,LastMntBy ,LastMntOn ,RecordStatus ,RoleCode ," );
		insertSql.append(" NextRoleCode ,TaskId ,NextTaskId ,RecordType ,WorkflowId ) ");
		insertSql.append(" Values(:FinReference ,:AgrId ,:FinType, :AgrName ,:AgrContent , ");
		insertSql.append(" :Version ,:LastMntBy ,:LastMntOn ,:RecordStatus ,:RoleCode ," );
		insertSql.append(" :NextRoleCode ,:TaskId ,:NextTaskId ,:RecordType ,:WorkflowId) ");
		
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finAgreementDetail);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return finAgreementDetail.getAgrId();
	}
	
	/**
	 * This method updates the Record FinAgreementDetail .
	 * update FinAgreementDetail
	 * 
	 * @param FinAgreementDetail (finAgreementDetail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(FinAgreementDetail finAgreementDetail,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update FinAgreementDetail");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" SET FinType=:FinType, AgrName =:AgrName ,AgrContent =:AgrContent , " );
		updateSql.append(" Version =:Version ,LastMntBy =:LastMntBy ,LastMntOn =:LastMntOn ,RecordStatus =:RecordStatus , " );
		updateSql.append(" RoleCode =:RoleCode , NextRoleCode =:NextRoleCode ,TaskId =:TaskId ," );
		updateSql.append(" NextTaskId =:NextTaskId ,RecordType =:RecordType ,WorkflowId =:WorkflowId ");
		updateSql.append(" Where FinReference =:FinReference AND AgrId =:AgrId");
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finAgreementDetail);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :"+recordCount);
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Deletion of Agreement Detail object
	 */
	@Override
	public void delete(FinAgreementDetail finAgreementDetail,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Delete From FinAgreementDetail");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Where FinReference =:FinReference AND AgrId =:AgrId");
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finAgreementDetail);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :"+recordCount);
		}
		logger.debug("Leaving");
	}
	
	@Override
	public void deleteByFinRef(String finReference,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		
		FinAgreementDetail finAgreementDetail = new FinAgreementDetail();
		finAgreementDetail.setFinReference(finReference);
		
		StringBuilder	updateSql =new StringBuilder("Delete From FinAgreementDetail");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Where FinReference =:FinReference ");
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finAgreementDetail);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :"+recordCount);
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Fetch Finance Agreement Details List
	 */
	@Override
	public List<FinAgreementDetail> getFinAgreementDetailList(String finReference, String finType,
			boolean isAgrsExist, String agrIds, String type) {
		logger.debug("Entering");
		
		FinAgreementDetail agreementDetail = new FinAgreementDetail();
		agreementDetail.setFinReference(finReference);
		agreementDetail.setFinType(finType);
		
		StringBuilder selectSql = new StringBuilder(" SELECT FinReference ,AgrId ,FinType, AgrName ,AgrContent , " );
		selectSql.append(" Version ,LastMntBy ,LastMntOn ,RecordStatus ,RoleCode ," );
		selectSql.append(" NextRoleCode ,TaskId ,NextTaskId ,RecordType ,WorkflowId ");
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(" ,LovDescAgrName ");
		}
		selectSql.append(" FROM FinAgreementDetail");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference AND FinType=:FinType" );
		if(isAgrsExist){
			selectSql.append(" AND AgrId IN ("+agrIds+ ")");
		}else{
			selectSql.append(" AND AgrId NOT IN ("+agrIds+ ")");
		}
		
		logger.debug("selectSql: " + selectSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(agreementDetail);
		RowMapper<FinAgreementDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinAgreementDetail.class);
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,typeRowMapper);
	}

	@Override
    public List<FinAgreementDetail> getFinAgrByFinRef(String finReference, String type) {
		logger.debug("Entering");
		
		FinAgreementDetail agreementDetail = new FinAgreementDetail();
		agreementDetail.setFinReference(finReference);
		
		StringBuilder selectSql = new StringBuilder(" SELECT AgrId , AgrName " );
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(" ,LovDescAgrName ");
		}
		selectSql.append(" FROM FinAgreementDetail");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference " );
		
		logger.debug("selectSql: " + selectSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(agreementDetail);
		RowMapper<FinAgreementDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinAgreementDetail.class);
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,typeRowMapper);
    }

}
