package com.pennant.pff.presentment.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.TransactionStatus;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.pff.presentment.dao.RePresentmentUploadDAO;
import com.pennant.pff.presentment.exception.PresentmentError;
import com.pennant.pff.presentment.model.RePresentmentUploadDetail;
import com.pennant.pff.presentment.service.ExtractionService;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.service.impl.AUploadServiceImpl;
import com.pennanttech.dataengine.model.DataEngineAttributes;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.file.UploadTypes;
import com.pennapps.core.util.ObjectUtil;

public class RePresentmentUploadServiceImpl extends AUploadServiceImpl<RePresentmentUploadDetail> {
	private static final Logger logger = LogManager.getLogger(RePresentmentUploadServiceImpl.class);

	private ExtractionService extractionService;
	private RePresentmentUploadDAO representmentUploadDAO;
	private FinanceMainDAO financeMainDAO;
	private FinanceProfitDetailDAO profitDetailsDAO;

	public RePresentmentUploadServiceImpl() {
		super();
	}

	@Override
	protected RePresentmentUploadDetail getDetail(Object object) {
		if (object instanceof RePresentmentUploadDetail detail) {
			return detail;
		}

		throw new AppException(IN_VALID_OBJECT);
	}

	@Override
	public void doApprove(List<FileUploadHeader> headers) {
		new Thread(() -> {

			List<Long> headerIdList = headers.stream().map(FileUploadHeader::getId).toList();

			Date appDate = SysParamUtil.getAppDate();
			String acBounce = SysParamUtil.getValueAsString(SMTParameterConstants.BOUNCE_CODES_FOR_ACCOUNT_CLOSED);

			for (FileUploadHeader header : headers) {
				logger.info("Processing the File {}", header.getFileName());

				List<RePresentmentUploadDetail> details = representmentUploadDAO.getDetails(header.getId());
				header.getUploadDetails().addAll(details);

				header.setAppDate(appDate);

				for (RePresentmentUploadDetail detail : details) {
					detail.setAcBounce(acBounce);
					doValidate(header, detail);
				}

				representmentUploadDAO.update(details);

				logger.info("Processed the File {}", header.getFileName());
			}

			TransactionStatus txStatus = getTransactionStatus();
			try {
				logger.info("RePresentment Process is Initiated");
				extractionService.extractRePresentment(headerIdList);

				transactionManager.commit(txStatus);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);

				if (txStatus != null) {
					transactionManager.rollback(txStatus);
				}
			} finally {
				txStatus = null;
			}

			updateHeader(headers, true);
		}).start();
	}

	@Override
	public void doReject(List<FileUploadHeader> headers) {
		List<Long> headerIdList = headers.stream().map(FileUploadHeader::getId).toList();

		TransactionStatus txStatus = getTransactionStatus();

		try {
			headers.forEach(h1 -> {
				h1.setRemarks(REJECT_DESC);
				h1.getUploadDetails().addAll(representmentUploadDAO.getDetails(h1.getId()));
			});

			representmentUploadDAO.update(headerIdList, REJECT_CODE, REJECT_DESC);

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
	public void doValidate(FileUploadHeader header, Object object) {
		RePresentmentUploadDetail detail = getDetail(object);

		logger.info("Validating the Data for the reference {}", detail.getReference());

		Date appDate = header.getAppDate();

		detail.setHeaderId(header.getId());

		String reference = detail.getReference();

		if (StringUtils.isBlank(reference)) {
			setError(detail, PresentmentError.REPRMNT513);
			return;
		}

		FinanceMain fm = financeMainDAO.getFinanceMain(reference, header.getEntityCode());

		if (fm == null) {
			setError(detail, PresentmentError.REPRMNT514);
			return;
		}

		detail.setFm(fm);
		detail.setReferenceID(fm.getFinID());

		if (!fm.isFinIsActive()) {
			setError(detail, PresentmentError.REPRMNT515);
			return;
		}

		Date dueDate = detail.getDueDate();
		if (dueDate == null) {
			setError(detail, PresentmentError.REPRMNT516);
			return;
		}

		if (DateUtil.compare(dueDate, appDate) > 0) {
			setError(detail, PresentmentError.REPRMNT517);
			return;
		}

		String bounceCode = representmentUploadDAO.getBounceCode(reference, dueDate);

		if (bounceCode == null) {
			setError(detail, PresentmentError.REPRMNT518);
			return;
		}

		if (detail.getAcBounce().contains(bounceCode)) {
			setError(detail, PresentmentError.REPRMNT519);
			return;
		}

		if (representmentUploadDAO.isProcessed(reference, dueDate)) {
			setError(detail, PresentmentError.REPRMNT520);
			return;
		}

		if (profitDetailsDAO.getCurOddays(fm.getFinID()) == 0) {
			setError(detail, PresentmentError.REPRMNT521);
			return;
		}

		List<String> fileNames = representmentUploadDAO.isDuplicateExists(reference, dueDate, header.getId());

		if (!fileNames.isEmpty()) {
			StringBuilder message = new StringBuilder();

			message.append("Duplicate RePresentMent exists with combination FinReference -");
			message.append(reference);
			message.append(", Due Date -").append(dueDate);
			message.append("with filename").append(fileNames.get(0)).append("already exists");

			setFailureStatus(detail, PresentmentError.REPRMNT523.name(), message.toString());

			logger.info("Duplicate RePresentment found in RePresentUpload table..");
			return;
		}

		setSuccesStatus(detail);

		logger.info("Validated the Data for the reference {}", detail.getReference());
	}

	@Override
	public void uploadProcess() {
		uploadProcess(UploadTypes.RE_PRESENTMENT.name(), this, "RepresentUploadHeader");
	}

	@Override
	public String getSqlQuery() {
		return representmentUploadDAO.getSqlQuery();
	}

	@Override
	public void validate(DataEngineAttributes attributes, MapSqlParameterSource paramSource) throws Exception {
		logger.debug(Literal.ENTERING);

		RePresentmentUploadDetail representment = (RePresentmentUploadDetail) ObjectUtil.valueAsObject(paramSource,
				RePresentmentUploadDetail.class);

		String acBounce = SysParamUtil.getValueAsString(SMTParameterConstants.BOUNCE_CODES_FOR_ACCOUNT_CLOSED);
		representment.setReference(ObjectUtil.valueAsString(paramSource.getValue("finReference")));
		representment.setDueDate(ObjectUtil.valueAsDate(paramSource.getValue("dueDate")));
		representment.setAcBounce(acBounce);

		Map<String, Object> parameterMap = attributes.getParameterMap();

		FileUploadHeader header = (FileUploadHeader) parameterMap.get("FILE_UPLOAD_HEADER");

		representment.setHeaderId(header.getId());
		representment.setAppDate(header.getAppDate());

		doValidate(header, representment);

		updateProcess(header, representment, paramSource);

		header.getUploadDetails().add(representment);

		logger.debug(Literal.LEAVING);
	}

	private void setError(RePresentmentUploadDetail detail, PresentmentError error) {
		setFailureStatus(detail, error.name(), error.description());
	}

	@Autowired
	public void setExtractionService(ExtractionService extractionService) {
		this.extractionService = extractionService;
	}

	@Autowired
	public void setRePresentmentUploadDAO(RePresentmentUploadDAO rePresentmentUploadDAO) {
		this.representmentUploadDAO = rePresentmentUploadDAO;
	}

	@Autowired
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	@Autowired
	public void setProfitDetailsDAO(FinanceProfitDetailDAO profitDetailsDAO) {
		this.profitDetailsDAO = profitDetailsDAO;
	}
}