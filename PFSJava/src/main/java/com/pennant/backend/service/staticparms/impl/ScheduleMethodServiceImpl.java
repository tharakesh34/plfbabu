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
 * FileName    		:  ScheduleMethodServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-09-2011    														*
 *                                                                  						*
 * Modified Date    :  12-09-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-09-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.service.staticparms.impl;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.staticparms.ScheduleMethodDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.staticparms.ScheduleMethod;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.staticparms.ScheduleMethodService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;

/**
 * Service implementation for methods that depends on <b>ScheduleMethod</b>.<br>
 * 
 */
public class ScheduleMethodServiceImpl extends GenericService<ScheduleMethod> implements ScheduleMethodService {

	private static final Logger logger = Logger.getLogger(ScheduleMethodServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private ScheduleMethodDAO scheduleMethodDAO;

	public ScheduleMethodServiceImpl() {
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

	public ScheduleMethodDAO getScheduleMethodDAO() {
		return scheduleMethodDAO;
	}

	public void setScheduleMethodDAO(ScheduleMethodDAO scheduleMethodDAO) {
		this.scheduleMethodDAO = scheduleMethodDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * BMTSchdMethod/BMTSchdMethod_Temp by using ScheduleMethodDAO's save method
	 * b) Update the Record in the table. based on the module workFlow
	 * Configuration. by using ScheduleMethodDAO's update method 3) Audit the
	 * record in to AuditHeader and AdtBMTSchdMethod by using
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
		ScheduleMethod scheduleMethod = (ScheduleMethod) auditHeader.getAuditDetail().getModelData();

		if (scheduleMethod.isWorkflow()) {
			tableType = "_Temp";
		}

		if (scheduleMethod.isNew()) {
			getScheduleMethodDAO().save(scheduleMethod, tableType);
		} else {
			getScheduleMethodDAO().update(scheduleMethod, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table BMTSchdMethod by using ScheduleMethodDAO's delete method with type
	 * as Blank 3) Audit the record in to AuditHeader and AdtBMTSchdMethod by
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

		ScheduleMethod scheduleMethod = (ScheduleMethod) auditHeader.getAuditDetail().getModelData();
		getScheduleMethodDAO().delete(scheduleMethod, "");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getScheduleMethodById fetch the details by using ScheduleMethodDAO's
	 * getScheduleMethodById method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return ScheduleMethod
	 */

	@Override
	public ScheduleMethod getScheduleMethodById(String id) {
		return getScheduleMethodDAO().getScheduleMethodById(id, "_View");
	}

	/**
	 * getApprovedScheduleMethodById fetch the details by using
	 * ScheduleMethodDAO's getScheduleMethodById method . with parameter id and
	 * type as blank. it fetches the approved records from the BMTSchdMethod.
	 * 
	 * @param id
	 *            (String)
	 * @return ScheduleMethod
	 */

	public ScheduleMethod getApprovedScheduleMethodById(String id) {
		return getScheduleMethodDAO().getScheduleMethodById(id, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getScheduleMethodDAO().delete with parameters scheduleMethod,"" b)
	 * NEW Add new record in to main table by using getScheduleMethodDAO().save
	 * with parameters scheduleMethod,"" c) EDIT Update record in the main table
	 * by using getScheduleMethodDAO().update with parameters scheduleMethod,""
	 * 3) Delete the record from the workFlow table by using
	 * getScheduleMethodDAO().delete with parameters scheduleMethod,"_Temp" 4)
	 * Audit the record in to AuditHeader and AdtBMTSchdMethod by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in
	 * to AuditHeader and AdtBMTSchdMethod by using
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
			return auditHeader;
		}

		ScheduleMethod scheduleMethod = new ScheduleMethod();
		BeanUtils.copyProperties((ScheduleMethod) auditHeader.getAuditDetail().getModelData(), scheduleMethod);

		if (scheduleMethod.getRecordType().equals(
				PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			getScheduleMethodDAO().delete(scheduleMethod, "");

		} else {
			scheduleMethod.setRoleCode("");
			scheduleMethod.setNextRoleCode("");
			scheduleMethod.setTaskId("");
			scheduleMethod.setNextTaskId("");
			scheduleMethod.setWorkflowId(0);

			if (scheduleMethod.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				scheduleMethod.setRecordType("");
				getScheduleMethodDAO().save(scheduleMethod, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				scheduleMethod.setRecordType("");
				getScheduleMethodDAO().update(scheduleMethod, "");
			}
		}

		getScheduleMethodDAO().delete(scheduleMethod, "_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(scheduleMethod);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getScheduleMethodDAO().delete with parameters
	 * scheduleMethod,"_Temp" 3) Audit the record in to AuditHeader and
	 * AdtBMTSchdMethod by using auditHeaderDAO.addAudit(auditHeader) for Work
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

		ScheduleMethod scheduleMethod = (ScheduleMethod) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getScheduleMethodDAO().delete(scheduleMethod, "_Temp");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from
	 * the auditHeader. 2) fetch the details from the tables 3) Validate the
	 * Record based on the record details. 4) Validate for any business
	 * validation. 5) for any mismatch conditions Fetch the error details from
	 * getScheduleMethodDAO().getErrorDetail with Error ID and language as
	 * parameters. 6) if any error/Warnings then assign the to auditHeader
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader,
			String method) {
		logger.debug("Entering");
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(),auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any
	 * mismatch conditions Fetch the error details from
	 * getScheduleMethodDAO().getErrorDetail with Error ID and language as
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

		ScheduleMethod scheduleMethod = (ScheduleMethod) auditDetail.getModelData();
		ScheduleMethod tempScheduleMethod = null;

		if (scheduleMethod.isWorkflow()) {
			tempScheduleMethod = getScheduleMethodDAO().getScheduleMethodById(scheduleMethod.getId(), "_Temp");
		}

		ScheduleMethod befScheduleMethod = getScheduleMethodDAO()
		.getScheduleMethodById(scheduleMethod.getId(), "");
		ScheduleMethod oldScheduleMethod = scheduleMethod.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];

		valueParm[0] = scheduleMethod.getId();
		errParm[0] = PennantJavaUtil.getLabel("label_SchdMethod") + ":"+ valueParm[0];

		if (scheduleMethod.isNew()) { // for New record or new record into work flow

			if (!scheduleMethod.isWorkflow()) {// With out Work flow only new records
				if (befScheduleMethod != null) { // Record Already Exists in the table then error
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD,"41001", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow
				if (scheduleMethod.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befScheduleMethod != null || tempScheduleMethod != null) { // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41001", errParm, valueParm),usrLanguage));
					}
				} else { // if records not exists in the Main flow table
					if (befScheduleMethod == null || tempScheduleMethod != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41005", errParm, valueParm),usrLanguage));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!scheduleMethod.isWorkflow()) { // With out Work flow for update and delete

				if (befScheduleMethod == null) { // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41002", errParm, valueParm), usrLanguage));
				} else {
					if (oldScheduleMethod != null
							&& !oldScheduleMethod.getLastMntOn().equals(befScheduleMethod.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41003", errParm, valueParm),usrLanguage));
						} else {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41004", errParm, valueParm),usrLanguage));
						}
					}
				}
			} else {

				if (tempScheduleMethod == null) { // if records not exists in the Work flow table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD,"41005", errParm, valueParm), usrLanguage));
			}

				if (tempScheduleMethod != null && oldScheduleMethod != null
						&& !oldScheduleMethod.getLastMntOn().equals(tempScheduleMethod.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD,"41005", errParm, valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
	
		if ("doApprove".equals(StringUtils.trimToEmpty(method))
				|| !scheduleMethod.isWorkflow()) {
			scheduleMethod.setBefImage(befScheduleMethod);
		}
		logger.debug("Leaving");
		return auditDetail;
	}
}