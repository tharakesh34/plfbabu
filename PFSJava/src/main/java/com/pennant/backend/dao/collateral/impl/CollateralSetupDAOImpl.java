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
 * * FileName : CollateralSetupDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 13-12-2016 * *
 * Modified Date : 13-12-2016 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 13-12-2016 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.dao.collateral.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennant.backend.dao.collateral.CollateralSetupDAO;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.pff.extension.CustomerExtension;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

/**
 * DAO methods implementation for the <b>CollateralSetup model</b> class.<br>
 * 
 */

public class CollateralSetupDAOImpl extends BasicDao<CollateralSetup> implements CollateralSetupDAO {
	private static Logger logger = LogManager.getLogger(CollateralSetupDAOImpl.class);

	@Override
	public String save(CollateralSetup cs, String type) {
		StringBuilder sql = new StringBuilder();
		sql.append("Insert Into CollateralSetup");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (CollateralRef, FinReference, DepositorId, CollateralType, CollateralCcy");
		sql.append(", MaxCollateralValue, SpecialLTV, CollateralLoc, Valuator, ExpiryDate, ReviewFrequency");
		sql.append(", NextReviewDate, MultiLoanAssignment, Status, ThirdPartyAssignment, Remarks");
		sql.append(", CollateralValue, BankLTV, BankValuation, Version , LastMntBy, LastMntOn, RecordStatus");
		sql.append(", RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId, CreatedBy, CreatedOn");
		sql.append(", RegStatus, Modified, RegistrationDate, ModificationDate, SatisfactionDate)");
		sql.append(" Values(");
		sql.append(" ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ? , ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, cs.getCollateralRef());
			ps.setString(index++, cs.getFinReference());
			ps.setLong(index++, cs.getDepositorId());
			ps.setString(index++, cs.getCollateralType());
			ps.setString(index++, cs.getCollateralCcy());
			ps.setBigDecimal(index++, cs.getMaxCollateralValue());
			ps.setBigDecimal(index++, cs.getSpecialLTV());
			ps.setString(index++, cs.getCollateralLoc());
			ps.setString(index++, cs.getValuator());
			ps.setDate(index++, JdbcUtil.getDate(cs.getExpiryDate()));
			ps.setString(index++, cs.getReviewFrequency());
			ps.setDate(index++, JdbcUtil.getDate(cs.getNextReviewDate()));
			ps.setBoolean(index++, cs.isMultiLoanAssignment());
			ps.setString(index++, cs.getStatus());
			ps.setBoolean(index++, cs.isThirdPartyAssignment());
			ps.setString(index++, cs.getRemarks());
			ps.setBigDecimal(index++, cs.getCollateralValue());
			ps.setBigDecimal(index++, cs.getBankLTV());
			ps.setBigDecimal(index++, cs.getBankValuation());
			ps.setInt(index++, cs.getVersion());
			ps.setLong(index++, cs.getLastMntBy());
			ps.setTimestamp(index++, cs.getLastMntOn());
			ps.setString(index++, cs.getRecordStatus());
			ps.setString(index++, cs.getRoleCode());
			ps.setString(index++, cs.getNextRoleCode());
			ps.setString(index++, cs.getTaskId());
			ps.setString(index++, cs.getNextTaskId());
			ps.setString(index++, cs.getRecordType());
			ps.setLong(index++, cs.getWorkflowId());
			ps.setLong(index++, cs.getCreatedBy());
			ps.setTimestamp(index++, cs.getCreatedOn());
			ps.setString(index++, cs.getRegStatus());
			ps.setBoolean(index++, cs.isModified());
			ps.setDate(index++, JdbcUtil.getDate(cs.getRegistrationDate()));
			ps.setDate(index++, JdbcUtil.getDate(cs.getModificationDate()));
			ps.setDate(index, JdbcUtil.getDate(cs.getSatisfactionDate()));

		});

		return cs.getId();
	}

	@Override
	public void update(CollateralSetup cs, String type) {
		StringBuilder sql = new StringBuilder("Update CollateralSetup");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set DepositorId = ?, CollateralType = ?, CollateralCcy = ?");
		sql.append(", MaxCollateralValue = ?, SpecialLTV = ?, CollateralLoc = ?, Valuator = ?");
		sql.append(", ExpiryDate = ?, ReviewFrequency = ?, NextReviewDate = ?, MultiLoanAssignment = ?");
		sql.append(", ThirdPartyAssignment = ?, Remarks = ?, CollateralValue = ?, BankLTV = ?, BankValuation = ?");
		sql.append(", Version = ?, LastMntBy = ?, LastMntOn = ?, RecordStatus = ?, RoleCode = ?");
		sql.append(", NextRoleCode = ?, TaskId = ?, NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		sql.append(" Where CollateralRef = ?");

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, cs.getDepositorId());
			ps.setString(index++, cs.getCollateralType());
			ps.setString(index++, cs.getCollateralCcy());
			ps.setBigDecimal(index++, cs.getMaxCollateralValue());
			ps.setBigDecimal(index++, cs.getSpecialLTV());
			ps.setString(index++, cs.getCollateralLoc());
			ps.setString(index++, cs.getValuator());
			ps.setDate(index++, JdbcUtil.getDate(cs.getExpiryDate()));
			ps.setString(index++, cs.getReviewFrequency());
			ps.setDate(index++, JdbcUtil.getDate(cs.getNextReviewDate()));
			ps.setBoolean(index++, cs.isMultiLoanAssignment());
			ps.setBoolean(index++, cs.isThirdPartyAssignment());
			ps.setString(index++, cs.getRemarks());
			ps.setBigDecimal(index++, cs.getCollateralValue());
			ps.setBigDecimal(index++, cs.getBankLTV());
			ps.setBigDecimal(index++, cs.getBankValuation());
			ps.setInt(index++, cs.getVersion());
			ps.setLong(index++, cs.getLastMntBy());
			ps.setTimestamp(index++, cs.getLastMntOn());
			ps.setString(index++, cs.getRecordStatus());
			ps.setString(index++, cs.getRoleCode());
			ps.setString(index++, cs.getNextRoleCode());
			ps.setString(index++, cs.getTaskId());
			ps.setString(index++, cs.getNextTaskId());
			ps.setString(index++, cs.getRecordType());
			ps.setLong(index++, cs.getWorkflowId());

			ps.setString(index, cs.getCollateralRef());
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public boolean updateCollReferene(long oldReference, long newReference) {
		String sql = "UPDATE  SeqCollateralSetup  SET Seqno = ? Where Seqno = ?";

		logger.debug(Literal.SQL + sql);
		return this.jdbcOperations.update(sql, ps -> {
			ps.setLong(1, newReference);
			ps.setLong(2, oldReference);
		}) > 0;
	}

	@Override
	public void updateCollateralSetup(CollateralSetup cs, String type) {
		StringBuilder sql = new StringBuilder("Update CollateralSetup");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set CollateralValue = ?, BankValuation = ?");
		sql.append(" Where CollateralRef = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			ps.setBigDecimal(1, cs.getCollateralValue());
			ps.setBigDecimal(2, cs.getBankValuation());
			ps.setString(3, cs.getCollateralRef());
		});
	}

	@Override
	public CollateralSetup getCollateralSetupByRef(String collateralRef, String type) {
		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where CollateralRef = ? and Status is null");

		logger.debug(Literal.SQL + sql.toString());

		CollateralSetupRowMapper rowMapper = new CollateralSetupRowMapper(type);

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), rowMapper, collateralRef);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public CollateralSetup getCollateralSetup(String collateralRef, long depositorId, String type) {
		StringBuilder selectSql = getSqlQuery(type);
		selectSql.append(" Where CollateralRef = ? and DepositorId = ? and Status is null");

		logger.debug(Literal.SQL + selectSql.toString());

		CollateralSetupRowMapper rowMapper = new CollateralSetupRowMapper(type);

		try {
			return this.jdbcOperations.queryForObject(selectSql.toString(), rowMapper, collateralRef, depositorId);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public List<CollateralSetup> getApprovedCollateralByCustId(long depositorId, String type) {
		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where DepositorId = ? and Status is null");

		logger.trace(Literal.SQL + sql);

		CollateralSetupRowMapper rowMapper = new CollateralSetupRowMapper(type);

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index, depositorId);
		}, rowMapper);

	}

	@Override
	public List<CollateralSetup> getCollateralSetupByFinRef(String finReference, String type) {
		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where FinReference = ? and Status is null");

		logger.debug(Literal.SQL + sql.toString());

		CollateralSetupRowMapper rowMapper = new CollateralSetupRowMapper(type);

		return this.jdbcOperations.query(sql.toString(), rowMapper, finReference);
	}

	@Override
	public List<CollateralSetup> getCollateralByRef(String reference, long depositorId, String type) {
		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where DepositorId = ?");
		sql.append(" and CollateralRef in (");
		sql.append(" Select CollateralRef from CollateralAssignment_Temp where Reference = ?)");
		/* FIXME : change to FinID */

		logger.debug(Literal.SQL + sql.toString());

		CollateralSetupRowMapper rowMapper = new CollateralSetupRowMapper(type);

		return this.jdbcOperations.query(sql.toString(), rowMapper, depositorId, reference);
	}

	@Override
	public void delete(CollateralSetup collateralSetup, String type) {
		StringBuilder sql = new StringBuilder("Delete From CollateralSetup");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where CollateralRef = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			if (this.jdbcOperations.update(sql.toString(),
					ps -> ps.setString(1, collateralSetup.getCollateralRef())) <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	@Override
	public int getVersion(String collateralRef, String tableType) {
		StringBuilder sql = new StringBuilder();
		sql.append("Select Version From CollateralSetup");
		sql.append(tableType);
		sql.append(" Where CollateralRef = ? and Status is null");

		logger.debug(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CollateralRef", collateralRef);

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), Integer.class, collateralRef);
		} catch (EmptyResultDataAccessException dae) {
			logger.warn(Message.NO_RECORD_FOUND);
			return 0;
		}
	}

	@Override
	public boolean isCollateralInMaintenance(String collatrlRef, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" count(CollateralRef)");
		sql.append(" From CollateralSetup");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where CollateralRef = ? and Status is null and FinReference is null");

		logger.debug(Literal.SQL + sql.toString());
		return this.jdbcOperations.queryForObject(sql.toString(), Integer.class, collatrlRef) > 0;
	}

	@Override
	public int getCollateralCountByref(String collateralRef, String tableType) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" count(CollateralRef)");
		sql.append(" From CollateralSetup");
		sql.append(StringUtils.trimToEmpty(tableType));
		sql.append(" Where CollateralRef = ? and Status is null");

		logger.debug(Literal.SQL + sql.toString());
		return this.jdbcOperations.queryForObject(sql.toString(), Integer.class, collateralRef);
	}

	@Override
	public int getCountByCollateralRef(String collateralRef) {
		return getCollateralCountByref(collateralRef, "");
	}

	@Override
	public boolean isCollReferenceExists(String generatedSeqNo, String type) {
		return getCollateralCountByref(generatedSeqNo, type) > 0;
	}

	@Override
	public Long getCustomerIdByCollateral(String collateralRef) {
		StringBuilder sql = new StringBuilder("Select distinct Depositorid");
		sql.append(" From (Select Depositorid, CollateralRef From Collateralsetup_temp");
		sql.append(" Union All");
		sql.append(" Select Depositorid, CollateralRef From Collateralsetup");
		sql.append(") T where CollateralRef = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> rs.getLong("Depositorid"),
				collateralRef);
	}

	private StringBuilder getSqlQuery(String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" CollateralRef, DepositorId, CollateralType, CollateralCcy, MaxCollateralValue");
		sql.append(", SpecialLTV, CollateralLoc, Valuator, ExpiryDate, ReviewFrequency, NextReviewDate");
		sql.append(", MultiLoanAssignment, ThirdPartyAssignment, Remarks, CollateralValue, BankLTV");
		sql.append(", BankValuation, Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId, CreatedBy, CreatedOn");
		sql.append(", RegStatus, registrationDate, modificationDate, satisfactionDate");
		if (StringUtils.containsIgnoreCase(type, "View")) {
			sql.append(", CollateralType, DepositorCif, DepositorName, CollateralTypeName, AssetId, SiId");
		}

		sql.append(" From CollateralSetup");
		sql.append(StringUtils.trimToEmpty(type));

		return sql;
	}

	private class CollateralSetupRowMapper implements RowMapper<CollateralSetup> {
		private String type;

		private CollateralSetupRowMapper(String type) {
			this.type = type;
		}

		@Override
		public CollateralSetup mapRow(ResultSet rs, int rowNum) throws SQLException {
			CollateralSetup cs = new CollateralSetup();

			cs.setCollateralRef(rs.getString("CollateralRef"));
			cs.setDepositorId(rs.getLong("DepositorId"));
			cs.setCollateralType(rs.getString("CollateralType"));
			cs.setCollateralCcy(rs.getString("CollateralCcy"));
			cs.setMaxCollateralValue(rs.getBigDecimal("MaxCollateralValue"));
			cs.setSpecialLTV(rs.getBigDecimal("SpecialLTV"));
			cs.setCollateralLoc(rs.getString("CollateralLoc"));
			cs.setValuator(rs.getString("Valuator"));
			cs.setExpiryDate(rs.getTimestamp("ExpiryDate"));
			cs.setReviewFrequency(rs.getString("ReviewFrequency"));
			cs.setNextReviewDate(rs.getTimestamp("NextReviewDate"));
			cs.setMultiLoanAssignment(rs.getBoolean("MultiLoanAssignment"));
			cs.setThirdPartyAssignment(rs.getBoolean("ThirdPartyAssignment"));
			cs.setRemarks(rs.getString("Remarks"));
			cs.setCollateralValue(rs.getBigDecimal("CollateralValue"));
			cs.setBankLTV(rs.getBigDecimal("BankLTV"));
			cs.setBankValuation(rs.getBigDecimal("BankValuation"));
			cs.setVersion(rs.getInt("Version"));
			cs.setLastMntOn(rs.getTimestamp("LastMntOn"));
			cs.setLastMntBy(rs.getLong("LastMntBy"));
			cs.setRecordStatus(rs.getString("RecordStatus"));
			cs.setRoleCode(rs.getString("RoleCode"));
			cs.setNextRoleCode(rs.getString("NextRoleCode"));
			cs.setTaskId(rs.getString("TaskId"));
			cs.setNextTaskId(rs.getString("NextTaskId"));
			cs.setRecordType(rs.getString("RecordType"));
			cs.setWorkflowId(rs.getLong("WorkflowId"));
			cs.setCreatedBy(rs.getLong("CreatedBy"));
			cs.setCreatedOn(rs.getTimestamp("CreatedOn"));
			cs.setRegStatus(rs.getString("RegStatus"));
			cs.setRegistrationDate(JdbcUtil.getDate(rs.getDate("registrationDate")));
			cs.setModificationDate(JdbcUtil.getDate(rs.getDate("modificationDate")));
			cs.setSatisfactionDate(JdbcUtil.getDate(rs.getDate("satisfactionDate")));

			if (StringUtils.containsIgnoreCase(type, "View")) {
				cs.setCollateralType(rs.getString("CollateralType"));
				cs.setDepositorCif(rs.getString("DepositorCif"));
				cs.setDepositorName(rs.getString("DepositorName"));
				cs.setCollateralTypeName(rs.getString("CollateralTypeName"));
			}

			return cs;
		}
	}

	@Override
	public List<CollateralSetup> getCollateralSetupByCustomer(long custID) {
		StringBuilder sql = getSqlQuery();
		if (CustomerExtension.CUST_CORE_BANK_ID) {

			sql.append(" Where DepositorId in");
			sql.append(" ( Select distinct CustID From (");
			sql.append(
					" Select CustID from Customers Where CustCoreBank = (Select CustCoreBank From Customers Where CustID = "
							+ custID + ")");
			sql.append(" Union All");
			sql.append(
					" Select CustID from Customers_Temp Where CustCoreBank = (Select CustCoreBank From Customers_Temp Where CustID = "
							+ custID + ")");
			sql.append(") T)");

			logger.debug(Literal.SQL + sql.toString());

			return jdbcOperations.query(sql.toString(), new CollateralSetupRM());

		} else {
			sql.append(" Where DepositorId = ?");

			logger.debug(Literal.SQL + sql.toString());

			return jdbcOperations.query(sql.toString(), new CollateralSetupRM(), custID);
		}

	}

	@Override
	public List<CollateralSetup> getCollateralSetupByReference(long custID) {
		StringBuilder sql = getSqlQuery();

		if (CustomerExtension.CUST_CORE_BANK_ID) {

			sql.append(" Where CollateralRef in (Select CollateralRef From CollateralThirdParty Where CustomerId in ");
			sql.append(" ( Select distinct CustID From (");
			sql.append(
					" Select CustID from Customers Where CustCoreBank = (Select CustCoreBank From Customers Where CustID = "
							+ custID + ")");
			sql.append(" Union All");
			sql.append(
					" Select CustID from Customers_Temp Where CustCoreBank = (Select CustCoreBank From Customers_Temp Where CustID = "
							+ custID + ")");
			sql.append(") T))");

			logger.debug(Literal.SQL + sql.toString());

			return jdbcOperations.query(sql.toString(), new CollateralSetupRM());

		} else {
			sql.append(" Where CollateralRef in (Select CollateralRef From CollateralThirdParty Where CustomerId = ?)");

			logger.debug(Literal.SQL + sql.toString());

			return jdbcOperations.query(sql.toString(), new CollateralSetupRM(), custID);
		}

	}

	@Override
	public boolean isNotAssigned(String collateralRef) {
		StringBuilder sql = new StringBuilder("");
		sql.append("Select CollateralRef From CollateralAssignment Where CollateralRef = ?");
		sql.append(" Union All");
		sql.append(" Select CollateralRef From CollateralAssignment_Temp Where CollateralRef = ?");

		logger.debug(Literal.SQL + sql.toString());

		List<String> references = jdbcOperations.query(sql.toString(), (rs, rowNum) -> rs.getString(1), collateralRef,
				collateralRef);

		if (references.isEmpty()) {
			return true;
		}

		return false;
	}

	private StringBuilder getSqlQuery() {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" cs.CollateralRef, cs.DepositorId, c.CustCIF, cs.CollateralType, cs.CollateralCcy");
		sql.append(", cs.CollateralValue , cs.BankValuation, cs.MultiLoanAssignment, cs.ExpiryDate");
		sql.append(" From CollateralSetup cs");
		sql.append(" Inner join Customers c on c.CustID = cs.DepositorId");

		return sql;
	}

	private class CollateralSetupRM implements RowMapper<CollateralSetup> {

		@Override
		public CollateralSetup mapRow(ResultSet rs, int rowNum) throws SQLException {
			CollateralSetup cs = new CollateralSetup();

			cs.setCollateralRef(rs.getString("CollateralRef"));
			cs.setDepositorCif(rs.getString("CustCIF"));
			cs.setCollateralType(rs.getString("CollateralType"));
			cs.setCollateralCcy(rs.getString("CollateralCcy"));
			cs.setCollateralValue(rs.getBigDecimal("CollateralValue"));
			cs.setBankValuation(rs.getBigDecimal("BankValuation"));
			cs.setMultiLoanAssignment(rs.getBoolean("MultiLoanAssignment"));
			cs.setExpiryDate(JdbcUtil.getDate(rs.getDate("ExpiryDate")));

			return cs;
		}
	}

	@Override
	public void updateSetupDetail(String collateralref, boolean modified) {
		logger.debug(Literal.ENTERING);
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		StringBuilder sql = new StringBuilder();

		sql.append(" Update CollateralSetup ");
		sql.append(" Set Modified = :Modified");
		sql.append(" Where CollateralRef=:CollateralRef ");

		paramMap.addValue("Modified", modified);
		paramMap.addValue("CollateralRef", collateralref);

		this.jdbcTemplate.update(sql.toString(), paramMap);

		logger.debug(Literal.LEAVING);
	}

	@Override
	public Date getRegistrationDate(String collateralRef) {
		logger.debug(Literal.ENTERING);
		StringBuilder sql = new StringBuilder();

		sql.append(" select REGISTRATIONDATE From CollateralSetup ");
		sql.append(" Where CollateralRef= ? ");

		logger.debug(Literal.SQL + sql.toString());
		try {
			return this.jdbcOperations.queryForObject(sql.toString(), Date.class, collateralRef);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

	@Override
	public Date getExtendedFieldMap(String reference, String tableName, int seqNo) {

		StringBuilder sql = new StringBuilder();

		sql.append("select REVSECRTCRTNDATE from ");
		sql.append(tableName);
		sql.append(" where reference = :reference and seqno = :seqNo");

		logger.debug(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("reference", reference);
		source.addValue("seqNo", seqNo);
		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), source, Date.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
		} catch (DataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		return null;
	}

	@Override
	public void saveCollateralRevisedDate(String collateralRef, Date revDate) {
		logger.debug("Entering");

		StringBuilder sql = new StringBuilder("Insert Into CollateralRevisedHistory");
		sql.append(" (CollateralRef, RevisedDate )");
		sql.append(" Values(?, ?) ");

		logger.debug(Literal.SQL + sql.toString());

		try {
			jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setString(index++, collateralRef);
				ps.setDate(index, JdbcUtil.getDate(revDate));

			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug("Leaving");

	}
}