/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */
package com.pennant.backend.service.systemmasters.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.systemmasters.AcademicDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.Academic;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.systemmasters.AcademicService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>Academic</b>.<br>
 * 
 */
public class AcademicServiceImpl extends GenericService<Academic> implements AcademicService {

	private static Logger logger = Logger.getLogger(AcademicServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private AcademicDAO academicDAO;

	public AcademicServiceImpl() {
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

	public AcademicDAO getAcademicDAO() {
		return academicDAO;
	}

	public void setAcademicDAO(AcademicDAO academicDAO) {
		this.academicDAO = academicDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table BMTAcademics/BMTAcademics_Temp
	 * by using AcademicDAO's save method b) Update the Record in the table. based on the module workFlow Configuration.
	 * by using AcademicDAO's update method 3) Audit the record in to AuditHeader and AdtBMTAcademics by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader);

		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		Academic academic = (Academic) auditHeader.getAuditDetail().getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (academic.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (academic.isNew()) {
			academic.setAcademicID(Long.parseLong(getAcademicDAO().save(academic, tableType)));
			auditHeader.getAuditDetail().setModelData(academic);
			auditHeader.setAuditReference(String.valueOf(academic.getAcademicID()));
		} else {
			getAcademicDAO().update(academic, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * BMTAcademics by using AcademicDAO's delete method with type as Blank 3) Audit the record in to AuditHeader and
	 * AdtBMTAcademics by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader);

		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		Academic academic = (Academic) auditHeader.getAuditDetail().getModelData();
		getAcademicDAO().delete(academic, TableType.MAIN_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getAcademicById fetch the details by using AcademicDAO's getAcademicById method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return Academic
	 */
	@Override
	public Academic getAcademicById(long academicID) {
		return getAcademicDAO().getAcademicById(academicID, "_View");
	}

	/**
	 * getApprovedAcademicById fetch the details by using AcademicDAO's getAcademicById method . with parameter id and
	 * type as blank. it fetches the approved records from the BMTAcademics.
	 * 
	 * @param id
	 *            (String)
	 * @return Academic
	 */
	public Academic getApprovedAcademicById(long academicID) {
		return getAcademicDAO().getAcademicById(academicID, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getAcademicDAO().delete with
	 * parameters academic,"" b) NEW Add new record in to main table by using getAcademicDAO().save with parameters
	 * academic,"" c) EDIT Update record in the main table by using getAcademicDAO().update with parameters academic,""
	 * 3) Delete the record from the workFlow table by using getAcademicDAO().delete with parameters academic,"_Temp" 4)
	 * Audit the record in to AuditHeader and AdtBMTAcademics by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow 5) Audit the record in to AuditHeader and AdtBMTAcademics by using auditHeaderDAO.addAudit(auditHeader)
	 * based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");

		String tranType = "";
		auditHeader = businessValidation(auditHeader);

		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		Academic academic = new Academic();
		BeanUtils.copyProperties((Academic) auditHeader.getAuditDetail().getModelData(), academic);

		getAcademicDAO().delete(academic, TableType.TEMP_TAB);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(academic.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(academicDAO.getAcademicById(academic.getAcademicID(), ""));
		}

		if (academic.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getAcademicDAO().delete(academic, TableType.MAIN_TAB);
		} else {
			academic.setRoleCode("");
			academic.setNextRoleCode("");
			academic.setTaskId("");
			academic.setNextTaskId("");
			academic.setWorkflowId(0);

			if (academic.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				academic.setRecordType("");
				getAcademicDAO().save(academic, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				academic.setRecordType("");
				getAcademicDAO().update(academic, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(academic);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getAcademicDAO().delete with parameters academic,"_Temp" 3) Audit the record in to
	 * AuditHeader and AdtBMTAcademics by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader);

		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		Academic academic = (Academic) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAcademicDAO().delete(academic, TableType.TEMP_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from the auditHeader. 2) fetch the details
	 * from the tables 3) Validate the Record based on the record details. 4) Validate for any business validation.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader) {
		logger.debug("Entering");
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any mismatch conditions Fetch the error details
	 * from getAcademicDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings then assign
	 * the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug("Entering");

		// Get the model object.
		Academic academic = (Academic) auditDetail.getModelData();

		// Check the unique keys.
		if (academic.isNew() && academicDAO.isDuplicateKey(academic.getAcademicID(), academic.getAcademicLevel(),
				academic.getAcademicDecipline(), academic.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[2];
			parameters[0] = PennantJavaUtil.getLabel("label_AcademicLevel") + ": " + academic.getAcademicLevel();
			parameters[1] = PennantJavaUtil.getLabel("label_AcademicDecipline") + ": "
					+ academic.getAcademicDecipline();

			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug("Leaving");
		return auditDetail;
	}
}