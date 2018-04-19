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

package com.pennanttech.pennapps.pff.verification.dao;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.pff.verification.model.TechnicalVerification;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * Data access layer implementation for <code>TechnicalVerification</code> with set of CRUD operations.
 */
public class TechnicalVerificationDAOImpl extends SequenceDao<TechnicalVerification>
		implements TechnicalVerificationDAO {
	private static Logger logger = LogManager.getLogger(TechnicalVerificationDAOImpl.class);

	public TechnicalVerificationDAOImpl() {
		super();
	}

	@Override
	public String save(TechnicalVerification technicalVerification, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" insert into verification_fi");
		sql.append(tableType.getSuffix());
		sql.append("(verificationid,name, addresstype, houseNumber, flatnumber, street,");
		sql.append(" addressLine1, addressLine2, addressLine3, addressLine4, addressLine5,");
		sql.append(" poBox, country,");
		sql.append(" province, city, zipcode, contactNumber1, contactNumber2,");
		sql.append(" date, type, yearsatpresentaddress, personmet, ownershipstatus, relationship,");
		sql.append(" neighbourhoodfeedback, contactnumber, observationremarks, livingstandard, negativecheck,");
		sql.append(" noofattempts, agentcode, agentname, status, reason, summaryremarks,");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		sql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");

		sql.append("values (:verificationId, :name, :addressType, :houseNumber, :flatNumber, :street,");
		sql.append(" :addressLine1, :addressLine2, :addressLine3, :addressLine4, :addressLine5,");
		sql.append(" :poBox, :country, :province, :city, :zipCode, :contactNumber1, :contactNumber2,");
		sql.append(" :date, :type, :yearsAtPresentAddress, ");
		sql.append(" :personMet, :ownershipStatus, :relationship, ");
		sql.append(" :neighbourhoodFeedBack, :contactNumber, :observationRemarks, ");
		sql.append(" :livingStandard, :negativeCheck, :noofAttempts, ");
		sql.append(" :agentCode, :agentName, :status, :reason, :summaryRemarks, ");
		sql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode,");
		sql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(technicalVerification);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(technicalVerification.getId());
	}

	@Override
	public void update(TechnicalVerification technicalVerification, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update verification_fi");
		sql.append(tableType.getSuffix());
		sql.append(" set date = :date, type = :type, yearsatpresentaddress = :yearsAtPresentAddress, ");
		sql.append(" personmet = :personMet, ownershipstatus = :ownershipStatus, relationship = :relationship, ");
		sql.append(
				" neighbourhoodfeedback = :neighbourhoodFeedBack, contactnumber = :contactNumber, observationremarks = :observationRemarks, ");
		sql.append(" livingstandard = :livingStandard, negativecheck = :negativeCheck, noofattempts = :noofAttempts, ");
		sql.append(" agentcode = :agentCode, agentname = :agentName, status = :status, ");
		sql.append(" reason = :reason, summaryremarks = :summaryRemarks, Version = :Version, LastMntBy = :LastMntBy,");
		sql.append(" LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where verificationId = :id ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(technicalVerification);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(TechnicalVerification technicalVerification, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from verification_fi");
		sql.append(tableType.getSuffix());
		sql.append(" where verificationid = :id ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(technicalVerification);
		int recordCount = 0;

		try {
			recordCount = jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public TechnicalVerification getTechnicalVerification(long id, String type) {
		TechnicalVerification technicalVerification = new TechnicalVerification();
		technicalVerification.setId(id);
		StringBuilder selectSql = new StringBuilder();

		selectSql.append(" Select custcif, keyreference, collateraltype, Collateralref,");
		selectSql.append(
				" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" FROM  Technical_Verification");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where Collateralref = 'CT1804500002' ");

		logger.trace(Literal.SQL + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(technicalVerification);
		RowMapper<TechnicalVerification> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(TechnicalVerification.class);

		try {
			technicalVerification = jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			technicalVerification = null;
		}

		logger.debug(Literal.LEAVING);
		return technicalVerification;
	}
}
