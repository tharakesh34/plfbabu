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

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.commitment.CommitmentMovementDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.commitment.CommitmentMovement;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>CommitmentMovement model</b> class.<br>
 * 
 */

public class CommitmentMovementDAOImpl extends BasisCodeDAO<CommitmentMovement> implements
        CommitmentMovementDAO {

	private static Logger logger = Logger.getLogger(CommitmentMovementDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

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
		/*if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append("");
		}*/
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
			commitmentMovement = null;
		}
		logger.debug("Leaving");
		return commitmentMovement;
	}

	/**
	 * This method initialise the Record.
	 * 
	 * @param CommitmentMovement
	 *            (commitmentMovement)
	 * @return CommitmentMovement
	 */
	@Override
	public void initialize(CommitmentMovement commitmentMovement) {
		super.initialize(commitmentMovement);
	}

	/**
	 * This method refresh the Record.
	 * 
	 * @param CommitmentMovement
	 *            (commitmentMovement)
	 * @return void
	 */
	@Override
	public void refresh(CommitmentMovement commitmentMovement) {

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
	@SuppressWarnings("serial")
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
				ErrorDetails errorDetails = getError("41003", commitmentMovement.getId(),
				        commitmentMovement.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {
				};
			}
		} catch (DataAccessException e) {
			logger.error(e);
			ErrorDetails errorDetails = getError("41006", commitmentMovement.getId(),
			        commitmentMovement.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
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
	@SuppressWarnings("serial")
	@Override
	public void update(CommitmentMovement commitmentMovement, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update CommitmentMovements");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql
		        .append(" Set CmtReference = :CmtReference,FinReference= :FinReference,FinBranch= :FinBranch,FinType= :FinType,MovementDate= :MovementDate,MovementOrder= :MovementOrder,MovementType= :MovementType,MovementAmount= :MovementAmount,CmtAmount= :CmtAmount,CmtCharges=:CmtCharges,CmtUtilizedAmount= :CmtUtilizedAmount,CmtAvailable= :CmtAvailable,LinkedTranId= :LinkedTranId");
		updateSql
		        .append(", Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where CmtReference =:CmtReference");

		if (!type.endsWith("_TEMP")) {
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(commitmentMovement);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :" + recordCount);
			ErrorDetails errorDetails = getError("41004", commitmentMovement.getId(),
			        commitmentMovement.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {
			};
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

		StringBuilder selectSql = new StringBuilder("Select MAX(MovementOrder) ");
		selectSql.append(" From CommitmentMovements");
		selectSql.append(" Where CmtReference =:CmtReference");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(commitmentMovement);

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.queryForInt(selectSql.toString(), beanParameters);
    }

	private ErrorDetails getError(String errorId, String cmtReference, String userLanguage) {
		String[][] parms = new String[2][1];
		parms[1][0] = cmtReference;
		parms[0][0] = PennantJavaUtil.getLabel("label_CmtReference") + ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId,
		        parms[0], parms[1]), userLanguage);
	}


}