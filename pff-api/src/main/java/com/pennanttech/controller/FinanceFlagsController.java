package com.pennanttech.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.cxf.phase.PhaseInterceptorChain;
import org.apache.log4j.Logger;

import com.pennant.app.util.APIHeader;
import com.pennant.app.util.SessionUserDetails;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.LoggedInUser;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.financemanagement.FinFlagsDetail;
import com.pennant.backend.model.financemanagement.FinanceFlag;
import com.pennant.backend.service.finance.FinanceFlagsService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.ws.service.APIErrorHandlerService;

public class FinanceFlagsController {
	private static final Logger logger = Logger.getLogger(FinanceFlagsController.class);
	private FinanceFlagsService financeFlagsService;

	/**
	 * Method to process and fetch LoanFlags details
	 * 
	 * @param finReference
	 * @return
	 */
	public FinanceFlag getLoanFlags(String finReference) {
		logger.debug("Enteing");

		FinanceFlag response = null;
		try {
			response = financeFlagsService.getFinanceFlagsByRef(finReference, "_View");
			if (response != null && response.getFinFlagDetailList() != null && !response.getFinFlagDetailList().isEmpty()) {
				response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
			} else {
				response = new FinanceFlag();
				response.setFinFlagDetailList(null);
				String[] valueParm = new String[1];
				valueParm[0] = finReference;
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90218", valueParm));
			}
		} catch (Exception e) {
			logger.error("Exception", e);
			APIErrorHandlerService.logUnhandledException(e);
			response = new FinanceFlag();
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
		}
		logger.debug("Leaving");
		return response;
	}
	
	
	/**
	 * Method for create LoanFlags in PLF system.
	 * @param financeFlag
	 */

	public WSReturnStatus addLoanFlags(FinanceFlag financeFlag) {
		logger.debug("Entering");
		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());

		List<FinFlagsDetail> flagseList = financeFlag.getFinFlagDetailList();
		for(FinFlagsDetail detail: flagseList){
			detail.setLastMntBy(userDetails.getUserId());
			detail.setModuleName(FinanceConstants.MODULE_NAME);
		}

		financeFlag.setUserDetails(userDetails);
		financeFlag.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		financeFlag.setVersion(1);
		financeFlag.setNewRecord(true);
		financeFlag.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		financeFlag.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		financeFlag.setLastMntBy(userDetails.getUserId());

		WSReturnStatus response = null;
		try {
			//get the header details from the request
			APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
					.get(APIHeader.API_HEADER_KEY);
			AuditHeader auditHeader = getAuditHeader(financeFlag,PennantConstants.TRAN_WF);
			//set the headerDetails to AuditHeader
			auditHeader.setApiHeader(reqHeaderDetails);
			auditHeader = financeFlagsService.doApprove(auditHeader);

			if (auditHeader.getErrorMessage() != null) {
				for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
					response = (APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
				}
			} else {
				response = APIErrorHandlerService.getSuccessStatus();
			}
		} catch (Exception e) {
			logger.error("Exception", e);
			APIErrorHandlerService.logUnhandledException(e);
			return APIErrorHandlerService.getFailedStatus();
		}
		logger.debug("Leaving");
		return response;
	}
	
	/**
	 * Method for Delete LoanFlags in PLF system.
	 * @param financeFlag
	 */
	public WSReturnStatus deleteLoanFlags(FinanceFlag financeFlag) {
		logger.debug("Entering");

		// user language
		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		financeFlag.setUserDetails(userDetails);
		financeFlag.setRecordType(PennantConstants.RECORD_TYPE_DEL);
		financeFlag.setVersion(1);
		financeFlag.setNewRecord(false);
		financeFlag.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		financeFlag.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		financeFlag.setLastMntBy(userDetails.getUserId());

		for (FinFlagsDetail flagDetail : financeFlag.getFinFlagDetailList()) {
			flagDetail.setLastMntBy(userDetails.getUserId());
			flagDetail.setModuleName(FinanceConstants.MODULE_NAME);
		}

		WSReturnStatus response = new WSReturnStatus();
		try {
			//get the header details from the request
			APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange().get(APIHeader.API_HEADER_KEY);
			AuditHeader auditHeader = getAuditHeader(financeFlag,PennantConstants.TRAN_WF);
			//set the headerDetails to AuditHeader
			auditHeader.setApiHeader(reqHeaderDetails);
			auditHeader = financeFlagsService.deleteFinanceFlag(auditHeader);
			if (auditHeader.getErrorMessage() != null) {
				for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
					response = (APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
				}
			} else {
				response = APIErrorHandlerService.getSuccessStatus();
			}
		} catch (Exception e) {
			logger.error("Exception", e);
			APIErrorHandlerService.logUnhandledException(e);
			return APIErrorHandlerService.getFailedStatus();
		}
		logger.debug("Leaving");
		return response;
	}
	/**
	 * Get Audit Header Details
	 * 
	 * @param aFinanceFlag
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(FinanceFlag aFinanceFlag, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aFinanceFlag.getBefImage(), aFinanceFlag);
		return new AuditHeader(null,
				null, null, null, auditDetail,
				aFinanceFlag.getUserDetails(), new HashMap<String, ArrayList<ErrorDetail>>());
	}
	public void setFinanceFlagsService(FinanceFlagsService financeFlagsService) {
		this.financeFlagsService = financeFlagsService;
	}

}
