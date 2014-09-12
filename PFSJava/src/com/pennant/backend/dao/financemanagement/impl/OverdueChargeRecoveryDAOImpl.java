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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		OverdueChargeRecovery overdueChargeRecovery = new OverdueChargeRecovery();
		
		overdueChargeRecovery.setId(id);
		overdueChargeRecovery.setFinODSchdDate(finSchDate);
		overdueChargeRecovery.setFinODFor(finOdFor);
		
		StringBuilder selectSql = new StringBuilder("Select FinReference, FinODSchdDate, FinODFor, MovementDate, SeqNo,");
		selectSql.append(" ODDays, FinCurODAmt, FinCurODPri, FinCurODPft, PenaltyType, PenaltyCalOn,");
		selectSql.append(" PenaltyAmtPerc, Penalty, MaxWaiver, WaivedAmt, PenaltyPaid, PenaltyBal, RcdCanDel");
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(" ,lovDescFinFormatter, lovDescCustCIF, lovDescCustShrtName, lovDescFinStartDate,");
			selectSql.append(" lovDescMaturityDate, lovDescFinAmount, lovDescCurFinAmt");
		}
		selectSql.append(" From FinODCRecovery");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference AND FinODSchdDate = :FinODSchdDate AND FinODFor = :FinODFor");
		
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
	 * Fetch the Record  Overdue Charge Recovery details by key field
	 * 
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return OverdueChargeRecovery
	 */
	@Override
	public List<OverdueChargeRecovery> getOverdueChargeRecoveryByRef(final String finRef, String type) {
		logger.debug("Entering");
		OverdueChargeRecovery overdueChargeRecovery = new OverdueChargeRecovery();
		overdueChargeRecovery.setFinReference(finRef);
		
		StringBuilder selectSql = new StringBuilder("Select FinReference, FinODSchdDate, FinODFor, MovementDate, SeqNo,");
		selectSql.append(" ODDays, FinCurODAmt, FinCurODPri, FinCurODPft, PenaltyType, PenaltyCalOn,");
		selectSql.append(" PenaltyAmtPerc, Penalty, MaxWaiver, WaivedAmt, PenaltyPaid, PenaltyBal, RcdCanDel");
		if(StringUtils.trimToEmpty(type).contains("View")){
			selectSql.append(" ,lovDescFinFormatter");
		}
		selectSql.append(" From FinODCRecovery");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(overdueChargeRecovery);
		RowMapper<OverdueChargeRecovery> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(
				OverdueChargeRecovery.class);
	
		logger.debug("Leaving");
		return	this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);	
	}
	
	/**
	 * Fetch the Record  Overdue Charge Recovery details by key field
	 * 
	 * @param finReference (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return OverdueChargeRecovery
	 */
	@Override
	public OverdueChargeRecovery getMaxOverdueChargeRecoveryById(final String finReference,Date finSchDate,
			String finOdFor, String type) {
		logger.debug("Entering");
		OverdueChargeRecovery overdueChargeRecovery = new OverdueChargeRecovery();
		
		overdueChargeRecovery.setId(finReference);
		overdueChargeRecovery.setFinODSchdDate(finSchDate);
		overdueChargeRecovery.setFinODFor(finOdFor);
		
		StringBuilder selectSql = new StringBuilder("Select FinReference, FinODSchdDate, FinODFor, MovementDate,SeqNo,");
		selectSql.append(" Penalty, WaivedAmt, PenaltyPaid, PenaltyBal, RcdCanDel");
		selectSql.append(" From FinODCRecovery");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference AND FinODSchdDate = :FinODSchdDate AND FinODFor = :FinODFor");
		
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
	 * Method for Deletion of overdue Recovery Details records if when Overdue will not happen
	 */
	@Override
	public void deleteUnpaid(String finReference , Date finODSchDate, String finODFor, String type) {
		logger.debug("Entering");
		
		OverdueChargeRecovery recovery = new OverdueChargeRecovery();
		recovery.setFinReference(finReference);
		recovery.setFinODSchdDate(finODSchDate);
		recovery.setFinODFor(finODFor);
		
		StringBuilder deleteSql = new StringBuilder("Delete From FinODCRecovery");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FinReference =:FinReference AND FinODSchdDate= :FinODSchdDate AND FinODFor= :FinODFor AND RcdCanDel = 1");
		logger.debug("deleteSql: " + deleteSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(recovery);
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);
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
		insertSql.append(" (FinReference, FinODSchdDate, FinODFor, MovementDate,SeqNo, ODDays, FinCurODAmt," );
		insertSql.append(" FinCurODPri, FinCurODPft, PenaltyType, PenaltyCalOn, PenaltyAmtPerc, Penalty," );
		insertSql.append(" MaxWaiver, WaivedAmt, PenaltyPaid, PenaltyBal, RcdCanDel)" );
		insertSql.append(" Values( :FinReference, :FinODSchdDate, :FinODFor, :MovementDate, :SeqNo, :ODDays, :FinCurODAmt," );
		insertSql.append(" :FinCurODPri, :FinCurODPft, :PenaltyType, :PenaltyCalOn, :PenaltyAmtPerc," );
		insertSql.append(" :Penalty, :MaxWaiver, :WaivedAmt, :PenaltyPaid, :PenaltyBal, :RcdCanDel)" );
			
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
	public void update(OverdueChargeRecovery overdueChargeRecovery, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update FinODCRecovery");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set FinReference= :FinReference, FinODSchdDate= :FinODSchdDate, FinODFor= :FinODFor, " );
		updateSql.append(" MovementDate= :MovementDate,SeqNo=:SeqNo, ODDays= :ODDays, FinCurODAmt= :FinCurODAmt, FinCurODPri= :FinCurODPri," );
		updateSql.append(" FinCurODPft= :FinCurODPft, PenaltyType= :PenaltyType, PenaltyCalOn= :PenaltyCalOn, PenaltyAmtPerc= :PenaltyAmtPerc,");
		updateSql.append(" Penalty= :Penalty, MaxWaiver= :MaxWaiver, WaivedAmt= :WaivedAmt, PenaltyPaid= :PenaltyPaid, PenaltyBal= :PenaltyBal, RcdCanDel= :RcdCanDel");
		updateSql.append(" Where FinReference =:FinReference AND FinSchdDate = :FinSchdDate" );
		updateSql.append(" AND FinODFor = :FinODFor AND MovementDate= :MovementDate");
		
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
	 * Method for Overdue Recovery Details Updation on Postings
	 */
	@SuppressWarnings("serial")
    @Override
    public void updatePenaltyPaid(OverdueChargeRecovery recovery, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder	updateSql =new StringBuilder("Update FinODCRecovery");
		updateSql.append(StringUtils.trimToEmpty(type)); 
		updateSql.append(" Set WaivedAmt= :WaivedAmt, PenaltyPaid= :PenaltyPaid, " );
		updateSql.append(" PenaltyBal= (PenaltyBal - :PenaltyBal), RcdCanDel= :RcdCanDel");
		updateSql.append(" Where FinReference =:FinReference AND FinODSchdDate = :FinODSchdDate" );
		updateSql.append(" AND FinODFor = :FinODFor AND MovementDate= :MovementDate AND RcdCanDel =1");
		
		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(recovery);
		recordCount = this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);
		
		if (recordCount <= 0) {
			logger.debug("Error Update Method Count :"+recordCount);
			ErrorDetails errorDetails= getError("41004",recovery.getFinReference() , "EN" );//TODO
			throw new DataAccessException(errorDetails.getError()) {};
		}
		logger.debug("Leaving");	    
    }
	
	/**
	 * Method for getting Pending OverDue Amount based on FinReference
	 */
	public BigDecimal getPendingODCAmount(final String id) {
		logger.debug("Entering");
		OverdueChargeRecovery overdueChargeRecovery = new OverdueChargeRecovery();
		
		overdueChargeRecovery.setId(id);
		StringBuilder selectSql = new StringBuilder("SELECT SUM(TotPenaltyBal) As PendingODC ");
		selectSql.append(" From FinODDetails");
		selectSql.append(" Where FinReference =:FinReference ");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(overdueChargeRecovery);
		RowMapper<OverdueChargeRecovery> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(OverdueChargeRecovery.class);

		try{
			overdueChargeRecovery = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			overdueChargeRecovery = null;
		}
		
		if (overdueChargeRecovery ==null) {
			return BigDecimal.ZERO;
		} 
		logger.debug("Leaving");
		return overdueChargeRecovery.getPendingODC();
	}
	
	@Override
    public List<String> getOverDueFinanceList() {//FIXME
		logger.debug("Entering");
		
		StringBuilder selectSql = new StringBuilder("Select DISTINCT FinReference from FinODDetails ");
		selectSql.append(" WHERE FinCurODDays > GraceDays ");
		
		logger.debug("selectSql: " + selectSql.toString());
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.queryForList(selectSql.toString(),
				new BeanPropertySqlParameterSource(""), String.class);
    }
	
	/**
	 * Method for Paid Penalty Details using Key: FinRefrence
	 */
	@Override
    public List<OverdueChargeRecovery> getFinancePenaltysByFinRef(String finReference, String type) {
		logger.debug("Entering");
		OverdueChargeRecovery overdueChargeRecovery = new OverdueChargeRecovery();
		overdueChargeRecovery.setId(finReference);
		
		StringBuilder selectSql = new StringBuilder("Select FinReference, FinODSchdDate, MovementDate, PenaltyPaid ");
		selectSql.append(" From FinODCRecovery");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference AND PenaltyPaid > 0 ORDER BY FinODSchdDate, MovementDate, SeqNo ");
		
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(overdueChargeRecovery);
		RowMapper<OverdueChargeRecovery> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(
				OverdueChargeRecovery.class);

		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);	
    }
	
	@Override
	public BigDecimal getPaidPenaltiesbySchDates(String finReference, List<Date> pastSchDates) {
		logger.debug("Entering");
		
		Map<String,List<Date>> map=new HashMap<String, List<Date>>();
		map.put("PastSchDates", pastSchDates);
		
		StringBuilder selectSql = new StringBuilder("Select ISNULL(SUM(PenaltyPaid),0) ");
		selectSql.append(" From FinODCRecovery ");
		selectSql.append(" WITH(NOLOCK)  Where FinReference = '" );
		selectSql.append(finReference);
		selectSql.append( "' AND FinOdSchdDate IN (:PastSchDates) AND RcdCanDel = 0 AND PenaltyPaid > 0 ");
		
		logger.debug("selectSql: " + selectSql.toString());
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), map, BigDecimal.class);	
	}
	
	/**
	 * Method for Saving History Details while Processing Past due Deferment Case
	 */
	@Override
	public void saveODDeferHistory(String finReference, List<Date> pastSchDates) {
		logger.debug("Entering");
		
		Map<String,List<Date>> map=new HashMap<String, List<Date>>();
		map.put("PastSchDates", pastSchDates);
		
		StringBuilder selectSql = new StringBuilder("INSERT INTO FinODCRecovery_PD ");
		selectSql.append(" Select * From FinODCRecovery ");
		selectSql.append(" WITH(NOLOCK)  Where FinReference = '" );
		selectSql.append(finReference);
		selectSql.append( "' AND FinOdSchdDate IN (:PastSchDates) ");
		
		logger.debug("selectSql: " + selectSql.toString());
		this.namedParameterJdbcTemplate.update(selectSql.toString(), map);	
		logger.debug("Leaving");
	}

	/**
	 * Method for Delete History Details while Processing Past due Deferment Case
	 */
	@Override
    public void deleteODDeferHistory(String finReference, List<Date> pastSchDates) {
		logger.debug("Entering");
		
		Map<String,List<Date>> map=new HashMap<String, List<Date>>();
		map.put("PastSchDates", pastSchDates);
		
		StringBuilder deleteSql = new StringBuilder(" Delete From FinODCRecovery ");
		deleteSql.append("  Where FinReference = '" );
		deleteSql.append(finReference);
		deleteSql.append( "' AND FinOdSchdDate IN (:PastSchDates) ");
		logger.debug("deleteSql: " + deleteSql.toString());
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), map);
		logger.debug("Leaving");
    }
	
	private ErrorDetails  getError(String errorId, String finReference, String userLanguage){
		String[][] parms= new String[2][1];
		parms[1][0] = finReference;
		parms[0][0] = PennantJavaUtil.getLabel("label_FinReference")+ ":" + parms[1][0];
		return ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, errorId, parms[0],parms[1]), userLanguage);
	}


}