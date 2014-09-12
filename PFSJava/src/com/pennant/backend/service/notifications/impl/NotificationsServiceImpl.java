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
 * FileName    		:  NotificationsServiceImpl.java                                                   * 	  
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

package com.pennant.backend.service.notifications.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.notifications.NotificationsDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.rulefactory.Notifications;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.notifications.NotificationsService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>Notifications</b>.<br>
 * 
 */
public class NotificationsServiceImpl extends GenericService<Notifications> implements NotificationsService {

	private static Logger logger = Logger.getLogger(NotificationsServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private NotificationsDAO notificationsDAO;

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public NotificationsDAO getNotificationsDAO() {
		return notificationsDAO;
	}

	public void setNotificationsDAO(NotificationsDAO notificationsDAO) {
		this.notificationsDAO = notificationsDAO;
	}

	public Notifications getNotifications() {
		return getNotificationsDAO().getNotifications();
	}

	public Notifications getNewNotifications() {
		return getNotificationsDAO().getNewNotifications();
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * BMTNotificationss/BMTNotificationss_Temp by using NotificationsDAO's save method b)
	 * Update the Record in the table. based on the module workFlow
	 * Configuration. by using NotificationsDAO's update method 3) Audit the record
	 * in to AuditHeader and AdtBMTNotificationss by using
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
		Notifications notifications = (Notifications) auditHeader.getAuditDetail()
				.getModelData();

		if (notifications.isWorkflow()) {
			tableType = "_TEMP";
		}

		if (notifications.isNew()) {
			notifications.setRuleCode(getNotificationsDAO().save(notifications, tableType));
			auditHeader.getAuditDetail().setModelData(notifications);
			auditHeader.setAuditReference(String.valueOf(notifications.getRuleCode()));
		} else {
			getNotificationsDAO().update(notifications, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table BMTNotificationss by using NotificationsDAO's delete method with type as
	 * Blank 3) Audit the record in to AuditHeader and AdtBMTNotificationss by using
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
		
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		Notifications notifications = (Notifications) auditHeader.getAuditDetail()
				.getModelData();
		getNotificationsDAO().delete(notifications, "");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getNotificationsById fetch the details by using NotificationsDAO's getNotificationsById
	 * method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return Notifications
	 */
	@Override
	public Notifications getNotificationsById(String ruleCode) {
		return getNotificationsDAO().getNotificationsById(ruleCode, "_View");
	}

	/**
	 * getApprovedNotificationsById fetch the details by using NotificationsDAO's
	 * getNotificationsById method . with parameter id and type as blank. it fetches
	 * the approved records from the BMTNotificationss.
	 * 
	 * @param id
	 *            (String)
	 * @return Notifications
	 */
	public Notifications getApprovedNotificationsById(String ruleCode) {
		return getNotificationsDAO().getNotificationsById(ruleCode, "_AView");
	}

	/**
	 * getApprovedNotificationsById fetch the details by using NotificationsDAO's
	 * getNotificationsById method . with parameter id and type as blank. it fetches
	 * the approved records from the BMTNotificationss.
	 * 
	 * @param id
	 *            (String)
	 * @return Notifications
	 */
	public List<Notifications> getApprovedNotificationsByModule(String ruleModule) {
		return getNotificationsDAO().getNotificationsByModule(ruleModule, "_AView");
	}

	/**
	 * This method refresh the Record.
	 * 
	 * @param Notifications
	 *            (notifications)
	 * @return notifications
	 */
	@Override
	public Notifications refresh(Notifications notifications) {
		logger.debug("Entering");
		getNotificationsDAO().refresh(notifications);
		getNotificationsDAO().initialize(notifications);
		logger.debug("Leaving");
		return notifications;
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getNotificationsDAO().delete with parameters notifications,"" b) NEW Add new
	 * record in to main table by using getNotificationsDAO().save with parameters
	 * notifications,"" c) EDIT Update record in the main table by using
	 * getNotificationsDAO().update with parameters notifications,"" 3) Delete the record
	 * from the workFlow table by using getNotificationsDAO().delete with parameters
	 * notifications,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtBMTNotificationss by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow 5) Audit the record in to AuditHeader and AdtBMTNotificationss by using
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

		Notifications notifications = new Notifications();
		BeanUtils.copyProperties((Notifications) auditHeader.getAuditDetail()
				.getModelData(), notifications);

		if (notifications.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getNotificationsDAO().delete(notifications, "");
		} else {
			notifications.setRoleCode("");
			notifications.setNextRoleCode("");
			notifications.setTaskId("");
			notifications.setNextTaskId("");
			notifications.setWorkflowId(0);

			if (notifications.getRecordType().equals(
					PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				notifications.setRecordType("");
				getNotificationsDAO().save(notifications, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				notifications.setRecordType("");
				getNotificationsDAO().update(notifications, "");
			}
		}

		getNotificationsDAO().delete(notifications, "_TEMP");

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(notifications);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getNotificationsDAO().delete with parameters
	 * notifications,"_Temp" 3) Audit the record in to AuditHeader and
	 * AdtBMTNotificationss by using auditHeaderDAO.addAudit(auditHeader) for Work
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

		Notifications notifications = (Notifications) auditHeader.getAuditDetail()
				.getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getNotificationsDAO().delete(notifications, "_TEMP");

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
	 * getNotificationsDAO().getErrorDetail with Error ID and language as parameters.
	 * if any error/Warnings then assign the to auditDeail Object
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
		
		Notifications notifications = (Notifications) auditDetail.getModelData();

		Notifications tempNotifications = null;
		if (notifications.isWorkflow()) {
			tempNotifications = getNotificationsDAO().getNotifications(
					notifications.getRuleCode(),
					notifications.getRuleModule(), "_Temp");
		}

		Notifications befNotifications = getNotificationsDAO().getNotifications(
				notifications.getRuleCode(), notifications.getRuleModule(),
				" ");
		Notifications oldNotifications = notifications.getBefImage();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];
		
		valueParm[0] = notifications.getRuleCode();
		valueParm[1] = notifications.getRuleModule();

		errParm[0] = PennantJavaUtil.getLabel("label_Notifications_RuleCode") + ":"+ valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_Notifications_RuleModule") + ":"+valueParm[1];

		if (notifications.isNew()) { // for New record or new record into work flow

			if (!notifications.isWorkflow()) {// With out Work flow only new records
				if (befNotifications != null) { // Record Already Exists in the table
											// then error
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41001",errParm,null));
				}
			} else { // with work flow
				if (notifications.getRecordType().equals(
						PennantConstants.RECORD_TYPE_NEW)) { // if records type
																// is new
					if (befNotifications != null || tempNotifications != null) { // if records already exists in
												// the main table
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41001",errParm,null));
					}
				} else { // if records not exists in the Main flow table
					if (befNotifications == null || tempNotifications != null) {
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",errParm,null));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!notifications.isWorkflow()) { // With out Work flow for update and
											// delete
				if (befNotifications == null) { // if records not exists in the main
											// table
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41002",errParm,null));
				} else {
					if (oldNotifications != null
							&& !oldNotifications.getLastMntOn().equals(
									befNotifications.getLastMntOn())) {
						if (StringUtils.trimToEmpty(
								auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41003",errParm,null));
						} else {
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41004",errParm,null));
						}
					}
				}
			} else {
				if (tempNotifications == null) { // if records not exists in the Work flow table
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",errParm,null));
				}
				
				if ( tempNotifications != null &&  oldNotifications != null
						&& !oldNotifications.getLastMntOn().equals(
								tempNotifications.getLastMntOn())) {
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",errParm,null));
				}
			}
		}
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
		
		if (StringUtils.trimToEmpty(method).equals("doApprove")
				|| !notifications.isWorkflow()) {
			auditDetail.setBefImage(befNotifications);
		}

		logger.debug("Leaving");
		return auditDetail;
	}


}