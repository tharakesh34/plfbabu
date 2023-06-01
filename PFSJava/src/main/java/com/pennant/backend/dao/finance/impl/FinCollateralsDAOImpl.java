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
 * * FileName : FinCollateralsDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 12-11-2011 * *
 * Modified Date : 12-11-2011 * * Description : * *
 ******************************************************************************************** 
 * Date Author Version Comments *
 ******************************************************************************************** 
 * 12-11-2011 Pennant 0.1 * * * * * * * * *
 ******************************************************************************************** 
 */
package com.pennant.backend.dao.finance.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;

import com.pennant.backend.dao.finance.FinCollateralsDAO;
import com.pennant.backend.model.finance.FinCollaterals;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

/**
 * DAO methods implementation for the <b>FinCollaterals model</b> class.<br>
 */
public class FinCollateralsDAOImpl extends SequenceDao<FinCollaterals> implements FinCollateralsDAO {
	private static Logger logger = LogManager.getLogger(FinCollateralsDAOImpl.class);

	public FinCollateralsDAOImpl() {
		super();
	}

	@Override
	public FinCollaterals getFinCollateralsById(final long finID, final long id, String type) {
		StringBuilder sql = sqlSelectQuery(type);
		sql.append(" Where FinID = ? and CollateralSeq = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new FinCollateralsRowMapper(), finID, id);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	public void deleteByFinReference(long finID, String type) {
		StringBuilder sql = new StringBuilder("Delete From FinCollaterals");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> ps.setLong(1, finID));
	}

	@Override
	public long save(FinCollaterals fc, String type) {
		if (fc.getCollateralSeq() == Long.MIN_VALUE) {
			fc.setCollateralSeq(getNextValue("SeqCollateral"));
		}

		StringBuilder sql = new StringBuilder("Insert Into FinCollaterals");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (FinID, FinReference, CollateralSeq, CollateralType, CustID, Reference, Ccy");
		sql.append(", Value, Coverage, TenorType, Tenor, Rate, StartDate, MaturityDate");
		sql.append(", BankName, FirstChequeNo, LastChequeNo, Status, Remarks");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId");
		sql.append(", NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(" )");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, fc.getFinID());
			ps.setString(index++, fc.getFinReference());
			ps.setLong(index++, fc.getCollateralSeq());
			ps.setString(index++, fc.getCollateralType());
			ps.setLong(index++, fc.getCustID());
			ps.setString(index++, fc.getReference());
			ps.setString(index++, fc.getCcy());
			ps.setBigDecimal(index++, fc.getValue());
			ps.setBigDecimal(index++, fc.getCoverage());
			ps.setString(index++, fc.getTenorType());
			ps.setInt(index++, fc.getTenor());
			ps.setBigDecimal(index++, fc.getRate());
			ps.setDate(index++, JdbcUtil.getDate(fc.getStartDate()));
			ps.setDate(index++, JdbcUtil.getDate(fc.getMaturityDate()));
			ps.setString(index++, fc.getBankName());
			ps.setString(index++, fc.getFirstChequeNo());
			ps.setString(index++, fc.getLastChequeNo());
			ps.setString(index++, fc.getStatus());
			ps.setString(index++, fc.getRemarks());
			ps.setInt(index++, fc.getVersion());
			ps.setLong(index++, fc.getLastMntBy());
			ps.setTimestamp(index++, fc.getLastMntOn());
			ps.setString(index++, fc.getRecordStatus());
			ps.setString(index++, fc.getRoleCode());
			ps.setString(index++, fc.getNextRoleCode());
			ps.setString(index++, fc.getTaskId());
			ps.setString(index++, fc.getNextTaskId());
			ps.setString(index++, fc.getRecordType());
			ps.setLong(index, fc.getWorkflowId());
		});

		return fc.getId();
	}

	@Override
	public void update(FinCollaterals fc, String type) {
		StringBuilder sql = new StringBuilder("Update FinCollaterals");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set CollateralType = ?, CustID = ?, Reference = ?, Ccy = ?");
		sql.append(", Value = ?, Coverage = ?, TenorType = ?, Tenor = ?, Rate = ?");
		sql.append(", StartDate = ?, MaturityDate = ?, BankName = ?, FirstChequeNo = ?");
		sql.append(", LastChequeNo = ?, Status = ?, Remarks = ?");
		sql.append(", Version = ?, LastMntBy = ?, LastMntOn = ?, RecordStatus = ?");
		sql.append(", RoleCode = ?, NextRoleCode = ?, TaskId = ?");
		sql.append(", NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		sql.append(" Where FinID = ? and CollateralSeq = ?");

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, fc.getCollateralType());
			ps.setLong(index++, fc.getCustID());
			ps.setString(index++, fc.getReference());
			ps.setString(index++, fc.getCcy());
			ps.setBigDecimal(index++, fc.getValue());
			ps.setBigDecimal(index++, fc.getCoverage());
			ps.setString(index++, fc.getTenorType());
			ps.setInt(index++, fc.getTenor());
			ps.setBigDecimal(index++, fc.getRate());
			ps.setDate(index++, JdbcUtil.getDate(fc.getStartDate()));
			ps.setDate(index++, JdbcUtil.getDate(fc.getMaturityDate()));
			ps.setString(index++, fc.getBankName());
			ps.setString(index++, fc.getFirstChequeNo());
			ps.setString(index++, fc.getLastChequeNo());
			ps.setString(index++, fc.getStatus());
			ps.setString(index++, fc.getRemarks());
			ps.setInt(index++, fc.getVersion());
			ps.setLong(index++, fc.getLastMntBy());
			ps.setTimestamp(index++, fc.getLastMntOn());
			ps.setString(index++, fc.getRecordStatus());
			ps.setString(index++, fc.getRoleCode());
			ps.setString(index++, fc.getNextRoleCode());
			ps.setString(index++, fc.getTaskId());
			ps.setString(index++, fc.getNextTaskId());
			ps.setString(index++, fc.getRecordType());
			ps.setLong(index++, fc.getWorkflowId());
			ps.setLong(index++, fc.getFinID());
			ps.setLong(index, fc.getCollateralSeq());
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public List<FinCollaterals> getFinCollateralsByFinRef(long finID, String type) {
		StringBuilder sql = sqlSelectQuery(type);
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), new FinCollateralsRowMapper(), finID);
	}

	@Override
	public void delete(FinCollaterals fc, String type) {
		StringBuilder sql = new StringBuilder("Delete From FinCollaterals");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ? and CollateralSeq = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, fc.getFinID());
			ps.setLong(index, fc.getCollateralSeq());
		});
	}

	@Override
	public int getFinCollateralsByBank(String bankCode, String type) {
		StringBuilder sql = new StringBuilder("Select Count(FinID)");
		sql.append(" From FinCollaterals");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where BankName = ?");

		logger.debug(Literal.SQL + sql.toString());
		return this.jdbcOperations.queryForObject(sql.toString(), Integer.class, bankCode);
	}

	private StringBuilder sqlSelectQuery(String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinID, FinReference, CollateralSeq, CollateralType, CustID, Reference");
		sql.append(", Ccy, Value, Coverage, TenorType, Tenor, Rate, StartDate, MaturityDate");
		sql.append(", BankName, FirstChequeNo, LastChequeNo, Status, Remarks");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId");
		sql.append(", NextTaskId, RecordType, WorkflowId");
		sql.append(" From FinCollaterals");
		sql.append(StringUtils.trimToEmpty(type));

		return sql;
	}

	private class FinCollateralsRowMapper implements RowMapper<FinCollaterals> {

		private FinCollateralsRowMapper() {
			super();
		}

		@Override
		public FinCollaterals mapRow(ResultSet rs, int rowNum) throws SQLException {
			FinCollaterals fc = new FinCollaterals();

			fc.setFinID(rs.getLong("FinID"));
			fc.setFinReference(rs.getString("FinReference"));
			fc.setCollateralSeq(rs.getLong("CollateralSeq"));
			fc.setCollateralType(rs.getString("CollateralType"));
			fc.setCustID(rs.getLong("CustID"));
			fc.setReference(rs.getString("Reference"));
			fc.setCcy(rs.getString("Ccy"));
			fc.setValue(rs.getBigDecimal("Value"));
			fc.setCoverage(rs.getBigDecimal("Coverage"));
			fc.setTenorType(rs.getString("TenorType"));
			fc.setTenor(rs.getInt("Tenor"));
			fc.setRate(rs.getBigDecimal("Rate"));
			fc.setStartDate(rs.getDate("StartDate"));
			fc.setMaturityDate(rs.getDate("MaturityDate"));
			fc.setBankName(rs.getString("BankName"));
			fc.setFirstChequeNo(rs.getString("FirstChequeNo"));
			fc.setLastChequeNo(rs.getString("LastChequeNo"));
			fc.setStatus(rs.getString("Status"));
			fc.setRemarks(rs.getString("Remarks"));
			fc.setVersion(rs.getInt("Version"));
			fc.setLastMntBy(rs.getLong("LastMntBy"));
			fc.setLastMntOn(rs.getTimestamp("LastMntOn"));
			fc.setRecordStatus(rs.getString("RecordStatus"));
			fc.setRoleCode(rs.getString("RoleCode"));
			fc.setNextRoleCode(rs.getString("NextRoleCode"));
			fc.setTaskId(rs.getString("TaskId"));
			fc.setNextTaskId(rs.getString("NextTaskId"));
			fc.setRecordType(rs.getString("RecordType"));
			fc.setWorkflowId(rs.getLong("WorkflowId"));

			return fc;
		}
	}

}