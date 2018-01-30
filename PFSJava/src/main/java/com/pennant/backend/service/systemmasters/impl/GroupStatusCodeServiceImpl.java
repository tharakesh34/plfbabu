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
 * FileName    		:  GroupStatusCodeServiceImpl.java                                                   * 	  
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
import com.pennant.backend.dao.systemmasters.GroupStatusCodeDAO;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.GroupStatusCode;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.systemmasters.GroupStatusCodeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>GroupStatusCode</b>.<br>
 * 
 */
public class GroupStatusCodeServiceImpl extends GenericService<GroupStatusCode>
		implements GroupStatusCodeService {

	private static Logger logger = Logger.getLogger(GroupStatusCodeServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private GroupStatusCodeDAO groupStatusCodeDAO;

	public GroupStatusCodeServiceImpl() {
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

	public GroupStatusCodeDAO getGroupStatusCodeDAO() {
		return groupStatusCodeDAO;
	}

	public void setGroupStatusCodeDAO(GroupStatusCodeDAO groupStatusCodeDAO) {
		this.groupStatusCodeDAO = groupStatusCodeDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * BMTGrpStatusCodes/BMTGrpStatusCodes_Temp by using GroupStatusCodeDAO's
	 * save method b) Update the Record in the table. based on the module
	 * workFlow Configuration. by using GroupStatusCodeDAO's update method 3)
	 * Audit the record in to AuditHeader and AdtBMTGrpStatusCodes by using
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
		GroupStatusCode groupStatusCode = (GroupStatusCode) auditHeader
				.getAuditDetail().getModelData();

		if (groupStatusCode.isWorkflow()) {
			tableType = "_Temp";
		}

		if (groupStatusCode.isNew()) {
			groupStatusCode.setId(getGroupStatusCodeDAO().save(groupStatusCode,
					tableType));
			auditHeader.getAuditDetail().setModelData(groupStatusCode);
			auditHeader.setAuditReference(groupStatusCode.getId());
		} else {
			getGroupStatusCodeDAO().update(groupStatusCode, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table BMTGrpStatusCodes by using GroupStatusCodeDAO's delete method with
	 * type as Blank 3) Audit the record in to AuditHeader and
	 * AdtBMTGrpStatusCodes by using auditHeaderDAO.addAudit(auditHeader)
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
		GroupStatusCode groupStatusCode = (GroupStatusCode) auditHeader
				.getAuditDetail().getModelData();

		getGroupStatusCodeDAO().delete(groupStatusCode, "");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getGroupStatusCodeById fetch the details by using GroupStatusCodeDAO's
	 * getGroupStatusCodeById method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return GroupStatusCode
	 */
	@Override
	public GroupStatusCode getGroupStatusCodeById(String id) {
		return getGroupStatusCodeDAO().getGroupStatusCodeById(id, "_View");
	}

	/**
	 * getApprovedGroupStatusCodeById fetch the details by using
	 * GroupStatusCodeDAO's getGroupStatusCodeById method . with parameter id
	 * and type as blank. it fetches the approved records from the
	 * BMTGrpStatusCodes.
	 * 
	 * @param id
	 *            (String)
	 * @return GroupStatusCode
	 */
	public GroupStatusCode getApprovedGroupStatusCodeById(String id) {
		return getGroupStatusCodeDAO().getGroupStatusCodeById(id, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getGroupStatusCodeDAO().delete with parameters groupStatusCode,""
	 * b) NEW Add new record in to main table by using
	 * getGroupStatusCodeDAO().save with parameters groupStatusCode,"" c) EDIT
	 * Update record in the main table by using getGroupStatusCodeDAO().update
	 * with parameters groupStatusCode,"" 3) Delete the record from the workFlow
	 * table by using getGroupStatusCodeDAO().delete with parameters
	 * groupStatusCode,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtBMTGrpStatusCodes by using auditHeaderDAO.addAudit(auditHeader) for
	 * Work flow 5) Audit the record in to AuditHeader and AdtBMTGrpStatusCodes
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
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		GroupStatusCode groupStatusCode = new GroupStatusCode();
		BeanUtils.copyProperties((GroupStatusCode) auditHeader.getAuditDetail()
				.getModelData(), groupStatusCode);

		if (groupStatusCode.getRecordType().equals(
				PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			getGroupStatusCodeDAO().delete(groupStatusCode, "");
		} else {
			groupStatusCode.setRoleCode("");
			groupStatusCode.setNextRoleCode("");
			groupStatusCode.setTaskId("");
			groupStatusCode.setNextTaskId("");
			groupStatusCode.setWorkflowId(0);

			if (groupStatusCode.getRecordType().equals(
					PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				groupStatusCode.setRecordType("");
				getGroupStatusCodeDAO().save(groupStatusCode, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				groupStatusCode.setRecordType("");
				getGroupStatusCodeDAO().update(groupStatusCode, "");
			}
		}

		getGroupStatusCodeDAO().delete(groupStatusCode, "_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(groupStatusCode);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getGroupStatusCodeDAO().delete with
	 * parameters groupStatusCode,"_Temp" 3) Audit the record in to AuditHeader
	 * and AdtBMTGrpStatusCodes by using auditHeaderDAO.addAudit(auditHeader)
	 * for Work flow
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
		GroupStatusCode groupStatusCode = (GroupStatusCode) auditHeader
				.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getGroupStatusCodeDAO().delete(groupStatusCode, "_Temp");

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
	 * getGroupStatusCodeDAO().getErrorDetail with Error ID and language as
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
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());

		GroupStatusCode groupStatusCode = (GroupStatusCode) auditDetail
				.getModelData();
		GroupStatusCode tempGroupStatusCode = null;

		if (groupStatusCode.isWorkflow()) {
			tempGroupStatusCode = getGroupStatusCodeDAO()
					.getGroupStatusCodeById(groupStatusCode.getId(), "_Temp");
		}

		GroupStatusCode befGroupStatusCode = getGroupStatusCodeDAO()
				.getGroupStatusCodeById(groupStatusCode.getId(), "");
		GroupStatusCode oldGroupStatusCode = groupStatusCode.getBefImage();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = groupStatusCode.getGrpStsCode();
		errParm[0] = PennantJavaUtil.getLabel("label_GrpStsCode") + ":"
				+ valueParm[0];

		if (groupStatusCode.isNew()) { // for New record or new record into work
			// flow
			if (!groupStatusCode.isWorkflow()) {// With out Work flow only new
				// records
				if (befGroupStatusCode != null) { // Record Already Exists in
					// the table then error
					auditDetail
							.setErrorDetail(new ErrorDetail(
									PennantConstants.KEY_FIELD, "41001",
									errParm, null));
				}
			} else { // with work flow

				if (groupStatusCode.getRecordType().equals(
						PennantConstants.RECORD_TYPE_NEW)) { // if records type
																// is new
					if (befGroupStatusCode != null
							|| tempGroupStatusCode != null) { // if
						  					// records already exists
											// in the main table
						auditDetail.setErrorDetail(new ErrorDetail(
								PennantConstants.KEY_FIELD, "41001", errParm,
								null));
					}
				} else { // if records not exists in the Main flow table
					if (befGroupStatusCode == null
							|| tempGroupStatusCode != null) {
						auditDetail.setErrorDetail(new ErrorDetail(
								PennantConstants.KEY_FIELD, "41005", errParm,
								null));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!groupStatusCode.isWorkflow()) { // With out Work flow for
													// update and delete

				if (befGroupStatusCode == null) { // if records not exists in
													// the main table
					auditDetail
							.setErrorDetail(new ErrorDetail(
									PennantConstants.KEY_FIELD, "41002",
									errParm, null));
				} else {
					if (oldGroupStatusCode != null
							&& !oldGroupStatusCode.getLastMntOn().equals(
									befGroupStatusCode.getLastMntOn())) {
						if (StringUtils.trimToEmpty(
								auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(new ErrorDetail(
									PennantConstants.KEY_FIELD, "41003",
									errParm, null));
						} else {
							auditDetail.setErrorDetail(new ErrorDetail(
									PennantConstants.KEY_FIELD, "41004",
									errParm, null));
						}
					}
				}
			} else {
				if (tempGroupStatusCode == null) { // if records not exists in
					// the Work flow table
					auditDetail
							.setErrorDetail(new ErrorDetail(
									PennantConstants.KEY_FIELD, "41005",
									errParm, null));
				}
				if (tempGroupStatusCode != null
						&& oldGroupStatusCode != null
						&& !oldGroupStatusCode.getLastMntOn().equals(
								tempGroupStatusCode.getLastMntOn())) {
					auditDetail
							.setErrorDetail(new ErrorDetail(
									PennantConstants.KEY_FIELD, "41005",
									errParm, null));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(
				auditDetail.getErrorDetails(), usrLanguage));
		if ("doApprove".equals(StringUtils.trimToEmpty(method))
				|| !groupStatusCode.isWorkflow()) {
			auditDetail.setBefImage(befGroupStatusCode);
		}
		logger.debug("Leaving");
		return auditDetail;
	}

}