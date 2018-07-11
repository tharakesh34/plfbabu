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

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.receipts.ReceiptTaxDetailDAO;
import com.pennant.backend.model.finance.ReceiptTaxDetail;
import com.pennanttech.pff.core.TableType;

/**
 * DAO methods implementation for the <b>Finance Repayments</b> class.<br>
 * 
 */
public class ReceiptTaxDetailDAOImpl implements ReceiptTaxDetailDAO {
	private static Logger	           logger	= Logger.getLogger(ReceiptTaxDetailDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate	namedParameterJdbcTemplate;

	public ReceiptTaxDetailDAOImpl() {
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
	public ReceiptTaxDetail getTaxDetailByID(long receiptSeqID, String type) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ReceiptSeqID", receiptSeqID);

		StringBuilder selectSql = new StringBuilder("Select ReceiptSeqID , ReceiptID, TaxComponent , PaidCGST , PaidSGST , PaidUGST  ,PaidIGST, TotalGST ");
		selectSql.append(" From ReceiptTaxDetail");
		selectSql.append(StringUtils.trim(type));
		selectSql.append(" Where ReceiptSeqID =:ReceiptSeqID ");

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<ReceiptTaxDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ReceiptTaxDetail.class);
		ReceiptTaxDetail taxDetail = null; 
		try {
			taxDetail = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			taxDetail = null;
		}

		logger.debug("Leaving");
		return taxDetail;
	}

	@Override
	public void save(ReceiptTaxDetail taxDetail, TableType tableType) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into ReceiptTaxDetail");
		insertSql.append(tableType.getSuffix());
		insertSql.append(" (ReceiptSeqID , ReceiptID, TaxComponent , PaidCGST , PaidSGST , PaidUGST  ,PaidIGST, TotalGST)");
		insertSql.append(" Values(:ReceiptSeqID , :ReceiptID, :TaxComponent , :PaidCGST , :PaidSGST , :PaidUGST  , :PaidIGST, :TotalGST)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(taxDetail);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
	}
	
	@Override
	public void deleteByReceiptID(long receiptID, TableType tableType) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ReceiptID", receiptID);

		StringBuilder deleteSql = new StringBuilder(" DELETE From ReceiptTaxDetail");
		deleteSql.append(tableType.getSuffix());
		deleteSql.append(" where ReceiptID = :ReceiptID ");

		logger.debug("selectSql: " + deleteSql.toString());
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), source);
		logger.debug("Leaving");
	}
	
	@Override
	public void delete(long receiptSeqID, TableType tableType) {
		logger.debug("Entering");
		
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ReceiptSeqID", receiptSeqID);
		
		StringBuilder deleteSql = new StringBuilder(" DELETE From ReceiptTaxDetail");
		deleteSql.append(tableType.getSuffix());
		deleteSql.append(" where ReceiptSeqID = :ReceiptSeqID ");
		
		logger.debug("selectSql: " + deleteSql.toString());
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), source);
		logger.debug("Leaving");
	}

}
