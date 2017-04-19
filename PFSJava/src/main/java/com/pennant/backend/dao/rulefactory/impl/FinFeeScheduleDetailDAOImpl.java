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
 *********************************************************************************************
 *                                 FILE HEADER                                               *
 *********************************************************************************************
 *
 * FileName    		:  FinFeeCharges.java                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES              			
 *                                                                  
 * Creation Date    :  10-06-2014    
 *                                                                  
 * Modified Date    :  10-06-2014    
 *                                                                  
 * Description 		:                                             
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 10-06-2014       PENNANT TECHONOLOGIES	                 0.1                            * 
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

package com.pennant.backend.dao.rulefactory.impl;

import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.dao.rulefactory.FinFeeScheduleDetailDAO;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinFeeScheduleDetail;
import com.pennant.backend.model.rulefactory.FeeRule;

public class FinFeeScheduleDetailDAOImpl extends BasisCodeDAO<FeeRule> implements FinFeeScheduleDetailDAO {

private static Logger logger = Logger.getLogger(FinFeeScheduleDetailDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	public FinFeeScheduleDetailDAOImpl() {
		super();
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	

	/**
	 * Method for saving Fee schedule Details list
	 */
	@Override
	public void saveFeeScheduleBatch(List<FinFeeScheduleDetail> feeScheduleList, boolean isWIF, String tableType) {
		logger.debug("Entering");
		
		StringBuilder insertSql = new StringBuilder();
		if(isWIF){
			insertSql.append(" INSERT INTO WIFFinFeeScheduleDetail");
		}else{
			insertSql.append(" INSERT INTO FinFeeScheduleDetail");
		}
		insertSql.append(StringUtils.trimToEmpty(tableType));
		insertSql.append(" (FeeID, SchDate, SchAmount, PaidAmount, OsAmount, WaiverAmount, WriteoffAmount) ");
		insertSql.append(" VALUES (:FeeID, :SchDate, :SchAmount, :PaidAmount, :OsAmount, :WaiverAmount, :WriteoffAmount) ");

		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(feeScheduleList.toArray());
		this.namedParameterJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	/**
	 * Method for saving Fee schedule Details list
	 */
	@Override
	public void deleteFeeScheduleBatch(long feeId, boolean isWIF, String tableType) {
		logger.debug("Entering");
		
		FinFeeDetail finFeeDetail = new FinFeeDetail();
		finFeeDetail.setFeeID(feeId);
		
		StringBuilder deleteSql = new StringBuilder();
		if(isWIF){
			deleteSql.append(" DELETE FROM WIFFinFeeScheduleDetail");
		}else{
			deleteSql.append(" DELETE FROM FinFeeScheduleDetail");
		}
		deleteSql.append(StringUtils.trimToEmpty(tableType));
		deleteSql.append(" WHERE FeeID = :FeeID ");

		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finFeeDetail);
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Fetching Fee schedule Details list based upon Reference
	 */
	@Override
    public List<FinFeeScheduleDetail> getFeeScheduleByFeeID(long feeID, boolean isWIF, String tableType) {
		logger.debug("Entering");
		FinFeeDetail finFeeDetail = new FinFeeDetail();
		finFeeDetail.setFeeID(feeID);
		
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT FeeID, SchDate, SchAmount, PaidAmount, OsAmount, WaiverAmount, WriteoffAmount " );
		if(isWIF){
			selectSql.append(" FROM WIFFinFeeScheduleDetail");
		}else{
			selectSql.append(" FROM FinFeeScheduleDetail");
		}
		selectSql.append(StringUtils.trimToEmpty(tableType));
		selectSql.append(" WHERE  FeeID = :FeeID ");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finFeeDetail);
		RowMapper<FinFeeScheduleDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinFeeScheduleDetail.class);
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
    }
	
	/**
	 * Method for Fetching Fee schedule Details list based upon Reference
	 */
	@Override
	public List<FinFeeScheduleDetail> getFeeScheduleByFinID(List<Long> feeID, boolean isWIF, String tableType) {
		logger.debug("Entering");
		
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT FeeID, SchDate, SchAmount, PaidAmount, OsAmount, WaiverAmount, WriteoffAmount " );
		if(isWIF){
			selectSql.append(" FROM WIFFinFeeScheduleDetail");
		}else{
			selectSql.append(" FROM FinFeeScheduleDetail");
		}
		selectSql.append(StringUtils.trimToEmpty(tableType));
		
		selectSql.append(" WHERE  FeeID IN(:FeeID) ");
		
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FeeID", feeID);
		
		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<FinFeeScheduleDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinFeeScheduleDetail.class);
		List<FinFeeScheduleDetail> feeSchdList = this.namedParameterJdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
		logger.debug("Leaving");
		return feeSchdList;
	}
	
}
