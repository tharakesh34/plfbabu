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
 * * FileName : FinFeeReceiptDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 22-12-2019 * * Modified
 * Date : 22-12-2019 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 22-12-2019 Ganesh.P 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.dao.finance.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;

import com.pennant.backend.dao.finance.FinFeeRefundDAO;
import com.pennant.backend.model.finance.FinFeeRefundDetails;
import com.pennant.backend.model.finance.FinFeeRefundHeader;
import com.pennant.backend.model.finance.PrvsFinFeeRefund;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * DAO methods implementation for the <b>FinFeeReceipt model</b> class.<br>
 * 
 */

public class FinFeeRefundDAOImpl extends SequenceDao<FinFeeRefundHeader> implements FinFeeRefundDAO {
	private static Logger logger = LogManager.getLogger(FinFeeRefundDAOImpl.class);

	public FinFeeRefundDAOImpl() {
		super();
	}

	@Override
	public long save(FinFeeRefundHeader frh, String type) {
		StringBuilder sql = new StringBuilder("Insert Into FinFeeRefundHeader");
		sql.append(type);
		sql.append("( HeaderId, FinID, FinReference, LinkedTranId");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(", RecordType, WorkflowId )");
		sql.append(" Values ");
		sql.append("( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		if (frh.getId() <= 0) {
			frh.setId(getNextValue(("SeqFinFeeRefundHeader")));
		}

		logger.debug(Literal.SQL + sql.toString());

		try {
			jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index++, frh.getHeaderId());
				ps.setObject(index++, frh.getFinID());
				ps.setString(index++, frh.getFinReference());
				ps.setLong(index++, frh.getLinkedTranId());
				ps.setInt(index++, frh.getVersion());
				ps.setLong(index++, frh.getLastMntBy());
				ps.setTimestamp(index++, frh.getLastMntOn());
				ps.setString(index++, frh.getRecordStatus());
				ps.setString(index++, frh.getRoleCode());
				ps.setString(index++, frh.getNextRoleCode());
				ps.setString(index++, frh.getTaskId());
				ps.setString(index++, frh.getNextTaskId());
				ps.setString(index++, frh.getRecordType());
				ps.setLong(index, frh.getWorkflowId());
			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		return frh.getId();
	}

	@Override
	public void update(FinFeeRefundHeader frh, String type) {
		StringBuilder sql = new StringBuilder("Update FinFeeRefundHeader");
		sql.append(type);
		sql.append(" Set FinID = ?, FinReference = ?, LinkedTranId = ?");
		sql.append(", Version = ?, LastMntBy = ?, LastMntOn = ?, RecordStatus = ?");
		sql.append(", RoleCode = ?, NextRoleCode = ?, TaskId = ?, NextTaskId = ?");
		sql.append(", RecordType = ?, WorkflowId = ?");
		sql.append(" Where HeaderId = ?");

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setObject(index++, frh.getFinID());
			ps.setString(index++, frh.getFinReference());
			ps.setLong(index++, frh.getLinkedTranId());
			ps.setInt(index++, frh.getVersion());
			ps.setLong(index++, frh.getLastMntBy());
			ps.setTimestamp(index++, frh.getLastMntOn());
			ps.setString(index++, frh.getRecordStatus());
			ps.setString(index++, frh.getRoleCode());
			ps.setString(index++, frh.getNextRoleCode());
			ps.setString(index++, frh.getTaskId());
			ps.setString(index++, frh.getNextTaskId());
			ps.setString(index++, frh.getRecordType());
			ps.setLong(index++, frh.getWorkflowId());

			ps.setLong(index, frh.getHeaderId());
		});

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

	}

	@Override
	public void deleteFinFeeRefundHeader(FinFeeRefundHeader frh, TableType tableType) {
		StringBuilder sql = new StringBuilder("Delete from FinFeeRefundHeader");
		sql.append(tableType.getSuffix());
		sql.append(" Where HeaderId = ?");
		sql.append(QueryUtil.getConcurrencyClause(tableType));

		logger.debug(Literal.SQL + sql.toString());

		try {
			int recordCount = jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;
				ps.setLong(index++, frh.getHeaderId());

				if (tableType == TableType.TEMP_TAB) {
					ps.setTimestamp(index, frh.getPrevMntOn());
				} else {
					ps.setInt(index, frh.getVersion() - 1);
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
	public FinFeeRefundHeader getFinFeeRefundHeaderById(long headerId, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" HeaderId, FinID, FinReference, LinkedTranId");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId ");

		if (type.contains("View")) {
			sql.append(" ,Fintype, FinBranch, FinCcy, lovDescCustCIF, LovDescCustShrtName");
			sql.append(", Fintypedesc, Branchdesc, CustId, FinTDSApplicable");
		}

		sql.append(" From  FinFeeRefundHeader");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where HeaderId = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return jdbcOperations.queryForObject(sql.toString(), (rs, num) -> {
				FinFeeRefundHeader frh = new FinFeeRefundHeader();

				frh.setHeaderId(rs.getLong("HeaderId"));
				frh.setFinID(JdbcUtil.getLong(rs.getObject("FinID")));
				frh.setFinReference(rs.getString("FinReference"));
				frh.setLinkedTranId(rs.getLong("LinkedTranId"));
				frh.setVersion(rs.getInt("Version"));
				frh.setLastMntBy(rs.getLong("LastMntBy"));
				frh.setLastMntOn(rs.getTimestamp("LastMntOn"));
				frh.setRecordStatus(rs.getString("RecordStatus"));
				frh.setRoleCode(rs.getString("RoleCode"));
				frh.setNextRoleCode(rs.getString("NextRoleCode"));
				frh.setTaskId(rs.getString("TaskId"));
				frh.setNextTaskId(rs.getString("NextTaskId"));
				frh.setRecordType(rs.getString("RecordType"));
				frh.setWorkflowId(rs.getLong("WorkflowId"));

				if (type.contains("View")) {
					frh.setFinType(rs.getString("FinType"));
					frh.setFinBranch(rs.getString("FinBranch"));
					frh.setFinCcy(rs.getString("FinCcy"));
					frh.setLovDescCustCIF(rs.getString("LovDescCustCIF"));
					frh.setLovDescCustShrtName(rs.getString("LovDescCustShrtName"));
					frh.setFintypedesc(rs.getString("Fintypedesc"));
					frh.setBranchdesc(rs.getString("Branchdesc"));
					frh.setCustId(rs.getLong("CustId"));
					frh.setFinTDSApplicable(rs.getBoolean("FinTDSApplicable"));
				}

				return frh;
			}, headerId);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public FinFeeRefundDetails getFinFeeRefundDetailsById(long id, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Id, HeaderId, FeeId, RefundAmount, RefundAmtGST, RefundAmtOriginal,");
		sql.append(" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId,");
		sql.append(" RecordType, WorkflowId ");
		sql.append(" From  FinFeeRefundDetails");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where Id = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return jdbcOperations.queryForObject(sql.toString(), (rs, num) -> {
				FinFeeRefundDetails fr = new FinFeeRefundDetails();

				fr.setId(rs.getLong("Id"));
				fr.setHeaderId(rs.getLong("HeaderId"));
				fr.setFeeId(rs.getLong("FeeId"));
				fr.setRefundAmount(rs.getBigDecimal("RefundAmount"));
				fr.setRefundAmtGST(rs.getBigDecimal("RefundAmtGST"));
				fr.setRefundAmtOriginal(rs.getBigDecimal("RefundAmtOriginal"));
				fr.setVersion(rs.getInt("Version"));
				fr.setLastMntBy(rs.getLong("LastMntBy"));
				fr.setLastMntOn(rs.getTimestamp("LastMntOn"));
				fr.setRecordStatus(rs.getString("RecordStatus"));
				fr.setRoleCode(rs.getString("RoleCode"));
				fr.setNextRoleCode(rs.getString("NextRoleCode"));
				fr.setTaskId(rs.getString("TaskId"));
				fr.setNextTaskId(rs.getString("NextTaskId"));
				fr.setRecordType(rs.getString("RecordType"));
				fr.setWorkflowId(rs.getLong("WorkflowId"));

				return fr;
			}, id);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public List<FinFeeRefundDetails> getFinFeeRefundDetailsByHeaderId(long headerId, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Id, HeaderId, FeeId, RefundAmount, RefundAmtGST, RefundAmtOriginal, RefundAmtTds");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(", RecordType, WorkflowId");
		sql.append(" From  FinFeeRefundDetails");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where HeaderId = ? ");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> ps.setLong(1, headerId), (rs, num) -> {
			FinFeeRefundDetails fr = new FinFeeRefundDetails();

			fr.setId(rs.getLong("Id"));
			fr.setHeaderId(rs.getLong("HeaderId"));
			fr.setFeeId(rs.getLong("FeeId"));
			fr.setRefundAmount(rs.getBigDecimal("RefundAmount"));
			fr.setRefundAmtGST(rs.getBigDecimal("RefundAmtGST"));
			fr.setRefundAmtOriginal(rs.getBigDecimal("RefundAmtOriginal"));
			fr.setRefundAmtTDS(rs.getBigDecimal("RefundAmtTds"));
			fr.setVersion(rs.getInt("Version"));
			fr.setLastMntBy(rs.getLong("LastMntBy"));
			fr.setLastMntOn(rs.getTimestamp("LastMntOn"));
			fr.setRecordStatus(rs.getString("RecordStatus"));
			fr.setRoleCode(rs.getString("RoleCode"));
			fr.setNextRoleCode(rs.getString("NextRoleCode"));
			fr.setTaskId(rs.getString("TaskId"));
			fr.setNextTaskId(rs.getString("NextTaskId"));
			fr.setRecordType(rs.getString("RecordType"));
			fr.setWorkflowId(rs.getLong("WorkflowId"));

			return fr;
		});
	}

	@Override
	public String save(FinFeeRefundDetails frd, String type) {
		StringBuilder sql = new StringBuilder("Insert Into FinFeeRefundDetails");
		sql.append(type);
		sql.append("(Id, HeaderId, FeeId, RefundAmount, RefundAmtGST, RefundAmtOriginal, RefundAmtTDS");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(", RecordType, WorkflowId )");
		sql.append(" Values ");
		sql.append("(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		if (frd.getId() <= 0) {
			frd.setId(getNextValue(("SeqFinFeeRefundDetails")));
		}

		logger.debug(Literal.SQL + sql.toString());

		try {
			jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index++, frd.getId());
				ps.setLong(index++, frd.getHeaderId());
				ps.setLong(index++, frd.getFeeId());
				ps.setBigDecimal(index++, frd.getRefundAmount());
				ps.setBigDecimal(index++, frd.getRefundAmtGST());
				ps.setBigDecimal(index++, frd.getRefundAmtOriginal());
				ps.setBigDecimal(index++, frd.getRefundAmtTDS());
				ps.setInt(index++, frd.getVersion());
				ps.setLong(index++, frd.getLastMntBy());
				ps.setTimestamp(index++, frd.getLastMntOn());
				ps.setString(index++, frd.getRecordStatus());
				ps.setString(index++, frd.getRoleCode());
				ps.setString(index++, frd.getNextRoleCode());
				ps.setString(index++, frd.getTaskId());
				ps.setString(index++, frd.getNextTaskId());
				ps.setString(index++, frd.getRecordType());
				ps.setLong(index, frd.getWorkflowId());
			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		return String.valueOf(frd.getId());
	}

	@Override
	public void update(FinFeeRefundDetails frd, String type) {
		StringBuilder sql = new StringBuilder("Update FinFeeRefundDetails");
		sql.append(type);
		sql.append(" Set HeaderId = ?, FeeId = ?, RefundAmount = ?");
		sql.append(", RefundAmtGST = ?, RefundAmtOriginal = ?, RefundAmtTDS = ?");
		sql.append(", Version = ?, LastMntBy = ?, LastMntOn = ?, RecordStatus = ?, RoleCode = ?");
		sql.append(", NextRoleCode = ?, TaskId = ?, NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		sql.append(" Where Id = ?");

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, frd.getHeaderId());
			ps.setLong(index++, frd.getFeeId());
			ps.setBigDecimal(index++, frd.getRefundAmount());
			ps.setBigDecimal(index++, frd.getRefundAmtGST());
			ps.setBigDecimal(index++, frd.getRefundAmtOriginal());
			ps.setBigDecimal(index++, frd.getRefundAmtTDS());
			ps.setInt(index++, frd.getVersion());
			ps.setLong(index++, frd.getLastMntBy());
			ps.setTimestamp(index++, frd.getLastMntOn());
			ps.setString(index++, frd.getRecordStatus());
			ps.setString(index++, frd.getRoleCode());
			ps.setString(index++, frd.getNextRoleCode());
			ps.setString(index++, frd.getTaskId());
			ps.setString(index++, frd.getNextTaskId());
			ps.setString(index++, frd.getRecordType());
			ps.setLong(index++, frd.getWorkflowId());

			ps.setLong(index, frd.getId());
		});

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public void deleteFinFeeRefundDetailsByID(FinFeeRefundDetails frd, String type) {
		StringBuilder sql = new StringBuilder("Delete from FinFeeRefundDetails");
		sql.append(type);
		sql.append(" Where Id = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			int recordCount = jdbcOperations.update(sql.toString(), ps -> ps.setLong(1, frd.getId()));

			if (recordCount == 0) {
				throw new ConcurrencyException();
			}

		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	@Override
	public PrvsFinFeeRefund getPrvsRefundsByFeeId(long feeID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Sum(RefundAmount) TotRefundAmount, Sum(RefundAmtGST) TotRefundAmtGST");
		sql.append(", Sum(RefundAmtOriginal) TotRefundAmtOriginal, Sum(RefundAmtTDS) TotRefundAmtTDS");
		sql.append(" From FinFeeRefundDetails");
		sql.append(" Where FeeId = ?");

		logger.debug(Literal.SQL + sql.toString());
		return jdbcOperations.queryForObject(sql.toString(), (rs, num) -> {
			PrvsFinFeeRefund pffr = new PrvsFinFeeRefund();

			pffr.setTotRefundAmount(rs.getBigDecimal("TotRefundAmount"));
			pffr.setTotRefundAmtGST(rs.getBigDecimal("TotRefundAmtGST"));
			pffr.setTotRefundAmtOriginal(rs.getBigDecimal("TotRefundAmtOriginal"));
			pffr.setTotRefundAmtTDS(rs.getBigDecimal("TotRefundAmtTDS"));

			return pffr;
		}, feeID);
	}

	@Override
	public FinFeeRefundDetails getPrvRefundDetails(long headerId, long feeID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Sum(RefundAmount) RefundAmount, Sum(RefundAmtGST) RefundAmtGST");
		sql.append(", Sum(RefundAmtOriginal) RefundAmtOriginal, Sum(RefundAmtTDS) RefundAmtTDS");
		sql.append(" From FinFeeRefundDetails");
		sql.append(" Where HeaderId <> ? and FeeId = ?");

		logger.debug(Literal.SQL + sql.toString());
		return jdbcOperations.queryForObject(sql.toString(), (rs, num) -> {
			FinFeeRefundDetails feeRefund = new FinFeeRefundDetails();

			feeRefund.setRefundAmount(rs.getBigDecimal("RefundAmount"));
			feeRefund.setRefundAmtGST(rs.getBigDecimal("refundAmtGST"));
			feeRefund.setRefundAmtOriginal(rs.getBigDecimal("RefundAmtOriginal"));
			feeRefund.setRefundAmtTDS(rs.getBigDecimal("RefundAmtTDS"));

			return feeRefund;
		}, headerId, feeID);
	}
}