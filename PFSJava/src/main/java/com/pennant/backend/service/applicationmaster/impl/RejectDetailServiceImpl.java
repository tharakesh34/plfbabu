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
 * FileName    		:  RejectDetailServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  06-05-2011    														*
 *                                                                  						*
 * Modified Date    :  06-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 06-05-2011       Pennant	                 0.1                                            * 
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
import com.pennant.backend.dao.applicationmaster.RejectDetailDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.applicationmaster.RejectDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.RejectDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>RejectDetail</b>.<br>
 * 
 */
public class RejectDetailServiceImpl extends GenericService<RejectDetail> implements RejectDetailService {

	private static Logger logger = Logger.getLogger(RejectDetailServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private RejectDetailDAO rejectDetailDAO;

	public RejectDetailServiceImpl() {
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

	public RejectDetailDAO getRejectDetailDAO() {
		return rejectDetailDAO;
	}

	public void setRejectDetailDAO(RejectDetailDAO rejectDetailDAO) {
		this.rejectDetailDAO = rejectDetailDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * BMTRejectCodes/BMTRejectCodes_Temp by using RejectDetailDAO's save method
	 * b) Update the Record in the table. based on the module workFlow
	 * Configuration. by using RejectDetailDAO's update method 3) Audit the
	 * record in to AuditHeader and AdtBMTRejectCodes by using
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
		
		RejectDetail rejectDetail = (RejectDetail) auditHeader.getAuditDetail().getModelData();
		
		TableType tableType = TableType.MAIN_TAB;
		if (rejectDetail.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (rejectDetail.isNew()) {
			rejectDetail.setRejectCode(getRejectDetailDAO().save(rejectDetail, tableType));
			auditHeader.getAuditDetail().setModelData(rejectDetail);
			auditHeader.setAuditReference(rejectDetail.getRejectCode());
		} else {
			getRejectDetailDAO().update(rejectDetail, tableType);
		}
		
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table BMTRejectCodes by using RejectDetailDAO's delete method with type
	 * as Blank 3) Audit the record in to AuditHeader and AdtBMTRejectCodes by
	 * using auditHeaderDAO.addAudit(auditHeader)
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
		RejectDetail rejectDetail = (RejectDetail) auditHeader.getAuditDetail().getModelData();
		getRejectDetailDAO().delete(rejectDetail, TableType.MAIN_TAB);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getRejectDetailById fetch the details by using RejectDetailDAO's
	 * getRejectDetailById method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return RejectDetail
	 */
	
	@Override
	public RejectDetail getRejectDetailById(String id) {
		return getRejectDetailDAO().getRejectDetailById(id, "_View");
	}

	/**
	 * getApprovedRejectDetailById fetch the details by using RejectDetailDAO's
	 * getRejectDetailById method . with parameter id and type as blank. it
	 * fetches the approved records from the BMTRejectCodes.
	 * 
	 * @param id
	 *            (String)
	 * @return RejectDetail
	 */
	public RejectDetail getApprovedRejectDetailById(String id) {
		return getRejectDetailDAO().getRejectDetailById(id, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getRejectDetailDAO().delete with parameters rejectDetail,"" b) NEW
	 * Add new record in to main table by using getRejectDetailDAO().save with
	 * parameters rejectDetail,"" c) EDIT Update record in the main table by
	 * using getRejectDetailDAO().update with parameters rejectDetail,"" 3)
	 * Delete the record from the workFlow table by using
	 * getRejectDetailDAO().delete with parameters rejectDetail,"_Temp" 4) Audit
	 * the record in to AuditHeader and AdtBMTRejectCodes by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in
	 * to AuditHeader and AdtBMTRejectCodes by using
	 * auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");
		
		String tranType = "";
		auditHeader = businessValidation(auditHeader);
		
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		
		RejectDetail rejectDetail = new RejectDetail();
		BeanUtils.copyProperties((RejectDetail) auditHeader.getAuditDetail().getModelData(), rejectDetail);
		
		getRejectDetailDAO().delete(rejectDetail, TableType.TEMP_TAB);
		
		if (!PennantConstants.RECORD_TYPE_NEW.equals(rejectDetail.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(rejectDetailDAO.getRejectDetailById(rejectDetail.getRejectCode(), ""));
		}
		
		if (rejectDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getRejectDetailDAO().delete(rejectDetail, TableType.MAIN_TAB);
		} else {
			rejectDetail.setRoleCode("");
			rejectDetail.setNextRoleCode("");
			rejectDetail.setTaskId("");
			rejectDetail.setNextTaskId("");
			rejectDetail.setWorkflowId(0);

			if (rejectDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				rejectDetail.setRecordType("");
				getRejectDetailDAO().save(rejectDetail, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				rejectDetail.setRecordType("");
				getRejectDetailDAO().update(rejectDetail, TableType.MAIN_TAB);
			}
		}
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(rejectDetail);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getRejectDetailDAO().delete with parameters
	 * rejectDetail,"_Temp" 3) Audit the record in to AuditHeader and
	 * AdtBMTRejectCodes by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug("Entering");
		
		auditHeader = businessValidation(auditHeader);
		
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		RejectDetail rejectDetail = (RejectDetail) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getRejectDetailDAO().delete(rejectDetail, TableType.TEMP_TAB);
		
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
	private AuditHeader businessValidation(AuditHeader auditHeader) {
		logger.debug("Entering");
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(),auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any mismatch conditions Fetch the error details
	 * from getRejectDetailDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings then assign
	 * the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug("Entering");

		// Get the model object.
		RejectDetail rejectDetail = (RejectDetail) auditDetail.getModelData();

		// Check the unique keys.
		if (rejectDetail.isNew()
				&& PennantConstants.RECORD_TYPE_NEW.equals(rejectDetail.getRecordType())
				&& rejectDetailDAO.isDuplicateKey(rejectDetail.getRejectCode(),
						rejectDetail.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[1];

			parameters[0] = PennantJavaUtil.getLabel("label_RejectCode") + ": " + rejectDetail.getRejectCode();

			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug("Leaving");
		return auditDetail;
	}
}