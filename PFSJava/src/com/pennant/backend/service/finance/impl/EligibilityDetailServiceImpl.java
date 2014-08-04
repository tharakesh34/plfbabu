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
 * FileName    		:  EligibilityDetailServiceImpl.java     	                            * 	  
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

package com.pennant.backend.service.finance.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.backend.dao.finance.FinanceEligibilityDetailDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.lmtmasters.FinanceReferenceDetailDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.customermasters.CustomerEligibilityCheck;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceEligibilityDetail;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.EligibilityDetailService;
import com.pennant.backend.util.PennantConstants;

public class EligibilityDetailServiceImpl extends GenericService<FinanceDetail> implements EligibilityDetailService {
	
	private final static Logger logger = Logger.getLogger(FinanceDetailServiceImpl.class);

	private FinanceMainDAO financeMainDAO;
	private FinanceReferenceDetailDAO financeReferenceDetailDAO;
	private FinanceEligibilityDetailDAO financeEligibilityDetailDAO;
	private RuleExecutionUtil ruleExecutionUtil;
	
	/**
	 * Set Finance Eligibility Details to the Finance Detail
	 * @param financeDetail
	 * @param finType
	 * @param userRole
	 */
	@Override
	public List<FinanceEligibilityDetail> setFinanceEligibilityDetails(String finReference, String finCcy, BigDecimal finAmount, 
			boolean isNewRecord, String finType, String userRole){
		logger.debug("Entering");
		
		List<FinanceReferenceDetail> financeReferenceList  = null;
		List<FinanceEligibilityDetail> eligibilityRuleList = null;
		
		//Fetching Existed Executed Eligibility Rule List in Previous Stages
 		boolean executed = false;
 		if(!isNewRecord){
 			eligibilityRuleList = getFinElgDetailList(finReference);
 		}else{
 			eligibilityRuleList = new ArrayList<FinanceEligibilityDetail>();
 		}
		financeReferenceList =  getFinanceReferenceDetailDAO().getFinRefDetByRoleAndFinType(finType, userRole, null, "_AEView");

		for (FinanceEligibilityDetail financeEligibilityDetail : eligibilityRuleList) {
			financeEligibilityDetail.setEligible(getEligibilityStatus(financeEligibilityDetail, finCcy, finAmount));
		}
		if(financeReferenceList != null && !financeReferenceList.isEmpty()){
			for (FinanceReferenceDetail finReferenceDetail : financeReferenceList) {
				for (FinanceEligibilityDetail detail : eligibilityRuleList) {
					if(finReferenceDetail.getFinRefId() == detail.getElgRuleCode()){
						executed = true;
						detail.setExecute(true);
						detail.setElgRuleValue(finReferenceDetail.getLovDescElgRuleValue());
						break;
					}
				}
				if(!executed){
					eligibilityRuleList.add(prepareElgDetail(finReferenceDetail, finReference));
				}
				executed = false;
			}
			financeReferenceList.clear();
		}else{
			eligibilityRuleList.clear();
		}
		
		logger.debug("Leaving");
		return eligibilityRuleList;
	}
	
	/**
	 * Method for Preparation of Eligibility Rule Details Execution List
	 * @param elgRuleCode
	 * @param ruleResult
	 * @param ruleResultType
	 * @return
	 */
	@Override
	public FinanceEligibilityDetail prepareElgDetail(FinanceReferenceDetail referenceDetail, String finReference){
		FinanceEligibilityDetail detail = new FinanceEligibilityDetail();
		detail.setFinReference(finReference);
		detail.setElgRuleCode(referenceDetail.getFinRefId());
		detail.setRuleResultType(referenceDetail.getLovDescRuleReturnType());
		detail.setCanOverride(referenceDetail.isOverRide());
		detail.setOverridePerc(referenceDetail.getOverRideValue());
		detail.setLovDescElgRuleCode(referenceDetail.getLovDescCodelov());
		detail.setLovDescElgRuleCodeDesc(referenceDetail.getLovDescNamelov());
		detail.setRuleResultType(referenceDetail.getLovDescRuleReturnType());
		detail.setElgRuleValue(referenceDetail.getLovDescElgRuleValue());
		detail.setEligible(false);
		detail.setRuleResult("");
		//detail= getElgResult(detail, financeDetail);
		detail.setExecute(true);
		return detail;
	}

	/**
	 * Method to invoke method to execute Eligibility rules and return result.
	 * 
	 * @param financeReferenceDetail
	 * @return String
	 */
	@Override
    public FinanceEligibilityDetail getElgResult(FinanceEligibilityDetail financeEligibilityDetail, FinanceDetail financeDetail) {
		logger.debug("Entering");
		CustomerEligibilityCheck customerEligibilityCheck = financeDetail.getCustomerEligibilityCheck();
 		String finCcy = financeDetail.getFinScheduleData().getFinanceMain().getFinCcy();
		
		String ruleResString = "";
		String rule = financeEligibilityDetail.getElgRuleValue(); 
  		BigDecimal finAmount = customerEligibilityCheck.getReqFinAmount();
  		customerEligibilityCheck.setReqFinAmount(CalculationUtil.getConvertedAmount(finCcy, null, finAmount));
  		
  		ruleResString = getRuleExecutionUtil().executeRule(rule, customerEligibilityCheck,finCcy);
  		
  		//Amount Conversion Based upon Finance Currency
		if("D".equals(financeEligibilityDetail.getRuleResultType()) &&  StringUtils.isNumeric(ruleResString)){
			
			if(!StringUtils.trimToEmpty(ruleResString).equals("")){
			//Get Finance Currency
				ruleResString = CalculationUtil.getConvertedAmount(null, finCcy, new BigDecimal(ruleResString)).toString();
			}else{
				ruleResString = "0";
			}
		}
		
		customerEligibilityCheck.setReqFinAmount(finAmount);
 		financeEligibilityDetail.setRuleResult(ruleResString);
		financeEligibilityDetail.setEligible(getEligibilityStatus(financeEligibilityDetail, finCcy, customerEligibilityCheck.getReqFinAmount()));
		logger.debug("Leaving");
		return financeEligibilityDetail;
	}
	
	/**
	 * Execute Rule by using rule engine
	 * @param rule
	 * @param customerEligibilityCheck
	 * @return
	 */
	@Override
    public boolean getEligibilityStatus(FinanceEligibilityDetail financeEligibilityDetail, String finCcy, BigDecimal financeAmount){
		BigDecimal finAmount = CalculationUtil.getConvertedAmount(null, finCcy, financeAmount);
		String ruleResString = financeEligibilityDetail.getRuleResult();
		if(financeEligibilityDetail.getLovDescElgRuleCode().equals("DSRCAL")){
			return true;
		}
		if("D".equals(financeEligibilityDetail.getRuleResultType())){
			if(finAmount.compareTo(new BigDecimal(ruleResString)) > 0 ){
				if(financeEligibilityDetail.isUserOverride() && finAmount.compareTo(financeEligibilityDetail.getOverrideResult()) <= 0 ){	
					return true;
				}else{
					return false;
				}
			} 
		}else if(ruleResString.equals("0.00") || ruleResString.equals("-1.00")){
			if(financeEligibilityDetail.isUserOverride()){	
				return true;
			}else{
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Returns if the customer is Eligible True/False
	 * @param List<FinanceEligibilityDetail> financeEligibilityDetails
	 * @return boolean(true/false)
	 */
	@Override
	public boolean isCustEligible(List<FinanceEligibilityDetail> financeEligibilityDetails){
		for (FinanceEligibilityDetail financeEligibilityDetail : financeEligibilityDetails) {
			if(!financeEligibilityDetail.isEligible()){
				return false;
			}
		}
		return true;
	}
	
		
	/**
	 * Save or update the Customer Eligibility Details
	 * @param financeDetail
	 */
	@Override
	public void saveOrUpdate(FinanceDetail financeDetail) {
		logger.debug("Entering");
		List<FinanceEligibilityDetail>  elgRuleList = financeDetail.getElgRuleList();
		if ( elgRuleList != null && !elgRuleList.isEmpty()) {
			List<FinanceEligibilityDetail> updateList = new ArrayList<FinanceEligibilityDetail>();
			List<FinanceEligibilityDetail> insertList = new ArrayList<FinanceEligibilityDetail>();

			for (FinanceEligibilityDetail detail : elgRuleList) {
				detail.setFinReference(financeDetail.getFinScheduleData().getFinReference());
				if(detail.isExecute()){
					detail.setLastMntBy(financeDetail.getFinScheduleData().getFinanceMain().getLastMntBy());
					detail.setLastMntOn(financeDetail.getFinScheduleData().getFinanceMain().getLastMntOn());
					if(getFinanceEligibilityDetailDAO().getFinElgDetailCount(detail) > 0){
						updateList.add(detail);
					}else{
						insertList.add(detail);
					}
				}	
			}
			if(!insertList.isEmpty()){
				getFinanceEligibilityDetailDAO().saveList(insertList);
			}
			if(!updateList.isEmpty()){
				getFinanceEligibilityDetailDAO().updateList(updateList);
			}
			insertList.clear();
			updateList.clear();
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Fetching List of Eligibility Details
	 * 
	 * @param finReference
	 * @return
	 */
	@Override
    public List<FinanceEligibilityDetail> getFinElgDetailList(String finReference) {
		return getFinanceEligibilityDetailDAO().getFinElgDetailByFinRef(finReference, "_View");
	}
	
	/**
	 * 
	 * Validate the Customer Eligibility Details
	 * @param FinanceEligibilityDetailList
	 * @param auditDetail
	 * @param errParm
	 * @param valueParm
	 * @param usrLanguage
	 */
	@Override
	public void validate(List<FinanceEligibilityDetail> FinanceEligibilityDetailList, AuditDetail auditDetail, String[] errParm, String[] valueParm, String usrLanguage) {
		// Eligibility
		if (!isCustEligible(FinanceEligibilityDetailList)) {
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,  "S0007", errParm, valueParm), usrLanguage));
		}
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
	
	public void setFinanceReferenceDetailDAO(FinanceReferenceDetailDAO financeReferenceDetailDAO) {
		this.financeReferenceDetailDAO = financeReferenceDetailDAO;
	}
	public FinanceReferenceDetailDAO getFinanceReferenceDetailDAO() {
		return financeReferenceDetailDAO;
	}
	
	public FinanceEligibilityDetailDAO getFinanceEligibilityDetailDAO() {
		return financeEligibilityDetailDAO;
	}
	public void setFinanceEligibilityDetailDAO(
	        FinanceEligibilityDetailDAO financeEligibilityDetailDAO) {
		this.financeEligibilityDetailDAO = financeEligibilityDetailDAO;
	}
	
    public RuleExecutionUtil getRuleExecutionUtil() {
		return ruleExecutionUtil;
	}
    public void setRuleExecutionUtil(RuleExecutionUtil ruleExecutionUtil) {
		this.ruleExecutionUtil = ruleExecutionUtil;
	}

}
