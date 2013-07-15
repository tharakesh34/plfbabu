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
 * FileName    		:  PhoneTypeServiceImpl.java                                                   * 	  
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

package com.pennant.backend.service.systemmasters.impl;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.systemmasters.PhoneTypeDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.PhoneType;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.systemmasters.PhoneTypeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>PhoneType</b>.<br>
 * 
 */
public class PhoneTypeServiceImpl extends GenericService<PhoneType> implements PhoneTypeService {

	private static Logger logger = Logger.getLogger(PhoneTypeServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private PhoneTypeDAO phoneTypeDAO;

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public PhoneTypeDAO getPhoneTypeDAO() {
		return phoneTypeDAO;
	}

	public void setPhoneTypeDAO(PhoneTypeDAO phoneTypeDAO) {
		this.phoneTypeDAO = phoneTypeDAO;
	}

	public PhoneType getPhoneType() {
		return getPhoneTypeDAO().getPhoneType();
	}

	public PhoneType getNewPhoneType() {
		return getPhoneTypeDAO().getNewPhoneType();
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * BMTPhoneTypes/BMTPhoneTypes_Temp by using PhoneTypeDAO's save method b)
	 * Update the Record in the table. based on the module workFlow
	 * Configuration. by using PhoneTypeDAO's update method 3) Audit the record
	 * in to AuditHeader and AdtBMTPhoneTypes by using
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
		PhoneType phoneType = (PhoneType) auditHeader.getAuditDetail().getModelData();
		if (phoneType.isWorkflow()) {
			tableType = "_TEMP";
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		}

		if (phoneType.isNew()) {
			phoneType.setPhoneTypeCode(getPhoneTypeDAO().save(phoneType,tableType));
			auditHeader.getAuditDetail().setModelData(phoneType);
			auditHeader.setAuditReference(String.valueOf(phoneType.getPhoneTypeCode()));
		} else {
			getPhoneTypeDAO().update(phoneType, tableType);
		}
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table BMTPhoneTypes by using PhoneTypeDAO's delete method with type as
	 * Blank 3) Audit the record in to AuditHeader and AdtBMTPhoneTypes by using
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
		PhoneType phoneType = (PhoneType) auditHeader.getAuditDetail().getModelData();
		getPhoneTypeDAO().delete(phoneType, "");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getPhoneTypeById fetch the details by using PhoneTypeDAO's
	 * getPhoneTypeById method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return PhoneType
	 */
	@Override
	public PhoneType getPhoneTypeById(String id) {
		return getPhoneTypeDAO().getPhoneTypeById(id, "_View");
	}

	/**
	 * getApprovedPhoneTypeById fetch the details by using PhoneTypeDAO's
	 * getPhoneTypeById method . with parameter id and type as blank. it fetches
	 * the approved records from the BMTPhoneTypes.
	 * 
	 * @param id
	 *            (String)
	 * @return PhoneType
	 */
	public PhoneType getApprovedPhoneTypeById(String id) {
		return getPhoneTypeDAO().getPhoneTypeById(id, "_AView");
	}

	/**
	 * This method refresh the Record.
	 * 
	 * @param PhoneType
	 *            (phoneType)
	 * @return phoneType
	 */
	@Override
	public PhoneType refresh(PhoneType phoneType) {
		logger.debug("Entering");
		getPhoneTypeDAO().refresh(phoneType);
		getPhoneTypeDAO().initialize(phoneType);
		logger.debug("Leaving");
		return phoneType;
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getPhoneTypeDAO().delete with parameters phoneType,"" b) NEW Add
	 * new record in to main table by using getPhoneTypeDAO().save with
	 * parameters phoneType,"" c) EDIT Update record in the main table by using
	 * getPhoneTypeDAO().update with parameters phoneType,"" 3) Delete the
	 * record from the workFlow table by using getPhoneTypeDAO().delete with
	 * parameters phoneType,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtBMTPhoneTypes by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow 5) Audit the record in to AuditHeader and AdtBMTPhoneTypes by using
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
		PhoneType phoneType = new PhoneType();
		BeanUtils.copyProperties((PhoneType) auditHeader.getAuditDetail().getModelData(), phoneType);
		if (phoneType.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getPhoneTypeDAO().delete(phoneType, "");

		} else {
			phoneType.setRoleCode("");
			phoneType.setNextRoleCode("");
			phoneType.setTaskId("");
			phoneType.setNextTaskId("");
			phoneType.setWorkflowId(0);

			if (phoneType.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				phoneType.setRecordType("");
				getPhoneTypeDAO().save(phoneType, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				phoneType.setRecordType("");
				getPhoneTypeDAO().update(phoneType, "");
			}
		}
		getPhoneTypeDAO().delete(phoneType, "_TEMP");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(phoneType);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getPhoneTypeDAO().delete with parameters
	 * phoneType,"_Temp" 3) Audit the record in to AuditHeader and
	 * AdtBMTPhoneTypes by using auditHeaderDAO.addAudit(auditHeader) for Work
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
		PhoneType phoneType = (PhoneType) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getPhoneTypeDAO().delete(phoneType, "_TEMP");
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

		PhoneType phoneType = (PhoneType) auditDetail.getModelData();
		PhoneType tempPhoneType = null;

		if (phoneType.isWorkflow()) {
			tempPhoneType = getPhoneTypeDAO().getPhoneTypeById(phoneType.getId(), "_Temp");
		}

		PhoneType befPhoneType = getPhoneTypeDAO().getPhoneTypeById(phoneType.getId(), "");
		PhoneType old_PhoneType = phoneType.getBefImage();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = phoneType.getPhoneTypeCode();
		errParm[0] = PennantJavaUtil.getLabel("label_PhoneType_Code") + ":"+ valueParm[0];

		if (phoneType.isNew()) { // for New record or new record into work flow

			if (!phoneType.isWorkflow()) {// With out Work flow only new records
				if (befPhoneType != null) { // Record Already Exists in the table then error
					auditDetail
					.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001",errParm, null));
				}
			} else { // with work flow
				if (phoneType.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befPhoneType != null || tempPhoneType != null) { // if records already exists in the main table
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,null));
					}
				} else { // if records not exists in the Main flow table
					if (befPhoneType == null || tempPhoneType != null) {
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,null));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!phoneType.isWorkflow()) { // With out Work flow for update and delete

				if (befPhoneType == null) { // if records not exists in the main table
					auditDetail
					.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41002",errParm, null));
				} else {
					if (old_PhoneType != null
							&& !old_PhoneType.getLastMntOn().equals(befPhoneType.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41003",errParm, null));
						} else {
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41004",errParm, null));
						}
					}
				}
			} else {

				if (tempPhoneType == null) { // if records not exists in the Work flow table
					auditDetail
					.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005",errParm, null));
				}

				if (tempPhoneType != null
						&& old_PhoneType != null
						&& !old_PhoneType.getLastMntOn().equals(tempPhoneType.getLastMntOn())) {
					auditDetail
					.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005",errParm, null));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
		if (StringUtils.trimToEmpty(method).equals("doApprove")
				|| !phoneType.isWorkflow()) {
			auditDetail.setBefImage(befPhoneType);
		}
		logger.debug("Leaving");
		return auditDetail;
	}

}