package com.pennant.pff.mandate.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.TransactionStatus;

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

	private Mandate process(MandateUpload detail, Mandate mandate) {
		Mandate response = null;

		TransactionStatus txStatus = getTransactionStatus();

		try {
			response = mandateService.createMandates(mandate);
			transactionManager.commit(txStatus);
		} catch (Exception e) {
			if (txStatus != null) {
				transactionManager.rollback(txStatus);
			}
			detail.setProgress(EodConstants.PROGRESS_FAILED);
			detail.setErrorCode(ERR_CODE);
			detail.setErrorDesc(e.getMessage());
		}
		return response;
	}

	@Override
	public void doApprove(List<FileUploadHeader> headers) {
		logger.debug(Literal.ENTERING);

		new Thread(() -> {
			Date appDate = SysParamUtil.getAppDate();

			for (FileUploadHeader header : headers) {
				List<MandateUpload> details = mandateUploadDAO.loadRecordData(header.getId());

				header.setAppDate(appDate);
				header.setTotalRecords(details.size());
				int sucessRecords = 0;
				int failRecords = 0;

				try {
					for (MandateUpload detail : details) {
						doValidate(header, detail);

						if (EodConstants.PROGRESS_FAILED == detail.getProgress()) {
							failRecords++;
							continue;
						}
						Mandate mandate = detail.getMandate();
						mandate.setUserDetails(header.getUserDetails());
						mandate.setSourceId(RequestSource.UPLOAD.name());

						Mandate response = process(detail, mandate);

						if (response == null) {
							detail.setProgress(EodConstants.PROGRESS_FAILED);
							detail.setErrorCode(ERR_CODE);
							detail.setErrorDesc("");
							continue;
						}

						ErrorDetail error = response.getError();
						if (error != null) {
							failRecords++;
							detail.setProgress(EodConstants.PROGRESS_FAILED);
							detail.setErrorCode(error.getCode());
							detail.setErrorDesc(error.getError());
						} else {
							sucessRecords++;
							detail.setProgress(EodConstants.PROGRESS_SUCCESS);
							detail.setErrorCode("");
							detail.setErrorDesc("");
							detail.setReferenceID(response.getMandateID());
						}

						header.getUploadDetails().add(detail);
					}

					mandateUploadDAO.update(details);
				} catch (Exception e) {
					logger.error(Literal.EXCEPTION, e);
				}

				header.setSuccessRecords(sucessRecords);
				header.setFailureRecords(failRecords);
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
			mandateUploadDAO.update(headerIdList, ERR_CODE, ERR_DESC, EodConstants.PROGRESS_FAILED);

			headers.forEach(h1 -> {
				h1.setRemarks(ERR_DESC);
				h1.getUploadDetails().addAll(mandateUploadDAO.loadRecordData(h1.getId()));
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
	public void doValidate(FileUploadHeader header, Object detail) {
		MandateUpload mu = getDetail(detail);

		Mandate mandate = mu.getMandate();

		String extMndt = mandate.getStrExternalMandate();
		String swapMndt = mandate.getStrSwapIsActive();
		String openMndt = mandate.getStrOpenMandate();

		if ("T".equals(swapMndt) && mandate.getSwapEffectiveDate() == null) {
			setError(mu, MandateUploadError.MANUP_018);
		}

		if ("T".equals(swapMndt) && mandate.getSwapEffectiveDate().compareTo(SysParamUtil.getAppDate()) <= 0) {
			setError(mu, MandateUploadError.MANUP_019);
		}

		InstrumentType instrumentType = InstrumentType.getType(mandate.getMandateType());

		switch (instrumentType) {
		case ECS, DD, NACH, EMANDATE:
			externalMandateRequired(mu, mandate, extMndt);
			swapMandateRequired(mu, mandate, swapMndt);
			openMandateRequired(mu, mandate, openMndt);

			break;

		case SI, DAS:
			swapMandateRequired(mu, mandate, swapMndt);
			break;
		default:
			break;
		}

	}

	private void openMandateRequired(MandateUpload mu, Mandate mandate, String openMndt) {
		if ("T".equals(openMndt)) {
			mandate.setOpenMandate(true);
		} else if ("F".equals(openMndt)) {
			mandate.setOpenMandate(false);
		} else {
			setError(mu, MandateUploadError.MANUP_013);
		}
	}

	private void swapMandateRequired(MandateUpload mu, Mandate mandate, String swapMndt) {
		if ("T".equals(swapMndt)) {
			mandate.setSwapIsActive(true);
		} else if ("F".equals(swapMndt)) {
			mandate.setSwapIsActive(false);
		} else {
			setError(mu, MandateUploadError.MANUP_014);
		}
	}

	private void externalMandateRequired(MandateUpload mu, Mandate mandate, String extMndt) {
		if ("T".equals(extMndt)) {
			mandate.setExternalMandate(true);
		} else if ("F".equals(extMndt)) {
			mandate.setExternalMandate(false);
		} else {
			setError(mu, MandateUploadError.MANUP_015);
		}
	}

	private void setError(MandateUpload detail, MandateUploadError error) {
		detail.setProgress(EodConstants.PROGRESS_FAILED);
		detail.setStatus("F");
		detail.setErrorCode(error.name());
		detail.setErrorDesc(error.description());
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