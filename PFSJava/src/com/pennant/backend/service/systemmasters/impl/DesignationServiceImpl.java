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
 * FileName    		:  DesignationServiceImpl.java                                                   * 	  
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
import com.pennant.backend.dao.systemmasters.DesignationDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.Designation;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.systemmasters.DesignationService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>Designation</b>.<br>
 * 
 */
public class DesignationServiceImpl extends GenericService<Designation> implements DesignationService {

	private static Logger logger = Logger.getLogger(DesignationServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private DesignationDAO designationDAO;

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public DesignationDAO getDesignationDAO() {
		return designationDAO;
	}

	public void setDesignationDAO(DesignationDAO designationDAO) {
		this.designationDAO = designationDAO;
	}

	public Designation getDesignation() {
		return getDesignationDAO().getDesignation();
	}

	public Designation getNewDesignation() {
		return getDesignationDAO().getNewDesignation();
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * BMTDesignations/BMTDesignations_Temp by using DesignationDAO's save
	 * method b) Update the Record in the table. based on the module workFlow
	 * Configuration. by using DesignationDAO's update method 3) Audit the
	 * record in to AuditHeader and AdtBMTDesignations by using
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
		Designation designation = (Designation) auditHeader.getAuditDetail()
				.getModelData();

		if (designation.isWorkflow()) {
			tableType = "_TEMP";
		}

		if (designation.isNew()) {
			designation.setId(getDesignationDAO().save(designation, tableType));
			auditHeader.getAuditDetail().setModelData(designation);
			auditHeader.setAuditReference(designation.getId());
		} else {
			getDesignationDAO().update(designation, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table BMTDesignations by using DesignationDAO's delete method with type
	 * as Blank 3) Audit the record in to AuditHeader and AdtBMTDesignations by
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
		Designation designation = (Designation) auditHeader.getAuditDetail()
				.getModelData();

		getDesignationDAO().delete(designation, "");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getDesignationById fetch the details by using DesignationDAO's
	 * getDesignationById method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return Designation
	 */
	@Override
	public Designation getDesignationById(String id) {
		return getDesignationDAO().getDesignationById(id, "_View");
	}

	/**
	 * getApprovedDesignationById fetch the details by using DesignationDAO's
	 * getDesignationById method . with parameter id and type as blank. it
	 * fetches the approved records from the BMTDesignations.
	 * 
	 * @param id
	 *            (String)
	 * @return Designation
	 */
	public Designation getApprovedDesignationById(String id) {
		return getDesignationDAO().getDesignationById(id, "_AView");
	}

	/**
	 * This method refresh the Record.
	 * 
	 * @param Designation
	 *            (designation)
	 * @return designation
	 */
	@Override
	public Designation refresh(Designation designation) {
		logger.debug("Entering");
		getDesignationDAO().refresh(designation);
		getDesignationDAO().initialize(designation);
		logger.debug("Leaving");
		return designation;
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getDesignationDAO().delete with parameters designation,"" b) NEW
	 * Add new record in to main table by using getDesignationDAO().save with
	 * parameters designation,"" c) EDIT Update record in the main table by
	 * using getDesignationDAO().update with parameters designation,"" 3) Delete
	 * the record from the workFlow table by using getDesignationDAO().delete
	 * with parameters designation,"_Temp" 4) Audit the record in to AuditHeader
	 * and AdtBMTDesignations by using auditHeaderDAO.addAudit(auditHeader) for
	 * Work flow 5) Audit the record in to AuditHeader and AdtBMTDesignations by
	 * using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
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
		Designation designation = new Designation();
		BeanUtils.copyProperties((Designation) auditHeader.getAuditDetail()
				.getModelData(), designation);

		if (designation.getRecordType()
				.equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			getDesignationDAO().delete(designation, "");

		} else {
			designation.setRoleCode("");
			designation.setNextRoleCode("");
			designation.setTaskId("");
			designation.setNextTaskId("");
			designation.setWorkflowId(0);

			if (designation.getRecordType().equals(
					PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				designation.setRecordType("");
				getDesignationDAO().save(designation, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				designation.setRecordType("");
				getDesignationDAO().update(designation, "");
			}
		}

		getDesignationDAO().delete(designation, "_TEMP");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(designation);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getDesignationDAO().delete with parameters
	 * designation,"_Temp" 3) Audit the record in to AuditHeader and
	 * AdtBMTDesignations by using auditHeaderDAO.addAudit(auditHeader) for Work
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
		Designation designation = (Designation) auditHeader.getAuditDetail()
				.getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getDesignationDAO().delete(designation, "_TEMP");

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
	 * getDesignationDAO().getErrorDetail with Error ID and language as
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

		Designation designation = (Designation) auditDetail.getModelData();
		Designation tempDesignation = null;

		if (designation.isWorkflow()) {
			tempDesignation = getDesignationDAO().getDesignationById(
					designation.getId(), "_Temp");
		}

		Designation befDesignation = getDesignationDAO().getDesignationById(
				designation.getId(), "");
		Designation old_Designation = designation.getBefImage();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = designation.getDesgCode();
		errParm[0] = PennantJavaUtil.getLabel("label_DesgCode") + ":"
				+ valueParm[0];

		if (designation.isNew()) { // for New record or new record into work
									// flow

			if (!designation.isWorkflow()) {// With out Work flow only new
											// records
				if (befDesignation != null) { // Record Already Exists in the
					// table then error
					auditDetail
							.setErrorDetail(new ErrorDetails(
									PennantConstants.KEY_FIELD, "41001",
									errParm, null));
				}
			} else { // with work flow

				if (designation.getRecordType().equals(
						PennantConstants.RECORD_TYPE_NEW)) { // if records type
					// is new
					if (befDesignation != null || tempDesignation != null) { // if
															// records already exists
															// in the main table
						auditDetail.setErrorDetail(new ErrorDetails(
								PennantConstants.KEY_FIELD, "41001", errParm,
								null));
					}
				} else { // if records not exists in the Main flow table
					if (befDesignation == null || tempDesignation != null) {
						auditDetail.setErrorDetail(new ErrorDetails(
								PennantConstants.KEY_FIELD, "41005", errParm,
								null));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!designation.isWorkflow()) { // With out Work flow for update
				// and delete

				if (befDesignation == null) { // if records not exists in the
					// main table
					auditDetail
							.setErrorDetail(new ErrorDetails(
									PennantConstants.KEY_FIELD, "41002",
									errParm, null));
				} else {
					if (old_Designation != null
							&& !old_Designation.getLastMntOn().equals(
									befDesignation.getLastMntOn())) {
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

				if (tempDesignation == null) { // if records not exists in the
					// Work flow table
					auditDetail
							.setErrorDetail(new ErrorDetails(
									PennantConstants.KEY_FIELD, "41005",
									errParm, null));
				}
				if (tempDesignation != null
						&& old_Designation != null
						&& !old_Designation.getLastMntOn().equals(
								tempDesignation.getLastMntOn())) {
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
				|| !designation.isWorkflow()) {
			auditDetail.setBefImage(befDesignation);
		}
		logger.debug("Leaving");
		return auditDetail;
	}

}