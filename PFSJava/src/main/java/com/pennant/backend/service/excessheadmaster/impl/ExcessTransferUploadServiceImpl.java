package com.pennant.backend.service.excessheadmaster.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.model.excessheadmaster.ExcessTransferUpload;
import com.pennant.backend.model.excessheadmaster.FinExcessTransfer;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.presentment.dao.ExcessTransferUploadDAO;
import com.pennant.pff.presentment.exception.PresentmentError;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.service.impl.AUploadServiceImpl;
import com.pennanttech.pennapps.core.AppException;

public class ExcessTransferUploadServiceImpl<getBalanceAmt> extends AUploadServiceImpl {
	private static final Logger logger = LogManager.getLogger(ExcessTransferUploadServiceImpl.class);

	private ExcessTransferUploadDAO excessTransferUploadDAO;
	private FinanceMainDAO financeMainDAO;
	private FinExcessAmountDAO finExcessAmountDAO;

	@Override
	public void doValidate(FileUploadHeader header, Object object) {
		ExcessTransferUpload detail = null;
		FinExcessAmount fem = null;

		if (object instanceof ExcessTransferUpload) {
			detail = (ExcessTransferUpload) object;
		}

		if (detail == null) {
			throw new AppException("Invalid Data transferred...");
		}

		String reference = detail.getReference();

		logger.info("Validating the Data for the reference {}", reference);

		if (StringUtils.isBlank(reference)) {
			setError(detail, PresentmentError.REPRMNT513);
			return;
		}

		FinanceMain fm = financeMainDAO.getFinanceMain(reference, header.getEntityCode());

		if (fm == null) {
			setError(detail, PresentmentError.REPRMNT514);
			return;
		}

		if (!fm.isFinIsActive()) {
			setError(detail, PresentmentError.REPRMNT515);
			return;
		}

		if (excessTransferUploadDAO.isDuplicateExists(reference, detail.getTransferFromType(), detail.getHeaderId())) {
			setError(detail, PresentmentError.EX_608);
			return;
		}

		BigDecimal balanceAmount = excessTransferUploadDAO.getBalanceAmount(fm.getFinID(),
				detail.getTransferFromType());

		if (balanceAmount.compareTo(detail.getTransferAmount()) < 0) {
			setError(detail, PresentmentError.EX_607);
			return;
		}

		detail.setReferenceID(fm.getFinID());
		detail.setProgress(EodConstants.PROGRESS_SUCCESS);
		detail.setErrorCode("");
		detail.setErrorDesc("");
	}

	private void setError(ExcessTransferUpload detail, PresentmentError error) {
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

				List<ExcessTransferUpload> details = excessTransferUploadDAO.getDetails(header.getId());

				header.setTotalRecords(details.size());
				int sucessRecords = 0;
				int failRecords = 0;

				for (ExcessTransferUpload fc : details) {
					doValidate(header, fc);

					if (fc.getProgress() == EodConstants.PROGRESS_FAILED) {
						failRecords++;
					} else {
						sucessRecords++;
					}
				}

				try {
					txStatus = transactionManager.getTransaction(txDef);

					excessTransferUploadDAO.update(details);

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

					logger.info("Excess Transfer Process is Initiated");

					// Process needs to be implement
					process(header);
					FinExcessTransfer finExcessTransfer = new FinExcessTransfer();
					for (ExcessTransferUpload exc : details) {
						FinExcessAmount excess = new FinExcessAmount();
						excess.setFinID(finExcessTransfer.getFinId());
						excess.setFinReference(finExcessTransfer.getFinReference());
						excess.setAmountType(finExcessTransfer.getTransferToType());
						excess.setAmount(finExcessTransfer.getTransferAmount());
						excess.setUtilisedAmt(BigDecimal.ZERO);
						excess.setBalanceAmt(finExcessTransfer.getTransferAmount());
						excess.setReservedAmt(BigDecimal.ZERO);
						excess.setReceiptID(finExcessTransfer.getId());
					}

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

	private void process(FileUploadHeader header) {
		long headerID = Long.MAX_VALUE;
		String status = "";
		excessTransferUploadDAO.getRecords(headerID, status);
	}

	@Override
	public void doReject(List<FileUploadHeader> headers) {
		List<Long> headerIdList = headers.stream().map(FileUploadHeader::getId).collect(Collectors.toList());

		DefaultTransactionDefinition txDef = new DefaultTransactionDefinition(
				TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		TransactionStatus txStatus = null;

		try {
			txStatus = transactionManager.getTransaction(txDef);

			excessTransferUploadDAO.update(headerIdList, ERR_CODE, ERR_DESC, EodConstants.PROGRESS_FAILED);

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
		return excessTransferUploadDAO.getSqlQuery();
	}

	@Autowired
	public void setExcessTransferUploadDAO(ExcessTransferUploadDAO excessTransferUploadDAO) {
		this.excessTransferUploadDAO = excessTransferUploadDAO;
	}

	@Autowired
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public FinExcessAmountDAO getFinExcessAmountDAO() {
		return finExcessAmountDAO;
	}

	public void setFinExcessAmountDAO(FinExcessAmountDAO finExcessAmountDAO) {
		this.finExcessAmountDAO = finExcessAmountDAO;
	}
}
