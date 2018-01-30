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
 * FileName    		:  AccountsServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  02-01-2012    														*
 *                                                                  						*
 * Modified Date    :  02-01-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 02-01-2012       Pennant	                 0.1                                            * 
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

package com.pennant.backend.service.accounts.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.accounts.AccountsDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.accounts.Accounts;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.accounts.AccountsService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>Accounts</b>.<br>
 * 
 */
public class AccountsServiceImpl extends GenericService<Accounts> implements AccountsService {
	private static final Logger logger = Logger.getLogger(AccountsServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;

	private AccountsDAO accountsDAO;

	public AccountsServiceImpl() {
		super();
	}
	
	/**
	 * @return the auditHeaderDAO
	 */
	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	/**
	 * @param auditHeaderDAO the auditHeaderDAO to set
	 */
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}
	/**
	 * @return the accountsDAO
	 */
	public AccountsDAO getAccountsDAO() {
		return accountsDAO;
	}
	/**
	 * @param accountsDAO the accountsDAO to set
	 */
	public void setAccountsDAO(AccountsDAO accountsDAO) {
		this.accountsDAO = accountsDAO;
	}

	/**
	 * @return the accounts
	 */
	@Override
	public Accounts getAccounts() {
		return getAccountsDAO().getAccounts();
	}
	/**
	 * @return the accounts for New Record
	 */
	@Override
	public Accounts getNewAccounts() {
		return getAccountsDAO().getNewAccounts();
	}


	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table Accounts/Accounts_Temp 
	 * 			by using AccountsDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using AccountsDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtAccounts by using auditHeaderDAO.addAudit(auditHeader)
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */

	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug("Entering");	
		auditHeader = businessValidation(auditHeader,"saveOrUpdate");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		String tableType="";
		Accounts accounts = (Accounts) auditHeader.getAuditDetail().getModelData();

		if (accounts.isWorkflow()) {
			tableType="_Temp";
		}

		if (accounts.isNew()) {
			getAccountsDAO().save(accounts,tableType);
		}else{
			getAccountsDAO().update(accounts,tableType);
		}

		String[] fields = PennantJavaUtil.getFieldDetails(new Accounts(),"acAvailableBal");
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1,fields[0],fields[1], accounts.getBefImage(), accounts));
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	delete Record for the DB table Accounts by using AccountsDAO's delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtAccounts by using auditHeaderDAO.addAudit(auditHeader)    
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader,"delete");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		Accounts accounts = (Accounts) auditHeader.getAuditDetail().getModelData();
		getAccountsDAO().delete(accounts,"");

		String[] fields = PennantJavaUtil.getFieldDetails(new Accounts(),"acAvailableBal");
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1,fields[0],fields[1], accounts.getBefImage(), accounts));
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getAcountsById fetch the details by using AccountsDAO's getAcountsById method.
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return Accounts
	 */

	@Override
	public Accounts getAccountsById(String id) {
		return getAccountsDAO().getAccountsById(id,"_View");
	}
	
	@Override
	public List<Accounts> getAccountsByAcPurpose(String acPurpose) {
		return getAccountsDAO().getAccountsByAcPurpose(acPurpose, "_AView");
	}

	/**
	 * getApprovedAcountsById fetch the details by using AccountsDAO's getAcountsById method .
	 * with parameter id and type as blank. it fetches the approved records from the Accounts.
	 * @param id (String)
	 * @return Accounts
	 */

	public Accounts getApprovedAccountsById(String id) {
		return getAccountsDAO().getAccountsById(id,"_AView");
	}

	/**
	 * doApprove method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	based on the Record type do following actions
	 * 		a)  DELETE	Delete the record from the main table by using getAcountsDAO().delete with parameters accounts,""
	 * 		b)  NEW		Add new record in to main table by using getAcountsDAO().save with parameters accounts,""
	 * 		c)  EDIT	Update record in the main table by using getAcountsDAO().update with parameters accounts,""
	 * 3)	Delete the record from the workFlow table by using getAcountsDAO().delete with parameters accounts,"_Temp"
	 * 4)	Audit the record in to AuditHeader and AdtAccounts by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 5)  	Audit the record in to AuditHeader and AdtAccounts by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */

	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");
		String tranType="";
		auditHeader = businessValidation(auditHeader,"doApprove");
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		Accounts accounts = new Accounts();
		BeanUtils.copyProperties((Accounts) auditHeader.getAuditDetail().getModelData(), accounts);

		if (accounts.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType=PennantConstants.TRAN_DEL;

			getAccountsDAO().delete(accounts,"");

		} else {
			accounts.setRoleCode("");
			accounts.setNextRoleCode("");
			accounts.setTaskId("");
			accounts.setNextTaskId("");
			accounts.setWorkflowId(0);

			if(accounts.isAcClosed()){
				accounts.setAcCloseDate(new Date(System.currentTimeMillis()));
			}
			if (accounts.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType=PennantConstants.TRAN_ADD;
				accounts.setRecordType("");
				accounts.setAcOpenDate(new Date(System.currentTimeMillis()));
				getAccountsDAO().save(accounts,"");
		
			} else {
				tranType=PennantConstants.TRAN_UPD;
				accounts.setRecordType("");
				getAccountsDAO().update(accounts,"");
			}
		}

		getAccountsDAO().delete(accounts,"_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		String[] fields = PennantJavaUtil.getFieldDetails(new Accounts(),"acAvailableBal");
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1,fields[0],fields[1], accounts.getBefImage(), accounts));
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(accounts);
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1,fields[0],fields[1], accounts.getBefImage(), accounts));
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");		

		return auditHeader;
	}

	/**
	 * doReject method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Delete the record from the workFlow table by using getAcountsDAO().delete with parameters accounts,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtAccounts by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */

	public AuditHeader  doReject(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader,"doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		Accounts accounts = (Accounts) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAccountsDAO().delete(accounts,"_Temp");

		String[] fields = PennantJavaUtil.getFieldDetails(new Accounts(),"acAvailableBal");
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1,fields[0],fields[1], accounts.getBefImage(), accounts));
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
	 * 5)	for any mismatch conditions Fetch the error details from getAcountsDAO().getErrorDetail with Error ID and language as parameters.
	 * 6)	if any error/Warnings  then assign the to auditHeader 
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */


	private AuditHeader businessValidation(AuditHeader auditHeader, String method){
		logger.debug("Entering");
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader=nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	private AuditDetail validation(AuditDetail auditDetail,String usrLanguage,String method){
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());			
		Accounts accounts= (Accounts) auditDetail.getModelData();

		Accounts tempAcounts= null;
		if (accounts.isWorkflow()){
			tempAcounts = getAccountsDAO().getAccountsById(accounts.getAccountId(), "_Temp");
		}
		Accounts befAcounts= getAccountsDAO().getAccountsById(accounts.getAccountId(), "");

		Accounts oldAcounts= accounts.getBefImage();


		String[] errParm= new String[1];
		String[] valueParm= new String[1];
		valueParm[0]=accounts.getAccountId();
		errParm[0]=PennantJavaUtil.getLabel("label_AccountId")+":"+valueParm[0];

		if (accounts.isNew()){ // for New record or new record into work flow

			if (!accounts.isWorkflow()){// With out Work flow only new records  
				if (befAcounts !=null){	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
				}	
			}else{ // with work flow
				if (accounts.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befAcounts !=null || tempAcounts!=null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
					}
				}else{ // if records not exists in the Main flow table
					if (befAcounts ==null || tempAcounts!=null ){
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!accounts.isWorkflow()){	// With out Work flow for update and delete

				if (befAcounts ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm,valueParm), usrLanguage));
				}else{
					if (oldAcounts!=null && !oldAcounts.getLastMntOn().equals(befAcounts.getLastMntOn())){
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm,valueParm), usrLanguage));
						}else{
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{

				if (tempAcounts==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}

				if (tempAcounts!=null  && oldAcounts!=null && !oldAcounts.getLastMntOn().equals(tempAcounts.getLastMntOn())){ 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if("doApprove".equals(StringUtils.trimToEmpty(method)) || !accounts.isWorkflow()){
			accounts.setBefImage(befAcounts);	
		}

		return auditDetail;
	}

}