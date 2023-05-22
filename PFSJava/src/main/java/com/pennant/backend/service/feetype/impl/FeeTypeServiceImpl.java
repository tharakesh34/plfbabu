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
 * * FileName : FeeTypeServiceImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 03-01-2017 * * Modified
 * Date : 03-01-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 03-01-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.service.feetype.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.feetype.FeeTypeDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FeeType;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.feetype.FeeTypeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.pff.fee.AdviseType;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.receipt.constants.Allocation;

/**
 * Service implementation for methods that depends on <b>FeeType</b>.<br>
 * 
 */
public class FeeTypeServiceImpl extends GenericService<FeeType> implements FeeTypeService {
	private static final Logger logger = LogManager.getLogger(FeeTypeServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private FeeTypeDAO feeTypeDAO;

	public FeeTypeServiceImpl() {
		super();
	}

	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader);

		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		FeeType feeType = (FeeType) auditHeader.getAuditDetail().getModelData();
		TableType tableType = TableType.MAIN_TAB;

		if (feeType.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (feeType.isNewRecord()) {
			feeType.setFeeTypeID(Long.parseLong(feeTypeDAO.save(feeType, tableType)));
			auditHeader.getAuditDetail().setModelData(feeType);
			auditHeader.setAuditReference(String.valueOf(feeType.getFeeTypeID()));
		} else {
			feeTypeDAO.update(feeType, tableType);
		}

		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;

	}

	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		String tranType = "";
		auditHeader = businessValidation(auditHeader);

		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		FeeType feeType = new FeeType();
		BeanUtils.copyProperties((FeeType) auditHeader.getAuditDetail().getModelData(), feeType);

		feeTypeDAO.delete(feeType, TableType.TEMP_TAB);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(feeType.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(feeTypeDAO.getFeeTypeById(feeType.getFeeTypeID(), ""));
		}

		if (feeType.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			feeTypeDAO.delete(feeType, TableType.MAIN_TAB);

		} else {
			feeType.setRoleCode("");
			feeType.setNextRoleCode("");
			feeType.setTaskId("");
			feeType.setNextTaskId("");
			feeType.setWorkflowId(0);

			if (feeType.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				feeType.setRecordType("");
				feeTypeDAO.save(feeType, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				feeType.setRecordType("");
				feeTypeDAO.update(feeType, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeaderDAO.addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(feeType);

		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader);

		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		FeeType feeType = (FeeType) auditHeader.getAuditDetail().getModelData();
		feeTypeDAO.delete(feeType, TableType.MAIN_TAB);

		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader);
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		FeeType feeType = (FeeType) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		feeTypeDAO.delete(feeType, TableType.TEMP_TAB);

		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public FeeType getFeeTypeById(long id) {
		return feeTypeDAO.getFeeTypeById(id, "_View");
	}

	@Override
	public FeeType getApprovedFeeTypeById(long id) {
		return feeTypeDAO.getFeeTypeById(id, "_AView");
	}

	@Override
	public Long getFinFeeTypeIdByFeeType(String feeTypeCode) {
		return feeTypeDAO.getFinFeeTypeIdByFeeType(feeTypeCode, "_View");
	}

	@Override
	public String getTaxCompByCode(String feeTypeCode) {
		return feeTypeDAO.getTaxCompByCode(feeTypeCode);
	}

	@Override
	public FeeType getPayableFeeType(String feeTypeCode) {
		return feeTypeDAO.getPayableFeeType(feeTypeCode);
	}

	@Override
	public List<FeeType> getAMZReqFeeTypes() {
		return feeTypeDAO.getAMZReqFeeTypes();
	}

	@Override
	public List<FeeType> getFeeTypesForAccountingById(List<Long> feeTypeIds) {
		return feeTypeDAO.getFeeTypeListByIds(feeTypeIds);
	}

	@Override
	public List<FeeType> getFeeTypesForAccountingByCode(List<String> feeTypeCodes) {
		List<Long> feeTypeIDs = feeTypeDAO.getFeeTypeIDs(feeTypeCodes);

		if (CollectionUtils.isNotEmpty(feeTypeIDs)) {
			return getFeeTypesForAccountingById(feeTypeIDs);
		}

		return new ArrayList<>();
	}

	@Override
	public FeeType getFeeTypeByRecvFeeTypeId(long id) {
		return feeTypeDAO.getFeeTypeByRecvFeeTypeId(id);
	}

	private AuditHeader businessValidation(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug(Literal.ENTERING);

		FeeType feeType = (FeeType) auditDetail.getModelData();

		String feeTypeCode = feeType.getFeeTypeCode();
		if (feeType.isNewRecord() && feeTypeDAO.isDuplicateKey(feeType.getFeeTypeID(), feeTypeCode,
				feeType.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[2];
			parameters[0] = PennantJavaUtil.getLabel("label_FeeTypeCode") + ": " + feeTypeCode;

			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}

		int adviseType = feeType.getAdviseType();
		String payableLinkTo = feeType.getPayableLinkTo();

		if (AdviseType.PAYABLE.id() == adviseType
				&& !(Allocation.MANADV.equals(payableLinkTo) || Allocation.ADHOC.equals(payableLinkTo))) {
			String otherFeeTypeCode = feeTypeDAO.getFeeTypeCode(feeTypeCode, payableLinkTo);
			if (otherFeeTypeCode != null) {
				String[] valueParm = new String[2];
				valueParm[0] = PennantJavaUtil.getLabel("label_FeeTypeDialog_PayableLinkTo.value").concat(": ")
						.concat(payableLinkTo);
				valueParm[1] = PennantJavaUtil.getLabel("label_FeeTypeCode").concat(": ").concat(otherFeeTypeCode);
				auditDetail.setErrorDetail(new ErrorDetail("41018", valueParm));
			}
		}

		if (Allocation.MANADV.equals(payableLinkTo)) {
			payableLinkTo = Allocation.MANADV;
			long recvFeeTypeId = feeType.getRecvFeeTypeId();

			String otherFeeTypeCode = feeTypeDAO.getOtrRecFeeTypeCode(feeTypeCode, payableLinkTo, recvFeeTypeId);
			long recvFeeTypeIds = feeTypeDAO.getRecvFeeTypeId(feeTypeCode, payableLinkTo, recvFeeTypeId);

			if (otherFeeTypeCode != null && recvFeeTypeIds > 0) {
				String[] valueParm = new String[2];
				valueParm[0] = PennantJavaUtil.getLabel("label_FeeTypeDialog_PayableLinkTo.value").concat(": ")
						.concat("Other Receivables").concat(" And Receivable Type ");
				valueParm[1] = PennantJavaUtil.getLabel("label_FeeTypeCode").concat(": ").concat(otherFeeTypeCode);
				auditDetail.setErrorDetail(new ErrorDetail("41018", valueParm));

			}

		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	@Override
	public FeeType getApprovedFeeTypeByFeeCode(String feeTyeCode) {
		return feeTypeDAO.getApprovedFeeTypeByFeeCode(feeTyeCode);
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public void setFeeTypeDAO(FeeTypeDAO feeTypeDAO) {
		this.feeTypeDAO = feeTypeDAO;
	}

}