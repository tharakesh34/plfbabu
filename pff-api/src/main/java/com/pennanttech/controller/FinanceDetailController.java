package com.pennanttech.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.util.APIHeader;
import com.pennant.app.util.FeeScheduleCalculator;
import com.pennant.app.util.ReferenceGenerator;
import com.pennant.app.util.ReferenceUtil;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.app.util.ScheduleGenerator;
import com.pennant.app.util.SessionUserDetails;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.solutionfactory.StepPolicyDetailDAO;
import com.pennant.backend.dao.solutionfactory.StepPolicyHeaderDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.extendedfield.ExtendedField;
import com.pennant.backend.model.extendedfield.ExtendedFieldData;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinPlanEmiHoliday;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceStepPolicyDetail;
import com.pennant.backend.model.solutionfactory.StepPolicyDetail;
import com.pennant.backend.model.solutionfactory.StepPolicyHeader;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.fees.FeeDetailService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.VASConsatnts;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.util.APIConstants;
import com.pennanttech.ws.model.finance.EmiResponse;
import com.pennanttech.ws.service.APIErrorHandlerService;

public class FinanceDetailController extends SummaryDetailService {
	private static final Logger logger = LogManager.getLogger(FinanceDetailController.class);

	private FinanceDetailService financeDetailService;
	private StepPolicyDetailDAO stepPolicyDetailDAO;
	private StepPolicyHeaderDAO stepPolicyHeaderDAO;
	private CustomerDetailsService customerDetailsService;
	private FeeDetailService feeDetailService;

	public FinScheduleData doCreateFinanceSchedule(FinScheduleData schdData) throws AppException {
		logger.debug(Literal.ENTERING);

		if (schdData == null) {
			return null;
		}

		FinanceMain fm = schdData.getFinanceMain();
		fm.setFinType(schdData.getFinanceType().getFinType());

		String finReference = null;
		if (StringUtils.isBlank(fm.getFinReference())) {
			finReference = String.valueOf(ReferenceGenerator.generateFinRef(fm, schdData.getFinanceType()));
		} else {
			finReference = fm.getFinReference();
		}

		fm.setFinReference(finReference);
		fm.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		fm.setWorkflowId(0);
		fm.setNewRecord(true);
		fm.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		fm.setFinSourceID(PennantConstants.FINSOURCE_ID_API);
		// prepare required data
		doSetRequiredData(schdData);

		try {
			// call schedule calculator
			schdData = ScheduleGenerator.getNewSchd(schdData);
			if (schdData.getFinanceScheduleDetails().size() != 0) {

				schdData = ScheduleCalculator.getCalSchd(schdData, BigDecimal.ZERO);
				schdData.setSchduleGenerated(true);
				if (!fm.isAllowGrcPeriod()) {
					fm.setGrcSchdMthd(null);
				}
				// fees calculation
				if (!schdData.getFinFeeDetailList().isEmpty()) {
					schdData = FeeScheduleCalculator.feeSchdBuild(schdData);
				}
			}

			if (!schdData.getErrorDetails().isEmpty()) {
				return schdData;
			}

			// set LastMntBy , LastMntOn and status fields to schedule details
			for (FinanceScheduleDetail schdDetail : schdData.getFinanceScheduleDetails()) {
				schdDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				schdDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}

			// Reset Data
			fm.setEqualRepay(fm.isEqualRepay());
			fm.setRecalType(fm.getRecalType());
			fm.setLastRepayDate(fm.getFinStartDate());
			fm.setLastRepayPftDate(fm.getFinStartDate());
			fm.setLastRepayRvwDate(fm.getFinStartDate());
			fm.setLastRepayCpzDate(fm.getFinStartDate());

			fm.setFinRemarks("SUCCESS");

			// Finance detail object
			FinanceDetail fd = new FinanceDetail();
			fd.setFinScheduleData(schdData);
			fd.setUserAction("");
			fd.setExtSource(false);
			fd.setModuleDefiner(FinServiceEvent.ORG);

			if (fm.getCustID() > 0) {
				CustomerDetails custDetails = customerDetailsService.getApprovedCustomerById(fm.getCustID());
				fd.setCustomerDetails(custDetails);
			}

			schdData.setFinID(fm.getFinID());
			schdData.setFinReference(fm.getFinReference());

			doProcessPlanEMIHDays(schdData);

			if (schdData.getErrorDetails() != null) {
				for (ErrorDetail errorDetail : schdData.getErrorDetails()) {
					FinScheduleData response = new FinScheduleData();
					doEmptyResponseObject(response);
					response.setReturnStatus(
							APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
					return response;
				}
			}

			AuditDetail auditDetail = new AuditDetail(PennantConstants.TRAN_WF, 1, null, fd);
			AuditHeader auditHeader = new AuditHeader(schdData.getFinReference(), null, null, null, auditDetail,
					fm.getUserDetails(), new HashMap<String, List<ErrorDetail>>());
			// get the header details from the request
			APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
					.get(APIHeader.API_HEADER_KEY);
			// set the headerDetails to AuditHeader
			auditHeader.setApiHeader(reqHeaderDetails);
			// save the finance details into main table
			auditHeader = financeDetailService.doApprove(auditHeader, true);

			if (auditHeader.getOverideMessage() != null && auditHeader.getOverideMessage().size() > 0) {
				for (ErrorDetail ed : auditHeader.getOverideMessage()) {
					schdData = new FinScheduleData();
					doEmptyResponseObject(schdData);
					schdData.setReturnStatus(APIErrorHandlerService.getFailedStatus(ed.getCode(), ed.getError()));
					return schdData;
				}
			}
			if (auditHeader.getErrorMessage() != null) {
				for (ErrorDetail ed : auditHeader.getErrorMessage()) {
					schdData = new FinScheduleData();
					doEmptyResponseObject(schdData);
					schdData.setReturnStatus(APIErrorHandlerService.getFailedStatus(ed.getCode(), ed.getError()));
					return schdData;
				}
			}

			if (auditHeader.getAuditDetail().getErrorDetails() != null) {
				for (ErrorDetail ed : auditHeader.getAuditDetail().getErrorDetails()) {
					schdData = new FinScheduleData();
					doEmptyResponseObject(schdData);
					schdData.setReturnStatus(APIErrorHandlerService.getFailedStatus(ed.getCode(), ed.getError()));
					return schdData;
				}
			}

			schdData = getFinanceScheduleResponse(auditHeader);
			schdData.setReturnStatus(APIErrorHandlerService.getSuccessStatus());

			// for failure case logging purpose
			APIErrorHandlerService.logReference(schdData.getFinReference());

			logger.debug(Literal.LEAVING);
			return schdData;
		} catch (InterfaceException ex) {
			logger.error("InterfaceException", ex);
			schdData = new FinScheduleData();
			doEmptyResponseObject(schdData);
			schdData.setReturnStatus(APIErrorHandlerService.getFailedStatus("9999", ex.getMessage()));
			return schdData;
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION);
			APIErrorHandlerService.logUnhandledException(e);
			schdData = new FinScheduleData();
			doEmptyResponseObject(schdData);
			schdData.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			return schdData;
		}
	}

	private void doSetRequiredData(FinScheduleData schdData) throws AppException {
		logger.debug(Literal.ENTERING);

		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());

		for (VASRecording vasRecording : schdData.getVasRecordingList()) {
			vasRecording.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			vasRecording.setNewRecord(true);
			vasRecording.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			vasRecording.setVasReference(ReferenceUtil.generateVASRef());
			vasRecording.setPostingAgainst(VASConsatnts.VASAGAINST_FINANCE);
			vasRecording.setVasStatus("N");

			List<ExtendedField> extendedFields = vasRecording.getExtendedDetails();

			if (extendedFields != null) {
				int seqNo = 0;
				ExtendedFieldRender exdFieldRender = new ExtendedFieldRender();
				exdFieldRender.setReference(vasRecording.getVasReference());
				exdFieldRender.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				exdFieldRender.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				exdFieldRender.setLastMntBy(userDetails.getUserId());
				exdFieldRender.setSeqNo(++seqNo);
				exdFieldRender.setNewRecord(true);
				exdFieldRender.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				exdFieldRender.setVersion(1);
				for (ExtendedField extendedField : extendedFields) {
					Map<String, Object> mapValues = new HashMap<String, Object>();
					for (ExtendedFieldData extFieldData : extendedField.getExtendedFieldDataList()) {
						mapValues.put(extFieldData.getFieldName(), extFieldData.getFieldValue());
						exdFieldRender.setMapValues(mapValues);
					}
				}
				if (extendedFields.size() <= 0) {
					Map<String, Object> mapValues = new HashMap<String, Object>();
					exdFieldRender.setMapValues(mapValues);
				}

				vasRecording.setExtendedFieldRender(exdFieldRender);
			} else {
				ExtendedFieldRender exdFieldRender = new ExtendedFieldRender();
				exdFieldRender.setReference(vasRecording.getVasReference());
				exdFieldRender.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				exdFieldRender.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				exdFieldRender.setLastMntBy(userDetails.getUserId());
				exdFieldRender.setSeqNo(0);
				exdFieldRender.setNewRecord(true);
				exdFieldRender.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				exdFieldRender.setVersion(1);
				Map<String, Object> mapValues = new HashMap<String, Object>();
				exdFieldRender.setMapValues(mapValues);
				vasRecording.setExtendedFieldRender(exdFieldRender);
			}
		}
		FinanceMain fm = schdData.getFinanceMain();

		// user details

		fm.setUserDetails(userDetails);
		fm.setFinIsActive(true);
		fm.setVersion(1);
		fm.setLastMntBy(userDetails.getUserId());
		fm.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		fm.setFinStatus(FinanceConstants.FINSTSRSN_SYSTEM);

		fm.setMaturityDate(fm.getCalMaturity());
		fm.setNumberOfTerms(fm.getCalTerms());
		fm.setGrcPeriodEndDate(fm.getCalGrcEndDate());
		fm.setGraceTerms(fm.getCalGrcTerms());

		// set Head branch
		if (StringUtils.isBlank(fm.getFinBranch())) {
			fm.setFinBranch(userDetails.getBranchCode());
		}

		FinanceDetail fd = new FinanceDetail();
		fd.setFinScheduleData(schdData);

		if (fm.getCustID() > 0) {
			CustomerDetails custDetails = customerDetailsService.getApprovedCustomerById(fm.getCustID());
			fd.setCustomerDetails(custDetails);
		}

		// Set VAS reference as feeCode for VAS related fees
		for (FinFeeDetail fee : schdData.getFinFeeDetailList()) {
			for (VASRecording vasRecording : schdData.getVasRecordingList()) {
				if (StringUtils.equals(fee.getFinEvent(), AccountingEvent.VAS_FEE)
						&& StringUtils.contains(fee.getFeeTypeCode(), vasRecording.getProductCode())) {
					fee.setFeeTypeCode(vasRecording.getVasReference());
					fee.setVasReference(vasRecording.getVasReference());
					fee.setCalculatedAmount(vasRecording.getFee());
					fee.setFixedAmount(vasRecording.getFee());
					fee.setAlwDeviation(true);
					fee.setMaxWaiverPerc(BigDecimal.valueOf(100));
					// feeDetail.setAlwModifyFee(true);
					fee.setAlwModifyFeeSchdMthd(true);
					fee.setCalculationType(PennantConstants.FEE_CALCULATION_TYPE_FIXEDAMOUNT);
					// Fee Details set to the VasRecording
					vasRecording.setWaivedAmt(fee.getWaivedAmount());
					vasRecording.setPaidAmt(fee.getPaidAmount());
				}
			}
		}
		// fetch finType fees details
		String finEvent = "";
		boolean enquiry = true;
		if (CollectionUtils.isNotEmpty(schdData.getFinFeeDetailList())) {
			enquiry = false;
		}

		executeFeeCharges(fd, finEvent, enquiry);

		// Step Policy Details
		if (fm.isStepFinance() && PennantConstants.STEPPING_CALC_PERC.equals(fm.getCalcOfSteps())) {
			fm.setRpyStps(true);
			String stepPolicyCode = fm.getStepPolicy();
			if (StringUtils.isNotBlank(stepPolicyCode)) {
				List<StepPolicyDetail> stepPolicyList = stepPolicyDetailDAO.getStepPolicyDetailListByID(stepPolicyCode,
						"_AView");

				// reset step policy details
				schdData.resetStepPolicyDetails(stepPolicyList);

				schdData.getFinanceMain().setStepFinance(true);
				schdData.getFinanceMain().setStepPolicy(stepPolicyCode);

				// fetch stepHeader details
				StepPolicyHeader header = stepPolicyHeaderDAO.getStepPolicyHeaderByID(stepPolicyCode, "");
				if (header != null) {
					schdData.getFinanceMain().setStepType(header.getStepType());
				}

				List<FinanceStepPolicyDetail> finStepDetails = schdData.getStepPolicyDetails();

				// method for prepare step installments
				prepareStepInstallements(finStepDetails, fm.getNumberOfTerms());

			} else {
				List<FinanceStepPolicyDetail> finStepDetails = schdData.getStepPolicyDetails();
				Collections.sort(finStepDetails, new Comparator<FinanceStepPolicyDetail>() {
					@Override
					public int compare(FinanceStepPolicyDetail b1, FinanceStepPolicyDetail b2) {
						return (Integer.valueOf(b1.getStepNo()).compareTo(Integer.valueOf(b2.getStepNo())));
					}
				});
				// method for prepare step installments
				prepareStepInstallements(finStepDetails, fm.getNumberOfTerms());
			}
		} else if (fm.isStepFinance() && PennantConstants.STEPPING_CALC_AMT.equals(fm.getCalcOfSteps())) {

			List<FinanceStepPolicyDetail> finStepPoliciesList = schdData.getStepPolicyDetails();
			List<FinanceStepPolicyDetail> graceSpdList = new ArrayList<>();
			List<FinanceStepPolicyDetail> rpySpdList = new ArrayList<>();
			fm.setRpyStps(false);
			fm.setGrcStps(false);

			for (FinanceStepPolicyDetail spd : finStepPoliciesList) {
				if (PennantConstants.STEP_SPECIFIER_REG_EMI.equals(spd.getStepSpecifier())) {
					rpySpdList.add(spd);
					fm.setRpyStps(true);
				} else if (PennantConstants.STEP_SPECIFIER_GRACE.equals(spd.getStepSpecifier())) {
					graceSpdList.add(spd);
					fm.setGrcStps(true);
				}
			}
			prepareStepInstallements(rpySpdList, fm.getNumberOfTerms());
			prepareStepInstallements(graceSpdList, fm.getGraceTerms());
			schdData.setStepPolicyDetails(finStepPoliciesList, true);
		}

		schdData.getFinanceMain().setCalculateRepay(true);// FIXME: why this field

		// Disbursement details
		FinanceDisbursement disbursementDetails = new FinanceDisbursement();
		disbursementDetails.setDisbDate(fm.getFinStartDate());
		disbursementDetails.setDisbAmount(fm.getFinAmount());
		disbursementDetails.setDisbSeq(1);
		disbursementDetails.setVersion(1);
		disbursementDetails.setDisbReqDate(SysParamUtil.getAppDate());
		disbursementDetails.setFeeChargeAmt(fm.getFeeChargeAmt());
		schdData.getDisbursementDetails().add(disbursementDetails);

		logger.debug(Literal.LEAVING);
	}

	private void executeFeeCharges(FinanceDetail fd, String eventCode, boolean enquiry) throws AppException {
		FinScheduleData schData = fd.getFinScheduleData();

		if (CollectionUtils.isEmpty(schData.getFinFeeDetailList())) {
			if (StringUtils.isBlank(eventCode)) {
				eventCode = PennantApplicationUtil.getEventCode(schData.getFinanceMain().getFinStartDate());
			}
			feeDetailService.doProcessFeesForInquiry(fd, eventCode, null, enquiry);
		} else {
			feeDetailService.doExecuteFeeCharges(fd, eventCode, null, enquiry);
		}

		if (fd.isStp()) {
			for (FinFeeDetail feeDetail : schData.getFinFeeDetailList()) {
				feeDetail.setWorkflowId(0);
			}
		}
	}

	private void prepareStepInstallements(List<FinanceStepPolicyDetail> finStepDetails, int totalTerms) {
		logger.debug("Entering");

		int sumInstallments = 0;
		BigDecimal tenurePerc = BigDecimal.ZERO;
		BigDecimal sumTenurePerc = BigDecimal.ZERO;

		for (int i = 0; i < finStepDetails.size(); i++) {
			FinanceStepPolicyDetail detail = finStepDetails.get(i);

			if (detail.getInstallments() > 0) {
				tenurePerc = (new BigDecimal(detail.getInstallments()).multiply(new BigDecimal(100)))
						.divide(new BigDecimal(totalTerms), 2, RoundingMode.HALF_DOWN);
				detail.setTenorSplitPerc(tenurePerc);
				sumTenurePerc = sumTenurePerc.add(tenurePerc);
				sumInstallments = sumInstallments + detail.getInstallments();
				if (i == (finStepDetails.size() - 1) && sumInstallments == totalTerms) {
					if (sumTenurePerc.compareTo(new BigDecimal(100)) != 0) {
						detail.setTenorSplitPerc(
								detail.getTenorSplitPerc().add(new BigDecimal(100)).subtract(sumTenurePerc));
					}
				}
			} else {
				BigDecimal terms = detail.getTenorSplitPerc().multiply(new BigDecimal(totalTerms))
						.divide(new BigDecimal(100), 0, RoundingMode.HALF_DOWN);
				sumInstallments = sumInstallments + Integer.parseInt(terms.toString());
				detail.setInstallments(Integer.parseInt(terms.toString()));
				detail.setStepSpecifier(PennantConstants.STEP_SPECIFIER_REG_EMI);
				if (i == (finStepDetails.size() - 1)) {
					if (sumInstallments != totalTerms) {
						detail.setInstallments(detail.getInstallments() + totalTerms - sumInstallments);
					}
				}
			}
		}
		logger.debug(Literal.LEAVING);
	}

	private FinScheduleData getFinanceScheduleResponse(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		FinanceDetail fd = (FinanceDetail) auditHeader.getAuditDetail().getModelData();

		// fetch finance basic details
		FinanceMain fm = fd.getFinScheduleData().getFinanceMain();
		FinScheduleData schdData = new FinScheduleData();

		if (fm != null) {
			schdData.setFinID(fm.getFinID());
			schdData.setFinReference(fm.getFinReference());
			schdData.setFinFeeDetailList(fd.getFinScheduleData().getFinFeeDetailList());
			schdData.setStepPolicyDetails(fd.getFinScheduleData().getStepPolicyDetails());
			schdData.setFinanceScheduleDetails(fd.getFinScheduleData().getFinanceScheduleDetails());

			// set fee paid amounts based on schedule method
			schdData.setFinFeeDetailList(getUpdatedFees(schdData.getFinFeeDetailList()));

			// summary
			FinanceDetail response = new FinanceDetail();
			// used for AEAMOUNTS class
			response.setFinReference(fm.getFinReference());
			fm.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			response.getFinScheduleData().setFinanceMain(fm);
			response.getFinScheduleData()
					.setFinanceScheduleDetails(fd.getFinScheduleData().getFinanceScheduleDetails());
			schdData.setFinanceSummary(getFinanceSummary(response));
		}
		// to remove un-necessary objects from response make them as null
		schdData.setDisbursementDetails(null);
		schdData.setRepayInstructions(null);
		schdData.setRateInstruction(null);
		schdData.setFinODPenaltyRate(null);

		logger.debug(Literal.LEAVING);

		return schdData;
	}

	private void doEmptyResponseObject(FinScheduleData schdData) {
		schdData.setDisbursementDetails(null);
		schdData.setRepayInstructions(null);
		schdData.setRateInstruction(null);
		schdData.setFinFeeDetailList(null);
		schdData.setStepPolicyDetails(null);
		schdData.setFinanceScheduleDetails(null);
		schdData.setPlanEMIHDates(null);
		schdData.setPlanEMIHmonths(null);
		schdData.setApiPlanEMIHDates(null);
		schdData.setApiplanEMIHmonths(null);
		schdData.setFinODDetails(null);
	}

	public FinScheduleData getFinanceInquiryDetails(long finID, String type) {
		logger.debug(Literal.ENTERING);

		FinScheduleData schdData = new FinScheduleData();

		FinanceDetail fd = null;
		if (APIConstants.FINANCE_ORIGINATION.equals(type)) {
			fd = financeDetailService.getFinanceDetailById(finID, false, "", false, FinServiceEvent.ORG, "");
		} else {
			fd = financeDetailService.getWIFFinance(finID, false, null);
		}

		if (fd == null) {
			schdData.setReturnStatus(APIErrorHandlerService.getFailedStatus());

			return schdData;
		}

		try {

			schdData = fd.getFinScheduleData();
			FinanceMain fm = schdData.getFinanceMain();
			// setting Disb first and lastDates
			List<FinanceDisbursement> disbList = schdData.getDisbursementDetails();
			Collections.sort(disbList, new Comparator<FinanceDisbursement>() {
				@Override
				public int compare(FinanceDisbursement b1, FinanceDisbursement b2) {
					return (Integer.valueOf(b1.getDisbSeq()).compareTo(Integer.valueOf(b2.getDisbSeq())));
				}
			});

			if (disbList != null && disbList.size() > 0) {
				if (disbList.size() == 1) {
					fm.setFirstDisbDate(disbList.get(0).getDisbDate());
					fm.setLastDisbDate(disbList.get(0).getDisbDate());
				} else {
					fm.setFirstDisbDate(disbList.get(0).getDisbDate());
					fm.setLastDisbDate(disbList.get(disbList.size() - 1).getDisbDate());
				}
			}

			// Avoid Grace Period details into the marshaling in case of Allow grace is false
			if (!fm.isAllowGrcPeriod()) {
				fm.setGrcPeriodEndDate(null);
				fm.setGrcRateBasis(null);
				fm.setGrcPftRate(null);
				fm.setGraceBaseRate(null);
				fm.setGraceSpecialRate(null);
				fm.setGrcMargin(null);
				fm.setGrcProfitDaysBasis(null);
				fm.setGrcPftFrq(null);
				fm.setNextGrcPftDate(null);
				fm.setGrcPftRvwFrq(null);
				fm.setNextGrcPftRvwDate(null);
				fm.setGrcCpzFrq(null);
				fm.setNextGrcCpzDate(null);
				fm.setAllowGrcRepay(false);
				fm.setGrcSchdMthd(null);
				fm.setGrcMinRate(null);
				fm.setGrcMaxRate(null);
			}

			schdData.setFinFeeDetailList(getUpdatedFees(schdData.getFinFeeDetailList()));
			schdData.setFinanceSummary(getFinanceSummary(fd));
			schdData.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
			schdData.setFinODDetails(finODDetailsDAO.getFinODDByFinRef(finID, null));

			schdData.setDisbursementDetails(null);
			schdData.setRepayInstructions(null);
			schdData.setRateInstruction(null);
			schdData.setFinODPenaltyRate(null);

		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
			APIErrorHandlerService.logUnhandledException(e);
			schdData.setReturnStatus(APIErrorHandlerService.getFailedStatus());
		}

		logger.debug(Literal.LEAVING);
		return schdData;
	}

	public EmiResponse getEMIAmount(FinanceDetail fd) {
		logger.debug(Literal.ENTERING);
		EmiResponse response = new EmiResponse();
		try {
			FinScheduleData schdData = fd.getFinScheduleData();
			FinanceMain fm = schdData.getFinanceMain();

			fm.setMaturityDate(fm.getCalMaturity());

			doSetRequiredData(schdData);

			List<Integer> planEMIHmonths = new ArrayList<>();
			if (CollectionUtils.isNotEmpty(schdData.getApiPlanEMIHmonths()) && StringUtils
					.equals(schdData.getFinanceMain().getPlanEMIHMethod(), FinanceConstants.PLANEMIHMETHOD_FRQ)) {
				for (FinPlanEmiHoliday detail : schdData.getApiPlanEMIHmonths()) {
					planEMIHmonths.add(detail.getPlanEMIHMonth());
				}
			}

			schdData.setPlanEMIHmonths(planEMIHmonths);

			BigDecimal repayAmount = ScheduleCalculator.getEMIOnFinAssetValue(fd.getFinScheduleData());

			if (repayAmount.compareTo(BigDecimal.ZERO) > 0) {
				response.setRepayAmount(repayAmount);
				response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			return response;
		}

		logger.debug(Literal.LEAVING);

		return response;

	}

	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	public void setFeeDetailService(FeeDetailService feeDetailService) {
		this.feeDetailService = feeDetailService;
	}

	@Autowired
	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}

}
