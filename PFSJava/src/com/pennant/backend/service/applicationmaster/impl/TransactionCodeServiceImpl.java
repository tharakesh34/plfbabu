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
 * FileName    		:  TransactionCodeServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  10-11-2011    														*
 *                                                                  						*
 * Modified Date    :  10-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 10-11-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.service.applicationmaster.impl;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.applicationmaster.TransactionCodeDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.applicationmaster.TransactionCode;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.TransactionCodeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>TransactionCode</b>.<br>
 * 
 */
public class TransactionCodeServiceImpl extends GenericService<TransactionCode>
				implements TransactionCodeService {
	
	private final static Logger logger = Logger.getLogger(TransactionCodeServiceImpl.class);
	
	private AuditHeaderDAO auditHeaderDAO;
	private TransactionCodeDAO transactionCodeDAO;

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}
	
	public TransactionCodeDAO getTransactionCodeDAO() {
		return transactionCodeDAO;
	}
	public void setTransactionCodeDAO(TransactionCodeDAO transactionCodeDAO) {
		this.transactionCodeDAO = transactionCodeDAO;
	}

	@Override
	public TransactionCode getTransactionCode() {
		return getTransactionCodeDAO().getTransactionCode();
	}
	@Override
	public TransactionCode getNewTransactionCode() {
		return getTransactionCodeDAO().getNewTransactionCode();
	}
	
	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * BMTTransactionCode/BMTTransactionCode_Temp by using TransactionCodeDAO's
	 * save method b) Update the Record in the table. based on the module
	 * workFlow Configuration. by using TransactionCodeDAO's update method 3)
	 * Audit the record in to AuditHeader and AdtBMTTransactionCode by using
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
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		String tableType="";
		TransactionCode transactionCode = (TransactionCode) auditHeader.
														getAuditDetail().getModelData();
		
		if (transactionCode.isWorkflow()) {
			tableType="_TEMP";
		}

		if (transactionCode.isNew()) {
			transactionCode.setId(getTransactionCodeDAO().save(transactionCode,tableType));
			auditHeader.getAuditDetail().setModelData(transactionCode);
			auditHeader.setAuditReference(transactionCode.getTranCode());
		}else{
			getTransactionCodeDAO().update(transactionCode,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table BMTTransactionCode by using TransactionCodeDAO's delete method with
	 * type as Blank 3) Audit the record in to AuditHeader and
	 * AdtBMTTransactionCode by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
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
		
		TransactionCode transactionCode = (TransactionCode) auditHeader.
														getAuditDetail().getModelData();
		getTransactionCodeDAO().delete(transactionCode,"");
		
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getTransactionCodeById fetch the details by using TransactionCodeDAO's
	 * getTransactionCodeById method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return TransactionCode
	 */
	@Override
	public TransactionCode getTransactionCodeById(String id) {
		return getTransactionCodeDAO().getTransactionCodeById(id,"_View");
	}
	
	/**
	 * getApprovedTransactionCodeById fetch the details by using
	 * TransactionCodeDAO's getTransactionCodeById method . with parameter id
	 * and type as blank. it fetches the approved records from the
	 * BMTTransactionCode.
	 * 
	 * @param id
	 *            (String)
	 * @return TransactionCode
	 */
	public TransactionCode getApprovedTransactionCodeById(String id) {
		return getTransactionCodeDAO().getTransactionCodeById(id,"_AView");
	}
		
	/**
	 * This method refresh the Record.
	 * @param TransactionCode (transactionCode)
 	 * @return transactionCode
	 */
	@Override
	public TransactionCode refresh(TransactionCode transactionCode) {
		logger.debug("Entering");
		getTransactionCodeDAO().refresh(transactionCode);
		getTransactionCodeDAO().initialize(transactionCode);
		logger.debug("Leaving");
		return transactionCode;
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getTransactionCodeDAO().delete with parameters transactionCode,""
	 * b) NEW Add new record in to main table by using
	 * getTransactionCodeDAO().save with parameters transactionCode,"" c) EDIT
	 * Update record in the main table by using getTransactionCodeDAO().update
	 * with parameters transactionCode,"" 3) Delete the record from the workFlow
	 * table by using getTransactionCodeDAO().delete with parameters
	 * transactionCode,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtBMTTransactionCode by using auditHeaderDAO.addAudit(auditHeader) for
	 * Work flow 5) Audit the record in to AuditHeader and AdtBMTTransactionCode
	 * by using auditHeaderDAO.addAudit(auditHeader) based on the transaction
	 * Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");

		String tranType="";
		auditHeader = businessValidation(auditHeader,"doApprove");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		TransactionCode transactionCode = new TransactionCode();
		BeanUtils.copyProperties((TransactionCode) auditHeader.getAuditDetail().
											getModelData(), transactionCode);

		if (transactionCode.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType=PennantConstants.TRAN_DEL;

			getTransactionCodeDAO().delete(transactionCode,"");

		} else {
			transactionCode.setRoleCode("");
			transactionCode.setNextRoleCode("");
			transactionCode.setTaskId("");
			transactionCode.setNextTaskId("");
			transactionCode.setWorkflowId(0);

			if (transactionCode.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) 
			{	
				tranType=PennantConstants.TRAN_ADD;
				transactionCode.setRecordType("");
				getTransactionCodeDAO().save(transactionCode,"");
			} else {
				tranType=PennantConstants.TRAN_UPD;
				transactionCode.setRecordType("");
				getTransactionCodeDAO().update(transactionCode,"");
			}
		}

		getTransactionCodeDAO().delete(transactionCode,"_TEMP");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(transactionCode);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");		

		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getTransactionCodeDAO().delete with
	 * parameters transactionCode,"_Temp" 3) Audit the record in to AuditHeader
	 * and AdtBMTTransactionCode by using auditHeaderDAO.addAudit(auditHeader)
	 * for Work flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader  doReject(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader,"doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		TransactionCode transactionCode = (TransactionCode) auditHeader.
		getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getTransactionCodeDAO().delete(transactionCode,"_TEMP");

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
	 *
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader, String method){
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
	 * getTransactionCodeDAO().getErrorDetail with Error ID and language as parameters.
	 * if any error/Warnings then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail,String usrLanguage,String method){
		logger.debug("Entering");

		auditDetail.setErrorDetails(new ArrayList<ErrorDetails>());			
		TransactionCode transactionCode= (TransactionCode) auditDetail.getModelData();

		TransactionCode tempTransactionCode= null;
		if (transactionCode.isWorkflow()){
			tempTransactionCode = getTransactionCodeDAO().getTransactionCodeById(
					transactionCode.getId(), "_Temp");
		}
		TransactionCode befTransactionCode= getTransactionCodeDAO().getTransactionCodeById(
				transactionCode.getId(), "");

		TransactionCode old_TransactionCode= transactionCode.getBefImage();


		String[] errParm= new String[1];
		String[] valueParm= new String[1];
		valueParm[0]=transactionCode.getId();
		errParm[0]=PennantJavaUtil.getLabel("label_TranCode")+":"+valueParm[0];

		if (transactionCode.isNew()){ // for New record or new record into work flow

			if (!transactionCode.isWorkflow()){// With out Work flow only new records  
				if (befTransactionCode !=null){	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetails(PennantConstants.KEY_FIELD, "41001", 
									errParm,valueParm), usrLanguage));
				}	
			}else{ // with work flow
				if (transactionCode.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befTransactionCode !=null || tempTransactionCode!=null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetails(PennantConstants.KEY_FIELD, "41001", 
										errParm,valueParm), usrLanguage));
					}
				}else{ // if records not exists in the Main flow table
					if (befTransactionCode ==null || tempTransactionCode!=null ){
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetails(PennantConstants.KEY_FIELD, "41005", 
										errParm,valueParm), usrLanguage));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!transactionCode.isWorkflow()){	// With out Work flow for update and delete

				if (befTransactionCode ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetails(PennantConstants.KEY_FIELD, "41002", 
									errParm,valueParm), usrLanguage));
				}else{
					if (old_TransactionCode!=null && !old_TransactionCode.getLastMntOn().equals(
							befTransactionCode.getLastMntOn())){
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).
								equalsIgnoreCase(PennantConstants.TRAN_DEL)){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
									new ErrorDetails(PennantConstants.KEY_FIELD, "41003", 
											errParm,valueParm), usrLanguage));
						}else{
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
									new ErrorDetails(PennantConstants.KEY_FIELD, "41004",
											errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{

				if (tempTransactionCode==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetails(PennantConstants.KEY_FIELD, "41005", 
									errParm,valueParm), usrLanguage));
				}

				if (old_TransactionCode!=null && !old_TransactionCode.getLastMntOn().
						equals(tempTransactionCode.getLastMntOn())){ 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetails(PennantConstants.KEY_FIELD, "41005", 
									errParm,valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(
				auditDetail.getErrorDetails(), usrLanguage));

		if(StringUtils.trimToEmpty(method).equals("doApprove") || !transactionCode.isWorkflow()){
			auditDetail.setBefImage(befTransactionCode);	
		}
		logger.debug("Leaving");
		return auditDetail;
	}

}