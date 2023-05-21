package com.pennant.pff.mandate.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.TransactionStatus;

import com.cronutils.utils.StringUtils;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.mandate.MandateUploadDAO;
import com.pennant.backend.model.mandate.Mandate;
import com.pennant.backend.model.mandate.MandateUpload;
import com.pennant.backend.service.mandate.MandateService;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.excess.MandateUploadError;
import com.pennant.pff.mandate.InstrumentType;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.service.impl.AUploadServiceImpl;
import com.pennanttech.dataengine.model.DataEngineAttributes;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.RequestSource;
import com.pennanttech.pff.file.UploadTypes;
import com.pennapps.core.util.ObjectUtil;

public class MandateUploadServiceImpl extends AUploadServiceImpl<MandateUpload> {
	private static final Logger logger = LogManager.getLogger(MandateUploadServiceImpl.class);

	private MandateUploadDAO mandateUploadDAO;
	private MandateService mandateService;

	public MandateUploadServiceImpl() {
		super();
	}

	@Override
	protected MandateUpload getDetail(Object object) {
		if (object instanceof MandateUpload detail) {
			return detail;
		}

		throw new AppException(IN_VALID_OBJECT);
	}

	private void process(MandateUpload detail, Mandate mandate) {
		TransactionStatus txStatus = getTransactionStatus();

		try {
			Mandate response = mandateService.createMandates(mandate);

			if (response == null) {
				setFailureStatus(detail, "Mandate is null");
				return;
			}

			ErrorDetail error = response.getError();
			if (error != null) {
				setFailureStatus(detail, error);
				return;
			}

			setSuccesStatus(detail);
			detail.setReferenceID(response.getMandateID());

			transactionManager.commit(txStatus);
		} catch (Exception e) {
			if (txStatus != null) {
				transactionManager.rollback(txStatus);
			}
			setFailureStatus(detail, e.getMessage());
		}
	}

	@Override
	public void doApprove(List<FileUploadHeader> headers) {
		logger.debug(Literal.ENTERING);

		new Thread(() -> {
			Date appDate = SysParamUtil.getAppDate();

			for (FileUploadHeader header : headers) {
				List<MandateUpload> details = mandateUploadDAO.loadRecordData(header.getId());
				header.getUploadDetails().addAll(details);
				header.setAppDate(appDate);

				try {
					for (MandateUpload detail : details) {
						doValidate(header, detail);
						prepareUserDetails(header, detail);

						if (EodConstants.PROGRESS_FAILED == detail.getProgress()) {
							continue;
						}

						Mandate mandate = detail.getMandate();
						mandate.setUserDetails(detail.getUserDetails());
						mandate.setSourceId(RequestSource.UPLOAD.name());

						process(detail, mandate);
					}

					mandateUploadDAO.update(details);
				} catch (Exception e) {
					logger.error(Literal.EXCEPTION, e);
				}
			}

			try {
				updateHeader(headers, true);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			}

		}).start();

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void doReject(List<FileUploadHeader> headers) {
		List<Long> headerIdList = headers.stream().map(FileUploadHeader::getId).toList();

		TransactionStatus txStatus = getTransactionStatus();
		try {

			headers.forEach(h1 -> {
				h1.setRemarks(REJECT_DESC);
				h1.getUploadDetails().addAll(mandateUploadDAO.loadRecordData(h1.getId()));
			});

			mandateUploadDAO.update(headerIdList, REJECT_CODE, REJECT_DESC);

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
	public void doValidate(FileUploadHeader header, Object detail) {
		MandateUpload mu = getDetail(detail);

		Mandate mandate = mu.getMandate();

		String extMndt = mandate.getStrExternalMandate();
		String swapMndt = mandate.getStrSwapIsActive();
		String openMndt = mandate.getStrOpenMandate();
		String secMndt = mandate.getStrSecurityMandate();
		String mandateType = mandate.getMandateType();

		if ("T".equals(secMndt) && (InstrumentType.isDAS(mandateType) || InstrumentType.isSI(mandateType))) {
			setError(mu, MandateUploadError.MANUP_016);
		}

		if ("T".equals(swapMndt) && mandate.getSwapEffectiveDate() == null) {
			setError(mu, MandateUploadError.MANUP_018);
		}

		if ("T".equals(swapMndt) && mandate.getSwapEffectiveDate().compareTo(SysParamUtil.getAppDate()) <= 0) {
			setError(mu, MandateUploadError.MANUP_019);
		}

		InstrumentType instrumentType = InstrumentType.getType(mandateType);

		switch (instrumentType) {
		case ECS, DD, NACH, EMANDATE:
			externalMandateRequired(mu, mandate, extMndt);
			swapMandateRequired(mu, mandate, swapMndt);
			openMandateRequired(mu, mandate, openMndt);
			securityMandateRequired(mu, mandate, secMndt);

			break;

		case SI, DAS:
			swapMandateRequired(mu, mandate, swapMndt);
			break;
		default:
			break;
		}

		if (mu.getProgress() != EodConstants.PROGRESS_FAILED) {
			setSuccesStatus(mu);
		}
	}

	private void openMandateRequired(MandateUpload mu, Mandate mandate, String openMndt) {
		if (StringUtils.isEmpty(openMndt)) {
			return;
		}

		if ("T".equals(openMndt)) {
			mandate.setOpenMandate(true);
		} else if ("F".equals(openMndt)) {
			mandate.setOpenMandate(false);
		} else {
			setError(mu, MandateUploadError.MANUP_013);
		}
	}

	private void swapMandateRequired(MandateUpload mu, Mandate mandate, String swapMndt) {
		if (StringUtils.isEmpty(swapMndt)) {
			return;
		}

		if ("T".equals(swapMndt)) {
			mandate.setSwapIsActive(true);
		} else if ("F".equals(swapMndt)) {
			mandate.setSwapIsActive(false);
		} else {
			setError(mu, MandateUploadError.MANUP_014);
		}
	}

	private void externalMandateRequired(MandateUpload mu, Mandate mandate, String extMndt) {
		if (StringUtils.isEmpty(extMndt)) {
			return;
		}

		if ("T".equals(extMndt)) {
			mandate.setExternalMandate(true);
		} else if ("F".equals(extMndt)) {
			mandate.setExternalMandate(false);
		} else {
			setError(mu, MandateUploadError.MANUP_015);
		}
	}

	private void securityMandateRequired(MandateUpload mu, Mandate mandate, String secMndt) {
		if (StringUtils.isEmpty(secMndt)) {
			return;
		}

		if ("T".equals(secMndt)) {
			mandate.setSecurityMandate(true);
		} else if ("F".equals(secMndt)) {
			mandate.setSecurityMandate(false);
		} else {
			setError(mu, MandateUploadError.MANUP_017);
		}

	}

	private void setError(MandateUpload detail, MandateUploadError error) {
		setFailureStatus(detail, error.name(), error.description());
	}

	@Override
	public void uploadProcess() {
		uploadProcess(UploadTypes.MANDATES.name(), this, "MandateUploadHeader");
	}

	@Override
	public String getSqlQuery() {
		return mandateUploadDAO.getSqlQuery();
	}

	@Override
	public void validate(DataEngineAttributes attributes, MapSqlParameterSource paramSource) throws Exception {
		logger.debug(Literal.ENTERING);

		Long headerID = ObjectUtil.valueAsLong(attributes.getParameterMap().get("HEADER_ID"));

		if (headerID == null) {
			return;
		}

		FileUploadHeader header = (FileUploadHeader) attributes.getParameterMap().get("FILE_UPLOAD_HEADER");

		MandateUpload detail = new MandateUpload();

		detail.setReference(ObjectUtil.valueAsString(paramSource.getValue("orgReference")));

		Mandate mndts = new Mandate();

		mndts.setCustCIF(ObjectUtil.valueAsString(paramSource.getValue("custCIF")));
		mndts.setMandateRef(ObjectUtil.valueAsString(paramSource.getValue("mandateRef")));
		mndts.setMandateType(ObjectUtil.valueAsString(paramSource.getValue("mandateType")));
		mndts.setAccNumber(ObjectUtil.valueAsString(paramSource.getValue("accNumber")));
		mndts.setAccHolderName(ObjectUtil.valueAsString(paramSource.getValue("accHolderName")));
		mndts.setJointAccHolderName(ObjectUtil.valueAsString(paramSource.getValue("jointAccHolderName")));
		mndts.setAccType(ObjectUtil.valueAsString(paramSource.getValue("accType")));
		mndts.setStrOpenMandate(ObjectUtil.valueAsString(paramSource.getValue("openMandate")));
		mndts.setStartDate(ObjectUtil.valueAsDate(paramSource.getValue("startDate")));
		mndts.setExpiryDate(ObjectUtil.valueAsDate(paramSource.getValue("expiryDate")));
		mndts.setMaxLimit(ObjectUtil.valueAsBigDecimal(paramSource.getValue("maxLimit")));
		mndts.setPeriodicity(ObjectUtil.valueAsString(paramSource.getValue("periodicity")));
		mndts.setStatus(ObjectUtil.valueAsString(paramSource.getValue("mandateStatus")));
		mndts.setReason(ObjectUtil.valueAsString(paramSource.getValue("reason")));
		mndts.setStrSwapIsActive(ObjectUtil.valueAsString(paramSource.getValue("swapIsActive")));
		mndts.setEntityCode(ObjectUtil.valueAsString(paramSource.getValue("entityCode")));
		mndts.setPartnerBankId(ObjectUtil.valueAsLong(paramSource.getValue("partnerBankId")));
		mndts.seteMandateSource(ObjectUtil.valueAsString(paramSource.getValue("eMandateSource")));
		mndts.seteMandateReferenceNo(ObjectUtil.valueAsString(paramSource.getValue("eMandateReferenceNo")));
		mndts.setSwapEffectiveDate(ObjectUtil.valueAsDate(paramSource.getValue("swapEffectiveDate")));
		mndts.setEmployerID(ObjectUtil.valueAsLong(paramSource.getValue("employerID")));
		mndts.setEmployeeNo(ObjectUtil.valueAsString(paramSource.getValue("employeeNo")));
		mndts.setIFSC(ObjectUtil.valueAsString(paramSource.getValue("iFSC")));
		mndts.setMICR(ObjectUtil.valueAsString(paramSource.getValue("mICR")));
		mndts.setStrExternalMandate(ObjectUtil.valueAsString(paramSource.getValue("externalMandate")));
		mndts.setStrSecurityMandate(ObjectUtil.valueAsString(paramSource.getValue("securityMandate")));
		detail.setMandate(mndts);

		doValidate(header, detail);

		updateProcess(header, detail, paramSource);

		header.getUploadDetails().add(detail);

		logger.debug(Literal.LEAVING);
	}

	@Autowired
	public void setMandateUploadDAO(MandateUploadDAO mandateUploadDAO) {
		this.mandateUploadDAO = mandateUploadDAO;
	}

	@Autowired
	public void setMandateService(MandateService mandateService) {
		this.mandateService = mandateService;
	}

}