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
 * * FileName : FinAdvancePaymentsDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 14-08-2013 * *
 * Modified Date : 14-08-2013 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 14-08-2013 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.dao.finance.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;

import com.pennant.backend.dao.finance.AdvancePaymentDetailDAO;
import com.pennant.backend.model.finance.AdvancePaymentDetail;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * DAO methods implementation for the <b>FinAdvancePayments model</b> class.<br>
 * 
 */
public class AdvancePaymentDetailDAOImpl extends BasicDao<AdvancePaymentDetail> implements AdvancePaymentDetailDAO {
	private static Logger logger = LogManager.getLogger(AdvancePaymentDetailDAOImpl.class);

	public AdvancePaymentDetailDAOImpl() {
		super();
	}

	/**
	 * Method for Fetching Advance Payment Detail Balance By Reference
	 */
	@Override
	public AdvancePaymentDetail getAdvancePaymentDetailBalByRef(long finID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinID, FinReference, SUM(AdvInt) AdvInt, SUM(AdvIntTds) AdvIntTds");
		sql.append(", SUM(AdvEMI) AdvEMI, SUM(AdvEMITds) AdvEMITds");
		sql.append(" From AdvancePaymentDetails");
		sql.append(" Where FinID = ? Group by FinID, FinReference");

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, i) -> {
				AdvancePaymentDetail apd = new AdvancePaymentDetail();

				apd.setFinID(rs.getLong("FinID"));
				apd.setFinReference(rs.getString("FinReference"));
				apd.setAdvInt(rs.getBigDecimal("AdvInt"));
				apd.setAdvIntTds(rs.getBigDecimal("AdvIntTds"));
				apd.setAdvEMI(rs.getBigDecimal("AdvEMI"));
				apd.setAdvEMITds(rs.getBigDecimal("AdvEMITds"));

				return apd;
			}, finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Record is not found in AdvancePaymentDetails table for the specified FinID >> {}", finID);
		}
		return null;
	}

	@Override
	public void save(AdvancePaymentDetail apd) {
		StringBuilder sql = new StringBuilder();
		sql.append("Insert Into AdvancePaymentDetails");
		sql.append("(FinID, FinReference, InstructionUID , AdvInt, AdvIntTds, AdvEMI, AdvEMITds)");
		sql.append("Values(?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index++, apd.getFinID());
			ps.setString(index++, apd.getFinReference());
			ps.setLong(index++, apd.getInstructionUID());
			ps.setBigDecimal(index++, apd.getAdvInt());
			ps.setBigDecimal(index++, apd.getAdvIntTds());
			ps.setBigDecimal(index++, apd.getAdvEMI());
			ps.setBigDecimal(index++, apd.getAdvEMITds());
		});

	}

}