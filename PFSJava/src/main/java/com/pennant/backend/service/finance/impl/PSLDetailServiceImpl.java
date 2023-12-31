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
 * * FileName : PSLDetailServiceImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 20-06-2018 * * Modified
 * Date : 20-06-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 20-06-2018 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.service.finance.impl;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.psl.PSLDetailDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.psl.PSLDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.PSLDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>PSLDetail</b>.<br>
 */
public class PSLDetailServiceImpl extends GenericService<PSLDetail> implements PSLDetailService {
	private static final Logger logger = LogManager.getLogger(PSLDetailServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private PSLDetailDAO pSLDetailDAO;

	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		PSLDetail pSLDetail = (PSLDetail) auditHeader.getAuditDetail().getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (pSLDetail.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (pSLDetail.isNewRecord()) {
			pSLDetailDAO.save(pSLDetail, tableType);
		} else {
			pSLDetailDAO.update(pSLDetail, tableType);
		}

		auditHeaderDAO.addAudit(auditHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	@Override
	public AuditDetail saveOrUpdate(PSLDetail pslDetail, TableType tableType, String auditTranType) {
		logger.debug(Literal.ENTERING);

		String[] fields = PennantJavaUtil.getFieldDetails(pslDetail, pslDetail.getExcludeFields());

		pslDetail.setWorkflowId(0);
		if (pslDetail.isNewRecord()) {
			pSLDetailDAO.save(pslDetail, tableType);
		} else {
			pSLDetailDAO.update(pslDetail, tableType);
		}

		logger.debug(Literal.LEAVING);
		return new AuditDetail(auditTranType, 1, fields[0], fields[1], pslDetail.getBefImage(), pslDetail);

	}

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		PSLDetail pSLDetail = (PSLDetail) auditHeader.getAuditDetail().getModelData();
		pSLDetailDAO.delete(pSLDetail, TableType.MAIN_TAB);

		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public AuditDetail doApprove(PSLDetail pslDetail, TableType tableType, String auditTranType) {
		logger.debug(Literal.ENTERING);

		String[] fields = PennantJavaUtil.getFieldDetails(pslDetail, pslDetail.getExcludeFields());

		pslDetail.setRoleCode("");
		pslDetail.setNextRoleCode("");
		pslDetail.setTaskId("");
		pslDetail.setNextTaskId("");
		pslDetail.setWorkflowId(0);

		pSLDetailDAO.save(pslDetail, tableType);

		logger.debug(Literal.LEAVING);
		return new AuditDetail(auditTranType, 1, fields[0], fields[1], pslDetail.getBefImage(), pslDetail);
	}

	@Override
	public AuditDetail delete(PSLDetail pslDetail, TableType tableType, String auditTranType) {
		logger.debug(Literal.ENTERING);

		String[] fields = PennantJavaUtil.getFieldDetails(pslDetail, pslDetail.getExcludeFields());

		pSLDetailDAO.delete(pslDetail, tableType);

		logger.debug(Literal.LEAVING);
		return new AuditDetail(auditTranType, 1, fields[0], fields[1], null, pslDetail);
	}

	@Override
	public PSLDetail getPSLDetail(long finID) {
		return pSLDetailDAO.getPSLDetail(finID, "_View");
	}

	public PSLDetail getApprovedPSLDetail(long finID) {
		return pSLDetailDAO.getPSLDetail(finID, "_AView");
	}

	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		PSLDetail pSLDetail = new PSLDetail();
		BeanUtils.copyProperties((PSLDetail) auditHeader.getAuditDetail().getModelData(), pSLDetail);

		pSLDetailDAO.delete(pSLDetail, TableType.TEMP_TAB);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(pSLDetail.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(pSLDetailDAO.getPSLDetail(pSLDetail.getFinID(), ""));
		}

		if (pSLDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			pSLDetailDAO.delete(pSLDetail, TableType.MAIN_TAB);
		} else {
			pSLDetail.setRoleCode("");
			pSLDetail.setNextRoleCode("");
			pSLDetail.setTaskId("");
			pSLDetail.setNextTaskId("");
			pSLDetail.setWorkflowId(0);

			if (pSLDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				pSLDetail.setRecordType("");
				pSLDetailDAO.save(pSLDetail, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				pSLDetail.setRecordType("");
				pSLDetailDAO.update(pSLDetail, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeaderDAO.addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(pSLDetail);
		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		PSLDetail pSLDetail = (PSLDetail) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		pSLDetailDAO.delete(pSLDetail, TableType.TEMP_TAB);

		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		auditHeader = doValidation(auditHeader, method);
		auditHeader = nextProcess(auditHeader);
		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	private AuditHeader doValidation(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		AuditDetail auditDetail = validate(auditHeader.getAuditDetail(), method, auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public AuditDetail validate(PSLDetail pslDetail, String method, String auditTranType, String usrLanguage) {
		return doValidation(pslDetail, auditTranType, method, usrLanguage);
	}

	public AuditDetail doValidation(PSLDetail pslDetail, String auditTranType, String method, String usrLanguage) {
		logger.debug(Literal.ENTERING);
		String[] fields = PennantJavaUtil.getFieldDetails(pslDetail, pslDetail.getExcludeFields());

		AuditDetail auditDetail = new AuditDetail(auditTranType, 1, fields[0], fields[1], pslDetail.getBefImage(),
				pslDetail);

		logger.debug(Literal.LEAVING);
		return validate(auditDetail, usrLanguage, method);
	}

	private AuditDetail validate(AuditDetail auditDetail, String method, String usrLanguage) {
		logger.debug(Literal.ENTERING);
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		PSLDetail pslDetail = (PSLDetail) auditDetail.getModelData();

		PSLDetail tempPslDetails = null;
		if (pslDetail.isWorkflow()) {
			tempPslDetails = pSLDetailDAO.getPSLDetail(pslDetail.getFinID(), "_Temp");

		}
		PSLDetail befPSLDetail = pSLDetailDAO.getPSLDetail(pslDetail.getFinID(), "");
		PSLDetail oldpslDetail = pslDetail.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = String.valueOf(pslDetail.getId());
		errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];

		if (pslDetail.isNewRecord()) { // for New record or new record into work flow

			if (!pslDetail.isWorkflow()) {// With out Work flow only new
											// records
				if (befPSLDetail != null) { // Record Already Exists in the
											// table then error
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow
				if (pslDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if
																							// records
																							// type
																							// is
																							// new
					if (befPSLDetail != null || tempPslDetails != null) {
						// if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
					}
				} else { // if records not exists in the Main flow table
					if (befPSLDetail == null || tempPslDetails != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!pslDetail.isWorkflow()) { // With out Work flow for update
											// and delete

				if (befPSLDetail == null) { // if records not exists in the
											// main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, valueParm), usrLanguage));
				} else {
					if (oldpslDetail != null && !oldpslDetail.getLastMntOn().equals(befPSLDetail.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm, valueParm),
									usrLanguage));
						} else {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm, valueParm),
									usrLanguage));
						}
					}
				}
			} else {

				if (tempPslDetails == null) { // if records not exists in
												// the Work flow table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}

				if (tempPslDetails != null && oldpslDetail != null
						&& !oldpslDetail.getLastMntOn().equals(tempPslDetails.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !pslDetail.isWorkflow()) {
			pslDetail.setBefImage(befPSLDetail);
		}
		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public void setpSLDetailDAO(PSLDetailDAO pSLDetailDAO) {
		this.pSLDetailDAO = pSLDetailDAO;
	}

}