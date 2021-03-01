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
 * FileName    		:  CustomerPhoneNumberDAOImpl.java                                      * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-05-2011    														*
 *                                                                  						*
 * Modified Date    :  26-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-05-2011       Pennant	                 0.1                                            * 
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
package com.pennant.backend.dao.finance.financialSummary.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.finance.financialSummary.DueDiligenceDetailsDAO;
import com.pennant.backend.model.finance.financialsummary.DueDiligenceDetails;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class DueDiligenceDetailsDAOImpl extends SequenceDao<DueDiligenceDetails> implements DueDiligenceDetailsDAO {
	private static Logger logger = LogManager.getLogger(DueDiligenceDetailsDAOImpl.class);

	public DueDiligenceDetailsDAOImpl() {
		super();
	}

	@Override
	public List<DueDiligenceDetails> getDueDiligenceDetails(String finReference) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" t1.Id,t1.FinReference, t1.ParticularId,t3.Particulars,t1.Status,t1.Remarks");
		sql.append(", t1.Version, t1.LastMntBy, t1.LastMnton, t1.RecordStatus, t1.RoleCode, t1.NextRoleCode");
		sql.append(", t1.TaskId, t1.NextTaskId, t1.RecordType, t1.WorkflowId");
		sql.append(" from  DUE_DILIGENCES_TEMP t1");
		sql.append(" left join FinanceMain_TEMP t2 on t2.finreference =  t1.finreference");
		sql.append(" left join Due_Diligence_Checklist t3 on t3.id =  t1.ParticularId");
		sql.append(" Where t1.finReference = ?");
		sql.append(" UNION ALL");
		sql.append(" Select  t1.Id, t1.FinReference, t1.ParticularId,t3.Particulars,t1.Status,t1.Remarks");
		sql.append(", t1.Version, t1.LastMntBy, t1.LastMnton, t1.RecordStatus, t1.RoleCode, t1.NextRoleCode");
		sql.append(", t1.TaskId, t1.NextTaskId, t1.RecordType, t1.WorkflowId ");
		sql.append(" from due_diligences t1");
		sql.append(" left join FinanceMain t2 on t2.finreference =  t1.finreference");
		sql.append(" left join Due_Diligence_Checklist t3 on t3.id =  t1.ParticularId");
		sql.append(" Where not exists ( Select 1 from due_diligence_checklist_temp Where id = t1.id)");
		sql.append(" and t1.finReference = ?");
		sql.append(" order by particularid");

		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setString(index++, finReference);
			ps.setString(index++, finReference);
		}, (rs, rowNum) -> {
			DueDiligenceDetails ddd = new DueDiligenceDetails();

			ddd.setId(rs.getLong("Id"));
			ddd.setFinReference(rs.getString("FinReference"));
			ddd.setParticularId(rs.getLong("ParticularId"));
			ddd.setParticulars(rs.getString("Particulars"));
			ddd.setStatus(rs.getString("Status"));
			ddd.setRemarks(rs.getString("Remarks"));
			ddd.setVersion(rs.getInt("Version"));
			ddd.setLastMntBy(rs.getLong("LastMntBy"));
			ddd.setLastMntOn(rs.getTimestamp("LastMntOn"));
			ddd.setRecordStatus(rs.getString("RecordStatus"));
			ddd.setRoleCode(rs.getString("RoleCode"));
			ddd.setNextRoleCode(rs.getString("NextRoleCode"));
			ddd.setTaskId(rs.getString("TaskId"));
			ddd.setNextTaskId(rs.getString("NextTaskId"));
			ddd.setRecordType(rs.getString("RecordType"));
			ddd.setWorkflowId(rs.getLong("WorkflowId"));

			return ddd;
		});
	}

	@Override
	public void delete(DueDiligenceDetails dueDiligenceDetails, String type) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append(" Delete From DUE_DILIGENCES");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where id =:id and finReference =:finReference");

		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(dueDiligenceDetails);

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

	@Override
	public long save(DueDiligenceDetails dueDiligenceDetails, String type) {
		logger.debug("Entering");

		if (dueDiligenceDetails.getId() == Long.MIN_VALUE) {
			dueDiligenceDetails.setId(getNextValue("SeqDUE_DILIGENCES"));
			logger.debug("get NextID:" + dueDiligenceDetails.getId());
		}

		StringBuilder insertSql = new StringBuilder();
		insertSql.append("Insert Into DUE_DILIGENCES");
		insertSql.append(type);
		insertSql.append(" (id, FinReference, ParticularId,Status,Remarks,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId,");
		insertSql.append(" RecordType, WorkflowId)");
		insertSql.append(" Values(:id,:FinReference, :ParticularId, :Status,:Remarks,");
		insertSql.append(
				" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, ");
		insertSql.append(" :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(dueDiligenceDetails);

		try {
			this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug("Leaving");
		return dueDiligenceDetails.getId();
	}

	@Override
	public void update(DueDiligenceDetails dueDiligenceDetails, String type) {
		int recordCount = 0;
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder();
		updateSql.append("Update DUE_DILIGENCES");
		updateSql.append(type);
		updateSql.append(" Set FinReference = :FinReference, ParticularId = :ParticularId,");
		updateSql.append(" Status = :Status, Remarks = :Remarks,");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, ");
		updateSql.append(
				" RecordStatus= :RecordStatus, RoleCode = :RoleCode,NextRoleCode = :NextRoleCode, TaskId = :TaskId,");
		updateSql.append(" NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where id =:id and finReference =:finReference");

		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(dueDiligenceDetails);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	/**
	 * Fetch current version of the record.
	 * 
	 * @param id
	 * @param typeCode
	 * @return Integer
	 */
	@Override
	public int getVersion(long id, String dueDiligenceDetails) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("id", id);
		source.addValue("dueDiligenceDetails", dueDiligenceDetails);

		StringBuffer selectSql = new StringBuffer();
		selectSql.append("SELECT Version FROM DUE_DILIGENCES");

		selectSql.append(" WHERE id = :id AND finReference = :finReference");

		logger.debug("insertSql: " + selectSql.toString());

		logger.debug("Leaving");

		return this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
	}

	/**
	 * Fetch current version of the record.
	 * 
	 * @param id
	 * @param typeCode
	 * @return Integer
	 */
	@Override
	public String getStatus(long id) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("id", id);

		StringBuffer selectSql = new StringBuffer();
		selectSql.append("SELECT status FROM DUE_DILIGENCE_CHECKLIST");
		selectSql.append(" WHERE id = :id");

		logger.debug("insertSql: " + selectSql.toString());

		logger.debug("Leaving");

		return this.jdbcTemplate.queryForObject(selectSql.toString(), source, String.class);
	}

}