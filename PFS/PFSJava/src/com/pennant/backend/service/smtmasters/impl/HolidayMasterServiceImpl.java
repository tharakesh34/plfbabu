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

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.smtmasters.HolidayMasterDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.smtmasters.HolidayMaster;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.smtmasters.HolidayMasterService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>HolidayMaster</b>.<br>
 * 
 */
public class HolidayMasterServiceImpl extends GenericService<HolidayMaster>
		implements HolidayMasterService {
	private static Logger logger = Logger
			.getLogger(HolidayMasterServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private HolidayMasterDAO holidayMasterDAO;

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
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

	@Override
	public HolidayMaster getHolidayMaster() {
		return getHolidayMasterDAO().getHolidayMaster();
	}
	@Override
	public HolidayMaster getNewHolidayMaster() {
		return getHolidayMasterDAO().getNewHolidayMaster();
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
		HolidayMaster holidayMaster = (HolidayMaster) auditHeader
				.getAuditDetail().getModelData();

		if (holidayMaster.isWorkflow()) {
			tableType = "_TEMP";
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
			auditHeader.setAuditReference(String.valueOf(holidayMaster
					.getHolidayCode()));
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
		
		HolidayMaster holidayMaster = (HolidayMaster) auditHeader
				.getAuditDetail().getModelData();
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
	public HolidayMaster getHolidayMasterById(String id, BigDecimal year,
			String holidayType) {
		return getHolidayMasterDAO().getHolidayMasterByID(id, year,
				holidayType, "_View");
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
	public List<HolidayMaster> getHolidayMasterCodeYear(String holidayCode,
			BigDecimal year) {
		return getHolidayMasterDAO().getHolidayMasterCodeYear(holidayCode,
				year, "_View");
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
	public HolidayMaster getApprovedHolidayMasterById(String id,
			BigDecimal year, String holidayType) {
		return getHolidayMasterDAO().getHolidayMasterByID(id, year,
				holidayType, "_AView");
	}

	/**
	 * This method refresh the Record.
	 * 
	 * @param HolidayMaster
	 *            (holidayMaster)
	 * @return holidayMaster
	 */
	@Override
	public HolidayMaster refresh(HolidayMaster holidayMaster) {
		logger.debug("Entering ");
		getHolidayMasterDAO().refresh(holidayMaster);
		getHolidayMasterDAO().initialize(holidayMaster);
		logger.debug("Leaving ");
		return holidayMaster;
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
		logger.debug("Entering ");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetails>());

		HolidayMaster holidayMaster = (HolidayMaster) auditDetail
				.getModelData();
		HolidayMaster tempHolidayMaster = null;
		if (holidayMaster.isWorkflow()) {
			tempHolidayMaster = getHolidayMasterDAO().getHolidayMasterByID(
					holidayMaster.getId(), holidayMaster.getHolidayYear(),
					holidayMaster.getHolidayType(), "_Temp");
		}
		HolidayMaster befHolidayMaster = getHolidayMasterDAO()
				.getHolidayMasterByID(holidayMaster.getId(),
						holidayMaster.getHolidayYear(),
						holidayMaster.getHolidayType(), "");

		HolidayMaster old_HolidayMaster = holidayMaster.getBefImage();

		String[] valueParm = new String[3];
		String[] errParm = new String[3];

		valueParm[0] = holidayMaster.getHolidayCode();
		valueParm[1] = holidayMaster.getHolidayYear().toString();
		valueParm[2] = holidayMaster.getHolidayType();

		errParm[0] = PennantJavaUtil
				.getLabel("label_HolidayMasterDialog_HolidayCode")
				+ ":"
				+ valueParm[0];
		errParm[1] = PennantJavaUtil
				.getLabel("label_HolidayMasterDialog_HolidayYear")
				+ ":"
				+ valueParm[1];
		errParm[2] = PennantJavaUtil
				.getLabel("label_HolidayMasterDialog_HolidayType")
				+ ":"
				+ valueParm[2];

		if (holidayMaster.isNew()) { // for New record or new record into work
			// flow

			if (!holidayMaster.isWorkflow()) {// With out Work flow only new
				// records
				if (befHolidayMaster != null) { // Record Already Exists in the
					// table then error
					auditDetail
							.setErrorDetail(new ErrorDetails(
									PennantConstants.KEY_FIELD, "41001",
									errParm, null));
				}
			} else { // with work flow
				if (tempHolidayMaster != null) { // if records already exists in
					// the Work flow table
					auditDetail
							.setErrorDetail(new ErrorDetails(
									PennantConstants.KEY_FIELD, "41005",
									errParm, null));
					return auditDetail;
				}

				/*
				 * if (holidayMaster.getRecordType().equals(PennantConstants.
				 * RECORD_TYPE_NEW)){ // if records type is new if
				 * (befHolidayMaster !=null){ // if records already exists in
				 * the main table
				 * auditHeader.setErrorDetails(getHolidayMasterDAO
				 * ().getErrorDetail("41001",
				 * auditHeader.getUsrLanguage(),parm)); return auditHeader; } }
				 */
				else { // if records not exists in the Main flow table
					if (befHolidayMaster == null) {
						auditDetail.setErrorDetail(new ErrorDetails(
								PennantConstants.KEY_FIELD, "41005", errParm,
								null));
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
					auditDetail
							.setErrorDetail(new ErrorDetails(
									PennantConstants.KEY_FIELD, "41002",
									errParm, null));
				}

				if (old_HolidayMaster != null
						&& !old_HolidayMaster.getLastMntOn().equals(
								befHolidayMaster.getLastMntOn())) {
					if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
							.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
						auditDetail.setErrorDetail(new ErrorDetails(
								PennantConstants.KEY_FIELD, "41003", errParm,
								null));
					} else {
						auditDetail.setErrorDetail(new ErrorDetails(
								PennantConstants.KEY_FIELD, "41004", errParm,
								null));
					}
				}
			} else {
				if (tempHolidayMaster == null) { // if records not exists in the
					// Work flow table
					auditDetail
							.setErrorDetail(new ErrorDetails(
									PennantConstants.KEY_FIELD, "41005",
									errParm, null));
				}

				if (old_HolidayMaster != null
						&& !old_HolidayMaster.getLastMntOn().equals(
								tempHolidayMaster.getLastMntOn())) {
					auditDetail
							.setErrorDetail(new ErrorDetails(
									PennantConstants.KEY_FIELD, "41005",
									errParm, null));
				}

			}
		}
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(
				auditDetail.getErrorDetails(), usrLanguage));

		if (StringUtils.trimToEmpty(method).equals("doApprove")) {
			holidayMaster.setBefImage(befHolidayMaster);
		}
		logger.debug("Leaving ");
		return auditDetail;
	}
}