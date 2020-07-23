/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  FinanceRepaymentsDAOImpl.java                                        * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-05-2011    														*
 *                                                                  						*
 * Modified Date    :  05-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2011       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennant.backend.dao.receipts.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.receipts.ReceiptAllocationDetailDAO;
import com.pennant.backend.model.finance.ReceiptAllocationDetail;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * DAO methods implementation for the <b>Finance Repayments</b> class.<br>
 * 
 */
public class ReceiptAllocationDetailDAOImpl extends SequenceDao<ReceiptAllocationDetail>
		implements ReceiptAllocationDetailDAO {
	private static Logger logger = Logger.getLogger(ReceiptAllocationDetailDAOImpl.class);

	public ReceiptAllocationDetailDAOImpl() {
		super();
	}

	@Override
	public List<ReceiptAllocationDetail> getAllocationsByReceiptID(long receiptID, String type) {
		logger.debug(Literal.ENTERING);

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

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int index = 1;
					ps.setLong(index++, receiptID);
				}
			}, new RowMapper<ReceiptAllocationDetail>() {
				@Override
				public ReceiptAllocationDetail mapRow(ResultSet rs, int rowNum) throws SQLException {
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
					rad.setTaxHeaderId(rs.getLong("TaxHeaderId"));//TdsDue,TdsPaid,TdsWaived
					rad.setTdsDue(rs.getBigDecimal("TdsDue"));
					rad.setTdsPaid(rs.getBigDecimal("TdsPaid"));
					rad.setTdsWaived(rs.getBigDecimal("TdsWaived"));

					if (StringUtils.trimToEmpty(type).contains("View")) {
						rad.setTypeDesc(rs.getString("TypeDesc"));
					}

					return rad;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
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
		logger.debug(Literal.ENTERING);

		for (ReceiptAllocationDetail allocation : allocations) {
			if (allocation.getReceiptAllocationid() == Long.MIN_VALUE) {
				allocation.setReceiptAllocationid(getNextValue("SeqReceiptAllocationDetail"));
				logger.debug("get NextID:" + allocation.getReceiptAllocationid());
			}
		}

		StringBuilder sql = new StringBuilder();
		sql.append("Insert Into ReceiptAllocationDetail");
		sql.append(tableType.getSuffix());
		sql.append("(ReceiptAllocationid, ReceiptID, AllocationID, AllocationType, AllocationTo");
		sql.append(
				", PaidAmount, WaivedAmount, WaiverAccepted, PaidGST, TotalDue, WaivedGST, TaxHeaderId,TdsDue,TdsPaid,TdsWaived)");
		sql.append(" Values(:ReceiptAllocationid, :ReceiptID, :AllocationID, :AllocationType, :AllocationTo");
		sql.append(
				", :PaidAmount, :WaivedAmount, :WaiverAccepted, :PaidGST, :TotalDue, :WaivedGST, :TaxHeaderId,:TdsDue,:TdsPaid,:TdsWaived)");

		logger.debug(Literal.SQL + sql.toString());

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(allocations.toArray());
		this.jdbcTemplate.batchUpdate(sql.toString(), beanParameters);
		logger.debug(Literal.LEAVING);
	}

	//MIGRATION PURPOSE
	@Override
	public List<ReceiptAllocationDetail> getDMAllocationsByReference(String reference, String type) {

		//Copied from getAllocationsByReference and added inner join instead of sub query
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

		//If required ignore cancelled receipts
		selectSql.append(" where T1.Reference = :Reference");
		selectSql.append(" Order by T2.ReceiptID ");

		logger.debug("selectSql: " + selectSql.toString());

		List<ReceiptAllocationDetail> allocations = null;

		try {
			RowMapper<ReceiptAllocationDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
					.newInstance(ReceiptAllocationDetail.class);
			allocations = this.jdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
		} catch (Exception e) {
			logger.error(e);
		}

		logger.debug("Leaving");
		return allocations;

	}

	@Override
	public List<ReceiptAllocationDetail> getManualAllocationsByRef(String finReference, long curReceiptID) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" rad.AllocationType AllocationType, rad.AllocationTo AllocationTo");
		sql.append(", SUM(rad.PaidAmount) PaidAmount, SUM(rad.WaivedAmount) WaivedAmount");
		sql.append(", SUM(rad.PaidGST) PaidGST, SUM(rad.WaivedGST) WaivedGST");
		sql.append(" FROM RECEIPTALLOCATIONDETAIL_TEMP rad");
		sql.append(" INNER JOIN FINRECEIPTHEADER_TEMP rch ON rad.RECEIPTID = rch.RECEIPTID ");
		sql.append(" Where rch.Reference = ? and rch.ReceiptID <> ?");
		sql.append(" and rch.ALLOCATIONTYPE = ? and rch.CANCELREASON IS NULL ");
		sql.append(" GROUP BY rad.AllocationType, rad.AllocationTo ");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int index = 1;
					ps.setString(index++, finReference);
					ps.setLong(index++, curReceiptID);
					ps.setString(index++, "M");
				}
			}, new RowMapper<ReceiptAllocationDetail>() {
				@Override
				public ReceiptAllocationDetail mapRow(ResultSet rs, int rowNum) throws SQLException {
					ReceiptAllocationDetail rad = new ReceiptAllocationDetail();

					rad.setAllocationType(rs.getString("AllocationType"));
					rad.setAllocationTo(rs.getLong("AllocationTo"));
					rad.setPaidAmount(rs.getBigDecimal("PaidAmount"));
					rad.setWaivedAmount(rs.getBigDecimal("WaivedAmount"));
					rad.setPaidGST(rs.getBigDecimal("PaidGST"));
					rad.setWaivedGST(rs.getBigDecimal("WaivedGST"));

					return rad;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
	}
}
