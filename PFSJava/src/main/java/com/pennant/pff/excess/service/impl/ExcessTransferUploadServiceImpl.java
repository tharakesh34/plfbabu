package com.pennant.pff.excess.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.pennant.app.constants.AccountConstants;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinExcessMovement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.excess.ExcessHead;
import com.pennant.pff.excess.ExcessTransferError;
import com.pennant.pff.excess.dao.ExcessTransferUploadDAO;
import com.pennant.pff.excess.model.ExcessTransferUpload;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.service.impl.AUploadServiceImpl;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.util.DateUtil;

public class ExcessTransferUploadServiceImpl extends AUploadServiceImpl {
	private static final Logger logger = LogManager.getLogger(ExcessTransferUploadServiceImpl.class);

	private ExcessTransferUploadDAO excessTransferUploadDAO;
	private FinanceMainDAO financeMainDAO;
	private FinExcessAmountDAO finExcessAmountDAO;

	@Override
	public void doValidate(FileUploadHeader header, Object object) {
		ExcessTransferUpload detail = null;

		if (object instanceof ExcessTransferUpload) {
			detail = (ExcessTransferUpload) object;
		}

		if (detail == null) {
			throw new AppException("Invalid Data transferred...");
		}

		String reference = detail.getReference();

		logger.info("Validating the Data for the reference {}", reference);

		if (StringUtils.isBlank(reference)) {
			setError(detail, ExcessTransferError.EXT_001);
			return;
		}

		FinanceMain fm = financeMainDAO.getFinanceMain(reference, header.getEntityCode());

		if (fm == null) {
			setError(detail, ExcessTransferError.EXT_002);
			return;
		}

		if (!fm.isFinIsActive()) {
			setError(detail, ExcessTransferError.EXT_003);
			return;
		}

		detail.setReference(reference);
		detail.setReferenceID(fm.getFinID());

		String transferFrom = detail.getTransferFromType();
		String transferToType = detail.getTransferToType();

		if (!ExcessHead.isValidExcessTransferHead(transferFrom)) {
			setError(detail, ExcessTransferError.EXT_006);
		}

		if (!ExcessHead.isValidExcessTransferHead(transferToType)) {
			setError(detail, ExcessTransferError.EXT_006);
		}

		if (transferFrom.equals(transferToType)) {
			setError(detail, ExcessTransferError.EXT_006);
		}

		if (excessTransferUploadDAO.isDuplicateExists(reference, transferFrom, detail.getHeaderId())) {
			setError(detail, ExcessTransferError.EXT_005);
			return;
		}

		detail.setTransferAmount(detail.getTransferAmount().multiply(new BigDecimal(100)));

		BigDecimal balanceAmount = excessTransferUploadDAO.getBalanceAmount(fm.getFinID(), transferFrom);

		if (balanceAmount.compareTo(detail.getTransferAmount()) < 0) {
			setError(detail, ExcessTransferError.EXT_004);
			return;
		}
		detail.setReferenceID(fm.getFinID());
		detail.setProgress(EodConstants.PROGRESS_SUCCESS);
		detail.setErrorCode("");
		detail.setErrorDesc("");
	}

	private void setError(ExcessTransferUpload detail, ExcessTransferError error) {
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
					process(header.getId());

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

	private void process(long headerID) {
		List<FinExcessAmount> amount = new ArrayList<>();
		List<ExcessTransferUpload> process = excessTransferUploadDAO.getProcess(headerID);

		Date appDate = SysParamUtil.getAppDate();
		for (ExcessTransferUpload exc : process) {
			FinExcessAmount excessamount = new FinExcessAmount();

			excessamount.setFinID(exc.getReferenceID());
			excessamount.setFinReference(exc.getReference());
			excessamount.setAmountType(exc.getTransferToType());
			excessamount.setAmount(exc.getTransferAmount());
			excessamount.setUtilisedAmt(BigDecimal.ZERO);
			excessamount.setBalanceAmt(exc.getTransferAmount());
			excessamount.setReservedAmt(BigDecimal.ZERO);
			excessamount.setReceiptID(exc.getId());
			excessamount.setValueDate(appDate);
			excessamount.setPostDate(appDate);

			List<FinExcessAmount> existingExcess = finExcessAmountDAO.getExcessAmountsByRefAndType(exc.getReferenceID(),
					exc.getTransferFromType());

			List<FinExcessAmount> excessList = existingExcess.stream()
					.sorted((l1, l2) -> DateUtil.compare(l1.getValueDate(), l2.getValueDate()))
					.collect(Collectors.toList());

			BigDecimal transferAmount = exc.getTransferAmount();
			for (FinExcessAmount excess : excessList) {
				if (transferAmount.compareTo(BigDecimal.ZERO) <= 0) {
					break;
				}

				BigDecimal available = excess.getBalanceAmt();

				if (transferAmount.compareTo(available) >= 0) {
					excess.setUtilisedAmt(available);
					transferAmount = transferAmount.subtract(available);
				} else {
					excess.setUtilisedAmt(transferAmount);
					transferAmount = BigDecimal.ZERO;
				}

				finExcessAmountDAO.updateUtiliseOnly(excess.getExcessID(), excess.getUtilisedAmt());
				FinExcessMovement movement = new FinExcessMovement();
				movement.setExcessID(excess.getExcessID());
				movement.setReceiptID(exc.getId());
				movement.setMovementType(RepayConstants.RECEIPTTYPE_TRANSFER);
				movement.setTranType(AccountConstants.TRANTYPE_DEBIT);
				movement.setAmount(excess.getUtilisedAmt());
				finExcessAmountDAO.saveExcessMovement(movement);
			}

			amount.add(excessamount);

			if (amount.size() == PennantConstants.CHUNK_SIZE) {
				finExcessAmountDAO.saveExcessList(amount);
				amount.clear();
			}
		}

		if (CollectionUtils.isNotEmpty(amount)) {
			finExcessAmountDAO.saveExcessList(amount);
		}

		List<FinExcessMovement> movementList = new ArrayList<>();

		for (FinExcessAmount fea : amount) {
			FinExcessMovement toMovement = new FinExcessMovement();
			toMovement.setExcessID(fea.getId());
			toMovement.setReceiptID(fea.getReceiptID());
			toMovement.setMovementType(RepayConstants.RECEIPTTYPE_TRANSFER);
			toMovement.setTranType(AccountConstants.TRANTYPE_CREDIT);
			toMovement.setAmount(fea.getAmount());

			movementList.add(toMovement);

			if (movementList.size() == PennantConstants.CHUNK_SIZE) {
				finExcessAmountDAO.saveExcessMovementList(movementList);
				movementList.clear();
			}

		}

		if (CollectionUtils.isNotEmpty(movementList)) {
			finExcessAmountDAO.saveExcessMovementList(movementList);
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

	@Autowired
	public void setFinExcessAmountDAO(FinExcessAmountDAO finExcessAmountDAO) {
		this.finExcessAmountDAO = finExcessAmountDAO;
	}
}
