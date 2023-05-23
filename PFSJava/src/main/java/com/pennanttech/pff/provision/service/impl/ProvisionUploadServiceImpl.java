package com.pennanttech.pff.provision.service.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
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
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.util.PennantConstants;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.service.impl.AUploadServiceImpl;
import com.pennanttech.dataengine.model.DataEngineAttributes;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.file.UploadTypes;
import com.pennanttech.pff.provision.ProvisionUploadError;
import com.pennanttech.pff.provision.dao.ProvisionUploadDAO;
import com.pennanttech.pff.provision.model.Provision;
import com.pennanttech.pff.provision.model.ProvisionUpload;
import com.pennanttech.pff.provision.service.ProvisionService;
import com.pennapps.core.util.ObjectUtil;

public class ProvisionUploadServiceImpl extends AUploadServiceImpl<ProvisionUpload> {
	private static final Logger logger = LogManager.getLogger(ProvisionUploadServiceImpl.class);

	private ProvisionService provisionService;
	private ProvisionUploadDAO provisionUploadDAO;
	private FinanceMainDAO financeMainDAO;

	@Override
	protected ProvisionUpload getDetail(Object object) {

		if (object instanceof ProvisionUpload detail) {
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

				List<ProvisionUpload> details = provisionUploadDAO.getDetails(header.getId());
				header.getUploadDetails().addAll(details);
				header.setAppDate(appDate);

				for (ProvisionUpload detail : details) {
					doValidate(header, detail);

					if (detail.getErrorCode() != null) {
						setFailureStatus(detail);
					} else {
						setSuccesStatus(detail);
						process(header, detail);
					}
				}

				try {
					provisionUploadDAO.update(details);

					List<FileUploadHeader> headerList = new ArrayList<>();
					headerList.add(header);
					updateHeader(headers, true);

				} catch (Exception e) {
					logger.error(Literal.EXCEPTION, e);
				}
			}

		}).start();
	}

	private void process(FileUploadHeader header, ProvisionUpload detail) {
		Provision mp = new Provision();

		if (detail.getAssetClassId() != null) {
			mp.setManualAssetClassID(detail.getAssetClassId());
		}

		if (detail.getAssetSubClassId() != null) {
			mp.setManualAssetSubClassID(detail.getAssetSubClassId());
		}

		mp.setManualAssetClassCode(detail.getAssetClassCode());
		mp.setManualAssetSubClassCode(detail.getAssetSubClassCode());

		mp.setOverrideProvision(PennantConstants.YES.equals(detail.getOverrideProvision()));
		mp.setManProvsnPer(detail.getProvisionPercentage());
		mp.setFinID(detail.getReferenceID());
		mp.setManualProvision(true);

		TransactionStatus txStatus = getTransactionStatus();
		AuditHeader auditHeader;
		try {

			Provision p = provisionService.getProvision(detail.getReferenceID(), header.getAppDate(), mp);

			LoggedInUser userDetails = detail.getUserDetails();

			if (userDetails == null) {
				userDetails = new LoggedInUser();
				userDetails.setLoginUsrID(header.getApprovedBy());
				userDetails.setUserName(header.getApprovedByName());
			}

			Timestamp timeStamp = new Timestamp(System.currentTimeMillis());

			p.setLastMntBy(header.getApprovedBy());
			p.setLastMntOn(timeStamp);

			p.setApprovedBy(header.getApprovedBy());
			p.setApprovedOn(timeStamp);
			p.setCreatedBy(header.getApprovedBy());
			p.setApprovedOn(timeStamp);

			p.setUserDetails(userDetails);
			p.setRecordType(PennantConstants.RECORD_TYPE_UPD);
			p.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			p.setVersion(p.getVersion() + 1);

			auditHeader = getAuditHeader(p, PennantConstants.TRAN_WF);
			auditHeader.getAuditDetail().setModelData(p);

			provisionService.doApprove(auditHeader);
			transactionManager.commit(txStatus);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			if (txStatus != null) {
				transactionManager.rollback(txStatus);
			}

			setFailureStatus(detail, e.getMessage());
			return;
		}

		if (auditHeader != null && auditHeader.getErrorMessage() != null) {
			setFailureStatus(detail, auditHeader.getErrorMessage().get(0));
		} else {
			setSuccesStatus(detail);
		}
	}

	private AuditHeader getAuditHeader(Provision provision, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, provision.getBefImage(), provision);
		return new AuditHeader(null, null, null, null, auditDetail, provision.getUserDetails(), new HashMap<>());
	}

	@Override
	public void doReject(List<FileUploadHeader> headers) {
		List<Long> headerIdList = headers.stream().map(FileUploadHeader::getId).toList();

		TransactionStatus txStatus = getTransactionStatus();

		try {
			headers.forEach(h1 -> {
				h1.setRemarks(REJECT_DESC);
				h1.getUploadDetails().addAll(provisionUploadDAO.getDetails(h1.getId()));
			});

			provisionUploadDAO.update(headerIdList, REJECT_CODE, REJECT_DESC);

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
		return provisionUploadDAO.getSqlQuery();
	}

	@Override
	public void uploadProcess() {
		uploadProcess(UploadTypes.PROVISION.name(), this, "ProvisionUploadHeader");
	}

	@Override
	public void validate(DataEngineAttributes attributes, MapSqlParameterSource paramSource) throws Exception {
		logger.debug(Literal.ENTERING);

		ProvisionUpload provision = (ProvisionUpload) ObjectUtil.valueAsObject(paramSource, ProvisionUpload.class);

		provision.setReference(ObjectUtil.valueAsString(paramSource.getValue("finReference")));

		Map<String, Object> parameterMap = attributes.getParameterMap();

		FileUploadHeader header = (FileUploadHeader) parameterMap.get("FILE_UPLOAD_HEADER");

		provision.setHeaderId(header.getId());
		provision.setAppDate(header.getAppDate());

		doValidate(header, provision);

		updateProcess(header, provision, paramSource);

		header.getUploadDetails().add(provision);

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void doValidate(FileUploadHeader header, Object object) {
		ProvisionUpload detail = getDetail(object);

		String reference = detail.getReference();
		String overrideprovision = detail.getOverrideProvision();
		BigDecimal provisionPercentage = detail.getProvisionPercentage();

		logger.info("Validating the Data for the reference {}", reference);

		detail.setHeaderId(header.getId());

		if (StringUtils.isNotBlank(reference)) {
			validateLoan(detail, reference);
		}

		if (EodConstants.PROGRESS_FAILED == detail.getProgress()) {
			return;
		}

		doValidateManualProvision(detail, overrideprovision, provisionPercentage);

		if (EodConstants.PROGRESS_FAILED == detail.getProgress()) {
			return;
		}

		doValidateAssetClassifications(detail);

		if (EodConstants.PROGRESS_FAILED == detail.getProgress()) {
			return;
		}

		setSuccesStatus(detail);
	}

	private void doValidateAssetClassifications(ProvisionUpload detail) {
		String assetClassCode = detail.getAssetClassCode();
		String subClassCode = detail.getAssetSubClassCode();

		if (StringUtils.isNotBlank(assetClassCode)) {
			Long assetClassId = provisionUploadDAO.getAssetClassId(assetClassCode);

			if (assetClassId == null) {
				setError(detail, ProvisionUploadError.PROVSN_04);
				return;
			}

			detail.setAssetClassId(assetClassId);
		}

		if (StringUtils.isNotBlank(subClassCode) && StringUtils.isNotBlank(assetClassCode)) {

			Long assetSubClassId = provisionUploadDAO.getAssetSubClassId(detail.getAssetClassId(), subClassCode);

			if (assetSubClassId == null) {
				setError(detail, ProvisionUploadError.PROVSN_05);
				return;
			}

			detail.setAssetSubClassId(assetSubClassId);

		}
	}

	private void doValidateManualProvision(ProvisionUpload detail, String overrideprovision,
			BigDecimal manualprovision) {
		if (StringUtils.isBlank(overrideprovision)
				&& !(PennantConstants.NO.equals(overrideprovision) || PennantConstants.YES.equals(overrideprovision))) {
			setError(detail, ProvisionUploadError.PROVSN_02);
			return;
		}

		if (PennantConstants.YES.equals(overrideprovision) && manualprovision.compareTo(BigDecimal.ZERO) <= 0
				|| (manualprovision.compareTo(new BigDecimal(100)) > 0)) {
			setError(detail, ProvisionUploadError.PROVSN_03);
			return;
		}

		if (PennantConstants.NO.equals(overrideprovision) && manualprovision.compareTo(BigDecimal.ZERO) > 0) {
			setError(detail, ProvisionUploadError.PROVSN_06);
			return;
		}
	}

	private void validateLoan(ProvisionUpload detail, String reference) {
		FinanceMain fm = financeMainDAO.getFinanceMain(reference, TableType.MAIN_TAB);

		if (fm == null || !fm.isFinIsActive()) {
			setError(detail, ProvisionUploadError.PROVSN_01);
			return;
		}

		if (!provisionService.isRecordExists(fm.getFinID())) {
			setError(detail, ProvisionUploadError.PROVSN_07);
			return;
		}

		detail.setReferenceID(fm.getFinID());
	}

	private void setError(ProvisionUpload detail, ProvisionUploadError error) {
		setFailureStatus(detail, error.name(), error.description());
	}

	@Autowired
	public void setProvisionService(ProvisionService provisionService) {
		this.provisionService = provisionService;
	}

	@Autowired
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	@Autowired
	public void setProvisionUploadDAO(ProvisionUploadDAO provisionUploadDAO) {
		this.provisionUploadDAO = provisionUploadDAO;
	}

}