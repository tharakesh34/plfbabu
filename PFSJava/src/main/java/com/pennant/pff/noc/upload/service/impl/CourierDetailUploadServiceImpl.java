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
import com.pennant.backend.util.NOCConstants;
import com.pennant.pff.letter.dao.AutoLetterGenerationDAO;
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
	private AutoLetterGenerationDAO autoLetterGenerationDAO;

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

		Long finID = financeMainDAO.getFinID(reference);

		if (finID == null) {
			setError(detail, CourierDetailUploadError.LCD_001);
			return;
		}

		detail.setReferenceID(finID);

		if (!isValidLetterType(letterType)) {
			setError(detail, CourierDetailUploadError.LCD_003);
			return;
		}

		if (StringUtils.isNotBlank(deliveryStatus) && !isValidDeliveryStatus(deliveryStatus)) {
			setError(detail, CourierDetailUploadError.LCD_004);
			return;
		}

		if ("D".equals(deliveryStatus) || "R".equals(deliveryStatus)) {
			if (StringUtils.isBlank(DateUtil.formatToLongDate(detail.getDeliveryDate()))) {
				setError(detail, CourierDetailUploadError.LCD_009);
				return;
			}
		}

		Date letterGenDate = DateUtil.getSqlDate(detail.getLetterDate());
		if (courierDetailUploadDAO.isValidRecord(detail.getReferenceID(), letterType, letterGenDate)) {
			setError(detail, CourierDetailUploadError.LCD_006);
			return;
		}

		String letterMode = courierDetailUploadDAO.isValidCourierMode(detail.getReferenceID(), letterType,
				letterGenDate);
		if (letterMode != null && !NOCConstants.MODE_COURIER.equals(letterMode)) {
			setError(detail, CourierDetailUploadError.LCD_007);
			return;
		}

		setSuccesStatus(detail);
	}

	@Override
	public void doApprove(List<FileUploadHeader> headers) {
		logger.debug(Literal.ENTERING);

		new Thread(() -> {

			for (FileUploadHeader header : headers) {
				logger.info("Processing the File {}", header.getFileName());

				List<CourierDetailUpload> details = courierDetailUploadDAO.getDetails(header.getId());
				header.getUploadDetails().addAll(details);

				for (CourierDetailUpload detail : details) {
					doValidate(header, detail);
					Long letterId = validateLetterDetails(detail);

					if (letterId != null) {
						courierDetailUploadDAO.update(detail, letterId);
					}

				}

				try {
					courierDetailUploadDAO.update(details);

					logger.info("Processed the File {}", header.getFileName());

					updateHeader(headers, true);

				} catch (Exception e) {
					logger.error(Literal.EXCEPTION, e);
				}
			}
		}).start();
	}

	private Long validateLetterDetails(CourierDetailUpload detail) {
		Long letterId = autoLetterGenerationDAO.getLetterId(detail.getReferenceID(), detail.getLetterType(),
				detail.getLetterDate());
		if (letterId == null) {
			setError(detail, CourierDetailUploadError.LCD_008);
		}

		return letterId;
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
		case "CANCELLATION":
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

	@Autowired
	public void setAutoLetterGenerationDAO(AutoLetterGenerationDAO autoLetterGenerationDAO) {
		this.autoLetterGenerationDAO = autoLetterGenerationDAO;
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