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
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;

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

	public BankDetailDAOImpl() {
		super();
	}

	@Override
	public String save(BankDetail bd, TableType tableType) {
		StringBuilder sql = new StringBuilder("Insert into BMTBankDetail");
		sql.append(tableType.getSuffix());
		sql.append(" (BankCode, BankName, BankShortCode, Active,  AccNoLength, MinAccNoLength");
		sql.append(", AllowMultipleIFSC, Nach, Dd, Dda, Ecs, Cheque, Emandate, AllowedSources");
		sql.append(", UpdateBranches, Version, LastMntBy, LastMntOn, RecordStatus");
		sql.append(", RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setString(index++, bd.getBankCode());
				ps.setString(index++, bd.getBankName());
				ps.setString(index++, bd.getBankShortCode());
				ps.setBoolean(index++, bd.isActive());
				ps.setInt(index++, bd.getAccNoLength());
				ps.setInt(index++, bd.getMinAccNoLength());
				ps.setBoolean(index++, bd.isAllowMultipleIFSC());
				ps.setBoolean(index++, bd.isNach());
				ps.setBoolean(index++, bd.isDd());
				ps.setBoolean(index++, bd.isDda());
				ps.setBoolean(index++, bd.isEcs());
				ps.setBoolean(index++, bd.isCheque());
				ps.setBoolean(index++, bd.isEmandate());
				ps.setString(index++, bd.getAllowedSources());
				ps.setBoolean(index++, bd.isUpdateBranches());
				ps.setInt(index++, bd.getVersion());
				ps.setLong(index++, bd.getLastMntBy());
				ps.setTimestamp(index++, bd.getLastMntOn());
				ps.setString(index++, bd.getRecordStatus());
				ps.setString(index++, bd.getRoleCode());
				ps.setString(index++, bd.getNextRoleCode());
				ps.setString(index++, bd.getTaskId());
				ps.setString(index++, bd.getNextTaskId());
				ps.setString(index++, bd.getRecordType());
				ps.setLong(index, bd.getWorkflowId());
			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		return bd.getId();
	}

	@Override
	public void update(BankDetail bd, TableType tableType) {
		StringBuilder sql = new StringBuilder("Update BMTBankDetail");
		sql.append(tableType.getSuffix());
		sql.append(" Set BankName = ?, BankShortCode = ?, Active = ?");
		sql.append(", AccNoLength = ?, MinAccNoLength = ?, AllowMultipleIFSC = ?");
		sql.append(", Nach = ?, Dd = ?, Dda = ?, Ecs = ?, Cheque = ?, Emandate = ?");
		sql.append(", AllowedSources = ?, UpdateBranches = ?, Version = ?");
		sql.append(", LastMntBy = ?, LastMntOn = ?, RecordStatus = ?");
		sql.append(", RoleCode = ?, NextRoleCode = ?, TaskId = ?");
		sql.append(", NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		sql.append(" where BankCode = ?");
		sql.append(QueryUtil.getConcurrencyClause(tableType));

		logger.debug(Literal.SQL.concat(sql.toString()));

		int recordCount = jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, bd.getBankName());
			ps.setString(index++, bd.getBankShortCode());
			ps.setBoolean(index++, bd.isActive());
			ps.setInt(index++, bd.getAccNoLength());
			ps.setInt(index++, bd.getMinAccNoLength());
			ps.setBoolean(index++, bd.isAllowMultipleIFSC());
			ps.setBoolean(index++, bd.isNach());
			ps.setBoolean(index++, bd.isDd());
			ps.setBoolean(index++, bd.isDda());
			ps.setBoolean(index++, bd.isEcs());
			ps.setBoolean(index++, bd.isCheque());
			ps.setBoolean(index++, bd.isEmandate());
			ps.setString(index++, bd.getAllowedSources());
			ps.setBoolean(index++, bd.isUpdateBranches());
			ps.setInt(index++, bd.getVersion());
			ps.setLong(index++, bd.getLastMntBy());
			ps.setTimestamp(index++, bd.getLastMntOn());
			ps.setString(index++, bd.getRecordStatus());
			ps.setString(index++, bd.getRoleCode());
			ps.setString(index++, bd.getNextRoleCode());
			ps.setString(index++, bd.getTaskId());
			ps.setString(index++, bd.getNextTaskId());
			ps.setString(index++, bd.getRecordType());
			ps.setLong(index++, bd.getWorkflowId());

			ps.setString(index++, bd.getBankCode());

			if (tableType == TableType.TEMP_TAB) {
				ps.setTimestamp(index, bd.getPrevMntOn());
			} else {
				ps.setInt(index, bd.getVersion() - 1);
			}
		});

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public void delete(BankDetail bd, TableType tableType) {
		StringBuilder sql = new StringBuilder("Delete from BMTBankDetail");
		sql.append(tableType.getSuffix());
		sql.append(" Where BankCode = ?");
		sql.append(QueryUtil.getConcurrencyClause(tableType));

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			int recordCount = jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setString(index++, bd.getBankCode());

				if (tableType == TableType.TEMP_TAB) {
					ps.setTimestamp(index++, bd.getPrevMntOn());
				} else {
					ps.setInt(index++, bd.getVersion() - 1);
				}

			});

			if (recordCount == 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	@Override
	public boolean isDuplicateKey(String bankCode, TableType tableType) {
		String sql;
		String whereClause = "BankCode = ?";
		Object[] obj = new Object[] { bankCode };

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("BMTBankDetail", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("BMTBankDetail_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "BMTBankDetail_Temp", "BMTBankDetail" }, whereClause);
			obj = new Object[] { bankCode, bankCode };
			break;
		}

		logger.debug(Literal.SQL.concat(sql));

		return jdbcOperations.queryForObject(sql, Integer.class, obj) > 0;
	}

	@Override
	public BankDetail getBankDetailById(final String id, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" BankCode, BankName, BankShortCode, Active, AccNoLength, MinAccNolength");
		sql.append(", AllowMultipleIFSC, Nach, Dd, Dda, Ecs, Cheque, Emandate, AllowedSources");
		sql.append(", UpdateBranches, Version, LastMntOn, LastMntBy, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" from BMTBankDetail");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where BankCode = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				BankDetail bd = new BankDetail();

				bd.setBankCode(rs.getString("BankCode"));
				bd.setBankName(rs.getString("BankName"));
				bd.setBankShortCode(rs.getString("BankShortCode"));
				bd.setActive(rs.getBoolean("Active"));
				bd.setAccNoLength(rs.getInt("AccNoLength"));
				bd.setMinAccNoLength(rs.getInt("MinAccNolength"));
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
	public BankDetail getBankDetailByIfsc(String ifsc) {
		StringBuilder sql = new StringBuilder("Select bb.BranchDesc, bbd.BankName");
		sql.append(" From BankBranches bb");
		sql.append(" Left Join BMTBankDetail bbd on bb.bankcode = bbd.bankcode");
		sql.append(" Where Ifsc = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				BankDetail bd = new BankDetail();

				bd.setBankBranch(rs.getString("BranchDesc"));
				bd.setBankName(rs.getString("BankName"));

				return bd;
			}, ifsc);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public BankDetail getAccNoLengthByCode(String bankCode) {
		String sql = "Select AccNoLength, MinAccNoLength From BMTBankDetail Where BankCode = ?";

		logger.debug(Literal.SQL.concat(sql));

		try {
			return this.jdbcOperations.queryForObject(sql, (rs, rowNum) -> {
				BankDetail bankDetail = new BankDetail();

				bankDetail.setBankCode(bankCode);
				bankDetail.setAccNoLength(rs.getInt("AccNoLength"));
				bankDetail.setMinAccNoLength(rs.getInt("MinAccNoLength"));

				return bankDetail;
			}, bankCode);
		} catch (EmptyResultDataAccessException dae) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public String getBankCodeByName(String bankName) {
		String sql = "Select BankCode From BMTBankDetail Where BankName = ?";

		logger.debug(Literal.SQL.concat(sql));

		try {
			return this.jdbcOperations.queryForObject(sql, String.class, bankName);
		} catch (EmptyResultDataAccessException dae) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public boolean isBankCodeExits(String bankCode) {
		String sql = "Select BankCode From BMTBankDetail Where BankCode = ? and Active = ?";

		logger.debug(Literal.SQL.concat(sql));

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

		final String UNION_ALL = " Union all";

		StringBuilder sql = new StringBuilder("Select min(");
		sql.append(length);
		sql.append("(AccNo)) MinAccNoLength, max(");
		sql.append(length);
		sql.append("(AccNo)) MaxAccNoLength from (");
		sql.append(" select BeneficiaryAccno AccNo, BankBranchId From FinAdvancePayments");
		sql.append(UNION_ALL);
		sql.append(" Select BeneficiaryAccno AccNo, BankBranchId From FinAdvancePayments_Temp");
		sql.append(UNION_ALL);
		sql.append(" Select AccountNo  AccNo, BankBranchId From PaymentInstructions");
		sql.append(UNION_ALL);
		sql.append(" Select AccountNo  AccNo, BankBranchId From PaymentInstructions_Temp");
		sql.append(UNION_ALL);
		sql.append(" Select AccNumber  AccNo, BankBranchId From Mandates");
		sql.append(UNION_ALL);
		sql.append(" Select AccNumber  AccNo, BankBranchId From Mandates_Temp");
		sql.append(UNION_ALL);
		sql.append(" Select AccountNo  AccNo, BankBranchId From ChequeDetail");
		sql.append(UNION_ALL);
		sql.append(" Select AccountNo  AccNo, BankBranchId From ChequeDetail_Temp");
		sql.append(UNION_ALL);
		sql.append(" Select AccountNumber  AccNo, BankBranchId From CustomerBankInfo");
		sql.append(UNION_ALL);
		sql.append(" Select AccountNumber  AccNo, BankBranchId From CustomerBankInfo_Temp");
		sql.append(" ) T");
		sql.append(" Inner Join BankBranches bb on bb.BankBranchId = T.BankBranchId");
		sql.append(" where bb.BankCode = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

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