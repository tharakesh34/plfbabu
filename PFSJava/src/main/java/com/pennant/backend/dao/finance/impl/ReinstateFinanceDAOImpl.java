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
 * * FileName : ReinstateFinanceDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 05-05-2011 * *
 * Modified Date : 05-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 05-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.dao.finance.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;

import com.pennant.backend.dao.finance.ReinstateFinanceDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.ReinstateFinance;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

/**
 * DAO methods implementation for the <b>ReinstateFinance model</b> class.<br>
 * 
 */
/**
 * @author manoj.c
 *
 */
public class ReinstateFinanceDAOImpl extends BasicDao<ReinstateFinance> implements ReinstateFinanceDAO {
	private static Logger logger = LogManager.getLogger(ReinstateFinanceDAOImpl.class);

	public ReinstateFinanceDAOImpl() {
		super();
	}

	@Override
	public ReinstateFinance getReinstateFinance() {
		WorkFlowDetails wfd = WorkFlowUtil.getWorkFlowDetails("ReinstateFinance");
		ReinstateFinance rf = new ReinstateFinance();

		if (wfd != null) {
			rf.setWorkflowId(wfd.getWorkFlowId());
		}

		return rf;
	}

	@Override
	public ReinstateFinance getNewReinstateFinance() {
		ReinstateFinance rf = getReinstateFinance();

		rf.setNewRecord(true);

		return rf;
	}

	@Override
	public ReinstateFinance getReinstateFinanceById(long finID, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinID, FinReference");

		if (type.contains("View")) {
			sql.append(", FinCategory, FinPreApprovedRef, FinType, FinCcy, ScheduleMethod");
			sql.append(", ProfitDaysBasis, CustShrtName");
		}

		sql.append(", Version, LastMntOn, LastMntBy, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From ReinstateFinance");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				ReinstateFinance rf = new ReinstateFinance();

				rf.setFinID(rs.getLong("FinID"));
				rf.setFinReference(rs.getString("FinReference"));

				if (type.contains("View")) {
					rf.setFinCategory(rs.getString("FinCategory"));
					rf.setFinPreApprovedRef(rs.getString("FinPreApprovedRef"));
					rf.setFinType(rs.getString("FinType"));
					rf.setFinCcy(rs.getString("FinCcy"));
					rf.setScheduleMethod(rs.getString("ScheduleMethod"));
					rf.setProfitDaysBasis(rs.getString("ProfitDaysBasis"));
					rf.setCustShrtName(rs.getString("CustShrtName"));
				}

				rf.setVersion(rs.getInt("Version"));
				rf.setLastMntOn(rs.getTimestamp("LastMntOn"));
				rf.setLastMntBy(rs.getLong("LastMntBy"));
				rf.setRecordStatus(rs.getString("RecordStatus"));
				rf.setRoleCode(rs.getString("RoleCode"));
				rf.setNextRoleCode(rs.getString("NextRoleCode"));
				rf.setTaskId(rs.getString("TaskId"));
				rf.setNextTaskId(rs.getString("NextTaskId"));
				rf.setRecordType(rs.getString("RecordType"));
				rf.setWorkflowId(rs.getLong("WorkflowId"));

				return rf;
			}, finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public ReinstateFinance getFinanceDetailsById(long finID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" t1.FinID, t1.FinReference, t2.CustCIF, t2.CustShrtName, t1.FinType, t1.FinCategory");
		sql.append(", t3.FinTypeDesc LovDescFinTypeName, t1.FinBranch, t4.BranchDesc LovDescFinBranchName, t1.FinCcy");
		sql.append(", FinAmount, DownPayment, FinStartDate, MaturityDate, TotalProfit, t7.UsrLogin RejectedBy");
		sql.append(", t1.LastMntOn RejectedOn, t9.Activity RejectStatus, t9.Remarks RejectRemarks");
		sql.append(", t1.ScheduleMethod, t1.GrcPeriodEndDate, t1.AllowGrcPeriod, t1.ProfitDaysBasis, t3.Product");
		sql.append(", t1.Version, t1.LastMntOn, t1.LastMntBy, t1.RecordStatus, t1.RoleCode, t1.NextRoleCode");
		sql.append(", t1.TaskId, t1.NextTaskId, t1.RecordType, t1.WorkflowId");
		sql.append(" From RejectFinanceMain t1");
		sql.append(" Left Outer Join Customers t2 on t1.CustID = t2.CustID");
		sql.append(" Left Outer Join RMTFinanceTypes t3 on t1.FinType = t3.FinType");
		sql.append(" Left Outer Join RMTBranches t4 on t1.FinBranch = t4.BranchCode");
		sql.append(" Left Outer Join BMTRejectCodes t6 on t1.RejectStatus = t6.RejectCode");
		sql.append(" Left Outer Join SecUsers t7 on t1.LastMntBy = t7.UsrID");
		sql.append(" Left Outer Join (Select Reference, max(Id) as Id From ReasonHeader group by Reference) t8");
		sql.append(" on T8.Reference = t1.FinReference");
		sql.append(" Left Join ReasonHeader t9 on t9.Id = t8.Id and t9.Reference = T1.FinReference");
		sql.append(" Where FinID = ?");

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				ReinstateFinance rf = new ReinstateFinance();

				rf.setFinID(rs.getLong("FinID"));
				rf.setFinReference(rs.getString("FinReference"));
				rf.setCustCIF(rs.getString("CustCIF"));
				rf.setCustShrtName(rs.getString("CustShrtName"));
				rf.setFinType(rs.getString("FinType"));
				rf.setFinCategory(rs.getString("FinCategory"));
				rf.setLovDescFinTypeName(rs.getString("LovDescFinTypeName"));
				rf.setFinBranch(rs.getString("FinBranch"));
				rf.setLovDescFinBranchName(rs.getString("LovDescFinBranchName"));
				rf.setFinCcy(rs.getString("FinCcy"));
				rf.setFinAmount(rs.getBigDecimal("FinAmount"));
				rf.setDownPayment(rs.getBigDecimal("DownPayment"));
				rf.setFinStartDate(rs.getDate("FinStartDate"));
				rf.setMaturityDate(rs.getDate("MaturityDate"));
				rf.setTotalProfit(rs.getBigDecimal("TotalProfit"));
				rf.setRejectedBy(rs.getString("RejectedBy"));
				rf.setRejectedOn(rs.getDate("RejectedOn"));
				rf.setRejectStatus(rs.getString("RejectStatus"));
				rf.setRejectRemarks(rs.getString("RejectRemarks"));
				rf.setScheduleMethod(rs.getString("ScheduleMethod"));
				rf.setGrcPeriodEndDate(rs.getDate("GrcPeriodEndDate"));
				rf.setAllowGrcPeriod(rs.getBoolean("AllowGrcPeriod"));
				rf.setProfitDaysBasis(rs.getString("ProfitDaysBasis"));
				rf.setProduct(rs.getString("Product"));
				rf.setVersion(rs.getInt("Version"));
				rf.setLastMntOn(rs.getTimestamp("LastMntOn"));
				rf.setLastMntBy(rs.getLong("LastMntBy"));
				rf.setRecordStatus(rs.getString("RecordStatus"));
				rf.setRoleCode(rs.getString("RoleCode"));
				rf.setNextRoleCode(rs.getString("NextRoleCode"));
				rf.setTaskId(rs.getString("TaskId"));
				rf.setNextTaskId(rs.getString("NextTaskId"));
				rf.setRecordType(rs.getString("RecordType"));
				rf.setWorkflowId(rs.getLong("WorkflowId"));

				return rf;
			}, finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public void delete(ReinstateFinance rf, String type) {
		StringBuilder sql = new StringBuilder("Delete From ReinstateFinance");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index, rf.getFinID());
			});

			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}

		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	@Override
	public String save(ReinstateFinance rf, String type) {
		StringBuilder sql = new StringBuilder("Insert Into ReinstateFinance");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (FinID, FinReference");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(", RecordType, WorkflowId)");
		sql.append(" Values( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, rf.getFinID());
			ps.setString(index++, rf.getFinReference());
			ps.setInt(index++, rf.getVersion());
			ps.setLong(index++, rf.getLastMntBy());
			ps.setTimestamp(index++, rf.getLastMntOn());
			ps.setString(index++, rf.getRecordStatus());
			ps.setString(index++, rf.getRoleCode());
			ps.setString(index++, rf.getNextRoleCode());
			ps.setString(index++, rf.getTaskId());
			ps.setString(index++, rf.getNextTaskId());
			ps.setString(index++, rf.getRecordType());
			ps.setLong(index, rf.getWorkflowId());
		});

		return rf.getFinReference();
	}

	@Override
	public void update(ReinstateFinance rf, String type) {
		StringBuilder sql = new StringBuilder("Update ReinstateFinance");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set Version = ?, LastMntBy = ?, LastMntOn = ?, RecordStatus = ?");
		sql.append(", RoleCode = ?, NextRoleCode = ?, TaskId = ?");
		sql.append(", NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		sql.append(" Where FinID = ?");

		if (!type.endsWith("_Temp")) {
			sql.append(" and Version = ?");
		}

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setInt(index++, rf.getVersion());
			ps.setLong(index++, rf.getLastMntBy());
			ps.setTimestamp(index++, rf.getLastMntOn());
			ps.setString(index++, rf.getRecordStatus());
			ps.setString(index++, rf.getRoleCode());
			ps.setString(index++, rf.getNextRoleCode());
			ps.setString(index++, rf.getTaskId());
			ps.setString(index++, rf.getNextTaskId());
			ps.setString(index++, rf.getRecordType());
			ps.setLong(index++, rf.getWorkflowId());

			ps.setLong(index++, rf.getFinID());

			if (!type.endsWith("_Temp")) {
				ps.setInt(index, rf.getVersion() - 1);
			}

		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public void processReInstateFinance(FinanceMain fm) {
		long finID = fm.getFinID();

		saveFinanceChildDetail(
				"Insert Into DocumentDetails_Temp Select * From  RejectDocumentDetails Where ReferenceId = ?",
				fm.getFinReference());
		saveFinanceChildDetail("FinAgreementDetail_Temp", "RejectFinAgreementDetail", finID);
		saveFinanceChildDetail("FinanceEligibilityDetail", "RejectFinanceEligibilityDetail", finID);
		saveFinanceChildDetail("FinanceScoreHeader", "RejectFinanceScoreHeader", finID);
		saveFinanceChildDetail("FinContributorHeader_Temp", "RejectFinContributorHeader", finID);
		saveFinanceChildDetail("FinContributorDetail_Temp", "RejectFinContributorDetail", finID);
		saveFinanceChildDetail("FinDisbursementDetails_Temp", "RejectFinDisbursementdetails", finID);
		saveFinanceChildDetail("FinRepayinstruction_Temp", "RejectFinRepayinstruction", finID);
		saveFinanceChildDetail("FinScheduledetails_Temp", "RejectFinScheduledetails", finID);
		saveFinanceChildDetail("FinDedupDetail", "RejectFinDedupDetail", finID);
		saveFinanceChildDetail("FinBlackListDetail", "RejectFinBlackListDetail", finID);
		saveFinanceChildDetail("FinODPenaltyRates_Temp", "RejectFinODPenaltyRates", finID);
		saveFinanceChildDetail("FinFeeCharges_Temp", "RejectFinFeeCharges", finID);

		StringBuilder sql = new StringBuilder("Insert Into FinanceScoreDetail");
		sql.append(" Select d.HeaderId, d.SubGroupId, d.RuleId, d.MaxScore, d.ExecScore");
		sql.append(" From RejectFinanceScoreDetail d Inner Join FinanceScoreHeader h on d.HeaderId = h.HeaderId");
		sql.append(" Where FinID = ?");
		saveFinanceChildDetail(sql.toString(), finID);
		sql.delete(0, sql.length());

		deleteChildDetailsByQuery("Delete From RejectDocumentDetails Where ReferenceId = ?", fm.getFinReference());
		deleteChildDetailsByTableName("RejectFinAgreementDetail", finID);
		deleteChildDetailsByTableName("RejectFinanceEligibilityDetail", finID);
		deleteChildDetailsByTableName("RejectFinContributorDetail", finID);
		deleteChildDetailsByTableName("RejectFinContributorHeader", finID);
		deleteChildDetailsByTableName("RejectFinDisbursementdetails", finID);
		deleteChildDetailsByTableName("RejectFinRepayinstruction", finID);
		deleteChildDetailsByTableName("RejectFinScheduledetails", finID);
		deleteChildDetailsByTableName("RejectFinDedupDetail", finID);
		deleteChildDetailsByTableName("RejectFinBlackListDetail", finID);
		deleteChildDetailsByTableName("RejectFinODPenaltyRates", finID);
		deleteChildDetailsByTableName("RejectFinFeeCharges", finID);

		StringBuilder sql1 = new StringBuilder("Delete From RejectFinanceScoreDetail Where HeaderId in (");
		sql1.append(" Select HeaderId From RejectFinanceScoreHeader");
		sql1.append(" Where FinID = ?)");
		deleteChildDetailsByQuery(sql1.toString(), finID);
		sql1.delete(0, sql.length());

		deleteChildDetailsByTableName("RejectFinanceScoreHeader", finID);
	}

	private void saveFinanceChildDetail(String parentTable, String childTable, long finID) {
		StringBuilder sql = new StringBuilder("Insert Into");
		sql.append(parentTable);
		sql.append("Select * From");
		sql.append(childTable);
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index, finID);
		});
	}

	private void saveFinanceChildDetail(String sql, String finReference) {
		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setString(index, finReference);
		});
	}

	private void saveFinanceChildDetail(String sql, long finID) {
		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setLong(index, finID);
		});
	}

	private void deleteChildDetailsByTableName(String tableName, long finID) {
		StringBuilder sql = new StringBuilder("Delete From");
		sql.append(tableName);
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index, finID);
		});
	}

	private void deleteChildDetailsByQuery(String sql, String finReference) {
		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setString(index, finReference);
		});
	}

	private void deleteChildDetailsByQuery(String sql, long finID) {
		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setLong(index, finID);
		});
	}

	@Override
	public void deleteRejectFinance(ReinstateFinance rf) {
		StringBuilder sql = new StringBuilder("Delete From RejectFinanceMain");
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index, rf.getFinID());

		});
	}
}