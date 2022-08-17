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
 * * FileName : CustomerPhoneNumberServiceImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 26-05-2011 *
 * * Modified Date : 26-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.service.customermasters.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.customermasters.GSTDetailDAO;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.GSTDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.customermasters.GSTDetailService;
import com.pennant.backend.service.customermasters.validation.GSTDetailValidation;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * Service implementation for methods that depends on <b>CustomerPhoneNumber</b>.<br>
 * 
 */
public class GSTDetailServiceImpl extends GenericService<GSTDetail> implements GSTDetailService {
	private static Logger logger = LogManager.getLogger(GSTDetailServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private GSTDetailDAO gstDetailDAO;
	private GSTDetailValidation gstDetailValidation;

	public GSTDetailServiceImpl() {
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
		GSTDetail gstDetail = (GSTDetail) auditHeader.getAuditDetail().getModelData();

		if (gstDetail.isWorkflow()) {
			tableType = "_Temp";
		}

		if (gstDetail.isNewRecord()) {
			gstDetail.setId(gstDetailDAO.save(gstDetail, tableType));
			auditHeader.getAuditDetail().setModelData(gstDetail);
		} else {
			gstDetailDAO.update(gstDetail, tableType);
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

		GSTDetail gstDetail = (GSTDetail) auditHeader.getAuditDetail().getModelData();

		gstDetailDAO.delete(gstDetail, "");

		auditHeaderDAO.addAudit(auditHeader);
		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public GSTDetail getGSTDetailById(long id, String typeCode) {
		return gstDetailDAO.getGSTDetailByID(id, typeCode, "_View");
	}

	@Override
	public GSTDetail getApprovedGSTDetailById(long id, String typeCode) {
		return gstDetailDAO.getGSTDetailByID(id, typeCode, "_AView");
	}

	@Override
	public GSTDetail getDefaultGSTDetailById(long id) {
		return gstDetailDAO.getDefaultGSTDetailById(id, "_AView");
	}

	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		GSTDetail gstDetail = new GSTDetail();
		BeanUtils.copyProperties((GSTDetail) auditHeader.getAuditDetail().getModelData(), gstDetail);

		if (gstDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			gstDetailDAO.delete(gstDetail, "");
		} else {
			gstDetail.setRoleCode("");
			gstDetail.setNextRoleCode("");
			gstDetail.setTaskId("");
			gstDetail.setNextTaskId("");
			gstDetail.setWorkflowId(0);

			if (gstDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				gstDetail.setRecordType("");
				gstDetailDAO.save(gstDetail, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				gstDetail.setRecordType("");
				gstDetailDAO.update(gstDetail, "");
			}
		}
		if (!StringUtils.equals(gstDetail.getSourceId(), PennantConstants.FINSOURCE_ID_API)) {
			gstDetailDAO.delete(gstDetail, "_Temp");
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			auditHeaderDAO.addAudit(auditHeader);
		}

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(gstDetail);

		auditHeaderDAO.addAudit(auditHeader);
		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		GSTDetail gstdetail = (GSTDetail) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		gstDetailDAO.delete(gstdetail, "_Temp");

		auditHeaderDAO.addAudit(auditHeader);
		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		auditHeader = getGSTDetailValidation().gstDetailValidation(auditHeader, method);
		auditHeader = nextProcess(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public void setGstDetailDAO(GSTDetailDAO gstDetailDAO) {
		this.gstDetailDAO = gstDetailDAO;
	}

	public void setGSTDetailValidation(GSTDetailValidation gstDetailValidation) {
		this.gstDetailValidation = gstDetailValidation;
	}

	public GSTDetailValidation getGSTDetailValidation() {
		if (gstDetailValidation == null) {
			this.gstDetailValidation = new GSTDetailValidation(gstDetailDAO);
		}

		return this.gstDetailValidation;
	}
}