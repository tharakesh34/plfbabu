package com.pennant.backend.service.excessheadmaster.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.constants.AccountConstants;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.excessheadmaster.FinExcessTransferDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.dao.receipts.FinReceiptHeaderDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.excessheadmaster.FinExcessTransfer;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinExcessAmountReserve;
import com.pennant.backend.model.finance.FinExcessMovement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.excessheadmaster.ExcessTransferService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.RepayConstants;
import com.pennant.cache.util.AccountingConfigCache;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.core.TableType;

public class ExcessTransferServiceImpl extends GenericService<FinExcessTransfer> implements ExcessTransferService {

	private static Logger logger = LogManager.getLogger(ExcessTransferServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private FinExcessTransferDAO finExcessTransferDAO;
	private CustomerDAO customerDAO;
	private FinanceMainDAO financeMainDAO;
	private FinanceProfitDetailDAO profitDetailsDAO;
	private FinReceiptHeaderDAO finReceiptHeaderDAO;
	private FinanceDetailService financeDetailService;
	private FinExcessAmountDAO finExcessAmountDAO;
	private PostingsPreparationUtil postingsPreparationUtil;

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
		boolean isExists = finExcessTransferDAO.isFinReceferenceExist(finReference, "_Temp");
		return isExists;
	}

	@Override
	public FinExcessTransfer getFinExcessData(long finID) {

		FinExcessTransfer finExcessTransfer = new FinExcessTransfer();
		finExcessTransfer.setFinId(finID);
		FinanceMain fm = financeMainDAO.getFinanceMainById(finID, "_View", false);
		finExcessTransfer.setCustomer(customerDAO.getCustomerByID(fm.getCustID()));
		finExcessTransfer.setFinBranch(fm.getFinBranch());
		finExcessTransfer.setFinType(fm.getFinType());
		finExcessTransfer.setFinReference(fm.getFinReference());

		return finExcessTransfer;
	}

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		FinExcessTransfer finExcessTransfer = (FinExcessTransfer) auditHeader.getAuditDetail().getModelData();
		getFinExcessTransferDAO().delete(finExcessTransfer, TableType.MAIN_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader, "saveOrUpdate");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		FinExcessTransfer finExcessTransfer = (FinExcessTransfer) auditHeader.getAuditDetail().getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (finExcessTransfer.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (finExcessTransfer.isNewRecord()) {
			finExcessTransfer.setTransferDate(SysParamUtil.getAppDate());
			finExcessTransfer.setId(Long.parseLong(getFinExcessTransferDAO().save(finExcessTransfer, tableType)));
			auditHeader.getAuditDetail().setModelData(finExcessTransfer);
			auditHeader.setAuditReference(String.valueOf(finExcessTransfer.getId()));
		} else {
			getFinExcessTransferDAO().update(finExcessTransfer, tableType);
		}

		// Excess Amount make utilization
		FinExcessAmountReserve exReserve = finExcessAmountDAO.getExcessReserve(finExcessTransfer.getId(),
				finExcessTransfer.getTransferFromId(), RepayConstants.TRANSFER);
		if (exReserve == null) {

			// Update Excess Amount in Reserve
			finExcessAmountDAO.updateExcessReserve(finExcessTransfer.getTransferFromId(),
					finExcessTransfer.getTransferAmount());

			// Save Excess Reserve Log Amount
			finExcessAmountDAO.saveExcessReserveLog(finExcessTransfer.getId(), finExcessTransfer.getTransferFromId(),
					finExcessTransfer.getTransferAmount(), RepayConstants.TRANSFER);

		} else {

			if (finExcessTransfer.getTransferAmount().compareTo(exReserve.getReservedAmt()) != 0) {
				BigDecimal diffInReserve = finExcessTransfer.getTransferAmount().subtract(exReserve.getReservedAmt());

				// Update Reserve Amount in FinExcessAmount
				finExcessAmountDAO.updateExcessReserve(finExcessTransfer.getTransferFromId(), diffInReserve);

				// Update Excess Reserve Log
				// finExcessAmountDAO.updateExcessReserveLog(finExcessTransfer.getTransferId(),
				// finExcessTransfer.getTransferFromId(), diffInReserve, RepayConstants.RECEIPTTYPE_RECIPT);
				finExcessAmountDAO.updateExcessReserveLog(finExcessTransfer.getId(),
						finExcessTransfer.getTransferFromId(), diffInReserve, RepayConstants.TRANSFER);
			}
		}

		// auditHeader.setAuditDetails(auditDetails);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		FinExcessTransfer finExcessTransfer = new FinExcessTransfer();
		BeanUtils.copyProperties((FinExcessTransfer) auditHeader.getAuditDetail().getModelData(), finExcessTransfer);

		getFinExcessTransferDAO().delete(finExcessTransfer, TableType.TEMP_TAB);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(finExcessTransfer.getRecordType())) {
			auditHeader.getAuditDetail()
					.setBefImage(finExcessTransferDAO.getExcessTransferByTransferId(finExcessTransfer.getId(), ""));
		}

		long linkedTranId = 0;

		AEEvent aeEvent = executeAccounting(finExcessTransfer);
		if (aeEvent.isPostingSucess()) {
			linkedTranId = aeEvent.getLinkedTranId();
		}

		FinExcessAmountReserve exReserve = finExcessAmountDAO.getExcessReserve(finExcessTransfer.getId(),
				finExcessTransfer.getTransferFromId(), RepayConstants.TRANSFER);

		if (exReserve != null) {
			finExcessAmountDAO.updateUtilise(finExcessTransfer.getTransferFromId(),
					finExcessTransfer.getTransferAmount());
			finExcessAmountDAO.deleteExcessReserve(finExcessTransfer.getId(), finExcessTransfer.getTransferFromId(),
					RepayConstants.TRANSFER);
		} else {
			finExcessAmountDAO.updateUtiliseOnly(finExcessTransfer.getTransferFromId(),
					finExcessTransfer.getTransferAmount());
		}

		FinExcessMovement movement = new FinExcessMovement();
		movement.setExcessID(finExcessTransfer.getTransferFromId());
		movement.setReceiptID(finExcessTransfer.getId());
		movement.setMovementType(RepayConstants.TRANSFER);
		movement.setTranType(AccountConstants.TRANTYPE_DEBIT);
		movement.setAmount(finExcessTransfer.getTransferAmount());
		finExcessAmountDAO.saveExcessMovement(movement);
		finExcessTransfer.setRoleCode("");
		finExcessTransfer.setNextRoleCode("");
		finExcessTransfer.setTaskId("");
		finExcessTransfer.setNextTaskId("");
		finExcessTransfer.setWorkflowId(0);
		finExcessTransfer.setStatus(PennantConstants.CHEQUESTATUS_REALISE);

		long tranferToExcessId = 0;

		FinExcessAmount excess = new FinExcessAmount();
		excess.setFinID(finExcessTransfer.getFinId());
		excess.setFinReference(finExcessTransfer.getFinReference());
		excess.setAmountType(finExcessTransfer.getTransferToType());
		excess.setAmount(finExcessTransfer.getTransferAmount());
		excess.setUtilisedAmt(BigDecimal.ZERO);
		excess.setBalanceAmt(finExcessTransfer.getTransferAmount());
		excess.setReservedAmt(BigDecimal.ZERO);
		excess.setReceiptID(finExcessTransfer.getId());
		finExcessAmountDAO.saveExcess(excess);
		tranferToExcessId = excess.getExcessID();

		finExcessTransfer.setTransferToId(tranferToExcessId);
		finExcessTransfer.setLinkedTranId(linkedTranId);
		getFinExcessTransferDAO().save(finExcessTransfer, TableType.MAIN_TAB);

		FinExcessMovement toMovement = new FinExcessMovement();
		toMovement.setExcessID(tranferToExcessId);
		toMovement.setReceiptID(finExcessTransfer.getId());
		toMovement.setMovementType(RepayConstants.TRANSFER);
		toMovement.setTranType(AccountConstants.TRANTYPE_CREDIT);
		toMovement.setAmount(finExcessTransfer.getTransferAmount());
		finExcessAmountDAO.saveExcessMovement(toMovement);

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(finExcessTransfer);

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	private AEEvent executeAccounting(FinExcessTransfer excessTransfer) {

		String eventCode = AccountingEvent.EXTRF;

		AEEvent aeEvent = new AEEvent();
		AEAmountCodes amountCodes = new AEAmountCodes();

		FinanceMain fm = financeMainDAO.getFinanceMainForExcessTransfer(excessTransfer.getFinId());

		aeEvent.setFinReference(excessTransfer.getFinReference());
		aeEvent.setAccountingEvent(eventCode);
		aeEvent.setPostDate(SysParamUtil.getAppDate());
		aeEvent.setValueDate(excessTransfer.getTransferDate());
		aeEvent.setSchdDate(excessTransfer.getTransferDate());
		aeEvent.setBranch(fm.getFinBranch());
		aeEvent.setCcy(fm.getFinCcy());
		aeEvent.setFinType(fm.getFinType());
		aeEvent.setPromotion(fm.getPromotionCode());
		aeEvent.setCustID(fm.getCustID());
		aeEvent.setPostRefId(excessTransfer.getId());

		// Finance Fields
		amountCodes.setFinType(aeEvent.getFinType());
		aeEvent.setAeAmountCodes(amountCodes);

		aeEvent.setDataMap(aeEvent.getAeAmountCodes().getDeclaredFieldValues());
		aeEvent.getAcSetIDList().add(AccountingConfigCache.getAccountSetID(fm.getFinType(),
				aeEvent.getAccountingEvent(), FinanceConstants.MODULEID_FINTYPE));
		aeEvent.setCustAppDate(SysParamUtil.getAppDate());
		aeEvent.setEntityCode(fm.getEntityCode());

		aeEvent = postingsPreparationUtil.postAccounting(aeEvent);
		return aeEvent;

	}

	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		FinExcessTransfer finExcessTransfer = (FinExcessTransfer) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getFinExcessTransferDAO().delete(finExcessTransfer, TableType.TEMP_TAB);

		FinExcessAmountReserve exReserve = finExcessAmountDAO.getExcessReserve(finExcessTransfer.getId(),
				finExcessTransfer.getTransferFromId(), RepayConstants.TRANSFER);

		if (exReserve != null) {
			// Delete Reserve Amount in FinExcessAmount
			finExcessAmountDAO.deleteExcessReserve(finExcessTransfer.getId(), finExcessTransfer.getTransferFromId(),
					RepayConstants.TRANSFER);

			// Update Reserve Amount in FinExcessAmount
			finExcessAmountDAO.updateExcessReserve(finExcessTransfer.getTransferFromId(),
					exReserve.getReservedAmt().negate());

		}

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
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

		// Get the model object.
		FinExcessTransfer finExcessTransfer = (FinExcessTransfer) auditDetail.getModelData();

		String[] parameters = new String[2];
		parameters[0] = PennantJavaUtil.getLabel("label_TransferID") + ": " + finExcessTransfer.getId();

		if (StringUtils.equals(PennantConstants.RECORD_TYPE_DEL, finExcessTransfer.getRecordType())) {
			boolean workflowExists = getFinExcessTransferDAO().isIdExists(finExcessTransfer.getId());
			if (workflowExists) {
				auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41006", parameters, null));
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	@Override
	public FinExcessAmount getFinExcessAmountById(long excessId) {
		return finExcessAmountDAO.getFinExcessAmountById(excessId, "");
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public FinExcessTransferDAO getFinExcessTransferDAO() {
		return finExcessTransferDAO;
	}

	public void setFinExcessTransferDAO(FinExcessTransferDAO finExcessTransferDAO) {
		this.finExcessTransferDAO = finExcessTransferDAO;
	}

	public CustomerDAO getCustomerDAO() {
		return customerDAO;
	}

	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

	public FinanceMainDAO getFinanceMainDAO() {
		return financeMainDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public FinanceProfitDetailDAO getProfitDetailsDAO() {
		return profitDetailsDAO;
	}

	public void setProfitDetailsDAO(FinanceProfitDetailDAO profitDetailsDAO) {
		this.profitDetailsDAO = profitDetailsDAO;
	}

	public FinReceiptHeaderDAO getFinReceiptHeaderDAO() {
		return finReceiptHeaderDAO;
	}

	public void setFinReceiptHeaderDAO(FinReceiptHeaderDAO finReceiptHeaderDAO) {
		this.finReceiptHeaderDAO = finReceiptHeaderDAO;
	}

	public FinanceDetailService getFinanceDetailService() {
		return financeDetailService;
	}

	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	public void setFinExcessAmountDAO(FinExcessAmountDAO finExcessAmountDAO) {
		this.finExcessAmountDAO = finExcessAmountDAO;
	}

	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}

}
