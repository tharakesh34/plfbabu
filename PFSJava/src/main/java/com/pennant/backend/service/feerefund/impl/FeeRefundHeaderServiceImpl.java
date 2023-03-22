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
 * * FileName : PaymentHeaderServiceImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 27-05-2017 * *
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.core.FinOverDueService;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.feerefund.FeeRefundHeaderDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.feerefund.FeeRefundDetail;
import com.pennant.backend.model.feerefund.FeeRefundHeader;
import com.pennant.backend.model.feerefund.FeeRefundInstruction;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.PaymentInstruction;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.feerefund.FeeRefundDetailService;
import com.pennant.backend.service.feerefund.FeeRefundHeaderService;
import com.pennant.backend.service.feerefund.FeeRefundInstructionService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.pff.payment.model.PaymentDetail;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>PaymentHeader</b>.<br>
 */
public class FeeRefundHeaderServiceImpl extends GenericService<FeeRefundHeader> implements FeeRefundHeaderService {
	private static final Logger logger = LogManager.getLogger(FeeRefundHeaderServiceImpl.class);

	private FeeRefundHeaderDAO feeRefundHeaderDAO;
	private FinanceMainDAO financeMainDAO;
	private AuditHeaderDAO auditHeaderDAO;
	private FeeRefundDetailService feeRefundDetailService;
	private FeeRefundInstructionService feeRefundInstructionService;
	private FinOverDueService finOverDueService;

	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader, "saveOrUpdate");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		FeeRefundHeader frh = (FeeRefundHeader) auditHeader.getAuditDetail().getModelData();

		long finID = frh.getFinID();

		TableType tableType = TableType.MAIN_TAB;
		if (frh.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (StringUtils.equals(frh.getRecordStatus(), PennantConstants.RCD_STATUS_SUBMITTED)) {
			frh.setApprovalStatus(PennantConstants.FEE_REFUND_APPROVAL_HOLD);
		}

		if (frh.isNewRecord()) {
			frh.setId(feeRefundHeaderDAO.save(frh, tableType));
			setFeeRefundHeaderId(frh);
			auditHeader.getAuditDetail().setModelData(frh);
			auditHeader.setAuditReference(String.valueOf(frh.getId()));
		} else {
			feeRefundHeaderDAO.update(frh, tableType);
		}

		// PaymentHeader
		List<FeeRefundDetail> pdList = frh.getFeeRefundDetailList();

		if (pdList != null && pdList.size() > 0) {
			List<AuditDetail> auditDetail = frh.getAuditDetailMap().get("FeeRefundDetails");
			auditDetail = this.feeRefundDetailService.processFeeRefundDetails(auditDetail, tableType, "", 0, finID);
			auditDetails.addAll(auditDetail);
		}

		// PaymentInstructions
		if (frh.getFeeRefundInstruction() != null) {
			List<AuditDetail> auditDetail = frh.getAuditDetailMap().get("FeeRefundInstructions");
			auditDetail = this.feeRefundInstructionService.processFeeRefundInstrDetails(auditDetail, tableType, "");
			auditDetails.addAll(auditDetail);
		}
		String rcdMaintainSts = FinServiceEvent.FEEREFUNDINST;
		financeMainDAO.updateMaintainceStatus(finID, rcdMaintainSts);

		auditHeader.setAuditDetails(auditDetails);
		auditHeaderDAO.addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}

	private void setFeeRefundHeaderId(FeeRefundHeader frh) {
		if (frh.getFeeRefundDetailList() != null && frh.getFeeRefundDetailList().size() > 0) {
			for (FeeRefundDetail detail : frh.getFeeRefundDetailList()) {
				detail.setHeaderID(frh.getId());
			}
		}
		if (frh.getFeeRefundInstruction() != null) {
			frh.getFeeRefundInstruction().setHeaderID(frh.getId());
		}
	}

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		FeeRefundHeader ph = (FeeRefundHeader) auditHeader.getAuditDetail().getModelData();
		feeRefundHeaderDAO.delete(ph, TableType.MAIN_TAB);
		auditHeader.setAuditDetails(
				processChildsAudit(deleteChilds(ph, TableType.MAIN_TAB, auditHeader.getAuditTranType())));

		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public FeeRefundHeader getFeeRefundHeader(long feeRefundId) {
		FeeRefundHeader frh = feeRefundHeaderDAO.getFeeRefundHeader(feeRefundId, "_View");
		List<FeeRefundDetail> list = this.feeRefundDetailService.getFeeRefundDetailList(frh.getId(), TableType.VIEW);

		frh.setOverDueAgainstLoan(finOverDueService.getDueAgnistLoan(frh.getFinID()));
		frh.setOverDueAgainstCustomer(finOverDueService.getDueAgnistCustomer(frh.getFinID(), false));

		frh.setFeeRefundDetailList(list);

		FeeRefundInstruction fri = this.feeRefundInstructionService.getFeeRefundInstructionDetails(frh.getId(),
				"_View");
		if (fri != null) {
			frh.setFeeRefundInstruction(fri);
		}
		return frh;
	}

	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		FeeRefundHeader frh = (FeeRefundHeader) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeader.setAuditDetails(
				processChildsAudit(deleteChilds(frh, TableType.TEMP_TAB, auditHeader.getAuditTranType())));
		financeMainDAO.updateMaintainceStatus(frh.getFinID(), "");
		feeRefundHeaderDAO.delete(frh, TableType.TEMP_TAB);
		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		auditHeader = prepareChildsAudit(auditHeader, method);
		auditHeader.setErrorList(validateChilds(auditHeader, auditHeader.getUsrLanguage(), method));

		for (int i = 0; i < auditDetails.size(); i++) {
			auditHeader.setErrorList(auditDetails.get(i).getErrorDetails());
		}
		auditHeader = nextProcess(auditHeader);

		logger.debug("Leaving");

		return auditHeader;
	}

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug(Literal.ENTERING);

		// Write the required validation over hear.
		FeeRefundHeader frh = (FeeRefundHeader) auditDetail.getModelData();

		BigDecimal overDueAmt = frh.getOverDueAgainstCustomer();
		overDueAmt = overDueAmt.add(frh.getOverDueAgainstLoan());

		if (overDueAmt.compareTo(BigDecimal.ZERO) > 0) {
			if ("saveOrUpdate".equals(method) || "doApprove".equals(method)) {
				auditDetail.setErrorDetail(ErrorUtil
						.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "REFUND_050", null, null)));
			}
		}

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	private AuditHeader prepareChildsAudit(AuditHeader auditHeader, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		Map<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

		FeeRefundHeader frh = (FeeRefundHeader) auditHeader.getAuditDetail().getModelData();
		String auditTranType = "";

		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (frh.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}
		// PaymentDetails
		if (frh.getFeeRefundDetailList() != null && frh.getFeeRefundDetailList().size() > 0) {
			for (FeeRefundDetail detail : frh.getFeeRefundDetailList()) {
				detail.setFinID(frh.getFinID());
				detail.setHeaderID(frh.getId());
				detail.setWorkflowId(frh.getWorkflowId());
			}
			auditDetailMap.put("FeeRefundDetails", this.feeRefundDetailService
					.setFeeRefundDetailAuditData(frh.getFeeRefundDetailList(), auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("FeeRefundDetails"));
		}

		// Insurance Details
		if (frh.getFeeRefundInstruction() != null) {
			FeeRefundInstruction detail = frh.getFeeRefundInstruction();
			detail.setHeaderID(frh.getId());
			detail.setWorkflowId(frh.getWorkflowId());
			detail.setRecordStatus(frh.getRecordStatus());
			detail.setRecordType(frh.getRecordType());
			detail.setNewRecord(frh.isNewRecord());
			detail.setUserDetails(frh.getUserDetails());
			detail.setLastMntBy(frh.getLastMntBy());
			detail.setLastMntOn(frh.getLastMntOn());
			detail.setRoleCode(frh.getRoleCode());
			detail.setNextRoleCode(frh.getNextRoleCode());
			detail.setTaskId(frh.getTaskId());
			detail.setNextTaskId(frh.getNextTaskId());

			auditDetailMap.put("FeeRefundInstructions", this.feeRefundInstructionService
					.setFeeRefundInstDetailAuditData(frh.getFeeRefundInstruction(), auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("FeeRefundInstructions"));
		}

		frh.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(frh);
		auditHeader.setAuditDetails(auditDetails);
		logger.debug("Leaving");

		return auditHeader;
	}

	private List<ErrorDetail> validateChilds(AuditHeader auditHeader, String usrLanguage, String method) {
		logger.debug("Entering");

		List<ErrorDetail> errorDetails = new ArrayList<ErrorDetail>();
		FeeRefundHeader ph = (FeeRefundHeader) auditHeader.getAuditDetail().getModelData();

		List<AuditDetail> auditDetails = null;

		// PaymentDetails
		if (ph.getAuditDetailMap().get("FeeRefundDetails") != null) {
			auditDetails = ph.getAuditDetailMap().get("FeeRefundDetails");
			for (AuditDetail auditDetail : auditDetails) {
				List<ErrorDetail> details = this.feeRefundDetailService.validation(auditDetail, usrLanguage, method)
						.getErrorDetails();
				if (details != null) {
					errorDetails.addAll(details);
				}
			}
		}

		// PaymentInstruction
		if (ph.getAuditDetailMap().get("FeeRefundInstructions") != null) {
			auditDetails = ph.getAuditDetailMap().get("FeeRefundInstructions");
			for (AuditDetail auditDetail : auditDetails) {
				List<ErrorDetail> details = this.feeRefundInstructionService
						.validation(auditDetail, usrLanguage, method).getErrorDetails();
				if (details != null) {
					errorDetails.addAll(details);
				}
			}
		}
		logger.debug("Leaving");
		return errorDetails;
	}

	public List<AuditDetail> deleteChilds(FeeRefundHeader frh, TableType tableType, String auditTranType) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		// PaymentDetails
		if (frh.getFeeRefundDetailList() != null && !frh.getFeeRefundDetailList().isEmpty()) {
			auditDetails.addAll(this.feeRefundDetailService.delete(frh.getFeeRefundDetailList(), tableType,
					auditTranType, frh.getId()));
		}
		// PaymentInstructions
		if (frh.getFeeRefundInstruction() != null) {
			auditDetails.addAll(this.feeRefundInstructionService.delete(frh.getFeeRefundInstruction(), tableType,
					auditTranType, frh.getId()));
		}
		logger.debug("Leaving");
		return auditDetails;
	}

	private List<AuditDetail> processChildsAudit(List<AuditDetail> list) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		if (list == null || list.isEmpty()) {
			return auditDetails;
		}

		for (AuditDetail detail : list) {
			String transType = "";
			String rcdType = "";
			Object object = detail.getModelData();

			if (object instanceof PaymentDetail) {
				rcdType = ((PaymentDetail) object).getRecordType();
			} else if (object instanceof PaymentInstruction) {
				rcdType = ((PaymentInstruction) object).getRecordType();
			}

			if (PennantConstants.RECORD_TYPE_NEW.equalsIgnoreCase(rcdType)) {
				transType = PennantConstants.TRAN_ADD;
			} else if (PennantConstants.RECORD_TYPE_DEL.equalsIgnoreCase(rcdType)
					|| PennantConstants.RECORD_TYPE_CAN.equalsIgnoreCase(rcdType)) {
				transType = PennantConstants.TRAN_DEL;
			} else {
				transType = PennantConstants.TRAN_UPD;
			}
			auditDetails.add(new AuditDetail(transType, detail.getAuditSeq(), detail.getBefImage(), object));
		}
		logger.debug("Leaving");

		return auditDetails;
	}

	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) throws InterfaceException {
		logger.info(Literal.ENTERING);

		String tranType = "";
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader, "doApprove");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		FeeRefundHeader frh = new FeeRefundHeader();
		BeanUtils.copyProperties((FeeRefundHeader) auditHeader.getAuditDetail().getModelData(), frh);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(frh.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(feeRefundHeaderDAO.getFeeRefundHeader(frh.getId(), ""));
		}

		if (frh.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			auditDetails.addAll(deleteChilds(frh, TableType.MAIN_TAB, tranType));
			feeRefundHeaderDAO.delete(frh, TableType.MAIN_TAB);
		} else {
			frh.setRoleCode("");
			frh.setNextRoleCode("");
			frh.setTaskId("");
			frh.setNextTaskId("");
			frh.setWorkflowId(0);

			if (frh.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				frh.setRecordType("");
				feeRefundHeaderDAO.save(frh, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				frh.setRecordType("");
				feeRefundHeaderDAO.update(frh, TableType.MAIN_TAB);
			}

			// PaymentDetails
			if (auditHeader.getAuditDetails() != null && !auditHeader.getAuditDetails().isEmpty()) {
				List<AuditDetail> frd = frh.getAuditDetailMap().get("FeeRefundDetails");
				if (frd != null && !frd.isEmpty()) {
					frd = this.feeRefundDetailService.processFeeRefundDetails(frd, TableType.MAIN_TAB, "doApprove",
							frh.getLinkedTranId(), frh.getFinID());
					auditDetails.addAll(frd);
				}
			}
			// PaymentInstruction
			if (auditHeader.getAuditDetails() != null && !auditHeader.getAuditDetails().isEmpty()) {
				List<AuditDetail> friList = frh.getAuditDetailMap().get("FeeRefundInstructions");

				if (friList != null && !friList.isEmpty()) {
					friList = this.feeRefundInstructionService.processFeeRefundInstrDetails(friList, TableType.MAIN_TAB,
							"doApprove");
					auditDetails.addAll(friList);
				}
			}
		}

		auditHeader.setAuditDetails(deleteChilds(frh, TableType.TEMP_TAB, auditHeader.getAuditTranType()));
		String[] fields = PennantJavaUtil.getFieldDetails(new FeeRefundHeader(), frh.getExcludeFields());
		auditHeader.setAuditDetail(
				new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1], frh.getBefImage(), frh));

		feeRefundHeaderDAO.delete(frh, TableType.TEMP_TAB);
		financeMainDAO.updateMaintainceStatus(frh.getFinID(), "");

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(frh);
		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	@Override
	public FinanceMain getFinanceDetails(long finID) {
		return feeRefundHeaderDAO.getFinanceDetails(finID);
	}

	@Override
	public List<ManualAdvise> getManualAdvise(long finID) {
		return feeRefundHeaderDAO.getManualAdvise(finID);
	}

	@Override
	public FeeRefundHeader getFeeRefundHeader(long id, String type) {
		return feeRefundHeaderDAO.getFeeRefundHeader(id, type);
	}

	@Override
	public boolean isFileDownloaded(long id, int isDownloaded) {
		return feeRefundHeaderDAO.isFileDownloaded(id, isDownloaded);
	}

	@Override
	public void updateApprovalStatus(Long id, int downloadStatus) {
		feeRefundHeaderDAO.updateApprovalStatus(id, downloadStatus);
	}

	@Override
	public boolean isInProgress(long finID) {
		return feeRefundInstructionService.isInstructionInProgress(finID);
	}

	@Autowired
	public void setFeeRefundHeaderDAO(FeeRefundHeaderDAO feeRefundHeaderDAO) {
		this.feeRefundHeaderDAO = feeRefundHeaderDAO;
	}

	@Autowired
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	@Autowired
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	@Autowired
	public void setFeeRefundDetailService(FeeRefundDetailService feeRefundDetailService) {
		this.feeRefundDetailService = feeRefundDetailService;
	}

	@Autowired
	public void setFeeRefundInstructionService(FeeRefundInstructionService feeRefundInstructionService) {
		this.feeRefundInstructionService = feeRefundInstructionService;
	}

	@Autowired
	public void setFinOverDueService(FinOverDueService finOverDueService) {
		this.finOverDueService = finOverDueService;
	}

}