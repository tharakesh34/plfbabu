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
 * FileName    		:  WIFFinanceScheduleDetailDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-11-2011    														*
 *                                                                  						*
 * Modified Date    :  12-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-11-2011       Pennant	                 0.1                                            * 
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

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.finance.WIFFinanceScheduleDetailDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pff.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>WIFFinanceScheduleDetail model</b> class.<br>
 * 
 */

public class WIFFinanceScheduleDetailDAOImpl extends BasisCodeDAO<FinanceScheduleDetail> implements WIFFinanceScheduleDetailDAO {

	private static Logger logger = Logger.getLogger(WIFFinanceScheduleDetailDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	public WIFFinanceScheduleDetailDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record  Finance Schedule Detail details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return FinanceScheduleDetail
	 */
	@Override
	public FinanceScheduleDetail getWIFFinanceScheduleDetailById(final String id, String type) {
		logger.debug("Entering");
		FinanceScheduleDetail wIFFinanceScheduleDetail = new FinanceScheduleDetail();
		
		wIFFinanceScheduleDetail.setId(id);
		
		StringBuilder selectSql = new StringBuilder("Select FinReference, SchDate, SchSeq, PftOnSchDate,");
		selectSql.append(" CpzOnSchDate, RepayOnSchDate, RvwOnSchDate, DisbOnSchDate,");
		selectSql.append(" DownpaymentOnSchDate, BalanceForPftCal, BaseRate, SplRate, ActRate, NoOfDays,");
		selectSql.append(" DayFactor, ProfitCalc, ProfitSchd, PrincipalSchd, RepayAmount, ProfitBalance,");
		selectSql.append(" DisbAmount, DownPaymentAmount, CpzAmount,");
		selectSql.append(" ClosingBalance, ProfitFraction, PrvRepayAmount, ");
		selectSql.append(" SchdPriPaid, SchdPftPaid, IsSchdPriPaid, IsSchdPftPaid,Specifier,");
		selectSql.append(" DefSchdDate,");

		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append("lovDescBaseRateName,lovDescSplRateName,");
		}
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId,");
		selectSql.append(" NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From WIFFinScheduleDetails");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(wIFFinanceScheduleDetail);
		RowMapper<FinanceScheduleDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(
				FinanceScheduleDetail.class);
		
		try{
			wIFFinanceScheduleDetail = this.namedParameterJdbcTemplate.queryForObject(
					selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			wIFFinanceScheduleDetail = null;
		}
		logger.debug("Leaving");
		return wIFFinanceScheduleDetail;
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the WIFFinScheduleDetails or WIFFinScheduleDetails_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Finance Schedule Detail by key FinReference
	 * 
	 * @param Finance Schedule Detail (wIFFinanceScheduleDetail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(FinanceScheduleDetail wIFFinanceScheduleDetail,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete From WIFFinScheduleDetails");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FinReference =:FinReference");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(wIFFinanceScheduleDetail);
		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		}catch(DataAccessException e){
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}
	
	/**
	 * This method insert new Records into WIFFinScheduleDetails or WIFFinScheduleDetails_Temp.
	 *
	 * save Finance Schedule Detail 
	 * 
	 * @param Finance Schedule Detail (wIFFinanceScheduleDetail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@Override
	public String save(FinanceScheduleDetail wIFFinanceScheduleDetail,String type) {
		logger.debug("Entering");
		
		StringBuilder insertSql =new StringBuilder("Insert Into WIFFinScheduleDetails");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (FinReference, SchDate, SchSeq, PftOnSchDate,");
		insertSql.append(" CpzOnSchDate, RepayOnSchDate, RvwOnSchDate, DisbOnSchDate,");
		insertSql.append(" DownpaymentOnSchDate, BalanceForPftCal, BaseRate, SplRate, ActRate, NoOfDays,");
		insertSql.append(" DayFactor, ProfitCalc, ProfitSchd, PrincipalSchd, RepayAmount, ProfitBalance,");
		insertSql.append(" DisbAmount, DownPaymentAmount, CpzAmount,");
		insertSql.append(" ClosingBalance, ProfitFraction, PrvRepayAmount, ");
		insertSql.append(" SchdPriPaid, SchdPftPaid, IsSchdPriPaid, IsSchdPftPaid, Specifier,");
		insertSql.append(" DefSchdDate");
		insertSql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId,");
		insertSql.append(" NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:FinReference, :SchDate, :SchSeq, :PftOnSchDate,");
		insertSql.append(" :CpzOnSchDate, :RepayOnSchDate, :RvwOnSchDate, :DisbOnSchDate, ");
		insertSql.append(" :DownpaymentOnSchDate, :BalanceForPftCal, :BaseRate, :SplRate, :ActRate, :NoOfDays,");
		insertSql.append(" :DayFactor, :ProfitCalc, :ProfitSchd, :PrincipalSchd, :RepayAmount, :ProfitBalance,");
		insertSql.append(" :DisbAmount, :DownPaymentAmount, :CpzAmount,");
		insertSql.append(" :ClosingBalance, :ProfitFraction, :PrvRepayAmount, ");
		insertSql.append(" :SchdPriPaid, :SchdPftPaid, :IsSchdPriPaid, :IsSchdPftPaid, :Specifier,");
		insertSql.append(" :DefSchdDate");
		insertSql.append(", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId,");
		insertSql.append(" :NextTaskId, :RecordType, :WorkflowId)");
		
		
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(wIFFinanceScheduleDetail);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return wIFFinanceScheduleDetail.getId();
	}
	
	/**
	 * This method updates the Record WIFFinScheduleDetails or WIFFinScheduleDetails_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Finance Schedule Detail by key FinReference and Version
	 * 
	 * @param Finance Schedule Detail (wIFFinanceScheduleDetail)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@Override
	public void update(FinanceScheduleDetail wIFFinanceScheduleDetail,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update WIFFinScheduleDetails");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set SchDate = :SchDate, SchSeq = :SchSeq,");
		updateSql.append(" PftOnSchDate= :PftOnSchDate, CpzOnSchDate = :CpzOnSchDate, RepayOnSchDate= :RepayOnSchDate,");
		updateSql.append(" RvwOnSchDate= :RvwOnSchDate, DisbOnSchDate= :DisbOnSchDate,");
		updateSql.append(" DownpaymentOnSchDate = :DownpaymentOnSchDate,");
		updateSql.append(" BalanceForPftCal= :BalanceForPftCal, BaseRate= :BaseRate, SplRate= :SplRate,");
		updateSql.append(" ActRate= :ActRate, NoOfDays= :NoOfDays, DayFactor =:DayFactor, ProfitCalc= :ProfitCalc,");
		updateSql.append(" ProfitSchd= :ProfitSchd, PrincipalSchd= :PrincipalSchd, RepayAmount= :RepayAmount,");
		updateSql.append(" ProfitBalance=:ProfitBalance, DisbAmount= :DisbAmount, DownPaymentAmount= :DownPaymentAmount,");
		updateSql.append(" CpzAmount= :CpzAmount,");
		updateSql.append(" ClosingBalance= :ClosingBalance,");
		updateSql.append(" ProfitFraction= :ProfitFraction, PrvRepayAmount= :PrvRepayAmount,");
		updateSql.append(" SchdPriPaid= :SchdPriPaid, SchdPftPaid= :SchdPftPaid, IsSchdPriPaid= :IsSchdPriPaid,");
		updateSql.append(" IsSchdPftPaid= :IsSchdPftPaid,Specifier= :Specifier,");
		updateSql.append(" DefSchdDate= :DefSchdDate,");
		updateSql.append(" Version= :Version , LastMntBy=:LastMntBy,");
		updateSql.append(" LastMntOn= :LastMntOn, RecordStatus=:RecordStatus, RoleCode=:RoleCode,");
		updateSql.append(" NextRoleCode= :NextRoleCode, TaskId= :TaskId,");
		updateSql.append(" NextTaskId= :NextTaskId, RecordType= :RecordType, WorkflowId= :WorkflowId)");
		updateSql.append(" Where FinReference =:FinReference");
		
		if (!type.endsWith("_Temp")){
			updateSql.append("  AND Version= :Version-1");
		}
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(wIFFinanceScheduleDetail);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	@Override
	public List<FinanceScheduleDetail> getWIFFinScheduleDetails(String id,
			String type) {
		
		FinanceScheduleDetail wIFFinanceScheduleDetail = new FinanceScheduleDetail();		
		wIFFinanceScheduleDetail.setId(id);
		
		StringBuilder selectSql = new StringBuilder("Select FinReference, SchDate, SchSeq, PftOnSchDate,");
		selectSql.append(" CpzOnSchDate, RepayOnSchDate, RvwOnSchDate, DisbOnSchDate,");
		selectSql.append(" DownpaymentOnSchDate, BalanceForPftCal, BaseRate, SplRate, ActRate, NoOfDays,");
		selectSql.append(" DayFactor, ProfitCalc, ProfitSchd, PrincipalSchd, RepayAmount, ProfitBalance,");
		selectSql.append(" DisbAmount, DownPaymentAmount, CpzAmount,");
		selectSql.append(" ClosingBalance, ProfitFraction, PrvRepayAmount, ");
		selectSql.append(" SchdPriPaid, SchdPftPaid, IsSchdPriPaid, IsSchdPftPaid,Specifier,");
		selectSql.append(" DefSchdDate,");
		

		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(" lovDescBaseRateName,lovDescSplRateName,");
		}
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId,");
		selectSql.append(" NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From WIFFinScheduleDetails");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(wIFFinanceScheduleDetail);
		RowMapper<FinanceScheduleDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(
				FinanceScheduleDetail.class);
		
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	
}