package com.pennant.backend.service.finance.impl;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.receipts.CrossLoanKnockOffDAO;
import com.pennant.backend.dao.receipts.CrossLoanTransferDAO;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.dao.receipts.FinReceiptDetailDAO;
import com.pennant.backend.dao.receipts.FinReceiptHeaderDAO;
import com.pennant.backend.dao.rmtmasters.AccountingSetDAO;
import com.pennant.backend.dao.rulefactory.PostingsDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.CrossLoanKnockOffHeader;
import com.pennant.backend.model.finance.CrossLoanTransfer;
import com.pennant.backend.model.finance.FinReceiptData;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.service.finance.CrossLoanKnockOffService;
import com.pennant.backend.service.finance.FinanceMainService;
import com.pennant.backend.service.finance.GenericFinanceDetailService;
import com.pennant.backend.service.finance.ReceiptService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.RepayConstants;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.TableType;

public class CrossLoanKnockOffServiceImpl extends GenericFinanceDetailService implements CrossLoanKnockOffService {

	private static final Logger logger = LogManager.getLogger(CrossLoanKnockOffServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private CrossLoanKnockOffDAO crossLoanKnockOffDAO;
	private CrossLoanTransferDAO crossLoanTransferDAO;
	private FinReceiptHeaderDAO finReceiptHeaderDAO;
	private FinReceiptDetailDAO finReceiptDetailDAO;
	private PostingsPreparationUtil postingsPreparationUtil;
	private PostingsDAO postingsDAO;
	private AccountingSetDAO accountingSetDAO;
	private FinExcessAmountDAO finExcessAmountDAO;
	private FinanceMainService financeMainService;
	private ReceiptService receiptService;

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		CrossLoanKnockOffHeader crossLoanKnockOffHeader = (CrossLoanKnockOffHeader) auditHeader.getAuditDetail()
				.getModelData();
		TableType tableType = TableType.MAIN_TAB;
		if (crossLoanKnockOffHeader.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (crossLoanKnockOffHeader.getFinReceiptData() != null) {
			// do set workflow details
			crossLoanKnockOffHeader.getFinReceiptData().getReceiptHeader()
					.setWorkflowId(crossLoanKnockOffHeader.getWorkflowId());
			crossLoanKnockOffHeader.getFinReceiptData().getReceiptHeader()
					.setTaskId(crossLoanKnockOffHeader.getTaskId());
			crossLoanKnockOffHeader.getFinReceiptData().getReceiptHeader()
					.setRoleCode(crossLoanKnockOffHeader.getRoleCode());
			crossLoanKnockOffHeader.getFinReceiptData().getReceiptHeader()
					.setNextRoleCode(crossLoanKnockOffHeader.getNextRoleCode());
			crossLoanKnockOffHeader.getFinReceiptData().getReceiptHeader()
					.setRecordType(crossLoanKnockOffHeader.getRecordType());
			crossLoanKnockOffHeader.getFinReceiptData().getReceiptHeader()
					.setNextTaskId(crossLoanKnockOffHeader.getNextTaskId());
			crossLoanKnockOffHeader.getFinReceiptData().getReceiptHeader()
					.setNewRecord(crossLoanKnockOffHeader.isNew());
			crossLoanKnockOffHeader.getFinReceiptData().getReceiptHeader()
					.setVersion(crossLoanKnockOffHeader.getVersion());
			crossLoanKnockOffHeader.getFinReceiptData().getReceiptHeader().setRcdMaintainSts(FinServiceEvent.RECEIPT);
			crossLoanKnockOffHeader.getFinReceiptData().getReceiptHeader()
					.setLastMntOn(crossLoanKnockOffHeader.getLastMntOn());
			crossLoanKnockOffHeader.getFinReceiptData().getReceiptHeader()
					.setRecordStatus(crossLoanKnockOffHeader.getRecordStatus());
			crossLoanKnockOffHeader.getFinReceiptData().getReceiptHeader()
					.setUserDetails(crossLoanKnockOffHeader.getUserDetails());

			crossLoanKnockOffHeader.getCrossLoanTransfer().setWorkflowId(crossLoanKnockOffHeader.getWorkflowId());
			crossLoanKnockOffHeader.getCrossLoanTransfer().setTaskId(crossLoanKnockOffHeader.getTaskId());
			crossLoanKnockOffHeader.getCrossLoanTransfer().setRoleCode(crossLoanKnockOffHeader.getRoleCode());
			crossLoanKnockOffHeader.getCrossLoanTransfer().setNextRoleCode(crossLoanKnockOffHeader.getNextRoleCode());
			crossLoanKnockOffHeader.getCrossLoanTransfer().setRecordType(crossLoanKnockOffHeader.getRecordType());
			crossLoanKnockOffHeader.getCrossLoanTransfer().setNextTaskId(crossLoanKnockOffHeader.getNextTaskId());
			crossLoanKnockOffHeader.getCrossLoanTransfer().setNewRecord(crossLoanKnockOffHeader.isNew());
			crossLoanKnockOffHeader.getCrossLoanTransfer().setVersion(crossLoanKnockOffHeader.getVersion());
			crossLoanKnockOffHeader.getCrossLoanTransfer().setLastMntOn(crossLoanKnockOffHeader.getLastMntOn());
			crossLoanKnockOffHeader.getCrossLoanTransfer().setRecordStatus(crossLoanKnockOffHeader.getRecordStatus());
			crossLoanKnockOffHeader.getCrossLoanTransfer().setUserDetails(crossLoanKnockOffHeader.getUserDetails());
		}
		// Receipt Saving
		AuditHeader auditReceiptHeader = getAuditHeader(crossLoanKnockOffHeader.getFinReceiptData(),
				PennantConstants.TRAN_WF);

		try {
			auditReceiptHeader = receiptService.saveOrUpdate(auditReceiptHeader);
		} catch (InterfaceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (auditReceiptHeader.getErrorMessage() != null) {
			return auditReceiptHeader;
		}

		CrossLoanTransfer crossLoanTransfer = crossLoanKnockOffHeader.getCrossLoanTransfer();

		FinReceiptData newReceiptData = (FinReceiptData) auditReceiptHeader.getAuditDetail().getModelData();
		crossLoanKnockOffHeader.setKnockOffReceiptId(crossLoanKnockOffHeader.getKnockOffReceiptId());
		crossLoanTransfer.setReceiptId(crossLoanKnockOffHeader.getKnockOffReceiptId());

		// Child Table Saving
		if (crossLoanKnockOffHeader.isNew()) {
			crossLoanKnockOffHeader
					.setCrossLoanId(getCrossLoanTransferDAO().save(crossLoanTransfer, tableType.getSuffix()));

			// Save Header
			getCrossLoanKnockOffDAO().saveCrossLoanHeader(crossLoanKnockOffHeader, tableType.getSuffix());
			auditHeader.getAuditDetail().setModelData(crossLoanKnockOffHeader);
			auditHeader.setAuditReference(String.valueOf(crossLoanKnockOffHeader.getCrossLoanId()));
		} else {
			getCrossLoanTransferDAO().update(crossLoanTransfer, tableType.getSuffix());
			getCrossLoanKnockOffDAO().updateCrossLoanHeader(crossLoanKnockOffHeader, tableType.getSuffix());
		}

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {

		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		CrossLoanKnockOffHeader crossLoanKnockOffHeader = (CrossLoanKnockOffHeader) auditHeader.getAuditDetail()
				.getModelData();
		// Child deleting
		getCrossLoanKnockOffDAO().deleteHeader(crossLoanKnockOffHeader.getCrossLoanHeaderId(),
				TableType.MAIN_TAB.getSuffix());
		getCrossLoanTransferDAO().delete(crossLoanKnockOffHeader.getCrossLoanId(), TableType.MAIN_TAB.getSuffix());

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");

		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");

		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		CrossLoanKnockOffHeader crossLoanKnockOffHeader = new CrossLoanKnockOffHeader();
		BeanUtils.copyProperties((CrossLoanKnockOffHeader) auditHeader.getAuditDetail().getModelData(),
				crossLoanKnockOffHeader);
		CrossLoanTransfer crossLoanTransfer = crossLoanKnockOffHeader.getCrossLoanTransfer();
		crossLoanTransfer.setUserDetails(crossLoanKnockOffHeader.getUserDetails());

		getCrossLoanTransferDAO().delete(crossLoanKnockOffHeader.getCrossLoanId(), TableType.TEMP_TAB.getSuffix());
		getCrossLoanKnockOffDAO().deleteHeader(crossLoanKnockOffHeader.getCrossLoanHeaderId(),
				TableType.TEMP_TAB.getSuffix());

		if (!PennantConstants.RECORD_TYPE_NEW.equals(crossLoanKnockOffHeader.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(
					getCrossLoanTransferDAO().getCrossLoanTransferById(crossLoanKnockOffHeader.getCrossLoanId(), ""));
		}

		if (crossLoanKnockOffHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getCrossLoanKnockOffDAO().deleteHeader(crossLoanKnockOffHeader.getCrossLoanHeaderId(),
					TableType.MAIN_TAB.getSuffix());
			getCrossLoanTransferDAO().delete(crossLoanKnockOffHeader.getCrossLoanId(), TableType.MAIN_TAB.getSuffix());
		} else {
			crossLoanKnockOffHeader.setRoleCode("");
			crossLoanKnockOffHeader.setNextRoleCode("");
			crossLoanKnockOffHeader.setTaskId("");
			crossLoanKnockOffHeader.setNextTaskId("");
			crossLoanKnockOffHeader.setWorkflowId(0);

			// updating Utilize and Reserve Amount
			crossLoanTransfer
					.setUtiliseAmount(crossLoanTransfer.getUtiliseAmount().add(crossLoanTransfer.getTransferAmount()));
			crossLoanTransfer.setReserveAmount(BigDecimal.ZERO);

			if (crossLoanKnockOffHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				crossLoanKnockOffHeader.setRecordType("");

				// Receipt Saving
				AuditHeader auditReceiptHeader = getAuditHeader(crossLoanKnockOffHeader.getFinReceiptData(),
						PennantConstants.TRAN_WF);

				auditReceiptHeader = receiptService.doApprove(auditReceiptHeader);

				if (auditReceiptHeader.getErrorMessage() != null) {
					return auditReceiptHeader;
				}

				// Executing Posting & Accounting
				executeAccounting(crossLoanKnockOffHeader.getCrossLoanTransfer());
				// Set receipt Id
				crossLoanTransfer.setReceiptId(crossLoanKnockOffHeader.getKnockOffReceiptId());

				// saving Cross Loan KnockOff
				getCrossLoanKnockOffDAO().saveCrossLoanHeader(crossLoanKnockOffHeader, TableType.MAIN_TAB.getSuffix());
				getCrossLoanTransferDAO().save(crossLoanTransfer, TableType.MAIN_TAB.getSuffix());

			} else {
				tranType = PennantConstants.TRAN_UPD;
				crossLoanKnockOffHeader.setRecordType("");
				if (RepayConstants.MODULETYPE_CANCEL.equals(crossLoanTransfer.getModuleType())) {
					crossLoanKnockOffHeader.setRecordStatus(PennantConstants.RCD_STATUS_CANCELLED);

					// reverting Posting
					postingsPreparationUtil.postReversalsByLinkedTranID(crossLoanTransfer.getToLinkedTranId());
					postingsPreparationUtil.postReversalsByLinkedTranID(crossLoanTransfer.getFromLinkedTranId());
				}
				getCrossLoanKnockOffDAO().updateCrossLoanHeader(crossLoanKnockOffHeader,
						TableType.MAIN_TAB.getSuffix());
				getCrossLoanTransferDAO().update(crossLoanTransfer, TableType.MAIN_TAB.getSuffix());
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(crossLoanKnockOffHeader);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}

	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		CrossLoanKnockOffHeader crossLoanKnockOffHeader = (CrossLoanKnockOffHeader) auditHeader.getAuditDetail()
				.getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);

		if (crossLoanKnockOffHeader.getFinReceiptData() != null) {
			// do set workflow details
			crossLoanKnockOffHeader.getFinReceiptData().getReceiptHeader()
					.setWorkflowId(crossLoanKnockOffHeader.getWorkflowId());
			crossLoanKnockOffHeader.getFinReceiptData().getReceiptHeader()
					.setTaskId(crossLoanKnockOffHeader.getTaskId());
			crossLoanKnockOffHeader.getFinReceiptData().getReceiptHeader()
					.setRoleCode(crossLoanKnockOffHeader.getRoleCode());
			crossLoanKnockOffHeader.getFinReceiptData().getReceiptHeader()
					.setNextRoleCode(crossLoanKnockOffHeader.getNextRoleCode());
			crossLoanKnockOffHeader.getFinReceiptData().getReceiptHeader()
					.setRecordType(crossLoanKnockOffHeader.getRecordType());
			crossLoanKnockOffHeader.getFinReceiptData().getReceiptHeader()
					.setNextTaskId(crossLoanKnockOffHeader.getNextTaskId());
			crossLoanKnockOffHeader.getFinReceiptData().getReceiptHeader()
					.setNewRecord(crossLoanKnockOffHeader.isNew());
			crossLoanKnockOffHeader.getFinReceiptData().getReceiptHeader()
					.setVersion(crossLoanKnockOffHeader.getVersion());
			crossLoanKnockOffHeader.getFinReceiptData().getReceiptHeader().setRcdMaintainSts(FinServiceEvent.RECEIPT);
			crossLoanKnockOffHeader.getFinReceiptData().getReceiptHeader()
					.setLastMntOn(crossLoanKnockOffHeader.getLastMntOn());
			crossLoanKnockOffHeader.getFinReceiptData().getReceiptHeader()
					.setRecordStatus(crossLoanKnockOffHeader.getRecordStatus());
		}
		AuditHeader auditReceiptHeader = getAuditHeader(crossLoanKnockOffHeader.getFinReceiptData(),
				PennantConstants.TRAN_WF);
		try {
			auditReceiptHeader = receiptService.doReject(auditReceiptHeader);
		} catch (InterfaceException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		if (auditReceiptHeader.getErrorMessage() != null) {
			return auditReceiptHeader;
		}

		getCrossLoanTransferDAO().delete(crossLoanKnockOffHeader.getCrossLoanId(), TableType.TEMP_TAB.getSuffix());
		getCrossLoanKnockOffDAO().deleteHeader(crossLoanKnockOffHeader.getCrossLoanHeaderId(),
				TableType.TEMP_TAB.getSuffix());

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}

	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug("Entering");

		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		CrossLoanKnockOffHeader crossLoanKnockOffHeader = (CrossLoanKnockOffHeader) auditDetail.getModelData();
		CrossLoanTransfer crossLoanTransfer = crossLoanKnockOffHeader.getCrossLoanTransfer();

		if (crossLoanKnockOffHeader.isNew()) {
			// Validate if any from or To Loan is available in Cross Loan Transfer table
			boolean isFromLoanExists = getCrossLoanTransferDAO()
					.isLoanExistInTemp(crossLoanTransfer.getFromFinReference(), true);
			if (isFromLoanExists) {
				String[] parameters = new String[1];
				parameters[0] = "From Loan is already exist in progress";
				auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "30550", parameters, null));
			}

			boolean isToLoanExists = getCrossLoanTransferDAO().isLoanExistInTemp(crossLoanTransfer.getToFinReference(),
					false);
			if (isToLoanExists) {
				String[] parameters = new String[1];
				parameters[0] = "To Loan is already exist in progress";
				auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "30550", parameters, null));
			}
		}

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = String.valueOf(crossLoanTransfer.getCustId());
		errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];

		// Checking , if Customer is in EOD process or not. if Yes, not allowed
		// to do an action
		// int eodProgressCount = getReceiptService().getProgressCountByCust(crossLoanTransfer.getCustId());

		// If Customer Exists in EOD Processing, Not allowed to Maintenance till
		// completion
		/*
		 * if (eodProgressCount > 0) { auditDetail.setErrorDetail(ErrorUtil.getErrorDetail( new
		 * ErrorDetail(PennantConstants.KEY_FIELD, "60203", errParm, valueParm), usrLanguage)); }
		 */
		// auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		return auditDetail;
	}

	public void executeAccounting(CrossLoanTransfer crossLoan) {
		logger.debug(Literal.ENTERING);

		FinanceMain main = financeMainService.getFinanceMainByRef(crossLoan.getFromFinReference(), false);

		// from Loan
		AEEvent aeEvent = new AEEvent();
		aeEvent.setEntityCode(main.getEntityCode());
		aeEvent.setPostingUserBranch(crossLoan.getUserDetails().getBranchCode());
		aeEvent.setAccountingEvent(AccountingEvent.ACCEVENT_CROSSLOANFROM);
		aeEvent.setFinReference(crossLoan.getFromFinReference());
		aeEvent.setValueDate(SysParamUtil.getAppDate());

		aeEvent.setFinReference(main.getFinReference());
		// aeEvent.setSource(PennantConstants.LOAN);
		aeEvent.setFinID(main.getFinID());

		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
		if (amountCodes == null) {
			amountCodes = new AEAmountCodes();
		}

		// Setting GstINCategory for Loan Transactions
		// String gstINCategory = getPostingsDAO().get`GstINCategory(main.getFinType(), "", true);
		// aeEvent.setGstINCategory(gstINCategory);
		// amountCodes.setGstINCategory(gstINCategory);

		aeEvent.setBranch(crossLoan.getUserDetails().getBranchCode());
		aeEvent.setCcy(main.getFinCcy());
		amountCodes.setUserBranch(crossLoan.getUserDetails().getBranchCode());
		amountCodes.setEntitycode(main.getEntityCode());
		amountCodes.setFinType(main.getFinType());
		// amountCodes.setPromotionCode(main.getPromotionCode());
		aeEvent.setBranch(main.getFinBranch());

		// Assignment Fields Mapping
		// HashMap<String, Object> dataMap = getReceiptCalculator().prepareAssignmentMap(main.getAssignmentId(),
		// amountCodes);

		Map<String, Object> dataMap = null;
		dataMap = amountCodes.getDeclaredFieldValues();
		aeEvent.setDataMap(dataMap);

		aeEvent.getDataMap().put("rd_amount", crossLoan.getTransferAmount());
		// PSD :186217
		aeEvent.getDataMap().put("ae_isWriteOff", main.isWriteoffLoan());

		long accountsetId = getAccountingSetDAO().getAccountingSetId(AccountingEvent.ACCEVENT_CROSSLOANFROM,
				AccountingEvent.ACCEVENT_CROSSLOANFROM);

		aeEvent.getAcSetIDList().add(accountsetId);
		aeEvent = getPostingsPreparationUtil().postAccounting(aeEvent);
		crossLoan.setFromLinkedTranId(aeEvent.getLinkedTranId()); // setting LinkedTranId

		FinanceMain fm = financeMainService.getFinanceMainByRef(crossLoan.getToFinReference(), false);

		// to Loan
		AEEvent aeEvent1 = new AEEvent();
		aeEvent1.setEntityCode(fm.getEntityCode());
		aeEvent1.setPostingUserBranch(crossLoan.getUserDetails().getBranchCode());
		aeEvent1.setAccountingEvent(AccountingEvent.ACCEVENT_CROSSLOANTO);
		aeEvent1.setFinReference(crossLoan.getToFinReference());
		aeEvent1.setValueDate(SysParamUtil.getAppDate());

		aeEvent1.setFinReference(fm.getFinReference());
		// aeEvent_.setSource(PennantConstants.LOAN);
		aeEvent1.setFinID(fm.getFinID());

		AEAmountCodes amountCodes2 = aeEvent1.getAeAmountCodes();
		if (amountCodes2 == null) {
			amountCodes2 = new AEAmountCodes();
		}

		// Setting GstINCategory for Loan Transactions
		// String gstINCategory_ = getPostingsDAO().getGstINCategory(fm.getFinType(), "", true);
		// aeEvent_.setGstINCategory(gstINCategory_);
		// amountCodes_.setGstINCategory(gstINCategory_);

		aeEvent1.setBranch(crossLoan.getUserDetails().getBranchCode());
		aeEvent1.setCcy(fm.getFinCcy());
		amountCodes2.setEntitycode(fm.getEntityCode());
		amountCodes2.setUserBranch(crossLoan.getUserDetails().getBranchCode());
		amountCodes2.setFinType(fm.getFinType());
		aeEvent1.setBranch(fm.getFinBranch());

		// Assignment Fields Mapping
		// dataMap = getReceiptCalculator().prepareAssignmentMap(main_.getAssignmentId(), amountCodes);

		dataMap = amountCodes2.getDeclaredFieldValues();
		aeEvent1.setDataMap(dataMap);
		aeEvent1.getDataMap().put("rd_amount", crossLoan.getTransferAmount());

		long accountsetId_ = getAccountingSetDAO().getAccountingSetId(AccountingEvent.ACCEVENT_CROSSLOANTO,
				AccountingEvent.ACCEVENT_CROSSLOANTO);

		aeEvent1.getAcSetIDList().add(accountsetId_);
		aeEvent1 = getPostingsPreparationUtil().postAccounting(aeEvent1);
		crossLoan.setToLinkedTranId(aeEvent1.getLinkedTranId()); // setting linkedTranId

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Get Audit Header Details
	 */
	protected AuditHeader getAuditHeader(FinReceiptData finReceiptData, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, null, finReceiptData);
		return new AuditHeader(finReceiptData.getFinReference(), null, null, null, auditDetail,
				finReceiptData.getReceiptHeader().getUserDetails(), null);
	}

	public FinReceiptHeaderDAO getFinReceiptHeaderDAO() {
		return finReceiptHeaderDAO;
	}

	public FinReceiptDetailDAO getFinReceiptDetailDAO() {
		return finReceiptDetailDAO;
	}

	public PostingsPreparationUtil getPostingsPreparationUtil() {
		return postingsPreparationUtil;
	}

	public PostingsDAO getPostingsDAO() {
		return postingsDAO;
	}

	public AccountingSetDAO getAccountingSetDAO() {
		return accountingSetDAO;
	}

	public void setFinReceiptHeaderDAO(FinReceiptHeaderDAO finReceiptHeaderDAO) {
		this.finReceiptHeaderDAO = finReceiptHeaderDAO;
	}

	public void setFinReceiptDetailDAO(FinReceiptDetailDAO finReceiptDetailDAO) {
		this.finReceiptDetailDAO = finReceiptDetailDAO;
	}

	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}

	public void setPostingsDAO(PostingsDAO postingsDAO) {
		this.postingsDAO = postingsDAO;
	}

	public void setAccountingSetDAO(AccountingSetDAO accountingSetDAO) {
		this.accountingSetDAO = accountingSetDAO;
	}

	public FinExcessAmountDAO getFinExcessAmountDAO() {
		return finExcessAmountDAO;
	}

	public void setFinExcessAmountDAO(FinExcessAmountDAO finExcessAmountDAO) {
		this.finExcessAmountDAO = finExcessAmountDAO;
	}

	public FinanceMainService getFinanceMainService() {
		return financeMainService;
	}

	public void setFinanceMainService(FinanceMainService financeMainService) {
		this.financeMainService = financeMainService;
	}

	/*
	 * @Override public FinExcessAmount getExcessAmountById(long excessId, String amountType) { return
	 * finExcessAmountDAO.getExcessAmountById(excessId, amountType); }
	 */

	public CrossLoanKnockOffDAO getCrossLoanKnockOffDAO() {
		return crossLoanKnockOffDAO;
	}

	public void setCrossLoanKnockOffDAO(CrossLoanKnockOffDAO crossLoanKnockOffDAO) {
		this.crossLoanKnockOffDAO = crossLoanKnockOffDAO;
	}

	public ReceiptService getReceiptService() {
		return receiptService;
	}

	public void setReceiptService(ReceiptService receiptService) {
		this.receiptService = receiptService;
	}

	@Override
	public CrossLoanKnockOffHeader getCrossLoanHeaderById(long crossLoanHeaderId, String type) {
		return getCrossLoanKnockOffDAO().getCrossLoanHeaderById(crossLoanHeaderId, type);
	}

	public CrossLoanTransferDAO getCrossLoanTransferDAO() {
		return crossLoanTransferDAO;
	}

	public void setCrossLoanTransferDAO(CrossLoanTransferDAO crossLoanTransferDAO) {
		this.crossLoanTransferDAO = crossLoanTransferDAO;
	}

	@Override
	public CrossLoanTransfer getCrossLoanTransferById(long crossLoanId, String string) {
		return getCrossLoanTransferDAO().getCrossLoanTransferById(crossLoanId, string);
	}

	@Override
	public List<CrossLoanTransfer> getExcessAmountsByRefAndType(String finReference, String amountType) {
		// TODO Auto-generated method stub
		return null;
	}

}
