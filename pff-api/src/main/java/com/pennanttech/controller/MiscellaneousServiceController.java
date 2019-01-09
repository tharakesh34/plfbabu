package com.pennanttech.controller;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.apache.log4j.Logger;
import org.springframework.util.CollectionUtils;

import com.pennant.app.util.APIHeader;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SessionUserDetails;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.applicationmaster.AccountMapping;
import com.pennant.backend.model.applicationmaster.TransactionCode;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.dashboard.DashboardConfiguration;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.others.JVPosting;
import com.pennant.backend.model.others.JVPostingEntry;
import com.pennant.backend.service.administration.SecurityUserService;
import com.pennant.backend.service.applicationmaster.AccountMappingService;
import com.pennant.backend.service.applicationmaster.TransactionCodeService;
import com.pennant.backend.service.dashboard.DashboardConfigurationService;
import com.pennant.backend.service.finance.FinanceMainService;
import com.pennant.backend.service.others.JVPostingService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.fusioncharts.ChartSetElement;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.util.APIConstants;
import com.pennanttech.ws.model.dashboard.DashBoardRequest;
import com.pennanttech.ws.model.dashboard.DashBoardResponse;
import com.pennanttech.ws.service.APIErrorHandlerService;

public class MiscellaneousServiceController {
	
	private final Logger logger = Logger.getLogger(getClass());
	
	private FinanceMainService financeMainService;
	private TransactionCodeService transactionCodeService;
	private AccountMappingService accountMappingService;
	private JVPostingService jVPostingService;
	
	private DashboardConfigurationService dashboardConfigurationService;
	private SecurityUserService securityUserService;
	
	private List<ErrorDetail> doDashboardValidations(DashBoardRequest request, boolean usernameProvided) {

		logger.debug(Literal.ENTERING);

		List<ErrorDetail> errorsList = new ArrayList<>();
		if (usernameProvided) {

			if (StringUtils.isBlank(request.getCode())) {
				String[] param = new String[1];
				param[0] = "code";
				errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("30561", param)));

				return errorsList;
			}

			DashboardConfiguration configuration = dashboardConfigurationService
					.getApprovedDashboardDetailById(request.getCode());
			if (null == configuration) {
				String[] param = new String[1];
				param[0] = "dashboardcode: " + request.getCode();
				errorsList.add(new ErrorDetail("90266", param));

				return errorsList;
			}

			long userID = securityUserService.getSecuredUserDetails(request.getUserLogin());
			if (userID <= 0) {
				String[] param = new String[1];
				param[0] = "userLogin: " + request.getUserLogin();
				errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("90266", param)));

				return errorsList;
			}
		} else {
			if (StringUtils.isBlank(request.getCode())) {
				String[] param = new String[1];
				param[0] = "code";
				errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("30561", param)));

				return errorsList;
			}

			DashboardConfiguration configuration = dashboardConfigurationService
					.getApprovedDashboardDetailById(request.getCode());
			if (null == configuration) {
				String[] param = new String[1];
				param[0] = "dashboardcode: " + request.getCode();
				errorsList.add(new ErrorDetail("90266", param));

				return errorsList;
			}
		}
		logger.debug(Literal.LEAVING);

		return errorsList;
	}

	public DashBoardResponse prepareDashboardConfiguration(DashBoardRequest request) {
		
		logger.debug(Literal.ENTERING);

		DashBoardResponse response = null;
		DashboardConfiguration config = null;
		
		if(StringUtils.isBlank(request.getUserLogin()))	{
			response = new DashBoardResponse();
			List<ErrorDetail> validationErrors = doDashboardValidations(request, false);
			if (CollectionUtils.isEmpty(validationErrors)) {
				LoggedInUser loggedUser = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
				request.setUserLogin(loggedUser.getUserName());

				config = dashboardConfigurationService.getApprovedDashboardDetailById(request.getCode());
				if (config != null) {
					List<ChartSetElement> chartList = dashboardConfigurationService.getDashboardLabelAndValues(config);
					response.setChartSetElement(chartList);
					response.setDashboardConfiguration(config);
					response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
				} else {
					String[] valueParm = new String[1];
					valueParm[0] = request.getCode();
					response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90501", valueParm));
				}
			} else {
				for (ErrorDetail errorDetail : validationErrors) {
					response.setReturnStatus(
							APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getParameters()));
				}
			}
		}
		else	{
			// user login available in request
			response = new DashBoardResponse();
			List<ErrorDetail> validationErrors = doDashboardValidations(request, true);
			if (CollectionUtils.isEmpty(validationErrors)) {
				config = dashboardConfigurationService.getApprovedDashboardDetailById(request.getCode());
				if (config != null) {
					List<ChartSetElement> charSetElementsList = dashboardConfigurationService
							.getDashboardLabelAndValues(config);
					if (!CollectionUtils.isEmpty(charSetElementsList)) {
						response.setChartSetElement(charSetElementsList);
						response.setDashboardConfiguration(config);
						response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
					} else {
						String[] valueParm = new String[1];
						valueParm[0] = request.getCode();
						response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90501", valueParm));
					}
				}
			} else {
				for (ErrorDetail errorDetail : validationErrors) {
					response.setReturnStatus(
							APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getParameters()));
				}
			}
		}
		logger.debug(Literal.LEAVING);
		
		return response;
	}
	
	private void setJVPostingEntryMandatoryFieldsData(JVPostingEntry postingEntry)	{
		
		logger.debug(Literal.ENTERING);
		
		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		postingEntry.setUserDetails(userDetails);
		postingEntry.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		postingEntry.setLastMntBy(userDetails.getUserId());
		postingEntry.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		postingEntry.setFinSourceID(APIConstants.FINSOURCE_ID_API);
		
		logger.debug(Literal.LEAVING);
	}
		
	private void setJVPostingMandatoryFieldsData(JVPosting posting)	{
		
		logger.debug(Literal.ENTERING);
		
		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		posting.setUserDetails(userDetails);
		posting.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		posting.setLastMntBy(userDetails.getUserId());
		posting.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		posting.setFinSourceID(APIConstants.FINSOURCE_ID_API);
		
		logger.debug(Literal.LEAVING);
	}
	
	public WSReturnStatus prepareJVPostData(JVPosting jvPosting)	{
		
		logger.debug(Literal.ENTERING);
		
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
		
		logger.debug(Literal.LEAVING);
		
		return returnStatus;
	}
	
	private AuditHeader prepareAuditHeader(JVPosting aJVPosting, String tranType) {
		
		logger.debug(Literal.ENTERING);
		
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aJVPosting.getBefImage(), aJVPosting);
		AuditHeader auditHeader = new AuditHeader(aJVPosting.getReference(), Long.toString(aJVPosting.getBatchReference()), null, null, auditDetail, aJVPosting.getUserDetails(), new HashMap<String, ArrayList<ErrorDetail>>());
		
		APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange().get(APIHeader.API_HEADER_KEY);
		auditHeader.setApiHeader(reqHeaderDetails);
		
		logger.debug(Literal.LEAVING);
		
		return auditHeader;
	}
	
	private WSReturnStatus saveJVPostingData(final JVPosting postReadyData)	{
		
		logger.debug(Literal.ENTERING);
		
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
		
		logger.debug(Literal.LEAVING);
		
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
	
	public void setDashboardConfigurationService(DashboardConfigurationService dashboardConfigurationService) {
		this.dashboardConfigurationService = dashboardConfigurationService;
	}

	public void setSecurityUserService(SecurityUserService securityUserService) {
		this.securityUserService = securityUserService;
	}

}
