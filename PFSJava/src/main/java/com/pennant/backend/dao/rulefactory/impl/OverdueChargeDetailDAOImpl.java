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
 * FileName    		:  OverdueChargeDetailDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  10-05-2012    														*
 *                                                                  						*
 * Modified Date    :  10-05-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 10-05-2012       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.rulefactory.impl;

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
import com.pennant.backend.dao.rulefactory.OverdueChargeDetailDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.rulefactory.OverdueChargeDetail;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pff.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>OverdueChargeDetail model</b> class.<br>
 * 
 */

public class OverdueChargeDetailDAOImpl extends BasisCodeDAO<OverdueChargeDetail> implements OverdueChargeDetailDAO {

	private static Logger logger = Logger.getLogger(OverdueChargeDetailDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public OverdueChargeDetailDAOImpl() {
		super();
	}
	
	/**
	 * This method set the Work Flow id based on the module name and return the
	 * new OverdueChargeDetail
	 * 
	 * @return OverdueChargeDetail
	 */

	@Override
	public OverdueChargeDetail getOverdueChargeDetail() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("OverdueChargeDetail");
		OverdueChargeDetail overdueChargeDetail = new OverdueChargeDetail();
		if (workFlowDetails != null) {
			overdueChargeDetail.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return overdueChargeDetail;
	}

	/**
	 * This method get the module from method getOverdueChargeDetail() and set
	 * the new record flag as true and return OverdueChargeDetail()
	 * 
	 * @return OverdueChargeDetail
	 */

	@Override
	public OverdueChargeDetail getNewOverdueChargeDetail() {
		logger.debug("Entering");
		OverdueChargeDetail overdueChargeDetail = getOverdueChargeDetail();
		overdueChargeDetail.setNewRecord(true);
		logger.debug("Leaving");
		return overdueChargeDetail;
	}

	/**
	 * Fetch the Record Overdue Charge Detail details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return OverdueChargeDetail
	 */
	@Override
	public OverdueChargeDetail getOverdueChargeDetailById(final String ruleCode, final String ctgCode, String type) {
		logger.debug("Entering");
		OverdueChargeDetail overdueChargeDetail = new OverdueChargeDetail();

		overdueChargeDetail.setId(ruleCode);
		overdueChargeDetail.setoDCCustCtg(ctgCode);

		StringBuilder selectSql = new StringBuilder("Select ODCRuleCode, ODCCustCtg, ODCType, ODCOn, ODCAmount,");
		selectSql.append(" ODCGraceDays, ODCAllowWaiver, ODCMaxWaiver");
		selectSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId,");
		selectSql.append(" NextTaskId, RecordType, WorkflowId");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(",lovDescODCCustCtgName,lovDescODCTypeName");
		}
		selectSql.append(" From FinODCDetails");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where ODCRuleCode =:ODCRuleCode AND ODCCustCtg =:ODCCustCtg ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(overdueChargeDetail);
		RowMapper<OverdueChargeDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(OverdueChargeDetail.class);

		try {
			overdueChargeDetail = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			overdueChargeDetail = null;
		}
		logger.debug("Leaving");
		return overdueChargeDetail;
	}

	/**
	 * To Set dataSource
	 * 
	 * @param dataSource
	 */

	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * This method Deletes the Record from the FinODCDetails or
	 * FinODCDetails_Temp. if Record not deleted then throws DataAccessException
	 * with error 41003. delete Overdue Charge Detail by key ODCRuleCode
	 * 
	 * @param Overdue
	 *            Charge Detail (overdueChargeDetail)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(OverdueChargeDetail overdueChargeDetail, String type) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder("Delete From FinODCDetails");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where ODCRuleCode =:ODCRuleCode");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(overdueChargeDetail);
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
	 * This method insert new Records into FinODCDetails or FinODCDetails_Temp.
	 * 
	 * save Overdue Charge Detail
	 * 
	 * @param Overdue
	 *            Charge Detail (overdueChargeDetail)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public String save(OverdueChargeDetail overdueChargeDetail, String type) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into FinODCDetails");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (ODCRuleCode, ODCCustCtg, ODCType, ODCOn, ODCAmount, ODCGraceDays, ODCAllowWaiver, ODCMaxWaiver");
		insertSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:ODCRuleCode, :ODCCustCtg, :ODCType, :ODCOn, :ODCAmount, :ODCGraceDays, :ODCAllowWaiver, :ODCMaxWaiver");
		insertSql.append(", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(overdueChargeDetail);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return overdueChargeDetail.getId();
	}

	/**
	 * This method updates the Record FinODCDetails or FinODCDetails_Temp. if
	 * Record not updated then throws DataAccessException with error 41004.
	 * update Overdue Charge Detail by key ODCRuleCode and Version
	 * 
	 * @param Overdue
	 *            Charge Detail (overdueChargeDetail)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public void update(OverdueChargeDetail overdueChargeDetail, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update FinODCDetails");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set ODCType = :ODCType, ODCOn = :ODCOn,");
		updateSql.append(" ODCAmount = :ODCAmount, ODCGraceDays = :ODCGraceDays, ODCAllowWaiver = :ODCAllowWaiver, ODCMaxWaiver = :ODCMaxWaiver");
		updateSql.append(", Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus,");
		updateSql.append(" RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where ODCRuleCode =:ODCRuleCode AND ODCCustCtg = :ODCCustCtg");

		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(overdueChargeDetail);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	/**
	 * Fetch the Record Overdue Charge List details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return OverdueChargeDetail List
	 */
	@Override
	public List<OverdueChargeDetail> getListOverdueChargeDetailById(String id, String type) {
		logger.debug("Entering");

		OverdueChargeDetail overdueChargeDetail = new OverdueChargeDetail();
		overdueChargeDetail.setoDCRuleCode(id);

		StringBuilder selectSql = new StringBuilder("Select ODCRuleCode, ODCCustCtg, ODCType, ODCOn,");
		selectSql.append(" ODCAmount, ODCGraceDays, ODCAllowWaiver, ODCMaxWaiver");
		selectSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId");
		if (StringUtils.trimToEmpty(type).contains("View")) {
		}
		selectSql.append(" From FinODCDetails");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where ODCRuleCode =:ODCRuleCode");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(overdueChargeDetail);
		RowMapper<OverdueChargeDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(OverdueChargeDetail.class);
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}
}