package com.pennant.pff.holdrefund.service.impl;

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
import com.pennant.backend.dao.systemmasters.LovFieldDetailDAO;
import com.pennant.backend.util.UploadConstants;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.holdrefund.dao.HoldRefundUploadDAO;
import com.pennant.pff.holdrefund.model.HoldRefundUploadDetail;
import com.pennant.pff.paymentupload.exception.PaymentUploadError;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.service.impl.AUploadServiceImpl;
import com.pennanttech.pennapps.core.AppException;

public class HoldRefundUploadServiceImpl extends AUploadServiceImpl {
	private static final Logger logger = LogManager.getLogger(HoldRefundUploadServiceImpl.class);

	private HoldRefundUploadDAO holdRefundUploadDAO;
	private FinanceMainDAO financeMainDAO;
	private LovFieldDetailDAO lovFieldDetailDAO;

	@Override
	public void doApprove(List<FileUploadHeader> headers) {
		new Thread(() -> {

			DefaultTransactionDefinition txDef = new DefaultTransactionDefinition(
					TransactionDefinition.PROPAGATION_REQUIRES_NEW);
			TransactionStatus txStatus = null;

			Date appDate = SysParamUtil.getAppDate();

			for (FileUploadHeader header : headers) {
				logger.info("Processing the File {}", header.getFileName());

				List<HoldRefundUploadDetail> details = holdRefundUploadDAO.getDetails(header.getId());

				header.setAppDate(appDate);
				header.setTotalRecords(details.size());
				int sucessRecords = 0;
				int failRecords = 0;

				for (HoldRefundUploadDetail detail : details) {
					doValidate(header, detail);

					if (detail.getProgress() == EodConstants.PROGRESS_FAILED) {
						failRecords++;
					} else {
						sucessRecords++;
						if (holdRefundUploadDAO.isFinIDExists(detail.getReferenceID())) {
							holdRefundUploadDAO.updateFinHoldDetail(detail);
						} else {
							// insertion into maintable for checking hold flag for a loan
							holdRefundUploadDAO.save(detail);
						}
					}
					// insertion for logging purpose
					holdRefundUploadDAO.saveLog(detail, header);
				}

				try {
					txStatus = transactionManager.getTransaction(txDef);

					holdRefundUploadDAO.update(details);

					transactionManager.commit(txStatus);
				} catch (Exception e) {
					logger.error(ERROR_LOG, e.getCause(), e.getMessage(), e.getLocalizedMessage(), e);

					if (txStatus != null) {
						transactionManager.rollback(txStatus);
					}
				} finally {
					txStatus = null;
				}

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
			}

			try {
				txStatus = transactionManager.getTransaction(txDef);

				updateHeader(headers, true);

				logger.info("Hold Refund Process is Completed");

				transactionManager.commit(txStatus);
			} catch (Exception e) {
				logger.error(ERROR_LOG, e.getCause(), e.getMessage(), e.getLocalizedMessage(), e);

				if (txStatus != null) {
					transactionManager.rollback(txStatus);
				}
			} finally {
				txStatus = null;
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

			holdRefundUploadDAO.update(headerIdList, ERR_CODE, ERR_DESC, EodConstants.PROGRESS_FAILED);

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
	public void doValidate(FileUploadHeader header, Object object) {
		HoldRefundUploadDetail detail = null;

		if (object instanceof HoldRefundUploadDetail) {
			detail = (HoldRefundUploadDetail) object;
		}

		if (detail == null) {
			throw new AppException("Invalid Data transferred...");
		}

		logger.info("Validating the Data for the reference {}", detail.getReference());

		detail.setHeaderId(header.getId());

		String reference = detail.getReference();

		Long finID = financeMainDAO.getFinIDByFinReference(reference, "", false);

		if (StringUtils.isBlank(reference)) {
			setError(detail, PaymentUploadError.HOLDUP001);
			return;
		} else if (finID == null) {
			setError(detail, PaymentUploadError.HOLDUP009);
			return;
		}

		String holdStatus = detail.getHoldStatus();
		detail.setReferenceID(finID);

		if (StringUtils.isBlank(holdStatus)) {
			setError(detail, PaymentUploadError.HOLDUP002);
			return;
		}

		boolean isValidFlag = false;
		if (StringUtils.equals(UploadConstants.HOLD_REFUND_FLAG, holdStatus)) {
			isValidFlag = true;
		} else if (StringUtils.equals(UploadConstants.HOLD_REFUND_REMOVAL_FLAG, holdStatus)) {
			isValidFlag = true;
		}

		if (!isValidFlag) {
			setError(detail, PaymentUploadError.HOLDUP0010);
			return;
		}

		String reasonCode = detail.getReason();

		if (StringUtils.isBlank(reasonCode) && StringUtils.equals(UploadConstants.HOLD_REFUND_FLAG, holdStatus)) {
			setError(detail, PaymentUploadError.HOLDUP003);
			return;
		} else if (StringUtils.isNotBlank(reasonCode)
				&& StringUtils.equals(UploadConstants.HOLD_REFUND_REMOVAL_FLAG, holdStatus)) {
			setError(detail, PaymentUploadError.HOLDUP008);
			return;
		}

		String holdFlag = holdRefundUploadDAO.getHoldRefundStatus(finID);

		if (holdFlag == null && StringUtils.equals(UploadConstants.HOLD_REFUND_REMOVAL_FLAG, holdStatus)) {
			setError(detail, PaymentUploadError.HOLDUP005);
			return;
		}

		if (StringUtils.equals(holdFlag, detail.getHoldStatus())) {
			setError(detail, PaymentUploadError.HOLDUP004);
			return;
		}

		if (!lovFieldDetailDAO.isfieldCodeValueExists(detail.getReason(), true)) {
			setError(detail, PaymentUploadError.HOLDUP007);
			return;
		}

		detail.setProgress(EodConstants.PROGRESS_SUCCESS);
		detail.setErrorCode("");
		detail.setErrorDesc("");
	}

	@Override
	public String getSqlQuery() {
		return holdRefundUploadDAO.getSqlQuery();
	}

	private void setError(HoldRefundUploadDetail detail, PaymentUploadError error) {
		detail.setProgress(EodConstants.PROGRESS_FAILED);
		detail.setErrorCode(error.name());
		detail.setErrorDesc(error.description());
	}

	@Autowired
	public void setHoldRefundUploadDAO(HoldRefundUploadDAO holdRefundUploadDAO) {
		this.holdRefundUploadDAO = holdRefundUploadDAO;
	}

	@Autowired
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	@Autowired
	public void setLovFieldDetailDAO(LovFieldDetailDAO lovFieldDetailDAO) {
		this.lovFieldDetailDAO = lovFieldDetailDAO;
	}

}
