/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : IdentityDetailsServiceImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 05-05-2011 * *
 * Modified Date : 05-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 05-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.service.systemmasters.impl;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.systemmasters.IdentityDetailsDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.IdentityDetails;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.systemmasters.IdentityDetailsService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;

/**
 * Service implementation for methods that depends on <b>IdentityDetails</b>.<br>
 * 
 */
public class IdentityDetailsServiceImpl extends GenericService<IdentityDetails> implements IdentityDetailsService {

	private static Logger logger = LogManager.getLogger(IdentityDetailsServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private IdentityDetailsDAO identityDetailsDAO;

	public IdentityDetailsServiceImpl() {
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

	public IdentityDetailsDAO getIdentityDetailsDAO() {
		return identityDetailsDAO;
	}

	public void setIdentityDetailsDAO(IdentityDetailsDAO identityDetailsDAO) {
		this.identityDetailsDAO = identityDetailsDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table
	 * BMTIdentityType/BMTIdentityType_Temp by using IdentityDetailsDAO's save method b) Update the Record in the table.
	 * based on the module workFlow Configuration. by using IdentityDetailsDAO's update method 3) Audit the record in to
	 * AuditHeader and AdtBMTIdentityType by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader (auditHeader)
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
		IdentityDetails identityDetails = (IdentityDetails) auditHeader.getAuditDetail().getModelData();

		if (identityDetails.isWorkflow()) {
			tableType = "_Temp";
		}

		if (identityDetails.isNewRecord()) {
			identityDetails.setId(getIdentityDetailsDAO().save(identityDetails, tableType));
			auditHeader.getAuditDetail().setModelData(identityDetails);
			auditHeader.setAuditReference(identityDetails.getId());
		} else {
			getIdentityDetailsDAO().update(identityDetails, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * BMTIdentityType by using IdentityDetailsDAO's delete method with type as Blank 3) Audit the record in to
	 * AuditHeader and AdtBMTIdentityType by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader (auditHeader)
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
		IdentityDetails identityDetails = (IdentityDetails) auditHeader.getAuditDetail().getModelData();

		getIdentityDetailsDAO().delete(identityDetails, "");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getIdentityDetailsById fetch the details by using IdentityDetailsDAO's getIdentityDetailsById method.
	 * 
	 * @param id   (String)
	 * @param type (String) ""/_Temp/_View
	 * @return IdentityDetails
	 */
	@Override
	public IdentityDetails getIdentityDetailsById(String id) {
		return getIdentityDetailsDAO().getIdentityDetailsById(id, "_View");
	}

	/**
	 * getApprovedIdentityDetailsById fetch the details by using IdentityDetailsDAO's getIdentityDetailsById method .
	 * with parameter id and type as blank. it fetches the approved records from the BMTIdentityType.
	 * 
	 * @param id (String)
	 * @return IdentityDetails
	 */
	public IdentityDetails getApprovedIdentityDetailsById(String id) {
		return getIdentityDetailsDAO().getIdentityDetailsById(id, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getIdentityDetailsDAO().delete with
	 * parameters identityDetails,"" b) NEW Add new record in to main table by using getIdentityDetailsDAO().save with
	 * parameters identityDetails,"" c) EDIT Update record in the main table by using getIdentityDetailsDAO().update
	 * with parameters identityDetails,"" 3) Delete the record from the workFlow table by using
	 * getIdentityDetailsDAO().delete with parameters identityDetails,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtBMTIdentityType by using auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to
	 * AuditHeader and AdtBMTIdentityType by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader (auditHeader)
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
		IdentityDetails identityDetails = new IdentityDetails();
		BeanUtils.copyProperties((IdentityDetails) auditHeader.getAuditDetail().getModelData(), identityDetails);

		if (identityDetails.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			getIdentityDetailsDAO().delete(identityDetails, "");

		} else {
			identityDetails.setRoleCode("");
			identityDetails.setNextRoleCode("");
			identityDetails.setTaskId("");
			identityDetails.setNextTaskId("");
			identityDetails.setWorkflowId(0);

			if (identityDetails.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				identityDetails.setRecordType("");
				getIdentityDetailsDAO().save(identityDetails, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				identityDetails.setRecordType("");
				getIdentityDetailsDAO().update(identityDetails, "");
			}
		}

		getIdentityDetailsDAO().delete(identityDetails, "_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(identityDetails);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getIdentityDetailsDAO().delete with parameters identityDetails,"_Temp" 3) Audit the
	 * record in to AuditHeader and AdtBMTIdentityType by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doReject(AuditHeader auditHeader) {

		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		IdentityDetails identityDetails = (IdentityDetails) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getIdentityDetailsDAO().delete(identityDetails, "_Temp");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from the auditHeader. 2) fetch the details
	 * from the tables 3) Validate the Record based on the record details. 4) Validate for any business validation.
	 * 
	 * @param AuditHeader (auditHeader)
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
	 * For Validating AuditDetals object getting from Audit Header, if any mismatch conditions Fetch the error details
	 * from getIdentityDetailsDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings then
	 * assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());

		IdentityDetails identityDetails = (IdentityDetails) auditDetail.getModelData();
		IdentityDetails tempIdentityDetails = null;

		if (identityDetails.isWorkflow()) {
			tempIdentityDetails = getIdentityDetailsDAO().getIdentityDetailsById(identityDetails.getId(), "_Temp");
		}

		IdentityDetails befIdentityDetails = getIdentityDetailsDAO().getIdentityDetailsById(identityDetails.getId(),
				"");
		IdentityDetails oldIdentityDetails = identityDetails.getBefImage();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = identityDetails.getIdentityType();
		errParm[0] = PennantJavaUtil.getLabel("label_IdentityType") + ":" + valueParm[0];

		if (identityDetails.isNewRecord()) { // for New record or new record into work
			// flow

			if (!identityDetails.isWorkflow()) {// With out Work flow only new
				// records
				if (befIdentityDetails != null) { // Record Already Exists in
					// the table then error
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, null));
				}
			} else { // with work flow

				if (identityDetails.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type
					// is new
					if (befIdentityDetails != null || tempIdentityDetails != null) { // if
						// records already exists
						// in the main table
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, null));
					}
				} else { // if records not exists in the Main flow table
					if (befIdentityDetails == null || tempIdentityDetails != null) {
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!identityDetails.isWorkflow()) { // With out Work flow for
				// update and delete

				if (befIdentityDetails == null) { // if records not exists in
					// the main table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, null));
				} else {

					if (oldIdentityDetails != null
							&& !oldIdentityDetails.getLastMntOn().equals(befIdentityDetails.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm, null));
						} else {
							auditDetail.setErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm, null));
						}
					}
				}

			} else {

				if (tempIdentityDetails == null) { // if records not exists in
					// the Work flow table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
				}
				if (tempIdentityDetails != null && oldIdentityDetails != null
						&& !oldIdentityDetails.getLastMntOn().equals(tempIdentityDetails.getLastMntOn())) {
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !identityDetails.isWorkflow()) {
			auditDetail.setBefImage(befIdentityDetails);
		}
		logger.debug("Leaving");
		return auditDetail;
	}

}