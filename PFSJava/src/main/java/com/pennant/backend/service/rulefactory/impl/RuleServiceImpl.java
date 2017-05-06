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
 *********************************************************************************************
 *                                 FILE HEADER                                               *
 *********************************************************************************************
 *
 * FileName    		:  RuleServiceImpl.java                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES              			
 *                                                                  
 * Creation Date    :  03-06-2011    
 *                                                                  
 * Modified Date    :  03-06-2011    
 *                                                                  
 * Description 		:                                             
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-06-2011       Pennant	                 0.1                                         * 
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

package com.pennant.backend.service.rulefactory.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.rulefactory.RuleDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.rulefactory.BMTRBFldCriterias;
import com.pennant.backend.model.rulefactory.BMTRBFldDetails;
import com.pennant.backend.model.rulefactory.NFScoreRuleDetail;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.model.rulefactory.RuleModule;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.limit.LimitGroupService;
import com.pennant.backend.service.rulefactory.RuleService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.cache.util.AccountingSetCache;

/**
 * Service implementation for methods that depends on <b>Rule</b>.<br>
 */
public class RuleServiceImpl extends GenericService<Rule> implements RuleService {

	private static Logger logger=Logger.getLogger(RuleServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private RuleDAO ruleDAO;
	private LimitGroupService limitGroupService;

	public RuleServiceImpl() {
		super();
	}
	
	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table Queries/Queries_Temp 
	 * 			by using RuleDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using RuleDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtRules by using auditHeaderDAO.addAudit(auditHeader)
	 *
	 * @param AuditHeader (auditHeader)    
	 * 
	 * @return auditHeader
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader, "saveOrUpdate");

		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		String tableType = "";
		Rule rule = (Rule) auditHeader.getAuditDetail().getModelData();

		if (rule.isWorkflow()) {
			tableType = "_Temp";
		}if (rule.isNew()) {
			getRuleDAO().save(rule,tableType);
			auditHeader.getAuditDetail().setModelData(rule);
			auditHeader.setAuditReference(rule.getRuleCode());
		} else {
			getRuleDAO().update(rule,tableType);
			if (StringUtils.isEmpty(tableType)) {
				AccountingSetCache.clearRuleCache(rule.getRuleCode(), rule.getRuleModule(), rule.getRuleEvent());
			}
		}
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}
	
	/**
	 * delete method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	delete Record for the DB table Rules by using RuleDAO's delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtRule by using auditHeaderDAO.addAudit(auditHeader)    
	 * 
	 * @param AuditHeader (auditHeader)    
	 * 
	 * @return auditHeader
	 */
	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader, "delete");

		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		Rule rule = (Rule) auditHeader.getAuditDetail().getModelData();
		getRuleDAO().delete(rule, "");
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getRuleById fetch the details by using RuleDAO's getRuleById method.
	 * 
	 * @param id (String)
	 * 
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return Rule
	 */
	@Override
	public Rule getRuleById(String id,String module,String event) {
		return getRuleDAO().getRuleByID(id,module,event, "_View");
	}
	
	/**
	 * Method for Fetch SQL Rule based on Parameters
	 */
	@Override
	public String getAmountRule(String id,String module,String event) {
		return getRuleDAO().getAmountRule(id,module,event);
	}

	/**
	 * getApprovedRuleById fetch the details by using RuleDAO's getRuleById method .
	 * with parameter id and type as blank. it fetches the approved records from the Rules.
	 * 
	 * @param id (String)
	 * 
	 * @return Rule
	 */
	public Rule getApprovedRuleById(String id,String module,String event) {
		return getRuleDAO().getRuleByID(id,module,event, "_AView");
	}
	
	/**
	 * This method return the columns list of the table
	 * 
	 * @return List
	 */
	@Override
	public List<BMTRBFldDetails> getFieldList(String module,String event) {
		return getRuleDAO().getFieldList(module, event);
	}

	/**
	 * This method return the Operator list 
	 * 
	 * @return List
	 */
	@Override
	public List<BMTRBFldCriterias> getOperatorsList() {
		return getRuleDAO().getOperatorsList();
	}

	/**
	 * Method for getting List of Rule Modules
	 */
	@Override
	public List<RuleModule> getRuleModules(String module) {
		return getRuleDAO().getRuleModules(module);
	}
	
	@Override
    public List<Rule> getRulesByGroupId(long groupId,String ruleModule, String ruleEvent) {
	    return getRuleDAO().getRulesByGroupId(groupId,ruleModule, ruleEvent,"_AView");
    }	

	@Override
    public List<NFScoreRuleDetail> getNFRulesByGroupId(long groupId) {
	    return getRuleDAO().getNFRulesByGroupId(groupId , "");
    }

	/**
	 * doApprove method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	based on the Record type do following actions
	 * 		a)  DELETE	Delete the record from the main table by using getRuleDAO().delete with parameters Rule,""
	 * 		b)  NEW		Add new record in to main table by using getRuleDAO().save with parameters Rule,""
	 * 		c)  EDIT	Update record in the main table by using getRuleDAO().update with parameters Rule,""
	 * 3)	Delete the record from the workFlow table by using getRuleDAO().delete with parameters Rule,"_Temp"
	 * 4)	Audit the record in to AuditHeader and AdtRules by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 5)  	Audit the record in to AuditHeader and AdtRules by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader (auditHeader)    
	 * 
	 * @return auditHeader
	 */
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader, "doApprove");
		String tranType="";
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		Rule rule = new Rule();
		BeanUtils.copyProperties((Rule) auditHeader.getAuditDetail().getModelData(), rule);

		if (rule.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getRuleDAO().delete(rule, "");
			AccountingSetCache.clearRuleCache(rule.getRuleCode(), rule.getRuleModule(), rule.getRuleEvent());
		} else {
			rule.setRoleCode("");
			rule.setNextRoleCode("");
			rule.setTaskId("");
			rule.setNextTaskId("");
			rule.setWorkflowId(0);

			if (rule.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				rule.setRecordType("");
				getRuleDAO().save(rule, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				rule.setRecordType("");
				getRuleDAO().update(rule, "");
				AccountingSetCache.clearRuleCache(rule.getRuleCode(), rule.getRuleModule(), rule.getRuleEvent());

			}
		}

		getRuleDAO().delete(rule, "_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(rule);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Delete the record from the workFlow table by using getRuleDAO().delete with parameters Rule,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtRules by using 
	 * 			auditHeaderDAO.addAudit(auditHeader) for Work flow
	 *
	 * @param AuditHeader (auditHeader)    
	 * 
	 * @return auditHeader
	 */
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader, "doReject");

		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		Rule rule = (Rule) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getRuleDAO().delete(rule, "_Temp");
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps.
	 * 1)	get the details from the auditHeader. 
	 * 2)	fetch the details from the tables
	 * 3)	Validate the Record based on the record details. 
	 * 4) 	Validate for any business validation.
	 * 5)	for any mismatch conditions Fetch the error details from 
	 * 			getRuleDAO().getErrorDetail with Error ID and language as parameters.
	 * 6)	if any error/Warnings  then assign the to auditHeader 
	 * 
	 * @param AuditHeader (auditHeader)    
	 * 
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader,
			String method) {
		logger.debug("Entering");
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(),
				auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	
	}
	/**
	 * For Validating AuditDetals object getting from Audit Header, if any
	 * mismatch conditions Fetch the error details from
	 * getAcademicDAO().getErrorDetail with Error ID and language as parameters.
	 * if any error/Warnings then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage,
			String method) {
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetails>());

		Rule rule = (Rule) auditDetail.getModelData();
		Rule tempRule = null;

		if (rule.isWorkflow()) {
			tempRule = getRuleDAO().getRuleByID(rule.getRuleCode(),rule.getRuleModule(),rule.getRuleEvent(),"_Temp");
		}

		Rule befRule = getRuleDAO().getRuleByID(rule.getRuleCode(),rule.getRuleModule(),rule.getRuleEvent(), "");
		Rule oldRule = rule.getBefImage();

		String[] valueParm = new String[3];
		String[] errParm = new String[3];

		valueParm[0] = rule.getRuleCode();
		valueParm[1] = rule.getRuleModule();
		valueParm[2] = rule.getRuleEvent();
		errParm[0] = PennantJavaUtil.getLabel("label_RuleCode") + ": "+ valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_RuleModule") + ": "+ valueParm[1];
		errParm[2] = PennantJavaUtil.getLabel("label_RuleEvent") + ": "+ valueParm[2];
		
		if (rule.isNew()) { // for New record or new record into work flow

			if (!rule.isWorkflow()) {// With out Work flow only new records
				if (befRule != null) { // Record Already Exists in the table
					// then error
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001",errParm, null));
				}
			} else { // with work flow
				if (rule.getRecordType().equals(
						PennantConstants.RECORD_TYPE_NEW)) { // if records type
					// is new
					if (befRule != null || tempRule != null) { //if records 
						// already exists
						// in the main table
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,null));
					}
				} else { // if records not exists in the Main flow table
					if (befRule == null || tempRule != null) {
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,null));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!rule.isWorkflow()) { // With out Work flow for update and
				// delete

				if (befRule == null) { // if records not exists in the main
					// table
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41002",errParm, null));
				} else {
					if (oldRule != null && !oldRule.getLastMntOn().equals(befRule.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41003",errParm, null));
						} else {
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41004",errParm, null));
						}
					}
				}
			} else {

				if (tempRule == null) { // if records not exists in the Work
					// flow table
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005",errParm, null));
				}
				if (tempRule != null && oldRule != null && !oldRule.getLastMntOn().equals(tempRule.getLastMntOn())) {

					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005",errParm, null));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !rule.isWorkflow()) {
			auditDetail.setBefImage(befRule);
		}
		logger.debug("Leaving");
		return auditDetail;
	}
	
	
	/** (non-Javadoc)
	 * @see com.pennant.backend.service.rulefactory.RuleService
	 * 
	 * validationCheck(ruleModule, ruleEvent, ruleCode)
	 **/
	@Override
	public boolean validationCheck( String ruleEvent,
			String ruleCode) {
		return getLimitGroupService().limitLineActiveCheck(ruleEvent,ruleCode);
		
	}

	/**
	 * Fetch Rule Details based on List of Rule codes and module type
	 * 
	 * @param ruleCodes
	 * @param module
	 */
	@Override
	public List<Rule> getRuleDetails(List<String> ruleCodes, String module) {
		return getRuleDAO().getRuleDetails(ruleCodes, module, "");
	}

	/**
	 * Fetch Rule Details based on List of Rule codes , ruleModule and ruleEvent
	 * 
	 * @param ruleCodes
	 * @param module
	 */
	@Override
	public List<Rule> getRuleDetailList(List<String> ruleCodeList, String ruleModule, String ruleEvent) {
		return getRuleDAO().getRuleDetailList(ruleCodeList, ruleModule, ruleEvent);
	}
	
	@Override
	public List<String> getAEAmountCodesList(String event) {
		return getRuleDAO().getAEAmountCodesList(event);
	}

	@Override
	public Rule getRuleById(long ruleID, String type) {
		return getRuleDAO().getRuleByID(ruleID, type);
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public RuleDAO getRuleDAO() {
		return ruleDAO;
	}
	public void setRuleDAO(RuleDAO ruleDAO) {
		this.ruleDAO = ruleDAO;
	}

	public LimitGroupService getLimitGroupService() {
		return limitGroupService;
	}

	public void setLimitGroupService(LimitGroupService limitGroupService) {
		this.limitGroupService = limitGroupService;
	}

}