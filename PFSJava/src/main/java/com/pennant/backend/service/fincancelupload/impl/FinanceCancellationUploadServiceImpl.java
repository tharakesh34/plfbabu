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
import com.pennanttech.pff.core.RequestSource;
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

	public FinanceCancellationUploadServiceImpl() {
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
		fm.setFinSourceID(RequestSource.UPLOAD.name());
		FinCancelUploadError errorDetail = financeCancelValidator.validLoan(fm, schedules);
		if (errorDetail != null) {
			setError(detail, errorDetail);
			return;
		}

		setSuccesStatus(detail);

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

				List<String> key = new ArrayList<>();

				for (FinCancelUploadDetail detail : details) {
					String keyRef = detail.getReference();

					if (key.contains(keyRef)) {
						setError(detail, FinCancelUploadError.LANCLUP016);
						continue;
					}

					key.add(keyRef);
					header.getUploadDetails().add(detail);

					doValidate(header, detail);
					if (detail.getProgress() == EodConstants.PROGRESS_FAILED) {
						setFailureStatus(detail);
					} else {
						logger.info("Loan Cancelation Process is Initiated for the Header ID {}", header.getId());

						detail.setAppDate(appDate);
						processCancelLoan(detail);

						if (detail.getProgress() == EodConstants.PROGRESS_SUCCESS) {
							setSuccesStatus(detail);
						}
						logger.info("Loan Cancelation Process is Completed for the Header ID {}", header.getId());
					}

				}

				try {
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
			headers.forEach(h1 -> {
				h1.setRemarks(REJECT_DESC);
				h1.getUploadDetails().addAll(financeCancellationUploadDAO.getDetails(h1.getId()));
			});

			financeCancellationUploadDAO.update(headerIdList, REJECT_CODE, REJECT_DESC);

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

		if (EodConstants.PROGRESS_FAILED == detail.getProgress()) {
			return;
		}

		TransactionStatus txnStatus = getTransactionStatus();

		try {
			fm.setFinSourceID(UploadTypes.LOAN_CANCEL.name());
			fm.setCancelRemarks(detail.getRemarks());
			fm.setCancelType(detail.getCancelType());
			if (rh != null) {
				fm.setDetailsList(rh.getDetailsList());
			}

			processLoanCancel(fd, detail);

			this.transactionManager.commit(txnStatus);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);

			if (txnStatus != null) {
				transactionManager.rollback(txnStatus);
			}

			setFailureStatus(detail, e.getMessage());
		}
	}

	private ReasonHeader processReasonHeader(FinCancelUploadDetail detail) {
		ReasonCode reasonCode = reasonDetailDAO.getCancelReasonByCode(detail.getReason(), "_AView");
		if (reasonCode == null) {
			setFailureStatus(detail, "Reason Code is not Valid");

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

	private void processLoanCancel(FinanceDetail fd, FinCancelUploadDetail fcud) {
		AuditHeader audH = financeCancellationService.doApprove(getAuditDetail(fd), true);

		if (audH.getErrorMessage() != null) {
			ErrorDetail ed = audH.getErrorMessage().get(0);
			setFailureStatus(fcud, ed.getCode(), ed.getError());
		}
	}

	private AuditHeader getAuditDetail(FinanceDetail fd) {
		AuditDetail auditDetail = new AuditDetail(PennantConstants.TRAN_WF, 1, null, fd);
		return new AuditHeader(fd.getFinReference(), null, null, null, auditDetail,
				fd.getFinScheduleData().getFinanceMain().getUserDetails(), new HashMap<>());
	}

	private void setError(FinCancelUploadDetail detail, FinCancelUploadError error) {
		String desc = financeCancelValidator.getOverrideDescription(error, detail.getFm());
		setFailureStatus(detail, error.name(), desc);
	}

	@Override
	public void uploadProcess() {
		uploadProcess(UploadTypes.LOAN_CANCEL.name(), this, "LoanCancelUploadHeader");
	}

	@Override
	public void validate(DataEngineAttributes attributes, MapSqlParameterSource paramSource) throws Exception {
		logger.debug(Literal.ENTERING);

		FinCancelUploadDetail detail = (FinCancelUploadDetail) ObjectUtil.valueAsObject(paramSource,
				FinCancelUploadDetail.class);

		detail.setReference(ObjectUtil.valueAsString(paramSource.getValue("finReference")));

		Map<String, Object> parameterMap = attributes.getParameterMap();

		FileUploadHeader header = (FileUploadHeader) parameterMap.get("FILE_UPLOAD_HEADER");

		detail.setHeaderId(header.getId());
		detail.setAppDate(header.getAppDate());

		doValidate(header, detail);

		updateProcess(header, detail, paramSource);

		header.getUploadDetails().add(detail);

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