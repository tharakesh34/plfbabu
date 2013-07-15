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
 * FileName    		:  OverdueChargeRecoveryDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  11-05-2012    														*
 *                                                                  						*
 * Modified Date    :  11-05-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 11-05-2012       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.financemanagement.impl;

import java.math.BigDecimal;
import java.util.Date;
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

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.financemanagement.OverdueChargeRecoveryDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.financemanagement.OverdueChargeRecovery;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * DAO methods implementation for the <b>OverdueChargeRecovery model</b> class.<br>
 * 
 */
public class OverdueChargeRecoveryDAOImpl extends BasisCodeDAO<OverdueChargeRecovery> implements OverdueChargeRecoveryDAO {

	private static Logger logger = Logger.getLogger(OverdueChargeRecoveryDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	/**
	 * This method set the Work Flow id based on the module name and return the new OverdueChargeRecovery 
	 * @return OverdueChargeRecovery
	 */
	@Override
	public OverdueChargeRecovery getOverdueChargeRecovery() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("OverdueChargeRecovery");
		OverdueChargeRecovery overdueChargeRecovery= new OverdueChargeRecovery();
		if (workFlowDetails!=null){
			overdueChargeRecovery.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return overdueChargeRecovery;
	}

	/**
	 * This method get the module from method getOverdueChargeRecovery() and 
	 * set the new record flag as true and return OverdueChargeRecovery()
	 * 
	 * @return OverdueChargeRecovery
	 */
	@Override
	public OverdueChargeRecovery getNewOverdueChargeRecovery() {
		logger.debug("Entering");
		OverdueChargeRecovery overdueChargeRecovery = getOverdueChargeRecovery();
		overdueChargeRecovery.setNewRecord(true);
		logger.debug("Leaving");
		return overdueChargeRecovery;
	}

	/**
	 * Fetch the Record  Overdue Charge Recovery details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return OverdueChargeRecovery
	 */
	@Override
	public OverdueChargeRecovery getOverdueChargeRecoveryById(final String id,Date finSchDate,
			String finOdFor, String type) {
		logger.debug("Entering");
		OverdueChargeRecovery overdueChargeRecovery = getOverdueChargeRecovery();
		
		overdueChargeRecovery.setId(id);
		overdueChargeRecovery.setFinSchdDate(finSchDate);
		overdueChargeRecovery.setFinODFor(finOdFor);
		
		StringBuilder selectSql = new StringBuilder("Select FinReference, FinSchdDate, FinODFor,");
		selectSql.append(" FinBranch, FinType, FinCustId, FinCcy, FinODDate, FinODPri, FinODPft,");
		selectSql.append(" FinODTot, FinODCRuleCode, FinODCPLAc, FinODCCAc, FinODCPLShare,");
		selectSql.append(" FinODCSweep, FinODCCustCtg, FinODCType, FinODCOn, FinODC,");
		selectSql.append(" FinODCGraceDays, FinODCAlwWaiver, FinODCMaxWaiver, FinODCPenalty,");
		selectSql.append(" FinODCWaived, FinODCPLPenalty, FinODCCPenalty, FinODCPaid, FinODCWaiverPaid,");
		selectSql.append(" FinODCLastPaidDate, FinODCRecoverySts,");
		selectSql.append(" (SELECT SUM(FinODCWaived) From FinODCRecovery WHERE  FinReference =:FinReference)");
		selectSql.append(" AS lovDescTotOvrDueChrgWaived ,");
		selectSql.append(" (SELECT SUM(FinODCPenalty) From FinODCRecovery WHERE  FinReference =:FinReference)");
		selectSql.append(" AS lovDescTotOvrDueChrg,");
		selectSql.append(" (SELECT SUM(FinODCPaid) From FinODCRecovery WHERE  FinReference =:FinReference)");
		selectSql.append(" AS lovDescTotOvrDueChrgPaid");
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(" ,lovDescFinFormatter, lovDescCustCIF, lovDescCustShrtName, lovDescFinStartDate,");
			selectSql.append(" lovDescMaturityDate, lovDescFinAmount, lovDescCurFinAmt");
		}
		selectSql.append(" From FinODCRecovery");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference AND FinSchdDate = :FinSchdDate AND FinODFor = :FinODFor");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(overdueChargeRecovery);
		RowMapper<OverdueChargeRecovery> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(
				OverdueChargeRecovery.class);

		try{
			overdueChargeRecovery = this.namedParameterJdbcTemplate.queryForObject(
					selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			overdueChargeRecovery = null;
		}
		logger.debug("Leaving");
		return overdueChargeRecovery;
	}
	
	/**
	 * This method initialise the Record.
	 * @param OverdueChargeRecovery (overdueChargeRecovery)
 	 * @return OverdueChargeRecovery
	 */
	@Override
	public void initialize(OverdueChargeRecovery overdueChargeRecovery) {
		super.initialize(overdueChargeRecovery);
	}
	
	/**
	 * This method refresh the Record.
	 * @param OverdueChargeRecovery (overdueChargeRecovery)
 	 * @return void
	 */
	@Override
	public void refresh(OverdueChargeRecovery overdueChargeRecovery) {
		
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/**
	 * This method Deletes the Record from the FinODCRecovery or FinODCRecovery_Temp.
	 * if Record not deleted then throws DataAccessException with  error  41003.
	 * delete Overdue Charge Recovery by key FinReference
	 * 
	 * @param Overdue Charge Recovery (overdueChargeRecovery)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	public void delete(OverdueChargeRecovery overdueChargeRecovery,String type) {
		logger.debug("Entering");
		int recordCount = 0;
		
		StringBuilder deleteSql = new StringBuilder("Delete From FinODCRecovery");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FinReference =:FinReference");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(overdueChargeRecovery);
		try{
			recordCount = this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				ErrorDetails errorDetails= getError("41003",overdueChargeRecovery.getId() ,
						overdueChargeRecovery.getUserDetails().getUsrLanguage());
				throw new DataAccessException(errorDetails.getError()) {};
			}
		}catch(DataAccessException e){
			logger.error(e);
			ErrorDetails errorDetails= getError("41006",overdueChargeRecovery.getId() ,
					overdueChargeRecovery.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	/**
	 * This method insert new Records into FinODCRecovery or FinODCRecovery_Temp.
	 *
	 * save Overdue Charge Recovery 
	 * 
	 * @param Overdue Charge Recovery (overdueChargeRecovery)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public String save(OverdueChargeRecovery overdueChargeRecovery,String type) {
		logger.debug("Entering");
		
		StringBuilder insertSql =new StringBuilder("Insert Into FinODCRecovery");
		insertSql.append(" (FinReference, FinSchdDate, FinODFor, FinBranch, FinType," );
		insertSql.append(" FinCustId, FinCcy, FinODDate, FinODPri, FinODPft, FinODTot, FinODCRuleCode," );
		insertSql.append(" FinODCPLAc, FinODCCAc, FinODCPLShare, FinODCSweep, FinODCCustCtg," );
		insertSql.append(" FinODCType, FinODCOn, FinODC, FinODCGraceDays, FinODCAlwWaiver," );
		insertSql.append(" FinODCMaxWaiver, FinODCPenalty, FinODCWaived, FinODCPLPenalty," );
		insertSql.append(" FinODCCPenalty, FinODCPaid, FinODCWaiverPaid, FinODCLastPaidDate, FinODCRecoverySts )");
		insertSql.append(" Values(:FinReference, :FinSchdDate, :FinODFor, :FinBranch, :FinType," );
		insertSql.append(" :FinCustId, :FinCcy, :FinODDate, :FinODPri, :FinODPft, :FinODTot," );
		insertSql.append(" :FinODCRuleCode, :FinODCPLAc, :FinODCCAc, :FinODCPLShare, :FinODCSweep," );
		insertSql.append(" :FinODCCustCtg, :FinODCType, :FinODCOn, :FinODC, :FinODCGraceDays," );
		insertSql.append(" :FinODCAlwWaiver, :FinODCMaxWaiver, :FinODCPenalty, :FinODCWaived," );
		insertSql.append(" :FinODCPLPenalty, :FinODCCPenalty, :FinODCPaid, :FinODCWaiverPaid, :FinODCLastPaidDate, :FinODCRecoverySts )");
		
		logger.debug("insertSql: " + insertSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(overdueChargeRecovery);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return overdueChargeRecovery.getId();
	}
	
	/**
	 * This method updates the Record FinODCRecovery or FinODCRecovery_Temp.
	 * if Record not updated then throws DataAccessException with  error  41004.
	 * update Overdue Charge Recovery by key FinReference and Version
	 * 
	 * @param Overdue Charge Recovery (overdueChargeRecovery)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@SuppressWarnings("serial")
	@Override
	public void update(OverdueChargeRecovery overdueChargeRecovery,String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update FinODCRecovery");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set FinODCPaid = :FinODCPaid, FinODCLastPaidDate = :FinODCLastPaidDate,");
		updateSql.append(" FinODCWaived =:FinODCWaived, FinODCWaiverPaid =:FinODCWaiverPaid,FinODCRecoverySts = :FinODCRecoverySts");
		updateSql.append(" Where FinReference =:FinReference AND FinSchdDate = :FinSchdDate" );
		updateSql.append(" AND FinODFor = :FinODFor");
		
		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(overdueChargeRecovery);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :"+recordCount);
			ErrorDetails errorDetails= getError("41004",overdueChargeRecovery.getId() ,
					overdueChargeRecovery.getUserDetails().getUsrLanguage());
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Method for getting Pending OverDue Amount basrd on FinReference
	 */
	public BigDecimal getPendingODCAmount(final String id) {
		logger.debug("Entering");
		OverdueChargeRecovery overdueChargeRecovery = getOverdueChargeRecovery();
		
		overdueChargeRecovery.setId(id);
		StringBuilder selectSql = new StringBuilder("SELECT SUM(FinODCPenalty - FinODCWaived - FinODCPaid) As PendingODC ");
		selectSql.append(" From FinODCRecovery");
		selectSql.append(" Where FinReference =:FinReference AND FinODCRecoverySts <> 'C'");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(overdueChargeRecovery);
		RowMapper<OverdueChargeRecovery> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(OverdueChargeRecovery.class);

		try{
			overdueChargeRecovery = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			overdueChargeRecovery = null;
		}
		
		if (overdueChargeRecovery ==null) {
			return new BigDecimal(0);
		} 
		logger.debug("Leaving");
		return overdueChargeRecovery.getPendingODC();
	}
	
	@Override
    public List<String> getOverDueFinanceList() {
		logger.debug("Entering");
		
		StringBuilder selectSql = new StringBuilder("Select DISTINCT FinReference from FinODDetails ");
		selectSql.append(" WHERE FinCurODDays > 0  AND FinCurODAmt <> 0 ");
		
		logger.debug("selectSql: " + selectSql.toString());
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.queryForList(selectSql.toString(),
				new BeanPropertySqlParameterSource(""), String.class);
    }
	
	
	private ErrorDetails  getError(String errorId, String FinReference, String userLanguage){
		String[][] parms= new String[2][1];
		parms[1][0] = FinReference;
		parms[0][0] = PennantJavaUtil.getLabel("label_FinReference")+ ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, 
				errorId, parms[0],parms[1]), userLanguage);
	}

}