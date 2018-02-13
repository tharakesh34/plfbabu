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

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.applicationmaster.TransactionCodeDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.applicationmaster.TransactionCode;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.TransactionCodeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>TransactionCode</b>.<br>
 * 
 */
public class TransactionCodeServiceImpl extends GenericService<TransactionCode> implements TransactionCodeService {
	
	private static final Logger logger = Logger.getLogger(TransactionCodeServiceImpl.class);
	
	private AuditHeaderDAO auditHeaderDAO;
	private TransactionCodeDAO transactionCodeDAO;

	public TransactionCodeServiceImpl() {
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
	
	public TransactionCodeDAO getTransactionCodeDAO() {
		return transactionCodeDAO;
	}
	public void setTransactionCodeDAO(TransactionCodeDAO transactionCodeDAO) {
		this.transactionCodeDAO = transactionCodeDAO;
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
		
		auditHeader = businessValidation(auditHeader);
		
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		
		TransactionCode transactionCode = (TransactionCode) auditHeader
				.getAuditDetail().getModelData();
		
		TableType tableType = TableType.MAIN_TAB;
		if (transactionCode.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (transactionCode.isNew()) {
			transactionCode.setId(getTransactionCodeDAO().save(transactionCode,tableType));
			auditHeader.getAuditDetail().setModelData(transactionCode);
			auditHeader.setAuditReference(transactionCode.getTranCode());
		}else{
			getTransactionCodeDAO().update(transactionCode, tableType);
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
		
		auditHeader = businessValidation(auditHeader);
	
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		
		TransactionCode transactionCode = (TransactionCode) auditHeader
				.getAuditDetail().getModelData();
		getTransactionCodeDAO().delete(transactionCode, TableType.MAIN_TAB);
		
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
		auditHeader = businessValidation(auditHeader);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		TransactionCode transactionCode = new TransactionCode();
		BeanUtils.copyProperties((TransactionCode) auditHeader.getAuditDetail()
				.getModelData(), transactionCode);
		
		getTransactionCodeDAO().delete(transactionCode, TableType.TEMP_TAB);
		
		if (!PennantConstants.RECORD_TYPE_NEW.equals(transactionCode.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(transactionCodeDAO.getTransactionCodeById(transactionCode.getTranCode(), ""));
		}

		if (transactionCode.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType=PennantConstants.TRAN_DEL;
			getTransactionCodeDAO().delete(transactionCode, TableType.MAIN_TAB);
		} else {
			transactionCode.setRoleCode("");
			transactionCode.setNextRoleCode("");
			transactionCode.setTaskId("");
			transactionCode.setNextTaskId("");
			transactionCode.setWorkflowId(0);

			if (transactionCode.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType=PennantConstants.TRAN_ADD;
				transactionCode.setRecordType("");
				getTransactionCodeDAO().save(transactionCode, TableType.MAIN_TAB);
			} else {
				tranType=PennantConstants.TRAN_UPD;
				transactionCode.setRecordType("");
				getTransactionCodeDAO().update(transactionCode, TableType.MAIN_TAB);
			}
		}
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

		auditHeader = businessValidation(auditHeader);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		TransactionCode transactionCode = (TransactionCode) auditHeader
				.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getTransactionCodeDAO().delete(transactionCode, TableType.TEMP_TAB);

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
	 * @param AuditHeader 
	 * 				(auditHeader)    
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader){
		logger.debug("Entering");
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage());
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
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug("Entering");

		// Get the model object.
		TransactionCode transactionCode = (TransactionCode) auditDetail.getModelData();

		// Check the unique keys.
		if (transactionCode.isNew() 
				&& PennantConstants.RECORD_TYPE_NEW.equals(transactionCode.getRecordType())
				&& transactionCodeDAO.isDuplicateKey(transactionCode.getTranCode(), 
				transactionCode.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[1];

			parameters[0] = PennantJavaUtil.getLabel("label_TranCode") + ": " + transactionCode.getTranCode();

			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug("Leaving");
		return auditDetail;
	}
}