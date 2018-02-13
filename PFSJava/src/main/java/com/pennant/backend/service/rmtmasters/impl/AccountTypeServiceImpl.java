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
 * FileName    		:  AccountTypeServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-05-2011    														*
 *                                                                  						*
 * Modified Date    :  26-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-05-2011       Pennant	                 0.1                                            * 
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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.rmtmasters.AccountTypeDAO;
import com.pennant.backend.dao.rmtmasters.impl.AccountTypeDAOImpl;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.rmtmasters.AccountType;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.rmtmasters.AccountTypeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.cache.util.AccountingConfigCache;
import com.pennanttech.pennapps.core.model.ErrorDetail;

/**
 * Service implementation for methods that depends on <b>AccountType</b>.<br>
 * 
 */
public class AccountTypeServiceImpl extends GenericService<AccountType> implements AccountTypeService {
	private static Logger logger = Logger.getLogger(AccountTypeDAOImpl.class);

	private AuditHeaderDAO auditHeaderDAO;	
	private AccountTypeDAO accountTypeDAO;

	public AccountTypeServiceImpl() {
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

	public AccountTypeDAO getAccountTypeDAO() {
		return accountTypeDAO;
	}
	public void setAccountTypeDAO(AccountTypeDAO accountTypeDAO) {
		this.accountTypeDAO = accountTypeDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * RMTAccountTypes/RMTAccountTypes_Temp by using AccountTypeDAO's save
	 * method b) Update the Record in the table. based on the module workFlow
	 * Configuration. by using AccountTypeDAO's update method 3) Audit the
	 * record in to AuditHeader and AdtRMTAccountTypes by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader,"saveOrUpdate");
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}
		String tableType="";
		AccountType accountType = (AccountType) auditHeader.getAuditDetail().getModelData();

		if (accountType.isWorkflow()) {
			tableType="_Temp";
		}

		if (accountType.isNew()) {
			accountType.setAcType(getAccountTypeDAO().save(accountType,tableType));
			auditHeader.getAuditDetail().setModelData(accountType);
			auditHeader.setAuditReference(accountType.getAcType());
		}else{
			getAccountTypeDAO().update(accountType,tableType);
			if (StringUtils.isEmpty(tableType)) {
				AccountingConfigCache.clearAccountTypeCache(accountType.getAcType());
			}
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table RMTAccountTypes by using AccountTypeDAO's delete method with type
	 * as Blank 3) Audit the record in to AuditHeader and AdtRMTAccountTypes by
	 * using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader,"delete");
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}

		AccountType accountType = (AccountType) auditHeader.getAuditDetail().getModelData();
		getAccountTypeDAO().delete(accountType,"");
		AccountingConfigCache.clearAccountTypeCache(accountType.getAcType());
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getAccountTypeById fetch the details by using AccountTypeDAO's
	 * getAccountTypeById method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return AccountType
	 */
	@Override
	public AccountType getAccountTypeById(String id) {
		return getAccountTypeDAO().getAccountTypeById(id,"_View");
	}

	/**
	 * getApprovedAccountTypeById fetch the details by using AccountTypeDAO's
	 * getAccountTypeById method . with parameter id and type as blank. it
	 * fetches the approved records from the RMTAccountTypes.
	 * 
	 * @param id
	 *            (String)
	 * @return AccountType
	 */
	@Override
	public AccountType getApprovedAccountTypeById(String id) {
		return getAccountTypeDAO().getAccountTypeById(id,"_AView");
	}

	/**
	 * getApprovedAccountTypeById fetch the detail.
	 * It fetches the approved records from RMTAccountTypes.
	 * 
	 * @param String acType
	 * @return AccountType
	 */
	@Override
	public AccountType getApprovedAccountType(String acType) {
		return AccountingConfigCache.getAccountType(acType);
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getAccountTypeDAO().delete with parameters accountType,"" b) NEW
	 * Add new record in to main table by using getAccountTypeDAO().save with
	 * parameters accountType,"" c) EDIT Update record in the main table by
	 * using getAccountTypeDAO().update with parameters accountType,"" 3) Delete
	 * the record from the workFlow table by using getAccountTypeDAO().delete
	 * with parameters accountType,"_Temp" 4) Audit the record in to AuditHeader
	 * and AdtRMTAccountTypes by using auditHeaderDAO.addAudit(auditHeader) for
	 * Work flow 5) Audit the record in to AuditHeader and AdtRMTAccountTypes by
	 * using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
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

		AccountType accountType = new AccountType();
		BeanUtils.copyProperties((AccountType) auditHeader.getAuditDetail().getModelData(), accountType);

		if (accountType.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType=PennantConstants.TRAN_DEL;

			getAccountTypeDAO().delete(accountType,"");
			AccountingConfigCache.clearAccountTypeCache(accountType.getAcType());
		} else {
			accountType.setRoleCode("");
			accountType.setNextRoleCode("");
			accountType.setTaskId("");
			accountType.setNextTaskId("");
			accountType.setWorkflowId(0);

			if (accountType.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType=PennantConstants.TRAN_ADD;
				accountType.setRecordType("");
				getAccountTypeDAO().save(accountType,"");
			} else {
				tranType=PennantConstants.TRAN_UPD;
				accountType.setRecordType("");
				getAccountTypeDAO().update(accountType,"");
				AccountingConfigCache.clearAccountTypeCache(accountType.getAcType());
			}
		}

		getAccountTypeDAO().delete(accountType,"_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(accountType);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getAccountTypeDAO().delete with parameters
	 * accountType,"_Temp" 3) Audit the record in to AuditHeader and
	 * AdtRMTAccountTypes by using auditHeaderDAO.addAudit(auditHeader) for Work
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

		AccountType accountType= (AccountType) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAccountTypeDAO().delete(accountType,"_Temp");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
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
	 * getAccountTypeDAO().getErrorDetail with Error ID and language as parameters.
	 * if any error/Warnings then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail,String usrLanguage,String method) {
		logger.debug("Entering");

		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		AccountType accountType = (AccountType) auditDetail.getModelData();

		AccountType tempAccountType = null;
		if (accountType.isWorkflow()) {
			tempAccountType = getAccountTypeDAO().getAccountTypeById(
					accountType.getId(), "_Temp");
		}
		AccountType befAccountType = getAccountTypeDAO().getAccountTypeById(
				accountType.getId(), "");

		AccountType oldAccountType = accountType.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];

		valueParm[0] = accountType.getId();
		errParm[0] = PennantJavaUtil.getLabel("label_AcType") + ":"+ valueParm[0];

		if (accountType.isNew()) { // for New record or new record into workFlow

			if (!accountType.isWorkflow()) {// With out Work flow only new records
				if (befAccountType != null) { // Record Already Exists in the table then error
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41001",errParm,null));
				}
			} else { // with work flow
				if (accountType.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
					if (befAccountType != null || tempAccountType != null) { // if records already exists in the main
																				// table
						auditDetail
								.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, null));
					}
				} else { // if records not exists in the Main flow table
					if (befAccountType == null || tempAccountType != null) {
						auditDetail
								.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
					}
				}
			}	

		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!accountType.isWorkflow()) { // With out Work flow for update and delete

				if (befAccountType == null) { // if records not exists in the main table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41002",errParm,null));
				}else{
					if (oldAccountType != null
							&& !oldAccountType.getLastMntOn().equals(
									befAccountType.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41003",errParm,null));
						} else {
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41004",errParm,null));
						}
					}
				}
			} else {
				if (tempAccountType == null) { // if records not exists in the WorkFlow table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41005",errParm,null));
				}
				if (tempAccountType != null && oldAccountType != null
						&& !oldAccountType.getLastMntOn().equals(
								tempAccountType.getLastMntOn())) {
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41005",errParm,null));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !accountType.isWorkflow()) {
			auditDetail.setBefImage(befAccountType);
		}
		logger.debug("Leaving");
		return auditDetail;
	}
	@Override
    public List<ValueLabel> getAccountTypeDesc(List<String> acTypeList) {
	    return getAccountTypeDAO().getAccountTypeDesc(acTypeList);
    }


}