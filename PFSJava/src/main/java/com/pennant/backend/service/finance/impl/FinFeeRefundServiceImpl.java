package com.pennant.backend.service.finance.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.constants.AccountConstants;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.GSTCalculator;
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.finance.FinFeeDetailDAO;
import com.pennant.backend.dao.finance.FinFeeRefundDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.TaxHeaderDetailsDAO;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinExcessMovement;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinFeeRefundDetails;
import com.pennant.backend.model.finance.FinFeeRefundHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.InvoiceDetail;
import com.pennant.backend.model.finance.PrvsFinFeeRefund;
import com.pennant.backend.model.finance.TaxAmountSplit;
import com.pennant.backend.model.finance.TaxHeader;
import com.pennant.backend.model.finance.Taxes;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.FinFeeDetailService;
import com.pennant.backend.service.finance.FinFeeRefundService;
import com.pennant.backend.service.finance.FinanceTaxDetailService;
import com.pennant.backend.service.finance.GSTInvoiceTxnService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.RuleConstants;
import com.pennant.pff.core.engine.accounting.AccountingEngine;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.core.TableType;

/**
 * @author ganesh.p
 *
 */
public class FinFeeRefundServiceImpl extends GenericService<FinFeeRefundHeader> implements FinFeeRefundService {
	private static final Logger logger = LogManager.getLogger(FinFeeRefundServiceImpl.class);

	private FinFeeDetailDAO finFeeDetailDAO;
	private FinanceMainDAO financeMainDAO;
	private PostingsPreparationUtil postingsPreparationUtil;
	private FinExcessAmountDAO finExcessAmountDAO;
	private GSTInvoiceTxnService gstInvoiceTxnService;
	private FinanceTaxDetailService financeTaxDetailService;
	private FinFeeRefundDAO finFeeRefundDao;
	private AuditHeaderDAO auditHeaderDAO;
	private FinFeeDetailService finFeeDetailService;
	private TaxHeaderDetailsDAO taxHeaderDetailsDAO;

	public FinFeeRefundServiceImpl() {
		super();
	}

	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");

		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		FinFeeRefundHeader refundHeader = (FinFeeRefundHeader) auditHeader.getAuditDetail().getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (refundHeader.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (refundHeader.isNewRecord()) {
			refundHeader.setHeaderId(finFeeRefundDao.save(refundHeader, tableType.getSuffix()));
			auditHeader.getAuditDetail().setModelData(refundHeader);
			auditHeader.setAuditReference(String.valueOf(refundHeader.getHeaderId()));
		} else {
			finFeeRefundDao.update(refundHeader, tableType.getSuffix());
		}

		if (CollectionUtils.isNotEmpty(refundHeader.getFinFeeRefundDetails())) {
			List<AuditDetail> details = refundHeader.getAuditDetailMap().get("FeeRefundDetails");
			List<AuditDetail> list = processFinFeeRefundDetails(details, tableType.getSuffix(),
					auditHeader.getAuditTranType(), refundHeader.getHeaderId());
			auditHeader.setAuditDetails(list);
		}

		auditHeaderDAO.addAudit(auditHeader);
		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");

		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		FinFeeRefundHeader refundHeader = new FinFeeRefundHeader();
		BeanUtils.copyProperties((FinFeeRefundHeader) auditHeader.getAuditDetail().getModelData(), refundHeader);

		// PROCESS ACCOUNTING
		AEEvent aeEvent = processAccounting(refundHeader);
		aeEvent.setPostingUserBranch(auditHeader.getAuditBranchCode());
		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
		Map<String, Object> dataMap = amountCodes.getDeclaredFieldValues();
		String userBranch = refundHeader.getUserDetails().getBranchCode();
		prepareFeeRulesMap(refundHeader, dataMap, userBranch);
		aeEvent.setDataMap(dataMap);
		// Fetch Accounting Set ID
		Long accountingSetID = AccountingEngine.getAccountSetID(refundHeader.getFinType(), AccountingEvent.FEEREFUND,
				FinanceConstants.MODULEID_FINTYPE);
		if (accountingSetID == null || accountingSetID <= 0) {
			auditHeader.setErrorDetails(
					ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "65015", null, null)));
			logger.debug("Leaving");
			return auditHeader;
		}
		aeEvent.getAcSetIDList().add(accountingSetID);
		aeEvent.setValueDate(SysParamUtil.getAppDate());
		aeEvent = postingsPreparationUtil.postAccounting(aeEvent);
		refundHeader.setLinkedTranId(aeEvent.getLinkedTranId());

		if (!PennantConstants.RECORD_TYPE_NEW.equals(refundHeader.getRecordType())) {
			auditHeader.getAuditDetail()
					.setBefImage(finFeeRefundDao.getFinFeeRefundHeaderById(refundHeader.getHeaderId(), ""));
		}

		if (refundHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			finFeeRefundDao.deleteFinFeeRefundHeader(refundHeader, TableType.MAIN_TAB);
		} else {
			refundHeader.setRoleCode("");
			refundHeader.setNextRoleCode("");
			refundHeader.setTaskId("");
			refundHeader.setNextTaskId("");
			refundHeader.setWorkflowId(0);

			if (refundHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				refundHeader.setRecordType("");
				finFeeRefundDao.save(refundHeader, TableType.MAIN_TAB.getSuffix());
			} else {
				tranType = PennantConstants.TRAN_UPD;
				refundHeader.setRecordType("");
				finFeeRefundDao.update(refundHeader, TableType.MAIN_TAB.getSuffix());
			}
		}
		// Process FinFeeRefundDetails
		if (CollectionUtils.isNotEmpty(refundHeader.getFinFeeRefundDetails())) {
			List<AuditDetail> details = refundHeader.getAuditDetailMap().get("FeeRefundDetails");
			List<AuditDetail> list = processFinFeeRefundDetails(details, TableType.MAIN_TAB.getSuffix(),
					auditHeader.getAuditTranType(), refundHeader.getHeaderId());
			auditHeader.setAuditDetails(list);
		}
		processExcessAmount(refundHeader);
		processGSTInvoicePreparation(refundHeader);
		finFeeRefundDao.deleteFinFeeRefundHeader(refundHeader, TableType.TEMP_TAB);

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeaderDAO.addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(refundHeader);
		auditHeaderDAO.addAudit(auditHeader);
		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "doReject");

		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}
		FinFeeRefundHeader refundHeader = (FinFeeRefundHeader) auditHeader.getAuditDetail().getModelData();

		// Process FinFeeRefundDetails
		if (CollectionUtils.isNotEmpty(refundHeader.getFinFeeRefundDetails())) {
			List<AuditDetail> details = refundHeader.getAuditDetailMap().get("FeeRefundDetails");
			List<AuditDetail> list = processFinFeeRefundDetails(details, TableType.TEMP_TAB.getSuffix(),
					auditHeader.getAuditTranType(), refundHeader.getHeaderId());
			auditHeader.setAuditDetails(list);
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		finFeeRefundDao.deleteFinFeeRefundHeader(refundHeader, TableType.TEMP_TAB);

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

		FinFeeRefundHeader refundHeader = (FinFeeRefundHeader) auditHeader.getAuditDetail().getModelData();
		finFeeRefundDao.deleteFinFeeRefundHeader(refundHeader, TableType.MAIN_TAB);

		// Process FinFeeRefundDetails
		if (CollectionUtils.isNotEmpty(refundHeader.getFinFeeRefundDetails())) {
			List<AuditDetail> details = refundHeader.getAuditDetailMap().get("FeeRefundDetails");
			List<AuditDetail> list = processFinFeeRefundDetails(details, TableType.MAIN_TAB.getSuffix(),
					auditHeader.getAuditTranType(), refundHeader.getHeaderId());
			auditHeader.setAuditDetails(list);
		}
		auditHeaderDAO.addAudit(auditHeader);
		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		FinFeeRefundHeader refundHeader = (FinFeeRefundHeader) auditHeader.getAuditDetail().getModelData();
		List<FinFeeRefundDetails> refundDetailList = refundHeader.getFinFeeRefundDetails();
		String auditTranType = auditHeader.getAuditTranType();
		String usrLanguage = PennantConstants.default_Language;
		if (refundHeader.getUserDetails() != null) {
			usrLanguage = refundHeader.getUserDetails().getLanguage();
		}
		auditHeader.setAuditDetail(auditDetail);

		if (refundDetailList != null && !refundDetailList.isEmpty()) {
			if ("doReject".equals(method)) {
				refundHeader.setRecordType(PennantConstants.RECORD_TYPE_CAN);
			}
			setParentDetails(refundDetailList, refundHeader);
			List<AuditDetail> details = validateFinFeeRefundDetails(refundDetailList, refundHeader.getWorkflowId(),
					method, auditTranType, usrLanguage);
			refundHeader.getAuditDetailMap().put("FeeRefundDetails", details);
			auditHeader.setAuditDetails(details);
		}

		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);
		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	private void setParentDetails(List<FinFeeRefundDetails> refundDetailList, FinFeeRefundHeader refundHeader) {
		for (FinFeeRefundDetails finFeeRefundDetails : refundDetailList) {
			finFeeRefundDetails.setRecordType(refundHeader.getRecordType());
			finFeeRefundDetails.setRecordStatus(refundHeader.getRecordStatus());
			finFeeRefundDetails.setRoleCode(refundHeader.getRoleCode());
			finFeeRefundDetails.setNextRoleCode(refundHeader.getNextRoleCode());
			finFeeRefundDetails.setTaskId(refundHeader.getTaskId());
			finFeeRefundDetails.setNextTaskId(refundHeader.getNextTaskId());
			finFeeRefundDetails.setLastMntBy(refundHeader.getLastMntBy());
			finFeeRefundDetails.setLastMntOn(refundHeader.getLastMntOn());
		}
	}

	public List<AuditDetail> validateFinFeeRefundDetails(List<FinFeeRefundDetails> refundDetailList, long workflowId,
			String method, String auditTranType, String usrLanguage) {
		logger.debug("Entering");
		List<AuditDetail> auditDetails = getAuditDetail(refundDetailList, auditTranType, method, workflowId);
		for (AuditDetail auditDetail : auditDetails) {
			validate(auditDetail, method, usrLanguage);
		}
		logger.debug("Leaving");
		return auditDetails;
	}

	private List<AuditDetail> getAuditDetail(List<FinFeeRefundDetails> finFeeRefundDetails, String auditTranType,
			String method, long workflowId) {
		logger.debug(Literal.ENTERING);
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		FinFeeRefundDetails object = new FinFeeRefundDetails();
		String[] fields = PennantJavaUtil.getFieldDetails(object, object.getExcludeFields());

		for (int i = 0; i < finFeeRefundDetails.size(); i++) {

			FinFeeRefundDetails refundDetails = finFeeRefundDetails.get(i);
			refundDetails.setWorkflowId(workflowId);
			boolean isRcdType = false;

			if (refundDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				refundDetails.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (refundDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				refundDetails.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				isRcdType = true;
			} else if (refundDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				refundDetails.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				isRcdType = true;
			}

			if ("saveOrUpdate".equals(method) && (isRcdType)) {
				refundDetails.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (refundDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (refundDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| refundDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			if (StringUtils.isNotEmpty(refundDetails.getRecordType())) {
				auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
						refundDetails.getBefImage(), refundDetails));
			}
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	private AuditDetail validate(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug(Literal.ENTERING);
		if (auditDetail.getErrorDetails() == null) {
			auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		}
		FinFeeRefundDetails refundDetails = (FinFeeRefundDetails) auditDetail.getModelData();
		FinFeeRefundDetails tempRefundDetail = null;
		FinFeeRefundDetails befRefundDetail = null;
		FinFeeRefundDetails oldRefundDetail = null;

		if (refundDetails.isWorkflow()) {
			tempRefundDetail = finFeeRefundDao.getFinFeeRefundDetailsById(refundDetails.getId(), "_Temp");
		}

		befRefundDetail = finFeeRefundDao.getFinFeeRefundDetailsById(refundDetails.getId(), "");
		oldRefundDetail = refundDetails.getBefImage();

		String[] errParm = new String[2];
		String[] valueParm = new String[2];
		valueParm[0] = "";
		valueParm[1] = refundDetails.getFeeTypeCode();
		errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_FeeTypeCode") + ":" + valueParm[1];

		if (refundDetails.isNewRecord()) { // for New record or new record into work flow

			if (!refundDetails.isWorkflow()) {// With out Work flow only new records
				if (befRefundDetail != null) { // Record Already Exists in the table then error
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow
				if (refundDetails.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befRefundDetail != null || tempRefundDetail != null) { // if records already exists in the main
																				// table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
					}
				} else { // if records not exists in the Main flow table
					if (befRefundDetail == null || tempRefundDetail != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!refundDetails.isWorkflow()) { // With out Work flow for update and delete

				if (befRefundDetail == null) { // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, valueParm), usrLanguage));
				} else {
					if (oldRefundDetail != null
							&& !oldRefundDetail.getLastMntOn().equals(befRefundDetail.getLastMntOn())) {
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

				if (tempRefundDetail == null) { // if records not exists in the Work flow table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}

				if (tempRefundDetail != null && oldRefundDetail != null
						&& !oldRefundDetail.getLastMntOn().equals(tempRefundDetail.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !refundDetails.isWorkflow()) {
			auditDetail.setBefImage(befRefundDetail);
		}
		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug(Literal.ENTERING);
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		FinFeeRefundHeader refundHeader = (FinFeeRefundHeader) auditDetail.getModelData();
		FinFeeRefundHeader tempRefundHeader = null;
		FinFeeRefundHeader befRefundHeader = null;
		FinFeeRefundHeader oldRefundHeader = null;

		if (refundHeader.isWorkflow()) {
			tempRefundHeader = finFeeRefundDao.getFinFeeRefundHeaderById(refundHeader.getHeaderId(), "_Temp");
		}

		befRefundHeader = finFeeRefundDao.getFinFeeRefundHeaderById(refundHeader.getHeaderId(), "");
		oldRefundHeader = refundHeader.getBefImage();

		String[] errParm = new String[2];
		String[] valueParm = new String[2];
		valueParm[0] = refundHeader.getFinReference();
		valueParm[1] = refundHeader.getLovDescCustCIF();
		errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_CustCIF") + ":" + valueParm[1];

		if (refundHeader.isNewRecord()) { // for New record or new record into work flow

			if (!refundHeader.isWorkflow()) {// With out Work flow only new records
				if (befRefundHeader != null) { // Record Already Exists in the table then error
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow
				if (refundHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befRefundHeader != null || tempRefundHeader != null) { // if records already exists in the main
																				// table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
					}
				} else { // if records not exists in the Main flow table
					if (befRefundHeader == null || tempRefundHeader != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!refundHeader.isWorkflow()) { // With out Work flow for update and delete

				if (befRefundHeader == null) { // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, valueParm), usrLanguage));
				} else {
					if (oldRefundHeader != null
							&& !oldRefundHeader.getLastMntOn().equals(befRefundHeader.getLastMntOn())) {
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

				if (tempRefundHeader == null) { // if records not exists in the Work flow table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}

				if (tempRefundHeader != null && oldRefundHeader != null
						&& !oldRefundHeader.getLastMntOn().equals(tempRefundHeader.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !refundHeader.isWorkflow()) {
			auditDetail.setBefImage(befRefundHeader);
		}
		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	private List<AuditDetail> processFinFeeRefundDetails(List<AuditDetail> auditDetails, String tableType,
			String auditTranType, long headerID) {
		logger.debug(Literal.ENTERING);

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {

			FinFeeRefundDetails refundDetails = (FinFeeRefundDetails) auditDetails.get(i).getModelData();
			refundDetails.setHeaderId(headerID);

			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (tableType.equals("")) {
				approveRec = true;
				refundDetails.setRoleCode("");
				refundDetails.setNextRoleCode("");
				refundDetails.setTaskId("");
				refundDetails.setNextTaskId("");
			}

			if (refundDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (refundDetails.isNewRecord()) {
				saveRecord = true;
				if (refundDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					refundDetails.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (refundDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					refundDetails.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (refundDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					refundDetails.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (refundDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (refundDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (refundDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (refundDetails.isNewRecord()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = refundDetails.getRecordType();
				recordStatus = refundDetails.getRecordStatus();
				refundDetails.setWorkflowId(0);
				refundDetails.setRecordType("");
				refundDetails.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}

			if (saveRecord) {
				finFeeRefundDao.save(refundDetails, tableType);
			}

			if (updateRecord) {
				finFeeRefundDao.update(refundDetails, tableType);
			}

			if (deleteRecord) {
				finFeeRefundDao.deleteFinFeeRefundDetailsByID(refundDetails, tableType);
			}

			if (approveRec) {
				refundDetails.setRecordType(rcdType);
				refundDetails.setRecordStatus(recordStatus);
				finFeeRefundDao.deleteFinFeeRefundDetailsByID(refundDetails, TableType.TEMP_TAB.getSuffix());
			}
			auditDetails.get(i).setModelData(refundDetails);
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	public AuditDetail getAuditDetails(FinFeeRefundDetails refundDetails, int auditSeq, String transType) {
		FinFeeRefundDetails detail = new FinFeeRefundDetails();
		String[] fields = PennantJavaUtil.getFieldDetails(detail, detail.getExcludeFields());
		return new AuditDetail(transType, auditSeq, fields[0], fields[1], refundDetails.getBefImage(), refundDetails);
	}

	@Override
	public FinFeeRefundHeader getFinFeeRefundHeaderById(long headerId, String type) {
		return finFeeRefundDao.getFinFeeRefundHeaderById(headerId, type);
	}

	@Override
	public List<FinFeeRefundDetails> getFinFeeRefundDetailsByHeaderId(long headerId, String type) {
		return finFeeRefundDao.getFinFeeRefundDetailsByHeaderId(headerId, type);
	}

	@Override
	public PrvsFinFeeRefund getPrvsRefundsByFeeId(long feeID) {
		return finFeeRefundDao.getPrvsRefundsByFeeId(feeID);
	}

	@Override
	public FinFeeRefundHeader getPaidFeeDetails(FinFeeRefundHeader header, String type) {
		logger.debug(Literal.ENTERING);
		List<FinFeeDetail> finFeeDetails = new ArrayList<>();
		List<FinFeeDetail> list = finFeeDetailDAO.getPaidFinFeeDetails(header.getFinReference(), type);
		if (!header.isNewRecord()) {
			List<FinFeeRefundDetails> finFeeRefundDetails = finFeeRefundDao
					.getFinFeeRefundDetailsByHeaderId(header.getHeaderId(), type);
			for (FinFeeRefundDetails refundDetail : finFeeRefundDetails) {
				FinFeeDetail finFeeDetail = getFeeDetailByFeeID(list, refundDetail.getFeeId());
				if (finFeeDetail != null) {
					// Tax Details
					Long headerId = finFeeDetail.getTaxHeaderId();
					if (headerId != null && headerId > 0) {
						List<Taxes> taxDetails = taxHeaderDetailsDAO.getTaxDetailById(headerId, type);
						TaxHeader taxheader = new TaxHeader();
						taxheader.setTaxDetails(taxDetails);
						taxheader.setHeaderId(headerId);
						finFeeDetail.setTaxHeader(taxheader);
					}

					refundDetail.setFeeTypeCode(finFeeDetail.getFeeTypeCode());
					refundDetail.setTaxApplicable(finFeeDetail.isTaxApplicable());
					refundDetail.setTaxComponent(finFeeDetail.getTaxComponent());
					refundDetail.setFinEvent(finFeeDetail.getFinEvent());
					finFeeDetails.add(finFeeDetail);
				}
			}
			header.setFinFeeDetailList(finFeeDetails);
			header.setFinFeeRefundDetails(finFeeRefundDetails);
		} else {
			List<FinFeeRefundDetails> finFeeRefundDetails = new ArrayList<>(1);
			for (FinFeeDetail finFeeDetail : list) {
				if (BigDecimal.ZERO.compareTo(finFeeDetail.getPaidAmountOriginal()) == 0) {
					continue;
				}

				// Tax Details
				Long headerId = finFeeDetail.getTaxHeaderId();
				if (headerId != null && headerId > 0) {
					List<Taxes> taxDetails = taxHeaderDetailsDAO.getTaxDetailById(headerId, type);
					TaxHeader taxheader = new TaxHeader();
					taxheader.setTaxDetails(taxDetails);
					taxheader.setHeaderId(headerId);
					finFeeDetail.setTaxHeader(taxheader);
				}

				finFeeDetails.add(finFeeDetail);
				FinFeeRefundDetails refundDetail = new FinFeeRefundDetails();
				refundDetail.setNewRecord(true);
				refundDetail.setRecordType(PennantConstants.RCD_ADD);
				refundDetail.setFeeId(finFeeDetail.getFeeID());
				refundDetail.setFeeTypeCode(finFeeDetail.getFeeTypeCode());
				refundDetail.setTaxApplicable(finFeeDetail.isTaxApplicable());
				refundDetail.setTaxComponent(finFeeDetail.getTaxComponent());
				finFeeRefundDetails.add(refundDetail);
			}
			header.setFinFeeDetailList(finFeeDetails);
			header.setFinFeeRefundDetails(finFeeRefundDetails);
		}
		logger.debug(Literal.LEAVING);
		return header;
	}

	private FinFeeDetail getFeeDetailByFeeID(List<FinFeeDetail> finFeeDetails, long feeId) {
		for (FinFeeDetail finFeeDetail : finFeeDetails) {
			if (feeId == finFeeDetail.getFeeID()) {
				return finFeeDetail;
			}
		}
		return null;
	}

	@Override
	public AEEvent processAccounting(FinFeeRefundHeader frh) {
		logger.debug(Literal.ENTERING);
		AEEvent aeEvent = new AEEvent();
		aeEvent.setAccountingEvent(AccountingEvent.FEEREFUND);
		aeEvent.setFinID(frh.getFinID());
		aeEvent.setFinReference(frh.getFinReference());
		aeEvent.setCustCIF(frh.getLovDescCustCIF());
		aeEvent.setBranch(frh.getFinBranch());
		aeEvent.setCcy(frh.getFinCcy());
		aeEvent.setCustID(frh.getCustId());
		aeEvent.setValueDate(SysParamUtil.getAppDate());
		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
		if (amountCodes == null) {
			amountCodes = new AEAmountCodes();
			aeEvent.setAeAmountCodes(amountCodes);
		}
		BigDecimal toExcessAmt = BigDecimal.ZERO;
		amountCodes.setToExcessAmt(toExcessAmt);
		amountCodes.setFinType(frh.getFinType());
		logger.debug(Literal.ENTERING);
		return aeEvent;
	}

	@Override
	public void prepareFeeRulesMap(FinFeeRefundHeader feeRefundHeader, Map<String, Object> dataMap, String userBranch) {
		logger.debug(Literal.ENTERING);
		List<FinFeeRefundDetails> finFeeRefundDtlsList = feeRefundHeader.getFinFeeRefundDetails();
		if (CollectionUtils.isEmpty(finFeeRefundDtlsList)) {
			return;
		}
		Map<String, BigDecimal> taxPercentages = GSTCalculator.getTaxPercentages(feeRefundHeader.getCustId(),
				feeRefundHeader.getFinCcy(), userBranch, feeRefundHeader.getFinBranch());

		List<FinFeeDetail> list = feeRefundHeader.getFinFeeDetailList();
		BigDecimal totPaidAmt = BigDecimal.ZERO;
		dataMap.put("ae_refundVasFee", BigDecimal.ZERO);
		for (FinFeeRefundDetails refundDetail : finFeeRefundDtlsList) {
			FinFeeDetail finFeeDetail = getFeeDetailByFeeID(list, refundDetail.getFeeId());
			PrvsFinFeeRefund prvsFinFeeRefund = getPrvsRefundsByFeeId(refundDetail.getFeeId());
			// Incase of Vas FEE
			if (AccountingEvent.VAS_FEE.equals(refundDetail.getFinEvent())) {
				BigDecimal vasPaidFee = (BigDecimal) dataMap.get("ae_refundVasFee");
				vasPaidFee = vasPaidFee.add(refundDetail.getRefundAmtOriginal());
				dataMap.put("ae_refundVasFee", vasPaidFee);
				totPaidAmt = totPaidAmt.add(refundDetail.getRefundAmtOriginal());
				continue;
			}

			String feeTypeCode = refundDetail.getFeeTypeCode();
			dataMap.put(feeTypeCode + "_R", refundDetail.getRefundAmtOriginal());
			dataMap.put(feeTypeCode + "_TDS_R", refundDetail.getRefundAmtTDS());
			if (refundDetail.isTaxApplicable()) {
				TaxAmountSplit taxAmountSplit = null;

				taxAmountSplit = GSTCalculator.getInclusiveGST(
						refundDetail.getRefundAmount().add(refundDetail.getRefundAmtTDS()), taxPercentages);
				totPaidAmt = totPaidAmt.add(refundDetail.getRefundAmount());

				if (finFeeDetail.getPaidAmount().subtract(prvsFinFeeRefund.getTotRefundAmount())
						.compareTo(refundDetail.getRefundAmount()) == 0) {

					TaxAmountSplit prevTaxSplit = GSTCalculator.getInclusiveGST(
							prvsFinFeeRefund.getTotRefundAmount().add(prvsFinFeeRefund.getTotRefundAmtTDS()),
							taxPercentages);
					TaxAmountSplit netTaxSplit = GSTCalculator.getInclusiveGST(
							finFeeDetail.getPaidAmount().add(finFeeDetail.getPaidTDS()), taxPercentages);

					dataMap.put(feeTypeCode + "_CGST_R", netTaxSplit.getcGST().subtract(prevTaxSplit.getcGST()));
					dataMap.put(feeTypeCode + "_SGST_R", netTaxSplit.getsGST().subtract(prevTaxSplit.getsGST()));
					dataMap.put(feeTypeCode + "_IGST_R", netTaxSplit.getiGST().subtract(prevTaxSplit.getiGST()));
					dataMap.put(feeTypeCode + "_UGST_R", netTaxSplit.getuGST().subtract(prevTaxSplit.getuGST()));
					dataMap.put(feeTypeCode + "_CESS_R", netTaxSplit.getCess().subtract(prevTaxSplit.getCess()));

				} else {
					dataMap.put(feeTypeCode + "_CGST_R", taxAmountSplit.getcGST());
					dataMap.put(feeTypeCode + "_SGST_R", taxAmountSplit.getsGST());
					dataMap.put(feeTypeCode + "_IGST_R", taxAmountSplit.getiGST());
					dataMap.put(feeTypeCode + "_UGST_R", taxAmountSplit.getuGST());
					dataMap.put(feeTypeCode + "_CESS_R", taxAmountSplit.getCess());
				}
			} else {
				totPaidAmt = totPaidAmt.add(refundDetail.getRefundAmount());
			}
		}

		dataMap.put("ae_toExcessAmt", totPaidAmt);
		logger.debug(Literal.LEAVING);
	}

	private void processExcessAmount(FinFeeRefundHeader frh) {
		logger.debug(Literal.ENTERING);
		List<FinFeeRefundDetails> refundDetails = frh.getFinFeeRefundDetails();

		if (CollectionUtils.isEmpty(refundDetails)) {
			return;
		}

		BigDecimal excessAmt = BigDecimal.ZERO;

		for (FinFeeRefundDetails detail : refundDetails) {
			excessAmt = excessAmt.add(detail.getRefundAmount());
		}

		if (BigDecimal.ZERO.compareTo(excessAmt) < 0) {
			FinExcessAmount excess = new FinExcessAmount();
			excess = new FinExcessAmount();
			excess.setFinID(frh.getFinID());
			excess.setFinReference(frh.getFinReference());
			excess.setAmountType(RepayConstants.EXCESSADJUSTTO_EXCESS);
			excess.setAmount(excessAmt);
			excess.setUtilisedAmt(BigDecimal.ZERO);
			excess.setBalanceAmt(excessAmt);
			excess.setReservedAmt(BigDecimal.ZERO);
			excess.setReceiptID(null);
			excess.setValueDate(SysParamUtil.getAppDate());
			excess.setPostDate(excess.getValueDate());

			finExcessAmountDAO.saveExcess(excess);

			FinExcessMovement excessMovement = new FinExcessMovement();
			excessMovement.setExcessID(excess.getExcessID());
			excessMovement.setAmount(excessAmt);
			excessMovement.setReceiptID(frh.getHeaderId());
			excessMovement.setMovementType(RepayConstants.RECEIPTTYPE_RECIPT);
			excessMovement.setTranType(AccountConstants.TRANTYPE_CREDIT);
			excessMovement.setMovementFrom("UPFRONT");

			finExcessAmountDAO.saveExcessMovement(excessMovement);

			logger.debug(Literal.LEAVING);
		}
	}

	@Override
	public FinanceDetail getFinanceDetailById(long finID) {
		logger.debug(Literal.ENTERING);

		FinanceMain fm = financeMainDAO.getFinBasicDetails(finID, "_View");

		FinanceDetail fd = new FinanceDetail();

		FinScheduleData schdData = fd.getFinScheduleData();

		schdData.setFinID(fm.getFinID());
		schdData.setFinReference(fm.getFinReference());

		schdData.setFinanceMain(fm);

		logger.debug(Literal.LEAVING);

		return fd;
	}

	private void processGSTInvoicePreparation(FinFeeRefundHeader feeRefundHeader) {
		long linkedTranId = feeRefundHeader.getLinkedTranId();
		String finReference = feeRefundHeader.getFinReference();
		FinanceDetail fd = new FinanceDetail();
		FinanceMain fm = this.financeMainDAO.getFinanceMainByRef(finReference, "_View", false);
		fd.setCustomerDetails(null);
		fd.getFinScheduleData().setFinanceMain(fm);

		long finID = fm.getFinID();

		fd.setFinanceTaxDetail(financeTaxDetailService.getFinanceTaxDetail(finID));
		List<FinFeeDetail> finFeeDetailsList = processFinFeeDetailForinvoice(feeRefundHeader, fm);

		InvoiceDetail invoiceDetail = new InvoiceDetail();
		invoiceDetail.setLinkedTranId(linkedTranId);
		invoiceDetail.setFinanceDetail(fd);
		invoiceDetail.setFinFeeDetailsList(finFeeDetailsList);
		invoiceDetail.setOrigination(false);
		invoiceDetail.setWaiver(true);
		invoiceDetail.setDbInvSetReq(true);
		invoiceDetail.setInvoiceType(PennantConstants.GST_INVOICE_TRANSACTION_TYPE_CREDIT);

		Long dueInvoiceID = this.gstInvoiceTxnService.feeTaxInvoicePreparation(invoiceDetail);

		for (FinFeeDetail finFeeDetail : finFeeDetailsList) {
			TaxHeader taxHeader = finFeeDetail.getTaxHeader();
			if (taxHeader != null && finFeeDetail.getWaivedAmount().compareTo(BigDecimal.ZERO) >= 0) {
				if (dueInvoiceID == null) {
					dueInvoiceID = finFeeDetail.getTaxHeader().getInvoiceID();
				}
				taxHeader.setInvoiceID(dueInvoiceID);
				String type = "";
				if (!(fm.getRecordStatus().equalsIgnoreCase("Approved"))) {
					type = "_TView";
				}
				this.taxHeaderDetailsDAO.update(taxHeader, type);
			}
		}

		logger.debug(Literal.LEAVING);
	}

	private List<FinFeeDetail> processFinFeeDetailForinvoice(FinFeeRefundHeader feeRefundHeader,
			FinanceMain financeMain) {
		List<FinFeeDetail> finFeeDetails = feeRefundHeader.getFinFeeDetailList();
		List<FinFeeRefundDetails> refundList = feeRefundHeader.getFinFeeRefundDetails();
		String userBranch = feeRefundHeader.getUserDetails().getBranchCode();

		Map<String, BigDecimal> taxPercentages = GSTCalculator.getTaxPercentages(feeRefundHeader.getCustId(),
				feeRefundHeader.getFinCcy(), userBranch, feeRefundHeader.getFinBranch());

		List<FinFeeDetail> finFeeDetailsList = new ArrayList<>();

		for (FinFeeRefundDetails finFeeRefundDetails : refundList) {
			FinFeeDetail feeDetail = getFeeDetailByFeeID(finFeeDetails, finFeeRefundDetails.getFeeId());
			FinFeeRefundDetails prvFinFeeRefundDetail = finFeeRefundDao
					.getPrvRefundDetails(feeRefundHeader.getHeaderId(), feeDetail.getFeeID());
			BigDecimal waivedAmt = finFeeRefundDetails.getRefundAmount().add(finFeeRefundDetails.getRefundAmtTDS());

			feeDetail.setWaivedAmount(waivedAmt);
			feeDetail.setWaivedGST(finFeeRefundDetails.getRefundAmtGST());

			String taxComponent = feeDetail.getTaxComponent();
			feeDetail.setTaxComponent(FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE);

			Long taxHeaderId = feeDetail.getTaxHeaderId();
			if (taxHeaderId == null) {
				continue;
			}

			String type = "";
			if (!(financeMain.getRecordStatus().equalsIgnoreCase("Approved"))) {
				type = "_TView";
			}
			feeDetail.setTaxHeader(taxHeaderDetailsDAO.getTaxHeaderDetailsById(taxHeaderId, type));

			if (feeDetail.getTaxHeader() != null) {
				feeDetail.getTaxHeader().setTaxDetails(taxHeaderDetailsDAO.getTaxDetailById(taxHeaderId, type));
			}

			finFeeDetailService.calculateFees(feeDetail, financeMain, taxPercentages);

			feeDetail.setTaxComponent(taxComponent);

			if (feeDetail.getPaidAmount().subtract(prvFinFeeRefundDetail.getRefundAmount())
					.compareTo(feeDetail.getWaivedAmount().subtract(finFeeRefundDetails.getRefundAmtTDS())) == 0) {
				TaxAmountSplit prevTaxSplit = GSTCalculator.getInclusiveGST(
						prvFinFeeRefundDetail.getRefundAmount().add(prvFinFeeRefundDetail.getRefundAmtTDS()),
						taxPercentages);
				TaxAmountSplit netTaxSplit = GSTCalculator
						.getInclusiveGST(feeDetail.getPaidAmount().add(feeDetail.getPaidTDS()), taxPercentages);
				List<Taxes> taxDetails = feeDetail.getTaxHeader().getTaxDetails();

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
							cgstTax.setWaivedTax(netTaxSplit.getcGST().subtract(prevTaxSplit.getcGST()));
							break;
						case RuleConstants.CODE_SGST:
							sgstTax = taxes;
							sgstTax.setWaivedTax(netTaxSplit.getsGST().subtract(prevTaxSplit.getsGST()));
							break;
						case RuleConstants.CODE_IGST:
							igstTax = taxes;
							igstTax.setWaivedTax(netTaxSplit.getiGST().subtract(prevTaxSplit.getiGST()));
							break;
						case RuleConstants.CODE_UGST:
							ugstTax = taxes;
							ugstTax.setWaivedTax(netTaxSplit.getuGST().subtract(prevTaxSplit.getuGST()));
							break;
						case RuleConstants.CODE_CESS:
							cessTax = taxes;
							cessTax.setWaivedTax(netTaxSplit.getCess().subtract(prevTaxSplit.getCess()));
							break;
						default:
							break;
						}

					}
				}
				BigDecimal gstAmount = cgstTax.getWaivedTax().add(sgstTax.getWaivedTax()).add(igstTax.getWaivedTax())
						.add(ugstTax.getWaivedTax()).add(cessTax.getWaivedTax());
				feeDetail.setWaivedGST(gstAmount);
			}

			if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equalsIgnoreCase(taxComponent)) {
				feeDetail.setWaivedAmount(feeDetail.getWaivedAmount().subtract(feeDetail.getWaivedGST()));
			}

			finFeeDetailsList.add(feeDetail);
		}
		return finFeeDetailsList;
	}

	public void setFinFeeDetailDAO(FinFeeDetailDAO finFeeDetailDAO) {
		this.finFeeDetailDAO = finFeeDetailDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}

	public void setFinExcessAmountDAO(FinExcessAmountDAO finExcessAmountDAO) {
		this.finExcessAmountDAO = finExcessAmountDAO;
	}

	public void setGstInvoiceTxnService(GSTInvoiceTxnService gstInvoiceTxnService) {
		this.gstInvoiceTxnService = gstInvoiceTxnService;
	}

	public void setFinanceTaxDetailService(FinanceTaxDetailService financeTaxDetailService) {
		this.financeTaxDetailService = financeTaxDetailService;
	}

	public void setFinFeeRefundDao(FinFeeRefundDAO finFeeRefundDao) {
		this.finFeeRefundDao = finFeeRefundDao;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public void setFinFeeDetailService(FinFeeDetailService finFeeDetailService) {
		this.finFeeDetailService = finFeeDetailService;
	}

	public void setTaxHeaderDetailsDAO(TaxHeaderDetailsDAO taxHeaderDetailsDAO) {
		this.taxHeaderDetailsDAO = taxHeaderDetailsDAO;
	}

}
