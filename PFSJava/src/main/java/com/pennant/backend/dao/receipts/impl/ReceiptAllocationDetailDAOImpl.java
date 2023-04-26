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
 * * FileName : FinanceRepaymentsDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 05-05-2011 * *
 * Modified Date : 05-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 05-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.receipts.impl;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennant.backend.dao.receipts.ReceiptAllocationDetailDAO;
import com.pennant.backend.model.finance.ReceiptAllocationDetail;
import com.pennant.backend.util.RepayConstants;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.receipt.constants.Allocation;

/**
 * DAO methods implementation for the <b>Finance Repayments</b> class.<br>
 * 
 */
public class ReceiptAllocationDetailDAOImpl extends SequenceDao<ReceiptAllocationDetail>
		implements ReceiptAllocationDetailDAO {
	private static Logger logger = LogManager.getLogger(ReceiptAllocationDetailDAOImpl.class);

	public ReceiptAllocationDetailDAOImpl() {
		super();
	}

	@Override
	public List<ReceiptAllocationDetail> getAllocationsByReceiptID(long receiptID, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ReceiptAllocationid, ReceiptID, AllocationID, AllocationType, AllocationTo");
		sql.append(", PaidAmount, WaivedAmount, WaiverAccepted, PaidGST, TotalDue, WaivedGST, TaxHeaderId");
		sql.append(", TdsDue, TdsPaid, TdsWaived");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", TypeDesc");
		}

		sql.append(" from ReceiptAllocationDetail");
		sql.append(StringUtils.trim(type));
		sql.append(" Where ReceiptID = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index, receiptID);
		}, (rs, rowNum) -> {
			ReceiptAllocationDetail rad = new ReceiptAllocationDetail();

			rad.setReceiptAllocationid(rs.getLong("ReceiptAllocationid"));
			rad.setReceiptID(rs.getLong("ReceiptID"));
			rad.setAllocationID(rs.getInt("AllocationID"));
			rad.setAllocationType(rs.getString("AllocationType"));
			rad.setAllocationTo(rs.getLong("AllocationTo"));
			rad.setPaidAmount(rs.getBigDecimal("PaidAmount"));
			rad.setWaivedAmount(rs.getBigDecimal("WaivedAmount"));
			rad.setWaiverAccepted(rs.getString("WaiverAccepted"));
			rad.setPaidGST(rs.getBigDecimal("PaidGST"));
			rad.setTotalDue(rs.getBigDecimal("TotalDue"));
			rad.setWaivedGST(rs.getBigDecimal("WaivedGST"));
			rad.setTaxHeaderId(JdbcUtil.getLong(rs.getObject("TaxHeaderId")));
			rad.setTdsDue(rs.getBigDecimal("TdsDue"));
			rad.setTdsPaid(rs.getBigDecimal("TdsPaid"));
			rad.setTdsWaived(rs.getBigDecimal("TdsWaived"));

			if (StringUtils.trimToEmpty(type).contains("View")) {
				rad.setTypeDesc(rs.getString("TypeDesc"));
			}

			return rad;
		});
	}

	@Override
	public void deleteByReceiptID(long receiptID, TableType tableType) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ReceiptID", receiptID);

		StringBuilder deleteSql = new StringBuilder(" DELETE From ReceiptAllocationDetail");
		deleteSql.append(tableType.getSuffix());
		deleteSql.append(" where ReceiptID=:ReceiptID ");

		logger.debug("selectSql: " + deleteSql.toString());
		this.jdbcTemplate.update(deleteSql.toString(), source);
		logger.debug("Leaving");
	}

	@Override
	public void saveAllocations(List<ReceiptAllocationDetail> allocations, TableType tableType) {
		for (ReceiptAllocationDetail allocation : allocations) {
			if (allocation.getReceiptAllocationid() == Long.MIN_VALUE) {
				allocation.setReceiptAllocationid(getNextValue("SeqReceiptAllocationDetail"));
			}
		}

		StringBuilder sql = new StringBuilder();
		sql.append("Insert Into ReceiptAllocationDetail");
		sql.append(tableType.getSuffix());
		sql.append(" (ReceiptAllocationid, ReceiptID, AllocationID, AllocationType, AllocationTo");
		sql.append(", PaidAmount, WaivedAmount, WaiverAccepted, PaidGST, TotalDue");
		sql.append(", WaivedGST, TaxHeaderId, TdsDue, TdsPaid, TdsWaived");
		sql.append(") values(");
		sql.append("?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(")");

		logger.trace(Literal.SQL + sql.toString());

		this.jdbcTemplate.getJdbcOperations().batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ReceiptAllocationDetail rAD = allocations.get(i);

				int index = 1;

				ps.setLong(index++, rAD.getReceiptAllocationid());
				ps.setLong(index++, rAD.getReceiptID());
				ps.setInt(index++, rAD.getAllocationID());
				ps.setString(index++, rAD.getAllocationType());
				ps.setLong(index++, rAD.getAllocationTo());
				ps.setBigDecimal(index++, rAD.getPaidAmount());
				ps.setBigDecimal(index++, rAD.getWaivedAmount());
				ps.setString(index++, rAD.getWaiverAccepted());
				ps.setBigDecimal(index++, rAD.getPaidGST());
				ps.setBigDecimal(index++, rAD.getTotalDue());
				ps.setBigDecimal(index++, rAD.getWaivedGST());
				ps.setObject(index++, rAD.getTaxHeaderId());
				ps.setBigDecimal(index++, rAD.getTdsDue());
				ps.setBigDecimal(index++, rAD.getTdsPaid());
				ps.setBigDecimal(index, rAD.getTdsWaived());

			}

			@Override
			public int getBatchSize() {
				return allocations.size();
			}
		});

	}

	// MIGRATION PURPOSE
	@Override
	public List<ReceiptAllocationDetail> getDMAllocationsByReference(String reference, String type) {

		// Copied from getAllocationsByReference and added inner join instead of sub query
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Reference", reference);

		StringBuilder selectSql = new StringBuilder(" Select ");
		selectSql.append(" T2.ReceiptAllocationid, T2.ReceiptID, T2.AllocationID, T2.AllocationType,");
		selectSql.append(" T2.AllocationTo, T2.PaidAmount, T2.PaidGST, T2.WaivedAmount, T2.TotalDue, T2.WaivedGST");
		selectSql.append(" From FINRECEIPTHEADER");
		selectSql.append(StringUtils.trim(type));
		selectSql.append(" T1");
		selectSql.append(" Inner Join ReceiptAllocationDetail");
		selectSql.append(StringUtils.trim(type));
		selectSql.append(" T2 on T1.ReceiptID = T2.ReceiptID");

		// If required ignore cancelled receipts
		selectSql.append(" where T1.Reference = :Reference");
		selectSql.append(" Order by T2.ReceiptID ");

		logger.debug("selectSql: " + selectSql.toString());

		RowMapper<ReceiptAllocationDetail> typeRowMapper = BeanPropertyRowMapper
				.newInstance(ReceiptAllocationDetail.class);

		return this.jdbcTemplate.query(selectSql.toString(), source, typeRowMapper);

	}

	@Override
	public List<ReceiptAllocationDetail> getManualAllocationsByRef(long finID, long curReceiptID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" rad.AllocationType AllocationType, rad.AllocationTo AllocationTo");
		sql.append(", sum(rad.PaidAmount) PaidAmount, sum(rad.WaivedAmount) WaivedAmount");
		sql.append(", sum(rad.PaidGST) PaidGST, sum(rad.WaivedGST) WaivedGST");
		sql.append(" From ReceiptAllocationDetail_Temp rad");
		sql.append(" Inner Join FinReceiptHeader_Temp rch On rad.ReceiptId = rch.ReceiptId ");
		sql.append(" Where rch.FinID = ? and rch.ReceiptID <> ?");
		sql.append(" and rch.AllocationType = ? and rch.CancelReason is null");
		sql.append(" Group by rad.AllocationType, rad.AllocationTo");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index++, finID);
			ps.setLong(index++, curReceiptID);
			ps.setString(index, "M");
		}, (rs, rowNum) -> {
			ReceiptAllocationDetail rad = new ReceiptAllocationDetail();

			rad.setAllocationType(rs.getString("AllocationType"));
			rad.setAllocationTo(rs.getLong("AllocationTo"));
			rad.setPaidAmount(rs.getBigDecimal("PaidAmount"));
			rad.setWaivedAmount(rs.getBigDecimal("WaivedAmount"));
			rad.setPaidGST(rs.getBigDecimal("PaidGST"));
			rad.setWaivedGST(rs.getBigDecimal("WaivedGST"));

			return rad;
		});
	}

	@Override
	public List<ReceiptAllocationDetail> getReceiptAllocDetail(long finID, String allocType) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" sum(PaidAmount) PaidAmount, rad.AllocationType, rad.AllocationTo, rad.ReceiptId");
		sql.append(" From ReceiptAllocationDetail_Temp rad");
		sql.append(" Inner Join FinReceiptHeader_Temp rch on rch.ReceiptId = rad.ReceiptId");
		sql.append(" Where FinID = ? and PaidAmount > ?");

		if (Allocation.MANADV.equals(allocType)) {
			sql.append(" and rad.AllocationType in(?, ?)");
		} else {
			sql.append(" and rad.AllocationType = ?");
		}

		sql.append(" and rch.ReceiptModeStatus in (?, ?)");
		sql.append(" group by rad.AllocationType, rad.AllocationTo, rad.ReceiptId");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 0;

			ps.setLong(++index, finID);
			ps.setBigDecimal(++index, BigDecimal.ZERO);

			if (Allocation.MANADV.equals(allocType)) {
				ps.setString(++index, "MANADV");
				ps.setString(++index, "BOUNCE");
			} else if (Allocation.ODC.equals(allocType)) {
				ps.setString(++index, "ODC");
			} else if (Allocation.LPFT.equals(allocType)) {
				ps.setString(++index, "LPFT");
			} else {
				ps.setString(++index, "FEE");
			}

			ps.setString(++index, "C");
			ps.setString(++index, "B");
		}, (rs, rowNum) -> {
			ReceiptAllocationDetail rad = new ReceiptAllocationDetail();
			rad.setPaidAmount(rs.getBigDecimal("PaidAmount"));
			rad.setAllocationType(rs.getString("AllocationType"));
			rad.setAllocationTo(rs.getLong("AllocationTo"));
			rad.setReceiptID(rs.getLong("ReceiptId"));

			return rad;
		});
	}

	@Override
	public List<ReceiptAllocationDetail> getReceiptPaidAmount(long receiptId, String finReference) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" AllocationType, case when AllocationType = 'EMI' then sum(PaidAmount)");
		sql.append(" When AllocationType = 'PRI' then sum(PaidAmount)");
		sql.append(" When AllocationType = 'PFT' then sum(PaidAmount) end PaidAmount From (");
		sql.append(" Select sum(rad.PaidAmount) PaidAmount, rad.AllocationType from ReceiptAllocationDetail rad");
		sql.append(" Inner Join FinReceiptHeader frh on frh.ReceiptID = rad.ReceiptID");
		sql.append(" where frh.ReceiptID > ? and frh.ReceiptID < (select max(ReceiptID)");
		sql.append(" from FinReceiptHeader where Reference = ?)");
		sql.append(" and frh.Reference = ?  and rad.AllocationType not in (?, ?)");
		sql.append(" and frh.ReceiptModeStatus not in (?, ?)");
		sql.append(" group by rad.PaidAmount, rad.AllocationType)");
		sql.append(" group by AllocationType");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 0;

			ps.setLong(++index, receiptId);
			ps.setString(++index, finReference);
			ps.setString(++index, finReference);
			ps.setString(++index, "MANADV");
			ps.setString(++index, "BOUNCE");
			ps.setString(++index, RepayConstants.PAYSTATUS_BOUNCE);
			ps.setString(++index, RepayConstants.PAYSTATUS_CANCEL);
		}, (rs, rowNum) -> {
			ReceiptAllocationDetail rad = new ReceiptAllocationDetail();
			rad.setPaidAmount(rs.getBigDecimal("PaidAmount"));
			rad.setAllocationType(rs.getString("AllocationType"));

			return rad;
		});
	}
}
