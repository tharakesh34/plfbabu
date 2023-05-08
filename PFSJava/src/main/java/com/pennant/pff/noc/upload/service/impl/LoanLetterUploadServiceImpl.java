package com.pennant.pff.noc.upload.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.TransactionStatus;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.rmtmasters.FinTypeFees;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.NOCConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.noc.upload.dao.LoanLetterUploadDAO;
import com.pennant.pff.noc.upload.error.LoanLetterUploadError;
import com.pennant.pff.noc.upload.model.LoanLetterUpload;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.service.impl.AUploadServiceImpl;
import com.pennanttech.dataengine.model.DataEngineAttributes;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.util.LoanCancelationUtil;
import com.pennanttech.pff.file.UploadTypes;
import com.pennapps.core.util.ObjectUtil;

public class LoanLetterUploadServiceImpl extends AUploadServiceImpl<LoanLetterUpload> {
	private static final Logger logger = LogManager.getLogger(LoanLetterUploadServiceImpl.class);

	private LoanLetterUploadDAO loanLetterUploadDAO;
	private FinanceMainDAO financeMainDAO;

	public LoanLetterUploadServiceImpl() {
		super();
	}

	protected LoanLetterUpload getDetail(Object object) {
		if (object instanceof LoanLetterUpload detail) {
			return detail;
		}

		throw new AppException(IN_VALID_OBJECT);
	}

	@Override
	public void doValidate(FileUploadHeader header, Object object) {
		LoanLetterUpload detail = getDetail(object);

		String reference = detail.getReference();

		logger.info("Validating the Data for the reference {}", reference);

		detail.setHeaderId(header.getId());

		FinanceMain fm = financeMainDAO.getFinanceMainByRef(reference, "", false);

		if (fm == null) {
			setError(detail, LoanLetterUploadError.LOAN_LTR_01);
			return;
		}

		detail.setReferenceID(fm.getFinID());

		String letterType = detail.getLetterType();
		if (!NOCConstants.TYPE_NOC_LTR.equals(letterType) && !NOCConstants.TYPE_CLOSE_LTR.equals(letterType)
				&& !NOCConstants.TYPE_CAN_LTR.equals(letterType)) {
			setError(detail, LoanLetterUploadError.LOAN_LTR_02);
			return;
		}

		String mode = detail.getModeOfTransfer();
		if (!NOCConstants.MODE_COURIER.equals(mode) && !NOCConstants.MODE_EMAIL.equals(mode)) {
			setError(detail, LoanLetterUploadError.LOAN_LTR_03);
			return;
		}

		String waiverCharges = detail.getWaiverCharges();
		if (!PennantConstants.YES.equals(waiverCharges) && !PennantConstants.NO.equals(waiverCharges)) {
			setError(detail, LoanLetterUploadError.LOAN_LTR_04);
			return;
		}

		if (fm.isFinIsActive() && fm.getClosingStatus() == null) {
			setError(detail, LoanLetterUploadError.LOAN_LTR_05);
			return;
		}

		int finTypeLtrmap = loanLetterUploadDAO.getFinTypeLtrMap(reference);
		if (finTypeLtrmap < 0) {
			setError(detail, LoanLetterUploadError.LOAN_LTR_08);
			return;
		}

		if (NOCConstants.TYPE_CLOSE_LTR.equals(letterType) || NOCConstants.TYPE_NOC_LTR.equals(letterType)
				&& FinanceConstants.CLOSE_STATUS_CANCELLED.equals(fm.getClosingStatus())) {
			setError(detail, LoanLetterUploadError.LOAN_LTR_06);
			return;
		}

		if (NOCConstants.TYPE_CAN_LTR.equals(letterType)) {
			if (FinanceConstants.CLOSE_STATUS_MATURED.equals(fm.getClosingStatus())
					|| FinanceConstants.CLOSE_STATUS_EARLYSETTLE.equals(fm.getClosingStatus())
					|| FinanceConstants.CLOSE_STATUS_WRITEOFF.equals(fm.getClosingStatus())) {
				setError(detail, LoanLetterUploadError.LOAN_LTR_07);
				return;
			}
		}

		FinTypeFees ftf = loanLetterUploadDAO.getFeeWaiverAllowed(fm.getFinType(), letterType);

		if (PennantConstants.YES.equals(waiverCharges) && ftf == null
				|| PennantConstants.YES.equals(waiverCharges) && BigDecimal.ZERO == ftf.getMaxWaiverPerc()) {
			setError(detail, LoanLetterUploadError.LOAN_LTR_09);
			return;
		}

		String cancelType = loanLetterUploadDAO.getCanceltype(reference);
		if (LoanCancelationUtil.LOAN_CANCEL_REBOOK.equals(cancelType)) {
			setError(detail, LoanLetterUploadError.LOAN_LTR_10);
		}

	}

	@Override
	public void doApprove(List<FileUploadHeader> headers) {
		new Thread(() -> {

			Date appDate = SysParamUtil.getAppDate();

			for (FileUploadHeader header : headers) {
				logger.info("Processing the File {}", header.getFileName());

				List<LoanLetterUpload> details = loanLetterUploadDAO.getDetails(header.getId());

				header.setAppDate(appDate);
				header.setTotalRecords(details.size());
				int sucessRecords = 0;
				int failRecords = 0;

				for (LoanLetterUpload detail : details) {
					doValidate(header, detail);

					if (detail.getErrorCode() != null) {
						detail.setProgress(EodConstants.PROGRESS_FAILED);
					} else {
						detail.setProgress(EodConstants.PROGRESS_SUCCESS);
						detail.setErrorCode("");
						detail.setErrorDesc("");
					}

					detail.setUserDetails(header.getUserDetails());

					if (detail.getProgress() == EodConstants.PROGRESS_FAILED) {
						failRecords++;
					} else {
						sucessRecords++;
					}

					header.getUploadDetails().add(detail);
				}

				try {
					header.setSuccessRecords(sucessRecords);
					header.setFailureRecords(failRecords);

					loanLetterUploadDAO.update(details);

					List<FileUploadHeader> headerList = new ArrayList<>();
					headerList.add(header);
					updateHeader(headers, true);
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
			loanLetterUploadDAO.update(headerIdList, ERR_CODE, ERR_DESC, EodConstants.PROGRESS_FAILED);

			headers.forEach(h1 -> {
				h1.setRemarks(ERR_DESC);
				h1.getUploadDetails().addAll(loanLetterUploadDAO.getDetails(h1.getId()));
			});

			updateHeader(headers, false);

			transactionManager.commit(txStatus);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);

			if (txStatus != null) {
				transactionManager.rollback(txStatus);
			}
		}
	}

	private void setError(LoanLetterUpload detail, LoanLetterUploadError error) {
		detail.setProgress(EodConstants.PROGRESS_FAILED);
		detail.setErrorCode(error.name());
		detail.setErrorDesc(error.description());
	}

	@Override
	public String getSqlQuery() {
		return loanLetterUploadDAO.getSqlQuery();
	}

	@Autowired
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	@Autowired
	public void setLoanLetterUploadDAO(LoanLetterUploadDAO loanLetterUploadDAO) {
		this.loanLetterUploadDAO = loanLetterUploadDAO;
	}

	@Override
	public void uploadProcess() {
		uploadProcess(UploadTypes.LOAN_LETTER.name(), this, "LoanLetterUploadHeader");
	}

	@Override
	public void validate(DataEngineAttributes attributes, MapSqlParameterSource paramSource) throws Exception {
		logger.debug(Literal.ENTERING);

		LoanLetterUpload transfer = (LoanLetterUpload) ObjectUtil.valueAsObject(paramSource, LoanLetterUpload.class);

		transfer.setReference(ObjectUtil.valueAsString(paramSource.getValue("finReference")));

		Map<String, Object> parameterMap = attributes.getParameterMap();

		FileUploadHeader header = (FileUploadHeader) parameterMap.get("FILE_UPLOAD_HEADER");

		transfer.setHeaderId(header.getId());
		transfer.setAppDate(header.getAppDate());

		doValidate(header, transfer);

		updateProcess(header, transfer, paramSource);

		logger.debug(Literal.LEAVING);
	}
}