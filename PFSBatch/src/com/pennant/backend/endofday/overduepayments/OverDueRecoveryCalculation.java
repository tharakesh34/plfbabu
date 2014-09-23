
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
 *//*

*//**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *
 * FileName    		:  OverDueRecoveryCalculation.java													*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  26-04-2011															*
 *                                                                  
 * Modified Date    :  30-07-2011															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-04-2011       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 *//*
package com.pennant.backend.endofday.overduepayments;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.pennant.app.util.AccountEngineExecution;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.batch.admin.BatchAdminDAO;
import com.pennant.backend.dao.financemanagement.OverdueChargeRecoveryDAO;
import com.pennant.backend.model.financemanagement.OverdueChargeRecovery;

public class OverDueRecoveryCalculation implements Tasklet {

	private Logger logger = Logger.getLogger(OverDueRecoveryCalculation.class);
	
	private AccountEngineExecution engineExecution;
	private OverdueChargeRecoveryDAO recoveryDAO;
	private DataSource dataSource;
	private BatchAdminDAO batchAdminDAO;
	
	private Date dateValueDate = null;

	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {
		
		dateValueDate= DateUtility.getDBDate(SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_VALUE).toString());
		
		logger.debug("START: OverDue Recovery Caluclation for Value Date: "+ dateValueDate);
		
		context.getStepContext().getStepExecution().getExecutionContext().put(context.getStepContext().getStepExecution().getId().toString(), dateValueDate);

		// READ REPAYMENTS DUE TODAY
		Connection connection = null;
		ResultSet resultSet = null;
		PreparedStatement sqlStatement = null;
		StringBuffer selQuery = new StringBuffer();
		
		try {
			
			// Fetch OverDue Details Data
			selQuery = prepareODDetailsQuery(selQuery);
			connection = DataSourceUtils.doGetConnection(getDataSource());
			sqlStatement = connection.prepareStatement(selQuery.toString());
			resultSet = sqlStatement.executeQuery();
			
			while (resultSet.next()) {
				
				//Date Check for OverDue Recovery
				Date dateCheck = DateUtility.addDays(resultSet.getDate("FinODSchdDate"), resultSet.getInt("ODGraceDays"));
				if(dateCheck.compareTo(dateValueDate) <= 0){

					//Fetch Customer Category using FinReference from ODDetails
					selQuery = prepareScheduleQuery(new StringBuffer(),resultSet.getString("FinReference"),
							resultSet.getDate("FinODSchdDate"));
					sqlStatement = connection.prepareStatement(selQuery.toString());
					ResultSet rs = sqlStatement.executeQuery();
					
					//Prepare Finance OverDue Recovery Details Object Data
					OverdueChargeRecovery chargeRecovery = null;
					
					//Insert OverDue Recovery(s) into Recoveries table
					while (rs.next()) {
						chargeRecovery = saveOrUpdateODRData(resultSet,rs);
						context.getStepContext().getStepExecution().getExecutionContext().putInt("FIELD_COUNT", resultSet.getRow());
						getBatchAdminDAO().saveStepDetails(resultSet.getString("FinReference"), getODChargeRecory(chargeRecovery), context.getStepContext().getStepExecution().getId());
					}
					
					rs.close();
				}
			}

		}catch (SQLException e) {
			logger.error(e);
			throw new SQLException(e.getMessage()) {};
		}finally{
			resultSet.close();
			sqlStatement.close();
		}
		
		logger.debug("COMPLETED: OverDue Recovery Caluclation for Value Date: "+ dateValueDate);
		return RepeatStatus.FINISHED;
	}

	*//**
	 * Method for Preparing OverDue Recovery Object data
	 * @param oDDRS
	 * @param schdlRS
	 * @param custCtgCode
	 * @param overDueRuleCode
	 * @param work
	 * @return
	 * @throws SQLException 
	 *//*
	@SuppressWarnings("serial")
	private OverdueChargeRecovery saveOrUpdateODRData(ResultSet oDDRS, ResultSet schdlRS) throws SQLException {
		logger.debug("Entering");

		// If only when Finance Charge type is based upon Penalty Due days
		OverdueChargeRecovery  recovery = null;
		boolean isRcdNotFound = true;
		BigDecimal prvPenaltyBal = BigDecimal.ZERO;
		if(PennantConstants.PERCONDUEDAYS.equals(oDDRS.getString("ODChargeType"))){
			recovery = getRecoveryDAO().getMaxOverdueChargeRecoveryById(oDDRS.getString("FinReference"), oDDRS.getDate("FinODSchdDate"),
					oDDRS.getString("FinODFor"), "");
			
			//Set Data For creation of new object or for Updation
			if(recovery == null){
				recovery = new OverdueChargeRecovery();
				recovery.setSeqOrder(1);
			}else{
				
				//Check for Condition whether new record must add or not 
				if(recovery.getFinODCPenaltyPaid().compareTo(BigDecimal.ZERO) > 0){
					isRcdNotFound = true;
				}else if(PennantConstants.SPFT.equals(oDDRS.getString("ODChargeCalOn"))){
					
					if((schdlRS.getBigDecimal("ProfitSchd").subtract(schdlRS.getBigDecimal("SchdPftPaid"))).compareTo(
							recovery.getFinODPft()) < 0){
						isRcdNotFound = true;
					}
				}else if(PennantConstants.SPRI.equals(oDDRS.getString("ODChargeCalOn"))){
					
					if((schdlRS.getBigDecimal("PrincipalSchd").subtract(schdlRS.getBigDecimal("SchdPriPaid"))).compareTo(
							recovery.getFinODPri()) < 0){
						isRcdNotFound = true;
					}
				}else if(PennantConstants.STOT.equals(oDDRS.getString("ODChargeCalOn"))){

					if((schdlRS.getBigDecimal("PrincipalSchd").subtract(schdlRS.getBigDecimal("SchdPriPaid")).add(
							schdlRS.getBigDecimal("ProfitSchd").subtract(schdlRS.getBigDecimal("SchdPftPaid")))).compareTo(
							recovery.getFinODTot()) < 0){
						isRcdNotFound = true;
					}
				}else{
					isRcdNotFound = false;
				}
				
				//Reset Data For new Overdue RecoveryRecord
				if(isRcdNotFound){
					recovery.setSeqOrder(recovery.getSeqOrder()+1);
					recovery.setoDDays(0);
					prvPenaltyBal = recovery.getFinODCPenaltyBal(); 
				}
			}
			
		} else{
			recovery = new OverdueChargeRecovery();
			recovery.setSeqOrder(1);
		}
		
		try {

			if(isRcdNotFound){
				recovery.setFinReference(oDDRS.getString("FinReference"));
				recovery.setFinSchdDate(oDDRS.getDate("FinODSchdDate"));
				recovery.setFinODFor(oDDRS.getString("FinODFor"));
				recovery.setFinBranch(oDDRS.getString("FinBranch"));
				recovery.setFinType(oDDRS.getString("FinType"));
				recovery.setFinCustId(oDDRS.getLong("CustID"));
				recovery.setFinCcy(schdlRS.getString("FinCcy"));
				recovery.setFinODDate(dateValueDate);
			}
			
			//For Updation
			recovery.setFinODCalDate(dateValueDate);
			recovery.setoDDays(recovery.getoDDays()+1);

			if(recovery.getFinODFor().equals(PennantConstants.SCHEDULE)){
				recovery.setFinODPri(schdlRS.getBigDecimal("PrincipalSchd").subtract(schdlRS.getBigDecimal("SchdPriPaid")));
				recovery.setFinODPft(schdlRS.getBigDecimal("ProfitSchd").subtract(schdlRS.getBigDecimal("SchdPftPaid")));
			}else if(recovery.getFinODFor().equals(PennantConstants.DEFERED)){
				recovery.setFinODPri(schdlRS.getBigDecimal("DefPrincipal").subtract(schdlRS.getBigDecimal("DefSchdPriPaid")));
				recovery.setFinODPft(schdlRS.getBigDecimal("DefProfit").subtract(schdlRS.getBigDecimal("DefSchdPftPaid")));
			}

			recovery.setFinODTot(recovery.getFinODPri().add(recovery.getFinODPft()));
			
			if(isRcdNotFound){
				recovery.setFinODCRuleCode(oDDRS.getString("AccountSetCode"));
				recovery.setFinODCType(oDDRS.getString("ODChargeType"));
				recovery.setFinODCOn(oDDRS.getString("ODChargeCalOn"));
				recovery.setFinODC(oDDRS.getBigDecimal("ODChargeAmtOrPerc"));
				recovery.setFinODCGraceDays(oDDRS.getInt("ODGraceDays"));
				recovery.setFinODCAlwWaiver(oDDRS.getBoolean("ODAllowWaiver"));
				recovery.setFinODCMaxWaiver(oDDRS.getBigDecimal("ODMaxWaiverPerc"));
			}

			if(recovery.getFinODCType().equals(PennantConstants.FLAT)){
				recovery.setFinODCPenalty(recovery.getFinODC().multiply(schdlRS.getBigDecimal("NumberOfTerms")));
			}else if(recovery.getFinODCType().equals(PennantConstants.PERCONETIME)){

				if(recovery.getFinODCOn().equals(PennantConstants.SPFT)){
					recovery.setFinODCPenalty(getPercentageValue(recovery.getFinODPft(),recovery.getFinODC()));
				}else if(recovery.getFinODCOn().equals(PennantConstants.SPFT)){
					recovery.setFinODCPenalty(getPercentageValue(recovery.getFinODPri(),recovery.getFinODC()));
				}else{
					recovery.setFinODCPenalty(getPercentageValue(recovery.getFinODTot(),recovery.getFinODC()));
				}
			}else if(recovery.getFinODCType().equals(PennantConstants.PERCONDUEDAYS)){

				if(recovery.getFinODCOn().equals(PennantConstants.SPFT)){
					recovery.setFinODCPenalty(getDayPercValue(recovery.getFinODPft(),recovery.getFinODC(),
							recovery.getFinODDate(),dateValueDate,oDDRS.getString("ProfitDaysBasis")));
				}else if(recovery.getFinODCOn().equals(PennantConstants.SPFT)){
					recovery.setFinODCPenalty(getDayPercValue(recovery.getFinODPri(),recovery.getFinODC(),
							recovery.getFinODDate(),dateValueDate,oDDRS.getString("ProfitDaysBasis")));
				}else{
					recovery.setFinODCPenalty(getDayPercValue(recovery.getFinODTot(),recovery.getFinODC(),
							recovery.getFinODDate(),dateValueDate,oDDRS.getString("ProfitDaysBasis")));
				}
				recovery.setFinODCPenalty(recovery.getFinODCPenalty().add(prvPenaltyBal));
			}

			if(isRcdNotFound){
				recovery.setFinODCPenaltyBal(recovery.getFinODCPenalty());
				recovery.setFinODCWaived(BigDecimal.ZERO);
				recovery.setFinODCPenaltyPaid(BigDecimal.ZERO);
			}else{
				recovery.setFinODCPenaltyBal(recovery.getFinODCPenalty().subtract(recovery.getFinODCPenaltyPaid()));
			}
			recovery.setFinODCLastPaidDate(dateValueDate);
			recovery.setFinODCRecoverySts("R");
			
			//Updation for Overdue Recovery Data
			if(isRcdNotFound){
				getRecoveryDAO().save(recovery, "");
			}else{
				getRecoveryDAO().update(recovery,false, "");
			}

		} catch (Exception e) {
			logger.error(e);
			throw new DataAccessException(e.getMessage()) {};
		}
		logger.debug("Leaving");
		return recovery;
	}
	
	*//**
	 * Method for get the Percentage of given value 
	 * @param dividend
	 * @param divider
	 * @return
	 *//*
	private BigDecimal getDayPercValue(BigDecimal dividend, BigDecimal divider, Date odDate, Date dateValueDate, String profitDayBasis){
		BigDecimal value = ((dividend.multiply(unFormateAmount(divider,2).divide(new BigDecimal(100)))).multiply(
				CalculationUtil.getInterestDays(odDate, dateValueDate, profitDayBasis))).divide(new BigDecimal(100),RoundingMode.HALF_DOWN);
		
		return value.setScale(0, RoundingMode.HALF_DOWN);
	}
	
	*//**
	 * Method for get the Percentage of given value 
	 * @param dividend
	 * @param divider
	 * @return
	 *//*
	private BigDecimal getPercentageValue(BigDecimal dividend, BigDecimal divider){
		return (dividend.multiply(unFormateAmount(divider,2).divide(
				new BigDecimal(100)))).divide(new BigDecimal(100),RoundingMode.HALF_DOWN);
	}
	
	*//**
	 * Method for UnFormat the passing Amount Value
	 * @param amount
	 * @param dec
	 * @return
	 *//*
	public BigDecimal unFormateAmount(BigDecimal amount, int dec) {
		if (amount == null) {
			return BigDecimal.ZERO;
		}
		BigInteger bigInteger = amount.multiply(new BigDecimal(Math.pow(10, dec))).toBigInteger();
		return new BigDecimal(bigInteger);
	}
	
	*//**
	 * Method for preparation of Select Query To get OverDueDetails data
	 * 
	 * @param selQuery
	 * @return
	 *//*
	private StringBuffer prepareODDetailsQuery(StringBuffer selQuery) {
		
		selQuery.append(" SELECT ODD.FinReference ,ODD.FinODSchdDate , ODD.FinODFor ,ODD.FinBranch ,ODD.FinType , " );
		selQuery.append(" ODD.CustID ,ODD.FinODTillDate ,ODD.FinCurODAmt , ODD.FinMaxODAmt ,ODD.FinCurODDays ,FM.ProfitDaysBasis, " );
		selQuery.append(" (FM.FinAmount + FM.FeeChargeAmt  - FM.FinRepaymentAmount) As FinAmount,  FM.DownPayment , FM.NumberOfTerms , " );
		selQuery.append(" ACS.AccountSetCode , FM.ODChargeType, FM.ODChargeCalOn, FM.ODChargeAmtOrPerc, " );
		selQuery.append(" FM.ODGraceDays, FM.ODAllowWaiver, FM.ODMaxWaiverPerc " );
		selQuery.append(" FROM FinODDetails AS ODD INNER JOIN FinanceMain AS FM  ON ODD.FinReference= FM.FinReference " );
		selQuery.append(" INNER JOIN RMTFinanceTypes AS FT ON FM.FinType = FT.FinType " );
		selQuery.append(" INNER JOIN RMTAccountingSet AS ACS ON FT.FinLatePayRule = ACS.AccountSetId " );
		selQuery.append(" WHERE ODD.FinReference NOT IN (SELECT FinReference FROM FINODCRecovery AS ODR " );
		selQuery.append(" WHERE ODR.FinReference = ODD.FinReference AND ODR.FinSchdDate = ODD.FinODSchdDate " );
		selQuery.append(" AND ODR.FinODFor = ODD.FinODFor AND FinODCRecoverySts='C' ) AND  FM.ODChargeType ='D' " );
		selQuery.append(" UNION " );
		selQuery.append(" SELECT ODD.FinReference ,ODD.FinODSchdDate , ODD.FinODFor ,ODD.FinBranch ,ODD.FinType , " );
		selQuery.append(" ODD.CustID ,ODD.FinODTillDate ,ODD.FinCurODAmt , ODD.FinMaxODAmt ,ODD.FinCurODDays ,FM.ProfitDaysBasis, " );
		selQuery.append(" (FM.FinAmount + FM.FeeChargeAmt - FM.FinRepaymentAmount) As FinAmount,  FM.DownPayment , FM.NumberOfTerms , " );
		selQuery.append(" ACS.AccountSetCode , FM.ODChargeType, FM.ODChargeCalOn, FM.ODChargeAmtOrPerc, " );
		selQuery.append(" FM.ODGraceDays, FM.ODAllowWaiver, FM.ODMaxWaiverPerc " );
		selQuery.append(" FROM FinODDetails AS ODD INNER JOIN FinanceMain AS FM  ON ODD.FinReference= FM.FinReference " );
		selQuery.append(" INNER JOIN RMTFinanceTypes AS FT ON FM.FinType = FT.FinType " );
		selQuery.append(" INNER JOIN RMTAccountingSet AS ACS ON FT.FinLatePayRule = ACS.AccountSetId " );
		selQuery.append(" WHERE ODD.FinReference NOT IN (SELECT FinReference FROM FINODCRecovery AS ODR " );
		selQuery.append(" WHERE ODR.FinReference = ODD.FinReference AND ODR.FinSchdDate = ODD.FinODSchdDate " );
		selQuery.append(" AND ODR.FinODFor = ODD.FinODFor) AND FM.ODChargeType IN ('F','P') " );

		return selQuery;
		
	}
	
	*//**
	 * Method for get the Finance Schedule Object data on particular Schedule date & FinReference 
	 * @param stringBuffer
	 * @param string
	 * @param date
	 * @return
	 *//*
	private StringBuffer prepareScheduleQuery(StringBuffer selQuery, String finReference,  java.sql.Date schdDate) {
		
		selQuery.append(" SELECT PrincipalSchd, SchdPriPaid , DefPrincipal, DefSchdPriPaid, ProfitSchd," );
		selQuery.append(" SchdPftPaid, DefProfit, DefSchdPftPaid, FinanceMain.FinCcy AS FinCcy ," );
		selQuery.append(" RMTCurrencies.CcyEditField AS NoOfDecimals " );
		selQuery.append(" From FinScheduleDetails, FinanceMain, RMTCurrencies " );
		selQuery.append(" Where FinScheduleDetails.FinReference = FinanceMain.FinReference " );
		selQuery.append(" AND RMTCurrencies.CcyCode = FinanceMain.FinCcy " );
		selQuery.append(" AND FinScheduleDetails.FinReference = '"+finReference+"'" );	
		selQuery.append(" AND FinScheduleDetails.schDate = '"+schdDate+"'" );	
		return selQuery;
		
	}
	
	
	private String getODChargeRecory(OverdueChargeRecovery odcr) {
		StringBuffer strodcr = new StringBuffer();
		
		if(odcr != null) {
			strodcr.append("Schedule");
			strodcr.append("-");
			strodcr.append(DateUtility.formatUtilDate(odcr.getFinSchdDate(), PennantConstants.dateFormat));
			strodcr.append(";");

			strodcr.append("OverDue Date");
			strodcr.append("-");
			strodcr.append(DateUtility.formatUtilDate(odcr.getFinODDate(), PennantConstants.dateFormat));
			strodcr.append(";");

			strodcr.append("Due Principal");
			strodcr.append("-");			
			strodcr.append(odcr.getFinODPri());
			strodcr.append(";");

			strodcr.append("Due Profit");			
			strodcr.append("-");			
			strodcr.append(odcr.getFinODPft());
			strodcr.append(";");

			strodcr.append("Due Total");
			strodcr.append("-");
			strodcr.append(odcr.getFinODTot());
			strodcr.append(";");

			strodcr.append("Total Charge");
			strodcr.append("-");
			strodcr.append(odcr.getFinODCPenalty());
			strodcr.append(";");

			strodcr.append("Charge Waived");
			strodcr.append("-");
			strodcr.append(odcr.getFinODCWaived());
			strodcr.append(";");

		}
		
		return strodcr.toString();

	}
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public AccountEngineExecution getEngineExecution() {
		return engineExecution;
	}
	public void setEngineExecution(AccountEngineExecution engineExecution) {
		this.engineExecution = engineExecution;
	}

	public void setRecoveryDAO(OverdueChargeRecoveryDAO recoveryDAO) {
		this.recoveryDAO = recoveryDAO;
	}
	public OverdueChargeRecoveryDAO getRecoveryDAO() {
		return recoveryDAO;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	public DataSource getDataSource() {
		return dataSource;
	}

	public BatchAdminDAO getBatchAdminDAO() {
		return batchAdminDAO;
	}
	public void setBatchAdminDAO(BatchAdminDAO batchAdminDAO) {
		this.batchAdminDAO = batchAdminDAO;
	}

}
*/