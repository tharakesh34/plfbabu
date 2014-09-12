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
 * FileName    		:  DocumentTypeServiceImpl.java                                                   * 	  
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
import com.pennant.backend.dao.systemmasters.DocumentTypeDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.DocumentType;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.systemmasters.DocumentTypeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>DocumentType</b>.<br>
 * 
 */
public class DocumentTypeServiceImpl extends GenericService<DocumentType>
		implements DocumentTypeService {
	
	private final static Logger logger = Logger.getLogger(DocumentTypeServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private DocumentTypeDAO documentTypeDAO;

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public DocumentTypeDAO getDocumentTypeDAO() {
		return documentTypeDAO;
	}

	public void setDocumentTypeDAO(DocumentTypeDAO documentTypeDAO) {
		this.documentTypeDAO = documentTypeDAO;
	}

	public DocumentType getDocumentType() {
		return getDocumentTypeDAO().getDocumentType();
	}

	public DocumentType getNewDocumentType() {
		return getDocumentTypeDAO().getNewDocumentType();
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * BMTDocumentTypes/BMTDocumentTypes_Temp by using DocumentTypeDAO's save
	 * method b) Update the Record in the table. based on the module workFlow
	 * Configuration. by using DocumentTypeDAO's update method 3) Audit the
	 * record in to AuditHeader and AdtBMTDocumentTypes by using
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
		DocumentType documentType = (DocumentType) auditHeader.getAuditDetail()
				.getModelData();

		if (documentType.isWorkflow()) {
			tableType = "_TEMP";
		}

		if (documentType.isNew()) {
			documentType.setId(getDocumentTypeDAO().save(documentType,
					tableType));
			auditHeader.getAuditDetail().setModelData(documentType);
			auditHeader.setAuditReference(documentType.getId());
		} else {
			getDocumentTypeDAO().update(documentType, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table BMTDocumentTypes by using DocumentTypeDAO's delete method with type
	 * as Blank 3) Audit the record in to AuditHeader and AdtBMTDocumentTypes by
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
		DocumentType documentType = (DocumentType) auditHeader.getAuditDetail()
				.getModelData();

		getDocumentTypeDAO().delete(documentType, "");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getDocumentTypeById fetch the details by using DocumentTypeDAO's
	 * getDocumentTypeById method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return DocumentType
	 */
	@Override
	public DocumentType getDocumentTypeById(String id) {
		return getDocumentTypeDAO().getDocumentTypeById(id, "_View");
	}

	/**
	 * getApprovedDocumentTypeById fetch the details by using DocumentTypeDAO's
	 * getDocumentTypeById method . with parameter id and type as blank. it
	 * fetches the approved records from the BMTDocumentTypes.
	 * 
	 * @param id
	 *            (String)
	 * @return DocumentType
	 */
	public DocumentType getApprovedDocumentTypeById(String id) {
		return getDocumentTypeDAO().getDocumentTypeById(id, "_AView");
	}

	/**
	 * This method refresh the Record.
	 * 
	 * @param DocumentType
	 *            (documentType)
	 * @return documentType
	 */
	@Override
	public DocumentType refresh(DocumentType documentType) {
		logger.debug("Entering");
		getDocumentTypeDAO().refresh(documentType);
		getDocumentTypeDAO().initialize(documentType);
		logger.debug("Leaving");
		return documentType;
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getDocumentTypeDAO().delete with parameters documentType,"" b) NEW
	 * Add new record in to main table by using getDocumentTypeDAO().save with
	 * parameters documentType,"" c) EDIT Update record in the main table by
	 * using getDocumentTypeDAO().update with parameters documentType,"" 3)
	 * Delete the record from the workFlow table by using
	 * getDocumentTypeDAO().delete with parameters documentType,"_Temp" 4) Audit
	 * the record in to AuditHeader and AdtBMTDocumentTypes by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in
	 * to AuditHeader and AdtBMTDocumentTypes by using
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
			return auditHeader;
		}
		DocumentType documentType = new DocumentType();
		BeanUtils.copyProperties((DocumentType) auditHeader.getAuditDetail()
				.getModelData(), documentType);

		if (documentType.getRecordType().equals(
				PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			getDocumentTypeDAO().delete(documentType, "");
		} else {
			documentType.setRoleCode("");
			documentType.setNextRoleCode("");
			documentType.setTaskId("");
			documentType.setNextTaskId("");
			documentType.setWorkflowId(0);

			if (documentType.getRecordType().equals(
					PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				documentType.setRecordType("");
				getDocumentTypeDAO().save(documentType, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				documentType.setRecordType("");
				getDocumentTypeDAO().update(documentType, "");
			}
		}

		getDocumentTypeDAO().delete(documentType, "_TEMP");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(documentType);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getDocumentTypeDAO().delete with parameters
	 * documentType,"_Temp" 3) Audit the record in to AuditHeader and
	 * AdtBMTDocumentTypes by using auditHeaderDAO.addAudit(auditHeader) for
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
			return auditHeader;
		}
		DocumentType documentType = (DocumentType) auditHeader.getAuditDetail()
				.getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getDocumentTypeDAO().delete(documentType, "_TEMP");

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
		auditHeader = nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any
	 * mismatch conditions Fetch the error details from
	 * getDocumentTypeDAO().getErrorDetail with Error ID and language as
	 * parameters. if any error/Warnings then assign the to auditDeail Object
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

		DocumentType documentType = (DocumentType) auditDetail.getModelData();
		DocumentType tempDocumentType = null;

		if (documentType.isWorkflow()) {
			tempDocumentType = getDocumentTypeDAO().getDocumentTypeById(
					documentType.getId(), "_Temp");
		}

		DocumentType befDocumentType = getDocumentTypeDAO()
				.getDocumentTypeById(documentType.getId(), "");
		DocumentType oldDocumentType = documentType.getBefImage();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = documentType.getDocTypeCode();
		errParm[0] = PennantJavaUtil.getLabel("label_DocTypeCode") + ":"
				+ valueParm[0];

		if (documentType.isNew()) { // for New record or new record into work
			// flow

			if (!documentType.isWorkflow()) {// With out Work flow only new
				// records
				if (befDocumentType != null) { // Record Already Exists in the
					// table then error
					auditDetail
							.setErrorDetail(new ErrorDetails(
									PennantConstants.KEY_FIELD, "41001",
									errParm, null));
				}
			} else { // with work flow

				if (documentType.getRecordType().equals(
						PennantConstants.RECORD_TYPE_NEW)) { // if records type
					// is new
					if (befDocumentType != null || tempDocumentType != null) { // if
															// records already exists
															// in the main table
						auditDetail.setErrorDetail(new ErrorDetails(
								PennantConstants.KEY_FIELD, "41001", errParm,
								null));
					}
				} else { // if records not exists in the Main flow table
					if (befDocumentType == null || tempDocumentType != null) {
						auditDetail.setErrorDetail(new ErrorDetails(
								PennantConstants.KEY_FIELD, "41005", errParm,
								null));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!documentType.isWorkflow()) { // With out Work flow for update
				// and delete

				if (befDocumentType == null) { // if records not exists in the
					// main table
					auditDetail
							.setErrorDetail(new ErrorDetails(
									PennantConstants.KEY_FIELD, "41002",
									errParm, null));
				} else {
					if (oldDocumentType != null
							&& !oldDocumentType.getLastMntOn().equals(
									befDocumentType.getLastMntOn())) {
						if (StringUtils.trimToEmpty(
								auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(new ErrorDetails(
									PennantConstants.KEY_FIELD, "41003",
									errParm, null));
						} else {
							auditDetail.setErrorDetail(new ErrorDetails(
									PennantConstants.KEY_FIELD, "41004",
									errParm, null));
						}
					}
				}
			} else {

				if (tempDocumentType == null) { // if records not exists in the
					// Work flow table
					auditDetail
							.setErrorDetail(new ErrorDetails(
									PennantConstants.KEY_FIELD, "41005",
									errParm, null));
				}
				if (tempDocumentType != null
						&& oldDocumentType != null
						&& !oldDocumentType.getLastMntOn().equals(
								tempDocumentType.getLastMntOn())) {
					auditDetail
							.setErrorDetail(new ErrorDetails(
									PennantConstants.KEY_FIELD, "41005",
									errParm, null));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(
				auditDetail.getErrorDetails(), usrLanguage));
		if (StringUtils.trimToEmpty(method).equals("doApprove")
				|| !documentType.isWorkflow()) {
			auditDetail.setBefImage(befDocumentType);
		}
		logger.debug("Leaving");
		return auditDetail;
	}

}