package com.pennant.pff.lpp.service.impl;

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
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinODPenaltyRate;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.lpp.upload.LPPUpload;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.finance.FinanceMaintenanceService;
import com.pennant.backend.service.rmtmasters.FinanceTypeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.lpp.dao.LPPUploadDAO;
import com.pennant.pff.lpp.upload.validate.LPPUploadProcessRecord;
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
	private FinanceTypeDAO financeTypeDAO;
	private FinanceTypeService financeTypeService;
	private LPPUploadProcessRecord lPPUploadProcessRecord;

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
				header.getUploadDetails().addAll(details);
				header.setAppDate(appDate);

				for (LPPUpload detail : details) {
					doValidate(header, detail);

					if (detail.getErrorCode() != null) {
						setFailureStatus(detail);
					} else {
						setSuccesStatus(detail);
						detail.setUserDetails(header.getUserDetails());
						process(detail, header);
					}
				}
				updateFailures(details);

				try {
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
		if (StringUtils.isBlank(detail.getReference())) {
			processLoanType(detail, header);
		} else {
			processByFinRef(detail, header);
		}

	}

	private void processLoanType(LPPUpload detail, FileUploadHeader header) {
		FinanceType ft = financeTypeDAO.getFinanceTypeByID(detail.getLoanType(), "");
		AuditHeader auditHeader = null;

		if (PennantConstants.NO.equals(detail.getApplyOverDue())) {
			ft.setODAllowWaiver(false);
			ft.setODIncGrcDays(false);
			ft.setApplyODPenalty(false);
		} else {
			ft.setApplyODPenalty(true);
		}

		if (PennantConstants.YES.equals(detail.getApplyOverDue())) {
			ft.setODIncGrcDays(PennantConstants.YES.equals(detail.getIncludeGraceDays()));
			ft.setODAllowWaiver(PennantConstants.YES.equals(detail.getAllowWaiver()));
		}

		ft.setODChargeType(detail.getPenaltyType());
		ft.setODChargeAmtOrPerc(detail.getAmountOrPercent());
		ft.setODChargeCalOn(detail.getCalculatedOn());
		ft.setODGraceDays(detail.getGraceDays());
		ft.setODMaxWaiverPerc(detail.getMaxWaiver());
		ft.setOdMinAmount(detail.getODMinAmount());
		ft.setRequestSource(RequestSource.UPLOAD);
		LoggedInUser userDetails = detail.getUserDetails();

		if (userDetails == null) {
			userDetails = new LoggedInUser();
			userDetails.setLoginUsrID(header.getApprovedBy());
			userDetails.setUserName(header.getApprovedByName());
		}

		ft.setUserDetails(userDetails);
		ft.setNewRecord(false);
		ft.setRecordType(PennantConstants.RECORD_TYPE_UPD);
		ft.setVersion(ft.getVersion() + 1);

		auditHeader = getAuditHeader(ft, PennantConstants.TRAN_WF);
		auditHeader.getAuditDetail().setModelData(ft);

		TransactionStatus txStatus = getTransactionStatus();

		try {
			auditHeader = financeTypeService.doApprove(auditHeader);
			transactionManager.commit(txStatus);
		} catch (Exception e) {
			if (txStatus != null) {
				transactionManager.rollback(txStatus);
			}

			setFailureStatus(detail, e.getMessage());

			return;
		}

		if (auditHeader == null) {
			setFailureStatus(detail, "Audit Header is null.");
			return;
		}

		if (auditHeader.getErrorMessage() != null) {
			setFailureStatus(detail, auditHeader.getErrorMessage().get(0));
		} else {
			setSuccesStatus(detail);
		}

	}

	private void processByFinRef(LPPUpload detail, FileUploadHeader header) {
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
			setError(detail, LPPUploadError.LPP_02);
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

			setFailureStatus(detail, e.getMessage());

			return;
		}

		if (auditHeader == null) {
			setFailureStatus(detail, "Audit Header is null.");
			return;
		}

		if (auditHeader.getErrorMessage() != null) {
			setFailureStatus(detail, auditHeader.getErrorMessage().get(0));
		} else {
			setSuccesStatus(detail);
		}
	}

	@Override
	public void doReject(List<FileUploadHeader> headers) {
		List<Long> headerIdList = headers.stream().map(FileUploadHeader::getId).toList();

		TransactionStatus txStatus = getTransactionStatus();
		try {

			headers.forEach(h1 -> {
				h1.setRemarks(REJECT_DESC);
				h1.getUploadDetails().addAll(lppUploadDAO.getDetails(h1.getId()));
			});

			lppUploadDAO.update(headerIdList, REJECT_CODE, REJECT_DESC);

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
		lPPUploadProcessRecord.validate(header, detail);
		if (detail.getProgress() == EodConstants.PROGRESS_FAILED) {
			setFailureStatus(detail);
		}
	}

	@Override
	public void uploadProcess() {
		uploadProcess(UploadTypes.LPP_LOAN.name(), this, "LPPLoanUploadHeader");
	}

	@Override
	public void uploadProcess(String type) {
		if (type.equals(UploadTypes.LPP_LOAN_TYPE.name())) {
			uploadProcess(UploadTypes.LPP_LOAN_TYPE.name(), lPPUploadProcessRecord, this, "LPPLoanTypeUploadHeader");
		} else {
			uploadProcess(UploadTypes.LPP_LOAN.name(), this, "LPPLoanUploadHeader");
		}
	}

	@Override
	public String getSqlQuery() {
		return lppUploadDAO.getSqlQuery();
	}

	@Override
	public LPPUploadProcessRecord getProcessRecord() {
		return lPPUploadProcessRecord;
	}

	@Override
	public void validate(DataEngineAttributes attributes, MapSqlParameterSource paramSource) throws Exception {
		logger.debug(Literal.ENTERING);

		LPPUpload detail = (LPPUpload) ObjectUtil.valueAsObject(paramSource, LPPUpload.class);

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

	private void updateFailures(List<LPPUpload> details) {
		List<LPPUpload> loanTypeDetail = new ArrayList<>();

		for (LPPUpload detail : details) {
			if (StringUtils.isEmpty(detail.getReference())) {
				loanTypeDetail.add(detail);
			}
		}

		for (LPPUpload lppUpload : loanTypeDetail) {
			String errorDesc = "";
			
			for (LPPUpload detail : details) {
				if (detail.getReference() == null) {
					continue;
				}

				if (detail.getErrorDesc() != null && detail.getLoanType().equals(lppUpload.getLoanType())) {
					if (errorDesc.length() > 0) {
						errorDesc = errorDesc.concat(",");
					}

					errorDesc = errorDesc.concat(detail.getReference().concat("-").concat(detail.getErrorDesc()));
				}
			}

			if (errorDesc.length() > 1999) {
				errorDesc = errorDesc.substring(0, 1999);
			}

			if (StringUtils.isNotEmpty(errorDesc)) {
				lppUpload.setErrorCode("LTR_001");
				lppUpload.setErrorDesc(errorDesc);
			}

			lppUpload.setStatus("S");
			lppUpload.setProgress(EodConstants.PROGRESS_SUCCESS);
		}
	}

	private void setError(LPPUpload detail, LPPUploadError error) {
		setFailureStatus(detail, error.name(), error.description());
	}

	private AuditHeader getAuditHeader(FinanceDetail afinanceDetail, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, afinanceDetail.getBefImage(), afinanceDetail);
		return new AuditHeader(afinanceDetail.getFinScheduleData().getFinReference(), null, null, null, auditDetail,
				afinanceDetail.getUserDetails(), new HashMap<>());
	}

	private AuditHeader getAuditHeader(FinanceType financetype, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, financetype.getBefImage(), financetype);
		return new AuditHeader(null, null, null, null, auditDetail, financetype.getUserDetails(), new HashMap<>());
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
	public void setFinanceTypeDAO(FinanceTypeDAO financeTypeDAO) {
		this.financeTypeDAO = financeTypeDAO;
	}

	@Autowired
	public void setFinanceTypeService(FinanceTypeService financeTypeService) {
		this.financeTypeService = financeTypeService;
	}

	@Autowired
	public void setFinanceMaintenanceService(FinanceMaintenanceService financeMaintenanceService) {
		this.financeMaintenanceService = financeMaintenanceService;
	}

	@Autowired
	public void setlPPUploadProcessRecord(LPPUploadProcessRecord lPPUploadProcessRecord) {
		this.lPPUploadProcessRecord = lPPUploadProcessRecord;
	}
}