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

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pennapps.pff.verification.Agencies;
import com.pennanttech.pennapps.pff.verification.DocumentType;
import com.pennanttech.pennapps.pff.verification.RequestType;
import com.pennanttech.pennapps.pff.verification.VerificationType;
import com.pennanttech.pennapps.pff.verification.WaiverReasons;
import com.pennanttech.pennapps.pff.verification.model.RCUDocument;
import com.pennanttech.pennapps.pff.verification.model.Verification;
import com.pennanttech.pff.core.TableType;

/**
 * Data access layer implementation for <code>Verification</code> with set of CRUD operations.
 */
public class VerificationDAOImpl extends BasicDao<Verification> implements VerificationDAO {
	private static Logger logger = LogManager.getLogger(VerificationDAOImpl.class);

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
				" requesttype, reinitid, agency,verificationcategory, a.dealerName agencyName, reason, r.code reasonName, remarks, ");
		sql.append(" v.createdBy, v.createdOn, status, agencyRemarks, agencyReason, decision, ");
		sql.append(" verificationDate, decisionRemarks, ");
		sql.append(" v.LastMntOn, v.LastMntBy");
		sql.append(" From verifications v");
		sql.append(" left join customers c on c.custCif = v.Reference");
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
		} else if (verificationType == VerificationType.PD.getKey()) {
			parameterSource.addValue("dealerType", Agencies.PDAGENCY.getKey());
			parameterSource.addValue("reasontypecode", WaiverReasons.PDWRES.getKey());
		} else if (verificationType == VerificationType.VETTING.getKey()) {
			parameterSource.addValue("dealerType", Agencies.LVAGENCY.getKey());
			parameterSource.addValue("reasontypecode", WaiverReasons.LVWRES.getKey());
		}

		parameterSource.addValue("keyReference", keyReference);
		parameterSource.addValue("verificationType", verificationType);

		RowMapper<Verification> rowMapper = BeanPropertyRowMapper.newInstance(Verification.class);

		return jdbcTemplate.query(sql.toString(), parameterSource, rowMapper);
	}

	@Override
	public String save(Verification entity, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("insert into verifications");
		sql.append("(verificationType, module, keyReference, referenceType, reference,");
		sql.append(" referenceFor, custId, requestType, reinitid, agency,verificationCategory, reason, remarks,");
		sql.append(" createdBy, createdOn, status, agencyReason, agencyRemarks,");
		sql.append(" verificationDate, decision, decisionRemarks,");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		sql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values(");
		sql.append(":verificationType, :module, :keyReference, :referenceType, :reference,");
		sql.append(
				" :referenceFor, :custId, :requestType, :reinitid, :agency, :verificationCategory, :reason, :remarks,");
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
		sql.append(
				" referenceType = :referenceType, reference = :reference, referenceFor = :referenceFor, custId = :custId,");
		sql.append(
				" requestType = :requestType, reinitid = :reinitid, agency = :agency, verificationcategory = :verificationCategory, ");
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
				" where referenceFor=:referenceFor and verificationType=:verificationType and keyReference=:keyReference and requestType=:requestType");

		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("keyReference", finReference);
		paramMap.addValue("referenceFor", referenceFor);
		paramMap.addValue("verificationType", verificationType);
		paramMap.addValue("requestType", RequestType.INITIATE.getKey());
		try {
			Long verificationId = jdbcTemplate.queryForObject(sql.toString(), paramMap, Long.class);
			if (verificationId != null) {
				return verificationId;
			}
		} catch (Exception e) {
			return null;
		}

		return null;
	}

	@Override
	public Verification getVerificationById(long id) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("select");
		sql.append(" v.id, verificationType, module, keyReference, referenceType, reference, ");
		sql.append(" referenceFor, v.verificationCategory,");
		sql.append(
				" requesttype, reinitid, agency, a.dealerName agencyName,a.dealerCity agencyCity, reason, remarks, ");
		sql.append(" createdBy, createdOn, status, agencyRemarks, agencyReason, decision, ");
		sql.append(" verificationDate, decisionRemarks,a.dealerName agencyName ");
		sql.append(" from verifications v left join AMTVehicleDealer_AView a on a.dealerid=v.agency");
		sql.append(" where id=:id and a.dealerType=:dealerType");

		RowMapper<Verification> rowMapper = BeanPropertyRowMapper.newInstance(Verification.class);
		MapSqlParameterSource paramMap = new MapSqlParameterSource();

		paramMap.addValue("dealerType", Agencies.LVAGENCY.getKey());
		paramMap.addValue("id", id);
		try {
			return jdbcTemplate.queryForObject(sql.toString(), paramMap, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public Verification getLastStatus(Verification verification) {
		logger.debug(Literal.ENTERING);

		int type = verification.getVerificationType();

		// FIXME : need to check for Legal Vetting

		if (type == VerificationType.VETTING.getKey()) {
			return null;
		}

		RCUDocument rcuDocument = null;

		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		RowMapper<Verification> rowMapper = BeanPropertyRowMapper.newInstance(Verification.class);
		StringBuilder sql = new StringBuilder();

		sql.append("select v.id, v.verificationDate, coalesce(v.status, 0) status, a.dealerName as lastAgency");
		sql.append(" from verifications v");
		sql.append(" left join AMTVehicleDealer_AView a on a.dealerid = v.agency and dealerType = :dealerType");

		if (type == VerificationType.FI.getKey()) {
			sql.append(" where Id = (select coalesce(max(id), 0)");
			sql.append(" from verifications where custid = :custid and referenceFor = :referenceFor");
			sql.append(" and verificationType = :verificationType and verificationdate is not null and status !=0)");
		} else if (type == VerificationType.LV.getKey() || type == VerificationType.TV.getKey()) {
			sql.append(" where v.id = (select coalesce(max(id), 0)");
			sql.append(" from verifications where referenceFor = :referenceFor ");
			sql.append(" and verificationType = :verificationType and verificationdate is not null and status !=0)");
		} else if (type == VerificationType.RCU.getKey()) {
			sql.append(" where v.id = (select coalesce(max(id), 0)");
			sql.append(" from verifications v ");
			sql.append(" inner join verification_rcu_details_view vd on vd.verificationid = v.id");

			rcuDocument = verification.getRcuDocument();
			if (rcuDocument.getDocumentType() == DocumentType.COLLATRL.getKey()) {
				sql.append(" where vd.documentid = :documentid");
			} else {
				sql.append(" where vd.documentid = :documentid and vd.documentsubid = :referenceFor");
				sql.append(" and vd.documenttype=:documenttype");
			}

			sql.append(
					" and v.verificationType = :verificationType and v.verificationdate is not null and v.status !=0)");
		} else if (type == VerificationType.PD.getKey()) {
			sql.append(" where Id = (select coalesce(max(id), 0)");
			sql.append(" from verifications where custid = :custid and referenceFor = :referenceFor");
			sql.append(" and verificationType = :verificationType and verificationdate is not null and status !=0)");
		}

		try {
			paramMap.addValue("referenceFor", verification.getReferenceFor());
			paramMap.addValue("verificationType", type);
			paramMap.addValue("custid", verification.getCustId());

			if (type == VerificationType.FI.getKey()) {
				paramMap.addValue("dealerType", Agencies.FIAGENCY.getKey());
			} else if (type == VerificationType.TV.getKey()) {
				paramMap.addValue("dealerType", Agencies.TVAGENCY.getKey());
			} else if (type == VerificationType.LV.getKey()) {
				paramMap.addValue("dealerType", Agencies.LVAGENCY.getKey());
			} else if (type == VerificationType.RCU.getKey()) {
				paramMap.addValue("dealerType", Agencies.RCUVAGENCY.getKey());
				if (rcuDocument != null) {
					paramMap.addValue("documenttype", rcuDocument.getDocumentType());
					paramMap.addValue("documentid", rcuDocument.getDocumentId());
					paramMap.addValue("referenceFor", rcuDocument.getDocumentSubId());
				} else {
					paramMap.addValue("documenttype", 0);
					paramMap.addValue("documentid", 0);
				}
			} else if (type == VerificationType.PD.getKey()) {
				paramMap.addValue("dealerType", Agencies.PDAGENCY.getKey());
			}

			logger.debug(Literal.SQL + sql.toString());
			return jdbcTemplate.queryForObject(sql.toString(), paramMap, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
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

		RowMapper<Verification> rowMapper = BeanPropertyRowMapper.newInstance(Verification.class);

		return jdbcTemplate.query(sql.toString(), parameterSource, rowMapper);
	}

	@Override
	public List<Integer> getVerificationTypes(String keyReference) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder();
		sql.append("select distinct verificationtype from verifications");
		sql.append(" Where keyreference = :keyReference");
		sql.append(" order by verificationtype asc");
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("keyReference", keyReference);

		return jdbcTemplate.queryForList(sql.toString(), parameterSource, Integer.class);
	}

	@Override
	public List<Long> getRCUVerificationId(String finReference, int verificationType, String referencetype) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("select id from verifications");
		sql.append(
				" where verificationType=:verificationType and keyReference=:keyReference and referencetype=:referencetype");

		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("keyReference", finReference);
		paramMap.addValue("verificationType", verificationType);
		paramMap.addValue("referencetype", referencetype);

		return jdbcTemplate.queryForList(sql.toString(), paramMap, Long.class);
	}

	@Override
	public void updateDocumentId(DocumentDetails detail, Long verificationId, TableType tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("update verification_lv_details");
		sql.append(tableType.getSuffix());
		sql.append(" set documentid = :documentid, documentrefid = :documentrefid ");
		sql.append(" Where verificationid = :verificationid and documentsubid = :documentsubid ");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("documentid", detail.getDocId());
		source.addValue("documentrefid", detail.getDocRefId());
		source.addValue("documentsubid", detail.getDocCategory());
		source.addValue("verificationid", verificationId);

		jdbcTemplate.update(sql.toString(), source);

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void updateRCUReference(DocumentDetails detail, Long verificationId) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder(
				"Update verifications set reference = :reference Where referenceFor = :referenceFor and id = :id ");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("id", verificationId);
		source.addValue("reference", String.valueOf(detail.getDocId()));
		source.addValue("referenceFor", detail.getDocCategory());

		jdbcTemplate.update(sql.toString(), source);

		logger.debug(Literal.LEAVING);
	}

	@Override
	public Long getVerificationIdByReferenceFor(String finReference, String referenceFor, int verificationType,
			int requestType, int verificationCategory) {

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" id from verifications");
		sql.append(" Where ReferenceFor = ? and VerificationType = ? and KeyReference = ?");
		sql.append(" and RequestType = ? and verificationcategory = ?");

		logger.debug(Literal.SQL + sql);

		Object[] args = new Object[] { referenceFor, verificationType, finReference, requestType,
				verificationCategory };

		try {
			return jdbcOperations.queryForObject(sql.toString(), args, Long.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
		}

		return null;
	}

	@Override
	public List<Long> getVerificationIds(String finReference, int verificationType, int requestType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("select id from verifications");
		sql.append(
				" where verificationType=:verificationType and keyReference=:keyReference and requestType=:requestType");

		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("keyReference", finReference);
		paramMap.addValue("verificationType", verificationType);
		paramMap.addValue("requestType", requestType);

		return jdbcTemplate.queryForList(sql.toString(), paramMap, Long.class);
	}

	@Override
	public List<Verification> getVerificationCount(String finReference, String collateralReference,
			int verificationType, Integer tvStatus) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder(
				"select Verificationcategory,Agency,ReferenceFor, Id, Reinitid from verifications");
		sql.append(
				" where verificationType = :verificationType and keyReference = :keyReference and referencefor = :collateralReference and verificationdate is not null");
		sql.append(" and status = :status");
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("keyReference", finReference);
		paramMap.addValue("verificationType", verificationType);
		paramMap.addValue("status", tvStatus);
		paramMap.addValue("collateralReference", collateralReference);
		RowMapper<Verification> rowMapper = BeanPropertyRowMapper.newInstance(Verification.class);

		return jdbcTemplate.query(sql.toString(), paramMap, rowMapper);
	}

	@Override
	public Verification getVerificationStatus(String reference, int verificationType, String addressType,
			String custCif) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("select status from verifications");
		sql.append(
				" where keyReference = :keyReference and verificationType = :verificationType and referencefor = :referencefor and Reference =:Reference");

		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("keyReference", reference);
		paramMap.addValue("verificationType", verificationType);
		paramMap.addValue("referencefor", addressType);
		paramMap.addValue("Reference", custCif);
		RowMapper<Verification> rowMapper = BeanPropertyRowMapper.newInstance(Verification.class);

		try {
			return jdbcTemplate.queryForObject(sql.toString(), paramMap, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public List<String> getAprrovedLVVerifications(int decision, int verificationType) {
		logger.debug(Literal.ENTERING);
		StringBuilder sql = new StringBuilder("select keyReference from verifications");
		sql.append(" where decision = :decision and verificationType = :verificationType");

		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("decision", decision);
		paramMap.addValue("verificationType", verificationType);

		return jdbcTemplate.queryForList(sql.toString(), paramMap, String.class);
	}

	// Specific to API
	@Override
	public List<Verification> getVerifications(String finReference, int verificationType, int requestType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT ID, REFERENCEFOR, REQUESTTYPE, VERIFICATIONTYPE, c.CUSTSHRTNAME  FROM VERIFICATIONS v");
		sql.append(" LEFT JOIN CUSTOMERS c on c.CUSTID = v.CUSTID");
		sql.append(" WHERE KEYREFERENCE= ? and VERIFICATIONTYPE= ? and REQUESTTYPE= ?");

		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), new Object[] { finReference, verificationType, requestType },
				(rs, rowNum) -> {
					Verification vf = new Verification();

					vf.setId(rs.getLong("ID"));
					vf.setReferenceFor(rs.getString("REFERENCEFOR"));
					vf.setCustomerName(rs.getString("CUSTSHRTNAME"));
					vf.setRequestType(rs.getInt("REQUESTTYPE"));
					vf.setVerificationType(rs.getInt("VERIFICATIONTYPE"));

					return vf;
				});
	}

	@Override
	public boolean isVerificationIdExists(String finReference, String referenceFor, String reference,
			int verificationtype, String referenceType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("SELECT COUNT(ID) FROM VERIFICATIONS");
		sql.append(" WHERE REFERENCEFOR= :referenceFor AND verificationtype= :verificationtype");
		sql.append(" AND KEYREFERENCE= :keyReference ");
		if (verificationtype != 4) {
			sql.append("and REFERENCE= :reference");
		}
		if (verificationtype == 4) {
			if (referenceType.equals("CUSTOMER")) {
				sql.append("and REFERENCE= :reference");
			}
			sql.append(" and ReferenceType=:referenceType");
		}
		logger.debug(Literal.SQL + sql);

		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("referenceFor", referenceFor);
		paramMap.addValue("verificationtype", verificationtype);
		paramMap.addValue("keyReference", finReference);
		paramMap.addValue("reference", reference);
		if (verificationtype == 4) {
			paramMap.addValue("referenceType", referenceType);
		}

		return jdbcTemplate.queryForObject(sql.toString(), paramMap, Integer.class) > 0;
	}

	@Override
	public boolean isInitiatedVerfication(VerificationType verificationType, long verificationId, String type) {
		// "verification_pd_temp"
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT COUNT(verificationid) FROM ");
		sql.append("VERIFICATION");
		sql.append("_");
		sql.append(verificationType);
		sql.append(type);
		sql.append(" WHERE verificationid= :verificationid");

		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("verificationid", verificationId);

		return jdbcTemplate.queryForObject(sql.toString(), paramMap, Integer.class) > 0;
	}

	@Override
	public Long isVerificationExist(String finReference, String referenceFor, String reference, int verificationtype,
			String referenceType) {

		StringBuilder sql = new StringBuilder("SELECT ID FROM VERIFICATIONS");
		sql.append(" WHERE REFERENCEFOR= :referenceFor AND verificationtype= :verificationtype");
		sql.append(" AND KEYREFERENCE= :keyReference ");
		if (verificationtype != 4) {
			sql.append("and REFERENCE= :reference");
		}
		if (verificationtype == 4) {
			if (referenceType.equals("CUSTOMER")) {
				sql.append("and REFERENCE= :reference");
			}
			sql.append(" and ReferenceType=:referenceType");
		}
		logger.debug(Literal.SQL + sql);

		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("referenceFor", referenceFor);
		paramMap.addValue("verificationtype", verificationtype);
		paramMap.addValue("keyReference", finReference);
		paramMap.addValue("reference", reference);
		if (verificationtype == 4) {
			paramMap.addValue("referenceType", referenceType);
		}

		try {
			return jdbcTemplate.queryForObject(sql.toString(), paramMap, Long.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}
}
