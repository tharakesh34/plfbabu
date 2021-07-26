package com.pennant.app.core;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jaxen.JaxenException;

import com.pennant.app.constants.AccountingEvent;
import com.pennant.app.constants.HolidayHandlerTypes;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.model.RateDetail;
import com.pennant.app.util.FrequencyUtil;
import com.pennant.app.util.RateUtil;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.app.util.ScheduleGenerator;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinAutoApprovalDetails;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.logging.dao.FinAutoApprovalDetailDAO;

public class FinAutoApprovalProcess extends GenericService<FinAutoApprovalDetails> {
	private static Logger logger = LogManager.getLogger(FinAutoApprovalProcess.class);

	private FinanceDetailService financeDetailService;
	private FinAutoApprovalDetailDAO finAutoApprovalDetailDAO;

	public void process(FinAutoApprovalDetails autoApproval) {
		logger.info(Literal.ENTERING);
		long userId = autoApproval.getUserId();

		LoggedInUser loggedInUser = new LoggedInUser();
		loggedInUser.setLoginUsrID(userId);

		String finReference = autoApproval.getFinReference();

		boolean finisQuickDisb = finAutoApprovalDetailDAO.isQuickDisb(finReference);

		if (!finisQuickDisb) {
			autoApproval.setErrorDesc("Quick disbursement is not enabled for the disbursement instruction.");
			logger.info(autoApproval.getErrorDesc());
			autoApproval.setStatus(DisbursementConstants.AUTODISB_STATUS_SUCCESS);
			logger.info(Literal.LEAVING);
			return;
		}

		boolean approvedLoan = checkForPreviousApproval(finReference);

		String nextRoleCode = financeDetailService.getNextRoleCodeByRef(finReference);

		FinanceDetail financeDetail = null;

		boolean servicing = finAutoApprovalDetailDAO.getFinanceServiceInstruction(finReference);

		if (!servicing && !approvedLoan) {
			financeDetail = financeDetailService.getOriginationFinance(finReference, nextRoleCode, FinServiceEvent.ORG,
					"");
			financeDetail.setModuleDefiner(FinServiceEvent.ORG);
		} else {
			financeDetail = financeDetailService.getServicingFinanceForQDP(finReference,
					AccountingEvent.ADDDBSN, FinServiceEvent.ADDDISB, nextRoleCode);
		}

		if (financeDetail != null) {
			processAndApprove(loggedInUser, autoApproval, financeDetail);
		}

		if (DisbursementConstants.AUTODISB_STATUS_PENDING.equals(autoApproval.getStatus())
				&& autoApproval.getErrorDesc() == null) {
			autoApproval.setStatus(DisbursementConstants.AUTODISB_STATUS_SUCCESS);
		}

		logger.info(Literal.LEAVING);
	}

	/**
	 * This Method Process and Approves the Finance which are in For Auto Approval. Prepares new Schedule based on the
	 * Realization Date and Validate and Approves the Record.
	 */
	private void processAndApprove(LoggedInUser user, FinAutoApprovalDetails finAad, FinanceDetail fd) {
		logger.info(Literal.ENTERING);

		FinScheduleData finScheduleData = fd.getFinScheduleData();
		FinanceMain fm = finScheduleData.getFinanceMain();
		FinanceType financeType = finScheduleData.getFinanceType();

		List<FinAdvancePayments> finAdvancePayments = fd.getAdvancePaymentsList();

		FinanceDisbursement disbursement = null;
		String paymentType = finAad.getPaymentType();
		for (FinanceDisbursement fds : finScheduleData.getDisbursementDetails()) {
			for (FinAdvancePayments fap : finAdvancePayments) {
				String disbStatus = fds.getDisbStatus();
				if (StringUtils.equals(null, disbStatus) && (fap.getPaymentId() == finAad.getDisbId())) {
					paymentType = fap.getPaymentType();
					disbursement = fds;
					break;
				}
			}
		}

		if (!disbursement.isQuickDisb()) {
			finAad.setErrorDesc("Quick disbursement is not enabled for the disbursement instruction.");
			finAad.setStatus(DisbursementConstants.AUTODISB_STATUS_SUCCESS);

			logger.info(finAad.getErrorDesc());
			logger.info(Literal.LEAVING);
			return;
		}

		if (!financeType.isAutoApprove()) {
			finAad.setErrorDesc("Auto approval flag is not enabled in loan type master.");
			finAad.setStatus(DisbursementConstants.AUTODISB_STATUS_SUCCESS);

			logger.info(finAad.getErrorDesc());
			logger.info(Literal.LEAVING);
			return;
		}

		if (finAad.getRealizedDate() == null) {
			throw new AppException("Payment Date is mandatory for Auto Approval Process.");
		}

		if (DateUtil.compare(fm.getNextRepayRvwDate(), finAad.getRealizedDate()) < 0) {
			throw new AppException("Payment Date is crossed Next Interest Review Frequency Date..");
		}

		if (DateUtil.compare(finAad.getRealizedDate(), fm.getFinStartDate()) < 0) {
			throw new AppException("Payment Date is before Loan Start Date..");
		}

		if (!fm.isFinIsActive()) {
			throw new AppException("loan is not active.");
		}

		Map<String, Integer> qdpValidityDays = finAutoApprovalDetailDAO.loadQDPValidityDays();
		int days = qdpValidityDays.get(paymentType);

		boolean validDisb = validateQDPDays(days, finAad.getDownloadedOn(), finAad.getRealizedDate());

		if (!validDisb) {
			finAad.setErrorDesc("Payment type validity expired");
			finAad.setStatus(DisbursementConstants.AUTODISB_STATUS_FAILED);

			logger.info(finAad.getErrorDesc());
			logger.info(Literal.LEAVING);
			return;
		}

		String moduleDefiner = fd.getModuleDefiner();

		try {

			if (FinServiceEvent.ORG.equals(moduleDefiner)) {
				approveLoan(fd, user, finAad, disbursement);
			} else if (checkForServicing(fm.getFinReference())) {
				approveAddDisbursemnt(fd, user, finAad);
			} else {
				finAad.setErrorDesc("Unable to Process");
				finAad.setStatus(DisbursementConstants.AUTODISB_STATUS_FAILED);
			}

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			finAad.setErrorDesc(StringUtils.substring(e.toString(), 0, 900));
			finAad.setStatus(DisbursementConstants.AUTODISB_STATUS_FAILED);
		}

		logger.info(Literal.LEAVING);
	}

	private void approveAddDisbursemnt(FinanceDetail fd, LoggedInUser userDetails,
			FinAutoApprovalDetails autoApproval) {
		logger.info(Literal.ENTERING);

		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();
		fm.setNewRecord(false);

		List<FinanceDisbursement> financeDisbursements = schdData.getDisbursementDetails();
		List<FinanceDisbursement> processedDisbursementList = new ArrayList<>(financeDisbursements.size());
		List<FinAdvancePayments> finAdvancePayments = fd.getAdvancePaymentsList();

		List<FinanceScheduleDetail> schedules = schdData.getFinanceScheduleDetails();
		if (CollectionUtils.isNotEmpty(schedules)) {
			List<FinServiceInstruction> finServiceInstructions = schdData.getFinServiceInstructions();

			for (FinServiceInstruction fsi : finServiceInstructions) {
				if (fm.isWorkflow()) {
					if (StringUtils.isEmpty(fm.getRecordType())) {
						fm.setVersion(fm.getVersion() + 1);
						if (fm.isNew()) {
							fm.setRecordType(PennantConstants.RECORD_TYPE_NEW);
						} else {
							fm.setRecordType(PennantConstants.RECORD_TYPE_UPD);
							fm.setNewRecord(true);
						}
					}
				} else {
					fm.setVersion(fm.getVersion() + 1);
				}

				schdData.getDisbursementDetails().clear();
				schdData.setDisbursementDetails(processedDisbursementList);

				fsi.setRecalFromDate(autoApproval.getRealizedDate());
				fm.setEventFromDate(autoApproval.getRealizedDate());
				fm.setRecalToDate(fsi.getRecalToDate());
				fm.setScheduleMethod(fsi.getSchdMethod());
				fm.setRecalType(fsi.getRecalType());

				fd.setFinScheduleData(
						ScheduleCalculator.addDisbursement(schdData, fsi.getAmount(), BigDecimal.ZERO, false));
				schdData.setSchduleGenerated(true);

				// Update Disb Status as D for the disbursement that was processed.
				for (FinanceDisbursement financeDisbursement : schdData.getDisbursementDetails()) {
					for (FinAdvancePayments finAdvancePayment : finAdvancePayments) {
						String status = finAdvancePayment.getStatus();
						if ((DisbursementConstants.STATUS_REALIZED.equals(status))
								|| (DisbursementConstants.STATUS_PAID.equals(status))) {
							financeDisbursement.setDisbStatus("D");
							financeDisbursement.setQuickDisb(true);
						}
					}
				}

				fm.setUserDetails(userDetails);
				fm.setFinCurrAssetValue(fm.getFinCurrAssetValue().add(fsi.getAmount()));

				fm.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				schdData.setFinanceMain(fm);

				doProcess(fd, userDetails);
			}
		}

		logger.info(Literal.LEAVING);
	}

	private void approveLoan(FinanceDetail fd, LoggedInUser user, FinAutoApprovalDetails appDetails,
			FinanceDisbursement disbursement) {
		logger.info(Literal.ENTERING);

		FinScheduleData finScheduleData = fd.getFinScheduleData();
		FinanceMain fm = finScheduleData.getFinanceMain();
		FinanceType financeType = finScheduleData.getFinanceType();

		List<FinanceDisbursement> financeDisbursements = finScheduleData.getDisbursementDetails();
		List<FinanceDisbursement> processedDisbursementList = new ArrayList<>(financeDisbursements.size());

		if (fm.isWorkflow()) {
			if (StringUtils.isEmpty(fm.getRecordType())) {
				fm.setVersion(fm.getVersion() + 1);
				if (fm.isNew()) {
					fm.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					fm.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					fm.setNewRecord(true);
				}
			}
		} else {
			fm.setVersion(fm.getVersion() + 1);
		}
		disbursement.setDisbStatus("D");
		disbursement.setDisbDate(appDetails.getRealizedDate());
		processedDisbursementList.add(disbursement);
		finScheduleData.setDisbursementDetails(processedDisbursementList);

		fm.setFinStartDate(appDetails.getRealizedDate());
		fm.setLastRepayDate(appDetails.getRealizedDate());
		fm.setLastRepayPftDate(appDetails.getRealizedDate());

		fm.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		if (!fm.isAllowGrcPeriod()) {
			fm.setGrcPeriodEndDate(appDetails.getRealizedDate());
		}

		fm.setRecalType("");
		fm.setCalculateRepay(true);
		finScheduleData.setFinanceMain(fm);

		finScheduleData.getRepayInstructions().clear();

		prepareSchedule(fd, fm, financeType);
		fm.setUserDetails(user);

		AuditHeader auditheader = doProcess(fd, user);
		if (!auditheader.isNextProcess()) {
			appDetails.setErrorDesc(auditheader.getAuditOveride());
			appDetails.setStatus(DisbursementConstants.AUTODISB_STATUS_FAILED);
		}

		logger.info(Literal.LEAVING);
	}

	private void prepareSchedule(FinanceDetail fd, FinanceMain fm, FinanceType financeType) {
		int fddLockPeriod = financeType.getFddLockPeriod();
		if (fm.isAllowGrcPeriod() && !ImplementationConstants.APPLY_FDDLOCKPERIOD_AFTERGRACE) {
			fddLockPeriod = 0;
		}

		if (!ImplementationConstants.ALLOW_FDD_ON_RVW_DATE) {
			fddLockPeriod = 0;
		}

		// grace period details
		if (fm.isAllowGrcPeriod()) {
			setGraceDetails(fm, financeType);
		}

		// Repay period details
		if (fm.getRepayPftFrq() != null) {
			fm.setNextRepayPftDate(FrequencyUtil.getNextDate(fm.getRepayPftFrq(), 1, fm.getGrcPeriodEndDate(),
					HolidayHandlerTypes.MOVE_NONE, false, fddLockPeriod).getNextFrequencyDate());
		}

		fm.setNextRepayPftDate(DateUtil.getDatePart(fm.getNextRepayPftDate()));

		// Allow Repay Review
		if (financeType.isFinIsRvwAlw()) {
			setRepayReviewDetails(fm, financeType);
		} else {
			fm.setRepayRvwFrq("");
		}

		// Allow Repay Capitalization
		if (financeType.isFinIsIntCpz()) {
			fm.setAllowRepayCpz(financeType.isFinIsIntCpz());
			fm.setNextRepayCpzDate(FrequencyUtil.getNextDate(fm.getRepayCpzFrq(), 1, fm.getGrcPeriodEndDate(),
					HolidayHandlerTypes.MOVE_NONE, false, fddLockPeriod).getNextFrequencyDate());

			fm.setNextRepayCpzDate(DateUtil.getDatePart(fm.getNextRepayCpzDate()));
		} else {
			fm.setRepayCpzFrq("");
		}

		// Repay Frequency
		if (fm.getRepayFrq() != null) {
			fm.setNextRepayDate(FrequencyUtil.getNextDate(fm.getRepayFrq(), 1, fm.getGrcPeriodEndDate(),
					HolidayHandlerTypes.MOVE_NONE, false, fddLockPeriod).getNextFrequencyDate());
		}
		if (fm.getNextRepayDate() != null) {
			fm.setNextRepayDate(DateUtil.getDatePart(fm.getNextRepayDate()));
		}

		// Maturity Date
		List<Calendar> scheduleDateList = null;
		if (fm.getRepayFrq() != null && fm.getNextRepayDate() != null) {
			scheduleDateList = FrequencyUtil.getNextDate(fm.getRepayFrq(), fm.getNumberOfTerms(), fm.getNextRepayDate(),
					HolidayHandlerTypes.MOVE_NONE, true, 0).getScheduleList();

		}

		if (scheduleDateList != null) {
			Calendar calendar = scheduleDateList.get(scheduleDateList.size() - 1);
			fm.setMaturityDate(calendar.getTime());
			fm.setMaturityDate(DateUtil.getDatePart(fm.getMaturityDate()));
		}

		fd.setFinScheduleData(ScheduleGenerator.getNewSchd(fd.getFinScheduleData()));

		if (!fd.getFinScheduleData().getFinanceScheduleDetails().isEmpty()) {
			fd.setFinScheduleData(ScheduleCalculator.getCalSchd(fd.getFinScheduleData(), BigDecimal.ZERO));
			fd.getFinScheduleData().setSchduleGenerated(true);
		}
	}

	private void setRepayReviewDetails(FinanceMain fm, FinanceType financeType) {
		int fddLockPeriod = financeType.getFddLockPeriod();
		fm.setAllowRepayRvw(financeType.isFinIsRvwAlw());

		RateDetail rateDetail = RateUtil.rates(fm.getRepayBaseRate(), fm.getFinCcy(), fm.getRepaySpecialRate(),
				fm.getRepayMargin(), financeType.getFInMinRate(), financeType.getFinMaxRate());
		Date baseDate = DateUtil.addDays(fm.getGrcPeriodEndDate(), rateDetail.getLockingPeriod());

		fm.setNextRepayRvwDate(FrequencyUtil
				.getNextDate(fm.getRepayRvwFrq(), 1, baseDate, HolidayHandlerTypes.MOVE_NONE, false, fddLockPeriod)
				.getNextFrequencyDate());

		fm.setNextRepayRvwDate(DateUtil.getDatePart(fm.getNextRepayRvwDate()));
	}

	private void setGraceDetails(FinanceMain fm, FinanceType financeType) {
		List<Calendar> scheduleDateList = FrequencyUtil.getNextDate(fm.getGrcPftFrq(), fm.getGraceTerms(),
				fm.getNextGrcPftDate(), HolidayHandlerTypes.MOVE_NONE, true, 0).getScheduleList();

		Date grcEndDate = null;
		if (scheduleDateList != null) {
			Calendar calendar = scheduleDateList.get(scheduleDateList.size() - 1);
			grcEndDate = calendar.getTime();
		}

		fm.setGrcPeriodEndDate(DateUtil.getDatePart(grcEndDate));

		if (fm.isAllowGrcPftRvw()) {
			RateDetail rateDetail = RateUtil.rates(fm.getGraceBaseRate(), fm.getFinCcy(), fm.getGraceSpecialRate(),
					fm.getGrcMargin(), financeType.getFInGrcMinRate(), financeType.getFinGrcMaxRate());
			Date baseDate = DateUtil.addDays(fm.getFinStartDate(), rateDetail.getLockingPeriod());

			// Next Grace profit Review Date
			if (ImplementationConstants.ALLOW_FDD_ON_RVW_DATE) {
				fm.setNextGrcPftRvwDate(FrequencyUtil.getNextDate(fm.getGrcPftRvwFrq(), 1, baseDate,
						HolidayHandlerTypes.MOVE_NONE, false, financeType.getFddLockPeriod()).getNextFrequencyDate());
			} else {
				fm.setNextGrcPftRvwDate(FrequencyUtil
						.getNextDate(fm.getGrcPftRvwFrq(), 1, baseDate, HolidayHandlerTypes.MOVE_NONE, false, 0)
						.getNextFrequencyDate());
			}
			fm.setNextGrcPftRvwDate(DateUtil.getDatePart(fm.getNextGrcPftRvwDate()));
			if (fm.getNextGrcPftRvwDate().after(fm.getGrcPeriodEndDate())) {
				fm.setNextGrcPftRvwDate(fm.getGrcPeriodEndDate());
			}
		}

		// Allow Grace Capitalization
		if (fm.isAllowGrcCpz()) {
			fm.setAllowGrcCpz(true);
			fm.setNextGrcCpzDate(FrequencyUtil.getNextDate(fm.getGrcCpzFrq(), 1, fm.getFinStartDate(),
					HolidayHandlerTypes.MOVE_NONE, false, financeType.getFddLockPeriod()).getNextFrequencyDate());
			fm.setNextGrcCpzDate(DateUtil.getDatePart(fm.getNextGrcCpzDate()));
			if (fm.getNextGrcCpzDate().after(fm.getGrcPeriodEndDate())) {
				fm.setNextGrcCpzDate(fm.getGrcPeriodEndDate());
			}
		}

		fm.setNextGrcPftDate(DateUtil.getDatePart(FrequencyUtil.getNextDate(fm.getGrcPftFrq(), 1, fm.getFinStartDate(),
				HolidayHandlerTypes.MOVE_NONE, false, financeType.getFddLockPeriod()).getNextFrequencyDate()));
	}

	private boolean checkForPreviousApproval(String finReference) {
		return finAutoApprovalDetailDAO.getFinanceIfApproved(finReference);
	}

	private boolean checkForServicing(String finReference) {
		return finAutoApprovalDetailDAO.getFinanceServiceInstruction(finReference);
	}

	private boolean validateQDPDays(int days, Date disbDate, Date realizedDate) {
		Date diffDate = DateUtil.addDays(disbDate, days);
		return realizedDate.compareTo(diffDate) < 0;
	}

	private AuditHeader doProcess(FinanceDetail fd, LoggedInUser user) {
		Map<String, List<ErrorDetail>> overideMap = new HashMap<>();

		AuditHeader auditHeader = null;
		FinanceMain fm = fd.getFinScheduleData().getFinanceMain();

		fm.setLastMntBy(user.getUserId());
		fm.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		fm.setAutoApprove(true);

		fm.setUserDetails(user);
		fd.setUserDetails(user);
		fd.getCustomerDetails().setUserDetails(user);

		auditHeader = getAuditHeader(fd, PennantConstants.TRAN_WF, overideMap);
		try {
			auditHeader = financeDetailService.doApprove(auditHeader, false);
		} catch (InterfaceException | JaxenException e) {
			throw new AppException(e.getMessage(), e);
		}

		List<ErrorDetail> overideMessage = auditHeader.getOverideMessage();
		if (overideMessage != null) {
			for (int i = 0; i < overideMessage.size(); i++) {
				ErrorDetail overideDetail = overideMessage.get(i);
				if (!isOverride(overideMap, overideDetail)) {
					setOverideMap(overideMap, overideDetail);
				}
			}

			auditHeader.setOverideMap(overideMap);
			auditHeader = getAuditHeader(fd, PennantConstants.TRAN_WF, overideMap);
			try {
				auditHeader = financeDetailService.doApprove(auditHeader, false);
			} catch (InterfaceException | JaxenException e) {
				throw new AppException(e.getMessage(), e);
			}
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

	private AuditHeader getAuditHeader(FinanceDetail fd, String tranType, Map<String, List<ErrorDetail>> overideMap) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, fd.getBefImage(), fd);
		String finReference = fd.getFinScheduleData().getFinReference();
		return new AuditHeader(finReference, null, null, null, auditDetail, fd.getUserDetails(), overideMap);
	}

	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	public void setFinAutoApprovalDetailDAO(FinAutoApprovalDetailDAO finAutoApprovalDetailDAO) {
		this.finAutoApprovalDetailDAO = finAutoApprovalDetailDAO;
	}

}
