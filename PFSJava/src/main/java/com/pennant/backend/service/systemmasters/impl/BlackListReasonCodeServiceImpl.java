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
 * FileName    		:  BlackListReasonCodeServiceImpl.java                                                   * 	  
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

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.systemmasters.BlackListReasonCodeDAO;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.BlackListReasonCode;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on
 * <b>BlackListReasonCode</b>.<br>
 * 
 */
public class BlackListReasonCodeServiceImpl extends GenericService<BlackListReasonCode> implements
		BlackListReasonCodeService {

	private static Logger logger = Logger.getLogger(BlackListReasonCodeServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private BlackListReasonCodeDAO blackListReasonCodeDAO;

	public BlackListReasonCodeServiceImpl() {
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

	public BlackListReasonCodeDAO getBlackListReasonCodeDAO() {
		return blackListReasonCodeDAO;
	}

	public void setBlackListReasonCodeDAO(BlackListReasonCodeDAO blackListReasonCodeDAO) {
		this.blackListReasonCodeDAO = blackListReasonCodeDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * BMTBlackListRsnCodes/BMTBlackListRsnCodes_Temp by using
	 * BlackListReasonCodeDAO's save method b) Update the Record in the table.
	 * based on the module workFlow Configuration. by using
	 * BlackListReasonCodeDAO's update method 3) Audit the record in to
	 * AuditHeader and AdtBMTBlackListRsnCodes by using
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
		BlackListReasonCode blackListReasonCode = (BlackListReasonCode) auditHeader
				.getAuditDetail().getModelData();
		if (blackListReasonCode.isWorkflow()) {
			tableType = "_Temp";
		}

		if (blackListReasonCode.isNew()) {
			blackListReasonCode.setId(getBlackListReasonCodeDAO().save(
					blackListReasonCode, tableType));
			auditHeader.getAuditDetail().setModelData(blackListReasonCode);
			auditHeader.setAuditReference(blackListReasonCode.getBLRsnCode());
		} else {
			getBlackListReasonCodeDAO()
					.update(blackListReasonCode, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table BMTBlackListRsnCodes by using BlackListReasonCodeDAO's delete
	 * method with type as Blank 3) Audit the record in to AuditHeader and
	 * AdtBMTBlackListRsnCodes by using auditHeaderDAO.addAudit(auditHeader)
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
		BlackListReasonCode blackListReasonCode = (BlackListReasonCode) auditHeader
				.getAuditDetail().getModelData();
		getBlackListReasonCodeDAO().delete(blackListReasonCode, "");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getBlackListReasonCodeById fetch the details by using
	 * BlackListReasonCodeDAO's getBlackListReasonCodeById method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return BlackListReasonCode
	 */
	@Override
	public BlackListReasonCode getBlackListReasonCodeById(String id) {
		return getBlackListReasonCodeDAO().getBlackListReasonCodeById(id,
				"_View");
	}

	/**
	 * getApprovedBlackListReasonCodeById fetch the details by using
	 * BlackListReasonCodeDAO's getBlackListReasonCodeById method . with
	 * parameter id and type as blank. it fetches the approved records from the
	 * BMTBlackListRsnCodes.
	 * 
	 * @param id
	 *            (String)
	 * @return BlackListReasonCode
	 */
	public BlackListReasonCode getApprovedBlackListReasonCodeById(String id) {
		return getBlackListReasonCodeDAO().getBlackListReasonCodeById(id, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getBlackListReasonCodeDAO().delete with parameters
	 * blackListReasonCode,"" b) NEW Add new record in to main table by using
	 * getBlackListReasonCodeDAO().save with parameters blackListReasonCode,""
	 * c) EDIT Update record in the main table by using
	 * getBlackListReasonCodeDAO().update with parameters
	 * blackListReasonCode,"" 3) Delete the record from the workFlow table by
	 * using getBlackListReasonCodeDAO().delete with parameters
	 * blackListReasonCode,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtBMTBlackListRsnCodes by using auditHeaderDAO.addAudit(auditHeader) for
	 * Work flow 5) Audit the record in to AuditHeader and
	 * AdtBMTBlackListRsnCodes by using auditHeaderDAO.addAudit(auditHeader)
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
		BlackListReasonCode blackListReasonCode = new BlackListReasonCode();
		BeanUtils.copyProperties((BlackListReasonCode) auditHeader
				.getAuditDetail().getModelData(), blackListReasonCode);

		if (blackListReasonCode.getRecordType().equals(
				PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getBlackListReasonCodeDAO().delete(blackListReasonCode, "");
		} else {
			blackListReasonCode.setRoleCode("");
			blackListReasonCode.setNextRoleCode("");
			blackListReasonCode.setTaskId("");
			blackListReasonCode.setNextTaskId("");
			blackListReasonCode.setWorkflowId(0);

			if (blackListReasonCode.getRecordType().equals(
					PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				blackListReasonCode.setRecordType("");
				getBlackListReasonCodeDAO().save(blackListReasonCode, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				blackListReasonCode.setRecordType("");
				getBlackListReasonCodeDAO().update(blackListReasonCode, "");
			}
		}

		getBlackListReasonCodeDAO().delete(blackListReasonCode, "_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(blackListReasonCode);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getBlackListReasonCodeDAO().delete with
	 * parameters blackListReasonCode,"_Temp" 3) Audit the record in to
	 * AuditHeader and AdtBMTBlackListRsnCodes by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}
		BlackListReasonCode blackListReasonCode = (BlackListReasonCode) auditHeader
				.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getBlackListReasonCodeDAO().delete(blackListReasonCode, "_Temp");

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
	 * getBlackListReasonCodeDAO().getErrorDetail with Error ID and language as
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

		BlackListReasonCode blackListReasonCode = (BlackListReasonCode) auditDetail
				.getModelData();
		BlackListReasonCode tempBlackListReasonCode = null;

		if (blackListReasonCode.isWorkflow()) {
			tempBlackListReasonCode = getBlackListReasonCodeDAO()
					.getBlackListReasonCodeById(blackListReasonCode.getId(),
							"_Temp");
		}

		BlackListReasonCode befBlackListReasonCode = getBlackListReasonCodeDAO()
				.getBlackListReasonCodeById(blackListReasonCode.getId(), "");
		BlackListReasonCode oldBlackListReasonCode = blackListReasonCode
				.getBefImage();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = blackListReasonCode.getBLRsnCode();
		errParm[0] = PennantJavaUtil.getLabel("label_BLRsnCode") + ":"
				+ valueParm[0];

		if (blackListReasonCode.isNew()) { // for New record or new record into
			// work flow

			if (!blackListReasonCode.isWorkflow()) {// With out Work flow only
														// new records
				if (befBlackListReasonCode != null) { // Record Already Exists
														// in the table then
														// error
					auditDetail
							.setErrorDetail(new ErrorDetail(
									PennantConstants.KEY_FIELD, "41001",
									errParm, null));
				}
			} else { // with work flow

				if (blackListReasonCode.getRecordType().equals(
						PennantConstants.RECORD_TYPE_NEW)) { // if records type
																// is new
					if (befBlackListReasonCode != null
							|| tempBlackListReasonCode != null) { // if records
																// already exists
																// in the main table
						auditDetail.setErrorDetail(new ErrorDetail(
								PennantConstants.KEY_FIELD, "41001", errParm,
								null));
					}
				} else { // if records not exists in the Main flow table
					if (befBlackListReasonCode == null
							|| tempBlackListReasonCode != null) {
						auditDetail.setErrorDetail(new ErrorDetail(
								PennantConstants.KEY_FIELD, "41005", errParm,
								null));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!blackListReasonCode.isWorkflow()) { // With out Work flow for
														// update and delete

				if (befBlackListReasonCode == null) { // if records not exists
					// in the main table
					auditDetail
							.setErrorDetail(new ErrorDetail(
									PennantConstants.KEY_FIELD, "41002",
									errParm, null));
				} else {
					if (oldBlackListReasonCode != null
							&& !oldBlackListReasonCode.getLastMntOn().equals(
									befBlackListReasonCode.getLastMntOn())) {
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

				if (tempBlackListReasonCode == null) { // if records not exists
					// in the Work flow
					// table
					auditDetail
							.setErrorDetail(new ErrorDetail(
									PennantConstants.KEY_FIELD, "41005",
									errParm, null));
				}
				if (tempBlackListReasonCode != null
						&& oldBlackListReasonCode != null
						&& !oldBlackListReasonCode.getLastMntOn().equals(
								tempBlackListReasonCode.getLastMntOn())) {
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
				|| !blackListReasonCode.isWorkflow()) {
			auditDetail.setBefImage(befBlackListReasonCode);
		}

		logger.debug("Leaving");
		return auditDetail;
	}

}