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
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.finance.financialSummary.RecommendationNotesDetailsDAO;
import com.pennant.backend.model.finance.financialsummary.RecommendationNotes;
import com.pennant.backend.model.finance.financialsummary.RecommendationNotesConfiguration;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class RecommendationNotesDetailsDAOImpl extends SequenceDao<RecommendationNotesDetailsDAO>
		implements RecommendationNotesDetailsDAO {
	private static Logger logger = LogManager.getLogger(RecommendationNotesDetailsDAOImpl.class);

	public RecommendationNotesDetailsDAOImpl() {
		super();
	}

	@Override
	public List<RecommendationNotes> getRecommendationNotesDetails(String finReference) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append("  t1.Id, t1.FinReference, t1.ParticularId,t3.Particulars,t1.Remarks");
		sql.append(", t1.Version, t1.LastMntBy, t1.LastMntOn, t1.RecordStatus, t1.RoleCode, t1.NextRoleCode");
		sql.append(", t1.TaskId, t1.NextTaskId, t1.RecordType, t1.WorkflowId");
		sql.append(" from  RECOMMENDATION_NOTES_TEMP t1");
		sql.append(" LEFT JOIN FinanceMain_TEMP t2 ON t2.finreference =  t1.finreference");
		sql.append(" LEFT JOIN RECOMMENDATION_NOTES_CONFIG t3 ON t3.id =  t1.ParticularId");
		sql.append(" Where t1.finReference = ?");
		sql.append(" UNION ALL");
		sql.append(" Select  t1.Id, t1.FinReference, t1.ParticularId,t3.Particulars,t1.Remarks");
		sql.append(", t1.Version, t1.LastMntBy, t1.LastMntOn, t1.RecordStatus, t1.RoleCode, t1.NextRoleCode");
		sql.append(", t1.TaskId, t1.NextTaskId, t1.RecordType, t1.WorkflowId");
		sql.append(" from  RECOMMENDATION_NOTES t1");
		sql.append(" LEFT JOIN FinanceMain_TEMP t2 ON t2.finreference =  t1.finreference");
		sql.append(" LEFT JOIN RECOMMENDATION_NOTES_CONFIG t3 ON t3.id =  t1.ParticularId");
		sql.append(" where NOT EXISTS (Select 1 from RECOMMENDATION_NOTES_TEMP where id = t1.id)");
		sql.append(" and t1.finReference = ?");

		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setString(index++, finReference);
			ps.setString(index++, finReference);
		}, (rs, rowNum) -> {
			RecommendationNotes rn = new RecommendationNotes();

			rn.setId(rs.getLong("Id"));
			rn.setFinReference(rs.getString("FinReference"));
			rn.setParticularId(rs.getLong("ParticularId"));
			rn.setParticulars(rs.getString("Particulars"));
			rn.setRemarks(rs.getString("Remarks"));
			rn.setVersion(rs.getInt("Version"));
			rn.setLastMntBy(rs.getLong("LastMntBy"));
			rn.setLastMntOn(rs.getTimestamp("LastMntOn"));
			rn.setRecordStatus(rs.getString("RecordStatus"));
			rn.setRoleCode(rs.getString("RoleCode"));
			rn.setNextRoleCode(rs.getString("NextRoleCode"));
			rn.setTaskId(rs.getString("TaskId"));
			rn.setNextTaskId(rs.getString("NextTaskId"));
			rn.setRecordType(rs.getString("RecordType"));
			rn.setWorkflowId(rs.getLong("WorkflowId"));

			return rn;
		});
	}

	@Override
	public void delete(RecommendationNotes recommendationNotesDetails, String type) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append(" Delete From RECOMMENDATION_NOTES");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where id =:id and finReference =:finReference");

		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(recommendationNotesDetails);

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
	public long save(RecommendationNotes recommendationNotesDetails, String type) {
		logger.debug("Entering");

		if (recommendationNotesDetails.getId() == Long.MIN_VALUE) {
			recommendationNotesDetails.setId(getNextValue("SeqRECOMMENDATION_NOTE"));
			logger.debug("get NextID:" + recommendationNotesDetails.getId());
		}

		StringBuilder insertSql = new StringBuilder();
		insertSql.append("Insert Into RECOMMENDATION_NOTES");
		insertSql.append(type);
		insertSql.append(" (id, FinReference, ParticularId,Remarks");
		insertSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		insertSql.append(", RecordType, WorkflowId)");
		insertSql.append(" Values(:id,:FinReference, :ParticularId, :Remarks");
		insertSql.append(
				", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId");
		insertSql.append(", :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(recommendationNotesDetails);

		try {
			this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug("Leaving");
		return recommendationNotesDetails.getId();
	}

	@Override
	public void update(RecommendationNotes recommendationNotesDetails, String type) {
		int recordCount = 0;
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder();
		updateSql.append("Update RECOMMENDATION_NOTES");
		updateSql.append(type);
		updateSql.append(" Set FinReference = :FinReference, ParticularId = :ParticularId");
		updateSql.append(", Remarks = :Remarks");
		updateSql.append(", Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn");
		updateSql.append(
				", RecordStatus= :RecordStatus, RoleCode = :RoleCode,NextRoleCode = :NextRoleCode, TaskId = :TaskId");
		updateSql.append(", NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where id =:id and finReference =:finReference");

		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(recommendationNotesDetails);
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
	public int getVersion(long id, String recommendationNotesDetails) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("id", id);
		source.addValue("recommendationNotesDetails", recommendationNotesDetails);

		StringBuffer selectSql = new StringBuffer();
		selectSql.append("SELECT Version FROM RECOMMENDATION_NOTES");

		selectSql.append(" WHERE id = :id AND finReference = :finReference");

		logger.debug("insertSql: " + selectSql.toString());

		logger.debug("Leaving");

		return this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
	}

	public List<RecommendationNotesConfiguration> getRecommendationNotesConfigurationDetails() {

		RecommendationNotesConfiguration recommendationNotesConfigurationDetails = new RecommendationNotesConfiguration();

		StringBuilder selectSql = new StringBuilder();
		selectSql.append("select id, Particulars ");
		selectSql.append(" FROM  RECOMMENDATION_NOTES_CONFIG");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(recommendationNotesConfigurationDetails);
		RowMapper<RecommendationNotesConfiguration> typeRowMapper = BeanPropertyRowMapper
				.newInstance(RecommendationNotesConfiguration.class);

		List<RecommendationNotesConfiguration> recommendationNotesConfigurationList = this.jdbcTemplate
				.query(selectSql.toString(), beanParameters, typeRowMapper);
		logger.debug("Leaving ");
		return recommendationNotesConfigurationList;

	}

}