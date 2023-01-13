package com.pennant.backend.service.settlement.impl;

import java.math.BigDecimal;
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
import org.zkoss.util.resource.Labels;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.app.util.ReceiptCalculator;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.Repayments.FinanceRepaymentsDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.dao.receipts.FinReceiptDetailDAO;
import com.pennant.backend.dao.receipts.FinReceiptHeaderDAO;
import com.pennant.backend.dao.rulefactory.PostingsDAO;
import com.pennant.backend.dao.settlement.SettlementDAO;
import com.pennant.backend.dao.settlementschedule.SettlementScheduleDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinReceiptData;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinRepayHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.ReceiptAllocationDetail;
import com.pennant.backend.model.finance.XcessPayables;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.model.settlement.FinSettlementHeader;
import com.pennant.backend.model.settlement.SettlementAllocationDetail;
import com.pennant.backend.model.settlement.SettlementSchedule;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.ReceiptService;
import com.pennant.backend.service.settlement.SettlementService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.RepayConstants;
import com.pennant.cache.util.AccountingConfigCache;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.receipt.constants.AllocationType;
import com.pennanttech.pff.receipt.constants.ReceiptMode;

public class SettlementServiceImpl extends GenericService<FinSettlementHeader> implements SettlementService {
	private static final Logger logger = LogManager.getLogger(SettlementServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private SettlementDAO settlementDAO;
	private SettlementScheduleDAO settlementScheduleDAO;
	private ReceiptService receiptService;
	private FinanceMainDAO financeMainDAO;
	private FinReceiptHeaderDAO finReceiptHeaderDAO;
	private FinReceiptDetailDAO finReceiptDetailDAO;
	private PostingsDAO postingsDAO;
	private PostingsPreparationUtil postingsPreparationUtil;
	private FinExcessAmountDAO finExcessAmountDAO;
	private FinanceRepaymentsDAO financeRepaymentsDAO;
	private ReceiptCalculator receiptCalculator;

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public SettlementDAO getSettlementDAO() {
		return settlementDAO;
	}

	public void setSettlementDAO(SettlementDAO settlementDAO) {
		this.settlementDAO = settlementDAO;
	}

	public SettlementScheduleDAO getSettlementScheduleDAO() {
		return settlementScheduleDAO;
	}

	public void setSettlementScheduleDAO(SettlementScheduleDAO settlementScheduleDAO) {
		this.settlementScheduleDAO = settlementScheduleDAO;
	}

	@Override
	public FinSettlementHeader getsettlementById(long id) {
		FinSettlementHeader settlementHeader = settlementDAO.getSettlementById(id, "_View");
		if (settlementHeader != null) {
			settlementHeader.setSettlementAllocationDetails(settlementDAO.getSettlementAllcDetailByHdrID(id, "_View"));
			settlementHeader.setSettlementScheduleList(settlementScheduleDAO.getSettlementScheduleDetails(id, "_View"));
		}
		return settlementHeader;
	}

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		FinSettlementHeader settlement = (FinSettlementHeader) auditHeader.getAuditDetail().getModelData();
		getSettlementDAO().deleteSettlementAllcByHeaderId(settlement.getID(), "");
		getSettlementDAO().delete(settlement, "");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader, "saveOrUpdate");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		String tableType = "";
		FinSettlementHeader settlement = (FinSettlementHeader) auditHeader.getAuditDetail().getModelData();
		if (StringUtils.isNotBlank(settlement.getCancelReasonCode())) {
			settlement.setSettlementStatus("C");
		}
		if (settlement.isWorkflow()) {
			tableType = "_Temp";
		}

		if (settlement.isNewRecord()) {
			settlement.setID(getSettlementDAO().save(settlement, tableType));
			auditHeader.getAuditDetail().setModelData(settlement);
			auditHeader.setAuditReference(String.valueOf(settlement.getID()));
		} else {
			getSettlementDAO().update(settlement, tableType);
		}

		if (settlement.getSettlementScheduleList() != null && settlement.getSettlementScheduleList().size() > 0
				&& StringUtils.isBlank(settlement.getCancelReasonCode())) {
			List<AuditDetail> details = settlement.getAuditDetailMap().get("SettlementSchedule");
			details = settlementScheduleList(details, tableType, settlement.getID());
			// auditDetails.addAll(details);
		}

		List<SettlementAllocationDetail> settlementAllocationDetails = settlement.getSettlementAllocationDetails();
		if (CollectionUtils.isNotEmpty(settlementAllocationDetails)) {
			for (SettlementAllocationDetail settlementAllocationDetail : settlementAllocationDetails) {
				settlementAllocationDetail.setHeaderID(settlement.getID());
				if (settlementAllocationDetail.isNewRecord()) {
					getSettlementDAO().deleteSettlementAllcById(settlementAllocationDetail, tableType);
					getSettlementDAO().saveSettlementAllcDetails(settlementAllocationDetail, tableType);
				} else {
					getSettlementDAO().deleteSettlementAllcById(settlementAllocationDetail, tableType);
					getSettlementDAO().deleteSettlementAllcById(settlementAllocationDetail, "");
					getSettlementDAO().saveSettlementAllcDetails(settlementAllocationDetail, tableType);
				}
			}
		}

		auditHeader.setAuditDetails(auditDetails);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	private List<AuditDetail> settlementScheduleList(List<AuditDetail> auditDetails, String type, long headerID) {
		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {

			SettlementSchedule settlementSchedule = (SettlementSchedule) auditDetails.get(i).getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				settlementSchedule.setRoleCode("");
				settlementSchedule.setNextRoleCode("");
				settlementSchedule.setTaskId("");
				settlementSchedule.setNextTaskId("");
			}

			settlementSchedule.setWorkflowId(0);
			settlementSchedule.setSettlementHeaderID(headerID);

			if (settlementSchedule.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN) && !approveRec) {
				deleteRecord = true;
			} else if (settlementSchedule.isNewRecord() && !approveRec) {
				saveRecord = true;
				if (settlementSchedule.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					settlementSchedule.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (settlementSchedule.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					settlementSchedule.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (settlementSchedule.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					settlementSchedule.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (settlementSchedule.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (settlementSchedule.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (settlementSchedule.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (settlementSchedule.isNewRecord()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = settlementSchedule.getRecordType();
				recordStatus = settlementSchedule.getRecordStatus();
				settlementSchedule.setRecordType("");
				settlementSchedule.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				settlementScheduleDAO.save(settlementSchedule, type);
			}

			if (updateRecord) {
				settlementScheduleDAO.update(settlementSchedule, type);
			}

			if (deleteRecord) {
				settlementScheduleDAO.delete(settlementSchedule, type);
			}

			if (approveRec) {
				settlementSchedule.setRecordType(rcdType);
				settlementSchedule.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(settlementSchedule);
		}

		return auditDetails;

	}

	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");
		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		FinSettlementHeader settlement = new FinSettlementHeader();
		BeanUtils.copyProperties((FinSettlementHeader) auditHeader.getAuditDetail().getModelData(), settlement);
		if (StringUtils.isNotBlank(settlement.getCancelReasonCode())) {
			settlement.setSettlementStatus("C");
		}
		if (settlement.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getSettlementDAO().deleteSettlementAllcByHeaderId(settlement.getID(), "");
			getSettlementDAO().delete(settlement, "");
		} else {
			settlement.setRoleCode("");
			settlement.setNextRoleCode("");
			settlement.setTaskId("");
			settlement.setNextTaskId("");
			settlement.setWorkflowId(0);

			if (settlement.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				settlement.setRecordType("");
				settlement.setID(getSettlementDAO().save(settlement, ""));
			} else {
				tranType = PennantConstants.TRAN_UPD;
				settlement.setRecordType("");
				getSettlementDAO().update(settlement, "");
			}

			List<SettlementAllocationDetail> settlementAllocationDetails = settlement.getSettlementAllocationDetails();
			if (CollectionUtils.isNotEmpty(settlementAllocationDetails)) {
				for (SettlementAllocationDetail settlementAllocationDetail : settlementAllocationDetails) {
					settlementAllocationDetail.setHeaderID(settlement.getID());
					getSettlementDAO().deleteSettlementAllcById(settlementAllocationDetail, "_Temp");
					getSettlementDAO().saveSettlementAllcDetails(settlementAllocationDetail, "");
				}
			}

			List<SettlementSchedule> settlementSchedules = settlement.getSettlementScheduleList();
			if (CollectionUtils.isNotEmpty(settlementSchedules)
					&& StringUtils.isBlank(settlement.getCancelReasonCode())) {
				for (SettlementSchedule settlementSchedule : settlementSchedules) {
					settlementSchedule.setSettlementHeaderID(settlement.getID());
					getSettlementScheduleDAO().delete(settlementSchedule, "_Temp");
					getSettlementScheduleDAO().save(settlementSchedule, "");
				}
			}

		}
		if (!StringUtils.equals(settlement.getSourceId(), PennantConstants.FINSOURCE_ID_API)) {
			getSettlementDAO().delete(settlement, "_Temp");
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			// getAuditHeaderDAO().addAudit(auditHeader);
		}

		if (StringUtils.isNotBlank(settlement.getCancelReasonCode())) {
			financeMainDAO.updateSettlementFlag(settlement.getFinID(), false);
			processSettlementCancellation(settlement.getFinID(), settlement.getStartDate());
		} else {
			financeMainDAO.updateSettlementFlag(settlement.getFinID(), true);
		}

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(settlement);

		// getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		FinSettlementHeader settlement = (FinSettlementHeader) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getSettlementDAO().deleteSettlementAllcByHeaderId(settlement.getID(), "_Temp");
		getSettlementDAO().delete(settlement, "_Temp");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = getAuditDetails(auditHeader, method);
		auditHeader = nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug("Entering");

		// Get the model object.
		FinSettlementHeader settlement = (FinSettlementHeader) auditDetail.getModelData();
		long settlementTypeId = settlement.getSettlementType();
		String finRefernce = settlement.getFinReference();
		long headerId = settlement.getID();

		// Check the unique keys.
		if (settlement.isNewRecord() && PennantConstants.RECORD_TYPE_NEW.equals(settlement.getRecordType())
				&& getSettlementDAO().isDuplicateKey(settlementTypeId, finRefernce, headerId,
						settlement.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[3];
			parameters[0] = PennantJavaUtil.getLabel("label_SettlementType") + ": " + settlementTypeId;
			parameters[1] = PennantJavaUtil.getLabel("label_loanReferenceNumber") + ": " + finRefernce;

			// auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}

		BigDecimal totSetAmount = BigDecimal.ZERO;
		for (SettlementSchedule settlementSchedule : settlement.getSettlementScheduleList()) {
			totSetAmount = totSetAmount.add(settlementSchedule.getSettlementAmount());
		}

		BigDecimal amount = PennantApplicationUtil.formateAmount(settlement.getSettlementAmount(),
				PennantConstants.defaultCCYDecPos);
		if (totSetAmount.compareTo(amount) > 0) {
			String[] parameters = new String[2];
			parameters[0] = Labels.getLabel("label_SettlementAmount") + ": " + amount;
			parameters[1] = Labels.getLabel("label_SchAmountInList") + ": " + totSetAmount;

			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "91121", parameters, null));
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug("Leaving");
		return auditDetail;
	}

	private AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		Map<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

		FinSettlementHeader settlement = (FinSettlementHeader) auditHeader.getAuditDetail().getModelData();

		String auditTranType = "";

		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (settlement.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}

		if (settlement.getSettlementScheduleList() != null && settlement.getSettlementScheduleList().size() > 0) {
			auditDetailMap.put("SettlementSchedule", setSettlementSchAuditData(settlement, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("SettlementSchedule"));
		}

		settlement.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(settlement);
		auditHeader.setAuditDetails(auditDetails);

		return auditHeader;
	}

	private List<AuditDetail> setSettlementSchAuditData(FinSettlementHeader settlement, String auditTranType,
			String method) {
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		SettlementSchedule settlementSchedule = new SettlementSchedule();
		String[] fields = PennantJavaUtil.getFieldDetails(settlementSchedule, settlementSchedule.getExcludeFields());

		for (int i = 0; i < settlement.getSettlementScheduleList().size(); i++) {
			SettlementSchedule settlementScheduleDetails = settlement.getSettlementScheduleList().get(i);

			if (StringUtils.isEmpty(settlementScheduleDetails.getRecordType())) {
				continue;
			}

			settlementScheduleDetails.setWorkflowId(settlement.getWorkflowId());
			if (settlementScheduleDetails.getSettlementHeaderID() <= 0) {
				settlementScheduleDetails.setSettlementHeaderID(settlement.getID());
			}

			boolean isRcdType = false;

			if (settlementScheduleDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				settlementScheduleDetails.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (settlementScheduleDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				settlementScheduleDetails.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (settlement.isWorkflow()) {
					isRcdType = true;
				}
			} else if (settlementScheduleDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				settlementScheduleDetails.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if ("saveOrUpdate".equals(method) && (isRcdType)) {
				settlementScheduleDetails.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (settlementScheduleDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (settlementScheduleDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| settlementScheduleDetails.getRecordType()
								.equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			settlementScheduleDetails.setRecordStatus(settlement.getRecordStatus());
			settlementScheduleDetails.setLoginDetails(settlement.getUserDetails());
			settlementScheduleDetails.setLastMntOn(settlement.getLastMntOn());

			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
					settlementScheduleDetails.getBefImage(), settlementScheduleDetails));
		}

		return auditDetails;
	}

	@Override
	public FinReceiptData getDues(String finReference, Date valueDate) {

		FinReceiptData receiptData = receiptService.getFinReceiptDataById(finReference, SysParamUtil.getAppDate(),
				AccountingEvent.EARLYSTL, FinServiceEvent.RECEIPT, "");
		receiptData.setEnquiry(true);
		FinanceDetail fd = receiptData.getFinanceDetail();
		if (fd != null) {
			fd.setFinFeeConfigList(null);
			fd.setFinTypeFeesList(null);
		} else {
			return receiptData;

		}
		FinReceiptHeader rch = receiptData.getReceiptHeader();
		FinScheduleData schdData = fd.getFinScheduleData();
		schdData.setFinServiceInstruction(new FinServiceInstruction());

		rch.setFinType(schdData.getFinanceMain().getFinType());
		rch.setReceiptPurpose(FinServiceEvent.EARLYSETTLE);

		rch.setReceiptDate(SysParamUtil.getAppDate());
		rch.setValueDate(valueDate);
		rch.setReceivedDate(valueDate);

		receiptData = receiptService.calcuateDues(receiptData);

		return receiptData;
	}

	@Override
	public void processSettlementCancellation(long finID, Date settlementDate) {
		FinSettlementHeader fsh = settlementDAO.getInitiateSettlementByFinID(finID, "");
		if (fsh == null) {
			return;
		}
		List<FinReceiptHeader> receiptList = finReceiptHeaderDAO.getSettlementReceipts(finID, settlementDate);

		if (CollectionUtils.isEmpty(receiptList)) {
			return;
		}

		// Settlement Changes
		FinanceMain financeMain = getFinanceMainDAO().getFinanceMainById(finID, "", false);

		String eventCode = AccountingEvent.REPAY;

		for (FinReceiptHeader rch : receiptList) {
			long newReceiptId = 0;
			long repayID = 0;
			String paymentType = "";
			String partnerBankActype = "";
			String partnerbank = "";
			FinReceiptHeader receiptHeader = finReceiptHeaderDAO.getReceiptHeaderByID(rch.getReceiptID(), "");
			List<FinReceiptDetail> recDtls = finReceiptDetailDAO.getReceiptHeaderByID(rch.getReceiptID(), "_View");
			receiptHeader.setReceiptDetails(recDtls);
			for (FinReceiptDetail receiptDetail : recDtls) {
				FinRepayHeader rph = financeRepaymentsDAO.getFinRepayHeadersByReceipt(receiptDetail.getReceiptSeqID(),
						"");
				repayID = rph.getRepayID();
				long postingId = postingsDAO.getPostingId();
				postingsPreparationUtil.postReversalsByPostRef(String.valueOf(rch.getReceiptID()), postingId,
						DateUtility.getAppValueDate());
				receiptDetail.setReceiptSeqID(0);
				receiptDetail.setId(0);
				receiptHeader.setReceiptID(0);
				receiptHeader.setBounceDate(SysParamUtil.getAppDate());
				newReceiptId = finReceiptHeaderDAO.generatedReceiptID(receiptHeader);
				receiptDetail.setReceiptID(newReceiptId);
				receiptHeader.setValueDate(receiptDetail.getValueDate());
				partnerbank = receiptDetail.getPartnerBankAc();
				paymentType = receiptDetail.getPaymentType();
				partnerBankActype = receiptDetail.getPartnerBankAcType();
			}
			AEEvent aeEvent = new AEEvent();
			AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
			amountCodes = new AEAmountCodes();
			long postingId = postingsDAO.getPostingId();
			aeEvent.setCustID(financeMain.getCustID());
			aeEvent.setFinReference(financeMain.getFinReference());
			aeEvent.setFinType(financeMain.getFinType());
			aeEvent.setPromotion(financeMain.getPromotionCode());
			aeEvent.setBranch(financeMain.getFinBranch());
			aeEvent.setCcy(financeMain.getFinCcy());
			aeEvent.setPostingUserBranch(receiptHeader.getCashierBranch());
			aeEvent.setLinkedTranId(0);
			aeEvent.setAccountingEvent(eventCode);
			aeEvent.setValueDate(receiptHeader.getValueDate());
			aeEvent.setPostRefId(newReceiptId);
			aeEvent.setPostingId(postingId);
			aeEvent.setEntityCode(financeMain.getEntityCode());

			amountCodes.setUserBranch(receiptHeader.getCashierBranch());
			amountCodes.setFinType(financeMain.getFinType());
			amountCodes.setPartnerBankAc(partnerbank);
			amountCodes.setPartnerBankAcType(partnerBankActype);
			amountCodes.setToExcessAmt(BigDecimal.ZERO);
			amountCodes.setToEmiAdvance(BigDecimal.ZERO);
			amountCodes.setPaymentType(paymentType);

			// Load Accounting configuration always from FinanceType only
			aeEvent.getAcSetIDList().clear();
			aeEvent.getAcSetIDList().add(AccountingConfigCache.getAccountSetID(financeMain.getFinType(), eventCode,
					FinanceConstants.MODULEID_FINTYPE));

			amountCodes.setFinType(financeMain.getFinType());

			Map<String, Object> extDataMap = amountCodes.getDeclaredFieldValues();
			extDataMap.put("PB_ReceiptAmount", receiptHeader.getReceiptAmount());
			extDataMap.put("ae_toExcessAmt", receiptHeader.getReceiptAmount());

			aeEvent.setDataMap(extDataMap);

			// Accounting Entry Execution
			FinReceiptDetail rcd = receiptHeader.getReceiptDetails().get(0);
			rcd.setReceiptID(newReceiptId);
			aeEvent = postingsPreparationUtil.postAccounting(aeEvent);
			receiptHeader.getReceiptDetails().get(0).getRepayHeader().setLinkedTranId(aeEvent.getLinkedTranId());
			finReceiptHeaderDAO.updateExcessAdjustTo(rch.getReceiptID(), RepayConstants.EXCESSADJUSTTO_EXCESS);
			FinRepayHeader finRepayHeader = new FinRepayHeader();
			finRepayHeader.setRepayID(repayID);
			finRepayHeader.setLinkedTranId(aeEvent.getLinkedTranId());
			finRepayHeader.setFinID(receiptHeader.getFinID());
			financeRepaymentsDAO.updateLinkedTranId(finRepayHeader);

			int recordCount = 0;

			recordCount = finExcessAmountDAO.updateExcessBalByRef(receiptHeader.getFinID(),
					RepayConstants.EXCESSADJUSTTO_EXCESS, receiptHeader.getReceiptAmount());
			// If record Not found then record count should be zero. Need to create new Excess Record
			if (recordCount <= 0) {
				FinExcessAmount excess = new FinExcessAmount();
				excess.setFinID(receiptHeader.getFinID());
				excess.setFinReference(receiptHeader.getReference());
				excess.setAmountType(RepayConstants.EXCESSADJUSTTO_EXCESS);
				excess.setAmount(receiptHeader.getReceiptAmount());
				excess.setUtilisedAmt(BigDecimal.ZERO);
				excess.setBalanceAmt(receiptHeader.getReceiptAmount());
				excess.setReservedAmt(BigDecimal.ZERO);
				if (StringUtils.equals(receiptHeader.getReceiptModeStatus(), RepayConstants.PAYSTATUS_DEPOSITED)) {
					excess.setBalanceAmt(BigDecimal.ZERO);
					excess.setReservedAmt(receiptHeader.getReceiptAmount());
					excess.setAmount(receiptHeader.getReceiptAmount());

				}
				finExcessAmountDAO.saveExcess(excess);
			}

		}

		financeMainDAO.updateSettlementFlag(finID, false);
		settlementDAO.updateSettlementStatus(fsh.getID(), "C");
	}

	@Override
	public void processSettlement(long settlementHeaderID, Date settlementDate) {
		FinSettlementHeader fsh = settlementDAO.getSettlementById(settlementHeaderID, "_View");
		if (fsh == null) {
			return;
		}
		List<SettlementAllocationDetail> sadList = settlementDAO.getSettlementAllcDetailByHdrID(fsh.getID(), "_View");
		if (CollectionUtils.isEmpty(sadList)) {
			return;
		}
		BigDecimal actualReceiptAmount = BigDecimal.ZERO;

		FinReceiptData receiptData = receiptService.getFinReceiptDataById(fsh.getFinReference(),
				SysParamUtil.getAppDate(), AccountingEvent.EARLYSTL, FinServiceEvent.RECEIPT, "");
		BigDecimal excessAmount = BigDecimal.ZERO;
		FinReceiptHeader rch = receiptData.getReceiptHeader();
		List<FinExcessAmount> excessList = rch.getExcessAmounts();
		List<ManualAdvise> payabaleList = rch.getPayableAdvises();
		if (CollectionUtils.isNotEmpty(excessList)) {
			for (FinExcessAmount excess : excessList) {
				excessAmount = excessAmount.add(excess.getBalanceAmt());
			}
		}
		if (CollectionUtils.isNotEmpty(payabaleList)) {
			for (ManualAdvise advise : payabaleList) {
				excessAmount = excessAmount.add(advise.getBalanceAmt());
			}
		}

		if (excessAmount.compareTo(fsh.getSettlementAmount()) < 0) {
			processSettlementCancellation(fsh.getFinID(), fsh.getStartDate());
			return;
		}
		FinanceDetail fd = receiptData.getFinanceDetail();
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();
		rch.setReceiptAmount(rch.getReceiptAmount().add(rch.getTdsAmount()));
		rch.setReceiptDate(SysParamUtil.getAppDate());
		rch.setValueDate(fsh.getOtsDate());
		rch.setReceivedDate(fsh.getOtsDate());
		rch.setFinID(fm.getFinID());
		rch.setReference(fsh.getFinReference());
		rch.setCashierBranch(fm.getFinBranch());
		rch.setFinType(schdData.getFinanceMain().getFinType());
		rch.setReceiptAmount(fsh.getSettlementAmount());
		rch.setReceiptPurpose(FinServiceEvent.EARLYSETTLE);
		rch.setExcessAdjustTo(RepayConstants.EXAMOUNTTYPE_EXCESS);
		rch.setReceiptMode(ReceiptMode.EXCESS);
		rch.setAllocationType(AllocationType.AUTO);
		rch.setEffectSchdMethod(CalculationConstants.EARLYPAY_ADJMUR);
		rch.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
		rch.setRecAgainst(RepayConstants.RECEIPTTO_FINANCE);
		rch.setReceiptModeStatus(RepayConstants.PAYSTATUS_REALIZED);

		receiptData = receiptService.calcuateDues(receiptData);

		List<ReceiptAllocationDetail> radList = rch.getAllocations();
		if (CollectionUtils.isEmpty(radList)) {
			return;
		}
		// Set waiver amounts
		for (ReceiptAllocationDetail rad : radList) {
			for (SettlementAllocationDetail sad : sadList) {
				if (StringUtils.equals(sad.getAllocationType().trim(), rad.getAllocationType())
						&& sad.getAllocationTo() == rad.getAllocationTo()) {
					rad.setWaivedAmount(sad.getWaivedAmount());
					break;
				}
			}
		}

		// Set Paid Amounts
		for (ReceiptAllocationDetail rad : radList) {
			if (excessAmount.compareTo(BigDecimal.ZERO) <= 0) {
				break;
			}
			BigDecimal dueAmount = rad.getTotalDue().subtract(rad.getWaivedAmount());
			BigDecimal allocAmount = BigDecimal.ZERO;
			if (excessAmount.compareTo(dueAmount) >= 0) {
				allocAmount = dueAmount;
			} else {
				allocAmount = excessAmount;
			}
			actualReceiptAmount = actualReceiptAmount.add(allocAmount);
			rad.setPaidAmount(allocAmount);
			excessAmount = excessAmount.subtract(allocAmount);
			rad.setBalance(rad.getTotalDue().subtract(rad.getWaivedAmount().add(rad.getPaidAmount())));
		}

		// Now waive off the remaining dues
		for (ReceiptAllocationDetail rad : radList) {
			BigDecimal dueAmount = rad.getTotalDue().subtract(rad.getWaivedAmount().add(rad.getPaidAmount()));
			if (dueAmount.compareTo(BigDecimal.ZERO) <= 0) {
				continue;
			}
			rad.setWaivedAmount(dueAmount);
		}

		if (excessAmount.compareTo(BigDecimal.ZERO) > 0) {
			// if excess still exists,reduce waive and allocate paid
			for (ReceiptAllocationDetail rad : radList) {
				if (excessAmount.compareTo(BigDecimal.ZERO) <= 0) {
					break;
				}
				BigDecimal waivedAMount = rad.getWaivedAmount();
				BigDecimal allocAmount = BigDecimal.ZERO;
				if (excessAmount.compareTo(waivedAMount) >= 0) {
					allocAmount = waivedAMount;
				} else {
					allocAmount = excessAmount;
				}
				rad.setPaidAmount(rad.getPaidAmount().add(excessAmount));
				rad.setWaivedAmount(rad.getWaivedAmount().subtract(excessAmount));
				actualReceiptAmount = actualReceiptAmount.add(allocAmount);
				excessAmount = excessAmount.subtract(allocAmount);
				rad.setBalance(rad.getTotalDue().subtract(rad.getWaivedAmount().add(rad.getPaidAmount())));
			}

		}

		receiptData.setTotalPastDues(receiptCalculator.getTotalNetPastDue(receiptData));
		rch.setReceiptAmount(actualReceiptAmount);
		for (XcessPayables xcess : rch.getXcessPayables()) {
			BigDecimal balAmount = xcess.getBalanceAmt();
			if (actualReceiptAmount.compareTo(BigDecimal.ZERO) <= 0) {
				break;
			}
			if (actualReceiptAmount.compareTo(balAmount) >= 0) {
				balAmount = actualReceiptAmount;
			}
			actualReceiptAmount = actualReceiptAmount.subtract(balAmount);
			xcess.setTotPaidNow(balAmount);
			xcess.setBalanceAmt(xcess.getBalanceAmt().subtract(balAmount));
		}
		receiptService.createXcessRCD(receiptData);

		for (ReceiptAllocationDetail allocate : receiptData.getReceiptHeader().getAllocations()) {
			allocate.setPaidAvailable(allocate.getPaidAmount());
			allocate.setFeeTypeCode(allocate.getFeeTypeCode());
			allocate.setWaivedAvailable(allocate.getWaivedAmount());
			allocate.setPaidAmount(BigDecimal.ZERO);
			allocate.setPaidGST(BigDecimal.ZERO);
			allocate.setTotalPaid(BigDecimal.ZERO);
			allocate.setBalance(allocate.getTotalDue());
			allocate.setWaivedAmount(BigDecimal.ZERO);
			allocate.setWaivedGST(BigDecimal.ZERO);
		}
		receiptData.setBuildProcess("R");
		receiptData = receiptCalculator.initiateReceipt(receiptData, false);

		receiptData.setOTSByEod(true);
		try {
			receiptData = receiptService.doApproveReceipt(receiptData);
			settlementDAO.updateSettlementStatus(fsh.getID(), "P");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public ReceiptService getReceiptService() {
		return receiptService;
	}

	public void setReceiptService(ReceiptService receiptService) {
		this.receiptService = receiptService;
	}

	public FinSettlementHeader getSettlementByRef(String finReference, String type) {
		FinSettlementHeader header = settlementDAO.getSettlementByRef(finReference, "_View");
		header.setSettlementAllocationDetails(settlementDAO.getSettlementAllcDetailByHdrID(header.getID(), "_View"));
		return header;
	}

	public FinanceMainDAO getFinanceMainDAO() {
		return financeMainDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public void setFinReceiptHeaderDAO(FinReceiptHeaderDAO finReceiptHeaderDAO) {
		this.finReceiptHeaderDAO = finReceiptHeaderDAO;
	}

	public void setFinReceiptDetailDAO(FinReceiptDetailDAO finReceiptDetailDAO) {
		this.finReceiptDetailDAO = finReceiptDetailDAO;
	}

	public void setPostingsDAO(PostingsDAO postingsDAO) {
		this.postingsDAO = postingsDAO;
	}

	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}

	public void setFinExcessAmountDAO(FinExcessAmountDAO finExcessAmountDAO) {
		this.finExcessAmountDAO = finExcessAmountDAO;
	}

	public void setFinanceRepaymentsDAO(FinanceRepaymentsDAO financeRepaymentsDAO) {
		this.financeRepaymentsDAO = financeRepaymentsDAO;
	}

	public ReceiptCalculator getReceiptCalculator() {
		return receiptCalculator;
	}

	public void setReceiptCalculator(ReceiptCalculator receiptCalculator) {
		this.receiptCalculator = receiptCalculator;
	}

	@Override
	public BigDecimal getSettlementAountReceived(long finId) {

		return finExcessAmountDAO.getSettlementAmountReceived(finId);
	}

}
