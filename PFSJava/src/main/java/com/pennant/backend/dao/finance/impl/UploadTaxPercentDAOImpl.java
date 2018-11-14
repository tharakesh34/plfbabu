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
 * FileName    		:  UploadHeaderDAOImpl.java                                             * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  17-12-2017    														*
 *                                                                  						*
 * Modified Date    :  17-12-2017    														*
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

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.finance.UploadTaxPercentDAO;
import com.pennant.backend.model.expenses.UploadTaxPercent;
import com.pennanttech.pennapps.core.jdbc.BasicDao;

/**
 * DAO methods implementation for the <b>UploadTaxPercent model</b> class.<br>
 * 
 */
public class UploadTaxPercentDAOImpl extends BasicDao<UploadTaxPercent> implements UploadTaxPercentDAO {
	private static Logger logger = Logger.getLogger(UploadTaxPercentDAOImpl.class);

	public UploadTaxPercentDAOImpl() {
		super();
	}

	/**
	 * Method for saving Fee schedule Details list
	 */
	@Override
	public void saveUploadDetails(List<UploadTaxPercent> uploadDetailsList) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder();
		insertSql.append(" INSERT INTO UploadTaxPercent");
		insertSql.append(" (UploadId, FinReference, FeeTypeCode, TaxPercent, Status, Reason) ");
		insertSql.append(" VALUES(:UploadId, :FinReference, :FeeTypeCode, :TaxPercent, :Status, :Reason)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(uploadDetailsList.toArray());

		this.jdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
	}

	@Override
	public List<UploadTaxPercent> getSuccesFailedCount(long uploadId) {

		logger.debug("Entering");
		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("UploadId", uploadId);
		StringBuilder selectSql = new StringBuilder("Select Count(UploadId) Count, Status ");
		selectSql.append(" from UploadTaxPercent ");
		selectSql.append(" Where UploadId = :UploadId Group By STATUS");

		logger.debug("selectListSql: " + selectSql.toString());
		RowMapper<UploadTaxPercent> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(UploadTaxPercent.class);
		logger.debug("Leaving");

		return this.jdbcTemplate.query(selectSql.toString(), mapSqlParameterSource, typeRowMapper);
	}

}