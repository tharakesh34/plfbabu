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
 * * FileName : OverdueChargeRecoveryServiceImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 11-05-2012
 * * * Modified Date : 11-05-2012 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 11-05-2012 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.service.financemanagement.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.financemanagement.OverdueChargeRecoveryDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.financemanagement.OverdueChargeRecovery;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.financemanagement.OverdueChargeRecoveryService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * Service implementation for methods that depends on <b>OverdueChargeRecovery</b>.<br>
 * 
 */
public class OverdueChargeRecoveryServiceImpl extends GenericService<OverdueChargeRecovery>
		implements OverdueChargeRecoveryService {
	private static final Logger logger = LogManager.getLogger(OverdueChargeRecoveryServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private OverdueChargeRecoveryDAO overdueChargeRecoveryDAO;

	public OverdueChargeRecoveryServiceImpl() {
		super();
	}

	@Override
	public OverdueChargeRecovery getNewOverdueChargeRecovery() {
		return overdueChargeRecoveryDAO.getNewOverdueChargeRecovery();
	}

	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);
		/*
		 * auditHeader = businessValidation(auditHeader,"saveOrUpdate"); if (!auditHeader.isNextProcess()) {
		 * logger.debug(Literal.LEAVING); return auditHeader; }
		 */
		String tableType = "";
		OverdueChargeRecovery overdueChargeRecovery = (OverdueChargeRecovery) auditHeader.getAuditDetail()
				.getModelData();

		if (overdueChargeRecovery.isWorkflow()) {
			tableType = "_Temp";
		}

		if (overdueChargeRecovery.isNewRecord()) {
			overdueChargeRecoveryDAO.save(overdueChargeRecovery, tableType);
		} else {
			overdueChargeRecoveryDAO.update(overdueChargeRecovery, tableType);
		}

		// auditHeaderDAO.addAudit(auditHeader);
		logger.debug(Literal.LEAVING);
		return auditHeader;

	}

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);
		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		OverdueChargeRecovery overdueChargeRecovery = (OverdueChargeRecovery) auditHeader.getAuditDetail()
				.getModelData();
		overdueChargeRecoveryDAO.delete(overdueChargeRecovery, "");

		auditHeaderDAO.addAudit(auditHeader);
		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public OverdueChargeRecovery getOverdueChargeRecoveryById(long finID, Date finSchDate, String finOdFor) {
		OverdueChargeRecovery ocr = getNewOverdueChargeRecovery();
		ocr = overdueChargeRecoveryDAO.getOverdueChargeRecoveryById(finID, finSchDate, finOdFor, "_View");
		ocr = overdueChargeRecoveryDAO.getODCRecoveryDetails(ocr);
		return ocr;
	}

	public OverdueChargeRecovery getApprovedOverdueChargeRecoveryById(long finID, Date finSchDate, String finOdFor) {
		return overdueChargeRecoveryDAO.getOverdueChargeRecoveryById(finID, finSchDate, finOdFor, "_AView");
	}

	@Override
	public BigDecimal getPendingODCAmount(long finID) {
		return overdueChargeRecoveryDAO.getPendingODCAmount(finID);
	}

	@Override
	public OverdueChargeRecovery getOverdueChargeRecovery(long finID) {
		OverdueChargeRecovery overdueCherage = new OverdueChargeRecovery();
		overdueCherage.setFinID(finID);

		return overdueChargeRecoveryDAO.getODCRecoveryDetails(overdueCherage);
	}

	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);
		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		OverdueChargeRecovery overdueChargeRecovery = new OverdueChargeRecovery();
		BeanUtils.copyProperties((OverdueChargeRecovery) auditHeader.getAuditDetail().getModelData(),
				overdueChargeRecovery);

		if (overdueChargeRecovery.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			overdueChargeRecoveryDAO.delete(overdueChargeRecovery, "");

		} else {
			overdueChargeRecovery.setRoleCode("");
			overdueChargeRecovery.setNextRoleCode("");
			overdueChargeRecovery.setTaskId("");
			overdueChargeRecovery.setNextTaskId("");
			overdueChargeRecovery.setWorkflowId(0);

			if (overdueChargeRecovery.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				overdueChargeRecovery.setRecordType("");
				overdueChargeRecoveryDAO.save(overdueChargeRecovery, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				overdueChargeRecovery.setRecordType("");
				overdueChargeRecoveryDAO.update(overdueChargeRecovery, "");
			}
		}

		overdueChargeRecoveryDAO.delete(overdueChargeRecovery, "_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeaderDAO.addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(overdueChargeRecovery);

		auditHeaderDAO.addAudit(auditHeader);
		logger.debug(Literal.LEAVING);

		return auditHeader;
	}

	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);
		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		OverdueChargeRecovery overdueChargeRecovery = (OverdueChargeRecovery) auditHeader.getAuditDetail()
				.getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		overdueChargeRecoveryDAO.delete(overdueChargeRecovery, "_Temp");

		auditHeaderDAO.addAudit(auditHeader);
		logger.debug(Literal.LEAVING);

		return auditHeader;
	}

	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);
		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug(Literal.ENTERING);

		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		OverdueChargeRecovery odcr = (OverdueChargeRecovery) auditDetail.getModelData();

		OverdueChargeRecovery tempOverdueChargeRecovery = null;
		if (odcr.isWorkflow()) {
			tempOverdueChargeRecovery = overdueChargeRecoveryDAO.getOverdueChargeRecoveryById(odcr.getFinID(),
					odcr.getFinODSchdDate(), odcr.getFinODFor(), "_Temp");
		}
		OverdueChargeRecovery befOverdueChargeRecovery = overdueChargeRecoveryDAO
				.getOverdueChargeRecoveryById(odcr.getFinID(), odcr.getFinODSchdDate(), odcr.getFinODFor(), "");
		OverdueChargeRecovery oldOverdueChargeRecovery = odcr.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = odcr.getId();
		errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];

		if (odcr.isNewRecord()) { // for New record or new record into work flow

			if (!odcr.isWorkflow()) {// With out Work flow only new records
				if (befOverdueChargeRecovery != null) { // Record Already Exists in the table then error
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow
				if (odcr.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type
																						// is new
					if (befOverdueChargeRecovery != null || tempOverdueChargeRecovery != null) { // if records already
																									// exists in the
																									// main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
					}
				} else { // if records not exists in the Main flow table
					if (befOverdueChargeRecovery == null || tempOverdueChargeRecovery != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!odcr.isWorkflow()) { // With out Work flow for update and delete

				if (befOverdueChargeRecovery == null) { // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, valueParm), usrLanguage));
				} else {
					if (oldOverdueChargeRecovery != null && !oldOverdueChargeRecovery.getLastMntOn()
							.equals(befOverdueChargeRecovery.getLastMntOn())) {
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

				if (tempOverdueChargeRecovery == null) { // if records not exists in the Work flow table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}

				if (tempOverdueChargeRecovery != null && oldOverdueChargeRecovery != null
						&& !oldOverdueChargeRecovery.getLastMntOn().equals(tempOverdueChargeRecovery.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !odcr.isWorkflow()) {
			odcr.setBefImage(befOverdueChargeRecovery);
		}

		return auditDetail;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public void setOverdueChargeRecoveryDAO(OverdueChargeRecoveryDAO overdueChargeRecoveryDAO) {
		this.overdueChargeRecoveryDAO = overdueChargeRecoveryDAO;
	}
}