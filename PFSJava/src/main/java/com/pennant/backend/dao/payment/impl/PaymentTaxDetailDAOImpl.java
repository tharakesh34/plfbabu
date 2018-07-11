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
package com.pennant.backend.dao.payment.impl;

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

import com.pennant.backend.dao.payment.PaymentTaxDetailDAO;
import com.pennant.backend.model.payment.PaymentTaxDetail;
import com.pennanttech.pff.core.TableType;

/**
 * DAO methods implementation for the <b>Payment Tax Details</b> class.<br>
 * 
 */
public class PaymentTaxDetailDAOImpl implements PaymentTaxDetailDAO {
	private static Logger	           logger	= Logger.getLogger(PaymentTaxDetailDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate	namedParameterJdbcTemplate;

	public PaymentTaxDetailDAOImpl() {
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
	public PaymentTaxDetail getTaxDetailByID(long paymentDetailID, String type) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("PaymentDetailID", paymentDetailID);

		StringBuilder selectSql = new StringBuilder("Select PaymentDetailID , PaymentID, TaxComponent , PaidCGST , PaidSGST , PaidUGST  ,PaidIGST, TotalGST ");
		selectSql.append(" From PaymentTaxDetail");
		selectSql.append(StringUtils.trim(type));
		selectSql.append(" Where PaymentDetailID =:PaymentDetailID ");

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<PaymentTaxDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(PaymentTaxDetail.class);
		PaymentTaxDetail taxDetail = null; 
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
	public void save(PaymentTaxDetail taxDetail, TableType tableType) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into PaymentTaxDetail");
		insertSql.append(tableType.getSuffix());
		insertSql.append(" (PaymentDetailID , PaymentID, TaxComponent , PaidCGST , PaidSGST , PaidUGST  ,PaidIGST, TotalGST)");
		insertSql.append(" Values(:PaymentDetailID , :PaymentID, :TaxComponent , :PaidCGST , :PaidSGST , :PaidUGST  , :PaidIGST, :TotalGST)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(taxDetail);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
	}
	
	@Override
	public void deleteByPaymentID(long paymentID, TableType tableType) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("PaymentID", paymentID);

		StringBuilder deleteSql = new StringBuilder(" DELETE From PaymentTaxDetail");
		deleteSql.append(tableType.getSuffix());
		deleteSql.append(" where PaymentID = :PaymentID ");

		logger.debug("selectSql: " + deleteSql.toString());
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), source);
		logger.debug("Leaving");
	}
	
	@Override
	public void delete(long receiptSeqID, TableType tableType) {
		logger.debug("Entering");
		
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("PaymentDetailID", receiptSeqID);
		
		StringBuilder deleteSql = new StringBuilder(" DELETE From PaymentTaxDetail");
		deleteSql.append(tableType.getSuffix());
		deleteSql.append(" where PaymentDetailID = :PaymentDetailID ");
		
		logger.debug("selectSql: " + deleteSql.toString());
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), source);
		logger.debug("Leaving");
	}

}
