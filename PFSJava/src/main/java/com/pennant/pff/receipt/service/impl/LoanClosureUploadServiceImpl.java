package com.pennant.pff.receipt.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.TransactionStatus;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.Repayments.FinanceRepaymentsDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.loanclosure.LoanClosureUpload;
import com.pennant.backend.model.receiptupload.ReceiptUploadDetail;
import com.pennant.backend.model.receiptupload.UploadAlloctionDetail;
import com.pennant.backend.service.finance.ReceiptService;
import com.pennant.backend.service.finance.impl.ManualAdviceUtil;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.receipt.ClosureType;
import com.pennant.pff.receipt.dao.LoanClosureUploadDAO;
import com.pennant.pff.receipt.error.LoanClosureUploadError;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.service.impl.AUploadServiceImpl;
import com.pennanttech.dataengine.ProcessRecord;
import com.pennanttech.dataengine.model.DataEngineAttributes;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.RequestSource;
import com.pennanttech.pff.file.UploadTypes;
import com.pennanttech.pff.receipt.constants.Allocation;
import com.pennanttech.pff.receipt.constants.AllocationType;

public class LoanClosureUploadServiceImpl extends AUploadServiceImpl<LoanClosureUpload> {
	private static final Logger logger = LogManager.getLogger(LoanClosureUploadServiceImpl.class);

	private LoanClosureUploadDAO loanClosureUploadDAO;
	private ProcessRecord loanClosureUploadProcessRecord;
	private FinanceMainDAO financeMainDAO;
	private ReceiptService receiptService;
	protected FinanceRepaymentsDAO financeRepaymentsDAO;
	private FinExcessAmountDAO finExcessAmountDAO;
	private ManualAdviseDAO manualAdviseDAO;

	protected LoanClosureUpload getDetail(Object object) {
		if (object instanceof LoanClosureUpload detail) {
			return detail;
		}

		throw new AppException(IN_VALID_OBJECT);
	}

	@Override
	public void doValidate(FileUploadHeader header, Object object) {
		LoanClosureUpload detail = getDetail(object);

		String reference = detail.getReference();

		logger.info("Validating the Data for the reference {}", reference);

		if (StringUtils.isBlank(reference)) {
			setError(detail, LoanClosureUploadError.LCU_01);
			return;
		}

		FinanceMain fm = financeMainDAO.getFinanceMain(reference, header.getEntityCode());

		if (fm == null) {
			setError(detail, LoanClosureUploadError.LCU_02);
			return;
		}

		if (!fm.isFinIsActive()) {
			setError(detail, LoanClosureUploadError.LCU_03);
			return;
		}

		String closureType = detail.getClosureType();

		if (closureType != null && !(ClosureType.isValid(closureType) || closureType.equalsIgnoreCase("SETTLEMENT"))
				&& StringUtils.isNotBlank(closureType)) {
			setError(detail, LoanClosureUploadError.LCU_04);
			return;
		}

		String reason = detail.getReasonCode();

		if (StringUtils.isNotBlank(reason)) {
			if (!loanClosureUploadDAO.getReason(reason)) {
				setError(detail, LoanClosureUploadError.LCU_05);
				return;
			}
		}

		BigDecimal balanceAmount = BigDecimal.ZERO;

		List<FinExcessAmount> excessList = finExcessAmountDAO.getExcessAmountsByRef(fm.getFinID());
		for (FinExcessAmount fea : excessList) {
			balanceAmount = balanceAmount.add(fea.getBalanceAmt());
		}

		List<ManualAdvise> maList = manualAdviseDAO.getPaybleAdvises(fm.getFinID(), "_AVIEW");
		balanceAmount = balanceAmount.add(ManualAdviceUtil.getBalanceAmount(maList));

		detail.setExcessList(excessList);
		detail.setAdvises(maList);
		detail.setAmount(balanceAmount);
		detail.setReferenceID(fm.getFinID());

		setSuccesStatus(detail);
	}

	protected void setError(LoanClosureUpload detail, LoanClosureUploadError error) {
		setFailureStatus(detail, error.name(), error.description());
	}

	@Override
	public void doApprove(List<FileUploadHeader> headers) {
		logger.debug(Literal.ENTERING);

		new Thread(() -> {
			Date appDate = SysParamUtil.getAppDate();

			for (FileUploadHeader header : headers) {
				logger.info("Processing the File {}", header.getFileName());

				List<LoanClosureUpload> details = loanClosureUploadDAO.loadRecordData(header.getId());
				header.setAppDate(appDate);
				header.setTotalRecords(details.size());
				int sucessRecords = 0;
				int failRecords = 0;

				for (LoanClosureUpload lcu : details) {
					lcu.setAppDate(appDate);
					doValidate(header, lcu);

					header.getUploadDetails().add(lcu);

					if (lcu.getProgress() == EodConstants.PROGRESS_FAILED) {
						failRecords++;
					} else {
						sucessRecords++;
					}

					if (lcu.getProgress() == EodConstants.PROGRESS_SUCCESS) {
						lcu.setAllocations(loanClosureUploadDAO.getAllocations(lcu.getId(), header.getId()));
						createReceipt(lcu, header);
					}
					LoggedInUser userDetails = header.getUserDetails();

					if (userDetails == null) {
						userDetails = new LoggedInUser();
						userDetails.setLoginUsrID(header.getApprovedBy());
						userDetails.setUserName(header.getApprovedByName());
					}
				}

				try {
					loanClosureUploadDAO.update(details);

					header.setSuccessRecords(sucessRecords);
					header.setFailureRecords(failRecords);

					List<FileUploadHeader> headerList = new ArrayList<>();
					headerList.add(header);

					updateHeader(headerList, true);

				} catch (Exception e) {
					logger.error(Literal.EXCEPTION, e);
				}

				logger.info("Processed the File {}", header.getFileName());

			}
		}).start();
	}

	private void createReceipt(LoanClosureUpload lcu, FileUploadHeader header) {
		String entityCode = header.getEntityCode();

		ReceiptUploadDetail rud = new ReceiptUploadDetail();
		Date appDate = header.getAppDate();

		rud.setReference(lcu.getReference());
		rud.setFinID(lcu.getReferenceID());
		rud.setValueDate(appDate);
		rud.setRealizationDate(appDate);
		rud.setReceivedDate(appDate);
		rud.setDepositDate(appDate);
		rud.setReceiptAmount(lcu.getAmount());
		rud.setExcessAdjustTo(RepayConstants.EXCESSADJUSTTO_EXCESS);
		rud.setReceiptPurpose(FinanceConstants.EARLYSETTLEMENT);
		rud.setReceiptMode(PennantConstants.List_Select);
		rud.setStatus(RepayConstants.PAYSTATUS_REALIZED);
		rud.setReceiptChannel(PennantConstants.List_Select);

		List<UploadAlloctionDetail> list = new ArrayList<>();

		for (LoanClosureUpload alloc : lcu.getAllocations()) {
			UploadAlloctionDetail uad = new UploadAlloctionDetail();
			uad.setRootId(String.valueOf(alloc.getFeeId()));
			uad.setAllocationType(Allocation.getCode(getAllocationCode(alloc)));
			uad.setReferenceCode(getAllocationCode(alloc));
			uad.setWaivedAmount(alloc.getAmount());

			list.add(uad);
		}

		rud.setListAllocationDetails(list);

		if (!list.isEmpty()) {
			rud.setAllocationType(AllocationType.MANUAL);
		}

		FinServiceInstruction fsi = receiptService.buildFinServiceInstruction(rud, entityCode);

		fsi.setReqType("Post");
		fsi.setReceiptUpload(true);
		fsi.setClosureReceipt(true);
		fsi.setRequestSource(RequestSource.UPLOAD);
		LoggedInUser userDetails = lcu.getUserDetails();
		fsi.setClosureType(lcu.getClosureType());

		if (userDetails == null) {
			userDetails = new LoggedInUser();
			userDetails.setLoginUsrID(header.getApprovedBy());
			userDetails.setUserName(header.getApprovedByName());
		}

		fsi.setLoggedInUser(userDetails);
		fsi.setKnockOffReceipt(true);
		fsi.setReceiptDetail(null);
		fsi.setReceiptDetails(receiptService.prepareRCDForExcess(lcu.getExcessList(), rud));
		fsi.getReceiptDetails().addAll(receiptService.prepareRCDForMA(lcu.getAdvises(), rud));
		FinanceDetail fd = null;

		TransactionStatus txStatus = getTransactionStatus();

		try {
			fd = receiptService.receiptTransaction(fsi);
			transactionManager.commit(txStatus);
		} catch (Exception e) {
			if (txStatus != null) {
				transactionManager.rollback(txStatus);
			}

			setFailureStatus(lcu, e.getMessage());
			return;
		}

		if (fd == null) {
			setFailureStatus(lcu, "Finance Detail is null.");
			return;
		}

		FinScheduleData schd = fd.getFinScheduleData();
		if (!schd.getErrorDetails().isEmpty()) {
			setFailureStatus(lcu, schd.getErrorDetails().get(0));
		} else {
			lcu.setReceiptID(fd.getReceiptId());
			setSuccesStatus(lcu);
		}
	}

	@Override
	public void doReject(List<FileUploadHeader> headers) {
		List<Long> headerIdList = headers.stream().map(FileUploadHeader::getId).toList();

		TransactionStatus txStatus = getTransactionStatus();
		try {

			headers.forEach(h1 -> {
				h1.setRemarks(REJECT_DESC);
				h1.getUploadDetails().addAll(loanClosureUploadDAO.loadRecordData(h1.getId()));
			});

			loanClosureUploadDAO.update(headerIdList, REJECT_CODE, REJECT_DESC);

			updateHeader(headers, false);

			transactionManager.commit(txStatus);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);

			if (txStatus != null) {
				transactionManager.rollback(txStatus);
			}
		}
	}

	@Override
	public boolean isInProgress(Long headerID, Object... args) {
		return loanClosureUploadDAO.isInProgress((String) args[0], headerID);
	}

	@Override
	public void uploadProcess() {
		uploadProcess(UploadTypes.LOAN_CLOSURE.name(), loanClosureUploadProcessRecord, this, "LoanClosureUpload");
	}

	@Override
	public String getSqlQuery() {
		return loanClosureUploadDAO.getSqlQuery();
	}

	@Override
	public void validate(DataEngineAttributes attributes, MapSqlParameterSource record) throws Exception {
		//
	}

	private String getAllocationCode(LoanClosureUpload alloc) {
		String code = alloc.getCode();

		code = code.replace("_W", "");
		code = code.replace("_w", "");

		if (code.equalsIgnoreCase("PRINCIPAL")) {
			code = "PRI";
		}

		if (code.equalsIgnoreCase("INTEREST")) {
			code = "PFT";
		}

		if (code.equalsIgnoreCase("FTINTEREST")) {
			code = "FUTPFT";
		}

		if (code.equalsIgnoreCase("FTPRINCIPAL")) {
			code = "FUTPRI";
		}

		if (code.equalsIgnoreCase("LPP")) {
			code = "ODC";
		}

		return code;
	}

	@Override
	public ProcessRecord getProcessRecord() {
		return loanClosureUploadProcessRecord;
	}

	@Autowired
	public void setLoanClosureUploadProcessRecord(ProcessRecord loanClosureUploadProcessRecord) {
		this.loanClosureUploadProcessRecord = loanClosureUploadProcessRecord;
	}

	@Autowired
	public void setLoanClosureUploadDAO(LoanClosureUploadDAO loanClosureUploadDAO) {
		this.loanClosureUploadDAO = loanClosureUploadDAO;
	}

	@Autowired
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	@Autowired
	public void setReceiptService(ReceiptService receiptService) {
		this.receiptService = receiptService;
	}

	@Autowired
	public void setFinanceRepaymentsDAO(FinanceRepaymentsDAO financeRepaymentsDAO) {
		this.financeRepaymentsDAO = financeRepaymentsDAO;
	}

	@Autowired
	public void setFinExcessAmountDAO(FinExcessAmountDAO finExcessAmountDAO) {
		this.finExcessAmountDAO = finExcessAmountDAO;
	}

	@Autowired
	public void setManualAdviseDAO(ManualAdviseDAO manualAdviseDAO) {
		this.manualAdviseDAO = manualAdviseDAO;
	}
}
