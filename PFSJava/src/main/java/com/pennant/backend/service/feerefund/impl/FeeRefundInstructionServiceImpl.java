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
 * * FileName : PaymentInstructionServiceImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 27-05-2017 * *
 * Modified Date : 27-05-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 27-05-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.service.feerefund.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.backend.dao.feerefund.FeeRefundInstructionDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.feerefund.FeeRefundInstruction;
import com.pennant.backend.model.finance.PaymentInstruction;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.feerefund.FeeRefundInstructionService;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>PaymentInstruction</b>.<br>
 */
public class FeeRefundInstructionServiceImpl extends GenericService<FeeRefundInstruction>
		implements FeeRefundInstructionService {
	private static final Logger logger = LogManager.getLogger(FeeRefundInstructionServiceImpl.class);

	private FeeRefundInstructionDAO feeRefundInstructionDAO;

	@Override
	public List<AuditDetail> setFeeRefundInstDetailAuditData(FeeRefundInstruction fri, String auditTranType,
			String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new FeeRefundInstruction(),
				new PaymentInstruction().getExcludeFields());

		if (StringUtils.isEmpty(fri.getRecordType())) {
			return auditDetails;
		}

		boolean isRcdType = false;
		if (fri.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
			fri.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			isRcdType = true;
		} else if (fri.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
			fri.setRecordType(PennantConstants.RECORD_TYPE_UPD);
			isRcdType = true;
		} else if (fri.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
			fri.setRecordType(PennantConstants.RECORD_TYPE_DEL);
		}
		if ("saveOrUpdate".equals(method) && isRcdType) {
			fri.setNewRecord(true);
		}
		if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
			if (fri.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				auditTranType = PennantConstants.TRAN_ADD;
			} else if (fri.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
					|| fri.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				auditTranType = PennantConstants.TRAN_DEL;
			} else {
				auditTranType = PennantConstants.TRAN_UPD;
			}
		}
		logger.debug("Leaving");
		auditDetails.add(new AuditDetail(auditTranType, 1, fields[0], fields[1], fri.getBefImage(), fri));
		return auditDetails;
	}

	@Override
	public List<AuditDetail> processFeeRefundInstrDetails(List<AuditDetail> auditDetails, TableType type,
			String methodName) {
		logger.debug("Entering");

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;
		for (int i = 0; i < auditDetails.size(); i++) {
			FeeRefundInstruction fri = (FeeRefundInstruction) auditDetails.get(i).getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			TableType tableType = TableType.TEMP_TAB;
			if (TableType.MAIN_TAB.equals(type)) {
				tableType = TableType.MAIN_TAB;
				approveRec = true;
				fri.setRoleCode("");
				fri.setNextRoleCode("");
				fri.setTaskId("");
				fri.setNextTaskId("");
				fri.setWorkflowId(0);
			}
			if (fri.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (fri.isNewRecord()) {
				saveRecord = true;
				if (fri.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					fri.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (fri.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					fri.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (fri.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					fri.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}
			} else if (fri.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (fri.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (fri.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (fri.isNewRecord()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}

			if (approveRec) {
				rcdType = fri.getRecordType();
				recordStatus = fri.getRecordStatus();
				fri.setRecordType("");
				fri.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				fri.setStatus(DisbursementConstants.STATUS_APPROVED);
			}
			if (saveRecord) {
				if (tableType.equals(TableType.MAIN_TAB)) {
					fri.setPaymentProcReq(true);
				}
				feeRefundInstructionDAO.save(fri, tableType);
			}
			if (updateRecord) {
				feeRefundInstructionDAO.update(fri, tableType);
			}
			if (deleteRecord) {
				feeRefundInstructionDAO.delete(fri, tableType);
			}
			if (approveRec) {
				fri.setRecordType(rcdType);
				fri.setRecordStatus(recordStatus);
			}

			if ("doApprove".equals(methodName)) {
				if (!PennantConstants.RECORD_TYPE_NEW.equals(fri.getRecordType())) {
					fri.setBefImage(feeRefundInstructionDAO.getFeeRefundInstructionDetails(fri.getHeaderID(), ""));
				}
			}
			auditDetails.get(i).setModelData(fri);

		}
		logger.debug("Leaving");
		return auditDetails;
	}

	public AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug(Literal.ENTERING);

		// Write the required validation over hear.

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	@Override
	public List<AuditDetail> delete(FeeRefundInstruction feeRefundInstruction, TableType tableType,
			String auditTranType, long feeRefundId) {
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		if (feeRefundInstruction != null) {
			String[] fields = PennantJavaUtil.getFieldDetails(new FeeRefundInstruction(),
					new FeeRefundInstruction().getExcludeFields());
			if (StringUtils.isNotEmpty(feeRefundInstruction.getRecordType())
					|| StringUtils.isEmpty(tableType.toString())) {
				auditDetails.add(new AuditDetail(auditTranType, 1, fields[0], fields[1],
						feeRefundInstruction.getBefImage(), feeRefundInstruction));
			}
			feeRefundInstructionDAO.delete(feeRefundInstruction, tableType);
		}
		return auditDetails;
	}

	@Override
	public boolean isInstructionInProgress(long finID) {
		return feeRefundInstructionDAO.isInstructionInProgress(finID);
	}

	@Override
	public FeeRefundInstruction getFeeRefundInstructionDetails(long feeRefundId, String type) {
		return feeRefundInstructionDAO.getFeeRefundInstructionDetails(feeRefundId, type);
	}

	public void setFeeRefundInstructionDAO(FeeRefundInstructionDAO feeRefundInstructionDAO) {
		this.feeRefundInstructionDAO = feeRefundInstructionDAO;
	}

}