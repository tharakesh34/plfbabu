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
 * * FileName : FinAgreementDetailDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 04-02-2012 * *
 * Modified Date : 04-02-2012 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 04-02-2012 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.dao.finance.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;

import com.pennant.backend.dao.finance.FinAgreementDetailDAO;
import com.pennant.backend.model.finance.FinAgreementDetail;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class FinAgreementDetailDAOImpl extends BasicDao<FinAgreementDetail> implements FinAgreementDetailDAO {
	private static Logger logger = LogManager.getLogger(FinAgreementDetailDAOImpl.class);

	public FinAgreementDetailDAOImpl() {
		super();
	}

	@Override
	public FinAgreementDetail getFinAgreementDetailById(long finID, long agrId, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinID, FinReference, AgrId, FinType, AgrName, AgrContent");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", LovDescAgrName");
		}

		sql.append(" From FinAgreementDetail");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ? and AgrId = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, num) -> {
				FinAgreementDetail fag = new FinAgreementDetail();

				fag.setFinID(rs.getLong("FinID"));
				fag.setFinReference(rs.getString("FinReference"));
				fag.setAgrId(rs.getLong("AgrId"));
				fag.setFinType(rs.getString("FinType"));
				fag.setAgrName(rs.getString("AgrName"));
				fag.setAgrContent(rs.getBytes("AgrContent"));
				fag.setVersion(rs.getInt("Version"));
				fag.setLastMntBy(rs.getLong("LastMntBy"));
				fag.setLastMntOn(rs.getTimestamp("LastMntOn"));
				fag.setRecordStatus(rs.getString("RecordStatus"));
				fag.setRoleCode(rs.getString("RoleCode"));
				fag.setNextRoleCode(rs.getString("NextRoleCode"));
				fag.setTaskId(rs.getString("TaskId"));
				fag.setNextTaskId(rs.getString("NextTaskId"));
				fag.setRecordType(rs.getString("RecordType"));
				fag.setWorkflowId(rs.getLong("WorkflowId"));

				if (StringUtils.trimToEmpty(type).contains("View")) {
					fag.setLovDescAgrName(rs.getString("LovDescAgrName"));
				}

				return fag;
			}, finID, agrId);
		} catch (EmptyResultDataAccessException e) {
			//
		}

		return null;
	}

	@Override
	public long save(FinAgreementDetail fag, String type) {
		StringBuilder sql = new StringBuilder("Insert Into FinAgreementDetail");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (FinID, FinReference, AgrId, FinType, AgrName, AgrContent");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, fag.getFinID());
			ps.setString(index++, fag.getFinReference());
			ps.setLong(index++, fag.getAgrId());
			ps.setString(index++, fag.getFinType());
			ps.setString(index++, fag.getAgrName());
			ps.setBytes(index++, fag.getAgrContent());
			ps.setInt(index++, fag.getVersion());
			ps.setLong(index++, fag.getLastMntBy());
			ps.setTimestamp(index++, fag.getLastMntOn());
			ps.setString(index++, fag.getRecordStatus());
			ps.setString(index++, fag.getRoleCode());
			ps.setString(index++, fag.getNextRoleCode());
			ps.setString(index++, fag.getTaskId());
			ps.setString(index++, fag.getNextTaskId());
			ps.setString(index++, fag.getRecordType());
			ps.setLong(index++, fag.getWorkflowId());

		});

		return fag.getAgrId();
	}

	@Override
	public void update(FinAgreementDetail fag, String type) {
		StringBuilder sql = new StringBuilder("Update FinAgreementDetail");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set FinType = ?, AgrName = ?, AgrContent = ?");
		sql.append(", Version = ?, LastMntBy = ?, LastMntOn = ?, RecordStatus = ?");
		sql.append(", RoleCode = ?, NextRoleCode = ?, TaskId = ?");
		sql.append(", NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		sql.append(" Where FinID = ? and AgrId = ?");

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, fag.getFinType());
			ps.setString(index++, fag.getAgrName());
			ps.setBytes(index++, fag.getAgrContent());
			ps.setInt(index++, fag.getVersion());
			ps.setLong(index++, fag.getLastMntBy());
			ps.setTimestamp(index++, fag.getLastMntOn());
			ps.setString(index++, fag.getRecordStatus());
			ps.setString(index++, fag.getRoleCode());
			ps.setString(index++, fag.getNextRoleCode());
			ps.setString(index++, fag.getTaskId());
			ps.setString(index++, fag.getNextTaskId());
			ps.setString(index++, fag.getRecordType());
			ps.setLong(index++, fag.getWorkflowId());
			ps.setLong(index++, fag.getFinID());
			ps.setLong(index++, fag.getAgrId());
		});

		if (recordCount <= 0) {
			logger.warn("Error Update Method Count :" + recordCount);
		}
	}

	@Override
	public void delete(FinAgreementDetail fag, String type) {
		StringBuilder sql = new StringBuilder("Delete From FinAgreementDetail");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ? and AgrId = ?");

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, fag.getFinID());
			ps.setLong(index++, fag.getAgrId());
		});

		if (recordCount <= 0) {
			logger.warn("Error Update Method Count :" + recordCount);
		}
	}

}
