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
 * FileName    		:  PRelationCodeServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-05-2011    														*
 *                                                                  						*
 * Modified Date    :  05-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.service.systemmasters.impl;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.systemmasters.PRelationCodeDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.PRelationCode;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.systemmasters.PRelationCodeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;

/**
 * Service implementation for methods that depends on <b>PRelationCode</b>.<br>
 * 
 */
public class PRelationCodeServiceImpl extends GenericService<PRelationCode> implements PRelationCodeService {

	private static Logger logger = Logger.getLogger(PRelationCodeServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private PRelationCodeDAO pRelationCodeDAO;

	public PRelationCodeServiceImpl() {
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

	public PRelationCodeDAO getPRelationCodeDAO() {
		return pRelationCodeDAO;
	}

	public void setPRelationCodeDAO(PRelationCodeDAO pRelationCodeDAO) {
		this.pRelationCodeDAO = pRelationCodeDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * BMTPRelationCodes/BMTPRelationCodes_Temp by using PRelationCodeDAO's save
	 * method b) Update the Record in the table. based on the module workFlow
	 * Configuration. by using PRelationCodeDAO's update method 3) Audit the
	 * record in to AuditHeader and AdtBMTPRelationCodes by using
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
		PRelationCode pRelationCode = (PRelationCode) auditHeader.getAuditDetail().getModelData();
		if (pRelationCode.isWorkflow()) {
			tableType = "_Temp";
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		}

		if (pRelationCode.isNew()) {
			pRelationCode.setPRelationCode(getPRelationCodeDAO().save(pRelationCode, tableType));
			auditHeader.getAuditDetail().setModelData(pRelationCode);
			auditHeader.setAuditReference(pRelationCode.getPRelationCode());
		} else {
			getPRelationCodeDAO().update(pRelationCode, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table BMTPRelationCodes by using PRelationCodeDAO's delete method with
	 * type as Blank 3) Audit the record in to AuditHeader and
	 * AdtBMTPRelationCodes by using auditHeaderDAO.addAudit(auditHeader)
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
		PRelationCode pRelationCode = (PRelationCode) auditHeader.getAuditDetail().getModelData();
		getPRelationCodeDAO().delete(pRelationCode, "");
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getPRelationCodeById fetch the details by using PRelationCodeDAO's
	 * getPRelationCodeById method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return PRelationCode
	 */
	@Override
	public PRelationCode getPRelationCodeById(String id) {
		return getPRelationCodeDAO().getPRelationCodeById(id, "_View");
	}

	/**
	 * getApprovedPRelationCodeById fetch the details by using
	 * PRelationCodeDAO's getPRelationCodeById method . with parameter id and
	 * type as blank. it fetches the approved records from the
	 * BMTPRelationCodes.
	 * 
	 * @param id
	 *            (String)
	 * @return PRelationCode
	 */
	public PRelationCode getApprovedPRelationCodeById(String id) {
		return getPRelationCodeDAO().getPRelationCodeById(id, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getPRelationCodeDAO().delete with parameters pRelationCode,"" b)
	 * NEW Add new record in to main table by using getPRelationCodeDAO().save
	 * with parameters pRelationCode,"" c) EDIT Update record in the main table
	 * by using getPRelationCodeDAO().update with parameters pRelationCode,"" 3)
	 * Delete the record from the workFlow table by using
	 * getPRelationCodeDAO().delete with parameters pRelationCode,"_Temp" 4)
	 * Audit the record in to AuditHeader and AdtBMTPRelationCodes by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in
	 * to AuditHeader and AdtBMTPRelationCodes by using
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
		PRelationCode pRelationCode = new PRelationCode();
		BeanUtils.copyProperties((PRelationCode) auditHeader.getAuditDetail().getModelData(), pRelationCode);
		if (pRelationCode.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getPRelationCodeDAO().delete(pRelationCode, "");
		} else {
			pRelationCode.setRoleCode("");
			pRelationCode.setNextRoleCode("");
			pRelationCode.setTaskId("");
			pRelationCode.setNextTaskId("");
			pRelationCode.setWorkflowId(0);

			if (pRelationCode.getRecordType().equals(
					PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				pRelationCode.setRecordType("");
				getPRelationCodeDAO().save(pRelationCode, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				pRelationCode.setRecordType("");
				getPRelationCodeDAO().update(pRelationCode, "");
			}
		}

		getPRelationCodeDAO().delete(pRelationCode, "_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(pRelationCode);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getPRelationCodeDAO().delete with parameters
	 * pRelationCode,"_Temp" 3) Audit the record in to AuditHeader and
	 * AdtBMTPRelationCodes by using auditHeaderDAO.addAudit(auditHeader) for
	 * Work flow
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
		PRelationCode pRelationCode = (PRelationCode) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getPRelationCodeDAO().delete(pRelationCode, "_Temp");

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
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());

		PRelationCode pRelationCode = (PRelationCode) auditDetail.getModelData();
		PRelationCode tempPRelationCode = null;

		if (pRelationCode.isWorkflow()) {
			tempPRelationCode = getPRelationCodeDAO().getPRelationCodeById(pRelationCode.getId(), "_Temp");
		}

		PRelationCode befPRelationCode = getPRelationCodeDAO().getPRelationCodeById(pRelationCode.getId(), "");
		PRelationCode oldPRelationCode = pRelationCode.getBefImage();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = pRelationCode.getPRelationCode();
		errParm[0] = PennantJavaUtil.getLabel("label_PRelationCode") + ":"
		+ valueParm[0];

		if (pRelationCode.isNew()) { // for New record or new record into work flow

			if (!pRelationCode.isWorkflow()) {// With out Work flow only new records
				if (befPRelationCode != null) { // Record Already Exists in the table then error
					auditDetail
					.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001",errParm, null));
				}
			} else { // with work flow

				if (pRelationCode.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befPRelationCode != null || tempPRelationCode != null) { //if records already exists in the main table
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,null));
					}
				} else { // if records not exists in the Main flow table
					if (befPRelationCode == null || tempPRelationCode != null) {
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,null));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!pRelationCode.isWorkflow()) { // With out Work flow for update and delete

				if (befPRelationCode == null) { // if records not exists in the main table
					auditDetail
					.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002",errParm, null));
				} else {
					if (oldPRelationCode != null
							&& !oldPRelationCode.getLastMntOn().equals(befPRelationCode.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41003",errParm, null));
						} else {
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41004",errParm, null));
						}
					}
				}
			} else {

				if (tempPRelationCode == null) { // if records not exists in the Work flow table
					auditDetail
					.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005",errParm, null));
				}

				if (tempPRelationCode != null
						&& oldPRelationCode != null
						&& !oldPRelationCode.getLastMntOn().equals(tempPRelationCode.getLastMntOn())) {
					auditDetail
					.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005",errParm, null));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
		if ("doApprove".equals(StringUtils.trimToEmpty(method))|| !pRelationCode.isWorkflow()) {
			auditDetail.setBefImage(befPRelationCode);
		}
		logger.debug("Leaving");
		return auditDetail;
	}

}