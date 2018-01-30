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

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.systemmasters.GeneralDesignationDAO;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.GeneralDesignation;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.systemmasters.GeneralDesignationService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>GeneralDesignation</b>.<br>
 * 
 */
public class GeneralDesignationServiceImpl extends GenericService<GeneralDesignation> implements GeneralDesignationService {

	private static Logger logger = Logger.getLogger(GeneralDesignationServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private GeneralDesignationDAO generalDesignationDAO;

	public GeneralDesignationServiceImpl() {
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

	public GeneralDesignationDAO getGeneralDesignationDAO() {
		return generalDesignationDAO;
	}
	public void setGeneralDesignationDAO(
			GeneralDesignationDAO generalDesignationDAO) {
		this.generalDesignationDAO = generalDesignationDAO;
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

		GeneralDesignation generalDesignation = (GeneralDesignation) auditHeader
				.getAuditDetail().getModelData();
		
		TableType tableType = TableType.MAIN_TAB;
		if (generalDesignation.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (generalDesignation.isNew()) {
			generalDesignation.setGenDesignation(getGeneralDesignationDAO()
					.save(generalDesignation, tableType));
			auditHeader.getAuditDetail().setModelData(generalDesignation);
			auditHeader.setAuditReference(generalDesignation
					.getGenDesignation());
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
		getGeneralDesignationDAO().delete(generalDesignation, TableType.MAIN_TAB);

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
		
		getGeneralDesignationDAO().delete(generalDesignation, TableType.TEMP_TAB);
		
		if (!PennantConstants.RECORD_TYPE_NEW.equals(generalDesignation.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(generalDesignationDAO.getGeneralDesignationById(generalDesignation.getGenDesignation(), ""));
		}
		
		if (generalDesignation.getRecordType().equals(
				PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			getGeneralDesignationDAO().delete(generalDesignation, TableType.MAIN_TAB);

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
				getGeneralDesignationDAO().save(generalDesignation, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				generalDesignation.setRecordType("");
				getGeneralDesignationDAO().update(generalDesignation, TableType.MAIN_TAB);
			}
		}

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
		getGeneralDesignationDAO().delete(generalDesignation, TableType.TEMP_TAB);

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
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage){
		logger.debug("Entering");

		// Get the model object.
		GeneralDesignation generalDesignation = (GeneralDesignation) auditDetail.getModelData();
		String code = generalDesignation.getGenDesignation();

		// Check the unique keys.
		if (generalDesignation.isNew()
				&& PennantConstants.RECORD_TYPE_NEW.equals(generalDesignation.getRecordType())
				&& generalDesignationDAO.isDuplicateKey(code, generalDesignation.isWorkflow() ? TableType.BOTH_TAB
						: TableType.MAIN_TAB)) {
			String[] parameters = new String[1];
			parameters[0] = PennantJavaUtil.getLabel("label_GenDesignation") + ": " + code;

			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41014", parameters, null));
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug("Leaving");
		return auditDetail;
	}

}