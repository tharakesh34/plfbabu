package com.pennanttech.controller;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.util.APIHeader;
import com.pennant.app.util.SessionUserDetails;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinCovenantType;
import com.pennant.backend.model.finance.FinMaintainInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.service.finance.FinCovenantMaintanceService;
import com.pennant.backend.service.finance.FinCovenantTypeService;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.ws.service.APIErrorHandlerService;

public class FinCovenantController {
	private static Logger logger = LogManager.getLogger(FinServiceInstController.class);

	private FinCovenantMaintanceService finCovenantMaintanceService;
	private FinCovenantTypeService finCovenantTypeService;
	private FinanceMainDAO financeMainDAO;

	public WSReturnStatus addFinCovenant(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);
		FinanceMain fm = financeDetail.getFinScheduleData().getFinanceMain();
		List<FinCovenantType> covenantTypeList = financeDetail.getCovenantTypeList();
		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		String finReference = fm.getFinReference();
		boolean referenceExitsinLQ = financeMainDAO.isFinReferenceExists(finReference, "_Temp", false);
		if (referenceExitsinLQ) {
			for (FinCovenantType finCovenantType : covenantTypeList) {
				finCovenantType.setFinReference(finReference);
				finCovenantType.setNewRecord(true);
				finCovenantType.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				finCovenantType.setTaskId(fm.getTaskId());
				finCovenantType.setNextTaskId(fm.getNextTaskId());
				finCovenantType.setRoleCode(fm.getRoleCode());
				finCovenantType.setNextRoleCode(fm.getNextRoleCode());
				finCovenantType.setRecordStatus(fm.getRecordStatus());
				finCovenantType.setWorkflowId(fm.getWorkflowId());
				finCovenantType.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				finCovenantType.setUserDetails(userDetails);
				finCovenantType.setVersion(1);
			}
			List<AuditDetail> covenantAuditDetails = null;
			covenantAuditDetails = finCovenantTypeService.saveOrUpdate(covenantTypeList, "_Temp",
					PennantConstants.TRAN_WF);
			if (CollectionUtils.isNotEmpty(covenantAuditDetails)) {
				for (AuditDetail auditDetail : covenantAuditDetails) {
					for (ErrorDetail errorDetail : auditDetail.getErrorDetails())
						return APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError());
				}
			}
		} else {
			FinMaintainInstruction finMaintainInstruction = new FinMaintainInstruction();
			for (FinCovenantType finCovenantType : covenantTypeList) {
				finCovenantType.setNewRecord(true);
				finCovenantType.setLastMntBy(userDetails.getUserId());
				finCovenantType.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				finCovenantType.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				finCovenantType.setUserDetails(userDetails);
				finCovenantType.setVersion(1);
			}
			finMaintainInstruction.setFinReference(financeDetail.getFinReference());
			finMaintainInstruction.setNewRecord(true);
			finMaintainInstruction.setEvent(FinServiceEvent.COVENANTS);
			finMaintainInstruction.setVersion(1);
			finMaintainInstruction.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			finMaintainInstruction.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			finMaintainInstruction.setLastMntBy(userDetails.getUserId());
			finMaintainInstruction.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			finMaintainInstruction.setUserDetails(userDetails);
			finMaintainInstruction.setFinCovenantTypeList(covenantTypeList);

			AuditHeader auditHeader = getAuditHeader(finMaintainInstruction, PennantConstants.TRAN_WF);
			APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
					.get(APIHeader.API_HEADER_KEY);
			auditHeader.setApiHeader(reqHeaderDetails);

			auditHeader = finCovenantMaintanceService.doApprove(auditHeader);
			if (CollectionUtils.isNotEmpty(auditHeader.getErrorMessage())) {
				for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
					return APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError());
				}
			}

			if (CollectionUtils.isNotEmpty(auditHeader.getAuditDetail().getErrorDetails())) {
				for (ErrorDetail errorDetail : auditHeader.getAuditDetail().getErrorDetails()) {
					return APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError());
				}
			}
		}
		logger.debug(Literal.LEAVING);
		return APIErrorHandlerService.getSuccessStatus();
	}

	public WSReturnStatus updateFinCovenant(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);

		FinanceMain fm = financeDetail.getFinScheduleData().getFinanceMain();
		List<FinCovenantType> covenantTypeList = financeDetail.getCovenantTypeList();
		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		String finReference = fm.getFinReference();
		boolean referenceExitsinLQ = financeMainDAO.isFinReferenceExists(finReference, "_Temp", false);
		if (referenceExitsinLQ) {
			for (FinCovenantType finCovenantType : covenantTypeList) {
				finCovenantType.setFinReference(finReference);
				finCovenantType.setNewRecord(false);
				finCovenantType.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				finCovenantType.setTaskId(fm.getTaskId());
				finCovenantType.setNextTaskId(fm.getNextTaskId());
				finCovenantType.setRoleCode(fm.getRoleCode());
				finCovenantType.setNextRoleCode(fm.getNextRoleCode());
				finCovenantType.setRecordStatus(fm.getRecordStatus());
				finCovenantType.setWorkflowId(fm.getWorkflowId());
				finCovenantType.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				finCovenantType.setUserDetails(userDetails);
				finCovenantType.setVersion(fm.getVersion() + 1);
			}
			List<AuditDetail> covenantAuditDetails = null;
			covenantAuditDetails = finCovenantTypeService.saveOrUpdate(covenantTypeList, "_Temp",
					PennantConstants.TRAN_WF);
			if (CollectionUtils.isNotEmpty(covenantAuditDetails)) {
				for (AuditDetail auditDetail : covenantAuditDetails) {
					for (ErrorDetail errorDetail : auditDetail.getErrorDetails())
						return APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError());
				}
			}
		} else {
			FinMaintainInstruction finMaintainInstruction = new FinMaintainInstruction();
			for (FinCovenantType finCovenantType : covenantTypeList) {
				finCovenantType.setNewRecord(false);
				finCovenantType.setLastMntBy(userDetails.getUserId());
				finCovenantType.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				finCovenantType.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				finCovenantType.setUserDetails(userDetails);
				finCovenantType.setVersion(fm.getVersion() + 1);
			}
			finMaintainInstruction.setFinReference(financeDetail.getFinReference());
			finMaintainInstruction.setEvent(FinServiceEvent.COVENANTS);
			finMaintainInstruction.setNewRecord(true);
			finMaintainInstruction.setVersion(1);
			finMaintainInstruction.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			finMaintainInstruction.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			finMaintainInstruction.setLastMntBy(userDetails.getUserId());
			finMaintainInstruction.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			finMaintainInstruction.setUserDetails(userDetails);
			finMaintainInstruction.setFinCovenantTypeList(covenantTypeList);

			AuditHeader auditHeader = getAuditHeader(finMaintainInstruction, PennantConstants.TRAN_WF);
			APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
					.get(APIHeader.API_HEADER_KEY);
			auditHeader.setApiHeader(reqHeaderDetails);
			auditHeader = finCovenantMaintanceService.doApprove(auditHeader);
			if (CollectionUtils.isNotEmpty(auditHeader.getErrorMessage())) {
				for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
					return APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError());
				}
			}

			if (CollectionUtils.isNotEmpty(auditHeader.getAuditDetail().getErrorDetails())) {
				for (ErrorDetail errorDetail : auditHeader.getAuditDetail().getErrorDetails()) {
					return APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError());
				}
			}
		}

		logger.debug(Literal.LEAVING);
		return APIErrorHandlerService.getSuccessStatus();
	}

	public WSReturnStatus deleteFinCovenant(FinanceDetail financeDetail, boolean referenceExitsinLQ) {
		logger.debug(Literal.ENTERING);
		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		List<FinCovenantType> covenantTypeList = financeDetail.getCovenantTypeList();

		if (referenceExitsinLQ) {
			for (FinCovenantType finCovenantType : covenantTypeList) {
				finCovenantType.setFinReference(financeDetail.getFinReference());
				finCovenantType.setNewRecord(false);
				finCovenantType.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}
			List<AuditDetail> covenantAuditDetails = finCovenantTypeService.delete(covenantTypeList, "_Temp",
					PennantConstants.TRAN_WF);
			if (CollectionUtils.isNotEmpty(covenantAuditDetails)) {
				for (AuditDetail auditDetail : covenantAuditDetails) {
					for (ErrorDetail errorDetail : auditDetail.getErrorDetails())
						return APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError());
				}
			}
		} else {
			FinMaintainInstruction finMaintainInstruction = new FinMaintainInstruction();
			for (FinCovenantType finCovenantType : covenantTypeList) {
				finCovenantType.setNewRecord(false);
				finCovenantType.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}
			finMaintainInstruction.setFinReference(financeDetail.getFinReference());
			finMaintainInstruction.setNewRecord(true);
			finMaintainInstruction.setEvent(FinServiceEvent.COVENANTS);
			finMaintainInstruction.setVersion(1);
			finMaintainInstruction.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			finMaintainInstruction.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			finMaintainInstruction.setLastMntBy(userDetails.getUserId());
			finMaintainInstruction.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			finMaintainInstruction.setUserDetails(userDetails);
			finMaintainInstruction.setFinCovenantTypeList(covenantTypeList);

			AuditHeader auditHeader = getAuditHeader(finMaintainInstruction, PennantConstants.TRAN_WF);
			APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
					.get(APIHeader.API_HEADER_KEY);
			auditHeader.setApiHeader(reqHeaderDetails);
			auditHeader = finCovenantMaintanceService.doApprove(auditHeader);
			if (CollectionUtils.isNotEmpty(auditHeader.getErrorMessage())) {
				for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
					return APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError());
				}
			}

			if (CollectionUtils.isNotEmpty(auditHeader.getAuditDetail().getErrorDetails())) {
				for (ErrorDetail errorDetail : auditHeader.getAuditDetail().getErrorDetails()) {
					return APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError());
				}
			}
		}
		logger.debug(Literal.LEAVING);
		return APIErrorHandlerService.getSuccessStatus();
	}

	protected AuditHeader getAuditHeader(FinMaintainInstruction finMaintainInstruction, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, null, finMaintainInstruction);
		return new AuditHeader(finMaintainInstruction.getFinReference(), null, null, null, auditDetail,
				finMaintainInstruction.getUserDetails(), new HashMap<String, List<ErrorDetail>>());
	}

	public FinCovenantMaintanceService getFinCovenantMaintanceService() {
		return finCovenantMaintanceService;
	}

	public void setFinCovenantMaintanceService(FinCovenantMaintanceService finCovenantMaintanceService) {
		this.finCovenantMaintanceService = finCovenantMaintanceService;
	}

	public void setFinCovenantTypeService(FinCovenantTypeService finCovenantTypeService) {
		this.finCovenantTypeService = finCovenantTypeService;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

}