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
import org.zkoss.util.resource.Labels;
import org.zkoss.zul.Messagebox;

import com.pennant.Interface.model.IAccounts;
import com.pennant.Interface.service.AccountInterfaceService;
import com.pennant.app.constants.AccountConstants;
import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.backend.dao.applicationmaster.CurrencyDAO;
import com.pennant.backend.dao.collateral.CollateralSetupDAO;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinancePremiumDetailDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.FinanceSuspHeadDAO;
import com.pennant.backend.dao.lmtmasters.FinanceReferenceDetailDAO;
import com.pennant.backend.dao.masters.SystemInternalAccountDefinitionDAO;
import com.pennant.backend.dao.rmtmasters.AccountingSetDAO;
import com.pennant.backend.dao.rmtmasters.FinTypeAccountingDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.dao.rmtmasters.TransactionEntryDAO;
import com.pennant.backend.dao.rulefactory.RuleDAO;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.commitment.Commitment;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinancePremiumDetail;
import com.pennant.backend.model.masters.SystemInternalAccountDefinition;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rmtmasters.TransactionEntry;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AECommitment;
import com.pennant.backend.model.rulefactory.DataSet;
import com.pennant.backend.model.rulefactory.DataSetFiller;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.RuleReturnType;
import com.pennant.backend.util.VASConsatnts;
import com.pennant.eod.util.EODProperties;
import com.pennant.exception.PFFInterfaceException;
import com.rits.cloning.Cloner;

public class AccountEngineExecution implements Serializable {
    private static final long serialVersionUID = 852062955563015315L;
	private Logger logger = Logger.getLogger(AccountEngineExecution.class);

	private FinanceTypeDAO financeTypeDAO;	
	private FinanceMainDAO financeMainDAO;	
	private TransactionEntryDAO transactionEntryDAO;
	private SystemInternalAccountDefinitionDAO internalAccountDefinitionDAO;
	private RuleDAO ruleDAO;
	private CustomerDAO customerDAO;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private CurrencyDAO currencyDAO;
	private FinanceSuspHeadDAO financeSuspHeadDAO;
	private AccountInterfaceService accountInterfaceService;
	private RuleExecutionUtil ruleExecutionUtil;
	private AccountingSetDAO accountingSetDAO;
	private FinancePremiumDetailDAO financePremiumDetailDAO;
	private FinanceReferenceDetailDAO financeReferenceDetailDAO;
	private FinTypeAccountingDAO finTypeAccountingDAO;
	private CollateralSetupDAO		collateralSetupDAO;

	//Default Constructor
	public AccountEngineExecution() {
		super();
	}
	
	/**
	 * Method for Execution of Accounting Sets depend on Event 
	 * @param dataSet
	 * @param aeAmountCodes
	 * @param type
	 * @return
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws PFFInterfaceException 
	 */
	public List<ReturnDataSet> getAccEngineExecResults(DataSet dataSet,AEAmountCodes aeAmountCodes
			,String createNow, Map<String, FeeRule> feeRuleDetailMap, boolean isAccrualCal, FinanceType finType)
					throws PFFInterfaceException, IllegalAccessException, InvocationTargetException {
		
		return processAccountExecution(dataSet, aeAmountCodes, createNow, feeRuleDetailMap, isAccrualCal, finType, null);
	}
	
	/**
	 * Method for Execution of Accounting Sets depend on Event 
	 * @param dataSet
	 * @param aeAmountCodes
	 * @param type
	 * @return
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws PFFInterfaceException 
	 */
	public List<ReturnDataSet> getAccEngineExecResults(DataSet dataSet,AEAmountCodes aeAmountCodes
			,String createNow, Map<String, FeeRule> feeRuleDetailMap, boolean isAccrualCal, FinanceType finType, FinancePremiumDetail financePremiumDetail)
					throws PFFInterfaceException, IllegalAccessException, InvocationTargetException {
		
		return processAccountExecution(dataSet, aeAmountCodes, createNow, feeRuleDetailMap, isAccrualCal, finType, financePremiumDetail);
	}
	
	/**
	 * Method for Execution Stage entries for RIA Investments
	 * @param dataSet
	 * @param aeAmountCodes
	 * @param createNow
	 * @param feeRuleDetailMap
	 * @return
	 * @throws PFFInterfaceException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public List<ReturnDataSet> getStageExecResults(DataSet dataSet,AEAmountCodes aeAmountCodes
			,String createNow, String roleCode, Map<String, FeeRule> feeRuleDetailMap, FinanceType finType, FinancePremiumDetail premiumDetail) 
					throws PFFInterfaceException, IllegalAccessException, InvocationTargetException{
		
		return processStageAccountExecution(dataSet, aeAmountCodes, createNow, roleCode, feeRuleDetailMap, finType, premiumDetail);
	}
	
	/**
	 * Method for Execution Of Fee & Charges Rules
	 * @param dataSet
	 * @param amountCode
	 * @return
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws PFFInterfaceException 
	 */
	public List<FeeRule> getFeeChargesExecResults(DataSet dataSet,
			AEAmountCodes aeAmountCodes, int formatter, boolean isWIF, FinanceType finType) throws IllegalAccessException, InvocationTargetException, PFFInterfaceException{
		
		return processFeeChargesExecResults(dataSet,aeAmountCodes, isWIF, finType);
	}
	
	/**
	 * Method for Execution Of Fee & Charges Rules
	 * @param dataSet
	 * @param amountCode
	 * @return
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws PFFInterfaceException 
	 */
	public List<FeeRule> getReExecFeeResults(DataSet dataSet,
			AEAmountCodes aeAmountCodes, int formatter, boolean isWIF, FinanceType finType, List<FeeRule> existFeeList) 
					throws IllegalAccessException, InvocationTargetException, PFFInterfaceException{
		return processReExecFeeResults(dataSet,aeAmountCodes, formatter, isWIF, finType, existFeeList);
	}
	
	/**
	 * Method For execution of Provision Rule For Provision Calculated Amount
	 * @param dataSet
	 * @param aeAmountCodes
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws PFFInterfaceException 
	 */
	public BigDecimal getProvisionExecResults(DataSet dataSet, AEAmountCodes aeAmountCodes) throws IllegalAccessException, InvocationTargetException, PFFInterfaceException{
		return processProvExecResults(dataSet,aeAmountCodes);
	}
	
	/**
	 * Method for Commitment posting Entries Execution
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws PFFInterfaceException 
	 */
	public List<ReturnDataSet> getCommitmentExecResults(AECommitment aeCommitment, Commitment commitment, String acSetEvent, 
			String createNow, Map<String, FeeRule> feeRuleDetailMap) throws PFFInterfaceException, IllegalAccessException, InvocationTargetException{
	
		return processCommitmentExecResults(aeCommitment, commitment, acSetEvent, createNow, feeRuleDetailMap);
	}
	/**
	 * Method for VasRecording posting Entries Execution
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws PFFInterfaceException 
	 */
	public List<ReturnDataSet> getVasExecResults(VASRecording vASRecording, String acSetEvent, 
			String createNow, Map<String, FeeRule> feeRuleDetailMap) throws PFFInterfaceException, IllegalAccessException, InvocationTargetException{
		
		return processVASRecordingExecResults(vASRecording, acSetEvent, createNow, feeRuleDetailMap);
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
	 * @throws PFFInterfaceException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private List<ReturnDataSet> processAccountExecution(DataSet dataSet,AEAmountCodes aeAmountCodes
			,String createNow, Map<String, FeeRule> feeRuleDetailMap, boolean isAccrualCal, FinanceType finType, FinancePremiumDetail premiumDetail)
			throws PFFInterfaceException, IllegalAccessException, InvocationTargetException{
		logger.debug("Entering");
		

		// Fill Amount Code Detail Object with Respect to Schedule Details
		DataSetFiller	dataSetFiller=prepareAmountCodes(dataSet, aeAmountCodes, false, isAccrualCal, finType, premiumDetail);

		// Execute entries depend on Finance Event
		long accountingSetId = getFinTypeAccountingDAO().getAccountSetID(finType.getFinType(), dataSet.getFinEvent(), FinanceConstants.FINTYPEFEES_FINTYPE);

		List<TransactionEntry> transactionEntries = null;
		String phase = SysParamUtil.getValueAsString(PennantConstants.APP_PHASE);
		if (phase.equals(PennantConstants.APP_PHASE_DAY)) {
			transactionEntries = getTransactionEntryDAO().getListTransactionEntryById(accountingSetId, "_AEView", true);
		} else {
			transactionEntries = EODProperties.getTransactionEntryList(accountingSetId);
		}

		List<ReturnDataSet> returnList = getPrepareAccountingSetResults(dataSet, transactionEntries,createNow,false, feeRuleDetailMap, finType,dataSetFiller,null,null);
		
		logger.debug("Leaving");
		return returnList;
	}
	
	
	/**
	 * Method for Execution Stage entries for RIA Investments
	 * @param dataSet
	 * @param aeAmountCodes
	 * @param createNow
	 * @param feeRuleDetailMap
	 * @return
	 * @throws PFFInterfaceException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private List<ReturnDataSet> processStageAccountExecution(DataSet dataSet,AEAmountCodes aeAmountCodes
			,String createNow, String roleCode, Map<String, FeeRule> feeRuleDetailMap, FinanceType finType, FinancePremiumDetail premiumDetail) throws PFFInterfaceException, IllegalAccessException, InvocationTargetException{
		logger.debug("Entering");

		//Fill Amount Code Detail Object with FinanceMain Object
		DataSetFiller dataSetFiller = doFillDataSetFiller(dataSet ,dataSet.isNewRecord(),  false,finType, premiumDetail);
		setAmountCodes(dataSetFiller,aeAmountCodes,dataSet.isNewRecord(), false, false);//set data to DataSetFiller Object		

		List<TransactionEntry> transactionEntries = getTransactionEntryDAO().getListTransactionEntryByRefType(
				dataSet.getFinType(),dataSet.getModuledefiner(), FinanceConstants.PROCEDT_STAGEACC, roleCode,  "_AEView",true);
		
		List<ReturnDataSet> returnList = getPrepareAccountingSetResults(dataSet,
				transactionEntries,createNow,false, feeRuleDetailMap, finType,dataSetFiller,null,null);
		logger.debug("Leaving");
		return returnList;
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
			AEAmountCodes aeAmountCodes,boolean isWIF, FinanceType finType) throws IllegalAccessException, InvocationTargetException{
		logger.debug("Entering");
		
		//Prepare AmountCode Details
		
		DataSetFiller dataSetFiller =	prepareAmountCodes(dataSet, aeAmountCodes, isWIF, false,finType, null);
		
		//Execute entries depend on Finance Event
		long accountingSetId = getFinTypeAccountingDAO().getAccountSetID(finType.getFinType(), dataSet.getFinEvent(), FinanceConstants.FINTYPEFEES_FINTYPE);

		List<FeeRule> feeRules = new ArrayList<FeeRule>();
		String ruleEvent= dataSet.getFinEvent();
		if(ruleEvent.startsWith(AccountEventConstants.ACCEVENT_ADDDBS) || 
				ruleEvent.startsWith(AccountEventConstants.ACCEVENT_DEFAULT)){
			ruleEvent = AccountEventConstants.ACCEVENT_ADDDBS;
		}
		
		if(dataSetFiller.getDISBURSE() == null){
			dataSetFiller.setDISBURSE(BigDecimal.ZERO);
		}
		
		//Fetch Stage Accounting AccountingSetId List 
		List<Long> accSetIdList = new ArrayList<Long>();
		accSetIdList.addAll(getFinanceReferenceDetailDAO().getRefIdListByFinType(finType.getFinType(), 
				("").equals(dataSet.getModuledefiner())? FinanceConstants.FINSER_EVENT_ORG : dataSet.getModuledefiner(), null , "_ACView"));
		if (!FinanceConstants.FINSER_EVENT_PREAPPROVAL.equals(dataSet.getModuledefiner())) {
			accSetIdList.add(accountingSetId);
        }
		
		if(!accSetIdList.isEmpty()){
			
			List<Rule> ruleList = getTransactionEntryDAO().getListFeeChargeRules(accSetIdList, ruleEvent, "_AView", 0);
			
			FeeRule feeRule ;
			BigDecimal totalSchdFeeAmt = BigDecimal.ZERO;
			for (Rule rule : ruleList) {
				feeRule = new FeeRule();
				
				//Set Object Data of ReturnDataSet(s)
				feeRule.setFeeCode(rule.getRuleCode());
				feeRule.setFeeCodeDesc(rule.getRuleCodeDesc());
				feeRule.setFeeToFinance(rule.getFeeToFinance());
				feeRule.setFeeOrder(rule.getSeqOrder());
				feeRule.setAllowWaiver(rule.isWaiver());
				feeRule.setWaiverPerc(rule.getWaiverPerc());
				feeRule.setCalFeeModify(rule.isCalFeeModify());
				
				BigDecimal amount = BigDecimal.ZERO;
				HashMap<String, Object> fieldsAndValues = dataSetFiller.getDeclaredFieldValues();
				amount = (BigDecimal) getRuleExecutionUtil().executeRule(rule.getSQLRule(), fieldsAndValues,
						dataSet.getFinCcy(), RuleReturnType.DECIMAL);

				totalSchdFeeAmt = totalSchdFeeAmt.add(amount);
				dataSetFiller.setFEETOSCHD(totalSchdFeeAmt);
				feeRule.setFeeAmount(amount);
				feeRules.add(feeRule);
			}
		}
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
		DataSetFiller dataSetFiller=prepareAmountCodes(dataSet, aeAmountCodes, isWIF, false,finType, null);
		
		//Execute entries depend on Finance Event
		long accountingSetId = getFinTypeAccountingDAO().getAccountSetID(finType.getFinType(), dataSet.getFinEvent(), FinanceConstants.FINTYPEFEES_FINTYPE);

		//Adding Existing Fees
		int feeOrder = 0;
		if(existFeeList != null && !existFeeList.isEmpty()){
			for (FeeRule feeRule : existFeeList) {
				dataSetFiller.setDISBURSE(dataSetFiller.getDISBURSE().add(feeRule.getFeeAmount().subtract(
						feeRule.getWaiverAmount()).subtract(feeRule.getPaidAmount())));
				feeOrder = feeRule.getFeeOrder();
			}
		}
		
		List<FeeRule> feeRules = existFeeList;
		String ruleEvent= dataSet.getFinEvent();
		if(ruleEvent.startsWith(AccountEventConstants.ACCEVENT_ADDDBS)){
			ruleEvent = AccountEventConstants.ACCEVENT_ADDDBS;
		}
		
		//Fetch Stage Accounting AccountingSetId List 
		List<Long> accSetIdList = new ArrayList<Long>();
		if(dataSet.isNewRecord() && ruleEvent.startsWith(AccountEventConstants.ACCEVENT_ADDDBS)){
			accSetIdList.addAll(getFinanceReferenceDetailDAO().getRefIdListByFinType(finType.getFinType(),
					FinanceConstants.FINSER_EVENT_ORG,null , "_ACView"));
		}
		accSetIdList.add(accountingSetId);
		
		if(!accSetIdList.isEmpty()){
			List<Rule> ruleList = getTransactionEntryDAO().getListFeeChargeRules(accSetIdList,ruleEvent, "_AView",feeOrder);
			FeeRule feeRule ;
			for (Rule rule : ruleList) {
				feeRule = new FeeRule();
				
				//Set Object Data of ReturnDataSet(s)
				feeRule.setFeeCode(rule.getRuleCode());
				feeRule.setFeeCodeDesc(rule.getRuleCodeDesc());
				feeRule.setCalFeeModify(rule.isCalFeeModify());
				feeRule.setFeeToFinance(rule.getFeeToFinance());
				feeRule.setFeeOrder(rule.getSeqOrder());
				feeRule.setAllowWaiver(rule.isWaiver());
				feeRule.setWaiverPerc(rule.getWaiverPerc());
				
				BigDecimal amount = BigDecimal.ZERO;
				HashMap<String, Object> fieldsAndValues = dataSetFiller.getDeclaredFieldValues();

				amount = (BigDecimal) getRuleExecutionUtil().executeRule(rule.getSQLRule(), fieldsAndValues, dataSet.getFinCcy(), RuleReturnType.DECIMAL);

				dataSetFiller.setDISBURSE(dataSetFiller.getDISBURSE().add(amount));
				feeRule.setFeeAmount(amount);
				feeRules.add(feeRule);
			}
		}

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
		
		String phase = SysParamUtil.getValueAsString(PennantConstants.APP_PHASE);
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
		
		DataSetFiller dataSetFiller = doFillDataSetFiller(dataSet ,dataSet.isNewRecord(), false,financeType, null);
		setAmountCodes(dataSetFiller,aeAmountCodes,dataSet.isNewRecord(), false, false);//set data to DataSetFiller Object	
		BigDecimal provCalAmount = BigDecimal.ZERO;

		String rule = getRuleDAO().getAmountRule("PROV", RuleConstants.MODULE_PROVSN, RuleConstants.EVENT_PROVSN);
		if(rule != null){
			
			Object result = getRuleExecutionUtil().executeRule(rule, dataSetFiller.getDeclaredFieldValues(),
					dataSet.getFinCcy(), RuleReturnType.DECIMAL);
			provCalAmount = new BigDecimal(result == null ? "0" : result.toString());
		}
		
		return provCalAmount;
	}
	/**
	 * Method for VASRecording posting Entries Execution
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws PFFInterfaceException 
	 */
	private List<ReturnDataSet> processVASRecordingExecResults(VASRecording vASRecording, String acSetEvent, 
			String createNow, Map<String, FeeRule> feeRuleDetailMap) throws PFFInterfaceException, IllegalAccessException, InvocationTargetException{
		
		logger.debug("Entering");
		
		//DataSet Object preparation
		DataSet dataSet = new DataSet();
		dataSet.setFinReference(vASRecording.getVasReference());
		dataSet.setFinEvent(acSetEvent);
		dataSet.setPostDate(DateUtility.getSysDate());
		dataSet.setValueDate(DateUtility.getSysDate());
		dataSet.setFeeAmount(vASRecording.getFee());

		//Setting Branch based on the VAS selection
		
		if(StringUtils.equals(VASConsatnts.VASAGAINST_CUSTOMER,vASRecording.getPostingAgainst())){
			
			Customer customer = getCustomerDAO().getCustomerByCIF(vASRecording.getPrimaryLinkRef(),"");
			
			dataSet.setCustId(customer.getCustID());
			dataSet.setFinBranch(customer.getCustDftBranch());
			dataSet.setFinCcy(customer.getCustBaseCcy());
			dataSet.setCustCIF(customer.getCustCIF());
			
		}else if(StringUtils.equals(VASConsatnts.VASAGAINST_FINANCE,vASRecording.getPostingAgainst())){
			
			FinanceMain finMain = getFinanceMainDAO().getFinanceMainForBatch(vASRecording.getPrimaryLinkRef());
			
			dataSet.setFinBranch(finMain.getFinBranch());
			dataSet.setFinCcy(finMain.getFinCcy());
			dataSet.setCustId(finMain.getCustID());
			dataSet.setCustCIF(finMain.getLovDescCustCIF());
			
		}else if(StringUtils.equals(VASConsatnts.VASAGAINST_COLLATERAL,vASRecording.getPostingAgainst())){
			
			CollateralSetup collateralSetup = getCollateralSetupDAO().getCollateralSetupByRef(vASRecording.getPrimaryLinkRef(),"");
			Customer customer = getCustomerDAO().getCustomerByID(collateralSetup.getDepositorId(),"");
			
			dataSet.setCustId(customer.getCustID());
			dataSet.setFinBranch(customer.getCustDftBranch());
			dataSet.setCustCIF(customer.getCustCIF());
			dataSet.setFinCcy(collateralSetup.getCollateralCcy());
			
		}

		//Accounting Set Details
		List<TransactionEntry> transactionEntries = null;
		long accountingSetId = vASRecording.getVasConfiguration().getFeeAccounting();
		if (accountingSetId != 0) {
			//get List of transaction entries
			transactionEntries = getTransactionEntryDAO().getListTransactionEntryById(accountingSetId, "_AEView",true);
		}
		
		List<ReturnDataSet> returnDataSets = null;
		if(transactionEntries != null && transactionEntries.size() > 0){
			returnDataSets =  getPrepareAccountingSetResults(dataSet, transactionEntries, createNow,true, feeRuleDetailMap, null,null,null,vASRecording);
		}
		
		//Method for Checking for Reverse Calculations Based upon Negative Amounts
		for (ReturnDataSet set : returnDataSets) {

			if (set.getPostAmount().compareTo(BigDecimal.ZERO) < 0) {

				String tranCode = set.getTranCode();
				String revTranCode = set.getRevTranCode();
				String debitOrCredit = set.getDrOrCr();

				set.setTranCode(revTranCode);
				set.setRevTranCode(tranCode);

				set.setPostAmount(set.getPostAmount().negate());

				if (debitOrCredit.equals(AccountConstants.TRANTYPE_CREDIT)) {
					set.setDrOrCr(AccountConstants.TRANTYPE_DEBIT);
				} else {
					set.setDrOrCr(AccountConstants.TRANTYPE_CREDIT);
				}
			}
		}
		
		logger.debug("Leaving");
		return returnDataSets;
	}
	
	/**
	 * Method for Commitment posting Entries Execution
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws PFFInterfaceException 
	 */
	private List<ReturnDataSet> processCommitmentExecResults(AECommitment aeCommitment, Commitment commitment, String acSetEvent, 
			String createNow, Map<String, FeeRule> feeRuleDetailMap) throws PFFInterfaceException, IllegalAccessException, InvocationTargetException{
		
		logger.debug("Entering");
		
		//DataSet Object preparation
		DataSet dataSet = new DataSet();
		dataSet.setFinReference(commitment.getCmtReference());
		dataSet.setFinEvent(acSetEvent);
		dataSet.setPostDate(DateUtility.getSysDate());
		dataSet.setValueDate(DateUtility.getSysDate());
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
			returnDataSets =  getPrepareAccountingSetResults(dataSet, transactionEntries, createNow,true, feeRuleDetailMap, null,null,aeCommitment,null);
		}
		
		
		logger.debug("Leaving");
		return returnDataSets;
	}	
	
	public List<ReturnDataSet> processAccountingByEvent(DataSet dataSet,DataSetFiller dataSetFiller,String acSetEvent, String createNow) throws IllegalAccessException, InvocationTargetException, PFFInterfaceException {
		logger.debug("Entering");
	
		//DataSet Object preparation
		dataSet.setFinEvent(acSetEvent);
		
		//Accounting Set Details
		List<TransactionEntry> transactionEntries = null;
		//Accounting set code will be hard coded
		long accountingSetId = getAccountingSetDAO().getAccountingSetId(acSetEvent, acSetEvent);
		if (accountingSetId != 0) {
			//get List of transaction entries
			transactionEntries = getTransactionEntryDAO().getListTransactionEntryById(accountingSetId, "_AEView",true);
		}
		
		List<ReturnDataSet> returnDataSets = null;
		if(transactionEntries != null && transactionEntries.size() > 0){
			returnDataSets =  getPrepareAccountingSetResults(dataSet, transactionEntries, createNow,true, null, null,dataSetFiller,null,null);
		}
		
		
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
	private DataSetFiller prepareAmountCodes(DataSet dataSet,AEAmountCodes aeAmountCodes, boolean isWIF, boolean isAccrualCal, FinanceType financeType, FinancePremiumDetail premiumDetail) 
			throws IllegalAccessException, InvocationTargetException{
		logger.debug("Entering");

		//Fill Amount Code Detail Object with FinanceMain Object
		DataSetFiller dataSetFiller = doFillDataSetFiller(dataSet ,dataSet.isNewRecord(), isAccrualCal,financeType, premiumDetail);
		
		setAmountCodes(dataSetFiller,aeAmountCodes,dataSet.isNewRecord(), isWIF, isAccrualCal);//set data to DataSetFiller Object	
		logger.debug("Leaving");
		return dataSetFiller;
	}
	
	/**
	 * Method for preparing List of ReturnDataSet objects by executing rules
	 * @param dataSet
	 * @param accountingSetId
	 * @return
	 * @throws PFFInterfaceException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	private List<ReturnDataSet> getPrepareAccountingSetResults(DataSet dataSet, List<TransactionEntry> transactionEntries,
			String createNow,boolean isCommitment, Map<String, FeeRule> feeRuleDetailMap, FinanceType financeType,DataSetFiller dataSetFiller,AECommitment aeCommitment,VASRecording vASRecording) 
					throws PFFInterfaceException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");
		logger.trace("FIN REFERENCE: " + dataSet.getFinReference());
		
		Customer customer = getCustomerDAO().getCustomerForPostings(dataSet.getCustId());//TODO : Only using for Customer CIF, better remove DB hitting
		if (customer == null) {
			Messagebox.show(Labels.getLabel("Cust_NotFound"),Labels.getLabel("message.Error"),Messagebox.OK,"z-msgbox z-msgbox-error");
			return null;
		}else{
			dataSet.setCustCIF(customer.getCustCIF());
		}

		List<ReturnDataSet> returnDataSets = new ArrayList<ReturnDataSet>();
		
		Map<String, Object> accountsMap = new HashMap<String, Object>(transactionEntries.size());
		List<IAccounts> accountsList = new ArrayList<IAccounts>(transactionEntries.size());
		logger.trace("Refactoring of Account Engine: accountsList Size := " + accountsList.size());
		Map<String, String> accountCcyMap = new HashMap<String, String>(transactionEntries.size());
		
		// Prepare list of account types from tranaactionEntries object
		List<String> accountTypeList = new ArrayList<String>();
		List<String> subHeadRuleList = new ArrayList<String>();
		
		for (TransactionEntry transactionEntry : transactionEntries) {
			if(transactionEntry.getAccountType() != null && !accountTypeList.contains(transactionEntry.getAccountType())) {
				accountTypeList.add(transactionEntry.getAccountType());
			}
			
			if(transactionEntry.getAccountSubHeadRule() != null && 
					!accountTypeList.contains(transactionEntry.getAccountSubHeadRule())) {
				subHeadRuleList.add(transactionEntry.getAccountSubHeadRule());
			}
		}
		
		/**
		 * Fetch System Internal Account list and SubHeadRule Result list based on the phase<br>
		 * Phase-->EOD fetch the records from EOD Properties Map object<br>
		 * Otherwise Fetch the records from Database
		 * 
		 */
		List<SystemInternalAccountDefinition> sysIntAcNumList = null;
		List<Rule> subHeadSqlList = null;
		
		String phase = SysParamUtil.getValueAsString(PennantConstants.APP_PHASE);
		if (phase.equals(PennantConstants.APP_PHASE_EOD)) {
			sysIntAcNumList = getEODSysIntAccounts(accountTypeList);
			subHeadSqlList = getEODSubheadRules(subHeadRuleList);
		} else {
			sysIntAcNumList = getInternalAccountDefinitionDAO().getSysIntAccNumList(accountTypeList);
			subHeadSqlList = getRuleDAO().getSubHeadRuleList(subHeadRuleList, RuleConstants.MODULE_SUBHEAD, 
					RuleConstants.EVENT_SUBHEAD );
		}
		
		Map<String, String> sysIntAccMap = new HashMap<String, String>(accountTypeList.size());
		
		for(SystemInternalAccountDefinition siaDef:sysIntAcNumList) {
			sysIntAccMap.put(siaDef.getSIACode(), siaDef.getSIANumber());
		}
		
		Map<String, String> ruleResultMap = null;
		if(ImplementationConstants.CLIENTTYPE.equals(ImplementationConstants.BANK)){
			ruleResultMap =  doProcessSubHeadRules(subHeadSqlList, aeCommitment,vASRecording, dataSetFiller, 
					dataSet.getFinCcy());
		}

		//Set Account number generation
		for (TransactionEntry transactionEntry : transactionEntries) {
			
			if("N".equals(createNow)){
				IAccounts account = getAccountNumber(dataSet,transactionEntry,isCommitment,false, financeType, accountsMap, 
						accountCcyMap, sysIntAccMap, ruleResultMap, dataSetFiller);
				if(account != null){
					accountsList.add(account);
				}
				
				account = null;
				
			}else{
				IAccounts account = getAccountNumber(dataSet,transactionEntry,isCommitment,true, financeType, accountsMap, 
						accountCcyMap, sysIntAccMap, ruleResultMap, dataSetFiller);
				if(account != null){
					logger.trace("Refactoring of Account Engine: accountsList Size := " + accountsList.size());
					accountsList.add(account);
				}
				
				account = null;
			}
		}

		// nullify the objects for quick garbage collection
		sysIntAcNumList = null;
		subHeadSqlList = null;
		sysIntAccMap = null;
		ruleResultMap = null;
			
		//Calling Core Banking Interface Service
		
//TODO Removed for AHB. New constant should be added in Implementation constants to check in core or not 		
/*		if("N".equals(createNow)){
			this.accountsList = getAccountInterfaceService().fetchExistAccount(this.accountsList,createNow);
		}else{*/
			if(accountCcyMap.size() > 0){
				accountCcyMap = getAccountInterfaceService().getAccountCurrencyMap(accountCcyMap);
			}
//		}
		
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
			returnDataSet.setFinBranch(dataSet.getFinBranch());
			returnDataSet.setFinEvent(dataSet.getFinEvent());
			returnDataSet.setLovDescEventCodeName(transactionEntry.getLovDescEventCodeDesc());
			returnDataSet.setAccSetCodeName(transactionEntry.getLovDescAccSetCodeName());
			returnDataSet.setAccSetId(transactionEntry.getAccountSetid());
			returnDataSet.setCustId(dataSet.getCustId());
			returnDataSet.setTranDesc(transactionEntry.getTransDesc());
			returnDataSet.setPostDate(dataSet.getPostDate());
			returnDataSet.setValueDate(dataSet.getPostDate());
			returnDataSet.setShadowPosting(transactionEntry.isShadowPosting());
			returnDataSet.setPostToSys(transactionEntry.getPostToSys());
			returnDataSet.setDerivedTranOrder(transactionEntry.getDerivedTranOrder());
			returnDataSet.setTransOrder(transactionEntry.getTransOrder());
			String ref = dataSet.getFinReference() + "/" + dataSet.getFinEvent() + "/" + transactionEntry.getTransOrder();
			returnDataSet.setPostingId(ref);
			returnDataSet.setFinPurpose(dataSet.getFinPurpose());
			if("N".equals(createNow)){
				returnDataSet.setAccountType(transactionEntry.getAccount());
			}

			//Post Reference
			String branch = StringUtils.isBlank(transactionEntry.getAccountBranch())? dataSet.getFinBranch():transactionEntry.getAccountBranch();
					
			String accType = "";
			if(financeType != null){
				accType = StringUtils.isBlank(transactionEntry.getAccountType())? financeType.getFinAcType():transactionEntry.getAccountType();
			}else{
				accType = StringUtils.isBlank(transactionEntry.getAccountType())? "":transactionEntry.getAccountType();
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
			BigDecimal postAmt = executeAmountRule(dataSet.getFinEvent(), transactionEntry, feeRuleDetailMap, dataSet.getFinCcy(),dataSetFiller,aeCommitment,vASRecording);
			if(postAmt.compareTo(BigDecimal.ZERO) >= 0 ){
				returnDataSet.setPostAmount(postAmt);
				returnDataSet.setTranCode(transactionEntry.getTranscationCode());
				returnDataSet.setRevTranCode(transactionEntry.getRvsTransactionCode());
				returnDataSet.setDrOrCr(transactionEntry.getDebitcredit());
			}else{
				returnDataSet.setPostAmount(postAmt.abs());
				returnDataSet.setRevTranCode(transactionEntry.getTranscationCode());
				returnDataSet.setTranCode(transactionEntry.getRvsTransactionCode());
				if(transactionEntry.getDebitcredit().equals(AccountConstants.TRANTYPE_CREDIT)){
					returnDataSet.setDrOrCr(AccountConstants.TRANTYPE_DEBIT);
				}else{
					returnDataSet.setDrOrCr(AccountConstants.TRANTYPE_CREDIT);
				}
			}
			//post amount in Local currency
			returnDataSet.setPostAmountLcCcy(CalculationUtil.getConvertedAmount(dataSet.getFinCcy(), SysParamUtil.getAppCurrency(), postAmt));
			returnDataSet.setExchangeRate(CurrencyUtil.getExChangeRate(dataSet.getFinCcy()));
			
			//Converting Post Amount Based on Account Currency
			returnDataSet.setAcCcy(dataSet.getFinCcy());
			returnDataSet.setFormatter(CurrencyUtil.getFormat(dataSet.getFinCcy()));
			
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
	
	/**
	 * Method for fetch the list of sub head rules and execute the rules to get Result object
	 * 
	 * @param subHeadSqlList
	 * @param aeCommitment
	 * @param dataSetFiller
	 * @param finCcy
	 * @param isEODProcess
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private Map<String, String> doProcessSubHeadRules(List<Rule> subHeadSqlList, AECommitment aeCommitment,VASRecording vASRecording,
			DataSetFiller dataSetFiller, String finCcy) throws IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		Map<String, String> ruleResultMap = new HashMap<String, String>();
		HashMap<String, Object> fieldValues = null;

		if (aeCommitment != null) {
			fieldValues = aeCommitment.getDeclaredFieldValues();
		} else if(vASRecording!= null){
			fieldValues = vASRecording.getDeclaredFieldValues();
		}else{
			fieldValues = dataSetFiller.getDeclaredFieldValues();
		}

		if (subHeadSqlList != null) {
			for (Rule rule : subHeadSqlList) {

				String result = (String) getRuleExecutionUtil().executeRule(rule.getSQLRule(), fieldValues, finCcy,
						RuleReturnType.STRING);

				ruleResultMap.put(rule.getRuleCode(), result);
			}
		}

		logger.debug("Leaving");

		return ruleResultMap;
	}

	/**
	 * Get list of System Internal account numbers from EOD Properties
	 * 
	 * @param accountTypeList
	 * @return
	 */
	private List<SystemInternalAccountDefinition> getEODSysIntAccounts(List<String> accountTypeList) {
		logger.debug("Entering");
		
		List<SystemInternalAccountDefinition> sysIntAccList = new ArrayList<SystemInternalAccountDefinition>();
		
		for(String siaCode: accountTypeList) {
			SystemInternalAccountDefinition sysIntAcc = new SystemInternalAccountDefinition();
			sysIntAcc.setSIACode(siaCode);
			sysIntAcc.setSIANumber(EODProperties.getSIANumber(siaCode));
			
			sysIntAccList.add(sysIntAcc);
		}
		
		logger.debug("Leaving");
		return sysIntAccList;
	}

	/**
	 * Get list of SubHead rules from EODProperties
	 * 
	 * @param subHeadRuleList
	 * @return
	 */
	private List<Rule> getEODSubheadRules(List<String> subHeadRuleList) {
		logger.debug("Entering");
		
		List<Rule> amountRuleList = new ArrayList<Rule>();
		
		for(String ruleCode: subHeadRuleList) {
			Rule rule = new Rule();
			rule.setRuleCode(ruleCode);
			rule.setSQLRule(EODProperties.getSubHeadRule(ruleCode));
			
			amountRuleList.add(rule);
		}
		
		logger.debug("Leaving");
		return amountRuleList;
	}

	private List<ReturnDataSet> createAccOnCCyConversion(ReturnDataSet existDataSet, String acCcy, BigDecimal unconvertedPostAmt, DataSet dataSet){
		
		List<ReturnDataSet> newEntries = new ArrayList<ReturnDataSet>(2);
		String actTranType = existDataSet.getDrOrCr();
		String phase = SysParamUtil.getValueAsString(PennantConstants.APP_PHASE);
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
		
		String drCr = actTranType.equals(AccountConstants.TRANTYPE_DEBIT) ? AccountConstants.TRANTYPE_CREDIT : AccountConstants.TRANTYPE_DEBIT;
		String crDr = actTranType.equals(AccountConstants.TRANTYPE_DEBIT) ? AccountConstants.TRANTYPE_DEBIT : AccountConstants.TRANTYPE_CREDIT;
		
		Cloner cloner = new Cloner();
		ReturnDataSet newDataSet1 = cloner.deepClone(existDataSet);
		newDataSet1.setDrOrCr(drCr);
		newDataSet1.setAcCcy(acCcy);
		newDataSet1.setTransOrder(existDataSet.getTransOrder() + 1);
		newDataSet1.setTranCode(SysParamUtil.getValueAsString("CCYCNV_" + drCr + "RTRANCODE"));
		newDataSet1.setRevTranCode(SysParamUtil.getValueAsString("CCYCNV_" + crDr + "RTRANCODE"));
		newDataSet1.setAccount(existDataSet.getAccount().substring(0, 4)+"881"+acCcyNum+finCcyNum);
		newDataSet1.setFormatter(formatter);
		newEntries.add(newDataSet1);
		
		cloner = new Cloner();
		ReturnDataSet newDataSet2 = cloner.deepClone(existDataSet);
		newDataSet2.setDrOrCr(crDr);
		newDataSet2.setTransOrder(existDataSet.getTransOrder() + 2);
		newDataSet2.setTranCode(SysParamUtil.getValueAsString("CCYCNV_" + crDr + "RTRANCODE"));
		newDataSet2.setRevTranCode(SysParamUtil.getValueAsString("CCYCNV_" + drCr + "RTRANCODE"));
		newDataSet2.setAccount(dataSet.getFinBranch()+"881"+finCcyNum+acCcyNum);
		newDataSet2.setPostAmount(unconvertedPostAmt);
		newDataSet2.setPostAmountLcCcy(CalculationUtil.getConvertedAmount(dataSet.getFinCcy(), SysParamUtil.getAppCurrency(), unconvertedPostAmt));
		newEntries.add(newDataSet2);
		
		existDataSet.setFormatter(formatter);
		
		return newEntries;
	}
	
	

	/**
	 * Fill Data For DataFiller Object depend on Event Condition
	 * @param dataSet
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	private DataSetFiller doFillDataSetFiller(DataSet dataSet,boolean isNewFinance, boolean isAccrualCal,FinanceType financeType, FinancePremiumDetail premiumDetail) throws IllegalAccessException, InvocationTargetException{

		DataSetFiller dataSetFiller=new DataSetFiller();
		logger.debug("Entering");
		Customer customer = getCustomerDAO().getCustomerForPostings(dataSet.getCustId());
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

		dataSetFiller.setReqCampaign("");
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
		dataSetFiller.setDEDUCTFEEDISB(dataSet.getDeductFeeDisb());
		dataSetFiller.setDEDUCTINSDISB(dataSet.getDeductInsDisb());
		dataSetFiller.setDISBURSE(dataSet.getDisburseAmount());
		dataSetFiller.setRETAMT(dataSet.getCurDisbRet());
		dataSetFiller.setNETRET(dataSet.getNetRetDue());
		dataSetFiller.setGRCPFTTB(dataSet.getGrcPftTillNow());
		dataSetFiller.setGRCPFTCH(dataSet.getGrcPftChg());
		dataSetFiller.setADVDUE(dataSet.getAdvDue());
		dataSetFiller.setCLAIMAMT(dataSet.getClaimAmt());
		dataSetFiller.setFEEAMOUNT(dataSet.getFeeAmount());
		dataSetFiller.setFinRepayMethod(dataSet.getFinRepayMethod());
		
		dataSetFiller.setRebate(dataSet.getRebate());

		int frqDfrCount = 0;
		int rpyDfrCount = 0;
		if(!isAccrualCal){
			if(dataSet.getSchdDate() != null){
				//frqDfrCount = getFinanceScheduleDetailDAO().getFrqDfrCount(dataSet.getFinReference()); 
			}
		}
		dataSetFiller.setFrqDfrCount(frqDfrCount);
		if(dataSet.getCurRpyDefCount() > rpyDfrCount){
			dataSetFiller.setRpyDfrCount(dataSet.getCurRpyDefCount() - rpyDfrCount);
		}else{
			dataSetFiller.setRpyDfrCount(rpyDfrCount);
		}
		
		if(financeType.getFinCategory().equals(FinanceConstants.PRODUCT_SUKUK)){
			if(premiumDetail != null){
				
				dataSetFiller.setFACEVAL(premiumDetail.getFaceValue().multiply(new BigDecimal(premiumDetail.getNoOfUnits())));
				dataSetFiller.setPRMVALUE(dataSet.getFinAmount().subtract(dataSetFiller.getFACEVAL()));
				Date curBussDate = DateUtility.getAppDate();
				int totalDays = DateUtility.getDaysBetween(dataSet.getMaturityDate(), premiumDetail.getPurchaseDate());
				int curPrmDays = DateUtility.getDaysBetween(curBussDate, premiumDetail.getPurchaseDate());
				
				dataSetFiller.setPRMAMZ(dataSetFiller.getPRMVALUE().multiply(new BigDecimal(curPrmDays)).divide(
						new BigDecimal(totalDays), 0, RoundingMode.HALF_DOWN));
				dataSetFiller.setACCRBAL(premiumDetail.getAccruedProfit());
				
				//Calculation Of  Fair Value ReValuation Amount
				if(!isNewFinance && dataSet.getFinEvent().equals(AccountEventConstants.ACCEVENT_COMPOUND)){
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
		
		dataSetFiller.setDEDUCTFEEDISB(dataSet.getDeductFeeDisb());
		dataSetFiller.setADDFEETOFINANCE(dataSet.getAddFeeToFinance());
		dataSetFiller.setPAIDFEE(dataSet.getPaidFee());
		dataSetFiller.setWAIVEDFEE(dataSet.getWaivedFee());
		dataSetFiller.setBPI(dataSet.getBpiAmount());
		
		logger.debug("Leaving");
		return dataSetFiller;
	}
	
	private void doFillCommitmentData(DataSet dataSet, Commitment commitment, 
			AECommitment aeCommitment) throws IllegalAccessException, InvocationTargetException{

		logger.debug("Entering");
		Customer customer = getCustomerDAO().getCustomerForPostings(dataSet.getCustId());
		if(customer != null){
			BeanUtils.copyProperties(aeCommitment, customer);
		}
	
		aeCommitment.setCmtAmount(commitment.getCmtAmount());
		aeCommitment.setCmtAmountOther(commitment.getCmtAvailable());
		aeCommitment.setCmtUtilized(commitment.getCmtUtilizedAmount());
		aeCommitment.setCmtUtilizedOther(commitment.getCmtUtilizedAmount());
		aeCommitment.setCmtMultiBrach(commitment.isMultiBranch());
		aeCommitment.setCmtRevolving(commitment.isRevolving());
		aeCommitment.setCmtShared(commitment.isSharedCmt());
		aeCommitment.setCmtUsedEarlier(commitment.isNonperformingStatus());

		logger.debug("Leaving");
	}

	/**
	 * Method for Prepare Account Number based on  Transaction Entry Account
	 * @param dataSet
	 * @param transactionEntry
	 * @param amountRuleMap 
	 * @param sysIntAccMap 
	 * @param finType
	 * @return
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	private IAccounts getAccountNumber(DataSet dataSet,TransactionEntry transactionEntry, boolean isCommitment,boolean createNow,
			FinanceType financeType, Map<String, Object> accountsMap, Map<String, String> accountCcyMap, Map<String, String> sysIntAccMap,
			Map<String, String> ruleResultMap, DataSetFiller dataSetFiller) throws IllegalAccessException, InvocationTargetException {

		logger.debug("Entering");
		IAccounts newAccount = new IAccounts();
		newAccount.setAcCustCIF(dataSet.getCustCIF());
		newAccount.setAcBranch(dataSet.getFinBranch());
		newAccount.setAcCcy(dataSet.getFinCcy());
		newAccount.setFlagCreateIfNF(true);
		newAccount.setFlagCreateNew(false);
		newAccount.setInternalAc(false);
		
		String tranOrder = String.valueOf(transactionEntry.getAccountSetid()+"-"+transactionEntry.getTransOrder());
		newAccount.setTransOrder(tranOrder);
		newAccount.setTranAc(transactionEntry.getAccount());

		//Set Disbursement Account
		if(transactionEntry.getAccount().equals(AccountConstants.TRANACC_DISB)){
			
			newAccount.setAcType(AccountConstants.TRANACC_DISB);
			newAccount.setFlagCreateIfNF(false);
			newAccount.setAccountId(dataSet.getDisburseAccount());
			
			if(!createNow){
				accountsMap.put(tranOrder, transactionEntry.getAccount()+"-"+transactionEntry.getTransOrder());
			}else{
				accountsMap.put(tranOrder,transactionEntry.getAccount());
			}
			
			//Account Saving For Currency Conversion Check
			if(StringUtils.isNotBlank(dataSet.getDisburseAccount()) && 
					!accountCcyMap.containsKey(dataSet.getDisburseAccount())){
				accountCcyMap.put(dataSet.getDisburseAccount(), "");
			}
			
			return newAccount;
		}

		//Set Customer Repayments Account
		if(transactionEntry.getAccount().equals(AccountConstants.TRANACC_REPAY)){
			
			newAccount.setAcType(AccountConstants.TRANACC_REPAY);
			
			if(isCommitment){
				if(dataSet.getCmtChargeAmount().compareTo(BigDecimal.ZERO) != 0){
					newAccount.setFlagCreateIfNF(true);
					newAccount.setAccountId(dataSet.getCmtChargeAccount());
					
					//Account Saving For Currency Conversion Check
					if(StringUtils.isNotBlank(dataSet.getCmtChargeAccount()) && 
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
				if(StringUtils.isNotBlank(dataSet.getRepayAccount()) && 
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
			if(StringUtils.isNotBlank(dataSet.getDisburseAccount()) && 
					!accountCcyMap.containsKey(dataSet.getDisburseAccount())){
				accountCcyMap.put(dataSet.getDisburseAccount(), "");
			}
			
			logger.debug("Leaving");
			return newAccount;
		}
		
		//Set Disbursement Account
		if(transactionEntry.getAccount().equals(AccountConstants.TRANACC_DOWNPAY)){
			
			newAccount.setAcType(AccountConstants.TRANACC_DOWNPAY);
			newAccount.setFlagCreateIfNF(false);
			newAccount.setAccountId(dataSet.getDownPayAccount());
			
			if(!createNow){
				accountsMap.put(tranOrder, transactionEntry.getAccount()+"-"+transactionEntry.getTransOrder());
			}else{
				accountsMap.put(tranOrder,transactionEntry.getAccount());
			}
			
			//Account Saving For Currency Conversion Check
			if(StringUtils.isNotBlank(dataSet.getDownPayAccount()) && 
					!accountCcyMap.containsKey(dataSet.getRepayAccount())){
				accountCcyMap.put(dataSet.getDownPayAccount(), "");
			}
			
			return newAccount;
		}
		
		//Set Finance Cancel Account
		if(transactionEntry.getAccount().equals(AccountConstants.TRANACC_CANFIN)){

			newAccount.setAcType(AccountConstants.TRANACC_CANFIN);
			newAccount.setFlagCreateIfNF(false);
			newAccount.setAccountId(dataSet.getFinCancelAc());

			if(!createNow){
				accountsMap.put(tranOrder, transactionEntry.getAccount()+"-"+transactionEntry.getTransOrder());
			}else{
				accountsMap.put(tranOrder,transactionEntry.getAccount());
			}

			//Account Saving For Currency Conversion Check
			if(StringUtils.isNotBlank(dataSet.getFinCancelAc()) && 
					!accountCcyMap.containsKey(dataSet.getFinCancelAc())){
				accountCcyMap.put(dataSet.getFinCancelAc(), "");
			}

			return newAccount;
		}
		
		//Set Finance Writeoff Account
		if(transactionEntry.getAccount().equals(AccountConstants.TRANACC_WRITEOFF)){

			newAccount.setAcType(AccountConstants.TRANACC_WRITEOFF);
			newAccount.setFlagCreateIfNF(false);
			newAccount.setAccountId(dataSet.getFinWriteoffAc());

			if(!createNow){
				accountsMap.put(tranOrder, transactionEntry.getAccount()+"-"+transactionEntry.getTransOrder());
			}else{
				accountsMap.put(tranOrder,transactionEntry.getAccount());
			}

			//Account Saving For Currency Conversion Check
			if(StringUtils.isNotBlank(dataSet.getFinWriteoffAc()) && 
					!accountCcyMap.containsKey(dataSet.getFinWriteoffAc())){
				accountCcyMap.put(dataSet.getFinWriteoffAc(), "");
			}

			return newAccount;
		}
		
		//Set Finance Fee Account
		if(transactionEntry.getAccount().equals(AccountConstants.TRANACC_FEEAC)){

			newAccount.setAcType(AccountConstants.TRANACC_FEEAC);
			newAccount.setFlagCreateIfNF(false);
			newAccount.setAccountId(dataSet.getFeeAccountId());

			if(!createNow){
				accountsMap.put(tranOrder, transactionEntry.getAccount()+"-"+transactionEntry.getTransOrder());
			}else{
				accountsMap.put(tranOrder,transactionEntry.getAccount());
			}

			//Account Saving For Currency Conversion Check
			if(StringUtils.isNotBlank(dataSet.getFeeAccountId()) && 
					!accountCcyMap.containsKey(dataSet.getFeeAccountId())){
				accountCcyMap.put(dataSet.getFeeAccountId(), "");
			}

			return newAccount;
		}

		//Set Writeoff Payment Account
		if(transactionEntry.getAccount().equals(AccountConstants.TRANACC_WRITEOFFPAY)){


			newAccount.setAcType(AccountConstants.TRANACC_WRITEOFFPAY);
			newAccount.setFlagCreateIfNF(false);
			newAccount.setAccountId(dataSet.getFinWriteoffPayAc());

			if(!createNow){
				accountsMap.put(tranOrder, transactionEntry.getAccount()+"-"+transactionEntry.getTransOrder());
			}else{
				accountsMap.put(tranOrder,transactionEntry.getAccount());
			}

			//Account Saving For Currency Conversion Check
			if(StringUtils.isNotBlank(dataSet.getFinWriteoffPayAc()) && 
					!accountCcyMap.containsKey(dataSet.getFinWriteoffPayAc())){
				accountCcyMap.put(dataSet.getFinWriteoffPayAc(), "");
			}

			return newAccount;

		}
		
		//Set GL&PL Account
		if(transactionEntry.getAccount().equals(AccountConstants.TRANACC_GLNPL)){
			
			newAccount.setAcType(transactionEntry.getAccountType());
			newAccount.setInternalAc(true);
			newAccount.setAccountId(generateAccount(dataSet, transactionEntry.getAccountType(), false, transactionEntry.getAccountSubHeadRule(), 
					transactionEntry.getDebitcredit(), sysIntAccMap, ruleResultMap));
			
			if(!createNow){
				accountsMap.put(tranOrder, transactionEntry.getAccount() +"-"+transactionEntry.getTransOrder());
			}else{
				accountsMap.put(tranOrder,transactionEntry.getAccount()+transactionEntry.getAccountType());
			}
			
			logger.debug("Leaving");
			return newAccount;
		}
		
		//Set Customer Loan Account
		if(transactionEntry.getAccount().equals(AccountConstants.TRANACC_FIN)){
			
			newAccount.setAcType(financeType.getFinAcType());
			if(StringUtils.isNotBlank(dataSet.getFinAccount())){
				newAccount.setAccountId(dataSet.getFinAccount());
			}else{
				
				if (financeType.isFinIsOpenNewFinAc()) {
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
		if (transactionEntry.getAccount().equals(AccountConstants.TRANACC_UNEARN)) {

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
		if (transactionEntry.getAccount().equals(AccountConstants.TRANACC_SUSP)) {

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
		if (transactionEntry.getAccount().equals(AccountConstants.TRANACC_PROVSN)) {

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
		if(transactionEntry.getAccount().equals(AccountConstants.TRANACC_CUSTSYS)){
			
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
		if(transactionEntry.getAccount().equals(AccountConstants.TRANACC_COMMIT)){
			
			String acType = SysParamUtil.getValueAsString("COMMITMENT_AC_TYPE");
			newAccount.setAcType(acType);
			newAccount.setFlagCreateNew(dataSet.isOpenNewCmtAc());
			newAccount.setAccountId("");
			if(!dataSet.isOpenNewCmtAc()){
				newAccount.setAccountId(StringUtils.trimToEmpty(dataSet.getCmtAccount()));
			}
			
			if(!dataSet.getFinEvent().equals(AccountEventConstants.ACCEVENT_NEWCMT)){
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
		if(transactionEntry.getAccount().equals(AccountConstants.TRANACC_BUILD)){
			
			newAccount.setAcType(transactionEntry.getAccountType());
			newAccount.setInternalAc(true);
			
			//FIXME Constant to be changed
			if(ImplementationConstants.CLIENTTYPE.equals(ImplementationConstants.NBFC)){
				Rule rule = getRuleDAO().getRuleByID(transactionEntry.getAccountSubHeadRule(),RuleConstants.MODULE_SUBHEAD, RuleConstants.MODULE_SUBHEAD, "");
				HashMap<String, Object> map = dataSetFiller.getDeclaredFieldValues();
				map.put("reqFinAcType", transactionEntry.getAccountType());
				newAccount.setAccountId((String) getRuleExecutionUtil().executeRule(rule.getSQLRule(), map,
						dataSet.getFinCcy(), RuleReturnType.STRING));
			}else{
				newAccount.setAccountId(generateAccount(dataSet, transactionEntry.getAccountType(), true, transactionEntry.getAccountSubHeadRule(), 
					transactionEntry.getDebitcredit(), sysIntAccMap, ruleResultMap));
			}
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
	 * @param amountRuleMap 
	 * @param sysIntAccMap 
	 * @return
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	private String generateAccount(DataSet dataSet, String accountType, boolean isBuildAc, String subHeadRuleCode, String dbOrCr,
			Map<String, String> sysIntAccMap, Map<String, String> ruleResultMap) 
					throws IllegalAccessException, InvocationTargetException{
		
		//System Internal Account Number Fetching
		String sysIntAcNum =StringUtils.trimToEmpty( sysIntAccMap.get(accountType));

		String currencyNymber =CurrencyUtil.getCcyNumber(dataSet.getFinCcy());
		
		int glplAcLength = 0 ;
		if (StringUtils.isNotEmpty(sysIntAcNum)) {
			glplAcLength = sysIntAcNum.length() - SysParamUtil.getValueAsInt("SYSINT_ACCOUNT_LEN");
		}
		String accNumber = dataSet.getFinBranch()+sysIntAcNum.substring(glplAcLength)+currencyNymber;
	
		if(StringUtils.isNotBlank(subHeadRuleCode)){

			// Get Rule execution result from map object
			String subHeadCode = ruleResultMap.get(subHeadRuleCode);

			if (StringUtils.trimToEmpty(subHeadCode).contains(".")) {
				subHeadCode = subHeadCode.substring(0, subHeadCode.indexOf('.'));
			}
			
			if(isBuildAc){
				logger.debug("Leaving");
				return subHeadCode;
			}
			
			if(StringUtils.isNotBlank(subHeadCode)){
				if(sysIntAcNum.length() > subHeadCode.length()){
					String sIANumber = sysIntAcNum.substring(glplAcLength, sysIntAcNum.length() - subHeadCode.length());
					logger.debug("Leaving");
					return dataSet.getFinBranch() + sIANumber + subHeadCode + currencyNymber;
				}
			}
			
			logger.debug("Leaving");
			return accNumber;
			
		}else{
			logger.debug("Leaving");
			return accNumber;
		}
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
			Map<String, FeeRule> feeRuleDetailMap, String finCcy,DataSetFiller dataSetFiller,
			AECommitment aeCommitment, VASRecording vASRecording) throws IllegalAccessException,
			InvocationTargetException {
		logger.debug("Entering");

		// Execute Transaction Entry Rule
		BigDecimal amount = BigDecimal.ZERO;
		if (event.startsWith(AccountEventConstants.ACCEVENT_ADDDBS)) {
			event = AccountEventConstants.ACCEVENT_ADDDBS;
		}

		String amountRule = transactionEntry.getAmountRule();
		String[] res = StringUtils.trimToEmpty(transactionEntry.getFeeCode()).split(",");

		if (res.length > 0) {
			for (int i = 0; i < res.length; i++) {
				if (!(StringUtils.isBlank(res[i]) || "Result".equalsIgnoreCase(res[i]))) {
					String trim = res[i].trim();

					if (feeRuleDetailMap != null
							&& feeRuleDetailMap.containsKey(trim.substring(0, trim.contains("_") ? trim.indexOf('_')
									: trim.length()))) {
						FeeRule feeRule = feeRuleDetailMap.get(trim.substring(0, trim.contains("_") ? trim.indexOf('_')
								: trim.length()));

						if (amountRule.contains(trim + "_C")) {
							amountRule = amountRule.replace(trim + "_C", feeRule.getFeeAmount().toString());
						}
						if (amountRule.contains(trim + "_W")) {
							amountRule = amountRule.replace(trim + "_W", feeRule.getWaiverAmount().toString());
						}
						if (amountRule.contains(trim + "_P")) {
							amountRule = amountRule.replace(trim + "_P", feeRule.getPaidAmount().toString());
						}
						if (amountRule.contains(trim + "_AF")) {
							if (StringUtils.equals(feeRule.getFeeToFinance(), RuleConstants.DFT_FEE_FINANCE)) {
								amountRule = amountRule.replace(trim + "_AF",
										(feeRule.getFeeAmount().subtract(feeRule.getWaiverAmount()).subtract(feeRule
												.getPaidAmount())).toString());
							} else {
								amountRule = amountRule.replace(trim + "_AF", "0");
							}
						}
						if (amountRule.contains(trim + "_SCH")) {
							if (feeRule.getFeeToFinance().equals(CalculationConstants.REMFEE_SCHD_TO_ENTIRE_TENOR)
									|| feeRule.getFeeToFinance().equals(
											CalculationConstants.REMFEE_SCHD_TO_FIRST_INSTALLMENT)
									|| feeRule.getFeeToFinance().equals(
											CalculationConstants.REMFEE_SCHD_TO_N_INSTALLMENTS)) {
								amountRule = amountRule.replace(trim + "_SCH", feeRule.getFeeAmount().toString());
							} else {
								amountRule = amountRule.replace(trim + "_SCH", "0");
							}
						}
					} else {
						Rule rule = getRuleDAO().getRuleByID(trim, RuleConstants.MODULE_FEES, event, "");
						if (event.equals(AccountEventConstants.ACCEVENT_NEWCMT)
								|| event.equals(AccountEventConstants.ACCEVENT_MNTCMT)) {
							if (rule != null) {
								try {
									amount = (BigDecimal) getRuleExecutionUtil().executeRule(rule.getSQLRule(),
											aeCommitment.getDeclaredFieldValues(), finCcy, RuleReturnType.DECIMAL);

									if (amountRule.contains(trim + "_C")) {
										amountRule = amountRule.replace(trim + "_C", amount.toString());
									}
									if (amountRule.contains(trim + "_W")) {
										amountRule = amountRule.replace(trim + "_W", "0");
									}
									if (amountRule.contains(trim + "_P")) {
										amountRule = amountRule.replace(trim + "_P", "0");
									}
									if (amountRule.contains(trim + "_AF")) {
										if (StringUtils.equals(rule.getFeeToFinance(), RuleConstants.DFT_FEE_FINANCE)) {
											amountRule = amountRule.replace(trim + "_AF", amount.toString());
										} else {
											amountRule = amountRule.replace(trim + "_AF", "0");
										}
									}
								} catch (Exception e) {
									logger.error("Exception: ", e);
								}
							}
						} else {
							if (rule != null) {
								try {
									Object result = getRuleExecutionUtil().executeRule(rule.getSQLRule(),
											dataSetFiller.getDeclaredFieldValues(), finCcy, RuleReturnType.DECIMAL);
									amount = new BigDecimal(result == null ? "0" : result.toString());

									if (amountRule.contains(trim + "_W")) {
										amountRule = amountRule.replace(trim + "_W", "0");
									}
									if (amountRule.contains(trim + "_C")) {
										amountRule = amountRule.replace(trim + "_C", amount.toString());
									}
									if (amountRule.contains(trim + "_P")) {
										amountRule = amountRule.replace(trim + "_P", "0");
									}
									if (amountRule.contains(trim + "_AF")) {
										if (StringUtils.equals(rule.getFeeToFinance(), RuleConstants.DFT_FEE_FINANCE)) {
											amountRule = amountRule.replace(trim + "_AF", amount.toString());
										} else {
											amountRule = amountRule.replace(trim + "_AF", "0");
										}
									}
								} catch (Exception e) {
									logger.error("Exception: ", e);
								}
							}
						}
					}
				}
			}
		}

		Object result = null;

		if (event.contains("CMT")) {
			result = getRuleExecutionUtil().executeRule(amountRule, aeCommitment.getDeclaredFieldValues(), finCcy,
					RuleReturnType.DECIMAL);
		} else if (event.contains("VAS")) {
			result = getRuleExecutionUtil().executeRule(amountRule, vASRecording.getDeclaredFieldValues(), finCcy,
					RuleReturnType.DECIMAL);
		} else {
			result = getRuleExecutionUtil().executeRule(amountRule, dataSetFiller.getDeclaredFieldValues(), finCcy,
					RuleReturnType.DECIMAL);
		}
		amount = (BigDecimal) result;

		logger.debug("Leaving");

		return amount;
	}

	/**
	 * Method for Calculate the Amount Codes By Execution Formulae
	 * @param amountCode
	 * @return
	 */
	private void setAmountCodes(DataSetFiller dataSetFiller,AEAmountCodes aeAmountCodes,boolean isNewFinance, boolean isWIF, boolean isAccrualCal){
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

		dataSetFiller.setALWDPSP(aeAmountCodes.isAlwDPSP());
		dataSetFiller.setRolloverFinance(aeAmountCodes.isRolloverFinance());
		
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
			actualTotalCpzProfit = list.get(1);
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
		dataSetFiller.setFINISACTIVE(aeAmountCodes.isFinisActive());
		
		//Customer Details
		dataSetFiller.setCustEmpSts(aeAmountCodes.getCustEmpSts());
		dataSetFiller.setSalariedCustomer(aeAmountCodes.isSalariedCustomer());
		dataSetFiller.setCommissionRate(aeAmountCodes.getCommissionRate());
		dataSetFiller.setCommissionType(aeAmountCodes.getCommissionType());
		dataSetFiller.setDdaModified(aeAmountCodes.isDdaModified());
		dataSetFiller.setWOPAYAMT(aeAmountCodes.getWoPayAmt());
		
		//Asset Details
		dataSetFiller.setAssetProduct(aeAmountCodes.getAssetProduct());
		dataSetFiller.setAssetPurpose(aeAmountCodes.getAssetPurpose());
		
		// Repay Account Selection Flags
		dataSetFiller.setRepayInAdv(aeAmountCodes.isRepayInAdv());
		dataSetFiller.setRepayInPD(aeAmountCodes.isRepayInPD());
		dataSetFiller.setRepayInSusp(aeAmountCodes.isRepayInSusp());
		
		dataSetFiller.setSuplRentPay(aeAmountCodes.getSuplRentPay());
		dataSetFiller.setIncrCostPay(aeAmountCodes.getIncrCostPay());
		dataSetFiller.setSchFeePay(aeAmountCodes.getSchFeePay());
		
		dataSetFiller.setPreApprovalExpired(aeAmountCodes.isPreApprovalExpired());
		dataSetFiller.setPreApprovalFinance(aeAmountCodes.isPreApprovalFinance());

		logger.debug("Leaving");
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public SystemInternalAccountDefinitionDAO getInternalAccountDefinitionDAO() {
		return internalAccountDefinitionDAO;
	}
	public void setInternalAccountDefinitionDAO(SystemInternalAccountDefinitionDAO internalAccountDefinitionDAO) {
		this.internalAccountDefinitionDAO = internalAccountDefinitionDAO;
	}

	public void setRuleDAO(RuleDAO ruleDAO) {
		this.ruleDAO = ruleDAO;
	}
	public RuleDAO getRuleDAO() {
		return ruleDAO;
	}

	public void setFinanceTypeDAO(FinanceTypeDAO financeTypeDAO) {
		this.financeTypeDAO = financeTypeDAO;
	}
	public FinanceTypeDAO getFinanceTypeDAO() {
		return financeTypeDAO;
	}

	public TransactionEntryDAO getTransactionEntryDAO() {
		return transactionEntryDAO;
	}
	public void setTransactionEntryDAO(
			TransactionEntryDAO transactionEntryDAO) {
		this.transactionEntryDAO = transactionEntryDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}
	public FinanceMainDAO getFinanceMainDAO() {
		return financeMainDAO;
	}

	public CustomerDAO getCustomerDAO() {
		return customerDAO;
	}
	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}
	public FinanceScheduleDetailDAO getFinanceScheduleDetailDAO() {
		return financeScheduleDetailDAO;
	}
	public void setFinanceScheduleDetailDAO(
			FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	public void setCurrencyDAO(CurrencyDAO currencyDAO) {
		this.currencyDAO = currencyDAO;
	}
	public CurrencyDAO getCurrencyDAO() {
		return currencyDAO;
	}

	public void setFinanceSuspHeadDAO(FinanceSuspHeadDAO financeSuspHeadDAO) {
		this.financeSuspHeadDAO = financeSuspHeadDAO;
	}
	public FinanceSuspHeadDAO getFinanceSuspHeadDAO() {
		return financeSuspHeadDAO;
	}

	public void setAccountInterfaceService(AccountInterfaceService accountInterfaceService) {
		this.accountInterfaceService = accountInterfaceService;
	}
	public AccountInterfaceService getAccountInterfaceService() {
		return accountInterfaceService;
	}

	public void setRuleExecutionUtil(RuleExecutionUtil ruleExecutionUtil) {
		this.ruleExecutionUtil = ruleExecutionUtil;
    }
	public RuleExecutionUtil getRuleExecutionUtil() {
	    return ruleExecutionUtil;
    }

	public AccountingSetDAO getAccountingSetDAO() {
    	return accountingSetDAO;
    }
	public void setAccountingSetDAO(AccountingSetDAO accountingSetDAO) {
		this.accountingSetDAO = accountingSetDAO;
    }

	public FinancePremiumDetailDAO getFinancePremiumDetailDAO() {
    	return financePremiumDetailDAO;
    }
	public void setFinancePremiumDetailDAO(FinancePremiumDetailDAO financePremiumDetailDAO) {
		this.financePremiumDetailDAO = financePremiumDetailDAO;
    }
	
	public FinanceReferenceDetailDAO getFinanceReferenceDetailDAO() {
		return financeReferenceDetailDAO;
	}
	public void setFinanceReferenceDetailDAO(FinanceReferenceDetailDAO financeReferenceDetailDAO) {
		this.financeReferenceDetailDAO = financeReferenceDetailDAO;
	}

	public FinTypeAccountingDAO getFinTypeAccountingDAO() {
		return finTypeAccountingDAO;
	}
	public void setFinTypeAccountingDAO(FinTypeAccountingDAO finTypeAccountingDAO) {
		this.finTypeAccountingDAO = finTypeAccountingDAO;
	}

	public CollateralSetupDAO getCollateralSetupDAO() {
		return collateralSetupDAO;
	}

	public void setCollateralSetupDAO(CollateralSetupDAO collateralSetupDAO) {
		this.collateralSetupDAO = collateralSetupDAO;
	}

}
