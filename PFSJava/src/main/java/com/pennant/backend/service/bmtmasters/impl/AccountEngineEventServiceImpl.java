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
 * FileName    		:  AccountEngineEventServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-06-2011    														*
 *                                                                  						*
 * Modified Date    :  27-06-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 27-06-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.service.bmtmasters.impl;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.bmtmasters.AccountEngineEventDAO;
import com.pennant.backend.dao.bmtmasters.impl.AccountEngineEventDAOImpl;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.AccountEngineEvent;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.bmtmasters.AccountEngineEventService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;

/**
 * Service implementation for methods that depends on <b>AccountEngineEvent</b>.<br>
 * 
 */
public class AccountEngineEventServiceImpl extends
		GenericService<AccountEngineEvent> implements AccountEngineEventService {

	private static Logger logger = Logger
			.getLogger(AccountEngineEventDAOImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private AccountEngineEventDAO accountEngineEventDAO;

	public AccountEngineEventServiceImpl() {
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

	public AccountEngineEventDAO getAccountEngineEventDAO() {
		return accountEngineEventDAO;
	}

	public void setAccountEngineEventDAO(
			AccountEngineEventDAO accountEngineEventDAO) {
		this.accountEngineEventDAO = accountEngineEventDAO;
	}

	public AccountEngineEvent getAccountEngineEvent() {
		return getAccountEngineEventDAO().getAccountEngineEvent();
	}

	public AccountEngineEvent getNewAccountEngineEvent() {
		return getAccountEngineEventDAO().getNewAccountEngineEvent();
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * BMTAEEvents/BMTAEEvents_Temp by using AccountEngineEventDAO's save method
	 * b) Update the Record in the table. based on the module workFlow
	 * Configuration. by using AccountEngineEventDAO's update method 3) Audit
	 * the record in to AuditHeader and AdtBMTAEEvents by using
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
		AccountEngineEvent accountEngineEvent = (AccountEngineEvent) auditHeader
				.getAuditDetail().getModelData();

		if (accountEngineEvent.isWorkflow()) {
			tableType = "_Temp";
		}

		if (accountEngineEvent.isNew()) {
			accountEngineEvent.setId(getAccountEngineEventDAO().save(
					accountEngineEvent, tableType));
			auditHeader.getAuditDetail().setModelData(accountEngineEvent);
			auditHeader.setAuditReference(accountEngineEvent.getAEEventCode());
		} else {
			getAccountEngineEventDAO().update(accountEngineEvent, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table BMTAEEvents by using AccountEngineEventDAO's delete method with
	 * type as Blank 3) Audit the record in to AuditHeader and AdtBMTAEEvents by
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
		AccountEngineEvent accountEngineEvent = (AccountEngineEvent) auditHeader
				.getAuditDetail().getModelData();
		getAccountEngineEventDAO().delete(accountEngineEvent, "");
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getAccountEngineEventById fetch the details by using
	 * AccountEngineEventDAO's getAccountEngineEventById method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return AccountEngineEvent
	 */
	@Override
	public AccountEngineEvent getAccountEngineEventById(String id) {
		return getAccountEngineEventDAO()
				.getAccountEngineEventById(id, "_View");
	}

	/**
	 * getApprovedAccountEngineEventById fetch the details by using
	 * AccountEngineEventDAO's getAccountEngineEventById method . with parameter
	 * id and type as blank. it fetches the approved records from the
	 * BMTAEEvents.
	 * 
	 * @param id
	 *            (String)
	 * @return AccountEngineEvent
	 */
	public AccountEngineEvent getApprovedAccountEngineEventById(String id) {
		return getAccountEngineEventDAO().getAccountEngineEventById(id, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getAccountEngineEventDAO().delete with parameters
	 * accountEngineEvent,"" b) NEW Add new record in to main table by using
	 * getAccountEngineEventDAO().save with parameters accountEngineEvent,"" c)
	 * EDIT Update record in the main table by using
	 * getAccountEngineEventDAO().update with parameters accountEngineEvent,""
	 * 3) Delete the record from the workFlow table by using
	 * getAccountEngineEventDAO().delete with parameters
	 * accountEngineEvent,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtBMTAEEvents by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow 5) Audit the record in to AuditHeader and AdtBMTAEEvents by using
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

		AccountEngineEvent accountEngineEvent = new AccountEngineEvent();
		BeanUtils.copyProperties((AccountEngineEvent) auditHeader
				.getAuditDetail().getModelData(), accountEngineEvent);

		if (accountEngineEvent.getRecordType().equals(
				PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getAccountEngineEventDAO().delete(accountEngineEvent, "");
		} else {
			accountEngineEvent.setRoleCode("");
			accountEngineEvent.setNextRoleCode("");
			accountEngineEvent.setTaskId("");
			accountEngineEvent.setNextTaskId("");
			accountEngineEvent.setWorkflowId(0);

			if (accountEngineEvent.getRecordType().equals(
					PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				accountEngineEvent.setRecordType("");
				getAccountEngineEventDAO().save(accountEngineEvent, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				accountEngineEvent.setRecordType("");
				getAccountEngineEventDAO().update(accountEngineEvent, "");
			}
		}

		getAccountEngineEventDAO().delete(accountEngineEvent, "_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(accountEngineEvent);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getAccountEngineEventDAO().delete with
	 * parameters accountEngineEvent,"_Temp" 3) Audit the record in to
	 * AuditHeader and AdtBMTAEEvents by using
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
			logger.debug("Leaving");
			return auditHeader;
		}

		AccountEngineEvent accountEngineEvent = (AccountEngineEvent) auditHeader
				.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAccountEngineEventDAO().delete(accountEngineEvent, "_Temp");

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
	 * getAccountEngineEventDAO().getErrorDetail with Error ID and language as
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

		AccountEngineEvent accountEngineEvent = (AccountEngineEvent) auditDetail
				.getModelData();
		AccountEngineEvent tempAccountEngineEvent = null;

		if (accountEngineEvent.isWorkflow()) {
			tempAccountEngineEvent = getAccountEngineEventDAO()
					.getAccountEngineEventById(accountEngineEvent.getId(),
							"_Temp");
		}

		AccountEngineEvent befAccountEngineEvent = getAccountEngineEventDAO()
				.getAccountEngineEventById(accountEngineEvent.getId(), "");
		AccountEngineEvent oldAccountEngineEvent = accountEngineEvent
				.getBefImage();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = accountEngineEvent.getAEEventCode();
		errParm[0] = PennantJavaUtil.getLabel("label_AEEventCode") + ":"
				+ valueParm[0];

		if (accountEngineEvent.isNew()) { // for New record or new record into
											// work flow

			if (!accountEngineEvent.isWorkflow()) {// With out Work flow only
													// new records
				if (befAccountEngineEvent != null) { // Record Already Exists in
														// the table then error
					auditDetail
							.setErrorDetail(new ErrorDetail(
									PennantConstants.KEY_FIELD, "41001",
									errParm, null));
				}
			} else { // with work flow

				if (accountEngineEvent.getRecordType().equals(
						PennantConstants.RECORD_TYPE_NEW)) { // if records type
																// is new
					if (befAccountEngineEvent != null
							|| tempAccountEngineEvent != null) { // if records already
																	// exists in the
																	// the main table
						auditDetail.setErrorDetail(new ErrorDetail(
								PennantConstants.KEY_FIELD, "41001", errParm,
								null));
					}
				} else { // if records not exists in the Main flow table
					if (befAccountEngineEvent == null
							|| tempAccountEngineEvent != null) {
						auditDetail.setErrorDetail(new ErrorDetail(
								PennantConstants.KEY_FIELD, "41005", errParm,
								null));
					}
				}

			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!accountEngineEvent.isWorkflow()) { // With out Work flow for
													// update and delete

				if (befAccountEngineEvent == null) { // if records not exists in
														// the main table
					auditDetail
							.setErrorDetail(new ErrorDetail(
									PennantConstants.KEY_FIELD, "41002",
									errParm, null));
				} else {

					if (oldAccountEngineEvent != null
							&& !oldAccountEngineEvent.getLastMntOn().equals(
									befAccountEngineEvent.getLastMntOn())) {
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
				if (tempAccountEngineEvent == null) { // if records not exists
														// in the Work flow
														// table
					auditDetail
							.setErrorDetail(new ErrorDetail(
									PennantConstants.KEY_FIELD, "41005",
									errParm, null));
				}

				if (tempAccountEngineEvent != null
						&& oldAccountEngineEvent != null
						&& !oldAccountEngineEvent.getLastMntOn().equals(
								tempAccountEngineEvent.getLastMntOn())) {
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
				|| !accountEngineEvent.isWorkflow()) {
			auditDetail.setBefImage(befAccountEngineEvent);
		}

		logger.debug("Leaving");
		return auditDetail;
	}
}