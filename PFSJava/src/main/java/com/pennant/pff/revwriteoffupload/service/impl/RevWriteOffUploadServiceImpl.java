package com.pennant.pff.revwriteoffupload.service.impl;

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
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.pennant.app.util.AEAmounts;
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.feetype.FeeTypeDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.FinanceWriteoffDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.dao.receipts.ReceiptAllocationDetailDAO;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.ManualAdviseMovements;
import com.pennant.backend.model.finance.ReceiptAllocationDetail;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.service.finance.impl.ManualAdviceUtil;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.cache.util.AccountingConfigCache;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.revwriteoffupload.dao.RevWriteOffUploadDAO;
import com.pennant.pff.revwriteoffupload.model.RevWriteOffUploadDetail;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.service.impl.AUploadServiceImpl;
import com.pennant.pff.writeoffupload.exception.WriteOffUploadError;
import com.pennanttech.dataengine.ValidateRecord;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.file.UploadTypes;
import com.pennanttech.pff.receipt.constants.Allocation;

public class RevWriteOffUploadServiceImpl extends AUploadServiceImpl {
	private static final Logger logger = LogManager.getLogger(RevWriteOffUploadServiceImpl.class);

	private RevWriteOffUploadDAO revWriteOffUploadDAO;
	private FinanceMainDAO financeMainDAO;
	private ValidateRecord revWriteOffUploadValidateRecord;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private FinanceProfitDetailDAO profitDetailsDAO;
	private FinanceWriteoffDAO financeWriteoffDAO;
	private ReceiptAllocationDetailDAO receiptAllocationDetailDAO;
	private ManualAdviseDAO manualAdviseDAO;
	private PostingsPreparationUtil postingsPreparationUtil;
	private FeeTypeDAO feeTypeDAO;

	@Override
	public void doApprove(List<FileUploadHeader> headers) {
		new Thread(() -> {

			DefaultTransactionDefinition txDef = new DefaultTransactionDefinition(
					TransactionDefinition.PROPAGATION_REQUIRES_NEW);
			TransactionStatus txStatus = null;

			Date appDate = SysParamUtil.getAppDate();

			for (FileUploadHeader header : headers) {
				logger.info("Processing the File {}", header.getFileName());

				List<RevWriteOffUploadDetail> details = revWriteOffUploadDAO.getDetails(header.getId());

				header.setAppDate(appDate);
				header.setTotalRecords(details.size());
				int sucessRecords = 0;
				int failRecords = 0;

				List<String> key = new ArrayList<>();

				for (RevWriteOffUploadDetail detail : details) {
					String reference = detail.getReference();
					detail.setAppDate(appDate);
					if (key.contains(reference)) {
						setError(detail, WriteOffUploadError.WOUP009);
						failRecords++;
						continue;
					}

					key.add(reference);

					doValidate(header, detail);

					if (detail.getProgress() == EodConstants.PROGRESS_FAILED) {
						failRecords++;
					} else {
						sucessRecords++;
					}
				}

				try {
					header.setSuccessRecords(sucessRecords);
					header.setFailureRecords(failRecords);

					StringBuilder remarks = new StringBuilder("Process Completed");

					if (failRecords > 0) {
						remarks.append(" with exceptions, ");
					}

					remarks.append(" Total Records : ").append(header.getTotalRecords());
					remarks.append(" Success Records : ").append(sucessRecords);
					remarks.append(" Failed Records : ").append(failRecords);

					logger.info("Processed the File {}", header.getFileName());

					txStatus = transactionManager.getTransaction(txDef);

					revWriteOffUploadDAO.update(details);

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

				logger.info("Reverse WriteOff Upload Process is Initiated for the Header ID {}", header.getId());

				processRevWriteOffLoan(header, details);

				logger.info("Reverse WriteOff Upload Process is Completed for the Header ID {}", header.getId());
			}
		}).start();

	}

	/**
	 * Method for processing Success Writeoff Records for Writeoff reversal Execution
	 * 
	 * @param header
	 */
	private void processRevWriteOffLoan(FileUploadHeader header, List<RevWriteOffUploadDetail> details) {
		for (RevWriteOffUploadDetail detail : details) {

			if (detail.getProgress() != EodConstants.PROGRESS_SUCCESS) {
				continue;
			}

			DefaultTransactionDefinition txDef = new DefaultTransactionDefinition(
					TransactionDefinition.PROPAGATION_REQUIRES_NEW);

			TransactionStatus transactionStatus = this.transactionManager.getTransaction(txDef);

			try {
				long finId = detail.getReferenceID();

				if (!executeAccountingProcess(detail, finId)) {
					throw new AppException("Postings Execution failed for Loan Reference : " + detail.getReference());
				}

				updateWriteOffStatus(finId);

				saveLog(detail, header);

				this.transactionManager.commit(transactionStatus);
			} catch (Exception e) {
				logger.error(ERROR_LOG, e.getCause(), e.getMessage(), e.getLocalizedMessage(), e);

				transactionManager.rollback(transactionStatus);

				String error = StringUtils.trimToEmpty(e.getMessage());

				if (error.length() > 1999) {
					error = error.substring(0, 1999);
				}

				detail.setProgress(EodConstants.PROGRESS_FAILED);
				detail.setErrorDesc(error);
				this.revWriteOffUploadDAO.update(detail);
			}

			if (EodConstants.PROGRESS_FAILED == detail.getProgress()) {
				updateFailRecords(1, 1, detail.getHeaderId());
			}
		}
	}

	/**
	 * Method for preparing Accounting and executing against Writeoff Loan amount reversals to Normal state
	 * 
	 * @param detail
	 * @param finId
	 */
	private boolean executeAccountingProcess(RevWriteOffUploadDetail detail, long finId) {
		String reference = detail.getReference();

		FinanceMain fm = financeMainDAO.getFinanceMainById(finId, "", false);

		List<FinanceScheduleDetail> schedules = financeScheduleDetailDAO.getFinScheduleDetails(finId, "", false);
		FinanceProfitDetail fpd = profitDetailsDAO.getFinProfitDetailsById(finId);

		AEEvent aeEvent = new AEEvent();
		aeEvent.setAppDate(detail.getAppDate());
		aeEvent.setEntityCode(fm.getEntityCode());

		aeEvent = AEAmounts.procAEAmounts(fm, schedules, fpd, AccountingEvent.REV_WRITEOFF, aeEvent.getAppDate(),
				aeEvent.getAppDate());

		aeEvent.getAcSetIDList().add(AccountingConfigCache.getAccountSetID(fm.getFinType(),
				AccountingEvent.REV_WRITEOFF, FinanceConstants.MODULEID_FINTYPE));

		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();

		long receiptId = revWriteOffUploadDAO.getReceiptIdByRef(reference);

		Map<String, Object> dataMap = aeEvent.getDataMap();
		dataMap.putAll(preparePaidAmount(reference, receiptId));
		dataMap.putAll(prepareAdviseMovements(reference, receiptId));

		dataMap = amountCodes.getDeclaredFieldValues(dataMap);
		aeEvent.setDataMap(dataMap);

		aeEvent = postingsPreparationUtil.postAccounting(aeEvent);
		return aeEvent.isPostingSucess();
	}

	private Map<String, BigDecimal> prepareAdviseMovements(String reference, long receiptId) {
		List<ManualAdviseMovements> mamList = ManualAdviceUtil
				.getMovements(manualAdviseDAO.getAdvisePaidAmount(receiptId, reference));

		String bounceComponent = feeTypeDAO.getTaxComponent(Allocation.BOUNCE);

		return ManualAdviceUtil.prepareMovementMap(mamList, bounceComponent);
	}

	private Map<String, BigDecimal> preparePaidAmount(String reference, long receiptId) {
		List<ReceiptAllocationDetail> radList = receiptAllocationDetailDAO.getReceiptPaidAmount(receiptId, reference);

		Map<String, BigDecimal> alocMap = new HashMap<>();

		for (ReceiptAllocationDetail rad : radList) {
			String allocType = rad.getAllocationType();
			switch (allocType) {
			case Allocation.PRI:
				alocMap.put("ae_priPaidWO", rad.getPaidAmount());
				break;
			case Allocation.PFT:
				alocMap.put("ae_pftPaidWO", rad.getPaidAmount());
				break;
			default:
				break;
			}
		}
		return alocMap;
	}

	/**
	 * Method for Updating write off Amount Details & status against Loan
	 * 
	 * @param detail
	 * @param finId
	 */
	private void updateWriteOffStatus(long finId) {
		financeScheduleDetailDAO.updateWriteOffDetail(finId);
		financeMainDAO.updateWriteOffStatus(finId, false);
		profitDetailsDAO.updateClosingSts(finId, false);

		revWriteOffUploadDAO.save(financeWriteoffDAO.getFinanceWriteoffById(finId, ""), "");
		financeWriteoffDAO.delete(finId, "");
	}

	@Override
	public void doReject(List<FileUploadHeader> headers) {
		List<Long> headerIdList = headers.stream().map(FileUploadHeader::getId).collect(Collectors.toList());

		DefaultTransactionDefinition txDef = new DefaultTransactionDefinition(
				TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		TransactionStatus txStatus = null;
		try {
			txStatus = transactionManager.getTransaction(txDef);

			revWriteOffUploadDAO.update(headerIdList, ERR_CODE, ERR_DESC, EodConstants.PROGRESS_FAILED);

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

	private void saveLog(RevWriteOffUploadDetail detail, FileUploadHeader header) {
		detail.setEvent(UploadTypes.REV_WRITE_OFF.name());

		revWriteOffUploadDAO.saveLog(detail, header);
	}

	@Override
	public void doValidate(FileUploadHeader header, Object object) {
		RevWriteOffUploadDetail detail = null;

		if (object instanceof RevWriteOffUploadDetail) {
			detail = (RevWriteOffUploadDetail) object;
		}

		if (detail == null) {
			throw new AppException("Invalid Data transferred...");
		}

		detail.setHeaderId(header.getId());

		String reference = detail.getReference();

		logger.info("Validating the Data for the reference {}", reference);

		if (StringUtils.isBlank(reference)) {
			setError(detail, WriteOffUploadError.WOUP001);
			return;
		}

		FinanceMain fm = financeMainDAO.getFinanceMain(reference, header.getEntityCode());

		if (fm == null) {
			setError(detail, WriteOffUploadError.WOUP002);
			return;
		}

		if (!fm.isFinIsActive()) {
			setError(detail, WriteOffUploadError.WOUP0011);
			return;
		}

		if (!fm.isWriteoffLoan()) {
			setError(detail, WriteOffUploadError.WOUP0012);
			return;
		}

		detail.setReferenceID(fm.getFinID());

		detail.setProgress(EodConstants.PROGRESS_SUCCESS);
		detail.setErrorCode("");
		detail.setErrorDesc("");

		logger.info("Validated the Data for the reference {}", reference);
	}

	@Override
	public String getSqlQuery() {
		return revWriteOffUploadDAO.getSqlQuery();
	}

	private void setError(RevWriteOffUploadDetail detail, WriteOffUploadError error) {
		detail.setProgress(EodConstants.PROGRESS_FAILED);
		detail.setErrorCode(error.name());
		detail.setErrorDesc(error.description());
	}

	@Override
	public boolean isInProgress(Long headerID, Object... args) {
		return revWriteOffUploadDAO.isInProgress((String) args[0], headerID);
	}

	@Override
	public ValidateRecord getValidateRecord() {
		return revWriteOffUploadValidateRecord;
	}

	@Autowired
	public void setRevWriteOffUploadDAO(RevWriteOffUploadDAO revWriteOffUploadDAO) {
		this.revWriteOffUploadDAO = revWriteOffUploadDAO;
	}

	@Autowired
	public void setRevWriteOffUploadValidateRecord(ValidateRecord revWriteOffUploadValidateRecord) {
		this.revWriteOffUploadValidateRecord = revWriteOffUploadValidateRecord;
	}

	@Autowired
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	@Autowired
	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	@Autowired
	public void setProfitDetailsDAO(FinanceProfitDetailDAO profitDetailsDAO) {
		this.profitDetailsDAO = profitDetailsDAO;
	}

	@Autowired
	public void setFinanceWriteoffDAO(FinanceWriteoffDAO financeWriteoffDAO) {
		this.financeWriteoffDAO = financeWriteoffDAO;
	}

	@Autowired
	public void setReceiptAllocationDetailDAO(ReceiptAllocationDetailDAO receiptAllocationDetailDAO) {
		this.receiptAllocationDetailDAO = receiptAllocationDetailDAO;
	}

	@Autowired
	public void setManualAdviseDAO(ManualAdviseDAO manualAdviseDAO) {
		this.manualAdviseDAO = manualAdviseDAO;
	}

	@Autowired
	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}

	@Autowired
	public void setFeeTypeDAO(FeeTypeDAO feeTypeDAO) {
		this.feeTypeDAO = feeTypeDAO;
	}

}