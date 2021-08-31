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
 * * FileName : SubventionDetailDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 12-09-2018 * *
 * Modified Date : 12-09-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 12-09-2018 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.finance.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;

import com.pennant.backend.dao.finance.SubventionDetailDAO;
import com.pennant.backend.model.finance.SubventionDetail;
import com.pennant.backend.model.finance.SubventionScheduleDetail;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.core.TableType;

/**
 * Data access layer implementation for <code>SubventionDetail</code> with set of CRUD operations.
 */
public class SubventionDetailDAOImpl extends BasicDao<SubventionDetail> implements SubventionDetailDAO {
	private static Logger logger = LogManager.getLogger(SubventionDetailDAOImpl.class);

	public SubventionDetailDAOImpl() {
		super();
	}

	@Override
	public SubventionDetail getSubventionDetail(long finID, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinID, FinReference, Method, Type, Rate, PeriodRate, DiscountRate");
		sql.append(", Tenure, StartDate, EndDate");
		sql.append(", Version, LastMntOn, LastMntBy, RecordStatus");
		sql.append(", RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From SubventionDetails");
		sql.append(StringUtils.trim(type));
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				SubventionDetail sd = new SubventionDetail();

				sd.setFinID(rs.getLong("FinID"));
				sd.setFinReference(rs.getString("FinReference"));
				sd.setMethod(rs.getString("Method"));
				sd.setType(rs.getString("Type"));
				sd.setRate(rs.getBigDecimal("Rate"));
				sd.setPeriodRate(rs.getBigDecimal("PeriodRate"));
				sd.setDiscountRate(rs.getBigDecimal("DiscountRate"));
				sd.setTenure(rs.getInt("Tenure"));
				sd.setStartDate(rs.getTimestamp("StartDate"));
				sd.setEndDate(rs.getTimestamp("EndDate"));
				sd.setVersion(rs.getInt("Version"));
				sd.setLastMntOn(rs.getTimestamp("LastMntOn"));
				sd.setLastMntBy(rs.getLong("LastMntBy"));
				sd.setRecordStatus(rs.getString("RecordStatus"));
				sd.setRoleCode(rs.getString("RoleCode"));
				sd.setNextRoleCode(rs.getString("NextRoleCode"));
				sd.setTaskId(rs.getString("TaskId"));
				sd.setNextTaskId(rs.getString("NextTaskId"));
				sd.setRecordType(rs.getString("RecordType"));
				sd.setWorkflowId(rs.getLong("WorkflowId"));

				return sd;
			}, finID);

		} catch (EmptyResultDataAccessException e) {
			//
		}

		return null;
	}

	@Override
	public String save(SubventionDetail sd, TableType tableType) {
		StringBuilder sql = new StringBuilder("Insert Into SubventionDetails");
		sql.append(tableType.getSuffix());
		sql.append(" (FinID, FinReference, Method, Type, Rate, PeriodRate, DiscountRate, Tenure");
		sql.append(", StartDate, EndDate, Version, LastMntBy, LastMntOn, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId, SubVentionAmt)");
		sql.append(" Values(");
		sql.append(" ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(")");

		logger.debug(Literal.SQL + sql.toString());

		try {
			jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index++, sd.getFinID());
				ps.setString(index++, sd.getFinReference());
				ps.setString(index++, sd.getMethod());
				ps.setString(index++, sd.getType());
				ps.setBigDecimal(index++, sd.getRate());
				ps.setBigDecimal(index++, sd.getPeriodRate());
				ps.setBigDecimal(index++, sd.getDiscountRate());
				ps.setInt(index++, sd.getTenure());
				ps.setDate(index++, JdbcUtil.getDate(sd.getStartDate()));
				ps.setDate(index++, JdbcUtil.getDate(sd.getEndDate()));
				ps.setInt(index++, sd.getVersion());
				ps.setLong(index++, sd.getLastMntBy());
				ps.setTimestamp(index++, sd.getLastMntOn());
				ps.setString(index++, sd.getRecordStatus());
				ps.setString(index++, sd.getRoleCode());
				ps.setString(index++, sd.getNextRoleCode());
				ps.setString(index++, sd.getTaskId());
				ps.setString(index++, sd.getNextTaskId());
				ps.setString(index++, sd.getRecordType());
				ps.setLong(index++, sd.getWorkflowId());
				ps.setBigDecimal(index++, sd.getSubVentionAmt());
			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		return String.valueOf(sd.getFinReference());
	}

	@Override
	public void update(SubventionDetail sd, TableType tableType) {
		StringBuilder sql = new StringBuilder("Update SubventionDetails");
		sql.append(tableType.getSuffix());
		sql.append(" Set Method = ?, Type = ?, Rate = ?, PeriodRate = ?");
		sql.append(", DiscountRate = ?, Tenure = ?, StartDate = ?, EndDate = ?");
		sql.append(", LastMntOn = ?, RecordStatus = ?, RoleCode = ?, , NextRoleCode = ?, TaskId = ?");
		sql.append(", NextTaskId = ?, RecordType = ?, WorkflowId = ?, SubVentionAmt = ?");
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, sd.getMethod());
			ps.setString(index++, sd.getType());
			ps.setBigDecimal(index++, sd.getRate());
			ps.setBigDecimal(index++, sd.getPeriodRate());
			ps.setBigDecimal(index++, sd.getDiscountRate());
			ps.setInt(index++, sd.getTenure());
			ps.setDate(index++, JdbcUtil.getDate(sd.getStartDate()));
			ps.setDate(index++, JdbcUtil.getDate(sd.getEndDate()));
			ps.setTimestamp(index++, sd.getLastMntOn());
			ps.setString(index++, sd.getRecordStatus());
			ps.setString(index++, sd.getRoleCode());
			ps.setString(index++, sd.getNextRoleCode());
			ps.setString(index++, sd.getTaskId());
			ps.setString(index++, sd.getNextTaskId());
			ps.setString(index++, sd.getRecordType());
			ps.setLong(index++, sd.getWorkflowId());
			ps.setBigDecimal(index++, sd.getSubVentionAmt());

			ps.setLong(index++, sd.getFinID());
		});

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public void delete(SubventionDetail sd, TableType tableType) {
		StringBuilder sql = new StringBuilder("Delete From SubventionDetails");
		sql.append(tableType.getSuffix());
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index++, sd.getFinID());
			});
		} catch (DataAccessException e) {
			//
		}
	}

	@Override
	public long save(SubventionScheduleDetail ssd, String type) {
		StringBuilder sql = new StringBuilder("Insert Into SubventionScheduleDetail");
		sql.append(type);
		sql.append("(FinID, FinReference, DisbSeqID, SchDate, NoOfDays");
		sql.append(", DiscountedPft, PresentValue, FutureValue, ClosingBal)");
		sql.append(" Values(");
		sql.append(" ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, ssd.getFinID());
			ps.setString(index++, ssd.getFinReference());
			ps.setLong(index++, ssd.getDisbSeqID());
			ps.setDate(index++, JdbcUtil.getDate(ssd.getSchDate()));
			ps.setInt(index++, ssd.getNoOfDays());
			ps.setBigDecimal(index++, ssd.getDiscountedPft());
			ps.setBigDecimal(index++, ssd.getPresentValue());
			ps.setBigDecimal(index++, ssd.getFutureValue());
			ps.setBigDecimal(index++, ssd.getClosingBal());

		});

		return ssd.getDisbSeqID();
	}

	@Override
	public void deleteByFinReference(long finID, String type) {
		StringBuilder sql = new StringBuilder("Delete From SubventionScheduleDetail");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, finID);
		});
	}

	@Override
	public List<SubventionScheduleDetail> getSubventionScheduleDetails(long finID, long disbSeqID, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinID, FinReference, DisbSeqID, SchDate, NoOfDays, DiscountedPft");
		sql.append(", PresentValue, FutureValue, ClosingBal");
		sql.append(" From SubventionScheduleDetail");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ?");

		if (disbSeqID != 0) {
			sql.append(" and DisbSeqID = ?");
		}

		logger.debug(Literal.SQL + sql.toString());

		List<SubventionScheduleDetail> ssdList = this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, finID);

			if (disbSeqID != 0) {
				ps.setLong(index++, disbSeqID);
			}

		}, (rs, rowNum) -> {
			SubventionScheduleDetail ssd = new SubventionScheduleDetail();

			ssd.setFinID(rs.getLong("FinID"));
			ssd.setFinReference(rs.getString("FinReference"));
			ssd.setDisbSeqID(rs.getLong("DisbSeqID"));
			ssd.setSchDate(rs.getDate("SchDate"));
			ssd.setNoOfDays(rs.getInt("NoOfDays"));
			ssd.setDiscountedPft(rs.getBigDecimal("DiscountedPft"));
			ssd.setPresentValue(rs.getBigDecimal("PresentValue"));
			ssd.setFutureValue(rs.getBigDecimal("FutureValue"));
			ssd.setClosingBal(rs.getBigDecimal("ClosingBal"));

			return ssd;
		});

		return ssdList.stream().sorted((s1, s2) -> DateUtil.compare(s1.getSchDate(), s2.getSchDate()))
				.collect(Collectors.toList());
	}

	public void updateSubVebtionAmt(long finID, BigDecimal totalSubVentionAmt) {
		String sql = "Update SubventionDetails Set SubVentionAmt = ? Where FinID = ?";

		logger.debug(Literal.SQL + sql);

		try {
			jdbcOperations.update(sql, ps -> {
				int index = 1;

				ps.setBigDecimal(index++, totalSubVentionAmt);
				ps.setLong(index++, finID);
			});
		} catch (DataAccessException e) {
			//
		}
	}

	@Override
	public BigDecimal getTotalSubVentionAmt(long finID) {
		String sql = "Select SubVentionAmt From SubventionDetails Where FinID = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql, BigDecimal.class, finID);
		} catch (EmptyResultDataAccessException e) {
			//
		}

		return BigDecimal.ZERO;
	}
}
