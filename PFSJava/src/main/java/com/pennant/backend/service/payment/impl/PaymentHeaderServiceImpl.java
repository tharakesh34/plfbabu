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
 * * FileName : PaymentHeaderServiceImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 27-05-2017 * *
 * Modified Date : 27-05-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 27-05-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.service.payment.impl;

import java.math.BigDecimal;
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
import org.springframework.beans.factory.annotation.Qualifier;

import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.GSTCalculator;
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.customermasters.CustomerAddresDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceTaxDetailDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.dao.finance.TaxHeaderDetailsDAO;
import com.pennant.backend.dao.payment.PaymentHeaderDAO;
import com.pennant.backend.endofday.main.PFSBatchAdmin;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.AutoRefundLoan;
import com.pennant.backend.model.finance.FeeType;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.InvoiceDetail;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.ManualAdviseMovements;
import com.pennant.backend.model.finance.PaymentInstruction;
import com.pennant.backend.model.finance.TaxHeader;
import com.pennant.backend.model.finance.Taxes;
import com.pennant.backend.model.payment.PaymentDetail;
import com.pennant.backend.model.payment.PaymentHeader;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.InstrumentwiseLimitService;
import com.pennant.backend.service.feetype.FeeTypeService;
import com.pennant.backend.service.finance.FinFeeDetailService;
import com.pennant.backend.service.finance.GSTInvoiceTxnService;
import com.pennant.backend.service.payment.PaymentDetailService;
import com.pennant.backend.service.payment.PaymentHeaderService;
import com.pennant.backend.service.payment.PaymentInstructionService;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.backend.util.UploadConstants;
import com.pennant.cache.util.AccountingConfigCache;
import com.pennant.pff.fee.AdviseType;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.pff.service.hook.PostValidationHook;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>PaymentHeader</b>.<br>
 */
public class PaymentHeaderServiceImpl extends GenericService<PaymentHeader> implements PaymentHeaderService {
	private static final Logger logger = LogManager.getLogger(PaymentHeaderServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private PaymentHeaderDAO paymentHeaderDAO;
	private PaymentDetailService paymentDetailService;
	private PaymentInstructionService paymentInstructionService;
	private transient PostingsPreparationUtil postingsPreparationUtil;
	private TaxHeaderDetailsDAO taxHeaderDetailsDAO;
	private FinFeeDetailService finFeeDetailService;
	private FinanceTaxDetailDAO financeTaxDetailDAO;
	private CustomerAddresDAO customerAddresDAO;
	private ManualAdviseDAO manualAdviseDAO;
	private GSTInvoiceTxnService gstInvoiceTxnService;
	private transient InstrumentwiseLimitService instrumentwiseLimitService;
	private FinanceMainDAO financeMainDAO;
	private FeeTypeService feeTypeService;
	@Autowired(required = false)
	@Qualifier("paymentInstructionPostValidationHook")
	private PostValidationHook postValidationHook;

	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader, "saveOrUpdate");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		PaymentHeader ph = (PaymentHeader) auditHeader.getAuditDetail().getModelData();

		long finID = ph.getFinID();

		TableType tableType = TableType.MAIN_TAB;
		if (ph.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (ph.isNewRecord()) {
			ph.setId(Long.parseLong(paymentHeaderDAO.save(ph, tableType)));
			setPaymentHeaderId(ph);
			auditHeader.getAuditDetail().setModelData(ph);
			auditHeader.setAuditReference(String.valueOf(ph.getPaymentId()));
		} else {
			paymentHeaderDAO.update(ph, tableType);
		}

		// PaymentHeader
		List<PaymentDetail> pdList = ph.getPaymentDetailList();

		if (pdList != null && pdList.size() > 0) {
			List<AuditDetail> auditDetail = ph.getAuditDetailMap().get("PaymentDetails");
			auditDetail = this.paymentDetailService.processPaymentDetails(auditDetail, tableType, "", 0, finID);
			auditDetails.addAll(auditDetail);
		}

		// PaymentInstructions
		if (ph.getPaymentInstruction() != null) {
			List<AuditDetail> auditDetail = ph.getAuditDetailMap().get("PaymentInstructions");
			auditDetail = this.paymentInstructionService.processPaymentInstrDetails(auditDetail, tableType, "");
			auditDetails.addAll(auditDetail);
		}
		String rcdMaintainSts = FinServiceEvent.PAYMENTINST;
		financeMainDAO.updateMaintainceStatus(finID, rcdMaintainSts);

		auditHeader.setAuditDetails(auditDetails);
		auditHeaderDAO.addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}

	private void setPaymentHeaderId(PaymentHeader paymentHeader) {
		if (paymentHeader.getPaymentDetailList() != null && paymentHeader.getPaymentDetailList().size() > 0) {
			for (PaymentDetail detail : paymentHeader.getPaymentDetailList()) {
				detail.setPaymentId(paymentHeader.getPaymentId());
			}
		}
		if (paymentHeader.getPaymentInstruction() != null) {
			paymentHeader.getPaymentInstruction().setPaymentId(paymentHeader.getPaymentId());
		}
	}

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		PaymentHeader ph = (PaymentHeader) auditHeader.getAuditDetail().getModelData();
		paymentHeaderDAO.delete(ph, TableType.MAIN_TAB);
		auditHeader.setAuditDetails(
				processChildsAudit(deleteChilds(ph, TableType.MAIN_TAB, auditHeader.getAuditTranType())));

		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public PaymentHeader getPaymentHeader(long paymentId) {
		PaymentHeader paymentHeader = paymentHeaderDAO.getPaymentHeader(paymentId, "_View");
		List<PaymentDetail> list = this.paymentDetailService.getPaymentDetailList(paymentHeader.getPaymentId(),
				"_View");

		paymentHeader.setOdAgainstLoan(getDueAgainstLoan(paymentHeader.getFinID()));
		paymentHeader.setOdAgainstCustomer(
				getDueAgainstCustomer(paymentHeader.getCustID(), paymentHeader.getCustCoreBank()));

		if (list != null) {
			paymentHeader.setPaymentDetailList(list);
			for (PaymentDetail pd : list) {
				if (pd.getTaxHeaderId() != null) {
					pd.setTaxHeader(taxHeaderDetailsDAO.getTaxHeaderDetailsById(pd.getTaxHeaderId(), "_View"));
				}
				if (pd.getTaxHeader() != null) {
					pd.getTaxHeader().setTaxDetails(taxHeaderDetailsDAO.getTaxDetailById(pd.getTaxHeaderId(), "_View"));
				}
			}
		}

		PaymentInstruction paymentInstruction = this.paymentInstructionService
				.getPaymentInstructionDetails(paymentHeader.getPaymentId(), "_View");
		if (paymentInstruction != null) {
			paymentHeader.setPaymentInstruction(paymentInstruction);
		}
		return paymentHeader;
	}

	public PaymentHeader getApprovedPaymentHeader(long paymentId) {
		return paymentHeaderDAO.getPaymentHeader(paymentId, "_AView");
	}

	@Override
	public BigDecimal getDueAgainstLoan(long finId) {
		return paymentHeaderDAO.getDueAgainstLoan(finId);
	}

	@Override
	public BigDecimal getDueAgainstCustomer(long custId, String custCoreBank) {
		return paymentHeaderDAO.getDueAgainstCustomer(custId, custCoreBank);
	}

	@Override
	public FinanceMain getFinanceDetails(long finID) {
		return paymentHeaderDAO.getFinanceDetails(finID);
	}

	@Override
	public List<FinExcessAmount> getfinExcessAmount(long finID) {
		return paymentHeaderDAO.getfinExcessAmount(finID);
	}

	@Override
	public List<ManualAdvise> getManualAdvise(long finID) {
		return paymentHeaderDAO.getManualAdvise(finID);
	}

	@Override
	public List<ManualAdvise> getManualAdviseForEnquiry(long finID) {
		return paymentHeaderDAO.getManualAdviseForEnquiry(finID);
	}

	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) throws InterfaceException {
		logger.info(Literal.ENTERING);

		String tranType = "";
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader, "doApprove");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		PaymentHeader paymentHeader = new PaymentHeader();
		BeanUtils.copyProperties((PaymentHeader) auditHeader.getAuditDetail().getModelData(), paymentHeader);

		// Processing Accounting Details
		if (StringUtils.equals(paymentHeader.getRecordType(), PennantConstants.RECORD_TYPE_NEW)) {
			auditHeader = executeAccountingProcess(auditHeader, SysParamUtil.getAppDate());
		}

		BeanUtils.copyProperties((PaymentHeader) auditHeader.getAuditDetail().getModelData(), paymentHeader);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(paymentHeader.getRecordType())) {
			auditHeader.getAuditDetail()
					.setBefImage(paymentHeaderDAO.getPaymentHeader(paymentHeader.getPaymentId(), ""));
		}

		if (paymentHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			auditDetails.addAll(deleteChilds(paymentHeader, TableType.MAIN_TAB, tranType));
			paymentHeaderDAO.delete(paymentHeader, TableType.MAIN_TAB);
		} else {
			paymentHeader.setRoleCode("");
			paymentHeader.setNextRoleCode("");
			paymentHeader.setTaskId("");
			paymentHeader.setNextTaskId("");
			paymentHeader.setWorkflowId(0);

			if (paymentHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				paymentHeader.setRecordType("");
				paymentHeaderDAO.save(paymentHeader, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				paymentHeader.setRecordType("");
				paymentHeaderDAO.update(paymentHeader, TableType.MAIN_TAB);
			}

			// PaymentDetails
			if (auditHeader.getAuditDetails() != null && !auditHeader.getAuditDetails().isEmpty()) {
				List<AuditDetail> paymentDetails = paymentHeader.getAuditDetailMap().get("PaymentDetails");
				if (paymentDetails != null && !paymentDetails.isEmpty()) {
					paymentDetails = this.paymentDetailService.processPaymentDetails(paymentDetails, TableType.MAIN_TAB,
							"doApprove", paymentHeader.getLinkedTranId(), paymentHeader.getFinID());
					auditDetails.addAll(paymentDetails);
				}
			}
			// PaymentInstruction
			if (auditHeader.getAuditDetails() != null && !auditHeader.getAuditDetails().isEmpty()) {
				List<AuditDetail> paymentinstructions = paymentHeader.getAuditDetailMap().get("PaymentInstructions");

				if (paymentinstructions != null && !paymentinstructions.isEmpty()) {
					paymentinstructions = this.paymentInstructionService.processPaymentInstrDetails(paymentinstructions,
							TableType.MAIN_TAB, "doApprove");
					auditDetails.addAll(paymentinstructions);
				}
			}
		}

		if (!StringUtils.equals(UploadConstants.FINSOURCE_ID_CD_PAY_UPLOAD, paymentHeader.getFinSource())
				&& !StringUtils.equals(UploadConstants.FINSOURCE_ID_AUTOPROCESS, paymentHeader.getFinSource())) {
			auditHeader
					.setAuditDetails(deleteChilds(paymentHeader, TableType.TEMP_TAB, auditHeader.getAuditTranType()));
			String[] fields = PennantJavaUtil.getFieldDetails(new PaymentHeader(), paymentHeader.getExcludeFields());
			auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
					paymentHeader.getBefImage(), paymentHeader));
			auditHeaderDAO.addAudit(auditHeader);
			paymentHeaderDAO.delete(paymentHeader, TableType.TEMP_TAB);
		}
		financeMainDAO.updateMaintainceStatus(paymentHeader.getFinID(), "");

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeaderDAO.addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(paymentHeader);
		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		PaymentHeader ph = (PaymentHeader) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeader.setAuditDetails(
				processChildsAudit(deleteChilds(ph, TableType.TEMP_TAB, auditHeader.getAuditTranType())));
		financeMainDAO.updateMaintainceStatus(ph.getFinID(), "");
		paymentHeaderDAO.delete(ph, TableType.TEMP_TAB);
		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		auditHeader = prepareChildsAudit(auditHeader, method);
		auditHeader.setErrorList(validateChilds(auditHeader, auditHeader.getUsrLanguage(), method));

		for (int i = 0; i < auditDetails.size(); i++) {
			auditHeader.setErrorList(auditDetails.get(i).getErrorDetails());
		}
		auditHeader = nextProcess(auditHeader);

		logger.debug("Leaving");

		return auditHeader;
	}

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug(Literal.ENTERING);

		// Write the required validation over hear.

		PaymentHeader ph = (PaymentHeader) auditDetail.getModelData();

		// validation for writeoff

		// validation for hold

		// validation for receipt cancel

		// validation for OD amount

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	// =================================== List maintaince
	private AuditHeader prepareChildsAudit(AuditHeader auditHeader, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		Map<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

		PaymentHeader paymentHeader = (PaymentHeader) auditHeader.getAuditDetail().getModelData();
		String auditTranType = "";

		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (paymentHeader.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}
		// PaymentDetails
		if (paymentHeader.getPaymentDetailList() != null && paymentHeader.getPaymentDetailList().size() > 0) {
			for (PaymentDetail detail : paymentHeader.getPaymentDetailList()) {
				detail.setPaymentId(paymentHeader.getPaymentId());
				detail.setWorkflowId(paymentHeader.getWorkflowId());
			}
			auditDetailMap.put("PaymentDetails", this.paymentDetailService
					.setPaymentDetailAuditData(paymentHeader.getPaymentDetailList(), auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("PaymentDetails"));
		}

		// Insurance Details
		if (paymentHeader.getPaymentInstruction() != null) {
			PaymentInstruction detail = paymentHeader.getPaymentInstruction();
			detail.setPaymentId(paymentHeader.getPaymentId());
			detail.setWorkflowId(paymentHeader.getWorkflowId());
			detail.setRecordStatus(paymentHeader.getRecordStatus());
			detail.setRecordType(paymentHeader.getRecordType());
			detail.setNewRecord(paymentHeader.isNewRecord());
			detail.setUserDetails(paymentHeader.getUserDetails());
			detail.setLastMntBy(paymentHeader.getLastMntBy());
			detail.setLastMntOn(paymentHeader.getLastMntOn());
			detail.setRoleCode(paymentHeader.getRoleCode());
			detail.setNextRoleCode(paymentHeader.getNextRoleCode());
			detail.setTaskId(paymentHeader.getTaskId());
			detail.setNextTaskId(paymentHeader.getNextTaskId());

			auditDetailMap.put("PaymentInstructions",
					this.paymentInstructionService.setPaymentInstructionDetailsAuditData(
							paymentHeader.getPaymentInstruction(), auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("PaymentInstructions"));
		}

		paymentHeader.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(paymentHeader);
		auditHeader.setAuditDetails(auditDetails);
		logger.debug("Leaving");

		return auditHeader;
	}

	private List<ErrorDetail> validateChilds(AuditHeader auditHeader, String usrLanguage, String method) {
		logger.debug("Entering");

		List<ErrorDetail> errorDetails = new ArrayList<ErrorDetail>();
		PaymentHeader ph = (PaymentHeader) auditHeader.getAuditDetail().getModelData();

		doPostHookValidation(auditHeader);

		List<AuditDetail> auditDetails = null;

		// PaymentDetails
		if (ph.getAuditDetailMap().get("PaymentDetails") != null) {
			auditDetails = ph.getAuditDetailMap().get("PaymentDetails");
			for (AuditDetail auditDetail : auditDetails) {
				List<ErrorDetail> details = this.paymentDetailService.validation(auditDetail, usrLanguage, method)
						.getErrorDetails();
				if (details != null) {
					errorDetails.addAll(details);
				}
			}
		}

		// PaymentInstruction
		if (ph.getAuditDetailMap().get("PaymentInstructions") != null) {
			auditDetails = ph.getAuditDetailMap().get("PaymentInstructions");
			for (AuditDetail auditDetail : auditDetails) {
				List<ErrorDetail> details = this.paymentInstructionService.validation(auditDetail, usrLanguage, method)
						.getErrorDetails();
				if (details != null) {
					errorDetails.addAll(details);
				}
			}
		}
		logger.debug("Leaving");
		return errorDetails;
	}

	public void doPostHookValidation(AuditHeader auditHeader) {
		if (postValidationHook != null) {
			List<ErrorDetail> errorDetails = postValidationHook.validation(auditHeader);
			if (errorDetails != null) {
				errorDetails = ErrorUtil.getErrorDetails(errorDetails, auditHeader.getUsrLanguage());
				auditHeader.setErrorList(errorDetails);
			}
		}
	}

	public List<AuditDetail> deleteChilds(PaymentHeader ph, TableType tableType, String auditTranType) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		// PaymentDetails
		if (ph.getPaymentDetailList() != null && !ph.getPaymentDetailList().isEmpty()) {
			auditDetails.addAll(this.paymentDetailService.delete(ph.getPaymentDetailList(), tableType, auditTranType,
					ph.getPaymentId()));
		}
		// PaymentInstructions
		if (ph.getPaymentInstruction() != null) {
			auditDetails.addAll(this.paymentInstructionService.delete(ph.getPaymentInstruction(), tableType,
					auditTranType, ph.getPaymentId()));
		}
		logger.debug("Leaving");
		return auditDetails;
	}

	private List<AuditDetail> processChildsAudit(List<AuditDetail> list) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		if (list == null || list.isEmpty()) {
			return auditDetails;
		}
		for (AuditDetail detail : list) {
			String transType = "";
			String rcdType = "";
			Object object = detail.getModelData();

			if (object instanceof PaymentDetail) {
				rcdType = ((PaymentDetail) object).getRecordType();
			} else if (object instanceof PaymentInstruction) {
				rcdType = ((PaymentInstruction) object).getRecordType();
			}

			if (PennantConstants.RECORD_TYPE_NEW.equalsIgnoreCase(rcdType)) {
				transType = PennantConstants.TRAN_ADD;
			} else if (PennantConstants.RECORD_TYPE_DEL.equalsIgnoreCase(rcdType)
					|| PennantConstants.RECORD_TYPE_CAN.equalsIgnoreCase(rcdType)) {
				transType = PennantConstants.TRAN_DEL;
			} else {
				transType = PennantConstants.TRAN_UPD;
			}
			auditDetails.add(new AuditDetail(transType, detail.getAuditSeq(), detail.getBefImage(), object));
		}
		logger.debug("Leaving");

		return auditDetails;
	}

	@Override
	public void executeAccountingProcess(AEEvent aeEvent, PaymentHeader ph) {
		logger.debug(Literal.ENTERING);

		aeEvent.setAccountingEvent(AccountingEvent.PAYMTINS);
		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();

		if (amountCodes == null) {
			amountCodes = new AEAmountCodes();
		}

		FinanceMain fm = paymentHeaderDAO.getFinanceDetails(ph.getFinID());
		amountCodes.setFinType(fm.getFinType());
		aeEvent.setBranch(fm.getFinBranch());
		aeEvent.setCustID(fm.getCustID());

		PaymentInstruction paymentInstruction = ph.getPaymentInstruction();

		if (paymentInstruction != null) {
			amountCodes.setPartnerBankAc(paymentInstruction.getPartnerBankAc());
			amountCodes.setPartnerBankAcType(paymentInstruction.getPartnerBankAcType());
			aeEvent.setValueDate(paymentInstruction.getPostDate());
		}

		// GST parameters
		Map<String, Object> gstExecutionMap = GSTCalculator.getGSTDataMap(fm.getFinID());

		aeEvent.setCcy(fm.getFinCcy());
		aeEvent.setFinID(fm.getFinID());
		aeEvent.setFinReference(fm.getFinReference());

		List<PaymentDetail> paymentDetailsList = ph.getPaymentDetailList();
		List<String> feeTypeCodes = new ArrayList<>();
		List<FeeType> feeTypesList = new ArrayList<>();

		for (PaymentDetail paymentDetail : paymentDetailsList) {
			feeTypeCodes.add(paymentDetail.getFeeTypeCode());
		}

		if (feeTypeCodes != null && !feeTypeCodes.isEmpty()) {
			feeTypesList = feeTypeService.getFeeTypeListByCodes(feeTypeCodes, "");
			aeEvent.setFeesList(feeTypesList);
		}

		aeEvent.setDataMap(amountCodes.getDeclaredFieldValues());

		BigDecimal excessAmount = BigDecimal.ZERO;
		BigDecimal emiInAdavance = BigDecimal.ZERO;
		BigDecimal advInst = BigDecimal.ZERO;
		BigDecimal cashCtrl = BigDecimal.ZERO;
		BigDecimal dsf = BigDecimal.ZERO;

		Map<String, Object> eventMapping = aeEvent.getDataMap();

		for (PaymentDetail paymentDetail : ph.getPaymentDetailList()) {

			String amountType = paymentDetail.getAmountType();

			switch (amountType) {
			case "2":
				BigDecimal payableAmount = BigDecimal.ZERO;
				String feeTypeCode = paymentDetail.getFeeTypeCode();
				if (eventMapping.containsKey(feeTypeCode + "_P")) {
					payableAmount = (BigDecimal) eventMapping.get(feeTypeCode + "_P");
				}
				eventMapping.put(feeTypeCode + "_P", payableAmount.add(paymentDetail.getAmount()));

				// GST Calculations
				TaxHeader taxHeader = paymentDetail.getTaxHeader();
				Taxes cgstTax = null;
				Taxes sgstTax = null;
				Taxes igstTax = null;
				Taxes ugstTax = null;
				Taxes cessTax = null;
				if (taxHeader == null) {
					taxHeader = new TaxHeader();
					taxHeader.setNewRecord(true);
					taxHeader.setRecordType(PennantConstants.RCD_ADD);
					taxHeader.setVersion(taxHeader.getVersion() + 1);
					paymentDetail.setTaxHeader(taxHeader);
				}
				List<Taxes> taxDetails = taxHeader.getTaxDetails();
				if (CollectionUtils.isNotEmpty(taxDetails)) {
					for (Taxes taxes : taxDetails) {
						if (StringUtils.equals(RuleConstants.CODE_CGST, taxes.getTaxType())) {
							cgstTax = taxes;
						} else if (StringUtils.equals(RuleConstants.CODE_SGST, taxes.getTaxType())) {
							sgstTax = taxes;
						} else if (StringUtils.equals(RuleConstants.CODE_IGST, taxes.getTaxType())) {
							igstTax = taxes;
						} else if (StringUtils.equals(RuleConstants.CODE_UGST, taxes.getTaxType())) {
							ugstTax = taxes;
						} else if (StringUtils.equals(RuleConstants.CODE_CESS, taxes.getTaxType())) {
							cessTax = taxes;
						}
					}

					// CGST
					BigDecimal gstAmount = BigDecimal.ZERO;
					if (eventMapping.containsKey(feeTypeCode + "_CGST_P")) {
						gstAmount = (BigDecimal) eventMapping.get(feeTypeCode + "_CGST_P");
					}
					eventMapping.put(feeTypeCode + "_CGST_P", gstAmount.add(cgstTax.getPaidTax()));

					// SGST
					gstAmount = BigDecimal.ZERO;
					if (eventMapping.containsKey(feeTypeCode + "_SGST_P")) {
						gstAmount = (BigDecimal) eventMapping.get(feeTypeCode + "_SGST_P");
					}
					eventMapping.put(feeTypeCode + "_SGST_P", gstAmount.add(sgstTax.getPaidTax()));

					// UGST
					gstAmount = BigDecimal.ZERO;
					if (eventMapping.containsKey(feeTypeCode + "_UGST_P")) {
						gstAmount = (BigDecimal) eventMapping.get(feeTypeCode + "_UGST_P");
					}
					eventMapping.put(feeTypeCode + "_UGST_P", gstAmount.add(ugstTax.getPaidTax()));

					// IGST
					gstAmount = BigDecimal.ZERO;
					if (eventMapping.containsKey(feeTypeCode + "_IGST_P")) {
						gstAmount = (BigDecimal) eventMapping.get(feeTypeCode + "_IGST_P");
					}
					eventMapping.put(feeTypeCode + "_IGST_P", gstAmount.add(igstTax.getPaidTax()));

					// CESS
					gstAmount = BigDecimal.ZERO;
					if (eventMapping.containsKey(feeTypeCode + "_CESS_P")) {
						gstAmount = (BigDecimal) eventMapping.get(feeTypeCode + "_CESS_P");
					}
					eventMapping.put(feeTypeCode + "_CESS_P", gstAmount.add(cessTax.getPaidTax()));
				}
				break;
			case RepayConstants.EXAMOUNTTYPE_EXCESS:
				excessAmount = excessAmount.add(paymentDetail.getAmount());
				break;
			case RepayConstants.EXAMOUNTTYPE_EMIINADV:
				emiInAdavance = emiInAdavance.add(paymentDetail.getAmount());
				break;
			case RepayConstants.EXAMOUNTTYPE_ADVINT:
				advInst = emiInAdavance.add(paymentDetail.getAmount());
				break;
			case RepayConstants.EXAMOUNTTYPE_CASHCLT:
				cashCtrl = cashCtrl.add(paymentDetail.getAmount());
				break;
			case RepayConstants.EXAMOUNTTYPE_DSF:
				dsf = dsf.add(paymentDetail.getAmount());
				break;

			default:
				break;
			}
			eventMapping.put("pi_excessAmount", excessAmount);
			eventMapping.put("pi_emiInAdvance", emiInAdavance);
			eventMapping.put("pi_paymentAmount", ph.getPaymentInstruction().getPaymentAmount());
			eventMapping.put("pi_advInst", advInst);
			eventMapping.put("CASHCLT_P", cashCtrl);
			eventMapping.put("DSF_P", dsf);

			aeEvent.setDataMap(eventMapping);

			if (gstExecutionMap != null) {
				for (String mapkey : gstExecutionMap.keySet()) {
					if (StringUtils.isNotBlank(mapkey)) {
						aeEvent.getDataMap().put(mapkey, gstExecutionMap.get(mapkey));
					}
				}
			}
		}

		long accountsetId = AccountingConfigCache.getAccountSetID(fm.getFinType(), AccountingEvent.PAYMTINS,
				FinanceConstants.MODULEID_FINTYPE);

		aeEvent.getAcSetIDList().add(accountsetId);

		logger.debug(Literal.LEAVING);
	}

	public AuditHeader executeAccountingProcess(AuditHeader auditHeader, Date appDate) throws InterfaceException {
		logger.debug(Literal.ENTERING);

		AEEvent aeEvent = new AEEvent();
		PaymentHeader paymentHeader = new PaymentHeader();
		BeanUtils.copyProperties((PaymentHeader) auditHeader.getAuditDetail().getModelData(), paymentHeader);

		if (paymentHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
			executeAccountingProcess(aeEvent, paymentHeader);

			List<ManualAdviseMovements> movements = new ArrayList<ManualAdviseMovements>();

			for (PaymentDetail paymentDetail : paymentHeader.getPaymentDetailList()) {
				if (AdviseType.isPayable(paymentDetail.getAmountType())) {
					ManualAdviseMovements advMov = prepareAdviseRefund(paymentDetail);
					if (advMov != null) {
						movements.add(advMov);
					}
				}
			}

			postingsPreparationUtil.postAccounting(aeEvent);

			if (aeEvent.getLinkedTranId() > 0 && aeEvent.isPostingSucess() && !movements.isEmpty()) {
				List<ManualAdviseMovements> waiverMovements = new ArrayList<>();

				FinanceMain fm = paymentHeaderDAO.getFinanceDetails(paymentHeader.getFinID());

				FinanceDetail fd = new FinanceDetail();
				fd.getFinScheduleData().setFinanceMain(fm);

				InvoiceDetail invoiceDetail = new InvoiceDetail();
				invoiceDetail.setLinkedTranId(aeEvent.getLinkedTranId());
				invoiceDetail.setFinanceDetail(fd);
				invoiceDetail.setInvoiceType(PennantConstants.GST_INVOICE_TRANSACTION_TYPE_CREDIT);
				invoiceDetail.setWaiver(false);

				for (ManualAdviseMovements advMov : movements) {
					waiverMovements.add(advMov);
					invoiceDetail.setMovements(waiverMovements);
					Long invoiceID = this.gstInvoiceTxnService.advTaxInvoicePreparation(invoiceDetail);

					if (advMov.getTaxHeader() != null) {
						advMov.getTaxHeader().setInvoiceID(invoiceID);
					}
					waiverMovements.clear();
				}
			}

		}

		paymentHeader.setLinkedTranId(aeEvent.getLinkedTranId());
		auditHeader.getAuditDetail().setModelData(paymentHeader);
		logger.debug(Literal.LEAVING);

		logger.debug("Leaving");
		return auditHeader;
	}

	private ManualAdviseMovements prepareAdviseRefund(PaymentDetail paymentDetail) {
		logger.debug(Literal.ENTERING);

		// GST Calculations
		TaxHeader taxHeader = paymentDetail.getTaxHeader();
		if (taxHeader == null || taxHeader.getTaxDetails().isEmpty()) {
			logger.debug(Literal.LEAVING);
			return null;
		}
		List<Taxes> taxDetails = taxHeader.getTaxDetails();
		BigDecimal totGSTAmount = BigDecimal.ZERO;
		if (CollectionUtils.isNotEmpty(taxDetails)) {
			for (Taxes taxes : taxDetails) {
				totGSTAmount = totGSTAmount.add(taxes.getPaidTax());
			}
		}

		if (totGSTAmount.compareTo(BigDecimal.ZERO) <= 0) {
			logger.debug(Literal.LEAVING);
			return null;
		}

		// Fetch Advise Details
		ManualAdvise advise = manualAdviseDAO.getManualAdviseById(paymentDetail.getReferenceId(), "_AView");
		ManualAdviseMovements movement = new ManualAdviseMovements();
		if (totGSTAmount.compareTo(BigDecimal.ZERO) > 0) {
			movement.setAdviseID(advise.getAdviseID());
			movement.setMovementDate(SysParamUtil.getAppDate());
			movement.setMovementAmount(paymentDetail.getAmount());
			movement.setPaidAmount(BigDecimal.ZERO);
			movement.setWaivedAmount(paymentDetail.getAmount());
			movement.setReceiptID(0);
			movement.setReceiptSeqID(0);
			movement.setFeeTypeCode(advise.getFeeTypeCode());
			movement.setFeeTypeDesc(advise.getFeeTypeDesc());
			movement.setTaxApplicable(advise.isTaxApplicable());
			movement.setTaxComponent(advise.getTaxComponent());

			if (taxHeader != null) {

				// Setting of Debit Invoice ID
				movement.setDebitInvoiceId(manualAdviseDAO.getDebitInvoiceID(advise.getAdviseID()));

				movement.setTaxHeaderId(taxHeader.getHeaderId());
				movement.setTaxHeader(taxHeader);
			}

			if (!AdviseType.isPayable(paymentDetail.getAmountType())) {
				manualAdviseDAO.saveMovement(movement, "");
			}
		}

		logger.debug(Literal.LEAVING);

		// GST Invoice data resetting based on Accounting Process
		if (SysParamUtil.isAllowed(SMTParameterConstants.GST_INV_ON_DUE) && advise.isDueCreation()) {
			return movement;
		}

		return null;
	}

	@Override
	public boolean getPaymentHeadersByFinReference(long finID, String type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Long getPaymentIdByFinId(long finID, long receiptId, String type) {
		return this.paymentHeaderDAO.getPaymentIdByFinId(finID, receiptId, type);
	}

	@Override
	public PaymentInstruction getPaymentInstruction(long paymentId) {
		return this.paymentInstructionService.getPaymentInstruction(paymentId);
	}

	@Override
	public boolean isInstructionInProgress(String finReference) {
		return this.paymentInstructionService.isInstructionInProgress(finReference);
	}

	@Override
	public Map<Long, BigDecimal> getAdvisesInProgess(long finId) {
		return this.paymentHeaderDAO.getAdvisesInProgess(finId);
	}

	@Override
	public BigDecimal getInProgressExcessAmt(long finId, long receiptId) {
		return this.paymentHeaderDAO.getInProgressExcessAmt(finId, receiptId);
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public void setPaymentHeaderDAO(PaymentHeaderDAO paymentHeaderDAO) {
		this.paymentHeaderDAO = paymentHeaderDAO;
	}

	public void setPaymentDetailService(PaymentDetailService paymentDetailService) {
		this.paymentDetailService = paymentDetailService;
	}

	public void setPaymentInstructionService(PaymentInstructionService paymentInstructionService) {
		this.paymentInstructionService = paymentInstructionService;
	}

	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}

	public void setTaxHeaderDetailsDAO(TaxHeaderDetailsDAO taxHeaderDetailsDAO) {
		this.taxHeaderDetailsDAO = taxHeaderDetailsDAO;
	}

	public void setFinFeeDetailService(FinFeeDetailService finFeeDetailService) {
		this.finFeeDetailService = finFeeDetailService;
	}

	public void setFinanceTaxDetailDAO(FinanceTaxDetailDAO financeTaxDetailDAO) {
		this.financeTaxDetailDAO = financeTaxDetailDAO;
	}

	public void setCustomerAddresDAO(CustomerAddresDAO customerAddresDAO) {
		this.customerAddresDAO = customerAddresDAO;
	}

	public void setManualAdviseDAO(ManualAdviseDAO manualAdviseDAO) {
		this.manualAdviseDAO = manualAdviseDAO;
	}

	public void setGstInvoiceTxnService(GSTInvoiceTxnService gstInvoiceTxnService) {
		this.gstInvoiceTxnService = gstInvoiceTxnService;
	}

	public void setInstrumentwiseLimitService(InstrumentwiseLimitService instrumentwiseLimitService) {
		this.instrumentwiseLimitService = instrumentwiseLimitService;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public void setFeeTypeService(FeeTypeService feeTypeService) {
		this.feeTypeService = feeTypeService;
	}

	@Override
	public PaymentHeader prepareRefund(AutoRefundLoan refundLoan, List<PaymentDetail> payDtlList,
			PaymentInstruction payInst, Date appDate) {
		logger.debug(Literal.ENTERING);
		LoggedInUser userDetails = PFSBatchAdmin.loggedInUser;
		Timestamp sysDate = new Timestamp(System.currentTimeMillis());

		// Payment Header
		PaymentHeader ph = new PaymentHeader();
		ph.setFinID(refundLoan.getFinID());
		ph.setFinReference(refundLoan.getFinReference());
		ph.setPaymentType(DisbursementConstants.CHANNEL_PAYMENT);
		ph.setCreatedOn(appDate);
		ph.setApprovedOn(appDate);
		ph.setStatus(RepayConstants.PAYMENT_APPROVE);
		ph.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		ph.setNewRecord(true);
		ph.setVersion(1);
		ph.setLastMntBy(userDetails.getUserId());
		ph.setLastMntOn(sysDate);
		ph.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		ph.setPaymentId(paymentHeaderDAO.getNewPaymentHeaderId());
		ph.setFinSource(UploadConstants.FINSOURCE_ID_CD_PAY_UPLOAD);

		// Payment Details
		BigDecimal totRefund = BigDecimal.ZERO;
		for (PaymentDetail pd : payDtlList) {
			pd.setRecordType(PennantConstants.RCD_ADD);
			pd.setNewRecord(true);
			pd.setVersion(1);
			pd.setUserDetails(userDetails);
			pd.setLastMntBy(userDetails.getUserId());
			pd.setLastMntOn(sysDate);
			pd.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);

			totRefund = totRefund.add(pd.getAmount());
		}

		// Payment Instructions
		payInst.setPostDate(appDate);
		payInst.setPaymentAmount(totRefund);
		payInst.setValueDate(appDate);
		payInst.setPaymentCCy(refundLoan.getFinCcy());
		payInst.setStatus(DisbursementConstants.STATUS_NEW);
		payInst.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		payInst.setRecordType(PennantConstants.RCD_ADD);
		payInst.setNewRecord(true);
		payInst.setVersion(1);
		payInst.setLastMntBy(userDetails.getUserId());
		payInst.setLastMntOn(sysDate);

		ph.setPaymentDetailList(payDtlList);
		ph.setPaymentInstruction(payInst);

		logger.debug(Literal.LEAVING);
		return ph;
	}

}