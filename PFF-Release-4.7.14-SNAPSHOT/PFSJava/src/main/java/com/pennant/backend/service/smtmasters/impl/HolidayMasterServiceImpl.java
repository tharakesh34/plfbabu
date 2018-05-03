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
 * FileName    		:  HolidayMasterServiceImpl.java                                                   * 	  
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.smtmasters.HolidayMasterDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.smtmasters.HolidayMaster;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.smtmasters.HolidayMasterService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;

/**
 * Service implementation for methods that depends on <b>HolidayMaster</b>.<br>
 * 
 */
public class HolidayMasterServiceImpl extends GenericService<HolidayMaster> implements HolidayMasterService {
	private static Logger logger = Logger.getLogger(HolidayMasterServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private HolidayMasterDAO holidayMasterDAO;

	public HolidayMasterServiceImpl() {
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

	public HolidayMasterDAO getHolidayMasterDAO() {
		return holidayMasterDAO;
	}

	public void setHolidayMasterDAO(HolidayMasterDAO holidayMasterDAO) {
		this.holidayMasterDAO = holidayMasterDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * SMTHolidayMaster/SMTHolidayMaster_Temp by using HolidayMasterDAO's save
	 * method b) Update the Record in the table. based on the module workFlow
	 * Configuration. by using HolidayMasterDAO's update method 3) Audit the
	 * record in to AuditHeader and AdtSMTHolidayMaster by using
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
		HolidayMaster holidayMaster = (HolidayMaster) auditHeader.getAuditDetail().getModelData();

		if (holidayMaster.isWorkflow()) {
			tableType = "_Temp";
		} else {
			if (holidayMaster.isNew()) {
				auditHeader.setAuditTranType(PennantConstants.TRAN_ADD);
			} else {
				auditHeader.setAuditTranType(PennantConstants.TRAN_UPD);
			}
		}

		if (holidayMaster.isNew()) {
			getHolidayMasterDAO().save(holidayMaster, tableType);
			auditHeader.getAuditDetail().setModelData(holidayMaster);
			auditHeader.setAuditReference(holidayMaster.getHolidayCode());
		} else {
			getHolidayMasterDAO().update(holidayMaster, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving ");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table SMTHolidayMaster by using HolidayMasterDAO's delete method with
	 * type as Blank 3) Audit the record in to AuditHeader and
	 * AdtSMTHolidayMaster by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering ");

		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		HolidayMaster holidayMaster = (HolidayMaster) auditHeader.getAuditDetail().getModelData();
		getHolidayMasterDAO().delete(holidayMaster, "");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving ");
		return auditHeader;
	}

	/**
	 * getHolidayMasterById fetch the details by using HolidayMasterDAO's
	 * getHolidayMasterById method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return HolidayMaster
	 */
	@Override
	public HolidayMaster getHolidayMasterById(String id, BigDecimal year) {
		return getHolidayMasterDAO().getHolidayMasterByID(id, year, "_View");
	}

	/**
	 * getApprovedHolidayMasterCodeYear fetch the details by using
	 * HolidayMasterDAO's getHolidayMasterById method . with parameter code and
	 * Year. it fetches the approved records from the SMTHolidayMaster.
	 * 
	 * @param code
	 *            (String)
	 * @return List
	 */
	@Override
	public List<HolidayMaster> getHolidayMasterCodeYear(String holidayCode, BigDecimal year) {
		return getHolidayMasterDAO().getHolidayMasterCodeYear(holidayCode, year, "_View");
	}

	/**
	 * getApprovedHolidayMasterById fetch the details by using
	 * HolidayMasterDAO's getHolidayMasterById method . with parameter id and
	 * type. it fetches the approved records from the SMTHolidayMaster.
	 * 
	 * @param id
	 *            (String)
	 * @return HolidayMaster
	 */
	public HolidayMaster getApprovedHolidayMasterById(String id, BigDecimal year) {
		return getHolidayMasterDAO().getHolidayMasterByID(id, year, "_AView");
	}
	
	
	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getCountryDAO().delete with parameters country,"" b) NEW Add new
	 * record in to main table by using getCountryDAO().save with parameters
	 * country,"" c) EDIT Update record in the main table by using
	 * getCountryDAO().update with parameters country,"" 3) Delete the record
	 * from the workFlow table by using getCountryDAO().delete with parameters
	 * country,"_Temp" 4) Audit the record in to AuditHeader and AdtBMTCountries
	 * by using auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the
	 * record in to AuditHeader and AdtBMTCountries by using
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
		HolidayMaster holidayMaster = new HolidayMaster();
		BeanUtils.copyProperties((HolidayMaster) auditHeader.getAuditDetail().getModelData(), holidayMaster);

		if (holidayMaster.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getHolidayMasterDAO().delete(holidayMaster, "");
		} else {
			holidayMaster.setRoleCode("");
			holidayMaster.setNextRoleCode("");
			holidayMaster.setTaskId("");
			holidayMaster.setNextTaskId("");
			holidayMaster.setWorkflowId(0);

			if (holidayMaster.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				holidayMaster.setRecordType("");
				getHolidayMasterDAO().save(holidayMaster, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				holidayMaster.setRecordType("");
				getHolidayMasterDAO().update(holidayMaster, "");
			}
		}

		getHolidayMasterDAO().delete(holidayMaster, "_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(holidayMaster);
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
	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
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
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug("Entering ");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());

		HolidayMaster holidayMaster = (HolidayMaster) auditDetail.getModelData();
		HolidayMaster tempHolidayMaster = null;
		if (holidayMaster.isWorkflow()) {
			tempHolidayMaster = getHolidayMasterDAO().getHolidayMasterByID(holidayMaster.getId(),
					holidayMaster.getHolidayYear(), "_Temp");
		}
		HolidayMaster befHolidayMaster = getHolidayMasterDAO().getHolidayMasterByID(holidayMaster.getId(),
				holidayMaster.getHolidayYear(), "");

		HolidayMaster oldHolidayMaster = holidayMaster.getBefImage();

		String[] valueParm = new String[3];
		String[] errParm = new String[3];

		valueParm[0] = holidayMaster.getHolidayCode();
		valueParm[1] = holidayMaster.getHolidayYear().toString();
		valueParm[2] = holidayMaster.getHolidayType();

		errParm[0] = PennantJavaUtil.getLabel("label_HolidayMasterDialog_HolidayCode.value") + ":" + valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_HolidayMasterDialog_HolidayYear.value") + ":" + valueParm[1];
		errParm[2] = PennantJavaUtil.getLabel("label_HolidayMasterDialog_HolidayType.value") + ":" + valueParm[2];

		if (holidayMaster.isNew()) { // for New record or new record into work
			// flow

			if (!holidayMaster.isWorkflow()) {// With out Work flow only new
				// records
				if (befHolidayMaster != null) { // Record Already Exists in the
					// table then error
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, null));
				}
			} else {
				// with work flow

				if (holidayMaster.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if
																								// records
																								// type
					// is new
					if (befHolidayMaster != null || tempHolidayMaster != null) { // if
																					// records
						// already exists
						// in the main table
						auditDetail
								.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, null));
					}
				} else { // if records not exists in the Main flow table
					if (befHolidayMaster == null || tempHolidayMaster != null) {
						auditDetail
								.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
					}
				}

			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!holidayMaster.isWorkflow()) { // With out Work flow for update
				// and delete

				if (befHolidayMaster == null) { // if records not exists in the
					// main table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, null));
				} else {
					if (oldHolidayMaster != null
							&& !oldHolidayMaster.getLastMntOn().equals(befHolidayMaster.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(
								PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm,
									null));
						} else {
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm,
									null));
						}
					}
				}

			} else {
				if (tempHolidayMaster == null) { // if records not exists in the
					// Work flow table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
				}

				if (tempHolidayMaster != null && oldHolidayMaster != null
						&& !oldHolidayMaster.getLastMntOn().equals(tempHolidayMaster.getLastMntOn())) {
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
				}

			}
		}
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method))) {
			holidayMaster.setBefImage(befHolidayMaster);
		}
		logger.debug("Leaving ");
		return auditDetail;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getCountryDAO().delete with parameters
	 * country,"_Temp" 3) Audit the record in to AuditHeader and AdtBMTCountries
	 * by using auditHeaderDAO.addAudit(auditHeader) for Work flow
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
		HolidayMaster holidayMaster = (HolidayMaster) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getHolidayMasterDAO().delete(holidayMaster, "_Temp");

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}
}