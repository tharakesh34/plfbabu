package com.pennant.backend.dao.finance.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;

import com.pennant.backend.dao.finance.FinFlagsHeaderDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.financemanagement.FinanceFlag;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

public class FinFlagsHeaderDAOImpl extends BasicDao<FinanceFlag> implements FinFlagsHeaderDAO {
	private static Logger logger = LogManager.getLogger(FinFlagsHeaderDAOImpl.class);

	public FinFlagsHeaderDAOImpl() {
		super();
	}

	@Override
	public FinanceFlag getFinanceFlags() {
		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("");

		FinanceFlag financeFlags = new FinanceFlag();

		if (workFlowDetails != null) {
			financeFlags.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		return financeFlags;

	}

	@Override
	public FinanceFlag getNewFinanceFlags() {
		FinanceFlag financeFlags = getFinanceFlags();
		financeFlags.setNewRecord(true);

		return financeFlags;
	}

	@Override
	public void save(FinanceFlag ff, String type) {
		StringBuilder sql = new StringBuilder("Insert Into FinFlagsHeader");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (FinID, FinReference");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, ff.getFinID());
			ps.setString(index++, ff.getFinReference());
			ps.setInt(index++, ff.getVersion());
			ps.setLong(index++, ff.getLastMntBy());
			ps.setTimestamp(index++, ff.getLastMntOn());
			ps.setString(index++, ff.getRecordStatus());
			ps.setString(index++, ff.getRoleCode());
			ps.setString(index++, ff.getNextRoleCode());
			ps.setString(index++, ff.getTaskId());
			ps.setString(index++, ff.getNextTaskId());
			ps.setString(index++, ff.getRecordType());
			ps.setLong(index, ff.getWorkflowId());
		});
	}

	@Override
	public void update(FinanceFlag ff, String type) {
		StringBuilder sql = new StringBuilder("Update FinFlagsHeader");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set Version = ?, LastMntBy = ?, LastMntOn = ?, RecordStatus = ?, RoleCode = ?");
		sql.append(" NextRoleCode = ?, TaskId = ?, NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		sql.append(" Where FinID = ?");

		if (!type.endsWith("_Temp")) {
			sql.append(" and Version = ? - 1");
		}

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setInt(index++, ff.getVersion());
			ps.setLong(index++, ff.getLastMntBy());
			ps.setTimestamp(index++, ff.getLastMntOn());
			ps.setString(index++, ff.getRecordStatus());
			ps.setString(index++, ff.getRoleCode());
			ps.setString(index++, ff.getNextRoleCode());
			ps.setString(index++, ff.getTaskId());
			ps.setString(index++, ff.getNextTaskId());
			ps.setString(index++, ff.getRecordType());
			ps.setLong(index++, ff.getWorkflowId());
			ps.setLong(index++, ff.getFinID());

			if (!type.endsWith("_Temp")) {
				ps.setInt(index, ff.getVersion() - 1);
			}

		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public FinanceFlag getFinFlagsHeaderByRef(long finID, String type) {
		StringBuilder sql = new StringBuilder(" Select FinID, FinReference");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", FinType, FinTypeDesc, FinCategory, CustCIF, FinBranch");
			sql.append(", BranchDesc, FinStartDate, NumberOfTerms, GraceTerms, MaturityDate");
			sql.append(", FinCcy, FinAmount, FinRepaymentAmount, ScheduleMethod");
			sql.append(", FeeChargeAmt, DownPayBank, DownPaySupl, EffectiveRateOfReturn, TotalProfit");
		}

		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId");
		sql.append(", NextTaskId, RecordType, WorkflowId");
		sql.append(" From FinFlagsHeader");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, num) -> {
				FinanceFlag ff = new FinanceFlag();

				ff.setFinID(rs.getLong("FinID"));
				ff.setFinReference(rs.getString("FinReference"));

				if (StringUtils.trimToEmpty(type).contains("View")) {
					ff.setFinType(rs.getString("FinType"));
					ff.setFinTypeDesc(rs.getString("FinTypeDesc"));
					ff.setFinCategory(rs.getString("FinCategory"));
					ff.setCustCIF(rs.getString("CustCIF"));
					ff.setFinBranch(rs.getString("FinBranch"));
					// ff.setBranchDesc(rs.getString("BranchDesc"));
					ff.setFinStartDate(rs.getDate("FinStartDate"));
					ff.setNumberOfTerms(rs.getInt("NumberOfTerms"));
					ff.setGraceTerms(rs.getInt("GraceTerms"));
					ff.setMaturityDate(rs.getDate("MaturityDate"));
					ff.setFinCcy(rs.getString("FinCcy"));
					ff.setFinAmount(rs.getBigDecimal("FinAmount"));
					ff.setFinRepaymentAmount(rs.getBigDecimal("FinRepaymentAmount"));
					// ff.setScheduleMethod(rs.getString("ScheduleMethod"));
					ff.setFeeChargeAmt(rs.getBigDecimal("FeeChargeAmt"));
					ff.setDownPayBank(rs.getBigDecimal("DownPayBank"));
					ff.setDownPaySupl(rs.getBigDecimal("DownPaySupl"));
					ff.setEffectiveRateOfReturn(rs.getBigDecimal("EffectiveRateOfReturn"));
					ff.setTotalProfit(rs.getBigDecimal("TotalProfit"));
				}

				ff.setVersion(rs.getInt("Version"));
				ff.setLastMntBy(rs.getLong("LastMntBy"));
				ff.setLastMntOn(rs.getTimestamp("LastMntOn"));
				ff.setRecordStatus(rs.getString("RecordStatus"));
				ff.setRoleCode(rs.getString("RoleCode"));
				ff.setNextRoleCode(rs.getString("NextRoleCode"));
				ff.setTaskId(rs.getString("TaskId"));
				ff.setNextTaskId(rs.getString("NextTaskId"));
				ff.setRecordType(rs.getString("RecordType"));
				ff.setWorkflowId(rs.getLong("WorkflowId"));

				return ff;

			}, finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	public void delete(FinanceFlag ff, String type) {
		StringBuilder sql = new StringBuilder("Delete From FinFlagsHeader");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> ps.setLong(1, ff.getFinID()));
	}
}
