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
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.backend.dao.finance.FinanceEligibilityDetailDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.lmtmasters.FinanceReferenceDetailDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.customermasters.CustomerEligibilityCheck;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceEligibilityDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.EligibilityDetailService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.RuleReturnType;
import com.pennanttech.pennapps.core.model.ErrorDetail;

public class EligibilityDetailServiceImpl extends GenericService<FinanceDetail> implements EligibilityDetailService {
	
	private static final Logger logger = Logger.getLogger(FinanceDetailServiceImpl.class);

	private FinanceMainDAO financeMainDAO;
	private FinanceReferenceDetailDAO financeReferenceDetailDAO;
	private FinanceEligibilityDetailDAO financeEligibilityDetailDAO;
	private RuleExecutionUtil ruleExecutionUtil;
	
	public EligibilityDetailServiceImpl() {
		super();
	}
	
	/**
	 * Set Finance Eligibility Details to the Finance Detail
	 * @param financeDetail
	 * @param finType
	 * @param userRole
	 */
	@Override
	public List<FinanceEligibilityDetail> fetchEligibilityDetails(FinanceMain financeMain,List<FinanceReferenceDetail> financeReferenceList){
		logger.debug("Entering");
		return  fetchFinElgDetails(financeReferenceList, financeMain.getFinReference(), financeMain.getFinCcy(),
				financeMain.getFinAmount(), financeMain.isNewRecord());
	}
	
	/**
	 * Set Finance Eligibility Details to the Finance Detail
	 * @param financeDetail
	 * @param finType
	 * @param userRole
	 */
	@Override
	public List<FinanceEligibilityDetail> setFinanceEligibilityDetails(String finReference, String finCcy, BigDecimal finAmount, 
			boolean isNewRecord, String finType, String userRole,String screenEvent){
		logger.debug("Entering");
		
		List<FinanceReferenceDetail> financeReferenceList  =  getFinanceReferenceDetailDAO().getFinRefDetByRoleAndFinType(finType,
				screenEvent, userRole, null, "_AEView");
		
		logger.debug("Leaving");
		return fetchFinElgDetails(financeReferenceList, finReference, finCcy, finAmount, isNewRecord);
	}
	
	
	private  List<FinanceEligibilityDetail> fetchFinElgDetails(List<FinanceReferenceDetail> financeReferenceList,
			String finReference, String finCcy, BigDecimal finAmount,boolean isNewRecord){
		logger.debug("Entering");

		List<FinanceEligibilityDetail> eligibilityRuleList = null;

		//Fetching Existed Executed Eligibility Rule List in Previous Stages
		boolean executed = false;
		if(isNewRecord){
			eligibilityRuleList = new ArrayList<FinanceEligibilityDetail>();
		}else{
			eligibilityRuleList = getFinElgDetailList(finReference);
			for (FinanceEligibilityDetail financeEligibilityDetail : eligibilityRuleList) {
				financeEligibilityDetail.setEligible(getEligibilityStatus(financeEligibilityDetail, finCcy, finAmount));
			}
		}

		if(financeReferenceList == null || financeReferenceList.isEmpty()){
			eligibilityRuleList.clear();
		}else{
			for (FinanceReferenceDetail finReferenceDetail : financeReferenceList) {
				for (FinanceEligibilityDetail detail : eligibilityRuleList) {
					if(finReferenceDetail.getFinRefId() == detail.getElgRuleCode()){
						executed = true;
						detail.setExecute(true);
						detail.setElgRuleValue(finReferenceDetail.getLovDescElgRuleValue());
						detail.setAllowDeviation(finReferenceDetail.isAllowDeviation());
						break;
					}
				}
				if(!executed){
					eligibilityRuleList.add(prepareElgDetail(finReferenceDetail, finReference));
				}
				executed = false;
			}
			financeReferenceList.clear();
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
		detail.setAllowDeviation(referenceDetail.isAllowDeviation());
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
  		customerEligibilityCheck.setDisbursedAmount(CalculationUtil.getConvertedAmount(finCcy, null, customerEligibilityCheck.getDisbursedAmount()));
  		
  		BigDecimal downpaybank = customerEligibilityCheck.getDownpayBank();
  		customerEligibilityCheck.setDownpayBank(CalculationUtil.getConvertedAmount(finCcy, null, downpaybank));
  		BigDecimal downpaySupl = customerEligibilityCheck.getDownpaySupl();
  		customerEligibilityCheck.setDownpaySupl(CalculationUtil.getConvertedAmount(finCcy, null, downpaySupl));
  		
  		Object object = getRuleExecutionUtil().executeRule(rule, customerEligibilityCheck.getDeclaredFieldValues(), finCcy, RuleReturnType.DECIMAL);
  		
  		
		if (object != null) {
			if(object instanceof BigDecimal) {
				//unFormating object
				int formatter = CurrencyUtil.getFormat(finCcy);
				object = PennantApplicationUtil.unFormateAmount((BigDecimal) object, formatter);
			} 
			ruleResString = object.toString();
		} else {
			ruleResString = null;
		}  
		
  		//Amount Conversion Based upon Finance Currency
		if(RuleConstants.RETURNTYPE_DECIMAL.equals(financeEligibilityDetail.getRuleResultType()) &&  StringUtils.isNumeric(ruleResString)){
			
			if(StringUtils.isNotBlank(ruleResString)){
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
		if (StringUtils.isBlank(ruleResString)) {
	        return false;
        }
		if(PennantStaticListUtil.getConstElgRules().contains(financeEligibilityDetail.getLovDescElgRuleCode())){
			return true;
		}
		if(RuleConstants.RETURNTYPE_DECIMAL.equals(financeEligibilityDetail.getRuleResultType())){
			if("E".equals(ruleResString)){
				return false;
			}else if(finAmount.compareTo(new BigDecimal(ruleResString)) > 0 ){
				if(financeEligibilityDetail.isUserOverride() && finAmount.compareTo(financeEligibilityDetail.getOverrideResult()) <= 0 ){	
					return true;
				}else{
					return false;
				}
			} 
		}else if("0.0".equals(ruleResString)|| "0.00".equals(ruleResString) || "-1.00".equals(ruleResString)){
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
	public List<AuditDetail> saveOrUpdate(FinanceDetail financeDetail) {
		logger.debug("Entering");
		
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		FinanceEligibilityDetail eligibilityDetail = new FinanceEligibilityDetail();
		String[] fields = PennantJavaUtil.getFieldDetails(eligibilityDetail, eligibilityDetail.getExcludeFields());
		
		List<FinanceEligibilityDetail>  elgRuleList = financeDetail.getElgRuleList();
		if ( elgRuleList != null && !elgRuleList.isEmpty()) {
			List<FinanceEligibilityDetail> updateList = new ArrayList<FinanceEligibilityDetail>();
			List<FinanceEligibilityDetail> insertList = new ArrayList<FinanceEligibilityDetail>();
			
			FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

			for(int i=0; i<elgRuleList.size(); i++){
				FinanceEligibilityDetail detail = elgRuleList.get(i);
				detail.setFinReference(financeDetail.getFinScheduleData().getFinReference());
				if(detail.isExecute()){
					detail.setLastMntBy(financeMain.getLastMntBy());
					detail.setLastMntOn(financeMain.getLastMntOn());
					detail.setRoleCode(financeMain.getRoleCode());
					detail.setRecordStatus(financeMain.getRecordStatus());
					if(getFinanceEligibilityDetailDAO().getFinElgDetailCount(detail) > 0){
						updateList.add(detail);
						auditDetails.add(new AuditDetail(PennantConstants.TRAN_WF, i+1, fields[0], fields[1], null, detail));
					}else{
						insertList.add(detail);
						auditDetails.add(new AuditDetail(PennantConstants.TRAN_WF, i+1, fields[0], fields[1], null, detail));
					}
				}	
			}
			if(!insertList.isEmpty()){
				saveList(insertList,"");
			}
			if(!updateList.isEmpty()){
				getFinanceEligibilityDetailDAO().updateList(updateList);
			}
			insertList.clear();
			updateList.clear();
		}
		logger.debug("Leaving");
		return auditDetails;
	}
	
	@Override
    public void deleteByFinRef(String finReference) {
	    getFinanceEligibilityDetailDAO().deleteByFinRef(finReference);
    }
	
	@Override
	public void saveList(List<FinanceEligibilityDetail> list,String type) {
		getFinanceEligibilityDetailDAO().saveList(list,type);
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
	 * @param financeEligibilityDetailList
	 * @param auditDetail
	 * @param errParm
	 * @param valueParm
	 * @param usrLanguage
	 */
	@Override
	public void validate(List<FinanceEligibilityDetail> financeEligibilityDetailList, AuditDetail auditDetail, String[] errParm, String[] valueParm, String usrLanguage) {
		// Eligibility
		if (!isCustEligible(financeEligibilityDetailList)) {
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,  "30563", errParm, valueParm), usrLanguage));
		}
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
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
