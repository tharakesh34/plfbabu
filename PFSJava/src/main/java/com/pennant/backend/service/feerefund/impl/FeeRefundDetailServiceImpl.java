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
 * * FileName : PaymentDetailServiceImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 27-05-2017 * *
 * Modified Date : 27-05-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 27-05-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.service.feerefund.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.backend.dao.feerefund.FeeRefundDetailDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.feerefund.FeeRefundDetail;
import com.pennant.backend.model.finance.ManualAdviseMovements;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.feerefund.FeeRefundDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>PaymentDetail</b>.<br>
 */
public class FeeRefundDetailServiceImpl extends GenericService<FeeRefundDetail> implements FeeRefundDetailService {
	private static final Logger logger = LogManager.getLogger(FeeRefundDetailServiceImpl.class);

	private FeeRefundDetailDAO feeRefundDetailDAO;

	@Override
	public List<AuditDetail> setFeeRefundDetailAuditData(List<FeeRefundDetail> frdList, String auditTranType,
			String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new FeeRefundDetail(),
				new FeeRefundDetail().getExcludeFields());
		for (int i = 0; i < frdList.size(); i++) {
			FeeRefundDetail detail = frdList.get(i);
			if (StringUtils.isEmpty(detail.getRecordType())) {
				continue;
			}
			boolean isRcdType = false;
			if (detail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				detail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (detail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				detail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				isRcdType = true;
			} else if (detail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				detail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}
			if ("saveOrUpdate".equals(method) && isRcdType) {
				detail.setNewRecord(true);
			}
			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (detail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (detail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| detail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}
			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], detail.getBefImage(), detail));
		}
		logger.debug("Leaving");
		return auditDetails;
	}

	@Override
	public List<AuditDetail> processFeeRefundDetails(List<AuditDetail> auditDetails, TableType type, String methodName,
			long linkedTranId, long finID) {
		logger.debug("Entering");

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		List<ManualAdviseMovements> adviseMovements = new ArrayList<ManualAdviseMovements>();

		for (int i = 0; i < auditDetails.size(); i++) {
			FeeRefundDetail frd = (FeeRefundDetail) auditDetails.get(i).getModelData();
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
				frd.setRoleCode("");
				frd.setNextRoleCode("");
				frd.setTaskId("");
				frd.setNextTaskId("");
				frd.setWorkflowId(0);
			}
			if (frd.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (frd.isNewRecord()) {
				saveRecord = true;
				if (frd.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					frd.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (frd.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					frd.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (frd.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					frd.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}
			} else if (frd.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (frd.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (frd.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (frd.isNewRecord()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = frd.getRecordType();
				recordStatus = frd.getRecordStatus();
				frd.setRecordType("");
				frd.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {

				long detailId = feeRefundDetailDAO.save(frd, tableType);
				frd.setId(detailId);

			}
			if (updateRecord) {
				feeRefundDetailDAO.update(frd, tableType);
			}
			if (deleteRecord) {
				feeRefundDetailDAO.delete(frd, tableType);
			}
			if (approveRec) {
				frd.setRecordType(rcdType);
				frd.setRecordStatus(recordStatus);
			}

			if ("doApprove".equals(methodName)) {
				if (!PennantConstants.RECORD_TYPE_NEW.equals(frd.getRecordType())) {
					frd.setBefImage(feeRefundDetailDAO.getFeeRefundDetail(frd.getId(), TableType.MAIN_TAB));
				}
			}
			auditDetails.get(i).setModelData(frd);
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
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AuditDetail> delete(List<FeeRefundDetail> list, TableType tableType, String auditTranType,
			long paymentId) {
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		FeeRefundDetail frd = null;
		if (list != null && !list.isEmpty()) {
			String[] fields = PennantJavaUtil.getFieldDetails(new FeeRefundDetail(),
					new FeeRefundDetail().getExcludeFields());
			for (int i = 0; i < list.size(); i++) {
				frd = list.get(i);
				if (StringUtils.isNotEmpty(frd.getRecordType()) || StringUtils.isEmpty(tableType.toString())) {
					auditDetails
							.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], frd.getBefImage(), frd));
				}

			}
			feeRefundDetailDAO.deleteList(frd, tableType);
		}
		return auditDetails;
	}

	@Override
	public BigDecimal getPrvRefundAmt(long finID, long adviseID) {
		return feeRefundDetailDAO.getPrvRefundAmt(finID, adviseID);
	}

	@Override
	public List<FeeRefundDetail> getFeeRefundDetailList(long feeRefundId, TableType tableType) {
		return feeRefundDetailDAO.getFeeRefundDetailList(feeRefundId, tableType);
	}

	public void setFeeRefundDetailDAO(FeeRefundDetailDAO feeRefundDetailDAO) {
		this.feeRefundDetailDAO = feeRefundDetailDAO;
	}

}