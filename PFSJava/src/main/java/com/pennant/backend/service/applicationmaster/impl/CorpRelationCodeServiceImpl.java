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
 * FileName    		:  CorpRelationCodeServiceImpl.java                                                   * 	  
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

package com.pennant.backend.service.applicationmaster.impl;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.applicationmaster.CorpRelationCodeDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.applicationmaster.CorpRelationCode;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.CorpRelationCodeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;

/**
 * Service implementation for methods that depends on <b>CorpRelationCode</b>.<br>
 * 
 */
public class CorpRelationCodeServiceImpl extends GenericService<CorpRelationCode> implements CorpRelationCodeService {

	private static Logger logger = Logger.getLogger(CorpRelationCodeServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private CorpRelationCodeDAO corpRelationCodeDAO;

	public CorpRelationCodeServiceImpl() {
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

	public CorpRelationCodeDAO getCorpRelationCodeDAO() {
		return corpRelationCodeDAO;
	}

	public void setCorpRelationCodeDAO(CorpRelationCodeDAO corpRelationCodeDAO) {
		this.corpRelationCodeDAO = corpRelationCodeDAO;
	}


	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * BMTCorpRelationCodes/BMTCorpRelationCodes_Temp by using
	 * CorpRelationCodeDAO's save method b) Update the Record in the table.
	 * based on the module workFlow Configuration. by using
	 * CorpRelationCodeDAO's update method 3) Audit the record in to AuditHeader
	 * and AdtBMTCorpRelationCodes by using auditHeaderDAO.addAudit(auditHeader)
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
		CorpRelationCode corpRelationCode = (CorpRelationCode) auditHeader
				.getAuditDetail().getModelData();

		if (corpRelationCode.isWorkflow()) {
			tableType = "_Temp";
		}

		if (corpRelationCode.isNew()) {
			corpRelationCode.setId(getCorpRelationCodeDAO().save(
					corpRelationCode, tableType));
			auditHeader.getAuditDetail().setModelData(corpRelationCode);
			auditHeader.setAuditReference(corpRelationCode
					.getCorpRelationCode());
		} else {
			getCorpRelationCodeDAO().update(corpRelationCode, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table BMTCorpRelationCodes by using CorpRelationCodeDAO's delete method
	 * with type as Blank 3) Audit the record in to AuditHeader and
	 * AdtBMTCorpRelationCodes by using auditHeaderDAO.addAudit(auditHeader)
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
		CorpRelationCode corpRelationCode = (CorpRelationCode) auditHeader
				.getAuditDetail().getModelData();

		getCorpRelationCodeDAO().delete(corpRelationCode, "");
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getCorpRelationCodeById fetch the details by using CorpRelationCodeDAO's
	 * getCorpRelationCodeById method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return CorpRelationCode
	 */
	@Override
	public CorpRelationCode getCorpRelationCodeById(String id) {
		return getCorpRelationCodeDAO().getCorpRelationCodeById(id, "_View");
	}

	/**
	 * getApprovedCorpRelationCodeById fetch the details by using
	 * CorpRelationCodeDAO's getCorpRelationCodeById method . with parameter id
	 * and type as blank. it fetches the approved records from the
	 * BMTCorpRelationCodes.
	 * 
	 * @param id
	 *            (String)
	 * @return CorpRelationCode
	 */
	public CorpRelationCode getApprovedCorpRelationCodeById(String id) {
		return getCorpRelationCodeDAO().getCorpRelationCodeById(id, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getCorpRelationCodeDAO().delete with parameters corpRelationCode,""
	 * b) NEW Add new record in to main table by using
	 * getCorpRelationCodeDAO().save with parameters corpRelationCode,"" c) EDIT
	 * Update record in the main table by using getCorpRelationCodeDAO().update
	 * with parameters corpRelationCode,"" 3) Delete the record from the
	 * workFlow table by using getCorpRelationCodeDAO().delete with parameters
	 * corpRelationCode,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtBMTCorpRelationCodes by using auditHeaderDAO.addAudit(auditHeader) for
	 * Work flow 5) Audit the record in to AuditHeader and
	 * AdtBMTCorpRelationCodes by using auditHeaderDAO.addAudit(auditHeader)
	 * based on the transaction Type.
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
		CorpRelationCode corpRelationCode = new CorpRelationCode();
		BeanUtils.copyProperties((CorpRelationCode) auditHeader
				.getAuditDetail().getModelData(), corpRelationCode);

		if (corpRelationCode.getRecordType().equals(
				PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			getCorpRelationCodeDAO().delete(corpRelationCode, "");

		} else {
			corpRelationCode.setRoleCode("");
			corpRelationCode.setNextRoleCode("");
			corpRelationCode.setTaskId("");
			corpRelationCode.setNextTaskId("");
			corpRelationCode.setWorkflowId(0);

			if (corpRelationCode.getRecordType().equals(
					PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				corpRelationCode.setRecordType("");
				getCorpRelationCodeDAO().save(corpRelationCode, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				corpRelationCode.setRecordType("");
				getCorpRelationCodeDAO().update(corpRelationCode, "");
			}
		}

		getCorpRelationCodeDAO().delete(corpRelationCode, "_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(corpRelationCode);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getCorpRelationCodeDAO().delete with
	 * parameters corpRelationCode,"_Temp" 3) Audit the record in to AuditHeader
	 * and AdtBMTCorpRelationCodes by using auditHeaderDAO.addAudit(auditHeader)
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
		CorpRelationCode corpRelationCode = (CorpRelationCode) auditHeader
				.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getCorpRelationCodeDAO().delete(corpRelationCode, "_Temp");

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
	 * getCorpRelationCodeDAO().getErrorDetail with Error ID and language as
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

		CorpRelationCode corpRelationCode = (CorpRelationCode) auditDetail
				.getModelData();
		CorpRelationCode tempCorpRelationCode = null;

		if (corpRelationCode.isWorkflow()) {
			tempCorpRelationCode = getCorpRelationCodeDAO()
					.getCorpRelationCodeById(corpRelationCode.getId(), "_Temp");
		}

		CorpRelationCode befCorpRelationCode = getCorpRelationCodeDAO()
				.getCorpRelationCodeById(corpRelationCode.getId(), "");
		CorpRelationCode oldCorpRelationCode = corpRelationCode.getBefImage();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = corpRelationCode.getCorpRelationCode();
		errParm[0] = PennantJavaUtil.getLabel("label_CorpRelationCode") + ":"
				+ valueParm[0];

		if (corpRelationCode.isNew()) { // for New record or new record into
			// work flow

			if (!corpRelationCode.isWorkflow()) {// With out Work flow only new
				// records
				if (befCorpRelationCode != null) { // Record Already Exists in
													// the table then error
					auditDetail
							.setErrorDetail(new ErrorDetail(
									PennantConstants.KEY_FIELD, "41001",
									errParm, null));
				}
			} else { // with work flow

				if (corpRelationCode.getRecordType().equals(
						PennantConstants.RECORD_TYPE_NEW)) { // if records type
					// is new
					if (befCorpRelationCode != null
							|| tempCorpRelationCode != null) { // if records
																// already
																// exists in the
																// main table
						auditDetail.setErrorDetail(new ErrorDetail(
								PennantConstants.KEY_FIELD, "41001", errParm,
								null));
					}
				} else { // if records not exists in the Main flow table
					if (befCorpRelationCode == null
							|| tempCorpRelationCode != null) {
						auditDetail.setErrorDetail(new ErrorDetail(
								PennantConstants.KEY_FIELD, "41005", errParm,
								null));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!corpRelationCode.isWorkflow()) { // With out Work flow for
				// update and delete

				if (befCorpRelationCode == null) { // if records not exists in
													// the main table
					auditDetail
							.setErrorDetail(new ErrorDetail(
									PennantConstants.KEY_FIELD, "41002",
									errParm, null));
				} else {
					if (oldCorpRelationCode != null
							&& !oldCorpRelationCode.getLastMntOn().equals(
									befCorpRelationCode.getLastMntOn())) {
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

				if (tempCorpRelationCode == null) { // if records not exists in
													// the Work flow table
					auditDetail
							.setErrorDetail(new ErrorDetail(
									PennantConstants.KEY_FIELD, "41005",
									errParm, null));
				}

				if (tempCorpRelationCode != null
						&& oldCorpRelationCode != null
						&& !oldCorpRelationCode.getLastMntOn().equals(
								tempCorpRelationCode.getLastMntOn())) {
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
				|| !corpRelationCode.isWorkflow()) {
			auditDetail.setBefImage(befCorpRelationCode);
		}
		logger.debug("Leaving");
		return auditDetail;
	}

}