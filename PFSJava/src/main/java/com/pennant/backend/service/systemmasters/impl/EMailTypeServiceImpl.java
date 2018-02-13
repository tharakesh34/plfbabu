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

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.systemmasters.EMailTypeDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.EMailType;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.systemmasters.EMailTypeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>EMailType</b>.<br>
 * 
 */
public class EMailTypeServiceImpl extends GenericService<EMailType> implements
		EMailTypeService {

	private static Logger logger = Logger.getLogger(EMailTypeServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private EMailTypeDAO eMailTypeDAO;

	public EMailTypeServiceImpl() {
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

	public EMailTypeDAO getEMailTypeDAO() {
		return eMailTypeDAO;
	}

	public void setEMailTypeDAO(EMailTypeDAO eMailTypeDAO) {
		this.eMailTypeDAO = eMailTypeDAO;
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
		
		EMailType eMailType = (EMailType) auditHeader.getAuditDetail()
				.getModelData();
		TableType tableType = TableType.MAIN_TAB;
		
		if (eMailType.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
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

		getEMailTypeDAO().delete(eMailType, TableType.MAIN_TAB);

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

		getEMailTypeDAO().delete(eMailType, TableType.TEMP_TAB);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(eMailType.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(eMailTypeDAO.getEMailTypeById(eMailType.getEmailTypeCode(), ""));
		}

		if (eMailType.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getEMailTypeDAO().delete(eMailType, TableType.MAIN_TAB);
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
				getEMailTypeDAO().save(eMailType, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				eMailType.setRecordType("");
				getEMailTypeDAO().update(eMailType, TableType.MAIN_TAB);
			}
		}

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
		getEMailTypeDAO().delete(eMailType, TableType.TEMP_TAB);

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
				auditHeader.getUsrLanguage());
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
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug("Entering");

		// Get the model object.
		EMailType eMailType = (EMailType) auditDetail.getModelData();
		String code = eMailType.getEmailTypeCode();

		// Check the unique keys.
		if (eMailType.isNew()
				&& PennantConstants.RECORD_TYPE_NEW.equals(eMailType.getRecordType())
				&& eMailTypeDAO.isDuplicateKey(code, eMailType.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[1];
			parameters[0] = PennantJavaUtil.getLabel("label_EmailTypeCode") + ": " + code;

			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41014", parameters, null));
		}
		
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug("Leaving");
		return auditDetail;
	
	}
}