package com.pennant.pff.courierdetailupload.service.impl;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.courierdetailupload.dao.CourierDetailUploadDAO;
import com.pennant.pff.courierdetailupload.service.error.CourierDetailError;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.service.impl.AUploadServiceImpl;
import com.pennanttech.dataengine.ValidateRecord;
import com.pennanttech.model.courierdetailsupload.CourierDetailUpload;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;

public class CourierDetailUploadServiceImpl extends AUploadServiceImpl {
	private static final Logger logger = LogManager.getLogger(CourierDetailUploadServiceImpl.class);

	public CourierDetailUploadServiceImpl() {
		super();
	}

	private FinanceMainDAO financeMainDAO;
	private CourierDetailUploadDAO courierDetailUploadDAO;
	private ValidateRecord courierDetailUploadValidateRecord;

	@Override
	public void doValidate(FileUploadHeader header, Object object) {
		CourierDetailUpload detail = null;

		if (object instanceof CourierDetailUpload) {
			detail = (CourierDetailUpload) object;
		}

		if (detail == null) {
			throw new AppException("Invalid Data transferred...");
		}

		String letterType = detail.getLetterType();
		String reference = detail.getReference();
		String deliveryStatus = detail.getDeliveryStatus();
		FinanceMain fm = financeMainDAO.getFinanceMain(reference, header.getEntityCode());

		if (fm == null) {
			setError(detail, CourierDetailError.LCD_001);
			return;
		}

		if (!fm.isFinIsActive()) {
			setError(detail, CourierDetailError.LCD_002);
			return;
		}

		if (!isValidLetterType(letterType)) {
			setError(detail, CourierDetailError.LCD_003);
			return;
		}

		if (!isValidDeliveryStatus(deliveryStatus)) {
			setError(detail, CourierDetailError.LCD_004);
			return;
		}

		if ("D".equals(deliveryStatus) || "R".equals(deliveryStatus)) {
			if (StringUtils.isBlank(DateUtil.formatToLongDate(detail.getDeliveryDate()))) {
				setError(detail, CourierDetailError.LCD_005);
				return;
			}
		}

		Date date = DateUtil.getSqlDate(detail.getLetterDate());
		Long isExist = courierDetailUploadDAO.isFileExist(reference, letterType, date);
		if (isExist != null) {
			detail.setProgress(EodConstants.PROGRESS_FAILED);
			detail.setErrorCode("LCD_999");
			detail.setErrorDesc("Same data already exist with the uploadId : " + isExist);
			return;
		}

		// If Courier details are being uploaded against a letter generated and shared with customer
		// with Mode as ‘Email’ than application will mark the status of record as ‘failed’ with relevant reject reason.

		// If Combination of Loan reference, Letter Type and Letter Generation date is not available where letter was
		// issued
		// through courier then application to mark the status of record as ‘Failed’ with relevant reject reasons.

		detail.setReferenceID(fm.getFinID());
		detail.setProgress(EodConstants.PROGRESS_SUCCESS);
		detail.setErrorCode("");
		detail.setErrorDesc("");
	}

	@Override
	public void doApprove(List<FileUploadHeader> headers) {
		logger.debug(Literal.ENTERING);

		new Thread(() -> {
			DefaultTransactionDefinition txDef = new DefaultTransactionDefinition(
					TransactionDefinition.PROPAGATION_REQUIRES_NEW);
			TransactionStatus txStatus = null;

			for (FileUploadHeader header : headers) {
				logger.info("Processing the File {}", header.getFileName());
				List<CourierDetailUpload> details = courierDetailUploadDAO.getDetails(header.getId());

				header.setTotalRecords(details.size());
				int sucessRecords = 0;
				int failRecords = 0;

				for (CourierDetailUpload detail : details) {
					doValidate(header, detail);

					if (EodConstants.PROGRESS_SUCCESS == detail.getProgress()) {

						txStatus = transactionManager.getTransaction(txDef);

						detail.setProgress(EodConstants.PROGRESS_SUCCESS);
						detail.setErrorDesc("");
						detail.setErrorCode("");
					}

					transactionManager.commit(txStatus);

					if (EodConstants.PROGRESS_FAILED == detail.getProgress()) {
						failRecords++;
					} else {
						sucessRecords++;
						detail.setProgress(EodConstants.PROGRESS_SUCCESS);
					}

				}
				try {
					txStatus = transactionManager.getTransaction(txDef);

					courierDetailUploadDAO.update(details);

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

					updateHeader(headers, true);

					logger.info("BulkLetterCourierDetails Process is Initiated");

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

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void doReject(List<FileUploadHeader> headers) {
		List<Long> headerIdList = headers.stream().map(FileUploadHeader::getId).collect(Collectors.toList());

		DefaultTransactionDefinition txDef = new DefaultTransactionDefinition(
				TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		TransactionStatus txStatus = null;
		try {
			txStatus = transactionManager.getTransaction(txDef);

			courierDetailUploadDAO.update(headerIdList, ERR_CODE, ERR_DESC, EodConstants.PROGRESS_FAILED);

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

	private void setError(CourierDetailUpload detail, CourierDetailError error) {
		detail.setProgress(EodConstants.PROGRESS_FAILED);
		detail.setErrorCode(error.name());
		detail.setErrorDesc(error.description());
	}

	private boolean isValidLetterType(String letterType) {
		switch (letterType) {
		case "CLOSURE":
		case "NOC":
		case "CANCELLATIONLETTER":
			return true;
		default:
			return false;
		}
	}

	private boolean isValidDeliveryStatus(String deliverystatus) {
		switch (deliverystatus) {
		case "D":
		case "T":
		case "R":
		case "L":
			return true;
		default:
			return false;
		}
	}

	@Override
	public String getSqlQuery() {
		return courierDetailUploadDAO.getSqlQuery();
	}

	@Autowired
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	@Override
	public ValidateRecord getValidateRecord() {
		return courierDetailUploadValidateRecord;
	}

	@Autowired
	public void setCourierDetailUploadValidateRecord(CourierDetailUploadValidateRecord courierDetailVaidateRecord) {
		this.courierDetailUploadValidateRecord = courierDetailVaidateRecord;
	}

	@Autowired
	public void setCourierDetailUploadDAO(CourierDetailUploadDAO courierDetailUploadDAO) {
		this.courierDetailUploadDAO = courierDetailUploadDAO;
	}
}