/**
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
 * * FileName : FinanceDisbursementDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 15-11-2011 * *
 * Modified Date : 15-11-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 15-11-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.dao.finance.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;

import com.pennant.backend.dao.finance.FinanceDisbursementDAO;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.util.FinanceConstants;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.constants.AccountingEvent;

/**
 * DAO methods implementation for the <b>FinanceDisbursement model</b> class.<br>
 * 
 */

public class FinanceDisbursementDAOImpl extends BasicDao<FinanceDisbursement> implements FinanceDisbursementDAO {
	private static Logger logger = LogManager.getLogger(FinanceDisbursementDAOImpl.class);

	public FinanceDisbursementDAOImpl() {
		super();
	}

	@Override
	public FinanceDisbursement getFinanceDisbursementById(long finID, String type, boolean isWIF) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = getSqlQuery(type, isWIF);
		sql.append(" Where FinID = ?");

		logger.trace(Literal.SQL + sql.toString());

		FinanceDisbursementRowMapper rowMapper = new FinanceDisbursementRowMapper(isWIF);

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), rowMapper, finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	public void deleteByFinReference(long finID, String type, boolean isWIF, long logKey) {
		StringBuilder sql = new StringBuilder("Delete From");

		if (isWIF) {
			sql.append(" WIFFinDisbursementDetails");
		} else {
			sql.append(" FinDisbursementDetails");
		}

		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ?");

		if (logKey != 0) {
			sql.append(" and LogKey = ?");
		}

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, finID);

			if (logKey != 0) {
				ps.setLong(index, logKey);
			}
		});
	}

	@Override
	public void delete(FinanceDisbursement fd, String type, boolean isWIF) {
		StringBuilder sql = new StringBuilder("Delete From");

		if (isWIF) {
			sql.append(" WIFFinDisbursementDetails");
		} else {
			sql.append(" FinDisbursementDetails");
		}

		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ? and DisbDate = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
				ps.setLong(1, fd.getFinID());
				ps.setDate(1, JdbcUtil.getDate(fd.getDisbDate()));
			});
			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	@Override
	public String save(FinanceDisbursement fd, String type, boolean isWIF) {
		StringBuilder sql = new StringBuilder("Insert Into");
		if (isWIF) {
			sql.append(" WIFFinDisbursementDetails");
		} else {
			sql.append(" FinDisbursementDetails");
		}
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (FinID, FinReference, DisbDate, DisbSeq, DisbDesc, DisbAmount, DisbReqDate, FeeChargeAmt");

		if (!isWIF) {
			sql.append(", DisbStatus, DisbType, AutoDisb, InstructionUID, InstCalReq, LinkedDisbId");
		}

		sql.append(", DisbIsActive, DisbRemarks, Version , LastMntBy, LastMntOn, RecordStatus");
		sql.append(", RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");

		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?");

		if (!isWIF) {
			sql.append(", ?, ?, ?, ?, ?, ?");
		}

		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, fd.getFinID());
			ps.setString(index++, fd.getFinReference());
			ps.setDate(index++, JdbcUtil.getDate(fd.getDisbDate()));
			ps.setInt(index++, fd.getDisbSeq());
			ps.setString(index++, fd.getDisbDesc());
			ps.setBigDecimal(index++, fd.getDisbAmount());
			ps.setDate(index++, JdbcUtil.getDate(fd.getDisbReqDate()));
			ps.setBigDecimal(index++, fd.getFeeChargeAmt());

			if (!isWIF) {
				ps.setString(index++, fd.getDisbStatus());
				ps.setString(index++, fd.getDisbType());
				ps.setBoolean(index++, fd.isAutoDisb());
				ps.setLong(index++, fd.getInstructionUID());
				ps.setBoolean(index++, fd.isInstCalReq());
				ps.setLong(index++, fd.getLinkedDisbId());
			}

			ps.setBoolean(index++, fd.isDisbIsActive());
			ps.setString(index++, fd.getDisbRemarks());
			ps.setInt(index++, fd.getVersion());
			ps.setLong(index++, fd.getLastMntBy());
			ps.setTimestamp(index++, fd.getLastMntOn());
			ps.setString(index++, fd.getRecordStatus());
			ps.setString(index++, fd.getRoleCode());
			ps.setString(index++, fd.getNextRoleCode());
			ps.setString(index++, fd.getTaskId());
			ps.setString(index++, fd.getNextTaskId());
			ps.setString(index++, fd.getRecordType());
			ps.setLong(index, fd.getWorkflowId());
		});

		return fd.getId();
	}

	@Override
	public void saveList(List<FinanceDisbursement> financeDisbursement, String type, boolean isWIF) {
		StringBuilder sql = new StringBuilder("Insert Into");

		if (isWIF) {
			sql.append(" WIFFinDisbursementDetails");
		} else {
			sql.append(" FinDisbursementDetails");
		}

		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (FinID, FinReference, DisbDate, DisbSeq, DisbDesc, DisbAmount, DisbReqDate, FeeChargeAmt");
		if (!isWIF) {
			sql.append(", DisbStatus, QuickDisb, DisbType, AutoDisb");
			sql.append(", LinkedTranId, InstructionUID, InstCalReq, LinkedDisbId");
			if (type.contains("Log")) {
				sql.append(", LogKey");
			}
		}
		sql.append(", DisbIsActive, DisbRemarks, Version , LastMntBy, LastMntOn, RecordStatus");
		sql.append(", RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");

		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?");

		if (!isWIF) {
			sql.append(", ?, ?, ?, ?, ?, ?, ?, ?");
			if (type.contains("Log")) {
				sql.append(", ?");
			}
		}

		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				FinanceDisbursement fd = financeDisbursement.get(i);
				int index = 1;

				ps.setLong(index++, fd.getFinID());
				ps.setString(index++, fd.getFinReference());
				ps.setDate(index++, JdbcUtil.getDate(fd.getDisbDate()));
				ps.setInt(index++, fd.getDisbSeq());
				ps.setString(index++, fd.getDisbDesc());
				ps.setBigDecimal(index++, fd.getDisbAmount());
				ps.setDate(index++, JdbcUtil.getDate(fd.getDisbReqDate()));
				ps.setBigDecimal(index++, fd.getFeeChargeAmt());

				if (!isWIF) {
					ps.setString(index++, fd.getDisbStatus());
					ps.setBoolean(index++, fd.isQuickDisb());
					ps.setString(index++, fd.getDisbType());
					ps.setBoolean(index++, fd.isAutoDisb());
					ps.setLong(index++, fd.getLinkedTranId());
					ps.setLong(index++, fd.getInstructionUID());
					ps.setBoolean(index++, fd.isInstCalReq());
					ps.setLong(index++, fd.getLinkedDisbId());

					if (type.contains("Log")) {
						ps.setLong(index++, fd.getLogKey());
					}
				}

				ps.setBoolean(index++, fd.isDisbIsActive());
				ps.setString(index++, fd.getDisbRemarks());
				ps.setInt(index++, fd.getVersion());
				ps.setLong(index++, fd.getLastMntBy());
				ps.setTimestamp(index++, fd.getLastMntOn());
				ps.setString(index++, fd.getRecordStatus());
				ps.setString(index++, fd.getRoleCode());
				ps.setString(index++, fd.getNextRoleCode());
				ps.setString(index++, fd.getTaskId());
				ps.setString(index++, fd.getNextTaskId());
				ps.setString(index++, fd.getRecordType());
				ps.setLong(index, fd.getWorkflowId());

			}

			@Override
			public int getBatchSize() {
				return financeDisbursement.size();
			}
		});
	}

	@Override
	public void update(FinanceDisbursement fd, String type, boolean isWIF) {
		StringBuilder sql = new StringBuilder("Update");

		if (isWIF) {
			sql.append(" WIFFinDisbursementDetails");
		} else {
			sql.append(" FinDisbursementDetails");
		}
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set DisbDesc = ?, DisbAmount = ?, FeeChargeAmt = ?");

		if (!isWIF) {
			sql.append(", DisbStatus = ?, DisbType = ?, AutoDisb = ?");
			sql.append(", InstructionUID = ?, InstCalReq = ?, LinkedDisbId = ?");
		}

		sql.append(", DisbReqDate = ?, DisbIsActive = ?, DisbRemarks = ?, Version = ? , LastMntBy = ?");
		sql.append(", LastMntOn = ?, RecordStatus = ?, RoleCode = ?, NextRoleCode = ?");
		sql.append(", TaskId = ?, NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		sql.append(" Where FinID = ? and DisbDate = ? and DisbSeq = ?");

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, fd.getDisbDesc());
			ps.setBigDecimal(index++, fd.getDisbAmount());
			ps.setBigDecimal(index++, fd.getFeeChargeAmt());

			if (!isWIF) {
				ps.setString(index++, fd.getDisbStatus());
				ps.setString(index++, fd.getDisbType());
				ps.setBoolean(index++, fd.isAutoDisb());
				ps.setLong(index++, fd.getInstructionUID());
				ps.setBoolean(index++, fd.isInstCalReq());
				ps.setLong(index++, fd.getLinkedDisbId());
			}

			ps.setDate(index++, JdbcUtil.getDate(fd.getDisbReqDate()));
			ps.setBoolean(index++, fd.isDisbIsActive());
			ps.setString(index++, fd.getDisbRemarks());
			ps.setInt(index++, fd.getVersion());
			ps.setLong(index++, fd.getLastMntBy());
			ps.setTimestamp(index++, fd.getLastMntOn());
			ps.setString(index++, fd.getRecordStatus());
			ps.setString(index++, fd.getRoleCode());
			ps.setString(index++, fd.getNextRoleCode());
			ps.setString(index++, fd.getTaskId());
			ps.setString(index++, fd.getNextTaskId());
			ps.setString(index++, fd.getRecordType());
			ps.setLong(index++, fd.getWorkflowId());

			ps.setLong(index++, fd.getFinID());
			ps.setDate(index++, JdbcUtil.getDate(fd.getDisbDate()));
			ps.setInt(index, fd.getDisbSeq());

		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public int updateBatchDisb(List<FinanceDisbursement> fdList, String type) {
		StringBuilder sql = new StringBuilder("Update FinDisbursementDetails");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set LinkedTranId = ? ");
		sql.append(" Where FinID = ?  And DisbDate = ? And DisbSeq = ?");

		logger.debug(Literal.SQL + sql.toString());

		return jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				FinanceDisbursement fd = fdList.get(i);
				int index = 1;
				ps.setLong(index++, fd.getLinkedTranId());
				ps.setLong(index++, fd.getFinID());
				ps.setDate(index++, JdbcUtil.getDate(fd.getDisbDate()));
				ps.setInt(index, fd.getDisbSeq());
			}

			@Override
			public int getBatchSize() {
				return fdList.size();
			}
		}).length;
	}

	@Override
	public void updateLinkedTranId(long finID, long linkedTranId, String type) {
		StringBuilder sql = new StringBuilder("Update FinDisbursementDetails");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set LinkedTranId = ?");
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			ps.setLong(1, linkedTranId);
			ps.setLong(2, finID);
		});
	}

	@Override
	public List<FinanceDisbursement> getFinanceDisbursementDetails(long finID, String type, boolean isWIF) {
		StringBuilder sql = getSqlQuery(type, isWIF);
		sql.append(" Where FinID = ?");

		FinanceDisbursementRowMapper rowMapper = new FinanceDisbursementRowMapper(isWIF);

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index, finID);
		}, rowMapper);
	}

	private StringBuilder getSqlQuery(String type, boolean isWIF) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinID, FinReference, DisbDate, DisbSeq, DisbDesc, FeeChargeAmt ");
		sql.append(", DisbAmount, DisbReqDate, DisbIsActive, DisbRemarks");

		if (!isWIF) {
			sql.append(", DisbStatus, DisbType");
			sql.append(", AutoDisb, instructionUID, QuickDisb,InstCalReq,LinkedDisbId ");
		}

		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");

		if (isWIF) {
			sql.append(" From WIFFinDisbursementDetails");
		} else {
			sql.append(" From FinDisbursementDetails");
		}

		sql.append(StringUtils.trimToEmpty(type));
		return sql;
	}

	@Override
	public List<FinanceDisbursement> getFinanceDisbursementDetails(long finID, String type, boolean isWIF,
			long logKey) {
		StringBuilder sql = getSqlQuery(type, isWIF);
		sql.append(" Where FinID = ? and LogKey = ?");

		FinanceDisbursementRowMapper rowMapper = new FinanceDisbursementRowMapper(isWIF);

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				int index = 1;
				ps.setLong(index++, finID);
				ps.setLong(index, logKey);
			}
		}, rowMapper);
	}

	@Override
	public List<FinanceDisbursement> getDisbursementToday(long finID, Date disbDate) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinID, FinReference, DisbDate, DisbSeq, FeeChargeAmt, DisbAmount, DisbDate");
		sql.append(" From FinDisbursementDetails");
		sql.append(" Where FinID = ? and DisbDate = ?");
		sql.append(" and (DisbStatus is null or DisbStatus != ?)");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index++, finID);
			ps.setDate(index++, JdbcUtil.getDate(disbDate));
			ps.setString(index, FinanceConstants.DISB_STATUS_CANCEL);

		}, (rs, rowNum) -> {
			FinanceDisbursement fd = new FinanceDisbursement();

			fd.setFinID(rs.getLong("FinID"));
			fd.setFinReference(rs.getString("FinReference"));
			fd.setDisbDate(rs.getTimestamp("DisbDate"));
			fd.setDisbSeq(rs.getInt("DisbSeq"));
			fd.setFeeChargeAmt(rs.getBigDecimal("FeeChargeAmt"));
			fd.setDisbAmount(rs.getBigDecimal("DisbAmount"));

			return fd;
		});
	}

	@Override
	public List<FinanceDisbursement> getDMFinanceDisbursementDetails(long finID, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinID, FinReference, DisbDate, DisbSeq, DisbDesc, FeeChargeAmt");
		sql.append(", DisbAmount, DisbReqDate, DisbIsActive, DisbRemarks, DisbStatus");
		sql.append(", AutoDisb, LastMntBy, LastMntOn, QuickDisb");
		sql.append(" From FinDisbursementDetails");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		List<FinanceDisbursement> list = this.jdbcOperations.query(sql.toString(), ps -> ps.setLong(1, finID),
				(rs, rowNum) -> {
					FinanceDisbursement finDisb = new FinanceDisbursement();

					finDisb.setFinID(rs.getLong("FinID"));
					finDisb.setFinReference(rs.getString("FinReference"));
					finDisb.setDisbDate(rs.getTimestamp("DisbDate"));
					finDisb.setDisbSeq(rs.getInt("DisbSeq"));
					finDisb.setDisbDesc(rs.getString("DisbDesc"));
					finDisb.setFeeChargeAmt(rs.getBigDecimal("FeeChargeAmt"));
					finDisb.setDisbAmount(rs.getBigDecimal("DisbAmount"));
					finDisb.setDisbReqDate(rs.getTimestamp("DisbReqDate"));
					finDisb.setDisbIsActive(rs.getBoolean("DisbIsActive"));
					finDisb.setDisbRemarks(rs.getString("DisbRemarks"));
					finDisb.setDisbStatus(rs.getString("DisbStatus"));
					finDisb.setAutoDisb(rs.getBoolean("AutoDisb"));
					finDisb.setLastMntBy(rs.getLong("LastMntBy"));
					finDisb.setLastMntOn(rs.getTimestamp("LastMntOn"));
					finDisb.setQuickDisb(rs.getBoolean("QuickDisb"));

					return finDisb;
				});

		return list.stream().sorted((l1, l2) -> l1.getDisbDate().compareTo(l1.getDisbDate()))
				.collect(Collectors.toList());
	}

	@Override
	public List<Integer> getFinanceDisbSeqs(long finID, String type, boolean isWIF) {
		StringBuilder sql = new StringBuilder("Select DisbSeq");

		if (isWIF) {
			sql.append(" From WIFFinDisbursementDetails");
		} else {
			sql.append(" From FinDisbursementDetails");
		}

		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> ps.setLong(1, finID), (rs, rowNum) -> rs.getInt(1));
	}

	@Override
	public List<FinanceDisbursement> getDeductDisbFeeDetails(long finID) {
		StringBuilder sql = new StringBuilder();
		sql.append(" Select f.FinID, f.FinReference, d.DisbSeq");
		sql.append(", sum(f.ActualAmount - f.WaivedAmount - f.PaidAmount) DeductFeeDisb");
		sql.append(" From FinFeeDetail_Temp f");
		sql.append(" Inner Join FinDisbursementDetails_Temp d on d.InstructionUID =  f.InstructionUID");
		sql.append(" Where f.FinID = ? and f.FinEvent in (?, ?)");
		sql.append(" Group by f.FinID, f.FinReference, d.DisbSeq");
		sql.append(" Union All");
		sql.append(" Select f.FinID, f.FinReference, d.DisbSeq");
		sql.append(", sum(f.ActualAmount - f.WaivedAmount - f.PaidAmount) DeductFeeDisb");
		sql.append(" From FinFeeDetail f");
		sql.append(" Inner Join FinDisbursementDetails d on d.InstructionUID =  f.InstructionUID");
		sql.append(" Where f.FinID = ? and f.FinEvent in (?, ?)");
		sql.append(" Group by f.FinID, f.FinReference, d.DisbSeq");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, finID);
			ps.setString(index++, AccountingEvent.ADDDBSN);
			ps.setString(index++, AccountingEvent.ADDDBSP);
			ps.setLong(index++, finID);
			ps.setString(index++, AccountingEvent.ADDDBSN);
			ps.setString(index, AccountingEvent.ADDDBSP);
		}, (rs, rowNum) -> {
			FinanceDisbursement fd = new FinanceDisbursement();

			fd.setFinID(rs.getLong("FinID"));
			fd.setFinReference(rs.getString("FinReference"));
			fd.setDisbSeq(rs.getInt("DisbSeq"));
			fd.setDeductFeeDisb(rs.getBigDecimal("DeductFeeDisb"));

			return fd;
		});
	}

	private class FinanceDisbursementRowMapper implements RowMapper<FinanceDisbursement> {
		private boolean wIf;

		private FinanceDisbursementRowMapper(boolean wIf) {
			this.wIf = wIf;
		}

		@Override
		public FinanceDisbursement mapRow(ResultSet rs, int rowNum) throws SQLException {
			FinanceDisbursement finDisb = new FinanceDisbursement();

			finDisb.setFinID(rs.getLong("FinID"));
			finDisb.setFinReference(rs.getString("FinReference"));
			finDisb.setDisbDate(rs.getTimestamp("DisbDate"));
			finDisb.setDisbSeq(rs.getInt("DisbSeq"));
			finDisb.setDisbDesc(rs.getString("DisbDesc"));
			finDisb.setDisbAmount(rs.getBigDecimal("DisbAmount"));
			finDisb.setDisbReqDate(rs.getTimestamp("DisbReqDate"));
			finDisb.setDisbIsActive(rs.getBoolean("DisbIsActive"));
			finDisb.setFeeChargeAmt(rs.getBigDecimal("FeeChargeAmt"));
			finDisb.setDisbRemarks(rs.getString("DisbRemarks"));
			finDisb.setVersion(rs.getInt("Version"));
			finDisb.setLastMntBy(rs.getLong("LastMntBy"));
			finDisb.setLastMntOn(rs.getTimestamp("LastMntOn"));
			finDisb.setRecordStatus(rs.getString("RecordStatus"));
			finDisb.setRoleCode(rs.getString("RoleCode"));
			finDisb.setNextRoleCode(rs.getString("NextRoleCode"));
			finDisb.setTaskId(rs.getString("TaskId"));
			finDisb.setNextTaskId(rs.getString("NextTaskId"));
			finDisb.setRecordType(rs.getString("RecordType"));
			finDisb.setWorkflowId(rs.getLong("WorkflowId"));

			if (!wIf) {
				finDisb.setDisbStatus(rs.getString("DisbStatus"));
				finDisb.setDisbType(rs.getString("DisbType"));
				finDisb.setAutoDisb(rs.getBoolean("AutoDisb"));
				finDisb.setInstructionUID(rs.getLong("instructionUID"));
				finDisb.setQuickDisb(rs.getBoolean("QuickDisb"));
				finDisb.setInstCalReq(rs.getBoolean("InstCalReq"));
				finDisb.setLinkedDisbId(rs.getLong("LinkedDisbId"));
			}

			return finDisb;
		}
	}

	@Override
	public int getFinDsbursmntInstrctnIds(long instructionUid) {
		String sql = "Select count(InstructionUID) from FinDisbursementDetails Where InstructionUID = ?";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.queryForObject(sql, Integer.class, instructionUid);
	}

	@Override
	public List<FinanceDisbursement> getFinanceDisbursementForLMSEvent(long finID) {
		return getFinanceDisbursementDetails(finID, "", false);
	}

	@Override
	public FinanceDisbursement getFinanceDisbursementByInstId(final long instructionUID) {
		String sql = "Select DisbAmount, DisbDate From FinDisbursementDetails Where InstructionUID = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql, (rs, rowNum) -> {
				FinanceDisbursement fd = new FinanceDisbursement();

				fd.setDisbAmount(rs.getBigDecimal("DisbAmount"));
				fd.setDisbDate(JdbcUtil.getDate(rs.getDate("DisbDate")));

				return fd;
			}, instructionUID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}
}
