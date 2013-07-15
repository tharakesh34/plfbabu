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
 * FileName    		:  AcademicServiceImpl.java                                                   * 	  
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
import com.pennant.backend.dao.systemmasters.AcademicDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.Academic;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.systemmasters.AcademicService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>Academic</b>.<br>
 * 
 */
public class AcademicServiceImpl extends GenericService<Academic> implements AcademicService {

	private static Logger logger = Logger.getLogger(AcademicServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private AcademicDAO academicDAO;

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public AcademicDAO getAcademicDAO() {
		return academicDAO;
	}

	public void setAcademicDAO(AcademicDAO academicDAO) {
		this.academicDAO = academicDAO;
	}

	public Academic getAcademic() {
		return getAcademicDAO().getAcademic();
	}

	public Academic getNewAcademic() {
		return getAcademicDAO().getNewAcademic();
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * BMTAcademics/BMTAcademics_Temp by using AcademicDAO's save method b)
	 * Update the Record in the table. based on the module workFlow
	 * Configuration. by using AcademicDAO's update method 3) Audit the record
	 * in to AuditHeader and AdtBMTAcademics by using
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
		Academic academic = (Academic) auditHeader.getAuditDetail()
				.getModelData();

		if (academic.isWorkflow()) {
			tableType = "_TEMP";
		}

		if (academic.isNew()) {
			academic.setAcademicID(getAcademicDAO().save(academic, tableType));
			auditHeader.getAuditDetail().setModelData(academic);
			auditHeader.setAuditReference(String.valueOf(academic.getAcademicID()));
		} else {
			getAcademicDAO().update(academic, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table BMTAcademics by using AcademicDAO's delete method with type as
	 * Blank 3) Audit the record in to AuditHeader and AdtBMTAcademics by using
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

		Academic academic = (Academic) auditHeader.getAuditDetail()
				.getModelData();
		getAcademicDAO().delete(academic, "");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getAcademicById fetch the details by using AcademicDAO's getAcademicById
	 * method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return Academic
	 */
	@Override
	public Academic getAcademicById(long academicID) {
		return getAcademicDAO().getAcademicById(academicID, "_View");
	}

	/**
	 * getApprovedAcademicById fetch the details by using AcademicDAO's
	 * getAcademicById method . with parameter id and type as blank. it fetches
	 * the approved records from the BMTAcademics.
	 * 
	 * @param id
	 *            (String)
	 * @return Academic
	 */
	public Academic getApprovedAcademicById(long academicID) {
		return getAcademicDAO().getAcademicById(academicID, "_AView");
	}

	/**
	 * This method refresh the Record.
	 * 
	 * @param Academic
	 *            (academic)
	 * @return academic
	 */
	@Override
	public Academic refresh(Academic academic) {
		logger.debug("Entering");
		getAcademicDAO().refresh(academic);
		getAcademicDAO().initialize(academic);
		logger.debug("Leaving");
		return academic;
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getAcademicDAO().delete with parameters academic,"" b) NEW Add new
	 * record in to main table by using getAcademicDAO().save with parameters
	 * academic,"" c) EDIT Update record in the main table by using
	 * getAcademicDAO().update with parameters academic,"" 3) Delete the record
	 * from the workFlow table by using getAcademicDAO().delete with parameters
	 * academic,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtBMTAcademics by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow 5) Audit the record in to AuditHeader and AdtBMTAcademics by using
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

		Academic academic = new Academic();
		BeanUtils.copyProperties((Academic) auditHeader.getAuditDetail()
				.getModelData(), academic);

		if (academic.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getAcademicDAO().delete(academic, "");
		} else {
			academic.setRoleCode("");
			academic.setNextRoleCode("");
			academic.setTaskId("");
			academic.setNextTaskId("");
			academic.setWorkflowId(0);

			if (academic.getRecordType().equals(
					PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				academic.setRecordType("");
				getAcademicDAO().save(academic, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				academic.setRecordType("");
				getAcademicDAO().update(academic, "");
			}
		}

		getAcademicDAO().delete(academic, "_TEMP");

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(academic);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getAcademicDAO().delete with parameters
	 * academic,"_Temp" 3) Audit the record in to AuditHeader and
	 * AdtBMTAcademics by using auditHeaderDAO.addAudit(auditHeader) for Work
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

		Academic academic = (Academic) auditHeader.getAuditDetail()
				.getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAcademicDAO().delete(academic, "_TEMP");

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
		
		Academic academic = (Academic) auditDetail.getModelData();

		Academic tempAcademic = null;
		if (academic.isWorkflow()) {
			tempAcademic = getAcademicDAO().getAcademic(
					academic.getAcademicLevel(),
					academic.getAcademicDecipline(), "_Temp");
		}

		Academic befAcademic = getAcademicDAO().getAcademic(
				academic.getAcademicLevel(), academic.getAcademicDecipline(),
				" ");
		Academic old_Academic = academic.getBefImage();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];
		
		valueParm[0] = academic.getAcademicLevel();
		valueParm[1] = academic.getAcademicDecipline();

		errParm[0] = PennantJavaUtil.getLabel("label_AcademicLevel") + ":"+ valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_AcademicDecipline") + ":"+valueParm[1];

		if (academic.isNew()) { // for New record or new record into work flow

			if (!academic.isWorkflow()) {// With out Work flow only new records
				if (befAcademic != null) { // Record Already Exists in the table
											// then error
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41001",errParm,null));
				}
			} else { // with work flow
				if (academic.getRecordType().equals(
						PennantConstants.RECORD_TYPE_NEW)) { // if records type
																// is new
					if (befAcademic != null || tempAcademic != null) { // if records already exists in
												// the main table
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41001",errParm,null));
					}
				} else { // if records not exists in the Main flow table
					if (befAcademic == null || tempAcademic != null) {
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",errParm,null));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!academic.isWorkflow()) { // With out Work flow for update and
											// delete
				if (befAcademic == null) { // if records not exists in the main
											// table
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41002",errParm,null));
				} else {
					if (old_Academic != null
							&& !old_Academic.getLastMntOn().equals(
									befAcademic.getLastMntOn())) {
						if (StringUtils.trimToEmpty(
								auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41003",errParm,null));
						} else {
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41004",errParm,null));
						}
					}
				}
			} else {
				if (tempAcademic == null) { // if records not exists in the Work flow table
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",errParm,null));
				}
				
				if ( tempAcademic != null &&  old_Academic != null
						&& !old_Academic.getLastMntOn().equals(
								tempAcademic.getLastMntOn())) {
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",errParm,null));
				}
			}
		}
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
		
		if (StringUtils.trimToEmpty(method).equals("doApprove")
				|| !academic.isWorkflow()) {
			auditDetail.setBefImage(befAcademic);
		}

		logger.debug("Leaving");
		return auditDetail;
	}


}