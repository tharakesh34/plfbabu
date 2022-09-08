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
 * * FileName : BankDetailDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 05-05-2011 * * Modified
 * Date : 05-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 05-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.applicationmaster.impl;

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

import com.pennant.backend.dao.applicationmaster.BankDetailDAO;
import com.pennant.backend.model.applicationmaster.BankDetail;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.App.Database;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * DAO methods implementation for the <b>BankDetail model</b> class.<br>
 * 
 */
public class BankDetailDAOImpl extends BasicDao<BankDetail> implements BankDetailDAO {
	private static Logger logger = LogManager.getLogger(BankDetailDAOImpl.class);

	public BankDetailDAOImpl() {
		super();
	}

	@Override
	public BankDetail getBankDetailByIfsc(String ifsc) {
		BankDetail bankDetail = new BankDetail();
		bankDetail.setIfsc(ifsc);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" select branch.branchdesc bankBranch,bank.bankname bankName from BankBranches branch");
		selectSql.append(" left join BMTBankDetail bank on");
		selectSql.append(" branch.bankcode=bank.bankcode where ifsc=:ifsc");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(bankDetail);
		RowMapper<BankDetail> typeRowMapper = BeanPropertyRowMapper.newInstance(BankDetail.class);

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public BankDetail getBankDetailById(final String id, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" BankCode, BankName, BankShortCode, Active, AccNoLength, MinAccNoLength");
		sql.append(", AllowMultipleIFSC, Nach, Dd, Dda, Ecs, Cheque, Emandate, AllowedSources");
		sql.append(", UpdateBranches, Version, LastMntOn, LastMntBy, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" from BMTBankDetail");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where BankCode = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				BankDetail bd = new BankDetail();

				bd.setBankCode(rs.getString("BankCode"));
				bd.setBankName(rs.getString("BankName"));
				bd.setBankShortCode(rs.getString("BankShortCode"));
				bd.setActive(rs.getBoolean("Active"));
				bd.setAccNoLength(rs.getInt("AccNoLength"));
				bd.setMinAccNoLength(rs.getInt("MinAccNoLength"));
				bd.setAllowMultipleIFSC(rs.getBoolean("AllowMultipleIFSC"));
				bd.setNach(rs.getBoolean("Nach"));
				bd.setDd(rs.getBoolean("Dd"));
				bd.setDda(rs.getBoolean("Dda"));
				bd.setEcs(rs.getBoolean("Ecs"));
				bd.setCheque(rs.getBoolean("Cheque"));
				bd.setEmandate(rs.getBoolean("Emandate"));
				bd.setAllowedSources(rs.getString("AllowedSources"));
				bd.setUpdateBranches(rs.getBoolean("UpdateBranches"));
				bd.setVersion(rs.getInt("Version"));
				bd.setLastMntOn(rs.getTimestamp("LastMntOn"));
				bd.setLastMntBy(rs.getLong("LastMntBy"));
				bd.setRecordStatus(rs.getString("RecordStatus"));
				bd.setRoleCode(rs.getString("RoleCode"));
				bd.setNextRoleCode(rs.getString("NextRoleCode"));
				bd.setTaskId(rs.getString("TaskId"));
				bd.setNextTaskId(rs.getString("NextTaskId"));
				bd.setRecordType(rs.getString("RecordType"));
				bd.setWorkflowId(rs.getLong("WorkflowId"));

				return bd;
			}, id);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public boolean isDuplicateKey(String bankCode, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		String sql;
		String whereClause = "BankCode = :bankCode";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("BMTBankDetail", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("BMTBankDetail_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "BMTBankDetail_Temp", "BMTBankDetail" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("bankCode", bankCode);

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

	@Override
	public String save(BankDetail bankDetail, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("insert into BMTBankDetail");
		sql.append(tableType.getSuffix());
		sql.append(" (BankCode, BankName, BankShortCode, Active,  AccNoLength, MinAccNoLength");
		sql.append(", AllowMultipleIFSC, Nach, Dd, Dda, Ecs, Cheque, Emandate, AllowedSources");
		sql.append(", UpdateBranches, Version, LastMntBy, LastMntOn, RecordStatus");
		sql.append(", RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values(:BankCode, :BankName, :BankShortCode, :Active, :AccNoLength, :MinAccNoLength");
		sql.append(", :AllowMultipleIFSC, :Nach, :Dd, :Dda, :Ecs, :Cheque, :Emandate, :AllowedSources");
		sql.append(",:UpdateBranches, :Version, :LastMntBy, :LastMntOn, :RecordStatus");
		sql.append(", :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(bankDetail);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return bankDetail.getId();
	}

	@Override
	public void update(BankDetail bankDetail, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL, ensure primary key will not be updated.
		StringBuilder sql = new StringBuilder("update BMTBankDetail");
		sql.append(tableType.getSuffix());
		sql.append(" Set BankName = :BankName, BankShortCode = :BankShortCode, Active = :Active");
		sql.append(
				", AccNoLength = :AccNoLength, MinAccNoLength = :MinAccNoLength, AllowMultipleIFSC = :AllowMultipleIFSC");
		sql.append(", Nach = :Nach, Dd = :Dd, Dda = :Dda, Ecs = :Ecs, Cheque = :Cheque, Emandate = :Emandate");
		sql.append(", AllowedSources = :AllowedSources, UpdateBranches = :UpdateBranches, Version = :Version");
		sql.append(", LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus");
		sql.append(", RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId");
		sql.append(", NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append(" where BankCode = :BankCode ");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(bankDetail);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(BankDetail bankDetail, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from BMTBankDetail");
		sql.append(tableType.getSuffix());
		sql.append(" Where BankCode =:BankCode");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(bankDetail);
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
	public BankDetail getAccNoLengthByCode(String bankCode, String type) {
		logger.debug("Entering");

		BankDetail bankDetail = new BankDetail();
		bankDetail.setBankCode(bankCode);

		StringBuilder selectSql = new StringBuilder("Select AccNoLength, MinAccNoLength");

		selectSql.append(" From BMTBankDetail");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where BankCode =:BankCode");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(bankDetail);
		RowMapper<BankDetail> typeRowMapper = BeanPropertyRowMapper.newInstance(BankDetail.class);

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException dae) {
			logger.warn(Message.NO_RECORD_FOUND);
			return bankDetail;
		}
	}

	@Override
	public String getBankCodeByName(String bankName) {
		logger.debug("Entering");

		BankDetail bankDetail = new BankDetail();
		bankDetail.setBankName(bankName);

		StringBuilder selectSql = new StringBuilder("Select BankCode");

		selectSql.append(" From BMTBankDetail");
		selectSql.append(" Where bankName =:bankName");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(bankDetail);

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, String.class);
		} catch (EmptyResultDataAccessException dae) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	/**
	 * Ticket id:124998 return boolean value based on bankcode and active status exists in table .
	 * 
	 * @param bankCode
	 * @param type
	 * @param active
	 */
	@Override
	public boolean isBankCodeExits(String bankCode, String type, boolean active) {
		logger.debug("Entering");

		BankDetail bankDetail = new BankDetail();
		bankDetail.setBankCode(bankCode);
		bankDetail.setActive(active);

		StringBuilder selectSql = new StringBuilder("Select count(*) ");

		selectSql.append(" From BMTBankDetail");
		selectSql.append(type);
		selectSql.append(" Where BankCode =:BankCode and Active = :Active");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(bankDetail);

		return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class) > 0;
	}

	@Override
	public boolean isBankCodeExits(String bankCode) {
		String sql = "Select BankCode From BMTBankDetail Where BankCode = ? and Active = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return jdbcOperations.queryForObject(sql, String.class, bankCode, 1) != null;
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return false;
		}
	}

	@Override
	public BankDetail getAccNoLengths(String bankCode) {
		String length = "length";
		if (App.DATABASE == Database.SQL_SERVER) {
			length = "len";
		}

		StringBuilder sql = new StringBuilder("Select min(");
		sql.append(length);
		sql.append("(AccNo)) MinAccNoLength, max(");
		sql.append(length);
		sql.append("(AccNo)) MaxAccNoLength from (");
		sql.append(" select BeneficiaryAccno AccNo, BankBranchId From FinAdvancePayments");
		sql.append(" Union all");
		sql.append(" Select BeneficiaryAccno AccNo, BankBranchId From FinAdvancePayments_Temp");
		sql.append(" union all");
		sql.append(" Select AccountNo  AccNo, BankBranchId From PaymentInstructions");
		sql.append(" union all");
		sql.append(" Select AccountNo  AccNo, BankBranchId From PaymentInstructions_Temp");
		sql.append(" union all");
		sql.append(" Select AccNumber  AccNo, BankBranchId From Mandates");
		sql.append(" union all");
		sql.append(" Select AccNumber  AccNo, BankBranchId From Mandates_Temp");
		sql.append(" union all");
		sql.append(" Select AccountNo  AccNo, BankBranchId From ChequeDetail");
		sql.append(" union all");
		sql.append(" Select AccountNo  AccNo, BankBranchId From ChequeDetail_Temp");
		sql.append(" union all");
		sql.append(" Select AccountNumber  AccNo, BankBranchId From CustomerBankInfo");
		sql.append(" union all");
		sql.append(" Select AccountNumber  AccNo, BankBranchId From CustomerBankInfo_Temp");
		sql.append(" ) T");
		sql.append(" Inner Join BankBranches bb on bb.BankBranchId = T.BankBranchId");
		sql.append(" where bb.BankCode = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, i) -> {
				BankDetail bc = new BankDetail();

				bc.setMinAccNoLength(rs.getInt("MinAccNoLength"));
				bc.setAccNoLength(rs.getInt("MaxAccNoLength"));

				return bc;
			}, bankCode);
		} catch (EmptyResultDataAccessException dae) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

}