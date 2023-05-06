/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : FinAdvancePaymentsServiceImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 14-08-2013 * *
 * Modified Date : 14-08-2013 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 14-08-2013 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.service.finance.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.finance.limits.LimitCheckDetails;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.configuration.VASConfigurationDAO;
import com.pennant.backend.dao.documentdetails.DocumentDetailsDAO;
import com.pennant.backend.dao.finance.FinAdvancePaymentsDAO;
import com.pennant.backend.dao.finance.FinanceDisbursementDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.payorderissue.PayOrderIssueHeaderDAO;
import com.pennant.backend.dao.systemmasters.VASProviderAccDetailDAO;
import com.pennant.backend.model.applicationmaster.InstrumentwiseLimit;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.configuration.VASConfiguration;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.PaymentTransaction;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.model.payorderissue.PayOrderIssueHeader;
import com.pennant.backend.model.systemmasters.VASProviderAccDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.InstrumentwiseLimitService;
import com.pennant.backend.service.finance.FinAdvancePaymentsService;
import com.pennant.backend.service.finance.covenant.CovenantsService;
import com.pennant.backend.service.partnerbank.PartnerBankService;
import com.pennant.backend.service.payment.PaymentsProcessService;
import com.pennant.backend.service.pennydrop.PennyDropService;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.pff.accounting.model.PostingDTO;
import com.pennant.pff.core.engine.accounting.AccountingEngine;
import com.pennanttech.model.dms.DMSModule;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.advancepayment.AdvancePaymentUtil;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.external.BankAccountValidationService;
import com.pennanttech.pff.overdraft.service.OverdrafLoanService;

/**
 * Service implementation for methods that depends on <b>FinancePurposeDetail</b>.<br>
 * 
 */
public class FinAdvancePaymentsServiceImpl extends GenericService<FinAdvancePayments>
		implements FinAdvancePaymentsService {
	private static final Logger logger = LogManager.getLogger(FinAdvancePaymentsServiceImpl.class);

	private FinAdvancePaymentsDAO finAdvancePaymentsDAO;
	private PayOrderIssueHeaderDAO payOrderIssueHeaderDAO;
	private LimitCheckDetails limitCheckDetails;
	private PartnerBankService partnerBankService;
	private FinanceProfitDetailDAO profitDetailsDAO;
	private CovenantsService covenantsService;
	private transient InstrumentwiseLimitService instrumentwiseLimitService;
	protected FinanceDisbursementDAO financeDisbursementDAO;
	private PaymentsProcessService paymentsProcessService;
	private transient PennyDropService pennyDropService;
	private transient BankAccountValidationService bankAccountValidationService;
	private DocumentDetailsDAO documentDetailsDAO;
	private VASProviderAccDetailDAO vASProviderAccDetailDAO;
	private VASConfigurationDAO vASConfigurationDAO;
	private OverdrafLoanService overdrafLoanService;

	public FinAdvancePaymentsServiceImpl() {
		super();
	}

	@Override
	public List<FinAdvancePayments> getFinAdvancePaymentByFinRef(long finID) {
		return finAdvancePaymentsDAO.getFinAdvancePaymentsByFinRef(finID, "");
	}

	@Override
	public List<FinAdvancePayments> getFinAdvancePaymentsById(long finID, String type) {
		return finAdvancePaymentsDAO.getFinAdvancePaymentsByFinRef(finID, type);
	}

	@Override
	public List<AuditDetail> saveOrUpdate(List<FinAdvancePayments> advPayments, String tableType, String auditTranType,
			boolean disbStp) {
		logger.debug(Literal.ENTERING);
		List<AuditDetail> auditDetails = new ArrayList<>();

		auditDetails.addAll(processFinAdvancePaymentDetails(advPayments, tableType, auditTranType, false, disbStp));

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	private List<AuditDetail> processFinAdvancePaymentDetails(List<FinAdvancePayments> finAdvancePayments,
			String tableType, String auditTranType, boolean isApproveRcd, boolean disbStp) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<>();

		if (CollectionUtils.isEmpty(finAdvancePayments)) {
			return auditDetails;
		}

		int i = 0;
		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (FinAdvancePayments fap : finAdvancePayments) {
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = isApproveRcd;
			String rcdType = "";
			String recordStatus = "";
			if (fap.ispOIssued() || StringUtils.isEmpty(fap.getRecordType())) {
				continue;
			}

			if (StringUtils.isEmpty(tableType)
					|| StringUtils.equals(tableType, PennantConstants.PREAPPROVAL_TABLE_TYPE)) {
				approveRec = true;
				fap.setRoleCode("");
				fap.setNextRoleCode("");
				fap.setTaskId("");
				fap.setNextTaskId("");
			}

			fap.setWorkflowId(0);
			if (fap.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (fap.isNewRecord()) {
				saveRecord = true;
				if (fap.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					fap.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (fap.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					fap.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (fap.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					fap.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (fap.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (fap.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (fap.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (fap.isNewRecord()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}

			if (approveRec) {
				rcdType = fap.getRecordType();
				recordStatus = fap.getRecordStatus();
				fap.setRecordType("");
				fap.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				if (!DisbursementConstants.STATUS_AWAITCON.equals(fap.getStatus())) {
					fap.setStatus(DisbursementConstants.STATUS_APPROVED);
				}
				fap.setpOIssued(true);
			}

			if (fap.isHoldDisbursement()) {
				fap.setStatus(DisbursementConstants.STATUS_HOLD);
			}

			if (disbStp || ImplementationConstants.DISB_STP) {
				fap.setStatus(DisbursementConstants.STATUS_AWAITCON);
			}

			if (saveRecord) {
				if (approveRec) {
					fap.setOnlineProcReq(true);
					overdrafLoanService.createDisbursement(fap);
				}
				finAdvancePaymentsDAO.save(fap, tableType);
				if (fap.getDocImage() != null) {
					saveDocumentDetails(fap);
				}
			}

			if (updateRecord) {
				finAdvancePaymentsDAO.update(fap, tableType);
				if (fap.getDocImage() != null) {
					saveDocumentDetails(fap);
				}
			}

			if (deleteRecord) {
				finAdvancePaymentsDAO.delete(fap, tableType);
			}

			if (approveRec) {
				fap.setRecordType(rcdType);
				fap.setRecordStatus(recordStatus);
			}

			String[] fields = PennantJavaUtil.getFieldDetails(fap, fap.getExcludeFields());
			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], fap.getBefImage(), fap));
			i++;
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	@Override
	public List<AuditDetail> doApprove(List<FinAdvancePayments> paymentsList, String tableType, String auditTranType,
			boolean disbStp) {
		logger.debug(Literal.ENTERING);
		List<AuditDetail> auditDetails = new ArrayList<>();

		auditDetails.addAll(processFinAdvancePaymentDetails(paymentsList, tableType, auditTranType, true, disbStp));

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	@Override
	public List<AuditDetail> delete(List<FinAdvancePayments> finAdvancePayments, String tableType,
			String auditTranType) {
		logger.debug(Literal.ENTERING);
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = null;

		if (finAdvancePayments != null && !finAdvancePayments.isEmpty()) {
			int auditSeq = 1;
			for (FinAdvancePayments finPayment : finAdvancePayments) {
				finAdvancePaymentsDAO.delete(finPayment, tableType);
				fields = PennantJavaUtil.getFieldDetails(finPayment, finPayment.getExcludeFields());
				auditDetails.add(new AuditDetail(auditTranType, auditSeq, fields[0], fields[1],
						finPayment.getBefImage(), finPayment));
				auditSeq++;
			}
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	private List<AuditDetail> getAdvancePaymentAuditDetail(List<FinAdvancePayments> fapList, String auditTranType,
			String method, long workFlowId) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		for (FinAdvancePayments fap : fapList) {
			if (StringUtils.isEmpty(fap.getRecordType())) {
				continue;
			}

			if ("doApprove".equals(method)
					&& !StringUtils.trimToEmpty(fap.getRecordStatus()).equals(PennantConstants.RCD_STATUS_SAVED)) {
				fap.setWorkflowId(0);
				fap.setNewRecord(true);
			} else {
				fap.setWorkflowId(workFlowId);
			}

			boolean isRcdType = false;

			if (fap.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				fap.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (fap.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				fap.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				isRcdType = true;
			} else if (fap.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				fap.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				isRcdType = true;
			}

			if ("saveOrUpdate".equals(method) && (isRcdType)) {
				fap.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (fap.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (fap.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| fap.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}
			String[] fields = PennantJavaUtil.getFieldDetails(fap, fap.getExcludeFields());
			if (StringUtils.isNotEmpty(fap.getRecordType())) {
				auditDetails.add(new AuditDetail(auditTranType, auditDetails.size() + 1, fields[0], fields[1],
						fap.getBefImage(), fap));
			}
		}
		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	@Override
	public List<AuditDetail> validate(List<FinAdvancePayments> fapList, long workflowId, String method,
			String auditTranType, String usrLanguage, FinanceDetail financeDetail, boolean isApi) {
		return doValidation(fapList, workflowId, method, auditTranType, usrLanguage, financeDetail, isApi);
	}

	private List<AuditDetail> doValidation(List<FinAdvancePayments> fapList, long workflowId, String method,
			String auditTranType, String usrLanguage, FinanceDetail financeDetail, boolean isApi) {
		logger.debug(Literal.ENTERING);
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		if (fapList != null && !fapList.isEmpty()) {
			List<AuditDetail> advancePayAuditDetails = getAdvancePaymentAuditDetail(fapList, auditTranType, method,
					workflowId);
			for (AuditDetail auditDetail : advancePayAuditDetails) {
				validateAdvancePayment(auditDetail, method, usrLanguage, financeDetail, isApi);
			}
			auditDetails.addAll(advancePayAuditDetails);
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	@Override
	public List<FinAdvancePayments> splitRequest(List<FinAdvancePayments> fapList) {
		logger.debug(Literal.ENTERING);

		List<FinAdvancePayments> finalPaymentsList = new ArrayList<FinAdvancePayments>();
		InstrumentwiseLimit instrumentwiseLimit = instrumentwiseLimitService
				.getInstrumentWiseModeLimit(DisbursementConstants.PAYMENT_TYPE_NEFT);

		if (instrumentwiseLimit == null) {
			finalPaymentsList.addAll(fapList);
			return finalPaymentsList;
		}

		int seqNo = getNextPaymentSequence(fapList);

		for (FinAdvancePayments fap : fapList) {
			if (!PennantConstants.RECORD_TYPE_DEL.equals(fap.getRecordType())
					&& !PennantConstants.RECORD_TYPE_CAN.equals(fap.getRecordType())
					&& PennantConstants.RECORD_TYPE_NEW.equals(fap.getRecordType())
					&& DisbursementConstants.PAYMENT_TYPE_NEFT.equals(fap.getPaymentType())
					&& !DisbursementConstants.STATUS_AWAITCON.equals(fap.getStatus())
					&& !DisbursementConstants.STATUS_PAID.equals(fap.getStatus())
					&& !DisbursementConstants.STATUS_REALIZED.equals(fap.getStatus())
					&& !DisbursementConstants.STATUS_REJECTED.equals(fap.getStatus())
					&& !DisbursementConstants.STATUS_CANCEL.equals(fap.getStatus())) {

				BigDecimal maxAmtPerInstruction = instrumentwiseLimit.getMaxAmtPerInstruction();
				if (fap.getAmtToBeReleased().compareTo(maxAmtPerInstruction) > 0) {

					if (BigDecimal.ZERO.compareTo(maxAmtPerInstruction) == 0) {
						throw new InterfaceException("NEFT",
								"For NEFT requests Max Amount Per Instruction should be greater than zero.");
					}

					BigDecimal noOfRecords = fap.getAmtToBeReleased().divide(maxAmtPerInstruction, 0, RoundingMode.UP);
					int records = noOfRecords.intValueExact();
					BigDecimal totAmount = BigDecimal.ZERO;

					for (int i = 1; i <= records; i++) {
						FinAdvancePayments finAdvPay = new FinAdvancePayments();
						BeanUtils.copyProperties(fap, finAdvPay);
						finAdvPay.setPaymentSeq(seqNo);
						if (records == i) {
							finAdvPay.setAmtToBeReleased(fap.getAmtToBeReleased().subtract(totAmount));
						} else {
							finAdvPay.setAmtToBeReleased(maxAmtPerInstruction);
						}
						finAdvPay.setPaymentId(Long.MIN_VALUE);
						finAdvPay.setNewRecord(true);
						finAdvPay.setLLDate(SysParamUtil.getAppDate());
						totAmount = totAmount.add(maxAmtPerInstruction);
						seqNo++;
						finalPaymentsList.add(finAdvPay);
					}
				} else {
					fap.setLLDate(SysParamUtil.getAppDate());
					finalPaymentsList.add(fap);
				}
			} else {
				finalPaymentsList.add(fap);
			}
		}

		logger.debug(Literal.LEAVING);

		return finalPaymentsList;
	}

	private int getNextPaymentSequence(List<FinAdvancePayments> fapList) {
		int seqNo = 0;
		if (fapList != null && !fapList.isEmpty()) {
			for (FinAdvancePayments fap : fapList) {
				int tempId = fap.getPaymentSeq();
				if (tempId > seqNo) {
					seqNo = tempId;
				}
			}
		}
		return seqNo + 1;
	}

	private AuditDetail validateAdvancePayment(AuditDetail auditDetail, String usrLanguage, String method,
			FinanceDetail fd, boolean isApi) {
		logger.debug(Literal.ENTERING);

		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		FinAdvancePayments finAdvancePay = (FinAdvancePayments) auditDetail.getModelData();
		FinAdvancePayments tempFinAdvancePay = null;
		boolean isOverDraft = false;
		int maxODDays = SysParamUtil.getValueAsInt(SMTParameterConstants.MAX_ODDAYS_ADDDISB);

		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		long finID = fm.getFinID();
		String finReference = fm.getFinReference();

		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, fm.getProductCategory())) {
			isOverDraft = true;
		}

		if (finAdvancePay.isWorkflow()) {
			tempFinAdvancePay = finAdvancePaymentsDAO.getFinAdvancePaymentsById(finAdvancePay, "_Temp");
		}

		FinAdvancePayments befFinAdvancePay = finAdvancePaymentsDAO.getFinAdvancePaymentsById(finAdvancePay, "");
		FinAdvancePayments oldFinAdvancePay = finAdvancePay.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = finAdvancePay.getFinReference();
		errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ": " + valueParm[0];

		if (finAdvancePay.isNewRecord()) { // for New record or new record into work flow

			if (!finAdvancePay.isWorkflow()) {// With out Work flow only new records
				if (befFinAdvancePay != null) { // Record Already Exists in the table then error
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow
				if (finAdvancePay.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befFinAdvancePay != null || tempFinAdvancePay != null) { // if records already exists in the
																					// main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
					}
				} else { // if records not exists in the Main flow table
					if (befFinAdvancePay == null || tempFinAdvancePay != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!finAdvancePay.isWorkflow()) { // With out Work flow for update and delete

				if (befFinAdvancePay == null) { // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, valueParm), usrLanguage));
				} else {
					if (oldFinAdvancePay != null
							&& !oldFinAdvancePay.getLastMntOn().equals(befFinAdvancePay.getLastMntOn())) {
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

				if (tempFinAdvancePay == null) { // if records not exists in the Work flow table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}

				if (tempFinAdvancePay != null && oldFinAdvancePay != null
						&& !oldFinAdvancePay.getLastMntOn().equals(tempFinAdvancePay.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}
			}
		}

		// validation related to Scheduled Disbursement date And Disbursement date should be same.
		boolean noValidation = isnoValidationUserAction(fd.getUserAction());
		if (!noValidation && !isDeleteRecord(finAdvancePay) && !finAdvancePay.ispOIssued()) {
			List<FinanceDisbursement> finDisbursementDetails = schdData.getDisbursementDetails();
			for (FinanceDisbursement finDisbursmentDetail : finDisbursementDetails) {
				if (finAdvancePay.getDisbSeq() == finDisbursmentDetail.getDisbSeq() && finAdvancePay.getLlDate() != null
						&& DateUtil.compare(finDisbursmentDetail.getDisbDate(), finAdvancePay.getLlDate()) != 0) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "65032", errParm, valueParm), usrLanguage));
				}
			}

			String partnerBankBankcode = partnerBankService.getBankCodeById(finAdvancePay.getPartnerBankID());
			if (ImplementationConstants.VALIDATE_BENFICIARY_ACCOUNT && bankAccountValidationService != null) {
				int count = pennyDropService.getPennyDropCount(finAdvancePay.getBeneficiaryAccNo(),
						finAdvancePay.getiFSC());

				if (count == 0) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41020", errParm, valueParm), usrLanguage));
				}
			}

			if (!StringUtils.equals(finAdvancePay.getBranchBankCode(), partnerBankBankcode)) {
				if (StringUtils.equals(finAdvancePay.getPaymentType(), DisbursementConstants.PAYMENT_TYPE_IFT)) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "65033", errParm, valueParm), usrLanguage));
				}
			} else if (!StringUtils.equals(finAdvancePay.getPaymentType(), DisbursementConstants.PAYMENT_TYPE_IFT)
					&& !isApi) {
				String[] errParam = new String[1];
				errParam[0] = finAdvancePay.getPaymentType();
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
						new ErrorDetail(PennantConstants.KEY_FIELD, "65038", errParam, valueParm), usrLanguage));
			}

		}

		// Validation for Automatic Blocking of Add Disbursement after N due days from Overdue schedules.
		if (isOverDraft) {
			int currODDays = profitDetailsDAO.getCurOddays(finID);
			if (currODDays != 0 && currODDays > maxODDays) {
				String[] errParam = new String[2];
				errParam[0] = finReference;
				errParam[1] = String.valueOf(maxODDays);
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
						new ErrorDetail(PennantConstants.KEY_FIELD, "ADM003", errParam, valueParm), usrLanguage));
			}
		}

		if (covenantsService != null && !"doReject".equals(StringUtils.trimToEmpty(method))) {
			auditDetail.setErrorDetails(
					covenantsService.validatePDDDocuments(finReference, auditDetail.getErrorDetails()));
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !finAdvancePay.isWorkflow()) {
			auditDetail.setBefImage(befFinAdvancePay);
		}
		return auditDetail;
	}

	@Override
	public void processDisbursments(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);

		List<FinAdvancePayments> finAdvancePayList = financeDetail.getAdvancePaymentsList();

		if (CollectionUtils.isEmpty(finAdvancePayList)) {
			return;
		}

		FinanceMain fm = financeDetail.getFinScheduleData().getFinanceMain();
		boolean save = false;
		long finID = fm.getFinID();
		String finReference = fm.getFinReference();

		PayOrderIssueHeader poiHeader = payOrderIssueHeaderDAO.getPayOrderIssueByHeaderRef(finID, "");

		if (poiHeader == null) {
			save = true;
			poiHeader = new PayOrderIssueHeader();
			poiHeader.setFinID(finID);
			poiHeader.setFinReference(finReference);
			poiHeader.setVersion(1);
			poiHeader.setLastMntBy(fm.getLastMntBy());
			poiHeader.setLastMntOn(fm.getLastMntOn());
			poiHeader.setRoleCode("");
			poiHeader.setNextRoleCode("");
			poiHeader.setTaskId("");
			poiHeader.setNextTaskId("");
			poiHeader.setRecordType("");
			poiHeader.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		}
		// get total amount from disbursement details
		BigDecimal totPOAmount = BigDecimal.ZERO;
		FinScheduleData schd = financeDetail.getFinScheduleData();
		if (schd != null && schd.getDisbursementDetails() != null) {
			for (FinanceDisbursement disbursement : schd.getDisbursementDetails()) {
				if (disbursement.getLinkedDisbId() != 0) {
					continue;
				}
				totPOAmount = totPOAmount.add(disbursement.getDisbAmount());
			}
		}

		BigDecimal totPOdueAmt = BigDecimal.ZERO;
		int totpoCount = 0;
		int totdueCount = 0;

		for (FinAdvancePayments fap : finAdvancePayList) {
			if (!fap.ispOIssued()) {
				totPOdueAmt = totPOdueAmt.add(fap.getAmtToBeReleased());
				totdueCount++;
			}

			totpoCount++;
		}

		poiHeader.setTotalPOCount(totpoCount);
		poiHeader.setpODueCount(totdueCount);
		poiHeader.setTotalPOAmount(totPOAmount);
		poiHeader.setpODueAmount(totPOdueAmt);
		if (save) {
			payOrderIssueHeaderDAO.save(poiHeader, "");
		} else {
			poiHeader.setVersion(poiHeader.getVersion() + 1);
			payOrderIssueHeaderDAO.update(poiHeader, "");
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public List<AuditDetail> processQuickDisbursment(FinanceDetail fd, String tableType, String tranType) {
		List<AuditDetail> auditDetails = new ArrayList<>();
		List<FinAdvancePayments> finAdvancePayList = fd.getAdvancePaymentsList();

		if (CollectionUtils.isEmpty(finAdvancePayList)) {
			return auditDetails;
		}

		String moduleDefiner = fd.getModuleDefiner();
		FinanceMain fm = fd.getFinScheduleData().getFinanceMain();
		String recordStatus = fm.getRecordStatus();

		if (isQDPProcess(fd) && FinServiceEvent.ORG.equals(moduleDefiner)) {
			processDisbursments(fd);
			// Postings preparation

			if (!ImplementationConstants.HOLD_DISB_INST_POST) {
				generateAccounting(fd);
			}

			auditDetails.addAll(doApprove(finAdvancePayList, "", PennantConstants.TRAN_ADD, fd.isDisbStp()));
			delete(fd.getAdvancePaymentsList(), "_Temp", "");
			return auditDetails;
		} else {
			if (ImplementationConstants.ALW_QDP_CUSTOMIZATION && fm.isQuickDisb()
					&& FinServiceEvent.ORG.equals(moduleDefiner)) {
				if (PennantConstants.RCD_STATUS_APPROVED.equals(recordStatus)) {
					int i = 0;
					for (FinAdvancePayments fap : finAdvancePayList) {
						if (DisbursementConstants.STATUS_PRINT.equalsIgnoreCase(fap.getStatus())) {
							String paymentType = fap.getPaymentType();
							if (DisbursementConstants.PAYMENT_TYPE_CHEQUE.equals(paymentType)
									|| DisbursementConstants.PAYMENT_TYPE_DD.equals(paymentType)) {
								fap.setStatus(DisbursementConstants.STATUS_PAID);
								++i;
								finAdvancePaymentsDAO.updateLLDate(fap, "");
								String[] fields = PennantJavaUtil.getFieldDetails(fap, fap.getExcludeFields());
								auditDetails.add(
										new AuditDetail(tranType, i, fields[0], fields[1], fap.getBefImage(), fap));
							}
						}
					}
					return auditDetails;
				} else if (PennantConstants.RCD_STATUS_RESUBMITTED.equals(recordStatus)) {
					int i = 0;
					for (FinAdvancePayments fap : finAdvancePayList) {
						if (DisbursementConstants.STATUS_PRINT.equalsIgnoreCase(fap.getStatus())) {
							fap.setStatus(DisbursementConstants.STATUS_REJECTED);
							++i;
							finAdvancePaymentsDAO.updateLLDate(fap, "");
							String[] fields = PennantJavaUtil.getFieldDetails(fap, fap.getExcludeFields());
							auditDetails
									.add(new AuditDetail(tranType, i, fields[0], fields[1], fap.getBefImage(), fap));
						}
					}
					return auditDetails;
				} else {
					auditDetails.addAll(saveOrUpdate(finAdvancePayList, tableType, tranType, fd.isDisbStp()));
				}
			} else {
				auditDetails.addAll(saveOrUpdate(finAdvancePayList, tableType, tranType, fd.isDisbStp()));
			}

		}

		return auditDetails;
	}

	private boolean isQDPProcess(FinanceDetail financeDetail) {
		FinanceMain finmain = financeDetail.getFinScheduleData().getFinanceMain();
		String nextrole = finmain.getNextRoleCode();
		String role = finmain.getRoleCode();
		boolean process = false;

		if (!financeDetail.isActionSave() && !StringUtils.equals(nextrole, role)) {
			// Checking Authority i.e Is current Role contains authority (or) Not
			List<FinanceReferenceDetail> limitCheckList = limitCheckDetails.doLimitChek(role, finmain.getFinType());

			if (CollectionUtils.isNotEmpty(limitCheckList)) {
				for (FinanceReferenceDetail finRefDetail : limitCheckList) {
					if (StringUtils.equals(finRefDetail.getLovDescNamelov(), FinanceConstants.QUICK_DISBURSEMENT)) {
						process = true;
						break;
					}
				}
			}
		}

		return process;
	}

	private void generateAccounting(FinanceDetail fd) {
		FinanceMain fm = fd.getFinScheduleData().getFinanceMain();

		PostingDTO postingDTO = new PostingDTO();
		postingDTO.setFinanceDetail(fd);
		postingDTO.setUserBranch(fm.getFinBranch());

		AccountingEngine.post(AccountingEvent.DISBINS, postingDTO);
	}

	@Override
	public List<AuditDetail> processAPIQuickDisbursment(FinanceDetail fd, String tableType, String auditTranType) {
		List<AuditDetail> auditDetails = new ArrayList<>();
		List<FinAdvancePayments> finAdvancePayList = fd.getAdvancePaymentsList();

		if (CollectionUtils.isEmpty(finAdvancePayList)) {
			return auditDetails;
		}
		processDisbursments(fd);
		auditDetails.addAll(doApprove(finAdvancePayList, "", PennantConstants.TRAN_ADD, fd.isDisbStp()));
		return auditDetails;
	}

	@Override
	public void doCancel(FinanceDetail fd) {
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		long finID = fm.getFinID();
		String finReference = fm.getFinReference();

		List<FinanceDisbursement> disbursements = schdData.getDisbursementDetails();

		List<FinanceDisbursement> canceldDisbList = new ArrayList<FinanceDisbursement>();
		for (FinanceDisbursement disbursement : disbursements) {
			if (StringUtils.equals(disbursement.getDisbStatus(), FinanceConstants.DISB_STATUS_CANCEL)) {
				canceldDisbList.add(disbursement);
			}
		}

		for (FinanceDisbursement disbursement : canceldDisbList) {
			FinAdvancePayments advancePayments = new FinAdvancePayments();

			advancePayments.setFinID(finID);
			advancePayments.setFinReference(finReference);
			advancePayments.setDisbSeq(disbursement.getDisbSeq());
			advancePayments.setStatus(DisbursementConstants.STATUS_CANCEL);

			finAdvancePaymentsDAO.updateStatus(advancePayments, "");
		}

	}

	@Override
	public List<ErrorDetail> validateFinAdvPayments(FinanceDetail fd, boolean loanApproved) {
		logger.debug(Literal.ENTERING);

		List<FinAdvancePayments> list = fd.getAdvancePaymentsList();
		FinScheduleData schdData = fd.getFinScheduleData();

		List<FinanceDisbursement> disbursements = schdData.getDisbursementDetails();
		FinanceMain fm = schdData.getFinanceMain();

		int ccyFormat = CurrencyUtil.getFormat(fm.getFinCcy());
		FinanceDisbursement totDisb = getTotal(disbursements, fm, 0, false);

		BigDecimal netFinAmount = totDisb.getDisbAmount();

		List<ErrorDetail> errorList = new ArrayList<>();
		boolean checkMode = true;

		BigDecimal totDisbAmt = BigDecimal.ZERO;
		Map<Integer, BigDecimal> map = new HashMap<>();

		for (FinAdvancePayments fap : list) {
			if (DisbursementConstants.PAYMENT_DETAIL_VAS.equals(fap.getPaymentDetail())) {
				continue;
			}

			if (!isDeleteRecord(fap)) {
				int key = fap.getDisbSeq();

				BigDecimal totalGroupAmt = map.get(key);
				if (totalGroupAmt == null) {
					totalGroupAmt = BigDecimal.ZERO;
				}

				totalGroupAmt = totalGroupAmt.add(fap.getAmtToBeReleased());
				map.put(key, totalGroupAmt);
				totDisbAmt = totDisbAmt.add(fap.getAmtToBeReleased());
			}
		}

		BigDecimal feeAmount = calculateAdvanceAmount(schdData, fm);

		if (netFinAmount.subtract(feeAmount).compareTo(totDisbAmt) != 0 && (loanApproved || fm.isQuickDisb())) {
			String[] valueParm = new String[2];
			valueParm[0] = PennantApplicationUtil.amountFormate(totDisbAmt, ccyFormat);
			valueParm[1] = PennantApplicationUtil.amountFormate(netFinAmount.subtract(feeAmount), ccyFormat);
			errorList.add(new ErrorDetail("60401", valueParm));
			return errorList;
		}

		if (!checkMode && fm.isQuickDisb()) {
			errorList.add(new ErrorDetail("60402", null));
			return errorList;
		}

		if (!loanApproved) {
			logger.debug(Literal.LEAVING);
			return errorList;
		}

		long finID = fm.getFinID();
		List<FinanceDisbursement> approvedList = financeDisbursementDAO.getFinanceDisbursementDetails(finID, "", false);

		boolean approvedDisbursement;
		BigDecimal singletDisbursment = BigDecimal.ZERO;
		BigDecimal totalGroupAmt = BigDecimal.ZERO;
		Date disbDate = new Date();

		for (FinanceDisbursement disbursement : disbursements) {
			approvedDisbursement = false;

			if (FinanceConstants.DISB_STATUS_CANCEL.equals(disbursement.getDisbStatus())) {
				continue;
			}

			for (FinanceDisbursement aprovedDisbursement : approvedList) {
				if (disbursement.getDisbSeq() == aprovedDisbursement.getDisbSeq()) {
					approvedDisbursement = true;
					break;
				}
			}

			if (approvedDisbursement) {
				continue;
			}

			disbDate = disbursement.getDisbDate();
			singletDisbursment = disbursement.getDisbAmount();

			if (disbDate.compareTo(fm.getFinStartDate()) == 0 && disbursement.getDisbSeq() == 1) {
				singletDisbursment = singletDisbursment.subtract(fm.getDownPaySupl());
				singletDisbursment = singletDisbursment.subtract(fm.getDeductFeeDisb());
				singletDisbursment = singletDisbursment.subtract(feeAmount);
				if (FinanceConstants.BPI_DISBURSMENT.equals(fm.getBpiTreatment())) {
					singletDisbursment = singletDisbursment.subtract(fm.getBpiAmount());
				}
			} else if (disbursement.getDisbSeq() > 1) {
				singletDisbursment = singletDisbursment.subtract(disbursement.getDeductFeeDisb());

			}
			int key = disbursement.getDisbSeq();

			totalGroupAmt = map.get(key);
			if (totalGroupAmt == null) {
				totalGroupAmt = BigDecimal.ZERO;
			}
		}

		if (singletDisbursment.compareTo(totalGroupAmt) != 0 && "#".equals(fm.getAdvType())
				&& "#".equals(fm.getGrcAdvType())) {
			String errorDesc = DateUtil.formatToLongDate(disbDate);
			ErrorDetail error = new ErrorDetail("60404", new String[] { errorDesc });
			errorList.add(error);
			logger.debug(Literal.LEAVING);
			return errorList;
		}

		logger.debug(Literal.LEAVING);
		return errorList;

	}

	private static FinanceDisbursement getTotal(List<FinanceDisbursement> list, FinanceMain main, int seq,
			boolean group) {
		BigDecimal totdisbAmt = BigDecimal.ZERO;
		Date date = null;
		if (list != null && !list.isEmpty()) {
			for (FinanceDisbursement financeDisbursement : list) {
				if (group && seq != financeDisbursement.getDisbSeq()) {
					continue;
				}

				// exclude inst based schedules
				if (financeDisbursement.getLinkedDisbId() != 0) {
					continue;
				}

				if (!group) {
					if (StringUtils.equals(FinanceConstants.DISB_STATUS_CANCEL, financeDisbursement.getDisbStatus())) {
						continue;
					}
				}

				date = financeDisbursement.getDisbDate();

				// check is first disbursement to make sure the we deducted from first disbursement date
				if (financeDisbursement.getDisbDate().getTime() == main.getFinStartDate().getTime()
						&& financeDisbursement.getDisbSeq() == 1) {
					totdisbAmt = totdisbAmt.subtract(main.getDownPaySupl());
					totdisbAmt = totdisbAmt.subtract(main.getDeductFeeDisb());
					if (StringUtils.trimToEmpty(main.getBpiTreatment()).equals(FinanceConstants.BPI_DISBURSMENT)) {
						totdisbAmt = totdisbAmt.subtract(main.getBpiAmount());
					}
				} else if (financeDisbursement.getDisbSeq() > 1) {
					totdisbAmt = totdisbAmt.subtract(financeDisbursement.getDeductFeeDisb());

				}
				totdisbAmt = totdisbAmt.add(financeDisbursement.getDisbAmount());
			}
		}

		FinanceDisbursement disbursement = new FinanceDisbursement();
		disbursement.setDisbAmount(totdisbAmt);
		if (date != null) {
			disbursement.setDisbDate(date);
		}
		return disbursement;

	}

	/**
	 * Method for validating VAS Instructions.<br>
	 * Every VAS if the fee is greater than 0 then it is having an instruction <br>
	 * Every VAS Instruction must be in the fee with amount greater than 0.<br>
	 * 
	 * @param advPaymentsList
	 * @param validate
	 * @return
	 */
	public List<ErrorDetail> validateVasInstructions(List<VASRecording> vasRecordingList,
			List<FinAdvancePayments> advPaymentsList, boolean validate) {
		logger.debug(Literal.ENTERING);
		List<ErrorDetail> errors = new ArrayList<ErrorDetail>();
		if (!validate) {
			logger.debug(Literal.LEAVING);
			return errors;
		}

		// If the fee is greater than 0 then it is having an instruction.
		if (CollectionUtils.isNotEmpty(vasRecordingList)) {
			for (VASRecording vas : vasRecordingList) {
				if (vas.getFee().compareTo(BigDecimal.ZERO) <= 0) {
					continue;
				}

				// if the given VAS fee has no Instruction
				FinAdvancePayments advancePayments = getVasInstruction(advPaymentsList, vas.getVasReference());
				if (advancePayments == null) {
					String[] errParm = new String[1];
					String[] valueParm = new String[1];
					errParm[0] = vas.getVasReference();
					valueParm[0] = vas.getVasReference();
					ErrorDetail error = new ErrorDetail(PennantConstants.KEY_FIELD, "VINST01", errParm, valueParm);
					errors.add(error);
					// if the given VAS fee has not matched with Instruction amount
				} else if (vas.getFee().compareTo(advancePayments.getAmtToBeReleased()) != 0) {
					String[] errParm = new String[1];
					String[] valueParm = new String[1];
					errParm[0] = vas.getVasReference();
					valueParm[0] = vas.getVasReference();
					ErrorDetail error = new ErrorDetail(PennantConstants.KEY_FIELD, "VINST03", errParm, valueParm);
					errors.add(error);
				}
			}
		}

		// VAS Instruction must be in the fee with amount greater than 0
		if (CollectionUtils.isNotEmpty(advPaymentsList)) {
			for (FinAdvancePayments fap : advPaymentsList) {
				if (!DisbursementConstants.PAYMENT_DETAIL_VAS.equals(fap.getPaymentDetail())) {
					continue;
				}

				if (isDeleteRecord(fap)) {
					continue;
				}

				// if instruction is there and finance having no VasRecording
				VASRecording vasRecording = getVasInst(fap.getVasReference(), vasRecordingList);
				if (vasRecording == null) {
					String[] errParm = new String[1];
					String[] valueParm = new String[1];
					errParm[0] = fap.getVasReference();
					valueParm[0] = fap.getVasReference();
					ErrorDetail error = new ErrorDetail(PennantConstants.KEY_FIELD, "VINST02", errParm, valueParm);
					errors.add(error);
				}
			}
		}
		logger.debug(Literal.LEAVING);
		return errors;
	}

	private FinAdvancePayments getVasInstruction(List<FinAdvancePayments> list, String vasReference) {
		if (CollectionUtils.isEmpty(list)) {
			return null;
		}

		for (FinAdvancePayments fap : list) {

			if (!DisbursementConstants.PAYMENT_DETAIL_VAS.equals(fap.getPaymentDetail())) {
				continue;
			}

			if (!StringUtils.equals(vasReference, fap.getVasReference())) {
				continue;
			}

			if (isDeleteRecord(fap)) {
				continue;
			}

			return fap;
		}

		return null;
	}

	@Override
	public List<FinAdvancePayments> processVasInstructions(List<VASRecording> vasRecordingList,
			List<FinAdvancePayments> curAdvPaymentsList, String entityCode) {
		logger.debug(Literal.ENTERING);

		if (curAdvPaymentsList == null) {
			curAdvPaymentsList = new ArrayList<>(1);
		}

		// Process VASRecording
		if (CollectionUtils.isNotEmpty(vasRecordingList)) {
			for (VASRecording vasRecording : vasRecordingList) {

				if (isVasInstExists(curAdvPaymentsList, vasRecording.getVasReference())) {
					continue;
				}

				VASConfiguration configuration = vasRecording.getVasConfiguration();

				if (configuration == null) {
					configuration = this.vASConfigurationDAO.getVASConfigurationByCode(vasRecording.getProductCode(),
							"");
				}

				if (configuration == null) {
					continue;
				}

				VASProviderAccDetail vasProvAccDetail = vASProviderAccDetailDAO
						.getVASProviderAccDetByPRoviderId(configuration.getManufacturerId(), entityCode, "_Aview");

				if (vasProvAccDetail == null) {
					continue;
				}

				FinAdvancePayments fap = new FinAdvancePayments();
				fap.setFinReference(vasRecording.getFinReference());
				fap.setVasReference(vasRecording.getVasReference());
				fap.setNewRecord(true);
				fap.setWorkflowId(0);
				fap.setPaymentSeq(getNextPaymentSequence(curAdvPaymentsList));
				fap.setPaymentDetail(DisbursementConstants.PAYMENT_DETAIL_VAS);
				fap.setRecordType(PennantConstants.RCD_ADD);
				fap.setVersion(1);
				fap.setRecordStatus("");
				fap.setDisbCCy("INR");
				fap.setDisbSeq(1);
				fap.setStatus(DisbursementConstants.STATUS_NEW);
				fap.setLLDate(SysParamUtil.getAppDate());
				fap.setAmtToBeReleased(vasRecording.getFee());
				fap.setPaymentType(vasProvAccDetail.getPaymentMode());
				fap.setPartnerBankID(vasProvAccDetail.getPartnerBankId());
				fap.setPartnerbankCode(vasProvAccDetail.getPartnerBankCode());
				fap.setPartnerBankName(vasProvAccDetail.getPartnerBankName());
				fap.setBankCode(vasProvAccDetail.getBankCode());
				fap.setBankName(vasProvAccDetail.getBankName());
				fap.setLiabilityHoldName(vasProvAccDetail.getProviderDesc());
				fap.setPrintingLoc(vasProvAccDetail.getBranchCode());
				fap.setPrintingLocDesc(vasProvAccDetail.getBranchDesc());
				fap.setBankBranchID(vasProvAccDetail.getBankBranchID());
				fap.setCity(vasProvAccDetail.getBranchCity());
				fap.setBranchBankCode(vasProvAccDetail.getBankCode());
				fap.setBranchBankName(vasProvAccDetail.getBankName());
				fap.setBranchDesc(vasProvAccDetail.getBranchDesc());
				fap.setiFSC(vasProvAccDetail.getIfscCode());
				fap.setBeneficiaryAccNo(vasProvAccDetail.getAccountNumber());
				fap.setBeneficiaryName(vasProvAccDetail.getProviderDesc());

				curAdvPaymentsList.add(fap);
			}
		}

		List<FinAdvancePayments> updateList = new ArrayList<>(1);
		for (FinAdvancePayments fap : curAdvPaymentsList) {
			String vasReference = fap.getVasReference();

			if (StringUtils.isBlank(vasReference) || PennantConstants.List_Select.equals(vasReference)) {
				updateList.add(fap);
				continue;
			}

			VASRecording vasRecording = getVasInst(fap.getVasReference(), vasRecordingList);
			if (vasRecording == null) {
				// need to remove that instruction;
				if (PennantConstants.RCD_ADD.equals(fap.getRecordType())) {
					//
				} else {
					fap.setRecordType(PennantConstants.RECORD_TYPE_CAN);
					updateList.add(fap);
				}
				continue;
			}

		}

		logger.debug(Literal.LEAVING);
		return updateList;
	}

	private VASRecording getVasInst(String vasReference, List<VASRecording> vasRecordingList) {
		if (CollectionUtils.isEmpty(vasRecordingList)) {
			return null;
		}

		for (VASRecording vasRecording : vasRecordingList) {
			if (StringUtils.equals(vasReference, vasRecording.getVasReference())) {
				if (PennantConstants.RECORD_TYPE_CAN.equals(vasRecording.getRecordType())) {
					return null;
				}
				return vasRecording;
			}
		}

		return null;
	}

	private boolean isVasInstExists(List<FinAdvancePayments> curAdvPaymentsList, String vasReference) {
		logger.debug(Literal.ENTERING);
		if (CollectionUtils.isEmpty(curAdvPaymentsList)) {
			return false;
		}
		for (FinAdvancePayments fap : curAdvPaymentsList) {
			if (StringUtils.equals(vasReference, fap.getVasReference())) {
				return true;
			}
		}
		logger.debug(Literal.LEAVING);
		return false;
	}

	private boolean isDeleteRecord(FinAdvancePayments aFinAdvancePayments) {
		String recordType = aFinAdvancePayments.getRecordType();
		String status = aFinAdvancePayments.getStatus();
		if (PennantConstants.RECORD_TYPE_CAN.equals(recordType) || PennantConstants.RECORD_TYPE_DEL.equals(recordType)
				|| DisbursementConstants.STATUS_CANCEL.equals(status)
				|| DisbursementConstants.STATUS_REJECTED.equals(status)
				|| DisbursementConstants.STATUS_PAID_BUT_CANCELLED.equals(status)) {
			return true;
		}
		return false;
	}

	private boolean isnoValidationUserAction(String userAction) {
		boolean noValidation = false;
		if (userAction != null) {
			if ("Cancel".equalsIgnoreCase(userAction) || userAction.contains("Reject")
					|| userAction.contains("Resubmit") || userAction.contains("Decline")) {
				noValidation = true;
			}
		}
		return noValidation;
	}

	private void saveDocumentDetails(FinAdvancePayments finPayment) {
		DocumentDetails details = new DocumentDetails();
		details.setDocModule(DisbursementConstants.DISB_MODULE);
		details.setDoctype(finPayment.getDocType());
		details.setDocCategory(DisbursementConstants.DISB_DOC_TYPE);
		details.setDocName(finPayment.getDocumentName());
		details.setLastMntBy(finPayment.getLastMntBy());
		details.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		details.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		details.setFinEvent(FinServiceEvent.ORG);
		details.setDocImage(finPayment.getDocImage());
		details.setReferenceId(String.valueOf(finPayment.getPaymentId()));
		details.setFinReference(finPayment.getFinReference());

		saveDocument(DMSModule.FINANCE, DMSModule.DISBINST, details);
		DocumentDetails oldDocumentDetails = documentDetailsDAO.getDocumentDetails(details.getReferenceId(),
				details.getDocCategory(), details.getDocModule(), TableType.MAIN_TAB.getSuffix());

		if (oldDocumentDetails != null && oldDocumentDetails.getDocId() != Long.MIN_VALUE) {
			details.setDocId(oldDocumentDetails.getDocId());
			documentDetailsDAO.update(details, TableType.MAIN_TAB.getSuffix());
		} else {
			documentDetailsDAO.save(details, TableType.MAIN_TAB.getSuffix());
		}
	}

	private BigDecimal calculateAdvanceAmount(FinScheduleData schdData, FinanceMain fm) {
		if ("#".equals(fm.getAdvType()) && "#".equals(fm.getGrcAdvType())) {
			return BigDecimal.ZERO;
		}

		BigDecimal feeAmount = BigDecimal.ZERO;

		List<FinFeeDetail> fees = schdData.getFinFeeDetailList();

		for (FinFeeDetail fee : fees) {
			FinFeeDetail feeTemp = new FinFeeDetail();
			feeTemp.setFeeTypeCode(fee.getFeeTypeCode());
			AdvancePaymentUtil.calculateLOSAdvPayment(schdData, feeTemp);
			feeAmount = feeAmount.add(feeTemp.getActualAmount());
		}

		return feeAmount;
	}

	@Override
	public void processPayments(PaymentTransaction paymentTransaction) {
		this.paymentsProcessService.processPayments(paymentTransaction);
	}

	@Override
	public void Update(long paymentId, long linkedTranId) {
		finAdvancePaymentsDAO.update(paymentId, linkedTranId);

	}

	@Override
	public int getMaxPaymentSeq(long finID) {
		return finAdvancePaymentsDAO.getMaxPaymentSeq(finID);
	}

	@Override
	public int getFinAdvCountByRef(long finID, String type) {
		return finAdvancePaymentsDAO.getFinAdvCountByRef(finID, type);
	}

	@Override
	public int getCountByFinReference(long finID) {
		return finAdvancePaymentsDAO.getCountByFinReference(finID);
	}

	@Override
	public List<FinAdvancePayments> getFinAdvancePaymentByFinRef(long finID, Date toDate) {
		return finAdvancePaymentsDAO.getFinAdvancePaymentByFinRef(finID, toDate, "");
	}

	@Override
	public void updateStatus(FinAdvancePayments finAdvancePayment, String type) {
		finAdvancePaymentsDAO.updateStatus(finAdvancePayment, type);
	}

	@Override
	public void updatePaymentStatus(FinAdvancePayments finAdvancePayment, String type) {
		finAdvancePaymentsDAO.updatePaymentStatus(finAdvancePayment, type);
	}

	@Override
	public FinAdvancePayments getFinAdvancePaymentsById(FinAdvancePayments finAdvancePayments, String type) {
		return finAdvancePaymentsDAO.getFinAdvancePaymentsById(finAdvancePayments, type);
	}

	@Override
	public int getCountByPaymentId(long finID, long paymentId) {
		return finAdvancePaymentsDAO.getCountByPaymentId(finID, paymentId);
	}

	@Override
	public int getStatusCountByFinRefrence(long finID) {
		return finAdvancePaymentsDAO.getStatusCountByFinRefrence(finID);
	}

	@Autowired
	public void setFinAdvancePaymentsDAO(FinAdvancePaymentsDAO finAdvancePaymentsDAO) {
		this.finAdvancePaymentsDAO = finAdvancePaymentsDAO;
	}

	@Autowired
	public void setPayOrderIssueHeaderDAO(PayOrderIssueHeaderDAO payOrderIssueHeaderDAO) {
		this.payOrderIssueHeaderDAO = payOrderIssueHeaderDAO;
	}

	@Autowired
	public void setLimitCheckDetails(LimitCheckDetails limitCheckDetails) {
		this.limitCheckDetails = limitCheckDetails;
	}

	@Autowired
	public void setPartnerBankService(PartnerBankService partnerBankService) {
		this.partnerBankService = partnerBankService;
	}

	@Autowired
	public void setProfitDetailsDAO(FinanceProfitDetailDAO profitDetailsDAO) {
		this.profitDetailsDAO = profitDetailsDAO;
	}

	@Autowired
	public void setCovenantsService(CovenantsService covenantsService) {
		this.covenantsService = covenantsService;
	}

	@Autowired
	public void setInstrumentwiseLimitService(InstrumentwiseLimitService instrumentwiseLimitService) {
		this.instrumentwiseLimitService = instrumentwiseLimitService;
	}

	@Autowired
	public void setFinanceDisbursementDAO(FinanceDisbursementDAO financeDisbursementDAO) {
		this.financeDisbursementDAO = financeDisbursementDAO;
	}

	@Autowired
	public void setPaymentsProcessService(PaymentsProcessService paymentsProcessService) {
		this.paymentsProcessService = paymentsProcessService;
	}

	@Autowired
	public void setPennyDropService(PennyDropService pennyDropService) {
		this.pennyDropService = pennyDropService;
	}

	@Autowired(required = false)
	public void setBankAccountValidationService(BankAccountValidationService bankAccountValidationService) {
		this.bankAccountValidationService = bankAccountValidationService;
	}

	@Autowired
	public void setDocumentDetailsDAO(DocumentDetailsDAO documentDetailsDAO) {
		this.documentDetailsDAO = documentDetailsDAO;
	}

	@Autowired
	public void setvASProviderAccDetailDAO(VASProviderAccDetailDAO vASProviderAccDetailDAO) {
		this.vASProviderAccDetailDAO = vASProviderAccDetailDAO;
	}

	@Autowired
	public void setvASConfigurationDAO(VASConfigurationDAO vASConfigurationDAO) {
		this.vASConfigurationDAO = vASConfigurationDAO;
	}

	@Autowired
	public void setOverdrafLoanService(OverdrafLoanService overdrafLoanService) {
		this.overdrafLoanService = overdrafLoanService;
	}

}