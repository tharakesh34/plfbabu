package com.pennant.backend.service.finance.impl;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.security.auth.login.AccountNotFoundException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.AEAmounts;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.finance.FinFlagDetailsDAO;
import com.pennant.backend.dao.finance.FinanceTaxDetailDAO;
import com.pennant.backend.dao.finance.FinanceWriteoffDAO;
import com.pennant.backend.dao.finance.GuarantorDetailDAO;
import com.pennant.backend.dao.finance.JountAccountDetailDAO;
import com.pennant.backend.dao.lmtmasters.FinanceReferenceDetailDAO;
import com.pennant.backend.dao.mandate.MandateDAO;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.collateral.CollateralAssignment;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.FinCollaterals;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinFeeScheduleDetail;
import com.pennant.backend.model.finance.FinODPenaltyRate;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinWriteoffPayment;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.GuarantorDetail;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.model.financemanagement.FinFlagsDetail;
import com.pennant.backend.model.lmtmasters.FinanceCheckListReference;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.service.collateral.impl.FlagDetailValidation;
import com.pennant.backend.service.finance.FinanceMaintenanceService;
import com.pennant.backend.service.finance.GenericFinanceDetailService;
import com.pennant.backend.service.finance.validation.FinGuarantorDetailValidation;
import com.pennant.backend.service.finance.validation.FinJointAccountDetailValidation;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pff.core.TableType;
import com.rits.cloning.Cloner;

public class FinanceMaintenanceServiceImpl extends GenericFinanceDetailService implements FinanceMaintenanceService {

	private static final Logger			logger	= Logger.getLogger(FinanceMaintenanceServiceImpl.class);

	private FinanceReferenceDetailDAO	financeReferenceDetailDAO;
	private FinanceWriteoffDAO			financeWriteoffDAO;
	private GuarantorDetailDAO			guarantorDetailDAO;
	private JountAccountDetailDAO		jountAccountDetailDAO;
	private FlagDetailValidation		flagDetailValidation;
	private FinJointAccountDetailValidation		finJointAccountDetailValidation;
	private FinGuarantorDetailValidation		finGuarantorDetailValidation;
	private FinFlagDetailsDAO			finFlagDetailsDAO;
	private MandateDAO					mandateDAO;
	private FinanceTaxDetailDAO financeTaxDetailDAO;

	public FinanceMaintenanceServiceImpl() {
		super();
	}

	/**
	 * Method for Fetching FInance Details & Repay Schedule Details
	 * 
	 * @param finReference
	 * @return
	 */
	@Override
	public FinanceDetail getFinanceDetailById(String finReference, String type, String userRole, String procEdtEvent,
			String eventCode) {
		logger.debug("Entering");

		//Finance Details
		FinanceDetail financeDetail = new FinanceDetail();
		FinScheduleData scheduleData = financeDetail.getFinScheduleData();
		scheduleData.setFinReference(finReference);
		FinanceMain finMain = getFinanceMainDAO().getFinanceMainById(finReference, type, false);
		scheduleData.setFinanceMain(getFinanceMainDAO().getFinanceMainById(finReference, type, false));
		scheduleData.setFinanceType(getFinanceTypeDAO().getFinanceTypeByID(scheduleData.getFinanceMain().getFinType(),
				"_AView"));

		//Finance Schedule Details
		scheduleData.setFinanceScheduleDetails(getFinanceScheduleDetailDAO().getFinScheduleDetails(finReference, type,
				false));
		

		// Finance Disbursement details
		scheduleData.setDisbursementDetails(getFinanceDisbursementDAO().getFinanceDisbursementDetails(finReference,
				type, false));

		// Fee Details
		scheduleData.setFinFeeDetailList(getFinFeeDetailDAO().getFinFeeDetailByFinRef(finReference, false, "_View"));

		// Finance Fee Schedule Details
		if (scheduleData.getFinFeeDetailList() != null && !scheduleData.getFinFeeDetailList().isEmpty()) {

			List<Long> feeIDList = new ArrayList<>();
			for (int i = 0; i < scheduleData.getFinFeeDetailList().size(); i++) {
				FinFeeDetail feeDetail = scheduleData.getFinFeeDetailList().get(i);

				if (StringUtils.equals(feeDetail.getFeeScheduleMethod(),
						CalculationConstants.REMFEE_SCHD_TO_FIRST_INSTALLMENT)
						|| StringUtils.equals(feeDetail.getFeeScheduleMethod(),
								CalculationConstants.REMFEE_SCHD_TO_N_INSTALLMENTS)
						|| StringUtils.equals(feeDetail.getFeeScheduleMethod(),
								CalculationConstants.REMFEE_SCHD_TO_ENTIRE_TENOR)) {
					feeIDList.add(feeDetail.getFeeID());
				}
			}

			if (!feeIDList.isEmpty()) {
				List<FinFeeScheduleDetail> feeScheduleList = getFinFeeScheduleDetailDAO().getFeeScheduleByFinID(
						feeIDList, false, "");

				if (feeScheduleList != null && !feeScheduleList.isEmpty()) {

					HashMap<Long, List<FinFeeScheduleDetail>> schFeeMap = new HashMap<>();
					for (int i = 0; i < feeScheduleList.size(); i++) {
						FinFeeScheduleDetail schdFee = feeScheduleList.get(i);

						List<FinFeeScheduleDetail> schList = new ArrayList<>();
						if (schFeeMap.containsKey(schdFee.getFeeID())) {
							schList = schFeeMap.get(schdFee.getFeeID());
							schFeeMap.remove(schdFee.getFeeID());
						}
						schList.add(schdFee);
						schFeeMap.put(schdFee.getFeeID(), schList);

					}

					for (int i = 0; i < scheduleData.getFinFeeDetailList().size(); i++) {
						FinFeeDetail feeDetail = scheduleData.getFinFeeDetailList().get(i);
						if (schFeeMap.containsKey(feeDetail.getFeeID())) {
							feeDetail.setFinFeeScheduleDetailList(schFeeMap.get(feeDetail.getFeeID()));
						}
					}
				}
			}
		}
		//Finance Customer Details			
		if (scheduleData.getFinanceMain().getCustID() != 0
				&& scheduleData.getFinanceMain().getCustID() != Long.MIN_VALUE) {
			financeDetail.setCustomerDetails(getCustomerDetailsService().getCustomerDetailsById(
					scheduleData.getFinanceMain().getCustID(), true, "_View"));
		}

		//Finance Agreement Details	
		//=======================================
		String finType = scheduleData.getFinanceType().getFinType();
		financeDetail.setAggrementList(getAgreementDetailService().getAggrementDetailList(finType, procEdtEvent,
				userRole));

		// Finance Check List Details 
		//=======================================
		getCheckListDetailService().setFinanceCheckListDetails(financeDetail, finType, procEdtEvent, userRole);

		//Finance Fee Charge Details
		//=======================================
		List<Long> accSetIdList = new ArrayList<Long>();
		accSetIdList.addAll(getFinanceReferenceDetailDAO()
				.getRefIdListByFinType(finType, procEdtEvent, null, "_ACView"));
		if (!accSetIdList.isEmpty()) {
			financeDetail.setFeeCharges(getTransactionEntryDAO().getListFeeChargeRules(accSetIdList, eventCode,
					"_AView", 0));
		}

		//Finance Flag Details
		financeDetail.setFinFlagsDetails(getFinFlagDetailsDAO().getFinFlagsByFinRef(finReference,
				FinanceConstants.MODULE_NAME, "_View"));

		//Finance Stage Accounting Posting Details 
		//=======================================
		financeDetail.setStageTransactionEntries(getTransactionEntryDAO().getListTransactionEntryByRefType(finType,
				StringUtils.isEmpty(procEdtEvent) ? FinanceConstants.FINSER_EVENT_ORG : procEdtEvent,
				FinanceConstants.PROCEDT_STAGEACC, userRole, "_AEView", true));

		//Finance Guaranteer Details		
		if (StringUtils.equals(procEdtEvent, FinanceConstants.FINSER_EVENT_BASICMAINTAIN)) {
			financeDetail.setGurantorsDetailList(getGuarantorDetailService().getGuarantorDetail(finReference, "_View"));

			//Finance Joint Account Details
			financeDetail.setJountAccountDetailList(getJointAccountDetailService().getJoinAccountDetail(finReference,
					"_View"));

			// Collateral Details
			if (ImplementationConstants.COLLATERAL_INTERNAL) {
				financeDetail.setCollateralAssignmentList(getCollateralAssignmentDAO().getCollateralAssignmentByFinRef(
						finReference, FinanceConstants.MODULE_NAME, "_View"));
			} else {
				financeDetail.setFinanceCollaterals(getFinCollateralService().getFinCollateralsByRef(finReference,
						"_View"));
			}
		}

		//Mandate
		financeDetail.setMandate(mandateDAO.getMandateById(finMain.getMandateID(), ""));

		//Finance Overdue Penalty Rate Details
		scheduleData.setFinODPenaltyRate(getFinODPenaltyRateDAO().getFinODPenaltyRateByRef(finReference, type));

		// Document Details
		financeDetail.setDocumentDetailsList(getDocumentDetailsDAO().getDocumentDetailsByRef(finReference,
				FinanceConstants.MODULE_NAME, procEdtEvent, "_View"));

		if (StringUtils.equals(procEdtEvent, FinanceConstants.FINSER_EVENT_WRITEOFFPAY)) {
			if (StringUtils.isNotBlank(scheduleData.getFinanceMain().getRecordType())) {
				financeDetail.setFinwriteoffPayment(getFinanceWriteoffDAO().getFinWriteoffPaymentById(finReference,
						"_Temp"));
			}
			if (financeDetail.getFinwriteoffPayment() == null) {
				FinWriteoffPayment finwriteoffPay = new FinWriteoffPayment();
				financeDetail.setFinwriteoffPayment(finwriteoffPay);
			}
			financeDetail.getFinwriteoffPayment().setWriteoffAmount(
					getFinanceWriteoffDAO().getTotalFinWriteoffDetailAmt(finReference));
			financeDetail.getFinwriteoffPayment().setWriteoffDate(
					getFinanceWriteoffDAO().getFinWriteoffDate(finReference));
			financeDetail.getFinwriteoffPayment().setWriteoffPaidAmount(
					getFinanceWriteoffDAO().getTotalWriteoffPaymentAmount(finReference));
		}

		logger.debug("Leaving");
		return financeDetail;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table FinanceMain/FinanceMain_Temp by
	 * using FinanceMainDAO's save method b) Update the Record in the table. based on the module workFlow Configuration.
	 * by using FinanceMainDAO's update method 3) Audit the record in to AuditHeader and AdtFinanceMain by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 * @throws AccountNotFoundException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader aAuditHeader) throws InterfaceException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		aAuditHeader = businessValidation(aAuditHeader, "saveOrUpdate");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		if (!aAuditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return aAuditHeader;
		}

		Cloner cloner = new Cloner();
		AuditHeader auditHeader = cloner.deepClone(aAuditHeader);

		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

		//Finance Stage Accounting Process
		//=======================================
		if (financeDetail.getStageAccountingList() != null && financeDetail.getStageAccountingList().size() > 0) {

			List<ReturnDataSet> list = new ArrayList<ReturnDataSet>();
			auditHeader = executeStageAccounting(auditHeader, list);
			if (auditHeader.getErrorMessage() != null && auditHeader.getErrorMessage().size() > 0) {
				return auditHeader;
			}
			list = null;
		}

		TableType tableType = TableType.MAIN_TAB;
		if (financeMain.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}
		if (tableType == TableType.MAIN_TAB) {
			financeMain.setRcdMaintainSts("");
		}

		//Finance Penalty OD Rate Details
		FinODPenaltyRate penaltyRate = financeDetail.getFinScheduleData().getFinODPenaltyRate();
		FinWriteoffPayment finWriteoffPay = financeDetail.getFinwriteoffPayment();
		if (penaltyRate == null) {

			penaltyRate = new FinODPenaltyRate();
			penaltyRate.setApplyODPenalty(false);
			penaltyRate.setODIncGrcDays(false);
			penaltyRate.setODChargeType("");
			penaltyRate.setODChargeAmtOrPerc(BigDecimal.ZERO);
			penaltyRate.setODChargeCalOn("");
			penaltyRate.setODGraceDays(0);
			penaltyRate.setODAllowWaiver(false);
			penaltyRate.setODMaxWaiverPerc(BigDecimal.ZERO);
		}
		penaltyRate.setFinReference(financeMain.getFinReference());
		penaltyRate.setFinEffectDate(DateUtility.getSysDate());

		// Finance Main Details Save And Update
		//=======================================
		if (financeMain.isNew()) {
			getFinanceMainDAO().save(financeMain, tableType, false);

			//Finance Penalty OD Rate Details
			getFinODPenaltyRateDAO().save(penaltyRate, tableType.getSuffix());

			if (finWriteoffPay != null) {
				finWriteoffPay.setFinReference(financeMain.getFinReference());
				long seqNo = getFinanceWriteoffDAO().getfinWriteoffPaySeqNo(finWriteoffPay.getFinReference(), "");
				finWriteoffPay.setSeqNo(seqNo + 1);
				getFinanceWriteoffDAO().saveFinWriteoffPayment(finWriteoffPay, "_Temp");
			}
		} else {
			getFinanceMainDAO().update(financeMain, tableType, false);

			//Finance Penalty OD Rate Details
			if (tableType == TableType.MAIN_TAB) {
				FinODPenaltyRate oldPenaltyRate = getFinODPenaltyRateDAO().getFinODPenaltyRateByRef(
						financeMain.getFinReference(), "");
				getFinODPenaltyRateDAO().saveLog(oldPenaltyRate, "_Log");

				getFinODPenaltyRateDAO().delete(financeMain.getFinReference(), "");
				getFinODPenaltyRateDAO().save(penaltyRate, tableType.getSuffix());
				getFinODPenaltyRateDAO().delete(financeMain.getFinReference(), "_Temp");
			} else {
				getFinODPenaltyRateDAO().update(penaltyRate, tableType.getSuffix());
			}

			if (finWriteoffPay != null) {
				getFinanceWriteoffDAO().updateFinWriteoffPayment(finWriteoffPay, "_Temp");
			}

		}

		// Save Fee Charges List
		//=======================================
		if (tableType == TableType.TEMP_TAB) {
			getFinFeeChargesDAO().deleteChargesBatch(financeMain.getFinReference(), financeDetail.getModuleDefiner(),
					false, tableType.getSuffix());
		}
		saveFeeChargeList(financeDetail.getFinScheduleData(), financeDetail.getModuleDefiner(), false,
				tableType.getSuffix());

		// set Finance Check List audit details to auditDetails
		//=======================================
		if (financeDetail.getFinanceCheckList() != null && !financeDetail.getFinanceCheckList().isEmpty()) {
			auditDetails.addAll(getCheckListDetailService().saveOrUpdate(financeDetail, tableType.getSuffix()));
		}

		// set Guarantor Details Audit
		//=======================================
		//String auditTranType = auditHeader.getAuditTranType();
		if (financeDetail.getGurantorsDetailList() != null && !financeDetail.getGurantorsDetailList().isEmpty()
				&& financeDetail.getGurantorsDetailList().size() > 0) {
			financeDetail.setGurantorsDetailList(financeDetail.getGurantorsDetailList());
			List<AuditDetail> details = financeDetail.getAuditDetailMap().get("GuarantorDetails");
			details = processingGuarantorDetailList(details, tableType.getSuffix(), financeMain.getFinReference());
			auditDetails.addAll(details);
		}

		// set JountAccount Details Audit
		//=======================================

		if (financeDetail.getJountAccountDetailList() != null && !financeDetail.getJountAccountDetailList().isEmpty()
				&& financeDetail.getJountAccountDetailList().size() > 0) {
			financeDetail.setJountAccountDetailList(financeDetail.getJountAccountDetailList());
			List<AuditDetail> details = financeDetail.getAuditDetailMap().get("JointAccountDetails");
			details = processingJointAccountDetailList(details, tableType.getSuffix(), financeMain.getFinReference());
			auditDetails.addAll(details);
		}

		// Save Document Details
		if (financeDetail.getDocumentDetailsList() != null && financeDetail.getDocumentDetailsList().size() > 0) {
			List<AuditDetail> details = financeDetail.getAuditDetailMap().get("DocumentDetails");
			details = processingDocumentDetailsList(details, tableType.getSuffix(), financeDetail.getFinScheduleData()
					.getFinanceMain(), financeDetail.getModuleDefiner());
			auditDetails.addAll(details);
		}

		if (ImplementationConstants.COLLATERAL_INTERNAL) {
			// set Finance Collateral Details Audit
			//=======================================
			if (financeDetail.getCollateralAssignmentList() != null
					&& !financeDetail.getCollateralAssignmentList().isEmpty()) {
				List<AuditDetail> details = financeDetail.getAuditDetailMap().get("CollateralAssignments");
				details = processingCollateralAssignmentList(details, tableType.getSuffix(), financeDetail
						.getFinScheduleData().getFinanceMain());
				auditDetails.addAll(details);
			}
		} else {
			//=======================================

			if (financeDetail.getFinanceCollaterals() != null && !financeDetail.getFinanceCollaterals().isEmpty()) {
				financeDetail.setFinanceCollaterals(financeDetail.getFinanceCollaterals());
				List<AuditDetail> details = financeDetail.getAuditDetailMap().get("FinCollateral");
				details = processingFinCollateralDetailList(details, tableType.getSuffix(),
						financeMain.getFinReference());
				auditDetails.addAll(details);
			}
		}
		// FinFlag Details
		// =======================================
		if (financeDetail.getFinFlagsDetails() != null && financeDetail.getFinFlagsDetails().size() > 0) {
			List<AuditDetail> details = financeDetail.getAuditDetailMap().get("FinFlagsDetail");
			details = processingFinFlagDetailList(details, financeDetail, tableType.getSuffix());
			auditDetails.addAll(details);
		}

		String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), financeMain.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1], financeMain
				.getBefImage(), financeMain));

		auditHeader.setAuditDetails(auditDetails);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}

	private List<AuditDetail> setGuarantorDetailAuditData(FinanceDetail financeDetail, String auditTranType,
			String method) {
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		GuarantorDetail guarantorDetail = new GuarantorDetail();
		String[] fields = PennantJavaUtil.getFieldDetails(guarantorDetail, guarantorDetail.getExcludeFields());

		for (int i = 0; i < financeDetail.getGurantorsDetailList().size(); i++) {
			GuarantorDetail finGuarantorDetailList = financeDetail.getGurantorsDetailList().get(i);

			if (StringUtils.isEmpty(finGuarantorDetailList.getRecordType())) {
				continue;
			}

			finGuarantorDetailList.setWorkflowId(guarantorDetail.getWorkflowId());

			boolean isRcdType = false;

			if (finGuarantorDetailList.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				finGuarantorDetailList.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (finGuarantorDetailList.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				finGuarantorDetailList.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (guarantorDetail.isWorkflow()) {
					isRcdType = true;
				}
			} else if (finGuarantorDetailList.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				finGuarantorDetailList.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if ("saveOrUpdate".equals(method) && (isRcdType)) {
				finGuarantorDetailList.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (finGuarantorDetailList.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (finGuarantorDetailList.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| finGuarantorDetailList.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			finGuarantorDetailList.setRecordStatus(guarantorDetail.getRecordStatus());
			finGuarantorDetailList.setLastMntOn(guarantorDetail.getLastMntOn());

			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], finGuarantorDetailList
					.getBefImage(), finGuarantorDetailList));
		}

		return auditDetails;
	}

	private List<AuditDetail> setJointAccountDetailAuditData(FinanceDetail financeDetail, String auditTranType,
			String method) {
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		JointAccountDetail jointAccountDetail = new JointAccountDetail();
		String[] fields = PennantJavaUtil.getFieldDetails(jointAccountDetail, jointAccountDetail.getExcludeFields());

		for (int i = 0; i < financeDetail.getJountAccountDetailList().size(); i++) {
			JointAccountDetail jointAccount = financeDetail.getJountAccountDetailList().get(i);

			if (StringUtils.isEmpty(jointAccount.getRecordType())) {
				continue;
			}

			jointAccount.setWorkflowId(jointAccountDetail.getWorkflowId());

			boolean isRcdType = false;

			if (jointAccount.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				jointAccount.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (jointAccount.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				jointAccount.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (jointAccountDetail.isWorkflow()) {
					isRcdType = true;
				}
			} else if (jointAccount.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				jointAccount.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if ("saveOrUpdate".equals(method) && (isRcdType)) {
				jointAccount.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (jointAccount.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (jointAccount.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| jointAccount.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			jointAccount.setRecordStatus(jointAccountDetail.getRecordStatus());
			jointAccount.setLastMntOn(jointAccountDetail.getLastMntOn());

			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], jointAccount.getBefImage(),
					jointAccount));
		}

		return auditDetails;
	}

	/*
	 * FinCollteralDetail Audit Map
	 */
	private List<AuditDetail> setFinCollateralDetailAuditData(FinanceDetail financeDetail, String auditTranType,
			String method) {
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		FinCollaterals collateralDetail = new FinCollaterals();
		String[] fields = PennantJavaUtil.getFieldDetails(collateralDetail, collateralDetail.getExcludeFields());

		for (int i = 0; i < financeDetail.getFinanceCollaterals().size(); i++) {
			FinCollaterals finCollateralList = financeDetail.getFinanceCollaterals().get(i);

			if (StringUtils.isEmpty(finCollateralList.getRecordType())) {
				continue;
			}

			finCollateralList.setWorkflowId(collateralDetail.getWorkflowId());

			boolean isRcdType = false;

			if (finCollateralList.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				finCollateralList.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (finCollateralList.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				finCollateralList.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (collateralDetail.isWorkflow()) {
					isRcdType = true;
				}
			} else if (finCollateralList.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				finCollateralList.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if ("saveOrUpdate".equals(method) && (isRcdType)) {
				finCollateralList.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (finCollateralList.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (finCollateralList.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| finCollateralList.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			finCollateralList.setRecordStatus(collateralDetail.getRecordStatus());
			finCollateralList.setLastMntOn(collateralDetail.getLastMntOn());

			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], finCollateralList
					.getBefImage(), finCollateralList));
		}

		return auditDetails;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getFinanceMainDAO().delete with parameters financeMain,"_Temp" 3) Audit the record in to
	 * AuditHeader and AdtFinanceMain by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 * @throws InterfaceException
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	@Override
	public AuditHeader doReject(AuditHeader auditHeader) throws InterfaceException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		List<AuditDetail> auditDetailList = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		String tranType = PennantConstants.TRAN_DEL;

		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

		// Cancel All Transactions done by Finance Reference
		//=======================================
		cancelStageAccounting(financeMain.getFinReference(), financeDetail.getModuleDefiner());

		// Fee charges deletion
		getFinFeeChargesDAO().deleteChargesBatch(financeMain.getFinReference(), financeDetail.getModuleDefiner(),
				false, "_Temp");

		// Checklist Details delete
		//=======================================
		auditDetailList.addAll(getCheckListDetailService().delete(financeDetail, "_Temp", tranType));

		// Save Document Details
		if (financeDetail.getDocumentDetailsList() != null && financeDetail.getDocumentDetailsList().size() > 0) {
			for (DocumentDetails docDetails : financeDetail.getDocumentDetailsList()) {
				docDetails.setRecordType(PennantConstants.RECORD_TYPE_CAN);
			}
			List<AuditDetail> details = financeDetail.getAuditDetailMap().get("DocumentDetails");
			details = processingDocumentDetailsList(details, "_Temp", financeDetail.getFinScheduleData()
					.getFinanceMain(), financeDetail.getModuleDefiner());
			auditDetailList.addAll(details);
		}

		// set Guarantor Details Audit
		//=======================================
		if (financeDetail.getGurantorsDetailList() != null && !financeDetail.getGurantorsDetailList().isEmpty()) {
			for (GuarantorDetail guarantorDetails : financeDetail.getGurantorsDetailList()) {
				guarantorDetails.setRecordType(PennantConstants.RECORD_TYPE_CAN);
			}
			List<AuditDetail> details = financeDetail.getAuditDetailMap().get("GuarantorDetails");
			details = processingGuarantorDetailList(details, "_Temp", financeMain.getFinReference());
			auditDetailList.addAll(details);
		}

		// set JointAccount Details Audit
		//=======================================
		if (financeDetail.getJountAccountDetailList() != null && !financeDetail.getJountAccountDetailList().isEmpty()) {
			for (JointAccountDetail jointAccountDetail : financeDetail.getJountAccountDetailList()) {
				jointAccountDetail.setRecordType(PennantConstants.RECORD_TYPE_CAN);
			}
			List<AuditDetail> details = financeDetail.getAuditDetailMap().get("JointAccountDetails");
			details = processingJointAccountDetailList(details, "_Temp", financeMain.getFinReference());
			auditDetailList.addAll(details);
		}

		// Finance Flag Details
		if (financeDetail.getFinFlagsDetails() != null && !financeDetail.getFinFlagsDetails().isEmpty()) {
			List<AuditDetail> details = financeDetail.getAuditDetailMap().get("FinFlagsDetail");
			for (int i = 0; i < details.size(); i++) {
				FinFlagsDetail finFlagsDetail = (FinFlagsDetail) details.get(i).getModelData();
				finFlagsDetail.setRecordType(PennantConstants.RECORD_TYPE_CAN);
			}
			getFinFlagDetailsDAO().deleteList(financeMain.getFinReference(), FinanceConstants.MODULE_NAME, "_Temp");
			auditDetailList.addAll(details);

		}

		//Collateral assignment Details
		if (financeDetail.getCollateralAssignmentList() != null
				&& !financeDetail.getCollateralAssignmentList().isEmpty()) {
			List<AuditDetail> details = financeDetail.getAuditDetailMap().get("CollateralAssignments");
			for (int i = 0; i < details.size(); i++) {
				CollateralAssignment assignment = (CollateralAssignment) details.get(i).getModelData();
				assignment.setRecordType(PennantConstants.RECORD_TYPE_CAN);
			}
			details = processingCollateralAssignmentList(details, "_Temp", financeDetail.getFinScheduleData()
					.getFinanceMain());
			auditDetailList.addAll(details);
		}

		if (financeDetail.getFinanceCollaterals() != null && !financeDetail.getFinanceCollaterals().isEmpty()) {
			financeDetail.setFinanceCollaterals(financeDetail.getFinanceCollaterals());
			List<AuditDetail> details = financeDetail.getAuditDetailMap().get("FinCollateral");
			details = processingFinCollateralDetailList(details, "_Temp", financeMain.getFinReference());
			auditDetailList.addAll(details);
		}

		// ScheduleDetails deletion
		getFinODPenaltyRateDAO().delete(financeMain.getFinReference(), "_Temp");
		if (financeDetail.getFinwriteoffPayment() != null) {
			getFinanceWriteoffDAO().deletefinWriteoffPayment(financeMain.getFinReference(),
					financeDetail.getFinwriteoffPayment().getSeqNo(), "_Temp");
		}
		getFinanceMainDAO().delete(financeMain, TableType.TEMP_TAB, false, false);

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), financeMain.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1], financeMain
				.getBefImage(), financeMain));
		auditHeader.setAuditDetails(auditDetailList);
		getAuditHeaderDAO().addAudit(auditHeader);

		//Reset Finance Detail Object for Service Task Verifications
		auditHeader.getAuditDetail().setModelData(financeDetail);

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getFinanceMainDAO().delete with
	 * parameters financeMain,"" b) NEW Add new record in to main table by using getFinanceMainDAO().save with
	 * parameters financeMain,"" c) EDIT Update record in the main table by using getFinanceMainDAO().update with
	 * parameters financeMain,"" 3) Delete the record from the workFlow table by using getFinanceMainDAO().delete with
	 * parameters financeMain,"_Temp" 4) Audit the record in to AuditHeader and AdtFinanceMain by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and AdtFinanceMain by
	 * using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 * @throws AccountNotFoundException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	@Override
	public AuditHeader doApprove(AuditHeader aAuditHeader) throws InterfaceException, IllegalAccessException,
			InvocationTargetException {
		logger.debug("Entering");

		String tranType = "";
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		aAuditHeader = businessValidation(aAuditHeader, "doApprove");
		if (!aAuditHeader.isNextProcess()) {
			return aAuditHeader;
		}

		Cloner cloner = new Cloner();
		AuditHeader auditHeader = cloner.deepClone(aAuditHeader);
		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		Date curBDay = DateUtility.getAppDate();
		//Finance Stage Accounting Process
		//=======================================
		if (financeDetail.getStageAccountingList() != null && financeDetail.getStageAccountingList().size() > 0) {

			List<ReturnDataSet> list = new ArrayList<ReturnDataSet>();
			auditHeader = executeStageAccounting(auditHeader, list);
			if (auditHeader.getErrorMessage() != null && auditHeader.getErrorMessage().size() > 0) {
				return auditHeader;
			}
			list = null;
		}

		//Finance Write off Posting Process Execution
		//=====================================
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		FinanceProfitDetail profitDetail = getProfitDetailsDAO().getFinProfitDetailsById(financeMain.getFinReference());

		String accEventCode = "";
		if (StringUtils.equals(financeDetail.getModuleDefiner(), FinanceConstants.FINSER_EVENT_WRITEOFFPAY)) {
			accEventCode = AccountEventConstants.ACCEVENT_WRITEBK;
		} else if (StringUtils.equals(financeDetail.getModuleDefiner(), FinanceConstants.FINSER_EVENT_BASICMAINTAIN)) {
			accEventCode = AccountEventConstants.ACCEVENT_AMENDMENT;
		} else if (StringUtils.equals(financeDetail.getModuleDefiner(), FinanceConstants.FINSER_EVENT_RPYBASICMAINTAIN)) {
			accEventCode = AccountEventConstants.ACCEVENT_SEGMENT;
		}

		AEEvent aeEvent = AEAmounts.procAEAmounts(financeMain, financeDetail.getFinScheduleData()
				.getFinanceScheduleDetails(), profitDetail, accEventCode, curBDay, financeMain.getMaturityDate());
		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();

		//FinanceWriteoffPayment set the writeoffPayAmount,WriteoffPayAccount 
		if (financeDetail.getFinwriteoffPayment() != null) {
			amountCodes.setWoPayAmt(financeDetail.getFinwriteoffPayment().getWriteoffPayAmount());
		}

		HashMap<String, Object> dataMap = amountCodes.getDeclaredFieldValues();
		aeEvent.setDataMap(dataMap);

		try {
			aeEvent = getPostingsPreparationUtil().processPostingDetails(aeEvent);
		} catch (AccountNotFoundException e) {
			e.printStackTrace();
		}


		if (!aeEvent.isPostingSucess()) {
			String errParm = aeEvent.getErrorMessage();
			throw new InterfaceException("9999", errParm);
		}

		long linkedTranId = aeEvent.getLinkedTranId();
		financeMain.setRcdMaintainSts("");
		financeMain.setRoleCode("");
		financeMain.setNextRoleCode("");
		financeMain.setTaskId("");
		financeMain.setNextTaskId("");
		financeMain.setWorkflowId(0);

		tranType = PennantConstants.TRAN_UPD;
		financeMain.setRecordType("");
		getFinanceMainDAO().update(financeMain, TableType.MAIN_TAB, false);

		//Save Finance WriteOffPayment Details
		FinWriteoffPayment financeWriteoffPayment = financeDetail.getFinwriteoffPayment();
		if (financeDetail.getFinwriteoffPayment() != null) {
			financeWriteoffPayment.setLinkedTranId(linkedTranId);
			getFinanceWriteoffDAO().saveFinWriteoffPayment(financeWriteoffPayment, "");
		}

		//Finance Penalty OD Rate Details
		FinODPenaltyRate penaltyRate = financeDetail.getFinScheduleData().getFinODPenaltyRate();
		if (penaltyRate == null) {
			penaltyRate = new FinODPenaltyRate();
			penaltyRate.setApplyODPenalty(false);
			penaltyRate.setODIncGrcDays(false);
			penaltyRate.setODChargeType("");
			penaltyRate.setODChargeAmtOrPerc(BigDecimal.ZERO);
			penaltyRate.setODChargeCalOn("");
			penaltyRate.setODGraceDays(0);
			penaltyRate.setODAllowWaiver(false);
			penaltyRate.setODMaxWaiverPerc(BigDecimal.ZERO);
		}
		penaltyRate.setFinReference(financeMain.getFinReference());
		penaltyRate.setFinEffectDate(DateUtility.getSysDate());

		FinODPenaltyRate oldPenaltyRate = getFinODPenaltyRateDAO().getFinODPenaltyRateByRef(
				financeMain.getFinReference(), "");
		if(oldPenaltyRate!=null){
			getFinODPenaltyRateDAO().saveLog(oldPenaltyRate, "_Log");
			getFinODPenaltyRateDAO().delete(financeMain.getFinReference(), "");
			getFinODPenaltyRateDAO().save(penaltyRate, "");
		}

		// Save Document Details
		if (financeDetail.getDocumentDetailsList() != null && financeDetail.getDocumentDetailsList().size() > 0) {
			List<AuditDetail> details = financeDetail.getAuditDetailMap().get("DocumentDetails");
			details = processingDocumentDetailsList(details, "", financeDetail.getFinScheduleData().getFinanceMain(),
					financeDetail.getModuleDefiner());
			auditDetails.addAll(details);
			listDocDeletion(financeDetail, "_Temp");
		}

		//Fee Charge Details
		//=======================================
		saveFeeChargeList(financeDetail.getFinScheduleData(), financeDetail.getModuleDefiner(), false, "");

		// set Check list details Audit
		//=======================================
		if (financeDetail.getFinanceCheckList() != null && !financeDetail.getFinanceCheckList().isEmpty()) {
			auditDetails.addAll(getCheckListDetailService().doApprove(financeDetail, ""));
		}

		// Guarantor Details
		if (financeDetail.getGurantorsDetailList() != null && !financeDetail.getGurantorsDetailList().isEmpty()) {
			List<AuditDetail> details = financeDetail.getAuditDetailMap().get("GuarantorDetails");
			details = processingGuarantorDetailList(details, "", financeMain.getFinReference());
			auditDetails.addAll(details);

		}

		// JointAccount Details
		if (financeDetail.getJountAccountDetailList() != null && !financeDetail.getJountAccountDetailList().isEmpty()) {
			List<AuditDetail> details = financeDetail.getAuditDetailMap().get("JointAccountDetails");
			details = processingJointAccountDetailList(details, "", financeMain.getFinReference());
			auditDetails.addAll(details);

		}

		// Fin Flag Details
		if (financeDetail.getFinFlagsDetails() != null && financeDetail.getFinFlagsDetails().size() > 0) {
			List<AuditDetail> details = financeDetail.getAuditDetailMap().get("FinFlagsDetail");
			details = processingFinFlagDetailList(details, financeDetail, "");
			auditDetails.addAll(details);
		}

		if (ImplementationConstants.COLLATERAL_INTERNAL) {
			// Collateral Assignments Details
			//=======================================
			if (financeDetail.getCollateralAssignmentList() != null
					&& !financeDetail.getCollateralAssignmentList().isEmpty()) {
				List<AuditDetail> details = financeDetail.getAuditDetailMap().get("CollateralAssignments");
				details = processingCollateralAssignmentList(details, "", financeDetail.getFinScheduleData()
						.getFinanceMain());
				auditDetails.addAll(details);
			}
		} else {
			// set Finance Collateral Details Audit
			if (financeDetail.getFinanceCollaterals() != null && !financeDetail.getFinanceCollaterals().isEmpty()) {
				financeDetail.setFinanceCollaterals(financeDetail.getFinanceCollaterals());
				List<AuditDetail> details = financeDetail.getAuditDetailMap().get("FinCollateral");
				details = processingFinCollateralDetailList(details, "", financeMain.getFinReference());
				auditDetails.addAll(details);
			}
		}

		//Finance Profit Details Updation - Repayment Account
		getProfitDetailsDAO().updateRpyAccount(financeMain.getFinReference(), financeMain.getRepayAccountId());

		String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), financeMain.getExcludeFields());
		List<AuditDetail> auditDetailList = new ArrayList<AuditDetail>();

		getFinODPenaltyRateDAO().delete(financeMain.getFinReference(), "_Temp");

		if (financeDetail.getFinwriteoffPayment() != null) {
			getFinanceWriteoffDAO().deletefinWriteoffPayment(financeMain.getFinReference(),
					financeDetail.getFinwriteoffPayment().getSeqNo(), "_Temp");
		}

		//Guarantor Details
		if (financeDetail.getGurantorsDetailList() != null && !financeDetail.getGurantorsDetailList().isEmpty()) {
			List<AuditDetail> details = financeDetail.getAuditDetailMap().get("GuarantorDetails");
			List<GuarantorDetail> guarantorDetail = new ArrayList<GuarantorDetail>();
			;
			for (int i = 0; i < details.size(); i++) {
				GuarantorDetail guarantor = (GuarantorDetail) details.get(i).getModelData();
				guarantorDetail.add(guarantor);
			}
			auditDetails.addAll(getGuarantorDetailService().delete(guarantorDetail, "_Temp", tranType));
		}
		//JointAccount Details
		if (financeDetail.getJountAccountDetailList() != null && !financeDetail.getJountAccountDetailList().isEmpty()) {
			List<AuditDetail> details = financeDetail.getAuditDetailMap().get("JointAccountDetails");
			List<JointAccountDetail> jointAccountDetail = new ArrayList<JointAccountDetail>();
			;
			for (int i = 0; i < details.size(); i++) {
				JointAccountDetail jointAcctDetail = (JointAccountDetail) details.get(i).getModelData();
				jointAccountDetail.add(jointAcctDetail);
			}
			auditDetails.addAll(getJointAccountDetailService().delete(jointAccountDetail, "_Temp", tranType));
		}

		// Checklist Details delete
		//=======================================
		auditDetailList.addAll(getCheckListDetailService().delete(financeDetail, "_Temp", tranType));

		//Fee Charge Details Clearing before 
		//=======================================
		getFinFeeChargesDAO().deleteChargesBatch(financeMain.getFinReference(), financeDetail.getModuleDefiner(),
				false, "_Temp");
		
		// Finance Flag Details
		if (financeDetail.getFinFlagsDetails() != null && !financeDetail.getFinFlagsDetails().isEmpty()) {
			List<AuditDetail> details = financeDetail.getAuditDetailMap().get("FinFlagsDetail");
			for (int i = 0; i < details.size(); i++) {
				FinFlagsDetail finFlagsDetail = (FinFlagsDetail) details.get(i).getModelData();
				finFlagsDetail.setRecordType(PennantConstants.RECORD_TYPE_CAN);
			}
			getFinFlagDetailsDAO().deleteList(financeMain.getFinReference(), FinanceConstants.MODULE_NAME, "_Temp");
			auditDetailList.addAll(details);

		}

		//FinanceMain Details Clearing before 
		//=======================================
		getFinanceMainDAO().delete(financeMain, TableType.TEMP_TAB, false, true);

		// Collateral assignment Details
		if (financeDetail.getCollateralAssignmentList() != null
				&& !financeDetail.getCollateralAssignmentList().isEmpty()) {
			List<AuditDetail> details = financeDetail.getAuditDetailMap().get("CollateralAssignments");
			auditDetailList.addAll(details);
			getCollateralAssignmentDAO().deleteByReference(financeMain.getFinReference(), "_Temp");
		}

		if (financeDetail.getFinanceCollaterals() != null) {
			auditDetailList.addAll(getFinCollateralService().delete(financeDetail.getFinanceCollaterals(), "_Temp",
					auditHeader.getAuditTranType()));
		}

		FinanceDetail tempfinanceDetail = (FinanceDetail) aAuditHeader.getAuditDetail().getModelData();
		FinanceMain tempfinanceMain = tempfinanceDetail.getFinScheduleData().getFinanceMain();
		auditHeader.setAuditDetail(new AuditDetail(aAuditHeader.getAuditTranType(), 1, fields[0], fields[1],
				tempfinanceMain.getBefImage(), tempfinanceMain));

		// Adding audit as deleted from TEMP table
		auditHeader.setAuditDetails(auditDetailList);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1], financeMain
				.getBefImage(), financeMain));

		// Adding audit as Insert/Update/deleted into main table
		auditHeader.setAuditDetails(auditDetails);
		getAuditHeaderDAO().addAudit(auditHeader);

		//Reset Finance Detail Object for Service Task Verifications
		auditHeader.getAuditDetail().setModelData(financeDetail);
		getFinStageAccountingLogDAO().update(financeMain.getFinReference(), financeDetail.getModuleDefiner(), false);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from the auditHeader. 2) fetch the details
	 * from the tables 3) Validate the Record based on the record details. 4) Validate for any business validation. 5)
	 * for any mismatch conditions Fetch the error details from getFinanceMainDAO().getErrorDetail with Error ID and
	 * language as parameters. 6) if any error/Warnings then assign the to auditHeader
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = getAuditDetails(auditHeader, method);

		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		String usrLanguage = financeMain.getUserDetails().getLanguage();

		//Collateral Assignments details
		//=======================================
		if (financeDetail.getCollateralAssignmentList() != null
				&& !financeDetail.getCollateralAssignmentList().isEmpty()) {

			//CoOwnerDetails Validation
			List<CollateralAssignment> assignments = financeDetail.getCollateralAssignmentList();
			if (assignments != null && !assignments.isEmpty()) {
				List<AuditDetail> details = financeDetail.getAuditDetailMap().get("CollateralAssignments");
				details = getCollateralAssignmentValidation().vaildateDetails(details, method, usrLanguage);
				auditDetails.addAll(details);
			}
		}
		// Finance Flag details Validation
		List<FinFlagsDetail> finFlagsDetailList = financeDetail.getFinFlagsDetails();
		if (finFlagsDetailList != null && !finFlagsDetailList.isEmpty()) {
			List<AuditDetail> details = financeDetail.getAuditDetailMap().get("FinFlagsDetail");
			details = getFlagDetailValidation().vaildateDetails(details, method, usrLanguage);
			auditDetails.addAll(details);
		}
		//Joint Account  Details Validation
		List<JointAccountDetail> finJoinAccDetailList = financeDetail.getJountAccountDetailList();
		if (finJoinAccDetailList != null && !finJoinAccDetailList.isEmpty()) {
			List<AuditDetail> details = financeDetail.getAuditDetailMap().get("JointAccountDetails");
			details = getFinJointAccountDetailValidation().jointAccountDetailsListValidation(details, method, usrLanguage);
			auditDetails.addAll(details);
		}
		//Guarantor Account  Details Validation
		List<GuarantorDetail> guarantorAccDetailList = financeDetail.getGurantorsDetailList();
		if (guarantorAccDetailList != null && !guarantorAccDetailList.isEmpty()) {
			List<AuditDetail> details = financeDetail.getAuditDetailMap().get("GuarantorDetails");
			details = getFinGuarantorDetailValidation().gurantorDetailsListValidation(details, method, usrLanguage);
			auditDetails.addAll(details);
		}

		for (int i = 0; i < auditDetails.size(); i++) {
			auditHeader.setErrorList(auditDetails.get(i).getErrorDetails());
		}

		auditHeader = nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * Method for Validate Finance Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @param isWIF
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug("Entering");

		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		FinanceDetail financeDetail = (FinanceDetail) auditDetail.getModelData();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		int format = CurrencyUtil.getFormat(financeMain.getFinCcy());

		FinanceMain tempFinanceMain = null;
		if (financeMain.isWorkflow()) {
			tempFinanceMain = getFinanceMainDAO().getFinanceMainById(financeMain.getId(), "_Temp", false);
		}
		FinanceMain befFinanceMain = getFinanceMainDAO().getFinanceMainById(financeMain.getId(), "", false);
		FinanceMain oldFinanceMain = financeMain.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = financeMain.getId();
		errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];

		if (financeMain.isNew()) { // for New record or new record into work flow

			if (!financeMain.isWorkflow()) {// With out Work flow only new
				// records
				if (befFinanceMain != null) { // Record Already Exists in the
					// table then error
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,
							"41001", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow
				if (financeMain.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if
					// records type is new
					if (befFinanceMain != null || tempFinanceMain != null) { // if
						// records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(
								PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
					}
				} else { // if records not exists in the Main flow table
					if (befFinanceMain == null || tempFinanceMain != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(
								PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!financeMain.isWorkflow()) { // With out Work flow for update
				// and delete

				if (befFinanceMain == null) { // if records not exists in the
					// main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,
							"41002", errParm, valueParm), usrLanguage));
				} else {
					if (oldFinanceMain != null && !oldFinanceMain.getLastMntOn().equals(befFinanceMain.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(
								PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(
									PennantConstants.KEY_FIELD, "41003", errParm, valueParm), usrLanguage));
						} else {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(
									PennantConstants.KEY_FIELD, "41004", errParm, valueParm), usrLanguage));
						}
					}
				}
			} else {

				if (tempFinanceMain == null) { // if records not exists in the
					// Work flow table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,
							"41005", errParm, valueParm), usrLanguage));
				}

				if (tempFinanceMain != null && oldFinanceMain != null
						&& !oldFinanceMain.getLastMntOn().equals(tempFinanceMain.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,
							"41005", errParm, valueParm), usrLanguage));
				}
			}
		}

		// Checking , if Customer is in EOD process or not. if Yes, not allowed to do an action
		int eodProgressCount = getCustomerQueuingDAO().getProgressCountByCust(financeMain.getCustID());

		// If Customer Exists in EOD Processing, Not allowed to Maintenance till completion
		if (eodProgressCount > 0) {
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "60203",
					errParm, valueParm), usrLanguage));
		}

		FinWriteoffPayment finWriteoffPay = financeDetail.getFinwriteoffPayment();
		if (finWriteoffPay != null) {
			//Save Finance WriteOff Details
			BigDecimal financeWriteoffDetailAmt = getFinanceWriteoffDAO().getTotalFinWriteoffDetailAmt(
					financeMain.getFinReference());
			BigDecimal finWriteoffPayAmt = getFinanceWriteoffDAO().getTotalWriteoffPaymentAmount(
					financeMain.getFinReference());
			BigDecimal finWriteoffTotAmt = financeWriteoffDetailAmt.subtract(finWriteoffPayAmt);
			if (finWriteoffPay.getWriteoffPayAmount().compareTo(finWriteoffTotAmt) > 0) {
				String[] errParm1 = new String[2];
				String[] valueParm1 = new String[2];
				valueParm1[0] = PennantApplicationUtil.amountFormate(finWriteoffPay.getWriteoffPayAmount(), format)
						.toString();
				errParm1[0] = PennantJavaUtil.getLabel("label_WriteoffPayAmount") + ":" + valueParm1[0];
				valueParm1[1] = PennantApplicationUtil.formateAmount(finWriteoffTotAmt, format).toString();
				errParm1[1] = PennantJavaUtil.getLabel("label_OutstandWriteoffAmount") + ":" + valueParm1[1];
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,
						"30568", errParm1, valueParm1), usrLanguage));
			}
		}
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !financeMain.isWorkflow()) {
			financeMain.setBefImage(befFinanceMain);
		}

		return auditDetail;
	}

	private AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {
		logger.debug("Entering ");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

		String auditTranType = "";
		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (financeMain.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}

		// Finance Flag details
		if (financeDetail.getFinFlagsDetails() != null && financeDetail.getFinFlagsDetails().size() > 0) {
			auditDetailMap.put("FinFlagsDetail", setFinFlagAuditData(financeDetail, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("FinFlagsDetail"));
		}

		//Finance Document Details
		if (financeDetail.getDocumentDetailsList() != null && financeDetail.getDocumentDetailsList().size() > 0) {
			auditDetailMap.put("DocumentDetails", setDocumentDetailsAuditData(financeDetail, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("DocumentDetails"));
		}

		//Finance Checklist Details
		//=======================================
		List<FinanceCheckListReference> financeCheckList = financeDetail.getFinanceCheckList();

		if (StringUtils.equals(method, "saveOrUpdate")) {
			if (financeCheckList != null && !financeCheckList.isEmpty()) {
				auditDetails.addAll(getCheckListDetailService().getAuditDetail(auditDetailMap, financeDetail,
						auditTranType, method));
			}
		}

		//Finance Guarantor Details
		//=======================================
		List<GuarantorDetail> finGuarantor = financeDetail.getGurantorsDetailList();
		if (finGuarantor != null && !finGuarantor.isEmpty()) {
			auditDetailMap.put("GuarantorDetails", setGuarantorDetailAuditData(financeDetail, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("GuarantorDetails"));
		}

		//Joint Account Details
		//=======================================
		List<JointAccountDetail> finJointAccoutnDetail = financeDetail.getJountAccountDetailList();
		if (finJointAccoutnDetail != null && !finJointAccoutnDetail.isEmpty()) {
			auditDetailMap.put("JointAccountDetails",
					setJointAccountDetailAuditData(financeDetail, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("JointAccountDetails"));
		}

		if (ImplementationConstants.COLLATERAL_INTERNAL) {

			// Collateral Assignment Details
			//=======================================
			if (financeDetail.getCollateralAssignmentList() != null
					&& financeDetail.getCollateralAssignmentList().size() > 0) {
				auditDetailMap.put("CollateralAssignments",
						setCollateralAssignmentAuditData(financeDetail, auditTranType, method));
				auditDetails.addAll(auditDetailMap.get("CollateralAssignments"));
			}
		} else {
			//Finance Collaterals Details
			//=======================================
			List<FinCollaterals> finCollateral = financeDetail.getFinanceCollaterals();
			if (finCollateral != null && !finCollateral.isEmpty()) {
				auditDetailMap.put("FinCollateral",
						setFinCollateralDetailAuditData(financeDetail, auditTranType, method));
				auditDetails.addAll(auditDetailMap.get("FinCollateral"));
			}
		}

		financeDetail.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(financeDetail);
		auditHeader.setAuditDetails(auditDetails);
		logger.debug("Leaving ");

		return auditHeader;

	}

	/**
	 * Methods for Creating List Finance Flag of Audit Details with detailed fields
	 * 
	 * @param financeDetail
	 * @param auditTranType
	 * @param method
	 * @return
	 */
	private List<AuditDetail> setFinFlagAuditData(FinanceDetail financeDetail, String auditTranType, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		for (int i = 0; i < financeDetail.getFinFlagsDetails().size(); i++) {

			FinFlagsDetail finFlagsDetail = financeDetail.getFinFlagsDetails().get(i);
			boolean isRcdType = false;
			if (StringUtils.isEmpty(finFlagsDetail.getRecordType())) {
				continue;
			}

			if (finFlagsDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				finFlagsDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (finFlagsDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				finFlagsDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (finFlagsDetail.isWorkflow()) {
					isRcdType = true;
				}
			} else if (finFlagsDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				finFlagsDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if ("saveOrUpdate".equals(method) && (isRcdType)) {
				finFlagsDetail.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (finFlagsDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (finFlagsDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| finFlagsDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}
			if (StringUtils.isNotEmpty(finFlagsDetail.getRecordType())) {
				String[] fields = PennantJavaUtil.getFieldDetails(new FinFlagsDetail(),
						finFlagsDetail.getExcludeFields());
				auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], finFlagsDetail
						.getBefImage(), finFlagsDetail));
			}
		}

		logger.debug("Leaving");
		return auditDetails;
	}

	/**
	 * Method For Preparing List of AuditDetails for Guarantor Details
	 * 
	 * @param auditDetails
	 * @param type
	 * @param Finreference
	 * @return
	 */
	private List<AuditDetail> processingGuarantorDetailList(List<AuditDetail> auditDetails, String type,
			String finReference) {

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {

			GuarantorDetail guarantorDetail = (GuarantorDetail) auditDetails.get(i).getModelData();

			if (StringUtils.isBlank(guarantorDetail.getRecordType())) {
				continue;
			}

			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				guarantorDetail.setRoleCode("");
				guarantorDetail.setNextRoleCode("");
				guarantorDetail.setTaskId("");
				guarantorDetail.setNextTaskId("");
			}

			guarantorDetail.setWorkflowId(0);
			guarantorDetail.setFinReference(finReference);

			if (guarantorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (guarantorDetail.isNewRecord()) {
				saveRecord = true;
				if (guarantorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					guarantorDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (guarantorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					guarantorDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (guarantorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					guarantorDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (guarantorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (guarantorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (guarantorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (guarantorDetail.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = guarantorDetail.getRecordType();
				recordStatus = guarantorDetail.getRecordStatus();
				guarantorDetail.setRecordType("");
				guarantorDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				getGuarantorDetailDAO().save(guarantorDetail, type);
			}

			if (updateRecord) {
				getGuarantorDetailDAO().update(guarantorDetail, type);
			}

			if (deleteRecord) {
				getGuarantorDetailDAO().delete(guarantorDetail, type);
			}

			if (approveRec) {
				guarantorDetail.setRecordType(rcdType);
				guarantorDetail.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(guarantorDetail);
		}

		return auditDetails;

	}

	/**
	 * Method For Preparing List of AuditDetails for Joint Details
	 * 
	 * @param auditDetails
	 * @param type
	 * @param Finreference
	 * @return
	 */
	private List<AuditDetail> processingJointAccountDetailList(List<AuditDetail> auditDetails, String type,
			String finReference) {

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {

			JointAccountDetail jointAccountDetail = (JointAccountDetail) auditDetails.get(i).getModelData();

			if (StringUtils.isBlank(jointAccountDetail.getRecordType())) {
				continue;
			}

			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				jointAccountDetail.setRoleCode("");
				jointAccountDetail.setNextRoleCode("");
				jointAccountDetail.setTaskId("");
				jointAccountDetail.setNextTaskId("");
			}

			jointAccountDetail.setWorkflowId(0);
			jointAccountDetail.setFinReference(finReference);

			if (jointAccountDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (jointAccountDetail.isNewRecord()) {
				saveRecord = true;
				if (jointAccountDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					jointAccountDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (jointAccountDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					jointAccountDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (jointAccountDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					jointAccountDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (jointAccountDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (jointAccountDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (jointAccountDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (jointAccountDetail.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = jointAccountDetail.getRecordType();
				recordStatus = jointAccountDetail.getRecordStatus();
				jointAccountDetail.setRecordType("");
				jointAccountDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				getJountAccountDetailDAO().save(jointAccountDetail, type);
			}

			if (updateRecord) {
				getJountAccountDetailDAO().update(jointAccountDetail, type);
			}

			if (deleteRecord) {
				getJountAccountDetailDAO().delete(jointAccountDetail, type);
			}

			if (approveRec) {
				jointAccountDetail.setRecordType(rcdType);
				jointAccountDetail.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(jointAccountDetail);
		}

		return auditDetails;

	}

	/**
	 * Method For Preparing List of AuditDetails for FinCollateral Details
	 * 
	 * @param auditDetails
	 * @param type
	 * @param Finreference
	 * @return
	 */
	private List<AuditDetail> processingFinCollateralDetailList(List<AuditDetail> auditDetails, String type,
			String finReference) {

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {

			FinCollaterals finCollaterals = (FinCollaterals) auditDetails.get(i).getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				finCollaterals.setRoleCode("");
				finCollaterals.setNextRoleCode("");
				finCollaterals.setTaskId("");
				finCollaterals.setNextTaskId("");
			}

			finCollaterals.setWorkflowId(0);
			finCollaterals.setFinReference(finReference);

			if (finCollaterals.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (finCollaterals.isNewRecord()) {
				saveRecord = true;
				if (finCollaterals.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					finCollaterals.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (finCollaterals.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					finCollaterals.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (finCollaterals.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					finCollaterals.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (finCollaterals.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (finCollaterals.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (finCollaterals.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (finCollaterals.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = finCollaterals.getRecordType();
				recordStatus = finCollaterals.getRecordStatus();
				finCollaterals.setRecordType("");
				finCollaterals.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				getFinCollateralsDAO().save(finCollaterals, type);
			}
			if (updateRecord) {
				getFinCollateralsDAO().update(finCollaterals, type);
			}

			if (deleteRecord) {
				getFinCollateralsDAO().delete(finCollaterals, type);
			}

			if (approveRec) {
				finCollaterals.setRecordType(rcdType);
				finCollaterals.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(finCollaterals);
		}

		return auditDetails;

	}

	/**
	 * Method For Preparing List of AuditDetails for Check List for Fin Flag Details
	 * 
	 * @param auditDetails
	 * @param financeDetail
	 * @param type
	 * @return
	 */
	private List<AuditDetail> processingFinFlagDetailList(List<AuditDetail> auditDetails, FinanceDetail financeDetail,
			String type) {
		logger.debug("Entering");

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {
			FinFlagsDetail finFlagsDetail = (FinFlagsDetail) auditDetails.get(i).getModelData();
			if (StringUtils.isEmpty(finFlagsDetail.getRecordType())) {
				continue;
			}
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				finFlagsDetail.setRoleCode("");
				finFlagsDetail.setNextRoleCode("");
				finFlagsDetail.setTaskId("");
				finFlagsDetail.setNextTaskId("");
				finFlagsDetail.setWorkflowId(0);
			}

			if (finFlagsDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (finFlagsDetail.isNewRecord()) {
				saveRecord = true;
				if (finFlagsDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					finFlagsDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (finFlagsDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					finFlagsDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (finFlagsDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					finFlagsDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (finFlagsDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (finFlagsDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (finFlagsDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (finFlagsDetail.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = finFlagsDetail.getRecordType();
				recordStatus = finFlagsDetail.getRecordStatus();
				finFlagsDetail.setRecordType("");
				finFlagsDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				getFinFlagDetailsDAO().save(finFlagsDetail, type);
			}

			if (updateRecord) {
				getFinFlagDetailsDAO().update(finFlagsDetail, type);
			}

			if (deleteRecord) {
				getFinFlagDetailsDAO().delete(finFlagsDetail.getReference(), finFlagsDetail.getFlagCode(),
						finFlagsDetail.getModuleName(), type);
			}

			if (approveRec) {
				finFlagsDetail.setRecordType(rcdType);
				finFlagsDetail.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(finFlagsDetail);
		}

		logger.debug("Leaving");
		return auditDetails;

	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public FinanceReferenceDetailDAO getFinanceReferenceDetailDAO() {
		return financeReferenceDetailDAO;
	}

	public void setFinanceReferenceDetailDAO(FinanceReferenceDetailDAO financeReferenceDetailDAO) {
		this.financeReferenceDetailDAO = financeReferenceDetailDAO;
	}

	public FinanceWriteoffDAO getFinanceWriteoffDAO() {
		return financeWriteoffDAO;
	}

	public void setFinanceWriteoffDAO(FinanceWriteoffDAO financeWriteoffDAO) {
		this.financeWriteoffDAO = financeWriteoffDAO;
	}

	public GuarantorDetailDAO getGuarantorDetailDAO() {
		return guarantorDetailDAO;
	}

	public void setGuarantorDetailDAO(GuarantorDetailDAO guarantorDetailDAO) {
		this.guarantorDetailDAO = guarantorDetailDAO;
	}

	public JountAccountDetailDAO getJountAccountDetailDAO() {
		return jountAccountDetailDAO;
	}

	public void setJountAccountDetailDAO(JountAccountDetailDAO jountAccountDetailDAO) {
		this.jountAccountDetailDAO = jountAccountDetailDAO;
	}

	public FlagDetailValidation getFlagDetailValidation() {
		if (flagDetailValidation == null) {
			this.flagDetailValidation = new FlagDetailValidation(finFlagDetailsDAO);
		}
		return this.flagDetailValidation;
	}

	public FinFlagDetailsDAO getFinFlagDetailsDAO() {
		return finFlagDetailsDAO;
	}

	public void setFinFlagDetailsDAO(FinFlagDetailsDAO finFlagDetailsDAO) {
		this.finFlagDetailsDAO = finFlagDetailsDAO;
	}

	public void setMandateDAO(MandateDAO mandateDAO) {
		this.mandateDAO = mandateDAO;
	}

	public FinJointAccountDetailValidation getFinJointAccountDetailValidation() {
		if (finJointAccountDetailValidation == null) {
			this.finJointAccountDetailValidation = new FinJointAccountDetailValidation(jountAccountDetailDAO,financeTaxDetailDAO);
		}
		return this.finJointAccountDetailValidation;
	}

	public void setFinJointAccountDetailValidation(FinJointAccountDetailValidation finJointAccountDetailValidation) {
		this.finJointAccountDetailValidation = finJointAccountDetailValidation;
	}


	public void setFinanceTaxDetailDAO(FinanceTaxDetailDAO financeTaxDetailDAO) {
		this.financeTaxDetailDAO = financeTaxDetailDAO;
	}

	public FinGuarantorDetailValidation getFinGuarantorDetailValidation() {
		if (finGuarantorDetailValidation == null) {
			this.finGuarantorDetailValidation = new FinGuarantorDetailValidation(guarantorDetailDAO,financeTaxDetailDAO);
		}
		return this.finGuarantorDetailValidation;
	}

	public void setFinGuarantorDetailValidation(FinGuarantorDetailValidation finGuarantorDetailValidation) {
		this.finGuarantorDetailValidation = finGuarantorDetailValidation;
	}
 
}
