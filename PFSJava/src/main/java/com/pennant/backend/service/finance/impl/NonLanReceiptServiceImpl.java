package com.pennant.backend.service.finance.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SessionUserDetails;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.applicationmaster.EntityDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.cashmanagement.BranchCashDetailDAO;
import com.pennant.backend.dao.partnerbank.PartnerBankDAO;
import com.pennant.backend.dao.receipts.FinReceiptDetailDAO;
import com.pennant.backend.dao.receipts.FinReceiptHeaderDAO;
import com.pennant.backend.dao.rmtmasters.AccountingSetDAO;
import com.pennant.backend.model.applicationmaster.BankDetail;
import com.pennant.backend.model.applicationmaster.Entity;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinReceiptData;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinReceiptQueueLog;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.ManualAdviseMovements;
import com.pennant.backend.model.finance.ReceiptAPIRequest;
import com.pennant.backend.model.partnerbank.PartnerBank;
import com.pennant.backend.model.partnerbank.PartnerBankModes;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.service.applicationmaster.BankDetailService;
import com.pennant.backend.service.finance.GenericFinanceDetailService;
import com.pennant.backend.service.finance.NonLanReceiptService;
import com.pennant.backend.service.finance.ReceiptCancellationService;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.UploadConstants;
import com.pennant.batchupload.fileprocessor.BatchUploadProcessorConstatnt;
import com.pennant.eod.constants.EodConstants;
import com.pennanttech.logging.model.InterfaceLogDetail;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.InterfaceConstants;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.logging.dao.InterfaceLoggingDAO;
import com.pennanttech.pff.receipt.constants.ReceiptMode;
import com.rits.cloning.Cloner;

public class NonLanReceiptServiceImpl extends GenericFinanceDetailService implements NonLanReceiptService {
	private static final Logger logger = LogManager.getLogger(NonLanReceiptServiceImpl.class);

	private FinReceiptHeaderDAO finReceiptHeaderDAO;
	private FinReceiptDetailDAO finReceiptDetailDAO;
	private PartnerBankDAO partnerBankDAO;
	private ReceiptCancellationService receiptCancellationService;
	private EntityDAO entityDAO;
	private BankDetailService bankDetailService;
	private AccountingSetDAO accountingSetDAO;
	private BranchCashDetailDAO branchCashDetailDAO;
	private InterfaceLoggingDAO interfaceLoggingDAO;
	private AuditHeaderDAO auditHeaderDAO;

	private List<String> RECEIPT_MODES;
	private List<String> RECEIPT_CHANNELS;
	private List<String> SUB_RECEIPT_MODES;
	private List<String> SUB_RECEIPT_SOURCES;

	private static final String AUTHORIZATION_KEY = App.getProperty("non.lan.receipt.mob.agency.authorization");
	private static final String ENTITY = App.getProperty("non.lan.receipt.mob.agency.entity");

	private static final String UPDATE_URL = App.getProperty("non.lan.receipt.mob.agency.limit.update.url");
	private static final String UPDATE_VERSION = App.getProperty("non.lan.receipt.mob.agency.limit.update.version");
	private static final String CREATE_URL = App.getProperty("non.lan.receipt.mob.agency.limit.create.url");
	private static final String CREATE_VERSION = App.getProperty("non.lan.receipt.mob.agency.limit.create.version");

	private static final String FLAG = App.getProperty("non.lan.receipt.mob.agency.limit.update");

	public static final String COLLECTION_API_APPROVER = "APPROVER";
	public static final String COLLECTION_API_CANCEL = "CANCEL";
	public static final String RECEIPT_SOURCE_MOBILE = "MOB";
	public static final String RECEIPT_CHANNEL_MOBILE = "MOB";

	public static final String HEADER_NAME = "ServiceName";
	public static final String HEADER_VERSION = "ServiceVersion";
	public static final String ENTITY_ID = "ENTITYID";
	public static final String LANGUAGE = "Language";
	public static final String REQUEST_TIME = "RequestTime";

	@Override
	public FinReceiptHeader getNonLanFinReceiptHeaderById(long receiptID, boolean isFeePayment, String type) {
		logger.info(Literal.ENTERING);

		FinReceiptHeader receiptHeader = finReceiptHeaderDAO.getNonLanReceiptHeader(receiptID, type);

		if (receiptHeader == null) {
			logger.info(Literal.LEAVING);
			return receiptHeader;
		}

		if (receiptHeader.getReasonCode() == null) {
			receiptHeader.setReasonCode(Long.valueOf(0));
		}

		List<FinReceiptDetail> receiptDetailList = finReceiptDetailDAO.getNonLanReceiptHeader(receiptID, type);

		int size = receiptDetailList.size();
		if (size > 0) {
			receiptHeader.setValueDate(receiptDetailList.get(size - 1).getValueDate());
		}
		receiptHeader.setReceiptDetails(receiptDetailList);

		receiptHeader.setManualAdvise(manualAdviseDAO.getManualAdviseByReceiptId(receiptID, "_View"));

		logger.info(Literal.LEAVING);
		return receiptHeader;
	}

	@Override
	public AuditHeader saveOrUpdate(AuditHeader aAuditHeader) {
		logger.info(Literal.ENTERING);

		aAuditHeader = businessValidation(aAuditHeader, "saveOrUpdate");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		if (!aAuditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return aAuditHeader;
		}

		boolean changeStatus = false;

		Cloner cloner = new Cloner();
		AuditHeader auditHeader = cloner.deepClone(aAuditHeader);
		FinReceiptData rceiptData = (FinReceiptData) auditHeader.getAuditDetail().getModelData();

		FinReceiptHeader receiptHeader = rceiptData.getReceiptHeader();
		if (!PennantConstants.RCD_STATUS_RESUBMITTED.equalsIgnoreCase(receiptHeader.getRecordStatus())
				&& !PennantConstants.RCD_STATUS_SAVED.equalsIgnoreCase(receiptHeader.getRecordStatus())) {
			changeStatus = true;
		}

		String roleCode = receiptHeader.getRoleCode();

		if (FinanceConstants.DEPOSIT_MAKER.equals(roleCode) && changeStatus) {
			receiptHeader.setReceiptModeStatus(RepayConstants.PAYSTATUS_INITIATED);
		} else if (FinanceConstants.DEPOSIT_APPROVER.equals(roleCode) && changeStatus) {
			receiptHeader.setReceiptModeStatus(RepayConstants.PAYSTATUS_DEPOSITED);
		} else if (FinanceConstants.REALIZATION_MAKER.equals(roleCode)) {
		} else if (FinanceConstants.REALIZATION_APPROVER.equals(roleCode)) {
		} else {
			receiptHeader.setReceiptModeStatus(RepayConstants.PAYSTATUS_INITIATED);
		}

		TableType tableType = TableType.MAIN_TAB;
		if (receiptHeader.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}
		// financeMain.setRcdMaintainSts(FinServiceEvent.RECEIPT);
		if (tableType == TableType.MAIN_TAB) {
			receiptHeader.setRcdMaintainSts(null);
		}

		long linkedTranId = 0;
		// Executing Accounting for Cheque/ DD
		if (FinanceConstants.DEPOSIT_APPROVER.equals(roleCode)
				&& !(PennantConstants.RCD_STATUS_SAVED.equals(receiptHeader.getRecordStatus())
						|| PennantConstants.RCD_STATUS_RESUBMITTED.equals(receiptHeader.getRecordStatus()))) {
			linkedTranId = executeAccounting(rceiptData);
		}

		// Finance Main Details Save And Update
		// =======================================
		long receiptID = receiptHeader.getReceiptID();
		if (linkedTranId != 0) {
			receiptHeader.setLinkedTranId(linkedTranId);
		}
		receiptHeader.setRcdMaintainSts("R");
		if (receiptHeader.isNewRecord()) {
			// Save Receipt Header
			receiptID = finReceiptHeaderDAO.save(receiptHeader, tableType);

		} else {
			// Save/Update FinRepayHeader Details depends on Workflow
			if (tableType == TableType.TEMP_TAB) {

				// Update Receipt Header
				finReceiptHeaderDAO.update(receiptHeader, tableType);

				// Delete Save Receipt Detail List by Reference
				finReceiptDetailDAO.deleteByReceiptID(receiptID, tableType);
			}

			// Bounce reason Code
		}

		ManualAdvise advise = manualAdviseDAO.getManualAdviseByReceiptId(receiptID, "_Temp");
		ManualAdvise manualAdvise = receiptHeader.getManualAdvise();
		if (manualAdvise != null) {
			if (advise == null) {
				manualAdviseDAO.save(manualAdvise, tableType);
			} else {
				manualAdviseDAO.update(manualAdvise, tableType);
			}
		} else {
			if (advise != null) {
				manualAdviseDAO.delete(manualAdvise, tableType);
			}
		}

		// Save Receipt Detail List by setting Receipt Header ID
		List<FinReceiptDetail> receiptDetails = receiptHeader.getReceiptDetails();

		// Manual Advise Movements
		manualAdviseDAO.deleteMovementsByReceiptID(receiptID, TableType.TEMP_TAB.getSuffix());

		for (FinReceiptDetail receiptDetail : receiptDetails) {
			receiptDetail.setReceiptID(receiptID);
			long receiptSeqID = receiptDetail.getReceiptSeqID();
			if (!receiptDetail.isDelRecord()) {
				receiptSeqID = finReceiptDetailDAO.save(receiptDetail, tableType);
			}

			// Manual Advise Movements
			for (ManualAdviseMovements movement : receiptDetail.getAdvMovements()) {
				movement.setReceiptID(receiptID);
				movement.setReceiptSeqID(receiptSeqID);
				manualAdviseDAO.saveMovement(movement, TableType.TEMP_TAB.getSuffix());
			}

		}

		FinReceiptHeader befRctHeader = new FinReceiptHeader();
		if (receiptHeader.isNewRecord()) {
			BeanUtils.copyProperties(receiptHeader, befRctHeader);
		} else {
			befRctHeader = getNonLanFinReceiptHeaderById(receiptHeader.getReceiptID(), false, "_View");
		}
		// FinReceiptDetail Audit Details Preparation
		String[] rhFields = PennantJavaUtil.getFieldDetails(new FinReceiptDetail(),
				receiptHeader.getReceiptDetails().get(0).getExcludeFields());
		for (int i = 0; i < receiptHeader.getReceiptDetails().size(); i++) {
			auditDetails.add(new AuditDetail(auditHeader.getAuditTranType(), 1, rhFields[0], rhFields[1],
					befRctHeader.getReceiptDetails().get(i), receiptHeader.getReceiptDetails().get(i)));
		}

		String[] fields = PennantJavaUtil.getFieldDetails(new FinReceiptHeader(), receiptHeader.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
				receiptHeader.getBefImage(), receiptHeader));

		auditHeader.setAuditDetails(auditDetails);
		auditHeader.setAuditModule("Receipt");
		auditHeaderDAO.addAudit(auditHeader);
		auditHeader.getAuditDetail().setModelData(rceiptData);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}
		String tranType = PennantConstants.TRAN_WF;

		FinReceiptData receiptData = (FinReceiptData) auditHeader.getAuditDetail().getModelData();
		FinReceiptHeader rch = receiptData.getReceiptHeader();
		List<AuditDetail> auditDetails = new ArrayList<>();

		// Delete Save Receipt Detail List by Reference
		long receiptID = rch.getReceiptID();
		finReceiptDetailDAO.deleteByReceiptID(receiptID, TableType.TEMP_TAB);

		// Delete Manual Advise Movements
		manualAdviseDAO.deleteMovementsByReceiptID(receiptID, TableType.TEMP_TAB.getSuffix());

		// Bounce reason Code
		/*
		 * ManualAdvise advise = getManualAdviseDAO().getManualAdviseByReceiptId(receiptData.
		 * getReceiptHeader().getReceiptID(), "_Temp"); if (advise != null) { getManualAdviseDAO().delete(advise,
		 * TableType.TEMP_TAB); }
		 */

		// Delete Receipt Header
		finReceiptHeaderDAO.deleteByReceiptID(receiptID, TableType.TEMP_TAB);

		// FinReceiptDetail Audit Details Preparation
		List<FinReceiptDetail> receiptDetails = rch.getReceiptDetails();
		String[] rFields = PennantJavaUtil.getFieldDetails(new FinReceiptDetail(),
				receiptDetails.get(0).getExcludeFields());

		for (int i = 0; i < receiptDetails.size(); i++) {
			auditDetails.add(
					new AuditDetail(tranType, 1, rFields[0], rFields[1], receiptDetails.get(i), receiptDetails.get(i)));
		}

		// Receipt Header Audit Details Preparation
		String[] rhFields = PennantJavaUtil.getFieldDetails(new FinReceiptHeader(), rch.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(tranType, 1, rhFields[0], rhFields[1], rch.getBefImage(), rch));

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeader.getAuditDetail().setAuditTranType(PennantConstants.TRAN_WF);
		auditHeader.setAuditModule("Receipt");
		auditHeader.setAuditDetails(auditDetails);
		auditHeaderDAO.addAudit(auditHeader);

		// Reset Finance Detail Object for Service Task Verifications
		auditHeader.getAuditDetail().setModelData(receiptData);

		logger.info(Literal.LEAVING);

		return auditHeader;
	}

	@Override
	public AuditHeader doReversal(AuditHeader auditHeader) {
		return null;
	}

	@Override
	public AuditHeader doApprove(AuditHeader aAuditHeader) {
		logger.info(Literal.ENTERING);

		FinReceiptData orgReceiptData = (FinReceiptData) aAuditHeader.getAuditDetail().getModelData();
		String tranType = "";
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		Cloner cloner = new Cloner();
		AuditHeader auditHeader = cloner.deepClone(aAuditHeader);
		FinReceiptData receiptData = (FinReceiptData) auditHeader.getAuditDetail().getModelData();
		FinReceiptHeader receiptHeader = receiptData.getReceiptHeader();
		String roleCode = receiptHeader.getRoleCode();
		String nextRoleCode = receiptHeader.getNextRoleCode();

		// Preparing Before Image for Audit
		FinReceiptHeader befRctHeader = null;
		if (!PennantConstants.RECORD_TYPE_NEW.equals(orgReceiptData.getReceiptHeader().getRecordType())) {
			befRctHeader = getNonLanFinReceiptHeaderById(receiptHeader.getReceiptID(), false, "_AView");
		}
		List<FinReceiptDetail> befFinReceiptDetail = new ArrayList<>();
		if (befRctHeader != null) {
			befFinReceiptDetail = befRctHeader.getReceiptDetails();
		}
		aAuditHeader.getAuditDetail().setBefImage(befRctHeader);

		String receiptSource = receiptHeader.getReceiptSource();
		String receiptMode = receiptHeader.getReceiptMode();
		String receiptChannel = receiptHeader.getReceiptChannel();

		String receiptModeStatus = receiptHeader.getReceiptModeStatus();
		if (RepayConstants.PAYSTATUS_BOUNCE.equals(receiptModeStatus)
				|| RepayConstants.PAYSTATUS_CANCEL.equals(receiptModeStatus)) {
			receiptCancellationService.doApproveNonLanReceipt(aAuditHeader);

			// Calling Collection Agencies API
			if (RECEIPT_SOURCE_MOBILE.equals(receiptSource)
					&& ((ReceiptMode.CASH.equals(receiptMode) && RECEIPT_CHANNEL_MOBILE.equals(receiptChannel))
							|| (ReceiptMode.ONLINE.equals(receiptMode)
									&& ReceiptMode.BANKDEPOSIT.equals(receiptHeader.getSubReceiptMode())))) {

				if ("Y".equalsIgnoreCase(FLAG)) {
					updateMobileAgencyLimit(receiptHeader, "D", false);
				}
			}

			aAuditHeader.getAuditDetail().setModelData(receiptData);
			return aAuditHeader;
		}

		aAuditHeader = businessValidation(aAuditHeader, "doApprove");
		if (!aAuditHeader.isNextProcess()) {
			return aAuditHeader;
		}

		FinReceiptHeader rch = receiptHeader;
		rch.setRoleCode(roleCode);
		rch.setNextRoleCode(nextRoleCode);
		rch.setReceiptModeStatus(RepayConstants.PAYSTATUS_REALIZED);
		if (StringUtils.equals(FinanceConstants.DEPOSIT_APPROVER, rch.getRoleCode()) || receiptData.isPresentment()) {
			rch.setReceiptModeStatus(RepayConstants.PAYSTATUS_DEPOSITED);
		}

		if (rch.getReceiptID() == 0 || rch.getReceiptID() == Long.MIN_VALUE) {
			long receiptId = finReceiptHeaderDAO.generatedReceiptID(rch);
			rch.setReceiptID(receiptId);
		}

		Long linkedTranId = rch.getLinkedTranId();

		if (PennantConstants.FINSOURCE_ID_API.equals(receiptHeader.getSourceId())) {

			linkedTranId = executeAccounting(receiptData);

		} else if (!StringUtils.equals(ReceiptMode.CHEQUE, rch.getReceiptMode())
				&& !StringUtils.equals(ReceiptMode.DD, rch.getReceiptMode())) {
			rch.setRealizationDate(rch.getValueDate());

			// Executing Accounting for Cash/Online
			linkedTranId = executeAccounting(receiptData);
		}

		finReceiptHeaderDAO.generatedReceiptID(rch);
		// rch.setPostBranch(auditHeader.getAuditBranchCode());
		// rch.setCashierBranch(auditHeader.getAuditBranchCode());
		rch.setRcdMaintainSts(null);
		rch.setRoleCode("");
		rch.setNextRoleCode("");
		rch.setTaskId("");
		rch.setNextTaskId("");
		rch.setWorkflowId(0);

		tranType = PennantConstants.TRAN_UPD;
		rch.setRecordType("");

		// Save Receipt Header

		rch.setRcdMaintainSts(null);
		rch.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		rch.setRecordType("");
		rch.setRoleCode("");
		rch.setNextRoleCode("");
		rch.setTaskId("");
		rch.setNextTaskId("");
		rch.setWorkflowId(0);
		rch.setLinkedTranId(linkedTranId);

		long receiptID = 0;
		if (PennantConstants.RECORD_TYPE_NEW.equals(orgReceiptData.getReceiptHeader().getRecordType())) {
			receiptID = finReceiptHeaderDAO.save(rch, TableType.MAIN_TAB); // saving Receipt Header
			rch.setReceiptID(receiptID);

			FinReceiptDetail rcd = rch.getReceiptDetails().get(0);
			rcd.setStatus(RepayConstants.PAYSTATUS_APPROVED);
			rcd.setReceiptID(receiptID);
			finReceiptDetailDAO.save(rcd, TableType.MAIN_TAB);// saving Receipt Detail
		} else {
			finReceiptHeaderDAO.update(rch, TableType.MAIN_TAB); // saving Receipt Header
			rch.setReceiptID(receiptID);

			FinReceiptDetail rcd = rch.getReceiptDetails().get(0);
			rcd.setStatus(RepayConstants.PAYSTATUS_APPROVED);
			rcd.setReceiptID(rch.getReceiptID());
			finReceiptDetailDAO.updateReceiptStatus(rch.getReceiptID(), rcd.getReceiptSeqID(),
					rch.getReceiptModeStatus());// saving Receipt Detail
		}

		List<AuditDetail> tempAuditDetailList = new ArrayList<AuditDetail>();

		if (!StringUtils.equals(PennantConstants.FINSOURCE_ID_API, receiptData.getSourceId())) {

			// Delete Save Receipt Detail List by Reference
			finReceiptDetailDAO.deleteByReceiptID(orgReceiptData.getReceiptHeader().getReceiptID(), TableType.TEMP_TAB);

			// Delete Manual Advise Movements
			manualAdviseDAO.deleteMovementsByReceiptID(orgReceiptData.getReceiptHeader().getReceiptID(),
					TableType.TEMP_TAB.getSuffix());

			// Delete Receipt Header
			finReceiptHeaderDAO.deleteByReceiptID(orgReceiptData.getReceiptHeader().getReceiptID(), TableType.TEMP_TAB);
		}

		// FinReceiptDetail Audit Details Preparation
		String[] rFields = PennantJavaUtil.getFieldDetails(new FinReceiptDetail(),
				receiptHeader.getReceiptDetails().get(0).getExcludeFields());
		for (int i = 0; i < receiptHeader.getReceiptDetails().size(); i++) {
			tempAuditDetailList.add(new AuditDetail(aAuditHeader.getAuditTranType(), 1, rFields[0], rFields[1], null,
					orgReceiptData.getReceiptHeader().getReceiptDetails().get(i)));
		}

		// Receipt Header Audit Details Preparation
		String[] rhFields = PennantJavaUtil.getFieldDetails(new FinReceiptHeader(), receiptHeader.getExcludeFields());
		aAuditHeader.setAuditDetail(new AuditDetail(aAuditHeader.getAuditTranType(), 1, rhFields[0], rhFields[1], null,
				orgReceiptData.getReceiptHeader()));

		// Adding audit as deleted from TEMP table
		aAuditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		aAuditHeader.setAuditDetails(tempAuditDetailList);
		aAuditHeader.setAuditModule("Receipt");
		auditHeaderDAO.addAudit(aAuditHeader);
		aAuditHeader.getAuditDetail().setModelData(receiptData);

		if (orgReceiptData.getReceiptHeader().getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
			tranType = PennantConstants.TRAN_ADD;
		} else {
			tranType = PennantConstants.TRAN_UPD;
		}

		// FinReceiptDetail Audit Details Preparation
		if (befFinReceiptDetail.isEmpty()) {
			for (int i = 0; i < receiptHeader.getReceiptDetails().size(); i++) {
				auditDetails.add(new AuditDetail(tranType, 1, rFields[0], rFields[1], null,
						receiptHeader.getReceiptDetails().get(i)));
			}
		} else {
			for (int i = 0; i < receiptHeader.getReceiptDetails().size(); i++) {
				auditDetails.add(new AuditDetail(tranType, 1, rFields[0], rFields[1], befFinReceiptDetail.get(i),
						receiptHeader.getReceiptDetails().get(i)));
			}
		}

		// FinReceiptHeader Audit
		auditHeader.setAuditDetail(new AuditDetail(tranType, 1, rhFields[0], rhFields[1], befRctHeader, receiptHeader));

		// Adding audit as Insert/Update/deleted into main table
		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.setAuditDetails(auditDetails);
		auditHeader.setAuditModule("Receipt");
		auditHeaderDAO.addAudit(auditHeader);
		auditHeader.getAuditDetail().setModelData(receiptData);

		if ("Y".equalsIgnoreCase(FLAG)) {
			if (RECEIPT_SOURCE_MOBILE.equals(receiptSource)
					&& ((ReceiptMode.CASH.equals(receiptMode) && RECEIPT_CHANNEL_MOBILE.equals(receiptChannel))
							|| (ReceiptMode.ONLINE.equals(receiptMode)
									&& ReceiptMode.BANKDEPOSIT.equals(receiptHeader.getSubReceiptMode())))) {
				updateMobileAgencyLimit(receiptHeader, "C", false);
			}
		}

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	public Long executeAccounting(FinReceiptData receiptData) {
		logger.info(Literal.ENTERING);
		FinReceiptHeader rch = receiptData.getReceiptHeader();

		String receiptChannel = rch.getReceiptChannel();

		/*
		 * if (RepayConstants.RECEIPTMODE_CASH.equals(receiptMode) && !RECEIPT_SOURCE_MOBILE.equals(receiptChannel)) {
		 * branchCashDetailDAO.updateBranchCashDetail(receiptHeader.getCashierBranch(), receiptAmount,
		 * CashManagementConstants.Add_Receipt_Amount); }
		 */

		AEEvent aeEvent = new AEEvent();
		aeEvent.setEntityCode(rch.getEntityCode());
		aeEvent.setBranch(rch.getCashierBranch());
		aeEvent.setPostingUserBranch(rch.getCashierBranch());
		aeEvent.setAccountingEvent(AccountingEvent.NLRCPT);

		if (rch.getFinID() != null) {
			aeEvent.setFinID(rch.getFinID());
		}

		aeEvent.setFinReference(rch.getReference());
		aeEvent.setValueDate(SysParamUtil.getAppDate());
		aeEvent.setPostRefId(rch.getReceiptID());

		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
		if (amountCodes == null) {
			amountCodes = new AEAmountCodes();
		}

		rch.getPartnerBankCode();
		aeEvent.setBranch(rch.getPostBranch());
		aeEvent.setCcy(SysParamUtil.getAppCurrency());
		amountCodes.setEntitycode(rch.getEntityCode());
		amountCodes.setUserBranch(rch.getCashierBranch());
		amountCodes.setReceiptChannel(receiptChannel);
		amountCodes.setPaymentType(rch.getReceiptDetails().get(0).getPaymentType());
		Map<String, Object> dataMap = amountCodes.getDeclaredFieldValues();

		receiptData.getReceiptHeader().getReceiptDetails().get(0).getDeclaredFieldValues(aeEvent.getDataMap());
		aeEvent.setDataMap(dataMap);

		BigDecimal amount = BigDecimal.ZERO;
		for (FinReceiptDetail detail : receiptData.getReceiptHeader().getReceiptDetails()) {
			String paymentType = detail.getPaymentType();
			if (!(paymentType.equals(ReceiptMode.EMIINADV) || paymentType.equals(ReceiptMode.EXCESS)
					|| paymentType.equals(ReceiptMode.PAYABLE))) {
				amount = amount.add(detail.getAmount());
				amountCodes.setPartnerBankAc(detail.getPartnerBankAc());
				amountCodes.setPartnerBankAcType(detail.getPartnerBankAcType());
			}
		}

		dataMap = amountCodes.getDeclaredFieldValues();

		rch.getReceiptDetails().get(0).getDeclaredFieldValues(aeEvent.getDataMap());
		aeEvent.setDataMap(dataMap);

		aeEvent.getDataMap().put("rd_amount", amount);
		aeEvent.getDataMap().put("ae_receiptSource", rch.getReceiptSource());
		aeEvent.getDataMap().put("ae_receiptSourceAcType", rch.getReceiptSourceAcType());

		long accountsetId = accountingSetDAO.getAccountingSetId(AccountingEvent.NLRCPT, AccountingEvent.NLRCPT);
		aeEvent.getAcSetIDList().add(accountsetId);
		aeEvent = postingsPreparationUtil.postAccounting(aeEvent);
		logger.debug(Literal.LEAVING);

		return aeEvent.getLinkedTranId();
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from the auditHeader. 2) fetch the details
	 * from the tables 3) Validate the Record based on the record details. 4) Validate for any business validation. 5)
	 * for any mismatch conditions Fetch the error details from getFinReceiptHeaderDAO().getErrorDetail with Error ID
	 * and language as parameters. 6) if any error/Warnings then assign the to auditHeader
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.info(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = getAuditDetails(auditHeader, method);

		for (int i = 0; i < auditDetails.size(); i++) {
			auditHeader.setErrorList(auditDetails.get(i).getErrorDetails());
		}

		auditHeader = nextProcess(auditHeader);
		logger.info(Literal.LEAVING);
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
		logger.info(Literal.ENTERING);

		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		FinReceiptData repayData = (FinReceiptData) auditDetail.getModelData();
		FinReceiptHeader finReceiptHeader = repayData.getReceiptHeader();

		FinReceiptHeader tempFinReceiptHeader = null;
		if (finReceiptHeader.isWorkflow()) {
			tempFinReceiptHeader = finReceiptHeaderDAO.getReceiptHeaderByID(finReceiptHeader.getReceiptID(),
					TableType.TEMP_TAB.getSuffix());
		}
		FinReceiptHeader befFinReceiptHeader = finReceiptHeaderDAO.getReceiptHeaderByID(finReceiptHeader.getReceiptID(),
				"");
		FinReceiptHeader oldFinReceiptHeader = finReceiptHeader.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = String.valueOf(finReceiptHeader.getReceiptID());
		errParm[0] = PennantJavaUtil.getLabel("label_ReceiptId") + ":" + valueParm[0];

		if (finReceiptHeader.isNewRecord()) { // for New record or new record into work flow

			if (!finReceiptHeader.isWorkflow()) {// With out Work flow only new
				// records
				if (befFinReceiptHeader != null) { // Record Already Exists in the table then error
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow
				if (finReceiptHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if
					// records type is new
					if (befFinReceiptHeader != null || tempFinReceiptHeader != null) { // if
						// records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
					}
				} else { // if records not exists in the Main flow table
					if (befFinReceiptHeader == null || tempFinReceiptHeader != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!finReceiptHeader.isWorkflow()) { // With out Work flow for update and delete

				if (befFinReceiptHeader == null) { // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, valueParm), usrLanguage));
				} else {
					if (oldFinReceiptHeader != null
							&& !oldFinReceiptHeader.getLastMntOn().equals(befFinReceiptHeader.getLastMntOn())) {
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

				if (tempFinReceiptHeader == null) { // if records not exists in the Work flow table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}

				if (tempFinReceiptHeader != null && oldFinReceiptHeader != null
						&& !oldFinReceiptHeader.getLastMntOn().equals(tempFinReceiptHeader.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !finReceiptHeader.isWorkflow()) {
			finReceiptHeader.setBefImage(befFinReceiptHeader);
		}

		return auditDetail;
	}

	/**
	 * Method for prepare AuditHeader
	 * 
	 */
	private AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {
		logger.info(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		FinReceiptData repayData = (FinReceiptData) auditHeader.getAuditDetail().getModelData();
		auditHeader.getAuditDetail().setModelData(repayData);
		auditHeader.setAuditDetails(auditDetails);
		logger.debug("Leaving ");

		return auditHeader;
	}

	@Override
	public void saveMultiReceipt(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);
		boolean flag = false;
		String error = "";
		FinReceiptQueueLog finReceiptQueue = new FinReceiptQueueLog();
		finReceiptQueue.setStartTime(LocalDateTime.now().getHour() + ":" + LocalDateTime.now().getMinute() + ":"
				+ LocalDateTime.now().getSecond()); // Thread Processing Start
													// Time

		Map<String, String> valueMap = new HashMap<>();
		FinReceiptData finReceiptData = (FinReceiptData) auditHeader.getAuditDetail().getModelData();
		FinReceiptDetail finReceiptDetail = new FinReceiptDetail();

		for (FinReceiptDetail receiptDetail : finReceiptData.getReceiptHeader().getReceiptDetails()) {
			if (!(ReceiptMode.EMIINADV.equals(receiptDetail.getPaymentType())
					|| ReceiptMode.EXCESS.equals(receiptDetail.getPaymentType())
					|| ReceiptMode.PAYABLE.equals(receiptDetail.getPaymentType()))) {
				finReceiptDetail = receiptDetail;
				finReceiptDetail.setStatus(finReceiptData.getReceiptHeader().getReceiptModeStatus());
			}
		}

		try {
			// saveOrUpdate Or Approve method
			if (PennantConstants.method_doApprove.equals(auditHeader.getAuditDetail().getLovDescRecordStatus())) {
				doApprove(auditHeader);
			} else {
				saveOrUpdate(auditHeader);
			}

		} catch (Exception e) {
			flag = true;
			error = e.getMessage();
			logger.error(Literal.EXCEPTION, e);
		}
		if (flag) {
			valueMap.put("uploadStatus", UploadConstants.UPLOAD_STATUS_FAIL);
			finReceiptQueue.setProgress(EodConstants.PROGRESS_FAILED);
		} else {
			valueMap.put("uploadStatus", UploadConstants.UPLOAD_STATUS_SUCCESS);
			finReceiptQueue.setProgress(EodConstants.PROGRESS_SUCCESS);
		}

		if (error != null && error.length() >= 1000) {
			error = error.substring(0, 999);
		}
		valueMap.put("reason", error);
		finReceiptHeaderDAO.saveMultiReceipt(finReceiptData.getReceiptHeader(), finReceiptDetail, valueMap); // Saving
																												// MultiReceiptApproval
																												// Table

		finReceiptQueue.setUploadId(finReceiptData.getReceiptHeader().getBatchId());
		finReceiptQueue.setReceiptId(finReceiptData.getReceiptHeader().getReceiptID());
		finReceiptQueue.setThreadId(Thread.currentThread().getId());
		finReceiptQueue.setEndTime(LocalDateTime.now().getHour() + ":" + LocalDateTime.now().getMinute() + ":"
				+ LocalDateTime.now().getSecond());
		finReceiptQueue.setErrorLog(error);
		finReceiptHeaderDAO.updateMultiReceiptLog(finReceiptQueue);

		logger.info(Literal.LEAVING);
	}

	@Override
	public FinReceiptData doReceiptValidations(FinanceDetail financeDetail, String method) {
		logger.debug("Entering");
		FinReceiptData receiptData = new FinReceiptData();
		receiptData.setFinanceDetail(financeDetail);
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		FinServiceInstruction fsi = toUpperCase(finScheduleData.getFinServiceInstruction());
		String finReference = fsi.getExternalReference();
		String entity = fsi.getEntity();
		String receiptSource = fsi.getReceiptSource();
		String parm1 = null;
		String recaginst = fsi.getRecAgainst();
		String receivedFrom = fsi.getReceivedFrom();
		String cif = fsi.getCustCIF();

		RECEIPT_MODES = PennantApplicationUtil.getActiveFieldCodeList(RepayConstants.RECEIPT_MODE);
		RECEIPT_CHANNELS = PennantApplicationUtil.getActiveFieldCodeList(RepayConstants.RECEIPT_CHANNEL);
		SUB_RECEIPT_MODES = PennantApplicationUtil.getActiveFieldCodeList(RepayConstants.SUB_RECEIPT_MODE);
		SUB_RECEIPT_SOURCES = PennantApplicationUtil.getActiveFieldCodeList(RepayConstants.RECEIPT_SOURCE);

		String receiptMode = fsi.getPaymentMode();
		if (SUB_RECEIPT_MODES.contains(receiptMode)) {
			fsi.setPaymentMode(ReceiptMode.ONLINE);
			fsi.setSubReceiptMode(receiptMode);
			if (StringUtils.equals(receiptMode, ReceiptMode.PORTAL)
					|| StringUtils.equals(receiptMode, ReceiptMode.BILLDESK)) {
				fsi.setReceiptChannel(DisbursementConstants.RECEIPT_CHANNEL_POR);
			}
		} else if (StringUtils.equals(receiptMode, ReceiptMode.CASH)
				|| StringUtils.equals(receiptMode, ReceiptMode.CHEQUE)
				|| StringUtils.equals(receiptMode, ReceiptMode.DD)) {
			fsi.setReceiptChannel("OTC");
		}

		if (StringUtils.isBlank(recaginst)) {
			recaginst = RepayConstants.NONLAN_RECEIPT_NOTAPPLICABLE;
			fsi.setRecAgainst(RepayConstants.NONLAN_RECEIPT_NOTAPPLICABLE);
		}

		if (recaginst.equals(RepayConstants.NONLAN_RECEIPT_CUSTOMER)) {

			if (StringUtils.isBlank(cif)) {
				finScheduleData = setErrorToFSD(finScheduleData, "90502", "CustomerCIF");
				return receiptData;
			}
		}

		/*
		 * else if (recaginst.equals(RepayConstants.NONLAN_RECEIPT_NOTAPPLICABLE)) { fsi.setRecAgainst(recaginst); }
		 */
		// set Default date formats
		setDefaultDateFormats(fsi);

		if (!(recaginst.equals(RepayConstants.NONLAN_RECEIPT_CUSTOMER)
				|| recaginst.equals(RepayConstants.NONLAN_RECEIPT_NOTAPPLICABLE))) {
			finScheduleData = setErrorToFSD(finScheduleData, "NC001", " '" + RepayConstants.NONLAN_RECEIPT_CUSTOMER
					+ "' or '" + RepayConstants.NONLAN_RECEIPT_NOTAPPLICABLE + "'");
			return receiptData;
		}
		/*
		 * if (StringUtils.isBlank(receivedFrom)) { finScheduleData = setErrorToFSD(finScheduleData, "90502",
		 * "ReceivedFrom"); return receiptData; }
		 */
		if (StringUtils.isBlank(finReference)) {
			finScheduleData = setErrorToFSD(finScheduleData, "90502", "Loan Reference");
			return receiptData;
		}

		if (StringUtils.isBlank(entity)) {
			finScheduleData = setErrorToFSD(finScheduleData, "90502", "Entity");
			return receiptData;
		}

		if (StringUtils.isBlank(receiptSource)) {
			finScheduleData = setErrorToFSD(finScheduleData, "90502", "receiptSource");
			return receiptData;
		}

		// Do First level Validation for Upload
		receiptData = doBasicValidations(receiptData);

		if (finScheduleData.getErrorDetails() != null && !finScheduleData.getErrorDetails().isEmpty()) {
			logger.debug("Leaving - Basic Validations Error");
			return receiptData;
		}

		// RECEIPT UPLOAD INQUIRY or API/Receipt Upload Post
		if (fsi.isReceiptUpload() && !StringUtils.equals(fsi.getReqType(), "Post")) {
			FinanceMain financeMain = financeMainDAO.getFinanceMainByRef(finReference, "_AView", false);
			finScheduleData.setFinanceMain(financeMain);
		} else {
			Cloner cloner = new Cloner();
			FinServiceInstruction tempFsi = cloner.deepClone(finScheduleData.getFinServiceInstruction());
			FinReceiptHeader rch = cloner.deepClone(receiptData.getReceiptHeader());

			if (finScheduleData.getErrorDetails() != null && !finScheduleData.getErrorDetails().isEmpty()) {
				logger.debug("Leaving");
				return receiptData;
			}
			financeDetail = receiptData.getFinanceDetail();
			receiptData.setReceiptHeader(rch);
			finScheduleData = financeDetail.getFinScheduleData();
			finScheduleData.setFinServiceInstruction(tempFsi);
		}

		receiptData = doDataValidations(receiptData);
		if (finScheduleData.getErrorDetails() != null && !finScheduleData.getErrorDetails().isEmpty()) {
			logger.debug("Leaving - Data Validations Error");
			return receiptData;
		}

		if (fsi.isReceiptUpload() && !StringUtils.equals(fsi.getReqType(), "Post")) {
			logger.debug("Leaving");
			return receiptData;
		}

		receiptData = doFunctionalValidations(receiptData);
		if (finScheduleData.getErrorDetails() != null && !finScheduleData.getErrorDetails().isEmpty()) {
			logger.debug("Leaving - Functional Validations Error");
			return receiptData;
		}

		receiptData = doBusinessValidations(receiptData);
		if (finScheduleData.getErrorDetails() != null && !finScheduleData.getErrorDetails().isEmpty()) {
			logger.debug("Leaving - Business Validations Error");
			return receiptData;
		}

		logger.debug("Leaving");
		return receiptData;
	}

	@Override
	public FinReceiptData doBasicValidations(FinReceiptData receiptData) {
		FinanceDetail financeDetail = receiptData.getFinanceDetail();
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		FinServiceInstruction fsi = finScheduleData.getFinServiceInstruction();

		String receiptMode = fsi.getPaymentMode();
		String subReceiptMode = fsi.getSubReceiptMode();
		String receiptChannel = fsi.getReceiptChannel();
		String receiptSource = fsi.getReceiptSource();
		String collectionAgency = fsi.getCollectionAgency();

		String instructstatus = fsi.getStatus();
		String allocationType = fsi.getAllocationType();
		String excessAdjustTo = fsi.getExcessAdjustTo();
		String recaginst = fsi.getRecAgainst();
		String entity = fsi.getEntity();
		String parm0 = null;
		String parm1 = null;

		// Valid Receipt Mode
		if (!RECEIPT_MODES.contains(receiptMode)) {
			parm0 = "Receipt mode";
			parm1 = RECEIPT_MODES.stream().collect(Collectors.joining(","));
			finScheduleData = setErrorToFSD(finScheduleData, "90281", parm0, parm1);
			return receiptData;
		}

		// Channel
		if (StringUtils.equals(receiptMode, ReceiptMode.CHEQUE) || StringUtils.equals(receiptMode, ReceiptMode.DD)
				|| StringUtils.equals(receiptMode, ReceiptMode.CASH)) {
			if (!RECEIPT_CHANNELS.contains(receiptChannel)) {
				parm0 = "Channel";
				parm1 = RECEIPT_CHANNELS.stream().collect(Collectors.joining(","));

				finScheduleData = setErrorToFSD(finScheduleData, "90281", parm0, parm1);
				return receiptData;
			}
		}

		// Sub Receipt Sub Mode
		if (StringUtils.equals(receiptMode, ReceiptMode.ONLINE)) {
			if (!SUB_RECEIPT_MODES.contains(subReceiptMode)) {
				parm0 = "Sub Receipt Mode";
				parm1 = SUB_RECEIPT_MODES.stream().collect(Collectors.joining(","));
				finScheduleData = setErrorToFSD(finScheduleData, "90281", parm0, parm1);
				return receiptData;
			}
		}

		// Receipt Source
		if (!SUB_RECEIPT_SOURCES.contains(receiptSource)) {
			parm0 = "Receipt Source";
			parm1 = SUB_RECEIPT_SOURCES.stream().collect(Collectors.joining(","));

			finScheduleData = setErrorToFSD(finScheduleData, "90281", parm0, parm1);
			return receiptData;
		}

		Entity entityObj = entityDAO.getEntity(entity, "");
		// Division
		if (StringUtils.isBlank(entity)) {
			finScheduleData = setErrorToFSD(finScheduleData, "90281", "Entity");
		} else {
			if (entityObj == null) {
				finScheduleData = setErrorToFSD(finScheduleData, "90281", "Entity", " Not available in System");
				return receiptData;
			}
		}

		long collectionAgentId = finReceiptHeaderDAO.getCollectionAgencyId(collectionAgency);
		// Collection Agency is mandatory When Receipt Source
		if (StringUtils.equals(receiptSource, RECEIPT_SOURCE_MOBILE)) {
			if (StringUtils.isBlank(collectionAgency)) {
				finScheduleData = setErrorToFSD(finScheduleData, "90502", "Collection Agency");
				return receiptData;
			} else {
				if (collectionAgentId == 0) {
					finScheduleData = setErrorToFSD(finScheduleData, "90281", "Collection Agency",
							" Not available in System");
					return receiptData;
				} else {
					fsi.setCollectionAgency(String.valueOf(collectionAgentId));
				}
			}

		} else {
			if (StringUtils.isNotBlank(collectionAgency)) {
				if (collectionAgentId == 0) {
					finScheduleData = setErrorToFSD(finScheduleData, "90281", "Collection Agency",
							" Not available in System");
					return receiptData;
				} else {
					fsi.setCollectionAgency(String.valueOf(collectionAgentId));
				}
			}
		}

		// Allocation Type
		if (StringUtils.isBlank(allocationType)) {
			allocationType = "N";
			fsi.setAllocationType(allocationType);
		} else {
			if (!StringUtils.equals(allocationType, "N")) {
				finScheduleData = setErrorToFSD(finScheduleData, "90281", "Allocation Type", "N");
				return receiptData;
			}
		}

		// Excess Adjust To
		if (StringUtils.isBlank(excessAdjustTo)) {
			excessAdjustTo = "N";
		} else {
			if (!StringUtils.equals(excessAdjustTo, "N")) {
				finScheduleData = setErrorToFSD(finScheduleData, "90281", "Excess Adjustment", "N");
				return receiptData;
			}
		}

		// Set Receipt Detail Record
		receiptData = setReceiptDetail(receiptData);
		FinReceiptDetail rcd = receiptData.getReceiptHeader().getReceiptDetails().get(0);
		if (rcd == null) {
			finScheduleData = setErrorToFSD(finScheduleData, "90502", "Receipt Details");
			return receiptData;
		}

		if (fsi.isReceiptUpload()) {
			if (fsi.getValueDate() == null) {
				finScheduleData = setErrorToFSD(finScheduleData, "90502", "ValueDate");
				return receiptData;
			}

			if (fsi.getValueDate().compareTo(fsi.getReceivedDate()) < 0) {
				finScheduleData = setErrorToFSD(finScheduleData, "RU0008", null);
				return receiptData;
			}
		}

		// Transaction Reference mandatory for all non CASH modes
		if (!StringUtils.equals(receiptMode, ReceiptMode.CASH)) {
			if (StringUtils.isBlank(rcd.getTransactionRef())) {
				finScheduleData = setErrorToFSD(finScheduleData, "90502", "Transaction Reference");
				return receiptData;
			}
		}

		// Funding account is mandatory for all modes except Cash
		if (!StringUtils.equals(receiptMode, ReceiptMode.CASH)) {
			if (rcd.getFundingAc() <= 0) {
				finScheduleData = setErrorToFSD(finScheduleData, "90502", "Funding Account");
				return receiptData;
			}
		}

		// RecAganist From
		if (recaginst.equals(RepayConstants.NONLAN_RECEIPT_CUSTOMER)) {
			Customer cust = (Customer) customerDAO.getCustomerByCIF(fsi.getCustCIF(), "_AView");
			if (cust == null) {
				finScheduleData = setErrorToFSD(finScheduleData, "NC002", "custCIF");
				return receiptData;
			}
			if (cust != null && cust.getId() != 0) {
				if (cust.getCustCIF() != null) {

					fsi.setCustCIF(cust.getCustCIF());
					fsi.setReference(String.valueOf(cust.getCustID()));
				}
			}
			fsi.setReceivedFrom(RepayConstants.RECEIVED_CUSTOMER);
			fsi.setRecAgainst(RepayConstants.NONLAN_RECEIPT_CUSTOMER);
			fsi.setExternalReference(fsi.getExternalReference());
		} else if (recaginst.equals(RepayConstants.NONLAN_RECEIPT_NOTAPPLICABLE)) {
			fsi.setReceivedFrom(RepayConstants.RECEIVED_NONLOAN);
			fsi.setRecAgainst(RepayConstants.NONLAN_RECEIPT_NOTAPPLICABLE);
			fsi.setReference(fsi.getExternalReference());
			fsi.setExternalReference(fsi.getExternalReference());
		} /*
			 * else { finScheduleData = setErrorToFSD(finScheduleData, "NC003", "ReceivedFrom"); return receiptData; }
			 */
		// Cheque OR DD
		if (StringUtils.equals(receiptMode, ReceiptMode.CHEQUE) || StringUtils.equals(receiptMode, ReceiptMode.DD)) {
			rcd.setFavourNumber(rcd.getTransactionRef());
			finScheduleData = validateForChequeOrDD(rcd, finScheduleData);
		} else if (StringUtils.equals(receiptMode, ReceiptMode.CASH)
				|| StringUtils.equals(receiptMode, ReceiptMode.ONLINE)) {
			// CASH OR ONLINE
			finScheduleData = validateForNonChequeOrDD(rcd, finScheduleData);
		}

		if (finScheduleData.getErrorDetails() != null && !finScheduleData.getErrorDetails().isEmpty()) {
			return receiptData;
		}

		// receiptData = validateBasicAllocations(receiptData, methodCtg);
		if (finScheduleData.getErrorDetails() != null && !finScheduleData.getErrorDetails().isEmpty()) {
			return receiptData;
		}

		// =======================================================================
		// Receipt Upload Related Code
		// =======================================================================

		if (!fsi.isReceiptUpload()) {
			return receiptData;
		}

		if (StringUtils.isBlank(instructstatus)) {
			finScheduleData = setErrorToFSD(finScheduleData, "90502", "Status");
			return receiptData;
		} else if (!StringUtils.equals(instructstatus, RepayConstants.PAYSTATUS_APPROVED)
				&& !StringUtils.equals(instructstatus, RepayConstants.PAYSTATUS_REALIZED)) {
			parm1 = RepayConstants.PAYSTATUS_APPROVED + "," + RepayConstants.PAYSTATUS_REALIZED;
			finScheduleData = setErrorToFSD(finScheduleData, "90298", "Status", parm1);
			return receiptData;
		} else if (!StringUtils.equals(receiptMode, ReceiptMode.DD)
				&& !StringUtils.equals(receiptMode, ReceiptMode.CHEQUE)) {

			if (StringUtils.equals(instructstatus, RepayConstants.PAYSTATUS_REALIZED)) {
				parm1 = RepayConstants.PAYSTATUS_APPROVED;
				finScheduleData = setErrorToFSD(finScheduleData, "90298", "Status", parm1);
				return receiptData;
			}
		}

		return receiptData;
	}

	@Override
	public FinReceiptData doDataValidations(FinReceiptData receiptData) {
		FinanceDetail financeDetail = receiptData.getFinanceDetail();
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		FinServiceInstruction fsi = finScheduleData.getFinServiceInstruction();

		// Entity Mismatch
		/*
		 * if (StringUtils.equals(financeMain.getEntityCode(), fsi.getEntity())) { finScheduleData =
		 * setErrorToFSD(finScheduleData, "RU004", fsi.getFinReference()); return receiptData; }
		 */

		// Partner Bank Validation against Loan Type, If not exists
		long fundingAccount = fsi.getReceiptDetail().getFundingAc();
		fsi.setFundingAc(fundingAccount);
		String receiptMode = fsi.getPaymentMode();

		if (!StringUtils.equals(receiptMode, ReceiptMode.CASH)
				&& !StringUtils.equals(fsi.getReqType(), RepayConstants.REQTYPE_INQUIRY)) {
			PartnerBank partnerBank = partnerBankDAO.getPartnerBankById(fundingAccount, "");
			if (partnerBank == null) {
				finScheduleData = setErrorToFSD(finScheduleData, "65040", "Funding Account is not available in system");
				return receiptData;
			} else {
				if (!partnerBank.getEntity().equals(fsi.getEntity())) {
					String msg = "For Entity: " + fsi.getEntity() + " Funding Account(Partner Bank): " + fundingAccount
							+ " is not available";
					finScheduleData = setErrorToFSD(finScheduleData, "65040", msg);
					return receiptData;
				}
				List<PartnerBankModes> partnerBankModes = partnerBankDAO.getPartnerBankModes(fundingAccount,
						RepayConstants.RECEIPTTYPE_RECIPT);
				if (partnerBankModes != null && CollectionUtils.isNotEmpty(partnerBankModes)) {
					PartnerBankModes partnerMode = null;
					if (StringUtils.equals(receiptMode, ReceiptMode.ONLINE)) {
						partnerMode = partnerBankModes.stream()
								.filter(mode -> fsi.getSubReceiptMode().equalsIgnoreCase(mode.getPaymentMode()))
								.findFirst().orElse(null);
					} else {
						partnerMode = partnerBankModes.stream()
								.filter(mode -> fsi.getPaymentMode().equalsIgnoreCase(mode.getPaymentMode()))
								.findFirst().orElse(null);
					}
					if (partnerMode == null) {
						String msg = "Payment Mode: " + fsi.getSubReceiptMode()
								+ " not configured for this funding Account(Partner Bank): " + fundingAccount;
						finScheduleData = setErrorToFSD(finScheduleData, "65040", msg);
						return receiptData;
					}
				} else {
					String msg = "Payment Mode: " + fsi.getSubReceiptMode()
							+ " not configured for this funding Account(Partner Bank): " + fundingAccount;
					finScheduleData = setErrorToFSD(finScheduleData, "65040", msg);
					return receiptData;
				}
				fsi.getReceiptDetail().setPartnerBankAc(partnerBank.getAccountNo());
				fsi.getReceiptDetail().setPartnerBankAcType(partnerBank.getAcType());
			}
		}

		return receiptData;
	}

	@Override
	public FinReceiptData doFunctionalValidations(FinReceiptData receiptData) {
		FinanceDetail financeDetail = receiptData.getFinanceDetail();
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		FinServiceInstruction fsi = finScheduleData.getFinServiceInstruction();
		FinReceiptDetail rcd = fsi.getReceiptDetail();

		Date appDate = SysParamUtil.getAppDate();

		// receiptData = validateRecalType(receiptData, methodCtg);
		if (finScheduleData.getErrorDetails() != null && !finScheduleData.getErrorDetails().isEmpty()) {
			logger.debug("Leaving");
			return receiptData;
		}

		Date fromDate = fsi.getFromDate();
		if (fromDate == null) {
			fromDate = appDate;
			fsi.setFromDate(fromDate);
		}

		// remarks
		if (!StringUtils.isBlank(rcd.getRemarks()) && 100 < rcd.getRemarks().length()) {
			finScheduleData = setErrorToFSD(finScheduleData, "RU0005", null);
			return receiptData;
		}

		logger.debug("Leaving");
		return receiptData;
	}

	public FinServiceInstruction toUpperCase(FinServiceInstruction fsi) {
		if (StringUtils.isNotBlank(fsi.getPaymentMode())) {
			fsi.setPaymentMode(fsi.getPaymentMode().toUpperCase());
		}

		if (StringUtils.isNotBlank(fsi.getAllocationType())) {
			fsi.setAllocationType(fsi.getAllocationType().toUpperCase());
		}

		if (StringUtils.isNotBlank(fsi.getStatus())) {
			fsi.setStatus(fsi.getStatus().toUpperCase());
		}

		if (StringUtils.isNotBlank(fsi.getRecalType())) {
			fsi.setRecalType(fsi.getRecalType().toUpperCase());
		}

		if (StringUtils.isNotBlank(fsi.getExcessAdjustTo())) {
			fsi.setExcessAdjustTo(fsi.getExcessAdjustTo().toUpperCase());
		}

		if (StringUtils.isNotBlank(fsi.getSubReceiptMode())) {
			fsi.setSubReceiptMode(fsi.getSubReceiptMode().toUpperCase());
		}

		if (StringUtils.isNotBlank(fsi.getReceiptChannel())) {
			fsi.setReceiptChannel(fsi.getReceiptChannel());
		}

		if (StringUtils.isNotBlank(fsi.getReceivedFrom())) {
			fsi.setReceivedFrom(fsi.getReceivedFrom().toUpperCase());
		}

		if (StringUtils.isNotBlank(fsi.getPanNumber())) {
			fsi.setPanNumber(fsi.getPanNumber().toUpperCase());
		}

		return fsi;
	}

	private void setDefaultDateFormats(FinServiceInstruction fsi) {
		fsi.setFromDate(DateUtil.getDatePart(fsi.getFromDate()));
		fsi.setToDate(DateUtil.getDatePart(fsi.getToDate()));
		fsi.setRecalFromDate(DateUtil.getDatePart(fsi.getRecalFromDate()));
		fsi.setRecalToDate(DateUtil.getDatePart(fsi.getRecalToDate()));
		fsi.setGrcPeriodEndDate(DateUtil.getDatePart(fsi.getGrcPeriodEndDate()));
		fsi.setNextGrcRepayDate(DateUtil.getDatePart(fsi.getNextGrcRepayDate()));
		fsi.setNextRepayDate(DateUtil.getDatePart(fsi.getNextRepayDate()));
		fsi.setReceivedDate(DateUtil.getDatePart(fsi.getReceivedDate()));
		fsi.setValueDate(DateUtil.getDatePart(fsi.getValueDate()));
		fsi.setDepositDate(DateUtil.getDatePart(fsi.getDepositDate()));
		fsi.setRealizationDate(DateUtil.getDatePart(fsi.getRealizationDate()));
		fsi.setInstrumentDate(DateUtil.getDatePart(fsi.getInstrumentDate()));
	}

	@Override
	public FinScheduleData setErrorToFSD(FinScheduleData finScheduleData, String errorCode, String parm0) {
		ErrorDetail errorDetail = new ErrorDetail();
		String[] valueParm = new String[1];
		valueParm[0] = parm0;
		errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail(errorCode, "", valueParm));
		finScheduleData.setErrorDetail(errorDetail);
		return finScheduleData;
	}

	@Override
	public FinScheduleData setErrorToFSD(FinScheduleData finScheduleData, String errorCode, String parm0,
			String parm1) {
		ErrorDetail errorDetail = new ErrorDetail();
		String[] valueParm = new String[2];
		valueParm[0] = parm0;
		valueParm[1] = parm1;
		errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail(errorCode, "", valueParm));
		finScheduleData.setErrorDetail(errorDetail);
		return finScheduleData;
	}

	@Override
	public FinScheduleData setErrorToFSD(FinScheduleData finScheduleData, String errorCode, String parm0, String parm1,
			String parm2) {
		ErrorDetail errorDetail = new ErrorDetail();
		String[] valueParm = new String[3];
		valueParm[0] = parm0;
		valueParm[1] = parm1;
		valueParm[2] = parm2;
		errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail(errorCode, "", valueParm));
		finScheduleData.setErrorDetail(errorDetail);
		return finScheduleData;
	}

	@Override
	public FinScheduleData setErrorToFSD(FinScheduleData finScheduleData, String errorCode, String parm0, String parm1,
			String parm2, String parm3) {
		ErrorDetail errorDetail = new ErrorDetail();
		String[] valueParm = new String[4];
		valueParm[0] = parm0;
		valueParm[1] = parm1;
		valueParm[2] = parm2;
		valueParm[3] = parm3;
		errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail(errorCode, "", valueParm));
		finScheduleData.setErrorDetail(errorDetail);
		return finScheduleData;
	}

	@Override
	public FinReceiptData doBusinessValidations(FinReceiptData receiptData) {
		FinanceDetail financeDetail = receiptData.getFinanceDetail();
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		FinServiceInstruction fsi = finScheduleData.getFinServiceInstruction();

		String finReference = fsi.getFinReference();
		String parm0 = null;

		// Validate duplicate record
		if (StringUtils.equals(fsi.getReqType(), "Post")) {
			String txnReference = fsi.getTransactionRef();
			BigDecimal receiptAmount = fsi.getAmount();
			boolean dedupFound = finReceiptDetailDAO.isDuplicateReceipt(finReference, txnReference, receiptAmount);
			if (dedupFound) {
				parm0 = "Txn Reference: " + txnReference + " with Amount";
				finScheduleData = setErrorToFSD(finScheduleData, "90273", parm0);
				return receiptData;
			}
		}

		logger.debug("Leaving");
		return receiptData;
	}

	@Override
	public FinReceiptData setReceiptDetail(FinReceiptData receiptData) {
		FinanceDetail financeDetail = receiptData.getFinanceDetail();
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		FinServiceInstruction fsi = finScheduleData.getFinServiceInstruction();
		receiptData.setReceiptHeader(new FinReceiptHeader());
		FinReceiptHeader rch = receiptData.getReceiptHeader();
		FinReceiptDetail rcd = fsi.getReceiptDetail();

		rcd.setReceiptType(RepayConstants.NONLAN_RECEIPT_NOTAPPLICABLE);
		rcd.setPaymentTo(RepayConstants.NONLAN_RECEIPT_NOTAPPLICABLE);
		if (StringUtils.equals(fsi.getPaymentMode(), ReceiptMode.ONLINE)) {
			rcd.setPaymentType(fsi.getSubReceiptMode());
		} else {
			rcd.setPaymentType(fsi.getPaymentMode());
		}
		rcd.setAmount(fsi.getAmount());
		rch.getReceiptDetails().add(rcd);
		return receiptData;
	}

	public FinScheduleData validateForChequeOrDD(FinReceiptDetail receiptDetail, FinScheduleData finScheduleData) {
		// Bank Code is Mandatory
		if (StringUtils.isBlank(receiptDetail.getBankCode())) {
			finScheduleData = setErrorToFSD(finScheduleData, "90502", "BankCode");
			return finScheduleData;
		}

		// Bank Details should be configured
		BankDetail bankDetail = bankDetailService.getBankDetailById(receiptDetail.getBankCode());
		if (bankDetail == null) {
			finScheduleData = setErrorToFSD(finScheduleData, "90224", "BankCode", receiptDetail.getBankCode());
			return finScheduleData;
		}

		// Value Date must be present
		if (receiptDetail.getValueDate() == null) {
			finScheduleData = setErrorToFSD(finScheduleData, "90502", "ValueDate");
			return finScheduleData;
		}

		// Favour Name is mandatory
		if (StringUtils.isBlank(receiptDetail.getFavourName())) {
			finScheduleData = setErrorToFSD(finScheduleData, "90502", "FavourName");
			return finScheduleData;
		}

		return finScheduleData;
	}

	public FinScheduleData validateForNonChequeOrDD(FinReceiptDetail receiptDetail, FinScheduleData finScheduleData) {

		String parm1 = ReceiptMode.CASH + "," + ReceiptMode.ONLINE;
		boolean isReceiptUpload = finScheduleData.getFinServiceInstruction().isReceiptUpload();

		// Value Date must not be sent
		if (!isReceiptUpload && receiptDetail.getValueDate() != null) {
			finScheduleData = setErrorToFSD(finScheduleData, "90329", "ValueDate", parm1);
			return finScheduleData;
		}

		// Favour Name must be blank
		if (StringUtils.isNotBlank(receiptDetail.getFavourName())) {
			finScheduleData = setErrorToFSD(finScheduleData, "90329", "FavourName", parm1);
			return finScheduleData;
		}

		// Bank code must be blank
		if (StringUtils.isNotBlank(receiptDetail.getBankCode())) {
			finScheduleData = setErrorToFSD(finScheduleData, "90329", "BankCode", parm1);
			return finScheduleData;
		}
		return finScheduleData;
	}

	@Override
	public FinReceiptData setReceiptData(FinReceiptData receiptData) {
		logger.debug(Literal.ENTERING);
		FinanceDetail financeDetail = receiptData.getFinanceDetail();
		FinScheduleData fsd = financeDetail.getFinScheduleData();
		FinServiceInstruction fsi = fsd.getFinServiceInstruction();
		String extReference = fsi.getExternalReference();
		String reference = fsi.getReference();
		receiptData.setFinReference(extReference);
		String receivedFrom = fsi.getReceivedFrom();
		FinReceiptHeader rch = receiptData.getReceiptHeader();
		FinReceiptDetail rcd = rch.getReceiptDetails().get(0);

		LoggedInUser userDetails = null;
		if (SessionUserDetails.getLogiedInUser() != null) {
			userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		} else {
			userDetails = fsi.getLoggedInUser();
		}
		receiptData.setUserDetails(userDetails);
		rch.setReference(reference);
		rch.setExcessAdjustTo(RepayConstants.NONLAN_RECEIPT_NOTAPPLICABLE);
		rch.setReceiptType(RepayConstants.NONLAN_RECEIPT_NOTAPPLICABLE);
		rch.setRecAgainst(fsi.getRecAgainst());
		rch.setReceiptDate(SysParamUtil.getAppDate());
		rch.setReceiptPurpose(RepayConstants.NONLAN_RECEIPT_NOTAPPLICABLE);
		rch.setEffectSchdMethod(fsi.getRecalType());
		rch.setExcessAdjustTo(fsi.getExcessAdjustTo());
		rch.setAllocationType(RepayConstants.NONLAN_RECEIPT_NOTAPPLICABLE);
		rch.setReceiptAmount(fsi.getAmount());
		rch.setReceiptMode(fsi.getPaymentMode());
		rch.setSubReceiptMode(fsi.getSubReceiptMode());
		rch.setReceiptChannel(fsi.getReceiptChannel());
		rch.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		rch.setNewRecord(true);
		rch.setLastMntBy(userDetails.getLoginLogId());
		rch.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		rch.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		rch.setUserDetails(userDetails);
		rch.setReceiptDate(rcd.getReceivedDate());
		rch.setRemarks(rcd.getRemarks());
		rch.setValueDate(rcd.getReceivedDate());
		rch.setReceiptSource(fsi.getReceiptSource());
		rch.setFinDivision(fsi.getDivision());
		rch.setReceivedFrom(receivedFrom);
		rch.setExtReference(fsi.getExternalReference());

		String paymentType = rcd.getPaymentType();
		if (ReceiptMode.CHEQUE.equals(paymentType) || ReceiptMode.DD.equals(paymentType)) {
			rch.setTransactionRef(rcd.getFavourNumber());
		} else {
			rch.setTransactionRef(rcd.getTransactionRef());
		}

		try {
			String accType = finReceiptDetailDAO.getReceiptSourceAccType(fsi.getReceiptSource());
			rch.setReceiptSourceAcType(accType);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			fsd = setErrorToFSD(fsd, "65040", "Account Type is not configured for Receipt Source");
			return receiptData;
		}

		if (StringUtils.isBlank(fsi.getCollectionAgency())) {
			rch.setCollectionAgentId(0);
		} else {
			rch.setCollectionAgentId(Long.valueOf(fsi.getCollectionAgency()));
		}

		rch.setPostBranch(userDetails.getBranchCode());
		rch.setCashierBranch(userDetails.getBranchCode());
		rch.setEntityCode(fsi.getEntity());
		receiptData.setSourceId(PennantConstants.FINSOURCE_ID_API);

		rch.setAllocationType(fsi.getAllocationType());
		rch.setRealizationDate(fsi.getRealizationDate());
		rch.setReceiptModeStatus(RepayConstants.PAYSTATUS_REALIZED);

		/* rch.setRemarks(fsi.getRemarks()); */
		rch.setDepositDate(SysParamUtil.getAppDate());
		rcd.setDepositDate(rch.getDepositDate());

		logger.debug(Literal.LEAVING);
		return receiptData;
	}

	private JSONObject prepareJsonObject(FinReceiptHeader rh) {
		JSONObject reqObj = new JSONObject();
		reqObj.put("agencycode", rh.getCollectionAgentCode());
		reqObj.put("receiptAmount", rh.getReceiptAmount());
		reqObj.put("extReference", rh.getReceiptID());
		reqObj.put("receiptType", rh.getReceiptType());
		reqObj.put("receiptMode", rh.getReceiptMode());
		return reqObj;

	}

	private void updateMobileAgencyLimit(FinReceiptHeader rh, String tranType, boolean retry) {
		JSONObject reqObj = prepareJsonObject(rh);

		String stage = null;
		if ("D".equals(tranType)) {
			reqObj.put("transactionType", "D");
			reqObj.put("receiptModeStatus", "C");
			reqObj.put("reference", rh.getReference());
			stage = COLLECTION_API_CANCEL;
		} else {
			reqObj.put("reference", rh.getReference());
			reqObj.put("receiptPurpose", rh.getReceiptPurpose());
			reqObj.put("allocationType", "M");
			reqObj.put("paymentSource", rh.getReceiptSource());
			reqObj.put("gDRAvailable", false);
			reqObj.put("receiptTab", "");
			reqObj.put("transactionType", "C");
			stage = COLLECTION_API_APPROVER;
		}

		ReceiptAPIRequest request = null;

		try {
			request = updateMobileAgencyLimit(stage, reqObj);
			request.setReceiptId(rh.getReceiptID());
			request.setRetryCount(0);
		} catch (Exception e) {
			throw e;
		} finally {

			if (!retry) {
				long id = finReceiptHeaderDAO.saveCollectionAPILog(request);
				request.setID(id);
			} else {
				request.setRetryOn(DateUtil.getSysDate());
				if (request != null) {
					finReceiptHeaderDAO.updateCollectionMobAgencyLimit(request);
				}
			}

		}

	}

	private ReceiptAPIRequest updateMobileAgencyLimit(String stage, JSONObject reqObj) {
		ReceiptAPIRequest request = new ReceiptAPIRequest();

		String requestBody = reqObj.toString();
		logger.debug("Request Data {}", requestBody);

		WebClient client = null;
		String url = null;
		String serviceName = null;
		String version = null;

		if (COLLECTION_API_CANCEL.equals(stage)) {
			url = UPDATE_URL;
			serviceName = "updateLimitAgecyCode";
			version = UPDATE_VERSION;
		} else {
			url = CREATE_URL;
			serviceName = "createReceipt";
			version = CREATE_VERSION;
		}

		InterfaceLogDetail log = new InterfaceLogDetail();
		log.setEndPoint(url);
		log.setReference(reqObj.getString("reference"));
		log.setServiceName(serviceName);

		if (StringUtils.equalsIgnoreCase("Y", App.getProperty("external.interface.fulllog"))) {
			log.setRequest(requestBody);
		} else {
			log.setRequest(StringUtils.left(requestBody, 1000));
		}
		log.setReqSentOn(new Timestamp(System.currentTimeMillis()));
		request.setRequestTime(log.getReqSentOn());

		try {
			client = getClient(url, serviceName, version);
		} catch (Exception e) {
			throw e;
		} finally {
			interfaceLoggingDAO.save(log);
			request.setMessageId(log.getSeqId());
		}

		Response response = null;

		String errorCode = "0000";
		String errorDesc = null;
		String status = InterfaceConstants.STATUS_SUCCESS;

		List<Object> returnCode = new ArrayList<>();
		try {
			logger.debug("Sending the limits update request to mobile agency with request data {}", requestBody);
			response = client.post(requestBody);
		} catch (Exception e) {
			errorCode = "8900";
			errorDesc = e.getMessage();
			status = InterfaceConstants.STATUS_FAILED;
			logger.error(Literal.EXCEPTION, e);
			throw new InterfaceException("999", "Unable to create web client to access the mobile agency API ", e);
		} finally {
			String responseBody = "";
			if (response != null) {
				responseBody = response.readEntity(String.class);
				returnCode = response.getMetadata().get("ReturnCode");
			}

			if (StringUtils.equalsIgnoreCase("Y", App.getProperty("external.interface.fulllog"))) {
				log.setResponse(responseBody);
			} else {
				log.setResponse(StringUtils.left(responseBody, 1000));
			}

			request.setResponseCode(log.getResponse());
			log.setRespReceivedOn(new Timestamp(System.currentTimeMillis()));
			log.setStatus(status);
			log.setErrorCode(errorCode);
			log.setErrorDesc(StringUtils.left(StringUtils.trimToEmpty(errorDesc), 200));

			interfaceLoggingDAO.update(log);

			if (CollectionUtils.isNotEmpty(returnCode)) {
				request.setStatus(UploadConstants.UPLOAD_STATUS_SUCCESS);
			} else {
				request.setStatus(UploadConstants.UPLOAD_STATUS_FAIL);
			}

		}

		return request;
	}

	private WebClient getClient(String url, String header, String version) {
		String messageId = String.valueOf(Math.random());
		logger.debug("Creating web client for mobile agency API URL {} with message-id {}", url, messageId);

		WebClient client = null;
		try {

			client = WebClient.create(url);
			client.accept(MediaType.APPLICATION_JSON);
			client.type(MediaType.APPLICATION_JSON);
			client.header(BatchUploadProcessorConstatnt.AUTHORIZATION_KEY, AUTHORIZATION_KEY);
			client.header(BatchUploadProcessorConstatnt.MESSAGE_ID, messageId);
			client.header(REQUEST_TIME, DateUtil.getSysDate(PennantConstants.APIDateFormatter));

			client.header(HEADER_VERSION, version);
			client.header(ENTITY_ID, ENTITY);
			client.header(LANGUAGE, "ENG");

			client.header(HEADER_NAME, header);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw new InterfaceException("999", "Unable to create web client to access the mobile agency API ", e);
		}
		return client;
	}

	@Override
	public List<ReturnDataSet> getPostingsByPostRefAndFinEvent(String postRef, String finEvent) {
		return postingsDAO.getPostings(postRef, finEvent);
	}

	@Override
	public void processCollectionAPILog() {
		logger.debug(Literal.ENTERING);

		List<ReceiptAPIRequest> logList = finReceiptHeaderDAO.getCollectionAPILog();
		for (ReceiptAPIRequest finReceiptHeaderAPILog : logList) {
			FinReceiptHeader rch = finReceiptHeaderDAO.getNonLanReceiptHeader(finReceiptHeaderAPILog.getReceiptId(),
					"_AView");

			if ("R".equals(rch.getReceiptModeStatus())) {
				updateMobileAgencyLimit(rch, "C", true);
			} else {
				updateMobileAgencyLimit(rch, "D", true);
			}

		}
		logger.debug(Literal.LEAVING);
	}

	public void setFinReceiptHeaderDAO(FinReceiptHeaderDAO finReceiptHeaderDAO) {
		this.finReceiptHeaderDAO = finReceiptHeaderDAO;
	}

	public void setFinReceiptDetailDAO(FinReceiptDetailDAO finReceiptDetailDAO) {
		this.finReceiptDetailDAO = finReceiptDetailDAO;
	}

	public void setPartnerBankDAO(PartnerBankDAO partnerBankDAO) {
		this.partnerBankDAO = partnerBankDAO;
	}

	public void setReceiptCancellationService(ReceiptCancellationService receiptCancellationService) {
		this.receiptCancellationService = receiptCancellationService;
	}

	public void setEntityDAO(EntityDAO entityDAO) {
		this.entityDAO = entityDAO;
	}

	public void setBankDetailService(BankDetailService bankDetailService) {
		this.bankDetailService = bankDetailService;
	}

	public void setAccountingSetDAO(AccountingSetDAO accountingSetDAO) {
		this.accountingSetDAO = accountingSetDAO;
	}

	public void setBranchCashDetailDAO(BranchCashDetailDAO branchCashDetailDAO) {
		this.branchCashDetailDAO = branchCashDetailDAO;
	}

	public void setInterfaceLoggingDAO(InterfaceLoggingDAO interfaceLoggingDAO) {
		this.interfaceLoggingDAO = interfaceLoggingDAO;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

}
