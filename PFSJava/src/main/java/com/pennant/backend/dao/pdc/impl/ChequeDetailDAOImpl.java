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
 * * FileName : ChequeDetailDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 27-11-2017 * * Modified
 * Date : 27-11-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 27-11-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.pdc.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.pdc.ChequeDetailDAO;
import com.pennant.backend.model.finance.ChequeDetail;
import com.pennant.backend.model.mandate.Mandate;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;
import com.pennanttech.pff.presentment.model.PresentmentDetail;

/**
 * Data access layer implementation for <code>ChequeDetail</code> with set of CRUD operations.
 */
public class ChequeDetailDAOImpl extends SequenceDao<Mandate> implements ChequeDetailDAO {
	private static Logger logger = LogManager.getLogger(ChequeDetailDAOImpl.class);

	public ChequeDetailDAOImpl() {
		super();
	}

	@Override
	public ChequeDetail getChequeDetail(long chequeDetailsID, String type) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(" chequeDetailsID, headerID, bankBranchID, accountNo, chequeSerialNo, chequeDate, ");
		sql.append(" eMIRefNo, amount, chequeCcy, status, active, documentName, documentRef, chequeType, ");
		sql.append(" chequeStatus, accountType, accHolderName, ");

		sql.append(
				" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From CHEQUEDETAIL");
		sql.append(type);
		sql.append(" Where chequeDetailsID = :chequeDetailsID");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		ChequeDetail chequeDetail = new ChequeDetail();
		chequeDetail.setChequeDetailsID(chequeDetailsID);

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(chequeDetail);
		RowMapper<ChequeDetail> rowMapper = BeanPropertyRowMapper.newInstance(ChequeDetail.class);

		try {
			return jdbcTemplate.queryForObject(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public List<ChequeDetail> getChequeDetailList(long headerID, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ChequeDetailsID, HeaderID, BankBranchID, AccountNo, ChequeSerialNo, ChequeDate");
		sql.append(", EMIRefNo, Amount, ChequeCcy, Status, Active, DocumentName, DocumentRef, ChequeType");
		sql.append(", ChequeStatus, AccountType, AccHolderName");
		if (type.equals("_View")) {
			sql.append(", BankCode, BranchCode, BranchDesc, Micr, Ifsc, City, BankName");
		}
		sql.append(", Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From CHEQUEDETAIL");
		sql.append(type);
		sql.append(" Where HeaderID = ?");

		logger.debug(Literal.SQL + sql.toString());

		List<ChequeDetail> list = jdbcOperations.query(sql.toString(), ps -> {
			ps.setLong(1, headerID);
		}, (rs, i) -> {
			ChequeDetail cd = new ChequeDetail();
			cd.setChequeDetailsID(rs.getLong("ChequeDetailsID"));
			cd.setHeaderID(rs.getLong("HeaderID"));
			cd.setBankBranchID(rs.getLong("BankBranchID"));
			cd.setAccountNo(rs.getString("AccountNo"));
			cd.setChequeSerialNo(rs.getInt("ChequeSerialNo"));
			cd.setChequeDate(rs.getDate("ChequeDate"));
			cd.seteMIRefNo(rs.getInt("EMIRefNo"));
			cd.setAmount(rs.getBigDecimal("Amount"));
			cd.setChequeCcy(rs.getString("ChequeCcy"));
			cd.setStatus(rs.getString("Status"));
			cd.setActive(rs.getBoolean("Active"));
			cd.setDocumentName(rs.getString("DocumentName"));
			cd.setDocumentRef(rs.getLong("DocumentRef"));
			cd.setChequeType(rs.getString("ChequeType"));
			cd.setChequeStatus(rs.getString("ChequeStatus"));
			cd.setAccountType(rs.getString("AccountType"));
			cd.setAccHolderName(rs.getString("AccHolderName"));

			if (type.equals("_View")) {
				cd.setBankCode(rs.getString("BankCode"));
				cd.setBranchCode(rs.getString("BranchCode"));
				cd.setBranchDesc(rs.getString("BranchDesc"));
				cd.setMicr(rs.getString("Micr"));
				cd.setIfsc(rs.getString("Ifsc"));
				cd.setCity(rs.getString("City"));
				cd.setBankName(rs.getString("BankName"));
			}

			cd.setVersion(rs.getInt("Version"));
			cd.setLastMntOn(rs.getTimestamp("LastMntOn"));
			cd.setLastMntBy(rs.getLong("LastMntBy"));
			cd.setRecordStatus(rs.getString("RecordStatus"));
			cd.setRoleCode(rs.getString("RoleCode"));
			cd.setNextRoleCode(rs.getString("NextRoleCode"));
			cd.setTaskId(rs.getString("TaskId"));
			cd.setNextTaskId(rs.getString("NextTaskId"));
			cd.setRecordType(rs.getString("RecordType"));
			cd.setWorkflowId(rs.getLong("WorkflowId"));

			return cd;
		});

		return list.stream().sorted((l1, l2) -> Long.compare(l1.getChequeDetailsID(), l2.getChequeDetailsID()))
				.collect(Collectors.toList());
	}

	@Override
	public boolean isDuplicateKey(long chequeDetailsID, long bankBranchID, String accountNo, int chequeSerialNo,
			TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		String sql;
		String whereClause = "bankBranchID = :bankBranchID AND accountNo = :accountNo AND chequeSerialNo = :chequeSerialNo AND chequeDetailsID != :chequeDetailsID";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("CHEQUEDETAIL", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("CHEQUEDETAIL_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "CHEQUEDETAIL_Temp", "CHEQUEDETAIL" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("chequeDetailsID", chequeDetailsID);
		paramSource.addValue("bankBranchID", bankBranchID);
		paramSource.addValue("accountNo", accountNo);
		paramSource.addValue("chequeSerialNo", String.valueOf(chequeSerialNo));

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			if (isActiveChequeDetails(chequeDetailsID, bankBranchID, accountNo, chequeSerialNo)) {
				exists = true;
			}
			if (isNonActiveChequeDetails(chequeDetailsID, bankBranchID, accountNo, chequeSerialNo)) {
				exists = true;
			}
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

	@Override
	public String save(ChequeDetail chequeDetail, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" insert into CHEQUEDETAIL");
		sql.append(tableType.getSuffix());
		sql.append("(chequeDetailsID, headerID, bankBranchID, accountNo, chequeSerialNo, chequeDate, ");
		sql.append(" eMIRefNo, amount, chequeCcy, status, active, documentName, documentRef, chequeType, ");
		sql.append(" chequeStatus, accountType, accHolderName, ");
		sql.append(
				" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values(");
		sql.append(" :chequeDetailsID, :headerID, :bankBranchID, :accountNo, :chequeSerialNo, :chequeDate, ");
		sql.append(" :eMIRefNo, :amount, :chequeCcy, 'NEW', :active, :documentName, :documentRef, :chequeType, ");
		sql.append(" :chequeStatus, :accountType, :accHolderName, ");
		sql.append(
				" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");
		if (chequeDetail.getId() == Long.MIN_VALUE || chequeDetail.getId() == 0) {
			chequeDetail.setId(getNextValue("SeqChequeDetail"));
			logger.debug("get NextID:" + chequeDetail.getId());
		}
		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(chequeDetail);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return String.valueOf(chequeDetail.getChequeDetailsID());
	}

	@Override
	public void update(ChequeDetail chequeDetail, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("update CHEQUEDETAIL");
		sql.append(tableType.getSuffix());
		sql.append("  set headerID = :headerID, bankBranchID = :bankBranchID, accountNo = :accountNo, ");
		sql.append(" chequeSerialNo = :chequeSerialNo, chequeDate = :chequeDate, eMIRefNo = :eMIRefNo, ");
		sql.append(" amount = :amount, chequeCcy = :chequeCcy, status = :status, ");
		sql.append(
				" active = :active, documentName = :documentName, documentRef = :documentRef, chequeType =:chequeType, ");
		sql.append(" chequeStatus = :chequeStatus, accountType = :accountType, accHolderName = :accHolderName, ");
		sql.append(" LastMntOn = :LastMntOn, RecordStatus = :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where chequeDetailsID = :chequeDetailsID ");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(chequeDetail);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(ChequeDetail chequeDetail, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from CHEQUEDETAIL");
		sql.append(tableType.getSuffix());
		sql.append(" where chequeDetailsID = :chequeDetailsID ");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(chequeDetail);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		// Check for the concurrency not required as the work-flow will be driven either by finance main (LOS)
		// or cheque header (LMS).
		logger.debug(Literal.LEAVING);
	}

	public void deleteById(long headerID, String tableType) {
		logger.debug(Literal.ENTERING);
		ChequeDetail chequeDetail = new ChequeDetail();
		chequeDetail.setHeaderID(headerID);
		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from CHEQUEDETAIL");
		sql.append(StringUtils.trimToEmpty(tableType));
		sql.append(" where headerID = :headerID ");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(chequeDetail);
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

	/**
	 * Method for Deletion of Cheque Header Related List of chequeDetail for the Cheque Header
	 */
	public void deleteByCheqID(final long headerID, String type) {
		logger.debug("Entering");
		ChequeDetail chequeDetail = new ChequeDetail();
		chequeDetail.setHeaderID(headerID);

		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append("Delete From CHEQUEDETAIL");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where headerID =:headerID ");

		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(chequeDetail);
		this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	@Override
	public int updateChequeStatus(List<PresentmentDetail> presentments) {
		StringBuilder sql = new StringBuilder();
		sql.append("Update ChequeDetail Set Chequestatus = ?");
		sql.append(" where ChequeDetailsId = ?");
		logger.trace(Literal.SQL + sql.toString());

		return jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int index) throws SQLException {
				PresentmentDetail pd = presentments.get(index);
				ps.setString(1, PennantConstants.CHEQUESTATUS_PRESENT);
				ps.setLong(2, pd.getMandateId());
			}

			@Override
			public int getBatchSize() {
				return presentments.size();
			}
		}).length;
	}

	@Override
	public void updateChequeStatus(long chequeDetailsId, String chequestatus) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("update CHEQUEDETAIL Set Chequestatus = :Chequestatus  where ChequeDetailsId = :ChequeDetailsId ");
		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Chequestatus", chequestatus);
		source.addValue("ChequeDetailsId", chequeDetailsId);
		jdbcTemplate.update(sql.toString(), source);

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void batchUpdateChequeStatus(List<Long> chequeDetailsId, String chequestatus) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Update");
		sql.append(" CHEQUEDETAIL");
		sql.append(" Set Chequestatus = ?");
		sql.append(" where ChequeDetailsId = ?");

		logger.trace(Literal.SQL + sql.toString());

		jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				int index = 1;
				long id = chequeDetailsId.get(i);

				ps.setString(index++, chequestatus);

				ps.setLong(index, id);
			}

			@Override
			public int getBatchSize() {
				return chequeDetailsId.size();
			}
		});

		logger.debug(Literal.LEAVING);
	}

	@Override
	public boolean isChequeExists(long headerID, Date chequeDate) {
		String sql = "Select count(ChequeDetailsID) From ChequeDetail_View Where HeaderID = ? and ChequeDate = ?";

		logger.debug(Literal.SQL + sql);

		return jdbcOperations.queryForObject(sql, Integer.class, headerID, JdbcUtil.getDate(chequeDate)) > 0;
	}

	private boolean isActiveChequeDetails(long chequeDetailsID, long bankBranchID, String accountNo,
			int chequeSerialNo) {

		StringBuilder sql = new StringBuilder();
		sql.append("Select Count(*) from CHEQUEHEADER ch");
		sql.append(" Inner join CHEQUEDETAIL cd on cd.HeaderID = ch.HeaderID");
		sql.append(" Inner join FinanceMain fm on fm.FinReference = ch.FinReference");
		sql.append(" Where cd.BankBranchID = ? and cd.AccountNo = ? and cd.ChequeSerialNo = ?");
		sql.append(" and cd.ChequeDetailsID != ?");

		try {
			return jdbcOperations.queryForObject(sql.toString(), Integer.class, bankBranchID, accountNo,
					String.valueOf(chequeSerialNo), chequeDetailsID) > 0;
		} catch (Exception e) {
			//
		}

		return false;
	}

	private boolean isNonActiveChequeDetails(long chequeDetailsID, long bankBranchID, String accountNo,
			int chequeSerialNo) {

		StringBuilder sql = new StringBuilder();
		sql.append("Select Count(*) from CHEQUEHEADER_TEMP ch");
		sql.append(" Inner join CHEQUEDETAIL_TEMP cd on cd.HeaderID = ch.HeaderID");
		sql.append(" Inner join FinanceMain_TEMP fm on fm.FinReference = ch.FinReference");
		sql.append(" Where cd.BankBranchID = ? and cd.AccountNo = ? and cd.ChequeSerialNo = ?");
		sql.append(" and cd.ChequeDetailsID != ?");

		try {
			return jdbcOperations.queryForObject(sql.toString(), Integer.class, bankBranchID, accountNo,
					String.valueOf(chequeSerialNo), chequeDetailsID) > 0;
		} catch (Exception e) {
			//
		}

		return false;
	}
}
