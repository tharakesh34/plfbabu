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

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.applicationmaster.RejectDetailDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.applicationmaster.RejectDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.RejectDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

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

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		String tableType = "";
		RejectDetail rejectDetail = (RejectDetail) auditHeader.getAuditDetail().getModelData();

		if (rejectDetail.isWorkflow()) {
			tableType = "_Temp";
		}

		if (rejectDetail.isNew()) {
			rejectDetail.setRejectCode(getRejectDetailDAO().save(rejectDetail,tableType));
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
		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		RejectDetail rejectDetail = (RejectDetail) auditHeader.getAuditDetail().getModelData();
		getRejectDetailDAO().delete(rejectDetail, "");
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
		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		RejectDetail rejectDetail = new RejectDetail();
		BeanUtils.copyProperties((RejectDetail) auditHeader.getAuditDetail().getModelData(), rejectDetail);
		if (rejectDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getRejectDetailDAO().delete(rejectDetail, "");
		} else {
			rejectDetail.setRoleCode("");
			rejectDetail.setNextRoleCode("");
			rejectDetail.setTaskId("");
			rejectDetail.setNextTaskId("");
			rejectDetail.setWorkflowId(0);

			if (rejectDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				rejectDetail.setRecordType("");
				getRejectDetailDAO().save(rejectDetail, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				rejectDetail.setRecordType("");
				getRejectDetailDAO().update(rejectDetail, "");
			}
		}
		getRejectDetailDAO().delete(rejectDetail, "_Temp");
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
		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		RejectDetail rejectDetail = (RejectDetail) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getRejectDetailDAO().delete(rejectDetail, "_Temp");
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
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(),auditHeader.getUsrLanguage(), method);
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

		RejectDetail rejectDetail = (RejectDetail) auditDetail.getModelData();
		RejectDetail tempRejectDetail = null;

		if (rejectDetail.isWorkflow()) {
			tempRejectDetail = getRejectDetailDAO().getRejectDetailById(rejectDetail.getId(), "_Temp");
		}

		RejectDetail befRejectDetail = getRejectDetailDAO().getRejectDetailById(rejectDetail.getId(), "");
		RejectDetail oldRejectDetail = rejectDetail.getBefImage();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = rejectDetail.getRejectCode();
		errParm[0] = PennantJavaUtil.getLabel("label_RejectCode") + ":"+ valueParm[0];

		if (rejectDetail.isNew()) { // for New record or new record into work flow

			if (!rejectDetail.isWorkflow()) {// With out Work flow only new records
				if (befRejectDetail != null) { // Record Already Exists in the table then error
					auditDetail
					.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001",errParm, null));
				}
			} else { // with work flow
				if (rejectDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is new 
					if (befRejectDetail != null || tempRejectDetail != null) { // if records already exists in the main table
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,null));
					}
				} else { // if records not exists in the Main flow table
					if (befRejectDetail == null || tempRejectDetail != null) {
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,null));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!rejectDetail.isWorkflow()) { // With out Work flow for update and delete
				if (befRejectDetail == null) { // if records not exists in the main table
					auditDetail
					.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41002",errParm, null));
				} else {
					if (oldRejectDetail != null
							&& !oldRejectDetail.getLastMntOn().equals(befRejectDetail.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41003",errParm, null));
						} else {
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41004",errParm, null));
						}
					}
				}
			} else {
				if (tempRejectDetail == null) { // if records not exists in the Work flow table
					auditDetail
					.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005",errParm, null));
				}

				if (tempRejectDetail != null
						&& oldRejectDetail != null
						&& !oldRejectDetail.getLastMntOn().equals(tempRejectDetail.getLastMntOn())) {
					auditDetail
					.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005",errParm, null));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
		if ("doApprove".equals(StringUtils.trimToEmpty(method))
				|| !rejectDetail.isWorkflow()) {
			auditDetail.setBefImage(befRejectDetail);
		}
		logger.debug("Leaving");
		return auditDetail;
	}
}