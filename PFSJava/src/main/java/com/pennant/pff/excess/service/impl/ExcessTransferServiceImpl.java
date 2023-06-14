package com.pennant.pff.excess.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.constants.AccountConstants;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.dao.rmtmasters.AccountingSetDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinExcessAmountReserve;
import com.pennant.backend.model.finance.FinExcessMovement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.RepayConstants;
import com.pennant.pff.excess.dao.FinExcessTransferDAO;
import com.pennant.pff.excess.model.FinExcessTransfer;
import com.pennant.pff.excess.service.ExcessTransferService;
import com.pennant.pff.mandate.ChequeSatus;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.receipt.constants.ExcessType;

public class ExcessTransferServiceImpl extends GenericService<FinExcessTransfer> implements ExcessTransferService {
	private static Logger logger = LogManager.getLogger(ExcessTransferServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private FinExcessTransferDAO finExcessTransferDAO;
	private CustomerDAO customerDAO;
	private FinanceMainDAO financeMainDAO;
	private FinExcessAmountDAO finExcessAmountDAO;
	private PostingsPreparationUtil postingsPreparationUtil;
	private AccountingSetDAO accountingSetDAO;

	public ExcessTransferServiceImpl() {
		super();
	}

	@Override
	public FinExcessTransfer getExcessTransfer(long transferId) {
		return finExcessTransferDAO.getExcessTransferByTransferId(transferId, "_View");
	}

	@Override
	public FinExcessTransfer getExcessTransferData(long finId, long trnasferId) {
		return finExcessTransferDAO.getExcessTransferByFinId(finId, trnasferId, "_View");
	}

	@Override
	public boolean isReferenceExist(String finReference) {
		return finExcessTransferDAO.isFinReceferenceExist(finReference, "_Temp");
	}

	@Override
	public FinExcessAmount getFinExcessAmountById(long excessId) {
		return finExcessAmountDAO.getFinExcessAmountById(excessId, "");
	}

	@Override
	public FinExcessTransfer getFinExcessData(long finID) {
		FinExcessTransfer transfer = new FinExcessTransfer();

		FinanceMain fm = financeMainDAO.getFinanceMainById(finID, "_View", false);
		transfer.setFinId(finID);
		transfer.setCustomer(customerDAO.getCustomerByID(fm.getCustID()));
		transfer.setFinBranch(fm.getFinBranch());
		transfer.setFinType(fm.getFinType());
		transfer.setFinReference(fm.getFinReference());

		return transfer;
	}

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader);
		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		FinExcessTransfer finExcessTransfer = (FinExcessTransfer) auditHeader.getAuditDetail().getModelData();
		finExcessTransferDAO.delete(finExcessTransfer, TableType.MAIN_TAB);

		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader);

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		FinExcessTransfer transfer = (FinExcessTransfer) auditHeader.getAuditDetail().getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (transfer.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (transfer.isNewRecord()) {
			transfer.setId(Long.parseLong(finExcessTransferDAO.save(transfer, tableType)));
			auditHeader.getAuditDetail().setModelData(transfer);
			auditHeader.setAuditReference(String.valueOf(transfer.getId()));
		} else {
			finExcessTransferDAO.update(transfer, tableType);
		}

		FinExcessAmountReserve exReserve = finExcessAmountDAO.getExcessReserve(transfer.getId(),
				transfer.getTransferFromId(), RepayConstants.RECEIPTTYPE_TRANSFER);

		if (exReserve == null) {
			finExcessAmountDAO.updateExcessReserve(transfer.getTransferFromId(), transfer.getTransferAmount());

			finExcessAmountDAO.saveExcessReserveLog(transfer.getId(), transfer.getTransferFromId(),
					transfer.getTransferAmount(), RepayConstants.RECEIPTTYPE_TRANSFER);
		} else {
			if (transfer.getTransferAmount().compareTo(exReserve.getReservedAmt()) != 0) {
				BigDecimal diffInReserve = transfer.getTransferAmount().subtract(exReserve.getReservedAmt());

				finExcessAmountDAO.updateExcessReserve(transfer.getTransferFromId(), diffInReserve);
				finExcessAmountDAO.updateExcessReserveLog(transfer.getId(), transfer.getTransferFromId(), diffInReserve,
						RepayConstants.RECEIPTTYPE_TRANSFER);
			}
		}

		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		String tranType = "";
		auditHeader = businessValidation(auditHeader);

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		FinExcessTransfer transfer = new FinExcessTransfer();
		BeanUtils.copyProperties(auditHeader.getAuditDetail().getModelData(), transfer);

		finExcessTransferDAO.delete(transfer, TableType.TEMP_TAB);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(transfer.getRecordType())) {
			auditHeader.getAuditDetail()
					.setBefImage(finExcessTransferDAO.getExcessTransferByTransferId(transfer.getId(), ""));
		}

		long linkedTranId = 0;

		AEEvent aeEvent = executeAccounting(transfer);
		if (aeEvent.isPostingSucess()) {
			linkedTranId = aeEvent.getLinkedTranId();
		}

		FinExcessAmountReserve exReserve = finExcessAmountDAO.getExcessReserve(transfer.getId(),
				transfer.getTransferFromId(), RepayConstants.RECEIPTTYPE_TRANSFER);

		if (exReserve != null) {
			finExcessAmountDAO.updateUtilise(transfer.getTransferFromId(), transfer.getTransferAmount());
			finExcessAmountDAO.deleteExcessReserve(transfer.getId(), transfer.getTransferFromId(),
					RepayConstants.RECEIPTTYPE_TRANSFER);
		} else {
			finExcessAmountDAO.updateUtiliseOnly(transfer.getTransferFromId(), transfer.getTransferAmount());
		}

		FinExcessMovement movement = new FinExcessMovement();
		movement.setExcessID(transfer.getTransferFromId());
		movement.setReceiptID(transfer.getId());
		movement.setMovementType(RepayConstants.RECEIPTTYPE_TRANSFER);
		movement.setTranType(AccountConstants.TRANTYPE_DEBIT);
		movement.setAmount(transfer.getTransferAmount());
		finExcessAmountDAO.saveExcessMovement(movement);

		FinExcessAmount excess = new FinExcessAmount();
		excess.setFinID(transfer.getFinId());
		excess.setFinReference(transfer.getFinReference());
		excess.setAmountType(transfer.getTransferToType());
		excess.setAmount(transfer.getTransferAmount());
		excess.setUtilisedAmt(BigDecimal.ZERO);
		excess.setBalanceAmt(transfer.getTransferAmount());
		excess.setReservedAmt(BigDecimal.ZERO);
		excess.setReceiptID(transfer.getId());
		excess.setPostDate(transfer.getTransferDate());
		excess.setValueDate(transfer.getTransferDate());
		finExcessAmountDAO.saveExcess(excess);
		long tranferToExcessId = excess.getExcessID();

		transfer.setTransferToId(tranferToExcessId);
		transfer.setLinkedTranId(linkedTranId);

		transfer.setRoleCode("");
		transfer.setNextRoleCode("");
		transfer.setTaskId("");
		transfer.setNextTaskId("");
		transfer.setWorkflowId(0);
		transfer.setRecordType("");
		transfer.setStatus(ChequeSatus.REALISE);

		finExcessTransferDAO.save(transfer, TableType.MAIN_TAB);

		FinExcessMovement toMovement = new FinExcessMovement();
		toMovement.setExcessID(tranferToExcessId);
		toMovement.setReceiptID(transfer.getId());
		toMovement.setMovementType(RepayConstants.RECEIPTTYPE_TRANSFER);
		toMovement.setTranType(AccountConstants.TRANTYPE_CREDIT);
		toMovement.setAmount(transfer.getTransferAmount());
		finExcessAmountDAO.saveExcessMovement(toMovement);

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeaderDAO.addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(transfer);

		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	@Override
	public AEEvent executeAccounting(FinExcessTransfer excessTransfer) {
		String eventCode = AccountingEvent.EXTRF;
		Date appDate = SysParamUtil.getAppDate();

		AEEvent aeEvent = new AEEvent();
		AEAmountCodes amountCodes = new AEAmountCodes();

		FinanceMain fm = financeMainDAO.getFinanceMainForExcessTransfer(excessTransfer.getFinId());

		aeEvent.setFinReference(excessTransfer.getFinReference());
		aeEvent.setAccountingEvent(eventCode);

		aeEvent.setPostDate(appDate);
		aeEvent.setValueDate(excessTransfer.getTransferDate());
		aeEvent.setSchdDate(excessTransfer.getTransferDate());
		aeEvent.setBranch(fm.getFinBranch());
		aeEvent.setCcy(fm.getFinCcy());
		aeEvent.setFinType(fm.getFinType());
		aeEvent.setPromotion(fm.getPromotionCode());
		aeEvent.setCustID(fm.getCustID());
		aeEvent.setPostRefId(excessTransfer.getId());

		amountCodes.setFinType(aeEvent.getFinType());
		aeEvent.setAeAmountCodes(amountCodes);

		Map<String, Object> extDataMap = amountCodes.getDeclaredFieldValues();

		BigDecimal transfer = excessTransfer.getTransferAmount();

		prepareDataMap(excessTransfer, extDataMap, transfer);

		aeEvent.setDataMap(extDataMap);

		aeEvent.getAcSetIDList().add(accountingSetDAO.getAccountingSetId(AccountingEvent.EXTRF));
		aeEvent.setCustAppDate(appDate);
		aeEvent.setEntityCode(fm.getEntityCode());

		aeEvent = postingsPreparationUtil.postAccounting(aeEvent);
		return aeEvent;
	}

	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader);
		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		FinExcessTransfer transfer = (FinExcessTransfer) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		finExcessTransferDAO.delete(transfer, TableType.TEMP_TAB);

		FinExcessAmountReserve exReserve = finExcessAmountDAO.getExcessReserve(transfer.getId(),
				transfer.getTransferFromId(), RepayConstants.RECEIPTTYPE_TRANSFER);

		if (exReserve != null) {
			finExcessAmountDAO.deleteExcessReserve(transfer.getId(), transfer.getTransferFromId(),
					RepayConstants.RECEIPTTYPE_TRANSFER);
			finExcessAmountDAO.updateExcessReserve(transfer.getTransferFromId(), exReserve.getReservedAmt().negate());
		}

		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	private AuditHeader businessValidation(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug(Literal.ENTERING);

		FinExcessTransfer transfer = (FinExcessTransfer) auditDetail.getModelData();

		String[] parameters = new String[2];
		parameters[0] = PennantJavaUtil.getLabel("label_TransferID") + ": " + transfer.getId();

		if (PennantConstants.RECORD_TYPE_DEL.equals(transfer.getRecordType())
				&& finExcessTransferDAO.isIdExists(transfer.getId())) {
			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41006", parameters, null));
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	private void prepareDataMap(FinExcessTransfer transfer, Map<String, Object> map, BigDecimal amount) {
		switch (transfer.getTransferToType()) {
		case ExcessType.EXCESS:
			map.put("ae_toExcessAmt", amount);
			break;
		case ExcessType.EMIINADV:
			map.put("ae_toEmiAdvance", amount);
			break;
		case ExcessType.TEXCESS:
			map.put("ae_toTExcessAmt", amount);
			break;
		case ExcessType.SETTLEMENT:
			map.put("ae_toSettlement", amount);
			break;
		default:
			break;
		}

		switch (transfer.getTransferFromType()) {
		case ExcessType.EXCESS:
			map.put("EX_ReceiptAmount", amount);
			break;
		case ExcessType.EMIINADV:
			map.put("EA_ReceiptAmount", amount);
			break;
		case ExcessType.TEXCESS:
			map.put("ET_ReceiptAmount", amount);
			break;
		case ExcessType.SETTLEMENT:
			map.put("SETTLE_ReceiptAmount", amount);
			break;
		default:
			break;
		}
	}

	@Autowired
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	@Autowired
	public void setFinExcessTransferDAO(FinExcessTransferDAO finExcessTransferDAO) {
		this.finExcessTransferDAO = finExcessTransferDAO;
	}

	@Autowired
	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

	@Autowired
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	@Autowired
	public void setFinExcessAmountDAO(FinExcessAmountDAO finExcessAmountDAO) {
		this.finExcessAmountDAO = finExcessAmountDAO;
	}

	@Autowired
	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}

	@Autowired
	public void setAccountingSetDAO(AccountingSetDAO accountingSetDAO) {
		this.accountingSetDAO = accountingSetDAO;
	}

}
