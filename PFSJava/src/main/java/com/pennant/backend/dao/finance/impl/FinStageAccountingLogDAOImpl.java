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
 *********************************************************************************************
 * FILE HEADER *
 *********************************************************************************************
 *
 * FileName : PostingsDAOImpl.java
 * 
 * Author : PENNANT TECHONOLOGIES
 * 
 * Creation Date : 07-02-2012
 * 
 * Modified Date : 07-02-2012
 * 
 * Description :
 * 
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 07-02-2012 PENNANT TECHONOLOGIES 0.1 * * 13-06-2018 Siva 0.2 Stage Accounting Modifications * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.dao.finance.impl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;

import com.pennant.backend.dao.finance.FinStageAccountingLogDAO;
import com.pennant.backend.model.finance.FinStageAccountingLog;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

/**
 * DAO methods implementation for the <b>ReturnDataSet model</b> class.<br>
 */
public class FinStageAccountingLogDAOImpl extends BasicDao<FinStageAccountingLog> implements FinStageAccountingLogDAO {
	private static Logger logger = LogManager.getLogger(FinStageAccountingLogDAOImpl.class);

	public FinStageAccountingLogDAOImpl() {
		super();
	}

	@Override
	public long getLinkedTranId(long finID, String finEvent, String roleCode) {
		String sql = "Select LinkedTranId From FinStageAccountingLog Where FinId = ? and RoleCode = ? and FinEvent = ? and Processed = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql, Long.class, finID, roleCode, finEvent, 0);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return 0;
		}
	}

	@Override
	public List<Long> getLinkedTranIdList(long finID, String finEvent) {
		String sql = "Select LinkedTranId From FinStageAccountingLog Where FinID = ? and FinEvent = ?";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.query(sql, ps -> {
			int index = 1;

			ps.setLong(index++, finID);
			ps.setString(index, finEvent);
		}, (rs, rowNum) -> rs.getLong(1));
	}

	@Override
	public void saveStageAccountingLog(FinStageAccountingLog saLog) {
		String sql = "Insert Into FinStageAccountingLog (FinID, FinReference, FinEvent, RoleCode, LinkedTranId, Processed, ReceiptNo) Values(?, ?, ?, ?, ?, ?, ?)";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setLong(index++, saLog.getFinID());
			ps.setString(index++, saLog.getFinReference());
			ps.setString(index++, saLog.getFinEvent());
			ps.setString(index++, saLog.getRoleCode());
			ps.setLong(index++, saLog.getLinkedTranId());
			ps.setBoolean(index++, saLog.isProcessed());
			ps.setString(index, saLog.getReceiptNo());
		});
	}

	@Override
	public void deleteByRefandRole(long finID, String finEvent, String roleCode) {
		String sql = "Delete From FinStageAccountingLog Where FinID = ? and FinEvent = ? and RoleCode = ? and Processed = ?";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setLong(index++, finID);
			ps.setString(index, finEvent);
			ps.setString(index, roleCode);
			ps.setInt(index, 0);
		});
	}

	@Override
	public void update(long finID, String finEvent, boolean processed) {
		String sql = "Update FinStageAccountingLog Set Processed = ? Where FinID = ? and FinEvent = ? and Processed = ?";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setInt(index++, 1);
			ps.setLong(index++, finID);
			ps.setString(index++, finEvent);
			ps.setBoolean(index, processed);
		});
	}

	@Override
	public int getTranCountByReceiptNo(String receiptNo) {
		String sql = "Select Count(LinkedTranId) From FinStageAccountingLog Where ReceiptNo = ?";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.queryForObject(sql, Integer.class, receiptNo);
	}

	@Override
	public List<Long> getTranIdListByReceipt(String receiptNo) {
		String sql = "Select LinkedTranId From FinStageAccountingLog Where ReceiptNo = ?";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.queryForList(sql, Long.class, receiptNo);
	}

	@Override
	public void deleteByReceiptNo(String receiptNo) {
		String sql = "Delete From FinStageAccountingLog Where ReceiptNo = ?";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setString(index, receiptNo);
		});
	}

}
