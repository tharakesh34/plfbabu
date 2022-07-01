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
import java.util.List;

import org.apache.commons.lang.StringUtils;
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

import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pennapps.pff.verification.model.PersonalDiscussion;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * Data access layer implementation for <code>PersonalDiscussion</code> with set of CRUD operations.
 */
public class PersonalDiscussionDAOImpl extends SequenceDao<PersonalDiscussion> implements PersonalDiscussionDAO {
	private static Logger logger = LogManager.getLogger(PersonalDiscussionDAOImpl.class);

	public PersonalDiscussionDAOImpl() {
		super();
	}

	@Override
	public List<PersonalDiscussion> getList(String keyReference) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(
				"select verificationid,name, addresstype, houseNumber, flatnumber, street,");
		sql.append(" addressLine1, addressLine2, addressLine3, addressLine4, addressLine5, poBox, country,");
		sql.append(" province, city, zipcode, contactNumber1, contactNumber2,");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		sql.append(" TaskId, NextTaskId, RecordType, WorkflowId, keyreference");
		sql.append(" From verification_pd_view");
		sql.append(" Where keyreference = :keyreference");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("keyreference", keyReference);

		RowMapper<PersonalDiscussion> rowMapper = BeanPropertyRowMapper.newInstance(PersonalDiscussion.class);

		return jdbcTemplate.query(sql.toString(), paramSource, rowMapper);
	}

	@Override
	public List<PersonalDiscussion> getList(String[] cif) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder();
		sql.append("select * from verification_pd_view");
		sql.append(" Where cif in(:cif) and verificationdate is not null");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("cif", Arrays.asList(cif));

		RowMapper<PersonalDiscussion> rowMapper = BeanPropertyRowMapper.newInstance(PersonalDiscussion.class);

		return jdbcTemplate.query(sql.toString(), paramSource, rowMapper);
	}

	@Override
	public String save(PersonalDiscussion personalDiscussion, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" insert into verification_pd");
		sql.append(tableType.getSuffix());
		sql.append("(verificationid,name, addresstype, houseNumber, flatnumber, street,");
		sql.append(" addressLine1, addressLine2, addressLine3, addressLine4, addressLine5,");
		sql.append(" poBox, country,");
		sql.append(" province, city, zipcode, contactNumber1, contactNumber2,");
		sql.append(" verifiedDate, agentcode, agentname, status, reason, summaryremarks,");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		sql.append(" TaskId, NextTaskId, RecordType, WorkflowId)");

		sql.append("values (:verificationId, :name, :addressType, :houseNumber, :flatNumber, :street,");
		sql.append(" :addressLine1, :addressLine2, :addressLine3, :addressLine4, :addressLine5,");
		sql.append(" :poBox, :country, :province, :city, :zipCode, :contactNumber1, :contactNumber2,");
		sql.append(" :verifiedDate, :agentCode, :agentName, :status, :reason, :summaryRemarks, ");
		sql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode,");
		sql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(personalDiscussion);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(personalDiscussion.getId());
	}

	@Override
	public void update(PersonalDiscussion personalDiscussion, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update verification_pd");
		sql.append(tableType.getSuffix());
		sql.append(
				" set verifiedDate = :verifiedDate, agentcode = :agentCode, agentname = :agentName, status = :status, ");
		sql.append(" reason = :reason, summaryremarks = :summaryRemarks, Version = :Version, LastMntBy = :LastMntBy,");
		sql.append(" LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where verificationId = :id ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(personalDiscussion);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(PersonalDiscussion personalDiscussion, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from verification_pd");
		sql.append(tableType.getSuffix());
		sql.append(" where verificationid = :id ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(personalDiscussion);
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
	public PersonalDiscussion getPersonalDiscussion(long id, String type) {
		StringBuilder sql = new StringBuilder();
		sql.append(" Select verificationid, name, addresstype, housenumber,flatnumber, street,");
		sql.append(
				" addressline1, addressline2, addressline3, addressline4, addressline5, pobox, country, province, city,");
		sql.append(" zipcode, contactnumber1, contactnumber2, verifiedDate, ");
		sql.append(" agentcode, agentname, status, reason, summaryremarks,");
		if ("_view".equalsIgnoreCase(type)) {
			sql.append(
					"cif, custid, keyreference, createdon, reasoncode, reasondesc, countryDesc, provinceDesc, cityDesc, ");
		}
		sql.append(
				" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" FROM  verification_pd");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where verificationid =:id");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("id", id);

		try {
			return jdbcTemplate.queryForObject(sql.toString(), mapSqlParameterSource,
					BeanPropertyRowMapper.newInstance(PersonalDiscussion.class));
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}
}
