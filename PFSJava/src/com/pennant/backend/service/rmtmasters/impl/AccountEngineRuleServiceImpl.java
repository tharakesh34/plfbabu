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
 * FileName    		:  AccountEngineRuleServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-06-2011    														*
 *                                                                  						*
 * Modified Date    :  27-06-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 27-06-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.service.rmtmasters.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.rmtmasters.AccountEngineRuleDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.rmtmasters.AccountEngineRule;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.rmtmasters.AccountEngineRuleService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>AccountEngineRule</b>.<br>
 * 
 */
public class AccountEngineRuleServiceImpl extends GenericService<AccountEngineRule> implements AccountEngineRuleService {

	private static Logger logger = Logger.getLogger(AccountEngineRuleServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private AccountEngineRuleDAO accountEngineRuleDAO;

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public AccountEngineRuleDAO getAccountEngineRuleDAO() {
		return accountEngineRuleDAO;
	}
	public void setAccountEngineRuleDAO(
			AccountEngineRuleDAO accountEngineRuleDAO) {
		this.accountEngineRuleDAO = accountEngineRuleDAO;
	}

	@Override
	public AccountEngineRule getAccountEngineRule() {
		return getAccountEngineRuleDAO().getAccountEngineRule();
	}
	@Override
	public AccountEngineRule getNewAccountEngineRule() {
		return getAccountEngineRuleDAO().getNewAccountEngineRule();
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * RMTAERules/RMTAERules_Temp by using AccountEngineRuleDAO's save method b)
	 * Update the Record in the table. based on the module workFlow
	 * Configuration. by using AccountEngineRuleDAO's update method 3) Audit the
	 * record in to AuditHeader and AdtRMTAERules by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}

		String tableType = "";
		AccountEngineRule accountEngineRule = (AccountEngineRule) auditHeader.getAuditDetail().getModelData();

		if (accountEngineRule.isWorkflow()) {
			tableType = "_TEMP";
		}
		
		if (accountEngineRule.isNew()) {
			accountEngineRule.setaERuleId(getAccountEngineRuleDAO().save(accountEngineRule, tableType));
			auditHeader.getAuditDetail().setModelData(accountEngineRule);
			auditHeader.setAuditReference(String.valueOf(accountEngineRule.getaERuleId()));
		} else {
			getAccountEngineRuleDAO().update(accountEngineRule, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table RMTAERules by using AccountEngineRuleDAO's delete method with type
	 * as Blank 3) Audit the record in to AuditHeader and AdtRMTAERules by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}
		
		AccountEngineRule accountEngineRule = (AccountEngineRule) auditHeader.getAuditDetail().getModelData();
		getAccountEngineRuleDAO().delete(accountEngineRule, "");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getAccountEngineRuleById fetch the details by using
	 * AccountEngineRuleDAO's getAccountEngineRuleById method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return AccountEngineRule
	 */
	@Override
	public AccountEngineRule getAccountEngineRuleById(long id) {
		return getAccountEngineRuleDAO().getAccountEngineRuleById(id, "_View");
	}

	/**
	 * getApprovedAccountEngineRuleById fetch the details by using
	 * AccountEngineRuleDAO's getAccountEngineRuleById method . with parameter
	 * id and type as blank. it fetches the approved records from the
	 * RMTAERules.
	 * 
	 * @param id
	 *            (String)
	 * @return AccountEngineRule
	 */
	public AccountEngineRule getApprovedAccountEngineRuleById(long id) {
		return getAccountEngineRuleDAO().getAccountEngineRuleById(id, "_AView");
	}

	/**
	 * This method refresh the Record.
	 * 
	 * @param AccountEngineRule
	 *            (accountEngineRule)
	 * @return accountEngineRule
	 */
	@Override
	public AccountEngineRule refresh(AccountEngineRule accountEngineRule) {
		logger.debug("Entering");
		getAccountEngineRuleDAO().refresh(accountEngineRule);
		getAccountEngineRuleDAO().initialize(accountEngineRule);
		logger.debug("Leaving");
		return accountEngineRule;
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getAccountEngineRuleDAO().delete with parameters
	 * accountEngineRule,"" b) NEW Add new record in to main table by using
	 * getAccountEngineRuleDAO().save with parameters accountEngineRule,"" c)
	 * EDIT Update record in the main table by using
	 * getAccountEngineRuleDAO().update with parameters accountEngineRule,"" 3)
	 * Delete the record from the workFlow table by using
	 * getAccountEngineRuleDAO().delete with parameters
	 * accountEngineRule,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtRMTAERules by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 5) Audit the record in to AuditHeader and AdtRMTAERules by using
	 * auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");

		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}

		AccountEngineRule accountEngineRule = new AccountEngineRule();
		BeanUtils.copyProperties(
				(AccountEngineRule) auditHeader.getAuditDetail().getModelData(),accountEngineRule);

		if (accountEngineRule.getRecordType().equals(
				PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			getAccountEngineRuleDAO().delete(accountEngineRule, "");

		} else {
			accountEngineRule.setRoleCode("");
			accountEngineRule.setNextRoleCode("");
			accountEngineRule.setTaskId("");
			accountEngineRule.setNextTaskId("");
			accountEngineRule.setWorkflowId(0);

			if (accountEngineRule.getRecordType().equals(
					PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				accountEngineRule.setRecordType("");
				getAccountEngineRuleDAO().save(accountEngineRule, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				accountEngineRule.setRecordType("");
				getAccountEngineRuleDAO().update(accountEngineRule, "");
			}
		}

		getAccountEngineRuleDAO().delete(accountEngineRule, "_TEMP");
		
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(accountEngineRule);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getAccountEngineRuleDAO().delete with
	 * parameters accountEngineRule,"_Temp" 3) Audit the record in to
	 * AuditHeader and AdtRMTAERules by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}

		AccountEngineRule accountEngineRule = (AccountEngineRule) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAccountEngineRuleDAO().delete(accountEngineRule, "_TEMP");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}
	
	/**
	 * getAccountEngineRuleById fetch the details by using
	 * AccountEngineRuleDAO's getAccountEngineRuleBySysDft method.
	 * 
	 * @param event (String)
	 * @param type (String) ""/_Temp/_View
	 * @return boolean
	 */
	@Override
	public AccountEngineRule getAccountEngineRuleBySysDft(AccountEngineRule accountEngineRule) {
		
		AccountEngineRule aERule = getAccountEngineRuleDAO().getAccountEngineRuleBySysDflt(accountEngineRule,"",true);
		if(aERule == null){
			aERule = getAccountEngineRuleDAO().getAccountEngineRuleBySysDflt(accountEngineRule,"_Temp",true);
		}
		return aERule;
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from
	 * the auditHeader. 2) fetch the details from the tables 3) Validate the
	 * Record based on the record details. 4) Validate for any business
	 * validation. 
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader,
			String method) {
		logger.debug("Entering");
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(),
				auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader=nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any
	 * mismatch conditions Fetch the error details from
	 * getAccountEngineRuleDAO().getErrorDetail with Error ID and language as parameters.
	 * if any error/Warnings then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail,String usrLanguage,String method) {
		logger.debug("Entering");

		AccountEngineRule accountEngineRule = (AccountEngineRule) auditDetail.getModelData();
		
		AccountEngineRule tempAccountEngineRule = null;
		if (accountEngineRule.isWorkflow()) {
			tempAccountEngineRule = getAccountEngineRuleDAO().
							getAccountEngineRuleById(accountEngineRule.getId(),"_Temp");
		}
		AccountEngineRule befAccountEngineRule = getAccountEngineRuleDAO().getAccountEngineRuleById(accountEngineRule.getId(), "");

		AccountEngineRule oldAccountEngineRule = accountEngineRule.getBefImage();

		String[] valueParm = new String[2];
		String[] errParm= new String[2];

		valueParm[0] = accountEngineRule.getAEEvent();
		valueParm[1] = accountEngineRule.getAERule();

		errParm[0] = PennantJavaUtil.getLabel("label_AEEvent") + ":"+ valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_AERule") + ":"+valueParm[1];

		if (accountEngineRule.isNew()) { // for New record or new record into workFlow

			if (!accountEngineRule.isWorkflow()) {// With out Work flow only new records
				if (befAccountEngineRule != null) { // Record Already Exists in the table then error
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41001",errParm,null));
				}
			} else { // with work flow
				if (accountEngineRule.getRecordType().equals(
						PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befAccountEngineRule != null || tempAccountEngineRule != null) { // if records already exists in the main table
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41001",errParm,null));
					}
				}
				 else { // if records not exists in the Main flow table
						if (befAccountEngineRule == null || tempAccountEngineRule != null) {
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",errParm,null));
						}
					}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!accountEngineRule.isWorkflow()) { // With out Work flow for update and delete

				if (befAccountEngineRule == null) { // if records not exists in the main table
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41002",errParm,null));
				}else{

					if (oldAccountEngineRule != null
							&& !oldAccountEngineRule.getLastMntOn().equals(
									befAccountEngineRule.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41003",errParm,null));
						} else {
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41004",errParm,null));
						}
					}
				}
			} else {

				if (tempAccountEngineRule == null) { // if records not exists in
														// the WorkFlow table
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",errParm,null));
				}

				if (tempAccountEngineRule != null && oldAccountEngineRule != null
						&& !oldAccountEngineRule.getLastMntOn().equals(
								tempAccountEngineRule.getLastMntOn())) {
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",errParm,null));
				}

			}
		}
		
		if(StringUtils.trimToEmpty(method).equals("saveOrUpdate")){
			AccountEngineRule aERule = getAccountEngineRuleBySysDft(accountEngineRule);
			if (accountEngineRule.isAEIsSysDefault() && (aERule != null)) {
				
				errParm[0] = PennantJavaUtil.getLabel("label_AEEvent") + ":"+ valueParm[0];
				
				auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41010",errParm,null));
			}else if(accountEngineRule.isAEIsSysDefault() && 
					accountEngineRule.getRecordType().equals(PennantConstants.RCD_DEL)){
				
				auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41013",errParm,null));
			}
		}
			
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
		
		if (StringUtils.trimToEmpty(method).equals("doApprove") || !accountEngineRule.isWorkflow()) {
			auditDetail.setBefImage(befAccountEngineRule);
		}
		logger.debug("Leaving");
		return auditDetail;
	}

}