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
 * * FileName : MandateStatusDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 25-10-2016 * * Modified
 * Date : 25-10-2016 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 25-10-2016 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.dao.mandate.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;

import com.pennant.backend.dao.mandate.MandateStatusDAO;
import com.pennant.backend.model.mandate.MandateStatus;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pennapps.core.util.DateUtil;

public class MandateStatusDAOImpl extends BasicDao<MandateStatus> implements MandateStatusDAO {

	public MandateStatusDAOImpl() {
		super();
	}

	@Override
	public MandateStatus getMandateStatusById(final long id, String type) {
		MandateStatus mandateStatus = new MandateStatus();
		mandateStatus.setId(id);
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" MandateID, Status, Reason, ChangeDate, FileID");
		sql.append(" From MandatesStatus");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where MandateId = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				MandateStatus ms = new MandateStatus();

				ms.setMandateID(rs.getLong("MandateID"));
				ms.setStatus(rs.getString("Status"));
				ms.setReason(rs.getString("Reason"));
				ms.setChangeDate(JdbcUtil.getDate(rs.getDate("ChangeDate")));
				ms.setFileID(rs.getLong("FileID"));

				return ms;
			}, id);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public void delete(MandateStatus ms, String type) {
		StringBuilder sql = new StringBuilder("Delete From MandatesStatus");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where MandateID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			if (this.jdbcOperations.update(sql.toString(), ms.getMandateID()) <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	@Override
	public long save(MandateStatus ms, String type) {
		ms.setChangeDate(DateUtil.getSysDate());

		StringBuilder sql = new StringBuilder("Insert Into MandatesStatus");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (MandateID, Status, Reason, ChangeDate, FileID)");
		sql.append(" Values(?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, ms.getMandateID());
			ps.setString(index++, ms.getStatus());
			ps.setString(index++, ms.getReason());
			ps.setDate(index++, JdbcUtil.getDate(ms.getChangeDate()));
			ps.setLong(index++, ms.getFileID());
		});

		return ms.getId();
	}

	@Override
	public void update(MandateStatus ms, String type) {
		ms.setChangeDate(DateUtil.getSysDate());
		StringBuilder sql = new StringBuilder("Update MandatesStatus");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set Status = ?, Reason = ?, ChangeDate = ?, FileID = ?");
		sql.append(" Where MandateID = ?");

		if (!type.endsWith("_Temp")) {
			sql.append(" and Version= ?");
		}

		logger.debug(Literal.SQL.concat(sql.toString()));

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, ms.getStatus());
			ps.setString(index++, ms.getReason());
			ps.setDate(index++, JdbcUtil.getDate(ms.getChangeDate()));
			ps.setLong(index++, ms.getFileID());

			if (!type.endsWith("_Temp")) {
				ps.setInt(index++, ms.getVersion() - 1);
			}
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

}