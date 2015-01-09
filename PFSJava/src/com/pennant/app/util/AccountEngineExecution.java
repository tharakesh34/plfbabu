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
 * FileName    		:  AccountEngineExecution.java													*                           
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
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.Interface.model.IAccounts;
import com.pennant.Interface.service.AccountInterfaceService;
import com.pennant.backend.dao.applicationmaster.CurrencyDAO;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.finance.DefermentHeaderDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinancePremiumDetailDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.FinanceSuspHeadDAO;
import com.pennant.backend.dao.masters.SystemInternalAccountDefinitionDAO;
import com.pennant.backend.dao.rmtmasters.AccountingSetDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.dao.rmtmasters.TransactionEntryDAO;
import com.pennant.backend.dao.rulefactory.RuleDAO;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.commitment.Commitment;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinancePremiumDetail;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rmtmasters.TransactionEntry;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AECommitment;
import com.pennant.backend.model.rulefactory.DataSet;
import com.pennant.backend.model.rulefactory.DataSetFiller;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.model.rulefactory.SubHeadRule;
import com.pennant.backend.util.PennantConstants;
import com.pennant.coreinterface.exception.AccountNotFoundException;
import com.pennant.eod.util.EODProperties;
import com.rits.cloning.Cloner;

public class AccountEngineExecution implements Serializable {

    private static final long serialVersionUID = 852062955563015315L;
	private Logger logger = Logger.getLogger(AccountEngineExecution.class);

	private static FinanceTypeDAO financeTypeDAO;	
	private static FinanceMainDAO financeMainDAO;	
	private static TransactionEntryDAO transactionEntryDAO;
	private static SystemInternalAccountDefinitionDAO internalAccountDefinitionDAO;
	private static RuleDAO ruleDAO;
	private static CustomerDAO customerDAO;
	private static FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private static CurrencyDAO currencyDAO;
	private static DefermentHeaderDAO defermentHeaderDAO;
	private static FinanceSuspHeadDAO financeSuspHeadDAO;
	private static AccountInterfaceService accountInterfaceService;
	private static RuleExecutionUtil ruleExecutionUtil;
	private static AccountingSetDAO accountingSetDAO;
	private static FinancePremiumDetailDAO financePremiumDetailDAO;

	private Customer customer;
	private DataSetFiller dataSetFiller;
	private AECommitment aeCommitment;
	private SubHeadRule subHeadRule;
	private String currencyNymber;
	private List<IAccounts> accountsList;  
	
	private List<ReturnDataSet> returnDataSetList = new ArrayList<ReturnDataSet>();
	private List<FeeRule> feeRuleList = new ArrayList<FeeRule>();
	private BigDecimal provisionAmt = BigDecimal.ZERO;

	//Default Constructor
	public AccountEngineExecution() {
		super();
	}
	
	private enum FINEVENT {
		ADDDBSF , ADDDBSN , ADDDBSP , AMZ , AMZSUSP , DEFRPY , DEFFRQ , EARLYPAY , EARLYSTL , LATEPAY , M_AMZ , M_NONAMZ , 
		RATCHG , REPAY , WRITEOFF , WRITEBK , GRACEEND , SCDCHG , COMPOUND , PROVSN , DPRCIATE , ISTBILL ;
	}
	
	/**
	 * Method for Execution of Accounting Sets depend on Event 
	 * @param dataSet
	 * @param aeAmountCodes
	 * @param type
	 * @return
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws AccountNotFoundException 
	 */
	public List<ReturnDataSet> getAccEngineExecResults(DataSet dataSet,AEAmountCodes aeAmountCodes
			,String createNow, Map<String, FeeRule> feeRuleDetailMap, boolean isAccrualCal, FinanceType finType)
					throws AccountNotFoundException, IllegalAccessException, InvocationTargetException {
		
		return new AccountEngineExecution(dataSet,aeAmountCodes,createNow, feeRuleDetailMap, isAccrualCal, finType, null).getReturnDataSetList();
	}
	
	/**
	 * Method for Execution of Accounting Sets depend on Event 
	 * @param dataSet
	 * @param aeAmountCodes
	 * @param type
	 * @return
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws AccountNotFoundException 
	 */
	public List<ReturnDataSet> getAccEngineExecResults(DataSet dataSet,AEAmountCodes aeAmountCodes
			,String createNow, Map<String, FeeRule> feeRuleDetailMap, boolean isAccrualCal, FinanceType finType, FinancePremiumDetail financePremiumDetail)
					throws AccountNotFoundException, IllegalAccessException, InvocationTargetException {
		
		return new AccountEngineExecution(dataSet,aeAmountCodes,createNow, feeRuleDetailMap, isAccrualCal, finType, financePremiumDetail).getReturnDataSetList();
	}
	
	/**
	 * Method for Execution Stage entries for RIA Investments
	 * @param dataSet
	 * @param aeAmountCodes
	 * @param createNow
	 * @param feeRuleDetailMap
	 * @return
	 * @throws AccountNotFoundException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public List<ReturnDataSet> getStageExecResults(DataSet dataSet,AEAmountCodes aeAmountCodes
			,String createNow, String roleCode, Map<String, FeeRule> feeRuleDetailMap, FinanceType finType, FinancePremiumDetail premiumDetail) 
					throws AccountNotFoundException, IllegalAccessException, InvocationTargetException{
		
		return new AccountEngineExecution(dataSet,aeAmountCodes,createNow, roleCode, feeRuleDetailMap, finType, premiumDetail).getReturnDataSetList();
	}
	
	/**
	 * Method for Execution Of Fee & Charges Rules
	 * @param dataSet
	 * @param amountCode
	 * @return
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws AccountNotFoundException 
	 */
	public List<FeeRule> getFeeChargesExecResults(DataSet dataSet,
			AEAmountCodes aeAmountCodes, int formatter, boolean isWIF, FinanceType finType) throws IllegalAccessException, InvocationTargetException, AccountNotFoundException{
		
		return new AccountEngineExecution(dataSet,aeAmountCodes, formatter, isWIF, finType).getFeeRuleList();
	}
	
	/**
	 * Method for Execution Of Fee & Charges Rules
	 * @param dataSet
	 * @param amountCode
	 * @return
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws AccountNotFoundException 
	 */
	public List<FeeRule> getReExecFeeResults(DataSet dataSet,
			AEAmountCodes aeAmountCodes, int formatter, boolean isWIF, FinanceType finType, List<FeeRule> existFeeList) 
					throws IllegalAccessException, InvocationTargetException, AccountNotFoundException{
		return new AccountEngineExecution(dataSet,aeAmountCodes, formatter, isWIF, finType, existFeeList).getFeeRuleList();
	}
	
	/**
	 * Method For execution of Provision Rule For Provision Calculated Amount
	 * @param dataSet
	 * @param aeAmountCodes
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws AccountNotFoundException 
	 */
	public BigDecimal getProvisionExecResults(DataSet dataSet, AEAmountCodes aeAmountCodes) throws IllegalAccessException, InvocationTargetException, AccountNotFoundException{
		return new AccountEngineExecution(dataSet,aeAmountCodes).getProvisionAmt();
	}
	
	/**
	 * Method for Commitment posting Entries Execution
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws AccountNotFoundException 
	 */
	public List<ReturnDataSet> getCommitmentExecResults(AECommitment aeCommitment, Commitment commitment, String acSetEvent, 
			String createNow, Map<String, FeeRule> feeRuleDetailMap) throws AccountNotFoundException, IllegalAccessException, InvocationTargetException{
	
		return new AccountEngineExecution(aeCommitment, commitment, acSetEvent, createNow,feeRuleDetailMap).getReturnDataSetList();
	}
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++++ Constructors ++++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	private AccountEngineExecution(DataSet dataSet,AEAmountCodes aeAmountCodes
			,String createNow, Map<String, FeeRule> feeRuleDetailMap, boolean isAccrualCal, FinanceType finType, FinancePremiumDetail financePremiumDetail) throws AccountNotFoundException, IllegalAccessException, InvocationTargetException {
		setReturnDataSetList(processAccountExecution(dataSet, aeAmountCodes, createNow, feeRuleDetailMap, isAccrualCal, finType, financePremiumDetail));
	}
	
	private AccountEngineExecution(DataSet dataSet,AEAmountCodes aeAmountCodes
			,String createNow, String roleCode, Map<String, FeeRule> feeRuleDetailMap, FinanceType finType, FinancePremiumDetail premiumDetail) throws AccountNotFoundException, IllegalAccessException, InvocationTargetException {
		setReturnDataSetList(processStageAccountExecution(dataSet, aeAmountCodes, createNow, roleCode, feeRuleDetailMap, finType, premiumDetail));
	}
	
	private AccountEngineExecution(DataSet dataSet, AEAmountCodes aeAmountCodes, int formatter, boolean isWIF, FinanceType finType) throws AccountNotFoundException, IllegalAccessException, InvocationTargetException {
		setFeeRuleList(processFeeChargesExecResults(dataSet,aeAmountCodes, formatter, isWIF, finType));
	}
	
	private AccountEngineExecution(DataSet dataSet, AEAmountCodes aeAmountCodes, int formatter, boolean isWIF, FinanceType finType, List<FeeRule> existFeeList) throws AccountNotFoundException, IllegalAccessException, InvocationTargetException {
		setFeeRuleList(processReExecFeeResults(dataSet,aeAmountCodes, formatter, isWIF, finType, existFeeList));
	}
	
	private AccountEngineExecution(DataSet dataSet, AEAmountCodes aeAmountCodes) throws AccountNotFoundException, IllegalAccessException, InvocationTargetException {
		setProvisionAmt(processProvExecResults(dataSet,aeAmountCodes));
	}
	
	private AccountEngineExecution(AECommitment aeCommitment, Commitment commitment, String acSetEvent, 
			String createNow, Map<String, FeeRule> feeRuleDetailMap) throws AccountNotFoundException, IllegalAccessException, InvocationTargetException {
		setReturnDataSetList(processCommitmentExecResults(aeCommitment, commitment, acSetEvent, createNow, feeRuleDetailMap));
	}
	
	/**
	 * Method for Process Accounting Transaction Entry Details
	 * @param dataSet
	 * @param aeAmountCodes
	 * @param createNow
	 * @param feeRuleDetailMap
	 * @param isAccrualCal
	 * @param finType
	 * @param financePremiumDetail
	 * @return
	 * @throws AccountNotFoundException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private List<ReturnDataSet> processAccountExecution(DataSet dataSet,AEAmountCodes aeAmountCodes
			,String createNow, Map<String, FeeRule> feeRuleDetailMap, boolean isAccrualCal, FinanceType finType, FinancePremiumDetail premiumDetail)
			throws AccountNotFoundException, IllegalAccessException, InvocationTargetException{
		logger.debug("Entering");
		
		resetVariables();
		dataSetFiller = new DataSetFiller();

		//Fill Amount Code Detail Object with Respect to Schedule Details
		prepareAmountCodes(dataSet,aeAmountCodes, false, isAccrualCal,finType, premiumDetail);
		
		//Execute entries depend on Finance Event
		String accountingSetId = getAccSetId(dataSet.getFinEvent(), finType);

		List<TransactionEntry> transactionEntries = null;
		String phase = StringUtils.trimToEmpty(SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_PHASE).toString());
		if (phase.equals(PennantConstants.APP_PHASE_DAY)) {
			transactionEntries = getTransactionEntryDAO().getListTransactionEntryById(Long.valueOf(accountingSetId), "_AEView",true);
		}else{
			transactionEntries = EODProperties.getTransactionEntryList(Long.valueOf(accountingSetId));
		}
		
		List<ReturnDataSet> returnList = getPrepareAccountingSetResults(dataSet, transactionEntries,createNow,false, feeRuleDetailMap, finType);
		
		resetVariables();
		logger.debug("Leaving");
		return returnList;
	}
	
	
	/**
	 * Method For Fetching Accounting Set ID Depends on Finance Event
	 * @param finEvent
	 * @return
	 */
	private String getAccSetId(String finEvent, FinanceType financeType){
		
		//Execute entries depend on Finance Event
		String accountingSetId = "";
		FINEVENT eventCode = FINEVENT.valueOf(finEvent);

		switch (eventCode) {
	        case ADDDBSF:
	        	accountingSetId = financeType.getFinAEAddDsbFD();
		        break;
	        case ADDDBSN:
	        	accountingSetId = financeType.getFinAEAddDsbFDA();
	        	break;
	        case ADDDBSP:
	        	accountingSetId = financeType.getFinAEAddDsbOD();
	        	break;
	        case AMZ:
	        	accountingSetId = financeType.getFinAEAmzNorm();
	        	break;
	        case AMZSUSP:
	        	accountingSetId = financeType.getFinAEAmzSusp();
	        	break;
	        case DEFRPY:
	        	accountingSetId = financeType.getFinDefRepay();
	        	break;
	        case DEFFRQ:
	        	accountingSetId = financeType.getFinAEPlanDef();
	        	break;
	        case EARLYPAY:
	        	accountingSetId = financeType.getFinAEEarlyPay();
	        	break;
	        case EARLYSTL:
	        	accountingSetId = financeType.getFinAEEarlySettle();
	        	break;
	        case LATEPAY:
	        	accountingSetId = financeType.getFinLatePayRule();
	        	break;
	        case M_AMZ:
	        	accountingSetId = financeType.getFinToAmz();
	        	break;
	        case M_NONAMZ:
	        	accountingSetId = financeType.getFinAEToNoAmz();
	        	break;
	        case RATCHG:
	        	accountingSetId = financeType.getFinAERateChg();
	        	break;
	        case REPAY:
	        	accountingSetId = financeType.getFinAERepay();
	        	break;
	        case WRITEOFF:
	        	accountingSetId = financeType.getFinAEWriteOff();
	        	break;
	        case WRITEBK:
	        	accountingSetId = financeType.getFinAEWriteOffBK();
	        	break;
	        case GRACEEND:
	        	accountingSetId = financeType.getFinAEGraceEnd();
	        	break;
	        case SCDCHG:
	        	accountingSetId = financeType.getFinSchdChange();
	        	break;
	        case COMPOUND:
	        	accountingSetId = financeType.getFinAECapitalize();
	        	break;
	        case PROVSN:
	        	accountingSetId = financeType.getFinProvision();
	        	break;
	        case DPRCIATE:
	        	accountingSetId = financeType.getFinDepreciationRule();
	        	break;
	        case ISTBILL:
	        	accountingSetId = "110";//TODO--- Hard code FIXME
	        	break;
	        default:
	        	accountingSetId = "0";
		        break;
	    }
		
		return accountingSetId;
	}
	
	/**
	 * Method for Execution Stage entries for RIA Investments
	 * @param dataSet
	 * @param aeAmountCodes
	 * @param createNow
	 * @param feeRuleDetailMap
	 * @return
	 * @throws AccountNotFoundException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private List<ReturnDataSet> processStageAccountExecution(DataSet dataSet,AEAmountCodes aeAmountCodes
			,String createNow, String roleCode, Map<String, FeeRule> feeRuleDetailMap, FinanceType finType, FinancePremiumDetail premiumDetail) throws AccountNotFoundException, IllegalAccessException, InvocationTargetException{
		logger.debug("Entering");
		
		resetVariables();
		dataSetFiller = new DataSetFiller();

		//Fill Amount Code Detail Object with Respect to Schedule Details
		subHeadRule = new SubHeadRule();

		//Fill Amount Code Detail Object with FinanceMain Object
		doFillDataSetFiller(dataSet ,dataSet.isNewRecord(),  false,finType, premiumDetail);
		setAmountCodes(aeAmountCodes,dataSet.isNewRecord(), false, false);//set data to DataSetFiller Object		

		List<TransactionEntry> transactionEntries = getTransactionEntryDAO().getListTransactionEntryByRefType(
				dataSet.getFinType(), PennantConstants.Accounting, roleCode,  "_AEView",true);
		
		List<ReturnDataSet> returnList = getPrepareAccountingSetResults(dataSet,
				transactionEntries,createNow,false, feeRuleDetailMap, finType);
		resetVariables();
		logger.debug("Leaving");
		return returnList;
	}

	/**
	 * Reset Data Objects after Completion of Execution
	 */
	private void resetVariables() {
		
		customer = null;
		dataSetFiller = null;
		subHeadRule = null;
		currencyNymber = null;
		aeCommitment = null;
	    
    }

	/**
	 * Method for Execution Of Fee & Charges Rules
	 * @param dataSet
	 * @param amountCode
	 * @return
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	private List<FeeRule> processFeeChargesExecResults(DataSet dataSet,
			AEAmountCodes aeAmountCodes, int formatter, boolean isWIF, FinanceType finType) throws IllegalAccessException, InvocationTargetException{
		logger.debug("Entering");
		
		//Prepare AmountCode Details
		resetVariables();
		
		dataSetFiller = new DataSetFiller();
		prepareAmountCodes(dataSet, aeAmountCodes, isWIF, false,finType, null);
		
		//Execute entries depend on Finance Event
		String accountingSetId = getAccSetId(dataSet.getFinEvent(), finType);

		List<FeeRule> feeRules = new ArrayList<FeeRule>();
		String ruleEvent= dataSet.getFinEvent();
		if(ruleEvent.startsWith("ADDDBS")){
			ruleEvent = "ADDDBS";
		}
		
		List<Rule> ruleList = getTransactionEntryDAO().getListFeeChargeRules(Long.valueOf(accountingSetId),
				ruleEvent, "_AView", 0);
		
		FeeRule feeRule ;
		for (Rule rule : ruleList) {
			feeRule = new FeeRule();
			
			//Set Object Data of ReturnDataSet(s)
			feeRule.setFeeCode(rule.getRuleCode());
			feeRule.setFeeCodeDesc(rule.getRuleCodeDesc());
			feeRule.setAddFeeCharges(rule.isAddFeeCharges());
			feeRule.setFeeOrder(rule.getSeqOrder());
			feeRule.setAllowWaiver(rule.isWaiver());
			feeRule.setWaiverPerc(rule.getWaiverPerc());
			
			BigDecimal amount = BigDecimal.ZERO;
			if(rule != null){
				Object result = getRuleExecutionUtil().executeRule(rule.getSQLRule(), dataSetFiller, SystemParameterDetails.getGlobaVariableList(),dataSet.getFinCcy());
				amount = new BigDecimal(result == null ? "0" : result.toString());
				
				if(rule.isAddFeeCharges()){
					dataSetFiller.setDISBURSE(dataSetFiller.getDISBURSE().add(amount));
				}
			}
			
			feeRule.setFeeAmount(amount);
			feeRules.add(feeRule);
		}
		
		resetVariables();
		logger.debug("Leaving");
		return feeRules;
	}
	
	/**
	 * Method for Execution Of Fee & Charges Rules
	 * @param dataSet
	 * @param amountCode
	 * @return
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	private List<FeeRule> processReExecFeeResults(DataSet dataSet,
			AEAmountCodes aeAmountCodes, int formatter, boolean isWIF, FinanceType finType, List<FeeRule> existFeeList) throws IllegalAccessException, InvocationTargetException{
		logger.debug("Entering");
		
		//Prepare AmountCode Details
		resetVariables();
		dataSetFiller = new DataSetFiller();
		prepareAmountCodes(dataSet, aeAmountCodes, isWIF, false,finType, null);
		
		//Execute entries depend on Finance Event
		String accountingSetId = getAccSetId(dataSet.getFinEvent(), finType);

		//Adding Existing Fees
		int feeOrder = 0;
		if(existFeeList != null && !existFeeList.isEmpty()){
			for (FeeRule feeRule : existFeeList) {
				if(feeRule.isAddFeeCharges()){
					dataSetFiller.setDISBURSE(dataSetFiller.getDISBURSE().add(feeRule.getFeeAmount().subtract(
							feeRule.getWaiverAmount()).subtract(feeRule.getPaidAmount())));
					feeOrder = feeRule.getFeeOrder();
				}
			}
		}
		
		List<FeeRule> feeRules = existFeeList;
		String ruleEvent= dataSet.getFinEvent();
		if(ruleEvent.startsWith("ADDDBS")){
			ruleEvent = "ADDDBS";
		}
		
		List<Rule> ruleList = getTransactionEntryDAO().getListFeeChargeRules(Long.valueOf(accountingSetId),ruleEvent, "_AView",feeOrder);
		
		FeeRule feeRule ;
		for (Rule rule : ruleList) {
			feeRule = new FeeRule();
			
			//Set Object Data of ReturnDataSet(s)
			feeRule.setFeeCode(rule.getRuleCode());
			feeRule.setFeeCodeDesc(rule.getRuleCodeDesc());
			feeRule.setAddFeeCharges(rule.isAddFeeCharges());
			feeRule.setFeeOrder(rule.getSeqOrder());
			feeRule.setAllowWaiver(rule.isWaiver());
			feeRule.setWaiverPerc(rule.getWaiverPerc());
			
			BigDecimal amount = BigDecimal.ZERO;
			if(rule != null){
				Object result = getRuleExecutionUtil().executeRule(rule.getSQLRule(), dataSetFiller, SystemParameterDetails.getGlobaVariableList(),dataSet.getFinCcy());
				amount = new BigDecimal(result == null ? "0" : result.toString());
				
				if(rule.isAddFeeCharges()){
					dataSetFiller.setDISBURSE(dataSetFiller.getDISBURSE().add(amount));
				}
			}
			
			feeRule.setFeeAmount(amount);
			feeRules.add(feeRule);
		}
		
		resetVariables();
		logger.debug("Leaving");
		return feeRules;
	}

	/**
	 * Method For execution of Provision Rule For Provision Calculated Amount
	 * @param dataSet
	 * @param aeAmountCodes
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private BigDecimal processProvExecResults(DataSet dataSet, AEAmountCodes aeAmountCodes) throws IllegalAccessException, InvocationTargetException{

		//Fill Amount Code Detail Object with FinanceMain Object
		resetVariables();
		
		dataSetFiller = new DataSetFiller();
		String phase = StringUtils.trimToEmpty(SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_PHASE).toString());
		boolean isEODProcess = false;
		if (phase.equals(PennantConstants.APP_PHASE_EOD)) {
			isEODProcess = true;
		}
		
		FinanceType financeType = null;
		if(isEODProcess){
			financeType = EODProperties.getFinanceType(dataSet.getFinType());
		}else{
			financeType = getFinanceTypeDAO().getFinanceTypeByFinType(dataSet.getFinType());
		}
		doFillDataSetFiller(dataSet ,dataSet.isNewRecord(), false,financeType, null);
		setAmountCodes(aeAmountCodes,dataSet.isNewRecord(), false, false);//set data to DataSetFiller Object	
		BigDecimal provCalAmount = BigDecimal.ZERO;

		String rule = getRuleDAO().getAmountRule("PROV", "PROVSN", "");
		if(rule != null){
			Object result = getRuleExecutionUtil().executeRule(rule, dataSetFiller,SystemParameterDetails.getGlobaVariableList(), dataSet.getFinCcy());
			provCalAmount = new BigDecimal(result == null ? "0" : result.toString());
		}
		
		resetVariables();
		return provCalAmount;
	}
	
	/**
	 * Method for Commitment posting Entries Execution
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws AccountNotFoundException 
	 */
	private List<ReturnDataSet> processCommitmentExecResults(AECommitment aeCommitment, Commitment commitment, String acSetEvent, 
			String createNow, Map<String, FeeRule> feeRuleDetailMap) throws AccountNotFoundException, IllegalAccessException, InvocationTargetException{
		
		logger.debug("Entering");
		resetVariables();
		
		//DataSet Object preparation
		DataSet dataSet = new DataSet();
		dataSet.setFinReference(commitment.getCmtReference());
		dataSet.setFinEvent(acSetEvent);
		dataSet.setPostDate(DateUtility.getSystemDate());
		dataSet.setValueDate(DateUtility.getSystemDate());
		dataSet.setFinBranch(commitment.getCmtBranch());
		dataSet.setFinCcy(commitment.getCmtCcy());
		dataSet.setCmtAccount(commitment.getCmtAccount());
		dataSet.setCustId(commitment.getCustID());		
		dataSet.setOpenNewCmtAc(commitment.isOpenAccount());
		dataSet.setCmtChargeAccount(commitment.getChargesAccount());
		dataSet.setCmtChargeAmount(commitment.getCmtCharges());
		
		//Commitment Execution Data Preparation
		doFillCommitmentData(dataSet, commitment, aeCommitment);
			
		//Accounting Set Details
		List<TransactionEntry> transactionEntries = null;
		long accountingSetId = getAccountingSetDAO().getAccountingSetId(acSetEvent, acSetEvent);
		if (accountingSetId != 0) {
			//get List of transaction entries
			transactionEntries = getTransactionEntryDAO().getListTransactionEntryById(accountingSetId, "_AEView",true);
		}
		
		List<ReturnDataSet> returnDataSets = null;
		if(transactionEntries != null && transactionEntries.size() > 0){
			returnDataSets =  getPrepareAccountingSetResults(dataSet, transactionEntries, createNow,true, feeRuleDetailMap, null);
		}
		
		resetVariables();
		
		logger.debug("Leaving");
		return returnDataSets;
	}	
	
	/**
	 * Method for Preparing List of FeeRule Objects 
	 * @param dataSet
	 * @param amountCode
	 * @return
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	private void prepareAmountCodes(DataSet dataSet,AEAmountCodes aeAmountCodes, boolean isWIF, boolean isAccrualCal, FinanceType financeType, FinancePremiumDetail premiumDetail) 
			throws IllegalAccessException, InvocationTargetException{
		logger.debug("Entering");
		
		subHeadRule = new SubHeadRule();

		//Fill Amount Code Detail Object with FinanceMain Object
		doFillDataSetFiller(dataSet ,dataSet.isNewRecord(), isAccrualCal,financeType, premiumDetail);
		
		setAmountCodes(aeAmountCodes,dataSet.isNewRecord(), isWIF, isAccrualCal);//set data to DataSetFiller Object	
		logger.debug("Leaving");
	}
	
	/**
	 * Method for preparing List of ReturnDataSet objects by executing rules
	 * @param dataSet
	 * @param accountingSetId
	 * @return
	 * @throws AccountNotFoundException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	private List<ReturnDataSet> getPrepareAccountingSetResults(DataSet dataSet, List<TransactionEntry> transactionEntries,
			String createNow,boolean isCommitment, Map<String, FeeRule> feeRuleDetailMap, FinanceType financeType) throws AccountNotFoundException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");
		
		List<ReturnDataSet> returnDataSets = new ArrayList<ReturnDataSet>();
		
		Map<String, Object> accountsMap = new HashMap<String, Object>(transactionEntries.size());
		accountsList = new ArrayList<IAccounts>(transactionEntries.size());
		Map<String, String> accountCcyMap = new HashMap<String, String>(transactionEntries.size());

		//Set Account number generation
		for (TransactionEntry transactionEntry : transactionEntries) {
			
			if("N".equals(createNow)){
				IAccounts account = getAccountNumber(dataSet,transactionEntry,isCommitment,false, financeType, accountsMap, accountCcyMap);
				if(account != null){
					accountsList.add(account);
				}
				
				account = null;
				
			}else{
				IAccounts account = getAccountNumber(dataSet,transactionEntry,isCommitment,true, financeType, accountsMap, accountCcyMap);
				if(account != null){
					accountsList.add(account);
				}
				
				account = null;
			}
		}

		//Calling Core Banking Interface Service
		if("N".equals(createNow)){
			accountsList = getAccountInterfaceService().fetchExistAccount(accountsList,createNow);
		}else{
			if(accountCcyMap.size() > 0){
				accountCcyMap = getAccountInterfaceService().getAccountCurrencyMap(accountCcyMap);
			}
		}
		
		for (IAccounts interfaceAccount : accountsList) {
			if(accountsMap.containsKey(interfaceAccount.getTransOrder().trim())){
				accountsMap.remove(interfaceAccount.getTransOrder());
				accountsMap.put(interfaceAccount.getTransOrder(),interfaceAccount);
			}
		}
		
		accountsList = null;
		
		ReturnDataSet returnDataSet ;
		for (TransactionEntry transactionEntry : transactionEntries) {
			
			returnDataSet = new ReturnDataSet();
			//Set Object Data of ReturnDataSet(s)
			returnDataSet.setFinReference(dataSet.getFinReference());
			returnDataSet.setFinEvent(dataSet.getFinEvent());
			returnDataSet.setLovDescEventCodeName(transactionEntry.getLovDescEventCodeDesc());
			returnDataSet.setCustId(dataSet.getCustId());
			returnDataSet.setTranDesc(transactionEntry.getTransDesc());
			returnDataSet.setPostDate(dataSet.getPostDate());
			returnDataSet.setValueDate(dataSet.getPostDate());
			returnDataSet.setTranCode(transactionEntry.getTranscationCode());
			returnDataSet.setRevTranCode(transactionEntry.getRvsTransactionCode());
			returnDataSet.setDrOrCr(transactionEntry.getDebitcredit());
			returnDataSet.setShadowPosting(transactionEntry.isShadowPosting());
			if("N".equals(createNow)){
				returnDataSet.setAccountType(transactionEntry.getAccount());
			}

			//Post Reference
			String branch = StringUtils.trimToEmpty(transactionEntry.getAccountBranch()).equals("")? dataSet.getFinBranch():transactionEntry.getAccountBranch();
					
			String accType = "";
			if(financeType != null){
				accType = StringUtils.trimToEmpty(transactionEntry.getAccountType()).equals("")? financeType.getFinAcType():transactionEntry.getAccountType();
			}else{
				accType = StringUtils.trimToEmpty(transactionEntry.getAccountType()).equals("")? "":transactionEntry.getAccountType();
			}
			
			returnDataSet.setPostref(branch+"-"+accType+"-"+dataSet.getFinCcy());

			returnDataSet.setPostStatus("S");
			returnDataSet.setAmountType(transactionEntry.getChargeType());
			
			//Set Account Number
			IAccounts acc = (IAccounts)accountsMap.get(transactionEntry.getAccountSetid()+"-"+transactionEntry.getTransOrder());
			if(acc == null){
				continue;
			}
			
			returnDataSet.setTranOrderId(acc.getTransOrder());
			returnDataSet.setAccount(acc.getAccountId());
			returnDataSet.setPostStatus(acc.getFlagPostStatus());
			returnDataSet.setErrorId(acc.getErrorCode());
			returnDataSet.setErrorMsg(acc.getErrorMsg());

			//Regarding to Posting Data
			if(!"N".equals(createNow)){
				returnDataSet.setAccountType(acc.getAcType());
				returnDataSet.setFinType(financeType == null ? "" :financeType.getFinType());
				returnDataSet.setCustCIF(customer.getCustCIF());
				returnDataSet.setFinBranch(dataSet.getFinBranch());
				returnDataSet.setFlagCreateNew(acc.getFlagCreateNew());
				returnDataSet.setFlagCreateIfNF(acc.getFlagCreateIfNF());
				returnDataSet.setInternalAc(acc.getInternalAc());
			}
			
			//Amount Rule Execution for Amount Calculation
			returnDataSet.setPostAmount(executeAmountRule(dataSet.getFinEvent(), transactionEntry, feeRuleDetailMap, dataSet.getFinCcy()));
			
			//Converting Post Amount Based on Account Currency
			returnDataSet.setAcCcy(dataSet.getFinCcy());
			
			BigDecimal postAmount = null;
			List<ReturnDataSet> newEntries = null;
			if("N".equals(createNow)){
				
				if(!dataSet.getFinCcy().equals(acc.getAcCcy())){
					postAmount = returnDataSet.getPostAmount();
					returnDataSet.setPostAmount(CalculationUtil.getConvertedAmount(dataSet.getFinCcy(), acc.getAcCcy(), postAmount));
					
					//Add Extra Entries For Debit & Credit
					newEntries = createAccOnCCyConversion(returnDataSet,acc.getAcCcy(), postAmount, dataSet);
					
					returnDataSet.setAcCcy(acc.getAcCcy());
				}
			}else{
				
				if(accountCcyMap.containsKey(acc.getAccountId())){
					String acCcy = accountCcyMap.get(acc.getAccountId());
					
					if(!dataSet.getFinCcy().equals(acCcy)){
						postAmount = returnDataSet.getPostAmount();
						returnDataSet.setPostAmount(CalculationUtil.getConvertedAmount(dataSet.getFinCcy(), acCcy, postAmount));
						
						//Add Extra Entries For Debit & Credit
						newEntries = createAccOnCCyConversion(returnDataSet, acCcy,  postAmount, dataSet);
						
						returnDataSet.setAcCcy(acCcy);
					}
				}
			}
			
			returnDataSets.add(returnDataSet);
			if(newEntries != null){
				returnDataSets.addAll(newEntries);
			}
		}
		
		accountsMap = null;
		accountCcyMap = null;

		logger.debug("Leaving");
		return returnDataSets;
	}
	
	private List<ReturnDataSet> createAccOnCCyConversion(ReturnDataSet existDataSet, String acCcy, BigDecimal unconvertedPostAmt, DataSet dataSet){
		
		List<ReturnDataSet> newEntries = new ArrayList<ReturnDataSet>(2);
		String actTranType = existDataSet.getDrOrCr();
		String phase = StringUtils.trimToEmpty(SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_PHASE).toString());
		String finCcyNum = "";
		String acCcyNum = "";
		int formatter = 0;
		
		if(phase.equals(PennantConstants.APP_PHASE_EOD)){
			finCcyNum = EODProperties.getCcyNumber(dataSet.getFinCcy());
			acCcyNum = EODProperties.getCcyNumber(acCcy);
		}else{
			finCcyNum = getCurrencyDAO().getCurrencyById(dataSet.getFinCcy());
			
			Currency currency = getCurrencyDAO().getCurrencyByCode(acCcy);
			acCcyNum = currency.getCcyNumber();
			formatter = currency.getCcyEditField();
		}
		
		String drCr = actTranType.equals("D") ? "C" : "D";
		String crDr = actTranType.equals("D") ? "D" : "C";
		
		Cloner cloner = new Cloner();
		ReturnDataSet newDataSet1 = cloner.deepClone(existDataSet);
		newDataSet1.setDrOrCr(drCr);
		newDataSet1.setAcCcy(acCcy);
		newDataSet1.setTranOrder(existDataSet.getTranOrder() + 1);
		newDataSet1.setTranCode(SystemParameterDetails.getSystemParameterValue("CCYCNV_"+drCr+"RTRANCODE").toString());
		newDataSet1.setRevTranCode(SystemParameterDetails.getSystemParameterValue("CCYCNV_"+crDr+"RTRANCODE").toString());
		newDataSet1.setAccount(existDataSet.getAccount().substring(0, 4)+"881"+acCcyNum+finCcyNum);
		newDataSet1.setFormatter(formatter);
		newEntries.add(newDataSet1);
		
		cloner = new Cloner();
		ReturnDataSet newDataSet2 = cloner.deepClone(existDataSet);
		newDataSet2.setDrOrCr(crDr);
		newDataSet2.setTranOrder(existDataSet.getTranOrder() + 2);
		newDataSet2.setTranCode(SystemParameterDetails.getSystemParameterValue("CCYCNV_"+crDr+"RTRANCODE").toString());
		newDataSet2.setRevTranCode(SystemParameterDetails.getSystemParameterValue("CCYCNV_"+drCr+"RTRANCODE").toString());
		newDataSet2.setAccount(dataSet.getFinBranch()+"881"+finCcyNum+acCcyNum);
		newDataSet2.setPostAmount(unconvertedPostAmt);
		newEntries.add(newDataSet2);
		
		existDataSet.setFormatter(formatter);
		
		return newEntries;
	}
	
	/**
	 * Method for Execution of Amount Rule and Getting Amount
	 * 
	 *  <br> IN AccountEngineExecution.java
	 * @param event
	 * @param transactionEntry
	 * @param feeRuleDetailMap
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException  BigDecimal
	 */
	private BigDecimal executeAmountRule(String event, TransactionEntry transactionEntry ,
			Map<String, FeeRule> feeRuleDetailMap, String finCcy) throws IllegalAccessException, InvocationTargetException{
		logger.debug("Entering");

		//Execute Transaction Entry Rule
		BigDecimal amount = BigDecimal.ZERO;
		if(event.startsWith("ADDDBS")){
			event = "ADDDBS";
		}

		String amountRule = transactionEntry.getAmountRule();
		String[] res = transactionEntry.getFeeCode().split(",");
		
		if(res.length > 0){
			for (int i = 0; i < res.length; i++) {

				if(!(StringUtils.trimToEmpty(res[i]).equals("") || res[i].equalsIgnoreCase("Result"))){

					if(feeRuleDetailMap != null && feeRuleDetailMap.containsKey(res[i].trim().substring(0,
							res[i].trim().contains("_") ? res[i].trim().indexOf('_'): res[i].trim().length()))){
						FeeRule feeRule = feeRuleDetailMap.get(res[i].trim().substring(0,
								res[i].trim().contains("_") ? res[i].trim().indexOf('_'): res[i].trim().length()));
						
						if(amountRule.contains(res[i].trim()+"_W")){
							amountRule = amountRule.replace(res[i].trim()+"_W",feeRule.getWaiverAmount().toString());
						}
						if(amountRule.contains(res[i].trim()+"_C")){
							amountRule = amountRule.replace(res[i].trim()+"_C",feeRule.getFeeAmount().toString());
						}
						if(amountRule.contains(res[i].trim()+"_P")){
							amountRule = amountRule.replace(res[i].trim()+"_P",feeRule.getPaidAmount().toString());
						}
						
					}else{

						Rule rule = getRuleDAO().getRuleByID(res[i].trim(), "FEES",event , "");
						if (event.equals(PennantConstants.NEWCMT)|| event.equals(PennantConstants.MNTCMT)) {

							if(rule != null){
								try {
									Object result = getRuleExecutionUtil().executeRule(rule.getSQLRule(), aeCommitment,
											SystemParameterDetails.getGlobaVariableList(),finCcy);
									amount = new BigDecimal(result == null ? "0" : result.toString());
									
									if(amountRule.contains(res[i].trim()+"_W")){
										amountRule = amountRule.replace(res[i].trim()+"_W","0");
									}
									if(amountRule.contains(res[i].trim()+"_C")){
										amountRule = amountRule.replace(res[i].trim()+"_C",amount.toString());
									}
									if(amountRule.contains(res[i].trim()+"_P")){
										amountRule = amountRule.replace(res[i].trim()+"_P","0");
									}
									
								}catch (Exception e) {
									e.printStackTrace();
								}
							}

						}else{

							if(rule != null){
								try {
									Object result = getRuleExecutionUtil().executeRule(rule.getSQLRule(), dataSetFiller,
											SystemParameterDetails.getGlobaVariableList(),finCcy);
									amount = new BigDecimal(result == null ? "0" : result.toString());
									
									if(amountRule.contains(res[i].trim()+"_W")){
										amountRule = amountRule.replace(res[i].trim()+"_W","0");
									}
									if(amountRule.contains(res[i].trim()+"_C")){
										amountRule = amountRule.replace(res[i].trim()+"_C",amount.toString());
									}
									if(amountRule.contains(res[i].trim()+"_P")){
										amountRule = amountRule.replace(res[i].trim()+"_P","0");
									}
									
								}catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
			}
		}

		Object result = null;

		if (event.contains("CMT")) {
			result = getRuleExecutionUtil().executeRule(amountRule, aeCommitment,
					SystemParameterDetails.getGlobaVariableList(),finCcy);
		}else{
			result = getRuleExecutionUtil().executeRule(amountRule, dataSetFiller,
					SystemParameterDetails.getGlobaVariableList(),finCcy);
		}
		amount = new BigDecimal(result == null ? "0" : result.toString());
		

		logger.debug("Leaving");
		return amount;
	}

	/**
	 * Fill Data For DataFiller Object depend on Event Condition
	 * @param dataSet
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	private void doFillDataSetFiller(DataSet dataSet,boolean isNewFinance, boolean isAccrualCal,FinanceType financeType, FinancePremiumDetail premiumDetail) throws IllegalAccessException, InvocationTargetException{

		logger.debug("Entering");
		customer = getCustomerDAO().getCustomerForPostings(dataSet.getCustId());
		if(customer != null){
			
			dataSetFiller.setCustCIF(customer.getCustCIF());
			dataSetFiller.setCustCOB(customer.getCustCOB());
			dataSetFiller.setCustCtgCode(customer.getCustCtgCode());
			dataSetFiller.setCustIndustry(customer.getCustIndustry());
			dataSetFiller.setCustIsStaff(customer.isCustIsStaff());
			dataSetFiller.setCustNationality(customer.getCustNationality());
			dataSetFiller.setCustParentCountry(customer.getCustParentCountry());
			dataSetFiller.setCustResdCountry(customer.getCustResdCountry());
			dataSetFiller.setCustRiskCountry(customer.getCustRiskCountry());
			dataSetFiller.setCustSector(customer.getCustSector());
			dataSetFiller.setCustSubSector(customer.getCustSubSector());
			dataSetFiller.setCustTypeCode(customer.getCustTypeCode());
			
		}

		dataSetFiller.setReqCampaign("");//TODO Future USE
		dataSetFiller.setReqFinAcType(financeType.getFinAcType());
		dataSetFiller.setReqFinCcy(dataSet.getFinCcy());
		dataSetFiller.setReqFinType(financeType.getFinType());
		dataSetFiller.setReqProduct(financeType.getFinCategory());
		dataSetFiller.setTerms(dataSet.getNoOfTerms());
		dataSetFiller.setTenure(dataSet.getTenure());
		dataSetFiller.setDOWNPAY(dataSet.getDownPayment());
		dataSetFiller.setDOWNPAYB(dataSet.getDownPayBank());
		dataSetFiller.setDOWNPAYS(dataSet.getDownPaySupl());
		dataSetFiller.setReqFinPurpose(dataSet.getFinPurpose());
		dataSetFiller.setFinJointAcCount(dataSet.getFinJointAcCount());
		dataSetFiller.setSECDEPST(dataSet.getSecurityDeposit());
		dataSetFiller.setDISBURSE(dataSet.getDisburseAmount());
		dataSetFiller.setRETAMT(dataSet.getCurDisbRet());
		dataSetFiller.setNETRET(dataSet.getNetRetDue());
		dataSetFiller.setGRCPFTTB(dataSet.getGrcPftTillNow());
		dataSetFiller.setGRCPFTCH(dataSet.getGrcPftChg());
		dataSetFiller.setADVDUE(dataSet.getAdvDue());
		dataSetFiller.setCLAIMAMT(dataSet.getClaimAmt());
		dataSetFiller.setFEEAMOUNT(dataSet.getFeeAmount());

		int frqDfrCount = 0;
		int rpyDfrCount = 0;
		if(!isAccrualCal){
			rpyDfrCount = getDefermentHeaderDAO().getRpyDfrCount(dataSet.getFinReference());
			if(dataSet.getSchdDate() != null){
				frqDfrCount = getFinanceScheduleDetailDAO().getFrqDfrCount(dataSet.getFinReference(),
						DateUtility.formatDate(dataSet.getSchdDate(), PennantConstants.DBDateFormat) );
			}
		}
		dataSetFiller.setFrqDfrCount(frqDfrCount);
		if(dataSet.getCurRpyDefCount() > rpyDfrCount){
			dataSetFiller.setRpyDfrCount(dataSet.getCurRpyDefCount() - rpyDfrCount);
		}else{
			dataSetFiller.setRpyDfrCount(rpyDfrCount);
		}
		
		if(financeType.getFinCategory().equals(PennantConstants.FINANCE_PRODUCT_SUKUK)){
			if(premiumDetail != null){
				
				dataSetFiller.setFACEVAL(premiumDetail.getFaceValue().multiply(new BigDecimal(premiumDetail.getNoOfUnits())));
				dataSetFiller.setPRMVALUE(dataSet.getFinAmount().subtract(dataSetFiller.getFACEVAL()));
				Date curBussDate = (Date) SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_CUR);
				int totalDays = DateUtility.getDaysBetween(dataSet.getMaturityDate(), premiumDetail.getPurchaseDate());
				int curPrmDays = DateUtility.getDaysBetween(curBussDate, premiumDetail.getPurchaseDate());
				
				dataSetFiller.setPRMAMZ(dataSetFiller.getPRMVALUE().multiply(new BigDecimal(curPrmDays)).divide(
						new BigDecimal(totalDays), 0, RoundingMode.HALF_DOWN));
				dataSetFiller.setACCRBAL(premiumDetail.getAccruedProfit());
				
				//Calculation Of  Fair Value ReValuation Amount
				if(!isNewFinance && dataSet.getFinEvent().equals("COMPOUND")){
					BigDecimal prvFairValueAmount = getFinancePremiumDetailDAO().getFairValueAmount(dataSet.getFinReference(), "");
					if(prvFairValueAmount != null){
						dataSetFiller.setREVALAMT(premiumDetail.getFairValueAmount().subtract(prvFairValueAmount));
					}
				}
			}
		}

		dataSetFiller.setReqFinBranch(dataSet.getFinBranch());
		dataSetFiller.setFinAmount(dataSet.getFinAmount());
		dataSetFiller.setFINAMT(dataSet.getFinAmount());
		dataSetFiller.setNewLoan(dataSet.isNewRecord());
		logger.debug("Leaving");
	}
	
	private void doFillCommitmentData(DataSet dataSet, Commitment commitment, 
			AECommitment aeCmt) throws IllegalAccessException, InvocationTargetException{

		logger.debug("Entering");
		aeCommitment = aeCmt;
		customer = getCustomerDAO().getCustomerForPostings(dataSet.getCustId());
		if(customer != null){
			BeanUtils.copyProperties(aeCommitment, customer);
		}
	
		aeCommitment.setCmtAmount(commitment.getCmtAmount());
		aeCommitment.setCmtAmountOther(commitment.getCmtAvailable());//TODO
		aeCommitment.setCmtUtilized(commitment.getCmtUtilizedAmount());
		aeCommitment.setCmtUtilizedOther(commitment.getCmtUtilizedAmount());//TODO
		aeCommitment.setCmtMultiBrach(commitment.isMultiBranch());
		aeCommitment.setCmtRevolving(commitment.isRevolving());
		aeCommitment.setCmtShared(commitment.isSharedCmt());
		aeCommitment.setCmtUsedEarlier(commitment.isNonperformingStatus());//TODO

		logger.debug("Leaving");
	}

	/**
	 * Method for Prepare Account Number based on  Transaction Entry Account
	 * @param dataSet
	 * @param transactionEntry
	 * @param finType
	 * @return
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	private IAccounts getAccountNumber(DataSet dataSet,TransactionEntry transactionEntry, boolean isCommitment,boolean createNow,
			FinanceType financeType, Map<String, Object> accountsMap, Map<String, String> accountCcyMap) throws IllegalAccessException, InvocationTargetException{

		logger.debug("Entering");
		IAccounts newAccount = new IAccounts();
		newAccount.setAcCustCIF(customer.getCustCIF());
		newAccount.setAcBranch(dataSet.getFinBranch());
		newAccount.setAcCcy(dataSet.getFinCcy());
		newAccount.setFlagCreateIfNF(true);
		newAccount.setFlagCreateNew(false);
		newAccount.setInternalAc(false);
		
		String tranOrder = String.valueOf(transactionEntry.getAccountSetid()+"-"+transactionEntry.getTransOrder());
		newAccount.setTransOrder(tranOrder);
		newAccount.setTranAc(transactionEntry.getAccount());

		//Set Disbursement Account
		if(transactionEntry.getAccount().equals(PennantConstants.DISB)){
			
			newAccount.setAcType("DISB");
			newAccount.setFlagCreateIfNF(false);
			newAccount.setAccountId(dataSet.getDisburseAccount());
			
			if(!createNow){
				accountsMap.put(tranOrder, transactionEntry.getAccount()+"-"+transactionEntry.getTransOrder());
			}else{
				accountsMap.put(tranOrder,transactionEntry.getAccount());
			}
			
			//Account Saving For Currency Conversion Check
			if(!StringUtils.trimToEmpty(dataSet.getDisburseAccount()).equals("") && 
					!accountCcyMap.containsKey(dataSet.getDisburseAccount())){
				accountCcyMap.put(dataSet.getDisburseAccount(), "");
			}
			
			return newAccount;
		}

		//Set Customer Repayments Account
		if(transactionEntry.getAccount().equals(PennantConstants.REPAY)){
			
			newAccount.setAcType("REPAY");
			
			if(isCommitment){
				if(dataSet.getCmtChargeAmount().compareTo(BigDecimal.ZERO) != 0){
					newAccount.setFlagCreateIfNF(true);
					newAccount.setAccountId(dataSet.getCmtChargeAccount());
					
					//Account Saving For Currency Conversion Check
					if(!StringUtils.trimToEmpty(dataSet.getCmtChargeAccount()).equals("") && 
							!accountCcyMap.containsKey(dataSet.getCmtChargeAccount())){
						accountCcyMap.put(dataSet.getCmtChargeAccount(), "");
					}
				}else{
					logger.debug("Leaving");
					return null;
				}
			}else{
				newAccount.setFlagCreateIfNF(false);
				newAccount.setAccountId(dataSet.getRepayAccount());
				
				//Account Saving For Currency Conversion Check
				if(!StringUtils.trimToEmpty(dataSet.getRepayAccount()).equals("") && 
						!accountCcyMap.containsKey(dataSet.getRepayAccount())){
					accountCcyMap.put(dataSet.getRepayAccount(), "");
				}
			}
			
			if(!createNow){
				accountsMap.put(tranOrder, transactionEntry.getAccount()+"-"+transactionEntry.getTransOrder());
			}else{
				accountsMap.put(tranOrder,transactionEntry.getAccount());
			}
			
			//Account Saving For Currency Conversion Check
			accountCcyMap.put(dataSet.getDisburseAccount(), "");
			
			logger.debug("Leaving");
			return newAccount;
		}
		
		//Set Disbursement Account
		if(transactionEntry.getAccount().equals(PennantConstants.DOWNPAY)){
			
			newAccount.setAcType("DOWNPAY");
			newAccount.setFlagCreateIfNF(false);
			newAccount.setAccountId(dataSet.getDownPayAccount());
			
			if(!createNow){
				accountsMap.put(tranOrder, transactionEntry.getAccount()+"-"+transactionEntry.getTransOrder());
			}else{
				accountsMap.put(tranOrder,transactionEntry.getAccount());
			}
			
			//Account Saving For Currency Conversion Check
			if(!StringUtils.trimToEmpty(dataSet.getDownPayAccount()).equals("") && 
					!accountCcyMap.containsKey(dataSet.getRepayAccount())){
				accountCcyMap.put(dataSet.getDownPayAccount(), "");
			}
			
			return newAccount;
		}
		
		//Set GL&PL Account
		if(transactionEntry.getAccount().equals(PennantConstants.GLNPL)){
			
			newAccount.setAcType(transactionEntry.getAccountType());
			newAccount.setInternalAc(true);
			newAccount.setAccountId(generateAccount(dataSet, transactionEntry.getAccountType(), false,
					transactionEntry.getAccountSubHeadRule(), transactionEntry.getDebitcredit()));
			
			if(!createNow){
				accountsMap.put(tranOrder, transactionEntry.getAccount() +"-"+transactionEntry.getTransOrder());
			}else{
				accountsMap.put(tranOrder,transactionEntry.getAccount()+transactionEntry.getAccountType());
			}
			
			logger.debug("Leaving");
			return newAccount;
		}
		
		//Set Customer Loan Account
		if(transactionEntry.getAccount().equals(PennantConstants.FIN)){
			
			newAccount.setAcType(financeType.getFinAcType());
			if(!StringUtils.trimToEmpty(dataSet.getFinAccount()).equals("")){
				newAccount.setAccountId(dataSet.getFinAccount());
			}else{
				
				if(financeType.isFinIsOpenNewFinAc()== true){
					newAccount.setFlagCreateNew(true);
				}
			}

			if(!createNow){
				accountsMap.put(tranOrder, transactionEntry.getAccount() +"-"+transactionEntry.getTransOrder());
			}else{
				accountsMap.put(tranOrder,transactionEntry.getAccount());
			}
			
			logger.debug("Leaving");
			return newAccount;
		}
		
		//Finance Unearned Profit Account
		if (transactionEntry.getAccount().equals(PennantConstants.UNEARN)) {

			newAccount.setAcType(financeType.getPftPayAcType());

			if (!createNow) {
				accountsMap.put(tranOrder, transactionEntry.getAccount() +"-"+transactionEntry.getTransOrder());
			} else {
				accountsMap.put(tranOrder, transactionEntry.getAccount());
			}

			logger.debug("Leaving");
			return newAccount;
		}
		
		//Finance Unearned Suspense Account
		if (transactionEntry.getAccount().equals(PennantConstants.SUSP)) {

			newAccount.setAcType(financeType.getFinSuspAcType());

			if (!createNow) {
				accountsMap.put(tranOrder, transactionEntry.getAccount() +"-"+transactionEntry.getTransOrder());
			} else {
				accountsMap.put(tranOrder, transactionEntry.getAccount());
			}

			logger.debug("Leaving");
			return newAccount;
		}
		
		//Finance Provision Account
		if (transactionEntry.getAccount().equals(PennantConstants.PROVSN)) {

			newAccount.setAcType(financeType.getFinProvisionAcType());

			if (!createNow) {
				accountsMap.put(tranOrder, transactionEntry.getAccount() +"-"+transactionEntry.getTransOrder());
			} else {
				accountsMap.put(tranOrder, transactionEntry.getAccount());
			}

			logger.debug("Leaving");
			return newAccount;
		}

		//Set Customer Loan Account
		if(transactionEntry.getAccount().equals(PennantConstants.CUSTSYS)){
			
			newAccount.setAcType(transactionEntry.getAccountType());
			newAccount.setFlagCreateNew(transactionEntry.isOpenNewFinAc());

			if(!createNow){
				accountsMap.put(tranOrder, transactionEntry.getAccount() +"-"+transactionEntry.getTransOrder());
			}else{
				accountsMap.put(tranOrder,transactionEntry.getAccount()+transactionEntry.getAccountType());
			}
			
			logger.debug("Leaving");
			return newAccount;
		}
		
		//Set Customer Loan Account
		if(transactionEntry.getAccount().equals(PennantConstants.COMMIT)){
			
			String acType = SystemParameterDetails.getSystemParameterValue("COMMITMENT_AC_TYPE").toString();
			newAccount.setAcType(acType);
			newAccount.setFlagCreateNew(dataSet.isOpenNewCmtAc());
			newAccount.setAccountId("");
			if(!dataSet.isOpenNewCmtAc()){
				newAccount.setAccountId(StringUtils.trimToEmpty(dataSet.getCmtAccount()));
			}
			
			if(!dataSet.getFinEvent().equals(PennantConstants.NEWCMT)){
				newAccount.setFlagCreateNew(false);
				newAccount.setAccountId(StringUtils.trimToEmpty(dataSet.getCmtAccount()));
			}

			if(!createNow){
				accountsMap.put(tranOrder, transactionEntry.getAccount() +"-"+transactionEntry.getTransOrder());
			}else{
				accountsMap.put(tranOrder,transactionEntry.getAccount()+transactionEntry.getAccountType());
			}
			
			logger.debug("Leaving");
			return newAccount;
		}
		
		//Set Build Account
		if(transactionEntry.getAccount().equals(PennantConstants.BUILD)){
			
			newAccount.setAcType(transactionEntry.getAccountType());
			newAccount.setInternalAc(true);
			newAccount.setAccountId(generateAccount(dataSet, transactionEntry.getAccountType(), true,
					transactionEntry.getAccountSubHeadRule(), transactionEntry.getDebitcredit()));
			
			if(!createNow){
				accountsMap.put(tranOrder, transactionEntry.getAccount() +"-"+transactionEntry.getTransOrder());
			}else{
				accountsMap.put(tranOrder,transactionEntry.getAccount()+transactionEntry.getAccountType());
			}
			
			logger.debug("Leaving");
			return newAccount;
		}

		logger.debug("Leaving");
		return newAccount;
	}

	/**
	 * Generate Account Number For GLNPL Account
	 * @param finBranch
	 * @param finCcy
	 * @param accountType
	 * @param subHeadRuleCode
	 * @param dbOrCr
	 * @return
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	private String generateAccount(DataSet dataSet, String accountType, boolean isBuildAc, String subHeadRuleCode,
			String dbOrCr) throws IllegalAccessException, InvocationTargetException{
		
		String phase = StringUtils.trimToEmpty(SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_PHASE).toString());
		boolean isEODProcess = false;
		if (phase.equals(PennantConstants.APP_PHASE_EOD)) {
			isEODProcess = true;
		}
		
		//System Internal Account Number Fetching
		String sysIntAcNum = "";
		if(isEODProcess){
			sysIntAcNum = EODProperties.getSIANumber(accountType);
		}else{
			sysIntAcNum = getInternalAccountDefinitionDAO().getSysIntAccNum(accountType);
		}

		if(isEODProcess){
			currencyNymber = EODProperties.getCcyNumber(dataSet.getFinCcy());
		}else{
			if(currencyNymber == null){
				currencyNymber = getCurrencyDAO().getCurrencyById(dataSet.getFinCcy());
			}
		}
		
		int glplAcLength = sysIntAcNum.length() - Integer.parseInt(SystemParameterDetails.getSystemParameterValue("SYSINT_ACCOUNT_LEN").toString());
		String accNumber = dataSet.getFinBranch()+sysIntAcNum.substring(glplAcLength)+currencyNymber;

		if(!(StringUtils.trimToEmpty(subHeadRuleCode).equals(""))){
			String amountRule = null;
			if(isEODProcess){
				amountRule =  EODProperties.getSubHeadRule(subHeadRuleCode);
			}else{
				amountRule = getRuleDAO().getAmountRule(subHeadRuleCode, "SUBHEAD", "");
			}
			
			if(subHeadRule == null){
				subHeadRule = new SubHeadRule();
			}

			//Data Already Fill or not
			if(!subHeadRule.isProcessed()){
				subHeadRule.setProcessed(true);
				if(aeCommitment != null){
					BeanUtils.copyProperties(subHeadRule, aeCommitment);
					subHeadRule.setReqFinType("");
					subHeadRule.setReqFinAcType("");
					subHeadRule.setReqProduct("");
				}else{
					BeanUtils.copyProperties(subHeadRule, dataSetFiller);
				}
			}
			
			Object result = getRuleExecutionUtil().executeRule(amountRule, subHeadRule, SystemParameterDetails.getGlobaVariableList(),dataSet.getFinCcy());
			
			String subHeadCode = result == null ? "" : result.toString();
			if (StringUtils.trimToEmpty(subHeadCode).contains(".")) {
				subHeadCode = subHeadCode.substring(0, subHeadCode.indexOf('.'));
			}
			
			if(isBuildAc){
				logger.debug("Leaving");
				return subHeadCode;
			}
			
			if(StringUtils.trimToEmpty(subHeadCode).equals("")){
				logger.debug("Leaving");
				return accNumber;
			}else{
				String sIANumber = sysIntAcNum.substring(glplAcLength, (sysIntAcNum.length() - subHeadCode.length()));
				logger.debug("Leaving");
				return (dataSet.getFinBranch()+sIANumber+subHeadCode+currencyNymber);
			}
			
		}else{
			logger.debug("Leaving");
			return accNumber;
		}
	}

	/**
	 * Method for Calculate the Amount Codes By Execution Formulae
	 * @param amountCode
	 * @return
	 */
	private void setAmountCodes(AEAmountCodes aeAmountCodes,boolean isNewFinance, boolean isWIF, boolean isAccrualCal){
		logger.debug("Entering");

		dataSetFiller.setPFT(aeAmountCodes.getPft());
		dataSetFiller.setPFTS(aeAmountCodes.getPftS());
		dataSetFiller.setPFTSP(aeAmountCodes.getPftSP());
		dataSetFiller.setPFTSB(aeAmountCodes.getPftSB());
		dataSetFiller.setPFTAP(aeAmountCodes.getPftAP());
		dataSetFiller.setPFTAB(aeAmountCodes.getPftAB());
		dataSetFiller.setPRI(aeAmountCodes.getPri());
		dataSetFiller.setPRIS(aeAmountCodes.getPriS());
		dataSetFiller.setPRISP(aeAmountCodes.getPriSP());
		dataSetFiller.setPRISB(aeAmountCodes.getPriSB());
		dataSetFiller.setPRIAP(aeAmountCodes.getPriAP());
		dataSetFiller.setPRIAB(aeAmountCodes.getPriAB());
		dataSetFiller.setDACCRUE(aeAmountCodes.getDAccrue());
		dataSetFiller.setNACCRUE(aeAmountCodes.getNAccrue());
		dataSetFiller.setRPPFT(aeAmountCodes.getRpPft());
		dataSetFiller.setRPPRI(aeAmountCodes.getRpPri());
		dataSetFiller.setRPTOT(aeAmountCodes.getRpTot());
		dataSetFiller.setACCRUE(aeAmountCodes.getAccrue());
		dataSetFiller.setACCRUE_S(aeAmountCodes.getAccrueS());
		dataSetFiller.setACCRUETSFD(aeAmountCodes.getAccrueTsfd());
		dataSetFiller.setREFUND(aeAmountCodes.getRefund());
		dataSetFiller.setINSREFUND(aeAmountCodes.getInsRefund());
		dataSetFiller.setCPZTOT(aeAmountCodes.getCpzTot());
		dataSetFiller.setCPZPRV(aeAmountCodes.getCpzPrv());
		dataSetFiller.setCPZCUR(aeAmountCodes.getCpzCur());
		dataSetFiller.setCPZNXT(aeAmountCodes.getCpzNxt());

		dataSetFiller.setCPNoOfDays(aeAmountCodes.getCPNoOfDays());
		dataSetFiller.setCpDaysTill(aeAmountCodes.getCpDaysTill());
		dataSetFiller.setTPPNoOfDays(aeAmountCodes.getTtlDays());
		dataSetFiller.setDaysDiff(aeAmountCodes.getDaysDiff());

		dataSetFiller.setFinOverDueCntInPast(aeAmountCodes.getODInst());
		dataSetFiller.setODDays(aeAmountCodes.getODDays());
		dataSetFiller.setODInst(aeAmountCodes.getODInst());
		
		if(aeAmountCodes.getODInst() > 0){
			dataSetFiller.setFinOverDueInPast(true);
		}else{
			dataSetFiller.setFinOverDueInPast(false);
		}

		BigDecimal actualTotalSchdProfit = BigDecimal.ZERO;
		BigDecimal actualTotalCpzProfit = BigDecimal.ZERO;

		if(!isAccrualCal && !isNewFinance && !isWIF){
			List<BigDecimal> list= getFinanceMainDAO().getActualPftBal(aeAmountCodes.getFinReference(),"");
			actualTotalSchdProfit = list.get(0);
			actualTotalCpzProfit = list.get(1);;
		}

		dataSetFiller.setPFTCHG(aeAmountCodes.getPft().subtract(actualTotalSchdProfit));
		dataSetFiller.setCPZCHG(aeAmountCodes.getCpzTot().subtract(actualTotalCpzProfit));
		dataSetFiller.setPROVAMT(aeAmountCodes.getProvAmt());
		
		dataSetFiller.setELPDAYS(aeAmountCodes.getElpDays());
		dataSetFiller.setELPMNTS(aeAmountCodes.getElpMnts());
		dataSetFiller.setELPTERMS(aeAmountCodes.getElpTerms());
		dataSetFiller.setTTLDAYS(aeAmountCodes.getTtlDays());
		dataSetFiller.setTTLMNTS(aeAmountCodes.getTtlMnts());
		dataSetFiller.setTTLTERMS(aeAmountCodes.getTtlTerms());
		
		dataSetFiller.setPROVDUE(aeAmountCodes.getPROVDUE());
		dataSetFiller.setSUSPNOW(aeAmountCodes.getSUSPNOW());
		dataSetFiller.setSUSPRLS(aeAmountCodes.getSUSPRLS());
		dataSetFiller.setPENALTY(aeAmountCodes.getPENALTY());
		dataSetFiller.setWAIVER(aeAmountCodes.getWAIVER());
		dataSetFiller.setODCPLShare(aeAmountCodes.getODCPLShare());
		dataSetFiller.setAstValO(aeAmountCodes.getAstValO());
		
		dataSetFiller.setDEFFEREDCOST(aeAmountCodes.getDEFFEREDCOST());
		dataSetFiller.setCURRETBILL(aeAmountCodes.getCURRETBILL());
		dataSetFiller.setTTLRETBILL(aeAmountCodes.getTTLRETBILL());
		
		dataSetFiller.setACCDPRPRI(aeAmountCodes.getAccumulatedDepPri());
		dataSetFiller.setDPRPRI(aeAmountCodes.getDepreciatePri());
		
		logger.debug("Leaving");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public static SystemInternalAccountDefinitionDAO getInternalAccountDefinitionDAO() {
		return internalAccountDefinitionDAO;
	}
	public void setInternalAccountDefinitionDAO(SystemInternalAccountDefinitionDAO internalAccountDefinitionDAO) {
		AccountEngineExecution.internalAccountDefinitionDAO = internalAccountDefinitionDAO;
	}

	public void setRuleDAO(RuleDAO ruleDAO) {
		AccountEngineExecution.ruleDAO = ruleDAO;
	}
	public static RuleDAO getRuleDAO() {
		return ruleDAO;
	}

	public void setFinanceTypeDAO(FinanceTypeDAO financeTypeDAO) {
		AccountEngineExecution.financeTypeDAO = financeTypeDAO;
	}
	public static FinanceTypeDAO getFinanceTypeDAO() {
		return financeTypeDAO;
	}

	public static TransactionEntryDAO getTransactionEntryDAO() {
		return transactionEntryDAO;
	}
	public void setTransactionEntryDAO(
			TransactionEntryDAO transactionEntryDAO) {
		AccountEngineExecution.transactionEntryDAO = transactionEntryDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		AccountEngineExecution.financeMainDAO = financeMainDAO;
	}
	public static FinanceMainDAO getFinanceMainDAO() {
		return financeMainDAO;
	}

	public static CustomerDAO getCustomerDAO() {
		return customerDAO;
	}
	public void setCustomerDAO(CustomerDAO customerDAO) {
		AccountEngineExecution.customerDAO = customerDAO;
	}
	public static FinanceScheduleDetailDAO getFinanceScheduleDetailDAO() {
		return financeScheduleDetailDAO;
	}
	public void setFinanceScheduleDetailDAO(
			FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		AccountEngineExecution.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	public void setCurrencyDAO(CurrencyDAO currencyDAO) {
		AccountEngineExecution.currencyDAO = currencyDAO;
	}
	public static CurrencyDAO getCurrencyDAO() {
		return currencyDAO;
	}

	public static DefermentHeaderDAO getDefermentHeaderDAO() {
		return defermentHeaderDAO;
	}
	public void setDefermentHeaderDAO(DefermentHeaderDAO defermentHeaderDAO) {
		AccountEngineExecution.defermentHeaderDAO = defermentHeaderDAO;
	}

	public void setFinanceSuspHeadDAO(FinanceSuspHeadDAO financeSuspHeadDAO) {
		AccountEngineExecution.financeSuspHeadDAO = financeSuspHeadDAO;
	}
	public static FinanceSuspHeadDAO getFinanceSuspHeadDAO() {
		return financeSuspHeadDAO;
	}

	public void setAccountInterfaceService(AccountInterfaceService accountInterfaceService) {
		AccountEngineExecution.accountInterfaceService = accountInterfaceService;
	}
	public static AccountInterfaceService getAccountInterfaceService() {
		return accountInterfaceService;
	}

	public void setRuleExecutionUtil(RuleExecutionUtil ruleExecutionUtil) {
		AccountEngineExecution.ruleExecutionUtil = ruleExecutionUtil;
    }
	public static RuleExecutionUtil getRuleExecutionUtil() {
	    return ruleExecutionUtil;
    }

	public static AccountingSetDAO getAccountingSetDAO() {
    	return accountingSetDAO;
    }
	public void setAccountingSetDAO(AccountingSetDAO accountingSetDAO) {
		AccountEngineExecution.accountingSetDAO = accountingSetDAO;
    }

	public static FinancePremiumDetailDAO getFinancePremiumDetailDAO() {
    	return financePremiumDetailDAO;
    }
	public void setFinancePremiumDetailDAO(FinancePremiumDetailDAO financePremiumDetailDAO) {
    	AccountEngineExecution.financePremiumDetailDAO = financePremiumDetailDAO;
    }

	public List<ReturnDataSet> getReturnDataSetList() {
	    return returnDataSetList;
    }
	public void setReturnDataSetList(List<ReturnDataSet> returnDataSetList) {
	    this.returnDataSetList = returnDataSetList;
    }

	public List<FeeRule> getFeeRuleList() {
	    return feeRuleList;
    }
	public void setFeeRuleList(List<FeeRule> feeRuleList) {
	    this.feeRuleList = feeRuleList;
    }
	
	public BigDecimal getProvisionAmt() {
    	return provisionAmt;
    }
	public void setProvisionAmt(BigDecimal provisionAmt) {
    	this.provisionAmt = provisionAmt;
    }


}
