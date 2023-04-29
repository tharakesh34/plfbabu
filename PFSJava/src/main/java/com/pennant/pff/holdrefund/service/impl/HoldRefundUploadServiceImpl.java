package com.pennant.pff.holdrefund.service.impl;

import java.util.Date;
import java.util.List;
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
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.systemmasters.LovFieldDetailDAO;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.util.UploadConstants;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.holdrefund.dao.HoldRefundUploadDAO;
import com.pennant.pff.holdrefund.model.HoldRefundUploadDetail;
import com.pennant.pff.paymentupload.exception.PaymentUploadError;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.service.impl.AUploadServiceImpl;
import com.pennanttech.dataengine.model.DataEngineAttributes;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.file.UploadTypes;
import com.pennapps.core.util.ObjectUtil;

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

		String reference = detail.getReference();

		logger.info("Validating the Data for the reference {}", reference);

		detail.setHeaderId(header.getId());

		if (StringUtils.isBlank(reference)) {
			setError(detail, PaymentUploadError.HOLDUP001);
			return;
		}

		Long finID = financeMainDAO.getFinID(reference);

		if (StringUtils.isBlank(reference)) {
			setError(detail, PaymentUploadError.HOLDUP001);
			return;
		} else if (finID == null) {
			setError(detail, PaymentUploadError.HOLDUP009);
			return;
		}

		FinanceMain fm = financeMainDAO.getFinanceMain(reference, header.getEntityCode());

		if (fm == null) {
			setError(detail, PaymentUploadError.HOLDUP009);
			return;
		}

		if (!fm.isFinIsActive()) {
			setError(detail, PaymentUploadError.HOLDUP0011);
			return;
		}

		String holdStatus = detail.getHoldStatus();
		detail.setReferenceID(finID);

		if (StringUtils.isBlank(holdStatus)) {
			setError(detail, PaymentUploadError.HOLDUP002);
			return;
		}

		boolean holdRefund = UploadConstants.REFUND_HOLD.equals(holdStatus);
		boolean releaseRefund = UploadConstants.REFUND_RELEASE.equals(holdStatus);

		if (!(holdRefund || releaseRefund)) {
			setError(detail, PaymentUploadError.HOLDUP0010);
			return;
		}

		String reasonCode = detail.getReason();

		if (holdRefund && StringUtils.isBlank(reasonCode)) {
			setError(detail, PaymentUploadError.HOLDUP003);
			return;
		}

		if (releaseRefund && StringUtils.isNotBlank(reasonCode)) {
			setError(detail, PaymentUploadError.HOLDUP008);
			return;
		}

		String refundStatus = holdRefundUploadDAO.getHoldRefundStatus(finID);

		if (refundStatus == null && releaseRefund) {
			setError(detail, PaymentUploadError.HOLDUP005);
			return;
		}

		if (UploadConstants.REFUND_RELEASE.equals(refundStatus) && !holdRefund) {
			setError(detail, PaymentUploadError.HOLDUP005);
			return;
		}

		if (UploadConstants.REFUND_HOLD.equals(refundStatus) && !releaseRefund) {
			setError(detail, PaymentUploadError.HOLDUP004);
			return;
		}

		if (!lovFieldDetailDAO.isfieldCodeValueExists(detail.getReason(), true) && holdRefund) {
			setError(detail, PaymentUploadError.HOLDUP007);
			return;
		}

		detail.setProgress(EodConstants.PROGRESS_SUCCESS);
		detail.setErrorCode("");
		detail.setErrorDesc("");
	}

	public void uploadProcess() {
		uploadProcess(UploadTypes.HOLD_REFUND.name(), this, "HoldRefundUploadHeader");
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

	@Override
	public boolean isInProgress(Long headerID, Object... args) {
		return holdRefundUploadDAO.isInProgress((String) args[0], headerID);
	}

	@Override
	public void validate(DataEngineAttributes attributes, MapSqlParameterSource record) throws Exception {
		logger.debug(Literal.ENTERING);

		Long headerID = ObjectUtil.valueAsLong(attributes.getParameterMap().get("HEADER_ID"));

		if (headerID == null) {
			return;
		}

		FileUploadHeader header = (FileUploadHeader) attributes.getParameterMap().get("FILE_UPLOAD_HEADER");

		String finReference = ObjectUtil.valueAsString(record.getValue("finReference"));
		boolean recordExist = isInProgress(headerID, finReference);

		if (recordExist) {
			throw new AppException("Record is already initiated, unable to proceed.");
		}

		HoldRefundUploadDetail detail = new HoldRefundUploadDetail();
		detail.setHeaderId(headerID);
		detail.setReference(finReference);
		detail.setHoldStatus(ObjectUtil.valueAsString(record.getValue("holdStatus")));
		detail.setReason(ObjectUtil.valueAsString(record.getValue("reason")));
		detail.setRemarks(ObjectUtil.valueAsString(record.getValue("remarks")));

		doValidate(header, detail);

		updateProcess(header, detail, record);
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
