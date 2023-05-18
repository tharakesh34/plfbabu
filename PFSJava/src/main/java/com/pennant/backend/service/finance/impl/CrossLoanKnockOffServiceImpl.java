package com.pennant.backend.service.finance.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.payment.PaymentDetailDAO;
import com.pennant.backend.dao.receipts.CrossLoanKnockOffDAO;
import com.pennant.backend.dao.receipts.CrossLoanTransferDAO;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.dao.rmtmasters.AccountingSetDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.crossloanknockoff.CrossLoanKnockoffUpload;
import com.pennant.backend.model.finance.CrossLoanKnockOff;
import com.pennant.backend.model.finance.CrossLoanTransfer;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinReceiptData;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.receiptupload.ReceiptUploadDetail;
import com.pennant.backend.model.receiptupload.UploadAlloctionDetail;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.CrossLoanKnockOffService;
import com.pennant.backend.service.finance.FinanceMainService;
import com.pennant.backend.service.finance.ReceiptCancellationService;
import com.pennant.backend.service.finance.ReceiptService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.knockoff.KnockOffType;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.RequestSource;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.receipt.constants.Allocation;
import com.pennanttech.pff.receipt.constants.ReceiptMode;
import com.pennanttech.pff.receipt.upload.ReceiptDataValidator;

public class CrossLoanKnockOffServiceImpl extends GenericService<CrossLoanKnockOff>
		implements CrossLoanKnockOffService {
	private static final Logger logger = LogManager.getLogger(CrossLoanKnockOffServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private CrossLoanKnockOffDAO crossLoanKnockOffDAO;
	private CrossLoanTransferDAO crossLoanTransferDAO;
	private AccountingSetDAO accountingSetDAO;
	private FinExcessAmountDAO finExcessAmountDAO;
	private FinanceMainService financeMainService;
	private ReceiptService receiptService;
	private ReceiptDataValidator receiptDataValidator;
	private PostingsPreparationUtil postingsPreparationUtil;
	private PaymentDetailDAO paymentDetailDAO;
	private ReceiptCancellationService receiptCancellationService;

	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader);
		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		CrossLoanKnockOff clk = (CrossLoanKnockOff) auditHeader.getAuditDetail().getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (clk.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		FinReceiptData frd = clk.getFinReceiptData();
		CrossLoanTransfer transfer = clk.getCrossLoanTransfer();

		if (frd != null) {
			FinReceiptHeader rch = frd.getReceiptHeader();
			rch.setWorkflowId(clk.getWorkflowId());
			rch.setTaskId(clk.getTaskId());
			rch.setRoleCode(clk.getRoleCode());
			rch.setNextRoleCode(clk.getNextRoleCode());
			rch.setRecordType(clk.getRecordType());
			rch.setNextTaskId(clk.getNextTaskId());
			rch.setNewRecord(clk.isNewRecord());
			rch.setVersion(clk.getVersion());
			rch.setRcdMaintainSts(FinServiceEvent.RECEIPT);
			rch.setKnockOffType(KnockOffType.CROSS_LOAN.code());
			rch.setLastMntOn(clk.getLastMntOn());
			rch.setRecordStatus(clk.getRecordStatus());
			rch.setUserDetails(clk.getUserDetails());

			transfer.setWorkflowId(clk.getWorkflowId());
			transfer.setTaskId(clk.getTaskId());
			transfer.setRoleCode(clk.getRoleCode());
			transfer.setNextRoleCode(clk.getNextRoleCode());
			transfer.setRecordType(clk.getRecordType());
			transfer.setNextTaskId(clk.getNextTaskId());
			transfer.setNewRecord(clk.isNewRecord());
			transfer.setVersion(clk.getVersion());
			transfer.setLastMntOn(clk.getLastMntOn());
			transfer.setRecordStatus(clk.getRecordStatus());
			transfer.setUserDetails(clk.getUserDetails());
		}

		AuditHeader ah = getAuditHeader(frd, PennantConstants.TRAN_WF);

		try {
			ah = receiptService.saveOrUpdate(ah);
		} catch (Exception e) {
			throw new AppException(e.getMessage());
		}

		if (ah.getErrorMessage() != null) {
			return ah;
		}

		clk.setKnockOffId(frd.getReceiptHeader().getReceiptID());
		transfer.setReceiptId(clk.getKnockOffId());

		if (clk.isNewRecord()) {
			clk.setTransferID(crossLoanTransferDAO.save(transfer, tableType.getSuffix()));

			crossLoanKnockOffDAO.saveCrossLoanHeader(clk, tableType.getSuffix());
			auditHeader.getAuditDetail().setModelData(clk);
			auditHeader.setAuditReference(String.valueOf(clk.getId()));
		} else {
			crossLoanTransferDAO.update(transfer, tableType.getSuffix());
			crossLoanKnockOffDAO.updateCrossLoanHeader(clk, tableType.getSuffix());
		}

		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader);
		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}
		CrossLoanKnockOff clk = (CrossLoanKnockOff) auditHeader.getAuditDetail().getModelData();
		crossLoanKnockOffDAO.deleteHeader(clk.getTransferID(), TableType.MAIN_TAB.getSuffix());
		crossLoanTransferDAO.delete(clk.getId(), TableType.MAIN_TAB.getSuffix());

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
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}
		CrossLoanKnockOff clk = new CrossLoanKnockOff();
		BeanUtils.copyProperties(auditHeader.getAuditDetail().getModelData(), clk);
		CrossLoanTransfer clt = clk.getCrossLoanTransfer();
		clt.setUserDetails(clk.getUserDetails());
		clk.getCrossLoanTransfer().setValueDate(clk.getValueDate());

		if (crossLoanKnockOffDAO.cancelReferenceID(clk.getKnockOffId())) {
			auditHeader.setErrorDetails(new ErrorDetail("30550", "Excess Receipt is cancelled", null));
			return auditHeader;
		}

		crossLoanTransferDAO.delete(clk.getTransferID(), TableType.TEMP_TAB.getSuffix());
		crossLoanKnockOffDAO.deleteHeader(clk.getId(), TableType.TEMP_TAB.getSuffix());

		if (!PennantConstants.RECORD_TYPE_NEW.equals(clk.getRecordType()) && !clk.isCancelProcess()) {
			auditHeader.getAuditDetail()
					.setBefImage(crossLoanTransferDAO.getCrossLoanTransferById(clk.getTransferID(), ""));
		}

		if (clk.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			crossLoanKnockOffDAO.deleteHeader(clk.getId(), TableType.MAIN_TAB.getSuffix());
			crossLoanTransferDAO.delete(clk.getTransferID(), TableType.MAIN_TAB.getSuffix());
		} else {
			clk.setRoleCode("");
			clk.setNextRoleCode("");
			clk.setTaskId("");
			clk.setNextTaskId("");
			clk.setWorkflowId(0);

			clt.setUtiliseAmount(clt.getUtiliseAmount().add(clt.getTransferAmount()));
			clt.setReserveAmount(BigDecimal.ZERO);
			AuditHeader auditReceiptHeader = getAuditHeader(clk.getFinReceiptData(), PennantConstants.TRAN_WF);

			if (clk.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				clk.setRecordType("");

				if (RequestSource.UPLOAD.name().equals(clk.getRequestSource())) {
					FinanceDetail fd = createReceipt(clk);
					FinScheduleData schd = fd.getFinScheduleData();

					if (!schd.getErrorDetails().isEmpty()) {
						ErrorDetail error = schd.getErrorDetails().get(0);
						auditHeader.setErrorMessage(schd.getErrorDetails());
						auditHeader.getAuditDetail().setErrorDetail(error);
						return auditHeader;
					}
					clk.setKnockOffId(fd.getReceiptId());
				} else {
					auditReceiptHeader = receiptService.doApprove(auditReceiptHeader);

					if (auditReceiptHeader.getErrorMessage() != null) {
						return auditReceiptHeader;
					}
				}

				if (auditReceiptHeader.getErrorMessage() != null) {
					return auditReceiptHeader;
				}

				executeAccounting(clk.getCrossLoanTransfer());

				clt.setReceiptId(clk.getKnockOffId());

				crossLoanTransferDAO.save(clt, TableType.MAIN_TAB.getSuffix());

				if (clk.getTransferID() <= 0) {
					clk.setTransferID(clt.getId());
				}
				crossLoanKnockOffDAO.saveCrossLoanHeader(clk, TableType.MAIN_TAB.getSuffix());

				((CrossLoanKnockOff) auditHeader.getAuditDetail().getModelData()).setId(clk.getId());

			} else {
				tranType = PennantConstants.TRAN_UPD;
				clk.setRecordType("");

				if (clk.isCancelProcess()) {
					clt.setModuleType(RepayConstants.MODULETYPE_CANCEL);
					clk.setRecordStatus(PennantConstants.RCD_STATUS_CANCELLED);

					receiptService.doApprove(auditReceiptHeader);

					postingsPreparationUtil.postReversalsByLinkedTranID(clt.getToLinkedTranId());
					postingsPreparationUtil.postReversalsByLinkedTranID(clt.getFromLinkedTranId());
				}

				crossLoanKnockOffDAO.updateCrossLoanHeader(clk, TableType.MAIN_TAB.getSuffix());
				crossLoanTransferDAO.update(clt, TableType.MAIN_TAB.getSuffix());
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeaderDAO.addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(clk);
		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		CrossLoanKnockOff ckk = (CrossLoanKnockOff) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);

		FinReceiptData frd = ckk.getFinReceiptData();

		if (frd != null) {
			FinReceiptHeader rch = frd.getReceiptHeader();
			rch.setWorkflowId(ckk.getWorkflowId());
			rch.setTaskId(ckk.getTaskId());
			rch.setRoleCode(ckk.getRoleCode());
			rch.setNextRoleCode(ckk.getNextRoleCode());
			rch.setRecordType(ckk.getRecordType());
			rch.setNextTaskId(ckk.getNextTaskId());
			rch.setNewRecord(ckk.isNewRecord());
			rch.setVersion(ckk.getVersion());
			rch.setRcdMaintainSts(FinServiceEvent.RECEIPT);
			rch.setLastMntOn(ckk.getLastMntOn());
			rch.setRecordStatus(ckk.getRecordStatus());
		}

		AuditHeader ah = getAuditHeader(frd, PennantConstants.TRAN_WF);
		try {
			ah = receiptService.doReject(ah);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		if (ah.getErrorMessage() != null) {
			return ah;
		}

		crossLoanTransferDAO.delete(ckk.getTransferID(), TableType.TEMP_TAB.getSuffix());
		crossLoanKnockOffDAO.deleteHeader(ckk.getId(), TableType.TEMP_TAB.getSuffix());

		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	private AuditHeader businessValidation(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	private AuditDetail validation(AuditDetail auditDetail) {
		logger.debug(Literal.ENTERING);

		auditDetail.setErrorDetails(new ArrayList<>());

		CrossLoanKnockOff clk = (CrossLoanKnockOff) auditDetail.getModelData();
		FinReceiptData rcdData = clk.getFinReceiptData();
		FinReceiptHeader frh = rcdData.getReceiptHeader();
		CrossLoanTransfer clt = clk.getCrossLoanTransfer();

		if (clk.isNewRecord()) {
			boolean isExists = crossLoanTransferDAO.isLoanExistInTemp(clt.getFromFinID(), true);
			if (isExists) {
				String[] parameters = new String[1];
				parameters[0] = "From Loan is already exist in progress";
				auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "30550", parameters, null));
			}

			isExists = crossLoanTransferDAO.isLoanExistInTemp(clt.getToFinID(), false);
			if (isExists) {
				String[] parameters = new String[1];
				parameters[0] = "To Loan is already exist in progress";
				auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "30550", parameters, null));

			}

		}

		if (RequestSource.UPLOAD.name().equals(clk.getRequestSource()) || clk.isCancelProcess()) {
			return auditDetail;
		}

		if (ReceiptMode.EXCESS.equals(frh.getReceiptMode())) {
			FinExcessAmount excess = finExcessAmountDAO.getFinExcessByID(clt.getExcessId());
			if (excess == null
					|| excess.getReservedAmt().add(excess.getBalanceAmt()).compareTo(clt.getTransferAmount()) < 0) {
				ErrorDetail errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("60205", "", null),
						PennantConstants.default_Language);
				auditDetail.setErrorDetail(errorDetail);
			}

			if (paymentDetailDAO.getPaymentId(clt.getExcessId())) {
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("60205", "", null)));
			}
		}
		return auditDetail;
	}

	public void executeAccounting(CrossLoanTransfer crossLoan) {
		logger.debug(Literal.ENTERING);

		FinanceMain main = financeMainService.getFinanceMainById(crossLoan.getFromFinID(), false);

		AEEvent aeEvent = new AEEvent();
		aeEvent.setEntityCode(main.getEntityCode());
		aeEvent.setPostingUserBranch(crossLoan.getUserDetails().getBranchCode());
		aeEvent.setAccountingEvent(AccountingEvent.CROSS_LOAN_FROM);
		aeEvent.setFinID(crossLoan.getFromFinID());
		aeEvent.setFinReference(crossLoan.getFromFinReference());
		aeEvent.setValueDate(crossLoan.getValueDate());

		aeEvent.setFinReference(main.getFinReference());
		aeEvent.setFinID(main.getFinID());

		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
		if (amountCodes == null) {
			amountCodes = new AEAmountCodes();
		}

		aeEvent.setBranch(crossLoan.getUserDetails().getBranchCode());
		aeEvent.setCcy(main.getFinCcy());
		amountCodes.setUserBranch(crossLoan.getUserDetails().getBranchCode());
		amountCodes.setEntitycode(main.getEntityCode());
		amountCodes.setFinType(main.getFinType());
		aeEvent.setBranch(main.getFinBranch());

		Map<String, Object> dataMap = null;
		dataMap = amountCodes.getDeclaredFieldValues();
		aeEvent.setDataMap(dataMap);

		aeEvent.getDataMap().put("rd_amount", crossLoan.getTransferAmount());
		aeEvent.getDataMap().put("ae_isWriteOff", main.isWriteoffLoan());

		long accountsetId = accountingSetDAO.getAccountingSetId(AccountingEvent.CROSS_LOAN_FROM);

		aeEvent.getAcSetIDList().add(accountsetId);
		aeEvent = postingsPreparationUtil.postAccounting(aeEvent);
		crossLoan.setFromLinkedTranId(aeEvent.getLinkedTranId()); // setting LinkedTranId

		FinanceMain fm = financeMainService.getFinanceMainById(crossLoan.getToFinID(), false);

		AEEvent aeEvent1 = new AEEvent();
		aeEvent1.setEntityCode(fm.getEntityCode());
		aeEvent1.setPostingUserBranch(crossLoan.getUserDetails().getBranchCode());
		aeEvent1.setAccountingEvent(AccountingEvent.CROSS_LOAN_TO);
		aeEvent1.setFinID(crossLoan.getToFinID());
		aeEvent1.setFinReference(crossLoan.getToFinReference());
		aeEvent1.setValueDate(crossLoan.getValueDate());

		aeEvent1.setFinReference(fm.getFinReference());
		aeEvent1.setFinID(fm.getFinID());

		AEAmountCodes amountCodes2 = aeEvent1.getAeAmountCodes();
		if (amountCodes2 == null) {
			amountCodes2 = new AEAmountCodes();
		}

		aeEvent1.setBranch(crossLoan.getUserDetails().getBranchCode());
		aeEvent1.setCcy(fm.getFinCcy());
		amountCodes2.setEntitycode(fm.getEntityCode());
		amountCodes2.setUserBranch(crossLoan.getUserDetails().getBranchCode());
		amountCodes2.setFinType(fm.getFinType());
		aeEvent1.setBranch(fm.getFinBranch());

		dataMap = amountCodes2.getDeclaredFieldValues();
		aeEvent1.setDataMap(dataMap);
		aeEvent1.getDataMap().put("rd_amount", crossLoan.getTransferAmount());

		long accountsetId1 = accountingSetDAO.getAccountingSetId(AccountingEvent.CROSS_LOAN_TO);

		aeEvent1.getAcSetIDList().add(accountsetId1);
		aeEvent1 = postingsPreparationUtil.postAccounting(aeEvent1);
		crossLoan.setToLinkedTranId(aeEvent1.getLinkedTranId());

		logger.debug(Literal.LEAVING);
	}

	private FinanceDetail createReceipt(CrossLoanKnockOff clko) {
		CrossLoanKnockoffUpload clku = clko.getCrossLoanKnockoffUpload();

		ReceiptUploadDetail rud = new ReceiptUploadDetail();

		String entityCode = clku.getEntityCode();

		rud.setReference(clku.getToFinReference());
		rud.setFinID(clku.getToFm().getFinID());
		rud.setAllocationType(clku.getAllocationType());
		Date appDate = SysParamUtil.getAppDate();
		rud.setValueDate(clko.getValueDate());
		rud.setRealizationDate(appDate);
		rud.setReceivedDate(appDate);
		rud.setReceiptAmount(clku.getExcessAmount());
		rud.setExcessAdjustTo(RepayConstants.EXCESSADJUSTTO_EXCESS);
		rud.setReceiptMode((RepayConstants.EXAMOUNTTYPE_EXCESS.equals(clku.getExcessType())) ? ReceiptMode.EXCESS
				: ReceiptMode.PAYABLE);
		rud.setReceiptPurpose(FinServiceEvent.SCHDRPY);
		rud.setStatus(RepayConstants.PAYSTATUS_REALIZED);
		rud.setReceiptChannel(PennantConstants.List_Select);

		List<UploadAlloctionDetail> list = new ArrayList<>();

		for (CrossLoanKnockoffUpload alloc : clku.getAllocations()) {
			UploadAlloctionDetail uad = new UploadAlloctionDetail();

			uad.setRootId(String.valueOf(alloc.getFeeId()));
			uad.setAllocationType(Allocation.getCode(alloc.getCode()));
			uad.setReferenceCode(alloc.getCode());
			uad.setStrPaidAmount(String.valueOf(PennantApplicationUtil.formateAmount(alloc.getAmount(), 2)));

			receiptDataValidator.validateAllocations(uad);

			if (!uad.getErrorDetails().isEmpty()) {
				clku.setProgress(EodConstants.PROGRESS_FAILED);
				clku.setErrorCode(uad.getErrorDetails().get(0).getCode());
				clku.setErrorDesc(uad.getErrorDetails().get(0).getError());
			}

			list.add(uad);
		}

		rud.setListAllocationDetails(list);

		FinServiceInstruction fsi = receiptService.buildFinServiceInstruction(rud, entityCode);

		fsi.setReqType("Post");
		fsi.setReceiptUpload(true);
		fsi.setRequestSource(RequestSource.UPLOAD);
		fsi.setLoggedInUser(clku.getUserDetails());
		fsi.setKnockOffReceipt(true);
		fsi.setUploadAllocationDetails(list);

		if (ReceiptMode.EXCESS.equals(rud.getReceiptMode())) {
			fsi.setReceiptDetail(null);
			fsi.setReceiptDetails(receiptService.prepareReceiptDetails(clku.getExcessList(), rud));
		} else if (ReceiptMode.PAYABLE.equals(rud.getReceiptMode())) {
			fsi.setAdviseId(clku.getManualAdvise().getAdviseID());
		}

		fsi.setKnockOffReceipt(true);
		fsi.setKnockoffType(KnockOffType.CROSS_LOAN.code());

		return receiptService.receiptTransaction(fsi);
	}

	@Override
	public CrossLoanKnockOff getCrossLoanHeaderById(long crossLoanHeaderId, String type) {
		return crossLoanKnockOffDAO.getCrossLoanHeaderById(crossLoanHeaderId, type);
	}

	@Override
	public CrossLoanTransfer getCrossLoanTransferById(long crossLoanId, String string) {
		return crossLoanTransferDAO.getCrossLoanTransferById(crossLoanId, string);
	}

	protected AuditHeader getAuditHeader(FinReceiptData frd, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, null, frd);
		return new AuditHeader(frd.getFinReference(), null, null, null, auditDetail,
				frd.getReceiptHeader().getUserDetails(), null);
	}

	@Autowired
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	@Autowired
	public void setCrossLoanKnockOffDAO(CrossLoanKnockOffDAO crossLoanKnockOffDAO) {
		this.crossLoanKnockOffDAO = crossLoanKnockOffDAO;
	}

	@Autowired
	public void setCrossLoanTransferDAO(CrossLoanTransferDAO crossLoanTransferDAO) {
		this.crossLoanTransferDAO = crossLoanTransferDAO;
	}

	@Autowired
	public void setAccountingSetDAO(AccountingSetDAO accountingSetDAO) {
		this.accountingSetDAO = accountingSetDAO;
	}

	@Autowired
	public void setFinanceMainService(FinanceMainService financeMainService) {
		this.financeMainService = financeMainService;
	}

	public void setReceiptService(ReceiptService receiptService) {
		this.receiptService = receiptService;
	}

	@Autowired
	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}

	@Autowired
	public void setReceiptDataValidator(ReceiptDataValidator receiptDataValidator) {
		this.receiptDataValidator = receiptDataValidator;
	}

	@Autowired
	public void setFinExcessAmountDAO(FinExcessAmountDAO finExcessAmountDAO) {
		this.finExcessAmountDAO = finExcessAmountDAO;
	}

	@Autowired
	public void setPaymentDetailDAO(PaymentDetailDAO paymentDetailDAO) {
		this.paymentDetailDAO = paymentDetailDAO;
	}

	@Autowired
	public void setReceiptCancellationService(ReceiptCancellationService receiptCancellationService) {
		this.receiptCancellationService = receiptCancellationService;
	}
}