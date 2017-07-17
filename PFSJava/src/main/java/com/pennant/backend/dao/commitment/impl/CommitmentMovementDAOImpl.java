/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ******************************************************************************************** 
 * FILE HEADER *
 ******************************************************************************************** 
 * * FileName : CommitmentDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 25-03-2013 * * Modified
 * Date : 25-03-2013 * * Description : * *
 ******************************************************************************************** 
 * Date Author Version Comments *
 ******************************************************************************************** 
 * 25-03-2013 Pennant 0.1 * * * * * * * * *
 ******************************************************************************************** 
 */
package com.pennant.backend.dao.commitment.impl;

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

import com.pennant.backend.dao.commitment.CommitmentMovementDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.commitment.CommitmentMovement;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pff.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>CommitmentMovement model</b> class.<br>
 * 
 */

public class CommitmentMovementDAOImpl extends BasisCodeDAO<CommitmentMovement> implements
        CommitmentMovementDAO {

	private static Logger logger = Logger.getLogger(CommitmentMovementDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public CommitmentMovementDAOImpl() {
		super();
	}
	
	/**
	 * This method set the Work Flow id based on the module name and return the new CommitmentMovement
	 * 
	 * @return CommitmentMovement
	 */

	@Override
	public CommitmentMovement getCommitmentMovement() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("CommitmentMovement");
		CommitmentMovement commitmentMovement = new CommitmentMovement();
		if (workFlowDetails != null) {
			commitmentMovement.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return commitmentMovement;
	}

	/**
	 * This method get the module from method getCommitment() and set the new record flag as true and return
	 * CommitmentMovement()
	 * 
	 * @return CommitmentMovement
	 */

	@Override
	public CommitmentMovement getNewCommitmentMovement() {
		logger.debug("Entering");
		CommitmentMovement commitmentMovement = getCommitmentMovement();
		commitmentMovement.setNewRecord(true);
		logger.debug("Leaving");
		return commitmentMovement;
	}

	/**
	 * Fetch the Record CommitmentMovement Detail details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return CommitmentMovement
	 */
	@Override
	public CommitmentMovement getCommitmentMovementById(final String id, String type) {
		logger.debug("Entering");
		CommitmentMovement commitmentMovement = new CommitmentMovement();
		commitmentMovement.setCmtReference(id);

		StringBuilder selectSql = new StringBuilder("Select CmtReference,FinReference,FinBranch");
		selectSql.append(",FinType,MovementDate,MovementOrder ,MovementType,MovementAmount,");
		selectSql.append("CmtAmount,CmtCharges,CmtUtilizedAmount,CmtAvailable,LinkedTranId");
		selectSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode,");
		selectSql.append(" NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From CommitmentMovements");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CmtReference =:CmtReference  order by  MovementOrder desc");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(commitmentMovement);
		RowMapper<CommitmentMovement> typeRowMapper = ParameterizedBeanPropertyRowMapper
		        .newInstance(CommitmentMovement.class);

		try {
			List<CommitmentMovement> list = this.namedParameterJdbcTemplate.query(
			        selectSql.toString(), beanParameters, typeRowMapper);
			if (list != null && list.size() > 0) {
				commitmentMovement = list.get(0);
			}
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			commitmentMovement = null;
		}
		logger.debug("Leaving");
		return commitmentMovement;
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
	 * This method Deletes the Record from the CommitmentMovements or Commitments_Temp. if Record not deleted then
	 * throws DataAccessException with error 41003. delete CommitmentMovement Detail by key CmtReference
	 * 
	 * @param CommitmentMovement
	 *            Detail (commitmentMovement)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(CommitmentMovement commitmentMovement, String type) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder("Delete From CommitmentMovements");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where CmtReference =:CmtReference");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(commitmentMovement);
		try {
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(),
			        beanParameters);
			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Delete Commitment Movement on Commitment Reference
	 * @param cmtReference
	 * @param type
	 */
	@Override
	public void deleteByRef(String cmtReference, String type) {
		logger.debug("Entering");
		CommitmentMovement commitmentMovement = new CommitmentMovement();
		commitmentMovement.setCmtReference(cmtReference);

		StringBuilder deleteSql = new StringBuilder("Delete From CommitmentMovements");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where CmtReference =:CmtReference");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(commitmentMovement);
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into CommitmentMovements or Commitments_Temp.
	 * 
	 * save CommitmentMovement Detail
	 * 
	 * @param CommitmentMovement
	 *            Detail (commitmentMovement)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public String save(CommitmentMovement commitmentMovement, String type) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into CommitmentMovements");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (CmtReference,FinReference,FinBranch,FinType,MovementDate,MovementOrder ,");
		insertSql
		        .append("MovementType,MovementAmount,CmtAmount,CmtCharges,CmtUtilizedAmount,CmtAvailable,LinkedTranId");
		insertSql
		        .append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql
		        .append(" Values(:CmtReference, :FinReference, :FinBranch,:FinType, :MovementDate, :MovementOrder, :MovementType, :MovementAmount, :CmtAmount,:CmtCharges, :CmtUtilizedAmount, :CmtAvailable, :LinkedTranId");
		insertSql
		        .append(", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(commitmentMovement);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return commitmentMovement.getId();
	}

	/**
	 * This method updates the Record CommitmentMovements or Commitments_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update CommitmentMovement Detail by key CmtReference and Version
	 * 
	 * @param CommitmentMovement
	 *            Detail (commitmentMovement)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(CommitmentMovement commitmentMovement, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update CommitmentMovements");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql
		        .append(" Set FinReference= :FinReference,FinBranch= :FinBranch,FinType= :FinType,MovementDate= :MovementDate,MovementOrder= :MovementOrder,MovementType= :MovementType,MovementAmount= :MovementAmount,CmtAmount= :CmtAmount,CmtCharges=:CmtCharges,CmtUtilizedAmount= :CmtUtilizedAmount,CmtAvailable= :CmtAvailable,LinkedTranId= :LinkedTranId");
		updateSql
		        .append(", Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where CmtReference =:CmtReference");

		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(commitmentMovement);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Fetching Max Movement Order for particular Commitment Reference
	 */
	@Override
    public int getMaxMovementOrderByRef(String cmtReference) {
		logger.debug("Entering");
		CommitmentMovement commitmentMovement = new CommitmentMovement();
		commitmentMovement.setCmtReference(cmtReference);

		StringBuilder selectSql = new StringBuilder("Select COALESCE(MAX(MovementOrder),0) ");
		selectSql.append(" From CommitmentMovements");
		selectSql.append(" Where CmtReference =:CmtReference");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(commitmentMovement);

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
    }
}