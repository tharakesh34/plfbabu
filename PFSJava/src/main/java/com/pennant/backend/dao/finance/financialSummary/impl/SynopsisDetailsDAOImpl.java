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
 * * FileName : PSLDetailDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 20-06-2018 * * Modified
 * Date : 20-06-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 20-06-2018 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.finance.financialSummary.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;

import com.pennant.backend.dao.finance.financialSummary.SynopsisDetailsDAO;
import com.pennant.backend.model.finance.financialsummary.SynopsisDetails;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Data access layer implementation for <code>PSLDetail</code> with set of CRUD operations.
 */
public class SynopsisDetailsDAOImpl extends SequenceDao<SynopsisDetails> implements SynopsisDetailsDAO {
	private static Logger logger = LogManager.getLogger(SynopsisDetailsDAOImpl.class);

	public SynopsisDetailsDAOImpl() {
		super();
	}

	@Override
	public SynopsisDetails getSynopsisDetails(long finID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" t1.Id, t2.FinID, t2.FinReference, t1.CustomerBackground, t1.DetailedBusinessProfile");
		sql.append(", t1.DetailsofGroupCompaniesIfAny, t1.PdDetails, t1.MajorProduct, t1.OtherRemarks");
		sql.append(", t1.CmtOnCollateralDtls, t1.EndUse, t1.Version, t1.LastMntBy, t1.LastMntOn");
		sql.append(", t1.RecordStatus, t1.RoleCode, t1.NextRoleCode, t1.TaskId");
		sql.append(", t1.NextTaskId, t1.RecordType, t1.WorkflowId");
		sql.append(" From SynopsisDetails_Temp t1");
		sql.append(" Left Join FinanceMain t2 on t2.FinID = t1.FinID");
		sql.append(" Where T2.FinID = ?");
		sql.append(" Union All");
		sql.append(" Select t1.Id, t2.FinID, t2.FinReference, t1.CustomerBackground, t1.DetailedBusinessProfile");
		sql.append(", t1.DetailsofGroupCompaniesIfAny, t1.PdDetails, t1.MajorProduct, t1.OtherRemarks");
		sql.append(", t1.CmtOnCollateralDtls, t1.EndUse, t1.Version, t1.LastMntBy, t1.LastMntOn");
		sql.append(", t1.RecordStatus, t1.RoleCode, t1.NextRoleCode, t1.TaskId");
		sql.append(", t1.NextTaskId, t1.RecordType, t1.WorkflowId");
		sql.append(" From SynopsisDetails t1");
		sql.append(" Left Join FinanceMain t2 on t2.FinID =  t1.FinID");
		sql.append(" Where not exists (Select 1 From SynopsisDetails_Temp Where Id = T1.Id)");
		sql.append(" and T2.FinID = ?");

		logger.debug(Literal.SQL + sql);

		try {
			return jdbcOperations.queryForObject(sql.toString(), (rs, i) -> {
				SynopsisDetails sd = new SynopsisDetails();

				sd.setId(rs.getLong("Id"));
				sd.setFinID(JdbcUtil.getLong(rs.getLong("FinID")));
				sd.setFinReference(rs.getString("FinReference"));
				sd.setCustomerBackGround(rs.getBytes("CustomerBackground"));
				sd.setDetailedBusinessProfile(rs.getBytes("DetailedBusinessProfile"));
				sd.setDetailsofGroupCompaniesIfAny(rs.getBytes("DetailsofGroupCompaniesIfAny"));
				sd.setPdDetails(rs.getBytes("PdDetails"));
				sd.setMajorProduct(rs.getBytes("MajorProduct"));
				sd.setOtherRemarks(rs.getBytes("OtherRemarks"));
				sd.setCmtOnCollateralDtls(rs.getBytes("CmtOnCollateralDtls"));
				sd.setEndUse(rs.getBytes("EndUse"));
				sd.setVersion(rs.getInt("Version"));
				sd.setLastMntBy(rs.getLong("LastMntBy"));
				sd.setLastMntOn(rs.getTimestamp("LastMntOn"));
				sd.setRecordStatus(rs.getString("RecordStatus"));
				sd.setRoleCode(rs.getString("RoleCode"));
				sd.setNextRoleCode(rs.getString("NextRoleCode"));
				sd.setTaskId(rs.getString("TaskId"));
				sd.setNextTaskId(rs.getString("NextTaskId"));
				sd.setRecordType(rs.getString("RecordType"));
				sd.setWorkflowId(rs.getLong("WorkflowId"));

				return sd;
			}, finID, finID);
		} catch (EmptyResultDataAccessException e) {
			//
		}

		return null;
	}

	@Override
	public String save(SynopsisDetails sd, TableType tableType) {
		if (sd.getId() == Long.MIN_VALUE) {
			sd.setId(getNextValue("SeqSynopsisDetails"));
		}

		StringBuilder sql = new StringBuilder("Insert Into SynopsisDetails");
		sql.append(tableType.getSuffix());
		sql.append(" (Id, FinID, FinReference, CustomerBackGround, DetailedBusinessProfile");
		sql.append(", DetailsofGroupCompaniesIfAny, PdDetails, MajorProduct, OtherRemarks");
		sql.append(", CmtOnCollateralDtls, EndUse, Version, LastMntBy, LastMntOn, RecordStatus");
		sql.append(", RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values(");
		sql.append(" ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		try {
			jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index++, sd.getId());
				ps.setLong(index++, sd.getFinID());
				ps.setString(index++, sd.getFinReference());
				ps.setBytes(index++, sd.getCustomerBackGround());
				ps.setBytes(index++, sd.getDetailedBusinessProfile());
				ps.setBytes(index++, sd.getDetailsofGroupCompaniesIfAny());
				ps.setBytes(index++, sd.getPdDetails());
				ps.setBytes(index++, sd.getMajorProduct());
				ps.setBytes(index++, sd.getOtherRemarks());
				ps.setBytes(index++, sd.getCmtOnCollateralDtls());
				ps.setBytes(index++, sd.getEndUse());
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
			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		return String.valueOf(sd.getFinReference());
	}

	@Override
	public void update(SynopsisDetails sd, TableType tableType) {
		StringBuilder sql = new StringBuilder("Update SynopsisDetails");
		sql.append(tableType.getSuffix());
		sql.append(" Set CustomerBackGround = ?, DetailedBusinessProfile = ?, DetailsofGroupCompaniesIfAny = ?");
		sql.append(", PdDetails = ?, MajorProduct = ?, OtherRemarks = ?, CmtOnCollateralDtls = ?, EndUse = ?");
		sql.append(", LastMntOn = ?, RecordStatus = ?, RoleCode = ?, NextRoleCode = ?, TaskId = ?");
		sql.append(", NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setBytes(index++, sd.getCustomerBackGround());
			ps.setBytes(index++, sd.getDetailedBusinessProfile());
			ps.setBytes(index++, sd.getDetailsofGroupCompaniesIfAny());
			ps.setBytes(index++, sd.getPdDetails());
			ps.setBytes(index++, sd.getMajorProduct());
			ps.setBytes(index++, sd.getOtherRemarks());
			ps.setBytes(index++, sd.getCmtOnCollateralDtls());
			ps.setBytes(index++, sd.getEndUse());
			ps.setTimestamp(index++, sd.getLastMntOn());
			ps.setString(index++, sd.getRecordStatus());
			ps.setString(index++, sd.getRoleCode());
			ps.setString(index++, sd.getNextRoleCode());
			ps.setString(index++, sd.getTaskId());
			ps.setString(index++, sd.getNextTaskId());
			ps.setString(index++, sd.getRecordType());
			ps.setLong(index++, sd.getWorkflowId());

			ps.setLong(index++, sd.getFinID());
		});

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public void delete(SynopsisDetails sd, TableType tableType) {
		StringBuilder sql = new StringBuilder("Delete From SynopsisDetails");
		sql.append(tableType.getSuffix());
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			int recordCount = jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index++, sd.getFinID());
			});

			if (recordCount == 0) {
				throw new ConcurrencyException();
			}

		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

}
