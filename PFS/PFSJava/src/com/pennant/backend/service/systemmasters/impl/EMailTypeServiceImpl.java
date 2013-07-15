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
 * FileName    		:  EMailTypeServiceImpl.java                                                   * 	  
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
import com.pennant.backend.dao.systemmasters.EMailTypeDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.EMailType;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.systemmasters.EMailTypeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>EMailType</b>.<br>
 * 
 */
public class EMailTypeServiceImpl extends GenericService<EMailType> implements
		EMailTypeService {

	private static Logger logger = Logger.getLogger(EMailTypeServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private EMailTypeDAO eMailTypeDAO;

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public EMailTypeDAO getEMailTypeDAO() {
		return eMailTypeDAO;
	}

	public void setEMailTypeDAO(EMailTypeDAO eMailTypeDAO) {
		this.eMailTypeDAO = eMailTypeDAO;
	}

	public EMailType getEMailType() {
		return getEMailTypeDAO().getEMailType();
	}

	public EMailType getNewEMailType() {
		return getEMailTypeDAO().getNewEMailType();
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * BMTEMailTypes/BMTEMailTypes_Temp by using EMailTypeDAO's save method b)
	 * Update the Record in the table. based on the module workFlow
	 * Configuration. by using EMailTypeDAO's update method 3) Audit the record
	 * in to AuditHeader and AdtBMTEMailTypes by using
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
		EMailType eMailType = (EMailType) auditHeader.getAuditDetail()
				.getModelData();

		if (eMailType.isWorkflow()) {
			tableType = "_TEMP";
		}

		if (eMailType.isNew()) {
			eMailType.setId(getEMailTypeDAO().save(eMailType, tableType));
			auditHeader.getAuditDetail().setModelData(eMailType);
			auditHeader.setAuditReference(eMailType.getId());
		} else {
			getEMailTypeDAO().update(eMailType, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table BMTEMailTypes by using EMailTypeDAO's delete method with type as
	 * Blank 3) Audit the record in to AuditHeader and AdtBMTEMailTypes by using
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
		EMailType eMailType = (EMailType) auditHeader.getAuditDetail()
				.getModelData();

		getEMailTypeDAO().delete(eMailType, "");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getEMailTypeById fetch the details by using EMailTypeDAO's
	 * getEMailTypeById method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return EMailType
	 */
	@Override
	public EMailType getEMailTypeById(String id) {
		return getEMailTypeDAO().getEMailTypeById(id, "_View");
	}

	/**
	 * getApprovedEMailTypeById fetch the details by using EMailTypeDAO's
	 * getEMailTypeById method . with parameter id and type as blank. it fetches
	 * the approved records from the BMTEMailTypes.
	 * 
	 * @param id
	 *            (String)
	 * @return EMailType
	 */
	public EMailType getApprovedEMailTypeById(String id) {
		return getEMailTypeDAO().getEMailTypeById(id, "_AView");
	}

	/**
	 * This method refresh the Record.
	 * 
	 * @param EMailType
	 *            (eMailType)
	 * @return eMailType
	 */
	@Override
	public EMailType refresh(EMailType eMailType) {
		logger.debug("Entering");
		getEMailTypeDAO().refresh(eMailType);
		getEMailTypeDAO().initialize(eMailType);
		logger.debug("Leaving");
		return eMailType;
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getEMailTypeDAO().delete with parameters eMailType,"" b) NEW Add
	 * new record in to main table by using getEMailTypeDAO().save with
	 * parameters eMailType,"" c) EDIT Update record in the main table by using
	 * getEMailTypeDAO().update with parameters eMailType,"" 3) Delete the
	 * record from the workFlow table by using getEMailTypeDAO().delete with
	 * parameters eMailType,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtBMTEMailTypes by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow 5) Audit the record in to AuditHeader and AdtBMTEMailTypes by using
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
		EMailType eMailType = new EMailType();
		BeanUtils.copyProperties((EMailType) auditHeader.getAuditDetail()
				.getModelData(), eMailType);

		if (eMailType.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getEMailTypeDAO().delete(eMailType, "");
		} else {
			eMailType.setRoleCode("");
			eMailType.setNextRoleCode("");
			eMailType.setTaskId("");
			eMailType.setNextTaskId("");
			eMailType.setWorkflowId(0);

			if (eMailType.getRecordType().equals(
					PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				eMailType.setRecordType("");
				getEMailTypeDAO().save(eMailType, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				eMailType.setRecordType("");
				getEMailTypeDAO().update(eMailType, "");
			}
		}

		getEMailTypeDAO().delete(eMailType, "_TEMP");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(eMailType);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getEMailTypeDAO().delete with parameters
	 * eMailType,"_Temp" 3) Audit the record in to AuditHeader and
	 * AdtBMTEMailTypes by using auditHeaderDAO.addAudit(auditHeader) for Work
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
		EMailType eMailType = (EMailType) auditHeader.getAuditDetail()
				.getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getEMailTypeDAO().delete(eMailType, "_TEMP");

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
	 * getEMailTypeDAO().getErrorDetail with Error ID and language as
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

		EMailType eMailType = (EMailType) auditDetail.getModelData();
		EMailType tempEMailType = null;

		if (eMailType.isWorkflow()) {
			tempEMailType = getEMailTypeDAO().getEMailTypeById(
					eMailType.getId(), "_Temp");
		}

		EMailType befEMailType = getEMailTypeDAO().getEMailTypeById(
				eMailType.getId(), "");
		EMailType old_EMailType = eMailType.getBefImage();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = eMailType.getEmailTypeCode();
		errParm[0] = PennantJavaUtil.getLabel("label_EmailTypeCode") + ":"
				+ valueParm[0];

		if (eMailType.isNew()) { // for New record or new record into work flow

			if (!eMailType.isWorkflow()) {// With out Work flow only new records
				if (befEMailType != null) { // Record Already Exists in the
					// table then error
					auditDetail
							.setErrorDetail(new ErrorDetails(
									PennantConstants.KEY_FIELD, "41001",
									errParm, null));
				}
			} else { // with work flow

				if (eMailType.getRecordType().equals(
						PennantConstants.RECORD_TYPE_NEW)) { // if records type
					// is new
					if (befEMailType != null || tempEMailType != null) { // if
													  // records already exists
          											 // in the main table
						auditDetail.setErrorDetail(new ErrorDetails(
								PennantConstants.KEY_FIELD, "41001", errParm,
								null));
					}
				} else { // if records not exists in the Main flow table
					if (befEMailType == null || tempEMailType != null) {
						auditDetail.setErrorDetail(new ErrorDetails(
								PennantConstants.KEY_FIELD, "41005", errParm,
								null));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!eMailType.isWorkflow()) { // With out Work flow for update and
				// delete

				if (befEMailType == null) { // if records not exists in the main
					// table
					auditDetail
							.setErrorDetail(new ErrorDetails(
									PennantConstants.KEY_FIELD, "41002",
									errParm, null));
				} else {

					if (old_EMailType != null
							&& !old_EMailType.getLastMntOn().equals(
									befEMailType.getLastMntOn())) {
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

				if (tempEMailType == null) { // if records not exists in the
					// Work flow table
					auditDetail
							.setErrorDetail(new ErrorDetails(
									PennantConstants.KEY_FIELD, "41005",
									errParm, null));
				}

				if (tempEMailType != null
						&& old_EMailType != null
						&& !old_EMailType.getLastMntOn().equals(
								tempEMailType.getLastMntOn())) {
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
				|| !eMailType.isWorkflow()) {
			auditDetail.setBefImage(befEMailType);
		}
		logger.debug("Leaving");
		return auditDetail;
	}

}