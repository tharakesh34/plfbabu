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
 * * FileName : ScoringMetricsDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 05-12-2011 * *
 * Modified Date : 05-12-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 05-12-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.dao.rmtmasters.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.rmtmasters.ScoringMetricsDAO;
import com.pennant.backend.model.rmtmasters.ScoringMetrics;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Message;

/**
 * DAO methods implementation for the <b>ScoringMetrics model</b> class.<br>
 * 
 */

public class ScoringMetricsDAOImpl extends BasicDao<ScoringMetrics> implements ScoringMetricsDAO {
	private static Logger logger = LogManager.getLogger(ScoringMetricsDAOImpl.class);

	public ScoringMetricsDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record Scoring Metrics Details details by key fields
	 * 
	 * @param id   (String)
	 * @param type (String) ""/_Temp/_View
	 * @return ScoringMetrics
	 */
	@Override
	public ScoringMetrics getScoringMetricsById(final String id, String type) {
		logger.debug("Entering");
		ScoringMetrics scoringMetrics = new ScoringMetrics();
		scoringMetrics.setId(0);

		StringBuilder selectSql = new StringBuilder("Select ScoreGroupId, ScoringId, CategoryType, ");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(" lovDescScoringCode,lovDescScoringCodeDesc,lovDescScoreMetricSeq, lovDescSQLRule, ");
		}
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode,");
		selectSql.append(" NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From RMTScoringMetrics");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where ScoreGroupId =:ScoreGroupId and ScoringId=:ScoringId");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(scoringMetrics);
		RowMapper<ScoringMetrics> typeRowMapper = BeanPropertyRowMapper.newInstance(ScoringMetrics.class);

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	/**
	 * This method returns the List<ScoringMetrics> by key field
	 */
	public List<ScoringMetrics> getScoringMetricsByScoreGrpId(final long scoreGrpId, String categoryType, String type) {
		logger.debug("Entering");
		ScoringMetrics scoringMetrics = new ScoringMetrics();
		scoringMetrics.setScoreGroupId(scoreGrpId);
		scoringMetrics.setCategoryType(categoryType);

		StringBuilder selectSql = new StringBuilder("Select ScoreGroupId, ScoringId, CategoryType, ");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(" lovDescScoringCode,lovDescScoringCodeDesc,lovDescScoreMetricSeq, lovDescSQLRule, ");
		}
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, ");
		selectSql.append(" NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From RMTScoringMetrics");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where ScoreGroupId =:ScoreGroupId AND CategoryType =:CategoryType ");
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(scoringMetrics);
		RowMapper<ScoringMetrics> typeRowMapper = BeanPropertyRowMapper.newInstance(ScoringMetrics.class);

		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	/**
	 * This method Deletes the Record from the RMTScoringMetrics or RMTScoringMetrics_Temp. if Record not deleted then
	 * throws DataAccessException with error 41003. delete Scoring Metrics Details by key ScoreGroupId and ScoringId
	 * 
	 * @param Scoring Metrics Details (scoringMetrics)
	 * @param type    (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(ScoringMetrics scoringMetrics, String type) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder("Delete From RMTScoringMetrics");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where ScoreGroupId =:ScoreGroupId AND ScoringId=:ScoringId");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(scoringMetrics);
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
	 * This method delete the record by scoreGroupId
	 */
	public void delete(long scoreGroupId, String type) {
		logger.debug("Entering");
		ScoringMetrics scoringMetrics = new ScoringMetrics();
		scoringMetrics.setScoreGroupId(scoreGroupId);

		StringBuilder deleteSql = new StringBuilder("Delete From RMTScoringMetrics");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where ScoreGroupId =:ScoreGroupId");

		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(scoringMetrics);
		this.jdbcTemplate.update(deleteSql.toString(), beanParameters);

		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into RMTScoringMetrics or RMTScoringMetrics_Temp.
	 *
	 * save Scoring Metrics Details
	 * 
	 * @param Scoring Metrics Details (scoringMetrics)
	 * @param type    (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public String save(ScoringMetrics scoringMetrics, String type) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into RMTScoringMetrics");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (ScoreGroupId, ScoringId, CategoryType, ");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode , ");
		insertSql.append(" NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:ScoreGroupId, :ScoringId, :CategoryType, ");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, ");
		insertSql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(scoringMetrics);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return String.valueOf(scoringMetrics.getId());
	}

	/**
	 * This method updates the Record RMTScoringMetrics or RMTScoringMetrics_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update Scoring Metrics Details by key ScoreGroupId and Version
	 * 
	 * @param Scoring Metrics Details (scoringMetrics)
	 * @param type    (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(ScoringMetrics scoringMetrics, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update RMTScoringMetrics");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set CategoryType =:CategoryType, ");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, ");
		updateSql.append(
				" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where ScoreGroupId =:ScoreGroupId AND  ScoringId = :ScoringId");

		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(scoringMetrics);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	@Override
	public int getScoringMetricsByRuleCode(long ruleId, String type) {
		logger.debug("Entering");
		ScoringMetrics scoringMetrics = new ScoringMetrics();
		scoringMetrics.setScoringId(ruleId);

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*)");
		selectSql.append(" From RMTScoringMetrics");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where ScoringId =:ScoringId");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(scoringMetrics);

		logger.debug("Leaving");
		return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
	}

	@Override
	public List<ScoringMetrics> getScoreMatricsListByCustType(String scoreRleCode, String custType) {
		logger.debug("Entering");

		ScoringMetrics scoringMetrics = new ScoringMetrics();
		scoringMetrics.setCategoryType(custType);

		StringBuilder selectSql = new StringBuilder("Select m.ScoreGroupId, m.ScoringId, m.CategoryType, ");
		selectSql.append(" t2.rulecode lovdescscoringcode ,t2.rulecodedesc lovdescscoringcodedesc ,");
		selectSql.append(" t2.sqlrule lovdescsqlrule, t2.seqorder lovdescscoremetricseq ");
		selectSql.append(" from plf.rmtscoringmetrics m ");
		selectSql.append(" inner join plf.rmtscoringgroup g on m.scoregroupid = g.scoregroupid ");
		selectSql.append(" and m.categorytype='R' ");
		selectSql.append(" inner JOIN rules t2 ON t2.ruleid = M.scoringid ");
		selectSql.append(" and g.scoregroupcode= :scoreGrooupCode");
		selectSql.append(" AND t2.rulemodule= 'SCORES' ");

		logger.debug("selectSql: " + selectSql.toString());
		MapSqlParameterSource beanParameters = new MapSqlParameterSource();
		beanParameters.addValue("scoreGrooupCode", scoreRleCode);

		RowMapper<ScoringMetrics> typeRowMapper = BeanPropertyRowMapper.newInstance(ScoringMetrics.class);

		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

}