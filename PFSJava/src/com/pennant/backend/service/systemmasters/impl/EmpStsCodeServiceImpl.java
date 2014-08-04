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
 * FileName    		:  EmpStsCodeServiceImpl.java                                                   * 	  
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

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.systemmasters.EmpStsCodeDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.EmpStsCode;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.systemmasters.EmpStsCodeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>EmpStsCode</b>.<br>
 * 
 */
public class EmpStsCodeServiceImpl extends GenericService<EmpStsCode> implements
		EmpStsCodeService {

	private static Logger logger = Logger.getLogger(EmpStsCodeServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private EmpStsCodeDAO empStsCodeDAO;

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public EmpStsCodeDAO getEmpStsCodeDAO() {
		return empStsCodeDAO;
	}

	public void setEmpStsCodeDAO(EmpStsCodeDAO empStsCodeDAO) {
		this.empStsCodeDAO = empStsCodeDAO;
	}

	public EmpStsCode getEmpStsCode() {
		return getEmpStsCodeDAO().getEmpStsCode();
	}

	public EmpStsCode getNewEmpStsCode() {
		return getEmpStsCodeDAO().getNewEmpStsCode();
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * BMTEmpStsCodes/BMTEmpStsCodes_Temp by using EmpStsCodeDAO's save method
	 * b) Update the Record in the table. based on the module workFlow
	 * Configuration. by using EmpStsCodeDAO's update method 3) Audit the record
	 * in to AuditHeader and AdtBMTEmpStsCodes by using
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
		EmpStsCode empStsCode = (EmpStsCode) auditHeader.getAuditDetail()
				.getModelData();

		if (empStsCode.isWorkflow()) {
			tableType = "_TEMP";
		}

		if (empStsCode.isNew()) {
			empStsCode.setId(getEmpStsCodeDAO().save(empStsCode, tableType));
			auditHeader.getAuditDetail().setModelData(empStsCode);
			auditHeader.setAuditReference(empStsCode.getId());
		} else {
			getEmpStsCodeDAO().update(empStsCode, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table BMTEmpStsCodes by using EmpStsCodeDAO's delete method with type as
	 * Blank 3) Audit the record in to AuditHeader and AdtBMTEmpStsCodes by
	 * using auditHeaderDAO.addAudit(auditHeader)
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
		EmpStsCode empStsCode = (EmpStsCode) auditHeader.getAuditDetail()
				.getModelData();
		getEmpStsCodeDAO().delete(empStsCode, "");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getEmpStsCodeById fetch the details by using EmpStsCodeDAO's
	 * getEmpStsCodeById method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return EmpStsCode
	 */
	@Override
	public EmpStsCode getEmpStsCodeById(String id) {
		return getEmpStsCodeDAO().getEmpStsCodeById(id, "_View");
	}

	/**
	 * getApprovedEmpStsCodeById fetch the details by using EmpStsCodeDAO's
	 * getEmpStsCodeById method . with parameter id and type as blank. it
	 * fetches the approved records from the BMTEmpStsCodes.
	 * 
	 * @param id
	 *            (String)
	 * @return EmpStsCode
	 */
	public EmpStsCode getApprovedEmpStsCodeById(String id) {
		return getEmpStsCodeDAO().getEmpStsCodeById(id, "_AView");
	}

	/**
	 * This method refresh the Record.
	 * 
	 * @param EmpStsCode
	 *            (empStsCode)
	 * @return empStsCode
	 */
	@Override
	public EmpStsCode refresh(EmpStsCode empStsCode) {
		logger.debug("Entering");
		getEmpStsCodeDAO().refresh(empStsCode);
		getEmpStsCodeDAO().initialize(empStsCode);
		logger.debug("Leaving");
		return empStsCode;
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getEmpStsCodeDAO().delete with parameters empStsCode,"" b) NEW Add
	 * new record in to main table by using getEmpStsCodeDAO().save with
	 * parameters empStsCode,"" c) EDIT Update record in the main table by using
	 * getEmpStsCodeDAO().update with parameters empStsCode,"" 3) Delete the
	 * record from the workFlow table by using getEmpStsCodeDAO().delete with
	 * parameters empStsCode,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtBMTEmpStsCodes by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow 5) Audit the record in to AuditHeader and AdtBMTEmpStsCodes by using
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
		EmpStsCode empStsCode = new EmpStsCode();
		BeanUtils.copyProperties((EmpStsCode) auditHeader.getAuditDetail()
				.getModelData(), empStsCode);

		if (empStsCode.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			getEmpStsCodeDAO().delete(empStsCode, "");
		} else {
			empStsCode.setRoleCode("");
			empStsCode.setNextRoleCode("");
			empStsCode.setTaskId("");
			empStsCode.setNextTaskId("");
			empStsCode.setWorkflowId(0);

			if (empStsCode.getRecordType().equals(
					PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				empStsCode.setRecordType("");
				getEmpStsCodeDAO().save(empStsCode, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				empStsCode.setRecordType("");
				getEmpStsCodeDAO().update(empStsCode, "");
			}
		}
		getEmpStsCodeDAO().delete(empStsCode, "_TEMP");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(empStsCode);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getEmpStsCodeDAO().delete with parameters
	 * empStsCode,"_Temp" 3) Audit the record in to AuditHeader and
	 * AdtBMTEmpStsCodes by using auditHeaderDAO.addAudit(auditHeader) for Work
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
		EmpStsCode empStsCode = (EmpStsCode) auditHeader.getAuditDetail()
				.getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getEmpStsCodeDAO().delete(empStsCode, "_TEMP");

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
	 * getEmpStsCodeDAO().getErrorDetail with Error ID and language as
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

		EmpStsCode empStsCode = (EmpStsCode) auditDetail.getModelData();
		EmpStsCode tempEmpStsCode = null;

		if (empStsCode.isWorkflow()) {
			tempEmpStsCode = getEmpStsCodeDAO().getEmpStsCodeById(
					empStsCode.getId(), "_Temp");
		}

		EmpStsCode befEmpStsCode = getEmpStsCodeDAO().getEmpStsCodeById(
				empStsCode.getId(), "");
		EmpStsCode old_EmpStsCode = empStsCode.getBefImage();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = empStsCode.getEmpStsCode();
		errParm[0] = PennantJavaUtil.getLabel("label_EmpStsCode") + ":"
				+ valueParm[0];

		if (empStsCode.isNew()) { // for New record or new record into work flow

			if (!empStsCode.isWorkflow()) {// With out Work flow only new
				// records
				if (befEmpStsCode != null) { // Record Already Exists in the
					// table then error
					auditDetail
							.setErrorDetail(new ErrorDetails(
									PennantConstants.KEY_FIELD, "41001",
									errParm, null));
				}
			} else { // with work flow

				if (empStsCode.getRecordType().equals(
						PennantConstants.RECORD_TYPE_NEW)) { // if records type
					// is new
					if (befEmpStsCode != null || tempEmpStsCode != null) { // if
						  								// records already exists
							 							// in the main table
						auditDetail.setErrorDetail(new ErrorDetails(
								PennantConstants.KEY_FIELD, "41001", errParm,
								null));
					}
				} else { // if records not exists in the Main flow table
					if (befEmpStsCode == null || tempEmpStsCode != null) {
						auditDetail.setErrorDetail(new ErrorDetails(
								PennantConstants.KEY_FIELD, "41005", errParm,
								null));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!empStsCode.isWorkflow()) { // With out Work flow for update and
				// delete

				if (befEmpStsCode == null) { // if records not exists in the
					// main table
					auditDetail
							.setErrorDetail(new ErrorDetails(
									PennantConstants.KEY_FIELD, "41002",
									errParm, null));
				} else {
					if (old_EmpStsCode != null
							&& !old_EmpStsCode.getLastMntOn().equals(
									befEmpStsCode.getLastMntOn())) {
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
				if (tempEmpStsCode == null) { // if records not exists in the
					// Work flow table
					auditDetail
							.setErrorDetail(new ErrorDetails(
									PennantConstants.KEY_FIELD, "41005",
									errParm, null));
				}
				if (tempEmpStsCode != null
						&& old_EmpStsCode != null
						&& !old_EmpStsCode.getLastMntOn().equals(
								tempEmpStsCode.getLastMntOn())) {
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
				|| !empStsCode.isWorkflow()) {
			auditDetail.setBefImage(befEmpStsCode);
		}
		logger.debug("Leaving");
		return auditDetail;
	}

}