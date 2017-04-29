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
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.backend.dao.applicationmaster.CurrencyDAO;
import com.pennant.backend.dao.collateral.CollateralSetupDAO;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.FinanceSuspHeadDAO;
import com.pennant.backend.dao.lmtmasters.FinanceReferenceDetailDAO;
import com.pennant.backend.dao.masters.SystemInternalAccountDefinitionDAO;
import com.pennant.backend.dao.rmtmasters.AccountingSetDAO;
import com.pennant.backend.dao.rmtmasters.FinTypeAccountingDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.dao.rmtmasters.TransactionEntryDAO;
import com.pennant.backend.dao.rulefactory.PostingsDAO;
import com.pennant.backend.dao.rulefactory.RuleDAO;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.commitment.Commitment;
import com.pennant.backend.model.configuration.VASConfiguration;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.masters.SystemInternalAccountDefinition;
import com.pennant.backend.model.rmtmasters.TransactionEntry;
import com.pennant.backend.model.rulefactory.AECommitment;
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
	private FinanceReferenceDetailDAO financeReferenceDetailDAO;
	private FinTypeAccountingDAO finTypeAccountingDAO;
	private CollateralSetupDAO		collateralSetupDAO;
	private PostingsDAO				postingsDAO;

	//Default Constructor
	public AccountEngineExecution() {
		super();
	}
	
	/**
	 * Method for Execution of Accounting Sets depend on Event 
	 * @param createNow
	 * @param executingMap
	 * @param isAccrualCal
	 * @return
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws PFFInterfaceException 
	 */
	public List<ReturnDataSet> getAccEngineExecResults(String createNow, HashMap<String, Object> executingMap, boolean isAccrualCal)
			throws PFFInterfaceException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		// Execute entries depend on Finance Event
		long accountingSetId;
		String promotionCode = (String) executingMap.get("fm_promotionCode");
		String finType = (String) executingMap.get("ft_finType");

		if (StringUtils.isNotBlank(promotionCode)) {
			accountingSetId = getFinTypeAccountingDAO().getAccountSetID(promotionCode, (String) executingMap.get("ae_finEvent"),
					FinanceConstants.MODULEID_PROMOTION);
		} else {
			accountingSetId = getFinTypeAccountingDAO().getAccountSetID(finType, (String) executingMap.get("ae_finEvent"),
					FinanceConstants.MODULEID_FINTYPE);
		}

		List<TransactionEntry> transactionEntries = null;
		
		if (PennantConstants.APP_PHASE_DAY.equals(SysParamUtil.getValueAsString(PennantConstants.APP_PHASE))) {
			transactionEntries = getTransactionEntryDAO().getListTransactionEntryById(accountingSetId, "_AEView", true);
		} else {
			transactionEntries = EODProperties.getTransactionEntryList(accountingSetId);
		}

		List<ReturnDataSet> returnList = getPrepareAccountingSetResults(new HashMap<String, Object>(), transactionEntries, createNow, false, executingMap);
		
		//Method for Checking for Reverse Calculations Based upon Negative Amounts
		for (ReturnDataSet returnDataSet : returnList) {

			//returnDataSet.setLinkedTranId(linkedTranId);

			if (returnDataSet.getPostAmount().compareTo(BigDecimal.ZERO) < 0) {

				String tranCode = returnDataSet.getTranCode();
				String revTranCode = returnDataSet.getRevTranCode();
				String debitOrCredit = returnDataSet.getDrOrCr();

				returnDataSet.setTranCode(revTranCode);
				returnDataSet.setRevTranCode(tranCode);

				returnDataSet.setPostAmount(returnDataSet.getPostAmount().negate());

				if (debitOrCredit.equals(AccountConstants.TRANTYPE_CREDIT)) {
					returnDataSet.setDrOrCr(AccountConstants.TRANTYPE_DEBIT);
				} else {
					returnDataSet.setDrOrCr(AccountConstants.TRANTYPE_CREDIT);
				}
			}
		}

		logger.debug("Leaving");

		return returnList;
	}
	
	/**
	 * Method for Execution Stage entries for RIA Investments
	 * @param createNow
	 * @param roleCode
	 * @param executingMap
	 * @param premiumDetail
	 * @return
	 * @throws PFFInterfaceException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public List<ReturnDataSet> getStageExecResults(String createNow, String roleCode, HashMap<String, Object> executingMap) 
					throws PFFInterfaceException, IllegalAccessException, InvocationTargetException{
		logger.debug("Entering");

		// Fill Amount Code Detail Object with FinanceMain Object
		doFillExecutingMap(executingMap);
		setAmountCodes(executingMap, false, false);

		List<TransactionEntry> transactionEntries = getTransactionEntryDAO().getListTransactionEntryByRefType(
				(String) executingMap.get("ft_finType"), (String) executingMap.get("moduleDefiner"),
				FinanceConstants.PROCEDT_STAGEACC, roleCode, "_AEView", true);

		List<ReturnDataSet> returnList = getPrepareAccountingSetResults(new HashMap<String, Object>(), transactionEntries, createNow, false, executingMap);

		logger.debug("Leaving");

		return returnList;
	}
	
	/**
	 * Method for Execution Of Fee & Charges Rules
	 * 
	 * @param formatter
	 * @param isWIF
	 * @param executingMap
	 * @return
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws PFFInterfaceException
	 */
	public List<FeeRule> getFeeChargesExecResults(int formatter, boolean isWIF, HashMap<String, Object> executingMap)
			throws IllegalAccessException, InvocationTargetException, PFFInterfaceException {
		logger.debug("Entering");
		
		//Prepare AmountCode Details
		
		prepareAmountCodes(isWIF, false, executingMap);
		
		//Execute entries depend on Finance Event
		long accountingSetId;

		String promotionCode = (String) executingMap.get("fm_promotionCode");

		if (StringUtils.isNotBlank(promotionCode)) {
			accountingSetId = getFinTypeAccountingDAO().getAccountSetID(promotionCode, (String)executingMap.get("ae_finEvent"),
					FinanceConstants.MODULEID_PROMOTION);
		} else {
			accountingSetId = getFinTypeAccountingDAO().getAccountSetID((String) executingMap.get("ft_finType"), (String)executingMap.get("ae_finEvent"),
					FinanceConstants.MODULEID_FINTYPE);
		}

		List<FeeRule> feeRules = new ArrayList<FeeRule>();
		String ruleEvent= (String)executingMap.get("ae_finEvent");
		if(ruleEvent.startsWith(AccountEventConstants.ACCEVENT_ADDDBS) || 
				ruleEvent.startsWith(AccountEventConstants.ACCEVENT_DEFAULT)){
			ruleEvent = AccountEventConstants.ACCEVENT_ADDDBS;
		}
		
		if(executingMap.get("ae_disburse") == null){
			executingMap.put("ae_disburse", BigDecimal.ZERO);
		}
		
		//Fetch Stage Accounting AccountingSetId List 
		List<Long> accSetIdList = new ArrayList<Long>();
		accSetIdList.addAll(getFinanceReferenceDetailDAO().getRefIdListByFinType((String) executingMap.get("ft_finType"), 
				("").equals((String) executingMap.get("moduleDefiner"))? FinanceConstants.FINSER_EVENT_ORG : (String) executingMap.get("moduleDefiner"), null , "_ACView"));
		if (!FinanceConstants.FINSER_EVENT_PREAPPROVAL.equals((String) executingMap.get("moduleDefiner"))) {
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
				amount = (BigDecimal) getRuleExecutionUtil().executeRule(rule.getSQLRule(), executingMap,
						(String) executingMap.get("fm_finCcy"), RuleReturnType.DECIMAL);

				totalSchdFeeAmt = totalSchdFeeAmt.add(amount);
				executingMap.put("FEETOSCHD", totalSchdFeeAmt);
				feeRule.setFeeAmount(amount);
				feeRules.add(feeRule);
			}
		}
		
		logger.debug("Leaving");
		
		return feeRules;
	}
	
	/**
	 * Method for Execution Of Fee & Charges Rules
	 * 
	 * @param formatter
	 * @param isWIF
	 * @param existFeeList
	 * @param executingMap
	 * @return
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws PFFInterfaceException
	 */
	public List<FeeRule> getReExecFeeResults(int formatter, boolean isWIF, List<FeeRule> existFeeList,
			HashMap<String, Object> executingMap) throws IllegalAccessException, InvocationTargetException, PFFInterfaceException {
		logger.debug("Entering");
		
		//Prepare AmountCode Details
		prepareAmountCodes(isWIF, false, executingMap);
		
		//Execute entries depend on Finance Event
		long accountingSetId;
		String promotionCode = (String) executingMap.get("fm_promotionCode");

		if (StringUtils.isNotBlank(promotionCode)) {
			accountingSetId = getFinTypeAccountingDAO().getAccountSetID(promotionCode, (String)executingMap.get("ae_finEvent"),
					FinanceConstants.MODULEID_PROMOTION);
		} else {
			accountingSetId = getFinTypeAccountingDAO().getAccountSetID((String) executingMap.get("ft_finType"), (String)executingMap.get("ae_finEvent"),
					FinanceConstants.MODULEID_FINTYPE);
		}

		//Adding Existing Fees
		int feeOrder = 0;
		if(existFeeList != null && !existFeeList.isEmpty()){
			for (FeeRule feeRule : existFeeList) {
				executingMap.put("ae_disburse", 
						((BigDecimal)executingMap.get("ae_disburse")).add(feeRule.getFeeAmount().subtract(feeRule.getWaiverAmount()).subtract(feeRule.getPaidAmount())));
				feeOrder = feeRule.getFeeOrder();
			}
		}
		
		List<FeeRule> feeRules = existFeeList;
		String ruleEvent= (String)executingMap.get("ae_finEvent");
		if(ruleEvent.startsWith(AccountEventConstants.ACCEVENT_ADDDBS)){
			ruleEvent = AccountEventConstants.ACCEVENT_ADDDBS;
		}
		
		//Fetch Stage Accounting AccountingSetId List 
		List<Long> accSetIdList = new ArrayList<Long>();
		if((boolean) executingMap.get("ae_newRecord") && ruleEvent.startsWith(AccountEventConstants.ACCEVENT_ADDDBS)){
			accSetIdList.addAll(getFinanceReferenceDetailDAO().getRefIdListByFinType((String) executingMap.get("ft_finType"),
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
				
				BigDecimal amount = (BigDecimal) getRuleExecutionUtil().executeRule(rule.getSQLRule(), executingMap, (String) executingMap.get("fm_finCcy"), RuleReturnType.DECIMAL);

				executingMap.put("ae_disburse",((BigDecimal)executingMap.get("ae_disburse")).add(amount));
				feeRule.setFeeAmount(amount);
				feeRules.add(feeRule);
			}
		}

		logger.debug("Leaving");
	
		return feeRules;
	}
	
	/**
	 * Method For execution of Provision Rule For Provision Calculated Amount
	 * 
	 * @param executingMap
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws PFFInterfaceException
	 */
	public BigDecimal getProvisionExecResults(HashMap<String, Object> executingMap) throws IllegalAccessException,
			InvocationTargetException, PFFInterfaceException {

		doFillExecutingMap(executingMap);
		setAmountCodes(executingMap, false, false);	
		BigDecimal provCalAmount = BigDecimal.ZERO;

		String rule = getRuleDAO().getAmountRule("PROV", RuleConstants.MODULE_PROVSN, RuleConstants.EVENT_PROVSN);
	
		if (rule != null) {
			Object result = getRuleExecutionUtil().executeRule(rule, executingMap, (String) executingMap.get("fm_finCcy"), RuleReturnType.DECIMAL);
			provCalAmount = new BigDecimal(result == null ? "0" : result.toString());
		}
		
		return provCalAmount;
	}
	
	/**
	 * Method for Commitment posting Entries Execution
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws PFFInterfaceException 
	 */
	public List<ReturnDataSet> getCommitmentExecResults(AECommitment aeCommitment, Commitment commitment, String acSetEvent, 
			String createNow, HashMap<String, Object> executingMap) throws PFFInterfaceException, IllegalAccessException, InvocationTargetException{
		
		logger.debug("Entering");
		
		HashMap<String, Object> aeCommitmentMap = new HashMap<String, Object>();
		
		aeCommitmentMap.put("cmt_postDate", DateUtility.getSysDate());
		aeCommitmentMap.put("cmt_valueDate", DateUtility.getSysDate());
		
		//Commitment Execution Data Preparation
		doFillCommitmentData(commitment, aeCommitment, executingMap);
		
		Customer customer = getCustomerDAO().getCustomerForPostings(commitment.getCustID());
		
		if (customer != null) {
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
		
		commitment.getDeclaredFieldValues(aeCommitmentMap);
		
		//Accounting Set Details
		List<TransactionEntry> transactionEntries = null;
		long accountingSetId = getAccountingSetDAO().getAccountingSetId(acSetEvent, acSetEvent);
		if (accountingSetId != 0) {
			//get List of transaction entries
			transactionEntries = getTransactionEntryDAO().getListTransactionEntryById(accountingSetId, "_AEView",true);
		}
		
		List<ReturnDataSet> returnDataSets = null;
		if(transactionEntries != null && transactionEntries.size() > 0){
			returnDataSets =  getPrepareAccountingSetResults(aeCommitmentMap, transactionEntries, createNow,true, executingMap);
		}
		
		logger.debug("Leaving");
		
		return returnDataSets;
	}
	
	/**
	 * Method for VasRecording posting Entries Execution
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws PFFInterfaceException 
	 */
	public List<ReturnDataSet> getVasExecResults(String acSetEvent, String createNow, HashMap<String, Object> executingMap) 
			throws PFFInterfaceException, IllegalAccessException, InvocationTargetException{
		
		logger.debug("Entering");
		
		executingMap.put("ae_finEvent", acSetEvent);
		executingMap.put("PostDate", DateUtility.getSysDate());
		executingMap.put("ValueDate", DateUtility.getSysDate());

		//Setting Branch based on the VAS selection
		
		if(StringUtils.equals(VASConsatnts.VASAGAINST_CUSTOMER,(String) executingMap.get("vr_postingAgainst"))){
			
			Customer customer = getCustomerDAO().getCustomerByCIF((String) executingMap.get("vr_primaryLinkRef"),"");
			
			customer.getDeclaredFieldValues(executingMap);
			
		}else if(StringUtils.equals(VASConsatnts.VASAGAINST_FINANCE,(String) executingMap.get("vr_postingAgainst"))){
			
			FinanceMain finMain = getFinanceMainDAO().getFinanceMainForBatch((String) executingMap.get("vr_primaryLinkRef"));
			
			finMain.getDeclaredFieldValues(executingMap);
			
		}else if(StringUtils.equals(VASConsatnts.VASAGAINST_COLLATERAL,(String) executingMap.get("vr_postingAgainst"))){
			
			CollateralSetup collateralSetup = getCollateralSetupDAO().getCollateralSetupByRef((String) executingMap.get("vr_primaryLinkRef"),"");
			Customer customer = getCustomerDAO().getCustomerByID(collateralSetup.getDepositorId(),"");
			
			customer.getDeclaredFieldValues(executingMap);
			collateralSetup.getDeclaredFieldValues(executingMap);
		}

		//Accounting Set Details
		List<TransactionEntry> transactionEntries = null;
		VASConfiguration vASConfiguration = (VASConfiguration)executingMap.get("vr_vasConfiguration");
		long accountingSetId = vASConfiguration.getFeeAccounting();
		if (accountingSetId != 0) {
			//get List of transaction entries
			transactionEntries = getTransactionEntryDAO().getListTransactionEntryById(accountingSetId, "_AEView",true);
		}
		
		List<ReturnDataSet> returnDataSets = null;
		HashMap<String, Object> aeCommitmentMap = new HashMap<String, Object>();
		if(transactionEntries != null && transactionEntries.size() > 0){
			returnDataSets =  getPrepareAccountingSetResults(aeCommitmentMap, transactionEntries, createNow,true, executingMap);
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
	
	public List<ReturnDataSet> processAccountingByEvent(HashMap<String, Object> executingMap,String createNow) throws IllegalAccessException, InvocationTargetException, PFFInterfaceException {
		logger.debug("Entering");
	
		String acSetEvent = (String) executingMap.get("ae_finEvent");
		
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
			returnDataSets =  getPrepareAccountingSetResults(new HashMap<String, Object>(), transactionEntries, createNow,true, executingMap);
		}
		
		
		logger.debug("Leaving");
		
		return returnDataSets;
	}
	

	/**
	 * Method for Preparing List of FeeRule Objects 
	 * @param isWIF
	 * @param isAccrualCal
	 * @param premiumDetail
	 * @param executingMap
	 * @return
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	private HashMap<String, Object> prepareAmountCodes(boolean isWIF, boolean isAccrualCal, HashMap<String, Object> executingMap) 
			throws IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		// Fill Amount Code Detail Object with FinanceMain Object
		doFillExecutingMap(executingMap);
		setAmountCodes(executingMap, isWIF, isAccrualCal);

		logger.debug("Leaving");

		return executingMap;
	}
	
	/**
	 * Method for preparing List of ReturnDataSet objects by executing rules
	 * @param aeCommitmentMap
	 * @param transactionEntries
	 * @param createNow
	 * @param isCommitment
	 * @param executingMap
	 * @param aeCommitment
	 * @return
	 * @throws PFFInterfaceException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	private List<ReturnDataSet> getPrepareAccountingSetResults(HashMap<String, Object> aeCommitmentMap, List<TransactionEntry> transactionEntries,
			String createNow,boolean isCommitment, HashMap<String, Object> executingMap) 
					throws PFFInterfaceException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");
		logger.trace("FIN REFERENCE: " + (String)executingMap.get("fm_finReference"));
		
		Customer customer = getCustomerDAO().getCustomerForPostings((long)executingMap.get("fm_custID"));//TODO : Only using for Customer CIF, better remove DB hitting
		if (customer == null) {
			Messagebox.show(Labels.getLabel("Cust_NotFound"),Labels.getLabel("message.Error"),Messagebox.OK,"z-msgbox z-msgbox-error");
			return null;
		}else{
			executingMap.put("ct_custCIF", customer.getCustCIF());
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
			ruleResultMap =  doProcessSubHeadRules(subHeadSqlList, executingMap);
		}

		//Set Account number generation
		for (TransactionEntry transactionEntry : transactionEntries) {
			
			if("N".equals(createNow)){
				IAccounts account = getAccountNumber(transactionEntry,isCommitment,false, accountsMap, 
						accountCcyMap, sysIntAccMap, ruleResultMap, executingMap);
				if(account != null){
					accountsList.add(account);
				}
				
				account = null;
				
			}else{
				IAccounts account = getAccountNumber(transactionEntry,isCommitment,true, accountsMap, 
						accountCcyMap, sysIntAccMap, ruleResultMap, executingMap);
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
			
			String finReference = (String) executingMap.get("fm_finReference");
			String finBranch = (String) executingMap.get("fm_finBranch");
			String finEvent = (String) executingMap.get("ae_finEvent");
			String finPurpose = (String) executingMap.get("fm_finPurpose");
			String finCcy = (String) executingMap.get("fm_finCcy");
			long custID = (long) executingMap.get("fm_custID");
			Date postDate = (Date) executingMap.get("PostDate");
			
			returnDataSet.setFinReference(finReference);
			returnDataSet.setFinBranch(finBranch);
			returnDataSet.setFinEvent(finEvent);
			returnDataSet.setLovDescEventCodeName(transactionEntry.getLovDescEventCodeDesc());
			returnDataSet.setAccSetCodeName(transactionEntry.getLovDescAccSetCodeName());
			returnDataSet.setAccSetId(transactionEntry.getAccountSetid());
			returnDataSet.setCustId(custID);
			returnDataSet.setTranDesc(transactionEntry.getTransDesc());
			returnDataSet.setPostDate(postDate);
			returnDataSet.setValueDate(postDate);
			returnDataSet.setShadowPosting(transactionEntry.isShadowPosting());
			returnDataSet.setPostToSys(transactionEntry.getPostToSys());
			returnDataSet.setDerivedTranOrder(transactionEntry.getDerivedTranOrder());
			returnDataSet.setTransOrder(transactionEntry.getTransOrder());
			String ref = finReference + "/" + finEvent + "/" + transactionEntry.getTransOrder();
			returnDataSet.setPostingId(ref);
			returnDataSet.setFinPurpose(finPurpose);
			if("N".equals(createNow)){
				returnDataSet.setAccountType(transactionEntry.getAccount());
			}

			//Post Reference
			String branch = StringUtils.isBlank(transactionEntry.getAccountBranch())? finBranch:transactionEntry.getAccountBranch();
					
			String accType = "";
			if(executingMap.containsKey("ft_finAcType")){
				accType = StringUtils.isBlank(transactionEntry.getAccountType())? StringUtils.trimToEmpty((String) executingMap.get("ft_finAcType")):transactionEntry.getAccountType();
			}else{
				accType = StringUtils.isBlank(transactionEntry.getAccountType())? "":transactionEntry.getAccountType();
			}
			
			returnDataSet.setPostref(branch + "-" + accType + "-" + finCcy);
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
				
				String finType = "";
				if(executingMap.containsKey("ft_finType")) {
					finType = ((String)executingMap.get("ft_finType"));
				}
				returnDataSet.setFinType(finType);
				
				returnDataSet.setCustCIF(customer.getCustCIF());
				returnDataSet.setFinBranch(finBranch);
				returnDataSet.setFlagCreateNew(acc.getFlagCreateNew());
				returnDataSet.setFlagCreateIfNF(acc.getFlagCreateIfNF());
				returnDataSet.setInternalAc(acc.getInternalAc());
			}
			
			//Amount Rule Execution for Amount Calculation
			BigDecimal postAmt = executeAmountRule(finEvent, transactionEntry, finCcy, executingMap);
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
			returnDataSet.setPostAmountLcCcy(CalculationUtil.getConvertedAmount(finCcy, SysParamUtil.getAppCurrency(), postAmt));
			returnDataSet.setExchangeRate(CurrencyUtil.getExChangeRate(finCcy));
			
			//Converting Post Amount Based on Account Currency
			returnDataSet.setAcCcy(finCcy);
			returnDataSet.setFormatter(CurrencyUtil.getFormat(finCcy));
			
			BigDecimal postAmount = null;
			List<ReturnDataSet> newEntries = null;
			if("N".equals(createNow)){
				if(!StringUtils.equals(finCcy, acc.getAcCcy())){
					postAmount = returnDataSet.getPostAmount();
					returnDataSet.setPostAmount(CalculationUtil.getConvertedAmount(finCcy, acc.getAcCcy(), postAmount));
					
					//Add Extra Entries For Debit & Credit
					newEntries = createAccOnCCyConversion(returnDataSet,acc.getAcCcy(), postAmount, executingMap);
					
					returnDataSet.setAcCcy(acc.getAcCcy());
				}
			}else{
				if(accountCcyMap.containsKey(acc.getAccountId())){
					String acCcy = accountCcyMap.get(acc.getAccountId());
					
					if(!StringUtils.equals(finCcy, acCcy)){
						postAmount = returnDataSet.getPostAmount();
						returnDataSet.setPostAmount(CalculationUtil.getConvertedAmount(finCcy, acCcy, postAmount));
						
						//Add Extra Entries For Debit & Credit
						newEntries = createAccOnCCyConversion(returnDataSet, acCcy,  postAmount, executingMap);
						
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
	 * @param executingMap
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private Map<String, String> doProcessSubHeadRules(List<Rule> subHeadSqlList, HashMap<String, Object> executingMap) throws IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		Map<String, String> ruleResultMap = new HashMap<String, String>();

		if (subHeadSqlList != null) {
			for (Rule rule : subHeadSqlList) {
				String result = (String) getRuleExecutionUtil().executeRule(rule.getSQLRule(), executingMap, (String)executingMap.get("fm_finCcy"), RuleReturnType.STRING);
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

	private List<ReturnDataSet> createAccOnCCyConversion(ReturnDataSet existDataSet, String acCcy, BigDecimal unconvertedPostAmt, HashMap<String, Object> executingMap){
		
		List<ReturnDataSet> newEntries = new ArrayList<ReturnDataSet>(2);
		
		String finBranch = (String) executingMap.get("fm_finBranch");
		String finCcy = (String) executingMap.get("fm_finCcy");
		
		String actTranType = existDataSet.getDrOrCr();
		String phase = SysParamUtil.getValueAsString(PennantConstants.APP_PHASE);
		String finCcyNum = "";
		String acCcyNum = "";
		int formatter = 0;
		
		if(phase.equals(PennantConstants.APP_PHASE_EOD)){
			finCcyNum = EODProperties.getCcyNumber(finCcy);
			acCcyNum = EODProperties.getCcyNumber(acCcy);
		}else{
			finCcyNum = getCurrencyDAO().getCurrencyById(finCcy);
			
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
		newDataSet2.setAccount(finBranch + "881" + finCcyNum + acCcyNum);
		newDataSet2.setPostAmount(unconvertedPostAmt);
		newDataSet2.setPostAmountLcCcy(CalculationUtil.getConvertedAmount(finCcy, SysParamUtil.getAppCurrency(), unconvertedPostAmt));
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
	private void doFillExecutingMap(HashMap<String, Object> executingMap) throws IllegalAccessException, InvocationTargetException{
		logger.debug("Entering");

		//boolean isNewFinance = (boolean)executingMap.get("NEWRECORD");
		Customer customer = getCustomerDAO().getCustomerForPostings((long) executingMap.get("fm_custID"));
		if(customer != null){
			customer.getDeclaredFieldValues(executingMap);
		}
		
		logger.debug("Leaving");
	}
	
	
	private void doFillCommitmentData(Commitment commitment, AECommitment aeCommitment, HashMap<String, Object> executingMap) 
			throws IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");
		Customer customer = getCustomerDAO().getCustomerForPostings((long)executingMap.get("fm_custID"));
		if (customer != null) {
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
	 * @param transactionEntry
	 * @param isCommitment
	 * @param createNow 
	 * @param accountsMap 
	 * @param accountCcyMap
	 * @param sysIntAccMap
	 * @param ruleResultMap
	 * @param executingMap
	 * @return
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	private IAccounts getAccountNumber(TransactionEntry transactionEntry, boolean isCommitment,boolean createNow,
			Map<String, Object> accountsMap, Map<String, String> accountCcyMap, Map<String, String> sysIntAccMap,
			Map<String, String> ruleResultMap, HashMap<String, Object> executingMap) throws IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		IAccounts newAccount = new IAccounts();
		newAccount.setAcCustCIF((String)executingMap.get("ct_custCIF"));
		newAccount.setAcBranch((String)executingMap.get("fm_finBranch"));
		newAccount.setAcCcy((String)executingMap.get("fm_finCcy"));
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
			newAccount.setAccountId((String)executingMap.get("fm_disbAccountId"));
			
			if(!createNow){
				accountsMap.put(tranOrder, transactionEntry.getAccount()+"-"+transactionEntry.getTransOrder());
			}else{
				accountsMap.put(tranOrder,transactionEntry.getAccount());
			}
			
			//Account Saving For Currency Conversion Check
			if(executingMap.containsKey("fm_disbAccountId") && StringUtils.isNotBlank((String)executingMap.get("fm_disbAccountId")) && 
					!accountCcyMap.containsKey((String)executingMap.get("fm_disbAccountId"))){
				accountCcyMap.put((String)executingMap.get("fm_disbAccountId"), "");
			}
			
			return newAccount;
		}

		//Set Customer Repayments Account
		if(transactionEntry.getAccount().equals(AccountConstants.TRANACC_REPAY)){
			newAccount.setAcType(AccountConstants.TRANACC_REPAY);
			
			if(isCommitment){
				if(((BigDecimal)executingMap.get("cmt_cmtChargeAmount")).compareTo(BigDecimal.ZERO) != 0){
					newAccount.setFlagCreateIfNF(true);
					newAccount.setAccountId(((String)executingMap.get("cmt_cmtChargeAccount")));
					
					//Account Saving For Currency Conversion Check
					if(StringUtils.isNotBlank(((String)executingMap.get("cmt_cmtChargeAccount"))) && 
							!accountCcyMap.containsKey(((String)executingMap.get("cmt_cmtChargeAccount")))){
						accountCcyMap.put(((String)executingMap.get("cmt_cmtChargeAccount")), "");
					}
				}else{
					logger.debug("Leaving");
					return null;
				}
			}else{
				newAccount.setFlagCreateIfNF(false);
				newAccount.setAccountId(((String)executingMap.get("fm_repayAccountId")));
				
				//Account Saving For Currency Conversion Check
				if(StringUtils.isNotBlank(((String)executingMap.get("fm_repayAccountId"))) && 
						!accountCcyMap.containsKey(((String)executingMap.get("fm_repayAccountId")))){
					accountCcyMap.put(((String)executingMap.get("fm_repayAccountId")), "");
				}
			}
			
			if(!createNow){
				accountsMap.put(tranOrder, transactionEntry.getAccount()+"-"+transactionEntry.getTransOrder());
			}else{
				accountsMap.put(tranOrder,transactionEntry.getAccount());
			}
			
			//Account Saving For Currency Conversion Check
			if(StringUtils.isNotBlank(((String)executingMap.get("fm_disbAccountId"))) && 
					!accountCcyMap.containsKey(((String)executingMap.get("fm_disbAccountId")))){
				accountCcyMap.put(((String)executingMap.get("fm_disbAccountId")), "");
			}
			
			logger.debug("Leaving");
			return newAccount;
		}
		
		//Set Disbursement Account
		if(transactionEntry.getAccount().equals(AccountConstants.TRANACC_DOWNPAY)){
			
			newAccount.setAcType(AccountConstants.TRANACC_DOWNPAY);
			newAccount.setFlagCreateIfNF(false);
			newAccount.setAccountId(((String)executingMap.get("fm_downPayAccount")));
			
			if(!createNow){
				accountsMap.put(tranOrder, transactionEntry.getAccount()+"-"+transactionEntry.getTransOrder());
			}else{
				accountsMap.put(tranOrder,transactionEntry.getAccount());
			}
			
			//Account Saving For Currency Conversion Check
			if(StringUtils.isNotBlank(((String)executingMap.get("fm_downPayAccount"))) && 
					!accountCcyMap.containsKey(((String)executingMap.get("fm_repayAccountId")))){
				accountCcyMap.put(((String)executingMap.get("fm_downPayAccount")), "");
			}
			
			return newAccount;
		}
		
		//Set Finance Cancel Account
		if(transactionEntry.getAccount().equals(AccountConstants.TRANACC_CANFIN)){

			newAccount.setAcType(AccountConstants.TRANACC_CANFIN);
			newAccount.setFlagCreateIfNF(false);
			newAccount.setAccountId((String)executingMap.get("fm_finCancelAct"));

			if(!createNow){
				accountsMap.put(tranOrder, transactionEntry.getAccount()+"-"+transactionEntry.getTransOrder());
			}else{
				accountsMap.put(tranOrder,transactionEntry.getAccount());
			}

			//Account Saving For Currency Conversion Check
			if(StringUtils.isNotBlank((String)executingMap.get("fm_finCancelAct")) && 
					!accountCcyMap.containsKey((String)executingMap.get("fm_finCancelAct"))){
				accountCcyMap.put((String)executingMap.get("fm_finCancelAct"), "");
			}

			return newAccount;
		}
		
		//Set Finance Writeoff Account
		if(transactionEntry.getAccount().equals(AccountConstants.TRANACC_WRITEOFF)){

			newAccount.setAcType(AccountConstants.TRANACC_WRITEOFF);
			newAccount.setFlagCreateIfNF(false);
			newAccount.setAccountId((String)executingMap.get("fm_finWriteoffAc"));

			if(!createNow){
				accountsMap.put(tranOrder, transactionEntry.getAccount()+"-"+transactionEntry.getTransOrder());
			}else{
				accountsMap.put(tranOrder,transactionEntry.getAccount());
			}

			//Account Saving For Currency Conversion Check
			if(StringUtils.isNotBlank((String)executingMap.get("fm_finWriteoffAc")) && 
					!accountCcyMap.containsKey((String)executingMap.get("fm_finWriteoffAc"))){
				accountCcyMap.put((String)executingMap.get("fm_finWriteoffAc"), "");
			}

			return newAccount;
		}
		
		//Set Finance Fee Account
		if(transactionEntry.getAccount().equals(AccountConstants.TRANACC_FEEAC)){

			newAccount.setAcType(AccountConstants.TRANACC_FEEAC);
			newAccount.setFlagCreateIfNF(false);
			newAccount.setAccountId((String)executingMap.get("fm_feeAccountId"));

			if(!createNow){
				accountsMap.put(tranOrder, transactionEntry.getAccount()+"-"+transactionEntry.getTransOrder());
			}else{
				accountsMap.put(tranOrder,transactionEntry.getAccount());
			}

			//Account Saving For Currency Conversion Check
			if(StringUtils.isNotBlank((String)executingMap.get("fm_feeAccountId")) && 
					!accountCcyMap.containsKey((String)executingMap.get("fm_feeAccountId"))){
				accountCcyMap.put((String)executingMap.get("fm_feeAccountId"), "");
			}

			return newAccount;
		}

		//FIXME Kesav : writeOffPayAccount
		//Set Writeoff Payment Account
		/*if(transactionEntry.getAccount().equals(AccountConstants.TRANACC_WRITEOFFPAY)){


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

		}*/
		
		//Set GL&PL Account
		if(transactionEntry.getAccount().equals(AccountConstants.TRANACC_GLNPL)){
			
			newAccount.setAcType(transactionEntry.getAccountType());
			newAccount.setInternalAc(true);
			newAccount.setAccountId(generateAccount(transactionEntry.getAccountType(), false, transactionEntry.getAccountSubHeadRule(), 
					transactionEntry.getDebitcredit(), sysIntAccMap, ruleResultMap, executingMap));
			
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
			
			newAccount.setAcType((String)executingMap.get("ft_finAcType"));
			if(StringUtils.isNotBlank((String)executingMap.get("fm_finAccount"))){
				newAccount.setAccountId((String)executingMap.get("fm_finAccount"));
			}else{
				
				if ((boolean)executingMap.get("ft_finIsOpenNewFinAc")) {
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

			newAccount.setAcType((String)executingMap.get("ft_pftPayAcType"));

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

			newAccount.setAcType((String)executingMap.get("ft_finSuspAcType"));

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

			newAccount.setAcType((String)executingMap.get("ft_finProvisionAcType"));

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
		
		//FIXME Kesav : CommitmentAccount
		//Set Customer Loan Account
		/*if(transactionEntry.getAccount().equals(AccountConstants.TRANACC_COMMIT)){
			
			String acType = SysParamUtil.getValueAsString("COMMITMENT_AC_TYPE");
			newAccount.setAcType(acType);
			newAccount.setFlagCreateNew(dataSet.isOpenNewCmtAc());
			newAccount.setAccountId("");
			if(!dataSet.isOpenNewCmtAc()){
				newAccount.setAccountId(StringUtils.trimToEmpty(dataSet.getCmtAccount()));
			}
			
			if(!(String)executingMap.get("finEvent").equals(AccountEventConstants.ACCEVENT_NEWCMT)){
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
		}*/
		
		//Set Build Account
		if(transactionEntry.getAccount().equals(AccountConstants.TRANACC_BUILD)){
			
			newAccount.setAcType(transactionEntry.getAccountType());
			newAccount.setInternalAc(true);
			
			//FIXME Constant to be changed
			if(ImplementationConstants.CLIENTTYPE.equals(ImplementationConstants.NBFC)){
				Rule rule = getRuleDAO().getRuleByID(transactionEntry.getAccountSubHeadRule(),RuleConstants.MODULE_SUBHEAD, RuleConstants.MODULE_SUBHEAD, "");
				executingMap.put("acType", transactionEntry.getAccountType());
				newAccount.setAccountId((String) getRuleExecutionUtil().executeRule(rule.getSQLRule(), executingMap,
						(String) executingMap.get("fm_finCcy"), RuleReturnType.STRING));
			}else{
				newAccount.setAccountId(generateAccount(transactionEntry.getAccountType(), true, transactionEntry.getAccountSubHeadRule(), 
					transactionEntry.getDebitcredit(), sysIntAccMap, ruleResultMap, executingMap));
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
	 * 
	 * @param accountType
	 * @param isBuildAc
	 * @param subHeadRuleCode
	 * @param dbOrCr
	 * @param amountRuleMap
	 * @param sysIntAccMap
	 * @param ruleResultMap
	 * @param executingMap
	 * @return
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	private String generateAccount(String accountType, boolean isBuildAc, String subHeadRuleCode, String dbOrCr,
			Map<String, String> sysIntAccMap, Map<String, String> ruleResultMap, HashMap<String, Object> executingMap)
			throws IllegalAccessException, InvocationTargetException {

		// System Internal Account Number Fetching
		String sysIntAcNum = StringUtils.trimToEmpty(sysIntAccMap.get(accountType));

		String currencyNymber = CurrencyUtil.getCcyNumber((String) executingMap.get("fm_finCcy"));

		int glplAcLength = 0;
		if (StringUtils.isNotEmpty(sysIntAcNum)) {
			glplAcLength = sysIntAcNum.length() - SysParamUtil.getValueAsInt("SYSINT_ACCOUNT_LEN");
		}
		String accNumber = executingMap.get("fm_finBranch") + sysIntAcNum.substring(glplAcLength) + currencyNymber;

		if (StringUtils.isNotBlank(subHeadRuleCode)) {

			// Get Rule execution result from map object
			String subHeadCode = ruleResultMap.get(subHeadRuleCode);

			if (StringUtils.trimToEmpty(subHeadCode).contains(".")) {
				subHeadCode = subHeadCode.substring(0, subHeadCode.indexOf('.'));
			}

			if (isBuildAc) {
				logger.debug("Leaving");
				return subHeadCode;
			}

			if (StringUtils.isNotBlank(subHeadCode)) {
				if (sysIntAcNum.length() > subHeadCode.length()) {
					String sIANumber = sysIntAcNum.substring(glplAcLength, sysIntAcNum.length() - subHeadCode.length());
					logger.debug("Leaving");
					return executingMap.get("fm_finBranch") + sIANumber + subHeadCode + currencyNymber;
				}
			}

			logger.debug("Leaving");
			return accNumber;

		} else {
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
			String finCcy,HashMap<String, Object> executingMap) throws IllegalAccessException,
			InvocationTargetException {
		logger.debug("Entering");

		// Execute Transaction Entry Rule
		BigDecimal amount = BigDecimal.ZERO;
		if (event.startsWith(AccountEventConstants.ACCEVENT_ADDDBS)) {
			event = AccountEventConstants.ACCEVENT_ADDDBS;
		}

		String amountRule = transactionEntry.getAmountRule();
 
		Object result = null;

		if (event.contains("CMT")) {
			result = getRuleExecutionUtil().executeRule(amountRule, executingMap, finCcy,
					RuleReturnType.DECIMAL);
		} else if (event.contains("VAS")) {
			result = getRuleExecutionUtil().executeRule(amountRule, executingMap, finCcy,
					RuleReturnType.DECIMAL);
		} else {
			result = getRuleExecutionUtil().executeRule(amountRule, executingMap, finCcy,
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
	private void setAmountCodes(HashMap<String, Object> executingMap, boolean isWIF, boolean isAccrualCal){
		logger.debug("Entering");

		boolean finOverDueInPast = false;
		
		if((int)executingMap.get("ae_ODInst") > 0){
			finOverDueInPast = true;
		}

		BigDecimal actualTotalSchdProfit = BigDecimal.ZERO;
		BigDecimal actualTotalCpzProfit = BigDecimal.ZERO;

		if(!isAccrualCal && !(boolean)executingMap.get("ae_newRecord") && !isWIF){
			List<BigDecimal> list= getFinanceMainDAO().getActualPftBal((String)executingMap.get("fm_finReference"),"");
			actualTotalSchdProfit = list.get(0);
			actualTotalCpzProfit = list.get(1);
		}

		BigDecimal PFTCHG = ((BigDecimal)executingMap.get("ae_pft")).subtract(actualTotalSchdProfit);
		BigDecimal CPZCHG = ((BigDecimal)executingMap.get("ae_cpzTot")).subtract(actualTotalCpzProfit);
		
		executingMap.put("PFTCHG", PFTCHG);
		executingMap.put("CPZCHG", CPZCHG);
		executingMap.put("finOverDueInPast", finOverDueInPast);
		
		logger.debug("Leaving");
	}
	
	public List<ReturnDataSet> cancelPostings(long linkedTranid) {
		logger.debug("Entering");
		List<ReturnDataSet> returnSetEntries = getPostingsDAO().getPostingsByLinkTransId(linkedTranid);
		
		for (ReturnDataSet returnDataSet : returnSetEntries) {
			
			returnDataSet.setLinkedTranId(linkedTranid);
			
			if (returnDataSet.getPostAmount().compareTo(BigDecimal.ZERO) > 0) {
				
				String tranCode = returnDataSet.getTranCode();
				String revTranCode = returnDataSet.getRevTranCode();
				String debitOrCredit = returnDataSet.getDrOrCr();
				
				returnDataSet.setTranCode(revTranCode);
				returnDataSet.setRevTranCode(tranCode);
				
				if (debitOrCredit.equals(AccountConstants.TRANTYPE_CREDIT)) {
					returnDataSet.setDrOrCr(AccountConstants.TRANTYPE_DEBIT);
				} else {
					returnDataSet.setDrOrCr(AccountConstants.TRANTYPE_CREDIT);
				}
			}
		}
		
		return returnSetEntries;
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

	public PostingsDAO getPostingsDAO() {
		return postingsDAO;
	}

	public void setPostingsDAO(PostingsDAO postingsDAO) {
		this.postingsDAO = postingsDAO;
	}

}
