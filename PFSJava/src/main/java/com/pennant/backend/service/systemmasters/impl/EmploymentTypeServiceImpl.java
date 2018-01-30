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
 * FileName    		:  EmploymentTypeServiceImpl.java                                                   * 	  
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
import com.pennant.backend.dao.systemmasters.EmploymentTypeDAO;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.EmploymentType;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.systemmasters.EmploymentTypeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>EmploymentType</b>.<br>
 * 
 */
public class EmploymentTypeServiceImpl extends GenericService<EmploymentType> implements EmploymentTypeService {

	private static Logger logger = Logger.getLogger(EmploymentTypeServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private EmploymentTypeDAO employmentTypeDAO;

	public EmploymentTypeServiceImpl() {
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

	public EmploymentTypeDAO getEmploymentTypeDAO() {
		return employmentTypeDAO;
	}
	public void setEmploymentTypeDAO(EmploymentTypeDAO employmentTypeDAO) {
		this.employmentTypeDAO = employmentTypeDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * RMTEmpTypes/RMTEmpTypes_Temp by using EmploymentTypeDAO's save method b)
	 * Update the Record in the table. based on the module workFlow
	 * Configuration. by using EmploymentTypeDAO's update method 3) Audit the
	 * record in to AuditHeader and AdtRMTEmpTypes by using
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
		
		EmploymentType employmentType = (EmploymentType) auditHeader.getAuditDetail().getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (employmentType.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (employmentType.isNew()) {
			employmentType.setEmpType(getEmploymentTypeDAO().save(employmentType, tableType));
			auditHeader.getAuditDetail().setModelData(employmentType);
			auditHeader.setAuditReference(employmentType.getEmpType());
		} else {
			getEmploymentTypeDAO().update(employmentType, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table RMTEmpTypes by using EmploymentTypeDAO's delete method with type as
	 * Blank 3) Audit the record in to AuditHeader and AdtRMTEmpTypes by using
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
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}

		EmploymentType employmentType = (EmploymentType) auditHeader
				.getAuditDetail().getModelData();
		getEmploymentTypeDAO().delete(employmentType, TableType.MAIN_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getEmploymentTypeById fetch the details by using EmploymentTypeDAO's
	 * getEmploymentTypeById method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return EmploymentType
	 */
	@Override
	public EmploymentType getEmploymentTypeById(String id) {
		return getEmploymentTypeDAO().getEmploymentTypeById(id, "_View");
	}

	/**
	 * getApprovedEmploymentTypeById fetch the details by using
	 * EmploymentTypeDAO's getEmploymentTypeById method . with parameter id and
	 * type as blank. it fetches the approved records from the RMTEmpTypes.
	 * 
	 * @param id
	 *            (String)
	 * @return EmploymentType
	 */
	public EmploymentType getApprovedEmploymentTypeById(String id) {
		return getEmploymentTypeDAO().getEmploymentTypeById(id, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getEmploymentTypeDAO().delete with parameters employmentType,"" b)
	 * NEW Add new record in to main table by using getEmploymentTypeDAO().save
	 * with parameters employmentType,"" c) EDIT Update record in the main table
	 * by using getEmploymentTypeDAO().update with parameters employmentType,""
	 * 3) Delete the record from the workFlow table by using
	 * getEmploymentTypeDAO().delete with parameters employmentType,"_Temp" 4)
	 * Audit the record in to AuditHeader and AdtRMTEmpTypes by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in
	 * to AuditHeader and AdtRMTEmpTypes by using
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
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}

		EmploymentType employmentType = new EmploymentType();
		BeanUtils.copyProperties((EmploymentType) auditHeader.getAuditDetail().getModelData(), employmentType);
		
		getEmploymentTypeDAO().delete(employmentType, TableType.TEMP_TAB);
		
		if (!PennantConstants.RECORD_TYPE_NEW.equals(employmentType.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(employmentTypeDAO.getEmploymentTypeById(employmentType.getEmpType(), ""));
		}

		if (employmentType.getRecordType().equals(
				PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			getEmploymentTypeDAO().delete(employmentType, TableType.MAIN_TAB);

		} else {
			employmentType.setRoleCode("");
			employmentType.setNextRoleCode("");
			employmentType.setTaskId("");
			employmentType.setNextTaskId("");
			employmentType.setWorkflowId(0);

			if (employmentType.getRecordType().equals(
					PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				employmentType.setRecordType("");
				getEmploymentTypeDAO().save(employmentType, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				employmentType.setRecordType("");
				getEmploymentTypeDAO().update(employmentType, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(employmentType);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getEmploymentTypeDAO().delete with parameters
	 * employmentType,"_Temp" 3) Audit the record in to AuditHeader and
	 * AdtRMTEmpTypes by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow
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

		EmploymentType employmentType = (EmploymentType) auditHeader
				.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getEmploymentTypeDAO().delete(employmentType, TableType.TEMP_TAB);

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
	 * getEmploymentTypeDAO().getErrorDetail with Error ID and language as
	 * parameters. if any error/Warnings then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug("Entering");

		// Get the model object.
		EmploymentType employmentType = (EmploymentType) auditDetail.getModelData();
		String code = employmentType.getEmpType();

		// Check the unique keys.
		if (employmentType.isNew()
				&& PennantConstants.RECORD_TYPE_NEW.equals(employmentType.getRecordType())
				&& employmentTypeDAO.isDuplicateKey(code, employmentType.isWorkflow() ? TableType.BOTH_TAB
						: TableType.MAIN_TAB)) {
			String[] parameters = new String[1];
			parameters[0] = PennantJavaUtil.getLabel("label_EmpType") + ": " + code;

			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41014", parameters, null));
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug("Leaving");
		return auditDetail;
	}
}