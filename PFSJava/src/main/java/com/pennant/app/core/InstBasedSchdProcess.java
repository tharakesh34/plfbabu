package com.pennant.app.core;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.app.util.SysParamUtil;
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
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.logging.dao.InstBasedSchdDetailDAO;

public class InstBasedSchdProcess extends GenericService<InstBasedSchdDetails> {
	private static Logger logger = LogManager.getLogger(InstBasedSchdProcess.class);

	private FinanceDetailService financeDetailService;
	private InstBasedSchdDetailDAO instBasedSchdDetailDAO;

	/**
	 * Method to Check Any Records Are There For Inst Based Schedule.
	 */

	public void process(InstBasedSchdDetails instBasedSchd) {
		logger.debug(Literal.ENTERING);

		LoggedInUser user = new LoggedInUser();
		user.setLoginUsrID(instBasedSchd.getUserId());
		FinanceDetail fd = null;

		//Check in main and temp rather than instruction
		String finReference = instBasedSchd.getFinReference();
		String nxtRoleCd = financeDetailService.getNextRoleCodeByRef(finReference);
		boolean isLoanApproved = instBasedSchdDetailDAO.getFinanceIfApproved(finReference);

		// Check if Loan is not Approveda
		if (!isLoanApproved) {
			fd = financeDetailService.getOriginationFinance(finReference, nxtRoleCd, FinanceConstants.FINSER_EVENT_ORG,
					"");
			fd.setModuleDefiner(FinanceConstants.FINSER_EVENT_ORG);
		} else if (StringUtils.isNotBlank(nxtRoleCd)) {
			fd = financeDetailService.getServicingFinanceForQDP(finReference, AccountEventConstants.ACCEVENT_ADDDBSN,
					FinanceConstants.FINSER_EVENT_ADDDISB, nxtRoleCd);
		} else {
			fd = financeDetailService.getFinSchdDetailByRef(finReference, "", false);
		}

		if (fd != null) {
			processAndApprove(user, instBasedSchd, fd, !isLoanApproved, nxtRoleCd);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * This Method Process and Approves the Finance which are in For Inst Schd. Prepares new Schedule based on the
	 * Realization Date and Validate and Approves the Record.
	 */

	private void processAndApprove(LoggedInUser userDetails, InstBasedSchdDetails instSchdDetail, FinanceDetail fd,
			boolean isLoanNotApproved, String nxtRoleCd) {
		logger.debug(Literal.ENTERING);

		FinanceMain fm = fd.getFinScheduleData().getFinanceMain();
		FinanceDisbursement finDisb = null;
		FinScheduleData instBasedSchedule = null;

		// from payment id we need to get disbursement id
		//in disbursement details we need to mark  instCalReq ="false";
		for (FinanceDisbursement financeDisbursement : fd.getFinScheduleData().getDisbursementDetails()) {
			for (FinAdvancePayments finAdvancePayments : fd.getAdvancePaymentsList()) {

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
		fm.setRecalType(CalculationConstants.RPYCHG_TILLMDT);

		if (StringUtils.equalsIgnoreCase(valueAsString, PennantConstants.YES) && fm.isStepFinance()) {
			if (StringUtils.isNotBlank(fm.getStepPolicy()) || (fm.isAlwManualSteps() && fm.getNoOfSteps() > 0)) {
				setStepFinanceDetails(instSchdDetail, fd, fm);
			}
		}

		try {

			// if Loan not approved, then
			if (isLoanNotApproved) {

				fm.setEventFromDate(instSchdDetail.getRealizedDate());
				fm.setRecalToDate(fm.getMaturityDate());
				//financeMain.setRecalType(CalculationConstants.RPYCHG_TILLMDT);

				instBasedSchedule = ScheduleCalculator.instBasedSchedule(fd.getFinScheduleData(),
						instSchdDetail.getDisbAmount(), false, isLoanNotApproved, finDisb, true);

				fd.setFinScheduleData(instBasedSchedule);
				fd.getFinScheduleData().setSchduleGenerated(true);

				fm.setUserDetails(userDetails);
				fm.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				fd.getFinScheduleData().setFinanceMain(fm);

				doProcess(fd, userDetails);

			} else {

				if (StringUtils.isBlank(nxtRoleCd)) {

					fm.setEventFromDate(instSchdDetail.getRealizedDate());
					fm.setRecalToDate(fm.getMaturityDate());

					instBasedSchedule = ScheduleCalculator.instBasedSchedule(fd.getFinScheduleData(),
							instSchdDetail.getDisbAmount(), false, isLoanNotApproved, finDisb, false);

					fd.setFinScheduleData(instBasedSchedule);
					fd.getFinScheduleData().setSchduleGenerated(true);

					fm.setUserDetails(userDetails);
					fm.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
					fd.getFinScheduleData().setFinanceMain(fm);

					doProcessDisbRecord(fd.getFinScheduleData(), userDetails);

				} else {

					if (CollectionUtils.isNotEmpty(fd.getFinScheduleData().getFinServiceInstructions())) {
						for (FinServiceInstruction finServiceInstruction : fd.getFinScheduleData()
								.getFinServiceInstructions()) {

							fm.setEventFromDate(instSchdDetail.getRealizedDate());
							fm.setRecalToDate(finServiceInstruction.getRecalToDate());
							fm.setScheduleMethod(finServiceInstruction.getSchdMethod());
							fm.setRecalType(finServiceInstruction.getRecalType());

							if (instSchdDetail.getRealizedDate().compareTo(finDisb.getDisbDate()) == 0) {
								List<FinanceScheduleDetail> listSchdDetails = getFinanceDetailService()
										.getFinScheduleDetails(fm.getFinReference(), "_temp", false);
								if (CollectionUtils.isNotEmpty(listSchdDetails)) {
									fd.getFinScheduleData().setFinanceScheduleDetails(listSchdDetails);
								}
							}
							instBasedSchedule = ScheduleCalculator.instBasedSchedule(fd.getFinScheduleData(),
									finServiceInstruction.getAmount(), false, isLoanNotApproved, finDisb, true);
							fd.setFinScheduleData(instBasedSchedule);
							fd.getFinScheduleData().setSchduleGenerated(true);

							fm.setUserDetails(userDetails);
							fm.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
							fd.getFinScheduleData().setFinanceMain(fm);

							// save/approve
							doProcess(fd, userDetails);
						}
					}
				}
			}
		} catch (Exception e) {
			instSchdDetail.setErrorDesc(StringUtils.substring(e.toString(), 0, 900));
			logger.info(e.toString());
			logger.debug(Literal.EXCEPTION, e);
			instSchdDetail.setStatus(DisbursementConstants.AUTODISB_STATUS_FAILED);

		}

		if (DisbursementConstants.AUTODISB_STATUS_FAILED != instSchdDetail.getStatus()) {
			instSchdDetail.setStatus(DisbursementConstants.AUTODISB_STATUS_SUCCESS);
		}

		logger.debug(Literal.LEAVING);

	}

	private void setStepFinanceDetails(InstBasedSchdDetails instSchdDetail, FinanceDetail fd, FinanceMain fm) {
		FinScheduleData schdData = fd.getFinScheduleData();
		String finReference = fm.getFinReference();
		schdData.setStepPolicyDetails(financeDetailService.getFinStepDetailListByFinRef(finReference, "_view", false));

		Date recalFromDate = null;

		List<RepayInstruction> rpst = schdData.getRepayInstructions();

		if (CollectionUtils.isEmpty(rpst)) {
			schdData.setRepayInstructions(financeDetailService.getRepayInstructions(finReference, "_view", false));
			rpst = schdData.getRepayInstructions();
		}

		if (instSchdDetail.getRealizedDate().compareTo(fm.getGrcPeriodEndDate()) > 0) {
			RepayInstruction rins = rpst.get(rpst.size() - 1);
			recalFromDate = rins.getRepayDate();
			fm.setRecalSteps(false);
		} else {
			fm.setRecalSteps(true);
			for (RepayInstruction repayInstruction : rpst) {
				if (repayInstruction.getRepayDate().compareTo(fm.getGrcPeriodEndDate()) > 0) {
					recalFromDate = repayInstruction.getRepayDate();
					break;
				}
			}
		}

		fm.setRecalFromDate(recalFromDate);
		fm.setRecalToDate(fm.getMaturityDate());
		fm.setRecalType(CalculationConstants.RPYCHG_STEPINST);

		if (CollectionUtils.isNotEmpty(schdData.getFinServiceInstructions())) {
			for (FinServiceInstruction fsi : schdData.getFinServiceInstructions()) {
				fsi.setRecalFromDate(recalFromDate);
				fsi.setRecalToDate(fm.getMaturityDate());
				fsi.setRecalType(CalculationConstants.RPYCHG_STEPINST);
				fsi.setSchdMethod(fm.getScheduleMethod());
			}
		}
	}

	private void doProcessDisbRecord(FinScheduleData finDetail, LoggedInUser userDetails) {
		Map<Date, Integer> mapDateSeq = new HashMap<Date, Integer>();
		// Finance Schedule Details
		List<FinanceScheduleDetail> schedules = finDetail.getFinanceScheduleDetails();
		for (FinanceScheduleDetail schd : schedules) {
			int seqNo = 0;

			if (mapDateSeq.containsKey(schd.getSchDate())) {
				seqNo = mapDateSeq.get(schd.getSchDate());
				mapDateSeq.remove(schd.getSchDate());
			}
			seqNo = seqNo + 1;
			mapDateSeq.put(schd.getSchDate(), seqNo);
			schd.setSchSeq(seqNo);
			schd.setLogKey(0);
		}

		FinanceMain fm = finDetail.getFinanceMain();
		String finReference = fm.getFinReference();
		financeDetailService.saveFinSchdDetail(schedules, finReference);

		// Finance Disbursement Details
		mapDateSeq = new HashMap<Date, Integer>();
		Date curBDay = SysParamUtil.getAppDate();

		for (FinanceDisbursement disbursement : finDetail.getDisbursementDetails()) {
			disbursement.setFinReference(finDetail.getFinReference());
			disbursement.setDisbReqDate(curBDay);
			disbursement.setDisbIsActive(true);
			disbursement.setLogKey(0);
			disbursement.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			disbursement.setLastMntBy(userDetails.getUserId());

			if (disbursement.getInstructionUID() == Long.MIN_VALUE) {
				disbursement.setInstructionUID(0);
			}
		}

		financeDetailService.saveDisbDetails(finDetail.getDisbursementDetails(), finReference);
	}

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

		Map<String, List<ErrorDetail>> overideMap = new HashMap<String, List<ErrorDetail>>();

		auditHeader = getAuditHeader(financeDetail, PennantConstants.TRAN_WF, overideMap);
		auditHeader = financeDetailService.doApprove(auditHeader, false);

		if (auditHeader.getOverideMessage() != null) {
			for (int i = 0; i < auditHeader.getOverideMessage().size(); i++) {
				ErrorDetail overideDetail = auditHeader.getOverideMessage().get(i);
				if (!isOverride(overideMap, overideDetail)) {
					setOverideMap(overideMap, overideDetail);
				}
			}

			auditHeader.setOverideMap(overideMap);
			auditHeader = getAuditHeader(financeDetail, PennantConstants.TRAN_WF, overideMap);
			auditHeader = financeDetailService.doApprove(auditHeader, false);
		}

		return auditHeader;

	}

	private Map<String, List<ErrorDetail>> setOverideMap(Map<String, List<ErrorDetail>> overideMap,
			ErrorDetail errorDetail) {

		if (StringUtils.isBlank(errorDetail.getField())) {
			return overideMap;
		}

		List<ErrorDetail> errorDetails = null;

		if (!overideMap.containsKey(errorDetail.getField())) {
			errorDetails = new ArrayList<>();
			overideMap.put(errorDetail.getField(), errorDetails);
		}

		errorDetails = overideMap.get(errorDetail.getField());

		for (int i = 0; i < errorDetails.size(); i++) {
			if (errorDetails.get(i).getCode().equals(errorDetail.getCode())) {
				errorDetails.remove(i);
				break;
			}
		}

		overideMap.remove(errorDetail.getField());

		errorDetail.setOveride(true);
		errorDetails.add(errorDetail);

		overideMap.put(errorDetail.getField(), errorDetails);

		return overideMap;
	}
	
	private boolean isOverride(Map<String, List<ErrorDetail>> overideMap, ErrorDetail errorDetail) {
		if (!overideMap.containsKey(errorDetail.getField())) {
			return false;
		}

		List<ErrorDetail> errorDetails = overideMap.get(errorDetail.getField());
		for (ErrorDetail ed : errorDetails) {
			if (ed.getCode().equals(errorDetail.getCode())) {
				return ed.isOveride();
			}
		}

		return false;
	}

	private AuditHeader getAuditHeader(FinanceDetail afinanceDetail, String tranType,
			Map<String, List<ErrorDetail>> overideMap) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, afinanceDetail.getBefImage(), afinanceDetail);
		return new AuditHeader(afinanceDetail.getFinScheduleData().getFinReference(), null, null, null, auditDetail,
				afinanceDetail.getUserDetails(), overideMap);
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

}
