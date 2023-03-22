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
 * * FileName : PaymentDetailDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 27-05-2017 * * Modified
 * Date : 27-05-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 27-05-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.payment.impl;

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

import com.pennant.backend.dao.payment.PaymentDetailDAO;
import com.pennant.pff.payment.model.PaymentDetail;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * Data access layer implementation for <code>PaymentDetail</code> with set of CRUD operations.
 */
public class PaymentDetailDAOImpl extends SequenceDao<PaymentDetail> implements PaymentDetailDAO {
	private static Logger logger = LogManager.getLogger(PaymentDetailDAOImpl.class);

	public PaymentDetailDAOImpl() {
		super();
	}

	@Override
	public PaymentDetail getPaymentDetail(long paymentDetailId, String type) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" paymentDetailId, paymentId, amountType, amount, referenceId, TaxHeaderId");
		if (type.contains("View")) {
			sql.append(", PaymentDetailId, paymentId, amountType, amount, referenceId, amountType,referenceId");
		}
		sql.append(
				", Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From PaymentDetails");
		sql.append(type);
		sql.append(" Where paymentDetailId = :paymentDetailId");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		PaymentDetail paymentDetail = new PaymentDetail();
		paymentDetail.setPaymentDetailId(paymentDetailId);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(paymentDetail);
		RowMapper<PaymentDetail> rowMapper = BeanPropertyRowMapper.newInstance(PaymentDetail.class);
		try {
			return jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public String save(PaymentDetail paymentDetail, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("insert into PaymentDetails");
		sql.append(tableType.getSuffix());
		sql.append(" (PaymentDetailId, PaymentId, AmountType, Amount, ReferenceId, TaxHeaderId");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values(");
		sql.append(" :PaymentDetailId, :PaymentId, :AmountType, :Amount, :ReferenceId, :TaxHeaderId ");
		sql.append(", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode");
		sql.append(", :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		// Get the sequence number.
		if (paymentDetail.getPaymentDetailId() <= 0) {
			paymentDetail.setPaymentDetailId(getNextValue("SeqPaymentDetails"));
		}
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(paymentDetail);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
		logger.debug(Literal.LEAVING);
		return String.valueOf(paymentDetail.getPaymentDetailId());
	}

	@Override
	public void update(PaymentDetail paymentDetail, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update PaymentDetails");
		sql.append(tableType.getSuffix());
		sql.append(
				" set paymentId = :paymentId, amount = :amount, referenceId = :referenceId, TaxHeaderId=:TaxHeaderId ");
		sql.append(", LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode");
		sql.append(", NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId");
		sql.append(", RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where paymentDetailId = :paymentDetailId ");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(paymentDetail);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(PaymentDetail paymentDetail, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from PaymentDetails");
		sql.append(tableType.getSuffix());
		sql.append(" where paymentDetailId = :paymentDetailId");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(paymentDetail);
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
	public void deleteList(PaymentDetail paymentDetail, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from PaymentDetails");
		sql.append(tableType.getSuffix());
		sql.append(" where paymentId = :paymentId");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(paymentDetail);
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
	public boolean isDuplicateKey(long paymentDetailId, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		String sql;
		String whereClause = "PaymentDetailId = :PaymentDetailId";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("PaymentDetails", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("PaymentDetails_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "PaymentDetails_Temp", "PaymentDetails" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("PaymentDetailId", paymentDetailId);

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);
		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

	@Override
	public List<PaymentDetail> getPaymentDetailList(long paymentId, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" PaymentDetailId, PaymentId, AmountType, Amount, ReferenceId, TaxHeaderId");
		sql.append(", Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", FeeTypeCode, FeeTypeDesc, TaxComponent");
		}
		sql.append(" From PaymentDetails");
		sql.append(type);
		sql.append(" Where PaymentId = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			PaymentDetail pd = new PaymentDetail();

			pd.setPaymentDetailId(rs.getLong("PaymentDetailId"));
			pd.setPaymentId(rs.getLong("PaymentId"));
			pd.setAmountType(rs.getString("AmountType"));
			pd.setAmount(rs.getBigDecimal("Amount"));
			pd.setReferenceId(rs.getLong("ReferenceId"));
			pd.setTaxHeaderId(rs.getLong("TaxHeaderId"));
			pd.setVersion(rs.getInt("Version"));
			pd.setLastMntOn(rs.getTimestamp("LastMntOn"));
			pd.setLastMntBy(rs.getLong("LastMntBy"));
			pd.setRecordStatus(rs.getString("RecordStatus"));
			pd.setRoleCode(rs.getString("RoleCode"));
			pd.setNextRoleCode(rs.getString("NextRoleCode"));
			pd.setTaskId(rs.getString("TaskId"));
			pd.setNextTaskId(rs.getString("NextTaskId"));
			pd.setRecordType(rs.getString("RecordType"));
			pd.setWorkflowId(rs.getLong("WorkflowId"));

			if (StringUtils.trimToEmpty(type).contains("View")) {
				pd.setFeeTypeCode(rs.getString("FeeTypeCode"));
				pd.setFeeTypeDesc(rs.getString("FeeTypeDesc"));
				pd.setTaxComponent(rs.getString("TaxComponent"));
			}

			return pd;
		}, paymentId);
	}

	@Override
	public boolean getPaymentId(long excessID) {
		StringBuilder sql = new StringBuilder("Select Count(PaymentID) From");
		sql.append(" (Select PaymentID From PaymentDetails Where ReferenceID = ?");
		sql.append(" Union All");
		sql.append(" Select PaymentID From PaymentDetails_Temp Where ReferenceID = ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return jdbcOperations.queryForObject(sql.toString(), Integer.class, excessID, excessID) > 0;
	}
}