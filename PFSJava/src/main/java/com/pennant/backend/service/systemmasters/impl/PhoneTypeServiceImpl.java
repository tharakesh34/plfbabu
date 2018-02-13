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

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.systemmasters.PhoneTypeDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.PhoneType;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.systemmasters.PhoneTypeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>PhoneType</b>.<br>
 * 
 */
public class PhoneTypeServiceImpl extends GenericService<PhoneType> implements PhoneTypeService {

	private static Logger logger = Logger.getLogger(PhoneTypeServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private PhoneTypeDAO phoneTypeDAO;

	public PhoneTypeServiceImpl() {
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

	public PhoneTypeDAO getPhoneTypeDAO() {
		return phoneTypeDAO;
	}

	public void setPhoneTypeDAO(PhoneTypeDAO phoneTypeDAO) {
		this.phoneTypeDAO = phoneTypeDAO;
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
		auditHeader = businessValidation(auditHeader);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		TableType tableType = TableType.MAIN_TAB;
		PhoneType phoneType = (PhoneType) auditHeader.getAuditDetail().getModelData();
		if (phoneType.isWorkflow()) {
			tableType=TableType.TEMP_TAB;
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		}

		if (phoneType.isNew()) {
			phoneType.setPhoneTypeCode(getPhoneTypeDAO().save(phoneType,tableType));
			auditHeader.getAuditDetail().setModelData(phoneType);
			auditHeader.setAuditReference(phoneType.getPhoneTypeCode());
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
		auditHeader = businessValidation(auditHeader);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		PhoneType phoneType = (PhoneType) auditHeader.getAuditDetail().getModelData();
		getPhoneTypeDAO().delete(phoneType, TableType.MAIN_TAB);

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
		auditHeader = businessValidation(auditHeader);

		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		PhoneType phoneType = new PhoneType();
		BeanUtils.copyProperties((PhoneType) auditHeader.getAuditDetail().getModelData(), phoneType);
		
		getPhoneTypeDAO().delete(phoneType, TableType.TEMP_TAB);
		if (!PennantConstants.RECORD_TYPE_NEW.equals(phoneType.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(
					phoneTypeDAO.getPhoneTypeById(phoneType.getId(), ""));
		}
		if (phoneType.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getPhoneTypeDAO().delete(phoneType, TableType.MAIN_TAB);

		} else {
			phoneType.setRoleCode("");
			phoneType.setNextRoleCode("");
			phoneType.setTaskId("");
			phoneType.setNextTaskId("");
			phoneType.setWorkflowId(0);

			if (phoneType.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				phoneType.setRecordType("");
				getPhoneTypeDAO().save(phoneType, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				phoneType.setRecordType("");
				getPhoneTypeDAO().update(phoneType, TableType.MAIN_TAB);
			}
		}
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
		auditHeader = businessValidation(auditHeader);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		PhoneType phoneType = (PhoneType) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getPhoneTypeDAO().delete(phoneType, TableType.TEMP_TAB);
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
	private AuditHeader businessValidation(AuditHeader auditHeader) {
		logger.debug("Entering");
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(),auditHeader.getUsrLanguage());
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
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug("Entering");

		// Get the model object.
		PhoneType phoneType = (PhoneType) auditDetail.getModelData();
		// Check the unique keys.
		if (phoneType.isNew()
				&& PennantConstants.RECORD_TYPE_NEW.equals(phoneType.getRecordType())
				&& phoneTypeDAO.isDuplicateKey(phoneType.getPhoneTypeCode(),
						phoneType.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[1];

			parameters[0] = PennantJavaUtil.getLabel("label_PhoneType_Code") + ":"+ phoneType.getPhoneTypeCode();
			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}
	
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug("Leaving");
		return auditDetail;
	}
}