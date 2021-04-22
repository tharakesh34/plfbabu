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
 * FileName    		:  IRRScheduleDetailDAOImpl.java                                    * 	  
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
package com.pennant.backend.dao.finance.impl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;

import com.pennant.backend.dao.finance.IRRScheduleDetailDAO;
import com.pennant.backend.model.finance.IRRScheduleDetail;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * DAO methods implementation for the <b>WIFIRRScheduleDetail model</b> class.<br>
 */
public class IRRScheduleDetailDAOImpl extends BasicDao<IRRScheduleDetail> implements IRRScheduleDetailDAO {
	private static Logger logger = LogManager.getLogger(IRRScheduleDetailDAOImpl.class);

	public IRRScheduleDetailDAOImpl() {
		super();
	}

	/**
	 * Method for Deletion of IRR Schedule Details Data by Reference
	 * 
	 * @param finReference
	 * @param type
	 */
	@Override
	public void deleteByFinReference(String finReference) {
		logger.debug("Entering");
		IRRScheduleDetail wIFIRRScheduleDetail = new IRRScheduleDetail();
		wIFIRRScheduleDetail.setFinReference(finReference);

		StringBuilder deleteSql = new StringBuilder("Delete From ");
		deleteSql.append(" IRRScheduleDetail");
		deleteSql.append(" Where FinReference =:FinReference");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(wIFIRRScheduleDetail);
		this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	/**
	 * Method for Saving List of IRR schedule Details against Reference
	 */
	@Override
	public void saveList(List<IRRScheduleDetail> IRRScheduleDetail) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into ");
		insertSql.append(" IRRScheduleDetail");
		insertSql.append(
				" (FinReference, SchDate, ProfitCalc, PrincipalCalc, RepayAmount, ClosingBalance, GapInterst )");
		insertSql.append(
				" Values(:FinReference, :SchDate, :ProfitCalc, :PrincipalCalc, :RepayAmount, :ClosingBalance, :GapInterst)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(IRRScheduleDetail.toArray());
		try {
			this.jdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		} catch (Exception e) {
			logger.error("Exception", e);
			throw e;
		}
		logger.debug("Leaving");
	}

	@Override
	public List<IRRScheduleDetail> getIRRScheduleDetailList(String finReference) {
		IRRScheduleDetail detail = new IRRScheduleDetail();
		detail.setFinReference(finReference);

		StringBuilder sql = new StringBuilder(" Select FinReference, SchDate, ProfitCalc, ");
		sql.append(" PrincipalCalc, RepayAmount, ClosingBalance, GapInterst ");
		sql.append(" From IRRScheduleDetail");
		sql.append(" Where FinReference =:FinReference  order by SchDate asc");

		logger.debug(Literal.SQL + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(detail);
		RowMapper<IRRScheduleDetail> typeRowMapper = BeanPropertyRowMapper.newInstance(IRRScheduleDetail.class);

		List<IRRScheduleDetail> finSchdDetails = this.jdbcTemplate.query(sql.toString(), beanParameters, typeRowMapper);
		return finSchdDetails;
	}

}