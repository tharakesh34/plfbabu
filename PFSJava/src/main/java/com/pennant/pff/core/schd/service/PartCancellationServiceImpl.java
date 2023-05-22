package com.pennant.pff.core.schd.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;

import com.pennant.app.util.APIHeader;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SessionUserDetails;
import com.pennant.app.util.SysParamUtil;
import com.pennant.app.util.TDSCalculator;
import com.pennant.backend.dao.finance.FinServiceInstrutionDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.collateral.CollateralAssignment;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.finance.FinAssetTypes;
import com.pennant.backend.model.finance.FinCollaterals;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDeviations;
import com.pennant.backend.model.finance.FinanceEligibilityDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceStepPolicyDetail;
import com.pennant.backend.model.finance.GuarantorDetail;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.model.lmtmasters.FinanceCheckListReference;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.external.service.PartCancellationSchd;

public class PartCancellationServiceImpl extends GenericService<FinServiceInstruction>
		implements PartCancellationService {
	private static final Logger logger = LogManager.getLogger(PartCancellationServiceImpl.class);

	private PartCancellationSchd partCancellationSchd;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private FinanceDetailService financeDetailService;
	private FinServiceInstrutionDAO finServiceInstructionDAO;

	/**
	 * Default constructor
	 */
	public PartCancellationServiceImpl() {
		super();
	}

	public FinanceDetail getFinanceDetails(FinServiceInstruction fsi, String eventCode) {
		logger.debug(Literal.ENTERING);

		FinanceDetail financeDetail = null;

		Long finID = fsi.getFinID();
		if (!fsi.isWif()) {
			financeDetail = financeDetailService.getFinanceDetailById(finID, false, "", false, FinServiceEvent.ORG, "");
		} else {
			financeDetail = financeDetailService.getWIFFinance(finID, false, null);
		}

		List<FinFeeDetail> newList = new ArrayList<FinFeeDetail>();
		if (financeDetail != null) {
			if (financeDetail.getFinScheduleData().getFinFeeDetailList() != null) {
				for (FinFeeDetail feeDetail : financeDetail.getFinScheduleData().getFinFeeDetailList()) {
					if (feeDetail.isOriginationFee()) {
						feeDetail.setOriginationFee(true);
						feeDetail.setRcdVisible(false);
						feeDetail.setRecordType(PennantConstants.RCD_UPD);
						feeDetail.setRecordStatus(PennantConstants.RECORD_TYPE_UPD);
						newList.add(feeDetail);
					}
				}
			}
			financeDetail.getFinScheduleData().setFinFeeDetailList(newList);
			financeDetail.setAccountingEventCode(eventCode);
			LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
			financeDetail.getFinScheduleData().getFinanceMain().setUserDetails(userDetails);
		}

		logger.debug(Literal.LEAVING);
		return financeDetail;
	}

	/**
	 * 
	 * @param finServiceInst
	 * @param financeDetail
	 * @param eventCode
	 * @return
	 */
	@Override
	public FinanceDetail doPartCancellation(FinServiceInstruction finServiceInst, FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);

		if (financeDetail != null) {
			FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
			FinanceMain financeMain = finScheduleData.getFinanceMain();

			financeMain.setEventFromDate(finServiceInst.getValueDate());
			financeMain.setEventToDate(financeMain.getMaturityDate());
			financeMain.setRecalSchdMethod(financeMain.getScheduleMethod());
			financeMain.setRecalType(finServiceInst.getRecalType());
			financeMain.setFinSourceID("API");
			financeMain.setMiscAmount(finServiceInst.getRefund());
			financeMain.setRcdMaintainSts(FinServiceEvent.PART_CANCELLATION);
			financeDetail.setModuleDefiner(FinServiceEvent.PART_CANCELLATION);

			// Call Schedule calculator for part cancellation
			if (finScheduleData.getErrorDetails() == null || finScheduleData.getErrorDetails().isEmpty()) {
				finScheduleData = partCancellationSchd.partDisbCancel(finScheduleData);
				financeDetail.setFinScheduleData(finScheduleData);
			}

			List<FinanceScheduleDetail> fsdList = finScheduleData.getFinanceScheduleDetails();
			financeMain.setMaturityDate(fsdList.get(fsdList.size() - 1).getSchDate());

		}

		logger.debug(Literal.LEAVING);
		return financeDetail;
	}

	@Override
	public void postPartCancellation(FinServiceInstruction finServiceInst, FinanceDetail financeDetail) {
		if (StringUtils.equals(finServiceInst.getReqType(), "Post")) {

			int version = financeDetail.getFinScheduleData().getFinanceMain().getVersion();
			financeDetail.getFinScheduleData().getFinanceMain().setVersion(version + 1);
			financeDetail.getFinScheduleData().setSchduleGenerated(true);
			financeDetail.setUserDetails(SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser()));

			AuditHeader auditHeader = getAuditHeader(financeDetail, PennantConstants.TRAN_WF);
			FinanceDetail aFinanceDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();

			aFinanceDetail = prepareInstructionObject(aFinanceDetail);

			// get the header details from the request
			APIHeader reqHeaderDetails = new APIHeader();
			auditHeader.setApiHeader(reqHeaderDetails);

			try {
				auditHeader = financeDetailService.doApprove(auditHeader, finServiceInst.isWif());
			} catch (Exception e) {
				logger.debug(e);
			}

			// financeScheduleDetailDAO.saveList(fsdList, "_Log", false);
			// financeScheduleDetailDAO.deleteByFinReference(finReference, "", false, 0);
			// financeScheduleDetailDAO.saveList(fsdList, "", false);
			// update finance main
			// save repay instructions
			// save service instructions
		}
	}

	private FinanceDetail prepareInstructionObject(FinanceDetail aFinanceDetail) {
		logger.debug(Literal.ENTERING);

		FinScheduleData finScheduleData = aFinanceDetail.getFinScheduleData();
		finScheduleData.getFinanceMain().setRecordType("");

		finScheduleData.setStepPolicyDetails(new ArrayList<FinanceStepPolicyDetail>(1));
		finScheduleData.setFinODPenaltyRate(null);
		finScheduleData.setFeeRules(new ArrayList<FeeRule>());

		aFinanceDetail.setTatDetail(null);
		aFinanceDetail.setFinanceCheckList(new ArrayList<FinanceCheckListReference>(1));
		aFinanceDetail.setCheckList(new ArrayList<FinanceReferenceDetail>(1));
		aFinanceDetail.setAggrementList(new ArrayList<FinanceReferenceDetail>(1));
		aFinanceDetail.setEligibilityRuleList(new ArrayList<FinanceReferenceDetail>(1));
		aFinanceDetail.setFinElgRuleList(new ArrayList<FinanceEligibilityDetail>(1));
		aFinanceDetail.setGurantorsDetailList(new ArrayList<GuarantorDetail>(1));
		aFinanceDetail.setJointAccountDetailList(new ArrayList<JointAccountDetail>(1));
		aFinanceDetail.setFinanceDeviations(new ArrayList<FinanceDeviations>());
		aFinanceDetail.setApprovedFinanceDeviations(new ArrayList<FinanceDeviations>());
		aFinanceDetail.setFinanceCollaterals(new ArrayList<FinCollaterals>(1));
		aFinanceDetail.setCollateralAssignmentList(new ArrayList<CollateralAssignment>(1));
		aFinanceDetail.setFinAssetTypesList(new ArrayList<FinAssetTypes>(1));
		aFinanceDetail.setExtendedFieldRenderList(new ArrayList<ExtendedFieldRender>(1));
		finScheduleData.setVasRecordingList(new ArrayList<VASRecording>(1));
		aFinanceDetail.setCovenantTypeList(null);
		aFinanceDetail.setMandate(null);
		aFinanceDetail.setFinFlagsDetails(null);

		logger.debug(Literal.LEAVING);
		return aFinanceDetail;
	}

	protected AuditHeader getAuditHeader(FinanceDetail afinanceDetail, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, afinanceDetail.getBefImage(), afinanceDetail);
		return new AuditHeader(afinanceDetail.getFinScheduleData().getFinReference(), null, null, null, auditDetail,
				afinanceDetail.getUserDetails(), new HashMap<String, List<ErrorDetail>>());
	}

	/**
	 * 
	 * @param fsi
	 * @param fd
	 * @return auditDetail
	 */
	public AuditDetail validateRequest(FinServiceInstruction fsi, FinanceDetail fd) {
		AuditDetail auditDetail = new AuditDetail();
		String lang = "EN";

		// validate Instruction details
		long finID = fsi.getFinID();
		String finReference = fsi.getFinReference();

		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		/**
		 * Checking whether the finance details are available or not for the specified fin reference.
		 */
		if (fm == null) {
			String[] valueParm = new String[4];
			valueParm[0] = "Fin Reference : ";
			valueParm[1] = finReference;
			valueParm[2] = "is does not exist's";
			valueParm[3] = "";
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("30550", "", valueParm), lang));
			return auditDetail;
		}

		/**
		 * Checking whether the specified fin reference is in active or not.
		 */
		if (!fm.isFinIsActive()) {
			String[] valueParm = new String[4];
			valueParm[0] = "Fin Reference:";
			valueParm[1] = finReference;
			valueParm[2] = "is not active";
			valueParm[3] = "";
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("30550", "", valueParm), lang));
			return auditDetail;
		}

		/**
		 * Checking whether TDS Applicable or not.If Loan have TDSApplicable not applicable part cancellation
		 */
		if (TDSCalculator.isTDSApplicable(fm)) {
			String[] valueParm = new String[2];
			valueParm[0] = "Part Cancellation";
			valueParm[1] = "TDS Applicable Loans";
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90329", "", valueParm), lang));
			return auditDetail;
		}

		/**
		 * Checking whether EMI Holiday or not.If loan has EMI Holiday not applicable part cancellation.
		 */
		if (fm.isPlanEMIHAlw()) {
			String[] valueParm = new String[2];
			valueParm[0] = "Part Cancellation";
			valueParm[1] = "EMI Holiday Loans";
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90329", "", valueParm), lang));
			return auditDetail;
		}

		/**
		 * Checking whether the Refund Amount is greater than 0.
		 */
		BigDecimal refundAmount = fsi.getRefund();
		Date refundDate = fsi.getValueDate();

		if (refundAmount == null) {
			String[] valueParm = new String[1];
			valueParm[0] = Labels.getLabel("label_PartCancellation_RefundAmount.value");
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm), lang));
			return auditDetail;
		}

		if (refundAmount.compareTo(BigDecimal.ZERO) == 0) {
			String[] valueParm = new String[2];
			valueParm[0] = Labels.getLabel("label_PartCancellation_RefundAmount.value");
			valueParm[1] = "Zero";
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91121", "", valueParm), lang));
			return auditDetail;
		}

		/**
		 * Checking whether Refund Amount is less than Finance Amount.
		 */
		if (refundAmount.compareTo(fm.getFinAmount()) > 0) {
			String[] valueParm = new String[2];
			valueParm[0] = Labels.getLabel("label_PartCancellation_RefundAmount.value") + " : "
					+ PennantApplicationUtil.amountFormate(refundAmount, 2);
			valueParm[1] = "Finance Amount : " + PennantApplicationUtil.amountFormate(fm.getFinAmount(), 2);
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("30508", "", valueParm), lang));
			return auditDetail;
		}

		/**
		 * Checking whether the Refund Date is given or not.
		 */
		if (refundDate == null) {
			String[] valueParm = new String[4];
			valueParm[0] = Labels.getLabel("label_PartCancellation_RefundDate.value") + " is Mandatory";
			valueParm[1] = "";
			valueParm[2] = "";
			valueParm[3] = "";
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("30550", "", valueParm), lang));
			return auditDetail;
		}

		/**
		 * Checking Refund date is not beyond app date.
		 */
		Date appDate = SysParamUtil.getAppDate();
		if (refundDate.compareTo(appDate) > 0) {
			String[] valueParm = new String[2];
			valueParm[0] = Labels.getLabel("label_PartCancellation_RefundDate.value");
			valueParm[1] = "Application date";
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("65029", "", valueParm), lang));
			return auditDetail;
		}

		/**
		 * Checking whether the Refund Date should be in between finance start date and maturity date.
		 */
		if (refundDate.compareTo(fm.getFinStartDate()) < 0 || refundDate.compareTo(fm.getMaturityDate()) >= 0) {
			String[] valueParm = new String[3];
			valueParm[0] = Labels.getLabel("label_PartCancellation_RefundDate.value");
			valueParm[1] = "finance start date: " + DateUtil.formatToShortDate(fm.getFinStartDate());
			valueParm[2] = "maturity date:" + DateUtil.formatToShortDate(fm.getMaturityDate());
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90318", "", valueParm), lang));
			return auditDetail;
		}

		/**
		 * Checking whether the part cancellation is on same date should be not be allowed more than once.
		 */
		Date eventDate = fsi.getFromDate();
		String eventCode = FinServiceEvent.PART_CANCELLATION;
		List<FinServiceInstruction> serviceInstructions;
		serviceInstructions = finServiceInstructionDAO.getFinServiceInstAddDisbDetail(finID, eventDate, eventCode);
		if (serviceInstructions != null && serviceInstructions.size() > 0) {
			String[] valueParm = new String[4];
			valueParm[0] = Labels.getLabel("label_PartCancellation_Valid_SameDate");
			valueParm[1] = "";
			valueParm[2] = "";
			valueParm[3] = "";
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("30550", "", valueParm), lang));
			return auditDetail;
		}

		/**
		 * Checking whether refund date should be greater than last month end date.
		 */
		Date rfDate = DateUtil.addMonths(appDate, -1);
		Date lastMonthEndDate = DateUtil.getMonthEnd(rfDate);

		if (refundDate.compareTo(lastMonthEndDate) < 0) {
			String[] valueParm = new String[2];
			valueParm[0] = Labels.getLabel("label_PartCancellation_RefundDate.value");
			valueParm[1] = "Last month end date:" + DateUtil.formatToShortDate(lastMonthEndDate);
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91125", "", valueParm), lang));
			return auditDetail;
		}

		/**
		 * Checking whether refund date should be greater than last due date.
		 */
		List<FinanceScheduleDetail> fsdList = schdData.getFinanceScheduleDetails();
		Date lastDueDate = null;
		FinanceScheduleDetail financeScheduleDetail = null;
		// ArrayList<FinanceScheduleDetail> afsdList = new ArrayList<FinanceScheduleDetail>();
		for (int iFsd = 0; iFsd < fsdList.size(); iFsd++) {
			FinanceScheduleDetail fsd = fsdList.get(iFsd);
			Date schdate = fsd.getSchDate();
			if (schdate.compareTo(appDate) < 0) {
				// afsdList.add(fsd);
				financeScheduleDetail = fsd;
			}
		}
		// lastDueDate = afsdList.get(afsdList.size() - 1).getSchDate();

		if (financeScheduleDetail != null) {
			lastDueDate = financeScheduleDetail.getSchDate();
			if (lastDueDate != null && refundDate.compareTo(lastDueDate) < 0) {
				String[] valueParm = new String[2];
				valueParm[0] = Labels.getLabel("label_PartCancellation_RefundDate.value");
				valueParm[1] = "Last due date :" + DateUtil.formatToShortDate(lastDueDate);
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("30509", "", valueParm), lang));
				return auditDetail;
			}
			/**
			 * Checking whether refund amount should not be greater than closing balance.
			 */
			BigDecimal closingBalance = financeScheduleDetail.getClosingBalance();

			if (refundAmount.compareTo(closingBalance) > 0) {
				String[] valueParm = new String[2];
				valueParm[0] = Labels.getLabel("label_PartCancellation_RefundAmount.value") + ": "
						+ PennantApplicationUtil.amountFormate(refundAmount, 2);
				valueParm[1] = "Outstanding balance:" + PennantApplicationUtil.amountFormate(closingBalance, 2);
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("30508", "", valueParm)));
			}
		}
		return auditDetail;

	}

	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	@Autowired(required = false)
	public void setPartCancellationSchd(PartCancellationSchd partCancellationSchd) {
		this.partCancellationSchd = partCancellationSchd;
	}

	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	public void setFinServiceInstructionDAO(FinServiceInstrutionDAO finServiceInstructionDAO) {
		this.finServiceInstructionDAO = finServiceInstructionDAO;
	}
}
