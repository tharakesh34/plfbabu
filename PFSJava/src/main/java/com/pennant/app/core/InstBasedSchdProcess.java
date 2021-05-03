package com.pennant.app.core;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.FactoryConfigurationError;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.batchProcessStatus.BatchProcessStatusDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.InstBasedSchdDetails;
import com.pennant.backend.model.finance.RepayInstruction;
import com.pennant.backend.model.finance.covenant.Covenant;
import com.pennant.backend.model.finance.covenant.CovenantDocument;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.engine.workflow.WorkflowEngine;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.logging.dao.InstBasedSchdDetailDAO;

public class InstBasedSchdProcess extends GenericService<InstBasedSchdDetails> {
	private static Logger logger = LogManager.getLogger(InstBasedSchdProcess.class);

	private transient FinanceDetailService financeDetailService;
	private transient InstBasedSchdDetailDAO instBasedSchdDetailDAO;
	private transient BatchProcessStatusDAO batchProcessStatusDAO;
	private PlatformTransactionManager transactionManager;

	protected Map<String, List<ErrorDetail>> overideMap = new HashMap<String, List<ErrorDetail>>();
	protected transient WorkflowEngine workFlow = null;

	/**
	 * Method to Check Any Records Are There For Inst Based Schedule.
	 */

	public void rebuildSchdBasedOnInst(LoggedInUser userId, long batchId) {
		logger.debug(Literal.ENTERING);

		List<InstBasedSchdDetails> instBasedSchddetailList = instBasedSchdDetailDAO
				.getUploadedDisbursementsWithBatchId(batchId);

		if (CollectionUtils.isEmpty(instBasedSchddetailList)) {
			return;
		}

		batchProcessStatusDAO.saveBatchStatus("IBS", new Timestamp(System.currentTimeMillis()), "I");

		FinanceDetail financeDetail = null;
		for (InstBasedSchdDetails instBasedSchdDetail : instBasedSchddetailList) {

			//Check in main and temp rather than instruction
			String nxtRoleCd = financeDetailService.getNextRoleCodeByRef(instBasedSchdDetail.getFinReference(),
					"_temp");
			boolean isLoanApproved = instBasedSchdDetailDAO.getFinanceIfApproved(instBasedSchdDetail.getFinReference());

			// Check if Loan is not Approved
			if (!isLoanApproved) {
				financeDetail = financeDetailService.getOriginationFinance(instBasedSchdDetail.getFinReference(),
						nxtRoleCd, FinanceConstants.FINSER_EVENT_ORG, "");
				financeDetail.setModuleDefiner(FinanceConstants.FINSER_EVENT_ORG);
			} else if (StringUtils.isNotBlank(nxtRoleCd)) {
				financeDetail = financeDetailService.getServicingFinanceForQDP(instBasedSchdDetail.getFinReference(),
						AccountEventConstants.ACCEVENT_ADDDBSN, FinanceConstants.FINSER_EVENT_ADDDISB, nxtRoleCd);
			} else {
				financeDetail = financeDetailService.getFinSchdDetailByRef(instBasedSchdDetail.getFinReference(), "",
						false);
			}

			if (financeDetail != null) {
				processAndApprove(userId, instBasedSchdDetail, financeDetail, !isLoanApproved, nxtRoleCd);
			}
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * This Method Process and Approves the Finance which are in For Inst Schd. Prepares new Schedule based on the
	 * Realization Date and Validate and Approves the Record.
	 */

	private void processAndApprove(LoggedInUser userDetails, InstBasedSchdDetails instSchdDetail,
			FinanceDetail financeDetail, boolean isLoanNotApproved, String nxtRoleCd) {

		DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
		txDef.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		TransactionStatus txStatus = null;

		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		FinanceDisbursement finDisb = null;
		FinScheduleData instBasedSchedule = null;

		// from payment id we need to get disbursement id
		//in disbursement details we need to mark  instCalReq ="false";
		for (FinanceDisbursement financeDisbursement : financeDetail.getFinScheduleData().getDisbursementDetails()) {
			for (FinAdvancePayments finAdvancePayments : financeDetail.getAdvancePaymentsList()) {

				// Check if payment id match
				if (instSchdDetail.getDisbId() == finAdvancePayments.getPaymentId()
						&& financeDisbursement.getDisbSeq() == finAdvancePayments.getDisbSeq()) {
					financeDisbursement.setInstCalReq(false);
					finDisb = financeDisbursement;
					break;
				}
			}
		}

		//Step Changes
		String valueAsString = SysParamUtil.getValueAsString("STEP_LOAN_SERVICING_REQ");
		financeMain.setRecalType(CalculationConstants.RPYCHG_TILLMDT);

		if (StringUtils.equalsIgnoreCase(valueAsString, PennantConstants.YES) && financeMain.isStepFinance()) {
			if (StringUtils.isNotBlank(financeMain.getStepPolicy())
					|| (financeMain.isAlwManualSteps() && financeMain.getNoOfSteps() > 0)) {

				financeDetail.getFinScheduleData().setStepPolicyDetails(financeDetailService
						.getFinStepDetailListByFinRef(financeMain.getFinReference(), "_view", false));

				Date recalFromDate = null;

				List<RepayInstruction> rpst = financeDetail.getFinScheduleData().getRepayInstructions();

				if (CollectionUtils.isEmpty(rpst)) {
					financeDetail.getFinScheduleData().setRepayInstructions(
							financeDetailService.getRepayInstructions(financeMain.getFinReference(), "_view", false));
					rpst = financeDetail.getFinScheduleData().getRepayInstructions();
				}

				if (instSchdDetail.getRealizedDate().compareTo(financeMain.getGrcPeriodEndDate()) > 0) {
					RepayInstruction rins = rpst.get(rpst.size() - 1);
					recalFromDate = rins.getRepayDate();
					financeMain.setRecalSteps(false);
				} else {
					financeMain.setRecalSteps(true);
					for (RepayInstruction repayInstruction : rpst) {
						if (repayInstruction.getRepayDate().compareTo(financeMain.getGrcPeriodEndDate()) > 0) {
							recalFromDate = repayInstruction.getRepayDate();
							break;
						}
					}
				}

				financeMain.setRecalFromDate(recalFromDate);
				financeMain.setRecalToDate(financeMain.getMaturityDate());
				financeMain.setRecalType(CalculationConstants.RPYCHG_STEPINST);
				if (CollectionUtils.isNotEmpty(financeDetail.getFinScheduleData().getFinServiceInstructions())) {
					for (FinServiceInstruction finServiceInstruction : financeDetail.getFinScheduleData()
							.getFinServiceInstructions()) {
						finServiceInstruction.setRecalFromDate(recalFromDate);
						finServiceInstruction.setRecalToDate(financeMain.getMaturityDate());
						finServiceInstruction.setRecalType(CalculationConstants.RPYCHG_STEPINST);
						finServiceInstruction.setSchdMethod(financeMain.getScheduleMethod());
					}
				}
			}

		}

		try {

			txStatus = transactionManager.getTransaction(txDef);

			// if Loan not approved, then
			if (isLoanNotApproved) {

				financeMain.setEventFromDate(instSchdDetail.getRealizedDate());
				financeMain.setRecalToDate(financeMain.getMaturityDate());
				//financeMain.setRecalType(CalculationConstants.RPYCHG_TILLMDT);

				instBasedSchedule = ScheduleCalculator.instBasedSchedule(financeDetail.getFinScheduleData(),
						instSchdDetail.getDisbAmount(), false, isLoanNotApproved, finDisb, true);

				financeDetail.setFinScheduleData(instBasedSchedule);
				financeDetail.getFinScheduleData().setSchduleGenerated(true);

				financeMain.setUserDetails(userDetails);
				financeMain.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				financeDetail.getFinScheduleData().setFinanceMain(financeMain);

				doProcess(financeDetail, userDetails);

			} else {

				if (StringUtils.isBlank(nxtRoleCd)) {

					financeMain.setEventFromDate(instSchdDetail.getRealizedDate());
					financeMain.setRecalToDate(financeMain.getMaturityDate());

					instBasedSchedule = ScheduleCalculator.instBasedSchedule(financeDetail.getFinScheduleData(),
							instSchdDetail.getDisbAmount(), false, isLoanNotApproved, finDisb, false);

					financeDetail.setFinScheduleData(instBasedSchedule);
					financeDetail.getFinScheduleData().setSchduleGenerated(true);

					financeMain.setUserDetails(userDetails);
					financeMain.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
					financeDetail.getFinScheduleData().setFinanceMain(financeMain);

					doProcessDisbRecord(financeDetail.getFinScheduleData(), userDetails);

				} else {

					if (CollectionUtils.isNotEmpty(financeDetail.getFinScheduleData().getFinServiceInstructions())) {
						for (FinServiceInstruction finServiceInstruction : financeDetail.getFinScheduleData()
								.getFinServiceInstructions()) {

							financeMain.setEventFromDate(instSchdDetail.getRealizedDate());
							financeMain.setRecalToDate(finServiceInstruction.getRecalToDate());
							financeMain.setScheduleMethod(finServiceInstruction.getSchdMethod());
							financeMain.setRecalType(finServiceInstruction.getRecalType());

							if (instSchdDetail.getRealizedDate().compareTo(finDisb.getDisbDate()) == 0) {
								List<FinanceScheduleDetail> listSchdDetails = getFinanceDetailService()
										.getFinScheduleDetails(financeMain.getFinReference(), "_temp", false);
								if (CollectionUtils.isNotEmpty(listSchdDetails)) {
									financeDetail.getFinScheduleData().setFinanceScheduleDetails(listSchdDetails);
								}
							}
							instBasedSchedule = ScheduleCalculator.instBasedSchedule(financeDetail.getFinScheduleData(),
									finServiceInstruction.getAmount(), false, isLoanNotApproved, finDisb, true);
							financeDetail.setFinScheduleData(instBasedSchedule);
							financeDetail.getFinScheduleData().setSchduleGenerated(true);

							financeMain.setUserDetails(userDetails);
							financeMain.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
							financeDetail.getFinScheduleData().setFinanceMain(financeMain);

							// save/approve
							doProcess(financeDetail, userDetails);
						}
					}
				}
			}
		} catch (Exception e) {
			transactionManager.rollback(txStatus);
			instSchdDetail.setErrorDesc(StringUtils.substring(e.toString(), 0, 900));
			logger.info(e.toString());
			logger.debug(Literal.EXCEPTION, e);
			instSchdDetail.setStatus(DisbursementConstants.AUTODISB_STATUS_FAILED);

			DefaultTransactionDefinition txDefinition = new DefaultTransactionDefinition();
			txDefinition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

			TransactionStatus txSts = null;
			instBasedSchdDetailDAO.updateFinAutoApprovals(instSchdDetail);

			txSts = transactionManager.getTransaction(txDefinition);
			transactionManager.commit(txSts);
		}

		if (DisbursementConstants.AUTODISB_STATUS_FAILED != instSchdDetail.getStatus()) {
			instSchdDetail.setStatus(DisbursementConstants.AUTODISB_STATUS_SUCCESS);
			instBasedSchdDetailDAO.updateFinAutoApprovals(instSchdDetail);
			if (txStatus != null) {
				transactionManager.commit(txStatus);
			}
		}

		batchProcessStatusDAO.updateBatchStatus("IBS", new Timestamp(System.currentTimeMillis()), "C");

	}

	private void doProcessDisbRecord(FinScheduleData finDetail, LoggedInUser userDetails) {

		HashMap<Date, Integer> mapDateSeq = new HashMap<Date, Integer>();
		// Finance Schedule Details
		for (int i = 0; i < finDetail.getFinanceScheduleDetails().size(); i++) {
			int seqNo = 0;

			if (mapDateSeq.containsKey(finDetail.getFinanceScheduleDetails().get(i).getSchDate())) {
				seqNo = mapDateSeq.get(finDetail.getFinanceScheduleDetails().get(i).getSchDate());
				mapDateSeq.remove(finDetail.getFinanceScheduleDetails().get(i).getSchDate());
			}
			seqNo = seqNo + 1;
			mapDateSeq.put(finDetail.getFinanceScheduleDetails().get(i).getSchDate(), seqNo);
			finDetail.getFinanceScheduleDetails().get(i).setSchSeq(seqNo);
			finDetail.getFinanceScheduleDetails().get(i).setLogKey(0);
		}

		financeDetailService.saveFinSchdDetail(finDetail.getFinanceScheduleDetails(),
				finDetail.getFinanceMain().getFinReference());

		// Finance Disbursement Details
		mapDateSeq = new HashMap<Date, Integer>();
		Date curBDay = SysParamUtil.getAppDate();

		for (FinanceDisbursement disbursement : finDetail.getDisbursementDetails()) {
			disbursement.setFinReference(finDetail.getFinReference());
			disbursement.setDisbReqDate(curBDay);
			disbursement.setDisbIsActive(true);
			disbursement.setDisbDisbursed(true);
			disbursement.setLogKey(0);
			//LastMnton  and LastMnt By
			disbursement.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			disbursement.setLastMntBy(userDetails.getUserId());

			if (disbursement.getInstructionUID() == Long.MIN_VALUE) {
				disbursement.setInstructionUID(0);
			}
		}
		financeDetailService.saveDisbDetails(finDetail.getDisbursementDetails(),
				finDetail.getFinanceMain().getFinReference());
	}

	/**
	 * Method for Processing Finance Detail Object for Database Operation
	 * 
	 * @param afinanceMain
	 * @param tranType
	 * @param userDetails
	 */
	private AuditHeader doProcess(FinanceDetail financeDetail, LoggedInUser userDetails) throws Exception {
		AuditHeader auditHeader = null;
		FinanceMain afinanceMain = financeDetail.getFinScheduleData().getFinanceMain();

		afinanceMain.setLastMntBy(userDetails.getUserId());
		afinanceMain.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		afinanceMain.setAutoApprove(true);

		afinanceMain.setUserDetails(userDetails);
		financeDetail.setUserDetails(userDetails);
		financeDetail.getCustomerDetails().setUserDetails(userDetails);
		//remove covenant documents from  DocumentdetailsList the same documents are available in covenant doc list(Bugfix: PrimaryKey issue)
		removeCovenantDocuments(financeDetail);
		auditHeader = getAuditHeader(financeDetail, PennantConstants.TRAN_WF);
		auditHeader = financeDetailService.doApprove(auditHeader, false);

		if (auditHeader.getOverideMessage() != null) {
			for (int i = 0; i < auditHeader.getOverideMessage().size(); i++) {
				ErrorDetail overideDetail = auditHeader.getOverideMessage().get(i);
				if (!isOverride(overideMap, overideDetail)) {
					setOverideMap(overideMap, overideDetail);
				}
			}

			auditHeader.setOverideMap(overideMap);
			setOverideMap(overideMap);
			auditHeader = getAuditHeader(financeDetail, PennantConstants.TRAN_WF);
			auditHeader = financeDetailService.doApprove(auditHeader, false);
		}

		return auditHeader;

	}

	private Map<String, List<ErrorDetail>> setOverideMap(Map<String, List<ErrorDetail>> overideMap,
			ErrorDetail errorDetail) {

		if (StringUtils.isNotBlank(errorDetail.getField())) {

			List<ErrorDetail> errorDetails = null;
			if (overideMap.containsKey(errorDetail.getField())) {
				errorDetails = overideMap.get(errorDetail.getField());

				for (int i = 0; i < errorDetails.size(); i++) {
					if (errorDetails.get(i).getCode().equals(errorDetail.getCode())) {
						errorDetails.remove(i);
						break;
					}
				}

				overideMap.remove(errorDetail.getField());

			} else {
				errorDetails = new ArrayList<ErrorDetail>();

			}

			errorDetail.setOveride(true);
			errorDetails.add(errorDetail);

			overideMap.put(errorDetail.getField(), errorDetails);

		}
		return overideMap;
	}

	private boolean isOverride(Map<String, List<ErrorDetail>> overideMap, ErrorDetail errorDetail) {

		if (overideMap.containsKey(errorDetail.getField())) {
			List<ErrorDetail> errorDetails = overideMap.get(errorDetail.getField());
			for (int i = 0; i < errorDetails.size(); i++) {

				if (errorDetails.get(i).getCode().equals(errorDetail.getCode())) {
					return errorDetails.get(i).isOveride();
				}
			}
		}

		return false;
	}

	public void doLoadWorkFlow(boolean workFlowEnabled, long workFlowId, String nextTaskID)
			throws FactoryConfigurationError {
		if (workFlowEnabled) {
			setWorkFlow(new WorkflowEngine(WorkFlowUtil.getWorkflow(workFlowId).getWorkFlowXml()));
		}
	}

	private AuditHeader getAuditHeader(FinanceDetail afinanceDetail, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, afinanceDetail.getBefImage(), afinanceDetail);
		return new AuditHeader(afinanceDetail.getFinScheduleData().getFinReference(), null, null, null, auditDetail,
				afinanceDetail.getUserDetails(), getOverideMap());
	}

	private void removeCovenantDocuments(FinanceDetail financeDetail) {
		List<Long> documents = new ArrayList<>();
		List<Covenant> covenants = financeDetail.getCovenants();
		if (CollectionUtils.isNotEmpty(covenants)) {
			for (Covenant covenant : covenants) {//if covenants tab is not available in loan queue below list is getting empty
				if (CollectionUtils.isNotEmpty(covenant.getDocumentDetails())) {
					for (DocumentDetails document : covenant.getDocumentDetails()) {
						documents.add(document.getDocId());
					}
				} else if (CollectionUtils.isNotEmpty(covenant.getCovenantDocuments())) {//we are preparing document list by using covenants doc
					for (CovenantDocument covenantDocument : covenant.getCovenantDocuments()) {
						if (covenantDocument.getDocumentDetail() != null) {
							documents.add(covenantDocument.getDocumentDetail().getDocId());
						}
					}
				}
			}

		}
		List<DocumentDetails> documentDetails = financeDetail.getDocumentDetailsList();
		if (!CollectionUtils.isEmpty(documentDetails)) {
			//remove covenant documents in loan document list
			for (int i = 0; i < documentDetails.size(); i++) {
				DocumentDetails documentDetail = documentDetails.get(i);
				if (documents.contains(documentDetail.getDocId())) {
					documentDetails.remove(i);
				}
			}
		}
		financeDetail.setDocumentDetailsList(documentDetails);
	}

	public WorkflowEngine getWorkFlow() {
		return workFlow;
	}

	public void setWorkFlow(WorkflowEngine workFlow) {
		this.workFlow = workFlow;
	}

	public Map<String, List<ErrorDetail>> getOverideMap() {
		return overideMap;
	}

	public void setOverideMap(Map<String, List<ErrorDetail>> overideMap) {
		this.overideMap = overideMap;
	}

	public PlatformTransactionManager getTransactionManager() {
		return transactionManager;
	}

	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	public FinanceDetailService getFinanceDetailService() {
		return financeDetailService;
	}

	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	public InstBasedSchdDetailDAO getInstBasedSchdDetailDAO() {
		return instBasedSchdDetailDAO;
	}

	public void setInstBasedSchdDetailDAO(InstBasedSchdDetailDAO instBasedSchdDetailDAO) {
		this.instBasedSchdDetailDAO = instBasedSchdDetailDAO;
	}

	public BatchProcessStatusDAO getBatchProcessStatusDAO() {
		return batchProcessStatusDAO;
	}

	public void setBatchProcessStatusDAO(BatchProcessStatusDAO batchProcessStatusDAO) {
		this.batchProcessStatusDAO = batchProcessStatusDAO;
	}

}
