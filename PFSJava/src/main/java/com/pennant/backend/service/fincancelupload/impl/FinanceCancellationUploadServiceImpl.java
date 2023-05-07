package com.pennant.backend.service.fincancelupload.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.loancancel.FinanceCancellationUploadDAO;
import com.pennant.backend.dao.reason.deatil.ReasonDetailDAO;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.ReasonCode;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinCancelUploadDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.reason.details.ReasonDetails;
import com.pennant.backend.model.reason.details.ReasonHeader;
import com.pennant.backend.service.finance.FinanceCancellationService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.finance.validation.FinanceCancelValidator;
import com.pennant.backend.util.PennantConstants;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.fincancelupload.exception.FinCancelUploadError;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.service.impl.AUploadServiceImpl;
import com.pennanttech.dataengine.model.DataEngineAttributes;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.util.LoanCancelationUtil;
import com.pennanttech.pff.file.UploadTypes;
import com.pennapps.core.util.ObjectUtil;

public class FinanceCancellationUploadServiceImpl extends AUploadServiceImpl<FinCancelUploadDetail> {
	private static final Logger logger = LogManager.getLogger(FinanceCancellationUploadServiceImpl.class);

	private FinanceCancellationUploadDAO financeCancellationUploadDAO;
	private FinanceCancellationService financeCancellationService;
	private FinanceDetailService financeDetailService;
	private ReasonDetailDAO reasonDetailDAO;
	private FinanceMainDAO financeMainDAO;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private FinanceCancelValidator financeCancelValidator;
	
	public FinanceCancellationUploadServiceImpl(){
		super();
	}

	@Override
	protected FinCancelUploadDetail getDetail(Object object) {
		if (object instanceof FinCancelUploadDetail detail) {
			return detail;
		}

		throw new AppException(IN_VALID_OBJECT);
	}

	@Override
	public void doValidate(FileUploadHeader header, Object object) {
		FinCancelUploadDetail detail = getDetail(object);

		logger.info("Validating the Data for the reference {}", detail.getReference());

		detail.setHeaderId(header.getId());

		String reference = detail.getReference();
		String cancelType = detail.getCancelType();

		if (StringUtils.isBlank(reference)) {
			setError(detail, FinCancelUploadError.LANCLUP001);
			return;
		}

		FinanceMain fm = financeMainDAO.getFinanceMain(reference, header.getEntityCode());

		if (fm == null) {
			setError(detail, FinCancelUploadError.LANCLUP002);
			return;
		}

		if (!fm.isFinIsActive()) {
			setError(detail, FinCancelUploadError.LANCLUP003);
			return;
		}

		if (fm.getRcdMaintainSts() != null && !FinServiceEvent.CANCELFIN.equals(fm.getRcdMaintainSts())) {
			setError(detail, FinCancelUploadError.LANCLUP004);
			return;
		}

		List<ValueLabel> type = LoanCancelationUtil.getLoancancelTypes();

		if (type.stream().noneMatch(c -> c.getValue().equals(cancelType))) {
			setError(detail, FinCancelUploadError.LANCLUP019);
			return;
		}

		detail.setFm(fm);
		detail.setReferenceID(fm.getFinID());
		fm.setUserDetails(header.getUserDetails());

		List<FinanceScheduleDetail> schedules = financeScheduleDetailDAO.getFinScheduleDetails(fm.getFinID(), "",
				false);
		fm.setAppDate(header.getAppDate());

		FinCancelUploadError errorDetail = financeCancelValidator.validLoan(fm, schedules);
		if (errorDetail != null) {
			setError(detail, errorDetail);
			return;
		}

		detail.setProgress(EodConstants.PROGRESS_SUCCESS);
		detail.setErrorCode("");
		detail.setErrorDesc("");

		logger.info("Validated the Data for the reference {}", detail.getReference());
	}

	@Override
	public void doApprove(List<FileUploadHeader> headers) {
		new Thread(() -> {
			Date appDate = SysParamUtil.getAppDate();

			for (FileUploadHeader header : headers) {
				logger.info("Processing the File {}", header.getFileName());

				List<FinCancelUploadDetail> details = financeCancellationUploadDAO.getDetails(header.getId());

				header.setAppDate(appDate);
				header.setTotalRecords(details.size());
				int sucessRecords = 0;
				int failRecords = 0;

				List<String> key = new ArrayList<>();

				for (FinCancelUploadDetail detail : details) {
					String keyRef = detail.getReference();

					if (key.contains(keyRef)) {
						setError(detail, FinCancelUploadError.LANCLUP016);
						failRecords++;
						continue;
					}

					key.add(keyRef);

					doValidate(header, detail);
					if (detail.getProgress() == EodConstants.PROGRESS_FAILED) {
						failRecords++;
					} else {
						sucessRecords++;

						logger.info("Loan Cancelation Process is Initiated for the Header ID {}", header.getId());

						detail.setAppDate(appDate);
						processCancelLoan(detail);

						logger.info("Loan Cancelation Process is Completed for the Header ID {}", header.getId());
					}

					header.getUploadDetails().add(detail);
				}

				try {
					header.setSuccessRecords(sucessRecords);
					header.setFailureRecords(failRecords);

					logger.info("Processed the File {}", header.getFileName());

					financeCancellationUploadDAO.update(details);

					List<FileUploadHeader> headerList = new ArrayList<>();
					headerList.add(header);
					updateHeader(headerList, true);

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
			financeCancellationUploadDAO.update(headerIdList, ERR_CODE, ERR_DESC, EodConstants.PROGRESS_FAILED);

			headers.forEach(h1 -> {
				h1.setRemarks(ERR_DESC);
				h1.getUploadDetails().addAll(financeCancellationUploadDAO.getDetails(h1.getId()));
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

	@Override
	public String getSqlQuery() {
		return financeCancellationUploadDAO.getSqlQuery();
	}

	private void processCancelLoan(FinCancelUploadDetail detail) {
		Long finId = detail.getReferenceID();

		FinanceDetail fd = financeDetailService.getFinanceDetailById(finId, false, "", false, FinServiceEvent.ORG, "");
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		fm.setVersion(schdData.getFinanceMain().getVersion() + 1);

		ReasonHeader rh = processReasonHeader(detail);

		if (rh != null) {
			fd.setReasonHeader(rh);
		}

		TransactionStatus txnStatus = getTransactionStatus();

		try {
			if (EodConstants.PROGRESS_FAILED != detail.getProgress()) {
				fm.setFinSourceID(UploadTypes.LOAN_CANCEL.name());
				fm.setCancelRemarks(detail.getRemarks());
				fm.setCancelType(detail.getCancelType());
				if (rh != null) {
					fm.setDetailsList(rh.getDetailsList());
				}

				processLoanCancel(fd, detail);
			}

			this.transactionManager.commit(txnStatus);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);

			if (txnStatus != null) {
				transactionManager.rollback(txnStatus);
			}

			String error = StringUtils.trimToEmpty(e.getMessage());

			if (error.length() > 1999) {
				error = error.substring(0, 1999);
			}

			detail.setProgress(EodConstants.PROGRESS_FAILED);
			detail.setErrorDesc(error);
		}
	}

	private ReasonHeader processReasonHeader(FinCancelUploadDetail detail) {
		ReasonCode reasonCode = reasonDetailDAO.getCancelReasonByCode(detail.getReason(), "_AView");
		if (reasonCode == null) {
			detail.setProgress(EodConstants.PROGRESS_FAILED);
			detail.setErrorDesc("Reason Code is not Valid");
			return null;
		}

		ReasonHeader rh = new ReasonHeader();

		rh.setRemarks(detail.getRemarks());
		rh.setCancelType(detail.getCancelType());
		rh.setReasonId(reasonCode.getReasonTypeID());

		ReasonDetails rd = new ReasonDetails();
		rd.setReasonId(reasonCode.getReasonTypeID());
		rd.setReasonCode(detail.getReason());
		rh.getDetailsList().add(rd);

		return rh;
	}

	private void processLoanCancel(FinanceDetail financeDetail, FinCancelUploadDetail fcud) {
		AuditHeader ah = getAuditDetail(financeDetail);

		AuditHeader audH = financeCancellationService.doApprove(ah, true);

		if (audH.getErrorMessage() != null) {
			fcud.setProgress(EodConstants.PROGRESS_FAILED);
			for (ErrorDetail errorDetail : audH.getErrorMessage()) {
				fcud.setErrorCode(errorDetail.getCode());
				fcud.setErrorDesc(errorDetail.getError());
			}
		}
	}

	private AuditHeader getAuditDetail(FinanceDetail fd) {
		AuditDetail auditDetail = new AuditDetail(PennantConstants.TRAN_WF, 1, null, fd);
		return new AuditHeader(fd.getFinReference(), null, null, null, auditDetail,
				fd.getFinScheduleData().getFinanceMain().getUserDetails(), new HashMap<>());
	}

	private void setError(FinCancelUploadDetail detail, FinCancelUploadError error) {
		detail.setProgress(EodConstants.PROGRESS_FAILED);
		detail.setErrorCode(error.name());
		detail.setErrorDesc(financeCancelValidator.getOverrideDescription(error, detail.getFm()));
	}

	@Override
	public void uploadProcess() {
		uploadProcess(UploadTypes.LOAN_CANCEL.name(), this, "LoanCancelUploadHeader");
	}

	@Override
	public void validate(DataEngineAttributes attributes, MapSqlParameterSource paramSource) throws Exception {
		logger.debug(Literal.ENTERING);

		FinCancelUploadDetail details = (FinCancelUploadDetail) ObjectUtil.valueAsObject(paramSource,
				FinCancelUploadDetail.class);

		details.setReference(ObjectUtil.valueAsString(paramSource.getValue("finReference")));

		Map<String, Object> parameterMap = attributes.getParameterMap();

		FileUploadHeader header = (FileUploadHeader) parameterMap.get("FILE_UPLOAD_HEADER");

		details.setHeaderId(header.getId());
		details.setAppDate(header.getAppDate());

		doValidate(header, details);

		updateProcess(header, details, paramSource);

		logger.debug(Literal.LEAVING);
	}

	@Autowired
	public void setFinanceCancellationUploadDAO(FinanceCancellationUploadDAO financeCancellationUploadDAO) {
		this.financeCancellationUploadDAO = financeCancellationUploadDAO;
	}

	@Autowired
	public void setFinanceCancellationService(FinanceCancellationService financeCancellationService) {
		this.financeCancellationService = financeCancellationService;
	}

	@Autowired
	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	@Autowired
	public void setReasonDetailDAO(ReasonDetailDAO reasonDetailDAO) {
		this.reasonDetailDAO = reasonDetailDAO;
	}

	@Autowired
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	@Autowired
	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	@Autowired
	public void setFinanceCancelValidator(FinanceCancelValidator financeCancelValidator) {
		this.financeCancelValidator = financeCancelValidator;
	}

}