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
 * * FileName : FinTypePartnerBankDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 24-04-2017 * *
 * Modified Date : 24-04-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 24-04-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.rmtmasters.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;

import com.pennant.app.constants.AccountConstants;
import com.pennant.backend.dao.rmtmasters.FinTypePartnerBankDAO;
import com.pennant.backend.model.rmtmasters.FinTypePartnerBank;
import com.pennant.pff.extension.PartnerBankExtension;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * Data access layer implementation for <code>FinTypePartnerBank</code> with set of CRUD operations.
 */
public class FinTypePartnerBankDAOImpl extends SequenceDao<FinTypePartnerBank> implements FinTypePartnerBankDAO {
	private static Logger logger = LogManager.getLogger(FinTypePartnerBankDAOImpl.class);

	public FinTypePartnerBankDAOImpl() {
		super();
	}

	@Override
	public FinTypePartnerBank getFinTypePartnerBank(String finType, long id, TableType tableType) {
		StringBuilder sql = getSelectQuery(tableType);
		sql.append(" Where Id = ? and FinType = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				return getFinTypePartnerBank(tableType, rs);
			}, id, finType);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public List<FinTypePartnerBank> getFinTypePartnerBanks(String finType, TableType tableType) {
		StringBuilder sql = getSelectQuery(tableType);
		sql.append(" Where FinType = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), ps -> {
			ps.setString(1, finType);

		}, (rs, rowNum) -> {
			return getFinTypePartnerBank(tableType, rs);
		});
	}

	@Override
	public String save(FinTypePartnerBank fpb, TableType tableType) {
		if (fpb.getId() == Long.MIN_VALUE) {
			fpb.setId(getNextValue("SeqFinTypePartnerBanks"));
			logger.debug("NextID {}", fpb.getId());
		}

		StringBuilder sql = new StringBuilder("Insert into FinTypePartnerBanks");
		sql.append(tableType.getSuffix());
		sql.append(" (Id, FinType, Purpose, PaymentMode, PartnerBankId, VanApplicable, BranchCode, ClusterId");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(", RecordType, WorkflowId)");
		sql.append(" values(");
		sql.append(" ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			jdbcOperations.update(sql.toString(), ps -> {
				int index = 0;
				ps.setLong(++index, fpb.getId());
				ps.setString(++index, fpb.getFinType());
				ps.setString(++index, fpb.getPurpose());
				ps.setString(++index, fpb.getPaymentMode());
				ps.setLong(++index, fpb.getPartnerBankID());
				ps.setBoolean(++index, fpb.isVanApplicable());
				ps.setString(++index, fpb.getBranchCode());
				ps.setObject(++index, fpb.getClusterId());
				ps.setInt(++index, fpb.getVersion());
				ps.setLong(++index, fpb.getLastMntBy());
				ps.setTimestamp(++index, fpb.getLastMntOn());
				ps.setString(++index, fpb.getRecordStatus());
				ps.setString(++index, fpb.getRoleCode());
				ps.setString(++index, fpb.getNextRoleCode());
				ps.setString(++index, fpb.getTaskId());
				ps.setString(++index, fpb.getNextTaskId());
				ps.setString(++index, fpb.getRecordType());
				ps.setLong(++index, fpb.getWorkflowId());

			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		return String.valueOf(fpb.getID());
	}

	@Override
	public void update(FinTypePartnerBank fpb, TableType tableType) {
		StringBuilder sql = new StringBuilder("Update FinTypePartnerBanks");
		sql.append(tableType.getSuffix());
		sql.append(" Set FinType = ?, Purpose = ?, PaymentMode = ?");
		sql.append(", PartnerBankID = ?, vanApplicable = ?, BranchCode = ?");
		sql.append(", ClusterId = ?, LastMntOn = ?, RecordStatus = ?");
		sql.append(", RoleCode = ?, NextRoleCode = ?, TaskId = ?, NextTaskId = ?");
		sql.append(", RecordType = ?, WorkflowId = ?");
		sql.append(" Where Id = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		int recordCount = jdbcOperations.update(sql.toString(), ps -> {
			int index = 0;

			ps.setString(++index, fpb.getFinType());
			ps.setString(++index, fpb.getPurpose());
			ps.setString(++index, fpb.getPaymentMode());
			ps.setLong(++index, fpb.getPartnerBankID());
			ps.setBoolean(++index, fpb.isVanApplicable());
			ps.setString(++index, fpb.getBranchCode());
			ps.setObject(++index, fpb.getClusterId());
			ps.setTimestamp(++index, fpb.getLastMntOn());
			ps.setString(++index, fpb.getRecordStatus());
			ps.setString(++index, fpb.getRoleCode());
			ps.setString(++index, fpb.getNextRoleCode());
			ps.setString(++index, fpb.getTaskId());
			ps.setString(++index, fpb.getNextTaskId());
			ps.setString(++index, fpb.getRecordType());
			ps.setLong(++index, fpb.getWorkflowId());

			ps.setLong(++index, fpb.getID());

		});

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(FinTypePartnerBank fpb, TableType tableType) {
		StringBuilder sql = new StringBuilder("Delete From FinTypePartnerBanks");
		sql.append(tableType.getSuffix());
		sql.append(" Where Id = ?");
		sql.append(QueryUtil.getConcurrencyClause(tableType));

		logger.debug(Literal.SQL.concat(sql.toString()));

		int recordCount = 0;

		try {

			recordCount = jdbcOperations.update(sql.toString(), ps -> {
				int index = 0;

				ps.setLong(++index, fpb.getID());
				if (tableType == TableType.TEMP_TAB) {
					ps.setTimestamp(++index, fpb.getPrevMntOn());
				} else {
					ps.setInt(++index, fpb.getVersion() - 1);
				}
			});
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public void deleteByFinType(String finType, TableType tableType) {
		StringBuilder sql = new StringBuilder("Delete From FinTypePartnerBanks");
		sql.append(tableType.getSuffix());
		sql.append(" Where FinType = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		int recordCount = 0;

		try {
			recordCount = jdbcOperations.update(sql.toString(), finType);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public int getPartnerBankCount(String finType, String paymentType, String purpose, long partnerBankID) {
		StringBuilder sql = new StringBuilder("Select Count(*) From FinTypePartnerBanks");
		sql.append(" Where Fintype = ? and PaymentMode = ? and Purpose = ? and PartnerBankID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.queryForObject(sql.toString(), Integer.class, finType, paymentType, purpose,
				partnerBankID);
	}

	@Override
	public int getAssignedPartnerBankCount(long partnerBankId, TableType tableType) {
		StringBuilder sql = new StringBuilder("Select Count(*)");
		sql.append(" From FinTypePartnerBanks");
		sql.append(tableType.getSuffix());
		sql.append(" Where PartnerBankId = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.queryForObject(sql.toString(), Integer.class, partnerBankId);
	}

	@Override
	public FinTypePartnerBank getFinTypePartnerBankByPartnerBankCode(String partnerBankCode, String finType,
			String paymentMode) {

		StringBuilder sql = getSelectQuery(TableType.AVIEW);
		sql.append(" Where PartnerBankCode = ? and FinType = ? and Purpose = ? and PaymentMode = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				return getFinTypePartnerBank(TableType.AVIEW, rs);
			}, partnerBankCode, finType, AccountConstants.PARTNERSBANK_PAYMENT, paymentMode);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}

	}

	@Override
	public List<FinTypePartnerBank> getFinTypePartnerBanks(FinTypePartnerBank fpb) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ftpb.Id, ft.FinType, ft.FintypeDesc, Purpose, PaymentMode, pb.PartnerBankID, VanApplicable");
		sql.append(", b.BranchCode, b.BranchDesc, ftpb.ClusterId, c.Code ClusterCode, c.Name, c.ClusterType");
		sql.append(", AccountNo, AcType AccountType, PartnerbankCode, PartnerbankName");
		sql.append(", bd.BankCode, bd.BankName");
		sql.append(", bb.BranchCode PrintingLoc, bb.BranchDesc PrintingLocDesc");
		sql.append(", ftpb.Version, ftpb.LastMntOn, ftpb.LastMntBy, ftpb.RecordStatus");
		sql.append(", ftpb.RoleCode, ftpb.NextRoleCode, ftpb.TaskId, ftpb.NextTaskId");
		sql.append(", ftpb.RecordType, ftpb.WorkflowId");
		sql.append(" From FinTypePartnerBanks ftpb");
		sql.append(" Inner Join PartnerBanks pb on pb.PartnerBankID = ftpb.PartnerBankID");
		sql.append(" Inner Join BMTBankDetail bd on bd.BankCode = pb.BankCode");
		sql.append(" Inner Join RmtFinanceTypes ft on ft.FinType = ftpb.FinType");
		sql.append(" Left Join RMTBranches b on b.BranchCode = ftpb.BranchCode");
		sql.append(" Left Join BankBranches bb on bb.BranchCode = b.DefChequeDDPrintLoc");
		sql.append(" Left Join Clusters c on c.ID = ftpb.ClusterId");
		sql.append(" Where ftpb.FinType = ? and Purpose = ? and PaymentMode = ?");

		if (PartnerBankExtension.BRANCH_OR_CLUSTER.equals("B")) {
			sql.append(" and ftpb.BranchCode = ?");
		} else {
			sql.append(" and ftpb.ClusterId = ?");
		}

		if (fpb.getPartnerBankID() != null && fpb.getPartnerBankID() > 0) {
			sql.append(" and ftpb.PartnerBankID = ?");
		}

		logger.debug(Literal.SQL.concat(sql.toString()));

		return jdbcOperations.query(sql.toString(), ps -> {
			int index = 0;

			ps.setString(++index, fpb.getFinType());
			ps.setString(++index, fpb.getPurpose());
			ps.setString(++index, fpb.getPaymentMode());

			if (PartnerBankExtension.BRANCH_OR_CLUSTER.equals("B")) {
				ps.setString(++index, fpb.getBranchCode());
			} else {
				ps.setObject(++index, fpb.getClusterId());
			}

			if (fpb.getPartnerBankID() != null && fpb.getPartnerBankID() > 0) {
				ps.setObject(++index, fpb.getPartnerBankID());
			}

		}, (rs, rowNum) -> {
			FinTypePartnerBank item = new FinTypePartnerBank();

			item.setID(rs.getLong("Id"));
			item.setFinType(rs.getString("FinType"));
			item.setPurpose(rs.getString("Purpose"));
			item.setPaymentMode(rs.getString("PaymentMode"));
			item.setPartnerBankID(rs.getLong("PartnerBankID"));
			item.setVanApplicable(rs.getBoolean("VanApplicable"));
			item.setBranchCode(rs.getString("BranchCode"));
			item.setBranchDesc(rs.getString("BranchDesc"));
			item.setClusterId(rs.getLong("ClusterId"));
			item.setClusterCode(rs.getString("ClusterCode"));
			item.setName(rs.getString("Name"));
			item.setClusterType(rs.getString("ClusterType"));
			item.setAccountNo(rs.getString("AccountNo"));
			item.setAccountType(rs.getString("AccountType"));
			item.setPartnerBankCode(rs.getString("PartnerBankCode"));
			item.setPartnerBankName(rs.getString("PartnerBankName"));
			item.setIssuingBankCode(rs.getString("BankCode"));
			item.setIssuingBankName(rs.getString("BankName"));
			item.setPrintingLoc(rs.getString("PrintingLoc"));
			item.setPrintingLocDesc(rs.getString("PrintingLocDesc"));
			item.setVersion(rs.getInt("Version"));
			item.setLastMntOn(rs.getTimestamp("LastMntOn"));
			item.setLastMntBy(rs.getLong("LastMntBy"));
			item.setRecordStatus(rs.getString("RecordStatus"));
			item.setRoleCode(rs.getString("RoleCode"));
			item.setNextRoleCode(rs.getString("NextRoleCode"));
			item.setTaskId(rs.getString("TaskId"));
			item.setNextTaskId(rs.getString("NextTaskId"));
			item.setRecordType(rs.getString("RecordType"));
			item.setWorkflowId(rs.getLong("WorkflowId"));

			return item;
		});
	}

	@Override
	public List<Long> getClusterByPartnerbankCode(long partnerbankId) {
		String sql = "Select Distinct ClusterId From FinTypePartnerBanks Where partnerbankId = ? and ClusterId is not null";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.query(sql, ps -> {
			ps.setLong(1, JdbcUtil.getLong(partnerbankId));
		}, (rs, rowNum) -> {
			return rs.getLong("ClusterId");
		});
	}

	@Override
	public List<FinTypePartnerBank> getFintypePartnerBankByBranch(List<String> branchCodes, Long clusterId) {
		StringBuilder sql = getSelectQuery(TableType.AVIEW);
		sql.append(" Where");

		if (PartnerBankExtension.BRANCH_OR_CLUSTER.equals("B")) {
			sql.append(" BranchCode in (").append(JdbcUtil.getInCondition(branchCodes)).append(")");
		} else if (PartnerBankExtension.BRANCH_OR_CLUSTER.equals("C")) {
			sql.append(" ClusterId = ?");
		}

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 0;

			if (PartnerBankExtension.BRANCH_OR_CLUSTER.equals("B")) {
				for (String branchCode : branchCodes) {
					ps.setString(++index, branchCode);
				}
			} else if (PartnerBankExtension.BRANCH_OR_CLUSTER.equals("C")) {
				ps.setObject(++index, clusterId);
			}

		}, (rs, rowNum) -> {
			return getFinTypePartnerBank(TableType.AVIEW, rs);
		});
	}

	@Override
	public int getPartnerBankCountByCluster(FinTypePartnerBank fpb) {
		StringBuilder sql = new StringBuilder("Select Count(*) From FinTypePartnerBanks_View");
		sql.append(" Where Fintype = ? and PaymentMode = ? and Purpose = ? and PartnerBankID = ?");
		if (PartnerBankExtension.BRANCH_OR_CLUSTER.equals("B")) {
			sql.append(" and BranchCode = ?");
		} else if (PartnerBankExtension.BRANCH_OR_CLUSTER.equals("C")) {
			sql.append(" and ClusterId = ?");
		}

		logger.debug(Literal.SQL.concat(sql.toString()));

		Object[] args = null;

		Long partnerBankID = fpb.getPartnerBankID();
		String purpose = fpb.getPurpose();
		String paymentMode = fpb.getPaymentMode();
		String finType = fpb.getFinType();

		if (PartnerBankExtension.BRANCH_OR_CLUSTER.equals("B")) {
			args = new Object[] { finType, paymentMode, purpose, partnerBankID, fpb.getBranchCode() };
		} else if (PartnerBankExtension.BRANCH_OR_CLUSTER.equals("C")) {
			args = new Object[] { finType, paymentMode, purpose, partnerBankID, fpb.getClusterId() };
		}

		return this.jdbcOperations.queryForObject(sql.toString(), Integer.class, args);
	}

	@Override
	public List<FinTypePartnerBank> getFinTypePartnerBanks(FinTypePartnerBank fpb, TableType tableType) {
		StringBuilder sql = getSelectQuery(tableType);
		sql.append(" Where FinType = ? and PaymentMode = ? and  Purpose = ? and Active= ?");

		if (tableType.getSuffix().contains("Aview")) {
			sql.append("and  EntityCode = :EntityCode");
		}

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 0;

			ps.setString(++index, fpb.getFinType());
			ps.setString(++index, fpb.getPaymentMode());
			ps.setString(++index, fpb.getPurpose());
			ps.setString(++index, fpb.getEntityCode());
			ps.setInt(++index, 1);

		}, (rs, rowNum) -> {
			return getFinTypePartnerBank(tableType, rs);
		});
	}

	private FinTypePartnerBank getFinTypePartnerBank(TableType tableType, ResultSet rs) throws SQLException {
		FinTypePartnerBank item = new FinTypePartnerBank();

		item.setID(rs.getLong("Id"));
		item.setFinType(rs.getString("FinType"));
		item.setPurpose(rs.getString("Purpose"));
		item.setPaymentMode(rs.getString("PaymentMode"));
		item.setPartnerBankID(rs.getLong("PartnerBankID"));
		item.setVanApplicable(rs.getBoolean("VanApplicable"));
		item.setBranchCode(rs.getString("BranchCode"));
		item.setClusterId(rs.getLong("ClusterId"));

		if (tableType.getSuffix().contains("View")) {
			item.setPartnerBankName(rs.getString("PartnerBankName"));
			item.setPartnerBankCode(rs.getString("PartnerBankCode"));
			item.setBranchDesc(rs.getString("BranchDesc"));
			item.setClusterCode(rs.getString("ClusterCode"));
			item.setName(rs.getString("Name"));
			item.setClusterType(rs.getString("ClusterType"));
			item.setFinTypeDesc(rs.getString("FinTypeDesc"));
			item.setDivisionCode(rs.getString("FinDivision"));
		}

		if (tableType.getSuffix().contains("Aview")) {
			item.setEntityCode(rs.getString("EntityCode"));
			item.setAccountNo(rs.getString("AccountNo"));
			item.setAccountType(rs.getString("AccountType"));
			// item.setActive(rs.getBoolean("Active"));
		}

		item.setVersion(rs.getInt("Version"));
		item.setLastMntOn(rs.getTimestamp("LastMntOn"));
		item.setLastMntBy(rs.getLong("LastMntBy"));
		item.setRecordStatus(rs.getString("RecordStatus"));
		item.setRoleCode(rs.getString("RoleCode"));
		item.setNextRoleCode(rs.getString("NextRoleCode"));
		item.setTaskId(rs.getString("TaskId"));
		item.setNextTaskId(rs.getString("NextTaskId"));
		item.setRecordType(rs.getString("RecordType"));
		item.setWorkflowId(rs.getLong("WorkflowId"));

		return item;
	}

	private StringBuilder getSelectQuery(TableType tableType) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Id, FinType, Purpose, PaymentMode, PartnerBankID, VanApplicable, BranchCode, ClusterId");

		if (tableType.getSuffix().contains("View")) {
			sql.append(", PartnerBankName, PartnerBankCode, BranchDesc, ClusterCode");
			sql.append(", Name, ClusterType, FintypeDesc, FinDivision");
		}

		if (tableType.getSuffix().contains("Aview")) {
			sql.append(", EntityCode, AccountNo, AccountType, Active");
		}
		sql.append(", Version, LastMntOn, LastMntBy");
		sql.append(", RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From FinTypePartnerBanks");
		sql.append(tableType.getSuffix());
		return sql;
	}

}
