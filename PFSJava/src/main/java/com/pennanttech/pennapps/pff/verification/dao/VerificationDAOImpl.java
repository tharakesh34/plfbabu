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
package com.pennanttech.pennapps.pff.verification.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.pff.verification.Agencies;
import com.pennanttech.pennapps.pff.verification.VerificationType;
import com.pennanttech.pennapps.pff.verification.WaiverReasons;
import com.pennanttech.pennapps.pff.verification.model.Verification;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * Data access layer implementation for <code>Verification</code> with set of CRUD operations.
 */
public class VerificationDAOImpl extends BasicDao<Verification> implements VerificationDAO {
	private static Logger logger = Logger.getLogger(VerificationDAOImpl.class);

	public VerificationDAOImpl() {
		super();
	}

	@Override
	public List<Verification> getVeriFications(String keyReference, int verificationType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("select ");
		sql.append(" v.id, verificationType, module, keyReference, referenceType, reference, ");
		sql.append(" referenceFor, c.custId, c.custCif as cif, c.custshrtname customerName,");
		sql.append(" requesttype, reinitid, agency, a.dealerName agencyName, reason, r.code reasonName, remarks, ");
		sql.append(" createdBy, createdOn, status, agencyRemarks, agencyReason, decision, ");
		sql.append(" verificationDate, decisionRemarks, ");
		sql.append(" v.LastMntOn, v.LastMntBy");
		sql.append(" From verifications v");
		sql.append(" left join customers c on c.custId = v.custId");
		sql.append(" left join amtvehicledealer a on a.dealerid = v.agency and dealerType = :dealerType");
		sql.append(" left join reasons_aview r on r.id = v.reason and reasontypecode = :reasontypecode");
		sql.append(" Where keyReference = :keyReference and verificationType = :verificationType");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		if (verificationType == VerificationType.FI.getKey()) {
			parameterSource.addValue("dealerType", Agencies.FIAGENCY.getKey());
			parameterSource.addValue("reasontypecode", WaiverReasons.FIWRES.getKey());
		} else if (verificationType == VerificationType.TV.getKey()) {
			parameterSource.addValue("dealerType", Agencies.TVAGENCY.getKey());
			parameterSource.addValue("reasontypecode", WaiverReasons.TVWRES.getKey());
		}
		parameterSource.addValue("keyReference", keyReference);
		parameterSource.addValue("verificationType", verificationType);

		RowMapper<Verification> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Verification.class);

		try {
			return jdbcTemplate.query(sql.toString(), parameterSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {

		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<Verification>();
	}

	@Override
	public String save(Verification entity, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("insert into verifications");
		sql.append("(verificationType, module, keyReference, referenceType, reference,");
		sql.append(" referenceFor, custId, requestType, reinitid, agency, reason, remarks,");
		sql.append(" createdBy, createdOn, status, agencyReason, agencyRemarks,");
		sql.append(" verificationDate, decision, decisionRemarks,");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		sql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values(");
		sql.append(":verificationType, :module, :keyReference, :referenceType, :reference,");
		sql.append(" :referenceFor, :custId, :requestType, :reinitid, :agency, :reason, :remarks,");
		sql.append(" :createdBy, :createdOn, :status, :agencyReason, :agencyRemarks,");
		sql.append(" :verificationDate, :decision,  :decisionRemarks,");
		sql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode,");
		sql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		KeyHolder keyHolder = new GeneratedKeyHolder();
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(entity);

		try {
			jdbcTemplate.update(sql.toString(), paramSource, keyHolder, new String[] { "id" });
			entity.setId(keyHolder.getKey().longValue());
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(entity.getId());
	}

	@Override
	public void update(Verification verification, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update verifications");
		sql.append(tableType.getSuffix());
		sql.append(" set verificationType = :verificationType, module = :module, keyReference = :keyReference, ");
		sql.append(" referenceType = :referenceType, reference = :reference, referenceFor = :referenceFor, ");
		sql.append(" requestType = :requestType, reinitid = :reinitid, agency = :agency, ");
		sql.append(" reason = :reason, remarks = :remarks, createdBy = :createdBy, ");
		sql.append(" createdOn = :createdOn, status = :status, agencyRemarks = :agencyRemarks, ");
		sql.append(" agencyReason = :agencyReason, decision = :decision, verificationDate = :verificationDate, ");
		sql.append(" decisionRemarks = :decisionRemarks, ");
		sql.append(
				" Version = :Version, LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, ");
		sql.append(" RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where id = :id ");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(verification);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void updateReInit(Verification verification, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update verifications");
		sql.append(tableType.getSuffix());
		sql.append(" set reinitid = :reinitid, Version = :Version,");
		sql.append(" LastMntBy = :LastMntBy, LastMntOn = :LastMntOn where id = :id ");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(verification);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(Verification verification, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from verifications");
		sql.append(tableType.getSuffix());
		sql.append(" where id = :id ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(verification);
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

	public void updateVerifiaction(long verificationId, Date verificationDate, int status) {
		StringBuilder sql = new StringBuilder("update verifications");
		sql.append(" set verificationdate = :verificationdate, status = :status ");
		sql.append(" where id = :id ");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("id", verificationId);
		parameterSource.addValue("verificationdate", verificationDate);
		parameterSource.addValue("status", status);

		int recordCount = jdbcTemplate.update(sql.toString(), parameterSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

}
