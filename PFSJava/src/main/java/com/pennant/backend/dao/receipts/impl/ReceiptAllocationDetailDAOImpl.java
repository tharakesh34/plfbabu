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

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
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
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ReceiptID", receiptID);

		StringBuilder selectSql = new StringBuilder(
				" Select ReceiptAllocationid, ReceiptID , AllocationID , AllocationType , AllocationTo , PaidAmount , WaivedAmount, WaiverAccepted, PaidGST, TotalDue, WaivedGST");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(" ,TypeDesc ");
		}
		selectSql.append(" From ReceiptAllocationDetail");
		selectSql.append(StringUtils.trim(type));
		selectSql.append(" Where ReceiptID =:ReceiptID ");

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<ReceiptAllocationDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(ReceiptAllocationDetail.class);
		List<ReceiptAllocationDetail> allocations = this.jdbcTemplate.query(selectSql.toString(), source,
				typeRowMapper);
		logger.debug("Leaving");
		return allocations;
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
		sql.append(", PaidAmount , WaivedAmount, WaiverAccepted, PaidGST, TotalDue, WaivedGST)");
		sql.append(" Values(:ReceiptAllocationid, :ReceiptID, :AllocationID, :AllocationType, :AllocationTo");
		sql.append(", :PaidAmount, :WaivedAmount, :WaiverAccepted, :PaidGST, :TotalDue, :WaivedGST)");

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
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Reference", finReference);
		source.addValue("ReceiptID", curReceiptID);

		StringBuilder selectSql = new StringBuilder("");
		selectSql.append(" Select RAD.AllocationType AllocationType, SUM(RAD.PaidAmount) PaidAmount, ");
		selectSql.append(" SUM(RAD.WaivedAmount) WaivedAmount, SUM(RAD.PaidGST) PaidGST, SUM(RAD.WaivedGST) WaivedGST");
		selectSql.append(" FROM RECEIPTALLOCATIONDETAIL_TEMP RAD ");
		selectSql.append(" INNER JOIN FINRECEIPTHEADER_TEMP RCH ON RAD.RECEIPTID = RCH.RECEIPTID ");
		selectSql.append(" Where RCH.Reference = :Reference AND RCH.ReceiptID <> :ReceiptID");
		selectSql.append(" AND RCH.ALLOCATIONTYPE = 'M' AND RCH.CANCELREASON IS NULL ");
		selectSql.append(" GROUP BY RAD.AllocationType ");

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<ReceiptAllocationDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(ReceiptAllocationDetail.class);

		List<ReceiptAllocationDetail> radList = null;

		try {
			radList = this.jdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {

		}

		logger.debug("Leaving");
		return radList;
	}
}
