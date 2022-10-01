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
package com.pennant.backend.dao.psl.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;

import com.pennant.backend.dao.psl.PSLDetailDAO;
import com.pennant.backend.model.finance.psl.PSLDetail;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.core.TableType;

/**
 * Data access layer implementation for <code>PSLDetail</code> with set of CRUD operations.
 */
public class PSLDetailDAOImpl extends BasicDao<PSLDetail> implements PSLDetailDAO {
	private static Logger logger = LogManager.getLogger(PSLDetailDAOImpl.class);

	public PSLDetailDAOImpl() {
		super();
	}

	@Override
	public PSLDetail getPSLDetail(long finID, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinID, FinReference, CategoryCode, WeakerSection, LandHolding, LandArea, Sector, Amount");
		sql.append(", SubCategory, Purpose, EndUse, LoanPurpose, EligibleAmount, Version, LastMntOn");
		sql.append(", LastMntBy, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", WeakerSectionName, PurposeName, EndUseName");
		}

		sql.append(" From PSLDetail");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ?");

		logger.trace(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				PSLDetail psl = new PSLDetail();

				psl.setFinID(rs.getLong("FinID"));
				psl.setFinReference(rs.getString("FinReference"));
				psl.setCategoryCode(rs.getString("CategoryCode"));
				psl.setWeakerSection(rs.getString("WeakerSection"));
				psl.setLandHolding(rs.getString("LandHolding"));
				psl.setLandArea(rs.getString("LandArea"));
				psl.setSector(rs.getString("Sector"));
				psl.setAmount(rs.getDouble("Amount"));
				psl.setSubCategory(rs.getString("SubCategory"));
				psl.setPurpose(rs.getString("Purpose"));
				psl.setEndUse(rs.getString("EndUse"));
				psl.setLoanPurpose(rs.getString("LoanPurpose"));
				psl.setEligibleAmount(rs.getBigDecimal("EligibleAmount"));
				psl.setVersion(rs.getInt("Version"));
				psl.setLastMntOn(rs.getTimestamp("LastMntOn"));
				psl.setLastMntBy(rs.getLong("LastMntBy"));
				psl.setRecordStatus(rs.getString("RecordStatus"));
				psl.setRoleCode(rs.getString("RoleCode"));
				psl.setNextRoleCode(rs.getString("NextRoleCode"));
				psl.setTaskId(rs.getString("TaskId"));
				psl.setNextTaskId(rs.getString("NextTaskId"));
				psl.setRecordType(rs.getString("RecordType"));
				psl.setWorkflowId(rs.getLong("WorkflowId"));

				if (StringUtils.trimToEmpty(type).contains("View")) {
					psl.setWeakerSectionName(rs.getString("WeakerSectionName"));
					psl.setPurposeName(rs.getString("PurposeName"));
					psl.setEndUseName(rs.getString("EndUseName"));
				}

				return psl;
			}, finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public String save(PSLDetail psld, TableType tableType) {
		StringBuilder sql = new StringBuilder("Insert Into PSLDetail");
		sql.append(tableType.getSuffix());
		sql.append("(FinID, FinReference, CategoryCode, WeakerSection, LandHolding, LandArea, Sector");
		sql.append(", Amount, SubCategory, Purpose, EndUse, LoanPurpose, EligibleAmount, Version, LastMntBy");
		sql.append(", LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values(");
		sql.append(" ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		try {
			jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index++, psld.getFinID());
				ps.setString(index++, psld.getFinReference());
				ps.setString(index++, psld.getCategoryCode());
				ps.setString(index++, psld.getWeakerSection());
				ps.setString(index++, psld.getLandHolding());
				ps.setString(index++, psld.getLandArea());
				ps.setString(index++, psld.getSector());
				ps.setDouble(index++, psld.getAmount());
				ps.setString(index++, psld.getSubCategory());
				ps.setString(index++, psld.getPurpose());
				ps.setString(index++, psld.getEndUse());
				ps.setString(index++, psld.getLoanPurpose());
				ps.setBigDecimal(index++, psld.getEligibleAmount());
				ps.setInt(index++, psld.getVersion());
				ps.setLong(index++, psld.getLastMntBy());
				ps.setTimestamp(index++, psld.getLastMntOn());
				ps.setString(index++, psld.getRecordStatus());
				ps.setString(index++, psld.getRoleCode());
				ps.setString(index++, psld.getNextRoleCode());
				ps.setString(index++, psld.getTaskId());
				ps.setString(index++, psld.getNextTaskId());
				ps.setString(index++, psld.getRecordType());
				ps.setLong(index, psld.getWorkflowId());
			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		return String.valueOf(psld.getFinReference());
	}

	@Override
	public void update(PSLDetail psld, TableType tableType) {
		StringBuilder sql = new StringBuilder("Update PSLDetail");
		sql.append(tableType.getSuffix());
		sql.append(" Set CategoryCode = ?, WeakerSection = ?, LandHolding = ?, LandArea = ?, Sector = ?");
		sql.append(", Amount = ?, SubCategory = ?, Purpose = ?, EndUse = ? ");
		sql.append(", LastMntOn = ?, RecordStatus = ?, RoleCode = ?, NextRoleCode = ?, TaskId = ?");
		sql.append(", NextTaskId = ?, RecordType = ?, WorkflowId = ?, LoanPurpose = ?, EligibleAmount = ?");
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, psld.getCategoryCode());
			ps.setString(index++, psld.getWeakerSection());
			ps.setString(index++, psld.getLandHolding());
			ps.setString(index++, psld.getLandArea());
			ps.setString(index++, psld.getSector());
			ps.setDouble(index++, psld.getAmount());
			ps.setString(index++, psld.getSubCategory());
			ps.setString(index++, psld.getPurpose());
			ps.setString(index++, psld.getEndUse());
			ps.setTimestamp(index++, psld.getLastMntOn());
			ps.setString(index++, psld.getRecordStatus());
			ps.setString(index++, psld.getRoleCode());
			ps.setString(index++, psld.getNextRoleCode());
			ps.setString(index++, psld.getTaskId());
			ps.setString(index++, psld.getNextTaskId());
			ps.setString(index++, psld.getRecordType());
			ps.setLong(index++, psld.getWorkflowId());
			ps.setString(index++, psld.getLoanPurpose());
			ps.setBigDecimal(index++, psld.getEligibleAmount());

			ps.setLong(index, psld.getFinID());
		});

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public void delete(PSLDetail psld, TableType tableType) {
		StringBuilder sql = new StringBuilder("Delete From PSLDetail");
		sql.append(tableType.getSuffix());
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			int recordCount = jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index, psld.getFinID());
			});

			if (recordCount == 0) {
				throw new ConcurrencyException();
			}

		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	@Override
	public String getPslCategoryCodes(String pslcategory) {
		String sql = "Select Code From PslCategory Where Code = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql, String.class, pslcategory);
		} catch (EmptyResultDataAccessException dae) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public int getWeakerSection(String weakerSectionCode) {
		String sql = "Select Count(Code) From PSLWeakerSection Where Code = ?";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.queryForObject(sql, Integer.class, weakerSectionCode);
	}

	@Override
	public int getEndUseCode(String endCode, String purposeCode) {
		String sql = "Select Count(Code) From PslEndUse Where code = ? and PurposeCode = ?";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.queryForObject(sql, Integer.class, endCode, purposeCode);
	}

	@Override
	public int getPurposeCount(String code, String categoryCode) {
		String sql = "Select Count(Code) From PslPurpose Where Code = ? and CategoryCode = ?";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.queryForObject(sql, Integer.class, code, categoryCode);
	}
}
