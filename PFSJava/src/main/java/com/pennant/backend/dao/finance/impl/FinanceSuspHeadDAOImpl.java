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
 * * FileName : FinanceSuspHeadDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 04-02-2012 * *
 * Modified Date : 04-02-2012 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 04-02-2012 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.dao.finance.impl;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;

import com.pennant.backend.dao.finance.FinanceSuspHeadDAO;
import com.pennant.backend.model.finance.FinStatusDetail;
import com.pennant.backend.model.finance.FinanceSuspDetails;
import com.pennant.backend.model.finance.FinanceSuspHead;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.resource.Literal;

public class FinanceSuspHeadDAOImpl extends BasicDao<FinanceSuspHead> implements FinanceSuspHeadDAO {
	private static Logger logger = LogManager.getLogger(FinanceSuspHeadDAOImpl.class);

	public FinanceSuspHeadDAOImpl() {
		super();
	}

	@Override
	public FinanceSuspHead getFinanceSuspHead() {
		return new FinanceSuspHead();
	}

	@Override
	public FinanceSuspHead getNewFinanceSuspHead() {
		return getFinanceSuspHead();
	}

	@Override
	public FinanceSuspHead getFinanceSuspHeadById(long finID, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinID, FinReference, FinBranch, FinType, CustId, FinSuspSeq");
		sql.append(", FinIsInSusp, ManualSusp, FinSuspDate, FinSuspTrfDate, FinSuspAmt, FinCurSuspAmt");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", FinCcy, LovDescCustCIFName, LovDescCustShrtName, LovDescFinDivision");
		}

		sql.append(", Version, LastMntOn, LastMntBy,RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From FinSuspHead");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				FinanceSuspHead fsh = new FinanceSuspHead();

				fsh.setFinID(rs.getLong("FinID"));
				fsh.setFinReference(rs.getString("FinReference"));
				fsh.setFinBranch(rs.getString("FinReference"));
				fsh.setFinType(rs.getString("FinType"));
				fsh.setCustId(rs.getLong("CustId"));
				fsh.setFinSuspSeq(rs.getInt("FinSuspSeq"));
				fsh.setFinIsInSusp(rs.getBoolean("FinIsInSusp"));
				fsh.setManualSusp(rs.getBoolean("ManualSusp"));
				fsh.setFinSuspDate(JdbcUtil.getDate(rs.getDate("FinSuspDate")));
				fsh.setFinSuspTrfDate(JdbcUtil.getDate(rs.getDate("FinSuspTrfDate")));
				fsh.setFinSuspAmt(rs.getBigDecimal("FinSuspAmt"));
				fsh.setFinCurSuspAmt(rs.getBigDecimal("FinCurSuspAmt"));

				if (StringUtils.trimToEmpty(type).contains("View")) {
					fsh.setFinCcy(rs.getString("FinCcy"));
					fsh.setLovDescCustCIFName(rs.getString("LovDescCustCIFName"));
					fsh.setLovDescCustShrtName(rs.getString("LovDescCustShrtName"));
					fsh.setLovDescFinDivision(rs.getString("LovDescFinDivision"));
				}

				fsh.setVersion(rs.getInt("Version"));
				fsh.setLastMntBy(rs.getLong("LastMntBy"));
				fsh.setLastMntOn(rs.getTimestamp("LastMntOn"));
				fsh.setRecordStatus(rs.getString("RecordStatus"));
				fsh.setRoleCode(rs.getString("RoleCode"));
				fsh.setNextRoleCode(rs.getString("NextRoleCode"));
				fsh.setTaskId(rs.getString("TaskId"));
				fsh.setNextTaskId(rs.getString("NextTaskId"));
				fsh.setRecordType(rs.getString("RecordType"));
				fsh.setWorkflowId(rs.getLong("WorkflowId"));

				return fsh;
			}, finID);
		} catch (EmptyResultDataAccessException e) {
			//
		}

		return null;
	}

	@Override
	public Date getFinSuspDate(long finID) {
		String sql = "Select FinSuspDate From FinSuspHead Where FinID = ? and FinIsInSusp = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return jdbcOperations.queryForObject(sql, Date.class, finID, 1);
		} catch (EmptyResultDataAccessException e) {
			//
		}

		return null;
	}

	@Override
	public List<String> getSuspFinanceList() {
		String sql = "Select FinReference From FinSuspHead Where FinIsInSusp = ?";

		logger.debug(Literal.SQL + sql);

		return jdbcOperations.queryForList(sql, String.class, 1);
	}

	@Override
	public long save(FinanceSuspHead fsh, String type) {
		StringBuilder sql = new StringBuilder("Insert Into FinSuspHead");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (FinID, FinReference, FinBranch, FinType, CustId, FinSuspSeq, FinIsInSusp");
		sql.append(", ManualSusp, FinSuspDate, FinSuspTrfDate, FinSuspAmt, FinCurSuspAmt");
		sql.append(", Version, LastMntOn, LastMntBy, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, fsh.getFinID());
			ps.setString(index++, fsh.getFinReference());
			ps.setString(index++, fsh.getFinBranch());
			ps.setString(index++, fsh.getFinType());
			ps.setLong(index++, fsh.getCustId());
			ps.setInt(index++, fsh.getFinSuspSeq());
			ps.setBoolean(index++, fsh.isFinIsInSusp());
			ps.setBoolean(index++, fsh.isManualSusp());
			ps.setDate(index++, JdbcUtil.getDate(fsh.getFinSuspDate()));
			ps.setDate(index++, JdbcUtil.getDate(fsh.getFinSuspTrfDate()));
			ps.setBigDecimal(index++, fsh.getFinSuspAmt());
			ps.setBigDecimal(index++, fsh.getFinCurSuspAmt());
			ps.setInt(index++, fsh.getVersion());
			ps.setTimestamp(index++, fsh.getLastMntOn());
			ps.setLong(index++, fsh.getLastMntBy());
			ps.setString(index++, fsh.getRecordStatus());
			ps.setString(index++, fsh.getRoleCode());
			ps.setString(index++, fsh.getNextRoleCode());
			ps.setString(index++, fsh.getTaskId());
			ps.setString(index++, fsh.getNextTaskId());
			ps.setString(index++, fsh.getRecordType());
			ps.setLong(index++, fsh.getWorkflowId());
		});

		return fsh.getFinID();
	}

	@Override
	public void update(FinanceSuspHead fsh, String type) {
		StringBuilder sql = new StringBuilder("Update FinSuspHead");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set FinBranch = ?, FinType = ?");
		sql.append(", CustId = ?, FinSuspSeq = ?, FinIsInSusp = ?, ManualSusp = ?");
		sql.append(", FinSuspDate = ?, FinSuspTrfDate = ?, FinSuspAmt = ?, FinCurSuspAmt = ?");
		sql.append(", Version = ? , LastMntOn = ?, LastMntBy = ?, RecordStatus = ?, RoleCode = ?");
		sql.append(", NextRoleCode = ?, TaskId = ?, NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, fsh.getFinBranch());
			ps.setString(index++, fsh.getFinType());
			ps.setLong(index++, fsh.getCustId());
			ps.setInt(index++, fsh.getFinSuspSeq());
			ps.setBoolean(index++, fsh.isFinIsInSusp());
			ps.setBoolean(index++, fsh.isManualSusp());
			ps.setDate(index++, JdbcUtil.getDate(fsh.getFinSuspDate()));
			ps.setDate(index++, JdbcUtil.getDate(fsh.getFinSuspTrfDate()));
			ps.setBigDecimal(index++, fsh.getFinSuspAmt());
			ps.setBigDecimal(index++, fsh.getFinCurSuspAmt());
			ps.setInt(index++, fsh.getVersion());
			ps.setTimestamp(index++, fsh.getLastMntOn());
			ps.setLong(index++, fsh.getLastMntBy());
			ps.setString(index++, fsh.getRecordStatus());
			ps.setString(index++, fsh.getRoleCode());
			ps.setString(index++, fsh.getNextRoleCode());
			ps.setString(index++, fsh.getTaskId());
			ps.setString(index++, fsh.getNextTaskId());
			ps.setString(index++, fsh.getRecordType());
			ps.setLong(index++, fsh.getWorkflowId());

			ps.setLong(index++, fsh.getFinID());
		});

		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :" + recordCount);
		}
	}

	@Override
	public void delete(FinanceSuspHead fsh, String type) {
		StringBuilder sql = new StringBuilder("Delete From FinSuspHead");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index++, fsh.getFinID());
			});

			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	@Override
	public long saveSuspenseDetails(FinanceSuspDetails fsd, String type) {
		StringBuilder sql = new StringBuilder("Insert Into FinSuspDetail");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (FinID, FinReference, FinBranch, FinType, CustId, FinSuspSeq, FinTrfDate");
		sql.append(", FinTrfMvt, FinTrfAmt, FinODDate, FinTrfFromDate, LinkedTranId)");
		sql.append(" Values( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, fsd.getFinID());
			ps.setString(index++, fsd.getFinReference());
			ps.setString(index++, fsd.getFinBranch());
			ps.setString(index++, fsd.getFinType());
			ps.setLong(index++, fsd.getCustId());
			ps.setInt(index++, fsd.getFinSuspSeq());
			ps.setDate(index++, JdbcUtil.getDate(fsd.getFinTrfDate()));
			ps.setString(index++, fsd.getFinTrfMvt());
			ps.setBigDecimal(index++, fsd.getFinTrfAmt());
			ps.setDate(index++, JdbcUtil.getDate(fsd.getFinODDate()));
			ps.setDate(index++, JdbcUtil.getDate(fsd.getFinTrfFromDate()));
			ps.setLong(index++, fsd.getLinkedTranId());
		});

		return fsd.getFinID();
	}

	@Override
	public List<FinanceSuspDetails> getFinanceSuspDetailsListById(long finID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinID, FinReference, FinBranch, FinType, CustId, FinSuspSeq");
		sql.append(", FinTrfDate, FinTrfMvt, FinTrfAmt, FinODDate, FinTrfFromDate, LinkedTranId");
		sql.append(" From FinSuspDetail");
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> ps.setLong(1, finID), (rs, rowNum) -> {
			FinanceSuspDetails fsd = new FinanceSuspDetails();

			fsd.setFinID(rs.getLong("FinID"));
			fsd.setFinReference(rs.getString("FinReference"));
			fsd.setFinBranch(rs.getString("FinBranch"));
			fsd.setFinType(rs.getString("FinType"));
			fsd.setCustId(rs.getLong("CustId"));
			fsd.setFinSuspSeq(rs.getInt("FinSuspSeq"));
			fsd.setFinTrfDate(JdbcUtil.getDate(rs.getDate("FinTrfDate")));
			fsd.setFinTrfMvt(rs.getString("FinTrfMvt"));
			fsd.setFinTrfAmt(rs.getBigDecimal("FinTrfAmt"));
			fsd.setFinODDate(JdbcUtil.getDate(rs.getDate("FinODDate")));
			fsd.setFinTrfFromDate(JdbcUtil.getDate(rs.getDate("FinTrfFromDate")));
			fsd.setLinkedTranId(rs.getLong("LinkedTranId"));

			return fsd;
		});
	}

	@Override
	public List<FinStatusDetail> getCustSuspDate(List<Long> custIdList) {
		StringBuilder sql = new StringBuilder("Select CustId, min(FinSuspTrfDate) ValueDate");
		sql.append(" From FinSuspHead");
		sql.append(" Where CustId IN(");
		sql.append(custIdList.stream().map(e -> "? ").collect(Collectors.joining(",")));
		sql.append(") and FinIsInSusp = ? Group By CustId");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			for (Long id : custIdList) {
				ps.setLong(index++, id);
			}

			ps.setInt(index++, 1);
		}, (rs, rowNum) -> {
			FinStatusDetail fsd = new FinStatusDetail();

			fsd.setCustId(rs.getLong("CustId"));
			fsd.setValueDate(JdbcUtil.getDate(rs.getDate("ValueDate")));

			return fsd;
		});
	}

	@Override
	public void updateSuspFlag(long finID) {
		String sql = "Update FinSuspHead Set FinIsInSusp = 0  Where FinID = ?";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, finID);
	}

}
