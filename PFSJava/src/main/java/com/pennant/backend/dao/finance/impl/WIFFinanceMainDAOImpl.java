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
 * * FileName : WIFFinanceMainDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 12-11-2011 * *
 * Modified Date : 12-11-2011 * * Description : * *
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

import com.pennant.backend.dao.finance.WIFFinanceMainDAO;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

/**
 * DAO methods implementation for the <b>FinanceMain model</b> class.<br>
 * 
 */

public class WIFFinanceMainDAOImpl extends BasicDao<FinanceMain> implements WIFFinanceMainDAO {
	private static Logger logger = LogManager.getLogger(WIFFinanceMainDAOImpl.class);

	public WIFFinanceMainDAOImpl() {
		super();
	}

	@Override
	public FinanceMain getWIFFinanceMainById(long finID, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinID, FinReference, NumberOfTerms, GrcPeriodEndDate, AllowGrcPeriod");
		sql.append(", GraceBaseRate, GraceSpecialRate, GrcPftRate, GrcPftFrq, NextGrcPftDate, AllowGrcPftRvw");
		sql.append(", GrcPftRvwFrq, NextGrcPftRvwDate, AllowGrcCpz, GrcCpzFrq, NextGrcCpzDate, RepayBaseRate");
		sql.append(", RepaySpecialRate, RepayProfitRate, RepayFrq, NextRepayDate, RepayPftFrq, NextRepayPftDate");
		sql.append(", AllowRepayRvw, RepayRvwFrq, NextRepayRvwDate, AllowRepayCpz, RepayCpzFrq, NextRepayCpzDate");
		sql.append(", MaturityDate, CpzAtGraceEnd, DownPayment, ReqRepayAmount, TotalProfit");
		sql.append(", TotalGrcProfit, GrcRateBasis, RepayRateBasis, FinType, FinRemarks, FinCcy, ScheduleMethod");
		sql.append(", ProfitDaysBasis, ReqMaturity, CalTerms, CalMaturity, FirstRepay, LastRepay");
		sql.append(", FinStartDate, FinAmount, FinRepaymentAmount, CustID, Defferments, FinIsActive");
		sql.append(", FinBranch, FinSourceID, AllowedDefRpyChange, AvailedDefRpyChange, AllowedDefFrqChange");
		sql.append(", AvailedDefFrqChange, RecalType, MinDownPayPerc, FinCategory, ProductCategory");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", LovDescFinFormatter, LovDescGraceBaseRateName, LovDescGraceSpecialRateName");
			sql.append(", LovDescRepayBaseRateName, LovDescRepaySpecialRateName, LovDescFinTypeName");
			sql.append(", LovDescFinCcyName, LovDescFinTypeName");
		}

		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId");
		sql.append(", NextTaskId, RecordType, WorkflowId");
		sql.append(" From WIFFinanceMain");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				FinanceMain fm = new FinanceMain();

				fm.setFinID(rs.getLong("FinID"));
				fm.setFinReference(rs.getString("FinReference"));
				fm.setNumberOfTerms(rs.getInt("NumberOfTerms"));
				fm.setGrcPeriodEndDate(rs.getTimestamp("GrcPeriodEndDate"));
				fm.setAllowGrcPeriod(rs.getBoolean("AllowGrcPeriod"));
				fm.setGraceBaseRate(rs.getString("GraceBaseRate"));
				fm.setGraceSpecialRate(rs.getString("GraceSpecialRate"));
				fm.setGrcPftRate(rs.getBigDecimal("GrcPftRate"));
				fm.setGrcPftFrq(rs.getString("GrcPftFrq"));
				fm.setNextGrcPftDate(rs.getTimestamp("NextGrcPftDate"));
				fm.setAllowGrcPftRvw(rs.getBoolean("AllowGrcPftRvw"));
				fm.setGrcPftRvwFrq(rs.getString("GrcPftRvwFrq"));
				fm.setNextGrcPftRvwDate(rs.getTimestamp("NextGrcPftRvwDate"));
				fm.setAllowGrcCpz(rs.getBoolean("AllowGrcCpz"));
				fm.setGrcCpzFrq(rs.getString("GrcCpzFrq"));
				fm.setNextGrcCpzDate(rs.getTimestamp("NextGrcCpzDate"));
				fm.setRepayBaseRate(rs.getString("RepayBaseRate"));
				fm.setRepaySpecialRate(rs.getString("RepaySpecialRate"));
				fm.setRepayProfitRate(rs.getBigDecimal("RepayProfitRate"));
				fm.setRepayFrq(rs.getString("RepayFrq"));
				fm.setNextRepayDate(rs.getTimestamp("NextRepayDate"));
				fm.setRepayPftFrq(rs.getString("RepayPftFrq"));
				fm.setNextRepayPftDate(rs.getTimestamp("NextRepayPftDate"));
				fm.setAllowRepayRvw(rs.getBoolean("AllowRepayRvw"));
				fm.setRepayRvwFrq(rs.getString("RepayRvwFrq"));
				fm.setNextRepayRvwDate(rs.getTimestamp("NextRepayRvwDate"));
				fm.setAllowRepayCpz(rs.getBoolean("AllowRepayCpz"));
				fm.setRepayCpzFrq(rs.getString("RepayCpzFrq"));
				fm.setNextRepayCpzDate(rs.getTimestamp("NextRepayCpzDate"));
				fm.setMaturityDate(rs.getTimestamp("MaturityDate"));
				fm.setCpzAtGraceEnd(rs.getBoolean("CpzAtGraceEnd"));
				fm.setDownPayment(rs.getBigDecimal("DownPayment"));
				fm.setReqRepayAmount(rs.getBigDecimal("ReqRepayAmount"));
				fm.setTotalProfit(rs.getBigDecimal("TotalProfit"));
				// fm.setTotalGrcProfit(rs.getBigDecimal("TotalGrcProfit"));
				fm.setGrcRateBasis(rs.getString("GrcRateBasis"));
				fm.setRepayRateBasis(rs.getString("RepayRateBasis"));
				fm.setFinType(rs.getString("FinType"));
				fm.setFinRemarks(rs.getString("FinRemarks"));
				fm.setFinCcy(rs.getString("FinCcy"));
				fm.setScheduleMethod(rs.getString("ScheduleMethod"));
				fm.setProfitDaysBasis(rs.getString("ProfitDaysBasis"));
				fm.setReqMaturity(rs.getTimestamp("ReqMaturity"));
				fm.setCalTerms(rs.getInt("CalTerms"));
				fm.setCalMaturity(rs.getTimestamp("CalMaturity"));
				fm.setFirstRepay(rs.getBigDecimal("FirstRepay"));
				fm.setLastRepay(rs.getBigDecimal("LastRepay"));
				fm.setFinStartDate(rs.getTimestamp("FinStartDate"));
				fm.setFinAmount(rs.getBigDecimal("FinAmount"));
				fm.setFinRepaymentAmount(rs.getBigDecimal("FinRepaymentAmount"));
				fm.setCustID(rs.getLong("CustID"));
				fm.setDefferments(rs.getInt("Defferments"));
				fm.setFinIsActive(rs.getBoolean("FinIsActive"));
				fm.setFinBranch(rs.getString("FinBranch"));
				fm.setFinSourceID(rs.getString("FinSourceID"));
				fm.setAllowedDefRpyChange(rs.getInt("AllowedDefRpyChange"));
				fm.setAvailedDefRpyChange(rs.getInt("AvailedDefRpyChange"));
				fm.setAllowedDefFrqChange(rs.getInt("AllowedDefFrqChange"));
				fm.setAvailedDefFrqChange(rs.getInt("AvailedDefFrqChange"));
				fm.setRecalType(rs.getString("RecalType"));
				fm.setMinDownPayPerc(rs.getBigDecimal("MinDownPayPerc"));
				fm.setFinCategory(rs.getString("FinCategory"));
				fm.setProductCategory(rs.getString("ProductCategory"));

				if (StringUtils.trimToEmpty(type).contains("View")) {
					// fm.setLovDescFinFormatter(rs.getString("LovDescFinFormatter"));
					// fm.setLovDescGraceBaseRateName(rs.getString("LovDescGraceBaseRateName"));
					// fm.setLovDescGraceSpecialRateName(rs.getString("LovDescGraceSpecialRateName"));
					// fm.setLovDescRepayBaseRateName(rs.getString("LovDescRepayBaseRateName"));
					// fm.setLovDescRepaySpecialRateName(rs.getString("LovDescRepaySpecialRateName"));
					fm.setLovDescFinTypeName(rs.getString("LovDescFinTypeName"));
					// fm.setLovDescFinCcyName(rs.getString("LovDescFinCcyName"));
					fm.setLovDescFinTypeName(rs.getString("LovDescFinTypeName"));
				}

				fm.setVersion(rs.getInt("Version"));
				fm.setLastMntBy(rs.getLong("LastMntBy"));
				fm.setLastMntOn(rs.getTimestamp("LastMntOn"));
				fm.setRecordStatus(rs.getString("RecordStatus"));
				fm.setRoleCode(rs.getString("RoleCode"));
				fm.setNextRoleCode(rs.getString("NextRoleCode"));
				fm.setTaskId(rs.getString("TaskId"));
				fm.setNextTaskId(rs.getString("NextTaskId"));
				fm.setRecordType(rs.getString("RecordType"));
				fm.setWorkflowId(rs.getLong("WorkflowId"));

				return fm;
			}, finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public void delete(FinanceMain fm, String type) {
		StringBuilder sql = new StringBuilder("Delete From WIFFinanceMain");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index, fm.getFinID());
			});

			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}

		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	@Override
	public String save(FinanceMain fm, String type) {
		StringBuilder sql = new StringBuilder("Insert Into WIFFinanceMain");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (FinID, FinReference, NumberOfTerms, GrcPeriodEndDate, AllowGrcPeriod");
		sql.append(", GraceBaseRate, GraceSpecialRate, GrcPftRate, GrcPftFrq, NextGrcPftDate, AllowGrcPftRvw");
		sql.append(", GrcPftRvwFrq, NextGrcPftRvwDate, AllowGrcCpz, GrcCpzFrq, NextGrcCpzDate, RepayBaseRate");
		sql.append(", RepaySpecialRate, RepayProfitRate, RepayFrq, NextRepayDate, RepayPftFrq, NextRepayPftDate");
		sql.append(", AllowRepayRvw, RepayRvwFrq, NextRepayRvwDate, AllowRepayCpz, RepayCpzFrq, NextRepayCpzDate");
		sql.append(", MaturityDate, CpzAtGraceEnd, DownPayment, ReqRepayAmount, TotalProfit");
		sql.append(", TotalGrcProfit, GrcRateBasis, RepayRateBasis, FinType, FinRemarks, FinCcy, ScheduleMethod");
		sql.append(", ProfitDaysBasis, ReqMaturity, CalTerms, CalMaturity, FirstRepay, LastRepay");
		sql.append(", FinStartDate, FinAmount, FinRepaymentAmount, CustID, Defferments");
		sql.append(", FinBranch, FinSourceID, AllowedDefRpyChange, AvailedDefRpyChange, AllowedDefFrqChange");
		sql.append(", AvailedDefFrqChange, RecalType, FinIsActive, FinCategory, ProductCategory");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId");
		sql.append(", NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, fm.getFinID());
			ps.setString(index++, fm.getFinReference());
			ps.setInt(index++, fm.getNumberOfTerms());
			ps.setDate(index++, JdbcUtil.getDate(fm.getGrcPeriodEndDate()));
			ps.setBoolean(index++, fm.isAllowGrcPeriod());
			ps.setString(index++, fm.getGraceBaseRate());
			ps.setString(index++, fm.getGraceSpecialRate());
			ps.setBigDecimal(index++, fm.getGrcPftRate());
			ps.setString(index++, fm.getGrcPftFrq());
			ps.setDate(index++, JdbcUtil.getDate(fm.getNextGrcPftDate()));
			ps.setBoolean(index++, fm.isAllowGrcPftRvw());
			ps.setString(index++, fm.getGrcPftRvwFrq());
			ps.setDate(index++, JdbcUtil.getDate(fm.getNextGrcPftRvwDate()));
			ps.setBoolean(index++, fm.isAllowGrcCpz());
			ps.setString(index++, fm.getGrcCpzFrq());
			ps.setDate(index++, JdbcUtil.getDate(fm.getNextGrcCpzDate()));
			ps.setString(index++, fm.getRepayBaseRate());
			ps.setString(index++, fm.getRepaySpecialRate());
			ps.setBigDecimal(index++, fm.getRepayProfitRate());
			ps.setString(index++, fm.getRepayFrq());
			ps.setDate(index++, JdbcUtil.getDate(fm.getNextRepayDate()));
			ps.setString(index++, fm.getRepayPftFrq());
			ps.setDate(index++, JdbcUtil.getDate(fm.getNextRepayPftDate()));
			ps.setBoolean(index++, fm.isAllowRepayRvw());
			ps.setString(index++, fm.getRepayRvwFrq());
			ps.setDate(index++, JdbcUtil.getDate(fm.getNextRepayRvwDate()));
			ps.setBoolean(index++, fm.isAllowRepayCpz());
			ps.setString(index++, fm.getRepayCpzFrq());
			ps.setDate(index++, JdbcUtil.getDate(fm.getNextRepayCpzDate()));
			ps.setDate(index++, JdbcUtil.getDate(fm.getMaturityDate()));
			ps.setBoolean(index++, fm.isCpzAtGraceEnd());
			ps.setBigDecimal(index++, fm.getDownPayment());
			ps.setBigDecimal(index++, fm.getReqRepayAmount());
			ps.setBigDecimal(index++, fm.getTotalProfit());
			// ps.setBigDecimal(index++, fm.getTotalGrcProfit());
			ps.setString(index++, fm.getGrcRateBasis());
			ps.setString(index++, fm.getRepayRateBasis());
			ps.setString(index++, fm.getFinType());
			ps.setString(index++, fm.getFinRemarks());
			ps.setString(index++, fm.getFinCcy());
			ps.setString(index++, fm.getScheduleMethod());
			ps.setString(index++, fm.getProfitDaysBasis());
			ps.setDate(index++, JdbcUtil.getDate(fm.getReqMaturity()));
			ps.setInt(index++, fm.getCalTerms());
			ps.setDate(index++, JdbcUtil.getDate(fm.getCalMaturity()));
			ps.setBigDecimal(index++, fm.getFirstRepay());
			ps.setBigDecimal(index++, fm.getLastRepay());
			ps.setDate(index++, JdbcUtil.getDate(fm.getFinStartDate()));
			ps.setBigDecimal(index++, fm.getFinAmount());
			ps.setBigDecimal(index++, fm.getFinRepaymentAmount());
			ps.setLong(index++, fm.getCustID());
			ps.setInt(index++, fm.getDefferments());
			ps.setString(index++, fm.getFinBranch());
			ps.setString(index++, fm.getFinSourceID());
			ps.setInt(index++, fm.getAllowedDefRpyChange());
			ps.setInt(index++, fm.getAvailedDefRpyChange());
			ps.setInt(index++, fm.getAllowedDefFrqChange());
			ps.setInt(index++, fm.getAvailedDefFrqChange());
			ps.setString(index++, fm.getRecalType());
			ps.setBoolean(index++, fm.isFinIsActive());
			ps.setString(index++, fm.getFinCategory());
			ps.setString(index++, fm.getProductCategory());
			ps.setInt(index++, fm.getVersion());
			ps.setLong(index++, fm.getLastMntBy());
			ps.setTimestamp(index++, fm.getLastMntOn());
			ps.setString(index++, fm.getRecordStatus());
			ps.setString(index++, fm.getRoleCode());
			ps.setString(index++, fm.getNextRoleCode());
			ps.setString(index++, fm.getTaskId());
			ps.setString(index++, fm.getNextTaskId());
			ps.setString(index++, fm.getRecordType());
			ps.setLong(index, fm.getWorkflowId());
		});

		return fm.getFinReference();
	}

	@Override
	public void update(FinanceMain fm, String type) {
		StringBuilder sql = new StringBuilder("Update WIFFinanceMain");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set NumberOfTerms = ?, GrcPeriodEndDate = ?, AllowGrcPeriod = ?");
		sql.append(", GraceBaseRate = ?, GraceSpecialRate = ?, GrcPftRate = ?, GrcPftFrq = ?");
		sql.append(", NextGrcPftDate = ?, AllowGrcPftRvw = ?, GrcPftRvwFrq = ?, NextGrcPftRvwDate = ?");
		sql.append(", AllowGrcCpz = ?, GrcCpzFrq = ?, NextGrcCpzDate = ?, RepayBaseRate = ?, RepaySpecialRate = ?");
		sql.append(", RepayProfitRate = ?, RepayFrq = ?, NextRepayDate = ?, RepayPftFrq = ?, NextRepayPftDate = ?");
		sql.append(", AllowRepayRvw = ?, RepayRvwFrq = ?, NextRepayRvwDate = ?, AllowRepayCpz = ?");
		sql.append(", RepayCpzFrq = ?, NextRepayCpzDate = ?, MaturityDate = ?, CpzAtGraceEnd = ?, DownPayment = ?");
		sql.append(", ReqRepayAmount = ?, TotalProfit = ?, TotalGrcProfit = ?, GrcRateBasis = ?, RepayRateBasis = ?");
		sql.append(", FinType = ?, FinRemarks = ?, FinCcy = ?, ScheduleMethod = ?, ProfitDaysBasis = ?");
		sql.append(", ReqMaturity = ?, CalTerms = ?, CalMaturity = ?, FirstRepay = ?, LastRepay = ?");
		sql.append(", FinStartDate = ?, FinAmount = ?, FinRepaymentAmount = ?, CustID = ?, Defferments = ?");
		sql.append(", FinBranch = ?, FinSourceID = ?, AllowedDefRpyChange = ?, AvailedDefRpyChange = ?");
		sql.append(", AllowedDefFrqChange = ?, AvailedDefFrqChange = ?, RecalType = ?, FinIsActive = ?");
		sql.append(", FinCategory = ?, ProductCategory = ?, Version = ?, LastMntBy = ?, LastMntOn = ?");
		sql.append(", RecordStatus = ?, RoleCode = ?, NextRoleCode = ?");
		sql.append(", TaskId = ?, NextTaskId = ?, RecordType = ?, WorkflowId = ?, MinDownPayPerc = ?");
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setInt(index++, fm.getNumberOfTerms());
			ps.setDate(index++, JdbcUtil.getDate(fm.getGrcPeriodEndDate()));
			ps.setBoolean(index++, fm.isAllowGrcPeriod());
			ps.setString(index++, fm.getGraceBaseRate());
			ps.setString(index++, fm.getGraceSpecialRate());
			ps.setBigDecimal(index++, fm.getGrcPftRate());
			ps.setString(index++, fm.getGrcPftFrq());
			ps.setDate(index++, JdbcUtil.getDate(fm.getNextGrcPftDate()));
			ps.setBoolean(index++, fm.isAllowGrcPftRvw());
			ps.setString(index++, fm.getGrcPftRvwFrq());
			ps.setDate(index++, JdbcUtil.getDate(fm.getNextGrcPftRvwDate()));
			ps.setBoolean(index++, fm.isAllowGrcCpz());
			ps.setString(index++, fm.getGrcCpzFrq());
			ps.setDate(index++, JdbcUtil.getDate(fm.getNextGrcCpzDate()));
			ps.setString(index++, fm.getRepayBaseRate());
			ps.setString(index++, fm.getRepaySpecialRate());
			ps.setBigDecimal(index++, fm.getRepayProfitRate());
			ps.setString(index++, fm.getRepayFrq());
			ps.setDate(index++, JdbcUtil.getDate(fm.getNextRepayDate()));
			ps.setString(index++, fm.getRepayPftFrq());
			ps.setDate(index++, JdbcUtil.getDate(fm.getNextRepayPftDate()));
			ps.setBoolean(index++, fm.isAllowRepayRvw());
			ps.setString(index++, fm.getRepayRvwFrq());
			ps.setDate(index++, JdbcUtil.getDate(fm.getNextRepayRvwDate()));
			ps.setBoolean(index++, fm.isAllowRepayCpz());
			ps.setString(index++, fm.getRepayCpzFrq());
			ps.setDate(index++, JdbcUtil.getDate(fm.getNextRepayCpzDate()));
			ps.setDate(index++, JdbcUtil.getDate(fm.getMaturityDate()));
			ps.setBoolean(index++, fm.isCpzAtGraceEnd());
			ps.setBigDecimal(index++, fm.getDownPayment());
			ps.setBigDecimal(index++, fm.getReqRepayAmount());
			ps.setBigDecimal(index++, fm.getTotalProfit());
			// ps.setBigDecimal(index++, fm.getTotalGrcProfit());
			ps.setString(index++, fm.getGrcRateBasis());
			ps.setString(index++, fm.getRepayRateBasis());
			ps.setString(index++, fm.getFinType());
			ps.setString(index++, fm.getFinRemarks());
			ps.setString(index++, fm.getFinCcy());
			ps.setString(index++, fm.getScheduleMethod());
			ps.setString(index++, fm.getProfitDaysBasis());
			ps.setDate(index++, JdbcUtil.getDate(fm.getReqMaturity()));
			ps.setInt(index++, fm.getCalTerms());
			ps.setDate(index++, JdbcUtil.getDate(fm.getCalMaturity()));
			ps.setBigDecimal(index++, fm.getFirstRepay());
			ps.setBigDecimal(index++, fm.getLastRepay());
			ps.setDate(index++, JdbcUtil.getDate(fm.getFinStartDate()));
			ps.setBigDecimal(index++, fm.getFinAmount());
			ps.setBigDecimal(index++, fm.getFinRepaymentAmount());
			ps.setLong(index++, fm.getCustID());
			ps.setInt(index++, fm.getDefferments());
			ps.setString(index++, fm.getFinBranch());
			ps.setString(index++, fm.getFinSourceID());
			ps.setInt(index++, fm.getAllowedDefRpyChange());
			ps.setInt(index++, fm.getAvailedDefRpyChange());
			ps.setInt(index++, fm.getAllowedDefFrqChange());
			ps.setInt(index++, fm.getAvailedDefFrqChange());
			ps.setString(index++, fm.getRecalType());
			ps.setBoolean(index++, fm.isFinIsActive());
			ps.setString(index++, fm.getFinCategory());
			ps.setString(index++, fm.getProductCategory());
			ps.setInt(index++, fm.getVersion());
			ps.setLong(index++, fm.getLastMntBy());
			ps.setTimestamp(index++, fm.getLastMntOn());
			ps.setString(index++, fm.getRecordStatus());
			ps.setString(index++, fm.getRoleCode());
			ps.setString(index++, fm.getNextRoleCode());
			ps.setString(index++, fm.getTaskId());
			ps.setString(index++, fm.getNextTaskId());
			ps.setString(index++, fm.getRecordType());
			ps.setLong(index++, fm.getWorkflowId());
			ps.setBigDecimal(index++, fm.getMinDownPayPerc());

			ps.setLong(index, fm.getFinID());
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

}