package com.pennant.pff.noc.upload.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.TransactionStatus;

import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.noc.upload.dao.CourierDetailUploadDAO;
import com.pennant.pff.noc.upload.error.CourierDetailUploadError;
import com.pennant.pff.noc.upload.model.CourierDetailUpload;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.service.impl.AUploadServiceImpl;
import com.pennanttech.dataengine.model.DataEngineAttributes;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.file.UploadTypes;
import com.pennapps.core.util.ObjectUtil;

public class CourierDetailUploadServiceImpl extends AUploadServiceImpl<CourierDetailUpload> {
	private static final Logger logger = LogManager.getLogger(CourierDetailUploadServiceImpl.class);

	private FinanceMainDAO financeMainDAO;
	private CourierDetailUploadDAO courierDetailUploadDAO;

	public CourierDetailUploadServiceImpl() {
		super();
	}

	protected CourierDetailUpload getDetail(Object object) {
		if (object instanceof CourierDetailUpload detail) {
			return detail;
		}

		throw new AppException(IN_VALID_OBJECT);
	}

	@Override
	public void doValidate(FileUploadHeader header, Object object) {
		CourierDetailUpload detail = getDetail(object);

		String letterType = detail.getLetterType();
		String reference = detail.getReference();
		String deliveryStatus = detail.getDeliveryStatus();
		FinanceMain fm = financeMainDAO.getFinanceMain(reference, header.getEntityCode());

		if (fm == null) {
			setError(detail, CourierDetailUploadError.LCD_001);
			return;
		}

		if (!fm.isFinIsActive()) {
			setError(detail, CourierDetailUploadError.LCD_002);
			return;
		}

		if (!isValidLetterType(letterType)) {
			setError(detail, CourierDetailUploadError.LCD_003);
			return;
		}

		if (!isValidDeliveryStatus(deliveryStatus)) {
			setError(detail, CourierDetailUploadError.LCD_004);
			return;
		}

		if ("D".equals(deliveryStatus) || "R".equals(deliveryStatus)) {
			if (StringUtils.isBlank(DateUtil.formatToLongDate(detail.getDeliveryDate()))) {
				setError(detail, CourierDetailUploadError.LCD_005);
				return;
			}
		}

		Date date = DateUtil.getSqlDate(detail.getLetterDate());
		Long isExist = courierDetailUploadDAO.isFileExist(reference, letterType, date);
		if (isExist != null) {
			setFailureStatus(detail, "LCD_999", "Same data already exist with the uploadId : " + isExist);
			return;
		}

		// If Courier details are being uploaded against a letter generated and shared with customer
		// with Mode as ‘Email’ than application will mark the status of record as ‘failed’ with relevant reject reason.

		// If Combination of Loan reference, Letter Type and Letter Generation date is not available where letter was
		// issued
		// through courier then application to mark the status of record as ‘Failed’ with relevant reject reasons.

		detail.setReferenceID(fm.getFinID());
		setSuccesStatus(detail);
	}

	@Override
	public void doApprove(List<FileUploadHeader> headers) {
		logger.debug(Literal.ENTERING);

		new Thread(() -> {

			for (FileUploadHeader header : headers) {
				logger.info("Processing the File {}", header.getFileName());
				List<CourierDetailUpload> details = courierDetailUploadDAO.getDetails(header.getId());

				header.setTotalRecords(details.size());
				int sucessRecords = 0;
				int failRecords = 0;

				for (CourierDetailUpload detail : details) {
					doValidate(header, detail);

					if (EodConstants.PROGRESS_FAILED == detail.getProgress()) {
						failRecords++;
					} else {
						sucessRecords++;
					}
				}

				header.getUploadDetails().addAll(details);

				try {
					courierDetailUploadDAO.update(details);

					header.setSuccessRecords(sucessRecords);
					header.setFailureRecords(failRecords);

					logger.info("Processed the File {}", header.getFileName());

					updateHeader(headers, true);

					logger.info("BulkLetterCourierDetails Process is Initiated");
				} catch (Exception e) {
					logger.error(Literal.EXCEPTION, e);
				}
			}
		}).start();
	}

	@Override
	public void doReject(List<FileUploadHeader> headers) {
		List<Long> headerIdList = headers.stream().map(FileUploadHeader::getId).toList();

		TransactionStatus txStatus = getTransactionStatus();

		try {

			headers.forEach(h1 -> {
				h1.setRemarks(REJECT_DESC);
				h1.getUploadDetails().addAll(courierDetailUploadDAO.getDetails(h1.getId()));
			});

			courierDetailUploadDAO.update(headerIdList, REJECT_CODE, REJECT_DESC);

			updateHeader(headers, false);

			transactionManager.commit(txStatus);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);

			if (txStatus != null) {
				transactionManager.rollback(txStatus);
			}
		}
	}

	@Override
	public void uploadProcess() {
		uploadProcess(UploadTypes.COURIER_DETAILS.name(), this, "CourierDetailUploadHeader");
	}

	@Override
	public void validate(DataEngineAttributes attributes, MapSqlParameterSource paramSource) throws Exception {
		logger.debug(Literal.ENTERING);

		CourierDetailUpload genLetter = (CourierDetailUpload) ObjectUtil.valueAsObject(paramSource,
				CourierDetailUpload.class);

		genLetter.setReference(ObjectUtil.valueAsString(paramSource.getValue("finReference")));

		Map<String, Object> parameterMap = attributes.getParameterMap();

		FileUploadHeader header = (FileUploadHeader) parameterMap.get("FILE_UPLOAD_HEADER");

		genLetter.setHeaderId(header.getId());
		genLetter.setAppDate(header.getAppDate());

		doValidate(header, genLetter);

		updateProcess(header, genLetter, paramSource);

		header.getUploadDetails().add(genLetter);

		logger.debug(Literal.LEAVING);
	}

	private void setError(CourierDetailUpload detail, CourierDetailUploadError error) {
		setFailureStatus(detail, error.name(), error.description());
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

	@Autowired
	public void setCourierDetailUploadDAO(CourierDetailUploadDAO courierDetailUploadDAO) {
		this.courierDetailUploadDAO = courierDetailUploadDAO;
	}
}