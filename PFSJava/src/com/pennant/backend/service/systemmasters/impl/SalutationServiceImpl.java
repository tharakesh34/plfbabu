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
 * FileName    		:  SalutationServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  03-05-2011    														*
 *                                                                  						*
 * Modified Date    :  03-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-05-2011       Pennant	                 0.1                                            * 
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
import com.pennant.backend.dao.systemmasters.SalutationDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.Salutation;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.systemmasters.SalutationService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>Salutation</b>.<br>
 * 
 */
public class SalutationServiceImpl extends GenericService<Salutation> implements SalutationService {

	private static Logger logger = Logger.getLogger(SalutationServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private SalutationDAO salutationDAO;

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public SalutationDAO getSalutationDAO() {
		return salutationDAO;
	}

	public void setSalutationDAO(SalutationDAO salutationDAO) {
		this.salutationDAO = salutationDAO;
	}

	public Salutation getSalutation() {
		return getSalutationDAO().getSalutation();
	}

	public Salutation getNewSalutation() {
		return getSalutationDAO().getNewSalutation();
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * BMTSalutations/BMTSalutations_Temp by using SalutationDAO's save method
	 * b) Update the Record in the table. based on the module workFlow
	 * Configuration. by using SalutationDAO's update method 3) Audit the record
	 * in to AuditHeader and AdtBMTSalutations by using
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
		Salutation salutation = (Salutation) auditHeader.getAuditDetail().getModelData();
		if (salutation.isWorkflow()) {
			tableType = "_TEMP";
		}

		if (salutation.isNew()) {
			salutation.setSalutationCode(getSalutationDAO().save(salutation,tableType));
			auditHeader.getAuditDetail().setModelData(salutation);
			auditHeader.setAuditReference(String.valueOf(salutation.getSalutationCode()));
		} else {
			getSalutationDAO().update(salutation, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table BMTSalutations by using SalutationDAO's delete method with type as
	 * Blank 3) Audit the record in to AuditHeader and AdtBMTSalutations by
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
		Salutation salutation = (Salutation) auditHeader.getAuditDetail().getModelData();
		getSalutationDAO().delete(salutation, "");
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getSalutationById fetch the details by using SalutationDAO's
	 * getSalutationById method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return Salutation
	 */
	@Override
	public Salutation getSalutationById(String id) {
		return getSalutationDAO().getSalutationById(id, "_View");
	}

	/**
	 * getApprovedSalutationById fetch the details by using SalutationDAO's
	 * getSalutationById method . with parameter id and type as blank. it
	 * fetches the approved records from the BMTSalutations.
	 * 
	 * @param id
	 *            (String)
	 * @return Salutation
	 */
	public Salutation getApprovedSalutationById(String id) {
		return getSalutationDAO().getSalutationById(id, "_AView");
	}

	/**
	 * This method refresh the Record.
	 * 
	 * @param Salutation
	 *            (salutation)
	 * @return salutation
	 */
	@Override
	public Salutation refresh(Salutation salutation) {
		logger.debug("Entering");
		getSalutationDAO().refresh(salutation);
		getSalutationDAO().initialize(salutation);
		logger.debug("Leaving");
		return salutation;
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getSalutationDAO().delete with parameters salutation,"" b) NEW Add
	 * new record in to main table by using getSalutationDAO().save with
	 * parameters salutation,"" c) EDIT Update record in the main table by using
	 * getSalutationDAO().update with parameters salutation,"" 3) Delete the
	 * record from the workFlow table by using getSalutationDAO().delete with
	 * parameters salutation,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtBMTSalutations by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow 5) Audit the record in to AuditHeader and AdtBMTSalutations by using
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
		Salutation salutation = new Salutation();
		BeanUtils.copyProperties((Salutation) auditHeader.getAuditDetail().getModelData(), salutation);
		if (salutation.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			getSalutationDAO().delete(salutation, "");

		} else {
			salutation.setRoleCode("");
			salutation.setNextRoleCode("");
			salutation.setTaskId("");
			salutation.setNextTaskId("");
			salutation.setWorkflowId(0);

			if (salutation.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				salutation.setRecordType("");
				getSalutationDAO().save(salutation, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				salutation.setRecordType("");
				getSalutationDAO().update(salutation, "");
			}
		}

		getSalutationDAO().delete(salutation, "_TEMP");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(salutation);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getSalutationDAO().delete with parameters
	 * salutation,"_Temp" 3) Audit the record in to AuditHeader and
	 * AdtBMTSalutations by using auditHeaderDAO.addAudit(auditHeader) for Work
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
		Salutation salutation = (Salutation) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getSalutationDAO().delete(salutation, "_TEMP");
		getAuditHeaderDAO().addAudit(auditHeader);
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

		Salutation salutation = (Salutation) auditDetail.getModelData();
		Salutation tempSalutation = null;

		if (salutation.isWorkflow()) {
			tempSalutation = getSalutationDAO().getSalutationById(salutation.getId(), "_Temp");
		}

		Salutation befSalutation = getSalutationDAO().getSalutationById(salutation.getId(), "");
		Salutation old_Salutation = salutation.getBefImage();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = salutation.getSalutationCode();
		errParm[0] = PennantJavaUtil.getLabel("label_SalutationCode") + ":"
		+ valueParm[0];

		if (salutation.isNew()) { // for New record or new record into work flow

			if (!salutation.isWorkflow()) {// With out Work flow only new records
				if (befSalutation != null) { // Record Already Exists in the table then error
					auditDetail
					.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001",errParm, null));
				}
			} else { // with work flow

				if (salutation.getRecordType().equals(
						PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befSalutation != null || tempSalutation != null) { //if records already exists in the main table
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,null));
					}

				} else { // if records not exists in the Main flow table
					if (befSalutation == null || tempSalutation != null) {
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,null));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!salutation.isWorkflow()) { // With out Work flow for update and delete

				if (befSalutation == null) { // if records not exists in the main table
					auditDetail
					.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41002",errParm, null));
				} else {
					if (old_Salutation != null
							&& !old_Salutation.getLastMntOn().equals(befSalutation.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41003",errParm, null));
						} else {
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41004",errParm, null));
						}
					}
				}

			} else {
				if (tempSalutation == null) { // if records not exists in the Work flow table
					auditDetail
					.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005",errParm, null));
				}
				if (tempSalutation != null
						&& old_Salutation != null
						&& !old_Salutation.getLastMntOn().equals(tempSalutation.getLastMntOn())) {
					auditDetail
					.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005",errParm, null));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
		if (StringUtils.trimToEmpty(method).equals("doApprove")
				|| !salutation.isWorkflow()) {
			auditDetail.setBefImage(befSalutation);
		}
		logger.debug("Leaving");
		return auditDetail;
	}
}