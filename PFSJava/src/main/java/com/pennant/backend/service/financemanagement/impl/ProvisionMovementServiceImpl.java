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
 * * FileName : ProvisionMovementServiceImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 31-05-2012 * *
 * Modified Date : 31-05-2012 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 31-05-2012 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.service.financemanagement.impl;

import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.financemanagement.ProvisionMovementDAO;
import com.pennant.backend.dao.rulefactory.PostingsDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.financemanagement.ProvisionMovement;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.financemanagement.ProvisionMovementService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * Service implementation for methods that depends on <b>ProvisionMovement</b>.<br>
 * 
 */
public class ProvisionMovementServiceImpl extends GenericService<ProvisionMovement>
		implements ProvisionMovementService {
	private static final Logger logger = LogManager.getLogger(ProvisionMovementServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private ProvisionMovementDAO provisionMovementDAO;
	private PostingsDAO postingsDAO;

	public ProvisionMovementServiceImpl() {
		super();
	}

	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);
		auditHeader = businessValidation(auditHeader, "saveOrUpdate");
		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}
		String tableType = "";
		ProvisionMovement provisionMovement = (ProvisionMovement) auditHeader.getAuditDetail().getModelData();

		if (provisionMovement.isWorkflow()) {
			tableType = "_Temp";
		}

		if (provisionMovement.isNewRecord()) {
			provisionMovementDAO.save(provisionMovement, tableType);
		} else {
			provisionMovementDAO.update(provisionMovement, tableType);
		}

		auditHeaderDAO.addAudit(auditHeader);
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

		ProvisionMovement provisionMovement = (ProvisionMovement) auditHeader.getAuditDetail().getModelData();
		provisionMovementDAO.delete(provisionMovement, "");

		auditHeaderDAO.addAudit(auditHeader);
		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public ProvisionMovement getProvisionMovementById(long finID, Date movementDate, long linkTransId) {
		ProvisionMovement movement = provisionMovementDAO.getProvisionMovementById(finID, movementDate, "_AView");
		if (linkTransId != Long.MIN_VALUE) {
			movement.setPostingsList(postingsDAO.getPostingsByLinkTransId(linkTransId));
		}
		return movement;
	}

	public ProvisionMovement getApprovedProvisionMovementById(long finID, Date movementDate) {
		return provisionMovementDAO.getProvisionMovementById(finID, movementDate, "_AView");
	}

	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);
		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		ProvisionMovement provisionMovement = new ProvisionMovement();
		BeanUtils.copyProperties((ProvisionMovement) auditHeader.getAuditDetail().getModelData(), provisionMovement);

		if (provisionMovement.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			provisionMovementDAO.delete(provisionMovement, "");

		} else {
			provisionMovement.setRoleCode("");
			provisionMovement.setNextRoleCode("");
			provisionMovement.setTaskId("");
			provisionMovement.setNextTaskId("");
			provisionMovement.setWorkflowId(0);

			if (provisionMovement.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				provisionMovement.setRecordType("");
				provisionMovementDAO.save(provisionMovement, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				provisionMovement.setRecordType("");
				provisionMovementDAO.update(provisionMovement, "");
			}
		}

		provisionMovementDAO.delete(provisionMovement, "_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeaderDAO.addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(provisionMovement);

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

		ProvisionMovement provisionMovement = (ProvisionMovement) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		provisionMovementDAO.delete(provisionMovement, "_Temp");

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
		ProvisionMovement pm = (ProvisionMovement) auditDetail.getModelData();

		ProvisionMovement tempProvisionMovement = null;
		if (pm.isWorkflow()) {
			tempProvisionMovement = provisionMovementDAO.getProvisionMovementById(pm.getFinID(),
					pm.getProvMovementDate(), "_Temp");
		}
		ProvisionMovement befProvisionMovement = provisionMovementDAO.getProvisionMovementById(pm.getFinID(),
				pm.getProvMovementDate(), "");
		ProvisionMovement oldProvisionMovement = pm.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = pm.getId();
		errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];

		if (pm.isNewRecord()) { // for New record or new record into work flow

			if (!pm.isWorkflow()) {// With out Work flow only new records
				if (befProvisionMovement != null) { // Record Already Exists in the table then error
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow
				if (pm.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is
																					// new
					if (befProvisionMovement != null || tempProvisionMovement != null) { // if records already exists in
																							// the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
					}
				} else { // if records not exists in the Main flow table
					if (befProvisionMovement == null || tempProvisionMovement != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!pm.isWorkflow()) { // With out Work flow for update and delete

				if (befProvisionMovement == null) { // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, valueParm), usrLanguage));
				} else {
					if (oldProvisionMovement != null
							&& !oldProvisionMovement.getLastMntOn().equals(befProvisionMovement.getLastMntOn())) {
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

				if (tempProvisionMovement == null) { // if records not exists in the Work flow table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}

				if (tempProvisionMovement != null && oldProvisionMovement != null
						&& !oldProvisionMovement.getLastMntOn().equals(tempProvisionMovement.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !pm.isWorkflow()) {
			pm.setBefImage(befProvisionMovement);
		}

		return auditDetail;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public void setProvisionMovementDAO(ProvisionMovementDAO provisionMovementDAO) {
		this.provisionMovementDAO = provisionMovementDAO;
	}

	public void setPostingsDAO(PostingsDAO postingsDAO) {
		this.postingsDAO = postingsDAO;
	}

}