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
 * * FileName : WIFFinanceScheduleDetailDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 12-11-2011 *
 * * Modified Date : 12-11-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 12-11-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.dao.finance.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;

import com.pennant.backend.dao.finance.WIFFinanceScheduleDetailDAO;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * DAO methods implementation for the <b>WIFFinanceScheduleDetail model</b> class.<br>
 * 
 */

public class WIFFinanceScheduleDetailDAOImpl extends BasicDao<FinanceScheduleDetail>
		implements WIFFinanceScheduleDetailDAO {
	private static Logger logger = LogManager.getLogger(WIFFinanceScheduleDetailDAOImpl.class);

	public WIFFinanceScheduleDetailDAOImpl() {
		super();
	}

	@Override
	public FinanceScheduleDetail getWIFFinanceScheduleDetailById(long finID, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinID, FinReference, SchDate, SchSeq, PftOnSchDate");
		sql.append(", CpzOnSchDate, RepayOnSchDate, RvwOnSchDate, DisbOnSchDate");
		sql.append(", DownpaymentOnSchDate, BalanceForPftCal, BaseRate, SplRate, ActRate, NoOfDays");
		sql.append(", DayFactor, ProfitCalc, ProfitSchd, PrincipalSchd, RepayAmount, ProfitBalance");
		sql.append(", DisbAmount, DownPaymentAmount, CpzAmount, ClosingBalance, ProfitFraction, PrvRepayAmount");
		sql.append(", SchdPriPaid, SchdPftPaid, IsSchdPriPaid, IsSchdPftPaid, Specifier, DefSchdDate");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", LovDescBaseRateName, LovDescSplRateName");
		}

		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId");
		sql.append(", NextTaskId, RecordType, WorkflowId");
		sql.append(" From WIFFinScheduleDetails");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				FinanceScheduleDetail schd = new FinanceScheduleDetail();

				schd.setFinID(rs.getLong("FinID"));
				schd.setFinReference(rs.getString("FinReference"));
				schd.setSchDate(rs.getDate("SchDate"));
				schd.setSchSeq(rs.getInt("SchSeq"));
				schd.setPftOnSchDate(rs.getBoolean("PftOnSchDate"));
				schd.setCpzOnSchDate(rs.getBoolean("CpzOnSchDate"));
				schd.setRepayOnSchDate(rs.getBoolean("RepayOnSchDate"));
				schd.setRvwOnSchDate(rs.getBoolean("RvwOnSchDate"));
				schd.setDisbOnSchDate(rs.getBoolean("DisbOnSchDate"));
				schd.setDownpaymentOnSchDate(rs.getBoolean("DownpaymentOnSchDate"));
				schd.setBalanceForPftCal(rs.getBigDecimal("BalanceForPftCal"));
				schd.setBaseRate(rs.getString("BaseRate"));
				schd.setSplRate(rs.getString("SplRate"));
				schd.setActRate(rs.getBigDecimal("ActRate"));
				schd.setNoOfDays(rs.getInt("NoOfDays"));
				schd.setDayFactor(rs.getBigDecimal("DayFactor"));
				schd.setProfitCalc(rs.getBigDecimal("ProfitCalc"));
				schd.setProfitSchd(rs.getBigDecimal("ProfitSchd"));
				schd.setPrincipalSchd(rs.getBigDecimal("PrincipalSchd"));
				schd.setRepayAmount(rs.getBigDecimal("RepayAmount"));
				schd.setProfitBalance(rs.getBigDecimal("ProfitBalance"));
				schd.setDisbAmount(rs.getBigDecimal("DisbAmount"));
				schd.setDownPaymentAmount(rs.getBigDecimal("DownPaymentAmount"));
				schd.setCpzAmount(rs.getBigDecimal("CpzAmount"));
				schd.setClosingBalance(rs.getBigDecimal("ClosingBalance"));
				schd.setProfitFraction(rs.getBigDecimal("ProfitFraction"));
				schd.setPrvRepayAmount(rs.getBigDecimal("PrvRepayAmount"));
				schd.setSchPriPaid(rs.getBoolean("SchPriPaid"));
				schd.setSchPftPaid(rs.getBoolean("SchPftPaid"));
				// schd.setIsSchdPriPaid(rs.getBoolean("IsSchdPriPaid"));
				// schd.setIsSchdPftPaid(rs.getBoolean("IsSchdPftPaid"));
				schd.setSpecifier(rs.getString("Specifier"));
				schd.setDefSchdDate(rs.getDate("DefSchdDate"));

				if (StringUtils.trimToEmpty(type).contains("View")) {
					// schd.setLovDescBaseRateName(rs.getString("LovDescBaseRateName"));
					// schd.setLovDescSplRateName(rs.getString("LovDescSplRateName"));
				}

				schd.setVersion(rs.getInt("Version"));
				schd.setLastMntBy(rs.getLong("LastMntBy"));
				schd.setLastMntOn(rs.getTimestamp("LastMntOn"));
				schd.setRecordStatus(rs.getString("RecordStatus"));
				schd.setRoleCode(rs.getString("RoleCode"));
				schd.setNextRoleCode(rs.getString("NextRoleCode"));
				schd.setTaskId(rs.getString("TaskId"));
				schd.setNextTaskId(rs.getString("NextTaskId"));
				schd.setRecordType(rs.getString("RecordType"));
				schd.setWorkflowId(rs.getLong("WorkflowId"));

				return schd;
			}, finID);
		} catch (EmptyResultDataAccessException e) {
			//
		}

		return null;
	}

	@Override
	public void delete(FinanceScheduleDetail schd, String type) {
		StringBuilder sql = new StringBuilder("Delete From WIFFinScheduleDetails");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index++, schd.getFinID());
			});

			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}

		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	@Override
	public String save(FinanceScheduleDetail schd, String type) {
		StringBuilder sql = new StringBuilder("Insert Into WIFFinScheduleDetails");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (FinID, FinReference, SchDate, SchSeq, PftOnSchDate");
		sql.append(", CpzOnSchDate, RepayOnSchDate, RvwOnSchDate, DisbOnSchDate");
		sql.append(", DownpaymentOnSchDate, BalanceForPftCal, BaseRate, SplRate, ActRate, NoOfDays");
		sql.append(", DayFactor, ProfitCalc, ProfitSchd, PrincipalSchd, RepayAmount, ProfitBalance");
		sql.append(", DisbAmount, DownPaymentAmount, CpzAmount, ClosingBalance, ProfitFraction");
		sql.append(", PrvRepayAmount, SchdPriPaid, SchdPftPaid, IsSchdPriPaid, IsSchdPftPaid");
		sql.append(", Specifier, DefSchdDate, Version, LastMntBy, LastMntOn, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, schd.getFinID());
			ps.setString(index++, schd.getFinReference());
			ps.setDate(index++, JdbcUtil.getDate(schd.getSchDate()));
			ps.setInt(index++, schd.getSchSeq());
			ps.setBoolean(index++, schd.isPftOnSchDate());
			ps.setBoolean(index++, schd.isCpzOnSchDate());
			ps.setBoolean(index++, schd.isRepayOnSchDate());
			ps.setBoolean(index++, schd.isRvwOnSchDate());
			ps.setBoolean(index++, schd.isDisbOnSchDate());
			ps.setBoolean(index++, schd.isDownpaymentOnSchDate());
			ps.setBigDecimal(index++, schd.getBalanceForPftCal());
			ps.setString(index++, schd.getBaseRate());
			ps.setString(index++, schd.getSplRate());
			ps.setBigDecimal(index++, schd.getActRate());
			ps.setInt(index++, schd.getNoOfDays());
			ps.setBigDecimal(index++, schd.getDayFactor());
			ps.setBigDecimal(index++, schd.getProfitCalc());
			ps.setBigDecimal(index++, schd.getProfitSchd());
			ps.setBigDecimal(index++, schd.getPrincipalSchd());
			ps.setBigDecimal(index++, schd.getRepayAmount());
			ps.setBigDecimal(index++, schd.getProfitBalance());
			ps.setBigDecimal(index++, schd.getDisbAmount());
			ps.setBigDecimal(index++, schd.getDownPaymentAmount());
			ps.setBigDecimal(index++, schd.getCpzAmount());
			ps.setBigDecimal(index++, schd.getClosingBalance());
			ps.setBigDecimal(index++, schd.getProfitFraction());
			ps.setBigDecimal(index++, schd.getPrvRepayAmount());
			ps.setBigDecimal(index++, schd.getSchdPriPaid());
			ps.setBigDecimal(index++, schd.getSchdPftPaid());
			// ps.setBigDecimal(index++, schd.getIsSchdPriPaid());
			// ps.setBigDecimal(index++, schd.getIsSchdPftPaid());
			ps.setString(index++, schd.getSpecifier());
			ps.setDate(index++, JdbcUtil.getDate(schd.getDefSchdDate()));
			ps.setInt(index++, schd.getVersion());
			ps.setLong(index++, schd.getLastMntBy());
			ps.setTimestamp(index++, schd.getLastMntOn());
			ps.setString(index++, schd.getRecordStatus());
			ps.setString(index++, schd.getRoleCode());
			ps.setString(index++, schd.getNextRoleCode());
			ps.setString(index++, schd.getTaskId());
			ps.setString(index++, schd.getNextTaskId());
			ps.setString(index++, schd.getRecordType());
			ps.setLong(index++, schd.getWorkflowId());

		});

		return schd.getFinReference();
	}

	@Override
	public void update(FinanceScheduleDetail schd, String type) {
		StringBuilder sql = new StringBuilder("Update WIFFinScheduleDetails");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set SchDate = ?, SchSeq = ?, PftOnSchDate = ?, CpzOnSchDate = ?, RepayOnSchDate = ?");
		sql.append(", RvwOnSchDate = ?, DisbOnSchDate = ?, DownpaymentOnSchDate = ?, BalanceForPftCal = ?");
		sql.append(", BaseRate = ?, SplRate = ?, ActRate = ?, NoOfDays = ?, DayFactor = ?, ProfitCalc = ?");
		sql.append(", ProfitSchd = ?, PrincipalSchd = ?, RepayAmount = ?, ProfitBalance = ?, DisbAmount = ?");
		sql.append(", DownPaymentAmount = ? CpzAmount = ?, ClosingBalance = ?, ProfitFraction = ?, PrvRepayAmount = ?");
		sql.append(", SchdPriPaid = ?, SchdPftPaid = ?, IsSchdPriPaid = ?, IsSchdPftPaid = ?, Specifier = ?");
		sql.append(", DefSchdDate = ?, Version = ?, LastMntBy = ?");
		sql.append(", LastMntOn = ?, RecordStatus = ?, RoleCode = ?, NextRoleCode = ?, TaskId = ?");
		sql.append(", NextTaskId = ?, RecordType = ?, WorkflowId = ?)");
		sql.append(" Where FinID = ?");

		if (!type.endsWith("_Temp")) {
			sql.append(" and Version = ?");
		}

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setDate(index++, JdbcUtil.getDate(schd.getSchDate()));
			ps.setInt(index++, schd.getSchSeq());
			ps.setBoolean(index++, schd.isPftOnSchDate());
			ps.setBoolean(index++, schd.isCpzOnSchDate());
			ps.setBoolean(index++, schd.isRepayOnSchDate());
			ps.setBoolean(index++, schd.isRvwOnSchDate());
			ps.setBoolean(index++, schd.isDisbOnSchDate());
			ps.setBoolean(index++, schd.isDownpaymentOnSchDate());
			ps.setBigDecimal(index++, schd.getBalanceForPftCal());
			ps.setString(index++, schd.getBaseRate());
			ps.setString(index++, schd.getSplRate());
			ps.setBigDecimal(index++, schd.getActRate());
			ps.setInt(index++, schd.getNoOfDays());
			ps.setBigDecimal(index++, schd.getDayFactor());
			ps.setBigDecimal(index++, schd.getProfitCalc());
			ps.setBigDecimal(index++, schd.getProfitSchd());
			ps.setBigDecimal(index++, schd.getPrincipalSchd());
			ps.setBigDecimal(index++, schd.getRepayAmount());
			ps.setBigDecimal(index++, schd.getProfitBalance());
			ps.setBigDecimal(index++, schd.getDisbAmount());
			ps.setBigDecimal(index++, schd.getDownPaymentAmount());
			ps.setBigDecimal(index++, schd.getCpzAmount());
			ps.setBigDecimal(index++, schd.getClosingBalance());
			ps.setBigDecimal(index++, schd.getProfitFraction());
			ps.setBigDecimal(index++, schd.getPrvRepayAmount());
			ps.setBigDecimal(index++, schd.getSchdPriPaid());
			ps.setBigDecimal(index++, schd.getSchdPftPaid());
			// ps.setBigDecimal(index++, schd.getIsSchdPriPaid());
			// ps.setBigDecimal(index++, schd.getIsSchdPftPaid());
			ps.setString(index++, schd.getSpecifier());
			ps.setDate(index++, JdbcUtil.getDate(schd.getDefSchdDate()));
			ps.setInt(index++, schd.getVersion());
			ps.setLong(index++, schd.getLastMntBy());
			ps.setTimestamp(index++, schd.getLastMntOn());
			ps.setString(index++, schd.getRecordStatus());
			ps.setString(index++, schd.getRoleCode());
			ps.setString(index++, schd.getNextRoleCode());
			ps.setString(index++, schd.getTaskId());
			ps.setString(index++, schd.getNextTaskId());
			ps.setString(index++, schd.getRecordType());
			ps.setLong(index++, schd.getWorkflowId());

			ps.setLong(index++, schd.getFinID());

			if (!type.endsWith("_Temp")) {
				ps.setInt(index++, schd.getVersion() - 1);
			}
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

}