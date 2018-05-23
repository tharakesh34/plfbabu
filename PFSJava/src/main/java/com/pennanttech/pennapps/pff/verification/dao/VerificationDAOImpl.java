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
import java.util.Arrays;
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
		sql.append(
				" requesttype, reinitid, agency, a.dealerName agencyName, reason, r.description reasonName, remarks, ");
		sql.append(" createdBy, createdOn, status, agencyRemarks, agencyReason, decision, ");
		sql.append(" verificationDate, decisionRemarks, ");
		sql.append(" v.LastMntOn, v.LastMntBy");
		sql.append(" From verifications v");
		sql.append(" left join customers c on c.custId = v.custId");
		sql.append(" left join amtvehicledealer a on a.dealerid = v.agency and dealerType = :dealerType");
		sql.append(" left join reasons_aview r on r.id = v.reason and reasontypecode = :reasontypecode");
		sql.append(" Where keyReference = :keyReference and verificationType = :verificationType order by v.id desc");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		if (verificationType == VerificationType.FI.getKey()) {
			parameterSource.addValue("dealerType", Agencies.FIAGENCY.getKey());
			parameterSource.addValue("reasontypecode", WaiverReasons.FIWRES.getKey());
		} else if (verificationType == VerificationType.TV.getKey()) {
			parameterSource.addValue("dealerType", Agencies.TVAGENCY.getKey());
			parameterSource.addValue("reasontypecode", WaiverReasons.TVWRES.getKey());
		} else if (verificationType == VerificationType.LV.getKey()) {
			parameterSource.addValue("dealerType", Agencies.LVAGENCY.getKey());
			parameterSource.addValue("reasontypecode", WaiverReasons.LVWRES.getKey());
		} else if (verificationType == VerificationType.RCU.getKey()) {
			parameterSource.addValue("dealerType", Agencies.RCUVAGENCY.getKey());
			parameterSource.addValue("reasontypecode", WaiverReasons.RCUWRES.getKey());
		}

		parameterSource.addValue("keyReference", keyReference);
		parameterSource.addValue("verificationType", verificationType);

		RowMapper<Verification> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Verification.class);

		try {
			return jdbcTemplate.query(sql.toString(), parameterSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {

		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
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
		sql.append(" reason = :reason, remarks = :remarks, agencyRemarks = :agencyRemarks, ");
		sql.append(" agencyReason = :agencyReason, decision = :decision, ");
		sql.append(" decisionRemarks = :decisionRemarks, ");
		sql.append(" LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, ");
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
		sql.append(" set reinitid = :reinitid, Version = :Version, decision=:decision,");
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
		StringBuilder sql = new StringBuilder("delete from verifications where id = :id ");

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
		logger.debug(Literal.ENTERING);

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

	@Override
	public Long getVerificationIdByReferenceFor(String finReference, String referenceFor, int verificationType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("select id from verifications");
		sql.append(
				" where referenceFor=:referenceFor and verificationType=:verificationType and keyReference=:keyReference");

		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("keyReference", finReference);
		paramMap.addValue("referenceFor", referenceFor);
		paramMap.addValue("verificationType", verificationType);
		try {
			Long verificationId = jdbcTemplate.queryForObject(sql.toString(), paramMap, Long.class);
			if (verificationId != null) {
				return verificationId;
			}
		} catch (EmptyResultDataAccessException e) {

		}

		logger.debug(Literal.LEAVING);
		return null;
	}

	@Override
	public Verification getVerificationById(long id) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("select");
		sql.append(" v.id, verificationType, module, keyReference, referenceType, reference, ");
		sql.append(" referenceFor,");
		sql.append(
				" requesttype, reinitid, agency, a.dealerName agencyName,a.dealerCity agencyCity, reason, remarks, ");
		sql.append(" createdBy, createdOn, status, agencyRemarks, agencyReason, decision, ");
		sql.append(" verificationDate, decisionRemarks,a.dealerName agencyName ");
		sql.append(" from verifications v left join AMTVehicleDealer_AView a on a.dealerid=v.agency");
		sql.append(" where id=:id and a.dealerType=:dealerType");

		RowMapper<Verification> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Verification.class);
		MapSqlParameterSource paramMap = new MapSqlParameterSource();

		paramMap.addValue("dealerType", Agencies.LVAGENCY.getKey());
		paramMap.addValue("id", id);
		try {
			return jdbcTemplate.queryForObject(sql.toString(), paramMap, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
		}
		logger.debug(Literal.LEAVING);
		return null;
	}

	@Override
	public Verification getLastStatus(Verification verification) {
		logger.debug(Literal.ENTERING);
		
		int type = verification.getVerificationType();

		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		RowMapper<Verification> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Verification.class);
		StringBuilder sql = new StringBuilder();
		
		sql.append("select v.id, v.verificationDate, coalesce(v.status, 0) status, a.dealerName as lastAgency from verifications v");
		sql.append(" left join AMTVehicleDealer_AView a on a.dealerid = v.agency and dealerType = :dealerType");

		if (verification.getVerificationType() == VerificationType.LV.getKey()
				|| verification.getVerificationType() == VerificationType.TV.getKey()) {
			sql.append(" where v.id = (select coalesce(max(id), 0)");
			sql.append(" from verifications where referenceFor = :referenceFor ");
			sql.append(" and verificationType = :verificationType and verificationdate is not null and status !=0)");
		} else if (verification.getVerificationType() == VerificationType.FI.getKey()) {
			sql.append(" where Id = (select coalesce(max(id), 0)");
			sql.append(" from verifications where custid = :custid and referenceFor = :referenceFor");
			sql.append(" and verificationType = :verificationType and verificationdate is not null and status !=0)");
		}
		
		paramMap.addValue("referenceFor", verification.getReferenceFor());
		paramMap.addValue("verificationType", verification.getVerificationType());
		paramMap.addValue("custid", verification.getCustId());
		
		if (type == VerificationType.FI.getKey()) {
			paramMap.addValue("dealerType", Agencies.FIAGENCY.getValue());
		} else if (type == VerificationType.TV.getKey()) {
			paramMap.addValue("dealerType", Agencies.TVAGENCY.getValue());
		} else if (type == VerificationType.LV.getKey()) {
			paramMap.addValue("dealerType", Agencies.LVAGENCY.getValue());
		} else if (type == VerificationType.RCU.getKey()) {
			paramMap.addValue("dealerType", Agencies.RCUVAGENCY.getValue());
		}
				
		try {
			return jdbcTemplate.queryForObject(sql.toString(), paramMap, rowMapper);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return null;

	}

	@Override
	public List<Verification> getCollateralDetails(String[] collaterals) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder();

		sql.append("select cs.collateralref referenceFor,");
		sql.append(" cs.collateraltype referenceType, depositorcif reference,");
		sql.append(" cs.depositorname customerName,  ct.collateralvaluatorreq verificationReq");
		sql.append(" from collateralsetup_view cs");
		sql.append(" inner join collateralstructure_view ct on ct.collateraltype = cs.collateraltype");
		sql.append(" Where cs.collateralref in(:referenceFor)");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource parameterSource = new MapSqlParameterSource();

		parameterSource.addValue("referenceFor", Arrays.asList(collaterals));

		RowMapper<Verification> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Verification.class);

		try {
			return jdbcTemplate.query(sql.toString(), parameterSource, rowMapper);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
	}

}
