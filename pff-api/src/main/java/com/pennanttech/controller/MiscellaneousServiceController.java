package com.pennanttech.controller;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.apache.log4j.Logger;

import com.pennant.app.util.APIHeader;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SessionUserDetails;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.applicationmaster.AccountMapping;
import com.pennant.backend.model.applicationmaster.TransactionCode;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.others.JVPosting;
import com.pennant.backend.model.others.JVPostingEntry;
import com.pennant.backend.service.applicationmaster.AccountMappingService;
import com.pennant.backend.service.applicationmaster.TransactionCodeService;
import com.pennant.backend.service.finance.FinanceMainService;
import com.pennant.backend.service.others.JVPostingService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.util.APIConstants;
import com.pennanttech.ws.service.APIErrorHandlerService;

public class MiscellaneousServiceController {
	
	private final Logger logger = Logger.getLogger(getClass());
	private FinanceMainService financeMainService;
	private TransactionCodeService transactionCodeService;
	private AccountMappingService accountMappingService;
	private JVPostingService jVPostingService;
	
	private void setJVPostingEntryMandatoryFieldsData(JVPostingEntry postingEntry)	{
		
		logger.info(Literal.ENTERING);
		
		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		postingEntry.setUserDetails(userDetails);
		postingEntry.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		postingEntry.setLastMntBy(userDetails.getUserId());
		postingEntry.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		postingEntry.setFinSourceID(APIConstants.FINSOURCE_ID_API);
		
		logger.info(Literal.LEAVING);
	}
		
	private void setJVPostingMandatoryFieldsData(JVPosting posting)	{
		logger.info(Literal.ENTERING);
		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		posting.setUserDetails(userDetails);
		posting.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		posting.setLastMntBy(userDetails.getUserId());
		posting.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		posting.setFinSourceID(APIConstants.FINSOURCE_ID_API);
		logger.info(Literal.LEAVING);
	}
	
	public WSReturnStatus prepareJVPostData(JVPosting jvPosting)	{
		
		logger.info(Literal.ENTERING);
		
		WSReturnStatus returnStatus = new WSReturnStatus();
		JVPosting posting = new JVPosting();
		JVPostingEntry postingEntry = null;
		List<JVPostingEntry> postingEntryList = new ArrayList<>();
		
		FinanceMain financeMainData = financeMainService.getFinanceByFinReference(jvPosting.getReference(), "_AView");
		if ( null != financeMainData )	{
			posting.setNewRecord(true);
			posting.setVersion(1);
			posting.setBranch(financeMainData.getFinBranch());
			posting.setBatch(jvPosting.getBatch());
			posting.setBatchReference(0);
			if(StringUtils.isNotBlank(jvPosting.getCurrency()))	
				posting.setCurrency(jvPosting.getCurrency());
			else
				posting.setCurrency(financeMainData.getFinCcy());
			posting.setPostingDate(DateUtility.getAppDate());
			posting.setPostAgainst(FinanceConstants.POSTING_AGAINST_LOAN);
			posting.setBatchPurpose("");
			posting.setBatchPostingStatus(financeMainData.getFinPurpose());
			posting.setReference(financeMainData.getFinReference());
			posting.setPostingDivision(financeMainData.getLovDescFinDivision());

			BigDecimal totalDebits=BigDecimal.ZERO;
			BigDecimal totalCredits=BigDecimal.ZERO;
			// JVPostingEntry Fields Data
			for(JVPostingEntry entry : jvPosting.getJVPostingEntrysList())	{
				postingEntry = new JVPostingEntry();
				postingEntry.setNewRecord(true);
				postingEntry.setVersion(1);
				postingEntry.setTxnCCy(entry.getTxnCCy());
				postingEntry.setNarrLine4(entry.getNarrLine4());
				postingEntry.setNarrLine3(entry.getNarrLine3());
				postingEntry.setNarrLine2(entry.getNarrLine2());
				postingEntry.setNarrLine1(entry.getNarrLine1());
				postingEntry.setTxnReference(entry.getTxnReference());
				postingEntry.setValueDate(DateUtility.getAppDate());
				postingEntry.setPostingDate(DateUtility.getAppDate());
				postingEntry.setBatchReference(entry.getBatchReference());
				postingEntry.setAccount(entry.getAccount());
				
				TransactionCode transactionCode = transactionCodeService.getApprovedTransactionCodeById(entry.getTxnCode());
				if ( null != transactionCode )	{
					AccountMapping accountMapping = accountMappingService.getApprovedAccountMapping(entry.getAccount());
					if ( null != accountMapping)	{
						switch (transactionCode.getTranType())	{
							case "C":
								postingEntry.setAccountName(accountMapping.getAccountTypeDesc());
								postingEntry.setAccCCy(financeMainData.getFinCcy());
								postingEntry.setTxnCCy(financeMainData.getFinCcy());
								postingEntry.setTxnEntry(transactionCode.getTranType());
								postingEntry.setAcType(accountMapping.getAccountType());
								postingEntry.setTxnAmount(entry.getTxnAmount());
								postingEntry.setTxnAmount_Ac(entry.getTxnAmount());
								postingEntry.setTxnCode(entry.getTxnCode());
								postingEntry.setAccount(entry.getAccount());
								posting.setCreditsCount(jvPosting.getCreditsCount()+1);
								postingEntry.setRecordType(PennantConstants.RCD_ADD);
								totalCredits = totalCredits.add(entry.getTxnAmount());
								break;
						
							case "D":
								postingEntry.setAccountName(accountMapping.getAccountTypeDesc());
								postingEntry.setAccCCy(financeMainData.getFinCcy());
								postingEntry.setTxnCCy(financeMainData.getFinCcy());
								postingEntry.setTxnEntry(transactionCode.getTranType());
								postingEntry.setAcType(entry.getAccount());
								postingEntry.setTxnAmount(entry.getTxnAmount());
								postingEntry.setTxnAmount_Ac(entry.getTxnAmount());
								postingEntry.setTxnCode(entry.getTxnCode());
								postingEntry.setDebitTxnCode(entry.getTxnCode());
								postingEntry.setDebitAccount(entry.getAccount());
								posting.setDebitCount(jvPosting.getDebitCount()+1);
								postingEntry.setRecordType(PennantConstants.RCD_ADD);
								totalDebits = totalDebits.add(entry.getTxnAmount());
								break;
						}
					}
				}
				setJVPostingEntryMandatoryFieldsData(postingEntry);
				postingEntryList.add(postingEntry);
			}
			posting.setJVPostingEntrysList(postingEntryList);
			posting.setTotCreditsByBatchCcy(totalCredits);
			posting.setTotDebitsByBatchCcy(totalDebits);
			setJVPostingMandatoryFieldsData(posting);
			
			returnStatus = saveJVPostingData(posting);
		}
		else	{
			// financemain data is not available
		}
		
		logger.info(Literal.LEAVING);
		
		return returnStatus;
	}
	
	private AuditHeader prepareAuditHeader(JVPosting aJVPosting, String tranType) {
		
		logger.info(Literal.ENTERING);
		
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aJVPosting.getBefImage(), aJVPosting);
		AuditHeader auditHeader = new AuditHeader(aJVPosting.getReference(), Long.toString(aJVPosting.getBatchReference()), null, null, auditDetail, aJVPosting.getUserDetails(), new HashMap<String, ArrayList<ErrorDetail>>());
		
		APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange().get(APIHeader.API_HEADER_KEY);
		auditHeader.setApiHeader(reqHeaderDetails);
		
		logger.info(Literal.LEAVING);
		
		return auditHeader;
	}
	
	private WSReturnStatus saveJVPostingData(final JVPosting postReadyData)	{
		
		logger.info(Literal.ENTERING);
		
		WSReturnStatus returnStatus = new WSReturnStatus();
		
		AuditHeader auditHeader = prepareAuditHeader(postReadyData, PennantConstants.TRAN_WF);
		AuditHeader savedJVPostData = jVPostingService.doApprove(auditHeader);
		
		if (savedJVPostData.getAuditError() != null)	{
			for (ErrorDetail errorDetail : savedJVPostData.getErrorMessage()) {
				returnStatus = APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError());
			}
		}
		else
			returnStatus = APIErrorHandlerService.getSuccessStatus();
		
		logger.info(Literal.LEAVING);
		
		return returnStatus;
	}
	
	public void setFinanceMainService(FinanceMainService financeMainService) {
		this.financeMainService = financeMainService;
	}
	
	public void setTransactionCodeService(TransactionCodeService transactionCodeService) {
		this.transactionCodeService = transactionCodeService;
	}

	public void setAccountMappingService(AccountMappingService accountMappingService) {
		this.accountMappingService = accountMappingService;
	}
	
	public void setjVPostingService(JVPostingService jVPostingService) {
		this.jVPostingService = jVPostingService;
	}
	
}
