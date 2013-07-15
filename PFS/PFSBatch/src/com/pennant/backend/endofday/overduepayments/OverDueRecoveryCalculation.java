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
 */
package com.pennant.backend.endofday.overduepayments;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.pennant.app.util.AccountEngineExecution;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.batch.admin.BatchAdminDAO;
import com.pennant.backend.dao.financemanagement.OverdueChargeRecoveryDAO;
import com.pennant.backend.endofday.model.ODCConfig;
import com.pennant.backend.endofday.model.WorkFields;
import com.pennant.backend.model.financemanagement.OverdueChargeRecovery;
import com.pennant.backend.model.rulefactory.DataSet;
import com.pennant.backend.util.PennantConstants;

public class OverDueRecoveryCalculation implements Tasklet {

	private Logger logger = Logger.getLogger(OverDueRecoveryCalculation.class);
	
	private AccountEngineExecution engineExecution;
	private OverdueChargeRecoveryDAO recoveryDAO;
	private DataSource dataSource;
	private BatchAdminDAO batchAdminDAO;

	private Map<String,ODCConfig > chargeDetailsMap = null;
	private ODCConfig config = null;
	private Date dateValueDate = null;
	private BigDecimal zeroValue = BigDecimal.ZERO;
	

	@SuppressWarnings("serial")
	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {
		
		chargeDetailsMap = new HashMap<String, ODCConfig>();
		
		dateValueDate= DateUtility.getDBDate(SystemParameterDetails.getSystemParameterValue("APP_VALUEDATE").toString());
		
		logger.debug("START: OverDue Recovery Caluclation for Value Date: "+ dateValueDate);
		
		context.getStepContext().getStepExecution().getExecutionContext().put(context.getStepContext().getStepExecution().getId().toString(), dateValueDate);

		boolean isConfigFound = false;

		//Fetch OD charges for all finance types by customer category

		// READ REPAYMENTS DUE TODAY
		Connection connection = null;
		ResultSet resultSet = null;
		PreparedStatement sqlStatement = null;
		StringBuffer selQuery = new StringBuffer();
		selQuery = prepareODCConfigQuery(selQuery);
		
		try {
			
			//Fetch ODCConfig records data 
			connection = DataSourceUtils.doGetConnection(getDataSource());
			sqlStatement = connection.prepareStatement(selQuery.toString());
			resultSet = sqlStatement.executeQuery();
			while (resultSet.next()) {
				doPrepareODChargeDetails(resultSet);
			}
			
			// Fetch OverDue Details Data
			selQuery = prepareODDetailsQuery(new StringBuffer());
			sqlStatement = connection.prepareStatement(selQuery.toString());
			resultSet = sqlStatement.executeQuery();
			WorkFields workFields = null;
			
			while (resultSet.next()) {
				
				//Fetch Customer Category using Finreference from ODDetails
				String custCtgCode = resultSet.getString("CustCtgCode").trim();
				String finType = resultSet.getString("FinType").trim();
				
				 
				//Find Finance Type & Customer Category part of chargeDetailsMap
				if(chargeDetailsMap.containsKey(finType+custCtgCode)){
					isConfigFound = true;
					//Set ODCCOnfig detals into WorkFields object
					config = chargeDetailsMap.get(finType+custCtgCode);
					workFields = prepareWorkFieldsFromConfig(config);
				}else{
					isConfigFound = false;
					//Set SMTparameters details to WorkFields object	
					workFields = prepareWorkFieldsFromSMT();
				}
				
				//Date Check for OverDue Recovery
				Date dateCheck = DateUtility.addDays(resultSet.getDate("FinODSchdDate"), workFields.getWorkGraceDays());
				if(dateCheck.compareTo(dateValueDate) <= 0){

					//Fetch Customer Category using Finreference from ODDetails
					selQuery = prepareScheduleQuery(new StringBuffer(),resultSet.getString("FinReference"),
							resultSet.getDate("FinODSchdDate"));
					sqlStatement = connection.prepareStatement(selQuery.toString());
					ResultSet rs = sqlStatement.executeQuery();
					
					OverdueChargeRecovery chargeRecovery = null;
					//Prepare Finance OverDue Recovery Details Object Data
					String overDueRuleCode = "";
					if(isConfigFound){
						overDueRuleCode = config.getAccountSetCode();
					}
					
					//Insert OverDue Recovery(s) into Recoveries table
					while (rs.next()) {
						chargeRecovery = prepareODRData(resultSet,rs,custCtgCode, overDueRuleCode, workFields);
						getRecoveryDAO().save(chargeRecovery, "");
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
			chargeDetailsMap =null;
			config = null;
			resultSet.close();
			sqlStatement.close();
		}
		
		logger.debug("COMPLETED: OverDue Recovery Caluclation for Value Date: "+ dateValueDate);
		return RepeatStatus.FINISHED;
	}

	/**
	 * Method for Preparing OverDue Recovery Object data
	 * @param oDDRS
	 * @param schdlRS
	 * @param custCtgCode
	 * @param overDueRuleCode
	 * @param work
	 * @return
	 */
	@SuppressWarnings("serial")
	private OverdueChargeRecovery prepareODRData(ResultSet oDDRS, ResultSet schdlRS , String custCtgCode, 
			String overDueRuleCode, WorkFields work) {
		logger.debug("Entering");
		
		OverdueChargeRecovery  recovery = new OverdueChargeRecovery();
		try {
			recovery.setFinReference(oDDRS.getString("FinReference"));
			recovery.setFinSchdDate(oDDRS.getDate("FinODSchdDate"));
			recovery.setFinODFor(oDDRS.getString("FinODFor"));
			recovery.setFinBranch(oDDRS.getString("FinBranch"));
			recovery.setFinType(oDDRS.getString("FinType"));
			recovery.setFinCustId(oDDRS.getLong("FinCustID"));
			recovery.setFinCcy(schdlRS.getString("FinCcy"));
			recovery.setFinODDate(dateValueDate);

			if(recovery.getFinODFor().equals(PennantConstants.SCHEDULE)){
				recovery.setFinODPri(schdlRS.getBigDecimal("PrincipalSchd").subtract(schdlRS.getBigDecimal("SchdPriPaid")));
				recovery.setFinODPft(schdlRS.getBigDecimal("ProfitSchd").subtract(schdlRS.getBigDecimal("SchdPftPaid")));
			}else if(recovery.getFinODFor().equals(PennantConstants.DEFERED)){
				recovery.setFinODPri(schdlRS.getBigDecimal("DefPrincipal").subtract(schdlRS.getBigDecimal("DefSchdPriPaid")));
				recovery.setFinODPft(schdlRS.getBigDecimal("DefProfit").subtract(schdlRS.getBigDecimal("DefSchdPftPaid")));
			}
			
			recovery.setFinODTot(recovery.getFinODPri().add(recovery.getFinODPft()));
			recovery.setFinODCRuleCode(overDueRuleCode);
			
			//Prepare Dataset For Account Number Generation
			DataSet dataSet = doPrepareDataSet(oDDRS, recovery);

			String oDCPlAccount = getEngineExecution().generateAccount(dataSet,work.getWorkPLAC(), 
					work.getWorkPLACSH(), recovery.getFinODFor(),false);
			recovery.setFinODCPLAc(oDCPlAccount);

			String oDCCharityAccount = getEngineExecution().generateAccount(dataSet, work.getWorkCAC(),
					work.getWorkCACSH(), recovery.getFinODFor(),false);
			recovery.setFinODCCAc(oDCCharityAccount);

			recovery.setFinODCPLShare(work.getWorkPLShare());
			recovery.setFinODCSweep(work.isWorkSweep());
			recovery.setFinODCCustCtg(custCtgCode);
			recovery.setFinODCType(work.getWorkType());
			recovery.setFinODCOn(work.getWorkCalOn());
			recovery.setFinODC(work.getWorkChargeAmount());
			recovery.setFinODCGraceDays(work.getWorkGraceDays());
			recovery.setFinODCAlwWaiver(work.isWorkWaiver());
			recovery.setFinODCMaxWaiver(work.getWorkMaxWaiver());
			
			if(recovery.getFinODCType().equals("F")){
				recovery.setFinODCPenalty(recovery.getFinODC().multiply(schdlRS.getBigDecimal("NoOfDecimals")));
			}else{
				
				if(recovery.getFinODCOn().equals(PennantConstants.SPFT)){
					recovery.setFinODCPenalty(getPercentageValue(recovery.getFinODPft(),recovery.getFinODC()));
				}else if(recovery.getFinODCOn().equals(PennantConstants.SPFT)){
					recovery.setFinODCPenalty(getPercentageValue(recovery.getFinODPri(),recovery.getFinODC()));
				}else{
					recovery.setFinODCPenalty(getPercentageValue(recovery.getFinODTot(),recovery.getFinODC()));
				}
				
			}
			recovery.setFinODCWaived(zeroValue);
			recovery.setFinODCPLPenalty(getPercentageValue(recovery.getFinODCPenalty(),recovery.getFinODCPLShare()));
			recovery.setFinODCCPenalty(recovery.getFinODCPenalty().subtract(recovery.getFinODCPLPenalty()));
			recovery.setFinODCPaid(zeroValue);
			recovery.setFinODCLastPaidDate(dateValueDate);
			recovery.setFinODCRecoverySts("R");

		} catch (Exception e) {
			logger.error(e);
			throw new DataAccessException(e.getMessage()) {};
		}
		logger.debug("Leaving");
		return recovery;
	}
	
	/**
	 * Method for get the Percentage of given value 
	 * @param dividend
	 * @param divider
	 * @return
	 */
	private BigDecimal getPercentageValue(BigDecimal dividend, BigDecimal divider){
		return (dividend.multiply(unFormateAmount(divider,2).divide(
				new BigDecimal(100)))).divide(new BigDecimal(100),RoundingMode.HALF_DOWN);
	}
	
	/**
	 * Method for UnFormat the passing Amount Value
	 * @param amount
	 * @param dec
	 * @return
	 */
	public BigDecimal unFormateAmount(BigDecimal amount, int dec) {
		if (amount == null) {
			return new BigDecimal(0);
		}
		BigInteger bigInteger = amount.multiply(new BigDecimal(Math.pow(10, dec))).toBigInteger();
		return new BigDecimal(bigInteger);
	}
	
	/**
	 * Method for Preparation of Dataset 
	 * @param oDDRS
	 * @param recovery
	 * @return
	 * @throws SQLException
	 */
	private DataSet doPrepareDataSet(ResultSet oDDRS, OverdueChargeRecovery  recovery) throws SQLException{
		
		DataSet dataSet = new DataSet();
		
		try {
			
			dataSet.setFinReference(recovery.getFinReference());
			dataSet.setFinBranch(recovery.getFinBranch());
			dataSet.setFinCcy(recovery.getFinCcy());
			dataSet.setSchdDate(recovery.getFinSchdDate());
			dataSet.setFinType(recovery.getFinType());
			dataSet.setCustId(recovery.getFinCustId());
			dataSet.setFinAmount(oDDRS.getBigDecimal("FinAmount"));
			dataSet.setNewRecord(false);
			dataSet.setDownPayment(oDDRS.getBigDecimal("DownPayment"));
			dataSet.setNoOfTerms(oDDRS.getInt("NoOfTerms"));

		} catch (SQLException e) {
			logger.error(e);
			throw new SQLException(e.getMessage());
		}

		return dataSet;

	}
	/**
	 * Method for preparation of Select Query To get ConfigDetails data
	 * 
	 * @param selQuery
	 * @return
	 */
	private StringBuffer prepareODCConfigQuery(StringBuffer selQuery) {

		selQuery.append(" SELECT RMTFinanceTypes.FinType, RMTFinanceTypes.FinLatePayRule As AccountSetID,");
		selQuery.append(" RMTAccountingSet.AccountSetCode, ODCH.ODCPLAccount, ODCH.ODCPLSubHead," );
		selQuery.append(" ODCH.ODCCharityAccount, ODCH.ODCCharitySubHead, ODCH.ODCPLShare, ODCH.ODCSweepCharges ," );
		selQuery.append(" ODCD.ODCCustCtg, ODCD.ODCType, ODCD.ODCOn, ODCD.ODCAmount, ODCD.ODCGraceDays,");
		selQuery.append(" ODCD.ODCAllowWaiver, ODCD.ODCMaxWaiver FROM RMTFinanceTypes " );
		selQuery.append(" INNER JOIN RMTAccountingSet ON RMTFinanceTypes.FinLatePayRule = RMTAccountingSet.AccountSetid" );
		selQuery.append(" INNER JOIN FinODCHeader AS ODCH " );
		selQuery.append(" INNER JOIN FinODCDetails AS ODCD ON ODCH.ODCRuleCode = ODCD.ODCRuleCode " );
		selQuery.append(" ON RMTAccountingSet.AccountSetCode = ODCH.ODCRuleCode");
		return selQuery;
		
	}
	
	/**
	 * Method for Prepare 
	 * @param set
	 */
	@SuppressWarnings("serial")
	private void doPrepareODChargeDetails(ResultSet set) {
		logger.debug("Entering");
		
		config = new ODCConfig();
		try {
			config.setFinType(set.getString("FinType"));
			config.setAccountSetID(set.getLong("AccountSetID"));
			config.setAccountSetCode(set.getString("AccountSetCode"));
			config.setODCPLAccount(set.getString("ODCPLAccount"));
			config.setODCPLSubHead(set.getString("ODCPLSubHead"));
			config.setODCCharityAccount(set.getString("ODCCharityAccount"));
			config.setODCCharitySubHead(set.getString("ODCCharitySubHead"));
			config.setODCPLShare(set.getBigDecimal("ODCPLShare"));
			config.setODCCustCtg(set.getString("ODCCustCtg"));
			config.setODCType(set.getString("ODCType"));
			config.setODCOn(set.getString("ODCOn"));
			config.setODCAmount(set.getBigDecimal("ODCAmount"));
			config.setODCGraceDays(set.getInt("ODCGraceDays"));
			config.setODCAllowWaiver(set.getBoolean("ODCAllowWaiver"));
			config.setODCMaxWaiver(set.getBigDecimal("ODCMaxWaiver"));
			config.setODCSweepCharges(set.getBoolean("ODCSweepCharges"));
			
			chargeDetailsMap.put(config.getFinType().trim().concat(config.getODCCustCtg().trim()), config);
			
		} catch (SQLException e) {
			logger.error(e);
			throw new DataAccessException(e.getMessage()) {};
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Method for preparation of Select Query To get OverDueDetails data
	 * 
	 * @param selQuery
	 * @return
	 */
	private StringBuffer prepareODDetailsQuery(StringBuffer selQuery) {
		
		selQuery.append(" SELECT ODD.FinReference As FinReference ,ODD.FinODSchdDate As FinODSchdDate ," );
		selQuery.append(" ODD.FinODFor As FinODFor ,ODD.FinBranch As FinBranch ,ODD.FinType As FinType ," );
		selQuery.append(" ODD.CustID As FinCustID ,ODD.FinODTillDate As FInODTillDate ,ODD.FinCurODAmt As FInCurODAmt ," );
		selQuery.append(" ODD.FinMaxODAmt As FinMaxODAmt ,ODD.FinCurODDays As FinCurODDays, CUST.CustctgCode,  " );
		selQuery.append(" (FinMain.FinAmount - FinMain.FinRepaymentAmount) As FinAmount, " );
		selQuery.append(" FinMain.DownPayment As DownPayment, FinMain.NumberOfTerms as NoOfTerms " );
		selQuery.append(" FROM FinODDetails AS ODD INNER JOIN FinanceMain AS Finmain ON ODD.FinReference= Finmain.FinReference " );
		selQuery.append(" INNER JOIN Customers As CUST ON ODD.CustID = CUST.CUSTID " );
		selQuery.append(" WHERE NOT EXISTS (SELECT FinReference FROM FINODCRecovery AS ODR " );
		selQuery.append(" WHERE ODR.FinReference = ODD.FinReference AND ODR.FinSchdDate = ODD.FinODSchdDate " );
		selQuery.append(" AND ODR.FinODFor = ODD.FinODFor) ");
		return selQuery;
		
	}
	
	/**
	 * Method for Preparation of WorkFields Using OverDueCharges Config Data
	 * @param config
	 * @return
	 */
	private WorkFields prepareWorkFieldsFromConfig(ODCConfig config) {
		
		WorkFields workFields = new WorkFields();
		workFields.setWorkChargeAmount(config.getODCAmount());
		workFields.setWorkCalOn(config.getODCOn());
		workFields.setWorkGraceDays(config.getODCGraceDays());
		workFields.setWorkPLShare(config.getODCPLShare());
		workFields.setWorkType(config.getODCType());
		workFields.setWorkPLAC(config.getODCPLAccount());
		workFields.setWorkPLACSH(config.getODCPLSubHead());
		workFields.setWorkCAC(config.getODCCharityAccount());
		workFields.setWorkCACSH(config.getODCCharitySubHead());
		workFields.setWorkWaiver(config.isODCAllowWaiver());
		workFields.setWorkMaxWaiver(config.getODCMaxWaiver());
		workFields.setWorkSweep(config.isODCSweepCharges());
		
		return workFields;
	}
	
	/**
	 * Method for Preparation of WorkFields Using OverDueCharges Config Data
	 * @param config
	 * @return
	 */
	public WorkFields prepareWorkFieldsFromSMT() {
		
		WorkFields workFields = new WorkFields();
		workFields.setWorkChargeAmount(new BigDecimal(SystemParameterDetails.getSystemParameterValue("ODC_AMT").toString()));
		workFields.setWorkCalOn(SystemParameterDetails.getSystemParameterValue("ODC_CALON").toString());
		workFields.setWorkGraceDays(Integer.parseInt(SystemParameterDetails.getSystemParameterValue("ODC_GRACE").toString()));
		workFields.setWorkPLShare(new BigDecimal(SystemParameterDetails.getSystemParameterValue("ODC_PLSHARE").toString()));
		workFields.setWorkType(SystemParameterDetails.getSystemParameterValue("ODC_TYPE").toString());
		workFields.setWorkPLAC(SystemParameterDetails.getSystemParameterValue("ODC_PLAC").toString());
		workFields.setWorkPLACSH(SystemParameterDetails.getSystemParameterValue("ODC_PLACSH").toString());
		workFields.setWorkCAC(SystemParameterDetails.getSystemParameterValue("ODC_CAC").toString());
		workFields.setWorkCACSH(SystemParameterDetails.getSystemParameterValue("ODC_CACSH").toString());
		workFields.setWorkWaiver(SystemParameterDetails.getSystemParameterValue("ODC_WAIVER").toString().equals("Y")?true:false);
		workFields.setWorkMaxWaiver(new BigDecimal(SystemParameterDetails.getSystemParameterValue("ODC_MAXWAIVER").toString()));
		workFields.setWorkSweep(SystemParameterDetails.getSystemParameterValue("ODC_SWEEP").toString().equals("Y")?true:false);
		
		return workFields;
	}
	
	/**
	 * Method for get the Finance Schedule Object data on particular Schedule date & FinReference 
	 * @param stringBuffer
	 * @param string
	 * @param date
	 * @return
	 */
	private StringBuffer prepareScheduleQuery(StringBuffer selQuery, String finReference, 
			java.sql.Date schdDate) {
		
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

			strodcr.append("To P&L");
			strodcr.append("-");
			strodcr.append(odcr.getFinODCPLPenalty());
			strodcr.append(";");

			strodcr.append("To Charity");
			strodcr.append("-");
			strodcr.append(odcr.getFinODCCPenalty());
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
