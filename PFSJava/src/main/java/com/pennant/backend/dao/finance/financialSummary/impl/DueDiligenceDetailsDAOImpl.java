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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.finance.financialSummary.DueDiligenceDetailsDAO;
import com.pennant.backend.model.finance.financialsummary.DueDiligenceDetails;
import com.pennant.backend.model.finance.financialsummary.SanctionConditions;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

public class DueDiligenceDetailsDAOImpl extends SequenceDao<DueDiligenceDetails> implements DueDiligenceDetailsDAO {
	private static Logger logger = Logger.getLogger(DueDiligenceDetailsDAOImpl.class);

	public DueDiligenceDetailsDAOImpl() {
		super();
	}

	@Override
	public List<DueDiligenceDetails> getDueDiligenceDetails(String finReference) {
		logger.debug(Literal.ENTERING);

		DueDiligenceDetails dueDiligenceDetails = new DueDiligenceDetails();
		dueDiligenceDetails.setFinReference(finReference);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT  T1.id,T1.ParticularId,T3.Particulars,T1.Status,T1.Remarks");
		selectSql.append(", T1.Version, T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode");
		selectSql.append(", T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId");
		selectSql.append(" FROM  DUE_DILIGENCES_TEMP T1");
		selectSql.append(" LEFT JOIN FinanceMain_TEMP T2 ON T2.finreference =  T1.finreference");
		selectSql.append(" LEFT JOIN DUE_DILIGENCE_CHECKLIST_TEMP T3 ON T3.id =  T1.ParticularId");
		selectSql.append(" UNION ALL");
		selectSql.append(" SELECT  T1.id,T1.ParticularId,T3.Particulars,T1.Status,T1.Remarks");
		selectSql.append(", T1.Version, T1.LastMntBy, T1.LastMntOn, T1.RecordStatus, T1.RoleCode, T1.NextRoleCode");
		selectSql.append(", T1.TaskId, T1.NextTaskId, T1.RecordType, T1.WorkflowId ");
		selectSql.append(" FROM  DUE_DILIGENCES T1");
		selectSql.append(" LEFT JOIN FinanceMain T2 ON T2.finreference =  T1.finreference");
		selectSql.append(" LEFT JOIN DUE_DILIGENCE_CHECKLIST T3 ON T3.id =  T1.ParticularId");
		selectSql.append(" WHERE NOT EXISTS ( SELECT 1 FROM SANCTION_CONDITIONS_TEMP WHERE id = T1.id)");
		selectSql.append(" AND T1.finReference = :finReference");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(dueDiligenceDetails);
		RowMapper<DueDiligenceDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(DueDiligenceDetails.class);

		List<DueDiligenceDetails> dueDiligenceDetailsList = this.jdbcTemplate.query(selectSql.toString(),
				beanParameters, typeRowMapper);
		logger.debug("Leaving ");
		return dueDiligenceDetailsList;
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
		insertSql
				.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, ");
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
		updateSql
				.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode,NextRoleCode = :NextRoleCode, TaskId = :TaskId,");
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
		selectSql.append("SELECT status FROM DUE_DILIGENCE_CHECKLIST_TEMP");
		selectSql.append(" WHERE id = :id");

		logger.debug("insertSql: " + selectSql.toString());

		logger.debug("Leaving");

		return this.jdbcTemplate.queryForObject(selectSql.toString(), source, String.class);
	}

}