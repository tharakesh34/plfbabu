package com.pennant.backend.service.finance.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.core.AccrualService;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.Repayments.FinanceRepaymentsDAO;
import com.pennant.backend.dao.applicationmaster.CustomerStatusCodeDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.commitment.CommitmentDAO;
import com.pennant.backend.dao.commitment.CommitmentMovementDAO;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.finance.FinLogEntryDetailDAO;
import com.pennant.backend.dao.finance.FinStatusDetailDAO;
import com.pennant.backend.dao.finance.FinanceDisbursementDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.RepayInstructionDAO;
import com.pennant.backend.dao.rulefactory.PostingsDAO;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.commitment.Commitment;
import com.pennant.backend.model.commitment.CommitmentMovement;
import com.pennant.backend.model.finance.FinLogEntryDetail;
import com.pennant.backend.model.finance.FinRepayHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinStatusDetail;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.RepayInstruction;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.RepaymentCancellationService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.eod.dao.CustomerQueuingDAO;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.TableType;
import com.pennapps.core.util.ObjectUtil;

public class RepaymentCancellationServiceImpl extends GenericService<FinanceMain>
		implements RepaymentCancellationService {

	private static final Logger logger = LogManager.getLogger(RepaymentCancellationServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private FinanceRepaymentsDAO financeRepaymentsDAO;
	private FinLogEntryDetailDAO finLogEntryDetailDAO;
	private PostingsDAO postingsDAO;
	private PostingsPreparationUtil postingsPreparationUtil;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private FinanceDisbursementDAO financeDisbursementDAO;
	private RepayInstructionDAO repayInstructionDAO;
	private FinanceMainDAO financeMainDAO;
	private FinanceProfitDetailDAO financeProfitDetailDAO;
	private CustomerDAO customerDAO;
	private CustomerStatusCodeDAO customerStatusCodeDAO;
	private FinStatusDetailDAO finStatusDetailDAO;
	private CommitmentDAO commitmentDAO;
	private CommitmentMovementDAO commitmentMovementDAO;
	private AccrualService accrualService;
	private CustomerQueuingDAO customerQueuingDAO;

	public RepaymentCancellationServiceImpl() {
		super();
	}

	@Override
	public FinanceDetail getFinanceDetailById(long finID, String type) {
		logger.debug(Literal.ENTERING);

		FinanceMain fm = financeMainDAO.getFinanceMainById(finID, type, false);

		String finReference = fm.getFinReference();

		FinanceDetail fd = new FinanceDetail();

		FinScheduleData schdData = fd.getFinScheduleData();

		schdData.setFinID(finID);
		schdData.setFinReference(finReference);

		schdData.setFinanceMain(fm);

		schdData.setRepayDetails(financeRepaymentsDAO.getFinRepayListByLinkedTranID(finID));

		logger.debug(Literal.LEAVING);

		return fd;
	}

	@Override
	public AuditHeader saveOrUpdate(AuditHeader aAuditHeader) throws AppException {
		logger.debug(Literal.ENTERING);

		aAuditHeader = businessValidation(aAuditHeader, "saveOrUpdate");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		if (!aAuditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return aAuditHeader;
		}

		AuditHeader auditHeader = ObjectUtil.clone(aAuditHeader);

		FinanceDetail fd = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		TableType tableType = TableType.MAIN_TAB;
		if (fm.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}
		fm.setRcdMaintainSts(FinServiceEvent.CANCELRPY);
		if (tableType == TableType.MAIN_TAB) {
			fm.setRcdMaintainSts("");
		}

		// Repayments Postings Details Process Execution
		if (!fm.isWorkflow()) {
			String errorCode = processRepayCancellation(fm);
			if (StringUtils.isNotBlank(errorCode)) {
				throw new InterfaceException("9999", errorCode);
			}
		} else {

			// Finance Main Details Save And Update
			// =======================================
			if (fm.isNewRecord()) {
				financeMainDAO.save(fm, tableType, false);
			} else {
				financeMainDAO.update(fm, tableType, false);
			}
		}

		String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), fm.getExcludeFields());
		String auditTranType = auditHeader.getAuditTranType();
		auditHeader.setAuditDetail(new AuditDetail(auditTranType, 1, fields[0], fields[1], fm.getBefImage(), fm));

		auditHeader.setAuditDetails(auditDetails);
		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		FinanceDetail fd = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		// ScheduleDetails deletion
		financeMainDAO.delete(fm, TableType.TEMP_TAB, false, false);

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), fm.getExcludeFields());
		String auditTranType = auditHeader.getAuditTranType();
		auditHeader.setAuditDetail(new AuditDetail(auditTranType, 1, fields[0], fields[1], fm.getBefImage(), fm));
		auditHeaderDAO.addAudit(auditHeader);

		// Reset Finance Detail Object for Service Task Verifications
		auditHeader.getAuditDetail().setModelData(fd);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public AuditHeader doApprove(AuditHeader aAuditHeader) throws AppException {
		logger.debug(Literal.ENTERING);

		String tranType = "";
		aAuditHeader = businessValidation(aAuditHeader, "doApprove");
		if (!aAuditHeader.isNextProcess()) {
			return aAuditHeader;
		}

		AuditHeader auditHeader = ObjectUtil.clone(aAuditHeader);
		FinanceDetail fd = (FinanceDetail) auditHeader.getAuditDetail().getModelData();

		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		String errorCode = processRepayCancellation(fm);
		if (StringUtils.isNotBlank(errorCode)) {
			throw new InterfaceException("9999", errorCode);
		}

		tranType = PennantConstants.TRAN_UPD;

		String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), fm.getExcludeFields());
		financeMainDAO.delete(fm, TableType.TEMP_TAB, false, true);
		String auditTranType = aAuditHeader.getAuditTranType();
		auditHeader.setAuditDetail(new AuditDetail(auditTranType, 1, fields[0], fields[1], fm.getBefImage(), fm));

		// Adding audit as deleted from TEMP table
		auditHeaderDAO.addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditTranType = auditHeader.getAuditTranType();
		auditHeader.setAuditDetail(new AuditDetail(auditTranType, 1, fields[0], fields[1], fm.getBefImage(), fm));

		// Adding audit as Insert/Update/deleted into main table
		auditHeaderDAO.addAudit(auditHeader);

		// Reset Finance Detail Object for Service Task Verifications
		auditHeader.getAuditDetail().setModelData(fd);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		for (int i = 0; i < auditDetails.size(); i++) {
			auditHeader.setErrorList(auditDetails.get(i).getErrorDetails());
		}

		auditHeader = nextProcess(auditHeader);

		logger.debug(Literal.LEAVING);

		return auditHeader;
	}

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug(Literal.ENTERING);

		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		FinanceDetail fd = (FinanceDetail) auditDetail.getModelData();

		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		long finID = fm.getFinID();
		String finReference = fm.getFinReference();
		long custID = fm.getCustID();

		FinanceMain tempFinanceMain = null;
		if (fm.isWorkflow()) {
			tempFinanceMain = financeMainDAO.getFinanceMainById(finID, "_Temp", false);
		}
		FinanceMain befFinanceMain = financeMainDAO.getFinanceMainById(finID, "", false);
		FinanceMain oldFinanceMain = fm.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = finReference;
		errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];

		if (fm.isNewRecord()) { // for New record or new record into work flow

			if (!fm.isWorkflow()) {// With out Work flow only new
				// records
				if (befFinanceMain != null) { // Record Already Exists in the
					// table then error
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow
				if (fm.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if
					// records type is new
					if (befFinanceMain != null || tempFinanceMain != null) { // if
						// records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
					}
				} else { // if records not exists in the Main flow table
					if (befFinanceMain == null || tempFinanceMain != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!fm.isWorkflow()) { // With out Work flow for update
				// and delete

				if (befFinanceMain == null) { // if records not exists in the
					// main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, valueParm), usrLanguage));
				} else {
					if (oldFinanceMain != null
							&& !oldFinanceMain.getLastMntOn().equals(befFinanceMain.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm, valueParm),
									usrLanguage));
						} else {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm, valueParm),
									usrLanguage));
						}
					}
				}
			} else {

				if (tempFinanceMain == null) { // if records not exists in the
					// Work flow table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}

				if (tempFinanceMain != null && oldFinanceMain != null
						&& !oldFinanceMain.getLastMntOn().equals(tempFinanceMain.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}
			}
		}

		// Checking , if Customer is in EOD process or not. if Yes, not allowed to do an action
		int eodProgressCount = customerQueuingDAO.getProgressCountByCust(custID);

		// If Customer Exists in EOD Processing, Not allowed to Maintenance till completion
		if (eodProgressCount > 0) {
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
					new ErrorDetail(PennantConstants.KEY_FIELD, "60203", errParm, valueParm), usrLanguage));
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !fm.isWorkflow()) {
			auditDetail.setBefImage(befFinanceMain);
		}

		return auditDetail;
	}

	private String processRepayCancellation(FinanceMain fm) throws AppException {
		logger.debug(Literal.ENTERING);

		long finID = fm.getFinID();
		String finReference = fm.getFinReference();
		Date appData = SysParamUtil.getAppDate();

		// Fetch Repayments Details List based on Finance Reference
		List<FinanceRepayments> rpdList = financeRepaymentsDAO.getFinRepayListByLinkedTranID(finID);

		FinanceRepayments repayment = rpdList.get(0);
		Date rpyValueDate = repayment.getFinValueDate();

		// Fetch Log Entry Details Greater than this Repayments Entry , which are having Schedule Recalculation
		// If Any Exist Case after this Repayments with Schedule Recalculation then Stop Process
		// ============================================
		List<FinLogEntryDetail> list = finLogEntryDetailDAO.getFinLogEntryDetailList(finID, 0);
		if (list != null && !list.isEmpty()) {
			return "Finance is Maintained after this Repayment done.";
		}

		// Fetch Repay header Details If Repayment Done from System
		FinRepayHeader rch = financeRepaymentsDAO.getFinRepayHeader(finID, repayment.getLinkedTranId(), "");
		String finEventCode = "";
		if (rch == null) {
			finEventCode = FinServiceEvent.EARLYRPY;
		} else {
			finEventCode = rch.getFinEvent();
		}

		// Valid Check for Finance Reversal On Active Finance Or not with ValueDate CheckUp
		if (!fm.isFinIsActive()) {

			// Not Allowed for Inactive Finances
			return "Fiannce Cannot be Processed for Reversal of Payment. Finance is in InActive State.";
		}

		// Is Schedule Regenerated >>> Adjust Finance Details From Log Tables to Main Tables and remove data from
		// Log Tables
		// Otherwise Only Schedule Change with Repayments Amount
		// ============================================
		FinLogEntryDetail detail = finLogEntryDetailDAO.getFinLogEntryDetailByLog(0);
		boolean isMigratedRepayment = false;
		if (detail == null) {
			logger.debug("Log Entry Details Missing. Cancellation process for Manual Reversal Payment Process");

			detail = new FinLogEntryDetail();
			detail.setFinID(finID);
			detail.setFinReference(finReference);
			detail.setEventAction(FinServiceEvent.EARLYRPY);
			detail.setSchdlRecal(false);
			detail.setPostDate(appData);
			detail.setReversalCompleted(false);
			isMigratedRepayment = true;
		}

		// Posting Reversal Case Program Calling in Equation
		// ============================================
		long linkedTranId = 0;
		if (!isMigratedRepayment) {
			linkedTranId = repayment.getLinkedTranId();
			postingsPreparationUtil.getReversalsByLinkedTranID(linkedTranId);
		}

		// Overdue Recovery Details Reset Back to Original State , If any penalties Paid On this Repayments Process
		// ============================================

		// Calculate Total Penalty Amount Paid based on this Transaction
		/*
		 * BigDecimal totalPenaltyPaid = getPostingsDAO().getPostAmtByTranIdandEvent(finReference,
		 * AccountEventConstants.ACCEVENT_LATEPAY , repayment.getLinkedTranId());
		 * if(totalPenaltyPaid.compareTo(BigDecimal.ZERO) > 0){ for (FinanceRepayments repay : repayList) {
		 * 
		 * } }
		 */

		if (!detail.isSchdlRecal()) {
			FinanceScheduleDetail schedule = null;
			for (FinanceRepayments rpd : rpdList) {
				Date schdDate = rpd.getFinSchdDate();
				schedule = financeScheduleDetailDAO.getFinanceScheduleDetailById(finID, schdDate, "", false);

				schedule = updateScheduleDetailsData(schedule, rpd);

				financeScheduleDetailDAO.updateForRpy(schedule);

			}

		} else {
			// Deletion of Finance Schedule Related Details From Main Table
			listDeletion(finID, "", false, 0);

			// Fetching Last Log Entry Finance Details
			FinScheduleData scheduleData = getFinSchDataByFinRef(finID, detail.getLogKey(), "_Log");
			scheduleData.setFinanceMain(fm);

			// Re-Insert Log Entry Data before Repayments Process Recalculations
			listSave(scheduleData, "", 0);

			// Delete Data from Log Entry Tables After Inserting into Main Tables
			listDeletion(finID, "_Log", false, detail.getLogKey());

		}

		// Finance Repayments Amount Updation if Principal Amount Exists
		// ============================================
		BigDecimal totalPriAmount = BigDecimal.ZERO;
		for (FinanceRepayments repay : rpdList) {
			if (repay.getFinSchdPriPaid().compareTo(BigDecimal.ZERO) > 0) {
				totalPriAmount = totalPriAmount.add(repay.getFinSchdPriPaid());
			}
		}

		// Check Current Finance Max Status For updation
		// ============================================
		if (totalPriAmount.compareTo(BigDecimal.ZERO) > 0) {
			boolean isStsChanged = false;
			String curFinStatus = customerStatusCodeDAO.getFinanceStatus(finReference, true);
			if (curFinStatus != null && !fm.getFinStatus().equals(curFinStatus)) {
				isStsChanged = true;
			}

			// Finance Main Details Update
			fm.setFinStatus(curFinStatus);
			fm.setFinStsReason(FinanceConstants.FINSTSRSN_MANUAL);
			fm.setClosingStatus(null);
			fm.setFinIsActive(true);
			fm.setWriteoffLoan(fm.isWriteoffLoan());
			financeMainDAO.updateRepaymentAmount(fm);

			// Finance Status Details insertion, if status modified then change to High Risk Level
			if (isStsChanged) {
				FinStatusDetail statusDetail = new FinStatusDetail();
				statusDetail.setFinReference(fm.getFinReference());
				statusDetail.setValueDate(appData);
				statusDetail.setCustId(fm.getCustID());
				statusDetail.setFinStatus(curFinStatus);

				finStatusDetailDAO.saveOrUpdateFinStatus(statusDetail);
			}

			// Finance Commitment Reference Posting Details
			Commitment commitment = null;
			if (StringUtils.isNotBlank(fm.getFinCommitmentRef())) {
				commitment = commitmentDAO.getCommitmentById(fm.getFinCommitmentRef().trim(), "");

				if (commitment != null && commitment.isRevolving()) {
					BigDecimal cmtUtlAmt = CalculationUtil.getConvertedAmount(fm.getFinCcy(), commitment.getCmtCcy(),
							totalPriAmount);
					commitmentDAO.updateCommitmentAmounts(commitment.getCmtReference(), cmtUtlAmt,
							commitment.getCmtExpDate());
					CommitmentMovement cmtMovement = prepareCommitMovement(commitment, fm, cmtUtlAmt, linkedTranId);
					if (cmtMovement != null) {
						commitmentMovementDAO.save(cmtMovement, "");
					}
				}
			}
		}

		if (!isMigratedRepayment) {
			finLogEntryDetailDAO.updateLogEntryStatus(detail);
			financeRepaymentsDAO.deleteRpyDetailbyLinkedTranId(repayment.getLinkedTranId(), finID);
			financeRepaymentsDAO.deleteFinRepayHeaderByTranId(finID, repayment.getLinkedTranId(), "");
			financeRepaymentsDAO.deleteFinRepaySchListByTranId(finID, repayment.getLinkedTranId(), "");
		} else {
			detail.setReversalCompleted(true);
			finLogEntryDetailDAO.save(detail);
			financeRepaymentsDAO.deleteRpyDetailbyMaxPostDate(rpyValueDate, finID);

		}

		List<FinanceScheduleDetail> schedules = financeScheduleDetailDAO.getFinSchdDetailsForBatch(finID);
		FinanceProfitDetail profitDetail = financeProfitDetailDAO.getFinProfitDetailsById(finID);
		profitDetail = accrualService.calProfitDetails(fm, schedules, profitDetail, appData);
		String worstSts = customerStatusCodeDAO.getFinanceStatus(profitDetail.getFinReference(), false);
		profitDetail.setFinWorstStatus(worstSts);

		// Reset Back Repayments Details
		rpdList = financeRepaymentsDAO.getFinRepayListByLinkedTranID(finID);
		if (!rpdList.isEmpty()) {
			BigDecimal totPri = BigDecimal.ZERO;
			BigDecimal totPft = BigDecimal.ZERO;
			for (FinanceRepayments repay : rpdList) {
				totPri = totPri.add(repay.getFinSchdPriPaid());
				totPft = totPft.add(repay.getFinSchdPftPaid());
			}

			profitDetail.setLatestRpyDate(rpdList.get(0).getFinPostDate());
			profitDetail.setLatestRpyPri(totPri);
			profitDetail.setLatestRpyPft(totPft);
		} else {
			profitDetail.setClosingStatus(fm.getClosingStatus());
			profitDetail.setLatestRpyDate(null);
			profitDetail.setLatestRpyPri(BigDecimal.ZERO);
			profitDetail.setLatestRpyPft(BigDecimal.ZERO);
		}
		profitDetail.setClosingStatus(null);
		profitDetail.setFinIsActive(fm.isFinIsActive());

		financeProfitDetailDAO.update(profitDetail, true);

		return "";
	}

	private FinanceScheduleDetail updateScheduleDetailsData(FinanceScheduleDetail schedule,
			FinanceRepayments repayment) {
		logger.debug(Literal.ENTERING);

		schedule.setSchdPftPaid(schedule.getSchdPftPaid().subtract(repayment.getFinSchdPftPaid()));
		schedule.setSchdPriPaid(schedule.getSchdPriPaid().subtract(repayment.getFinSchdPriPaid()));

		// Fee Details
		schedule.setSchdFeePaid(schedule.getSchdFeePaid().subtract(repayment.getSchdFeePaid()));

		// Finance Schedule Profit Balance Check
		schedule.setSchPriPaid(false);
		schedule.setSchPftPaid(false);
		if ((schedule.getProfitSchd().subtract(schedule.getSchdPftPaid())).compareTo(BigDecimal.ZERO) == 0) {
			schedule.setSchPftPaid(true);

			// Finance Schedule Principal Balance Check
			if ((schedule.getPrincipalSchd().subtract(schedule.getSchdPriPaid())).compareTo(BigDecimal.ZERO) == 0) {
				schedule.setSchPriPaid(true);
			} else {
				schedule.setSchPriPaid(false);
			}
		} else {
			schedule.setSchPftPaid(false);
		}
		logger.debug(Literal.LEAVING);
		return schedule;
	}

	private CommitmentMovement prepareCommitMovement(Commitment commitment, FinanceMain financeMain,
			BigDecimal postAmount, long linkedtranId) {

		CommitmentMovement movement = new CommitmentMovement();
		Date appData = SysParamUtil.getAppDate();

		movement.setCmtReference(commitment.getCmtReference());
		movement.setFinReference(financeMain.getFinReference());
		movement.setFinBranch(financeMain.getFinBranch());
		movement.setFinType(financeMain.getFinType());
		movement.setMovementDate(appData);
		movement.setMovementOrder(commitmentMovementDAO.getMaxMovementOrderByRef(commitment.getCmtReference()) + 1);
		movement.setMovementType("RR");// Repayment Reversal
		movement.setMovementAmount(postAmount);
		movement.setCmtAmount(commitment.getCmtAmount());
		movement.setCmtUtilizedAmount(commitment.getCmtUtilizedAmount().add(postAmount));
		if (commitment.getCmtExpDate().compareTo(appData) < 0) {
			movement.setCmtAvailable(BigDecimal.ZERO);
		} else {
			movement.setCmtAvailable(commitment.getCmtAvailable().subtract(postAmount));
		}
		movement.setCmtCharges(BigDecimal.ZERO);
		movement.setLinkedTranId(linkedtranId);
		movement.setVersion(1);
		movement.setLastMntBy(9999);
		movement.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		movement.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		movement.setRoleCode("");
		movement.setNextRoleCode("");
		movement.setTaskId("");
		movement.setNextTaskId("");
		movement.setRecordType("");
		movement.setWorkflowId(0);

		return movement;

	}

	private FinScheduleData getFinSchDataByFinRef(long finID, long logKey, String type) {
		logger.debug(Literal.ENTERING);

		FinScheduleData schdData = new FinScheduleData();
		schdData.setFinanceScheduleDetails(financeScheduleDetailDAO.getFinScheduleDetails(finID, type, false, logKey));
		schdData.setDisbursementDetails(
				financeDisbursementDAO.getFinanceDisbursementDetails(finID, type, false, logKey));
		schdData.setRepayInstructions(repayInstructionDAO.getRepayInstructions(finID, type, false, logKey));

		logger.debug(Literal.LEAVING);

		return schdData;
	}

	private void listDeletion(long finID, String tableType, boolean isWIF, long logKey) {
		logger.debug(Literal.ENTERING);

		financeScheduleDetailDAO.deleteByFinReference(finID, tableType, isWIF, logKey);
		financeDisbursementDAO.deleteByFinReference(finID, tableType, isWIF, logKey);
		repayInstructionDAO.deleteByFinReference(finID, tableType, isWIF, logKey);

		logger.debug(Literal.LEAVING);
	}

	private void listSave(FinScheduleData schdData, String tableType, long logKey) {
		Map<Date, Integer> mapDateSeq = new HashMap<>();

		List<FinanceScheduleDetail> schedules = schdData.getFinanceScheduleDetails();
		List<FinanceDisbursement> disbursements = schdData.getDisbursementDetails();
		List<RepayInstruction> repayInstructions = schdData.getRepayInstructions();

		FinanceMain fm = schdData.getFinanceMain();

		long finID = fm.getFinID();
		String finReference = fm.getFinReference();

		for (FinanceScheduleDetail schd : schedules) {
			schd.setLastMntBy(fm.getLastMntBy());
			schd.setFinID(finID);
			schd.setFinReference(finReference);
			int seqNo = 0;

			if (mapDateSeq.containsKey(schd.getSchDate())) {
				seqNo = mapDateSeq.get(schd.getSchDate());
				mapDateSeq.remove(schd.getSchDate());
			}
			seqNo = seqNo + 1;
			mapDateSeq.put(schd.getSchDate(), seqNo);
			schd.setSchSeq(seqNo);
			schd.setLogKey(logKey);
		}

		// Schedule Version Updating
		if (StringUtils.isBlank(tableType)) {
			financeMainDAO.updateSchdVersion(fm, false);
		}

		financeScheduleDetailDAO.saveList(schedules, tableType, false);

		// Finance Disbursement Details
		mapDateSeq = new HashMap<>();
		Date appDate = SysParamUtil.getAppDate();

		for (FinanceDisbursement dd : disbursements) {
			dd.setFinID(finID);
			dd.setFinReference(finReference);
			dd.setDisbReqDate(appDate);
			dd.setDisbIsActive(true);
			dd.setLogKey(logKey);
			dd.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			dd.setLastMntBy(schdData.getFinanceMain().getLastMntBy());
		}

		financeDisbursementDAO.saveList(disbursements, tableType, false);

		// Finance Repay Instruction Details
		for (RepayInstruction ri : repayInstructions) {
			ri.setFinID(finID);
			ri.setFinReference(finReference);
			ri.setLogKey(logKey);
		}

		repayInstructionDAO.saveList(repayInstructions, tableType, false);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public void setFinanceRepaymentsDAO(FinanceRepaymentsDAO financeRepaymentsDAO) {
		this.financeRepaymentsDAO = financeRepaymentsDAO;
	}

	public void setFinLogEntryDetailDAO(FinLogEntryDetailDAO finLogEntryDetailDAO) {
		this.finLogEntryDetailDAO = finLogEntryDetailDAO;
	}

	public void setPostingsDAO(PostingsDAO postingsDAO) {
		this.postingsDAO = postingsDAO;
	}

	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}

	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	public void setFinanceDisbursementDAO(FinanceDisbursementDAO financeDisbursementDAO) {
		this.financeDisbursementDAO = financeDisbursementDAO;
	}

	public void setRepayInstructionDAO(RepayInstructionDAO repayInstructionDAO) {
		this.repayInstructionDAO = repayInstructionDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public void setFinanceProfitDetailDAO(FinanceProfitDetailDAO financeProfitDetailDAO) {
		this.financeProfitDetailDAO = financeProfitDetailDAO;
	}

	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

	public void setCustomerStatusCodeDAO(CustomerStatusCodeDAO customerStatusCodeDAO) {
		this.customerStatusCodeDAO = customerStatusCodeDAO;
	}

	public void setFinStatusDetailDAO(FinStatusDetailDAO finStatusDetailDAO) {
		this.finStatusDetailDAO = finStatusDetailDAO;
	}

	public void setCommitmentDAO(CommitmentDAO commitmentDAO) {
		this.commitmentDAO = commitmentDAO;
	}

	public void setCommitmentMovementDAO(CommitmentMovementDAO commitmentMovementDAO) {
		this.commitmentMovementDAO = commitmentMovementDAO;
	}

	public void setAccrualService(AccrualService accrualService) {
		this.accrualService = accrualService;
	}

	public void setCustomerQueuingDAO(CustomerQueuingDAO customerQueuingDAO) {
		this.customerQueuingDAO = customerQueuingDAO;
	}

}
