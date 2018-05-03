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
 * FileName    		:  LanguageServiceImpl.java                                                   * 	  
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

package com.pennant.backend.service.staticparms.impl;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.staticparms.LanguageDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.staticparms.Language;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.staticparms.LanguageService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;

/**
 * Service implementation for methods that depends on <b>Language</b>.<br>
 * 
 */
public class LanguageServiceImpl extends GenericService<Language> implements LanguageService {

	private static Logger logger = Logger.getLogger(LanguageServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private LanguageDAO languageDAO;

	public LanguageServiceImpl() {
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

	public LanguageDAO getLanguageDAO() {
		return languageDAO;
	}

	public void setLanguageDAO(LanguageDAO languageDAO) {
		this.languageDAO = languageDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * BMTLanguage/BMTLanguage_Temp by using LanguageDAO's save method b) Update
	 * the Record in the table. based on the module workFlow Configuration. by
	 * using LanguageDAO's update method 3) Audit the record in to AuditHeader
	 * and AdtBMTLanguage by using auditHeaderDAO.addAudit(auditHeader)
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
		Language language = (Language) auditHeader.getAuditDetail().getModelData();

		if (language.isWorkflow()) {
			tableType = "_Temp";
		}

		if (language.isNew()) {
			language.setId(getLanguageDAO().save(language, tableType));
			auditHeader.getAuditDetail().setModelData(language);
			auditHeader.setAuditReference(language.getId());
		} else {
			getLanguageDAO().update(language, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table BMTLanguage by using LanguageDAO's delete method with type as Blank
	 * 3) Audit the record in to AuditHeader and AdtBMTLanguage by using
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
		Language language = (Language) auditHeader.getAuditDetail().getModelData();

		getLanguageDAO().delete(language, "");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getLanguageById fetch the details by using LanguageDAO's getLanguageById
	 * method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return Language
	 */
	@Override
	public Language getLanguageById(String id) {
		return getLanguageDAO().getLanguageById(id, "_View");
	}

	/**
	 * getApprovedLanguageById fetch the details by using LanguageDAO's
	 * getLanguageById method . with parameter id and type as blank. it fetches
	 * the approved records from the BMTLanguage.
	 * 
	 * @param id
	 *            (String)
	 * @return Language
	 */
	public Language getApprovedLanguageById(String id) {
		return getLanguageDAO().getLanguageById(id, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getLanguageDAO().delete with parameters language,"" b) NEW Add new
	 * record in to main table by using getLanguageDAO().save with parameters
	 * language,"" c) EDIT Update record in the main table by using
	 * getLanguageDAO().update with parameters language,"" 3) Delete the record
	 * from the workFlow table by using getLanguageDAO().delete with parameters
	 * language,"_Temp" 4) Audit the record in to AuditHeader and AdtBMTLanguage
	 * by using auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the
	 * record in to AuditHeader and AdtBMTLanguage by using
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
		Language language = new Language();
		BeanUtils.copyProperties((Language) auditHeader.getAuditDetail().getModelData(), language);

		if (language.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			getLanguageDAO().delete(language, "");

		} else {
			language.setRoleCode("");
			language.setNextRoleCode("");
			language.setTaskId("");
			language.setNextTaskId("");
			language.setWorkflowId(0);

			if (language.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				language.setRecordType("");
				getLanguageDAO().save(language, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				language.setRecordType("");
				getLanguageDAO().update(language, "");
			}
		}

		getLanguageDAO().delete(language, "_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(language);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getLanguageDAO().delete with parameters
	 * language,"_Temp" 3) Audit the record in to AuditHeader and AdtBMTLanguage
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
		Language language = (Language) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getLanguageDAO().delete(language, "_Temp");

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
	 * getLanguageDAO().getErrorDetail with Error ID and language as parameters.
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

		Language language = (Language) auditDetail.getModelData();
		Language tempLanguage = null;

		if (language.isWorkflow()) {
			tempLanguage = getLanguageDAO().getLanguageById(language.getId(),"_Temp");
		}

		Language befLanguage = getLanguageDAO().getLanguageById(language.getId(), "");
		Language oldLanguage = language.getBefImage();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = language.getLngCode();
		errParm[0] = PennantJavaUtil.getLabel("label_LngCode") + ":"
		+ valueParm[0];

		if (language.isNew()) { // for New record or new record into work flow

			if (!language.isWorkflow()) {// With out Work flow only new records
				if (befLanguage != null) { // Record Already Exists in the table then error
					auditDetail
					.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001",errParm, null));
				}
			} else { // with work flow

				if (language.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befLanguage != null || tempLanguage != null) { // if records already exists in the main table
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,null));
					}
				} else { // if records not exists in the Main flow table
					if (befLanguage == null || tempLanguage != null) {
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,null));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!language.isWorkflow()) { // With out Work flow for update and delete

				if (befLanguage == null) { // if records not exists in the main table
					auditDetail
					.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002",errParm, null));
				} else {

					if (oldLanguage != null
							&& !oldLanguage.getLastMntOn().equals(befLanguage.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41003",errParm, null));
						} else {
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41004",errParm, null));
						}
					}
				}

			} else {

				if (tempLanguage == null) { // if records not exists in the Work
					// flow table
					auditDetail
					.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005",errParm, null));
				}
				if (tempLanguage != null
						&& oldLanguage != null
						&& !oldLanguage.getLastMntOn().equals(tempLanguage.getLastMntOn())) {
					auditDetail
					.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005",errParm, null));
				}
			}
		}
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
		if ("doApprove".equals(StringUtils.trimToEmpty(method))|| !language.isWorkflow()) {
			auditDetail.setBefImage(befLanguage);
		}
		logger.debug("Leaving");
		return auditDetail;
	}

}