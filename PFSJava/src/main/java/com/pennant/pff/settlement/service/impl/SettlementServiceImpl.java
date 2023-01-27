package com.pennant.pff.settlement.service.impl;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;

import com.pennant.app.constants.CalculationConstants;
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
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.ReceiptService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.RepayConstants;
import com.pennant.cache.util.AccountingConfigCache;
import com.pennant.pff.settlement.dao.SettlementDAO;
import com.pennant.pff.settlement.dao.SettlementScheduleDAO;
import com.pennant.pff.settlement.model.FinSettlementHeader;
import com.pennant.pff.settlement.model.SettlementAllocationDetail;
import com.pennant.pff.settlement.model.SettlementSchedule;
import com.pennant.pff.settlement.service.SettlementService;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
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
	private FinanceMainDAO financeMainDAO;
	private FinReceiptHeaderDAO finReceiptHeaderDAO;
	private FinReceiptDetailDAO finReceiptDetailDAO;
	private PostingsDAO postingsDAO;
	private FinExcessAmountDAO finExcessAmountDAO;
	private FinanceRepaymentsDAO financeRepaymentsDAO;
	private ReceiptCalculator receiptCalculator;
	private ReceiptService receiptService;
	private PostingsPreparationUtil postingsPreparationUtil;

	@Override
	public FinSettlementHeader getsettlementById(long id) {
		String type = TableType.VIEW.getSuffix();
		FinSettlementHeader header = settlementDAO.getSettlementById(id, type);

		if (header != null) {
			header.setSettlementAllocationDetails(settlementDAO.getSettlementAllcDetailByHdrID(id, type));
			header.setSettlementScheduleList(settlementScheduleDAO.getSettlementScheduleDetails(id, type));
		}

		return header;
	}

	@Override
	public AuditHeader delete(AuditHeader ah) {
		logger.debug(Literal.ENTERING);

		ah = businessValidation(ah, "delete");
		if (!ah.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return ah;
		}

		FinSettlementHeader header = (FinSettlementHeader) ah.getAuditDetail().getModelData();

		settlementDAO.deleteSettlementAllcByHeaderId(header.getId(), "");
		settlementDAO.delete(header, "");

		auditHeaderDAO.addAudit(ah);

		logger.debug(Literal.LEAVING);
		return ah;
	}

	@Override
	public AuditHeader saveOrUpdate(AuditHeader ah) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<>();
		ah = businessValidation(ah, "saveOrUpdate");

		if (!ah.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return ah;
		}

		String tableType = "";
		FinSettlementHeader settlement = (FinSettlementHeader) ah.getAuditDetail().getModelData();
		if (StringUtils.isNotBlank(settlement.getCancelReasonCode())) {
			settlement.setSettlementStatus("C");
		}

		if (settlement.isWorkflow()) {
			tableType = "_Temp";
		}

		if (settlement.isNewRecord()) {
			settlement.setId(settlementDAO.save(settlement, tableType));
			ah.getAuditDetail().setModelData(settlement);
			ah.setAuditReference(String.valueOf(settlement.getId()));
		} else {
			settlementDAO.update(settlement, tableType);
		}

		if (CollectionUtils.isNotEmpty(settlement.getSettlementScheduleList())
				&& StringUtils.isBlank(settlement.getCancelReasonCode())) {
			settlementScheduleList(settlement.getAuditDetailMap().get("SettlementSchedule"), tableType,
					settlement.getId());
		}

		List<SettlementAllocationDetail> details = settlement.getSettlementAllocationDetails();
		if (CollectionUtils.isNotEmpty(details)) {
			for (SettlementAllocationDetail settlementAllocationDetail : details) {
				settlementAllocationDetail.setHeaderID(settlement.getId());
				if (settlementAllocationDetail.isNewRecord()) {
					settlementDAO.deleteSettlementAllcById(settlementAllocationDetail, tableType);
					settlementDAO.saveSettlementAllcDetails(settlementAllocationDetail, tableType);
				} else {
					settlementDAO.deleteSettlementAllcById(settlementAllocationDetail, tableType);
					settlementDAO.deleteSettlementAllcById(settlementAllocationDetail, "");
					settlementDAO.saveSettlementAllcDetails(settlementAllocationDetail, tableType);
				}
			}
		}

		ah.setAuditDetails(auditDetails);
		auditHeaderDAO.addAudit(ah);

		logger.debug(Literal.LEAVING);
		return ah;
	}

	private List<AuditDetail> settlementScheduleList(List<AuditDetail> auditDetails, String type, long headerID) {
		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {
			SettlementSchedule ss = (SettlementSchedule) auditDetails.get(i).getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";

			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				ss.setRoleCode("");
				ss.setNextRoleCode("");
				ss.setTaskId("");
				ss.setNextTaskId("");
			}

			ss.setWorkflowId(0);
			ss.setSettlementHeaderID(headerID);

			if (ss.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN) && !approveRec) {
				deleteRecord = true;
			} else if (ss.isNewRecord() && !approveRec) {
				saveRecord = true;
				if (ss.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					ss.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (ss.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					ss.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (ss.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					ss.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}
			} else if (ss.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (ss.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (ss.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (ss.isNewRecord()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = ss.getRecordType();
				recordStatus = ss.getRecordStatus();
				ss.setRecordType("");
				ss.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				settlementScheduleDAO.delete(ss, "_Temp");
			}

			if (saveRecord) {
				settlementScheduleDAO.save(ss, type);
			}

			if (updateRecord) {
				settlementScheduleDAO.update(ss, type);
			}

			if (deleteRecord) {
				settlementScheduleDAO.delete(ss, type);
			}

			if (approveRec) {
				ss.setRecordType(rcdType);
				ss.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(ss);
		}

		return auditDetails;
	}

	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

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
			settlementDAO.deleteSettlementAllcByHeaderId(settlement.getId(), "");
			settlementDAO.delete(settlement, "");
		} else {
			settlement.setRoleCode("");
			settlement.setNextRoleCode("");
			settlement.setTaskId("");
			settlement.setNextTaskId("");
			settlement.setWorkflowId(0);

			if (settlement.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				settlement.setRecordType("");
				settlement.setId(settlementDAO.save(settlement, ""));
			} else {
				tranType = PennantConstants.TRAN_UPD;
				settlement.setRecordType("");
				settlementDAO.update(settlement, "");
			}

			List<SettlementAllocationDetail> settlementAllocationDetails = settlement.getSettlementAllocationDetails();
			if (CollectionUtils.isNotEmpty(settlementAllocationDetails)) {
				for (SettlementAllocationDetail settlementAllocationDetail : settlementAllocationDetails) {
					settlementAllocationDetail.setHeaderID(settlement.getId());
					settlementDAO.deleteSettlementAllcById(settlementAllocationDetail, "_Temp");
					settlementDAO.saveSettlementAllcDetails(settlementAllocationDetail, "");
				}
			}

			if (CollectionUtils.isNotEmpty(settlement.getSettlementScheduleList())
					&& StringUtils.isBlank(settlement.getCancelReasonCode())) {
				settlementScheduleList(settlement.getAuditDetailMap().get("SettlementSchedule"), "",
						settlement.getId());
			}

		}
		if (!PennantConstants.FINSOURCE_ID_API.equals(settlement.getSourceId())) {
			settlementDAO.delete(settlement, "_Temp");
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		}

		if (StringUtils.isNotBlank(settlement.getCancelReasonCode())) {
			financeMainDAO.updateSettlementFlag(settlement.getFinID(), true);
			processSettlementCancellation(settlement.getFinID(), settlement.getStartDate());
		} else {
			financeMainDAO.updateSettlementFlag(settlement.getFinID(), true);
		}

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(settlement);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		FinSettlementHeader settlement = (FinSettlementHeader) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		settlementDAO.deleteSettlementAllcByHeaderId(settlement.getId(), "_Temp");
		settlementDAO.delete(settlement, "_Temp");

		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		getAuditDetails(auditHeader, method);
		auditHeader = nextProcess(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug(Literal.ENTERING);

		FinSettlementHeader settlement = (FinSettlementHeader) auditDetail.getModelData();

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

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	private AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {
		List<AuditDetail> auditDetails = new ArrayList<>();
		Map<String, List<AuditDetail>> auditDetailMap = new HashMap<>();

		FinSettlementHeader settlement = (FinSettlementHeader) auditHeader.getAuditDetail().getModelData();

		String auditTranType = "";

		if (("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method))
				&& settlement.isWorkflow()) {
			auditTranType = PennantConstants.TRAN_WF;
		}

		if (CollectionUtils.isNotEmpty(settlement.getSettlementScheduleList())) {
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
		List<AuditDetail> auditDetails = new ArrayList<>();

		SettlementSchedule ss = new SettlementSchedule();
		String[] fields = PennantJavaUtil.getFieldDetails(ss, ss.getExcludeFields());

		List<SettlementSchedule> settlementSchedules = settlement.getSettlementScheduleList();

		for (int i = 0; i < settlementSchedules.size(); i++) {
			SettlementSchedule settlementSchedule = settlementSchedules.get(i);

			if (StringUtils.isEmpty(settlementSchedule.getRecordType())) {
				continue;
			}

			settlementSchedule.setWorkflowId(settlement.getWorkflowId());
			if (settlementSchedule.getSettlementHeaderID() <= 0) {
				settlementSchedule.setSettlementHeaderID(settlement.getId());
			}

			boolean isRcdType = false;

			if (settlementSchedule.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				settlementSchedule.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (settlementSchedule.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				settlementSchedule.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (settlement.isWorkflow()) {
					isRcdType = true;
				}
			} else if (settlementSchedule.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				settlementSchedule.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if ("saveOrUpdate".equals(method) && (isRcdType)) {
				settlementSchedule.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (settlementSchedule.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (settlementSchedule.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| settlementSchedule.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			settlementSchedule.setRecordStatus(settlement.getRecordStatus());
			settlementSchedule.setLoginDetails(settlement.getUserDetails());
			settlementSchedule.setLastMntOn(settlement.getLastMntOn());

			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
					settlementSchedule.getBefImage(), settlementSchedule));
		}

		return auditDetails;
	}

	@Override
	public FinReceiptData getDues(String finReference, Date valueDate) {
		Date appDate = SysParamUtil.getAppDate();

		FinReceiptData receiptData = receiptService.getFinReceiptDataById(finReference, appDate,
				AccountingEvent.EARLYSTL, FinServiceEvent.RECEIPT, "");
		receiptData.setEnquiry(true);

		FinanceDetail fd = receiptData.getFinanceDetail();

		if (fd == null) {
			return receiptData;
		}

		fd.setFinFeeConfigList(null);
		fd.setFinTypeFeesList(null);

		FinReceiptHeader rch = receiptData.getReceiptHeader();
		FinScheduleData schdData = fd.getFinScheduleData();
		schdData.setFinServiceInstruction(new FinServiceInstruction());

		rch.setFinType(schdData.getFinanceMain().getFinType());
		rch.setReceiptPurpose(FinServiceEvent.EARLYSETTLE);
		rch.setReceiptDate(appDate);
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

		FinanceMain financeMain = financeMainDAO.getFinanceMainById(finID, "", false);

		String eventCode = AccountingEvent.REPAY;

		Date appDate = SysParamUtil.getAppDate();
		for (FinReceiptHeader rch : receiptList) {
			long newReceiptId = 0;
			long repayID = 0;
			String paymentType = "";
			String partnerBankActype = "";
			String partnerbank = "";
			FinReceiptHeader receiptHeader = finReceiptHeaderDAO.getReceiptHeaderByID(rch.getReceiptID(), "");
			List<FinReceiptDetail> recDtls = finReceiptDetailDAO.getReceiptHeaderByID(rch.getReceiptID(), "_View");
			receiptHeader.setReceiptDetails(recDtls);
			for (FinReceiptDetail rcd : recDtls) {
				FinRepayHeader rph = financeRepaymentsDAO.getFinRepayHeadersByReceipt(rcd.getReceiptSeqID(), "");
				repayID = rph.getRepayID();
				long postingId = postingsDAO.getPostingId();

				postingsPreparationUtil.postReversalsByPostRef(String.valueOf(rch.getReceiptID()), postingId, appDate);
				rcd.setReceiptSeqID(0);
				rcd.setId(0);
				receiptHeader.setReceiptID(0);
				receiptHeader.setBounceDate(appDate);
				newReceiptId = finReceiptHeaderDAO.generatedReceiptID(receiptHeader);
				rcd.setReceiptID(newReceiptId);
				receiptHeader.setValueDate(rcd.getValueDate());
				partnerbank = rcd.getPartnerBankAc();
				paymentType = rcd.getPaymentType();
				partnerBankActype = rcd.getPartnerBankAcType();
			}

			AEEvent aeEvent = new AEEvent();

			AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();

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

			aeEvent.getAcSetIDList().clear();
			aeEvent.getAcSetIDList().add(AccountingConfigCache.getAccountSetID(financeMain.getFinType(), eventCode,
					FinanceConstants.MODULEID_FINTYPE));

			amountCodes.setFinType(financeMain.getFinType());

			Map<String, Object> extDataMap = amountCodes.getDeclaredFieldValues();
			extDataMap.put("PB_ReceiptAmount", receiptHeader.getReceiptAmount());
			extDataMap.put("ae_toExcessAmt", receiptHeader.getReceiptAmount());

			aeEvent.setDataMap(extDataMap);

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

			int recordCount = finExcessAmountDAO.updateExcessBalByRef(receiptHeader.getFinID(),
					RepayConstants.EXCESSADJUSTTO_EXCESS, receiptHeader.getReceiptAmount());

			if (recordCount <= 0) {
				FinExcessAmount excess = new FinExcessAmount();
				excess.setFinID(receiptHeader.getFinID());
				excess.setFinReference(receiptHeader.getReference());
				excess.setAmountType(RepayConstants.EXCESSADJUSTTO_EXCESS);
				excess.setAmount(receiptHeader.getReceiptAmount());
				excess.setUtilisedAmt(BigDecimal.ZERO);
				excess.setBalanceAmt(receiptHeader.getReceiptAmount());
				excess.setReservedAmt(BigDecimal.ZERO);

				if (RepayConstants.PAYSTATUS_DEPOSITED.equals(receiptHeader.getReceiptModeStatus())) {
					excess.setBalanceAmt(BigDecimal.ZERO);
					excess.setReservedAmt(receiptHeader.getReceiptAmount());
					excess.setAmount(receiptHeader.getReceiptAmount());
				}

				finExcessAmountDAO.saveExcess(excess);
			}
		}

		financeMainDAO.updateSettlementFlag(finID, false);
		settlementDAO.updateSettlementStatus(fsh.getId(), "C");
	}

	@Override
	public void processSettlement(long settlementHeaderID, Date settlementDate) {
		FinSettlementHeader fsh = settlementDAO.getSettlementById(settlementHeaderID, "_View");

		if (fsh == null) {
			return;
		}

		List<SettlementAllocationDetail> sadList = settlementDAO.getSettlementAllcDetailByHdrID(fsh.getId(), "_View");
		if (CollectionUtils.isEmpty(sadList)) {
			return;
		}

		BigDecimal actualReceiptAmount = BigDecimal.ZERO;

		Date appDate = SysParamUtil.getAppDate();

		FinReceiptData receiptData = receiptService.getFinReceiptDataById(fsh.getFinReference(), appDate,
				AccountingEvent.EARLYSTL, FinServiceEvent.RECEIPT, "");

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
		rch.setReceiptDate(appDate);
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

		for (ReceiptAllocationDetail rad : radList) {
			for (SettlementAllocationDetail sad : sadList) {
				if (StringUtils.equals(sad.getAllocationType().trim(), rad.getAllocationType())
						&& sad.getAllocationTo() == rad.getAllocationTo()) {
					rad.setWaivedAmount(sad.getWaivedAmount());
					break;
				}
			}
		}

		for (ReceiptAllocationDetail rad : radList) {
			if (excessAmount.compareTo(BigDecimal.ZERO) <= 0) {
				break;
			}

			BigDecimal dueAmount = rad.getTotalDue().subtract(rad.getWaivedAmount());
			BigDecimal allocAmount = excessAmount;

			if (excessAmount.compareTo(dueAmount) >= 0) {
				allocAmount = dueAmount;
			}

			actualReceiptAmount = actualReceiptAmount.add(allocAmount);
			rad.setPaidAmount(allocAmount);
			excessAmount = excessAmount.subtract(allocAmount);
			rad.setBalance(rad.getTotalDue().subtract(rad.getWaivedAmount().add(rad.getPaidAmount())));
		}

		for (ReceiptAllocationDetail rad : radList) {
			BigDecimal dueAmount = rad.getTotalDue().subtract(rad.getWaivedAmount().add(rad.getPaidAmount()));
			if (dueAmount.compareTo(BigDecimal.ZERO) <= 0) {
				continue;
			}
			rad.setWaivedAmount(dueAmount);
		}

		if (excessAmount.compareTo(BigDecimal.ZERO) > 0) {
			for (ReceiptAllocationDetail rad : radList) {
				if (excessAmount.compareTo(BigDecimal.ZERO) <= 0) {
					break;
				}
				BigDecimal waivedAMount = rad.getWaivedAmount();
				BigDecimal allocAmount = excessAmount;

				if (excessAmount.compareTo(waivedAMount) >= 0) {
					allocAmount = waivedAMount;
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
			receiptService.doApproveReceipt(receiptData);
			settlementDAO.updateSettlementStatus(fsh.getId(), "P");
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
	}

	public FinSettlementHeader getSettlementByRef(String finReference, String type) {
		FinSettlementHeader header = settlementDAO.getSettlementByRef(finReference, "_View");
		header.setSettlementAllocationDetails(settlementDAO.getSettlementAllcDetailByHdrID(header.getId(), "_View"));
		header.setSettlementScheduleList(settlementScheduleDAO.getSettlementScheduleDetails(header.getId(), "_View"));

		return header;
	}

	@Override
	public BigDecimal getSettlementAountReceived(long finId) {
		return finExcessAmountDAO.getSettlementAmountReceived(finId);
	}

	@Autowired
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	@Autowired
	public void setSettlementDAO(SettlementDAO settlementDAO) {
		this.settlementDAO = settlementDAO;
	}

	@Autowired
	public void setSettlementScheduleDAO(SettlementScheduleDAO settlementScheduleDAO) {
		this.settlementScheduleDAO = settlementScheduleDAO;
	}

	@Autowired
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	@Autowired
	public void setFinReceiptHeaderDAO(FinReceiptHeaderDAO finReceiptHeaderDAO) {
		this.finReceiptHeaderDAO = finReceiptHeaderDAO;
	}

	@Autowired
	public void setFinReceiptDetailDAO(FinReceiptDetailDAO finReceiptDetailDAO) {
		this.finReceiptDetailDAO = finReceiptDetailDAO;
	}

	@Autowired
	public void setPostingsDAO(PostingsDAO postingsDAO) {
		this.postingsDAO = postingsDAO;
	}

	@Autowired
	public void setFinExcessAmountDAO(FinExcessAmountDAO finExcessAmountDAO) {
		this.finExcessAmountDAO = finExcessAmountDAO;
	}

	@Autowired
	public void setFinanceRepaymentsDAO(FinanceRepaymentsDAO financeRepaymentsDAO) {
		this.financeRepaymentsDAO = financeRepaymentsDAO;
	}

	@Autowired
	public void setReceiptCalculator(ReceiptCalculator receiptCalculator) {
		this.receiptCalculator = receiptCalculator;
	}

	public void setReceiptService(ReceiptService receiptService) {
		this.receiptService = receiptService;
	}

	@Autowired
	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}

}
