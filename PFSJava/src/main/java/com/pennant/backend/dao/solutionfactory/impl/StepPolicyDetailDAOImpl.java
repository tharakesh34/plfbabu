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
 * FileName    		:  StepPolicyDetailDAOImpl.java                                         * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  30-06-2011    														*
 *                                                                  						*
 * Modified Date    :  30-06-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 30-06-2011       Pennant	                 0.1                                            * 
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

import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.dao.solutionfactory.StepPolicyDetailDAO;
import com.pennant.backend.model.solutionfactory.StepPolicyDetail;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>StepPolicyDetail model</b> class.<br>
 * 
 */
public class StepPolicyDetailDAOImpl extends BasisCodeDAO<StepPolicyDetail> implements StepPolicyDetailDAO {

	private static Logger logger = Logger.getLogger(StepPolicyDetailDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate	namedParameterJdbcTemplate;

	public StepPolicyDetailDAOImpl() {
		super();
	}
	
	/**
	 * This method set the Work Flow id based on the module name and return the new StepPolicyDetail
	 * 
	 * @return StepPolicyDetail
	 */
	@Override
	public StepPolicyDetail getStepPolicyDetail() {
		logger.debug("Entering");
		StepPolicyDetail stepPolicyDetail = new StepPolicyDetail("");
		logger.debug("Leaving");
		return stepPolicyDetail;
	}

	/**
	 * This method get the module from method getStepPolicyDetail() and set the new record flag as true and
	 * return StepPolicyDetail()
	 * 
	 * @return StepPolicyDetail
	 */
	@Override
	public StepPolicyDetail getNewStepPolicyDetail() {
		logger.debug("Entering");
		StepPolicyDetail stepPolicyDetail = getStepPolicyDetail();
		stepPolicyDetail.setNewRecord(true);
		logger.debug("Leaving");
		return stepPolicyDetail;
	}



	/**
	 * Fetch the Record Finance Types details by key field
	 * 
	 * @param id
	 *         (String)
	 * @param type
	 *         (String) ""/_Temp/_View
	 * @return StepPolicyDetail
	 */
	@Override
	public List<StepPolicyDetail> getStepPolicyDetailListByID(final String id, String type) {
		logger.debug("Entering");
		StepPolicyDetail stepPolicyDetail = new StepPolicyDetail();
		stepPolicyDetail.setId(id);

		StringBuilder selectSql = new StringBuilder("SELECT PolicyCode, StepNumber, TenorSplitPerc, RateMargin, EMISplitPerc, ");
		selectSql.append(" Version, LastMntBy, LastMntOn, RecordStatus,");
		selectSql.append(" RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" FROM StepPolicyDetail");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where PolicyCode = :PolicyCode");

		logger.debug("selectListSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(stepPolicyDetail);
		RowMapper<StepPolicyDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(StepPolicyDetail.class);
		
		List<StepPolicyDetail> StepPolicyDetails = this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
		logger.debug("Leaving");
		return StepPolicyDetails;
	}
	
	
	/**
	 * Fetch the Record Finance Types details by key field
	 * 
	 * @param id
	 *         (String)
	 * @param type
	 *         (String) ""/_Temp/_View
	 * @return StepPolicyDetail
	 */
	@Override
	public StepPolicyDetail getStepPolicyDetailByID(StepPolicyDetail stepPolicyDetail, String type) {
		logger.debug("Entering");

		StringBuilder selectSql = new StringBuilder("SELECT PolicyCode, StepNumber, TenorSplitPerc, RateMargin, EMISplitPerc, ");
		selectSql.append(" Version, LastMntBy, LastMntOn, RecordStatus,");
		selectSql.append(" RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" FROM StepPolicyDetail");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where PolicyCode = :PolicyCode And StepNumber = :StepNumber");

		logger.debug("selectListSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(stepPolicyDetail);
		RowMapper<StepPolicyDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(StepPolicyDetail.class);

		try {
			stepPolicyDetail = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			stepPolicyDetail = null;
		}
		logger.debug("Leaving");
		return stepPolicyDetail;
	}

	/**
	 * @param dataSource
	 *         the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method insert new Records into StepPolicyDetails or StepPolicyDetails_Temp.
	 * 
	 * save Finance Types
	 * 
	 * @param Finance
	 *         Types (StepPolicyDetail)
	 * @param type
	 *         (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public String save(StepPolicyDetail stepPolicyDetail, String type) {
		logger.debug("Entering ");
		
		StringBuilder insertSql = new StringBuilder("Insert Into StepPolicyDetail" );
		insertSql.append(StringUtils.trimToEmpty(type) );
		insertSql.append(" (PolicyCode, StepNumber, TenorSplitPerc, RateMargin, EmiSplitPerc," );
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode," );
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId)" );
		insertSql.append(" Values(:PolicyCode, :StepNumber, :TenorSplitPerc, :RateMargin, :EmiSplitPerc," );
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode," );
		insertSql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		
		logger.debug("insertSql: "+ insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(stepPolicyDetail);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving ");
		return stepPolicyDetail.getId();
	}

	/**
	 * This method updates the Record StepPolicyDetails or StepPolicyDetails_Temp. if Record not updated
	 * then throws DataAccessException with error 41004. update Finance Types by key FinType and
	 * Version
	 * 
	 * @param Finance
	 *         Types (StepPolicyDetail)
	 * @param type
	 *         (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public void update(StepPolicyDetail stepPolicyDetail, String type) {
		int recordCount = 0;
		logger.debug("Entering ");
		
		StringBuilder updateSql = new StringBuilder("Update StepPolicyDetail" );
		updateSql.append(StringUtils.trimToEmpty(type) ); 
		updateSql.append(" Set TenorSplitPerc = :TenorSplitPerc,");
		updateSql.append(" RateMargin = :RateMargin,EmiSplitPerc = :EmiSplitPerc,");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn," );
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode," );
		updateSql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId," );
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId" );
		updateSql.append(" Where PolicyCode =:PolicyCode and StepNumber=:StepNumber");
		
		if (!type.endsWith("_Temp")){
			updateSql.append("  AND Version= :Version-1");
		}
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(stepPolicyDetail);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving ");
	}
	
	
	/**
	 * This method Deletes the Record from the StepPolicyDetails or StepPolicyDetails_Temp. if Record not
	 * deleted then throws DataAccessException with error 41003. delete Finance Types by key FinType
	 * 
	 * @param Finance
	 *         Types (StepPolicyDetail)
	 * @param type
	 *         (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(StepPolicyDetail stepPolicyDetail,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete From StepPolicyDetail");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append("  Where PolicyCode =:PolicyCode And StepNumber =:StepNumber");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(stepPolicyDetail);
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
	 * This method initialize the Record.
	 * 
	 * @param StepPolicyDetail
	 *         (StepPolicyDetail)
	 * @return StepPolicyDetail
	 */
	@Override
	public void deleteByPolicyCode(String policyCode, String type) {
		logger.debug("Entering");
		StepPolicyDetail stepPolicyDetail = new StepPolicyDetail();
		stepPolicyDetail.setPolicyCode(policyCode);
		StringBuilder deleteSql = new StringBuilder("Delete From StepPolicyDetail");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where PolicyCode =:PolicyCode");
		
		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(stepPolicyDetail);
		try {
			this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
		} catch (DataAccessException e) {
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");
	}
}