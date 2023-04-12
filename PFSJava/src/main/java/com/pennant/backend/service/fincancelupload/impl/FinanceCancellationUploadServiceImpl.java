package com.pennant.backend.service.fincancelupload.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.loancancel.FinanceCancellationUploadDAO;
import com.pennant.backend.dao.reason.deatil.ReasonDetailDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.applicationmaster.ReasonCode;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinCancelUploadDetail;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.reason.details.ReasonDetails;
import com.pennant.backend.model.reason.details.ReasonHeader;
import com.pennant.backend.service.finance.FinanceCancellationService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.UploadConstants;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.fincancelupload.exception.FinCancelUploadError;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.service.impl.AUploadServiceImpl;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pff.constants.FinServiceEvent;

public class FinanceCancellationUploadServiceImpl extends AUploadServiceImpl {
	private static final Logger logger = LogManager.getLogger(FinanceCancellationUploadServiceImpl.class);

	private FinanceCancellationUploadDAO financeCancellationUploadDAO;
	private FinanceCancellationService financeCancellationService;
	private FinanceDetailService financeDetailService;
	private ReasonDetailDAO reasonDetailDAO;
	private FinanceMainDAO financeMainDAO;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private AuditHeaderDAO auditHeaderDAO;
	private FinanceTypeDAO financeTypeDAO;

	@Override
	public void doValidate(FileUploadHeader header, Object object) {
		FinCancelUploadDetail detail = null;

		if (object instanceof FinCancelUploadDetail) {
			detail = (FinCancelUploadDetail) object;
		}

		if (detail == null) {
			throw new AppException("Invalid Data transferred...");
		}

		logger.info("Validating the Data for the reference {}", detail.getReference());

		detail.setHeaderId(header.getId());

		String reference = detail.getReference();

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

		if (fm.getRcdMaintainSts() != null && !fm.getRcdMaintainSts().equals(FinServiceEvent.CANCELFIN)) {
			setError(detail, FinCancelUploadError.LANCLUP004);
			return;
		}

		// Schedule Date verification, As Installment date crossed or not
		FinanceScheduleDetail bpiSchedule = null;

		Date appDate = SysParamUtil.getAppDate();

		if (!ImplementationConstants.ALLOW_CANCEL_LOAN_AFTER_PAYMENTS) {
			// Schedule details
			List<FinanceScheduleDetail> schdList = financeScheduleDetailDAO.getFinScheduleDetails(fm.getFinID(), "",
					false);

			for (int i = 1; i < schdList.size(); i++) {
				FinanceScheduleDetail curSchd = schdList.get(i);
				if (StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_BPI)) {
					bpiSchedule = curSchd;
					continue;
				}

				if (curSchd.getSchDate().compareTo(appDate) <= 0 && curSchd.isRepayOnSchDate()) {
					setError(detail, FinCancelUploadError.LANCLUP005);
					return;
				}
			}
		}

		// Check Repayments on Finance when it is not in Maintenance
		if (!ImplementationConstants.ALLOW_CANCEL_LOAN_AFTER_PAYMENTS) {
			if (StringUtils.isEmpty(fm.getRcdMaintainSts())) {
				List<FinanceRepayments> listFinanceRepayments = new ArrayList<FinanceRepayments>();
				listFinanceRepayments = financeDetailService.getFinRepayList(fm.getFinID());
				if (listFinanceRepayments != null && listFinanceRepayments.size() > 0) {
					boolean onlyBPIPayment = true;
					for (FinanceRepayments financeRepayments : listFinanceRepayments) {
						// check for the BPI payment
						if (bpiSchedule != null) {
							if (financeRepayments.getFinSchdDate().compareTo(bpiSchedule.getSchDate()) != 0) {
								onlyBPIPayment = false;
							}
						} else {
							onlyBPIPayment = false;
						}
					}
					if (!onlyBPIPayment) {
						setError(detail, FinCancelUploadError.LANCLUP006);
						return;
					}
				}
			}
		}

		detail.setFm(fm);
		detail.setReferenceID(fm.getFinID());
		fm.setUserDetails(header.getUserDetails());

		FinCancelUploadError errorDetail = financeCancellationService.validLoan(fm);
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

			DefaultTransactionDefinition txDef = new DefaultTransactionDefinition(
					TransactionDefinition.PROPAGATION_REQUIRES_NEW);
			TransactionStatus txStatus = null;

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

					financeCancellationUploadDAO.update(details);

					List<FileUploadHeader> headerList = new ArrayList<>();
					headerList.add(header);
					updateHeader(headerList, true);

					transactionManager.commit(txStatus);
				} catch (Exception e) {
					logger.error(ERROR_LOG, e.getCause(), e.getMessage(), e.getLocalizedMessage(), e);

					if (txStatus != null) {
						transactionManager.rollback(txStatus);
					}
				} finally {
					txStatus = null;
				}

				logger.info("Loan Cancelation Process is Initiated for the Header ID {}", header.getId());

				processCancelLoan(header.getId());

				logger.info("Loan Cancelation Process is Completed for the Header ID {}", header.getId());
			}
		}).start();
	}

	private void processCancelLoan(long headerID) {
		List<FinCancelUploadDetail> details = financeCancellationUploadDAO.getDetails(headerID);
		Date appDate = SysParamUtil.getAppDate();
		for (FinCancelUploadDetail detail : details) {
			if ("S".equals(detail.getStatus())) {
				detail.setAppDate(appDate);
				processCancelLoan(detail);
			}
		}
	}

	private void processCancelLoan(FinCancelUploadDetail detail) {
		// Date appDate = detail.getAppDate();

		Long finId = detail.getReferenceID();

		FinanceDetail findetail = financeDetailService.getFinanceDetailById(finId, false, "", false,
				FinServiceEvent.ORG, "");
		FinanceMain fm = findetail.getFinScheduleData().getFinanceMain();
		findetail.getFinScheduleData().getFinanceMain()
				.setVersion(findetail.getFinScheduleData().getFinanceMain().getVersion() + 1);

		ReasonHeader rh = processReasonHeader(detail);

		if (rh != null) {
			findetail.setReasonHeader(rh);
		}

		DefaultTransactionDefinition txDef = new DefaultTransactionDefinition(
				TransactionDefinition.PROPAGATION_REQUIRES_NEW);

		TransactionStatus transactionStatus = this.transactionManager.getTransaction(txDef);

		try {
			if (EodConstants.PROGRESS_FAILED != detail.getProgress()) {
				fm.setFinSourceID(UploadConstants.FINSOURCE_ID_LOAN_CANCEL_UPLOAD);
				fm.setCancelRemarks(detail.getRemarks());
				fm.setCancelType(detail.getCancelType());
				fm.setDetailsList(rh.getDetailsList());
				processLoanCancel(findetail, detail);
			}

			if (EodConstants.PROGRESS_FAILED == detail.getProgress()) {
				this.financeCancellationUploadDAO.update(detail);
			}
			this.transactionManager.commit(transactionStatus);

		} catch (Exception e) {
			logger.error(ERROR_LOG, e.getCause(), e.getMessage(), e.getLocalizedMessage(), e);

			transactionManager.rollback(transactionStatus);

			String error = StringUtils.trimToEmpty(e.getMessage());

			if (error.length() > 1999) {
				error = error.substring(0, 1999);
			}

			detail.setProgress(EodConstants.PROGRESS_FAILED);
			detail.setErrorDesc(error);
			this.financeCancellationUploadDAO.update(detail);
		}

		if (EodConstants.PROGRESS_FAILED == detail.getProgress()) {
			updateFailRecords(1, 1, detail.getHeaderId());
		}
	}

	private ReasonHeader processReasonHeader(FinCancelUploadDetail detail) {

		ReasonHeader rh = new ReasonHeader();

		rh.setRemarks(detail.getRemarks());
		rh.setCancelType(detail.getCancelType());
		ReasonCode reasonDtls = reasonDetailDAO.getCancelReasonByCode(detail.getReason(), "_AView");
		if (reasonDtls == null) {
			detail.setProgress(EodConstants.PROGRESS_FAILED);
			detail.setErrorDesc("Reason Code is not Valid");
			return null;
		} else {
			rh.setReasonId(reasonDtls.getReasonTypeID());
			ReasonDetails rd = new ReasonDetails();
			rd.setReasonId(reasonDtls.getReasonTypeID());
			rd.setReasonCode(detail.getReason());
			rh.getDetailsList().add(rd);
		}

		return rh;

	}

	@Override
	public void doReject(List<FileUploadHeader> headers) {
		List<Long> headerIdList = headers.stream().map(FileUploadHeader::getId).collect(Collectors.toList());

		DefaultTransactionDefinition txDef = new DefaultTransactionDefinition(
				TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		TransactionStatus txStatus = null;
		try {

			txStatus = transactionManager.getTransaction(txDef);

			financeCancellationUploadDAO.update(headerIdList, ERR_CODE, ERR_DESC, EodConstants.PROGRESS_FAILED);

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

	private void processLoanCancel(FinanceDetail financeDetail, FinCancelUploadDetail fcud) {
		String tranType = PennantConstants.TRAN_WF;

		AuditHeader ah = getAuditDetail(financeDetail, tranType);
		/*
		 * ah.setAuditTranType(PennantConstants.TRAN_WF); ah.getAuditDetail().setModelData(financeDetail);
		 * auditHeaderDAO.addAudit(ah);
		 */

		AuditHeader audH = financeCancellationService.doApprove(ah, true);
		if (audH.getErrorMessage() != null) {
			fcud.setProgress(EodConstants.PROGRESS_FAILED);
			for (ErrorDetail errorDetail : audH.getErrorMessage()) {
				fcud.setErrorCode(errorDetail.getCode());
				fcud.setErrorDesc(errorDetail.getError());
			}
		}
	}

	private AuditHeader getAuditDetail(FinanceDetail fd, String tranType) {
		AuditDetail auditDetail = new AuditDetail(PennantConstants.TRAN_WF, 1, null, fd);
		return new AuditHeader(fd.getFinReference(), null, null, null, auditDetail,
				fd.getFinScheduleData().getFinanceMain().getUserDetails(), new HashMap<String, List<ErrorDetail>>());
	}

	private void setError(FinCancelUploadDetail detail, FinCancelUploadError error) {
		detail.setProgress(EodConstants.PROGRESS_FAILED);
		detail.setErrorCode(error.name());
		detail.setErrorDesc(error.description());
	}

	@Override
	public String getSqlQuery() {
		return financeCancellationUploadDAO.getSqlQuery();
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
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

}
