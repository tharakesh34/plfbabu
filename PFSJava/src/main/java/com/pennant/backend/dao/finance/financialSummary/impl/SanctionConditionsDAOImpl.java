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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.finance.financialSummary.SanctionConditionsDAO;
import com.pennant.backend.model.finance.financialsummary.SanctionConditions;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * DAO methods implementation for the <b>CustomerPhoneNumber model</b> class.<br>
 * 
 */
public class SanctionConditionsDAOImpl extends SequenceDao<SanctionConditions> implements SanctionConditionsDAO {
	private static Logger logger = Logger.getLogger(SanctionConditionsDAOImpl.class);

	public SanctionConditionsDAOImpl() {
		super();
	}

	public List<SanctionConditions> getSanctionConditions(String finReference) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" t1.Id, t1.SeqNo, t1.SanctionCondition, t1.Status, t1.FinReference, t1.Version");
		sql.append(", t1.LastMntBy, t1.LastMntOn, t1.RecordStatus, t1.RoleCode, t1.NextRoleCode");
		sql.append(", t1.TaskId, t1.NextTaskId, t1.RecordType, t1.WorkflowId");
		sql.append(" from SANCTION_CONDITIONS_TEMP t1");
		sql.append(" LEFT JOIN FinanceMain t2 ON t2.finreference =  t1.finreference");
		sql.append(" where t1.finReference = ?");
		sql.append(" UNION ALL");
		sql.append(" Select");
		sql.append(" t1.Id, t1.SeqNo, t1.SanctionCondition, t1.Status, t1.FinReference, t1.Version");
		sql.append(", t1.LastMntBy, t1.LastMntOn, t1.RecordStatus, t1.RoleCode, t1.NextRoleCode");
		sql.append(", t1.TaskId, t1.NextTaskId, t1.RecordType, t1.WorkflowId");
		sql.append(" from SANCTION_CONDITIONS t1");
		sql.append(" LEFT JOIN FinanceMain t2 ON t2.finreference =  t1.finreference");
		sql.append(" where NOT EXISTS ( SELECT 1 FROM SANCTION_CONDITIONS_TEMP where id = t1.id)");
		sql.append(" and t1.finReference = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int index = 1;
					ps.setString(index++, finReference);
					ps.setString(index++, finReference);
				}
			}, new RowMapper<SanctionConditions>() {
				@Override
				public SanctionConditions mapRow(ResultSet rs, int rowNum) throws SQLException {
					SanctionConditions sc = new SanctionConditions();

					sc.setId(rs.getLong("Id"));
					sc.setSeqNo(rs.getLong("SeqNo"));
					sc.setSanctionCondition(rs.getString("SanctionCondition"));
					sc.setStatus(rs.getString("Status"));
					sc.setFinReference(rs.getString("FinReference"));
					sc.setVersion(rs.getInt("Version"));
					sc.setLastMntBy(rs.getLong("LastMntBy"));
					sc.setLastMntOn(rs.getTimestamp("LastMntOn"));
					sc.setRecordStatus(rs.getString("RecordStatus"));
					sc.setRoleCode(rs.getString("RoleCode"));
					sc.setNextRoleCode(rs.getString("NextRoleCode"));
					sc.setTaskId(rs.getString("TaskId"));
					sc.setNextTaskId(rs.getString("NextTaskId"));
					sc.setRecordType(rs.getString("RecordType"));
					sc.setWorkflowId(rs.getLong("WorkflowId"));

					return sc;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
	}

	/**
	 * This method Deletes the Record from the CustomerPhoneNumbers or CustomerPhoneNumbers_Temp. if Record not deleted
	 * then throws DataAccessException with error 41003. delete Customer PhoneNumbers by key PhoneCustID
	 * 
	 * @param Customer
	 *            PhoneNumbers (customerPhoneNumber)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(SanctionConditions sanctionConditions, String type) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append(" Delete From SANCTION_CONDITIONS");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where id =:id and finReference =:finReference");

		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(sanctionConditions);

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
	 * This method insert new Records into CustomerPhoneNumbers or CustomerPhoneNumbers_Temp.
	 *
	 * save Customer PhoneNumbers
	 * 
	 * @param Customer
	 *            PhoneNumbers (customerPhoneNumber)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public long save(SanctionConditions sanctionConditions, String type) {
		logger.debug("Entering");

		if (sanctionConditions.getId() == Long.MIN_VALUE) {
			sanctionConditions.setId(getNextValue("SEQ_SANCTION_CONDITIONS"));
			logger.debug("get NextID:" + sanctionConditions.getId());
		}

		StringBuilder insertSql = new StringBuilder();
		insertSql.append(" Insert Into SANCTION_CONDITIONS");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (id, SeqNo, SanctionCondition,Status,FinReference");
		insertSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		insertSql.append(", TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append("Values(:id, :SeqNo, :SanctionCondition, :Status, :FinReference");
		insertSql.append(", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode");
		insertSql.append(", :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(sanctionConditions);

		try {
			this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug("Leaving");
		return sanctionConditions.getId();
	}

	/**
	 * This method updates the Record CustomerPhoneNumbers or CustomerPhoneNumbers_Temp. if Record not updated then
	 * throws DataAccessException with error 41004. update Customer PhoneNumbers by key PhoneCustID and Version
	 * 
	 * @param Customer
	 *            PhoneNumbers (customerPhoneNumber)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(SanctionConditions sanctionConditions, String type) {
		int recordCount = 0;
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder();
		updateSql.append(" Update SANCTION_CONDITIONS");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set SanctionCondition = :SanctionCondition, Status = :Status");
		updateSql.append(", Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn");
		updateSql.append(", RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode");
		updateSql.append(", TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType");
		updateSql.append(", WorkflowId = :WorkflowId");
		updateSql.append(" Where id =:id and finReference =:finReference");

		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(sanctionConditions);
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
	public int getVersion(long id, String sanctionCondition) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("id", id);
		source.addValue("sanctionCondition", sanctionCondition);

		StringBuffer selectSql = new StringBuffer();
		selectSql.append("SELECT Version FROM SANCTION_CONDITIONS");

		selectSql.append(" WHERE id = :id AND finReference = :finReference");

		logger.debug("insertSql: " + selectSql.toString());

		logger.debug("Leaving");

		return this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
	}

}