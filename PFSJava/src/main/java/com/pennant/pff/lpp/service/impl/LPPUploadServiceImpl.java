package com.pennant.pff.lpp.service.impl;

import java.math.BigDecimal;
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
import com.pennant.pff.lpp.PenaltyTypes;
import com.pennant.pff.lpp.dao.LPPUploadDAO;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.service.impl.AUploadServiceImpl;
import com.pennanttech.dataengine.model.DataEngineAttributes;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.RequestSource;
import com.pennanttech.pff.file.UploadTypes;
import com.pennapps.core.util.ObjectUtil;

public class LPPUploadServiceImpl extends AUploadServiceImpl<LPPUpload> {
	private static final Logger logger = LogManager.getLogger(LPPUploadServiceImpl.class);

	private LPPUploadDAO lppUploadDAO;
	private FinanceMainDAO financeMainDAO;
	private FinanceMaintenanceService financeMaintenanceService;

	public LPPUploadServiceImpl() {
		super();
	}

	@Override
	protected LPPUpload getDetail(Object object) {
		if (object instanceof LPPUpload detail) {
			return detail;
		}

		throw new AppException(IN_VALID_OBJECT);
	}

	@Override
	public void doApprove(List<FileUploadHeader> headers) {
		new Thread(() -> {
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
						process(detail, header);
					}

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

					lppUploadDAO.update(details);

					List<FileUploadHeader> headerList = new ArrayList<>();
					headerList.add(header);
					updateHeader(headers, true);
				} catch (Exception e) {
					logger.error(Literal.EXCEPTION, e);
				}
			}

		}).start();
	}

	private void process(LPPUpload detail, FileUploadHeader header) {
		FinODPenaltyRate pr = new FinODPenaltyRate();
		FinanceDetail fd = new FinanceDetail();
		FinScheduleData schdData = new FinScheduleData();
		AuditHeader auditHeader = null;

		Long finID = financeMainDAO.getFinID(detail.getReference());

		if (PennantConstants.NO.equals(detail.getApplyOverDue())) {
			pr.setODAllowWaiver(false);
			pr.setODIncGrcDays(false);
			pr.setApplyODPenalty(false);
		} else {
			pr.setApplyODPenalty(true);
		}

		if (PennantConstants.YES.equals(detail.getApplyOverDue())) {
			pr.setODIncGrcDays(PennantConstants.YES.equals(detail.getIncludeGraceDays()));
			pr.setODAllowWaiver(PennantConstants.YES.equals(detail.getAllowWaiver()));
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
		pr.setOdMinAmount(detail.getODMinAmount());

		FinanceMain fm = financeMainDAO.getFinanceMainById(finID, "", false);

		if (fm == null) {
			setError(detail, LPPUploadError.LPP02);
			return;
		}

		LoggedInUser userDetails = detail.getUserDetails();

		if (userDetails == null) {
			userDetails = new LoggedInUser();
			userDetails.setLoginUsrID(header.getApprovedBy());
			userDetails.setUserName(header.getApprovedByName());
		}

		fm.setUserDetails(userDetails);
		fm.setNewRecord(false);
		fm.setRecordType(PennantConstants.RECORD_TYPE_UPD);
		fm.setVersion(fm.getVersion() + 1);

		schdData.setFinODPenaltyRate(pr);
		schdData.setFinanceMain(fm);
		fd.setFinScheduleData(schdData);

		auditHeader = getAuditHeader(fd, PennantConstants.TRAN_WF);
		auditHeader.getAuditDetail().setModelData(fd);

		TransactionStatus txStatus = getTransactionStatus();
		try {
			auditHeader = financeMaintenanceService.doApprove(auditHeader);
			transactionManager.commit(txStatus);
		} catch (Exception e) {
			if (txStatus != null) {
				transactionManager.rollback(txStatus);
			}

			detail.setProgress(EodConstants.PROGRESS_FAILED);
			detail.setErrorDesc(ERR_CODE);
			detail.setErrorCode(e.getMessage());

			return;
		}

		if (auditHeader == null) {
			detail.setProgress(EodConstants.PROGRESS_SUCCESS);
			detail.setErrorCode(ERR_CODE);
			detail.setErrorDesc("Audit Header is null.");
			return;
		}

		if (auditHeader.getErrorMessage() != null) {
			detail.setProgress(EodConstants.PROGRESS_FAILED);
			detail.setErrorDesc(auditHeader.getErrorMessage().get(0).getMessage());
			detail.setErrorCode(auditHeader.getErrorMessage().get(0).getCode());
		} else {
			detail.setProgress(EodConstants.PROGRESS_SUCCESS);
			detail.setErrorCode("");
			detail.setErrorDesc("");
		}
	}

	@Override
	public void doReject(List<FileUploadHeader> headers) {
		List<Long> headerIdList = headers.stream().map(FileUploadHeader::getId).toList();

		TransactionStatus txStatus = getTransactionStatus();
		try {
			lppUploadDAO.update(headerIdList, ERR_CODE, ERR_DESC, EodConstants.PROGRESS_FAILED);

			headers.forEach(h1 -> {
				h1.setRemarks(ERR_DESC);
				h1.getUploadDetails().addAll(lppUploadDAO.getDetails(h1.getId()));
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
	public void doValidate(FileUploadHeader header, Object object) {
		LPPUpload detail = getDetail(object);

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
			FinanceMain fm = financeMainDAO.getFinanceMain(reference, header.getEntityCode());

			if (StringUtils.isNotBlank(reference) && fm == null) {
				setError(detail, LPPUploadError.LPP02);
				return;
			}

			if (StringUtils.isNotBlank(reference) && !fm.isFinIsActive()) {
				setError(detail, LPPUploadError.LPP12);
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

		BigDecimal maxWaiver = detail.getMaxWaiver();
		BigDecimal minAmount = detail.getODMinAmount();

		if (maxWaiver == null) {
			maxWaiver = BigDecimal.ZERO;
		}

		if (amountOrPercent == null) {
			amountOrPercent = BigDecimal.ZERO;
		}

		if (PennantConstants.NO.equals(detail.getApplyOverDue())
				&& (StringUtils.isNotBlank(reference) || StringUtils.isNotBlank(loanType))) {
			if (StringUtils.isNotBlank(calculatedOn) || (StringUtils.isNotBlank(detail.getIncludeGraceDays()))
					|| (StringUtils.isNotBlank(penaltyType)) || StringUtils.isNotBlank(detail.getAllowWaiver())
					|| (maxWaiver.compareTo(BigDecimal.ZERO)) > 0 || detail.getGraceDays() > 0
					|| amountOrPercent.compareTo(BigDecimal.ZERO) > 0 || minAmount.compareTo(BigDecimal.ZERO) > 0) {
				setError(detail, LPPUploadError.LPP09);
				return;
			}
		}

		if (PennantConstants.YES.equals(detail.getApplyOverDue())) {
			boolean allowWaiver = PennantConstants.YES.equals(detail.getAllowWaiver());
			boolean includeGraceDays = PennantConstants.YES.equals(detail.getIncludeGraceDays());

			if (!(PennantConstants.NO.equals(detail.getAllowWaiver()) || allowWaiver)) {
				setError(detail, LPPUploadError.LPP20);
				return;
			}

			if (!(PennantConstants.NO.equals(detail.getIncludeGraceDays()) || includeGraceDays)) {
				setError(detail, LPPUploadError.LPP19);
				return;
			}

			if (includeGraceDays && (detail.getGraceDays() < 0 || detail.getGraceDays() > 999)) {
				setError(detail, LPPUploadError.LPP15);
				return;
			}

			if (allowWaiver && StringUtils.isBlank(String.valueOf(maxWaiver))) {
				setError(detail, LPPUploadError.LPP18);
				return;
			} else if (allowWaiver
					&& (maxWaiver.compareTo(BigDecimal.ZERO) <= 0 || maxWaiver.compareTo(new BigDecimal(100)) > 0)) {
				setError(detail, LPPUploadError.LPP10);
				return;
			}

			if (StringUtils.isNotBlank(String.valueOf(maxWaiver)) && !allowWaiver
					&& maxWaiver.compareTo(BigDecimal.ZERO) > 0) {
				setError(detail, LPPUploadError.LPP11);
				return;
			}

			PenaltyTypes lppType = PenaltyTypes.getTypes(penaltyType);

			if (lppType == null) {
				setError(detail, LPPUploadError.LPP05);
				return;
			}

			switch (lppType) {
			case FLAT, FLAT_ON_PD_MTH:
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

				if (StringUtils.isNotBlank(detail.getIncludeGraceDays())) {
					setError(detail, LPPUploadError.LPP27);
					return;
				}

				break;
			case PERC_ONE_TIME, PERC_ON_PD_MTH, PERC_ON_DUE_DAYS, PERC_ON_EFF_DUE_DAYS:
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
						|| FinanceConstants.ODCALON_SPFT.equals(calculatedOn)
						|| FinanceConstants.ODCALON_INST.equals(calculatedOn))) {
					setError(detail, LPPUploadError.LPP06);
					return;
				}

				break;
			default:
				break;
			}

			boolean isLppType = PenaltyTypes.PERC_ONE_TIME.equals(lppType)
					|| PenaltyTypes.PERC_ON_PD_MTH.equals(lppType);

			if (!isLppType && FinanceConstants.ODCALON_INST.equals(calculatedOn)) {
				setError(detail, LPPUploadError.LPP25);
				return;
			}

			if (isLppType && StringUtils.isBlank(String.valueOf(detail.getODMinAmount()))) {
				setError(detail, LPPUploadError.LPP26);
				return;
			}

			if (isLppType && StringUtils.isNotBlank(detail.getIncludeGraceDays())) {
				setError(detail, LPPUploadError.LPP27);
				return;
			}
		}
		setSuccesStatus(detail);
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
	public void uploadProcess() {
		uploadProcess(UploadTypes.LPP.name(), this, "LPPUploadHeader");
	}

	@Override
	public String getSqlQuery() {
		return lppUploadDAO.getSqlQuery();
	}

	@Override
	public void validate(DataEngineAttributes attributes, MapSqlParameterSource paramSource) throws Exception {
		logger.debug(Literal.ENTERING);

		LPPUpload lppUpload = (LPPUpload) ObjectUtil.valueAsObject(paramSource, LPPUpload.class);

		lppUpload.setReference(ObjectUtil.valueAsString(paramSource.getValue("finReference")));

		Map<String, Object> parameterMap = attributes.getParameterMap();

		FileUploadHeader header = (FileUploadHeader) parameterMap.get("FILE_UPLOAD_HEADER");

		lppUpload.setHeaderId(header.getId());
		lppUpload.setAppDate(header.getAppDate());

		doValidate(header, lppUpload);

		updateProcess(header, lppUpload, paramSource);

		logger.debug(Literal.LEAVING);
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