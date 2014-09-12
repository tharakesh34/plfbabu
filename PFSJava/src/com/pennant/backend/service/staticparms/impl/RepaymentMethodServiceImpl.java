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
 * FileName    		:  RepaymentMethodServiceImpl.java                                                   * 	  
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
import com.pennant.backend.dao.staticparms.RepaymentMethodDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.staticparms.RepaymentMethod;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.staticparms.RepaymentMethodService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>RepaymentMethod</b>.<br>
 * 
 */
public class RepaymentMethodServiceImpl extends GenericService<RepaymentMethod> implements RepaymentMethodService {

	private final static Logger logger = Logger.getLogger(RepaymentMethodServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private RepaymentMethodDAO repaymentMethodDAO;

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public RepaymentMethodDAO getRepaymentMethodDAO() {
		return repaymentMethodDAO;
	}

	public void setRepaymentMethodDAO(RepaymentMethodDAO repaymentMethodDAO) {
		this.repaymentMethodDAO = repaymentMethodDAO;
	}

	@Override
	public RepaymentMethod getRepaymentMethod() {
		return getRepaymentMethodDAO().getRepaymentMethod();
	}

	@Override
	public RepaymentMethod getNewRepaymentMethod() {
		return getRepaymentMethodDAO().getNewRepaymentMethod();
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * BMTRepayMethod/BMTRepayMethod_Temp by using RepaymentMethodDAO's save
	 * method b) Update the Record in the table. based on the module workFlow
	 * Configuration. by using RepaymentMethodDAO's update method 3) Audit the
	 * record in to AuditHeader and AdtBMTRepayMethod by using
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
		RepaymentMethod repaymentMethod = (RepaymentMethod) auditHeader.getAuditDetail().getModelData();

		if (repaymentMethod.isWorkflow()) {
			tableType = "_TEMP";
		}

		if (repaymentMethod.isNew()) {
			getRepaymentMethodDAO().save(repaymentMethod, tableType);
		} else {
			getRepaymentMethodDAO().update(repaymentMethod, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table BMTRepayMethod by using RepaymentMethodDAO's delete method with
	 * type as Blank 3) Audit the record in to AuditHeader and AdtBMTRepayMethod
	 * by using auditHeaderDAO.addAudit(auditHeader)
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

		RepaymentMethod repaymentMethod = (RepaymentMethod) auditHeader.getAuditDetail().getModelData();
		getRepaymentMethodDAO().delete(repaymentMethod, "");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getRepaymentMethodById fetch the details by using RepaymentMethodDAO's
	 * getRepaymentMethodById method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return RepaymentMethod
	 */

	@Override
	public RepaymentMethod getRepaymentMethodById(String id) {
		return getRepaymentMethodDAO().getRepaymentMethodById(id, "_View");
	}

	/**
	 * getApprovedRepaymentMethodById fetch the details by using
	 * RepaymentMethodDAO's getRepaymentMethodById method . with parameter id
	 * and type as blank. it fetches the approved records from the
	 * BMTRepayMethod.
	 * 
	 * @param id
	 *            (String)
	 * @return RepaymentMethod
	 */

	public RepaymentMethod getApprovedRepaymentMethodById(String id) {
		return getRepaymentMethodDAO().getRepaymentMethodById(id, "_AView");
	}

	/**
	 * This method refresh the Record.
	 * 
	 * @param RepaymentMethod
	 *            (repaymentMethod)
	 * @return repaymentMethod
	 */
	@Override
	public RepaymentMethod refresh(RepaymentMethod repaymentMethod) {
		logger.debug("Entering");
		getRepaymentMethodDAO().refresh(repaymentMethod);
		getRepaymentMethodDAO().initialize(repaymentMethod);
		logger.debug("Leaving");
		return repaymentMethod;
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getRepaymentMethodDAO().delete with parameters repaymentMethod,""
	 * b) NEW Add new record in to main table by using
	 * getRepaymentMethodDAO().save with parameters repaymentMethod,"" c) EDIT
	 * Update record in the main table by using getRepaymentMethodDAO().update
	 * with parameters repaymentMethod,"" 3) Delete the record from the workFlow
	 * table by using getRepaymentMethodDAO().delete with parameters
	 * repaymentMethod,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtBMTRepayMethod by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow 5) Audit the record in to AuditHeader and AdtBMTRepayMethod by using
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

		RepaymentMethod repaymentMethod = new RepaymentMethod();
		BeanUtils.copyProperties((RepaymentMethod) auditHeader.getAuditDetail().getModelData(), repaymentMethod);

		if (repaymentMethod.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			getRepaymentMethodDAO().delete(repaymentMethod, "");

		} else {
			repaymentMethod.setRoleCode("");
			repaymentMethod.setNextRoleCode("");
			repaymentMethod.setTaskId("");
			repaymentMethod.setNextTaskId("");
			repaymentMethod.setWorkflowId(0);

			if (repaymentMethod.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				repaymentMethod.setRecordType("");
				getRepaymentMethodDAO().save(repaymentMethod, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				repaymentMethod.setRecordType("");
				getRepaymentMethodDAO().update(repaymentMethod, "");
			}
		}

		getRepaymentMethodDAO().delete(repaymentMethod, "_TEMP");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(repaymentMethod);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getRepaymentMethodDAO().delete with
	 * parameters repaymentMethod,"_Temp" 3) Audit the record in to AuditHeader
	 * and AdtBMTRepayMethod by using auditHeaderDAO.addAudit(auditHeader) for
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

		RepaymentMethod repaymentMethod = (RepaymentMethod) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getRepaymentMethodDAO().delete(repaymentMethod, "_TEMP");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from
	 * the auditHeader. 2) fetch the details from the tables 3) Validate the
	 * Record based on the record details. 4) Validate for any business
	 * validation. 5) for any mismatch conditions Fetch the error details from
	 * getRepaymentMethodDAO().getErrorDetail with Error ID and language as
	 * parameters. 6) if any error/Warnings then assign the to auditHeader
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */

	private AuditHeader businessValidation(AuditHeader auditHeader,String method) {
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
	 * getRepaymentMethodDAO().getErrorDetail with Error ID and language as
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

		RepaymentMethod repaymentMethod = (RepaymentMethod) auditDetail.getModelData();
		RepaymentMethod tempRepaymentMethod = null;

		if (repaymentMethod.isWorkflow()) {
			tempRepaymentMethod = getRepaymentMethodDAO().getRepaymentMethodById(repaymentMethod.getId(), "_Temp");
		}

		RepaymentMethod befRepaymentMethod = getRepaymentMethodDAO()
		.getRepaymentMethodById(repaymentMethod.getId(), "");
		RepaymentMethod oldRepaymentMethod = repaymentMethod.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];

		valueParm[0] = repaymentMethod.getId();
		errParm[0] = PennantJavaUtil.getLabel("label_RepayMethod") + ":"
		+ valueParm[0];

		if (repaymentMethod.isNew()) { // for New record or new record into work flow

			if (!repaymentMethod.isWorkflow()) {// With out Work flow only new records
				if (befRepaymentMethod != null) { // Record Already Exists in the table then error
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41001", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow
				if (repaymentMethod.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befRepaymentMethod != null
							|| tempRepaymentMethod != null) { // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41001", errParm, valueParm),usrLanguage));
					}
				} else { // if records not exists in the Main flow table
					if (befRepaymentMethod == null|| tempRepaymentMethod != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005", errParm, valueParm),usrLanguage));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!repaymentMethod.isWorkflow()) { // With out Work flow for update and delete

				if (befRepaymentMethod == null) { // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41002", errParm, valueParm), usrLanguage));
				} else {
					if (oldRepaymentMethod != null
							&& !oldRepaymentMethod.getLastMntOn().equals(befRepaymentMethod.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41003", errParm, valueParm),usrLanguage));
						} else {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41004", errParm, valueParm),usrLanguage));
						}
					}
				}
			} else {

				if (tempRepaymentMethod == null) { // if records not exists in the Work flow table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005", errParm, valueParm), usrLanguage));
				}

				if (oldRepaymentMethod != null
						&& !oldRepaymentMethod.getLastMntOn().equals(tempRepaymentMethod.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005", errParm, valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if (StringUtils.trimToEmpty(method).equals("doApprove")|| !repaymentMethod.isWorkflow()) {
			repaymentMethod.setBefImage(befRepaymentMethod);
		}
		logger.debug("Leaving");
		return auditDetail;
	}
}