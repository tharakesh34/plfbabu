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
 * FileName    		:  ProvisionCalculationUtil.java													*                           
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
package com.pennant.app.util;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.financemanagement.ProvisionDAO;
import com.pennant.backend.dao.financemanagement.ProvisionMovementDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.financemanagement.Provision;
import com.pennant.backend.model.financemanagement.ProvisionMovement;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.DataSet;
import com.pennant.coreinterface.exception.AccountNotFoundException;

public class ProvisionCalculationUtil implements Serializable {
	
    private static final long serialVersionUID = 193855810060181970L;
	private static Logger logger = Logger.getLogger(ProvisionCalculationUtil.class);

	private FinanceMainDAO financeMainDAO;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private FinanceProfitDetailDAO financeProfitDetailDAO;
	private ProvisionDAO provisionDAO;
	private ProvisionMovementDAO provisionMovementDAO;
	private AccountEngineExecution engineExecution ;
	private PostingsPreparationUtil postingsPreparationUtil;
	
	private FinanceMain financeMain = null;
	private List<FinanceScheduleDetail> schdDetails = null;
	private FinanceProfitDetail pftDetail = null;
	private AEAmountCodes amountCodes = null;
	private DataSet dataSet = null;
	
	/**
	 * Method for Processing Provision Calculations
	 * @param procProvision
	 * @param dateValueDate
	 * @param isProvRelated
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws AccountNotFoundException 
	 */
	public ErrorDetails processProvCalculations(Provision procProvision , Date dateValueDate,
			boolean isProvRelated, boolean isScrnLvlProc, boolean isRIAFinance)
		throws IllegalAccessException, InvocationTargetException, AccountNotFoundException{
		
		logger.debug("Entering");
		
		BigDecimal provCalculated = BigDecimal.ZERO;
		
		financeMain = getFinanceMainDAO().getFinanceMainById(procProvision.getFinReference(), "", false);
		schdDetails = getFinanceScheduleDetailDAO().getFinScheduleDetails(procProvision.getFinReference(), "", false);
		pftDetail = getFinanceProfitDetailDAO().getFinProfitDetailsById(procProvision.getFinReference());
		
		AEAmounts aeAmounts = new AEAmounts();
		amountCodes = aeAmounts.procAEAmounts(financeMain, schdDetails, pftDetail, dateValueDate);
		amountCodes.setPROVDUE(procProvision.getProvisionDue() == null ? BigDecimal.ZERO : procProvision.getProvisionDue());
		dataSet = aeAmounts.createDataSet(financeMain, "PROVSN", dateValueDate, procProvision.getProvisionCalDate());
		dataSet.setNewRecord(false);
		
		provCalculated = getEngineExecution().getProvisionExecResults(dataSet, amountCodes);
		
		//Search For Provision Record in case of OverDue
		Provision provision = null;
		boolean isRcdFound = true;
		if(!isProvRelated){
			provision = getProvisionDAO().getProvisionById(procProvision.getFinReference(), "");
			if(provision == null){
				 isRcdFound = false;
			}
		}
		
		boolean isProceedFurthur = true;
		//Case for Provision Record not Found
		if(!isRcdFound){
			
			if(provCalculated.compareTo(BigDecimal.ZERO) == 0){
				if(!isScrnLvlProc){
					isProceedFurthur = false;
				}
			}
			
			if(isProceedFurthur){
				provision = procProvision;
				provision = prepareProvisionData(provision , dateValueDate, provCalculated, amountCodes, 1);

				//Save Provision Record
				getProvisionDAO().save(provision, "");
			}
		}else{
			
			isProceedFurthur = false;
			BigDecimal prvProvCalAmt = null;
			if(!isProvRelated){
				prvProvCalAmt = provision.getProvisionAmtCal();
			}else{
				prvProvCalAmt = procProvision.getProvisionAmtCal();
			}
			provision = procProvision;
			
			if(!provision.isUseNFProv() && prvProvCalAmt.compareTo(provCalculated) != 0){
				provision = prepareProvisionData(provision , dateValueDate, provCalculated, amountCodes,2);
				isProceedFurthur = true;
			}

			if(provision.isAutoReleaseNFP() && provCalculated.compareTo(BigDecimal.ZERO) == 0){
				provision = prepareProvisionData(provision , dateValueDate, provCalculated, amountCodes,3);
				isProceedFurthur = true;
			}

			if(provision.getProvisionedAmt().compareTo(provision.getNonFormulaProv()) < 0){
				provision = prepareProvisionData(provision , dateValueDate, provCalculated, amountCodes,4);
				isProceedFurthur = true;
			}
			
			if(isScrnLvlProc && provision.getProvisionedAmt().compareTo(provision.getNonFormulaProv()) > 0){
				provision = prepareProvisionData(provision , dateValueDate, provCalculated, amountCodes,4);
				isProceedFurthur = true;
			}
			
			//Update Provision Details data
			if(isProceedFurthur){
				getProvisionDAO().update(provision, "");
			}
			
		}
		
		ProvisionMovement movement = null;
		ErrorDetails errorDetails = null;
		if(isProceedFurthur){
			//Provision Movement record update
			if(provision.getProvisionDue().compareTo(BigDecimal.ZERO) == 0){
				// Nothing to do , loop repetation
			}else{
				movement = prepareProvisionMovementData(provision, dateValueDate);
			}
			
			//Provision Posting Process for Screen Level Process
			if(isScrnLvlProc && movement != null){
				amountCodes.setPROVDUE(movement.getProvisionDue() == null ? BigDecimal.ZERO : movement.getProvisionDue());
				Date dateAppDate = (Date) SystemParameterDetails.getSystemParameterValue("APP_DATE");
				if(!((Boolean)getPostingsPreparationUtil().processPostingDetails(dataSet, amountCodes, false,
						isRIAFinance, "Y", dateAppDate, movement, true).get(0))){
					errorDetails = new ErrorDetails("", "9999", "E", "Provision Posting Details are failed...",
							null, null);
				}
			}	
		}
		logger.debug("Leaving");
		return errorDetails;
	}
	
	/**
	 * Method for Preparation for Provision Details
	 * @param provision
	 * @param details
	 * @param valueDate
	 * @param provCalculated
	 * @param aeAmountCodes
	 * @param scenarioSeq
	 * @return
	 */
	private Provision prepareProvisionData(Provision provision ,Date valueDate, 
			BigDecimal provCalculated, AEAmountCodes aeAmountCodes, int scenarioSeq){
		logger.debug("Entering");
		
		// Scenario 1
		if(scenarioSeq == 1){
			provision.setProvisionedAmt(BigDecimal.ZERO);
			provision.setProvisionAmtCal(provCalculated);
			provision.setProvisionDue(provCalculated);
			provision.setNonFormulaProv(BigDecimal.ZERO);
			provision.setUseNFProv(false);
			provision.setAutoReleaseNFP(false);
		}
				
		// Scenario 2
		if(scenarioSeq == 2){
			provision.setProvisionAmtCal(provCalculated);
			provision.setProvisionDue(provision.getProvisionAmtCal().subtract(provision.getProvisionedAmt()));
		}

		// Scenario 3
		if(scenarioSeq == 3){
			provision.setProvisionAmtCal(BigDecimal.ZERO);
			provision.setProvisionDue(BigDecimal.ZERO.subtract(provision.getProvisionedAmt()));
		}

		// Scenario 4
		if(scenarioSeq == 4){
			provision.setProvisionAmtCal(provision.getNonFormulaProv());
			provision.setProvisionDue(provision.getProvisionAmtCal().subtract(provision.getProvisionedAmt()));
		}

		//Common Changes for all Scenario's
		provision.setProvisionCalDate(valueDate);
		provision.setPrincipalDue(aeAmountCodes.getPriAB());
		provision.setProfitDue(aeAmountCodes.getPftAB());
		Date curBussDate = (Date) SystemParameterDetails.getSystemParameterValue("APP_DATE");
		provision.setDueFromDate(DateUtility.addDays(curBussDate, -aeAmountCodes.getODDays()));
		provision.setLastFullyPaidDate(DateUtility.addDays(curBussDate, -aeAmountCodes.getDaysFromFullyPaid()));

		logger.debug("Leaving");
		return provision;
	}
	
	/**
	 * Method for Preparation for Provision Movement Details
	 * @param provision
	 * @param valueDate
	 */
	private ProvisionMovement prepareProvisionMovementData(Provision provision, Date valueDate){
		logger.debug("Entering");
		
		ProvisionMovement provisionMovement =  getProvisionMovementDAO().getProvisionMovementById(
				provision.getFinReference(),valueDate, "");
		
		ProvisionMovement movement = new ProvisionMovement();
		movement.setFinReference(provision.getFinReference());
		movement.setProvMovementDate(valueDate);
		
		//If Record is not exist in database
		if(provisionMovement == null){
			movement.setProvMovementSeq(1);
		}else{
			
			if(provision.getProvisionAmtCal().compareTo(provisionMovement.getProvisionAmtCal()) == 0){
				logger.debug("Leaving");
				return provisionMovement;
			}
			
			movement.setProvMovementSeq(provisionMovement.getProvMovementSeq()+1);
		}
		movement.setProvCalDate(valueDate);
		movement.setProvisionedAmt(provision.getProvisionedAmt());
		movement.setProvisionAmtCal(provision.getProvisionAmtCal());
		movement.setProvisionDue(provision.getProvisionDue());
		movement.setProvisionPostSts("R");
		movement.setNonFormulaProv(provision.getNonFormulaProv());
		movement.setUseNFProv(provision.isUseNFProv());
		movement.setAutoReleaseNFP(provision.isAutoReleaseNFP());
		movement.setPrincipalDue(provision.getPrincipalDue());
		movement.setProfitDue(provision.getProfitDue());
		movement.setDueFromDate(provision.getDueFromDate());
		movement.setLastFullyPaidDate(provision.getLastFullyPaidDate());
		movement.setLinkedTranId(0);
		
		//Added New Provision Movement
		getProvisionMovementDAO().save(movement, "");
		logger.debug("Leaving");
		return movement;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public FinanceMainDAO getFinanceMainDAO() {
		return financeMainDAO;
	}
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public FinanceScheduleDetailDAO getFinanceScheduleDetailDAO() {
		return financeScheduleDetailDAO;
	}
	public void setFinanceScheduleDetailDAO(
			FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	public FinanceProfitDetailDAO getFinanceProfitDetailDAO() {
		return financeProfitDetailDAO;
	}
	public void setFinanceProfitDetailDAO(
			FinanceProfitDetailDAO financeProfitDetailDAO) {
		this.financeProfitDetailDAO = financeProfitDetailDAO;
	}

	public ProvisionDAO getProvisionDAO() {
		return provisionDAO;
	}
	public void setProvisionDAO(ProvisionDAO provisionDAO) {
		this.provisionDAO = provisionDAO;
	}

	public ProvisionMovementDAO getProvisionMovementDAO() {
		return provisionMovementDAO;
	}
	public void setProvisionMovementDAO(ProvisionMovementDAO provisionMovementDAO) {
		this.provisionMovementDAO = provisionMovementDAO;
	}

	public AccountEngineExecution getEngineExecution() {
		return engineExecution;
	}
	public void setEngineExecution(AccountEngineExecution engineExecution) {
		this.engineExecution = engineExecution;
	}

	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
	    this.postingsPreparationUtil = postingsPreparationUtil;
    }
	public PostingsPreparationUtil getPostingsPreparationUtil() {
	    return postingsPreparationUtil;
    }
	
}
