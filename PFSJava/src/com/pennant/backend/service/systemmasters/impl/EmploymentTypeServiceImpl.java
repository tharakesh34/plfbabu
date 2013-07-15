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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.systemmasters.EmploymentTypeDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.EmploymentType;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.systemmasters.EmploymentTypeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>EmploymentType</b>.<br>
 * 
 */
public class EmploymentTypeServiceImpl extends GenericService<EmploymentType> implements EmploymentTypeService {

	private static Logger logger = Logger
			.getLogger(EmploymentTypeServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private EmploymentTypeDAO employmentTypeDAO;

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

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

	@Override
	public EmploymentType getEmploymentType() {
		return getEmploymentTypeDAO().getEmploymentType();
	}
	@Override
	public EmploymentType getNewEmploymentType() {
		return getEmploymentTypeDAO().getNewEmploymentType();
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

		String tableType = "";
		EmploymentType employmentType = (EmploymentType) auditHeader
				.getAuditDetail().getModelData();

		if (employmentType.isWorkflow()) {
			tableType = "_TEMP";
		}

		if (employmentType.isNew()) {
			employmentType.setEmpType(getEmploymentTypeDAO().save(
					employmentType, tableType));
			auditHeader.getAuditDetail().setModelData(employmentType);
			auditHeader.setAuditReference(String.valueOf(employmentType
					.getEmpType()));
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
		getEmploymentTypeDAO().delete(employmentType, "");

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
	 * This method refresh the Record.
	 * 
	 * @param EmploymentType
	 *            (employmentType)
	 * @return employmentType
	 */
	@Override
	public EmploymentType refresh(EmploymentType employmentType) {
		logger.debug("Entering");
		getEmploymentTypeDAO().refresh(employmentType);
		getEmploymentTypeDAO().initialize(employmentType);
		logger.debug("Leaving");
		return employmentType;
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
		BeanUtils.copyProperties((EmploymentType) auditHeader.getAuditDetail()
				.getModelData(), employmentType);

		if (employmentType.getRecordType().equals(
				PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			getEmploymentTypeDAO().delete(employmentType, "");

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
				getEmploymentTypeDAO().save(employmentType, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				employmentType.setRecordType("");
				getEmploymentTypeDAO().update(employmentType, "");
			}
		}

		getEmploymentTypeDAO().delete(employmentType, "_TEMP");
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
		getEmploymentTypeDAO().delete(employmentType, "_TEMP");

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
	 * getEmploymentTypeDAO().getErrorDetail with Error ID and language as
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

		EmploymentType employmentType = (EmploymentType) auditDetail
				.getModelData();
		EmploymentType tempEmploymentType = null;
		if (employmentType.isWorkflow()) {
			tempEmploymentType = getEmploymentTypeDAO().getEmploymentTypeById(
					employmentType.getId(), "_Temp");
		}
		EmploymentType befEmploymentType = getEmploymentTypeDAO()
				.getEmploymentTypeById(employmentType.getId(), "");

		EmploymentType old_EmploymentType = employmentType.getBefImage();

		String[] valueParm = new String[1];
		String[] errParm= new String[1];

		valueParm[0] = employmentType.getEmpType();
		errParm[0] = PennantJavaUtil.getLabel("label_EmpType") + ":"+ valueParm[0];

		if (employmentType.isNew()) { // for New record or new record into work flow

			if (!employmentType.isWorkflow()) {// With out Work flow only new
												// records
				if (befEmploymentType != null) { // Record Already Exists in the table then error
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41001",errParm,null));
				}
			} else { // with work flow
				

				if (employmentType.getRecordType().equals(
						PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befEmploymentType != null || tempEmploymentType != null) { // if records already
														// exists in the main table
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41001",errParm,null));
					}
				} else { // if records not exists in the Main flow table
					if (befEmploymentType == null || tempEmploymentType != null) {
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",errParm,null));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!employmentType.isWorkflow()) { // With out Work flow for update
												// and delete

				if (befEmploymentType == null) { // if records not exists in the
													// main table
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41002",errParm,null));
				}else{

					if (old_EmploymentType != null
							&& !old_EmploymentType.getLastMntOn().equals(
									befEmploymentType.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41003",errParm,null));
						} else {
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41004",errParm,null));
						}
					}
				}

			} else {

				if (tempEmploymentType == null) { // if records not exists in
													// the Work flow table
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",errParm,null));
				}

				if (tempEmploymentType != null && old_EmploymentType != null
						&& !old_EmploymentType.getLastMntOn().equals(
								tempEmploymentType.getLastMntOn())) {
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",errParm,null));
				}
			}
		}
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
		
		if (StringUtils.trimToEmpty(method).equals("doApprove")
				|| !employmentType.isWorkflow()) {
			auditDetail.setBefImage(befEmploymentType);
		}

		logger.debug("Leaving");
		return auditDetail;
	}

}