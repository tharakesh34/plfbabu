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
 * FileName    		:  GeneralDesignationServiceImpl.java                                                   * 	  
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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.systemmasters.GeneralDesignationDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.GeneralDesignation;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.systemmasters.GeneralDesignationService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>GeneralDesignation</b>.<br>
 * 
 */
public class GeneralDesignationServiceImpl extends GenericService<GeneralDesignation> implements GeneralDesignationService {

	private static Logger logger = Logger
			.getLogger(GeneralDesignationServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private GeneralDesignationDAO generalDesignationDAO;

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public GeneralDesignationDAO getGeneralDesignationDAO() {
		return generalDesignationDAO;
	}
	public void setGeneralDesignationDAO(
			GeneralDesignationDAO generalDesignationDAO) {
		this.generalDesignationDAO = generalDesignationDAO;
	}

	@Override
	public GeneralDesignation getGeneralDesignation() {
		return getGeneralDesignationDAO().getGeneralDesignation();
	}
	@Override
	public GeneralDesignation getNewGeneralDesignation() {
		return getGeneralDesignationDAO().getNewGeneralDesignation();
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * RMTGenDesignations/RMTGenDesignations_Temp by using
	 * GeneralDesignationDAO's save method b) Update the Record in the table.
	 * based on the module workFlow Configuration. by using
	 * GeneralDesignationDAO's update method 3) Audit the record in to
	 * AuditHeader and AdtRMTGenDesignations by using
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
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}

		String tableType = "";
		GeneralDesignation generalDesignation = (GeneralDesignation) auditHeader
				.getAuditDetail().getModelData();

		if (generalDesignation.isWorkflow()) {
			tableType = "_TEMP";
		}

		if (generalDesignation.isNew()) {
			generalDesignation.setGenDesignation(getGeneralDesignationDAO()
					.save(generalDesignation, tableType));
			auditHeader.getAuditDetail().setModelData(generalDesignation);
			auditHeader.setAuditReference(String.valueOf(generalDesignation
					.getGenDesignation()));
		} else {
			getGeneralDesignationDAO().update(generalDesignation, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table RMTGenDesignations by using GeneralDesignationDAO's delete method
	 * with type as Blank 3) Audit the record in to AuditHeader and
	 * AdtRMTGenDesignations by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}

		GeneralDesignation generalDesignation = (GeneralDesignation) auditHeader
				.getAuditDetail().getModelData();
		getGeneralDesignationDAO().delete(generalDesignation, "");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getGeneralDesignationById fetch the details by using
	 * GeneralDesignationDAO's getGeneralDesignationById method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return GeneralDesignation
	 */
	@Override
	public GeneralDesignation getGeneralDesignationById(String id) {
		return getGeneralDesignationDAO()
				.getGeneralDesignationById(id, "_View");
	}

	/**
	 * getApprovedGeneralDesignationById fetch the details by using
	 * GeneralDesignationDAO's getGeneralDesignationById method . with parameter
	 * id and type as blank. it fetches the approved records from the
	 * RMTGenDesignations.
	 * 
	 * @param id
	 *            (String)
	 * @return GeneralDesignation
	 */
	public GeneralDesignation getApprovedGeneralDesignationById(String id) {
		return getGeneralDesignationDAO().getGeneralDesignationById(id, "_AView");
	}

	/**
	 * This method refresh the Record.
	 * 
	 * @param GeneralDesignation
	 *            (generalDesignation)
	 * @return generalDesignation
	 */
	@Override
	public GeneralDesignation refresh(GeneralDesignation generalDesignation) {
		logger.debug("Entering");
		getGeneralDesignationDAO().refresh(generalDesignation);
		getGeneralDesignationDAO().initialize(generalDesignation);
		logger.debug("Leaving");
		return generalDesignation;
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getGeneralDesignationDAO().delete with parameters
	 * generalDesignation,"" b) NEW Add new record in to main table by using
	 * getGeneralDesignationDAO().save with parameters generalDesignation,"" c)
	 * EDIT Update record in the main table by using
	 * getGeneralDesignationDAO().update with parameters generalDesignation,""
	 * 3) Delete the record from the workFlow table by using
	 * getGeneralDesignationDAO().delete with parameters
	 * generalDesignation,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtRMTGenDesignations by using auditHeaderDAO.addAudit(auditHeader) for
	 * Work flow 5) Audit the record in to AuditHeader and AdtRMTGenDesignations
	 * by using auditHeaderDAO.addAudit(auditHeader) based on the transaction
	 * Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");

		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}

		GeneralDesignation generalDesignation = new GeneralDesignation();
		BeanUtils.copyProperties((GeneralDesignation) auditHeader
				.getAuditDetail().getModelData(), generalDesignation);

		if (generalDesignation.getRecordType().equals(
				PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			getGeneralDesignationDAO().delete(generalDesignation, "");

		} else {
			generalDesignation.setRoleCode("");
			generalDesignation.setNextRoleCode("");
			generalDesignation.setTaskId("");
			generalDesignation.setNextTaskId("");
			generalDesignation.setWorkflowId(0);

			if (generalDesignation.getRecordType().equals(
					PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				generalDesignation.setRecordType("");
				getGeneralDesignationDAO().save(generalDesignation, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				generalDesignation.setRecordType("");
				getGeneralDesignationDAO().update(generalDesignation, "");
			}
		}

		getGeneralDesignationDAO().delete(generalDesignation, "_TEMP");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(generalDesignation);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getGeneralDesignationDAO().delete with
	 * parameters generalDesignation,"_Temp" 3) Audit the record in to
	 * AuditHeader and AdtRMTGenDesignations by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}

		GeneralDesignation generalDesignation = (GeneralDesignation) auditHeader
				.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getGeneralDesignationDAO().delete(generalDesignation, "_TEMP");

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
		auditHeader=nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any
	 * mismatch conditions Fetch the error details from
	 * getGeneralDesignationDAO().getErrorDetail with Error ID and language as
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
		GeneralDesignation generalDesignation = (GeneralDesignation) auditDetail
				.getModelData();
		GeneralDesignation tempGeneralDesignation = null;
		if (generalDesignation.isWorkflow()) {
			tempGeneralDesignation = getGeneralDesignationDAO()
					.getGeneralDesignationById(generalDesignation.getId(),
							"_Temp");
		}
		GeneralDesignation befGeneralDesignation = getGeneralDesignationDAO()
				.getGeneralDesignationById(generalDesignation.getId(), "");

		GeneralDesignation old_GeneralDesignation = generalDesignation
				.getBefImage();

		String[] valueParm = new String[1];
		String[] errParm= new String[1];

		valueParm[0] = generalDesignation.getGenDesignation();
		errParm[0] = PennantJavaUtil.getLabel("label_GenDesignation") + ":"+ valueParm[0];

		if (generalDesignation.isNew()) { // for New record or new record into work flow

			if (!generalDesignation.isWorkflow()) {// With out Work flow only new records
				if (befGeneralDesignation != null) { // Record Already Exists in the table
													 // then error
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41001",errParm,null));
				}
			} else { // with work flow
				

				if (generalDesignation.getRecordType().equals(
						PennantConstants.RECORD_TYPE_NEW)) { // if records type
																// is new
					if (befGeneralDesignation != null || tempGeneralDesignation != null) { // if records already
															// exists in the
															// main table
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41001",errParm,null));
					}
				} else { // if records not exists in the Main flow table
					if (befGeneralDesignation == null || tempGeneralDesignation != null) {
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",errParm,null));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!generalDesignation.isWorkflow()) { // With out Work flow for
													// update and delete

				if (befGeneralDesignation == null) { // if records not exists in
														// the main table
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41002",errParm,null));
				}else{

					if (old_GeneralDesignation != null
							&& !old_GeneralDesignation.getLastMntOn().equals(
									befGeneralDesignation.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41003",errParm,null));
						} else {
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41004",errParm,null));
						}
					}
				}
			} else {

				if (tempGeneralDesignation == null) { // if records not exists
														// in the Work flow
														// table
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",errParm,null));
				}

				if (tempGeneralDesignation != null && old_GeneralDesignation != null
						&& !old_GeneralDesignation.getLastMntOn().equals(
								tempGeneralDesignation.getLastMntOn())) {
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",errParm,null));
				}
			}
		}
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
		
		if (StringUtils.trimToEmpty(method).equals("doApprove")
				|| !generalDesignation.isWorkflow()) {
			auditDetail.setBefImage(befGeneralDesignation);
		}
		logger.debug("Leaving");
		return auditDetail;
	}

}