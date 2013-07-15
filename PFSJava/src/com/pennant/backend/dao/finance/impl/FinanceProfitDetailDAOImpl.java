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
 * FileName    		:  FinanceProfitDetailDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  09-02-2012    														*
 *                                                                  						*
 * Modified Date    :  09-02-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 09-02-2012       Pennant	                 0.1                                            * 
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

import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.model.finance.FinanceProfitDetail;

/**
 * DAO methods implementation for the <b>FinanceProfitDetail model</b> class.<br>
 * 
 */
public class FinanceProfitDetailDAOImpl implements FinanceProfitDetailDAO {

	private static Logger logger = Logger.getLogger(FinanceProfitDetailDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	/**
	 * To Set dataSource
	 * 
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 *  Method for get the FinanceProfitDetail Object by Key finReference
	 */
	@Override
	public FinanceProfitDetail getFinProfitDetailsById(String finReference) {
		logger.debug("Entering");
		FinanceProfitDetail finProfitDetails = new FinanceProfitDetail();

		finProfitDetails.setFinReference(finReference);
	    
		StringBuilder selectSql = new StringBuilder("Select FinReference, CustId, FinBranch, FinType, LastMdfDate," );
		selectSql.append(" TotalPftSchd, TotalPftCpz, TotalPftPaid, TotalPftBal, TotalPftPaidInAdv," );
		selectSql.append(" TotalPriPaid, TotalPriBal, TdSchdPft, TdPftCpz, TdSchdPftPaid," );
		selectSql.append(" TdSchdPftBal, TdPftAccrued, TdPftAccrueSusp, TdPftAmortized, TdPftAmortizedSusp," );
		selectSql.append(" TdSchdPri, TdSchdPriPaid, TdSchdPriBal, AcrTillNBD, AcrTillLBD,");
		selectSql.append(" AcrTodayToNBD,AmzTillNBD,AmzTillLBD,AmzTodayToNBD");
		selectSql.append(" From FinPftDetails");
		selectSql.append(" Where FinReference =:FinReference");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finProfitDetails);
		RowMapper<FinanceProfitDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceProfitDetail.class);

		try {
			finProfitDetails = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			finProfitDetails = null;
		}
		logger.debug("Leaving");
		return finProfitDetails;

	}
	
	/**
	 *  Method for get the FinanceProfitDetail List by key Fileds
	 * @param lastMdfDate
	 * @param amzTillNBD
	 * @return
	 */
	@Override
	public List<FinanceProfitDetail> getFinPftDetailListToAMZ(Date lastMdfDate) {
		logger.debug("Entering");
		
		FinanceProfitDetail finProfitDetails = new FinanceProfitDetail();
		finProfitDetails.setLastMdfDate(lastMdfDate);
	    
		StringBuilder selectSql = new StringBuilder("Select FinReference, CustId, FinBranch, FinType, LastMdfDate," );
		selectSql.append(" TotalPftSchd, TotalPftCpz, TotalPftPaid, TotalPftBal, TotalPftPaidInAdv," );
		selectSql.append(" TotalPriPaid, TotalPriBal, TdSchdPft, TdPftCpz, TdSchdPftPaid," );
		selectSql.append(" TdSchdPftBal, TdPftAccrued, TdPftAccrueSusp, TdPftAmortized, TdPftAmortizedSusp," );
		selectSql.append(" TdSchdPri, TdSchdPriPaid, TdSchdPriBal, AcrTillNBD, AcrTillLBD,");
		selectSql.append(" AcrTodayToNBD,AmzTillNBD,AmzTillLBD,AmzTodayToNBD");
		selectSql.append(" From FinPftDetails");
		selectSql.append(" Where LastMdfDate =:LastMdfDate ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finProfitDetails);
		RowMapper<FinanceProfitDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceProfitDetail.class);
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	@Override
	public void update(FinanceProfitDetail finProfitDetails) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update FinPftDetails ");
		updateSql.append("Set FinReference =:FinReference , CustId=:CustId , FinBranch =:FinBranch, FinType = :FinType ," );
		updateSql.append(" LastMdfDate = :LastMdfDate, TotalPftSchd =:TotalPftSchd, TotalPftCpz =:TotalPftCpz," );
		updateSql.append(" TotalPftPaid =:TotalPftPaid, TotalPftBal=:TotalPftBal, TotalPftPaidInAdv=:TotalPftPaidInAdv, TotalPriPaid=:TotalPriPaid," );
		updateSql.append(" TotalPriBal =:TotalPriBal, TdSchdPft=:TdSchdPft, TdPftCpz=:TdPftCpz," );
		updateSql.append(" TdSchdPftPaid = :TdSchdPftPaid, TdSchdPftBal = :TdSchdPftBal, TdPftAccrued=:TdPftAccrued," );
		updateSql.append(" TdPftAccrueSusp=:TdPftAccrueSusp, TdPftAmortized = :TdPftAmortized ,TdPftAmortizedSusp= :TdPftAmortizedSusp,");
		updateSql.append(" TdSchdPri =:TdSchdPri, TdSchdPriPaid=:TdSchdPriPaid," );
		updateSql.append(" TdSchdPriBal=:TdSchdPriBal , AcrTillNBD=:AcrTillNBD , AcrTillLBD=:AcrTillLBD, AcrTodayToNBD=:AcrTodayToNBD," );
		updateSql.append(" AmzTillNBD =:AmzTillNBD,AmzTillLBD= :AmzTillLBD,AmzTodayToNBD = :AmzTodayToNBD");
		updateSql.append(" Where FinReference =:FinReference");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finProfitDetails);
		this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		logger.debug("Leaving");
	}

	@Override
	public String save(FinanceProfitDetail finProfitDetails) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into FinPftDetails");
		insertSql.append(" (FinReference, CustId, FinBranch, FinType, LastMdfDate," );
		insertSql.append(" TotalPftSchd, TotalPftCpz, TotalPftPaid, TotalPftBal, TotalPftPaidInAdv," );
		insertSql.append(" TotalPriPaid, TotalPriBal, TdSchdPft, TdPftCpz, TdSchdPftPaid," );
		insertSql.append(" TdSchdPftBal, TdPftAccrued, TdPftAccrueSusp, TdPftAmortized, TdPftAmortizedSusp," );
		insertSql.append(" TdSchdPri, TdSchdPriPaid, TdSchdPriBal, AcrTillNBD, AcrTillLBD,");
		insertSql.append(" AcrTodayToNBD, AmzTillNBD, AmzTillLBD, AmzTodayToNBD)");
		insertSql.append(" Values(:FinReference, :CustId, :FinBranch, :FinType,:LastMdfDate," );
		insertSql.append(" :TotalPftSchd, :TotalPftCpz , :TotalPftPaid, :TotalPftBal, :TotalPftPaidInAdv," );
		insertSql.append(" :TotalPriPaid, :TotalPriBal, :TdSchdPft, :TdPftCpz, :TdSchdPftPaid," );
		insertSql.append(" :TdSchdPftBal, :TdPftAccrued, :TdPftAccrueSusp, :TdPftAmortized, :TdPftAmortizedSusp,");
		insertSql.append(" :TdSchdPri, :TdSchdPriPaid, :TdSchdPriBal, :AcrTillNBD, :AcrTillLBD," );
		insertSql.append(" :AcrTodayToNBD, :AmzTillNBD, :AmzTillLBD, :AmzTodayToNBD)");
		
		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finProfitDetails);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return finProfitDetails.getFinReference();
	}
	
	/**
	 * Method for Processing List of Profitdetails using BatchUpdate
	 * @param finProfitDetailList
	 */
	public void profitDetailsBatchUpdate(List<FinanceProfitDetail> finProfitDetailList, boolean updateOnly){
		logger.debug("Entering");
		for (FinanceProfitDetail financeProfitDetail : finProfitDetailList) {
			if (!updateOnly && getFinProfitDetailsById(financeProfitDetail.getFinReference()) == null) {
				save(financeProfitDetail);
			} else {
				update(financeProfitDetail);
			}
		}
		logger.debug("Leaving");
	}

}
