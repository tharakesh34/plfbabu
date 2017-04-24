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
 * FileName    		:  GeneralDepartmentServiceImpl.java                                                   * 	  
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
import com.pennant.backend.dao.systemmasters.GeneralDepartmentDAO;
import com.pennant.backend.dao.systemmasters.impl.GeneralDepartmentDAOImpl;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.GeneralDepartment;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.systemmasters.GeneralDepartmentService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>GeneralDepartment</b>.<br>
 * 
 */
public class GeneralDepartmentServiceImpl extends GenericService<GeneralDepartment> implements GeneralDepartmentService {

	private static Logger logger = Logger.getLogger(GeneralDepartmentDAOImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private GeneralDepartmentDAO generalDepartmentDAO;

	public GeneralDepartmentServiceImpl() {
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

	public GeneralDepartmentDAO getGeneralDepartmentDAO() {
		return generalDepartmentDAO;
	}
	public void setGeneralDepartmentDAO(
			GeneralDepartmentDAO generalDepartmentDAO) {
		this.generalDepartmentDAO = generalDepartmentDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * RMTGenDepartments/RMTGenDepartments_Temp by using GeneralDepartmentDAO's
	 * save method b) Update the Record in the table. based on the module
	 * workFlow Configuration. by using GeneralDepartmentDAO's update method 3)
	 * Audit the record in to AuditHeader and AdtRMTGenDepartments by using
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
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}

		TableType tableType = TableType.MAIN_TAB;
		GeneralDepartment generalDepartment = (GeneralDepartment) auditHeader
				.getAuditDetail().getModelData();

		if (generalDepartment.isWorkflow()) {
			tableType=TableType.TEMP_TAB;
		}
		if (generalDepartment.isNew()) {
			generalDepartment.setGenDepartment(getGeneralDepartmentDAO().save(
					generalDepartment, tableType));
			auditHeader.getAuditDetail().setModelData(generalDepartment);
			auditHeader.setAuditReference(generalDepartment
					.getGenDepartment());
		} else {
			getGeneralDepartmentDAO().update(generalDepartment, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table RMTGenDepartments by using GeneralDepartmentDAO's delete method
	 * with type as Blank 3) Audit the record in to AuditHeader and
	 * AdtRMTGenDepartments by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader);
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}

		GeneralDepartment generalDepartment = (GeneralDepartment) auditHeader
				.getAuditDetail().getModelData();
		getGeneralDepartmentDAO().delete(generalDepartment, TableType.MAIN_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getGeneralDepartmentById fetch the details by using
	 * GeneralDepartmentDAO's getGeneralDepartmentById method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return GeneralDepartment
	 */
	@Override
	public GeneralDepartment getGeneralDepartmentById(String id) {
		return getGeneralDepartmentDAO().getGeneralDepartmentById(id, "_View");
	}

	/**
	 * getApprovedGeneralDepartmentById fetch the details by using
	 * GeneralDepartmentDAO's getGeneralDepartmentById method . with parameter
	 * id and type as blank. it fetches the approved records from the
	 * RMTGenDepartments.
	 * 
	 * @param id
	 *            (String)
	 * @return GeneralDepartment
	 */
	public GeneralDepartment getApprovedGeneralDepartmentById(String id) {
		return getGeneralDepartmentDAO().getGeneralDepartmentById(id, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getGeneralDepartmentDAO().delete with parameters
	 * generalDepartment,"" b) NEW Add new record in to main table by using
	 * getGeneralDepartmentDAO().save with parameters generalDepartment,"" c)
	 * EDIT Update record in the main table by using
	 * getGeneralDepartmentDAO().update with parameters generalDepartment,"" 3)
	 * Delete the record from the workFlow table by using
	 * getGeneralDepartmentDAO().delete with parameters
	 * generalDepartment,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtRMTGenDepartments by using auditHeaderDAO.addAudit(auditHeader) for
	 * Work flow 5) Audit the record in to AuditHeader and AdtRMTGenDepartments
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
		auditHeader = businessValidation(auditHeader);
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}

		GeneralDepartment generalDepartment = new GeneralDepartment();
		BeanUtils.copyProperties((GeneralDepartment) auditHeader
				.getAuditDetail().getModelData(), generalDepartment);
		
		getGeneralDepartmentDAO().delete(generalDepartment, TableType.TEMP_TAB);
		
		if (!PennantConstants.RECORD_TYPE_NEW.equals(generalDepartment.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(generalDepartmentDAO.getGeneralDepartmentById(generalDepartment.getGenDepartment(), ""));
		}

		if (generalDepartment.getRecordType().equals(
				PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			getGeneralDepartmentDAO().delete(generalDepartment, TableType.MAIN_TAB);

		} else {
			generalDepartment.setRoleCode("");
			generalDepartment.setNextRoleCode("");
			generalDepartment.setTaskId("");
			generalDepartment.setNextTaskId("");
			generalDepartment.setWorkflowId(0);

			if (generalDepartment.getRecordType().equals(
					PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				generalDepartment.setRecordType("");
				getGeneralDepartmentDAO().save(generalDepartment, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				generalDepartment.setRecordType("");
				getGeneralDepartmentDAO().update(generalDepartment, TableType.MAIN_TAB);
			}
		}

		
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(generalDepartment);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getGeneralDepartmentDAO().delete with
	 * parameters generalDepartment,"_Temp" 3) Audit the record in to
	 * AuditHeader and AdtRMTGenDepartments by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader);
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}

		GeneralDepartment generalDepartment = (GeneralDepartment) auditHeader
				.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getGeneralDepartmentDAO().delete(generalDepartment, TableType.TEMP_TAB);

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
	 * getGeneralDepartmentDAO().getErrorDetail with Error ID and language as
	 * parameters. if any error/Warnings then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug("Entering");

		// Get the model object.
		GeneralDepartment generalDepartment = (GeneralDepartment) auditDetail
				.getModelData();
		// Check the unique keys.
		if (generalDepartment.isNew()
				&& PennantConstants.RECORD_TYPE_NEW.equals(generalDepartment.getRecordType())
				&& generalDepartmentDAO.isDuplicateKey(generalDepartment.getGenDepartment(),
						generalDepartment.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[1];
			

			parameters[0] = PennantJavaUtil.getLabel("label_GenDepartment") + ":"+ generalDepartment.getGenDepartment();
			auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41001", parameters, null));
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug("Leaving");
		return auditDetail;
	}
	
/*	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage,
			String method) {
		logger.debug("Entering");
		GeneralDepartment generalDepartment = (GeneralDepartment) auditDetail
				.getModelData();
		GeneralDepartment tempGeneralDepartment = null;
		if (generalDepartment.isWorkflow()) {
			tempGeneralDepartment = getGeneralDepartmentDAO()
					.getGeneralDepartmentById(generalDepartment.getId(),
							"_Temp");
		}
		GeneralDepartment befGeneralDepartment = getGeneralDepartmentDAO()
				.getGeneralDepartmentById(generalDepartment.getId(), "");

		GeneralDepartment oldGeneralDepartment = generalDepartment
				.getBefImage();

		String[] valueParm = new String[1];
		String[] errParm= new String[1];

		valueParm[0] = generalDepartment.getGenDepartment();
		errParm[0] = PennantJavaUtil.getLabel("label_GenDepartment") + ":"+ valueParm[0];
		
		if (generalDepartment.isNew()) { // for New record or new record into work flow

			if (!generalDepartment.isWorkflow()) {// With out Work flow only new records
				if (befGeneralDepartment != null) { // Record Already Exists in the table
													// then error
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41001",errParm,null));
				}
			} else { // with work flow

				if (generalDepartment.getRecordType().equals(
						PennantConstants.RECORD_TYPE_NEW)) { // if records type
																// is new
					if (befGeneralDepartment != null || tempGeneralDepartment != null) { // if records already
														// exists in the main
														// table
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41001",errParm,null));
					}
				} else { // if records not exists in the Main flow table
					if (befGeneralDepartment == null || tempGeneralDepartment != null) {
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",errParm,null));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!generalDepartment.isWorkflow()) { // With out Work flow for
													// update and delete

				if (befGeneralDepartment == null) { // if records not exists in
													// the main table
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41002",errParm,null));
				}else{

					if (oldGeneralDepartment != null
							&& !oldGeneralDepartment.getLastMntOn().equals(
									befGeneralDepartment.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41003",errParm,null));
						} else {
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41004",errParm,null));
						}
					}
				}

			} else {

				if (tempGeneralDepartment == null) { // if records not exists in
														// the Work flow table
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",errParm,null));
				}

				if (tempGeneralDepartment != null && oldGeneralDepartment != null
						&& !oldGeneralDepartment.getLastMntOn().equals(
								tempGeneralDepartment.getLastMntOn())) {
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",errParm,null));
				}

			}
		}
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
		
		if ("doApprove".equals(StringUtils.trimToEmpty(method))
				|| !generalDepartment.isWorkflow()) {
			auditDetail.setBefImage(befGeneralDepartment);
		}
		logger.debug("Leaving");
		return auditDetail;
	}*/

}