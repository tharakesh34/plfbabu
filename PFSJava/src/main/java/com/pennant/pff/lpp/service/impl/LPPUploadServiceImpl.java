package com.pennant.pff.lpp.service.impl;

import java.math.BigDecimal;
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
import org.zkoss.util.resource.Labels;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinODPenaltyRate;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.lpp.upload.LPPUpload;
import com.pennant.backend.service.finance.FinanceMaintenanceService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.lpp.dao.LPPUploadDAO;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.service.impl.AUploadServiceImpl;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pff.core.RequestSource;
import com.pennanttech.pff.overdue.constants.ChargeType;

public class LPPUploadServiceImpl extends AUploadServiceImpl {
	private static final Logger logger = LogManager.getLogger(LPPUploadServiceImpl.class);

	private LPPUploadDAO lppUploadDAO;
	private FinanceMainDAO financeMainDAO;
	private FinanceMaintenanceService financeMaintenanceService;

	@Override
	public void doApprove(List<FileUploadHeader> headers) {
		new Thread(() -> {

			DefaultTransactionDefinition txDef = new DefaultTransactionDefinition(
					TransactionDefinition.PROPAGATION_REQUIRES_NEW);
			TransactionStatus txStatus = null;

			Date appDate = SysParamUtil.getAppDate();

			for (FileUploadHeader header : headers) {
				logger.info("Processing the File {}", header.getFileName());

				List<LPPUpload> details = lppUploadDAO.getDetails(header.getId());

				header.setAppDate(appDate);
				header.setTotalRecords(details.size());
				int sucessRecords = 0;
				int failRecords = 0;

				for (LPPUpload detail : details) {
					doValidate(header, detail);

					if (detail.getErrorCode() != null) {
						detail.setProgress(EodConstants.PROGRESS_FAILED);
					} else {
						detail.setProgress(EodConstants.PROGRESS_SUCCESS);
						detail.setErrorCode("");
						detail.setErrorDesc("");
						detail.setUserDetails(header.getUserDetails());
						setFinODPenaltyRateDate(detail);
					}

					if (detail.getProgress() == EodConstants.PROGRESS_FAILED) {
						failRecords++;
					} else {
						sucessRecords++;
					}
				}

				try {
					txStatus = transactionManager.getTransaction(txDef);

					lppUploadDAO.update(details);
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

				logger.info("LPP Process is Initiated");

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

	private void setFinODPenaltyRateDate(LPPUpload detail) {
		FinODPenaltyRate pr = new FinODPenaltyRate();
		FinanceDetail fd = new FinanceDetail();
		FinScheduleData schdData = new FinScheduleData();
		AuditHeader auditHeader = null;

		Long finID = financeMainDAO.getFinID(detail.getReference());

		if (PennantConstants.NO.equals(detail.getApplyOverDue())) {
			pr.setApplyODPenalty(false);
		} else {
			pr.setApplyODPenalty(true);
		}

		if (PennantConstants.NO.equals(detail.getIncludeGraceDays())) {
			pr.setODIncGrcDays(false);
		} else {
			pr.setODIncGrcDays(true);
		}

		if (PennantConstants.NO.equals(detail.getAllowWaiver())) {
			pr.setODAllowWaiver(false);
		} else {
			pr.setODAllowWaiver(true);
		}

		pr.setODChargeType(detail.getPenaltyType());
		pr.setODChargeAmtOrPerc(detail.getAmountOrPercent());
		pr.setODChargeCalOn(detail.getCalculatedOn());
		pr.setODGraceDays(detail.getGraceDays());
		pr.setODMaxWaiverPerc(detail.getMaxWaiver());
		pr.setFinReference(detail.getReference());
		pr.setFinID(finID);
		pr.setFinEffectDate(SysParamUtil.getAppDate());
		pr.setRequestSource(RequestSource.UPLOAD);

		FinanceMain fm = financeMainDAO.getFinanceMainById(finID, "", false);

		fm.setUserDetails(detail.getUserDetails());
		fm.setNewRecord(false);
		fm.setRecordType(PennantConstants.RECORD_TYPE_UPD);
		fm.setVersion(fm.getVersion() + 1);

		schdData.setFinODPenaltyRate(pr);
		schdData.setFinanceMain(fm);
		fd.setFinScheduleData(schdData);

		auditHeader = getAuditHeader(fd, PennantConstants.TRAN_WF);
		auditHeader.getAuditDetail().setModelData(fd);

		AuditHeader lppah = financeMaintenanceService.doApprove(auditHeader);

		if (lppah.getErrorMessage() != null) {
			detail.setProgress(EodConstants.PROGRESS_FAILED);
			detail.setErrorDesc(lppah.getErrorMessage().get(0).getMessage().toString());
			detail.setErrorCode(lppah.getErrorMessage().get(0).getCode().toString());
		} else {
			detail.setProgress(EodConstants.PROGRESS_SUCCESS);
			detail.setErrorCode("");
			detail.setErrorDesc("");
		}
	}

	@Override
	public void doReject(List<FileUploadHeader> headers) {
		List<Long> headerIdList = headers.stream().map(FileUploadHeader::getId).collect(Collectors.toList());

		DefaultTransactionDefinition txDef = new DefaultTransactionDefinition(
				TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		TransactionStatus txStatus = null;
		try {
			txStatus = transactionManager.getTransaction(txDef);

			lppUploadDAO.update(headerIdList, ERR_CODE, ERR_DESC, EodConstants.PROGRESS_FAILED);

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
		LPPUpload detail = null;

		if (object instanceof LPPUpload) {
			detail = (LPPUpload) object;
		}

		if (detail == null) {
			throw new AppException("Invalid Data transferred...");
		}

		String reference = detail.getReference();

		logger.info("Validating the Data for the reference {}", reference);

		detail.setHeaderId(header.getId());

		Long finID = financeMainDAO.getFinID(reference);
		detail.setReferenceID(finID);

		String loanType = detail.getLoanType();

		if (StringUtils.isBlank(reference) && StringUtils.isBlank(loanType)) {
			setError(detail, LPPUploadError.LPP01);
			return;
		}

		if (StringUtils.isNotBlank(reference) && finID == null) {
			setError(detail, LPPUploadError.LPP02);
			return;
		}

		if (StringUtils.isNotBlank(reference)) {
			FinanceMain fm = financeMainDAO.getFinanceMain(reference);

			if (StringUtils.isNotBlank(reference) && !fm.isFinIsActive()) {
				setError(detail, LPPUploadError.LPP12);
				return;
			}

			if (StringUtils.isNotBlank(reference) && fm == null) {
				setError(detail, LPPUploadError.LPP02);
				return;
			}

			if (StringUtils.isNotBlank(loanType) && !fm.getFinType().equals(loanType)) {
				setError(detail, LPPUploadError.LPP13);
				return;
			}

			String rcdMntnSts = financeMainDAO.getFinanceMainByRcdMaintenance(finID);
			if (StringUtils.isNotEmpty(rcdMntnSts)) {
				detail.setProgress(EodConstants.PROGRESS_FAILED);
				detail.setErrorCode("LPP_999");
				detail.setErrorDesc(Labels.getLabel("Finance_Inprogresss_" + rcdMntnSts));
				return;
			}
		}

		String existingLoans = detail.getApplyToExistingLoans();
		if (StringUtils.isNotBlank(loanType) && StringUtils.isBlank(reference)
				&& !(PennantConstants.NO.equals(existingLoans) || PennantConstants.YES.equals(existingLoans))) {
			setError(detail, LPPUploadError.LPP14);
			return;
		}

		if (StringUtils.isNotBlank(detail.getReference()) && StringUtils.isNotBlank(existingLoans)) {
			setError(detail, LPPUploadError.LPP17);
			return;
		}

		if (StringUtils.isBlank(detail.getApplyOverDue())) {
			setError(detail, LPPUploadError.LPP03);
			return;
		}

		boolean applyOverDue = PennantConstants.YES.equals(detail.getApplyOverDue());
		if (!(PennantConstants.NO.equals(detail.getApplyOverDue()) || applyOverDue)) {
			setError(detail, LPPUploadError.LPP04);
			return;
		}

		String penaltyType = detail.getPenaltyType();
		BigDecimal amountOrPercent = detail.getAmountOrPercent();
		String calculatedOn = detail.getCalculatedOn();

		if (PennantConstants.NO.equals(detail.getApplyOverDue())
				&& (StringUtils.isNotBlank(reference) || StringUtils.isNotBlank(loanType))) {
			if (StringUtils.isNotBlank(calculatedOn) || (StringUtils.isNotBlank(detail.getIncludeGraceDays()))
					|| (StringUtils.isNotBlank(penaltyType)) || StringUtils.isNotBlank(detail.getAllowWaiver())
					|| (detail.getMaxWaiver().compareTo(BigDecimal.ZERO)) > 0 || detail.getGraceDays() > 0
					|| amountOrPercent.compareTo(BigDecimal.ZERO) > 0) {
				setError(detail, LPPUploadError.LPP09);
				return;
			}
		}

		if (PennantConstants.YES.equals(detail.getApplyOverDue())) {
			boolean allowWaiver = PennantConstants.YES.equals(detail.getAllowWaiver());
			boolean includeGraceDays = PennantConstants.YES.equals(detail.getIncludeGraceDays());

			if (!(PennantConstants.NO.equals(detail.getAllowWaiver()) || allowWaiver)) {
				setError(detail, LPPUploadError.LPP04);
				return;
			}

			if (!(PennantConstants.NO.equals(detail.getIncludeGraceDays()) || includeGraceDays)) {
				setError(detail, LPPUploadError.LPP19);
				return;
			}

			if (!includeGraceDays && detail.getGraceDays() != 0) {
				setError(detail, LPPUploadError.LPP20);
				return;
			}

			if (includeGraceDays && (detail.getGraceDays() < 0 || detail.getGraceDays() > 999)) {
				setError(detail, LPPUploadError.LPP15);
				return;
			}

			if (!includeGraceDays && detail.getGraceDays() > 0) {
				setError(detail, LPPUploadError.LPP16);
				return;
			}

			if (!allowWaiver && detail.getMaxWaiver().compareTo(BigDecimal.ZERO) != 0) {
				setError(detail, LPPUploadError.LPP21);
				return;
			}

			if (allowWaiver && (detail.getMaxWaiver().compareTo(BigDecimal.ZERO) < 0
					|| detail.getMaxWaiver().compareTo(new BigDecimal(100)) > 1)) {
				setError(detail, LPPUploadError.LPP10);
				return;
			}

			if (allowWaiver && StringUtils.isBlank(String.valueOf(detail.getMaxWaiver()))) {
				setError(detail, LPPUploadError.LPP18);
				return;
			}

			if (StringUtils.isNotBlank(String.valueOf(detail.getMaxWaiver()))) {
				if (!allowWaiver && detail.getMaxWaiver().compareTo(BigDecimal.ZERO) > 0) {
					setError(detail, LPPUploadError.LPP11);
					return;
				}
			}

			if (StringUtils.isBlank(penaltyType)) {
				setError(detail, LPPUploadError.LPP05);
				return;
			}

			switch (penaltyType) {
			case ChargeType.FLAT:
			case ChargeType.FLAT_ON_PD_MTH:
				amountOrPercent = amountOrPercent.divide(new BigDecimal(100));
				if ((amountOrPercent.compareTo(BigDecimal.ZERO)) < 0
						|| (amountOrPercent.compareTo(new BigDecimal(9999999)) > 0)) {
					setError(detail, LPPUploadError.LPP07);
					return;
				}

				if (StringUtils.isNotBlank(calculatedOn)) {
					setError(detail, LPPUploadError.LPP22);
					return;
				}

				break;
			case ChargeType.PERC_ONE_TIME:
			case ChargeType.PERC_ON_PD_MTH:
			case ChargeType.PERC_ON_DUE_DAYS:
			case ChargeType.PERC_ON_EFF_DUE_DAYS:
				amountOrPercent = amountOrPercent.divide(new BigDecimal(100));
				if ((amountOrPercent.compareTo(BigDecimal.ZERO)) <= 0
						|| (amountOrPercent.compareTo(new BigDecimal(100)) > 0)) {
					setError(detail, LPPUploadError.LPP08);
					return;
				}

				if (StringUtils.isBlank(calculatedOn)) {
					setError(detail, LPPUploadError.LPP23);
					return;
				}

				if (!(FinanceConstants.ODCALON_STOT.equals(calculatedOn)
						|| FinanceConstants.ODCALON_SPRI.equals(calculatedOn)
						|| FinanceConstants.ODCALON_SPFT.equals(calculatedOn))) {
					setError(detail, LPPUploadError.LPP06);
					return;
				}

				break;
			default:
				break;
			}
		}
	}

	private void setError(LPPUpload detail, LPPUploadError error) {
		detail.setProgress(EodConstants.PROGRESS_FAILED);
		detail.setErrorCode(error.name());
		detail.setErrorDesc(error.description());
	}

	private AuditHeader getAuditHeader(FinanceDetail afinanceDetail, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, afinanceDetail.getBefImage(), afinanceDetail);
		return new AuditHeader(afinanceDetail.getFinScheduleData().getFinReference(), null, null, null, auditDetail,
				afinanceDetail.getUserDetails(), new HashMap<>());
	}

	@Override
	public String getSqlQuery() {
		return lppUploadDAO.getSqlQuery();
	}

	@Autowired
	public void setLPPUploadDAO(LPPUploadDAO lppUploadDAO) {
		this.lppUploadDAO = lppUploadDAO;
	}

	@Autowired
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	@Autowired
	public void setFinanceMaintenanceService(FinanceMaintenanceService financeMaintenanceService) {
		this.financeMaintenanceService = financeMaintenanceService;
	}

}