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
 * FileName    		:  FrequencyServiceImpl.java                                                   * 	  
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

package com.pennant.backend.service.staticparms.impl;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.staticparms.FrequencyDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.staticparms.Frequency;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.staticparms.FrequencyService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;

/**
 * Service implementation for methods that depends on <b>Frequency</b>.<br>
 * 
 */
public class FrequencyServiceImpl extends GenericService<Frequency> implements
		FrequencyService {

	private static Logger logger = Logger.getLogger(FrequencyServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private FrequencyDAO frequencyDAO;

	public FrequencyServiceImpl() {
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

	public FrequencyDAO getFrequencyDAO() {
		return frequencyDAO;
	}

	public void setFrequencyDAO(FrequencyDAO frequencyDAO) {
		this.frequencyDAO = frequencyDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * BMTFrequencies/BMTFrequencies_Temp by using FrequencyDAO's save method b)
	 * Update the Record in the table. based on the module workFlow
	 * Configuration. by using FrequencyDAO's update method 3) Audit the record
	 * in to AuditHeader and AdtBMTFrequencies by using
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
		Frequency frequency = (Frequency) auditHeader.getAuditDetail()
				.getModelData();

		if (frequency.isWorkflow()) {
			tableType = "_Temp";
		}

		if (frequency.isNew()) {
			frequency.setId(getFrequencyDAO().save(frequency, tableType));
			auditHeader.getAuditDetail().setModelData(frequency);
			auditHeader.setAuditReference(frequency.getId());
		} else {
			getFrequencyDAO().update(frequency, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table BMTFrequencies by using FrequencyDAO's delete method with type as
	 * Blank 3) Audit the record in to AuditHeader and AdtBMTFrequencies by
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
		Frequency frequency = (Frequency) auditHeader.getAuditDetail()
				.getModelData();

		getFrequencyDAO().delete(frequency, "");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getFrequencyById fetch the details by using FrequencyDAO's
	 * getFrequencyById method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return Frequency
	 */
	@Override
	public Frequency getFrequencyById(String id) {
		return getFrequencyDAO().getFrequencyById(id, "_View");
	}

	/**
	 * getApprovedFrequencyById fetch the details by using FrequencyDAO's
	 * getFrequencyById method . with parameter id and type as blank. it fetches
	 * the approved records from the BMTFrequencies.
	 * 
	 * @param id
	 *            (String)
	 * @return Frequency
	 */
	public Frequency getApprovedFrequencyById(String id) {
		return getFrequencyDAO().getFrequencyById(id, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getFrequencyDAO().delete with parameters frequency,"" b) NEW Add
	 * new record in to main table by using getFrequencyDAO().save with
	 * parameters frequency,"" c) EDIT Update record in the main table by using
	 * getFrequencyDAO().update with parameters frequency,"" 3) Delete the
	 * record from the workFlow table by using getFrequencyDAO().delete with
	 * parameters frequency,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtBMTFrequencies by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow 5) Audit the record in to AuditHeader and AdtBMTFrequencies by using
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
		Frequency frequency = new Frequency();
		BeanUtils.copyProperties((Frequency) auditHeader.getAuditDetail()
				.getModelData(), frequency);

		if (frequency.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			getFrequencyDAO().delete(frequency, "");

		} else {
			frequency.setRoleCode("");
			frequency.setNextRoleCode("");
			frequency.setTaskId("");
			frequency.setNextTaskId("");
			frequency.setWorkflowId(0);

			if (frequency.getRecordType().equals(
					PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				frequency.setRecordType("");
				getFrequencyDAO().save(frequency, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				frequency.setRecordType("");
				getFrequencyDAO().update(frequency, "");
			}
		}

		getFrequencyDAO().delete(frequency, "_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(frequency);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getFrequencyDAO().delete with parameters
	 * frequency,"_Temp" 3) Audit the record in to AuditHeader and
	 * AdtBMTFrequencies by using auditHeaderDAO.addAudit(auditHeader) for Work
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
		Frequency frequency = (Frequency) auditHeader.getAuditDetail()
				.getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getFrequencyDAO().delete(frequency, "_Temp");

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
	 * getFrequencyDAO().getErrorDetail with Error ID and language as
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

		Frequency frequency = (Frequency) auditDetail.getModelData();
		Frequency tempFrequency = null;

		if (frequency.isWorkflow()) {
			tempFrequency = getFrequencyDAO().getFrequencyById(
					frequency.getId(), "_Temp");
		}

		Frequency befFrequency = getFrequencyDAO().getFrequencyById(
				frequency.getId(), "");
		Frequency oldFrequency = frequency.getBefImage();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = frequency.getFrqCode();
		errParm[0] = PennantJavaUtil.getLabel("label_Frq_Code") + ":"
				+ valueParm[0];

		if (frequency.isNew()) { // for New record or new record into work flow

			if (!frequency.isWorkflow()) {// With out Work flow only new records
				if (befFrequency != null) { // Record Already Exists in the
					// table then error
					auditDetail
							.setErrorDetail(new ErrorDetail(
									PennantConstants.KEY_FIELD, "41001",
									errParm, null));
				}
			} else { // with work flow

				if (frequency.getRecordType().equals(
						PennantConstants.RECORD_TYPE_NEW)) { // if records type
					// is new
					if (befFrequency != null || tempFrequency != null) { // if
						  							// records already exists
													// in the main table
						auditDetail.setErrorDetail(new ErrorDetail(
								PennantConstants.KEY_FIELD, "41001", errParm,
								null));
					}
				} else { // if records not exists in the Main flow table
					if (befFrequency == null || tempFrequency != null) {
						auditDetail.setErrorDetail(new ErrorDetail(
								PennantConstants.KEY_FIELD, "41005", errParm,
								null));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!frequency.isWorkflow()) { // With out Work flow for update and
				// delete

				if (befFrequency == null) { // if records not exists in the main
					// table
					auditDetail
							.setErrorDetail(new ErrorDetail(
									PennantConstants.KEY_FIELD, "41002",
									errParm, null));
				} else {
					if (oldFrequency != null
							&& !oldFrequency.getLastMntOn().equals(
									befFrequency.getLastMntOn())) {
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
				if (tempFrequency == null) { // if records not exists in the
					// Work flow table
					auditDetail
							.setErrorDetail(new ErrorDetail(
									PennantConstants.KEY_FIELD, "41005",
									errParm, null));
				}
				if (tempFrequency != null
						&& oldFrequency != null
						&& !oldFrequency.getLastMntOn().equals(
								tempFrequency.getLastMntOn())) {
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
				|| !frequency.isWorkflow()) {
			auditDetail.setBefImage(befFrequency);
		}
		logger.debug("Leaving");
		return auditDetail;
	}
}