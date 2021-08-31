package com.pennant.backend.dao.payorderissue.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;

import com.pennant.backend.dao.payorderissue.PayOrderIssueHeaderDAO;
import com.pennant.backend.model.payorderissue.PayOrderIssueHeader;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class PayOrderIssueHeaderDAOImpl extends BasicDao<PayOrderIssueHeader> implements PayOrderIssueHeaderDAO {
	private static Logger logger = LogManager.getLogger(PayOrderIssueHeaderDAOImpl.class);

	public PayOrderIssueHeaderDAOImpl() {
		super();
	}

	@Override
	public void save(PayOrderIssueHeader poi, String type) {
		StringBuilder sql = new StringBuilder("Insert Into PayOrderIssueHeader");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (FinID, FinReference, TotalPOAmount, TotalPOCount, IssuedPOAmount, IssuedPOCount, PODueAmount");
		sql.append(", PODueCount, Version, LastMntBy, LastMntOn, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, poi.getFinID());
			ps.setString(index++, poi.getFinReference());
			ps.setBigDecimal(index++, poi.getTotalPOAmount());
			ps.setInt(index++, poi.getTotalPOCount());
			ps.setBigDecimal(index++, poi.getIssuedPOAmount());
			ps.setInt(index++, poi.getIssuedPOCount());
			ps.setBigDecimal(index++, poi.getpODueAmount());
			ps.setInt(index++, poi.getpODueCount());
			ps.setInt(index++, poi.getVersion());
			ps.setLong(index++, poi.getLastMntBy());
			ps.setTimestamp(index++, poi.getLastMntOn());
			ps.setString(index++, poi.getRecordStatus());
			ps.setString(index++, poi.getRoleCode());
			ps.setString(index++, poi.getNextRoleCode());
			ps.setString(index++, poi.getTaskId());
			ps.setString(index++, poi.getNextTaskId());
			ps.setString(index++, poi.getRecordType());
			ps.setLong(index++, poi.getWorkflowId());
		});

	}

	@Override
	public void update(PayOrderIssueHeader poi, String type) {
		StringBuilder sql = new StringBuilder("Update PayOrderIssueHeader");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set TotalPOAmount = ?, TotalPOCount = ?, IssuedPOAmount = ?, IssuedPOCount = ?");
		sql.append(", PODueAmount = ?, PODueCount = ?, Version = ?, LastMntBy = ?, LastMntOn = ?");
		sql.append(", RecordStatus = ?, RoleCode = ?, NextRoleCode = ?, TaskId = ?");
		sql.append(", NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		sql.append(" Where FinID = ?");

		if (!type.endsWith("_Temp")) {
			sql.append(" and Version = ?");
		}

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setBigDecimal(index++, poi.getTotalPOAmount());
			ps.setInt(index++, poi.getTotalPOCount());
			ps.setBigDecimal(index++, poi.getIssuedPOAmount());
			ps.setInt(index++, poi.getIssuedPOCount());
			ps.setBigDecimal(index++, poi.getpODueAmount());
			ps.setInt(index++, poi.getpODueCount());
			ps.setInt(index++, poi.getVersion());
			ps.setLong(index++, poi.getLastMntBy());
			ps.setTimestamp(index++, poi.getLastMntOn());
			ps.setString(index++, poi.getRecordStatus());
			ps.setString(index++, poi.getRoleCode());
			ps.setString(index++, poi.getNextRoleCode());
			ps.setString(index++, poi.getTaskId());
			ps.setString(index++, poi.getNextTaskId());
			ps.setString(index++, poi.getRecordType());
			ps.setLong(index++, poi.getWorkflowId());
			ps.setLong(index++, poi.getFinID());

			if (!type.endsWith("_Temp")) {
				ps.setInt(index++, poi.getVersion() - 1);
			}
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public PayOrderIssueHeader getPayOrderIssueByHeaderRef(long finID, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinID, FinReference, TotalPOAmount, TotalPOCount, IssuedPOAmount, IssuedPOCount, PODueAmount");
		sql.append(", PODueCount, Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", FinType, CustCIF, CustID, CustShrtName");
			sql.append(", FinTypeDesc, FinCcy, AlwMultiPartyDisb, FinIsActive"); // FinIsActive not availble in AView
		}

		sql.append(" From PayOrderIssueHeader");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				PayOrderIssueHeader poi = new PayOrderIssueHeader();

				poi.setFinID(rs.getLong("FinID"));
				poi.setFinReference(rs.getString("FinReference"));
				poi.setTotalPOAmount(rs.getBigDecimal("TotalPOAmount"));
				poi.setTotalPOCount(rs.getInt("TotalPOCount"));
				poi.setIssuedPOAmount(rs.getBigDecimal("IssuedPOAmount"));
				poi.setIssuedPOCount(rs.getInt("IssuedPOCount"));
				poi.setpODueAmount(rs.getBigDecimal("PODueAmount"));
				poi.setpODueCount(rs.getInt("PODueCount"));
				poi.setVersion(rs.getInt("Version"));
				poi.setLastMntBy(rs.getLong("LastMntBy"));
				poi.setLastMntOn(rs.getTimestamp("LastMntOn"));
				poi.setRecordStatus(rs.getString("RecordStatus"));
				poi.setRoleCode(rs.getString("RoleCode"));
				poi.setNextRoleCode(rs.getString("NextRoleCode"));
				poi.setTaskId(rs.getString("TaskId"));
				poi.setNextTaskId(rs.getString("NextTaskId"));
				poi.setRecordType(rs.getString("RecordType"));
				poi.setWorkflowId(rs.getLong("WorkflowId"));

				if (StringUtils.trimToEmpty(type).contains("View")) {
					poi.setFinType(rs.getString("FinType"));
					poi.setCustCIF(rs.getString("CustCIF"));
					// poi.setCustID(rs.getString("CustID")); (not availble in bean)
					poi.setCustShrtName(rs.getString("CustShrtName"));
					poi.setFinTypeDesc(rs.getString("FinTypeDesc"));
					poi.setFinCcy(rs.getString("FinCcy"));
					poi.setAlwMultiPartyDisb(rs.getBoolean("AlwMultiPartyDisb"));
					poi.setFinIsActive(rs.getBoolean("FinIsActive"));
				}

				return poi;

			}, finID);
		} catch (EmptyResultDataAccessException e) {
			//
		}

		return null;
	}

	public void delete(PayOrderIssueHeader poi, String type) {
		StringBuilder sql = new StringBuilder("Delete From PayOrderIssueHeader");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, poi.getFinID());
		});
	}

}
