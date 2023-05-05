package com.pennant.pff.blockautolettergenerate.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.model.blockautolettergenerate.upload.BlockAutoGenLetterUpload;
import com.pennant.backend.util.LimitConstants;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.blockautolettergenerate.dao.BlockAutoGenLetterUploadDAO;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.service.impl.AUploadServiceImpl;
import com.pennanttech.pennapps.core.AppException;

public class BlockAutoGenLetterUploadServiceImpl extends AUploadServiceImpl {
	private static final Logger logger = LogManager.getLogger(BlockAutoGenLetterUploadServiceImpl.class);

	public BlockAutoGenLetterUploadServiceImpl() {
		super();
	}

	private BlockAutoGenLetterUploadDAO blockAutoGenLetterUploadDAO;
	private BlockAutoGenLetterUploadValidateRecord blockAutoGenLetterUploadValidateRecord;
	private FinanceMainDAO financeMainDAO;

	@Override
	public void doValidate(FileUploadHeader header, Object object) {
		BlockAutoGenLetterUpload detail = null;

		if (object instanceof BlockAutoGenLetterUpload) {
			detail = (BlockAutoGenLetterUpload) object;
		}

		if (detail == null) {
			throw new AppException("Invalid Data transferred...");
		}

		String reference = detail.getReference();

		logger.info("Validating the Data for the reference {}", reference);

		detail.setHeaderId(header.getId());

		if (StringUtils.isNotBlank(reference)) {
			Long finID = financeMainDAO.getFinID(reference);

			if (finID == null) {
				setError(detail, BlockAutoGenLetterUploadError.BALG_01);
				return;
			}

			detail.setReferenceID(finID);
		}

		String action = detail.getAction();
		if (StringUtils.isNotBlank(action)) {
			validateAction(detail, action);
		}

		if (EodConstants.PROGRESS_FAILED == detail.getProgress()) {
			return;
		}
	}

	private void validateAction(BlockAutoGenLetterUpload detail, String action) {
		if (!(LimitConstants.BLOCK.equals(action) || LimitConstants.UNBLOCK.equals(action))) {
			setError(detail, BlockAutoGenLetterUploadError.BALG_02);
			return;
		}

		if (blockAutoGenLetterUploadDAO.isValidateAction(detail.getReference(), action,
				EodConstants.PROGRESS_SUCCESS)) {
			setError(detail, BlockAutoGenLetterUploadError.BALG_03);
			return;
		}

		if (LimitConstants.UNBLOCK.equals(action) && !blockAutoGenLetterUploadDAO
				.isValidateAction(detail.getReference(), LimitConstants.BLOCK, EodConstants.PROGRESS_SUCCESS)) {
			setError(detail, BlockAutoGenLetterUploadError.BALG_04);
			return;
		}
	}

	@Override
	public void doApprove(List<FileUploadHeader> headers) {
		new Thread(() -> {

			DefaultTransactionDefinition txDef = new DefaultTransactionDefinition(
					TransactionDefinition.PROPAGATION_REQUIRES_NEW);
			TransactionStatus txStatus = null;

			Date appDate = SysParamUtil.getAppDate();

			for (FileUploadHeader header : headers) {
				logger.info("Processing the File {}", header.getFileName());

				List<BlockAutoGenLetterUpload> details = blockAutoGenLetterUploadDAO.getDetails(header.getId());

				header.setAppDate(appDate);
				header.setTotalRecords(details.size());

				int sucessRecords = 0;
				int failRecords = 0;

				for (BlockAutoGenLetterUpload detail : details) {
					detail.setCreatedOn(header.getCreatedOn());
					detail.setCreatedBy(header.getCreatedBy());
					detail.setApprovedOn(header.getLastMntOn());
					detail.setApprovedBy(header.getLastMntBy());

					doValidate(header, detail);

					if (detail.getErrorCode() != null) {
						detail.setProgress(EodConstants.PROGRESS_FAILED);
					} else {
						detail.setProgress(EodConstants.PROGRESS_SUCCESS);
						detail.setErrorCode("");
						detail.setErrorDesc("");
						int count = blockAutoGenLetterUploadDAO.getReference(detail.getReference(),
								EodConstants.PROGRESS_SUCCESS);
						if (count > 0) {
							blockAutoGenLetterUploadDAO.delete(detail.getReference(), EodConstants.PROGRESS_SUCCESS);
						}

						blockAutoGenLetterUploadDAO.save(detail);
					}

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

					blockAutoGenLetterUploadDAO.update(details);

					List<FileUploadHeader> headerList = new ArrayList<>();
					headerList.add(header);
					updateHeader(headers, true);

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

		}).start();
	}

	@Override
	public void doReject(List<FileUploadHeader> headers) {
		List<Long> headerIdList = headers.stream().map(FileUploadHeader::getId).collect(Collectors.toList());

		DefaultTransactionDefinition txDef = new DefaultTransactionDefinition(
				TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		TransactionStatus txStatus = null;
		try {
			txStatus = transactionManager.getTransaction(txDef);

			blockAutoGenLetterUploadDAO.update(headerIdList, ERR_CODE, ERR_DESC, EodConstants.PROGRESS_FAILED);

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
		return blockAutoGenLetterUploadDAO.getSqlQuery();
	}

	private void setError(BlockAutoGenLetterUpload detail, BlockAutoGenLetterUploadError error) {
		detail.setProgress(EodConstants.PROGRESS_FAILED);
		detail.setErrorCode(error.name());
		detail.setErrorDesc(error.description());
	}

	@Autowired
	public void setBlockAutoLetterGenerateUploadDAO(BlockAutoGenLetterUploadDAO blockAutoLetterGenerateUploadDAO) {
		this.blockAutoGenLetterUploadDAO = blockAutoLetterGenerateUploadDAO;
	}

	@Override
	public BlockAutoGenLetterUploadValidateRecord getValidateRecord() {
		return blockAutoGenLetterUploadValidateRecord;
	}

	@Autowired
	public void setBlockAutoLetterGenerateUploadValidateRecord(
			BlockAutoGenLetterUploadValidateRecord blockAutoLetterGenerateUploadValidateRecord) {
		this.blockAutoGenLetterUploadValidateRecord = blockAutoLetterGenerateUploadValidateRecord;
	}

	@Autowired
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}
}
