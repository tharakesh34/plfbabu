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
 * FileName    		:  MandateServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  18-10-2016    														*
 *                                                                  						*
 * Modified Date    :  18-10-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 18-10-2016       Pennant	                 0.1                                            * 
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

package com.pennant.backend.service.mandate.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.documentdetails.DocumentManagerDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.mandate.MandateDAO;
import com.pennant.backend.dao.mandate.MandateStatusDAO;
import com.pennant.backend.dao.mandate.MandateStatusUpdateDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.documentdetails.DocumentManager;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.model.mandate.Mandate;
import com.pennant.backend.model.mandate.MandateStatus;
import com.pennant.backend.model.mandate.MandateStatusUpdate;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.mandate.MandateService;
import com.pennant.backend.util.MandateConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>Mandate</b>.<br>
 * 
 */
public class MandateServiceImpl extends GenericService<Mandate> implements MandateService {
	private static final Logger		logger	= Logger.getLogger(MandateServiceImpl.class);

	private AuditHeaderDAO			auditHeaderDAO;

	private MandateDAO				mandateDAO;
	private MandateStatusDAO		mandateStatusDAO;
	private MandateStatusUpdateDAO	mandateStatusUpdateDAO;
	private FinanceMainDAO			financeMainDAO;
	private DocumentManagerDAO documentManagerDAO;

	/**
	 * @return the auditHeaderDAO
	 */
	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	/**
	 * @param auditHeaderDAO
	 *            the auditHeaderDAO to set
	 */
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	/**
	 * @return the mandateDAO
	 */
	public MandateDAO getMandateDAO() {
		return mandateDAO;
	}

	/**
	 * @param mandateDAO
	 *            the mandateDAO to set
	 */
	public void setMandateDAO(MandateDAO mandateDAO) {
		this.mandateDAO = mandateDAO;
	}

	/**
	 * @return the mandate
	 */
	@Override
	public Mandate getMandate() {
		return getMandateDAO().getMandate();
	}

	/**
	 * @return the mandateStatusDAO
	 */
	public MandateStatusDAO getMandateStatusDAO() {
		return mandateStatusDAO;
	}

	/**
	 * @param mandateStatusDAO
	 *            the mandateStatusDAO to set
	 */
	public void setMandateStatusDAO(MandateStatusDAO mandateStatusDAO) {
		this.mandateStatusDAO = mandateStatusDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * Mandates/Mandates_Temp by using MandateDAO's save method b) Update the
	 * Record in the table. based on the module workFlow Configuration. by using
	 * MandateDAO's update method 3) Audit the record in to AuditHeader and
	 * AdtMandates by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @param boolean onlineRequest
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
		Mandate mandate = (Mandate) auditHeader.getAuditDetail().getModelData();

		if (mandate.isWorkflow()) {
			tableType = "_Temp";
		}

		if (mandate.isNew()) {
			getDocument(mandate);
			mandate.setId(getMandateDAO().save(mandate, tableType));
			auditHeader.getAuditDetail().setModelData(mandate);
			auditHeader.setAuditReference(String.valueOf(mandate.getMandateID()));
		} else {
			getDocument(mandate);
			getMandateDAO().update(mandate, tableType);
			if (StringUtils.trimToEmpty(mandate.getModule()).equals(MandateConstants.MODULE_REGISTRATION)) {
				MandateStatus mandateStatus = new MandateStatus();
				mandateStatus.setMandateID(mandate.getMandateID());
				mandateStatus.setStatus(mandate.getStatus());
				mandateStatus.setReason(mandate.getReason());
				mandateStatus.setChangeDate(mandate.getInputDate());
				getMandateStatusDAO().save(mandateStatus, "");
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
	 * table Mandates by using MandateDAO's delete method with type as Blank 3)
	 * Audit the record in to AuditHeader and AdtMandates by using
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
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		Mandate mandate = (Mandate) auditHeader.getAuditDetail().getModelData();
		getMandateDAO().delete(mandate, "");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getMandateById fetch the details by using MandateDAO's getMandateById
	 * method.
	 * 
	 * @param id
	 *            (int)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return Mandate
	 */

	@Override
	public Mandate getMandateById(long id) {
		return getMandateDAO().getMandateById(id, "_View");
	}

	@Override
	public Mandate getMandateStatusUpdateById(long id, String status) {
		return getMandateDAO().getMandateByStatus(id, status, "_View");
	}

	/**
	 * getApprovedMandateById fetch the details by using MandateDAO's
	 * getMandateById method . with parameter id and type as blank. it fetches
	 * the approved records from the Mandates.
	 * 
	 * @param id
	 *            (int)
	 * @return Mandate
	 */

	public Mandate getApprovedMandateById(long id) {
		return getMandateDAO().getMandateById(id, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getMandateDAO().delete with parameters mandate,"" b) NEW Add new
	 * record in to main table by using getMandateDAO().save with parameters
	 * mandate,"" c) EDIT Update record in the main table by using
	 * getMandateDAO().update with parameters mandate,"" 3) Delete the record
	 * from the workFlow table by using getMandateDAO().delete with parameters
	 * mandate,"_Temp" 4) Audit the record in to AuditHeader and AdtMandates by
	 * using auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the
	 * record in to AuditHeader and AdtMandates by using
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

		Mandate mandate = new Mandate();
		BeanUtils.copyProperties((Mandate) auditHeader.getAuditDetail().getModelData(), mandate);

		if (mandate.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			// For Mandate deletion is not required , so make it as inActive
			getMandateDAO().updateActive(mandate.getMandateID(), false);

		} else {
			mandate.setRoleCode("");
			mandate.setNextRoleCode("");
			mandate.setTaskId("");
			mandate.setNextTaskId("");
			mandate.setWorkflowId(0);
			
			if (StringUtils.trimToEmpty(mandate.getStatus()).equals(MandateConstants.STATUS_RELEASE)) {
				mandate.setStatus(MandateConstants.STATUS_APPROVED);
			} else if (!StringUtils.trimToEmpty(mandate.getStatus()).equals(MandateConstants.STATUS_HOLD)) {
				mandate.setStatus(MandateConstants.STATUS_NEW);
			}
			
			MandateStatus mandateStatus = new MandateStatus();
			mandateStatus.setMandateID(mandate.getMandateID());
			mandateStatus.setStatus(mandate.getStatus());
			mandateStatus.setReason(mandate.getReason());
			mandateStatus.setChangeDate(mandate.getInputDate());
			getMandateStatusDAO().save(mandateStatus, "");

			getDocument(mandate);

			if (mandate.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				mandate.setRecordType("");
				mandate.setMandateID(getMandateDAO().save(mandate, ""));
			} else {
				tranType = PennantConstants.TRAN_UPD;
				mandate.setRecordType("");
				getMandateDAO().update(mandate, "");
			}
		}

		if(!StringUtils.equals(mandate.getSourceId(), PennantConstants.FINSOURCE_ID_API)) {
			getMandateDAO().delete(mandate, "_Temp");
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			getAuditHeaderDAO().addAudit(auditHeader);
		}

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(mandate);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getMandateDAO().delete with parameters
	 * mandate,"_Temp" 3) Audit the record in to AuditHeader and AdtMandates by
	 * using auditHeaderDAO.addAudit(auditHeader) for Work flow
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

		Mandate mandate = (Mandate) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getMandateDAO().delete(mandate, "_Temp");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) validate the audit
	 * detail 2) if any error/Warnings then assign the to auditHeader 3)
	 * identify the nextprocess
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @param boolean onlineRequest
	 * @return auditHeader
	 */

	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * Validation method do the following steps. 1) get the details from the
	 * auditHeader. 2) fetch the details from the tables 3) Validate the Record
	 * based on the record details. 4) Validate for any business validation. 5)
	 * for any mismatch conditions Fetch the error details from
	 * getMandateDAO().getErrorDetail with Error ID and language as parameters.
	 * 6) if any error/Warnings then assign the to auditHeader
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @param boolean onlineRequest
	 * @return auditHeader
	 */

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetails>());
		Mandate mandate = (Mandate) auditDetail.getModelData();

		Mandate tempMandate = null;
		if (mandate.isWorkflow()) {
			tempMandate = getMandateDAO().getMandateById(mandate.getId(), "_Temp");
		}
		Mandate befMandate = getMandateDAO().getMandateById(mandate.getId(), "");

		Mandate oldMandate = mandate.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = String.valueOf(mandate.getId());
		errParm[0] = PennantJavaUtil.getLabel("label_MandateID") + ":" + valueParm[0];

		if (mandate.isNew()) { // for New record or new record into work flow

			if (!mandate.isWorkflow()) {// With out Work flow only new records  
				if (befMandate != null) { // Record Already Exists in the table then error  
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow
				if (mandate.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befMandate != null || tempMandate != null) { // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
					}
				} else { // if records not exists in the Main flow table				
					if (befMandate == null || tempMandate != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!mandate.isWorkflow()) { // With out Work flow for update and delete

				if (befMandate == null) { // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41002", errParm, valueParm), usrLanguage));
				} else {
					if (oldMandate != null && !oldMandate.getLastMntOn().equals(befMandate.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41003", errParm, valueParm), usrLanguage));
						} else {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41004", errParm, valueParm), usrLanguage));
						}
					}
				}
			} else {

				if (tempMandate == null) { // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}

				if (tempMandate != null && oldMandate != null && !oldMandate.getLastMntOn().equals(tempMandate.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}
			}
		}
		if (StringUtils.trimToEmpty(mandate.getRecordType()).equals(PennantConstants.RECORD_TYPE_DEL)) {
			int count = financeMainDAO.getFinanceCountByMandateId(mandate.getMandateID());
			if (count != 0) {
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41006", errParm, valueParm), usrLanguage));
			}
		}
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if (StringUtils.trimToEmpty(method).equals("doApprove") || !mandate.isWorkflow()) {
			auditDetail.setBefImage(befMandate);
		}
		return auditDetail;
	}

	public void processDownload(Mandate mandate) {
		logger.debug("Entering");

		getMandateDAO().updateStatus(mandate.getMandateID(), MandateConstants.STATUS_AWAITCON, mandate.getMandateRef(), mandate.getApprovalID(), "");
		MandateStatus mandateStatus = new MandateStatus();
		mandateStatus.setMandateID(mandate.getMandateID());
		mandateStatus.setStatus(MandateConstants.STATUS_AWAITCON);
		mandateStatus.setReason("");
		mandateStatus.setChangeDate(DateUtility.getAppDate());
		getMandateStatusDAO().save(mandateStatus, "");
		AuditHeader auditHeader = getAuditHeader(mandate, PennantConstants.TRAN_UPD);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");
	}

	public void processFileUpload(Mandate mandate, String status, String reasons, long fileID) {
		logger.debug("Entering");

		getMandateDAO().updateStatus(mandate.getMandateID(), status, mandate.getMandateRef(), mandate.getApprovalID(), "");
		MandateStatus mandateStatus = new MandateStatus();
		mandateStatus.setMandateID(mandate.getMandateID());
		mandateStatus.setStatus(status);
		mandateStatus.setReason(reasons);
		mandateStatus.setFileID(fileID);
		mandateStatus.setChangeDate(DateUtility.getAppDate());
		getMandateStatusDAO().save(mandateStatus, "");
		AuditHeader auditHeader = getAuditHeader(mandate, PennantConstants.TRAN_UPD);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
	}

	public long processStatusSave(MandateStatusUpdate mandateStatusUpdate) {
		return getMandateStatusUpdateDAO().save(mandateStatusUpdate, "");
	}

	public void processStatusUpdate(MandateStatusUpdate mandateStatusUpdate) {
		getMandateStatusUpdateDAO().update(mandateStatusUpdate, "");
	}

	public List<FinanceEnquiry> getMandateFinanceDetailById(long id) {
		return getMandateDAO().getMandateFinanceDetailById(id);
	}
	
	public int getFileCount(String fileName) {
		return getMandateStatusUpdateDAO().getFileCount(fileName);
	}
	
	@Override
	public List<Mandate> getApprovedMandatesByCustomerId(long custID) {
		return getMandateDAO().getApprovedMandatesByCustomerId(custID,"_AView");
	}

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */
	private AuditHeader getAuditHeader(Mandate aMandate, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aMandate.getBefImage(), aMandate);
		return new AuditHeader(String.valueOf(aMandate.getMandateID()), null, null, null, auditDetail, aMandate.getUserDetails(), null);
	}

	public MandateStatusUpdateDAO getMandateStatusUpdateDAO() {
		return mandateStatusUpdateDAO;
	}

	public void setMandateStatusUpdateDAO(MandateStatusUpdateDAO mandateStatusUpdateDAO) {
		this.mandateStatusUpdateDAO = mandateStatusUpdateDAO;
	}

	public FinanceMainDAO getFinanceMainDAO() {
		return financeMainDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	@Override
	public void getDocumentImage(Mandate mandate) {
		DocumentManager data = getDocumentManagerDAO().getById(mandate.getDocumentRef());
		if (data != null) {
			mandate.setDocImage(data.getDocImage());
		}
	}

	private void getDocument(Mandate mandate) {
		DocumentManager documentManager = new DocumentManager();
		if (mandate.getDocumentRef() != 0) {
			DocumentManager olddocumentManager = getDocumentManagerDAO().getById(mandate.getDocumentRef());
			byte[] arr1 = olddocumentManager.getDocImage();
			byte[] arr2 = mandate.getDocImage();
			if (!Arrays.equals(arr1, arr2)) {
				documentManager.setDocImage(arr2);
				mandate.setDocumentRef(getDocumentManagerDAO().save(documentManager));
			}
		} else {
			documentManager.setDocImage(mandate.getDocImage());
			mandate.setDocumentRef(getDocumentManagerDAO().save(documentManager));
		}
	}

	public DocumentManagerDAO getDocumentManagerDAO() {
		return documentManagerDAO;
	}

	public void setDocumentManagerDAO(DocumentManagerDAO documentManagerDAO) {
		this.documentManagerDAO = documentManagerDAO;
	}
}