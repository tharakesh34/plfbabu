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
 * 03-06-2011       Pennant	                 0.1                                         	* 
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
import com.pennant.backend.dao.rulefactory.impl.LimitRuleDAO;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.rulefactory.BMTRBFldDetails;
import com.pennant.backend.model.rulefactory.LimitFilterQuery;
import com.pennant.backend.model.rulefactory.LimitFldCriterias;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>Rule</b>.<br>
 */
public class LimitRuleServiceImpl extends GenericService<LimitFilterQuery> implements LimitRuleService {

	private static Logger logger=Logger.getLogger(LimitRuleServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private LimitRuleDAO limitRuleDAO;

	public LimitRuleServiceImpl() {
		super();
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

	@Override
	public LimitFilterQuery getLimitRule() {
		return getLimitRuleDAO().getLimitRule();
	}

	@Override
	public LimitFilterQuery getNewLimitRule() {
		return getLimitRuleDAO().getNewLimitRule();
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
		LimitFilterQuery rule = (LimitFilterQuery) auditHeader.getAuditDetail().getModelData();

		if (rule.isWorkflow()) {
			tableType = "_Temp";
		}
		if (rule.isNew()) {
			getLimitRuleDAO().save(rule,tableType);
			auditHeader.getAuditDetail().setModelData(rule);
			auditHeader.setAuditReference(rule.getQueryCode());
		} else {
			getLimitRuleDAO().update(rule,tableType);
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

		LimitFilterQuery rule = (LimitFilterQuery) auditHeader.getAuditDetail().getModelData();
		getLimitRuleDAO().delete(rule, "");
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
	public LimitFilterQuery getLimitRuleByID(String id,String module,String event) {
		return getLimitRuleDAO().getLimitRuleByID(id,module,event, "_View");
	}
	
	/**
	 * Method for Fetch SQL Rule based on Parameters
	 */
	//@Override
	/*public String getAmountRule(String id,String module,String event) {
		return getLimitRuleDAO().getAmountRule(id,module,event);
	}*/

	/**
	 * getApprovedRuleById fetch the details by using RuleDAO's getRuleById method .
	 * with parameter id and type as blank. it fetches the approved records from the Rules.
	 * 
	 * @param id (String)
	 * 
	 * @return Rule
	 */
	/*public Rule getApprovedRuleById(String id,String module,String event) {
		return getLimitRuleDAO().getRuleByID(id,module,event, "_AView");
	}*/
	
	/**
	 * This method return the columns list of the table
	 * 
	 * @return List
	 */
	//@Override
	//public List<BMTRBFldDetails> getFieldList(String module,String event) {
	//	return getLimitRuleDAO().getFieldList(module, event);
	//}

	/**
	 * This method return the Operator list 
	 * 
	 * @return List
	 */
	/*@Override
	public List<BMTRBFldCriterias> getOperatorsList() {
		return getLimitRuleDAO().getOperatorsList();
	}

	*//**
	 * Method for getting List of Rule Modules
	 *//*
	@Override
	public List<RuleModule> getRuleModules(String module) {
		return getLimitRuleDAO().getRuleModules(module);
	}
	
	@Override
    public List<Rule> getRulesByGroupId(long groupId,String ruleModule, String ruleEvent) {
	    return getLimitRuleDAO().getRulesByGroupId(groupId,ruleModule, ruleEvent,"_AView");
    }	

	@Override
    public List<NFScoreRuleDetail> getNFRulesByGroupId(long groupId) {
	    return getLimitRuleDAO().getNFRulesByGroupId(groupId , "");
    }*/

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getLimitRuleDAO().delete with parameters dedupParm,"" b) NEW Add
	 * new record in to main table by using getLimitRuleDAO().save with
	 * parameters dedupParm,"" c) EDIT Update record in the main table by using
	 * getLimitRuleDAO().update with parameters dedupParm,"" 3) Delete the
	 * record from the workFlow table by using getLimitRuleDAO().delete with
	 * parameters dedupParm,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtDedupParams by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow 5) Audit the record in to AuditHeader and AdtDedupParams by using
	 * auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");

		String tranType="";
		auditHeader = businessValidation(auditHeader,"doApprove");
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}

		LimitFilterQuery dedupParm = new LimitFilterQuery();
		BeanUtils.copyProperties((LimitFilterQuery) auditHeader.getAuditDetail()
				.getModelData(), dedupParm);

		if (dedupParm.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType=PennantConstants.TRAN_DEL;

			getLimitRuleDAO().delete(dedupParm,"");

		} else {
			dedupParm.setRoleCode("");
			dedupParm.setNextRoleCode("");
			dedupParm.setTaskId("");
			dedupParm.setNextTaskId("");
			dedupParm.setWorkflowId(0);

			if (dedupParm.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {	
				tranType=PennantConstants.TRAN_ADD;
				dedupParm.setRecordType("");
				getLimitRuleDAO().save(dedupParm,"");
			} else {
				tranType=PennantConstants.TRAN_UPD;
				dedupParm.setRecordType("");
				getLimitRuleDAO().update(dedupParm,"");
			}
		}

		getLimitRuleDAO().delete(dedupParm,"_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(dedupParm);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");		

		return auditHeader;
	}
	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getDedupParmDAO().delete with parameters
	 * dedupParm,"_Temp" 3) Audit the record in to AuditHeader and
	 * AdtDedupParams by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader  doReject(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader,"doReject");
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}

		LimitFilterQuery dedupParm = (LimitFilterQuery) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getLimitRuleDAO().delete(dedupParm,"_Temp");

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
	private AuditDetail validation(AuditDetail auditDetail,String usrLanguage,String method){
		logger.debug("Entering");

		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		LimitFilterQuery dedupParm = (LimitFilterQuery) auditDetail.getModelData();

		LimitFilterQuery tempDedupParm = null;
		if (dedupParm.isWorkflow()) {
			tempDedupParm = getLimitRuleDAO().getLimitRuleByID(dedupParm.getQueryCode(),dedupParm.getQueryModule(),dedupParm.getQuerySubCode(), "_Temp");
		}
		LimitFilterQuery befDedupParm = getLimitRuleDAO().getLimitRuleByID(
				dedupParm.getQueryCode(),dedupParm.getQueryModule(),dedupParm.getQuerySubCode(), "");

		LimitFilterQuery oldDedupParm = dedupParm.getBefImage();

		String[] valueParm = new String[1];
		String[] errParm = new String[1];

		valueParm[0] = dedupParm.getQueryCode();
		errParm[0] = PennantJavaUtil.getLabel("label_QueryCode") + ":" + valueParm[0];

		if (dedupParm.isNew()) { // for New record or new record into work flow

			if (!dedupParm.isWorkflow()) {// With out Work flow only new records
				if (befDedupParm != null) { // Record Already Exists in the
					// table then error
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41001",errParm,null));
				}
			} else { // with work flow
				if (dedupParm.getRecordType().equals(
						PennantConstants.RECORD_TYPE_NEW)) { // if records type
					// is new
					if (befDedupParm != null || tempDedupParm != null) { // if records already exists in
						// the main table
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41001",errParm,null));
					}
				} else { // if records not exists in the Main flow table
					if (befDedupParm == null || tempDedupParm != null) {
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41005",errParm,null));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!dedupParm.isWorkflow()) { // With out Work flow for update and
				// delete

				if (befDedupParm == null) { // if records not exists in the main
					// table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41002",errParm,null));
				} else {
					if (oldDedupParm != null
							&& !oldDedupParm.getLastMntOn().equals(
									befDedupParm.getLastMntOn())) {
						if (StringUtils.trimToEmpty(
								auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41003",errParm,null));
						} else {
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41004",errParm,null));
						}
					}
				}
			} else {

				if (tempDedupParm == null) { // if records not exists in the Work flow table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41005",errParm,null));
				}

				if (tempDedupParm != null && oldDedupParm != null
						&& !oldDedupParm.getLastMntOn().equals(
								tempDedupParm.getLastMntOn())) {
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41005",errParm,null));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !dedupParm.isWorkflow()) {
			dedupParm.setBefImage(befDedupParm);
		}

		return auditDetail;
	}

	@Override
	public List<LimitFldCriterias> getOperatorsList() {
		return getLimitRuleDAO().getOperatorsList();
	}
	
	/**
	 * This method return the columns list of the table
	 * 
	 * @return List
	 */
	@Override
	public List<BMTRBFldDetails> getFieldList(String module,String event) {
		return getLimitRuleDAO().getFieldList(module, event);
	}

	public LimitRuleDAO getLimitRuleDAO() {
		return limitRuleDAO;
	}

	public void setLimitRuleDAO(LimitRuleDAO limitRuleDAO) {
		this.limitRuleDAO = limitRuleDAO;
	}
}