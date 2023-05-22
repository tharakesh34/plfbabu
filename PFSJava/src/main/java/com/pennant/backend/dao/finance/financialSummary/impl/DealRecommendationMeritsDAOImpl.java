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
 * * FileName : CustomerPhoneNumberDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 26-05-2011 * *
 * Modified Date : 26-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-05-2011 Pennant 0.1 * * * * * * * * *
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

import com.pennant.backend.dao.finance.financialSummary.DealRecommendationMeritsDAO;
import com.pennant.backend.model.finance.financialsummary.DealRecommendationMerits;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * DAO methods implementation for the <b>CustomerPhoneNumber model</b> class.<br>
 * 
 */
public class DealRecommendationMeritsDAOImpl extends SequenceDao<DealRecommendationMeritsDAO>
		implements DealRecommendationMeritsDAO {
	private static Logger logger = LogManager.getLogger(DealRecommendationMeritsDAOImpl.class);

	public DealRecommendationMeritsDAOImpl() {
		super();
	}

	public List<DealRecommendationMerits> getDealRecommendationMerits(String finReference) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" t1.Id, t1.SeqNo, t1.DealMerits, t1.FinReference, t1.Version, t1.LastMntBy");
		sql.append(", t1.LastMntOn, t1.RecordStatus, t1.RoleCode, t1.NextRoleCode, t1.TaskId");
		sql.append(", t1.NextTaskId, t1.RecordType, t1.WorkflowId");
		sql.append(" from DealRecommendation_Merits_Temp t1");
		sql.append(" left join FinanceMain t2 on t2.finreference =  t1.finreference");
		sql.append(" where t1.finReference = ?");
		sql.append(" UNION ALL");
		sql.append(" Select");
		sql.append("  t1.Id, t1.SeqNo, t1.DealMerits, t1.FinReference, t1.Version, t1.LastMntBy");
		sql.append(", t1.LastMntOn, t1.RecordStatus, t1.RoleCode, t1.NextRoleCode, t1.TaskId");
		sql.append(", t1.NextTaskId, t1.RecordType, t1.WorkflowId");
		sql.append(" from DealRecommendation_Merits t1");
		sql.append(" left join FinanceMain t2 on t2.finreference =  t1.finreference");
		sql.append(" where not exists ( Select 1 from DealRecommendation_Merits_Temp where id = t1.id)");
		sql.append(" and t1.FinReference = ?");

		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setString(index++, finReference);
			ps.setString(index, finReference);
		}, (rs, rowNum) -> {
			DealRecommendationMerits drm = new DealRecommendationMerits();

			drm.setId(rs.getLong("Id"));
			drm.setSeqNo(rs.getLong("SeqNo"));
			drm.setDealMerits(rs.getString("DealMerits"));
			drm.setFinReference(rs.getString("FinReference"));
			drm.setVersion(rs.getInt("Version"));
			drm.setLastMntBy(rs.getLong("LastMntBy"));
			drm.setLastMntOn(rs.getTimestamp("LastMntOn"));
			drm.setRecordStatus(rs.getString("RecordStatus"));
			drm.setRoleCode(rs.getString("RoleCode"));
			drm.setNextRoleCode(rs.getString("NextRoleCode"));
			drm.setTaskId(rs.getString("TaskId"));
			drm.setNextTaskId(rs.getString("NextTaskId"));
			drm.setRecordType(rs.getString("RecordType"));
			drm.setWorkflowId(rs.getLong("WorkflowId"));

			return drm;
		});
	}

	@Override
	public void delete(DealRecommendationMerits dealRecommendationMerits, String type) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append(" Delete From DealRecommendation_Merits");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where id =:id and finReference =:finReference");

		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(dealRecommendationMerits);

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
	public long save(DealRecommendationMerits dealRecommendationMerits, String type) {
		logger.debug("Entering");

		if (dealRecommendationMerits.getId() == Long.MIN_VALUE) {
			dealRecommendationMerits.setId(getNextValue("Seq_DealRecommendation_Merits"));
			logger.debug("get NextID:" + dealRecommendationMerits.getId());
		}

		StringBuilder insertSql = new StringBuilder();
		insertSql.append(" Insert Into DealRecommendation_Merits");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (id, SeqNo, DealMerits, FinReference");
		insertSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		insertSql.append(", TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:id, :SeqNo, :DealMerits, :FinReference");
		insertSql.append(", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode");
		insertSql.append(", :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(dealRecommendationMerits);

		try {
			this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug("Leaving");
		return dealRecommendationMerits.getId();
	}

	@Override
	public void update(DealRecommendationMerits dealRecommendationMerits, String type) {
		int recordCount = 0;
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder();
		updateSql.append(" Update DealRecommendation_Merits");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set DealMerits = :DealMerits");
		updateSql.append(", Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn");
		updateSql.append(", RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode");
		updateSql.append(", TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType");
		updateSql.append(", WorkflowId = :WorkflowId");
		updateSql.append(" Where id =:id and finReference =:finReference");

		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(dealRecommendationMerits);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	@Override
	public int getVersion(long id, String dealRecommendationMerits) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("id", id);
		source.addValue("dealRecommendationMerits", dealRecommendationMerits);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append("SELECT Version FROM DealRecommendation_Merits");
		selectSql.append(" WHERE id = :id AND finReference = :finReference");

		logger.debug("insertSql: " + selectSql.toString());

		logger.debug("Leaving");

		return this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
	}

}