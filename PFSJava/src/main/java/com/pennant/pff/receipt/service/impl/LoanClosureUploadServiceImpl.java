package com.pennant.pff.receipt.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

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
import com.pennant.pff.receipt.dao.LoanClosureUploadDAO;
import com.pennant.pff.receipt.loanclosureerror.LoanClosureUploadError;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.service.impl.AUploadServiceImpl;
import com.pennanttech.dataengine.ProcessRecord;
import com.pennanttech.dataengine.model.DataEngineAttributes;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.RequestSource;
import com.pennanttech.pff.file.UploadTypes;
import com.pennanttech.pff.receipt.constants.Allocation;

public class LoanClosureUploadServiceImpl extends AUploadServiceImpl {
	private static final Logger logger = LogManager.getLogger(LoanClosureUploadServiceImpl.class);

	private LoanClosureUploadDAO loanClosureUploadDAO;
	private ProcessRecord loanClosureUploadProcessRecord;
	private FinanceMainDAO financeMainDAO;
	private ReceiptService receiptService;
	protected FinanceRepaymentsDAO financeRepaymentsDAO;
	private FinExcessAmountDAO finExcessAmountDAO;
	private ManualAdviseDAO manualAdviseDAO;

	@Override
	public void doValidate(FileUploadHeader header, Object object) {
		LoanClosureUpload detail = null;

		if (object instanceof LoanClosureUpload) {
			detail = (LoanClosureUpload) object;
		}

		if (detail == null) {
			throw new AppException("Invalid Data transferred...");
		}

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

		if (!closureType.equals(null)
				&& !(closureType.equalsIgnoreCase("CLOSURE") || closureType.equalsIgnoreCase("FORE-CLOSURE")
						|| closureType.equalsIgnoreCase("REPOSSESSION") || closureType.equalsIgnoreCase("TOPUP")
						|| closureType.equalsIgnoreCase("CANCEL") || closureType.equalsIgnoreCase("SETTLEMENT"))) {
			setError(detail, LoanClosureUploadError.LCU_04);
			return;
		}

		String reason = detail.getReasonCode();

		if (StringUtils.isNotBlank(reason)) {

			boolean isExists = loanClosureUploadDAO.getReason(reason);

			if (!isExists) {
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
	}

	protected void setError(LoanClosureUpload detail, LoanClosureUploadError error) {
		detail.setProgress(EodConstants.PROGRESS_FAILED);
		detail.setErrorCode(error.name());
		detail.setErrorDesc(error.description());
	}

	@Override
	public void doApprove(List<FileUploadHeader> headers) {
		logger.debug(Literal.ENTERING);

		new Thread(() -> {
			DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
			txDef.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
			TransactionStatus txStatus = null;

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
					lcu.setAllocations(loanClosureUploadDAO.getAllocations(lcu.getId(), header.getId()));
					doValidate(header, lcu);

					header.getUploadDetails().add(lcu);

					if (lcu.getProgress() == EodConstants.PROGRESS_FAILED) {
						failRecords++;
					} else {
						sucessRecords++;
						lcu.setProgress(EodConstants.PROGRESS_SUCCESS);
					}

					if (lcu.getProgress() == EodConstants.PROGRESS_SUCCESS) {
						txStatus = transactionManager.getTransaction(txDef);

						try {
							createReceipt(lcu, header);
						} catch (AppException e) {
							lcu.setProgress(EodConstants.PROGRESS_FAILED);
							lcu.setErrorCode("9999");
							lcu.setErrorDesc(e.getMessage());
						}

						transactionManager.commit(txStatus);
					}
					LoggedInUser userDetails = header.getUserDetails();

					if (userDetails == null) {
						userDetails = new LoggedInUser();
						userDetails.setLoginUsrID(header.getApprovedBy());
						userDetails.setUserName(header.getApprovedByName());
					}
				}

				try {
					txStatus = transactionManager.getTransaction(txDef);

					loanClosureUploadDAO.update(details);

					header.setSuccessRecords(sucessRecords);
					header.setFailureRecords(failRecords);

					StringBuilder remarks = new StringBuilder("Process Completed");

					if (failRecords > 0) {
						remarks.append(" with exceptions, ");
					}

					remarks.append(" Total Records : ").append(header.getTotalRecords());
					remarks.append(" Success Records : ").append(sucessRecords);
					remarks.append(" Failed Records : ").append(failRecords);

					List<FileUploadHeader> headerList = new ArrayList<>();
					headerList.add(header);

					updateHeader(headerList, true);

					transactionManager.commit(txStatus);
				} catch (Exception e) {
					logger.error(ERROR_LOG, e.getCause(), e.getMessage(), e.getLocalizedMessage(), e);

					if (txStatus != null) {
						transactionManager.rollback(txStatus);
					}
				} finally {
					txStatus = null;
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
		// rud.setAllocationType(lcu.getAllocationType());
		rud.setValueDate(appDate);
		rud.setRealizationDate(appDate);
		rud.setReceivedDate(appDate);
		rud.setReceiptAmount(lcu.getAmount());
		rud.setExcessAdjustTo(RepayConstants.EXCESSADJUSTTO_EXCESS);
		rud.setReceiptPurpose("ES");
		rud.setReceiptMode("E");
		rud.setStatus(RepayConstants.PAYSTATUS_REALIZED);
		rud.setReceiptChannel(PennantConstants.List_Select);

		List<UploadAlloctionDetail> list = new ArrayList<>();

		Map<String, BigDecimal> waivedAmounts = new HashMap<>();
		for (LoanClosureUpload alloc : lcu.getAllocations()) {
			if (alloc.getCode().contains("_W")) {
				String code = alloc.getCode().split("_")[0];

				waivedAmounts.put(code, alloc.getAmount());
			}
		}

		for (LoanClosureUpload alloc : lcu.getAllocations()) {
			UploadAlloctionDetail uad = new UploadAlloctionDetail();

			uad.setRootId(String.valueOf(alloc.getFeeId()));
			uad.setAllocationType(Allocation.getCode(alloc.getCode()));
			uad.setReferenceCode(alloc.getCode());
			uad.setStrPaidAmount(String.valueOf(alloc.getAmount()));
			uad.setPaidAmount(alloc.getAmount());
			if (waivedAmounts.get(alloc.getCode()) != null) {
				uad.setWaivedAmount(waivedAmounts.get(alloc.getCode()));
			}

			list.add(uad);
		}

		rud.setListAllocationDetails(list);

		FinServiceInstruction fsi = receiptService.buildFinServiceInstruction(rud, entityCode);

		fsi.setReqType("Post");
		fsi.setReceiptUpload(true);
		fsi.setRequestSource(RequestSource.UPLOAD);
		LoggedInUser userDetails = lcu.getUserDetails();
		if (FinanceConstants.EARLYSETTLEMENT.equals("ES")) {
			fsi.setClosureType(lcu.getClosureType());
		}

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

		FinanceDetail fd = receiptService.receiptTransaction(fsi);

		FinScheduleData schd = fd.getFinScheduleData();
		if (!schd.getErrorDetails().isEmpty()) {
			ErrorDetail error = schd.getErrorDetails().get(0);
			lcu.setProgress(EodConstants.PROGRESS_FAILED);
			lcu.setErrorCode(error.getCode());
			lcu.setErrorDesc(error.getError());
		} else {
			lcu.setReceiptID(fd.getReceiptId());
			lcu.setProgress(EodConstants.PROGRESS_SUCCESS);
		}
	}

	@Override
	public void doReject(List<FileUploadHeader> headers) {
		List<Long> headerIdList = headers.stream().map(FileUploadHeader::getId).collect(Collectors.toList());

		DefaultTransactionDefinition txDef = new DefaultTransactionDefinition(
				TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		TransactionStatus txStatus = null;
		try {
			txStatus = transactionManager.getTransaction(txDef);

			loanClosureUploadDAO.update(headerIdList, ERR_CODE, ERR_DESC, EodConstants.PROGRESS_FAILED);

			headers.forEach(h1 -> h1.setRemarks(ERR_DESC));
			updateHeader(headers, false);

			transactionManager.commit(txStatus);
		} catch (Exception e) {
			logger.error(ERROR_LOG, e.getCause(), e.getMessage(), e.getLocalizedMessage(), e);

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
