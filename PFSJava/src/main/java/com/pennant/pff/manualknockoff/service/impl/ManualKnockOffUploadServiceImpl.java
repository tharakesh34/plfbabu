package com.pennant.pff.manualknockoff.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.receiptupload.ReceiptUploadDetail;
import com.pennant.backend.model.receiptupload.UploadAlloctionDetail;
import com.pennant.backend.service.finance.ReceiptService;
import com.pennant.backend.util.RepayConstants;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.fee.AdviseType;
import com.pennant.pff.manualknockoff.dao.ManualKnockOffUploadDAO;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.service.impl.AUploadServiceImpl;
import com.pennanttech.dataengine.ProcessRecord;
import com.pennanttech.model.knockoff.ManualKnockOffUpload;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pff.core.RequestSource;
import com.pennanttech.pff.receipt.constants.Allocation;
import com.pennanttech.pff.receipt.constants.AllocationType;
import com.pennanttech.pff.receipt.constants.ReceiptMode;
import com.pennanttech.pff.receipt.upload.ReceiptDataValidator;

public class ManualKnockOffUploadServiceImpl extends AUploadServiceImpl {
	private static final Logger logger = LogManager.getLogger(ManualKnockOffUploadServiceImpl.class);

	private ManualKnockOffUploadDAO manualKnockOffUploadDAO;
	private FinanceMainDAO financeMainDAO;
	private ManualKnockOffUploadProcessRecord manualKnockOffUploadProcessRecord;
	private ReceiptDataValidator receiptDataValidator;
	private ReceiptService receiptService;
	private FinExcessAmountDAO finExcessAmountDAO;
	private ManualAdviseDAO manualAdviseDAO;

	@Override
	public void doValidate(FileUploadHeader header, Object object) {
		ManualKnockOffUpload detail = null;

		if (object instanceof ManualKnockOffUpload) {
			detail = (ManualKnockOffUpload) object;
		}

		if (detail == null) {
			throw new AppException("Invalid Data transferred...");
		}

		String reference = detail.getReference();

		logger.info("Validating the Data for the reference {}", reference);

		if (StringUtils.isBlank(reference)) {
			setError(detail, ManualKnockOffUploadError.MKOU_101);
			return;
		}

		FinanceMain fm = financeMainDAO.getFinanceMain(reference, header.getEntityCode());

		if (fm == null) {
			setError(detail, ManualKnockOffUploadError.MKOU_102);
			return;
		}

		if (!fm.isFinIsActive()) {
			setError(detail, ManualKnockOffUploadError.MKOU_103);
			return;
		}

		detail.setReferenceID(fm.getFinID());
		if (detail.getAllocationType() == null) {
			setError(detail, ManualKnockOffUploadError.MKOU_104);
			return;
		}

		String allocationType = StringUtils.upperCase(detail.getAllocationType());

		if ("MANUAL".equals(allocationType)) {
			allocationType = AllocationType.MANUAL;
		} else if ("AUTO".equals(allocationType)) {
			allocationType = AllocationType.AUTO;
		}

		detail.setAllocationType(allocationType);

		if (!AllocationType.MANUAL.equals(allocationType) && !AllocationType.AUTO.equals(allocationType)) {
			setError(detail, ManualKnockOffUploadError.MKOU_105);
			return;
		}

		List<ManualKnockOffUpload> allocations = detail.getAllocations();
		if (AllocationType.MANUAL.equals(allocationType) && allocations.isEmpty()) {
			setError(detail, ManualKnockOffUploadError.MKOU_106);
			return;
		}

		if (AllocationType.AUTO.equals(allocationType) && !allocations.isEmpty()) {
			setError(detail, ManualKnockOffUploadError.MKOU_107);
			return;
		}

		String excessType = detail.getExcessType();

		if ((!"E".equals(excessType) && !"A".equals(excessType)) || detail.getAdviseId() == null
				|| detail.getAdviseId() <= 0) {
			setError(detail, ManualKnockOffUploadError.MKOU_108);
			return;
		}

		BigDecimal balanceAmount = BigDecimal.ZERO;

		if ("E".equals(excessType) || "A".equals(excessType)) {
			List<FinExcessAmount> excessList = finExcessAmountDAO.getExcessAmountsByRefAndType(fm.getFinID(),
					excessType);

			if (CollectionUtils.isEmpty(excessList)) {
				setError(detail, ManualKnockOffUploadError.MKOU_109);
				return;
			}

			for (FinExcessAmount fea : excessList) {
				balanceAmount = balanceAmount.add(fea.getBalanceAmt());
			}
		} else {
			ManualAdvise ma = manualAdviseDAO.getManualAdviseById(detail.getAdviseId(), "");

			if (ma == null) {
				setError(detail, ManualKnockOffUploadError.MKOU_1011);
				return;
			}

			if (ma.getAdviseType() != AdviseType.PAYABLE.id()) {
				setError(detail, ManualKnockOffUploadError.MKOU_1012);
				return;
			}

			balanceAmount = ma.getAdviseAmount().subtract(ma.getPaidAmount().add(ma.getWaivedAmount()));
		}

		if (balanceAmount.compareTo(detail.getReceiptAmount()) < 0) {
			setError(detail, ManualKnockOffUploadError.MKOU_1010);
			return;
		}

		for (ManualKnockOffUpload alloc : allocations) {
			UploadAlloctionDetail uad = new UploadAlloctionDetail();

			uad.setRootId(String.valueOf(alloc.getFeeId()));
			uad.setAllocationType(allocationType);
			uad.setReferenceCode(alloc.getCode());
			uad.setStrPaidAmount(String.valueOf(alloc.getAmount()));

			receiptDataValidator.validateAllocations(uad);

			if (!uad.getErrorDetails().isEmpty()) {
				detail.setProgress(EodConstants.PROGRESS_FAILED);
				detail.setErrorCode(uad.getErrorDetails().get(0).getCode());
				detail.setErrorDesc(uad.getErrorDetails().get(0).getError());
				return;
			}
		}

		detail.setProgress(EodConstants.PROGRESS_SUCCESS);
		detail.setErrorCode("");
		detail.setErrorDesc("");

	}

	private void setError(ManualKnockOffUpload detail, ManualKnockOffUploadError error) {
		detail.setProgress(EodConstants.PROGRESS_FAILED);
		detail.setErrorCode(error.name());
		detail.setErrorDesc(error.description());
	}

	@Override
	public void doApprove(List<FileUploadHeader> headers) {
		new Thread(() -> {

			DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
			txDef.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
			TransactionStatus txStatus = null;

			for (FileUploadHeader header : headers) {
				logger.info("Processing the File {}", header.getFileName());

				List<ManualKnockOffUpload> details = manualKnockOffUploadDAO.getDetails(header.getId());

				header.setTotalRecords(details.size());
				int sucessRecords = 0;
				int failRecords = 0;

				for (ManualKnockOffUpload fc : details) {
					fc.setAllocations(manualKnockOffUploadDAO.getAllocations(fc.getId()));
					doValidate(header, fc);
					fc.setUserDetails(header.getUserDetails());

					if (fc.getProgress() == EodConstants.PROGRESS_SUCCESS) {
						txStatus = transactionManager.getTransaction(txDef);

						createReceipt(fc, header.getEntityCode());

						transactionManager.commit(txStatus);
					}

					if (fc.getProgress() == EodConstants.PROGRESS_FAILED) {
						failRecords++;
					} else {
						sucessRecords++;
					}
				}

				try {
					txStatus = transactionManager.getTransaction(txDef);

					manualKnockOffUploadDAO.update(details);

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
					headers.get(0).setThreadInProcess(false);
				}

				logger.info("Processed the File {}", header.getFileName());
			}
		}).start();

	}

	private void createReceipt(ManualKnockOffUpload fc, String entityCode) {
		ReceiptUploadDetail rud = new ReceiptUploadDetail();

		rud.setReference(fc.getReference());
		rud.setFinID(fc.getReferenceID());
		rud.setAllocationType(fc.getAllocationType());
		Date appDate = SysParamUtil.getAppDate();
		rud.setValueDate(appDate);
		rud.setRealizationDate(appDate);
		rud.setReceivedDate(appDate);
		rud.setReceiptAmount(fc.getReceiptAmount());
		rud.setExcessAdjustTo(fc.getExcessType());
		rud.setReceiptMode(ReceiptMode.EXCESS);
		rud.setReceiptPurpose("SP");
		rud.setStatus(RepayConstants.PAYSTATUS_REALIZED);

		List<UploadAlloctionDetail> list = new ArrayList<>();
		for (ManualKnockOffUpload alloc : fc.getAllocations()) {
			UploadAlloctionDetail uad = new UploadAlloctionDetail();

			uad.setRootId(String.valueOf(alloc.getFeeId()));
			uad.setAllocationType(Allocation.getCode(alloc.getCode()));
			uad.setReferenceCode(alloc.getCode());
			uad.setStrPaidAmount(String.valueOf(alloc.getAmount()));

			list.add(uad);
		}

		rud.setListAllocationDetails(list);

		FinServiceInstruction fsi = receiptService.buildFinServiceInstruction(rud, entityCode);

		fsi.setReqType("Post");
		fsi.setReceiptUpload(true);
		fsi.setRequestSource(RequestSource.UPLOAD);
		fsi.setLoggedInUser(fc.getUserDetails());
		fsi.setKnockOffReceipt(true);

		FinanceDetail fd = receiptService.receiptTransaction(fsi);

		FinScheduleData schd = fd.getFinScheduleData();
		if (!schd.getErrorDetails().isEmpty()) {
			ErrorDetail error = schd.getErrorDetails().get(0);
			fc.setProgress(EodConstants.PROGRESS_FAILED);
			fc.setErrorCode(error.getCode());
			fc.setErrorDesc(error.getError());
		} else {
			fc.setProgress(EodConstants.PROGRESS_SUCCESS);
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

			manualKnockOffUploadDAO.update(headerIdList, ERR_CODE, ERR_DESC, EodConstants.PROGRESS_FAILED);

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
	public String getSqlQuery() {
		return manualKnockOffUploadDAO.getSqlQuery();
	}

	@Override
	public ProcessRecord getProcessRecord() {
		return manualKnockOffUploadProcessRecord;
	}

	@Autowired
	public void setManualKnockOffUploadDAO(ManualKnockOffUploadDAO manualKnockOffUploadDAO) {
		this.manualKnockOffUploadDAO = manualKnockOffUploadDAO;
	}

	@Autowired
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	@Autowired
	public void setManualKnockOffUploadProcessRecord(
			ManualKnockOffUploadProcessRecord manualKnockOffUploadProcessRecord) {
		this.manualKnockOffUploadProcessRecord = manualKnockOffUploadProcessRecord;
	}

	@Autowired
	public void setReceiptDataValidator(ReceiptDataValidator receiptDataValidator) {
		this.receiptDataValidator = receiptDataValidator;
	}

	@Autowired
	public void setReceiptService(ReceiptService receiptService) {
		this.receiptService = receiptService;
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
