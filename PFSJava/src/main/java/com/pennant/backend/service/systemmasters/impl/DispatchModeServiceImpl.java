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
 * FileName    		:  DispatchModeServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  18-08-2011    														*
 *                                                                  						*
 * Modified Date    :  18-08-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 18-08-2011       Pennant	                 0.1                                            * 
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
import com.pennant.backend.dao.systemmasters.DispatchModeDAO;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.DispatchMode;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.systemmasters.DispatchModeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>DispatchMode</b>.<br>
 * 
 */
public class DispatchModeServiceImpl extends GenericService<DispatchMode>
		implements DispatchModeService {

	private static final Logger logger = Logger
			.getLogger(DispatchModeServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private DispatchModeDAO dispatchModeDAO;

	public DispatchModeServiceImpl() {
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

	public DispatchModeDAO getDispatchModeDAO() {
		return dispatchModeDAO;
	}

	public void setDispatchModeDAO(DispatchModeDAO dispatchModeDAO) {
		this.dispatchModeDAO = dispatchModeDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * BMTDispatchModes/BMTDispatchModes_Temp by using DispatchModeDAO's save
	 * method b) Update the Record in the table. based on the module workFlow
	 * Configuration. by using DispatchModeDAO's update method 3) Audit the
	 * record in to AuditHeader and AdtBMTDispatchModes by using
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
		DispatchMode dispatchMode = (DispatchMode) auditHeader.getAuditDetail()
				.getModelData();

		if (dispatchMode.isWorkflow()) {
			tableType = "_Temp";
		}

		if (dispatchMode.isNew()) {
			getDispatchModeDAO().save(dispatchMode, tableType);
		} else {
			getDispatchModeDAO().update(dispatchMode, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table BMTDispatchModes by using DispatchModeDAO's delete method with type
	 * as Blank 3) Audit the record in to AuditHeader and AdtBMTDispatchModes by
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

		DispatchMode dispatchMode = (DispatchMode) auditHeader.getAuditDetail()
				.getModelData();
		getDispatchModeDAO().delete(dispatchMode, "");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getDispatchModeById fetch the details by using DispatchModeDAO's
	 * getDispatchModeById method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return DispatchMode
	 */
	@Override
	public DispatchMode getDispatchModeById(String id) {
		return getDispatchModeDAO().getDispatchModeById(id, "_View");
	}

	/**
	 * getApprovedDispatchModeById fetch the details by using DispatchModeDAO's
	 * getDispatchModeById method . with parameter id and type as blank. it
	 * fetches the approved records from the BMTDispatchModes.
	 * 
	 * @param id
	 *            (String)
	 * @return DispatchMode
	 */
	public DispatchMode getApprovedDispatchModeById(String id) {
		return getDispatchModeDAO().getDispatchModeById(id, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getDispatchModeDAO().delete with parameters dispatchMode,"" b) NEW
	 * Add new record in to main table by using getDispatchModeDAO().save with
	 * parameters dispatchMode,"" c) EDIT Update record in the main table by
	 * using getDispatchModeDAO().update with parameters dispatchMode,"" 3)
	 * Delete the record from the workFlow table by using
	 * getDispatchModeDAO().delete with parameters dispatchMode,"_Temp" 4) Audit
	 * the record in to AuditHeader and AdtBMTDispatchModes by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in
	 * to AuditHeader and AdtBMTDispatchModes by using
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

		DispatchMode dispatchMode = new DispatchMode();
		BeanUtils.copyProperties((DispatchMode) auditHeader.getAuditDetail()
				.getModelData(), dispatchMode);

		if (dispatchMode.getRecordType().equals(
				PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			getDispatchModeDAO().delete(dispatchMode, "");

		} else {
			dispatchMode.setRoleCode("");
			dispatchMode.setNextRoleCode("");
			dispatchMode.setTaskId("");
			dispatchMode.setNextTaskId("");
			dispatchMode.setWorkflowId(0);

			if (dispatchMode.getRecordType().equals(
					PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				dispatchMode.setRecordType("");
				getDispatchModeDAO().save(dispatchMode, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				dispatchMode.setRecordType("");
				getDispatchModeDAO().update(dispatchMode, "");
			}
		}

		getDispatchModeDAO().delete(dispatchMode, "_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(dispatchMode);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getDispatchModeDAO().delete with parameters
	 * dispatchMode,"_Temp" 3) Audit the record in to AuditHeader and
	 * AdtBMTDispatchModes by using auditHeaderDAO.addAudit(auditHeader) for
	 * Work flow
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

		DispatchMode dispatchMode = (DispatchMode) auditHeader.getAuditDetail()
				.getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getDispatchModeDAO().delete(dispatchMode, "_Temp");

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
	 * getDispatchModeDAO().getErrorDetail with Error ID and language as
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

		DispatchMode dispatchMode = (DispatchMode) auditDetail.getModelData();
		DispatchMode tempDispatchMode = null;

		if (dispatchMode.isWorkflow()) {
			tempDispatchMode = getDispatchModeDAO().getDispatchModeById(
					dispatchMode.getId(), "_Temp");
		}

		DispatchMode befDispatchMode = getDispatchModeDAO()
				.getDispatchModeById(dispatchMode.getId(), "");
		DispatchMode oldDispatchMode = dispatchMode.getBefImage();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = dispatchMode.getDispatchModeCode();
		errParm[0] = PennantJavaUtil.getLabel("label_DispatchModeCode") + ":"
				+ valueParm[0];

		if (dispatchMode.isNew()) { // for New record or new record into work
			// flow

			if (!dispatchMode.isWorkflow()) {// With out Work flow only new
				// records
				if (befDispatchMode != null) { // Record Already Exists in the
					// table then error
					auditDetail
							.setErrorDetail(new ErrorDetail(
									PennantConstants.KEY_FIELD, "41001",
									errParm, null));
				}
			} else { // with work flow
				if (dispatchMode.getRecordType().equals(
						PennantConstants.RECORD_TYPE_NEW)) { // if records type
					// is new
					if (befDispatchMode != null || tempDispatchMode != null) { // if
															// records already exists
															// in the main table
						auditDetail.setErrorDetail(new ErrorDetail(
								PennantConstants.KEY_FIELD, "41001", errParm,
								null));
					}
				} else { // if records not exists in the Main flow table
					if (befDispatchMode == null || tempDispatchMode != null) {
						auditDetail.setErrorDetail(new ErrorDetail(
								PennantConstants.KEY_FIELD, "41005", errParm,
								null));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!dispatchMode.isWorkflow()) { // With out Work flow for update
				// and delete

				if (befDispatchMode == null) { // if records not exists in the
					// main table
					auditDetail
							.setErrorDetail(new ErrorDetail(
									PennantConstants.KEY_FIELD, "41002",
									errParm, null));
				} else {
					if (oldDispatchMode != null
							&& !oldDispatchMode.getLastMntOn().equals(
									befDispatchMode.getLastMntOn())) {
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

				if (tempDispatchMode == null) { // if records not exists in the
					// Work flow table
					auditDetail
							.setErrorDetail(new ErrorDetail(
									PennantConstants.KEY_FIELD, "41005",
									errParm, null));
				}
				if (tempDispatchMode != null
						&& oldDispatchMode != null
						&& !oldDispatchMode.getLastMntOn().equals(
								tempDispatchMode.getLastMntOn())) {
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
				|| !dispatchMode.isWorkflow()) {
			auditDetail.setBefImage(befDispatchMode);
		}

		return auditDetail;
	}
}