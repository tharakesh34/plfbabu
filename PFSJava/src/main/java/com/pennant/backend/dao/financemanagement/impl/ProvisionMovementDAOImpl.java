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
 * * FileName : ProvisionMovementDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 31-05-2012 * *
 * Modified Date : 31-05-2012 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 31-05-2012 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.dao.financemanagement.impl;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;

import com.pennant.backend.dao.financemanagement.ProvisionMovementDAO;
import com.pennant.backend.model.financemanagement.ProvisionMovement;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

/**
 * DAO methods implementation for the <b>ProvisionMovement model</b> class.<br>
 * 
 */

public class ProvisionMovementDAOImpl extends BasicDao<ProvisionMovement> implements ProvisionMovementDAO {
	private static Logger logger = LogManager.getLogger(ProvisionMovementDAOImpl.class);

	public ProvisionMovementDAOImpl() {
		super();
	}

	@Override
	public ProvisionMovement getProvisionMovementById(long finID, final Date movementDate, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinID, FinReference, ProvMovementDate, ProvMovementSeq, ProvCalDate, ProvisionedAmt");
		sql.append(", ProvisionAmtCal, ProvisionDue, ProvisionPostSts, NonFormulaProv, UseNFProv");
		sql.append(", AutoReleaseNFP, PrincipalDue, ProfitDue, DueFromDate, LastFullyPaidDate");
		sql.append(", LinkedTranId, AssetCode, AssetStageOrdr, Npa, ManualProvision");
		sql.append(", ProvChgLinkedTranId, PrvovisionRate, DueDays, PriBal");
		sql.append(" From FinProvMovements");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ? and ProvMovementDate = ?");
		sql.append(" and ProvMovementSeq = (Select max(ProvMovementSeq) From FinProvMovements");
		sql.append(" Where FinID = ? and ProvMovementDate = ?)");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, num) -> {
				ProvisionMovement pm = new ProvisionMovement();

				pm.setFinID(rs.getLong("FinID"));
				pm.setFinReference(rs.getString("FinReference"));
				pm.setProvMovementDate(rs.getDate("ProvMovementDate"));
				pm.setProvMovementSeq(rs.getInt("ProvMovementSeq"));
				pm.setProvCalDate(rs.getDate("ProvCalDate"));
				pm.setProvisionedAmt(rs.getBigDecimal("ProvisionedAmt"));
				pm.setProvisionAmtCal(rs.getBigDecimal("ProvisionAmtCal"));
				pm.setProvisionDue(rs.getBigDecimal("ProvisionDue"));
				pm.setProvisionPostSts(rs.getString("ProvisionPostSts"));
				pm.setNonFormulaProv(rs.getBigDecimal("NonFormulaProv"));
				pm.setUseNFProv(rs.getBoolean("UseNFProv"));
				pm.setAutoReleaseNFP(rs.getBoolean("AutoReleaseNFP"));
				pm.setPrincipalDue(rs.getBigDecimal("PrincipalDue"));
				pm.setProfitDue(rs.getBigDecimal("ProfitDue"));
				pm.setDueFromDate(rs.getDate("DueFromDate"));
				pm.setLastFullyPaidDate(rs.getDate("LastFullyPaidDate"));
				pm.setLinkedTranId(rs.getLong("LinkedTranId"));
				pm.setAssetCode(rs.getString("AssetCode"));
				pm.setAssetStageOrdr(rs.getInt("AssetStageOrdr"));
				pm.setNpa(rs.getBoolean("NPA"));
				pm.setManualProvision(rs.getBoolean("ManualProvision"));
				pm.setProvChgLinkedTranId(rs.getLong("ProvChgLinkedTranId"));
				pm.setPrvovisionRate(rs.getBigDecimal("PrvovisionRate"));
				pm.setDueDays(rs.getInt("DueDays"));
				pm.setPriBal(rs.getBigDecimal("PriBal"));

				return pm;
			}, finID, movementDate, finID, movementDate);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public void delete(ProvisionMovement pm, String type) {
		StringBuilder sql = new StringBuilder("Delete From FinProvMovements");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index++, pm.getFinID());
			});

			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	@Override
	public String save(ProvisionMovement pm, String type) {
		StringBuilder sql = new StringBuilder("Insert Into FinProvMovements");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (FinID, FinReference, ProvMovementDate, ProvMovementSeq, ProvCalDate, ProvisionedAmt");
		sql.append(", ProvisionAmtCal, ProvisionDue, ProvisionPostSts, NonFormulaProv, UseNFProv");
		sql.append(", AutoReleaseNFP, PrincipalDue, ProfitDue, DueFromDate, LastFullyPaidDate, LinkedTranId");
		sql.append(", AssetCode, AssetStageOrdr, Npa, ManualProvision");
		sql.append(", ProvChgLinkedTranId, PrvovisionRate, DueDays, PriBal)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, pm.getFinID());
			ps.setString(index++, pm.getFinReference());
			ps.setDate(index++, JdbcUtil.getDate(pm.getProvMovementDate()));
			ps.setInt(index++, pm.getProvMovementSeq());
			ps.setDate(index++, JdbcUtil.getDate(pm.getProvCalDate()));
			ps.setBigDecimal(index++, pm.getProvisionedAmt());
			ps.setBigDecimal(index++, pm.getProvisionAmtCal());
			ps.setBigDecimal(index++, pm.getProvisionDue());
			ps.setString(index++, pm.getProvisionPostSts());
			ps.setBigDecimal(index++, pm.getNonFormulaProv());
			ps.setBoolean(index++, pm.isUseNFProv());
			ps.setBoolean(index++, pm.isAutoReleaseNFP());
			ps.setBigDecimal(index++, pm.getPrincipalDue());
			ps.setBigDecimal(index++, pm.getProfitDue());
			ps.setDate(index++, JdbcUtil.getDate(pm.getDueFromDate()));
			ps.setDate(index++, JdbcUtil.getDate(pm.getLastFullyPaidDate()));
			ps.setLong(index++, pm.getLinkedTranId());
			ps.setString(index++, pm.getAssetCode());
			ps.setInt(index++, pm.getAssetStageOrdr());
			ps.setBoolean(index++, pm.isNpa());
			ps.setBoolean(index++, pm.isManualProvision());
			ps.setLong(index++, pm.getProvChgLinkedTranId());
			ps.setBigDecimal(index++, pm.getPrvovisionRate());
			ps.setInt(index++, pm.getDueDays());
			ps.setBigDecimal(index++, pm.getPriBal());
		});

		return pm.getId();
	}

	@Override
	public void update(ProvisionMovement pm, String type) {
		StringBuilder sql = new StringBuilder("Update FinProvMovements");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set ProvisionedAmt = ?, ProvisionDue = ?, ProvisionPostSts = ?");
		sql.append(", LinkedTranId = ?, AssetCode = ?, AssetStageOrdr = ?, NPA = ?, ManualProvision = ?");
		sql.append(", ProvChgLinkedTranId = ?, PrvovisionRate = ?, DueDays = ?, PriBal = ?");
		sql.append(" Where FinID = ? and ProvMovementDate = ? and ProvMovementSeq = ?");

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setBigDecimal(index++, pm.getProvisionedAmt());
			ps.setBigDecimal(index++, pm.getProvisionDue());
			ps.setString(index++, pm.getProvisionPostSts());
			ps.setLong(index++, pm.getLinkedTranId());
			ps.setString(index++, pm.getAssetCode());
			ps.setInt(index++, pm.getAssetStageOrdr());
			ps.setBoolean(index++, pm.isNpa());
			ps.setBoolean(index++, pm.isManualProvision());
			ps.setLong(index++, pm.getProvChgLinkedTranId());
			ps.setBigDecimal(index++, pm.getPrvovisionRate());
			ps.setInt(index++, pm.getDueDays());
			ps.setBigDecimal(index++, pm.getPriBal());

			ps.setLong(index++, pm.getFinID());
			ps.setDate(index++, JdbcUtil.getDate(pm.getProvMovementDate()));
			ps.setInt(index++, pm.getProvMovementSeq());
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}
}