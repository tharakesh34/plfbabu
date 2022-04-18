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
 * * FileName : BankBranchDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 17-10-2016 * * Modified
 * Date : 17-10-2016 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 17-10-2016 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.dao.bmtmasters.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;

import com.pennant.backend.dao.bmtmasters.BankBranchDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.bmtmasters.BankBranch;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * DAO methods implementation for the <b>BankBranch model</b> class.<br>
 * 
 */

public class BankBranchDAOImpl extends SequenceDao<BankBranch> implements BankBranchDAO {

	public BankBranchDAOImpl() {
		super();
	}

	@Override
	public BankBranch getBankBranch() {
		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("BankBranch");
		BankBranch bankBranch = new BankBranch();

		if (workFlowDetails != null) {
			bankBranch.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		return bankBranch;
	}

	@Override
	public BankBranch getNewBankBranch() {
		BankBranch bankBranch = getBankBranch();
		bankBranch.setNewRecord(true);

		return bankBranch;
	}

	@Override
	public BankBranch getBankBranchById(final long id, String type) {
		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where BankBranchID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new BankBranchRM(type), id);
		} catch (EmptyResultDataAccessException e) {
			//
		}

		return null;
	}

	@Override
	public int getBankBranchByIFSC(final String iFSC, long id, String type) {
		StringBuilder sql = new StringBuilder("Select Count(BankBranchID) From BankBranches");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where IFSC = ? and BankBranchID != ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.queryForObject(sql.toString(), Integer.class, iFSC, id);
	}

	@Override
	public int getBankBranchByMICR(final String mICR, long id, String type) {
		StringBuilder sql = new StringBuilder("Select Count(BankBranchID) From BankBranches");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where MICR = ? and BankBranchID != ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.queryForObject(sql.toString(), Integer.class, mICR, id);
	}

	@Override
	public int getBankBrachByBank(String bankCode, String type) {
		StringBuilder sql = new StringBuilder("Select Count(BankBranchID) From BankBranches");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where BankCode = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.queryForObject(sql.toString(), Integer.class, bankCode);
	}

	@Override
	public long save(BankBranch bankBranch, String type) {
		if (bankBranch.getId() == Long.MIN_VALUE) {
			bankBranch.setId(getNextValue("SeqBankBranches"));
		}

		bankBranch.setCity(StringUtils.trimToNull(bankBranch.getCity()));

		StringBuilder sql = new StringBuilder("Insert Into BankBranches");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (BankBranchID, BankCode, BranchCode, BranchDesc, City, MICR");
		sql.append(", IFSC, AddOfBranch, Nach, Dd, Dda, Ecs, Cheque, Active");
		sql.append(", ParentBranch, ParentBranchDesc, Emandate, Allowedsources");
		sql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, bankBranch.getBankBranchID());
			ps.setString(index++, bankBranch.getBankCode());
			ps.setString(index++, bankBranch.getBranchCode());
			ps.setString(index++, bankBranch.getBranchDesc());
			ps.setString(index++, bankBranch.getCity());
			ps.setString(index++, bankBranch.getMICR());
			ps.setString(index++, bankBranch.getIFSC());
			ps.setString(index++, bankBranch.getAddOfBranch());
			ps.setBoolean(index++, bankBranch.isNach());
			ps.setBoolean(index++, bankBranch.isDd());
			ps.setBoolean(index++, bankBranch.isDda());
			ps.setBoolean(index++, bankBranch.isEcs());
			ps.setBoolean(index++, bankBranch.isCheque());
			ps.setBoolean(index++, bankBranch.isActive());
			ps.setString(index++, bankBranch.getParentBranch());
			ps.setString(index++, bankBranch.getParentBranchDesc());
			ps.setBoolean(index++, bankBranch.isEmandate());
			ps.setString(index++, bankBranch.getAllowedSources());
			ps.setInt(index++, bankBranch.getVersion());
			ps.setLong(index++, bankBranch.getLastMntBy());
			ps.setTimestamp(index++, bankBranch.getLastMntOn());
			ps.setString(index++, bankBranch.getRecordStatus());
			ps.setString(index++, bankBranch.getRoleCode());
			ps.setString(index++, bankBranch.getNextRoleCode());
			ps.setString(index++, bankBranch.getTaskId());
			ps.setString(index++, bankBranch.getNextTaskId());
			ps.setString(index++, bankBranch.getRecordType());
			ps.setLong(index++, bankBranch.getWorkflowId());

		});

		return bankBranch.getId();
	}

	@Override
	public void update(BankBranch bankBranch, String type) {
		StringBuilder sql = new StringBuilder("Update BankBranches");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set BankCode = ?, BranchCode = ?, BranchDesc = ?, City = ?, MICR = ?, IFSC = ?");
		sql.append(", AddOfBranch = ?, Nach = ?, Dd = ?, Dda = ?, Ecs = ?, Cheque = ?, Active = ?");
		sql.append(", ParentBranch = ?, ParentBranchDesc = ?, Emandate = ?, AllowedSources = ?");
		sql.append(", Version = ?, LastMntBy = ?, LastMntOn = ?, RecordStatus = ?, RoleCode = ?, NextRoleCode = ?");
		sql.append(", TaskId = ?, NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		sql.append(" Where BankBranchID = ?");

		if (!type.endsWith("_Temp")) {
			sql.append("  and Version = ?");
		}

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, bankBranch.getBankCode());
			ps.setString(index++, bankBranch.getBranchCode());
			ps.setString(index++, bankBranch.getBranchDesc());
			ps.setString(index++, bankBranch.getCity());
			ps.setString(index++, bankBranch.getMICR());
			ps.setString(index++, bankBranch.getIFSC());
			ps.setString(index++, bankBranch.getAddOfBranch());
			ps.setBoolean(index++, bankBranch.isNach());
			ps.setBoolean(index++, bankBranch.isDd());
			ps.setBoolean(index++, bankBranch.isDda());
			ps.setBoolean(index++, bankBranch.isEcs());
			ps.setBoolean(index++, bankBranch.isCheque());
			ps.setBoolean(index++, bankBranch.isActive());
			ps.setString(index++, bankBranch.getParentBranch());
			ps.setString(index++, bankBranch.getParentBranchDesc());
			ps.setBoolean(index++, bankBranch.isEmandate());
			ps.setString(index++, bankBranch.getAllowedSources());
			ps.setInt(index++, bankBranch.getVersion());
			ps.setLong(index++, bankBranch.getLastMntBy());
			ps.setTimestamp(index++, bankBranch.getLastMntOn());
			ps.setString(index++, bankBranch.getRecordStatus());
			ps.setString(index++, bankBranch.getRoleCode());
			ps.setString(index++, bankBranch.getNextRoleCode());
			ps.setString(index++, bankBranch.getTaskId());
			ps.setString(index++, bankBranch.getNextTaskId());
			ps.setString(index++, bankBranch.getRecordType());
			ps.setLong(index++, bankBranch.getWorkflowId());

			ps.setLong(index++, bankBranch.getBankBranchID());

			if (!type.endsWith("_Temp")) {
				ps.setInt(index++, bankBranch.getVersion() - 1);
			}
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public void delete(BankBranch bankBranch, String type) {
		StringBuilder sql = new StringBuilder("Delete From BankBranches");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where BankBranchID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {

				ps.setLong(1, bankBranch.getBankBranchID());
			});
			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	@Override
	public BankBranch getBankBrachByIFSC(String ifsc, String type) {
		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where IFSC = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new BankBranchRM(type), ifsc);
		} catch (EmptyResultDataAccessException e) {
			//
		}

		return null;
	}

	@Override
	public BankBranch getBankBrachByCode(String bankCode, String branchCode, String type) {
		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where BankCode = ? and BranchCode = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new BankBranchRM(type), bankCode, branchCode);
		} catch (EmptyResultDataAccessException e) {
			//
		}

		return null;
	}

	@Override
	public BankBranch getBankBrachByMicr(String micr, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" b.BankCode, bb.MICR, bb.BranchCode, bb.BankBranchID, b.AccNoLength, b.BankName, bb.BranchDesc");
		sql.append(" From BMTBankDetail b ");
		sql.append("Inner Join BankBranches bb on b.Bankcode = bb.bankCode");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where MICR = ? and bb.Active = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				BankBranch bb = new BankBranch();

				bb.setBankCode(rs.getString("BankCode"));
				bb.setMICR(rs.getString("MICR"));
				bb.setBranchCode(rs.getString("BranchCode"));
				bb.setBankBranchID(rs.getLong("BankBranchID"));
				bb.setAccNoLength(rs.getInt("AccNoLength"));
				bb.setBankName(rs.getString("BankName"));
				bb.setBranchDesc(rs.getString("BranchDesc"));
				bb.setActive(true);

				return bb;

			}, micr, 1);
		} catch (EmptyResultDataAccessException e) {
			//
		}

		return null;
	}

	@Override
	public BankBranch getBankBrachByIFSCandMICR(String ifsc, String micr, String type) {
		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where IFSC = ? And MICR = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new BankBranchRM(type), ifsc, micr);
		} catch (EmptyResultDataAccessException e) {
			//
		}

		return null;
	}

	@Override
	public boolean isDuplicateKey(String bankCode, String branchCode, TableType tableType) {
		String sql;
		String whereClause = "BankCode = ? and BranchCode = ?";

		Object[] args = new Object[] { bankCode, branchCode };

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("BankBranches", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("BankBranches_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "BankBranches_Temp", "BankBranches" }, whereClause);

			args = new Object[] { bankCode, branchCode, bankCode, branchCode };

			break;
		}

		logger.debug(Literal.SQL + sql);

		return jdbcOperations.queryForObject(sql, Integer.class, args) > 0;
	}

	@Override
	public int getAccNoLengthByIFSC(String ifscCode, String type) {
		StringBuilder sql = new StringBuilder("Select AccNoLength From BankBranches");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where Ifsc = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), Integer.class, ifscCode);
		} catch (EmptyResultDataAccessException dae) {
			//
			return 0;
		}
	}

	private StringBuilder getSqlQuery(String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" BankBranchID, BankCode, BranchCode, BranchDesc, City, MICR");
		sql.append(", IFSC, AddOfBranch, Nach, Dd, Dda, Ecs, Cheque, Active");
		sql.append(", ParentBranch, ParentBranchDesc, Emandate, AllowedSources");
		sql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", BankName, PCCityName");
		}
		sql.append(" from BankBranches");
		sql.append(StringUtils.trimToEmpty(type));
		return sql;
	}

	private class BankBranchRM implements RowMapper<BankBranch> {
		private String type;

		public BankBranchRM(String type) {
			this.type = type;
		}

		@Override
		public BankBranch mapRow(ResultSet rs, int rowNum) throws SQLException {
			BankBranch bb = new BankBranch();

			bb.setBankBranchID(rs.getLong("BankBranchID"));
			bb.setBankCode(rs.getString("BankCode"));
			bb.setBranchCode(rs.getString("BranchCode"));
			bb.setBranchDesc(rs.getString("BranchDesc"));
			bb.setCity(rs.getString("City"));
			bb.setMICR(rs.getString("MICR"));
			bb.setIFSC(rs.getString("IFSC"));
			bb.setAddOfBranch(rs.getString("AddOfBranch"));
			bb.setNach(rs.getBoolean("Nach"));
			bb.setDd(rs.getBoolean("Dd"));
			bb.setDda(rs.getBoolean("Dda"));
			bb.setEcs(rs.getBoolean("Ecs"));
			bb.setCheque(rs.getBoolean("Cheque"));
			bb.setActive(rs.getBoolean("Active"));
			bb.setParentBranch(rs.getString("ParentBranch"));
			bb.setParentBranchDesc(rs.getString("ParentBranchDesc"));
			bb.setEmandate(rs.getBoolean("Emandate"));
			bb.setAllowedSources(rs.getString("AllowedSources"));
			bb.setVersion(rs.getInt("Version"));
			bb.setLastMntBy(rs.getLong("LastMntBy"));
			bb.setLastMntOn(rs.getTimestamp("LastMntOn"));
			bb.setRecordStatus(rs.getString("RecordStatus"));
			bb.setRoleCode(rs.getString("RoleCode"));
			bb.setNextRoleCode(rs.getString("NextRoleCode"));
			bb.setTaskId(rs.getString("TaskId"));
			bb.setNextTaskId(rs.getString("NextTaskId"));
			bb.setRecordType(rs.getString("RecordType"));
			bb.setWorkflowId(rs.getLong("WorkflowId"));

			if (StringUtils.trimToEmpty(type).contains("View")) {
				bb.setBankName(rs.getString("BankName"));
				bb.setPCCityName(rs.getString("PCCityName"));
			}

			return bb;
		}
	}
}
