package com.pennant.pff.mandate.service.impl;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

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

public class MandateUploadServiceImpl extends AUploadServiceImpl {
	private static final Logger logger = LogManager.getLogger(MandateUploadServiceImpl.class);

	private MandateUploadDAO mandateUploadDAO;
	private MandateService mandateService;

	@Override
	public void doApprove(List<FileUploadHeader> headers) {
		logger.debug(Literal.ENTERING);

		new Thread(() -> {

			DefaultTransactionDefinition txDef = new DefaultTransactionDefinition(
					TransactionDefinition.PROPAGATION_REQUIRES_NEW);
			TransactionStatus txStatus = null;

			Date appDate = SysParamUtil.getAppDate();

			for (FileUploadHeader header : headers) {
				List<MandateUpload> details = mandateUploadDAO.loadRecordData(header.getId());

				header.setAppDate(appDate);
				header.setTotalRecords(details.size());
				int sucessRecords = 0;
				int failRecords = 0;

				try {
					txStatus = transactionManager.getTransaction(txDef);

					for (MandateUpload detail : details) {
						doValidate(header, detail);

						if (EodConstants.PROGRESS_FAILED == detail.getProgress()) {
							failRecords++;
							continue;
						}
						Mandate mandate = detail.getMandate();
						mandate.setUserDetails(header.getUserDetails());
						mandate.setSourceId(RequestSource.UPLOAD.name());
						Mandate response = mandateService.createMandates(mandate);

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
					transactionManager.commit(txStatus);
				} catch (Exception e) {
					logger.error(Literal.EXCEPTION, e);

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
			}

			try {
				txStatus = transactionManager.getTransaction(txDef);

				updateHeader(headers, true);

				transactionManager.commit(txStatus);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);

				if (txStatus != null) {
					transactionManager.rollback(txStatus);
				}
			} finally {
				txStatus = null;
			}

		}).start();

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void doReject(List<FileUploadHeader> headers) {
		List<Long> headerIdList = headers.stream().map(FileUploadHeader::getId).collect(Collectors.toList());

		DefaultTransactionDefinition txDef = new DefaultTransactionDefinition(
				TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		TransactionStatus txStatus = null;
		try {
			txStatus = transactionManager.getTransaction(txDef);

			mandateUploadDAO.update(headerIdList, ERR_CODE, ERR_DESC, EodConstants.PROGRESS_FAILED);

			headers.forEach(h1 -> h1.setRemarks(ERR_DESC));
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

		MandateUpload mu = null;

		if (detail instanceof MandateUpload) {
			mu = (MandateUpload) detail;
		}

		if (mu == null) {
			throw new AppException("Invalid Data transferred...");
		}

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
		case ECS:
		case DD:
		case NACH:
		case EMANDATE:

			externalMandateRequired(mu, mandate, extMndt);

			swapMandateRequired(mu, mandate, swapMndt);

			openMandateRequired(mu, mandate, openMndt);

			break;

		case SI:
		case DAS:
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
	public void validate(DataEngineAttributes attributes, MapSqlParameterSource record) throws Exception {
		logger.debug(Literal.ENTERING);

		Long headerID = ObjectUtil.valueAsLong(attributes.getParameterMap().get("HEADER_ID"));

		if (headerID == null) {
			return;
		}

		FileUploadHeader header = (FileUploadHeader) attributes.getParameterMap().get("FILE_UPLOAD_HEADER");

		MandateUpload detail = new MandateUpload();

		detail.setReference(ObjectUtil.valueAsString(record.getValue("orgReference")));

		Mandate mndts = new Mandate();

		mndts.setCustCIF(ObjectUtil.valueAsString(record.getValue("custCIF")));
		mndts.setMandateRef(ObjectUtil.valueAsString(record.getValue("mandateRef")));
		mndts.setMandateType(ObjectUtil.valueAsString(record.getValue("mandateType")));
		mndts.setAccNumber(ObjectUtil.valueAsString(record.getValue("accNumber")));
		mndts.setAccHolderName(ObjectUtil.valueAsString(record.getValue("accHolderName")));
		mndts.setJointAccHolderName(ObjectUtil.valueAsString(record.getValue("jointAccHolderName")));
		mndts.setAccType(ObjectUtil.valueAsString(record.getValue("accType")));
		mndts.setStrOpenMandate(ObjectUtil.valueAsString(record.getValue("openMandate")));
		mndts.setStartDate(ObjectUtil.valueAsDate(record.getValue("startDate")));
		mndts.setExpiryDate(ObjectUtil.valueAsDate(record.getValue("expiryDate")));
		mndts.setMaxLimit(ObjectUtil.valueAsBigDecimal(record.getValue("maxLimit")));
		mndts.setPeriodicity(ObjectUtil.valueAsString(record.getValue("periodicity")));
		mndts.setStatus(ObjectUtil.valueAsString(record.getValue("mandateStatus")));
		mndts.setReason(ObjectUtil.valueAsString(record.getValue("reason")));
		mndts.setStrSwapIsActive(ObjectUtil.valueAsString(record.getValue("swapIsActive")));
		mndts.setEntityCode(ObjectUtil.valueAsString(record.getValue("entityCode")));
		mndts.setPartnerBankId(ObjectUtil.valueAsLong(record.getValue("partnerBankId")));
		mndts.seteMandateSource(ObjectUtil.valueAsString(record.getValue("eMandateSource")));
		mndts.seteMandateReferenceNo(ObjectUtil.valueAsString(record.getValue("eMandateReferenceNo")));
		mndts.setSwapEffectiveDate(ObjectUtil.valueAsDate(record.getValue("swapEffectiveDate")));
		mndts.setEmployerID(ObjectUtil.valueAsLong(record.getValue("employerID")));
		mndts.setEmployeeNo(ObjectUtil.valueAsString(record.getValue("employeeNo")));
		mndts.setIFSC(ObjectUtil.valueAsString(record.getValue("iFSC")));
		mndts.setMICR(ObjectUtil.valueAsString(record.getValue("mICR")));
		mndts.setStrExternalMandate(ObjectUtil.valueAsString(record.getValue("externalMandate")));
		mndts.setStrSecurityMandate(ObjectUtil.valueAsString(record.getValue("securityMandate")));
		detail.setMandate(mndts);

		doValidate(header, detail);

		updateProcess(header, detail, record);

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