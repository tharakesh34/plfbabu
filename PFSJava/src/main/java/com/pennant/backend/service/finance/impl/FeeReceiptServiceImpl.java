package com.pennant.backend.service.finance.impl;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.security.auth.login.AccountNotFoundException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.constants.AccountConstants;
import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.GSTCalculator;
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.app.util.SessionUserDetails;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.Repayments.FinanceRepaymentsDAO;
import com.pennant.backend.dao.administration.SecurityUserDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.configuration.VASRecordingDAO;
import com.pennant.backend.dao.finance.FinFeeDetailDAO;
import com.pennant.backend.dao.finance.FinFeeReceiptDAO;
import com.pennant.backend.dao.finance.FinServiceInstrutionDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.TaxHeaderDetailsDAO;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.dao.receipts.FinReceiptDetailDAO;
import com.pennant.backend.dao.receipts.FinReceiptHeaderDAO;
import com.pennant.backend.dao.rmtmasters.AccountingSetDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinExcessMovement;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinFeeReceipt;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinRepayHeader;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.InvoiceDetail;
import com.pennant.backend.model.finance.TaxHeader;
import com.pennant.backend.model.finance.Taxes;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.FeeReceiptService;
import com.pennant.backend.service.finance.FinFeeDetailService;
import com.pennant.backend.service.finance.GSTInvoiceTxnService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.cache.util.AccountingConfigCache;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.engine.workflow.WorkflowEngine;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.rits.cloning.Cloner;

public class FeeReceiptServiceImpl extends GenericService<FinReceiptHeader> implements FeeReceiptService {
	private static final Logger logger = Logger.getLogger(FeeReceiptServiceImpl.class);

	private FinReceiptHeaderDAO finReceiptHeaderDAO;
	private FinReceiptDetailDAO finReceiptDetailDAO;
	private FinFeeDetailDAO finFeeDetailDAO;
	private PostingsPreparationUtil postingsPreparationUtil;
	private FinanceRepaymentsDAO financeRepaymentsDAO;
	private AccountingSetDAO accountingSetDAO;
	private AuditHeaderDAO auditHeaderDAO;
	private FinFeeReceiptDAO finFeeReceiptDAO;
	protected transient WorkflowEngine workFlow = null;
	private FinServiceInstrutionDAO finServiceInstructionDAO;
	private FinanceMainDAO financeMainDAO;
	private SecurityUserDAO securityUserDAO;
	private FinExcessAmountDAO finExcessAmountDAO;
	private TaxHeaderDetailsDAO taxHeaderDetailsDAO;
	private FinFeeDetailService finFeeDetailService;
	private GSTInvoiceTxnService gstInvoiceTxnService;
	private VASRecordingDAO vASRecordingDAO;

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
		FinReceiptHeader receiptHeader = finReceiptHeaderDAO.getReceiptHeaderByID(receiptID, "_FView");

		if (receiptHeader == null) {
			return receiptHeader;
		}

		// Fetch Receipt Detail List
		List<FinReceiptDetail> receiptDetailList = finReceiptDetailDAO.getReceiptHeaderByID(receiptID, "_TView");

		// Fetch Repay Headers List
		String reference = receiptHeader.getReference();
		List<FinRepayHeader> rpyHeaderList = financeRepaymentsDAO.getFinRepayHeadersByRef(reference,
				TableType.TEMP_TAB.getSuffix());

		for (FinReceiptDetail receiptDetail : receiptDetailList) {
			for (FinRepayHeader finRepayHeader : rpyHeaderList) {
				if (finRepayHeader.getReceiptSeqID() == receiptDetail.getReceiptSeqID()) {
					receiptDetail.getRepayHeaders().add(finRepayHeader);
				}
			}
		}
		receiptHeader.setReceiptDetails(receiptDetailList);

		// Paid Fee Details
		reference = receiptHeader.getExtReference();
		if (StringUtils.isBlank(reference)) {
			reference = receiptHeader.getReference();
		}
		receiptHeader.setPaidFeeList(getPaidFinFeeDetails(reference, receiptHeader.getReceiptID(), "_TView"));

		logger.debug(Literal.LEAVING);
		return receiptHeader;
	}

	/**
	 * Method for Fetching List of Fee Details for Display purpose
	 */
	@Override
	public List<FinFeeDetail> getPaidFinFeeDetails(String finReference, long receiptID, String type) {
		List<FinFeeDetail> finFeeDetails = finFeeDetailDAO.getPaidFinFeeDetails(finReference, type);

		if (CollectionUtils.isEmpty(finFeeDetails)) {
			return finFeeDetails;
		}

		List<FinFeeReceipt> currFinfeereceipts = finFeeReceiptDAO.getFinFeeReceiptByReceiptId(receiptID, type);

		// Finance Fee Schedule Details
		for (FinFeeDetail finFeeDetail : finFeeDetails) {

			// Finance Fee Schedule Details

			finFeeDetail.getFinFeeReceipts().add(getFinFeeReceiptbyFeeID(currFinfeereceipts, finFeeDetail.getFeeID()));

			Long taxHeaderId = finFeeDetail.getTaxHeaderId();
			if (taxHeaderId == null || taxHeaderId <= 0) {
				continue;
			}

			finFeeDetail.setTaxHeader(taxHeaderDetailsDAO.getTaxHeaderDetailsById(taxHeaderId, type));
			if (finFeeDetail.getTaxHeader() != null) {
				finFeeDetail.getTaxHeader().setTaxDetails(taxHeaderDetailsDAO.getTaxDetailById(taxHeaderId, type));
			}
		}

		return finFeeDetails;
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
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 * @throws AccountNotFoundException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader aAuditHeader)
			throws InterfaceException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		aAuditHeader = businessValidation(aAuditHeader, "saveOrUpdate");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		if (!aAuditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return aAuditHeader;
		}

		Cloner cloner = new Cloner();
		AuditHeader auditHeader = cloner.deepClone(aAuditHeader);
		FinReceiptHeader receiptHeader = (FinReceiptHeader) auditHeader.getAuditDetail().getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (receiptHeader.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		// Receipt Header Details Save And Update
		//=======================================
		long receiptID = receiptHeader.getReceiptID();
		if (receiptHeader.isNew()) {
			receiptHeader.setReceiptModeStatus(RepayConstants.PAYSTATUS_FEES);

			// Save Receipt Header
			receiptID = finReceiptHeaderDAO.save(receiptHeader, tableType);

		} else {
			finReceiptHeaderDAO.update(receiptHeader, tableType);

			// Delete Save Receipt Detail List by Reference
			finReceiptDetailDAO.deleteByReceiptID(receiptID, tableType);

			// Delete and Save FinRepayHeader Detail list by Reference
			financeRepaymentsDAO.deleteByRef(receiptHeader.getReference(), tableType);
		}

		Date appDate = SysParamUtil.getAppDate();
		FinServiceInstruction finServInst = new FinServiceInstruction();
		finServInst.setFinReference(receiptHeader.getReference());
		finServInst.setFinEvent(AccountEventConstants.ACCEVENT_FEEPAY);
		finServInst.setAppDate(appDate);
		finServInst.setAmount(receiptHeader.getReceiptAmount());
		finServInst.setSystemDate(DateUtility.getSysDate());
		finServInst.setMaker(auditHeader.getAuditUsrId());
		finServInst.setMakerAppDate(appDate);
		finServInst.setMakerSysDate(DateUtility.getSysDate());
		finServInst.setReference(String.valueOf(receiptHeader.getReceiptID()));
		finServiceInstructionDAO.save(finServInst, tableType.getSuffix());

		for (FinReceiptDetail receiptDetail : receiptHeader.getReceiptDetails()) {
			receiptDetail.setReceiptID(receiptID);
			long receiptSeqID = finReceiptDetailDAO.save(receiptDetail, tableType);

			List<FinRepayHeader> rpyHeaderList = receiptDetail.getRepayHeaders();
			for (FinRepayHeader rpyHeader : rpyHeaderList) {
				rpyHeader.setReceiptSeqID(receiptSeqID);

				//Save Repay Header details
				financeRepaymentsDAO.saveFinRepayHeader(rpyHeader, tableType);
			}
		}

		//Processing FinFeeReceipt
		List<AuditDetail> auditdetails = processFinFeeReceipt(receiptHeader, tableType.getSuffix(),
				auditHeader.getAuditTranType());
		auditDetails.addAll(auditdetails);

		//Audit
		String[] recHeaderFields = PennantJavaUtil.getFieldDetails(new FinReceiptHeader(),
				receiptHeader.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, recHeaderFields[0],
				recHeaderFields[1], receiptHeader.getBefImage(), receiptHeader));

		FinReceiptDetail detail = new FinReceiptDetail();
		String[] fields = PennantJavaUtil.getFieldDetails(detail, detail.getExcludeFields());
		for (int i = 0; i < receiptHeader.getReceiptDetails().size(); i++) {
			auditDetails.add(new AuditDetail(auditHeader.getAuditTranType(), i, fields[0], fields[1], null,
					receiptHeader.getReceiptDetails().get(i)));
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
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 * @throws InterfaceException
	 */
	@Override
	public AuditHeader doReject(AuditHeader auditHeader) throws InterfaceException {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		FinReceiptHeader receiptHeader = (FinReceiptHeader) auditHeader.getAuditDetail().getModelData();

		// Delete Receipt Header
		if (RepayConstants.RECEIPTTO_CUSTOMER.equals(receiptHeader.getRecAgainst())) {
			finFeeDetailDAO.deleteByTransactionId(receiptHeader.getExtReference(), false,
					TableType.TEMP_TAB.getSuffix());
			if (CollectionUtils.isNotEmpty(receiptHeader.getPaidFeeList())) {
				//Removing the details from temp while Approve
				for (FinFeeDetail finFeeDetail : receiptHeader.getPaidFeeList()) {
					//finTaxDetailsDAO.deleteByFeeID(finFeeDetail.getFeeID(), TableType.TEMP_TAB.getSuffix());
					//FIXME Murthy
				}
			}
		}
		financeRepaymentsDAO.deleteByRef(receiptHeader.getReference(), TableType.TEMP_TAB);
		finReceiptDetailDAO.deleteByReceiptID(receiptHeader.getReceiptID(), TableType.TEMP_TAB);
		receiptHeader.setRecordType(PennantConstants.RECORD_TYPE_CAN);
		if (RepayConstants.RECEIPTTO_CUSTOMER.equals(receiptHeader.getRecAgainst())) {
			finFeeDetailDAO.deleteByTransactionId(receiptHeader.getExtReference(), false,
					TableType.TEMP_TAB.getSuffix());
		}

		List<AuditDetail> adtdetaisl = processFinFeeReceipt(receiptHeader, TableType.TEMP_TAB.getSuffix(),
				auditHeader.getAuditTranType());
		finReceiptHeaderDAO.deleteByReceiptID(receiptHeader.getReceiptID(), TableType.TEMP_TAB);
		//finServiceInstructionDAO.deleteFinServInstList(receiptHeader.getReference(), TableType.TEMP_TAB.getSuffix(), String.valueOf(receiptHeader.getReceiptID()));

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		String[] fields = PennantJavaUtil.getFieldDetails(new FinReceiptHeader(), receiptHeader.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
				receiptHeader.getBefImage(), receiptHeader));
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
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 * @throws AccountNotFoundException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	@Override
	public AuditHeader doApprove(AuditHeader aAuditHeader)
			throws InterfaceException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		String tranType = "";
		aAuditHeader = businessValidation(aAuditHeader, "doApprove");
		List<AuditDetail> auditDetails = new ArrayList<>();
		if (!aAuditHeader.isNextProcess()) {
			return aAuditHeader;
		}

		Cloner cloner = new Cloner();
		AuditHeader auditHeader = cloner.deepClone(aAuditHeader);
		FinReceiptHeader receiptHeader = (FinReceiptHeader) auditHeader.getAuditDetail().getModelData();

		// Accounting Process Execution
		AEEvent aeEvent = new AEEvent();
		aeEvent.setAccountingEvent(AccountEventConstants.ACCEVENT_FEEPAY);
		aeEvent.setFinReference(receiptHeader.getReference());
		if (StringUtils.isNotBlank(receiptHeader.getExtReference())) {
			aeEvent.setFinReference(receiptHeader.getExtReference());
		}
		aeEvent.setCustCIF(receiptHeader.getCustCIF());
		aeEvent.setCustID(receiptHeader.getCustID());
		aeEvent.setBranch(receiptHeader.getPostBranch());
		aeEvent.setCcy(receiptHeader.getFinCcy());
		aeEvent.setPostingUserBranch(receiptHeader.getPostBranch());

		//Setting value date from receipt header for backdated receipt 
		//aeEvent.setValueDate(DateUtility.getAppDate());
		aeEvent.setValueDate(receiptHeader.getValueDate());

		if (aeEvent.getCcy() == null) {
			aeEvent.setCcy(receiptHeader.getCustBaseCcy());
		}

		if (aeEvent.getCcy() == null) {
			aeEvent.setCcy(SysParamUtil.getAppCurrency());
		}

		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
		if (amountCodes == null) {
			amountCodes = new AEAmountCodes();
		}

		// Fetch Accounting Set ID
		long accountingSetID = 0;
		Map<String, Object> map = null;

		String recAgainst2 = receiptHeader.getRecAgainst();
		String recAgainst = recAgainst2;
		if (RepayConstants.RECEIPTTO_FINANCE.equals(recAgainst) || (RepayConstants.RECEIPTTO_CUSTOMER.equals(recAgainst)
				&& StringUtils.isNotBlank(receiptHeader.getExtReference()))) {
			accountingSetID = AccountingConfigCache.getAccountSetID(receiptHeader.getFinType(),
					AccountEventConstants.ACCEVENT_FEEPAY, FinanceConstants.MODULEID_FINTYPE);
			map = financeMainDAO.getGLSubHeadCodes(receiptHeader.getReference());
		} else {
			accountingSetID = accountingSetDAO.getAccountingSetId(AccountEventConstants.ACCEVENT_FEEPAY,
					AccountEventConstants.ACCEVENT_FEEPAY);
		}
		if (accountingSetID == 0 || accountingSetID == Long.MIN_VALUE) {
			auditHeader.setErrorDetails(
					ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "65015", null, null)));
			logger.debug("Leaving");
			return auditHeader;
		}

		FinReceiptDetail finreceiptDetail = receiptHeader.getReceiptDetails().get(0);
		amountCodes.setPartnerBankAc(finreceiptDetail.getPartnerBankAc());
		amountCodes.setPaymentType(finreceiptDetail.getPaymentType());
		amountCodes.setPartnerBankAcType(finreceiptDetail.getPartnerBankAcType());
		amountCodes.setPaidFee(finreceiptDetail.getAmount());
		amountCodes.setFinType(receiptHeader.getFinType());
		Map<String, Object> dataMap = amountCodes.getDeclaredFieldValues();

		if (map != null) {
			BigDecimal alwFlexi = BigDecimal.ZERO;
			if (map.get("AlwFlexi") instanceof Long) {
				long value = (long) map.get("AlwFlexi");
				amountCodes.setAlwflexi(value == 0 ? false : true);
			} else if (map.get("AlwFlexi") instanceof BigDecimal) {
				alwFlexi = (BigDecimal) map.get("AlwFlexi");
				amountCodes.setAlwflexi(alwFlexi.compareTo(BigDecimal.ZERO) == 0 ? false : true);
			} else if (map.get("AlwFlexi") instanceof Integer) {
				int value = (int) map.get("AlwFlexi");
				amountCodes.setAlwflexi(value == 0 ? false : true);
			}

			amountCodes.setEntitycode((String) map.get("Entitycode"));
			dataMap.put("ae_finbranch", map.get("FinBranch"));
			dataMap.put("emptype", map.get("emptype"));
			dataMap.put("branchcity", map.get("branchcity"));
			dataMap.put("fincollateralreq", map.get("fincollateralreq"));
			dataMap.put("btloan", map.get("btloan"));

		} else {
			amountCodes.setEntitycode(receiptHeader.getEntityCode());
			amountCodes.setFinbranch(receiptHeader.getPostBranch());
		}

		aeEvent.setDataMap(dataMap);
		aeEvent.setPostRefId(receiptHeader.getReceiptID());

		aeEvent.getAcSetIDList().add(accountingSetID);

		// GST parameters
		Map<String, Object> gstExecutionMap = GSTCalculator.getGSTDataMap(receiptHeader.getReference());

		if (gstExecutionMap != null) {
			for (String mapkey : gstExecutionMap.keySet()) {
				if (StringUtils.isNotBlank(mapkey)) {
					aeEvent.getDataMap().put(mapkey, gstExecutionMap.get(mapkey));
				}
			}
		}
		prepareFeeRulesMap(receiptHeader, aeEvent.getDataMap());

		aeEvent = postingsPreparationUtil.postAccounting(aeEvent);

		// Receipt Header Updation
		//=======================================
		tranType = PennantConstants.TRAN_UPD;
		receiptHeader.setReceiptModeStatus(RepayConstants.PAYSTATUS_FEES);
		receiptHeader.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		receiptHeader.setRecordType("");
		receiptHeader.setRoleCode("");
		receiptHeader.setNextRoleCode("");
		receiptHeader.setTaskId("");
		receiptHeader.setNextTaskId("");
		receiptHeader.setWorkflowId(0);
		receiptHeader.setExcessAdjustTo(RepayConstants.EXAMOUNTTYPE_EXCESS);
		finReceiptHeaderDAO.save(receiptHeader, TableType.MAIN_TAB);

		// Save Receipt Header
		for (FinReceiptDetail receiptDetail : receiptHeader.getReceiptDetails()) {
			receiptDetail.setStatus(RepayConstants.PAYSTATUS_APPROVED);

			long receiptSeqID = finReceiptDetailDAO.save(receiptDetail, TableType.MAIN_TAB);

			List<FinRepayHeader> rpyHeaderList = receiptDetail.getRepayHeaders();
			for (FinRepayHeader rpyHeader : rpyHeaderList) {
				rpyHeader.setReceiptSeqID(receiptSeqID);
				rpyHeader.setLinkedTranId(aeEvent.getLinkedTranId());

				//Save Repay Header details
				financeRepaymentsDAO.saveFinRepayHeader(rpyHeader, TableType.MAIN_TAB);
			}
		}

		if (RepayConstants.RECEIPTTO_FINANCE.equals(recAgainst) || (RepayConstants.RECEIPTTO_CUSTOMER.equals(recAgainst)
				&& StringUtils.isNotBlank(receiptHeader.getExtReference()))) {
			Map<String, BigDecimal> taxPercentages = null;
			taxPercentages = GSTCalculator.getTaxPercentages(receiptHeader.getCustID(), receiptHeader.getFinCcy(),
					receiptHeader.getUserDetails().getBranchCode(), receiptHeader.getFinBranch());
			List<FinFeeDetail> feeList = receiptHeader.getPaidFeeList();
			calculateGST(receiptHeader.getPaidFeeList(), taxPercentages, true);

			// External Reference Process
			processFinfeeDetails(receiptHeader);

			for (FinFeeDetail finFeeDetail : feeList) {
				List<Taxes> taxDetails = finFeeDetail.getTaxHeader().getTaxDetails();
				for (Taxes taxes : taxDetails) {
					taxHeaderDetailsDAO.update(taxes, "_Temp");
				}
			}

			if (CollectionUtils.isNotEmpty(feeList)) {
				FinanceMain financeMain = null;
				if (StringUtils.isBlank(receiptHeader.getExtReference())) {
					financeMain = financeMainDAO.getFinanceMainById(receiptHeader.getReference(), "_Temp", false);
					FinanceMain befImage = new FinanceMain();
					BeanUtils.copyProperties(financeMain, befImage);
					financeMain.setBefImage(befImage);
					financeMain.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					BigDecimal totFeePaid = BigDecimal.ZERO;
					for (FinFeeDetail finFeeDetail : feeList) {
						FinFeeReceipt finFeeReceipt = finFeeDetail.getFinFeeReceipts().get(0);
						totFeePaid = totFeePaid.add(finFeeReceipt.getPaidAmount());
					}
					financeMain.setDeductFeeDisb(financeMain.getDeductFeeDisb().subtract(totFeePaid));
					financeMainDAO.updateDeductFeeDisb(financeMain, TableType.TEMP_TAB);
				} else {
					financeMain = new FinanceMain();
					financeMain.setFinType(receiptHeader.getFinType());
					financeMain.setFinBranch(receiptHeader.getFinBranch());
					financeMain.setCustID(receiptHeader.getCustID());
					financeMain.setFinReference(receiptHeader.getExtReference());
				}
				processGSTInvoicePreparation(aeEvent.getLinkedTranId(), financeMain, feeList, taxPercentages);
			}
		}

		// Normal Process
		List<AuditDetail> adtdetails = processFinFeeReceipt(receiptHeader, TableType.MAIN_TAB.getSuffix(), tranType);
		auditDetails.addAll(adtdetails);
		processExcessAmount(receiptHeader);

		// Delete Receipt Header
		if (RepayConstants.RECEIPTTO_CUSTOMER.equals(receiptHeader.getRecAgainst())) {
			finFeeDetailDAO.deleteByTransactionId(receiptHeader.getExtReference(), false,
					TableType.TEMP_TAB.getSuffix());
			if (CollectionUtils.isNotEmpty(receiptHeader.getPaidFeeList())) {
				//Removing the details from temp while Approve
				for (FinFeeDetail finFeeDetail : receiptHeader.getPaidFeeList()) {
					// finTaxDetailsDAO.deleteByFeeID(finFeeDetail.getFeeID(), TableType.TEMP_TAB.getSuffix());
					// FIXME MURTHY
				}
			}
		}
		financeRepaymentsDAO.deleteByRef(receiptHeader.getReference(), TableType.TEMP_TAB);
		finReceiptDetailDAO.deleteByReceiptID(receiptHeader.getReceiptID(), TableType.TEMP_TAB);
		finReceiptHeaderDAO.deleteByReceiptID(receiptHeader.getReceiptID(), TableType.TEMP_TAB);

		String[] fields = PennantJavaUtil.getFieldDetails(new FinReceiptHeader(), receiptHeader.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(aAuditHeader.getAuditTranType(), 1, fields[0], fields[1],
				receiptHeader.getBefImage(), receiptHeader));

		auditHeader.setAuditTranType(tranType);
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
				receiptHeader.getBefImage(), receiptHeader));

		//Adding audit as Insert/Update/deleted into main table
		auditHeader.setAuditDetails(auditDetails);
		auditHeaderDAO.addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from the auditHeader. 2) fetch the details
	 * from the tables 3) Validate the Record based on the record details. 4) Validate for any business validation. 5)
	 * for any mismatch conditions Fetch the error details from finReceiptHeaderDAO.getErrorDetail with Error ID and
	 * language as parameters. 6) if any error/Warnings then assign the to auditHeader
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug("Entering");

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
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
		if (receiptHeader.isNew()) { // for New record or new record into work flow

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
		//IN case of any business level validation is required.
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
			return;
		}

		String userBranch = finReceiptHeader.getPostBranch();
		Map<String, BigDecimal> taxPercentages = GSTCalculator.getTaxPercentages(finReceiptHeader.getCustID(),
				finReceiptHeader.getFinCcy(), userBranch, finReceiptHeader.getFinBranch());

		List<FinFeeDetail> tempFinFeeDetails = new ArrayList<FinFeeDetail>();
		for (FinFeeDetail finFeeDetail : finFeeDetailList) {
			FinFeeDetail tempfinFee = new FinFeeDetail();
			BeanUtils.copyProperties(finFeeDetail, tempfinFee);
			tempFinFeeDetails.add(tempfinFee);
		}

		calculateGST(tempFinFeeDetails, taxPercentages, false);

		dataMap.put("ae_paidVasFee", BigDecimal.ZERO);
		BigDecimal totPaidAmt = BigDecimal.ZERO;

		for (FinFeeDetail finFeeDetail : tempFinFeeDetails) {
			if (!finFeeDetail.isRcdVisible()) {
				continue;
			}

			//Incase of Vas FEE
			if (AccountEventConstants.ACCEVENT_VAS_FEE.equals(finFeeDetail.getFinEvent())) {
				BigDecimal vasPaidFee = (BigDecimal) dataMap.get("ae_paidVasFee");
				vasPaidFee = vasPaidFee.add(finFeeDetail.getPaidAmountOriginal());
				dataMap.put("ae_paidVasFee", vasPaidFee);
				totPaidAmt = totPaidAmt.add(finFeeDetail.getPaidAmount());
				continue;
			}

			TaxHeader taxHeader = finFeeDetail.getTaxHeader();
			Taxes cgstTax = new Taxes();
			Taxes sgstTax = new Taxes();
			Taxes igstTax = new Taxes();
			Taxes ugstTax = new Taxes();
			Taxes cessTax = new Taxes();
			if (taxHeader != null) {
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
				}
			}
			String feeTypeCode = finFeeDetail.getFeeTypeCode();

			dataMap.put(feeTypeCode + "_C", finFeeDetail.getActualAmount());
			dataMap.put(feeTypeCode + "_P", finFeeDetail.getPaidAmountOriginal());
			dataMap.put(feeTypeCode + "_TDS_P", finFeeDetail.getPaidTDS());
			dataMap.put(feeTypeCode + "_N", finFeeDetail.getNetAmount());
			dataMap.put(feeTypeCode + "_TDS_N", finFeeDetail.getNetTDS());

			if (FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE.equals(finFeeDetail.getTaxComponent())) {
				dataMap.put(feeTypeCode + "_W",
						finFeeDetail.getWaivedAmount().subtract(cgstTax.getWaivedTax().add(sgstTax.getWaivedTax())
								.add(igstTax.getWaivedTax()).add(ugstTax.getWaivedTax()).add(cessTax.getWaivedTax())));
			} else {
				dataMap.put(feeTypeCode + "_W", finFeeDetail.getWaivedAmount());
			}

			// Calculated Amount
			dataMap.put(feeTypeCode + "_CGST_C", cgstTax.getActualTax());
			dataMap.put(feeTypeCode + "_SGST_C", sgstTax.getActualTax());
			dataMap.put(feeTypeCode + "_IGST_C", igstTax.getActualTax());
			dataMap.put(feeTypeCode + "_UGST_C", ugstTax.getActualTax());
			dataMap.put(feeTypeCode + "_CESS_C", cessTax.getActualTax());

			// Paid Amount
			dataMap.put(feeTypeCode + "_CGST_P", cgstTax.getPaidTax());
			dataMap.put(feeTypeCode + "_SGST_P", sgstTax.getPaidTax());
			dataMap.put(feeTypeCode + "_IGST_P", igstTax.getPaidTax());
			dataMap.put(feeTypeCode + "_UGST_P", ugstTax.getPaidTax());
			dataMap.put(feeTypeCode + "_CESS_P", cessTax.getPaidTax());

			// Net Amount
			dataMap.put(feeTypeCode + "_CGST_N", cgstTax.getNetTax());
			dataMap.put(feeTypeCode + "_SGST_N", sgstTax.getNetTax());
			dataMap.put(feeTypeCode + "_IGST_N", igstTax.getNetTax());
			dataMap.put(feeTypeCode + "_UGST_N", ugstTax.getNetTax());
			dataMap.put(feeTypeCode + "_CESS_N", cessTax.getNetTax());

			// Waiver GST Amounts (GST Waiver Changes)
			dataMap.put(feeTypeCode + "_CGST_W", cgstTax.getWaivedTax());
			dataMap.put(feeTypeCode + "_SGST_W", sgstTax.getWaivedTax());
			dataMap.put(feeTypeCode + "_IGST_W", igstTax.getWaivedTax());
			dataMap.put(feeTypeCode + "_UGST_W", ugstTax.getWaivedTax());
			dataMap.put(feeTypeCode + "_CESS_W", cessTax.getWaivedTax());

			totPaidAmt = totPaidAmt.add(finFeeDetail.getPaidAmount());
		}

		BigDecimal excessAmt = finReceiptHeader.getReceiptAmount().subtract(totPaidAmt);
		dataMap.put("ae_toExcessAmt", excessAmt);

		logger.debug(Literal.LEAVING);
	}

	@Override
	public ErrorDetail processFeePayment(FinServiceInstruction finServInst) throws Exception {
		logger.debug(Literal.ENTERING);

		LoggedInUser userDetails = finServInst.getLoggedInUser();
		if (SessionUserDetails.getLogiedInUser() != null) {
			userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		}
		List<FinFeeDetail> paidFeeList = null;
		if (StringUtils.isNotBlank(finServInst.getFinReference())) {
			String finReference = finServInst.getFinReference();

			FinanceMain financeMain = this.financeMainDAO.getFinBasicDetails(finReference, "_Temp");
			if (financeMain == null) {
				return ErrorUtil.getErrorDetail(new ErrorDetail("9999"), userDetails.getLanguage());
			}
			//FinFeedetails under temp table
			paidFeeList = getPaidFinFeeDetails(finReference, Long.MIN_VALUE, "_TView");

			ErrorDetail errorDetail = validateUpFrontFees(finServInst, paidFeeList, userDetails);
			if (errorDetail != null) {
				return errorDetail;
			}
			finServInst.setCurrency(financeMain.getFinCcy());
			finServInst.setCustCIF(financeMain.getCustCIF());
			finServInst.setCustID(financeMain.getCustID());
			finServInst.setFinType(financeMain.getFinType());
			finServInst.setCurrency(financeMain.getFinCcy());
		} else {
			paidFeeList = finServInst.getFinFeeDetails();
		}

		FinReceiptHeader header = new FinReceiptHeader();
		header.setReference(finServInst.getFinReference());
		header.setCustCIF(finServInst.getCustCIF());
		header.setCustID(finServInst.getCustID());
		long receiptId = finReceiptHeaderDAO.generatedReceiptID(header);
		finServInst.setReceiptId(receiptId);
		header.setReceiptDate(SysParamUtil.getAppDate());
		header.setFinType(finServInst.getFinType());
		header.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
		header.setRecAgainst(RepayConstants.RECEIPTTO_FINANCE);
		header.setReceiptID(receiptId);
		header.setReceiptPurpose(FinanceConstants.FINSER_EVENT_FEEPAYMENT);
		header.setExcessAdjustTo(RepayConstants.EXAMOUNTTYPE_EXCESS);
		header.setAllocationType(RepayConstants.ALLOCATIONTYPE_AUTO);
		header.setReceiptAmount(finServInst.getAmount());
		header.setEffectSchdMethod(PennantConstants.List_Select);
		header.setReceiptMode(finServInst.getPaymentMode());
		header.setReceiptModeStatus(RepayConstants.PAYSTATUS_FEES);
		header.setRemarks(finServInst.getReceiptDetail().getRemarks());
		//changed
		header.setFinCcy(finServInst.getCurrency());
		header.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		header.setNewRecord(true);
		header.setLastMntBy(userDetails.getUserId());
		header.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		header.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		header.setUserDetails(userDetails);
		header.setReceiptSource(PennantConstants.FINSOURCE_ID_API);
		header.setRealizationDate(finServInst.getRealizationDate());
		header.setFinType(finServInst.getFinType());
		header.setFinBranch(finServInst.getFromBranch());
		header.setPostBranch(finServInst.getFromBranch());
		header.setCashierBranch(finServInst.getToBranch());

		WorkFlowDetails workFlowDetails = null;
		String roleCode = null;
		String taskid = null;
		String nextTaskId = null;
		long workFlowId = 0;
		if (!finServInst.isNonStp()) {
			header.setRecordStatus(PennantConstants.RCD_STATUS_SUBMITTED);
			workFlowDetails = WorkFlowUtil.getDetailsByType("FEERECEIPT_PROCESS");
			String processStage = finServInst.getProcessStage();
			if (workFlowDetails != null) {
				workFlow = new WorkflowEngine(workFlowDetails.getWorkFlowXml());
				if (StringUtils.isBlank(processStage)) {
					processStage = workFlow.firstTaskOwner();
				} else {
					//validating given role is available in workflow roles
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

				//If user role as first task owner record should be in save mode
				if (StringUtils.equals(processStage, workFlow.firstTaskOwner())) {
					header.setRecordStatus(PennantConstants.RCD_STATUS_SAVED);
				}

				taskid = workFlow.getUserTaskId(processStage); //### 19-07-2018 Ticket ID : 128015
				workFlowId = workFlowDetails.getWorkFlowId();
				roleCode = processStage;//### 19-07-2018 Ticket ID : 128015

				//nextTaskId = workFlow.getUserTaskId(workFlow.firstTaskOwner());//### 19-07-2018 Ticket ID : 128015
				nextTaskId = workFlow.getNextUserTaskIdsAsString(taskid, header);
				setNextRoleDetails(nextTaskId, workFlow, header);
			}
			header.setTaskId(taskid);
			header.setNextTaskId(nextTaskId);
			header.setRoleCode(roleCode);
			header.setWorkflowId(workFlowId);
		}

		FinReceiptDetail fsiReceiptDtl = finServInst.getReceiptDetail();
		FinReceiptDetail receiptDetail = new FinReceiptDetail();
		receiptDetail.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
		receiptDetail.setPaymentTo(RepayConstants.RECEIPTTO_FINANCE);
		receiptDetail.setAmount(header.getReceiptAmount());
		receiptDetail.setPaymentType(header.getReceiptMode());
		receiptDetail.setValueDate(fsiReceiptDtl.getValueDate());
		receiptDetail.setReceiptID(receiptId);
		receiptDetail.setFavourNumber(fsiReceiptDtl.getFavourNumber());
		receiptDetail.setBankCode(fsiReceiptDtl.getBankCode());
		receiptDetail.setBankBranchID(fsiReceiptDtl.getBankBranchID());
		receiptDetail.setFavourName(fsiReceiptDtl.getFavourName());
		receiptDetail.setDepositDate(fsiReceiptDtl.getDepositDate());
		receiptDetail.setDepositNo(fsiReceiptDtl.getDepositNo());
		receiptDetail.setPaymentRef(fsiReceiptDtl.getPaymentRef());
		receiptDetail.setChequeAcNo(fsiReceiptDtl.getChequeAcNo());
		receiptDetail.setFundingAc(fsiReceiptDtl.getFundingAc());
		receiptDetail.setReceivedDate(fsiReceiptDtl.getReceivedDate());
		receiptDetail.setTransactionRef(fsiReceiptDtl.getTransactionRef());
		receiptDetail.setPartnerBankAc(fsiReceiptDtl.getPartnerBankAc());
		receiptDetail.setPartnerBankAcType(fsiReceiptDtl.getPartnerBankAcType());

		FinRepayHeader repayHeader = new FinRepayHeader();
		repayHeader.setFinReference(header.getReference());
		repayHeader.setValueDate(receiptDetail.getReceivedDate());
		repayHeader.setFinEvent(FinanceConstants.FINSER_EVENT_FEEPAYMENT);
		repayHeader.setRepayAmount(header.getReceiptAmount());

		//Setting
		receiptDetail.getRepayHeaders().add(repayHeader);
		header.getReceiptDetails().add(receiptDetail);
		header.setPaidFeeList(paidFeeList);
		if (StringUtils.isNotBlank(finServInst.getExternalReference())) {
			header.setReference(Objects.toString(finServInst.getCustID(), ""));
			header.setExtReference(finServInst.getExternalReference());
			header.setRecAgainst(RepayConstants.RECEIPTTO_CUSTOMER);
			repayHeader.setFinReference(finServInst.getExternalReference());
		}
		header.setExtReference(finServInst.getExternalReference());

		AuditHeader auditHeader = getAuditHeader(header, PennantConstants.TRAN_WF);

		if (!finServInst.isNonStp()) {
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

		//failed
		ErrorDetail errorDetail = null;
		if (!auditHeader.isNextProcess()) {
			errorDetail = auditHeader.getAuditDetail().getErrorDetails().get(0);
			return ErrorUtil.getErrorDetail(errorDetail);
		}
		return errorDetail;

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
			TableType tableType = TableType.MAIN_TAB;
			for (FinFeeDetail feeDetail : feeDetails) {
				if (feeDetail.getFeeID() <= 0) {
					FinFeeReceipt feeReceipt = feeDetail.getFinFeeReceipts().get(0);
					feeDetail.setTransactionId(receiptHeader.getExtReference());
					feeDetail.setFeeID(finFeeDetailDAO.save(feeDetail, false, tableType.getSuffix()));
					feeReceipt.setFeeID(feeDetail.getFeeID());
					/*
					 * if (feeDetail.getFinTaxDetails() != null) {
					 * feeDetail.getFinTaxDetails().setFeeID(feeDetail.getFeeID());
					 * finTaxDetailsDAO.save(feeDetail.getFinTaxDetails(), ""); }
					 */

					//FIXME>>MURTHY
				} else {
					//checking if the fee available in main table or not, if no save or else update in main table
					FinFeeDetail finFeeDetail = new FinFeeDetail();
					finFeeDetail.setFeeID(feeDetail.getFeeID());
					FinFeeDetail prvsFees = finFeeDetailDAO.getFinFeeDetailById(finFeeDetail, false,
							TableType.MAIN_TAB.getSuffix());
					if (prvsFees != null) {
						if (prvsFees.getFeeID() == feeDetail.getFeeID()) {
							finFeeDetailDAO.update(feeDetail, false, tableType.getSuffix());
						}
					} else {//Saving the fin fee details
						finFeeDetailDAO.save(feeDetail, false, tableType.getSuffix());
					}
					/*
					 * //The same way we need to check weather the tax details are available in main table, then update
					 * other wise save if (feeDetail.getFinTaxDetails() != null) { FinTaxDetails details =
					 * getFinTaxDetailsDAO().getFinTaxByFeeID(feeDetail.getFeeID(), tableType.getSuffix()); if (details
					 * != null) { getFinTaxDetailsDAO().update(feeDetail.getFinTaxDetails(), tableType.getSuffix()); }
					 * else { feeDetail.getFinTaxDetails().setFeeID(feeDetail.getFeeID());
					 * getFinTaxDetailsDAO().save(feeDetail.getFinTaxDetails(), tableType.getSuffix()); } }
					 */
					//FIXME>>MURTHY
				}
			}
			return;
		}

		String type = "_Temp";
		for (FinFeeDetail fee : feeDetails) {
			finFeeDetailService.updateFeesFromUpfront(fee, type);
			//VAS need to update vasrecording also
			if (AccountEventConstants.ACCEVENT_VAS_FEE.equals(fee.getFinEvent())) {
				BigDecimal paidAmount = fee.getPaidAmount();
				if (BigDecimal.ZERO.compareTo(paidAmount) < 0) {
					String vasReference = fee.getVasReference();
					String finReference = fee.getFinReference();
					vASRecordingDAO.updatePaidAmt(vasReference, finReference, paidAmount, type);
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
		//For Audit purpose;
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
				if (feeDetail.getFeeID() <= 0) {
					feeDetail.setFeeID(finFeeDetailDAO.save(feeDetail, false, type));
					feeReceipt.setFeeID(feeDetail.getFeeID());
					/*
					 * if (feeDetail.getFinTaxDetails() != null) {
					 * feeDetail.getFinTaxDetails().setFeeID(feeDetail.getFeeID());
					 * getFinTaxDetailsDAO().save(feeDetail.getFinTaxDetails(), type); }
					 */
					//FIXME MURTHY
				} else {
					//FIXME:Satish Need to check record is available in main/temp tables or not(Loan is created from API with FEE's and it is in temp table
					//user is trying to create a UPFEE with stp=true
					FinFeeDetail detail = finFeeDetailDAO.getFinFeeDetailById(feeDetail, false, type);
					if (detail == null) {
						finFeeDetailDAO.save(feeDetail, false, type);
					} else {
						finFeeDetailDAO.update(feeDetail, false, type);
					}

					/*
					 * if (feeDetail.getFinTaxDetails() != null) {
					 * getFinTaxDetailsDAO().update(feeDetail.getFinTaxDetails(), type); }
					 */

					//FIXME Murthy
				}
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

			if (feeReceipt.isNew() || StringUtils.isEmpty(feeReceipt.getRecordType())) {
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

	private void processExcessAmount(FinReceiptHeader receiptHeader) {
		List<FinFeeDetail> feeDetails = receiptHeader.getPaidFeeList();

		if (CollectionUtils.isEmpty(feeDetails)) {
			return;
		}

		BigDecimal paidAmt = BigDecimal.ZERO;
		for (FinFeeDetail feeDetail : feeDetails) {
			FinFeeReceipt finFeeReceipts = feeDetail.getFinFeeReceipts().get(0);
			paidAmt = paidAmt.add(finFeeReceipts.getPaidAmount());
		}

		BigDecimal excessAmt = receiptHeader.getReceiptAmount().subtract(paidAmt);

		if (BigDecimal.ZERO.compareTo(excessAmt) < 0) {
			FinExcessAmount excess = null;
			String reference = receiptHeader.getReference();
			if (StringUtils.isNotBlank(receiptHeader.getExtReference())) {
				reference = receiptHeader.getExtReference();
			}
			excess = finExcessAmountDAO.getExcessAmountsByRefAndType(reference, receiptHeader.getExcessAdjustTo());
			//Creating Excess
			if (excess == null) {
				excess = new FinExcessAmount();
				excess.setFinReference(reference);
				excess.setAmountType(receiptHeader.getExcessAdjustTo());
				excess.setAmount(excessAmt);
				excess.setUtilisedAmt(BigDecimal.ZERO);
				excess.setBalanceAmt(excessAmt);
				excess.setReservedAmt(BigDecimal.ZERO);
				finExcessAmountDAO.saveExcess(excess);
			} else {
				excess.setBalanceAmt(excess.getBalanceAmt().add(excessAmt));
				excess.setAmount(excess.getAmount().add(excessAmt));
				finExcessAmountDAO.updateExcess(excess);
			}

			//Creating ExcessMoment
			FinExcessMovement excessMovement = new FinExcessMovement();
			excessMovement.setExcessID(excess.getExcessID());
			excessMovement.setAmount(excessAmt);
			excessMovement.setReceiptID(receiptHeader.getReceiptID());
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
			finFeeDetail.setPaidCalcReq(true);

			//In case of approve need to calculate complete paid amount GST because it is updating on fees
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

		//in case of PaidFee's contains vas
		List<VASRecording> vasRecordingList = new ArrayList<>(1);
		for (FinFeeDetail finFeeDetail : paidFeeList) {
			if (AccountEventConstants.ACCEVENT_VAS_FEE.equals(finFeeDetail.getFinEvent())) {
				VASRecording vasRecording = null;
				vasRecording = vASRecordingDAO.getVASRecordingByReference(finFeeDetail.getVasReference(), "_Temp");
				if (vasRecording != null) {
					vasRecordingList.add(vasRecording);
				}
			}
		}

		BigDecimal totalFeePaid = BigDecimal.ZERO;
		for (FinFeeDetail upFinFeeDetail : fsi.getFinFeeDetails()) {
			String feeCode = StringUtils.trimToEmpty(upFinFeeDetail.getFeeTypeCode());

			//In case of req contain duplicate fees.
			if (processedFees.contains(feeCode.toLowerCase())) {
				String[] valueParm = new String[1];
				valueParm[0] = "Fees : " + feeCode;
				return ErrorUtil.getErrorDetail(new ErrorDetail("90273", valueParm));
			}
			processedFees.add(feeCode.toLowerCase());
			FinFeeDetail fee = getFeeByFeeTypeCode(feeCode, paidFeeList, vasRecordingList);
			//In case of Invalid fee code
			if (fee == null) {
				String[] valueParm = new String[1];
				valueParm[0] = feeCode;
				return ErrorUtil.getErrorDetail(new ErrorDetail("90206", valueParm));
			}
			// PSD #157162 IMD API issue> system is deducting waiver amount  from remaining fee amount and not allowing to make the payment of complete remaining amount
			BigDecimal amount = fee.getRemainingFee().add(fee.getWaivedAmount());

			if (upFinFeeDetail.getPaidAmount() == null) {
				upFinFeeDetail.setPaidAmount(BigDecimal.ZERO);
			}
			if (upFinFeeDetail.getPaidAmount().compareTo(amount) > 0) {
				String[] valueParm = new String[2];
				valueParm[0] = feeCode;
				valueParm[1] = PennantApplicationUtil.amountFormate(amount, 0);
				return ErrorUtil.getErrorDetail(new ErrorDetail("IMD003", valueParm));
			}
			BigDecimal allocatedAmt = upFinFeeDetail.getPaidAmount();
			totalFeePaid = totalFeePaid.add(allocatedAmt);
			FinFeeReceipt feeReceipt = fee.getFinFeeReceipts().get(0);
			feeReceipt.setPaidAmount(allocatedAmt);
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
		//In case of VAS
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

		//In case of normal Fee
		for (FinFeeDetail finFeeDetail : finfeeDetailList) {
			if (StringUtils.equalsIgnoreCase(feeCode, finFeeDetail.getFeeTypeCode())) {
				return finFeeDetail;
			}
		}
		return null;
	}

	/**
	 * Get Audit Header Details
	 */
	private AuditHeader getAuditHeader(FinReceiptHeader receiptHeader, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, null, receiptHeader);
		return new AuditHeader(String.valueOf(receiptHeader.getReceiptID()), null, null, null, auditDetail,
				receiptHeader.getUserDetails(), new HashMap<String, ArrayList<ErrorDetail>>());
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

	public void setFinServiceInstructionDAO(FinServiceInstrutionDAO finServiceInstructionDAO) {
		this.finServiceInstructionDAO = finServiceInstructionDAO;
	}

	@Override
	public Map<String, Object> getGLSubHeadCodes(String finRef) {
		return financeMainDAO.getGLSubHeadCodes(finRef);
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

}
