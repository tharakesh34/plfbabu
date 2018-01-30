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
 * FileName    		:  ProfessionServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  03-05-2011    														*
 *                                                                  						*
 * Modified Date    :  03-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-05-2011       Pennant	                 0.1                                            * 
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

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.systemmasters.ProfessionDAO;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.Profession;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.systemmasters.ProfessionService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>Profession</b>.<br>
 * 
 */
public class ProfessionServiceImpl extends GenericService<Profession> implements ProfessionService {

	private static Logger logger = Logger.getLogger(ProfessionServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private ProfessionDAO professionDAO;

	public ProfessionServiceImpl() {
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

	public ProfessionDAO getProfessionDAO() {
		return professionDAO;
	}

	public void setProfessionDAO(ProfessionDAO professionDAO) {
		this.professionDAO = professionDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * BMTProfessions/BMTProfessions_Temp by using ProfessionDAO's save method
	 * b) Update the Record in the table. based on the module workFlow
	 * Configuration. by using ProfessionDAO's update method 3) Audit the record
	 * in to AuditHeader and AdtBMTProfessions by using
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
		
		Profession profession = (Profession) auditHeader.getAuditDetail().getModelData();
		
		TableType tableType = TableType.MAIN_TAB;
		if (profession.isWorkflow()) {
			tableType = TableType.TEMP_TAB;;
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		}

		if (profession.isNew()) {
			profession.setProfessionCode(getProfessionDAO().save(profession,tableType));
			auditHeader.getAuditDetail().setModelData(profession);
			auditHeader.setAuditReference(profession.getProfessionCode());
		} else {
			getProfessionDAO().update(profession, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table BMTProfessions by using ProfessionDAO's delete method with type as
	 * Blank 3) Audit the record in to AuditHeader and AdtBMTProfessions by
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
			return auditHeader;
		}
		Profession profession = (Profession) auditHeader.getAuditDetail().getModelData();
		getProfessionDAO().delete(profession, TableType.MAIN_TAB);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getProfessionById fetch the details by using ProfessionDAO's
	 * getProfessionById method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return Profession""
	 */
	@Override
	public Profession getProfessionById(String id) {
		return getProfessionDAO().getProfessionById(id, "_View");
	}

	/**
	 * getApprovedProfessionById fetch the details by using ProfessionDAO's
	 * getProfessionById method . with parameter id and type as blank. it
	 * fetches the approved records from the BMTProfessions.
	 * 
	 * @param id
	 *            (String)
	 * @return Profession
	 */
	public Profession getApprovedProfessionById(String id) {
		return getProfessionDAO().getProfessionById(id, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getProfessionDAO().delete with parameters profession,"" b) NEW Add
	 * new record in to main table by using getProfessionDAO().save with
	 * parameters profession,"" c) EDIT Update record in the main table by using
	 * getProfessionDAO().update with parameters profession,"" 3) Delete the
	 * record from the workFlow table by using getProfessionDAO().delete with
	 * parameters profession,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtBMTProfessions by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow 5) Audit the record in to AuditHeader and AdtBMTProfessions by using
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
		Profession profession = new Profession();
		BeanUtils.copyProperties((Profession) auditHeader.getAuditDetail().getModelData(), profession);
		
		getProfessionDAO().delete(profession, TableType.TEMP_TAB);
		
		if (!PennantConstants.RECORD_TYPE_NEW.equals(profession.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(professionDAO.getProfessionById(profession.getProfessionCode(), ""));
		}
		
		if (profession.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			getProfessionDAO().delete(profession, TableType.MAIN_TAB);

		} else {
			profession.setRoleCode("");
			profession.setNextRoleCode("");
			profession.setTaskId("");
			profession.setNextTaskId("");
			profession.setWorkflowId(0);

			if (profession.getRecordType().equals(
					PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				profession.setRecordType("");
				getProfessionDAO().save(profession, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				profession.setRecordType("");
				getProfessionDAO().update(profession, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(profession);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getProfessionDAO().delete with parameters
	 * profession,"_Temp" 3) Audit the record in to AuditHeader and
	 * AdtBMTProfessions by using auditHeaderDAO.addAudit(auditHeader) for Work
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
		Profession profession = (Profession) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getProfessionDAO().delete(profession, TableType.TEMP_TAB);
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
	private AuditHeader businessValidation(AuditHeader auditHeader,String method) {
		logger.debug("Entering");
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(),auditHeader.getUsrLanguage());
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
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug("Entering");

		// Get the model object.
		Profession profession = (Profession) auditDetail.getModelData();
		String code = profession.getProfessionCode();

		// Check the unique keys.
		if (profession.isNew()
				&& PennantConstants.RECORD_TYPE_NEW.equals(profession.getRecordType())
				&& professionDAO.isDuplicateKey(code, profession.isWorkflow() ? TableType.BOTH_TAB
						: TableType.MAIN_TAB)) {
			String[] parameters = new String[1];
			parameters[0] = PennantJavaUtil.getLabel("label_ProfessionCode") + ": " + code;

			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41014", parameters, null));
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug("Leaving");
		return auditDetail;
	}
}