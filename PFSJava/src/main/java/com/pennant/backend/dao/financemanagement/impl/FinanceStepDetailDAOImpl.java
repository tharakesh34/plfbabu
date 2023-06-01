package com.pennant.backend.dao.financemanagement.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import com.pennant.backend.dao.financemanagement.FinanceStepDetailDAO;
import com.pennant.backend.model.finance.FinanceStepPolicyDetail;
import com.pennant.backend.model.solutionfactory.StepPolicyDetail;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.resource.Literal;

public class FinanceStepDetailDAOImpl extends BasicDao<StepPolicyDetail> implements FinanceStepDetailDAO {
	private static Logger logger = LogManager.getLogger(FinanceStepDetailDAOImpl.class);

	public FinanceStepDetailDAOImpl() {
		super();
	}

	@Override
	public FinanceStepPolicyDetail getFinStepPolicy() {
		FinanceStepPolicyDetail finStepDetail = new FinanceStepPolicyDetail();

		return finStepDetail;
	}

	@Override
	public FinanceStepPolicyDetail getNewFinStepPolicy() {
		FinanceStepPolicyDetail stepPolicyDetail = getFinStepPolicy();
		stepPolicyDetail.setNewRecord(true);

		return stepPolicyDetail;
	}

	@Override
	public List<FinanceStepPolicyDetail> getFinStepDetailListByFinRef(long finID, String type, boolean isWIF) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinID, FinReference, StepNo, TenorSplitPerc, Installments, RateMargin, EmiSplitPerc");
		sql.append(", SteppedEMI, StepSpecifier, StepStart, StepEnd, AutoCal");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");

		if (isWIF) {
			sql.append(" from WIFFinStepPolicyDetail");
		} else {
			sql.append(" from FinStepPolicyDetail");
		}

		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> ps.setLong(1, finID), (rs, i) -> {
			FinanceStepPolicyDetail spd = new FinanceStepPolicyDetail();

			spd.setFinID(rs.getLong("FinID"));
			spd.setFinReference(rs.getString("FinReference"));
			spd.setStepNo(rs.getInt("StepNo"));
			spd.setTenorSplitPerc(rs.getBigDecimal("TenorSplitPerc"));
			spd.setInstallments(rs.getInt("Installments"));
			spd.setRateMargin(rs.getBigDecimal("RateMargin"));
			spd.setEmiSplitPerc(rs.getBigDecimal("EmiSplitPerc"));
			spd.setSteppedEMI(rs.getBigDecimal("SteppedEMI"));
			spd.setStepSpecifier(rs.getString("StepSpecifier"));
			spd.setStepStart(rs.getDate("StepStart"));
			spd.setStepEnd(rs.getDate("StepEnd"));
			spd.setAutoCal(rs.getBoolean("AutoCal"));
			spd.setVersion(rs.getInt("Version"));
			spd.setLastMntBy(rs.getLong("LastMntBy"));
			spd.setLastMntOn(rs.getTimestamp("LastMntOn"));
			spd.setRecordStatus(rs.getString("RecordStatus"));
			spd.setRoleCode(rs.getString("RoleCode"));
			spd.setNextRoleCode(rs.getString("NextRoleCode"));
			spd.setTaskId(rs.getString("TaskId"));
			spd.setNextTaskId(rs.getString("NextTaskId"));
			spd.setRecordType(rs.getString("RecordType"));
			spd.setWorkflowId(rs.getLong("WorkflowId"));

			return spd;
		});
	}

	@Override
	public void saveList(List<FinanceStepPolicyDetail> finStepDetailList, boolean isWIF, String type) {
		StringBuilder sql = new StringBuilder("Insert Into");
		if (isWIF) {
			sql.append(" WIFFinStepPolicyDetail");
		} else {
			sql.append(" FinStepPolicyDetail");
		}

		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (FinID, FinReference, StepNo, TenorSplitPerc, Installments, RateMargin, EmiSplitPerc");
		sql.append(", SteppedEMI, StepSpecifier, StepStart, StepEnd, AutoCal");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				FinanceStepPolicyDetail spd = finStepDetailList.get(i);
				int index = 1;

				ps.setLong(index++, spd.getFinID());
				ps.setString(index++, spd.getFinReference());
				ps.setInt(index++, spd.getStepNo());
				ps.setBigDecimal(index++, spd.getTenorSplitPerc());
				ps.setInt(index++, spd.getInstallments());
				ps.setBigDecimal(index++, spd.getRateMargin());
				ps.setBigDecimal(index++, spd.getEmiSplitPerc());
				ps.setBigDecimal(index++, spd.getSteppedEMI());
				ps.setString(index++, spd.getStepSpecifier());
				ps.setDate(index++, JdbcUtil.getDate(spd.getStepStart()));
				ps.setDate(index++, JdbcUtil.getDate(spd.getStepEnd()));
				ps.setBoolean(index++, spd.isAutoCal());
				ps.setInt(index++, spd.getVersion());
				ps.setLong(index++, spd.getLastMntBy());
				ps.setTimestamp(index++, spd.getLastMntOn());
				ps.setString(index++, spd.getRecordStatus());
				ps.setString(index++, spd.getRoleCode());
				ps.setString(index++, spd.getNextRoleCode());
				ps.setString(index++, spd.getTaskId());
				ps.setString(index++, spd.getNextTaskId());
				ps.setString(index++, spd.getRecordType());
				ps.setLong(index, spd.getWorkflowId());
			}

			@Override
			public int getBatchSize() {
				return finStepDetailList.size();
			}
		});
	}

	@Override
	public void deleteList(long finID, boolean isWIF, String type) {
		StringBuilder sql = new StringBuilder("Delete From");
		if (isWIF) {
			sql.append(" WIFFinStepPolicyDetail");
		} else {
			sql.append(" FinStepPolicyDetail");
		}

		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index, finID);
		});
	}

	@Override
	public List<FinanceStepPolicyDetail> getStepDetailsForLMSEvent(long finID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinID, FinReference, StepNo, TenorSplitPerc, Installments, RateMargin, EmiSplitPerc");
		sql.append(", SteppedEMI, StepSpecifier, StepStart, StepEnd, AutoCal");
		sql.append(" From FinStepPolicyDetail");
		sql.append(" Where FinID = ?");

		return jdbcOperations.query(sql.toString(), ps -> ps.setLong(1, finID), (rs, rowNum) -> {
			FinanceStepPolicyDetail spd = new FinanceStepPolicyDetail();

			spd.setFinID(rs.getLong("FinID"));
			spd.setFinReference(rs.getString("FinReference"));
			spd.setStepNo(rs.getInt("StepNo"));
			spd.setTenorSplitPerc(rs.getBigDecimal("TenorSplitPerc"));
			spd.setInstallments(rs.getInt("Installments"));
			spd.setRateMargin(rs.getBigDecimal("RateMargin"));
			spd.setEmiSplitPerc(rs.getBigDecimal("EmiSplitPerc"));
			spd.setSteppedEMI(rs.getBigDecimal("SteppedEMI"));
			spd.setStepSpecifier(rs.getString("StepSpecifier"));
			spd.setStepStart(rs.getDate("StepStart"));
			spd.setStepEnd(rs.getDate("StepEnd"));
			spd.setAutoCal(rs.getBoolean("AutoCal"));

			return spd;
		});
	}
}
