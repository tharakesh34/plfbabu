/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related PayOrderIssueHeaders. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. RepayOrderIssueHeaderion or retransmission of
 * the materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : PayOrderIssueHeaderServiceImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 12-08-2011 *
 * * Modified Date : 12-08-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 12-08-2011 Pennant 0.1 * 16-05-2018 Madhubabu 0.2 added the validation for * disbursement by checking Otc/PDD * * * *
 * * * *
 ********************************************************************************************
 */
package com.pennant.backend.service.payorderissue.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.zkoss.util.resource.Labels;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.beneficiary.BeneficiaryDAO;
import com.pennant.backend.dao.configuration.VASRecordingDAO;
import com.pennant.backend.dao.documentdetails.DocumentDetailsDAO;
import com.pennant.backend.dao.finance.FinAdvancePaymentsDAO;
import com.pennant.backend.dao.finance.FinCovenantTypeDAO;
import com.pennant.backend.dao.finance.FinFeeDetailDAO;
import com.pennant.backend.dao.finance.FinanceDisbursementDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.JointAccountDetailDAO;
import com.pennant.backend.dao.payorderissue.PayOrderIssueHeaderDAO;
import com.pennant.backend.dao.pennydrop.PennyDropDAO;
import com.pennant.backend.dao.rulefactory.PostingsDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.beneficiary.Beneficiary;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinCovenantType;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.payorderissue.PayOrderIssueHeader;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.FinAdvancePaymentsService;
import com.pennant.backend.service.partnerbank.PartnerBankService;
import com.pennant.backend.service.payment.PaymentsProcessService;
import com.pennant.backend.service.payorderissue.PayOrderIssueService;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.pff.accounting.model.PostingDTO;
import com.pennant.pff.core.engine.accounting.AccountingEngine;
import com.pennanttech.model.dms.DMSModule;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.pff.service.hook.PostValidationHook;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.external.BankAccountValidationService;

/**
 * Service implementation for methods that depends on <b>PayOrderIssueHeader</b>.<br>
 * 
 */
public class PayOrderIssueServiceImpl extends GenericService<PayOrderIssueHeader> implements PayOrderIssueService {
	private static final Logger logger = LogManager.getLogger(PayOrderIssueServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private PayOrderIssueHeaderDAO payOrderIssueHeaderDAO;
	private FinAdvancePaymentsDAO finAdvancePaymentsDAO;
	private BeneficiaryDAO beneficiaryDAO;
	private FinanceDisbursementDAO financeDisbursementDAO;
	private PostingsPreparationUtil postingsPreparationUtil;
	private FinanceMainDAO financeMainDAO;
	private FinAdvancePaymentsService finAdvancePaymentsService;
	private DocumentDetailsDAO documentDetailsDAO;
	private FinCovenantTypeDAO finCovenantTypeDAO;
	private VASRecordingDAO vasRecordingDAO;
	private PartnerBankService partnerBankService;
	private PostingsDAO postingsDAO;
	private PaymentsProcessService paymentsProcessService;
	private PennyDropDAO pennyDropDAO;
	private transient BankAccountValidationService bankAccountValidationService;
	private JointAccountDetailDAO jointAccountDetailDAO;
	private FinFeeDetailDAO finFeeDetailDAO;
	@Autowired(required = false)
	@Qualifier("payOrderIssuePostValidationHook")
	private PostValidationHook postValidationHook;

	public PayOrderIssueServiceImpl() {
		super();
	}

	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader, "saveOrUpdate");
		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}
		String tableType = "";
		PayOrderIssueHeader poih = (PayOrderIssueHeader) auditHeader.getAuditDetail().getModelData();

		if (poih.isWorkflow()) {
			tableType = "_Temp";
		}

		if (poih.isNewRecord()) {
			payOrderIssueHeaderDAO.save(poih, tableType);
		} else {
			payOrderIssueHeaderDAO.update(poih, tableType);
		}

		String rcdMaintainSts = FinServiceEvent.DISBINST;
		financeMainDAO.updateMaintainceStatus(poih.getFinID(), rcdMaintainSts);

		List<AuditDetail> details = processFinAdvancepayments(poih, tableType, null);
		auditDetails.addAll(details);

		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, poih.getBefImage(), poih));
		auditHeader.setAuditDetails(auditDetails);

		auditHeaderDAO.addAudit(auditHeader);
		logger.debug(Literal.LEAVING);
		return auditHeader;

	}

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		PayOrderIssueHeader poih = (PayOrderIssueHeader) auditHeader.getAuditDetail().getModelData();
		payOrderIssueHeaderDAO.delete(poih, "");
		List<FinAdvancePayments> list = poih.getFinAdvancePaymentsList();
		if (list != null && !list.isEmpty()) {
			finAdvancePaymentsDAO.deleteByFinRef(poih.getFinID(), "_Temp");
		}
		auditHeaderDAO.addAudit(auditHeader);
		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public PayOrderIssueHeader getPayOrderIssueHeaderById(long finID) {
		logger.debug(Literal.ENTERING);
		PayOrderIssueHeader issueHeader = payOrderIssueHeaderDAO.getPayOrderIssueByHeaderRef(finID, "_View");
		issueHeader.setLoanApproved(true);

		FinanceMain fm = financeMainDAO.getDisbursmentFinMainById(finID, TableType.MAIN_TAB);

		if (fm == null) {
			fm = financeMainDAO.getDisbursmentFinMainById(finID, TableType.TEMP_TAB);
			issueHeader.setLoanApproved(false);
		}

		String finReference = fm.getFinReference();

		issueHeader.setFinanceMain(fm);
		issueHeader.setFinAdvancePaymentsList(finAdvancePaymentsDAO.getFinAdvancePaymentsByFinRef(finID, "_View"));

		List<FinanceDisbursement> teemList = financeDisbursementDAO.getFinanceDisbursementDetails(finID,
				TableType.TEMP_TAB.getSuffix(), false);
		List<FinanceDisbursement> mainList = financeDisbursementDAO.getFinanceDisbursementDetails(finID,
				TableType.MAIN_TAB.getSuffix(), false);

		List<FinanceDisbursement> deductDisbFeeList = financeDisbursementDAO.getDeductDisbFeeDetails(finID);

		if (CollectionUtils.isNotEmpty(deductDisbFeeList)) {
			for (FinanceDisbursement disbursement : deductDisbFeeList) {
				for (FinanceDisbursement finDisb : mainList) {
					if (finDisb.getDisbSeq() == disbursement.getDisbSeq()) {
						finDisb.setDeductFeeDisb(disbursement.getDeductFeeDisb());
						break;
					}
				}
			}
		}

		// Covenants List
		issueHeader.setCovenantTypeList(finCovenantTypeDAO.getFinCovenantTypeByFinRef(finReference, "_View", false));
		// Document details
		issueHeader.setDocumentDetailsList(
				documentDetailsDAO.getDocumentDetailsByRef(finReference, FinanceConstants.MODULE_NAME, "", ""));

		if (issueHeader.isLoanApproved()) {
			issueHeader.setFinanceDisbursements(mainList);
		} else {
			issueHeader.setFinanceDisbursements(teemList);
		}
		String module = FinanceConstants.MODULE_NAME;
		String document = SysParamUtil.getValueAsString("DISB_DOC");

		if (StringUtils.isNotEmpty(document)) {
			DocumentDetails docDetails = null;
			if (issueHeader.isLoanApproved()) {
				docDetails = documentDetailsDAO.getDocumentDetails(fm.getFinReference(), document, module,
						TableType.MAIN_TAB.getSuffix());

				issueHeader.setFinanceDisbursements(mainList);

			} else {
				docDetails = documentDetailsDAO.getDocumentDetails(fm.getFinReference(), document, module,
						TableType.TEMP_TAB.getSuffix());
			}
			issueHeader.setDocumentDetails(docDetails);
		}

		issueHeader.setApprovedFinanceDisbursements(mainList);
		// Fee details
		List<FinFeeDetail> finFeeDetailList = finFeeDetailDAO.getFinFeeDetailByFinRef(finID, false, "",
				AccountingEvent.VAS_FEE);
		issueHeader.setFinFeeDetailList(finFeeDetailList);

		if (ImplementationConstants.VAS_INST_ON_DISB) {
			fm.setEntityCode(financeMainDAO.getLovDescEntityCode(finID, "_View"));
			fm.setLovDescEntityCode(fm.getEntityCode());
			issueHeader.setvASRecordings(vasRecordingDAO.getVASRecordingsByLinkRef(finReference, ""));
		}
		// Getting the JoinAccount Details
		issueHeader.setJointAccountDetails(
				jointAccountDetailDAO.getJointAccountDetailByFinRef(finID, TableType.MAIN_TAB.getSuffix()));

		logger.debug(Literal.LEAVING);
		return issueHeader;
	}

	@Override
	public PayOrderIssueHeader getApprovedPayOrderIssueHeaderById(long finID, String code) {
		PayOrderIssueHeader poih = payOrderIssueHeaderDAO.getPayOrderIssueByHeaderRef(finID, "_AView");
		poih.setFinAdvancePaymentsList(finAdvancePaymentsDAO.getFinAdvancePaymentsByFinRef(finID, "_AView"));
		return poih;
	}

	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		String tranType = "";
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		PayOrderIssueHeader poih = new PayOrderIssueHeader();
		BeanUtils.copyProperties((PayOrderIssueHeader) auditHeader.getAuditDetail().getModelData(), poih);
		calcluatePOHeaderDetails(poih);

		// Splitting IMPS Requests
		if (!PennantConstants.RECORD_TYPE_DEL.equals(poih.getRecordType())) {
			poih.setFinAdvancePaymentsList(finAdvancePaymentsService.splitRequest(poih.getFinAdvancePaymentsList()));
		}

		boolean posted = true;
		Map<Integer, Long> data = null;
		FinanceDetail fd = new FinanceDetail();
		fd.setAdvancePaymentsList(poih.getFinAdvancePaymentsList());
		fd.getFinScheduleData().setFinanceMain(poih.getFinanceMain());

		if (!ImplementationConstants.HOLD_DISB_INST_POST) {
			PostingDTO postingDTO = new PostingDTO();
			postingDTO.setFinanceDetail(fd);
			postingDTO.setUserBranch(auditHeader.getAuditBranchCode());
			AccountingEngine.post(AccountingEvent.DISBINS, postingDTO);
			for (FinAdvancePayments fap : poih.getFinAdvancePaymentsList()) {
				if (fap.getLinkedTranId() == Long.MIN_VALUE) {
					posted = false;
				}
			}
			if (!posted) {
				auditHeader.setErrorDetails(new ErrorDetail("0000", "Postigs Failed", null));
				return auditHeader;
			}
			if (!posted) {
				auditHeader.setErrorDetails(new ErrorDetail("0000", "Postigs Failed", null));
				return auditHeader;
			}
		}

		if (poih.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			payOrderIssueHeaderDAO.delete(poih, "");
		} else {
			poih.setRoleCode("");
			poih.setNextRoleCode("");
			poih.setTaskId("");
			poih.setNextTaskId("");
			poih.setWorkflowId(0);

			if (poih.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				poih.setRecordType("");
				payOrderIssueHeaderDAO.save(poih, "");
				financeMainDAO.updateMaintainceStatus(poih.getFinID(), "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				poih.setRecordType("");
				payOrderIssueHeaderDAO.update(poih, "");
				financeMainDAO.updateMaintainceStatus(poih.getFinID(), "");
			}

		}

		// Retrieving List of Audit Details For PayOrderIssueHeader Asset
		// related modules
		List<AuditDetail> details = processFinAdvancepayments(poih, "", data);
		auditDetails.addAll(details);

		finAdvancePaymentsDAO.deleteByFinRef(poih.getFinID(), "_Temp");

		payOrderIssueHeaderDAO.delete(poih, "_Temp");

		processPayment(poih);

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeaderDAO.addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(poih);
		auditHeader.setAuditDetails(auditDetails);
		auditHeaderDAO.addAudit(auditHeader);
		logger.debug(Literal.LEAVING);

		return auditHeader;
	}

	private void processPayment(PayOrderIssueHeader poih) {
		List<FinAdvancePayments> advancePayments = poih.getFinAdvancePaymentsList();
		List<FinAdvancePayments> resultPayments = new ArrayList<>();

		FinanceDetail fd = new FinanceDetail();
		fd.setFinScheduleData(new FinScheduleData());
		fd.getFinScheduleData().setFinanceMain(new FinanceMain());

		if (CollectionUtils.isNotEmpty(advancePayments)) {
			for (FinAdvancePayments finAdvancePayments : advancePayments) {
				if (finAdvancePayments.isPaymentProcReq()) {
					resultPayments.add(finAdvancePayments);
					fd.setFinReference(finAdvancePayments.getFinReference());
					fd.getFinScheduleData().getFinanceMain().setFinReference(finAdvancePayments.getFinReference());
					finAdvancePayments.setUserDetails(poih.getUserDetails());
				}
			}
		}
		if (CollectionUtils.isNotEmpty(resultPayments)) {
			fd.setAdvancePaymentsList(resultPayments);
			this.paymentsProcessService.process(fd, DisbursementConstants.CHANNEL_DISBURSEMENT);
		}
	}

	private void calcluatePOHeaderDetails(PayOrderIssueHeader poih) {
		logger.debug(Literal.ENTERING);

		if (poih.getFinAdvancePaymentsList() != null && !poih.getFinAdvancePaymentsList().isEmpty()) {
			BigDecimal totIssuedPOAmt = poih.getIssuedPOAmount();
			int totIssuedPOCount = poih.getIssuedPOCount();
			for (FinAdvancePayments finAdvancePayments : poih.getFinAdvancePaymentsList()) {
				if (StringUtils.equals(PennantConstants.PO_STATUS_ISSUE, finAdvancePayments.getStatus())) { // FIXME
																											// MURTHY
					totIssuedPOAmt = totIssuedPOAmt.add(finAdvancePayments.getAmtToBeReleased());
					totIssuedPOCount++;
				}
			}
			poih.setIssuedPOAmount(totIssuedPOAmt);
			poih.setpODueAmount(poih.getTotalPOAmount().subtract(totIssuedPOAmt));
			poih.setIssuedPOCount(totIssuedPOCount);
			poih.setpODueCount(poih.getTotalPOCount() - totIssuedPOCount);
		}

		logger.debug(Literal.LEAVING);
	}

	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		PayOrderIssueHeader poih = (PayOrderIssueHeader) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		payOrderIssueHeaderDAO.delete(poih, "_Temp");

		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, poih.getBefImage(), poih));

		financeMainDAO.updateMaintainceStatus(poih.getFinID(), "");

		List<FinAdvancePayments> list = poih.getFinAdvancePaymentsList();
		if (list != null && !list.isEmpty()) {
			finAdvancePaymentsDAO.deleteByFinRef(poih.getFinID(), "_Temp");
		}
		auditHeaderDAO.addAudit(auditHeader);
		logger.debug(Literal.LEAVING);

		return auditHeader;
	}

	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		doPostHookValidation(auditHeader);
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		for (int i = 0; i < auditDetails.size(); i++) {
			auditHeader.setErrorList(auditDetails.get(i).getErrorDetails());
		}

		auditHeader = nextProcess(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	private void doPostHookValidation(AuditHeader auditHeader) {
		if (postValidationHook == null) {
			return;
		}

		List<ErrorDetail> errorDetails = postValidationHook.validation(auditHeader);
		if (errorDetails == null) {
			return;
		}

		auditHeader.setErrorList(ErrorUtil.getErrorDetails(errorDetails, auditHeader.getUsrLanguage()));
	}

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug(Literal.ENTERING);

		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		PayOrderIssueHeader opih = (PayOrderIssueHeader) auditDetail.getModelData();

		PayOrderIssueHeader tempPoih = null;
		if (opih.isWorkflow()) {
			tempPoih = payOrderIssueHeaderDAO.getPayOrderIssueByHeaderRef(opih.getFinID(), "_Temp");
		}
		PayOrderIssueHeader befPayOrderIssueHeader = payOrderIssueHeaderDAO.getPayOrderIssueByHeaderRef(opih.getFinID(),
				"");
		PayOrderIssueHeader oldPayOrderIssueHeader = opih.getBefImage();

		String[] valueParm = new String[1];
		String[] errParm = new String[1];
		valueParm[0] = opih.getFinReference();

		errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];

		if (opih.isNewRecord()) { // for New record or new record into
			// work flow

			if (!opih.isWorkflow()) {// With out Work flow only
										// new records
				if (befPayOrderIssueHeader != null) { // Record Already Exists
														// in the table then
														// error
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));

				}
			} else { // with work flow
				if (opih.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if
																						// records
																						// type
																						// is
																						// new
					if (befPayOrderIssueHeader != null || tempPoih != null) { // if
																				// records
																				// already
																				// exists
																				// in
																				// the
																				// main
																				// table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
					}
				} else { // if records not exists in the Main flow table
					if (befPayOrderIssueHeader == null || tempPoih != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
					}
				}

			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!opih.isWorkflow()) { // With out Work flow for
										// update and delete

				if (befPayOrderIssueHeader == null) { // if records not exists
														// in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, valueParm), usrLanguage));
				} else {
					if (oldPayOrderIssueHeader != null
							&& !oldPayOrderIssueHeader.getLastMntOn().equals(befPayOrderIssueHeader.getLastMntOn())) {
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

				if (tempPoih == null) { // if records not exists
										// in the Work flow
										// table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}

				if (tempPoih != null && oldPayOrderIssueHeader != null
						&& !oldPayOrderIssueHeader.getLastMntOn().equals(tempPoih.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}
			}
		}

		if (opih.getRecordStatus().equals(PennantConstants.RCD_STATUS_SUBMITTED)) {

			validateOtcPayment(auditDetail, opih);
		}

		boolean noValidation = isnoValidationUserAction(opih.getRecordStatus());
		if (!noValidation) {
			List<FinAdvancePayments> advpayments = opih.getFinAdvancePaymentsList();
			String tableType = null;
			if (opih.isLoanApproved()) {
				tableType = TableType.MAIN_TAB.getSuffix();
			} else {
				tableType = TableType.MAIN_TAB.getSuffix();
			}
			List<FinanceDisbursement> disbursements = financeDisbursementDAO
					.getFinanceDisbursementDetails(opih.getFinID(), tableType, false);

			for (FinAdvancePayments finAdvancePay : advpayments) {
				if (!isDeleteRecord(finAdvancePay)) {
					for (FinanceDisbursement finDisbursmentDetail : disbursements) {
						if (finAdvancePay.getDisbSeq() == finDisbursmentDetail.getDisbSeq()
								&& finAdvancePay.getLlDate() != null && DateUtil
										.compare(finDisbursmentDetail.getDisbDate(), finAdvancePay.getLlDate()) != 0) {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "65032", errParm, valueParm),
									usrLanguage));
						}
					}

					String partnerBankBankcode = partnerBankService.getBankCodeById(finAdvancePay.getPartnerBankID());

					if (!StringUtils.equals(finAdvancePay.getBranchBankCode(), partnerBankBankcode)) {
						if (StringUtils.equals(finAdvancePay.getPaymentType(),
								DisbursementConstants.PAYMENT_TYPE_IFT)) {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "65033", errParm, valueParm),
									usrLanguage));
						}
					} else if (!StringUtils.equals(finAdvancePay.getPaymentType(),
							DisbursementConstants.PAYMENT_TYPE_IFT)) {
						String[] errParam = new String[1];
						errParam[0] = finAdvancePay.getPaymentType();
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "65038", errParam, valueParm),
								usrLanguage));
					}
				}

				if (bankAccountValidationService != null) {
					int count = pennyDropDAO.getPennyDropCount(finAdvancePay.getBeneficiaryAccNo(),
							finAdvancePay.getiFSC());
					if (count == 0) {
						/*
						 * auditDetail.setErrorDetail(ErrorUtil.getErrorDetail( new
						 * ErrorDetail(PennantConstants.KEY_FIELD, "41020", errParm, valueParm), usrLanguage));
						 */
					}
				}
			}

		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !opih.isWorkflow()) {
			auditDetail.setBefImage(befPayOrderIssueHeader);
		}
		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	private void validateOtcPayment(AuditDetail auditDetail, PayOrderIssueHeader header) {
		// ####_0.2
		// Not allowed to approve loan with Disbursement type if it is
		// not in the configured OTC Types

		String alwrepayMethods = (String) SysParamUtil.getValue("COVENANT_REPAY_OTC_TYPE");
		if (alwrepayMethods != null) {
			String[] valueParm = new String[2];
			boolean isFound = false;
			boolean isOTCPayment = false;
			boolean isDocExist = false;

			String[] repaymethod = alwrepayMethods.split(",");
			if (header.getFinAdvancePaymentsList() != null && header.getFinAdvancePaymentsList().size() > 0) {
				for (FinAdvancePayments finAdvancePayments : header.getFinAdvancePaymentsList()) {
					isFound = false;
					for (String rpymethod : repaymethod) {
						if (StringUtils.equals(finAdvancePayments.getPaymentType(), rpymethod)) {
							isFound = true;
							break;
						}
					}
					if (!isFound) {
						valueParm[0] = finAdvancePayments.getPaymentType();
						isOTCPayment = true;
						break;
					}
				}
				if (isOTCPayment) {
					if (header.getCovenantTypeList() != null && header.getCovenantTypeList().size() > 0) {
						for (FinCovenantType covenantType : header.getCovenantTypeList()) {
							isDocExist = false;
							if (!covenantType.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)) {
								// validate the document against the current
								// list.
								for (DocumentDetails documentDetails : header.getDocumentDetailsList()) {
									if (documentDetails.getDocCategory().equals(covenantType.getCovenantType())
											&& !documentDetails.getRecordType()
													.equals(PennantConstants.RECORD_TYPE_CAN)) {
										isDocExist = true;
										break;
									}
								}
								if (!isDocExist && covenantType.isAlwOtc()) {
									valueParm[1] = Labels.getLabel("label_FinCovenantTypeDialog_AlwOTC.value");
									auditDetail.setErrorDetail(
											ErrorUtil.getErrorDetail(new ErrorDetail("41101", valueParm)));
									break;
								}
							}
						}
					}
				}
			}
		}
	}

	private List<AuditDetail> processFinAdvancepayments(PayOrderIssueHeader payOrderIssueHeader, String type,
			Map<Integer, Long> data) {
		logger.debug(" Entering ");

		List<FinAdvancePayments> list = payOrderIssueHeader.getFinAdvancePaymentsList();

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		if (list == null || list.isEmpty()) {
			return auditDetails;
		}

		int count = 0;

		for (FinAdvancePayments finAdvpay : list) {

			String auditTransType = PennantConstants.TRAN_WF;

			String rcdType = finAdvpay.getRecordType();
			boolean save = true;
			FinAdvancePayments tempfinAdvPay = finAdvancePaymentsDAO.getFinAdvancePaymentsById(finAdvpay, type);
			if (tempfinAdvPay != null) {
				save = false;
			}
			if (StringUtils.isBlank(rcdType)) {
				continue;
			}

			if (rcdType.equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				finAdvpay.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			}

			finAdvpay.setRecordStatus(payOrderIssueHeader.getRecordStatus());
			finAdvpay.setLastMntOn(payOrderIssueHeader.getLastMntOn());
			finAdvpay.setLastMntBy(payOrderIssueHeader.getLastMntBy());

			if (StringUtils.isEmpty(type)) {
				finAdvpay.setRoleCode("");
				finAdvpay.setNextRoleCode("");
				finAdvpay.setTaskId("");
				finAdvpay.setNextTaskId("");
				finAdvpay.setRecordType("");
				// other
				if (rcdType.equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					if (ImplementationConstants.DISB_PAID_CANCELLATION_REQ
							&& DisbursementConstants.STATUS_PAID.equals(finAdvpay.getStatus())) {
						finAdvpay.setStatus(DisbursementConstants.STATUS_PAID_BUT_CANCELLED);
					} else {
						finAdvpay.setStatus(DisbursementConstants.STATUS_CANCEL);
					}
				} else if (!DisbursementConstants.STATUS_REVERSED.equals(finAdvpay.getStatus())) {
					if (ImplementationConstants.DISB_PAID_CANCELLATION_REQ) {
						finAdvpay.setStatus(DisbursementConstants.STATUS_AWAITCON);
					} else {
						finAdvpay.setStatus(DisbursementConstants.STATUS_APPROVED);
					}
				}
				finAdvpay.setpOIssued(true);
			}
			if (finAdvpay.isHoldDisbursement()) {
				finAdvpay.setStatus(DisbursementConstants.STATUS_HOLD);
			}

			// Reverse the disbursement instruction postings for PAID BUT CANCEL Case.
			if (ImplementationConstants.DISB_PAID_CANCELLATION_REQ
					&& DisbursementConstants.STATUS_PAID_BUT_CANCELLED.equals(finAdvpay.getStatus())) {
				postingsPreparationUtil.postReversalsByLinkedTranID(finAdvpay.getLinkedTranId());
			}

			if (save) {
				count = count + 1;

				if (StringUtils.isEmpty(type)) {
					auditTransType = PennantConstants.TRAN_ADD;
					if (data != null && data.containsKey(finAdvpay.getPaymentSeq())) {
						finAdvpay.setLinkedTranId(data.get(finAdvpay.getPaymentSeq()));
					}
				}
				finAdvancePaymentsDAO.save(finAdvpay, type);
				if (finAdvpay.getDocImage() != null) {
					saveDocumentDetails(finAdvpay);
				}
				if (StringUtils.isEmpty(type)) {
					finAdvpay.setPaymentProcReq(true);
				}
			} else {
				if (rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					finAdvancePaymentsDAO.delete(finAdvpay, type);
					count = count + 1;
					if (StringUtils.isEmpty(type)) {
						auditTransType = PennantConstants.TRAN_DEL;
					}
				} else {
					if (tempfinAdvPay != null) {
						if (tempfinAdvPay.isHoldDisbursement()) {
							finAdvpay.setPaymentProcReq(true);
						}
					}
					if (data != null && data.containsKey(finAdvpay.getPaymentSeq())) {
						finAdvpay.setLinkedTranId(data.get(finAdvpay.getPaymentSeq()));
					}
					finAdvancePaymentsDAO.update(finAdvpay, type);
					if (finAdvpay.getDocImage() != null) {
						saveDocumentDetails(finAdvpay);
					}

					count = count + 1;
					if (StringUtils.isEmpty(type)) {
						finAdvpay.setBefImage(tempfinAdvPay);
						auditTransType = PennantConstants.TRAN_UPD;
					}
				}
			}
			auditDetails.add(getAuditDetails(finAdvpay, count, auditTransType));
		}

		logger.debug(" Leaving ");
		return auditDetails;
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
		details.setFinEvent(FinServiceEvent.BASICMAINTAIN);
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

	public AuditDetail getAuditDetails(FinAdvancePayments finAdvancePayments, int auditSeq, String transType) {
		String[] fields = PennantJavaUtil.getFieldDetails(new FinAdvancePayments(),
				new FinAdvancePayments().getExcludeFields());
		return new AuditDetail(transType, auditSeq, fields[0], fields[1], finAdvancePayments.getBefImage(),
				finAdvancePayments);
	}

	public void addToCustomerBeneficiary(FinAdvancePayments finAdvPay, long cusID) {
		int count = beneficiaryDAO.getBeneficiaryByBankBranchId(finAdvPay.getBeneficiaryAccNo(),
				finAdvPay.getBankBranchID(), "_View");
		if (count == 0) {
			Beneficiary beneficiary = new Beneficiary();
			beneficiary.setCustID(cusID);
			beneficiary.setBankBranchID(finAdvPay.getBankBranchID());
			beneficiary.setAccNumber(finAdvPay.getBeneficiaryAccNo());
			beneficiary.setAccHolderName(finAdvPay.getBeneficiaryName());
			beneficiary.setPhoneCountryCode(finAdvPay.getPhoneCountryCode());
			beneficiary.setPhoneAreaCode(finAdvPay.getPhoneAreaCode());
			beneficiary.setPhoneNumber(finAdvPay.getPhoneNumber());
			beneficiaryDAO.save(beneficiary, "");
		}
	}

	private boolean isDeleteRecord(FinAdvancePayments aFinAdvancePayments) {
		if (StringUtils.equals(PennantConstants.RECORD_TYPE_CAN, aFinAdvancePayments.getRecordType())
				|| StringUtils.equals(PennantConstants.RECORD_TYPE_DEL, aFinAdvancePayments.getRecordType())
				|| StringUtils.equals(DisbursementConstants.STATUS_CANCEL, aFinAdvancePayments.getStatus())
				|| StringUtils.equals(DisbursementConstants.STATUS_REJECTED, aFinAdvancePayments.getStatus())
				|| StringUtils.equals(DisbursementConstants.STATUS_REVERSED, aFinAdvancePayments.getStatus())) {
			return true;
		}
		return false;
	}

	private boolean isnoValidationUserAction(String userAction) {
		boolean noValidation = false;
		if (userAction != null) {
			if (userAction.equalsIgnoreCase("Cancel") || userAction.contains("Reject")
					|| userAction.contains("Resubmit") || userAction.contains("Decline")) {
				noValidation = true;
			}
		}
		return noValidation;
	}

	@Override
	public List<ReturnDataSet> getDisbursementPostings(long finID) {
		return postingsDAO.getDisbursementPostings(finID);
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public void setPayOrderIssueHeaderDAO(PayOrderIssueHeaderDAO payOrderIssueHeaderDAO) {
		this.payOrderIssueHeaderDAO = payOrderIssueHeaderDAO;
	}

	public void setFinAdvancePaymentsDAO(FinAdvancePaymentsDAO finAdvancePaymentsDAO) {
		this.finAdvancePaymentsDAO = finAdvancePaymentsDAO;
	}

	public void setBeneficiaryDAO(BeneficiaryDAO beneficiaryDAO) {
		this.beneficiaryDAO = beneficiaryDAO;
	}

	public void setFinanceDisbursementDAO(FinanceDisbursementDAO financeDisbursementDAO) {
		this.financeDisbursementDAO = financeDisbursementDAO;
	}

	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public void setFinAdvancePaymentsService(FinAdvancePaymentsService finAdvancePaymentsService) {
		this.finAdvancePaymentsService = finAdvancePaymentsService;
	}

	public void setDocumentDetailsDAO(DocumentDetailsDAO documentDetailsDAO) {
		this.documentDetailsDAO = documentDetailsDAO;
	}

	public void setFinCovenantTypeDAO(FinCovenantTypeDAO finCovenantTypeDAO) {
		this.finCovenantTypeDAO = finCovenantTypeDAO;
	}

	public void setVasRecordingDAO(VASRecordingDAO vasRecordingDAO) {
		this.vasRecordingDAO = vasRecordingDAO;
	}

	public void setPartnerBankService(PartnerBankService partnerBankService) {
		this.partnerBankService = partnerBankService;
	}

	public void setPostingsDAO(PostingsDAO postingsDAO) {
		this.postingsDAO = postingsDAO;
	}

	public void setPaymentsProcessService(PaymentsProcessService paymentsProcessService) {
		this.paymentsProcessService = paymentsProcessService;
	}

	public void setPennyDropDAO(PennyDropDAO pennyDropDAO) {
		this.pennyDropDAO = pennyDropDAO;
	}

	@Autowired(required = false)
	public void setBankAccountValidationService(BankAccountValidationService bankAccountValidationService) {
		this.bankAccountValidationService = bankAccountValidationService;
	}

	public void setJointAccountDetailDAO(JointAccountDetailDAO jointAccountDetailDAO) {
		this.jointAccountDetailDAO = jointAccountDetailDAO;
	}

	public void setFinFeeDetailDAO(FinFeeDetailDAO finFeeDetailDAO) {
		this.finFeeDetailDAO = finFeeDetailDAO;
	}

}