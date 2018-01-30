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
 * FileName    		:  DepartmentServiceImpl.java                                                   * 	  
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
import com.pennant.backend.dao.systemmasters.DepartmentDAO;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.Department;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.systemmasters.DepartmentService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>Department</b>.<br>
 * 
 */
public class DepartmentServiceImpl extends GenericService<Department> implements
		DepartmentService {

	private static Logger logger = Logger.getLogger(DepartmentServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private DepartmentDAO departmentDAO;

	public DepartmentServiceImpl() {
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

	public DepartmentDAO getDepartmentDAO() {
		return departmentDAO;
	}

	public void setDepartmentDAO(DepartmentDAO departmentDAO) {
		this.departmentDAO = departmentDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * BMTDepartments/BMTDepartments_Temp by using DepartmentDAO's save method
	 * b) Update the Record in the table. based on the module workFlow
	 * Configuration. by using DepartmentDAO's update method 3) Audit the record
	 * in to AuditHeader and AdtBMTDepartments by using
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
		Department department = (Department) auditHeader.getAuditDetail()
				.getModelData();
		TableType tableType = TableType.MAIN_TAB;
		if (department.isWorkflow()) {
			tableType=TableType.TEMP_TAB;
		}

		if (department.isNew()) {
			department.setId(getDepartmentDAO().save(department, tableType));
			auditHeader.getAuditDetail().setModelData(department);
			auditHeader.setAuditReference(department.getId());
		} else {
			getDepartmentDAO().update(department, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table BMTDepartments by using DepartmentDAO's delete method with type as
	 * Blank 3) Audit the record in to AuditHeader and AdtBMTDepartments by
	 * using auditHeaderDAO.addAudit(auditHeader)
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
		Department department = (Department) auditHeader.getAuditDetail()
				.getModelData();
		getDepartmentDAO().delete(department, TableType.MAIN_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getDepartmentById fetch the details by using DepartmentDAO's
	 * getDepartmentById method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return Department
	 */
	@Override
	public Department getDepartmentById(String id) {
		return getDepartmentDAO().getDepartmentById(id, "_View");
	}

	/**
	 * getApprovedDepartmentById fetch the details by using DepartmentDAO's
	 * getDepartmentById method . with parameter id and type as blank. it
	 * fetches the approved records from the BMTDepartments.
	 * 
	 * @param id
	 *            (String)
	 * @return Department
	 */
	public Department getApprovedDepartmentById(String id) {
		return getDepartmentDAO().getDepartmentById(id, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getDepartmentDAO().delete with parameters department,"" b) NEW Add
	 * new record in to main table by using getDepartmentDAO().save with
	 * parameters department,"" c) EDIT Update record in the main table by using
	 * getDepartmentDAO().update with parameters department,"" 3) Delete the
	 * record from the workFlow table by using getDepartmentDAO().delete with
	 * parameters department,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtBMTDepartments by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow 5) Audit the record in to AuditHeader and AdtBMTDepartments by using
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
		Department department = new Department();
		
		BeanUtils.copyProperties((Department) auditHeader.getAuditDetail()
				.getModelData(), department);
		
		getDepartmentDAO().delete(department, TableType.TEMP_TAB);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(department.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(departmentDAO.getDepartmentById(department.getDeptCode(), ""));
		}

		if (department.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			getDepartmentDAO().delete(department, TableType.MAIN_TAB);

		} else {
			department.setRoleCode("");
			department.setNextRoleCode("");
			department.setTaskId("");
			department.setNextTaskId("");
			department.setWorkflowId(0);

			if (department.getRecordType().equals(
					PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				department.setRecordType("");
				getDepartmentDAO().save(department, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				department.setRecordType("");
				getDepartmentDAO().update(department, TableType.MAIN_TAB);
			}
		}
		
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(department);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getDepartmentDAO().delete with parameters
	 * department,"_Temp" 3) Audit the record in to AuditHeader and
	 * AdtBMTDepartments by using auditHeaderDAO.addAudit(auditHeader) for Work
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
		Department department = (Department) auditHeader.getAuditDetail()
				.getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getDepartmentDAO().delete(department, TableType.TEMP_TAB);

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
		auditHeader = nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any
	 * mismatch conditions Fetch the error details from
	 * getDepartmentDAO().getErrorDetail with Error ID and language as
	 * parameters. if any error/Warnings then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug("Entering");

		// Get the model object.
		Department department = (Department) auditDetail.getModelData();

		// Check the unique keys.
		if (department.isNew()
				&& PennantConstants.RECORD_TYPE_NEW.equals(department.getRecordType())
				&& departmentDAO.isDuplicateKey(department.getDeptCode(),
						department.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[1];
			parameters[0]=PennantJavaUtil.getLabel("label_DeptCode")+":"+department.getDeptCode();
			
			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
		logger.debug("Leaving");
		return auditDetail;
	}
}