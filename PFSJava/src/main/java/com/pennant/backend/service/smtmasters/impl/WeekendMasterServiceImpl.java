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
 * FileName    		:  WeekendMasterServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  11-07-2011    														*
 *                                                                  						*
 * Modified Date    :  11-07-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 11-07-2011       Pennant	                 0.1                                            * 
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
package com.pennant.backend.service.smtmasters.impl;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.smtmasters.WeekendMasterDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.smtmasters.WeekendMaster;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.smtmasters.WeekendMasterService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;

/**
 * Service implementation for methods that depends on <b>WeekendMaster</b>.<br>
 * 
 */
public class WeekendMasterServiceImpl extends GenericService<WeekendMaster> implements WeekendMasterService {
	private static Logger logger = Logger.getLogger(WeekendMasterServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private WeekendMasterDAO weekendMasterDAO;

	public WeekendMasterServiceImpl() {
		super();
	}
	
	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public WeekendMasterDAO getWeekendMasterDAO() {
		return weekendMasterDAO;
	}

	public void setWeekendMasterDAO(WeekendMasterDAO weekendMasterDAO) {
		this.weekendMasterDAO = weekendMasterDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * SMTWeekendMaster/SMTWeekendMaster_Temp by using WeekendMasterDAO's save
	 * method b) Update the Record in the table. based on the module workFlow
	 * Configuration. by using WeekendMasterDAO's update method 3) Audit the
	 * record in to AuditHeader and AdtSMTWeekendMaster by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */

	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug("Entering ");

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");

		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}
		String tableType = "";
		WeekendMaster weekendMaster = (WeekendMaster) auditHeader
		.getAuditDetail().getModelData();

		if (weekendMaster.isWorkflow()) {
			tableType = "_Temp";
		}

		if (weekendMaster.isNew()) {
			weekendMaster.setWeekendCode(getWeekendMasterDAO().save(
					weekendMaster, tableType));
			auditHeader.getAuditDetail().setModelData(weekendMaster);
			auditHeader.setAuditReference(weekendMaster
					.getWeekendCode());
		} else {
			getWeekendMasterDAO().update(weekendMaster, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving ");
		return auditHeader;
	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table SMTWeekendMaster by using WeekendMasterDAO's delete method with
	 * type as Blank 3) Audit the record in to AuditHeader and
	 * AdtSMTWeekendMaster by using auditHeaderDAO.addAudit(auditHeader)
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
			return auditHeader;
		}
		WeekendMaster weekendMaster = (WeekendMaster) auditHeader
		.getAuditDetail().getModelData();
		getWeekendMasterDAO().delete(weekendMaster, "");

		// auditID = getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving ");
		return auditHeader;
	}

	/**
	 * getWeekendMasterById fetch the details by using WeekendMasterDAO's
	 * getWeekendMasterById method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return WeekendMaster
	 */
	@Override
	public WeekendMaster getWeekendMasterById(String id) {
		return getWeekendMasterDAO().getWeekendMasterByID(id, "_View");
	}

	/**
	 * getApprovedWeekendMasterById fetch the details by using
	 * WeekendMasterDAO's getWeekendMasterById method . with parameter id and
	 * type as blank. it fetches the approved records from the SMTWeekendMaster.
	 * 
	 * @param id
	 *            (String)
	 * @return WeekendMaster
	 */
	public WeekendMaster getApprovedWeekendMasterById(String id) {
		return getWeekendMasterDAO().getWeekendMasterByID(id, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getWeekendMasterDAO().delete with parameters weekendMaster,"" b)
	 * NEW Add new record in to main table by using getWeekendMasterDAO().save
	 * with parameters weekendMaster,"" c) EDIT Update record in the main table
	 * by using getWeekendMasterDAO().update with parameters weekendMaster,"" 3)
	 * Delete the record from the workFlow table by using
	 * getWeekendMasterDAO().delete with parameters weekendMaster,"_Temp" 4)
	 * Audit the record in to AuditHeader and AdtSMTWeekendMaster by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in
	 * to AuditHeader and AdtSMTWeekendMaster by using
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
		WeekendMaster weekendMaster = new WeekendMaster();
		BeanUtils.copyProperties((WeekendMaster) auditHeader.getAuditDetail().getModelData(), weekendMaster);

		if (weekendMaster.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getWeekendMasterDAO().delete(weekendMaster, "");
		} else {
			weekendMaster.setRoleCode("");
			weekendMaster.setNextRoleCode("");
			weekendMaster.setTaskId("");
			weekendMaster.setNextTaskId("");
			weekendMaster.setWorkflowId(0);

			if (weekendMaster.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				weekendMaster.setRecordType("");
				getWeekendMasterDAO().save(weekendMaster, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				weekendMaster.setRecordType("");
				getWeekendMasterDAO().update(weekendMaster, "");
			}
		}

		getWeekendMasterDAO().delete(weekendMaster, "_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(weekendMaster);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getWeekendMasterDAO().delete with parameters
	 * weekendMaster,"_Temp" 3) Audit the record in to AuditHeader and
	 * AdtSMTWeekendMaster by using auditHeaderDAO.addAudit(auditHeader) for
	 * Work flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug("Entering ");
		WeekendMaster weekendMaster = (WeekendMaster) auditHeader
		.getAuditDetail().getModelData();

		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getWeekendMasterDAO().delete(weekendMaster, "_Temp");

		// long auditID = getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving ");
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
	 * getAcademicDAO().getErrorDetail with Error ID and language as parameters.
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
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());

		WeekendMaster weekendMaster = (WeekendMaster) auditDetail
		.getModelData();

		WeekendMaster tempWeekendMaster = null;
		if (weekendMaster.isWorkflow()) {
			tempWeekendMaster = getWeekendMasterDAO().getWeekendMasterByID(
					weekendMaster.getId(), "_Temp");
		}

		WeekendMaster befWeekendMaster = getWeekendMasterDAO()
		.getWeekendMasterByID(weekendMaster.getId(), "");
		WeekendMaster oldWeekendMaster = weekendMaster.getBefImage();

		String[] valueParm = new String[1];
		String[] errParm = new String[1];

		valueParm[0] = weekendMaster.getId();

		errParm[0] = PennantJavaUtil.getLabel("label_WeekendCode") + ":"
		+ valueParm[0];

		if (weekendMaster.isNew()) { // for New record or new record into work
			// flow

			if (!weekendMaster.isWorkflow()) {// With out Work flow only new
				// records
				if (befWeekendMaster != null) { // Record Already Exists in the
					// table
					// then error
					auditDetail
					.setErrorDetail(new ErrorDetail(
							PennantConstants.KEY_FIELD, "41001",
							errParm, null));
				}
			} else { // with work flow

				if (weekendMaster.getRecordType().equals(
						PennantConstants.RECORD_TYPE_NEW)) { // if records type
					// is new
					if (befWeekendMaster != null || tempWeekendMaster != null) { // if records
																// already exists
																// in the main table
						auditDetail.setErrorDetail(new ErrorDetail(
								PennantConstants.KEY_FIELD, "41001", errParm,
								null));
					}
				} else { // if records not exists in the Main flow table
					if (befWeekendMaster == null || tempWeekendMaster != null) {
						auditDetail.setErrorDetail(new ErrorDetail(
								PennantConstants.KEY_FIELD, "41005", errParm,
								null));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!weekendMaster.isWorkflow()) { // With out Work flow for update
				// and
				// delete
				if (befWeekendMaster == null) { // if records not exists in the
					// main
					// table
					auditDetail
					.setErrorDetail(new ErrorDetail(
							PennantConstants.KEY_FIELD, "41002",
							errParm, null));
				} else {
					if (oldWeekendMaster != null
							&& !oldWeekendMaster.getLastMntOn().equals(
									befWeekendMaster.getLastMntOn())) {
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
				if (tempWeekendMaster == null) { // if records not exists in the
					// Work flow table
					auditDetail
					.setErrorDetail(new ErrorDetail(
							PennantConstants.KEY_FIELD, "41005",
							errParm, null));
				}

				if (tempWeekendMaster != null
						&& oldWeekendMaster != null
						&& !oldWeekendMaster.getLastMntOn().equals(
								tempWeekendMaster.getLastMntOn())) {
					auditDetail
					.setErrorDetail(new ErrorDetail(
							PennantConstants.KEY_FIELD, "41005",
							errParm, null));
				}
			}
		}
		// auditDetail.setErrorDetail(new
		// ErrorDetails(PennantConstants.KEY_FIELD,"81001",errParm,null));
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(
				auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method))
				|| !weekendMaster.isWorkflow()) {
			auditDetail.setBefImage(befWeekendMaster);
		}

		logger.debug("Leaving");
		return auditDetail;
	}

}