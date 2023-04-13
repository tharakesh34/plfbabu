package com.pennant.backend.service.finance.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.dao.receipts.FinReceiptDetailDAO;
import com.pennant.backend.dao.receipts.FinReceiptHeaderDAO;
import com.pennant.backend.dao.receipts.ReceiptAllocationDetailDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldExtension;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinReceiptData;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinRepayHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.extendedfields.ExtendedFieldDetailsService;
import com.pennant.backend.service.extendedfieldsExtension.ExtendedFieldExtensionService;
import com.pennant.backend.service.finance.ReceiptRealizationService;
import com.pennant.backend.util.ExtendedFieldConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.RepayConstants;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.receipt.constants.ReceiptMode;
import com.pennapps.core.util.ObjectUtil;

public class ReceiptRealizationServiceImpl extends GenericService<FinReceiptHeader>
		implements ReceiptRealizationService {
	private static final Logger logger = LogManager.getLogger(ReceiptRealizationServiceImpl.class);

	private FinReceiptHeaderDAO finReceiptHeaderDAO;
	private FinReceiptDetailDAO finReceiptDetailDAO;
	private FinanceMainDAO financeMainDAO;
	private FinODDetailsDAO finODDetailsDAO;
	private ManualAdviseDAO manualAdviseDAO;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private ReceiptAllocationDetailDAO allocationDetailDAO;
	private FinExcessAmountDAO finExcessAmountDAO;
	private AuditHeaderDAO auditHeaderDAO;
	private ExtendedFieldDetailsService extendedFieldDetailsService;
	private ExtendedFieldExtensionService extendedFieldExtensionService;

	public ReceiptRealizationServiceImpl() {
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
		FinReceiptHeader receiptHeader = null;
		receiptHeader = finReceiptHeaderDAO.getReceiptHeaderByID(receiptID, "_View");

		// Fetch Receipt Detail List
		if (receiptHeader != null) {
			List<FinReceiptDetail> receiptDetailList = finReceiptDetailDAO.getReceiptHeaderByID(receiptID, "_AView");
			receiptHeader.setReceiptDetails(receiptDetailList);
		}

		logger.debug(Literal.LEAVING);
		return receiptHeader;
	}

	@Override
	public AuditHeader saveOrUpdate(AuditHeader aAuditHeader) throws AppException {
		logger.debug(Literal.ENTERING);

		aAuditHeader = businessValidation(aAuditHeader, "saveOrUpdate");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		if (!aAuditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return aAuditHeader;
		}

		AuditHeader auditHeader = ObjectUtil.clone(aAuditHeader);
		FinReceiptHeader receiptHeader = (FinReceiptHeader) auditHeader.getAuditDetail().getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (receiptHeader.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		// Receipt Header Details Save And Update
		// =======================================
		if (receiptHeader.isNewRecord()) {
			receiptHeader.setReceiptModeStatus(RepayConstants.PAYSTATUS_REALIZED);
			finReceiptHeaderDAO.save(receiptHeader, tableType);
		} else {
			finReceiptHeaderDAO.update(receiptHeader, tableType);
		}

		String[] fields = PennantJavaUtil.getFieldDetails(new FinReceiptHeader(), receiptHeader.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
				receiptHeader.getBefImage(), receiptHeader));

		auditHeader.setAuditDetails(auditDetails);
		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
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
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}
		FinReceiptHeader receiptHeader = (FinReceiptHeader) auditHeader.getAuditDetail().getModelData();

		// Delete Receipt Header
		finReceiptHeaderDAO.deleteByReceiptID(receiptHeader.getReceiptID(), TableType.TEMP_TAB);

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		String[] fields = PennantJavaUtil.getFieldDetails(new FinReceiptHeader(), receiptHeader.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
				receiptHeader.getBefImage(), receiptHeader));
		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
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
	 */
	@Override
	public AuditHeader doApprove(AuditHeader aAuditHeader) {
		logger.debug(Literal.ENTERING);

		String tranType = "";
		aAuditHeader = businessValidation(aAuditHeader, "doApprove");
		if (!aAuditHeader.isNextProcess()) {
			return aAuditHeader;
		}

		FinReceiptData orgReceiptData = (FinReceiptData) aAuditHeader.getAuditDetail().getModelData();
		AuditHeader auditHeader = ObjectUtil.clone(aAuditHeader);
		FinReceiptData rd = (FinReceiptData) auditHeader.getAuditDetail().getModelData();
		FinReceiptHeader rch = rd.getReceiptHeader();

		FinanceDetail fd = rd.getFinanceDetail();
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		long serviceUID = Long.MIN_VALUE;
		if (fd.getExtendedFieldRender() != null) {
			serviceUID = extendedFieldDetailsService.getInstructionUID(fd.getExtendedFieldRender(),
					fd.getExtendedFieldExtension());
		}

		// Receipt Header Updation
		// =======================================
		tranType = PennantConstants.TRAN_UPD;
		rch.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		rch.setRcdMaintainSts(null);
		rch.setRecordType("");
		rch.setRoleCode("");
		rch.setNextRoleCode("");
		rch.setTaskId("");
		rch.setNextTaskId("");
		rch.setWorkflowId(0);
		finReceiptHeaderDAO.update(rch, TableType.MAIN_TAB);

		// Update Receipt Details based on Receipt Mode
		for (int i = 0; i < rch.getReceiptDetails().size(); i++) {
			FinReceiptDetail receiptDetail = rch.getReceiptDetails().get(i);
			if (StringUtils.equals(receiptDetail.getPaymentType(), ReceiptMode.CHEQUE)
					|| StringUtils.equals(receiptDetail.getPaymentType(), ReceiptMode.DD)) {
				finReceiptDetailDAO.updateReceiptStatus(receiptDetail.getReceiptID(), receiptDetail.getReceiptSeqID(),
						RepayConstants.PAYSTATUS_REALIZED);
				break;
			}
		}

		// Making Finance Inactive Incase of Schedule Payment
		long finID = rch.getFinID();
		if (StringUtils.equals(rch.getReceiptPurpose(), FinServiceEvent.SCHDRPY)) {
			List<FinanceScheduleDetail> schdList = financeScheduleDetailDAO.getFinScheduleDetails(finID, "", false);
			if (isSchdFullyPaid(finID, schdList)) {
				financeMainDAO.updateMaturity(finID, FinanceConstants.CLOSE_STATUS_MATURED, false, null);
			}
		}

		// updating fixexcess amount after realization
		if (StringUtils.equals(rch.getReceiptPurpose(), FinServiceEvent.SCHDRPY)) {
			FinRepayHeader rph = rch.getReceiptDetails().get(0).getRepayHeader();
			if (rph != null && rph.getExcessAmount().compareTo(BigDecimal.ZERO) > 0) {
				long receiptID = rch.getReceiptID();
				finExcessAmountDAO.updExcessAfterRealize(finID, rch.getExcessAdjustTo(), rph.getExcessAmount(),
						receiptID);
			}
		}

		// Delete Save Receipt Detail List by Reference
		long receiptID = rch.getReceiptID();
		finReceiptDetailDAO.deleteByReceiptID(receiptID, TableType.TEMP_TAB);

		// Receipt Allocation Details
		allocationDetailDAO.deleteByReceiptID(receiptID, TableType.TEMP_TAB);

		// Delete Manual Advise Movements
		manualAdviseDAO.deleteMovementsByReceiptID(receiptID, TableType.TEMP_TAB.getSuffix());

		// Delete Receipt Header
		finReceiptHeaderDAO.deleteByReceiptID(receiptID, TableType.TEMP_TAB);

		// Preparing Before Image for Audit
		FinReceiptHeader befRctHeader = null;
		if (!PennantConstants.RECORD_TYPE_NEW.equals(orgReceiptData.getReceiptHeader().getRecordType())) {
			befRctHeader = (FinReceiptHeader) aAuditHeader.getAuditDetail().getBefImage();
		}
		List<FinReceiptDetail> befFinReceiptDetail = new ArrayList<>();
		if (befRctHeader != null) {
			befFinReceiptDetail = befRctHeader.getReceiptDetails();
		}

		// Audit Header for WorkFlow Image
		List<AuditDetail> tempAuditDetailList = new ArrayList<>();
		// FinReceiptDetail Audit Details Preparation
		String[] rFields = PennantJavaUtil.getFieldDetails(new FinReceiptDetail(),
				rd.getReceiptHeader().getReceiptDetails().get(0).getExcludeFields());
		for (int i = 0; i < rd.getReceiptHeader().getReceiptDetails().size(); i++) {
			tempAuditDetailList.add(new AuditDetail(aAuditHeader.getAuditTranType(), 1, rFields[0], rFields[1], null,
					orgReceiptData.getReceiptHeader().getReceiptDetails().get(i)));
		}

		// Receipt Header Audit Details Preparation
		String[] rhFields = PennantJavaUtil.getFieldDetails(new FinReceiptHeader(),
				rd.getReceiptHeader().getExcludeFields());
		aAuditHeader.setAuditDetail(new AuditDetail(aAuditHeader.getAuditTranType(), 1, rhFields[0], rhFields[1], null,
				orgReceiptData.getReceiptHeader()));

		// Adding audit as deleted from TEMP table
		aAuditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		aAuditHeader.setAuditDetails(tempAuditDetailList);
		aAuditHeader.setAuditModule("Receipt");
		auditHeaderDAO.addAudit(aAuditHeader);

		if (orgReceiptData.getReceiptHeader().getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
			tranType = PennantConstants.TRAN_ADD;
		} else {
			tranType = PennantConstants.TRAN_UPD;
		}

		// Audit Header for Before and After Image
		List<AuditDetail> auditDetails = new ArrayList<>();
		// FinReceiptDetail Audit Details Preparation
		if (befFinReceiptDetail.isEmpty()) {
			for (int i = 0; i < rd.getReceiptHeader().getReceiptDetails().size(); i++) {
				auditDetails.add(new AuditDetail(tranType, 1, rFields[0], rFields[1], null,
						rd.getReceiptHeader().getReceiptDetails().get(i)));
			}
		} else {
			for (int i = 0; i < rd.getReceiptHeader().getReceiptDetails().size(); i++) {
				auditDetails.add(new AuditDetail(tranType, 1, rFields[0], rFields[1], befFinReceiptDetail.get(i),
						rd.getReceiptHeader().getReceiptDetails().get(i)));
			}
		}

		// Extended Field Details
		List<AuditDetail> extendedDetails = fd.getAuditDetailMap().get("ExtendedFieldDetails");
		if (extendedDetails != null && extendedDetails.size() > 0) {
			extendedDetails = extendedFieldDetailsService.processingExtendedFieldDetailList(extendedDetails,
					ExtendedFieldConstants.MODULE_LOAN, fd.getExtendedFieldHeader().getEvent(), "", serviceUID);
			auditDetails.addAll(extendedDetails);
		}

		// Extended field Extensions Details
		List<AuditDetail> extensionDetails = fd.getAuditDetailMap().get("ExtendedFieldExtension");
		if (extensionDetails != null && extensionDetails.size() > 0) {
			extensionDetails = extendedFieldExtensionService.processingExtendedFieldExtList(extensionDetails, rd,
					serviceUID, TableType.MAIN_TAB);

			auditDetails.addAll(extensionDetails);
		}

		// FinReceiptHeader Audit
		auditHeader.setAuditDetail(
				new AuditDetail(tranType, 1, rhFields[0], rhFields[1], befRctHeader, rd.getReceiptHeader()));

		// Adding audit as Insert/Update/deleted into main table
		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.setAuditDetails(auditDetails);
		auditHeader.setAuditModule("Receipt");
		auditHeaderDAO.addAudit(auditHeader);
		// Reset Finance Detail Object for Service Task Verifications
		// receiptData.getFinanceDetail().getFinScheduleData().setFinanceMain(financeMain);

		// Extended field Render Details Delete from temp.
		if (extendedDetails != null && extendedDetails.size() > 0) {
			extendedDetails = extendedFieldDetailsService.delete(fd.getExtendedFieldHeader(), fm.getFinReference(),
					fd.getExtendedFieldRender().getSeqNo(), "_Temp", auditHeader.getAuditTranType(), extendedDetails);
			auditDetails.addAll(extendedDetails);
		}

		if (extensionDetails != null && extensionDetails.size() > 0) {
			extensionDetails = extendedFieldExtensionService.delete(extensionDetails, tranType, TableType.TEMP_TAB);
			auditDetails.addAll(extensionDetails);
		}

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * Method for Checking Schedule is Fully Paid or not
	 * 
	 * @param finReference
	 * @param scheduleDetails
	 * @return
	 */
	private boolean isSchdFullyPaid(long finID, List<FinanceScheduleDetail> scheduleDetails) {
		// Check Total Finance profit Amount
		boolean fullyPaid = true;
		for (int i = 1; i < scheduleDetails.size(); i++) {
			FinanceScheduleDetail curSchd = scheduleDetails.get(i);

			// Profit
			if ((curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid())).compareTo(BigDecimal.ZERO) > 0) {
				fullyPaid = false;
				break;
			}

			// Principal
			if ((curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid())).compareTo(BigDecimal.ZERO) > 0) {
				fullyPaid = false;
				break;
			}

			// Fees
			if ((curSchd.getFeeSchd().subtract(curSchd.getSchdFeePaid())).compareTo(BigDecimal.ZERO) > 0) {
				fullyPaid = false;
				break;
			}
		}

		// Check Penalty Paid Fully or not
		if (fullyPaid) {
			FinODDetails overdue = finODDetailsDAO.getTotals(finID);
			if (overdue != null) {
				BigDecimal balPenalty = overdue.getTotPenaltyAmt().subtract(overdue.getTotPenaltyPaid())
						.add(overdue.getLPIAmt().subtract(overdue.getLPIPaid()));

				// Penalty Not fully Paid
				if (balPenalty.compareTo(BigDecimal.ZERO) > 0) {
					fullyPaid = false;
				}
			}
		}

		return fullyPaid;
	}

	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<>();

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		auditHeader = getAuditDetails(auditHeader, method);

		FinReceiptData repayData = (FinReceiptData) auditHeader.getAuditDetail().getModelData();
		FinanceDetail financeDetail = repayData.getFinanceDetail();

		String usrLanguage = repayData.getReceiptHeader().getUserDetails().getLanguage();

		// Extended field details Validation
		if (financeDetail.getExtendedFieldRender() != null) {
			List<AuditDetail> details = financeDetail.getAuditDetailMap().get("ExtendedFieldDetails");
			ExtendedFieldHeader extHeader = financeDetail.getExtendedFieldHeader();
			details = extendedFieldDetailsService.validateExtendedDdetails(extHeader, details, method, usrLanguage);
			auditDetails.addAll(details);
		}

		if (financeDetail.getExtendedFieldExtension() != null) {
			List<AuditDetail> details = financeDetail.getAuditDetailMap().get("ExtendedFieldExtension");
			details = extendedFieldExtensionService.vaildateDetails(details, usrLanguage);
			auditDetails.addAll(details);
		}

		for (int i = 0; i < auditDetails.size(); i++) {
			auditHeader.setErrorList(auditDetails.get(i).getErrorDetails());
		}

		auditHeader = nextProcess(auditHeader);
		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	private AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {
		logger.debug("Entering ");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		Map<String, List<AuditDetail>> auditDetailMap = new HashMap<>();

		FinReceiptData repayData = (FinReceiptData) auditHeader.getAuditDetail().getModelData();
		FinanceDetail financeDetail = repayData.getFinanceDetail();
		FinReceiptHeader financeMain = repayData.getReceiptHeader();

		String auditTranType = "";
		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (financeMain.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}

		// Extended Field Details
		if (financeDetail.getExtendedFieldRender() != null) {
			auditDetailMap.put("ExtendedFieldDetails",
					extendedFieldDetailsService.setExtendedFieldsAuditData(financeDetail.getExtendedFieldHeader(),
							financeDetail.getExtendedFieldRender(), auditTranType, method,
							ExtendedFieldConstants.MODULE_LOAN));
			auditDetails.addAll(auditDetailMap.get("ExtendedFieldDetails"));
		}

		ExtendedFieldExtension extendedFieldExtension = financeDetail.getExtendedFieldExtension();
		if (extendedFieldExtension != null) {
			auditDetailMap.put("ExtendedFieldExtension", extendedFieldExtensionService
					.setExtendedFieldExtAuditData(extendedFieldExtension, auditTranType, method));
			financeDetail.setAuditDetailMap(auditDetailMap);
			auditDetails.addAll(auditDetailMap.get("ExtendedFieldExtension"));
		}

		financeDetail.setAuditDetailMap(auditDetailMap);
		repayData.setFinanceDetail(financeDetail);
		auditHeader.getAuditDetail().setModelData(repayData);
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
		logger.debug(Literal.ENTERING);

		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		FinReceiptData finReceiptData = (FinReceiptData) auditDetail.getModelData();
		FinReceiptHeader receiptHeader = finReceiptData.getReceiptHeader();

		FinReceiptHeader tempReceiptHeader = null;
		if (receiptHeader.isWorkflow()) {
			tempReceiptHeader = finReceiptHeaderDAO.getReceiptHeaderByID(receiptHeader.getReceiptID(), "_Temp");
		}
		FinReceiptHeader beFinReceiptHeader = finReceiptHeaderDAO.getReceiptHeaderByID(receiptHeader.getReceiptID(),
				"");
		FinReceiptHeader oldReceiptHeader = receiptHeader.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = String.valueOf(receiptHeader.getReceiptID());
		errParm[0] = PennantJavaUtil.getLabel("label_ReceiptID") + ":" + valueParm[0];

		if (receiptHeader.isNewRecord()) { // for New record or new record into work
											// flow

			if (!receiptHeader.isWorkflow()) {// With out Work flow only new
				// records
				if (beFinReceiptHeader != null) { // Record Already Exists in
													// the
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

				if (beFinReceiptHeader == null) { // if records not exists in
													// the
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

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !receiptHeader.isWorkflow()) {
			receiptHeader.setBefImage(beFinReceiptHeader);
		}

		return auditDetail;
	}

	public void setFinReceiptHeaderDAO(FinReceiptHeaderDAO finReceiptHeaderDAO) {
		this.finReceiptHeaderDAO = finReceiptHeaderDAO;
	}

	public void setFinReceiptDetailDAO(FinReceiptDetailDAO finReceiptDetailDAO) {
		this.finReceiptDetailDAO = finReceiptDetailDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public void setFinODDetailsDAO(FinODDetailsDAO finODDetailsDAO) {
		this.finODDetailsDAO = finODDetailsDAO;
	}

	public void setManualAdviseDAO(ManualAdviseDAO manualAdviseDAO) {
		this.manualAdviseDAO = manualAdviseDAO;
	}

	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	public void setAllocationDetailDAO(ReceiptAllocationDetailDAO allocationDetailDAO) {
		this.allocationDetailDAO = allocationDetailDAO;
	}

	public void setFinExcessAmountDAO(FinExcessAmountDAO finExcessAmountDAO) {
		this.finExcessAmountDAO = finExcessAmountDAO;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public void setExtendedFieldDetailsService(ExtendedFieldDetailsService extendedFieldDetailsService) {
		this.extendedFieldDetailsService = extendedFieldDetailsService;
	}

	public void setExtendedFieldExtensionService(ExtendedFieldExtensionService extendedFieldExtensionService) {
		this.extendedFieldExtensionService = extendedFieldExtensionService;
	}
}
