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
 * FileName    		:  FinanceCheckListReferenceDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  08-12-2011    														*
 *                                                                  						*
 * Modified Date    :  08-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 08-12-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.lmtmasters.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.lmtmasters.FinanceCheckListReferenceDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.lmtmasters.FinanceCheckListReference;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * DAO methods implementation for the <b>FinanceCheckListReference model</b> class.<br>
 * 
 */

public class FinanceCheckListReferenceDAOImpl extends BasicDao<FinanceCheckListReference>
		implements FinanceCheckListReferenceDAO {
	private static Logger logger = Logger.getLogger(FinanceCheckListReferenceDAOImpl.class);

	public FinanceCheckListReferenceDAOImpl() {
		super();
	}

	/**
	 * This method set the Work Flow id based on the module name and return the new FinanceCheckListReference
	 * 
	 * @return FinanceCheckListReference
	 */

	@Override
	public FinanceCheckListReference getFinanceCheckListReference() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("FinanceCheckListReference");
		FinanceCheckListReference financeCheckListReference = new FinanceCheckListReference();
		if (workFlowDetails != null) {
			financeCheckListReference.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return financeCheckListReference;
	}

	/**
	 * This method get the module from method getFinanceCheckListReference() and set the new record flag as true and
	 * return FinanceCheckListReference()
	 * 
	 * @return FinanceCheckListReference
	 */

	@Override
	public FinanceCheckListReference getNewFinanceCheckListReference() {
		logger.debug("Entering");
		FinanceCheckListReference financeCheckListReference = getFinanceCheckListReference();
		financeCheckListReference.setNewRecord(true);
		logger.debug("Leaving");
		return financeCheckListReference;
	}

	/**
	 * Fetch the Record Finance Check List Details details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return FinanceCheckListReference
	 */
	@Override
	public FinanceCheckListReference getFinanceCheckListReferenceById(final String id, long questionId, long answer,
			String type) {
		logger.debug("Entering");
		FinanceCheckListReference financeCheckListReference = new FinanceCheckListReference();

		financeCheckListReference.setId(id);
		financeCheckListReference.setQuestionId(questionId);
		financeCheckListReference.setAnswer(answer);

		StringBuilder selectSql = new StringBuilder("Select FinReference, QuestionId, Answer,Remarks");
		selectSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		selectSql.append(", TaskId, NextTaskId, RecordType, WorkflowId, InstructionUID");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(",lovDescQuesDesc, lovDescAnswerDesc ");
		}
		selectSql.append(" From FinanceCheckListRef");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference and  QuestionId=:QuestionId and Answer=:Answer");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeCheckListReference);
		RowMapper<FinanceCheckListReference> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(FinanceCheckListReference.class);

		try {
			financeCheckListReference = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters,
					typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			financeCheckListReference = null;
		}
		logger.debug("Leaving");
		return financeCheckListReference;
	}

	/**
	 * Fetch the Record Finance Check List Details details by key field
	 * 
	 * @param finReference
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return FinanceCheckListReference
	 */
	@Override
	public List<FinanceCheckListReference> getCheckListByFinRef(final String finReference, String showStageCheckListIds,
			String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinReference, QuestionId, Answer, Remarks, Version, LastMntBy");
		sql.append(", LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId");
		sql.append(", NextTaskId, RecordType, WorkflowId, InstructionUID");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", LovDescQuesDesc, LovDescAnswerDesc");
		}

		sql.append(" from FinanceCheckListRef");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinReference = ?");

		if (StringUtils.isNotBlank(showStageCheckListIds)) {
			String[] showStageCheckList = showStageCheckListIds.split(",");
			sql.append(" and QuestionId IN(");

			int i = 0;

			while (i < showStageCheckList.length) {
				sql.append(" ?,");
				i++;
			}

			sql.deleteCharAt(sql.length() - 1);
			sql.append(")");
		}

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int index = 1;
					ps.setString(index++, finReference);

					if (StringUtils.isNotBlank(showStageCheckListIds)) {
						String[] showStageCheckList = showStageCheckListIds.split(",");

						for (String showStage : showStageCheckList) {
							ps.setLong(index++, Long.valueOf(showStage));
						}
					}
				}
			}, new RowMapper<FinanceCheckListReference>() {
				@Override
				public FinanceCheckListReference mapRow(ResultSet rs, int rowNum) throws SQLException {
					FinanceCheckListReference fcr = new FinanceCheckListReference();

					fcr.setFinReference(rs.getString("FinReference"));
					fcr.setQuestionId(rs.getLong("QuestionId"));
					fcr.setAnswer(rs.getLong("Answer"));
					fcr.setRemarks(rs.getString("Remarks"));
					fcr.setVersion(rs.getInt("Version"));
					fcr.setLastMntBy(rs.getLong("LastMntBy"));
					fcr.setLastMntOn(rs.getTimestamp("LastMntOn"));
					fcr.setRecordStatus(rs.getString("RecordStatus"));
					fcr.setRoleCode(rs.getString("RoleCode"));
					fcr.setNextRoleCode(rs.getString("NextRoleCode"));
					fcr.setTaskId(rs.getString("TaskId"));
					fcr.setNextTaskId(rs.getString("NextTaskId"));
					fcr.setRecordType(rs.getString("RecordType"));
					fcr.setWorkflowId(rs.getLong("WorkflowId"));
					fcr.setInstructionUID(rs.getLong("InstructionUID"));

					if (StringUtils.trimToEmpty(type).contains("View")) {
						fcr.setLovDescQuesDesc(rs.getString("LovDescQuesDesc"));
						fcr.setLovDescAnswerDesc(rs.getString("LovDescAnswerDesc"));
					}

					return fcr;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
	}

	/**
	 * This method Deletes the Record from the FinanceCheckListRef or FinanceCheckListRef_Temp. if Record not deleted
	 * then throws DataAccessException with error 41003. delete Finance Check List Details by key FinReference
	 * 
	 * @param Finance
	 *            Check List Details (financeCheckListReference)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(FinanceCheckListReference financeCheckListReference, String type) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder("Delete From FinanceCheckListRef");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FinReference =:FinReference and QuestionId=:QuestionId and Answer=:Answer");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeCheckListReference);
		try {
			recordCount = this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * This method deletes all the records with finReference condition
	 */
	public void delete(String finReference, String type) {
		logger.debug("Entering");
		FinanceCheckListReference financeCheckListReference = new FinanceCheckListReference();
		financeCheckListReference.setFinReference(finReference);
		StringBuilder deleteSql = new StringBuilder("Delete From FinanceCheckListRef");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FinReference =:FinReference");
		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeCheckListReference);
		this.jdbcTemplate.update(deleteSql.toString(), beanParameters);

		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into FinanceCheckListRef or FinanceCheckListRef_Temp.
	 *
	 * save Finance Check List Details
	 * 
	 * @param Finance
	 *            Check List Details (financeCheckListReference)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public String save(FinanceCheckListReference financeCheckListReference, String type) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into FinanceCheckListRef");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (FinReference, QuestionId, Answer,Remarks");
		insertSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		insertSql.append(", TaskId, NextTaskId, RecordType, WorkflowId, InstructionUID)");
		insertSql.append(" Values(:FinReference, :QuestionId, :Answer,:Remarks");
		insertSql.append(", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode");
		insertSql.append(", :TaskId, :NextTaskId, :RecordType, :WorkflowId, :InstructionUID)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeCheckListReference);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return financeCheckListReference.getId();
	}

	/**
	 * This method updates the Record FinanceCheckListRef or FinanceCheckListRef_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update Finance Check List Details by key FinReference and Version
	 * 
	 * @param Finance
	 *            Check List Details (financeCheckListReference)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public void update(FinanceCheckListReference financeCheckListReference, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update FinanceCheckListRef");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set Remarks=:Remarks, Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn");
		updateSql.append(
				", RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId");
		updateSql.append(
				", NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId, InstructionUID = :InstructionUID");
		updateSql.append(" Where FinReference =:FinReference AND QuestionId = :QuestionId AND Answer =:Answer");

		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(financeCheckListReference);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

}