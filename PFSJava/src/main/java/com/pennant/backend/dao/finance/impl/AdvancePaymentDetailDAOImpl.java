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
 * FileName    		:  FinAdvancePaymentsDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-08-2013    														*
 *                                                                  						*
 * Modified Date    :  14-08-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-08-2013       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.finance.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

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
	public AdvancePaymentDetail getAdvancePaymentDetailBalByRef(String finReference) {
		StringBuilder sql = new StringBuilder("Select FinReference, SUM(AdvInt) AdvInt");
		sql.append(", SUM(AdvIntTds) AdvIntTds, SUM(AdvEMI) AdvEMI, SUM(AdvEMITds) AdvEMITds");
		sql.append(" From AdvancePaymentDetails");
		sql.append(" Where FinReference = :FinReference group by FinReference");

		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("FinReference", finReference);

		logger.trace(Literal.SQL + sql.toString());
		RowMapper<AdvancePaymentDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(AdvancePaymentDetail.class);

		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), paramMap, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}
		return null;
	}

	@Override
	public String save(AdvancePaymentDetail advancePaymentDetail) {
		StringBuilder sql = new StringBuilder();
		sql.append("Insert Into AdvancePaymentDetails");
		sql.append("(FinReference, InstructionUID , AdvInt, AdvIntTds, AdvEMI, AdvEMITds)");
		sql.append("Values(:FinReference, :InstructionUID, :AdvInt, :AdvIntTds, :AdvEMI, :AdvEMITds)");
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(advancePaymentDetail);

		this.jdbcTemplate.update(sql.toString(), beanParameters);

		return advancePaymentDetail.getFinReference();
	}

}