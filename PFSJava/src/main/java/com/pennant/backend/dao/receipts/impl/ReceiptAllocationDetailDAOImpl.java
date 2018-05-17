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
 * FileName    		:  FinanceRepaymentsDAOImpl.java                                                   * 	  
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

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.dao.receipts.ReceiptAllocationDetailDAO;
import com.pennant.backend.model.finance.ReceiptAllocationDetail;
import com.pennanttech.pff.core.TableType;

/**
 * DAO methods implementation for the <b>Finance Repayments</b> class.<br>
 * 
 */
public class ReceiptAllocationDetailDAOImpl extends BasisNextidDaoImpl<ReceiptAllocationDetail> implements ReceiptAllocationDetailDAO {
	private static Logger	           logger	= Logger.getLogger(ReceiptAllocationDetailDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate	namedParameterJdbcTemplate;

	public ReceiptAllocationDetailDAOImpl() {
		super();
	}

	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	@Override
	public List<ReceiptAllocationDetail> getAllocationsByReceiptID(long receiptID, String type) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ReceiptID", receiptID);

		StringBuilder selectSql = new StringBuilder(" Select ReceiptAllocationid, ReceiptID , AllocationID , AllocationType , AllocationTo , PaidAmount , WaivedAmount ");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append( " ,TypeDesc ");
		}
		selectSql.append(" From ReceiptAllocationDetail");
		selectSql.append(StringUtils.trim(type));
		selectSql.append(" Where ReceiptID =:ReceiptID ");

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<ReceiptAllocationDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ReceiptAllocationDetail.class);
		List<ReceiptAllocationDetail> allocations = this.namedParameterJdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
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
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), source);
		logger.debug("Leaving");
	}

	@Override
	public void saveAllocations(List<ReceiptAllocationDetail> allocations, TableType tableType) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder();

		for (ReceiptAllocationDetail receiptAllocationDetail : allocations) {
			if (receiptAllocationDetail.getReceiptAllocationid() == Long.MIN_VALUE) {
				receiptAllocationDetail
						.setReceiptAllocationid(getNextidviewDAO().getNextId("SeqReceiptAllocationDetail"));
				logger.debug("get NextID:" + receiptAllocationDetail.getReceiptAllocationid());
			}
		}
        insertSql.append("Insert Into ReceiptAllocationDetail");
		insertSql.append(tableType.getSuffix());
		insertSql.append(" (ReceiptAllocationid, ReceiptID , AllocationID , AllocationType , AllocationTo , PaidAmount , WaivedAmount)");
		insertSql.append(" Values(:ReceiptAllocationid,:ReceiptID , :AllocationID , :AllocationType , :AllocationTo , :PaidAmount , :WaivedAmount)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(allocations.toArray());
		this.namedParameterJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
	}
	
	
	
}
