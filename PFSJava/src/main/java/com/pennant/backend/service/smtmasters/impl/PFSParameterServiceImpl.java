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

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : PFSParameterServiceImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 12-07-2011 * *
 * Modified Date : 12-07-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 12-07-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.service.smtmasters.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.smtmasters.PFSParameterDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.smtmasters.PFSParameter;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.smtmasters.PFSParameterService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.GlobalVariable;

/**
 * Service implementation for methods that depends on <b>PFSParameter</b>.<br>
 * 
 */
public class PFSParameterServiceImpl extends GenericService<PFSParameter> implements PFSParameterService {
	private static Logger logger = LogManager.getLogger(PFSParameterServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private PFSParameterDAO pFSParameterDAO;

	public PFSParameterServiceImpl() {
		super(true, "SMTParameters");
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

	public PFSParameterDAO getPFSParameterDAO() {
		return pFSParameterDAO;
	}

	public void setPFSParameterDAO(PFSParameterDAO pFSParameterDAO) {
		this.pFSParameterDAO = pFSParameterDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table SMTparameters/SMTparameters_Temp
	 * by using PFSParameterDAO's save method b) Update the Record in the table. based on the module workFlow
	 * Configuration. by using PFSParameterDAO's update method 3) Audit the record in to AuditHeader and
	 * AdtSMTparameters by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */

	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug("Entering ");
		auditHeader = businessValidation(auditHeader, "saveOrUpdate");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		String tableType = "";
		PFSParameter pFSParameter = (PFSParameter) auditHeader.getAuditDetail().getModelData();
		if (pFSParameter.isWorkflow()) {
			tableType = "_Temp";
		}
		if (pFSParameter.isNewRecord()) {
			pFSParameter.setSysParmCode(getPFSParameterDAO().save(pFSParameter, tableType));
			auditHeader.getAuditDetail().setModelData(pFSParameter);
			auditHeader.setAuditReference(pFSParameter.getSysParmCode());

		} else {
			getPFSParameterDAO().update(pFSParameter, tableType);
		}

		if (!pFSParameter.isWorkflow()) {
			invalidateEntity(pFSParameter.getId());
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving ");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * SMTparameters by using PFSParameterDAO's delete method with type as Blank 3) Audit the record in to AuditHeader
	 * and AdtSMTparameters by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering ");

		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		PFSParameter pFSParameter = (PFSParameter) auditHeader.getAuditDetail().getModelData();
		getPFSParameterDAO().delete(pFSParameter, "");

		getAuditHeaderDAO().addAudit(auditHeader);

		invalidateEntity(pFSParameter.getId());

		logger.debug("Leaving ");
		return auditHeader;
	}

	/**
	 * getPFSParameterById fetch the details by using PFSParameterDAO's getPFSParameterById method.
	 * 
	 * @param id   (String)
	 * @param type (String) ""/_Temp/_View
	 * @return PFSParameter
	 */
	@Override
	public PFSParameter getPFSParameterById(String id) {
		return getPFSParameterDAO().getPFSParameterById(id, "_View");
	}

	/**
	 * getApprovedPFSParameterById fetch the details by using PFSParameterDAO's getPFSParameterById method . with
	 * parameter id and type as blank. it fetches the approved records from the SMTparameters.
	 * 
	 * @param id (String)
	 * @return PFSParameter
	 */
	public PFSParameter getApprovedPFSParameterById(String code) {
		return getCachedEntity(code);
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getPFSParameterDAO().delete with
	 * parameters pFSParameter,"" b) NEW Add new record in to main table by using getPFSParameterDAO().save with
	 * parameters pFSParameter,"" c) EDIT Update record in the main table by using getPFSParameterDAO().update with
	 * parameters pFSParameter,"" 3) Delete the record from the workFlow table by using getPFSParameterDAO().delete with
	 * parameters pFSParameter,"_Temp" 4) Audit the record in to AuditHeader and AdtSMTparameters by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and AdtSMTparameters by
	 * using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering ");
		PFSParameter pFSParameter = new PFSParameter();
		BeanUtils.copyProperties((PFSParameter) auditHeader.getAuditDetail().getModelData(), pFSParameter);

		String tranType = "";

		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		if (pFSParameter.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {

			tranType = PennantConstants.TRAN_DEL;
			getPFSParameterDAO().delete(pFSParameter, "");

		} else {

			pFSParameter.setRoleCode("");
			pFSParameter.setNextRoleCode("");
			pFSParameter.setTaskId("");
			pFSParameter.setNextTaskId("");
			pFSParameter.setWorkflowId(0);

			if (pFSParameter.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				pFSParameter.setRecordType("");
				getPFSParameterDAO().save(pFSParameter, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				pFSParameter.setRecordType("");
				getPFSParameterDAO().update(pFSParameter, "");
			}

		}

		invalidateEntity(pFSParameter.getId());
		getPFSParameterDAO().delete(pFSParameter, "_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving ");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getPFSParameterDAO().delete with parameters pFSParameter,"_Temp" 3) Audit the record in
	 * to AuditHeader and AdtSMTparameters by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug("Entering ");
		PFSParameter pFSParameter = (PFSParameter) auditHeader.getAuditDetail().getModelData();

		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getPFSParameterDAO().delete(pFSParameter, "_Temp");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving ");
		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from the auditHeader. 2) fetch the details
	 * from the tables 3) Validate the Record based on the record details. 4) Validate for any business validation.
	 * 
	 * @param AuditHeader (auditHeader)
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
	 * For Validating AuditDetals object getting from Audit Header, if any mismatch conditions Fetch the error details
	 * from getAcademicDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings then assign
	 * the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug("Entering");

		PFSParameter pFSParameter = (PFSParameter) auditDetail.getModelData();
		PFSParameter tempPFSParameter = null;
		if (pFSParameter.isWorkflow()) {
			tempPFSParameter = getPFSParameterDAO().getPFSParameterById(pFSParameter.getId(), "_Temp");
		}
		PFSParameter befPFSParameter = getPFSParameterDAO().getPFSParameterById(pFSParameter.getId(), "");

		PFSParameter oldPFSParameter = pFSParameter.getBefImage();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = pFSParameter.getSysParmCode();
		errParm[0] = PennantJavaUtil.getLabel("label_PFSParameterDialog_SysParmCode.value") + ":" + valueParm[0];

		if (pFSParameter.isNewRecord()) { // for New record or new record into work
											// flow

			if (!pFSParameter.isWorkflow()) {// With out Work flow only new
												// records
				if (befPFSParameter != null) { // Record Already Exists in the
												// table then error
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, null));
				}
			} else { // with work flow
				if (tempPFSParameter != null) { // if records already exists in
												// the Work flow table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, null));
				}

				if (pFSParameter.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type
																								// is new
					if (befPFSParameter != null) { // if records already exists
													// in the main table
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, null));
					}
				} else { // if records not exists in the Main flow table
					if (befPFSParameter == null) {
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!pFSParameter.isWorkflow()) { // With out Work flow for update
												// and delete

				if (befPFSParameter == null) { // if records not exists in the
												// main table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, null));
				}

				if (befPFSParameter != null && oldPFSParameter != null
						&& !oldPFSParameter.getLastMntOn().equals(befPFSParameter.getLastMntOn())) {
					if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
							.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm, null));
					} else {
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm, null));
					}
				}

			} else {

				if (tempPFSParameter == null) { // if records not exists in the
												// Work flow table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
				}

				if (tempPFSParameter != null && oldPFSParameter != null
						&& !oldPFSParameter.getLastMntOn().equals(tempPFSParameter.getLastMntOn())) {
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
				}

			}
		}

		if (pFSParameter.getSysParmCode().equals(SMTParameterConstants.DPD_STRING_LENGTH)) {
			int newDPDLength = Integer.valueOf(pFSParameter.getSysParmValue());
			int oldDPDLength = Integer.valueOf(befPFSParameter.getSysParmValue());
			if (newDPDLength < oldDPDLength) {
				valueParm[0] = pFSParameter.getSysParmValue();
				valueParm[1] = befPFSParameter.getSysParmValue();
				errParm[0] = PennantJavaUtil.getLabel("label_PFSParameterDialog_SysParmValue.value") + ":"
						+ valueParm[0];
				errParm[1] = ":" + valueParm[1];
				auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "30507", errParm, null));
			}

			if (newDPDLength > 2400) {
				valueParm[0] = pFSParameter.getSysParmValue();
				valueParm[1] = String.valueOf(2400);
				errParm[0] = PennantJavaUtil.getLabel("label_PFSParameterDialog_SysParmValue.value") + ":"
						+ valueParm[0];
				errParm[1] = ":" + valueParm[1];
				auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "30558", errParm, null));
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !pFSParameter.isWorkflow()) {
			pFSParameter.setBefImage(befPFSParameter);
		}
		logger.debug("Leaving ");
		return auditDetail;
	}

	@Override
	public void update(String code, String value, String type) {
		try {
			invalidateEntity(code);
		} catch (Exception e) {
			//
		}
		pFSParameterDAO.update(code, value, type);
	}

	@Override
	public List<GlobalVariable> getGlobaVariables() {
		return pFSParameterDAO.getGlobaVariables();
	}

	@Override
	protected PFSParameter getEntity(String code) {
		return pFSParameterDAO.getPFSParameterById(code, "_AView");
	}

	@Override
	public PFSParameter getParameter(String code) {
		return getCachedEntity(code);
	}

}