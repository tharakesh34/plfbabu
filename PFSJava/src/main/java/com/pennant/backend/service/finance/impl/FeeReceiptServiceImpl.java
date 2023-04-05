package com.pennant.backend.service.finance.impl;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.security.auth.login.AccountNotFoundException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.constants.AccountConstants;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.FeeCalculator;
import com.pennant.app.util.GSTCalculator;
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.app.util.SessionUserDetails;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.Repayments.FinanceRepaymentsDAO;
import com.pennant.backend.dao.administration.SecurityUserDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.configuration.VASConfigurationDAO;
import com.pennant.backend.dao.configuration.VASRecordingDAO;
import com.pennant.backend.dao.finance.FinFeeDetailDAO;
import com.pennant.backend.dao.finance.FinFeeReceiptDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.TaxHeaderDetailsDAO;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.dao.receipts.FinReceiptDetailDAO;
import com.pennant.backend.dao.receipts.FinReceiptHeaderDAO;
import com.pennant.backend.dao.rmtmasters.AccountingSetDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.configuration.VASConfiguration;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.extendedfield.ExtendedFieldExtension;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinExcessMovement;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinFeeReceipt;
import com.pennant.backend.model.finance.FinReceiptData;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinRepayHeader;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.InvoiceDetail;
import com.pennant.backend.model.finance.TaxAmountSplit;
import com.pennant.backend.model.finance.TaxHeader;
import com.pennant.backend.model.finance.Taxes;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.extendedfields.ExtendedFieldDetailsService;
import com.pennant.backend.service.extendedfieldsExtension.ExtendedFieldExtensionService;
import com.pennant.backend.service.finance.FeeReceiptService;
import com.pennant.backend.service.finance.FinFeeDetailService;
import com.pennant.backend.service.finance.GSTInvoiceTxnService;
import com.pennant.backend.util.ExtendedFieldConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.pff.core.engine.accounting.AccountingEngine;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.engine.workflow.WorkflowEngine;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.receipt.constants.AllocationType;
import com.pennanttech.pff.receipt.constants.ReceiptMode;
import com.rits.cloning.Cloner;

public class FeeReceiptServiceImpl extends GenericService<FinReceiptHeader> implements FeeReceiptService {
	private static final Logger logger = LogManager.getLogger(FeeReceiptServiceImpl.class);

	private FinReceiptHeaderDAO finReceiptHeaderDAO;
	private FinReceiptDetailDAO finReceiptDetailDAO;
	private FinFeeDetailDAO finFeeDetailDAO;
	private PostingsPreparationUtil postingsPreparationUtil;
	private FinanceRepaymentsDAO financeRepaymentsDAO;
	private AccountingSetDAO accountingSetDAO;
	private AuditHeaderDAO auditHeaderDAO;
	private FinFeeReceiptDAO finFeeReceiptDAO;
	protected transient WorkflowEngine workFlow = null;
	private FinanceMainDAO financeMainDAO;
	private SecurityUserDAO securityUserDAO;
	private FinExcessAmountDAO finExcessAmountDAO;
	private TaxHeaderDetailsDAO taxHeaderDetailsDAO;
	private FinFeeDetailService finFeeDetailService;
	private GSTInvoiceTxnService gstInvoiceTxnService;
	private VASRecordingDAO vASRecordingDAO;
	private FinanceTypeDAO financeTypeDAO;
	private ExtendedFieldDetailsService extendedFieldDetailsService;
	private ExtendedFieldExtensionService extendedFieldExtensionService;
	private VASConfigurationDAO vASConfigurationDAO;

	public FeeReceiptServiceImpl() {
		super();
	}

	/**
	 * Method for Fetching Receipt Details , record is waiting for Realization
	 * 
	 * @param finReference
	 * @return
	 */
	@Override
	public FinReceiptHeader getFinReceiptHeaderById(long receiptID, String type) {
		logger.debug(Literal.ENTERING);

		// Receipt Header Details
		FinReceiptHeader rch = finReceiptHeaderDAO.getReceiptHeaderByID(receiptID, "_FView");

		if (rch == null) {
			return rch;
		}

		// Fetch Receipt Detail List
		List<FinReceiptDetail> receiptDetailList = finReceiptDetailDAO.getReceiptHeaderByID(receiptID, "_TView");

		// Fetch Repay Headers List
		String reference = rch.getReference();
		Long finID = rch.getFinID();

		List<FinRepayHeader> rphList = new ArrayList<>();

		if (finID != null && finID > 0) {
			rphList = financeRepaymentsDAO.getFinRepayHeadersByRef(finID, TableType.TEMP_TAB.getSuffix());
		}

		for (FinReceiptDetail receiptDetail : receiptDetailList) {
			for (FinRepayHeader finRepayHeader : rphList) {
				if (finRepayHeader.getReceiptSeqID() == receiptDetail.getReceiptSeqID()) {
					receiptDetail.getRepayHeaders().add(finRepayHeader);
				}
			}
		}

		rch.setReceiptDetails(receiptDetailList);

		// Paid Fee Details
		reference = rch.getExtReference();
		if (StringUtils.isBlank(reference)) {
			reference = rch.getReference();
		}

		rch.setPaidFeeList(getPaidFinFeeDetails(reference, rch.getReceiptID(), "_TView"));

		logger.debug(Literal.LEAVING);
		return rch;
	}

	/**
	 * Method for Fetching List of Fee Details for Display purpose
	 */
	@Override
	public List<FinFeeDetail> getPaidFinFeeDetails(String reference, long receiptID, String type) {
		List<FinFeeDetail> feeList = finFeeDetailDAO.getPaidFinFeeDetails(reference, type);

		if (CollectionUtils.isEmpty(feeList)) {
			return feeList;
		}

		List<FinFeeReceipt> feeReceipts = finFeeReceiptDAO.getFinFeeReceiptByReceiptId(receiptID, type);

		// Finance Fee Schedule Details
		for (FinFeeDetail fee : feeList) {
			fee.getFinFeeReceipts().add(getFinFeeReceiptbyFeeID(feeReceipts, fee.getFeeID()));

			Long taxHeaderId = fee.getTaxHeaderId();
			if (taxHeaderId == null || taxHeaderId <= 0) {
				continue;
			}

			fee.setTaxHeader(taxHeaderDetailsDAO.getTaxHeaderDetailsById(taxHeaderId, type));
			if (fee.getTaxHeader() != null) {
				fee.getTaxHeader().setTaxDetails(taxHeaderDetailsDAO.getTaxDetailById(taxHeaderId, type));
			}
		}

		return feeList;
	}

	private FinFeeReceipt getFinFeeReceiptbyFeeID(List<FinFeeReceipt> finFeeReceipts, long feeID) {
		for (FinFeeReceipt finFeeReceipt : finFeeReceipts) {
			if (feeID == finFeeReceipt.getFeeID()) {
				return finFeeReceipt;
			}
		}
		FinFeeReceipt finFeeReceipt = new FinFeeReceipt();
		finFeeReceipt.setFeeID(feeID);
		return finFeeReceipt;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table
	 * FinReceiptHeader/FinReceiptHeader_Temp by using FinReceiptHeaderDAO's save method b) Update the Record in the
	 * table. based on the module workFlow Configuration. by using FinReceiptHeaderDAO's update method 3) Audit the
	 * record in to AuditHeader and AdtFinReceiptHeader by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 * @throws AccountNotFoundException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader aAuditHeader)
			throws InterfaceException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		FinReceiptHeader rch = (FinReceiptHeader) aAuditHeader.getAuditDetail().getModelData();
		long serviceUID = Long.MIN_VALUE;

		ExtendedFieldRender efr = rch.getExtendedFieldRender();
		if (efr != null) {
			serviceUID = extendedFieldDetailsService.getInstructionUID(efr, rch.getExtendedFieldExtension());
		}

		aAuditHeader = businessValidation(aAuditHeader, "saveOrUpdate");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		if (!aAuditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return aAuditHeader;
		}

		Cloner cloner = new Cloner();
		AuditHeader auditHeader = cloner.deepClone(aAuditHeader);

		TableType tableType = TableType.MAIN_TAB;
		if (rch.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		// Receipt Header Details Save And Update
		// =======================================
		long receiptID = rch.getReceiptID();
		if (rch.isNewRecord()) {
			rch.setReceiptModeStatus(RepayConstants.PAYSTATUS_FEES);

			// Save Receipt Header
			receiptID = finReceiptHeaderDAO.save(rch, tableType);

		} else {
			finReceiptHeaderDAO.update(rch, tableType);

			// Delete Save Receipt Detail List by Reference
			finReceiptDetailDAO.deleteByReceiptID(receiptID, tableType);

			// Delete and Save FinRepayHeader Detail list by Reference
			financeRepaymentsDAO.deleteByRef(rch.getFinID(), tableType);
		}

		for (FinReceiptDetail receiptDetail : rch.getReceiptDetails()) {
			receiptDetail.setReceiptID(receiptID);
			long receiptSeqID = finReceiptDetailDAO.save(receiptDetail, tableType);

			List<FinRepayHeader> rpyHeaderList = receiptDetail.getRepayHeaders();
			for (FinRepayHeader rpyHeader : rpyHeaderList) {
				rpyHeader.setReceiptSeqID(receiptSeqID);

				// Save Repay Header details
				financeRepaymentsDAO.saveFinRepayHeader(rpyHeader, tableType);
			}
		}

		// Processing FinFeeReceipt
		List<AuditDetail> auditdetails = processFinFeeReceipt(rch, tableType.getSuffix(),
				auditHeader.getAuditTranType());
		auditDetails.addAll(auditdetails);

		// Extended field Details
		ExtendedFieldRender exdFldRender = rch.getExtendedFieldRender();
		ExtendedFieldExtension exdFldExt = rch.getExtendedFieldExtension();
		Map<String, List<AuditDetail>> auditDetailMap = rch.getAuditDetailMap();
		ExtendedFieldHeader exdHeader = rch.getExtendedFieldHeader();

		if (exdFldRender != null && exdHeader != null) {
			List<AuditDetail> details = auditDetailMap.get("ExtendedFieldDetails");
			details = extendedFieldDetailsService.processingExtendedFieldDetailList(details,
					ExtendedFieldConstants.MODULE_LOAN, exdHeader.getEvent(), tableType.getSuffix(), serviceUID);

			auditDetails.addAll(details);
		}

		// Extended field Extensions Details
		if (exdFldExt != null) {
			List<AuditDetail> details = auditDetailMap.get("ExtendedFieldExtension");
			FinReceiptData rd = new FinReceiptData();
			rd.setReceiptHeader(rch);

			details = extendedFieldExtensionService.processingExtendedFieldExtList(details, rd, serviceUID, tableType);

			auditDetails.addAll(details);
		}

		// Audit
		String[] recHeaderFields = PennantJavaUtil.getFieldDetails(new FinReceiptHeader(), rch.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, recHeaderFields[0],
				recHeaderFields[1], rch.getBefImage(), rch));

		FinReceiptDetail detail = new FinReceiptDetail();
		String[] fields = PennantJavaUtil.getFieldDetails(detail, detail.getExcludeFields());
		for (int i = 0; i < rch.getReceiptDetails().size(); i++) {
			auditDetails.add(new AuditDetail(auditHeader.getAuditTranType(), i, fields[0], fields[1], null,
					rch.getReceiptDetails().get(i)));
		}
		auditHeader.setAuditDetails(auditDetails);
		auditHeaderDAO.addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using finReceiptHeaderDAO.delete with parameters finReceiptHeader,"_Temp" 3) Audit the record
	 * in to AuditHeader and AdtFinReceiptHeader by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 * @throws InterfaceException
	 */
	@Override
	public AuditHeader doReject(AuditHeader auditHeader) throws InterfaceException {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<>();

		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		FinReceiptHeader rch = (FinReceiptHeader) auditHeader.getAuditDetail().getModelData();

		// Delete Receipt Header
		if (RepayConstants.RECEIPTTO_CUSTOMER.equals(rch.getRecAgainst())) {
			finFeeDetailDAO.deleteByTransactionId(rch.getExtReference(), false, TableType.TEMP_TAB.getSuffix());
		}

		financeRepaymentsDAO.deleteByRef(rch.getFinID(), TableType.TEMP_TAB);
		finReceiptDetailDAO.deleteByReceiptID(rch.getReceiptID(), TableType.TEMP_TAB);

		rch.setRecordType(PennantConstants.RECORD_TYPE_CAN);
		if (RepayConstants.RECEIPTTO_CUSTOMER.equals(rch.getRecAgainst())) {
			finFeeDetailDAO.deleteByTransactionId(rch.getExtReference(), false, TableType.TEMP_TAB.getSuffix());
		}

		List<AuditDetail> adtdetaisl = processFinFeeReceipt(rch, TableType.TEMP_TAB.getSuffix(),
				auditHeader.getAuditTranType());
		finReceiptHeaderDAO.deleteByReceiptID(rch.getReceiptID(), TableType.TEMP_TAB);
		// finServiceInstructionDAO.deleteFinServInstList(receiptHeader.getReference(), TableType.TEMP_TAB.getSuffix(),
		// String.valueOf(receiptHeader.getReceiptID()));

		// Delete Extended field Render Details.
		List<AuditDetail> extendedDetails = rch.getAuditDetailMap().get("ExtendedFieldDetails");
		if (extendedDetails != null && extendedDetails.size() > 0) {
			auditDetails.addAll(extendedFieldDetailsService.delete(rch.getExtendedFieldHeader(), rch.getReference(),
					rch.getExtendedFieldRender().getSeqNo(), "_Temp", auditHeader.getAuditTranType(), extendedDetails));
		}

		if (rch.getExtendedFieldExtension() != null) {
			List<AuditDetail> details = rch.getAuditDetailMap().get("ExtendedFieldExtension");
			details = extendedFieldExtensionService.delete(details, auditHeader.getAuditTranType(), TableType.TEMP_TAB);
			auditDetails.addAll(details);
		}

		auditHeader.setAuditDetails(auditDetails);
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		String[] fields = PennantJavaUtil.getFieldDetails(new FinReceiptHeader(), rch.getExcludeFields());
		auditHeader.setAuditDetail(
				new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1], rch.getBefImage(), rch));
		if (auditHeader.getAuditDetails() == null) {
			auditHeader.setAuditDetails(new ArrayList<AuditDetail>());
		}
		auditHeader.getAuditDetails().addAll(adtdetaisl);
		auditHeaderDAO.addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doApprove method do the following steps. Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. based on the Record type do
	 * following actions Update record in the main table by using finReceiptHeaderDAO.update with parameters
	 * FinReceiptHeader. Audit the record in to AuditHeader and AdtFinReceiptHeader by using
	 * auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 * @throws AccountNotFoundException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	@Override
	public AuditHeader doApprove(AuditHeader aAuditHeader)
			throws InterfaceException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<>();
		FinReceiptHeader rch = (FinReceiptHeader) aAuditHeader.getAuditDetail().getModelData();

		long serviceUID = Long.MIN_VALUE;
		ExtendedFieldRender exdFldRender = rch.getExtendedFieldRender();
		if (exdFldRender != null) {
			serviceUID = extendedFieldDetailsService.getInstructionUID(exdFldRender, rch.getExtendedFieldExtension());
		}

		String tranType = "";
		aAuditHeader = businessValidation(aAuditHeader, "doApprove");
		if (!aAuditHeader.isNextProcess()) {
			return aAuditHeader;
		}

		Cloner cloner = new Cloner();
		AuditHeader auditHeader = cloner.deepClone(aAuditHeader);
		rch = (FinReceiptHeader) auditHeader.getAuditDetail().getModelData();

		// Accounting Process Execution
		AEEvent aeEvent = new AEEvent();
		aeEvent.setAccountingEvent(AccountingEvent.FEEPAY);
		aeEvent.setFinID(rch.getFinID());
		aeEvent.setFinReference(rch.getReference());
		if (StringUtils.isNotBlank(rch.getExtReference())) {
			aeEvent.setFinReference(rch.getExtReference());
		}
		aeEvent.setCustCIF(rch.getCustCIF());
		aeEvent.setCustID(rch.getCustID());
		aeEvent.setBranch(rch.getPostBranch());
		aeEvent.setCcy(rch.getFinCcy());
		aeEvent.setPostingUserBranch(rch.getPostBranch());

		// Setting value date from receipt header for backdated receipt
		// aeEvent.setValueDate(DateUtility.getAppDate());
		aeEvent.setValueDate(rch.getValueDate());

		if (aeEvent.getCcy() == null) {
			aeEvent.setCcy(rch.getCustBaseCcy());
		}

		if (aeEvent.getCcy() == null) {
			aeEvent.setCcy(SysParamUtil.getAppCurrency());
		}

		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
		if (amountCodes == null) {
			amountCodes = new AEAmountCodes();
		}

		// Fetch Accounting Set ID
		Long accountingSetID = 0L;
		Map<String, Object> map = null;

		String recAgainst = rch.getRecAgainst();
		if (RepayConstants.RECEIPTTO_FINANCE.equals(recAgainst) || (RepayConstants.RECEIPTTO_CUSTOMER.equals(recAgainst)
				&& StringUtils.isNotBlank(rch.getExtReference()))) {
			accountingSetID = AccountingEngine.getAccountSetID(rch.getFinType(), AccountingEvent.FEEPAY,
					FinanceConstants.MODULEID_FINTYPE);

			map = financeMainDAO.getGLSubHeadCodes(rch.getFinID());
		}

		if (accountingSetID == null || accountingSetID <= 0) {
			auditHeader.setErrorDetails(
					ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "65015", null, null)));
			logger.debug("Leaving");
			return auditHeader;
		}

		FinReceiptDetail finreceiptDetail = rch.getReceiptDetails().get(0);
		amountCodes.setPartnerBankAc(finreceiptDetail.getPartnerBankAc());
		amountCodes.setPaymentType(finreceiptDetail.getPaymentType());
		amountCodes.setPartnerBankAcType(finreceiptDetail.getPartnerBankAcType());
		amountCodes.setPaidFee(finreceiptDetail.getAmount());
		amountCodes.setFinType(rch.getFinType());
		Map<String, Object> dataMap = amountCodes.getDeclaredFieldValues();

		if (map != null) {
			BigDecimal alwFlexi = BigDecimal.ZERO;
			if (map.get("ALWFLEXI") instanceof Long) {
				long value = (long) map.get("ALWFLEXI");
				amountCodes.setAlwflexi(value == 0 ? false : true);
			} else if (map.get("ALWFLEXI") instanceof BigDecimal) {
				alwFlexi = (BigDecimal) map.get("ALWFLEXI");
				amountCodes.setAlwflexi(alwFlexi.compareTo(BigDecimal.ZERO) == 0 ? false : true);
			} else if (map.get("ALWFLEXI") instanceof Integer) {
				int value = (int) map.get("ALWFLEXI");
				amountCodes.setAlwflexi(value == 0 ? false : true);
			}

			amountCodes.setEntitycode((String) map.get("ENTITYCODE"));
			dataMap.put("ae_finbranch", map.get("FINBRANCH"));
			dataMap.put("emptype", map.get("EMPTYPE"));
			dataMap.put("branchcity", map.get("BRANCHCITY"));
			dataMap.put("fincollateralreq", map.get("FINCOLLATERALREQ"));
			dataMap.put("btloan", map.get("BTLOAN"));

		} else {
			amountCodes.setEntitycode(rch.getEntityCode());
			amountCodes.setFinbranch(rch.getPostBranch());
		}

		aeEvent.setDataMap(dataMap);
		aeEvent.setPostRefId(rch.getReceiptID());

		aeEvent.getAcSetIDList().add(accountingSetID);

		// GST parameters
		Map<String, Object> gstExecutionMap = GSTCalculator.getGSTDataMap(rch.getFinID());

		if (gstExecutionMap != null) {
			for (String mapkey : gstExecutionMap.keySet()) {
				if (StringUtils.isNotBlank(mapkey)) {
					aeEvent.getDataMap().put(mapkey, gstExecutionMap.get(mapkey));
				}
			}
		}
		prepareFeeRulesMap(rch, aeEvent.getDataMap());

		aeEvent = postingsPreparationUtil.postAccounting(aeEvent);

		// Receipt Header Updation
		// =======================================
		tranType = PennantConstants.TRAN_UPD;
		rch.setReceiptModeStatus(RepayConstants.PAYSTATUS_FEES);
		rch.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		rch.setRecordType("");
		rch.setRoleCode("");
		rch.setNextRoleCode("");
		rch.setTaskId("");
		rch.setNextTaskId("");
		rch.setWorkflowId(0);
		rch.setExcessAdjustTo(RepayConstants.EXAMOUNTTYPE_EXCESS);

		if (StringUtils.isEmpty(rch.getExtReference())) {
			rch.setExtReference(rch.getReference());
		}

		finReceiptHeaderDAO.save(rch, TableType.MAIN_TAB);

		// Save Receipt Header
		for (FinReceiptDetail receiptDetail : rch.getReceiptDetails()) {
			receiptDetail.setStatus(RepayConstants.PAYSTATUS_APPROVED);

			long receiptSeqID = finReceiptDetailDAO.save(receiptDetail, TableType.MAIN_TAB);

			List<FinRepayHeader> rpyHeaderList = receiptDetail.getRepayHeaders();
			for (FinRepayHeader rpyHeader : rpyHeaderList) {
				rpyHeader.setReceiptSeqID(receiptSeqID);
				rpyHeader.setLinkedTranId(aeEvent.getLinkedTranId());

				// Save Repay Header details
				financeRepaymentsDAO.saveFinRepayHeader(rpyHeader, TableType.MAIN_TAB);
			}
		}

		List<AuditDetail> extendedDetails = rch.getAuditDetailMap().get("ExtendedFieldDetails");
		if (extendedDetails != null && extendedDetails.size() > 0) {
			extendedDetails = extendedFieldDetailsService.processingExtendedFieldDetailList(extendedDetails,
					ExtendedFieldConstants.MODULE_LOAN, rch.getExtendedFieldHeader().getEvent(), "", serviceUID);
			auditDetails.addAll(extendedDetails);
		}

		// Extended field Extensions Details
		List<AuditDetail> extensionDetails = rch.getAuditDetailMap().get("ExtendedFieldExtension");
		if (extensionDetails != null && extensionDetails.size() > 0) {
			FinReceiptData receiptData = new FinReceiptData();
			receiptData.setReceiptHeader(rch);
			extensionDetails = extendedFieldExtensionService.processingExtendedFieldExtList(extensionDetails,
					receiptData, serviceUID, TableType.MAIN_TAB);

			auditDetails.addAll(extensionDetails);
		}

		if (RepayConstants.RECEIPTTO_FINANCE.equals(recAgainst) || (RepayConstants.RECEIPTTO_CUSTOMER.equals(recAgainst)
				&& StringUtils.isNotBlank(rch.getExtReference()))) {
			Map<String, BigDecimal> taxPercentages = null;
			taxPercentages = GSTCalculator.getTaxPercentages(rch.getCustID(), rch.getFinCcy(),
					rch.getUserDetails().getBranchCode(), rch.getFinBranch());
			List<FinFeeDetail> feeList = rch.getPaidFeeList();
			calculateGST(rch.getPaidFeeList(), taxPercentages, true);

			// External Reference Process
			processFinfeeDetails(rch);

			for (FinFeeDetail finFeeDetail : feeList) {
				List<Taxes> taxDetails = finFeeDetail.getTaxHeader().getTaxDetails();
				for (Taxes taxes : taxDetails) {
					taxHeaderDetailsDAO.update(taxes, "_Temp");
				}
			}

			if (CollectionUtils.isNotEmpty(feeList)) {
				FinanceMain fm = null;
				if (StringUtils.isBlank(rch.getExtReference())) {
					fm = financeMainDAO.getFinanceMainById(rch.getFinID(), "_Temp", false);
					FinanceMain befImage = new FinanceMain();
					BeanUtils.copyProperties(fm, befImage);
					fm.setBefImage(befImage);
					fm.setLastMntOn(new Timestamp(System.currentTimeMillis()));

					BigDecimal totFeePaid = BigDecimal.ZERO;
					for (FinFeeDetail finFeeDetail : feeList) {
						FinFeeReceipt finFeeReceipt = finFeeDetail.getFinFeeReceipts().get(0);
						totFeePaid = totFeePaid.add(finFeeReceipt.getPaidAmount());
					}

					fm.setDeductFeeDisb(fm.getDeductFeeDisb().subtract(totFeePaid));
					financeMainDAO.updateDeductFeeDisb(fm, TableType.TEMP_TAB);
				} else {
					fm = new FinanceMain();
					fm.setFinType(rch.getFinType());
					fm.setFinBranch(rch.getFinBranch());
					fm.setCustID(rch.getCustID());
					fm.setFinReference(rch.getExtReference());
				}

				processGSTInvoicePreparation(aeEvent.getLinkedTranId(), fm, feeList, taxPercentages);
			}
		}

		// Normal Process
		List<AuditDetail> adtdetails = processFinFeeReceipt(rch, TableType.MAIN_TAB.getSuffix(), tranType);
		auditDetails.addAll(adtdetails);
		processExcessAmount(rch);

		// Delete Receipt Header
		if (RepayConstants.RECEIPTTO_CUSTOMER.equals(rch.getRecAgainst())) {
			finFeeDetailDAO.deleteByTransactionId(rch.getExtReference(), false, TableType.TEMP_TAB.getSuffix());
		}

		financeRepaymentsDAO.deleteByRef(rch.getFinID(), TableType.TEMP_TAB);
		finReceiptDetailDAO.deleteByReceiptID(rch.getReceiptID(), TableType.TEMP_TAB);
		finReceiptHeaderDAO.deleteByReceiptID(rch.getReceiptID(), TableType.TEMP_TAB);

		String[] fields = PennantJavaUtil.getFieldDetails(new FinReceiptHeader(), rch.getExcludeFields());
		auditHeader.setAuditDetail(
				new AuditDetail(aAuditHeader.getAuditTranType(), 1, fields[0], fields[1], rch.getBefImage(), rch));

		auditHeader.setAuditTranType(tranType);
		auditHeader.setAuditDetail(
				new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1], rch.getBefImage(), rch));

		// Adding audit as Insert/Update/deleted into main table
		auditHeader.setAuditDetails(auditDetails);
		auditHeaderDAO.addAudit(auditHeader);

		// Extended field Render Details Delete from temp.
		if (extendedDetails != null && extendedDetails.size() > 0) {
			extendedDetails = extendedFieldDetailsService.delete(rch.getExtendedFieldHeader(), rch.getReference(),
					exdFldRender.getSeqNo(), "_Temp", auditHeader.getAuditTranType(), extendedDetails);
		}

		if (extensionDetails != null && extensionDetails.size() > 0) {
			extensionDetails = extendedFieldExtensionService.delete(extensionDetails, tranType, TableType.TEMP_TAB);
		}

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from the auditHeader. 2) fetch the details
	 * from the tables 3) Validate the Record based on the record details. 4) Validate for any business validation. 5)
	 * for any mismatch conditions Fetch the error details from finReceiptHeaderDAO.getErrorDetail with Error ID and
	 * language as parameters. 6) if any error/Warnings then assign the to auditHeader
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<>();

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		auditHeader = getAuditDetails(auditHeader, method);

		FinReceiptHeader rch = (FinReceiptHeader) auditHeader.getAuditDetail().getModelData();
		String usrLanguage = rch.getUserDetails().getLanguage();

		if (rch.getExtendedFieldRender() != null) {
			List<AuditDetail> details = rch.getAuditDetailMap().get("ExtendedFieldDetails");
			ExtendedFieldHeader extHeader = rch.getExtendedFieldHeader();
			details = extendedFieldDetailsService.validateExtendedDdetails(extHeader, details, method, usrLanguage);
			auditDetails.addAll(details);
		}

		if (rch.getExtendedFieldExtension() != null) {
			List<AuditDetail> details = rch.getAuditDetailMap().get("ExtendedFieldExtension");
			details = extendedFieldExtensionService.vaildateDetails(details, usrLanguage);
			auditDetails.addAll(details);
		}

		auditHeader.setAuditDetails(auditDetails);

		auditHeader = nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * Method for prepare AuditHeader
	 * 
	 */
	private AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<>();
		Map<String, List<AuditDetail>> auditDetailMap = new HashMap<>();

		FinReceiptHeader frh = (FinReceiptHeader) auditHeader.getAuditDetail().getModelData();

		String auditTranType = "";
		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (frh.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}

		// Extended Field Details
		if (frh.getExtendedFieldRender() != null) {
			auditDetailMap.put("ExtendedFieldDetails",
					extendedFieldDetailsService.setExtendedFieldsAuditData(frh.getExtendedFieldHeader(),
							frh.getExtendedFieldRender(), auditTranType, method, ExtendedFieldConstants.MODULE_LOAN));
			auditDetails.addAll(auditDetailMap.get("ExtendedFieldDetails"));
		}

		ExtendedFieldExtension extendedFieldExtension = frh.getExtendedFieldExtension();
		if (extendedFieldExtension != null) {
			auditDetailMap.put("ExtendedFieldExtension", extendedFieldExtensionService
					.setExtendedFieldExtAuditData(extendedFieldExtension, auditTranType, method));
			frh.setAuditDetailMap(auditDetailMap);
			auditDetails.addAll(auditDetailMap.get("ExtendedFieldExtension"));
		}

		frh.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(frh);
		auditHeader.setAuditDetails(auditDetails);

		logger.debug("Leaving ");
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
		FinReceiptHeader receiptHeader = (FinReceiptHeader) auditDetail.getModelData();

		FinReceiptHeader tempReceiptHeader = null;
		if (receiptHeader.isWorkflow()) {
			tempReceiptHeader = finReceiptHeaderDAO.getReceiptHeaderByID(receiptHeader.getReceiptID(), "_Temp");
		}
		FinReceiptHeader beFinReceiptHeader = finReceiptHeaderDAO.getReceiptHeaderByID(receiptHeader.getReceiptID(),
				"");
		FinReceiptHeader oldReceiptHeader = receiptHeader.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];

		String reference = receiptHeader.getReference();
		if (RepayConstants.RECEIPTTO_CUSTOMER.equals(receiptHeader.getRecAgainst())) {
			reference = receiptHeader.getCustomerCIF();
		}
		valueParm[0] = reference;
		errParm[0] = PennantJavaUtil.getLabel("label_Reference") + ":" + valueParm[0];
		if (receiptHeader.isNewRecord()) { // for New record or new record into work flow

			if (!receiptHeader.isWorkflow()) {// With out Work flow only new
				// records
				if (beFinReceiptHeader != null) { // Record Already Exists in the
					// table then error
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow
				if (receiptHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if
					// records type is new
					if (beFinReceiptHeader != null || tempReceiptHeader != null) { // if
						// records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
					}
				} else { // if records not exists in the Main flow table
					if (beFinReceiptHeader == null || tempReceiptHeader != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!receiptHeader.isWorkflow()) { // With out Work flow for update
				// and delete

				if (beFinReceiptHeader == null) { // if records not exists in the
					// main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, valueParm), usrLanguage));
				} else {
					if (oldReceiptHeader != null
							&& !oldReceiptHeader.getLastMntOn().equals(beFinReceiptHeader.getLastMntOn())) {
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

				if (tempReceiptHeader == null) { // if records not exists in the
					// Work flow table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}

				if (tempReceiptHeader != null && oldReceiptHeader != null
						&& !oldReceiptHeader.getLastMntOn().equals(tempReceiptHeader.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}
			}
		}

		if (!PennantConstants.RCD_STATUS_RESUBMITTED.equals(receiptHeader.getRecordStatus())
				&& !PennantConstants.RCD_STATUS_REJECTED.equals(receiptHeader.getRecordStatus())
				&& !PennantConstants.RCD_STATUS_CANCELLED.equals(receiptHeader.getRecordStatus())
				&& !PennantConstants.FINSOURCE_ID_API.equals(receiptHeader.getReceiptSource())) {
			if (getFeeReceiptExist(receiptHeader.getReference(), receiptHeader.getReceiptPurpose(),
					receiptHeader.getReceiptID())) {
				auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "65014", errParm, valueParm));
			}
		}

		validateFinFeereceipts(receiptHeader, usrLanguage, auditDetail);

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !receiptHeader.isWorkflow()) {
			receiptHeader.setBefImage(beFinReceiptHeader);
		}

		return auditDetail;
	}

	private void validateFinFeereceipts(FinReceiptHeader receiptHeader, String usrLanguage, AuditDetail auditDetail) {

		List<FinFeeDetail> feeDetails = receiptHeader.getPaidFeeList();
		if (CollectionUtils.isEmpty(feeDetails)) {
			return;
		}
		// IN case of any business level validation is required.
	}

	private boolean getFeeReceiptExist(String reference, String receiptPurpose, long receiptId) {
		logger.debug("Entering");

		boolean codeExist = false;

		if (finReceiptHeaderDAO.geFeeReceiptCount(reference, receiptPurpose, receiptId) != 0) {
			codeExist = true;
		}

		logger.debug("Leaving");

		return codeExist;
	}

	@Override
	public void prepareFeeRulesMap(FinReceiptHeader finReceiptHeader, Map<String, Object> dataMap) {
		logger.debug(Literal.ENTERING);

		List<FinFeeDetail> finFeeDetailList = finReceiptHeader.getPaidFeeList();

		if (CollectionUtils.isEmpty(finFeeDetailList)) {
			dataMap.put("ae_toExcessAmt", finReceiptHeader.getReceiptAmount());
			return;
		}

		String userBranch = finReceiptHeader.getPostBranch();
		Map<String, BigDecimal> taxPercentages = finReceiptHeader.getTaxPercentages();
		if (MapUtils.isEmpty(finReceiptHeader.getTaxPercentages())) {
			taxPercentages = GSTCalculator.getTaxPercentages(finReceiptHeader.getCustID(), finReceiptHeader.getFinCcy(),
					userBranch, finReceiptHeader.getFinBranch());
		}

		List<FinFeeDetail> tempFinFeeDetails = new ArrayList<FinFeeDetail>();
		for (FinFeeDetail finFeeDetail : finFeeDetailList) {
			FinFeeDetail tempfinFee = new FinFeeDetail();
			BeanUtils.copyProperties(finFeeDetail, tempfinFee);
			tempFinFeeDetails.add(tempfinFee);
		}

		calculateGST(tempFinFeeDetails, taxPercentages, false);

		dataMap.put("ae_paidVasFee", BigDecimal.ZERO);
		BigDecimal totPaidAmt = BigDecimal.ZERO;

		List<VASConfiguration> vasProdutsList = this.vASConfigurationDAO.getVASConfigurations("_AView");
		dataMap.put("VAS_P", BigDecimal.ZERO);
		for (VASConfiguration vap : vasProdutsList) {
			String vasProductCode = vap.getProductCode();
			dataMap.put("VAS_" + vasProductCode + "_DD", BigDecimal.ZERO);
			dataMap.put("VAS_" + vasProductCode + "_AF", BigDecimal.ZERO);
			dataMap.put("VAS_" + vasProductCode + "_W", BigDecimal.ZERO);
			dataMap.put("VAS_" + vasProductCode + "_P", BigDecimal.ZERO);
		}

		for (FinFeeDetail fd : tempFinFeeDetails) {
			if (!fd.isRcdVisible()) {
				continue;
			}

			// Incase of Vas FEE
			if (AccountingEvent.VAS_FEE.equals(fd.getFinEvent())) {
				BigDecimal vasPaidFee = (BigDecimal) dataMap.get("ae_paidVasFee");
				vasPaidFee = vasPaidFee.add(fd.getPaidAmountOriginal());
				dataMap.put("ae_paidVasFee", vasPaidFee);
				dataMap.put("VAS_P", vasPaidFee);
				totPaidAmt = totPaidAmt.add(fd.getPaidAmount());

				String vasProductCode = fd.getVasProductCode();
				if (fd.getFeeScheduleMethod().equals(CalculationConstants.REMFEE_PART_OF_DISBURSE)) {
					dataMap.put("VAS_" + vasProductCode + "_DD", fd.getRemainingFee());
					dataMap.put("VAS_" + vasProductCode + "_AF", BigDecimal.ZERO);

				} else {
					dataMap.put("VAS_" + vasProductCode + "_DD", BigDecimal.ZERO);
					dataMap.put("VAS_" + vasProductCode + "_AF", fd.getRemainingFee());
				}

				dataMap.put("VAS_" + vasProductCode + "_W", fd.getWaivedAmount());
				dataMap.put("VAS_" + vasProductCode + "_P", fd.getPaidAmountOriginal());

				continue;
			}

			String feeTypeCode = fd.getFeeTypeCode();

			dataMap.putAll(FeeCalculator.getFeeRuleMap(fd));

			// Paid Amount
			FinFeeDetail befImage = fd.getBefImage();

			if ((befImage.getRemainingFee().compareTo(fd.getPaidAmount()) == 0) && fd.getTaxHeaderId() != null
					&& (befImage.getRemainingFeeGST().compareTo(fd.getPaidAmountGST()) != 0)) {

				BigDecimal totalNetFee = fd.getActualAmountOriginal().subtract(fd.getWaivedAmount());
				totalNetFee = totalNetFee.add(fd.getNetAmountGST());

				TaxAmountSplit netTaxSplit = GSTCalculator.getInclusiveGST(totalNetFee, taxPercentages);
				BigDecimal totalPaid = befImage.getPaidAmount().add(befImage.getPaidTDS());
				TaxAmountSplit paidTaxSplit = GSTCalculator.getInclusiveGST(totalPaid, taxPercentages);
				BigDecimal paidAmt = fd.getNetAmountOriginal().subtract(befImage.getPaidAmountOriginal());

				dataMap.put(feeTypeCode + "_CGST_P", netTaxSplit.getcGST().subtract(paidTaxSplit.getcGST()));
				dataMap.put(feeTypeCode + "_SGST_P", netTaxSplit.getsGST().subtract(paidTaxSplit.getsGST()));
				dataMap.put(feeTypeCode + "_IGST_P", netTaxSplit.getiGST().subtract(paidTaxSplit.getiGST()));
				dataMap.put(feeTypeCode + "_UGST_P", netTaxSplit.getuGST().subtract(paidTaxSplit.getuGST()));
				dataMap.put(feeTypeCode + "_CESS_P", netTaxSplit.getCess().subtract(paidTaxSplit.getCess()));
				dataMap.put(feeTypeCode + "_P", paidAmt);
			}

			totPaidAmt = totPaidAmt.add(fd.getPaidAmount());
		}

		BigDecimal excessAmt = finReceiptHeader.getReceiptAmount().subtract(totPaidAmt);
		dataMap.put("ae_toExcessAmt", excessAmt);

		logger.debug(Literal.LEAVING);
	}

	@Override
	public ErrorDetail processFeePayment(FinServiceInstruction fsi) throws Exception {
		logger.debug(Literal.ENTERING);

		LoggedInUser userDetails = fsi.getLoggedInUser();

		if (SessionUserDetails.getLogiedInUser() != null) {
			userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		}

		List<FinFeeDetail> paidFeeList = null;
		String finDivision = null;

		Long finID = null;
		if (StringUtils.isNotBlank(fsi.getFinReference())) {
			String finReference = fsi.getFinReference();

			finID = financeMainDAO.getFinID(finReference, TableType.TEMP_TAB);

			if (finID == null) {
				return ErrorUtil.getErrorDetail(new ErrorDetail("9999"), userDetails.getLanguage());
			}

			FinanceMain fm = this.financeMainDAO.getFinBasicDetails(finID, "_Temp");
			if (fm == null) {
				return ErrorUtil.getErrorDetail(new ErrorDetail("9999"), userDetails.getLanguage());
			}
			// FinFeedetails under temp table
			paidFeeList = getPaidFinFeeDetails(finReference, Long.MIN_VALUE, "_TView");

			ErrorDetail errorDetail = validateUpFrontFees(fsi, paidFeeList, userDetails);
			if (errorDetail != null) {
				return errorDetail;
			}
			fsi.setCurrency(fm.getFinCcy());
			fsi.setCustCIF(fm.getCustCIF());
			fsi.setCustID(fm.getCustID());
			fsi.setFinType(fm.getFinType());
			fsi.setCurrency(fm.getFinCcy());

			if (StringUtils.isEmpty(fsi.getFromBranch())) {
				fsi.setFromBranch(fm.getFinBranch());
			}

			fsi.setToBranch(userDetails.getBranchCode());
			finDivision = fm.getLovDescFinDivision();
		} else {
			paidFeeList = fsi.getFinFeeDetails();
			finDivision = financeTypeDAO.getFinDivsion(fsi.getFinType());
		}

		FinReceiptHeader header = new FinReceiptHeader();

		header.setFinID(finID);
		header.setReference(fsi.getFinReference());
		header.setCustCIF(fsi.getCustCIF());
		header.setCustID(fsi.getCustID());

		long receiptId = finReceiptHeaderDAO.generatedReceiptID(header);

		fsi.setReceiptId(receiptId);
		header.setReceiptDate(SysParamUtil.getAppDate());
		header.setToState(fsi.getToState());
		header.setFromState(fsi.getFromState());
		header.setFinType(fsi.getFinType());
		header.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
		header.setRecAgainst(RepayConstants.RECEIPTTO_FINANCE);
		header.setReceiptID(receiptId);
		header.setReceiptPurpose(FinServiceEvent.FEEPAYMENT);
		header.setExcessAdjustTo(RepayConstants.EXAMOUNTTYPE_EXCESS);
		header.setAllocationType(AllocationType.AUTO);
		header.setReceiptAmount(fsi.getAmount());
		header.setEffectSchdMethod(PennantConstants.List_Select);
		header.setReceiptMode(fsi.getPaymentMode());
		header.setReceiptModeStatus(RepayConstants.PAYSTATUS_FEES);
		header.setRemarks(fsi.getReceiptDetail().getRemarks());
		// changed
		header.setFinCcy(fsi.getCurrency());
		header.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		header.setNewRecord(true);
		header.setLastMntBy(userDetails.getUserId());
		header.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		header.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		header.setUserDetails(userDetails);
		header.setReceiptSource(PennantConstants.FINSOURCE_ID_API);
		header.setRealizationDate(fsi.getRealizationDate());
		header.setFinType(fsi.getFinType());
		header.setFinBranch(fsi.getFromBranch());
		header.setCashierBranch(fsi.getToBranch());
		header.setFinDivision(finDivision);
		header.setValueDate(fsi.getValueDate());
		header.setTaxPercentages(fsi.getTaxPercentages());

		if (StringUtils.isNotEmpty(fsi.getFromBranch())) {
			header.setPostBranch(fsi.getFromBranch());
		} else {
			header.setPostBranch(userDetails.getBranchCode());
		}

		WorkFlowDetails workFlowDetails = null;
		String roleCode = null;
		String taskid = null;
		String nextTaskId = null;
		long workFlowId = 0;
		if (!fsi.isNonStp()) {
			header.setRecordStatus(PennantConstants.RCD_STATUS_SUBMITTED);
			workFlowDetails = WorkFlowUtil.getDetailsByType("FEERECEIPT_PROCESS");
			String processStage = fsi.getProcessStage();
			if (workFlowDetails != null) {
				workFlow = new WorkflowEngine(workFlowDetails.getWorkFlowXml());
				if (StringUtils.isBlank(processStage)) {
					processStage = workFlow.firstTaskOwner();
				} else {
					// validating given role is available in workflow roles
					String[] roles = StringUtils.split(workFlowDetails.getWorkFlowRoles(), ";");
					if (roles != null) {
						boolean flag = Arrays.asList(roles).contains(processStage);
						if (!flag) {
							String[] valueParm = new String[2];
							valueParm[0] = "processStage " + processStage;
							valueParm[1] = " workflow FEERECEIPT_PROCESS";
							return ErrorUtil.getErrorDetail(new ErrorDetail("API002", valueParm));
						}
					}
				}

				// If user role as first task owner record should be in save mode
				if (StringUtils.equals(processStage, workFlow.firstTaskOwner())) {
					header.setRecordStatus(PennantConstants.RCD_STATUS_SAVED);
				}

				taskid = workFlow.getUserTaskId(processStage); // ### 19-07-2018 Ticket ID : 128015
				workFlowId = workFlowDetails.getWorkFlowId();
				roleCode = processStage;// ### 19-07-2018 Ticket ID : 128015

				// nextTaskId = workFlow.getUserTaskId(workFlow.firstTaskOwner());//### 19-07-2018 Ticket ID : 128015
				nextTaskId = workFlow.getNextUserTaskIdsAsString(taskid, header);
				setNextRoleDetails(nextTaskId, workFlow, header);
			}
			header.setTaskId(taskid);
			header.setNextTaskId(nextTaskId);
			header.setRoleCode(roleCode);
			header.setWorkflowId(workFlowId);
		}
		if (ReceiptMode.CHEQUE.equals(header.getPaymentType()) || ReceiptMode.DD.equals(header.getPaymentType())) {
			header.setTransactionRef(fsi.getReceiptDetail().getFavourNumber());
		} else {
			header.setTransactionRef(fsi.getReceiptDetail().getTransactionRef());
		}

		header.setBankCode(fsi.getReceiptDetail().getBankCode());

		FinReceiptDetail afrd = fsi.getReceiptDetail();
		FinReceiptDetail frd = new FinReceiptDetail();
		frd.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
		frd.setPaymentTo(RepayConstants.RECEIPTTO_FINANCE);
		frd.setAmount(header.getReceiptAmount());
		frd.setPaymentType(header.getReceiptMode());
		frd.setValueDate(afrd.getValueDate());
		frd.setReceiptID(receiptId);
		frd.setFavourNumber(afrd.getFavourNumber());
		frd.setBankCode(afrd.getBankCode());
		frd.setBankBranchID(afrd.getBankBranchID());
		frd.setFavourName(afrd.getFavourName());
		frd.setDepositDate(afrd.getDepositDate());
		frd.setDepositNo(afrd.getDepositNo());
		frd.setPaymentRef(afrd.getPaymentRef());
		frd.setChequeAcNo(afrd.getChequeAcNo());
		frd.setFundingAc(afrd.getFundingAc());
		frd.setReceivedDate(afrd.getReceivedDate());
		frd.setTransactionRef(afrd.getTransactionRef());
		frd.setPartnerBankAc(afrd.getPartnerBankAc());
		frd.setPartnerBankAcType(afrd.getPartnerBankAcType());

		FinRepayHeader rh = new FinRepayHeader();
		rh.setFinReference(header.getReference());
		rh.setValueDate(frd.getReceivedDate());
		rh.setFinEvent(FinServiceEvent.FEEPAYMENT);
		rh.setRepayAmount(header.getReceiptAmount());

		// Setting
		frd.getRepayHeaders().add(rh);
		header.getReceiptDetails().add(frd);
		header.setPaidFeeList(paidFeeList);

		if (StringUtils.isNotBlank(fsi.getExternalReference())) {
			if (fsi.getCustID() != 0) {
				header.setReference(Objects.toString(fsi.getCustID(), ""));
			}
			header.setExtReference(fsi.getExternalReference());
			header.setRecAgainst(RepayConstants.RECEIPTTO_CUSTOMER);
			rh.setFinReference(fsi.getExternalReference());
		}
		header.setExtReference(fsi.getExternalReference());

		AuditHeader auditHeader = getAuditHeader(header, PennantConstants.TRAN_WF);

		if (!fsi.isNonStp()) {
			if (RepayConstants.RECEIPTTO_CUSTOMER.equals(header.getRecAgainst())) {
				Map<String, BigDecimal> taxPercentages = GSTCalculator.getTaxPercentages(header.getCustID(),
						header.getFinCcy(), header.getUserDetails().getBranchCode(), header.getFinBranch());
				FinanceMain financeMain = null;
				for (FinFeeDetail finFeeDetail : header.getPaidFeeList()) {
					String taxComponent = finFeeDetail.getTaxComponent();
					finFeeDetail.setTaxComponent(FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE);
					finFeeDetail.setPaidCalcReq(false);
					calculateFees(finFeeDetail, financeMain, taxPercentages);
					finFeeDetail.setTaxComponent(taxComponent);
				}
			}
			saveOrUpdate(auditHeader);
		} else {
			doApprove(auditHeader);
		}

		// failed
		if (!auditHeader.isNextProcess()) {
			return ErrorUtil.getErrorDetail(auditHeader.getAuditDetail().getErrorDetails().get(0));
		}

		return null;
	}

	private void setNextRoleDetails(String nextTaskId, WorkflowEngine workFlow, FinReceiptHeader header) {
		// Set the role codes for the next tasks
		String nextRoleCode = "";
		String nextRole = "";
		Map<String, String> baseRoleMap = null;

		if (workFlow != null && !StringUtils.isBlank(nextTaskId)) {
			String[] nextTasks = nextTaskId.split(";");
			if (nextTasks.length > 0) {
				baseRoleMap = new HashMap<String, String>(nextTasks.length);
				for (int i = 0; i < nextTasks.length; i++) {
					if (nextRoleCode.length() > 1) {
						nextRoleCode = nextRoleCode.concat(",");
					}
					nextRole = workFlow.getUserTask(nextTaskId).getActor();
					nextRoleCode += nextRole;

					String baseRole = "";
					baseRole = StringUtils.trimToEmpty(workFlow.getUserTask(nextTasks[i]).getBaseActor());
					baseRoleMap.put(nextRole, baseRole);
				}
			}

			header.setNextRoleCode(nextRoleCode);

			baseRoleMap = null;
		}
	}

	@Override
	public Long getAccountingSetId(String eventCode, String accSetCode) {
		return this.accountingSetDAO.getAccountingSetId(eventCode, accSetCode);
	}

	@Override
	public SecurityUser getSecurityUserById(long userId, String type) {
		return this.securityUserDAO.getSecurityUserById(userId, type);
	}

	private void processFinfeeDetails(FinReceiptHeader receiptHeader) {
		logger.debug(Literal.ENTERING);

		List<FinFeeDetail> feeDetails = receiptHeader.getPaidFeeList();

		if (CollectionUtils.isEmpty(feeDetails)) {
			logger.debug(Literal.LEAVING);
			return;
		}

		if (StringUtils.isNotBlank(receiptHeader.getExtReference())) {
			for (FinFeeDetail feeDetail : feeDetails) {
				if (feeDetail.getFeeID() <= 0) {
					FinFeeReceipt feeReceipt = feeDetail.getFinFeeReceipts().get(0);
					feeDetail.setTransactionId(receiptHeader.getExtReference());
					feeDetail.setFeeID(finFeeDetailDAO.save(feeDetail, false, ""));
					feeReceipt.setFeeID(feeDetail.getFeeID());
				} else {
					finFeeDetailDAO.update(feeDetail, false, "_Temp");
				}
			}
		} else {
			for (FinFeeDetail fee : feeDetails) {
				finFeeDetailService.updateFeesFromUpfront(fee, "_Temp");

				if (AccountingEvent.VAS_FEE.equals(fee.getFinEvent())) {
					BigDecimal paidAmount = fee.getPaidAmount();

					if (BigDecimal.ZERO.compareTo(paidAmount) < 0) {
						String vasReference = fee.getVasReference();
						String finReference = fee.getFinReference();
						vASRecordingDAO.updatePaidAmt(vasReference, finReference, paidAmount, "_Temp");
					}
				}
			}
		}

		logger.debug(Literal.LEAVING);
	}

	private List<AuditDetail> processFinFeeReceipt(FinReceiptHeader receiptHeader, String type, String auditTranType) {
		logger.debug(Literal.ENTERING);
		// Building fin fee receipts
		List<FinFeeDetail> feeDetails = receiptHeader.getPaidFeeList();
		if (CollectionUtils.isEmpty(feeDetails)) {
			logger.debug(Literal.LEAVING);
			return Collections.emptyList();
		}
		// For Audit purpose;
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>(feeDetails.size());
		FinFeeReceipt finfeeRec = new FinFeeReceipt();
		String[] feeRecFilds = PennantJavaUtil.getFieldDetails(finfeeRec, finfeeRec.getExcludeFields());
		List<FinFeeReceipt> beforeFeeReceipts = null;

		if (TableType.MAIN_TAB.getSuffix().equalsIgnoreCase(type)) {
			beforeFeeReceipts = finFeeReceiptDAO.getFinFeeReceiptByReceiptId(receiptHeader.getReceiptID(),
					TableType.TEMP_TAB.getSuffix());
		}
		int count = 0;

		for (FinFeeDetail feeDetail : feeDetails) {
			FinFeeReceipt feeReceipt = feeDetail.getFinFeeReceipts().get(0);
			if (feeReceipt.getId() == Long.MIN_VALUE) {
				feeDetail.setTransactionId(receiptHeader.getExtReference());
				feeDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				feeReceipt.setReceiptID(receiptHeader.getReceiptID());
				feeReceipt.setFeeID(feeDetail.getFeeID());
				feeReceipt.setFeeTypeId(feeDetail.getFeeTypeID());
				feeReceipt.setFeeTypeDesc(feeDetail.getFeeTypeDesc());
				feeReceipt.setFeeTypeCode(feeDetail.getFeeTypeCode());
				feeReceipt.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				feeReceipt.setNewRecord(true);
			} else {
				feeReceipt.setRecordType(receiptHeader.getRecordType());
			}
			feeReceipt.setLastMntBy(receiptHeader.getLastMntBy());
			feeReceipt.setTaskId(receiptHeader.getTaskId());
			feeReceipt.setNextTaskId(receiptHeader.getNextTaskId());
			feeReceipt.setRoleCode(receiptHeader.getRoleCode());
			feeReceipt.setRecordStatus(receiptHeader.getRecordStatus());
			feeReceipt.setWorkflowId(receiptHeader.getWorkflowId());

			if (feeReceipt.isNewRecord() || StringUtils.isEmpty(feeReceipt.getRecordType())) {
				finFeeReceiptDAO.save(feeReceipt, type);
			} else if (PennantConstants.RECORD_TYPE_CAN.equalsIgnoreCase(feeReceipt.getRecordType())) {
				finFeeReceiptDAO.delete(feeReceipt, type);
			} else {
				finFeeReceiptDAO.update(feeReceipt, type);
			}
			FinFeeReceipt befImg = null;
			if (TableType.MAIN_TAB.getSuffix().equalsIgnoreCase(type)) {
				befImg = getFinFeeReceipt(beforeFeeReceipts, feeReceipt.getFeeID());
			}
			auditDetails
					.add(new AuditDetail(auditTranType, count++, feeRecFilds[0], feeRecFilds[1], befImg, feeReceipt));
		}

		if (TableType.MAIN_TAB.getSuffix().equalsIgnoreCase(type)) {
			finFeeReceiptDAO.deleteFinFeeReceiptByReceiptId(receiptHeader.getReceiptID(),
					TableType.TEMP_TAB.getSuffix());
		}
		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	private void processExcessAmount(FinReceiptHeader rch) {
		List<FinFeeDetail> feeDetails = rch.getPaidFeeList();

		if (CollectionUtils.isEmpty(feeDetails)) {
			return;
		}

		BigDecimal paidAmt = BigDecimal.ZERO;
		for (FinFeeDetail feeDetail : feeDetails) {
			FinFeeReceipt finFeeReceipts = feeDetail.getFinFeeReceipts().get(0);
			paidAmt = paidAmt.add(finFeeReceipts.getPaidAmount());
		}

		BigDecimal excessAmt = rch.getReceiptAmount().subtract(paidAmt);

		if (BigDecimal.ZERO.compareTo(excessAmt) < 0) {
			String reference = rch.getReference();

			Long finID = rch.getFinID();

			if (StringUtils.isNotBlank(rch.getExtReference())) {
				reference = rch.getExtReference();
			}

			FinExcessAmount excess = new FinExcessAmount();

			excess.setFinID(finID);
			excess.setFinReference(reference);
			excess.setAmountType(rch.getExcessAdjustTo());
			excess.setAmount(excessAmt);
			excess.setUtilisedAmt(BigDecimal.ZERO);
			excess.setBalanceAmt(excessAmt);
			excess.setReservedAmt(BigDecimal.ZERO);
			excess.setReceiptID(rch.getReceiptID());
			excess.setValueDate(rch.getValueDate());
			excess.setPostDate(SysParamUtil.getAppDate());

			finExcessAmountDAO.saveExcess(excess);

			FinExcessMovement excessMovement = new FinExcessMovement();
			excessMovement.setExcessID(excess.getExcessID());
			excessMovement.setAmount(excessAmt);
			excessMovement.setReceiptID(rch.getReceiptID());
			excessMovement.setMovementType(RepayConstants.RECEIPTTYPE_RECIPT);
			excessMovement.setTranType(AccountConstants.TRANTYPE_CREDIT);
			excessMovement.setMovementFrom("UPFRONT");

			finExcessAmountDAO.saveExcessMovement(excessMovement);
		}
	}

	private FinFeeReceipt getFinFeeReceipt(List<FinFeeReceipt> beforeFeeReceipts, long feeID) {
		if (CollectionUtils.isEmpty(beforeFeeReceipts)) {
			return null;
		}
		for (FinFeeReceipt finFeeReceipt : beforeFeeReceipts) {
			if (finFeeReceipt.getFeeID() == feeID) {
				return finFeeReceipt;
			}
		}
		return null;
	}

	/**
	 * Method for calculate GST for Details for the given allocated amount.
	 * 
	 * @param receiptHeader
	 */
	@Override
	public void calculateGST(List<FinFeeDetail> finFeeDetails, Map<String, BigDecimal> taxPercentages,
			boolean isApprove) {

		logger.debug(Literal.ENTERING);
		if (CollectionUtils.isEmpty(finFeeDetails)) {
			return;
		}
		for (FinFeeDetail finFeeDetail : finFeeDetails) {
			FinFeeReceipt finFeeReceipt = finFeeDetail.getFinFeeReceipts().get(0);
			BigDecimal paidAmt = finFeeReceipt.getPaidAmount();
			BigDecimal paidTds = finFeeReceipt.getPaidTds();
			FinFeeDetail tempfinFee = new FinFeeDetail();
			BeanUtils.copyProperties(finFeeDetail, tempfinFee);
			if (finFeeDetail.getBefImage() == null) {
				finFeeDetail.setBefImage(tempfinFee);
			}
			finFeeDetail.setPaidCalcReq(true);

			// In case of approve need to calculate complete paid amount GST because it is updating on fees
			if (isApprove) {
				paidAmt = paidAmt.add(finFeeDetail.getPaidAmount());
				paidTds = paidTds.add(finFeeDetail.getPaidTDS());
			}

			finFeeDetail.setPaidAmount(paidAmt);
			finFeeDetail.setPaidAmountOriginal(paidAmt);
			finFeeDetail.setPaidTDS(paidTds);
			FinanceMain financeMain = null;
			String taxComponent = finFeeDetail.getTaxComponent();
			finFeeDetail.setPrvTaxComponent(taxComponent);
			finFeeDetail.setTaxComponent(FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE);
			finFeeDetail.setUpfrontFee(true);
			calculateFees(finFeeDetail, financeMain, taxPercentages);
			finFeeDetail.setTaxComponent(taxComponent);
		}
		logger.debug(Literal.LEAVING);
	}

	private void processGSTInvoicePreparation(long linkedTranId, FinanceMain financeMain,
			List<FinFeeDetail> finFeeDetailsList, Map<String, BigDecimal> taxPercentages) {
		logger.debug(Literal.ENTERING);

		calculateGST(finFeeDetailsList, taxPercentages, false);

		FinanceDetail financeDetail = new FinanceDetail();

		financeDetail.getFinScheduleData().setFinanceMain(financeMain);
		financeDetail.setCustomerDetails(null);
		financeDetail.setFinanceTaxDetail(null);

		InvoiceDetail invoiceDetail = new InvoiceDetail();
		invoiceDetail.setLinkedTranId(linkedTranId);
		invoiceDetail.setFinanceDetail(financeDetail);
		invoiceDetail.setFinFeeDetailsList(finFeeDetailsList);
		setRemTaxes(finFeeDetailsList, taxPercentages);
		invoiceDetail.setInvoiceType(PennantConstants.GST_INVOICE_TRANSACTION_TYPE_DEBIT);
		invoiceDetail.setOrigination(false);
		invoiceDetail.setWaiver(false);
		invoiceDetail.setDbInvSetReq(false);

		Long dueInvoiceID = this.gstInvoiceTxnService.feeTaxInvoicePreparation(invoiceDetail);

		for (FinFeeDetail finFeeDetail : finFeeDetailsList) {
			TaxHeader taxHeader = finFeeDetail.getTaxHeader();
			if (taxHeader != null && finFeeDetail.getPaidAmount().compareTo(BigDecimal.ZERO) > 0) {
				if (dueInvoiceID == null) {
					dueInvoiceID = finFeeDetail.getTaxHeader().getInvoiceID();
				}
				taxHeader.setInvoiceID(dueInvoiceID);
				this.taxHeaderDetailsDAO.update(taxHeader, "_Temp");
			}
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void calculateFees(FinFeeDetail finFeeDetail, FinanceMain financeMain,
			Map<String, BigDecimal> taxPercentages) {
		finFeeDetailService.calculateFees(finFeeDetail, financeMain, taxPercentages);

	}

	private ErrorDetail validateUpFrontFees(FinServiceInstruction fsi, List<FinFeeDetail> paidFeeList,
			LoggedInUser userDetails) {
		logger.debug(Literal.ENTERING);

		List<String> processedFees = new ArrayList<>(fsi.getFinFeeDetails().size());

		// in case of PaidFee's contains vas
		List<VASRecording> vasRecordingList = new ArrayList<>(1);
		for (FinFeeDetail finFeeDetail : paidFeeList) {
			if (AccountingEvent.VAS_FEE.equals(finFeeDetail.getFinEvent())) {
				VASRecording vasRecording = null;
				vasRecording = vASRecordingDAO.getVASRecordingByReference(finFeeDetail.getVasReference(), "_Temp");
				if (vasRecording != null) {
					vasRecordingList.add(vasRecording);
				}
			}
		}

		String tdsRoundMode = SysParamUtil.getValue(CalculationConstants.TDS_ROUNDINGMODE).toString();
		int tdsRoundingTarget = SysParamUtil.getValueAsInt(CalculationConstants.TDS_ROUNDINGTARGET);

		BigDecimal totalFeePaid = BigDecimal.ZERO;
		for (FinFeeDetail upFinFeeDetail : fsi.getFinFeeDetails()) {
			String feeCode = StringUtils.trimToEmpty(upFinFeeDetail.getFeeTypeCode());

			// In case of req contain duplicate fees.
			if (processedFees.contains(feeCode.toLowerCase())) {
				String[] valueParm = new String[1];
				valueParm[0] = "Fees : " + feeCode;
				return ErrorUtil.getErrorDetail(new ErrorDetail("90273", valueParm));
			}
			processedFees.add(feeCode.toLowerCase());
			FinFeeDetail fee = getFeeByFeeTypeCode(feeCode, paidFeeList, vasRecordingList);
			// In case of Invalid fee code
			if (fee == null) {
				String[] valueParm = new String[1];
				valueParm[0] = feeCode;
				return ErrorUtil.getErrorDetail(new ErrorDetail("90206", valueParm));
			}
			// PSD #157162 IMD API issue> system is deducting waiver amount from remaining fee amount and not allowing
			// to make the payment of complete remaining amount
			BigDecimal amount = fee.getRemainingFee().add(fee.getWaivedAmount());
			BigDecimal diffTDS = BigDecimal.ZERO;
			BigDecimal tdsPaid = BigDecimal.ZERO;

			if (upFinFeeDetail.getPaidAmount() == null) {
				upFinFeeDetail.setPaidAmount(BigDecimal.ZERO);
			}
			if (upFinFeeDetail.getPaidAmount().compareTo(amount) > 0) {
				String[] valueParm = new String[2];
				valueParm[0] = feeCode;
				valueParm[1] = PennantApplicationUtil.amountFormate(amount, 2);
				return ErrorUtil.getErrorDetail(new ErrorDetail("IMD003", valueParm));
			}

			BigDecimal allocatedAmt = upFinFeeDetail.getPaidAmount();
			totalFeePaid = totalFeePaid.add(allocatedAmt);
			FinFeeReceipt feeReceipt = fee.getFinFeeReceipts().get(0);

			BigDecimal remTDS = fee.getNetTDS();

			if (fee.isTdsReq() && fee.getRemTDS().compareTo(BigDecimal.ZERO) > 0) {
				tdsPaid = (remTDS.multiply(allocatedAmt)).divide(fee.getNetAmountOriginal(), 2, RoundingMode.HALF_DOWN);
				tdsPaid = CalculationUtil.roundAmount(tdsPaid, tdsRoundMode, tdsRoundingTarget);
			}

			if (allocatedAmt.compareTo(fee.getRemainingFee()) == 0) {
				diffTDS = fee.getRemTDS().subtract(tdsPaid);
			}

			tdsPaid = tdsPaid.add(diffTDS);
			feeReceipt.setPaidAmount(allocatedAmt);
			feeReceipt.setPaidTds(tdsPaid);
		}

		if (BigDecimal.ZERO.compareTo(totalFeePaid) == 0) {
			String valueParm[] = new String[2];
			valueParm[0] = "total fees paid";
			valueParm[1] = "ZERO";
			return ErrorUtil.getErrorDetail(new ErrorDetail("90205", valueParm), userDetails.getLanguage());
		}

		if (fsi.getAmount().compareTo(totalFeePaid) < 0) {
			String valueParm[] = new String[2];
			valueParm[0] = "amount : " + fsi.getAmount();
			valueParm[1] = "total fees paid : " + totalFeePaid;
			return ErrorUtil.getErrorDetail(new ErrorDetail("90205", valueParm), userDetails.getLanguage());
		}
		return null;
	}

	private FinFeeDetail getFeeByFeeTypeCode(String feeCode, List<FinFeeDetail> finfeeDetailList,
			List<VASRecording> vasRecordingList) {
		// In case of VAS
		if (StringUtils.contains(feeCode, "{")) {
			for (VASRecording vasRecording : vasRecordingList) {
				if (StringUtils.equals(feeCode, "{" + vasRecording.getProductCode() + "}")) {
					for (FinFeeDetail finFeeDetail : finfeeDetailList) {
						if (StringUtils.equalsIgnoreCase(finFeeDetail.getVasReference(),
								vasRecording.getVasReference())) {
							return finFeeDetail;
						}
					}
				}
			}
			return null;
		}

		// In case of normal Fee
		for (FinFeeDetail finFeeDetail : finfeeDetailList) {
			if (StringUtils.equalsIgnoreCase(feeCode, finFeeDetail.getFeeTypeCode())) {
				return finFeeDetail;
			}
		}
		return null;
	}

	private void setRemTaxes(List<FinFeeDetail> finFeeDetailsList, Map<String, BigDecimal> taxPercentages) {
		for (FinFeeDetail finFeeDetail : finFeeDetailsList) {
			if (finFeeDetail.getBefImage().getRemainingFee().compareTo(finFeeDetail.getPaidAmount()) == 0
					&& finFeeDetail.getTaxHeaderId() != null && (finFeeDetail.getBefImage().getRemainingFeeGST()
							.compareTo(finFeeDetail.getPaidAmountGST()) != 0)) {
				BigDecimal totalNetFee = finFeeDetail.getActualAmountOriginal()
						.subtract(finFeeDetail.getWaivedAmount());
				totalNetFee = totalNetFee.add(finFeeDetail.getNetAmountGST());
				TaxAmountSplit netTaxSplit = GSTCalculator.getInclusiveGST(totalNetFee, taxPercentages);
				BigDecimal TotalPaid = finFeeDetail.getBefImage().getPaidAmount()
						.add(finFeeDetail.getBefImage().getPaidTDS());
				TaxAmountSplit paidTaxSplit = GSTCalculator.getInclusiveGST(TotalPaid, taxPercentages);
				List<Taxes> taxDetails = finFeeDetail.getTaxHeader().getTaxDetails();
				Taxes cgstTax = null;
				Taxes sgstTax = null;
				Taxes igstTax = null;
				Taxes ugstTax = null;
				Taxes cessTax = null;

				if (CollectionUtils.isNotEmpty(taxDetails)) {
					for (Taxes taxes : taxDetails) {
						String taxType = taxes.getTaxType();
						switch (taxType) {
						case RuleConstants.CODE_CGST:
							cgstTax = taxes;
							cgstTax.setRemFeeTax(netTaxSplit.getcGST().subtract(paidTaxSplit.getcGST()));
							break;
						case RuleConstants.CODE_SGST:
							sgstTax = taxes;
							sgstTax.setRemFeeTax(netTaxSplit.getsGST().subtract(paidTaxSplit.getsGST()));
							break;
						case RuleConstants.CODE_IGST:
							igstTax = taxes;
							igstTax.setRemFeeTax(netTaxSplit.getiGST().subtract(paidTaxSplit.getiGST()));
							break;
						case RuleConstants.CODE_UGST:
							ugstTax = taxes;
							ugstTax.setRemFeeTax(netTaxSplit.getuGST().subtract(paidTaxSplit.getuGST()));
							break;
						case RuleConstants.CODE_CESS:
							cessTax = taxes;
							cessTax.setRemFeeTax(netTaxSplit.getCess().subtract(paidTaxSplit.getCess()));
							break;
						default:
							break;
						}

					}
				}
			}
		}
	}

	/**
	 * Get Audit Header Details
	 */
	private AuditHeader getAuditHeader(FinReceiptHeader receiptHeader, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, null, receiptHeader);
		return new AuditHeader(String.valueOf(receiptHeader.getReceiptID()), null, null, null, auditDetail,
				receiptHeader.getUserDetails(), new HashMap<String, List<ErrorDetail>>());
	}

	public void setFinReceiptHeaderDAO(FinReceiptHeaderDAO finReceiptHeaderDAO) {
		this.finReceiptHeaderDAO = finReceiptHeaderDAO;
	}

	public void setFinReceiptDetailDAO(FinReceiptDetailDAO finReceiptDetailDAO) {
		this.finReceiptDetailDAO = finReceiptDetailDAO;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public void setFinFeeDetailDAO(FinFeeDetailDAO finFeeDetailDAO) {
		this.finFeeDetailDAO = finFeeDetailDAO;
	}

	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}

	public void setFinanceRepaymentsDAO(FinanceRepaymentsDAO financeRepaymentsDAO) {
		this.financeRepaymentsDAO = financeRepaymentsDAO;
	}

	public void setAccountingSetDAO(AccountingSetDAO accountingSetDAO) {
		this.accountingSetDAO = accountingSetDAO;
	}

	public void setFinFeeReceiptDAO(FinFeeReceiptDAO finFeeReceiptDAO) {
		this.finFeeReceiptDAO = finFeeReceiptDAO;
	}

	@Override
	public Map<String, Object> getGLSubHeadCodes(long finID) {
		return financeMainDAO.getGLSubHeadCodes(finID);
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public void setSecurityUserDAO(SecurityUserDAO securityUserDAO) {
		this.securityUserDAO = securityUserDAO;
	}

	public void setFinExcessAmountDAO(FinExcessAmountDAO finExcessAmountDAO) {
		this.finExcessAmountDAO = finExcessAmountDAO;
	}

	public void setTaxHeaderDetailsDAO(TaxHeaderDetailsDAO taxHeaderDetailsDAO) {
		this.taxHeaderDetailsDAO = taxHeaderDetailsDAO;
	}

	public void setFinFeeDetailService(FinFeeDetailService finFeeDetailService) {
		this.finFeeDetailService = finFeeDetailService;
	}

	public void setGstInvoiceTxnService(GSTInvoiceTxnService gstInvoiceTxnService) {
		this.gstInvoiceTxnService = gstInvoiceTxnService;
	}

	public void setvASRecordingDAO(VASRecordingDAO vASRecordingDAO) {
		this.vASRecordingDAO = vASRecordingDAO;
	}

	public void setExtendedFieldDetailsService(ExtendedFieldDetailsService extendedFieldDetailsService) {
		this.extendedFieldDetailsService = extendedFieldDetailsService;
	}

	public void setExtendedFieldExtensionService(ExtendedFieldExtensionService extendedFieldExtensionService) {
		this.extendedFieldExtensionService = extendedFieldExtensionService;
	}

	public void setFinanceTypeDAO(FinanceTypeDAO financeTypeDAO) {
		this.financeTypeDAO = financeTypeDAO;
	}

	public VASConfigurationDAO getvASConfigurationDAO() {
		return vASConfigurationDAO;
	}

	public void setvASConfigurationDAO(VASConfigurationDAO vASConfigurationDAO) {
		this.vASConfigurationDAO = vASConfigurationDAO;
	}

}
