package com.pennant.pff.receipt.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.receiptupload.ReceiptUploadDetail;
import com.pennant.backend.model.receiptupload.UploadAlloctionDetail;
import com.pennant.backend.service.finance.ReceiptService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.receipt.dao.CreateReceiptUploadDAO;
import com.pennant.pff.receipt.model.CreateReceiptUpload;
import com.pennant.pff.receipt.validate.CreateReceiptUploadDataValidator;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.service.impl.AUploadServiceImpl;
import com.pennanttech.dataengine.model.DataEngineAttributes;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pff.core.RequestSource;
import com.pennanttech.pff.file.UploadTypes;
import com.pennanttech.pff.receipt.constants.Allocation;
import com.pennanttech.pff.receipt.constants.AllocationType;
import com.pennanttech.pff.receipt.constants.ReceiptMode;

public class CreateReceiptUploadServiceImpl extends AUploadServiceImpl {
	private static final Logger logger = LogManager.getLogger(CreateReceiptUploadServiceImpl.class);

	private ReceiptService receiptService;
	private CreateReceiptUploadDAO createReceiptUploadDAO;
	private CreateReceiptUploadDataValidator createReceiptUploadDataValidator;

	public CreateReceiptUploadServiceImpl() {
		super();
	}

	@Override
	public void doValidate(FileUploadHeader header, Object object) {
		CreateReceiptUpload detail = null;

		if (object instanceof CreateReceiptUpload) {
			detail = (CreateReceiptUpload) object;
		}

		if (detail == null) {
			throw new AppException("Invalid Data transferred...");
		}

		createReceiptUploadDataValidator.validate(detail, header);
		createReceiptUploadDataValidator.validateAllocations(detail);
	}

	@Override
	public void doApprove(List<FileUploadHeader> headers) {
		new Thread(() -> {

			DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
			txDef.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
			TransactionStatus txStatus = null;

			Date appDate = SysParamUtil.getAppDate();
			boolean dedupCheck = SysParamUtil.isAllowed(SMTParameterConstants.RECEIPTUPLOAD_DEDUPCHECK);

			for (FileUploadHeader header : headers) {
				logger.info("Processing the File {}", header.getFileName());

				List<CreateReceiptUpload> details = createReceiptUploadDAO.getDetails(header.getId());
				int sucessRecords = 0;
				int failRecords = 0;

				for (CreateReceiptUpload receipt : details) {
					receipt.setAppDate(appDate);
					receipt.setDedupCheck(dedupCheck);
					receipt.setAllocations(createReceiptUploadDAO.getAllocations(receipt.getId(), header.getId()));
					doValidate(header, receipt);
					receipt.setUserDetails(header.getUserDetails());

					if (receipt.getProgress() == EodConstants.PROGRESS_SUCCESS) {
						txStatus = transactionManager.getTransaction(txDef);

						createReceipt(receipt, header.getEntityCode());

						transactionManager.commit(txStatus);
					}

					if (receipt.getProgress() == EodConstants.PROGRESS_FAILED) {
						failRecords++;
					} else {
						sucessRecords++;
					}

					try {
						txStatus = transactionManager.getTransaction(txDef);

						createReceiptUploadDAO.update(details);

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

						logger.info("Manual KnockOff Process is Initiated");

						transactionManager.commit(txStatus);
					} catch (Exception e) {
						logger.error(ERROR_LOG, e.getCause(), e.getMessage(), e.getLocalizedMessage(), e);

						if (txStatus != null) {
							transactionManager.rollback(txStatus);
						}
					} finally {
						txStatus = null;
					}

				}
				logger.info("Processed the File {}", header.getFileName());

			}
		}).start();
	}

	private void createReceipt(CreateReceiptUpload reaceipt, String entityCode) {
		ReceiptUploadDetail rud = new ReceiptUploadDetail();

		rud.setReference(reaceipt.getReference());
		rud.setFinID(reaceipt.getReferenceID());
		rud.setAllocationType(reaceipt.getAllocationType());

		rud.setValueDate(reaceipt.getAppDate());
		rud.setRealizationDate(reaceipt.getAppDate());
		rud.setReceivedDate(reaceipt.getAppDate());
		rud.setReceiptAmount(reaceipt.getReceiptAmount());
		rud.setExcessAdjustTo(RepayConstants.EXCESSADJUSTTO_EXCESS);
		rud.setReceiptMode(reaceipt.getReceiptMode());
		rud.setSubReceiptMode(reaceipt.getSubReceiptMode());
		// rud.setReceiptMode("E".equals(fc.getExcessType()) ? ReceiptMode.EXCESS : ReceiptMode.PAYABLE);
		rud.setReceiptPurpose(reaceipt.getReceiptPurpose());
		rud.setStatus(RepayConstants.PAYSTATUS_REALIZED);
		rud.setReceiptChannel(PennantConstants.List_Select);
		rud.setDepositDate(reaceipt.getDepositDate());
		rud.setBankCode(reaceipt.getBankCode());
		rud.setEffectSchdMethod(reaceipt.getEffectSchdMethod());
		String receiptMode = reaceipt.getReceiptMode();
		if (ReceiptMode.CHEQUE.equals(receiptMode) || ReceiptMode.DD.equals(receiptMode)) {
			rud.setTransactionRef(reaceipt.getChequeNumber());
			rud.setFavourNumber(reaceipt.getChequeNumber());
		}

		List<UploadAlloctionDetail> list = new ArrayList<>();

		Map<String, BigDecimal> waivedAmounts = new HashMap<>();
		for (CreateReceiptUpload alloc : reaceipt.getAllocations()) {
			if (alloc.getCode().contains("_W")) {
				String code = alloc.getCode().split("_")[0];

				waivedAmounts.put(code, alloc.getAmount());
			}
		}

		if (AllocationType.MANUAL.equals(reaceipt.getAllocationType())) {
			for (CreateReceiptUpload alloc : reaceipt.getAllocations()) {
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
		}

		rud.setListAllocationDetails(list);

		if (AllocationType.MANUAL.equals(reaceipt.getAllocationType()) && list != null) {
			if (!(reaceipt.getReceiptAmount().equals(getSumOfAllocations(list)))) {
				reaceipt.setProgress(EodConstants.PROGRESS_FAILED);
				reaceipt.setErrorDesc("RECEIPT Amount and Allocations amount should be same");
				return;
			}
		}

		FinServiceInstruction fsi = receiptService.buildFinServiceInstruction(rud, entityCode);

		fsi.setReqType("Post");
		fsi.setReceiptUpload(true);
		fsi.setRequestSource(RequestSource.UPLOAD);
		fsi.setLoggedInUser(reaceipt.getUserDetails());
		fsi.setKnockOffReceipt(true);
		if (FinanceConstants.EARLYSETTLEMENT.equals(reaceipt.getReceiptPurpose())) {
			fsi.setClosureType(reaceipt.getClosureType());
		}

		FinanceDetail fd = receiptService.receiptTransaction(fsi);

		FinScheduleData schd = fd.getFinScheduleData();
		if (!schd.getErrorDetails().isEmpty()) {
			ErrorDetail error = schd.getErrorDetails().get(0);
			reaceipt.setProgress(EodConstants.PROGRESS_FAILED);
			reaceipt.setErrorCode(error.getCode());
			reaceipt.setErrorDesc(error.getError());
		} else {
			reaceipt.setReceiptID(fd.getReceiptId());
			reaceipt.setProgress(EodConstants.PROGRESS_SUCCESS);
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

			createReceiptUploadDAO.update(headerIdList, ERR_CODE, ERR_DESC, EodConstants.PROGRESS_FAILED);

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

	private BigDecimal getSumOfAllocations(List<UploadAlloctionDetail> list) {
		BigDecimal sum = BigDecimal.ZERO;
		for (UploadAlloctionDetail cru : list) {
			sum = sum.add(cru.getPaidAmount());
		}

		return sum;
	}

	@Override
	public void uploadProcess() {
		uploadProcess(UploadTypes.CREATE_RECEIPT.name(), createReceiptUploadDataValidator, this,
				"CreateReceiptUploadHeader");
	}

	@Override
	public void validate(DataEngineAttributes attributes, MapSqlParameterSource record) throws Exception {
		// Implemented in process record.
	}

	@Override
	public CreateReceiptUploadDataValidator getProcessRecord() {
		return createReceiptUploadDataValidator;
	}

	@Override
	public String getSqlQuery() {
		return createReceiptUploadDAO.getSqlQuery();
	}

	@Autowired
	public void setReceiptService(ReceiptService receiptService) {
		this.receiptService = receiptService;
	}

	@Autowired
	public void setCreateReceiptUploadDAO(CreateReceiptUploadDAO createReceiptUploadDAO) {
		this.createReceiptUploadDAO = createReceiptUploadDAO;
	}

	@Autowired
	public void setCreateReceiptUploadDataValidator(CreateReceiptUploadDataValidator createReceiptUploadDataValidator) {
		this.createReceiptUploadDataValidator = createReceiptUploadDataValidator;
	}

}
