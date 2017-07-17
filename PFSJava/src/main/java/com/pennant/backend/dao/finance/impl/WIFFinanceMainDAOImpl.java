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
 * FileName    		:  WIFFinanceMainDAOImpl.java                                                   * 	  
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

import com.pennant.backend.dao.finance.WIFFinanceMainDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pff.core.DependencyFoundException;

/**
 * DAO methods implementation for the <b>FinanceMain model</b> class.<br>
 * 
 */

public class WIFFinanceMainDAOImpl extends BasisCodeDAO<FinanceMain> implements WIFFinanceMainDAO {

	private static Logger logger = Logger.getLogger(WIFFinanceMainDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	public WIFFinanceMainDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record  What If Finance Main Detail details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return WIFFinanceMain
	 */
	@Override
	public FinanceMain getWIFFinanceMainById(final String id, String type) {
		logger.debug("Entering");
		FinanceMain wIFFinanceMain = new FinanceMain();
		
		
		wIFFinanceMain.setId(id);
		StringBuilder selectSql = new StringBuilder("SELECT FinReference, NumberOfTerms, GrcPeriodEndDate, AllowGrcPeriod,");
		selectSql.append(" GraceBaseRate, GraceSpecialRate, GrcPftRate, GrcPftFrq,NextGrcPftDate, AllowGrcPftRvw,");
		selectSql.append(" GrcPftRvwFrq, NextGrcPftRvwDate, AllowGrcCpz, GrcCpzFrq, NextGrcCpzDate,RepayBaseRate,");
		selectSql.append(" RepaySpecialRate, RepayProfitRate, RepayFrq, NextRepayDate, RepayPftFrq, NextRepayPftDate,");
		selectSql.append(" AllowRepayRvw,RepayRvwFrq, NextRepayRvwDate, AllowRepayCpz, RepayCpzFrq, NextRepayCpzDate,");
		selectSql.append(" MaturityDate, CpzAtGraceEnd,DownPayment, ReqRepayAmount, TotalProfit,");
		selectSql.append(" TotalGrcProfit, GrcRateBasis, RepayRateBasis, FinType,FinRemarks, FinCcy, ScheduleMethod,");
		selectSql.append(" ProfitDaysBasis, ReqMaturity, CalTerms, CalMaturity, FirstRepay, LastRepay,"); 
		selectSql.append(" FinStartDate, FinAmount, FinRepaymentAmount, CustID, Defferments, FinIsActive,");
		selectSql.append(" FinBranch, FinSourceID, AllowedDefRpyChange, AvailedDefRpyChange, AllowedDefFrqChange,"); 
		selectSql.append(" AvailedDefFrqChange, RecalType, MinDownPayPerc, FinCategory, ProductCategory, ");


		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append("lovDescFinFormatter,lovDescGraceBaseRateName,lovDescGraceSpecialRateName,");
			selectSql.append(" lovDescRepayBaseRateName,lovDescRepaySpecialRateName,lovDescFinTypeName,");
			selectSql.append(" lovDescFinCcyName, lovDescFinTypeName,");
		}
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, ");
		selectSql.append(" NextTaskId, RecordType, WorkflowId");

		selectSql.append(" From WIFFinanceMain");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(wIFFinanceMain);
		RowMapper<FinanceMain> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceMain.class);
		
		try{
			wIFFinanceMain = this.namedParameterJdbcTemplate.queryForObject(
					selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			wIFFinanceMain = null;
		}
		logger.debug("Leaving");
		return wIFFinanceMain;
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the WIFFinanceMain or WIFFinanceMain_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete What If Finance Main Detail by key FinReference
	 * 
	 * @param What If Finance Main Detail (wIFFinanceMain)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(FinanceMain wIFFinanceMain,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete From WIFFinanceMain");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FinReference =:FinReference");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(wIFFinanceMain);
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
	 * This method insert new Records into WIFFinanceMain or WIFFinanceMain_Temp.
	 *
	 * save What If Finance Main Detail 
	 * 
	 * @param What If Finance Main Detail (wIFFinanceMain)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@Override
	public String save(FinanceMain wIFFinanceMain,String type) {
		logger.debug("Entering");
		
		StringBuilder insertSql =new StringBuilder("Insert Into WIFFinanceMain");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (FinReference, NumberOfTerms, GrcPeriodEndDate, AllowGrcPeriod,");
		insertSql.append(" GraceBaseRate, GraceSpecialRate, GrcPftRate, GrcPftFrq,NextGrcPftDate, AllowGrcPftRvw,");
		insertSql.append(" GrcPftRvwFrq, NextGrcPftRvwDate, AllowGrcCpz, GrcCpzFrq, NextGrcCpzDate,RepayBaseRate,");
		insertSql.append(" RepaySpecialRate, RepayProfitRate, RepayFrq, NextRepayDate, RepayPftFrq, NextRepayPftDate,");
		insertSql.append(" AllowRepayRvw,RepayRvwFrq, NextRepayRvwDate, AllowRepayCpz, RepayCpzFrq, NextRepayCpzDate,");
		insertSql.append(" MaturityDate, CpzAtGraceEnd,DownPayment, ReqRepayAmount, TotalProfit,");
		insertSql.append(" TotalGrcProfit, GrcRateBasis, RepayRateBasis, FinType,FinRemarks, FinCcy, ScheduleMethod,");
		insertSql.append(" ProfitDaysBasis, ReqMaturity, CalTerms, CalMaturity, FirstRepay, LastRepay,"); 
		insertSql.append(" FinStartDate, FinAmount, FinRepaymentAmount, CustID, Defferments,");
		insertSql.append(" FinBranch, FinSourceID, AllowedDefRpyChange, AvailedDefRpyChange, AllowedDefFrqChange,"); 
		insertSql.append(" AvailedDefFrqChange, RecalType, FinIsActive, FinCategory, ProductCategory,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId,");
		insertSql.append(" NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:FinReference, :NumberOfTerms, :GrcPeriodEndDate, :AllowGrcPeriod,");
		insertSql.append(" :GraceBaseRate, :GraceSpecialRate,:GrcPftRate,:GrcPftFrq,:NextGrcPftDate,:AllowGrcPftRvw,");
		insertSql.append(" :GrcPftRvwFrq,:NextGrcPftRvwDate,:AllowGrcCpz,:GrcCpzFrq,:NextGrcCpzDate,:RepayBaseRate,");
		insertSql.append(" :RepaySpecialRate,:RepayProfitRate,:RepayFrq,:NextRepayDate,:RepayPftFrq,:NextRepayPftDate,");
		insertSql.append(" :AllowRepayRvw,:RepayRvwFrq,:NextRepayRvwDate,:AllowRepayCpz,:RepayCpzFrq,:NextRepayCpzDate,");
		insertSql.append(" :MaturityDate,:CpzAtGraceEnd,:DownPayment,:ReqRepayAmount,:TotalProfit,");
		insertSql.append(" :TotalGrcProfit,:GrcRateBasis,:RepayRateBasis,:FinType,:FinRemarks,:FinCcy,:ScheduleMethod,");
		insertSql.append(" :ProfitDaysBasis,:ReqMaturity,:CalTerms,:CalMaturity,:FirstRepay,:LastRepay,"); 
		insertSql.append(" :FinStartDate,:FinAmount,:FinRepaymentAmount,:CustID,:Defferments,");
		insertSql.append(" :FinBranch, :FinSourceID, :AllowedDefRpyChange, :AvailedDefRpyChange, :AllowedDefFrqChange,"); 
		insertSql.append(" :AvailedDefFrqChange, :RecalType, :FinIsActive, :FinCategory, :ProductCategory");
		insertSql.append(" :Version ,:LastMntBy,:LastMntOn,:RecordStatus,:RoleCode,:NextRoleCode,:TaskId,");
		insertSql.append(" :NextTaskId,:RecordType,:WorkflowId,:MinDownPayPerc)");
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(wIFFinanceMain);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return wIFFinanceMain.getId();
	}
	
	/**
	 * This method updates the Record WIFFinanceMain or WIFFinanceMain_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update What If Finance Main Detail by key FinReference and Version
	 * 
	 * @param What If Finance Main Detail (wIFFinanceMain)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	
	@Override
	public void update(FinanceMain wIFFinanceMain,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update WIFFinanceMain");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set NumberOfTerms = :NumberOfTerms,");
		updateSql.append(" GrcPeriodEndDate = :GrcPeriodEndDate, AllowGrcPeriod = :AllowGrcPeriod,");
		updateSql.append(" GraceBaseRate = :GraceBaseRate, GraceSpecialRate = :GraceSpecialRate,");
		updateSql.append(" GrcPftRate = :GrcPftRate, GrcPftFrq = :GrcPftFrq,");
		updateSql.append(" NextGrcPftDate = :NextGrcPftDate, AllowGrcPftRvw = :AllowGrcPftRvw,");
		updateSql.append(" GrcPftRvwFrq = :GrcPftRvwFrq, NextGrcPftRvwDate = :NextGrcPftRvwDate,");
		updateSql.append(" AllowGrcCpz = :AllowGrcCpz, GrcCpzFrq = :GrcCpzFrq, NextGrcCpzDate = :NextGrcCpzDate,");
		updateSql.append(" RepayBaseRate = :RepayBaseRate, RepaySpecialRate = :RepaySpecialRate,");
		updateSql.append(" RepayProfitRate = :RepayProfitRate, RepayFrq = :RepayFrq, NextRepayDate = :NextRepayDate,");
		updateSql.append(" RepayPftFrq = :RepayPftFrq, NextRepayPftDate = :NextRepayPftDate,");
		updateSql.append(" AllowRepayRvw = :AllowRepayRvw, RepayRvwFrq = :RepayRvwFrq,");
		updateSql.append(" NextRepayRvwDate = :NextRepayRvwDate, AllowRepayCpz = :AllowRepayCpz,");
		updateSql.append(" RepayCpzFrq = :RepayCpzFrq, NextRepayCpzDate = :NextRepayCpzDate,");
		updateSql.append(" MaturityDate = :MaturityDate, CpzAtGraceEnd = :CpzAtGraceEnd, DownPayment = :DownPayment,");
		updateSql.append(" ReqRepayAmount = :ReqRepayAmount, TotalProfit = :TotalProfit, TotalGrcProfit = :TotalGrcProfit,");
		updateSql.append(" GrcRateBasis = :GrcRateBasis, RepayRateBasis = :RepayRateBasis, FinType = :FinType,");
		updateSql.append(" FinRemarks = :FinRemarks, FinCcy = :FinCcy, ScheduleMethod = :ScheduleMethod,");
		updateSql.append(" ProfitDaysBasis = :ProfitDaysBasis, ReqMaturity = :ReqMaturity, CalTerms = :CalTerms,");
		updateSql.append(" CalMaturity = :CalMaturity, FirstRepay = :FirstRepay, LastRepay = :LastRepay,");
		updateSql.append(" FinStartDate = :FinStartDate, FinAmount = :FinAmount,");
		updateSql.append(" FinRepaymentAmount = :FinRepaymentAmount, CustID = :CustID,");
		updateSql.append(" Defferments = :Defferments, FinBranch =:FinBranch, FinSourceID= :FinSourceID,");
		updateSql.append(" AllowedDefRpyChange= :AllowedDefRpyChange, AvailedDefRpyChange= :AvailedDefRpyChange,");
		updateSql.append(" AllowedDefFrqChange= :AllowedDefFrqChange, AvailedDefFrqChange= :AvailedDefFrqChange,");
		updateSql.append(" RecalType=:RecalType, FinIsActive= :FinIsActive,FinCategory = :FinCategory, ProductCategory=:ProductCategory");
		updateSql.append(", Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode,");
		updateSql.append(" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId, MinDownPayPerc=:MinDownPayPerc");
		updateSql.append(" Where FinReference =:FinReference");
		
		logger.debug("updateSql: " + updateSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(wIFFinanceMain);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}
	
}